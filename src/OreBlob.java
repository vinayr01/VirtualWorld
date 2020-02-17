import processing.core.PImage;

import java.util.List;

public class OreBlob implements EntityAnimation, EntityAction {
    private String id;
    private Point position;
    private List<PImage> images;
    private int imageIndex;
    private int resourceLimit;
    private int resourceCount;
    private int actionPeriod;
    private int animationPeriod;

    public OreBlob(String id, Point position, int actionPeriod, int animationPeriod, List<PImage> images)
    {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        //this.resourceLimit = 0;
        //this.resourceCount = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
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

    public int getActionPeriod()
    {
        return actionPeriod;
    }

    public int getAnimationPeriod()
    {
        return animationPeriod;
    }

    public void nextImage()
    {
        imageIndex = (imageIndex + 1) % images.size();
    }
}
