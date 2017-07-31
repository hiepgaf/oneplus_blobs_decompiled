package com.amap.api.maps2d.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

public final class TileOverlayOptions
  implements Parcelable
{
  public static final k CREATOR = new k();
  private final int a;
  private TileProvider b;
  private boolean c = true;
  private float d;
  private int e = 5120;
  private int f = 20480;
  private String g = null;
  private boolean h = true;
  private boolean i = true;
  
  public TileOverlayOptions()
  {
    this.a = 1;
  }
  
  TileOverlayOptions(int paramInt, IBinder paramIBinder, boolean paramBoolean, float paramFloat)
  {
    this.a = paramInt;
    this.c = paramBoolean;
    this.d = paramFloat;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public TileOverlayOptions diskCacheDir(String paramString)
  {
    this.g = paramString;
    return this;
  }
  
  public TileOverlayOptions diskCacheEnabled(boolean paramBoolean)
  {
    this.i = paramBoolean;
    return this;
  }
  
  public TileOverlayOptions diskCacheSize(int paramInt)
  {
    this.f = (paramInt * 1024);
    return this;
  }
  
  public String getDiskCacheDir()
  {
    return this.g;
  }
  
  public boolean getDiskCacheEnabled()
  {
    return this.i;
  }
  
  public int getDiskCacheSize()
  {
    return this.f;
  }
  
  public int getMemCacheSize()
  {
    return this.e;
  }
  
  public boolean getMemoryCacheEnabled()
  {
    return this.h;
  }
  
  public TileProvider getTileProvider()
  {
    return this.b;
  }
  
  public float getZIndex()
  {
    return this.d;
  }
  
  public boolean isVisible()
  {
    return this.c;
  }
  
  public TileOverlayOptions memCacheSize(int paramInt)
  {
    this.e = paramInt;
    return this;
  }
  
  public TileOverlayOptions memoryCacheEnabled(boolean paramBoolean)
  {
    this.h = paramBoolean;
    return this;
  }
  
  public TileOverlayOptions tileProvider(TileProvider paramTileProvider)
  {
    this.b = paramTileProvider;
    return this;
  }
  
  public TileOverlayOptions visible(boolean paramBoolean)
  {
    this.c = paramBoolean;
    return this;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int j = 0;
    paramParcel.writeInt(this.a);
    paramParcel.writeValue(this.b);
    if (!this.c)
    {
      paramInt = 0;
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeFloat(this.d);
      paramParcel.writeInt(this.e);
      paramParcel.writeInt(this.f);
      paramParcel.writeString(this.g);
      if (this.h) {
        break label101;
      }
      paramInt = 0;
      label74:
      paramParcel.writeByte((byte)paramInt);
      if (this.i) {
        break label106;
      }
    }
    label101:
    label106:
    for (paramInt = j;; paramInt = 1)
    {
      paramParcel.writeByte((byte)paramInt);
      return;
      paramInt = 1;
      break;
      paramInt = 1;
      break label74;
    }
  }
  
  public TileOverlayOptions zIndex(float paramFloat)
  {
    this.d = paramFloat;
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/TileOverlayOptions.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */