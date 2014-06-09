package com.gamesbykevin.mario.level.powerups;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.level.tiles.Tile;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

public final class PowerUps implements Disposable
{
    /**
     * Different types of power-ups
     */
    public enum Type
    {
        Mushroom, Flower, Star, Coin, CoinSwitch
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
    
    public void add(final Type type, final int x, final int y)
    {
        try
        {
            PowerUp powerUp;
            powerUp = new PowerUp(type);
            powerUp.setLocation(x, y);
            
            //setup animation
            switch (type)
            {
                case Mushroom:
                    powerUp.addAnimation(type, 1, 0 * Tile.WIDTH, 0 * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), false);
                    break;

                case Flower:
                    powerUp.addAnimation(type, 1, 2 * Tile.WIDTH, 0 * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), false);
                    break;

                case Star:
                    powerUp.addAnimation(type, 1, 1 * Tile.WIDTH, 0 * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), false);
                    break;

                case Coin:
                    powerUp.addAnimation(type, 4, 0 * Tile.WIDTH, 1 * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), true);
                    break;

                case CoinSwitch:
                    powerUp.addAnimation(type, 3, 0 * Tile.WIDTH, 2 * Tile.HEIGHT, Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(125L), false);
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
    
    public void update(final long time, final double scrollX)
    {
        for (int i = 0; i < powerUps.size(); i++)
        {
            //set the velocity according to the scrolling
            powerUps.get(i).setVelocityX(scrollX);
            
            //update location
            powerUps.get(i).update();
            
            //update animation
            powerUps.get(i).update(time);
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