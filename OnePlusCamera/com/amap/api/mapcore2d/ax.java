package com.amap.api.mapcore2d;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.RemoteException;
import android.util.Log;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

class ax
  implements aa
{
  private static int a = 0;
  private int b = 0;
  private float c = 0.0F;
  private CopyOnWriteArrayList<BitmapDescriptor> d = null;
  private int e = 20;
  private String f;
  private LatLng g;
  private LatLng h;
  private String i;
  private String j;
  private float k = 0.5F;
  private float l = 1.0F;
  private boolean m = false;
  private boolean n = true;
  private as o;
  private Object p;
  private boolean q = false;
  private a r;
  private boolean s = false;
  private int t;
  private int u;
  private float v;
  private int w;
  
  public ax(MarkerOptions paramMarkerOptions, as paramas)
  {
    this.o = paramas;
    this.q = paramMarkerOptions.isGps();
    this.v = paramMarkerOptions.getZIndex();
    if (paramMarkerOptions.getPosition() == null)
    {
      this.k = paramMarkerOptions.getAnchorU();
      this.l = paramMarkerOptions.getAnchorV();
      this.n = paramMarkerOptions.isVisible();
      this.j = paramMarkerOptions.getSnippet();
      this.i = paramMarkerOptions.getTitle();
      this.m = paramMarkerOptions.isDraggable();
      this.e = paramMarkerOptions.getPeriod();
      this.f = d();
      b(paramMarkerOptions.getIcons());
      if (this.d != null) {
        break label240;
      }
    }
    label240:
    while (this.d.size() != 0)
    {
      return;
      if (!this.q) {}
      for (;;)
      {
        this.g = paramMarkerOptions.getPosition();
        break;
        try
        {
          paramas = en.a(paramMarkerOptions.getPosition().longitude, paramMarkerOptions.getPosition().latitude);
          this.h = new LatLng(paramas[1], paramas[0]);
        }
        catch (Exception paramas)
        {
          cj.a(paramas, "MarkerDelegateImp", "MarkerDelegateImp");
          this.h = paramMarkerOptions.getPosition();
        }
      }
    }
    b(paramMarkerOptions.getIcon());
  }
  
  private ad b(float paramFloat1, float paramFloat2)
  {
    float f1 = (float)(this.c * 3.141592653589793D / 180.0D);
    ad localad = new ad();
    localad.a = ((int)(paramFloat1 * Math.cos(f1) + paramFloat2 * Math.sin(f1)));
    localad.b = ((int)(paramFloat2 * Math.cos(f1) - paramFloat1 * Math.sin(f1)));
    return localad;
  }
  
  private void b(BitmapDescriptor paramBitmapDescriptor)
  {
    if (paramBitmapDescriptor == null) {}
    for (;;)
    {
      this.o.a().postInvalidate();
      return;
      w();
      this.d.add(paramBitmapDescriptor.clone());
    }
  }
  
  private static String c(String paramString)
  {
    a += 1;
    return paramString + a;
  }
  
  public BitmapDescriptor A()
  {
    if (this.d == null)
    {
      w();
      this.d.add(BitmapDescriptorFactory.defaultMarker());
    }
    do
    {
      return (BitmapDescriptor)this.d.get(0);
      if (this.d.size() == 0) {
        break;
      }
    } while (this.d.get(0) != null);
    this.d.clear();
    return A();
  }
  
  public float B()
  {
    return this.k;
  }
  
  public float C()
  {
    return this.l;
  }
  
  public void a(float paramFloat)
  {
    this.c = ((-paramFloat % 360.0F + 360.0F) % 360.0F);
    if (!k()) {}
    for (;;)
    {
      this.o.a().postInvalidate();
      return;
      this.o.e(this);
      this.o.d(this);
    }
  }
  
  public void a(float paramFloat1, float paramFloat2)
  {
    if ((this.k == paramFloat1) && (this.l == paramFloat2)) {
      return;
    }
    this.k = paramFloat1;
    this.l = paramFloat2;
    if (!k()) {}
    for (;;)
    {
      this.o.a().postInvalidate();
      return;
      this.o.e(this);
      this.o.d(this);
    }
  }
  
  public void a(int paramInt)
    throws RemoteException
  {
    if (paramInt > 1)
    {
      this.e = paramInt;
      return;
    }
    this.e = 1;
  }
  
  public void a(int paramInt1, int paramInt2)
  {
    this.t = paramInt1;
    this.u = paramInt2;
    this.s = true;
    if (!k()) {
      return;
    }
    i();
  }
  
  public void a(Canvas paramCanvas, w paramw)
  {
    if (!this.n) {}
    while ((t() == null) || (A() == null)) {
      return;
    }
    ad localad;
    if (!q())
    {
      localad = z();
      paramw = p();
      if (paramw == null) {
        break label85;
      }
      if (paramw.size() > 1) {
        break label86;
      }
      if (paramw.size() == 1) {
        break label104;
      }
      paramw = null;
      label61:
      if (paramw != null) {
        break label119;
      }
    }
    label85:
    label86:
    label104:
    label119:
    while (paramw.isRecycled())
    {
      return;
      localad = new ad(this.t, this.u);
      break;
      return;
      paramw = ((BitmapDescriptor)paramw.get(this.b)).getBitmap();
      break label61;
      paramw = ((BitmapDescriptor)paramw.get(0)).getBitmap();
      break label61;
    }
    paramCanvas.save();
    paramCanvas.rotate(this.c, localad.a, localad.b);
    paramCanvas.drawBitmap(paramw, localad.a - B() * paramw.getWidth(), localad.b - C() * paramw.getHeight(), null);
    paramCanvas.restore();
  }
  
  public void a(BitmapDescriptor paramBitmapDescriptor)
  {
    if (paramBitmapDescriptor == null) {}
    while (this.d == null) {
      return;
    }
    this.d.clear();
    this.d.add(paramBitmapDescriptor);
    if (!k()) {}
    for (;;)
    {
      this.o.a().postInvalidate();
      return;
      this.o.e(this);
      this.o.d(this);
    }
  }
  
  public void a(LatLng paramLatLng)
  {
    if (!this.q)
    {
      this.g = paramLatLng;
      return;
    }
    this.h = paramLatLng;
  }
  
  public void a(Object paramObject)
  {
    this.p = paramObject;
  }
  
  public void a(String paramString)
  {
    this.i = paramString;
  }
  
  public void a(ArrayList<BitmapDescriptor> paramArrayList)
    throws RemoteException
  {
    if (paramArrayList != null)
    {
      b(paramArrayList);
      if (this.r == null) {
        break label35;
      }
      if (k()) {
        break label58;
      }
    }
    for (;;)
    {
      this.o.a().postInvalidate();
      return;
      return;
      label35:
      this.r = new a(null);
      this.r.start();
      break;
      label58:
      this.o.e(this);
      this.o.d(this);
    }
  }
  
  public void a(boolean paramBoolean)
  {
    this.m = paramBoolean;
  }
  
  public boolean a()
  {
    return this.o.b(this);
  }
  
  public boolean a(aa paramaa)
  {
    if (equals(paramaa)) {}
    while (paramaa.d().equals(d())) {
      return true;
    }
    return false;
  }
  
  public Rect b()
  {
    ad localad1 = z();
    if (localad1 != null) {}
    try
    {
      int i1 = n();
      int i2 = y();
      Rect localRect = new Rect();
      if (this.c == 0.0F)
      {
        localRect.top = ((int)(localad1.b - i2 * this.l));
        localRect.left = ((int)(localad1.a - this.k * i1));
        f1 = localad1.b;
        localRect.bottom = ((int)(i2 * (1.0F - this.l) + f1));
        f1 = localad1.a;
        f2 = this.k;
        localRect.right = ((int)(f1 + i1 * (1.0F - f2)));
        return localRect;
        return new Rect(0, 0, 0, 0);
      }
      ad localad2 = b(-this.k * i1, (this.l - 1.0F) * i2);
      ad localad3 = b(-this.k * i1, this.l * i2);
      ad localad4 = b((1.0F - this.k) * i1, this.l * i2);
      float f1 = this.k;
      float f2 = i1;
      float f3 = this.l;
      ad localad5 = b(f2 * (1.0F - f1), i2 * (f3 - 1.0F));
      localRect.top = (localad1.b - Math.max(localad2.b, Math.max(localad3.b, Math.max(localad4.b, localad5.b))));
      localRect.left = (localad1.a + Math.min(localad2.a, Math.min(localad3.a, Math.min(localad4.a, localad5.a))));
      localRect.bottom = (localad1.b - Math.min(localad2.b, Math.min(localad3.b, Math.min(localad4.b, localad5.b))));
      localRect.right = (localad1.a + Math.max(localad2.a, Math.max(localad3.a, Math.max(localad4.a, localad5.a))));
      return localRect;
    }
    catch (Throwable localThrowable)
    {
      cj.a(localThrowable, "MarkerDelegateImp", "getRect");
    }
    return new Rect(0, 0, 0, 0);
  }
  
  public void b(float paramFloat)
  {
    this.v = paramFloat;
    this.o.d();
  }
  
  public void b(int paramInt)
  {
    this.w = paramInt;
  }
  
  public void b(LatLng paramLatLng)
  {
    if (!this.q) {}
    for (;;)
    {
      this.s = false;
      this.g = paramLatLng;
      this.o.a().postInvalidate();
      return;
      try
      {
        double[] arrayOfDouble = en.a(paramLatLng.longitude, paramLatLng.latitude);
        this.h = new LatLng(arrayOfDouble[1], arrayOfDouble[0]);
      }
      catch (Exception localException)
      {
        cj.a(localException, "MarkerDelegateImp", "setPosition");
        this.h = paramLatLng;
      }
    }
  }
  
  public void b(String paramString)
  {
    this.j = paramString;
  }
  
  public void b(ArrayList<BitmapDescriptor> paramArrayList)
  {
    w();
    if (paramArrayList == null)
    {
      this.o.a().postInvalidate();
      return;
    }
    Iterator localIterator = paramArrayList.iterator();
    for (;;)
    {
      if (!localIterator.hasNext())
      {
        if ((paramArrayList.size() <= 1) || (this.r != null)) {
          break;
        }
        this.r = new a(null);
        this.r.start();
        break;
      }
      BitmapDescriptor localBitmapDescriptor = (BitmapDescriptor)localIterator.next();
      if (localBitmapDescriptor != null) {
        this.d.add(localBitmapDescriptor.clone());
      }
    }
  }
  
  public void b(boolean paramBoolean)
  {
    this.n = paramBoolean;
    if (paramBoolean) {}
    for (;;)
    {
      this.o.a().postInvalidate();
      return;
      if (k()) {
        this.o.e(this);
      }
    }
  }
  
  public LatLng c()
  {
    if (!this.s)
    {
      if (!this.q) {
        return this.g;
      }
    }
    else
    {
      r localr = new r();
      this.o.a.a(this.t, this.u, localr);
      return new LatLng(localr.b, localr.a);
    }
    return this.h;
  }
  
  public String d()
  {
    if (this.f != null) {}
    for (;;)
    {
      return this.f;
      this.f = c("Marker");
    }
  }
  
  public r e()
  {
    r localr = new r();
    if (this.d == null) {}
    while (this.d.size() == 0) {
      return localr;
    }
    localr.a = (n() * this.k);
    localr.b = (y() * this.l);
    return localr;
  }
  
  public String f()
  {
    return this.i;
  }
  
  public String g()
  {
    return this.j;
  }
  
  public boolean h()
  {
    return this.m;
  }
  
  public void i()
  {
    if (s())
    {
      this.o.d(this);
      return;
    }
  }
  
  public void j()
  {
    if (!k()) {
      return;
    }
    this.o.e(this);
  }
  
  public boolean k()
  {
    return this.o.f(this);
  }
  
  public void l()
  {
    for (;;)
    {
      try
      {
        if (this.d == null) {
          continue;
        }
        localIterator = this.d.iterator();
        if (localIterator.hasNext()) {
          continue;
        }
        this.d = null;
        this.g = null;
        this.p = null;
      }
      catch (Exception localException)
      {
        Iterator localIterator;
        Bitmap localBitmap;
        cj.a(localException, "MarkerDelegateImp", "destroy");
        Log.d("destroy erro", "MarkerDelegateImp destroy");
        continue;
      }
      this.r = null;
      return;
      this.g = null;
      this.p = null;
      this.r = null;
      return;
      localBitmap = ((BitmapDescriptor)localIterator.next()).getBitmap();
      if (localBitmap != null) {
        localBitmap.recycle();
      }
    }
  }
  
  public int m()
  {
    return super.hashCode();
  }
  
  public int n()
  {
    return A().getWidth();
  }
  
  public int o()
    throws RemoteException
  {
    return this.e;
  }
  
  public ArrayList<BitmapDescriptor> p()
  {
    if (this.d == null) {}
    while (this.d.size() <= 0) {
      return null;
    }
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.d.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {
        return localArrayList;
      }
      localArrayList.add((BitmapDescriptor)localIterator.next());
    }
  }
  
  public boolean q()
  {
    return this.s;
  }
  
  public float r()
  {
    return this.v;
  }
  
  public boolean s()
  {
    return this.n;
  }
  
  public LatLng t()
  {
    if (!this.s) {
      return this.g;
    }
    r localr = new r();
    this.o.a.a(this.t, this.u, localr);
    return new LatLng(localr.b, localr.a);
  }
  
  public Object u()
  {
    return this.p;
  }
  
  public int v()
  {
    return this.w;
  }
  
  void w()
  {
    if (this.d != null)
    {
      this.d.clear();
      return;
    }
    this.d = new CopyOnWriteArrayList();
  }
  
  public ad x()
  {
    if (t() != null)
    {
      ad localad = new ad();
      try
      {
        if (!this.q) {}
        for (u localu = new u((int)(t().latitude * 1000000.0D), (int)(t().longitude * 1000000.0D));; localu = new u((int)(c().latitude * 1000000.0D), (int)(c().longitude * 1000000.0D)))
        {
          Point localPoint = new Point();
          this.o.a().s().a(localu, localPoint);
          localad.a = localPoint.x;
          localad.b = localPoint.y;
          return localad;
        }
        return null;
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
        return localad;
      }
    }
  }
  
  public int y()
  {
    return A().getHeight();
  }
  
  public ad z()
  {
    ad localad = x();
    if (localad != null) {
      return localad;
    }
    return null;
  }
  
  private class a
    extends Thread
  {
    private a() {}
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: ldc 26
      //   3: invokevirtual 30	com/amap/api/mapcore2d/ax$a:setName	(Ljava/lang/String;)V
      //   6: invokestatic 34	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   9: invokevirtual 38	java/lang/Thread:isInterrupted	()Z
      //   12: ifeq +4 -> 16
      //   15: return
      //   16: aload_0
      //   17: getfield 12	com/amap/api/mapcore2d/ax$a:a	Lcom/amap/api/mapcore2d/ax;
      //   20: invokestatic 41	com/amap/api/mapcore2d/ax:a	(Lcom/amap/api/mapcore2d/ax;)Ljava/util/concurrent/CopyOnWriteArrayList;
      //   23: ifnull +124 -> 147
      //   26: aload_0
      //   27: getfield 12	com/amap/api/mapcore2d/ax$a:a	Lcom/amap/api/mapcore2d/ax;
      //   30: invokestatic 41	com/amap/api/mapcore2d/ax:a	(Lcom/amap/api/mapcore2d/ax;)Ljava/util/concurrent/CopyOnWriteArrayList;
      //   33: invokevirtual 47	java/util/concurrent/CopyOnWriteArrayList:size	()I
      //   36: iconst_1
      //   37: if_icmple +110 -> 147
      //   40: aload_0
      //   41: getfield 12	com/amap/api/mapcore2d/ax$a:a	Lcom/amap/api/mapcore2d/ax;
      //   44: invokestatic 51	com/amap/api/mapcore2d/ax:b	(Lcom/amap/api/mapcore2d/ax;)I
      //   47: aload_0
      //   48: getfield 12	com/amap/api/mapcore2d/ax$a:a	Lcom/amap/api/mapcore2d/ax;
      //   51: invokestatic 41	com/amap/api/mapcore2d/ax:a	(Lcom/amap/api/mapcore2d/ax;)Ljava/util/concurrent/CopyOnWriteArrayList;
      //   54: invokevirtual 47	java/util/concurrent/CopyOnWriteArrayList:size	()I
      //   57: iconst_1
      //   58: isub
      //   59: if_icmpeq +64 -> 123
      //   62: aload_0
      //   63: getfield 12	com/amap/api/mapcore2d/ax$a:a	Lcom/amap/api/mapcore2d/ax;
      //   66: invokestatic 54	com/amap/api/mapcore2d/ax:c	(Lcom/amap/api/mapcore2d/ax;)I
      //   69: pop
      //   70: aload_0
      //   71: getfield 12	com/amap/api/mapcore2d/ax$a:a	Lcom/amap/api/mapcore2d/ax;
      //   74: invokestatic 58	com/amap/api/mapcore2d/ax:d	(Lcom/amap/api/mapcore2d/ax;)Lcom/amap/api/mapcore2d/as;
      //   77: invokevirtual 63	com/amap/api/mapcore2d/as:a	()Lcom/amap/api/mapcore2d/b;
      //   80: invokevirtual 68	com/amap/api/mapcore2d/b:postInvalidate	()V
      //   83: aload_0
      //   84: getfield 12	com/amap/api/mapcore2d/ax$a:a	Lcom/amap/api/mapcore2d/ax;
      //   87: invokestatic 71	com/amap/api/mapcore2d/ax:e	(Lcom/amap/api/mapcore2d/ax;)I
      //   90: sipush 250
      //   93: imul
      //   94: i2l
      //   95: invokestatic 75	java/lang/Thread:sleep	(J)V
      //   98: aload_0
      //   99: getfield 12	com/amap/api/mapcore2d/ax$a:a	Lcom/amap/api/mapcore2d/ax;
      //   102: invokestatic 41	com/amap/api/mapcore2d/ax:a	(Lcom/amap/api/mapcore2d/ax;)Ljava/util/concurrent/CopyOnWriteArrayList;
      //   105: ifnonnull -99 -> 6
      //   108: invokestatic 34	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   111: invokevirtual 78	java/lang/Thread:interrupt	()V
      //   114: goto -108 -> 6
      //   117: astore_1
      //   118: aload_1
      //   119: invokevirtual 81	java/lang/Throwable:printStackTrace	()V
      //   122: return
      //   123: aload_0
      //   124: getfield 12	com/amap/api/mapcore2d/ax$a:a	Lcom/amap/api/mapcore2d/ax;
      //   127: iconst_0
      //   128: invokestatic 84	com/amap/api/mapcore2d/ax:a	(Lcom/amap/api/mapcore2d/ax;I)I
      //   131: pop
      //   132: goto -62 -> 70
      //   135: astore_1
      //   136: aload_1
      //   137: ldc 86
      //   139: ldc 87
      //   141: invokestatic 92	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
      //   144: goto -46 -> 98
      //   147: return
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	148	0	this	a
      //   117	2	1	localThrowable	Throwable
      //   135	2	1	localInterruptedException	InterruptedException
      // Exception table:
      //   from	to	target	type
      //   0	6	117	java/lang/Throwable
      //   6	15	117	java/lang/Throwable
      //   16	70	117	java/lang/Throwable
      //   70	83	117	java/lang/Throwable
      //   83	98	117	java/lang/Throwable
      //   98	114	117	java/lang/Throwable
      //   123	132	117	java/lang/Throwable
      //   136	144	117	java/lang/Throwable
      //   83	98	135	java/lang/InterruptedException
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ax.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */