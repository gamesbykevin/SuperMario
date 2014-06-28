package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.world.level.Level;
import com.gamesbykevin.mario.world.level.tiles.Tile;
import com.gamesbykevin.mario.projectiles.SpikeBall;

import java.util.List;
import java.util.Random;

public final class Spike extends Enemy
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
    
    //how long should the enemy rest for after attacking
    private static final long ATTACK_DELAY_DURATON = Timers.toNanoSeconds(1250L);
    
    //different speeds for this enemy
    private static final double WALK_SPEED = 0.55;
    
    //the range to determine if the enemy is to attack
    private static final double PIXEL_RANGE = Tile.WIDTH * 6;
    
    public Spike(final Random random)
    {
        super(Enemy.DEFAULT_JUMP_VELOCITY, WALK_SPEED, WALK_SPEED);
        
        //set defaults
        setWeaknessProjectile(true);
        setDamageCollision(true);
        setWeaknessStomp(true);
        setProjectileLimit(1);
        setDefyGravity(false);
        setFaceEast(false);
        
        //create new timer
        timer = new Timer(ATTACK_DELAY_DURATON);
        
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
        super.addAnimation(State.Walking, 2, 0, 462, 16, 16, Timers.toNanoSeconds(250L), true);
        
        //stunned animation
        super.addAnimation(State.Attacking, 5, 32, 462, 16, 16, Timers.toNanoSeconds(150L), false);
        
        //set the default animation
        super.setAnimation(State.Walking, true);
    }
    
    @Override
    protected void checkSideCollision(final Hero hero)
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
        if (isAnimation(State.Walking))
        {
            //determine the middle x coordinates
            final double middleX = getX() + (getWidth() / 2);
            final double heroMiddleX = hero.getX() + (hero.getWidth() / 2);
            
            //determine how many pixels separate the hero and enemy
            final double pixelDistance = (middleX > heroMiddleX) ? middleX - heroMiddleX : heroMiddleX - middleX;
            
            //check if the hero is close enough to the enemy
            if (pixelDistance <= PIXEL_RANGE)
            {
                //update timer
                timer.update(time);
                
                //if the timer has passed
                if (timer.hasTimePassed())
                {
                    //make sure we can attack
                    if (canThrowProjectile())
                    {
                        //stop moving
                        resetVelocity();

                        //start to attack
                        setAnimation(State.Attacking, true);

                        //make sure facing the appropriate direction
                        correctFacingDirection((middleX < heroMiddleX) ? true : false);
                        
                        //add projectile
                        addProjectile(new SpikeBall());
                        
                        //reset timer
                        timer.reset();
                    }
                }
            }
        }
        else if (isAnimation(State.Attacking))
        {
            if (getSpriteSheet().hasFinished())
            {
                //start walking again
                setAnimation(State.Walking, true);

                //start walking again
                if (hasHorizontalFlip())
                {
                    setVelocityX(getSpeedWalk());
                }
                else
                {
                    setVelocityX(-getSpeedWalk());
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