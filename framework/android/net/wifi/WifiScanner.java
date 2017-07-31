package android.net.wifi;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.WorkSource;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;

public class WifiScanner
{
  private static final int BASE = 159744;
  public static final int CMD_AP_FOUND = 159753;
  public static final int CMD_AP_LOST = 159754;
  public static final int CMD_CONFIGURE_WIFI_CHANGE = 159757;
  public static final int CMD_DEREGISTER_SCAN_LISTENER = 159772;
  public static final int CMD_FULL_SCAN_RESULT = 159764;
  public static final int CMD_GET_SCAN_RESULTS = 159748;
  public static final int CMD_OP_FAILED = 159762;
  public static final int CMD_OP_SUCCEEDED = 159761;
  public static final int CMD_PERIOD_CHANGED = 159763;
  public static final int CMD_PNO_NETWORK_FOUND = 159770;
  public static final int CMD_REGISTER_SCAN_LISTENER = 159771;
  public static final int CMD_RESET_HOTLIST = 159751;
  public static final int CMD_SCAN = 159744;
  public static final int CMD_SCAN_RESULT = 159749;
  public static final int CMD_SET_HOTLIST = 159750;
  public static final int CMD_SINGLE_SCAN_COMPLETED = 159767;
  public static final int CMD_START_BACKGROUND_SCAN = 159746;
  public static final int CMD_START_PNO_SCAN = 159768;
  public static final int CMD_START_SINGLE_SCAN = 159765;
  public static final int CMD_START_TRACKING_CHANGE = 159755;
  public static final int CMD_STOP_BACKGROUND_SCAN = 159747;
  public static final int CMD_STOP_PNO_SCAN = 159769;
  public static final int CMD_STOP_SINGLE_SCAN = 159766;
  public static final int CMD_STOP_TRACKING_CHANGE = 159756;
  public static final int CMD_WIFI_CHANGES_STABILIZED = 159760;
  public static final int CMD_WIFI_CHANGE_DETECTED = 159759;
  private static final boolean DBG = false;
  public static final String GET_AVAILABLE_CHANNELS_EXTRA = "Channels";
  private static final int INVALID_KEY = 0;
  public static final int MAX_SCAN_PERIOD_MS = 1024000;
  public static final int MIN_SCAN_PERIOD_MS = 1000;
  public static final String PNO_PARAMS_PNO_SETTINGS_KEY = "PnoSettings";
  public static final String PNO_PARAMS_SCAN_SETTINGS_KEY = "ScanSettings";
  public static final int REASON_DUPLICATE_REQEUST = -5;
  public static final int REASON_INVALID_LISTENER = -2;
  public static final int REASON_INVALID_REQUEST = -3;
  public static final int REASON_NOT_AUTHORIZED = -4;
  public static final int REASON_SUCCEEDED = 0;
  public static final int REASON_UNSPECIFIED = -1;
  @Deprecated
  public static final int REPORT_EVENT_AFTER_BUFFER_FULL = 0;
  public static final int REPORT_EVENT_AFTER_EACH_SCAN = 1;
  public static final int REPORT_EVENT_FULL_SCAN_RESULT = 2;
  public static final int REPORT_EVENT_NO_BATCH = 4;
  public static final String SCAN_PARAMS_SCAN_SETTINGS_KEY = "ScanSettings";
  public static final String SCAN_PARAMS_WORK_SOURCE_KEY = "WorkSource";
  private static final String TAG = "WifiScanner";
  public static final int WIFI_BAND_24_GHZ = 1;
  public static final int WIFI_BAND_5_GHZ = 2;
  public static final int WIFI_BAND_5_GHZ_DFS_ONLY = 4;
  public static final int WIFI_BAND_5_GHZ_WITH_DFS = 6;
  public static final int WIFI_BAND_BOTH = 3;
  public static final int WIFI_BAND_BOTH_WITH_DFS = 7;
  public static final int WIFI_BAND_UNSPECIFIED = 0;
  private AsyncChannel mAsyncChannel;
  private Context mContext;
  private final Handler mInternalHandler;
  private int mListenerKey = 1;
  private final SparseArray mListenerMap = new SparseArray();
  private final Object mListenerMapLock = new Object();
  private IWifiScanner mService;
  
  public WifiScanner(Context paramContext, IWifiScanner paramIWifiScanner, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mService = paramIWifiScanner;
    try
    {
      paramContext = this.mService.getMessenger();
      if (paramContext == null) {
        throw new IllegalStateException("getMessenger() returned null!  This is invalid.");
      }
    }
    catch (RemoteException paramContext)
    {
      throw paramContext.rethrowFromSystemServer();
    }
    this.mAsyncChannel = new AsyncChannel();
    this.mInternalHandler = new ServiceHandler(paramLooper);
    this.mAsyncChannel.connectSync(this.mContext, this.mInternalHandler, paramContext);
    this.mAsyncChannel.sendMessage(69633);
  }
  
  private int addListener(ActionListener paramActionListener)
  {
    synchronized (this.mListenerMapLock)
    {
      if (getListenerKey(paramActionListener) != 0) {}
      int j;
      for (int i = 1;; i = 0)
      {
        j = putListener(paramActionListener);
        if (i == 0) {
          break;
        }
        paramActionListener = new OperationResult(-5, "Outstanding request with same key not stopped yet");
        Message.obtain(this.mInternalHandler, 159762, 0, j, paramActionListener).sendToTarget();
        return 0;
      }
      return j;
    }
  }
  
  private Object getListener(int paramInt)
  {
    if (paramInt == 0) {
      return null;
    }
    synchronized (this.mListenerMapLock)
    {
      Object localObject2 = this.mListenerMap.get(paramInt);
      return localObject2;
    }
  }
  
  private int getListenerKey(Object paramObject)
  {
    if (paramObject == null) {
      return 0;
    }
    synchronized (this.mListenerMapLock)
    {
      int i = this.mListenerMap.indexOfValue(paramObject);
      if (i == -1) {
        return 0;
      }
      i = this.mListenerMap.keyAt(i);
      return i;
    }
  }
  
  private int putListener(Object paramObject)
  {
    if (paramObject == null) {
      return 0;
    }
    synchronized (this.mListenerMapLock)
    {
      int i;
      do
      {
        i = this.mListenerKey;
        this.mListenerKey = (i + 1);
      } while (i == 0);
      this.mListenerMap.put(i, paramObject);
      return i;
    }
  }
  
  private int removeListener(Object arg1)
  {
    int i = getListenerKey(???);
    if (i == 0)
    {
      Log.e("WifiScanner", "listener cannot be found");
      return i;
    }
    synchronized (this.mListenerMapLock)
    {
      this.mListenerMap.remove(i);
      return i;
    }
  }
  
  private Object removeListener(int paramInt)
  {
    if (paramInt == 0) {
      return null;
    }
    synchronized (this.mListenerMapLock)
    {
      Object localObject2 = this.mListenerMap.get(paramInt);
      this.mListenerMap.remove(paramInt);
      return localObject2;
    }
  }
  
  private void startPnoScan(ScanSettings paramScanSettings, PnoSettings paramPnoSettings, int paramInt)
  {
    Bundle localBundle = new Bundle();
    paramScanSettings.isPnoScan = true;
    localBundle.putParcelable("ScanSettings", paramScanSettings);
    localBundle.putParcelable("PnoSettings", paramPnoSettings);
    this.mAsyncChannel.sendMessage(159768, 0, paramInt, localBundle);
  }
  
  private void validateChannel()
  {
    if (this.mAsyncChannel == null) {
      throw new IllegalStateException("No permission to access and change wifi or a bad initialization");
    }
  }
  
  public void configureWifiChange(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, BssidInfo[] paramArrayOfBssidInfo)
  {
    validateChannel();
    WifiChangeSettings localWifiChangeSettings = new WifiChangeSettings();
    localWifiChangeSettings.rssiSampleSize = paramInt1;
    localWifiChangeSettings.lostApSampleSize = paramInt2;
    localWifiChangeSettings.unchangedSampleSize = paramInt3;
    localWifiChangeSettings.minApsBreachingThreshold = paramInt4;
    localWifiChangeSettings.periodInMs = paramInt5;
    localWifiChangeSettings.bssidInfos = paramArrayOfBssidInfo;
    configureWifiChange(localWifiChangeSettings);
  }
  
  public void configureWifiChange(WifiChangeSettings paramWifiChangeSettings)
  {
    validateChannel();
    this.mAsyncChannel.sendMessage(159757, 0, 0, paramWifiChangeSettings);
  }
  
  public void deregisterScanListener(ScanListener paramScanListener)
  {
    Preconditions.checkNotNull(paramScanListener, "listener cannot be null");
    int i = removeListener(paramScanListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    this.mAsyncChannel.sendMessage(159772, 0, i);
  }
  
  public List<Integer> getAvailableChannels(int paramInt)
  {
    try
    {
      ArrayList localArrayList = this.mService.getAvailableChannels(paramInt).getIntegerArrayList("Channels");
      return localArrayList;
    }
    catch (RemoteException localRemoteException) {}
    return null;
  }
  
  public boolean getScanResults()
  {
    boolean bool = false;
    validateChannel();
    if (this.mAsyncChannel.sendMessageSynchronously(159748, 0).what == 159761) {
      bool = true;
    }
    return bool;
  }
  
  public void registerScanListener(ScanListener paramScanListener)
  {
    Preconditions.checkNotNull(paramScanListener, "listener cannot be null");
    int i = addListener(paramScanListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    this.mAsyncChannel.sendMessage(159771, 0, i);
  }
  
  public void startBackgroundScan(ScanSettings paramScanSettings, ScanListener paramScanListener)
  {
    startBackgroundScan(paramScanSettings, paramScanListener, null);
  }
  
  public void startBackgroundScan(ScanSettings paramScanSettings, ScanListener paramScanListener, WorkSource paramWorkSource)
  {
    Preconditions.checkNotNull(paramScanListener, "listener cannot be null");
    int i = addListener(paramScanListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    paramScanListener = new Bundle();
    paramScanListener.putParcelable("ScanSettings", paramScanSettings);
    paramScanListener.putParcelable("WorkSource", paramWorkSource);
    this.mAsyncChannel.sendMessage(159746, 0, i, paramScanListener);
  }
  
  public void startConnectedPnoScan(ScanSettings paramScanSettings, PnoSettings paramPnoSettings, PnoScanListener paramPnoScanListener)
  {
    Preconditions.checkNotNull(paramPnoScanListener, "listener cannot be null");
    Preconditions.checkNotNull(paramPnoSettings, "pnoSettings cannot be null");
    int i = addListener(paramPnoScanListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    paramPnoSettings.isConnected = true;
    startPnoScan(paramScanSettings, paramPnoSettings, i);
  }
  
  public void startDisconnectedPnoScan(ScanSettings paramScanSettings, PnoSettings paramPnoSettings, PnoScanListener paramPnoScanListener)
  {
    Preconditions.checkNotNull(paramPnoScanListener, "listener cannot be null");
    Preconditions.checkNotNull(paramPnoSettings, "pnoSettings cannot be null");
    int i = addListener(paramPnoScanListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    paramPnoSettings.isConnected = false;
    startPnoScan(paramScanSettings, paramPnoSettings, i);
  }
  
  public void startScan(ScanSettings paramScanSettings, ScanListener paramScanListener)
  {
    startScan(paramScanSettings, paramScanListener, null);
  }
  
  public void startScan(ScanSettings paramScanSettings, ScanListener paramScanListener, WorkSource paramWorkSource)
  {
    Preconditions.checkNotNull(paramScanListener, "listener cannot be null");
    int i = addListener(paramScanListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    paramScanListener = new Bundle();
    paramScanListener.putParcelable("ScanSettings", paramScanSettings);
    paramScanListener.putParcelable("WorkSource", paramWorkSource);
    this.mAsyncChannel.sendMessage(159765, 0, i, paramScanListener);
  }
  
  public void startTrackingBssids(BssidInfo[] paramArrayOfBssidInfo, int paramInt, BssidListener paramBssidListener)
  {
    Preconditions.checkNotNull(paramBssidListener, "listener cannot be null");
    int i = addListener(paramBssidListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    paramBssidListener = new HotlistSettings();
    paramBssidListener.bssidInfos = paramArrayOfBssidInfo;
    paramBssidListener.apLostThreshold = paramInt;
    this.mAsyncChannel.sendMessage(159750, 0, i, paramBssidListener);
  }
  
  public void startTrackingWifiChange(WifiChangeListener paramWifiChangeListener)
  {
    Preconditions.checkNotNull(paramWifiChangeListener, "listener cannot be null");
    int i = addListener(paramWifiChangeListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    this.mAsyncChannel.sendMessage(159755, 0, i);
  }
  
  public void stopBackgroundScan(ScanListener paramScanListener)
  {
    Preconditions.checkNotNull(paramScanListener, "listener cannot be null");
    int i = removeListener(paramScanListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    this.mAsyncChannel.sendMessage(159747, 0, i);
  }
  
  public void stopPnoScan(ScanListener paramScanListener)
  {
    Preconditions.checkNotNull(paramScanListener, "listener cannot be null");
    int i = removeListener(paramScanListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    this.mAsyncChannel.sendMessage(159769, 0, i);
  }
  
  public void stopScan(ScanListener paramScanListener)
  {
    Preconditions.checkNotNull(paramScanListener, "listener cannot be null");
    int i = removeListener(paramScanListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    this.mAsyncChannel.sendMessage(159766, 0, i);
  }
  
  public void stopTrackingBssids(BssidListener paramBssidListener)
  {
    Preconditions.checkNotNull(paramBssidListener, "listener cannot be null");
    int i = removeListener(paramBssidListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    this.mAsyncChannel.sendMessage(159751, 0, i);
  }
  
  public void stopTrackingWifiChange(WifiChangeListener paramWifiChangeListener)
  {
    int i = removeListener(paramWifiChangeListener);
    if (i == 0) {
      return;
    }
    validateChannel();
    this.mAsyncChannel.sendMessage(159756, 0, i);
  }
  
  public static abstract interface ActionListener
  {
    public abstract void onFailure(int paramInt, String paramString);
    
    public abstract void onSuccess();
  }
  
  public static class BssidInfo
  {
    public String bssid;
    public int frequencyHint;
    public int high;
    public int low;
  }
  
  public static abstract interface BssidListener
    extends WifiScanner.ActionListener
  {
    public abstract void onFound(ScanResult[] paramArrayOfScanResult);
    
    public abstract void onLost(ScanResult[] paramArrayOfScanResult);
  }
  
  public static class ChannelSpec
  {
    public int dwellTimeMS;
    public int frequency;
    public boolean passive;
    
    public ChannelSpec(int paramInt)
    {
      this.frequency = paramInt;
      this.passive = false;
      this.dwellTimeMS = 0;
    }
  }
  
  public static class HotlistSettings
    implements Parcelable
  {
    public static final Parcelable.Creator<HotlistSettings> CREATOR = new Parcelable.Creator()
    {
      public WifiScanner.HotlistSettings createFromParcel(Parcel paramAnonymousParcel)
      {
        WifiScanner.HotlistSettings localHotlistSettings = new WifiScanner.HotlistSettings();
        localHotlistSettings.apLostThreshold = paramAnonymousParcel.readInt();
        int j = paramAnonymousParcel.readInt();
        localHotlistSettings.bssidInfos = new WifiScanner.BssidInfo[j];
        int i = 0;
        while (i < j)
        {
          WifiScanner.BssidInfo localBssidInfo = new WifiScanner.BssidInfo();
          localBssidInfo.bssid = paramAnonymousParcel.readString();
          localBssidInfo.low = paramAnonymousParcel.readInt();
          localBssidInfo.high = paramAnonymousParcel.readInt();
          localBssidInfo.frequencyHint = paramAnonymousParcel.readInt();
          localHotlistSettings.bssidInfos[i] = localBssidInfo;
          i += 1;
        }
        return localHotlistSettings;
      }
      
      public WifiScanner.HotlistSettings[] newArray(int paramAnonymousInt)
      {
        return new WifiScanner.HotlistSettings[paramAnonymousInt];
      }
    };
    public int apLostThreshold;
    public WifiScanner.BssidInfo[] bssidInfos;
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.apLostThreshold);
      if (this.bssidInfos != null)
      {
        paramParcel.writeInt(this.bssidInfos.length);
        paramInt = 0;
        while (paramInt < this.bssidInfos.length)
        {
          WifiScanner.BssidInfo localBssidInfo = this.bssidInfos[paramInt];
          paramParcel.writeString(localBssidInfo.bssid);
          paramParcel.writeInt(localBssidInfo.low);
          paramParcel.writeInt(localBssidInfo.high);
          paramParcel.writeInt(localBssidInfo.frequencyHint);
          paramInt += 1;
        }
      }
      paramParcel.writeInt(0);
    }
  }
  
  public static class OperationResult
    implements Parcelable
  {
    public static final Parcelable.Creator<OperationResult> CREATOR = new Parcelable.Creator()
    {
      public WifiScanner.OperationResult createFromParcel(Parcel paramAnonymousParcel)
      {
        return new WifiScanner.OperationResult(paramAnonymousParcel.readInt(), paramAnonymousParcel.readString());
      }
      
      public WifiScanner.OperationResult[] newArray(int paramAnonymousInt)
      {
        return new WifiScanner.OperationResult[paramAnonymousInt];
      }
    };
    public String description;
    public int reason;
    
    public OperationResult(int paramInt, String paramString)
    {
      this.reason = paramInt;
      this.description = paramString;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.reason);
      paramParcel.writeString(this.description);
    }
  }
  
  public static class ParcelableScanData
    implements Parcelable
  {
    public static final Parcelable.Creator<ParcelableScanData> CREATOR = new Parcelable.Creator()
    {
      public WifiScanner.ParcelableScanData createFromParcel(Parcel paramAnonymousParcel)
      {
        int j = paramAnonymousParcel.readInt();
        WifiScanner.ScanData[] arrayOfScanData = new WifiScanner.ScanData[j];
        int i = 0;
        while (i < j)
        {
          arrayOfScanData[i] = ((WifiScanner.ScanData)WifiScanner.ScanData.CREATOR.createFromParcel(paramAnonymousParcel));
          i += 1;
        }
        return new WifiScanner.ParcelableScanData(arrayOfScanData);
      }
      
      public WifiScanner.ParcelableScanData[] newArray(int paramAnonymousInt)
      {
        return new WifiScanner.ParcelableScanData[paramAnonymousInt];
      }
    };
    public WifiScanner.ScanData[] mResults;
    
    public ParcelableScanData(WifiScanner.ScanData[] paramArrayOfScanData)
    {
      this.mResults = paramArrayOfScanData;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public WifiScanner.ScanData[] getResults()
    {
      return this.mResults;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (this.mResults != null)
      {
        paramParcel.writeInt(this.mResults.length);
        int i = 0;
        while (i < this.mResults.length)
        {
          this.mResults[i].writeToParcel(paramParcel, paramInt);
          i += 1;
        }
      }
      paramParcel.writeInt(0);
    }
  }
  
  public static class ParcelableScanResults
    implements Parcelable
  {
    public static final Parcelable.Creator<ParcelableScanResults> CREATOR = new Parcelable.Creator()
    {
      public WifiScanner.ParcelableScanResults createFromParcel(Parcel paramAnonymousParcel)
      {
        int j = paramAnonymousParcel.readInt();
        ScanResult[] arrayOfScanResult = new ScanResult[j];
        int i = 0;
        while (i < j)
        {
          arrayOfScanResult[i] = ((ScanResult)ScanResult.CREATOR.createFromParcel(paramAnonymousParcel));
          i += 1;
        }
        return new WifiScanner.ParcelableScanResults(arrayOfScanResult);
      }
      
      public WifiScanner.ParcelableScanResults[] newArray(int paramAnonymousInt)
      {
        return new WifiScanner.ParcelableScanResults[paramAnonymousInt];
      }
    };
    public ScanResult[] mResults;
    
    public ParcelableScanResults(ScanResult[] paramArrayOfScanResult)
    {
      this.mResults = paramArrayOfScanResult;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public ScanResult[] getResults()
    {
      return this.mResults;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (this.mResults != null)
      {
        paramParcel.writeInt(this.mResults.length);
        int i = 0;
        while (i < this.mResults.length)
        {
          this.mResults[i].writeToParcel(paramParcel, paramInt);
          i += 1;
        }
      }
      paramParcel.writeInt(0);
    }
  }
  
  public static abstract interface PnoScanListener
    extends WifiScanner.ScanListener
  {
    public abstract void onPnoNetworkFound(ScanResult[] paramArrayOfScanResult);
  }
  
  public static class PnoSettings
    implements Parcelable
  {
    public static final Parcelable.Creator<PnoSettings> CREATOR = new Parcelable.Creator()
    {
      public WifiScanner.PnoSettings createFromParcel(Parcel paramAnonymousParcel)
      {
        boolean bool = true;
        WifiScanner.PnoSettings localPnoSettings = new WifiScanner.PnoSettings();
        if (paramAnonymousParcel.readInt() == 1) {}
        for (;;)
        {
          localPnoSettings.isConnected = bool;
          localPnoSettings.min5GHzRssi = paramAnonymousParcel.readInt();
          localPnoSettings.min24GHzRssi = paramAnonymousParcel.readInt();
          localPnoSettings.initialScoreMax = paramAnonymousParcel.readInt();
          localPnoSettings.currentConnectionBonus = paramAnonymousParcel.readInt();
          localPnoSettings.sameNetworkBonus = paramAnonymousParcel.readInt();
          localPnoSettings.secureBonus = paramAnonymousParcel.readInt();
          localPnoSettings.band5GHzBonus = paramAnonymousParcel.readInt();
          int j = paramAnonymousParcel.readInt();
          localPnoSettings.networkList = new WifiScanner.PnoSettings.PnoNetwork[j];
          int i = 0;
          while (i < j)
          {
            WifiScanner.PnoSettings.PnoNetwork localPnoNetwork = new WifiScanner.PnoSettings.PnoNetwork(paramAnonymousParcel.readString());
            localPnoNetwork.networkId = paramAnonymousParcel.readInt();
            localPnoNetwork.priority = paramAnonymousParcel.readInt();
            localPnoNetwork.flags = paramAnonymousParcel.readByte();
            localPnoNetwork.authBitField = paramAnonymousParcel.readByte();
            localPnoSettings.networkList[i] = localPnoNetwork;
            i += 1;
          }
          bool = false;
        }
        return localPnoSettings;
      }
      
      public WifiScanner.PnoSettings[] newArray(int paramAnonymousInt)
      {
        return new WifiScanner.PnoSettings[paramAnonymousInt];
      }
    };
    public int band5GHzBonus;
    public int currentConnectionBonus;
    public int initialScoreMax;
    public boolean isConnected;
    public int min24GHzRssi;
    public int min5GHzRssi;
    public PnoNetwork[] networkList;
    public int sameNetworkBonus;
    public int secureBonus;
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (this.isConnected) {}
      for (paramInt = 1;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeInt(this.min5GHzRssi);
        paramParcel.writeInt(this.min24GHzRssi);
        paramParcel.writeInt(this.initialScoreMax);
        paramParcel.writeInt(this.currentConnectionBonus);
        paramParcel.writeInt(this.sameNetworkBonus);
        paramParcel.writeInt(this.secureBonus);
        paramParcel.writeInt(this.band5GHzBonus);
        if (this.networkList == null) {
          break;
        }
        paramParcel.writeInt(this.networkList.length);
        paramInt = 0;
        while (paramInt < this.networkList.length)
        {
          paramParcel.writeString(this.networkList[paramInt].ssid);
          paramParcel.writeInt(this.networkList[paramInt].networkId);
          paramParcel.writeInt(this.networkList[paramInt].priority);
          paramParcel.writeByte(this.networkList[paramInt].flags);
          paramParcel.writeByte(this.networkList[paramInt].authBitField);
          paramInt += 1;
        }
      }
      paramParcel.writeInt(0);
    }
    
    public static class PnoNetwork
    {
      public static final byte AUTH_CODE_EAPOL = 4;
      public static final byte AUTH_CODE_OPEN = 1;
      public static final byte AUTH_CODE_PSK = 2;
      public static final byte FLAG_A_BAND = 2;
      public static final byte FLAG_DIRECTED_SCAN = 1;
      public static final byte FLAG_G_BAND = 4;
      public static final byte FLAG_SAME_NETWORK = 16;
      public static final byte FLAG_STRICT_MATCH = 8;
      public byte authBitField;
      public byte flags;
      public int networkId;
      public int priority;
      public String ssid;
      
      public PnoNetwork(String paramString)
      {
        this.ssid = paramString;
        this.flags = 0;
        this.authBitField = 0;
      }
    }
  }
  
  public static class ScanData
    implements Parcelable
  {
    public static final Parcelable.Creator<ScanData> CREATOR = new Parcelable.Creator()
    {
      public WifiScanner.ScanData createFromParcel(Parcel paramAnonymousParcel)
      {
        int j = paramAnonymousParcel.readInt();
        int k = paramAnonymousParcel.readInt();
        int m = paramAnonymousParcel.readInt();
        if (paramAnonymousParcel.readInt() != 0) {}
        ScanResult[] arrayOfScanResult;
        for (boolean bool = true;; bool = false)
        {
          int n = paramAnonymousParcel.readInt();
          arrayOfScanResult = new ScanResult[n];
          int i = 0;
          while (i < n)
          {
            arrayOfScanResult[i] = ((ScanResult)ScanResult.CREATOR.createFromParcel(paramAnonymousParcel));
            i += 1;
          }
        }
        return new WifiScanner.ScanData(j, k, m, bool, arrayOfScanResult);
      }
      
      public WifiScanner.ScanData[] newArray(int paramAnonymousInt)
      {
        return new WifiScanner.ScanData[paramAnonymousInt];
      }
    };
    private boolean mAllChannelsScanned;
    private int mBucketsScanned;
    private int mFlags;
    private int mId;
    private ScanResult[] mResults;
    
    ScanData() {}
    
    public ScanData(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, ScanResult[] paramArrayOfScanResult)
    {
      this.mId = paramInt1;
      this.mFlags = paramInt2;
      this.mBucketsScanned = paramInt3;
      this.mAllChannelsScanned = paramBoolean;
      this.mResults = paramArrayOfScanResult;
    }
    
    public ScanData(int paramInt1, int paramInt2, ScanResult[] paramArrayOfScanResult)
    {
      this.mId = paramInt1;
      this.mFlags = paramInt2;
      this.mResults = paramArrayOfScanResult;
    }
    
    public ScanData(ScanData paramScanData)
    {
      this.mId = paramScanData.mId;
      this.mFlags = paramScanData.mFlags;
      this.mBucketsScanned = paramScanData.mBucketsScanned;
      this.mAllChannelsScanned = paramScanData.mAllChannelsScanned;
      this.mResults = new ScanResult[paramScanData.mResults.length];
      int i = 0;
      while (i < paramScanData.mResults.length)
      {
        ScanResult localScanResult = new ScanResult(paramScanData.mResults[i]);
        this.mResults[i] = localScanResult;
        i += 1;
      }
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public int getBucketsScanned()
    {
      return this.mBucketsScanned;
    }
    
    public int getFlags()
    {
      return this.mFlags;
    }
    
    public int getId()
    {
      return this.mId;
    }
    
    public ScanResult[] getResults()
    {
      return this.mResults;
    }
    
    public boolean isAllChannelsScanned()
    {
      return this.mAllChannelsScanned;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 0;
      if (this.mResults != null)
      {
        paramParcel.writeInt(this.mId);
        paramParcel.writeInt(this.mFlags);
        paramParcel.writeInt(this.mBucketsScanned);
        if (this.mAllChannelsScanned) {
          i = 1;
        }
        paramParcel.writeInt(i);
        paramParcel.writeInt(this.mResults.length);
        i = 0;
        while (i < this.mResults.length)
        {
          this.mResults[i].writeToParcel(paramParcel, paramInt);
          i += 1;
        }
      }
      paramParcel.writeInt(0);
    }
  }
  
  public static abstract interface ScanListener
    extends WifiScanner.ActionListener
  {
    public abstract void onFullResult(ScanResult paramScanResult);
    
    public abstract void onPeriodChanged(int paramInt);
    
    public abstract void onResults(WifiScanner.ScanData[] paramArrayOfScanData);
  }
  
  public static class ScanSettings
    implements Parcelable
  {
    public static final Parcelable.Creator<ScanSettings> CREATOR = new Parcelable.Creator()
    {
      public WifiScanner.ScanSettings createFromParcel(Parcel paramAnonymousParcel)
      {
        WifiScanner.ScanSettings localScanSettings = new WifiScanner.ScanSettings();
        localScanSettings.band = paramAnonymousParcel.readInt();
        localScanSettings.periodInMs = paramAnonymousParcel.readInt();
        localScanSettings.reportEvents = paramAnonymousParcel.readInt();
        localScanSettings.numBssidsPerScan = paramAnonymousParcel.readInt();
        localScanSettings.maxScansToCache = paramAnonymousParcel.readInt();
        localScanSettings.maxPeriodInMs = paramAnonymousParcel.readInt();
        localScanSettings.stepCount = paramAnonymousParcel.readInt();
        int i;
        label106:
        WifiScanner.ChannelSpec localChannelSpec;
        if (paramAnonymousParcel.readInt() == 1)
        {
          bool = true;
          localScanSettings.isPnoScan = bool;
          int j = paramAnonymousParcel.readInt();
          localScanSettings.channels = new WifiScanner.ChannelSpec[j];
          i = 0;
          if (i >= j) {
            break label179;
          }
          localChannelSpec = new WifiScanner.ChannelSpec(paramAnonymousParcel.readInt());
          localChannelSpec.dwellTimeMS = paramAnonymousParcel.readInt();
          if (paramAnonymousParcel.readInt() != 1) {
            break label173;
          }
        }
        label173:
        for (boolean bool = true;; bool = false)
        {
          localChannelSpec.passive = bool;
          localScanSettings.channels[i] = localChannelSpec;
          i += 1;
          break label106;
          bool = false;
          break;
        }
        label179:
        localScanSettings.hiddenNetworkIds = paramAnonymousParcel.createIntArray();
        return localScanSettings;
      }
      
      public WifiScanner.ScanSettings[] newArray(int paramAnonymousInt)
      {
        return new WifiScanner.ScanSettings[paramAnonymousInt];
      }
    };
    public int band;
    public WifiScanner.ChannelSpec[] channels;
    public int[] hiddenNetworkIds;
    public boolean isPnoScan;
    public int maxPeriodInMs;
    public int maxScansToCache;
    public int numBssidsPerScan;
    public int periodInMs;
    public int reportEvents;
    public int stepCount;
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.band);
      paramParcel.writeInt(this.periodInMs);
      paramParcel.writeInt(this.reportEvents);
      paramParcel.writeInt(this.numBssidsPerScan);
      paramParcel.writeInt(this.maxScansToCache);
      paramParcel.writeInt(this.maxPeriodInMs);
      paramParcel.writeInt(this.stepCount);
      if (this.isPnoScan)
      {
        paramInt = 1;
        paramParcel.writeInt(paramInt);
        if (this.channels == null) {
          break label159;
        }
        paramParcel.writeInt(this.channels.length);
        paramInt = 0;
        label88:
        if (paramInt >= this.channels.length) {
          break label164;
        }
        paramParcel.writeInt(this.channels[paramInt].frequency);
        paramParcel.writeInt(this.channels[paramInt].dwellTimeMS);
        if (!this.channels[paramInt].passive) {
          break label154;
        }
      }
      label154:
      for (int i = 1;; i = 0)
      {
        paramParcel.writeInt(i);
        paramInt += 1;
        break label88;
        paramInt = 0;
        break;
      }
      label159:
      paramParcel.writeInt(0);
      label164:
      paramParcel.writeIntArray(this.hiddenNetworkIds);
    }
  }
  
  private class ServiceHandler
    extends Handler
  {
    ServiceHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      Object localObject;
      switch (paramMessage.what)
      {
      case 69635: 
      default: 
        localObject = WifiScanner.-wrap0(WifiScanner.this, paramMessage.arg2);
        if (localObject == null) {
          return;
        }
        break;
      case 69634: 
        return;
      case 69636: 
        Log.e("WifiScanner", "Channel connection lost");
        WifiScanner.-set0(WifiScanner.this, null);
        getLooper().quit();
        return;
      }
      switch (paramMessage.what)
      {
      case 159750: 
      case 159751: 
      case 159752: 
      case 159755: 
      case 159756: 
      case 159757: 
      case 159758: 
      case 159765: 
      case 159766: 
      case 159768: 
      case 159769: 
      default: 
        return;
      case 159761: 
        ((WifiScanner.ActionListener)localObject).onSuccess();
        return;
      case 159762: 
        WifiScanner.OperationResult localOperationResult = (WifiScanner.OperationResult)paramMessage.obj;
        ((WifiScanner.ActionListener)localObject).onFailure(localOperationResult.reason, localOperationResult.description);
        WifiScanner.-wrap1(WifiScanner.this, paramMessage.arg2);
        return;
      case 159749: 
        ((WifiScanner.ScanListener)localObject).onResults(((WifiScanner.ParcelableScanData)paramMessage.obj).getResults());
        return;
      case 159764: 
        paramMessage = (ScanResult)paramMessage.obj;
        ((WifiScanner.ScanListener)localObject).onFullResult(paramMessage);
        return;
      case 159763: 
        ((WifiScanner.ScanListener)localObject).onPeriodChanged(paramMessage.arg1);
        return;
      case 159753: 
        ((WifiScanner.BssidListener)localObject).onFound(((WifiScanner.ParcelableScanResults)paramMessage.obj).getResults());
        return;
      case 159754: 
        ((WifiScanner.BssidListener)localObject).onLost(((WifiScanner.ParcelableScanResults)paramMessage.obj).getResults());
        return;
      case 159759: 
        ((WifiScanner.WifiChangeListener)localObject).onChanging(((WifiScanner.ParcelableScanResults)paramMessage.obj).getResults());
        return;
      case 159760: 
        ((WifiScanner.WifiChangeListener)localObject).onQuiescence(((WifiScanner.ParcelableScanResults)paramMessage.obj).getResults());
        return;
      case 159767: 
        WifiScanner.-wrap1(WifiScanner.this, paramMessage.arg2);
        return;
      }
      ((WifiScanner.PnoScanListener)localObject).onPnoNetworkFound(((WifiScanner.ParcelableScanResults)paramMessage.obj).getResults());
    }
  }
  
  public static abstract interface WifiChangeListener
    extends WifiScanner.ActionListener
  {
    public abstract void onChanging(ScanResult[] paramArrayOfScanResult);
    
    public abstract void onQuiescence(ScanResult[] paramArrayOfScanResult);
  }
  
  public static class WifiChangeSettings
    implements Parcelable
  {
    public static final Parcelable.Creator<WifiChangeSettings> CREATOR = new Parcelable.Creator()
    {
      public WifiScanner.WifiChangeSettings createFromParcel(Parcel paramAnonymousParcel)
      {
        WifiScanner.WifiChangeSettings localWifiChangeSettings = new WifiScanner.WifiChangeSettings();
        localWifiChangeSettings.rssiSampleSize = paramAnonymousParcel.readInt();
        localWifiChangeSettings.lostApSampleSize = paramAnonymousParcel.readInt();
        localWifiChangeSettings.unchangedSampleSize = paramAnonymousParcel.readInt();
        localWifiChangeSettings.minApsBreachingThreshold = paramAnonymousParcel.readInt();
        localWifiChangeSettings.periodInMs = paramAnonymousParcel.readInt();
        int j = paramAnonymousParcel.readInt();
        localWifiChangeSettings.bssidInfos = new WifiScanner.BssidInfo[j];
        int i = 0;
        while (i < j)
        {
          WifiScanner.BssidInfo localBssidInfo = new WifiScanner.BssidInfo();
          localBssidInfo.bssid = paramAnonymousParcel.readString();
          localBssidInfo.low = paramAnonymousParcel.readInt();
          localBssidInfo.high = paramAnonymousParcel.readInt();
          localBssidInfo.frequencyHint = paramAnonymousParcel.readInt();
          localWifiChangeSettings.bssidInfos[i] = localBssidInfo;
          i += 1;
        }
        return localWifiChangeSettings;
      }
      
      public WifiScanner.WifiChangeSettings[] newArray(int paramAnonymousInt)
      {
        return new WifiScanner.WifiChangeSettings[paramAnonymousInt];
      }
    };
    public WifiScanner.BssidInfo[] bssidInfos;
    public int lostApSampleSize;
    public int minApsBreachingThreshold;
    public int periodInMs;
    public int rssiSampleSize;
    public int unchangedSampleSize;
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.rssiSampleSize);
      paramParcel.writeInt(this.lostApSampleSize);
      paramParcel.writeInt(this.unchangedSampleSize);
      paramParcel.writeInt(this.minApsBreachingThreshold);
      paramParcel.writeInt(this.periodInMs);
      if (this.bssidInfos != null)
      {
        paramParcel.writeInt(this.bssidInfos.length);
        paramInt = 0;
        while (paramInt < this.bssidInfos.length)
        {
          WifiScanner.BssidInfo localBssidInfo = this.bssidInfos[paramInt];
          paramParcel.writeString(localBssidInfo.bssid);
          paramParcel.writeInt(localBssidInfo.low);
          paramParcel.writeInt(localBssidInfo.high);
          paramParcel.writeInt(localBssidInfo.frequencyHint);
          paramInt += 1;
        }
      }
      paramParcel.writeInt(0);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/WifiScanner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */