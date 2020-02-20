public abstract class Action
{
   private int repeatCount;

   public Action(int repeatCount)
   {
      this.repeatCount = repeatCount;
   }

   abstract Entity getEntity();
   public int getRepeatCount()
   {
      return repeatCount;
   }
}
