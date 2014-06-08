package com.gamesbykevin.mario.level.tiles;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.LevelCreatorHelper;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Random;

public final class Tiles implements Disposable
{
    //array of tiles in the level
    private Tile[][] tiles;
    
    //the image containing all tiles
    private Image image;
    
    public enum Type
    {
        Empty(1,1), 
        
        QuestionBlock(1,1), UsedBlock(1,1), BreakableBrick(1,1), RotatingGear(1,1), 
        Lava(1,1), Water1(1,1), Water2(1,1), 
        BigBlock(2,2), BiggerBlock1(3,3), BiggerBlock2(3,3), BiggerBlock3(3,3), BiggerBlock4(3,3), 
        VerticalPipe1(2,2), VerticalPipe2(2,2), VerticalPipe3(2,2), VerticalPipe4(2,2), 
        VerticalPipe5(2,2), VerticalPipe6(2,2), VerticalPipe7(2,2), VerticalPipe8(2,2), 
        
        HorizontalPipe1(2,2), HorizontalPipe2(2,2), HorizontalPipe3(2,2), HorizontalPipe4(2,2), 
        HorizontalPipe5(2,2), HorizontalPipe6(2,2), HorizontalPipe7(2,2), HorizontalPipe8(2,2), 
        
        Floor1West(1,2),  Floor1Center(1,2),  Floor1East(1,2), 
        Floor2West(1,2),  Floor2Center(1,2),  Floor2East(1,2), 
        Floor3West(1,2),  Floor3Center(1,2),  Floor3East(1,2), 
        Floor4West(1,2),  Floor4Center(1,2),  Floor4East(1,2), 
        Floor5West(1,2),  Floor5Center(1,2),  Floor5East(1,2), 
        Floor6West(1,2),  Floor6Center(1,2),  Floor6East(1,2), 
        Floor7West(1,2),  Floor7Center(1,2),  Floor7East(1,2), 
        Floor8West(1,2),  Floor8Center(1,2),  Floor8East(1,2), 
        Floor9West(1,2),  Floor9Center(1,2),  Floor9East(1,2), 
        Floor10West(1,2), Floor10Center(1,2), Floor10East(1,2), 
        Floor11West(1,2), Floor11Center(1,2), Floor11East(1,2), 
        Floor12West(1,2), Floor12Center(1,2), Floor12East(1,2), 
        Floor13West(1,2), Floor13Center(1,2), Floor13East(1,2), 
        
        PlatformBlock1(1,1), PlatformBlock2(1,1), PlatformBlock3(1,1), PlatformBlock4(1,1),
        Platform1(3,1), Platform2(3,1), Platform3(3,1), 
        Cloud1(3,1), Cloud2(3,1), Cloud3(3,3), 
        SpikesUp1(1,1), SpikesUp2(1,1), SpikesDown1(1,1), SpikesDown2(1,1), 
        BackgroundPlant(1,1), Background1(2,2), Background2(3,1); 
        
        private int col, row;
        
        /**
         * The constant type
         * @param col How many columns wide of this tile type
         * @param row How many rows tall of this tile type
         */
        private Type(final int col, final int row)
        {
            this.col = col;
            this.row = row;
        }
        
        public int getColumnDimensions()
        {
            return this.col;
        }
        
        public int getRowDimensions()
        {
            return this.row;
        }
    }
    
    public Tiles(final int columns, final int rows, final Image image)
    {
        //create new array of tiles
        this.tiles = new Tile[rows][columns];
        
        //set the tile sheet
        this.image = image;
    }
    
    public void update(final long time, final double scrollX)
    {
        for (int row = 0; row < tiles.length; row++)
        {
            for (int col = 0; col < tiles[row].length; col++)
            {
                //if there is no tile here skip
                if (!hasTile(col, row))
                    continue;
                
                Tile tile = getTile(col, row);
                
                //update animation
                tile.update(time);
                
                //set velocity
                tile.setVelocityX(scrollX);
                
                //move tile if velocity set
                tile.update();
            }
        }
    }
    
    /**
     * Get the tile that is found at the specified (x,y) location
     * @param x x-coordinate
     * @param y y-coordinate
     * @return Tile tile that exists at the x,y location, if nothing is found null is returned
     */
    public Tile getTile(final double x, final double y)
    {
        for (int row = 0; row < tiles.length; row++)
        {
            for (int col = 0; col < tiles[row].length; col++)
            {
                //if there is no tile here skip
                if (!hasTile(col, row))
                    continue;
                
                Tile tile = getTile(col, row);
                
                if (x < tile.getX() || x > tile.getX() + tile.getWidth())
                    continue;
                if (y < tile.getY() || y > tile.getY() + tile.getHeight())
                    continue;
                
                //we found a tile close enough to the entity
                return tile;
            }
        }
        
        return null;
    }
    
    public Tile getTile(final int column, final int row)
    {
        return this.tiles[row][column];
    }
    
    public Tile getTileWest()
    {
        return getTile(getColumns() - 1, 0);
    }
    
    /**
     * Does a tile exist?
     * @param column Location where we want to check
     * @param row Location where we want to check
     * @return true if there is an instance of Tile found at the specified location, false otherwise
     */
    public boolean hasTile(final int column, final int row)
    {
        return (getTile(column, row) != null);
    }
    
    /**
     * Check if the tiles are occupied for the specific type at the specified location.<br>
     * @param column Column where we want to check if space is occupied
     * @param row Row where we want to check if space is occupied
     * @param type The type of tile so we know how wide of an area to check
     * @param checkAll If true we will verify if every tile is occupied, if false we will verify if at least 1 tile is occupied
     * @return true if at least 1 tile is occupied, false otherwise
     */
    public boolean isOccupied(final int column, final int row, final Type type, final boolean checkAll)
    {
        for (int z = 0; z < type.getRowDimensions(); z++)
        {
            for (int x = 0; x < type.getColumnDimensions(); x++)
            {
                //does a tile already exist at this location
                if (hasTile(column + x, row + z))
                {
                    //if we only need to check for 1 tile, it is occupied
                    if (!checkAll)
                        return true;
                }
                else
                {
                    //if there isn't a tile here and we are checking for all, return false
                    if (checkAll)
                        return false;
                }
            }
        }
        
        if (checkAll)
        {
            //if we are checking all then this is occupied
            return true;
        }
        else
        {
            //if we are checking only 1, we didn't find a tile so this is not occupied
            return false;
        }
    }
    
    /**
     * Get the row dimension for our tiles
     * @return The total number of rows in the array
     */
    public int getRows()
    {
        return this.tiles.length;
    }
    
    /**
     * Get the column dimension for our tiles
     * @return The total number of columns in the array
     */
    public int getColumns()
    {
        return this.tiles[0].length;
    }
    
    /**
     * Get the floor row where the floor will be placed
     * @return 2 rows above the last row will be the floor row
     */
    public int getFloorRow()
    {
        return (getRows() - 2);
    }
    
    /**
     * Determine if this column is in the safe zone.<br>
     * The safe zone is the first screen where the player starts and the last screen of the level
     * @param column The column we want to check
     * @return true if the column is in the first or last screen, false otherwise
     */
    public boolean isSafeZone(final int column)
    {
        return (column < Level.LEVEL_COLUMNS_PER_SCREEN || column >= getColumns() - Level.LEVEL_COLUMNS_PER_SCREEN);
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
                case Empty:
                    tile = new Tile(type);
                    tile.addAnimation(type, 1, 0, 0, 0, 0, Timers.toNanoSeconds(125L), false);
                    tile.setDamage(false);
                    tile.setSolid(false);
                    tile.setLocation(x, y);
                    set(tile, column, row);
                    break;
                
                case UsedBlock:
                    addSingleObject(type, 4, 0, x, y, column, row, false, true);
                    break;
                
                case Lava:
                    tile = new Tile(type);
                    tile.addAnimation(type, 4, getStartX(0), getStartY(9), Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), true);
                    tile.setDamage(true);
                    tile.setSolid(true);
                    tile.setLocation(x, y);
                    set(tile, column, row);
                    break;
                
                case Water1:
                    tile = new Tile(type);
                    tile.addAnimation(type, 4, getStartX(0), getStartY(10), Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), true);
                    tile.setDamage(true);
                    tile.setSolid(true);
                    tile.setLocation(x, y);
                    set(tile, column, row);
                    break;
                    
                case Water2:
                    tile = new Tile(type);
                    tile.addAnimation(type, 4, getStartX(0), getStartY(11), Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), true);
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
                    addSingleObject(type, 0, 3, x, y, column, row, false, true);
                    break;
                    
                case VerticalPipe2:
                    addSingleObject(type, 2, 3, x, y, column, row, false, true);
                    break;
                    
                case VerticalPipe3:
                    addSingleObject(type, 4, 3, x, y, column, row, false, true);
                    break;
                    
                case VerticalPipe4:
                    addSingleObject(type, 0, 5, x, y, column, row, false, true);
                    break;
                    
                case VerticalPipe5:
                    addSingleObject(type, 2, 5, x, y, column, row, false, true);
                    break;
                    
                case VerticalPipe6:
                    addSingleObject(type, 4, 5, x, y, column, row, false, true);
                    break;
                    
                case VerticalPipe7:
                    addSingleObject(type, 0, 7, x, y, column, row, false, true);
                    break;
                    
                case VerticalPipe8:
                    addSingleObject(type, 2, 7, x, y, column, row, false, true);
                    break;
                    
                case HorizontalPipe1:
                    addSingleObject(type, 4, 1, x, y, column, row, false, true);
                    break;
                    
                case HorizontalPipe2:
                    addSingleObject(type, 6, 1, x, y, column, row, false, true);
                    break;
                    
                case HorizontalPipe3:
                    addSingleObject(type, 8, 1, x, y, column, row, false, true);
                    break;
                    
                case HorizontalPipe4:
                    addSingleObject(type, 6, 3, x, y, column, row, false, true);
                    break;
                    
                case HorizontalPipe5:
                    addSingleObject(type, 8, 3, x, y, column, row, false, true);
                    break;
                    
                case HorizontalPipe6:
                    addSingleObject(type, 6, 5, x, y, column, row, false, true);
                    break;
                    
                case HorizontalPipe7:
                    addSingleObject(type, 6, 7, x, y, column, row, false, true);
                    break;
                    
                case HorizontalPipe8:
                    addSingleObject(type, 4, 7, x, y, column, row, false, true);
                    break;
                    
                case Floor1West:
                    addSingleObject(type, 10, 0, x, y, column, row, false, true);
                    break;
                    
                case Floor1Center:
                    addSingleObject(type, 11, 0, x, y, column, row, false, true);
                    break;
                    
                case Floor1East: 
                    addSingleObject(type, 12, 0, x, y, column, row, false, true);
                    break;
                    
                case Floor2West:
                    addSingleObject(type, 10, 2, x, y, column, row, false, true);
                    break;
                    
                case Floor2Center:
                    addSingleObject(type, 11, 2, x, y, column, row, false, true);
                    break;
                    
                case Floor2East: 
                    addSingleObject(type, 12, 2, x, y, column, row, false, true);
                    break;
                    
                case Floor3West:
                    addSingleObject(type, 10, 4, x, y, column, row, false, true);
                    break;
                    
                case Floor3Center:
                    addSingleObject(type, 11, 4, x, y, column, row, false, true);
                    break;
                    
                case Floor3East: 
                    addSingleObject(type, 12, 4, x, y, column, row, false, true);
                    break;
                    
                case Floor4West:
                    addSingleObject(type, 10, 6, x, y, column, row, false, true);
                    break;
                    
                case Floor4Center:
                    addSingleObject(type, 11, 6, x, y, column, row, false, true);
                    break;
                    
                case Floor4East: 
                    addSingleObject(type, 12, 6, x, y, column, row, false, true);
                    break;
                    
                case Floor5West:
                    addSingleObject(type, 10, 8, x, y, column, row, false, true);
                    break;
                    
                case Floor5Center:
                    addSingleObject(type, 11, 8, x, y, column, row, false, true);
                    break;
                    
                case Floor5East: 
                    addSingleObject(type, 12, 8, x, y, column, row, false, true);
                    break;
                    
                case Floor6West:
                    addSingleObject(type, 10, 10, x, y, column, row, false, true);
                    break;
                    
                case Floor6Center:
                    addSingleObject(type, 11, 10, x, y, column, row, false, true);
                    break;
                    
                case Floor6East: 
                    addSingleObject(type, 12, 10, x, y, column, row, false, true);
                    break;
                    
                case Floor7West:
                    addSingleObject(type, 10, 12, x, y, column, row, false, true);
                    break;
                    
                case Floor7Center:
                    addSingleObject(type, 11, 12, x, y, column, row, false, true);
                    break;
                    
                case Floor7East: 
                    addSingleObject(type, 12, 12, x, y, column, row, false, true);
                    break;
                    
                case Floor8West:
                    addSingleObject(type, 13, 0, x, y, column, row, false, true);
                    break;
                    
                case Floor8Center:
                    addSingleObject(type, 14, 0, x, y, column, row, false, true);
                    break;
                    
                case Floor8East: 
                    addSingleObject(type, 15, 0, x, y, column, row, false, true);
                    break;
                    
                case Floor9West:
                    addSingleObject(type, 13, 2, x, y, column, row, false, true);
                    break;
                    
                case Floor9Center:
                    addSingleObject(type, 14, 2, x, y, column, row, false, true);
                    break;
                    
                case Floor9East: 
                    addSingleObject(type, 15, 2, x, y, column, row, false, true);
                    break;
                    
                case Floor10West:
                    addSingleObject(type, 13, 4, x, y, column, row, false, true);
                    break;
                    
                case Floor10Center:
                    addSingleObject(type, 14, 4, x, y, column, row, false, true);
                    break;
                    
                case Floor10East: 
                    addSingleObject(type, 15, 4, x, y, column, row, false, true);
                    break;
                    
                case Floor11West:
                    addSingleObject(type, 13, 6, x, y, column, row, false, true);
                    break;
                    
                case Floor11Center:
                    addSingleObject(type, 14, 6, x, y, column, row, false, true);
                    break;
                    
                case Floor11East: 
                    addSingleObject(type, 15, 6, x, y, column, row, false, true);
                    break;

                case Floor12West:
                    addSingleObject(type, 13, 8, x, y, column, row, false, true);
                    break;
                    
                case Floor12Center:
                    addSingleObject(type, 14, 8, x, y, column, row, false, true);
                    break;
                    
                case Floor12East: 
                    addSingleObject(type, 15, 8, x, y, column, row, false, true);
                    break;
                    
                case Floor13West:
                    addSingleObject(type, 13, 10, x, y, column, row, false, true);
                    break;
                    
                case Floor13Center:
                    addSingleObject(type, 14, 10, x, y, column, row, false, true);
                    break;
                    
                case Floor13East: 
                    addSingleObject(type, 15, 10, x, y, column, row, false, true);
                    break;
                    
                case PlatformBlock1:
                    addSingleObject(type, 5, 0, x, y, column, row, false, true);
                    break;
                    
                case PlatformBlock2:
                    addSingleObject(type, 6, 0, x, y, column, row, false, true);
                    break;
                    
                case PlatformBlock3:
                    addSingleObject(type, 7, 0, x, y, column, row, false, true);
                    break;
                    
                case PlatformBlock4:
                    addSingleObject(type, 8, 0, x, y, column, row, false, true);
                    break;
                    
                case Platform1: 
                    addSingleObject(type, 4, 9, x, y, column, row, false, true);
                    break;
                    
                case Platform2: 
                    addSingleObject(type, 4, 10, x, y, column, row, false, true);
                    break;
                    
                case Platform3: 
                    addSingleObject(type, 0, 13, x, y, column, row, false, true);
                    break;
                    
                case Cloud1:
                    addSingleObject(type, 4, 11, x, y, column, row, false, false);
                    break;
                            
                case Cloud2:
                    addSingleObject(type, 4, 12, x, y, column, row, false, false);
                    break;
                    
                case Cloud3:
                    addSingleObject(type, 4, 13, x, y, column, row, false, false);
                    break;
                    
                case SpikesUp1:
                    addSingleObject(type, 0, 12, x, y, column, row, true, true);
                    break;
                    
                case SpikesUp2:
                    addSingleObject(type, 2, 12, x, y, column, row, true, true);
                    break;
                    
                case SpikesDown1:
                    addSingleObject(type, 1, 12, x, y, column, row, true, true);
                    break;
                    
                case SpikesDown2:
                    addSingleObject(type, 3, 12, x, y, column, row, true, true);
                    break;
                    
                case BackgroundPlant:
                    addSingleObject(type, 9, 0, x, y, column, row, false, false);
                    break;
                    
                case Background1:
                    addSingleObject(type, 0, 14, x, y, column, row, false, false);
                    break;
                    
                case Background2:
                    addSingleObject(type, 0, 16, x, y, column, row, false, false);
                    break;
                    
                case BigBlock:
                    addSingleObject(type, 0, 17, x, y, column, row, false, true);
                    break;

                case BiggerBlock1:
                    addSingleObject(type, 16, 0, x, y, column, row, false, true);
                    break;
                    
                case BiggerBlock2:
                    addSingleObject(type, 16, 3, x, y, column, row, false, true);
                    break;
                    
                case BiggerBlock3:
                    addSingleObject(type, 16, 6, x, y, column, row, false, true);
                    break;
                    
                case BiggerBlock4:
                    addSingleObject(type, 16, 9, x, y, column, row, false, true);
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
     * Add a single object to our tile array.<br>
     * An object can take multiple cols/rows
     * @param type The type of tile
     * @param animationCol Start column of tile
     * @param animationRow Start row of tile
     * @param x x-coordinate where tile(s) will be drawn
     * @param y y-coordinate where tile(s) will be drawn
     * @param column Column where this will lie in our array
     * @param row Row where this will lie in our array
     * @param damage Does this object cause damage upon collision
     * @param solid Is this object solid, so we know to check for collision
     */
    private void addSingleObject(final Type type, final int animationCol, final int animationRow, final int x, final int y, final int column, final int row, final boolean damage, final boolean solid)
    {
        Tile tile;
        
        for (int i = 0; i < type.getColumnDimensions(); i++)
        {
            for (int z = 0; z < type.getRowDimensions(); z++)
            {
                tile = new Tile(type);
                tile.addAnimation(type, 1, getStartX(animationCol + i), getStartY(animationRow + z), Tile.WIDTH, Tile.HEIGHT, 0, false);
                tile.setDamage(damage);
                tile.setSolid(solid);
                tile.setLocation(x + (Tile.WIDTH * i), y + (Tile.HEIGHT * z));
                set(tile, column + i, row + z);
            }
        }
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
    
    /**
     * Here we will begin to add tiles to the level for creation
     * @param level The current level object used to locate x,y placement/render coordinates
     */
    public void populate(final Level level, final Random random)
    {
        /*
         * 1. First we will pass through the level and place the floor.<br>
         * 2. Then we will place some block obstacles in the way.<br>
         * 3. Then we will place some bonus blocks or breakable bricks
         * 4. Then we will place some background images
         * 5. Then we will place some clouds in the background
         */
        
        //create the floor first
        LevelCreatorHelper.createFloor(level, this, random);
        
        //now place some obstacles
        LevelCreatorHelper.createObstacles(level, this, random);
        
        //now lets place some platforms
        LevelCreatorHelper.createPlatforms(level, this, random);
        
        //now lets place some backgrounds
        LevelCreatorHelper.createBackgrounds(level, this, random);
        
        //now lets place some clouds
        LevelCreatorHelper.createClouds(level, this, random);
        
        try
        {
            /**
             * Always add make sure these tiles exist for proper scrolling of level.
             */
            add(Type.Empty, 0, 0, level.getX(0), level.getY(0));

            //make sure tile has been created
            if (!hasTile(getColumns() - 1, getRows() - 1))
                throw new Exception("South-East corner of tile array must have a tile for proper scrolling");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public boolean canScrollNorth(final double y)
    {
        return (getTile(0,0).getY() <= y);
    }
    
    public boolean canScrollSouth(final double y)
    {
        return (getTile(getColumns() - 1, getRows() - 1).getY() >= y);
    }
    
    public boolean canScrollWest(final double x)
    {
        return (getTile(0,0).getX() <= x);
    }
    
    public boolean canScrollEast(final double x)
    {
        return (getTile(getColumns() - 1, getRows() - 1).getX() >= x);
    }
    
    /**
     * Make sure we are in the bounds of the array
     * @param type The type of tile
     * @param column Location where tile(s) are to be placed
     * @param row Location where tile(s) are to be placed
     * @return true if we are in bounds, false otherwise
     */
    public boolean hasRange(final Type type, final int column, final int row)
    {
        for (int i = 0; i < type.getColumnDimensions(); i++)
        {
            for (int z = 0; z < type.getRowDimensions(); z++)
            {
                if (column + i < 0 || column + i >= getColumns())
                    return false;
                if (row + z < 0 || row + z >= getRows())
                    return false;
            }
        }
        
        return true;
    }
    
    /**
     * Render the tiles
     * @param graphics Object where images will be written
     * @param window The area where the tiles will be rendered so we don't have to render tiles that aren't part of the screen
     */
    public void render(final Graphics graphics, final Rectangle window)
    {
        for (int row = 0; row < tiles.length; row++)
        {
            for (int col = 0; col < tiles[row].length; col++)
            {
                //if there is no tile here skip
                if (!hasTile(col, row))
                    continue;
                
                Tile tile = getTile(col, row);
                
                //if not on screen, we won't render the tile
                if (tile.getX() + tile.getWidth()  < window.x || tile.getX() > window.x + window.width)
                    continue;
                if (tile.getY() + tile.getHeight() < window.y || tile.getY() > window.y + window.height)
                    continue;
                
                //draw the tile
                tile.draw(graphics, image);
            }
        }
    }
}