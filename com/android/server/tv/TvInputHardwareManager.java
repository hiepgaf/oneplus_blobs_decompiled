package com.android.server.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiHotplugEvent;
import android.hardware.hdmi.IHdmiControlService;
import android.hardware.hdmi.IHdmiControlService.Stub;
import android.hardware.hdmi.IHdmiDeviceEventListener;
import android.hardware.hdmi.IHdmiDeviceEventListener.Stub;
import android.hardware.hdmi.IHdmiHotplugEventListener;
import android.hardware.hdmi.IHdmiHotplugEventListener.Stub;
import android.hardware.hdmi.IHdmiSystemAudioModeChangeListener;
import android.hardware.hdmi.IHdmiSystemAudioModeChangeListener.Stub;
import android.media.AudioDevicePort;
import android.media.AudioFormat;
import android.media.AudioGain;
import android.media.AudioGainConfig;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioPortUpdateListener;
import android.media.AudioPatch;
import android.media.AudioPort;
import android.media.AudioPortConfig;
import android.media.tv.ITvInputHardware;
import android.media.tv.ITvInputHardware.Stub;
import android.media.tv.ITvInputHardwareCallback;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvStreamConfig;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.Surface;
import com.android.internal.util.IndentingPrintWriter;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

class TvInputHardwareManager
  implements TvInputHal.Callback
{
  private static final String TAG = TvInputHardwareManager.class.getSimpleName();
  private final AudioManager mAudioManager;
  private final SparseArray<Connection> mConnections = new SparseArray();
  private final Context mContext;
  private int mCurrentIndex = 0;
  private int mCurrentMaxIndex = 0;
  private final TvInputHal mHal = new TvInputHal(this);
  private final Handler mHandler = new ListenerHandler(null);
  private final SparseArray<String> mHardwareInputIdMap = new SparseArray();
  private final List<TvInputHardwareInfo> mHardwareList = new ArrayList();
  private final IHdmiDeviceEventListener mHdmiDeviceEventListener = new HdmiDeviceEventListener(null);
  private final List<HdmiDeviceInfo> mHdmiDeviceList = new LinkedList();
  private final IHdmiHotplugEventListener mHdmiHotplugEventListener = new HdmiHotplugEventListener(null);
  private final SparseArray<String> mHdmiInputIdMap = new SparseArray();
  private final SparseBooleanArray mHdmiStateMap = new SparseBooleanArray();
  private final IHdmiSystemAudioModeChangeListener mHdmiSystemAudioModeChangeListener = new HdmiSystemAudioModeChangeListener(null);
  private final Map<String, TvInputInfo> mInputMap = new ArrayMap();
  private final Listener mListener;
  private final Object mLock = new Object();
  private final List<Message> mPendingHdmiDeviceEvents = new LinkedList();
  private final BroadcastReceiver mVolumeReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      TvInputHardwareManager.-wrap4(TvInputHardwareManager.this, paramAnonymousContext, paramAnonymousIntent);
    }
  };
  
  public TvInputHardwareManager(Context paramContext, Listener paramListener)
  {
    this.mContext = paramContext;
    this.mListener = paramListener;
    this.mAudioManager = ((AudioManager)paramContext.getSystemService("audio"));
    this.mHal.init();
  }
  
  private void buildHardwareListLocked()
  {
    this.mHardwareList.clear();
    int i = 0;
    while (i < this.mConnections.size())
    {
      this.mHardwareList.add(((Connection)this.mConnections.valueAt(i)).getHardwareInfoLocked());
      i += 1;
    }
  }
  
  private boolean checkUidChangedLocked(Connection paramConnection, int paramInt1, int paramInt2)
  {
    Integer localInteger = paramConnection.getCallingUidLocked();
    paramConnection = paramConnection.getResolvedUserIdLocked();
    if ((localInteger == null) || (paramConnection == null)) {}
    while ((localInteger.intValue() != paramInt1) || (paramConnection.intValue() != paramInt2)) {
      return true;
    }
    return false;
  }
  
  private int convertConnectedToState(boolean paramBoolean)
  {
    if (paramBoolean) {
      return 0;
    }
    return 2;
  }
  
  private int findDeviceIdForInputIdLocked(String paramString)
  {
    int i = 0;
    while (i < this.mConnections.size())
    {
      if (((Connection)this.mConnections.get(i)).getInfoLocked().getId().equals(paramString)) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private TvInputHardwareInfo findHardwareInfoForHdmiPortLocked(int paramInt)
  {
    Iterator localIterator = this.mHardwareList.iterator();
    while (localIterator.hasNext())
    {
      TvInputHardwareInfo localTvInputHardwareInfo = (TvInputHardwareInfo)localIterator.next();
      if ((localTvInputHardwareInfo.getType() == 9) && (localTvInputHardwareInfo.getHdmiPortId() == paramInt)) {
        return localTvInputHardwareInfo;
      }
    }
    return null;
  }
  
  private float getMediaStreamVolume()
  {
    return this.mCurrentIndex / this.mCurrentMaxIndex;
  }
  
  private void handleVolumeChange(Context paramContext, Intent paramIntent)
  {
    paramContext = paramIntent.getAction();
    if (paramContext.equals("android.media.VOLUME_CHANGED_ACTION"))
    {
      if (paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1) == 3) {}
    }
    else
    {
      if (!paramContext.equals("android.media.STREAM_MUTE_CHANGED_ACTION")) {
        break label124;
      }
      if (paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1) == 3) {
        break label74;
      }
      return;
    }
    int i = paramIntent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0);
    if (i == this.mCurrentIndex) {
      return;
    }
    this.mCurrentIndex = i;
    label74:
    paramContext = this.mLock;
    i = 0;
    try
    {
      while (i < this.mConnections.size())
      {
        paramIntent = ((Connection)this.mConnections.valueAt(i)).getHardwareImplLocked();
        if (paramIntent != null) {
          paramIntent.onMediaStreamVolumeChanged();
        }
        i += 1;
        continue;
        label124:
        Slog.w(TAG, "Unrecognized intent: " + paramIntent);
        return;
      }
      return;
    }
    finally {}
  }
  
  private static <T> int indexOfEqualValue(SparseArray<T> paramSparseArray, T paramT)
  {
    int i = 0;
    while (i < paramSparseArray.size())
    {
      if (paramSparseArray.valueAt(i).equals(paramT)) {
        return i;
      }
      i += 1;
    }
    return -1;
  }
  
  private static boolean intArrayContains(int[] paramArrayOfInt, int paramInt)
  {
    int j = paramArrayOfInt.length;
    int i = 0;
    while (i < j)
    {
      if (paramArrayOfInt[i] == paramInt) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private void processPendingHdmiDeviceEventsLocked()
  {
    Iterator localIterator = this.mPendingHdmiDeviceEvents.iterator();
    while (localIterator.hasNext())
    {
      Message localMessage = (Message)localIterator.next();
      if (findHardwareInfoForHdmiPortLocked(((HdmiDeviceInfo)localMessage.obj).getPortId()) != null)
      {
        localMessage.sendToTarget();
        localIterator.remove();
      }
    }
  }
  
  private void updateVolume()
  {
    this.mCurrentMaxIndex = this.mAudioManager.getStreamMaxVolume(3);
    this.mCurrentIndex = this.mAudioManager.getStreamVolume(3);
  }
  
  public ITvInputHardware acquireHardware(int paramInt1, ITvInputHardwareCallback paramITvInputHardwareCallback, TvInputInfo paramTvInputInfo, int paramInt2, int paramInt3)
  {
    if (paramITvInputHardwareCallback == null) {
      throw new NullPointerException();
    }
    synchronized (this.mLock)
    {
      Connection localConnection = (Connection)this.mConnections.get(paramInt1);
      if (localConnection == null)
      {
        Slog.e(TAG, "Invalid deviceId : " + paramInt1);
        return null;
      }
      TvInputHardwareImpl localTvInputHardwareImpl;
      if (checkUidChangedLocked(localConnection, paramInt2, paramInt3)) {
        localTvInputHardwareImpl = new TvInputHardwareImpl(localConnection.getHardwareInfoLocked());
      }
      try
      {
        paramITvInputHardwareCallback.asBinder().linkToDeath(localConnection, 0);
        localConnection.resetLocked(localTvInputHardwareImpl, paramITvInputHardwareCallback, paramTvInputInfo, Integer.valueOf(paramInt2), Integer.valueOf(paramInt3));
        paramITvInputHardwareCallback = localConnection.getHardwareLocked();
        return paramITvInputHardwareCallback;
      }
      catch (RemoteException paramITvInputHardwareCallback)
      {
        localTvInputHardwareImpl.release();
        return null;
      }
    }
  }
  
  public void addHardwareInput(int paramInt, TvInputInfo paramTvInputInfo)
  {
    boolean bool = true;
    for (;;)
    {
      int i;
      synchronized (this.mLock)
      {
        Object localObject2 = (String)this.mHardwareInputIdMap.get(paramInt);
        if (localObject2 != null) {
          Slog.w(TAG, "Trying to override previous registration: old = " + this.mInputMap.get(localObject2) + ":" + paramInt + ", new = " + paramTvInputInfo + ":" + paramInt);
        }
        this.mHardwareInputIdMap.put(paramInt, paramTvInputInfo.getId());
        this.mInputMap.put(paramTvInputInfo.getId(), paramTvInputInfo);
        i = 0;
        if (i < this.mHdmiStateMap.size())
        {
          localObject2 = findHardwareInfoForHdmiPortLocked(this.mHdmiStateMap.keyAt(i));
          if (localObject2 != null)
          {
            localObject2 = (String)this.mHardwareInputIdMap.get(((TvInputHardwareInfo)localObject2).getDeviceId());
            if ((localObject2 != null) && (((String)localObject2).equals(paramTvInputInfo.getId()))) {
              this.mHandler.obtainMessage(1, convertConnectedToState(this.mHdmiStateMap.valueAt(i)), 0, localObject2).sendToTarget();
            }
          }
        }
        else
        {
          localObject2 = (Connection)this.mConnections.get(paramInt);
          if (localObject2 != null)
          {
            Handler localHandler = this.mHandler;
            if (((Connection)localObject2).getConfigsLocked().length > 0) {
              localHandler.obtainMessage(1, convertConnectedToState(bool), 0, paramTvInputInfo.getId()).sendToTarget();
            }
          }
          else
          {
            return;
          }
          bool = false;
        }
      }
      i += 1;
    }
  }
  
  public void addHdmiInput(int paramInt, TvInputInfo paramTvInputInfo)
  {
    if (paramTvInputInfo.getType() != 1007) {
      throw new IllegalArgumentException("info (" + paramTvInputInfo + ") has non-HDMI type.");
    }
    synchronized (this.mLock)
    {
      str = paramTvInputInfo.getParentId();
      if (indexOfEqualValue(this.mHardwareInputIdMap, str) < 0) {
        throw new IllegalArgumentException("info (" + paramTvInputInfo + ") has invalid parentId.");
      }
    }
    String str = (String)this.mHdmiInputIdMap.get(paramInt);
    if (str != null) {
      Slog.w(TAG, "Trying to override previous registration: old = " + this.mInputMap.get(str) + ":" + paramInt + ", new = " + paramTvInputInfo + ":" + paramInt);
    }
    this.mHdmiInputIdMap.put(paramInt, paramTvInputInfo.getId());
    this.mInputMap.put(paramTvInputInfo.getId(), paramTvInputInfo);
  }
  
  public boolean captureFrame(String paramString, Surface paramSurface, final TvStreamConfig paramTvStreamConfig, int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      paramInt1 = findDeviceIdForInputIdLocked(paramString);
      if (paramInt1 < 0)
      {
        Slog.e(TAG, "Invalid inputId : " + paramString);
        return false;
      }
      paramString = (Connection)this.mConnections.get(paramInt1);
      final TvInputHardwareImpl localTvInputHardwareImpl = paramString.getHardwareImplLocked();
      if (localTvInputHardwareImpl != null)
      {
        Runnable localRunnable = paramString.getOnFirstFrameCapturedLocked();
        if (localRunnable != null)
        {
          localRunnable.run();
          paramString.setOnFirstFrameCapturedLocked(null);
        }
        boolean bool = TvInputHardwareImpl.-wrap0(localTvInputHardwareImpl, paramSurface, paramTvStreamConfig);
        if (bool) {
          paramString.setOnFirstFrameCapturedLocked(new Runnable()
          {
            public void run()
            {
              TvInputHardwareManager.TvInputHardwareImpl.-wrap1(localTvInputHardwareImpl, paramTvStreamConfig);
            }
          });
        }
        return bool;
      }
      return false;
    }
  }
  
  public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter = new IndentingPrintWriter(paramPrintWriter, "  ");
    if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0)
    {
      paramPrintWriter.println("Permission Denial: can't dump TvInputHardwareManager from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
      return;
    }
    int j;
    synchronized (this.mLock)
    {
      paramPrintWriter.println("TvInputHardwareManager Info:");
      paramPrintWriter.increaseIndent();
      paramPrintWriter.println("mConnections: deviceId -> Connection");
      paramPrintWriter.increaseIndent();
      i = 0;
      while (i < this.mConnections.size())
      {
        j = this.mConnections.keyAt(i);
        paramArrayOfString = (Connection)this.mConnections.valueAt(i);
        paramPrintWriter.println(j + ": " + paramArrayOfString);
        i += 1;
      }
      paramPrintWriter.decreaseIndent();
      paramPrintWriter.println("mHardwareList:");
      paramPrintWriter.increaseIndent();
      paramArrayOfString = this.mHardwareList.iterator();
      if (paramArrayOfString.hasNext()) {
        paramPrintWriter.println((TvInputHardwareInfo)paramArrayOfString.next());
      }
    }
    paramPrintWriter.decreaseIndent();
    paramPrintWriter.println("mHdmiDeviceList:");
    paramPrintWriter.increaseIndent();
    paramArrayOfString = this.mHdmiDeviceList.iterator();
    while (paramArrayOfString.hasNext()) {
      paramPrintWriter.println((HdmiDeviceInfo)paramArrayOfString.next());
    }
    paramPrintWriter.decreaseIndent();
    paramPrintWriter.println("mHardwareInputIdMap: deviceId -> inputId");
    paramPrintWriter.increaseIndent();
    int i = 0;
    while (i < this.mHardwareInputIdMap.size())
    {
      j = this.mHardwareInputIdMap.keyAt(i);
      paramArrayOfString = (String)this.mHardwareInputIdMap.valueAt(i);
      paramPrintWriter.println(j + ": " + paramArrayOfString);
      i += 1;
    }
    paramPrintWriter.decreaseIndent();
    paramPrintWriter.println("mHdmiInputIdMap: id -> inputId");
    paramPrintWriter.increaseIndent();
    i = 0;
    while (i < this.mHdmiInputIdMap.size())
    {
      j = this.mHdmiInputIdMap.keyAt(i);
      paramArrayOfString = (String)this.mHdmiInputIdMap.valueAt(i);
      paramPrintWriter.println(j + ": " + paramArrayOfString);
      i += 1;
    }
    paramPrintWriter.decreaseIndent();
    paramPrintWriter.println("mInputMap: inputId -> inputInfo");
    paramPrintWriter.increaseIndent();
    paramArrayOfString = this.mInputMap.entrySet().iterator();
    while (paramArrayOfString.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramArrayOfString.next();
      paramPrintWriter.println((String)localEntry.getKey() + ": " + localEntry.getValue());
    }
    paramPrintWriter.decreaseIndent();
    paramPrintWriter.decreaseIndent();
  }
  
  public List<TvStreamConfig> getAvailableTvStreamConfigList(String paramString, int paramInt1, int paramInt2)
  {
    paramInt1 = 0;
    ArrayList localArrayList = new ArrayList();
    synchronized (this.mLock)
    {
      paramInt2 = findDeviceIdForInputIdLocked(paramString);
      if (paramInt2 < 0)
      {
        Slog.e(TAG, "Invalid inputId : " + paramString);
        return localArrayList;
      }
      paramString = ((Connection)this.mConnections.get(paramInt2)).getConfigsLocked();
      paramInt2 = paramString.length;
      while (paramInt1 < paramInt2)
      {
        Object localObject2 = paramString[paramInt1];
        if (((TvStreamConfig)localObject2).getType() == 2) {
          localArrayList.add(localObject2);
        }
        paramInt1 += 1;
      }
      return localArrayList;
    }
  }
  
  public List<TvInputHardwareInfo> getHardwareList()
  {
    synchronized (this.mLock)
    {
      List localList = Collections.unmodifiableList(this.mHardwareList);
      return localList;
    }
  }
  
  public List<HdmiDeviceInfo> getHdmiDeviceList()
  {
    synchronized (this.mLock)
    {
      List localList = Collections.unmodifiableList(this.mHdmiDeviceList);
      return localList;
    }
  }
  
  public void onBootPhase(int paramInt)
  {
    Object localObject;
    if (paramInt == 500)
    {
      localObject = IHdmiControlService.Stub.asInterface(ServiceManager.getService("hdmi_control"));
      if (localObject == null) {
        break label122;
      }
    }
    for (;;)
    {
      try
      {
        ((IHdmiControlService)localObject).addHotplugEventListener(this.mHdmiHotplugEventListener);
        ((IHdmiControlService)localObject).addDeviceEventListener(this.mHdmiDeviceEventListener);
        ((IHdmiControlService)localObject).addSystemAudioModeChangeListener(this.mHdmiSystemAudioModeChangeListener);
        this.mHdmiDeviceList.addAll(((IHdmiControlService)localObject).getInputDevices());
        localObject = new IntentFilter();
        ((IntentFilter)localObject).addAction("android.media.VOLUME_CHANGED_ACTION");
        ((IntentFilter)localObject).addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
        this.mContext.registerReceiver(this.mVolumeReceiver, (IntentFilter)localObject);
        updateVolume();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w(TAG, "Error registering listeners to HdmiControlService:", localRemoteException);
        continue;
      }
      label122:
      Slog.w(TAG, "HdmiControlService is not available");
    }
  }
  
  public void onDeviceAvailable(TvInputHardwareInfo paramTvInputHardwareInfo, TvStreamConfig[] paramArrayOfTvStreamConfig)
  {
    synchronized (this.mLock)
    {
      Connection localConnection = new Connection(paramTvInputHardwareInfo);
      localConnection.updateConfigsLocked(paramArrayOfTvStreamConfig);
      this.mConnections.put(paramTvInputHardwareInfo.getDeviceId(), localConnection);
      buildHardwareListLocked();
      this.mHandler.obtainMessage(2, 0, 0, paramTvInputHardwareInfo).sendToTarget();
      if (paramTvInputHardwareInfo.getType() == 9) {
        processPendingHdmiDeviceEventsLocked();
      }
      return;
    }
  }
  
  public void onDeviceUnavailable(int paramInt)
  {
    synchronized (this.mLock)
    {
      Object localObject2 = (Connection)this.mConnections.get(paramInt);
      if (localObject2 == null)
      {
        Slog.e(TAG, "onDeviceUnavailable: Cannot find a connection with " + paramInt);
        return;
      }
      ((Connection)localObject2).resetLocked(null, null, null, null, null);
      this.mConnections.remove(paramInt);
      buildHardwareListLocked();
      localObject2 = ((Connection)localObject2).getHardwareInfoLocked();
      if (((TvInputHardwareInfo)localObject2).getType() == 9)
      {
        Iterator localIterator = this.mHdmiDeviceList.iterator();
        while (localIterator.hasNext())
        {
          HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)localIterator.next();
          if (localHdmiDeviceInfo.getPortId() == ((TvInputHardwareInfo)localObject2).getHdmiPortId())
          {
            this.mHandler.obtainMessage(5, 0, 0, localHdmiDeviceInfo).sendToTarget();
            localIterator.remove();
          }
        }
      }
    }
    this.mHandler.obtainMessage(3, 0, 0, localObject3).sendToTarget();
  }
  
  public void onFirstFrameCaptured(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      Connection localConnection = (Connection)this.mConnections.get(paramInt1);
      if (localConnection == null)
      {
        Slog.e(TAG, "FirstFrameCaptured: Cannot find a connection with " + paramInt1);
        return;
      }
      Runnable localRunnable = localConnection.getOnFirstFrameCapturedLocked();
      if (localRunnable != null)
      {
        localRunnable.run();
        localConnection.setOnFirstFrameCapturedLocked(null);
      }
      return;
    }
  }
  
  public void onStreamConfigurationChanged(int paramInt, TvStreamConfig[] paramArrayOfTvStreamConfig)
  {
    boolean bool = true;
    synchronized (this.mLock)
    {
      Object localObject2 = (Connection)this.mConnections.get(paramInt);
      if (localObject2 == null)
      {
        Slog.e(TAG, "StreamConfigurationChanged: Cannot find a connection with " + paramInt);
        return;
      }
      ((Connection)localObject2).updateConfigsLocked(paramArrayOfTvStreamConfig);
      String str = (String)this.mHardwareInputIdMap.get(paramInt);
      Handler localHandler;
      if (str != null)
      {
        localHandler = this.mHandler;
        if (paramArrayOfTvStreamConfig.length <= 0) {
          break label136;
        }
      }
      for (;;)
      {
        localHandler.obtainMessage(1, convertConnectedToState(bool), 0, str).sendToTarget();
        localObject2 = ((Connection)localObject2).getCallbackLocked();
        if (localObject2 != null) {}
        try
        {
          ((ITvInputHardwareCallback)localObject2).onStreamConfigChanged(paramArrayOfTvStreamConfig);
          return;
          label136:
          bool = false;
        }
        catch (RemoteException paramArrayOfTvStreamConfig)
        {
          for (;;)
          {
            Slog.e(TAG, "error in onStreamConfigurationChanged", paramArrayOfTvStreamConfig);
          }
        }
      }
    }
  }
  
  public void releaseHardware(int paramInt1, ITvInputHardware paramITvInputHardware, int paramInt2, int paramInt3)
  {
    synchronized (this.mLock)
    {
      Connection localConnection = (Connection)this.mConnections.get(paramInt1);
      if (localConnection == null)
      {
        Slog.e(TAG, "Invalid deviceId : " + paramInt1);
        return;
      }
      if (localConnection.getHardwareLocked() == paramITvInputHardware)
      {
        boolean bool = checkUidChangedLocked(localConnection, paramInt2, paramInt3);
        if (!bool) {}
      }
      else
      {
        return;
      }
      localConnection.resetLocked(null, null, null, null, null);
      return;
    }
  }
  
  public void removeHardwareInput(String paramString)
  {
    synchronized (this.mLock)
    {
      this.mInputMap.remove(paramString);
      int i = indexOfEqualValue(this.mHardwareInputIdMap, paramString);
      if (i >= 0) {
        this.mHardwareInputIdMap.removeAt(i);
      }
      i = indexOfEqualValue(this.mHdmiInputIdMap, paramString);
      if (i >= 0) {
        this.mHdmiInputIdMap.removeAt(i);
      }
      return;
    }
  }
  
  private class Connection
    implements IBinder.DeathRecipient
  {
    private ITvInputHardwareCallback mCallback;
    private Integer mCallingUid = null;
    private TvStreamConfig[] mConfigs = null;
    private TvInputHardwareManager.TvInputHardwareImpl mHardware = null;
    private final TvInputHardwareInfo mHardwareInfo;
    private TvInputInfo mInfo;
    private Runnable mOnFirstFrameCaptured;
    private Integer mResolvedUserId = null;
    
    public Connection(TvInputHardwareInfo paramTvInputHardwareInfo)
    {
      this.mHardwareInfo = paramTvInputHardwareInfo;
    }
    
    public void binderDied()
    {
      synchronized (TvInputHardwareManager.-get10(TvInputHardwareManager.this))
      {
        resetLocked(null, null, null, null, null);
        return;
      }
    }
    
    public ITvInputHardwareCallback getCallbackLocked()
    {
      return this.mCallback;
    }
    
    public Integer getCallingUidLocked()
    {
      return this.mCallingUid;
    }
    
    public TvStreamConfig[] getConfigsLocked()
    {
      return this.mConfigs;
    }
    
    public TvInputHardwareManager.TvInputHardwareImpl getHardwareImplLocked()
    {
      return this.mHardware;
    }
    
    public TvInputHardwareInfo getHardwareInfoLocked()
    {
      return this.mHardwareInfo;
    }
    
    public ITvInputHardware getHardwareLocked()
    {
      return this.mHardware;
    }
    
    public TvInputInfo getInfoLocked()
    {
      return this.mInfo;
    }
    
    public Runnable getOnFirstFrameCapturedLocked()
    {
      return this.mOnFirstFrameCaptured;
    }
    
    public Integer getResolvedUserIdLocked()
    {
      return this.mResolvedUserId;
    }
    
    public void resetLocked(TvInputHardwareManager.TvInputHardwareImpl paramTvInputHardwareImpl, ITvInputHardwareCallback paramITvInputHardwareCallback, TvInputInfo paramTvInputInfo, Integer paramInteger1, Integer paramInteger2)
    {
      if (this.mHardware != null) {}
      try
      {
        this.mCallback.onReleased();
        this.mHardware.release();
        this.mHardware = paramTvInputHardwareImpl;
        this.mCallback = paramITvInputHardwareCallback;
        this.mInfo = paramTvInputInfo;
        this.mCallingUid = paramInteger1;
        this.mResolvedUserId = paramInteger2;
        this.mOnFirstFrameCaptured = null;
        if ((this.mHardware == null) || (this.mCallback == null)) {}
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          try
          {
            this.mCallback.onStreamConfigChanged(getConfigsLocked());
            return;
          }
          catch (RemoteException paramTvInputHardwareImpl)
          {
            Slog.e(TvInputHardwareManager.-get0(), "error in Connection::resetLocked", paramTvInputHardwareImpl);
          }
          localRemoteException = localRemoteException;
          Slog.e(TvInputHardwareManager.-get0(), "error in Connection::resetLocked", localRemoteException);
        }
      }
    }
    
    public void setOnFirstFrameCapturedLocked(Runnable paramRunnable)
    {
      this.mOnFirstFrameCaptured = paramRunnable;
    }
    
    public String toString()
    {
      return "Connection{ mHardwareInfo: " + this.mHardwareInfo + ", mInfo: " + this.mInfo + ", mCallback: " + this.mCallback + ", mConfigs: " + Arrays.toString(this.mConfigs) + ", mCallingUid: " + this.mCallingUid + ", mResolvedUserId: " + this.mResolvedUserId + " }";
    }
    
    public void updateConfigsLocked(TvStreamConfig[] paramArrayOfTvStreamConfig)
    {
      this.mConfigs = paramArrayOfTvStreamConfig;
    }
  }
  
  private final class HdmiDeviceEventListener
    extends IHdmiDeviceEventListener.Stub
  {
    private HdmiDeviceEventListener() {}
    
    private HdmiDeviceInfo findHdmiDeviceInfo(int paramInt)
    {
      Iterator localIterator = TvInputHardwareManager.-get6(TvInputHardwareManager.this).iterator();
      while (localIterator.hasNext())
      {
        HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)localIterator.next();
        if (localHdmiDeviceInfo.getId() == paramInt) {
          return localHdmiDeviceInfo;
        }
      }
      return null;
    }
    
    /* Error */
    public void onStatusChanged(HdmiDeviceInfo paramHdmiDeviceInfo, int paramInt)
    {
      // Byte code:
      //   0: aload_1
      //   1: invokevirtual 53	android/hardware/hdmi/HdmiDeviceInfo:isSourceType	()Z
      //   4: ifne +4 -> 8
      //   7: return
      //   8: aload_0
      //   9: getfield 13	com/android/server/tv/TvInputHardwareManager$HdmiDeviceEventListener:this$0	Lcom/android/server/tv/TvInputHardwareManager;
      //   12: invokestatic 57	com/android/server/tv/TvInputHardwareManager:-get10	(Lcom/android/server/tv/TvInputHardwareManager;)Ljava/lang/Object;
      //   15: astore 5
      //   17: aload 5
      //   19: monitorenter
      //   20: iconst_0
      //   21: istore_3
      //   22: aconst_null
      //   23: astore 4
      //   25: iload_2
      //   26: tableswitch	default:+26->52, 1:+68->94, 2:+136->162, 3:+206->232
      //   52: iload_3
      //   53: istore_2
      //   54: aload_0
      //   55: getfield 13	com/android/server/tv/TvInputHardwareManager$HdmiDeviceEventListener:this$0	Lcom/android/server/tv/TvInputHardwareManager;
      //   58: invokestatic 61	com/android/server/tv/TvInputHardwareManager:-get4	(Lcom/android/server/tv/TvInputHardwareManager;)Landroid/os/Handler;
      //   61: iload_2
      //   62: iconst_0
      //   63: iconst_0
      //   64: aload 4
      //   66: invokevirtual 67	android/os/Handler:obtainMessage	(IIILjava/lang/Object;)Landroid/os/Message;
      //   69: astore 4
      //   71: aload_0
      //   72: getfield 13	com/android/server/tv/TvInputHardwareManager$HdmiDeviceEventListener:this$0	Lcom/android/server/tv/TvInputHardwareManager;
      //   75: aload_1
      //   76: invokevirtual 70	android/hardware/hdmi/HdmiDeviceInfo:getPortId	()I
      //   79: invokestatic 74	com/android/server/tv/TvInputHardwareManager:-wrap0	(Lcom/android/server/tv/TvInputHardwareManager;I)Landroid/media/tv/TvInputHardwareInfo;
      //   82: ifnull +235 -> 317
      //   85: aload 4
      //   87: invokevirtual 79	android/os/Message:sendToTarget	()V
      //   90: aload 5
      //   92: monitorexit
      //   93: return
      //   94: aload_0
      //   95: aload_1
      //   96: invokevirtual 48	android/hardware/hdmi/HdmiDeviceInfo:getId	()I
      //   99: invokespecial 81	com/android/server/tv/TvInputHardwareManager$HdmiDeviceEventListener:findHdmiDeviceInfo	(I)Landroid/hardware/hdmi/HdmiDeviceInfo;
      //   102: ifnonnull +25 -> 127
      //   105: aload_0
      //   106: getfield 13	com/android/server/tv/TvInputHardwareManager$HdmiDeviceEventListener:this$0	Lcom/android/server/tv/TvInputHardwareManager;
      //   109: invokestatic 26	com/android/server/tv/TvInputHardwareManager:-get6	(Lcom/android/server/tv/TvInputHardwareManager;)Ljava/util/List;
      //   112: aload_1
      //   113: invokeinterface 87 2 0
      //   118: pop
      //   119: iconst_4
      //   120: istore_2
      //   121: aload_1
      //   122: astore 4
      //   124: goto -70 -> 54
      //   127: invokestatic 91	com/android/server/tv/TvInputHardwareManager:-get0	()Ljava/lang/String;
      //   130: new 93	java/lang/StringBuilder
      //   133: dup
      //   134: invokespecial 94	java/lang/StringBuilder:<init>	()V
      //   137: ldc 96
      //   139: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   142: aload_1
      //   143: invokevirtual 103	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   146: ldc 105
      //   148: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   151: invokevirtual 108	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   154: invokestatic 114	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   157: pop
      //   158: aload 5
      //   160: monitorexit
      //   161: return
      //   162: aload_0
      //   163: aload_1
      //   164: invokevirtual 48	android/hardware/hdmi/HdmiDeviceInfo:getId	()I
      //   167: invokespecial 81	com/android/server/tv/TvInputHardwareManager$HdmiDeviceEventListener:findHdmiDeviceInfo	(I)Landroid/hardware/hdmi/HdmiDeviceInfo;
      //   170: astore 4
      //   172: aload_0
      //   173: getfield 13	com/android/server/tv/TvInputHardwareManager$HdmiDeviceEventListener:this$0	Lcom/android/server/tv/TvInputHardwareManager;
      //   176: invokestatic 26	com/android/server/tv/TvInputHardwareManager:-get6	(Lcom/android/server/tv/TvInputHardwareManager;)Ljava/util/List;
      //   179: aload 4
      //   181: invokeinterface 117 2 0
      //   186: ifne +38 -> 224
      //   189: invokestatic 91	com/android/server/tv/TvInputHardwareManager:-get0	()Ljava/lang/String;
      //   192: new 93	java/lang/StringBuilder
      //   195: dup
      //   196: invokespecial 94	java/lang/StringBuilder:<init>	()V
      //   199: ldc 119
      //   201: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   204: aload_1
      //   205: invokevirtual 103	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   208: ldc 105
      //   210: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   213: invokevirtual 108	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   216: invokestatic 114	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   219: pop
      //   220: aload 5
      //   222: monitorexit
      //   223: return
      //   224: iconst_5
      //   225: istore_2
      //   226: aload_1
      //   227: astore 4
      //   229: goto -175 -> 54
      //   232: aload_0
      //   233: aload_1
      //   234: invokevirtual 48	android/hardware/hdmi/HdmiDeviceInfo:getId	()I
      //   237: invokespecial 81	com/android/server/tv/TvInputHardwareManager$HdmiDeviceEventListener:findHdmiDeviceInfo	(I)Landroid/hardware/hdmi/HdmiDeviceInfo;
      //   240: astore 4
      //   242: aload_0
      //   243: getfield 13	com/android/server/tv/TvInputHardwareManager$HdmiDeviceEventListener:this$0	Lcom/android/server/tv/TvInputHardwareManager;
      //   246: invokestatic 26	com/android/server/tv/TvInputHardwareManager:-get6	(Lcom/android/server/tv/TvInputHardwareManager;)Ljava/util/List;
      //   249: aload 4
      //   251: invokeinterface 117 2 0
      //   256: ifne +38 -> 294
      //   259: invokestatic 91	com/android/server/tv/TvInputHardwareManager:-get0	()Ljava/lang/String;
      //   262: new 93	java/lang/StringBuilder
      //   265: dup
      //   266: invokespecial 94	java/lang/StringBuilder:<init>	()V
      //   269: ldc 119
      //   271: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   274: aload_1
      //   275: invokevirtual 103	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
      //   278: ldc 105
      //   280: invokevirtual 100	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   283: invokevirtual 108	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   286: invokestatic 114	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   289: pop
      //   290: aload 5
      //   292: monitorexit
      //   293: return
      //   294: aload_0
      //   295: getfield 13	com/android/server/tv/TvInputHardwareManager$HdmiDeviceEventListener:this$0	Lcom/android/server/tv/TvInputHardwareManager;
      //   298: invokestatic 26	com/android/server/tv/TvInputHardwareManager:-get6	(Lcom/android/server/tv/TvInputHardwareManager;)Ljava/util/List;
      //   301: aload_1
      //   302: invokeinterface 87 2 0
      //   307: pop
      //   308: bipush 6
      //   310: istore_2
      //   311: aload_1
      //   312: astore 4
      //   314: goto -260 -> 54
      //   317: aload_0
      //   318: getfield 13	com/android/server/tv/TvInputHardwareManager$HdmiDeviceEventListener:this$0	Lcom/android/server/tv/TvInputHardwareManager;
      //   321: invokestatic 122	com/android/server/tv/TvInputHardwareManager:-get11	(Lcom/android/server/tv/TvInputHardwareManager;)Ljava/util/List;
      //   324: aload 4
      //   326: invokeinterface 87 2 0
      //   331: pop
      //   332: goto -242 -> 90
      //   335: astore_1
      //   336: aload 5
      //   338: monitorexit
      //   339: aload_1
      //   340: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	341	0	this	HdmiDeviceEventListener
      //   0	341	1	paramHdmiDeviceInfo	HdmiDeviceInfo
      //   0	341	2	paramInt	int
      //   21	32	3	i	int
      //   23	302	4	localObject1	Object
      //   15	322	5	localObject2	Object
      // Exception table:
      //   from	to	target	type
      //   54	90	335	finally
      //   94	119	335	finally
      //   127	158	335	finally
      //   162	220	335	finally
      //   232	290	335	finally
      //   294	308	335	finally
      //   317	332	335	finally
    }
  }
  
  private final class HdmiHotplugEventListener
    extends IHdmiHotplugEventListener.Stub
  {
    private HdmiHotplugEventListener() {}
    
    public void onReceived(HdmiHotplugEvent paramHdmiHotplugEvent)
    {
      synchronized (TvInputHardwareManager.-get10(TvInputHardwareManager.this))
      {
        TvInputHardwareManager.-get8(TvInputHardwareManager.this).put(paramHdmiHotplugEvent.getPort(), paramHdmiHotplugEvent.isConnected());
        Object localObject2 = TvInputHardwareManager.-wrap0(TvInputHardwareManager.this, paramHdmiHotplugEvent.getPort());
        if (localObject2 == null) {
          return;
        }
        localObject2 = (String)TvInputHardwareManager.-get5(TvInputHardwareManager.this).get(((TvInputHardwareInfo)localObject2).getDeviceId());
        if (localObject2 == null) {
          return;
        }
        TvInputHardwareManager.-get4(TvInputHardwareManager.this).obtainMessage(1, TvInputHardwareManager.-wrap3(TvInputHardwareManager.this, paramHdmiHotplugEvent.isConnected()), 0, localObject2).sendToTarget();
        return;
      }
    }
  }
  
  private final class HdmiSystemAudioModeChangeListener
    extends IHdmiSystemAudioModeChangeListener.Stub
  {
    private HdmiSystemAudioModeChangeListener() {}
    
    public void onStatusChanged(boolean paramBoolean)
      throws RemoteException
    {
      Object localObject1 = TvInputHardwareManager.-get10(TvInputHardwareManager.this);
      int i = 0;
      try
      {
        while (i < TvInputHardwareManager.-get2(TvInputHardwareManager.this).size())
        {
          TvInputHardwareManager.TvInputHardwareImpl localTvInputHardwareImpl = ((TvInputHardwareManager.Connection)TvInputHardwareManager.-get2(TvInputHardwareManager.this).valueAt(i)).getHardwareImplLocked();
          if (localTvInputHardwareImpl != null) {
            TvInputHardwareManager.TvInputHardwareImpl.-wrap2(localTvInputHardwareImpl);
          }
          i += 1;
        }
        return;
      }
      finally {}
    }
  }
  
  static abstract interface Listener
  {
    public abstract void onHardwareDeviceAdded(TvInputHardwareInfo paramTvInputHardwareInfo);
    
    public abstract void onHardwareDeviceRemoved(TvInputHardwareInfo paramTvInputHardwareInfo);
    
    public abstract void onHdmiDeviceAdded(HdmiDeviceInfo paramHdmiDeviceInfo);
    
    public abstract void onHdmiDeviceRemoved(HdmiDeviceInfo paramHdmiDeviceInfo);
    
    public abstract void onHdmiDeviceUpdated(String paramString, HdmiDeviceInfo paramHdmiDeviceInfo);
    
    public abstract void onStateChanged(String paramString, int paramInt);
  }
  
  private class ListenerHandler
    extends Handler
  {
    private static final int HARDWARE_DEVICE_ADDED = 2;
    private static final int HARDWARE_DEVICE_REMOVED = 3;
    private static final int HDMI_DEVICE_ADDED = 4;
    private static final int HDMI_DEVICE_REMOVED = 5;
    private static final int HDMI_DEVICE_UPDATED = 6;
    private static final int STATE_CHANGED = 1;
    
    private ListenerHandler() {}
    
    public final void handleMessage(Message arg1)
    {
      switch (???.what)
      {
      default: 
        Slog.w(TvInputHardwareManager.-get0(), "Unhandled message: " + ???);
        return;
      case 1: 
        localObject1 = (String)???.obj;
        int i = ???.arg1;
        TvInputHardwareManager.-get9(TvInputHardwareManager.this).onStateChanged((String)localObject1, i);
        return;
      case 2: 
        ??? = (TvInputHardwareInfo)???.obj;
        TvInputHardwareManager.-get9(TvInputHardwareManager.this).onHardwareDeviceAdded(???);
        return;
      case 3: 
        ??? = (TvInputHardwareInfo)???.obj;
        TvInputHardwareManager.-get9(TvInputHardwareManager.this).onHardwareDeviceRemoved(???);
        return;
      case 4: 
        ??? = (HdmiDeviceInfo)???.obj;
        TvInputHardwareManager.-get9(TvInputHardwareManager.this).onHdmiDeviceAdded(???);
        return;
      case 5: 
        ??? = (HdmiDeviceInfo)???.obj;
        TvInputHardwareManager.-get9(TvInputHardwareManager.this).onHdmiDeviceRemoved(???);
        return;
      }
      Object localObject1 = (HdmiDeviceInfo)???.obj;
      synchronized (TvInputHardwareManager.-get10(TvInputHardwareManager.this))
      {
        String str = (String)TvInputHardwareManager.-get7(TvInputHardwareManager.this).get(((HdmiDeviceInfo)localObject1).getId());
        if (str != null)
        {
          TvInputHardwareManager.-get9(TvInputHardwareManager.this).onHdmiDeviceUpdated(str, (HdmiDeviceInfo)localObject1);
          return;
        }
      }
      Slog.w(TvInputHardwareManager.-get0(), "Could not resolve input ID matching the device info; ignoring.");
    }
  }
  
  private class TvInputHardwareImpl
    extends ITvInputHardware.Stub
  {
    private TvStreamConfig mActiveConfig = null;
    private final AudioManager.OnAudioPortUpdateListener mAudioListener = new AudioManager.OnAudioPortUpdateListener()
    {
      public void onAudioPatchListUpdate(AudioPatch[] paramAnonymousArrayOfAudioPatch) {}
      
      public void onAudioPortListUpdate(AudioPort[] arg1)
      {
        synchronized (TvInputHardwareManager.TvInputHardwareImpl.-get2(TvInputHardwareManager.TvInputHardwareImpl.this))
        {
          TvInputHardwareManager.TvInputHardwareImpl.-wrap3(TvInputHardwareManager.TvInputHardwareImpl.this);
          return;
        }
      }
      
      public void onServiceDied()
      {
        synchronized (TvInputHardwareManager.TvInputHardwareImpl.-get2(TvInputHardwareManager.TvInputHardwareImpl.this))
        {
          TvInputHardwareManager.TvInputHardwareImpl.-set1(TvInputHardwareManager.TvInputHardwareImpl.this, null);
          TvInputHardwareManager.TvInputHardwareImpl.-get1(TvInputHardwareManager.TvInputHardwareImpl.this).clear();
          if (TvInputHardwareManager.TvInputHardwareImpl.-get0(TvInputHardwareManager.TvInputHardwareImpl.this) != null)
          {
            TvInputHardwareManager.-get1(TvInputHardwareManager.this);
            AudioManager.releaseAudioPatch(TvInputHardwareManager.TvInputHardwareImpl.-get0(TvInputHardwareManager.TvInputHardwareImpl.this));
            TvInputHardwareManager.TvInputHardwareImpl.-set0(TvInputHardwareManager.TvInputHardwareImpl.this, null);
          }
          return;
        }
      }
    };
    private AudioPatch mAudioPatch = null;
    private List<AudioDevicePort> mAudioSink = new ArrayList();
    private AudioDevicePort mAudioSource;
    private float mCommittedVolume = -1.0F;
    private int mDesiredChannelMask = 1;
    private int mDesiredFormat = 1;
    private int mDesiredSamplingRate = 0;
    private final Object mImplLock = new Object();
    private final TvInputHardwareInfo mInfo;
    private String mOverrideAudioAddress = "";
    private int mOverrideAudioType = 0;
    private boolean mReleased = false;
    private float mSourceVolume = 0.0F;
    
    public TvInputHardwareImpl(TvInputHardwareInfo paramTvInputHardwareInfo)
    {
      this.mInfo = paramTvInputHardwareInfo;
      TvInputHardwareManager.-get1(TvInputHardwareManager.this).registerAudioPortUpdateListener(this.mAudioListener);
      if (this.mInfo.getAudioType() != 0)
      {
        this.mAudioSource = findAudioDevicePort(this.mInfo.getAudioType(), this.mInfo.getAudioAddress());
        findAudioSinkFromAudioPolicy(this.mAudioSink);
      }
    }
    
    private AudioDevicePort findAudioDevicePort(int paramInt, String paramString)
    {
      if (paramInt == 0) {
        return null;
      }
      Object localObject = new ArrayList();
      TvInputHardwareManager.-get1(TvInputHardwareManager.this);
      if (AudioManager.listAudioDevicePorts((ArrayList)localObject) != 0) {
        return null;
      }
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        AudioDevicePort localAudioDevicePort = (AudioDevicePort)((Iterator)localObject).next();
        if ((localAudioDevicePort.type() == paramInt) && (localAudioDevicePort.address().equals(paramString))) {
          return localAudioDevicePort;
        }
      }
      return null;
    }
    
    private void findAudioSinkFromAudioPolicy(List<AudioDevicePort> paramList)
    {
      paramList.clear();
      Object localObject = new ArrayList();
      TvInputHardwareManager.-get1(TvInputHardwareManager.this);
      if (AudioManager.listAudioDevicePorts((ArrayList)localObject) != 0) {
        return;
      }
      int i = TvInputHardwareManager.-get1(TvInputHardwareManager.this).getDevicesForStream(3);
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        AudioDevicePort localAudioDevicePort = (AudioDevicePort)((Iterator)localObject).next();
        if (((localAudioDevicePort.type() & i) != 0) && ((localAudioDevicePort.type() & 0x80000000) == 0)) {
          paramList.add(localAudioDevicePort);
        }
      }
    }
    
    private void handleAudioSinkUpdated()
    {
      synchronized (this.mImplLock)
      {
        updateAudioConfigLocked();
        return;
      }
    }
    
    private boolean startCapture(Surface paramSurface, TvStreamConfig paramTvStreamConfig)
    {
      boolean bool1 = false;
      synchronized (this.mImplLock)
      {
        boolean bool2 = this.mReleased;
        if (bool2) {
          return false;
        }
        if ((paramSurface == null) || (paramTvStreamConfig == null)) {
          return false;
        }
        int i = paramTvStreamConfig.getType();
        if (i != 2) {
          return false;
        }
        i = TvInputHardwareManager.-get3(TvInputHardwareManager.this).addOrUpdateStream(this.mInfo.getDeviceId(), paramSurface, paramTvStreamConfig);
        if (i == 0) {
          bool1 = true;
        }
        return bool1;
      }
    }
    
    private boolean stopCapture(TvStreamConfig paramTvStreamConfig)
    {
      boolean bool1 = false;
      synchronized (this.mImplLock)
      {
        boolean bool2 = this.mReleased;
        if (bool2) {
          return false;
        }
        if (paramTvStreamConfig == null) {
          return false;
        }
        int i = TvInputHardwareManager.-get3(TvInputHardwareManager.this).removeStream(this.mInfo.getDeviceId(), paramTvStreamConfig);
        if (i == 0) {
          bool1 = true;
        }
        return bool1;
      }
    }
    
    private void updateAudioConfigLocked()
    {
      boolean bool1 = updateAudioSinkLocked();
      boolean bool2 = updateAudioSourceLocked();
      if ((this.mAudioSource == null) || (this.mAudioSink.isEmpty()) || (this.mActiveConfig == null))
      {
        if (this.mAudioPatch != null)
        {
          TvInputHardwareManager.-get1(TvInputHardwareManager.this);
          AudioManager.releaseAudioPatch(this.mAudioPatch);
          this.mAudioPatch = null;
        }
        return;
      }
      TvInputHardwareManager.-wrap5(TvInputHardwareManager.this);
      float f = this.mSourceVolume * TvInputHardwareManager.-wrap2(TvInputHardwareManager.this);
      Object localObject2 = null;
      Object localObject1 = localObject2;
      int i;
      int j;
      if (this.mAudioSource.gains().length > 0)
      {
        localObject1 = localObject2;
        if (f != this.mCommittedVolume)
        {
          localObject3 = null;
          localObject4 = this.mAudioSource.gains();
          i = 0;
          j = localObject4.length;
          localObject1 = localObject3;
          if (i < j)
          {
            localObject1 = localObject4[i];
            if ((((AudioGain)localObject1).mode() & 0x1) == 0) {
              break label505;
            }
          }
          if (localObject1 == null) {
            break label521;
          }
          i = (((AudioGain)localObject1).maxValue() - ((AudioGain)localObject1).minValue()) / ((AudioGain)localObject1).stepValue();
          j = ((AudioGain)localObject1).minValue();
          if (f >= 1.0F) {
            break label512;
          }
          i = j + ((AudioGain)localObject1).stepValue() * (int)(i * f + 0.5D);
          label215:
          localObject1 = ((AudioGain)localObject1).buildConfig(1, ((AudioGain)localObject1).channelMask(), new int[] { i }, 0);
        }
      }
      label236:
      Object localObject3 = this.mAudioSource.activeConfig();
      ArrayList localArrayList = new ArrayList();
      AudioPatch[] arrayOfAudioPatch = new AudioPatch[1];
      arrayOfAudioPatch[0] = this.mAudioPatch;
      label273:
      label284:
      AudioDevicePort localAudioDevicePort;
      int n;
      int m;
      int i1;
      int k;
      if (!bool2)
      {
        Iterator localIterator = this.mAudioSink.iterator();
        if (!localIterator.hasNext()) {
          break label570;
        }
        localAudioDevicePort = (AudioDevicePort)localIterator.next();
        localObject4 = localAudioDevicePort.activeConfig();
        n = this.mDesiredSamplingRate;
        m = this.mDesiredChannelMask;
        i1 = this.mDesiredFormat;
        i = m;
        k = n;
        if (localObject4 != null)
        {
          j = n;
          if (n == 0) {
            j = ((AudioPortConfig)localObject4).samplingRate();
          }
          i = m;
          if (m == 1) {
            i = ((AudioPortConfig)localObject4).channelMask();
          }
          k = j;
          if (i1 == 1)
          {
            i = ((AudioPortConfig)localObject4).format();
            k = j;
          }
        }
        if ((localObject4 != null) && (((AudioPortConfig)localObject4).samplingRate() == k)) {
          break label544;
        }
      }
      for (;;)
      {
        label405:
        j = k;
        if (!TvInputHardwareManager.-wrap1(localAudioDevicePort.samplingRates(), k))
        {
          j = k;
          if (localAudioDevicePort.samplingRates().length > 0) {
            j = localAudioDevicePort.samplingRates()[0];
          }
        }
        k = i;
        if (!TvInputHardwareManager.-wrap1(localAudioDevicePort.channelMasks(), i)) {
          k = 1;
        }
        i = i1;
        if (!TvInputHardwareManager.-wrap1(localAudioDevicePort.formats(), i1)) {
          i = 1;
        }
        localObject2 = localAudioDevicePort.buildConfig(j, k, i, null);
        bool1 = true;
        label505:
        label512:
        label521:
        label544:
        do
        {
          localArrayList.add(localObject2);
          break label284;
          i += 1;
          break;
          i = ((AudioGain)localObject1).maxValue();
          break label215;
          Slog.w(TvInputHardwareManager.-get0(), "No audio source gain with MODE_JOINT support exists.");
          localObject1 = localObject2;
          break label236;
          bool1 = true;
          break label273;
          if (((AudioPortConfig)localObject4).channelMask() != i) {
            break label405;
          }
          localObject2 = localObject4;
        } while (((AudioPortConfig)localObject4).format() == i1);
      }
      label570:
      Object localObject4 = (AudioPortConfig)localArrayList.get(0);
      if (localObject3 != null)
      {
        localObject2 = localObject3;
        if (localObject1 == null) {}
      }
      else
      {
        i = 0;
        if (!TvInputHardwareManager.-wrap1(this.mAudioSource.samplingRates(), ((AudioPortConfig)localObject4).samplingRate())) {
          break label832;
        }
        i = ((AudioPortConfig)localObject4).samplingRate();
        m = 1;
        localObject2 = this.mAudioSource.channelMasks();
        k = 0;
        n = localObject2.length;
      }
      for (;;)
      {
        j = m;
        if (k < n)
        {
          j = localObject2[k];
          if (AudioFormat.channelCountFromOutChannelMask(((AudioPortConfig)localObject4).channelMask()) != AudioFormat.channelCountFromInChannelMask(j)) {}
        }
        else
        {
          k = 1;
          if (TvInputHardwareManager.-wrap1(this.mAudioSource.formats(), ((AudioPortConfig)localObject4).format())) {
            k = ((AudioPortConfig)localObject4).format();
          }
          localObject2 = this.mAudioSource.buildConfig(i, j, k, (AudioGainConfig)localObject1);
          bool1 = true;
          if (bool1)
          {
            this.mCommittedVolume = f;
            if (this.mAudioPatch != null)
            {
              TvInputHardwareManager.-get1(TvInputHardwareManager.this);
              AudioManager.releaseAudioPatch(this.mAudioPatch);
            }
            TvInputHardwareManager.-get1(TvInputHardwareManager.this);
            localObject3 = (AudioPortConfig[])localArrayList.toArray(new AudioPortConfig[localArrayList.size()]);
            AudioManager.createAudioPatch(arrayOfAudioPatch, new AudioPortConfig[] { localObject2 }, (AudioPortConfig[])localObject3);
            this.mAudioPatch = arrayOfAudioPatch[0];
            if (localObject1 != null)
            {
              TvInputHardwareManager.-get1(TvInputHardwareManager.this);
              AudioManager.setAudioPortGain(this.mAudioSource, (AudioGainConfig)localObject1);
            }
          }
          return;
          label832:
          if (this.mAudioSource.samplingRates().length <= 0) {
            break;
          }
          i = this.mAudioSource.samplingRates()[0];
          break;
        }
        k += 1;
      }
    }
    
    private boolean updateAudioSinkLocked()
    {
      if (this.mInfo.getAudioType() == 0) {
        return false;
      }
      List localList = this.mAudioSink;
      this.mAudioSink = new ArrayList();
      if (this.mOverrideAudioType == 0) {
        findAudioSinkFromAudioPolicy(this.mAudioSink);
      }
      while (this.mAudioSink.size() != localList.size())
      {
        return true;
        AudioDevicePort localAudioDevicePort = findAudioDevicePort(this.mOverrideAudioType, this.mOverrideAudioAddress);
        if (localAudioDevicePort != null) {
          this.mAudioSink.add(localAudioDevicePort);
        }
      }
      localList.removeAll(this.mAudioSink);
      return !localList.isEmpty();
    }
    
    private boolean updateAudioSourceLocked()
    {
      if (this.mInfo.getAudioType() == 0) {
        return false;
      }
      AudioDevicePort localAudioDevicePort = this.mAudioSource;
      this.mAudioSource = findAudioDevicePort(this.mInfo.getAudioType(), this.mInfo.getAudioAddress());
      if (this.mAudioSource == null) {
        if (localAudioDevicePort == null) {}
      }
      while (!this.mAudioSource.equals(localAudioDevicePort))
      {
        return true;
        return false;
      }
      return false;
    }
    
    public boolean dispatchKeyEventToHdmi(KeyEvent arg1)
      throws RemoteException
    {
      synchronized (this.mImplLock)
      {
        if (this.mReleased) {
          throw new IllegalStateException("Device already released.");
        }
      }
      return this.mInfo.getType() == 9;
    }
    
    public void onMediaStreamVolumeChanged()
    {
      synchronized (this.mImplLock)
      {
        updateAudioConfigLocked();
        return;
      }
    }
    
    public void overrideAudioSink(int paramInt1, String paramString, int paramInt2, int paramInt3, int paramInt4)
    {
      synchronized (this.mImplLock)
      {
        this.mOverrideAudioType = paramInt1;
        this.mOverrideAudioAddress = paramString;
        this.mDesiredSamplingRate = paramInt2;
        this.mDesiredChannelMask = paramInt3;
        this.mDesiredFormat = paramInt4;
        updateAudioConfigLocked();
        return;
      }
    }
    
    public void release()
    {
      synchronized (this.mImplLock)
      {
        TvInputHardwareManager.-get1(TvInputHardwareManager.this).unregisterAudioPortUpdateListener(this.mAudioListener);
        if (this.mAudioPatch != null)
        {
          TvInputHardwareManager.-get1(TvInputHardwareManager.this);
          AudioManager.releaseAudioPatch(this.mAudioPatch);
          this.mAudioPatch = null;
        }
        this.mReleased = true;
        return;
      }
    }
    
    public void setStreamVolume(float paramFloat)
      throws RemoteException
    {
      synchronized (this.mImplLock)
      {
        if (this.mReleased) {
          throw new IllegalStateException("Device already released.");
        }
      }
      this.mSourceVolume = paramFloat;
      updateAudioConfigLocked();
    }
    
    public boolean setSurface(Surface paramSurface, TvStreamConfig paramTvStreamConfig)
      throws RemoteException
    {
      boolean bool = true;
      synchronized (this.mImplLock)
      {
        if (this.mReleased) {
          throw new IllegalStateException("Device already released.");
        }
      }
      int j = 0;
      if (paramSurface == null) {
        if (this.mActiveConfig != null)
        {
          j = TvInputHardwareManager.-get3(TvInputHardwareManager.this).removeStream(this.mInfo.getDeviceId(), this.mActiveConfig);
          this.mActiveConfig = null;
          updateAudioConfigLocked();
          if (j != 0) {
            break label218;
          }
        }
      }
      for (;;)
      {
        return bool;
        return true;
        if (paramTvStreamConfig == null) {
          return false;
        }
        int i = j;
        if (this.mActiveConfig != null)
        {
          if (!paramTvStreamConfig.equals(this.mActiveConfig)) {
            break label176;
          }
          i = j;
        }
        for (;;)
        {
          j = i;
          if (i != 0) {
            break;
          }
          i = TvInputHardwareManager.-get3(TvInputHardwareManager.this).addOrUpdateStream(this.mInfo.getDeviceId(), paramSurface, paramTvStreamConfig);
          j = i;
          if (i != 0) {
            break;
          }
          this.mActiveConfig = paramTvStreamConfig;
          j = i;
          break;
          label176:
          j = TvInputHardwareManager.-get3(TvInputHardwareManager.this).removeStream(this.mInfo.getDeviceId(), this.mActiveConfig);
          i = j;
          if (j != 0)
          {
            this.mActiveConfig = null;
            i = j;
          }
        }
        label218:
        bool = false;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/tv/TvInputHardwareManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */