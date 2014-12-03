/**
 * CS576 Multimedia Systems Design
 * Image Based Searching
 * @author bijani@usc.edu
 * @author hmehra@usc.edu
 * 	 
 **/

import java.awt.image.*;

public class colorFeatures 
{
    private BufferedImage inputImg, outputImg;
    private int width;
    private int height;
    private float[][] quantHue = new float[width][height];
    private float[][][] hsvImage = new float[width][height][3];
        
    colorTransform C;
    ImageIO I;
    
    public colorFeatures(BufferedImage inputImg, BufferedImage outputImg)
    {
        this.inputImg = inputImg;
        this.outputImg = outputImg;
        width = inputImg.getWidth();
        height = inputImg.getHeight();
        C = new colorTransform(inputImg, outputImg);
        I = new ImageIO(inputImg);
    }
    
    public void rgbToHSV()
    {
        hsvImage = C.rgbToHSV();        
    }
    
    public BufferedImage rgbToGray()
    {
    	int[][] gray = C.rgbToGray();
    	BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    	
    	for(int l = 0; l < height; l++)
    	{
    		for(int k = 0; k < width; k++)
    		{
    			int pix = 0xff000000 | ((gray[k][l] & 0xff) << 16) | ((gray[k][l] & 0xff) << 8) | ((gray[k][l] & 0xff) << 0);
    			grayImage.setRGB(k, l, pix);
    		}
    	}
    	
    	return grayImage;
    }
    
    public void hueQuantization()
    {
        quantHue = C.quantize(hsvImage, width, height);
    }

    public float[] saturationHist(int x_start, int x_end, int y_start, int y_end)
    {
        float[] sat_Hist = new float[100];
        int k, numPixels = 0;
        
        //Step 3: 'H' component histogram
        for(k = 0; k < 100; k++)
                sat_Hist[k] = 0;

        for(int y = y_start; y < y_end; y++)
        {
            for(int x = x_start; x < x_end; x++)
            {
                k = (int) Math.floor(hsvImage[x][y][1] * 100);
                if(k > 0 && k < 100)                       //make sure white/black/gray don't contribute
                {
                    numPixels++;
                    sat_Hist[k] += 1;               
                }
            }
        }

        for(k = 0; k < 100; k++)
                sat_Hist[k] /= numPixels;   //Normalize
        
        return sat_Hist;
    }
    
    public float[] quantHueHistogram(int x_start, int x_end, int y_start, int y_end)
    {  
        float[] hue_Hist = new float[360];  
        float[] hue_CHist = new float[360];
        int k, numPixels = 0;
        
        //Step 3: 'H' component histogram
        for(k = 0; k < 360; k++)
                hue_Hist[k] = 0;

        for(int y = y_start; y < y_end; y++)
        {
            for(int x = x_start; x < x_end; x++)
            {
                k = (int) Math.floor(quantHue[x][y]);
                if(k >= 0)
                {
                    numPixels++;
                    hue_Hist[k] += 1;               
                }
            }
        }

        for(k = 0; k < 360; k++)
                hue_Hist[k] /= numPixels;   //Normalize 
    
        //Step 4: 'H' component cumulative histogram
        hue_CHist[0] = hue_Hist[0];
        for(k = 1; k < 360; k++)
                hue_CHist[k] = hue_CHist[k-1] + hue_Hist[k];
        
        return hue_Hist;
    }

    public float[] quantHueHistogram(int[][] alpha)
    {  
        float[] hue_Hist = new float[360];  
        float[] hue_CHist = new float[360];
        int k, numPixels = 0;
        
        //Step 3: 'H' component histogram
        for(k = 0; k < 360; k++)
                hue_Hist[k] = 0;

        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                //if(alpha[x][y] == 1 && inputImg.getRGB(x, y) != 0xff000000 && inputImg.getRGB(x, y) != 0xffffffff)
                if(alpha[x][y] == 1)
                {
                    k = (int) Math.floor(quantHue[x][y]);
                    if(k >= 0)
                    {
                        hue_Hist[k] += 1;
                        numPixels++;
                    }    
                }
            }
        }

        for(k = 0; k < 360; k++)
                hue_Hist[k] /= numPixels;   //Normalize 
    
        //Step 4: 'H' component cumulative histogram
        hue_CHist[0] = hue_Hist[0];
        for(k = 1; k < 360; k++)
                hue_CHist[k] = hue_CHist[k-1] + hue_Hist[k];
        
        return hue_Hist;
    }

    
    /*
    public float[] quantHueHistogram(int[][] alpha)
    {  
        float[] hue_Hist = new float[360];  
        float[] hue_CHist = new float[360];
        int k, numPixels = 0;
        
        //Step 3: 'H' component histogram
        for(k = 0; k < 360; k++)
                hue_Hist[k] = 0;

        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                if(alpha[x][y] == 1)
                {
                    k = (int) Math.floor(quantHue[x][y]);
                    if(k >= 0)
                    {
                        hue_Hist[k] += 1;
                        numPixels++;
                    }    
                }
            }
        }

        for(k = 0; k < 360; k++)
                hue_Hist[k] /= numPixels;   //Normalize 
    
        //Step 4: 'H' component cumulative histogram
        hue_CHist[0] = hue_Hist[0];
        for(k = 1; k < 360; k++)
                hue_CHist[k] = hue_CHist[k-1] + hue_Hist[k];
        
        return hue_Hist;
    }
    */
    
    public BufferedImage reconstructRGB(int flag)
    {
        if(flag == 1)
        {
            for(int y = 0; y < height; y++)
                for(int x = 0; x < width; x++)
                    hsvImage[x][y][0] = (quantHue[x][y] / 360);
        }
        
        outputImg = C.hsvToRGB(hsvImage);            
        return outputImg;
    }
    
    public float[][][] getHsvMatrix()
    {
        return hsvImage;
    }
            
    public void displayGrayImage()
    {
        BufferedImage grayImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
        int gray[][] = new int[width][height];
        int x, y;
        
        gray = C.rgbToGray();
        
        for(y = 0; y < height; y++)
            for(x = 0; x < width; x++)
            {   
                int pix = (0xff000000 | ((gray[x][y] & 0xff) << 16) | ((gray[x][y] & 0xff) << 8) | (gray[x][y] & 0xff));
                grayImg.setRGB(x, y, pix);
            }
        
        I.displayImage(grayImg);
    }
    
    
    public void displayGrayImage(float[] hueHist)
    {
        BufferedImage grayImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
        int gray[][] = new int[width][height];
        int x, y;
        
        gray = C.rgbToGray(hueHist);
        
        for(y = 0; y < height; y++)
            for(x = 0; x < width; x++)
            {   
                int pix = (0xff000000 | ((gray[x][y] & 0xff) << 16) | ((gray[x][y] & 0xff) << 8) | (gray[x][y] & 0xff));
                grayImg.setRGB(x, y, pix);
            }
        
        I.displayImage(grayImg);
    }
    
}
