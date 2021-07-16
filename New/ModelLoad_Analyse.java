/*
 * To the extent possible under law, the ImageJ developers have waived
 * all copyright and related or neighboring rights to this tutorial code.
 *
 * See the CC0 1.0 Universal license for details:
 *     http://creativecommons.org/publicdomain/zero/1.0/
 */

package org.vcell.imagej.plugin;

import java.awt.Dimension;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;
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
  */



@Plugin(type = ContextCommand.class, menuPath = "Plugins>ModelLoad_Analyse")
public class ModelLoad_Analyse extends ContextCommand {
	
	// ModelLoad_Analyse(Toolkit.getDefaultToolkit().getImage(JFrame_Icon.class.getResource("/images/ccam")));
    //
    // Feel free to add more parameters here...
    //
	
	//"colreeze\",\"Monkeyflower_pigmentation_v2\",\"Pattern_formation\",\"WT\",null,null\n"+e.getMessage(), "Search failed", MessageType.ERROR_MESSAGE);
	//displayProgressBar(false, null, null);

//These are the Parameters for our GUI which will be the model information for the generation of the output image
	//This dimensions of our GUI is generated automatically according to the amount of information 
	
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
	
	/* @Parameter
	private String  imageName; */
	
	/* @Parameter */
	/*private String size; */

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

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
