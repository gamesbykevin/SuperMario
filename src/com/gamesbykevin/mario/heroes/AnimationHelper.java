package com.gamesbykevin.mario.heroes;

/**
 * This class will set the proper animation for the hero
 * @author GOD
 */
public final class AnimationHelper 
{
    public static void setAnimationVictory(final Hero hero)
    {
        if (!hero.isBig())
        {
            //set accordingly
            hero.setAnimation(Hero.State.SmallVictory, false);
        }
        else if (hero.hasFire())
        {
            //we have fire so set accordingly
            hero.setAnimation(Hero.State.FireVictory, false);
        }
        else
        {
            //we are just big so set accordingly
            hero.setAnimation(Hero.State.BigVictory, false);
        }
    }
    
    public static Hero.State getDefaultAnimation(final Hero hero)
    {
        if (hero.hasFire())
        {
            return Hero.State.FireIdle;
        }
        else
        {
            if (hero.isBig())
            {
                return Hero.State.BigIdle;
            }
            else
            {
                return Hero.State.SmallIdle;
            }
        }
    }
    
    public static void setAnimationAttack(final Hero hero)
    {
        if (hero.hasFire())
        {
            //we have fire so set accordingly
            hero.setAnimation(Hero.State.FireAttack, false);
        }
    }
    
    public static void setAnimationDead(final Hero hero)
    {
        hero.setAnimation(Hero.State.Dead, false);
    }
    
    public static void setAnimationDuck(final Hero hero)
    {
        if (!hero.isBig())
        {
            //there is not animation for small so do nothing
        }
        else if (hero.hasFire())
        {
            //we have fire so set accordingly
            hero.setAnimation(Hero.State.FireDuck, false);
        }
        else
        {
            //we are just big so set accordingly
            hero.setAnimation(Hero.State.BigDuck, false);
        }
    }
    
    public static void setAnimationJump(final Hero hero)
    {
        if (!hero.isBig())
        {
            //we are not big so set small
            hero.setAnimation(Hero.State.SmallJump, false);
        }
        else if (hero.hasFire())
        {
            //we have fire so set accordingly
            hero.setAnimation(Hero.State.FireJump, false);
        }
        else
        {
            //we are just big so set accordingly
            hero.setAnimation(Hero.State.BigJump, false);
        }
    }
    
    public static void setAnimationRun(final Hero hero)
    {
        if (!hero.isBig())
        {
            //we are not big so set small
            hero.setAnimation(Hero.State.SmallRun, false);
        }
        else if (hero.hasFire())
        {
            //we have fire so set accordingly
            hero.setAnimation(Hero.State.FireRun, false);
        }
        else
        {
            //we are just big so set accordingly
            hero.setAnimation(Hero.State.BigRun, false);
        }
    }
    
    public static void setAnimationWalk(final Hero hero)
    {
        if (!hero.isBig())
        {
            //we are not big so set small
            hero.setAnimation(Hero.State.SmallWalk, false);
        }
        else if (hero.hasFire())
        {
            //we have fire so set accordingly
            hero.setAnimation(Hero.State.FireWalk, false);
        }
        else
        {
            //we are just big so set accordingly
            hero.setAnimation(Hero.State.BigWalk, false);
        }
    }
    
    public static void setAnimationIdle(final Hero hero)
    {
        if (!hero.isBig())
        {
            //we are not big so set small
            hero.setAnimation(Hero.State.SmallIdle, false);
        }
        else if (hero.hasFire())
        {
            //we have fire so set accordingly
            hero.setAnimation(Hero.State.FireIdle, false);
        }
        else
        {
            //we are just big so set accordingly
            hero.setAnimation(Hero.State.BigIdle, false);
        }
    }
    
    public static void setAnimationMiniMap(final Hero hero)
    {
        if (!hero.isBig())
        {
            //we are not big so set small
            hero.setAnimation(Hero.State.SmallMiniMap, false);
        }
        else if (hero.hasFire())
        {
            //we have fire so set accordingly
            hero.setAnimation(Hero.State.FireMiniMap, false);
        }
        else
        {
            //we are just big so set accordingly
            hero.setAnimation(Hero.State.BigMiniMap, false);
        }
    }
}