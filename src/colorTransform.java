/**
 *
 * @author bijani@usc.edu, hmehra@usc.edu
 */

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

public class colorTransform 
{
    private BufferedImage img, outputImg;
    private int width;
    private int height;
    
    ImageIO I;
        
    public colorTransform(BufferedImage img, BufferedImage outputImg)
    {
        this.img = img;
        this.outputImg = outputImg;
        width = img.getWidth();
        height = img.getHeight();
        I = new ImageIO(img);
    }
        
    public float[][][] rgbToHSV()
    {
        int[][] rComp = new int[width][height];
        int[][] gComp = new int[width][height];
        int[][] bComp = new int[width][height];
        
        float[][][] hsv = new float[width][height][3];       
        
        rComp = I.extractColorPlanes(1);
        gComp = I.extractColorPlanes(2);
        bComp = I.extractColorPlanes(3);
        
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                   hsv[x][y] = Color.RGBtoHSB(rComp[x][y], gComp[x][y], bComp[x][y], hsv[x][y]);

        return hsv;
    }
    
    public BufferedImage hsvToRGB(float[][][] hsv)
    {
        int pix;
        
        for(int y = 0; y < height; y++)
        {
            for(int x = 0; x < width; x++)
            {
                pix = Color.HSBtoRGB(hsv[x][y][0], hsv[x][y][1], hsv[x][y][2]);
                outputImg.setRGB(x, y, pix);
            }    
        }        
        return outputImg;
    }
        
    public int[][] rgbToGray()
    {
        int[][] rComp = new int[width][height];
        int[][] gComp = new int[width][height];
        int[][] bComp = new int[width][height];
        
        int[][] gray = new int[width][height];       
        
        rComp = I.extractColorPlanes(1);
        gComp = I.extractColorPlanes(2);
        bComp = I.extractColorPlanes(3);
        
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                   gray[x][y] = (int)((0.21 * rComp[x][y]) + (0.71 * gComp[x][y]) + (0.07 * bComp[x][y]));

        return gray;
    }
        
    public int[][] rgbToGray(float[] hueHist)
    {
        int[][] rComp = new int[width][height];
        int[][] gComp = new int[width][height];
        int[][] bComp = new int[width][height];
        
        int[][] gray = new int[width][height];       
        
        float rCoeff = 0, gCoeff = 0, bCoeff = 0;
        
        rComp = I.extractColorPlanes(1);
        gComp = I.extractColorPlanes(2);
        bComp = I.extractColorPlanes(3);
        
        for(int k = 61; k <= 180; k++)
            gCoeff += hueHist[k];
        
        for(int k = 181; k <= 300; k++)
            bCoeff += hueHist[k];
        
        rCoeff = 1 - (gCoeff + bCoeff);
        
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                   gray[x][y] = (int)((rCoeff * rComp[x][y]) + (gCoeff * gComp[x][y]) + (bCoeff * bComp[x][y]));
        
        return gray;
    }

    //check quantization parameters. Try combinations (no. of bins etc)
    public float[][] quantize(float[][][] hsvImage, int width, int height)
        {       
            float[][] quantHue = new float[width][height];
            int val;

            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    val = Math.round(hsvImage[x][y][0] * (360 / 30));           //[0, 1) to [0, 360)                    

                    
                    if((((hsvImage[x][y][0] <= 5) || (hsvImage[x][y][0] >= 355)) && (hsvImage[x][y][1] <= 0.3)) || (hsvImage[x][y][2] <= 0.2))
                       quantHue[x][y] = -1;

                    //if(hsvImage[x][y][1] != 0)
                    else if(hsvImage[x][y][1] >= 0.62 && hsvImage[x][y][2] >= 0.2)    
                    //else
                    {   
                        switch(val)
                        {
                            case 0: quantHue[x][y] = 0; break;
                            case 1: if((hsvImage[x][y][0] * 360) >= 15 && (hsvImage[x][y][0] * 360) <= 30)
                                    quantHue[x][y] = 15;
                                    else
                                    quantHue[x][y] = 30; 
                                    break;
                            case 2: quantHue[x][y] = 60; break;
                            case 3: quantHue[x][y] = 90; break;
                            case 4: quantHue[x][y] = 120; break;
                            case 5: quantHue[x][y] = 150; break;
                            case 6: quantHue[x][y] = 180; break;
                            case 7: quantHue[x][y] = 210; break;
                            case 8: quantHue[x][y] = 240; break;
                            case 9: quantHue[x][y] = 270; break;
                            case 10: quantHue[x][y] = 300; break;
                            case 11: quantHue[x][y] = 330; break;
                            default: quantHue[x][y] = 0;
                        }
                    }
                    else
                        quantHue[x][y] = -1;
                    /*else
                    {
                       if(hsvImage[x][y][2] == 1)
                          quantHue[x][y] = -1;
                        
                       else if(hsvImage[x][y][2] == 0)
                            quantHue[x][y] = -1;
                    }
                    */    
                }
            }
            return quantHue;
        }

    
    
/*    
//check quantization parameters. Try combinations (no. of bins etc)
    public float[][] quantize(float[][][] hsvImage, int width, int height)
        {       
            float[][] quantHue = new float[width][height];
            int val;

            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    val = Math.round(hsvImage[x][y][0] * (360 / 20));           //[0, 1) to [0, 360)                    
                    
                    if((((hsvImage[x][y][0] <= 5) || (hsvImage[x][y][0] >= 355)) && (hsvImage[x][y][1] <= 0.3)) || (hsvImage[x][y][2] <= 0.2))
                       quantHue[x][y] = -1;
                    
                    //if(hsvImage[x][y][1] != 0)
                    else if(hsvImage[x][y][1] >= 0.1 && hsvImage[x][y][2] >= 0.1)    
                    //else
                    {   
                        switch(val)
                        {
                            case 0: quantHue[x][y] = 0; break;
                            case 1: quantHue[x][y] = 20; break;
                            case 2: quantHue[x][y] = 40; break;
                            case 3: quantHue[x][y] = 60; break;
                            case 4: quantHue[x][y] = 80; break;
                            case 5: quantHue[x][y] = 100; break;
                            case 6: quantHue[x][y] = 120; break;
                            case 7: quantHue[x][y] = 140; break;
                            case 8: quantHue[x][y] = 160; break;
                            case 9: quantHue[x][y] = 180; break;
                            case 10: quantHue[x][y] = 200; break;
                            case 11: quantHue[x][y] = 220; break;
                            case 12: quantHue[x][y] = 240; break;
                            case 13: quantHue[x][y] = 260; break;
                            case 14: quantHue[x][y] = 280; break;
                            case 15: quantHue[x][y] = 300; break;
                            case 16: quantHue[x][y] = 320; break;
                            case 17: quantHue[x][y] = 340; break;
                            default: quantHue[x][y] = 0;
                        }
                    }
                    else
                        quantHue[x][y] = -1;
                    /*else
                    {
                       if(hsvImage[x][y][2] == 1)
                          quantHue[x][y] = -1;
                        
                       else if(hsvImage[x][y][2] == 0)
                            quantHue[x][y] = -1;
                    }
                    */    
/*
                }
            }
            return quantHue;
        } 
    
/*
    //check quantization parameters. Try combinations (no. of bins etc)
    public float[][] quantize(float[][][] hsvImage, int width, int height)
        {       
            float[][] quantHue = new float[width][height];
            int val;

            for(int y = 0; y < height; y++)
            {
                for(int x = 0; x < width; x++)
                {
                    val = Math.round(hsvImage[x][y][0] * (360 / 10));           //[0, 1) to [0, 360)                    
                    
//                    if(hsvImage[x][y][1] != 0)
                    if((((hsvImage[x][y][0] <= 5) || (hsvImage[x][y][0] >= 355)) && (hsvImage[x][y][1] <= 0.3)) || (hsvImage[x][y][2] <= 0.2))
                       quantHue[x][y] = -1;
                        
                    
                    else if(hsvImage[x][y][1] >= 0.8 && hsvImage[x][y][2] >= 0.8)
                    //else
                    {   
                        switch(val)
                        {
                            case 0: quantHue[x][y] = 0; break;
                            case 1: quantHue[x][y] = 10; break;
                            case 2: quantHue[x][y] = 20; break;
                            case 3: quantHue[x][y] = 30; break;
                            case 4: quantHue[x][y] = 40; break;
                            case 5: quantHue[x][y] = 50; break;
                            case 6: quantHue[x][y] = 60; break;
                            case 7: quantHue[x][y] = 70; break;
                            case 8: quantHue[x][y] = 80; break;
                            case 9: quantHue[x][y] = 90; break;
                            case 10: quantHue[x][y] = 100; break;
                            case 11: quantHue[x][y] = 110; break;
                            case 12: quantHue[x][y] = 120; break;
                            case 13: quantHue[x][y] = 130; break;
                            case 14: quantHue[x][y] = 140; break;
                            case 15: quantHue[x][y] = 150; break;
                            case 16: quantHue[x][y] = 160; break;
                            case 17: quantHue[x][y] = 170; break;
                            case 18: quantHue[x][y] = 180; break;
                            case 19: quantHue[x][y] = 190; break;
                            case 20: quantHue[x][y] = 200; break;
                            case 21: quantHue[x][y] = 210; break;
                            case 22: quantHue[x][y] = 220; break;
                            case 23: quantHue[x][y] = 230; break;
                            case 24: quantHue[x][y] = 240; break;
                            case 25: quantHue[x][y] = 250; break;
                            case 26: quantHue[x][y] = 260; break;
                            case 27: quantHue[x][y] = 270; break;
                            case 28: quantHue[x][y] = 280; break;
                            case 29: quantHue[x][y] = 290; break;
                            case 30: quantHue[x][y] = 300; break;
                            case 31: quantHue[x][y] = 310; break;
                            case 32: quantHue[x][y] = 320; break;
                            case 33: quantHue[x][y] = 330; break;
                            case 34: quantHue[x][y] = 340; break;
                            case 35: quantHue[x][y] = 350; break;
                            default: quantHue[x][y] = 0;
                        }
                    }
                    else
                        quantHue[x][y] = -1;
                }
            }
            return quantHue;
        }
*/
}