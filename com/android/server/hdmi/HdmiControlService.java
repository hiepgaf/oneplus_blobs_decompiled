package com.android.server.hdmi;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiHotplugEvent;
import android.hardware.hdmi.HdmiPortInfo;
import android.hardware.hdmi.IHdmiControlCallback;
import android.hardware.hdmi.IHdmiControlCallback.Stub;
import android.hardware.hdmi.IHdmiControlService.Stub;
import android.hardware.hdmi.IHdmiDeviceEventListener;
import android.hardware.hdmi.IHdmiHotplugEventListener;
import android.hardware.hdmi.IHdmiInputChangeListener;
import android.hardware.hdmi.IHdmiMhlVendorCommandListener;
import android.hardware.hdmi.IHdmiRecordListener;
import android.hardware.hdmi.IHdmiSystemAudioModeChangeListener;
import android.hardware.hdmi.IHdmiVendorCommandListener;
import android.media.AudioManager;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputManager.TvInputCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings.Global;
import android.text.TextUtils.SimpleStringSplitter;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.SystemService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import libcore.util.EmptyArray;

public final class HdmiControlService
  extends SystemService
{
  static final int INITIATED_BY_BOOT_UP = 1;
  static final int INITIATED_BY_ENABLE_CEC = 0;
  static final int INITIATED_BY_HOTPLUG = 4;
  static final int INITIATED_BY_SCREEN_ON = 2;
  static final int INITIATED_BY_WAKE_UP_MESSAGE = 3;
  static final String PERMISSION = "android.permission.HDMI_CEC";
  static final int STANDBY_SCREEN_OFF = 0;
  static final int STANDBY_SHUTDOWN = 1;
  private static final String TAG = "HdmiControlService";
  private final Locale HONG_KONG = new Locale("zh", "HK");
  private final Locale MACAU = new Locale("zh", "MO");
  @HdmiAnnotations.ServiceThreadOnly
  private int mActivePortId = -1;
  private boolean mAddressAllocated = false;
  private HdmiCecController mCecController;
  private final CecMessageBuffer mCecMessageBuffer = new CecMessageBuffer(null);
  @GuardedBy("mLock")
  private final ArrayList<DeviceEventListenerRecord> mDeviceEventListenerRecords = new ArrayList();
  private final Handler mHandler = new Handler();
  private final HdmiControlBroadcastReceiver mHdmiControlBroadcastReceiver = new HdmiControlBroadcastReceiver(null);
  @GuardedBy("mLock")
  private boolean mHdmiControlEnabled;
  @GuardedBy("mLock")
  private final ArrayList<HotplugEventListenerRecord> mHotplugEventListenerRecords = new ArrayList();
  @GuardedBy("mLock")
  private InputChangeListenerRecord mInputChangeListenerRecord;
  private final HandlerThread mIoThread = new HandlerThread("Hdmi Control Io Thread");
  @HdmiAnnotations.ServiceThreadOnly
  private String mLanguage = Locale.getDefault().getISO3Language();
  @HdmiAnnotations.ServiceThreadOnly
  private int mLastInputMhl = -1;
  private final List<Integer> mLocalDevices = getIntList(SystemProperties.get("ro.hdmi.device_type"));
  private final Object mLock = new Object();
  private HdmiCecMessageValidator mMessageValidator;
  private HdmiMhlControllerStub mMhlController;
  @GuardedBy("mLock")
  private List<HdmiDeviceInfo> mMhlDevices;
  @GuardedBy("mLock")
  private boolean mMhlInputChangeEnabled;
  @GuardedBy("mLock")
  private final ArrayList<HdmiMhlVendorCommandListenerRecord> mMhlVendorCommandListenerRecords = new ArrayList();
  private UnmodifiableSparseArray<HdmiDeviceInfo> mPortDeviceMap;
  private UnmodifiableSparseIntArray mPortIdMap;
  private List<HdmiPortInfo> mPortInfo;
  private UnmodifiableSparseArray<HdmiPortInfo> mPortInfoMap;
  private PowerManager mPowerManager;
  @HdmiAnnotations.ServiceThreadOnly
  private int mPowerStatus = 1;
  @GuardedBy("mLock")
  private boolean mProhibitMode;
  @GuardedBy("mLock")
  private HdmiRecordListenerRecord mRecordListenerRecord;
  private final SelectRequestBuffer mSelectRequestBuffer = new SelectRequestBuffer();
  private final SettingsObserver mSettingsObserver = new SettingsObserver(this.mHandler);
  @HdmiAnnotations.ServiceThreadOnly
  private boolean mStandbyMessageReceived = false;
  private final ArrayList<SystemAudioModeChangeListenerRecord> mSystemAudioModeChangeListenerRecords = new ArrayList();
  private TvInputManager mTvInputManager;
  @GuardedBy("mLock")
  private final ArrayList<VendorCommandListenerRecord> mVendorCommandListenerRecords = new ArrayList();
  @HdmiAnnotations.ServiceThreadOnly
  private boolean mWakeUpMessageReceived = false;
  
  public HdmiControlService(Context paramContext)
  {
    super(paramContext);
  }
  
  /* Error */
  private void addDeviceEventListener(IHdmiDeviceEventListener arg1)
  {
    // Byte code:
    //   0: new 60	com/android/server/hdmi/HdmiControlService$DeviceEventListenerRecord
    //   3: dup
    //   4: aload_0
    //   5: aload_1
    //   6: invokespecial 484	com/android/server/hdmi/HdmiControlService$DeviceEventListenerRecord:<init>	(Lcom/android/server/hdmi/HdmiControlService;Landroid/hardware/hdmi/IHdmiDeviceEventListener;)V
    //   9: astore_2
    //   10: aload_1
    //   11: invokeinterface 490 1 0
    //   16: aload_2
    //   17: iconst_0
    //   18: invokeinterface 496 3 0
    //   23: aload_0
    //   24: getfield 201	com/android/server/hdmi/HdmiControlService:mLock	Ljava/lang/Object;
    //   27: astore_1
    //   28: aload_1
    //   29: monitorenter
    //   30: aload_0
    //   31: getfield 251	com/android/server/hdmi/HdmiControlService:mDeviceEventListenerRecords	Ljava/util/ArrayList;
    //   34: aload_2
    //   35: invokevirtual 500	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   38: pop
    //   39: aload_1
    //   40: monitorexit
    //   41: return
    //   42: astore_1
    //   43: ldc 111
    //   45: ldc_w 502
    //   48: invokestatic 508	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   51: pop
    //   52: return
    //   53: astore_2
    //   54: aload_1
    //   55: monitorexit
    //   56: aload_2
    //   57: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	58	0	this	HdmiControlService
    //   9	26	2	localDeviceEventListenerRecord	DeviceEventListenerRecord
    //   53	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	23	42	android/os/RemoteException
    //   30	39	53	finally
  }
  
  /* Error */
  private void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener arg1)
  {
    // Byte code:
    //   0: new 69	com/android/server/hdmi/HdmiControlService$HdmiMhlVendorCommandListenerRecord
    //   3: dup
    //   4: aload_0
    //   5: aload_1
    //   6: invokespecial 510	com/android/server/hdmi/HdmiControlService$HdmiMhlVendorCommandListenerRecord:<init>	(Lcom/android/server/hdmi/HdmiControlService;Landroid/hardware/hdmi/IHdmiMhlVendorCommandListener;)V
    //   9: astore_2
    //   10: aload_1
    //   11: invokeinterface 513 1 0
    //   16: aload_2
    //   17: iconst_0
    //   18: invokeinterface 496 3 0
    //   23: aload_0
    //   24: getfield 201	com/android/server/hdmi/HdmiControlService:mLock	Ljava/lang/Object;
    //   27: astore_1
    //   28: aload_1
    //   29: monitorenter
    //   30: aload_0
    //   31: getfield 209	com/android/server/hdmi/HdmiControlService:mMhlVendorCommandListenerRecords	Ljava/util/ArrayList;
    //   34: aload_2
    //   35: invokevirtual 500	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   38: pop
    //   39: aload_1
    //   40: monitorexit
    //   41: return
    //   42: astore_1
    //   43: ldc 111
    //   45: ldc_w 515
    //   48: invokestatic 508	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   51: pop
    //   52: return
    //   53: astore_2
    //   54: aload_1
    //   55: monitorexit
    //   56: aload_2
    //   57: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	58	0	this	HdmiControlService
    //   9	26	2	localHdmiMhlVendorCommandListenerRecord	HdmiMhlVendorCommandListenerRecord
    //   53	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	23	42	android/os/RemoteException
    //   30	39	53	finally
  }
  
  /* Error */
  private void addHotplugEventListener(final IHdmiHotplugEventListener paramIHdmiHotplugEventListener)
  {
    // Byte code:
    //   0: new 75	com/android/server/hdmi/HdmiControlService$HotplugEventListenerRecord
    //   3: dup
    //   4: aload_0
    //   5: aload_1
    //   6: invokespecial 517	com/android/server/hdmi/HdmiControlService$HotplugEventListenerRecord:<init>	(Lcom/android/server/hdmi/HdmiControlService;Landroid/hardware/hdmi/IHdmiHotplugEventListener;)V
    //   9: astore_3
    //   10: aload_1
    //   11: invokeinterface 520 1 0
    //   16: aload_3
    //   17: iconst_0
    //   18: invokeinterface 496 3 0
    //   23: aload_0
    //   24: getfield 201	com/android/server/hdmi/HdmiControlService:mLock	Ljava/lang/Object;
    //   27: astore_2
    //   28: aload_2
    //   29: monitorenter
    //   30: aload_0
    //   31: getfield 257	com/android/server/hdmi/HdmiControlService:mHotplugEventListenerRecords	Ljava/util/ArrayList;
    //   34: aload_3
    //   35: invokevirtual 500	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   38: pop
    //   39: aload_2
    //   40: monitorexit
    //   41: aload_0
    //   42: new 8	com/android/server/hdmi/HdmiControlService$2
    //   45: dup
    //   46: aload_0
    //   47: aload_3
    //   48: aload_1
    //   49: invokespecial 523	com/android/server/hdmi/HdmiControlService$2:<init>	(Lcom/android/server/hdmi/HdmiControlService;Lcom/android/server/hdmi/HdmiControlService$HotplugEventListenerRecord;Landroid/hardware/hdmi/IHdmiHotplugEventListener;)V
    //   52: invokevirtual 527	com/android/server/hdmi/HdmiControlService:runOnServiceThread	(Ljava/lang/Runnable;)V
    //   55: return
    //   56: astore_1
    //   57: ldc 111
    //   59: ldc_w 502
    //   62: invokestatic 508	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   65: pop
    //   66: return
    //   67: astore_1
    //   68: aload_2
    //   69: monitorexit
    //   70: aload_1
    //   71: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	HdmiControlService
    //   0	72	1	paramIHdmiHotplugEventListener	IHdmiHotplugEventListener
    //   9	39	3	localHotplugEventListenerRecord	HotplugEventListenerRecord
    // Exception table:
    //   from	to	target	type
    //   10	23	56	android/os/RemoteException
    //   30	39	67	finally
  }
  
  /* Error */
  private void addSystemAudioModeChangeListner(IHdmiSystemAudioModeChangeListener arg1)
  {
    // Byte code:
    //   0: new 87	com/android/server/hdmi/HdmiControlService$SystemAudioModeChangeListenerRecord
    //   3: dup
    //   4: aload_0
    //   5: aload_1
    //   6: invokespecial 529	com/android/server/hdmi/HdmiControlService$SystemAudioModeChangeListenerRecord:<init>	(Lcom/android/server/hdmi/HdmiControlService;Landroid/hardware/hdmi/IHdmiSystemAudioModeChangeListener;)V
    //   9: astore_2
    //   10: aload_1
    //   11: invokeinterface 532 1 0
    //   16: aload_2
    //   17: iconst_0
    //   18: invokeinterface 496 3 0
    //   23: aload_0
    //   24: getfield 201	com/android/server/hdmi/HdmiControlService:mLock	Ljava/lang/Object;
    //   27: astore_1
    //   28: aload_1
    //   29: monitorenter
    //   30: aload_0
    //   31: getfield 234	com/android/server/hdmi/HdmiControlService:mSystemAudioModeChangeListenerRecords	Ljava/util/ArrayList;
    //   34: aload_2
    //   35: invokevirtual 500	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   38: pop
    //   39: aload_1
    //   40: monitorexit
    //   41: return
    //   42: astore_1
    //   43: ldc 111
    //   45: ldc_w 502
    //   48: invokestatic 508	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   51: pop
    //   52: return
    //   53: astore_2
    //   54: aload_1
    //   55: monitorexit
    //   56: aload_2
    //   57: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	58	0	this	HdmiControlService
    //   9	26	2	localSystemAudioModeChangeListenerRecord	SystemAudioModeChangeListenerRecord
    //   53	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	23	42	android/os/RemoteException
    //   30	39	53	finally
  }
  
  /* Error */
  private void addVendorCommandListener(IHdmiVendorCommandListener arg1, int paramInt)
  {
    // Byte code:
    //   0: new 90	com/android/server/hdmi/HdmiControlService$VendorCommandListenerRecord
    //   3: dup
    //   4: aload_0
    //   5: aload_1
    //   6: iload_2
    //   7: invokespecial 534	com/android/server/hdmi/HdmiControlService$VendorCommandListenerRecord:<init>	(Lcom/android/server/hdmi/HdmiControlService;Landroid/hardware/hdmi/IHdmiVendorCommandListener;I)V
    //   10: astore_3
    //   11: aload_1
    //   12: invokeinterface 537 1 0
    //   17: aload_3
    //   18: iconst_0
    //   19: invokeinterface 496 3 0
    //   24: aload_0
    //   25: getfield 201	com/android/server/hdmi/HdmiControlService:mLock	Ljava/lang/Object;
    //   28: astore_1
    //   29: aload_1
    //   30: monitorenter
    //   31: aload_0
    //   32: getfield 237	com/android/server/hdmi/HdmiControlService:mVendorCommandListenerRecords	Ljava/util/ArrayList;
    //   35: aload_3
    //   36: invokevirtual 500	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   39: pop
    //   40: aload_1
    //   41: monitorexit
    //   42: return
    //   43: astore_1
    //   44: ldc 111
    //   46: ldc_w 502
    //   49: invokestatic 508	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   52: pop
    //   53: return
    //   54: astore_3
    //   55: aload_1
    //   56: monitorexit
    //   57: aload_3
    //   58: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	59	0	this	HdmiControlService
    //   0	59	2	paramInt	int
    //   10	26	3	localVendorCommandListenerRecord	VendorCommandListenerRecord
    //   54	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   11	24	43	android/os/RemoteException
    //   31	40	54	finally
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void allocateLogicalAddress(final ArrayList<HdmiCecLocalDevice> paramArrayList, final int paramInt)
  {
    assertRunOnServiceThread();
    this.mCecController.clearLogicalAddress();
    final ArrayList localArrayList = new ArrayList();
    final int[] arrayOfInt = new int[1];
    this.mAddressAllocated = paramArrayList.isEmpty();
    this.mSelectRequestBuffer.clear();
    Iterator localIterator = paramArrayList.iterator();
    while (localIterator.hasNext())
    {
      final HdmiCecLocalDevice localHdmiCecLocalDevice = (HdmiCecLocalDevice)localIterator.next();
      this.mCecController.allocateLogicalAddress(localHdmiCecLocalDevice.getType(), localHdmiCecLocalDevice.getPreferredAddress(), new HdmiCecController.AllocateAddressCallback()
      {
        public void onAllocated(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if (paramAnonymousInt2 == 15) {
            Slog.e("HdmiControlService", "Failed to allocate address:[device_type:" + paramAnonymousInt1 + "]");
          }
          for (;;)
          {
            paramAnonymousInt1 = paramArrayList.size();
            Object localObject = arrayOfInt;
            paramAnonymousInt2 = localObject[0] + 1;
            localObject[0] = paramAnonymousInt2;
            if (paramAnonymousInt1 == paramAnonymousInt2)
            {
              HdmiControlService.-set0(HdmiControlService.this, true);
              if (paramInt != 4) {
                HdmiControlService.-wrap15(HdmiControlService.this, paramInt);
              }
              HdmiControlService.-wrap14(HdmiControlService.this, localArrayList, paramInt);
              HdmiControlService.-get5(HdmiControlService.this).processMessages();
            }
            return;
            localObject = HdmiControlService.-wrap0(HdmiControlService.this, paramAnonymousInt2, paramAnonymousInt1, 0);
            localHdmiCecLocalDevice.setDeviceInfo((HdmiDeviceInfo)localObject);
            HdmiControlService.-get4(HdmiControlService.this).addLocalDevice(paramAnonymousInt1, localHdmiCecLocalDevice);
            HdmiControlService.-get4(HdmiControlService.this).addLogicalAddress(paramAnonymousInt2);
            localArrayList.add(localHdmiCecLocalDevice);
          }
        }
      });
    }
  }
  
  private void announceHotplugEvent(int paramInt, boolean paramBoolean)
  {
    HdmiHotplugEvent localHdmiHotplugEvent = new HdmiHotplugEvent(paramInt, paramBoolean);
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mHotplugEventListenerRecords.iterator();
      if (localIterator.hasNext()) {
        invokeHotplugEventListenerLocked(HotplugEventListenerRecord.-get0((HotplugEventListenerRecord)localIterator.next()), localHdmiHotplugEvent);
      }
    }
  }
  
  private void assertRunOnServiceThread()
  {
    if (Looper.myLooper() != this.mHandler.getLooper()) {
      throw new IllegalStateException("Should run on service thread.");
    }
  }
  
  private boolean canGoToStandby()
  {
    Iterator localIterator = this.mCecController.getLocalDeviceList().iterator();
    while (localIterator.hasNext()) {
      if (!((HdmiCecLocalDevice)localIterator.next()).canGoToStandby()) {
        return false;
      }
    }
    return true;
  }
  
  private int checkPollStrategy(int paramInt)
  {
    int i = paramInt & 0x3;
    if (i == 0) {
      throw new IllegalArgumentException("Invalid poll strategy:" + paramInt);
    }
    int j = paramInt & 0x30000;
    if (j == 0) {
      throw new IllegalArgumentException("Invalid iteration strategy:" + paramInt);
    }
    return i | j;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void clearLocalDevices()
  {
    assertRunOnServiceThread();
    if (this.mCecController == null) {
      return;
    }
    this.mCecController.clearLogicalAddress();
    this.mCecController.clearLocalDevices();
  }
  
  private HdmiDeviceInfo createDeviceInfo(int paramInt1, int paramInt2, int paramInt3)
  {
    String str = Build.MODEL;
    return new HdmiDeviceInfo(paramInt1, getPhysicalAddress(), pathToPortId(getPhysicalAddress()), paramInt2, getVendorId(), str);
  }
  
  private void disableDevices(HdmiCecLocalDevice.PendingActionClearedCallback paramPendingActionClearedCallback)
  {
    if (this.mCecController != null)
    {
      Iterator localIterator = this.mCecController.getLocalDeviceList().iterator();
      while (localIterator.hasNext()) {
        ((HdmiCecLocalDevice)localIterator.next()).disableDevice(this.mStandbyMessageReceived, paramPendingActionClearedCallback);
      }
    }
    this.mMhlController.clearAllLocalDevices();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void disableHdmiControlService()
  {
    disableDevices(new HdmiCecLocalDevice.PendingActionClearedCallback()
    {
      public void onCleared(HdmiCecLocalDevice paramAnonymousHdmiCecLocalDevice)
      {
        HdmiControlService.-wrap8(HdmiControlService.this);
        HdmiControlService.-get4(HdmiControlService.this).flush(new Runnable()
        {
          public void run()
          {
            HdmiControlService.-get4(HdmiControlService.this).setOption(2, 0);
            HdmiControlService.-get13(HdmiControlService.this).setOption(103, 0);
            HdmiControlService.-wrap9(HdmiControlService.this);
          }
        });
      }
    });
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private boolean dispatchMessageToLocalDevice(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    Iterator localIterator = this.mCecController.getLocalDeviceList().iterator();
    while (localIterator.hasNext()) {
      if ((((HdmiCecLocalDevice)localIterator.next()).dispatchMessage(paramHdmiCecMessage)) && (paramHdmiCecMessage.getDestination() != 15)) {
        return true;
      }
    }
    if (paramHdmiCecMessage.getDestination() != 15) {
      HdmiLogger.warning("Unhandled cec command:" + paramHdmiCecMessage, new Object[0]);
    }
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void enableHdmiControlService()
  {
    this.mCecController.setOption(2, 1);
    this.mMhlController.setOption(103, 1);
    initializeCec(0);
  }
  
  private void enforceAccessPermission()
  {
    getContext().enforceCallingOrSelfPermission("android.permission.HDMI_CEC", "HdmiControlService");
  }
  
  private static List<Integer> getIntList(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject = new TextUtils.SimpleStringSplitter(',');
    ((TextUtils.SimpleStringSplitter)localObject).setString(paramString);
    paramString = ((Iterable)localObject).iterator();
    while (paramString.hasNext())
    {
      localObject = (String)paramString.next();
      try
      {
        localArrayList.add(Integer.valueOf(Integer.parseInt((String)localObject)));
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Slog.w("HdmiControlService", "Can't parseInt: " + (String)localObject);
      }
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  private List<HdmiDeviceInfo> getMhlDevicesLocked()
  {
    return this.mMhlDevices;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void initPortInfo()
  {
    assertRunOnServiceThread();
    HdmiPortInfo[] arrayOfHdmiPortInfo = null;
    if (this.mCecController != null) {
      arrayOfHdmiPortInfo = this.mCecController.getPortInfos();
    }
    if (arrayOfHdmiPortInfo == null) {
      return;
    }
    Object localObject1 = new SparseArray();
    Object localObject2 = new SparseIntArray();
    Object localObject3 = new SparseArray();
    int i = 0;
    int j = arrayOfHdmiPortInfo.length;
    while (i < j)
    {
      HdmiPortInfo localHdmiPortInfo = arrayOfHdmiPortInfo[i];
      ((SparseIntArray)localObject2).put(localHdmiPortInfo.getAddress(), localHdmiPortInfo.getId());
      ((SparseArray)localObject1).put(localHdmiPortInfo.getId(), localHdmiPortInfo);
      ((SparseArray)localObject3).put(localHdmiPortInfo.getId(), new HdmiDeviceInfo(localHdmiPortInfo.getAddress(), localHdmiPortInfo.getId()));
      i += 1;
    }
    this.mPortIdMap = new UnmodifiableSparseIntArray((SparseIntArray)localObject2);
    this.mPortInfoMap = new UnmodifiableSparseArray((SparseArray)localObject1);
    this.mPortDeviceMap = new UnmodifiableSparseArray((SparseArray)localObject3);
    localObject2 = this.mMhlController.getPortInfos();
    localObject1 = new ArraySet(localObject2.length);
    i = 0;
    j = localObject2.length;
    while (i < j)
    {
      localObject3 = localObject2[i];
      if (((HdmiPortInfo)localObject3).isMhlSupported()) {
        ((ArraySet)localObject1).add(Integer.valueOf(((HdmiPortInfo)localObject3).getId()));
      }
      i += 1;
    }
    if (((ArraySet)localObject1).isEmpty())
    {
      this.mPortInfo = Collections.unmodifiableList(Arrays.asList(arrayOfHdmiPortInfo));
      return;
    }
    localObject2 = new ArrayList(arrayOfHdmiPortInfo.length);
    j = arrayOfHdmiPortInfo.length;
    i = 0;
    if (i < j)
    {
      localObject3 = arrayOfHdmiPortInfo[i];
      if (((ArraySet)localObject1).contains(Integer.valueOf(((HdmiPortInfo)localObject3).getId()))) {
        ((ArrayList)localObject2).add(new HdmiPortInfo(((HdmiPortInfo)localObject3).getId(), ((HdmiPortInfo)localObject3).getType(), ((HdmiPortInfo)localObject3).getAddress(), ((HdmiPortInfo)localObject3).isCecSupported(), true, ((HdmiPortInfo)localObject3).isArcSupported()));
      }
      for (;;)
      {
        i += 1;
        break;
        ((ArrayList)localObject2).add(localObject3);
      }
    }
    this.mPortInfo = Collections.unmodifiableList((List)localObject2);
  }
  
  private void initializeCec(int paramInt)
  {
    this.mAddressAllocated = false;
    this.mCecController.setOption(3, 1);
    this.mCecController.setOption(5, HdmiUtils.languageToInt(this.mLanguage));
    initializeLocalDevices(paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void initializeLocalDevices(int paramInt)
  {
    assertRunOnServiceThread();
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mLocalDevices.iterator();
    while (localIterator.hasNext())
    {
      int i = ((Integer)localIterator.next()).intValue();
      HdmiCecLocalDevice localHdmiCecLocalDevice2 = this.mCecController.getLocalDevice(i);
      HdmiCecLocalDevice localHdmiCecLocalDevice1 = localHdmiCecLocalDevice2;
      if (localHdmiCecLocalDevice2 == null) {
        localHdmiCecLocalDevice1 = HdmiCecLocalDevice.create(this, i);
      }
      localHdmiCecLocalDevice1.init();
      localArrayList.add(localHdmiCecLocalDevice1);
    }
    clearLocalDevices();
    allocateLogicalAddress(localArrayList, paramInt);
  }
  
  private void invokeCallback(IHdmiControlCallback paramIHdmiControlCallback, int paramInt)
  {
    try
    {
      paramIHdmiControlCallback.onComplete(paramInt);
      return;
    }
    catch (RemoteException paramIHdmiControlCallback)
    {
      Slog.e("HdmiControlService", "Invoking callback failed:" + paramIHdmiControlCallback);
    }
  }
  
  private void invokeHotplugEventListenerLocked(IHdmiHotplugEventListener paramIHdmiHotplugEventListener, HdmiHotplugEvent paramHdmiHotplugEvent)
  {
    try
    {
      paramIHdmiHotplugEventListener.onReceived(paramHdmiHotplugEvent);
      return;
    }
    catch (RemoteException paramIHdmiHotplugEventListener)
    {
      Slog.e("HdmiControlService", "Failed to report hotplug event:" + paramHdmiHotplugEvent.toString(), paramIHdmiHotplugEventListener);
    }
  }
  
  private void invokeSystemAudioModeChangeLocked(IHdmiSystemAudioModeChangeListener paramIHdmiSystemAudioModeChangeListener, boolean paramBoolean)
  {
    try
    {
      paramIHdmiSystemAudioModeChangeListener.onStatusChanged(paramBoolean);
      return;
    }
    catch (RemoteException paramIHdmiSystemAudioModeChangeListener)
    {
      Slog.e("HdmiControlService", "Invoking callback failed:" + paramIHdmiSystemAudioModeChangeListener);
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void notifyAddressAllocated(ArrayList<HdmiCecLocalDevice> paramArrayList, int paramInt)
  {
    assertRunOnServiceThread();
    paramArrayList = paramArrayList.iterator();
    while (paramArrayList.hasNext())
    {
      HdmiCecLocalDevice localHdmiCecLocalDevice = (HdmiCecLocalDevice)paramArrayList.next();
      localHdmiCecLocalDevice.handleAddressAllocated(localHdmiCecLocalDevice.getDeviceInfo().getLogicalAddress(), paramInt);
    }
    if (isTvDeviceEnabled()) {
      tv().setSelectRequestBuffer(this.mSelectRequestBuffer);
    }
  }
  
  private void onInitializeCecComplete(int paramInt)
  {
    if (this.mPowerStatus == 2) {
      this.mPowerStatus = 0;
    }
    this.mWakeUpMessageReceived = false;
    if (isTvDeviceEnabled()) {
      this.mCecController.setOption(1, toInt(tv().getAutoWakeup()));
    }
    int i = -1;
    switch (paramInt)
    {
    default: 
      paramInt = i;
    }
    for (;;)
    {
      if (paramInt != -1) {
        invokeVendorCommandListenersOnControlStateChanged(true, paramInt);
      }
      return;
      paramInt = 0;
      continue;
      paramInt = 1;
      continue;
      paramInt = 2;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void onLanguageChanged(String paramString)
  {
    assertRunOnServiceThread();
    this.mLanguage = paramString;
    if (isTvDeviceEnabled())
    {
      tv().broadcastMenuLanguage(paramString);
      this.mCecController.setOption(5, HdmiUtils.languageToInt(paramString));
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void onStandby(final int paramInt)
  {
    assertRunOnServiceThread();
    this.mPowerStatus = 3;
    invokeVendorCommandListenersOnControlStateChanged(false, 3);
    if (!canGoToStandby())
    {
      this.mPowerStatus = 1;
      return;
    }
    disableDevices(new HdmiCecLocalDevice.PendingActionClearedCallback()
    {
      public void onCleared(HdmiCecLocalDevice paramAnonymousHdmiCecLocalDevice)
      {
        Slog.v("HdmiControlService", "On standby-action cleared:" + paramAnonymousHdmiCecLocalDevice.mDeviceType);
        this.val$devices.remove(paramAnonymousHdmiCecLocalDevice);
        if (this.val$devices.isEmpty()) {
          HdmiControlService.-wrap17(HdmiControlService.this, paramInt);
        }
      }
    });
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void onStandbyCompleted(int paramInt)
  {
    assertRunOnServiceThread();
    Slog.v("HdmiControlService", "onStandbyCompleted");
    if (this.mPowerStatus != 3) {
      return;
    }
    this.mPowerStatus = 1;
    Iterator localIterator = this.mCecController.getLocalDeviceList().iterator();
    while (localIterator.hasNext()) {
      ((HdmiCecLocalDevice)localIterator.next()).onStandby(this.mStandbyMessageReceived, paramInt);
    }
    this.mStandbyMessageReceived = false;
    this.mAddressAllocated = false;
    this.mCecController.setOption(3, 0);
    this.mMhlController.setOption(104, 0);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void onWakeUp()
  {
    assertRunOnServiceThread();
    this.mPowerStatus = 2;
    if (this.mCecController != null)
    {
      if (this.mHdmiControlEnabled)
      {
        int i = 2;
        if (this.mWakeUpMessageReceived) {
          i = 3;
        }
        initializeCec(i);
      }
      return;
    }
    Slog.i("HdmiControlService", "Device does not support HDMI-CEC.");
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void oneTouchPlay(IHdmiControlCallback paramIHdmiControlCallback)
  {
    assertRunOnServiceThread();
    HdmiCecLocalDevicePlayback localHdmiCecLocalDevicePlayback = playback();
    if (localHdmiCecLocalDevicePlayback == null)
    {
      Slog.w("HdmiControlService", "Local playback device not available");
      invokeCallback(paramIHdmiControlCallback, 2);
      return;
    }
    localHdmiCecLocalDevicePlayback.oneTouchPlay(paramIHdmiControlCallback);
  }
  
  private HdmiCecLocalDevicePlayback playback()
  {
    return (HdmiCecLocalDevicePlayback)this.mCecController.getLocalDevice(4);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void queryDisplayStatus(IHdmiControlCallback paramIHdmiControlCallback)
  {
    assertRunOnServiceThread();
    HdmiCecLocalDevicePlayback localHdmiCecLocalDevicePlayback = playback();
    if (localHdmiCecLocalDevicePlayback == null)
    {
      Slog.w("HdmiControlService", "Local playback device not available");
      invokeCallback(paramIHdmiControlCallback, 2);
      return;
    }
    localHdmiCecLocalDevicePlayback.queryDisplayStatus(paramIHdmiControlCallback);
  }
  
  private void registerContentObserver()
  {
    ContentResolver localContentResolver = getContext().getContentResolver();
    String[] arrayOfString = new String[5];
    arrayOfString[0] = "hdmi_control_enabled";
    arrayOfString[1] = "hdmi_control_auto_wakeup_enabled";
    arrayOfString[2] = "hdmi_control_auto_device_off_enabled";
    arrayOfString[3] = "mhl_input_switching_enabled";
    arrayOfString[4] = "mhl_power_charge_enabled";
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      localContentResolver.registerContentObserver(Settings.Global.getUriFor(arrayOfString[i]), false, this.mSettingsObserver, -1);
      i += 1;
    }
  }
  
  private void removeHotplugEventListener(IHdmiHotplugEventListener paramIHdmiHotplugEventListener)
  {
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mHotplugEventListenerRecords.iterator();
      while (localIterator.hasNext())
      {
        HotplugEventListenerRecord localHotplugEventListenerRecord = (HotplugEventListenerRecord)localIterator.next();
        if (HotplugEventListenerRecord.-get0(localHotplugEventListenerRecord).asBinder() == paramIHdmiHotplugEventListener.asBinder())
        {
          paramIHdmiHotplugEventListener.asBinder().unlinkToDeath(localHotplugEventListenerRecord, 0);
          this.mHotplugEventListenerRecords.remove(localHotplugEventListenerRecord);
        }
      }
      return;
    }
  }
  
  private void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener paramIHdmiSystemAudioModeChangeListener)
  {
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mSystemAudioModeChangeListenerRecords.iterator();
      while (localIterator.hasNext())
      {
        SystemAudioModeChangeListenerRecord localSystemAudioModeChangeListenerRecord = (SystemAudioModeChangeListenerRecord)localIterator.next();
        if (SystemAudioModeChangeListenerRecord.-get0(localSystemAudioModeChangeListenerRecord).asBinder() == paramIHdmiSystemAudioModeChangeListener)
        {
          paramIHdmiSystemAudioModeChangeListener.asBinder().unlinkToDeath(localSystemAudioModeChangeListenerRecord, 0);
          this.mSystemAudioModeChangeListenerRecords.remove(localSystemAudioModeChangeListenerRecord);
        }
      }
      return;
    }
  }
  
  private void setHdmiRecordListener(IHdmiRecordListener paramIHdmiRecordListener)
  {
    synchronized (this.mLock)
    {
      this.mRecordListenerRecord = new HdmiRecordListenerRecord(paramIHdmiRecordListener);
      try
      {
        paramIHdmiRecordListener.asBinder().linkToDeath(this.mRecordListenerRecord, 0);
        return;
      }
      catch (RemoteException paramIHdmiRecordListener)
      {
        for (;;)
        {
          Slog.w("HdmiControlService", "Listener already died.", paramIHdmiRecordListener);
        }
      }
    }
  }
  
  private void setInputChangeListener(IHdmiInputChangeListener paramIHdmiInputChangeListener)
  {
    synchronized (this.mLock)
    {
      this.mInputChangeListenerRecord = new InputChangeListenerRecord(paramIHdmiInputChangeListener);
      try
      {
        paramIHdmiInputChangeListener.asBinder().linkToDeath(this.mInputChangeListenerRecord, 0);
        return;
      }
      catch (RemoteException paramIHdmiInputChangeListener)
      {
        Slog.w("HdmiControlService", "Listener already died");
        return;
      }
    }
  }
  
  private static int toInt(boolean paramBoolean)
  {
    if (paramBoolean) {
      return 1;
    }
    return 0;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void updateSafeMhlInput()
  {
    assertRunOnServiceThread();
    Object localObject1 = Collections.emptyList();
    SparseArray localSparseArray = this.mMhlController.getAllLocalDevices();
    int i = 0;
    while (i < localSparseArray.size())
    {
      HdmiMhlLocalDeviceStub localHdmiMhlLocalDeviceStub = (HdmiMhlLocalDeviceStub)localSparseArray.valueAt(i);
      ??? = localObject1;
      if (localHdmiMhlLocalDeviceStub.getInfo() != null)
      {
        ??? = localObject1;
        if (((List)localObject1).isEmpty()) {
          ??? = new ArrayList();
        }
        ((List)???).add(localHdmiMhlLocalDeviceStub.getInfo());
      }
      i += 1;
      localObject1 = ???;
    }
    synchronized (this.mLock)
    {
      this.mMhlDevices = ((List)localObject1);
      return;
    }
  }
  
  void announceSystemAudioModeChange(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mSystemAudioModeChangeListenerRecords.iterator();
      if (localIterator.hasNext()) {
        invokeSystemAudioModeChangeLocked(SystemAudioModeChangeListenerRecord.-get0((SystemAudioModeChangeListenerRecord)localIterator.next()), paramBoolean);
      }
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void changeInputForMhl(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    if (tv() == null) {
      return;
    }
    final int i;
    if (paramBoolean)
    {
      i = tv().getActivePortId();
      if (paramInt != -1) {
        tv().doManualPortSwitching(paramInt, new IHdmiControlCallback.Stub()
        {
          public void onComplete(int paramAnonymousInt)
            throws RemoteException
          {
            HdmiControlService.this.setLastInputForMhl(i);
          }
        });
      }
      tv().setActivePortId(paramInt);
      localObject = this.mMhlController.getLocalDevice(paramInt);
      if (localObject == null) {
        break label88;
      }
    }
    label88:
    for (Object localObject = ((HdmiMhlLocalDeviceStub)localObject).getInfo();; localObject = (HdmiDeviceInfo)this.mPortDeviceMap.get(paramInt, HdmiDeviceInfo.INACTIVE_DEVICE))
    {
      invokeInputChangeListener((HdmiDeviceInfo)localObject);
      return;
      i = -1;
      break;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void displayOsd(int paramInt)
  {
    assertRunOnServiceThread();
    Intent localIntent = new Intent("android.hardware.hdmi.action.OSD_MESSAGE");
    localIntent.putExtra("android.hardware.hdmi.extra.MESSAGE_ID", paramInt);
    getContext().sendBroadcastAsUser(localIntent, UserHandle.ALL, "android.permission.HDMI_CEC");
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void displayOsd(int paramInt1, int paramInt2)
  {
    assertRunOnServiceThread();
    Intent localIntent = new Intent("android.hardware.hdmi.action.OSD_MESSAGE");
    localIntent.putExtra("android.hardware.hdmi.extra.MESSAGE_ID", paramInt1);
    localIntent.putExtra("android.hardware.hdmi.extra.MESSAGE_EXTRA_PARAM1", paramInt2);
    getContext().sendBroadcastAsUser(localIntent, UserHandle.ALL, "android.permission.HDMI_CEC");
  }
  
  List<HdmiCecLocalDevice> getAllLocalDevices()
  {
    assertRunOnServiceThread();
    return this.mCecController.getLocalDeviceList();
  }
  
  AudioManager getAudioManager()
  {
    return (AudioManager)getContext().getSystemService("audio");
  }
  
  int getCecVersion()
  {
    return this.mCecController.getVersion();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  HdmiDeviceInfo getDeviceInfo(int paramInt)
  {
    assertRunOnServiceThread();
    if (tv() == null) {
      return null;
    }
    return tv().getCecDeviceInfo(paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  HdmiDeviceInfo getDeviceInfoByPort(int paramInt)
  {
    assertRunOnServiceThread();
    HdmiMhlLocalDeviceStub localHdmiMhlLocalDeviceStub = this.mMhlController.getLocalDevice(paramInt);
    if (localHdmiMhlLocalDeviceStub != null) {
      return localHdmiMhlLocalDeviceStub.getInfo();
    }
    return null;
  }
  
  Looper getIoLooper()
  {
    return this.mIoThread.getLooper();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  String getLanguage()
  {
    assertRunOnServiceThread();
    return this.mLanguage;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  int getLastInputForMhl()
  {
    assertRunOnServiceThread();
    return this.mLastInputMhl;
  }
  
  int getPhysicalAddress()
  {
    return this.mCecController.getPhysicalAddress();
  }
  
  HdmiPortInfo getPortInfo(int paramInt)
  {
    return (HdmiPortInfo)this.mPortInfoMap.get(paramInt, null);
  }
  
  List<HdmiPortInfo> getPortInfo()
  {
    return this.mPortInfo;
  }
  
  PowerManager getPowerManager()
  {
    return this.mPowerManager;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  int getPowerStatus()
  {
    assertRunOnServiceThread();
    return this.mPowerStatus;
  }
  
  Object getServiceLock()
  {
    return this.mLock;
  }
  
  Looper getServiceLooper()
  {
    return this.mHandler.getLooper();
  }
  
  TvInputManager getTvInputManager()
  {
    return this.mTvInputManager;
  }
  
  int getVendorId()
  {
    return this.mCecController.getVendorId();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean handleCecCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if (!this.mAddressAllocated)
    {
      this.mCecMessageBuffer.bufferMessage(paramHdmiCecMessage);
      return true;
    }
    int i = this.mMessageValidator.isValid(paramHdmiCecMessage);
    if (i != 0)
    {
      if (i == 3) {
        maySendFeatureAbortCommand(paramHdmiCecMessage, 3);
      }
      return true;
    }
    return dispatchMessageToLocalDevice(paramHdmiCecMessage);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void handleMhlBusModeChanged(int paramInt1, int paramInt2)
  {
    assertRunOnServiceThread();
    HdmiMhlLocalDeviceStub localHdmiMhlLocalDeviceStub = this.mMhlController.getLocalDevice(paramInt1);
    if (localHdmiMhlLocalDeviceStub != null)
    {
      localHdmiMhlLocalDeviceStub.setBusMode(paramInt2);
      return;
    }
    Slog.w("HdmiControlService", "No mhl device exists for bus mode change[portId:" + paramInt1 + ", busmode:" + paramInt2 + "]");
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void handleMhlBusOvercurrent(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    HdmiMhlLocalDeviceStub localHdmiMhlLocalDeviceStub = this.mMhlController.getLocalDevice(paramInt);
    if (localHdmiMhlLocalDeviceStub != null)
    {
      localHdmiMhlLocalDeviceStub.onBusOvercurrentDetected(paramBoolean);
      return;
    }
    Slog.w("HdmiControlService", "No mhl device exists for bus overcurrent event[portId:" + paramInt + "]");
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void handleMhlDeviceStatusChanged(int paramInt1, int paramInt2, int paramInt3)
  {
    assertRunOnServiceThread();
    HdmiMhlLocalDeviceStub localHdmiMhlLocalDeviceStub = this.mMhlController.getLocalDevice(paramInt1);
    if (localHdmiMhlLocalDeviceStub != null)
    {
      localHdmiMhlLocalDeviceStub.setDeviceStatusChange(paramInt2, paramInt3);
      return;
    }
    Slog.w("HdmiControlService", "No mhl device exists for device status event[portId:" + paramInt1 + ", adopterId:" + paramInt2 + ", deviceId:" + paramInt3 + "]");
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void handleMhlHotplugEvent(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    HdmiMhlLocalDeviceStub localHdmiMhlLocalDeviceStub1;
    if (paramBoolean)
    {
      localHdmiMhlLocalDeviceStub1 = new HdmiMhlLocalDeviceStub(this, paramInt);
      HdmiMhlLocalDeviceStub localHdmiMhlLocalDeviceStub2 = this.mMhlController.addLocalDevice(localHdmiMhlLocalDeviceStub1);
      if (localHdmiMhlLocalDeviceStub2 != null)
      {
        localHdmiMhlLocalDeviceStub2.onDeviceRemoved();
        Slog.i("HdmiControlService", "Old device of port " + paramInt + " is removed");
      }
      invokeDeviceEventListeners(localHdmiMhlLocalDeviceStub1.getInfo(), 1);
      updateSafeMhlInput();
    }
    for (;;)
    {
      announceHotplugEvent(paramInt, paramBoolean);
      return;
      localHdmiMhlLocalDeviceStub1 = this.mMhlController.removeLocalDevice(paramInt);
      if (localHdmiMhlLocalDeviceStub1 != null)
      {
        localHdmiMhlLocalDeviceStub1.onDeviceRemoved();
        invokeDeviceEventListeners(localHdmiMhlLocalDeviceStub1.getInfo(), 2);
        updateSafeMhlInput();
      }
      else
      {
        Slog.w("HdmiControlService", "No device to remove:[portId=" + paramInt);
      }
    }
  }
  
  void invokeClearTimerRecordingResult(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      HdmiRecordListenerRecord localHdmiRecordListenerRecord = this.mRecordListenerRecord;
      if (localHdmiRecordListenerRecord != null) {}
      try
      {
        HdmiRecordListenerRecord.-get0(this.mRecordListenerRecord).onClearTimerRecordingResult(paramInt1, paramInt2);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Slog.w("HdmiControlService", "Failed to call onClearTimerRecordingResult.", localRemoteException);
        }
      }
    }
  }
  
  void invokeDeviceEventListeners(HdmiDeviceInfo paramHdmiDeviceInfo, int paramInt)
  {
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mDeviceEventListenerRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          DeviceEventListenerRecord localDeviceEventListenerRecord = (DeviceEventListenerRecord)localIterator.next();
          try
          {
            DeviceEventListenerRecord.-get0(localDeviceEventListenerRecord).onStatusChanged(paramHdmiDeviceInfo, paramInt);
          }
          catch (RemoteException localRemoteException)
          {
            Slog.e("HdmiControlService", "Failed to report device event:" + localRemoteException);
          }
        }
      }
    }
  }
  
  void invokeInputChangeListener(HdmiDeviceInfo paramHdmiDeviceInfo)
  {
    synchronized (this.mLock)
    {
      InputChangeListenerRecord localInputChangeListenerRecord = this.mInputChangeListenerRecord;
      if (localInputChangeListenerRecord != null) {}
      try
      {
        InputChangeListenerRecord.-get0(this.mInputChangeListenerRecord).onChanged(paramHdmiDeviceInfo);
        return;
      }
      catch (RemoteException paramHdmiDeviceInfo)
      {
        for (;;)
        {
          Slog.w("HdmiControlService", "Exception thrown by IHdmiInputChangeListener: " + paramHdmiDeviceInfo);
        }
      }
    }
  }
  
  void invokeMhlVendorCommandListeners(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mMhlVendorCommandListenerRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          HdmiMhlVendorCommandListenerRecord localHdmiMhlVendorCommandListenerRecord = (HdmiMhlVendorCommandListenerRecord)localIterator.next();
          try
          {
            HdmiMhlVendorCommandListenerRecord.-get0(localHdmiMhlVendorCommandListenerRecord).onReceived(paramInt1, paramInt2, paramInt3, paramArrayOfByte);
          }
          catch (RemoteException localRemoteException)
          {
            Slog.e("HdmiControlService", "Failed to notify MHL vendor command", localRemoteException);
          }
        }
      }
    }
  }
  
  void invokeOneTouchRecordResult(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      HdmiRecordListenerRecord localHdmiRecordListenerRecord = this.mRecordListenerRecord;
      if (localHdmiRecordListenerRecord != null) {}
      try
      {
        HdmiRecordListenerRecord.-get0(this.mRecordListenerRecord).onOneTouchRecordResult(paramInt1, paramInt2);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Slog.w("HdmiControlService", "Failed to call onOneTouchRecordResult.", localRemoteException);
        }
      }
    }
  }
  
  byte[] invokeRecordRequestListener(int paramInt)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = this.mRecordListenerRecord;
      if (localObject2 != null) {
        try
        {
          localObject2 = HdmiRecordListenerRecord.-get0(this.mRecordListenerRecord).getOneTouchRecordSource(paramInt);
          return (byte[])localObject2;
        }
        catch (RemoteException localRemoteException)
        {
          Slog.w("HdmiControlService", "Failed to start record.", localRemoteException);
        }
      }
      byte[] arrayOfByte = EmptyArray.BYTE;
      return arrayOfByte;
    }
  }
  
  void invokeTimerRecordingResult(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      HdmiRecordListenerRecord localHdmiRecordListenerRecord = this.mRecordListenerRecord;
      if (localHdmiRecordListenerRecord != null) {}
      try
      {
        HdmiRecordListenerRecord.-get0(this.mRecordListenerRecord).onTimerRecordingResult(paramInt1, paramInt2);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          Slog.w("HdmiControlService", "Failed to call onTimerRecordingResult.", localRemoteException);
        }
      }
    }
  }
  
  boolean invokeVendorCommandListenersOnControlStateChanged(boolean paramBoolean, int paramInt)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mVendorCommandListenerRecords.isEmpty();
      if (bool) {
        return false;
      }
      Iterator localIterator = this.mVendorCommandListenerRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          VendorCommandListenerRecord localVendorCommandListenerRecord = (VendorCommandListenerRecord)localIterator.next();
          try
          {
            VendorCommandListenerRecord.-get1(localVendorCommandListenerRecord).onControlStateChanged(paramBoolean, paramInt);
          }
          catch (RemoteException localRemoteException)
          {
            Slog.e("HdmiControlService", "Failed to notify control-state-changed to vendor handler", localRemoteException);
          }
        }
      }
    }
    return true;
  }
  
  boolean invokeVendorCommandListenersOnReceived(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mVendorCommandListenerRecords.isEmpty();
      if (bool) {
        return false;
      }
      Iterator localIterator = this.mVendorCommandListenerRecords.iterator();
      for (;;)
      {
        if (localIterator.hasNext())
        {
          VendorCommandListenerRecord localVendorCommandListenerRecord = (VendorCommandListenerRecord)localIterator.next();
          int i = VendorCommandListenerRecord.-get0(localVendorCommandListenerRecord);
          if (i != paramInt1) {
            continue;
          }
          try
          {
            VendorCommandListenerRecord.-get1(localVendorCommandListenerRecord).onReceived(paramInt2, paramInt3, paramArrayOfByte, paramBoolean);
          }
          catch (RemoteException localRemoteException)
          {
            Slog.e("HdmiControlService", "Failed to notify vendor command reception", localRemoteException);
          }
        }
      }
    }
    return true;
  }
  
  boolean isAddressAllocated()
  {
    return this.mAddressAllocated;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean isConnected(int paramInt)
  {
    assertRunOnServiceThread();
    return this.mCecController.isConnected(paramInt);
  }
  
  boolean isConnectedToArcPort(int paramInt)
  {
    paramInt = pathToPortId(paramInt);
    if (paramInt != -1) {
      return ((HdmiPortInfo)this.mPortInfoMap.get(paramInt)).isArcSupported();
    }
    return false;
  }
  
  boolean isControlEnabled()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mHdmiControlEnabled;
      return bool;
    }
  }
  
  boolean isMhlInputChangeEnabled()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mMhlInputChangeEnabled;
      return bool;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean isPowerOnOrTransient()
  {
    assertRunOnServiceThread();
    return (this.mPowerStatus == 0) || (this.mPowerStatus == 2);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean isPowerStandby()
  {
    assertRunOnServiceThread();
    return this.mPowerStatus == 1;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean isPowerStandbyOrTransient()
  {
    assertRunOnServiceThread();
    return (this.mPowerStatus == 1) || (this.mPowerStatus == 3);
  }
  
  boolean isProhibitMode()
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mProhibitMode;
      return bool;
    }
  }
  
  boolean isTvDevice()
  {
    return this.mLocalDevices.contains(Integer.valueOf(0));
  }
  
  boolean isTvDeviceEnabled()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (isTvDevice())
    {
      bool1 = bool2;
      if (tv() != null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  boolean isValidPortId(int paramInt)
  {
    return getPortInfo(paramInt) != null;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void maySendFeatureAbortCommand(HdmiCecMessage paramHdmiCecMessage, int paramInt)
  {
    assertRunOnServiceThread();
    this.mCecController.maySendFeatureAbortCommand(paramHdmiCecMessage, paramInt);
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500)
    {
      this.mTvInputManager = ((TvInputManager)getContext().getSystemService("tv_input"));
      this.mPowerManager = ((PowerManager)getContext().getSystemService("power"));
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void onHotplug(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    if ((!paramBoolean) || (isTvDevice())) {}
    for (;;)
    {
      Object localObject = this.mCecController.getLocalDeviceList().iterator();
      while (((Iterator)localObject).hasNext()) {
        ((HdmiCecLocalDevice)((Iterator)localObject).next()).onHotplug(paramInt, paramBoolean);
      }
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = this.mLocalDevices.iterator();
      while (localIterator.hasNext())
      {
        int i = ((Integer)localIterator.next()).intValue();
        HdmiCecLocalDevice localHdmiCecLocalDevice = this.mCecController.getLocalDevice(i);
        localObject = localHdmiCecLocalDevice;
        if (localHdmiCecLocalDevice == null)
        {
          localObject = HdmiCecLocalDevice.create(this, i);
          ((HdmiCecLocalDevice)localObject).init();
        }
        localArrayList.add(localObject);
      }
      allocateLogicalAddress(localArrayList, 4);
    }
    announceHotplugEvent(paramInt, paramBoolean);
  }
  
  public void onStart()
  {
    this.mIoThread.start();
    this.mPowerStatus = 2;
    this.mProhibitMode = false;
    this.mHdmiControlEnabled = readBooleanSetting("hdmi_control_enabled", true);
    this.mMhlInputChangeEnabled = readBooleanSetting("mhl_input_switching_enabled", true);
    this.mCecController = HdmiCecController.create(this);
    if (this.mCecController != null)
    {
      if (this.mHdmiControlEnabled) {
        initializeCec(1);
      }
      this.mMhlController = HdmiMhlControllerStub.create(this);
      if (!this.mMhlController.isReady()) {
        Slog.i("HdmiControlService", "Device does not support MHL-control.");
      }
      this.mMhlDevices = Collections.emptyList();
      initPortInfo();
      this.mMessageValidator = new HdmiCecMessageValidator(this);
      publishBinderService("hdmi_control", new BinderService(null));
      if (this.mCecController != null)
      {
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
        localIntentFilter.addAction("android.intent.action.SCREEN_ON");
        localIntentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
        localIntentFilter.addAction("android.intent.action.CONFIGURATION_CHANGED");
        getContext().registerReceiver(this.mHdmiControlBroadcastReceiver, localIntentFilter);
        registerContentObserver();
      }
      this.mMhlController.setOption(104, 1);
      return;
    }
    Slog.i("HdmiControlService", "Device does not support HDMI-CEC.");
  }
  
  int pathToPortId(int paramInt)
  {
    return this.mPortIdMap.get(paramInt & 0xF000, -1);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void pollDevices(DevicePollingCallback paramDevicePollingCallback, int paramInt1, int paramInt2, int paramInt3)
  {
    assertRunOnServiceThread();
    this.mCecController.pollDevices(paramDevicePollingCallback, paramInt1, checkPollStrategy(paramInt2), paramInt3);
  }
  
  int portIdToPath(int paramInt)
  {
    HdmiPortInfo localHdmiPortInfo = getPortInfo(paramInt);
    if (localHdmiPortInfo == null)
    {
      Slog.e("HdmiControlService", "Cannot find the port info: " + paramInt);
      return 65535;
    }
    return localHdmiPortInfo.getAddress();
  }
  
  boolean readBooleanSetting(String paramString, boolean paramBoolean)
  {
    return Settings.Global.getInt(getContext().getContentResolver(), paramString, toInt(paramBoolean)) == 1;
  }
  
  void registerTvInputCallback(TvInputManager.TvInputCallback paramTvInputCallback)
  {
    if (this.mTvInputManager == null) {
      return;
    }
    this.mTvInputManager.registerCallback(paramTvInputCallback, this.mHandler);
  }
  
  void runOnServiceThread(Runnable paramRunnable)
  {
    this.mHandler.post(paramRunnable);
  }
  
  void runOnServiceThreadAtFrontOfQueue(Runnable paramRunnable)
  {
    this.mHandler.postAtFrontOfQueue(paramRunnable);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void sendCecCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    sendCecCommand(paramHdmiCecMessage, null);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void sendCecCommand(HdmiCecMessage paramHdmiCecMessage, SendMessageCallback paramSendMessageCallback)
  {
    assertRunOnServiceThread();
    if (this.mMessageValidator.isValid(paramHdmiCecMessage) == 0) {
      this.mCecController.sendCommand(paramHdmiCecMessage, paramSendMessageCallback);
    }
    do
    {
      return;
      HdmiLogger.error("Invalid message type:" + paramHdmiCecMessage, new Object[0]);
    } while (paramSendMessageCallback == null);
    paramSendMessageCallback.onSendCompleted(3);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void setActivePortId(int paramInt)
  {
    assertRunOnServiceThread();
    this.mActivePortId = paramInt;
    setLastInputForMhl(-1);
  }
  
  void setAudioReturnChannel(int paramInt, boolean paramBoolean)
  {
    this.mCecController.setAudioReturnChannel(paramInt, paramBoolean);
  }
  
  void setAudioStatus(boolean paramBoolean, int paramInt)
  {
    AudioManager localAudioManager = getAudioManager();
    boolean bool = localAudioManager.isStreamMute(3);
    if (paramBoolean)
    {
      if (!bool) {
        localAudioManager.setStreamMute(3, true);
      }
      return;
    }
    if (bool) {
      localAudioManager.setStreamMute(3, false);
    }
    localAudioManager.setStreamVolume(3, paramInt, 257);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void setCecOption(int paramInt1, int paramInt2)
  {
    assertRunOnServiceThread();
    this.mCecController.setOption(paramInt1, paramInt2);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void setControlEnabled(boolean paramBoolean)
  {
    assertRunOnServiceThread();
    synchronized (this.mLock)
    {
      this.mHdmiControlEnabled = paramBoolean;
      if (paramBoolean)
      {
        enableHdmiControlService();
        return;
      }
    }
    invokeVendorCommandListenersOnControlStateChanged(false, 1);
    runOnServiceThread(new Runnable()
    {
      public void run()
      {
        HdmiControlService.-wrap10(HdmiControlService.this);
      }
    });
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void setLastInputForMhl(int paramInt)
  {
    assertRunOnServiceThread();
    this.mLastInputMhl = paramInt;
  }
  
  void setMhlInputChangeEnabled(boolean paramBoolean)
  {
    this.mMhlController.setOption(101, toInt(paramBoolean));
    synchronized (this.mLock)
    {
      this.mMhlInputChangeEnabled = paramBoolean;
      return;
    }
  }
  
  void setProhibitMode(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      this.mProhibitMode = paramBoolean;
      return;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void standby()
  {
    assertRunOnServiceThread();
    if (!canGoToStandby()) {
      return;
    }
    this.mStandbyMessageReceived = true;
    this.mPowerManager.goToSleep(SystemClock.uptimeMillis(), 5, 0);
  }
  
  public HdmiCecLocalDeviceTv tv()
  {
    return (HdmiCecLocalDeviceTv)this.mCecController.getLocalDevice(0);
  }
  
  void unregisterTvInputCallback(TvInputManager.TvInputCallback paramTvInputCallback)
  {
    if (this.mTvInputManager == null) {
      return;
    }
    this.mTvInputManager.unregisterCallback(paramTvInputCallback);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void wakeUp()
  {
    assertRunOnServiceThread();
    this.mWakeUpMessageReceived = true;
    this.mPowerManager.wakeUp(SystemClock.uptimeMillis(), "android.server.hdmi:WAKE");
  }
  
  void writeBooleanSetting(String paramString, boolean paramBoolean)
  {
    Settings.Global.putInt(getContext().getContentResolver(), paramString, toInt(paramBoolean));
  }
  
  private final class BinderService
    extends IHdmiControlService.Stub
  {
    private BinderService() {}
    
    public void addDeviceEventListener(IHdmiDeviceEventListener paramIHdmiDeviceEventListener)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.-wrap3(HdmiControlService.this, paramIHdmiDeviceEventListener);
    }
    
    public void addHdmiMhlVendorCommandListener(IHdmiMhlVendorCommandListener paramIHdmiMhlVendorCommandListener)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.-wrap4(HdmiControlService.this, paramIHdmiMhlVendorCommandListener);
    }
    
    public void addHotplugEventListener(IHdmiHotplugEventListener paramIHdmiHotplugEventListener)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.-wrap5(HdmiControlService.this, paramIHdmiHotplugEventListener);
    }
    
    public void addSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener paramIHdmiSystemAudioModeChangeListener)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.-wrap6(HdmiControlService.this, paramIHdmiSystemAudioModeChangeListener);
    }
    
    public void addVendorCommandListener(IHdmiVendorCommandListener paramIHdmiVendorCommandListener, int paramInt)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.-wrap7(HdmiControlService.this, paramIHdmiVendorCommandListener, paramInt);
    }
    
    public boolean canChangeSystemAudioMode()
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiCecLocalDeviceTv localHdmiCecLocalDeviceTv = HdmiControlService.this.tv();
      if (localHdmiCecLocalDeviceTv == null) {
        return false;
      }
      return localHdmiCecLocalDeviceTv.hasSystemAudioDevice();
    }
    
    public void clearTimerRecording(final int paramInt1, final int paramInt2, final byte[] paramArrayOfByte)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          if (!HdmiControlService.this.isTvDeviceEnabled())
          {
            Slog.w("HdmiControlService", "TV device is not enabled.");
            return;
          }
          HdmiControlService.this.tv().clearTimerRecording(paramInt1, paramInt2, paramArrayOfByte);
        }
      });
    }
    
    public void deviceSelect(final int paramInt, final IHdmiControlCallback paramIHdmiControlCallback)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          if (paramIHdmiControlCallback == null)
          {
            Slog.e("HdmiControlService", "Callback cannot be null");
            return;
          }
          HdmiCecLocalDeviceTv localHdmiCecLocalDeviceTv = HdmiControlService.this.tv();
          if (localHdmiCecLocalDeviceTv == null)
          {
            if (!HdmiControlService.-get3(HdmiControlService.this))
            {
              HdmiControlService.-get19(HdmiControlService.this).set(SelectRequestBuffer.newDeviceSelect(HdmiControlService.this, paramInt, paramIHdmiControlCallback));
              return;
            }
            Slog.w("HdmiControlService", "Local tv device not available");
            HdmiControlService.-wrap12(HdmiControlService.this, paramIHdmiControlCallback, 2);
            return;
          }
          HdmiMhlLocalDeviceStub localHdmiMhlLocalDeviceStub = HdmiControlService.-get13(HdmiControlService.this).getLocalDeviceById(paramInt);
          if (localHdmiMhlLocalDeviceStub != null)
          {
            if (localHdmiMhlLocalDeviceStub.getPortId() == localHdmiCecLocalDeviceTv.getActivePortId())
            {
              HdmiControlService.-wrap12(HdmiControlService.this, paramIHdmiControlCallback, 0);
              return;
            }
            localHdmiMhlLocalDeviceStub.turnOn(paramIHdmiControlCallback);
            localHdmiCecLocalDeviceTv.doManualPortSwitching(localHdmiMhlLocalDeviceStub.getPortId(), null);
            return;
          }
          localHdmiCecLocalDeviceTv.deviceSelect(paramInt, paramIHdmiControlCallback);
        }
      });
    }
    
    protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      HdmiControlService.this.getContext().enforceCallingOrSelfPermission("android.permission.DUMP", "HdmiControlService");
      paramFileDescriptor = new IndentingPrintWriter(paramPrintWriter, "  ");
      paramFileDescriptor.println("mHdmiControlEnabled: " + HdmiControlService.-get7(HdmiControlService.this));
      paramFileDescriptor.println("mProhibitMode: " + HdmiControlService.-get17(HdmiControlService.this));
      if (HdmiControlService.-get4(HdmiControlService.this) != null)
      {
        paramFileDescriptor.println("mCecController: ");
        paramFileDescriptor.increaseIndent();
        HdmiControlService.-get4(HdmiControlService.this).dump(paramFileDescriptor);
        paramFileDescriptor.decreaseIndent();
      }
      paramFileDescriptor.println("mMhlController: ");
      paramFileDescriptor.increaseIndent();
      HdmiControlService.-get13(HdmiControlService.this).dump(paramFileDescriptor);
      paramFileDescriptor.decreaseIndent();
      paramFileDescriptor.println("mPortInfo: ");
      paramFileDescriptor.increaseIndent();
      paramPrintWriter = HdmiControlService.-get15(HdmiControlService.this).iterator();
      while (paramPrintWriter.hasNext())
      {
        paramArrayOfString = (HdmiPortInfo)paramPrintWriter.next();
        paramFileDescriptor.println("- " + paramArrayOfString);
      }
      paramFileDescriptor.decreaseIndent();
      paramFileDescriptor.println("mPowerStatus: " + HdmiControlService.-get16(HdmiControlService.this));
    }
    
    public HdmiDeviceInfo getActiveSource()
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiCecLocalDeviceTv localHdmiCecLocalDeviceTv = HdmiControlService.this.tv();
      if (localHdmiCecLocalDeviceTv == null)
      {
        Slog.w("HdmiControlService", "Local tv device not available");
        return null;
      }
      Object localObject = localHdmiCecLocalDeviceTv.getActiveSource();
      if (((HdmiCecLocalDevice.ActiveSource)localObject).isValid()) {
        return new HdmiDeviceInfo(((HdmiCecLocalDevice.ActiveSource)localObject).logicalAddress, ((HdmiCecLocalDevice.ActiveSource)localObject).physicalAddress, -1, -1, 0, "");
      }
      int i = localHdmiCecLocalDeviceTv.getActivePath();
      if (i != 65535)
      {
        localObject = localHdmiCecLocalDeviceTv.getSafeDeviceInfoByPath(i);
        if (localObject != null) {
          return (HdmiDeviceInfo)localObject;
        }
        return new HdmiDeviceInfo(i, localHdmiCecLocalDeviceTv.getActivePortId());
      }
      return null;
    }
    
    public List<HdmiDeviceInfo> getDeviceList()
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      Object localObject1 = HdmiControlService.this.tv();
      localObject3 = HdmiControlService.-get12(HdmiControlService.this);
      if (localObject1 == null) {}
      for (;;)
      {
        try
        {
          localObject1 = Collections.emptyList();
          return (List<HdmiDeviceInfo>)localObject1;
        }
        finally {}
        localObject1 = ((HdmiCecLocalDeviceTv)localObject1).getSafeCecDevicesLocked();
      }
    }
    
    public List<HdmiDeviceInfo> getInputDevices()
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      Object localObject1 = HdmiControlService.this.tv();
      localObject3 = HdmiControlService.-get12(HdmiControlService.this);
      if (localObject1 == null) {}
      for (;;)
      {
        try
        {
          localObject1 = Collections.emptyList();
          localObject1 = HdmiUtils.mergeToUnmodifiableList((List)localObject1, HdmiControlService.-wrap2(HdmiControlService.this));
          return (List<HdmiDeviceInfo>)localObject1;
        }
        finally {}
        localObject1 = ((HdmiCecLocalDeviceTv)localObject1).getSafeExternalInputsLocked();
      }
    }
    
    public List<HdmiPortInfo> getPortInfo()
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      return HdmiControlService.this.getPortInfo();
    }
    
    public int[] getSupportedTypes()
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      int[] arrayOfInt = new int[HdmiControlService.-get11(HdmiControlService.this).size()];
      int i = 0;
      while (i < arrayOfInt.length)
      {
        arrayOfInt[i] = ((Integer)HdmiControlService.-get11(HdmiControlService.this).get(i)).intValue();
        i += 1;
      }
      return arrayOfInt;
    }
    
    public boolean getSystemAudioMode()
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiCecLocalDeviceTv localHdmiCecLocalDeviceTv = HdmiControlService.this.tv();
      if (localHdmiCecLocalDeviceTv == null) {
        return false;
      }
      return localHdmiCecLocalDeviceTv.isSystemAudioActivated();
    }
    
    public void oneTouchPlay(final IHdmiControlCallback paramIHdmiControlCallback)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          HdmiControlService.-wrap20(HdmiControlService.this, paramIHdmiControlCallback);
        }
      });
    }
    
    public void portSelect(final int paramInt, final IHdmiControlCallback paramIHdmiControlCallback)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          if (paramIHdmiControlCallback == null)
          {
            Slog.e("HdmiControlService", "Callback cannot be null");
            return;
          }
          HdmiCecLocalDeviceTv localHdmiCecLocalDeviceTv = HdmiControlService.this.tv();
          if (localHdmiCecLocalDeviceTv == null)
          {
            if (!HdmiControlService.-get3(HdmiControlService.this))
            {
              HdmiControlService.-get19(HdmiControlService.this).set(SelectRequestBuffer.newPortSelect(HdmiControlService.this, paramInt, paramIHdmiControlCallback));
              return;
            }
            Slog.w("HdmiControlService", "Local tv device not available");
            HdmiControlService.-wrap12(HdmiControlService.this, paramIHdmiControlCallback, 2);
            return;
          }
          localHdmiCecLocalDeviceTv.doManualPortSwitching(paramInt, paramIHdmiControlCallback);
        }
      });
    }
    
    public void queryDisplayStatus(final IHdmiControlCallback paramIHdmiControlCallback)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          HdmiControlService.-wrap21(HdmiControlService.this, paramIHdmiControlCallback);
        }
      });
    }
    
    public void removeHotplugEventListener(IHdmiHotplugEventListener paramIHdmiHotplugEventListener)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.-wrap22(HdmiControlService.this, paramIHdmiHotplugEventListener);
    }
    
    public void removeSystemAudioModeChangeListener(IHdmiSystemAudioModeChangeListener paramIHdmiSystemAudioModeChangeListener)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.-wrap23(HdmiControlService.this, paramIHdmiSystemAudioModeChangeListener);
    }
    
    public void sendKeyEvent(final int paramInt1, final int paramInt2, final boolean paramBoolean)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          Object localObject = HdmiControlService.-get13(HdmiControlService.this).getLocalDevice(HdmiControlService.-get2(HdmiControlService.this));
          if (localObject != null)
          {
            ((HdmiMhlLocalDeviceStub)localObject).sendKeyEvent(paramInt2, paramBoolean);
            return;
          }
          if (HdmiControlService.-get4(HdmiControlService.this) != null)
          {
            localObject = HdmiControlService.-get4(HdmiControlService.this).getLocalDevice(paramInt1);
            if (localObject == null)
            {
              Slog.w("HdmiControlService", "Local device not available");
              return;
            }
            ((HdmiCecLocalDevice)localObject).sendKeyEvent(paramInt2, paramBoolean);
          }
        }
      });
    }
    
    public void sendMhlVendorCommand(final int paramInt1, final int paramInt2, final int paramInt3, final byte[] paramArrayOfByte)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          if (!HdmiControlService.this.isControlEnabled())
          {
            Slog.w("HdmiControlService", "Hdmi control is disabled.");
            return;
          }
          if (HdmiControlService.-get13(HdmiControlService.this).getLocalDevice(paramInt1) == null)
          {
            Slog.w("HdmiControlService", "Invalid port id:" + paramInt1);
            return;
          }
          HdmiControlService.-get13(HdmiControlService.this).sendVendorCommand(paramInt1, paramInt2, paramInt3, paramArrayOfByte);
        }
      });
    }
    
    public void sendStandby(final int paramInt1, final int paramInt2)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          Object localObject = HdmiControlService.-get13(HdmiControlService.this).getLocalDeviceById(paramInt2);
          if (localObject != null)
          {
            ((HdmiMhlLocalDeviceStub)localObject).sendStandby();
            return;
          }
          localObject = HdmiControlService.-get4(HdmiControlService.this).getLocalDevice(paramInt1);
          if (localObject == null)
          {
            Slog.w("HdmiControlService", "Local device not available");
            return;
          }
          ((HdmiCecLocalDevice)localObject).sendStandby(paramInt2);
        }
      });
    }
    
    public void sendVendorCommand(final int paramInt1, final int paramInt2, final byte[] paramArrayOfByte, final boolean paramBoolean)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          HdmiCecLocalDevice localHdmiCecLocalDevice = HdmiControlService.-get4(HdmiControlService.this).getLocalDevice(paramInt1);
          if (localHdmiCecLocalDevice == null)
          {
            Slog.w("HdmiControlService", "Local device not available");
            return;
          }
          if (paramBoolean)
          {
            HdmiControlService.this.sendCecCommand(HdmiCecMessageBuilder.buildVendorCommandWithId(localHdmiCecLocalDevice.getDeviceInfo().getLogicalAddress(), paramInt2, HdmiControlService.this.getVendorId(), paramArrayOfByte));
            return;
          }
          HdmiControlService.this.sendCecCommand(HdmiCecMessageBuilder.buildVendorCommand(localHdmiCecLocalDevice.getDeviceInfo().getLogicalAddress(), paramInt2, paramArrayOfByte));
        }
      });
    }
    
    public void setArcMode(boolean paramBoolean)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          if (HdmiControlService.this.tv() == null)
          {
            Slog.w("HdmiControlService", "Local tv device not available to change arc mode.");
            return;
          }
        }
      });
    }
    
    public void setHdmiRecordListener(IHdmiRecordListener paramIHdmiRecordListener)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.-wrap24(HdmiControlService.this, paramIHdmiRecordListener);
    }
    
    public void setInputChangeListener(IHdmiInputChangeListener paramIHdmiInputChangeListener)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.-wrap25(HdmiControlService.this, paramIHdmiInputChangeListener);
    }
    
    public void setProhibitMode(boolean paramBoolean)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      if (!HdmiControlService.this.isTvDevice()) {
        return;
      }
      HdmiControlService.this.setProhibitMode(paramBoolean);
    }
    
    public void setSystemAudioMode(final boolean paramBoolean, final IHdmiControlCallback paramIHdmiControlCallback)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          HdmiCecLocalDeviceTv localHdmiCecLocalDeviceTv = HdmiControlService.this.tv();
          if (localHdmiCecLocalDeviceTv == null)
          {
            Slog.w("HdmiControlService", "Local tv device not available");
            HdmiControlService.-wrap12(HdmiControlService.this, paramIHdmiControlCallback, 2);
            return;
          }
          localHdmiCecLocalDeviceTv.changeSystemAudioMode(paramBoolean, paramIHdmiControlCallback);
        }
      });
    }
    
    public void setSystemAudioMute(final boolean paramBoolean)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          HdmiCecLocalDeviceTv localHdmiCecLocalDeviceTv = HdmiControlService.this.tv();
          if (localHdmiCecLocalDeviceTv == null)
          {
            Slog.w("HdmiControlService", "Local tv device not available");
            return;
          }
          localHdmiCecLocalDeviceTv.changeMute(paramBoolean);
        }
      });
    }
    
    public void setSystemAudioVolume(final int paramInt1, final int paramInt2, final int paramInt3)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          HdmiCecLocalDeviceTv localHdmiCecLocalDeviceTv = HdmiControlService.this.tv();
          if (localHdmiCecLocalDeviceTv == null)
          {
            Slog.w("HdmiControlService", "Local tv device not available");
            return;
          }
          localHdmiCecLocalDeviceTv.changeVolume(paramInt1, paramInt2 - paramInt1, paramInt3);
        }
      });
    }
    
    public void startOneTouchRecord(final int paramInt, final byte[] paramArrayOfByte)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          if (!HdmiControlService.this.isTvDeviceEnabled())
          {
            Slog.w("HdmiControlService", "TV device is not enabled.");
            return;
          }
          HdmiControlService.this.tv().startOneTouchRecord(paramInt, paramArrayOfByte);
        }
      });
    }
    
    public void startTimerRecording(final int paramInt1, final int paramInt2, final byte[] paramArrayOfByte)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          if (!HdmiControlService.this.isTvDeviceEnabled())
          {
            Slog.w("HdmiControlService", "TV device is not enabled.");
            return;
          }
          HdmiControlService.this.tv().startTimerRecording(paramInt1, paramInt2, paramArrayOfByte);
        }
      });
    }
    
    public void stopOneTouchRecord(final int paramInt)
    {
      HdmiControlService.-wrap11(HdmiControlService.this);
      HdmiControlService.this.runOnServiceThread(new Runnable()
      {
        public void run()
        {
          if (!HdmiControlService.this.isTvDeviceEnabled())
          {
            Slog.w("HdmiControlService", "TV device is not enabled.");
            return;
          }
          HdmiControlService.this.tv().stopOneTouchRecord(paramInt);
        }
      });
    }
  }
  
  private final class CecMessageBuffer
  {
    private List<HdmiCecMessage> mBuffer = new ArrayList();
    
    private CecMessageBuffer() {}
    
    private void bufferActiveSource(HdmiCecMessage paramHdmiCecMessage)
    {
      if (!replaceMessageIfBuffered(paramHdmiCecMessage, 130)) {
        this.mBuffer.add(paramHdmiCecMessage);
      }
    }
    
    private void bufferImageOrTextViewOn(HdmiCecMessage paramHdmiCecMessage)
    {
      if ((replaceMessageIfBuffered(paramHdmiCecMessage, 4)) || (replaceMessageIfBuffered(paramHdmiCecMessage, 13))) {
        return;
      }
      this.mBuffer.add(paramHdmiCecMessage);
    }
    
    private boolean replaceMessageIfBuffered(HdmiCecMessage paramHdmiCecMessage, int paramInt)
    {
      int i = 0;
      while (i < this.mBuffer.size())
      {
        if (((HdmiCecMessage)this.mBuffer.get(i)).getOpcode() == paramInt)
        {
          this.mBuffer.set(i, paramHdmiCecMessage);
          return true;
        }
        i += 1;
      }
      return false;
    }
    
    public void bufferMessage(HdmiCecMessage paramHdmiCecMessage)
    {
      switch (paramHdmiCecMessage.getOpcode())
      {
      default: 
        return;
      case 130: 
        bufferActiveSource(paramHdmiCecMessage);
        return;
      }
      bufferImageOrTextViewOn(paramHdmiCecMessage);
    }
    
    public void processMessages()
    {
      Iterator localIterator = this.mBuffer.iterator();
      while (localIterator.hasNext())
      {
        final HdmiCecMessage localHdmiCecMessage = (HdmiCecMessage)localIterator.next();
        HdmiControlService.this.runOnServiceThread(new Runnable()
        {
          public void run()
          {
            HdmiControlService.this.handleCecCommand(localHdmiCecMessage);
          }
        });
      }
      this.mBuffer.clear();
    }
  }
  
  private final class DeviceEventListenerRecord
    implements IBinder.DeathRecipient
  {
    private final IHdmiDeviceEventListener mListener;
    
    public DeviceEventListenerRecord(IHdmiDeviceEventListener paramIHdmiDeviceEventListener)
    {
      this.mListener = paramIHdmiDeviceEventListener;
    }
    
    public void binderDied()
    {
      synchronized (HdmiControlService.-get12(HdmiControlService.this))
      {
        HdmiControlService.-get6(HdmiControlService.this).remove(this);
        return;
      }
    }
  }
  
  static abstract interface DevicePollingCallback
  {
    public abstract void onPollingFinished(List<Integer> paramList);
  }
  
  private class HdmiControlBroadcastReceiver
    extends BroadcastReceiver
  {
    private HdmiControlBroadcastReceiver() {}
    
    private String getMenuLanguage()
    {
      Locale localLocale = Locale.getDefault();
      if ((localLocale.equals(Locale.TAIWAN)) || (localLocale.equals(HdmiControlService.-get0(HdmiControlService.this))) || (localLocale.equals(HdmiControlService.-get1(HdmiControlService.this)))) {
        return "chi";
      }
      return localLocale.getISO3Language();
    }
    
    @HdmiAnnotations.ServiceThreadOnly
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      HdmiControlService.-wrap8(HdmiControlService.this);
      paramContext = paramIntent.getAction();
      if (paramContext.equals("android.intent.action.SCREEN_OFF")) {
        if (HdmiControlService.this.isPowerOnOrTransient()) {
          HdmiControlService.-wrap18(HdmiControlService.this, 0);
        }
      }
      do
      {
        do
        {
          do
          {
            return;
            if (!paramContext.equals("android.intent.action.SCREEN_ON")) {
              break;
            }
          } while (!HdmiControlService.this.isPowerStandbyOrTransient());
          HdmiControlService.-wrap19(HdmiControlService.this);
          return;
          if (!paramContext.equals("android.intent.action.CONFIGURATION_CHANGED")) {
            break;
          }
          paramContext = getMenuLanguage();
        } while (HdmiControlService.-get10(HdmiControlService.this).equals(paramContext));
        HdmiControlService.-wrap16(HdmiControlService.this, paramContext);
        return;
      } while ((!paramContext.equals("android.intent.action.ACTION_SHUTDOWN")) || (!HdmiControlService.this.isPowerOnOrTransient()));
      HdmiControlService.-wrap18(HdmiControlService.this, 1);
    }
  }
  
  private class HdmiMhlVendorCommandListenerRecord
    implements IBinder.DeathRecipient
  {
    private final IHdmiMhlVendorCommandListener mListener;
    
    public HdmiMhlVendorCommandListenerRecord(IHdmiMhlVendorCommandListener paramIHdmiMhlVendorCommandListener)
    {
      this.mListener = paramIHdmiMhlVendorCommandListener;
    }
    
    public void binderDied()
    {
      HdmiControlService.-get14(HdmiControlService.this).remove(this);
    }
  }
  
  private class HdmiRecordListenerRecord
    implements IBinder.DeathRecipient
  {
    private final IHdmiRecordListener mListener;
    
    public HdmiRecordListenerRecord(IHdmiRecordListener paramIHdmiRecordListener)
    {
      this.mListener = paramIHdmiRecordListener;
    }
    
    public void binderDied()
    {
      synchronized (HdmiControlService.-get12(HdmiControlService.this))
      {
        if (HdmiControlService.-get18(HdmiControlService.this) == this) {
          HdmiControlService.-set2(HdmiControlService.this, null);
        }
        return;
      }
    }
  }
  
  private final class HotplugEventListenerRecord
    implements IBinder.DeathRecipient
  {
    private final IHdmiHotplugEventListener mListener;
    
    public HotplugEventListenerRecord(IHdmiHotplugEventListener paramIHdmiHotplugEventListener)
    {
      this.mListener = paramIHdmiHotplugEventListener;
    }
    
    public void binderDied()
    {
      synchronized (HdmiControlService.-get12(HdmiControlService.this))
      {
        HdmiControlService.-get8(HdmiControlService.this).remove(this);
        return;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof HotplugEventListenerRecord)) {
        return false;
      }
      if (paramObject == this) {
        return true;
      }
      return ((HotplugEventListenerRecord)paramObject).mListener == this.mListener;
    }
    
    public int hashCode()
    {
      return this.mListener.hashCode();
    }
  }
  
  private final class InputChangeListenerRecord
    implements IBinder.DeathRecipient
  {
    private final IHdmiInputChangeListener mListener;
    
    public InputChangeListenerRecord(IHdmiInputChangeListener paramIHdmiInputChangeListener)
    {
      this.mListener = paramIHdmiInputChangeListener;
    }
    
    public void binderDied()
    {
      synchronized (HdmiControlService.-get12(HdmiControlService.this))
      {
        if (HdmiControlService.-get9(HdmiControlService.this) == this) {
          HdmiControlService.-set1(HdmiControlService.this, null);
        }
        return;
      }
    }
  }
  
  static abstract interface SendMessageCallback
  {
    public abstract void onSendCompleted(int paramInt);
  }
  
  private class SettingsObserver
    extends ContentObserver
  {
    public SettingsObserver(Handler paramHandler)
    {
      super();
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      paramUri = paramUri.getLastPathSegment();
      paramBoolean = HdmiControlService.this.readBooleanSetting(paramUri, true);
      if (paramUri.equals("hdmi_control_enabled")) {
        HdmiControlService.this.setControlEnabled(paramBoolean);
      }
      do
      {
        for (;;)
        {
          return;
          if (paramUri.equals("hdmi_control_auto_wakeup_enabled"))
          {
            if (HdmiControlService.this.isTvDeviceEnabled()) {
              HdmiControlService.this.tv().setAutoWakeup(paramBoolean);
            }
            HdmiControlService.this.setCecOption(1, HdmiControlService.-wrap1(paramBoolean));
            return;
          }
          if (!paramUri.equals("hdmi_control_auto_device_off_enabled")) {
            break;
          }
          paramUri = HdmiControlService.-get11(HdmiControlService.this).iterator();
          while (paramUri.hasNext())
          {
            int i = ((Integer)paramUri.next()).intValue();
            HdmiCecLocalDevice localHdmiCecLocalDevice = HdmiControlService.-get4(HdmiControlService.this).getLocalDevice(i);
            if (localHdmiCecLocalDevice != null) {
              localHdmiCecLocalDevice.setAutoDeviceOff(paramBoolean);
            }
          }
        }
        if (paramUri.equals("mhl_input_switching_enabled"))
        {
          HdmiControlService.this.setMhlInputChangeEnabled(paramBoolean);
          return;
        }
      } while (!paramUri.equals("mhl_power_charge_enabled"));
      HdmiControlService.-get13(HdmiControlService.this).setOption(102, HdmiControlService.-wrap1(paramBoolean));
    }
  }
  
  private final class SystemAudioModeChangeListenerRecord
    implements IBinder.DeathRecipient
  {
    private final IHdmiSystemAudioModeChangeListener mListener;
    
    public SystemAudioModeChangeListenerRecord(IHdmiSystemAudioModeChangeListener paramIHdmiSystemAudioModeChangeListener)
    {
      this.mListener = paramIHdmiSystemAudioModeChangeListener;
    }
    
    public void binderDied()
    {
      synchronized (HdmiControlService.-get12(HdmiControlService.this))
      {
        HdmiControlService.-get20(HdmiControlService.this).remove(this);
        return;
      }
    }
  }
  
  class VendorCommandListenerRecord
    implements IBinder.DeathRecipient
  {
    private final int mDeviceType;
    private final IHdmiVendorCommandListener mListener;
    
    public VendorCommandListenerRecord(IHdmiVendorCommandListener paramIHdmiVendorCommandListener, int paramInt)
    {
      this.mListener = paramIHdmiVendorCommandListener;
      this.mDeviceType = paramInt;
    }
    
    public void binderDied()
    {
      synchronized (HdmiControlService.-get12(HdmiControlService.this))
      {
        HdmiControlService.-get21(HdmiControlService.this).remove(this);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiControlService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */