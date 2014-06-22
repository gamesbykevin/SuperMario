package com.gamesbykevin.mario.level;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.level.tiles.*;
import com.gamesbykevin.mario.level.powerups.PowerUps;
import com.gamesbykevin.mario.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Random;

public final class Level implements Disposable, IElement
{
    //list of tiles in the level
    private Tiles tiles;
    
    //used so we can determine what tiles need to be rendered
    private Rectangle boundary;
    
    //variables used for scrolling the tiles
    private double scrollX = 0;
    
    //the location where the level will start to be drawn on screen (north-west corner)
    public static final int LEVEL_START_X = 0;
    public static final int LEVEL_START_Y = 0;
    
    //how many tiles can appear on one screen
    public static final int LEVEL_COLUMNS_PER_SCREEN = 16;
    public static final int LEVEL_ROWS_PER_SCREEN = 12;
    
    //our background
    private Background background;
    
    //collection of power ups
    private PowerUps powerUps;
    
    //the effects
    private Effects effects;
    
    //is the level complete
    private boolean complete = false;
    
    //our level timer
    private Timer timer;
    
    //the default level duration is 5 minutes
    private static final long LEVEL_DURATION = Timers.toNanoSeconds(5);
    
    /**
     * Create a new level
     */
    public Level()
    {
        //create a new timer
        this.timer = new Timer(LEVEL_DURATION);
        
        //pause at first
        startTimer();
    }
    
    public void startTimer()
    {
        timer.setPause(false);
    }
    
    public void pauseTimer()
    {
        timer.setPause(true);
    }
    
    private void updateTimer(final long time)
    {
        timer.update(time);
        
        //don't let the timer go below 0
        if (timer.getRemaining() < 0)
            timer.setRemaining(0);
    }
    
    public boolean isComplete()
    {
        return this.complete;
    }
    
    public void setComplete(final boolean complete)
    {
        this.complete = complete;
    }
    
    /**
     * Set the scroll speed
     * @param scrollX The speed at which to move the x-coordinate 
     */
    public void setScrollX(final double scrollX)
    {
        this.scrollX = scrollX;
        
        //also set speed of background a little slower so there appears to be depth
        background.setVelocityX(scrollX / 2);
    }
    
    /**
     * Get the scroll speed
     * @return the x-coordinate speed to scroll
     */
    public double getScrollX()
    {
        return this.scrollX;
    }
    
    /**
     * Get the x-coordinate based on the specified column, the startX set for the level, and the tile width
     * @param column The column where we want the x-coordinate
     * @return The x-coordinate where this column starts
     */
    public int getX(final int column)
    {
        return (LEVEL_START_X + (column * Tile.WIDTH));
    }
    
    /**
     * Get the y-coordinate based on the specified row, the startY set for the level, and the tile height
     * @param row The row where we want the y-coordinate
     * @return The y-coordinate where this row starts
     */
    public int getY(final int row)
    {
        return (LEVEL_START_Y + (row * Tile.HEIGHT));
    }
    
    /**
     * Get the type of power up found to intersect with the entity and remove from power up list
     * @param entity The object we want to check for collision with power up
     * @return Type of power up found, if none found null is returned
     */
    public PowerUps.Type getPowerUpCollision(final Entity entity)
    {
        return powerUps.getCollision(entity);
    }
    
    private void createPowerUps(final Image image)
    {
        this.powerUps = new PowerUps(image);
    }
    
    public void createBackground(final Image image, final Random random)
    {
        //create a random background for now
        this.background = new Background(image, Background.Type.values()[random.nextInt(Background.Type.values().length)]);
    }
    
    public void createTiles(final int columns, final Image image, final Image powerUpImg, final Random random)
    {
        //create our power ups first
        createPowerUps(powerUpImg);
        
        //crete new tile container
        this.tiles = new Tiles(columns, Level.LEVEL_ROWS_PER_SCREEN, image);
        
        //populate the tiles
        this.tiles.populate(this, random);
    }
    
    public void createEffects(final Image image)
    {
        //create new object that will contain our effects
        this.effects = new Effects(image);
    }
    
    public Timer getTimer()
    {
        return this.timer;
    }
    
    public Background getBackground()
    {
        return this.background;
    }
    
    public PowerUps getPowerUps()
    {
        return this.powerUps;
    }
    
    public Tiles getTiles()
    {
        return this.tiles;
    }
    
    public Effects getEffects()
    {
        return this.effects;
    }
    
    public Rectangle getBoundary()
    {
        return this.boundary;
    }
    
    @Override
    public void dispose()
    {
        boundary = null;
        timer = null;
        
        if (tiles != null)
        {
            tiles.dispose();
            tiles = null;
        }
        
        if (effects != null)
        {
            effects.dispose();
            effects = null;
        }
        
        if (background != null)
        {
            background.dispose();
            background = null;
        }
        
        if (powerUps != null)
        {
            powerUps.dispose();
            powerUps = null;
        }
    }
    
    @Override
    public void update(final Engine engine)
    {
        //if the boundary is not set, get it
        if (getBoundary() == null)
            boundary = engine.getManager().getWindow();
        
        //update tiles
        tiles.update(engine.getMain().getTime(), getScrollX());
        
        //update power ups
        powerUps.update(engine.getMain().getTime(), getScrollX(), engine.getRandom(), getTiles());
        
        //update location
        background.update(getBoundary().x, getBoundary().x + getBoundary().width, engine.getMain().getTime());
        
        //update effects animation
        effects.update(engine.getMain().getTime(), getScrollX());
        
        //update timer
        updateTimer(engine.getMain().getTime());
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //draw the background first
        background.render(graphics);
        
        //draw background objects etc..
        tiles.renderNonSolidTiles(graphics, getBoundary());
        
        //draw the power ups
        powerUps.render(graphics);
        
        //draw objects the players can interact with (collision etc..)
        tiles.renderSolidTiles(graphics, getBoundary());
        
        //draw the effects
        effects.render(graphics);
    }
}