package com.gamesbykevin.mario.level.powerups;

import com.gamesbykevin.mario.entity.Entity;

import com.gamesbykevin.mario.level.powerups.PowerUps.Type;
import com.gamesbykevin.mario.level.tiles.Tile;

public final class PowerUp extends Entity
{
    //the type of power up
    private final Type type;
    
    //the fist place the power up needs to go
    private final double destinationY;
    
    //have we reached our destination yet
    private boolean destination = false;
    
    //the power ups have to rise first when the block is hit
    private static final int DEFAULT_SPEED_VELOCITY_Y = 1;
    
    //speed to move left or right
    protected static final double DEFAULT_SPEED_VELOCITY_X = .5;
    
    //for the star to jump while moving
    protected static final double DEFAULT_JUMP_VELOCITY = 5;
    
    protected PowerUp(final Type type, final double destinationY)
    {
        //set the type
        this.type = type;
        
        //default the velocity
        super.setVelocityY(-DEFAULT_SPEED_VELOCITY_Y);
        
        //set where we need to head
        this.destinationY = destinationY;
        
        //set default dimensions
        super.setDimensions(Tile.WIDTH, Tile.HEIGHT);
    }
    
    protected void flagDestination()
    {
        this.destination = true;
    }
    
    protected boolean hasDestination()
    {
        return this.destination;
    }
    
    protected double getDestinationY()
    {
        return this.destinationY;
    }
    
    protected Type getType()
    {
        return this.type;
    }
}