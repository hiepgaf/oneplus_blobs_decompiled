package android.hardware.camera2.impl;

import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.ICameraDeviceCallbacks.Stub;
import android.hardware.camera2.ICameraDeviceUser;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.InputConfiguration;
import android.hardware.camera2.params.OutputConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.utils.SubmitInfo;
import android.hardware.camera2.utils.SurfaceUtils;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.util.Log;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraDeviceImpl
  extends CameraDevice
  implements IBinder.DeathRecipient
{
  private static final int REQUEST_ID_NONE = -1;
  private final boolean DEBUG = false;
  private final String TAG;
  private final Runnable mCallOnActive = new Runnable()
  {
    public void run()
    {
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        Object localObject2 = CameraDeviceImpl.-get10(CameraDeviceImpl.this);
        if (localObject2 == null) {
          return;
        }
        localObject2 = CameraDeviceImpl.-get12(CameraDeviceImpl.this);
        if (localObject2 != null) {
          ((CameraDeviceImpl.StateCallbackKK)localObject2).onActive(CameraDeviceImpl.this);
        }
        return;
      }
    }
  };
  private final Runnable mCallOnBusy = new Runnable()
  {
    public void run()
    {
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        Object localObject2 = CameraDeviceImpl.-get10(CameraDeviceImpl.this);
        if (localObject2 == null) {
          return;
        }
        localObject2 = CameraDeviceImpl.-get12(CameraDeviceImpl.this);
        if (localObject2 != null) {
          ((CameraDeviceImpl.StateCallbackKK)localObject2).onBusy(CameraDeviceImpl.this);
        }
        return;
      }
    }
  };
  private final Runnable mCallOnClosed = new Runnable()
  {
    private boolean mClosedOnce = false;
    
    public void run()
    {
      if (this.mClosedOnce) {
        throw new AssertionError("Don't post #onClosed more than once");
      }
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        CameraDeviceImpl.StateCallbackKK localStateCallbackKK = CameraDeviceImpl.-get12(CameraDeviceImpl.this);
        if (localStateCallbackKK != null) {
          localStateCallbackKK.onClosed(CameraDeviceImpl.this);
        }
        CameraDeviceImpl.-get6(CameraDeviceImpl.this).onClosed(CameraDeviceImpl.this);
        this.mClosedOnce = true;
        return;
      }
    }
  };
  private final Runnable mCallOnDisconnected = new Runnable()
  {
    public void run()
    {
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        Object localObject2 = CameraDeviceImpl.-get10(CameraDeviceImpl.this);
        if (localObject2 == null) {
          return;
        }
        localObject2 = CameraDeviceImpl.-get12(CameraDeviceImpl.this);
        if (localObject2 != null) {
          ((CameraDeviceImpl.StateCallbackKK)localObject2).onDisconnected(CameraDeviceImpl.this);
        }
        CameraDeviceImpl.-get6(CameraDeviceImpl.this).onDisconnected(CameraDeviceImpl.this);
        return;
      }
    }
  };
  private final Runnable mCallOnIdle = new Runnable()
  {
    public void run()
    {
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        Object localObject2 = CameraDeviceImpl.-get10(CameraDeviceImpl.this);
        if (localObject2 == null) {
          return;
        }
        localObject2 = CameraDeviceImpl.-get12(CameraDeviceImpl.this);
        if (localObject2 != null) {
          ((CameraDeviceImpl.StateCallbackKK)localObject2).onIdle(CameraDeviceImpl.this);
        }
        return;
      }
    }
  };
  private final Runnable mCallOnOpened = new Runnable()
  {
    public void run()
    {
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        Object localObject2 = CameraDeviceImpl.-get10(CameraDeviceImpl.this);
        if (localObject2 == null) {
          return;
        }
        localObject2 = CameraDeviceImpl.-get12(CameraDeviceImpl.this);
        if (localObject2 != null) {
          ((CameraDeviceImpl.StateCallbackKK)localObject2).onOpened(CameraDeviceImpl.this);
        }
        CameraDeviceImpl.-get6(CameraDeviceImpl.this).onOpened(CameraDeviceImpl.this);
        return;
      }
    }
  };
  private final Runnable mCallOnUnconfigured = new Runnable()
  {
    public void run()
    {
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        Object localObject2 = CameraDeviceImpl.-get10(CameraDeviceImpl.this);
        if (localObject2 == null) {
          return;
        }
        localObject2 = CameraDeviceImpl.-get12(CameraDeviceImpl.this);
        if (localObject2 != null) {
          ((CameraDeviceImpl.StateCallbackKK)localObject2).onUnconfigured(CameraDeviceImpl.this);
        }
        return;
      }
    }
  };
  private final CameraDeviceCallbacks mCallbacks = new CameraDeviceCallbacks();
  private final String mCameraId;
  private final SparseArray<CaptureCallbackHolder> mCaptureCallbackMap = new SparseArray();
  private final CameraCharacteristics mCharacteristics;
  private final AtomicBoolean mClosing = new AtomicBoolean();
  private AbstractMap.SimpleEntry<Integer, InputConfiguration> mConfiguredInput = new AbstractMap.SimpleEntry(Integer.valueOf(-1), null);
  private final SparseArray<OutputConfiguration> mConfiguredOutputs = new SparseArray();
  private CameraCaptureSessionCore mCurrentSession;
  private final CameraDevice.StateCallback mDeviceCallback;
  private final Handler mDeviceHandler;
  private final FrameNumberTracker mFrameNumberTracker = new FrameNumberTracker();
  private boolean mIdle = true;
  private boolean mInError = false;
  final Object mInterfaceLock = new Object();
  private int mNextSessionId = 0;
  private ICameraDeviceUserWrapper mRemoteDevice;
  private int mRepeatingRequestId = -1;
  private final List<RequestLastFrameNumbersHolder> mRequestLastFrameNumbersList = new ArrayList();
  private volatile StateCallbackKK mSessionStateCallback;
  private final int mTotalPartialCount;
  
  public CameraDeviceImpl(String paramString, CameraDevice.StateCallback paramStateCallback, Handler paramHandler, CameraCharacteristics paramCameraCharacteristics)
  {
    if ((paramString == null) || (paramStateCallback == null)) {}
    while ((paramHandler == null) || (paramCameraCharacteristics == null)) {
      throw new IllegalArgumentException("Null argument given");
    }
    this.mCameraId = paramString;
    this.mDeviceCallback = paramStateCallback;
    this.mDeviceHandler = paramHandler;
    this.mCharacteristics = paramCameraCharacteristics;
    paramStateCallback = String.format("CameraDevice-JV-%s", new Object[] { this.mCameraId });
    paramString = paramStateCallback;
    if (paramStateCallback.length() > 23) {
      paramString = paramStateCallback.substring(0, 23);
    }
    this.TAG = paramString;
    paramString = (Integer)this.mCharacteristics.get(CameraCharacteristics.REQUEST_PARTIAL_RESULT_COUNT);
    if (paramString == null)
    {
      this.mTotalPartialCount = 1;
      return;
    }
    this.mTotalPartialCount = paramString.intValue();
  }
  
  private void checkAndFireSequenceComplete()
  {
    long l1 = this.mFrameNumberTracker.getCompletedFrameNumber();
    long l2 = this.mFrameNumberTracker.getCompletedReprocessFrameNumber();
    Iterator localIterator = this.mRequestLastFrameNumbersList.iterator();
    while (localIterator.hasNext())
    {
      final RequestLastFrameNumbersHolder localRequestLastFrameNumbersHolder = (RequestLastFrameNumbersHolder)localIterator.next();
      int j = 0;
      final int k = localRequestLastFrameNumbersHolder.getRequestId();
      synchronized (this.mInterfaceLock)
      {
        if (this.mRemoteDevice == null)
        {
          Log.w(this.TAG, "Camera closed while checking sequences");
          return;
        }
        int m = this.mCaptureCallbackMap.indexOfKey(k);
        final CaptureCallbackHolder localCaptureCallbackHolder;
        if (m >= 0)
        {
          localCaptureCallbackHolder = (CaptureCallbackHolder)this.mCaptureCallbackMap.valueAt(m);
          int i = j;
          if (localCaptureCallbackHolder != null)
          {
            long l3 = localRequestLastFrameNumbersHolder.getLastRegularFrameNumber();
            long l4 = localRequestLastFrameNumbersHolder.getLastReprocessFrameNumber();
            i = j;
            if (l3 <= l1)
            {
              i = j;
              if (l4 <= l2)
              {
                i = 1;
                this.mCaptureCallbackMap.removeAt(m);
              }
            }
          }
          if ((localCaptureCallbackHolder == null) || (i != 0)) {
            localIterator.remove();
          }
          if (i != 0)
          {
            ??? = new Runnable()
            {
              public void run()
              {
                if (!CameraDeviceImpl.-wrap1(CameraDeviceImpl.this)) {
                  localCaptureCallbackHolder.getCallback().onCaptureSequenceCompleted(CameraDeviceImpl.this, k, localRequestLastFrameNumbersHolder.getLastFrameNumber());
                }
              }
            };
            localCaptureCallbackHolder.getHandler().post((Runnable)???);
          }
        }
        else
        {
          localCaptureCallbackHolder = null;
        }
      }
    }
  }
  
  private void checkEarlyTriggerSequenceComplete(final int paramInt, long paramLong)
  {
    final CaptureCallbackHolder localCaptureCallbackHolder = null;
    if (paramLong == -1L)
    {
      int i = this.mCaptureCallbackMap.indexOfKey(paramInt);
      if (i >= 0) {
        localCaptureCallbackHolder = (CaptureCallbackHolder)this.mCaptureCallbackMap.valueAt(i);
      }
      if (localCaptureCallbackHolder != null) {
        this.mCaptureCallbackMap.removeAt(i);
      }
      if (localCaptureCallbackHolder != null)
      {
        Runnable local9 = new Runnable()
        {
          public void run()
          {
            if (!CameraDeviceImpl.-wrap1(CameraDeviceImpl.this)) {
              localCaptureCallbackHolder.getCallback().onCaptureSequenceAborted(CameraDeviceImpl.this, paramInt);
            }
          }
        };
        localCaptureCallbackHolder.getHandler().post(local9);
        return;
      }
      Log.w(this.TAG, String.format("did not register callback to request %d", new Object[] { Integer.valueOf(paramInt) }));
      return;
    }
    this.mRequestLastFrameNumbersList.add(new RequestLastFrameNumbersHolder(paramInt, paramLong));
    checkAndFireSequenceComplete();
  }
  
  static Handler checkHandler(Handler paramHandler)
  {
    Handler localHandler = paramHandler;
    if (paramHandler == null)
    {
      paramHandler = Looper.myLooper();
      if (paramHandler == null) {
        throw new IllegalArgumentException("No handler given, and current thread has no looper!");
      }
      localHandler = new Handler(paramHandler);
    }
    return localHandler;
  }
  
  static <T> Handler checkHandler(Handler paramHandler, T paramT)
  {
    if (paramT != null) {
      return checkHandler(paramHandler);
    }
    return paramHandler;
  }
  
  private void checkIfCameraClosedOrInError()
    throws CameraAccessException
  {
    if (this.mRemoteDevice == null) {
      throw new IllegalStateException("CameraDevice was already closed");
    }
    if (this.mInError) {
      throw new CameraAccessException(3, "The camera device has encountered a serious error");
    }
  }
  
  private void checkInputConfiguration(InputConfiguration paramInputConfiguration)
  {
    int k = 0;
    if (paramInputConfiguration != null)
    {
      Object localObject = (StreamConfigurationMap)this.mCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
      int[] arrayOfInt = ((StreamConfigurationMap)localObject).getInputFormats();
      int j = 0;
      int m = arrayOfInt.length;
      int i = 0;
      while (i < m)
      {
        if (arrayOfInt[i] == paramInputConfiguration.getFormat()) {
          j = 1;
        }
        i += 1;
      }
      if (j == 0) {
        throw new IllegalArgumentException("input format " + paramInputConfiguration.getFormat() + " is not valid");
      }
      j = 0;
      localObject = ((StreamConfigurationMap)localObject).getInputSizes(paramInputConfiguration.getFormat());
      m = localObject.length;
      i = k;
      while (i < m)
      {
        arrayOfInt = localObject[i];
        k = j;
        if (paramInputConfiguration.getWidth() == arrayOfInt.getWidth())
        {
          k = j;
          if (paramInputConfiguration.getHeight() == arrayOfInt.getHeight()) {
            k = 1;
          }
        }
        i += 1;
        j = k;
      }
      if (j == 0) {
        throw new IllegalArgumentException("input size " + paramInputConfiguration.getWidth() + "x" + paramInputConfiguration.getHeight() + " is not valid");
      }
    }
  }
  
  private void createCaptureSessionInternal(InputConfiguration paramInputConfiguration, List<OutputConfiguration> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler, boolean paramBoolean)
    throws CameraAccessException
  {
    synchronized (this.mInterfaceLock)
    {
      checkIfCameraClosedOrInError();
      if ((paramBoolean) && (paramInputConfiguration != null)) {
        throw new IllegalArgumentException("Constrained high speed session doesn't support input configuration yet.");
      }
    }
    if (this.mCurrentSession != null) {
      this.mCurrentSession.replaceSessionClose();
    }
    Object localObject3 = null;
    Object localObject4 = null;
    try
    {
      boolean bool2 = configureStreamsChecked(paramInputConfiguration, paramList, paramBoolean);
      bool1 = bool2;
      localObject1 = localObject4;
      localObject2 = localObject3;
      if (bool2)
      {
        bool1 = bool2;
        localObject1 = localObject4;
        localObject2 = localObject3;
        if (paramInputConfiguration != null)
        {
          localObject1 = this.mRemoteDevice.getInputSurface();
          localObject2 = localObject3;
          bool1 = bool2;
        }
      }
    }
    catch (CameraAccessException localCameraAccessException)
    {
      for (;;)
      {
        Object localObject2;
        int i;
        boolean bool1 = false;
        Object localObject1 = null;
      }
    }
    paramInputConfiguration = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      paramInputConfiguration.add(((OutputConfiguration)paramList.next()).getSurface());
    }
    if (paramBoolean)
    {
      i = this.mNextSessionId;
      this.mNextSessionId = (i + 1);
    }
    for (paramInputConfiguration = new CameraConstrainedHighSpeedCaptureSessionImpl(i, paramInputConfiguration, paramStateCallback, paramHandler, this, this.mDeviceHandler, bool1, this.mCharacteristics);; paramInputConfiguration = new CameraCaptureSessionImpl(i, (Surface)localObject1, paramInputConfiguration, paramStateCallback, paramHandler, this, this.mDeviceHandler, bool1))
    {
      this.mCurrentSession = paramInputConfiguration;
      if (localObject2 == null) {
        break;
      }
      throw ((Throwable)localObject2);
      i = this.mNextSessionId;
      this.mNextSessionId = (i + 1);
    }
    this.mSessionStateCallback = this.mCurrentSession.getDeviceStateCallback();
  }
  
  private CameraCharacteristics getCharacteristics()
  {
    return this.mCharacteristics;
  }
  
  private boolean isClosed()
  {
    return this.mClosing.get();
  }
  
  private int submitCaptureRequest(List<CaptureRequest> paramList, CaptureCallback paramCaptureCallback, Handler paramHandler, boolean paramBoolean)
    throws CameraAccessException
  {
    paramHandler = checkHandler(paramHandler, paramCaptureCallback);
    ??? = paramList.iterator();
    Object localObject2;
    do
    {
      while (!((Iterator)localObject2).hasNext())
      {
        if (!((Iterator)???).hasNext()) {
          break;
        }
        localObject2 = (CaptureRequest)((Iterator)???).next();
        if (((CaptureRequest)localObject2).getTargets().isEmpty()) {
          throw new IllegalArgumentException("Each request must have at least one Surface target");
        }
        localObject2 = ((CaptureRequest)localObject2).getTargets().iterator();
      }
    } while ((Surface)((Iterator)localObject2).next() != null);
    throw new IllegalArgumentException("Null Surface targets are not allowed");
    synchronized (this.mInterfaceLock)
    {
      checkIfCameraClosedOrInError();
      if (paramBoolean) {
        stopRepeating();
      }
      localObject2 = (CaptureRequest[])paramList.toArray(new CaptureRequest[paramList.size()]);
      localObject2 = this.mRemoteDevice.submitRequestList((CaptureRequest[])localObject2, paramBoolean);
      if (paramCaptureCallback != null) {
        this.mCaptureCallbackMap.put(((SubmitInfo)localObject2).getRequestId(), new CaptureCallbackHolder(paramCaptureCallback, paramList, paramHandler, paramBoolean, this.mNextSessionId - 1));
      }
      if (paramBoolean)
      {
        if (this.mRepeatingRequestId != -1) {
          checkEarlyTriggerSequenceComplete(this.mRepeatingRequestId, ((SubmitInfo)localObject2).getLastFrameNumber());
        }
        this.mRepeatingRequestId = ((SubmitInfo)localObject2).getRequestId();
        if (this.mIdle) {
          this.mDeviceHandler.post(this.mCallOnActive);
        }
        this.mIdle = false;
        int i = ((SubmitInfo)localObject2).getRequestId();
        return i;
      }
      this.mRequestLastFrameNumbersList.add(new RequestLastFrameNumbersHolder(paramList, (SubmitInfo)localObject2));
    }
  }
  
  private void waitUntilIdle()
    throws CameraAccessException
  {
    synchronized (this.mInterfaceLock)
    {
      checkIfCameraClosedOrInError();
      if (this.mRepeatingRequestId != -1) {
        throw new IllegalStateException("Active repeating request ongoing");
      }
    }
    this.mRemoteDevice.waitUntilIdle();
  }
  
  public void binderDied()
  {
    Log.w(this.TAG, "CameraDevice " + this.mCameraId + " died unexpectedly");
    if (this.mRemoteDevice == null) {
      return;
    }
    this.mInError = true;
    Runnable local11 = new Runnable()
    {
      public void run()
      {
        if (!CameraDeviceImpl.-wrap1(CameraDeviceImpl.this)) {
          CameraDeviceImpl.-get6(CameraDeviceImpl.this).onError(CameraDeviceImpl.this, 5);
        }
      }
    };
    this.mDeviceHandler.post(local11);
  }
  
  public int capture(CaptureRequest paramCaptureRequest, CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(paramCaptureRequest);
    return submitCaptureRequest(localArrayList, paramCaptureCallback, paramHandler, false);
  }
  
  public int captureBurst(List<CaptureRequest> paramList, CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    if ((paramList == null) || (paramList.isEmpty())) {
      throw new IllegalArgumentException("At least one request must be given");
    }
    return submitCaptureRequest(paramList, paramCaptureCallback, paramHandler, false);
  }
  
  public void close()
  {
    synchronized (this.mInterfaceLock)
    {
      boolean bool = this.mClosing.getAndSet(true);
      if (bool) {
        return;
      }
      if (this.mRemoteDevice != null)
      {
        this.mRemoteDevice.disconnect();
        this.mRemoteDevice.unlinkToDeath(this, 0);
      }
      if ((this.mRemoteDevice != null) || (this.mInError)) {
        this.mDeviceHandler.post(this.mCallOnClosed);
      }
      this.mRemoteDevice = null;
      return;
    }
  }
  
  public void configureOutputs(List<Surface> paramList)
    throws CameraAccessException
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      localArrayList.add(new OutputConfiguration((Surface)paramList.next()));
    }
    configureStreamsChecked(null, localArrayList, false);
  }
  
  public boolean configureStreamsChecked(InputConfiguration paramInputConfiguration, List<OutputConfiguration> arg2, boolean paramBoolean)
    throws CameraAccessException
  {
    Object localObject1 = ???;
    if (??? == null) {
      localObject1 = new ArrayList();
    }
    if ((((List)localObject1).size() == 0) && (paramInputConfiguration != null)) {
      throw new IllegalArgumentException("cannot configure an input stream without any output streams");
    }
    checkInputConfiguration(paramInputConfiguration);
    for (;;)
    {
      HashSet localHashSet;
      Object localObject2;
      int i;
      Object localObject3;
      synchronized (this.mInterfaceLock)
      {
        checkIfCameraClosedOrInError();
        localHashSet = new HashSet((Collection)localObject1);
        localObject2 = new ArrayList();
        i = 0;
        if (i < this.mConfiguredOutputs.size())
        {
          int j = this.mConfiguredOutputs.keyAt(i);
          localObject3 = (OutputConfiguration)this.mConfiguredOutputs.valueAt(i);
          if ((!((List)localObject1).contains(localObject3)) || (((OutputConfiguration)localObject3).isDeferredConfiguration())) {
            ((List)localObject2).add(Integer.valueOf(j));
          } else {
            localHashSet.remove(localObject3);
          }
        }
      }
      this.mDeviceHandler.post(this.mCallOnBusy);
      stopRepeating();
      try
      {
        waitUntilIdle();
        this.mRemoteDevice.beginConfigure();
        localObject3 = (InputConfiguration)this.mConfiguredInput.getValue();
        if (paramInputConfiguration != localObject3) {
          if ((paramInputConfiguration == null) || (!paramInputConfiguration.equals(localObject3))) {
            break label344;
          }
        }
      }
      catch (IllegalArgumentException paramInputConfiguration)
      {
        for (;;)
        {
          Log.w(this.TAG, "Stream configuration failed due to: " + paramInputConfiguration.getMessage());
          if ((0 == 0) || (((List)localObject1).size() <= 0)) {
            break;
          }
          this.mDeviceHandler.post(this.mCallOnIdle);
          return false;
          if (localObject3 != null)
          {
            this.mRemoteDevice.deleteStream(((Integer)this.mConfiguredInput.getKey()).intValue());
            this.mConfiguredInput = new AbstractMap.SimpleEntry(Integer.valueOf(-1), null);
          }
          if (paramInputConfiguration != null) {
            this.mConfiguredInput = new AbstractMap.SimpleEntry(Integer.valueOf(this.mRemoteDevice.createInputStream(paramInputConfiguration.getWidth(), paramInputConfiguration.getHeight(), paramInputConfiguration.getFormat())), paramInputConfiguration);
          }
        }
      }
      catch (CameraAccessException paramInputConfiguration)
      {
        if (paramInputConfiguration.getReason() != 4) {
          break label592;
        }
        throw new IllegalStateException("The camera is currently busy. You must wait until the previous operation completes.", paramInputConfiguration);
      }
      finally
      {
        if (0 == 0) {
          break label609;
        }
      }
      paramInputConfiguration = ((Iterable)localObject2).iterator();
      while (paramInputConfiguration.hasNext())
      {
        localObject2 = (Integer)paramInputConfiguration.next();
        this.mRemoteDevice.deleteStream(((Integer)localObject2).intValue());
        this.mConfiguredOutputs.delete(((Integer)localObject2).intValue());
        continue;
        label344:
        if (((List)localObject1).size() <= 0) {
          break label609;
        }
        this.mDeviceHandler.post(this.mCallOnIdle);
      }
      for (;;)
      {
        throw paramInputConfiguration;
        paramInputConfiguration = ((Iterable)localObject1).iterator();
        while (paramInputConfiguration.hasNext())
        {
          localObject2 = (OutputConfiguration)paramInputConfiguration.next();
          if (localHashSet.contains(localObject2))
          {
            i = this.mRemoteDevice.createStream((OutputConfiguration)localObject2);
            this.mConfiguredOutputs.put(i, localObject2);
          }
        }
        this.mRemoteDevice.endConfigure(paramBoolean);
        if ((1 != 0) && (((List)localObject1).size() > 0)) {
          this.mDeviceHandler.post(this.mCallOnIdle);
        }
        for (;;)
        {
          return true;
          this.mDeviceHandler.post(this.mCallOnUnconfigured);
        }
        label592:
        throw paramInputConfiguration;
        this.mDeviceHandler.post(this.mCallOnUnconfigured);
        break;
        label609:
        this.mDeviceHandler.post(this.mCallOnUnconfigured);
      }
      i += 1;
    }
  }
  
  public CaptureRequest.Builder createCaptureRequest(int paramInt)
    throws CameraAccessException
  {
    synchronized (this.mInterfaceLock)
    {
      checkIfCameraClosedOrInError();
      CaptureRequest.Builder localBuilder = new CaptureRequest.Builder(this.mRemoteDevice.createDefaultRequest(paramInt), false, -1);
      return localBuilder;
    }
  }
  
  public void createCaptureSession(List<Surface> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler)
    throws CameraAccessException
  {
    ArrayList localArrayList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      localArrayList.add(new OutputConfiguration((Surface)paramList.next()));
    }
    createCaptureSessionInternal(null, localArrayList, paramStateCallback, paramHandler, false);
  }
  
  public void createCaptureSessionByOutputConfigurations(List<OutputConfiguration> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler)
    throws CameraAccessException
  {
    createCaptureSessionInternal(null, new ArrayList(paramList), paramStateCallback, paramHandler, false);
  }
  
  public void createConstrainedHighSpeedCaptureSession(List<Surface> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler)
    throws CameraAccessException
  {
    if ((paramList == null) || (paramList.size() == 0)) {}
    while (paramList.size() > 2) {
      throw new IllegalArgumentException("Output surface list must not be null and the size must be no more than 2");
    }
    SurfaceUtils.checkConstrainedHighSpeedSurfaces(paramList, null, (StreamConfigurationMap)getCharacteristics().get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP));
    ArrayList localArrayList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      localArrayList.add(new OutputConfiguration((Surface)paramList.next()));
    }
    createCaptureSessionInternal(null, localArrayList, paramStateCallback, paramHandler, true);
  }
  
  public CaptureRequest.Builder createReprocessCaptureRequest(TotalCaptureResult paramTotalCaptureResult)
    throws CameraAccessException
  {
    synchronized (this.mInterfaceLock)
    {
      checkIfCameraClosedOrInError();
      paramTotalCaptureResult = new CaptureRequest.Builder(new CameraMetadataNative(paramTotalCaptureResult.getNativeCopy()), true, paramTotalCaptureResult.getSessionId());
      return paramTotalCaptureResult;
    }
  }
  
  public void createReprocessableCaptureSession(InputConfiguration paramInputConfiguration, List<Surface> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler)
    throws CameraAccessException
  {
    if (paramInputConfiguration == null) {
      throw new IllegalArgumentException("inputConfig cannot be null when creating a reprocessable capture session");
    }
    ArrayList localArrayList = new ArrayList(paramList.size());
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      localArrayList.add(new OutputConfiguration((Surface)paramList.next()));
    }
    createCaptureSessionInternal(paramInputConfiguration, localArrayList, paramStateCallback, paramHandler, false);
  }
  
  public void createReprocessableCaptureSessionByConfigurations(InputConfiguration paramInputConfiguration, List<OutputConfiguration> paramList, CameraCaptureSession.StateCallback paramStateCallback, Handler paramHandler)
    throws CameraAccessException
  {
    if (paramInputConfiguration == null) {
      throw new IllegalArgumentException("inputConfig cannot be null when creating a reprocessable capture session");
    }
    if (paramList == null) {
      throw new IllegalArgumentException("Output configurations cannot be null when creating a reprocessable capture session");
    }
    ArrayList localArrayList = new ArrayList();
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      localArrayList.add(new OutputConfiguration((OutputConfiguration)paramList.next()));
    }
    createCaptureSessionInternal(paramInputConfiguration, localArrayList, paramStateCallback, paramHandler, false);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void finishDeferredConfig(List<OutputConfiguration> paramList)
    throws CameraAccessException
  {
    if ((paramList == null) || (paramList.size() == 0)) {
      throw new IllegalArgumentException("deferred config is null or empty");
    }
    for (;;)
    {
      OutputConfiguration localOutputConfiguration;
      int i;
      int j;
      synchronized (this.mInterfaceLock)
      {
        paramList = paramList.iterator();
        if (!paramList.hasNext()) {
          break;
        }
        localOutputConfiguration = (OutputConfiguration)paramList.next();
        int k = -1;
        i = 0;
        j = k;
        if (i < this.mConfiguredOutputs.size())
        {
          if (localOutputConfiguration.equals(this.mConfiguredOutputs.valueAt(i))) {
            j = this.mConfiguredOutputs.keyAt(i);
          }
        }
        else
        {
          if (j != -1) {
            break label133;
          }
          throw new IllegalArgumentException("Deferred config is not part of this session");
        }
      }
      i += 1;
      continue;
      label133:
      if (localOutputConfiguration.getSurface() == null) {
        throw new IllegalArgumentException("The deferred config for stream " + j + " must have a non-null surface");
      }
      this.mRemoteDevice.setDeferredConfiguration(j, localOutputConfiguration);
    }
  }
  
  public void flush()
    throws CameraAccessException
  {
    synchronized (this.mInterfaceLock)
    {
      checkIfCameraClosedOrInError();
      this.mDeviceHandler.post(this.mCallOnBusy);
      if (this.mIdle)
      {
        this.mDeviceHandler.post(this.mCallOnIdle);
        return;
      }
      long l = this.mRemoteDevice.flush();
      if (this.mRepeatingRequestId != -1)
      {
        checkEarlyTriggerSequenceComplete(this.mRepeatingRequestId, l);
        this.mRepeatingRequestId = -1;
      }
      return;
    }
  }
  
  public CameraDeviceCallbacks getCallbacks()
  {
    return this.mCallbacks;
  }
  
  public String getId()
  {
    return this.mCameraId;
  }
  
  public void prepare(int paramInt, Surface paramSurface)
    throws CameraAccessException
  {
    if (paramSurface == null) {
      throw new IllegalArgumentException("Surface is null");
    }
    if (paramInt <= 0) {
      throw new IllegalArgumentException("Invalid maxCount given: " + paramInt);
    }
    Object localObject = this.mInterfaceLock;
    int k = -1;
    int i = 0;
    int j;
    for (;;)
    {
      j = k;
      try
      {
        if (i < this.mConfiguredOutputs.size())
        {
          if (paramSurface == ((OutputConfiguration)this.mConfiguredOutputs.valueAt(i)).getSurface()) {
            j = this.mConfiguredOutputs.keyAt(i);
          }
        }
        else
        {
          if (j != -1) {
            break;
          }
          throw new IllegalArgumentException("Surface is not part of this session");
        }
      }
      finally {}
      i += 1;
    }
    this.mRemoteDevice.prepare2(paramInt, j);
  }
  
  public void prepare(Surface paramSurface)
    throws CameraAccessException
  {
    if (paramSurface == null) {
      throw new IllegalArgumentException("Surface is null");
    }
    Object localObject = this.mInterfaceLock;
    int k = -1;
    int i = 0;
    int j;
    for (;;)
    {
      j = k;
      try
      {
        if (i < this.mConfiguredOutputs.size())
        {
          if (paramSurface == ((OutputConfiguration)this.mConfiguredOutputs.valueAt(i)).getSurface()) {
            j = this.mConfiguredOutputs.keyAt(i);
          }
        }
        else
        {
          if (j != -1) {
            break;
          }
          throw new IllegalArgumentException("Surface is not part of this session");
        }
      }
      finally {}
      i += 1;
    }
    this.mRemoteDevice.prepare(j);
  }
  
  public void setRemoteDevice(ICameraDeviceUser paramICameraDeviceUser)
    throws CameraAccessException
  {
    synchronized (this.mInterfaceLock)
    {
      boolean bool = this.mInError;
      if (bool) {
        return;
      }
      this.mRemoteDevice = new ICameraDeviceUserWrapper(paramICameraDeviceUser);
      paramICameraDeviceUser = paramICameraDeviceUser.asBinder();
      if (paramICameraDeviceUser != null) {}
      try
      {
        paramICameraDeviceUser.linkToDeath(this, 0);
        this.mDeviceHandler.post(this.mCallOnOpened);
        this.mDeviceHandler.post(this.mCallOnUnconfigured);
        return;
      }
      catch (RemoteException paramICameraDeviceUser)
      {
        this.mDeviceHandler.post(this.mCallOnDisconnected);
        throw new CameraAccessException(2, "The camera device has encountered a serious error");
      }
    }
  }
  
  public void setRemoteFailure(ServiceSpecificException arg1)
  {
    final int i = 4;
    final boolean bool = true;
    switch (???.errorCode)
    {
    case 5: 
    case 9: 
    default: 
      Log.e(this.TAG, "Unexpected failure in opening camera device: " + ???.errorCode + ???.getMessage());
    }
    synchronized (this.mInterfaceLock)
    {
      for (;;)
      {
        this.mInError = true;
        this.mDeviceHandler.post(new Runnable()
        {
          public void run()
          {
            if (bool)
            {
              CameraDeviceImpl.-get6(CameraDeviceImpl.this).onError(CameraDeviceImpl.this, i);
              return;
            }
            CameraDeviceImpl.-get6(CameraDeviceImpl.this).onDisconnected(CameraDeviceImpl.this);
          }
        });
        return;
        i = 1;
        continue;
        i = 2;
        continue;
        i = 3;
        continue;
        bool = false;
      }
      i = 4;
    }
  }
  
  public int setRepeatingBurst(List<CaptureRequest> paramList, CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    if ((paramList == null) || (paramList.isEmpty())) {
      throw new IllegalArgumentException("At least one request must be given");
    }
    return submitCaptureRequest(paramList, paramCaptureCallback, paramHandler, true);
  }
  
  public int setRepeatingRequest(CaptureRequest paramCaptureRequest, CaptureCallback paramCaptureCallback, Handler paramHandler)
    throws CameraAccessException
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(paramCaptureRequest);
    return submitCaptureRequest(localArrayList, paramCaptureCallback, paramHandler, true);
  }
  
  public void setSessionListener(StateCallbackKK paramStateCallbackKK)
  {
    synchronized (this.mInterfaceLock)
    {
      this.mSessionStateCallback = paramStateCallbackKK;
      return;
    }
  }
  
  public void stopRepeating()
    throws CameraAccessException
  {
    synchronized (this.mInterfaceLock)
    {
      checkIfCameraClosedOrInError();
      int i;
      if (this.mRepeatingRequestId != -1)
      {
        i = this.mRepeatingRequestId;
        this.mRepeatingRequestId = -1;
      }
      try
      {
        long l = this.mRemoteDevice.cancelRequest(i);
        checkEarlyTriggerSequenceComplete(i, l);
        return;
      }
      catch (IllegalArgumentException localIllegalArgumentException) {}
    }
  }
  
  public void tearDown(Surface paramSurface)
    throws CameraAccessException
  {
    if (paramSurface == null) {
      throw new IllegalArgumentException("Surface is null");
    }
    Object localObject = this.mInterfaceLock;
    int k = -1;
    int i = 0;
    int j;
    for (;;)
    {
      j = k;
      try
      {
        if (i < this.mConfiguredOutputs.size())
        {
          if (paramSurface == ((OutputConfiguration)this.mConfiguredOutputs.valueAt(i)).getSurface()) {
            j = this.mConfiguredOutputs.keyAt(i);
          }
        }
        else
        {
          if (j != -1) {
            break;
          }
          throw new IllegalArgumentException("Surface is not part of this session");
        }
      }
      finally {}
      i += 1;
    }
    this.mRemoteDevice.tearDown(j);
  }
  
  public class CameraDeviceCallbacks
    extends ICameraDeviceCallbacks.Stub
  {
    public CameraDeviceCallbacks() {}
    
    private void onCaptureErrorLocked(int paramInt, CaptureResultExtras paramCaptureResultExtras)
    {
      int i = paramCaptureResultExtras.getRequestId();
      int j = paramCaptureResultExtras.getSubsequenceId();
      final long l = paramCaptureResultExtras.getFrameNumber();
      final CameraDeviceImpl.CaptureCallbackHolder localCaptureCallbackHolder = (CameraDeviceImpl.CaptureCallbackHolder)CameraDeviceImpl.-get3(CameraDeviceImpl.this).get(i);
      final CaptureRequest localCaptureRequest = localCaptureCallbackHolder.getRequest(j);
      if (paramInt == 5)
      {
        paramCaptureResultExtras = new Runnable()
        {
          public void run()
          {
            if (!CameraDeviceImpl.-wrap1(CameraDeviceImpl.this)) {
              localCaptureCallbackHolder.getCallback().onCaptureBufferLost(CameraDeviceImpl.this, localCaptureRequest, this.val$outputSurface, l);
            }
          }
        };
        localCaptureCallbackHolder.getHandler().post(paramCaptureResultExtras);
        return;
      }
      boolean bool;
      if (paramInt == 4)
      {
        bool = true;
        label101:
        if ((CameraDeviceImpl.-get5(CameraDeviceImpl.this) == null) || (!CameraDeviceImpl.-get5(CameraDeviceImpl.this).isAborting())) {
          break label190;
        }
      }
      label190:
      for (paramInt = 1;; paramInt = 0)
      {
        paramCaptureResultExtras = new Runnable()
        {
          public void run()
          {
            if (!CameraDeviceImpl.-wrap1(CameraDeviceImpl.this)) {
              localCaptureCallbackHolder.getCallback().onCaptureFailed(CameraDeviceImpl.this, localCaptureRequest, this.val$failure);
            }
          }
        };
        CameraDeviceImpl.-get8(CameraDeviceImpl.this).updateTracker(l, true, localCaptureRequest.isReprocess());
        CameraDeviceImpl.-wrap2(CameraDeviceImpl.this);
        break;
        bool = false;
        break label101;
      }
    }
    
    public IBinder asBinder()
    {
      return this;
    }
    
    public void onCaptureStarted(final CaptureResultExtras paramCaptureResultExtras, final long paramLong)
    {
      int i = paramCaptureResultExtras.getRequestId();
      long l = paramCaptureResultExtras.getFrameNumber();
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        final Object localObject2 = CameraDeviceImpl.-get10(CameraDeviceImpl.this);
        if (localObject2 == null) {
          return;
        }
        localObject2 = (CameraDeviceImpl.CaptureCallbackHolder)CameraDeviceImpl.-get3(CameraDeviceImpl.this).get(i);
        if (localObject2 == null) {
          return;
        }
        boolean bool = CameraDeviceImpl.-wrap1(CameraDeviceImpl.this);
        if (bool) {
          return;
        }
        ((CameraDeviceImpl.CaptureCallbackHolder)localObject2).getHandler().post(new Runnable()
        {
          public void run()
          {
            if (!CameraDeviceImpl.-wrap1(CameraDeviceImpl.this)) {
              localObject2.getCallback().onCaptureStarted(CameraDeviceImpl.this, localObject2.getRequest(paramCaptureResultExtras.getSubsequenceId()), paramLong, this.val$frameNumber);
            }
          }
        });
        return;
      }
    }
    
    public void onDeviceError(final int paramInt, CaptureResultExtras paramCaptureResultExtras)
    {
      for (;;)
      {
        synchronized (CameraDeviceImpl.this.mInterfaceLock)
        {
          ICameraDeviceUserWrapper localICameraDeviceUserWrapper = CameraDeviceImpl.-get10(CameraDeviceImpl.this);
          if (localICameraDeviceUserWrapper == null) {
            return;
          }
          switch (paramInt)
          {
          default: 
            Log.e(CameraDeviceImpl.-get0(CameraDeviceImpl.this), "Unknown error from camera device: " + paramInt);
          case 1: 
          case 2: 
            CameraDeviceImpl.-set1(CameraDeviceImpl.this, true);
            if (paramInt == 1)
            {
              paramInt = 4;
              paramCaptureResultExtras = new Runnable()
              {
                public void run()
                {
                  if (!CameraDeviceImpl.-wrap1(CameraDeviceImpl.this)) {
                    CameraDeviceImpl.-get6(CameraDeviceImpl.this).onError(CameraDeviceImpl.this, paramInt);
                  }
                }
              };
              CameraDeviceImpl.-get7(CameraDeviceImpl.this).post(paramCaptureResultExtras);
              return;
            }
            break;
          case 0: 
            CameraDeviceImpl.-get7(CameraDeviceImpl.this).post(CameraDeviceImpl.-get1(CameraDeviceImpl.this));
          }
        }
        paramInt = 5;
        continue;
        onCaptureErrorLocked(paramInt, paramCaptureResultExtras);
      }
    }
    
    public void onDeviceIdle()
    {
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        ICameraDeviceUserWrapper localICameraDeviceUserWrapper = CameraDeviceImpl.-get10(CameraDeviceImpl.this);
        if (localICameraDeviceUserWrapper == null) {
          return;
        }
        if (!CameraDeviceImpl.-get9(CameraDeviceImpl.this)) {
          CameraDeviceImpl.-get7(CameraDeviceImpl.this).post(CameraDeviceImpl.-get2(CameraDeviceImpl.this));
        }
        CameraDeviceImpl.-set0(CameraDeviceImpl.this, true);
        return;
      }
    }
    
    public void onPrepared(int paramInt)
    {
      CameraDeviceImpl.StateCallbackKK localStateCallbackKK;
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        OutputConfiguration localOutputConfiguration = (OutputConfiguration)CameraDeviceImpl.-get4(CameraDeviceImpl.this).get(paramInt);
        localStateCallbackKK = CameraDeviceImpl.-get12(CameraDeviceImpl.this);
        if (localStateCallbackKK == null) {
          return;
        }
      }
      if (localObject2 == null)
      {
        Log.w(CameraDeviceImpl.-get0(CameraDeviceImpl.this), "onPrepared invoked for unknown output Surface");
        return;
      }
      localStateCallbackKK.onSurfacePrepared(((OutputConfiguration)localObject2).getSurface());
    }
    
    public void onRepeatingRequestError(long paramLong)
    {
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        if (CameraDeviceImpl.-get10(CameraDeviceImpl.this) != null)
        {
          int i = CameraDeviceImpl.-get11(CameraDeviceImpl.this);
          if (i != -1) {}
        }
        else
        {
          return;
        }
        CameraDeviceImpl.-wrap3(CameraDeviceImpl.this, CameraDeviceImpl.-get11(CameraDeviceImpl.this), paramLong);
        CameraDeviceImpl.-set2(CameraDeviceImpl.this, -1);
        return;
      }
    }
    
    public void onResultReceived(final CameraMetadataNative paramCameraMetadataNative, CaptureResultExtras paramCaptureResultExtras)
      throws RemoteException
    {
      int i = paramCaptureResultExtras.getRequestId();
      long l = paramCaptureResultExtras.getFrameNumber();
      synchronized (CameraDeviceImpl.this.mInterfaceLock)
      {
        final Object localObject2 = CameraDeviceImpl.-get10(CameraDeviceImpl.this);
        if (localObject2 == null) {
          return;
        }
        paramCameraMetadataNative.set(CameraCharacteristics.LENS_INFO_SHADING_MAP_SIZE, (Size)CameraDeviceImpl.-wrap0(CameraDeviceImpl.this).get(CameraCharacteristics.LENS_INFO_SHADING_MAP_SIZE));
        localObject2 = (CameraDeviceImpl.CaptureCallbackHolder)CameraDeviceImpl.-get3(CameraDeviceImpl.this).get(i);
        final CaptureRequest localCaptureRequest = ((CameraDeviceImpl.CaptureCallbackHolder)localObject2).getRequest(paramCaptureResultExtras.getSubsequenceId());
        if (paramCaptureResultExtras.getPartialResultCount() < CameraDeviceImpl.-get13(CameraDeviceImpl.this)) {}
        boolean bool2;
        for (boolean bool1 = true;; bool1 = false)
        {
          bool2 = localCaptureRequest.isReprocess();
          if (localObject2 != null) {
            break;
          }
          CameraDeviceImpl.-get8(CameraDeviceImpl.this).updateTracker(l, null, bool1, bool2);
          return;
        }
        if (CameraDeviceImpl.-wrap1(CameraDeviceImpl.this))
        {
          CameraDeviceImpl.-get8(CameraDeviceImpl.this).updateTracker(l, null, bool1, bool2);
          return;
        }
        if (bool1)
        {
          paramCameraMetadataNative = new CaptureResult(paramCameraMetadataNative, localCaptureRequest, paramCaptureResultExtras);
          paramCaptureResultExtras = new Runnable()
          {
            public void run()
            {
              if (!CameraDeviceImpl.-wrap1(CameraDeviceImpl.this)) {
                localObject2.getCallback().onCaptureProgressed(CameraDeviceImpl.this, localCaptureRequest, paramCameraMetadataNative);
              }
            }
          };
          ((CameraDeviceImpl.CaptureCallbackHolder)localObject2).getHandler().post(paramCaptureResultExtras);
          CameraDeviceImpl.-get8(CameraDeviceImpl.this).updateTracker(l, paramCameraMetadataNative, bool1, bool2);
          if (!bool1) {
            CameraDeviceImpl.-wrap2(CameraDeviceImpl.this);
          }
          return;
        }
        paramCameraMetadataNative = new TotalCaptureResult(paramCameraMetadataNative, localCaptureRequest, paramCaptureResultExtras, CameraDeviceImpl.-get8(CameraDeviceImpl.this).popPartialResults(l), ((CameraDeviceImpl.CaptureCallbackHolder)localObject2).getSessionId());
        paramCaptureResultExtras = new Runnable()
        {
          public void run()
          {
            if (!CameraDeviceImpl.-wrap1(CameraDeviceImpl.this)) {
              localObject2.getCallback().onCaptureCompleted(CameraDeviceImpl.this, localCaptureRequest, paramCameraMetadataNative);
            }
          }
        };
      }
    }
  }
  
  public static abstract class CaptureCallback
  {
    public static final int NO_FRAMES_CAPTURED = -1;
    
    public void onCaptureBufferLost(CameraDevice paramCameraDevice, CaptureRequest paramCaptureRequest, Surface paramSurface, long paramLong) {}
    
    public void onCaptureCompleted(CameraDevice paramCameraDevice, CaptureRequest paramCaptureRequest, TotalCaptureResult paramTotalCaptureResult) {}
    
    public void onCaptureFailed(CameraDevice paramCameraDevice, CaptureRequest paramCaptureRequest, CaptureFailure paramCaptureFailure) {}
    
    public void onCapturePartial(CameraDevice paramCameraDevice, CaptureRequest paramCaptureRequest, CaptureResult paramCaptureResult) {}
    
    public void onCaptureProgressed(CameraDevice paramCameraDevice, CaptureRequest paramCaptureRequest, CaptureResult paramCaptureResult) {}
    
    public void onCaptureSequenceAborted(CameraDevice paramCameraDevice, int paramInt) {}
    
    public void onCaptureSequenceCompleted(CameraDevice paramCameraDevice, int paramInt, long paramLong) {}
    
    public void onCaptureStarted(CameraDevice paramCameraDevice, CaptureRequest paramCaptureRequest, long paramLong1, long paramLong2) {}
  }
  
  static class CaptureCallbackHolder
  {
    private final CameraDeviceImpl.CaptureCallback mCallback;
    private final Handler mHandler;
    private final boolean mRepeating;
    private final List<CaptureRequest> mRequestList;
    private final int mSessionId;
    
    CaptureCallbackHolder(CameraDeviceImpl.CaptureCallback paramCaptureCallback, List<CaptureRequest> paramList, Handler paramHandler, boolean paramBoolean, int paramInt)
    {
      if ((paramCaptureCallback == null) || (paramHandler == null)) {
        throw new UnsupportedOperationException("Must have a valid handler and a valid callback");
      }
      this.mRepeating = paramBoolean;
      this.mHandler = paramHandler;
      this.mRequestList = new ArrayList(paramList);
      this.mCallback = paramCaptureCallback;
      this.mSessionId = paramInt;
    }
    
    public CameraDeviceImpl.CaptureCallback getCallback()
    {
      return this.mCallback;
    }
    
    public Handler getHandler()
    {
      return this.mHandler;
    }
    
    public CaptureRequest getRequest()
    {
      return getRequest(0);
    }
    
    public CaptureRequest getRequest(int paramInt)
    {
      if (paramInt >= this.mRequestList.size()) {
        throw new IllegalArgumentException(String.format("Requested subsequenceId %d is larger than request list size %d.", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(this.mRequestList.size()) }));
      }
      if (paramInt < 0) {
        throw new IllegalArgumentException(String.format("Requested subsequenceId %d is negative", new Object[] { Integer.valueOf(paramInt) }));
      }
      return (CaptureRequest)this.mRequestList.get(paramInt);
    }
    
    public int getSessionId()
    {
      return this.mSessionId;
    }
    
    public boolean isRepeating()
    {
      return this.mRepeating;
    }
  }
  
  public class FrameNumberTracker
  {
    private long mCompletedFrameNumber = -1L;
    private long mCompletedReprocessFrameNumber = -1L;
    private final TreeMap<Long, Boolean> mFutureErrorMap = new TreeMap();
    private final HashMap<Long, List<CaptureResult>> mPartialResults = new HashMap();
    private final LinkedList<Long> mSkippedRegularFrameNumbers = new LinkedList();
    private final LinkedList<Long> mSkippedReprocessFrameNumbers = new LinkedList();
    
    public FrameNumberTracker() {}
    
    private void update()
    {
      Iterator localIterator = this.mFutureErrorMap.entrySet().iterator();
      label223:
      while (localIterator.hasNext())
      {
        Object localObject = (Map.Entry)localIterator.next();
        Long localLong = (Long)((Map.Entry)localObject).getKey();
        Boolean localBoolean = (Boolean)((Map.Entry)localObject).getValue();
        localObject = Boolean.valueOf(true);
        if (localBoolean.booleanValue()) {
          if (localLong.longValue() == this.mCompletedReprocessFrameNumber + 1L) {
            this.mCompletedReprocessFrameNumber = localLong.longValue();
          }
        }
        for (;;)
        {
          if (!((Boolean)localObject).booleanValue()) {
            break label223;
          }
          localIterator.remove();
          break;
          if ((!this.mSkippedReprocessFrameNumbers.isEmpty()) && (localLong == this.mSkippedReprocessFrameNumbers.element()))
          {
            this.mCompletedReprocessFrameNumber = localLong.longValue();
            this.mSkippedReprocessFrameNumbers.remove();
          }
          else
          {
            localObject = Boolean.valueOf(false);
            continue;
            if (localLong.longValue() == this.mCompletedFrameNumber + 1L)
            {
              this.mCompletedFrameNumber = localLong.longValue();
            }
            else if ((!this.mSkippedRegularFrameNumbers.isEmpty()) && (localLong == this.mSkippedRegularFrameNumbers.element()))
            {
              this.mCompletedFrameNumber = localLong.longValue();
              this.mSkippedRegularFrameNumbers.remove();
            }
            else
            {
              localObject = Boolean.valueOf(false);
            }
          }
        }
      }
    }
    
    private void updateCompletedFrameNumber(long paramLong)
      throws IllegalArgumentException
    {
      if (paramLong <= this.mCompletedFrameNumber) {
        throw new IllegalArgumentException("frame number " + paramLong + " is a repeat");
      }
      if (paramLong <= this.mCompletedReprocessFrameNumber)
      {
        if ((this.mSkippedRegularFrameNumbers.isEmpty()) || (paramLong < ((Long)this.mSkippedRegularFrameNumbers.element()).longValue())) {
          throw new IllegalArgumentException("frame number " + paramLong + " is a repeat");
        }
        if (paramLong > ((Long)this.mSkippedRegularFrameNumbers.element()).longValue()) {
          throw new IllegalArgumentException("frame number " + paramLong + " comes out of order. Expecting " + this.mSkippedRegularFrameNumbers.element());
        }
        this.mSkippedRegularFrameNumbers.remove();
      }
      for (;;)
      {
        this.mCompletedFrameNumber = paramLong;
        return;
        for (long l = Math.max(this.mCompletedFrameNumber, this.mCompletedReprocessFrameNumber) + 1L; l < paramLong; l += 1L) {
          this.mSkippedReprocessFrameNumbers.add(Long.valueOf(l));
        }
      }
    }
    
    private void updateCompletedReprocessFrameNumber(long paramLong)
      throws IllegalArgumentException
    {
      if (paramLong < this.mCompletedReprocessFrameNumber) {
        throw new IllegalArgumentException("frame number " + paramLong + " is a repeat");
      }
      if (paramLong < this.mCompletedFrameNumber)
      {
        if ((this.mSkippedReprocessFrameNumbers.isEmpty()) || (paramLong < ((Long)this.mSkippedReprocessFrameNumbers.element()).longValue())) {
          throw new IllegalArgumentException("frame number " + paramLong + " is a repeat");
        }
        if (paramLong > ((Long)this.mSkippedReprocessFrameNumbers.element()).longValue()) {
          throw new IllegalArgumentException("frame number " + paramLong + " comes out of order. Expecting " + this.mSkippedReprocessFrameNumbers.element());
        }
        this.mSkippedReprocessFrameNumbers.remove();
      }
      for (;;)
      {
        this.mCompletedReprocessFrameNumber = paramLong;
        return;
        for (long l = Math.max(this.mCompletedFrameNumber, this.mCompletedReprocessFrameNumber) + 1L; l < paramLong; l += 1L) {
          this.mSkippedRegularFrameNumbers.add(Long.valueOf(l));
        }
      }
    }
    
    public long getCompletedFrameNumber()
    {
      return this.mCompletedFrameNumber;
    }
    
    public long getCompletedReprocessFrameNumber()
    {
      return this.mCompletedReprocessFrameNumber;
    }
    
    public List<CaptureResult> popPartialResults(long paramLong)
    {
      return (List)this.mPartialResults.remove(Long.valueOf(paramLong));
    }
    
    public void updateTracker(long paramLong, CaptureResult paramCaptureResult, boolean paramBoolean1, boolean paramBoolean2)
    {
      if (!paramBoolean1)
      {
        updateTracker(paramLong, false, paramBoolean2);
        return;
      }
      if (paramCaptureResult == null) {
        return;
      }
      List localList = (List)this.mPartialResults.get(Long.valueOf(paramLong));
      Object localObject = localList;
      if (localList == null)
      {
        localObject = new ArrayList();
        this.mPartialResults.put(Long.valueOf(paramLong), localObject);
      }
      ((List)localObject).add(paramCaptureResult);
    }
    
    public void updateTracker(long paramLong, boolean paramBoolean1, boolean paramBoolean2)
    {
      if (paramBoolean1) {
        this.mFutureErrorMap.put(Long.valueOf(paramLong), Boolean.valueOf(paramBoolean2));
      }
      for (;;)
      {
        update();
        return;
        if (paramBoolean2) {
          try
          {
            updateCompletedReprocessFrameNumber(paramLong);
          }
          catch (IllegalArgumentException localIllegalArgumentException)
          {
            Log.e(CameraDeviceImpl.-get0(CameraDeviceImpl.this), localIllegalArgumentException.getMessage());
          }
        } else {
          updateCompletedFrameNumber(paramLong);
        }
      }
    }
  }
  
  static class RequestLastFrameNumbersHolder
  {
    private final long mLastRegularFrameNumber;
    private final long mLastReprocessFrameNumber;
    private final int mRequestId;
    
    public RequestLastFrameNumbersHolder(int paramInt, long paramLong)
    {
      this.mLastRegularFrameNumber = paramLong;
      this.mLastReprocessFrameNumber = -1L;
      this.mRequestId = paramInt;
    }
    
    public RequestLastFrameNumbersHolder(List<CaptureRequest> paramList, SubmitInfo paramSubmitInfo)
    {
      long l3 = -1L;
      long l2 = -1L;
      long l1 = paramSubmitInfo.getLastFrameNumber();
      if (paramSubmitInfo.getLastFrameNumber() < paramList.size() - 1) {
        throw new IllegalArgumentException("lastFrameNumber: " + paramSubmitInfo.getLastFrameNumber() + " should be at least " + (paramList.size() - 1) + " for the number of " + " requests in the list: " + paramList.size());
      }
      int i = paramList.size() - 1;
      for (;;)
      {
        long l4 = l3;
        long l5 = l2;
        CaptureRequest localCaptureRequest;
        if (i >= 0)
        {
          localCaptureRequest = (CaptureRequest)paramList.get(i);
          if ((!localCaptureRequest.isReprocess()) || (l2 != -1L)) {
            break label199;
          }
          l5 = l1;
          l4 = l3;
        }
        while ((l5 != -1L) && (l4 != -1L))
        {
          this.mLastRegularFrameNumber = l4;
          this.mLastReprocessFrameNumber = l5;
          this.mRequestId = paramSubmitInfo.getRequestId();
          return;
          label199:
          l4 = l3;
          l5 = l2;
          if (!localCaptureRequest.isReprocess())
          {
            l4 = l3;
            l5 = l2;
            if (l3 == -1L)
            {
              l4 = l1;
              l5 = l2;
            }
          }
        }
        l1 -= 1L;
        i -= 1;
        l3 = l4;
        l2 = l5;
      }
    }
    
    public long getLastFrameNumber()
    {
      return Math.max(this.mLastRegularFrameNumber, this.mLastReprocessFrameNumber);
    }
    
    public long getLastRegularFrameNumber()
    {
      return this.mLastRegularFrameNumber;
    }
    
    public long getLastReprocessFrameNumber()
    {
      return this.mLastReprocessFrameNumber;
    }
    
    public int getRequestId()
    {
      return this.mRequestId;
    }
  }
  
  public static abstract class StateCallbackKK
    extends CameraDevice.StateCallback
  {
    public void onActive(CameraDevice paramCameraDevice) {}
    
    public void onBusy(CameraDevice paramCameraDevice) {}
    
    public void onIdle(CameraDevice paramCameraDevice) {}
    
    public void onSurfacePrepared(Surface paramSurface) {}
    
    public void onUnconfigured(CameraDevice paramCameraDevice) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/impl/CameraDeviceImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */