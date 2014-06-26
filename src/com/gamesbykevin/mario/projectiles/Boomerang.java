package com.gamesbykevin.mario.projectiles;


import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.level.Level;

public final class Boomerang extends Projectile
{
    public static final int PROJECTILE_WIDTH = 16;
    public static final int PROJECTILE_HEIGHT = 16;
    
    //the speed to move
    private static final double DEFAULT_VELOCITY_X = (Character.DEFAULT_SPEED_WALK * 2.15);
    private static final double DEFAULT_VELOCITY_Y = 0.5;
    
    //the points where we change the velocity for the projectile
    private double step1X, step2Y, step3Y, step4X;
    
    //the steps we will use to control the movement of the projectile
    private boolean step1 = true, step2 = false, step3 = false, step4 = false;
    
    private enum State
    {
        Attacking
    }
    
    public Boomerang(final boolean east)
    {
        //set direction
        setVelocityX((east) ? DEFAULT_VELOCITY_X : -DEFAULT_VELOCITY_X);
        
        //the jump velocity
        super.setJumpVelocity(DEFAULT_VELOCITY_Y);
    }
    
    @Override
    protected void defineAnimations()
    {
        //add animation
        super.addAnimation(State.Attacking, 4, 64, 510, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, Timers.toNanoSeconds(125L), true);
    }
    
    @Override
    public boolean checkCharacterCollision(final Character character, final Effects effects)
    {
        //don't continue if already dead, or if the character is already hurt
        if (isDead() || character.isHurt())
            return false;
        
        //check how we should handle collision
        if (getRectangle().intersects(character.getRectangle()))
        {
            //if the character is invincible
            if (character.isInvincible())
            {
                //flag projectile dead
                markDead();
                
                //stop moving
                resetVelocity();
                
                //start falling
                startJump();
            }
            else
            {
                //is the enemy weak towards a projectile
                if (character.hasWeaknessProjectile())
                {
                    //check if character has been hurt
                    character.flagDamage();
                }
            }
        }
        
        //return false always so bullet isn't removed until off screen
        return false;
    }
    
    @Override
    protected void checkLevelCollision(final Level level)
    {
        //no level collision is required
    }
    
    @Override
    public void setup(final Character character)
    {
        //set position
        setLocation(character);
        
        //this coordinate the y-coordinate will begin to change
        step1X = getX() + (getVelocityX() * 30);
        
        //this coordinate the x-coordinate will begin to come back
        step2Y = getY() + (getHeight() * .5);
        
        //this coordinate will stop moving the y-coordinate
        step3Y = getY() + (getHeight() * 1);
                
        //this coordinate will stop moving the projectile
        step4X = getX();
                
        //face the appropriate direction
        setHorizontalFlip(!character.hasHorizontalFlip());
    }
    
    @Override
    public void updateLogic(final Level level)
    {
        //if dead apply gravity
        if (isDead())
        {
            applyGravity(level.getTiles());
            
            if (!hasVelocityY())
                applyGravity(level.getTiles());
        }
        else
        {
            //update for scrolling
            step1X += level.getScrollX();
            step4X += level.getScrollX();
            
            if (step1)
            {
                if (getVelocityX() > 0)
                {
                    //correct coordinate
                    if (getX() >= step1X)
                        setX(step1X);
                }
                else
                {
                    //correct coordinate
                    if (getX() <= step1X)
                        setX(step1X);
                }
                
                //at destination
                if (getX() == step1X)
                {
                    //start moving projectile south
                    setVelocityY(DEFAULT_VELOCITY_Y);

                    //move to next step
                    step1 = false;
                    step2 = true;
                }
            }
            else if (step2)
            {
                //correct coordinate
                if (getY() >= step2Y)
                    setY(step2Y);
                
                //at destination
                if (getY() == step2Y)
                {
                    //start turning back
                    setVelocityX(-getVelocityX());
                    
                    //move to next step
                    step2 = false;
                    step3 = true;
                }
            }
            else if (step3)
            {
                //correct coordinate
                if (getY() >= step3Y)
                    setY(step3Y);
                
                //at destination
                if (getY() == step3Y)
                {
                    //stop y-velocity
                    resetVelocityY();
                    
                    //move to next step
                    step3 = false;
                    step4 = true;
                }
            }
            else if (step4)
            {
                if (getVelocityX() > 0)
                {
                    //correct coordinate
                    if (getX() >= step4X)
                        setX(step4X);
                }
                else
                {
                    //correct coordinate
                    if (getX() <= step4X)
                        setX(step4X);
                }
                
                //at destination
                if (getX() == step4X)
                {
                    //flag dead
                    markDead();
                    
                    //stop moving
                    resetVelocity();
                    
                    //all steps completed
                    step4 = false;
                }
            }
        }
        
        //if the projectile is no longer on the screen, flag as dead
        if (!level.getBoundary().intersects(getRectangle()) && getY() > level.getBoundary().getY())
        {
            //flag dead
            markDead();
            
            //stop moving
            resetVelocity();
        }
    }
}