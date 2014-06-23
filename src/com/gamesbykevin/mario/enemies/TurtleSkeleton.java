package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.util.Timer;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.tiles.Tile;
import com.gamesbykevin.mario.level.tiles.Tiles;

import java.util.Random;

public final class TurtleSkeleton extends Enemy
{
    /**
     * The different possibilities
     */
    public enum State
    {
        Walking, Stunned, Recover 
    }
    
    //track how long we have been stunned
    private Timer timer;
    
    //how long should the enemy be stunned for
    private static final long DEFAULT_DURATON_STUNNED = Timers.toNanoSeconds(5000L);
    
    //how long should the enemy be recover for
    private static final long DEFAULT_DURATON_RECOVER = Timers.toNanoSeconds(3000L);
    
    //different speeds for this enemy
    private static final double WALK_SPEED = 0.33;
    private static final double RUN_SPEED = 0.66;
    
    public TurtleSkeleton(final Random random)
    {
        super(Enemy.DEFAULT_JUMP_VELOCITY, WALK_SPEED, RUN_SPEED);
        
        //set defaults
        setWeaknessProjectile(false);
        setDamageCollision(true);
        setWeaknessStomp(true);
        setProjectileLimit(0);
        setDefyGravity(false);
        setFaceEast(false);
        
        //create new timer
        timer = new Timer(DEFAULT_DURATON_STUNNED);
        
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
        super.addAnimation(State.Walking, 2, 0, 305, 16, 28, Timers.toNanoSeconds(250L), true);
        
        //stunned animation
        super.addAnimation(State.Stunned, 4, 32, 305, 24, 16, Timers.toNanoSeconds(333L), false);
        
        //recover animation
        super.addAnimation(State.Recover, 2, 79, 305, 24, 16, Timers.toNanoSeconds(200L), true);
        
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
                //if the hero is on the left side and the enemy is moving left
                if (hasDamageCollision())
                {
                    hero.flagDamage();
                }
            }
            else if (hero.getX() + hero.getWidth() > getX() + getWidth() && getVelocityX() > 0)
            {
                //if the hero is on the right side and the enemy is moving right
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
            setAnimation(State.Stunned, true);
            
            //stop the enemy from moving
            resetVelocity();
            
            //set new dimensions
            setDimensions();
            
            //adjust y-coordinate
            setY(y - getHeight());
        }
    }
    
    @Override
    public void update(final Hero hero, final Level level, final long time)
    {
        //get our tiles object
        Tiles tiles = level.getTiles();
        
        
        if (!isAnimation((State.Walking)))
        {
            //update timer
            timer.update(time);
            
            if (timer.hasTimePassed())
            {
                if (isAnimation(State.Stunned))
                {
                    //set new time duration
                    timer.setReset(DEFAULT_DURATON_RECOVER);
                    
                    //reset the timer
                    timer.reset();
                    
                    //set new animation
                    setAnimation(State.Recover, true);
                }
                else if (isAnimation(State.Recover))
                {
                    //set new time duration
                    timer.setReset(DEFAULT_DURATON_STUNNED);
                    
                    //reset the timer
                    timer.reset();
                    
                    final double y = getY() + getHeight();

                    //set new animation
                    setAnimation(State.Walking, true);

                    //correct width-height
                    setDimensions();

                    //correct y-coordinate
                    correctY(y);

                    if (hero.getX() < getX())
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
        }
        
        //check basic collision
        checkDefaultLevelCollision(tiles);
        
        //if moving east or west check if going to fall off edge
        if (isAnimation(State.Walking))
            preventDeath(tiles);
    }
}