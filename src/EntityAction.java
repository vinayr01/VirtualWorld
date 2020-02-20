import processing.core.PImage;

import java.util.List;

public abstract class EntityAction extends Entity {
    protected int actionPeriod;

    public EntityAction(String id, Point position, List<PImage> images, int actionPeriod)
    {
        super(id, position, images);
        this.actionPeriod = actionPeriod;
    }

    public int getActionPeriod()
    {
        return actionPeriod;
    }
}
