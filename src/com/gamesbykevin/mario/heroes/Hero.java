package com.gamesbykevin.mario.heroes;

import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.entity.Entity;
import com.gamesbykevin.mario.shared.IElement;

import java.awt.Graphics;

public abstract class Hero extends Entity implements IElement
{
    /**
     * The different animations for the hero
     */
    public enum State
    {
        //all of the different things we can do while small
        SmallMiniMap, SmallIdle, SmallWalk, SmallRun, SmallDeath, SmallJump, SmallChangeDirection, 
        
        //the things we can do while big
        BigMiniMap, BigIdle, BigWalk, BigRun, BigJump, BigChangeDirection, BigDuck, 
        
        //the things we can do while big fire
        FireMiniMap, FireIdle, FireWalk, FireRun, FireJump, FireChangeDirection, FireDuck, FireAttack,
        
        //the fireball animation
        Fireball
    }
    
    @Override
    public void update(final Engine engine)
    {
        //update parent entity
        super.update(engine.getMain().getTime());
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        super.draw(graphics);
    }
}
