package com.gamesbykevin.mario.projectiles;

import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.tiles.Tile;
import com.gamesbykevin.mario.level.tiles.Tiles;

public final class Bullet extends Projectile
{
    private static final int PROJECTILE_WIDTH = 16;
    private static final int PROJECTILE_HEIGHT = 14;
    
    //the speed to move
    private static final double DEFAULT_VELOCITY_X = (Character.DEFAULT_SPEED_WALK * 2);
    private static final double DEFAULT_JUMP_VELOCITY = 4;    
    
    private enum State
    {
        Attacking
    }
    
    public Bullet()
    {
        //the jump velocity
        super.setJumpVelocity(DEFAULT_JUMP_VELOCITY);
    }
    
    @Override
    protected void defineAnimations()
    {
        //add animation
        super.addAnimation(State.Attacking, 1, 16, 445, PROJECTILE_WIDTH, PROJECTILE_HEIGHT, 0, false);
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
            //make sure the character collides with the north and is jumping/falling, or invincible
            if (character.isJumping() && checkCollisionNorthAny(character) || character.isInvincible())
            {
                //if character is stomping on projectile it is dead
                markDead();
                
                //stop moving
                resetVelocity();
                
                //start falling
                startJump();
                
                if (character.isJumping())
                {
                    //bounce off projectile only if jumping
                    character.setVelocityY(-character.getVelocityY());
                }
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
        setVelocityX(character.hasHorizontalFlip() ? -DEFAULT_VELOCITY_X : DEFAULT_VELOCITY_X);
        
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