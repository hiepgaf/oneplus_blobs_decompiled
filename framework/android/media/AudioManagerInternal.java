package android.media;

public abstract class AudioManagerInternal
{
  public abstract void adjustStreamVolumeForUid(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4);
  
  public abstract void adjustSuggestedStreamVolumeForUid(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4);
  
  public abstract int getRingerModeInternal();
  
  public abstract int getVolumeControllerUid();
  
  public abstract void setRingerModeDelegate(RingerModeDelegate paramRingerModeDelegate);
  
  public abstract void setRingerModeInternal(int paramInt, String paramString);
  
  public abstract void setStreamVolumeForUid(int paramInt1, int paramInt2, int paramInt3, String paramString, int paramInt4);
  
  public abstract void updateRingerModeAffectedStreamsInternal();
  
  public static abstract interface RingerModeDelegate
  {
    public abstract boolean canVolumeDownEnterSilent();
    
    public abstract int getRingerModeAffectedStreams(int paramInt);
    
    public abstract int onSetRingerModeExternal(int paramInt1, int paramInt2, String paramString, int paramInt3, VolumePolicy paramVolumePolicy);
    
    public abstract int onSetRingerModeInternal(int paramInt1, int paramInt2, String paramString, int paramInt3, VolumePolicy paramVolumePolicy);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */