/**
 * CS576 Multimedia Systems Design
 * Image Based Searching
 * @author bijani@usc.edu
 * @author hmehra@usc.edu
 * 	 
 **/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.border.TitledBorder;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class entryPoint extends JFrame {
	
	private static final long serialVersionUID = 1L;        
	static entryPoint baseObj;
	JPanel qPanel, cmpPanel;
    HsvApproach hsv;
    BufferedImage inputImage,outputImage,resultImage;
    String searchPath, queryPath, alphaFilePath = "";
        
	public entryPoint() {
		setResizable(false);
		setMaximumSize(new Dimension(1024, 600));
		setMinimumSize(new Dimension(880, 460));
		setTitle("CS576 - Final Project - Media Based Querying & Searching");
		getContentPane().setLayout(null);
		
		JButton queryButton = new JButton("Query Image");
		queryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile obj = new openFile();
				try {
					queryPath = obj.getFilePath();
					hsv = new HsvApproach();
					inputImage = hsv.returnQueryImage(queryPath);
					setQueryImage(inputImage);
					
				} catch (Exception exp) {
					System.out.println("Error Opening File");
				}
			}
		});
		
		queryButton.setBounds(29, 23, 143, 27);
		getContentPane().add(queryButton);
		
		JButton alphaButton = new JButton("Alpha Image");
		alphaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				openFile obj = new openFile();
				try {
                    alphaFilePath = obj.getFilePath();
					hsv.setAlphaPath(alphaFilePath);
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		});
		
		alphaButton.setBounds(206, 23, 143, 27);
		getContentPane().add(alphaButton);
		
		JButton cmpButton = new JButton("Compare Image");
		cmpButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openFile obj = new openFile();
					try {
						searchPath = obj.getFilePath();
						outputImage = hsv.returncmpImage(searchPath);
						setcmpImage(outputImage);
					} catch (Exception exp) {
						exp.printStackTrace();
					}
				}
		});
		
		cmpButton.setBounds(385, 23, 169, 27);
		getContentPane().add(cmpButton);
		
		JButton btnSearch = new JButton("Search M-I");
		btnSearch.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			try {
					hsv.compute(alphaFilePath);
					resultImage = hsv.returnResultImage();
					setcmpImage(resultImage);	
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		});
		
		btnSearch.setBounds(593, 23, 112, 27);
		getContentPane().add(btnSearch);
		
		JPanel qPanelBorder = new JPanel();
		qPanelBorder.setBorder(new TitledBorder(null, "Query Image", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		qPanelBorder.setBounds(29, 85, 389, 336);
		getContentPane().add(qPanelBorder);
		qPanelBorder.setLayout(null);
		
		qPanel = new JPanel();
		qPanel.setBounds(19, 22, 352, 288);
		qPanelBorder.add(qPanel);
		qPanel.setMaximumSize(new Dimension(352, 288));
		
		JPanel cmpPanelBorder = new JPanel();
		cmpPanelBorder.setLayout(null);
		cmpPanelBorder.setBorder(new TitledBorder(null, "Compare Image", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		cmpPanelBorder.setBounds(460, 85, 389, 336);
		getContentPane().add(cmpPanelBorder);
		
		cmpPanel = new JPanel();
		cmpPanel.setMaximumSize(new Dimension(352, 288));
		cmpPanel.setBounds(19, 22, 352, 288);
		cmpPanelBorder.add(cmpPanel);
		
		JButton btnSearchMii = new JButton("Search M-II");
		btnSearchMii.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					
					setcmpImage(hsv.computeSecond(queryPath,searchPath, alphaFilePath));	
				} catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		});
		btnSearchMii.setBounds(735, 23, 133, 27);
		getContentPane().add(btnSearchMii);
		
	}
               
        public void setQueryImage(BufferedImage img)
        {
            JLabel label = new JLabel(new ImageIcon(img));            
            qPanel.removeAll();
            qPanel.add(label);
            baseObj.setVisible(true);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
        
        public void setcmpImage(BufferedImage img)
        {
            JLabel label = new JLabel(new ImageIcon(img));            
            cmpPanel.removeAll();
            cmpPanel.add(label);
            baseObj.setVisible(true);
           
        }
        	
	
        public static void main(String[] args) {
		baseObj = new entryPoint();
		baseObj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		baseObj.setVisible(true);
	}
}

