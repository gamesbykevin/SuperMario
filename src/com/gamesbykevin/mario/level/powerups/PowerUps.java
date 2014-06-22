package com.gamesbykevin.mario.level.powerups;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.effects.Effects;

import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.heroes.AnimationHelper;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.level.*;
import com.gamesbykevin.mario.level.tiles.*;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
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
    
    public void manageHeroCollision(final Level level, final Hero hero)
    {
        try
        {
            //a type will be returned and removed from the list if the hero has collision
            PowerUps.Type type = level.getPowerUpCollision(hero);
            
            //if no collision return
            if (type == null)
                return;

            switch (type)
            {
                case Mushroom:
                    hero.setBig(true);
                    hero.setAnimation(AnimationHelper.getDefaultAnimation(hero), false);
                    hero.setDimensions();
                    
                    Tile south = hero.checkCollisionSouth(level.getTiles());
            
                    //correct mario y location
                    if (south != null)
                        hero.setY(south.getY() - hero.getHeight());
                    break;
                    
                case Flower:
                    hero.setBig(true);
                    hero.setFire(true);
                    hero.setAnimation(AnimationHelper.getDefaultAnimation(hero), false);
                    hero.setDimensions();
                    
                    south = hero.checkCollisionSouth(level.getTiles());
            
                    //correct mario y location
                    if (south != null)
                        hero.setY(south.getY() - hero.getHeight());
                    break;
                    
                case Star:
                    hero.setAnimation(AnimationHelper.getDefaultAnimation(hero), false);
                    
                    //flag invincible
                    hero.setInvincible(true);
                    
                    //reset invincible timers
                    hero.getTimers().reset();
                    break;
                    
                case Coin:
                    //add coin
                    hero.addCoin();
                    break;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void managePowerupBlock(final Random random, final Tile north, final Level level, final Hero mario)
    {
        level.getTiles().add(Tiles.Type.UsedBlock, north.getCol(), north.getRow(), north.getX(), north.getY());
        
        //choose at random if we are to add a power up, that is not a coin
        if (north.isPowerup())
        {
            //the block can do two different things
            if (random.nextBoolean())
            {
                //determine mario power up
                if (!mario.isBig())
                {
                    //if not big it will be a mushroom
                    level.getPowerUps().add(PowerUps.Type.Mushroom, north.getX(), north.getY(), north.getY() - Tile.HEIGHT);
                }
                else
                {
                    //if not big it will be a fire flower
                    level.getPowerUps().add(PowerUps.Type.Flower, north.getX(), north.getY(), north.getY() - Tile.HEIGHT);
                }
            }
            else
            {
                //choose star
                level.getPowerUps().add(PowerUps.Type.Star, north.getX(), north.getY(), north.getY() - Tile.HEIGHT);
            }
        }
        else
        {
            //need to add animation effect of collecting a coin here
            level.getEffects().add(north.getX(), north.getY() - north.getHeight(), Effects.Type.CollectCoin);
            
            //add to our count
            mario.addCoin();
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
                //now that destination has been reached, apply gravity to the appropriate power ups
                switch (powerUp.getType())
                {
                    case Star:
                    case Mushroom:
                    case Flower:
                        powerUp.applyGravity(tiles);
                        break;
                }
                
                //star is always jumping/falling
                if (powerUp.getType() == Type.Star)
                {
                    //we always want the star to keep jumping
                    if (!powerUp.isJumping())
                        powerUp.startJump();
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
            
            //the coin is the only powerup that can't move
            switch (powerUp.getType())
            {
                case Star:
                case Mushroom:
                case Flower:
                    //update location based velocity
                    powerUp.update();
                    break;
            }
            
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