package com.amap.api.mapcore2d;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import com.amap.api.maps2d.AMap.CancelableCallback;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnCacheRemoveListener;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMapLongClickListener;
import com.amap.api.maps2d.AMap.OnMapScreenShotListener;
import com.amap.api.maps2d.AMap.OnMapTouchListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMap.OnMarkerDragListener;
import com.amap.api.maps2d.AMap.OnMyLocationChangeListener;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.CameraPosition.Builder;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.GroundOverlayOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolygonOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.amap.api.maps2d.model.Text;
import com.amap.api.maps2d.model.TextOptions;
import com.amap.api.maps2d.model.TileOverlay;
import com.amap.api.maps2d.model.TileOverlayOptions;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class b
  extends View
  implements GestureDetector.OnDoubleTapListener, GestureDetector.OnGestureListener, ba.b, bj.a, k.a, m.a, w
{
  private static int aD = Color.rgb(222, 215, 214);
  private static Paint aE = null;
  private static Bitmap aF = null;
  private ca A;
  private bi B;
  private LocationSource C;
  private o D;
  private a E = null;
  private boolean F = false;
  private boolean G = false;
  private AMap.OnCameraChangeListener H;
  private j I;
  private AMap.CancelableCallback J = null;
  private au K;
  private boolean L = false;
  private boolean M = false;
  private View N;
  private AMap.OnInfoWindowClickListener O;
  private AMap.InfoWindowAdapter P;
  private ax Q;
  private AMap.OnMarkerClickListener R;
  private Drawable S = null;
  private ag T;
  private boolean U = false;
  private boolean V = false;
  private boolean W = false;
  private AMap.OnMarkerDragListener Z;
  ay a;
  private long aA = 0L;
  private int aB = 0;
  private int aC = 0;
  private int aG = 0;
  private boolean aH = false;
  private a aI = null;
  private AMap.OnMapTouchListener aa;
  private AMap.OnMapLongClickListener ab;
  private AMap.OnMapLoadedListener ac;
  private AMap.OnMapClickListener ad;
  private boolean ae = false;
  private AMap.OnMapScreenShotListener af = null;
  private Timer ag = null;
  private Thread ah = null;
  private TimerTask ai = new TimerTask()
  {
    public void run()
    {
      try
      {
        b.this.k.sendEmptyMessage(19);
        return;
      }
      catch (Throwable localThrowable)
      {
        cj.a(localThrowable, "AMapDelegateImpGLSurfaceView", "TimerTask run");
      }
    }
  };
  private Handler aj = new Handler();
  private Handler ak = new Handler()
  {
    String a = "onTouchHandler";
    
    public void handleMessage(Message paramAnonymousMessage)
    {
      super.handleMessage(paramAnonymousMessage);
      try
      {
        if (b.a(b.this) == null) {
          return;
        }
        b.a(b.this).onTouch((MotionEvent)paramAnonymousMessage.obj);
        return;
      }
      catch (Throwable paramAnonymousMessage)
      {
        cj.a(paramAnonymousMessage, "AMapDelegateImpGLSurfaceView", this.a);
      }
    }
  };
  private Point al;
  private GestureDetector am;
  private ba.a an;
  private ArrayList<GestureDetector.OnGestureListener> ao = new ArrayList();
  private ArrayList<ba.b> ap = new ArrayList();
  private Scroller aq;
  private int ar = 0;
  private int as = 0;
  private Matrix at = new Matrix();
  private float au = 1.0F;
  private boolean av = false;
  private float aw;
  private float ax;
  private int ay;
  private int az;
  public ap b;
  float[] c = new float[2];
  boolean d = false;
  ar e = new ar(this);
  cb f;
  public at g;
  protected ak h;
  public br i;
  public as j;
  final Handler k = new Handler()
  {
    String a = "handleMessage";
    
    public void handleMessage(Message paramAnonymousMessage)
    {
      if (paramAnonymousMessage == null) {}
      while ((b.this.a == null) || (b.this.a.c == null)) {
        return;
      }
      try
      {
        switch (paramAnonymousMessage.what)
        {
        case 15: 
          b.b(b.this);
          return;
        }
      }
      catch (Throwable paramAnonymousMessage)
      {
        cj.a(paramAnonymousMessage, "AMapDelegateImpGLSurfaceView", "handle_handleMessage");
        return;
      }
      if (b.c(b.this) != null)
      {
        b.c(b.this).onMapLoaded();
        return;
        Object localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Key验证失败：[");
        if (paramAnonymousMessage.obj == null) {
          ((StringBuilder)localObject).append(cm.b);
        }
        for (;;)
        {
          ((StringBuilder)localObject).append("]");
          Log.w("amapsdk", ((StringBuilder)localObject).toString());
          return;
          ((StringBuilder)localObject).append(paramAnonymousMessage.obj);
        }
        try
        {
          paramAnonymousMessage = (Bitmap)paramAnonymousMessage.obj;
          if (paramAnonymousMessage.isRecycled()) {
            break label863;
          }
          paramAnonymousMessage = Bitmap.createBitmap(paramAnonymousMessage);
          if (paramAnonymousMessage == null)
          {
            if (b.g(b.this) != null) {
              break label436;
            }
            b.this.destroyDrawingCache();
            b.a(b.this, null);
          }
        }
        catch (Exception paramAnonymousMessage)
        {
          for (;;)
          {
            cj.a(paramAnonymousMessage, "AMapDelegateImpGLSurfaceView", this.a);
            paramAnonymousMessage = null;
            continue;
            localObject = new Canvas(paramAnonymousMessage);
            if (b.d(b.this) == null) {
              label314:
              if (b.e(b.this) != null) {
                break label365;
              }
            }
            while (b.g(b.this) != null)
            {
              b.g(b.this).onMapScreenShot(paramAnonymousMessage);
              break;
              b.d(b.this).draw((Canvas)localObject);
              break label314;
              label365:
              if (b.f(b.this) != null)
              {
                Bitmap localBitmap = b.e(b.this).getDrawingCache(true);
                if (localBitmap != null)
                {
                  int i = b.e(b.this).getLeft();
                  int j = b.e(b.this).getTop();
                  ((Canvas)localObject).drawBitmap(localBitmap, i, j, new Paint());
                }
              }
            }
            label436:
            b.g(b.this).onMapScreenShot(null);
          }
        }
        paramAnonymousMessage = b.h(b.this);
        if (b.i(b.this) == null)
        {
          if (p.h != null) {
            break label559;
          }
          label476:
          if ((paramAnonymousMessage.zoom >= 10.0F) && (!ci.a(paramAnonymousMessage.target.latitude, paramAnonymousMessage.target.longitude))) {
            break label574;
          }
          b.d(b.this).setVisibility(0);
          label517:
          if (b.j(b.this) != null) {
            break label589;
          }
        }
        for (;;)
        {
          if (!b.k(b.this)) {
            break label622;
          }
          b.b(b.this, false);
          return;
          b.a(b.this, true, paramAnonymousMessage);
          break;
          label559:
          if (p.h.trim().length() == 0) {
            break label476;
          }
          break label517;
          label574:
          b.d(b.this).setVisibility(8);
          break label517;
          label589:
          b.a(b.this, true);
          b.j(b.this).onFinish();
          b.a(b.this, false);
        }
        label622:
        b.a(b.this, null);
        return;
        if (b.i(b.this) != null)
        {
          paramAnonymousMessage = new CameraPosition(b.l(b.this), b.this.f(), 0.0F, 0.0F);
          b.i(b.this).onCameraChange(paramAnonymousMessage);
          return;
          if ((b.m(b.this) == null) || (!b.m(b.this).g())) {}
        }
      }
      switch (b.m(b.this).h())
      {
      case 2: 
        paramAnonymousMessage = l.a(new ad(b.m(b.this).b(), b.m(b.this).c()), b.m(b.this).d(), b.m(b.this).e(), b.m(b.this).f());
        if (!b.m(b.this).a()) {}
        for (;;)
        {
          b.this.e.a(paramAnonymousMessage);
          return;
          paramAnonymousMessage.l = true;
        }
        if ((b.this.a != null) && (b.this.a.d != null))
        {
          b.this.a.d.a();
          return;
        }
        label863:
        return;
      }
    }
  };
  float l = -1.0F;
  private Context m;
  private boolean n = false;
  private boolean o = true;
  private Marker p;
  private aa q;
  private final int[] r = { 10000000, 5000000, 2000000, 1000000, 500000, 200000, 100000, 50000, 30000, 20000, 10000, 5000, 2000, 1000, 500, 200, 100, 50, 25, 10, 5 };
  private boolean s = true;
  private int t = 1;
  private ao u;
  private Location v;
  private c w;
  private AMap.OnMyLocationChangeListener x;
  private boolean y = true;
  private bb z;
  
  public b(Context paramContext)
  {
    super(paramContext);
    U();
    setClickable(true);
    a(paramContext, null);
  }
  
  public static int H()
  {
    return aD;
  }
  
  /* Error */
  public static Paint I()
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 184	com/amap/api/mapcore2d/b:aE	Landroid/graphics/Paint;
    //   6: ifnull +12 -> 18
    //   9: getstatic 184	com/amap/api/mapcore2d/b:aE	Landroid/graphics/Paint;
    //   12: astore_0
    //   13: ldc 2
    //   15: monitorexit
    //   16: aload_0
    //   17: areturn
    //   18: new 313	android/graphics/Paint
    //   21: dup
    //   22: invokespecial 314	android/graphics/Paint:<init>	()V
    //   25: putstatic 184	com/amap/api/mapcore2d/b:aE	Landroid/graphics/Paint;
    //   28: getstatic 184	com/amap/api/mapcore2d/b:aE	Landroid/graphics/Paint;
    //   31: ldc_w 315
    //   34: invokevirtual 319	android/graphics/Paint:setColor	(I)V
    //   37: getstatic 184	com/amap/api/mapcore2d/b:aE	Landroid/graphics/Paint;
    //   40: bipush 90
    //   42: invokevirtual 322	android/graphics/Paint:setAlpha	(I)V
    //   45: new 324	android/graphics/DashPathEffect
    //   48: dup
    //   49: iconst_2
    //   50: newarray <illegal type>
    //   52: dup
    //   53: iconst_0
    //   54: fconst_2
    //   55: fastore
    //   56: dup
    //   57: iconst_1
    //   58: ldc_w 325
    //   61: fastore
    //   62: fconst_1
    //   63: invokespecial 328	android/graphics/DashPathEffect:<init>	([FF)V
    //   66: astore_0
    //   67: getstatic 184	com/amap/api/mapcore2d/b:aE	Landroid/graphics/Paint;
    //   70: aload_0
    //   71: invokevirtual 332	android/graphics/Paint:setPathEffect	(Landroid/graphics/PathEffect;)Landroid/graphics/PathEffect;
    //   74: pop
    //   75: goto -66 -> 9
    //   78: astore_0
    //   79: ldc 2
    //   81: monitorexit
    //   82: aload_0
    //   83: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   12	59	0	localObject1	Object
    //   78	5	0	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   3	9	78	finally
    //   9	13	78	finally
    //   18	75	78	finally
  }
  
  private void U()
  {
    Object localObject = null;
    Method[] arrayOfMethod = View.class.getMethods();
    int i2 = arrayOfMethod.length;
    int i1 = 0;
    if (i1 >= i2) {}
    for (;;)
    {
      if (localObject != null) {
        break label58;
      }
      return;
      Method localMethod = arrayOfMethod[i1];
      if (!localMethod.getName().equals("setLayerType"))
      {
        i1 += 1;
        break;
      }
      localObject = localMethod;
    }
    try
    {
      label58:
      ((Method)localObject).invoke(this, new Object[] { Integer.valueOf(View.class.getField("LAYER_TYPE_SOFTWARE").getInt(null)), null });
      return;
    }
    catch (Exception localException)
    {
      cj.a(localException, "AMapDelegateImpGLSurfaceView", "setLayerType");
    }
  }
  
  private void V()
  {
    a(this.m);
    ViewGroup.LayoutParams localLayoutParams = new ViewGroup.LayoutParams(-1, -1);
    this.g.addView(this, 0, localLayoutParams);
  }
  
  private void W()
  {
    this.a.a();
    if (this.b == null) {}
    for (;;)
    {
      this.b = null;
      this.a = null;
      return;
      this.b.a(true);
      this.b.e();
    }
  }
  
  private void X()
  {
    if (!this.L)
    {
      if (this.W) {
        break label42;
      }
      label14:
      if (this.M) {
        break label67;
      }
      label21:
      this.V = false;
      if (this.Z != null) {
        break label92;
      }
    }
    label42:
    label67:
    label92:
    while (this.p == null)
    {
      return;
      this.L = false;
      break;
      this.W = false;
      l locall = l.a();
      locall.l = true;
      this.e.a(locall);
      break label14;
      this.M = false;
      locall = l.a();
      locall.l = true;
      this.e.a(locall);
      break label21;
    }
    this.Z.onMarkerDragEnd(this.p);
    this.p = null;
    this.q = null;
  }
  
  private void Y()
  {
    if (this.al != null)
    {
      int i1 = this.al.x;
      int i2 = this.aB;
      int i3 = this.al.y;
      int i4 = this.aC;
      this.al.x = this.aB;
      this.al.y = this.aC;
      this.b.b(i1 - i2, i3 - i4);
      return;
    }
  }
  
  private CameraPosition Z()
  {
    u localu = C();
    if (localu != null) {
      return CameraPosition.fromLatLngZoom(new LatLng(localu.b() / 1000000.0D, localu.a() / 1000000.0D), f());
    }
    return null;
  }
  
  private LatLng a(LatLng paramLatLng)
  {
    ad localad = new ad();
    b(paramLatLng.latitude, paramLatLng.longitude, localad);
    localad.b -= 60;
    paramLatLng = new r();
    a(localad.a, localad.b, paramLatLng);
    return new LatLng(paramLatLng.b, paramLatLng.a);
  }
  
  private void a(float paramFloat1, PointF paramPointF, float paramFloat2, float paramFloat3)
  {
    try
    {
      boolean bool = this.h.f();
      if (!bool) {
        break label171;
      }
    }
    catch (RemoteException paramPointF)
    {
      for (;;)
      {
        int i1;
        int i2;
        label171:
        cj.a(paramPointF, "AMapDelegateImpGLSurfaceView", "doScale");
      }
    }
    this.aG = 2;
    i1 = this.a.c.c() / 2;
    i2 = this.a.c.d() / 2;
    paramFloat1 = a((float)(Math.log(paramFloat1) / Math.log(2.0D) + this.a.c.e()));
    if (paramFloat1 != this.a.c.e())
    {
      this.c[0] = this.c[1];
      this.c[1] = paramFloat1;
      if (this.c[0] != this.c[1])
      {
        paramPointF = this.a.b.a(i1, i2);
        this.a.c.a(paramFloat1);
        this.a.c.a(paramPointF);
        aa();
      }
    }
    return;
  }
  
  private void a(int paramInt1, int paramInt2)
  {
    if (this.al != null)
    {
      this.aB = paramInt1;
      this.aC = paramInt2;
      Y();
      return;
    }
  }
  
  private void a(int paramInt1, int paramInt2, ad paramad)
  {
    f();
    Object localObject = new PointF(paramInt1, paramInt2);
    localObject = this.K.a((PointF)localObject, this.K.l, this.K.n, this.K.k, this.K.o);
    if (paramad == null) {
      return;
    }
    paramad.a = ((int)((u)localObject).e());
    paramad.b = ((int)((u)localObject).f());
  }
  
  private void a(Context paramContext)
  {
    this.al = null;
    this.am = new GestureDetector(paramContext, this);
    this.an = ba.a(paramContext, this);
    this.aq = new Scroller(paramContext);
    new DisplayMetrics();
    paramContext = paramContext.getApplicationContext().getResources().getDisplayMetrics();
    this.ay = paramContext.widthPixels;
    this.az = paramContext.heightPixels;
    this.ar = (paramContext.widthPixels / 2);
    this.as = (paramContext.heightPixels / 2);
  }
  
  /* Error */
  private void a(Context paramContext, android.util.AttributeSet paramAttributeSet)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokestatic 643	com/amap/api/mapcore2d/cl:c	(Landroid/content/Context;)Ljava/lang/String;
    //   4: putstatic 648	com/amap/api/mapcore2d/p:b	Ljava/lang/String;
    //   7: aload_0
    //   8: aload_1
    //   9: putfield 387	com/amap/api/mapcore2d/b:m	Landroid/content/Context;
    //   12: aload_0
    //   13: new 650	com/amap/api/mapcore2d/f
    //   16: dup
    //   17: aload_0
    //   18: getfield 387	com/amap/api/mapcore2d/b:m	Landroid/content/Context;
    //   21: aload_0
    //   22: invokespecial 653	com/amap/api/mapcore2d/f:<init>	(Landroid/content/Context;Lcom/amap/api/mapcore2d/w;)V
    //   25: putfield 250	com/amap/api/mapcore2d/b:ah	Ljava/lang/Thread;
    //   28: aload_0
    //   29: new 655	com/amap/api/mapcore2d/bg
    //   32: dup
    //   33: aload_0
    //   34: invokespecial 658	com/amap/api/mapcore2d/bg:<init>	(Lcom/amap/api/mapcore2d/w;)V
    //   37: putfield 660	com/amap/api/mapcore2d/b:T	Lcom/amap/api/mapcore2d/ag;
    //   40: aload_0
    //   41: sipush 222
    //   44: sipush 215
    //   47: sipush 214
    //   50: invokestatic 180	android/graphics/Color:rgb	(III)I
    //   53: invokevirtual 663	com/amap/api/mapcore2d/b:setBackgroundColor	(I)V
    //   56: invokestatic 668	com/amap/api/mapcore2d/m:a	()Lcom/amap/api/mapcore2d/m;
    //   59: aload_0
    //   60: invokevirtual 671	com/amap/api/mapcore2d/m:a	(Lcom/amap/api/mapcore2d/m$a;)V
    //   63: invokestatic 676	com/amap/api/mapcore2d/k:a	()Lcom/amap/api/mapcore2d/k;
    //   66: aload_0
    //   67: invokevirtual 679	com/amap/api/mapcore2d/k:a	(Lcom/amap/api/mapcore2d/k$a;)V
    //   70: aload_0
    //   71: new 681	com/amap/api/mapcore2d/a
    //   74: dup
    //   75: aload_0
    //   76: invokespecial 682	com/amap/api/mapcore2d/a:<init>	(Lcom/amap/api/mapcore2d/b;)V
    //   79: putfield 224	com/amap/api/mapcore2d/b:E	Lcom/amap/api/mapcore2d/a;
    //   82: aload_0
    //   83: new 684	com/amap/api/mapcore2d/c
    //   86: dup
    //   87: aload_0
    //   88: invokespecial 685	com/amap/api/mapcore2d/c:<init>	(Lcom/amap/api/mapcore2d/w;)V
    //   91: putfield 687	com/amap/api/mapcore2d/b:w	Lcom/amap/api/mapcore2d/c;
    //   94: aload_0
    //   95: new 689	com/amap/api/mapcore2d/j
    //   98: dup
    //   99: aload_1
    //   100: invokespecial 690	com/amap/api/mapcore2d/j:<init>	(Landroid/content/Context;)V
    //   103: putfield 692	com/amap/api/mapcore2d/b:I	Lcom/amap/api/mapcore2d/j;
    //   106: aload_0
    //   107: new 694	com/amap/api/mapcore2d/br
    //   110: dup
    //   111: aload_0
    //   112: getfield 387	com/amap/api/mapcore2d/b:m	Landroid/content/Context;
    //   115: aload_0
    //   116: invokespecial 695	com/amap/api/mapcore2d/br:<init>	(Landroid/content/Context;Lcom/amap/api/mapcore2d/w;)V
    //   119: putfield 697	com/amap/api/mapcore2d/b:i	Lcom/amap/api/mapcore2d/br;
    //   122: aload_0
    //   123: new 406	com/amap/api/mapcore2d/ay
    //   126: dup
    //   127: aload_0
    //   128: getfield 387	com/amap/api/mapcore2d/b:m	Landroid/content/Context;
    //   131: aload_0
    //   132: getstatic 699	com/amap/api/mapcore2d/p:j	I
    //   135: invokespecial 702	com/amap/api/mapcore2d/ay:<init>	(Landroid/content/Context;Lcom/amap/api/mapcore2d/b;I)V
    //   138: putfield 404	com/amap/api/mapcore2d/b:a	Lcom/amap/api/mapcore2d/ay;
    //   141: aload_0
    //   142: getfield 697	com/amap/api/mapcore2d/b:i	Lcom/amap/api/mapcore2d/br;
    //   145: iconst_1
    //   146: invokevirtual 703	com/amap/api/mapcore2d/br:a	(Z)V
    //   149: aload_0
    //   150: aload_0
    //   151: getfield 404	com/amap/api/mapcore2d/b:a	Lcom/amap/api/mapcore2d/ay;
    //   154: getfield 705	com/amap/api/mapcore2d/ay:i	Lcom/amap/api/mapcore2d/au;
    //   157: putfield 568	com/amap/api/mapcore2d/b:K	Lcom/amap/api/mapcore2d/au;
    //   160: aload_0
    //   161: new 412	com/amap/api/mapcore2d/ap
    //   164: dup
    //   165: aload_0
    //   166: getfield 404	com/amap/api/mapcore2d/b:a	Lcom/amap/api/mapcore2d/ay;
    //   169: invokespecial 708	com/amap/api/mapcore2d/ap:<init>	(Lcom/amap/api/mapcore2d/ay;)V
    //   172: putfield 410	com/amap/api/mapcore2d/b:b	Lcom/amap/api/mapcore2d/ap;
    //   175: aload_0
    //   176: new 710	com/amap/api/mapcore2d/bx
    //   179: dup
    //   180: aload_0
    //   181: invokespecial 711	com/amap/api/mapcore2d/bx:<init>	(Lcom/amap/api/mapcore2d/w;)V
    //   184: putfield 513	com/amap/api/mapcore2d/b:h	Lcom/amap/api/mapcore2d/ak;
    //   187: aload_0
    //   188: new 713	com/amap/api/mapcore2d/cb
    //   191: dup
    //   192: aload_0
    //   193: getfield 387	com/amap/api/mapcore2d/b:m	Landroid/content/Context;
    //   196: aload_0
    //   197: getfield 410	com/amap/api/mapcore2d/b:b	Lcom/amap/api/mapcore2d/ap;
    //   200: aload_0
    //   201: invokespecial 716	com/amap/api/mapcore2d/cb:<init>	(Landroid/content/Context;Lcom/amap/api/mapcore2d/ap;Lcom/amap/api/mapcore2d/w;)V
    //   204: putfield 718	com/amap/api/mapcore2d/b:f	Lcom/amap/api/mapcore2d/cb;
    //   207: aload_0
    //   208: new 398	com/amap/api/mapcore2d/at
    //   211: dup
    //   212: aload_0
    //   213: getfield 387	com/amap/api/mapcore2d/b:m	Landroid/content/Context;
    //   216: aload_0
    //   217: invokespecial 719	com/amap/api/mapcore2d/at:<init>	(Landroid/content/Context;Lcom/amap/api/mapcore2d/w;)V
    //   220: putfield 396	com/amap/api/mapcore2d/b:g	Lcom/amap/api/mapcore2d/at;
    //   223: aload_0
    //   224: new 721	com/amap/api/mapcore2d/ao
    //   227: dup
    //   228: aload_0
    //   229: getfield 387	com/amap/api/mapcore2d/b:m	Landroid/content/Context;
    //   232: aload_0
    //   233: getfield 220	com/amap/api/mapcore2d/b:e	Lcom/amap/api/mapcore2d/ar;
    //   236: aload_0
    //   237: invokespecial 724	com/amap/api/mapcore2d/ao:<init>	(Landroid/content/Context;Lcom/amap/api/mapcore2d/ar;Lcom/amap/api/mapcore2d/w;)V
    //   240: putfield 726	com/amap/api/mapcore2d/b:u	Lcom/amap/api/mapcore2d/ao;
    //   243: aload_0
    //   244: new 728	com/amap/api/mapcore2d/ca
    //   247: dup
    //   248: aload_0
    //   249: getfield 387	com/amap/api/mapcore2d/b:m	Landroid/content/Context;
    //   252: aload_0
    //   253: invokespecial 731	com/amap/api/mapcore2d/ca:<init>	(Landroid/content/Context;Lcom/amap/api/mapcore2d/b;)V
    //   256: putfield 733	com/amap/api/mapcore2d/b:A	Lcom/amap/api/mapcore2d/ca;
    //   259: aload_0
    //   260: new 735	com/amap/api/mapcore2d/bi
    //   263: dup
    //   264: aload_0
    //   265: getfield 387	com/amap/api/mapcore2d/b:m	Landroid/content/Context;
    //   268: aload_0
    //   269: invokespecial 736	com/amap/api/mapcore2d/bi:<init>	(Landroid/content/Context;Lcom/amap/api/mapcore2d/b;)V
    //   272: putfield 738	com/amap/api/mapcore2d/b:B	Lcom/amap/api/mapcore2d/bi;
    //   275: aload_0
    //   276: new 740	com/amap/api/mapcore2d/o
    //   279: dup
    //   280: aload_0
    //   281: getfield 387	com/amap/api/mapcore2d/b:m	Landroid/content/Context;
    //   284: aload_0
    //   285: getfield 220	com/amap/api/mapcore2d/b:e	Lcom/amap/api/mapcore2d/ar;
    //   288: aload_0
    //   289: invokespecial 741	com/amap/api/mapcore2d/o:<init>	(Landroid/content/Context;Lcom/amap/api/mapcore2d/ar;Lcom/amap/api/mapcore2d/w;)V
    //   292: putfield 743	com/amap/api/mapcore2d/b:D	Lcom/amap/api/mapcore2d/o;
    //   295: aload_0
    //   296: new 745	com/amap/api/mapcore2d/as
    //   299: dup
    //   300: aload_0
    //   301: getfield 387	com/amap/api/mapcore2d/b:m	Landroid/content/Context;
    //   304: aload_2
    //   305: aload_0
    //   306: invokespecial 748	com/amap/api/mapcore2d/as:<init>	(Landroid/content/Context;Landroid/util/AttributeSet;Lcom/amap/api/mapcore2d/b;)V
    //   309: putfield 750	com/amap/api/mapcore2d/b:j	Lcom/amap/api/mapcore2d/as;
    //   312: new 391	android/view/ViewGroup$LayoutParams
    //   315: dup
    //   316: iconst_m1
    //   317: iconst_m1
    //   318: invokespecial 394	android/view/ViewGroup$LayoutParams:<init>	(II)V
    //   321: astore_1
    //   322: aload_0
    //   323: invokespecial 752	com/amap/api/mapcore2d/b:V	()V
    //   326: aload_0
    //   327: getfield 396	com/amap/api/mapcore2d/b:g	Lcom/amap/api/mapcore2d/at;
    //   330: aload_0
    //   331: getfield 697	com/amap/api/mapcore2d/b:i	Lcom/amap/api/mapcore2d/br;
    //   334: aload_1
    //   335: invokevirtual 755	com/amap/api/mapcore2d/at:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   338: aload_0
    //   339: getfield 396	com/amap/api/mapcore2d/b:g	Lcom/amap/api/mapcore2d/at;
    //   342: aload_0
    //   343: getfield 733	com/amap/api/mapcore2d/b:A	Lcom/amap/api/mapcore2d/ca;
    //   346: aload_1
    //   347: invokevirtual 755	com/amap/api/mapcore2d/at:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   350: aload_0
    //   351: getfield 396	com/amap/api/mapcore2d/b:g	Lcom/amap/api/mapcore2d/at;
    //   354: aload_0
    //   355: getfield 738	com/amap/api/mapcore2d/b:B	Lcom/amap/api/mapcore2d/bi;
    //   358: aload_1
    //   359: invokevirtual 755	com/amap/api/mapcore2d/at:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   362: new 757	com/amap/api/mapcore2d/at$a
    //   365: dup
    //   366: aload_1
    //   367: invokespecial 760	com/amap/api/mapcore2d/at$a:<init>	(Landroid/view/ViewGroup$LayoutParams;)V
    //   370: astore_1
    //   371: aload_0
    //   372: getfield 396	com/amap/api/mapcore2d/b:g	Lcom/amap/api/mapcore2d/at;
    //   375: aload_0
    //   376: getfield 750	com/amap/api/mapcore2d/b:j	Lcom/amap/api/mapcore2d/as;
    //   379: aload_1
    //   380: invokevirtual 755	com/amap/api/mapcore2d/at:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   383: new 757	com/amap/api/mapcore2d/at$a
    //   386: dup
    //   387: bipush -2
    //   389: bipush -2
    //   391: new 456	com/amap/api/maps2d/model/LatLng
    //   394: dup
    //   395: dconst_0
    //   396: dconst_0
    //   397: invokespecial 467	com/amap/api/maps2d/model/LatLng:<init>	(DD)V
    //   400: iconst_0
    //   401: iconst_0
    //   402: bipush 83
    //   404: invokespecial 763	com/amap/api/mapcore2d/at$a:<init>	(IILcom/amap/api/maps2d/model/LatLng;III)V
    //   407: astore_1
    //   408: aload_0
    //   409: getfield 396	com/amap/api/mapcore2d/b:g	Lcom/amap/api/mapcore2d/at;
    //   412: aload_0
    //   413: getfield 718	com/amap/api/mapcore2d/b:f	Lcom/amap/api/mapcore2d/cb;
    //   416: aload_1
    //   417: invokevirtual 755	com/amap/api/mapcore2d/at:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   420: new 757	com/amap/api/mapcore2d/at$a
    //   423: dup
    //   424: bipush -2
    //   426: bipush -2
    //   428: new 456	com/amap/api/maps2d/model/LatLng
    //   431: dup
    //   432: dconst_0
    //   433: dconst_0
    //   434: invokespecial 467	com/amap/api/maps2d/model/LatLng:<init>	(DD)V
    //   437: iconst_0
    //   438: iconst_0
    //   439: bipush 83
    //   441: invokespecial 763	com/amap/api/mapcore2d/at$a:<init>	(IILcom/amap/api/maps2d/model/LatLng;III)V
    //   444: astore_1
    //   445: aload_0
    //   446: getfield 396	com/amap/api/mapcore2d/b:g	Lcom/amap/api/mapcore2d/at;
    //   449: aload_0
    //   450: getfield 726	com/amap/api/mapcore2d/b:u	Lcom/amap/api/mapcore2d/ao;
    //   453: aload_1
    //   454: invokevirtual 755	com/amap/api/mapcore2d/at:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   457: aload_0
    //   458: invokevirtual 766	com/amap/api/mapcore2d/b:q	()Lcom/amap/api/mapcore2d/ak;
    //   461: invokeinterface 768 1 0
    //   466: istore_3
    //   467: iload_3
    //   468: ifeq +98 -> 566
    //   471: aload_0
    //   472: getfield 743	com/amap/api/mapcore2d/b:D	Lcom/amap/api/mapcore2d/o;
    //   475: bipush 8
    //   477: invokevirtual 771	com/amap/api/mapcore2d/o:setVisibility	(I)V
    //   480: new 757	com/amap/api/mapcore2d/at$a
    //   483: dup
    //   484: bipush -2
    //   486: bipush -2
    //   488: new 456	com/amap/api/maps2d/model/LatLng
    //   491: dup
    //   492: dconst_0
    //   493: dconst_0
    //   494: invokespecial 467	com/amap/api/maps2d/model/LatLng:<init>	(DD)V
    //   497: iconst_0
    //   498: iconst_0
    //   499: bipush 51
    //   501: invokespecial 763	com/amap/api/mapcore2d/at$a:<init>	(IILcom/amap/api/maps2d/model/LatLng;III)V
    //   504: astore_1
    //   505: aload_0
    //   506: getfield 396	com/amap/api/mapcore2d/b:g	Lcom/amap/api/mapcore2d/at;
    //   509: aload_0
    //   510: getfield 743	com/amap/api/mapcore2d/b:D	Lcom/amap/api/mapcore2d/o;
    //   513: aload_1
    //   514: invokevirtual 755	com/amap/api/mapcore2d/at:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   517: aload_0
    //   518: new 773	com/amap/api/mapcore2d/bb
    //   521: dup
    //   522: aload_0
    //   523: invokespecial 774	com/amap/api/mapcore2d/bb:<init>	(Lcom/amap/api/mapcore2d/w;)V
    //   526: putfield 776	com/amap/api/mapcore2d/b:z	Lcom/amap/api/mapcore2d/bb;
    //   529: aload_0
    //   530: getfield 718	com/amap/api/mapcore2d/b:f	Lcom/amap/api/mapcore2d/cb;
    //   533: getstatic 779	com/amap/api/mapcore2d/g:a	I
    //   536: invokevirtual 782	com/amap/api/mapcore2d/cb:setId	(I)V
    //   539: aload_0
    //   540: getfield 250	com/amap/api/mapcore2d/b:ah	Ljava/lang/Thread;
    //   543: ldc_w 784
    //   546: invokevirtual 790	java/lang/Thread:setName	(Ljava/lang/String;)V
    //   549: aload_0
    //   550: getfield 250	com/amap/api/mapcore2d/b:ah	Ljava/lang/Thread;
    //   553: invokevirtual 793	java/lang/Thread:start	()V
    //   556: aload_0
    //   557: getfield 248	com/amap/api/mapcore2d/b:ag	Ljava/util/Timer;
    //   560: astore_1
    //   561: aload_1
    //   562: ifnull +30 -> 592
    //   565: return
    //   566: aload_0
    //   567: getfield 726	com/amap/api/mapcore2d/b:u	Lcom/amap/api/mapcore2d/ao;
    //   570: bipush 8
    //   572: invokevirtual 794	com/amap/api/mapcore2d/ao:setVisibility	(I)V
    //   575: goto -104 -> 471
    //   578: astore_1
    //   579: aload_1
    //   580: ldc_w 380
    //   583: ldc_w 796
    //   586: invokestatic 385	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   589: goto -118 -> 471
    //   592: aload_0
    //   593: new 798	java/util/Timer
    //   596: dup
    //   597: invokespecial 799	java/util/Timer:<init>	()V
    //   600: putfield 248	com/amap/api/mapcore2d/b:ag	Ljava/util/Timer;
    //   603: aload_0
    //   604: getfield 248	com/amap/api/mapcore2d/b:ag	Ljava/util/Timer;
    //   607: aload_0
    //   608: getfield 253	com/amap/api/mapcore2d/b:ai	Ljava/util/TimerTask;
    //   611: ldc2_w 800
    //   614: ldc2_w 802
    //   617: invokevirtual 807	java/util/Timer:schedule	(Ljava/util/TimerTask;JJ)V
    //   620: return
    //   621: astore_1
    //   622: aload_1
    //   623: ldc_w 380
    //   626: ldc_w 796
    //   629: invokestatic 385	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   632: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	633	0	this	b
    //   0	633	1	paramContext	Context
    //   0	633	2	paramAttributeSet	android.util.AttributeSet
    //   466	2	3	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   457	467	578	android/os/RemoteException
    //   566	575	578	android/os/RemoteException
    //   539	561	621	java/lang/Throwable
    //   592	620	621	java/lang/Throwable
  }
  
  private void a(MotionEvent paramMotionEvent)
  {
    if (!this.V) {}
    do
    {
      do
      {
        do
        {
          return;
        } while ((this.q == null) || (this.p == null));
        int i1 = (int)paramMotionEvent.getX();
        int i2 = (int)(paramMotionEvent.getY() - 60.0F);
        paramMotionEvent = new r();
        a(i1, i2, paramMotionEvent);
        paramMotionEvent = new LatLng(paramMotionEvent.b, paramMotionEvent.a);
      } while ((this.q == null) || (!this.q.h()));
      this.q.a(paramMotionEvent);
    } while (this.Z == null);
    this.Z.onMarkerDrag(this.p);
  }
  
  private void a(boolean paramBoolean, CameraPosition paramCameraPosition)
  {
    if ((this.H == null) || ((!this.I.a()) || ((!isEnabled()) || (paramCameraPosition != null)))) {}
    for (;;)
    {
      try
      {
        this.H.onCameraChangeFinish(paramCameraPosition);
        return;
      }
      catch (Throwable paramCameraPosition)
      {
        paramCameraPosition.printStackTrace();
      }
      return;
      return;
      return;
      try
      {
        CameraPosition localCameraPosition = g();
        paramCameraPosition = localCameraPosition;
      }
      catch (RemoteException localRemoteException)
      {
        cj.a(localRemoteException, "AMapDelegateImpGLSurfaceView", "cameraChangeFinish");
      }
    }
  }
  
  private void aa()
  {
    int i2 = 50;
    int i1;
    int i4;
    if (this.B != null) {
      if (this.l == -1.0F)
      {
        i1 = getWidth();
        i4 = getHeight();
        int i3 = this.m.getResources().getDisplayMetrics().densityDpi;
        if (i3 <= 120) {
          break label233;
        }
        if (i3 <= 160) {
          break label240;
        }
        if (i3 <= 240) {
          break label267;
        }
        i1 = i2;
        if (i3 > 320)
        {
          i1 = i2;
          if (i3 > 480) {
            i1 = 40;
          }
        }
      }
    }
    for (;;)
    {
      this.l = (i1 / 100.0F);
      Object localObject = ab();
      if (localObject == null) {
        break;
      }
      float f1 = f();
      float f2 = this.l;
      double d1 = (float)(Math.cos(((LatLng)localObject).latitude * 3.141592653589793D / 180.0D) * 2.0D * 3.141592653589793D * 6378137.0D / (Math.pow(2.0D, f1) * 256.0D));
      i1 = (int)(this.r[((int)f1)] / (f2 * d1));
      localObject = cj.a(this.r[((int)f1)]);
      this.B.a(i1);
      this.B.a((String)localObject);
      this.B.invalidate();
      return;
      return;
      label233:
      i1 = 100;
      continue;
      label240:
      if (Math.max(i1, i4) > 480)
      {
        i1 = 100;
      }
      else
      {
        i1 = 120;
        continue;
        label267:
        if (Math.min(i1, i4) < 1000) {
          i1 = 70;
        } else {
          i1 = 60;
        }
      }
    }
  }
  
  private LatLng ab()
  {
    u localu = C();
    if (localu != null) {
      return new LatLng(q.a(localu.b()), q.a(localu.a()));
    }
    return null;
  }
  
  private ad ac()
  {
    u localu = C();
    ad localad = new ad();
    localad.a = ((int)localu.e());
    localad.b = ((int)localu.f());
    return localad;
  }
  
  private boolean b(MotionEvent paramMotionEvent)
  {
    boolean bool = this.an.a(paramMotionEvent, getWidth(), getHeight());
    if (bool)
    {
      if (paramMotionEvent.getAction() == 1) {
        break label59;
      }
      label29:
      if (paramMotionEvent.getAction() == 2) {
        break label75;
      }
    }
    for (;;)
    {
      if (paramMotionEvent.getAction() == 1) {
        break label83;
      }
      return bool;
      bool = this.am.onTouchEvent(paramMotionEvent);
      break;
      label59:
      if (!this.W) {
        break label29;
      }
      k.a().b();
      break label29;
      label75:
      a(paramMotionEvent);
    }
    label83:
    X();
    return bool;
  }
  
  Point A()
  {
    return this.A.c();
  }
  
  public boolean B()
  {
    return this.o;
  }
  
  public u C()
  {
    if (this.a == null) {}
    while (this.a.c == null) {
      return null;
    }
    return this.a.c.f();
  }
  
  public ap D()
  {
    return this.b;
  }
  
  public boolean E()
  {
    if (this.a == null) {}
    while (this.a.e == null) {
      return false;
    }
    am localam = b().e.b(b().e.h);
    if (localam == null) {
      return false;
    }
    return localam.a();
  }
  
  public boolean F()
  {
    Object localObject;
    if (b() != null)
    {
      localObject = b().e.i;
      localObject = b().e.b((String)localObject);
      if (localObject == null) {
        return false;
      }
    }
    else
    {
      return false;
    }
    return ((am)localObject).a();
  }
  
  public ba G()
  {
    return this.an;
  }
  
  public float J()
  {
    return this.au;
  }
  
  public void K()
  {
    this.aw = 0.0F;
    this.ax = 0.0F;
  }
  
  public int L()
  {
    return 0;
  }
  
  protected void M()
  {
    this.k.sendEmptyMessage(10);
  }
  
  void N()
  {
    this.k.sendEmptyMessage(15);
  }
  
  public void O()
  {
    if (this.J == null) {
      return;
    }
    this.J.onCancel();
    this.J = null;
  }
  
  public void P() {}
  
  public void Q()
  {
    this.k.sendEmptyMessage(17);
  }
  
  public void R()
  {
    postInvalidate();
    this.g.postInvalidate();
  }
  
  public List<Marker> S()
  {
    if (cj.a(getWidth(), getHeight())) {
      return this.j.g();
    }
    return new ArrayList();
  }
  
  public void T()
  {
    a(null);
  }
  
  public float a(float paramFloat)
  {
    float f1 = paramFloat;
    if (paramFloat < this.a.c.b()) {
      f1 = this.a.c.b();
    }
    paramFloat = f1;
    if (f1 > this.a.c.a()) {
      paramFloat = this.a.c.a();
    }
    return paramFloat;
  }
  
  public Handler a()
  {
    return this.k;
  }
  
  public ae a(PolygonOptions paramPolygonOptions)
    throws RemoteException
  {
    bd localbd = new bd(this);
    localbd.a(paramPolygonOptions.getFillColor());
    localbd.a(paramPolygonOptions.getPoints());
    localbd.a(paramPolygonOptions.isVisible());
    localbd.b(paramPolygonOptions.getStrokeWidth());
    localbd.a(paramPolygonOptions.getZIndex());
    localbd.b(paramPolygonOptions.getStrokeColor());
    if (this.a != null)
    {
      this.a.g.a(localbd);
      invalidate();
      return localbd;
    }
    return null;
  }
  
  public af a(PolylineOptions paramPolylineOptions)
    throws RemoteException
  {
    be localbe = new be(this);
    localbe.a(paramPolylineOptions.getColor());
    localbe.b(paramPolylineOptions.isDottedLine());
    localbe.c(paramPolylineOptions.isGeodesic());
    localbe.a(paramPolylineOptions.getPoints());
    localbe.a(paramPolylineOptions.isVisible());
    localbe.b(paramPolylineOptions.getWidth());
    localbe.a(paramPolylineOptions.getZIndex());
    if (this.a != null)
    {
      b().g.a(localbe);
      invalidate();
      return localbe;
    }
    return null;
  }
  
  public x a(CircleOptions paramCircleOptions)
    throws RemoteException
  {
    n localn = new n(this);
    localn.b(paramCircleOptions.getFillColor());
    localn.a(paramCircleOptions.getCenter());
    localn.a(paramCircleOptions.isVisible());
    localn.b(paramCircleOptions.getStrokeWidth());
    localn.a(paramCircleOptions.getZIndex());
    localn.a(paramCircleOptions.getStrokeColor());
    localn.a(paramCircleOptions.getRadius());
    if (this.a != null)
    {
      this.a.g.a(localn);
      invalidate();
      return localn;
    }
    return null;
  }
  
  public y a(GroundOverlayOptions paramGroundOverlayOptions)
    throws RemoteException
  {
    v localv = new v(this);
    localv.b(paramGroundOverlayOptions.getAnchorU(), paramGroundOverlayOptions.getAnchorV());
    localv.c(paramGroundOverlayOptions.getBearing());
    localv.a(paramGroundOverlayOptions.getWidth(), paramGroundOverlayOptions.getHeight());
    localv.a(paramGroundOverlayOptions.getImage());
    localv.a(paramGroundOverlayOptions.getLocation());
    localv.a(paramGroundOverlayOptions.getBounds());
    localv.d(paramGroundOverlayOptions.getTransparency());
    localv.a(paramGroundOverlayOptions.isVisible());
    localv.a(paramGroundOverlayOptions.getZIndex());
    if (this.a != null)
    {
      this.a.g.a(localv);
      invalidate();
      return localv;
    }
    return null;
  }
  
  public Marker a(MarkerOptions paramMarkerOptions)
    throws RemoteException
  {
    paramMarkerOptions = new ax(paramMarkerOptions, this.j);
    this.j.a(paramMarkerOptions);
    invalidate();
    return new Marker(paramMarkerOptions);
  }
  
  public Text a(TextOptions paramTextOptions)
    throws RemoteException
  {
    paramTextOptions = new bm(this, paramTextOptions, this.j);
    this.j.a(paramTextOptions);
    invalidate();
    return new Text(paramTextOptions);
  }
  
  public TileOverlay a(TileOverlayOptions paramTileOverlayOptions)
    throws RemoteException
  {
    if (this.a != null)
    {
      paramTileOverlayOptions = new bq(paramTileOverlayOptions, this.i, this.a.i, this.a, this.m);
      this.i.a(paramTileOverlayOptions);
      invalidate();
      return new TileOverlay(paramTileOverlayOptions);
    }
    return null;
  }
  
  public void a(double paramDouble1, double paramDouble2, ad paramad)
  {
    u localu = this.K.b(new u((int)(paramDouble1 * 1000000.0D), (int)(1000000.0D * paramDouble2)));
    paramad.a = localu.a();
    paramad.b = localu.b();
  }
  
  public void a(double paramDouble1, double paramDouble2, r paramr)
  {
    f();
    Object localObject = new u((int)q.a(paramDouble1), (int)q.a(paramDouble2));
    localObject = this.K.b((u)localObject, this.K.l, this.K.n, this.K.k);
    if (paramr == null) {
      return;
    }
    paramr.a = ((PointF)localObject).x;
    paramr.b = ((PointF)localObject).y;
  }
  
  public void a(float paramFloat, Point paramPoint, boolean paramBoolean)
  {
    if (this.b != null)
    {
      float f1 = f();
      if (cj.b(f1 + paramFloat) - f1 != 0.0F) {}
    }
    else
    {
      return;
    }
    new ad();
    ad localad1 = ac();
    if (paramPoint == null) {
      return;
    }
    ad localad2 = new ad();
    a(paramPoint.x, paramPoint.y, localad2);
    int i2 = localad1.a - localad2.a;
    int i1 = localad1.b - localad2.b;
    i2 = (int)(i2 / Math.pow(2.0D, paramFloat) - i2);
    i1 = (int)(i1 / Math.pow(2.0D, paramFloat) - i1);
    localad1.a = (i2 + localad2.a);
    localad2.b += i1;
    paramPoint = new u(localad1.b, localad1.a, false);
    paramPoint = this.a.i.b(paramPoint);
    if (!paramBoolean)
    {
      this.b.a(paramPoint);
      k.a().b();
      return;
    }
    this.b.a(paramPoint, 1000);
  }
  
  public void a(int paramInt)
    throws RemoteException
  {
    if (paramInt != 2)
    {
      this.t = 1;
      h(false);
      this.A.a(false);
    }
    for (;;)
    {
      postInvalidate();
      return;
      this.t = 2;
      h(true);
      this.A.a(true);
    }
  }
  
  public void a(int paramInt1, int paramInt2, r paramr)
  {
    Object localObject = new PointF(paramInt1, paramInt2);
    localObject = this.K.a((PointF)localObject, this.K.l, this.K.n, this.K.k, this.K.o);
    if (paramr == null) {
      return;
    }
    double d1 = q.a(((u)localObject).b());
    double d2 = q.a(((u)localObject).a());
    paramr.b = d1;
    paramr.a = d2;
  }
  
  public void a(Location paramLocation)
  {
    if (paramLocation != null)
    {
      for (;;)
      {
        LatLng localLatLng;
        try
        {
          localLatLng = new LatLng(paramLocation.getLatitude(), paramLocation.getLongitude());
          try
          {
            if (!n())
            {
              this.z.a();
              this.z = null;
              return;
            }
            if (this.C == null) {
              continue;
            }
            if (this.z != null) {
              continue;
            }
            bb localbb = this.z;
            if (localbb == null) {
              continue;
            }
            if (localLatLng != null) {
              break label152;
            }
          }
          catch (RemoteException localRemoteException)
          {
            cj.a(localRemoteException, "AMapDelegateImpGLSurfaceView", "showMyLocationOverlay");
            continue;
          }
          this.z.a(localLatLng, paramLocation.getAccuracy());
          if (this.x != null) {
            break;
          }
          this.v = new Location(paramLocation);
          return;
        }
        catch (Throwable paramLocation)
        {
          paramLocation.printStackTrace();
          return;
        }
        if (this.v != null)
        {
          continue;
          this.z = new bb(this);
          continue;
          label152:
          a(l.a(localLatLng, this.a.c.e()));
        }
      }
      if (this.v == null) {}
      for (;;)
      {
        this.x.onMyLocationChange(paramLocation);
        break;
        if ((this.v.getBearing() == paramLocation.getBearing()) && (this.v.getAccuracy() == paramLocation.getAccuracy()) && (this.v.getLatitude() == paramLocation.getLatitude()))
        {
          double d1 = this.v.getLongitude();
          double d2 = paramLocation.getLongitude();
          if (d1 == d2) {
            break;
          }
        }
      }
    }
  }
  
  public void a(aa paramaa)
    throws RemoteException
  {
    i2 = -2;
    if (paramaa != null)
    {
      if (paramaa.f() == null) {
        break label282;
      }
      t();
      localObject1 = new Marker(paramaa);
      if (this.P != null) {
        break label292;
      }
    }
    for (;;)
    {
      try
      {
        localObject2 = this.S;
        if (localObject2 == null) {
          continue;
        }
      }
      catch (Exception localException)
      {
        Object localObject2;
        TextView localTextView;
        label282:
        label292:
        cj.a(localException, "AMapDelegateImpGLSurfaceView", "showInfoWindow");
        continue;
        if (this.P == null) {
          continue;
        }
        this.N = this.P.getInfoContents((Marker)localObject1);
        continue;
        if (this.N.getBackground() != null) {
          continue;
        }
        this.N.setBackgroundDrawable(this.S);
        continue;
        int i1 = ((ViewGroup.LayoutParams)localObject1).width;
        i2 = ((ViewGroup.LayoutParams)localObject1).height;
        continue;
      }
      if (this.N == null) {
        continue;
      }
      if (this.N != null) {
        continue;
      }
      localObject1 = new LinearLayout(this.m);
      ((LinearLayout)localObject1).setBackgroundDrawable(this.S);
      localObject2 = new TextView(this.m);
      ((TextView)localObject2).setText(paramaa.f());
      ((TextView)localObject2).setTextColor(-16777216);
      localTextView = new TextView(this.m);
      localTextView.setTextColor(-16777216);
      localTextView.setText(paramaa.g());
      ((LinearLayout)localObject1).setOrientation(1);
      ((LinearLayout)localObject1).addView((View)localObject2);
      ((LinearLayout)localObject1).addView(localTextView);
      this.N = ((View)localObject1);
      localObject1 = this.N.getLayoutParams();
      this.N.setDrawingCacheEnabled(true);
      this.N.setDrawingCacheQuality(0);
      localObject2 = paramaa.e();
      if (localObject1 != null) {
        continue;
      }
      i1 = -2;
      localObject1 = new at.a(i1, i2, paramaa.c(), -(int)((r)localObject2).a + paramaa.n() / 2, -(int)((r)localObject2).b + 2, 81);
      this.Q = ((ax)paramaa);
      this.g.addView(this.N, (ViewGroup.LayoutParams)localObject1);
      return;
      return;
      if (paramaa.g() != null) {
        break;
      }
      return;
      this.N = this.P.getInfoWindow((Marker)localObject1);
      continue;
      this.S = bc.a(this.m, "infowindow_bg2d.9.png");
    }
  }
  
  public void a(l paraml)
    throws RemoteException
  {
    this.E.a(paraml);
    Q();
  }
  
  public void a(l paraml, long paramLong, AMap.CancelableCallback paramCancelableCallback)
    throws RemoteException
  {
    if (paraml.a != l.a.j)
    {
      if (this.b == null) {
        break label151;
      }
      if (paramCancelableCallback != null) {
        break label152;
      }
      label22:
      if (this.b.f()) {
        break label161;
      }
      label32:
      if (paramCancelableCallback != null) {
        break label171;
      }
      label37:
      if (this.F) {
        break label180;
      }
      label44:
      if (paraml.a == l.a.h) {
        break label188;
      }
      if (paraml.a == l.a.b) {
        break label230;
      }
      if (paraml.a == l.a.e) {
        break label239;
      }
      if (paraml.a == l.a.f) {
        break label248;
      }
      if (paraml.a == l.a.g) {
        break label265;
      }
      if (paraml.a == l.a.i) {
        break label279;
      }
      if (paraml.a == l.a.c) {
        break label345;
      }
      if (paraml.a != l.a.j) {
        break label399;
      }
    }
    label151:
    label152:
    label161:
    label171:
    label180:
    label188:
    label230:
    label239:
    label248:
    label265:
    label279:
    label345:
    label399:
    while (paraml.a == l.a.k)
    {
      M();
      a(paraml, true, paramLong);
      return;
      if (cj.a(getWidth(), getHeight())) {
        break;
      }
      return;
      return;
      this.J = paramCancelableCallback;
      break label22;
      this.b.g();
      break label32;
      this.J = paramCancelableCallback;
      break label37;
      this.G = true;
      break label44;
      M();
      if (this.a != null)
      {
        if (this.n)
        {
          this.b.b((int)paraml.b, (int)paraml.c);
          postInvalidate();
        }
      }
      else {
        return;
      }
      return;
      this.b.c();
      return;
      this.b.d();
      return;
      float f1 = paraml.d;
      this.b.c(f1);
      return;
      a(paraml.e, paraml.k, true);
      return;
      paraml = paraml.f;
      this.b.c(paraml.zoom);
      int i1 = (int)(paraml.target.latitude * 1000000.0D);
      int i2 = (int)(paraml.target.longitude * 1000000.0D);
      this.b.a(new u(i1, i2), (int)paramLong);
      return;
      paraml = paraml.f;
      i1 = (int)(paraml.target.latitude * 1000000.0D);
      i2 = (int)(paraml.target.longitude * 1000000.0D);
      this.b.a(new u(i1, i2), (int)paramLong);
      return;
    }
    paraml.l = true;
    this.e.a(paraml);
  }
  
  public void a(l paraml, AMap.CancelableCallback paramCancelableCallback)
    throws RemoteException
  {
    a(paraml, 250L, paramCancelableCallback);
  }
  
  protected void a(l paraml, boolean paramBoolean, long paramLong)
  {
    if (this.b != null) {
      try
      {
        Object localObject = paraml.g;
        float f1 = (float)(((LatLngBounds)localObject).northeast.latitude * 1000000.0D - ((LatLngBounds)localObject).southwest.latitude * 1000000.0D);
        float f2 = (float)(((LatLngBounds)localObject).northeast.longitude * 1000000.0D - ((LatLngBounds)localObject).southwest.longitude * 1000000.0D);
        localObject = new u((int)((((LatLngBounds)localObject).northeast.latitude * 1000000.0D + ((LatLngBounds)localObject).southwest.latitude * 1000000.0D) / 2.0D), (int)((((LatLngBounds)localObject).northeast.longitude * 1000000.0D + ((LatLngBounds)localObject).southwest.longitude * 1000000.0D) / 2.0D));
        if (!paramBoolean) {
          this.b.a((u)localObject);
        }
        for (;;)
        {
          this.b.a(f1, f2, paraml.i, paraml.j, paraml.h);
          k.a().b();
          return;
          this.b.a((u)localObject, (int)paramLong);
        }
        return;
      }
      catch (Exception paraml)
      {
        cj.a(paraml, "AMapDelegateImpGLSurfaceView", "newLatLngBoundsWithSize");
        return;
      }
    }
  }
  
  public void a(AMap.InfoWindowAdapter paramInfoWindowAdapter)
    throws RemoteException
  {
    this.P = paramInfoWindowAdapter;
  }
  
  public void a(AMap.OnCacheRemoveListener paramOnCacheRemoveListener)
  {
    if (this.aj == null) {
      return;
    }
    try
    {
      paramOnCacheRemoveListener = new b(this.m, paramOnCacheRemoveListener);
      this.aj.removeCallbacks(paramOnCacheRemoveListener);
      this.aj.post(paramOnCacheRemoveListener);
      return;
    }
    catch (Throwable paramOnCacheRemoveListener)
    {
      db.b(paramOnCacheRemoveListener, "AMapDelegateImpGLSurfaceView", "removecache");
      paramOnCacheRemoveListener.printStackTrace();
    }
  }
  
  public void a(AMap.OnCameraChangeListener paramOnCameraChangeListener)
    throws RemoteException
  {
    this.H = paramOnCameraChangeListener;
  }
  
  public void a(AMap.OnInfoWindowClickListener paramOnInfoWindowClickListener)
    throws RemoteException
  {
    this.O = paramOnInfoWindowClickListener;
  }
  
  public void a(AMap.OnMapClickListener paramOnMapClickListener)
    throws RemoteException
  {
    this.ad = paramOnMapClickListener;
  }
  
  public void a(AMap.OnMapLoadedListener paramOnMapLoadedListener)
    throws RemoteException
  {
    this.ac = paramOnMapLoadedListener;
  }
  
  public void a(AMap.OnMapLongClickListener paramOnMapLongClickListener)
    throws RemoteException
  {
    this.ab = paramOnMapLongClickListener;
  }
  
  public void a(AMap.OnMapScreenShotListener paramOnMapScreenShotListener)
  {
    this.af = paramOnMapScreenShotListener;
    this.U = true;
  }
  
  public void a(AMap.OnMapTouchListener paramOnMapTouchListener)
    throws RemoteException
  {
    this.aa = paramOnMapTouchListener;
  }
  
  public void a(AMap.OnMarkerClickListener paramOnMarkerClickListener)
    throws RemoteException
  {
    this.R = paramOnMarkerClickListener;
  }
  
  public void a(AMap.OnMarkerDragListener paramOnMarkerDragListener)
    throws RemoteException
  {
    this.Z = paramOnMarkerDragListener;
  }
  
  public void a(AMap.OnMyLocationChangeListener paramOnMyLocationChangeListener)
    throws RemoteException
  {
    this.x = paramOnMyLocationChangeListener;
  }
  
  public void a(LocationSource paramLocationSource)
    throws RemoteException
  {
    this.C = paramLocationSource;
    if (paramLocationSource == null)
    {
      this.u.a(false);
      return;
    }
    this.u.a(true);
  }
  
  public void a(MyLocationStyle paramMyLocationStyle)
    throws RemoteException
  {
    if (o() == null) {
      return;
    }
    o().a(paramMyLocationStyle);
  }
  
  public void a(boolean paramBoolean)
    throws RemoteException
  {}
  
  public boolean a(float paramFloat1, float paramFloat2)
  {
    this.b.a(true);
    if (!this.av) {}
    for (;;)
    {
      invalidate();
      return this.av;
      this.aw += paramFloat1;
      this.ax += paramFloat2;
    }
  }
  
  public boolean a(float paramFloat, PointF paramPointF)
  {
    try
    {
      boolean bool = this.h.f();
      if (!bool) {
        break label65;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        label65:
        cj.a(localRemoteException, "AMapDelegateImpGLSurfaceView", "onScale");
      }
    }
    this.a.e.c = false;
    M();
    a(paramFloat, paramPointF, this.aw, this.ax);
    this.av = false;
    postInvalidateDelayed(8L);
    this.a.a(true);
    return true;
    return false;
  }
  
  public boolean a(Matrix paramMatrix)
  {
    return false;
  }
  
  public boolean a(PointF paramPointF)
  {
    try
    {
      bool = this.h.f();
      if (!bool) {}
    }
    catch (RemoteException paramPointF)
    {
      for (;;)
      {
        boolean bool;
        label28:
        cj.a(paramPointF, "AMapDelegateImpGLSurfaceView", "startScale");
      }
    }
    try
    {
      bool = q().f();
      if (!bool) {
        break label84;
      }
    }
    catch (RemoteException paramPointF)
    {
      cj.a(paramPointF, "AMapDelegateImpGLSurfaceView", "startScale");
      break label28;
    }
    this.a.a(this.o);
    this.a.e.a(true);
    this.a.e.c = true;
    this.av = true;
    return true;
    return false;
    label84:
    return false;
  }
  
  public boolean a(String paramString)
    throws RemoteException
  {
    if (this.a != null) {
      return this.a.g.b(paramString);
    }
    return false;
  }
  
  protected PointF b(PointF paramPointF)
  {
    PointF localPointF = new PointF();
    int i1 = getWidth();
    int i2 = getHeight();
    float f1 = paramPointF.x - (i1 >> 1);
    float f2 = paramPointF.y - (i2 >> 1);
    double d2 = Math.atan2(f2, f1);
    double d1 = Math.pow(f1, 2.0D);
    d1 = Math.sqrt(Math.pow(f2, 2.0D) + d1);
    d2 -= L() * 3.141592653589793D / 180.0D;
    localPointF.x = ((float)(Math.cos(d2) * d1 + (i1 >> 1)));
    d2 = Math.sin(d2);
    localPointF.y = ((float)((i2 >> 1) + d1 * d2));
    return localPointF;
  }
  
  public ax b(MarkerOptions paramMarkerOptions)
    throws RemoteException
  {
    paramMarkerOptions = new ax(paramMarkerOptions, this.j);
    this.j.a(paramMarkerOptions);
    invalidate();
    return paramMarkerOptions;
  }
  
  public ay b()
  {
    return this.a;
  }
  
  public void b(double paramDouble1, double paramDouble2, ad paramad)
  {
    f();
    Object localObject = new u((int)q.a(paramDouble1), (int)q.a(paramDouble2));
    localObject = this.K.b((u)localObject, this.K.l, this.K.n, this.K.k);
    if (paramad == null) {
      return;
    }
    paramad.a = ((int)((PointF)localObject).x);
    paramad.b = ((int)((PointF)localObject).y);
  }
  
  public void b(float paramFloat)
    throws RemoteException
  {
    if (this.z == null) {
      return;
    }
    this.z.a(paramFloat);
  }
  
  public void b(int paramInt)
  {
    if (this.A == null) {}
    do
    {
      return;
      this.A.a(paramInt);
      this.A.invalidate();
    } while (this.B.getVisibility() != 0);
    this.B.invalidate();
  }
  
  public void b(int paramInt1, int paramInt2, r paramr)
  {
    if (paramr == null) {
      return;
    }
    paramr.a = q.a(paramInt1);
    paramr.b = q.a(paramInt2);
  }
  
  public void b(l paraml)
    throws RemoteException
  {
    a(paraml, null);
  }
  
  public void b(boolean paramBoolean)
    throws RemoteException
  {
    i(paramBoolean);
    postInvalidate();
  }
  
  public boolean b(float paramFloat, PointF paramPointF)
  {
    this.av = false;
    try
    {
      boolean bool = this.h.f();
      if (!bool) {
        break label27;
      }
    }
    catch (RemoteException paramPointF)
    {
      for (;;)
      {
        cj.a(paramPointF, "AMapDelegateImpGLSurfaceView", "endScale");
      }
    }
    k.a().b();
    return true;
    label27:
    return false;
  }
  
  public boolean b(Matrix paramMatrix)
  {
    try
    {
      boolean bool = this.h.f();
      if (!bool) {
        break label28;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        label28:
        cj.a(localRemoteException, "AMapDelegateImpGLSurfaceView", "onScale");
      }
    }
    this.at.set(paramMatrix);
    postInvalidate();
    return true;
    return false;
  }
  
  public boolean b(aa paramaa)
  {
    if (this.Q == null) {}
    while (this.N == null) {
      return false;
    }
    return this.Q.d().equals(paramaa.d());
  }
  
  public boolean b(String paramString)
  {
    try
    {
      paramString = this.j.a(paramString);
      if (paramString == null) {
        return false;
      }
    }
    catch (RemoteException paramString)
    {
      for (;;)
      {
        cj.a(paramString, "AMapDelegateImpGLSurfaceView", "removeMarker");
        paramString = null;
      }
    }
    return this.j.b(paramString);
  }
  
  public int c()
  {
    if (this.a == null) {}
    while (this.a.c == null) {
      return 0;
    }
    return this.a.c.c();
  }
  
  protected PointF c(PointF paramPointF)
  {
    PointF localPointF = new PointF();
    int i1 = getWidth();
    int i2 = getHeight();
    float f1 = paramPointF.x - (i1 >> 1);
    float f2 = paramPointF.y - (i2 >> 1);
    double d2 = Math.atan2(f2, f1);
    double d1 = Math.pow(f1, 2.0D);
    d1 = Math.sqrt(Math.pow(f2, 2.0D) + d1);
    d2 += L() * 3.141592653589793D / 180.0D;
    localPointF.x = ((float)(Math.cos(d2) * d1 + (i1 >> 1)));
    d2 = Math.sin(d2);
    localPointF.y = ((float)((i2 >> 1) + d1 * d2));
    return localPointF;
  }
  
  public void c(float paramFloat)
  {
    this.au = paramFloat;
  }
  
  public void c(int paramInt)
  {
    if (this.f == null) {
      return;
    }
    this.f.a(paramInt);
    this.f.invalidate();
  }
  
  public void c(String paramString)
    throws RemoteException
  {
    if (this.a == null) {}
    while (this.a.e == null) {
      return;
    }
    if (!E())
    {
      this.a.e.a(paramString);
      return;
    }
  }
  
  public void c(boolean paramBoolean)
    throws RemoteException
  {
    if (this.C == null)
    {
      this.u.a(false);
      if (!paramBoolean) {
        break label119;
      }
    }
    for (;;)
    {
      this.y = paramBoolean;
      return;
      if (!paramBoolean) {
        if (this.z != null) {
          break label104;
        }
      }
      for (;;)
      {
        this.v = null;
        this.C.deactivate();
        this.u.a(false);
        break;
        this.C.activate(this.w);
        this.u.a(true);
        if (this.z != null) {
          break;
        }
        this.z = new bb(this);
        break;
        label104:
        this.z.a();
        this.z = null;
      }
      label119:
      this.h.d(paramBoolean);
    }
  }
  
  public void computeScroll()
  {
    if (!this.aq.computeScrollOffset())
    {
      super.computeScroll();
      return;
    }
    int i1 = this.aq.getCurrX();
    int i2 = this.ar;
    int i3 = this.aq.getCurrY();
    int i4 = this.as;
    this.ar = this.aq.getCurrX();
    this.as = this.aq.getCurrY();
    u localu = this.a.b.a(i1 - i2 + this.a.i.n.x, i3 - i4 + this.a.i.n.y);
    if (!this.aq.isFinished())
    {
      this.a.c.b(localu);
      return;
    }
    k.a().b();
    if (this.H == null) {}
    for (;;)
    {
      this.a.c.a(false, false);
      return;
      a(true, Z());
    }
  }
  
  public int d()
  {
    if (this.a == null) {}
    while (this.a.c == null) {
      return 0;
    }
    return this.a.c.d();
  }
  
  public void d(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      this.f.setVisibility(8);
      return;
    }
    this.f.setVisibility(0);
  }
  
  public boolean d(float paramFloat)
  {
    try
    {
      boolean bool = this.h.f();
      if (!bool) {
        break label21;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        label21:
        cj.a(localRemoteException, "AMapDelegateImpGLSurfaceView", "onScale");
      }
    }
    c(paramFloat);
    return false;
    return false;
  }
  
  public View e()
    throws RemoteException
  {
    return this.g;
  }
  
  public void e(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      this.u.setVisibility(8);
      return;
    }
    this.u.setVisibility(0);
  }
  
  public float f()
  {
    if (this.a == null) {}
    while (this.a.c == null) {
      return 0.0F;
    }
    try
    {
      float f1 = this.a.c.e();
      return f1;
    }
    catch (Exception localException)
    {
      cj.a(localException, "AMapDelegateImpGLSurfaceView", "getZoomLevel");
    }
    return 0.0F;
  }
  
  public void f(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      this.D.setVisibility(8);
      return;
    }
    this.D.setVisibility(0);
  }
  
  public CameraPosition g()
    throws RemoteException
  {
    LatLng localLatLng = ab();
    if (localLatLng != null)
    {
      float f1 = f();
      return CameraPosition.builder().target(localLatLng).zoom(f1).build();
    }
    return null;
  }
  
  public void g(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      this.B.a("");
      this.B.a(0);
      this.B.setVisibility(8);
      return;
    }
    this.B.setVisibility(0);
    N();
  }
  
  public float h()
  {
    if (this.a == null) {}
    while (this.a.c == null) {
      return p.c;
    }
    return this.a.c.a();
  }
  
  public void h(boolean paramBoolean)
  {
    if (E() != paramBoolean)
    {
      if (paramBoolean)
      {
        if (this.a == null) {
          break label238;
        }
        if (b().e.b(b().e.h) != null) {
          break label239;
        }
        am localam = new am(this.K);
        localam.q = new bs(this.a, this.m, localam);
        localam.j = new by()
        {
          public String a(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            return aw.a().e() + "/appmaptile?z=" + paramAnonymousInt3 + "&x=" + paramAnonymousInt1 + "&y=" + paramAnonymousInt2 + "&lang=zh_cn&size=1&scale=1&style=6";
          }
        };
        localam.b = b().e.h;
        localam.e = true;
        localam.a(true);
        localam.f = true;
        localam.c = p.c;
        localam.d = p.d;
        b().e.a(localam, getContext());
        b().e.a(b().e.h, true);
        b().c.a(false, false);
      }
    }
    else {
      return;
    }
    b().e.a(b().e.h, false);
    b().e.a(b().e.g, true);
    b().c.a(false, false);
    return;
    label238:
    return;
    label239:
    b().e.a(b().e.h, true);
    b().c.a(false, false);
  }
  
  public float i()
  {
    if (this.a == null) {}
    while (this.a.c == null) {
      return p.d;
    }
    return this.a.c.b();
  }
  
  public void i(boolean paramBoolean)
  {
    String str;
    if (paramBoolean != F())
    {
      if (this.a != null)
      {
        str = b().e.i;
        if (!paramBoolean) {
          break label176;
        }
        if (b().e.b(str) != null) {
          break label202;
        }
        am localam = new am(this.K);
        localam.q = new bs(this.a, this.m, localam);
        localam.g = true;
        localam.i = 120000L;
        localam.j = new by()
        {
          public String a(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            return aw.a().c() + "/trafficengine/mapabc/traffictile?v=w2.61&zoom=" + (17 - paramAnonymousInt3) + "&x=" + paramAnonymousInt1 + "&y=" + paramAnonymousInt2;
          }
        };
        localam.b = str;
        localam.e = false;
        localam.a(true);
        localam.f = false;
        localam.c = 18;
        localam.d = 9;
        b().e.a(localam, getContext());
        b().e.a(str, true);
        b().c.a(false, false);
      }
    }
    else {
      return;
    }
    return;
    label176:
    b().e.a(str, false);
    b().c.a(false, false);
    return;
    label202:
    b().e.a(str, true);
    b().c.a(false, false);
  }
  
  public void j()
    throws RemoteException
  {
    if (this.b != null)
    {
      if (this.I.a()) {
        this.b.a(true);
      }
    }
    else {
      return;
    }
    this.I.a(true);
    k.a().b();
    if (this.J == null) {}
    for (;;)
    {
      this.J = null;
      break;
      this.J.onCancel();
    }
  }
  
  public void k()
    throws RemoteException
  {
    try
    {
      t();
      if (this.a != null)
      {
        this.a.g.a();
        this.j.c();
        this.i.b();
        if (this.z == null) {}
        for (;;)
        {
          invalidate();
          return;
          this.z.a();
        }
      }
      return;
    }
    catch (Exception localException)
    {
      cj.a(localException, "AMapDelegateImpGLSurfaceView", "clear");
      Log.d("amapApi", "AMapDelegateImpGLSurfaceView clear erro" + localException.getMessage());
      return;
    }
    catch (Throwable localThrowable)
    {
      cj.a(localThrowable, "AMapDelegateImpGLSurfaceView", "clear");
      return;
    }
  }
  
  public int l()
    throws RemoteException
  {
    return this.t;
  }
  
  public boolean m()
    throws RemoteException
  {
    return F();
  }
  
  public boolean n()
    throws RemoteException
  {
    return this.y;
  }
  
  public bb o()
  {
    return this.z;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
  }
  
  public boolean onDoubleTap(MotionEvent paramMotionEvent)
  {
    try
    {
      boolean bool = this.h.f();
      if (!bool) {
        break label55;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        label55:
        cj.a(localRemoteException, "AMapDelegateImpGLSurfaceView", "onDoubleTap");
        continue;
        this.b.a((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
      }
    }
    if (!this.s)
    {
      if (this.aG > 1) {
        break label92;
      }
      this.aH = true;
      this.f.a(this.a.c.e() + 1.0F);
      return true;
      return true;
    }
    label92:
    return true;
  }
  
  public boolean onDoubleTapEvent(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  public boolean onDown(MotionEvent paramMotionEvent)
  {
    this.W = false;
    if (this.aH) {}
    while (this.I.a())
    {
      this.aH = false;
      this.aG = 0;
      if (this.al == null) {
        break;
      }
      this.al.set((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
      return true;
    }
    this.I.a(true);
    if (this.J == null) {}
    for (;;)
    {
      this.J = null;
      break;
      this.J.onCancel();
    }
    this.al = new Point((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
    return true;
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    Object localObject = I();
    paramCanvas.drawColor(H());
    int i1 = getWidth();
    int i2 = getHeight();
    float f1;
    float f2;
    if (i1 <= i2)
    {
      i1 = i2;
      f1 = getLeft();
      f2 = getTop();
      i2 = 0;
      label50:
      if (i2 < i1) {
        break label126;
      }
      if (this.U) {
        break label177;
      }
      label64:
      this.a.c.a(getWidth(), getHeight());
      this.a.e.a(paramCanvas, this.at, this.aw, this.ax);
      if (!this.I.a()) {
        break label233;
      }
    }
    for (;;)
    {
      if (!this.ae) {
        break label246;
      }
      return;
      break;
      label126:
      paramCanvas.drawLine(f1, i2, f1 + getWidth(), i2, (Paint)localObject);
      paramCanvas.drawLine(i2, f2, i2, f2 + getHeight(), (Paint)localObject);
      i2 += 256;
      break label50;
      label177:
      setDrawingCacheEnabled(true);
      buildDrawingCache();
      localObject = getDrawingCache();
      Message localMessage = this.k.obtainMessage();
      localMessage.what = 16;
      localMessage.obj = localObject;
      this.k.sendMessage(localMessage);
      this.U = false;
      break label64;
      label233:
      this.k.sendEmptyMessage(13);
    }
    label246:
    this.k.sendEmptyMessage(11);
    this.ae = true;
  }
  
  public boolean onFling(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    if (this.an.k) {}
    for (;;)
    {
      return true;
      if (paramMotionEvent1.getEventTime() - this.an.o >= 30L) {}
      for (int i1 = 1; i1 != 0; i1 = 0)
      {
        invalidate();
        this.W = false;
        try
        {
          boolean bool = this.h.e();
          if (!bool) {
            break label125;
          }
        }
        catch (RemoteException paramMotionEvent1)
        {
          for (;;)
          {
            cj.a(paramMotionEvent1, "AMapDelegateImpGLSurfaceView", "onFling");
          }
        }
        this.J = null;
        this.aq.fling(this.ar, this.as, (int)-paramFloat1 * 3 / 5, (int)-paramFloat2 * 3 / 5, -this.ay, this.ay, -this.az, this.az);
        return true;
      }
    }
    label125:
    return true;
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.a != null)
    {
      if (!this.n) {
        break label33;
      }
      if (!this.a.e.a(paramInt, paramKeyEvent)) {
        break label35;
      }
    }
    label33:
    label35:
    while (this.b.onKey(this, paramInt, paramKeyEvent))
    {
      return true;
      return true;
      return false;
    }
    return false;
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    if (this.a != null)
    {
      if (!this.n) {
        break label33;
      }
      if (!this.a.e.b(paramInt, paramKeyEvent)) {
        break label35;
      }
    }
    label33:
    label35:
    while (this.b.onKey(this, paramInt, paramKeyEvent))
    {
      return true;
      return true;
      return false;
    }
    return false;
  }
  
  public void onLongPress(MotionEvent paramMotionEvent)
  {
    try
    {
      this.W = false;
      if (this.ab == null) {}
      for (;;)
      {
        this.q = this.j.a(paramMotionEvent);
        if (this.q == null) {
          break;
        }
        this.p = new Marker(this.q);
        if (this.Z != null) {
          break label116;
        }
        return;
        r localr = new r();
        a((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY(), localr);
        this.ab.onMapLongClick(new LatLng(localr.b, localr.a));
        this.L = true;
      }
      return;
    }
    catch (Throwable paramMotionEvent)
    {
      paramMotionEvent.printStackTrace();
      return;
    }
    label116:
    if ((this.q != null) && (this.q.h()))
    {
      paramMotionEvent = a(this.q.c());
      this.q.a(paramMotionEvent);
      this.j.c(this.q);
      this.Z.onMarkerDragStart(this.p);
      this.V = true;
    }
  }
  
  protected final void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    super.onRestoreInstanceState(paramParcelable);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    return super.onSaveInstanceState();
  }
  
  public boolean onScroll(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2)
  {
    if (this.an.k) {}
    for (;;)
    {
      return true;
      if (paramMotionEvent2.getEventTime() - this.an.o >= 30L) {}
      for (int i1 = 1; i1 != 0; i1 = 0)
      {
        try
        {
          boolean bool = this.h.e();
          if (!bool) {
            break label98;
          }
        }
        catch (RemoteException paramMotionEvent1)
        {
          for (;;)
          {
            cj.a(paramMotionEvent1, "AMapDelegateImpGLSurfaceView", "onScroll");
          }
          this.W = false;
        }
        if (this.aG > 1) {
          break label119;
        }
        this.W = true;
        a((int)paramMotionEvent2.getX(), (int)paramMotionEvent2.getY());
        postInvalidate();
        M();
        return true;
      }
    }
    label98:
    this.W = false;
    return true;
    label119:
    return true;
  }
  
  public void onShowPress(MotionEvent paramMotionEvent) {}
  
  public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent)
  {
    Object localObject;
    if (this.b != null)
    {
      this.a.e.b(paramMotionEvent);
      localObject = this.ao.iterator();
    }
    for (;;)
    {
      if (!((Iterator)localObject).hasNext())
      {
        this.W = false;
        if (this.L) {
          break label98;
        }
      }
      try
      {
        if (this.N == null) {}
        for (;;)
        {
          if (!this.j.b(paramMotionEvent))
          {
            localObject = this.ad;
            if (localObject != null) {
              break label373;
            }
            return true;
            return false;
            ((GestureDetector.OnGestureListener)((Iterator)localObject).next()).onSingleTapUp(paramMotionEvent);
            break;
            label98:
            this.L = false;
            return true;
            localObject = new Rect(this.N.getLeft(), this.N.getTop(), this.N.getRight(), this.N.getBottom());
            if ((this.j.a((Rect)localObject, (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())) && (this.O != null))
            {
              paramMotionEvent = this.j.e();
              if (!paramMotionEvent.s()) {
                break label422;
              }
              paramMotionEvent = new Marker(paramMotionEvent);
              this.O.onInfoWindowClick(paramMotionEvent);
              return true;
            }
          }
        }
        paramMotionEvent = this.j.e();
        if (paramMotionEvent == null) {
          return true;
        }
        if (!paramMotionEvent.s()) {
          break label424;
        }
        localObject = new Marker(paramMotionEvent);
        if (this.R == null) {}
        for (;;)
        {
          a(paramMotionEvent);
          this.j.c(paramMotionEvent);
          return true;
          if (this.R.onMarkerClick((Marker)localObject)) {}
          int i1;
          do
          {
            this.j.c(paramMotionEvent);
            return true;
            i1 = this.j.b();
          } while (i1 <= 0);
          try
          {
            if ((this.j.e() != null) && (!paramMotionEvent.q()))
            {
              localObject = paramMotionEvent.c();
              if (localObject != null)
              {
                this.b.a(cj.a((LatLng)localObject));
                k.a().b();
              }
            }
          }
          catch (Throwable localThrowable)
          {
            cj.a(localThrowable, "AMapDelegateImpGLSurfaceView", "onSingleTapConfirmed");
          }
        }
        localr = new r();
      }
      catch (Throwable paramMotionEvent)
      {
        cj.a(paramMotionEvent, "AMapDelegateImpGLSurfaceView", "onSingleTapConfirmed");
        return true;
      }
    }
    label373:
    r localr;
    a((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY(), localr);
    this.ad.onMapClick(new LatLng(localr.b, localr.a));
    return true;
    label422:
    return true;
    label424:
    return true;
  }
  
  public boolean onSingleTapUp(MotionEvent paramMotionEvent)
  {
    return false;
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    Point localPoint = new Point(paramInt1 / 2, paramInt2 / 2);
    this.a.i.a(localPoint);
    this.a.c.a(paramInt1, paramInt2);
    if ((this.b.a() != 0.0F) && (this.b.b() != 0.0F))
    {
      this.b.a(this.b.a(), this.b.b());
      this.b.a(0.0F);
      this.b.b(0.0F);
    }
    u();
    if (this.aI == null) {
      return;
    }
    this.aI.a(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (p.q)
    {
      if (this.a == null) {
        break label55;
      }
      if (!this.n) {
        break label57;
      }
      if (this.aa != null) {
        break label59;
      }
    }
    while (!this.a.e.a(paramMotionEvent))
    {
      b(paramMotionEvent);
      return super.onTouchEvent(paramMotionEvent);
      return true;
      label55:
      return true;
      label57:
      return false;
      label59:
      this.ak.removeMessages(1);
      Message localMessage = this.ak.obtainMessage();
      localMessage.what = 1;
      localMessage.obj = MotionEvent.obtain(paramMotionEvent);
      localMessage.sendToTarget();
    }
    return true;
  }
  
  public void onWindowFocusChanged(boolean paramBoolean)
  {
    super.onWindowFocusChanged(paramBoolean);
  }
  
  public Location p()
    throws RemoteException
  {
    if (this.C == null) {
      return null;
    }
    return this.w.a;
  }
  
  public ak q()
    throws RemoteException
  {
    return this.h;
  }
  
  public ag r()
  {
    return this.T;
  }
  
  public bf s()
  {
    if (this.a != null) {
      return this.a.b;
    }
    return null;
  }
  
  public void setClickable(boolean paramBoolean)
  {
    this.n = paramBoolean;
    super.setClickable(paramBoolean);
  }
  
  public void t()
  {
    if (this.N == null)
    {
      this.Q = null;
      return;
    }
    this.N.clearFocus();
    this.N.destroyDrawingCache();
    this.g.removeView(this.N);
    Drawable localDrawable = this.N.getBackground();
    if (localDrawable == null) {}
    for (;;)
    {
      this.N = null;
      break;
      localDrawable.setCallback(null);
    }
  }
  
  public void u()
  {
    if (this.N == null) {}
    while (this.Q == null) {
      return;
    }
    at.a locala = (at.a)this.N.getLayoutParams();
    if (locala == null) {}
    for (;;)
    {
      this.g.a();
      return;
      locala.b = this.Q.c();
    }
  }
  
  public void v()
  {
    for (;;)
    {
      try
      {
        if (this.ag == null)
        {
          if (this.ai == null)
          {
            if (this.ak != null) {
              break label205;
            }
            if (this.k != null) {
              break label216;
            }
            if (this.ah != null) {
              break label227;
            }
            m.a().b(this);
            bj.a().a(this);
            k.a().b(this);
            this.f.a();
            this.B.a();
            this.A.a();
            this.u.a();
            this.D.a();
            this.a.g.b();
            this.j.f();
            if (this.S != null) {
              break label242;
            }
            this.g.removeAllViews();
            t();
            if (this.i != null) {
              break label253;
            }
            if (this.a != null) {
              break label263;
            }
            this.C = null;
            this.ad = null;
            p.h = null;
            p.g = null;
            db.b();
          }
        }
        else
        {
          this.ag.cancel();
          this.ag = null;
          continue;
        }
        this.ai.cancel();
      }
      catch (Exception localException)
      {
        cj.a(localException, "AMapDelegateImpGLSurfaceView", "destroy");
        return;
      }
      this.ai = null;
      continue;
      label205:
      this.ak.removeCallbacksAndMessages(null);
      continue;
      label216:
      this.k.removeCallbacksAndMessages(null);
      continue;
      label227:
      this.ah.interrupt();
      this.ah = null;
      continue;
      label242:
      this.S.setCallback(null);
      continue;
      label253:
      this.i.f();
      continue;
      label263:
      this.a.d.b();
      W();
    }
  }
  
  public float w()
  {
    int i1 = getWidth();
    r localr1 = new r();
    r localr2 = new r();
    a(0, 0, localr1);
    a(i1, 0, localr2);
    return (float)(cj.a(new LatLng(localr1.b, localr1.a), new LatLng(localr2.b, localr2.a)) / i1);
  }
  
  public LatLngBounds x()
  {
    return null;
  }
  
  public void y()
  {
    if (this.a == null) {}
    while (this.i == null)
    {
      return;
      this.a.d.c();
    }
    this.i.e();
  }
  
  public void z()
  {
    if (this.a == null) {}
    while (this.i == null)
    {
      return;
      this.a.d.d();
    }
    this.i.d();
  }
  
  private static abstract class a
  {
    public abstract void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  }
  
  private class b
    implements Runnable
  {
    private Context b;
    private AMap.OnCacheRemoveListener c;
    
    public b(Context paramContext, AMap.OnCacheRemoveListener paramOnCacheRemoveListener)
    {
      this.b = paramContext;
      this.c = paramOnCacheRemoveListener;
    }
    
    public void run()
    {
      try
      {
        cj.a(new File(cj.b(this.b)));
        try
        {
          if (this.c == null) {
            return;
          }
          this.c.onRemoveCacheFinish(true);
          return;
        }
        catch (Throwable localThrowable1)
        {
          localThrowable1.printStackTrace();
          return;
        }
        AMap.OnCacheRemoveListener localOnCacheRemoveListener;
        return;
      }
      catch (Throwable localThrowable2)
      {
        db.b(localThrowable2, "AMapDelegateImpGLSurfaceView", "RemoveCacheRunnable");
        try
        {
          if (this.c != null)
          {
            this.c.onRemoveCacheFinish(false);
            return;
          }
        }
        catch (Throwable localThrowable3)
        {
          localThrowable3.printStackTrace();
          return;
        }
      }
      finally
      {
        for (;;)
        {
          try
          {
            localOnCacheRemoveListener = this.c;
            if (localOnCacheRemoveListener != null) {
              continue;
            }
          }
          catch (Throwable localThrowable4)
          {
            localThrowable4.printStackTrace();
            continue;
          }
          throw ((Throwable)localObject);
          this.c.onRemoveCacheFinish(true);
        }
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/b.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */