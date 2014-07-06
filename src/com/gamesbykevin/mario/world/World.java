package com.gamesbykevin.mario.world;

import com.gamesbykevin.framework.util.*;
import com.gamesbykevin.framework.resources.Progress;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.game.matching.Matching;
import com.gamesbykevin.mario.game.slot.Slot;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.resources.GameFont;
import com.gamesbykevin.mario.shared.IElement;
import com.gamesbykevin.mario.shared.IProgress;
import com.gamesbykevin.mario.resources.GameAudio;
import com.gamesbykevin.mario.resources.GameImages;
import com.gamesbykevin.mario.shared.Shared;
import com.gamesbykevin.mario.world.level.hud.Hud;
import com.gamesbykevin.mario.world.map.Map;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Random;

public final class World implements Disposable, IElement, IProgress
{
    //the container for the levels
    private Levels levels;
    
    //card matching game
    private Matching matching;
    
    //slot game
    private Slot slot;
    
    //the visual map
    private Map map;
    
    //heads-up-display
    private Hud hud;
    
    //the number of bonus stages per world
    public static final int BONUS_STAGES = 2;
    
    //are all objects setup
    private boolean complete = false;
    
    //track progress of creation
    private Progress progress;
    
    //the timer to track time until new world is created
    private Timer timer;
    
    //the delay before creating a new world
    private static final long WORLD_COMPLETE_DELAY = Timers.toNanoSeconds(8000L);
    
    public World()
    {
        try
        {
            //create new timer
            this.timer = new Timer(WORLD_COMPLETE_DELAY);
            
            //we have 4 different things to reset to create the world
            this.progress = new Progress(4);
            this.progress.setDescription("Creating World");
            this.progress.setScreen(new Rectangle(0, 0, (int)(Shared.ORIGINAL_WIDTH * .55), (int)(Shared.ORIGINAL_HEIGHT * .75)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public boolean isComplete()
    {
        return this.complete;
    }
    
    @Override
    public void setComplete(final boolean complete)
    {
        this.complete = complete;
    }
    
    public boolean isPlayingGame()
    {
        return (slot.isDisplayed() || matching.isDisplayed());
    }
    
    /**
     * Set the hero at the beginning of a level or on the mini-map.<br>
     * This will depend if the mini-map is currently displayed.<br>
     * This will also stop the hero velocity
     * @param hero The hero we want to place at the start of level
     */
    public void setStart(final Hero hero)
    {
        if (getMap().isDisplayed())
        {
            //position on the map
            getMap().resetHero(hero);
        }
        else
        {
            //set the start location of the hero
            getLevels().placeHero(hero);
        }
        
        //stop moving
        hero.resetVelocity();
    }
    
    /**
     * Set the current level by where the hero is located on the map
     */
    public void setLevel()
    {
        getLevels().setLevel(getMap());
    }
    
    @Override
    public void dispose()
    {
        if (levels != null)
        {
            levels.dispose();
            levels = null;
        }
        
        if (map != null)
        {
            map.dispose();
            map = null;
        }
        
        if (hud != null)
        {
            hud.dispose();
            hud = null;
        }
        
        if (slot != null)
        {
            slot.dispose();
            slot = null;
        }
        
        if (matching != null)
        {
            matching.dispose();
            matching = null;
        }
    }
    
    /**
     * Get the hud display
     * @return The heads up display
     */
    public Hud getHud()
    {
        return this.hud;
    }
    
    public Map getMap()
    {
        return this.map;
    }
    
    public Slot getSlot()
    {
        return this.slot;
    }
    
    public Levels getLevels()
    {
        return this.levels;
    }
    
    public Matching getMatching()
    {
        return this.matching;
    }
    
    public void reset(final Random random)
    {
        //reset timer
        timer.reset();
        
        //flag as not complete
        setComplete(false);
        
        //re-create all levels
        getLevels().reset(random);
        
        //flag as not complete
        getMap().setComplete(false);
        
        //reset
        getSlot().setReset();
        
        //reset
        getMatching().setReset();
        
        //reset progress
        progress.setCount(0);
    }
    
    public void setup(final Engine engine)
    {
        if (getHud() == null)
        {
            //create new heads up display
            hud = new Hud(
                engine.getResources().getGameImage(GameImages.Keys.Hud), 
                engine.getMain().getScreen(),
                engine.getResources().getFont(GameFont.Keys.Hud));
        }
        
        if (getLevels() == null || !getLevels().isComplete())
        {
            if (getLevels() == null)
            {
                final boolean hideEnemies = false;
                
                //create new levels object
                levels = new Levels(hideEnemies);
                
                //re-create all levels
                getLevels().reset(engine.getRandom());
            }
            else
            {
                //create levels if not complete yet
                getLevels().update(engine);
            }
            
            //increase our progress bar
            progress.increase();
        }
        else if (getMap() == null || !getMap().isComplete())
        {
            if (getMap() == null)
            {
                //create the world map
                map = new Map(
                    engine.getResources().getGameImage(GameImages.Keys.WorldMap), 
                    engine.getResources().getFont(GameFont.Keys.Map)
                    );
            }
            else
            {
                //reset new map
                getMap().reset(engine.getRandom(), getLevels().getCount());
            }
            
            //increase our progress bar
            progress.increase();
        }
        else if (getSlot() == null || getSlot().hasReset())
        {
            if (getSlot() == null)
            {
                //create slot game
                slot = new Slot(engine.getResources().getGameImage(GameImages.Keys.GameSlot));
            }
            else
            {
                //reset game
                getSlot().reset(engine.getRandom());
            }
            
            //increase our progress bar
            progress.increase();
        }
        else if (getMatching() == null || getMatching().hasReset())
        {
            if (getMatching() == null)
            {
                //create matching game
                matching = new Matching(engine.getResources().getGameImage(GameImages.Keys.GameCardMatching));
            }
            else
            {
                //reset game
                getMatching().reset(engine.getRandom());
            }
            
            //increase our progress bar
            progress.increase();
        }
        else
        {
            //everything is setup mark complete
            setComplete(true);
            
            //mark as complete
            progress.setComplete();
        }
    }
    
    @Override
    public void update(final Engine engine)
    {
        if (!isComplete())
        {
            //update creation
            setup(engine);
        }
        else
        {
            //update the map
            if (getMap().isDisplayed())
            {
                getMap().update(engine);
                
                //if the map is solved
                if (getMap().hasSolved())
                {
                    //prevent mario from moving
                    engine.getManager().getMario().resetVelocity();
                    
                    //reset input
                    engine.getKeyboard().reset();
                    
                    //update timer
                    timer.update(engine.getMain().getTime());
                    
                    //if time has passed we need to create a new world
                    if (timer.hasTimePassed())
                    {
                        //stop all sound
                        engine.getResources().stopAllSound();
                        
                        //mark to reset
                        reset(engine.getRandom());
                        
                        //increase the world number
                        getMap().nextWorld();
                    }
                    
                    return;
                }
            }
            else
            {
                //if playing slot game
                if (getSlot().isDisplayed())
                {
                    getSlot().update(engine);
                }
                else if (getMatching().isDisplayed())
                {
                    getMatching().update(engine);
                }
                else
                {
                    //update the current level
                    if (getLevels() != null)
                        getLevels().update(engine);

                    //update the h.u.d.
                    if (getHud() != null)
                        getHud().update(engine);
                }
            }
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        if (!isComplete())
        {
            //draw progress
            progress.render(graphics);
        }
        else
        {
            if (getMap().isDisplayed())
            {
                //draw the map
                getMap().render(graphics);
            }
            else
            {
                //if playing slot game
                if (getSlot().isDisplayed())
                {
                    getSlot().render(graphics);
                }
                else if (getMatching().isDisplayed())
                {
                    getMatching().render(graphics);
                }
                else
                {
                    //draw the current level
                    if (getLevels() != null)
                        getLevels().render(graphics);

                    //draw h.u.d.
                    if (getHud() != null)
                        getHud().render(graphics);
                }
            }
        }
    }
}