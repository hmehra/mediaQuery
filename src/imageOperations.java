/**
 * CS576 Multimedia Systems Design
 * Image Based Searching
 * @author bijani@usc.edu
 * @author hmehra@usc.edu
 * 	 
 **/


import java.awt.image.*;

public class imageOperations 
{
    private BufferedImage img;
    private int width, height;
    
    public imageOperations(BufferedImage img)
    {
        this.img = img;
        width = img.getWidth();
        height = img.getHeight();
    }
    
    public BufferedImage blockMedianFilter(int blockSize)
    {
        BufferedImage outputImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[][] gray = new int[width][height];
        int[][] index = new int[width/blockSize][height/blockSize];
        int sum;
        
        colorTransform cT = new colorTransform(img, outputImg);
        gray = cT.rgbToGray();
        
        for(int y = 0; y < height; y = y + blockSize)
        {
            for(int x = 0; x < width; x = x + blockSize)
            {
                sum = 0;
                for(int l = y; l < (y + blockSize); l++)
                    for(int k = x; k < (x + blockSize); k++)
                        sum += gray[k][l];
                
                if(sum != 0)
                {
                    //for(int l = y; l < (y + blockSize); l++)
                      //  for(int k = x; k < (x + blockSize); k++)
                            index[x/blockSize][y/blockSize] = 1;
                }
                else
                {
                    index[x/blockSize][y/blockSize] = 0;
                }
            }
        }
        
        for(int l = 0; l < height; l++)
            for(int k = 0; k < width; k++)
                outputImg.setRGB(k, l, 0xff000000);
                
        for(int y = blockSize; y < (height - blockSize); y = y + blockSize)
        {
            for(int x = blockSize; x < (width - blockSize); x = x + blockSize)
            {
                int p = (x/blockSize);
                int q = (y/blockSize);
                sum = 0;
                
                for(int l = (q-1); l <= (q+1); l++)
                    for(int k = (p-1); k <= (p+1); k++)
                            sum += index[k][l];
                
                if(sum >= 5)                    //original 5
                {
                    for(int l = y; l < (y+blockSize); l++)
                        for(int k = x; k < (x+blockSize); k++)
                            outputImg.setRGB(k, l, img.getRGB(k, l));
                }        
            }
        }
        
        return outputImg;
    }
    
    public BufferedImage postProcessing(int blockSize)
    {
        BufferedImage outputImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        int[][] map = connectivityMap(blockSize);
        int[][] map_processed = morphologyOps(blockSize);
                
        for(int l = 0; l < height; l++)
            for(int k = 0; k < width; k++)
                outputImg.setRGB(k, l, 0xff000000);
        
        for(int y = blockSize; y < (height - blockSize); y = y + blockSize)
        {
            for(int x = blockSize; x < (width - blockSize); x = x + blockSize)
            {
                int p = (x/blockSize);
                int q = (y/blockSize);
                
                if(map_processed[p][q] == 1)                    //original 5
                {
                    for(int l = y; l < (y+blockSize); l++)
                        for(int k = x; k < (x+blockSize); k++)
                            outputImg.setRGB(k, l, img.getRGB(k, l));
                }        
            }
        }
        
        return outputImg;
    }
    
    public int[][] connectivityMap(int blockSize)
    {
        BufferedImage outputImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[][] gray = new int[width][height];
        int[][] index = new int[width/blockSize][height/blockSize];
        int sum;
        
        colorTransform cT = new colorTransform(img, outputImg);
        gray = cT.rgbToGray();
        
        for(int y = 0; y < height; y = y + blockSize)
        {
            for(int x = 0; x < width; x = x + blockSize)
            {
                sum = 0;
                for(int l = y; l < (y + blockSize); l++)
                    for(int k = x; k < (x + blockSize); k++)
                        sum += gray[k][l];
                
                if(sum != 0)
                            index[x/blockSize][y/blockSize] = 1;
                else
                    index[x/blockSize][y/blockSize] = 0;
            }
        }
        
        return index;
    }
            
    public int[][] extendMatrix(int[][] oldMat, int blockSize)
    {
        int h = (height/blockSize) + 2;
        int w = (width/blockSize) + 2;
        int[][] newMat = new int[w][h];
        
        for(int y = 0; y < h; y++)
            for(int x = 0; x < w; x++)
                newMat[x][y] = 0;
        
        for(int y = 1; y < (h-1); y++)
            for(int x = 1; x < (w-1); x++)
                newMat[x][y] = oldMat[x-1][y-1];
        
        return newMat;
    }
    
    public int[][] shrinkMatrix(int[][] oldMat, int blockSize)
    {
        int h = (height/blockSize);
        int w = (width/blockSize);
        int[][] newMat = new int[w][h];
        
        for(int y = 0; y < h; y++)
            for(int x = 0; x < w; x++)
                newMat[x][y] = oldMat[x+1][y+1];
        
        return newMat;
    }
    
    public int[][] morphologyOps(int blockSize)
    {
        int[][] map = connectivityMap(blockSize);
        int[][] map_extend = extendMatrix(map, blockSize);
        
        morphOperations m = new morphOperations((width/blockSize) + 2, (height/blockSize) + 2);
        int[][] map_noImpulse = m.impulseRemove(map_extend);
        //int[][] map_noSpur = m.spurRemove(map_noImpulse);
        //int[][] map_fill = m.interiorFill(map_noSpur);
        
        //int[][] map_reduce = shrinkMatrix(map_fill, blockSize);
        int[][] map_reduce = shrinkMatrix(map_noImpulse, blockSize);
        
        return map_reduce;
    }
    
    
    public int[] getBoundingBox(BufferedImage im)
    {
        int[] boxCoords = new int[4];
        int xMin = width, xMax = 0, yMin = height, yMax = 0;
        int rComp, gComp, bComp, gray, inside = 0;
        
        for(int l = 0; l < height; l++)
        {    
            for(int k = 0; k < width; k++)
            {    
                int pix = im.getRGB(k, l);
                rComp = (pix >> 16) & 0xff;
                gComp = (pix >> 8) & 0xff;
                bComp = (pix >> 0) & 0xff;
                
                gray = (int)((0.21 * rComp) + (0.71 * gComp) + (0.07 * bComp));
                
                if(gray > 0)
                {
                    inside++;
                    
                    if(k < xMin)
                       xMin = k;
                    
                    if(k > xMax)
                       xMax = k;
                    
                    if(l < yMin)
                        yMin = l;
                    
                    if(l > yMax)
                        yMax = l;
                }
            }
        }    
        if(inside >= 1)
        {
            boxCoords[0] = xMin;    boxCoords[1] = xMax;
            boxCoords[2] = yMin;    boxCoords[3] = yMax;
        }
        else
        {
            boxCoords[0] = 0;    boxCoords[1] = 0;
            boxCoords[2] = 0;    boxCoords[3] = 0;
        } 
            
        return boxCoords;
    }

    
    public BufferedImage drawBoundingBox(BufferedImage im, int[] boxCoords)
    {
        BufferedImage im1  = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        boxCoords[0] = boxCoords[0] - 5;
        boxCoords[1] = boxCoords[1] + 5;
        boxCoords[2] = boxCoords[2] - 5;
        boxCoords[3] = boxCoords[3] + 5;

        for(int l = 0; l < height; l++)
            for(int k = 0; k <  width; k++)                	
                im1.setRGB(k, l, im.getRGB(k, l));
        
        for(int l = boxCoords[2]; l <= boxCoords[3]; l++)
        {
            for(int k = boxCoords[0]; k <= boxCoords[1]; k++)
            {
                if(((k >= boxCoords[0] && k <= (boxCoords[0] + 5)) || (k >= (boxCoords[1] - 5) && k <= boxCoords[1])) || 
                        ((l >= boxCoords[2] && l <= (boxCoords[2] + 5)) || (l >= (boxCoords[3] - 5) && l <= boxCoords[3])))
                    im1.setRGB(k, l, 0xffff0000);
            }
        }
        
        return im1;
    }

}
