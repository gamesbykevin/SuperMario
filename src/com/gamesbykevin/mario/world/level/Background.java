package com.gamesbykevin.mario.world.level;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.entity.Entity;

import java.awt.Graphics;
import java.awt.Image;

public final class Background extends Entity implements Disposable
{
    private static final int WIDTH = 256;
    private static final int HEIGHT = 192;
    
    private final Type type;
    
    public enum Type
    {
        Background01(0, 0), Background02(1, 0), Background03(2, 0), Background04(3, 0), 
        Background05(0, 1), Background06(1, 1), Background07(2, 1), Background08(3, 1), 
        Background09(0, 2), Background10(1, 2), Background11(2, 2), Background12(3, 2), 
        Background13(0, 3), Background14(1, 3), Background15(2, 3), Background16(3, 3), 
        
        Background17(0, 4), Background18(1, 4), Background19(2, 4), Background20(3, 4), 
        Background21(0, 5), Background22(1, 5), Background23(2, 5), Background24(3, 5), 
        Background25(0, 6), Background26(1, 6), Background27(2, 6), Background28(3, 6), 
        Background29(0, 7),  
        
        Background30(0, 8), 
        Background31(0, 9), 
        Background32(0, 10), 
        Background33(0, 11); 
        
        //store where this is on sprite sheet
        private final int column, row;
        
        private Type(final int column, final int row)
        {
            this.column = column;
            this.row = row;
        }
    }
    
    public Background(final Image image, Type type)
    {
        //store the background image
        super.setImage(image);
        
        this.type = type;
        
        switch (type)
        {
            
            //this is a special background
            case Background30:
            case Background31:
            case Background32:
            case Background33:
                //add default animation
                super.addAnimation(type, 3, type.column * WIDTH, type.row * HEIGHT, WIDTH, HEIGHT, Timers.toNanoSeconds(250L), true);
                
                //set the dimensions
                super.setDimensions(WIDTH, HEIGHT);
                break;
                
            default:
                //add default animation
                super.addAnimation(type, 1, type.column * WIDTH, type.row * HEIGHT, WIDTH, HEIGHT, 0, false);
                
                //set the dimensions
                super.setDimensions(WIDTH, HEIGHT);
                break;
        }
    }
    
    public Type getType()
    {
        return this.type;
    }
    
    public void update(final int repeatLeft, final int repeatRight, final long time)
    {
        //update animation
        super.update(time);
        
        //update location
        super.update();
        
        //do we need to repeat the background
        if (getX() + getWidth() < repeatLeft)
            setX(repeatRight);
        
        if (getX() > repeatRight)
            setX(repeatLeft);
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    public void render(final Graphics graphics)
    {
        //get original location
        final double x = getX();
        final double y = getY();
        
        //draw the background 3 times
        super.draw(graphics);
        super.setX(x - super.getWidth());
        super.draw(graphics);
        super.setX(x + super.getWidth());
        super.draw(graphics);
        
        //restore location
        super.setX(x);
        super.setY(y);
    }
}
