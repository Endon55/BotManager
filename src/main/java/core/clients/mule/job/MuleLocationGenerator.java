package core.clients.mule.job;

import types.Pair;
import types.game.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MuleLocationGenerator
{
    //We need 2 adjacent tiles to execute the trade, we could
    private static final List<Pair> locations = new ArrayList<>(Arrays.asList(
            new Pair(new Tile(3165, 3487, 0), new Tile(3164, 3487, 0)),   //GE south of center column
            new Pair(new Tile(3162, 3489, 0), new Tile(3162, 3490, 0)),   //GE west of center column
            new Pair(new Tile(3167, 3489, 0), new Tile(3167, 3490, 0)))); //GE east of center column
    
    private static final Random random = new Random();
    
    public static MuleLocation getMuleLocation()
    {
        Pair location = locations.get(random.nextInt(locations.size()));
        int num = random.nextInt(2);
        if(num == 0)
        {
            return new MuleLocation((Tile) location.getFirst(), (Tile) location.getSecond());
        }
        else
        {
            return new MuleLocation((Tile) location.getSecond(), (Tile) location.getFirst());
        }
    }
}
