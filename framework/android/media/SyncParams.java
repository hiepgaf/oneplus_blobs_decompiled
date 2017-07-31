package android.media;

public final class SyncParams
{
  public static final int AUDIO_ADJUST_MODE_DEFAULT = 0;
  public static final int AUDIO_ADJUST_MODE_RESAMPLE = 2;
  public static final int AUDIO_ADJUST_MODE_STRETCH = 1;
  private static final int SET_AUDIO_ADJUST_MODE = 2;
  private static final int SET_FRAME_RATE = 8;
  private static final int SET_SYNC_SOURCE = 1;
  private static final int SET_TOLERANCE = 4;
  public static final int SYNC_SOURCE_AUDIO = 2;
  public static final int SYNC_SOURCE_DEFAULT = 0;
  public static final int SYNC_SOURCE_SYSTEM_CLOCK = 1;
  public static final int SYNC_SOURCE_VSYNC = 3;
  private int mAudioAdjustMode = 0;
  private float mFrameRate = 0.0F;
  private int mSet = 0;
  private int mSyncSource = 0;
  private float mTolerance = 0.0F;
  
  public SyncParams allowDefaults()
  {
    this.mSet |= 0x7;
    return this;
  }
  
  public int getAudioAdjustMode()
  {
    if ((this.mSet & 0x2) == 0) {
      throw new IllegalStateException("audio adjust mode not set");
    }
    return this.mAudioAdjustMode;
  }
  
  public float getFrameRate()
  {
    if ((this.mSet & 0x8) == 0) {
      throw new IllegalStateException("frame rate not set");
    }
    return this.mFrameRate;
  }
  
  public int getSyncSource()
  {
    if ((this.mSet & 0x1) == 0) {
      throw new IllegalStateException("sync source not set");
    }
    return this.mSyncSource;
  }
  
  public float getTolerance()
  {
    if ((this.mSet & 0x4) == 0) {
      throw new IllegalStateException("tolerance not set");
    }
    return this.mTolerance;
  }
  
  public SyncParams setAudioAdjustMode(int paramInt)
  {
    this.mAudioAdjustMode = paramInt;
    this.mSet |= 0x2;
    return this;
  }
  
  public SyncParams setFrameRate(float paramFloat)
  {
    this.mFrameRate = paramFloat;
    this.mSet |= 0x8;
    return this;
  }
  
  public SyncParams setSyncSource(int paramInt)
  {
    this.mSyncSource = paramInt;
    this.mSet |= 0x1;
    return this;
  }
  
  public SyncParams setTolerance(float paramFloat)
  {
    if ((paramFloat < 0.0F) || (paramFloat >= 1.0F)) {
      throw new IllegalArgumentException("tolerance must be less than one and non-negative");
    }
    this.mTolerance = paramFloat;
    this.mSet |= 0x4;
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/SyncParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */