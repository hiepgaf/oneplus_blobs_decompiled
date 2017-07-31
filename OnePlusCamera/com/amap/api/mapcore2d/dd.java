package com.amap.api.mapcore2d;

import android.content.Context;
import android.os.Looper;
import java.util.Date;
import java.util.List;

public class dd
  extends df
{
  private static boolean a = true;
  
  protected dd(int paramInt)
  {
    super(paramInt);
  }
  
  protected String a(String paramString)
  {
    String str = cv.a(new Date().getTime());
    return cr.c(paramString + str);
  }
  
  protected String a(List<cu> paramList)
  {
    return null;
  }
  
  protected boolean a(Context paramContext)
  {
    if (!a) {
      return false;
    }
    a = false;
    synchronized (Looper.getMainLooper())
    {
      paramContext = new ds(paramContext);
      dt localdt = paramContext.a();
      if (localdt != null)
      {
        if (!localdt.a()) {
          return false;
        }
      }
      else {
        return true;
      }
      localdt.a(false);
      paramContext.a(localdt);
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/dd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */