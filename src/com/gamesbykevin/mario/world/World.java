package com.gamesbykevin.mario.world;

import com.gamesbykevin.mario.world.map.Map;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.resources.GameImages;
import com.gamesbykevin.mario.world.level.hud.Hud;
import com.gamesbykevin.mario.world.level.Level;
import com.gamesbykevin.mario.shared.IElement;

import java.awt.Graphics;

import java.util.ArrayList;
import java.util.List;

public final class World implements Disposable, IElement
{
    //the levels in the world
    private List<Level> levels;
    
    //the current level
    private int index = 0;
    
    //the visual map
    private Map map;
    
    //heads-up-display
    private Hud hud;
    
    //how many levels can be in the world
    private static final int LEVEL_COUNT_MIN = 5;
    private static final int LEVEL_COUNT_MAX = 10;
    
    //how big and small the screens are
    private static final int LEVEL_SCREENS_MIN = 6;
    private static final int LEVEL_SCREENS_MAX = 15;
    
    public World()
    {
        this.levels = new ArrayList<>();
    }
    
    /**
     * Add levels to the world, any previously existed levels will be removed
     * @param engine Object that contains everything we need
     */
    public void addLevels(final Engine engine)
    {
        //remove any existing levels
        this.levels.clear();
        
        //determine a random number of levels
        final int count = engine.getRandom().nextInt(LEVEL_COUNT_MAX - LEVEL_COUNT_MIN) + LEVEL_COUNT_MIN;
        
        for (int i = 0; i < count; i++)
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
            level.createBackground(engine.getResources().getGameImage(GameImages.Keys.LevelBackgrounds), engine.getRandom());
            level.createEffects(engine.getResources().getGameImage(GameImages.Keys.Effects));
            level.placeEnemies(engine.getResources().getGameImage(GameImages.Keys.Enemies), engine.getRandom());
            
            //add level to collection
            levels.add(level);
        }
        
        //create hud
        if (getHud() == null)
        {
            //create new heads up display
            this.hud = new Hud(engine.getResources().getGameImage(GameImages.Keys.Hud), engine.getMain().getScreen());
        }
    }
    
    /**
     * Set the hero start at the beginning of a level
     * @param hero The hero we want to place at the start of level
     */
    public void setStart(final Hero hero)
    {
        //set the start location of the hero
        hero.setLocation(getLevel().getX(4), getLevel().getY(getLevel().getTiles().getFloorRow()) - hero.getHeight());
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
    }
    
    /**
     * Get the current level
     * @return The current level
     */
    public Level getLevel()
    {
        return this.levels.get(index);
    }
    
    /**
     * Get the hud display
     * @return The heads up display
     */
    public Hud getHud()
    {
        return this.hud;
    }
    
    @Override
    public void update(final Engine engine)
    {
        if (getLevel() != null)
        {
            //update the level
            getLevel().update(engine);
        }
        
        if (getHud() != null)
        {
            getHud().update(engine);
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        if (getLevel() != null)
        {
            //draw the level
            getLevel().render(graphics);
        }
        
        if (getHud() != null)
        {
            getHud().render(graphics);
        }
    }
}