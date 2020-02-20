public class Activity extends Action{
    private EntityAction entity;
    private WorldModel world;
    private ImageStore imageStore;

    public Activity(EntityAction entity, WorldModel world, ImageStore imageStore)
    {
        super(0);
        this.entity = entity;
        this.world = world;
        this.imageStore = imageStore;
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
}
