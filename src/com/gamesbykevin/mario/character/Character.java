package com.gamesbykevin.mario.character;

import com.gamesbykevin.mario.entity.Entity;

public abstract class Character extends Entity
{
    //different actions for character
    private boolean attack = false, duck = false, run = false, walk = false, idle = true;
    
    //when we first jump the rate which we move
    public static final double DEFAULT_JUMP_VELOCITY = 6;
    
    //the speed which the character can walk/run
    public static final int DEFAULT_SPEED_WALK = 1;
    public static final int DEFAULT_SPEED_RUN = 2;
    
    private final double speedWalk;
    private final double speedRun;
    
    protected Character(final double jumpVelocity, final double speedWalk, final double speedRun)
    {
        super.setJumpVelocity(jumpVelocity);
        
        this.speedWalk = speedWalk;
        this.speedRun = speedRun;
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
    
    /**
     * Each character will need to manage their current animation
     */
    protected abstract void checkAnimation();
}