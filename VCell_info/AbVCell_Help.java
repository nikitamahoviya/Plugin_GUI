package org.vcell.imagej.plugin;

import java.awt.Desktop;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.*;

public class AbVCell_Help extends JFrame
{
    public AbVCell_Help()
    {
        JFrame frame = new JFrame("VCell Help");
        frame.setVisible(true);
        frame.setSize(350, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Image icon = Toolkit.getDefaultToolkit().getImage("\"D:\\GoogleSummerofCode\\vcell\\vcell-client\\src\\main\\resources\\images\\ccam_sm_colorgr.gif"); 
        frame.setIconImage(icon);     
        frame.setVisible(true);
        JPanel panel = new JPanel();
        frame.add(panel);

    }

	public static void main(String[] args)
    {
        new AbVCell_Help();
    }
}
