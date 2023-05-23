package core.clients.mule.job;

import types.game.Tile;

public class MuleLocation
{
    private final Tile primaryTile;
    private final Tile muleTile;
    
    public MuleLocation(Tile primaryTile, Tile muleTile)
    {
        this.primaryTile = primaryTile;
        this.muleTile = muleTile;
    }
    
    public Tile getPrimaryTile()
    {
        return primaryTile;
    }
    
    public Tile getMuleTile()
    {
        return muleTile;
    }
}
