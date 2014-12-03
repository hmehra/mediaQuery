/**
 * CS576 Multimedia Systems Design
 * Image Based Searching
 * @author bijani@usc.edu
 * @author hmehra@usc.edu
 * 	 
 **/

import java.awt.image.*;
public class HsvApproach2 {

    public static void main(String[] args) {

        BufferedImage inputImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
        BufferedImage outputImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
        BufferedImage inputIm = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
        
        int width, height, k, l, x, y;
        int x_start, x_end, y_start, y_end;
        float mse = 0;
        
        width = inputImg.getWidth();
        height = inputImg.getHeight();
        
        float[][][] hsvImage = new float[width][height][3];
        float[] hue_Hist = new float[360];  
        float[] hue_Hist1 = new float[360];
        int[][] alpha = new int[width][height];
        int[] boxCoords = new int[4];
        
        String searchImage = "Images/Search/starbucks_lev0.v576.rgb";
        String alphaFile = "Images/Alpha/starbucks.alpha";
        String queryImage = "Images/Query/starbucks_source_img.v576.rgb";
        
        ImageIO I = new ImageIO(inputImg);
        newApproach F = new newApproach(inputImg, outputImg);
        
        try
        {
            inputImg = I.readColorImage(queryImage);
            I.displayImage();
            
            F.rgbToHSV();         
            alpha = I.readAlphaChannel(alphaFile);
            //hue_Hist = F.hueHistogram(alpha);                               
            hue_Hist = F.hueHistogram(0, width, 0, height);
            double mean = F.meanHistogram(hue_Hist, 1), dev = F.meanHistogram(hue_Hist, 2);            
            
            inputImg = I.readColorImage(searchImage);                        
            F.rgbToHSV();
            hsvImage = F.getHSVImage();
            BufferedImage threshImage = F.thresholdImage(mean, dev);
            BufferedImage cleanImage = F.medianFilter(threshImage);
            BufferedImage majorityBlack = F.majorityBlack(cleanImage);
            BufferedImage isMajorityWhite = F.isMajorityWhite(majorityBlack);
            
            //I.displayImage(threshImage);
            //I.displayImage(cleanImage);
            //I.displayImage(majorityBlack);
            
            cleanImage = F.medianFilter(isMajorityWhite);
            //I.displayImage(cleanImage);
            
            int[] com = F.meanLocation(cleanImage);
            
            if(com[0] > 0 && com[1] > 0)
            {
                int[] devv = F.boxLimits(cleanImage, com);            
                inputImg = I.readColorImage(searchImage);
                F.drawCOM(inputImg, com, devv);
                I.displayImage();
            }
            else
            {
                for(l = 0; l < height; l++)
                    for(k = 0; k < width; k++)
                        inputImg.setRGB(k, l, 0xffffffff);
                
                I.displayImage();
            }
        }         
        catch(Exception e)
        {
            e.printStackTrace();
        }
    
    }
    
}
