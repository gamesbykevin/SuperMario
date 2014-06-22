package com.gamesbykevin.mario.heroes;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.level.Level;
import com.gamesbykevin.mario.level.tiles.*;
import com.gamesbykevin.mario.shared.IElement;

import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

public abstract class Hero extends Character implements IElement, Disposable 
{
    //by default the hero isn't big
    private boolean big = true;
    
    //by default the hero doesn't have fire
    private boolean fire = true;
    
    //store if we are jumping
    private boolean jumping = false;
    
    //has the hero solved the puzzle
    private boolean victory = false;
    
    //how many coins does the hero have
    private int coin = 0;
    
    //the number of lives the hero has
    private int lives = 5;
    
    //our invincible image
    private BufferedImage invincibleImage;
    
    //our transparent image
    private BufferedImage transparentImage;
    
    private static final double SCROLL_WEST_RATIO = .25;
    private static final double SCROLL_EAST_RATIO = .65;
    
    //how long to wait until switching to other image
    private static final long INVINCIBLE_IMAGE_SWITCH_DELAY = Timers.toNanoSeconds(100L);
    
    //how long the hero will be invincible for
    private static final long INVINCIBLE_DELAY = Timers.toNanoSeconds(10000L);
    
    //how long will mario be hurt before receiving damage again
    private static final long HURT_DELAY = Timers.toNanoSeconds(5000L);
    
    //how many coins are required for a new life
    private static final int COINS_PER_LIFE = 100;
    
    private static final int MAX_LIVES = 99;
    
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
        
        //setup animations
        defineAnimations();
    }
    
    @Override
    public void dispose()
    {
        super.dispose();
        
        if (timers != null)
        {
            timers.dispose();
            timers = null;
        }
        
        if (invincibleImage != null)
        {
            invincibleImage.flush();
            invincibleImage = null;
        }
        
        if (transparentImage != null)
        {
            transparentImage.flush();
            transparentImage = null;
        }
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
    
    private void setupTimers(final long time)
    {
        this.timers = new Timers(time);
        this.timers.add(TimerKey.ImageSwitch, INVINCIBLE_IMAGE_SWITCH_DELAY);
        this.timers.add(TimerKey.InvincibilityDuration, INVINCIBLE_DELAY);
        this.timers.add(TimerKey.Hurt, HURT_DELAY);
        this.timers.reset();
    }
    
    public Timers getTimers()
    {
        return this.timers;
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
    
    public void setLives(final int lives)
    {
        this.lives = lives;
        
        //limit the number of lives
        if (getLives() > MAX_LIVES)
            setLives(MAX_LIVES);
    }
    
    public int getLives()
    {
        return this.lives;
    }
    
    public void addCoin()
    {
        this.coin++;
        
        if (this.coin >= COINS_PER_LIFE)
        {
            //reset coins back to 0
            this.coin = 0;
            this.setLives(this.getLives() + 1);
        }
    }
    
    public int getCoin()
    {
        return this.coin;
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
        
        if (!isDead())
        {
            //check the heroes collision with the level
            checkLevelCollision(engine.getManager().getLevel(), engine.getRandom());

            //if the hero is off the screen, we are dead
            if (getY() > engine.getManager().getWindow().y + engine.getManager().getWindow().height)
                markDead();
        
            //manage power up collision
            engine.getManager().getLevel().getPowerUps().manageHeroCollision(engine.getManager().getLevel(), this);
        }
        
        //make sure correct animation is set
        checkAnimation();
        
        //check if we are to scroll the level
        checkScroll(engine.getManager().getLevel(), engine.getManager().getWindow());
        
        //update the projectiles
        updateProjectiles(engine.getMain().getTime(), engine.getManager().getLevel());
        
        //check if hero has been hurt
        if (hasDamageCheck())
        {
            markHurt();
            unflagDamage();
        }
    }
    
    private void checkLevelCollision(final Level level, final Random random)
    {
        Tiles tiles = level.getTiles();
        
        Tile south = checkCollisionSouth(tiles);

        //if we hit a tile at our feet, make sure to stop
        if (south != null)
        {
            if (super.isJumping())
                super.stopJumping();

            if (south.getType() == Tiles.Type.Goal)
            {
                //set as complete
                tiles.add(Tiles.Type.GoalComplete, south.getCol(), south.getRow(), south.getX(), south.getY());

                //mark level as complete
                level.setComplete(true);

                //stop the level timer
                level.pauseTimer();
                
                //stop moving
                resetVelocity();
                
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
        
        //check for collision to the north first
        Tile north = checkCollisionNorth(tiles);
        
        if (north != null)
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
                    level.getPowerUps().managePowerupBlock(random, north, level, this);
                    break;

                case BreakableBrick:

                    //at random choose if a hidden power up is here
                    if (north.isPowerup())
                    {
                        level.getPowerUps().managePowerupBlock(random, north, level, this);
                    }
                    else
                    {
                        //can only break a brick if big or has fire
                        if (isBig() || this.hasFire())
                        {
                            //remove tile
                            tiles.remove((int)north.getCol(), (int)north.getRow());

                            //need to add animation effect of brick breaking here
                            level.getEffects().add(north, Effects.Type.BreakBrick);
                        }
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
        
        //jump off the screen
        startJump();
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
    protected void checkAnimation()
    {
        //default animation is idle
        AnimationHelper.setAnimationIdle(this);
        
        if (isDucking())
            AnimationHelper.setAnimationDuck(this);
        if (isWalking())
            AnimationHelper.setAnimationWalk(this);
        if (isRunning())
            AnimationHelper.setAnimationRun(this);
        if (isJumping())
            AnimationHelper.setAnimationJump(this);
        if (isAttacking())
            AnimationHelper.setAnimationAttack(this);
        
        //if we were previosly jumping and are no longer, set correct animation
        if (jumping && !super.isJumping())
        {
            if (isWalking())
            {
                AnimationHelper.setAnimationWalk(this);
            }
            else if (isRunning())
            {
                AnimationHelper.setAnimationRun(this);
            }
            else
            {
                AnimationHelper.setAnimationIdle(this);
            }
        }
        else
        {
            //if we weren't jumping and are now, set correct animation
            if (!jumping && super.isJumping())
            {
                AnimationHelper.setAnimationJump(this);
            }
        }
        
        //if we are dead, we are dead!
        if (isDead())
            AnimationHelper.setAnimationDead(this);
        
        if (hasVictory())
            AnimationHelper.setAnimationVictory(this);
        
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
        
        //draw projectiles
        super.renderProjectiles(graphics);
    }
}