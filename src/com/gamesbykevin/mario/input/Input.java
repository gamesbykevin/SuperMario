package com.gamesbykevin.mario.input;

import com.gamesbykevin.framework.input.Keyboard;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.heroes.Hero;

import java.awt.event.KeyEvent;

/**
 * This class will handle the input of the hero
 * @author GOD
 */
public final class Input 
{
    //the different key inputs
    private static final int KEY_MOVE_RIGHT = KeyEvent.VK_RIGHT;
    private static final int KEY_MOVE_LEFT  = KeyEvent.VK_LEFT;
    private static final int KEY_MOVE_DOWN  = KeyEvent.VK_DOWN;
    private static final int KEY_JUMP       = KeyEvent.VK_A;
    private static final int KEY_RUN        = KeyEvent.VK_S;
    private static final int KEY_FIREBALL   = KeyEvent.VK_D;
    
    public void update(final Engine engine)
    {
        //get mario object
        final Hero mario = engine.getManager().getMario();
        
        //get keyboard input
        final Keyboard keyboard = engine.getKeyboard();
        
        if (keyboard.hasKeyPressed(KEY_MOVE_DOWN))
        {
            if (mario.canDuck() && (mario.isBig() || mario.hasFire()))
            {
                mario.resetVelocity();
                mario.setDuck(true);
            }
        }
        
        if (keyboard.hasKeyReleased(KEY_MOVE_DOWN))
        {
            if (mario.isDucking())
            {
                mario.resetVelocity();
                mario.setIdle(true);
            }
        }
        
        if (keyboard.hasKeyPressed(KEY_MOVE_RIGHT))
        {
            if (mario.isJumping())
            {
                mario.setVelocityX(mario.isRunning() ? mario.getSpeedRun() : mario.getSpeedWalk());
                mario.setHorizontalFlip(false);
                mario.setJump(true);
            }
            else if (mario.canWalk())
            {
                mario.setVelocityX(mario.getSpeedWalk());
                mario.setHorizontalFlip(false);
                mario.setWalk(true);
            }
        }
        
        if (keyboard.hasKeyPressed(KEY_MOVE_LEFT))
        {
            if (mario.isJumping())
            {
                mario.setVelocityX(mario.isRunning() ? -mario.getSpeedRun() : -mario.getSpeedWalk());
                mario.setHorizontalFlip(true);
                mario.setJump(true);
            }
            else if (mario.canWalk())
            {
                mario.setVelocityX(-mario.getSpeedWalk());
                mario.setHorizontalFlip(true);
                mario.setWalk(true);
            }
        }
        
        if (keyboard.hasKeyReleased(KEY_MOVE_RIGHT))
        {
            if (mario.isJumping() || mario.isWalking() || mario.isRunning())
            {
                mario.resetVelocityX();
                
                if (mario.isWalking() || mario.isRunning())
                {
                    mario.setWalk(false);
                    mario.setRun(false);
                }
                
                if (!mario.isJumping())
                {
                    mario.setHorizontalFlip(false);
                    mario.setIdle(true);
                }
            }
        }
        
        if (keyboard.hasKeyReleased(KEY_MOVE_LEFT))
        {
            if (mario.isJumping() || mario.isWalking() || mario.isRunning())
            {
                mario.resetVelocityX();
                
                if (mario.isWalking() || mario.isRunning())
                {
                    mario.setWalk(false);
                    mario.setRun(false);
                }
                
                if (!mario.isJumping())
                {
                    mario.setHorizontalFlip(true);
                    mario.setIdle(true);
                }
            }
        }
        
        if (keyboard.hasKeyPressed(KEY_JUMP))
        {
            if (mario.canJump())
            {
                mario.startJump();
                mario.setJump(true);
            }
        }
        
        if (keyboard.hasKeyPressed(KEY_FIREBALL))
        {
            if (!mario.isDucking() && mario.hasFire())
            {
                mario.setAttack(true);
            }
        }
        
        if (keyboard.hasKeyPressed(KEY_RUN))
        {
            if (mario.canRun())
            {
                mario.resetVelocity();
                mario.setVelocityX((!mario.hasHorizontalFlip()) ? mario.getSpeedRun() : -mario.getSpeedRun());
                mario.setRun(true);
            }
        }
        
        if (keyboard.hasKeyReleased(KEY_RUN))
        {
            if (mario.isRunning())
            {
                //if we are moving then we now walk
                if (mario.hasVelocityX())
                {
                    //set the appropriate walking velocity
                    mario.setVelocityX((mario.getVelocityX() > 0) ? mario.getSpeedWalk() : -mario.getSpeedWalk());
                    mario.setWalk(true);
                }
                else
                {
                    mario.resetVelocityX();
                    mario.setIdle(true);
                }
            }
        }
        
        /**
         * Remove any keys we are no longer using
         */
        if (keyboard.hasKeyReleased(KEY_MOVE_RIGHT))
        {
            keyboard.removeKeyPressed(KEY_MOVE_RIGHT);
            keyboard.removeKeyReleased(KEY_MOVE_RIGHT);
        }
        
        if (keyboard.hasKeyReleased(KEY_MOVE_LEFT))
        {
            keyboard.removeKeyPressed(KEY_MOVE_LEFT);
            keyboard.removeKeyReleased(KEY_MOVE_LEFT);
        }
        
        if (keyboard.hasKeyReleased(KEY_MOVE_DOWN))
        {
            keyboard.removeKeyPressed(KEY_MOVE_DOWN);
            keyboard.removeKeyReleased(KEY_MOVE_DOWN);
        }
        
        if (keyboard.hasKeyReleased(KEY_JUMP))
        {
            keyboard.removeKeyPressed(KEY_JUMP);
            keyboard.removeKeyReleased(KEY_JUMP);
        }
        
        if (keyboard.hasKeyReleased(KEY_RUN))
        {
            keyboard.removeKeyPressed(KEY_RUN);
            keyboard.removeKeyReleased(KEY_RUN);
        }
        
        if (keyboard.hasKeyReleased(KEY_FIREBALL))
        {
            keyboard.removeKeyPressed(KEY_FIREBALL);
            keyboard.removeKeyReleased(KEY_FIREBALL);
        }
    }
}