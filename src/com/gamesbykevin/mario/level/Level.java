package com.gamesbykevin.mario.level;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.engine.Engine;
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
    
    /**
     * Create a new level
     */
    public Level()
    {
        
    }
    
    /**
     * Set the scroll speed
     * @param scrollX The speed at which to move the x-coordinate 
     */
    public void setScrollX(final double scrollX)
    {
        this.scrollX = scrollX;
        
        //also set speed of background
        background.setVelocityX(scrollX);
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
        this.tiles.populate(this, powerUps, random);
    }
    
    public Tiles getTiles()
    {
        return this.tiles;
    }
    
    @Override
    public void dispose()
    {
        boundary = null;
        
        if (tiles != null)
        {
            tiles.dispose();
            tiles = null;
        }
    }
    
    @Override
    public void update(final Engine engine)
    {
        //if the boundary is not set, get it
        if (boundary == null)
            boundary = engine.getManager().getWindow();
        
        //update tiles
        tiles.update(engine.getMain().getTime(), scrollX);
        
        //update power ups
        powerUps.update(engine.getMain().getTime(), scrollX);
        
        //update location
        background.update(boundary.x, boundary.x + boundary.width);
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //draw the background first
        background.render(graphics);
        
        //draw the power ups
        powerUps.render(graphics);
        
        //then draw the tiles
        tiles.render(graphics, boundary);
    }
}