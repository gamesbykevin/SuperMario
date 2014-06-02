package com.gamesbykevin.mario.level.tiles;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.level.tiles.Tiles.Type;

public final class Tile extends Entity implements Disposable
{
    //what type of tile is this
    private Type type;
    
    //will collision with this tile cause damage
    private boolean damage = false;
    
    //do we check this tile for collision
    private boolean solid = false;
    
    public static final int WIDTH = 16;
    public static final int HEIGHT = 16;
    
    public Tile(final Type type)
    {
        super.setDimensions(WIDTH, HEIGHT);
        
        this.type = type;
    }
    
    public Type getType()
    {
        return this.type;
    }
    
    protected void setDamage(final boolean damage)
    {
        this.damage = damage;
    }
    
    public boolean hasDamage()
    {
        return this.damage;
    }
    
    protected void setSolid(final boolean solid)
    {
        this.solid = solid;
    }
    
    public boolean isSolid()
    {
        return this.solid;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}