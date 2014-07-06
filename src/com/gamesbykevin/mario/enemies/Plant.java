package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.world.level.Level;
import com.gamesbykevin.mario.world.level.tiles.Tile;
import com.gamesbykevin.mario.projectiles.PlantFireball;
import com.gamesbykevin.mario.resources.GameAudio;

import java.util.Random;

public final class Plant extends Enemy
{
    /**
     * The different possibilities
     */
    public enum State
    {
        FaceSouth, FaceNorth
    }
    
    //track how long we pause before appearing
    private Timers timers;
    
    private enum Key
    {
        Paused, Attack
    }
    
    //our start and stop locations
    private double startY, destinationY;
    
    //how long should the enemy pause before retreating
    private static final long DEFAULT_DURATON_PAUSED = Timers.toNanoSeconds(3500L);
    
    //the amount of time to wait between each attack
    private static final long DEFAULT_DURATON_ATTACK = Timers.toNanoSeconds(500L);
    
    //the speed which to appear
    protected static final double DEFAULT_VELOCITY_Y = 0.25;
    
    //the range to determine if the enemy is to attack
    private static final double PIXEL_RANGE_ATTACK = Tile.WIDTH * (Level.LEVEL_COLUMNS_PER_SCREEN / 2);
    
    //the range to determine if the enemy is to hide
    private static final double PIXEL_RANGE_HIDE = Tile.WIDTH * 3.25;
    
    public Plant(final Random random)
    {
        super(Enemy.DEFAULT_JUMP_VELOCITY * 1.25, Entity.SPEED_NONE, Entity.SPEED_NONE);
        
        //set defaults
        setWeaknessProjectile(true);
        setDamageCollision(true);
        setWeaknessStomp(false);
        setProjectileLimit(1);
        setDefyGravity(true);
        setFaceEast(false);
        
        //by default stop moving
        super.resetVelocity();
        
        //start as idle
        super.setIdle(true);
    }
    
    /**
     * Set up animations
     */
    @Override
    protected void defineAnimations()
    {
        //add animation
        super.addAnimation(State.FaceSouth, 1, 0, 478, 16, 32, 0, false);
        
        //add animation
        super.addAnimation(State.FaceNorth, 1, 16, 478, 16, 32, 0, false);
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
        //can't be hurt unless hero is invincible or hits it with a flower
    }
    
    @Override
    public void update(final Hero hero, final Level level, final long time)
    {
        //setup timers if not created yet
        if (timers == null)
        {
            //create new timer
            timers = new Timers(time);
            timers.add(Key.Paused, DEFAULT_DURATON_PAUSED);
            timers.add(Key.Attack, DEFAULT_DURATON_ATTACK);
        }
        
        if (isIdle())
        {
            //store the original location
            startY = getY();
            
            //store the destination
            destinationY = getY() - getHeight();
            
            //determine the middle x coordinates
            final double middleX = getX() + (getWidth() / 2);
            final double heroMiddleX = hero.getX() + (hero.getWidth() / 2);
            
            //determine how many pixels separate the hero and enemy
            final double pixelDistance = (middleX > heroMiddleX) ? middleX - heroMiddleX : heroMiddleX - middleX;
            
            if (pixelDistance <= PIXEL_RANGE_HIDE)
            {
                //the hero is too close, stay hidden
            }
            else if (pixelDistance <= PIXEL_RANGE_ATTACK)
            {
                //the hero is close, but not too close. we can start to come out to attack
                setIdle(false);

                //start to appear
                setVelocityY(-DEFAULT_VELOCITY_Y);

                //set correct animation
                setAnimation((getY() < hero.getY()) ? State.FaceSouth : State.FaceNorth, true);

                //face the correct direction
                correctFacingDirection((middleX < heroMiddleX) ? true : false);
            }
        }
        else
        {
            //don't move past destination
            if (getY() < destinationY)
                setY(destinationY);
            
            //if we are at our destination
            if (getY() == destinationY)
            {
                //stop moving
                resetVelocity();
                
                //update timer
                timers.update(Key.Paused);
                
                //if time has passed we will go back towards hiding
                if (timers.hasTimePassed(Key.Paused))
                {
                    //move back towards hiding
                    setVelocityY(DEFAULT_VELOCITY_Y);
                    
                    //reset timer
                    timers.reset();
                }
                else
                {
                    //check if we can fire projectile
                    if (canThrowProjectile())
                    {
                        //update attack timer
                        timers.update(Key.Attack);
                        
                        //if time has passed can attack again
                        if (timers.hasTimePassed(Key.Attack))
                        {
                            //add projectile
                            addProjectile(new PlantFireball(isAnimation(State.FaceNorth) ? true : false));
                            
                            //set sound to play
                            super.setAudioKey(GameAudio.Keys.SfxLevelEnemyFireball);
            
                            //reset timer
                            timers.reset(Key.Attack);
                        }
                    }
                }
            }
            else
            {
                //meaning we are going back to hiding
                if (getVelocityY() > 0)
                {
                    if (getY() > startY)
                        setY(startY);
                    
                    //we made it back to hiding
                    if (getY() == startY)
                    {
                        //stop moving
                        resetVelocity();
                        
                        //set idle again
                        setIdle(true);
                    }
                }
            }
        }
    }
}