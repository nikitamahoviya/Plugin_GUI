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
import java.awt.FlowLayout;
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

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
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


/**
 * This example illustrates how to create an ImageJ {@link ContextCommand} plugin that uses VCellHelper.
 * <p>
 * You should replace the parameter fields with your own inputs and outputs,
 * and replace the {@link run} method implementation with your own logic.
 * </p>
 * <p>
 * To add VCellHelper to this project,
 * rt-click on topmost tree element
 * "imagej-plugin2"->Properties->Libraries tab->Add External Jars...->
 * File Dialog->{EclipseVCellWorkspaceRootDir}/vcell/vcell-imagej-helper/target/vcell-imagej-helper-0.0.1-SNAPSHOT.jar.
 * </p>
 * <p>
 * Once vcell-imagej-helper-0.0.1-SNAPSHOT.jar has been added to the Libraries tab open
 * the small arrow to the left and select "Source Attachment"->Add/Edit->External Location->
 * External File Dialog->{EclipseVCellWorkspaceRootDir}/vcell/vcell-imagej-helper/target/vcell-imagej-helper-0.0.1-SNAPSHOT-sources.jar.
 * </p>
 * <p>
 * When editing the original VCellHelper in another running Eclipse,
 * to make the changes show up in this project choose thisEclipse->Project->Clean...->clean.
 * </p>
  */
@Plugin(type = ContextCommand.class, menuPath = "Plugins>PluginTrialNext")
public class PluginTrialNext extends ContextCommand {
	
//	final UIService service = getContext().getService(UIService.class);
//	System.out.println(service.getDefaultUI().getApplicationFrame().getClass().getName()+" "+(service.getDefaultUI().getApplicationFrame() instanceof Frame));

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
	
	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}
	private Object getIconLabel() {
		Object iconLabel = null;
		if (iconLabel == null) {
			try {
				JLabel Label = new JLabel();
				//iconLabel = new JLabel();
				((Component) iconLabel).setName("IconLabel");
//				iconLabel.set
				((JLabel) iconLabel).setIcon(new ImageIcon(getClass().getResource("/images/ccam_sm_colorgr.gif")));
				((JLabel) iconLabel).setText("");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return iconLabel;
	}
	public static class VCellSelection {
		public String theCacheKey;
		public VCellHelper.ModelType modelType;
		public String userid;
		public String modelName;
		public String appName;
		public String simname;
		public String[] varName;
		public String varNames;
		public int[] timePointIndexes;
		public int timePointIndex;
		public Exception exception;
		public VCellSelection(String theCacheKey, VCellHelper.ModelType modelType,String userid,String modelName, String appName, String simname,String[] varName,int[] timePointIndexes, String varNames) {
			super();
			this.theCacheKey = theCacheKey;
			this.modelType = modelType;
			this.userid=userid;
			this.modelName = modelName;
			this.appName = appName;
			this.simname = simname;
			this.varName = varName;
			this.varNames = varNames;
			this.timePointIndexes = timePointIndexes;
			this.timePointIndex = timePointIndex;
		}
		public VCellSelection(Exception exception) {
			this.exception = exception;
		}
		public VCellSelection(String string, String string2, String string3, String string4, String string5,
				String string6, int i) {
			// TODO Auto-generated constructor stub
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
		private JComboBox<ImageIcon> jcbIconLabel = new JComboBox<ImageIcon>();
		
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
		private Hashtable<ImageIcon,TreeSet<ImageIcon>> mapAppsToIcon = new Hashtable<ImageIcon, TreeSet<ImageIcon>>();
		// a warning pop-up message for starting VCell ImageJ service after selecting the desired plugin
		{
		 JOptionPane.showMessageDialog(null, "Please make sure VCell is launched and Fiji Service is started (Under Tools)!","Warning", JOptionPane.ERROR_MESSAGE); 
		}
		public MyPreProcessor() {
//			jcbModelType.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				try {
//					searchVCell();
//				} catch (Exception e1) {
////					// TODO Auto-generated catch block
////					e1.printStackTrace();
//				}
//			}});
//			jcbModelType.setSelectedIndex(0);

		}
		
		private String createMapAppToSimsKeyName(String modelName,String appName) {
			//return jcbModelNames.getSelectedItem()+" "+jcbAppNames.getSelectedItem();
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
			//System.out.println(service.getDefaultUI().getApplicationFrame().getClass().getName()+" "+(service.getDefaultUI().getApplicationFrame() instanceof Frame));

			displayProgressBar(true, "Searching Database...", "VCell Model Loader", 25,uiService);
			VCellHelper.VCellModelSearch vcms = new VCellHelper.VCellModelSearch(ModelType.valueOf(jcbModelType.getSelectedItem().toString()),null,null,null,null,null,null);
			displayProgressBar(true, "Creating GUI...", "VCell Model Loader", 100,uiService);
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
					//System.out.println(modelName+" "+next.getApplicationName()+" "+next.getSimulationName());
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
//				DefaultComboBoxModel a= null;
//				((DefaultComboBoxModel)jcbUserid.getModel()).
			} catch (Exception e) {
				throw e;
//				//e.printStackTrace();
//				displayProgressBar(false, "Creating GUI...", "VCell Model Loader", 100,uiService);
//				vcellModelsInput.setValue(module, new VCellSelection(e));//return empty VCellSelection
//				module.resolveInput(vcellModelsInput.getName());
//				return;
			}finally {
				displayProgressBar(false, "Creating GUI...", "VCell Model Loader", 100,uiService);
			}

		}
		@Override
		public void process(Module module) {
			final ModuleItem<VCellSelection> vcellModelsInput = getvcellModelsInput(module);
			if (vcellModelsInput == null) {
				return;
			}
//			try {
//				searchVCell();
//			} catch (Exception e) {
//				vcellModelsInput.setValue(module, new VCellSelection(e));//return empty VCellSelection
//				module.resolveInput(vcellModelsInput.getName());
//				return;
//			}
			
//			displayProgressBar(true, "Searching Database...", "VCell Model Loader", 25,uiService);
//			VCellHelper.VCellModelSearch vcms = new VCellHelper.VCellModelSearch(ModelType.valueOf(jcbModelType.getSelectedItem().toString()),null,null,null,null,null,null);
//			displayProgressBar(true, "Creating GUI...", "VCell Model Loader", 100,uiService);
//			try {
//				final DateFormat dateTimeInstance = DateFormat.getDateTimeInstance();
//				ArrayList<VCellModelSearchResults> vcmsr = vcellHelper.getSearchedModelSimCacheKey(false,vcms,null);
//				final Iterator<VCellModelSearchResults> iterator = vcmsr.iterator();
//				while(iterator.hasNext()) {
//					final VCellModelSearchResults next = iterator.next();
//					String userid = next.getUserId();
//					useridSet.add(userid);
//					final String modelName = next.getModelName();
//					final String modelNameTime = modelName+" ("+next.getModelType().name()+")"+" - "+(next.getDate()==null?"unsaved":dateTimeInstance.format(new Date(next.getDate())));
//					TreeSet<String> modelsForUserid = mapUseridToModelNameTime.get(userid);
//					if(modelsForUserid == null) {
//						modelsForUserid = new TreeSet<String>();
//						mapUseridToModelNameTime.put(userid, modelsForUserid);
//					}
//					modelsForUserid.add(modelNameTime);
//					mapModelNameTimeToActualModelname.put(modelNameTime, modelName);
//					//System.out.println(modelName+" "+next.getApplicationName()+" "+next.getSimulationName());
//					TreeSet<String> appsForModel = mapModelToApps.get(modelNameTime);
//					if(appsForModel == null) {
//						appsForModel = new TreeSet<String>();
//						mapModelToApps.put(modelNameTime, appsForModel);
//					}
//					appsForModel.add(next.getApplicationName());
//					String modelNameTimeApp = modelNameTime+" "+next.getApplicationName();
//					TreeSet<String> simsForApp = mapAppsToSims.get(modelNameTimeApp);
//					if(simsForApp == null) {
//						simsForApp = new TreeSet<String>();
//						mapAppsToSims.put(modelNameTimeApp, simsForApp);
//					}
//					simsForApp.add(next.getSimulationName());
//				}
//			} catch (Exception e) {
//				//e.printStackTrace();
//				displayProgressBar(false, "Creating GUI...", "VCell Model Loader", 100,uiService);
//				vcellModelsInput.setValue(module, new VCellSelection(e));//return empty VCellSelection
//				module.resolveInput(vcellModelsInput.getName());
//				return;
//			}finally {
//				displayProgressBar(false, "Creating GUI...", "VCell Model Loader", 100,uiService);
//			}
			
			//ApplicationFrame applicationFrame = uiService.getDefaultUI().getApplicationFrame();
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

		//	jp.add(new JLabel("IconLabel"));
		//	jp.add(jcbIconLabel);
			jcbIconLabel.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JLabel Label = new JLabel();
					Component iconLabel = null;
					//iconLabel = new JLabel();
					((Component) iconLabel).setName("IconLabel");
//					iconLabel.set
					((JLabel) iconLabel).setIcon(new ImageIcon(getClass().getResource("/images/ccam_sm_colorgr.gif")));
					((JLabel) iconLabel).setText("");					
				}
				});
			
			//jcbModelType
			//jp.add(new JLabel("Model Type"));
			//jp.add(jcbModelType);
			
			
			jp.add(new JLabel("VCell Userid"));
//			JComboBox<String> jcbUserid = new StyledComboBox<String>(useridSet.toArray(new String[0]));
			jcbModelType.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								searchVCell();
							} catch (Exception e1) {
								uiService.showDialog("Activate VCell Client ImageJ service\\nTools->'Start Fiji (ImageJ) service'\n\n\""+e1.getClass().getName()+"\n"+e1.getMessage());//								JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(jp);
//								topFrame.dispose();
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
										uiService.showDialog("Check connectivity. Login with your userId or a as a guest.");
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
//			JComboBox<String> jcbModelNames = new StyledComboBox<String>(mapUseridToModelNameTime.get(jcbUserid.getSelectedItem()).toArray(new String[0]));
			jcbUserid.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					jcbModelNames.removeAllItems();
					if(jcbUserid.getSelectedItem() != null && mapUseridToModelNameTime.get(jcbUserid.getSelectedItem()) != null) {
						jcbModelNames.setModel(new DefaultComboBoxModel<String>(mapUseridToModelNameTime.get(jcbUserid.getSelectedItem()).toArray(new String[0])));
						if(bUseVCellSelectionHolder[0]) {
							for(int i=0;i<jcbModelNames.getModel().getSize();i++) {
								//Do this because the model names are annotated with their date in this combobox
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
//			JComboBox<String> jcbAppNames = new StyledComboBox<String>(mapModelToApps.get(jcbModelNames.getSelectedItem()).toArray(new String[0]));
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

			//final JComboBox<String> jcbVars = new StyledComboBox<String>();
			//jcbVars.setEnabled(false);

			// JComboBox<String> jcbTimes = new StyledComboBox<String>();
			//jcbTimes.setEnabled(false);

			jp.add(new JLabel("Sim Name"));
			jcbAppNames.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
//					jcbVars.removeAllItems();
//					jcbTimes.removeAllItems();
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
			
			jp.add(new JLabel("Load File"));
			JButton loadFileBtn = new JButton("Load...");
			loadFileBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							displayProgressBar(true, "Getting Vars and Times...", "VCell Model Loader", 25,uiService);
							String userid = jcbUserid.getSelectedItem().toString();
							String modelName = (jcbModelNames.getSelectedItem()==null?null:mapModelNameTimeToActualModelname.get(jcbModelNames.getSelectedItem()).toString());
							String appName = (jcbModelNames.getSelectedItem()==null?null:jcbAppNames.getSelectedItem().toString());
							String simName = (jcbModelNames.getSelectedItem()==null?null:jcbSimNames.getSelectedItem().toString());
							VCellHelper.VCellModelSearch vcms = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm,userid,modelName,appName,simName,null,null);
						      try {
						  		ArrayList<VCellModelSearchResults> vcmsr = vcellHelper.getSearchedModelSimCacheKey(false,vcms,null);
						  		if(vcmsr.size() == 0) {
						  			throw new Exception("No Results for search found");
						  		}
						  		cacheKeyHolder[0] = vcmsr.get(0).getCacheKey();
						  		System.out.println("theCacheKey="+cacheKeyHolder[0]);
						  		
						  		SwingUtilities.invokeAndWait(new Runnable() {
									@Override
									public void run() {
								  		try {
								  			displayProgressBar(true, "Setting Var Times GUI...", "VCell Model Loader", 100,uiService);
										//	jcbVars.setEnabled(true);
											ijVarInfosHolder[0] = vcellHelper.getVarInfos(cacheKeyHolder[0]);
											for(int i=0;i<ijVarInfosHolder[0].getIjVarInfo().size();i++) {
											//	jcbVars.insertItemAt(""+ijVarInfosHolder[0].getIjVarInfo().get(i).getName()+":"+ijVarInfosHolder[0].getIjVarInfo().get(i).getDomain()+" ("+ijVarInfosHolder[0].getIjVarInfo().get(i).getVariableType()+")", i);
											}

										//	jcbTimes.setEnabled(true);
											for(int i=0;i<ijVarInfosHolder[0].getTimes().length;i++) {
										//		jcbTimes.insertItemAt(""+ijVarInfosHolder[0].getTimes()[i], i);
											}
											
											if(vcellModelsInput.getDefaultValue() != null) {//If user provided an inital value for VCellSelection var in VCellPlugin
												final VCellSelection defaultValue = vcellModelsInput.getDefaultValue();
											AbstractButton jcbVars;
												//	jcbVars.setSelectedIndex(0);
												for(int i=0;i<( jcbVars.getModel()).getSize();i++) {
													if(((DefaultComboBoxModel<String>) jcbVars.getModel()).getElementAt(i).startsWith(defaultValue.varName+":")) {
														( jcbVars.setSelected(i));
														break;
													}
												}
												
												JComboBox jcbTimes;
												jcbTimes.setSelectedIndex(0);
												if(defaultValue.timePointIndex < jcbTimes.getModel().getSize()) {
													jcbTimes.setSelectedIndex(defaultValue.timePointIndex);
												}
											}
										} catch (Exception e) {
											//e.printStackTrace();
									  		uiService.showDialog("VCellHelper.ModelType.bm,\"colreeze\",\""+modelName+"\",\""+appName+"\",\""+simName+"\",null,null\n"+e.getMessage(), "GUI setupfailed", MessageType.ERROR_MESSAGE);

										}finally {
											displayProgressBar(false, "Getting Vars and Times...", "VCell Model Loader", 25,uiService);
										}
										
									}});
						  		
						  		
						  	} catch (Exception e2) {
						  		displayProgressBar(false, "Getting Vars and Times...", "VCell Model Loader", 25,uiService);
						  		//e.printStackTrace();
						  		uiService.showDialog("VCellHelper.ModelType.bm,\"colreeze\",\""+modelName+"\",\""+appName+"\",\""+simName+"\",null,null\n"+e2.getMessage(), "Search failed", MessageType.ERROR_MESSAGE);
						  	}
						}}).start();
				}
				});
			jp.add(loadFileBtn);

			
			if(vcellModelsInput.getDefaultValue() != null) {//If user provided an inital value for VCellSelection var in VCellPlugin
				final VCellSelection defaultValue = vcellModelsInput.getDefaultValue();
				jcbUserid.setSelectedItem(defaultValue.userid);
				System.out.println(jcbUserid.getSelectedItem());
				for(int i=0;i<jcbModelNames.getModel().getSize();i++) {
					//Do this because the model names are annotated with their date in this combobox
					if(jcbModelNames.getModel().getElementAt(i).toString().startsWith(defaultValue.modelName)) {
						jcbModelNames.setSelectedIndex(i);
						break;
					}
				}				
				System.out.println(jcbModelNames.getSelectedItem());
				jcbAppNames.setSelectedItem(defaultValue.appName);
				System.out.println(jcbAppNames.getSelectedItem());
				jcbSimNames.setSelectedItem(defaultValue.simname);
				System.out.println(jcbSimNames.getSelectedItem());
			}

			displayProgressBar(false, "Creating GUI...", "VCell Model Loader", 100,uiService);
			Component applicationFrame = null;
			int response = JOptionPane.showConfirmDialog(applicationFrame, jp,"Select User Model App Sim",JOptionPane.OK_CANCEL_OPTION);
			if(response != JOptionPane.OK_OPTION) {
				vcellModelsInput.setValue(module, new VCellSelection(new Exception(CANCELLED)));//return VCellSelection with 'cancel' exception
				module.resolveInput(vcellModelsInput.getName());
				return;
			}
			
			String userid = jcbUserid.getSelectedItem().toString();
			String modelName = (jcbModelNames.getSelectedItem()==null?null:mapModelNameTimeToActualModelname.get(jcbModelNames.getSelectedItem()).toString());
			String appName = (jcbModelNames.getSelectedItem()==null?null:jcbAppNames.getSelectedItem().toString());
			String simName = (jcbModelNames.getSelectedItem()==null?null:jcbSimNames.getSelectedItem().toString());

		    JComboBox jcbTimes = null;
			JComboBox jcbVars = null;
			VCellSelection result = new VCellSelection(cacheKeyHolder[0], userid,modelName, appName, simName,ijVarInfosHolder[0].getIjVarInfo().get(jcbVars.getSelectedIndex()).getName(),jcbTimes.getSelectedIndex());
			vcellModelsInput.setValue(module, result);
			module.resolveInput(vcellModelsInput.getName());

//			String userid = jcbUserid.getSelectedItem().toString();
//			String modelName = (jcbModelNames.getSelectedItem()==null?null:mapModelNameTimeToActualModelname.get(jcbModelNames.getSelectedItem()).toString());
//			String appName = (jcbModelNames.getSelectedItem()==null?null:jcbAppNames.getSelectedItem().toString());
//			String simName = (jcbModelNames.getSelectedItem()==null?null:jcbSimNames.getSelectedItem().toString());
//			vcms = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm,userid,modelName,appName,simName,null,null);
//			String theCacheKey = null;
//		      try {
//		  		ArrayList<VCellModelSearchResults> vcmsr = vcellHelper.getSearchedModelSimCacheKey(false,vcms,null);
//		  		if(vcmsr.size() == 0) {
//		  			throw new Exception("No Results for search found");
//		  		}
//		  		theCacheKey = vcmsr.get(0).getCacheKey();
//		  		System.out.println("theCacheKey="+theCacheKey);
//		  		
//		  		jcbTimes.setEnabled(true);
//		  		IJVarInfos ijVarInfos = vcellHelper.getVarInfos(theCacheKey);
//				for(int i=0;i<ijVarInfos.getTimes().length;i++) {
//					jcbTimes.insertItemAt(""+ijVarInfos.getTimes()[i], i);
//				}
//
//		  		//displayProgressBar(false, null, null);
//		  	} catch (Exception e) {
//		  		//e.printStackTrace();
//		  		uiService.showDialog("VCellHelper.ModelType.bm,\"tutorial\",\""+modelName+"\",\""+appName+"\",\""+simName+"\",null,null\n"+e.getMessage(), "Search failed", MessageType.ERROR_MESSAGE);
//		  		//displayProgressBar(false, null, null);
//		  	}
//
//		    VCellSelection result = new VCellSelection(theCacheKey, userid,modelName, appName, simName);
//			vcellModelsInput.setValue(module, result);
//			module.resolveInput(vcellModelsInput.getName());

//			System.out.println(module);
//			final Map<String, Object> inputs = module.getInputs();
//			for(String key:inputs.keySet()) {
//				System.out.println("     "+key+" "+inputs.get(key));
//			}
		}
		
		private ModuleItem<VCellSelection> getvcellModelsInput(final Module module) {
			ModuleItem<VCellSelection> result = null;
			for (final ModuleItem<?> input : module.getInfo().inputs()) {
				if (module.isInputResolved(input.getName())) continue;
				final Class<?> type = input.getType();
				if (!VCellSelection.class.isAssignableFrom(type)) {
					// not a VCellSelection parameter; abort
					return null;
				}
				if (result != null) {
					// second VCellSelection parameter; abort
					return null;
				}
				@SuppressWarnings("unchecked")
				final ModuleItem<VCellSelection> vcellSelect = (ModuleItem<VCellSelection>) input;
				result = vcellSelect;
			}
			return result;
		}
		
		private JPanel getLabeledJComboBox(String labelName,String[] items) {
			JPanel jp = new JPanel(new FlowLayout());
			jp.add(new JLabel(labelName));
			JComboBox<String> jcbModelNames = new JComboBox<String>(items);
			jp.add(jcbModelNames);
			return jp;
		}

	}	
	
	@Parameter
	private UIService uiService;

  	@Parameter
	private VCellHelper vcellHelper;
  	
  	@Parameter
  	private VCellSelection vcellSelection = new VCellSelection("-1", "colreeze","Monkeyflower_pigmentation_v2", "Pattern_formation", "WT","A",50);
  	//private VCellSelection vcellSelection;
	

    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
    }

	private static JDialog progressDialog = null;
	private static final Dimension dim = new Dimension(200,30);
	private static final JProgressBar jProgressBar = new JProgressBar(0,100) {
		@Override
		public Dimension getPreferredSize() {
			return dim;
		}
		@Override
		public Dimension getSize(Dimension rv) {
			return dim;
		}
	};
    private static void displayProgressBar(boolean bShow,String message,String title,int progress,UIService uiService) {
    	if(progressDialog == null) {
			JFrame applicationFrame = (JFrame)uiService.getDefaultUI().getApplicationFrame();
			progressDialog = new JDialog(applicationFrame,"Checking for VCell Client",false);
			progressDialog.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					super.windowClosing(e);
					progressDialog.dispose();
					progressDialog = null;
				}});
			progressDialog.getContentPane().add(jProgressBar);
			jProgressBar.setStringPainted(true);
			jProgressBar.setString("setting up...");
			progressDialog.pack();
			
    	}
    	
    	if(SwingUtilities.isEventDispatchThread()) {
	    	if(progressDialog ==null) {
				return;
			}
			if(!bShow) {
				progressDialog.dispose();
				progressDialog = null;
				return;
			}
			progressDialog.setVisible(true);
			jProgressBar.setValue(progress);
			progressDialog.setTitle(title);
			jProgressBar.setString(message);
			jProgressBar.invalidate();
			progressDialog.revalidate();
			
    	}else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if(progressDialog ==null) {
						return;
					}
					if(!bShow) {
						progressDialog.dispose();
						progressDialog = null;
						return;
					}
					progressDialog.setVisible(true);
					jProgressBar.setValue(progress);
					progressDialog.setTitle(title);
					jProgressBar.setString(message);
					jProgressBar.invalidate();
					progressDialog.revalidate();
				}
			});
    	}
    }

//    private Hashtable<String,Thread> threadHash = new Hashtable<String,Thread>();
//    private void startJProgressThread0(String lastName,String newName) {
//    	if(lastName != null && threadHash.get(lastName) != null) {
//	    	threadHash.get(lastName).interrupt();
//	    	while(threadHash.get(lastName) != null) {
//	    		try {
//					Thread.sleep(50);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//					break;
//				}
//	    	}
//    	}
//    	if(newName == null) {
//    		return;
//    	}
//    	final Thread progressThread = new Thread(new Runnable(){
//			@Override
//			public void run() {
//				final int[] progress = new int[] {1};
//				while(progressDialog.isVisible()) {
//					if(Thread.currentThread().isInterrupted()) {
//						break;
//					}
//					SwingUtilities.invokeLater(new Runnable() {
//						@Override
//						public void run() {
//							jProgressBar.setValue(progress[0]);
//						}});
//					progress[0]++;
//					try {
//						Thread.sleep(500);
//					} catch (InterruptedException e) {
//						break;
//					}
//				}
//				threadHash.remove(Thread.currentThread().getName());
//			}});
//    	threadHash.put(newName, progressThread);
//		progressThread.setName(newName);
//		progressThread.setDaemon(true);//So not block JVM exit
//		progressThread.start();
//    }
    
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
			String var = vcellSelection.varNames;
			int[] time = new int[] {vcellSelection.timePointIndex};
			displayProgressBar(true, "loading Image...", "VCell Model Loader", 50,uiService);
			IJDataList tpd = vcellHelper.getTimePointData(vcellSelection.theCacheKey,var,VCellHelper.VARTYPE_POSTPROC.NotPostProcess,time,0);
			displayProgressBar(true, "displaying Image...", "VCell Model Loader", 100,uiService);
			double[] data = tpd.ijData[0].getDoubleData();
			BasicStackDimensions bsd = tpd.ijData[0].stackInfo;
			System.out.println(bsd.xsize+" "+bsd.ysize);
			ArrayImg<DoubleType, DoubleArray> testimg = ArrayImgs.doubles( data, bsd.xsize,bsd.ysize,bsd.zsize);
			uiService.show(testimg);
		} catch (Exception e) {
			displayProgressBar(false, "displaying Image...", "VCell Model Loader", 100,uiService);
			uiService.showDialog("theCacheKey,var,VCellHelper.VARTYPE_POSTPROC.NotPostProcess,time,0\n"+e.getMessage(), "getTimePoint(...) failed", MessageType.ERROR_MESSAGE);
		}finally {
			displayProgressBar(false, "displaying Image...", "VCell Model Loader", 100,uiService);
		}
	}
}
