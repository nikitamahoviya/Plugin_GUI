//VCellPlugin_Reduced

/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package org.vcell.imagej.plugin;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.table.DefaultTableModel;

import org.scijava.command.ContextCommand;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.display.event.DisplayUpdatedEvent;
import org.scijava.event.EventService;
import org.scijava.module.Module;
import org.scijava.module.ModuleItem;
import org.scijava.module.process.AbstractPreprocessorPlugin;
import org.scijava.module.process.PreprocessorPlugin;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.plugin.PluginInfo;
import org.scijava.run.RunService;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.UIService;
import org.scijava.widget.UIComponent;
import org.vcell.imagej.helper.VCellHelper;
import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
import org.vcell.imagej.helper.VCellHelper.IJDataList;
import org.vcell.imagej.helper.VCellHelper.IJVarInfo;
import org.vcell.imagej.helper.VCellHelper.IJVarInfos;
import org.vcell.imagej.helper.VCellHelper.ModelType;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;

import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.NewImage;
import net.imagej.ImageJ;
import net.imagej.ImgPlus;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imagej.axis.CalibratedAxis;
import net.imagej.axis.DefaultAxisType;
import net.imagej.axis.DefaultLinearAxis;
import net.imagej.display.DefaultDatasetView;
import net.imagej.display.DefaultImageDisplay;
import net.imagej.display.ImageDisplay;
import net.imagej.display.ImageDisplayService;
import net.imagej.display.WindowService;
import net.imagej.display.ZoomService;
import net.imagej.display.event.PanZoomEvent;
import net.imagej.ops.Op;
import net.imagej.ops.OpService;
import net.imagej.ops.Ops.Map;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Pair;

@Plugin(type = ContextCommand.class, menuPath = "Plugins>VCellPlugin_Reduced")
public class VCellPlugin_Reduced extends ContextCommand {

		private static Frame mainApplicationFrame;
	
	public static class StyledComboBoxUI extends BasicComboBoxUI {
		  protected ComboPopup createPopup() {
		    @SuppressWarnings("serial")
			BasicComboPopup popup = new BasicComboPopup(comboBox) {
		      @Override
		      protected Rectangle computePopupBounds(int px,int py,int pw,int ph) {
		        return super.computePopupBounds(
		            px,py,Math.max(comboBox.getPreferredSize().width,pw),ph
		        );
		      }
		    };
		    popup.getAccessibleContext().setAccessibleParent(comboBox);
		    return popup;
		  }
		}

	@SuppressWarnings("serial")
	public static class StyledComboBox<E> extends JComboBox<String> {
		  public StyledComboBox() {
		    setUI(new StyledComboBoxUI());
		  }
		  public StyledComboBox(String[] items) {
			  setUI(new StyledComboBoxUI());
			  setModel(new DefaultComboBoxModel<String>(items));
		  }
		}
	
	public static class VCellSelection {
		public String theCacheKey;
		public VCellHelper.ModelType modelType;
		public String userid;
		public String modelName;
		public String appName;
		public String simname;
		public String[] varName;
		public int[] timePointIndexes;
		public Exception exception;
		public VCellSelection(String theCacheKey, VCellHelper.ModelType modelType,String userid,String modelName, String appName, String simname,String[] varName,int[] timePointIndexes) {
			super();
			this.theCacheKey = theCacheKey;
			this.modelType = modelType;
			this.userid=userid;
			this.modelName = modelName;
			this.appName = appName;
			this.simname = simname;
			this.varName = varName;
			this.timePointIndexes = timePointIndexes;
		}
		public VCellSelection(Exception exception) {
			this.exception = exception;
		}
	}
	

	
	@Plugin(type = PreprocessorPlugin.class)
	public static class MyPreProcessor extends AbstractPreprocessorPlugin {
		
		public static final String CANCELLED = "cancelled";

		@Parameter
		private UIService uiService;

		@Parameter(required = true)
		private VCellHelper vcellHelper;

		private JComboBox<String> jcbModelType = new StyledComboBox<String>(new String[] {ModelType.bm.name(),ModelType.mm.name(),ModelType.quick.name()});
		private JComboBox<String> jcbUserid = new StyledComboBox<String>();
		private JComboBox<String> jcbModelNames = new StyledComboBox<String>();
		private JComboBox<String> jcbAppNames = new StyledComboBox<String>();
		private JComboBox<String> jcbSimNames = new StyledComboBox<String>();

		
		private Comparator<String> comp = new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.toLowerCase().compareTo(o2.toLowerCase());
			}
		};
		private TreeSet<String> useridSet = new TreeSet<String>(comp);
		private Hashtable<String,TreeSet<String>> mapUseridToModelNameTime  = new Hashtable<String, TreeSet<String>>();
		private Hashtable<String,String> mapModelNameTimeToActualModelname  = new Hashtable<String, String>();
		private Hashtable<String,TreeSet<String>> mapModelToApps = new Hashtable<String, TreeSet<String>>();
		private Hashtable<String,TreeSet<String>> mapAppsToSims = new Hashtable<String, TreeSet<String>>();

		public MyPreProcessor() {
		}
		
			private String createMapAppToSimsKeyName(String modelName,String appName) {
			return modelName+" "+(appName==null?modelName:appName);
		}
		private void searchVCell() throws Exception{
			final UIService service = getContext().getService(UIService.class);
			Object obj = service.getDefaultUI().getApplicationFrame();
			if(obj instanceof UIComponent && ((UIComponent)obj).getComponent() instanceof Frame) {
				mainApplicationFrame = (Frame)((UIComponent)obj).getComponent();
			}else if(obj instanceof Frame) {
				mainApplicationFrame = (Frame)obj;
			}
			VCellHelper.VCellModelSearch vcms = new VCellHelper.VCellModelSearch(ModelType.valueOf(jcbModelType.getSelectedItem().toString()),null,null,null,null,null,null);
			try {
				final DateFormat dateTimeInstance = DateFormat.getDateTimeInstance();
				ArrayList<VCellModelSearchResults> vcmsr = vcellHelper.getSearchedModelSimCacheKey(false,vcms,null);
				useridSet = new TreeSet<String>(comp);
				mapUseridToModelNameTime  = new Hashtable<String, TreeSet<String>>();
				mapModelNameTimeToActualModelname  = new Hashtable<String, String>();
				mapModelToApps = new Hashtable<String, TreeSet<String>>();
				mapAppsToSims = new Hashtable<String, TreeSet<String>>();

				final Iterator<VCellModelSearchResults> iterator = vcmsr.iterator();
				while(iterator.hasNext()) {
					final VCellModelSearchResults next = iterator.next();
					String userid = next.getUserId();
					useridSet.add(userid);
					final String modelName = next.getModelName();
					final String modelNameTime = modelName+" ("+next.getModelType().name()+")"+" - "+(next.getDate()==null?"unsaved":dateTimeInstance.format(new Date(next.getDate())));
					TreeSet<String> modelsForUserid = mapUseridToModelNameTime.get(userid);
					if(modelsForUserid == null) {
						modelsForUserid = new TreeSet<String>();
						mapUseridToModelNameTime.put(userid, modelsForUserid);
					}
					modelsForUserid.add(modelNameTime);
					mapModelNameTimeToActualModelname.put(modelNameTime, modelName);
					TreeSet<String> appsForModel = mapModelToApps.get(modelNameTime);
					if(appsForModel == null) {
						appsForModel = new TreeSet<String>();
						mapModelToApps.put(modelNameTime, appsForModel);
					}
					appsForModel.add((next.getModelType()==ModelType.mm?modelNameTime:next.getApplicationName()));
					String modelNameTimeApp = createMapAppToSimsKeyName(modelNameTime,(next.getModelType()==ModelType.mm?null:next.getApplicationName()));//modelNameTime+(next.getModelType()==ModelType.mm?"":" "+next.getApplicationName());
					TreeSet<String> simsForApp = mapAppsToSims.get(modelNameTimeApp);
					if(simsForApp == null) {
						simsForApp = new TreeSet<String>();
						mapAppsToSims.put(modelNameTimeApp, simsForApp);
					}
					simsForApp.add(next.getSimulationName());
				}
		}finally {
			}

		}
		@Override
		public void process(Module module) {
			final ModuleItem<VCellSelection> vcellModelsInput = getvcellModelsInput(module);
			if (vcellModelsInput == null) {
				return;
			}
			final Dimension dim = new Dimension(300,120);
			@SuppressWarnings("serial")
			final JPanel jp = new JPanel() {
				@Override
				public Dimension getPreferredSize() {
					return dim;
				}
			};
			jp.setLayout(new GridLayout(8,2));
			
			final boolean[] bUseVCellSelectionHolder = new boolean[] {false};

			//jcbModelType
			jp.add(new JLabel("Model Type"));
			jp.add(jcbModelType);
			
			
			jp.add(new JLabel("VCell Userid"));
			jcbModelType.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								searchVCell();
							} catch (Exception e1) {
								return;
							}
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									if(vcellModelsInput.getDefaultValue() != null && vcellModelsInput.getDefaultValue().modelType.name().equals(jcbModelType.getSelectedItem().toString())) {
										bUseVCellSelectionHolder[0] = true;
									}
									jcbUserid.removeAllItems();
									jcbUserid.setModel(new DefaultComboBoxModel<String>(useridSet.toArray(new String[0])));
									if(jcbUserid.getItemCount()==0) {
										jcbUserid.addItem("Nothing Found");
									}else if(bUseVCellSelectionHolder[0]) {
										jcbUserid.setSelectedItem(vcellModelsInput.getDefaultValue().userid);
									}else {
										jcbUserid.setSelectedIndex(0);
									}
									bUseVCellSelectionHolder[0] = false;
								}});
						}}).start();
				}});
			jp.add(jcbUserid);

			jp.add(new JLabel("Model Name"));
			jcbUserid.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					jcbModelNames.removeAllItems();
					if(jcbUserid.getSelectedItem() != null && mapUseridToModelNameTime.get(jcbUserid.getSelectedItem()) != null) {
						jcbModelNames.setModel(new DefaultComboBoxModel<String>(mapUseridToModelNameTime.get(jcbUserid.getSelectedItem()).toArray(new String[0])));
						if(bUseVCellSelectionHolder[0]) {
							for(int i=0;i<jcbModelNames.getModel().getSize();i++) {
								if(jcbModelNames.getModel().getElementAt(i).toString().startsWith(vcellModelsInput.getDefaultValue().modelName)) {
									jcbModelNames.setSelectedIndex(i);
									break;
								}
							}
						}else {
							jcbModelNames.setSelectedIndex(0);
						}
					}
				}});
			jp.add(jcbModelNames);

			
			jp.add(new JLabel("App Name"));
			jcbModelNames.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					jcbAppNames.removeAllItems();
					if(jcbModelNames.getSelectedItem() != null && mapModelToApps.get(jcbModelNames.getSelectedItem()) != null) {
						jcbAppNames.setModel(new DefaultComboBoxModel<String>(mapModelToApps.get(jcbModelNames.getSelectedItem()).toArray(new String[0])));
						if(bUseVCellSelectionHolder[0]) {
							jcbAppNames.setSelectedItem(vcellModelsInput.getDefaultValue().appName);
						}else {
							jcbAppNames.setSelectedIndex(0);
						}
					}
				}});
			jp.add(jcbAppNames);

			jp.add(new JLabel("Sim Name"));
			jcbAppNames.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					jcbSimNames.removeAllItems();
					if(jcbAppNames.getSelectedItem() != null && mapAppsToSims.get(jcbModelNames.getSelectedItem()+" "+jcbAppNames.getSelectedItem()) != null) {
						jcbSimNames.setModel(new DefaultComboBoxModel<String>(mapAppsToSims.get(jcbModelNames.getSelectedItem()+" "+jcbAppNames.getSelectedItem()).toArray(new String[0])));
						if(bUseVCellSelectionHolder[0]) {
							jcbSimNames.setSelectedItem(vcellModelsInput.getDefaultValue().simname);
						}else {
							jcbSimNames.setSelectedIndex(0);
						}
					}
				}});
			jp.add(jcbSimNames);
			
			final String[] cacheKeyHolder = new String[1];
			final IJVarInfos[] ijVarInfosHolder = new IJVarInfos[1];

			
			JTable jtVars = new JTable();
			JSlider minTimeJSlider1 = new javax.swing.JSlider();
			JSlider maxTimeJSlider1 = new javax.swing.JSlider();
			
			jp.add(new JLabel("Vars and Times"));

			JButton selectMultipleVarsAndTimesBtn = new JButton("Select...");
			selectMultipleVarsAndTimesBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							ijVarInfosHolder[0] = null;
							populateVarAndTimes(cacheKeyHolder, ijVarInfosHolder);
							while(ijVarInfosHolder[0] == null) {
								try {
									Thread.sleep(100);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							if(ijVarInfosHolder[0].getSimname() == null) {//nothing found or error
								return;
							}
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									JPanel selectJPanel = new JPanel();

									BoxLayout boxLayout = new BoxLayout(selectJPanel,BoxLayout.Y_AXIS);
									selectJPanel.setLayout(boxLayout);

									Object[][] dataVars = new Object[ijVarInfosHolder[0].getIjVarInfo().size()][3];
									for(int i=0;i<ijVarInfosHolder[0].getIjVarInfo().size();i++) {
										dataVars[i][0] = ijVarInfosHolder[0].getIjVarInfo().get(i).getName();
										dataVars[i][1] = ijVarInfosHolder[0].getIjVarInfo().get(i).getDomain();
										dataVars[i][2] = ijVarInfosHolder[0].getIjVarInfo().get(i).getVariableType();
									}

									jtVars.setModel(new DefaultTableModel(dataVars,new String[] {"Variable","Domain","Type"}));

									JScrollPane jspVars = new JScrollPane(jtVars);

									final String MINTIMESTR = "Min Time ";
									final String MAXTIMESTR = "Max Time ";
									final JLabel minTimeJLabel = new JLabel(MINTIMESTR+ijVarInfosHolder[0].getTimes()[0]);
									final JLabel maxTimeJLabel = new JLabel(MAXTIMESTR+ijVarInfosHolder[0].getTimes()[ijVarInfosHolder[0].getTimes().length-1]);

									ChangeListener changeListener = new ChangeListener() {
										@Override
										public void stateChanged(ChangeEvent ce) {
											if(ce.getSource()==minTimeJSlider1 && minTimeJSlider1.getValue() > maxTimeJSlider1.getValue()){
												maxTimeJSlider1.setValue(minTimeJSlider1.getValue());
												return;
											}else if(ce.getSource()==maxTimeJSlider1 && maxTimeJSlider1.getValue() < minTimeJSlider1.getValue()){
												minTimeJSlider1.setValue(maxTimeJSlider1.getValue());
												return;
											}
											minTimeJLabel.setText(MINTIMESTR+ijVarInfosHolder[0].getTimes()[minTimeJSlider1.getValue()]);
											maxTimeJLabel.setText(MAXTIMESTR+ijVarInfosHolder[0].getTimes()[maxTimeJSlider1.getValue()]);
										}};
									
									minTimeJSlider1.setName("minTime");
									minTimeJSlider1.setPaintTicks(true);
									minTimeJSlider1.setMajorTickSpacing(10);
									minTimeJSlider1.setSnapToTicks(true);
									minTimeJSlider1.setMinorTickSpacing(1);
									minTimeJSlider1.setMaximum(ijVarInfosHolder[0].getTimes().length - 1);
									minTimeJSlider1.setValue(0);
									minTimeJSlider1.addChangeListener(changeListener);
									
									maxTimeJSlider1.setName("maxTime");
									maxTimeJSlider1.setPaintTicks(true);
									maxTimeJSlider1.setMajorTickSpacing(10);
									maxTimeJSlider1.setSnapToTicks(true);
									maxTimeJSlider1.setMinorTickSpacing(1);
									maxTimeJSlider1.setMaximum(ijVarInfosHolder[0].getTimes().length - 1);
									maxTimeJSlider1.setValue(ijVarInfosHolder[0].getTimes().length - 1);
									maxTimeJSlider1.addChangeListener(changeListener);

									selectJPanel.add(jspVars);
									selectJPanel.add(minTimeJLabel);
									selectJPanel.add(minTimeJSlider1);
									selectJPanel.add(maxTimeJLabel);
									selectJPanel.add(maxTimeJSlider1);
									//jp.setSize(325, 450);
									int response = JOptionPane.showConfirmDialog(jp, selectJPanel,"Select Vars and Times",JOptionPane.OK_CANCEL_OPTION);
									if(response != JOptionPane.OK_OPTION) {
										return;
									}
									ArrayList<Container> compList = new ArrayList<Container>();
									compList.add(SwingUtilities.getAncestorOfClass(Window.class, selectMultipleVarsAndTimesBtn));
									while(compList.size() > 0) {
										Container container = compList.remove(0);
										for(int i=0;i<container.getComponentCount();i++) {
											Component comp = container.getComponent(i);
											if(comp instanceof JButton && ((JButton)comp).getText().equals("OK")) {
												((JButton)comp).doClick();
												return;
											}else if(comp instanceof Container) {
												compList.add((Container)comp);
											}
										}
									}
								}
							});

						}}).start();
				
				}});
			jp.add(selectMultipleVarsAndTimesBtn);
			

			jcbModelType.setSelectedIndex(0);

			int response = JOptionPane.showConfirmDialog(VCellPlugin_Reduced.mainApplicationFrame, jp,"Select User Model App Sim",JOptionPane.OK_CANCEL_OPTION);
			if(response != JOptionPane.OK_OPTION) {
				vcellModelsInput.setValue(module, new VCellSelection(new Exception(CANCELLED)));//return VCellSelection with 'cancel' exception
				module.resolveInput(vcellModelsInput.getName());
				return;
			}
			
			ModelType modelType = ModelType.valueOf((String)jcbModelType.getSelectedItem());
			String userid = jcbUserid.getSelectedItem().toString();
			String modelName = (jcbModelNames.getSelectedItem()==null?null:mapModelNameTimeToActualModelname.get(jcbModelNames.getSelectedItem()).toString());
			String appName = (jcbModelNames.getSelectedItem()==null?null:jcbAppNames.getSelectedItem().toString());
			String simName = (jcbModelNames.getSelectedItem()==null?null:jcbSimNames.getSelectedItem().toString());

			
			String[] selectedVarNames = new String[jtVars.getSelectedRows().length];
			for(int i=0;i<jtVars.getSelectedRows().length;i++) {
				selectedVarNames[i] = ijVarInfosHolder[0].getIjVarInfo().get(jtVars.getSelectedRows()[i]).getName();
			}
			int[] selectedTimeIndexes = new int[maxTimeJSlider1.getValue()-minTimeJSlider1.getValue()+1];
			for(int i=minTimeJSlider1.getValue();i<=maxTimeJSlider1.getValue();i++) {
				selectedTimeIndexes[i-minTimeJSlider1.getValue()] = i;
			}
			
		    VCellSelection result = new VCellSelection(cacheKeyHolder[0], modelType, userid,modelName, appName, simName,selectedVarNames,selectedTimeIndexes);
			vcellModelsInput.setValue(module, result);
			module.resolveInput(vcellModelsInput.getName());
		}
		
		private void populateVarAndTimes(final String[] cacheKeyHolder,IJVarInfos[] ijVarInfosHolder
											){

			String userid = jcbUserid.getSelectedItem().toString();
			String modelName = (jcbModelNames.getSelectedItem()==null?null:mapModelNameTimeToActualModelname.get(jcbModelNames.getSelectedItem()).toString());
			String appName = (jcbModelNames.getSelectedItem()==null?null:jcbAppNames.getSelectedItem().toString());
			String simName = (jcbModelNames.getSelectedItem()==null?null:jcbSimNames.getSelectedItem().toString());
			VCellHelper.VCellModelSearch vcms = new VCellHelper.VCellModelSearch(ModelType.valueOf(jcbModelType.getSelectedItem().toString()),userid,modelName,appName,simName,null,null);
		      try {
			  		ArrayList<VCellModelSearchResults> vcmsr = vcellHelper.getSearchedModelSimCacheKey(false,vcms,null);
		  		if(vcmsr.size() == 0) {
		  			throw new Exception("No Results for search found");
		  		}
		  		cacheKeyHolder[0] = vcmsr.get(0).getCacheKey();
		  		System.out.println("theCacheKey="+cacheKeyHolder[0]);
				ijVarInfosHolder[0] = vcellHelper.getVarInfos(cacheKeyHolder[0]);

				ijVarInfosHolder[0].getIjVarInfo().sort(new Comparator<IJVarInfo>() {
					@Override
					public int compare(IJVarInfo o1, IJVarInfo o2) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}});	  		
		  	} catch (Exception e2) {
				if(e2.getMessage().contains(".log not exist")) {
					uiService.showDialog("VCellHelper.ModelType.bm,\""+userid+"\",\""+modelName+"\",\""+appName+"\",\""+simName+"\",null,null\n"+"Data Not Found.", "populateVarAndTimes failed", MessageType.ERROR_MESSAGE);
				}else {
					uiService.showDialog("VCellHelper.ModelType.bm,\""+userid+"\",\""+modelName+"\",\""+appName+"\",\""+simName+"\",null,null\n"+e2.getMessage(), "populateVarAndTimes failed", MessageType.ERROR_MESSAGE);
				}
		  	}finally {}
		}
		private ModuleItem<VCellSelection> getvcellModelsInput(final Module module) {
			ModuleItem<VCellSelection> result = null;
			for (final ModuleItem<?> input : module.getInfo().inputs()) {
				if (module.isInputResolved(input.getName())) continue;
				final Class<?> type = input.getType();
				if (!VCellSelection.class.isAssignableFrom(type)) {

					return null;
				}
				if (result != null) {

					return null;
				}
				@SuppressWarnings("unchecked")
				final ModuleItem<VCellSelection> vcellSelect = (ModuleItem<VCellSelection>) input;
				result = vcellSelect;
			}
			return result;
		}
	}	
	
	@Parameter
	private UIService uiService;

	@Parameter
	private DisplayService displayService;

	@Parameter
	EventService eventService;
	
	@Parameter
	ZoomService zoomService;
	
  	@Parameter
	private VCellHelper vcellHelper;
  	
  	@Parameter
  	private VCellSelection vcellSelection = new VCellSelection("-1",ModelType.bm, "tutorial","Tutorial_MultiApp", "3D pde", "Simulation4",new String[] {"C_cyt"},new int[] {50});
 
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
    }

	@Override
	public void run() {
		try {			
			if(vcellSelection != null && vcellSelection.exception != null) {
				if(!vcellSelection.exception.getMessage().equals(MyPreProcessor.CANCELLED)) {
					uiService.showDialog("Model search failed\n"+vcellSelection.exception.getClass().getName()+"\n"+vcellSelection.exception.getMessage(), MessageType.ERROR_MESSAGE);
				}
				return;
			}
			if(vcellSelection == null || vcellSelection.theCacheKey==null) {
				return;
			}

			for(int varIndex=0;varIndex<vcellSelection.varName.length;varIndex++) {
				int[] time = vcellSelection.timePointIndexes;
						final IJDataList tpd = vcellHelper.getTimePointData(vcellSelection.theCacheKey,vcellSelection.varName[varIndex],VCellHelper.VARTYPE_POSTPROC.NotPostProcess,time,0);
				BasicStackDimensions bsd = tpd.ijData[0].stackInfo;
				double[] data = new double[bsd.getTotalSize()*tpd.ijData.length];
				double min = Double.MAX_VALUE;
				double max = Double.MIN_VALUE;
				ArrayImg<DoubleType, DoubleArray> testimg = ArrayImgs.doubles( data, bsd.xsize,bsd.ysize,bsd.zsize,tpd.ijData.length);
				for(int i=0;i<tpd.ijData.length;i++) {
					System.arraycopy(tpd.ijData[i].getDoubleData(), 0, data, i*bsd.getTotalSize(), bsd.getTotalSize());

					for(int j=0;j<tpd.ijData[i].getDoubleData().length;j++){
						if(tpd.ijData[i].getDoubleData()[j] != tpd.ijData[i].notInDomainValue) {
							min = Math.min(min, tpd.ijData[i].getDoubleData()[j]);
							max = Math.max(max, tpd.ijData[i].getDoubleData()[j]);
						}
					}
				}
				ImgPlus<DoubleType> imgPlus = new ImgPlus<DoubleType>(testimg);
				imgPlus.setChannelMinimum(0, min);
				imgPlus.setChannelMaximum(0, max);
				imgPlus.setAxis(new DefaultLinearAxis(Axes.Z), 2);
				imgPlus.setAxis(new DefaultLinearAxis(Axes.TIME), 3);
				
				uiService.show(vcellSelection.varName[varIndex],imgPlus
						);
				while(displayService.getActiveDisplay() == null) {
					Thread.sleep(100);
				}
				WindowManager.getActiveWindow().setSize(400, 400);
				IJ.run("Scale to Fit", "");
				WindowManager.getActiveWindow().setSize(400, 400);

			}
		} catch (Exception e) {
		}
	}
}
