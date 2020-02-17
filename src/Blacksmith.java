import processing.core.PImage;

import java.util.List;

public class Blacksmith implements EntityAnimation {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    //private int resourceLimit;
    //private int resourceCount;
    //private int actionPeriod;
    private int animationPeriod;

    public Blacksmith(String id, Point position,
                                          List<PImage> images)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        //this.resourceLimit = 0;
        //this.resourceCount = 0;
        //this.actionPeriod = 0;
        this.animationPeriod = 0;
    }

    public String getId()
    {
        return id;
    }

    public Point getPosition()
    {
        return position;
    }

    public void setPosition(Point pos)
    {
        position = pos;
    }

    public List<PImage> getImages()
    {
        return images;
    }

    public int getImageIndex()
    {
        return imageIndex;
    }

    /*public int getActionPeriod()
    {
        return actionPeriod;
    }*/

    public int getAnimationPeriod()
    {
        return animationPeriod;
    }

    public void nextImage()
    {
        imageIndex = (imageIndex + 1) % images.size();
    }
}
