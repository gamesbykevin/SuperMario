package com.gamesbykevin.mario.enemies;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.engine.Engine;
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
    
    public enum Type
    {
        TurtleGreen, TurtleRed, TurtleGreenWings, TurtleRedWings, TurtleGreenBig, 
        TurtleGreenWingsBig, TurtleRedBig, TurtleRedWingsBig, Spiny, Boo, 
        GoombaBig, BuzzyBeetle, TurtleSkeleton, Goomba, GoombaWings, 
        GoombaRed, GoombaRedWings, Thwomp, BulletBill, Spike, 
        Plant, BoomerangBros, HammerBros, FireballBros, 
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
            
            //udpate enemy
            enemy.update(engine);
            
            //if enemy has fallen off screen, or has been flagged as dead and not moving and no veritcal image flip
            if (enemy.getY() > boundary.y + boundary.height || enemy.isDead() && !enemy.hasVelocity() && !enemy.hasVerticalFlip())
            {
                //remove from list
                enemies.remove(i);
                
                //move index back by 1
                i--;
            }
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        for (int i = 0; i < enemies.size(); i++)
        {
            //get the current enemy
            Enemy enemy = enemies.get(i);
            
            //draw projectiles regardless if enemy isn't on screen
            enemy.renderProjectiles(graphics, boundary);
            
            //if not on screen, we won't render
            if (enemy.getX() + enemy.getWidth()  < boundary.x || enemy.getX() > boundary.x + boundary.width)
                return;
            if (enemy.getY() + enemy.getHeight() < boundary.y || enemy.getY() > boundary.y + boundary.height)
                return;
            
            //draw the enemy
            enemy.render(graphics);
        }
    }
}