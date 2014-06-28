package com.gamesbykevin.mario.character;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.world.level.Level;
import com.gamesbykevin.mario.projectiles.Projectile;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public abstract class Character extends Entity implements Disposable
{
    //different actions for character
    private boolean attack = false, duck = false, run = false, walk = false, idle = true;
    
    //when we first jump the rate which we move
    public static final double DEFAULT_JUMP_VELOCITY = 6;
    
    //the speed which the character can walk/run
    public static final double DEFAULT_SPEED_WALK = 1;
    public static final double DEFAULT_SPEED_RUN = 2;
    
    private final double speedWalk;
    private final double speedRun;
    
    //the number of projectiles that can be thrown at a time
    private int projectileLimit = 0;
    
    //the list of projectiles
    private List<Projectile> projectiles;
    
    //can this character be killed by a projectile
    private boolean weaknessProjectile = false;
    
    //by default the character won't be hurt
    private boolean hurt = false;
    
    //do we check if damage has been done
    private boolean damage = false;
    
    //by default the character won't be invincible
    private boolean invincible = false;
    
    protected Character(final double jumpVelocity, final double speedWalk, final double speedRun)
    {
        super.setJumpVelocity(jumpVelocity);
        
        this.speedWalk = speedWalk;
        this.speedRun = speedRun;
        
        //create container for projectiles
        this.projectiles = new ArrayList<>();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if (projectiles != null)
        {
            for (int i = 0; i < projectiles.size(); i++)
            {
                projectiles.get(i).dispose();
                projectiles.set(i, null);
            }
            
            projectiles.clear();
            projectiles = null;
        }
    }
    
    public double getSpeedWalk()
    {
        return this.speedWalk;
    }
    
    public double getSpeedRun()
    {
        return this.speedRun;
    }
    
    public void stopJumping()
    {
        //stop jumping
        setJump(false);
        
        if (isWalking())
        {
            setWalk(true);
        }
        else if (isRunning())
        {
            setRun(true);
        }
        else
        {
            setIdle(true);
            resetVelocity();
        }
    }
    
    public boolean isAttacking()
    {
        return this.attack;
    }
    
    public void setAttack(final boolean attack)
    {
        this.attack = attack;
        
        if (attack)
        {
            setIdle(false);
            setDuck(false);
        }
    }
    
    public boolean isDucking()
    {
        return this.duck;
    }
    
    public void setDuck(final boolean duck)
    {
        this.duck = duck;
        
        //if ducking we can't do these
        if (duck)
        {
            setAttack(false);
            setJump(false);
            setRun(false);
            setIdle(false);
        }
    }
    
    public boolean canDuck()
    {
        return (isWalking() || isRunning() || isIdle()) && !isJumping();
    }
    
    public boolean canJump()
    {
        return (isWalking() || isRunning() || isIdle()) && !isJumping();
    }
    
    public boolean isRunning()
    {
        return this.run;
    }
    
    public void setRun(final boolean run)
    {
        this.run = run;
        
        //if running we can't do these
        if (run)
        {
            setDuck(false);
            setIdle(false);
            setWalk(false);
        }
    }
    
    public boolean canRun()
    {
        return (isWalking() && !isJumping() && !isAttacking());
    }
    
    public boolean isWalking()
    {
        return this.walk;
    }
    
    public void setWalk(final boolean walk)
    {
        this.walk = walk;
        
        //if walking we can't do these
        if (walk)
        {
            setDuck(false);
            setIdle(false);
            setRun(false);
        }
    }
    
    public boolean canWalk()
    {
        return (isIdle());
    }
    
    public boolean isIdle()
    {
        return this.idle;
    }
    
    public void setIdle(final boolean idle)
    {
        this.idle = idle;
        
        //if idle we can't do these
        if (idle)
        {
            setAttack(false);
            setDuck(false);
            setJump(false);
            setRun(false);
            setWalk(false);
        }
    }
    
    public boolean isHurt()
    {
        return this.hurt;
    }
    
    protected void setHurt(final boolean hurt)
    {
        this.hurt = hurt;
    }
    
    /**
     * Flag damage so we can see if the hero has been hurt
     */
    public void flagDamage()
    {
        this.damage = true;
    }
    
    /**
     * We don't need to check for damage
     */
    public void unflagDamage()
    {
        this.damage = false;
    }
    
    protected boolean hasDamageCheck()
    {
        return this.damage;
    }
    
    public void setInvincible(final boolean invincible)
    {
        this.invincible = invincible;
        
        if (invincible)
        {
            setHurt(false);
        }
    }
    
    public boolean isInvincible()
    {
        return this.invincible;
    }
    
    /**
     * Determine if this enemy is weak towards a projectile.<br>
     * @param weaknessProjectile true the enemy can be hurt by a projectile, false otherwise
     */
    protected void setWeaknessProjectile(final boolean weaknessProjectile)
    {
        this.weaknessProjectile = weaknessProjectile;
    }
    
    public boolean hasWeaknessProjectile()
    {
        return this.weaknessProjectile;
    }
    
    /**
     * Set the number of projectiles an enemy can throw at once
     * @param projectileLimit The limit of projectiles
     */
    protected void setProjectileLimit(final int projectileLimit)
    {
        this.projectileLimit = projectileLimit;
    }
    
    public boolean canThrowProjectile()
    {
        return (projectileLimit > 0 && projectiles.size() < projectileLimit);
    }
    
    public final void addProjectile(final Projectile projectile)
    {
        //setup projectile
        projectile.setup(this);
        
        //add to list
        projectiles.add(projectile);
    }
    
    public List<Projectile> getProjectiles()
    {
        return this.projectiles;
    }
    
    /**
     * Determine if this character has been hit by any projectiles
     * @param projectiles The enemy projectiles in play
     * @param effects Object used to add effects to level
     */
    public void checkProjectileCollision(final List<Projectile> projectiles, final Effects effects)
    {
        for (int i = 0; i < projectiles.size(); i++)
        {
            Projectile projectile = projectiles.get(i);
            
            //if collision was returned true, we will remove the projectile
            if (projectile.checkCharacterCollision(this, effects))
            {
                //remove from list
                projectiles.remove(i);
                
                //move index back by 1
                i--;
            }
        }
    }
    
    protected void updateProjectiles(final long time, final Level level)
    {
        //don't continue if nothing exists
        if (getProjectiles().isEmpty())
            return;
        
        for (int i = 0; i < projectiles.size(); i++)
        {
            Projectile projectile = projectiles.get(i);
        
            //adjust for scrolling
            projectile.setX(projectile.getX() + level.getScrollX());
            
            //update animation
            projectile.update(time);

            //update location
            projectile.update();
            
            //update specific logic for this projectile
            projectile.updateLogic(level);
            
            //if it is now dead and not moving, remove from list
            if (projectile.isDead() && !projectile.hasVelocity())
            {
                //remove from list
                projectiles.remove(i);
                
                //move index back by 1
                i--;
            }
        }
    }
    
    /**
     * We need a method that will setup the animations
     */
    protected abstract void defineAnimations();
    
    /**
     * Draw the character's projectiles
     * @param graphics Graphics object used to write images
     * @param boundary The border used to determine if a projectile needs to be rendered, if null we won't check the boundary
     */
    public final void renderProjectiles(final Graphics graphics, final Rectangle boundary)
    {
        for (int i = 0; i < projectiles.size(); i++)
        {
            //get the current projectile
            Projectile projectile = projectiles.get(i);
            
            if (boundary != null)
            {
                //don't render if not on screen
                if (projectile.getX() + projectile.getWidth()  < boundary.x || projectile.getX() > boundary.x + boundary.width)
                    continue;
                if (projectile.getY() + projectile.getHeight() < boundary.y || projectile.getY() > boundary.y + boundary.height)
                    continue;
            }
            
            //draw projectile
            projectile.draw(graphics, getImage());
        }
    }
}