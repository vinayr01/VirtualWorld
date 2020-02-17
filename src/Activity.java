public class Activity implements ActionActivity{
    private EntityAction entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Activity(EntityAction entity, WorldModel world, ImageStore imageStore)
    {
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
        this.repeatCount = 0;
    }

    public EntityAction getEntity()
    {
        return entity;
    }

    public WorldModel getWorld()
    {
        return world;
    }

    public ImageStore getImageStore()
    {
        return imageStore;
    }

    public int getRepeatCount()
    {
        return repeatCount;
    }
}
