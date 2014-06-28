package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.util.Timer;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.world.level.Level;
import com.gamesbykevin.mario.world.level.tiles.Tile;
import com.gamesbykevin.mario.projectiles.Bullet;
import java.util.List;

import java.util.Random;

public final class BulletBill extends Enemy
{
    /**
     * The different possibilities
     */
    public enum State
    {
        Idle, 
    }
    
    //track how long we have been stunned
    private Timer timer;
    
    //how long should the enemy be stunned for
    private static final long DEFAULT_DURATON_REST = Timers.toNanoSeconds(1500L);
    
    //the range to determine if the enemy is to attack
    private static final double PIXEL_RANGE = Tile.WIDTH * 8;
    
    public BulletBill(final Random random)
    {
        super(Enemy.DEFAULT_JUMP_VELOCITY, Entity.SPEED_NONE, Entity.SPEED_NONE);
        
        //set defaults
        setWeaknessProjectile(false);
        setDamageCollision(false);
        setWeaknessStomp(false);
        setProjectileLimit(1);
        setDefyGravity(true);
        setFaceEast(false);
        
        //create new timer
        timer = new Timer(DEFAULT_DURATON_REST);
    }
    
    /**
     * Set up animations
     */
    @Override
    protected void defineAnimations()
    {
        //walking animation
        super.addAnimation(State.Idle, 1, 0, 445, 16, 17, 0, false);
        
        //set the default animation
        super.setAnimation(State.Idle, true);
    }
    
    @Override
    protected void checkSideCollision(final Hero hero)
    {
        //this enemy won't hurt the hero
    }
    
    @Override
    protected void markHurt()
    {
        //enemy can't be hurt unless hero is invincible
    }
    
    @Override
    public void update(final Hero hero, final Level level, final long time)
    {
        //determine the middle x coordinates
        final double middleX = getX() + (getWidth() / 2);
        final double heroMiddleX = hero.getX() + (hero.getWidth() / 2);

        //determine how many pixels separate the hero and enemy
        final double pixelDistance = (middleX > heroMiddleX) ? middleX - heroMiddleX : heroMiddleX - middleX;

        //make sure we can throw projectiles first
        if (canThrowProjectile())
        {
            //check if the hero is close enough to the enemy
            if (pixelDistance <= PIXEL_RANGE)
            {
                //update timer
                timer.update(time);

                //if time has passed we can attack
                if (timer.hasTimePassed())
                {
                    //reset timer
                    timer.reset();
                    
                    //make sure facing the appropriate direction
                    correctFacingDirection((middleX < heroMiddleX) ? false : true);
                    
                    //add projectile
                    addProjectile(new Bullet());
                }
            }
        }
    }
}