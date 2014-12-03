/**
 * CS576 Multimedia Systems Design
 * Image Based Searching
 * @author bijani@usc.edu
 * @author hmehra@usc.edu
 * 	 
 **/
 

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;


public class ImageIO {
   
   private BufferedImage img;
   private int width;
   private int height;
   
   public ImageIO(BufferedImage img)
   {
       this.img = img;
       width = img.getWidth();
       height = img.getHeight();
   }
   
   public BufferedImage readColorImage(String filename)
   {
       try {
	    File file = new File(filename);
	    InputStream is = new FileInputStream(file);

	    long len = file.length();
	    byte[] bytes = new byte[(int)len];
	    
	    int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        		
            int ind = 0;
            for(int y = 0; y < height; y++)
                {
                    for(int x = 0; x < width; x++)
                    {      
                        byte r = bytes[ind];
                        byte g = bytes[ind+height*width];
                        byte b = bytes[ind+height*width*2];

                        int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
                        img.setRGB(x,y,pix);
                        ind++;
                    }
                }
            is.close();	
            } 
            
            catch (FileNotFoundException e) {
                    e.printStackTrace();
            } 
            catch (IOException e) {
                    e.printStackTrace();
            }
       return img;
   }
   
   
   public void resetImage(BufferedImage im)
   {
       for(int l = 0; l < im.getHeight(); l++)
         for(int k = 0; k < im.getWidth(); k++)                        
              im.setRGB(k, l, 0xff000000);
   }
   
   public BufferedImage addAlphaComponent(String filename)
   {
       try {
	    File file = new File(filename);
	    InputStream is = new FileInputStream(file);

	    long len = file.length();
	    byte[] bytes = new byte[(int)len];
	    
	    int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        		
            int ind = 0;
            for(int y = 0; y < height; y++)
                {
                    for(int x = 0; x < width; x++)
                    {
                        byte a = bytes[ind];
                        int pix = img.getRGB(x, y);
                        int pix1 = pix & 0x00ffffff;
                        int pix_new = (((255 * a) & 0xff) << 24) | pix1;
                        img.setRGB(x,y,pix_new);
                        ind++;
                    }
                }
            is.close();
       }
       
            catch (FileNotFoundException e) {
                    e.printStackTrace();
            } 
            catch (IOException e) {
                    e.printStackTrace();
            }
       
       return img;
   }        
        
   public int[][] readAlphaChannel(String filename)
   {
       int[][] alpha = new int[width][height];
       
       try {
	    File file = new File(filename);
	    InputStream is = new FileInputStream(file);

	    long len = file.length();
	    byte[] bytes = new byte[(int)len];
	    
	    int offset = 0;
            int numRead = 0;
            while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
                offset += numRead;
            }
        		
            int ind = 0;
            for(int y = 0; y < height; y++)
            	for(int x = 0; x < width; x++)
            		alpha[x][y] = bytes[ind++];
            
          is.close();              
       }
       
            catch (FileNotFoundException e) {
                    e.printStackTrace();
            } 
            catch (IOException e) {
                    e.printStackTrace();
            }
       
       return alpha;
   }        
   
   public int[][] extractColorPlanes(int planeNum)
   {
       int[][] grayPlane = new int[width][height];
       int shiftBy;
       
       if(planeNum == 1)
           shiftBy = 16;
       else if(planeNum == 2)
           shiftBy = 8;
       else
           shiftBy = 0;
       
       for(int x = 0; x < width; x++)
       {
           for(int y = 0; y < height; y++)
           {
               int pix = img.getRGB(x, y);
               grayPlane[x][y] = (pix >> shiftBy) & 0xff;               
           }
       }
       return grayPlane;
   }
           
   public void displayImage()
   {
        //Create a new JFrame to display images
        JFrame frame = new JFrame();
        frame.setTitle("Output Image");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel label = new JLabel(new ImageIcon(img));            //Input Image
        frame.getContentPane().add(label, BorderLayout.CENTER);
        
        //Additional display controls
        frame.getContentPane().setBackground(Color.white);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
   } 
   
   public void displayImage(BufferedImage outputImg)
   {
        //Create a new JFrame to display images
        JFrame frame = new JFrame();
        frame.setTitle("Input and Output Images");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel label = new JLabel(new ImageIcon(img));            //Input Image
        frame.getContentPane().add(label, BorderLayout.WEST);
        
        //Place the images in the frame
        JLabel label2 = new JLabel(new ImageIcon(outputImg));   //Output Image  
        frame.getContentPane().add(label2, BorderLayout.CENTER);

        //Additional display controls
        frame.getContentPane().setBackground(Color.white);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
   } 
     
}
