package com.gamesbykevin.mario.effects;

import com.gamesbykevin.framework.util.Timers;
import com.gamesbykevin.mario.entity.Entity;

import com.gamesbykevin.mario.effects.Effects.Type;
import com.gamesbykevin.mario.level.tiles.Tile;

public final class Effect extends Entity
{
    //the type of effect
    private final Type type;
    
    public Effect(final Type type)
    {
        this.type = type;
        
        try
        {
            switch(type)
            {
                case CollectCoin:
                    
                    //add animation
                    super.addAnimation(type, 3, 0, (Tile.HEIGHT * 0), Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(175L), false);
                    break;

                case Misc:
                    
                    //add animation
                    super.addAnimation(type, 4, 0, (Tile.HEIGHT * 1), Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(75L), false);
                    break;

                case BreakBrick:
                    
                    //add animation
                    super.addAnimation(type, 4, 0, (Tile.HEIGHT * 2), Tile.WIDTH, Tile.HEIGHT, Timers.toNanoSeconds(75L), false);
                    break;

                case FireballDestroy:
                    
                    //add animation
                    super.addAnimation(type, 4, 0, (Tile.HEIGHT * 3), 7, 8, Timers.toNanoSeconds(75L), false);
                    break;
                    
                default:
                    throw new Exception("Effect not setup here: " + type.toString());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        //set the width/height of object
        super.setDimensions();
    }
    
    public Type getType()
    {
        return this.type;
    }
}