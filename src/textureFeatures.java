/**
 * @author hmehra@usc.edu
 *		   bijani@usc.edu	
 */

import java.awt.image.BufferedImage;

public class textureFeatures {
	
	public int[][] calculateGCLM(BufferedImage img,
								 int x_start,int x_end, 
								 int y_start, int y_end, 
								 int nLevels,boolean flag) {
		
		int size = (int) Math.pow(2, nLevels);
		int factor = (int) (256 - Math.pow(2, nLevels));
		int[][] GCLM = new int[size][size];
		
		if(flag) {
			for(int y=y_start;y<y_end;y++)				// Compute GCLM using east neighbor
				for(int x=x_start;x<x_end-1;x++) 			
					GCLM[img.getRGB(x, y)/factor][img.getRGB(x+1, y)/factor]++;	
			
		} 
		
		else {
			for(int y=y_start;y<y_end-1;y++) 			    // Compute GCLM using south neighbor
				for(int x=x_start;x<x_end;x++) 				
					GCLM[img.getRGB(x, y)/factor][img.getRGB(x, y+1)/factor]++;	
		}		
		return GCLM;
	}
	
	public float[][] calculateNGCLM(int[][] GCLM, int nLevels) {
		int sum = 0;
		int range = (int) Math.pow(2, nLevels);
		float[][] NGCLM = new float[range][range];
		
		for(int i=0;i<range;i++)
			for(int j=0;j<range;j++)
				sum += GCLM[i][j];
		
		for(int i=0;i<range;i++)
			for(int j=0;j<range;j++)
				NGCLM[i][j] = GCLM[i][j]/sum;
		
		return NGCLM;
	}
	
	public float calculateASM(float[][] NGCLM, int nLevels) {
		float asm=0;
		int range = (int) Math.pow(2, nLevels);
		for(int i=0;i<range;i++)
			for(int j=0;j<range;j++)
				asm += NGCLM[i][j]*NGCLM[i][j];
		
		return asm;
	}
	
	public float calculateContrast(float[][] NGCLM, int nLevels) {
		float contrast=0;
		int range = (int) Math.pow(2, nLevels);
		
		for(int i=0;i<range;i++)
			for(int j=0;j<range;j++)
				contrast += Math.pow((i-j), 2)*NGCLM[i][j]; 
		
		return contrast;
	}
	
	public float calculateEntropy(float[][] NGCLM, int nLevels) {
		float entropy=0;
		int range = (int) Math.pow(2, nLevels);
		
		for(int i=0;i<range;i++)
			for(int j=0;j<range;j++)
			{
				if(NGCLM[i][j]==0)
					continue;
				
				entropy += NGCLM[i][j]*Math.log(NGCLM[i][j]);
			}	
		
		return (-1*entropy);
	}
	
	public float calculateCorrelation(float[][] GCLM, int nLevels) {
		float correlation = 0;
		int range = (int) Math.pow(2, nLevels);
		
		float muI, muJ, sigmaX,sigmaY;
		float sum = 0;
		
		for(int i=0;i<range;i++)	
			for(int j=0;j<range;j++) 
				sum += i*GCLM[i][j];
		muI = sum;
		sum=0;
		
		for(int i=0;i<range;i++)
			for(int j=0;j<range;j++) 
				sum += j*GCLM[i][j]; 
		muJ = sum;
		sum=0;
		
		for(int i=0;i<range;i++)
			for(int j=0;j<range;j++) 
				sum += GCLM[i][j]*(i-muI)*(i-muI);
		
		sigmaX = (float) Math.sqrt(sum);
		sum=0;

		for(int i=0;i<range;i++)
			for(int j=0;j<range;j++)
				sum += GCLM[i][j]*(j-muJ)*(j-muJ);
		sigmaY = (float) Math.sqrt(sum); 
		
		for(int i=0;i<range;i++)
			for(int j=0;j<range;j++)
				correlation += GCLM[i][j]*(i-muI)*(j-muJ) / (sigmaX*sigmaY);
		
		return correlation;
	}
	
}
