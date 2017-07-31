package com.amap.api.mapcore2d;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import java.util.LinkedList;

final class ap
  implements View.OnKeyListener
{
  private float a = 0.0F;
  private float b = 0.0F;
  private ay c;
  private boolean d;
  private b e;
  private a f;
  
  ap(ay paramay)
  {
    this.c = paramay;
    this.d = false;
    this.e = new b(null);
    this.f = new a(null);
  }
  
  private boolean a(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    return a(paramInt1, paramInt2, paramBoolean1, paramBoolean2, 1);
  }
  
  private boolean a(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3)
  {
    boolean bool = false;
    this.c.c.g().M();
    float f1;
    if (!paramBoolean1) {
      f1 = this.c.c.e() - paramInt3;
    }
    for (;;)
    {
      f1 = this.c.c.g().a(f1);
      if (f1 != this.c.c.e())
      {
        a(paramInt1, paramInt2, f1, paramBoolean1, paramBoolean2);
        bool = true;
      }
      try
      {
        paramBoolean1 = this.c.h.q().a();
        if (!paramBoolean1)
        {
          return bool;
          f1 = this.c.c.e() + paramInt3;
        }
        else
        {
          this.c.h.N();
          return bool;
        }
      }
      catch (RemoteException localRemoteException)
      {
        cj.a(localRemoteException, "MapController", "zoomWithAnimation");
      }
    }
    return bool;
  }
  
  private boolean b(u paramu)
  {
    if (paramu == null) {}
    while ((this.c == null) || (this.c.c == null)) {
      return false;
    }
    u localu = this.c.c.f();
    if (localu != null) {
      if (paramu.b() == localu.b()) {
        break label53;
      }
    }
    label53:
    while (paramu.a() != localu.a())
    {
      return true;
      return false;
    }
    return false;
  }
  
  private void c(u paramu)
  {
    this.c.h.M();
    this.c.c.a(paramu);
  }
  
  private float e(float paramFloat)
  {
    b localb = this.c.c.g();
    localb.M();
    paramFloat = localb.a(paramFloat);
    this.c.c.a(paramFloat);
    try
    {
      if (!this.c.h.q().a()) {
        return paramFloat;
      }
      this.c.h.N();
      return paramFloat;
    }
    catch (RemoteException localRemoteException)
    {
      cj.a(localRemoteException, "MapController", "setZoom");
    }
    return paramFloat;
  }
  
  private boolean f(float paramFloat)
  {
    if (this.c == null) {}
    while (this.c.c == null) {
      return false;
    }
    return paramFloat != this.c.c.e();
  }
  
  public float a()
  {
    return this.a;
  }
  
  public void a(float paramFloat)
  {
    this.a = paramFloat;
  }
  
  public void a(float paramFloat1, float paramFloat2)
  {
    a(paramFloat1, paramFloat2, 0, 0, 0);
  }
  
  /* Error */
  public void a(float paramFloat1, float paramFloat2, int paramInt1, int paramInt2, int paramInt3)
  {
    // Byte code:
    //   0: fload_1
    //   1: fconst_0
    //   2: fcmpg
    //   3: ifgt +18 -> 21
    //   6: iconst_1
    //   7: istore 13
    //   9: iload 13
    //   11: ifne +9 -> 20
    //   14: fload_2
    //   15: fconst_0
    //   16: fcmpg
    //   17: ifgt +10 -> 27
    //   20: return
    //   21: iconst_0
    //   22: istore 13
    //   24: goto -15 -> 9
    //   27: aload_0
    //   28: getfield 34	com/amap/api/mapcore2d/ap:c	Lcom/amap/api/mapcore2d/ay;
    //   31: ifnonnull +4 -> 35
    //   34: return
    //   35: aload_0
    //   36: getfield 34	com/amap/api/mapcore2d/ap:c	Lcom/amap/api/mapcore2d/ay;
    //   39: getfield 57	com/amap/api/mapcore2d/ay:c	Lcom/amap/api/mapcore2d/ay$d;
    //   42: ifnull -8 -> 34
    //   45: aload_0
    //   46: getfield 34	com/amap/api/mapcore2d/ap:c	Lcom/amap/api/mapcore2d/ay;
    //   49: getfield 131	com/amap/api/mapcore2d/ay:b	Lcom/amap/api/mapcore2d/ay$e;
    //   52: ifnull -18 -> 34
    //   55: fconst_0
    //   56: fstore 11
    //   58: fload 11
    //   60: fstore 10
    //   62: aload_0
    //   63: getfield 34	com/amap/api/mapcore2d/ap:c	Lcom/amap/api/mapcore2d/ay;
    //   66: getfield 57	com/amap/api/mapcore2d/ay:c	Lcom/amap/api/mapcore2d/ay$d;
    //   69: invokevirtual 71	com/amap/api/mapcore2d/ay$d:e	()F
    //   72: fstore 12
    //   74: fload 11
    //   76: fstore 10
    //   78: aload_0
    //   79: getfield 34	com/amap/api/mapcore2d/ap:c	Lcom/amap/api/mapcore2d/ay;
    //   82: getfield 131	com/amap/api/mapcore2d/ay:b	Lcom/amap/api/mapcore2d/ay$e;
    //   85: iload_3
    //   86: iload 4
    //   88: iload 5
    //   90: invokevirtual 136	com/amap/api/mapcore2d/ay$e:b	(III)I
    //   93: istore 13
    //   95: fload 11
    //   97: fstore 10
    //   99: aload_0
    //   100: getfield 34	com/amap/api/mapcore2d/ap:c	Lcom/amap/api/mapcore2d/ay;
    //   103: getfield 131	com/amap/api/mapcore2d/ay:b	Lcom/amap/api/mapcore2d/ay$e;
    //   106: iload_3
    //   107: iload 4
    //   109: iload 5
    //   111: invokevirtual 138	com/amap/api/mapcore2d/ay$e:a	(III)I
    //   114: istore_3
    //   115: iload 13
    //   117: ifeq +79 -> 196
    //   120: iload_3
    //   121: i2f
    //   122: fload_1
    //   123: fdiv
    //   124: f2d
    //   125: dstore 6
    //   127: iload 13
    //   129: i2f
    //   130: fload_2
    //   131: fdiv
    //   132: f2d
    //   133: dstore 8
    //   135: dload 6
    //   137: dload 8
    //   139: invokestatic 144	java/lang/Math:min	(DD)D
    //   142: dstore 6
    //   144: aload_0
    //   145: getfield 34	com/amap/api/mapcore2d/ap:c	Lcom/amap/api/mapcore2d/ay;
    //   148: getfield 148	com/amap/api/mapcore2d/ay:i	Lcom/amap/api/mapcore2d/au;
    //   151: getfield 154	com/amap/api/mapcore2d/au:k	D
    //   154: dload 6
    //   156: ddiv
    //   157: dstore 8
    //   159: iconst_0
    //   160: istore_3
    //   161: aload_0
    //   162: getfield 34	com/amap/api/mapcore2d/ap:c	Lcom/amap/api/mapcore2d/ay;
    //   165: getfield 148	com/amap/api/mapcore2d/ay:i	Lcom/amap/api/mapcore2d/au;
    //   168: getfield 156	com/amap/api/mapcore2d/au:d	D
    //   171: dstore 6
    //   173: dload 6
    //   175: ldc2_w 157
    //   178: ddiv
    //   179: dstore 6
    //   181: dload 6
    //   183: dload 8
    //   185: dcmpl
    //   186: ifle +33 -> 219
    //   189: iload_3
    //   190: iconst_1
    //   191: iadd
    //   192: istore_3
    //   193: goto -20 -> 173
    //   196: iload_3
    //   197: ifne -77 -> 120
    //   200: fload 11
    //   202: fstore 10
    //   204: aload_0
    //   205: fload_1
    //   206: putfield 30	com/amap/api/mapcore2d/ap:a	F
    //   209: fload 11
    //   211: fstore 10
    //   213: aload_0
    //   214: fload_2
    //   215: putfield 32	com/amap/api/mapcore2d/ap:b	F
    //   218: return
    //   219: aload_0
    //   220: getfield 34	com/amap/api/mapcore2d/ap:c	Lcom/amap/api/mapcore2d/ay;
    //   223: getfield 148	com/amap/api/mapcore2d/ay:i	Lcom/amap/api/mapcore2d/au;
    //   226: getfield 156	com/amap/api/mapcore2d/au:d	D
    //   229: iconst_1
    //   230: iload_3
    //   231: ishl
    //   232: i2d
    //   233: ddiv
    //   234: dload 8
    //   236: ddiv
    //   237: invokestatic 162	java/lang/Math:log	(D)D
    //   240: dstore 6
    //   242: ldc2_w 157
    //   245: invokestatic 162	java/lang/Math:log	(D)D
    //   248: dstore 8
    //   250: dload 6
    //   252: dload 8
    //   254: ddiv
    //   255: iload_3
    //   256: i2d
    //   257: dadd
    //   258: d2f
    //   259: fstore_1
    //   260: fload_1
    //   261: fstore 10
    //   263: aload_0
    //   264: fload_1
    //   265: invokevirtual 164	com/amap/api/mapcore2d/ap:d	(F)F
    //   268: fstore_2
    //   269: fload_2
    //   270: f2i
    //   271: istore_3
    //   272: fload_2
    //   273: iload_3
    //   274: i2f
    //   275: fsub
    //   276: fstore 10
    //   278: fload 10
    //   280: f2d
    //   281: dstore 6
    //   283: dload 6
    //   285: dconst_1
    //   286: dconst_1
    //   287: getstatic 166	com/amap/api/mapcore2d/ay:a	D
    //   290: dsub
    //   291: ldc2_w 167
    //   294: dmul
    //   295: dsub
    //   296: dcmpl
    //   297: ifle +47 -> 344
    //   300: getstatic 166	com/amap/api/mapcore2d/ay:a	D
    //   303: dstore 6
    //   305: dload 6
    //   307: d2f
    //   308: iload_3
    //   309: i2f
    //   310: fadd
    //   311: fstore_1
    //   312: ldc 95
    //   314: new 170	java/lang/StringBuilder
    //   317: dup
    //   318: invokespecial 171	java/lang/StringBuilder:<init>	()V
    //   321: ldc -83
    //   323: invokevirtual 177	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   326: fload_1
    //   327: invokevirtual 180	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
    //   330: invokevirtual 184	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   333: invokestatic 189	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   336: pop
    //   337: aload_0
    //   338: fload_1
    //   339: invokevirtual 191	com/amap/api/mapcore2d/ap:c	(F)F
    //   342: pop
    //   343: return
    //   344: fload 10
    //   346: f2d
    //   347: dstore 6
    //   349: dload 6
    //   351: getstatic 166	com/amap/api/mapcore2d/ay:a	D
    //   354: dcmpl
    //   355: ifle +18 -> 373
    //   358: getstatic 166	com/amap/api/mapcore2d/ay:a	D
    //   361: ldc2_w 192
    //   364: dsub
    //   365: d2f
    //   366: iload_3
    //   367: i2f
    //   368: fadd
    //   369: fstore_1
    //   370: goto -58 -> 312
    //   373: fload_2
    //   374: fstore_1
    //   375: fload 10
    //   377: getstatic 166	com/amap/api/mapcore2d/ay:a	D
    //   380: ldc2_w 192
    //   383: dsub
    //   384: d2f
    //   385: fcmpl
    //   386: ifne -74 -> 312
    //   389: getstatic 166	com/amap/api/mapcore2d/ay:a	D
    //   392: dstore 6
    //   394: dload 6
    //   396: ldc2_w 192
    //   399: dsub
    //   400: d2f
    //   401: iload_3
    //   402: i2f
    //   403: fadd
    //   404: fstore_1
    //   405: goto -93 -> 312
    //   408: astore 14
    //   410: fload 10
    //   412: fstore_1
    //   413: aload 14
    //   415: ldc 95
    //   417: ldc -61
    //   419: invokestatic 102	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   422: goto -110 -> 312
    //   425: astore 14
    //   427: fload 12
    //   429: fstore_1
    //   430: goto -17 -> 413
    //   433: astore 14
    //   435: fload_2
    //   436: fstore_1
    //   437: goto -24 -> 413
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	440	0	this	ap
    //   0	440	1	paramFloat1	float
    //   0	440	2	paramFloat2	float
    //   0	440	3	paramInt1	int
    //   0	440	4	paramInt2	int
    //   0	440	5	paramInt3	int
    //   125	270	6	d1	double
    //   133	120	8	d2	double
    //   60	351	10	f1	float
    //   56	154	11	f2	float
    //   72	356	12	f3	float
    //   7	121	13	i	int
    //   408	6	14	localException1	Exception
    //   425	1	14	localException2	Exception
    //   433	1	14	localException3	Exception
    // Exception table:
    //   from	to	target	type
    //   62	74	408	java/lang/Exception
    //   78	95	408	java/lang/Exception
    //   99	115	408	java/lang/Exception
    //   204	209	408	java/lang/Exception
    //   213	218	408	java/lang/Exception
    //   263	269	408	java/lang/Exception
    //   135	159	425	java/lang/Exception
    //   161	173	425	java/lang/Exception
    //   219	250	425	java/lang/Exception
    //   283	305	433	java/lang/Exception
    //   349	370	433	java/lang/Exception
    //   375	394	433	java/lang/Exception
  }
  
  public void a(int paramInt1, int paramInt2, float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.e.a(paramInt1, paramInt2, paramFloat, paramBoolean1, paramBoolean2);
  }
  
  public void a(u paramu)
  {
    if (!b(paramu)) {
      return;
    }
    c(paramu);
  }
  
  public void a(u paramu, float paramFloat)
  {
    if (b(paramu)) {}
    while (f(paramFloat))
    {
      c(paramu);
      e(paramFloat);
      return;
    }
  }
  
  public void a(u paramu, int paramInt)
  {
    this.f.a(paramu, null, null, paramInt);
  }
  
  public void a(boolean paramBoolean)
  {
    this.e.a();
    this.f.b();
  }
  
  boolean a(int paramInt)
  {
    return a(this.c.c.c() / 2, this.c.c.d() / 2, true, false, paramInt);
  }
  
  public boolean a(int paramInt1, int paramInt2)
  {
    return a(paramInt1, paramInt2, true, true);
  }
  
  public float b()
  {
    return this.b;
  }
  
  public void b(float paramFloat)
  {
    this.b = paramFloat;
  }
  
  public void b(int paramInt1, int paramInt2)
  {
    if (!this.d)
    {
      if (paramInt1 == 0) {
        break label37;
      }
      if (p.r == true) {
        break label42;
      }
    }
    for (;;)
    {
      this.c.c.a(false, false);
      return;
      this.d = false;
      return;
      label37:
      if (paramInt2 != 0) {
        break;
      }
      return;
      label42:
      PointF localPointF1 = new PointF(0.0F, 0.0F);
      PointF localPointF2 = new PointF(paramInt1, paramInt2);
      this.c.i.a(localPointF1, localPointF2, this.c.c.e());
    }
  }
  
  boolean b(int paramInt)
  {
    return a(this.c.c.c() / 2, this.c.c.d() / 2, false, false, paramInt);
  }
  
  public float c(float paramFloat)
  {
    if (f(paramFloat))
    {
      e(paramFloat);
      return paramFloat;
    }
    return paramFloat;
  }
  
  public boolean c()
  {
    return a(1);
  }
  
  public float d(float paramFloat)
  {
    float f1 = paramFloat;
    if (paramFloat < this.c.c.b()) {
      f1 = this.c.c.b();
    }
    paramFloat = f1;
    if (f1 > this.c.c.a()) {
      paramFloat = this.c.c.a();
    }
    return paramFloat;
  }
  
  public boolean d()
  {
    return b(1);
  }
  
  public void e()
  {
    this.d = true;
  }
  
  public boolean f()
  {
    return this.f.a();
  }
  
  public void g()
  {
    this.f.b();
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    if (paramKeyEvent.getAction() == 0) {}
    switch (paramInt)
    {
    default: 
      return false;
      return false;
    case 21: 
      b(-10, 0);
      return true;
    case 22: 
      b(10, 0);
      return true;
    case 19: 
      b(0, -10);
      return true;
    }
    b(0, 10);
    return true;
  }
  
  private class a
    implements bw
  {
    private bv b = null;
    private Message c = null;
    private Runnable d = null;
    
    private a() {}
    
    private bv a(u paramu, int paramInt)
    {
      int i = 500;
      if (paramInt >= 500) {
        i = paramInt;
      }
      return new bv(i, 10, ap.a(ap.this).i.l, paramu, paramInt, this);
    }
    
    private void d()
    {
      this.b = null;
      this.c = null;
      this.d = null;
    }
    
    public void a(u paramu)
    {
      if (paramu != null)
      {
        if ((paramu.d() == Long.MIN_VALUE) || (paramu.c() == Long.MIN_VALUE))
        {
          paramu = ap.a(ap.this).i.b(paramu);
          ap.this.a(paramu);
        }
      }
      else {
        return;
      }
      ap.this.a(paramu);
    }
    
    public void a(u paramu, Message paramMessage, Runnable paramRunnable, int paramInt)
    {
      ap.a(ap.this).d.a = true;
      ap.a(ap.this).i.m = paramu.g();
      this.b = a(paramu, paramInt);
      this.c = paramMessage;
      this.d = paramRunnable;
      this.b.d();
    }
    
    public boolean a()
    {
      if (this.b == null) {
        return false;
      }
      return this.b.f();
    }
    
    public void b()
    {
      if (this.b == null) {
        return;
      }
      this.b.e();
    }
    
    public void c()
    {
      if (this.c == null) {
        if (this.d != null) {
          break label50;
        }
      }
      for (;;)
      {
        d();
        if (ap.a(ap.this).d != null) {
          break label62;
        }
        return;
        this.c.getTarget().sendMessage(this.c);
        break;
        label50:
        this.d.run();
      }
      label62:
      ap.a(ap.this).d.a = false;
    }
  }
  
  private class b
    implements Animation.AnimationListener
  {
    private LinkedList<Animation> b = new LinkedList();
    private cc c = null;
    
    private b() {}
    
    private void a(float paramFloat, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if (this.c != null) {}
      for (;;)
      {
        this.c.d = paramBoolean;
        this.c.c = paramFloat;
        this.c.a(paramFloat, false, paramInt1, paramInt2);
        return;
        this.c = new cc(ap.a(ap.this).c.g(), this);
      }
    }
    
    private void b(float paramFloat, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if (this.c != null)
      {
        this.c.c = paramFloat;
        this.c.d = paramBoolean;
        if (this.c.d == true) {
          break label77;
        }
      }
      for (;;)
      {
        this.c.a(paramFloat, true, paramInt1, paramInt2);
        return;
        this.c = new cc(ap.a(ap.this).c.g(), this);
        break;
        label77:
        Point localPoint = new Point(paramInt1, paramInt2);
        u localu = ap.a(ap.this).c.g().s().a(paramInt1, paramInt2);
        ap.a(ap.this).i.l = ap.a(ap.this).i.a(localu);
        ap.a(ap.this).i.a(localPoint);
      }
    }
    
    public void a()
    {
      this.b.clear();
    }
    
    public void a(int paramInt1, int paramInt2, float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
    {
      if (paramBoolean1)
      {
        b(paramFloat, paramInt1, paramInt2, paramBoolean2);
        return;
      }
      a(paramFloat, paramInt1, paramInt2, paramBoolean2);
    }
    
    public void onAnimationEnd(Animation paramAnimation)
    {
      paramAnimation = ap.a(ap.this).c.g();
      if (this.b.size() != 0)
      {
        paramAnimation.startAnimation((Animation)this.b.remove());
        return;
      }
      ap.a(ap.this).e.b();
    }
    
    public void onAnimationRepeat(Animation paramAnimation) {}
    
    public void onAnimationStart(Animation paramAnimation) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */