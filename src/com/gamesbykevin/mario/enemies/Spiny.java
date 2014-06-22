package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.tiles.Tile;
import com.gamesbykevin.mario.level.tiles.Tiles;
import java.util.Random;

public class Spiny extends Enemy
{
    /**
     * The different possibilities
     */
    public enum State
    {
        Walking, 
    }
    
    //track how long we have been stunned
    private Timer timer;
    
    //how long to wait before running
    private static final long DEFAULT_DURATON_RUN = Timers.toNanoSeconds(10000L);
    
    //different speeds for this enemy
    private static final double WALK_SPEED = 0.35;
    private static final double RUN_SPEED = 0.7;
    
    public Spiny(final Random random)
    {
        super(Enemy.DEFAULT_JUMP_VELOCITY, WALK_SPEED, RUN_SPEED);
        
        //set defaults
        setWeaknessProjectile(true);
        setDamageCollision(true);
        setWeaknessStomp(false);
        setProjectileLimit(0);
        setDefyGravity(false);
        setFaceEast(false);
        
        //create new timer
        timer = new Timer(DEFAULT_DURATON_RUN);
        
        //determine random direction to walk
        if (random.nextBoolean())
        {
            //set speed
            super.setVelocityX(getSpeedWalk());
            
            //make enemy face the opposite direction
            super.setHorizontalFlip(true);
        }
        else
        {
            //set speed
            super.setVelocityX(-getSpeedWalk());
        }
        
        //we are waling by default
        super.setWalk(true);
    }
    
    /**
     * Set up animations
     */
    @Override
    protected void defineAnimations()
    {
        //walking animation
        super.addAnimation(State.Walking, 2, 0, 234, 16, 16, Timers.toNanoSeconds(250L), true);
        
        //set the default animation
        super.setAnimation(State.Walking, true);
    }
    
    @Override
    protected void checkSideCollision(final Hero hero)
    {
        if (isAnimation(State.Walking))
        {
            if (hero.getX() < getX() && getVelocityX() < 0)
            {
                //if the hero is on the left side and the shell is moving left
                if (hasDamageCollision())
                {
                    hero.flagDamage();
                }
            }
            else if (hero.getX() + hero.getWidth() > getX() + getWidth() && getVelocityX() > 0)
            {
                //if the hero is on the right side and the shell is moving right
                if (hasDamageCollision())
                {
                    hero.flagDamage();
                }
            }
        }
    }
    
    @Override
    protected void markHurt()
    {
        //can't hurt spiny with physical collision and not invisible
    }
    
    @Override
    public void update(final Hero hero, final Level level, final long time)
    {
        //get our tiles object
        Tiles tiles = level.getTiles();
        
        //make sure time has not passed
        if (!timer.hasTimePassed())
        {
            //update timer
            timer.update(time);

            //if the timer has finished
            if (timer.hasTimePassed())
            {
                if (getVelocityX() < 0)
                {
                    //move the enemy faster now
                    setVelocityX(-getSpeedRun());

                    //face the appropriate direction
                    correctFacingDirection(false);
                }
                else
                {
                    //move the enemy faster now
                    setVelocityX(getSpeedRun());

                    //face the appropriate direction
                    correctFacingDirection(true);
                }
            }
        }
        
        //check tile below
        Tile south = checkCollisionSouth(tiles);

        //if we hit a tile at our feet, make sure to stop
        if (south != null)
        {
            //stop jumping if we were previously
            if (super.isJumping())
                super.stopJumping();
        }
        
        //if moving west, check for west collision
        if (getVelocityX() < 0)
        {
            if (checkCollisionWest(tiles) != null)
                turnAround();
        }
        
        //if moving east, check for east collision
        if (getVelocityX() > 0)
        {
            if (checkCollisionEast(tiles) != null)
                turnAround();
        }
        
        //if moving east or west check if going to fall off edge
        if (isAnimation(State.Walking) && hasVelocityX())
        {
            if (getVelocityX() < 0)
            {
                //if there is no floor below, turn around
                if (!tiles.hasFloorBelow(getX() + getVelocityX()))
                    turnAround();
            }
            else
            {
                //if there is no floor below, turn around
                if (!tiles.hasFloorBelow(getX() + getWidth() + getVelocityX()))
                    turnAround();
            }
        }
    }
}