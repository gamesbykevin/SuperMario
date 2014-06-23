package com.gamesbykevin.mario.projectiles;


import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.level.Level;

public final class SpikeBall extends Projectile
{
    private static final int PROJECTILE_WIDTH = 14;
    private static final int PROJECTILE_HEIGHT = 14;
    
    //the speed to move
    protected static final double DEFAULT_VELOCITY_X = (Character.DEFAULT_SPEED_WALK * 2);
    protected static final double DEFAULT_VELOCITY_Y = 0.5;
            
    //the direction/speed which to move
    protected double velocityX;
    
    //location where the projectile will start to fly at target
    protected double destinationY;
    
    private enum State
    {
        Attacking
    }
    
    public SpikeBall()
    {
        //the jump velocity
        super.setJumpVelocity(Entity.SPEED_NONE);
    }
    
    @Override
    protected void defineAnimations()
    {
        //add animation
        super.addAnimation(State.Attacking, 1, 112, 462, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, 0, false);
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
        //no movement
        super.resetVelocity();
        
        //set position
        setLocation(character);
        
        //set direction
        velocityX = character.hasHorizontalFlip() ? DEFAULT_VELOCITY_X : -DEFAULT_VELOCITY_X;
        
        //set location when projectile will target enemy
        destinationY = getY() - getHeight();
        
        //start moving projectile north
        setVelocityY(-DEFAULT_VELOCITY_Y);
        
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
            //set at proper location
            if (getY() < destinationY)
                setY(destinationY);
                
            //if we reached our destination start moving towards target
            if (getY() == destinationY)
            {
                //stop moving
                resetVelocity();
                
                //apply x-velocity
                setVelocityX(velocityX);
            }
        }
        
        //if the projectile is no longer on the screen, flag as dead
        if (!level.getBoundary().intersects(getRectangle()))
        {
            //flag dead
            markDead();
            
            //stop moving
            resetVelocity();
        }
    }
}