package android.media;

public class MediaSyncEvent
{
  public static final int SYNC_EVENT_NONE = 0;
  public static final int SYNC_EVENT_PRESENTATION_COMPLETE = 1;
  private int mAudioSession = 0;
  private final int mType;
  
  private MediaSyncEvent(int paramInt)
  {
    this.mType = paramInt;
  }
  
  public static MediaSyncEvent createEvent(int paramInt)
    throws IllegalArgumentException
  {
    if (!isValidType(paramInt)) {
      throw new IllegalArgumentException(paramInt + "is not a valid MediaSyncEvent type.");
    }
    return new MediaSyncEvent(paramInt);
  }
  
  private static boolean isValidType(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  public int getAudioSessionId()
  {
    return this.mAudioSession;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public MediaSyncEvent setAudioSessionId(int paramInt)
    throws IllegalArgumentException
  {
    if (paramInt > 0)
    {
      this.mAudioSession = paramInt;
      return this;
    }
    throw new IllegalArgumentException(paramInt + " is not a valid session ID.");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaSyncEvent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */