package android.media;

public final class MediaTimestamp
{
  public final float clockRate;
  public final long mediaTimeUs;
  public final long nanoTime;
  
  MediaTimestamp()
  {
    this.mediaTimeUs = 0L;
    this.nanoTime = 0L;
    this.clockRate = 1.0F;
  }
  
  MediaTimestamp(long paramLong1, long paramLong2, float paramFloat)
  {
    this.mediaTimeUs = paramLong1;
    this.nanoTime = paramLong2;
    this.clockRate = paramFloat;
  }
  
  public long getAnchorMediaTimeUs()
  {
    return this.mediaTimeUs;
  }
  
  public long getAnchorSytemNanoTime()
  {
    return this.nanoTime;
  }
  
  public float getMediaClockRate()
  {
    return this.clockRate;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaTimestamp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */