import processing.core.PImage;

import java.util.*;

final class WorldModel
{
   private int numRows;
   private int numCols;
   private Background background[][];
   private Entity occupancy[][];
   private Set<Entity> entities;

   public static final int ORE_REACH = 1;

   public WorldModel(int numRows, int numCols, Background defaultBackground)
   {
      this.numRows = numRows;
      this.numCols = numCols;
      this.background = new Background[numRows][numCols];
      this.occupancy = new Entity[numRows][numCols];
      this.entities = new HashSet<>();

      for (int row = 0; row < numRows; row++)
      {
         Arrays.fill(this.background[row], defaultBackground);
      }
   }

   public int getNumRows()
   {
      return numRows;
   }

   public int getNumCols()
   {
      return numCols;
   }

   public Background[][] getBackground()
   {
      return background;
   }

   public Entity[][] getOccupancy()
   {
      return occupancy;
   }

   public Set<Entity> getEntities()
   {
      return entities;
   }

   public PImage getCurrentImage(Object entity)
   {
      if (entity instanceof Background)
      {
         return ((Background)entity).getImages()
                 .get(((Background)entity).getImageIndex());
      }
      else if (entity instanceof Entity)
      {
         return ((Entity)entity).getImages().get(((Entity)entity).getImageIndex());
      }
      else
      {
         throw new UnsupportedOperationException(
                 String.format("getCurrentImage not supported for %s",
                         entity));
      }
   }

   public Entity getOccupancyCell(Point pos)
   {
      return occupancy[pos.y][pos.x];
   }

   public void setOccupancyCell(Point pos, Entity entity)
   {
      occupancy[pos.y][pos.x] = entity;
   }

   public boolean withinBounds(Point pos)
   {
      return pos.y >= 0 && pos.y < numRows &&
              pos.x >= 0 && pos.x < numCols;
   }

   public boolean isOccupied(Point pos)
   {
      return withinBounds(pos) &&
              getOccupancyCell(pos) != null;
   }

   public Optional<Point> findOpenAround(Point pos)
   {
      for (int dy = -ORE_REACH; dy <= ORE_REACH; dy++)
      {
         for (int dx = -ORE_REACH; dx <= ORE_REACH; dx++)
         {
            Point newPt = new Point(pos.x + dx, pos.y + dy);
            if (withinBounds(newPt) &&
                    !isOccupied(newPt))
            {
               return Optional.of(newPt);
            }
         }
      }

      return Optional.empty();
   }

   public Optional<Entity> nearestEntity(List<Entity> entities, Point pos)
   {
      if (entities.isEmpty())
      {
         return Optional.empty();
      }
      else
      {
         Entity nearest = entities.get(0);
         int nearestDistance = Point.distanceSquared(nearest.getPosition(), pos);

         for (Entity other : entities)
         {
            int otherDistance = Point.distanceSquared(other.getPosition(), pos);

            if (otherDistance < nearestDistance)
            {
               nearest = other;
               nearestDistance = otherDistance;
            }
         }

         return Optional.of(nearest);
      }
   }

   public Optional<Entity> findNearest(Point pos, Class kind)
   {
      List<Entity> ofType = new LinkedList<>();
      for (Entity entity : entities)
      {
         if (kind.isInstance(entity))
         {
            ofType.add(entity);
         }
      }

      return nearestEntity(ofType, pos);
   }

   public void tryAddEntity(Entity entity)
   {
      if (isOccupied(entity.getPosition()))
      {
         // arguably the wrong type of exception, but we are not
         // defining our own exceptions yet
         throw new IllegalArgumentException("position occupied");
      }

      addEntity(entity);
   }

   public void addEntity(Entity entity)
   {
      if (withinBounds(entity.getPosition()))
      {
         setOccupancyCell(entity.getPosition(), entity);
         entities.add(entity);
      }
   }

   public void moveEntity(Entity entity, Point pos)
   {
      Point oldPos = entity.getPosition();
      if (withinBounds(pos) && !pos.equals(oldPos))
      {
         setOccupancyCell(oldPos, null);
         removeEntityAt(pos);
         setOccupancyCell(pos, entity);
         entity.setPosition(pos);
      }
   }

   public void removeEntity(Entity entity)
   {
      removeEntityAt(entity.getPosition());
   }

   public void removeEntityAt(Point pos)
   {
      if (withinBounds(pos)
              && getOccupancyCell(pos) != null)
      {
         Entity entity = getOccupancyCell(pos);

         /* this moves the entity just outside of the grid for
            debugging purposes */
         entity.setPosition(new Point(-1, -1));
         entities.remove(entity);
         setOccupancyCell(pos, null);
      }
   }

   public Optional<PImage> getBackgroundImage(Point pos)
   {
      if (withinBounds(pos))
      {
         return Optional.of(getCurrentImage(getBackgroundCell(pos)));
      }
      else
      {
         return Optional.empty();
      }
   }

   public Background getBackgroundCell(Point pos)
   {
      return background[pos.y][pos.x];
   }

   public void setBackground(Point pos, Background background)
   {
      if (withinBounds(pos))
      {
         setBackgroundCell(pos, background);
      }
   }

   public void setBackgroundCell(Point pos, Background background)
   {
      this.background[pos.y][pos.x] = background;
   }

   public Optional<Entity> getOccupant(Point pos)
   {
      if (isOccupied(pos))
      {
         return Optional.of(getOccupancyCell(pos));
      }
      else
      {
         return Optional.empty();
      }
   }

   public boolean adjacent(Point p1, Point p2)
   {
      return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) ||
              (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
   }

   public Point nextPositionMiner(Entity entity, Point destPos)
   {
      int horiz = Integer.signum(destPos.x - entity.getPosition().x);
      Point newPos = new Point(entity.getPosition().x + horiz,
              entity.getPosition().y);

      if (horiz == 0 || isOccupied(newPos))
      {
         int vert = Integer.signum(destPos.y - entity.getPosition().y);
         newPos = new Point(entity.getPosition().x,
                 entity.getPosition().y + vert);

         if (vert == 0 || isOccupied(newPos))
         {
            newPos = entity.getPosition();
         }
      }

      return newPos;
   }

   public Point nextPositionOreBlob(Entity entity, Point destPos)
   {
      int horiz = Integer.signum(destPos.x - entity.getPosition().x);
      Point newPos = new Point(entity.getPosition().x + horiz,
              entity.getPosition().y);

      Optional<Entity> occupant = getOccupant(newPos);

      if (horiz == 0 ||
              (occupant.isPresent() && !(occupant.get().getClass().isInstance(Ore.class))))
      {
         int vert = Integer.signum(destPos.y - entity.getPosition().y);
         newPos = new Point(entity.getPosition().x, entity.getPosition().y + vert);
         occupant = getOccupant(newPos);

         if (vert == 0 ||
                 (occupant.isPresent() && !(occupant.get().getClass().isInstance(Ore.class))))
         {
            newPos = entity.getPosition();
         }
      }

      return newPos;
   }

   public boolean moveToNotFull(EntityMiner miner, Entity target, EventScheduler scheduler)
   {
      if (adjacent(miner.getPosition(), target.getPosition()))
      {
         miner.incrementResourceCount(1);
         removeEntity(target);
         scheduler.unscheduleAllEvents(target);

         return true;
      }
      else
      {
         Point nextPos = nextPositionMiner(miner, target.getPosition());

         if (!miner.getPosition().equals(nextPos))
         {
            Optional<Entity> occupant = getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            moveEntity(miner, nextPos);
         }
         return false;
      }
   }

   public boolean moveToFull(Entity miner, Entity target, EventScheduler scheduler)
   {
      if (adjacent(miner.getPosition(), target.getPosition()))
      {
         return true;
      }
      else
      {
         Point nextPos = nextPositionMiner(miner, target.getPosition());

         if (!miner.getPosition().equals(nextPos))
         {
            Optional<Entity> occupant = getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            moveEntity(miner, nextPos);
         }
         return false;
      }
   }

   public boolean moveToOreBlob(Entity blob, Entity target, EventScheduler scheduler)
   {
      if (adjacent(blob.getPosition(), target.getPosition()))
      {
         removeEntity(target);
         scheduler.unscheduleAllEvents(target);
         return true;
      }
      else
      {
         Point nextPos = nextPositionOreBlob(blob, target.getPosition());

         if (!blob.getPosition().equals(nextPos))
         {
            Optional<Entity> occupant = getOccupant(nextPos);
            if (occupant.isPresent())
            {
               scheduler.unscheduleAllEvents(occupant.get());
            }

            moveEntity(blob, nextPos);
         }
         return false;
      }
   }
}
