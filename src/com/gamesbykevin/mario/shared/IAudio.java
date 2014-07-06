package com.gamesbykevin.mario.shared;

import com.gamesbykevin.mario.resources.GameAudio;

public interface IAudio 
{
    public void setAudioKey(final GameAudio.Keys audioKey);
    
    public GameAudio.Keys getAudioKey();
}