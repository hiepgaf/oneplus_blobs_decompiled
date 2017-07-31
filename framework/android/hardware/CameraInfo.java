package android.hardware;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class CameraInfo
  implements Parcelable
{
  public static final Parcelable.Creator<CameraInfo> CREATOR = new Parcelable.Creator()
  {
    public CameraInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      CameraInfo localCameraInfo = new CameraInfo();
      localCameraInfo.readFromParcel(paramAnonymousParcel);
      return localCameraInfo;
    }
    
    public CameraInfo[] newArray(int paramAnonymousInt)
    {
      return new CameraInfo[paramAnonymousInt];
    }
  };
  public Camera.CameraInfo info = new Camera.CameraInfo();
  
  public int describeContents()
  {
    return 0;
  }
  
  public void readFromParcel(Parcel paramParcel)
  {
    this.info.facing = paramParcel.readInt();
    this.info.orientation = paramParcel.readInt();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.info.facing);
    paramParcel.writeInt(this.info.orientation);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/CameraInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */