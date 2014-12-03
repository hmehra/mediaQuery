/**
 * CS576 Multimedia Systems Design
 * Image Based Searching
 * @author bijani@usc.edu
 * @author hmehra@usc.edu
 * 	 
 **/

import java.awt.image.*;

public class HsvApproach {
	
	ImageIO I;
	BufferedImage inputImg, outputImg,searchImg,blankOut,blankIn,finalImg;
	BufferedImage medFiltImg;
	colorFeatures F;
	float[][][] hsvImage;
	float[] hue_Hist;
	float[] hue_Hist1;
	int[][] alpha;
	int[] boxCoords;
	imageOperations iO,iO1;
	static int width,height;
	String alphaPath;
	
	public HsvApproach()
	{
		inputImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
		outputImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
		medFiltImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
		searchImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
		blankOut = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
		blankIn = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
		finalImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
		alphaPath = "Magic";
	}
	
    public BufferedImage returnQueryImage(String queryPath) {
    	I = new ImageIO(inputImg);
    	inputImg = I.readColorImage(queryPath);
    	blankOut = I.readColorImage(queryPath);
    	return inputImg;
    }
    
    public BufferedImage returncmpImage(String cmpPath) {
    	
    	I = new ImageIO(outputImg);             
        outputImg = I.readColorImage(cmpPath);
        searchImg = I.readColorImage(cmpPath);
        blankIn = I.readColorImage(cmpPath);
        return outputImg;
    }
    
    public void setAlphaPath(String alpha) {
    	alphaPath.replace("Magic", alpha);
    }
    
    public void compute(String alphaFilePath) {
        int k, l, x, y;
        int x_start, x_end, y_start, y_end;
        float mse = 0;
        
        width = inputImg.getWidth();
        height = inputImg.getHeight();
        
        hsvImage = new float[width][height][3];
        hue_Hist = new float[360];  
        hue_Hist1 = new float[360];
        alpha = new int[width][height];
        boxCoords = new int[4];
                
        F = new colorFeatures(inputImg, blankOut); 	
        F.rgbToHSV();
        F.hueQuantization();
    
        
        if(alphaFilePath.equals(""))
        {
        	x_start = 0; x_end = width; y_start = 0; y_end = height;
        	hue_Hist = F.quantHueHistogram(x_start, x_end, y_start, y_end);
        } else {
        
                alpha = I.readAlphaChannel(alphaFilePath);            
        	hue_Hist = F.quantHueHistogram(alpha);
        }    
        
        
        for(l = 0; l  < height; l++)//I.resetImage(outputImg);
        	for(k = 0; k < width; k++)
        		inputImg.setRGB(k, l, searchImg.getRGB(k, l));
        
        for(l = 0; l  < height; l++)//I.resetImage(outputImg);
        	for(k = 0; k < width; k++)
        		outputImg.setRGB(k, l, 0xff000000);
        
        
        F.rgbToHSV();
        F.hueQuantization();

        //Step 3: Compute histogram of Quantized Hue
            for(k = 0; k < height; k = k + 8)
            {
                for(l = 0; l < width; l = l + 8)                        
                {
                    x_start = l; x_end = (l + 8); y_start = k; y_end = (k + 8);
                    hue_Hist1 = F.quantHueHistogram(x_start, x_end, y_start, y_end);        

                    //MSE computation
                    mse = 0;
                    for(x = 0; x < 360; x++)
                        mse += ((hue_Hist[x] - hue_Hist1[x]) * (hue_Hist[x] - hue_Hist1[x]));

                    if(mse < 0.5)
                    {
                        for(y = y_start; y < y_end; y++)
                            for(x = x_start; x < x_end; x++)
                                outputImg.setRGB(x, y, inputImg.getRGB(x, y));
                    }
                }
            }

            iO = new imageOperations(outputImg);    
            medFiltImg = iO.blockMedianFilter(8);           
            
            for(l = 0; l  < height; l++)
            	for(k = 0; k < width; k++)
            		blankIn.setRGB(k, l, medFiltImg.getRGB(k, l));
            
            //I.displayImage(medFiltImg);                        
            iO1 = new imageOperations(medFiltImg);
            int map[][] = iO1.connectivityMap(8);
            int mapp[][] = new int[width/8][height/8];

            for(y = 0; y < ((height/8) - 4); y++)
            {
                for(x = 0; x < ((width/8) - 4); x++)
                {
                    if(map[x][y] == 1)
                    {
                        int sum = 0;                                
                        for(l = y; l < (y+4); l++)
                            for(k = x; k < (x+4); k++)
                                sum += map[k][l];

                        if(sum >= 4)
                            mapp[x][y] = 1;

                        else
                            mapp[x][y] = 0;

                    }
                }
            }


        for(l = 0; l < height; l++)
            for(k = 0; k < width; k++)
                medFiltImg.setRGB(k, l, 0xff000000);

        for(y = 8; y < (height - 8); y = y + 8)
        {
            for(x = 8; x < (width - 8); x = x + 8)
            {
                int p = (x/8);
                int q = (y/8);

                if(mapp[p][q] == 1)                    //original 5
                {
                    for(l = y; l < (y+8); l++)
                        for(k = x; k < (x+8); k++)
                            medFiltImg.setRGB(k, l, inputImg.getRGB(k, l));
                }        
            }
        }

        medFiltImg = iO1.postProcessing(8);                
        boxCoords = iO1.getBoundingBox(medFiltImg);
        
        for(l = 0; l  < height; l++)
        	for(k = 0; k < width; k++)
        		outputImg.setRGB(k, l, searchImg.getRGB(k, l));
        
        float boxArea = 0;
        boxArea = (boxCoords[1] - boxCoords[0]) * (boxCoords[3] - boxCoords[2]);                    
        
        if(boxArea >= (0.005 * width * height)) {
        	finalImg = iO1.drawBoundingBox(blankOut, boxCoords);
        } else {        
        	finalImg = F.rgbToGray();
        }
        
    
    }
       
  public BufferedImage returnResultImage () {	  
	  return finalImg;
  }
   
  public BufferedImage computeSecond(String queryPath,String searchPath, String alphaFilePath) {
	  
      int x_start, x_end, y_start, y_end;
      width = inputImg.getWidth();
      height = inputImg.getHeight();
      
      BufferedImage iImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
      BufferedImage oImg = new BufferedImage(352, 288, BufferedImage.TYPE_INT_RGB);
      
      hsvImage = new float[width][height][3];
      colorFeatures f1 = new colorFeatures(iImg, oImg);
      float [] hue_Hist_cS = new float[360];  
      int[][] alpha_cS = new int[width][height];
      boxCoords = new int[4];
             
      ImageIO i1 = new ImageIO(iImg);
      newApproach nA = new newApproach(iImg, oImg);
      
      iImg = i1.readColorImage(queryPath);
      nA.rgbToHSV();

      
      
	  if(alphaFilePath.equals("")) {
        	x_start = 0; x_end = width; y_start = 0; y_end = height;
        	hue_Hist_cS = nA.hueHistogram(x_start, x_end, y_start, y_end);
      } else {       
        	alpha_cS = i1.readAlphaChannel(alphaFilePath);            
        	hue_Hist_cS = nA.hueHistogram(alpha_cS);
      }
                  
      double mean = nA.meanHistogram(hue_Hist_cS, 1), dev = nA.meanHistogram(hue_Hist_cS, 2);
          
      iImg = i1.readColorImage(searchPath);                        
      nA.rgbToHSV();
      hsvImage = nA.getHSVImage();
      BufferedImage threshImage = nA.thresholdImage(mean, dev);
      BufferedImage cleanImage = nA.medianFilter(threshImage);
      BufferedImage majorityBlack = nA.majorityBlack(cleanImage);
      BufferedImage isMajorityWhite = nA.isMajorityWhite(majorityBlack);
        
      i1.displayImage(majorityBlack);
        cleanImage = nA.medianFilter(isMajorityWhite);
                
        int[] com = nA.meanLocation(cleanImage);
        
        if(com[0] > 0 && com[1] > 0)
        {
            int[] devv = nA.boxLimits(cleanImage, com);            
            oImg = i1.readColorImage(searchPath);
            nA.drawCOM(oImg, com, devv);  
        }
        else
        {
            oImg = f1.rgbToGray();
        }
        return oImg;
  }   
}
              

    
