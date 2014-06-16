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
    
    //will collision with this tile cause instant death
    private boolean death = false;
    
    //do we check for collision
    private boolean solid;
    
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
    
    protected void setDeath(final boolean death)
    {
        this.death = death;
    }
    
    public boolean hasDeath()
    {
        return this.death;
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