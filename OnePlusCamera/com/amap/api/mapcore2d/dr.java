package com.amap.api.mapcore2d;

import android.content.Context;
import java.util.List;

public class dr
{
  private dh a;
  private Context b;
  
  public dr(Context paramContext, boolean paramBoolean)
  {
    this.b = paramContext;
    this.a = a(this.b, paramBoolean);
  }
  
  private dh a(Context paramContext, boolean paramBoolean)
  {
    try
    {
      paramContext = new dh(paramContext, dh.a(do.class));
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      if (paramBoolean)
      {
        paramContext.printStackTrace();
        return null;
      }
      cy.a(paramContext, "SDKDB", "getDB");
    }
    return null;
  }
  
  public List<cu> a()
  {
    try
    {
      Object localObject = cu.g();
      localObject = this.a.a((String)localObject, cu.class, true);
      return (List<cu>)localObject;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return null;
  }
  
  public void a(cu paramcu)
  {
    if (paramcu != null)
    {
      String str;
      try
      {
        List localList;
        if (this.a != null)
        {
          str = cu.a(paramcu.a());
          localList = this.a.b(str, cu.class);
          if (localList != null) {
            break label73;
          }
        }
        while (localList.size() == 0)
        {
          this.a.a(paramcu);
          return;
          this.a = a(this.b, false);
          break;
        }
      }
      catch (Throwable paramcu)
      {
        cy.a(paramcu, "SDKDB", "insert");
        paramcu.printStackTrace();
        return;
      }
      label73:
      this.a.a(str, paramcu);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/dr.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */