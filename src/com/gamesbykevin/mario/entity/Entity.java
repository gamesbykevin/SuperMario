package com.gamesbykevin.mario.entity;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.world.level.tiles.Tile;
import com.gamesbykevin.mario.world.level.tiles.Tiles;

/**
 * Every object in the game is an entity
 * @author GOD
 */
public abstract class Entity extends Sprite implements Disposable
{
    //default time delay
    protected static final long DEFAULT_DELAY = Timers.toNanoSeconds(250L);
    
    //the speed at which we will jump-fall
    private double jumpVelocity;
    
    //the rate at which we slow down the y-velocity
    public static final double VELOCITY_DECREASE = .25;
    
    //is this entity jumping/falling
    private boolean jump = false;
    
    /**
     * No velocity
     */
    protected static final int SPEED_NONE = 0;
    
    //determine when entity is dead (if applicable)
    private boolean dead = false;
    
    protected Entity()
    {
        //create sprite sheet
        super.createSpriteSheet();
    }
    
    protected double getNorthX()
    {
        return (getX() + (getWidth() / 2));
    }
    
    protected double getNorthY()
    {
        return (getY());
    }
    
    protected double getSouthX()
    {
        return (getX() + (getWidth() / 2));
    }
    
    protected double getSouthY()
    {
        return (getY() + getHeight());
    }
    
    protected double getWestX()
    {
        return (getX());
    }
    
    protected double getWestY()
    {
        return (getY() + (getHeight() / 2));
    }
    
    protected double getNorthWestX()
    {
        return (getX());
    }
    
    protected double getNorthWestY()
    {
        return (getY() + (getHeight() / 4));
    }
    
    protected double getEastX()
    {
        return (getX() + getWidth());
    }
    
    protected double getEastY()
    {
        return (getY() + (getHeight() / 2));
    }
    
    protected double getNorthEastX()
    {
        return (getX() + getWidth());
    }
    
    protected double getNorthEastY()
    {
        return (getY() + (getHeight() / 4));
    }
    
    /**
     * Add animation.<br>
     * If no animations are set as the current, this animation will be set by default.
     * @param key The unique identifier to access this animation
     * @param count The number of animation frames
     * @param startX The starting x-coordinate of our first frame
     * @param startY The starting x-coordinate of our first frame
     * @param w Width
     * @param h Height
     * @param delay The time delay between each frame (nano-seconds)
     * @param loop Does the animation loop once finished
     */
    public final void addAnimation(final Object key, final int count, final int startX, final int startY, final int w, final int h, final long delay, final boolean loop)
    {
        try
        {
            if (hasAnimation(key))
                throw new Exception("Animation was already added: " + key.toString());

            //create a new animation
            Animation animation = new Animation();

            //does this animation loop?
            animation.setLoop(loop);

            //load animation frames
            for (int i=0; i < count; i++)
            {
                animation.add(startX + (i * w), startY, w, h, delay);
            }

            //add animation to our sprite sheet
            super.getSpriteSheet().add(animation, key);
            
            //if the current animation isn't set, set this one by default
            if (super.getSpriteSheet().getCurrent() == null)
                super.getSpriteSheet().setCurrent(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Reset the current animation
     */
    public void resetAnimation()
    {
        super.getSpriteSheet().reset();
    }
    
    /**
     * Determine if the specified parameter is the current animation
     * @param object The animation we are checking for
     * @return true if the parameter object is the current animation, false otherwise
     */
    protected final boolean isAnimation(final Object object)
    {
        return (super.getSpriteSheet().getCurrent() == object);
    }
    
    /**
     * Do we have the specified animation
     * @param object The animation we are looking for
     * @return true if animation is found, false otherwise
     */
    protected final boolean hasAnimation(final Object object)
    {
        return (super.getSpriteSheet().getSpriteSheetAnimation(object) != null);
    }
    
    /**
     * Set the current animation and reset it to start
     * @param key The animation we want to set
     * @param reset Do we reset the animation
     */
    public void setAnimation(final Object key, final boolean reset)
    {
        try
        {
            if (!hasAnimation(key))
                throw new Exception("Animation does not exist: " + key.toString());
            
            //set the current animation
            getSpriteSheet().setCurrent(key);
            
            if (reset)
                resetAnimation();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Update current animation
     * @param time The time to deduct from the current animation (nano-seconds)
     */
    public void update(final long time)
    {
        try
        {
            super.getSpriteSheet().update(time);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Get the current animation key
     * @return The animation key
     */
    public Object getCurrentAnimation()
    {
        return super.getSpriteSheet().getCurrent();
    }
    
    public double getJumpVelocity()
    {
        return this.jumpVelocity;
    }
    
    public void setJumpVelocity(final double jumpVelocity)
    {
        this.jumpVelocity = jumpVelocity;
    }
    
    /**
     * Is jumping or falling
     * @return true if jumping or falling, false otherwise
     */
    public boolean isJumping()
    {
        return this.jump;
    }
    
    /**
     * Set jump to true and set y-velocity to the value jumpVelocity
     */
    public void startJump()
    {
        setJump(true);
        setVelocityY(-getJumpVelocity());
    }
    
    public void stopJump()
    {
        setJump(false);
        resetVelocityY();
    }
    
    /**
     * Check if the entity should be falling.<br>
     * If there are no tiles below gravity will be applied
     * @param tiles The object containing all tiles in the level
     */
    public void applyGravity(final Tiles tiles)
    {
        //manage jumping here
        if (isJumping())
        {
            //change velocity
            setVelocityY(super.getVelocityY() + VELOCITY_DECREASE);
            
            //limit the fall rate
            if (getVelocityY() > getJumpVelocity())
                setVelocityY(getJumpVelocity());
            
            try
            {
                if (getJumpVelocity() == 0)
                    throw new Exception("Gravity can't be applied because the jump velocity is not set.");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            //store original location for now
            final double y = super.getY();

            //apply gravity temporary to check below tiles
            super.setY(y + VELOCITY_DECREASE);

            //get the tile below
            Tile tile = tiles.getTile(getSouthX(), getSouthY());
            
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
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    public Tile checkCollisionWest(final Tiles tiles)
    {
        Tile tile = tiles.getTile(getWestX(), getWestY());
        
        //if there is a tile, then there is collision
        if (tile != null && tile.isSolid())
        {
            //correct coordinate
            setX(tile.getX() + tile.getWidth());
            
            //return collision tile
            return tile;
        }
        
        return null;
    }
    
    /**
     * Check to see if the east side of this object intersects with the specified entity
     * @param entity The entity we want to check
     * @return true if the east side of this object collides with the parameter entity, false otherwise
     */
    public boolean checkCollisionEast(final Entity entity)
    {
        return entity.getRectangle().contains(getEastX(), getEastY());
    }
    
    /**
     * Check to see if the west side of this object intersects with the specified entity
     * @param entity The entity we want to check
     * @return true if the west side of this object collides with the parameter entity, false otherwise
     */
    public boolean checkCollisionWest(final Entity entity)
    {
        return entity.getRectangle().contains(getWestX(), getWestY());
    }
    
    /**
     * Check to see if the north side of this object intersects with the specified entity
     * @param entity The entity we want to check
     * @return true if the north side of this object collides with the parameter entity, false otherwise
     */
    public boolean checkCollisionNorth(final Entity entity)
    {
        return entity.getRectangle().contains(getNorthX(), getNorthY());
    }
    
    /**
     * Check for collision at north-west, north, and north-east
     * @param entity The entity we want to check
     * @return true if the north side of this object collides with the parameter entity, false otherwise
     */
    public boolean checkCollisionNorthAny(final Entity entity)
    {
        if (checkCollisionNorth(entity))
            return true;
        if (checkCollisionNorthWest(entity))
            return true;
        if (checkCollisionNorthEast(entity))
            return true;
        
        return false;
    }
    
    /**
     * Check to see if the south side of this object intersects with the specified entity
     * @param entity The entity we want to check
     * @return true if the south side of this object collides with the parameter entity, false otherwise
     */
    public boolean checkCollisionSouth(final Entity entity)
    {
        return entity.getRectangle().contains(getSouthX(), getSouthY());
    }
    
    /**
     * Check to see if the north-west side of this object intersects with the specified entity
     * @param entity The entity we want to check
     * @return true if the north-west side of this object collides with the parameter entity, false otherwise
     */
    public boolean checkCollisionNorthWest(final Entity entity)
    {
        return entity.getRectangle().contains(getNorthWestX(), getNorthWestY());
    }
    
    /**
     * Check to see if the north-east side of this object intersects with the specified entity
     * @param entity The entity we want to check
     * @return true if the north-east side of this object collides with the parameter entity, false otherwise
     */
    public boolean checkCollisionNorthEast(final Entity entity)
    {
        return entity.getRectangle().contains(getNorthEastX(), getNorthEastY());
    }
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    public Tile checkCollisionEast(final Tiles tiles)
    {
        //get the tile to the east of the character
        Tile tile = tiles.getTile(getEastX(), getEastY());
        
        //if there is a tile, then there is collision
        if (tile != null && tile.isSolid())
        {
            //correct coordinate
            setX(tile.getX() - getWidth());
            
            //return collision tile
            return tile;
        }
        
        //no tile found
        return null;
    }
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    public Tile checkCollisionNorthEast(final Tiles tiles)
    {
        //get the tile to the east of the character
        Tile tile = tiles.getTile(getNorthEastX(), getNorthEastY());
        
        //if there is a tile, then there is collision
        if (tile != null && tile.isSolid())
        {
            //correct coordinate
            setX(tile.getX() - getWidth());
            
            //return collision tile
            return tile;
        }
        
        //no tile found
        return null;
    }
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    public Tile checkCollisionNorth(final Tiles tiles)
    {
        final int range = (int)(getWidth() / 4);
        
        for (int x = -range; x <= range; x++)
        {
            //get the tile above the character's head
            Tile tile = tiles.getTile(getNorthX() + x, getNorthY());

            //if there is a tile, then there is collision
            if (tile != null && tile.isSolid())
            {
                //place character right below tile
                setY(tile.getY() + tile.getHeight());

                //return collision tile
                return tile;
            }
        }
        
        //no tile found
        return null;
    }
    
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    public Tile checkCollisionNorthWest(final Tiles tiles)
    {
        //get the tile to the west of the character
        Tile tile = tiles.getTile(getNorthWestX(), getNorthWestY());
        
        //if there is a tile, then there is collision
        if (tile != null && tile.isSolid())
        {
            //correct coordinate
            setX(tile.getX() + tile.getWidth());
            
            //return collision tile
            return tile;
        }
        
        //no tile found
        return null;
    }
    
    /**
     * Check for collision with the level tiles
     * @param tiles The object containing all tiles in the level
     * @return The tile the character has collision with, if no collision null is returned
     */
    public Tile checkCollisionSouth(final Tiles tiles)
    {
        final int range = (int)(getWidth() / 4);
        
        for (int x = -range; x <= range; x++)
        {
            //get the tile below the character's feet
            Tile tile = tiles.getTile(getSouthX() + x, getSouthY());

            //if there is a tile, then there is collision
            if (tile != null && tile.isSolid())
            {
                //place character right above tile
                setY(tile.getY() - super.getHeight());

                //return collision tile
                return tile;
            }
        }
        
        //no tile found
        return null;
    }
    
    public void setJump(final boolean jump)
    {
        this.jump = jump;
    }
    
    /**
     * Does this entity collide with another?
     * @param entity Entity we want to check for collision
     * @return true if these objects touch each other, false otherwise
     */
    public final boolean hasCollision(final Entity entity)
    {
        return super.getRectangle().intersects(entity.getRectangle());
    }
    
    /**
     * Is this the current animation
     * @param object The animation we are looking for
     * @return true if this is the current animation set, false otherwise
     */
    public final boolean isCurrentAnimation(final Object object)
    {
        return (super.getSpriteSheet().getCurrent() == object);
    }
    
    public void markDead()
    {
        this.dead = true;
    }
    
    public boolean isDead()
    {
        return this.dead;
    }
    
    public void setDead(final boolean dead)
    {
        this.dead = dead;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}