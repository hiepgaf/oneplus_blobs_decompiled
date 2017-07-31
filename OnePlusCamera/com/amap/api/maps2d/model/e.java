package com.amap.api.maps2d.model;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.amap.api.mapcore2d.cj;

class e
  implements Parcelable.Creator<LatLngBounds>
{
  static void a(LatLngBounds paramLatLngBounds, Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(paramLatLngBounds.a());
    paramParcel.writeParcelable(paramLatLngBounds.southwest, paramInt);
    paramParcel.writeParcelable(paramLatLngBounds.northeast, paramInt);
  }
  
  public LatLngBounds a(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    for (;;)
    {
      try
      {
        LatLng localLatLng = (LatLng)paramParcel.readParcelable(LatLngBounds.class.getClassLoader());
        cj.a(localBadParcelableException1, "LatLngBoundsCreator", "createFromParcel");
      }
      catch (BadParcelableException localBadParcelableException1)
      {
        try
        {
          paramParcel = (LatLng)paramParcel.readParcelable(LatLngBounds.class.getClassLoader());
          return new LatLngBounds(i, localLatLng, paramParcel);
        }
        catch (BadParcelableException localBadParcelableException2)
        {
          for (;;)
          {
            Object localObject2;
            paramParcel = (Parcel)localObject1;
            Object localObject1 = localBadParcelableException2;
          }
        }
        localBadParcelableException1 = localBadParcelableException1;
        paramParcel = null;
      }
      localObject2 = null;
      localObject1 = paramParcel;
      paramParcel = (Parcel)localObject2;
    }
  }
  
  public LatLngBounds[] a(int paramInt)
  {
    return new LatLngBounds[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/e.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */