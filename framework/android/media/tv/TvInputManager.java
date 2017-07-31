package android.media.tv;

import android.graphics.Rect;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pools.Pool;
import android.util.Pools.SimplePool;
import android.util.SparseArray;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventSender;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class TvInputManager
{
  public static final String ACTION_BLOCKED_RATINGS_CHANGED = "android.media.tv.action.BLOCKED_RATINGS_CHANGED";
  public static final String ACTION_PARENTAL_CONTROLS_ENABLED_CHANGED = "android.media.tv.action.PARENTAL_CONTROLS_ENABLED_CHANGED";
  public static final String ACTION_QUERY_CONTENT_RATING_SYSTEMS = "android.media.tv.action.QUERY_CONTENT_RATING_SYSTEMS";
  public static final String ACTION_SETUP_INPUTS = "android.media.tv.action.SETUP_INPUTS";
  public static final int DVB_DEVICE_DEMUX = 0;
  public static final int DVB_DEVICE_DVR = 1;
  static final int DVB_DEVICE_END = 2;
  public static final int DVB_DEVICE_FRONTEND = 2;
  static final int DVB_DEVICE_START = 0;
  public static final int INPUT_STATE_CONNECTED = 0;
  public static final int INPUT_STATE_CONNECTED_STANDBY = 1;
  public static final int INPUT_STATE_DISCONNECTED = 2;
  public static final String META_DATA_CONTENT_RATING_SYSTEMS = "android.media.tv.metadata.CONTENT_RATING_SYSTEMS";
  static final int RECORDING_ERROR_END = 2;
  public static final int RECORDING_ERROR_INSUFFICIENT_SPACE = 1;
  public static final int RECORDING_ERROR_RESOURCE_BUSY = 2;
  static final int RECORDING_ERROR_START = 0;
  public static final int RECORDING_ERROR_UNKNOWN = 0;
  private static final String TAG = "TvInputManager";
  public static final long TIME_SHIFT_INVALID_TIME = Long.MIN_VALUE;
  public static final int TIME_SHIFT_STATUS_AVAILABLE = 3;
  public static final int TIME_SHIFT_STATUS_UNAVAILABLE = 2;
  public static final int TIME_SHIFT_STATUS_UNKNOWN = 0;
  public static final int TIME_SHIFT_STATUS_UNSUPPORTED = 1;
  public static final int VIDEO_UNAVAILABLE_REASON_AUDIO_ONLY = 4;
  public static final int VIDEO_UNAVAILABLE_REASON_BUFFERING = 3;
  static final int VIDEO_UNAVAILABLE_REASON_END = 4;
  static final int VIDEO_UNAVAILABLE_REASON_START = 0;
  public static final int VIDEO_UNAVAILABLE_REASON_TUNING = 1;
  public static final int VIDEO_UNAVAILABLE_REASON_UNKNOWN = 0;
  public static final int VIDEO_UNAVAILABLE_REASON_WEAK_SIGNAL = 2;
  private final List<TvInputCallbackRecord> mCallbackRecords = new LinkedList();
  private final ITvInputClient mClient;
  private final Object mLock = new Object();
  private int mNextSeq;
  private final ITvInputManager mService;
  private final SparseArray<SessionCallbackRecord> mSessionCallbackRecordMap = new SparseArray();
  private final Map<String, Integer> mStateMap = new ArrayMap();
  private final int mUserId;
  
  public TvInputManager(ITvInputManager arg1, int paramInt)
  {
    this.mService = ???;
    this.mUserId = paramInt;
    this.mClient = new ITvInputClient.Stub()
    {
      private void postVideoSizeChangedIfNeededLocked(TvInputManager.SessionCallbackRecord paramAnonymousSessionCallbackRecord)
      {
        TvTrackInfo localTvTrackInfo = TvInputManager.SessionCallbackRecord.-get0(paramAnonymousSessionCallbackRecord).getVideoTrackToNotify();
        if (localTvTrackInfo != null) {
          paramAnonymousSessionCallbackRecord.postVideoSizeChanged(localTvTrackInfo.getVideoWidth(), localTvTrackInfo.getVideoHeight());
        }
      }
      
      public void onChannelRetuned(Uri paramAnonymousUri, int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt);
            return;
          }
          localSessionCallbackRecord.postChannelRetuned(paramAnonymousUri);
          return;
        }
      }
      
      public void onContentAllowed(int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt);
            return;
          }
          localSessionCallbackRecord.postContentAllowed();
          return;
        }
      }
      
      public void onContentBlocked(String paramAnonymousString, int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt);
            return;
          }
          localSessionCallbackRecord.postContentBlocked(TvContentRating.unflattenFromString(paramAnonymousString));
          return;
        }
      }
      
      public void onError(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt2);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt2);
            return;
          }
          localSessionCallbackRecord.postError(paramAnonymousInt1);
          return;
        }
      }
      
      public void onLayoutSurface(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt5);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt5);
            return;
          }
          localSessionCallbackRecord.postLayoutSurface(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
          return;
        }
      }
      
      public void onRecordingStopped(Uri paramAnonymousUri, int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt);
            return;
          }
          localSessionCallbackRecord.postRecordingStopped(paramAnonymousUri);
          return;
        }
      }
      
      public void onSessionCreated(String paramAnonymousString, IBinder paramAnonymousIBinder, InputChannel paramAnonymousInputChannel, int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for " + paramAnonymousIBinder);
            return;
          }
          paramAnonymousString = null;
          if (paramAnonymousIBinder != null) {
            paramAnonymousString = new TvInputManager.Session(paramAnonymousIBinder, paramAnonymousInputChannel, TvInputManager.-get2(TvInputManager.this), TvInputManager.-get5(TvInputManager.this), paramAnonymousInt, TvInputManager.-get3(TvInputManager.this), null);
          }
          localSessionCallbackRecord.postSessionCreated(paramAnonymousString);
          return;
        }
      }
      
      public void onSessionEvent(String paramAnonymousString, Bundle paramAnonymousBundle, int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt);
            return;
          }
          localSessionCallbackRecord.postSessionEvent(paramAnonymousString, paramAnonymousBundle);
          return;
        }
      }
      
      public void onSessionReleased(int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          TvInputManager.-get3(TvInputManager.this).delete(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq:" + paramAnonymousInt);
            return;
          }
          TvInputManager.Session.-wrap1(TvInputManager.SessionCallbackRecord.-get0(localSessionCallbackRecord));
          localSessionCallbackRecord.postSessionReleased();
          return;
        }
      }
      
      public void onTimeShiftCurrentPositionChanged(long paramAnonymousLong, int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt);
            return;
          }
          localSessionCallbackRecord.postTimeShiftCurrentPositionChanged(paramAnonymousLong);
          return;
        }
      }
      
      public void onTimeShiftStartPositionChanged(long paramAnonymousLong, int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt);
            return;
          }
          localSessionCallbackRecord.postTimeShiftStartPositionChanged(paramAnonymousLong);
          return;
        }
      }
      
      public void onTimeShiftStatusChanged(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt2);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt2);
            return;
          }
          localSessionCallbackRecord.postTimeShiftStatusChanged(paramAnonymousInt1);
          return;
        }
      }
      
      public void onTrackSelected(int paramAnonymousInt1, String paramAnonymousString, int paramAnonymousInt2)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt2);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt2);
            return;
          }
          if (TvInputManager.SessionCallbackRecord.-get0(localSessionCallbackRecord).updateTrackSelection(paramAnonymousInt1, paramAnonymousString))
          {
            localSessionCallbackRecord.postTrackSelected(paramAnonymousInt1, paramAnonymousString);
            postVideoSizeChangedIfNeededLocked(localSessionCallbackRecord);
          }
          return;
        }
      }
      
      public void onTracksChanged(List<TvTrackInfo> paramAnonymousList, int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt);
            return;
          }
          if (TvInputManager.SessionCallbackRecord.-get0(localSessionCallbackRecord).updateTracks(paramAnonymousList))
          {
            localSessionCallbackRecord.postTracksChanged(paramAnonymousList);
            postVideoSizeChangedIfNeededLocked(localSessionCallbackRecord);
          }
          return;
        }
      }
      
      public void onTuned(int paramAnonymousInt, Uri paramAnonymousUri)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt);
            return;
          }
          localSessionCallbackRecord.postTuned(paramAnonymousUri);
          return;
        }
      }
      
      public void onVideoAvailable(int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt);
            return;
          }
          localSessionCallbackRecord.postVideoAvailable();
          return;
        }
      }
      
      public void onVideoUnavailable(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        synchronized (TvInputManager.-get3(TvInputManager.this))
        {
          TvInputManager.SessionCallbackRecord localSessionCallbackRecord = (TvInputManager.SessionCallbackRecord)TvInputManager.-get3(TvInputManager.this).get(paramAnonymousInt2);
          if (localSessionCallbackRecord == null)
          {
            Log.e("TvInputManager", "Callback not found for seq " + paramAnonymousInt2);
            return;
          }
          localSessionCallbackRecord.postVideoUnavailable(paramAnonymousInt1);
          return;
        }
      }
    };
    ??? = new ITvInputManagerCallback.Stub()
    {
      public void onInputAdded(String paramAnonymousString)
      {
        synchronized (TvInputManager.-get1(TvInputManager.this))
        {
          TvInputManager.-get4(TvInputManager.this).put(paramAnonymousString, Integer.valueOf(0));
          Iterator localIterator = TvInputManager.-get0(TvInputManager.this).iterator();
          if (localIterator.hasNext()) {
            ((TvInputManager.TvInputCallbackRecord)localIterator.next()).postInputAdded(paramAnonymousString);
          }
        }
      }
      
      public void onInputRemoved(String paramAnonymousString)
      {
        synchronized (TvInputManager.-get1(TvInputManager.this))
        {
          TvInputManager.-get4(TvInputManager.this).remove(paramAnonymousString);
          Iterator localIterator = TvInputManager.-get0(TvInputManager.this).iterator();
          if (localIterator.hasNext()) {
            ((TvInputManager.TvInputCallbackRecord)localIterator.next()).postInputRemoved(paramAnonymousString);
          }
        }
      }
      
      public void onInputStateChanged(String paramAnonymousString, int paramAnonymousInt)
      {
        synchronized (TvInputManager.-get1(TvInputManager.this))
        {
          TvInputManager.-get4(TvInputManager.this).put(paramAnonymousString, Integer.valueOf(paramAnonymousInt));
          Iterator localIterator = TvInputManager.-get0(TvInputManager.this).iterator();
          if (localIterator.hasNext()) {
            ((TvInputManager.TvInputCallbackRecord)localIterator.next()).postInputStateChanged(paramAnonymousString, paramAnonymousInt);
          }
        }
      }
      
      public void onInputUpdated(String paramAnonymousString)
      {
        synchronized (TvInputManager.-get1(TvInputManager.this))
        {
          Iterator localIterator = TvInputManager.-get0(TvInputManager.this).iterator();
          if (localIterator.hasNext()) {
            ((TvInputManager.TvInputCallbackRecord)localIterator.next()).postInputUpdated(paramAnonymousString);
          }
        }
      }
      
      public void onTvInputInfoUpdated(TvInputInfo paramAnonymousTvInputInfo)
      {
        synchronized (TvInputManager.-get1(TvInputManager.this))
        {
          Iterator localIterator = TvInputManager.-get0(TvInputManager.this).iterator();
          if (localIterator.hasNext()) {
            ((TvInputManager.TvInputCallbackRecord)localIterator.next()).postTvInputInfoUpdated(paramAnonymousTvInputInfo);
          }
        }
      }
    };
    try
    {
      if (this.mService != null)
      {
        this.mService.registerCallback(???, this.mUserId);
        Object localObject1 = this.mService.getTvInputList(this.mUserId);
        synchronized (this.mLock)
        {
          localObject1 = ((Iterable)localObject1).iterator();
          if (((Iterator)localObject1).hasNext())
          {
            String str = ((TvInputInfo)((Iterator)localObject1).next()).getId();
            this.mStateMap.put(str, Integer.valueOf(this.mService.getTvInputState(str, this.mUserId)));
          }
        }
      }
    }
    catch (RemoteException ???)
    {
      throw ???.rethrowFromSystemServer();
    }
  }
  
  private void createSessionInternal(String paramString, boolean paramBoolean, SessionCallback arg3, Handler paramHandler)
  {
    Preconditions.checkNotNull(paramString);
    Preconditions.checkNotNull(???);
    Preconditions.checkNotNull(paramHandler);
    paramHandler = new SessionCallbackRecord(???, paramHandler);
    synchronized (this.mSessionCallbackRecordMap)
    {
      int i = this.mNextSeq;
      this.mNextSeq = (i + 1);
      this.mSessionCallbackRecordMap.put(i, paramHandler);
      try
      {
        this.mService.createSession(this.mClient, paramString, paramBoolean, i, this.mUserId);
        return;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
  }
  
  public Hardware acquireTvInputHardware(int paramInt, TvInputInfo paramTvInputInfo, final HardwareCallback paramHardwareCallback)
  {
    try
    {
      paramTvInputInfo = new Hardware(this.mService.acquireTvInputHardware(paramInt, new ITvInputHardwareCallback.Stub()
      {
        public void onReleased()
        {
          paramHardwareCallback.onReleased();
        }
        
        public void onStreamConfigChanged(TvStreamConfig[] paramAnonymousArrayOfTvStreamConfig)
        {
          paramHardwareCallback.onStreamConfigChanged(paramAnonymousArrayOfTvStreamConfig);
        }
      }, paramTvInputInfo, this.mUserId), null);
      return paramTvInputInfo;
    }
    catch (RemoteException paramTvInputInfo)
    {
      throw paramTvInputInfo.rethrowFromSystemServer();
    }
  }
  
  public Hardware acquireTvInputHardware(int paramInt, HardwareCallback paramHardwareCallback, TvInputInfo paramTvInputInfo)
  {
    return acquireTvInputHardware(paramInt, paramTvInputInfo, paramHardwareCallback);
  }
  
  public void addBlockedRating(TvContentRating paramTvContentRating)
  {
    Preconditions.checkNotNull(paramTvContentRating);
    try
    {
      this.mService.addBlockedRating(paramTvContentRating.flattenToString(), this.mUserId);
      return;
    }
    catch (RemoteException paramTvContentRating)
    {
      throw paramTvContentRating.rethrowFromSystemServer();
    }
  }
  
  public boolean captureFrame(String paramString, Surface paramSurface, TvStreamConfig paramTvStreamConfig)
  {
    try
    {
      boolean bool = this.mService.captureFrame(paramString, paramSurface, paramTvStreamConfig, this.mUserId);
      return bool;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void createRecordingSession(String paramString, SessionCallback paramSessionCallback, Handler paramHandler)
  {
    createSessionInternal(paramString, true, paramSessionCallback, paramHandler);
  }
  
  public void createSession(String paramString, SessionCallback paramSessionCallback, Handler paramHandler)
  {
    createSessionInternal(paramString, false, paramSessionCallback, paramHandler);
  }
  
  public List<TvStreamConfig> getAvailableTvStreamConfigList(String paramString)
  {
    try
    {
      paramString = this.mService.getAvailableTvStreamConfigList(paramString, this.mUserId);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public List<TvContentRating> getBlockedRatings()
  {
    try
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = this.mService.getBlockedRatings(this.mUserId).iterator();
      while (localIterator.hasNext()) {
        localArrayList.add(TvContentRating.unflattenFromString((String)localIterator.next()));
      }
      return localRemoteException;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<DvbDeviceInfo> getDvbDeviceList()
  {
    try
    {
      List localList = this.mService.getDvbDeviceList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<TvInputHardwareInfo> getHardwareList()
  {
    try
    {
      List localList = this.mService.getHardwareList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public int getInputState(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    synchronized (this.mLock)
    {
      Integer localInteger = (Integer)this.mStateMap.get(paramString);
      if (localInteger == null)
      {
        Log.w("TvInputManager", "Unrecognized input ID: " + paramString);
        return 2;
      }
      int i = localInteger.intValue();
      return i;
    }
  }
  
  public List<TvContentRatingSystemInfo> getTvContentRatingSystemList()
  {
    try
    {
      List localList = this.mService.getTvContentRatingSystemList(this.mUserId);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public TvInputInfo getTvInputInfo(String paramString)
  {
    Preconditions.checkNotNull(paramString);
    try
    {
      paramString = this.mService.getTvInputInfo(paramString, this.mUserId);
      return paramString;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public List<TvInputInfo> getTvInputList()
  {
    try
    {
      List localList = this.mService.getTvInputList(this.mUserId);
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isParentalControlsEnabled()
  {
    try
    {
      boolean bool = this.mService.isParentalControlsEnabled(this.mUserId);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public boolean isRatingBlocked(TvContentRating paramTvContentRating)
  {
    Preconditions.checkNotNull(paramTvContentRating);
    try
    {
      boolean bool = this.mService.isRatingBlocked(paramTvContentRating.flattenToString(), this.mUserId);
      return bool;
    }
    catch (RemoteException paramTvContentRating)
    {
      throw paramTvContentRating.rethrowFromSystemServer();
    }
  }
  
  public boolean isSingleSessionActive()
  {
    try
    {
      boolean bool = this.mService.isSingleSessionActive(this.mUserId);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public ParcelFileDescriptor openDvbDevice(DvbDeviceInfo paramDvbDeviceInfo, int paramInt)
  {
    if ((paramInt < 0) || (2 < paramInt)) {
      try
      {
        throw new IllegalArgumentException("Invalid DVB device: " + paramInt);
      }
      catch (RemoteException paramDvbDeviceInfo)
      {
        throw paramDvbDeviceInfo.rethrowFromSystemServer();
      }
    }
    paramDvbDeviceInfo = this.mService.openDvbDevice(paramDvbDeviceInfo, paramInt);
    return paramDvbDeviceInfo;
  }
  
  public void registerCallback(TvInputCallback paramTvInputCallback, Handler paramHandler)
  {
    Preconditions.checkNotNull(paramTvInputCallback);
    Preconditions.checkNotNull(paramHandler);
    synchronized (this.mLock)
    {
      this.mCallbackRecords.add(new TvInputCallbackRecord(paramTvInputCallback, paramHandler));
      return;
    }
  }
  
  public void releaseTvInputHardware(int paramInt, Hardware paramHardware)
  {
    try
    {
      this.mService.releaseTvInputHardware(paramInt, Hardware.-wrap0(paramHardware), this.mUserId);
      return;
    }
    catch (RemoteException paramHardware)
    {
      throw paramHardware.rethrowFromSystemServer();
    }
  }
  
  public void removeBlockedRating(TvContentRating paramTvContentRating)
  {
    Preconditions.checkNotNull(paramTvContentRating);
    try
    {
      this.mService.removeBlockedRating(paramTvContentRating.flattenToString(), this.mUserId);
      return;
    }
    catch (RemoteException paramTvContentRating)
    {
      throw paramTvContentRating.rethrowFromSystemServer();
    }
  }
  
  public void setParentalControlsEnabled(boolean paramBoolean)
  {
    try
    {
      this.mService.setParentalControlsEnabled(paramBoolean, this.mUserId);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void unregisterCallback(TvInputCallback paramTvInputCallback)
  {
    Preconditions.checkNotNull(paramTvInputCallback);
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mCallbackRecords.iterator();
      while (localIterator.hasNext()) {
        if (((TvInputCallbackRecord)localIterator.next()).getCallback() == paramTvInputCallback) {
          localIterator.remove();
        }
      }
      return;
    }
  }
  
  public void updateTvInputInfo(TvInputInfo paramTvInputInfo)
  {
    Preconditions.checkNotNull(paramTvInputInfo);
    try
    {
      this.mService.updateTvInputInfo(paramTvInputInfo, this.mUserId);
      return;
    }
    catch (RemoteException paramTvInputInfo)
    {
      throw paramTvInputInfo.rethrowFromSystemServer();
    }
  }
  
  public static final class Hardware
  {
    private final ITvInputHardware mInterface;
    
    private Hardware(ITvInputHardware paramITvInputHardware)
    {
      this.mInterface = paramITvInputHardware;
    }
    
    private ITvInputHardware getInterface()
    {
      return this.mInterface;
    }
    
    public boolean dispatchKeyEventToHdmi(KeyEvent paramKeyEvent)
    {
      try
      {
        boolean bool = this.mInterface.dispatchKeyEventToHdmi(paramKeyEvent);
        return bool;
      }
      catch (RemoteException paramKeyEvent)
      {
        throw new RuntimeException(paramKeyEvent);
      }
    }
    
    public void overrideAudioSink(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4)
    {
      try
      {
        this.mInterface.overrideAudioSink(paramInt1, paramString, paramInt2, paramInt3, paramInt4);
        return;
      }
      catch (RemoteException paramString)
      {
        throw new RuntimeException(paramString);
      }
    }
    
    public void setStreamVolume(float paramFloat)
    {
      try
      {
        this.mInterface.setStreamVolume(paramFloat);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw new RuntimeException(localRemoteException);
      }
    }
    
    public boolean setSurface(Surface paramSurface, TvStreamConfig paramTvStreamConfig)
    {
      try
      {
        boolean bool = this.mInterface.setSurface(paramSurface, paramTvStreamConfig);
        return bool;
      }
      catch (RemoteException paramSurface)
      {
        throw new RuntimeException(paramSurface);
      }
    }
  }
  
  public static abstract class HardwareCallback
  {
    public abstract void onReleased();
    
    public abstract void onStreamConfigChanged(TvStreamConfig[] paramArrayOfTvStreamConfig);
  }
  
  public static final class Session
  {
    static final int DISPATCH_HANDLED = 1;
    static final int DISPATCH_IN_PROGRESS = -1;
    static final int DISPATCH_NOT_HANDLED = 0;
    private static final long INPUT_SESSION_NOT_RESPONDING_TIMEOUT = 2500L;
    private final List<TvTrackInfo> mAudioTracks = new ArrayList();
    private InputChannel mChannel;
    private final InputEventHandler mHandler = new InputEventHandler(Looper.getMainLooper());
    private final Object mMetadataLock = new Object();
    private final Pools.Pool<PendingEvent> mPendingEventPool = new Pools.SimplePool(20);
    private final SparseArray<PendingEvent> mPendingEvents = new SparseArray(20);
    private String mSelectedAudioTrackId;
    private String mSelectedSubtitleTrackId;
    private String mSelectedVideoTrackId;
    private TvInputEventSender mSender;
    private final int mSeq;
    private final ITvInputManager mService;
    private final SparseArray<TvInputManager.SessionCallbackRecord> mSessionCallbackRecordMap;
    private final List<TvTrackInfo> mSubtitleTracks = new ArrayList();
    private IBinder mToken;
    private final int mUserId;
    private int mVideoHeight;
    private final List<TvTrackInfo> mVideoTracks = new ArrayList();
    private int mVideoWidth;
    
    private Session(IBinder paramIBinder, InputChannel paramInputChannel, ITvInputManager paramITvInputManager, int paramInt1, int paramInt2, SparseArray<TvInputManager.SessionCallbackRecord> paramSparseArray)
    {
      this.mToken = paramIBinder;
      this.mChannel = paramInputChannel;
      this.mService = paramITvInputManager;
      this.mUserId = paramInt1;
      this.mSeq = paramInt2;
      this.mSessionCallbackRecordMap = paramSparseArray;
    }
    
    private boolean containsTrack(List<TvTrackInfo> paramList, String paramString)
    {
      paramList = paramList.iterator();
      while (paramList.hasNext()) {
        if (((TvTrackInfo)paramList.next()).getId().equals(paramString)) {
          return true;
        }
      }
      return false;
    }
    
    private void flushPendingEventsLocked()
    {
      this.mHandler.removeMessages(3);
      int j = this.mPendingEvents.size();
      int i = 0;
      while (i < j)
      {
        int k = this.mPendingEvents.keyAt(i);
        Message localMessage = this.mHandler.obtainMessage(3, k, 0);
        localMessage.setAsynchronous(true);
        localMessage.sendToTarget();
        i += 1;
      }
    }
    
    private PendingEvent obtainPendingEventLocked(InputEvent paramInputEvent, Object paramObject, FinishedInputEventCallback paramFinishedInputEventCallback, Handler paramHandler)
    {
      PendingEvent localPendingEvent2 = (PendingEvent)this.mPendingEventPool.acquire();
      PendingEvent localPendingEvent1 = localPendingEvent2;
      if (localPendingEvent2 == null) {
        localPendingEvent1 = new PendingEvent(null);
      }
      localPendingEvent1.mEvent = paramInputEvent;
      localPendingEvent1.mEventToken = paramObject;
      localPendingEvent1.mCallback = paramFinishedInputEventCallback;
      localPendingEvent1.mEventHandler = paramHandler;
      return localPendingEvent1;
    }
    
    private void recyclePendingEventLocked(PendingEvent paramPendingEvent)
    {
      paramPendingEvent.recycle();
      this.mPendingEventPool.release(paramPendingEvent);
    }
    
    private void releaseInternal()
    {
      this.mToken = null;
      synchronized (this.mHandler)
      {
        if (this.mChannel != null)
        {
          if (this.mSender != null)
          {
            flushPendingEventsLocked();
            this.mSender.dispose();
            this.mSender = null;
          }
          this.mChannel.dispose();
          this.mChannel = null;
        }
      }
      synchronized (this.mSessionCallbackRecordMap)
      {
        this.mSessionCallbackRecordMap.remove(this.mSeq);
        return;
        localObject2 = finally;
        throw ((Throwable)localObject2);
      }
    }
    
    private void sendInputEventAndReportResultOnMainLooper(PendingEvent paramPendingEvent)
    {
      synchronized (this.mHandler)
      {
        int i = sendInputEventOnMainLooperLocked(paramPendingEvent);
        if (i == -1) {
          return;
        }
        invokeFinishedInputEventCallback(paramPendingEvent, false);
        return;
      }
    }
    
    private int sendInputEventOnMainLooperLocked(PendingEvent paramPendingEvent)
    {
      if (this.mChannel != null)
      {
        if (this.mSender == null) {
          this.mSender = new TvInputEventSender(this.mChannel, this.mHandler.getLooper());
        }
        InputEvent localInputEvent = paramPendingEvent.mEvent;
        int i = localInputEvent.getSequenceNumber();
        if (this.mSender.sendInputEvent(i, localInputEvent))
        {
          this.mPendingEvents.put(i, paramPendingEvent);
          paramPendingEvent = this.mHandler.obtainMessage(2, paramPendingEvent);
          paramPendingEvent.setAsynchronous(true);
          this.mHandler.sendMessageDelayed(paramPendingEvent, 2500L);
          return -1;
        }
        Log.w("TvInputManager", "Unable to send input event to session: " + this.mToken + " dropping:" + localInputEvent);
      }
      return 0;
    }
    
    void createOverlayView(View paramView, Rect paramRect)
    {
      Preconditions.checkNotNull(paramView);
      Preconditions.checkNotNull(paramRect);
      if (paramView.getWindowToken() == null) {
        throw new IllegalStateException("view must be attached to a window");
      }
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.createOverlayView(this.mToken, paramView.getWindowToken(), paramRect, this.mUserId);
        return;
      }
      catch (RemoteException paramView)
      {
        throw paramView.rethrowFromSystemServer();
      }
    }
    
    public int dispatchInputEvent(InputEvent paramInputEvent, Object paramObject, FinishedInputEventCallback paramFinishedInputEventCallback, Handler paramHandler)
    {
      Preconditions.checkNotNull(paramInputEvent);
      Preconditions.checkNotNull(paramFinishedInputEventCallback);
      Preconditions.checkNotNull(paramHandler);
      synchronized (this.mHandler)
      {
        InputChannel localInputChannel = this.mChannel;
        if (localInputChannel == null) {
          return 0;
        }
        paramInputEvent = obtainPendingEventLocked(paramInputEvent, paramObject, paramFinishedInputEventCallback, paramHandler);
        if (Looper.myLooper() == Looper.getMainLooper())
        {
          int i = sendInputEventOnMainLooperLocked(paramInputEvent);
          return i;
        }
        paramInputEvent = this.mHandler.obtainMessage(1, paramInputEvent);
        paramInputEvent.setAsynchronous(true);
        this.mHandler.sendMessage(paramInputEvent);
        return -1;
      }
    }
    
    public void dispatchSurfaceChanged(int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.dispatchSurfaceChanged(this.mToken, paramInt1, paramInt2, paramInt3, this.mUserId);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    void finishedInputEvent(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
    {
      synchronized (this.mHandler)
      {
        paramInt = this.mPendingEvents.indexOfKey(paramInt);
        if (paramInt < 0) {
          return;
        }
        PendingEvent localPendingEvent = (PendingEvent)this.mPendingEvents.valueAt(paramInt);
        this.mPendingEvents.removeAt(paramInt);
        if (paramBoolean2)
        {
          Log.w("TvInputManager", "Timeout waiting for session to handle input event after 2500 ms: " + this.mToken);
          invokeFinishedInputEventCallback(localPendingEvent, paramBoolean1);
          return;
        }
        this.mHandler.removeMessages(2, localPendingEvent);
      }
    }
    
    public String getSelectedTrack(int paramInt)
    {
      localObject1 = this.mMetadataLock;
      if (paramInt == 0) {}
      String str;
      try
      {
        str = this.mSelectedAudioTrackId;
        return str;
      }
      finally {}
      if (paramInt == 1)
      {
        str = this.mSelectedVideoTrackId;
        return str;
      }
      if (paramInt == 2)
      {
        str = this.mSelectedSubtitleTrackId;
        return str;
      }
      throw new IllegalArgumentException("invalid type: " + paramInt);
    }
    
    IBinder getToken()
    {
      return this.mToken;
    }
    
    public List<TvTrackInfo> getTracks(int paramInt)
    {
      localObject1 = this.mMetadataLock;
      if (paramInt == 0) {}
      Object localObject2;
      try
      {
        localObject2 = this.mAudioTracks;
        if (localObject2 == null) {
          return null;
        }
        localObject2 = new ArrayList(this.mAudioTracks);
        return (List<TvTrackInfo>)localObject2;
      }
      finally {}
      if (paramInt == 1)
      {
        localObject2 = this.mVideoTracks;
        if (localObject2 == null) {
          return null;
        }
        localObject2 = new ArrayList(this.mVideoTracks);
        return (List<TvTrackInfo>)localObject2;
      }
      if (paramInt == 2)
      {
        localObject2 = this.mSubtitleTracks;
        if (localObject2 == null) {
          return null;
        }
        localObject2 = new ArrayList(this.mSubtitleTracks);
        return (List<TvTrackInfo>)localObject2;
      }
      throw new IllegalArgumentException("invalid type: " + paramInt);
    }
    
    TvTrackInfo getVideoTrackToNotify()
    {
      synchronized (this.mMetadataLock)
      {
        if ((!this.mVideoTracks.isEmpty()) && (this.mSelectedVideoTrackId != null))
        {
          Iterator localIterator = this.mVideoTracks.iterator();
          while (localIterator.hasNext())
          {
            TvTrackInfo localTvTrackInfo = (TvTrackInfo)localIterator.next();
            if (localTvTrackInfo.getId().equals(this.mSelectedVideoTrackId))
            {
              int i = localTvTrackInfo.getVideoWidth();
              int j = localTvTrackInfo.getVideoHeight();
              if ((this.mVideoWidth != i) || (this.mVideoHeight != j))
              {
                this.mVideoWidth = i;
                this.mVideoHeight = j;
                return localTvTrackInfo;
              }
            }
          }
        }
        return null;
      }
    }
    
    void invokeFinishedInputEventCallback(PendingEvent paramPendingEvent, boolean paramBoolean)
    {
      paramPendingEvent.mHandled = paramBoolean;
      if (paramPendingEvent.mEventHandler.getLooper().isCurrentThread())
      {
        paramPendingEvent.run();
        return;
      }
      paramPendingEvent = Message.obtain(paramPendingEvent.mEventHandler, paramPendingEvent);
      paramPendingEvent.setAsynchronous(true);
      paramPendingEvent.sendToTarget();
    }
    
    void relayoutOverlayView(Rect paramRect)
    {
      Preconditions.checkNotNull(paramRect);
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.relayoutOverlayView(this.mToken, paramRect, this.mUserId);
        return;
      }
      catch (RemoteException paramRect)
      {
        throw paramRect.rethrowFromSystemServer();
      }
    }
    
    public void release()
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.releaseSession(this.mToken, this.mUserId);
        releaseInternal();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    void removeOverlayView()
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.removeOverlayView(this.mToken, this.mUserId);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    public void selectTrack(int paramInt, String paramString)
    {
      Object localObject = this.mMetadataLock;
      if ((paramInt != 0) || (paramString != null)) {}
      do
      {
        do
        {
          try
          {
            boolean bool = containsTrack(this.mAudioTracks, paramString);
            if (bool)
            {
              if (this.mToken != null) {
                break label221;
              }
              Log.w("TvInputManager", "The session has been already released");
              return;
            }
            Log.w("TvInputManager", "Invalid audio trackId: " + paramString);
            return;
          }
          finally {}
          if (paramInt != 1) {
            break;
          }
        } while ((paramString == null) || (containsTrack(this.mVideoTracks, paramString)));
        Log.w("TvInputManager", "Invalid video trackId: " + paramString);
        return;
        if (paramInt != 2) {
          break;
        }
      } while ((paramString == null) || (containsTrack(this.mSubtitleTracks, paramString)));
      Log.w("TvInputManager", "Invalid subtitle trackId: " + paramString);
      return;
      throw new IllegalArgumentException("invalid type: " + paramInt);
      try
      {
        label221:
        this.mService.selectTrack(this.mToken, paramInt, paramString, this.mUserId);
        return;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
    
    public void sendAppPrivateCommand(String paramString, Bundle paramBundle)
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.sendAppPrivateCommand(this.mToken, paramString, paramBundle, this.mUserId);
        return;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
    }
    
    public void setCaptionEnabled(boolean paramBoolean)
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.setCaptionEnabled(this.mToken, paramBoolean, this.mUserId);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    void setMain()
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.setMainSession(this.mToken, this.mUserId);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    public void setStreamVolume(float paramFloat)
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      if ((paramFloat < 0.0F) || (paramFloat > 1.0F)) {
        try
        {
          throw new IllegalArgumentException("volume should be between 0.0f and 1.0f");
        }
        catch (RemoteException localRemoteException)
        {
          throw localRemoteException.rethrowFromSystemServer();
        }
      }
      this.mService.setVolume(this.mToken, paramFloat, this.mUserId);
    }
    
    public void setSurface(Surface paramSurface)
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.setSurface(this.mToken, paramSurface, this.mUserId);
        return;
      }
      catch (RemoteException paramSurface)
      {
        throw paramSurface.rethrowFromSystemServer();
      }
    }
    
    void startRecording(Uri paramUri)
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.startRecording(this.mToken, paramUri, this.mUserId);
        return;
      }
      catch (RemoteException paramUri)
      {
        throw paramUri.rethrowFromSystemServer();
      }
    }
    
    void stopRecording()
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.stopRecording(this.mToken, this.mUserId);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    void timeShiftEnablePositionTracking(boolean paramBoolean)
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.timeShiftEnablePositionTracking(this.mToken, paramBoolean, this.mUserId);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    void timeShiftPause()
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.timeShiftPause(this.mToken, this.mUserId);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    void timeShiftPlay(Uri paramUri)
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.timeShiftPlay(this.mToken, paramUri, this.mUserId);
        return;
      }
      catch (RemoteException paramUri)
      {
        throw paramUri.rethrowFromSystemServer();
      }
    }
    
    void timeShiftResume()
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.timeShiftResume(this.mToken, this.mUserId);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    void timeShiftSeekTo(long paramLong)
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.timeShiftSeekTo(this.mToken, paramLong, this.mUserId);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    void timeShiftSetPlaybackParams(PlaybackParams paramPlaybackParams)
    {
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.timeShiftSetPlaybackParams(this.mToken, paramPlaybackParams, this.mUserId);
        return;
      }
      catch (RemoteException paramPlaybackParams)
      {
        throw paramPlaybackParams.rethrowFromSystemServer();
      }
    }
    
    public void tune(Uri paramUri)
    {
      tune(paramUri, null);
    }
    
    public void tune(Uri paramUri, Bundle paramBundle)
    {
      Preconditions.checkNotNull(paramUri);
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      synchronized (this.mMetadataLock)
      {
        this.mAudioTracks.clear();
        this.mVideoTracks.clear();
        this.mSubtitleTracks.clear();
        this.mSelectedAudioTrackId = null;
        this.mSelectedVideoTrackId = null;
        this.mSelectedSubtitleTrackId = null;
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
      }
    }
    
    void unblockContent(TvContentRating paramTvContentRating)
    {
      Preconditions.checkNotNull(paramTvContentRating);
      if (this.mToken == null)
      {
        Log.w("TvInputManager", "The session has been already released");
        return;
      }
      try
      {
        this.mService.unblockContent(this.mToken, paramTvContentRating.flattenToString(), this.mUserId);
        return;
      }
      catch (RemoteException paramTvContentRating)
      {
        throw paramTvContentRating.rethrowFromSystemServer();
      }
    }
    
    boolean updateTrackSelection(int paramInt, String paramString)
    {
      Object localObject = this.mMetadataLock;
      if (paramInt == 0) {}
      try
      {
        if (TextUtils.equals(paramString, this.mSelectedAudioTrackId))
        {
          if ((paramInt != 1) || (TextUtils.equals(paramString, this.mSelectedVideoTrackId)))
          {
            if (paramInt == 2)
            {
              boolean bool = TextUtils.equals(paramString, this.mSelectedSubtitleTrackId);
              if (!bool) {
                break label83;
              }
            }
            return false;
          }
        }
        else
        {
          this.mSelectedAudioTrackId = paramString;
          return true;
        }
        this.mSelectedVideoTrackId = paramString;
        return true;
        label83:
        this.mSelectedSubtitleTrackId = paramString;
        return true;
      }
      finally {}
    }
    
    boolean updateTracks(List<TvTrackInfo> paramList)
    {
      boolean bool1 = false;
      for (;;)
      {
        TvTrackInfo localTvTrackInfo;
        synchronized (this.mMetadataLock)
        {
          this.mAudioTracks.clear();
          this.mVideoTracks.clear();
          this.mSubtitleTracks.clear();
          paramList = paramList.iterator();
          if (!paramList.hasNext()) {
            break;
          }
          localTvTrackInfo = (TvTrackInfo)paramList.next();
          if (localTvTrackInfo.getType() == 0) {
            this.mAudioTracks.add(localTvTrackInfo);
          }
        }
        if (localTvTrackInfo.getType() == 1) {
          this.mVideoTracks.add(localTvTrackInfo);
        } else if (localTvTrackInfo.getType() == 2) {
          this.mSubtitleTracks.add(localTvTrackInfo);
        }
      }
      if ((this.mAudioTracks.isEmpty()) && (this.mVideoTracks.isEmpty()))
      {
        boolean bool2 = this.mSubtitleTracks.isEmpty();
        if (!bool2) {
          break label190;
        }
      }
      for (;;)
      {
        return bool1;
        bool1 = true;
        continue;
        label190:
        bool1 = true;
      }
    }
    
    public static abstract interface FinishedInputEventCallback
    {
      public abstract void onFinishedInputEvent(Object paramObject, boolean paramBoolean);
    }
    
    private final class InputEventHandler
      extends Handler
    {
      public static final int MSG_FLUSH_INPUT_EVENT = 3;
      public static final int MSG_SEND_INPUT_EVENT = 1;
      public static final int MSG_TIMEOUT_INPUT_EVENT = 2;
      
      InputEventHandler(Looper paramLooper)
      {
        super(null, true);
      }
      
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default: 
          return;
        case 1: 
          TvInputManager.Session.-wrap2(TvInputManager.Session.this, (TvInputManager.Session.PendingEvent)paramMessage.obj);
          return;
        case 2: 
          TvInputManager.Session.this.finishedInputEvent(paramMessage.arg1, false, true);
          return;
        }
        TvInputManager.Session.this.finishedInputEvent(paramMessage.arg1, false, false);
      }
    }
    
    private final class PendingEvent
      implements Runnable
    {
      public TvInputManager.Session.FinishedInputEventCallback mCallback;
      public InputEvent mEvent;
      public Handler mEventHandler;
      public Object mEventToken;
      public boolean mHandled;
      
      private PendingEvent() {}
      
      public void recycle()
      {
        this.mEvent = null;
        this.mEventToken = null;
        this.mCallback = null;
        this.mEventHandler = null;
        this.mHandled = false;
      }
      
      public void run()
      {
        this.mCallback.onFinishedInputEvent(this.mEventToken, this.mHandled);
        synchronized (this.mEventHandler)
        {
          TvInputManager.Session.-wrap0(TvInputManager.Session.this, this);
          return;
        }
      }
    }
    
    private final class TvInputEventSender
      extends InputEventSender
    {
      public TvInputEventSender(InputChannel paramInputChannel, Looper paramLooper)
      {
        super(paramLooper);
      }
      
      public void onInputEventFinished(int paramInt, boolean paramBoolean)
      {
        TvInputManager.Session.this.finishedInputEvent(paramInt, paramBoolean, false);
      }
    }
  }
  
  public static abstract class SessionCallback
  {
    public void onChannelRetuned(TvInputManager.Session paramSession, Uri paramUri) {}
    
    public void onContentAllowed(TvInputManager.Session paramSession) {}
    
    public void onContentBlocked(TvInputManager.Session paramSession, TvContentRating paramTvContentRating) {}
    
    void onError(TvInputManager.Session paramSession, int paramInt) {}
    
    public void onLayoutSurface(TvInputManager.Session paramSession, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    void onRecordingStopped(TvInputManager.Session paramSession, Uri paramUri) {}
    
    public void onSessionCreated(TvInputManager.Session paramSession) {}
    
    public void onSessionEvent(TvInputManager.Session paramSession, String paramString, Bundle paramBundle) {}
    
    public void onSessionReleased(TvInputManager.Session paramSession) {}
    
    public void onTimeShiftCurrentPositionChanged(TvInputManager.Session paramSession, long paramLong) {}
    
    public void onTimeShiftStartPositionChanged(TvInputManager.Session paramSession, long paramLong) {}
    
    public void onTimeShiftStatusChanged(TvInputManager.Session paramSession, int paramInt) {}
    
    public void onTrackSelected(TvInputManager.Session paramSession, int paramInt, String paramString) {}
    
    public void onTracksChanged(TvInputManager.Session paramSession, List<TvTrackInfo> paramList) {}
    
    void onTuned(TvInputManager.Session paramSession, Uri paramUri) {}
    
    public void onVideoAvailable(TvInputManager.Session paramSession) {}
    
    public void onVideoSizeChanged(TvInputManager.Session paramSession, int paramInt1, int paramInt2) {}
    
    public void onVideoUnavailable(TvInputManager.Session paramSession, int paramInt) {}
  }
  
  private static final class SessionCallbackRecord
  {
    private final Handler mHandler;
    private TvInputManager.Session mSession;
    private final TvInputManager.SessionCallback mSessionCallback;
    
    SessionCallbackRecord(TvInputManager.SessionCallback paramSessionCallback, Handler paramHandler)
    {
      this.mSessionCallback = paramSessionCallback;
      this.mHandler = paramHandler;
    }
    
    void postChannelRetuned(final Uri paramUri)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onChannelRetuned(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramUri);
        }
      });
    }
    
    void postContentAllowed()
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onContentAllowed(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this));
        }
      });
    }
    
    void postContentBlocked(final TvContentRating paramTvContentRating)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onContentBlocked(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramTvContentRating);
        }
      });
    }
    
    void postError(final int paramInt)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onError(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramInt);
        }
      });
    }
    
    void postLayoutSurface(final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onLayoutSurface(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramInt1, paramInt2, paramInt3, paramInt4);
        }
      });
    }
    
    void postRecordingStopped(final Uri paramUri)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onRecordingStopped(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramUri);
        }
      });
    }
    
    void postSessionCreated(final TvInputManager.Session paramSession)
    {
      this.mSession = paramSession;
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onSessionCreated(paramSession);
        }
      });
    }
    
    void postSessionEvent(final String paramString, final Bundle paramBundle)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onSessionEvent(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramString, paramBundle);
        }
      });
    }
    
    void postSessionReleased()
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onSessionReleased(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this));
        }
      });
    }
    
    void postTimeShiftCurrentPositionChanged(final long paramLong)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onTimeShiftCurrentPositionChanged(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramLong);
        }
      });
    }
    
    void postTimeShiftStartPositionChanged(final long paramLong)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onTimeShiftStartPositionChanged(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramLong);
        }
      });
    }
    
    void postTimeShiftStatusChanged(final int paramInt)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onTimeShiftStatusChanged(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramInt);
        }
      });
    }
    
    void postTrackSelected(final int paramInt, final String paramString)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onTrackSelected(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramInt, paramString);
        }
      });
    }
    
    void postTracksChanged(final List<TvTrackInfo> paramList)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onTracksChanged(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramList);
        }
      });
    }
    
    void postTuned(final Uri paramUri)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onTuned(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramUri);
        }
      });
    }
    
    void postVideoAvailable()
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onVideoAvailable(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this));
        }
      });
    }
    
    void postVideoSizeChanged(final int paramInt1, final int paramInt2)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onVideoSizeChanged(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramInt1, paramInt2);
        }
      });
    }
    
    void postVideoUnavailable(final int paramInt)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.SessionCallbackRecord.-get1(TvInputManager.SessionCallbackRecord.this).onVideoUnavailable(TvInputManager.SessionCallbackRecord.-get0(TvInputManager.SessionCallbackRecord.this), paramInt);
        }
      });
    }
  }
  
  public static abstract class TvInputCallback
  {
    public void onInputAdded(String paramString) {}
    
    public void onInputRemoved(String paramString) {}
    
    public void onInputStateChanged(String paramString, int paramInt) {}
    
    public void onInputUpdated(String paramString) {}
    
    public void onTvInputInfoUpdated(TvInputInfo paramTvInputInfo) {}
  }
  
  private static final class TvInputCallbackRecord
  {
    private final TvInputManager.TvInputCallback mCallback;
    private final Handler mHandler;
    
    public TvInputCallbackRecord(TvInputManager.TvInputCallback paramTvInputCallback, Handler paramHandler)
    {
      this.mCallback = paramTvInputCallback;
      this.mHandler = paramHandler;
    }
    
    public TvInputManager.TvInputCallback getCallback()
    {
      return this.mCallback;
    }
    
    public void postInputAdded(final String paramString)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.TvInputCallbackRecord.-get0(TvInputManager.TvInputCallbackRecord.this).onInputAdded(paramString);
        }
      });
    }
    
    public void postInputRemoved(final String paramString)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.TvInputCallbackRecord.-get0(TvInputManager.TvInputCallbackRecord.this).onInputRemoved(paramString);
        }
      });
    }
    
    public void postInputStateChanged(final String paramString, final int paramInt)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.TvInputCallbackRecord.-get0(TvInputManager.TvInputCallbackRecord.this).onInputStateChanged(paramString, paramInt);
        }
      });
    }
    
    public void postInputUpdated(final String paramString)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.TvInputCallbackRecord.-get0(TvInputManager.TvInputCallbackRecord.this).onInputUpdated(paramString);
        }
      });
    }
    
    public void postTvInputInfoUpdated(final TvInputInfo paramTvInputInfo)
    {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          TvInputManager.TvInputCallbackRecord.-get0(TvInputManager.TvInputCallbackRecord.this).onTvInputInfoUpdated(paramTvInputInfo);
        }
      });
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/TvInputManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */