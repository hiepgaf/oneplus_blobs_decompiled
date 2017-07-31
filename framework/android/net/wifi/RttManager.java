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
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.AsyncChannel;

public class RttManager
{
  public static final int BASE = 160256;
  public static final int CMD_OP_ABORTED = 160260;
  public static final int CMD_OP_DISABLE_RESPONDER = 160262;
  public static final int CMD_OP_ENABLE_RESPONDER = 160261;
  public static final int CMD_OP_ENALBE_RESPONDER_FAILED = 160264;
  public static final int CMD_OP_ENALBE_RESPONDER_SUCCEEDED = 160263;
  public static final int CMD_OP_FAILED = 160258;
  public static final int CMD_OP_START_RANGING = 160256;
  public static final int CMD_OP_STOP_RANGING = 160257;
  public static final int CMD_OP_SUCCEEDED = 160259;
  private static final boolean DBG = false;
  public static final String DESCRIPTION_KEY = "android.net.wifi.RttManager.Description";
  private static final int INVALID_KEY = 0;
  public static final int PREAMBLE_HT = 2;
  public static final int PREAMBLE_LEGACY = 1;
  public static final int PREAMBLE_VHT = 4;
  public static final int REASON_INITIATOR_NOT_ALLOWED_WHEN_RESPONDER_ON = -6;
  public static final int REASON_INVALID_LISTENER = -3;
  public static final int REASON_INVALID_REQUEST = -4;
  public static final int REASON_NOT_AVAILABLE = -2;
  public static final int REASON_PERMISSION_DENIED = -5;
  public static final int REASON_UNSPECIFIED = -1;
  public static final int RTT_BW_10_SUPPORT = 2;
  public static final int RTT_BW_160_SUPPORT = 32;
  public static final int RTT_BW_20_SUPPORT = 4;
  public static final int RTT_BW_40_SUPPORT = 8;
  public static final int RTT_BW_5_SUPPORT = 1;
  public static final int RTT_BW_80_SUPPORT = 16;
  @Deprecated
  public static final int RTT_CHANNEL_WIDTH_10 = 6;
  @Deprecated
  public static final int RTT_CHANNEL_WIDTH_160 = 3;
  @Deprecated
  public static final int RTT_CHANNEL_WIDTH_20 = 0;
  @Deprecated
  public static final int RTT_CHANNEL_WIDTH_40 = 1;
  @Deprecated
  public static final int RTT_CHANNEL_WIDTH_5 = 5;
  @Deprecated
  public static final int RTT_CHANNEL_WIDTH_80 = 2;
  @Deprecated
  public static final int RTT_CHANNEL_WIDTH_80P80 = 4;
  @Deprecated
  public static final int RTT_CHANNEL_WIDTH_UNSPECIFIED = -1;
  public static final int RTT_PEER_NAN = 5;
  public static final int RTT_PEER_P2P_CLIENT = 4;
  public static final int RTT_PEER_P2P_GO = 3;
  public static final int RTT_PEER_TYPE_AP = 1;
  public static final int RTT_PEER_TYPE_STA = 2;
  @Deprecated
  public static final int RTT_PEER_TYPE_UNSPECIFIED = 0;
  public static final int RTT_STATUS_ABORTED = 8;
  public static final int RTT_STATUS_FAILURE = 1;
  public static final int RTT_STATUS_FAIL_AP_ON_DIFF_CHANNEL = 6;
  public static final int RTT_STATUS_FAIL_BUSY_TRY_LATER = 12;
  public static final int RTT_STATUS_FAIL_FTM_PARAM_OVERRIDE = 15;
  public static final int RTT_STATUS_FAIL_INVALID_TS = 9;
  public static final int RTT_STATUS_FAIL_NOT_SCHEDULED_YET = 4;
  public static final int RTT_STATUS_FAIL_NO_CAPABILITY = 7;
  public static final int RTT_STATUS_FAIL_NO_RSP = 2;
  public static final int RTT_STATUS_FAIL_PROTOCOL = 10;
  public static final int RTT_STATUS_FAIL_REJECTED = 3;
  public static final int RTT_STATUS_FAIL_SCHEDULE = 11;
  public static final int RTT_STATUS_FAIL_TM_TIMEOUT = 5;
  public static final int RTT_STATUS_INVALID_REQ = 13;
  public static final int RTT_STATUS_NO_WIFI = 14;
  public static final int RTT_STATUS_SUCCESS = 0;
  @Deprecated
  public static final int RTT_TYPE_11_MC = 4;
  @Deprecated
  public static final int RTT_TYPE_11_V = 2;
  public static final int RTT_TYPE_ONE_SIDED = 1;
  public static final int RTT_TYPE_TWO_SIDED = 2;
  @Deprecated
  public static final int RTT_TYPE_UNSPECIFIED = 0;
  private static final String TAG = "RttManager";
  private AsyncChannel mAsyncChannel;
  private final Object mCapabilitiesLock = new Object();
  private final Context mContext;
  private int mListenerKey = 1;
  private final SparseArray mListenerMap = new SparseArray();
  private final Object mListenerMapLock = new Object();
  private RttCapabilities mRttCapabilities;
  private final IRttManager mService;
  
  public RttManager(Context paramContext, IRttManager paramIRttManager, Looper paramLooper)
  {
    this.mContext = paramContext;
    this.mService = paramIRttManager;
    try
    {
      Log.d("RttManager", "Get the messenger from " + this.mService);
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
    paramIRttManager = new ServiceHandler(paramLooper);
    this.mAsyncChannel.connectSync(this.mContext, paramIRttManager, paramContext);
    this.mAsyncChannel.sendMessage(69633);
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
  
  private int putListenerIfAbsent(Object paramObject)
  {
    if (paramObject == null) {
      return 0;
    }
    synchronized (this.mListenerMapLock)
    {
      int i = getListenerKey(paramObject);
      if (i != 0) {
        return i;
      }
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
    if (i == 0) {
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
  
  private boolean rttParamSanity(RttParams paramRttParams, int paramInt)
  {
    if ((this.mRttCapabilities == null) && (getRttCapabilities() == null))
    {
      Log.e("RttManager", "Can not get RTT capabilities");
      throw new IllegalStateException("RTT chip is not working");
    }
    if (paramRttParams.deviceType != 1) {
      return false;
    }
    if ((paramRttParams.requestType != 1) && (paramRttParams.requestType != 2))
    {
      Log.e("RttManager", "Request " + paramInt + ": Illegal Request Type: " + paramRttParams.requestType);
      return false;
    }
    if ((paramRttParams.requestType != 1) || (this.mRttCapabilities.oneSidedRttSupported))
    {
      if ((paramRttParams.requestType != 2) || (this.mRttCapabilities.twoSided11McRttSupported))
      {
        if ((paramRttParams.bssid != null) && (!paramRttParams.bssid.isEmpty())) {
          break label233;
        }
        Log.e("RttManager", "No BSSID in params");
        return false;
      }
    }
    else
    {
      Log.e("RttManager", "Request " + paramInt + ": One side RTT is not supported");
      return false;
    }
    Log.e("RttManager", "Request " + paramInt + ": two side RTT is not supported");
    return false;
    label233:
    if (paramRttParams.numberBurst != 0)
    {
      Log.e("RttManager", "Request " + paramInt + ": Illegal number of burst: " + paramRttParams.numberBurst);
      return false;
    }
    if ((paramRttParams.numSamplesPerBurst <= 0) || (paramRttParams.numSamplesPerBurst > 31))
    {
      Log.e("RttManager", "Request " + paramInt + ": Illegal sample number per burst: " + paramRttParams.numSamplesPerBurst);
      return false;
    }
    if ((paramRttParams.numRetriesPerMeasurementFrame < 0) || (paramRttParams.numRetriesPerMeasurementFrame > 3))
    {
      Log.e("RttManager", "Request " + paramInt + ": Illegal measurement frame retry number:" + paramRttParams.numRetriesPerMeasurementFrame);
      return false;
    }
    if ((paramRttParams.numRetriesPerFTMR < 0) || (paramRttParams.numRetriesPerFTMR > 3))
    {
      Log.e("RttManager", "Request " + paramInt + ": Illegal FTMR frame retry number:" + paramRttParams.numRetriesPerFTMR);
      return false;
    }
    if ((!paramRttParams.LCIRequest) || (this.mRttCapabilities.lciSupported))
    {
      if ((!paramRttParams.LCRRequest) || (this.mRttCapabilities.lcrSupported))
      {
        if ((paramRttParams.burstTimeout >= 1) && ((paramRttParams.burstTimeout <= 11) || (paramRttParams.burstTimeout == 15))) {
          break label619;
        }
        Log.e("RttManager", "Request " + paramInt + ": Illegal burst timeout: " + paramRttParams.burstTimeout);
        return false;
      }
    }
    else
    {
      Log.e("RttManager", "Request " + paramInt + ": LCI is not supported");
      return false;
    }
    Log.e("RttManager", "Request " + paramInt + ": LCR is not supported");
    return false;
    label619:
    if ((paramRttParams.preamble & this.mRttCapabilities.preambleSupported) == 0)
    {
      Log.e("RttManager", "Request " + paramInt + ": Do not support this preamble: " + paramRttParams.preamble);
      return false;
    }
    if ((paramRttParams.bandwidth & this.mRttCapabilities.bwSupported) == 0)
    {
      Log.e("RttManager", "Request " + paramInt + ": Do not support this bandwidth: " + paramRttParams.bandwidth);
      return false;
    }
    return true;
  }
  
  private void validateChannel()
  {
    if (this.mAsyncChannel == null) {
      throw new IllegalStateException("No permission to access and change wifi or a bad initialization");
    }
  }
  
  public void disableResponder(ResponderCallback paramResponderCallback)
  {
    if (paramResponderCallback == null) {
      throw new IllegalArgumentException("callback cannot be null");
    }
    validateChannel();
    int i = removeListener(paramResponderCallback);
    if (i == 0)
    {
      Log.e("RttManager", "responder not enabled yet");
      return;
    }
    this.mAsyncChannel.sendMessage(160262, 0, i);
  }
  
  public void enableResponder(ResponderCallback paramResponderCallback)
  {
    if (paramResponderCallback == null) {
      throw new IllegalArgumentException("callback cannot be null");
    }
    validateChannel();
    int i = putListenerIfAbsent(paramResponderCallback);
    this.mAsyncChannel.sendMessage(160261, 0, i);
  }
  
  @Deprecated
  public Capabilities getCapabilities()
  {
    return new Capabilities();
  }
  
  public RttCapabilities getRttCapabilities()
  {
    synchronized (this.mCapabilitiesLock)
    {
      RttCapabilities localRttCapabilities = this.mRttCapabilities;
      if (localRttCapabilities == null) {}
      try
      {
        this.mRttCapabilities = this.mService.getRttCapabilities();
        localRttCapabilities = this.mRttCapabilities;
        return localRttCapabilities;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
  }
  
  public void startRanging(RttParams[] paramArrayOfRttParams, RttListener paramRttListener)
  {
    int j = 0;
    int k = paramArrayOfRttParams.length;
    int i = 0;
    while (i < k)
    {
      if (!rttParamSanity(paramArrayOfRttParams[i], j)) {
        throw new IllegalArgumentException("RTT Request Parameter Illegal");
      }
      j += 1;
      i += 1;
    }
    validateChannel();
    paramArrayOfRttParams = new ParcelableRttParams(paramArrayOfRttParams);
    Log.i("RttManager", "Send RTT request to RTT Service");
    this.mAsyncChannel.sendMessage(160256, 0, putListener(paramRttListener), paramArrayOfRttParams);
  }
  
  public void stopRanging(RttListener paramRttListener)
  {
    validateChannel();
    this.mAsyncChannel.sendMessage(160257, 0, removeListener(paramRttListener));
  }
  
  @Deprecated
  public class Capabilities
  {
    public int supportedPeerType;
    public int supportedType;
    
    public Capabilities() {}
  }
  
  public static class ParcelableRttParams
    implements Parcelable
  {
    public static final Parcelable.Creator<ParcelableRttParams> CREATOR = new Parcelable.Creator()
    {
      public RttManager.ParcelableRttParams createFromParcel(Parcel paramAnonymousParcel)
      {
        int j = paramAnonymousParcel.readInt();
        RttManager.RttParams[] arrayOfRttParams = new RttManager.RttParams[j];
        int i = 0;
        if (i < j)
        {
          arrayOfRttParams[i] = new RttManager.RttParams();
          arrayOfRttParams[i].deviceType = paramAnonymousParcel.readInt();
          arrayOfRttParams[i].requestType = paramAnonymousParcel.readInt();
          RttManager.RttParams localRttParams = arrayOfRttParams[i];
          if (paramAnonymousParcel.readByte() != 0)
          {
            bool = true;
            label67:
            localRttParams.secure = bool;
            arrayOfRttParams[i].bssid = paramAnonymousParcel.readString();
            arrayOfRttParams[i].channelWidth = paramAnonymousParcel.readInt();
            arrayOfRttParams[i].frequency = paramAnonymousParcel.readInt();
            arrayOfRttParams[i].centerFreq0 = paramAnonymousParcel.readInt();
            arrayOfRttParams[i].centerFreq1 = paramAnonymousParcel.readInt();
            arrayOfRttParams[i].numberBurst = paramAnonymousParcel.readInt();
            arrayOfRttParams[i].interval = paramAnonymousParcel.readInt();
            arrayOfRttParams[i].numSamplesPerBurst = paramAnonymousParcel.readInt();
            arrayOfRttParams[i].numRetriesPerMeasurementFrame = paramAnonymousParcel.readInt();
            arrayOfRttParams[i].numRetriesPerFTMR = paramAnonymousParcel.readInt();
            localRttParams = arrayOfRttParams[i];
            if (paramAnonymousParcel.readInt() != 1) {
              break label278;
            }
            bool = true;
            label201:
            localRttParams.LCIRequest = bool;
            localRttParams = arrayOfRttParams[i];
            if (paramAnonymousParcel.readInt() != 1) {
              break label284;
            }
          }
          label278:
          label284:
          for (boolean bool = true;; bool = false)
          {
            localRttParams.LCRRequest = bool;
            arrayOfRttParams[i].burstTimeout = paramAnonymousParcel.readInt();
            arrayOfRttParams[i].preamble = paramAnonymousParcel.readInt();
            arrayOfRttParams[i].bandwidth = paramAnonymousParcel.readInt();
            i += 1;
            break;
            bool = false;
            break label67;
            bool = false;
            break label201;
          }
        }
        return new RttManager.ParcelableRttParams(arrayOfRttParams);
      }
      
      public RttManager.ParcelableRttParams[] newArray(int paramAnonymousInt)
      {
        return new RttManager.ParcelableRttParams[paramAnonymousInt];
      }
    };
    public RttManager.RttParams[] mParams;
    
    public ParcelableRttParams(RttManager.RttParams[] paramArrayOfRttParams)
    {
      RttManager.RttParams[] arrayOfRttParams = paramArrayOfRttParams;
      if (paramArrayOfRttParams == null) {
        arrayOfRttParams = new RttManager.RttParams[0];
      }
      this.mParams = arrayOfRttParams;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mParams.length);
      RttManager.RttParams[] arrayOfRttParams = this.mParams;
      int j = arrayOfRttParams.length;
      paramInt = 0;
      if (paramInt < j)
      {
        RttManager.RttParams localRttParams = arrayOfRttParams[paramInt];
        paramParcel.writeInt(localRttParams.deviceType);
        paramParcel.writeInt(localRttParams.requestType);
        byte b;
        if (localRttParams.secure)
        {
          b = 1;
          label62:
          paramParcel.writeByte(b);
          paramParcel.writeString(localRttParams.bssid);
          paramParcel.writeInt(localRttParams.channelWidth);
          paramParcel.writeInt(localRttParams.frequency);
          paramParcel.writeInt(localRttParams.centerFreq0);
          paramParcel.writeInt(localRttParams.centerFreq1);
          paramParcel.writeInt(localRttParams.numberBurst);
          paramParcel.writeInt(localRttParams.interval);
          paramParcel.writeInt(localRttParams.numSamplesPerBurst);
          paramParcel.writeInt(localRttParams.numRetriesPerMeasurementFrame);
          paramParcel.writeInt(localRttParams.numRetriesPerFTMR);
          if (!localRttParams.LCIRequest) {
            break label230;
          }
          i = 1;
          label168:
          paramParcel.writeInt(i);
          if (!localRttParams.LCRRequest) {
            break label236;
          }
        }
        label230:
        label236:
        for (int i = 1;; i = 0)
        {
          paramParcel.writeInt(i);
          paramParcel.writeInt(localRttParams.burstTimeout);
          paramParcel.writeInt(localRttParams.preamble);
          paramParcel.writeInt(localRttParams.bandwidth);
          paramInt += 1;
          break;
          b = 0;
          break label62;
          i = 0;
          break label168;
        }
      }
    }
  }
  
  public static class ParcelableRttResults
    implements Parcelable
  {
    public static final Parcelable.Creator<ParcelableRttResults> CREATOR = new Parcelable.Creator()
    {
      public RttManager.ParcelableRttResults createFromParcel(Parcel paramAnonymousParcel)
      {
        int j = paramAnonymousParcel.readInt();
        if (j == 0) {
          return new RttManager.ParcelableRttResults(null);
        }
        RttManager.RttResult[] arrayOfRttResult = new RttManager.RttResult[j];
        int i = 0;
        if (i < j)
        {
          arrayOfRttResult[i] = new RttManager.RttResult();
          arrayOfRttResult[i].bssid = paramAnonymousParcel.readString();
          arrayOfRttResult[i].burstNumber = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].measurementFrameNumber = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].successMeasurementFrameNumber = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].frameNumberPerBurstPeer = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].status = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].measurementType = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].retryAfterDuration = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].ts = paramAnonymousParcel.readLong();
          arrayOfRttResult[i].rssi = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].rssiSpread = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].txRate = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].rtt = paramAnonymousParcel.readLong();
          arrayOfRttResult[i].rttStandardDeviation = paramAnonymousParcel.readLong();
          arrayOfRttResult[i].rttSpread = paramAnonymousParcel.readLong();
          arrayOfRttResult[i].distance = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].distanceStandardDeviation = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].distanceSpread = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].burstDuration = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].negotiatedBurstNum = paramAnonymousParcel.readInt();
          arrayOfRttResult[i].LCI = new RttManager.WifiInformationElement();
          arrayOfRttResult[i].LCI.id = paramAnonymousParcel.readByte();
          int k;
          if (arrayOfRttResult[i].LCI.id != -1)
          {
            k = paramAnonymousParcel.readByte();
            arrayOfRttResult[i].LCI.data = new byte[k];
            paramAnonymousParcel.readByteArray(arrayOfRttResult[i].LCI.data);
          }
          arrayOfRttResult[i].LCR = new RttManager.WifiInformationElement();
          arrayOfRttResult[i].LCR.id = paramAnonymousParcel.readByte();
          if (arrayOfRttResult[i].LCR.id != -1)
          {
            k = paramAnonymousParcel.readByte();
            arrayOfRttResult[i].LCR.data = new byte[k];
            paramAnonymousParcel.readByteArray(arrayOfRttResult[i].LCR.data);
          }
          RttManager.RttResult localRttResult = arrayOfRttResult[i];
          if (paramAnonymousParcel.readByte() != 0) {}
          for (boolean bool = true;; bool = false)
          {
            localRttResult.secure = bool;
            i += 1;
            break;
          }
        }
        return new RttManager.ParcelableRttResults(arrayOfRttResult);
      }
      
      public RttManager.ParcelableRttResults[] newArray(int paramAnonymousInt)
      {
        return new RttManager.ParcelableRttResults[paramAnonymousInt];
      }
    };
    public RttManager.RttResult[] mResults;
    
    public ParcelableRttResults(RttManager.RttResult[] paramArrayOfRttResult)
    {
      this.mResults = paramArrayOfRttResult;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      if (this.mResults != null)
      {
        paramParcel.writeInt(this.mResults.length);
        RttManager.RttResult[] arrayOfRttResult = this.mResults;
        int i = arrayOfRttResult.length;
        paramInt = 0;
        if (paramInt < i)
        {
          RttManager.RttResult localRttResult = arrayOfRttResult[paramInt];
          paramParcel.writeString(localRttResult.bssid);
          paramParcel.writeInt(localRttResult.burstNumber);
          paramParcel.writeInt(localRttResult.measurementFrameNumber);
          paramParcel.writeInt(localRttResult.successMeasurementFrameNumber);
          paramParcel.writeInt(localRttResult.frameNumberPerBurstPeer);
          paramParcel.writeInt(localRttResult.status);
          paramParcel.writeInt(localRttResult.measurementType);
          paramParcel.writeInt(localRttResult.retryAfterDuration);
          paramParcel.writeLong(localRttResult.ts);
          paramParcel.writeInt(localRttResult.rssi);
          paramParcel.writeInt(localRttResult.rssiSpread);
          paramParcel.writeInt(localRttResult.txRate);
          paramParcel.writeLong(localRttResult.rtt);
          paramParcel.writeLong(localRttResult.rttStandardDeviation);
          paramParcel.writeLong(localRttResult.rttSpread);
          paramParcel.writeInt(localRttResult.distance);
          paramParcel.writeInt(localRttResult.distanceStandardDeviation);
          paramParcel.writeInt(localRttResult.distanceSpread);
          paramParcel.writeInt(localRttResult.burstDuration);
          paramParcel.writeInt(localRttResult.negotiatedBurstNum);
          paramParcel.writeByte(localRttResult.LCI.id);
          if (localRttResult.LCI.id != -1)
          {
            paramParcel.writeByte((byte)localRttResult.LCI.data.length);
            paramParcel.writeByteArray(localRttResult.LCI.data);
          }
          paramParcel.writeByte(localRttResult.LCR.id);
          if (localRttResult.LCR.id != -1)
          {
            paramParcel.writeInt((byte)localRttResult.LCR.data.length);
            paramParcel.writeByte(localRttResult.LCR.id);
          }
          if (localRttResult.secure) {}
          for (byte b = 1;; b = 0)
          {
            paramParcel.writeByte(b);
            paramInt += 1;
            break;
          }
        }
      }
      else
      {
        paramParcel.writeInt(0);
      }
    }
  }
  
  public static abstract class ResponderCallback
  {
    public abstract void onResponderEnableFailure(int paramInt);
    
    public abstract void onResponderEnabled(RttManager.ResponderConfig paramResponderConfig);
  }
  
  public static class ResponderConfig
    implements Parcelable
  {
    public static final Parcelable.Creator<ResponderConfig> CREATOR = new Parcelable.Creator()
    {
      public RttManager.ResponderConfig createFromParcel(Parcel paramAnonymousParcel)
      {
        RttManager.ResponderConfig localResponderConfig = new RttManager.ResponderConfig();
        localResponderConfig.macAddress = paramAnonymousParcel.readString();
        localResponderConfig.frequency = paramAnonymousParcel.readInt();
        localResponderConfig.centerFreq0 = paramAnonymousParcel.readInt();
        localResponderConfig.centerFreq1 = paramAnonymousParcel.readInt();
        localResponderConfig.channelWidth = paramAnonymousParcel.readInt();
        localResponderConfig.preamble = paramAnonymousParcel.readInt();
        return localResponderConfig;
      }
      
      public RttManager.ResponderConfig[] newArray(int paramAnonymousInt)
      {
        return new RttManager.ResponderConfig[paramAnonymousInt];
      }
    };
    public int centerFreq0;
    public int centerFreq1;
    public int channelWidth;
    public int frequency;
    public String macAddress = "";
    public int preamble;
    
    public int describeContents()
    {
      return 0;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("macAddress = ").append(this.macAddress).append(" frequency = ").append(this.frequency).append(" centerFreq0 = ").append(this.centerFreq0).append(" centerFreq1 = ").append(this.centerFreq1).append(" channelWidth = ").append(this.channelWidth).append(" preamble = ").append(this.preamble);
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeString(this.macAddress);
      paramParcel.writeInt(this.frequency);
      paramParcel.writeInt(this.centerFreq0);
      paramParcel.writeInt(this.centerFreq1);
      paramParcel.writeInt(this.channelWidth);
      paramParcel.writeInt(this.preamble);
    }
  }
  
  public static class RttCapabilities
    implements Parcelable
  {
    public static final Parcelable.Creator<RttCapabilities> CREATOR = new Parcelable.Creator()
    {
      public RttManager.RttCapabilities createFromParcel(Parcel paramAnonymousParcel)
      {
        boolean bool2 = true;
        RttManager.RttCapabilities localRttCapabilities = new RttManager.RttCapabilities();
        if (paramAnonymousParcel.readInt() == 1)
        {
          bool1 = true;
          localRttCapabilities.oneSidedRttSupported = bool1;
          if (paramAnonymousParcel.readInt() != 1) {
            break label142;
          }
          bool1 = true;
          label37:
          localRttCapabilities.twoSided11McRttSupported = bool1;
          if (paramAnonymousParcel.readInt() != 1) {
            break label147;
          }
          bool1 = true;
          label53:
          localRttCapabilities.lciSupported = bool1;
          if (paramAnonymousParcel.readInt() != 1) {
            break label152;
          }
          bool1 = true;
          label69:
          localRttCapabilities.lcrSupported = bool1;
          localRttCapabilities.preambleSupported = paramAnonymousParcel.readInt();
          localRttCapabilities.bwSupported = paramAnonymousParcel.readInt();
          if (paramAnonymousParcel.readInt() != 1) {
            break label157;
          }
          bool1 = true;
          label103:
          localRttCapabilities.responderSupported = bool1;
          if (paramAnonymousParcel.readInt() != 1) {
            break label162;
          }
        }
        label142:
        label147:
        label152:
        label157:
        label162:
        for (boolean bool1 = bool2;; bool1 = false)
        {
          localRttCapabilities.secureRttSupported = bool1;
          localRttCapabilities.mcVersion = paramAnonymousParcel.readInt();
          return localRttCapabilities;
          bool1 = false;
          break;
          bool1 = false;
          break label37;
          bool1 = false;
          break label53;
          bool1 = false;
          break label69;
          bool1 = false;
          break label103;
        }
      }
      
      public RttManager.RttCapabilities[] newArray(int paramAnonymousInt)
      {
        return new RttManager.RttCapabilities[paramAnonymousInt];
      }
    };
    public int bwSupported;
    public boolean lciSupported;
    public boolean lcrSupported;
    public int mcVersion;
    public boolean oneSidedRttSupported;
    public int preambleSupported;
    public boolean responderSupported;
    public boolean secureRttSupported;
    @Deprecated
    public boolean supportedPeerType;
    @Deprecated
    public boolean supportedType;
    public boolean twoSided11McRttSupported;
    
    public int describeContents()
    {
      return 0;
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer1 = new StringBuffer();
      StringBuffer localStringBuffer2 = localStringBuffer1.append("oneSidedRtt ");
      if (this.oneSidedRttSupported)
      {
        str = "is Supported. ";
        localStringBuffer2 = localStringBuffer2.append(str).append("twoSided11McRtt ");
        if (!this.twoSided11McRttSupported) {
          break label339;
        }
        str = "is Supported. ";
        label46:
        localStringBuffer2 = localStringBuffer2.append(str).append("lci ");
        if (!this.lciSupported) {
          break label345;
        }
        str = "is Supported. ";
        label67:
        localStringBuffer2 = localStringBuffer2.append(str).append("lcr ");
        if (!this.lcrSupported) {
          break label351;
        }
        str = "is Supported. ";
        label88:
        localStringBuffer2.append(str);
        if ((this.preambleSupported & 0x1) != 0) {
          localStringBuffer1.append("Legacy ");
        }
        if ((this.preambleSupported & 0x2) != 0) {
          localStringBuffer1.append("HT ");
        }
        if ((this.preambleSupported & 0x4) != 0) {
          localStringBuffer1.append("VHT ");
        }
        localStringBuffer1.append("is supported. ");
        if ((this.bwSupported & 0x1) != 0) {
          localStringBuffer1.append("5 MHz ");
        }
        if ((this.bwSupported & 0x2) != 0) {
          localStringBuffer1.append("10 MHz ");
        }
        if ((this.bwSupported & 0x4) != 0) {
          localStringBuffer1.append("20 MHz ");
        }
        if ((this.bwSupported & 0x8) != 0) {
          localStringBuffer1.append("40 MHz ");
        }
        if ((this.bwSupported & 0x10) != 0) {
          localStringBuffer1.append("80 MHz ");
        }
        if ((this.bwSupported & 0x20) != 0) {
          localStringBuffer1.append("160 MHz ");
        }
        localStringBuffer1.append("is supported.");
        localStringBuffer2 = localStringBuffer1.append(" STA responder role is ");
        if (!this.responderSupported) {
          break label357;
        }
        str = "supported";
        label272:
        localStringBuffer2.append(str);
        localStringBuffer2 = localStringBuffer1.append(" Secure RTT protocol is ");
        if (!this.secureRttSupported) {
          break label363;
        }
      }
      label339:
      label345:
      label351:
      label357:
      label363:
      for (String str = "supported";; str = "not supported")
      {
        localStringBuffer2.append(str);
        localStringBuffer1.append(" 11mc version is " + this.mcVersion);
        return localStringBuffer1.toString();
        str = "is not supported. ";
        break;
        str = "is not supported. ";
        break label46;
        str = "is not supported. ";
        break label67;
        str = "is not supported. ";
        break label88;
        str = "not supported";
        break label272;
      }
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 1;
      if (this.oneSidedRttSupported)
      {
        paramInt = 1;
        paramParcel.writeInt(paramInt);
        if (!this.twoSided11McRttSupported) {
          break label116;
        }
        paramInt = 1;
        label25:
        paramParcel.writeInt(paramInt);
        if (!this.lciSupported) {
          break label121;
        }
        paramInt = 1;
        label39:
        paramParcel.writeInt(paramInt);
        if (!this.lcrSupported) {
          break label126;
        }
        paramInt = 1;
        label53:
        paramParcel.writeInt(paramInt);
        paramParcel.writeInt(this.preambleSupported);
        paramParcel.writeInt(this.bwSupported);
        if (!this.responderSupported) {
          break label131;
        }
        paramInt = 1;
        label83:
        paramParcel.writeInt(paramInt);
        if (!this.secureRttSupported) {
          break label136;
        }
      }
      label116:
      label121:
      label126:
      label131:
      label136:
      for (paramInt = i;; paramInt = 0)
      {
        paramParcel.writeInt(paramInt);
        paramParcel.writeInt(this.mcVersion);
        return;
        paramInt = 0;
        break;
        paramInt = 0;
        break label25;
        paramInt = 0;
        break label39;
        paramInt = 0;
        break label53;
        paramInt = 0;
        break label83;
      }
    }
  }
  
  public static abstract interface RttListener
  {
    public abstract void onAborted();
    
    public abstract void onFailure(int paramInt, String paramString);
    
    public abstract void onSuccess(RttManager.RttResult[] paramArrayOfRttResult);
  }
  
  public static class RttParams
  {
    public boolean LCIRequest;
    public boolean LCRRequest;
    public int bandwidth = 4;
    public String bssid;
    public int burstTimeout = 15;
    public int centerFreq0;
    public int centerFreq1;
    public int channelWidth;
    public int deviceType = 1;
    public int frequency;
    public int interval;
    public int numRetriesPerFTMR = 0;
    public int numRetriesPerMeasurementFrame = 0;
    public int numSamplesPerBurst = 8;
    @Deprecated
    public int num_retries;
    @Deprecated
    public int num_samples;
    public int numberBurst = 0;
    public int preamble = 2;
    public int requestType = 1;
    public boolean secure;
  }
  
  public static class RttResult
  {
    public RttManager.WifiInformationElement LCI;
    public RttManager.WifiInformationElement LCR;
    public String bssid;
    public int burstDuration;
    public int burstNumber;
    public int distance;
    public int distanceSpread;
    public int distanceStandardDeviation;
    @Deprecated
    public int distance_cm;
    @Deprecated
    public int distance_sd_cm;
    @Deprecated
    public int distance_spread_cm;
    public int frameNumberPerBurstPeer;
    public int measurementFrameNumber;
    public int measurementType;
    public int negotiatedBurstNum;
    @Deprecated
    public int requestType;
    public int retryAfterDuration;
    public int rssi;
    public int rssiSpread;
    @Deprecated
    public int rssi_spread;
    public long rtt;
    public long rttSpread;
    public long rttStandardDeviation;
    @Deprecated
    public long rtt_ns;
    @Deprecated
    public long rtt_sd_ns;
    @Deprecated
    public long rtt_spread_ns;
    public int rxRate;
    public boolean secure;
    public int status;
    public int successMeasurementFrameNumber;
    public long ts;
    public int txRate;
    @Deprecated
    public int tx_rate;
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
      Log.i("RttManager", "RTT manager get message: " + paramMessage.what);
      Object localObject;
      switch (paramMessage.what)
      {
      case 69635: 
      default: 
        localObject = RttManager.-wrap0(RttManager.this, paramMessage.arg2);
        if (localObject == null)
        {
          Log.e("RttManager", "invalid listener key = " + paramMessage.arg2);
          return;
        }
        break;
      case 69634: 
        return;
      case 69636: 
        Log.e("RttManager", "Channel connection lost");
        RttManager.-set0(RttManager.this, null);
        getLooper().quit();
        return;
      }
      Log.i("RttManager", "listener key = " + paramMessage.arg2);
      switch (paramMessage.what)
      {
      case 160261: 
      case 160262: 
      default: 
        return;
      case 160259: 
        reportSuccess(localObject, paramMessage);
        RttManager.-wrap1(RttManager.this, paramMessage.arg2);
        return;
      case 160258: 
        reportFailure(localObject, paramMessage);
        RttManager.-wrap1(RttManager.this, paramMessage.arg2);
        return;
      case 160260: 
        ((RttManager.RttListener)localObject).onAborted();
        RttManager.-wrap1(RttManager.this, paramMessage.arg2);
        return;
      case 160263: 
        paramMessage = (RttManager.ResponderConfig)paramMessage.obj;
        ((RttManager.ResponderCallback)localObject).onResponderEnabled(paramMessage);
        return;
      }
      ((RttManager.ResponderCallback)localObject).onResponderEnableFailure(paramMessage.arg1);
      RttManager.-wrap1(RttManager.this, paramMessage.arg2);
    }
    
    void reportFailure(Object paramObject, Message paramMessage)
    {
      Object localObject = (RttManager.RttListener)paramObject;
      localObject = (Bundle)paramMessage.obj;
      ((RttManager.RttListener)paramObject).onFailure(paramMessage.arg1, ((Bundle)localObject).getString("android.net.wifi.RttManager.Description"));
    }
    
    void reportSuccess(Object paramObject, Message paramMessage)
    {
      RttManager.RttListener localRttListener = (RttManager.RttListener)paramObject;
      paramMessage = (RttManager.ParcelableRttResults)paramMessage.obj;
      ((RttManager.RttListener)paramObject).onSuccess(paramMessage.mResults);
    }
  }
  
  public static class WifiInformationElement
  {
    public byte[] data;
    public byte id;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/RttManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */