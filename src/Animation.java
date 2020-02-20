public class Animation extends Action {
    private EntityAnimation entity;

    public Animation(EntityAnimation entity, int repeatCount)
    {
        super(repeatCount);
        this.entity = entity;
    }

    public EntityAnimation getEntity()
    {
        return entity;
    }
}
