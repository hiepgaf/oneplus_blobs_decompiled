package com.amap.api.mapcore2d;

import android.content.Context;
import android.os.Looper;
import java.util.List;

public class de
  extends df
{
  private static boolean a = true;
  
  protected de(int paramInt)
  {
    super(paramInt);
  }
  
  protected String a(List<cu> paramList)
  {
    return null;
  }
  
  protected boolean a(Context paramContext)
  {
    if (cp.m(paramContext) != 1) {}
    while (!a) {
      return false;
    }
    a = false;
    synchronized (Looper.getMainLooper())
    {
      paramContext = new ds(paramContext);
      dt localdt = paramContext.a();
      if (localdt != null)
      {
        if (!localdt.b()) {
          return false;
        }
      }
      else {
        return true;
      }
      localdt.b(false);
      paramContext.a(localdt);
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/de.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */