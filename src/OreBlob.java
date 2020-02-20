import processing.core.PImage;

import java.util.List;

public class OreBlob extends EntityAnimation {

    public OreBlob(String id, Point position, List<PImage> images, int actionPeriod, int animationPeriod)
    {
        super(id, position, images, actionPeriod, animationPeriod);
    }
}
