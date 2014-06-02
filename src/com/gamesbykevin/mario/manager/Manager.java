package com.gamesbykevin.mario.manager;

import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.heroes.*;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.menu.CustomMenu;
import com.gamesbykevin.mario.menu.CustomMenu.*;
import com.gamesbykevin.mario.resources.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The parent class that contains all of the game elements
 * @author GOD
 */
public final class Manager implements IManager
{
    //the area where gameplay will occur
    private Rectangle window;
    
    //object representing our hero
    private Hero mario;
    
    //the level
    private Level level;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine Engine for our game that contains all objects needed
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //set the game window where game play will occur
        setWindow(engine.getMain().getScreen());

        //get the menu object
        //final Menu menu = engine.getMenu();

        //get index of option selected
        //menu.getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Lives)
                
        //create new game
        reset(engine);
    }
    
    @Override
    public void reset(final Engine engine) throws Exception
    {
        this.level = new Level(0, -192);
        this.level.createTiles(32, 24, engine.getResources().getGameImage(GameImages.Keys.LevelTiles));
        
        this.mario = new Mario();
        this.mario.setImage(engine.getResources().getGameImage(GameImages.Keys.MarioSpriteSheet));
        this.mario.setDimensions();
        
        //set the start location of the hero
        this.mario.setLocation(level.getX(2), level.getY(22) - mario.getHeight());
    }
    
    @Override
    public Rectangle getWindow()
    {
        return this.window;
    }
    
    @Override
    public void setWindow(final Rectangle window)
    {
        this.window = new Rectangle(window);
    }
    
    /**
     * Free up resources
     */
    @Override
    public void dispose()
    {
        if (getWindow() != null)
            setWindow(null);
        
        if (mario != null)
        {
            mario.dispose();
            mario = null;
        }
        
        if (level != null)
        {
            level.dispose();
            level = null;
        }
    }
    
    /**
     * Update all application elements
     * 
     * @param engine Our main game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        if (mario != null)
        {
            mario.update(engine);
        }
        
        if (level != null)
        {
            level.update(engine);
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        if (mario != null)
        {
            mario.render(graphics);
        }
        
        if (level != null)
        {
            level.render(graphics);
        }
    }
}