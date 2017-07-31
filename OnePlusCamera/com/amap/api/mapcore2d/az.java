package com.amap.api.mapcore2d;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import java.util.List;

class az
{
  protected final a[] a;
  protected final int b;
  protected final int c;
  protected final a[] d;
  private boolean e = false;
  private long f = 0L;
  private am g;
  private Paint h = null;
  private Path i = null;
  
  public az(int paramInt1, int paramInt2, boolean paramBoolean, long paramLong, am paramam)
  {
    this.b = paramInt1;
    this.c = paramInt2;
    this.g = paramam;
    this.e = paramBoolean;
    this.f = (1000000L * paramLong);
    if (this.b <= 0)
    {
      this.a = null;
      this.d = null;
      return;
    }
    this.a = new a[this.b];
    this.d = new a[this.c];
  }
  
  private void a(Bitmap paramBitmap, final List<bu> paramList)
  {
    paramList = new i()
    {
      public void a(Canvas paramAnonymousCanvas)
      {
        if (az.a(az.this) != null) {
          if (az.b(az.this) == null) {
            break label119;
          }
        }
        int j;
        for (;;)
        {
          int m = paramList.size();
          j = 0;
          if (j < m) {
            break label137;
          }
          return;
          az.a(az.this, new Paint());
          az.a(az.this).setStyle(Paint.Style.STROKE);
          az.a(az.this).setDither(true);
          az.a(az.this).setAntiAlias(true);
          az.a(az.this).setStrokeJoin(Paint.Join.ROUND);
          az.a(az.this).setStrokeCap(Paint.Cap.ROUND);
          break;
          label119:
          az.a(az.this, new Path());
        }
        label137:
        Object localObject = (bu)paramList.get(j);
        az.a(az.this).setStrokeWidth(3.0F);
        int i = ((bu)localObject).b();
        if (i != 1)
        {
          if (i == 2) {
            break label263;
          }
          if (i == 3) {
            break label279;
          }
        }
        int k;
        for (;;)
        {
          localObject = ((bu)localObject).a();
          int n = ((List)localObject).size();
          k = 0;
          i = 1;
          if (k < n) {
            break label294;
          }
          paramAnonymousCanvas.drawPath(az.b(az.this), az.a(az.this));
          az.b(az.this).reset();
          j += 1;
          break;
          az.a(az.this).setColor(-65536);
          continue;
          label263:
          az.a(az.this).setColor(65280);
          continue;
          label279:
          az.a(az.this).setColor(-16711936);
        }
        label294:
        PointF localPointF = (PointF)((List)localObject).get(k);
        if (i == 0) {
          az.b(az.this).lineTo(localPointF.x, localPointF.y);
        }
        for (;;)
        {
          k += 1;
          break;
          az.b(az.this).moveTo(localPointF.x, localPointF.y);
          i = 0;
        }
      }
    };
    h localh = new h(null);
    localh.a(paramBitmap);
    localh.a(paramList);
  }
  
  private long d()
  {
    return System.nanoTime();
  }
  
  protected int a()
  {
    int j = 0;
    if (j >= this.c) {
      j = 0;
    }
    int k;
    Object localObject1;
    for (;;)
    {
      if (j >= this.b)
      {
        k = -1;
        j = 0;
        if (j < this.c) {
          break label153;
        }
        return k;
        this.d[j] = null;
        j += 1;
        break;
      }
      localObject1 = this.a[j];
      k = 0;
      if (k < this.c) {
        break label73;
      }
      label66:
      j += 1;
    }
    label73:
    if (this.d[k] != null) {
      if (this.d[k].d > ((a)localObject1).d) {
        break label148;
      }
    }
    label148:
    for (int m = 1;; m = 0)
    {
      Object localObject2 = localObject1;
      if (m == 0)
      {
        localObject2 = this.d[k];
        this.d[k] = localObject1;
      }
      k += 1;
      localObject1 = localObject2;
      break;
      this.d[k] = localObject1;
      break label66;
    }
    label153:
    if (this.d[j] == null) {
      m = k;
    }
    for (;;)
    {
      j += 1;
      k = m;
      break;
      this.d[j].c = false;
      m = k;
      if (k < 0) {
        m = this.d[j].e;
      }
    }
  }
  
  protected int a(String paramString)
  {
    int k = 1;
    if (paramString == null) {}
    while (paramString.equals("") == true) {
      return -1;
    }
    int j = 0;
    for (;;)
    {
      if (j >= this.b) {
        return -1;
      }
      if ((this.a[j] != null) && (this.a[j].b.equals(paramString)))
      {
        if (this.a[j].c) {
          if (this.e == true) {
            break label104;
          }
        }
        while (this.a[j].a != null)
        {
          this.a[j].d = d();
          return j;
          return -1;
          label104:
          if (d() - this.a[j].f <= this.f) {}
          while (k == 0)
          {
            this.a[j].c = false;
            return -1;
            k = 0;
          }
        }
        return -1;
      }
      j += 1;
    }
  }
  
  protected int a(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, boolean paramBoolean, List<bu> paramList, String paramString)
  {
    if (paramArrayOfByte1 != null) {}
    for (;;)
    {
      int j;
      try
      {
        j = b();
        if (j >= 0)
        {
          if (j >= 0)
          {
            if (this.a == null) {
              continue;
            }
            if (this.a[j] != null) {
              continue;
            }
            if (this.a[j].g != null) {
              break label176;
            }
            break label417;
            if (this.a[j].a == null) {
              break label336;
            }
            label68:
            paramArrayOfByte1 = this.a[j];
            if (paramArrayOfByte1 != null) {
              break label355;
            }
            label80:
            return j;
            if ((paramArrayOfByte2 != null) || (paramList != null)) {
              continue;
            }
            return -1;
          }
        }
        else
        {
          j = a();
          continue;
        }
        return -1;
        return -1;
        if ((this.a[j].a == null) || (this.a[j].a.isRecycled())) {
          continue;
        }
        this.a[j].a.recycle();
        this.a[j].a = null;
        continue;
        this.a[j].g.clear();
      }
      finally {}
      label176:
      this.a[j].g = null;
      break label417;
      label205:
      if (paramArrayOfByte1 != null)
      {
        try
        {
          this.a[j].a = BitmapFactory.decodeByteArray(paramArrayOfByte1, 0, paramArrayOfByte1.length);
        }
        catch (OutOfMemoryError paramArrayOfByte1) {}catch (Throwable paramArrayOfByte1)
        {
          cj.a(paramArrayOfByte1, "BitmapManager", "setBitmapData");
        }
        try
        {
          label245:
          this.a[j].a = BitmapFactory.decodeByteArray(paramArrayOfByte2, 0, paramArrayOfByte2.length);
        }
        catch (OutOfMemoryError paramArrayOfByte1) {}catch (Throwable paramArrayOfByte1)
        {
          cj.a(paramArrayOfByte1, "BitmapManager", "setBitmapData");
        }
      }
      label336:
      label355:
      label417:
      while (paramList != null)
      {
        this.a[j].a = Bitmap.createBitmap(this.g.a.a, this.g.a.a, Bitmap.Config.ARGB_4444);
        a(this.a[j].a, paramList);
        break;
        paramArrayOfByte1 = this.a[j].g;
        if (paramArrayOfByte1 != null) {
          break label68;
        }
        return -1;
        this.a[j].c = true;
        this.a[j].b = paramString;
        this.a[j].d = d();
        if (this.e != true) {
          break label80;
        }
        this.a[j].f = d();
        break label80;
        if (paramBoolean == true) {
          break label205;
        }
        if (paramArrayOfByte2 != null) {
          break label245;
        }
      }
    }
  }
  
  protected Bitmap a(int paramInt)
  {
    if (paramInt < 0) {}
    while (paramInt >= this.b) {
      return null;
    }
    if (this.a[paramInt] != null) {
      return this.a[paramInt].a;
    }
    return null;
  }
  
  protected int b()
  {
    int j = 0;
    int k = -1;
    if (j >= this.b) {
      return k;
    }
    int m;
    if (this.a[j] != null)
    {
      if (!this.a[j].c) {
        break label72;
      }
      m = k;
    }
    for (;;)
    {
      j += 1;
      k = m;
      break;
      this.a[j] = new a();
      this.a[j].e = j;
      return j;
      label72:
      m = k;
      if (k < 0) {
        m = j;
      }
    }
  }
  
  protected void c()
  {
    int j = 0;
    if (j >= this.b) {
      return;
    }
    if (this.a[j] != null) {
      if (this.a[j].a != null) {
        break label49;
      }
    }
    for (;;)
    {
      this.a[j].a = null;
      j += 1;
      break;
      label49:
      if (!this.a[j].a.isRecycled()) {
        this.a[j].a.recycle();
      }
    }
  }
  
  private class a
  {
    Bitmap a = null;
    String b = "";
    boolean c = false;
    long d = 0L;
    int e = -1;
    long f = 0L;
    List<bu> g = null;
    
    public a() {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/az.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */