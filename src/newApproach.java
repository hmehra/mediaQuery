
import java.awt.image.BufferedImage;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lohit
 */
public class newApproach {
    
    private BufferedImage inputImg, outputImg;
    private int width;
    private int height;
    private float[][] quantHue = new float[width][height];
    private float[][][] hsvImage = new float[width][height][3];
        
    colorTransform C;
    ImageIO I;
    
    public newApproach(BufferedImage inputImg, BufferedImage outputImg)
    {
        this.inputImg = inputImg;
        this.outputImg = outputImg;
        width = inputImg.getWidth();
        height = inputImg.getHeight();
        C = new colorTransform(inputImg, outputImg);
        I = new ImageIO(inputImg);
    }
    
    public float[][][] getHSVImage()
    {
        return hsvImage;
    }
    
    public void rgbToHSV()
    {
        hsvImage = C.rgbToHSV();        
    }
    
    public float[] hueHistogram(int x_start, int x_end, int y_start, int y_end)
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
                if(hsvImage[x][y][1] >= 0.65 && hsvImage[x][y][2] >= 0.2)
                {
                    k = (int) Math.floor(hsvImage[x][y][0] * 360);
                    if(k >= 0)
                    {
                        numPixels++;
                        hue_Hist[k] += 1;               
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

    
    public float[] hueHistogram(int[][] alpha)
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
                    k = (int) Math.floor(hsvImage[x][y][0] * 360);
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

    public double meanHistogram(float[] hist, int choice)
    {
        float mean = 0, stddev = 0;
        
        for(int k = 0; k  < 360; k++)
            mean += (k * hist[k]);
                
        for(int l = 0; l  < 360; l++)
            stddev += (hist[l] * (l - mean) * (l - mean));
        
        if(choice == 1)
            return mean;        
        else
            return Math.sqrt(stddev);
    }

    public BufferedImage thresholdImage(double mean, double dev)
    {
        BufferedImage threshImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
            for(int l = 0; l < height; l++)
            {    
                for(int k = 0; k < width; k++)
                {
                    float hue = (hsvImage[k][l][0] * 360), sat = hsvImage[k][l][1],  val = hsvImage[k][l][2];
                    float lowerLimit = (float) (mean - (dev/1.2));
                    float upperLimit = (float) (mean + (dev/1.2));
                    
                    if(hue >= lowerLimit && hue <= upperLimit && sat >= 0.5 && val >= 0.2)
                        threshImage.setRGB(k, l, 0xffffffff);
                    
                }    
            }
            
            return threshImage;
    }
    
    
    public BufferedImage isMajorityWhite(BufferedImage im)
    {
        int sum = 0;
        int[][] map = pixelMap(im);
        
        for(int l = 0; l < height; l++)
            for(int k = 0; k < width; k++)
                sum += map[k][l];
        
        if(sum <= (0.01 * width * height))
        {
            for(int l = 0; l < height; l++)
                for(int k = 0; k < width; k++)
                    im.setRGB(k, l, 0xff000000);
        }
        
        return im;
    }
    public int[][] pixelMap(BufferedImage im)
    {
       int[][] candidateMap = new int[width][height]; 
       
        for(int l = 0; l < height; l++)
        {
            for(int k = 0; k < width; k++)
            {
                if(im.getRGB(k,l) == 0xffffffff)
                    candidateMap[k][l] = 1;                
                else
                    candidateMap[k][l] = 0;
            }    
        }

        return candidateMap;
    }

    public BufferedImage medianFilter(BufferedImage im)
    {
        int[][] connectivityMap = pixelMap(im);
        BufferedImage cleanedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int l = 1; l < height-1; l++)
        {
            for(int k = 1; k < width-1; k++)
            {
                if(connectivityMap[k][l] == 1)
                {
                    int sum = 0;
                    for(int q = (l-1); q <= (l+1); q++)
                        for(int p = (k-1); p <= (k+1); p++)
                            sum += connectivityMap[p][q];
                    
                    if(sum >= 5)
                        cleanedImage.setRGB(k, l, 0xffffffff);
                }
            }
        }
        return cleanedImage;
    }

    public BufferedImage majorityBlack(BufferedImage im)
    {
        int[][] connectivityMap = pixelMap(im);
        BufferedImage cleanedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for(int l = 1; l < height-1; l++)
        {
            for(int k = 1; k < width-1; k++)
            {
                cleanedImage.setRGB(k, l, im.getRGB(k, l));
                
                if(connectivityMap[k][l] == 0)
                {
                    int sum = 0;
                    for(int q = (l-1); q <= (l+1); q++)
                        for(int p = (k-1); p <= (k+1); p++)
                            sum += connectivityMap[p][q];
                    
                    if(sum >= 5)
                        cleanedImage.setRGB(k, l, 0xffffffff);
                }
            }
        }
        return cleanedImage;
    }
    
    public int[] meanLocation(BufferedImage im)
    {
        int[][] map = pixelMap(im);
        int[] mean = {0, 0};
        int numPixels = 0;
        
        for(int l = 0; l < height; l++)
        {
            for(int k = 0; k < width; k++)
            {
                if(map[k][l] == 1)
                {
                    numPixels++;
                    mean[0] += k;
                    mean[1] += l;
                }
            }
        }
        
        if(numPixels > 0)
        {
            mean[0] /= numPixels;
            mean[1] /= numPixels;
        }
        else
        {
            mean[0] = 0;
            mean[1] = 0;
        }
        return mean;
    }
    
    public int[] boxLimits(BufferedImage im, int[] mean)
    {
        int[][] map = pixelMap(im);
        int[] dev = {0, 0};
        double[] dev1 = {0, 0}; 
        int numXPixels = 0, numYPixels = 0;
        int xMean = mean[0], yMean = mean[1];
        
        for(int k = 0; k < height; k++)
        {
            if(map[xMean][k] == 1)
            {
                numYPixels++;
                dev1[1] += ((k - yMean) * (k - yMean));
            }
        }

        for(int k = 0; k < width; k++)
        {
            if(map[k][yMean] == 1)
            {
                numXPixels++;
                dev1[0] += ((k - xMean) * (k - xMean));
            }
        }

        dev1[0] /= numXPixels;
        dev1[1] /= numYPixels;

        dev[0] = (int) Math.sqrt(dev1[0]);
        dev[1] = (int) Math.sqrt(dev1[1]);
               
        return dev;
    }
    
    public BufferedImage drawCOM(BufferedImage im, int[] com, int[] dev)
    {        
            if(dev[0] >= 40)
                dev[0] = 40;

            if(dev[1] >= 40)
                dev[1] = 40;    

            if(dev[0] > 0 && dev[0] < 15)                
                dev[0] = 45;
            
            if(dev[1] > 0 && dev[1] < 15)                
                dev[1] = 45;
            
            int[] topLeft = {com[0] - (int)(1.5 * dev[0]), com[1] - (int)(1.5 * dev[1])};
            int[] bottomRight = {com[0] + (int)(1.5 * dev[0]), com[1] + (int)(1.5 * dev[1])};

            for(int q = (com[1] - 3); q <= (com[1] + 3); q++)
                for(int p = (com[0] - 3); p <= (com[0] + 3); p++)
                        im.setRGB(p, q, 0xffffff00);

            for(int l = topLeft[1]; l <= bottomRight[1]; l++)
                for(int k = topLeft[0]; k <= bottomRight[0]; k++)
                {
                    if(((k >= topLeft[0] && k <= (topLeft[0] + 5)) || (k >= (bottomRight[0] - 5) && k <= bottomRight[0])) || 
                            ((l >= topLeft[1] && l <= (topLeft[1] + 5)) || (l >= (bottomRight[1] - 5) && l <= bottomRight[1])))
                    {
                        if(k > 0 && k < width && l > 0 && l < height)
                                    im.setRGB(k, l, 0xffff0000);
                    }            

                }
        
        return im;
    }
}
