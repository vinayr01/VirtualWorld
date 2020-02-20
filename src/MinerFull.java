import processing.core.PImage;

import java.util.List;

public class MinerFull extends EntityMiner {

    public MinerFull(String id, int resourceLimit, Point position, int actionPeriod, int animationPeriod,
                     List<PImage> images)
    {
        super(id, position, images, actionPeriod, animationPeriod, resourceLimit, resourceLimit);
    }

    public void transformFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore)
    {
        MinerNotFull miner = new MinerNotFull(id, resourceLimit,
                position, actionPeriod, animationPeriod,
                images);

        world.removeEntity(this);
        scheduler.unscheduleAllEvents(this);

        world.addEntity(miner);
        scheduler.scheduleActions(miner, world, imageStore);
    }
}
