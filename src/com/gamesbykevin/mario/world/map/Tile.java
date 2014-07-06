package com.gamesbykevin.mario.world.map;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.entity.Entity;

public final class Tile extends Entity implements Disposable
{
    private final Type type;
    
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
        Start(0,180,16,16,1),
        LevelComplete(16,180,16,16,1), 
        
        PathHorizontalYellow(0, 260, 24, 4, 1), 
        PathHorizontalGray(0, 264, 24, 4, 1), 
        PathHorizontalGreen(0, 268, 24, 4, 1), 
        PathHorizontalBlue(0, 272, 24, 4, 1), 
        PathHorizontalBrown(0, 276, 24, 4, 1), 
        
        PathVerticalYellow(68, 256, 4, 24, 1), 
        PathVerticalGray(72, 256, 4, 24, 1), 
        PathVerticalGreen(76, 256, 4, 24, 1), 
        PathVerticalBlue(80, 256, 4, 24, 1), 
        PathVerticalBrown(84, 256, 4, 24, 1), 
        
        TileGreen(0, 280, 16, 15, 1), 
        TileBlue(16, 280, 16, 15, 1), 
        TileDarkBrown(32, 280, 16, 15, 1), 
        TileYellowish(48, 280, 16, 15, 1), 
        TileOther(64, 280, 16, 15, 1), 
        
        MushroomHouse1(0, 312, 16, 16, 1), 
        MushroomHouse2(16, 312, 16, 16, 1), 
        
        IceBlock(0,164,16,16,4), 
        PalmTree(0,196,16,16,3), 
        Plant(0,212,16,16,3), 
        Hills(0,228,16,16,4), 
        Flower(0,244,16,16,4), 
        Fire(0, 296, 16, 16, 4), 
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
    
    protected static final int TYPES_COUNT = Type.values().length;
    
    protected Type getType()
    {
        return this.type;
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
                delay = Timers.toNanoSeconds(250L);
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
        
        //add animation
        super.addAnimation(type, type.c, type.x, type.y, type.w, type.h, delay, true);
        
        //set default width-height based on current animation
        super.setDimensions();
    }
    
    protected static boolean isGameTile(final Type type)
    {
        return (isCardMatchingTile(type) || isMushroomHouseTile(type));
    }
    
    protected static boolean isBackgroundMotionTile(final Type type)
    {
        if (type == null)
            return false;
        
        switch (type)
        {
            case IceBlock:
            case PalmTree:
            case Plant:
            case Hills:
            case Flower:
            case Fire:
            case Skull:
                return true;
                
            default:
                return false;
        }
    }
    
    protected static boolean isBackgroundTile(final Type type)
    {
        if (type == null)
            return false;
        
        switch (type)
        {
            case TileGreen:
            case TileBlue:
            case TileDarkBrown:
            case TileYellowish:
            case TileOther:
                return true;
                
            default:
                return false;
        }
    }
    
    protected static boolean isCardMatchingTile(final Type type)
    {
        if (type == null)
            return false;
        
        switch(type)
        {
            case CardMatching:
                return true;
                
            default:
                return false;
        }
    }
    
    protected static boolean isMushroomHouseTile(final Type type)
    {
        if (type == null)
            return false;
        
        switch(type)
        {
            case MushroomHouse1:
            case MushroomHouse2:
                return true;
                
            default:
                return false;
        }
    }
    
    protected static boolean isLevelTile(final Type type)
    {
        if (type == null)
            return false;
        
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
                return true;
                
            default:
                return false;
        }
    }
    
    protected static boolean isPathTile(final Type type)
    {
        if (type == null)
            return false;
        
        switch (type)
        {
            case PathHorizontalYellow:
            case PathHorizontalGray:
            case PathHorizontalGreen:
            case PathHorizontalBlue:
            case PathHorizontalBrown:
                return true;
                
            default:
                return false;
        }
    }
    
    /**
     * Get the level index
     * @param type The tile type that is for the levels
     * @return The index
     */
    public static int getLevelIndex(final Type type)
    {
        if (type == null)
            return -1;
        
        switch (type)
        {
            case Level01:
                return 0;
                
            case Level02:
                return 1;
                
            case Level03:
                return 2;
                
            case Level04:
                return 3;
                
            case Level05:
                return 4;
                
            case Level06:
                return 5;
                
            case Level07:
                return 6;
                
            case Level08:
                return 7;
                
            case Level09:
                return 8;
                
            case Level10:
                return 9;
                
            default:
                return -1;
        }
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
}