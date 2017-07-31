package com.android.server.wm;

import android.os.Handler;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.MotionEvent;
import android.view.WindowManagerPolicy.PointerEventListener;
import com.android.server.UiThread;
import java.util.ArrayList;

public class PointerEventDispatcher
  extends InputEventReceiver
{
  ArrayList<WindowManagerPolicy.PointerEventListener> mListeners = new ArrayList();
  WindowManagerPolicy.PointerEventListener[] mListenersArray = new WindowManagerPolicy.PointerEventListener[0];
  
  public PointerEventDispatcher(InputChannel paramInputChannel)
  {
    super(paramInputChannel, UiThread.getHandler().getLooper());
  }
  
  public void onInputEvent(InputEvent paramInputEvent)
  {
    try
    {
      if (((paramInputEvent instanceof MotionEvent)) && ((paramInputEvent.getSource() & 0x2) != 0))
      {
        MotionEvent localMotionEvent = (MotionEvent)paramInputEvent;
        synchronized (this.mListeners)
        {
          if (this.mListenersArray == null)
          {
            this.mListenersArray = new WindowManagerPolicy.PointerEventListener[this.mListeners.size()];
            this.mListeners.toArray(this.mListenersArray);
          }
          WindowManagerPolicy.PointerEventListener[] arrayOfPointerEventListener = this.mListenersArray;
          int i = 0;
          if (i < arrayOfPointerEventListener.length)
          {
            arrayOfPointerEventListener[i].onPointerEvent(localMotionEvent);
            i += 1;
          }
        }
      }
    }
    finally
    {
      finishInputEvent(paramInputEvent, false);
    }
  }
  
  public void registerInputEventListener(WindowManagerPolicy.PointerEventListener paramPointerEventListener)
  {
    synchronized (this.mListeners)
    {
      if (this.mListeners.contains(paramPointerEventListener)) {
        throw new IllegalStateException("registerInputEventListener: trying to register" + paramPointerEventListener + " twice.");
      }
    }
    this.mListeners.add(paramPointerEventListener);
    this.mListenersArray = null;
  }
  
  public void unregisterInputEventListener(WindowManagerPolicy.PointerEventListener paramPointerEventListener)
  {
    synchronized (this.mListeners)
    {
      if (!this.mListeners.contains(paramPointerEventListener)) {
        throw new IllegalStateException("registerInputEventListener: " + paramPointerEventListener + " not registered.");
      }
    }
    this.mListeners.remove(paramPointerEventListener);
    this.mListenersArray = null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/PointerEventDispatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */