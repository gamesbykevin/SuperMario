package com.gamesbykevin.mario.level;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.level.tiles.*;
import com.gamesbykevin.mario.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public final class Level implements Disposable, IElement
{
    //list of tiles in the level
    private Tiles tiles;
    
    //the area where the level will be rendered to the user
    private Rectangle boundary;
    
    //the location in the northwest corner where the level will start
    private final int startX;
    private final int startY;
    
    /**
     * Create a new level
     * @param startX The x-coordinate where the level will start.
     * @param startY The y-coordinate where the level will start.
     */
    public Level(final int startX, final int startY)
    {
        this.startX = startX;
        this.startY = startY;
    }
    
    /**
     * Get the x-coordinate based on the specified column, the startX set for the level, and the tile width
     * @param column The column where we want the x-coordinate
     * @return The x-coordinate where this column starts
     */
    public int getX(final int column)
    {
        return (startX + (column * Tile.WIDTH));
    }
    
    /**
     * Get the y-coordinate based on the specified row, the startY set for the level, and the tile height
     * @param row The row where we want the y-coordinate
     * @return The y-coordinate where this row starts
     */
    public int getY(final int row)
    {
        return (startY + (row * Tile.HEIGHT));
    }
    
    public void createTiles(final int columns, final int rows, final Image image)
    {
        //crete new tile container
        this.tiles = new Tiles(columns, rows, image);
        
        //populate the tiles
        this.tiles.populate(startX, startY, this);
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
        tiles.update(engine.getMain().getTime());
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        if (boundary != null)
        {
            //draw the tiles
            tiles.render(graphics, boundary);
        }
    }
}