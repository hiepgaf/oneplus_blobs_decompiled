package com.amap.api.mapcore2d;

import android.content.Context;
import java.util.List;

public class dp
{
  private dh a;
  
  public dp(Context paramContext)
  {
    try
    {
      this.a = new dh(paramContext, dh.a(do.class));
      return;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
  }
  
  private void c(String paramString, Class<? extends dq> paramClass)
  {
    paramString = dq.c(paramString);
    this.a.a(paramString, paramClass);
  }
  
  public List<? extends dq> a(int paramInt, Class<? extends dq> paramClass)
  {
    try
    {
      String str = dq.c(paramInt);
      paramClass = this.a.b(str, paramClass);
      return paramClass;
    }
    catch (Throwable paramClass)
    {
      cy.a(paramClass, "LogDB", "ByState");
    }
    return null;
  }
  
  public void a(dq paramdq)
  {
    String str;
    if (paramdq != null)
    {
      str = dq.c(paramdq.b());
      localObject = this.a.a(str, paramdq.getClass(), true);
      if (localObject != null) {
        break label41;
      }
    }
    label41:
    while (((List)localObject).size() == 0)
    {
      this.a.a(paramdq, true);
      return;
      return;
    }
    Object localObject = (dq)((List)localObject).get(0);
    if (paramdq.a() != 0) {
      ((dq)localObject).b(0);
    }
    for (;;)
    {
      this.a.a(str, localObject, true);
      return;
      ((dq)localObject).b(((dq)localObject).c() + 1);
    }
  }
  
  public void a(String paramString, Class<? extends dq> paramClass)
  {
    try
    {
      c(paramString, paramClass);
      return;
    }
    catch (Throwable paramString)
    {
      cy.a(paramString, "LogDB", "delLog");
    }
  }
  
  public void b(dq paramdq)
  {
    try
    {
      String str = dq.c(paramdq.b());
      this.a.a(str, paramdq);
      return;
    }
    catch (Throwable paramdq)
    {
      cy.a(paramdq, "LogDB", "updateLogInfo");
    }
  }
  
  public void b(String paramString, Class<? extends dq> paramClass)
  {
    try
    {
      c(paramString, paramClass);
      return;
    }
    catch (Throwable paramString)
    {
      paramString.printStackTrace();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/dp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */