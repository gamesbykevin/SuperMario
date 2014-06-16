package com.gamesbykevin.mario.heroes;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.powerups.PowerUps;
import com.gamesbykevin.mario.level.tiles.Tile;
import com.gamesbykevin.mario.level.tiles.Tiles;
import java.util.Random;

public final class Projectile extends Entity
{
    private static final int FIREBALL_WIDTH = 8;
    private static final int FIREBALL_HEIGHT = 9;
    
    protected static final double DEFAULT_VELOCITY_X = 2;
    protected static final double DEFAULT_VELOCITY_Y = 2;
    protected static final double DEFAULT_JUMP_VELOCITY = 2;
    
    public Projectile()
    {
        //fireball
        super.addAnimation(Hero.State.Fireball, 4, 0, 80, FIREBALL_WIDTH, FIREBALL_HEIGHT, Timers.toNanoSeconds(50L), true);
        
        //set the width, height
        super.setDimensions();
        
        //the jump velocity
        super.setJumpVelocity(DEFAULT_JUMP_VELOCITY);
    }
    
    public void manageLevelCollision(final Level level, final Random random)
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
            }
        }
    }
}