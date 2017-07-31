package com.amap.api.maps2d;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.amap.api.maps2d.model.CameraPosition;

public class AMapOptionsCreator
  implements Parcelable.Creator<AMapOptions>
{
  public AMapOptions createFromParcel(Parcel paramParcel)
  {
    AMapOptions localAMapOptions = new AMapOptions();
    CameraPosition localCameraPosition = (CameraPosition)paramParcel.readParcelable(CameraPosition.class.getClassLoader());
    localAMapOptions.mapType(paramParcel.readInt());
    localAMapOptions.camera(localCameraPosition);
    paramParcel = paramParcel.createBooleanArray();
    if (paramParcel == null) {}
    while (paramParcel.length < 6) {
      return localAMapOptions;
    }
    localAMapOptions.scrollGesturesEnabled(paramParcel[0]);
    localAMapOptions.zoomGesturesEnabled(paramParcel[1]);
    localAMapOptions.zoomControlsEnabled(paramParcel[2]);
    localAMapOptions.zOrderOnTop(paramParcel[3]);
    localAMapOptions.compassEnabled(paramParcel[4]);
    localAMapOptions.scaleControlsEnabled(paramParcel[5]);
    return localAMapOptions;
  }
  
  public AMapOptions[] newArray(int paramInt)
  {
    return new AMapOptions[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/AMapOptionsCreator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */