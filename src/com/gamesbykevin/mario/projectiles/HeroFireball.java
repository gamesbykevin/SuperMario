package com.gamesbykevin.mario.projectiles;

import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.tiles.Tile;
import com.gamesbykevin.mario.level.tiles.Tiles;

public final class HeroFireball extends Projectile
{
    private static final int FIREBALL_WIDTH = 8;
    private static final int FIREBALL_HEIGHT = 9;
    
    protected static final double DEFAULT_VELOCITY_X = 2;
    protected static final double DEFAULT_VELOCITY_Y = 2;
    protected static final double DEFAULT_JUMP_VELOCITY = 2;
    
    public HeroFireball()
    {
        //the jump velocity
        super.setJumpVelocity(DEFAULT_JUMP_VELOCITY);
    }
    
    @Override
    protected void defineAnimations()
    {
        //fireball
        super.addAnimation(Hero.State.Fireball, 4, 0, 80, FIREBALL_WIDTH, FIREBALL_HEIGHT, Timers.toNanoSeconds(250L), true);
    }
    
    @Override
    public boolean checkCharacterCollision(final Character character, final Effects effects)
    {
        if (getRectangle().intersects(character.getRectangle()))
        {
            //is the enemy weak towards a projectile
            if (character.hasWeaknessProjectile())
            {
                //enemy has been killed
                character.markDead();
                
                //flip enemy upside down
                character.setVerticalFlip(true);
            }
            
            //add effect to be drawn
            effects.add(this, Effects.Type.FireballDestroy);
            
            //collision occurred
            return true;
        }
        
        //no collision detected
        return false;
    }
    
    @Override
    protected void checkLevelCollision(final Level level)
    {
        Tiles tiles = level.getTiles();
        
        Tile south = checkCollisionSouth(tiles);

        //if we hit a tile at the floor
        if (south != null && getVelocityY() > 0)
        {
            startJump();
        }
        else
        {
            Tile west = checkCollisionWest(tiles);
            Tile east = checkCollisionEast(tiles);

            //we have collision, remove fireball
            if (west != null || east != null)
            {
                //add effect
                level.getEffects().add(this, Effects.Type.FireballDestroy);

                //flag dead
                markDead();
                
                //stop moving
                resetVelocity();
            }
        }
    }
    
    @Override
    public void setup(final Character character)
    {
        if (character.hasHorizontalFlip())
        {
            setLocation(character.getX() - getWidth(), character.getY());
            setVelocityX(-HeroFireball.DEFAULT_VELOCITY_X);
        }
        else
        {
            setLocation(character.getX() + character.getWidth(), character.getY());
            setVelocityX(HeroFireball.DEFAULT_VELOCITY_X);
        }
        
        setVelocityY(HeroFireball.DEFAULT_VELOCITY_Y);
        startJump();
    }
    
    @Override
    public void updateLogic(final Level level)
    {
        //apply gravity
        applyGravity(level.getTiles());
        
        //check for level collision
        checkLevelCollision(level);
        
        //if the projectile is no longer on the screen, flag as dead
        if (!level.getBoundary().contains(getCenter()))
        {
            //flag dead
            markDead();
            
            //stop moving
            resetVelocity();
        }
    }
}