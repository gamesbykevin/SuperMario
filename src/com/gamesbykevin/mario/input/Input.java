package com.gamesbykevin.mario.input;

import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.world.map.Map.Selection;
import com.gamesbykevin.mario.projectiles.HeroFireball;
import com.gamesbykevin.mario.resources.GameAudio;

import java.awt.event.KeyEvent;

/**
 * This class will handle the input of the hero
 * @author GOD
 */
public final class Input 
{
    //the different key inputs
    public static final int KEY_MOVE_RIGHT = KeyEvent.VK_RIGHT;
    public static final int KEY_MOVE_LEFT  = KeyEvent.VK_LEFT;
    public static final int KEY_MOVE_DOWN  = KeyEvent.VK_DOWN;
    public static final int KEY_MOVE_UP    = KeyEvent.VK_UP;
    public static final int KEY_JUMP       = KeyEvent.VK_A;
    //public static final int KEY_RUN        = KeyEvent.VK_S;
    public static final int KEY_FIREBALL   = KeyEvent.VK_S;
    
    //time to wait until level appears
    private static final long ENTER_LEVEL_DELAY = Timers.toNanoSeconds(1250L);
    
    //timer to track switch
    private Timer timer;
    
    //the player selection
    private Selection selection = null;
    
    public Input()
    {
        this.timer = new Timer(ENTER_LEVEL_DELAY);
        this.timer.reset();
    }
    
    public void update(final Engine engine)
    {
        //get mario object
        final Hero mario = engine.getManager().getMario();
            
        //get keyboard input
        final Keyboard keyboard = engine.getKeyboard();
        
        //if the map is displayed we will move the hero accordingly
        if (engine.getManager().getWorld().getMap().isDisplayed())
        {
            //make sure hero isn't already moving
            if (mario.hasVelocity())
                return;
            
            //any one of these buttons can select the level
            if (keyboard.hasKeyReleased(KEY_JUMP) || 
                //keyboard.hasKeyReleased(KEY_RUN) || 
                keyboard.hasKeyReleased(KEY_FIREBALL))
            {
                //make sure we didn't already make a selection
                if (selection == null)
                {
                    //get the type of tile selected
                    selection = engine.getManager().getWorld().getMap().getSelection();

                    //if a selection was made
                    if (selection != null)
                    {
                        //stop all sound
                        engine.getResources().stopAllSound();

                        //play enter level sound effect
                        engine.getResources().playGameAudio(GameAudio.Keys.SfxMapEnter);
                    }
                }
            }
            else
            {
                //can only move if selection isn't already made
                if (selection == null)
                {
                    if (keyboard.hasKeyPressed(KEY_MOVE_RIGHT))
                    {
                        mario.setVelocityX(mario.getSpeedWalk());
                    }
                    else if (keyboard.hasKeyPressed(KEY_MOVE_LEFT))
                    {
                        mario.setVelocityX(-mario.getSpeedWalk());
                    }
                    else if (keyboard.hasKeyPressed(KEY_MOVE_UP))
                    {
                        mario.setVelocityY(-mario.getSpeedWalk());
                    }
                    else if (keyboard.hasKeyPressed(KEY_MOVE_DOWN))
                    {
                        mario.setVelocityY(mario.getSpeedWalk());
                    }

                    //check the direction/location of hero
                    engine.getManager().getWorld().getMap().checkHero(mario);
                }
            }

            if (selection != null)
            {
                //update timer
                timer.update(engine.getMain().getTime());

                //if time has passed make the switch
                if (timer.hasTimePassed())
                {
                    try
                    {
                        switch (selection)
                        {
                            case Level:
                                //we no longer are displaying the map
                                engine.getManager().getWorld().getMap().setDisplayed(false);

                                //set the appropriate level
                                engine.getManager().getWorld().setLevel();

                                //set audio to play
                                engine.getManager().getWorld().getLevels().getLevel().assignBackgroundMusic();
                                
                                //position the hero appropriately
                                engine.getManager().getWorld().setStart(mario);
                                break;

                            case SlotGame:
                                //start mushroom game
                                engine.getManager().getWorld().getSlot().setDisplayed(true);
                                
                                //we no longer are displaying the map
                                engine.getManager().getWorld().getMap().setDisplayed(false);
                                break;

                            case MatchingGame:
                                //start card matching game
                                engine.getManager().getWorld().getMatching().setDisplayed(true);
                                
                                //we no longer are displaying the map
                                engine.getManager().getWorld().getMap().setDisplayed(false);
                                break;

                            default:
                                throw new Exception("Selection not setup here: " + selection.toString());
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    //reset the timer
                    timer.reset();

                    //no longer set selection
                    selection = null;
                }
            }
            
            //reset key input
            engine.getKeyboard().reset();
            
            //no need to continue
            return;
        }
        else
        {
            //if playing a mini game we won't track input here
            if (engine.getManager().getWorld().isPlayingGame())
                return;
        }
        
        //if mario completed the level or has died, prevent movement
        if (mario.hasVictory() || mario.isDead())
            return;
        
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
                mario.setIdle(false);
            }
            else if (mario.canWalk())
            {
                mario.setVelocityX(mario.getSpeedWalk());
                mario.setHorizontalFlip(false);
                mario.setWalk(true);
            }
        }
        else if (keyboard.hasKeyPressed(KEY_MOVE_LEFT))
        {
            if (mario.isJumping())
            {
                mario.setVelocityX(mario.isRunning() ? -mario.getSpeedRun() : -mario.getSpeedWalk());
                mario.setHorizontalFlip(true);
                mario.setJump(true);
                mario.setIdle(false);
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
                
                //set direction to face
                mario.setHorizontalFlip(false);
                
                //if we aren't jumping set idle
                if (!mario.isJumping())
                    mario.setIdle(true);
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
                
                //set direction to face
                mario.setHorizontalFlip(true);
                
                //if we aren't jumping set idle
                if (!mario.isJumping())
                    mario.setIdle(true);
            }
        }
        
        if (keyboard.hasKeyPressed(KEY_JUMP))
        {
            if (mario.canJump())
            {
                //start jumping
                mario.startJump();
                
                //determine sound to play if hero is big or small
                mario.setAudioKey((mario.isBig()) ? GameAudio.Keys.SfxLevelJumpBig : GameAudio.Keys.SfxLevelJumpSmall);
            }
        }
        
        if (keyboard.hasKeyReleased(KEY_JUMP))
        {
            if (mario.isJumping() && mario.getVelocityY() < 0)
                mario.setVelocityY(-mario.getVelocityY());
        }
        
        /*
        if (keyboard.hasKeyPressed(KEY_RUN))
        {
            if (mario.canRun())
            {
                mario.resetVelocity();
                mario.setVelocityX((!mario.hasHorizontalFlip()) ? mario.getSpeedRun() : -mario.getSpeedRun());
                mario.setRun(true);
            }
        }
        */
        
        /*
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
        */
        
        if (keyboard.hasKeyPressed(KEY_FIREBALL))
        {
            if (!mario.isDucking() && mario.hasFire() && mario.canThrowProjectile())
            {
                //start to attack
                mario.setAttack(true);
            }
        }
        
        //only attack when key is released
        if (keyboard.hasKeyReleased(KEY_FIREBALL))
        {
            if (mario.isAttacking())
            {
                //no longer attacking
                mario.setAttack(false);
                
                //add fireball
                mario.addProjectile(new HeroFireball());
                
                //set sound effect to play
                mario.setAudioKey(GameAudio.Keys.SfxLevelHeroFireball);
                
                //make sure mario can walk afterwards
                if (!mario.isWalking() || !mario.isJumping() || !mario.isRunning())
                    mario.setIdle(true);
            }
        }
        
        //manage keyboard input
        manageKeyboard(keyboard);
    }
    
    public static void manageKeyboard(final Keyboard keyboard)
    {
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
        
        /*
        if (keyboard.hasKeyReleased(KEY_RUN))
        {
            keyboard.removeKeyPressed(KEY_RUN);
            keyboard.removeKeyReleased(KEY_RUN);
        }
        */
        
        if (keyboard.hasKeyReleased(KEY_FIREBALL))
        {
            keyboard.removeKeyPressed(KEY_FIREBALL);
            keyboard.removeKeyReleased(KEY_FIREBALL);
        }
    }
}