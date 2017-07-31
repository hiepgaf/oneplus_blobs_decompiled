package com.amap.api.mapcore2d;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.amap.api.maps2d.model.LatLng;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

class as
  extends View
{
  b a;
  a b = new a();
  private ArrayList<ai> c = new ArrayList(8);
  private ArrayList<aa> d = new ArrayList(8);
  private volatile int e = 0;
  private Handler f = new Handler();
  private Runnable g = new Runnable()
  {
    public void run()
    {
      try
      {
        Collections.sort(as.a(as.this), as.this.b);
        Collections.sort(as.b(as.this), as.this.b);
        as.this.invalidate();
        return;
      }
      catch (Throwable localThrowable)
      {
        for (;;)
        {
          db.b(localThrowable, "MapOverlayImageView", "changeOverlayIndex");
        }
      }
      finally {}
    }
  };
  private ad h;
  private aa i;
  private aa j = null;
  private float k = 0.0F;
  private CopyOnWriteArrayList<Integer> l = new CopyOnWriteArrayList();
  
  public as(Context paramContext, AttributeSet paramAttributeSet, b paramb)
  {
    super(paramContext, paramAttributeSet);
    this.a = paramb;
  }
  
  private ai a(Iterator<ai> paramIterator, Rect paramRect, ad paramad)
  {
    ai localai;
    do
    {
      LatLng localLatLng;
      do
      {
        if (!paramIterator.hasNext()) {
          return null;
        }
        localai = (ai)paramIterator.next();
        localLatLng = localai.t();
      } while (localLatLng == null);
      this.a.b(localLatLng.latitude, localLatLng.longitude, paramad);
    } while (!a(paramRect, paramad.a, paramad.b));
    return localai;
  }
  
  private aa b(Iterator<aa> paramIterator, Rect paramRect, ad paramad)
  {
    aa localaa;
    do
    {
      LatLng localLatLng;
      do
      {
        if (!paramIterator.hasNext()) {
          return null;
        }
        localaa = (aa)paramIterator.next();
        localLatLng = localaa.c();
      } while (localLatLng == null);
      this.a.b(localLatLng.latitude, localLatLng.longitude, paramad);
    } while (!a(paramRect, paramad.a, paramad.b));
    return localaa;
  }
  
  private int h()
  {
    int m = this.e;
    this.e = (m + 1);
    return m;
  }
  
  private void i()
  {
    Iterator localIterator = this.d.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return;
      }
      aa localaa = (aa)localIterator.next();
      if ((this.i != null) && (this.i.d().equals(localaa.d()))) {}
      try
      {
        boolean bool = this.i.q();
        if (bool) {
          break;
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Rect localRect;
          int m;
          cj.a(localRemoteException, "MapOverlayImageView", "redrawInfoWindow");
        }
      }
      localRect = localaa.b();
      m = localRect.left;
      this.h = new ad(localaa.n() / 2 + m, localRect.top);
      this.a.u();
    }
  }
  
  public aa a(MotionEvent paramMotionEvent)
  {
    try
    {
      int m = this.d.size();
      m -= 1;
      aa localaa;
      for (;;)
      {
        if (m < 0) {
          return null;
        }
        localaa = (aa)this.d.get(m);
        boolean bool = a(localaa.b(), (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
        if (bool) {
          break;
        }
        m -= 1;
      }
      return localaa;
    }
    finally {}
  }
  
  public aa a(String paramString)
    throws RemoteException
  {
    try
    {
      Iterator localIterator = this.d.iterator();
      boolean bool;
      aa localaa;
      do
      {
        do
        {
          bool = localIterator.hasNext();
          if (!bool) {
            return null;
          }
          localaa = (aa)localIterator.next();
        } while (localaa == null);
        bool = localaa.d().equals(paramString);
      } while (!bool);
      return localaa;
    }
    finally {}
  }
  
  public b a()
  {
    return this.a;
  }
  
  public void a(Canvas paramCanvas)
  {
    Rect localRect;
    ad localad;
    Iterator localIterator1;
    Iterator localIterator2;
    aa localaa;
    ai localai;
    label84:
    label198:
    try
    {
      i();
      localRect = new Rect(0, 0, this.a.c(), this.a.d());
      localad = new ad();
      localIterator1 = this.d.iterator();
      localIterator2 = this.c.iterator();
      localaa = b(localIterator1, localRect, localad);
      localai = a(localIterator2, localRect, localad);
    }
    finally {}
    if (localaa != null)
    {
      if (localai == null) {
        break label198;
      }
      if (localaa.r() >= localai.r()) {
        break label265;
      }
    }
    label257:
    label265:
    for (int m = 1;; m = 0)
    {
      if ((m == 0) && ((localaa.r() != localai.r()) || (localaa.v() >= localai.v())))
      {
        localai.a(paramCanvas);
        localai = a(localIterator2, localRect, localad);
        break label257;
        if (localai != null) {
          break label84;
        }
        return;
        localai.a(paramCanvas);
        localai = a(localIterator2, localRect, localad);
        break label257;
        localaa.a(paramCanvas, this.a);
        localaa = b(localIterator1, localRect, localad);
        break label257;
      }
      localaa.a(paramCanvas, this.a);
      localaa = b(localIterator1, localRect, localad);
      if (localaa == null) {
        break;
      }
      break label84;
    }
  }
  
  public void a(aa paramaa)
  {
    try
    {
      e(paramaa);
      paramaa.b(h());
      this.d.remove(paramaa);
      this.d.add(paramaa);
      d();
      return;
    }
    catch (Throwable paramaa)
    {
      for (;;)
      {
        cj.a(paramaa, "MapOverlayImageView", "addMarker");
      }
    }
    finally {}
  }
  
  public void a(ai paramai)
    throws RemoteException
  {
    try
    {
      this.c.remove(paramai);
      paramai.b(h());
      this.c.add(paramai);
      d();
      return;
    }
    finally
    {
      paramai = finally;
      throw paramai;
    }
  }
  
  public boolean a(Rect paramRect, int paramInt1, int paramInt2)
  {
    return paramRect.contains(paramInt1, paramInt2);
  }
  
  protected int b()
  {
    return this.d.size();
  }
  
  public void b(ai paramai)
  {
    try
    {
      this.c.remove(paramai);
      postInvalidate();
      return;
    }
    finally
    {
      paramai = finally;
      throw paramai;
    }
  }
  
  /* Error */
  public boolean b(MotionEvent paramMotionEvent)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 47	com/amap/api/mapcore2d/as:d	Ljava/util/ArrayList;
    //   6: invokevirtual 181	java/util/ArrayList:size	()I
    //   9: istore_2
    //   10: iload_2
    //   11: iconst_1
    //   12: isub
    //   13: istore_2
    //   14: iload_2
    //   15: ifge +9 -> 24
    //   18: iconst_0
    //   19: istore_3
    //   20: aload_0
    //   21: monitorexit
    //   22: iload_3
    //   23: ireturn
    //   24: aload_0
    //   25: getfield 47	com/amap/api/mapcore2d/as:d	Ljava/util/ArrayList;
    //   28: iload_2
    //   29: invokevirtual 185	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   32: checkcast 122	com/amap/api/mapcore2d/aa
    //   35: astore 4
    //   37: aload 4
    //   39: invokeinterface 149 1 0
    //   44: astore 5
    //   46: aload_0
    //   47: aload 5
    //   49: aload_1
    //   50: invokevirtual 191	android/view/MotionEvent:getX	()F
    //   53: f2i
    //   54: aload_1
    //   55: invokevirtual 194	android/view/MotionEvent:getY	()F
    //   58: f2i
    //   59: invokevirtual 116	com/amap/api/mapcore2d/as:a	(Landroid/graphics/Rect;II)Z
    //   62: istore_3
    //   63: iload_3
    //   64: ifne +10 -> 74
    //   67: iload_2
    //   68: iconst_1
    //   69: isub
    //   70: istore_2
    //   71: goto -57 -> 14
    //   74: aload_0
    //   75: new 109	com/amap/api/mapcore2d/ad
    //   78: dup
    //   79: aload 5
    //   81: getfield 154	android/graphics/Rect:left	I
    //   84: aload 4
    //   86: invokeinterface 157 1 0
    //   91: iconst_2
    //   92: idiv
    //   93: iadd
    //   94: aload 5
    //   96: getfield 160	android/graphics/Rect:top	I
    //   99: invokespecial 163	com/amap/api/mapcore2d/ad:<init>	(II)V
    //   102: putfield 165	com/amap/api/mapcore2d/as:h	Lcom/amap/api/mapcore2d/ad;
    //   105: aload_0
    //   106: aload 4
    //   108: putfield 134	com/amap/api/mapcore2d/as:i	Lcom/amap/api/mapcore2d/aa;
    //   111: goto -91 -> 20
    //   114: astore_1
    //   115: aload_0
    //   116: monitorexit
    //   117: aload_1
    //   118: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	119	0	this	as
    //   0	119	1	paramMotionEvent	MotionEvent
    //   9	62	2	m	int
    //   19	45	3	bool	boolean
    //   35	72	4	localaa	aa
    //   44	51	5	localRect	Rect
    // Exception table:
    //   from	to	target	type
    //   2	10	114	finally
    //   24	63	114	finally
    //   74	111	114	finally
  }
  
  public boolean b(aa paramaa)
  {
    try
    {
      e(paramaa);
      boolean bool = this.d.remove(paramaa);
      postInvalidate();
      return bool;
    }
    finally
    {
      paramaa = finally;
      throw paramaa;
    }
  }
  
  public void c()
  {
    for (;;)
    {
      try
      {
        if (this.d != null) {
          continue;
        }
        localObject1 = this.c;
        if (localObject1 != null) {
          break label83;
        }
      }
      catch (Throwable localThrowable)
      {
        Object localObject1;
        cj.a(localThrowable, "MapOverlayImageView", "clear");
        continue;
      }
      finally {}
      return;
      localObject1 = this.d.iterator();
      if (!((Iterator)localObject1).hasNext())
      {
        this.d.clear();
      }
      else
      {
        ((aa)((Iterator)localObject2).next()).l();
        continue;
        label83:
        this.c.clear();
      }
    }
  }
  
  /* Error */
  public void c(aa paramaa)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 66	com/amap/api/mapcore2d/as:j	Lcom/amap/api/mapcore2d/aa;
    //   6: aload_1
    //   7: if_acmpeq +41 -> 48
    //   10: aload_0
    //   11: getfield 66	com/amap/api/mapcore2d/as:j	Lcom/amap/api/mapcore2d/aa;
    //   14: ifnonnull +37 -> 51
    //   17: aload_0
    //   18: aload_1
    //   19: invokeinterface 215 1 0
    //   24: putfield 68	com/amap/api/mapcore2d/as:k	F
    //   27: aload_0
    //   28: aload_1
    //   29: putfield 66	com/amap/api/mapcore2d/as:j	Lcom/amap/api/mapcore2d/aa;
    //   32: aload_1
    //   33: ldc_w 262
    //   36: invokeinterface 265 2 0
    //   41: aload_0
    //   42: invokevirtual 242	com/amap/api/mapcore2d/as:d	()V
    //   45: aload_0
    //   46: monitorexit
    //   47: return
    //   48: aload_0
    //   49: monitorexit
    //   50: return
    //   51: aload_0
    //   52: getfield 66	com/amap/api/mapcore2d/as:j	Lcom/amap/api/mapcore2d/aa;
    //   55: invokeinterface 215 1 0
    //   60: ldc_w 262
    //   63: fcmpl
    //   64: ifne -47 -> 17
    //   67: aload_0
    //   68: getfield 66	com/amap/api/mapcore2d/as:j	Lcom/amap/api/mapcore2d/aa;
    //   71: aload_0
    //   72: getfield 68	com/amap/api/mapcore2d/as:k	F
    //   75: invokeinterface 265 2 0
    //   80: goto -63 -> 17
    //   83: astore_1
    //   84: aload_0
    //   85: monitorexit
    //   86: aload_1
    //   87: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	as
    //   0	88	1	paramaa	aa
    // Exception table:
    //   from	to	target	type
    //   2	17	83	finally
    //   17	45	83	finally
    //   51	80	83	finally
  }
  
  void d()
  {
    this.f.removeCallbacks(this.g);
    this.f.postDelayed(this.g, 5L);
  }
  
  public void d(aa paramaa)
  {
    if (this.h != null) {}
    for (;;)
    {
      Rect localRect = paramaa.b();
      this.h = new ad(localRect.left + paramaa.n() / 2, localRect.top);
      this.i = paramaa;
      try
      {
        this.a.a(e());
        return;
      }
      catch (Throwable paramaa)
      {
        cj.a(paramaa, "MapOverlayImageView", "showInfoWindow");
      }
      this.h = new ad();
    }
  }
  
  public aa e()
  {
    return this.i;
  }
  
  public void e(aa paramaa)
  {
    if (!f(paramaa)) {
      return;
    }
    this.a.t();
  }
  
  public void f()
  {
    try
    {
      if (this.f == null) {}
      for (;;)
      {
        c();
        return;
        this.f.removeCallbacksAndMessages(null);
      }
      return;
    }
    catch (Exception localException)
    {
      cj.a(localException, "MapOverlayImageView", "destory");
      Log.d("amapApi", "MapOverlayImageView clear erro" + localException.getMessage());
    }
  }
  
  public boolean f(aa paramaa)
  {
    return this.a.b(paramaa);
  }
  
  /* Error */
  public java.util.List<com.amap.api.maps2d.model.Marker> g()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: new 40	java/util/ArrayList
    //   5: dup
    //   6: invokespecial 322	java/util/ArrayList:<init>	()V
    //   9: astore_2
    //   10: new 151	android/graphics/Rect
    //   13: dup
    //   14: iconst_0
    //   15: iconst_0
    //   16: aload_0
    //   17: getfield 75	com/amap/api/mapcore2d/as:a	Lcom/amap/api/mapcore2d/b;
    //   20: invokevirtual 202	com/amap/api/mapcore2d/b:c	()I
    //   23: aload_0
    //   24: getfield 75	com/amap/api/mapcore2d/as:a	Lcom/amap/api/mapcore2d/b;
    //   27: invokevirtual 204	com/amap/api/mapcore2d/b:d	()I
    //   30: invokespecial 207	android/graphics/Rect:<init>	(IIII)V
    //   33: astore_3
    //   34: new 109	com/amap/api/mapcore2d/ad
    //   37: dup
    //   38: invokespecial 208	com/amap/api/mapcore2d/ad:<init>	()V
    //   41: astore 4
    //   43: aload_0
    //   44: getfield 47	com/amap/api/mapcore2d/as:d	Ljava/util/ArrayList;
    //   47: invokevirtual 132	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   50: astore 5
    //   52: aload 5
    //   54: invokeinterface 83 1 0
    //   59: istore_1
    //   60: iload_1
    //   61: ifne +7 -> 68
    //   64: aload_0
    //   65: monitorexit
    //   66: aload_2
    //   67: areturn
    //   68: aload 5
    //   70: invokeinterface 87 1 0
    //   75: checkcast 122	com/amap/api/mapcore2d/aa
    //   78: astore 6
    //   80: aload 6
    //   82: invokeinterface 124 1 0
    //   87: astore 7
    //   89: aload 7
    //   91: ifnull -27 -> 64
    //   94: aload_0
    //   95: getfield 75	com/amap/api/mapcore2d/as:a	Lcom/amap/api/mapcore2d/b;
    //   98: aload 7
    //   100: getfield 99	com/amap/api/maps2d/model/LatLng:latitude	D
    //   103: aload 7
    //   105: getfield 102	com/amap/api/maps2d/model/LatLng:longitude	D
    //   108: aload 4
    //   110: invokevirtual 107	com/amap/api/mapcore2d/b:b	(DDLcom/amap/api/mapcore2d/ad;)V
    //   113: aload_0
    //   114: aload_3
    //   115: aload 4
    //   117: getfield 111	com/amap/api/mapcore2d/ad:a	I
    //   120: aload 4
    //   122: getfield 113	com/amap/api/mapcore2d/ad:b	I
    //   125: invokevirtual 116	com/amap/api/mapcore2d/as:a	(Landroid/graphics/Rect;II)Z
    //   128: ifeq -76 -> 52
    //   131: aload_2
    //   132: new 324	com/amap/api/maps2d/model/Marker
    //   135: dup
    //   136: aload 6
    //   138: invokespecial 326	com/amap/api/maps2d/model/Marker:<init>	(Lcom/amap/api/mapcore2d/aa;)V
    //   141: invokeinterface 329 2 0
    //   146: pop
    //   147: goto -95 -> 52
    //   150: astore_2
    //   151: aload_0
    //   152: monitorexit
    //   153: aload_2
    //   154: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	155	0	this	as
    //   59	2	1	bool	boolean
    //   9	123	2	localArrayList	ArrayList
    //   150	4	2	localObject	Object
    //   33	82	3	localRect	Rect
    //   41	80	4	localad	ad
    //   50	19	5	localIterator	Iterator
    //   78	59	6	localaa	aa
    //   87	17	7	localLatLng	LatLng
    // Exception table:
    //   from	to	target	type
    //   2	52	150	finally
    //   52	60	150	finally
    //   68	89	150	finally
    //   94	147	150	finally
  }
  
  static class a
    implements Serializable, Comparator<ab>
  {
    public int a(ab paramab1, ab paramab2)
    {
      if (paramab1 == null) {}
      for (;;)
      {
        return 0;
        if (paramab2 != null) {
          try
          {
            if (paramab1.r() > paramab2.r()) {
              return 1;
            }
            float f1 = paramab1.r();
            float f2 = paramab2.r();
            if (f1 < f2) {
              return -1;
            }
          }
          catch (Throwable paramab1)
          {
            cj.a(paramab1, "MapOverlayImageView", "compare");
          }
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/as.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */