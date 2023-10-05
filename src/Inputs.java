import processing.core.PApplet;
import processing.event.KeyEvent;

public class Inputs
{
    Boolean up = false,down = false,left = false,right = false,dash = false,space = false,backspace = false;
    public void keyPressed(KeyEvent event)
    {
        switch (event.getKeyCode())
        {
            case 38: //up
                up = true;
                break;
            case 40: //down
                down = true;
                break;
            case 39: //right
                right = true;
                break;
            case 37://left
                left = true;
                break;
            case 16://shift
                dash = true;
                break;
            case 32: //space bar
                space = true;
                break;
            case '\b'://backspace
                backspace = true;
                break;
        }
    }
    public void keyReleased(KeyEvent event)
    {
        switch (event.getKeyCode())
        {
            case 38: //up
                up = false;
                break;
            case 40: //down
                down = false;
                break;
            case 39: //right
                right = false;
                break;
            case 37://left
                left = false;
                break;
            case 16://shift
                dash = false;
                break;
            case 32: //space bar
                space = false;
                break;
            case '\b'://backspace
                backspace = false;
                break;
        }
    }
}
