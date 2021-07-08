package org.vcell.imagej.plugin;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JSlider;

import ij.WindowManager;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.Range;
import org.jfree.data.xy.DefaultXYDataset;
import org.scijava.command.ContextCommand;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.DialogPrompt.MessageType;
import org.scijava.ui.UIService;
import org.vcell.imagej.helper.VCellHelper;
import org.vcell.imagej.helper.VCellHelper.IJDataList;
import org.vcell.imagej.helper.VCellHelper.IJTimeSeriesJobResults;
import org.vcell.imagej.helper.VCellHelper.IJVarInfos;
import org.vcell.imagej.helper.VCellHelper.VARTYPE_POSTPROC;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;

import java.awt.Color;

import net.imagej.ImageJ;

@Plugin(type = ContextCommand.class, menuPath = "Plugins>Combinefig")
public class Combinefig extends ContextCommand{

	@Parameter
	private UIService uiService;

  	@Parameter
	private VCellHelper vcellHelper;
  	
  	@Parameter (choices={"bm", "mm"}, style="listBox")
  	private String modelType;
  	
	@Parameter
	private String vCellUser = "colreeze";
	
	@Parameter
	private String  vCellModel = "Monkeyflower_pigmentation_v2";
	
	@Parameter
	private String application = "Pattern_formation";
	
	@Parameter
	private String simulation = "WT";
	
	@Parameter
	private String simImageTitle = "Sim fluor_0";
	
	@Parameter
	private String expImageTitle = "abp";
	
	public static void main(String[] args) {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
	}
	@Override
	public void run() {
		
		try {
			//Find the port that a separately running VCell client is listening on
			System.out.println("vcell service port="+vcellHelper.findVCellApiServerPort());
		} catch (Exception e) {
			//	uiService.showDialog("Activate VCell client ImageJ service\nTools->'Start Fiji (ImageJ) service'\n"+e.getMessage(), "Couldn't contact VCell client", MessageType.ERROR_MESSAGE);
				return;
		}
		

		VCellHelper.VCellModelSearch vcms = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.valueOf(modelType),vCellUser,vCellModel,application,simulation,null,null);
	      try {
			ArrayList<VCellModelSearchResults> vcmsr = vcellHelper.getSearchedModelSimCacheKey(false,vcms,null);
			if(vcmsr.size() == 0) {
				throw new Exception("No Results for search found");
			}
		} catch (Exception e) {
			uiService.showDialog(modelType+", "+vCellUser+", "+vCellModel+", "+application+", "+simulation+", null, null\n"+e.getMessage(), "Search failed", MessageType.ERROR_MESSAGE);
			return;
		}
	     String simDupTitle = simImageTitle+"-1";
	     String expDupTitle = expImageTitle+"-1";
	     
	     
	     JFrame frame = new JFrame("Figure");
	     frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	     frame.getContentPane().setBackground(new Color(0,0,0)); //setting the background color
	     frame.setLocationRelativeTo(null);
	     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.pack();
			frame.setVisible(true);	     
			JButton SelectAll = new javax.swing.JButton("Select All");
			JButton Copy = new javax.swing.JButton("Copy");
			JButton Paste = new javax.swing.JButton("Paste");
			JButton AddSlice = new javax.swing.JButton("Add Slice");
			
	}
}
	      
