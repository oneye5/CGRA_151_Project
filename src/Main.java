import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class Main extends PApplet
{
    Physics physics = new Physics();
    GraphicsHandler graphics;

    Instant startTime;


    public void settings()
    {
        fullScreen();
    }

    public void setup()
    {
        physics.WORLD_GRAVITY = -0.3f;
        frameRate(60);
        rectMode(2);
        fill(125,125,255,255);

        Object obj;

        //player
        obj = physics.CreateObject(new Vector3(0.0f,50.0f,0f),100.0f); obj.ELASTICITY = 0.05f;

        //now add ground
        obj = physics.CreateObject(new Vector3(0.0f,0.0f,0f),new Vector2(10000.0f,1.0f)); obj.TYPE = Type.Static;//b

        //now obstacles
        obj = physics.CreateObject(new Vector3(1000.0f,100.0f,0f),new Vector2(200.0f,100.0f)); obj.TYPE = Type.Static;
        obj = physics.CreateObject(new Vector3(1400.0f,200.0f,0f),new Vector2(200.0f,200.0f)); obj.TYPE = Type.Static;

        obj = physics.CreateObject(new Vector3(1800.0f,600.0f,0f),new Vector2(200.0f,600.0f)); obj.TYPE = Type.Static;

        //lose condition, seccond to last index
        obj = physics.CreateObject(new Vector3(-400.0f,15.0f,0f),new Vector2(100.0f,5000.0f)); obj.TYPE = Type.Static;
        //WIN CONDITION OBJECT, ALLWAYS LAST INDEX
        obj = physics.CreateObject(new Vector3(4000.0f,15.0f,0f),new Vector2(100.0f,500.0f)); obj.TYPE = Type.Static;

        //load graphical assets
        List<PImage> images = new ArrayList<>();
        PImage image =  loadImage("Assets/PLAYER_SHEET_1.png"); images.add(image);

        //backgrounds
        image =  loadImage("Assets/SideScroller/background/bg_layer_1.png"); images.add(image);
        image =  loadImage("Assets/SideScroller/background/bg_layer_2.png"); images.add(image);
        image =  loadImage("Assets/SideScroller/background/bg_layer_3.png"); images.add(image);


        graphics = new GraphicsHandler(images ,sketchWidth(),sketchHeight());

        delay(1000);
        startTime = Instant.now();
    }
    public void draw()
    {
        physics.physicsTick(0.0f);
        graphics.tickAnimations();

        background(0);
        //tickBackground();
        playerTick();
        tickLoseCondition();
        tickCamera();
        renderHitboxes();
    }
    float loseSpeed = 0.5f;
    void tickLoseCondition()
    {
        var obj = physics.PHYSICS_OBJECTS.get(physics.PHYSICS_OBJECTS.size()-2);
        obj.POS.x += loseSpeed;
    }
    void tickBackground() //tiles, calculates parallax and renders all 3 background layers
    {
        Vector3 pos,relative; float paralaxValue;int x ;
        float sizeX = 1280;
        float sizeY = 360;

        paralaxValue = 0.3f;
        pos = new Vector3(graphics.camera.camPos.x *-1,0.0f,0.0f);
        x = Math.round(graphics.camera.camPos.x/(sizeX / paralaxValue));
        pos.x += x*sizeX/paralaxValue;
        pos.x *= paralaxValue;
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.BACKGROUND_L3).getCurrentImage(),pos.x,pos.y,sizeX,sizeY);
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.BACKGROUND_L3).getCurrentImage(),pos.x-sizeX,pos.y,sizeX,sizeY);
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.BACKGROUND_L3).getCurrentImage(),pos.x+sizeX,pos.y,sizeX,sizeY);

        /*
        paralaxValue = 0.5f;
        pos = new Vector3(graphics.camera.camPos.x *-1,0.0f,0.0f);
        x = Math.round(graphics.camera.camPos.x/(sizeX / paralaxValue));
        pos.x += x*sizeX/paralaxValue;
        pos.x *= paralaxValue;
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.BACKGROUND_L2).getCurrentImage(),pos.x,pos.y,sizeX,sizeY);
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.BACKGROUND_L2).getCurrentImage(),pos.x-sizeX,pos.y,sizeX,sizeY);
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.BACKGROUND_L2).getCurrentImage(),pos.x+sizeX,pos.y,sizeX,sizeY);



        paralaxValue = 0.8f;
        pos = new Vector3(graphics.camera.camPos.x *-1,0.0f,0.0f);
        x = Math.round(graphics.camera.camPos.x/(sizeX / paralaxValue));
        pos.x += x*sizeX/paralaxValue;
        pos.x *= paralaxValue;
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.BACKGROUND_L1).getCurrentImage(),pos.x,pos.y,sizeX,sizeY);
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.BACKGROUND_L1).getCurrentImage(),pos.x-sizeX,pos.y,sizeX,sizeY);
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.BACKGROUND_L1).getCurrentImage(),pos.x+sizeX,pos.y,sizeX,sizeY);

         */
    }
    //player vars
    boolean dirRight = true;
    boolean jumping = false;
    boolean dead = false;
    PImage obstacle;
    boolean won = false;
    Boolean left = false,right = false,up = false,down = false,dash = false;
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

    //region input
    public void keyPressed(KeyEvent event)
    {
        switch (event.getKeyCode())
        {
            case UP:
                up = true;
                break;
            case DOWN:
                down = true;
                break;
            case RIGHT:
                right = true;
                break;
            case LEFT:
                left = true;
                break;
            case SHIFT:
                dash = true;
                break;
        }
    }
    public void keyReleased(KeyEvent event)
    {
        switch (event.getKeyCode())
        {
            case UP:
                up = false;
                break;
            case DOWN:
                down = false;
                break;
            case RIGHT:
                right = false;
                break;
            case LEFT:
                left = false;
                break;
            case SHIFT:
                dash = false;
                break;
        }
    }
    //endregion
    void playerTick()//this method handles player behaviour and visuals
    {
        if(remainingRecoveryFrames != 0)
            remainingRecoveryFrames--;

        var pObj = physics.PHYSICS_OBJECTS.get(0);
        GraphicsHandler.Animation anim = null;

        if(remainingDashDuration != 0 && !won && !dead)
        {

            anim = graphics.Animations.get( GraphicsHandler.AnimationNames.PLAYER_IDLE);
            if(remainingDashDuration == dashDuration)
            {
                //set vel
                Vector2 dir = new Vector2(0.0f,0.0f);
                if(up)
                    dir.y = 1.0f;
                if(down)
                    dir.y = -1.0f;
                if(left)
                    dir.x = -1.0f;
                if(right)
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
        }
        else if (!won && !dead)
        {

            if (right) {
                if (pObj.touchingObject)
                    pObj.VELOCITY.x += xVelMulti;
                else
                    pObj.VELOCITY.x += xVelMultiAir;
                anim = graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_RUN);
                dirRight = true;
            }
            if (left) {
                if (pObj.touchingObject)
                    pObj.VELOCITY.x -= xVelMulti;
                else
                    pObj.VELOCITY.x -= xVelMultiAir;
                anim = graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_RUN);
                dirRight = false;
            }
            if (up && pObj.touchingObject) {
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
            if (down && !pObj.touchingObject)
            {
                if(pObj.VELOCITY.y > fastFallThreshold)
                pObj.VELOCITY.y = fastFallThreshold - 0.1f;
                else
                    pObj.VELOCITY.y += fastFallAccel;
            }
            if (dash && remainingRecoveryFrames == 0)
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
        //check win lose conditions
        if(physics.PHYSICS_OBJECTS.get(physics.PHYSICS_OBJECTS.size()-1).touchingObject)
            won = true;
        if(physics.PHYSICS_OBJECTS.get(physics.PHYSICS_OBJECTS.size()-2).touchingObject && !dead)
        {
            dead = true;
            graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_DIE).restartWithoutRepeat();
        }
        if(dead)
            anim = graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_DIE);

        //now draw
        if (anim == null)
            anim = graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_IDLE);
        Vector3 screenPos = graphics.worldToScreenSpace(pObj.POS);
        if(dirRight)
        {
            image(anim.getCurrentImage(),screenPos.x - 125 ,screenPos.y - 125,250,250);
        }
        else
        {//flip the image
            pushMatrix();
            translate((screenPos.x - 125), (screenPos.y - 125));
            scale(-1.0f, 1.0f);
            translate(-(screenPos.x + 125), -(screenPos.y - 125));

            image(anim.getCurrentImage(), screenPos.x - 125, screenPos.y - 125, 250, 250);
            popMatrix();
        }


    }
    void tickCamera() //this method handles lerped camera movement
    {
        //lerp camera to player position, its nice when its smooth and bouncy :)))))))
        var player = physics.PHYSICS_OBJECTS.get(0);

        float x = lerp(graphics.camera.camPos.x,player.POS.x - graphics.sWidth/2,0.2f);
        float y = lerp(graphics.camera.camPos.y,player.POS.y - graphics.sHeight/3,0.1f);

        graphics.camera.camPos = new Vector3(x,y ,graphics.camera.camPos.z);

    }

    void renderHitboxes()
    {
        var i = physics.PHYSICS_OBJECTS.iterator();
        while(i.hasNext())
        {
            var obj = i.next();
            Vector3 pos = graphics.worldToScreenSpace(obj.POS);

            fill(0,255,0,125);
            if(obj.HIT_BOX_SQUARE.size() != 0)
            {
                rect(pos.x,pos.y,obj.HIT_BOX_SQUARE.get(0).widthHeight.x,obj.HIT_BOX_SQUARE.get(0).widthHeight.y);
            }
            if(obj.HIT_BOX_CIRCLE.size() != 0)
            {
                circle(pos.x,pos.y,obj.HIT_BOX_CIRCLE.get(0).RADIUS);
            }
        }
    }
    public static void main(String[] args)
    {
        PApplet.main("Main");
    }
}
