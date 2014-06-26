package com.gamesbykevin.mario.projectiles;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.level.Level;

public final class Hammer extends Projectile
{
    public static final int PROJECTILE_WIDTH = 16;
    public static final int PROJECTILE_HEIGHT = 16;
    
    //the speed to move
    private static final double DEFAULT_VELOCITY_X = (Character.DEFAULT_SPEED_RUN * 1.1);
    
    private enum State
    {
        Attacking
    }
    
    public Hammer(final boolean east)
    {
        //set direction
        setVelocityX((east) ? DEFAULT_VELOCITY_X : -DEFAULT_VELOCITY_X);
        
        //the jump velocity
        super.setJumpVelocity((Character.DEFAULT_JUMP_VELOCITY * .5));
    }
    
    @Override
    protected void defineAnimations()
    {
        //add animation
        super.addAnimation(State.Attacking, 4, 64, 534, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, Timers.toNanoSeconds(125L), true);
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
        
        //start throwing the projectile
        startJump();
                
        //face the appropriate direction
        setHorizontalFlip(!character.hasHorizontalFlip());
    }
    
    @Override
    public void updateLogic(final Level level)
    {
        //apply gravity to projectile
        applyGravity(level.getTiles());
        
        if (!hasVelocityY())
            applyGravity(level.getTiles());
        
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