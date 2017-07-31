package com.android.server.accessibility;

import android.accessibilityservice.IAccessibilityServiceClient;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Slog;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.MotionEvent.PointerProperties;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.os.SomeArgs;
import java.util.List;

public class MotionEventInjector
  implements EventStreamTransformation
{
  private static final String LOG_TAG = "MotionEventInjector";
  private static final int MAX_POINTERS = 11;
  private static final int MESSAGE_INJECT_EVENTS = 2;
  private static final int MESSAGE_SEND_MOTION_EVENT = 1;
  private final Handler mHandler = new Handler(paramLooper, new Callback(null));
  private boolean mIsDestroyed = false;
  private EventStreamTransformation mNext;
  private final SparseArray<Boolean> mOpenGesturesInProgress = new SparseArray();
  private MotionEvent.PointerCoords[] mPointerCoords = new MotionEvent.PointerCoords[11];
  private MotionEvent.PointerProperties[] mPointerProperties = new MotionEvent.PointerProperties[11];
  private int mSequenceForCurrentGesture;
  private IAccessibilityServiceClient mServiceInterfaceForCurrentGesture;
  private int mSourceOfInjectedGesture = 0;
  
  public MotionEventInjector(Looper paramLooper) {}
  
  private void cancelAnyGestureInProgress(int paramInt)
  {
    if ((this.mNext != null) && (((Boolean)this.mOpenGesturesInProgress.get(paramInt, Boolean.valueOf(false))).booleanValue()))
    {
      long l = SystemClock.uptimeMillis();
      MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
      sendMotionEventToNext(localMotionEvent, localMotionEvent, 1073741824);
    }
  }
  
  private void cancelAnyPendingInjectedEvents()
  {
    if (this.mHandler.hasMessages(1))
    {
      cancelAnyGestureInProgress(this.mSourceOfInjectedGesture);
      this.mHandler.removeMessages(1);
      notifyService(false);
    }
  }
  
  private void injectEventsMainThread(List<MotionEvent> paramList, IAccessibilityServiceClient paramIAccessibilityServiceClient, int paramInt)
  {
    if (this.mIsDestroyed) {
      try
      {
        paramIAccessibilityServiceClient.onPerformGestureResult(paramInt, false);
        return;
      }
      catch (RemoteException paramList)
      {
        Slog.e("MotionEventInjector", "Error sending status with mIsDestroyed to " + paramIAccessibilityServiceClient, paramList);
        return;
      }
    }
    cancelAnyPendingInjectedEvents();
    this.mSourceOfInjectedGesture = ((MotionEvent)paramList.get(0)).getSource();
    cancelAnyGestureInProgress(this.mSourceOfInjectedGesture);
    this.mServiceInterfaceForCurrentGesture = paramIAccessibilityServiceClient;
    this.mSequenceForCurrentGesture = paramInt;
    if (this.mNext == null)
    {
      notifyService(false);
      return;
    }
    long l = SystemClock.uptimeMillis();
    paramInt = 0;
    while (paramInt < paramList.size())
    {
      paramIAccessibilityServiceClient = (MotionEvent)paramList.get(paramInt);
      int j = paramIAccessibilityServiceClient.getPointerCount();
      if (j > this.mPointerCoords.length)
      {
        this.mPointerCoords = new MotionEvent.PointerCoords[j];
        this.mPointerProperties = new MotionEvent.PointerProperties[j];
      }
      int i = 0;
      while (i < j)
      {
        if (this.mPointerCoords[i] == null)
        {
          this.mPointerCoords[i] = new MotionEvent.PointerCoords();
          this.mPointerProperties[i] = new MotionEvent.PointerProperties();
        }
        paramIAccessibilityServiceClient.getPointerCoords(i, this.mPointerCoords[i]);
        paramIAccessibilityServiceClient.getPointerProperties(i, this.mPointerProperties[i]);
        i += 1;
      }
      Object localObject = MotionEvent.obtain(paramIAccessibilityServiceClient.getDownTime() + l, paramIAccessibilityServiceClient.getEventTime() + l, paramIAccessibilityServiceClient.getAction(), j, this.mPointerProperties, this.mPointerCoords, paramIAccessibilityServiceClient.getMetaState(), paramIAccessibilityServiceClient.getButtonState(), paramIAccessibilityServiceClient.getXPrecision(), paramIAccessibilityServiceClient.getYPrecision(), paramIAccessibilityServiceClient.getDeviceId(), paramIAccessibilityServiceClient.getEdgeFlags(), paramIAccessibilityServiceClient.getSource(), paramIAccessibilityServiceClient.getFlags());
      localObject = this.mHandler.obtainMessage(1, localObject);
      this.mHandler.sendMessageDelayed((Message)localObject, paramIAccessibilityServiceClient.getEventTime());
      paramInt += 1;
    }
  }
  
  private void notifyService(boolean paramBoolean)
  {
    try
    {
      this.mServiceInterfaceForCurrentGesture.onPerformGestureResult(this.mSequenceForCurrentGesture, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.e("MotionEventInjector", "Error sending motion event injection status to " + this.mServiceInterfaceForCurrentGesture, localRemoteException);
    }
  }
  
  private void sendMotionEventToNext(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
  {
    if (this.mNext != null)
    {
      this.mNext.onMotionEvent(paramMotionEvent1, paramMotionEvent2, paramInt);
      if (paramMotionEvent1.getActionMasked() == 0) {
        this.mOpenGesturesInProgress.put(paramMotionEvent1.getSource(), Boolean.valueOf(true));
      }
      if ((paramMotionEvent1.getActionMasked() == 1) || (paramMotionEvent1.getActionMasked() == 3)) {
        this.mOpenGesturesInProgress.put(paramMotionEvent1.getSource(), Boolean.valueOf(false));
      }
    }
  }
  
  public void clearEvents(int paramInt)
  {
    if (!this.mHandler.hasMessages(1)) {
      this.mOpenGesturesInProgress.put(paramInt, Boolean.valueOf(false));
    }
  }
  
  public void injectEvents(List<MotionEvent> paramList, IAccessibilityServiceClient paramIAccessibilityServiceClient, int paramInt)
  {
    SomeArgs localSomeArgs = SomeArgs.obtain();
    localSomeArgs.arg1 = paramList;
    localSomeArgs.arg2 = paramIAccessibilityServiceClient;
    localSomeArgs.argi1 = paramInt;
    this.mHandler.sendMessage(this.mHandler.obtainMessage(2, localSomeArgs));
  }
  
  public void onAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (this.mNext != null) {
      this.mNext.onAccessibilityEvent(paramAccessibilityEvent);
    }
  }
  
  public void onDestroy()
  {
    cancelAnyPendingInjectedEvents();
    this.mIsDestroyed = true;
  }
  
  public void onKeyEvent(KeyEvent paramKeyEvent, int paramInt)
  {
    if (this.mNext != null) {
      this.mNext.onKeyEvent(paramKeyEvent, paramInt);
    }
  }
  
  public void onMotionEvent(MotionEvent paramMotionEvent1, MotionEvent paramMotionEvent2, int paramInt)
  {
    cancelAnyPendingInjectedEvents();
    sendMotionEventToNext(paramMotionEvent1, paramMotionEvent2, paramInt);
  }
  
  public void setNext(EventStreamTransformation paramEventStreamTransformation)
  {
    this.mNext = paramEventStreamTransformation;
  }
  
  private class Callback
    implements Handler.Callback
  {
    private Callback() {}
    
    public boolean handleMessage(Message paramMessage)
    {
      if (paramMessage.what == 2)
      {
        paramMessage = (SomeArgs)paramMessage.obj;
        MotionEventInjector.-wrap0(MotionEventInjector.this, (List)paramMessage.arg1, (IAccessibilityServiceClient)paramMessage.arg2, paramMessage.argi1);
        paramMessage.recycle();
        return true;
      }
      if (paramMessage.what != 1) {
        throw new IllegalArgumentException("Unknown message: " + paramMessage.what);
      }
      paramMessage = (MotionEvent)paramMessage.obj;
      MotionEventInjector.-wrap2(MotionEventInjector.this, paramMessage, paramMessage, 1073741824);
      if (!MotionEventInjector.-get0(MotionEventInjector.this).hasMessages(1)) {
        MotionEventInjector.-wrap1(MotionEventInjector.this, true);
      }
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/MotionEventInjector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */