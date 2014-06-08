package com.gamesbykevin.mario.level;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.entity.Entity;

import java.awt.Graphics;
import java.awt.Image;

public final class Background extends Entity implements Disposable
{
    private static final int WIDTH = 256;
    private static final int HEIGHT = 192;
    
    public enum Type
    {
        Background01(0, 0), Background02(1, 0), Background03(2, 0), Background04(3, 0), 
        Background05(0, 1), Background06(1, 1), Background07(2, 1), Background08(3, 1), 
        Background09(0, 2), Background10(1, 2), Background11(2, 2), Background12(3, 2), 
        Background13(0, 3); 
        
        //store where this is on sprite sheet
        private final int column, row;
        
        private Type(final int column, final int row)
        {
            this.column = column;
            this.row = row;
        }
    }
    
    public Background(final Image image, final Type type)
    {
        //store the background image
        super.setImage(image);
        
        //add default animation
        super.addAnimation(type, 1, type.column * WIDTH, type.row * HEIGHT, WIDTH, HEIGHT, 0, false);
        
        //set the dimensions
        super.setDimensions(WIDTH, HEIGHT);
    }
    
    public void update(final int repeatLeft, final int repeatRight)
    {
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
