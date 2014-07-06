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
    //where gameplay occurs
    private Rectangle window;
    
    //our hero
    private Hero mario;
    
    //the world
    private World world;
    
    //manage keyboard input
    private Input input;
    
    /**
     * Constructor for Manager, this is the point where we load any menu option configurations
     * @param engine Engine for our game that contains all objects needed
     * @throws Exception 
     */
    public Manager(final Engine engine) throws Exception
    {
        //set the game window where game play will occur
        setWindow(engine.getMain().getScreen());

        //create new world
        this.world = new World();
        
        //create object to manage input
        this.input = new Input();
        
        //create new mario
        this.mario = new Mario(engine.getMain().getTime());
        this.mario.setImage(engine.getResources().getGameImage(GameImages.Keys.MarioSpriteSheet));
        this.mario.setGameOverImage(engine.getResources().getGameImage(GameImages.Keys.GameOverScreen));
        this.mario.createMiscImages();
        this.mario.setDimensions();

        //check the number of lives set
        switch (engine.getMenu().getOptionSelectionIndex(CustomMenu.LayerKey.Options, CustomMenu.OptionKey.Lives))
        {
            case 0:
                mario.setLives(5);
                break;
                
            case 1:
                mario.setLives(10);
                break;
                
            case 2:
                mario.setLives(33);
                break;
                
            case 3:
                mario.setLives(99);
                break;
        }
    }
    
    @Override
    public void reset(final Engine engine) throws Exception
    {
        //set world to reset
        getWorld().reset(engine.getRandom());
    }
    
    public Input getInput()
    {
        return this.input;
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
        if (window != null)
            window = null;
        
        if (input != null)
            input = null;
        
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
     * Update all elements
     * @param engine Our game engine
     * @throws Exception 
     */
    @Override
    public void update(final Engine engine) throws Exception
    {
        //only update if the hero has lives
        if (getMario().hasLives())
        {
            if (getWorld().isComplete())
            {
                getInput().update(engine);
                getWorld().update(engine);
                getMario().update(engine);
            }
            else
            {
                //update world generation
                getWorld().update(engine);

                //reset game if world generation is complete
                if (getWorld().isComplete())
                {
                    //position mario accordingly
                    getWorld().setStart(getMario());
                }
            }
        }
    }
    
    /**
     * Draw all of our application elements
     * @param graphics Graphics object used for drawing
     */
    @Override
    public void render(final Graphics graphics)
    {
        if (!getMario().hasLives())
        {
            getMario().render(graphics);
        }
        else
        {
            //draw world objects/creation progress
            getWorld().render(graphics);

            //is the world creation complete
            if (getWorld().isComplete())
            {
                //don't draw mario when we are playing the games
                if (!getWorld().isPlayingGame())
                {
                    getMario().render(graphics);
                }
            }
        }
    }
}