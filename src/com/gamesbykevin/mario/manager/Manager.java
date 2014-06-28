package com.gamesbykevin.mario.manager;

import com.gamesbykevin.framework.menu.Menu;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.mario.effects.*;
import com.gamesbykevin.mario.enemies.Enemies;
import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.heroes.*;
import com.gamesbykevin.mario.input.Input;
import com.gamesbykevin.mario.world.World;
import com.gamesbykevin.mario.world.level.hud.Hud;
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
    
    //the world
    private World world;
    
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

        //get the game font
        this.font = engine.getResources().getFont(GameFont.Keys.GameFont).deriveFont(12f);
        
        
        
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
        //create new world
        if (this.world == null)
        {
            //create new world
            this.world = new World();
        }
        
        //add levels to world
        this.world.addLevels(engine);
        
        //create new mario
        if (this.mario == null)
        {
            this.mario = new Mario();
            this.mario.setImage(engine.getResources().getGameImage(GameImages.Keys.MarioSpriteSheet));
            this.mario.createMiscImages();
            this.mario.setDimensions();
        }
        
        //set the hero to start at beginning of current level
        this.world.setStart(mario);
        
        //create new object to manage input
        this.input = new Input();
    }
    
    public Hero getMario()
    {
        return this.mario;
    }
    
    public World getWorld()
    {
        return this.world;
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
        
        if (world != null)
        {
            world.dispose();
            world = null;
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
        
        if (world != null)
        {
            world.update(engine);
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
        
        if (world != null)
        {
            world.render(graphics);
        }
        
        if (mario != null)
        {
            mario.render(graphics);
        }
    }
}