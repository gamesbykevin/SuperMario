package com.gamesbykevin.mario.world.level;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.enemies.Enemies;
import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.world.level.powerups.PowerUps;
import com.gamesbykevin.mario.world.level.tiles.Tile;
import com.gamesbykevin.mario.world.level.tiles.Tiles;
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
    
    //our enemies in the game
    private Enemies enemies;
    
    //is the level complete
    private boolean complete = false;
    
    //are we to not have enemies
    private boolean hide = false;
    
    //our level timer
    private Timer timer;
    
    //the default level duration is 5 minutes
    private static final long LEVEL_DURATION = Timers.toNanoSeconds(5);
    
    /**
     * Create a new level
     */
    public Level(final Rectangle boundary)
    {
        //set boundary
        this.boundary = boundary;
        
        //create a new timer
        this.timer = new Timer(LEVEL_DURATION);
        
        //pause at first
        startTimer();
    }
    
    public void hideEnemies()
    {
        this.hide = true;
    }
    
    public void showEnemies()
    {
        this.hide = false;
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
    
    /**
     * Has the level been solved
     * @return true if hero hit goal switch, false otherwise
     */
    public boolean isComplete()
    {
        return this.complete;
    }
    
    /**
     * Mark the level as solved
     */
    public void markComplete()
    {
        this.complete = true;
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
    
    public void placeEnemies(final Image image, final Random random)
    {
        if (this.enemies == null)
        {
            //create new container for the enemies
            this.enemies = new Enemies(image, boundary);
        }
        else
        {
            //remove any existing enemies
            this.enemies.reset();
        }
        
        //place enemies in level
        this.enemies.placeEnemies(getTiles(), random);
    }
    
    public Timer getTimer()
    {
        return this.timer;
    }
    
    /**
     * Has the timer finished
     * @return true if the timer has run out, false otherwise
     */
    public boolean hasTimePassed()
    {
        return getTimer().hasTimePassed();
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
    
    public Enemies getEnemies()
    {
        return this.enemies;
    }
    
    public Rectangle getBoundary()
    {
        return this.boundary;
    }
    
    /**
     * Move all everything back to start coordinates
     */
    public void reset()
    {
        //x offset
        final double x = getTiles().getTile(0, 0).getX();
        
        //reset level timer
        getTimer().reset();
        
        //move tiles back
        getTiles().moveTiles(-x, 0);
        
        //move enemies back as well
        getEnemies().moveEnemies(-x, 0);
        
        //remove all projectiles
        getEnemies().removeProjectiles();
        
        //move power ups back as well
        getPowerUps().movePowerUps(-x, 0);
        
        //remove all effects
        getEffects().removeAll();
        
        //stop scrolling
        setScrollX(0);
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
        
        if (enemies != null)
        {
            enemies.dispose();
            enemies = null;
        }
    }
    
    @Override
    public void update(final Engine engine)
    {
        //update tiles
        tiles.update(engine.getMain().getTime(), getScrollX());
        
        //update power ups
        powerUps.update(engine.getMain().getTime(), getScrollX(), engine.getRandom(), getTiles());
        
        //update location
        background.update(getBoundary().x, getBoundary().x + getBoundary().width, engine.getMain().getTime());
        
        //update effects animation
        effects.update(engine.getMain().getTime(), getScrollX());
        
        //if level is not solved
        if (!isComplete())
        {
            //if we aren't hiding the enemies
            if (!hide)
            {
                //update enemies
                enemies.update(engine);
            }
        }
        
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
        
        //draw the effects
        effects.render(graphics);
        
        //if we are not hiding enemies
        if (!hide)
        {
            //now draw the enemies
            enemies.render(graphics);
        }
        
        //draw solid level tiles after
        getTiles().renderSolidTiles(graphics, getBoundary());
        
        //if we are not hiding enemies
        if (!hide)
        {
            //draw projectiles on top of solid tiles
            enemies.renderProjectiles(graphics);
        }
    }
}