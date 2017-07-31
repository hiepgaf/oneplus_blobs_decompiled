package com.amap.api.maps2d.model;

import android.os.BadParcelableException;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.amap.api.mapcore2d.cj;

class l
  implements Parcelable.Creator<VisibleRegion>
{
  static void a(VisibleRegion paramVisibleRegion, Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(paramVisibleRegion.a());
    paramParcel.writeParcelable(paramVisibleRegion.nearLeft, paramInt);
    paramParcel.writeParcelable(paramVisibleRegion.nearRight, paramInt);
    paramParcel.writeParcelable(paramVisibleRegion.farLeft, paramInt);
    paramParcel.writeParcelable(paramVisibleRegion.farRight, paramInt);
    paramParcel.writeParcelable(paramVisibleRegion.latLngBounds, paramInt);
  }
  
  public VisibleRegion a(Parcel paramParcel)
  {
    Object localObject2 = null;
    int i = paramParcel.readInt();
    for (;;)
    {
      try
      {
        LatLng localLatLng1 = (LatLng)paramParcel.readParcelable(LatLng.class.getClassLoader());
        cj.a(localBadParcelableException1, "VisibleRegionCreator", "createFromParcel");
      }
      catch (BadParcelableException localBadParcelableException1)
      {
        try
        {
          localLatLng3 = (LatLng)paramParcel.readParcelable(LatLng.class.getClassLoader());
        }
        catch (BadParcelableException localBadParcelableException2)
        {
          for (;;)
          {
            paramParcel = null;
            localLatLng2 = null;
            LatLng localLatLng3 = null;
          }
        }
        try
        {
          localLatLng2 = (LatLng)paramParcel.readParcelable(LatLng.class.getClassLoader());
        }
        catch (BadParcelableException localBadParcelableException3)
        {
          paramParcel = null;
          localLatLng2 = null;
          break label105;
        }
        try
        {
          localObject1 = (LatLng)paramParcel.readParcelable(LatLng.class.getClassLoader());
        }
        catch (BadParcelableException localBadParcelableException4)
        {
          paramParcel = null;
          break label105;
        }
        try
        {
          paramParcel = (LatLngBounds)paramParcel.readParcelable(LatLngBounds.class.getClassLoader());
          return new VisibleRegion(i, localLatLng1, localLatLng3, localLatLng2, (LatLng)localObject1, paramParcel);
        }
        catch (BadParcelableException localBadParcelableException5)
        {
          paramParcel = (Parcel)localObject1;
          break label105;
        }
        localBadParcelableException1 = localBadParcelableException1;
        paramParcel = null;
        localLatLng2 = null;
        localLatLng3 = null;
        localLatLng1 = null;
      }
      label105:
      localObject1 = paramParcel;
      paramParcel = (Parcel)localObject2;
    }
  }
  
  public VisibleRegion[] a(int paramInt)
  {
    return new VisibleRegion[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/l.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */