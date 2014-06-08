package com.gamesbykevin.mario.entity;

import com.gamesbykevin.framework.base.Animation;
import com.gamesbykevin.framework.base.Sprite;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

/**
 * Every object in the game is an entity
 * @author GOD
 */
public abstract class Entity extends Sprite implements Disposable
{
    //default time delay
    protected static final long DEFAULT_DELAY = Timers.toNanoSeconds(250L);
    
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
            
            super.getSpriteSheet().setCurrent(key);
            
            if (reset)
                super.getSpriteSheet().reset();
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
    
    /**
     * Is this the current animation
     * @param object The animation we are looking for
     * @return true if this is the current animation set, false otherwise
     */
    public final boolean isCurrentAnimation(final Object object)
    {
        return (super.getSpriteSheet().getCurrent() == object);
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}