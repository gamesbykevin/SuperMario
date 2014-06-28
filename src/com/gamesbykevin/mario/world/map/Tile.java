package com.gamesbykevin.mario.world.map;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.entity.Entity;

public final class Tile extends Entity implements Disposable
{
    private Type type;
    
    public enum Type
    {
        Level01(0,0,  14,14,3),
        Level02(0,14, 14,14,3),
        Level03(0,28, 14,14,3),
        Level04(0,42, 14,14,3),
        Level05(0,56, 14,14,3),
        Level06(0,70, 14,14,3),
        Level07(0,84, 14,14,3),
        Level08(0,98, 14,14,3),
        Level09(0,112,14,14,3),
        Level10(0,126,14,14,3),
        
        CardMatching(0,140,16,16,4),
        Dot(0,156,8,8,3),
        IceBlock(0,164,16,16,4),
        Start(0,180,16,16,1),
        LevelComplete(16,180,16,16,1), 
        
        PalmTree(0,196,16,16,3),
        Plant(0,212,16,16,3),
        Hills(0,228,16,16,4),
        Flower(0,244,16,16,4),
        
        PathYellow(0, 260, 24, 4, 1), 
        PathGray(0, 264, 24, 4, 1), 
        PathGreen(0, 268, 24, 4, 1), 
        PathBlue(0, 272, 24, 4, 1), 
        PathBrown(0, 276, 24, 4, 1), 
        
        TileGreen(0, 280, 16, 15, 1), 
        TileBlue(16, 280, 16, 15, 1), 
        TileDarkBrown(32, 280, 16, 15, 1), 
        TileYellowish(48, 280, 16, 15, 1), 
        TileOther(64, 280, 16, 15, 1), 
        
        Fire(0, 296, 16, 16, 4), 
        
        MushroomHouse1(0, 312, 16, 16, 1), 
        MushroomHouse2(16, 312, 16, 16, 1), 
        
        Skull(0, 328, 16, 16, 3);
        
        private int x, y, w, h, c;
        
        private Type(final int x, final int y, final int w, final int h, final int c)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.c = c;
        }
    }
    
    protected Tile(final Type type)
    {
        this.type = type;
        
        final long delay;
        
        switch (type)
        {
            case Level01:
            case Level02:
            case Level03:
            case Level04:
            case Level05:
            case Level06:
            case Level07:
            case Level08:
            case Level09:
            case Level10:
                delay = Timers.toNanoSeconds(225L);
                break;
            
            case CardMatching:
                delay = Timers.toNanoSeconds(175L);
                break;
                
            case Dot:
                delay = Timers.toNanoSeconds(333L);
                break;
                
            case IceBlock:
                delay = Timers.toNanoSeconds(250L);
                break;

            case PalmTree:
                delay = Timers.toNanoSeconds(450L);
                break;
                
            case Plant:
                delay = Timers.toNanoSeconds(650L);
                break;
                
            case Hills:
                delay = Timers.toNanoSeconds(750L);
                break;
                
            case Flower:
                delay = Timers.toNanoSeconds(250L);
                break;
        
            case Fire:
                delay = Timers.toNanoSeconds(200L);
                break;
                
            case Skull:
                delay = Timers.toNanoSeconds(350L);
                break;
                
            default:
                delay = 0;
                break;
        }
        
        super.addAnimation(type, type.c, type.x, type.y, type.w, type.h, delay, true);
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}