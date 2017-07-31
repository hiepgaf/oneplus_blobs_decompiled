package android.filterpacks.performance;

public class Throughput
{
  private final int mPeriodFrames;
  private final int mPeriodTime;
  private final int mPixels;
  private final int mTotalFrames;
  
  public Throughput(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mTotalFrames = paramInt1;
    this.mPeriodFrames = paramInt2;
    this.mPeriodTime = paramInt3;
    this.mPixels = paramInt4;
  }
  
  public float getFramesPerSecond()
  {
    return this.mPeriodFrames / this.mPeriodTime;
  }
  
  public float getNanosPerPixel()
  {
    return (float)(this.mPeriodTime / this.mPeriodFrames * 1000000.0D / this.mPixels);
  }
  
  public int getPeriodFrameCount()
  {
    return this.mPeriodFrames;
  }
  
  public int getPeriodTime()
  {
    return this.mPeriodTime;
  }
  
  public int getTotalFrameCount()
  {
    return this.mTotalFrames;
  }
  
  public String toString()
  {
    return getFramesPerSecond() + " FPS";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/performance/Throughput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */