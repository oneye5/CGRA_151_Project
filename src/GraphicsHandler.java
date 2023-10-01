import processing.core.PImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class GraphicsHandler
{
    HashMap<AnimationNames,Animation> Animations = new HashMap<>();
    List<Particle> particles = new ArrayList<>();
    Camera camera = new Camera();
    int sWidth,sHeight;
    Vector3 worldToScreenSpace(Vector3 in) //converts world space to screen coords
    {
       Vector3 out = in.multiply(camera.zoom).subtract(camera.camPos);
       out.y = sHeight - out.y;
       return out;
    }
    void tickGraphics() //advances all animations
    {
        for (var x : Animations.values())
            x.Tick();

        for(var x : particles)
        {
            x.Tick();
            if(x.lifeTime == 0)
                particles.remove(x);
        }
    }
    void emitParticlesPresetOne(Vector2 origin)
    {
        int amount = 3;
        int lifetime = 30;
        int minLifeTime = 15;
        float vel = 3;
        float size = 5.0f;
        float angular = 5.0f;
        float friction = 0.95f;

        for(int i = 0; i < amount; i++)
        {
            int lifeTime2 = Math.round((float)Math.random() * lifetime);
            if(lifeTime2 < minLifeTime)
                lifeTime2 = minLifeTime;

            float xVel = (float)Math.random() * vel;
            float yVel = (float)Math.random() * vel;
            float size2 = (float)Math.random() * size;
            float angular2 = (float) Math.random() * angular;
            float startAngle = (float) Math.random() * 360f;

            var obj = new Particle(
                    origin,
                    new Vector2(xVel,yVel),
                    new Vector2(size2,size2),
                    angular2,startAngle,friction,
                    new Vector3(1.0f,1.0f,1.0f),
                    lifeTime2,
                    true);

            particles.add(obj);
        }
    }
    enum AnimationNames
    {
        PLAYER_IDLE,
        PLAYER_RUN,
        PLAYER_JUMP,
        PLAYER_JUMP_END,
        PLAYER_DIE,
        BACKGROUND_L1,
        BACKGROUND_L2,
        BACKGROUND_L3,
    }
    GraphicsHandler(List<PImage> rawImages,int screenWidth,int screenHeight)
    {
        sWidth = screenWidth;
        sHeight = screenHeight;

        //load animations here
        int currentSheetIndex = 0;

        var currentSheet = rawImages.get(currentSheetIndex);
        Animation anim = new Animation(currentSheet,160,160,0,4);
        Animations.put(AnimationNames.PLAYER_IDLE,anim);

        anim = new Animation(currentSheet,160,160,1,8);
        Animations.put(AnimationNames.PLAYER_RUN,anim);

        anim = new Animation(currentSheet,160,160,1,8);
        Animations.put(AnimationNames.PLAYER_RUN,anim);

        anim = new Animation(currentSheet,160,160,2, 11);
        Animations.put(AnimationNames.PLAYER_JUMP,anim);

        anim = new Animation(currentSheet,160,160,3, 3);
        Animations.put(AnimationNames.PLAYER_JUMP_END,anim);

        anim = new Animation(currentSheet,160,160,4, 8);
        Animations.put(AnimationNames.PLAYER_DIE,anim);

        //setup backgrounds
        currentSheetIndex++; currentSheet = rawImages.get(currentSheetIndex);
        anim = new Animation(currentSheet,1280,360,0,1);
        Animations.put(AnimationNames.BACKGROUND_L1,anim);

        currentSheetIndex++; currentSheet = rawImages.get(currentSheetIndex);
        anim = new Animation(currentSheet,1280,360,0,1);
        Animations.put(AnimationNames.BACKGROUND_L2,anim);

        currentSheetIndex++; currentSheet = rawImages.get(currentSheetIndex);
        anim = new Animation(currentSheet,1280,360,0,1);
        Animations.put(AnimationNames.BACKGROUND_L3,anim);
    }
    public class Animation
    {
        Object physicsObject;
        ArrayList<PImage> frames = new ArrayList<>();
        boolean autoPlay = true;
        int current = 0;
        int timeTillNext = 1;
        int displayTime = 4; //frames per main loop iteration, eg 1 = mainFps, 2 = main/2
        PImage getCurrentImage()
        {
           return frames.get(current);
        }
        void Tick()
        {
            timeTillNext--;
            if(timeTillNext == 0)
            {
                current++;
                timeTillNext = displayTime;
            }
            if(current > frames.size() -1)
            {
                if(autoPlay)
                current = 0;
                else
                    current = frames.size()-1;
            }
        }
        void restart()
        {
            current = 0;
            timeTillNext = displayTime;
        }
        void restartWithoutRepeat()
        {
            autoPlay = false;
            current = 0;
            timeTillNext = displayTime;
        }
        Animation(PImage file,int frameWidth,int frameHeight,int row,int frameCount) //uses a colum and row format for the sprite sheet
        {
            for(int i = 0; i < frameCount;i++)
            {
                int x = i * frameWidth;
                if(x >= file.width)
                    break;
                if(x < 0)
                    break;
                int y = row * frameHeight;
                frames.add(file.get(x,y,frameWidth,frameHeight));
            }
        }

    }
    public class Camera
    {
        float zoom;
        Vector3 camPos;
        Camera()
        {
            camPos = new Vector3(0.0f,0.0f,0.0f);
            zoom = 1;
        }
    }



    public class Particle
    {
        Vector2 pos;
        Vector2 vel;
        Vector2 size;
        float angularVel;
        float angle;
        float friction;
        Vector3 color;
        float alpha;
        int startLifetime;
        int lifeTime;//frames
        Boolean fadeOut;
        Particle(Vector2 pos, Vector2 vel,Vector2 size,float angleVel,float angle,float friction, Vector3 color,int lifeTime,Boolean fadeOut)
        {
            this.pos = pos;
            this.vel = vel;
            this.size = size;
            this.angularVel = angleVel;
            this.angle = angle;
            this.friction = friction;
            this.color = color;
            this.alpha = 1;
            this.lifeTime = lifeTime;
            this.fadeOut = fadeOut;
            startLifetime = lifeTime;
        }
        void Tick()
        {
            pos = pos.add(vel);
            vel = vel.multiply(friction);
            angle += angularVel;
            angularVel *= friction;
            if(fadeOut)
            alpha = startLifetime/lifeTime;
            lifeTime--;
        }
        List<Vector2> getVertices()
        {
            Vector2 v1 = new Vector2(-0.5f,0.5f);
            Vector2 v2 = new Vector2(0.5f,0.5f);

            Vector2 v3 = new Vector2(0.5f,-0.5f);
            Vector2 v4 = new Vector2(-0.5f,-0.5f);

            //scale
            v1.x *= size.x; v1.y *= size.y;
            v2.x *= size.x; v2.y *= size.y;
            v3.x *= size.x; v3.y *= size.y;
            v4.x *= size.x; v4.y *= size.y;
            //rotate
            v1.x *= Math.sin(angle); v1.y *= Math.cos(angle);
            v2.x *= Math.sin(angle); v2.y *= Math.cos(angle);
            v3.x *= Math.sin(angle); v3.y *= Math.cos(angle);
            v4.x *= Math.sin(angle); v4.y *= Math.cos(angle);
            //translate
            v1 = v1.add(pos);
            v2 = v2.add(pos);
            v3 = v3.add(pos);
            v4 = v4.add(pos);

            List<Vector2> out = new ArrayList<>();
            out.add(v1);out.add(v2);out.add(v3);out.add(v4);
            return out;
        }
    }
}
