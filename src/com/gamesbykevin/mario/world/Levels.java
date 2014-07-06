package com.gamesbykevin.mario.world;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.resources.GameAudio;
import com.gamesbykevin.mario.resources.GameImages;
import com.gamesbykevin.mario.shared.IElement;
import com.gamesbykevin.mario.shared.IProgress;
import com.gamesbykevin.mario.world.level.Level;
import com.gamesbykevin.mario.world.map.Map;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Levels implements Disposable, IElement, IProgress
{
    //the levels in the world
    private List<Level> levels;
    
    //is creation complete
    private boolean complete = false;
    
    //the current level
    private int index = 0;
    
    //how many levels to create
    private int count;
    
    //how big and small the screens are
    private static final int LEVEL_SCREENS_MIN = 13;
    private static final int LEVEL_SCREENS_MAX = 20;
    
    //how many levels can be in the world
    private static final int LEVEL_COUNT_MIN = 5;
    private static final int LEVEL_COUNT_MAX = 10;
    
    //do we hide enemies
    private final boolean hide;
    
    //list of optional music for levels
    private List<GameAudio.Keys> options = new ArrayList<>();
    
    public Levels(final boolean hide)
    {
        //do we hide enemies
        this.hide = hide;
        
        //create new container for levels
        this.levels = new ArrayList<>();
    }
    
    @Override
    public void dispose()
    {
        if (levels != null)
        {
            for (int i = 0; i < levels.size(); i++)
            {
                levels.get(i).dispose();
                levels.set(i, null);
            }
            
            levels.clear();
            levels = null;
        }
    }

    /**
     * Get the current level
     * @return The current level
     */
    public Level getLevel()
    {
        return this.levels.get(index);
    }
    
    private boolean hasEnemiesHidden()
    {
        return this.hide;
    }
    
    /**
     * Set the current level by where the hero is located on the map.<br>
     * Also reset the level so all tiles are positioned back at start.
     * @param map World map
     */
    protected void setLevel(final Map map)
    {
        //set the current level
        this.index = map.getLevelIndex();
        
        //reset level
        getLevel().reset();
    }
    
    /**
     * Place the hero appropriately at the start of the level
     * @param hero The hero we want to place
     */
    protected void placeHero(final Hero hero)
    {
        //set the start location of the hero
        hero.setLocation(getLevel().getX(Level.LEVEL_COLUMNS_PER_SCREEN / 2), getLevel().getY(getLevel().getTiles().getFloorRow()) - hero.getHeight());
    }
    
    /**
     * Get the level count
     * @return The number of existing levels
     */
    protected int getCount()
    {
        return this.levels.size();
    }
    
    /**
     * Remove all existing levels and determine how many new levels will be added
     * @param random Object used to make random decisions
     */
    public void reset(final Random random)
    {
        //remove any existing levels
        this.levels.clear();
        
        //determine a random number of levels
        this.count = random.nextInt((LEVEL_COUNT_MAX+1) - LEVEL_COUNT_MIN) + LEVEL_COUNT_MIN;
        
        //mark as not complete
        this.setComplete(false);
        
        //clear list
        this.options.clear();
        
        //add optional music to list
        for (int i = 0; i < GameAudio.Keys.values().length; i++)
        {
            if (GameAudio.isLevelMusic(GameAudio.Keys.values()[i]))
                options.add(GameAudio.Keys.values()[i]);
        }
    }
    
    protected void addLevel(final Engine engine)
    {
        //create new level starting at specified location
        Level level = new Level(engine.getMain().getScreen());

        //determine the level size
        final int screens = engine.getRandom().nextInt(LEVEL_SCREENS_MAX - LEVEL_SCREENS_MIN) + LEVEL_SCREENS_MIN;

        //create tiles of specified size
        level.createTiles(
            Level.LEVEL_COLUMNS_PER_SCREEN * screens, 
            engine.getResources().getGameImage(GameImages.Keys.LevelTiles), 
            engine.getResources().getGameImage(GameImages.Keys.PowerUps), 
            engine.getRandom());
        
        //create the background for the level
        level.createBackground(engine.getResources().getGameImage(GameImages.Keys.LevelBackgrounds), engine.getRandom());
        
        //create special effects for dislay
        level.createEffects(engine.getResources().getGameImage(GameImages.Keys.Effects));
        
        //place enemies in level
        level.placeEnemies(engine.getResources().getGameImage(GameImages.Keys.Enemies), engine.getRandom());
        
        //pick random index for sound assignment
        final int i = engine.getRandom().nextInt(options.size());
        
        //assign background music to level
        level.assignBackgroundMusic(options.get(i));
        
        //determine if enemies are hidden or not
        if (hasEnemiesHidden())
        {
            level.hideEnemies();
        }
        else
        {
            level.showEnemies();
        }
        
        //add level to collection
        levels.add(level);
        
        //we haven't reached the level goal count yet
        if (getCount() < count)
        {
            //if there is at least more than 1 option left, then the randomly chosen can be removed
            if (options.size() > 1)
            {
                //remove music from list
                options.remove(i);
            }
        }
        else
        {
            //we reached our goal, the list can be cleared
            options.clear();
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
    
    @Override
    public void update(final Engine engine)
    {
        if (!isComplete())
        {
            //keep adding levels until we reach our limit
            if (getCount() < count)
            {
                //add level
                addLevel(engine);
            }
            else
            {
                //mark complete
                setComplete(true);
            }
        }
        else
        {
            if (getLevel() != null)
            {
                //update the current level
                getLevel().update(engine);
            }
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        if (getLevel() != null)
        {
            //draw the current level
            getLevel().render(graphics);
        }
    }
}