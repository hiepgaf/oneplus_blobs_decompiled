package com.amap.api.maps2d.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;

class g
  implements Parcelable.Creator<MarkerOptions>
{
  public MarkerOptions a(Parcel paramParcel)
  {
    boolean bool2 = false;
    MarkerOptions localMarkerOptions = new MarkerOptions();
    localMarkerOptions.position((LatLng)paramParcel.readParcelable(LatLng.class.getClassLoader()));
    localMarkerOptions.icon((BitmapDescriptor)paramParcel.readParcelable(BitmapDescriptor.class.getClassLoader()));
    localMarkerOptions.title(paramParcel.readString());
    localMarkerOptions.snippet(paramParcel.readString());
    localMarkerOptions.anchor(paramParcel.readFloat(), paramParcel.readFloat());
    if (paramParcel.readByte() != 1)
    {
      bool1 = false;
      localMarkerOptions.visible(bool1);
      if (paramParcel.readByte() == 1) {
        break label172;
      }
      bool1 = false;
      label108:
      localMarkerOptions.draggable(bool1);
      if (paramParcel.readByte() == 1) {
        break label177;
      }
    }
    label172:
    label177:
    for (boolean bool1 = bool2;; bool1 = true)
    {
      localMarkerOptions.setGps(bool1);
      localMarkerOptions.a = paramParcel.readString();
      localMarkerOptions.zIndex(paramParcel.readFloat());
      localMarkerOptions.icons(paramParcel.createTypedArrayList(BitmapDescriptor.a));
      return localMarkerOptions;
      bool1 = true;
      break;
      bool1 = true;
      break label108;
    }
  }
  
  public MarkerOptions[] a(int paramInt)
  {
    return new MarkerOptions[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/g.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */