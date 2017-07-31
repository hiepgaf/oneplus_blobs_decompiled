package com.oneplus.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class ConcurrencyObject<T>
{
  private static final int MSG_SYNC_VALUE = 10001;
  private final Object SYNC_VALUE_INTERNAL = new Object();
  private Handler m_Handler;
  private Boolean m_IsSendingMsgSyncValue = Boolean.valueOf(false);
  private T m_Value;
  private volatile T m_ValueInternal;
  
  public ConcurrencyObject()
  {
    this(Looper.getMainLooper());
  }
  
  public ConcurrencyObject(Handler paramHandler)
  {
    this(paramHandler.getLooper());
  }
  
  public ConcurrencyObject(Looper paramLooper)
  {
    this.m_Handler = new Handler(paramLooper)
    {
      public void handleMessage(Message paramAnonymousMessage)
      {
        ConcurrencyObject.-wrap0(ConcurrencyObject.this, paramAnonymousMessage);
      }
    };
  }
  
  private T getInternalValue()
  {
    synchronized (this.SYNC_VALUE_INTERNAL)
    {
      Object localObject2 = this.m_ValueInternal;
      return (T)localObject2;
    }
  }
  
  private void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return;
    }
    setIsSendingMsgSyncValue(false);
    syncValue();
  }
  
  private boolean isSendingMsgSyncValue()
  {
    synchronized (this.m_IsSendingMsgSyncValue)
    {
      boolean bool = this.m_IsSendingMsgSyncValue.booleanValue();
      return bool;
    }
  }
  
  private void setInternalValue(T paramT)
  {
    synchronized (this.SYNC_VALUE_INTERNAL)
    {
      this.m_ValueInternal = paramT;
      return;
    }
  }
  
  private void setIsSendingMsgSyncValue(boolean paramBoolean)
  {
    synchronized (this.m_IsSendingMsgSyncValue)
    {
      this.m_IsSendingMsgSyncValue = Boolean.valueOf(paramBoolean);
      return;
    }
  }
  
  private void syncValue()
  {
    this.m_Value = getInternalValue();
  }
  
  public T get()
  {
    if (isSyncThread()) {
      return (T)this.m_Value;
    }
    return (T)getInternalValue();
  }
  
  public boolean isSyncThread()
  {
    return Thread.currentThread() == this.m_Handler.getLooper().getThread();
  }
  
  public void set(T paramT)
  {
    if (isSyncThread())
    {
      this.m_Value = paramT;
      setInternalValue(paramT);
    }
    do
    {
      return;
      setInternalValue(paramT);
    } while (isSendingMsgSyncValue());
    this.m_Handler.sendEmptyMessage(10001);
    setIsSendingMsgSyncValue(true);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/ConcurrencyObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */