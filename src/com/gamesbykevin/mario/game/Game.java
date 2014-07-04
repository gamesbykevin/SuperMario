package com.gamesbykevin.mario.game;

import com.gamesbykevin.framework.resources.Disposable;

import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.heroes.Hero;
import com.gamesbykevin.mario.shared.IElement;

import java.util.Random;

public abstract class Game extends Entity implements IGame, Disposable, IElement
{
    ///does this game need to be reset
    private boolean reset = true;
    
    @Override
    public void dispose()
    {
        super.dispose();
    }
    
    @Override
    public void setReset()
    {
        this.reset = true;
    }
    
    protected void unflagReset()
    {
        this.reset = false;
    }
    
    /**
     * Is reset been flagged
     * @return true if the game needs to be reset, false otherwise
     */
    public boolean hasReset()
    {
        return this.reset;
    }
    
    /**
     * Each game has it's own logic to reset
     * @param random Object used to make random decisions
     */
    public abstract void reset(final Random random);
    
    /**
     * Each game needs to determine how to win
     * @param hero The hero that will receive the reward
     */
    protected abstract void calculateWin(final Hero hero);
}