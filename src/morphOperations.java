/**
 *
 * @author Lohit
 */
public class morphOperations {

    private int width, height;
    
    public morphOperations(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
    
    
    public int[][] impulseRemove(int[][] map)
    {
        int[][] map_noImpulse = new int[width][height];
        
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                map_noImpulse[x][y] = 0;
        
        for(int y = 1; y < (height - 1); y++)
        {
            for(int x = 1; x < (width - 1); x++)
            {
                if(map[x][y] == 1)
                {
                    int sum = 0;
                    for(int l = (y-1); l <= (y+1); l++)
                        for(int k = (x-1); k <= (x+1); k++)
                            sum += map[k][l];
                    
                    if(sum == 1)
                        map_noImpulse[x][y] = 0;
                    else
                        map_noImpulse[x][y] = 1;
                }
            }                
        }        
        return map_noImpulse;
    }
    
    public int[][] spurRemove(int[][] map)
    {
        int[][] map_noSpur = new int[width][height];
        
        for(int y = 0; y < height; y++)
            for(int x = 0; x < width; x++)
                map_noSpur[x][y] = 0;
        
        for(int y = 1; y < (height - 1); y++)
        {
            for(int x = 1; x < (width - 1); x++)
            {
                if(map[x][y] == 1)
                {
                    int sum = 0;
                    for(int l = (y-1); l <= (y+1); l++)
                        for(int k = (x-1); k <= (x+1); k++)
                            sum += map[k][l];
                    
                    if(sum == 2)
                        map_noSpur[x][y] = 0;
                    else
                        map_noSpur[x][y] = 1;
                }
            }                
        }
        
        return map_noSpur;
    }
    
    public int[][] interiorFill(int[][] map)
    {
        int[][] map_fill = new int[width][height];
        
        
        return map_fill;
    }
}
