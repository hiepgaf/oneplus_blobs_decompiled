package com.amap.api.mapcore2d;

import android.content.Context;

public class cy
{
  protected static cy a;
  protected Thread.UncaughtExceptionHandler b;
  protected boolean c = true;
  
  public static void a(Throwable paramThrowable, String paramString1, String paramString2)
  {
    paramThrowable.printStackTrace();
    if (a == null) {
      return;
    }
    a.a(paramThrowable, 1, paramString1, paramString2);
  }
  
  protected void a(Context paramContext, cu paramcu, boolean paramBoolean) {}
  
  protected void a(Throwable paramThrowable, int paramInt, String paramString1, String paramString2) {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/cy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */