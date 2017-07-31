package android.app;

import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ProfilerInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ProfilerInfo> CREATOR = new Parcelable.Creator()
  {
    public ProfilerInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ProfilerInfo(paramAnonymousParcel, null);
    }
    
    public ProfilerInfo[] newArray(int paramAnonymousInt)
    {
      return new ProfilerInfo[paramAnonymousInt];
    }
  };
  public final boolean autoStopProfiler;
  public ParcelFileDescriptor profileFd;
  public final String profileFile;
  public final int samplingInterval;
  
  private ProfilerInfo(Parcel paramParcel)
  {
    this.profileFile = paramParcel.readString();
    ParcelFileDescriptor localParcelFileDescriptor;
    if (paramParcel.readInt() != 0)
    {
      localParcelFileDescriptor = (ParcelFileDescriptor)ParcelFileDescriptor.CREATOR.createFromParcel(paramParcel);
      this.profileFd = localParcelFileDescriptor;
      this.samplingInterval = paramParcel.readInt();
      if (paramParcel.readInt() == 0) {
        break label65;
      }
    }
    label65:
    for (boolean bool = true;; bool = false)
    {
      this.autoStopProfiler = bool;
      return;
      localParcelFileDescriptor = null;
      break;
    }
  }
  
  public ProfilerInfo(String paramString, ParcelFileDescriptor paramParcelFileDescriptor, int paramInt, boolean paramBoolean)
  {
    this.profileFile = paramString;
    this.profileFd = paramParcelFileDescriptor;
    this.samplingInterval = paramInt;
    this.autoStopProfiler = paramBoolean;
  }
  
  public int describeContents()
  {
    if (this.profileFd != null) {
      return this.profileFd.describeContents();
    }
    return 0;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeString(this.profileFile);
    if (this.profileFd != null)
    {
      paramParcel.writeInt(1);
      this.profileFd.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.samplingInterval);
      if (!this.autoStopProfiler) {
        break label62;
      }
    }
    label62:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramParcel.writeInt(0);
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/ProfilerInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */