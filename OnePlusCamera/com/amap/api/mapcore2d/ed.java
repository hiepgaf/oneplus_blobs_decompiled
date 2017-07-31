package com.amap.api.mapcore2d;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.net.Proxy;

public class ed
  extends dy
{
  private static ed a;
  private el b;
  private Handler c;
  
  private ed(boolean paramBoolean, int paramInt)
  {
    if (!paramBoolean) {}
    for (;;)
    {
      try
      {
        if (Looper.myLooper() == null) {
          break;
        }
        this.c = new a();
        return;
      }
      catch (Throwable localThrowable)
      {
        db.b(localThrowable, "NetManger", "NetManger1");
        localThrowable.printStackTrace();
        return;
      }
      this.b = el.a(paramInt);
    }
    this.c = new a(Looper.getMainLooper(), null);
  }
  
  public static ed a(boolean paramBoolean)
  {
    return a(paramBoolean, 5);
  }
  
  private static ed a(boolean paramBoolean, int paramInt)
  {
    for (;;)
    {
      try
      {
        localed = a;
        if (localed == null) {
          continue;
        }
        if (paramBoolean) {
          break label53;
        }
      }
      catch (Throwable localThrowable)
      {
        ed localed;
        localThrowable.printStackTrace();
        continue;
      }
      finally {}
      localed = a;
      return localed;
      a = new ed(paramBoolean, paramInt);
      continue;
      label53:
      if (a.b == null) {
        a.b = el.a(paramInt);
      }
    }
  }
  
  private void a(ck paramck, ef paramef)
  {
    eh localeh = new eh();
    localeh.a = paramck;
    localeh.b = paramef;
    paramck = Message.obtain();
    paramck.obj = localeh;
    paramck.what = 1;
    this.c.sendMessage(paramck);
  }
  
  private void a(eg parameg, ef paramef)
  {
    paramef.a(parameg.b, parameg.a);
    parameg = new eh();
    parameg.b = paramef;
    paramef = Message.obtain();
    paramef.obj = parameg;
    paramef.what = 0;
    this.c.sendMessage(paramef);
  }
  
  public byte[] a(ee paramee)
    throws ck
  {
    try
    {
      paramee = a(paramee, false);
      if (paramee == null) {
        return null;
      }
    }
    catch (ck paramee)
    {
      throw paramee;
    }
    catch (Throwable paramee)
    {
      paramee.printStackTrace();
      db.a().c(paramee, "NetManager", "makeSyncPostRequest");
      throw new ck("未知的错误");
    }
    return paramee.a;
  }
  
  public eg b(ee paramee, boolean paramBoolean)
    throws ck
  {
    Proxy localProxy = null;
    try
    {
      b(paramee);
      if (paramee.e != null) {
        localProxy = paramee.e;
      }
      paramee = new eb(paramee.c, paramee.d, localProxy, paramBoolean).a(paramee.g(), paramee.e(), paramee.f());
      return paramee;
    }
    catch (ck paramee)
    {
      throw paramee;
    }
    catch (Throwable paramee)
    {
      paramee.printStackTrace();
      throw new ck("未知的错误");
    }
  }
  
  public byte[] c(ee paramee)
    throws ck
  {
    try
    {
      paramee = b(paramee, false);
      if (paramee == null) {
        return null;
      }
    }
    catch (ck paramee)
    {
      throw paramee;
    }
    catch (Throwable paramee)
    {
      throw new ck("未知的错误");
    }
    return paramee.a;
  }
  
  static class a
    extends Handler
  {
    public a() {}
    
    private a(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      try
      {
        switch (paramMessage.what)
        {
        case 0: 
          ((eh)paramMessage.obj).b.a();
          return;
        }
      }
      catch (Throwable paramMessage)
      {
        paramMessage.printStackTrace();
        return;
      }
      paramMessage = (eh)paramMessage.obj;
      paramMessage.b.a(paramMessage.a);
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/ed.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */