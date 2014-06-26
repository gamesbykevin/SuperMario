package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.util.Timer;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.tiles.Tiles;

import java.util.Random;

public final class GoombaRedWings extends Enemy
{
    /**
     * The different possibilities
     */
    public enum State
    {
        Walking, Stomped
    }
    
    //track how long we have been stunned
    private Timer timer;
    
    //how long should the enemy be stunned for
    private static final long DEFAULT_DURATON_STOMPED = Timers.toNanoSeconds(2000L);
    
    //different speeds for this enemy
    private static final double WALK_SPEED = 0.45;
    
    public GoombaRedWings(final Random random)
    {
        super(Enemy.DEFAULT_JUMP_VELOCITY * 1.5, WALK_SPEED, WALK_SPEED);
        
        //set defaults
        setWeaknessProjectile(true);
        setDamageCollision(true);
        setWeaknessStomp(true);
        setProjectileLimit(0);
        setDefyGravity(false);
        setFaceEast(false);
        
        //create new timer
        timer = new Timer(DEFAULT_DURATON_STOMPED);
        
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
        
        //start jumping as well
        super.startJump();
    }
    
    /**
     * Set up animations
     */
    @Override
    protected void defineAnimations()
    {
        //walking animation
        super.addAnimation(State.Walking, 4, 0, 389, 20, 24, Timers.toNanoSeconds(100L), true);
        
        //stunned animation
        super.addAnimation(State.Stomped, 1, 80, 389, 16, 9, 0, false);
        
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
        final double y = getY() + getHeight();
        
        if (isAnimation((State.Walking)))
        {
            //if walking set the enemy to stunned
            setAnimation(State.Stomped, true);
            
            //stop the enemy from moving
            resetVelocity();
            
            //set new dimensions
            setDimensions();
            
            //adjust y-coordinate
            correctY(y);
        }
    }
    
    @Override
    public void update(final Hero hero, final Level level, final long time)
    {
        //get our tiles object
        Tiles tiles = level.getTiles();
        
        //if the enemy has been stomped
        if (isAnimation(State.Stomped))
        {
            //update the timer
            timer.update(time);
            
            //if time has run out mark the enemy as dead
            if (timer.hasTimePassed())
            {
                //flag dead
                markDead();
            }
        }
        
        //if we hit a tile at our feet, make sure to stop
        if (checkCollisionSouth(tiles) != null)
        {
            if (isJumping())
            {
                //stop, then start jumping again
                super.stopJumping();

                //keep jumping if the enemy is walking
                if (isAnimation(State.Walking))
                    super.startJump();
            }
        }
        
        //if moving north
        if (getVelocityY() < 0)
        {
            //check for collision north so we don't hit blocks;
            checkCollisionNorth(tiles);
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
        if (isAnimation(State.Walking))
            preventDeath(tiles);
    }
}