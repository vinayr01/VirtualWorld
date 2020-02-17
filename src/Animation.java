public class Animation implements Action {
    private EntityAnimation entity;
    private WorldModel world;
    private ImageStore imageStore;
    private int repeatCount;

    public Animation(EntityAnimation entity, int repeatCount)
    {
        this.entity = entity;
        //this.world = null;
        //this.imageStore = null;
        this.repeatCount = repeatCount;
    }

    public EntityAnimation getEntity()
    {
        return entity;
    }

    public int getRepeatCount()
    {
        return repeatCount;
    }
}
