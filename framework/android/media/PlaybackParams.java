package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class PlaybackParams
  implements Parcelable
{
  public static final int AUDIO_FALLBACK_MODE_DEFAULT = 0;
  public static final int AUDIO_FALLBACK_MODE_FAIL = 2;
  public static final int AUDIO_FALLBACK_MODE_MUTE = 1;
  public static final int AUDIO_STRETCH_MODE_DEFAULT = 0;
  public static final int AUDIO_STRETCH_MODE_VOICE = 1;
  public static final Parcelable.Creator<PlaybackParams> CREATOR = new Parcelable.Creator()
  {
    public PlaybackParams createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PlaybackParams(paramAnonymousParcel, null);
    }
    
    public PlaybackParams[] newArray(int paramAnonymousInt)
    {
      return new PlaybackParams[paramAnonymousInt];
    }
  };
  private static final int SET_AUDIO_FALLBACK_MODE = 4;
  private static final int SET_AUDIO_STRETCH_MODE = 8;
  private static final int SET_PITCH = 2;
  private static final int SET_SPEED = 1;
  private int mAudioFallbackMode = 0;
  private int mAudioStretchMode = 0;
  private float mPitch = 1.0F;
  private int mSet = 0;
  private float mSpeed = 1.0F;
  
  public PlaybackParams() {}
  
  private PlaybackParams(Parcel paramParcel)
  {
    this.mSet = paramParcel.readInt();
    this.mAudioFallbackMode = paramParcel.readInt();
    this.mAudioStretchMode = paramParcel.readInt();
    this.mPitch = paramParcel.readFloat();
    if (this.mPitch < 0.0F) {
      this.mPitch = 0.0F;
    }
    this.mSpeed = paramParcel.readFloat();
  }
  
  public PlaybackParams allowDefaults()
  {
    this.mSet |= 0xF;
    return this;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public int getAudioFallbackMode()
  {
    if ((this.mSet & 0x4) == 0) {
      throw new IllegalStateException("audio fallback mode not set");
    }
    return this.mAudioFallbackMode;
  }
  
  public int getAudioStretchMode()
  {
    if ((this.mSet & 0x8) == 0) {
      throw new IllegalStateException("audio stretch mode not set");
    }
    return this.mAudioStretchMode;
  }
  
  public float getPitch()
  {
    if ((this.mSet & 0x2) == 0) {
      throw new IllegalStateException("pitch not set");
    }
    return this.mPitch;
  }
  
  public float getSpeed()
  {
    if ((this.mSet & 0x1) == 0) {
      throw new IllegalStateException("speed not set");
    }
    return this.mSpeed;
  }
  
  public PlaybackParams setAudioFallbackMode(int paramInt)
  {
    this.mAudioFallbackMode = paramInt;
    this.mSet |= 0x4;
    return this;
  }
  
  public PlaybackParams setAudioStretchMode(int paramInt)
  {
    this.mAudioStretchMode = paramInt;
    this.mSet |= 0x8;
    return this;
  }
  
  public PlaybackParams setPitch(float paramFloat)
  {
    if (paramFloat < 0.0F) {
      throw new IllegalArgumentException("pitch must not be negative");
    }
    this.mPitch = paramFloat;
    this.mSet |= 0x2;
    return this;
  }
  
  public PlaybackParams setSpeed(float paramFloat)
  {
    this.mSpeed = paramFloat;
    this.mSet |= 0x1;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mSet);
    paramParcel.writeInt(this.mAudioFallbackMode);
    paramParcel.writeInt(this.mAudioStretchMode);
    paramParcel.writeFloat(this.mPitch);
    paramParcel.writeFloat(this.mSpeed);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/PlaybackParams.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */