package android.filterfw.core;

public abstract class Program
{
  public abstract Object getHostValue(String paramString);
  
  public void process(Frame paramFrame1, Frame paramFrame2)
  {
    process(new Frame[] { paramFrame1 }, paramFrame2);
  }
  
  public abstract void process(Frame[] paramArrayOfFrame, Frame paramFrame);
  
  public void reset() {}
  
  public abstract void setHostValue(String paramString, Object paramObject);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/Program.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */