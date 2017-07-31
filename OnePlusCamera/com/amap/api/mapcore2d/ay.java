package com.amap.api.mapcore2d;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.RemoteException;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.WindowManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

class ay
{
  static double a = 0.6499999761581421D;
  public e b;
  public d c;
  public b d;
  public a e;
  public c f;
  public t g;
  public b h;
  public au i = null;
  private am j;
  
  public ay(Context paramContext, b paramb, int paramInt)
  {
    this.h = paramb;
    this.c = new d(paramb, null);
    this.i = new au(this.c);
    this.i.a = paramInt;
    this.i.b = paramInt;
    this.i.a();
    a(paramContext);
    this.f = new c(this, paramContext, null);
    this.e = new a(paramContext, null);
    this.b = new e();
    this.d = new b();
    this.g = new t();
    this.c.a(false, false);
  }
  
  public void a()
  {
    this.e.a();
    this.b = null;
    this.c = null;
    this.d = null;
    this.e = null;
    this.f = null;
  }
  
  public void a(Context paramContext)
  {
    int m = 0;
    new DisplayMetrics();
    DisplayMetrics localDisplayMetrics = paramContext.getApplicationContext().getResources().getDisplayMetrics();
    paramContext = null;
    long l;
    try
    {
      Field localField = localDisplayMetrics.getClass().getField("densityDpi");
      paramContext = localField;
    }
    catch (SecurityException localSecurityException)
    {
      for (;;)
      {
        cj.a(localSecurityException, "Mediator", "initialize");
      }
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      for (;;)
      {
        cj.a(localNoSuchFieldException, "Mediator", "initialize");
      }
      k = 160;
      l = localDisplayMetrics.widthPixels * localDisplayMetrics.heightPixels;
      try
      {
        n = paramContext.getInt(localDisplayMetrics);
        k = n;
      }
      catch (IllegalArgumentException paramContext)
      {
        for (;;)
        {
          cj.a(paramContext, "Mediator", "initialize");
        }
      }
      catch (IllegalAccessException paramContext)
      {
        for (;;)
        {
          cj.a(paramContext, "Mediator", "initialize");
        }
        p.m = 1;
        return;
      }
      if (k <= 120) {
        break label198;
      }
      if (k <= 160) {
        break label203;
      }
      if (k <= 240) {
        break label208;
      }
      if (l > 153600L) {
        break label213;
      }
      k = 1;
      if (k != 0) {
        break label218;
      }
      p.m = 2;
      return;
      label198:
      label203:
      p.m = 3;
      return;
    }
    if (paramContext == null)
    {
      l = localDisplayMetrics.widthPixels * localDisplayMetrics.heightPixels;
      if (l > 153600L) {
        break label245;
      }
    }
    label208:
    label213:
    label218:
    label245:
    for (int k = 1; k == 0; k = 0)
    {
      p.m = 2;
      return;
      for (;;)
      {
        int n;
        p.m = 2;
        return;
        k = 0;
      }
      k = m;
      if (l >= 153600L) {
        k = 1;
      }
      if (k == 0)
      {
        p.m = 1;
        return;
      }
      p.m = 3;
      return;
    }
    if (l >= 153600L) {}
    for (k = 1; k == 0; k = 0)
    {
      p.m = 1;
      return;
    }
    p.m = 3;
  }
  
  public void a(boolean paramBoolean)
  {
    this.e.b(paramBoolean);
  }
  
  public class a
  {
    public bk<am> a = null;
    public boolean b = false;
    public boolean c = false;
    String d = "zh_cn";
    int e = 0;
    int f = 0;
    String g;
    String h = "SatelliteMap3";
    String i = "GridTmc3";
    String j = "SateliteTmc3";
    private boolean l = false;
    private boolean m = true;
    private Context n;
    private boolean o = false;
    
    private a(Context paramContext)
    {
      if (paramContext != null)
      {
        this.n = paramContext;
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        ((WindowManager)paramContext.getSystemService("window")).getDefaultDisplay().getMetrics(localDisplayMetrics);
        int i1 = localDisplayMetrics.widthPixels / ay.this.i.a + c();
        int i2 = localDisplayMetrics.heightPixels / ay.this.i.a + c();
        this.e = (i1 + i1 * i2 + i2);
        this.f = (this.e / 8 + 1);
        if (this.f == 0) {
          break label199;
        }
        if (this.f > 5) {
          break label207;
        }
      }
      for (;;)
      {
        a(paramContext, "zh_cn");
        return;
        return;
        label199:
        this.f = 1;
        continue;
        label207:
        this.f = 5;
      }
    }
    
    private void a(Context paramContext, final String paramString)
    {
      if (this.a != null)
      {
        if (p.g != null) {
          break label138;
        }
        label13:
        if (paramString.equals("zh_cn")) {
          break label159;
        }
        if (paramString.equals("en")) {
          break label168;
        }
      }
      for (;;)
      {
        paramString = new am(ay.this.i);
        paramString.j = new by()
        {
          public String a(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            String str2;
            if (p.h == null)
            {
              paramString.h = true;
              str2 = aw.a().b();
              if (p.m == 2) {
                break label133;
              }
            }
            label133:
            for (String str1 = "webrd";; str1 = "wprd")
            {
              return String.format(str2, new Object[] { Integer.valueOf(paramAnonymousInt3), Integer.valueOf(paramAnonymousInt1), Integer.valueOf(paramAnonymousInt2), str1, ay.a.this.d });
              if (p.h.equals("")) {
                break;
              }
              paramString.h = false;
              return String.format(Locale.US, p.h, new Object[] { Integer.valueOf(paramAnonymousInt3), Integer.valueOf(paramAnonymousInt1), Integer.valueOf(paramAnonymousInt2) });
            }
          }
        };
        paramString.b = this.g;
        paramString.e = true;
        paramString.f = true;
        paramString.c = p.c;
        paramString.d = p.d;
        paramString.q = new bs(ay.this, this.n, paramString);
        paramString.a(true);
        a(paramString, paramContext);
        return;
        this.a = new bk();
        break;
        label138:
        if (p.g.equals("")) {
          break label13;
        }
        this.g = p.g;
        continue;
        label159:
        this.g = "GridMapV3";
        continue;
        label168:
        this.g = "GridMapEnV3";
      }
    }
    
    private void a(Canvas paramCanvas)
    {
      int i2 = this.a.size();
      int i1 = 0;
      for (;;)
      {
        if (i1 >= i2) {
          return;
        }
        am localam = (am)this.a.get(i1);
        if ((localam != null) && (localam.a())) {
          localam.a(paramCanvas);
        }
        i1 += 1;
      }
    }
    
    private void b(Canvas paramCanvas)
    {
      if (!this.m) {
        return;
      }
      ay.this.g.a(paramCanvas);
    }
    
    private int c()
    {
      return 3;
    }
    
    private void c(Canvas paramCanvas)
    {
      ay.this.h.j.a(paramCanvas);
    }
    
    private void c(String paramString)
    {
      int i1;
      if (paramString.equals("") != true)
      {
        int i2 = this.a.size();
        i1 = 0;
        if (i1 < i2) {}
      }
      else
      {
        return;
      }
      am localam = (am)this.a.get(i1);
      if ((localam == null) || (localam.b.equals(paramString))) {}
      for (;;)
      {
        i1 += 1;
        break;
        if ((localam.e == true) && (localam.a() == true)) {
          localam.a(false);
        }
      }
    }
    
    private void d()
    {
      int i2 = this.a.size();
      int i1 = 0;
      for (;;)
      {
        if (i1 >= i2) {
          return;
        }
        am localam = (am)this.a.get(i1);
        if (localam != null) {
          localam.l = i1;
        }
        i1 += 1;
      }
    }
    
    private boolean d(String paramString)
    {
      int i2;
      int i1;
      if (this.a != null)
      {
        i2 = this.a.size();
        i1 = 0;
      }
      for (;;)
      {
        if (i1 >= i2)
        {
          return false;
          return false;
        }
        am localam = (am)this.a.get(i1);
        if ((localam != null) && (localam.b.equals(paramString) == true)) {
          break;
        }
        i1 += 1;
      }
      return true;
    }
    
    public void a()
    {
      Iterator localIterator;
      if (ay.this.e.a != null) {
        localIterator = ay.this.e.a.iterator();
      }
      for (;;)
      {
        if (!localIterator.hasNext())
        {
          ay.this.e.a.clear();
          ay.this.e.a = null;
          return;
          return;
        }
        am localam = (am)localIterator.next();
        if (localam != null) {
          localam.b();
        }
      }
    }
    
    public void a(Canvas paramCanvas, Matrix paramMatrix, float paramFloat1, float paramFloat2)
    {
      if (!this.l)
      {
        a(paramCanvas);
        ay.this.h.i.a(paramCanvas);
        b(paramCanvas);
      }
      label75:
      label188:
      label194:
      for (;;)
      {
        c(paramCanvas);
        return;
        paramCanvas.save();
        paramCanvas.translate(paramFloat1, paramFloat2);
        paramCanvas.concat(paramMatrix);
        a(paramCanvas);
        if (!ay.this.h.i.a())
        {
          ay.this.h.i.a(paramCanvas);
          paramCanvas.restore();
          if (!ay.this.h.i.a()) {
            break label188;
          }
        }
        for (;;)
        {
          if ((this.b) || (this.c)) {
            break label194;
          }
          a(false);
          ay.d.a(ay.this.c).b(new Matrix());
          ay.d.a(ay.this.c).d(1.0F);
          ay.d.a(ay.this.c).K();
          break;
          b(paramCanvas);
          break label75;
          b(paramCanvas);
        }
      }
    }
    
    public void a(String paramString)
    {
      if (paramString == null) {}
      while (paramString.equals("")) {
        return;
      }
      if (!this.d.equals(paramString))
      {
        if (!paramString.equals("zh_cn")) {
          break label101;
        }
        if (p.g != null) {
          break label111;
        }
        label40:
        if (paramString.equals("zh_cn")) {
          break label132;
        }
        if (paramString.equals("en")) {
          break label141;
        }
        label58:
        ay.a(ay.this, b(this.g));
        if (ay.a(ay.this) == null) {
          break label150;
        }
      }
      for (;;)
      {
        a(this.g, true);
        this.d = paramString;
        return;
        return;
        label101:
        if (paramString.equals("en")) {
          break;
        }
        return;
        label111:
        if (p.g.equals("")) {
          break label40;
        }
        this.g = p.g;
        break label58;
        label132:
        this.g = "GridMapV3";
        break label58;
        label141:
        this.g = "GridMapEnV3";
        break label58;
        label150:
        ay.a(ay.this, new am(ay.this.i));
        ay.a(ay.this).q = new bs(ay.this, this.n, ay.a(ay.this));
        ay.a(ay.this).j = new by()
        {
          public String a(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            String str2;
            if (p.h == null)
            {
              ay.a(ay.this).h = true;
              str2 = aw.a().b();
              if (p.m == 2) {
                break label145;
              }
            }
            label145:
            for (String str1 = "webrd";; str1 = "wprd")
            {
              return String.format(str2, new Object[] { Integer.valueOf(paramAnonymousInt3), Integer.valueOf(paramAnonymousInt1), Integer.valueOf(paramAnonymousInt2), str1, ay.a.this.d });
              if (p.h.equals("")) {
                break;
              }
              ay.a(ay.this).h = false;
              return String.format(Locale.US, p.h, new Object[] { Integer.valueOf(paramAnonymousInt3), Integer.valueOf(paramAnonymousInt1), Integer.valueOf(paramAnonymousInt2) });
            }
          }
        };
        ay.a(ay.this).b = this.g;
        ay.a(ay.this).e = true;
        ay.a(ay.this).a(true);
        ay.a(ay.this).f = true;
        ay.a(ay.this).c = p.c;
        ay.a(ay.this).d = p.d;
        a(ay.a(ay.this), this.n);
      }
    }
    
    public void a(boolean paramBoolean)
    {
      this.l = paramBoolean;
    }
    
    public boolean a(int paramInt, KeyEvent paramKeyEvent)
    {
      return false;
    }
    
    public boolean a(MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    boolean a(am paramam, Context paramContext)
    {
      int i1;
      boolean bool;
      if (paramam != null)
      {
        if (paramam.b.equals("") == true) {
          break label147;
        }
        if (d(paramam.b) == true) {
          break label149;
        }
        paramam.p = new bk();
        paramam.n = new az(this.e, this.f, paramam.g, paramam.i, paramam);
        paramam.o = new s(paramContext, ay.d.a(ay.this.c).d, paramam);
        paramam.o.a(paramam.n);
        i1 = this.a.size();
        if (paramam.e) {
          break label151;
        }
        bool = this.a.add(paramam);
      }
      for (;;)
      {
        d();
        if (paramam.a() == true) {
          break label215;
        }
        return bool;
        return false;
        label147:
        return false;
        label149:
        return false;
        label151:
        if (i1 == 0) {
          break;
        }
        i1 -= 1;
        for (;;)
        {
          if (i1 < 0)
          {
            bool = false;
            break;
          }
          paramContext = (am)this.a.get(i1);
          if ((paramContext != null) && (paramContext.e == true)) {
            break label200;
          }
          i1 -= 1;
        }
        label200:
        this.a.add(i1, paramam);
        bool = false;
      }
      label215:
      a(paramam.b, true);
      return bool;
    }
    
    boolean a(String paramString, boolean paramBoolean)
    {
      int i1;
      if (paramString.equals("") != true)
      {
        int i2 = this.a.size();
        i1 = 0;
        if (i1 >= i2) {
          return false;
        }
      }
      else
      {
        return false;
      }
      am localam = (am)this.a.get(i1);
      if ((localam == null) || (localam.b.equals(paramString) != true)) {}
      do
      {
        i1 += 1;
        break;
        localam.a(paramBoolean);
        if (!localam.e) {
          break label120;
        }
      } while (paramBoolean != true);
      if (localam.c <= localam.d) {}
      for (;;)
      {
        c(paramString);
        ay.this.c.a(false, false);
        return true;
        label120:
        return true;
        ay.this.c.a(localam.c);
        ay.this.c.b(localam.d);
      }
    }
    
    am b(String paramString)
    {
      if (paramString.equals("") == true) {}
      while ((this.a == null) || (this.a.size() == 0)) {
        return null;
      }
      int i2 = this.a.size();
      int i1 = 0;
      am localam;
      for (;;)
      {
        if (i1 >= i2) {
          return null;
        }
        localam = (am)this.a.get(i1);
        if ((localam != null) && (localam.b.equals(paramString) == true)) {
          break;
        }
        i1 += 1;
      }
      return localam;
    }
    
    public void b()
    {
      if (ay.this.c == null) {}
      while (ay.d.a(ay.this.c) == null) {
        return;
      }
      ay.d.a(ay.this.c).postInvalidate();
    }
    
    public void b(boolean paramBoolean)
    {
      this.m = paramBoolean;
    }
    
    public boolean b(int paramInt, KeyEvent paramKeyEvent)
    {
      return false;
    }
    
    protected boolean b(MotionEvent paramMotionEvent)
    {
      return false;
    }
  }
  
  public class b
  {
    public boolean a = false;
    int b = 0;
    
    public b()
    {
      e();
    }
    
    public void a()
    {
      if (!ay.a.a(ay.this.e))
      {
        this.b += 1;
        if (this.b >= 20) {
          break label46;
        }
      }
      label46:
      while (this.b % 20 != 0)
      {
        return;
        ay.this.e.b();
        break;
      }
      if (ay.this.e.a == null) {}
      while (ay.this.e.a.size() == 0) {
        return;
      }
      int j = ay.this.e.a.size();
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return;
        }
        ((am)ay.this.e.a.get(i)).q.i();
        i += 1;
      }
    }
    
    public void b()
    {
      ay.this.c.a = false;
      if (ay.this.e.a == null) {}
      while (ay.this.e.a.size() == 0) {
        return;
      }
      int j = ay.this.e.a.size();
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return;
        }
        ((am)ay.this.e.a.get(i)).q.b();
        i += 1;
      }
    }
    
    public void c()
    {
      if (ay.this.e.a == null) {}
      while (ay.this.e.a.size() == 0) {
        return;
      }
      int j = ay.this.e.a.size();
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return;
        }
        ((am)ay.this.e.a.get(i)).q.d();
        i += 1;
      }
    }
    
    public void d()
    {
      if (ay.this.e.a == null) {}
      while (ay.this.e.a.size() == 0) {
        return;
      }
      int j = ay.this.e.a.size();
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return;
        }
        ((am)ay.this.e.a.get(i)).q.c();
        i += 1;
      }
    }
    
    public void e()
    {
      if (ay.this.e.a == null) {}
      while (ay.this.e.a.size() == 0) {
        return;
      }
      int j = ay.this.e.a.size();
      int i = 0;
      for (;;)
      {
        if (i >= j) {
          return;
        }
        ((am)ay.this.e.a.get(i)).q.h();
        i += 1;
      }
    }
  }
  
  public class c
  {
    private final Context b;
    
    private c(ay paramay, Context paramContext)
    {
      this.b = paramContext;
    }
  }
  
  public class d
  {
    public boolean a = true;
    private b c;
    private ArrayList<bz> d;
    
    private d(b paramb)
    {
      this.c = paramb;
      this.d = new ArrayList();
    }
    
    public int a()
    {
      try
      {
        int i = ay.this.i.i;
        return i;
      }
      catch (Throwable localThrowable)
      {
        cj.a(localThrowable, "Mediator", "getMaxZoomLevel");
      }
      return 0;
    }
    
    public void a(float paramFloat)
    {
      int i;
      double d1;
      if (paramFloat != ay.this.i.j)
      {
        ay.this.i.j = paramFloat;
        i = (int)paramFloat;
        d1 = ay.this.i.d / (1 << i);
        if (paramFloat - i >= ay.a) {
          break label166;
        }
        ay.this.i.a = ((int)(ay.this.i.b * ((paramFloat - i) * 0.4D + 1.0D)));
      }
      label166:
      double d2;
      for (d1 /= ay.this.i.a / ay.this.i.b;; d1 = d1 / 2.0D / d2)
      {
        ay.this.i.k = d1;
        ay.this.h.c[1] = paramFloat;
        ay.this.h.f.a(paramFloat);
        a(false, false);
        return;
        float f = i;
        ay.this.i.a = ((int)(ay.this.i.b / (2.0F / (2.0F - (1.0F - (paramFloat - f)) * 0.4F))));
        d2 = ay.this.i.a / ay.this.i.b;
      }
    }
    
    public void a(int paramInt)
    {
      if (paramInt > 0) {}
      try
      {
        au localau = ay.this.i;
        p.c = paramInt;
        localau.i = paramInt;
        return;
      }
      catch (Throwable localThrowable)
      {
        cj.a(localThrowable, "Mediator", "setMaxZoomLevel");
      }
      return;
    }
    
    public void a(int paramInt1, int paramInt2)
    {
      if (paramInt1 != p.n) {}
      while (paramInt2 != p.o)
      {
        p.n = paramInt1;
        p.o = paramInt2;
        a(true, false);
        return;
      }
    }
    
    public void a(bz parambz)
    {
      this.d.add(parambz);
    }
    
    public void a(u paramu)
    {
      if (paramu != null) {
        if (p.r == true) {
          break label19;
        }
      }
      for (;;)
      {
        a(false, false);
        return;
        return;
        label19:
        paramu = ay.this.i.a(paramu);
        ay.this.i.l = paramu;
      }
    }
    
    public void a(boolean paramBoolean1, boolean paramBoolean2)
    {
      Iterator localIterator = this.d.iterator();
      if (!localIterator.hasNext()) {
        if (ay.this.h != null) {
          break label47;
        }
      }
      label47:
      while (ay.this.h.i == null)
      {
        return;
        ((bz)localIterator.next()).a(paramBoolean1, paramBoolean2);
        break;
      }
      ay.this.h.i.a(true);
      ay.this.h.postInvalidate();
    }
    
    public int b()
    {
      try
      {
        int i = ay.this.i.h;
        return i;
      }
      catch (Throwable localThrowable)
      {
        cj.a(localThrowable, "Mediator", "getMinZoomLevel");
      }
      return 0;
    }
    
    public void b(int paramInt)
    {
      if (paramInt > 0) {}
      try
      {
        au localau = ay.this.i;
        p.d = paramInt;
        localau.h = paramInt;
        return;
      }
      catch (Throwable localThrowable)
      {
        cj.a(localThrowable, "Mediator", "setMinZoomLevel");
      }
      return;
    }
    
    public void b(bz parambz)
    {
      this.d.remove(parambz);
    }
    
    public void b(u paramu)
    {
      u localu = ay.this.c.f();
      if (paramu == null) {}
      while (paramu.equals(localu)) {
        return;
      }
      if (p.r != true) {}
      for (;;)
      {
        a(false, true);
        return;
        paramu = ay.this.i.a(paramu);
        ay.this.i.l = paramu;
      }
    }
    
    public int c()
    {
      return p.n;
    }
    
    public int d()
    {
      return p.o;
    }
    
    public float e()
    {
      try
      {
        float f = ay.this.i.j;
        return f;
      }
      catch (Throwable localThrowable)
      {
        cj.a(localThrowable, "Mediator", "getZoomLevel");
      }
      return 0.0F;
    }
    
    public u f()
    {
      u localu = ay.this.i.b(ay.this.i.l);
      if (ay.this.d == null) {}
      while (!ay.this.d.a) {
        return localu;
      }
      return ay.this.i.m;
    }
    
    public b g()
    {
      return this.c;
    }
  }
  
  public class e
    implements bf
  {
    private float b = 0.0F;
    private HashMap<Float, Float> c = new HashMap();
    
    public e() {}
    
    private int a(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      int i = paramInt1;
      if (paramInt1 <= 0) {
        i = ay.this.c.c();
      }
      paramInt1 = paramInt2;
      if (paramInt2 <= 0) {
        paramInt1 = ay.this.c.d();
      }
      u localu1 = a(paramInt3, paramInt1 - paramInt3);
      u localu2 = a(i - paramInt3, paramInt3);
      if (!paramBoolean) {
        return Math.abs(localu1.b() - localu2.b());
      }
      return Math.abs(localu1.a() - localu2.a());
    }
    
    public float a(float paramFloat)
    {
      float f = ay.this.c.e();
      if (this.c.size() > 30)
      {
        this.b = f;
        this.c.clear();
        label35:
        if (!this.c.containsKey(Float.valueOf(paramFloat))) {
          break label79;
        }
      }
      for (;;)
      {
        return ((Float)this.c.get(Float.valueOf(paramFloat))).floatValue();
        if (f == this.b) {
          break label35;
        }
        break;
        label79:
        u localu1 = a(0, 0);
        u localu2 = a(0, 100);
        f = ay.this.i.a(localu1, localu2);
        if (f <= 0.0F) {
          return 0.0F;
        }
        f = paramFloat / f;
        this.c.put(Float.valueOf(paramFloat), Float.valueOf(f * 100.0F));
      }
    }
    
    public int a(int paramInt1, int paramInt2, int paramInt3)
    {
      return a(paramInt1, paramInt2, paramInt3, false);
    }
    
    public Point a(u paramu, Point paramPoint)
    {
      paramu = ay.this.i.b(paramu, ay.this.i.l, ay.this.i.n, ay.this.i.k);
      ba localba = ay.d.a(ay.this.c).G();
      Point localPoint = ay.d.a(ay.this.c).b().i.n;
      float f1;
      float f2;
      int i;
      int m;
      int k;
      int j;
      if (!localba.m)
      {
        f1 = ay.this.i.c;
        f2 = (int)paramu.x - localPoint.x;
        f1 = localPoint.x + f1 * f2;
        f2 = ay.this.i.c * ((int)paramu.y - localPoint.y) + localPoint.y;
        i = (int)f1;
        m = (int)f2;
        k = i;
        if (f1 >= i + 0.5D) {
          k = i + 1;
        }
        i = m;
        j = k;
        if (f2 >= m + 0.5D)
        {
          i = m + 1;
          j = k;
        }
      }
      for (;;)
      {
        paramu = new Point(j, i);
        if (paramPoint != null) {
          break;
        }
        return paramu;
        try
        {
          bool = ay.this.h.h.f();
          if (!localba.l)
          {
            j = (int)paramu.x;
            i = (int)paramu.y;
          }
        }
        catch (RemoteException localRemoteException)
        {
          boolean bool;
          do
          {
            for (;;)
            {
              localRemoteException.printStackTrace();
              bool = true;
            }
          } while (!bool);
          f1 = ba.j * ((int)paramu.x - localba.f.x) + localba.f.x + (localba.g.x - localba.f.x);
          f2 = ba.j * ((int)paramu.y - localba.f.y) + localba.f.y + (localba.g.y - localba.f.y);
          i = (int)f1;
          m = (int)f2;
          k = i;
          if (f1 >= i + 0.5D) {
            k = i + 1;
          }
          i = m;
          j = k;
        }
        if (f2 >= m + 0.5D)
        {
          i = m + 1;
          j = k;
        }
      }
      paramPoint.x = paramu.x;
      paramPoint.y = paramu.y;
      return paramu;
    }
    
    public u a(int paramInt1, int paramInt2)
    {
      PointF localPointF = new PointF(paramInt1, paramInt2);
      return ay.this.i.a(localPointF, ay.this.i.l, ay.this.i.n, ay.this.i.k, ay.this.i.o);
    }
    
    public int b(int paramInt1, int paramInt2, int paramInt3)
    {
      return a(paramInt1, paramInt2, paramInt3, true);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */