package com.gamesbykevin.mario.heroes;

public final class Mario extends Hero
{
    private static final int SMALL_MARIO_WIDTH = 16;
    private static final int SMALL_MARIO_HEIGHT = 16;
    
    private static final int BIG_MARIO_WIDTH = 16;
    private static final int BIG_MARIO_HEIGHT = 31;
    
    private static final int FIREBALL_WIDTH = 8;
    private static final int FIREBALL_HEIGHT = 9;
    
    /**
     * here we will setup the animations
     */
    public Mario()
    {
        //small mario on the mini map
        super.addAnimation(State.SmallMiniMap, 2, 0, 0, SMALL_MARIO_WIDTH, SMALL_MARIO_HEIGHT, DEFAULT_DELAY, true);
        
        //small mario standing idle
        super.addAnimation(State.SmallIdle, 1, 32, 0, SMALL_MARIO_WIDTH, SMALL_MARIO_HEIGHT, DEFAULT_DELAY, false);
        
        //small mario walking
        super.addAnimation(State.SmallWalk, 2, 32, 0, SMALL_MARIO_WIDTH, SMALL_MARIO_HEIGHT, DEFAULT_DELAY, true);
        
        //small mario running, will be same as walking but move between frames faster
        super.addAnimation(State.SmallRun, 2, 32, 0, SMALL_MARIO_WIDTH, SMALL_MARIO_HEIGHT, DEFAULT_DELAY / 3, true);
        
        //small mario jumping
        super.addAnimation(State.SmallJump, 2, 64, 0, SMALL_MARIO_WIDTH, SMALL_MARIO_HEIGHT, DEFAULT_DELAY / 2, false);
        
        //small mario dead
        super.addAnimation(State.SmallDeath, 1, 112, 0, SMALL_MARIO_WIDTH, SMALL_MARIO_HEIGHT, DEFAULT_DELAY, false);
        
        
        //big mario on the mini map
        super.addAnimation(State.BigMiniMap, 2, 0, 16, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY, true);
        
        //big mario standing idle
        super.addAnimation(State.BigIdle, 1, 32, 16, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY, false);
        
        //big mario walking
        super.addAnimation(State.BigWalk, 2, 32, 16, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY, true);
        
        //big mario running, will be same as walking but move between frames faster
        super.addAnimation(State.BigRun, 2, 32, 16, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY / 3, true);
        
        //big mario jumping
        super.addAnimation(State.BigJump, 2, 64, 16, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY / 2, false);
        
        //big mario ducking
        super.addAnimation(State.BigDuck, 1, 112, 16, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY, false);
        
        
        //fire mario on the mini map
        super.addAnimation(State.FireMiniMap, 2, 0, 47, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY, true);
        
        //fire mario standing idle
        super.addAnimation(State.FireIdle, 1, 32, 47, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY, false);
        
        //fire mario walking
        super.addAnimation(State.FireWalk, 2, 32, 47, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY, true);
        
        //fire mario running, will be same as walking but move between frames faster
        super.addAnimation(State.FireRun, 2, 32, 47, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY / 3, true);
        
        //fire mario jumping
        super.addAnimation(State.FireJump, 2, 64, 47, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY / 2, false);
        
        //fire mario ducking
        super.addAnimation(State.FireDuck, 1, 112, 47, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY, false);
        
        //fire mario attacking
        super.addAnimation(State.FireAttack, 1, 128, 47, BIG_MARIO_WIDTH, BIG_MARIO_HEIGHT, DEFAULT_DELAY * 2, false);
        
        
        //fireball
        super.addAnimation(State.Fireball, 4, 0, 80, FIREBALL_WIDTH, FIREBALL_HEIGHT, DEFAULT_DELAY, true);
        
        
        //set default animation
        super.setAnimation(getDefaultAnimation(), true);
    }
}