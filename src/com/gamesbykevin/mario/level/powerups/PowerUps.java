package com.gamesbykevin.mario.level.powerups;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.level.tiles.*;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class PowerUps implements Disposable
{
    /**
     * Different types of power-ups
     */
    public enum Type
    {
        Mushroom, Flower, Star, Coin
    }
    
    //contain our power up sprites
    private Image image;
    
    //list of powerups
    private List<PowerUp> powerUps;
    
    public PowerUps(final Image image)
    {
        //sprite sheet
        this.image = image;
        
        //create new list
        this.powerUps = new ArrayList<>();
    }
    
    /**
     * Get the type of power up found, and remove from list
     * @param entity Object we want to check for collision
     * @return The type of power up found, if none found null is returned
     */
    public Type getCollision(final Entity entity)
    {
        for (int i = 0; i < powerUps.size(); i++)
        {
            //get the current power up
            PowerUp powerUp = powerUps.get(i);
            
            //check for collision
            if (entity.getRectangle().intersects(powerUp.getRectangle()))
            {
                //get the type
                Type type = powerUp.getType();
                
                //remove from list
                powerUps.remove(i);
                
                //return our result
                return type;
            }
        }
        
        return null;
    }
    
    public void add(final Type type, final double x, final double y, final double destinationY)
    {
        try
        {
            PowerUp powerUp;
            powerUp = new PowerUp(type, destinationY);
            powerUp.setLocation(x, y);
            
            //if we are already at our destination, flag it
            if (y == destinationY)
                powerUp.flagDestination();
            
            //setup animation
            switch (type)
            {
                case Mushroom:
                    powerUp.addAnimation(type, 1, 0 * Tile.WIDTH, 0 * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), false);
                    powerUp.setJumpVelocity(PowerUp.DEFAULT_JUMP_VELOCITY);
                    break;

                case Flower:
                    powerUp.addAnimation(type, 1, 2 * Tile.WIDTH, 0 * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), false);
                    powerUp.setJumpVelocity(PowerUp.DEFAULT_JUMP_VELOCITY);
                    break;

                case Star:
                    powerUp.addAnimation(type, 1, 1 * Tile.WIDTH, 0 * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), false);
                    powerUp.setJumpVelocity(PowerUp.DEFAULT_JUMP_VELOCITY);
                    break;

                case Coin:
                    powerUp.addAnimation(type, 4, 0 * Tile.WIDTH, 1 * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), true);
                    break;

                default:
                    throw new Exception("Type is not setup here: " + type.toString());
            }
            
            powerUps.add(powerUp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void dispose()
    {
        if (powerUps != null)
        {
            for (int i = 0; i < powerUps.size(); i++)
            {
                powerUps.get(i).dispose();
                powerUps.set(i, null);
            }
            
            powerUps.clear();
            powerUps = null;
        }
    }
    
    public void update(final long time, final double scrollX, final Random random, final Tiles tiles)
    {
        for (int i = 0; i < powerUps.size(); i++)
        {
            PowerUp powerUp = powerUps.get(i);
            
            //if not at destination yet
            if (!powerUp.hasDestination())
            {
                if (powerUp.getY() == powerUp.getDestinationY())
                {
                    //mark as found destination
                    powerUp.flagDestination();
                    
                    //stop y-velocity
                    powerUp.resetVelocityY();
                    
                    //these are the power ups that move after reaching initial destination
                    switch (powerUp.getType())
                    {
                        case Mushroom:
                            //choose random x-velocity direction
                            powerUp.setVelocityX((random.nextBoolean()) ? -PowerUp.DEFAULT_SPEED_VELOCITY_X : PowerUp.DEFAULT_SPEED_VELOCITY_X);
                            break;
                            
                        case Star:
                            //choose random x-velocity direction
                            powerUp.setVelocityX((random.nextBoolean()) ? -PowerUp.DEFAULT_SPEED_VELOCITY_X : PowerUp.DEFAULT_SPEED_VELOCITY_X);
                            
                            //the star will jump
                            powerUp.startJump();
                            break;
                    }
                }
            }
            else
            {
                //apply gravity once destination has been reached
                powerUp.applyGravity(tiles);
                
                switch (powerUp.getType())
                {
                    case Star:

                        //we always want the star to keep jumping
                        if (!powerUp.isJumping())
                            powerUp.startJump();
                        break;
                }
            
                if (powerUp.hasVelocityX())
                {
                    Tile west = powerUp.checkCollisionWest(tiles);
                    Tile east = powerUp.checkCollisionEast(tiles);

                    if (west != null)
                    {
                        if (powerUp.getVelocityX() < 0)
                            powerUp.setVelocityX(-powerUp.getVelocityX());
                    }

                    if (east != null)
                    {
                        if (powerUp.getVelocityX() > 0)
                            powerUp.setVelocityX(-powerUp.getVelocityX());
                    }
                }
                
                if (powerUp.hasVelocityY())
                {
                    //makre sure we are jumping before checking north tile
                    if (powerUp.getVelocityY() < 0)
                    {
                        //make sure we are also jumping
                        if (powerUp.isJumping())
                        {
                            Tile north = powerUp.checkCollisionNorth(tiles);
                            
                            //if we hit a north tile
                            if (north != null)
                                powerUp.setY(north.getY() + north.getHeight());
                        }
                    }
                    
                    //make sure we are falling before checking south tile
                    if (powerUp.getVelocityY() > 0)
                    {
                        Tile south = powerUp.checkCollisionSouth(tiles);

                        //if we hit a south tile and are jumping, we should stop
                        if (south != null && powerUp.isJumping())
                            powerUp.stopJump();
                    }
                }
            }
            
            //update location based on scrolling
            powerUp.setX(powerUp.getX() + scrollX);
            
            //update location based velocity
            powerUp.update();
            
            //update animation
            powerUp.update(time);
        }
    }
    
    public void render(final Graphics graphics)
    {
        //draw all of the power ups
        for (int i = 0; i < powerUps.size(); i++)
        {
            powerUps.get(i).draw(graphics, image);
        }
    }
}