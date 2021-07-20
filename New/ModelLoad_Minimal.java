/* To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package org.vcell.imagej.plugin;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

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

	@Parameter
	private UIService uiService;

  	@Parameter
	private VCellHelper vcellHelper;
  	
  	
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
	
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
    	
    	
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
    }

    private Hashtable<String,Thread> threadHash = new Hashtable<String,Thread>();
    private void startJProgressThread0(String lastName,String newName) {
    	
    	if(newName == null) {
    		return;
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
      startJProgressThread0("getTimePointData",null);
	}
}
