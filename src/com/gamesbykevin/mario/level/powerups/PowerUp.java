package com.gamesbykevin.mario.level.powerups;

import com.gamesbykevin.mario.entity.Entity;

import com.gamesbykevin.mario.level.powerups.PowerUps.Type;
import com.gamesbykevin.mario.level.tiles.Tile;

public final class PowerUp extends Entity
{
    private final Type type;
    
    protected PowerUp(final Type type)
    {
        this.type = type;
        
        super.setDimensions(Tile.WIDTH, Tile.HEIGHT);
    }
    
    protected Type getType()
    {
        return this.type;
    }
}