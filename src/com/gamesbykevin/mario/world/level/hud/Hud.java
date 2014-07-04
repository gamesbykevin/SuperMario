package com.gamesbykevin.mario.world.level.hud;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.shared.IElement;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;

public final class Hud extends Entity implements Disposable, IElement
{
    public enum Key
    {
        Clock, Coin, Heart
    }
    
    private boolean lightText = false;
    
    //where we will be drawing our information
    private final Point livesLocation, timerLocation, coinLocation;
    
    //the heroes number of lives and coins
    private int lives, coin;
    
    //time remaining
    private long remaining;
    
    //the font for the hud
    private Font font;
    
    public Hud(final Image image, final Rectangle window, final Font font)
    {
        //create font for hud
        this.font = font.deriveFont(12f);        
        
        super.setImage(image);
        
        //clock image
        super.addAnimation(Key.Clock, 1, 0, 0, 66, 66, 0, false);
        
        //coin image
        super.addAnimation(Key.Coin, 4, 66, 0, 16, 16, Timers.toNanoSeconds(275L), true);
        
        //heart image
        super.addAnimation(Key.Heart, 1, 130, 0, 56, 56, 0, false);
        
        //set default size
        super.setDimensions(16, 16);
        
        final int y = (int)(window.y + (window.height * .03));
        
        livesLocation   = new Point((int)(window.x + (window.width * .03)), y);
        timerLocation   = new Point((int)(window.x + (window.width * .24)), y);
        coinLocation    = new Point((int)(window.x + (window.width * .50)), y);
    }
    
    @Override
    public void update(final Engine engine)
    {
        //coin is the only visual motion animation, so set it as current so it can be updated
        super.setAnimation(Key.Coin, false);
        
        //update animation
        super.update(engine.getMain().getTime());
        
        //store the lives
        lives = engine.getManager().getMario().getLives();
        
        //store the coins
        coin = engine.getManager().getMario().getCoin();
        
        //determine the time left
        remaining = engine.getManager().getWorld().getLevels().getLevel().getTimer().getRemaining();
        
        //determine the color of text that is to be displayed
        switch (engine.getManager().getWorld().getLevels().getLevel().getBackground().getType())
        {
            case Background08:
            case Background10:
            case Background11:
            case Background12:
            case Background13:
            case Background14:
            case Background21:
            case Background25:
            case Background26:
            case Background27:
            case Background28:
            case Background30:
            case Background31:
            case Background32:
            case Background33:
                lightText = true;
                break;
            
            default:
                lightText = false;
                break;
        }
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if (font != null)
            font = null;
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        if (!lightText)
        {
            graphics.setColor(Color.BLACK);
        }
        else
        {
            graphics.setColor(Color.WHITE);
        }
        
        //set the appropriate font
        graphics.setFont(font);
        
        super.setAnimation(Key.Heart, false);
        super.setLocation(livesLocation);
        super.draw(graphics);
        graphics.drawString("" + lives, livesLocation.x + (int)getWidth() + 3, livesLocation.y + (int)getHeight());
        
        super.setAnimation(Key.Clock, false);
        super.setLocation(timerLocation);
        super.draw(graphics);
        graphics.drawString("" + (int)(remaining / Timers.NANO_SECONDS_PER_SECOND), timerLocation.x + (int)getWidth() + 3, timerLocation.y + (int)getHeight());
        
        super.setAnimation(Key.Coin, false);
        super.setLocation(coinLocation);
        super.draw(graphics);
        graphics.drawString("" + coin, coinLocation.x + (int)getWidth() + 1, coinLocation.y + (int)getHeight());
    }
}