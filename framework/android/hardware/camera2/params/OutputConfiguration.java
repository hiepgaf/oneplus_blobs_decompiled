package android.hardware.camera2.params;

import android.graphics.SurfaceTexture;
import android.hardware.camera2.utils.HashCodeHelpers;
import android.hardware.camera2.utils.SurfaceUtils;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.android.internal.util.Preconditions;

public final class OutputConfiguration
  implements Parcelable
{
  public static final Parcelable.Creator<OutputConfiguration> CREATOR = new Parcelable.Creator()
  {
    public OutputConfiguration createFromParcel(Parcel paramAnonymousParcel)
    {
      try
      {
        paramAnonymousParcel = new OutputConfiguration(paramAnonymousParcel, null);
        return paramAnonymousParcel;
      }
      catch (Exception paramAnonymousParcel)
      {
        Log.e("OutputConfiguration", "Exception creating OutputConfiguration from parcel", paramAnonymousParcel);
      }
      return null;
    }
    
    public OutputConfiguration[] newArray(int paramAnonymousInt)
    {
      return new OutputConfiguration[paramAnonymousInt];
    }
  };
  public static final int ROTATION_0 = 0;
  public static final int ROTATION_180 = 2;
  public static final int ROTATION_270 = 3;
  public static final int ROTATION_90 = 1;
  public static final int SURFACE_GROUP_ID_NONE = -1;
  private static final String TAG = "OutputConfiguration";
  private final int SURFACE_TYPE_SURFACE_TEXTURE = 1;
  private final int SURFACE_TYPE_SURFACE_VIEW = 0;
  private final int SURFACE_TYPE_UNKNOWN = -1;
  private final int mConfiguredDataspace;
  private final int mConfiguredFormat;
  private final int mConfiguredGenerationId;
  private final Size mConfiguredSize;
  private final boolean mIsDeferredConfig;
  private final int mRotation;
  private Surface mSurface;
  private final int mSurfaceGroupId;
  private final int mSurfaceType;
  
  public OutputConfiguration(int paramInt, Surface paramSurface)
  {
    this(paramInt, paramSurface, 0);
  }
  
  public OutputConfiguration(int paramInt1, Surface paramSurface, int paramInt2)
  {
    Preconditions.checkNotNull(paramSurface, "Surface must not be null");
    Preconditions.checkArgumentInRange(paramInt2, 0, 3, "Rotation constant");
    this.mSurfaceGroupId = paramInt1;
    this.mSurfaceType = -1;
    this.mSurface = paramSurface;
    this.mRotation = paramInt2;
    this.mConfiguredSize = SurfaceUtils.getSurfaceSize(paramSurface);
    this.mConfiguredFormat = SurfaceUtils.getSurfaceFormat(paramSurface);
    this.mConfiguredDataspace = SurfaceUtils.getSurfaceDataspace(paramSurface);
    this.mConfiguredGenerationId = paramSurface.getGenerationId();
    this.mIsDeferredConfig = false;
  }
  
  public OutputConfiguration(OutputConfiguration paramOutputConfiguration)
  {
    if (paramOutputConfiguration == null) {
      throw new IllegalArgumentException("OutputConfiguration shouldn't be null");
    }
    this.mSurface = paramOutputConfiguration.mSurface;
    this.mRotation = paramOutputConfiguration.mRotation;
    this.mSurfaceGroupId = paramOutputConfiguration.mSurfaceGroupId;
    this.mSurfaceType = paramOutputConfiguration.mSurfaceType;
    this.mConfiguredDataspace = paramOutputConfiguration.mConfiguredDataspace;
    this.mConfiguredFormat = paramOutputConfiguration.mConfiguredFormat;
    this.mConfiguredSize = paramOutputConfiguration.mConfiguredSize;
    this.mConfiguredGenerationId = paramOutputConfiguration.mConfiguredGenerationId;
    this.mIsDeferredConfig = paramOutputConfiguration.mIsDeferredConfig;
  }
  
  private OutputConfiguration(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    int j = paramParcel.readInt();
    int k = paramParcel.readInt();
    int m = paramParcel.readInt();
    int n = paramParcel.readInt();
    paramParcel = (Surface)Surface.CREATOR.createFromParcel(paramParcel);
    Preconditions.checkArgumentInRange(i, 0, 3, "Rotation constant");
    this.mSurfaceGroupId = j;
    this.mSurface = paramParcel;
    this.mRotation = i;
    if (paramParcel != null)
    {
      this.mSurfaceType = -1;
      this.mConfiguredSize = SurfaceUtils.getSurfaceSize(this.mSurface);
      this.mConfiguredFormat = SurfaceUtils.getSurfaceFormat(this.mSurface);
      this.mConfiguredDataspace = SurfaceUtils.getSurfaceDataspace(this.mSurface);
      this.mConfiguredGenerationId = this.mSurface.getGenerationId();
      this.mIsDeferredConfig = true;
      return;
    }
    this.mSurfaceType = k;
    this.mConfiguredSize = new Size(m, n);
    this.mConfiguredFormat = StreamConfigurationMap.imageFormatToInternal(34);
    this.mConfiguredGenerationId = 0;
    this.mConfiguredDataspace = StreamConfigurationMap.imageFormatToDataspace(34);
    this.mIsDeferredConfig = false;
  }
  
  public <T> OutputConfiguration(Size paramSize, Class<T> paramClass)
  {
    Preconditions.checkNotNull(paramClass, "surfaceSize must not be null");
    Preconditions.checkNotNull(paramClass, "klass must not be null");
    if (paramClass == SurfaceHolder.class) {}
    for (this.mSurfaceType = 0;; this.mSurfaceType = 1)
    {
      this.mSurfaceGroupId = -1;
      this.mSurface = null;
      this.mRotation = 0;
      this.mConfiguredSize = paramSize;
      this.mConfiguredFormat = StreamConfigurationMap.imageFormatToInternal(34);
      this.mConfiguredDataspace = StreamConfigurationMap.imageFormatToDataspace(34);
      this.mConfiguredGenerationId = 0;
      this.mIsDeferredConfig = true;
      return;
      if (paramClass != SurfaceTexture.class) {
        break;
      }
    }
    this.mSurfaceType = -1;
    throw new IllegalArgumentException("Unknow surface source class type");
  }
  
  public OutputConfiguration(Surface paramSurface)
  {
    this(-1, paramSurface, 0);
  }
  
  public OutputConfiguration(Surface paramSurface, int paramInt)
  {
    this(-1, paramSurface, paramInt);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    if (this == paramObject) {
      return true;
    }
    if ((paramObject instanceof OutputConfiguration))
    {
      paramObject = (OutputConfiguration)paramObject;
      int i;
      if (this.mSurface == ((OutputConfiguration)paramObject).mSurface) {
        if (this.mConfiguredGenerationId == ((OutputConfiguration)paramObject).mConfiguredGenerationId) {
          i = 1;
        }
      }
      for (;;)
      {
        if (this.mIsDeferredConfig)
        {
          Log.i("OutputConfiguration", "deferred config has the same surface");
          i = 1;
        }
        if ((this.mRotation != ((OutputConfiguration)paramObject).mRotation) || (i == 0) || (!this.mConfiguredSize.equals(((OutputConfiguration)paramObject).mConfiguredSize)) || (this.mConfiguredFormat != ((OutputConfiguration)paramObject).mConfiguredFormat) || (this.mConfiguredDataspace != ((OutputConfiguration)paramObject).mConfiguredDataspace) || (this.mSurfaceGroupId != ((OutputConfiguration)paramObject).mSurfaceGroupId) || (this.mSurfaceType != ((OutputConfiguration)paramObject).mSurfaceType)) {
          break label164;
        }
        if (this.mIsDeferredConfig != ((OutputConfiguration)paramObject).mIsDeferredConfig) {
          break;
        }
        return true;
        i = 0;
        continue;
        i = 0;
      }
      return false;
      label164:
      return false;
    }
    return false;
  }
  
  public int getRotation()
  {
    return this.mRotation;
  }
  
  public Surface getSurface()
  {
    return this.mSurface;
  }
  
  public int getSurfaceGroupId()
  {
    return this.mSurfaceGroupId;
  }
  
  public int hashCode()
  {
    if (this.mIsDeferredConfig) {
      return HashCodeHelpers.hashCode(new int[] { this.mRotation, this.mConfiguredSize.hashCode(), this.mConfiguredFormat, this.mConfiguredDataspace, this.mSurfaceGroupId, this.mSurfaceType });
    }
    return HashCodeHelpers.hashCode(new int[] { this.mRotation, this.mSurface.hashCode(), this.mConfiguredGenerationId, this.mConfiguredSize.hashCode(), this.mConfiguredFormat, this.mConfiguredDataspace, this.mSurfaceGroupId });
  }
  
  public boolean isDeferredConfiguration()
  {
    return this.mIsDeferredConfig;
  }
  
  public void setDeferredSurface(Surface paramSurface)
  {
    Preconditions.checkNotNull(paramSurface, "Surface must not be null");
    if (this.mSurface != null) {
      throw new IllegalStateException("Deferred surface is already set!");
    }
    Size localSize = SurfaceUtils.getSurfaceSize(paramSurface);
    if (!localSize.equals(this.mConfiguredSize)) {
      Log.w("OutputConfiguration", "Deferred surface size " + localSize + " is different with pre-configured size " + this.mConfiguredSize + ", the pre-configured size will be used.");
    }
    this.mSurface = paramSurface;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (paramParcel == null) {
      throw new IllegalArgumentException("dest must not be null");
    }
    paramParcel.writeInt(this.mRotation);
    paramParcel.writeInt(this.mSurfaceGroupId);
    paramParcel.writeInt(this.mSurfaceType);
    paramParcel.writeInt(this.mConfiguredSize.getWidth());
    paramParcel.writeInt(this.mConfiguredSize.getHeight());
    if (this.mSurface != null) {
      this.mSurface.writeToParcel(paramParcel, paramInt);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/params/OutputConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */