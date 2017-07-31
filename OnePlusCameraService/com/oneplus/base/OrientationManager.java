package com.oneplus.base;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.Settings.System;
import android.view.OrientationEventListener;
import java.util.ArrayList;
import java.util.List;

public final class OrientationManager
{
  private static final int MSG_ORIENTATION_CHANGED = 10000;
  private static final int MSG_ROTATION_CHANGED = 10001;
  private static final int MSG_SYSTEM_ORIENTATION_SETTINGS_CHANGED = 10002;
  private static final boolean SUPPORT_SYSTEM_ORIENTATION_SETTINGS = false;
  private static final String TAG = "OrientationManager";
  private static ContentObserver m_AccRotationObserver;
  private static final List<CallbackHandle> m_CallbackHandles = new ArrayList();
  private static volatile Context m_Context;
  private static Boolean m_IsAccRotationEnabled = Boolean.valueOf(true);
  private static boolean m_IsSensorStarted;
  private static Handler m_MainHandler;
  private static OrientationEventListener m_OrientationListener;
  private static final Runnable m_RegisterAccRotationRunnable = new Runnable()
  {
    public void run() {}
  };
  private static volatile Rotation m_Rotation;
  private static final List<Handle> m_SensorRequestHandles = new ArrayList();
  private static volatile HandlerThread m_SensorThread;
  private static Handler m_SensorThreadHandler;
  private static final Runnable m_StartSensorRunnable = new Runnable()
  {
    public void run() {}
  };
  private static final Runnable m_StopSensorRunnable = new Runnable()
  {
    public void run() {}
  };
  private static final Runnable m_UnregisterAccRotationRunnable = new Runnable()
  {
    public void run() {}
  };
  
  public static Rotation getRotation()
  {
    return m_Rotation;
  }
  
  public static boolean isSystemOrientationEnabled()
  {
    return m_IsAccRotationEnabled.booleanValue();
  }
  
  private static void onAccRotationSettingsChanged(Boolean paramBoolean)
  {
    if (m_IsAccRotationEnabled == paramBoolean) {
      return;
    }
    Log.v("OrientationManager", "onAccRotationSettingsChanged() - Enabled: ", paramBoolean);
    m_IsAccRotationEnabled = paramBoolean;
    for (;;)
    {
      int i;
      synchronized (m_CallbackHandles)
      {
        i = m_CallbackHandles.size() - 1;
        if (i >= 0)
        {
          CallbackHandler localCallbackHandler = ((CallbackHandle)m_CallbackHandles.get(i)).handler;
          if (localCallbackHandler != null) {
            Message.obtain(localCallbackHandler, 10002, 0, 0, paramBoolean).sendToTarget();
          } else {
            ((CallbackHandle)m_CallbackHandles.get(i)).callback.onSystemOrientationSettingsChanged(paramBoolean.booleanValue());
          }
        }
      }
      return;
      i -= 1;
    }
  }
  
  private static void onAccRotationSettingsChangedInternal()
  {
    if (Settings.System.getInt(m_Context.getContentResolver(), "accelerometer_rotation", 0) == 1) {}
    for (boolean bool = true;; bool = false)
    {
      m_MainHandler.post(new Runnable()
      {
        public void run()
        {
          OrientationManager.-wrap1(Boolean.valueOf(this.val$enabled));
        }
      });
      return;
    }
  }
  
  private static void onOrientationChanged(int paramInt)
  {
    for (;;)
    {
      int i;
      synchronized (m_CallbackHandles)
      {
        i = m_CallbackHandles.size() - 1;
        if (i >= 0)
        {
          CallbackHandler localCallbackHandler = ((CallbackHandle)m_CallbackHandles.get(i)).handler;
          if (localCallbackHandler != null) {
            Message.obtain(localCallbackHandler, 10000, paramInt, 0).sendToTarget();
          } else {
            ((CallbackHandle)m_CallbackHandles.get(i)).callback.onOrientationChanged(paramInt);
          }
        }
      }
      if ((paramInt == -1) && (m_Rotation != null))
      {
        Log.w("OrientationManager", "onOrientationChanged() - Ignore unknown orientation");
        return;
      }
      if (m_Rotation != null)
      {
        int j = paramInt - m_Rotation.getDeviceOrientation();
        if (j > 180) {
          i = 360 - j;
        }
        while (Math.abs(i) <= 70)
        {
          return;
          i = j;
          if (j < 65356) {
            i = j + 360;
          }
        }
      }
      ??? = m_Rotation;
      m_Rotation = Rotation.fromDeviceOrientation(paramInt);
      if (??? == m_Rotation) {
        return;
      }
      onRotationChanged((Rotation)???, m_Rotation);
      return;
      i -= 1;
    }
  }
  
  private static void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    Log.v("OrientationManager", "onRotationChanged() - ", paramRotation1, " -> ", paramRotation2);
    for (;;)
    {
      int i;
      synchronized (m_CallbackHandles)
      {
        i = m_CallbackHandles.size() - 1;
        if (i >= 0)
        {
          CallbackHandler localCallbackHandler = ((CallbackHandle)m_CallbackHandles.get(i)).handler;
          if (localCallbackHandler != null) {
            Message.obtain(localCallbackHandler, 10001, 0, 0, new Object[] { paramRotation1, paramRotation2 }).sendToTarget();
          } else {
            ((CallbackHandle)m_CallbackHandles.get(i)).callback.onRotationChanged(paramRotation1, paramRotation2);
          }
        }
      }
      return;
      i -= 1;
    }
  }
  
  private static void registerAccRotationInternal()
  {
    Log.v("OrientationManager", "registerAutoRotationInternal()");
    m_Context.getContentResolver().registerContentObserver(Settings.System.getUriFor("accelerometer_rotation"), true, m_AccRotationObserver);
    onAccRotationSettingsChangedInternal();
  }
  
  private static void removeCallback(CallbackHandle paramCallbackHandle)
  {
    synchronized (m_CallbackHandles)
    {
      m_CallbackHandles.remove(paramCallbackHandle);
      return;
    }
  }
  
  public static Handle setCallback(Callback paramCallback, Handler paramHandler)
  {
    if (paramCallback == null)
    {
      Log.e("OrientationManager", "setCallback() - No call-back");
      return null;
    }
    synchronized (m_CallbackHandles)
    {
      paramCallback = new CallbackHandle(paramCallback, paramHandler);
      m_CallbackHandles.add(paramCallback);
      return paramCallback;
    }
  }
  
  public static Handle startOrientationSensor(Context paramContext)
  {
    if (paramContext == null)
    {
      Log.e("OrientationManager", "startOrientationSensor() - No context");
      return null;
    }
    synchronized (m_SensorRequestHandles)
    {
      Handle local6 = new Handle("RequestOrientationSensor")
      {
        protected void onClose(int paramAnonymousInt)
        {
          OrientationManager.-wrap7(this);
        }
      };
      m_SensorRequestHandles.add(local6);
      if (m_SensorRequestHandles.size() == 1)
      {
        if (m_MainHandler == null) {
          m_MainHandler = new Handler(paramContext.getMainLooper());
        }
        if (m_SensorThread == null)
        {
          m_SensorThread = new HandlerThread("Orientation sensor thread");
          Log.v("OrientationManager", "startOrientationSensor() - Start sensor thread");
          m_SensorThread.start();
          m_SensorThreadHandler = new Handler(m_SensorThread.getLooper());
          Log.v("OrientationManager", "startOrientationSensor() - Sensor thread started");
          m_AccRotationObserver = new ContentObserver(m_SensorThreadHandler)
          {
            public void onChange(boolean paramAnonymousBoolean) {}
          };
        }
        m_Context = paramContext.getApplicationContext();
        m_SensorThreadHandler.removeCallbacks(m_StopSensorRunnable);
        m_SensorThreadHandler.post(m_StartSensorRunnable);
      }
      return local6;
    }
  }
  
  private static void startOrientationSensorInternal()
  {
    if (!m_IsSensorStarted)
    {
      if (m_OrientationListener == null) {
        m_OrientationListener = new OrientationEventListener(m_Context)
        {
          public void onOrientationChanged(int paramAnonymousInt)
          {
            OrientationManager.-wrap2(paramAnonymousInt);
          }
        };
      }
      Log.v("OrientationManager", "startOrientationSensorInternal()");
      m_OrientationListener.enable();
      m_IsSensorStarted = true;
    }
  }
  
  private static void stopOrientationSensor(Handle paramHandle)
  {
    synchronized (m_SensorRequestHandles)
    {
      if ((m_SensorRequestHandles.remove(paramHandle)) && (m_SensorRequestHandles.isEmpty())) {
        m_SensorThreadHandler.post(m_StopSensorRunnable);
      }
      return;
    }
  }
  
  private static void stopOrientationSensorInternal()
  {
    if ((m_OrientationListener != null) && (m_IsSensorStarted))
    {
      Log.v("OrientationManager", "stopOrientationSensorInternal()");
      m_IsSensorStarted = false;
      m_OrientationListener.disable();
    }
  }
  
  private static void unregisterAccRotationInternal()
  {
    Log.v("OrientationManager", "unregisterAccRotationInternal()");
    m_Context.getContentResolver().unregisterContentObserver(m_AccRotationObserver);
  }
  
  public static abstract class Callback
  {
    public void onOrientationChanged(int paramInt) {}
    
    public abstract void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2);
    
    public void onSystemOrientationSettingsChanged(boolean paramBoolean) {}
  }
  
  private static final class CallbackHandle
    extends Handle
  {
    public final OrientationManager.Callback callback;
    public final OrientationManager.CallbackHandler handler;
    
    public CallbackHandle(OrientationManager.Callback paramCallback, Handler paramHandler)
    {
      super();
      this.callback = paramCallback;
      if (paramHandler != null) {
        localCallbackHandler = new OrientationManager.CallbackHandler(paramCallback, paramHandler);
      }
      this.handler = localCallbackHandler;
    }
    
    protected void onClose(int paramInt)
    {
      OrientationManager.-wrap4(this);
    }
  }
  
  private static final class CallbackHandler
    extends Handler
  {
    public final OrientationManager.Callback callback;
    
    public CallbackHandler(OrientationManager.Callback paramCallback, Handler paramHandler)
    {
      super();
      this.callback = paramCallback;
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      case 10000: 
        this.callback.onOrientationChanged(paramMessage.arg1);
        return;
      case 10001: 
        paramMessage = (Object[])paramMessage.obj;
        this.callback.onRotationChanged((Rotation)paramMessage[0], (Rotation)paramMessage[1]);
        return;
      }
      this.callback.onSystemOrientationSettingsChanged(((Boolean)paramMessage.obj).booleanValue());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/OrientationManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */