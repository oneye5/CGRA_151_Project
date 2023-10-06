import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static processing.core.PApplet.lerp;

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

        for(int i = 0; i < particles.size();i++)
        {
            var x = particles.get(i);
            x.Tick();
            if(x.lifeTime == 0)
                particles.remove(x);
        }
    }
    void emitParticlesPresetOne(Vector2 origin) //constant stream, made for dashes
    {
        int amount = 3;
        int lifetime = 30;
        int minLifeTime = 15;
        float vel = 7;
        float size = 15.0f;
        float angular = 0.20f;
        float friction = 0.925f;

        for(int i = 0; i < amount; i++)
        {
            int lifeTime2 = Math.round((float)Math.random() * lifetime);
            if(lifeTime2 < minLifeTime)
                lifeTime2 = minLifeTime;

            float xVel = ((float)Math.random()-0.5f) * vel * 2.0f;
            float yVel = ((float)Math.random()-0.5f) * vel * 2.0f;
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
                    true,
                    true);

            particles.add(obj);
        }
    }
    void emitParticlesPresetTwo(Vector2 origin) //short burst, made for jumps
    {
        int amount = 40;
        int lifetime = 15;
        int minLifeTime = 5;
        float vel = 20;
        float size = 14.0f;
        float angular = 0.20f;
        float friction = 0.75f;

        for(int i = 0; i < amount; i++)
        {
            int lifeTime2 = Math.round((float)Math.random() * lifetime);
            if(lifeTime2 < minLifeTime)
                lifeTime2 = minLifeTime;

            float xVel = ((float)Math.random()-0.5f) * vel * 2.0f;
            float yVel = ((float)Math.random()-0.5f) * vel * 2.0f;
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
                    true,
                    true);

            particles.add(obj);
        }
    }
    void emitParticlesPresetThree(Vector2 origin) //for waking, random based
    {
        int amount = Math.round((float)Math.random() - 0.3f); // 50 - 50 chance to spawn 1
        if (amount == 0)
            return;
        int lifetime = 60;
        int minLifeTime = 30;
        float vel = 2;
        float size = 10.0f;
        float angular = 0.1f;
        float friction = 0.975f;

            int lifeTime2 = Math.round((float)Math.random() * lifetime);
            if(lifeTime2 < minLifeTime)
                lifeTime2 = minLifeTime;

            float xVel = ((float)Math.random()-0.5f) * vel * 2.0f;
            float yVel = ((float)Math.random()-0.5f) * vel * 2.0f;
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
                    false,
                    true);

            particles.add(obj);
    }
    enum AnimationNames
    {
        PLAYER_IDLE,
        PLAYER_RUN,
        PLAYER_JUMP,
        PLAYER_JUMP_END,
        PLAYER_DIE,
        BACKGROUND_LAYERS,
        DEATH_WALL,
        WIN_WALL,
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
        anim = new Animation(currentSheet,896,480,0,4); anim.autoPlay = false; anim.timeTillNext = Integer.MAX_VALUE;
        Animations.put(AnimationNames.BACKGROUND_LAYERS,anim);

        currentSheetIndex++; currentSheet = rawImages.get(currentSheetIndex);
        anim = new Animation(currentSheet,960,540,0,8);
        Animations.put(AnimationNames.DEATH_WALL,anim);

        currentSheetIndex++; currentSheet = rawImages.get(currentSheetIndex);
        anim = new Animation(currentSheet,74,1920,0,11); anim.displayTime = 1;
        Animations.put(AnimationNames.WIN_WALL,anim);
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
        void next()
        {
            current++;
            if(current > frames.size())
                current = 0;
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
        void lerpCameraTo(Vector3 to)
        {
            float x = lerp(camPos.x,to.x - sWidth/2,0.2f);
            float y = lerp(camPos.y,to.y - sHeight/3,0.2f);

            camPos = new Vector3(x,y ,camPos.z);
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
        Boolean fadeOut; Boolean shrink;
        Vector2 startSize;
        Particle(Vector2 pos, Vector2 vel,Vector2 size,float angleVel,float angle,float friction, Vector3 color,int lifeTime,Boolean fadeOut,Boolean shrink)
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
            this.shrink = shrink;
            startLifetime = lifeTime;
            startSize = size;
        }
        void Tick()
        {
            pos = pos.add(vel);
            vel = vel.multiply(friction);
            angle += angularVel;
            angularVel *= friction;
            if(fadeOut)
            alpha = (float)lifeTime/(float)startLifetime;
            if(shrink)
            {
                float shrinkCoef = (float)lifeTime/(float)startLifetime;
                Vector2 newScale = new Vector2(startSize.x*shrinkCoef,startSize.y*shrinkCoef);
                size = newScale;
            }
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
            float sinValue = (float) Math.sin(angle);
            float cosValue = (float) Math.cos(angle);
            float tempX, tempY;
            tempX = v1.x * cosValue - v1.y * sinValue;
            tempY = v1.x * sinValue + v1.y * cosValue;
            v1.x = tempX;
            v1.y = tempY;

            tempX = v2.x * cosValue - v2.y * sinValue;
            tempY = v2.x * sinValue + v2.y * cosValue;
            v2.x = tempX;
            v2.y = tempY;

            tempX = v3.x * cosValue - v3.y * sinValue;
            tempY = v3.x * sinValue + v3.y * cosValue;
            v3.x = tempX;
            v3.y = tempY;

            tempX = v4.x * cosValue - v4.y * sinValue;
            tempY = v4.x * sinValue + v4.y * cosValue;
            v4.x = tempX;
            v4.y = tempY;
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
