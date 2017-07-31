package com.amap.api.mapcore2d;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import com.amap.api.maps2d.model.TileOverlayOptions;

public class bq
  implements aj
{
  private static int a = 0;
  private br b;
  private am c;
  private boolean d;
  private String e;
  private float f;
  
  public bq(TileOverlayOptions paramTileOverlayOptions, br parambr, au paramau, ay paramay, Context paramContext)
  {
    this.b = parambr;
    this.c = new am(paramau);
    this.c.e = false;
    this.c.g = false;
    this.c.f = paramTileOverlayOptions.getDiskCacheEnabled();
    this.c.p = new bk();
    this.c.k = paramTileOverlayOptions.getTileProvider();
    this.c.n = new az(paramay.e.e, paramay.e.f, false, 0L, this.c);
    paramau = paramTileOverlayOptions.getDiskCacheDir();
    if (!TextUtils.isEmpty(paramau)) {}
    for (;;)
    {
      this.c.m = paramau;
      this.c.o = new s(parambr.getContext(), false, this.c);
      parambr = new bs(paramay, paramContext, this.c);
      this.c.q = parambr;
      this.c.a(true);
      this.d = paramTileOverlayOptions.isVisible();
      this.e = c();
      this.f = paramTileOverlayOptions.getZIndex();
      return;
      this.c.f = false;
    }
  }
  
  private static String a(String paramString)
  {
    a += 1;
    return paramString + a;
  }
  
  public void a()
  {
    try
    {
      this.b.b(this);
      this.c.b();
      this.c.q.b();
      return;
    }
    catch (Throwable localThrowable)
    {
      cj.a(localThrowable, "TileOverlayDelegateImp", "remove");
    }
  }
  
  public void a(float paramFloat)
  {
    this.f = paramFloat;
  }
  
  public void a(Canvas paramCanvas)
  {
    this.c.a(paramCanvas);
  }
  
  public void a(boolean paramBoolean)
  {
    this.d = paramBoolean;
    this.c.a(paramBoolean);
  }
  
  public boolean a(aj paramaj)
  {
    return false;
  }
  
  public void b()
  {
    try
    {
      this.c.b();
      return;
    }
    catch (Throwable localThrowable)
    {
      cj.a(localThrowable, "TileOverlayDelegateImp", "remove");
    }
  }
  
  public void b(boolean paramBoolean) {}
  
  public String c()
  {
    if (this.e != null) {}
    for (;;)
    {
      return this.e;
      this.e = a("TileOverlay");
    }
  }
  
  public float d()
  {
    return this.f;
  }
  
  public boolean e()
  {
    return this.d;
  }
  
  public int f()
  {
    return 0;
  }
  
  public void g()
  {
    this.c.q.c();
  }
  
  public void h()
  {
    this.c.q.d();
  }
  
  public void i()
  {
    this.c.q.b();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bq.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */