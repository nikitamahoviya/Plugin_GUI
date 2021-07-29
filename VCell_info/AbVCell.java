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

public class AbVCell extends JFrame
{
    public AbVCell()
    {
        JFrame frame = new JFrame("VCell Information");
        frame.setVisible(true);
        frame.setSize(350, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel("Hello " + System.getProperty("user.name") + "! Welcome to VCell information");
    //    setIconImage(new ImageIcon("D:\\GoogleSummerofCode\\vcell\\vcell-client\\src\\main\\resources\\images\\ccam_sm_colorgr.gif").getImage());
        Image icon = Toolkit.getDefaultToolkit().getImage("\"D:\\GoogleSummerofCode\\vcell\\vcell-client\\src\\main\\resources\\images\\ccam_sm_colorgr.gif"); 
        frame.setIconImage(icon);     
        frame.setVisible(true);
        JPanel panel = new JPanel();
        frame.add(panel);
        panel.add(label);
        JButton site = new JButton("VCell Website");
        site.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               
                try {
                   
                    String myurl = "https://vcell.org/";
                   
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(myurl));
                   
                } catch (Exception e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }
            }
        });
        JButton forums = new JButton("Discussion Forum");
        forums.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               
                try {
                   
                    String myurl = "https://groups.google.com/g/vcell-discuss";
                   
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(myurl));
                   
                } catch (Exception e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }
            }
        });
        
        JButton permission = new JButton("Change Permission");
        permission.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               
                try {
                   
                    String myurl = "https://vcell.org/webstart/VCell_Tutorials/VCell_Help/topics/ch_1/Introduction/Permissions.html";
                   
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(myurl));
                   
                } catch (Exception e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }
            }
        });        
        
        JButton publish = new JButton("Publish your Model");
        publish.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               
                try {
                   
                    String myurl = "https://vcell.org/publish-a-vcell-model";
                   
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(myurl));
                   
                } catch (Exception e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }
            }
        });    
        
        JButton support = new JButton("Contact Support");
        support.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               
                try {
                   
                    String myurl = "https://vcell.org/support";
                   
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(myurl));
                   
                } catch (Exception e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }
            }
        }); 
        panel.add(site);
        panel.add(forums);
        panel.add(permission);
        panel.add(publish);
        panel.add(support);

    }

    protected void openWebPage(String string) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args)
    {
        new AbVCell();
    }
}
