package com.gamesbykevin.mario.projectiles;

import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.world.level.Level;
import com.gamesbykevin.mario.world.level.tiles.Tile;
import com.gamesbykevin.mario.world.level.tiles.Tiles;

public final class PlantFireball extends Projectile
{
    private static final int FIREBALL_WIDTH = 9;
    private static final int FIREBALL_HEIGHT = 9;
    
    private static final double DEFAULT_VELOCITY_Y = 0.55;
    private static final double DEFAULT_VELOCITY_X = DEFAULT_VELOCITY_Y * 1.85;
    
    public PlantFireball(final boolean north)
    {
        //set the appropriate direction
        setVelocityY((north) ? -DEFAULT_VELOCITY_Y : DEFAULT_VELOCITY_Y);
    }
    
    @Override
    protected void defineAnimations()
    {
        //fireball
        super.addAnimation(Hero.State.Fireball, 4, 32, 478, FIREBALL_WIDTH, FIREBALL_HEIGHT, Timers.toNanoSeconds(175L), true);
    }
    
    @Override
    public boolean checkCharacterCollision(final Character character, final Effects effects)
    {
        if (getRectangle().intersects(character.getRectangle()))
        {
            if (!character.isHurt())
            {
                //is the enemy weak towards a projectile
                if (character.hasWeaknessProjectile())
                {
                    //check if character has been hurt
                    character.flagDamage();
                }

                //add effect to be drawn
                effects.add(this, Effects.Type.FireballDestroy);

                //collision occurred
                return true;
            }
        }
        
        //no collision detected
        return false;
    }
    
    @Override
    protected void checkLevelCollision(final Level level)
    {
        Tiles tiles = level.getTiles();
        
        Tile south = checkCollisionSouth(tiles);
        Tile west = checkCollisionWest(tiles);
        Tile east = checkCollisionEast(tiles);

        //we have collision, remove fireball
        if (west != null || east != null || south != null)
        {
            //add effect
            level.getEffects().add(this, Effects.Type.FireballDestroy);

            //flag dead
            markDead();

            //stop moving
            resetVelocity();
        }
    }
    
    @Override
    public void setup(final Character character)
    {
        if (character.hasHorizontalFlip())
        {
            setLocation(character.getX() + (character.getWidth() / 2), character.getY() + (character.getHeight() / 4));
            setVelocityX(DEFAULT_VELOCITY_X);
        }
        else
        {
            setLocation(character.getX() + (character.getWidth() / 2), character.getY() + (character.getHeight() / 4));
            setVelocityX(-DEFAULT_VELOCITY_X);
        }
    }
    
    @Override
    public void updateLogic(final Level level)
    {
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