package com.gamesbykevin.mario.projectiles;

import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.level.Level;

import java.awt.Rectangle;

public abstract class Projectile extends Entity
{
    public Projectile()
    {
        //setup animations
        defineAnimations();
        
        //set default dimensions
        setDimensions();
    }
    
    /**
     * Each projectile will have its own logic
     * @param level Level object
     */
    public abstract void updateLogic(final Level level);
    
    /**
     * Here the animations will be setup
     */
    protected abstract void defineAnimations();
    
    /**
     * Handle level collision
     * @param level The level we want to check for collision
     */
    protected abstract void checkLevelCollision(final Level level);
    
    /**
     * Handle character collision
     * @param character The character we are checking for collision
     * @param effects Here we will add effects if collision
     * @return true if collision occurred, false otherwise
     */
    public abstract boolean checkCharacterCollision(final Character character, final Effects effects);
    
    /**
     * Setup projectile
     * @param character The character we want to add the projectile to
     */
    public abstract void setup(final Character character);
}