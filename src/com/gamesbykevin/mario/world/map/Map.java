package com.gamesbykevin.mario.world.map;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.labyrinth.Labyrinth.Algorithm;
import com.gamesbykevin.framework.labyrinth.*;
import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.heroes.AnimationHelper;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.resources.GameAudio;
import com.gamesbykevin.mario.shared.Displayable;
import com.gamesbykevin.mario.shared.IAudio;
import com.gamesbykevin.mario.shared.IElement;
import com.gamesbykevin.mario.shared.IProgress;
import com.gamesbykevin.mario.shared.Shared;
import com.gamesbykevin.mario.world.World;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Map implements Disposable, IElement, Displayable, IProgress, IAudio
{
    //list of all possible tiles
    private List<Tile> tiles;
    
    //the image containing all the map tiles
    private Image image;
    
    //the layout of the map
    private Labyrinth maze;
    
    // the type of tile at each location
    private Tile.Type[][] types;
    
    //the dimensions of each cell
    private static final int CELL_WIDTH  = 48;
    private static final int CELL_HEIGHT = 48;

    //dimensions of the map
    private static final int MAP_ROWS = 4;
    private static final int MAP_COLS = 5;
    
    //where the first tile (0,0) will be drawn
    private static final int START_X = 0;
    private static final int START_Y = 0;
    
    //where to draw the world #
    private static final Point WORLD_DISPLAY = new Point(15, 15);
    
    //the image we choose for the path
    private Tile.Type pathHorizontal, pathVertical;
    
    //the tile for the background and moving background
    private Tile.Type background, backgroundMotion;
    
    //where the hero is/was/and is going to be
    private Cell current, previous, target;
    
    //is this oject being displayed
    private boolean display = true;
    
    //is creation complete
    private boolean complete = false;
    
    //the world #
    private int world = 0;
    
    //the font for displayed text
    private Font font;
    
    private String worldDescription = null;
    
    //do we need to play a sound
    private GameAudio.Keys audioKey = null, mapMusic = null;
    
    public enum Selection
    {
        Level, MatchingGame, SlotGame
    }
    
    /**
     * Create a new map
     * @param image The image containing all the tiles for the map
     */
    public Map(final Image image, final Font font)
    {
        this.font = font.deriveFont(12f);
        
        //where the hero is located on the map
        this.current = new Cell();

        //where the hero previously was
        this.previous = new Cell();

        //where the hero wants to be
        this.target = new Cell();

        //store image
        this.image = image;

        //create new HashMap
        this.tiles = new ArrayList<>();

        //create new array
        this.types = new Tile.Type[MAP_ROWS][MAP_COLS];

        //create a single instance of each tile type
        for (int i = 0; i < Tile.TYPES_COUNT; i++)
        {
            //add to list
            add(new Tile(Tile.Type.values()[i]));
        }
    }
    
    private void add(final Tile tile)
    {
        tiles.add(tile);
    }
    
    
    private Tile getTile(final int index)
    {
        return tiles.get(index);
    }
    
    private Tile getTile(final Tile.Type type)
    {
        for (int i = 0; i < Tile.TYPES_COUNT; i++)
        {
            if (getTile(i).getType() == type)
                return getTile(i);
        }
        
        return null;
    }
    
    private void setWorldDescription()
    {
        //assign the appropriate world description
        worldDescription = (!hasSolved()) ? "World - " + world : "World - " + world + " (Complete)";
    }
    
    public void reset(final Random random, final int count)
    {
        try
        {
            //set background music for map
            mapMusic = GameAudio.getMapMusic(random);
            
            //set the world #
            nextWorld();
            
            current.setCol(0);
            current.setRow(0);

            previous.setCol(0);
            previous.setRow(0);

            target.setCol(0);
            target.setRow(0);
            
            //assign null to all types in array
            for (int row = 0; row < types.length; row++)
            {
                for (int col = 0; col < types[0].length; col++)
                {
                    assignType(row, col, null);
                }
            }
            
            //create new maze of the specified size
            this.maze = new Labyrinth(types[0].length, types.length, Algorithm.values()[random.nextInt(Algorithm.values().length)]);

            //set start location
            this.maze.setStart(0, 0);

            //genearate maze
            this.maze.generate();

            //the highest cost
            int cost = 0;

            //the location with highest cost will be the goal in the maze
            for (int row = 0; row < maze.getRows(); row++)
            {
                for (int col = 0; col < maze.getCols(); col++)
                {
                    //exclude the start location
                    if (maze.getStart().equals(col, row))
                        continue;

                    //locate highest cost
                    if (maze.getLocation(col, row).getCost() > cost)
                    {
                        //store the current cost to beat
                        cost = maze.getLocation(col, row).getCost();

                        //set the current location as the goal
                        maze.setFinish(col, row);
                    }
                }
            }

            //set the appropriate level tiles
            setLevelTiles(random, count);

            //set the start tile
            assignType(maze.getStart(), Tile.Type.Start);

            //create the bonus rooms
            setBonusRooms(random);

            //determine the path image
            setupPath(random);

            //determine the background tile
            setupBackground(random);

            //set the world description
            setWorldDescription();
            
            //mark complete
            setComplete(true);
            
            setAudioKey(mapMusic);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Is the map solved
     * @return true if all levels are complete, false otherwise
     */
    public boolean hasSolved()
    {
        return (getLevelCount() < 1);
    }
    
    /**
     * Count the number of existing unsolved levels
     * @return The count of any level that has not been completed
     */
    private int getLevelCount()
    {
        int count = 0;
        
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int col = 0; col < maze.getCols(); col++)
            {
                //if this is a level tile increase count
                if (Tile.isLevelTile(getType(col, row)))
                    count++;
            }
        }
        
        return count;
    }
    
    /**
     * Increase the world #
     */
    private void nextWorld()
    {
        this.world++;
    }
    
    @Override
    public void setAudioKey(final GameAudio.Keys audioKey)
    {
        this.audioKey = audioKey;
    }
    
    @Override
    public GameAudio.Keys getAudioKey()
    {
        return this.audioKey;
    }
    
    
    @Override
    public boolean isComplete()
    {
        return this.complete;
    }
    
    @Override
    public void setComplete(final boolean complete)
    {
        this.complete = complete;
    }
    
    @Override
    public void setDisplayed(final boolean display)
    {
        this.display = display;
        
        //if we are displaying this again
        if (display)
        {
            //if there are still existing levels
            if (getLevelCount() > 0)
            {
                //we will be playing background music again
                setAudioKey(this.mapMusic);
            }
            else
            {
                //we will be playing world clear
                setAudioKey(GameAudio.Keys.MusicMapWorldClear);
            }
        }
    }
    
    @Override
    public boolean isDisplayed()
    {
        return this.display;
    }
    
    private boolean isDeadEnd(final Location location)
    {
        return (location.getWalls().size() == Location.Wall.values().length - 1);
    }
    
    private void setupBackground(final Random random)
    {
        List<Tile.Type> options = new ArrayList<>();
        
        //add valid background tiles to list
        for (int i = 0; i < Tile.TYPES_COUNT; i++)
        {
            if (Tile.isBackgroundTile(getTile(i).getType()))
            {
                options.add(getTile(i).getType());
            }
        }
        
        //choose random background tile
        this.background = options.get(random.nextInt(options.size()));
        
        //clear list
        options.clear();
        
        //add valid background motion tiles to list
        for (int i = 0; i < Tile.TYPES_COUNT; i++)
        {
            if (Tile.isBackgroundMotionTile(getTile(i).getType()))
            {
                options.add(getTile(i).getType());
            }
        }
        
        //choose random background motion tile
        this.backgroundMotion = options.get(random.nextInt(options.size()));
    }
    
    private void setBonusRooms(final Random random)
    {
        List<Cell> options = new ArrayList<>();
        
        //locate remaining possible tiles for bonus stages
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int col = 0; col < maze.getCols(); col++)
            {
                if (getType(col, row) == null)
                    options.add(new Cell(col, row));
            }
        }

        int count = 0;
        
        //continue until all options are empty or we added the maximum allowed
        while (!options.isEmpty() && count < World.BONUS_STAGES)
        {
            //pick random index
            final int index = random.nextInt(options.size());
            
            //if there is already a game nearby 
            if (hasGameNeighbor(options.get(index)))
            {
                //remove from list
                options.remove(index);
            }
            else
            {
                if (random.nextBoolean())
                {
                    //add mushroom house (slots)
                    assignType(options.get(index), (random.nextBoolean() ? Tile.Type.MushroomHouse1 : Tile.Type.MushroomHouse2));
                }
                else
                {
                    //add card game (card matching)
                    assignType(options.get(index), Tile.Type.CardMatching);
                }
                
                //track count
                count++;
            }
        }
    }
    
    /**
     * Do any of the neighbor cells contain a game already
     * @param cell The cell where we want to check all neighbors
     * @return true if slot game or matching game, false otherwise
     */
    private boolean hasGameNeighbor(final Cell cell)
    {
        final int depth = 2;
        
        for (int row = -depth; row <= depth; row++)
        {
            for (int col = -depth; col <= depth; col++)
            {
                //get the current type
                final Tile.Type type = getType(cell.getCol() + col, cell.getRow() + row);
                
                //if type exists and is a game tile return true
                if (type != null && Tile.isGameTile(type))
                    return true;
            }
        }
        
        //no game neighbor found
        return false;
    }
    
    private int assignTypes(final List<Cell> options, final Random random, final int count)
    {
        //keep track of total levels added
        int total = 0;

        //keep looping until we found a place for the number of levels or the list of options runs out
        while (!options.isEmpty() && total < count)
        {
            final int index = random.nextInt(options.size());

            //track number of marked tiles
            total++;

            //we will place a level tile here
            assignType(options.get(index), Tile.Type.Level01);

            //remove from list
            options.remove(index);
        }
        
        //return the total number of level tiles added
        return total;
    }
    
    private void setLevelTiles(final Random random, final int count) throws Exception
    {
        //list containing dead ends
        List<Cell> deadEnds = new ArrayList<>();
        
        //other places to place the level tiles
        List<Cell> otherOptions = new ArrayList<>();
        
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int col = 0; col < maze.getCols(); col++)
            {
                //exclude the start and finish
                if (maze.getStart().equals(col, row) || maze.getFinish().equals(col, row))
                    continue;
                
                //for right now we only want the dead ends
                if (isDeadEnd(maze.getLocation(col, row)))
                {
                    //add valid option to list
                    deadEnds.add(new Cell(col, row));
                }
                else
                {
                    otherOptions.add(new Cell(col, row));
                }
            }
        }
        
        //assign to the dead ends and return the number added
        final int added = assignTypes(deadEnds, random, count - 1);
        
        //if we still have more level tiles to add
        if (added < count - 1)
        {
            //add the remaining levels to the list of other options
            assignTypes(otherOptions, random, count - 1 - added);
        }
        
        //mark the goal as a level
        assignType(maze.getFinish(), Tile.Type.Level01);
        
        //clear list
        otherOptions.clear();
        
        //add all locations that are level tiles
        for (int row = 0; row < types.length; row++)
        {
            for (int col = 0; col < types[0].length; col++)
            {
                if (getType(col, row) == null)
                    continue;

                //add this location because it is a level tile
                if (getType(col, row) == Tile.Type.Level01)
                    otherOptions.add(new Cell(col, row));
            }
        }

        //sort the locations by cost so the level #'s appear in numeric order on map
        for(int i = 0; i < otherOptions.size(); i++)
        {
            for(int x = 1; x < (otherOptions.size() - i); x++)
            {
                if(maze.getLocation(otherOptions.get(x - 1)).getCost() > maze.getLocation(otherOptions.get(x)).getCost())
                {
                    //swap the elements
                    Cell temp = otherOptions.get(x - 1);
                    otherOptions.set(x - 1, otherOptions.get(x));
                    otherOptions.set(x, temp);
                }
            }
        }
        
        //we have sorted list, now assign level tiles
        for (int i = 0; i < otherOptions.size(); i++)
        {
            switch (i)
            {
                case 0:
                    assignType(otherOptions.get(i), Tile.Type.Level01);
                    break;

                case 1:
                    assignType(otherOptions.get(i), Tile.Type.Level02);
                    break;

                case 2:
                    assignType(otherOptions.get(i), Tile.Type.Level03);
                    break;

                case 3:
                    assignType(otherOptions.get(i), Tile.Type.Level04);
                    break;

                case 4:
                    assignType(otherOptions.get(i), Tile.Type.Level05);
                    break;

                case 5:
                    assignType(otherOptions.get(i), Tile.Type.Level06);
                    break;

                case 6:
                    assignType(otherOptions.get(i), Tile.Type.Level07);
                    break;

                case 7:
                    assignType(otherOptions.get(i), Tile.Type.Level08);
                    break;

                case 8:
                    assignType(otherOptions.get(i), Tile.Type.Level09);
                    break;

                case 9:
                    assignType(otherOptions.get(i), Tile.Type.Level10);
                    break;

                default:
                    throw new Exception("Level Tile not setup here.");
            }
        }
    }
    
    private void setupPath(final Random random) throws Exception
    {
        //the different types of paths to choose from
        List<Tile.Type> paths = new ArrayList<>();

        for (int i = 0; i < Tile.TYPES_COUNT; i++)
        {
            if (Tile.isPathTile(getTile(i).getType()))
                paths.add(getTile(i).getType());
        }
        
        //get random type
        Tile.Type type = paths.get(random.nextInt(paths.size()));
        
        switch (type)
        {
            case PathHorizontalYellow:
                this.pathHorizontal = Tile.Type.PathHorizontalYellow;
                this.pathVertical = Tile.Type.PathVerticalYellow;
                break;

            case PathHorizontalGreen:
                this.pathHorizontal = Tile.Type.PathHorizontalGreen;
                this.pathVertical = Tile.Type.PathVerticalGreen;
                break;

            case PathHorizontalGray:
                this.pathHorizontal = Tile.Type.PathHorizontalGray;
                this.pathVertical = Tile.Type.PathVerticalGray;
                break;

            case PathHorizontalBrown:
                this.pathHorizontal = Tile.Type.PathHorizontalBrown;
                this.pathVertical = Tile.Type.PathVerticalBrown;
                break;

            case PathHorizontalBlue:
                this.pathHorizontal = Tile.Type.PathHorizontalBlue;
                this.pathVertical = Tile.Type.PathVerticalBlue;
                break;

            default:
                throw new Exception("Path type not setup here: " + type.toString());
        }
    }
    
    /**
     * Manage the hero on the map according to hero velocity.<br>
     * Will also prevent hero from advancing past levels that aren't complete
     * @param hero The hero we manage
     */
    public void checkHero(final Hero hero)
    {
        try
        {
            Location location = maze.getLocation(getCurrent());

            if (hero.getVelocityX() > 0)
            {
                //if moving in specified direction and hit wall
                if (location.hasWall(Location.Wall.East))
                {
                    //stop moving
                    hero.resetVelocity();
                    
                    //reset back to position
                    resetHero(hero);
                }
                else
                {
                    //set the target location
                    setTarget(getCurrent().getCol() + 1, getCurrent().getRow());
                    
                    //make sure the hero can move towards the target
                    checkMove(hero);
                }
            }
            else if (hero.getVelocityX() < 0)
            {
                //if moving in specified direction and hit wall
                if (location.hasWall(Location.Wall.West))
                {
                    //stop moving
                    hero.resetVelocity();
                    
                    //reset back to position
                    resetHero(hero);
                }
                else
                {
                    //set the target location
                    setTarget(getCurrent().getCol() - 1, getCurrent().getRow());
                    
                    //make sure the hero can move towards the target
                    checkMove(hero);
                }
            }
            else if (hero.getVelocityY() > 0)
            {
                //if moving in specified direction and hit wall
                if (location.hasWall(Location.Wall.South))
                {
                    //stop moving
                    hero.resetVelocity();
                    
                    //reset back to position
                    resetHero(hero);
                }
                else
                {
                    //set the target location
                    setTarget(getCurrent().getCol(), getCurrent().getRow() + 1);
                    
                    //make sure the hero can move towards the target
                    checkMove(hero);
                }
            }
            else if (hero.getVelocityY() < 0)
            {
                //if moving in specified direction and hit wall
                if (location.hasWall(Location.Wall.North))
                {
                    //stop moving
                    hero.resetVelocity();
                    
                    //reset back to position
                    resetHero(hero);
                }
                else
                {
                    //set the target location
                    setTarget(getCurrent().getCol(), getCurrent().getRow() - 1);
                    
                    //make sure the hero can move towards the target
                    checkMove(hero);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Check the hero current and target location.<br>
     * If the current location is a level tile, then the hero can't move past it and can only move back to previous location.<br>
     * Otherwise the hero can move in the specified location so we will store the current location as the previous.
     * @param hero The hero we are checking.
     */
    private void checkMove(final Hero hero)
    {
        //if the current tile is a level tile
        if (Tile.isLevelTile(getType()))
        {
            //player can only move back to the previous location
            if (!getTarget().equals(getPrevious()))
            {
                //stop moving
                hero.resetVelocity();

                //reset back to current position
                resetHero(hero);
            }
        }
        else
        {
            //assign previous location
            setPrevious();
        }
    }
    
    /**
     * Assign the previous position according to where the hero's current location
     */
    private void setPrevious()
    {
        previous.setCol(current);
        previous.setRow(current);
    }
    
    /**
     * Assign the hero's target location
     * @param col Column
     * @param row Row
     */
    private void setTarget(final double col, final double row)
    {
        target.setCol((int)col);
        target.setRow((int)row);
    }
    
    /**
     * Assign the hero's current location
     * @param col Column
     * @param row Row
     */
    private void setCurrent(final double col, final double row)
    {
        current.setCol((int)col);
        current.setRow((int)row);
    }
    
    private Cell getPrevious()
    {
        return this.previous;
    }
    
    private Cell getCurrent()
    {
        return this.current;
    }
    
    private Cell getTarget()
    {
        return this.target;
    }
    
    /**
     * Get the type of tile at the hero's location
     * @return The type of tile at the hero's current location
     */
    private Tile.Type getType()
    {
        return types[(int)getCurrent().getRow()][(int)getCurrent().getCol()];
    }
    
    public Selection getSelection()
    {
        if (Tile.isCardMatchingTile(getType()))
        {
            return Selection.MatchingGame;
        }
        else if (Tile.isMushroomHouseTile(getType()))
        {
            return Selection.SlotGame;
        }
        else if (getLevelIndex() > -1)
        {
            return Selection.Level;
        }
        else
        {
            return null;
        }
    }
    
    /**
     * Get the level index of the current level
     * @return The index of the current level. -1 will be returned if not found
     */
    public int getLevelIndex()
    {
        return Tile.getLevelIndex(getType());
    }
    
    /**
     * Mark the current tile where the hero is as complete
     */
    public void markLevelComplete()
    {
        types[(int)getCurrent().getRow()][(int)getCurrent().getCol()] = Tile.Type.LevelComplete;
        
        //update description in case map is solved
        if (hasSolved())
            setWorldDescription();
    }
    
    /**
     * Place the hero accordingly on the mini-map
     * @param hero The hero we are showing on the map
     */
    public void resetHero(final Hero hero)
    {
        //set the appropriate animation
        AnimationHelper.setAnimationMiniMap(hero);
        
        //set the default dimensions
        hero.setDimensions();
        
        //position hero in center
        hero.setLocation(
            getStartX(getCurrent().getCol()) - (hero.getWidth()  / 2), 
            getStartY(getCurrent().getRow()) - (hero.getHeight() / 2)
        );
    }
    
    private void assignType(final Cell cell, final Tile.Type type)
    {
        assignType((int)cell.getRow(), (int)cell.getCol(), type);
    }
    
    private Tile.Type getType(final double col, final double row)
    {
        return getType((int)col, (int)row);
    }
    
    private Tile.Type getType(final int col, final int row)
    {
        //if out of bounds, return null
        if (row < 0 || row >= types.length)
            return null;
        if (col < 0 || col >= types[0].length)
            return null;
        
        return types[row][col];
    }
    
    private void assignType(final int row, final int col, final Tile.Type type)
    {
        types[row][col] = type;
    }
    
    @Override
    public void dispose()
    {
        if (tiles != null)
        {
            for (int i = 0; i < Tile.TYPES_COUNT; i++)
            {
                tiles.get(i).dispose();
                tiles.set(i, null);
            }

            tiles.clear();
            tiles = null;
        }
        
        if (types != null)
        {
            types = null;
        }
        
        if (image != null)
        {
            image.flush();
            image = null;
        }
        
        if (maze != null)
        {
            maze.dispose();
            maze = null;
        }
        
        if (font != null)
            font = null;
    }
    
    @Override
    public void update(final Engine engine)
    {
        if (getAudioKey() != null)
        {
            //stop all other sound
            engine.getResources().stopAllSound();

            //start to play background sound
            engine.getResources().playGameAudio(getAudioKey(), (getLevelCount() > 0));

            //remove from list
            setAudioKey(null);
        }
        
        //update the tile animations/location
        for (int i = 0; i < Tile.TYPES_COUNT; i++)
        {
            getTile(i).update(engine);
        }

        final Hero hero = engine.getManager().getMario();
        
        //check the hero
        final double x = getStartX(getTarget().getCol());
        final double y = getStartY(getTarget().getRow());
        
        if (hero.getVelocityX() > 0)
        {
            //correct coordinate(s) if reached destination 
            if (hero.getX() >= x - (hero.getWidth() / 2))
            {
                hero.setX(x - (hero.getWidth() / 2));
                hero.resetVelocity();
                setCurrent(getTarget().getCol(), getCurrent().getRow());
                engine.getResources().playGameAudio(GameAudio.Keys.SfxMapMove);
            }
        }
        else if (hero.getVelocityX() < 0)
        {
            //correct coordinate(s) if reached destination 
            if (hero.getX() <= x - (hero.getWidth() / 2))
            {
                hero.setX(x - (hero.getWidth() / 2));
                hero.resetVelocity();
                setCurrent(getTarget().getCol(), getCurrent().getRow());
                engine.getResources().playGameAudio(GameAudio.Keys.SfxMapMove);
            }
        }
        else if (hero.getVelocityY() > 0)
        {
            //correct coordinate(s) if reached destination 
            if (hero.getY() >= y - (hero.getHeight() / 2))
            {
                hero.setY(y - (hero.getHeight() / 2));
                hero.resetVelocity();
                setCurrent(getCurrent().getCol(), getTarget().getRow());
                engine.getResources().playGameAudio(GameAudio.Keys.SfxMapMove);
            }
        }
        else if (hero.getVelocityY() < 0)
        {
            //correct coordinate(s) if reached destination 
            if (hero.getY() <= y - (hero.getHeight() / 2))
            {
                hero.setY(y - (hero.getHeight() / 2));
                hero.resetVelocity();
                setCurrent(getCurrent().getCol(), getTarget().getRow());
                engine.getResources().playGameAudio(GameAudio.Keys.SfxMapMove);
            }
        }
    }
    
    private double getStartX(final double col)
    {
        return (START_X + (col * CELL_WIDTH) + (CELL_WIDTH / 2));
    }
    
    private double getStartY(final double row)
    {
        return (START_Y + (row * CELL_HEIGHT) + (CELL_HEIGHT / 2));
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //our temp tile
        Tile tile;
        
        //draw the background tile
        tile = getTile(background);
        tile.setDimensions();
        
        for (int x = 0; x <= Shared.ORIGINAL_WIDTH; x += tile.getWidth())
        {
            for (int y = 0; y <= Shared.ORIGINAL_HEIGHT; y += tile.getHeight())
            {
                tile.setLocation(x, y);
                tile.draw(graphics, image);
            }
        }
        
        if (worldDescription != null)
        {
            //white font color
            graphics.setColor(Color.WHITE);
            
            if (graphics.getFont() != font)
            {
                //set the appropriate font
                graphics.setFont(font);
            }
            
            //display the world # description
            graphics.drawString(worldDescription, WORLD_DISPLAY.x, WORLD_DISPLAY.y);
        }
        
        for (int row = 0; row < maze.getRows(); row++)
        {
            for (int col = 0; col < maze.getCols(); col++)
            {
                try
                {
                    //calculate the middle location of the current cell
                    double middleX = getStartX(col);
                    double middleY = getStartY(row);
                    
                    if (col < maze.getCols() - 1 && row < maze.getRows() - 1)
                    {
                        //get the background motion tile and draw it
                        tile = getTile(backgroundMotion);
                        tile.setLocation(
                            middleX - (tile.getWidth() / 2) + (CELL_WIDTH / 2), 
                            middleY - (tile.getHeight() / 2) + (CELL_HEIGHT / 2));
                        tile.draw(graphics, image);
                    }
                    
                    //get current location
                    Location location = maze.getLocation(col, row);
                    
                    //if (location.hasWall(Location.Wall.East) && !location.hasWall(Location.Wall.West))
                    if (!location.hasWall(Location.Wall.West))
                    {
                        //get current tile
                        tile = getTile(pathHorizontal);
                        tile.setDimensions();
                        tile.setWidth(CELL_WIDTH / 2);
                        tile.setLocation(middleX - tile.getWidth(), middleY - (tile.getHeight() / 2));
                        tile.draw(graphics, image);
                    }
                    
                    //if (!location.hasWall(Location.Wall.East) && location.hasWall(Location.Wall.West))
                    if (!location.hasWall(Location.Wall.East))
                    {
                        tile = getTile(pathHorizontal);
                        tile.setDimensions();
                        tile.setWidth(CELL_WIDTH / 2);
                        tile.setLocation(middleX, middleY - (tile.getHeight() / 2));
                        tile.draw(graphics, image);
                    }
                    
                    if (!location.hasWall(Location.Wall.North))
                    {
                        tile = getTile(pathVertical);
                        tile.setDimensions();
                        tile.setHeight(CELL_HEIGHT / 2);
                        tile.setLocation(middleX - (tile.getWidth() / 2), middleY - tile.getHeight());
                        tile.draw(graphics, image);
                    }
                    
                    if (!location.hasWall(Location.Wall.South))
                    {
                        tile = getTile(pathVertical);
                        tile.setDimensions();
                        tile.setHeight(CELL_HEIGHT / 2);
                        tile.setLocation(middleX - (tile.getWidth() / 2), middleY);
                        tile.draw(graphics, image);
                    }

                    if (getType(col, row) != null)
                    {
                        tile = getTile(getType(col, row));
                        tile.setDimensions();
                        tile.setLocation(middleX - (tile.getWidth() / 2), middleY - (tile.getHeight() / 2));
                        tile.draw(graphics, image);
                    }
                    else
                    {
                        tile = getTile(Tile.Type.Dot);
                        tile.setDimensions();
                        tile.setLocation(middleX - (tile.getWidth() / 2), middleY - (tile.getHeight() / 2));
                        tile.draw(graphics, image);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}