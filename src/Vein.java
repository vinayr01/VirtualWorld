import processing.core.PImage;

import java.util.List;

public class Vein extends EntityAction {

    public Vein(String id, Point position, int actionPeriod, List<PImage> images)
    {
        super(id, position, images, actionPeriod);
    }
}
