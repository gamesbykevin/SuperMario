package com.gamesbykevin.mario.resources;

import com.gamesbykevin.framework.resources.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * All audio for game
 * @author GOD
 */
public final class GameAudio extends AudioManager
{
    //description for progress bar
    private static final String DESCRIPTION = "Loading Audio Resources";
    
    private static List<Keys> options = new ArrayList<>();
    
    /**
     * These are the keys used to access the resources and need to match the id in the xml file
     */
    public enum Keys
    {
        MusicBonus1, MusicBonus2, MusicBonus3,         
        MusicLevel01, MusicLevel02, MusicLevel03, MusicLevel04, MusicLevel05,
        MusicLevel06, MusicLevel07, MusicLevel08, MusicLevel09, MusicLevel10, 
        MusicInvincible1, MusicInvincible2, MusicInvincible3, 
        MusicMap1, MusicMap2, MusicMap3, MusicMap4, MusicMap5, MusicMap6, MusicMap7, 
        MusicMapWorldClear, 
        SfxGameMatch, SfxGameNoMatch, 
        SfxMapMove, SfxMapEnter, 
        SfxLevelBreakableBrick1, SfxLevelBreakableBrick2, 
        SfxLevelBulletBill,
        SfxLevelBump1, SfxLevelBump2,
        SfxLevelCoin1, SfxLevelCoin2,
        SfxLevelDie, 
        SfxLevelEnemyFireball,
        SfxLevelExtraLife, 
        SfxLevelGameOver1, SfxLevelGameOver2, SfxLevelGameOver3,
        SfxLevelHeroFireball,
        SfxLevelJumpSmall, SfxLevelJumpBig,
        SfxLevelKick,
        SfxLevelClear1, SfxLevelClear2, SfxLevelClear3, 
        SfxLevelPowerDown, SfxLevelPowerUp, 
        SfxLevelPowerUpHit1, SfxLevelPowerUpHit2,
        SfxLevelSkeletonStomp,
        SfxLevelStomp1, SfxLevelStomp2,
        SfxLevelThwomp
    }
    
    public GameAudio() throws Exception
    {
        super(Resources.XML_CONFIG_GAME_AUDIO);
        
        //the description that will be displayed for the progress bar
        super.setProgressDescription(DESCRIPTION);
        
        if (Keys.values().length < 1)
            super.increase();
    }
    
    public static Keys getInvincibleMusic(final Random random)
    {
        //clear list
        options.clear();
        
        for (int i = 0; i < Keys.values().length; i++)
        {
            switch (Keys.values()[i])
            {
                case MusicInvincible1:
                case MusicInvincible2:
                case MusicInvincible3:
                    options.add(Keys.values()[i]);
                    break;
            }
        }
        
        return options.get(random.nextInt(options.size()));
    }
    
    public static Keys getLevelCompleteMusic(final Random random)
    {
        //clear list
        options.clear();
        
        for (int i = 0; i < Keys.values().length; i++)
        {
            switch (Keys.values()[i])
            {
                case SfxLevelClear1:
                case SfxLevelClear2:
                case SfxLevelClear3:
                    options.add(Keys.values()[i]);
                    break;
            }
        }
        
        return options.get(random.nextInt(options.size()));
    }
    
    public static Keys getGameOverMusic(final Random random)
    {
        //clear list
        options.clear();
        
        for (int i = 0; i < Keys.values().length; i++)
        {
            switch (Keys.values()[i])
            {
                case SfxLevelGameOver1:
                case SfxLevelGameOver2:
                case SfxLevelGameOver3:
                    options.add(Keys.values()[i]);
                    break;
            }
        }
        
        return options.get(random.nextInt(options.size()));
    }
    
    public static Keys getMapMusic(final Random random)
    {
        //clear list
        options.clear();
        
        for (int i = 0; i < Keys.values().length; i++)
        {
            switch (Keys.values()[i])
            {
                case MusicMap1:
                case MusicMap2:
                case MusicMap3:
                case MusicMap4:
                case MusicMap5:
                case MusicMap6:
                case MusicMap7:
                    options.add(Keys.values()[i]);
                    break;
            }
        }
        
        return options.get(random.nextInt(options.size()));
    }
    
    public static Keys getBonusMusic(final Random random)
    {
        //clear list
        options.clear();
        
        for (int i = 0; i < Keys.values().length; i++)
        {
            switch (Keys.values()[i])
            {
                case MusicBonus1:
                case MusicBonus2:
                case MusicBonus3:
                    options.add(Keys.values()[i]);
                    break;
            }
        }
        
        return options.get(random.nextInt(options.size()));
    }
    
    public static boolean isLevelMusic(final GameAudio.Keys key)
    {
        switch(key)
        {
            case MusicLevel01:
            case MusicLevel02:
            case MusicLevel03:
            case MusicLevel04:
            case MusicLevel05:
            case MusicLevel06:
            case MusicLevel07:
            case MusicLevel08:
            case MusicLevel09:
            case MusicLevel10:
                return true;
                
            default:
                return false;
        }
    }
}