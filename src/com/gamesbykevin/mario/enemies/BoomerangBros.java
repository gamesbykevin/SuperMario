package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.tiles.Tile;
import com.gamesbykevin.mario.projectiles.Boomerang;

import java.util.Random;

public final class BoomerangBros extends Enemy
{
    /**
     * The different possibilities
     */
    public enum State
    {
        Walking, Attacking
    }
    
    //track how long
    private Timer timer;
    
    //how long before we throw another projectile
    private static final long DELAY_BETWEEN_PROJECTILES = Timers.toNanoSeconds(450L);
    
    //different speeds for this enemy
    private static final double WALK_SPEED = 0.35;
    
    //the range to determine if the enemy is to attack
    private static final double PIXEL_RANGE = Tile.WIDTH * 5;
    
    public BoomerangBros(final Random random)
    {
        super(Enemy.DEFAULT_JUMP_VELOCITY, WALK_SPEED, WALK_SPEED);
        
        //set defaults
        setWeaknessProjectile(true);
        setDamageCollision(true);
        setWeaknessStomp(true);
        setProjectileLimit(2);
        setDefyGravity(false);
        setFaceEast(true);
        
        //create new timer
        timer = new Timer(DELAY_BETWEEN_PROJECTILES);
        
        //don't move at first
        resetVelocity();
    }
    
    /**
     * Set up animations
     */
    @Override
    protected void defineAnimations()
    {
        //walking animation
        super.addAnimation(State.Walking, 2, 0, 510, 16, 24, Timers.toNanoSeconds(250L), true);
        
        //stunned animation
        super.addAnimation(State.Attacking, 2, 32, 510, 16, 24, Timers.toNanoSeconds(500L), false);
        
        //set the default animation
        super.setAnimation(State.Walking, true);
    }
    
    @Override
    protected void checkSideCollision(final Hero hero)
    {
        if (!hero.isHurt())
        {
            if (hasDamageCollision())
            {
                hero.flagDamage();
            }
        }
    }
    
    @Override
    protected void markHurt()
    {
        //store bottom y-coordinate
        final double y = getY() + getHeight();
        
        //flag the enemy as dead
        markDead();

        //stop the enemy from moving
        resetVelocity();

        //set new dimensions
        setDimensions();

        //correct y-coordinate
        correctY(y);
    }
    
    @Override
    public void update(final Hero hero, final Level level, final long time)
    {
        //determine the middle x coordinates
        final double middleX = getX() + (getWidth() / 2);
        final double heroMiddleX = hero.getX() + (hero.getWidth() / 2);

        //determine how many pixels separate the hero and enemy
        final double pixelDistance = (middleX > heroMiddleX) ? middleX - heroMiddleX : heroMiddleX - middleX;

        //correct facing direction
        correctFacingDirection((heroMiddleX > middleX));
        
        if (!isAnimation(State.Attacking))
        {
            //check if the hero is close enough to the enemy
            if (pixelDistance <= PIXEL_RANGE)
            {
                //set the animation walking
                setAnimation(State.Walking, false);

                //move away from hero
                setVelocityX((middleX > heroMiddleX) ? getSpeedWalk() : -getSpeedWalk());
                
                //update timer
                timer.update(time);

                //if time has passed
                if (timer.hasTimePassed())
                {
                    //start attacking
                    setAnimation(State.Attacking, true);

                    //reset timer
                    timer.reset();
                    
                    //stop moving
                    resetVelocity();
                }
            }
        }
        else
        {
            //if animation is finished we can start 
            if (getSpriteSheet().hasFinished())
            {
                if (canThrowProjectile())
                {
                    //save original coordinate
                    final double y = getY();
                    final double x = getX();
                    
                    //set temp location
                    setY(y - Boomerang.PROJECTILE_HEIGHT);
                    setX(middleX - (Boomerang.PROJECTILE_WIDTH / 2));

                    //add projectile
                    addProjectile(new Boomerang((heroMiddleX > middleX) ? true : false));

                    //restore location
                    setY(y);
                    setX(x);
                    
                    //set the animation walking
                    setAnimation(State.Walking, false);
                }
            }
        }
        
        //check basic collision
        checkDefaultLevelCollision(level.getTiles());
        
        //if moving east or west check if going to fall off edge
        if (isAnimation(State.Walking))
            preventDeath(level.getTiles());
    }
}