package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.world.level.Level;
import com.gamesbykevin.mario.world.level.LevelCreatorHelper;
import com.gamesbykevin.mario.world.level.tiles.Tile;
import com.gamesbykevin.mario.world.level.tiles.Tiles;
import com.gamesbykevin.mario.shared.IElement;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Enemies implements Disposable, IElement
{
    private List<Enemy> enemies;
    
    //our sprite sheet
    private Image image;
    
    //the area where the action takes place, this will determine what enemies are rendered as well as a.i. logic
    private Rectangle boundary;
    
    //probability that an enemy will be added given the position is available for this
    private static final int ADD_ENEMY_PROBABILITY = 4;
    
    //probability that an enemy will be added given the position is available for this
    private static final int ADD_ENEMY_THWOMP_PROBABILITY = 10;
    
    private static final int THWOMP_DISTANCE_REQUIREMENT = Tile.WIDTH * 10;
    
    //probability if the tile is a pipe that it can have a plant in it
    private static final int ADD_ENEMY_PLANT_PROBABILITY = 2;
    
    //the number of tiles the enemy needs around it to be placed
    private static final int ADD_ENEMY_PLATFORM_REQUIREMENT = 3;
    
    //the number of pixels that needs to be between enemies
    private static final int ENEMY_DISTANCE_REQUIREMENT = Tile.WIDTH * 5;
    
    public enum Type
    {
        TurtleGreen, TurtleRed, TurtleGreenWings, TurtleRedWings, TurtleGreenBig, 
        TurtleGreenWingsBig, TurtleRedBig, TurtleRedWingsBig, Spiny, Boo, 
        GoombaBig, BuzzyBeetle, TurtleSkeleton, Goomba, GoombaWings, 
        GoombaRed, GoombaRedWings, BulletBill, Spike, 
        BoomerangBros, HammerBros, FireballBros, 
        
        //keep these 2 at the end so they aren't involved when choosing a random enemy type
        Plant, Thwomp, 
    }
    
    public Enemies(final Image image, final Rectangle boundary)
    {
        //create new list
        this.enemies = new ArrayList<>();
        
        //store sprite sheet
        this.image = image;
        
        //store window for a.i. logic and to determine what is rendered
        this.boundary = boundary;
    }
    
    public List<Enemy> getEnemies()
    {
        return this.enemies;
    }
    
    @Override
    public void dispose()
    {
        if (enemies != null)
        {
            for (int i = 0; i < enemies.size(); i++)
            {
                enemies.get(i).dispose();
                enemies.set(i, null);
            }
            
            enemies.clear();
            enemies = null;
        }
        
        if (image != null)
        {
            image.flush();
            image = null;
        }
        
        boundary = null;
    }
    
    /**
     * Remove all enemies
     */
    public void reset()
    {
        enemies.clear();
    }
    
    /**
     * Determine where enemies will be placed
     * @param level The level
     * @param random Object used to make random decisions
     */
    public void placeEnemies(final Tiles tiles, final Random random)
    {
        //get the size of the level tiles
        final int cols = tiles.getColumns();
        final int rows = tiles.getRows();
        
        for (int col = 0; col < cols; col++)
        {
            //skip if we are in the safe zone
            if (tiles.isSafeZone(col))
                continue;
            
            for (int row = 0; row < rows; row++)
            {
                //don't check past the floor or the very top
                if (row > tiles.getFloorRow() || row < 2)
                    continue;
                
                try
                {
                    //locate the x,y location of the current column, row
                    final double tileCenterX = (col * Tile.WIDTH) + (Tile.WIDTH / 2);
                    final double tileY = row * Tile.HEIGHT;
                    
                    //enemies need a floor below
                    if (!tiles.hasFloorBelow(tileCenterX))
                        continue;
                    
                    //if there is a tile here
                    if (tiles.hasTile(col, row))
                    {
                        //get the type of the current tile
                        Tile tile = tiles.getTile(col, row);

                        //if this is a pipe determine if we are to add a plant inside
                        if (LevelCreatorHelper.isPipe(tile.getType()))
                        {
                            //west side
                            final Tile tmp1 = tiles.getTile(col - 1, row);
                            final Tile tmp2 = tiles.getTile(col - 1, row + 1);

                            //east side
                            final Tile tmp3 = tiles.getTile(col + 1, row);
                            final Tile tmp4 = tiles.getTile(col + 1, row + 1);

                            //if the west side is not part of the pipe but the east side is, then this is valid
                            if ((tmp1 == null || tmp1.getType() != tile.getType()) && 
                                (tmp2 == null || tmp2.getType() != tile.getType()) && 
                                (tmp3 != null && tmp3.getType() == tile.getType()) && 
                                (tmp4 != null && tmp4.getType() == tile.getType()))
                            {
                                //now choose at random if we are to add a plant
                                if (random.nextInt(ADD_ENEMY_PLANT_PROBABILITY) == 0)
                                {
                                    //add random enemy
                                    add(tileCenterX, tileY + (Tile.HEIGHT * 2), Type.Plant, random);
                                }
                            }
                        }
                        else
                        {
                            boolean valid = true;

                            //check the tiles at and directly above the location
                            for (int x = 0; x < ADD_ENEMY_PLATFORM_REQUIREMENT; x++)
                            {
                                //get temp tile above
                                tile = tiles.getTile(col + x, row - 1);

                                //if the tile above where we want to place isn't a background tile
                                if (tile != null && !LevelCreatorHelper.isBackgroundTile(tile))
                                {
                                    valid = false;
                                    break;
                                }
                                
                                //make sure all have floor below
                                if (!tiles.hasTile(col + x, tiles.getFloorRow()))
                                {
                                    valid = false;
                                    break;
                                }
                                
                                //get the current row
                                tile = tiles.getTile(col + x, row);
                                
                                //make sure current row has tiles
                                if (tile == null || LevelCreatorHelper.isBackgroundTile(tile))
                                {
                                    valid = false;
                                    break;
                                }
                            }

                            //if valid place for enemy
                            if (valid)
                            {
                                //calculate x-ccordinate where enemy will be placed
                                final double x = (col + (int)(ADD_ENEMY_PLATFORM_REQUIREMENT / 2)) * Tile.WIDTH;
                                
                                //check that this location isn't so close to other enemies
                                if (hasAvailableSpace(x, ENEMY_DISTANCE_REQUIREMENT))
                                {
                                    //we have verified everything, finally decide at random if we are to add enemy
                                    if (random.nextInt(ADD_ENEMY_PROBABILITY) == 0)
                                    {
                                        add(x, tileY, Type.values()[random.nextInt(Type.values().length - 2)], random);
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        //if there isn't a tile and we are near the top
                        if (row < Level.LEVEL_ROWS_PER_SCREEN / 4)
                        {
                            boolean valid = true;
                            
                            //check all rows below until the floor
                            for (int y = row; y < tiles.getFloorRow(); y++)
                            {
                                //if we have a tile and it isn't a background
                                if (tiles.hasTile(col, y) && !LevelCreatorHelper.isBackgroundTile(tiles.getTile(col, y)) || 
                                    tiles.hasTile(col + 1, y) && !LevelCreatorHelper.isBackgroundTile(tiles.getTile(col + 1, y)))
                                {
                                    //this isn't a valid location
                                    valid = false;
                                    break;
                                }
                            }
                            
                            for (int x = col - 2; x < col + 3; x++)
                            {
                                //also make sure there is a floor at the bottom
                                if (!tiles.hasTile(x, tiles.getFloorRow()))
                                {
                                    valid = false;
                                    break;
                                }
                            }
                            
                            //this is a valid location
                            if (valid)
                            {
                                //check that this location isn't so close to other enemies
                                if (hasAvailableSpace(tileCenterX, THWOMP_DISTANCE_REQUIREMENT))
                                {
                                    //we have verified everything, finally decide at random if we are to add enemy
                                    if (random.nextInt(ADD_ENEMY_THWOMP_PROBABILITY) == 0)
                                        add(tileCenterX, tileY, Type.Thwomp, random);
                                }
                            }
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void moveEnemies(final double x, final double y)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            //get current enemy
            Enemy enemy = enemies.get(i);
            
            //move back
            enemy.setLocation(enemy.getX() + x, enemy.getY() + y);
        }
    }
    
    public void removeProjectiles()
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            //get current enemy
            Enemy enemy = enemies.get(i);
            
            //remove all from enemy
            enemy.getProjectiles().clear();
        }
    }
    
    /**
     * Make sure this location has enough pixels between the closest enemy
     * @param x The x-coordinate we want to check
     * @param distance The minimum pixel distance between enemies
     * @return true if the coordinate is at least 3 tiles width away from all enemies, false otherwise
     */
    private boolean hasAvailableSpace(final double x, final double distance)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            //get current enemy
            Enemy enemy = enemies.get(i);
            
            if (enemy.getX() > x)
            {
                //if enemy is too close this is not a valid space
                if (enemy.getX() - x < distance)
                    return false;
            }
            else
            {
                //if enemy is too close this is not a valid space
                if (x - enemy.getX() < distance)
                    return false;
            }
        }
        
        //we have enough space
        return true;
    }
    
    /**
     * Add enemy of specified type at specified location
     * @param x x-coordinate
     * @param y y-coordinate
     * @param type The type of enemy we want to add
     */
    public void add(final double x, final double y, final Type type, final Random random)
    {
        final Enemy enemy;
        
        try
        {
            switch (type)
            {
                case TurtleGreen:
                    enemy = new TurtleGreen(random);
                    break;
                    
                case TurtleRed:
                    enemy = new TurtleRed(random);
                    break;
                    
                case TurtleGreenWings:
                    enemy = new TurtleGreenWings(random);
                    break;
                    
                case TurtleRedWings:
                    enemy = new TurtleRedWings(random);
                    break;
                    
                case TurtleGreenBig:
                    enemy = new TurtleGreenBig(random);
                    break;
                    
                case TurtleGreenWingsBig:
                    enemy = new TurtleGreenWingsBig(random);
                    break;
                    
                case TurtleRedBig:
                    enemy = new TurtleRedBig(random);
                    break;
                    
                case TurtleRedWingsBig:
                    enemy = new TurtleRedWingsBig(random);
                    break;
                    
                case Spiny:
                    enemy = new Spiny(random);
                    break;
                    
                case Boo:
                    enemy = new Boo(random);
                    break;
                    
                case GoombaBig:
                    enemy = new GoombaBig(random);
                    break;
                    
                case BuzzyBeetle:
                    enemy = new BuzzyBeetle(random);
                    break;
                    
                case TurtleSkeleton:
                    enemy = new TurtleSkeleton(random);
                    break;
                    
                case Goomba:
                    enemy = new Goomba(random);
                    break;
                    
                case GoombaWings:
                    enemy = new GoombaWings(random);
                    break;
                    
                case GoombaRed:
                    enemy = new GoombaRed(random);
                    break;
                    
                case GoombaRedWings:
                    enemy = new GoombaRedWings(random);
                    break;
                    
                case Thwomp:
                    enemy = new Thwomp(random);
                    break;
                    
                case BulletBill:
                    enemy = new BulletBill(random);
                    break;
                    
                case Spike:
                    enemy = new Spike(random);
                    break;
                    
                case Plant:
                    enemy = new Plant(random);
                    break;
                    
                case BoomerangBros:
                    enemy = new BoomerangBros(random);
                    break;
                    
                case HammerBros:
                    enemy = new HammerBros(random);
                    break;
                    
                case FireballBros:
                    enemy = new FireballBros(random);
                    break;
                    
                default:
                    throw new Exception("Enemy type not setup here: " + type.toString());
            }
            
            //store the sprite sheet
            enemy.setImage(image);
            
            //set the dimensions based on the current animation
            enemy.setDimensions();

            //set location
            enemy.setLocation(x, y - enemy.getHeight());
            
            //store the type of enemy
            enemy.setType(type);

            //add to list
            enemies.add(enemy);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void update(final Engine engine)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            //get the current enemy
            Enemy enemy = enemies.get(i);
            
            //if enemy has fallen off screen, or has been flagged as dead and not moving and no veritcal image flip
            if (enemy.getY() > boundary.y + boundary.height || enemy.isDead() && !enemy.hasVelocity() && !enemy.hasVerticalFlip())
            {
                //remove enemy from list
                enemies.remove(i);
                
                //move back to the previous index
                i--;
            }
            
            //even if we don't update the enemy we still need to adjust for scrolling
            enemy.setX(enemy.getX() + engine.getManager().getWorld().getLevels().getLevel().getScrollX());
            
            //if the enemy is at least a screen away don't update
            if (enemy.getX() + enemy.getWidth()  < boundary.x - (boundary.width * 1))
                continue;
            
            //if the enemy is at least a screen away don't update
            if (enemy.getX() > boundary.x + (boundary.width * 2))
                continue;
            
            //udpate enemy
            enemy.update(engine);
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            //get the current enemy
            Enemy enemy = enemies.get(i);
            
            //if not on screen, we won't render so skip to next
            if (enemy.getX() + enemy.getWidth()  < boundary.x || enemy.getX() > boundary.x + boundary.width)
                continue;
            if (enemy.getY() + enemy.getHeight() < boundary.y || enemy.getY() > boundary.y + boundary.height)
                continue;
            
            //draw the enemy
            enemy.render(graphics);
        }
    }
    
    public void renderProjectiles(final Graphics graphics)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            //get the current enemy
            Enemy enemy = enemies.get(i);
            
            //draw projectiles regardless if enemy isn't on screen
            enemy.renderProjectiles(graphics, boundary);
        }
    }
}