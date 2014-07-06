package com.gamesbykevin.mario.game.matching;

import com.gamesbykevin.framework.base.Cell;
import com.gamesbykevin.framework.input.Keyboard;
import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.*;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.game.Game;
import com.gamesbykevin.mario.input.Input;
import com.gamesbykevin.mario.resources.GameAudio;
import com.gamesbykevin.mario.shared.Displayable;
import com.gamesbykevin.mario.shared.IElement;
import com.gamesbykevin.mario.shared.Shared;

import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public final class Matching extends Game implements Disposable, IElement, Displayable
{
    //how many columns/rows are in the game
    private static final int COLUMNS = 6;
    private static final int ROWS = 3;
    
    //start location of places
    private static final int START_X = 10;
    private static final int START_Y = 20;
    
    private static final int WIDTH = 40;
    private static final int HEIGHT = 50;
    
    //the delay to wait after attempting a matching selection
    private static final long SELECTION_DELAY = Timers.toNanoSeconds(1000L);
    
    //to keep track of time
    private Timer timer;
    
    //where the player is currently located
    private int playerCol, playerRow;
    
    //list of all possible tiles
    private HashMap<Type, Tile> tiles;
    
    //places where the cards will lie
    private Place[][] places;
    
    //the recent selections the player has made
    private List<Cell> selections;
    
    //did our previous match pass or fail
    private boolean success = false;
    
    //is this oject being displayed
    private boolean display = false;
    
    //keep track of failed attempts
    private int attempts = 0;
    
    //the number of attempts before the game ends
    private static final int FAILED_ATTEMPTS_LIMIT = 3;
    
    private enum Type
    {
        Mushroom(0,192, 22, 32),
        Flower(22,192, 22, 32),
        Coin10(44,192, 22, 32),
        Coin20(66,192, 22, 32),
        ExtraLife(88,192, 22, 32),
        Star(110,192, 22, 32),
        Hidden(132,192, 22, 32),
        Selection(154,192, 28, 44);
        
        private int x, y, w, h;
        
        private Type(final int x, final int y, final int w, final int h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }
    
    public Matching(final Image image)
    {
        try
        {
            if ((ROWS * COLUMNS) % 2 != 0)
                throw new Exception("There needs to be an even amount of places.");
            
            //create new timer with delay
            timer = new Timer(SELECTION_DELAY);
            
            super.setImage(image);

            //setup background
            super.addAnimation("Default", 1, 0, 0, Shared.ORIGINAL_WIDTH, Shared.ORIGINAL_HEIGHT, 0, false);
            super.setDimensions();

            //create empty list of selections made
            this.selections = new ArrayList<>();
            
            //create empty list
            this.tiles = new HashMap<>();

            //populate list with each type
            for (int i = 0; i < Type.values().length; i++)
            {
                //add to hash map
                this.tiles.put(Type.values()[i], new Tile(Type.values()[i]));
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void setDisplayed(final boolean display)
    {
        this.display = display;
        
        if (display)
            setReset();
    }
    
    @Override
    public boolean isDisplayed()
    {
        return this.display;
    }
    
    private final class Place
    {
        //the Tile type
        private final Type type;
        
        //by default the place is hidden
        private boolean hidden = true;
        
        private Place(final Type type)
        {
            this.type = type;
        }
        
        private Type getType()
        {
            return this.type;
        }
        
        private boolean isHidden()
        {
            return this.hidden;
        }
        
        private void setHidden(final boolean hidden)
        {
            this.hidden = hidden;
        }
    }
    
    private final class Tile extends Entity
    {
        private Tile(final Type type)
        {
            //add default animation
            super.addAnimation(type, 1, type.x, type.y, type.w, type.h, 0, false);
            
            //set the dimension based on current animation
            super.setDimensions();
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
        
        places = null;
        
        timer = null;
    }
    
    @Override
    protected void calculateWin(final Hero hero)
    {
        //get the type of the first selection
        Type type = getPlace(selections.get(0)).getType();
        
        //currently mark as successful
        success = true;
        
        for (int i = 0; i < selections.size(); i++)
        {
            //if the current selection does not match
            if (getPlace(selections.get(i)).getType() != type)
            {
                //failed attempt we are no longer successful
                success = false;
                break;
            }
        }
        
        //we have a match
        if (success)
        {
            try
            {
                //determine the reward
                switch(type)
                {
                    case Mushroom:
                        hero.setBig(true);
                        break;
                        
                    case Flower:
                        hero.setFire(true);
                        break;

                    case Coin10:
                        for (int i = 0; i < 10; i++)
                        {
                            hero.addCoin();
                        }
                        break;
                        
                    case Coin20:
                        for (int i = 0; i < 20; i++)
                        {
                            hero.addCoin();
                        }
                        break;
                        
                    case ExtraLife:
                        hero.setLives(hero.getLives() + 1);
                        break;
                        
                    case Star:
                        hero.setInvincible(true);
                        break;
                        
                    default:
                        throw new Exception("Reward is not setup here");
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            //we have a failed attempt
            attempts++;
        }
    }
    
    @Override
    public boolean isComplete()
    {
        //if we have reached the number of failed attempts the game is complete
        if (attempts == FAILED_ATTEMPTS_LIMIT)
            return true;
        
        for (int row = 0; row < places.length; row++)
        {
            for (int col = 0; col < places[0].length; col++)
            {
                //if we have at least 1 card that is not selected, the game is not complete
                if (getPlace(col, row).isHidden())
                    return false;
            }
        }
        
        //the game is complete
        return true;
    }
    
    @Override
    public void reset(final Random random)
    {
        //choose random bonus music to play
        super.setAudioKey(GameAudio.getBonusMusic(random));
        
        super.unflagReset();
        
        //make timer finish on purpose
        timer.setRemaining(0);
        
        //there is no previous attempt yet
        success = false;
        
        //clear list of selections made
        selections.clear();
        
        //reset the number of failed attempts
        attempts = 0;
        
        //move player back to origin
        playerCol = 0;
        playerRow = 0;
        
        //create new array
        this.places = new Place[ROWS][COLUMNS];
        
        //temp list that we will assign to the places
        List<Type> tmp = new ArrayList<>();
        
        final int limit = places.length * places[0].length;
        
        //keep looping until we have a tile type for each place
        while (tmp.size() < limit)
        {
            for (int i = 0; i < Type.values().length; i++)
            {
                //get the current type
                Type type = Type.values()[i];
                
                try
                {
                    switch(type)
                    {
                        case Mushroom:
                        case Flower:
                        case Coin10:
                        case Coin20:
                        case ExtraLife:
                        case Star:
                            //add the type to the list twice so there is a matching tile
                            tmp.add(type);
                            tmp.add(type);
                            break;

                        //we don't want to add these, so skip them
                        case Hidden:
                        case Selection:
                            continue;

                        default:
                            throw new Exception("Type not setup here: " + type.toString());
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                
                //check to see if we reached our limit yet
                if (tmp.size() >= limit)
                    break;
            }
        }

        for (int row = 0; row < places.length; row++)
        {
            for (int col = 0; col < places[0].length; col++)
            {
                //choose random type from list
                final int index = random.nextInt(tmp.size());
                
                //create new place 
                this.places[row][col] = new Place(tmp.get(index));
                
                //remove from list
                tmp.remove(index);
            }
        }
        
        tmp.clear();
        tmp = null;
    }
    
    @Override
    public void update(final Engine engine)
    {
        //update parent
        super.update(engine);
        
        //get the input object
        final Keyboard keyboard = engine.getKeyboard();

        //if time has not passed yet
        if (!timer.hasTimePassed())
        {
            //update timer
            timer.update(engine.getMain().getTime());

            //if time has now passed
            if (timer.hasTimePassed())
            {
                //if our previous attempt was not successful make selections hidden
                if (!success)
                {
                    //mark selections as hidden again
                    for (int i = 0; i < selections.size(); i++)
                    {
                        getPlace(selections.get(i)).setHidden(true);
                    }
                }

                //clear selections
                selections.clear();
            }

            //don't continue yet
            return;
        }

        //if the game is complete we will switch back
        if (isComplete())
        {
            //switch back to mini map
            engine.getManager().getMario().switchBack(engine.getManager().getWorld());
            
            //mark current tile as complete
            engine.getManager().getWorld().getMap().markLevelComplete();
            
            //reset game
            reset(engine.getRandom());
            
            //reset input
            engine.getKeyboard().reset();
        }

        if (keyboard.hasKeyReleased(Input.KEY_MOVE_DOWN))
        {
            playerRow++;

            if (playerRow >= ROWS)
                playerRow = 0;
        }
        else if (keyboard.hasKeyReleased(Input.KEY_MOVE_UP))
        {
            playerRow--;

            if (playerRow < 0)
                playerRow = ROWS - 1;
        }
        else if (keyboard.hasKeyReleased(Input.KEY_MOVE_LEFT))
        {
            playerCol--;

            if (playerCol < 0)
                playerCol = COLUMNS - 1;
        }
        else if (keyboard.hasKeyReleased(Input.KEY_MOVE_RIGHT))
        {
            playerCol++;

            if (playerCol >= COLUMNS)
                playerCol = 0;
        }
        else if (keyboard.hasKeyReleased(Input.KEY_JUMP) || keyboard.hasKeyReleased(Input.KEY_FIREBALL))
        {
            //can only select if it is currently hidden
            if (getPlace().isHidden())
            {
                //no longer hide place
                getPlace().setHidden(false);

                //add selection to list
                selections.add(new Cell(playerCol, playerRow));

                //we have seleccted 2 places now check for match
                if (selections.size() == 2)
                {
                    //restart timer
                    timer.reset();

                    //check if we have match or failed attempt
                    calculateWin(engine.getManager().getMario());
                    
                    //play sound if won or not
                    engine.getResources().playGameAudio((success) ? GameAudio.Keys.SfxGameMatch : GameAudio.Keys.SfxGameNoMatch);
                }
            }
        }

        //reset input
        keyboard.reset();
    }
    
    private Place getPlace()
    {
        return getPlace(playerCol, playerRow);
    }
    
    private Place getPlace(final Cell cell)
    {
        return getPlace((int)cell.getCol(), (int)cell.getRow());
    }
    
    private Place getPlace(final int col, final int row)
    {
        return places[row][col];
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        //draw background
        super.draw(graphics);
        
        for (int row = 0; row < ROWS; row++)
        {
            for (int col = 0; col < COLUMNS; col++)
            {
                //determine the center location of each place
                final int x = START_X + (col * WIDTH) + (WIDTH / 2);
                final int y = START_Y + (row * HEIGHT) + (HEIGHT / 2);
                
                //get the specified tile
                Tile tile = tiles.get(getPlace(col, row).isHidden() ? Type.Hidden : getPlace(col, row).getType());
                
                //set the location
                tile.setLocation(x - (tile.getWidth() / 2), y - (tile.getHeight() / 2));
                
                //draw the tile
                tile.draw(graphics, getImage());
                
                //if this is the location where the player is
                if (playerCol == col && playerRow == row)
                {
                    //get the selection tile
                    tile = tiles.get(Type.Selection);
                    
                    //set the location
                    tile.setLocation(x - (tile.getWidth() / 2), y - (tile.getHeight() / 2));
                    
                    //draw the tile
                    tile.draw(graphics, getImage());
                }
            }
        }
    }
}