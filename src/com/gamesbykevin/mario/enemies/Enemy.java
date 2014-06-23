package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.tiles.Tiles;
import com.gamesbykevin.mario.shared.IElement;

import java.awt.Graphics;

public abstract class Enemy extends Character implements Disposable, IElement
{
    //the type of enemy
    private Enemies.Type type;
    
    //determine if damage is caused by collision
    private boolean damageCollision = false;
    
    //can this enemy be hurt if stomped on
    private boolean weaknessStomp = false;
    
    //can this enemy float
    private boolean defyGravity = false;
    
    //be default is the enemy animation facing east
    private boolean faceEast = false;
    
    //when we first jump the rate which we move
    public static final double DEFAULT_JUMP_VELOCITY = 3;
    
    public Enemy(final double jumpVelocity, final double speedWalk, final double speedRun)
    {
        super(jumpVelocity, speedWalk, speedRun);
        
        //setup animations
        defineAnimations();
    }
    
    protected void setFaceEast(final boolean faceEast)
    {
        this.faceEast = faceEast;
    }
    
    protected boolean isFacingEast()
    {
        return this.faceEast;
    }
    
    protected void correctFacingDirection(final boolean faceEast)
    {
        if (isFacingEast())
        {
            if (faceEast)
            {
                setHorizontalFlip(false);
            }
            else
            {
                setHorizontalFlip(true);
            }
        }
        else
        {
            if (faceEast)
            {
                setHorizontalFlip(true);
            }
            else
            {
                setHorizontalFlip(false);
            }
        }
    }
    
    /**
     * Turn the enemy around in the opposite direction
     */
    protected void turnAround()
    {
        //set speed
        setVelocityX(-getVelocityX());

        //the enemy will face the opposite direction
        setHorizontalFlip(!hasHorizontalFlip());
    }
    
    /**
     * Prevent the enemy from walking off the edge
     * @param tiles The tiles in our level
     */
    protected void preventDeath(final Tiles tiles)
    {
        if (hasVelocityX())
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
    
    protected void checkDefaultLevelCollision(final Tiles tiles)
    {
        //if we hit a tile at our feet, make sure to stop
        if (checkCollisionSouth(tiles) != null)
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
    }
    
    /**
     * Determine if collision with the enemy can cause damage.<br>
     * An example would be bullet bill, collision would not cause damage so this value would be false. <br>
     * Only the projectile will cause damage.
     * @param damageCollision true if collision will cause damage, false otherwise
     */
    protected void setDamageCollision(final boolean damageCollision)
    {
        this.damageCollision = damageCollision;
    }
    
    protected boolean hasDamageCollision()
    {
        return this.damageCollision;
    }
    
    protected void setDefyGravity(final boolean defyGravity)
    {
        this.defyGravity = defyGravity;
    }
    
    protected boolean canDefyGravity()
    {
        return this.defyGravity;
    }
    
    /**
     * Determine if being stomped on is a weakness.<br>
     * @param weaknessStomp true the enemy can be hurt when stomped on, false the enemy will hurt the hero
     */
    protected void setWeaknessStomp(final boolean weaknessStomp)
    {
        this.weaknessStomp = weaknessStomp;
    }
    
    protected boolean hasWeaknessStomp()
    {
        return this.weaknessStomp;
    }
    
    protected void setType(final Enemies.Type type)
    {
        this.type = type;
    }
    
    public Enemies.Type getType()
    {
        return this.type;
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    /**
     * Here the a.i. logic will be ran
     * @param hero The hero object
     * @param level The level object
     * @param time The time duration to deduct per each update (nanoseconds)
     */
    protected abstract void update(final Hero hero, final Level level, final long time);
    
    /**
     * Handle how the enemy is hurt
     */
    protected abstract void markHurt();
    
    /**
     * Each enemy can have different logic for side collision
     * @param hero The hero we have collided with
     */
    protected abstract void checkSideCollision(final Hero hero);
    
    /**
     * Correct the y-coordinate when the animation changes
     * @param bottomY The bottom y where the enemy is
     */
    protected void correctY(final double bottomY)
    {
        setY(bottomY - getHeight());
    }
    
    @Override
    public void update(final Engine engine)
    {
        //update animation
        super.update(engine.getMain().getTime());
        
        //update based on velocity
        super.update();
        
        //update the projetiles
        super.updateProjectiles(engine.getMain().getTime(), engine.getManager().getLevel());
        
        //apply gravity to the enemies that are supposed to
        if (!canDefyGravity())
        {
            //apply gravity
            applyGravity(engine.getManager().getLevel().getTiles());
        }
        
        //adjust for scrolling
        setX(getX() + engine.getManager().getLevel().getScrollX());
        
        //don't continue if we are dead
        if (isDead())
            return;
        
        //get the hero
        final Hero hero = engine.getManager().getMario();
        
        //if the enemy has collision with the hero
        if (hasCollision(hero))
        {
            if (hero.isInvincible())
            {
                //hero is invincible so the enemy is dead
                markDead();
            }
            else
            {
                //make sure hero isn't already hurt
                if (!hero.isHurt())
                {
                    //check if this enemy was stomped on
                    if (checkCollisionNorth(hero) && hero.getVelocityY() > 0)
                    {
                        if (hasWeaknessStomp())
                        {
                            //make the hero bounce off the enemy
                            hero.setVelocityY(-hero.getVelocityY());
                            
                            //mark enemy as dead?
                            markHurt();
                        }
                        else
                        {
                            if (hasDamageCollision())
                            {
                                //the hero isn't supposed to jump on enemy
                                hero.flagDamage();
                            }
                            else
                            {
                                hero.setY(getY() - hero.getHeight());
                                hero.stopJump();
                                hero.setIdle(true);
                            }
                        }
                    }
                    else if (checkCollisionSouth(hero))
                    {
                        //enemy is on top of hero
                        if (hasDamageCollision())
                        {
                            hero.flagDamage();
                        }
                        
                        //correct position
                        hero.setY(getY() + getHeight());
                        hero.stopJump();
                    }
                    else if (checkCollisionEast(hero))
                    {
                        //check the side collision
                        checkSideCollision(hero);
                    }
                    else if (checkCollisionWest(hero))
                    {
                        //check the side collision
                        checkSideCollision(hero);
                    }
                }
            }
        }

        //check if any projectiles hit the hero
        hero.checkProjectileCollision(getProjectiles(), engine.getManager().getLevel().getEffects());
        
        //check if any of the hero projectiles hit the enemy
        checkProjectileCollision(hero.getProjectiles(), engine.getManager().getLevel().getEffects());

        //make sure enemy is still not dead
        if (!isDead())
        {
            //update ai logic
            update(hero, engine.getManager().getLevel(), engine.getMain().getTime());
        }
        else
        {
            //stop moving
            resetVelocity();
            
            //start jumping
            startJump();

            //once dead no enemy can defy gravity
            setDefyGravity(false);
            
            //turn enemy upside down
            setVerticalFlip(true);
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //draw enemy
        super.draw(graphics);
    }
}