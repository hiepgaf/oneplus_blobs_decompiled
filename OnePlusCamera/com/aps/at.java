package com.aps;

import android.os.Handler;
import android.os.Message;

final class at
  extends Handler
{
  at(as paramas) {}
  
  public final void handleMessage(Message paramMessage)
  {
    try
    {
      switch (paramMessage.what)
      {
      case 1: 
        if (y.d(this.a.a) != null)
        {
          y.d(this.a.a).a((String)paramMessage.obj);
          return;
        }
        break;
      }
    }
    catch (Exception paramMessage) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/at.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */