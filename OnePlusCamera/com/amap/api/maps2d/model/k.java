package com.amap.api.maps2d.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;

class k
  implements Parcelable.Creator<TileOverlayOptions>
{
  public TileOverlayOptions a(Parcel paramParcel)
  {
    boolean bool3 = false;
    int i = paramParcel.readInt();
    TileProvider localTileProvider = (TileProvider)paramParcel.readValue(TileProvider.class.getClassLoader());
    boolean bool1;
    int j;
    int k;
    String str;
    boolean bool2;
    if (paramParcel.readByte() == 0)
    {
      bool1 = false;
      float f = paramParcel.readFloat();
      j = paramParcel.readInt();
      k = paramParcel.readInt();
      str = paramParcel.readString();
      if (paramParcel.readByte() != 0) {
        break label133;
      }
      bool2 = false;
      label65:
      if (paramParcel.readByte() != 0) {
        break label139;
      }
      label72:
      paramParcel = new TileOverlayOptions(i, null, bool1, f);
      if (localTileProvider != null) {
        break label145;
      }
    }
    for (;;)
    {
      paramParcel.memCacheSize(j);
      paramParcel.diskCacheSize(k);
      paramParcel.diskCacheDir(str);
      paramParcel.memoryCacheEnabled(bool2);
      paramParcel.diskCacheEnabled(bool3);
      return paramParcel;
      bool1 = true;
      break;
      label133:
      bool2 = true;
      break label65;
      label139:
      bool3 = true;
      break label72;
      label145:
      paramParcel.tileProvider(localTileProvider);
    }
  }
  
  public TileOverlayOptions[] a(int paramInt)
  {
    return new TileOverlayOptions[paramInt];
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/k.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */