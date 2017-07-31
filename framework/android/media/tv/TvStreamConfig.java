package android.media.tv;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

public class TvStreamConfig
  implements Parcelable
{
  public static final Parcelable.Creator<TvStreamConfig> CREATOR = new Parcelable.Creator()
  {
    public TvStreamConfig createFromParcel(Parcel paramAnonymousParcel)
    {
      try
      {
        paramAnonymousParcel = new TvStreamConfig.Builder().streamId(paramAnonymousParcel.readInt()).type(paramAnonymousParcel.readInt()).maxWidth(paramAnonymousParcel.readInt()).maxHeight(paramAnonymousParcel.readInt()).generation(paramAnonymousParcel.readInt()).build();
        return paramAnonymousParcel;
      }
      catch (Exception paramAnonymousParcel)
      {
        Log.e(TvStreamConfig.TAG, "Exception creating TvStreamConfig from parcel", paramAnonymousParcel);
      }
      return null;
    }
    
    public TvStreamConfig[] newArray(int paramAnonymousInt)
    {
      return new TvStreamConfig[paramAnonymousInt];
    }
  };
  public static final int STREAM_TYPE_BUFFER_PRODUCER = 2;
  public static final int STREAM_TYPE_INDEPENDENT_VIDEO_SOURCE = 1;
  static final String TAG = TvStreamConfig.class.getSimpleName();
  private int mGeneration;
  private int mMaxHeight;
  private int mMaxWidth;
  private int mStreamId;
  private int mType;
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool2 = false;
    if (paramObject == null) {
      return false;
    }
    if (!(paramObject instanceof TvStreamConfig)) {
      return false;
    }
    paramObject = (TvStreamConfig)paramObject;
    boolean bool1 = bool2;
    if (((TvStreamConfig)paramObject).mGeneration == this.mGeneration)
    {
      bool1 = bool2;
      if (((TvStreamConfig)paramObject).mStreamId == this.mStreamId)
      {
        bool1 = bool2;
        if (((TvStreamConfig)paramObject).mType == this.mType)
        {
          bool1 = bool2;
          if (((TvStreamConfig)paramObject).mMaxWidth == this.mMaxWidth)
          {
            bool1 = bool2;
            if (((TvStreamConfig)paramObject).mMaxHeight == this.mMaxHeight) {
              bool1 = true;
            }
          }
        }
      }
    }
    return bool1;
  }
  
  public int getGeneration()
  {
    return this.mGeneration;
  }
  
  public int getMaxHeight()
  {
    return this.mMaxHeight;
  }
  
  public int getMaxWidth()
  {
    return this.mMaxWidth;
  }
  
  public int getStreamId()
  {
    return this.mStreamId;
  }
  
  public int getType()
  {
    return this.mType;
  }
  
  public String toString()
  {
    return "TvStreamConfig {mStreamId=" + this.mStreamId + ";" + "mType=" + this.mType + ";mGeneration=" + this.mGeneration + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mStreamId);
    paramParcel.writeInt(this.mType);
    paramParcel.writeInt(this.mMaxWidth);
    paramParcel.writeInt(this.mMaxHeight);
    paramParcel.writeInt(this.mGeneration);
  }
  
  public static final class Builder
  {
    private Integer mGeneration;
    private Integer mMaxHeight;
    private Integer mMaxWidth;
    private Integer mStreamId;
    private Integer mType;
    
    public TvStreamConfig build()
    {
      if ((this.mStreamId == null) || (this.mType == null)) {}
      while ((this.mMaxWidth == null) || (this.mMaxHeight == null) || (this.mGeneration == null)) {
        throw new UnsupportedOperationException();
      }
      TvStreamConfig localTvStreamConfig = new TvStreamConfig(null);
      TvStreamConfig.-set3(localTvStreamConfig, this.mStreamId.intValue());
      TvStreamConfig.-set4(localTvStreamConfig, this.mType.intValue());
      TvStreamConfig.-set2(localTvStreamConfig, this.mMaxWidth.intValue());
      TvStreamConfig.-set1(localTvStreamConfig, this.mMaxHeight.intValue());
      TvStreamConfig.-set0(localTvStreamConfig, this.mGeneration.intValue());
      return localTvStreamConfig;
    }
    
    public Builder generation(int paramInt)
    {
      this.mGeneration = Integer.valueOf(paramInt);
      return this;
    }
    
    public Builder maxHeight(int paramInt)
    {
      this.mMaxHeight = Integer.valueOf(paramInt);
      return this;
    }
    
    public Builder maxWidth(int paramInt)
    {
      this.mMaxWidth = Integer.valueOf(paramInt);
      return this;
    }
    
    public Builder streamId(int paramInt)
    {
      this.mStreamId = Integer.valueOf(paramInt);
      return this;
    }
    
    public Builder type(int paramInt)
    {
      this.mType = Integer.valueOf(paramInt);
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/TvStreamConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */