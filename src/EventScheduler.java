import java.util.*;

final class EventScheduler
{
   private PriorityQueue<Event> eventQueue;
   private Map<Entity, List<Event>> pendingEvents;
   private double timeScale;

   public static final int QUAKE_ANIMATION_REPEAT_COUNT = 10;

   public EventScheduler(double timeScale)
   {
      this.eventQueue = new PriorityQueue<>(new EventComparator());
      this.pendingEvents = new HashMap<>();
      this.timeScale = timeScale;
   }

   public void scheduleEvent(Entity entity, Action action, long afterPeriod)
   {
      long time = System.currentTimeMillis() +
              (long)(afterPeriod * timeScale);
      Event event = new Event(action, time, entity);

      eventQueue.add(event);

      // update list of pending events for the given entity
      List<Event> pending = pendingEvents.getOrDefault(entity,
              new LinkedList<>());
      pending.add(event);
      pendingEvents.put(entity, pending);
   }

   public void unscheduleAllEvents(Entity entity)
   {
      List<Event> pending = pendingEvents.remove(entity);

      if (pending != null)
      {
         for (Event event : pending)
         {
            eventQueue.remove(event);
         }
      }
   }

   public void removePendingEvent(Event event)
   {
      List<Event> pending = pendingEvents.get(event.getEntity());

      if (pending != null)
      {
         pending.remove(event);
      }
   }

   public void updateOnTime(long time)
   {
      while (!eventQueue.isEmpty() &&
              eventQueue.peek().getTime() < time)
      {
         Event next = eventQueue.poll();

         removePendingEvent(next);

         next.executeAction(next.getAction(), this);
      }
   }

   public void scheduleActions(Entity entity, WorldModel world, ImageStore imageStore)
   {
      if (entity instanceof MinerFull)
      {
         scheduleEvent(entity,
                 new Activity((EntityAction) entity, world, imageStore),
                 ((MinerFull) entity).getActionPeriod());
         scheduleEvent(entity, new Animation((EntityAnimation) entity, 0),
                 ((MinerFull) entity).getAnimationPeriod());
      }
      else if (entity instanceof MinerNotFull)
      {
         scheduleEvent(entity,
                 new Activity((EntityAction) entity, world, imageStore),
                 ((MinerNotFull) entity).getActionPeriod());
         scheduleEvent(entity,
                 new Animation((EntityAnimation) entity, 0), ((MinerNotFull) entity).getAnimationPeriod());
      }
      else if (entity instanceof Ore)
      {
         scheduleEvent(entity,
                 new Activity((EntityAction) entity, world, imageStore),
                 ((Ore) entity).getActionPeriod());
      }
      else if (entity instanceof OreBlob)
      {
         scheduleEvent(entity,
                 new Activity((EntityAction) entity, world, imageStore),
                 ((OreBlob) entity).getActionPeriod());
         scheduleEvent(entity,
                 new Animation((EntityAnimation) entity, 0), ((OreBlob) entity).getAnimationPeriod());
      }
      else if (entity instanceof Quake)
      {
         scheduleEvent(entity,
                 new Activity((EntityAction) entity, world, imageStore),
                 ((Quake) entity).getActionPeriod());
         scheduleEvent(entity,
                 new Animation((EntityAnimation) entity, QUAKE_ANIMATION_REPEAT_COUNT),
                 ((Quake) entity).getAnimationPeriod());
      }
      else if (entity instanceof Vein)
      {
         scheduleEvent(entity,
                 new Activity((EntityAction) entity, world, imageStore),
                 ((Vein) entity).getActionPeriod());
      }
   }
}
