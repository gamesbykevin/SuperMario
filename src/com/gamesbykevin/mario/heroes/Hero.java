package com.gamesbykevin.mario.heroes;

import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.powerups.PowerUps;
import com.gamesbykevin.mario.level.tiles.*;
import com.gamesbykevin.mario.level.tiles.Tiles.Type;
import com.gamesbykevin.mario.shared.IElement;

import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

public abstract class Hero extends Character implements IElement 
{
    //by default the hero isn't big
    private boolean big = false;
    
    //by default the hero doesn't have fire
    private boolean fire = false;
    
    //by default the hero won't be invincible
    private boolean invincible = false;
    
    //by default the hero won't be hurt
    private boolean hurt = false;
    
    //store if we are jumping
    private boolean jumping = false;
    
    //has the hero solved the puzzle
    private boolean victory = false;
    
    //the fireball mario can fire
    private Projectile fireball;
    
    //our invincible image
    private BufferedImage invincibleImage;
    
    //our transparent image
    private BufferedImage transparentImage;
    
    private static final double SCROLL_WEST_RATIO = .25;
    private static final double SCROLL_EAST_RATIO = .65;
    
    /**
     * What are the chances hitting this block is a power up
     */
    private static final int BLOCK_POWER_UP_PROBABILITY = 5;
    
    //how long to wait until switching to other image
    private static final long INVINCIBLE_IMAGE_SWITCH_DELAY = Timers.toNanoSeconds(100L);
    
    //how long the hero will be invincible for
    private static final long INVINCIBLE_DELAY = Timers.toNanoSeconds(10000L);
    
    //how long will mario be hurt before receiving damage again
    private static final long HURT_DELAY = Timers.toNanoSeconds(5000L);
    
    private enum TimerKey
    {
        ImageSwitch, InvincibilityDuration, Hurt
    }
    
    //do we switch between images
    private boolean switchImage = false;
    
    //this object will keep track of multiple time(s)
    private Timers timers;
    
    /**
     * The different animations for the hero
     */
    public enum State
    {
        //all of the different things we can do while small
        SmallMiniMap, SmallIdle, SmallWalk, SmallRun, SmallJump, SmallVictory, 
        
        //the things we can do while big
        BigMiniMap, BigIdle, BigWalk, BigRun, BigJump, BigDuck, BigVictory, 
        
        //the things we can do while big fire
        FireMiniMap, FireIdle, FireWalk, FireRun, FireJump, FireDuck, FireAttack, FireVictory, 
        
        //the projectile
        Fireball,
        
        //mario dead
        Dead, 
    }
    
    protected Hero()
    {
        super(Character.DEFAULT_JUMP_VELOCITY, Character.DEFAULT_SPEED_WALK, Character.DEFAULT_SPEED_RUN);
    }
    
    /**
     * Create the alternate images for the hero (invincible, hurt, etc...)
     */
    public void createMiscImages()
    {
        try
        {
            //create new image
            this.invincibleImage = new BufferedImage(getImage().getWidth(null), getImage().getHeight(null), BufferedImage.TYPE_INT_ARGB);
            
            //get graphics object to write
            Graphics2D g2d = this.invincibleImage.createGraphics();
            
            //write original spritesheet to this image
            g2d.drawImage(getImage(), 0, 0, null);
            
            for (int x = 0; x < invincibleImage.getWidth(); x++) 
            {
                for (int y = 0; y < invincibleImage.getHeight(); y++) 
                {
                    invincibleImage.setRGB(x, y, invincibleImage.getRGB(x, y) & 0xff00ff00);
                }
            }
            
            //create new image
            this.transparentImage = new BufferedImage(getImage().getWidth(null), getImage().getHeight(null), BufferedImage.TYPE_INT_ARGB);
            
            //get the graphics object to write
            g2d = this.transparentImage.createGraphics();
            
            //set transparency for this image
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            
            //write original spritesheet to this image
            g2d.drawImage(getImage(), 0, 0, null);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public State getDefaultAnimation()
    {
        if (hasFire())
        {
            return State.FireIdle;
        }
        else
        {
            if (isBig())
            {
                return State.BigIdle;
            }
            else
            {
                return State.SmallIdle;
            }
        }
    }
    
    public void setAnimationAttack()
    {
        if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireAttack, false);
        }
    }
    
    public void setAnimationDead()
    {
        setAnimation(State.Dead, false);
    }
    
    public void setAnimationVictory()
    {
        if (!isBig())
        {
            //set accordingly
            setAnimation(State.SmallVictory, false);
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireVictory, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigVictory, false);
        }
    }
    
    public void setAnimationDuck()
    {
        if (!isBig())
        {
            //there is not animation for small so do nothing
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireDuck, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigDuck, false);
        }
    }
    
    public void setAnimationJump()
    {
        if (!isBig())
        {
            //we are not big so set small
            setAnimation(State.SmallJump, false);
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireJump, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigJump, false);
        }
    }
    
    public void setAnimationRun()
    {
        if (!isBig())
        {
            //we are not big so set small
            setAnimation(State.SmallRun, false);
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireRun, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigRun, false);
        }
    }
    
    public void setAnimationWalk()
    {
        if (!isBig())
        {
            //we are not big so set small
            setAnimation(State.SmallWalk, false);
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireWalk, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigWalk, false);
        }
    }
    
    public void setAnimationIdle()
    {
        if (!isBig())
        {
            //we are not big so set small
            setAnimation(State.SmallIdle, false);
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireIdle, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigIdle, false);
        }
    }
    
    public void setAnimationMiniMap()
    {
        if (!isBig())
        {
            //we are not big so set small
            setAnimation(State.SmallMiniMap, false);
        }
        else if (hasFire())
        {
            //we have fire so set accordingly
            setAnimation(State.FireMiniMap, false);
        }
        else
        {
            //we are just big so set accordingly
            setAnimation(State.BigMiniMap, false);
        }
    }
    
    public void setInvincible(final boolean invincible)
    {
        this.invincible = invincible;
        
        if (invincible)
        {
            setHurt(false);
        }
    }
    
    public boolean isHurt()
    {
        return this.hurt;
    }
    
    public void setHurt(final boolean hurt)
    {
        this.hurt = hurt;
    }
    
    public boolean isInvincible()
    {
        return this.invincible;
    }
    
    public void setBig(final boolean big)
    {
        this.big = big;
    }
    
    public boolean isBig()
    {
        return this.big;
    }
    
    public void setVictory(final boolean victory)
    {
        this.victory = victory;
    }
    
    public boolean hasVictory()
    {
        return this.victory;
    }
    
    public void setFire(final boolean fire)
    {
        this.fire = fire;
    }
    
    public boolean hasFire()
    {
        return this.fire;
    }
    
    public boolean hasFireball()
    {
        return (fireball != null);
    }
    
    private void removeFireball()
    {
        this.fireball = null;
        this.setAttack(false);
    }
    
    private void setupTimers(final long time)
    {
        this.timers = new Timers(time);
        this.timers.add(TimerKey.ImageSwitch, INVINCIBLE_IMAGE_SWITCH_DELAY);
        this.timers.add(TimerKey.InvincibilityDuration, INVINCIBLE_DELAY);
        this.timers.add(TimerKey.Hurt, HURT_DELAY);
        this.timers.reset();
    }
    
    private void updateTimers()
    {
        //update timers when invincible
        if (isInvincible())
        {
            if (timers.hasTimePassed(TimerKey.ImageSwitch))
            {
                //flip images
                this.switchImage = !switchImage;

                //reset timer
                timers.reset(TimerKey.ImageSwitch);
            }
            else
            {
                //update timer
                timers.update(TimerKey.ImageSwitch);
            }

            if (timers.hasTimePassed(TimerKey.InvincibilityDuration))
            {
                //no longer invincible
                this.setInvincible(false);

                //back to default image
                this.switchImage = false;

                //reset timer
                timers.reset(TimerKey.InvincibilityDuration);
            }
            else
            {
                //update timer
                timers.update(TimerKey.InvincibilityDuration);
            }
        }
        
        if (isHurt())
        {
            if (timers.hasTimePassed(TimerKey.Hurt))
            {
                //we are no longer hurt
                setHurt(false);
                
                //reser timer
                timers.reset(TimerKey.Hurt);
            }
            else
            {
                //update timer
                timers.update(TimerKey.Hurt);
            }
        }
    }
    
    @Override
    public void update(final Engine engine)
    {
        if (timers == null)
            setupTimers(engine.getMain().getTime());
        
        //update our timers
        updateTimers();
        
        //are we currently jumping/falling
        this.jumping = super.isJumping();
        
        //update parent entity
        super.update(engine.getMain().getTime());
        
        //update location
        super.update();
        
        //apply gravity
        applyGravity(engine.getManager().getLevel().getTiles());
        
        //manage the heroes collision with the level
        manageLevelCollision(engine.getManager().getLevel(), engine.getRandom());
        
        //if the hero is off the screen, we are dead
        if (getY() > engine.getManager().getWindow().y + engine.getManager().getWindow().height)
            markDead();
        
        if (hasFireball())
        {
            fireball.update(engine.getMain().getTime());
            fireball.update();
            fireball.applyGravity(engine.getManager().getLevel().getTiles());
            fireball.manageLevelCollision(engine.getManager().getLevel(), engine.getRandom());
            
            //flag fireball as dead if no longer on the screen
            if (!engine.getManager().getWindow().contains(fireball.getCenter()))
                fireball.markDead();
            
            if (fireball.isDead())
                removeFireball();
        }
        
        //manage power up collision
        managePowerUp(engine.getManager().getLevel().getPowerUpCollision(this), engine.getManager().getLevel().getTiles());
        
        //make sure correct animation is set
        checkAnimation();
        
        //check if we are to scroll the level
        checkScroll(engine.getManager().getLevel(), engine.getManager().getWindow());
    }
    
    private void manageLevelCollision(final Level level, final Random random)
    {
        Tiles tiles = level.getTiles();
        
        PowerUps powerUps = level.getPowerUps();
        
        //check for collision to the north first
        Tile north = checkCollisionNorth(tiles);
        
        //no collision was found north, now check the rest
        if (north == null || !isJumping())
        {
            if (getVelocityX() < 0)
            {
                Tile west = checkCollisionWest(tiles);
                
                if (west != null)
                {
                    setVelocityX(SPEED_NONE);
                    
                    if (!isInvincible())
                    {
                        if (west.hasDamage())
                        {
                            switch (west.getType())
                            {
                                case RotatingGear:
                                case RotatingGear2:
                                    markHurt();
                                    break;
                            }
                        }
                    }
                }
                
                if (checkCollisionNorthWest(tiles) != null)
                    setVelocityX(SPEED_NONE);
            }
            
            if (getVelocityX() > 0)
            {
                Tile east = checkCollisionEast(tiles);
                
                //if collision with east
                if (east != null)
                {
                    setVelocityX(SPEED_NONE);
                    
                    if (!isInvincible())
                    {
                        if (east.hasDamage())
                        {
                            switch (east.getType())
                            {
                                case RotatingGear:
                                case RotatingGear2:
                                    markHurt();
                                    break;
                            }
                        }
                    }
                }
                
                if (checkCollisionNorthEast(tiles) != null)
                    setVelocityX(SPEED_NONE);
            }
            
            Tile south = checkCollisionSouth(tiles);
            
            //if we hit a tile at our feet, make sure to stop
            if (south != null)
            {
                if (super.isJumping())
                    super.stopJumping();
                
                if (south.getType() == Tiles.Type.Goal)
                {
                    //remove goal tile
                    tiles.remove((int)south.getCol(), (int)south.getRow());
                    
                    //set as complete
                    tiles.add(Tiles.Type.GoalComplete, south.getCol(), south.getRow(), south.getX(), south.getY());
                    
                    //mark level as complete
                    level.setComplete(true);
                    
                    //set animation
                    setVictory(true);
                }
                
                if (!isInvincible())
                {
                    if (south.hasDeath())
                    {
                        switch (south.getType())
                        {
                            case Lava:
                            case Water1:
                            case Water2:
                                markDead();
                                setY(south.getY() - Tile.HEIGHT);
                                break;
                        }
                    }
                    else if (south.hasDamage())
                    {
                        switch (south.getType())
                        {
                            case RotatingGear:
                            case RotatingGear2:
                            case SpikesUp1:
                            case SpikesUp2:
                                markHurt();
                                break;
                        }
                    }
                }
            }
        }
        else
        {
            //if moving north check if the tile will hurt
            if (getVelocityY() < 0)
            {
                //also start falling down
                setVelocityY(VELOCITY_DECREASE);
                
                //if the block can cause death
                if (north.hasDeath())
                {
                    if (!isHurt())
                    {
                        //mark the hero as dead
                        markDead();
                    }
                }
                else if (north.hasDamage())
                {
                    if (!isHurt())
                    {
                        //mark the hero hurt
                        markHurt();
                    }
                }
            }
            
            switch (north.getType())
            {
                case QuestionBlock:
                    tiles.add(Tiles.Type.UsedBlock, north.getCol(), north.getRow(), north.getX(), north.getY());

                    //choose at random if we are to add a power up, that is not a coin
                    if (random.nextInt(BLOCK_POWER_UP_PROBABILITY) == 0)
                    {
                        //the block can do two different things
                        if (random.nextBoolean())
                        {
                            //determine mario power up
                            if (!isBig())
                            {
                                //if not big it will be a mushroom
                                powerUps.add(PowerUps.Type.Mushroom, north.getX(), north.getY(), north.getY() - Tile.HEIGHT);
                            }
                            else
                            {
                                //if not big it will be a fire flower
                                powerUps.add(PowerUps.Type.Flower, north.getX(), north.getY(), north.getY() - Tile.HEIGHT);
                            }
                        }
                        else
                        {
                            //choose star
                            powerUps.add(PowerUps.Type.Star, north.getX(), north.getY(), north.getY() - Tile.HEIGHT);
                        }
                    }
                    else
                    {
                        //need to add animation effect of collecting a coin here
                        level.getEffects().add(north.getX(), north.getY() - north.getHeight(), Effects.Type.CollectCoin);
                    }
                    break;
                    
                case BreakableBrick:
                    
                    //can only break a brick if big or has fire
                    if (isBig() || this.hasFire())
                    {
                        //remove tile
                        tiles.remove((int)north.getCol(), (int)north.getRow());
                        
                        //need to add animation effect of brick breaking here
                        level.getEffects().add(north, Effects.Type.BreakBrick);
                    }
                    break;
            }
        }
    }
    
    /**
     * Mark the hero as hurt, if the hero is invincible or already hurt nothing will happen here.
     */
    private void markHurt()
    {
        //make sure we are not invincible
        if (!isInvincible())
        {
            //also make sure we are not hurt
            if (!isHurt())
            {
                //flag hurt 
                setHurt(true);

                //reset timer
                timers.reset(TimerKey.Hurt);

                //downgrade the hero
                if (hasFire())
                {
                    //if fire then downgrade
                    setFire(false);
                    setBig(true);
                }
                else if (isBig())
                {
                    //if big then downgrade
                    setFire(false);
                    setBig(false);
                }
                else
                {
                    //if small then dead
                    markDead();
                }
            }
        }
    }
    
    @Override
    public void markDead()
    {
        //flag as dead
        super.markDead();

        //set the appropriate flags
        setFire(false);
        setBig(false);

        //stop moving
        resetVelocity();
    }
    
    public void addFireball()
    {
        this.fireball = new Projectile();
        
        if (hasHorizontalFlip())
        {
            this.fireball.setLocation(getX() - fireball.getWidth(), getY());
            this.fireball.setVelocityX(-Projectile.DEFAULT_VELOCITY_X);
        }
        else
        {
            this.fireball.setLocation(getX() + getWidth(), getY());
            this.fireball.setVelocityX(Projectile.DEFAULT_VELOCITY_X);
        }
        
        this.fireball.setVelocityY(Projectile.DEFAULT_VELOCITY_Y);
        this.fireball.startJump();
    }
        
    private void managePowerUp(final PowerUps.Type type, final Tiles tiles)
    {
        try
        {
            if (type == null)
                return;

            switch (type)
            {
                case Mushroom:
                    setBig(true);
                    setAnimation(getDefaultAnimation(), false);
                    setDimensions();
                    
                    Tile south = checkCollisionSouth(tiles);
            
                    //correct mario y location
                    if (south != null)
                        setY(south.getY() - getHeight());
                    break;
                    
                case Flower:
                    setBig(true);
                    setFire(true);
                    setAnimation(getDefaultAnimation(), false);
                    setDimensions();
                    
                    south = checkCollisionSouth(tiles);
            
                    //correct mario y location
                    if (south != null)
                        setY(south.getY() - getHeight());
                    break;
                    
                case Star:
                    setAnimation(getDefaultAnimation(), false);
                    
                    //flag invincible
                    setInvincible(true);
                    
                    //reset invincible timers
                    timers.reset();
                    break;
                    
                case Coin:
                    break;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Determine if we need to scroll the level tiles/background
     * @param level 
     */
    private void checkScroll(final Level level, final Rectangle window)
    {
        final double westX  = window.x + (window.width * SCROLL_WEST_RATIO);
        final double eastX  = window.x + (window.width * SCROLL_EAST_RATIO);
        
        //determine where we want to scroll (if any)
        final boolean scrollWest = (getX() < westX) && (getVelocityX() < 0);
        final boolean scrollEast = (getX() > eastX) && (getVelocityX() > 0);
        
        if (scrollWest)
        {
            setX(westX);
            level.setScrollX((level.getTiles().canScrollWest(window.x)) ? -getVelocityX() : Character.SPEED_NONE);
        }
        
        if (scrollEast)
        {
            setX(eastX);
            level.setScrollX((level.getTiles().canScrollEast(window.x + window.width - Tile.WIDTH)) ? -getVelocityX() : Character.SPEED_NONE);
        }
        
        if (!hasVelocityX() || isIdle())
            level.setScrollX(Character.SPEED_NONE);
    }
    
    /**
     * Set the correct animation
     */
    @Override
    protected void checkAnimation()
    {
        //default animation is idle
        setAnimationIdle();
        
        if (isDucking())
            setAnimationDuck();
        if (isWalking())
            setAnimationWalk();
        if (isRunning())
            setAnimationRun();
        if (isJumping())
            setAnimationJump();
        if (isAttacking())
            setAnimationAttack();
        
        //if we were previosly jumping and are no longer, set correct animation
        if (jumping && !super.isJumping())
        {
            if (isWalking())
            {
                setAnimationWalk();
            }
            else if (isRunning())
            {
                setAnimationRun();
            }
            else
            {
                setAnimationIdle();
            }
        }
        else
        {
            //if we weren't jumping and are now, set correct animation
            if (!jumping && super.isJumping())
            {
                setAnimationJump();
            }
        }
        
        //if we are dead, we are dead!
        if (isDead())
            setAnimationDead();
        
        if (hasVictory())
            setAnimationVictory();
        
        //auto set the dimensions based on current animation
        super.setDimensions();
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        if (isInvincible())
        {
            //we will alternate between the images when invincible
            if (switchImage)
            {
                //draw same image with bitmask applied
                super.draw(graphics, invincibleImage);
            }
            else
            {
                //draw default image
                super.draw(graphics);
            }
        }
        else if (isDead())
        {
            super.draw(graphics);
        }
        else if (isHurt())
        {
            super.draw(graphics, transparentImage);
        }
        else
        {
            super.draw(graphics);
        }
        
        
        if (hasFireball())
            fireball.draw(graphics, getImage());
    }
}