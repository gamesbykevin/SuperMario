package com.gamesbykevin.mario.level;

import com.gamesbykevin.mario.level.tiles.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelCreatorHelper 
{
    //change these to increase/decrease the probability of being added to the level
    private static final int ADD_CLOUDS_PROBABILITY = 6;
    private static final int ADD_BACKGROUNDS_PROBABILITY = 3;
    private static final int ADD_OBSTACLES_PROBABILITY = 3;
    private static final int ADD_PLATFORMS_PROBABILITY = 3;
    
    /**
     * How many tiles the average floor is
     */
    private static final int MAX_FLOOR_LENGTH = 15;
    
    /**
     * The height of the floor can vary
     */
    private static final int MAX_FLOOR_HEIGHT_DIFFERENCE = 3;
    
    /**
     * The height of the platforms can vary
     */
    private static final int PLATFORM_HEIGHT_DIFFERENCE = 8;
    
    /**
     * The maximum distance the floors can be apart
     */
    private static final int MAX_GAP_LENGTH = 3;
    
    private static Tiles.Type getFloorTypeWest(final Tiles.Type type)
    {
        try
        {
            switch(type)
            {
                case Floor1Center:
                    return Tiles.Type.Floor1West;
                    
                case Floor2Center:
                    return Tiles.Type.Floor2West;
                    
                case Floor3Center:
                    return Tiles.Type.Floor3West;
                    
                case Floor4Center:
                    return Tiles.Type.Floor4West;
                    
                case Floor5Center:
                    return Tiles.Type.Floor5West;
                    
                case Floor6Center:
                    return Tiles.Type.Floor6West;
                    
                case Floor7Center:
                    return Tiles.Type.Floor7West;
                    
                case Floor8Center:
                    return Tiles.Type.Floor8West;
                    
                case Floor9Center:
                    return Tiles.Type.Floor9West;
                    
                case Floor10Center:
                    return Tiles.Type.Floor10West;
                    
                case Floor11Center:
                    return Tiles.Type.Floor11West;
                    
                case Floor12Center:
                    return Tiles.Type.Floor12West;
                    
                case Floor13Center:
                    return Tiles.Type.Floor13West;

                default: 
                    throw new Exception("Floor type not setup here: " + type.toString());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static Tiles.Type getFloorTypeEast(final Tiles.Type type)
    {
        try
        {
            switch(type)
            {
                case Floor1Center:
                    return Tiles.Type.Floor1East;
                    
                case Floor2Center:
                    return Tiles.Type.Floor2East;
                    
                case Floor3Center:
                    return Tiles.Type.Floor3East;
                    
                case Floor4Center:
                    return Tiles.Type.Floor4East;
                    
                case Floor5Center:
                    return Tiles.Type.Floor5East;
                    
                case Floor6Center:
                    return Tiles.Type.Floor6East;
                    
                case Floor7Center:
                    return Tiles.Type.Floor7East;
                    
                case Floor8Center:
                    return Tiles.Type.Floor8East;
                    
                case Floor9Center:
                    return Tiles.Type.Floor9East;
                    
                case Floor10Center:
                    return Tiles.Type.Floor10East;
                    
                case Floor11Center:
                    return Tiles.Type.Floor11East;
                    
                case Floor12Center:
                    return Tiles.Type.Floor12East;
                    
                case Floor13Center:
                    return Tiles.Type.Floor13East;

                default: 
                    throw new Exception("Floor type not setup here: " + type.toString());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return null;
    }
    
    private static Tiles.Type getRandomFloorType(final Random random)
    {
        List<Tiles.Type> options = new ArrayList<>();
        options.add(Tiles.Type.Floor1Center);
        options.add(Tiles.Type.Floor2Center);
        options.add(Tiles.Type.Floor3Center);
        options.add(Tiles.Type.Floor4Center);
        options.add(Tiles.Type.Floor5Center);
        options.add(Tiles.Type.Floor6Center);
        options.add(Tiles.Type.Floor7Center);
        options.add(Tiles.Type.Floor8Center);
        options.add(Tiles.Type.Floor9Center);
        options.add(Tiles.Type.Floor10Center);
        options.add(Tiles.Type.Floor11Center);
        options.add(Tiles.Type.Floor12Center);
        options.add(Tiles.Type.Floor13Center);
        
        //choose random tile type from list
        return options.get(random.nextInt(options.size()));
    }
    
    private static Tiles.Type getRandomPlatform(final Random random)
    {
        List<Tiles.Type> options = new ArrayList<>();
        
        options.add(Tiles.Type.Platform1);
        options.add(Tiles.Type.Platform2);
        options.add(Tiles.Type.Platform3);
        options.add(Tiles.Type.PlatformBlock1);
        options.add(Tiles.Type.PlatformBlock2);
        options.add(Tiles.Type.PlatformBlock3);
        options.add(Tiles.Type.PlatformBlock4);
        
        //choose random tile type from list
        return options.get(random.nextInt(options.size()));
    }
    
    private static Tiles.Type getRandomCloud(final Random random)
    {
        List<Tiles.Type> options = new ArrayList<>();
        
        options.add(Tiles.Type.Cloud1);
        options.add(Tiles.Type.Cloud2);
        options.add(Tiles.Type.Cloud3);
        
        //choose random tile type from list
        return options.get(random.nextInt(options.size()));
    }
    
    private static Tiles.Type getRandomBackground(final Random random)
    {
        List<Tiles.Type> options = new ArrayList<>();
        
        options.add(Tiles.Type.BackgroundPlant);
        options.add(Tiles.Type.Background1);
        options.add(Tiles.Type.Background2);
        
        //choose random tile type from list
        return options.get(random.nextInt(options.size()));
    }
    
    private static Tiles.Type getRandomCollisionObstacle(final Random random)
    {
        List<Tiles.Type> options = new ArrayList<>();
        options.add(Tiles.Type.BigBlock);
        options.add(Tiles.Type.BiggerBlock1);
        options.add(Tiles.Type.BiggerBlock2);
        options.add(Tiles.Type.BiggerBlock3);
        options.add(Tiles.Type.BiggerBlock4);
        
        /*
        options.add(Tiles.Type.HorizontalPipe1);
        options.add(Tiles.Type.HorizontalPipe2);
        options.add(Tiles.Type.HorizontalPipe3);
        options.add(Tiles.Type.HorizontalPipe4);
        options.add(Tiles.Type.HorizontalPipe5);
        options.add(Tiles.Type.HorizontalPipe6);
        options.add(Tiles.Type.HorizontalPipe7);
        options.add(Tiles.Type.HorizontalPipe8);
        */
        
        options.add(Tiles.Type.VerticalPipe1);
        options.add(Tiles.Type.VerticalPipe2);
        options.add(Tiles.Type.VerticalPipe3);
        options.add(Tiles.Type.VerticalPipe4);
        options.add(Tiles.Type.VerticalPipe5);
        options.add(Tiles.Type.VerticalPipe6);
        options.add(Tiles.Type.VerticalPipe7);
        options.add(Tiles.Type.VerticalPipe8);
        
        //choose random tile type from list
        return options.get(random.nextInt(options.size()));
    }
    
    /**
     * Create the floor for the level.<br>
     * 
     * @param level Our level object used to determine the (x,y) coordinates
     * @param tiles The object that contains all of the level's tiles
     * @param random Object used to make random decisions
     */
    public static void createFloor(final Level level, final Tiles tiles, final Random random)
    {
        //get random floor type
        Tiles.Type type = getRandomFloorType(random);
        Tiles.Type west = getFloorTypeWest(type);
        Tiles.Type east = getFloorTypeEast(type);
        
        //did we previously just create a gap, used to prevent gap from being too large
        boolean createdGap = false;
        
        //fill floor first
        for (int col = 0; col < tiles.getColumns(); col++)
        {
            //locate x,y coordinates
            int x = level.getX(col);
            int y = level.getY(tiles.getFloorRow());
            
            //if the first screen or last add all floors by default
            if (tiles.isSafeZone(col))
            {
                if (col == 0 || col == tiles.getColumns() - Level.LEVEL_COLUMNS_PER_SCREEN)
                {
                    tiles.add(west, col, tiles.getFloorRow(), x, y);
                }
                else if (col == Level.LEVEL_COLUMNS_PER_SCREEN - 1 || col == tiles.getColumns() - 1)
                {
                    tiles.add(east, col, tiles.getFloorRow(), x, y);
                }
                else
                {
                    tiles.add(type, col, tiles.getFloorRow(), x, y);
                }
            }
            else
            {
                //decide at random if we add a gap and make sure we previosuly just didn't add one
                if (random.nextBoolean() && !createdGap)
                {
                    //choose random gap size
                    final int gapCount = random.nextInt(MAX_GAP_LENGTH) + 1;
                    
                    createdGap = true;
                    
                    //if the gap count overlaps the last screen, we want the last screen to be all floor
                    if (col + gapCount >= tiles.getColumns() - Level.LEVEL_COLUMNS_PER_SCREEN)
                    {
                        col = tiles.getColumns() - Level.LEVEL_COLUMNS_PER_SCREEN;
                    }
                    else
                    {
                        //skip to the column after the gap count
                        col += gapCount;
                    }
                }
                else
                {
                    //flag false since we are adding the floor
                    createdGap = false;
                    
                    //choose how long this floor will be
                    int floorLength = random.nextInt(MAX_FLOOR_LENGTH) + 2;
                    
                    //make sure floor doesn't go into last screen
                    if (col + floorLength >= tiles.getColumns() - Level.LEVEL_COLUMNS_PER_SCREEN)
                    {
                        //adjust floor length
                        floorLength = (tiles.getColumns() - Level.LEVEL_COLUMNS_PER_SCREEN) - col;
                    }
                    
                    //add the tiles to the floor
                    for (int i=0; i < floorLength; i++)
                    {
                        //locate x,y coordinates for each tile
                        x = level.getX(col + i);
                        y = level.getY(tiles.getFloorRow());
                        
                        //add west side for first
                        if (i == 0)
                        {
                            tiles.add(west, col + i, tiles.getFloorRow(), x, y);
                        }
                        else if (col + i == tiles.getColumns() - 1 || i == floorLength - 1)
                        {
                            //add end floor and exit loop
                            tiles.add(east, col + i, tiles.getFloorRow(), x, y);
                            break;
                        }
                        else if (col + i < tiles.getColumns())
                        {
                            //everything else will have regular floor
                            tiles.add(type, col + i, tiles.getFloorRow(), x, y);
                        }
                    }
                    
                    col += floorLength;
                }
            }
        }
    }
    
    /**
     * Now place some obstacles
     * 
     * @param level Our level object used to determine the (x,y) coordinates
     * @param tiles The object that contains all of the level's tiles
     * @param random Object used to make random decisions
     */
    public static void createObstacles(final Level level, final Tiles tiles, final Random random)
    {
        for (int col = 0; col < tiles.getColumns(); col++)
        {
            //don't place any objects in safe zone
            if (tiles.isSafeZone(col))
                continue;
            
            //choose at random to skip
            if (random.nextInt(ADD_OBSTACLES_PROBABILITY) != 0)
                continue;
            
            //get random obstacle
            Tiles.Type type = getRandomCollisionObstacle(random);
            
            //is there a floor to place the obstacle on
            boolean valid = true;
            
            for (int x = 0; x < type.getColumnDimensions(); x++)
            {
                //if floor is not there this is not a valid place for the obstacle
                if (!tiles.hasTile(col + x, tiles.getFloorRow()))
                {
                    valid = false;
                    break;
                }
            }
            
            //if not valid check the next location
            if (!valid)
                continue;
            
            //the row where we are trying to place obstacle
            final int placeRow = tiles.getFloorRow() - type.getRowDimensions();

            //now make sure there is nothing blocking it
            if (!tiles.isOccupied(col, placeRow, type, false))
            {
                //locate x,y coordinates for the tile
                int x = level.getX(col);
                int y = level.getY(placeRow);

                //finally add obstacle
                tiles.add(type, col, placeRow, x, y);
            }
        }
    }
    
    /**
     * Place platforms in the level
     * 
     * @param level Our level object used to determine the (x,y) coordinates
     * @param tiles The object that contains all of the level's tiles
     * @param random Object used to make random decisions
     */
    public static void createPlatforms(final Level level, final Tiles tiles, final Random random)
    {
        for (int col = 0; col < tiles.getColumns(); col++)
        {
            //don't place any objects in safe zone
            if (tiles.isSafeZone(col))
                continue;

            //choose at random to skip
            if (random.nextInt(ADD_PLATFORMS_PROBABILITY) != 0)
                continue;

            //pick a random height
            final int difference = random.nextInt(PLATFORM_HEIGHT_DIFFERENCE);
            
            //get random obstacle
            Tiles.Type type = getRandomPlatform(random);
            
            //determine where item will be placed
            final int placeRow = tiles.getFloorRow() - difference - 1;
            
            //now make sure there is nothing blocking it
            if (!tiles.isOccupied(col, placeRow, type, false))
            {
                //locate x,y coordinates for the tile
                int x = level.getX(col);
                int y = level.getY(placeRow);

                //place platform
                tiles.add(type, col, placeRow, x, y);
            }
        }
    }
    
    /**
     * Place backgrounds in the level
     * 
     * @param level Our level object used to determine the (x,y) coordinates
     * @param tiles The object that contains all of the level's tiles
     * @param random Object used to make random decisions
     */
    public static void createBackgrounds(final Level level, final Tiles tiles, final Random random)
    {
        for (int col = 0; col < tiles.getColumns(); col++)
        {
            //choose at random to skip
            if (random.nextInt(ADD_BACKGROUNDS_PROBABILITY) != 0)
                continue;
            
            //get random obstacle
            Tiles.Type type = getRandomBackground(random);
            
            //determine where item will be placed
            final int placeRow = tiles.getFloorRow() - type.getRowDimensions();
            
            if (tiles.hasRange(type, col, placeRow))
            {
                //make sure tiles below are occupied before we add background
                if (tiles.isOccupied(col, placeRow + type.getRowDimensions(), type, true) && !tiles.isOccupied(col, placeRow, type, false))
                {
                    //locate x,y coordinates for the tile
                    int x = level.getX(col);
                    int y = level.getY(placeRow);

                    //place background
                    tiles.add(type, col, placeRow, x, y);
                }
            }
        }
    }
    
    /**
     * Place clouds in the level
     * 
     * @param level Our level object used to determine the (x,y) coordinates
     * @param tiles The object that contains all of the level's tiles
     * @param random Object used to make random decisions
     */
    public static void createClouds(final Level level, final Tiles tiles, final Random random)
    {
        for (int col = 0; col < tiles.getColumns(); col++)
        {
            //choose at random to skip
            if (random.nextInt(ADD_CLOUDS_PROBABILITY) != 0)
                continue;
            
            //get random obstacle
            Tiles.Type type = getRandomCloud(random);
            
            //determine where item will be placed
            final int placeRow = random.nextInt(Level.LEVEL_ROWS_PER_SCREEN / 3);
            
            if (tiles.hasRange(type, col, placeRow))
            {
                if (!tiles.isOccupied(col, placeRow, type, false))
                {
                    //locate x,y coordinates for the tile
                    int x = level.getX(col);
                    int y = level.getY(placeRow);

                    //place cloud
                    tiles.add(type, col, placeRow, x, y);
                }
            }
        }
    }
}