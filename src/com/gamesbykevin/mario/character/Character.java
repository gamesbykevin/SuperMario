package com.gamesbykevin.mario.character;

import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.level.tiles.Tile;
import com.gamesbykevin.mario.level.tiles.Tiles;

public abstract class Character extends Entity
{
    //different actions for character
    private boolean attack = false, duck = false, jump = false, run = false, walk = false, idle = true;
    
    //when we first jump the rate which we move
    public static final double DEFAULT_JUMP_VELOCITY = 6;
    
    //the speed at which we will jump
    private final double jumpVelocity;
    
    //the rate at which we slow down the y-velocity
    private static final double VELOCITY_DECREASE = .25;
    
    //the speed which the character can walk/run
    public static final int DEFAULT_SPEED_WALK = 1;
    public static final int DEFAULT_SPEED_RUN = 2;
    protected static final int SPEED_NONE = 0;
    
    private final double speedWalk;
    private final double speedRun;
    
    protected Character(final double jumpVelocity, final double speedWalk, final double speedRun)
    {
        this.jumpVelocity = jumpVelocity;
        this.speedWalk = speedWalk;
        this.speedRun = speedRun;
    }
    
    public double getSpeedWalk()
    {
        return this.speedWalk;
    }
    
    public double getSpeedRun()
    {
        return this.speedRun;
    }
    
    public double getJumpVelocity()
    {
        return this.jumpVelocity;
    }
    
    public void startJump()
    {
        setVelocityY(-getJumpVelocity());
    }
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    protected Tile checkCollisionNorthWest(final Tiles tiles)
    {
        //get the tile to the west of the character
        Tile tile = tiles.getTile(super.getNorthWestX(), super.getNorthWestY());
        
        //if there is a tile, then there is collision
        if (tile != null && tile.isSolid())
        {
            setX(tile.getX() + tile.getWidth());
            
            if (getVelocityX() < 0)
                setVelocityX(SPEED_NONE);
            
            //return collision tile
            return tile;
        }
        
        return null;
    }
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    protected Tile checkCollisionWest(final Tiles tiles)
    {
        Tile tile = tiles.getTile(super.getWestX(), super.getWestY());
        
        //if there is a tile, then there is collision
        if (tile != null && tile.isSolid())
        {
            setX(tile.getX() + tile.getWidth());
            
            if (getVelocityX() < 0)
                setVelocityX(SPEED_NONE);
            
            //return collision tile
            return tile;
        }
        
        return null;
    }
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    protected Tile checkCollisionEast(final Tiles tiles)
    {
        //get the tile to the east of the character
        Tile tile = tiles.getTile(super.getEastX(), super.getEastY());
        
        //if there is a tile, then there is collision
        if (tile != null && tile.isSolid())
        {
            setX(tile.getX() - getWidth());
            
            if (getVelocityX() > 0)
                setVelocityX(SPEED_NONE);
            
            //return collision tile
            return tile;
        }
        
        return null;
    }
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    protected Tile checkCollisionNorthEast(final Tiles tiles)
    {
        //get the tile to the east of the character
        Tile tile = tiles.getTile(super.getNorthEastX(), super.getNorthEastY());
        
        //if there is a tile, then there is collision
        if (tile != null && tile.isSolid())
        {
            setX(tile.getX() - getWidth());
            
            if (getVelocityX() > 0)
                setVelocityX(SPEED_NONE);
            
            //return collision tile
            return tile;
        }
        
        return null;
    }
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    protected Tile checkCollisionNorth(final Tiles tiles)
    {
        //get the tile above the character's head
        Tile tile = tiles.getTile(super.getNorthX(), super.getNorthY());
        
        //if there is a tile, then there is collision
        if (tile != null && tile.isSolid())
        {
            //place character right below tile
            setY(tile.getY() + tile.getHeight());
            
            //if we are jumping, start falling down
            if (isJumping())
                setVelocityY(VELOCITY_DECREASE);
            
            //return collision tile
            return tile;
        }
        
        return null;
    }
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    protected Tile checkCollisionSouth(final Tiles tiles)
    {
        //get the tile below the character's feet
        Tile tile = tiles.getTile(super.getSouthX(), super.getSouthY());
        
        //if there is a tile, then there is collision
        if (tile != null && tile.isSolid())
        {
            //if we are falling/jumping then stop
            if (isJumping())
                stopJumping();
            
            //place character right above tile
            setY(tile.getY() - super.getHeight());
            
            //return collision tile
            return tile;
        }
        
        return null;
    }
    
    /**
     * Apply gravity to character and check for collision while falling
     * @param tiles The object containing all tiles in the level
     */
    protected void applyGravity(final Tiles tiles)
    {
        //manage jumping here
        if (isJumping())
        {
            //increase velocity
            setVelocityY(super.getVelocityY() + VELOCITY_DECREASE);
            
            //limit the fall rate
            if (getVelocityY() > getJumpVelocity())
                setVelocityY(getJumpVelocity());
        }
        else
        {
            //store original location for now
            final double y = super.getY();

            //apply gravity temporary to check below tiles
            super.setY(y + VELOCITY_DECREASE);

            Tile tile = tiles.getTile(super.getSouthX(), super.getSouthY());
            
            //if there is no tile below or not solid
            if (tile == null || !tile.isSolid())
            {
                //apply gravity
                setJump(true);
                setVelocityY(VELOCITY_DECREASE);
            }

            //restore y-coordinate
            super.setY(y);
        }
    }
    
    public void stopJumping()
    {
        //stop jumping
        setJump(false);
        
        if (isWalking())
        {
            setWalk(true);
        }
        else if (isRunning())
        {
            setRun(true);
        }
        else
        {
            setIdle(true);
            resetVelocity();
        }
    }
    
    public boolean isAttacking()
    {
        return this.attack;
    }
    
    public void setAttack(final boolean attack)
    {
        this.attack = attack;
        
        //if attacking we can't do these
        if (attack)
        {
            setDuck(false);
            setIdle(false);
        }
    }
    
    public boolean isDucking()
    {
        return this.duck;
    }
    
    public void setDuck(final boolean duck)
    {
        this.duck = duck;
        
        //if ducking we can't do these
        if (duck)
        {
            setAttack(false);
            setJump(false);
            setRun(false);
            setIdle(false);
        }
    }
    
    public boolean canDuck()
    {
        return (isWalking() || isRunning() || isIdle()) && !isJumping();
    }
    
    public boolean isJumping()
    {
        return this.jump;
    }
    
    public void setJump(final boolean jump)
    {
        this.jump = jump;
        
        //if jumping we can't do these
        if (jump)
        {
            setDuck(false);
            setIdle(false);
        }
    }
    
    public boolean canJump()
    {
        return (isIdle() || isWalking() || isRunning()) && !isJumping();
    }
    
    public boolean isRunning()
    {
        return this.run;
    }
    
    public void setRun(final boolean run)
    {
        this.run = run;
        
        //if running we can't do these
        if (run)
        {
            setAttack(false);
            setDuck(false);
            setIdle(false);
            setWalk(false);
        }
    }
    
    public boolean canRun()
    {
        return (isWalking() && !isJumping());
    }
    
    public boolean isWalking()
    {
        return this.walk;
    }
    
    public void setWalk(final boolean walk)
    {
        this.walk = walk;
        
        //if walking we can't do these
        if (walk)
        {
            setAttack(false);
            setDuck(false);
            setIdle(false);
            setRun(false);
        }
    }
    
    public boolean canWalk()
    {
        return (isIdle());
    }
    
    public boolean isIdle()
    {
        return this.idle;
    }
    
    public void setIdle(final boolean idle)
    {
        this.idle = idle;
        
        //if idle we can't do these
        if (idle)
        {
            setAttack(false);
            setDuck(false);
            setJump(false);
            setRun(false);
            setWalk(false);
        }
    }
    
    /**
     * Each character will need to manage their current animation
     */
    protected abstract void checkAnimation();
}