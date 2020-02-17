import java.util.Optional;
import java.util.Random;

final class Event
{
   private Action action;
   private long time;
   private Entity entity;

   public static final Random rand = new Random();

   public static final String BLOB_KEY = "blob";
   public static final String BLOB_ID_SUFFIX = " -- blob";
   public static final int BLOB_PERIOD_SCALE = 4;
   public static final int BLOB_ANIMATION_MIN = 50;
   public static final int BLOB_ANIMATION_MAX = 150;

   public static final String ORE_ID_PREFIX = "ore -- ";
   public static final int ORE_CORRUPT_MIN = 20000;
   public static final int ORE_CORRUPT_MAX = 30000;

   public static final String QUAKE_KEY = "quake";

   public Event(Action action, long time, Entity entity)
   {
      this.action = action;
      this.time = time;
      this.entity = entity;
   }

   public Action getAction()
   {
      return action;
   }

   public long getTime()
   {
      return time;
   }

   public Entity getEntity()
   {
      return entity;
   }

   public void executeAction(Action action, EventScheduler scheduler)
   {
      if (action instanceof Activity)
      {
         executeActivityAction((Activity) action, scheduler);
      }
      else if (action instanceof Animation)
      {
         executeAnimationAction((Animation) action, scheduler);
      }
   }

   public static void executeAnimationAction(Animation action,
                                             EventScheduler scheduler)
   {
      action.getEntity().nextImage();

      if (action.getRepeatCount() != 1)
      {
         scheduler.scheduleEvent(action.getEntity(),
                 new Animation(action.getEntity(),
                         Math.max(action.getRepeatCount() - 1, 0)),
                 action.getEntity().getAnimationPeriod());
      }
   }

   public static void executeActivityAction(Activity action,
                                            EventScheduler scheduler)
   {
      if (action.getEntity() instanceof MinerFull)
      {
         executeMinerFullActivity((MinerFull) action.getEntity(), action.getWorld(),
                 action.getImageStore(), scheduler);
      }
      else if (action.getEntity() instanceof MinerNotFull)
      {
         executeMinerNotFullActivity((MinerNotFull) action.getEntity(), action.getWorld(),
                 action.getImageStore(), scheduler);
      }
      else if (action.getEntity() instanceof Ore)
      {
         executeOreActivity((Ore) action.getEntity(), action.getWorld(), action.getImageStore(),
                 scheduler);
      }
      else if (action.getEntity() instanceof OreBlob)
      {
         executeOreBlobActivity((OreBlob) action.getEntity(), action.getWorld(),
                 action.getImageStore(), scheduler);
      }
      else if (action.getEntity() instanceof Quake)
      {
         executeQuakeActivity((Quake) action.getEntity(), action.getWorld(), action.getImageStore(),
                 scheduler);
      }
      else if (action.getEntity() instanceof Vein)
      {
         executeVeinActivity((Vein) action.getEntity(), action.getWorld(), action.getImageStore(),
                 scheduler);
      }
      else if (action.getEntity() instanceof Blacksmith)
      {
         throw new UnsupportedOperationException("executeActivityAction not supported for Blacksmith");
      }
      else if (action.getEntity() instanceof Obstacle)
      {
         throw new UnsupportedOperationException("executeActivityAction not supported for Obstacle");
      }
   }

   public static void executeMinerFullActivity(MinerFull entity, WorldModel world,
                                               ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> fullTarget = world.findNearest(entity.getPosition(),
              Blacksmith.class);

      if (fullTarget.isPresent() &&
              world.moveToFull(entity, fullTarget.get(), scheduler))
      {
         entity.transformFull(world, scheduler, imageStore);
      }
      else
      {
         scheduler.scheduleEvent(entity,
                 new Activity(entity, world, imageStore),
                 entity.getActionPeriod());
      }
   }

   public static void executeMinerNotFullActivity(MinerNotFull entity,
                                                  WorldModel world, ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> notFullTarget = world.findNearest(entity.getPosition(),
              Ore.class);

      if (!notFullTarget.isPresent() ||
              !world.moveToNotFull(entity, notFullTarget.get(), scheduler) ||
              !entity.transformNotFull(world, scheduler, imageStore))
      {
         scheduler.scheduleEvent(entity,
                 new Activity(entity, world, imageStore),
                 entity.getActionPeriod());
      }
   }

   public static void executeOreActivity(Entity entity, WorldModel world,
                                         ImageStore imageStore, EventScheduler scheduler)
   {
      Point pos = entity.getPosition();  // store current position before removing

      world.removeEntity(entity);
      scheduler.unscheduleAllEvents(entity);

      OreBlob blob = new OreBlob(entity.getId() + BLOB_ID_SUFFIX,
              pos, ((OreBlob) entity).getActionPeriod() / BLOB_PERIOD_SCALE,
              BLOB_ANIMATION_MIN +
                      rand.nextInt(BLOB_ANIMATION_MAX - BLOB_ANIMATION_MIN),
              imageStore.getImageList(BLOB_KEY));

      world.addEntity(blob);
      scheduler.scheduleActions(blob, world, imageStore);
   }

   public static void executeOreBlobActivity(OreBlob entity, WorldModel world,
                                             ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Entity> blobTarget = world.findNearest(entity.getPosition(), Vein.class);
      long nextPeriod = entity.getActionPeriod();

      if (blobTarget.isPresent())
      {
         Point tgtPos = blobTarget.get().getPosition();

         if (world.moveToOreBlob(entity, blobTarget.get(), scheduler))
         {
            Quake quake = new Quake(tgtPos,
                    imageStore.getImageList(QUAKE_KEY));

            world.addEntity(quake);
            nextPeriod += entity.getActionPeriod();
            scheduler.scheduleActions(quake, world, imageStore);
         }
      }

      scheduler.scheduleEvent(entity,
              new Activity(entity, world, imageStore),
              nextPeriod);
   }

   public static void executeQuakeActivity(Quake entity, WorldModel world,
                                           ImageStore imageStore, EventScheduler scheduler)
   {
      scheduler.unscheduleAllEvents(entity);
      world.removeEntity(entity);
   }

   public static void executeVeinActivity(Vein entity, WorldModel world,
                                          ImageStore imageStore, EventScheduler scheduler)
   {
      Optional<Point> openPt = world.findOpenAround(entity.getPosition());

      if (openPt.isPresent())
      {
         Ore ore = new Ore(ORE_ID_PREFIX + entity.getId(),
                 openPt.get(), ORE_CORRUPT_MIN +
                         rand.nextInt(ORE_CORRUPT_MAX - ORE_CORRUPT_MIN),
                 imageStore.getImageList(VirtualWorld.ORE_KEY));
         world.addEntity(ore);
         scheduler.scheduleActions(ore, world, imageStore);
      }

      scheduler.scheduleEvent(entity,
              new Activity(entity, world, imageStore),
              entity.getActionPeriod());
   }
}
