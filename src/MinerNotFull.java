import processing.core.PImage;

import java.util.List;

public class MinerNotFull extends EntityMiner {

    public MinerNotFull(String id, int resourceLimit, Point position, int actionPeriod, int animationPeriod,
                        List<PImage> images)
    {
        super(id, position, images, actionPeriod, animationPeriod, resourceLimit, 0);
    }

    public boolean transformNotFull(WorldModel world,
                                    EventScheduler scheduler, ImageStore imageStore)
    {
        if (resourceCount >= resourceLimit)
        {
            MinerFull miner = new MinerFull(id, resourceLimit,
                    position, actionPeriod, animationPeriod,
                    images);

            world.removeEntity(this);
            scheduler.unscheduleAllEvents(this);

            world.addEntity(miner);
            scheduler.scheduleActions(miner, world, imageStore);

            return true;
        }

        return false;
    }
}
