package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.os.Binder;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.ArrayMap;
import android.util.Pools.Pool;
import android.util.Pools.SimplePool;
import android.view.InputEventConsistencyVerifier;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class KeyEventDispatcher
{
  private static final boolean DEBUG = false;
  private static final String LOG_TAG = "KeyEventDispatcher";
  private static final int MAX_POOL_SIZE = 10;
  private static final int MSG_ON_KEY_EVENT_TIMEOUT = 1;
  private static final long ON_KEY_EVENT_TIMEOUT_MILLIS = 500L;
  private final Handler mHandlerToSendKeyEventsToInputFilter;
  private final Handler mKeyEventTimeoutHandler;
  private final Object mLock;
  private final int mMessageTypeForSendKeyEvent;
  private final Pools.Pool<PendingKeyEvent> mPendingEventPool = new Pools.SimplePool(10);
  private final Map<AccessibilityManagerService.Service, ArrayList<PendingKeyEvent>> mPendingEventsMap = new ArrayMap();
  private final PowerManager mPowerManager;
  private final InputEventConsistencyVerifier mSentEventsVerifier;
  
  public KeyEventDispatcher(Handler paramHandler, int paramInt, Object paramObject, PowerManager paramPowerManager)
  {
    if (InputEventConsistencyVerifier.isInstrumentationEnabled()) {}
    for (this.mSentEventsVerifier = new InputEventConsistencyVerifier(this, 0, KeyEventDispatcher.class.getSimpleName());; this.mSentEventsVerifier = null)
    {
      this.mHandlerToSendKeyEventsToInputFilter = paramHandler;
      this.mMessageTypeForSendKeyEvent = paramInt;
      this.mKeyEventTimeoutHandler = new Handler(this.mHandlerToSendKeyEventsToInputFilter.getLooper(), new Callback(null));
      this.mLock = paramObject;
      this.mPowerManager = paramPowerManager;
      return;
    }
  }
  
  private PendingKeyEvent obtainPendingEventLocked(KeyEvent paramKeyEvent, int paramInt)
  {
    PendingKeyEvent localPendingKeyEvent2 = (PendingKeyEvent)this.mPendingEventPool.acquire();
    PendingKeyEvent localPendingKeyEvent1 = localPendingKeyEvent2;
    if (localPendingKeyEvent2 == null) {
      localPendingKeyEvent1 = new PendingKeyEvent(null);
    }
    localPendingKeyEvent1.event = paramKeyEvent;
    localPendingKeyEvent1.policyFlags = paramInt;
    localPendingKeyEvent1.referenceCount = 0;
    localPendingKeyEvent1.handled = false;
    return localPendingKeyEvent1;
  }
  
  private static PendingKeyEvent removeEventFromListLocked(List<PendingKeyEvent> paramList, int paramInt)
  {
    int i = 0;
    while (i < paramList.size())
    {
      PendingKeyEvent localPendingKeyEvent = (PendingKeyEvent)paramList.get(i);
      if (localPendingKeyEvent.event.getSequenceNumber() == paramInt)
      {
        paramList.remove(localPendingKeyEvent);
        return localPendingKeyEvent;
      }
      i += 1;
    }
    return null;
  }
  
  private boolean removeReferenceToPendingEventLocked(PendingKeyEvent paramPendingKeyEvent)
  {
    int i = paramPendingKeyEvent.referenceCount - 1;
    paramPendingKeyEvent.referenceCount = i;
    if (i > 0) {
      return false;
    }
    this.mKeyEventTimeoutHandler.removeMessages(1, paramPendingKeyEvent);
    if (!paramPendingKeyEvent.handled)
    {
      if (this.mSentEventsVerifier != null) {
        this.mSentEventsVerifier.onKeyEvent(paramPendingKeyEvent.event, 0);
      }
      i = paramPendingKeyEvent.policyFlags;
      this.mHandlerToSendKeyEventsToInputFilter.obtainMessage(this.mMessageTypeForSendKeyEvent, i | 0x40000000, 0, paramPendingKeyEvent.event).sendToTarget();
    }
    for (;;)
    {
      this.mPendingEventPool.release(paramPendingKeyEvent);
      return true;
      paramPendingKeyEvent.event.recycle();
    }
  }
  
  public void flush(AccessibilityManagerService.Service paramService)
  {
    synchronized (this.mLock)
    {
      List localList = (List)this.mPendingEventsMap.get(paramService);
      if (localList != null)
      {
        int i = 0;
        while (i < localList.size())
        {
          removeReferenceToPendingEventLocked((PendingKeyEvent)localList.get(i));
          i += 1;
        }
        this.mPendingEventsMap.remove(paramService);
      }
      return;
    }
  }
  
  public boolean notifyKeyEventLocked(KeyEvent paramKeyEvent, int paramInt, List<AccessibilityManagerService.Service> paramList)
  {
    Object localObject = null;
    KeyEvent localKeyEvent = KeyEvent.obtain(paramKeyEvent);
    int i = 0;
    AccessibilityManagerService.Service localService;
    if (i < paramList.size())
    {
      localService = (AccessibilityManagerService.Service)paramList.get(i);
      paramKeyEvent = (KeyEvent)localObject;
      if (localService.mRequestFilterKeyEvents)
      {
        if (localService.mServiceInterface != null) {
          break label70;
        }
        paramKeyEvent = (KeyEvent)localObject;
      }
    }
    for (;;)
    {
      i += 1;
      localObject = paramKeyEvent;
      break;
      label70:
      paramKeyEvent = (KeyEvent)localObject;
      if ((localService.mAccessibilityServiceInfo.getCapabilities() & 0x8) != 0)
      {
        try
        {
          localService.mServiceInterface.onKeyEvent(localKeyEvent, localKeyEvent.getSequenceNumber());
          paramKeyEvent = (KeyEvent)localObject;
          if (localObject == null) {
            paramKeyEvent = obtainPendingEventLocked(localKeyEvent, paramInt);
          }
          ArrayList localArrayList = (ArrayList)this.mPendingEventsMap.get(localService);
          localObject = localArrayList;
          if (localArrayList == null)
          {
            localObject = new ArrayList();
            this.mPendingEventsMap.put(localService, localObject);
          }
          ((ArrayList)localObject).add(paramKeyEvent);
          paramKeyEvent.referenceCount += 1;
        }
        catch (RemoteException paramKeyEvent)
        {
          paramKeyEvent = (KeyEvent)localObject;
        }
        if (localObject == null)
        {
          localKeyEvent.recycle();
          return false;
        }
        paramKeyEvent = this.mKeyEventTimeoutHandler.obtainMessage(1, localObject);
        this.mKeyEventTimeoutHandler.sendMessageDelayed(paramKeyEvent, 500L);
        return true;
      }
    }
  }
  
  public void setOnKeyEventResult(AccessibilityManagerService.Service paramService, boolean paramBoolean, int paramInt)
  {
    synchronized (this.mLock)
    {
      paramService = removeEventFromListLocked((List)this.mPendingEventsMap.get(paramService), paramInt);
      if (paramService != null)
      {
        if ((!paramBoolean) || (paramService.handled)) {
          removeReferenceToPendingEventLocked(paramService);
        }
      }
      else {
        return;
      }
      paramService.handled = paramBoolean;
      l = Binder.clearCallingIdentity();
    }
  }
  
  private class Callback
    implements Handler.Callback
  {
    private Callback() {}
    
    public boolean handleMessage(Message arg1)
    {
      if (???.what != 1) {
        throw new IllegalArgumentException("Unknown message: " + ???.what);
      }
      KeyEventDispatcher.PendingKeyEvent localPendingKeyEvent = (KeyEventDispatcher.PendingKeyEvent)???.obj;
      synchronized (KeyEventDispatcher.-get0(KeyEventDispatcher.this))
      {
        Iterator localIterator = KeyEventDispatcher.-get1(KeyEventDispatcher.this).values().iterator();
        boolean bool;
        do
        {
          do
          {
            if (!localIterator.hasNext()) {
              break;
            }
          } while (!((ArrayList)localIterator.next()).remove(localPendingKeyEvent));
          bool = KeyEventDispatcher.-wrap0(KeyEventDispatcher.this, localPendingKeyEvent);
        } while (!bool);
        return true;
      }
    }
  }
  
  private static final class PendingKeyEvent
  {
    KeyEvent event;
    boolean handled;
    int policyFlags;
    int referenceCount;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/KeyEventDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */