package android.bluetooth.le;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityThread;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCallbackWrapper;
import android.bluetooth.IBluetoothGatt;
import android.bluetooth.IBluetoothManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.os.Process;
import android.os.RemoteException;
import android.os.WorkSource;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class BluetoothLeScanner
{
  private static final boolean DBG = true;
  private static final String TAG = "BluetoothLeScanner";
  private static final boolean VDBG = false;
  private BluetoothAdapter mBluetoothAdapter;
  private final IBluetoothManager mBluetoothManager;
  private final Handler mHandler;
  private final Map<ScanCallback, BleScanCallbackWrapper> mLeScanClients;
  
  public BluetoothLeScanner(IBluetoothManager paramIBluetoothManager)
  {
    this.mBluetoothManager = paramIBluetoothManager;
    this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    this.mHandler = new Handler(Looper.getMainLooper());
    this.mLeScanClients = new HashMap();
  }
  
  private boolean isHardwareResourcesAvailableForScan(ScanSettings paramScanSettings)
  {
    boolean bool = false;
    int i = paramScanSettings.getCallbackType();
    if (((i & 0x2) != 0) || ((i & 0x4) != 0))
    {
      if (this.mBluetoothAdapter.isOffloadedFilteringSupported()) {
        bool = this.mBluetoothAdapter.isHardwareTrackingFiltersAvailable();
      }
      return bool;
    }
    return true;
  }
  
  private boolean isSettingsAndFilterComboAllowed(ScanSettings paramScanSettings, List<ScanFilter> paramList)
  {
    if ((paramScanSettings.getCallbackType() & 0x6) != 0)
    {
      if (paramList == null) {
        return false;
      }
      paramScanSettings = paramList.iterator();
      while (paramScanSettings.hasNext()) {
        if (((ScanFilter)paramScanSettings.next()).isAllFieldsEmpty()) {
          return false;
        }
      }
    }
    return true;
  }
  
  private boolean isSettingsConfigAllowedForScan(ScanSettings paramScanSettings)
  {
    if (this.mBluetoothAdapter.isOffloadedFilteringSupported()) {
      return true;
    }
    return (paramScanSettings.getCallbackType() == 1) && (paramScanSettings.getReportDelayMillis() == 0L);
  }
  
  private void postCallbackError(final ScanCallback paramScanCallback, final int paramInt)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        paramScanCallback.onScanFailed(paramInt);
      }
    });
  }
  
  private void startScan(List<ScanFilter> paramList, ScanSettings paramScanSettings, WorkSource paramWorkSource, ScanCallback paramScanCallback, List<List<ResultStorageDescriptor>> paramList1)
  {
    BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
    if (paramScanCallback == null) {
      throw new IllegalArgumentException("callback is null");
    }
    if (paramScanSettings == null) {
      throw new IllegalArgumentException("settings is null");
    }
    synchronized (this.mLeScanClients)
    {
      if (this.mLeScanClients.containsKey(paramScanCallback))
      {
        postCallbackError(paramScanCallback, 1);
        return;
      }
      try
      {
        IBluetoothGatt localIBluetoothGatt1 = this.mBluetoothManager.getBluetoothGatt();
        if (localIBluetoothGatt1 == null)
        {
          postCallbackError(paramScanCallback, 3);
          return;
        }
      }
      catch (RemoteException localRemoteException)
      {
        IBluetoothGatt localIBluetoothGatt2;
        for (;;)
        {
          localIBluetoothGatt2 = null;
        }
        if (!isSettingsConfigAllowedForScan(paramScanSettings))
        {
          postCallbackError(paramScanCallback, 4);
          return;
        }
        if (!isHardwareResourcesAvailableForScan(paramScanSettings))
        {
          postCallbackError(paramScanCallback, 5);
          return;
        }
        if (!isSettingsAndFilterComboAllowed(paramScanSettings, paramList))
        {
          postCallbackError(paramScanCallback, 4);
          return;
        }
        new BleScanCallbackWrapper(localIBluetoothGatt2, paramList, paramScanSettings, paramWorkSource, paramScanCallback, paramList1).startRegisteration();
        return;
      }
    }
  }
  
  public void cleanup()
  {
    this.mLeScanClients.clear();
  }
  
  public void flushPendingScanResults(ScanCallback paramScanCallback)
  {
    BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
    if (paramScanCallback == null) {
      throw new IllegalArgumentException("callback cannot be null!");
    }
    synchronized (this.mLeScanClients)
    {
      paramScanCallback = (BleScanCallbackWrapper)this.mLeScanClients.get(paramScanCallback);
      if (paramScanCallback == null) {
        return;
      }
      paramScanCallback.flushPendingBatchResults();
      return;
    }
  }
  
  public void startScan(ScanCallback paramScanCallback)
  {
    startScan(null, new ScanSettings.Builder().build(), paramScanCallback);
  }
  
  public void startScan(List<ScanFilter> paramList, ScanSettings paramScanSettings, ScanCallback paramScanCallback)
  {
    startScan(paramList, paramScanSettings, null, paramScanCallback, null);
  }
  
  public void startScanFromSource(WorkSource paramWorkSource, ScanCallback paramScanCallback)
  {
    startScanFromSource(null, new ScanSettings.Builder().build(), paramWorkSource, paramScanCallback);
  }
  
  public void startScanFromSource(List<ScanFilter> paramList, ScanSettings paramScanSettings, WorkSource paramWorkSource, ScanCallback paramScanCallback)
  {
    startScan(paramList, paramScanSettings, paramWorkSource, paramScanCallback, null);
  }
  
  public void startTruncatedScan(List<TruncatedFilter> paramList, ScanSettings paramScanSettings, ScanCallback paramScanCallback)
  {
    int i = paramList.size();
    ArrayList localArrayList1 = new ArrayList(i);
    ArrayList localArrayList2 = new ArrayList(i);
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      TruncatedFilter localTruncatedFilter = (TruncatedFilter)paramList.next();
      localArrayList1.add(localTruncatedFilter.getFilter());
      localArrayList2.add(localTruncatedFilter.getStorageDescriptors());
    }
    startScan(localArrayList1, paramScanSettings, null, paramScanCallback, localArrayList2);
  }
  
  public void stopScan(ScanCallback paramScanCallback)
  {
    BluetoothLeUtils.checkAdapterStateOn(this.mBluetoothAdapter);
    synchronized (this.mLeScanClients)
    {
      paramScanCallback = (BleScanCallbackWrapper)this.mLeScanClients.remove(paramScanCallback);
      if (paramScanCallback == null)
      {
        Log.d("BluetoothLeScanner", "could not find callback wrapper");
        return;
      }
      paramScanCallback.stopLeScan();
      return;
    }
  }
  
  private class BleScanCallbackWrapper
    extends BluetoothGattCallbackWrapper
  {
    private static final int REGISTRATION_CALLBACK_TIMEOUT_MILLIS = 2000;
    private IBluetoothGatt mBluetoothGatt;
    private int mClientIf;
    private final List<ScanFilter> mFilters;
    private List<List<ResultStorageDescriptor>> mResultStorages;
    private final ScanCallback mScanCallback;
    private ScanSettings mSettings;
    private final WorkSource mWorkSource;
    
    public BleScanCallbackWrapper(List<ScanFilter> paramList, ScanSettings paramScanSettings, WorkSource paramWorkSource, ScanCallback paramScanCallback, List<List<ResultStorageDescriptor>> paramList1)
    {
      this.mBluetoothGatt = paramList;
      this.mFilters = paramScanSettings;
      this.mSettings = paramWorkSource;
      this.mWorkSource = paramScanCallback;
      this.mScanCallback = paramList1;
      this.mClientIf = 0;
      List localList;
      this.mResultStorages = localList;
    }
    
    private String getCurProcessName()
    {
      Object localObject = ActivityThread.currentApplication().getApplicationContext();
      int i = Process.myPid();
      localObject = ((ActivityManager)((Context)localObject).getSystemService("activity")).getRunningAppProcesses().iterator();
      while (((Iterator)localObject).hasNext())
      {
        ActivityManager.RunningAppProcessInfo localRunningAppProcessInfo = (ActivityManager.RunningAppProcessInfo)((Iterator)localObject).next();
        if (localRunningAppProcessInfo.pid == i) {
          return localRunningAppProcessInfo.processName;
        }
      }
      return null;
    }
    
    /* Error */
    void flushPendingBatchResults()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 54	android/bluetooth/le/BluetoothLeScanner$BleScanCallbackWrapper:mClientIf	I
      //   6: ifgt +34 -> 40
      //   9: ldc 122
      //   11: new 124	java/lang/StringBuilder
      //   14: dup
      //   15: invokespecial 125	java/lang/StringBuilder:<init>	()V
      //   18: ldc 127
      //   20: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   23: aload_0
      //   24: getfield 54	android/bluetooth/le/BluetoothLeScanner$BleScanCallbackWrapper:mClientIf	I
      //   27: invokevirtual 134	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   30: invokevirtual 137	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   33: invokestatic 143	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   36: pop
      //   37: aload_0
      //   38: monitorexit
      //   39: return
      //   40: aload_0
      //   41: getfield 46	android/bluetooth/le/BluetoothLeScanner$BleScanCallbackWrapper:mBluetoothGatt	Landroid/bluetooth/IBluetoothGatt;
      //   44: aload_0
      //   45: getfield 54	android/bluetooth/le/BluetoothLeScanner$BleScanCallbackWrapper:mClientIf	I
      //   48: iconst_0
      //   49: invokeinterface 148 3 0
      //   54: aload_0
      //   55: monitorexit
      //   56: return
      //   57: astore_1
      //   58: ldc 122
      //   60: ldc -106
      //   62: aload_1
      //   63: invokestatic 153	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   66: pop
      //   67: goto -13 -> 54
      //   70: astore_1
      //   71: aload_0
      //   72: monitorexit
      //   73: aload_1
      //   74: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	75	0	this	BleScanCallbackWrapper
      //   57	6	1	localRemoteException	RemoteException
      //   70	4	1	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   40	54	57	android/os/RemoteException
      //   2	37	70	finally
      //   40	54	70	finally
      //   58	67	70	finally
    }
    
    public void onBatchScanResults(final List<ScanResult> paramList)
    {
      new Handler(Looper.getMainLooper()).post(new Runnable()
      {
        public void run()
        {
          BluetoothLeScanner.BleScanCallbackWrapper.-get0(BluetoothLeScanner.BleScanCallbackWrapper.this).onBatchScanResults(paramList);
        }
      });
    }
    
    public void onClientRegistered(int paramInt1, int paramInt2)
    {
      Log.d("BluetoothLeScanner", "onClientRegistered() - status=" + paramInt1 + " clientIf=" + paramInt2 + " mClientIf=" + this.mClientIf);
      Log.d("BluetoothLeScanner", "get call process name: " + getCurProcessName());
      if (paramInt1 == 0) {}
      for (;;)
      {
        try
        {
          if (this.mClientIf == -1)
          {
            this.mBluetoothGatt.unregisterClient(paramInt2);
            notifyAll();
            return;
          }
          this.mClientIf = paramInt2;
          this.mBluetoothGatt.startScan(this.mClientIf, false, this.mSettings, this.mFilters, this.mWorkSource, this.mResultStorages, ActivityThread.currentOpPackageName());
          continue;
        }
        catch (RemoteException localRemoteException)
        {
          Log.e("BluetoothLeScanner", "fail to start le scan: " + localRemoteException);
          this.mClientIf = -1;
          continue;
        }
        finally {}
        this.mClientIf = -1;
      }
    }
    
    public void onFoundOrLost(final boolean paramBoolean, final ScanResult paramScanResult)
    {
      try
      {
        int i = this.mClientIf;
        if (i <= 0) {
          return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
          public void run()
          {
            if (paramBoolean)
            {
              BluetoothLeScanner.BleScanCallbackWrapper.-get0(BluetoothLeScanner.BleScanCallbackWrapper.this).onScanResult(2, paramScanResult);
              return;
            }
            BluetoothLeScanner.BleScanCallbackWrapper.-get0(BluetoothLeScanner.BleScanCallbackWrapper.this).onScanResult(4, paramScanResult);
          }
        });
        return;
      }
      finally {}
    }
    
    public void onScanManagerErrorCallback(int paramInt)
    {
      try
      {
        int i = this.mClientIf;
        if (i <= 0) {
          return;
        }
        BluetoothLeScanner.-wrap0(BluetoothLeScanner.this, this.mScanCallback, paramInt);
        return;
      }
      finally {}
    }
    
    public void onScanResult(final ScanResult paramScanResult)
    {
      try
      {
        int i = this.mClientIf;
        if (i <= 0) {
          return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable()
        {
          public void run()
          {
            BluetoothLeScanner.BleScanCallbackWrapper.-get0(BluetoothLeScanner.BleScanCallbackWrapper.this).onScanResult(1, paramScanResult);
          }
        });
        return;
      }
      finally {}
    }
    
    public void startRegisteration()
    {
      for (;;)
      {
        try
        {
          int i = this.mClientIf;
          if (i == -1) {
            return;
          }
          try
          {
            UUID localUUID = UUID.randomUUID();
            this.mBluetoothGatt.registerClient(new ParcelUuid(localUUID), this);
            wait(2000L);
            if (this.mClientIf > 0)
            {
              BluetoothLeScanner.-get0(BluetoothLeScanner.this).put(this.mScanCallback, this);
              return;
            }
          }
          catch (InterruptedException|RemoteException localInterruptedException)
          {
            Log.e("BluetoothLeScanner", "application registeration exception", localInterruptedException);
            BluetoothLeScanner.-wrap0(BluetoothLeScanner.this, this.mScanCallback, 3);
            continue;
          }
          if (this.mClientIf != 0) {
            break label115;
          }
        }
        finally {}
        this.mClientIf = -1;
        label115:
        BluetoothLeScanner.-wrap0(BluetoothLeScanner.this, this.mScanCallback, 2);
      }
    }
    
    /* Error */
    public void stopLeScan()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 54	android/bluetooth/le/BluetoothLeScanner$BleScanCallbackWrapper:mClientIf	I
      //   6: ifgt +34 -> 40
      //   9: ldc 122
      //   11: new 124	java/lang/StringBuilder
      //   14: dup
      //   15: invokespecial 125	java/lang/StringBuilder:<init>	()V
      //   18: ldc 127
      //   20: invokevirtual 131	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   23: aload_0
      //   24: getfield 54	android/bluetooth/le/BluetoothLeScanner$BleScanCallbackWrapper:mClientIf	I
      //   27: invokevirtual 134	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   30: invokevirtual 137	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   33: invokestatic 143	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   36: pop
      //   37: aload_0
      //   38: monitorexit
      //   39: return
      //   40: aload_0
      //   41: getfield 46	android/bluetooth/le/BluetoothLeScanner$BleScanCallbackWrapper:mBluetoothGatt	Landroid/bluetooth/IBluetoothGatt;
      //   44: aload_0
      //   45: getfield 54	android/bluetooth/le/BluetoothLeScanner$BleScanCallbackWrapper:mClientIf	I
      //   48: iconst_0
      //   49: invokeinterface 262 3 0
      //   54: aload_0
      //   55: getfield 46	android/bluetooth/le/BluetoothLeScanner$BleScanCallbackWrapper:mBluetoothGatt	Landroid/bluetooth/IBluetoothGatt;
      //   58: aload_0
      //   59: getfield 54	android/bluetooth/le/BluetoothLeScanner$BleScanCallbackWrapper:mClientIf	I
      //   62: invokeinterface 193 2 0
      //   67: aload_0
      //   68: iconst_m1
      //   69: putfield 54	android/bluetooth/le/BluetoothLeScanner$BleScanCallbackWrapper:mClientIf	I
      //   72: aload_0
      //   73: monitorexit
      //   74: return
      //   75: astore_1
      //   76: ldc 122
      //   78: ldc_w 264
      //   81: aload_1
      //   82: invokestatic 153	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   85: pop
      //   86: goto -19 -> 67
      //   89: astore_1
      //   90: aload_0
      //   91: monitorexit
      //   92: aload_1
      //   93: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	94	0	this	BleScanCallbackWrapper
      //   75	7	1	localRemoteException	RemoteException
      //   89	4	1	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   40	67	75	android/os/RemoteException
      //   2	37	89	finally
      //   40	67	89	finally
      //   67	72	89	finally
      //   76	86	89	finally
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/bluetooth/le/BluetoothLeScanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */