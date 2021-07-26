/* To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package org.vcell.imagej.plugin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import org.imagearchive.lsm.toolbox.MasterModel;
import org.imagearchive.lsm.toolbox.ServiceMediator;
import org.imagearchive.lsm.toolbox.gui.GUIButton;
import org.scijava.ItemVisibility;
/*import org.eclipse.swt.widgets.Text; */
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.UIService;
import org.vcell.imagej.helper.VCellHelper;
import org.vcell.imagej.helper.VCellHelper.BasicStackDimensions;
import org.vcell.imagej.helper.VCellHelper.IJDataList;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;

import net.imagej.ImageJ;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;



@Plugin(type = ContextCommand.class, menuPath = "Plugins>ModelLoad_Minimal")
public class ModelLoad_Minimal extends ContextCommand {

	  @Parameter(visibility = ItemVisibility.MESSAGE) private final String header =
	  "Warning: Please make sure that the Fiji (ImageJ) service is running under VCell  ";
	 
	
	@Parameter
	private UIService uiService;

  	@Parameter
	private VCellHelper vcellHelper;
  	
  	private JLabel iconLabel = null;
  	
	@Parameter
	private String vCellUser = "colreeze";
	
	@Parameter
	private String  vCellModel = "Monkeyflower_pigmentation_v2";
	
	@Parameter
	private String application = "Pattern_formation";
	
	@Parameter
	private String simulation = "WT";
	
	@Parameter
	private String variable = "A";
	
	@Parameter
	private int  timePoint = 500;
	
	private ButtonGroup radioGrp;

    private void handleException(Throwable throwable) {
		// TODO Auto-generated method stub
		
	}

	public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
    	
    	
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
    }
    
	@SuppressWarnings("serial")
	public class DocumentWindowAboutBox<VCellSoftwareVersion> extends JPanel {

		private JLabel appName = null;
		private JLabel copyright = null;
		private JLabel iconLabel = null;
		private JLabel version = null;
		private static final String VERSION_NO = "";
		private static final String BUILD_NO = "";
		private static final String EDITION = "";
		private JLabel buildNumber = null;
		private JLabel jarch;

		public void parseVCellVersion() {

		}

			public DocumentWindowAboutBox(String vers, String build) {
			setLayout(new GridBagLayout());

			int gridy = 0;
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridheight = GridBagConstraints.REMAINDER;
			gbc.insets = new Insets(0,0,4,4);
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			add(getIconLabel(), gbc);


			gbc = attributionConstraints(0,++gridy);
			add(getVersion(vers), gbc);

			gbc = attributionConstraints(0,++gridy);
			add(getJavaArch(),gbc);

			gbc = attributionConstraints(0,++gridy);
			add(getBuildNumber(build), gbc);

			gbc = attributionConstraints(0,++gridy);
			add(getCopyright(), gbc);

			gbc = attributionConstraints(10,++gridy);
			add(new JLabel("<html>Virtual Cell is Supported by NIH Grant R24 GM137787 from the<br/> National Institute for General Medical Sciences.</html>"), gbc);

			setFocusable(true);
		}

		

		private JLabel getBuildNumber(String build) {
			if (buildNumber == null) {
				try {
					buildNumber = new JLabel(build);
					buildNumber.setName("BuildNumber");
				} catch (Throwable throwable) {
					handleException(throwable);
				}
			}
			return buildNumber;
		}


		private JLabel getJavaArch() {
			if (jarch == null) {
				String desc = System.getProperty("java.version") + " " + System.getProperty("os.name")+ " " + System.getProperty("os.arch");
				jarch = new JLabel(desc);
			}
			return jarch;
		}

		private JLabel getCopyright() {
			if (copyright == null) {
				try {
					copyright = new JLabel();
					copyright.setName("Copyright");
					copyright.setText("(c) Copyright 1998-2020 UConn Health");
				} catch (Throwable throwable) {
					handleException(throwable);
				}
			}
			return copyright;
		}

		private JLabel getIconLabel() {
			if (iconLabel == null) {
				try {
					iconLabel = new JLabel();
					iconLabel.setName("IconLabel");
//					iconLabel.seth
					iconLabel.setIcon(new ImageIcon(getClass().getResource("/vcell-client/src/main/resources/images/ccam_sm_colorgr.gif")));
					iconLabel.setText("");
				} catch (Throwable throwable) {
					handleException(throwable);
				}
			}
			return iconLabel;
		}


		private JLabel getVersion(String vers) {
			if (version == null) {
				try {
					version = new JLabel(vers);
					version.setName("Version");
				} catch (Throwable throwable) {
					handleException(throwable);
				}
			}
			return version;
		}

		private void handleException(Throwable exception) {
			System.out.println("--------- UNCAUGHT EXCEPTION ---------");
			exception.printStackTrace(System.out);
		}

		/**
		 * common constraints for elements
		 * @param topOffset top offset for {@link Insets}
		 * @param gridy {@link GridBagConstraints#gridy} value
		 * @return new object
		 */
		private GridBagConstraints attributionConstraints(int topOffset, int gridy) {
				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridx = 1;
				gbc.gridy = gridy;
				gbc.weightx = 1.0;
				gbc.fill = GridBagConstraints.HORIZONTAL;
				gbc.insets = new Insets(topOffset,4,0,4);
				gbc.anchor = GridBagConstraints.LINE_START;
				return gbc;
		}
	}

    @Override
    public void run() {
	String theCacheKey = null;
    VCellHelper.VCellModelSearch vcms = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm,vCellUser,vCellModel,application,simulation,null,null);
    try {
		ArrayList<VCellModelSearchResults> vcmsr = vcellHelper.getSearchedModelSimCacheKey(false,vcms,null);
		theCacheKey = vcmsr.get(0).getCacheKey();
		System.out.println("theCacheKey="+theCacheKey);
	} catch (Exception e) {
		uiService.showDialog("VCellHelper.ModelType.bm,vCellUser,vCellModel,application,simulation,null,null\n"+e.getMessage(), "Search failed", MessageType.ERROR_MESSAGE);
	}

      try {
    	  String var = variable;
    	  int[] time = new int[] {timePoint};
    	  IJDataList tpd = vcellHelper.getTimePointData(theCacheKey,var,VCellHelper.VARTYPE_POSTPROC.NotPostProcess,time,0);
    	  double[] data = tpd.ijData[0].getDoubleData();
    	  BasicStackDimensions bsd = tpd.ijData[0].stackInfo;
    	  System.out.println(bsd.xsize+" "+bsd.ysize);
    	  ArrayImg<DoubleType, DoubleArray> testimg = ArrayImgs.doubles( data, bsd.xsize,bsd.ysize);
    	  uiService.show(testimg);
    	  
  	} catch (Exception e) {
		uiService.showDialog("theCacheKey,var,VCellHelper.VARTYPE_POSTPROC.NotPostProcess,time,0\n"+e.getMessage(), "getTimePoint(...) failed", MessageType.ERROR_MESSAGE);

	}
	}
}
