package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.level.Level;
import java.util.Random;

public class Boo extends Enemy
{
    /**
     * The different possibilities
     */
    public enum State
    {
        Hiding, Chasing 
    }
    
    //speed(s) for this enemy
    private static final double SPEED = 0.45;
    
    public Boo(final Random random)
    {
        super(Enemy.DEFAULT_JUMP_VELOCITY, SPEED, SPEED);
        
        //set defaults
        setWeaknessProjectile(false);
        setDamageCollision(true);
        setWeaknessStomp(false);
        setProjectileLimit(0);
        setDefyGravity(true);
        setFaceEast(false);
    }
    
    /**
     * Set up animations
     */
    @Override
    protected void defineAnimations()
    {
        //hiding animation
        super.addAnimation(State.Hiding, 1, 0, 250, 16, 16, 0, false);
        
        //chasing animation
        super.addAnimation(State.Chasing, 1, 16, 250, 16, 16, 0, false);
        
        //set the default animation
        super.setAnimation(State.Hiding, true);
    }
    
    @Override
    protected void checkSideCollision(final Hero hero)
    {
        //if collision, then hero is hurt
        hero.flagDamage();
    }
    
    @Override
    protected void markHurt()
    {
        //can't hurt boo with physical collision and not invisible
    }
    
    @Override
    public void update(final Hero hero, final Level level, final long time)
    {
        //stop movement
        resetVelocity();
        
        if (hero.getX() < getX())
        {
            //if the hero is on the left side
            if (!hero.hasHorizontalFlip())
            {
                //hero is facing the ghost
                super.setAnimation(State.Hiding, false);
            }
            else
            {
                //hero is facing away so chase
                super.setAnimation(State.Chasing, false);
                
                //move towards hero
                super.setVelocityX(-getSpeedWalk());
                
                //move towards hero
                setVelocityY(hero);
                
                //face the appropriate direction
                correctFacingDirection(false);
            }
        }
        else if (hero.getX() + hero.getWidth() > getX() + getWidth())
        {
            //if the hero is on the right side
            if (hero.hasHorizontalFlip())
            {
                //hero is facing the ghost
                super.setAnimation(State.Hiding, false);
            }
            else
            {
                //hero is facing away so chase
                super.setAnimation(State.Chasing, false);
                
                //move towards hero
                super.setVelocityX(getSpeedWalk());
                
                //move towards hero
                setVelocityY(hero);
                
                //face the appropriate direction
                correctFacingDirection(true);
            }
        }
    }
    
    /**
     * Set the appropriate y-velocity to chase the hero
     * @param hero The hero we are chasing
     */
    private void setVelocityY(final Hero hero)
    {
        if (hero.getY() > getY() && hero.getY() - getY() < SPEED)
            setY(hero.getY());
        if (getY() > hero.getY() && getY() - hero.getY() < SPEED)
            setY(hero.getY());

        //set the correct y velocity
        super.setVelocityY((getY() < hero.getY()) ? SPEED : -SPEED);
    }
}