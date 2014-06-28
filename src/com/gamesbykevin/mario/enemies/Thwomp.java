package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.util.Timer;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.world.level.Level;
import com.gamesbykevin.mario.world.level.tiles.Tile;
import com.gamesbykevin.mario.world.level.tiles.Tiles;

import java.util.List;
import java.util.Random;

public final class Thwomp extends Enemy
{
    /**
     * The different possibilities
     */
    public enum State
    {
        Idle, Attack, Rest, Reset
    }
    
    //track how long we have been stunned
    private Timer timer;
    
    //where the thwomp is positioned
    private double startY;
    
    //how long should the enemy be stunned for
    private static final long DEFAULT_DURATON_STOPPED = Timers.toNanoSeconds(2500L);
    
    //the range to determine if the enemy is to attack
    private static final double PIXEL_RANGE = Tile.WIDTH * 2;
    
    public Thwomp(final Random random)
    {
        super(Enemy.DEFAULT_JUMP_VELOCITY * 1.25, Entity.SPEED_NONE, Entity.SPEED_NONE);
        
        //set defaults
        setWeaknessProjectile(false);
        setDamageCollision(true);
        setWeaknessStomp(false);
        setProjectileLimit(0);
        setDefyGravity(true);
        setFaceEast(false);
        
        //create new timer
        timer = new Timer(DEFAULT_DURATON_STOPPED);
        
        //by default stop moving
        super.resetVelocity();
    }
    
    /**
     * Set up animations
     */
    @Override
    protected void defineAnimations()
    {
        //add animation
        super.addAnimation(State.Idle, 1, 0, 413, 24, 32, 0, false);
        
        //add animation
        super.addAnimation(State.Attack, 1, 0, 413, 24, 32, 0, false);
        
        //add animation
        super.addAnimation(State.Rest, 1, 0, 413, 24, 32, 0, false);
        
        //add animation
        super.addAnimation(State.Reset, 1, 0, 413, 24, 32, 0, false);
        
        //set the default animation
        super.setAnimation(State.Idle, true);
    }
    
    @Override
    protected void checkSideCollision(final Hero hero)
    {
        //any collision will hurt enemy
        hero.flagDamage();
    }
    
    @Override
    protected void markHurt()
    {
        //can't be hurt unless hero is invincible
    }
    
    @Override
    public void update(final Hero hero, final Level level, final long time)
    {
        //get our tiles object
        Tiles tiles = level.getTiles();
        
        if (isAnimation(State.Idle))
        {
            //determine the middle x coordinates
            final double middleX = getX() + (getWidth() / 2);
            final double heroMiddleX = hero.getX() + (hero.getWidth() / 2);
            
            //determine how many pixels separate the hero and enemy
            final double pixelDistance = (middleX > heroMiddleX) ? middleX - heroMiddleX : heroMiddleX - middleX;
            
            //check if the hero is close enough to the enemy
            if (pixelDistance <= PIXEL_RANGE)
            {
                //get the start
                startY = getY();

                //enemy will fall
                setJump(true);

                //the speed at which to fall
                setVelocityY(getJumpVelocity());

                //set appropriate animation
                setAnimation(State.Attack, false);

                //reset the timer
                timer.reset();
            }
        }
        else if (isAnimation(State.Attack))
        {
            //if we hit a tile at our feet, make sure to stop
            if (checkCollisionSouth(tiles) != null)
            {
                //stop jumping and this will also stop velocity
                stopJumping();

                //the enemy will rest at the current location
                setAnimation(State.Rest, false);
            }
        }
        else if (isAnimation(State.Rest))
        {
            //update timer
            timer.update(time);
            
            if (timer.hasTimePassed())
            {
                //now the enemy will climb back up
                setAnimation(State.Reset, false);
                
                //the speed at which to climb
                setVelocityY(-(getJumpVelocity() / 4));
            }
        }
        else if (isAnimation(State.Reset))
        {
            //if we have reached our original position
            if (getY() <= startY)
            {
                //set back at start location
                setY(startY);
                
                //stop moving
                resetVelocity();
                
                //the enemy is idle once again
                setAnimation(State.Idle, false);
            }
        }
    }
}