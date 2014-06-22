package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.util.Timer;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.tiles.Tile;
import com.gamesbykevin.mario.level.tiles.Tiles;

import java.util.Random;

public final class TurtleRed extends Enemy
{
    /**
     * The different possibilities
     */
    public enum State
    {
        Walking, Stunned, Kicked
    }
    
    //track how long we have been stunned
    private Timer timer;
    
    //how long should the enemy be stunned for
    private static final long DEFAULT_DURATON_STUNNED = Timers.toNanoSeconds(4500L);
    
    //different speeds for this enemy
    private static final double WALK_SPEED = 0.25;
    private static final double RUN_SPEED = 0.5;
    
    public TurtleRed(final Random random)
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
        super.addAnimation(State.Walking, 4, 0, 27, 16, 27, Timers.toNanoSeconds(250L), true);
        
        //stunned animation
        super.addAnimation(State.Stunned, 1, 32, 27, 16, 16, 0, false);
        
        //shell kicked
        super.addAnimation(State.Kicked, 3, 48, 27, 16, 16, Timers.toNanoSeconds(150L), true);
        
        //set the default animation
        super.setAnimation(State.Walking, true);
    }
    
    @Override
    protected void checkSideCollision(final Hero hero)
    {
        if (isAnimation(State.Stunned))
        {
            //if enemy is stunned then the shell has been kicked
            setAnimation(State.Kicked, true);
            
            //if the hero is on the left side
            if (hero.getX() < getX())
            {
                //kick the shell to the right, and make if move faster than the hero's run speed
                super.setVelocityX(Character.DEFAULT_SPEED_RUN * 1.5);
            }
            else if (hero.getX() + hero.getWidth() > getX() + getWidth())
            {
                //kick the shell to the left, and make if move faster than the hero's run speed
                super.setVelocityX(-Character.DEFAULT_SPEED_RUN * 1.5);
            }
        }
        else if (isAnimation(State.Walking) || isAnimation(State.Kicked))
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
        
        if (isAnimation((State.Kicked)))
        {
            //set the animation back to stunned
            setAnimation(State.Stunned, true);
            
            //stop the enemy from moving
            resetVelocity();
            
            //set new dimensions
            setDimensions();
            
            //correct y-coordinate
            correctY(y);
        }
        else if (isAnimation((State.Walking)))
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
        
        //reset the stun timer if not stunned
        if (!isAnimation((State.Stunned)))
        {
            timer.reset();
        }
        else
        {
            //update timer
            timer.update(time);
            
            //if the enemy has been stunned for so long they will move again at a faster rate
            if (timer.hasTimePassed())
            {
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