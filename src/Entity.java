import java.util.List;
import processing.core.PImage;

public abstract class Entity
{
   protected String id;
   protected Point position;
   protected List<PImage> images;
   protected int imageIndex;

   /*public EntityKind getKind()
   {
      return kind;
   }*/

   public Entity(String id, Point position, List<PImage> images)
   {
      this.id = id;
      this.position = position;
      this.images = images;
      imageIndex = 0;
   }

   public String getId()
   {
      return id;
   }

   public Point getPosition()
   {
      return position;
   }

   public void setPosition(Point pos)
   {
      position = pos;
   }

   public List<PImage> getImages()
   {
      return images;
   }

   public int getImageIndex()
   {
      return imageIndex;
   }

   public void nextImage()
   {
      imageIndex = (imageIndex + 1) % images.size();
   }
}
