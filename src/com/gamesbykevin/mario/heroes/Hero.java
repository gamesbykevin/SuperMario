package com.gamesbykevin.mario.heroes;

import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.powerups.PowerUps;
import com.gamesbykevin.mario.level.tiles.Tile;
import com.gamesbykevin.mario.shared.IElement;

import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Hero extends Character implements IElement 
{
    //by default the hero isn't big
    private boolean big = false;
    
    //by default the hero doesn't have fire
    private boolean fire = false;
    
    //store the previous jump state
    private boolean jumping = false;
    
    private static final double SCROLL_WEST_RATIO = .25;
    private static final double SCROLL_EAST_RATIO = .65;
    
    /**
     * The different animations for the hero
     */
    public enum State
    {
        //all of the different things we can do while small
        SmallMiniMap, SmallIdle, SmallWalk, SmallRun, SmallDeath, SmallJump,  
        
        //the things we can do while big
        BigMiniMap, BigIdle, BigWalk, BigRun, BigJump, BigDuck, 
        
        //the things we can do while big fire
        FireMiniMap, FireIdle, FireWalk, FireRun, FireJump, FireDuck, FireAttack,
        
        //the fireball animation
        Fireball
    }
    
    protected Hero()
    {
        super(Character.DEFAULT_JUMP_VELOCITY, Character.DEFAULT_SPEED_WALK, Character.DEFAULT_SPEED_RUN);
    }
    
    public State getDefaultAnimation()
    {
        if (hasFire())
        {
            return State.FireIdle;
        }
        else
        {
            if (isBig())
            {
                return State.BigIdle;
            }
            else
            {
                return State.SmallIdle;
            }
        }
    }
    
    public void setAnimationAttack()
    {
        if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireAttack, false);
        }
    }
    
    public void setAnimationDuck()
    {
        if (!isBig())
        {
            //there is not animation for small so do nothing
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireDuck, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigDuck, false);
        }
    }
    
    public void setAnimationJump()
    {
        if (!isBig())
        {
            //we are not big so set small
            setAnimation(State.SmallJump, false);
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireJump, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigJump, false);
        }
    }
    
    public void setAnimationRun()
    {
        if (!isBig())
        {
            //we are not big so set small
            setAnimation(State.SmallRun, false);
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireRun, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigRun, false);
        }
    }
    
    public void setAnimationWalk()
    {
        if (!isBig())
        {
            //we are not big so set small
            setAnimation(State.SmallWalk, false);
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireWalk, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigWalk, false);
        }
    }
    
    public void setAnimationIdle()
    {
        if (!isBig())
        {
            //we are not big so set small
            setAnimation(State.SmallIdle, false);
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireIdle, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigIdle, false);
        }
    }
    
    public void setAnimationMiniMap()
    {
        if (!isBig())
        {
            //we are not big so set small
            setAnimation(State.SmallMiniMap, false);
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireMiniMap, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigMiniMap, false);
        }
    }
    
    public void setBig(final boolean big)
    {
        this.big = big;
    }
    
    public boolean isBig()
    {
        return this.big;
    }
    
    public void setFire(final boolean fire)
    {
        this.fire = fire;
    }
    
    public boolean hasFire()
    {
        return this.fire;
    }
    
    @Override
    public void update(final Engine engine)
    {
        //update parent entity
        super.update(engine.getMain().getTime());
        
        //update location
        super.update();

        //are we currently jumping/falling
        this.jumping = super.isJumping();
        
        //apply gravity
        super.applyGravity(engine.getManager().getLevel().getTiles());
        
        //check for collision to the north first
        Tile n = checkCollisionNorth(engine.getManager().getLevel().getTiles());
        
        //no collision found north, now check the rest
        if (n == null)
        {
            Tile west       = checkCollisionWest(engine.getManager().getLevel().getTiles());
            Tile northWest  = checkCollisionNorthWest(engine.getManager().getLevel().getTiles());
            Tile east       = checkCollisionEast(engine.getManager().getLevel().getTiles());
            Tile northEast  = checkCollisionNorthEast(engine.getManager().getLevel().getTiles());
            Tile south      = checkCollisionSouth(engine.getManager().getLevel().getTiles());
        }
        
        //manage power up collision
        managePowerUp(engine.getManager().getLevel().getPowerUpCollision(this));
        
        //make sure correct animation is set
        checkAnimation();
        
        //check if we are to scroll the level
        checkScroll(engine.getManager().getLevel(), engine.getManager().getWindow());
    }
        
    private void managePowerUp(final PowerUps.Type type)
    {
        try
        {
            if (type == null)
                return;

            switch (type)
            {
                case Mushroom:
                    setBig(true);
                    setAnimation(getDefaultAnimation(), false);
                    break;
                    
                case Flower:
                    setBig(true);
                    setFire(true);
                    setAnimation(getDefaultAnimation(), false);
                    break;
                    
                case Star:
                    System.out.println("Collected: " + type.toString());
                    setAnimation(getDefaultAnimation(), false);
                    break;
                    
                case Coin:
                    System.out.println("Collected: " + type.toString());
                    break;
                    
                case CoinSwitch:
                    System.out.println("Collected: " + type.toString());
                    break;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Determine if we need to scroll the level tiles/background
     * @param level 
     */
    private void checkScroll(final Level level, final Rectangle window)
    {
        final double westX  = window.x + (window.width * SCROLL_WEST_RATIO);
        final double eastX  = window.x + (window.width * SCROLL_EAST_RATIO);
        
        //determine where we want to scroll (if any)
        final boolean scrollWest = (getX() < westX) && (getVelocityX() < 0);
        final boolean scrollEast = (getX() > eastX) && (getVelocityX() > 0);
        
        if (scrollWest)
        {
            setX(westX);
            level.setScrollX((level.getTiles().canScrollWest(window.x)) ? -getVelocityX() : Character.SPEED_NONE);
        }
        
        if (scrollEast)
        {
            setX(eastX);
            level.setScrollX((level.getTiles().canScrollEast(window.x + window.width - Tile.WIDTH)) ? -getVelocityX() : Character.SPEED_NONE);
        }
        
        if (!hasVelocityX() || isIdle())
            level.setScrollX(Character.SPEED_NONE);
    }
    
    /**
     * Set the correct animation
     */
    @Override
    protected void checkAnimation()
    {
        //default animation is idle
        setAnimationIdle();
        
        if (isDucking())
            setAnimationDuck();
        if (isWalking())
            setAnimationWalk();
        if (isRunning())
            setAnimationRun();
        if (isJumping())
            setAnimationJump();
        if (isAttacking())
            setAnimationAttack();
        
        //if we were previosly jumping and are no longer, set correct animation
        if (jumping && !super.isJumping())
        {
            if (isWalking())
            {
                setAnimationWalk();
            }
            else if (isRunning())
            {
                setAnimationRun();
            }
            else
            {
                setAnimationIdle();
            }
        }
        else
        {
            //if we weren't jumping and are now, set correct animation
            if (!jumping && super.isJumping())
            {
                setAnimationJump();
            }
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        super.draw(graphics);
    }
}