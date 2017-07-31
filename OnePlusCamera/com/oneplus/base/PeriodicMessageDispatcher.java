package com.oneplus.base;

import android.os.Handler;
import com.oneplus.base.component.Component;

public abstract interface PeriodicMessageDispatcher
  extends Component
{
  public static final int FLAG_UNIQUE = 1;
  
  public abstract void pause();
  
  public abstract void removeMessages(Handler paramHandler, int paramInt);
  
  public abstract void resume();
  
  public abstract void scheduleMessage(Handler paramHandler, int paramInt1, int paramInt2, int paramInt3, Object paramObject, int paramInt4);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/PeriodicMessageDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */