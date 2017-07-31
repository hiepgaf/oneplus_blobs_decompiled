package android.hardware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.MessageQueue;
import android.util.Log;
import android.util.SeempLog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import dalvik.system.CloseGuard;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class SystemSensorManager
  extends SensorManager
{
  private static boolean DEBUG_DYNAMIC_SENSOR = true;
  private static final int MAX_LISTENER_COUNT = 128;
  @GuardedBy("sLock")
  private static InjectEventQueue sInjectEventQueue = null;
  private static final Object sLock = new Object();
  @GuardedBy("sLock")
  private static boolean sNativeClassInited = false;
  private final Context mContext;
  private BroadcastReceiver mDynamicSensorBroadcastReceiver;
  private HashMap<SensorManager.DynamicSensorCallback, Handler> mDynamicSensorCallbacks = new HashMap();
  private boolean mDynamicSensorListDirty = true;
  private List<Sensor> mFullDynamicSensorsList = new ArrayList();
  private final ArrayList<Sensor> mFullSensorsList = new ArrayList();
  private final HashMap<Integer, Sensor> mHandleToSensor = new HashMap();
  private final Looper mMainLooper;
  private final long mNativeInstance;
  private final HashMap<SensorEventListener, SensorEventQueue> mSensorListeners = new HashMap();
  private final int mTargetSdkLevel;
  private final HashMap<TriggerEventListener, TriggerEventQueue> mTriggerListeners = new HashMap();
  
  public SystemSensorManager(Context paramContext, Looper paramLooper)
  {
    for (;;)
    {
      int i;
      synchronized (sLock)
      {
        if (!sNativeClassInited)
        {
          sNativeClassInited = true;
          nativeClassInit();
        }
        this.mMainLooper = paramLooper;
        this.mTargetSdkLevel = paramContext.getApplicationInfo().targetSdkVersion;
        this.mContext = paramContext;
        this.mNativeInstance = nativeCreate(paramContext.getOpPackageName());
        i = 0;
        paramContext = new Sensor();
        if (!nativeGetSensorAtIndex(this.mNativeInstance, paramContext, i)) {
          return;
        }
      }
      this.mFullSensorsList.add(paramContext);
      this.mHandleToSensor.put(Integer.valueOf(paramContext.getHandle()), paramContext);
      i += 1;
    }
  }
  
  private void cleanupSensorConnection(Sensor paramSensor)
  {
    this.mHandleToSensor.remove(Integer.valueOf(paramSensor.getHandle()));
    Iterator localIterator;
    Object localObject;
    if (paramSensor.getReportingMode() == 2) {
      synchronized (this.mTriggerListeners)
      {
        localIterator = this.mTriggerListeners.keySet().iterator();
        localObject = ???;
        if (localIterator.hasNext())
        {
          localObject = (TriggerEventListener)localIterator.next();
          if (DEBUG_DYNAMIC_SENSOR) {
            Log.i("SensorManager", "removed trigger listener" + ((TriggerEventListener)localObject).toString() + " due to sensor disconnection");
          }
          cancelTriggerSensorImpl((TriggerEventListener)localObject, paramSensor, true);
        }
      }
    }
    for (;;)
    {
      return;
      synchronized (this.mSensorListeners)
      {
        localIterator = this.mSensorListeners.keySet().iterator();
        localObject = ???;
        if (!localIterator.hasNext()) {
          continue;
        }
        localObject = (SensorEventListener)localIterator.next();
        if (DEBUG_DYNAMIC_SENSOR) {
          Log.i("SensorManager", "removed event listener" + localObject.toString() + " due to sensor disconnection");
        }
        unregisterListenerImpl((SensorEventListener)localObject, paramSensor);
      }
    }
  }
  
  private static boolean diffSortedSensorList(List<Sensor> paramList1, List<Sensor> paramList2, List<Sensor> paramList3, List<Sensor> paramList4, List<Sensor> paramList5)
  {
    boolean bool = false;
    int j = 0;
    int i = 0;
    for (;;)
    {
      if ((i < paramList1.size()) && ((j >= paramList2.size()) || (((Sensor)paramList2.get(j)).getHandle() > ((Sensor)paramList1.get(i)).getHandle())))
      {
        bool = true;
        if (paramList5 != null) {
          paramList5.add((Sensor)paramList1.get(i));
        }
        i += 1;
      }
      else if ((j < paramList2.size()) && ((i >= paramList1.size()) || (((Sensor)paramList2.get(j)).getHandle() < ((Sensor)paramList1.get(i)).getHandle())))
      {
        bool = true;
        if (paramList4 != null) {
          paramList4.add((Sensor)paramList2.get(j));
        }
        if (paramList3 != null) {
          paramList3.add((Sensor)paramList2.get(j));
        }
        j += 1;
      }
      else
      {
        if ((j >= paramList2.size()) || (i >= paramList1.size()) || (((Sensor)paramList2.get(j)).getHandle() != ((Sensor)paramList1.get(i)).getHandle())) {
          break;
        }
        if (paramList3 != null) {
          paramList3.add((Sensor)paramList1.get(i));
        }
        j += 1;
        i += 1;
      }
    }
    return bool;
  }
  
  private static native void nativeClassInit();
  
  private static native long nativeCreate(String paramString);
  
  private static native String nativeGetActiveSensorList(long paramLong);
  
  private static native void nativeGetDynamicSensors(long paramLong, List<Sensor> paramList);
  
  private static native boolean nativeGetSensorAtIndex(long paramLong, Sensor paramSensor, int paramInt);
  
  private static native boolean nativeIsDataInjectionEnabled(long paramLong);
  
  private void setupDynamicSensorBroadcastReceiver()
  {
    if (this.mDynamicSensorBroadcastReceiver == null)
    {
      this.mDynamicSensorBroadcastReceiver = new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          if (paramAnonymousIntent.getAction() == "android.intent.action.DYNAMIC_SENSOR_CHANGED")
          {
            if (SystemSensorManager.-get0()) {
              Log.i("SensorManager", "DYNS received DYNAMIC_SENSOR_CHANED broadcast");
            }
            SystemSensorManager.-set0(SystemSensorManager.this, true);
            SystemSensorManager.-wrap0(SystemSensorManager.this);
          }
        }
      };
      IntentFilter localIntentFilter = new IntentFilter("dynamic_sensor_change");
      localIntentFilter.addAction("android.intent.action.DYNAMIC_SENSOR_CHANGED");
      this.mContext.registerReceiver(this.mDynamicSensorBroadcastReceiver, localIntentFilter);
    }
  }
  
  private void teardownDynamicSensorBroadcastReceiver()
  {
    this.mDynamicSensorCallbacks.clear();
    this.mContext.unregisterReceiver(this.mDynamicSensorBroadcastReceiver);
    this.mDynamicSensorBroadcastReceiver = null;
  }
  
  private void updateDynamicSensorList()
  {
    final ArrayList localArrayList1;
    final ArrayList localArrayList2;
    synchronized (this.mFullDynamicSensorsList)
    {
      if (!this.mDynamicSensorListDirty) {
        break label288;
      }
      Object localObject1 = new ArrayList();
      nativeGetDynamicSensors(this.mNativeInstance, (List)localObject1);
      localObject4 = new ArrayList();
      localArrayList1 = new ArrayList();
      localArrayList2 = new ArrayList();
      if (!diffSortedSensorList(this.mFullDynamicSensorsList, (List)localObject1, (List)localObject4, localArrayList1, localArrayList2)) {
        break label283;
      }
      if (DEBUG_DYNAMIC_SENSOR) {
        Log.i("SensorManager", "DYNS dynamic sensor list cached should be updated");
      }
      this.mFullDynamicSensorsList = ((List)localObject4);
      localObject1 = localArrayList1.iterator();
      if (((Iterator)localObject1).hasNext())
      {
        localObject4 = (Sensor)((Iterator)localObject1).next();
        this.mHandleToSensor.put(Integer.valueOf(((Sensor)localObject4).getHandle()), localObject4);
      }
    }
    Object localObject4 = new Handler(this.mContext.getMainLooper());
    Iterator localIterator = this.mDynamicSensorCallbacks.entrySet().iterator();
    if (localIterator.hasNext())
    {
      localObject3 = (Map.Entry)localIterator.next();
      final SensorManager.DynamicSensorCallback localDynamicSensorCallback = (SensorManager.DynamicSensorCallback)((Map.Entry)localObject3).getKey();
      if (((Map.Entry)localObject3).getValue() == null) {}
      for (localObject3 = localObject4;; localObject3 = (Handler)((Map.Entry)localObject3).getValue())
      {
        ((Handler)localObject3).post(new Runnable()
        {
          public void run()
          {
            Iterator localIterator = localArrayList1.iterator();
            Sensor localSensor;
            while (localIterator.hasNext())
            {
              localSensor = (Sensor)localIterator.next();
              localDynamicSensorCallback.onDynamicSensorConnected(localSensor);
            }
            localIterator = localArrayList2.iterator();
            while (localIterator.hasNext())
            {
              localSensor = (Sensor)localIterator.next();
              localDynamicSensorCallback.onDynamicSensorDisconnected(localSensor);
            }
          }
        });
        break;
      }
    }
    Object localObject3 = localArrayList2.iterator();
    while (((Iterator)localObject3).hasNext()) {
      cleanupSensorConnection((Sensor)((Iterator)localObject3).next());
    }
    label283:
    this.mDynamicSensorListDirty = false;
    label288:
  }
  
  protected boolean cancelTriggerSensorImpl(TriggerEventListener paramTriggerEventListener, Sensor paramSensor, boolean paramBoolean)
  {
    if ((paramSensor != null) && (paramSensor.getReportingMode() != 2)) {
      return false;
    }
    synchronized (this.mTriggerListeners)
    {
      TriggerEventQueue localTriggerEventQueue = (TriggerEventQueue)this.mTriggerListeners.get(paramTriggerEventListener);
      if (localTriggerEventQueue != null)
      {
        if (paramSensor == null) {}
        for (paramBoolean = localTriggerEventQueue.removeAllSensors();; paramBoolean = localTriggerEventQueue.removeSensor(paramSensor, paramBoolean))
        {
          if (paramBoolean)
          {
            boolean bool = localTriggerEventQueue.hasSensors();
            if (!bool) {
              break;
            }
          }
          return paramBoolean;
        }
        this.mTriggerListeners.remove(paramTriggerEventListener);
        localTriggerEventQueue.dispose();
      }
    }
    return false;
  }
  
  protected boolean flushImpl(SensorEventListener paramSensorEventListener)
  {
    boolean bool = false;
    if (paramSensorEventListener == null) {
      throw new IllegalArgumentException("listener cannot be null");
    }
    synchronized (this.mSensorListeners)
    {
      paramSensorEventListener = (SensorEventQueue)this.mSensorListeners.get(paramSensorEventListener);
      if (paramSensorEventListener == null) {
        return false;
      }
      int i = paramSensorEventListener.flush();
      if (i == 0) {
        bool = true;
      }
      return bool;
    }
  }
  
  protected String getActiveSensorListImpl()
  {
    synchronized (sLock)
    {
      String str = nativeGetActiveSensorList(this.mNativeInstance);
      return str;
    }
  }
  
  protected List<Sensor> getFullDynamicSensorList()
  {
    setupDynamicSensorBroadcastReceiver();
    updateDynamicSensorList();
    return this.mFullDynamicSensorsList;
  }
  
  protected List<Sensor> getFullSensorList()
  {
    return this.mFullSensorsList;
  }
  
  protected boolean initDataInjectionImpl(boolean paramBoolean)
  {
    localObject1 = sLock;
    if (paramBoolean) {}
    for (;;)
    {
      try
      {
        if (!nativeIsDataInjectionEnabled(this.mNativeInstance))
        {
          Log.e("SensorManager", "Data Injection mode not enabled");
          return false;
        }
        if (sInjectEventQueue == null) {
          sInjectEventQueue = new InjectEventQueue(this.mMainLooper, this, this.mContext.getPackageName());
        }
        return true;
      }
      finally {}
      if (sInjectEventQueue != null)
      {
        sInjectEventQueue.dispose();
        sInjectEventQueue = null;
      }
    }
  }
  
  protected boolean injectSensorDataImpl(Sensor paramSensor, float[] paramArrayOfFloat, int paramInt, long paramLong)
  {
    synchronized (sLock)
    {
      if (sInjectEventQueue == null)
      {
        Log.e("SensorManager", "Data injection mode not activated before calling injectSensorData");
        return false;
      }
      paramInt = sInjectEventQueue.injectSensorData(paramSensor.getHandle(), paramArrayOfFloat, paramInt, paramLong);
      if (paramInt != 0)
      {
        sInjectEventQueue.dispose();
        sInjectEventQueue = null;
      }
      if (paramInt == 0)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  protected void registerDynamicSensorCallbackImpl(SensorManager.DynamicSensorCallback paramDynamicSensorCallback, Handler paramHandler)
  {
    if (DEBUG_DYNAMIC_SENSOR) {
      Log.i("SensorManager", "DYNS Register dynamic sensor callback");
    }
    if (paramDynamicSensorCallback == null) {
      throw new IllegalArgumentException("callback cannot be null");
    }
    if (this.mDynamicSensorCallbacks.containsKey(paramDynamicSensorCallback)) {
      return;
    }
    setupDynamicSensorBroadcastReceiver();
    this.mDynamicSensorCallbacks.put(paramDynamicSensorCallback, paramHandler);
  }
  
  protected boolean registerListenerImpl(SensorEventListener paramSensorEventListener, Sensor paramSensor, int paramInt1, Handler paramHandler, int paramInt2, int paramInt3)
  {
    SeempLog.record_sensor_rate(381, paramSensor, paramInt1);
    if ((paramSensorEventListener == null) || (paramSensor == null))
    {
      Log.e("SensorManager", "sensor or listener is null");
      return false;
    }
    if (paramSensor.getReportingMode() == 2)
    {
      Log.e("SensorManager", "Trigger Sensors should use the requestTriggerSensor.");
      return false;
    }
    if ((paramInt2 < 0) || (paramInt1 < 0))
    {
      Log.e("SensorManager", "maxBatchReportLatencyUs and delayUs should be non-negative");
      return false;
    }
    if (this.mSensorListeners.size() >= 128) {
      throw new IllegalStateException("register failed, the sensor listeners size has exceeded the maximum limit 128");
    }
    synchronized (this.mSensorListeners)
    {
      Object localObject = (SensorEventQueue)this.mSensorListeners.get(paramSensorEventListener);
      if (localObject == null)
      {
        if (paramHandler != null)
        {
          paramHandler = paramHandler.getLooper();
          if (paramSensorEventListener.getClass().getEnclosingClass() == null) {
            break label198;
          }
        }
        label198:
        for (localObject = paramSensorEventListener.getClass().getEnclosingClass().getName();; localObject = paramSensorEventListener.getClass().getName())
        {
          paramHandler = new SensorEventQueue(paramSensorEventListener, paramHandler, this, (String)localObject);
          if (paramHandler.addSensor(paramSensor, paramInt1, paramInt2)) {
            break label210;
          }
          paramHandler.dispose();
          return false;
          paramHandler = this.mMainLooper;
          break;
        }
        label210:
        this.mSensorListeners.put(paramSensorEventListener, paramHandler);
        return true;
      }
      boolean bool = ((SensorEventQueue)localObject).addSensor(paramSensor, paramInt1, paramInt2);
      return bool;
    }
  }
  
  protected boolean requestTriggerSensorImpl(TriggerEventListener paramTriggerEventListener, Sensor paramSensor)
  {
    if (paramSensor == null) {
      throw new IllegalArgumentException("sensor cannot be null");
    }
    if (paramTriggerEventListener == null) {
      throw new IllegalArgumentException("listener cannot be null");
    }
    if (paramSensor.getReportingMode() != 2) {
      return false;
    }
    if (this.mTriggerListeners.size() >= 128) {
      throw new IllegalStateException("request failed, the trigger listeners size has exceeded the maximum limit 128");
    }
    synchronized (this.mTriggerListeners)
    {
      Object localObject = (TriggerEventQueue)this.mTriggerListeners.get(paramTriggerEventListener);
      if (localObject == null)
      {
        if (paramTriggerEventListener.getClass().getEnclosingClass() != null) {}
        for (localObject = paramTriggerEventListener.getClass().getEnclosingClass().getName();; localObject = paramTriggerEventListener.getClass().getName())
        {
          localObject = new TriggerEventQueue(paramTriggerEventListener, this.mMainLooper, this, (String)localObject);
          if (((TriggerEventQueue)localObject).addSensor(paramSensor, 0, 0)) {
            break;
          }
          ((TriggerEventQueue)localObject).dispose();
          return false;
        }
        this.mTriggerListeners.put(paramTriggerEventListener, localObject);
        return true;
      }
      boolean bool = ((TriggerEventQueue)localObject).addSensor(paramSensor, 0, 0);
      return bool;
    }
  }
  
  protected void unregisterDynamicSensorCallbackImpl(SensorManager.DynamicSensorCallback paramDynamicSensorCallback)
  {
    if (DEBUG_DYNAMIC_SENSOR) {
      Log.i("SensorManager", "Removing dynamic sensor listerner");
    }
    this.mDynamicSensorCallbacks.remove(paramDynamicSensorCallback);
  }
  
  protected void unregisterListenerImpl(SensorEventListener paramSensorEventListener, Sensor paramSensor)
  {
    SeempLog.record_sensor(382, paramSensor);
    if ((paramSensor != null) && (paramSensor.getReportingMode() == 2)) {
      return;
    }
    synchronized (this.mSensorListeners)
    {
      SensorEventQueue localSensorEventQueue = (SensorEventQueue)this.mSensorListeners.get(paramSensorEventListener);
      if (localSensorEventQueue != null) {
        if (paramSensor != null) {
          break label76;
        }
      }
      label76:
      for (boolean bool = localSensorEventQueue.removeAllSensors();; bool = localSensorEventQueue.removeSensor(paramSensor, true))
      {
        if (bool)
        {
          bool = localSensorEventQueue.hasSensors();
          if (!bool) {
            break;
          }
        }
        return;
      }
      this.mSensorListeners.remove(paramSensorEventListener);
      localSensorEventQueue.dispose();
    }
  }
  
  private static abstract class BaseEventQueue
  {
    protected static final int OPERATING_MODE_DATA_INJECTION = 1;
    protected static final int OPERATING_MODE_NORMAL = 0;
    private final SparseBooleanArray mActiveSensors = new SparseBooleanArray();
    private final CloseGuard mCloseGuard = CloseGuard.get();
    protected final SystemSensorManager mManager;
    protected final SparseIntArray mSensorAccuracies = new SparseIntArray();
    private long nSensorEventQueue;
    
    BaseEventQueue(Looper paramLooper, SystemSensorManager paramSystemSensorManager, int paramInt, String paramString)
    {
      String str = paramString;
      if (paramString == null) {
        str = "";
      }
      this.nSensorEventQueue = nativeInitBaseEventQueue(SystemSensorManager.-get3(paramSystemSensorManager), new WeakReference(this), paramLooper.getQueue(), str, paramInt, SystemSensorManager.-get1(paramSystemSensorManager).getOpPackageName());
      this.mCloseGuard.open("dispose");
      this.mManager = paramSystemSensorManager;
    }
    
    private int disableSensor(Sensor paramSensor)
    {
      if (this.nSensorEventQueue == 0L) {
        throw new NullPointerException();
      }
      if (paramSensor == null) {
        throw new NullPointerException();
      }
      return nativeDisableSensor(this.nSensorEventQueue, paramSensor.getHandle());
    }
    
    private void dispose(boolean paramBoolean)
    {
      if (this.mCloseGuard != null)
      {
        if (paramBoolean) {
          this.mCloseGuard.warnIfOpen();
        }
        this.mCloseGuard.close();
      }
      if (this.nSensorEventQueue != 0L)
      {
        nativeDestroySensorEventQueue(this.nSensorEventQueue);
        this.nSensorEventQueue = 0L;
      }
    }
    
    private int enableSensor(Sensor paramSensor, int paramInt1, int paramInt2)
    {
      if (this.nSensorEventQueue == 0L) {
        throw new NullPointerException();
      }
      if (paramSensor == null) {
        throw new NullPointerException();
      }
      return nativeEnableSensor(this.nSensorEventQueue, paramSensor.getHandle(), paramInt1, paramInt2);
    }
    
    private static native void nativeDestroySensorEventQueue(long paramLong);
    
    private static native int nativeDisableSensor(long paramLong, int paramInt);
    
    private static native int nativeEnableSensor(long paramLong, int paramInt1, int paramInt2, int paramInt3);
    
    private static native int nativeFlushSensor(long paramLong);
    
    private static native long nativeInitBaseEventQueue(long paramLong, WeakReference<BaseEventQueue> paramWeakReference, MessageQueue paramMessageQueue, String paramString1, int paramInt, String paramString2);
    
    private static native int nativeInjectSensorData(long paramLong1, int paramInt1, float[] paramArrayOfFloat, int paramInt2, long paramLong2);
    
    public boolean addSensor(Sensor paramSensor, int paramInt1, int paramInt2)
    {
      int i = paramSensor.getHandle();
      if (this.mActiveSensors.get(i)) {
        return false;
      }
      this.mActiveSensors.put(i, true);
      addSensorEvent(paramSensor);
      if ((enableSensor(paramSensor, paramInt1, paramInt2) != 0) && ((paramInt2 == 0) || ((paramInt2 > 0) && (enableSensor(paramSensor, paramInt1, 0) != 0))))
      {
        removeSensor(paramSensor, false);
        return false;
      }
      return true;
    }
    
    protected abstract void addSensorEvent(Sensor paramSensor);
    
    protected void dispatchAdditionalInfoEvent(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat, int[] paramArrayOfInt) {}
    
    protected abstract void dispatchFlushCompleteEvent(int paramInt);
    
    protected abstract void dispatchSensorEvent(int paramInt1, float[] paramArrayOfFloat, int paramInt2, long paramLong);
    
    public void dispose()
    {
      dispose(false);
    }
    
    protected void finalize()
      throws Throwable
    {
      try
      {
        dispose(true);
        return;
      }
      finally
      {
        super.finalize();
      }
    }
    
    public int flush()
    {
      if (this.nSensorEventQueue == 0L) {
        throw new NullPointerException();
      }
      return nativeFlushSensor(this.nSensorEventQueue);
    }
    
    public boolean hasSensors()
    {
      return this.mActiveSensors.indexOfValue(true) >= 0;
    }
    
    protected int injectSensorDataBase(int paramInt1, float[] paramArrayOfFloat, int paramInt2, long paramLong)
    {
      return nativeInjectSensorData(this.nSensorEventQueue, paramInt1, paramArrayOfFloat, paramInt2, paramLong);
    }
    
    public boolean removeAllSensors()
    {
      int i = 0;
      while (i < this.mActiveSensors.size())
      {
        if (this.mActiveSensors.valueAt(i))
        {
          int j = this.mActiveSensors.keyAt(i);
          Sensor localSensor = (Sensor)SystemSensorManager.-get2(this.mManager).get(Integer.valueOf(j));
          if (localSensor != null)
          {
            disableSensor(localSensor);
            this.mActiveSensors.put(j, false);
            removeSensorEvent(localSensor);
          }
        }
        i += 1;
      }
      return true;
    }
    
    public boolean removeSensor(Sensor paramSensor, boolean paramBoolean)
    {
      int i = paramSensor.getHandle();
      if (this.mActiveSensors.get(i))
      {
        if (paramBoolean) {
          disableSensor(paramSensor);
        }
        this.mActiveSensors.put(paramSensor.getHandle(), false);
        removeSensorEvent(paramSensor);
        return true;
      }
      return false;
    }
    
    protected abstract void removeSensorEvent(Sensor paramSensor);
  }
  
  final class InjectEventQueue
    extends SystemSensorManager.BaseEventQueue
  {
    public InjectEventQueue(Looper paramLooper, SystemSensorManager paramSystemSensorManager, String paramString)
    {
      super(paramSystemSensorManager, 1, paramString);
    }
    
    protected void addSensorEvent(Sensor paramSensor) {}
    
    protected void dispatchFlushCompleteEvent(int paramInt) {}
    
    protected void dispatchSensorEvent(int paramInt1, float[] paramArrayOfFloat, int paramInt2, long paramLong) {}
    
    int injectSensorData(int paramInt1, float[] paramArrayOfFloat, int paramInt2, long paramLong)
    {
      return injectSensorDataBase(paramInt1, paramArrayOfFloat, paramInt2, paramLong);
    }
    
    protected void removeSensorEvent(Sensor paramSensor) {}
  }
  
  static final class SensorEventQueue
    extends SystemSensorManager.BaseEventQueue
  {
    private final SensorEventListener mListener;
    private final SparseArray<SensorEvent> mSensorsEvents = new SparseArray();
    
    public SensorEventQueue(SensorEventListener paramSensorEventListener, Looper paramLooper, SystemSensorManager paramSystemSensorManager, String paramString)
    {
      super(paramSystemSensorManager, 0, paramString);
      this.mListener = paramSensorEventListener;
    }
    
    public void addSensorEvent(Sensor paramSensor)
    {
      SensorEvent localSensorEvent = new SensorEvent(Sensor.getMaxLengthValuesArray(paramSensor, SystemSensorManager.-get4(this.mManager)));
      synchronized (this.mSensorsEvents)
      {
        this.mSensorsEvents.put(paramSensor.getHandle(), localSensorEvent);
        return;
      }
    }
    
    protected void dispatchAdditionalInfoEvent(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat, int[] paramArrayOfInt)
    {
      if ((this.mListener instanceof SensorEventCallback))
      {
        Sensor localSensor = (Sensor)SystemSensorManager.-get2(this.mManager).get(Integer.valueOf(paramInt1));
        if (localSensor == null) {
          return;
        }
        paramArrayOfFloat = new SensorAdditionalInfo(localSensor, paramInt2, paramInt3, paramArrayOfInt, paramArrayOfFloat);
        ((SensorEventCallback)this.mListener).onSensorAdditionalInfo(paramArrayOfFloat);
      }
    }
    
    protected void dispatchFlushCompleteEvent(int paramInt)
    {
      if ((this.mListener instanceof SensorEventListener2))
      {
        Sensor localSensor = (Sensor)SystemSensorManager.-get2(this.mManager).get(Integer.valueOf(paramInt));
        if (localSensor == null) {
          return;
        }
        ((SensorEventListener2)this.mListener).onFlushCompleted(localSensor);
      }
    }
    
    protected void dispatchSensorEvent(int paramInt1, float[] paramArrayOfFloat, int paramInt2, long paramLong)
    {
      Sensor localSensor = (Sensor)SystemSensorManager.-get2(this.mManager).get(Integer.valueOf(paramInt1));
      if (localSensor == null) {
        return;
      }
      SensorEvent localSensorEvent;
      synchronized (this.mSensorsEvents)
      {
        localSensorEvent = (SensorEvent)this.mSensorsEvents.get(paramInt1);
        if (localSensorEvent == null) {
          return;
        }
      }
      System.arraycopy(paramArrayOfFloat, 0, localSensorEvent.values, 0, localSensorEvent.values.length);
      localSensorEvent.timestamp = paramLong;
      localSensorEvent.accuracy = paramInt2;
      localSensorEvent.sensor = localSensor;
      paramInt2 = this.mSensorAccuracies.get(paramInt1);
      if ((localSensorEvent.accuracy >= 0) && (paramInt2 != localSensorEvent.accuracy))
      {
        this.mSensorAccuracies.put(paramInt1, localSensorEvent.accuracy);
        this.mListener.onAccuracyChanged(localSensorEvent.sensor, localSensorEvent.accuracy);
      }
      this.mListener.onSensorChanged(localSensorEvent);
    }
    
    public void removeSensorEvent(Sensor paramSensor)
    {
      synchronized (this.mSensorsEvents)
      {
        this.mSensorsEvents.delete(paramSensor.getHandle());
        return;
      }
    }
  }
  
  static final class TriggerEventQueue
    extends SystemSensorManager.BaseEventQueue
  {
    private final TriggerEventListener mListener;
    private final SparseArray<TriggerEvent> mTriggerEvents = new SparseArray();
    
    public TriggerEventQueue(TriggerEventListener paramTriggerEventListener, Looper paramLooper, SystemSensorManager paramSystemSensorManager, String paramString)
    {
      super(paramSystemSensorManager, 0, paramString);
      this.mListener = paramTriggerEventListener;
    }
    
    public void addSensorEvent(Sensor paramSensor)
    {
      TriggerEvent localTriggerEvent = new TriggerEvent(Sensor.getMaxLengthValuesArray(paramSensor, SystemSensorManager.-get4(this.mManager)));
      synchronized (this.mTriggerEvents)
      {
        this.mTriggerEvents.put(paramSensor.getHandle(), localTriggerEvent);
        return;
      }
    }
    
    protected void dispatchFlushCompleteEvent(int paramInt) {}
    
    protected void dispatchSensorEvent(int paramInt1, float[] paramArrayOfFloat, int paramInt2, long paramLong)
    {
      Sensor localSensor = (Sensor)SystemSensorManager.-get2(this.mManager).get(Integer.valueOf(paramInt1));
      if (localSensor == null) {
        return;
      }
      TriggerEvent localTriggerEvent;
      synchronized (this.mTriggerEvents)
      {
        localTriggerEvent = (TriggerEvent)this.mTriggerEvents.get(paramInt1);
        if (localTriggerEvent == null)
        {
          Log.e("SensorManager", "Error: Trigger Event is null for Sensor: " + localSensor);
          return;
        }
      }
      System.arraycopy(paramArrayOfFloat, 0, localTriggerEvent.values, 0, localTriggerEvent.values.length);
      localTriggerEvent.timestamp = paramLong;
      localTriggerEvent.sensor = localSensor;
      this.mManager.cancelTriggerSensorImpl(this.mListener, localSensor, false);
      this.mListener.onTrigger(localTriggerEvent);
    }
    
    public void removeSensorEvent(Sensor paramSensor)
    {
      synchronized (this.mTriggerEvents)
      {
        this.mTriggerEvents.delete(paramSensor.getHandle());
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/SystemSensorManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */