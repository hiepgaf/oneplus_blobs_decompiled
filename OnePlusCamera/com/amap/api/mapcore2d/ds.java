package com.amap.api.mapcore2d;

import android.content.Context;
import java.util.List;

public class ds
{
  private dh a;
  private Context b;
  
  public ds(Context paramContext)
  {
    this.b = paramContext;
    this.a = a(this.b);
  }
  
  private dh a(Context paramContext)
  {
    try
    {
      paramContext = new dh(paramContext, dh.a(do.class));
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      cy.a(paramContext, "UpdateLogDB", "getDB");
    }
    return null;
  }
  
  public dt a()
  {
    try
    {
      if (this.a != null) {}
      for (;;)
      {
        List localList = this.a.b("1=1", dt.class);
        if (localList.size() > 0) {
          break;
        }
        return null;
        this.a = a(this.b);
      }
      localdt = (dt)localThrowable.get(0);
    }
    catch (Throwable localThrowable)
    {
      cy.a(localThrowable, "UpdateLogDB", "getUpdateLog");
      return null;
    }
    dt localdt;
    return localdt;
  }
  
  public void a(dt paramdt)
  {
    if (paramdt != null)
    {
      try
      {
        List localList;
        if (this.a != null)
        {
          localList = this.a.b("1=1", dt.class);
          if (localList != null) {
            break label61;
          }
        }
        while (localList.size() == 0)
        {
          this.a.a(paramdt);
          return;
          this.a = a(this.b);
          break;
        }
      }
      catch (Throwable paramdt)
      {
        cy.a(paramdt, "UpdateLogDB", "updateLog");
        return;
      }
      label61:
      this.a.a("1=1", paramdt);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ds.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */