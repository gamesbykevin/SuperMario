package com.gamesbykevin.mario.manager;

import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.mario.effects.*;
import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.heroes.*;
import com.gamesbykevin.mario.input.Input;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.menu.CustomMenu;
import com.gamesbykevin.mario.menu.CustomMenu.*;
import com.gamesbykevin.mario.resources.*;

import java.awt.Color;
import java.awt.Font;
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
    
    //here we will manage the keyboard input in the game
    private Input input;
    
    //the game font
    private Font font;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine Engine for our game that contains all objects needed
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //set the game window where game play will occur
        setWindow(engine.getMain().getScreen());

        this.font = engine.getResources().getFont(GameFont.Keys.GameFont);
        
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
        //create new level starting at specified location
        this.level = new Level();
        
        //create tiles of specified size
        this.level.createTiles(
            Level.LEVEL_COLUMNS_PER_SCREEN * 16, 
            engine.getResources().getGameImage(GameImages.Keys.LevelTiles), 
            engine.getResources().getGameImage(GameImages.Keys.PowerUps), 
            engine.getRandom());
        this.level.createBackground(engine.getResources().getGameImage(GameImages.Keys.LevelBackgrounds), engine.getRandom());
        this.level.createEffects(engine.getResources().getGameImage(GameImages.Keys.Effects));
        
        //create new mario
        this.mario = new Mario();
        this.mario.setImage(engine.getResources().getGameImage(GameImages.Keys.MarioSpriteSheet));
        this.mario.createMiscImages();
        this.mario.setDimensions();
        
        //set the start location of the hero
        this.mario.setLocation(level.getX(4), level.getY(level.getTiles().getFloorRow()) - mario.getHeight());
        
        //create new object to manage input
        this.input = new Input();
    }
    
    public Hero getMario()
    {
        return this.mario;
    }
    
    public Level getLevel()
    {
        return this.level;
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
            this.window = null;
        
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
        if (input != null)
        {
            input.update(engine);
        }
        
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
        //set the font
        graphics.setFont(font);
        
        if (level != null)
        {
            level.render(graphics);
        }
        
        if (mario != null)
        {
            mario.render(graphics);
        }
    }
}