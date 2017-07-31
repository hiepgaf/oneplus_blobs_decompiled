package com.android.server.soundtrigger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.soundtrigger.IRecognitionStatusCallback;
import android.hardware.soundtrigger.SoundTrigger;
import android.hardware.soundtrigger.SoundTrigger.GenericRecognitionEvent;
import android.hardware.soundtrigger.SoundTrigger.GenericSoundModel;
import android.hardware.soundtrigger.SoundTrigger.KeyphraseRecognitionEvent;
import android.hardware.soundtrigger.SoundTrigger.KeyphraseRecognitionExtra;
import android.hardware.soundtrigger.SoundTrigger.KeyphraseSoundModel;
import android.hardware.soundtrigger.SoundTrigger.ModuleProperties;
import android.hardware.soundtrigger.SoundTrigger.RecognitionConfig;
import android.hardware.soundtrigger.SoundTrigger.RecognitionEvent;
import android.hardware.soundtrigger.SoundTrigger.SoundModel;
import android.hardware.soundtrigger.SoundTrigger.SoundModelEvent;
import android.hardware.soundtrigger.SoundTrigger.StatusListener;
import android.hardware.soundtrigger.SoundTriggerModule;
import android.os.PowerManager;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class SoundTriggerHelper
  implements SoundTrigger.StatusListener
{
  static final boolean DBG = false;
  private static final int INVALID_VALUE = Integer.MIN_VALUE;
  public static final int STATUS_ERROR = Integer.MIN_VALUE;
  public static final int STATUS_OK = 0;
  static final String TAG = "SoundTriggerHelper";
  private boolean mCallActive = false;
  private final Context mContext;
  private boolean mIsPowerSaveMode = false;
  private HashMap<Integer, UUID> mKeyphraseUuidMap;
  private final Object mLock = new Object();
  private final HashMap<UUID, ModelData> mModelDataMap;
  private SoundTriggerModule mModule;
  final SoundTrigger.ModuleProperties mModuleProperties;
  private final PhoneStateListener mPhoneStateListener;
  private final PowerManager mPowerManager;
  private PowerSaveModeListener mPowerSaveModeListener;
  private boolean mRecognitionRunning = false;
  private boolean mServiceDisabled = false;
  private final TelephonyManager mTelephonyManager;
  
  SoundTriggerHelper(Context paramContext)
  {
    ArrayList localArrayList = new ArrayList();
    int i = SoundTrigger.listModules(localArrayList);
    this.mContext = paramContext;
    this.mTelephonyManager = ((TelephonyManager)paramContext.getSystemService("phone"));
    this.mPowerManager = ((PowerManager)paramContext.getSystemService("power"));
    this.mModelDataMap = new HashMap();
    this.mKeyphraseUuidMap = new HashMap();
    this.mPhoneStateListener = new MyCallStateListener();
    if ((i != 0) || (localArrayList.size() == 0))
    {
      Slog.w("SoundTriggerHelper", "listModules status=" + i + ", # of modules=" + localArrayList.size());
      this.mModuleProperties = null;
      this.mModule = null;
      return;
    }
    this.mModuleProperties = ((SoundTrigger.ModuleProperties)localArrayList.get(0));
  }
  
  private int cleanUpExistingKeyphraseModel(ModelData paramModelData)
  {
    int i = tryStopAndUnloadLocked(paramModelData, true, true);
    if (i != 0) {
      Slog.w("SoundTriggerHelper", "Unable to stop or unload previous model: " + paramModelData.toString());
    }
    return i;
  }
  
  private boolean computeRecognitionRunningLocked()
  {
    if ((this.mModuleProperties == null) || (this.mModule == null))
    {
      this.mRecognitionRunning = false;
      return this.mRecognitionRunning;
    }
    Iterator localIterator = this.mModelDataMap.values().iterator();
    while (localIterator.hasNext()) {
      if (((ModelData)localIterator.next()).isModelStarted())
      {
        this.mRecognitionRunning = true;
        return this.mRecognitionRunning;
      }
    }
    this.mRecognitionRunning = false;
    return this.mRecognitionRunning;
  }
  
  private ModelData createKeyphraseModelDataLocked(UUID paramUUID, int paramInt)
  {
    this.mKeyphraseUuidMap.remove(Integer.valueOf(paramInt));
    this.mModelDataMap.remove(paramUUID);
    this.mKeyphraseUuidMap.put(Integer.valueOf(paramInt), paramUUID);
    ModelData localModelData = ModelData.createKeyphraseModelData(paramUUID);
    this.mModelDataMap.put(paramUUID, localModelData);
    return localModelData;
  }
  
  private void dumpModelStateLocked()
  {
    Iterator localIterator = this.mModelDataMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (UUID)localIterator.next();
      localObject = (ModelData)this.mModelDataMap.get(localObject);
      Slog.i("SoundTriggerHelper", "Model :" + ((ModelData)localObject).toString());
    }
  }
  
  private int getKeyphraseIdFromEvent(SoundTrigger.KeyphraseRecognitionEvent paramKeyphraseRecognitionEvent)
  {
    if (paramKeyphraseRecognitionEvent == null)
    {
      Slog.w("SoundTriggerHelper", "Null RecognitionEvent received.");
      return Integer.MIN_VALUE;
    }
    paramKeyphraseRecognitionEvent = paramKeyphraseRecognitionEvent.keyphraseExtras;
    if ((paramKeyphraseRecognitionEvent == null) || (paramKeyphraseRecognitionEvent.length == 0))
    {
      Slog.w("SoundTriggerHelper", "Invalid keyphrase recognition event!");
      return Integer.MIN_VALUE;
    }
    return paramKeyphraseRecognitionEvent[0].id;
  }
  
  private ModelData getKeyphraseModelDataLocked(int paramInt)
  {
    UUID localUUID = (UUID)this.mKeyphraseUuidMap.get(Integer.valueOf(paramInt));
    if (localUUID == null) {
      return null;
    }
    return (ModelData)this.mModelDataMap.get(localUUID);
  }
  
  private ModelData getModelDataForLocked(int paramInt)
  {
    Iterator localIterator = this.mModelDataMap.values().iterator();
    while (localIterator.hasNext())
    {
      ModelData localModelData = (ModelData)localIterator.next();
      if (localModelData.getHandle() == paramInt) {
        return localModelData;
      }
    }
    return null;
  }
  
  private ModelData getOrCreateGenericModelDataLocked(UUID paramUUID)
  {
    ModelData localModelData = (ModelData)this.mModelDataMap.get(paramUUID);
    if (localModelData == null)
    {
      localModelData = ModelData.createGenericModelData(paramUUID);
      this.mModelDataMap.put(paramUUID, localModelData);
      paramUUID = localModelData;
    }
    do
    {
      return paramUUID;
      paramUUID = localModelData;
    } while (localModelData.isGenericModel());
    Slog.e("SoundTriggerHelper", "UUID already used for non-generic model.");
    return null;
  }
  
  private void initializeTelephonyAndPowerStateListeners()
  {
    boolean bool = false;
    if (this.mTelephonyManager.getCallState() != 0) {
      bool = true;
    }
    this.mCallActive = bool;
    this.mTelephonyManager.listen(this.mPhoneStateListener, 32);
    if (this.mPowerSaveModeListener == null)
    {
      this.mPowerSaveModeListener = new PowerSaveModeListener();
      this.mContext.registerReceiver(this.mPowerSaveModeListener, new IntentFilter("android.os.action.POWER_SAVE_MODE_CHANGED"));
    }
    this.mIsPowerSaveMode = this.mPowerManager.isPowerSaveMode();
  }
  
  private void internalClearGlobalStateLocked()
  {
    this.mTelephonyManager.listen(this.mPhoneStateListener, 0);
    if (this.mPowerSaveModeListener != null)
    {
      this.mContext.unregisterReceiver(this.mPowerSaveModeListener);
      this.mPowerSaveModeListener = null;
    }
  }
  
  private void internalClearModelStateLocked()
  {
    Iterator localIterator = this.mModelDataMap.values().iterator();
    while (localIterator.hasNext()) {
      ((ModelData)localIterator.next()).clearState();
    }
  }
  
  private boolean isKeyphraseRecognitionEvent(SoundTrigger.RecognitionEvent paramRecognitionEvent)
  {
    return paramRecognitionEvent instanceof SoundTrigger.KeyphraseRecognitionEvent;
  }
  
  private boolean isRecognitionAllowed()
  {
    if ((this.mCallActive) || (this.mServiceDisabled)) {}
    while (this.mIsPowerSaveMode) {
      return false;
    }
    return true;
  }
  
  private void onCallStateChangedLocked(boolean paramBoolean)
  {
    if (this.mCallActive == paramBoolean) {
      return;
    }
    this.mCallActive = paramBoolean;
    updateAllRecognitionsLocked(true);
  }
  
  private void onGenericRecognitionSuccessLocked(SoundTrigger.GenericRecognitionEvent paramGenericRecognitionEvent)
  {
    MetricsLogger.count(this.mContext, "sth_generic_recognition_event", 1);
    if (paramGenericRecognitionEvent.status != 0) {
      return;
    }
    ModelData localModelData = getModelDataForLocked(paramGenericRecognitionEvent.soundModelHandle);
    Object localObject;
    if ((localModelData != null) && (localModelData.isGenericModel()))
    {
      localObject = localModelData.getCallback();
      if (localObject == null) {
        Slog.w("SoundTriggerHelper", "Generic recognition event: Null callback for model handle: " + paramGenericRecognitionEvent.soundModelHandle);
      }
    }
    else
    {
      Slog.w("SoundTriggerHelper", "Generic recognition event: Model does not exist for handle: " + paramGenericRecognitionEvent.soundModelHandle);
      return;
    }
    try
    {
      ((IRecognitionStatusCallback)localObject).onGenericSoundTriggerDetected(paramGenericRecognitionEvent);
      localModelData.setStopped();
      localObject = localModelData.getRecognitionConfig();
      if (localObject == null)
      {
        Slog.w("SoundTriggerHelper", "Generic recognition event: Null RecognitionConfig for model handle: " + paramGenericRecognitionEvent.soundModelHandle);
        return;
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("SoundTriggerHelper", "RemoteException in onGenericSoundTriggerDetected", localRemoteException);
      }
      localModelData.setRequested(localRemoteException.allowMultipleTriggers);
      if (localModelData.isRequested()) {
        updateRecognitionLocked(localModelData, isRecognitionAllowed(), true);
      }
    }
  }
  
  private void onKeyphraseRecognitionSuccessLocked(SoundTrigger.KeyphraseRecognitionEvent paramKeyphraseRecognitionEvent)
  {
    Slog.i("SoundTriggerHelper", "Recognition success");
    MetricsLogger.count(this.mContext, "sth_keyphrase_recognition_event", 1);
    int i = getKeyphraseIdFromEvent(paramKeyphraseRecognitionEvent);
    ModelData localModelData = getKeyphraseModelDataLocked(i);
    if ((localModelData != null) && (localModelData.isKeyphraseModel()))
    {
      if (localModelData.getCallback() == null) {
        Slog.w("SoundTriggerHelper", "Received onRecognition event without callback for keyphrase model.");
      }
    }
    else
    {
      Slog.e("SoundTriggerHelper", "Keyphase model data does not exist for ID:" + i);
      return;
    }
    try
    {
      localModelData.getCallback().onKeyphraseDetected(paramKeyphraseRecognitionEvent);
      localModelData.setStopped();
      paramKeyphraseRecognitionEvent = localModelData.getRecognitionConfig();
      if (paramKeyphraseRecognitionEvent != null) {
        localModelData.setRequested(paramKeyphraseRecognitionEvent.allowMultipleTriggers);
      }
      if (localModelData.isRequested()) {
        updateRecognitionLocked(localModelData, isRecognitionAllowed(), true);
      }
      return;
    }
    catch (RemoteException paramKeyphraseRecognitionEvent)
    {
      for (;;)
      {
        Slog.w("SoundTriggerHelper", "RemoteException in onKeyphraseDetected", paramKeyphraseRecognitionEvent);
      }
    }
  }
  
  private void onPowerSaveModeChangedLocked(boolean paramBoolean)
  {
    if (this.mIsPowerSaveMode == paramBoolean) {
      return;
    }
    this.mIsPowerSaveMode = paramBoolean;
    updateAllRecognitionsLocked(true);
  }
  
  private void onRecognitionAbortLocked(SoundTrigger.RecognitionEvent paramRecognitionEvent)
  {
    Slog.w("SoundTriggerHelper", "Recognition aborted");
    MetricsLogger.count(this.mContext, "sth_recognition_aborted", 1);
    paramRecognitionEvent = getModelDataForLocked(paramRecognitionEvent.soundModelHandle);
    if ((paramRecognitionEvent != null) && (paramRecognitionEvent.isModelStarted())) {
      paramRecognitionEvent.setStopped();
    }
    try
    {
      paramRecognitionEvent.getCallback().onRecognitionPaused();
      return;
    }
    catch (RemoteException paramRecognitionEvent)
    {
      Slog.w("SoundTriggerHelper", "RemoteException in onRecognitionPaused", paramRecognitionEvent);
    }
  }
  
  private void onRecognitionFailureLocked()
  {
    Slog.w("SoundTriggerHelper", "Recognition failure");
    MetricsLogger.count(this.mContext, "sth_recognition_failure_event", 1);
    try
    {
      sendErrorCallbacksToAll(Integer.MIN_VALUE);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("SoundTriggerHelper", "RemoteException in onError", localRemoteException);
      return;
    }
    finally
    {
      internalClearModelStateLocked();
      internalClearGlobalStateLocked();
    }
  }
  
  private void onServiceDiedLocked()
  {
    label45:
    label110:
    try
    {
      MetricsLogger.count(this.mContext, "sth_service_died", 1);
      sendErrorCallbacksToAll(SoundTrigger.STATUS_DEAD_OBJECT);
      internalClearModelStateLocked();
      internalClearGlobalStateLocked();
      if (this.mModule == null) {
        break label45;
      }
      this.mModule.detach();
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Slog.w("SoundTriggerHelper", "RemoteException in onError", localRemoteException);
        internalClearModelStateLocked();
        internalClearGlobalStateLocked();
        if (this.mModule != null) {
          this.mModule.detach();
        }
      }
    }
    finally
    {
      internalClearModelStateLocked();
      internalClearGlobalStateLocked();
      if (this.mModule == null) {
        break label110;
      }
      this.mModule.detach();
      this.mModule = null;
    }
    this.mModule = null;
  }
  
  private void onServiceStateChangedLocked(boolean paramBoolean)
  {
    if (paramBoolean == this.mServiceDisabled) {
      return;
    }
    this.mServiceDisabled = paramBoolean;
    updateAllRecognitionsLocked(true);
  }
  
  private void onSoundModelUpdatedLocked(SoundTrigger.SoundModelEvent paramSoundModelEvent) {}
  
  private void removeKeyphraseModelLocked(int paramInt)
  {
    UUID localUUID = (UUID)this.mKeyphraseUuidMap.get(Integer.valueOf(paramInt));
    if (localUUID == null) {
      return;
    }
    this.mModelDataMap.remove(localUUID);
    this.mKeyphraseUuidMap.remove(Integer.valueOf(paramInt));
  }
  
  private void sendErrorCallbacksToAll(int paramInt)
    throws RemoteException
  {
    Iterator localIterator = this.mModelDataMap.values().iterator();
    while (localIterator.hasNext())
    {
      IRecognitionStatusCallback localIRecognitionStatusCallback = ((ModelData)localIterator.next()).getCallback();
      if (localIRecognitionStatusCallback != null) {
        localIRecognitionStatusCallback.onError(Integer.MIN_VALUE);
      }
    }
  }
  
  private int startRecognitionLocked(ModelData paramModelData, boolean paramBoolean)
  {
    IRecognitionStatusCallback localIRecognitionStatusCallback = paramModelData.getCallback();
    int i = paramModelData.getHandle();
    SoundTrigger.RecognitionConfig localRecognitionConfig = paramModelData.getRecognitionConfig();
    if ((localIRecognitionStatusCallback == null) || (i == Integer.MIN_VALUE)) {}
    while (localRecognitionConfig == null)
    {
      Slog.w("SoundTriggerHelper", "startRecognition: Bad data passed in.");
      MetricsLogger.count(this.mContext, "sth_start_recognition_error", 1);
      return Integer.MIN_VALUE;
    }
    if (!isRecognitionAllowed())
    {
      Slog.w("SoundTriggerHelper", "startRecognition requested but not allowed.");
      MetricsLogger.count(this.mContext, "sth_start_recognition_not_allowed", 1);
      return 0;
    }
    i = this.mModule.startRecognition(i, localRecognitionConfig);
    if (i != 0)
    {
      Slog.w("SoundTriggerHelper", "startRecognition failed with " + i);
      MetricsLogger.count(this.mContext, "sth_start_recognition_error", 1);
      if (!paramBoolean) {}
    }
    do
    {
      try
      {
        localIRecognitionStatusCallback.onError(i);
        return i;
      }
      catch (RemoteException paramModelData)
      {
        Slog.w("SoundTriggerHelper", "RemoteException in onError", paramModelData);
        return i;
      }
      Slog.i("SoundTriggerHelper", "startRecognition successful.");
      MetricsLogger.count(this.mContext, "sth_start_recognition_success", 1);
      paramModelData.setStarted();
    } while (!paramBoolean);
    try
    {
      localIRecognitionStatusCallback.onRecognitionResumed();
      return i;
    }
    catch (RemoteException paramModelData)
    {
      Slog.w("SoundTriggerHelper", "RemoteException in onRecognitionResumed", paramModelData);
    }
    return i;
  }
  
  private int stopRecognition(ModelData paramModelData, IRecognitionStatusCallback paramIRecognitionStatusCallback)
  {
    Object localObject = this.mLock;
    if (paramIRecognitionStatusCallback == null) {
      return Integer.MIN_VALUE;
    }
    try
    {
      if ((this.mModuleProperties == null) || (this.mModule == null))
      {
        Slog.w("SoundTriggerHelper", "Attempting stopRecognition without the capability");
        return Integer.MIN_VALUE;
      }
      IRecognitionStatusCallback localIRecognitionStatusCallback = paramModelData.getCallback();
      if ((paramModelData == null) || (localIRecognitionStatusCallback == null)) {}
      while ((!paramModelData.isRequested()) && (!paramModelData.isModelStarted()))
      {
        Slog.w("SoundTriggerHelper", "Attempting stopRecognition without a successful startRecognition");
        return Integer.MIN_VALUE;
      }
      if (localIRecognitionStatusCallback.asBinder() != paramIRecognitionStatusCallback.asBinder())
      {
        Slog.w("SoundTriggerHelper", "Attempting stopRecognition for another recognition");
        return Integer.MIN_VALUE;
      }
      paramModelData.setRequested(false);
      int i = updateRecognitionLocked(paramModelData, isRecognitionAllowed(), false);
      if (i != 0) {
        return i;
      }
      paramModelData.setLoaded();
      paramModelData.clearCallback();
      paramModelData.setRecognitionConfig(null);
      if (!computeRecognitionRunningLocked()) {
        internalClearGlobalStateLocked();
      }
      return i;
    }
    finally {}
  }
  
  private int stopRecognitionLocked(ModelData paramModelData, boolean paramBoolean)
  {
    IRecognitionStatusCallback localIRecognitionStatusCallback = paramModelData.getCallback();
    int i = this.mModule.stopRecognition(paramModelData.getHandle());
    if (i != 0)
    {
      Slog.w("SoundTriggerHelper", "stopRecognition call failed with " + i);
      MetricsLogger.count(this.mContext, "sth_stop_recognition_error", 1);
      if (!paramBoolean) {}
    }
    do
    {
      try
      {
        localIRecognitionStatusCallback.onError(i);
        return i;
      }
      catch (RemoteException paramModelData)
      {
        Slog.w("SoundTriggerHelper", "RemoteException in onError", paramModelData);
        return i;
      }
      paramModelData.setStopped();
      MetricsLogger.count(this.mContext, "sth_stop_recognition_success", 1);
    } while (!paramBoolean);
    try
    {
      localIRecognitionStatusCallback.onRecognitionPaused();
      return i;
    }
    catch (RemoteException paramModelData)
    {
      Slog.w("SoundTriggerHelper", "RemoteException in onRecognitionPaused", paramModelData);
    }
    return i;
  }
  
  private int tryStopAndUnloadLocked(ModelData paramModelData, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 0;
    if (paramModelData.isModelNotLoaded()) {
      return 0;
    }
    int i = j;
    if (paramBoolean1)
    {
      i = j;
      if (paramModelData.isModelStarted())
      {
        j = stopRecognitionLocked(paramModelData, false);
        i = j;
        if (j != 0)
        {
          Slog.w("SoundTriggerHelper", "stopRecognition failed: " + j);
          return j;
        }
      }
    }
    j = i;
    if (paramBoolean2)
    {
      j = i;
      if (paramModelData.isModelLoaded())
      {
        Slog.d("SoundTriggerHelper", "Unloading previously loaded stale model.");
        j = this.mModule.unloadSoundModel(paramModelData.getHandle());
        MetricsLogger.count(this.mContext, "sth_unloading_stale_model", 1);
        if (j == 0) {
          break label165;
        }
        Slog.w("SoundTriggerHelper", "unloadSoundModel call failed with " + j);
      }
    }
    return j;
    label165:
    paramModelData.clearState();
    return j;
  }
  
  private void updateAllRecognitionsLocked(boolean paramBoolean)
  {
    boolean bool = isRecognitionAllowed();
    Iterator localIterator = this.mModelDataMap.values().iterator();
    while (localIterator.hasNext()) {
      updateRecognitionLocked((ModelData)localIterator.next(), bool, paramBoolean);
    }
  }
  
  private int updateRecognitionLocked(ModelData paramModelData, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramModelData.isRequested()) {}
    while (paramBoolean1 == paramModelData.isModelStarted())
    {
      return 0;
      paramBoolean1 = false;
    }
    if (paramBoolean1) {
      return startRecognitionLocked(paramModelData, paramBoolean2);
    }
    return stopRecognitionLocked(paramModelData, paramBoolean2);
  }
  
  void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] arg3)
  {
    synchronized (this.mLock)
    {
      paramPrintWriter.print("  module properties=");
      if (this.mModuleProperties == null)
      {
        paramFileDescriptor = "null";
        paramPrintWriter.println(paramFileDescriptor);
        paramPrintWriter.print("  call active=");
        paramPrintWriter.println(this.mCallActive);
        paramPrintWriter.print("  power save mode active=");
        paramPrintWriter.println(this.mIsPowerSaveMode);
        paramPrintWriter.print("  service disabled=");
        paramPrintWriter.println(this.mServiceDisabled);
        return;
      }
      paramFileDescriptor = this.mModuleProperties;
    }
  }
  
  public SoundTrigger.ModuleProperties getModuleProperties()
  {
    return this.mModuleProperties;
  }
  
  public void onRecognition(SoundTrigger.RecognitionEvent paramRecognitionEvent)
  {
    if (paramRecognitionEvent == null)
    {
      Slog.w("SoundTriggerHelper", "Null recognition event!");
      return;
    }
    if (((paramRecognitionEvent instanceof SoundTrigger.KeyphraseRecognitionEvent)) || ((paramRecognitionEvent instanceof SoundTrigger.GenericRecognitionEvent))) {}
    for (;;)
    {
      synchronized (this.mLock)
      {
        int i = paramRecognitionEvent.status;
        switch (i)
        {
        default: 
          return;
          Slog.w("SoundTriggerHelper", "Invalid recognition event type (not one of generic or keyphrase)!");
          return;
        case 1: 
          onRecognitionAbortLocked(paramRecognitionEvent);
        }
      }
      onRecognitionFailureLocked();
      continue;
      if (isKeyphraseRecognitionEvent(paramRecognitionEvent)) {
        onKeyphraseRecognitionSuccessLocked((SoundTrigger.KeyphraseRecognitionEvent)paramRecognitionEvent);
      } else {
        onGenericRecognitionSuccessLocked((SoundTrigger.GenericRecognitionEvent)paramRecognitionEvent);
      }
    }
  }
  
  public void onServiceDied()
  {
    Slog.e("SoundTriggerHelper", "onServiceDied!!");
    MetricsLogger.count(this.mContext, "sth_service_died", 1);
    synchronized (this.mLock)
    {
      onServiceDiedLocked();
      return;
    }
  }
  
  /* Error */
  public void onServiceStateChange(int paramInt)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: aload_0
    //   3: getfield 56	com/android/server/soundtrigger/SoundTriggerHelper:mLock	Ljava/lang/Object;
    //   6: astore_3
    //   7: aload_3
    //   8: monitorenter
    //   9: iconst_1
    //   10: iload_1
    //   11: if_icmpne +11 -> 22
    //   14: aload_0
    //   15: iload_2
    //   16: invokespecial 573	com/android/server/soundtrigger/SoundTriggerHelper:onServiceStateChangedLocked	(Z)V
    //   19: aload_3
    //   20: monitorexit
    //   21: return
    //   22: iconst_0
    //   23: istore_2
    //   24: goto -10 -> 14
    //   27: astore 4
    //   29: aload_3
    //   30: monitorexit
    //   31: aload 4
    //   33: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	34	0	this	SoundTriggerHelper
    //   0	34	1	paramInt	int
    //   1	23	2	bool	boolean
    //   6	24	3	localObject1	Object
    //   27	5	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   14	19	27	finally
  }
  
  public void onSoundModelUpdate(SoundTrigger.SoundModelEvent paramSoundModelEvent)
  {
    if (paramSoundModelEvent == null)
    {
      Slog.w("SoundTriggerHelper", "Invalid sound model event!");
      return;
    }
    synchronized (this.mLock)
    {
      MetricsLogger.count(this.mContext, "sth_sound_model_updated", 1);
      onSoundModelUpdatedLocked(paramSoundModelEvent);
      return;
    }
  }
  
  int startGenericRecognition(UUID paramUUID, SoundTrigger.GenericSoundModel paramGenericSoundModel, IRecognitionStatusCallback paramIRecognitionStatusCallback, SoundTrigger.RecognitionConfig paramRecognitionConfig)
  {
    MetricsLogger.count(this.mContext, "sth_start_recognition", 1);
    if ((paramUUID == null) || (paramGenericSoundModel == null)) {}
    while ((paramIRecognitionStatusCallback == null) || (paramRecognitionConfig == null))
    {
      Slog.w("SoundTriggerHelper", "Passed in bad data to startGenericRecognition().");
      return Integer.MIN_VALUE;
    }
    synchronized (this.mLock)
    {
      paramUUID = getOrCreateGenericModelDataLocked(paramUUID);
      if (paramUUID == null)
      {
        Slog.w("SoundTriggerHelper", "Irrecoverable error occurred, check UUID / sound model data.");
        return Integer.MIN_VALUE;
      }
      int i = startRecognition(paramGenericSoundModel, paramUUID, paramIRecognitionStatusCallback, paramRecognitionConfig, Integer.MIN_VALUE);
      return i;
    }
  }
  
  int startKeyphraseRecognition(int paramInt, SoundTrigger.KeyphraseSoundModel paramKeyphraseSoundModel, IRecognitionStatusCallback paramIRecognitionStatusCallback, SoundTrigger.RecognitionConfig paramRecognitionConfig)
  {
    synchronized (this.mLock)
    {
      MetricsLogger.count(this.mContext, "sth_start_recognition", 1);
      if ((paramKeyphraseSoundModel == null) || (paramIRecognitionStatusCallback == null)) {}
      while (paramRecognitionConfig == null) {
        return Integer.MIN_VALUE;
      }
      Object localObject2 = getKeyphraseModelDataLocked(paramInt);
      if ((localObject2 == null) || (((ModelData)localObject2).isKeyphraseModel()))
      {
        localObject1 = localObject2;
        if (localObject2 != null)
        {
          if (((ModelData)localObject2).getModelId().equals(paramKeyphraseSoundModel.uuid)) {
            localObject1 = localObject2;
          }
        }
        else
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = createKeyphraseModelDataLocked(paramKeyphraseSoundModel.uuid, paramInt);
          }
          paramInt = startRecognition(paramKeyphraseSoundModel, (ModelData)localObject2, paramIRecognitionStatusCallback, paramRecognitionConfig, paramInt);
          return paramInt;
        }
      }
      else
      {
        Slog.e("SoundTriggerHelper", "Generic model with same UUID exists.");
        return Integer.MIN_VALUE;
      }
      int i = cleanUpExistingKeyphraseModel((ModelData)localObject2);
      if (i != 0) {
        return i;
      }
      removeKeyphraseModelLocked(paramInt);
      Object localObject1 = null;
    }
  }
  
  int startRecognition(SoundTrigger.SoundModel paramSoundModel, ModelData paramModelData, IRecognitionStatusCallback paramIRecognitionStatusCallback, SoundTrigger.RecognitionConfig paramRecognitionConfig, int paramInt)
  {
    for (;;)
    {
      boolean bool1;
      boolean bool2;
      synchronized (this.mLock)
      {
        if (this.mModuleProperties == null)
        {
          Slog.w("SoundTriggerHelper", "Attempting startRecognition without the capability");
          return Integer.MIN_VALUE;
        }
        if (this.mModule == null)
        {
          this.mModule = SoundTrigger.attachModule(this.mModuleProperties.id, this, null);
          if (this.mModule == null)
          {
            Slog.w("SoundTriggerHelper", "startRecognition cannot attach to sound trigger module");
            return Integer.MIN_VALUE;
          }
        }
        if (!this.mRecognitionRunning) {
          initializeTelephonyAndPowerStateListeners();
        }
        if (paramModelData.getSoundModel() != null)
        {
          bool1 = false;
          bool2 = false;
          if ((paramModelData.getSoundModel().equals(paramSoundModel)) && (paramModelData.isModelStarted()))
          {
            bool1 = true;
            bool2 = false;
            break label462;
            paramInt = tryStopAndUnloadLocked(paramModelData, bool1, bool2);
            if (paramInt != 0)
            {
              Slog.w("SoundTriggerHelper", "Unable to stop or unload previous model: " + paramModelData.toString());
              return paramInt;
            }
          }
          else
          {
            if (paramModelData.getSoundModel().equals(paramSoundModel)) {
              break label462;
            }
            bool1 = paramModelData.isModelStarted();
            bool2 = paramModelData.isModelLoaded();
            break label462;
          }
        }
        Object localObject2 = paramModelData.getCallback();
        if ((localObject2 != null) && (((IRecognitionStatusCallback)localObject2).asBinder() != paramIRecognitionStatusCallback.asBinder())) {
          Slog.w("SoundTriggerHelper", "Canceling previous recognition for model id: " + paramModelData.getModelId());
        }
        try
        {
          ((IRecognitionStatusCallback)localObject2).onError(Integer.MIN_VALUE);
          paramModelData.clearCallback();
          if (paramModelData.isModelLoaded()) {
            break label427;
          }
          localObject2 = new int[1];
          localObject2[0] = Integer.MIN_VALUE;
          paramInt = this.mModule.loadSoundModel(paramSoundModel, (int[])localObject2);
          if (paramInt != 0)
          {
            Slog.w("SoundTriggerHelper", "loadSoundModel call failed with " + paramInt);
            return paramInt;
          }
        }
        catch (RemoteException localRemoteException)
        {
          Slog.w("SoundTriggerHelper", "RemoteException in onDetectionStopped", localRemoteException);
          continue;
        }
      }
      if (localRemoteException[0] == Integer.MIN_VALUE)
      {
        Slog.w("SoundTriggerHelper", "loadSoundModel call returned invalid sound model handle");
        return Integer.MIN_VALUE;
      }
      paramModelData.setHandle(localRemoteException[0]);
      paramModelData.setLoaded();
      Slog.d("SoundTriggerHelper", "Sound model loaded with handle:" + localRemoteException[0]);
      label427:
      paramModelData.setCallback(paramIRecognitionStatusCallback);
      paramModelData.setRequested(true);
      paramModelData.setRecognitionConfig(paramRecognitionConfig);
      paramModelData.setSoundModel(paramSoundModel);
      paramInt = startRecognitionLocked(paramModelData, false);
      return paramInt;
      label462:
      if (!bool1) {
        if (!bool2) {}
      }
    }
  }
  
  int stopGenericRecognition(UUID paramUUID, IRecognitionStatusCallback paramIRecognitionStatusCallback)
  {
    synchronized (this.mLock)
    {
      MetricsLogger.count(this.mContext, "sth_stop_recognition", 1);
      if ((paramIRecognitionStatusCallback == null) || (paramUUID == null))
      {
        Slog.e("SoundTriggerHelper", "Null callbackreceived for stopGenericRecognition() for modelid:" + paramUUID);
        return Integer.MIN_VALUE;
      }
      ModelData localModelData = (ModelData)this.mModelDataMap.get(paramUUID);
      if ((localModelData != null) && (localModelData.isGenericModel()))
      {
        int i = stopRecognition(localModelData, paramIRecognitionStatusCallback);
        if (i != 0) {
          Slog.w("SoundTriggerHelper", "stopGenericRecognition failed: " + i);
        }
        return i;
      }
      Slog.w("SoundTriggerHelper", "Attempting stopRecognition on invalid model with id:" + paramUUID);
      return Integer.MIN_VALUE;
    }
  }
  
  int stopKeyphraseRecognition(int paramInt, IRecognitionStatusCallback paramIRecognitionStatusCallback)
  {
    synchronized (this.mLock)
    {
      MetricsLogger.count(this.mContext, "sth_stop_recognition", 1);
      if (paramIRecognitionStatusCallback == null)
      {
        Slog.e("SoundTriggerHelper", "Null callback received for stopKeyphraseRecognition() for keyphraseId:" + paramInt);
        return Integer.MIN_VALUE;
      }
      ModelData localModelData = getKeyphraseModelDataLocked(paramInt);
      if ((localModelData != null) && (localModelData.isKeyphraseModel()))
      {
        paramInt = stopRecognition(localModelData, paramIRecognitionStatusCallback);
        if (paramInt != 0) {
          return paramInt;
        }
      }
      else
      {
        Slog.e("SoundTriggerHelper", "No model exists for given keyphrase Id.");
        return Integer.MIN_VALUE;
      }
      return paramInt;
    }
  }
  
  int unloadGenericSoundModel(UUID paramUUID)
  {
    synchronized (this.mLock)
    {
      MetricsLogger.count(this.mContext, "sth_unload_generic_sound_model", 1);
      if (paramUUID != null)
      {
        localObject2 = this.mModule;
        if (localObject2 != null) {}
      }
      else
      {
        return Integer.MIN_VALUE;
      }
      Object localObject2 = (ModelData)this.mModelDataMap.get(paramUUID);
      if ((localObject2 != null) && (((ModelData)localObject2).isGenericModel()))
      {
        if (!((ModelData)localObject2).isModelLoaded())
        {
          Slog.i("SoundTriggerHelper", "Unload: Given generic model is not loaded:" + paramUUID);
          return 0;
        }
      }
      else
      {
        Slog.w("SoundTriggerHelper", "Unload error: Attempting unload invalid generic model with id:" + paramUUID);
        return Integer.MIN_VALUE;
      }
      if (((ModelData)localObject2).isModelStarted())
      {
        i = stopRecognitionLocked((ModelData)localObject2, false);
        if (i != 0) {
          Slog.w("SoundTriggerHelper", "stopGenericRecognition failed: " + i);
        }
      }
      int i = this.mModule.unloadSoundModel(((ModelData)localObject2).getHandle());
      if (i != 0)
      {
        Slog.w("SoundTriggerHelper", "unloadGenericSoundModel() call failed with " + i);
        Slog.w("SoundTriggerHelper", "unloadGenericSoundModel() force-marking model as unloaded.");
      }
      this.mModelDataMap.remove(paramUUID);
      return i;
    }
  }
  
  int unloadKeyphraseSoundModel(int paramInt)
  {
    synchronized (this.mLock)
    {
      MetricsLogger.count(this.mContext, "sth_unload_keyphrase_sound_model", 1);
      ModelData localModelData = getKeyphraseModelDataLocked(paramInt);
      SoundTriggerModule localSoundTriggerModule = this.mModule;
      if ((localSoundTriggerModule == null) || (localModelData == null)) {}
      while ((localModelData.getHandle() == Integer.MIN_VALUE) || (!localModelData.isKeyphraseModel())) {
        return Integer.MIN_VALUE;
      }
      localModelData.setRequested(false);
      int i = updateRecognitionLocked(localModelData, isRecognitionAllowed(), false);
      if (i != 0) {
        Slog.w("SoundTriggerHelper", "Stop recognition failed for keyphrase ID:" + i);
      }
      i = this.mModule.unloadSoundModel(localModelData.getHandle());
      if (i != 0) {
        Slog.w("SoundTriggerHelper", "unloadKeyphraseSoundModel call failed with " + i);
      }
      removeKeyphraseModelLocked(paramInt);
      return i;
    }
  }
  
  private static class ModelData
  {
    static final int MODEL_LOADED = 1;
    static final int MODEL_NOTLOADED = 0;
    static final int MODEL_STARTED = 2;
    private IRecognitionStatusCallback mCallback = null;
    private int mModelHandle = Integer.MIN_VALUE;
    private UUID mModelId;
    private int mModelState;
    private int mModelType = -1;
    private SoundTrigger.RecognitionConfig mRecognitionConfig = null;
    private boolean mRequested = false;
    private SoundTrigger.SoundModel mSoundModel = null;
    
    private ModelData(UUID paramUUID, int paramInt)
    {
      this.mModelId = paramUUID;
      this.mModelType = paramInt;
    }
    
    static ModelData createGenericModelData(UUID paramUUID)
    {
      return new ModelData(paramUUID, 1);
    }
    
    static ModelData createKeyphraseModelData(UUID paramUUID)
    {
      return new ModelData(paramUUID, 0);
    }
    
    static ModelData createModelDataOfUnknownType(UUID paramUUID)
    {
      return new ModelData(paramUUID, -1);
    }
    
    /* Error */
    String callbackToString()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: new 58	java/lang/StringBuilder
      //   5: dup
      //   6: invokespecial 59	java/lang/StringBuilder:<init>	()V
      //   9: ldc 61
      //   11: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   14: astore_2
      //   15: aload_0
      //   16: getfield 38	com/android/server/soundtrigger/SoundTriggerHelper$ModelData:mCallback	Landroid/hardware/soundtrigger/IRecognitionStatusCallback;
      //   19: ifnull +26 -> 45
      //   22: aload_0
      //   23: getfield 38	com/android/server/soundtrigger/SoundTriggerHelper$ModelData:mCallback	Landroid/hardware/soundtrigger/IRecognitionStatusCallback;
      //   26: invokeinterface 71 1 0
      //   31: astore_1
      //   32: aload_2
      //   33: aload_1
      //   34: invokevirtual 74	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   37: invokevirtual 77	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   40: astore_1
      //   41: aload_0
      //   42: monitorexit
      //   43: aload_1
      //   44: areturn
      //   45: ldc 79
      //   47: astore_1
      //   48: goto -16 -> 32
      //   51: astore_1
      //   52: aload_0
      //   53: monitorexit
      //   54: aload_1
      //   55: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	56	0	this	ModelData
      //   31	17	1	localObject1	Object
      //   51	4	1	localObject2	Object
      //   14	19	2	localStringBuilder	StringBuilder
      // Exception table:
      //   from	to	target	type
      //   2	32	51	finally
      //   32	41	51	finally
    }
    
    void clearCallback()
    {
      try
      {
        this.mCallback = null;
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    void clearState()
    {
      try
      {
        this.mModelState = 0;
        this.mModelHandle = Integer.MIN_VALUE;
        this.mRecognitionConfig = null;
        this.mRequested = false;
        this.mCallback = null;
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    IRecognitionStatusCallback getCallback()
    {
      try
      {
        IRecognitionStatusCallback localIRecognitionStatusCallback = this.mCallback;
        return localIRecognitionStatusCallback;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    int getHandle()
    {
      try
      {
        int i = this.mModelHandle;
        return i;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    UUID getModelId()
    {
      try
      {
        UUID localUUID = this.mModelId;
        return localUUID;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    int getModelType()
    {
      try
      {
        int i = this.mModelType;
        return i;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    SoundTrigger.RecognitionConfig getRecognitionConfig()
    {
      try
      {
        SoundTrigger.RecognitionConfig localRecognitionConfig = this.mRecognitionConfig;
        return localRecognitionConfig;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    SoundTrigger.SoundModel getSoundModel()
    {
      try
      {
        SoundTrigger.SoundModel localSoundModel = this.mSoundModel;
        return localSoundModel;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    /* Error */
    boolean isGenericModel()
    {
      // Byte code:
      //   0: iconst_1
      //   1: istore_2
      //   2: aload_0
      //   3: monitorenter
      //   4: aload_0
      //   5: getfield 36	com/android/server/soundtrigger/SoundTriggerHelper$ModelData:mModelType	I
      //   8: istore_1
      //   9: iload_1
      //   10: iconst_1
      //   11: if_icmpne +7 -> 18
      //   14: aload_0
      //   15: monitorexit
      //   16: iload_2
      //   17: ireturn
      //   18: iconst_0
      //   19: istore_2
      //   20: goto -6 -> 14
      //   23: astore_3
      //   24: aload_0
      //   25: monitorexit
      //   26: aload_3
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	ModelData
      //   8	4	1	i	int
      //   1	19	2	bool	boolean
      //   23	4	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   4	9	23	finally
    }
    
    boolean isKeyphraseModel()
    {
      boolean bool = false;
      try
      {
        int i = this.mModelType;
        if (i == 0) {
          bool = true;
        }
        return bool;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    /* Error */
    boolean isModelLoaded()
    {
      // Byte code:
      //   0: iconst_1
      //   1: istore_3
      //   2: aload_0
      //   3: monitorenter
      //   4: iload_3
      //   5: istore_2
      //   6: aload_0
      //   7: getfield 83	com/android/server/soundtrigger/SoundTriggerHelper$ModelData:mModelState	I
      //   10: iconst_1
      //   11: if_icmpeq +15 -> 26
      //   14: aload_0
      //   15: getfield 83	com/android/server/soundtrigger/SoundTriggerHelper$ModelData:mModelState	I
      //   18: istore_1
      //   19: iload_1
      //   20: iconst_2
      //   21: if_icmpne +9 -> 30
      //   24: iload_3
      //   25: istore_2
      //   26: aload_0
      //   27: monitorexit
      //   28: iload_2
      //   29: ireturn
      //   30: iconst_0
      //   31: istore_2
      //   32: goto -6 -> 26
      //   35: astore 4
      //   37: aload_0
      //   38: monitorexit
      //   39: aload 4
      //   41: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	42	0	this	ModelData
      //   18	4	1	i	int
      //   5	27	2	bool1	boolean
      //   1	24	3	bool2	boolean
      //   35	5	4	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   6	19	35	finally
    }
    
    boolean isModelNotLoaded()
    {
      boolean bool = false;
      try
      {
        int i = this.mModelState;
        if (i == 0) {
          bool = true;
        }
        return bool;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    /* Error */
    boolean isModelStarted()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 83	com/android/server/soundtrigger/SoundTriggerHelper$ModelData:mModelState	I
      //   6: istore_1
      //   7: iload_1
      //   8: iconst_2
      //   9: if_icmpne +9 -> 18
      //   12: iconst_1
      //   13: istore_2
      //   14: aload_0
      //   15: monitorexit
      //   16: iload_2
      //   17: ireturn
      //   18: iconst_0
      //   19: istore_2
      //   20: goto -6 -> 14
      //   23: astore_3
      //   24: aload_0
      //   25: monitorexit
      //   26: aload_3
      //   27: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	28	0	this	ModelData
      //   6	4	1	i	int
      //   13	7	2	bool	boolean
      //   23	4	3	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   2	7	23	finally
    }
    
    boolean isRequested()
    {
      try
      {
        boolean bool = this.mRequested;
        return bool;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    String modelTypeToString()
    {
      String str1 = null;
      for (;;)
      {
        try
        {
          switch (this.mModelType)
          {
          case 1: 
            str1 = "Model type: " + str1 + "\n";
            return str1;
          }
        }
        finally {}
        str1 = "Generic";
        continue;
        str1 = "Keyphrase";
        continue;
        continue;
        String str2 = "Unknown";
      }
    }
    
    /* Error */
    String requestedToString()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: new 58	java/lang/StringBuilder
      //   5: dup
      //   6: invokespecial 59	java/lang/StringBuilder:<init>	()V
      //   9: ldc 115
      //   11: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   14: astore_2
      //   15: aload_0
      //   16: getfield 34	com/android/server/soundtrigger/SoundTriggerHelper$ModelData:mRequested	Z
      //   19: ifeq +19 -> 38
      //   22: ldc 117
      //   24: astore_1
      //   25: aload_2
      //   26: aload_1
      //   27: invokevirtual 65	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   30: invokevirtual 77	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   33: astore_1
      //   34: aload_0
      //   35: monitorexit
      //   36: aload_1
      //   37: areturn
      //   38: ldc 119
      //   40: astore_1
      //   41: goto -16 -> 25
      //   44: astore_1
      //   45: aload_0
      //   46: monitorexit
      //   47: aload_1
      //   48: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	49	0	this	ModelData
      //   24	17	1	str	String
      //   44	4	1	localObject	Object
      //   14	12	2	localStringBuilder	StringBuilder
      // Exception table:
      //   from	to	target	type
      //   2	22	44	finally
      //   25	34	44	finally
    }
    
    void setCallback(IRecognitionStatusCallback paramIRecognitionStatusCallback)
    {
      try
      {
        this.mCallback = paramIRecognitionStatusCallback;
        return;
      }
      finally
      {
        paramIRecognitionStatusCallback = finally;
        throw paramIRecognitionStatusCallback;
      }
    }
    
    void setHandle(int paramInt)
    {
      try
      {
        this.mModelHandle = paramInt;
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    void setLoaded()
    {
      try
      {
        this.mModelState = 1;
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    void setRecognitionConfig(SoundTrigger.RecognitionConfig paramRecognitionConfig)
    {
      try
      {
        this.mRecognitionConfig = paramRecognitionConfig;
        return;
      }
      finally
      {
        paramRecognitionConfig = finally;
        throw paramRecognitionConfig;
      }
    }
    
    void setRequested(boolean paramBoolean)
    {
      try
      {
        this.mRequested = paramBoolean;
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    void setSoundModel(SoundTrigger.SoundModel paramSoundModel)
    {
      try
      {
        this.mSoundModel = paramSoundModel;
        return;
      }
      finally
      {
        paramSoundModel = finally;
        throw paramSoundModel;
      }
    }
    
    void setStarted()
    {
      try
      {
        this.mModelState = 2;
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    void setStopped()
    {
      try
      {
        this.mModelState = 1;
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    String stateToString()
    {
      for (;;)
      {
        try
        {
          switch (this.mModelState)
          {
          case 0: 
            return "Unknown state";
          }
        }
        finally
        {
          localObject = finally;
          throw ((Throwable)localObject);
        }
        return "NOT_LOADED";
        return "LOADED";
        return "STARTED";
      }
    }
    
    public String toString()
    {
      try
      {
        String str = "Handle: " + this.mModelHandle + "\n" + "ModelState: " + stateToString() + "\n" + requestedToString() + "\n" + callbackToString() + "\n" + uuidToString() + "\n" + modelTypeToString();
        return str;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    String uuidToString()
    {
      try
      {
        String str = "UUID: " + this.mModelId;
        return str;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
  }
  
  class MyCallStateListener
    extends PhoneStateListener
  {
    MyCallStateListener() {}
    
    public void onCallStateChanged(int paramInt, String arg2)
    {
      boolean bool = false;
      synchronized (SoundTriggerHelper.-get0(SoundTriggerHelper.this))
      {
        SoundTriggerHelper localSoundTriggerHelper = SoundTriggerHelper.this;
        if (paramInt != 0) {
          bool = true;
        }
        SoundTriggerHelper.-wrap0(localSoundTriggerHelper, bool);
        return;
      }
    }
  }
  
  class PowerSaveModeListener
    extends BroadcastReceiver
  {
    PowerSaveModeListener() {}
    
    public void onReceive(Context arg1, Intent paramIntent)
    {
      if (!"android.os.action.POWER_SAVE_MODE_CHANGED".equals(paramIntent.getAction())) {
        return;
      }
      boolean bool = SoundTriggerHelper.-get1(SoundTriggerHelper.this).isPowerSaveMode();
      synchronized (SoundTriggerHelper.-get0(SoundTriggerHelper.this))
      {
        SoundTriggerHelper.-wrap1(SoundTriggerHelper.this, bool);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/soundtrigger/SoundTriggerHelper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */