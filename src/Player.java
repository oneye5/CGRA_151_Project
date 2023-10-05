import processing.core.PApplet;
import processing.core.PImage;

public class Player extends PApplet
{
    boolean dirRight = true;
    boolean jumping = false;
    boolean lose = false;
    boolean won = false;
    float xVelMulti = 2.5f;
    float jumpVel = 10;
    float xVelFriction = 0.825f;
    float walljumpXvel = 14.0f;
    float airFriction = 0.96f;
    float xVelMultiAir = 0.5f;
    int dashRecoveryFrames = 120;
    int remainingRecoveryFrames = 0;
    int dashDuration = 10;
    int remainingDashDuration = dashDuration;
    float dashVel = 15.0f;
    float fastFallAccel = -0.5f;
    float fastFallThreshold = -3;
    Physics physics;
    GraphicsHandler graphics;
    Inputs input;
    Object pObj;
    GraphicsHandler.Animation anim = null;
    boolean started = false;

    Player(Physics physics, GraphicsHandler graphics,Inputs input)
    {
        this.physics = physics;
        this.graphics = graphics;
        this.pObj = physics.PHYSICS_OBJECTS.get(0);
        this.input = input;
    }
    public void tick()
    {
        if(remainingRecoveryFrames != 0)
            remainingRecoveryFrames--;
        anim = null;
        tickMovement();

        if(lose)
            anim = graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_DIE);
        if (anim == null)
            anim = graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_IDLE);


    }
    public void tickMovement()
    {
        //========================== tick controls and movement =====================================================
        if(input.up || input.down || input.left || input.right || input.dash)
            started = true;



        if(pObj.touchingObject && pObj.hitNormal.y == 1 && Math.round(pObj.VELOCITY.x ) != 0) //ground particles
            graphics.emitParticlesPresetThree(new Vector2(pObj.POS.x,pObj.POS.y - 20.0f));

        if(remainingDashDuration != 0)
        {
            anim = graphics.Animations.get( GraphicsHandler.AnimationNames.PLAYER_RUN);
            graphics.emitParticlesPresetOne(new Vector2(pObj.POS.x,pObj.POS.y));

            if(remainingDashDuration == dashDuration) //tick dash state =============================================
            {
                //set vel
                Vector2 dir = new Vector2(0.0f,0.0f);
                if(input.up)
                    dir.y = 1.0f;
                if(input.down)
                    dir.y = -1.0f;
                if(input.left)
                    dir.x = -1.0f;
                if(input.right)
                    dir.x = 1.0f;

                pObj.VELOCITY = dir.multiply(dashVel);
                pObj.ELASTICITY = 1.0f;
                pObj.GRAVITY_MULTIPLIER = 0;
            }
            remainingDashDuration--;
            if(remainingDashDuration == 0)
            {
                pObj.ELASTICITY = 0.05f;
                pObj.GRAVITY_MULTIPLIER = 1.0f;
            }
            return;
        }

        //else tick regular player state =============================================================================


            if (input.right)
            {
                if (pObj.touchingObject)
                    pObj.VELOCITY.x += xVelMulti;
                else
                    pObj.VELOCITY.x += xVelMultiAir;
                anim = graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_RUN);
                dirRight = true;
            }
            if (input.left)
            {
                if (pObj.touchingObject)
                    pObj.VELOCITY.x -= xVelMulti;
                else
                    pObj.VELOCITY.x -= xVelMultiAir;
                anim = graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_RUN);
                dirRight = false;
            }
            if (input.up && pObj.touchingObject && pObj.hitNormal.y != -1)
            {
                graphics.emitParticlesPresetTwo(new Vector2(pObj.POS.x,pObj.POS.y));
                //differentiate wall jumps from ground

                if (pObj.hitNormal.y != 0)
                {
                    pObj.VELOCITY.y = jumpVel;
                    jumping = true;
                    graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_JUMP).restartWithoutRepeat();

                }
                else
                {
                    pObj.VELOCITY.y = jumpVel;
                    if (pObj.hitNormal.x > 0)
                    {
                        pObj.VELOCITY.x = walljumpXvel;
                        dirRight = true;
                    }
                    else
                    {
                        pObj.VELOCITY.x = -walljumpXvel;
                        dirRight = false;
                    }

                    jumping = true;
                    graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_JUMP).restartWithoutRepeat();
                }

            }
            if (input.down && !pObj.touchingObject)
            {
                if(pObj.VELOCITY.y > fastFallThreshold)
                    pObj.VELOCITY.y = fastFallThreshold - 0.1f;
                else
                    pObj.VELOCITY.y += fastFallAccel;
            }
            if (input.dash && remainingRecoveryFrames == 0)
            {
                remainingDashDuration = dashDuration;
                remainingRecoveryFrames = dashRecoveryFrames;
            }


            if (pObj.touchingObject)
                pObj.VELOCITY.x *= xVelFriction;
            else
                pObj.VELOCITY.x *= airFriction;


            if (jumping) //handle jump animations
            {
                anim = graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_JUMP);
                if (anim.current == anim.frames.size() - 1 && pObj.touchingObject)
                    jumping = false;
            }

    }




}
