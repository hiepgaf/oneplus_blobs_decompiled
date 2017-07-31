package com.amap.api.mapcore2d;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import com.amap.api.maps2d.MapsInitializer;

public class f
  extends Thread
{
  private Context a;
  private w b;
  
  public f(Context paramContext, w paramw)
  {
    this.a = paramContext;
    this.b = paramw;
  }
  
  public void run()
  {
    for (;;)
    {
      Object localObject;
      Message localMessage;
      try
      {
        if (!MapsInitializer.getNetworkEnable()) {
          break;
        }
        cu localcu = cj.a();
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("002");
        ((StringBuilder)localObject).append(";");
        ((StringBuilder)localObject).append("11K");
        localObject = ((StringBuilder)localObject).toString();
        localObject = cm.a(this.a, localcu, (String)localObject, null);
        if (cm.a == 1)
        {
          break label217;
          p.p = localcu;
          db.a(this.a, localcu);
          interrupt();
        }
        else
        {
          localMessage = this.b.a().obtainMessage();
          localMessage.what = 2;
          if (((cm.a)localObject).a == null) {
            this.b.a().sendMessage(localMessage);
          }
        }
      }
      catch (Throwable localThrowable)
      {
        interrupt();
        db.b(localThrowable, "AMapDelegateImpGLSurfaceView", "mVerfy");
        localThrowable.printStackTrace();
        return;
      }
      localMessage.obj = ((cm.a)localObject).a;
      continue;
      label217:
      do
      {
        if (((cm.a)localObject).p == null) {}
        while (((cm.a)localObject).r != null)
        {
          new ct(this.a, "2dmap", ((cm.a)localObject).r.a, ((cm.a)localObject).r.b).a();
          break;
          cj.a().a(((cm.a)localObject).p.a);
        }
      } while (localObject != null);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/f.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */