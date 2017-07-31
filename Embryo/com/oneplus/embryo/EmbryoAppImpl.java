package com.oneplus.embryo;

import android.app.IEmbryoApp;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Process;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ThreadedRenderer;
import android.view.View;
import android.view.WindowManagerGlobal;
import java.io.File;

public class EmbryoAppImpl
  implements Runnable, IEmbryoApp
{
  public static final boolean m = SystemProperties.getBoolean("persist.sys.assert.panic", false);
  private static final boolean n;
  private static final boolean o = SystemProperties.getBoolean("persist.sys.embryo.rename", m);
  private static final boolean p;
  private static String v = "/data/theme/color";
  private static String w;
  boolean A = false;
  SharedPreferences B;
  Context q;
  private boolean r = false;
  private a s;
  private boolean t = false;
  LayoutInflater u;
  private a x;
  boolean y = false;
  boolean z = false;
  
  static
  {
    n = SystemProperties.getBoolean("persist.sys.embryo.inflate", true);
    p = SystemProperties.getBoolean("persist.sys.embryo.optheme", true);
    w = "/data/theme";
  }
  
  private void i()
  {
    this.q = null;
    this.B = null;
    this.u = null;
    this.t = true;
  }
  
  private static boolean j(String paramString)
  {
    Object localObject = new File("/system/media/themes/");
    if (!((File)localObject).exists()) {
      return false;
    }
    localObject = ((File)localObject).listFiles();
    int k = localObject.length;
    int i = 0;
    if (i < k)
    {
      String[] arrayOfString = localObject[i];
      if (arrayOfString.isFile()) {}
      for (;;)
      {
        i += 1;
        break;
        arrayOfString = arrayOfString.list();
        int i1 = arrayOfString.length;
        int j = 0;
        while (j < i1)
        {
          if (arrayOfString[j].endsWith(paramString + ".apk")) {
            return true;
          }
          j += 1;
        }
      }
    }
    return false;
  }
  
  private void k()
  {
    if (!this.B.contains("layout_preload_decor")) {
      return;
    }
    this.s = new a("decor");
    this.y = this.s.b(this.q, this.B, this.u);
  }
  
  private void l()
  {
    if (!this.B.contains("layout_preload_main")) {
      return;
    }
    this.x = new a("main");
    this.z = this.x.b(this.q, this.B, this.u);
  }
  
  private void m()
  {
    if ((!this.r) || (this.t)) {
      return;
    }
    if (!this.B.contains("hwui")) {
      return;
    }
    if (!this.B.getBoolean("hwui", false)) {
      return;
    }
    ThreadedRenderer.prepareRenderThread();
    this.A = true;
  }
  
  private void n()
  {
    this.B = this.q.getSharedPreferences("embryo", 0);
    this.u = LayoutInflater.from(this.q);
  }
  
  public void attach(Context paramContext)
  {
    this.q = paramContext;
    this.r = true;
  }
  
  public View checkDecorLayout(Context paramContext, int paramInt)
  {
    if (this.s == null) {
      this.s = new a("decor");
    }
    return this.s.a(paramContext, paramInt);
  }
  
  public void checkHWUI(Context paramContext)
  {
    paramContext.getSharedPreferences("embryo", 0).edit().putBoolean("hwui", true).apply();
  }
  
  public View checkMainLayout(Context paramContext, int paramInt)
  {
    if (this.x == null) {
      this.x = new a("main");
    }
    return this.x.a(paramContext, paramInt);
  }
  
  public Runnable getRunnable()
  {
    return this;
  }
  
  public void run()
  {
    if ((!this.r) || (this.t)) {
      return;
    }
    String str = this.q.getPackageName();
    long l1 = System.currentTimeMillis();
    if (o)
    {
      Process.setArgV0("embryo:" + str);
      n();
      if (n)
      {
        boolean bool = j(str);
        WindowManagerGlobal.initialize();
        if (!bool) {
          break label190;
        }
        if (p)
        {
          k();
          l();
        }
      }
    }
    for (;;)
    {
      m();
      long l2 = System.currentTimeMillis();
      Log.d("Embryo", "preload " + str + ", " + (l2 - l1) + "ms, hwui=" + this.A + ", layout=" + this.z + ", decor=" + this.y);
      i();
      return;
      Process.setArgV0("embryo");
      break;
      label190:
      k();
      l();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/embryo/EmbryoAppImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */