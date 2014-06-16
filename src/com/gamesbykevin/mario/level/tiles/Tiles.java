package com.gamesbykevin.mario.level.tiles;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.LevelCreatorHelper;
import com.gamesbykevin.mario.level.powerups.PowerUps;

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
        
        QuestionBlock(1,1), UsedBlock(1,1), BreakableBrick(1,1), RotatingGear(1,1), RotatingGear2(1,1), 
        Lava(1,1), Water1(1,1), Water2(1,1), 
        BigBlock(2,2), BiggerBlock1(3,3), BiggerBlock2(3,3), BiggerBlock3(3,3), BiggerBlock4(3,3), 
        
        BiggerBlock5(3, 2), BiggerBlock6(6, 2), BiggerBlock7(4, 2), BiggerBlock8(4, 2), BiggerBlock9(5, 3),
        BiggerBlock10(4, 2), BiggerBlock11(4, 2), 
        
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

        Floor14West(1,2), Floor14Center(1,2), Floor14East(1,2), 
        Floor15West(1,2), Floor15Center(1,2), Floor15East(1,2), 
        Floor16West(1,2), Floor16Center(1,2), Floor16East(1,2), 
        Floor17West(1,2), Floor17Center(1,2), Floor17East(1,2), 
        Floor18West(1,2), Floor18Center(1,2), Floor18East(1,2), 
        Floor19West(1,2), Floor19Center(1,2), Floor19East(1,2), 
        Floor20West(1,2), Floor20Center(1,2), Floor20East(1,2), 
        
        PlatformBlock1(1,1), PlatformBlock2(1,1), PlatformBlock3(1,1), PlatformBlock4(1,1), PlatformBlock5(1,1), 
        PlatformBlock6(1,1), PlatformBlock7(1,1), PlatformBlock8(1,1), 
        
        Platform1(3,1), Platform2(3,1), Platform3(3,1), Platform4(2,1), Platform5(2,1), Platform6(2,1), 
        Platform7(3,1), 
        
        Cloud1(3,1), Cloud2(3,1), Cloud3(3,3), Cloud4(2,2), 
        
        SpikesUp1(1,1), SpikesUp2(1,1), SpikesDown1(1,1), SpikesDown2(1,1), 
        
        BackgroundPlant(1,1), BackgroundPlant2(1,1), Background1(2,2), Background2(3,1), 
        
        Background3(4,1), Background4(4,1), Background5(3,1), Background6(6, 4),
        
        Goal(1,1);
        
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
        for (int x = 0; x < type.getColumnDimensions(); x++)
        {
            for (int z = 0; z < type.getRowDimensions(); z++)
            {
                //we won't check tiles that are out of range
                if (!hasRange(type, column + x, row + z))
                    continue;
                
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
    public void add(final Type type, final double column, final double row, final double x, final double y)
    {
        try
        {
            //each tile will have 1 animation
            switch (type)
            {
                case Empty:
                    addSingleObject(type, 1, 0, false, -1, -1, x, y, column, row, false, false, false);
                    break;
                
                case UsedBlock:
                    addSingleObject(type, 1, 0, false, 4, 0, x, y, column, row, false, true, false);
                    break;
                
                case Lava:
                    addSingleObject(type, 4, Timers.toNanoSeconds(125L), true, 0, 9, x, y, column, row, true, true, true);
                    break;
                
                case Water1:
                    addSingleObject(type, 4, Timers.toNanoSeconds(125L), true, 0, 10, x, y, column, row, true, true, true);
                    break;
                    
                case Water2:
                    addSingleObject(type, 4, Timers.toNanoSeconds(125L), true, 0, 11, x, y, column, row, true, true, true);
                    break;
                    
                case QuestionBlock:
                    addSingleObject(type, 4, Timers.toNanoSeconds(125L), true, 0, 0, x, y, column, row, false, true, false);
                    break;
                    
                case BreakableBrick:
                    addSingleObject(type, 4, Timers.toNanoSeconds(200L), true, 0, 1, x, y, column, row, false, true, false);
                    break;
                    
                case RotatingGear:
                    addSingleObject(type, 4, Timers.toNanoSeconds(75L), true, 0, 2, x, y, column, row, true, true, false);
                    break;
                
                case RotatingGear2:
                    addSingleObject(type, 4, Timers.toNanoSeconds(75L), true, 0, 21, x, y, column, row, true, true, false);
                    break;
                    
                case VerticalPipe1:
                    addSingleObject(type, 1, 0, false, 0, 3, x, y, column, row, false, true, false);
                    break;
                    
                case VerticalPipe2:
                    addSingleObject(type, 1, 0, false, 2, 3, x, y, column, row, false, true, false);
                    break;
                    
                case VerticalPipe3:
                    addSingleObject(type, 1, 0, false, 4, 3, x, y, column, row, false, true, false);
                    break;
                    
                case VerticalPipe4:
                    addSingleObject(type, 1, 0, false, 0, 5, x, y, column, row, false, true, false);
                    break;
                    
                case VerticalPipe5:
                    addSingleObject(type, 1, 0, false, 2, 5, x, y, column, row, false, true, false);
                    break;
                    
                case VerticalPipe6:
                    addSingleObject(type, 1, 0, false, 4, 5, x, y, column, row, false, true, false);
                    break;
                    
                case VerticalPipe7:
                    addSingleObject(type, 1, 0, false, 0, 7, x, y, column, row, false, true, false);
                    break;
                    
                case VerticalPipe8:
                    addSingleObject(type, 1, 0, false, 2, 7, x, y, column, row, false, true, false);
                    break;
                    
                case HorizontalPipe1:
                    addSingleObject(type, 1, 0, false, 4, 1, x, y, column, row, false, true, false);
                    break;
                    
                case HorizontalPipe2:
                    addSingleObject(type, 1, 0, false, 6, 1, x, y, column, row, false, true, false);
                    break;
                    
                case HorizontalPipe3:
                    addSingleObject(type, 1, 0, false, 8, 1, x, y, column, row, false, true, false);
                    break;
                    
                case HorizontalPipe4:
                    addSingleObject(type, 1, 0, false, 6, 3, x, y, column, row, false, true, false);
                    break;
                    
                case HorizontalPipe5:
                    addSingleObject(type, 1, 0, false, 8, 3, x, y, column, row, false, true, false);
                    break;
                    
                case HorizontalPipe6:
                    addSingleObject(type, 1, 0, false, 6, 5, x, y, column, row, false, true, false);
                    break;
                    
                case HorizontalPipe7:
                    addSingleObject(type, 1, 0, false, 6, 7, x, y, column, row, false, true, false);
                    break;
                    
                case HorizontalPipe8:
                    addSingleObject(type, 1, 0, false, 4, 7, x, y, column, row, false, true, false);
                    break;
                    
                case Floor1West:
                    addSingleObject(type, 1, 0, false, 10, 0, x, y, column, row, false, true, false);
                    break;
                    
                case Floor1Center:
                    addSingleObject(type, 1, 0, false, 11, 0, x, y, column, row, false, true, false);
                    break;
                    
                case Floor1East: 
                    addSingleObject(type, 1, 0, false, 12, 0, x, y, column, row, false, true, false);
                    break;
                    
                case Floor2West:
                    addSingleObject(type, 1, 0, false, 10, 2, x, y, column, row, false, true, false);
                    break;
                    
                case Floor2Center:
                    addSingleObject(type, 1, 0, false, 11, 2, x, y, column, row, false, true, false);
                    break;
                    
                case Floor2East: 
                    addSingleObject(type, 1, 0, false, 12, 2, x, y, column, row, false, true, false);
                    break;
                    
                case Floor3West:
                    addSingleObject(type, 1, 0, false, 10, 4, x, y, column, row, false, true, false);
                    break;
                    
                case Floor3Center:
                    addSingleObject(type, 1, 0, false, 11, 4, x, y, column, row, false, true, false);
                    break;
                    
                case Floor3East: 
                    addSingleObject(type, 1, 0, false, 12, 4, x, y, column, row, false, true, false);
                    break;
                    
                case Floor4West:
                    addSingleObject(type, 1, 0, false, 10, 6, x, y, column, row, false, true, false);
                    break;
                    
                case Floor4Center:
                    addSingleObject(type, 1, 0, false, 11, 6, x, y, column, row, false, true, false);
                    break;
                    
                case Floor4East: 
                    addSingleObject(type, 1, 0, false, 12, 6, x, y, column, row, false, true, false);
                    break;
                    
                case Floor5West:
                    addSingleObject(type, 1, 0, false, 10, 8, x, y, column, row, false, true, false);
                    break;
                    
                case Floor5Center:
                    addSingleObject(type, 1, 0, false, 11, 8, x, y, column, row, false, true, false);
                    break;
                    
                case Floor5East: 
                    addSingleObject(type, 1, 0, false, 12, 8, x, y, column, row, false, true, false);
                    break;
                    
                case Floor6West:
                    addSingleObject(type, 1, 0, false, 10, 10, x, y, column, row, false, true, false);
                    break;
                    
                case Floor6Center:
                    addSingleObject(type, 1, 0, false, 11, 10, x, y, column, row, false, true, false);
                    break;
                    
                case Floor6East: 
                    addSingleObject(type, 1, 0, false, 12, 10, x, y, column, row, false, true, false);
                    break;
                    
                case Floor7West:
                    addSingleObject(type, 1, 0, false, 10, 12, x, y, column, row, false, true, false);
                    break;
                    
                case Floor7Center:
                    addSingleObject(type, 1, 0, false, 11, 12, x, y, column, row, false, true, false);
                    break;
                    
                case Floor7East: 
                    addSingleObject(type, 1, 0, false, 12, 12, x, y, column, row, false, true, false);
                    break;
                    
                case Floor8West:
                    addSingleObject(type, 1, 0, false, 13, 0, x, y, column, row, false, true, false);
                    break;
                    
                case Floor8Center:
                    addSingleObject(type, 1, 0, false, 14, 0, x, y, column, row, false, true, false);
                    break;
                    
                case Floor8East: 
                    addSingleObject(type, 1, 0, false, 15, 0, x, y, column, row, false, true, false);
                    break;
                    
                case Floor9West:
                    addSingleObject(type, 1, 0, false, 13, 2, x, y, column, row, false, true, false);
                    break;
                    
                case Floor9Center:
                    addSingleObject(type, 1, 0, false, 14, 2, x, y, column, row, false, true, false);
                    break;
                    
                case Floor9East: 
                    addSingleObject(type, 1, 0, false, 15, 2, x, y, column, row, false, true, false);
                    break;
                    
                case Floor10West:
                    addSingleObject(type, 1, 0, false, 13, 4, x, y, column, row, false, true, false);
                    break;
                    
                case Floor10Center:
                    addSingleObject(type, 1, 0, false, 14, 4, x, y, column, row, false, true, false);
                    break;
                    
                case Floor10East: 
                    addSingleObject(type, 1, 0, false, 15, 4, x, y, column, row, false, true, false);
                    break;
                    
                case Floor11West:
                    addSingleObject(type, 1, 0, false, 13, 6, x, y, column, row, false, true, false);
                    break;
                    
                case Floor11Center:
                    addSingleObject(type, 1, 0, false, 14, 6, x, y, column, row, false, true, false);
                    break;
                    
                case Floor11East: 
                    addSingleObject(type, 1, 0, false, 15, 6, x, y, column, row, false, true, false);
                    break;

                case Floor12West:
                    addSingleObject(type, 1, 0, false, 13, 8, x, y, column, row, false, true, false);
                    break;
                    
                case Floor12Center:
                    addSingleObject(type, 1, 0, false, 14, 8, x, y, column, row, false, true, false);
                    break;
                    
                case Floor12East: 
                    addSingleObject(type, 1, 0, false, 15, 8, x, y, column, row, false, true, false);
                    break;
                    
                case Floor13West:
                    addSingleObject(type, 1, 0, false, 13, 10, x, y, column, row, false, true, false);
                    break;
                    
                case Floor13Center:
                    addSingleObject(type, 1, 0, false, 14, 10, x, y, column, row, false, true, false);
                    break;
                    
                case Floor13East: 
                    addSingleObject(type, 1, 0, false, 15, 10, x, y, column, row, false, true, false);
                    break;
                    
                case Floor14West:
                    addSingleObject(type, 1, 0, false, 2, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor14Center:
                    addSingleObject(type, 1, 0, false, 3, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor14East: 
                    addSingleObject(type, 1, 0, false, 4, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor15West:
                    addSingleObject(type, 1, 0, false, 5, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor15Center:
                    addSingleObject(type, 1, 0, false, 6, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor15East: 
                    addSingleObject(type, 1, 0, false, 7, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor16West:
                    addSingleObject(type, 1, 0, false, 8, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor16Center:
                    addSingleObject(type, 1, 0, false, 9, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor16East: 
                    addSingleObject(type, 1, 0, false, 10, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor17West:
                    addSingleObject(type, 1, 0, false, 11, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor17Center:
                    addSingleObject(type, 1, 0, false, 12, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor17East: 
                    addSingleObject(type, 1, 0, false, 13, 17, x, y, column, row, false, true, false);
                    break;
                    
                case Floor18West:
                    addSingleObject(type, 1, 0, false, 0, 25, x, y, column, row, false, true, false);
                    break;
                    
                case Floor18Center:
                    addSingleObject(type, 1, 0, false, 1, 25, x, y, column, row, false, true, false);
                    break;
                    
                case Floor18East: 
                    addSingleObject(type, 1, 0, false, 2, 25, x, y, column, row, false, true, false);
                    break;
                    
                case Floor19West:
                    addSingleObject(type, 1, 0, false, 0, 27, x, y, column, row, false, true, false);
                    break;
                    
                case Floor19Center:
                    addSingleObject(type, 1, 0, false, 1, 27, x, y, column, row, false, true, false);
                    break;
                    
                case Floor19East: 
                    addSingleObject(type, 1, 0, false, 2, 27, x, y, column, row, false, true, false);
                    break;
                    
                case Floor20West:
                    addSingleObject(type, 1, 0, false, 3, 27, x, y, column, row, false, true, false);
                    break;
                    
                case Floor20Center:
                    addSingleObject(type, 1, 0, false, 4, 27, x, y, column, row, false, true, false);
                    break;
                    
                case Floor20East: 
                    addSingleObject(type, 1, 0, false, 5, 27, x, y, column, row, false, true, false);
                    break;
                    
                case PlatformBlock1:
                    addSingleObject(type, 1, 0, false, 5, 0, x, y, column, row, false, true, false);
                    break;
                    
                case PlatformBlock2:
                    addSingleObject(type, 1, 0, false, 6, 0, x, y, column, row, false, true, false);
                    break;
                    
                case PlatformBlock3:
                    addSingleObject(type, 1, 0, false, 7, 0, x, y, column, row, false, true, false);
                    break;
                    
                case PlatformBlock4:
                    addSingleObject(type, 1, 0, false, 8, 0, x, y, column, row, false, true, false);
                    break;
                    
                case PlatformBlock5:
                    addSingleObject(type, 1, 0, false, 8, 8, x, y, column, row, false, true, false);
                    break;
                    
                case PlatformBlock6:
                    addSingleObject(type, 1, 0, false, 9, 8, x, y, column, row, false, true, false);
                    break;
                    
                case PlatformBlock7:
                    addSingleObject(type, 1, 0, false, 8, 9, x, y, column, row, false, true, false);
                    break;
                    
                case PlatformBlock8:
                    addSingleObject(type, 1, 0, false, 9, 9, x, y, column, row, false, true, false);
                    break;
                    
                case Platform1: 
                    addSingleObject(type, 1, 0, false, 4, 9, x, y, column, row, false, true, false);
                    break;
                    
                case Platform2: 
                    addSingleObject(type, 1, 0, false, 4, 10, x, y, column, row, false, true, false);
                    break;
                    
                case Platform3: 
                    addSingleObject(type, 1, 0, false, 0, 13, x, y, column, row, false, true, false);
                    break;
                    
                case Platform4: 
                    addSingleObject(type, 1, 0, false, 8, 5, x, y, column, row, false, true, false);
                    break;
                    
                case Platform5: 
                    addSingleObject(type, 1, 0, false, 8, 5, x, y, column, row, false, true, false);
                    break;
                    
                case Platform6: 
                    addSingleObject(type, 1, 0, false, 8, 5, x, y, column, row, false, true, false);
                    break;
                    
                case Platform7: 
                    addSingleObject(type, 1, 0, false, 7, 10, x, y, column, row, false, true, false);
                    break;
                    
                case Cloud1:
                    addSingleObject(type, 1, 0, false, 4, 11, x, y, column, row, false, false, false);
                    break;
                            
                case Cloud2:
                    addSingleObject(type, 1, 0, false, 4, 12, x, y, column, row, false, false, false);
                    break;
                    
                case Cloud3:
                    addSingleObject(type, 1, 0, false, 4, 13, x, y, column, row, false, false, false);
                    break;
                    
                case Cloud4:
                    addSingleObject(type, 1, 0, false, 2, 14, x, y, column, row, false, false, false);
                    break;
                    
                case SpikesUp1:
                    addSingleObject(type, 1, 0, false, 0, 12, x, y, column, row, true, true, false);
                    break;
                    
                case SpikesUp2:
                    addSingleObject(type, 1, 0, false, 2, 12, x, y, column, row, true, true, false);
                    break;
                    
                case SpikesDown1:
                    addSingleObject(type, 1, 0, false, 1, 12, x, y, column, row, true, true, false);
                    break;
                    
                case SpikesDown2:
                    addSingleObject(type, 1, 0, false, 3, 12, x, y, column, row, true, true, false);
                    break;
                    
                case BackgroundPlant:
                    addSingleObject(type, 1, 0, false, 9, 0, x, y, column, row, false, false, false);
                    break;
                    
                case BackgroundPlant2:
                    addSingleObject(type, 1, 0, false, 3, 16, x, y, column, row, false, false, false);
                    break;
                    
                case Background1:
                    addSingleObject(type, 1, 0, false, 0, 14, x, y, column, row, false, false, false);
                    break;
                    
                case Background2:
                    addSingleObject(type, 1, 0, false, 0, 16, x, y, column, row, false, false, false);
                    break;
                    
                case Background3:
                    addSingleObject(type, 1, 0, false, 0, 22, x, y, column, row, false, false, false);
                    break;
                    
                case Background4:
                    addSingleObject(type, 1, 0, false, 0, 23, x, y, column, row, false, false, false);
                    break;
                    
                case Background5:
                    addSingleObject(type, 1, 0, false, 0, 24, x, y, column, row, false, false, false);
                    break;
                    
                case Background6:
                    addSingleObject(type, 1, 0, false, 6, 25, x, y, column, row, false, false, false);
                    break;
                    
                case BigBlock:
                    addSingleObject(type, 1, 0, false, 0, 17, x, y, column, row, false, true, false);
                    break;

                case BiggerBlock1:
                    addSingleObject(type, 1, 0, false, 16, 0, x, y, column, row, false, true, false);
                    break;
                    
                case BiggerBlock2:
                    addSingleObject(type, 1, 0, false, 16, 3, x, y, column, row, false, true, false);
                    break;
                    
                case BiggerBlock3:
                    addSingleObject(type, 1, 0, false, 16, 6, x, y, column, row, false, true, false);
                    break;
                    
                case BiggerBlock4:
                    addSingleObject(type, 1, 0, false, 16, 9, x, y, column, row, false, true, false);
                    break;
                    
                case BiggerBlock5:
                    addSingleObject(type, 1, 0, false, 7, 12, x, y, column, row, false, true, false);
                    break;
                    
                case BiggerBlock6:
                    addSingleObject(type, 1, 0, false, 8, 15, x, y, column, row, false, true, false);
                    break;
                    
                case BiggerBlock7:
                    addSingleObject(type, 1, 0, false, 14, 13, x, y, column, row, false, true, false);
                    break;
                    
                case BiggerBlock8:
                    addSingleObject(type, 1, 0, false, 14, 15, x, y, column, row, false, true, false);
                    break;
                    
                case BiggerBlock9:
                    addSingleObject(type, 1, 0, false, 14, 17, x, y, column, row, false, true, false);
                    break;
                    
                case BiggerBlock10:
                    addSingleObject(type, 1, 0, false, 0, 19, x, y, column, row, false, true, false);
                    break;
                    
                case BiggerBlock11:
                    addSingleObject(type, 1, 0, false, 4, 19, x, y, column, row, false, true, false);
                    break;
                    
                case Goal:
                    addSingleObject(type, 1, 0, false, 0, 29, x, y, column, row, false, true, false);
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
     * @param animationCount Number of animations
     * @param animationDelay The tile delay between animations
     * @param loop Do we loop the animation
     * @param animationCol Start column of tile
     * @param animationRow Start row of tile
     * @param x x-coordinate where tile(s) will be drawn
     * @param y y-coordinate where tile(s) will be drawn
     * @param column Column where this will lie in our array
     * @param row Row where this will lie in our array
     * @param damage Does this object cause damage upon collision
     * @param solid Is this object solid, so we know to check for collision
     * @param death Will this object cause death upon collision
     */
    private void addSingleObject(final Type type, final int animationCount, final long animationDelay, final boolean loop, final int animationCol, final int animationRow, final double x, final double y, final double column, final double row, final boolean damage, final boolean solid, final boolean death)
    {
        Tile tile;
        
        for (int i = 0; i < type.getColumnDimensions(); i++)
        {
            for (int z = 0; z < type.getRowDimensions(); z++)
            {
                tile = new Tile(type);
                tile.addAnimation(type, animationCount, getStartX(animationCol + i), getStartY(animationRow + z), Tile.WIDTH, Tile.HEIGHT, animationDelay, loop);
                tile.setDamage(damage);
                tile.setSolid(solid);
                tile.setDeath(death);
                tile.setLocation(x + (Tile.WIDTH * i), y + (Tile.HEIGHT * z));
                set(tile, column + i, row + z);
            }
        }
    }
    
    
    /**
     * Remove tile at specified location
     * @param column
     * @param row 
     */
    public void remove(final int column, final int row)
    {
        tiles[row][column] = null;
    }
    
    /**
     * Assign the tile to the array
     * @param tile The tile we want to assign
     * @param column The location in the array where we want to assign the tile
     * @param row The location in the array where we want to assign the tile
     */
    private void set(final Tile tile, final double column, final double row)
    {
        //set the location where this will be in the array
        tile.setCol(column);
        tile.setRow(row);
        
        //set the tile in the array
        tiles[(int)row][(int)column] = tile;
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
     * @param random Object used to make random decisions
     */
    public void populate(final Level level, final PowerUps powerUps, final Random random)
    {
        //create the floor first
        LevelCreatorHelper.createFloor(level, this, random);
        
        //now place some obstacles
        LevelCreatorHelper.createCollisionObstacles(level, this, random);
        
        //now lets place some platforms
        LevelCreatorHelper.createPlatforms(level, this, random);
        
        //here we will place breakable bricks and power blocks
        LevelCreatorHelper.createBlocks(level, this, random);
        
        //now lets place some backgrounds
        LevelCreatorHelper.createBackgrounds(level, this, random);
        
        //now lets place some clouds
        LevelCreatorHelper.createClouds(level, this, random);
        
        //now lets place some deadly obstacles
        LevelCreatorHelper.createDeadlyObstacles(level, this, random);
        
        //place coins in the level
        LevelCreatorHelper.placeCoins(level, this, powerUps, random);
        
        //place switch which will represent the goal
        LevelCreatorHelper.createGoal(level, this);
        
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
     * Render the tiles that the players can't walk through
     * @param graphics Object where images will be written
     * @param window The area where the tiles will be rendered so we don't have to render tiles that aren't part of the screen
     */
    public void renderSolidTiles(final Graphics graphics, final Rectangle window)
    {
        for (int row = 0; row < tiles.length; row++)
        {
            for (int col = 0; col < tiles[row].length; col++)
            {
                //if there is no tile here skip
                if (!hasTile(col, row))
                    continue;
                
                Tile tile = getTile(col, row);
                
                if (!LevelCreatorHelper.isBackgroundTile(tile) && !LevelCreatorHelper.isCloudTile(tile))
                    renderTile(tile, graphics, window);
            }
        }
    }
    
    /**
     * Render the tiles that the players can walk through without collision detection
     * @param graphics Object where images will be written
     * @param window The area where the tiles will be rendered so we don't have to render tiles that aren't part of the screen
     */
    public void renderNonSolidTiles(final Graphics graphics, final Rectangle window)
    {
        for (int row = 0; row < tiles.length; row++)
        {
            for (int col = 0; col < tiles[row].length; col++)
            {
                //if there is no tile here skip
                if (!hasTile(col, row))
                    continue;
                
                Tile tile = getTile(col, row);
                
                if (LevelCreatorHelper.isBackgroundTile(tile) || LevelCreatorHelper.isCloudTile(tile))
                    renderTile(tile, graphics, window);
            }
        }
    }
    
    /**
     * Render a tile
     * @param tile The tile we want to draw
     * @param graphics Object where images will be written
     * @param window The area where the tiles will be rendered so we don't have to render tiles that aren't part of the screen
     */
    public void renderTile(final Tile tile, final Graphics graphics, final Rectangle window)
    {
        //if not on screen, we won't render the tile
        if (tile.getX() + tile.getWidth()  < window.x || tile.getX() > window.x + window.width)
            return;
        if (tile.getY() + tile.getHeight() < window.y || tile.getY() > window.y + window.height)
            return;

        //draw the tile
        tile.draw(graphics, image);
    }
}