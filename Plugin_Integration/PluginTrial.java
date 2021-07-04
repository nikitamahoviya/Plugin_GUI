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
import org.vcell.imagej.plugin.ModelSearch.MyPreProcessor;

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
@Plugin(type = ContextCommand.class, menuPath = "Plugins>PluginTrial")
public abstract class PluginTrial extends ContextCommand {
	
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
		public VCellSelection(String theCacheKey, VCellHelper.ModelType modelType,String userid,String modelName, String appName, String simname,String[] varName,int[] timePointIndexes, String varNames, int timePointIndex) {
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
			
//			if(vcellModelsInput.getDefaultValue() != null) {//If user provided an inital value for VCellSelection var in VCellPlugin
//				final VCellSelection defaultValue = vcellModelsInput.getDefaultValue();
//				jcbUserid.setSelectedItem(defaultValue.userid);
//				System.out.println(jcbUserid.getSelectedItem());
//				for(int i=0;i<jcbModelNames.getModel().getSize();i++) {
//					//Do this because the model names are annotated with their date in this combobox
//					if(jcbModelNames.getModel().getElementAt(i).toString().startsWith(defaultValue.modelName)) {
//						jcbModelNames.setSelectedIndex(i);
//						break;
//					}
//				}				
//				System.out.println(jcbModelNames.getSelectedItem());
//				jcbAppNames.setSelectedItem(defaultValue.appName);
//				System.out.println(jcbAppNames.getSelectedItem());
//				jcbSimNames.setSelectedItem(defaultValue.simname);
//				System.out.println(jcbSimNames.getSelectedItem());
//			}

//	  		jp.add(new JLabel("Variables"));
//			jp.add(jcbVars);
//			jp.add(new JLabel("Times"));
//			jp.add(jcbTimes);

			final String[] cacheKeyHolder = new String[1];
			final IJVarInfos[] ijVarInfosHolder = new IJVarInfos[1];
//			final int[] ijVarInfoSelectionIndexesVars;
//			final int[] ijVarInfoSelectionIndexesTimes;

			
//			jp.add(new JLabel("Show Vars and Times"));
//			final JButton loadVarsAndTimesBtn = new JButton("Show...");
//			loadVarsAndTimesBtn.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					new Thread(new Runnable() {
//						@Override
//						public void run() {
//							populateVarAndTimes(vcellModelsInput.getDefaultValue(), jcbVars, jcbTimes, cacheKeyHolder, ijVarInfosHolder);
//						}
//					}).start();
//				}
//				});
//			jp.add(loadVarsAndTimesBtn);

			
			jp.add(new JLabel("Load File"));
			JButton selectLoadFileBtn = new JButton("Select...");
			selectLoadFileBtn.addActionListener(new ActionListener() {
				public void run() {
							try {
								Object vcellSelection;
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

				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
				}
					}
			

	private ModuleItem<VCellSelection> getvcellModelsInput(Module module) {
			// TODO Auto-generated method stub
			return null;
		}

	protected void displayProgressBar(boolean b, String string, String string2, int i, UIService uiService2) {
			// TODO Auto-generated method stub
			
		}

	public void run() {
	// TODO Auto-generated method stub
	
	}
		}
	}
				
