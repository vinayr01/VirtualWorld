import processing.core.PImage;

import java.util.List;

public abstract class EntityMiner extends EntityAnimation {
    protected int resourceCount;
    protected int resourceLimit;

    public EntityMiner(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod,
                       int resourceLimit, int resourceCount)
    {
        super(id, position, images, actionPeriod, animationPeriod);
        this.resourceLimit = resourceLimit;
        this.resourceCount = resourceCount;
    }

    public void incrementResourceCount(int num)
    {
        resourceCount += num;
    }
}
