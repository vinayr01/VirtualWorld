import java.util.List;
import processing.core.PImage;

public interface Entity
{

   /*public EntityKind getKind()
   {
      return kind;
   }*/

   public String getId();

   public Point getPosition();

   public List<PImage> getImages();

   public int getImageIndex();

   public void setPosition(Point pos);

   public void nextImage();
}
