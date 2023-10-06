import processing.core.PApplet;
import processing.core.PImage;
import processing.event.KeyEvent;

import java.util.ArrayList;
import java.util.List;


public class Main extends PApplet
{
    Physics physics = new Physics();
    GraphicsHandler graphics;
    Inputs inputs = new Inputs();
    Player player;
    float loseSpeed = 0;
    int currentLevel = 0;
    public void settings()
    {
        noSmooth();
       size(1600,900);
      // fullScreen();
    }

    public void setup()
    {

        frameRate(60);
        rectMode(2);
        fill(125,125,255,255);

        //load graphical assets

        List<PImage> images = new ArrayList<>();
        PImage image =  loadImage("Assets/PLAYER_SHEET_1.png"); images.add(image);
        image = loadImage("Assets/Miami-synth-files/Miami-synth-files/Layers/bgSheet.png"); images.add(image);
        image = loadImage("Assets/DEATHWALLTHING.png"); images.add(image);
        image = loadImage("Assets/GOODHAPPYWALL.png"); images.add(image);
        graphics = new GraphicsHandler(images ,sketchWidth(),sketchHeight());

        loadLevelOne();

    }
    public void draw()
    {
        physics.physicsTick(0.0f);
        graphics.tickGraphics();

        player.tick();
        graphics.camera.lerpCameraTo(player.pObj.POS.subtract(new Vector3(0.0f,-200.0f,0.0f)));
        drawAll();
        tickWinLoseCondition();
    }
    void drawAll()
    {
        background(0);
        drawBackground();
        drawParticles();
        drawWorld();
        drawPlayer();
        drawWinLoseWalls();

        if(player.won)
            showWin();
        if(player.lose)
            showLose();
        if(!player.started)
            drawPreStart();
    }
    void drawWinLoseWalls()
    {
        //draw scary wall thing
        var pos = graphics.worldToScreenSpace(physics.PHYSICS_OBJECTS.get(physics.PHYSICS_OBJECTS.size()-2).POS);
        Vector2 size = new Vector2(1920.0f,1080.0f);
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.DEATH_WALL).getCurrentImage(),pos.x - size.x/1.5f,pos.y - size.y+75,size.x,size.y);
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.DEATH_WALL).getCurrentImage(),pos.x - size.x/1.5f,pos.y - size.y*2.0f+75,size.x,size.y);

        size = new Vector2(72.0f,1920.0f);
        pos = graphics.worldToScreenSpace(physics.PHYSICS_OBJECTS.get(physics.PHYSICS_OBJECTS.size()-1).POS);
        image(graphics.Animations.get(GraphicsHandler.AnimationNames.WIN_WALL).getCurrentImage(),pos.x -152,pos.y +size.y + 1160,size.x,size.y);
    }
    void drawPreStart()
    {
        fill(0,125);
        rect(-2000,-2000,4000,4000);
        textSize(40.0f);
        float x = (float)graphics.sWidth/2.0f;
        float y  = (float)graphics.sHeight/2.0f;

        fill(255);
        text("Arrow keys to move, shift to dash, backspace to retry",x,y);
    }
    void drawWorld() //draws all physics objects other than player
    {
        for(int i = 1; i< physics.PHYSICS_OBJECTS.size()-2; i++)
        {

            var obj = physics.PHYSICS_OBJECTS.get(i);
            Vector3 pos = graphics.worldToScreenSpace(obj.POS);



            if(obj.HIT_BOX_SQUARE.size() != 0)
            {
                //DRAW RECT WITH BASIC DECORATION USING PRIMITIVES
                float w = obj.HIT_BOX_SQUARE.get(0).widthHeight.x;
                float h = obj.HIT_BOX_SQUARE.get(0).widthHeight.y;

                stroke(0, 0, 0,0); //transparent base
                fill(0,0,0,200);
                rect(pos.x,pos.y,w,h);

                fill(0,0,0,0);
                strokeWeight(10.0f);
                stroke(11, 11, 21);
                rect(pos.x,pos.y,w-25,h-25);

                fill(0,0,0,0);
                strokeWeight(5.0f);
                stroke(21, 21, 31);
                rect(pos.x,pos.y,w-20,h-20);

                strokeWeight(10.0f);
                stroke(33, 33, 56);
                rect(pos.x,pos.y,w-15,h-15);

                strokeWeight(10.0f);
                stroke(40, 42, 94);
                rect(pos.x,pos.y,w-10,h-10);

                strokeWeight(5.0f);
                stroke(38, 44, 158);
                rect(pos.x,pos.y,w-5,h-5);

                strokeWeight(3.0f);
                stroke(142, 28, 255);
                rect(pos.x,pos.y,w-2,h-2);


                stroke(0, 0, 0); //outline
                strokeWeight(1.0f);
                fill(0,0,0,0);
                rect(pos.x,pos.y,w,h);
            }





            if(obj.HIT_BOX_CIRCLE.size() != 0)
            {
                circle(pos.x,pos.y,obj.HIT_BOX_CIRCLE.get(0).RADIUS);
            }
        }
    }
    void drawBackground() //tiles, calculates parallax and renders all 3 background layers
    {
        Vector3 pos,relative; float paralaxValue;int x ;
        float sizeX;
        float sizeY;
        var anim = graphics.Animations.get(GraphicsHandler.AnimationNames.BACKGROUND_LAYERS);
        anim.restartWithoutRepeat();
        sizeX = 896*4.5f;
        sizeY = 240*4.5f;
        float adjust = 1.0f;
        float yShift; float yAdjust = -0f;


        yAdjust = -100f;
        yShift = 0.05f;
        paralaxValue = 0.3f;
        pos = new Vector3(graphics.camera.camPos.x *-1,(graphics.camera.camPos.y * yShift) + yAdjust,0.0f);
        x = Math.round(graphics.camera.camPos.x/(sizeX / paralaxValue));
        pos.x += x*sizeX/paralaxValue;
        pos.x *= paralaxValue;
        image(anim.getCurrentImage(),pos.x,pos.y,sizeX,sizeY);
        image(anim.getCurrentImage(),pos.x-(sizeX/adjust),pos.y,sizeX,sizeY);
        image(anim.getCurrentImage(),pos.x+(sizeX/adjust),pos.y,sizeX,sizeY);
        anim.next();

        yAdjust = -200f;
        yShift = 0.5f;
        paralaxValue = 0.7f;
        pos = new Vector3(graphics.camera.camPos.x *-1,(graphics.camera.camPos.y * yShift) + yAdjust,0.0f);
        x = Math.round(graphics.camera.camPos.x/(sizeX / paralaxValue));
        pos.x += x*sizeX/paralaxValue;
        pos.x *= paralaxValue;
        image(anim.getCurrentImage(),pos.x,pos.y,sizeX,sizeY);
        image(anim.getCurrentImage(),pos.x-(sizeX/adjust),pos.y,sizeX,sizeY);
        image(anim.getCurrentImage(),pos.x+(sizeX/adjust),pos.y,sizeX,sizeY);
        anim.next();

        yAdjust = -150f;
        yShift = 0.7f;
        paralaxValue = 0.8f;
        pos = new Vector3(graphics.camera.camPos.x *-1,(graphics.camera.camPos.y * yShift) + yAdjust,0.0f);
        x = Math.round(graphics.camera.camPos.x/(sizeX / paralaxValue));
        pos.x += x*sizeX/paralaxValue;
        pos.x *= paralaxValue;
        image(anim.getCurrentImage(),pos.x,pos.y,sizeX,sizeY);
        image(anim.getCurrentImage(),pos.x-(sizeX/adjust),pos.y,sizeX,sizeY);
        image(anim.getCurrentImage(),pos.x+(sizeX/adjust),pos.y,sizeX,sizeY);
        anim.next();

        yAdjust = -100f;
        yShift = 0.9f;
        paralaxValue = 0.9f;
        pos = new Vector3(graphics.camera.camPos.x *-1,(graphics.camera.camPos.y * yShift) + yAdjust,0.0f);
        x = Math.round(graphics.camera.camPos.x/(sizeX / paralaxValue));
        pos.x += x*sizeX/paralaxValue;
        pos.x *= paralaxValue;
        image(anim.getCurrentImage(),pos.x,pos.y,sizeX,sizeY);
        image(anim.getCurrentImage(),pos.x-(sizeX/adjust),pos.y,sizeX,sizeY);
        image(anim.getCurrentImage(),pos.x+(sizeX/adjust),pos.y,sizeX,sizeY);
        anim.next();


    }
    void drawPlayer()
    {
        Vector3 screenPos = graphics.worldToScreenSpace(player.pObj.POS).add(new Vector3(0.0f,-20.0f,0.0f));
        if(player.dirRight)
        {
            image(player.anim.getCurrentImage(),screenPos.x - 125 ,screenPos.y - 125,250,250);
        }
        else
        {//flip the image
            pushMatrix();
            translate((screenPos.x - 125), (screenPos.y - 125));
            scale(-1.0f, 1.0f);
            translate(-(screenPos.x + 125), -(screenPos.y - 125));

            image(player.anim.getCurrentImage(), screenPos.x - 125, screenPos.y - 125, 250, 250);
            popMatrix();
        }
    }
    void drawParticles()
    {
        for(var x: graphics.particles)
        {
            int r,g,b,a;
            r = Math.round(x.color.x*255.0f);   g = Math.round(x.color.y*255.0f);   b = Math.round(x.color.z*255.0f);   a = Math.round(x.alpha*255.0f);
            fill(r,g,b,a);
            var vertices = x.getVertices();
            stroke(0,0,0,0);
            beginShape();
            for(var y : vertices)
            {
                var screenPos = graphics.worldToScreenSpace(new Vector3(y.x,y.y,0.0f));
                vertex(screenPos.x, screenPos.y);
            }
            endShape();
        }
    }
    void tickWinLoseCondition()
    {
        if(!player.started)
            return;

        //================================= check win lose conditions ==================================================
        var pObj = physics.PHYSICS_OBJECTS.get(0);
        if(physics.PHYSICS_OBJECTS.get(physics.PHYSICS_OBJECTS.size()-1).touchingObject && !player.lose)
        {
            player.won = true;
            pObj.VELOCITY.x = 0;
        }
        if(physics.PHYSICS_OBJECTS.get(physics.PHYSICS_OBJECTS.size()-2).touchingObject && !player.won)
        {
            player.lose = true;
            graphics.Animations.get(GraphicsHandler.AnimationNames.PLAYER_DIE).restartWithoutRepeat();
        }

        var obj = physics.PHYSICS_OBJECTS.get(physics.PHYSICS_OBJECTS.size()-2);
        if(!player.lose && !player.won)
        obj.POS.x += loseSpeed;

        if(inputs.backspace)
        {
            if (currentLevel == 0)
                loadLevelOne();
            else
                loadLevelTwo();
        }
    }

    void showWin()
    {
        textSize(30); fill(255,255,255);
        text("YOU WIN!\nPress space to go to level 2, press backspace to go to level 1",sketchWidth()/2.0f,sketchHeight()/2.0f);
    }
    void showLose()
    {
        textSize(30); fill(255,255,255);
        text("YOU LOSE!\nPress space to retry, or press backspace to go to level one",sketchWidth()/2.0f,sketchHeight()/2.0f);
        return;
    }
    public static void main(String[] args)
    {
        PApplet.main("Main");
    }

    public void keyPressed(KeyEvent event)
    {
        inputs.keyPressed(event);
    }

    public void keyReleased(KeyEvent event)
    {
        inputs.keyReleased(event);
    }
    void loadLevelOne()
    {
        physics = new Physics();
        physics.WORLD_GRAVITY = -0.3f;
        loseSpeed = 4.55f;

        Object obj;

        //player
        obj = physics.CreateObject(new Vector3(0.0f,50.0f,0f),100.0f); obj.ELASTICITY = 0.05f;

        //now add ground
        obj = physics.CreateObject(new Vector3(0.0f,-1000.0f,0f),new Vector2(10000.0f,1000.0f)); obj.TYPE = Type.Static;//b

        //now obstacles
        obj = physics.CreateObject(new Vector3(1000.0f,100.0f,0f),new Vector2(200.0f,100.0f)); obj.TYPE = Type.Static;
        obj = physics.CreateObject(new Vector3(1400.0f,200.0f,0f),new Vector2(200.0f,200.0f)); obj.TYPE = Type.Static;

        obj = physics.CreateObject(new Vector3(1800.0f,600.0f,0f),new Vector2(200.0f,600.0f)); obj.TYPE = Type.Static;
        obj = physics.CreateObject(new Vector3(2600.0f,1200.0f,0f),new Vector2(100.0f,1000.0f)); obj.TYPE = Type.Static;

        obj = physics.CreateObject(new Vector3(3200.0f,600.0f,0f),new Vector2(200.0f,600.0f)); obj.TYPE = Type.Static;

        //lose condition, seccond to last index
        obj = physics.CreateObject(new Vector3(-500.0f,15.0f,0f),new Vector2(100.0f,5000.0f)); obj.TYPE = Type.Static;
        //WIN CONDITION OBJECT, ALLWAYS LAST INDEX
        obj = physics.CreateObject(new Vector3(4000.0f,5000.0f,0f),new Vector2(100.0f,5000.0f)); obj.TYPE = Type.Static;

        player = new Player(physics,graphics,inputs);
        player.won = false;
        player.lose = false;
        player.started = false;
    }
    void loadLevelTwo()
    {
        physics = new Physics();
        physics.WORLD_GRAVITY = -0.3f;

        loseSpeed = 5.55f;

        Object obj;

        //player
        obj = physics.CreateObject(new Vector3(0.0f,50.0f,0f),100.0f); obj.ELASTICITY = 0.05f;

        //now add ground
        obj = physics.CreateObject(new Vector3(0.0f,0.0f,0f),new Vector2(10000.0f,1.0f)); obj.TYPE = Type.Static;//b

        //now obstacles
        obj = physics.CreateObject(new Vector3(1000.0f,100.0f,0f),new Vector2(200.0f,100.0f)); obj.TYPE = Type.Static;
        obj = physics.CreateObject(new Vector3(1400.0f,200.0f,0f),new Vector2(200.0f,200.0f)); obj.TYPE = Type.Static;

        obj = physics.CreateObject(new Vector3(1800.0f,600.0f,0f),new Vector2(200.0f,600.0f)); obj.TYPE = Type.Static;
        obj = physics.CreateObject(new Vector3(2600.0f,1200.0f,0f),new Vector2(100.0f,1000.0f)); obj.TYPE = Type.Static;

        obj = physics.CreateObject(new Vector3(3200.0f,600.0f,0f),new Vector2(200.0f,600.0f)); obj.TYPE = Type.Static;

        //lose condition, seccond to last index
        obj = physics.CreateObject(new Vector3(-500.0f,15.0f,0f),new Vector2(100.0f,5000.0f)); obj.TYPE = Type.Static;
        //WIN CONDITION OBJECT, ALLWAYS LAST INDEX
        obj = physics.CreateObject(new Vector3(4000.0f,5000.0f,0f),new Vector2(100.0f,5000.0f)); obj.TYPE = Type.Static;

        player = new Player(physics,graphics,inputs);
        player.won = false;
        player.lose = false;
        player.started = false;
    }
}
