package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.util.Timer;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.world.level.Level;
import com.gamesbykevin.mario.world.level.tiles.Tiles;

import java.util.List;
import java.util.Random;

public final class GoombaRed extends Enemy
{
    /**
     * The different possibilities
     */
    public enum State
    {
        Walking, Stomped
    }
    
    //track how long
    private Timer timer;
    
    //how long should the enemy be stunned for
    private static final long DEFAULT_DURATON_STOMPED = Timers.toNanoSeconds(2000L);
    
    //different speeds for this enemy
    private static final double WALK_SPEED = 0.25;
    private static final double RUN_SPEED = 0.5;
    
    public GoombaRed(final Random random)
    {
        super(Enemy.DEFAULT_JUMP_VELOCITY, WALK_SPEED, RUN_SPEED);
        
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
    }
    
    /**
     * Set up animations
     */
    @Override
    protected void defineAnimations()
    {
        //walking animation
        super.addAnimation(State.Walking, 2, 0, 373, 16, 16, Timers.toNanoSeconds(250L), true);
        
        //stunned animation
        super.addAnimation(State.Stomped, 1, 32, 373, 16, 9, 0, false);
        
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
                //if the hero is on the left side and the goomba is moving left
                if (hasDamageCollision())
                {
                    hero.flagDamage();
                }
            }
            else if (hero.getX() + hero.getWidth() > getX() + getWidth() && getVelocityX() > 0)
            {
                //if the hero is on the right side and the goomba is moving right
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
        //store bottom y-coordinate
        final double y = getY() + getHeight();
        
        if (isAnimation((State.Walking)))
        {
            //set the animation to stomped
            setAnimation(State.Stomped, true);
            
            //stop the enemy from moving
            resetVelocity();
            
            //set new dimensions
            setDimensions();
            
            //correct y-coordinate
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
                markDead();
        }
        else
        {
            //check basic collision
            checkDefaultLevelCollision(tiles);
            
            //if moving east or west check if going to fall off edge
            if (isAnimation(State.Walking))
                preventDeath(tiles);
        }
    }
}