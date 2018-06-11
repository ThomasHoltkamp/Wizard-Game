
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

public class Animation
{

    private int speed;
    private int frames;
    private int index = 0;
    private int count = 0;

    ArrayList<BufferedImage> img;

    private BufferedImage currentImg;

    public Animation(int speed, BufferedImage... images)
    {
        img = new ArrayList<>();
        this.speed = speed;
        img.addAll(Arrays.asList(images));
        frames = img.size();
    }

    public void runAnimation()
    {
        index++;
        if (index > speed)
        {
            index = 0;
            nextFrame();
        }
    }

    public void nextFrame()
    {
        if (count < frames)
        {
            currentImg = img.get(count);
        }

        count++;

        if (count > frames)
        {
            count = 0;
        }

    }

    public void drawAnimation(Graphics g, double x, double y, int offset)
    {
        g.drawImage(currentImg, (int) x - offset, (int) y, null);
    }

    public void setCount(int count)
    {
        this.count = count;
    }

    public int getCount()
    {
        return count;
    }

    public int getSpeed()
    {
        return speed;
    }

    public void setSpeed(int speed)
    {
        this.speed = speed;
    }

}
