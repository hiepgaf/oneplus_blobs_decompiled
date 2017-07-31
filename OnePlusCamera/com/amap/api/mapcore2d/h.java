package com.amap.api.mapcore2d;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;

class h
{
  private Bitmap a = null;
  private Canvas b = null;
  private Bitmap.Config c;
  
  public h(Bitmap.Config paramConfig)
  {
    this.c = paramConfig;
  }
  
  public void a(Bitmap paramBitmap)
  {
    this.a = paramBitmap;
    this.b = new Canvas(this.a);
  }
  
  public void a(i parami)
  {
    this.b.save(1);
    parami.a(this.b);
    this.b.restore();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/h.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */