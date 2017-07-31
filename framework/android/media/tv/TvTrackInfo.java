package android.media.tv;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;
import java.util.Objects;

public final class TvTrackInfo
  implements Parcelable
{
  public static final Parcelable.Creator<TvTrackInfo> CREATOR = new Parcelable.Creator()
  {
    public TvTrackInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TvTrackInfo(paramAnonymousParcel, null);
    }
    
    public TvTrackInfo[] newArray(int paramAnonymousInt)
    {
      return new TvTrackInfo[paramAnonymousInt];
    }
  };
  public static final int TYPE_AUDIO = 0;
  public static final int TYPE_SUBTITLE = 2;
  public static final int TYPE_VIDEO = 1;
  private final int mAudioChannelCount;
  private final int mAudioSampleRate;
  private final CharSequence mDescription;
  private final Bundle mExtra;
  private final String mId;
  private final String mLanguage;
  private final int mType;
  private final byte mVideoActiveFormatDescription;
  private final float mVideoFrameRate;
  private final int mVideoHeight;
  private final float mVideoPixelAspectRatio;
  private final int mVideoWidth;
  
  private TvTrackInfo(int paramInt1, String paramString1, String paramString2, CharSequence paramCharSequence, int paramInt2, int paramInt3, int paramInt4, int paramInt5, float paramFloat1, float paramFloat2, byte paramByte, Bundle paramBundle)
  {
    this.mType = paramInt1;
    this.mId = paramString1;
    this.mLanguage = paramString2;
    this.mDescription = paramCharSequence;
    this.mAudioChannelCount = paramInt2;
    this.mAudioSampleRate = paramInt3;
    this.mVideoWidth = paramInt4;
    this.mVideoHeight = paramInt5;
    this.mVideoFrameRate = paramFloat1;
    this.mVideoPixelAspectRatio = paramFloat2;
    this.mVideoActiveFormatDescription = paramByte;
    this.mExtra = paramBundle;
  }
  
  private TvTrackInfo(Parcel paramParcel)
  {
    this.mType = paramParcel.readInt();
    this.mId = paramParcel.readString();
    this.mLanguage = paramParcel.readString();
    this.mDescription = paramParcel.readString();
    this.mAudioChannelCount = paramParcel.readInt();
    this.mAudioSampleRate = paramParcel.readInt();
    this.mVideoWidth = paramParcel.readInt();
    this.mVideoHeight = paramParcel.readInt();
    this.mVideoFrameRate = paramParcel.readFloat();
    this.mVideoPixelAspectRatio = paramParcel.readFloat();
    this.mVideoActiveFormatDescription = paramParcel.readByte();
    this.mExtra = paramParcel.readBundle();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = true;
    boolean bool3 = false;
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof TvTrackInfo)) {
      return false;
    }
    boolean bool1 = bool3;
    if (TextUtils.equals(this.mId, ((TvTrackInfo)paramObject).mId))
    {
      bool1 = bool3;
      if (this.mType == ((TvTrackInfo)paramObject).mType)
      {
        bool1 = bool3;
        if (TextUtils.equals(this.mLanguage, ((TvTrackInfo)paramObject).mLanguage))
        {
          bool1 = bool3;
          if (TextUtils.equals(this.mDescription, ((TvTrackInfo)paramObject).mDescription))
          {
            bool1 = bool3;
            if (Objects.equals(this.mExtra, ((TvTrackInfo)paramObject).mExtra))
            {
              if (this.mType != 0) {
                break label142;
              }
              bool1 = bool3;
              if (this.mAudioChannelCount == ((TvTrackInfo)paramObject).mAudioChannelCount)
              {
                bool1 = bool3;
                if (this.mAudioSampleRate == ((TvTrackInfo)paramObject).mAudioSampleRate) {
                  bool1 = true;
                }
              }
            }
          }
        }
      }
    }
    label142:
    do
    {
      do
      {
        do
        {
          return bool1;
          bool1 = bool2;
          if (this.mType != 1) {
            break;
          }
          bool1 = bool3;
        } while (this.mVideoWidth != ((TvTrackInfo)paramObject).mVideoWidth);
        bool1 = bool3;
      } while (this.mVideoHeight != ((TvTrackInfo)paramObject).mVideoHeight);
      bool1 = bool3;
    } while (this.mVideoFrameRate != ((TvTrackInfo)paramObject).mVideoFrameRate);
    if (this.mVideoPixelAspectRatio == ((TvTrackInfo)paramObject).mVideoPixelAspectRatio) {}
    for (bool1 = bool2;; bool1 = false) {
      return bool1;
    }
  }
  
  public final int getAudioChannelCount()
  {
    if (this.mType != 0) {
      throw new IllegalStateException("Not an audio track");
    }
    return this.mAudioChannelCount;
  }
  
  public final int getAudioSampleRate()
  {
    if (this.mType != 0) {
      throw new IllegalStateException("Not an audio track");
    }
    return this.mAudioSampleRate;
  }
  
  public final CharSequence getDescription()
  {
    return this.mDescription;
  }
  
  public final Bundle getExtra()
  {
    return this.mExtra;
  }
  
  public final String getId()
  {
    return this.mId;
  }
  
  public final String getLanguage()
  {
    return this.mLanguage;
  }
  
  public final int getType()
  {
    return this.mType;
  }
  
  public final byte getVideoActiveFormatDescription()
  {
    if (this.mType != 1) {
      throw new IllegalStateException("Not a video track");
    }
    return this.mVideoActiveFormatDescription;
  }
  
  public final float getVideoFrameRate()
  {
    if (this.mType != 1) {
      throw new IllegalStateException("Not a video track");
    }
    return this.mVideoFrameRate;
  }
  
  public final int getVideoHeight()
  {
    if (this.mType != 1) {
      throw new IllegalStateException("Not a video track");
    }
    return this.mVideoHeight;
  }
  
  public final float getVideoPixelAspectRatio()
  {
    if (this.mType != 1) {
      throw new IllegalStateException("Not a video track");
    }
    return this.mVideoPixelAspectRatio;
  }
  
  public final int getVideoWidth()
  {
    if (this.mType != 1) {
      throw new IllegalStateException("Not a video track");
    }
    return this.mVideoWidth;
  }
  
  public int hashCode()
  {
    return Objects.hashCode(this.mId);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    String str = null;
    paramParcel.writeInt(this.mType);
    paramParcel.writeString(this.mId);
    paramParcel.writeString(this.mLanguage);
    if (this.mDescription != null) {
      str = this.mDescription.toString();
    }
    paramParcel.writeString(str);
    paramParcel.writeInt(this.mAudioChannelCount);
    paramParcel.writeInt(this.mAudioSampleRate);
    paramParcel.writeInt(this.mVideoWidth);
    paramParcel.writeInt(this.mVideoHeight);
    paramParcel.writeFloat(this.mVideoFrameRate);
    paramParcel.writeFloat(this.mVideoPixelAspectRatio);
    paramParcel.writeByte(this.mVideoActiveFormatDescription);
    paramParcel.writeBundle(this.mExtra);
  }
  
  public static final class Builder
  {
    private int mAudioChannelCount;
    private int mAudioSampleRate;
    private CharSequence mDescription;
    private Bundle mExtra;
    private final String mId;
    private String mLanguage;
    private final int mType;
    private byte mVideoActiveFormatDescription;
    private float mVideoFrameRate;
    private int mVideoHeight;
    private float mVideoPixelAspectRatio = 1.0F;
    private int mVideoWidth;
    
    public Builder(int paramInt, String paramString)
    {
      if ((paramInt != 0) && (paramInt != 1) && (paramInt != 2)) {
        throw new IllegalArgumentException("Unknown type: " + paramInt);
      }
      Preconditions.checkNotNull(paramString);
      this.mType = paramInt;
      this.mId = paramString;
    }
    
    public TvTrackInfo build()
    {
      return new TvTrackInfo(this.mType, this.mId, this.mLanguage, this.mDescription, this.mAudioChannelCount, this.mAudioSampleRate, this.mVideoWidth, this.mVideoHeight, this.mVideoFrameRate, this.mVideoPixelAspectRatio, this.mVideoActiveFormatDescription, this.mExtra, null);
    }
    
    public final Builder setAudioChannelCount(int paramInt)
    {
      if (this.mType != 0) {
        throw new IllegalStateException("Not an audio track");
      }
      this.mAudioChannelCount = paramInt;
      return this;
    }
    
    public final Builder setAudioSampleRate(int paramInt)
    {
      if (this.mType != 0) {
        throw new IllegalStateException("Not an audio track");
      }
      this.mAudioSampleRate = paramInt;
      return this;
    }
    
    public final Builder setDescription(CharSequence paramCharSequence)
    {
      this.mDescription = paramCharSequence;
      return this;
    }
    
    public final Builder setExtra(Bundle paramBundle)
    {
      this.mExtra = new Bundle(paramBundle);
      return this;
    }
    
    public final Builder setLanguage(String paramString)
    {
      this.mLanguage = paramString;
      return this;
    }
    
    public final Builder setVideoActiveFormatDescription(byte paramByte)
    {
      if (this.mType != 1) {
        throw new IllegalStateException("Not a video track");
      }
      this.mVideoActiveFormatDescription = paramByte;
      return this;
    }
    
    public final Builder setVideoFrameRate(float paramFloat)
    {
      if (this.mType != 1) {
        throw new IllegalStateException("Not a video track");
      }
      this.mVideoFrameRate = paramFloat;
      return this;
    }
    
    public final Builder setVideoHeight(int paramInt)
    {
      if (this.mType != 1) {
        throw new IllegalStateException("Not a video track");
      }
      this.mVideoHeight = paramInt;
      return this;
    }
    
    public final Builder setVideoPixelAspectRatio(float paramFloat)
    {
      if (this.mType != 1) {
        throw new IllegalStateException("Not a video track");
      }
      this.mVideoPixelAspectRatio = paramFloat;
      return this;
    }
    
    public final Builder setVideoWidth(int paramInt)
    {
      if (this.mType != 1) {
        throw new IllegalStateException("Not a video track");
      }
      this.mVideoWidth = paramInt;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/TvTrackInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */