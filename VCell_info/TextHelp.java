/*
 * #%L
 * Core commands for SciJava applications.
 * %%
 * Copyright (C) 2010 - 2017 Board of Regents of the University of
 * Wisconsin-Madison, Broad Institute of MIT and Harvard, and Max Planck
 * Institute of Molecular Cell Biology and Genetics.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package org.vcell.imagej.plugin;


import java.awt.Dimension;
import org.scijava.ItemIO;
import org.scijava.ItemVisibility;
import org.scijava.command.Command;
import org.scijava.command.ContextCommand;
import org.scijava.command.Previewable;
import org.scijava.io.RecentFileService;
import org.scijava.menu.MenuConstants;
import org.scijava.plugin.Attr;
import org.scijava.plugin.Menu;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.widget.Button;
import org.scijava.widget.NumberWidget;
//import org.vcell.util.BeanUtils;

import net.imagej.ImageJ;

/*import cbit.vcell.client.PopupGenerator;
import cbit.vcell.resource.PropertyLoader*/

import java.awt.Font;
import java.awt.event.ActionEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 * A test of text displays.
 * 
 * @author Curtis Rueden
 */

@Plugin(type = Command.class, label = "VCell-ImageJ Help",  iconPath="help.gif",menu = {
		@Menu(label = MenuConstants.PLUGINS_LABEL, weight = MenuConstants.FILE_WEIGHT,
			mnemonic = MenuConstants.FILE_MNEMONIC),
		@Menu(label = "VCell Plugins",  weight = 4, mnemonic = 'V'),
		@Menu(label = "VCell-ImageJ Help", weight = 1,
			mnemonic = 'h',accelerator = "^H") },  attrs = { @Attr(name = "no-legacy") })
public class TextHelp implements Command, Previewable {

	JFrame frame = new JFrame();

	@Parameter(visibility = ItemVisibility.MESSAGE)
	private final String header1 = "<html><h2 style=\"font-family:'Times New Roman';color:green;\">Hello " + System.getProperty("user.name") + " !  Welcome to VCell Help</h2><html>";

	@Parameter(visibility = ItemVisibility.MESSAGE)
	private final String header2 = "<html><h3 style=\"font-family:'Optima';color:red;\">About your machine </h2><html>";

	@Parameter(visibility = ItemVisibility.MESSAGE) 
	private final String header3 = "<html><pstyle=\\\"font-family:'Optima'\\\">Java Version: "+" " + System.getProperty("java.version") +"<br> Operating System: "+" " +System.getProperty("os.name")+ " " + System.getProperty("os.arch");
	
	@Parameter(visibility = ItemVisibility.MESSAGE)
	private final String header4 = "<html><h3 style=\"font-family:'Optima';color:red;\">Pre-Requirements for running VCell Plugins </h2><html>";

	@Parameter(visibility = ItemVisibility.MESSAGE) 
	  private final String header6 = "<html><p style=\"font-family:'Optima'\">Virtual Cell is an advanced software platform for modeling and simulating reaction <br> "
		  		+ " kinetics,membrane transport and diffusion in the complex geometries</p><html>";
	
	@Parameter(visibility = ItemVisibility.MESSAGE) 
	private final String header5 = "<html><p style=\"font-family:'Optima'\">To run various VCell Plugins there are certain pre requirements which are listed below <p> "
		  		+ "<ul> <li>VCell Client ImageJ service should be activated.</li>"
		  		+"<li>Check your connectivity and login to VCell with your userId or as a guest.</li></ul></html>";


	@Parameter(visibility = ItemVisibility.MESSAGE) 
	  private final String header8 = "<html><p style=\"font-family:'Optima'\">Virtual Cell is an advanced software platform for modeling  <br> "
		  		+ "and simulating reaction kinetics,membrane transport <br>"
		  		+ "and diffusion in the complex geometries</p><html>";
	
	
	@Parameter(visibility = ItemVisibility.MESSAGE)
	private final String header9 = "<html><h3 style=\"font-family:'Optima';color:red;\">Online Resources</h2><html>";

	@Parameter(label="VCell Webiste")
	private Button buttonWebsite;
	
	@Parameter(label="Disucssion Forum")
	private Button buttonForum;
	
	@Parameter(label="Change Permission")
	private Button buttonPermission;
	
	@Parameter(label="Publish your Model")
	private Button buttonModel;
	
	@Parameter(label="Contact Support")
	private Button buttonSupport;

	
    public static void main(final String... args) throws Exception {
        // create the ImageJ application context with all available services
    	
    	
        final ImageJ ij = new ImageJ();
        ij.ui().showUI();
    }

	private JDialog progressDialog = null;
	private final Dimension dim = new Dimension(200,25);
	private final JProgressBar jProgressBar = new JProgressBar(0,100) {
		@Override
		public Dimension getPreferredSize() {
			// TODO Auto-generated method stub
			return dim;
		}
		@Override
		public Dimension getSize(Dimension rv) {
			// TODO Auto-generated method stub
			return dim;
		}
	};
		/*
	 * try { buttonWebsite.addActionListener(new ActionListener() { public void
	 * actionPerformed(ActionEvent e) {
	 * 
	 * try {
	 * 
	 * String myurl = "https://vcell.org/";
	 * 
	 * java.awt.Desktop.getDesktop().browse(java.net.URI.create(myurl));
	 * 
	 * } catch (Exception e2) { // TODO: handle exception e2.printStackTrace(); } }
	 * }); }
	 */
	@Override
	public void preview() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}
