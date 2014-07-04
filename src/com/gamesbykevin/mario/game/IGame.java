package com.gamesbykevin.mario.game;

public interface IGame 
{
    /**
     * Flag the game to be reset
     */
    public void setReset();
    
    /**
     * Has the game finished
     * @return true if it has finished, false otherwise
     */
    public boolean isComplete();
}
