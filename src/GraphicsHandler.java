import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
public class GraphicsHandler
{
    HashMap<AnimationNames,Animation> Animations = new HashMap<>();
    Camera camera = new Camera();
    int sWidth,sHeight;
    Vector3 worldToScreenSpace(Vector3 in) //converts world space to screen coords
    {
       Vector3 out = in.multiply(camera.zoom).subtract(camera.camPos);
       out.y = sHeight - out.y;
       return out;
    }
    void tickAnimations() //advances all animations
    {
        for (var x : Animations.values())
            x.Tick();
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
}
