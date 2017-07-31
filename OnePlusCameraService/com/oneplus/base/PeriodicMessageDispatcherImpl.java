package com.oneplus.base;

import android.os.Handler;
import android.os.Message;
import com.oneplus.base.component.BasicComponent;
import com.oneplus.base.component.ComponentOwner;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

class PeriodicMessageDispatcherImpl
  extends BasicComponent
  implements PeriodicMessageDispatcher
{
  private static final int DURATION_HANDLE_SCHDULED_MESSAGES = 10;
  private static final int MAX_MESSAGE_COUNTS_PER_HANDLED = 100;
  private static final int MSG_HANDLE_SCHEDULED_MESSAGES = -70001;
  private static final int MSG_PAUSE = -70005;
  private static final int MSG_RESUME = -70006;
  private final Object SYNC_FIELDS = new Object();
  private volatile boolean m_HasMessage;
  private volatile boolean m_IsPaused;
  private final Deque<Message> m_ScheduledMessages = new ArrayDeque();
  
  public PeriodicMessageDispatcherImpl(ComponentOwner paramComponentOwner)
  {
    super("Periodic Message Dispatcher Impl", paramComponentOwner, true);
  }
  
  private void handleScheduledMessages()
  {
    synchronized (this.SYNC_FIELDS)
    {
      this.m_HasMessage = false;
      if (this.m_IsPaused) {
        return;
      }
    }
    if (this.m_ScheduledMessages.isEmpty()) {
      return;
    }
    synchronized (this.m_ScheduledMessages)
    {
      boolean bool = this.m_ScheduledMessages.isEmpty();
      if (bool) {
        return;
      }
      ArrayList localArrayList = new ArrayList();
      int i = 0;
      while (i < 100)
      {
        Message localMessage = (Message)this.m_ScheduledMessages.poll();
        if (localMessage == null) {
          break;
        }
        localArrayList.add(localMessage);
        i += 1;
      }
      if (this.m_ScheduledMessages.size() > 0) {
        sendHandleScheduledMessage();
      }
      int j = localArrayList.size();
      i = 0;
      if (i < j)
      {
        ??? = (Message)localArrayList.get(i);
        ((Message)???).getTarget().handleMessage((Message)???);
        ((Message)???).recycle();
        i += 1;
      }
    }
  }
  
  private void sendHandleScheduledMessage()
  {
    if ((this.m_HasMessage) || (this.m_IsPaused)) {
      return;
    }
    synchronized (this.SYNC_FIELDS)
    {
      if (!this.m_HasMessage)
      {
        boolean bool = this.m_IsPaused;
        if (!bool) {}
      }
      else
      {
        return;
      }
      this.m_HasMessage = true;
      getHandler().sendEmptyMessageDelayed(-70001, 10L);
      return;
    }
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    case -70004: 
    case -70003: 
    case -70002: 
    default: 
      return;
    case -70001: 
      handleScheduledMessages();
      return;
    case -70005: 
      pause();
      return;
    }
    resume();
  }
  
  public void pause()
  {
    if (isDependencyThread()) {
      synchronized (this.SYNC_FIELDS)
      {
        this.m_IsPaused = true;
        this.m_HasMessage = false;
        getHandler().removeMessages(-70001);
        return;
      }
    }
    getHandler().sendEmptyMessage(-70005);
  }
  
  public void removeMessages(Handler arg1, int paramInt)
  {
    synchronized (this.m_ScheduledMessages)
    {
      Iterator localIterator = this.m_ScheduledMessages.iterator();
      while (localIterator.hasNext())
      {
        Message localMessage = (Message)localIterator.next();
        if ((localMessage.getTarget() == ???) && (localMessage.what == paramInt)) {
          localIterator.remove();
        }
      }
    }
    if (this.m_ScheduledMessages.size() == 0) {}
    synchronized (this.SYNC_FIELDS)
    {
      this.m_HasMessage = false;
      getHandler().removeMessages(-70001);
      return;
    }
  }
  
  public void resume()
  {
    if (isDependencyThread()) {
      synchronized (this.m_ScheduledMessages)
      {
        synchronized (this.SYNC_FIELDS)
        {
          this.m_IsPaused = false;
          if (this.m_ScheduledMessages.size() > 0) {
            sendHandleScheduledMessage();
          }
          return;
        }
      }
    }
    getHandler().sendEmptyMessage(-70006);
  }
  
  public void scheduleMessage(Handler paramHandler, int paramInt1, int paramInt2, int paramInt3, Object paramObject, int paramInt4)
  {
    Deque localDeque = this.m_ScheduledMessages;
    if ((paramInt4 & 0x1) != 0) {
      try
      {
        Iterator localIterator = this.m_ScheduledMessages.iterator();
        while (localIterator.hasNext())
        {
          Message localMessage = (Message)localIterator.next();
          if ((localMessage.getTarget() == paramHandler) && (localMessage.what == paramInt1)) {
            localIterator.remove();
          }
        }
        this.m_ScheduledMessages.add(Message.obtain(paramHandler, paramInt1, paramInt2, paramInt3, paramObject));
      }
      finally {}
    }
    sendHandleScheduledMessage();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/PeriodicMessageDispatcherImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */