package com.amap.api.mapcore2d;

import android.content.Context;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

public class db
  extends cy
  implements Thread.UncaughtExceptionHandler
{
  private static ExecutorService e;
  private Context d;
  
  private db(Context paramContext, cu paramcu)
  {
    this.d = paramContext;
    eb.a(new a(paramContext));
    d();
  }
  
  public static db a()
  {
    try
    {
      db localdb = (db)cy.a;
      return localdb;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public static db a(Context paramContext, cu paramcu)
    throws ck
  {
    if (paramcu != null) {}
    label93:
    try
    {
      if (paramcu.a() == null) {
        throw new ck("sdk name is invalid");
      }
    }
    finally
    {
      boolean bool;
      do
      {
        throw paramContext;
        throw new ck("sdk info is null");
        bool = "".equals(paramcu.a());
      } while (bool);
      try
      {
        if (cy.a == null) {
          break label93;
        }
        cy.a.c = false;
        cy.a.a(paramContext, paramcu, cy.a.c);
      }
      catch (Throwable paramContext)
      {
        for (;;)
        {
          paramContext.printStackTrace();
        }
      }
      paramContext = (db)cy.a;
      return paramContext;
    }
  }
  
  public static void b()
  {
    for (;;)
    {
      try
      {
        ExecutorService localExecutorService = e;
        if (localExecutorService != null) {}
      }
      catch (Throwable localThrowable1)
      {
        localThrowable1.printStackTrace();
        continue;
      }
      finally {}
      try
      {
        if (cy.a != null) {
          break label50;
        }
        cy.a = null;
      }
      catch (Throwable localThrowable2)
      {
        localThrowable2.printStackTrace();
        continue;
      }
      return;
      e.shutdown();
      continue;
      label50:
      if ((Thread.getDefaultUncaughtExceptionHandler() == cy.a) && (cy.a.b != null)) {
        Thread.setDefaultUncaughtExceptionHandler(cy.a.b);
      }
    }
  }
  
  public static void b(Throwable paramThrowable, String paramString1, String paramString2)
  {
    if (cy.a == null) {
      return;
    }
    cy.a.a(paramThrowable, 1, paramString1, paramString2);
  }
  
  public static ExecutorService c()
  {
    for (;;)
    {
      try
      {
        if (e != null) {
          continue;
        }
        e = Executors.newSingleThreadExecutor();
      }
      catch (Throwable localThrowable)
      {
        ExecutorService localExecutorService;
        boolean bool;
        localThrowable.printStackTrace();
        continue;
      }
      finally {}
      localExecutorService = e;
      return localExecutorService;
      bool = e.isShutdown();
      if (bool) {}
    }
  }
  
  private void d()
  {
    do
    {
      try
      {
        this.b = Thread.getDefaultUncaughtExceptionHandler();
        if (this.b != null)
        {
          String str = this.b.toString();
          if (str.indexOf("com.amap.api") != -1) {
            this.c = false;
          }
        }
        else
        {
          Thread.setDefaultUncaughtExceptionHandler(this);
          this.c = true;
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
        return;
      }
    } while (localThrowable.indexOf("com.amap.loc") != -1);
    Thread.setDefaultUncaughtExceptionHandler(this);
    this.c = true;
  }
  
  protected void a(final Context paramContext, final cu paramcu, final boolean paramBoolean)
  {
    try
    {
      ExecutorService localExecutorService = c();
      if (localExecutorService == null) {
        return;
      }
      if (!localExecutorService.isShutdown())
      {
        localExecutorService.submit(new Runnable()
        {
          public void run()
          {
            try
            {
              synchronized ()
              {
                new dr(paramContext, true).a(paramcu);
                boolean bool = paramBoolean;
                if (!bool) {
                  return;
                }
              }
              synchronized (Looper.getMainLooper())
              {
                ds localds = new ds(paramContext);
                dt localdt = new dt();
                localdt.c(true);
                localdt.a(true);
                localdt.b(true);
                localds.a(localdt);
                cz.a(db.a(db.this));
                return;
              }
            }
            catch (Throwable localThrowable)
            {
              localThrowable.printStackTrace();
              return;
            }
          }
        });
        return;
      }
    }
    catch (RejectedExecutionException paramContext) {}catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
      return;
    }
  }
  
  protected void a(Throwable paramThrowable, int paramInt, String paramString1, String paramString2)
  {
    cz.a(this.d, paramThrowable, paramInt, paramString1, paramString2);
  }
  
  public void c(Throwable paramThrowable, String paramString1, String paramString2)
  {
    if (paramThrowable != null) {}
    try
    {
      a(paramThrowable, 1, paramString1, paramString2);
      return;
    }
    catch (Throwable paramThrowable)
    {
      paramThrowable.printStackTrace();
    }
    return;
  }
  
  public void uncaughtException(Thread paramThread, Throwable paramThrowable)
  {
    if (paramThrowable != null)
    {
      a(paramThrowable, 0, null, null);
      if (this.b != null) {}
    }
    else
    {
      return;
    }
    try
    {
      Thread.setDefaultUncaughtExceptionHandler(this.b);
      this.b.uncaughtException(paramThread, paramThrowable);
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;) {}
    }
  }
  
  private static class a
    implements ec
  {
    private Context a;
    
    a(Context paramContext)
    {
      this.a = paramContext;
    }
    
    public void a()
    {
      try
      {
        cz.b(this.a);
        return;
      }
      catch (Throwable localThrowable)
      {
        cy.a(localThrowable, "LogNetListener", "onNetCompleted");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/db.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */