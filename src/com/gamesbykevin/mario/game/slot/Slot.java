package com.gamesbykevin.mario.game.slot;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timer;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.game.Game;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.input.Input;
import com.gamesbykevin.mario.shared.Displayable;
import com.gamesbykevin.mario.shared.IElement;
import com.gamesbykevin.mario.shared.Shared;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Slot extends Game implements Disposable, IElement, Displayable
{
    private enum Type
    {
        Top(0,0,288,37),
        Middle(0,37,288,52),
        Bottom(0,89,288,23),
        Reward1(263,152,25,16),
        Reward3(263,168,25,16),
        Reward5(263,184,25,16);
        
        //location and dimensions
        private int x, y, w, h;
        
        private Type(final int x, final int y, final int w, final int h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
    
    private enum Reward
    {
        Mushroom(1), Star(5), Flower(3);

        private int lives;

        private Reward(final int lives)
        {
            this.lives = lives;
        }

        private int getLives()
        {
            return this.lives;
        }
    }
        
    private List<Tile> tiles;
    
    //the speed at which the pieces move
    private static final int SPEED = 5;
    
    //we the pieces will be drawn
    private static final int START_Y = 50;
    
    //did we determine if there was a winner
    private boolean calculate = false;
    
    //the delay to wait after the game completes
    private static final long COMPLETE_DELAY = Timers.toNanoSeconds(2500L);
    
    //to keep track of time
    private Timer timer;
    
    //the different reward images
    private Tile reward1up, reward3up, reward5up;
    
    //the reward we have earned
    private Reward reward;
    
    //is this oject being displayed
    private boolean display = false;
    
    public Slot(final Image image)
    {
        super();
        
        //create our timer with specified delay
        timer = new Timer(COMPLETE_DELAY);
        
        //store the sprite sheet
        super.setImage(image);
        
        //setup background
        super.addAnimation("Default", 1, 0, 112, Shared.ORIGINAL_WIDTH, Shared.ORIGINAL_HEIGHT, 0, false);
        super.setDimensions();
        
        //temp object
        Tile tile;
        
        //create new list
        tiles = new ArrayList<>();
        
        //create the pieces in this order top, middle, bottom
        tile = new Tile(Type.Top);
        tile.setY(START_Y);
        tiles.add(tile);
        
        tile = new Tile(Type.Middle);
        tile.setY(tiles.get(0).getY() + tiles.get(0).getHeight());
        tiles.add(tile);
        
        tile = new Tile(Type.Bottom);
        tile.setY(tiles.get(1).getY() + tiles.get(1).getHeight());
        tiles.add(tile);
        
        //create reward display
        reward1up = new Tile(Type.Reward1);
        reward3up = new Tile(Type.Reward3);
        reward5up = new Tile(Type.Reward5);
    }
    
    @Override
    public void setDisplayed(final boolean display)
    {
        this.display = display;
        
        //if we are to display set reset
        if (display)
            setReset();
    }
    
    @Override
    public boolean isDisplayed()
    {
        return this.display;
    }
    
    @Override
    public void reset(final Random random)
    {
        //unflag reset
        super.unflagReset();
        
        //remove reward
        setReward(null);
        
        //hide the reward displays
        reward1up.setX((Shared.ORIGINAL_WIDTH / 2) - (reward1up.getWidth() / 2));
        reward1up.setY(Shared.ORIGINAL_HEIGHT);
        reward3up.setX((Shared.ORIGINAL_WIDTH / 2) - (reward3up.getWidth() / 2));
        reward3up.setY(Shared.ORIGINAL_HEIGHT);
        reward5up.setX((Shared.ORIGINAL_WIDTH / 2) - (reward3up.getWidth() / 2));
        reward5up.setY(Shared.ORIGINAL_HEIGHT);
        
        //determine random direction to move
        boolean flip = random.nextBoolean();
        
        //calculate center x
        final double middleX = (Shared.ORIGINAL_WIDTH / 2) - (tiles.get(0).getWidth() / 2);
        
        //place each piece and set velocity
        for (int i = 0; i < tiles.size(); i++)
        {
            Tile tile = tiles.get(i);
            tile.setX(middleX);
            tile.setVelocityX(flip ? SPEED : -SPEED);
            tile.reset();
            
            flip = !flip;
        }
        
        //need to calculate again
        calculate = false;
    }
    
    private final class Tile extends Entity
    {
        //do we prepare to stop
        private boolean stop = false;
        
        //where to stop (x-coordinate)
        private int destination = 0;
        
        //the reward of this tile
        private Reward reward;
        
        private Tile(final Type type)
        {
            try
            {
                switch (type)
                {
                    case Top:
                        addAnimation(type, 1, type.x, type.y, type.w, type.h, 0, false);
                        break;

                    case Middle:
                        addAnimation(type, 1, type.x, type.y, type.w, type.h, 0, false);
                        break;

                    case Bottom:
                        addAnimation(type, 1, type.x, type.y, type.w, type.h, 0, false);
                        break;
                        
                    case Reward1:
                        addAnimation(type, 1, type.x, type.y, type.w, type.h, 0, false);
                        break;
                        
                    case Reward3:
                        addAnimation(type, 1, type.x, type.y, type.w, type.h, 0, false);
                        break;
                        
                    case Reward5:
                        addAnimation(type, 1, type.x, type.y, type.w, type.h, 0, false);
                        break;
                        
                    default:
                        throw new Exception("Type not setup here. " + type.toString());
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            
            super.setDimensions();
        }
        
        private void reset()
        {
            this.destination = 0;
            stop = false;
        }
        
        private void setReward(final int index)
        {
            this.reward = Reward.values()[index];
        }
        
        private Reward getReward()
        {
            return this.reward;
        }
        
        /**
         * Check if the tile has reached the destination.<br>
         * If true tile will stop moving.
         */
        private void checkDestination()
        {
            //store original location
            final double x = getX();

            //update location
            update();

            if (getVelocityX() < 0)
            {
                //we are at or past, so stop
                if (getX() <= destination)
                {
                    setX(destination);
                    resetVelocity();
                }
                else
                {
                    //destination not yet reached so restore x
                    setX(x);
                }
            }
            else
            {
                //we are at or past, so stop
                if (getX() >= destination)
                {
                    setX(destination);
                    resetVelocity();
                }
                else
                {
                    //destination not yet reached so restore x
                    setX(x);
                }
            }
        }
        
        private boolean hasDestination()
        {
            return (getX() == destination);
        }
        
        private void markStop(final double destination)
        {
            this.destination = (int)destination;
            this.stop = true;
        }
        
        private boolean hasStop()
        {
            return this.stop;
        }
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if (tiles != null)
        {
            tiles.clear();
            tiles = null;
        }
    }
    
    private void setReward(final Reward reward)
    {
        this.reward = reward;
    }
    
    private Reward getReward()
    {
        return this.reward;
    }
    
    @Override
    protected void calculateWin(final Hero hero)
    {
        //set the reward of the first tile 
        setReward(tiles.get(0).getReward());
        
        for (int i = 0; i < tiles.size(); i++)
        {
            //if a tile doesn't match the first one there is no reward
            if (tiles.get(i).getReward() != getReward())
            {
                //no reward
                setReward(null);
                
                //exit loop
                break;
            }
        }
        
        //if there is a reward add it to hero
        if (getReward() != null)
        {
            try
            {
                //add the appropriate reward
                switch (getReward())
                {
                    case Mushroom:
                        hero.setLives(hero.getLives() + getReward().getLives());
                        break;
                        
                    case Flower:
                        hero.setLives(hero.getLives() + getReward().getLives());
                        break;
                        
                    case Star:
                        hero.setLives(hero.getLives() + getReward().getLives());
                        break;
                        
                    default:
                        throw new Exception("Reward not setup here.");
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void update(final Engine engine)
    {
        if (isComplete())
        {
            //if the game is complete determine if winner
            if (!calculate)
            {
                //check if we won anything
                calculateWin(engine.getManager().getMario());

                //we are done calculating
                calculate = true;

                //reset the timer
                timer.reset();
            }
            else
            {
                //now wait for timer to finish before game is complete
                if (!timer.hasTimePassed())
                {
                    //update timer
                    timer.update(engine.getMain().getTime());

                    //where we want to end up
                    final double destination = 0;

                    //distance between start and finish
                    final double length = Shared.ORIGINAL_HEIGHT - destination;

                    //locate current y-coordinate based on timer progress
                    final double y = Shared.ORIGINAL_HEIGHT - (length * timer.getProgress());

                    //if there is a reward move it to display
                    if (getReward() != null)
                    {
                        switch (getReward())
                        {
                            case Mushroom:
                                reward1up.setY(y);
                                break;

                            case Flower:
                                reward3up.setY(y);
                                break;

                            case Star:
                                reward5up.setY(y);
                                break;
                        }
                    }
                }
                else
                {
                    //reset game
                    reset(engine.getRandom());
                    
                    //mark current tile as complete
                    engine.getManager().getWorld().getMap().markLevelComplete();
                    
                    //switch back to mini map
                    engine.getManager().getMario().switchBack(engine.getManager().getWorld());
                    
                    //reset input
                    engine.getKeyboard().reset();
                }
            }
        }
        else
        {
            //move all pieces (assuming velocity is set)
            movePieces();

            //check if any pieces have reached their destination
            checkDestinations();

            //check boundaries
            checkBoundaries();

            //check when any key is released
            if (engine.getKeyboard().isKeyReleased())
            {
                //stop the current piece from moving
                stopPiece();

                //check released keys
                Input.manageKeyboard(engine.getKeyboard());
            }
        }
    }
    
    private void stopPiece()
    {
        for (int i = 0; i < tiles.size(); i++)
        {
            if (!tiles.get(i).hasStop())
            {
                //set the destination
                setTileStop(tiles.get(i));
                
                //only stop 1 for now
                break;
            }
        }
    }
    
    private void movePieces()
    {
        for (int i = 0; i < tiles.size(); i++)
        {
            tiles.get(i).update();
        }
    }
    
    private void checkDestinations()
    {
        for (int i = 0; i < tiles.size(); i++)
        {
            checkDestination(tiles.get(i));
        }
    }
    
    private void checkDestination(final Tile tile)
    {
        //if anyone is flagged to stop, check if reached destinaton
        if (tile.hasStop() && !tile.hasDestination())
        {
            tile.checkDestination();
        }
    }
    
    private void checkBoundaries()
    {
        for (int i = 0; i < tiles.size(); i++)
        {
            checkBoundary(tiles.get(i));
        }
    }
    
    /**
     * Repeat piece once offscreen
     * @param tile The tile we want to check
     */
    private void checkBoundary(final Tile tile)
    {
        if (tile.getVelocityX() < 0)
        {
            if (tile.getX() < -tile.getWidth())
                tile.setX(0);
        }
        else if (tile.getVelocityX() > 0)
        {
            if (tile.getX() > tile.getWidth())
                tile.setX(0);
        }
    }
    
    @Override
    public boolean isComplete()
    {
        for (int i = 0; i < tiles.size(); i++)
        {
            if (tiles.get(i).hasVelocity())
                return false;
            
            if (!tiles.get(i).hasStop())
                return false;
            
            if (!tiles.get(i).hasDestination())
                return false;
        }
        
        //at this point the game is complete
        return true;
    }
    
    /**
     * Determine where the tile is to stop
     * @param tile The tile we want to flag to stop
     */
    private void setTileStop(final Tile tile)
    {
        //middle location
        final double middleX = (Shared.ORIGINAL_WIDTH / 2);
        
        //determine where to start
        final double startX = middleX - tile.getWidth();
        
        //determine where to end
        final double endX = middleX + tile.getWidth();
        
        final int interval = (int)(tile.getWidth() / 3);
        
        //tmp index
        int index = 0;
        
        //now check where we are to determine the reward
        for (double x = startX; x <= endX; x += interval)
        {
            //if between these 2 coordinates
            if (tile.getX() <= x && tile.getX() >= x - interval)
            {
                //we know where to stop and the reward
                tile.markStop(x - (interval / 2));
                tile.setReward(index);
            }

            //move to next
            index++;

            //reset index if at end
            if (index >= Reward.values().length)
                index = 0;
        }
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //draw background
        super.draw(graphics);
        
        for (int i = 0; i < tiles.size(); i++)
        {
            //draw tiles
            drawTile(graphics, tiles.get(i));
        }
        
        //if the game is complete and the reward is determined
        if (isComplete() && calculate)
        {
            if (getReward() != null)
            {
                try
                {
                    switch (getReward())
                    {
                        case Mushroom:
                            reward1up.draw(graphics, getImage());
                            break;

                        case Flower:
                            reward3up.draw(graphics, getImage());
                            break;

                        case Star:
                            reward5up.draw(graphics, getImage());
                            break;

                        default:
                            throw new Exception("Reward not setup here. " + getReward().toString());
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void drawTile(final Graphics graphics, final Tile tile)
    {
        //store original location
        final double x = tile.getX();
        
        //draw west
        tile.setX(x - tile.getWidth());
        tile.draw(graphics, getImage());
        
        //draw center
        tile.setX(x);
        tile.draw(graphics, getImage());
        
        //draw east
        tile.setX(x + tile.getWidth());
        tile.draw(graphics, getImage());
        
        //restore location
        tile.setX(x);
    }
}