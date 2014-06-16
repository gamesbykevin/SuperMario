package com.gamesbykevin.mario.effects;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.entity.Entity;

import java.awt.Graphics;
import java.awt.Image;

import java.util.ArrayList;
import java.util.List;

public final class Effects implements Disposable
{
    public enum Type
    {
        CollectCoin, BreakBrick, Misc, FireballDestroy
    }
    
    //contain our sprites
    private Image image;
    
    //list of powerups
    private List<Effect> effects;
    
    public Effects(final Image image)
    {
        //sprite sheet
        this.image = image;
        
        //create new list
        this.effects = new ArrayList<>();
    }
    
    @Override
    public void dispose()
    {
        if (effects != null)
        {
            for (int i = 0; i < effects.size(); i++)
            {
                effects.get(i).dispose();
                effects.set(i, null);
            }
            
            effects.clear();
            effects = null;
        }
    }
    
    public void add(final Entity entity, final Type type)
    {
        add(entity.getX(), entity.getY(), type);
    }
    
    public void add(final double x, final double y, final Type type)
    {
        //create new effect
        Effect effect = new Effect(type);
        effect.setLocation(x, y);
        
        //add to list
        effects.add(effect);
    }
    
    public void update(final long time, final double scrollX)
    {
        for (int i = 0; i < effects.size(); i++)
        {
            Effect effect = effects.get(i);
            
            if (effect.getSpriteSheet().hasFinished())
            {
                //if the effect has finished, remove it
                effects.remove(i);
            }
            else
            {
                //update animation
                effect.update(time);
                
                //set velocity
                effect.setVelocityX(scrollX);
                
                //move if velocity set
                effect.update();
            }
        }
    }
    
    public void render(final Graphics graphics)
    {
        //draw the effects
        for (int i = 0; i < effects.size(); i++)
        {
            Effect effect = effects.get(i);
            effect.draw(graphics, image);
        }
    }
}