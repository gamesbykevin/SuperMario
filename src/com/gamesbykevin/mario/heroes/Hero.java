package com.gamesbykevin.mario.heroes;

import com.gamesbykevin.framework.resources.Disposable;
import com.gamesbykevin.framework.util.Timers;

import com.gamesbykevin.mario.character.Character;
import com.gamesbykevin.mario.effects.Effects;
import com.gamesbykevin.mario.engine.Engine;
import com.gamesbykevin.mario.resources.GameAudio;
import com.gamesbykevin.mario.shared.IElement;
import com.gamesbykevin.mario.world.level.Level;
import com.gamesbykevin.mario.world.level.tiles.Tile;
import com.gamesbykevin.mario.world.level.tiles.Tiles;
import com.gamesbykevin.mario.world.World;
import com.gamesbykevin.mario.world.level.powerups.PowerUps;

import java.awt.image.BufferedImage;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Random;

public abstract class Hero extends Character implements IElement, Disposable
{
    //by default the hero isn't big
    private boolean big = false;
    
    //by default the hero doesn't have fire
    private boolean fire = false;
    
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
    
    private static final double SCROLL_WEST_RATIO = .5;
    private static final double SCROLL_EAST_RATIO = .5;
    
    //how long to wait until switching to other image
    private static final long INVINCIBLE_IMAGE_SWITCH_DELAY = Timers.toNanoSeconds(100L);
    
    //how long the hero will be invincible for
    private static final long INVINCIBLE_DELAY = Timers.toNanoSeconds(10000L);
    
    //how long will mario be hurt before receiving damage again
    private static final long HURT_DELAY = Timers.toNanoSeconds(3000L);
    
    //how to pause the hero for victory
    private static final long VICTORY_DELAY = Timers.toNanoSeconds(4000L);
    
    //how to pause for hero death
    private static final long DEATH_DELAY = Timers.toNanoSeconds(4750L);
    
    //how many coins are required for a new life
    private static final int COINS_PER_LIFE = 100;
    
    //most lives the hero can have
    private static final int MAX_LIVES = 99;
    
    //dead over screen
    private Image image;
    
    private enum TimerKey
    {
        ImageSwitch, InvincibilityDuration, Hurt, Victory, Dead
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
    
    protected Hero(final long time)
    {
        super(Character.DEFAULT_JUMP_VELOCITY, Character.DEFAULT_SPEED_RUN, Character.DEFAULT_SPEED_RUN);
        
        //setup timers
        setupTimers(time);
        
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
    
    public void setGameOverImage(final Image image)
    {
        this.image = image;
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
        
        //if we have fire we are also big
        if (hasFire())
            setBig(true);
        
        //if not fire, remove any existing projectiles
        if (!hasFire())
            getProjectiles().clear();
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
        this.timers.add(TimerKey.Victory, VICTORY_DELAY);
        this.timers.add(TimerKey.Dead, DEATH_DELAY);
        
        try
        {
            //make sure all timers are setup
            for (int i = 0; i < TimerKey.values().length; i++)
            {
                if (timers.getTimer(TimerKey.values()[i]) == null)
                    throw new Exception("Timer not setup here: " + TimerKey.values()[i].toString());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
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
        
        //if the hero has finished the level
        if (hasVictory())
        {
            //if time has passed reset state(s)
            if (timers.hasTimePassed(TimerKey.Victory))
                setMiniMapDefaults();
            
            //update timer
            timers.update(TimerKey.Victory);
        }
        
        if (isDead())
        {
            if (!timers.hasTimePassed(TimerKey.Dead))
            {
                //update timer
                timers.update(TimerKey.Dead);
            }
        }
    }
    
    private void setMiniMapDefaults()
    {
        setVictory(false);
        setInvincible(false);
        setHurt(false);
        setWalk(false);
        setRun(false);
        setIdle(true);
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
    
    public boolean hasLives()
    {
        return (getLives() > 0);
    }
    
    public void addCoin()
    {
        this.coin++;
        
        if (this.coin >= COINS_PER_LIFE)
        {
            //reset coins back to 0
            this.coin = 0;
            this.setLives(this.getLives() + 1);
            
            //set sound to play
            setAudioKey(GameAudio.Keys.SfxLevelExtraLife);
        }
    }
    
    public int getCoin()
    {
        return this.coin;
    }
    
    @Override
    public void update(final Engine engine)
    {
        //if we are to play the death sound
        if (super.getAudioKey() == GameAudio.Keys.SfxLevelDie)
        {
            //stop all other sound
            engine.getResources().stopAllSound();
        }
        
        //update parent entity
        super.update(engine);
        
        //if we are displaying the mini-map, don't continue
        if (engine.getManager().getWorld().getMap().isDisplayed())
            return;
        
        //if the user is playing a game don't continue
        if (engine.getManager().getWorld().isPlayingGame())
            return;
        
        //get the current level
        final Level level = engine.getManager().getWorld().getLevels().getLevel();
        
        final boolean invincible = super.isInvincible();
        
        //update our timers
        updateTimers();
        
        //if we were invincible and are no longer
        if (invincible && !super.isInvincible())
        {
            //stop all sound
            engine.getResources().stopAllSound();
            
            //play level sound again
            engine.getManager().getWorld().getLevels().getLevel().assignBackgroundMusic();
        }
        
        //are we currently jumping/falling
        this.jumping = super.isJumping();
        
        //apply gravity
        applyGravity(level.getTiles());
        
        if (!isDead())
        {
            //check the heroes collision with the level
            checkLevelCollision(engine);
            
            //if the timer has finished mark the hero dead
            if (level.hasTimePassed())
                markDead();
            
            //if the hero is off the screen, we are dead
            if (getY() > engine.getManager().getWindow().y + engine.getManager().getWindow().height)
                markDead();
            
            //manage power up collision, and return type (if any)
            final PowerUps.Type type = level.getPowerUps().manageHeroCollision(level, this);
            
            //if are invincible and now are
            if (type == PowerUps.Type.Star)
            {
                //stop all sound
                engine.getResources().stopAllSound();

                //play invincible music
                engine.getManager().getWorld().getLevels().getLevel().setAudioKey(GameAudio.getInvincibleMusic(engine.getRandom()));
            }
            
        }

        if (isDead())
        {
            //if the hero is dead and off the screen, reset and go back to the display map
            if (getY() > engine.getManager().getWindow().y + engine.getManager().getWindow().height)
            {
                //check if time passed
                if (timers.hasTimePassed(TimerKey.Dead))
                {
                    //reset all 
                    reset();

                    //reset all keyboard input
                    engine.getKeyboard().reset();

                    //switch back to mini-map
                    switchBack(engine.getManager().getWorld());
                    
                    //lose 1 life
                    setLives(getLives() - 1);
                    
                    //if no lives left
                    if (!hasLives())
                    {
                        //play game over sound effect
                        engine.getResources().playGameAudio(GameAudio.getGameOverMusic(engine.getRandom()));
                    }
                }
                
                //don't continue
                return;
            }
        }
        
        //make sure correct animation is set
        checkAnimation();
        
        if (hasVictory())
        {
            //prevent from moving
            resetVelocity();
            
            //pause for a short time before moving back to mini-map
            if (timers.hasTimePassed(TimerKey.Victory))
            {
                setMiniMapDefaults();
                
                //flag complete
                engine.getManager().getWorld().getMap().markLevelComplete();

                //switch back to mini-map
                switchBack(engine.getManager().getWorld());
            }
            
            //don't continue
            return;
        }
        
        //check if we are to scroll the level
        checkScroll(level, engine.getManager().getWindow());
        
        //update the projectiles
        updateProjectiles(engine);
        
        //check if hero has been hurt
        if (hasDamageCheck())
        {
            markHurt();
            unflagDamage();
        }
    }
    
    public void switchBack(final World world)
    {
        setMiniMapDefaults();
        
        //set world options
        world.getMap().setDisplayed(true);
        world.getMatching().setDisplayed(false);
        world.getSlot().setDisplayed(false);
        world.setStart(this);
        
        //remove any existing projectiles the hero may have
        getProjectiles().clear();
    }
    
    /**
     * Set the character defaults
     */
    public void reset()
    {
        //reset all timers
        timers.reset();
        
        this.setIdle(true);
        this.setDead(false);
        this.setHurt(false);
        this.unflagDamage();
        this.setBig(false);
        this.setFire(false);
        this.setInvincible(false);
        this.setDuck(false);
        this.setAttack(false);
        this.setRun(false);
        this.setWalk(false);
        this.setJump(false);
        this.setVictory(false);
        this.setAnimation(AnimationHelper.getDefaultAnimation(this), true);
        this.resetVelocity();
        super.getProjectiles().clear();
    }
    
    private void checkLevelCollision(final Engine engine)
    {
        final Random random = engine.getRandom();
        
        //get the current level
        final Level level = engine.getManager().getWorld().getLevels().getLevel();
        
        //get the tiles in the level
        Tiles tiles = level.getTiles();
        
        if (hasVelocityY())
        {
            Tile south = checkCollisionSouth(tiles);

            //if we hit a tile at our feet, make sure to stop
            if (south != null)
            {
                if (super.isJumping())
                    super.stopJumping();

                //we hit the goal so the level is complete
                if (south.getType() == Tiles.Type.Goal)
                {
                    //stop all sound
                    engine.getResources().stopAllSound();
                    
                    //play level complete music
                    engine.getResources().playGameAudio(GameAudio.getLevelCompleteMusic(random));
                    
                    //set as complete
                    tiles.add(Tiles.Type.GoalComplete, south.getCol(), south.getRow(), south.getX(), south.getY());

                    //mark level as complete
                    level.markComplete();

                    //stop the level timer
                    level.pauseTimer();

                    //don't scroll the level
                    level.setScrollX(SPEED_NONE);

                    //stop moving
                    resetVelocity();

                    //set animation
                    setVictory(true);

                    //reset timer
                    timers.getTimer(TimerKey.Victory).reset();
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
                            //set sound to play
                            super.setAudioKey((random.nextBoolean()) ? GameAudio.Keys.SfxLevelBreakableBrick1 : GameAudio.Keys.SfxLevelBreakableBrick2);
                            
                            //remove tile
                            tiles.remove((int)north.getCol(), (int)north.getRow());

                            //need to add animation effect of brick breaking here
                            level.getEffects().add(north, Effects.Type.BreakBrick);
                        }
                        else
                        {
                            //set sound to play
                            super.setAudioKey((random.nextBoolean()) ? GameAudio.Keys.SfxLevelBump1 : GameAudio.Keys.SfxLevelBump2);
                        }
                    }
                    break;
                    
                case UsedBlock:
                    //set sound to play
                    super.setAudioKey((random.nextBoolean()) ? GameAudio.Keys.SfxLevelBump1 : GameAudio.Keys.SfxLevelBump2);
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
                //set sound to play
                super.setAudioKey(GameAudio.Keys.SfxLevelPowerDown);
                
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
        //set sound to play
        super.setAudioKey(GameAudio.Keys.SfxLevelDie);
        
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
        {
            level.setScrollX(Character.SPEED_NONE);
        }
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
        
        //get original bottom y-coordinate
        final double y = getY() + getHeight();
        
        //auto set the dimensions based on current animation
        super.setDimensions();
        
        //correct y-coordinate
        setY(y - getHeight());
    }
    
    @Override
    public void render(final Graphics graphics)
    {
        if (!hasLives())
        {
            //if no lives draw game over screen
            graphics.drawImage(image, 0, 0, null);
        }
        else
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
            super.renderProjectiles(graphics, null);
        }
    }
        
}