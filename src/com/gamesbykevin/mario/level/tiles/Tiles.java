package com.gamesbykevin.mario.level.tiles;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.level.Level;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

public final class Tiles implements Disposable
{
    //array of tiles in the level
    private Tile[][] tiles;
    
    //the image containing all tiles
    private Image image;
    
    public enum Type
    {
        QuestionBlock, UsedBlock, BreakableBrick, RotatingGear, Lava, 
        VerticalPipe1, VerticalPipe2, VerticalPipe3, VerticalPipe4, 
        VerticalPipe5, VerticalPipe6, VerticalPipe7, VerticalPipe8, 
    }
    
    public Tiles(final int columns, final int rows, final Image image)
    {
        //create new array of tiles
        this.tiles = new Tile[rows][columns];
        
        //set the tile sheet
        this.image = image;
    }
    
    public void update(final long time)
    {
        for (int row = 0; row < tiles.length; row++)
        {
            for (int col = 0; col < tiles[row].length; col++)
            {
                Tile tile = getTile(col, row);
                
                //if there is no tile here skip
                if (tile == null)
                    continue;
                
                //update animation
                tile.update(time);
                
                //move tile if velocity set
                tile.update();
            }
        }
    }
    
    public Tile getTile(final int column, final int row)
    {
        return this.tiles[row][column];
    }
    
    /**
     * Add this tile to the specified location.<br>
     * Will also setup animation etc...
     * @param type The type of tile we want
     * @param column Start column location of animation
     * @param row Start row location of animation
     * @param x x-coordinate where tile will be drawn
     * @param y y-coordinate where tile will be drawn
     */
    public void add(final Type type, final int column, final int row, final int x, final int y)
    {
        try
        {
            //tile object
            Tile tile;
            
            //each tile will have 1 animation
            switch (type)
            {
                case UsedBlock:
                    tile = new Tile(type);
                    tile.addAnimation(type, 1, getStartX(4), getStartY(0), Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), true);
                    tile.setDamage(false);
                    tile.setSolid(true);
                    tile.setLocation(x, y);
                    set(tile, column, row);
                    break;
                
                case Lava:
                    tile = new Tile(type);
                    tile.addAnimation(type, 4, getStartX(0), getStartY(9), Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), true);
                    tile.setDamage(true);
                    tile.setSolid(true);
                    tile.setLocation(x, y);
                    set(tile, column, row);
                    break;
                
                case QuestionBlock:
                    tile = new Tile(type);
                    tile.addAnimation(type, 4, getStartX(0), getStartY(0), Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(250L), true);
                    tile.setDamage(false);
                    tile.setSolid(true);
                    tile.setLocation(x, y);
                    set(tile, column, row);
                    break;
                    
                case BreakableBrick:
                    tile = new Tile(type);
                    tile.addAnimation(type, 4, getStartX(0), getStartY(1), Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(200L), true);
                    tile.setDamage(false);
                    tile.setSolid(true);
                    tile.setLocation(x, y);
                    set(tile, column, row);
                    break;
                    
                case RotatingGear:
                    tile = new Tile(type);
                    tile.addAnimation(type, 4, getStartX(0), getStartY(2), Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(250L), true);
                    tile.setDamage(true);
                    tile.setSolid(true);
                    tile.setLocation(x, y);
                    set(tile, column, row);
                    break;
                
                case VerticalPipe1:
                    addPipe(type, 0, 3, x, y, column, row);
                    break;
                    
                case VerticalPipe2:
                    addPipe(type, 2, 3, x, y, column, row);
                    break;
                    
                case VerticalPipe3:
                    addPipe(type, 4, 3, x, y, column, row);
                    break;
                    
                case VerticalPipe4:
                    addPipe(type, 0, 5, x, y, column, row);
                    break;
                    
                case VerticalPipe5:
                    addPipe(type, 2, 5, x, y, column, row);
                    break;
                    
                case VerticalPipe6:
                    addPipe(type, 4, 5, x, y, column, row);
                    break;
                    
                case VerticalPipe7:
                    addPipe(type, 0, 7, x, y, column, row);
                    break;
                    
                case VerticalPipe8:
                    addPipe(type, 2, 7, x, y, column, row);
                    break;
                    
                default:
                    throw new Exception("Tile not setup here: " + type.toString());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Add pipe to level
     * @param type The type of pipe
     * @param animationCol The start column of the pipe (north-west corner)
     * @param animationRow The start row of the pipe (north-west corner)
     * @param x x-coordinate where pipe will be drawn (north-west corner)
     * @param y y-coordinate where pipe will be drawn (north-west corner)
     * @param column Where to place this pipe in our tiles array
     * @param row Where to place this pipe in our tiles array
     */
    private void addPipe(final Type type, final int animationCol, final int animationRow, final int x, final int y, final int column, final int row)
    {
        Tile tile;
        
        //north west corner
        tile = new Tile(type);
        tile.addAnimation(type, 1, getStartX(animationCol), getStartY(animationRow), Tile.WIDTH, Tile.HEIGHT, 0, false);
        tile.setDamage(false);
        tile.setSolid(true);
        tile.setLocation(x, y);
        set(tile, column, row);
        
        //north east corner
        tile = new Tile(type);
        tile.addAnimation(type, 1, getStartX(animationCol + 1), getStartY(animationRow), Tile.WIDTH, Tile.HEIGHT, 0, false);
        tile.setDamage(false);
        tile.setSolid(true);
        tile.setLocation(x + Tile.WIDTH, y);
        set(tile, column + 1, row);
        
        //south east corner
        tile = new Tile(type);
        tile.addAnimation(type, 1, getStartX(animationCol + 1), getStartY(animationRow + 1), Tile.WIDTH, Tile.HEIGHT, 0, false);
        tile.setDamage(false);
        tile.setSolid(true);
        tile.setLocation(x + Tile.WIDTH, y + Tile.HEIGHT);
        set(tile, column + 1, row + 1);
        
        //south west corner
        tile = new Tile(type);
        tile.addAnimation(type, 1, getStartX(animationCol), getStartY(animationRow + 1), Tile.WIDTH, Tile.HEIGHT, 0, false);
        tile.setDamage(false);
        tile.setSolid(true);
        tile.setLocation(x, y + Tile.HEIGHT);
        set(tile, column, row + 1);
    }
    
    /**
     * Assign the tile to the array
     * @param tile The tile we want to assign
     * @param column The location in the array where we want to assign the tile
     * @param row The location in the array where we want to assign the tile
     */
    private void set(final Tile tile, final int column, final int row)
    {
        //set the tile in the array
        tiles[row][column] = tile;
    }
    
    @Override
    public void dispose()
    {
        if (tiles != null)
        {
            for (int row = 0; row < tiles.length; row++)
            {
                for (int col = 0; col < tiles[row].length; col++)
                {
                    if (tiles[row][col] != null)
                    {
                        tiles[row][col].dispose();
                        tiles[row][col] = null;
                    }
                }
            }
            
            tiles = null;
        }
    }
    
    /**
     * Determine where the x-coordinate is on the animation sprite sheet for this tile
     * @param col The column we are looking for
     * @return The x-coordinate based on the column
     */
    private int getStartX(final int col)
    {
        return (col * Tile.WIDTH);
    }
    
    /**
     * Determine where the y-coordinate is on the animation sprite sheet for this tile
     * @param row The row we are looking for
     * @return The y-coordinate based on the row
     */
    private int getStartY(final int row)
    {
        return (row * Tile.HEIGHT);
    }
    
    public void populate(final int startX, final int startY, final Level level)
    {
        for (int row = 0; row < tiles.length; row++)
        {
            for (int col = 0; col < tiles[row].length; col++)
            {
                //skip for now
                if (row < tiles.length - 2)
                    continue;
                
                //determine starting location of tile
                final int x = level.getX(col);
                final int y = level.getY(row);
                
                //add tile to array
                add(Type.UsedBlock, col, row, x, y);
            }
        }
    }
    
    public void render(final Graphics graphics, final Rectangle window)
    {
        for (int row = 0; row < tiles.length; row++)
        {
            for (int col = 0; col < tiles[row].length; col++)
            {
                Tile tile = getTile(col, row);
                
                //if there is no tile here skip
                if (tile == null)
                    continue;
                
                //don't render the tile if it is not on the screen
                if (tile.getX() < window.x || tile.getX() > window.x + window.width)
                    continue;
                if (tile.getY() < window.y || tile.getY() > window.y + window.height)
                    continue;
                
                //draw the tile
                tile.draw(graphics, image);
            }
        }
    }
}