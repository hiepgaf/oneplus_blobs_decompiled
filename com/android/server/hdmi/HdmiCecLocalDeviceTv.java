package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiPortInfo;
import android.hardware.hdmi.HdmiRecordSources;
import android.hardware.hdmi.HdmiTimerRecordSources;
import android.hardware.hdmi.IHdmiControlCallback;
import android.media.AudioManager;
import android.media.tv.TvInputInfo;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputManager.TvInputCallback;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

final class HdmiCecLocalDeviceTv
  extends HdmiCecLocalDevice
{
  private static final String TAG = "HdmiCecLocalDeviceTv";
  @HdmiAnnotations.ServiceThreadOnly
  private boolean mArcEstablished = false;
  private final SparseBooleanArray mArcFeatureEnabled = new SparseBooleanArray();
  private boolean mAutoDeviceOff = this.mService.readBooleanSetting("hdmi_control_auto_device_off_enabled", true);
  private boolean mAutoWakeup = this.mService.readBooleanSetting("hdmi_control_auto_wakeup_enabled", true);
  private final ArraySet<Integer> mCecSwitches = new ArraySet();
  private final DelayedMessageBuffer mDelayedMessageBuffer = new DelayedMessageBuffer(this);
  private final SparseArray<HdmiDeviceInfo> mDeviceInfos = new SparseArray();
  private List<Integer> mLocalDeviceAddresses;
  @GuardedBy("mLock")
  private int mPrevPortId = -1;
  @GuardedBy("mLock")
  private List<HdmiDeviceInfo> mSafeAllDeviceInfos = Collections.emptyList();
  @GuardedBy("mLock")
  private List<HdmiDeviceInfo> mSafeExternalInputs = Collections.emptyList();
  private SelectRequestBuffer mSelectRequestBuffer;
  private boolean mSkipRoutingControl;
  private final HdmiCecStandbyModeHandler mStandbyHandler = new HdmiCecStandbyModeHandler(paramHdmiControlService, this);
  @GuardedBy("mLock")
  private boolean mSystemAudioActivated = false;
  @GuardedBy("mLock")
  private boolean mSystemAudioMute = false;
  @GuardedBy("mLock")
  private int mSystemAudioVolume = -1;
  private final TvInputManager.TvInputCallback mTvInputCallback = new TvInputManager.TvInputCallback()
  {
    public void onInputAdded(String paramAnonymousString)
    {
      Object localObject = HdmiCecLocalDeviceTv.this.mService.getTvInputManager().getTvInputInfo(paramAnonymousString);
      if (localObject == null) {
        return;
      }
      localObject = ((TvInputInfo)localObject).getHdmiDeviceInfo();
      if (localObject == null) {
        return;
      }
      HdmiCecLocalDeviceTv.-wrap0(HdmiCecLocalDeviceTv.this, paramAnonymousString, ((HdmiDeviceInfo)localObject).getId());
      if (((HdmiDeviceInfo)localObject).isCecDevice()) {
        HdmiCecLocalDeviceTv.this.processDelayedActiveSource(((HdmiDeviceInfo)localObject).getLogicalAddress());
      }
    }
    
    public void onInputRemoved(String paramAnonymousString)
    {
      HdmiCecLocalDeviceTv.-wrap1(HdmiCecLocalDeviceTv.this, paramAnonymousString);
    }
  };
  private final HashMap<String, Integer> mTvInputs = new HashMap();
  
  HdmiCecLocalDeviceTv(HdmiControlService paramHdmiControlService)
  {
    super(paramHdmiControlService, 0);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private HdmiDeviceInfo addDeviceInfo(HdmiDeviceInfo paramHdmiDeviceInfo)
  {
    assertRunOnServiceThread();
    HdmiDeviceInfo localHdmiDeviceInfo = getCecDeviceInfo(paramHdmiDeviceInfo.getLogicalAddress());
    if (localHdmiDeviceInfo != null) {
      removeDeviceInfo(paramHdmiDeviceInfo.getId());
    }
    this.mDeviceInfos.append(paramHdmiDeviceInfo.getId(), paramHdmiDeviceInfo);
    updateSafeDeviceInfoList();
    return localHdmiDeviceInfo;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void addTvInput(String paramString, int paramInt)
  {
    assertRunOnServiceThread();
    this.mTvInputs.put(paramString, Integer.valueOf(paramInt));
  }
  
  private boolean canStartArcUpdateAction(int paramInt, boolean paramBoolean)
  {
    HdmiDeviceInfo localHdmiDeviceInfo = getAvrDeviceInfo();
    if ((localHdmiDeviceInfo != null) && (paramInt == localHdmiDeviceInfo.getLogicalAddress()) && (isConnectedToArcPort(localHdmiDeviceInfo.getPhysicalAddress())) && (isDirectConnectAddress(localHdmiDeviceInfo.getPhysicalAddress())))
    {
      if (paramBoolean) {
        return isArcFeatureEnabled(localHdmiDeviceInfo.getPortId());
      }
      return true;
    }
    return false;
  }
  
  private boolean checkRecordSource(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null) {
      return HdmiRecordSources.checkRecordSource(paramArrayOfByte);
    }
    return false;
  }
  
  private boolean checkRecorder(int paramInt)
  {
    if (getCecDeviceInfo(paramInt) != null) {
      return HdmiUtils.getTypeFromAddress(paramInt) == 1;
    }
    return false;
  }
  
  private boolean checkTimerRecordingSource(int paramInt, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null) {
      return HdmiTimerRecordSources.checkTimerRecordSource(paramInt, paramArrayOfByte);
    }
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void clearDeviceInfoList()
  {
    assertRunOnServiceThread();
    Iterator localIterator = this.mSafeExternalInputs.iterator();
    while (localIterator.hasNext()) {
      invokeDeviceEventListener((HdmiDeviceInfo)localIterator.next(), 2);
    }
    this.mDeviceInfos.clear();
    updateSafeDeviceInfoList();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void disableArcIfExist()
  {
    assertRunOnServiceThread();
    HdmiDeviceInfo localHdmiDeviceInfo = getAvrDeviceInfo();
    if (localHdmiDeviceInfo == null) {
      return;
    }
    removeAction(RequestArcInitiationAction.class);
    if ((!hasAction(RequestArcTerminationAction.class)) && (isArcEstablished())) {
      addAndStartAction(new RequestArcTerminationAction(this, localHdmiDeviceInfo.getLogicalAddress()));
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void disableSystemAudioIfExist()
  {
    assertRunOnServiceThread();
    if (getAvrDeviceInfo() == null) {
      return;
    }
    removeAction(SystemAudioActionFromAvr.class);
    removeAction(SystemAudioActionFromTv.class);
    removeAction(SystemAudioAutoInitiationAction.class);
    removeAction(SystemAudioStatusAction.class);
    removeAction(VolumeControlAction.class);
  }
  
  private int findKeyReceiverAddress()
  {
    if (getActiveSource().isValid()) {
      return getActiveSource().logicalAddress;
    }
    HdmiDeviceInfo localHdmiDeviceInfo = getDeviceInfoByPath(getActivePath());
    if (localHdmiDeviceInfo != null) {
      return localHdmiDeviceInfo.getLogicalAddress();
    }
    return -1;
  }
  
  private List<HdmiDeviceInfo> getInputDevices()
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    if (i < this.mDeviceInfos.size())
    {
      HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)this.mDeviceInfos.valueAt(i);
      if (isLocalDeviceAddress(localHdmiDeviceInfo.getLogicalAddress())) {}
      for (;;)
      {
        i += 1;
        break;
        if ((localHdmiDeviceInfo.isSourceType()) && (!hideDevicesBehindLegacySwitch(localHdmiDeviceInfo))) {
          localArrayList.add(localHdmiDeviceInfo);
        }
      }
    }
    return localArrayList;
  }
  
  private boolean handleNewDeviceAtTheTailOfActivePath(int paramInt)
  {
    if (isTailOfActivePath(paramInt, getActivePath()))
    {
      paramInt = this.mService.portIdToPath(getActivePortId());
      setActivePath(paramInt);
      startRoutingControl(getActivePath(), paramInt, false, null);
      return true;
    }
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void handleSelectInternalSource()
  {
    assertRunOnServiceThread();
    if ((this.mService.isControlEnabled()) && (this.mActiveSource.logicalAddress != this.mAddress))
    {
      updateActiveSource(this.mAddress, this.mService.getPhysicalAddress());
      if (this.mSkipRoutingControl)
      {
        this.mSkipRoutingControl = false;
        return;
      }
      HdmiCecMessage localHdmiCecMessage = HdmiCecMessageBuilder.buildActiveSource(this.mAddress, this.mService.getPhysicalAddress());
      this.mService.sendCecCommand(localHdmiCecMessage);
    }
  }
  
  private boolean hideDevicesBehindLegacySwitch(HdmiDeviceInfo paramHdmiDeviceInfo)
  {
    return !isConnectedToCecSwitch(paramHdmiDeviceInfo.getPhysicalAddress(), this.mCecSwitches);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private List<Integer> initLocalDeviceAddresses()
  {
    assertRunOnServiceThread();
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mService.getAllLocalDevices().iterator();
    while (localIterator.hasNext()) {
      localArrayList.add(Integer.valueOf(((HdmiCecLocalDevice)localIterator.next()).getDeviceInfo().getLogicalAddress()));
    }
    return Collections.unmodifiableList(localArrayList);
  }
  
  private static void invokeCallback(IHdmiControlCallback paramIHdmiControlCallback, int paramInt)
  {
    if (paramIHdmiControlCallback == null) {
      return;
    }
    try
    {
      paramIHdmiControlCallback.onComplete(paramInt);
      return;
    }
    catch (RemoteException paramIHdmiControlCallback)
    {
      Slog.e("HdmiCecLocalDeviceTv", "Invoking callback failed:" + paramIHdmiControlCallback);
    }
  }
  
  private void invokeDeviceEventListener(HdmiDeviceInfo paramHdmiDeviceInfo, int paramInt)
  {
    if (!hideDevicesBehindLegacySwitch(paramHdmiDeviceInfo)) {
      this.mService.invokeDeviceEventListeners(paramHdmiDeviceInfo, paramInt);
    }
  }
  
  private static boolean isConnectedToCecSwitch(int paramInt, Collection<Integer> paramCollection)
  {
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext()) {
      if (isParentPath(((Integer)paramCollection.next()).intValue(), paramInt)) {
        return true;
      }
    }
    return false;
  }
  
  private boolean isDirectConnectAddress(int paramInt)
  {
    return (0xF000 & paramInt) == paramInt;
  }
  
  private boolean isLocalDeviceAddress(int paramInt)
  {
    return this.mLocalDeviceAddresses.contains(Integer.valueOf(paramInt));
  }
  
  private boolean isMessageForSystemAudio(HdmiCecMessage paramHdmiCecMessage)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mService.isControlEnabled())
    {
      bool1 = bool2;
      if (paramHdmiCecMessage.getSource() == 5) {
        if (paramHdmiCecMessage.getDestination() != 0)
        {
          bool1 = bool2;
          if (paramHdmiCecMessage.getDestination() != 15) {}
        }
        else
        {
          bool1 = bool2;
          if (getAvrDeviceInfo() != null) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  private static boolean isParentPath(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    int i = 0;
    while (i <= 12)
    {
      if ((paramInt2 >> i & 0xF) != 0)
      {
        boolean bool1 = bool2;
        if ((paramInt1 >> i & 0xF) == 0)
        {
          bool1 = bool2;
          if (paramInt2 >> i + 4 == paramInt1 >> i + 4) {
            bool1 = true;
          }
        }
        return bool1;
      }
      i += 4;
    }
    return false;
  }
  
  static boolean isTailOfActivePath(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0) {
      return false;
    }
    int i = 12;
    while (i >= 0)
    {
      int j = paramInt2 >> i & 0xF;
      if (j == 0) {
        return true;
      }
      if ((paramInt1 >> i & 0xF) != j) {
        return false;
      }
      i -= 4;
    }
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void launchDeviceDiscovery()
  {
    assertRunOnServiceThread();
    clearDeviceInfoList();
    addAndStartAction(new DeviceDiscoveryAction(this, new DeviceDiscoveryAction.DeviceDiscoveryCallback()
    {
      public void onDeviceDiscoveryDone(List<HdmiDeviceInfo> paramAnonymousList)
      {
        paramAnonymousList = paramAnonymousList.iterator();
        Object localObject;
        while (paramAnonymousList.hasNext())
        {
          localObject = (HdmiDeviceInfo)paramAnonymousList.next();
          HdmiCecLocalDeviceTv.this.addCecDevice((HdmiDeviceInfo)localObject);
        }
        paramAnonymousList = HdmiCecLocalDeviceTv.this.mService.getAllLocalDevices().iterator();
        while (paramAnonymousList.hasNext())
        {
          localObject = (HdmiCecLocalDevice)paramAnonymousList.next();
          HdmiCecLocalDeviceTv.this.addCecDevice(((HdmiCecLocalDevice)localObject).getDeviceInfo());
        }
        HdmiCecLocalDeviceTv.-get0(HdmiCecLocalDeviceTv.this).process();
        HdmiCecLocalDeviceTv.-wrap2(HdmiCecLocalDeviceTv.this);
        HdmiCecLocalDeviceTv.this.addAndStartAction(new HotplugDetectionAction(HdmiCecLocalDeviceTv.this));
        HdmiCecLocalDeviceTv.this.addAndStartAction(new PowerStatusMonitorAction(HdmiCecLocalDeviceTv.this));
        paramAnonymousList = HdmiCecLocalDeviceTv.this.getAvrDeviceInfo();
        if (paramAnonymousList != null)
        {
          HdmiCecLocalDeviceTv.this.onNewAvrAdded(paramAnonymousList);
          return;
        }
        HdmiCecLocalDeviceTv.this.setSystemAudioMode(false, true);
      }
    }));
  }
  
  private void notifyArcStatusToAudioService(boolean paramBoolean)
  {
    AudioManager localAudioManager = this.mService.getAudioManager();
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localAudioManager.setWiredDeviceConnectionState(262144, i, "", "");
      return;
    }
  }
  
  private void removeCecSwitches(int paramInt)
  {
    Iterator localIterator = this.mCecSwitches.iterator();
    while (!localIterator.hasNext()) {
      if (pathToPortId(((Integer)localIterator.next()).intValue()) == paramInt) {
        localIterator.remove();
      }
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private HdmiDeviceInfo removeDeviceInfo(int paramInt)
  {
    assertRunOnServiceThread();
    HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)this.mDeviceInfos.get(paramInt);
    if (localHdmiDeviceInfo != null) {
      this.mDeviceInfos.remove(paramInt);
    }
    updateSafeDeviceInfoList();
    return localHdmiDeviceInfo;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void removeTvInput(String paramString)
  {
    assertRunOnServiceThread();
    this.mTvInputs.remove(paramString);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void resetSelectRequestBuffer()
  {
    assertRunOnServiceThread();
    setSelectRequestBuffer(SelectRequestBuffer.EMPTY_BUFFER);
  }
  
  private void sendClearTimerMessage(final int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    switch (paramInt2)
    {
    default: 
      Slog.w("HdmiCecLocalDeviceTv", "Invalid source type:" + paramInt1);
      announceClearTimerRecordingResult(paramInt1, 161);
      return;
    case 1: 
      paramArrayOfByte = HdmiCecMessageBuilder.buildClearDigitalTimer(this.mAddress, paramInt1, paramArrayOfByte);
    }
    for (;;)
    {
      this.mService.sendCecCommand(paramArrayOfByte, new HdmiControlService.SendMessageCallback()
      {
        public void onSendCompleted(int paramAnonymousInt)
        {
          if (paramAnonymousInt != 0) {
            HdmiCecLocalDeviceTv.this.announceClearTimerRecordingResult(paramInt1, 161);
          }
        }
      });
      return;
      paramArrayOfByte = HdmiCecMessageBuilder.buildClearAnalogueTimer(this.mAddress, paramInt1, paramArrayOfByte);
      continue;
      paramArrayOfByte = HdmiCecMessageBuilder.buildClearExternalTimer(this.mAddress, paramInt1, paramArrayOfByte);
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void updateArcFeatureStatus(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    if (!this.mService.getPortInfo(paramInt).isArcSupported()) {
      return;
    }
    HdmiDeviceInfo localHdmiDeviceInfo = getAvrDeviceInfo();
    if (localHdmiDeviceInfo == null)
    {
      if (paramBoolean) {
        this.mArcFeatureEnabled.put(paramInt, paramBoolean);
      }
      return;
    }
    if (localHdmiDeviceInfo.getPortId() == paramInt) {
      changeArcFeatureEnabled(paramInt, paramBoolean);
    }
  }
  
  private void updateAudioManagerForSystemAudio(boolean paramBoolean)
  {
    HdmiLogger.debug("[A]UpdateSystemAudio mode[on=%b] output=[%X]", new Object[] { Boolean.valueOf(paramBoolean), Integer.valueOf(this.mService.getAudioManager().setHdmiSystemAudioSupported(paramBoolean)) });
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void updateSafeDeviceInfoList()
  {
    assertRunOnServiceThread();
    List localList1 = HdmiUtils.sparseArrayToList(this.mDeviceInfos);
    List localList2 = getInputDevices();
    synchronized (this.mLock)
    {
      this.mSafeAllDeviceInfos = localList1;
      this.mSafeExternalInputs = localList2;
      return;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  final void addCecDevice(HdmiDeviceInfo paramHdmiDeviceInfo)
  {
    assertRunOnServiceThread();
    HdmiDeviceInfo localHdmiDeviceInfo = addDeviceInfo(paramHdmiDeviceInfo);
    if (paramHdmiDeviceInfo.getLogicalAddress() == this.mAddress) {
      return;
    }
    if (localHdmiDeviceInfo == null) {
      invokeDeviceEventListener(paramHdmiDeviceInfo, 1);
    }
    while (localHdmiDeviceInfo.equals(paramHdmiDeviceInfo)) {
      return;
    }
    invokeDeviceEventListener(localHdmiDeviceInfo, 2);
    invokeDeviceEventListener(paramHdmiDeviceInfo, 1);
  }
  
  void announceClearTimerRecordingResult(int paramInt1, int paramInt2)
  {
    this.mService.invokeClearTimerRecordingResult(paramInt1, paramInt2);
  }
  
  void announceOneTouchRecordResult(int paramInt1, int paramInt2)
  {
    this.mService.invokeOneTouchRecordResult(paramInt1, paramInt2);
  }
  
  void announceTimerRecordingResult(int paramInt1, int paramInt2)
  {
    this.mService.invokeTimerRecordingResult(paramInt1, paramInt2);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean broadcastMenuLanguage(String paramString)
  {
    assertRunOnServiceThread();
    paramString = HdmiCecMessageBuilder.buildSetMenuLanguageCommand(this.mAddress, paramString);
    if (paramString != null)
    {
      this.mService.sendCecCommand(paramString);
      return true;
    }
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void changeArcFeatureEnabled(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    if (this.mArcFeatureEnabled.get(paramInt) != paramBoolean)
    {
      this.mArcFeatureEnabled.put(paramInt, paramBoolean);
      if (!paramBoolean) {
        break label42;
      }
      if (!this.mArcEstablished) {
        startArcAction(true);
      }
    }
    label42:
    while (!this.mArcEstablished) {
      return;
    }
    startArcAction(false);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void changeMute(boolean paramBoolean)
  {
    assertRunOnServiceThread();
    HdmiLogger.debug("[A]:Change mute:%b", new Object[] { Boolean.valueOf(paramBoolean) });
    synchronized (this.mLock)
    {
      if (this.mSystemAudioMute == paramBoolean)
      {
        HdmiLogger.debug("No need to change mute.", new Object[0]);
        return;
      }
      if (!isSystemAudioActivated())
      {
        HdmiLogger.debug("[A]:System audio is not activated.", new Object[0]);
        return;
      }
    }
    removeAction(VolumeControlAction.class);
    sendUserControlPressedAndReleased(getAvrDeviceInfo().getLogicalAddress(), HdmiCecKeycode.getMuteKey(paramBoolean));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void changeSystemAudioMode(boolean paramBoolean, IHdmiControlCallback paramIHdmiControlCallback)
  {
    assertRunOnServiceThread();
    if ((!this.mService.isControlEnabled()) || (hasAction(DeviceDiscoveryAction.class)))
    {
      setSystemAudioMode(false, true);
      invokeCallback(paramIHdmiControlCallback, 6);
      return;
    }
    HdmiDeviceInfo localHdmiDeviceInfo = getAvrDeviceInfo();
    if (localHdmiDeviceInfo == null)
    {
      setSystemAudioMode(false, true);
      invokeCallback(paramIHdmiControlCallback, 3);
      return;
    }
    addAndStartAction(new SystemAudioActionFromTv(this, localHdmiDeviceInfo.getLogicalAddress(), paramBoolean, paramIHdmiControlCallback));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void changeVolume(int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool = true;
    assertRunOnServiceThread();
    if ((paramInt2 != 0) && (isSystemAudioActivated())) {
      paramInt1 = VolumeControlAction.scaleToCecVolume(paramInt1 + paramInt2, paramInt3);
    }
    for (;;)
    {
      synchronized (this.mLock)
      {
        if (paramInt1 == this.mSystemAudioVolume)
        {
          this.mService.setAudioStatus(false, VolumeControlAction.scaleToCustomVolume(this.mSystemAudioVolume, paramInt3));
          return;
          return;
        }
        ??? = getActions(VolumeControlAction.class);
        if (!((List)???).isEmpty()) {
          break;
        }
        paramInt1 = getAvrDeviceInfo().getLogicalAddress();
        if (paramInt2 > 0)
        {
          bool = true;
          addAndStartAction(new VolumeControlAction(this, paramInt1, bool));
          return;
        }
      }
      bool = false;
    }
    ??? = (VolumeControlAction)((List)???).get(0);
    if (paramInt2 > 0) {}
    for (;;)
    {
      ((VolumeControlAction)???).handleVolumeChange(bool);
      return;
      bool = false;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void clearTimerRecording(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    assertRunOnServiceThread();
    if (!this.mService.isControlEnabled())
    {
      Slog.w("HdmiCecLocalDeviceTv", "Can not start one touch record. CEC control is disabled.");
      announceClearTimerRecordingResult(paramInt1, 162);
      return;
    }
    if (!checkRecorder(paramInt1))
    {
      Slog.w("HdmiCecLocalDeviceTv", "Invalid recorder address:" + paramInt1);
      announceClearTimerRecordingResult(paramInt1, 160);
      return;
    }
    if (!checkTimerRecordingSource(paramInt2, paramArrayOfByte))
    {
      Slog.w("HdmiCecLocalDeviceTv", "Invalid record source." + Arrays.toString(paramArrayOfByte));
      announceClearTimerRecordingResult(paramInt1, 161);
      return;
    }
    sendClearTimerMessage(paramInt1, paramInt2, paramArrayOfByte);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void deviceSelect(int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
  {
    assertRunOnServiceThread();
    HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)this.mDeviceInfos.get(paramInt);
    if (localHdmiDeviceInfo == null)
    {
      invokeCallback(paramIHdmiControlCallback, 3);
      return;
    }
    paramInt = localHdmiDeviceInfo.getLogicalAddress();
    HdmiCecLocalDevice.ActiveSource localActiveSource = getActiveSource();
    if ((localHdmiDeviceInfo.getDevicePowerStatus() == 0) && (localActiveSource.isValid()) && (paramInt == localActiveSource.logicalAddress))
    {
      invokeCallback(paramIHdmiControlCallback, 0);
      return;
    }
    if (paramInt == 0)
    {
      handleSelectInternalSource();
      setActiveSource(paramInt, this.mService.getPhysicalAddress());
      setActivePath(this.mService.getPhysicalAddress());
      invokeCallback(paramIHdmiControlCallback, 0);
      return;
    }
    if (!this.mService.isControlEnabled())
    {
      setActiveSource(localHdmiDeviceInfo);
      invokeCallback(paramIHdmiControlCallback, 6);
      return;
    }
    removeAction(DeviceSelectAction.class);
    addAndStartAction(new DeviceSelectAction(this, localHdmiDeviceInfo, paramIHdmiControlCallback));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected void disableDevice(boolean paramBoolean, HdmiCecLocalDevice.PendingActionClearedCallback paramPendingActionClearedCallback)
  {
    assertRunOnServiceThread();
    this.mService.unregisterTvInputCallback(this.mTvInputCallback);
    removeAction(DeviceDiscoveryAction.class);
    removeAction(HotplugDetectionAction.class);
    removeAction(PowerStatusMonitorAction.class);
    removeAction(OneTouchRecordAction.class);
    removeAction(TimerRecordingAction.class);
    disableSystemAudioIfExist();
    disableArcIfExist();
    super.disableDevice(paramBoolean, paramPendingActionClearedCallback);
    clearDeviceInfoList();
    getActiveSource().invalidate();
    setActivePath(65535);
    checkIfPendingActionsCleared();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean dispatchMessage(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if ((this.mService.isPowerStandby()) && (this.mStandbyHandler.handleCommand(paramHdmiCecMessage))) {
      return true;
    }
    return super.onMessage(paramHdmiCecMessage);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void displayOsd(int paramInt)
  {
    assertRunOnServiceThread();
    this.mService.displayOsd(paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void displayOsd(int paramInt1, int paramInt2)
  {
    assertRunOnServiceThread();
    this.mService.displayOsd(paramInt1, paramInt2);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void doManualPortSwitching(int paramInt, IHdmiControlCallback paramIHdmiControlCallback)
  {
    assertRunOnServiceThread();
    if (!this.mService.isValidPortId(paramInt))
    {
      invokeCallback(paramIHdmiControlCallback, 6);
      return;
    }
    if (paramInt == getActivePortId())
    {
      invokeCallback(paramIHdmiControlCallback, 0);
      return;
    }
    this.mActiveSource.invalidate();
    if (!this.mService.isControlEnabled())
    {
      setActivePortId(paramInt);
      invokeCallback(paramIHdmiControlCallback, 6);
      return;
    }
    if (getActivePortId() != -1) {}
    for (int i = this.mService.portIdToPath(getActivePortId());; i = getDeviceInfo().getPhysicalAddress())
    {
      setActivePath(i);
      if (!this.mSkipRoutingControl) {
        break;
      }
      this.mSkipRoutingControl = false;
      return;
    }
    startRoutingControl(i, this.mService.portIdToPath(paramInt), true, paramIHdmiControlCallback);
  }
  
  protected void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    super.dump(paramIndentingPrintWriter);
    paramIndentingPrintWriter.println("mArcEstablished: " + this.mArcEstablished);
    paramIndentingPrintWriter.println("mArcFeatureEnabled: " + this.mArcFeatureEnabled);
    paramIndentingPrintWriter.println("mSystemAudioActivated: " + this.mSystemAudioActivated);
    paramIndentingPrintWriter.println("mSystemAudioMute: " + this.mSystemAudioMute);
    paramIndentingPrintWriter.println("mAutoDeviceOff: " + this.mAutoDeviceOff);
    paramIndentingPrintWriter.println("mAutoWakeup: " + this.mAutoWakeup);
    paramIndentingPrintWriter.println("mSkipRoutingControl: " + this.mSkipRoutingControl);
    paramIndentingPrintWriter.println("mPrevPortId: " + this.mPrevPortId);
    paramIndentingPrintWriter.println("CEC devices:");
    paramIndentingPrintWriter.increaseIndent();
    Iterator localIterator = this.mSafeAllDeviceInfos.iterator();
    while (localIterator.hasNext()) {
      paramIndentingPrintWriter.println((HdmiDeviceInfo)localIterator.next());
    }
    paramIndentingPrintWriter.decreaseIndent();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean getAutoWakeup()
  {
    assertRunOnServiceThread();
    return this.mAutoWakeup;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  HdmiDeviceInfo getAvrDeviceInfo()
  {
    assertRunOnServiceThread();
    return getCecDeviceInfo(5);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  HdmiDeviceInfo getCecDeviceInfo(int paramInt)
  {
    assertRunOnServiceThread();
    return (HdmiDeviceInfo)this.mDeviceInfos.get(HdmiDeviceInfo.idForCecDevice(paramInt));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  final HdmiDeviceInfo getDeviceInfoByPath(int paramInt)
  {
    assertRunOnServiceThread();
    Iterator localIterator = getDeviceInfoList(false).iterator();
    while (localIterator.hasNext())
    {
      HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)localIterator.next();
      if (localHdmiDeviceInfo.getPhysicalAddress() == paramInt) {
        return localHdmiDeviceInfo;
      }
    }
    return null;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  List<HdmiDeviceInfo> getDeviceInfoList(boolean paramBoolean)
  {
    assertRunOnServiceThread();
    if (paramBoolean) {
      return HdmiUtils.sparseArrayToList(this.mDeviceInfos);
    }
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (i < this.mDeviceInfos.size())
    {
      HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)this.mDeviceInfos.valueAt(i);
      if (!isLocalDeviceAddress(localHdmiDeviceInfo.getLogicalAddress())) {
        localArrayList.add(localHdmiDeviceInfo);
      }
      i += 1;
    }
    return localArrayList;
  }
  
  int getPortId(int paramInt)
  {
    return this.mService.pathToPortId(paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  int getPowerStatus()
  {
    assertRunOnServiceThread();
    return this.mService.getPowerStatus();
  }
  
  protected int getPreferredAddress()
  {
    return 0;
  }
  
  int getPrevPortId()
  {
    synchronized (this.mLock)
    {
      int i = this.mPrevPortId;
      return i;
    }
  }
  
  HdmiDeviceInfo getSafeAvrDeviceInfo()
  {
    return getSafeCecDeviceInfo(5);
  }
  
  HdmiDeviceInfo getSafeCecDeviceInfo(int paramInt)
  {
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mSafeAllDeviceInfos.iterator();
      while (localIterator.hasNext())
      {
        HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)localIterator.next();
        if (localHdmiDeviceInfo.isCecDevice())
        {
          int i = localHdmiDeviceInfo.getLogicalAddress();
          if (i == paramInt) {
            return localHdmiDeviceInfo;
          }
        }
      }
      return null;
    }
  }
  
  List<HdmiDeviceInfo> getSafeCecDevicesLocked()
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator = this.mSafeAllDeviceInfos.iterator();
    while (localIterator.hasNext())
    {
      HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)localIterator.next();
      if (!isLocalDeviceAddress(localHdmiDeviceInfo.getLogicalAddress())) {
        localArrayList.add(localHdmiDeviceInfo);
      }
    }
    return localArrayList;
  }
  
  HdmiDeviceInfo getSafeDeviceInfoByPath(int paramInt)
  {
    synchronized (this.mLock)
    {
      Iterator localIterator = this.mSafeAllDeviceInfos.iterator();
      while (localIterator.hasNext())
      {
        HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)localIterator.next();
        int i = localHdmiDeviceInfo.getPhysicalAddress();
        if (i == paramInt) {
          return localHdmiDeviceInfo;
        }
      }
      return null;
    }
  }
  
  List<HdmiDeviceInfo> getSafeExternalInputsLocked()
  {
    return this.mSafeExternalInputs;
  }
  
  boolean getSystemAudioModeSetting()
  {
    return this.mService.readBooleanSetting("hdmi_system_audio_enabled", false);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleActiveSource(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    int i = paramHdmiCecMessage.getSource();
    int j = HdmiUtils.twoBytesToInt(paramHdmiCecMessage.getParams());
    HdmiDeviceInfo localHdmiDeviceInfo = getCecDeviceInfo(i);
    if (localHdmiDeviceInfo == null)
    {
      if (!handleNewDeviceAtTheTailOfActivePath(j))
      {
        HdmiLogger.debug("Device info %X not found; buffering the command", new Object[] { Integer.valueOf(i) });
        this.mDelayedMessageBuffer.add(paramHdmiCecMessage);
      }
      return true;
    }
    if ((isInputReady(localHdmiDeviceInfo.getId())) || (localHdmiDeviceInfo.getDeviceType() == 5))
    {
      updateDevicePowerStatus(i, 0);
      paramHdmiCecMessage = HdmiCecLocalDevice.ActiveSource.of(i, j);
      ActiveSourceHandler.create(this, null).process(paramHdmiCecMessage, localHdmiDeviceInfo.getDeviceType());
      return true;
    }
    HdmiLogger.debug("Input not ready for device: %X; buffering the command", new Object[] { Integer.valueOf(localHdmiDeviceInfo.getId()) });
    this.mDelayedMessageBuffer.add(paramHdmiCecMessage);
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleGetMenuLanguage(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if (!broadcastMenuLanguage(this.mService.getLanguage())) {
      Slog.w("HdmiCecLocalDeviceTv", "Failed to respond to <Get Menu Language>: " + paramHdmiCecMessage.toString());
    }
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleImageViewOn(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    return handleTextViewOn(paramHdmiCecMessage);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleInactiveSource(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if (getActiveSource().logicalAddress != paramHdmiCecMessage.getSource()) {
      return true;
    }
    if (isProhibitMode()) {
      return true;
    }
    int i = getPrevPortId();
    if (i != -1)
    {
      paramHdmiCecMessage = getCecDeviceInfo(paramHdmiCecMessage.getSource());
      if (paramHdmiCecMessage == null) {
        return true;
      }
      if (this.mService.pathToPortId(paramHdmiCecMessage.getPhysicalAddress()) == i) {
        return true;
      }
      doManualPortSwitching(i, null);
      setPrevPortId(-1);
      return true;
    }
    this.mActiveSource.invalidate();
    setActivePath(65535);
    this.mService.invokeInputChangeListener(HdmiDeviceInfo.INACTIVE_DEVICE);
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleInitiateArc(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if (!canStartArcUpdateAction(paramHdmiCecMessage.getSource(), true))
    {
      if (getAvrDeviceInfo() == null)
      {
        this.mDelayedMessageBuffer.add(paramHdmiCecMessage);
        return true;
      }
      this.mService.maySendFeatureAbortCommand(paramHdmiCecMessage, 4);
      if (!isConnectedToArcPort(paramHdmiCecMessage.getSource())) {
        displayOsd(1);
      }
      return true;
    }
    removeAction(RequestArcInitiationAction.class);
    addAndStartAction(new SetArcTransmissionStateAction(this, paramHdmiCecMessage.getSource(), true));
    return true;
  }
  
  protected boolean handleMenuStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    return true;
  }
  
  protected boolean handleRecordStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleRecordTvScreen(HdmiCecMessage paramHdmiCecMessage)
  {
    List localList = getActions(OneTouchRecordAction.class);
    if (!localList.isEmpty())
    {
      if (((OneTouchRecordAction)localList.get(0)).getRecorderAddress() != paramHdmiCecMessage.getSource()) {
        announceOneTouchRecordResult(paramHdmiCecMessage.getSource(), 48);
      }
      return super.handleRecordTvScreen(paramHdmiCecMessage);
    }
    int i = paramHdmiCecMessage.getSource();
    i = startOneTouchRecord(i, this.mService.invokeRecordRequestListener(i));
    if (i != -1) {
      this.mService.maySendFeatureAbortCommand(paramHdmiCecMessage, i);
    }
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void handleRemoveActiveRoutingPath(int paramInt)
  {
    assertRunOnServiceThread();
    if (isTailOfActivePath(paramInt, getActivePath()))
    {
      paramInt = this.mService.portIdToPath(getActivePortId());
      startRoutingControl(getActivePath(), paramInt, true, null);
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleReportAudioStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    boolean bool = false;
    assertRunOnServiceThread();
    paramHdmiCecMessage = paramHdmiCecMessage.getParams();
    int i = paramHdmiCecMessage[0];
    int j = paramHdmiCecMessage[0];
    if ((i & 0x80) == 128) {
      bool = true;
    }
    setAudioStatus(bool, j & 0x7F);
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleReportPhysicalAddress(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    int i = HdmiUtils.twoBytesToInt(paramHdmiCecMessage.getParams());
    int j = paramHdmiCecMessage.getSource();
    int k = paramHdmiCecMessage.getParams()[2];
    if (updateCecSwitchInfo(j, k, i)) {
      return true;
    }
    if (hasAction(DeviceDiscoveryAction.class))
    {
      Slog.i("HdmiCecLocalDeviceTv", "Ignored while Device Discovery Action is in progress: " + paramHdmiCecMessage);
      return true;
    }
    if (!isInDeviceList(j, i)) {
      handleNewDeviceAtTheTailOfActivePath(i);
    }
    addCecDevice(new HdmiDeviceInfo(j, i, getPortId(i), k, 16777215, HdmiUtils.getDefaultDeviceName(j)));
    startNewDeviceAction(HdmiCecLocalDevice.ActiveSource.of(j, i), k);
    return true;
  }
  
  protected boolean handleReportPowerStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    int i = paramHdmiCecMessage.getParams()[0];
    updateDevicePowerStatus(paramHdmiCecMessage.getSource(), i & 0xFF);
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleRequestActiveSource(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if (this.mAddress == getActiveSource().logicalAddress) {
      this.mService.sendCecCommand(HdmiCecMessageBuilder.buildActiveSource(this.mAddress, getActivePath()));
    }
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleRoutingChange(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    paramHdmiCecMessage = paramHdmiCecMessage.getParams();
    int i = HdmiUtils.twoBytesToInt(paramHdmiCecMessage);
    if (HdmiUtils.isAffectingActiveRoutingPath(getActivePath(), i))
    {
      this.mActiveSource.invalidate();
      removeAction(RoutingControlAction.class);
      addAndStartAction(new RoutingControlAction(this, HdmiUtils.twoBytesToInt(paramHdmiCecMessage, 2), true, null));
    }
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleSetOsdName(HdmiCecMessage paramHdmiCecMessage)
  {
    HdmiDeviceInfo localHdmiDeviceInfo = getCecDeviceInfo(paramHdmiCecMessage.getSource());
    if (localHdmiDeviceInfo == null)
    {
      Slog.e("HdmiCecLocalDeviceTv", "No source device info for <Set Osd Name>." + paramHdmiCecMessage);
      return true;
    }
    String str;
    try
    {
      str = new String(paramHdmiCecMessage.getParams(), "US-ASCII");
      if (localHdmiDeviceInfo.getDisplayName().equals(str))
      {
        Slog.i("HdmiCecLocalDeviceTv", "Ignore incoming <Set Osd Name> having same osd name:" + paramHdmiCecMessage);
        return true;
      }
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      Slog.e("HdmiCecLocalDeviceTv", "Invalid <Set Osd Name> request:" + paramHdmiCecMessage, localUnsupportedEncodingException);
      return true;
    }
    addCecDevice(new HdmiDeviceInfo(localUnsupportedEncodingException.getLogicalAddress(), localUnsupportedEncodingException.getPhysicalAddress(), localUnsupportedEncodingException.getPortId(), localUnsupportedEncodingException.getDeviceType(), localUnsupportedEncodingException.getVendorId(), str));
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleSetSystemAudioMode(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if (!isMessageForSystemAudio(paramHdmiCecMessage))
    {
      if (getAvrDeviceInfo() == null)
      {
        this.mDelayedMessageBuffer.add(paramHdmiCecMessage);
        return true;
      }
      HdmiLogger.warning("Invalid <Set System Audio Mode> message:" + paramHdmiCecMessage, new Object[0]);
      this.mService.maySendFeatureAbortCommand(paramHdmiCecMessage, 4);
      return true;
    }
    removeAction(SystemAudioAutoInitiationAction.class);
    addAndStartAction(new SystemAudioActionFromAvr(this, paramHdmiCecMessage.getSource(), HdmiUtils.parseCommandParamSystemAudioStatus(paramHdmiCecMessage), null));
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleSystemAudioModeStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if (!isMessageForSystemAudio(paramHdmiCecMessage))
    {
      HdmiLogger.warning("Invalid <System Audio Mode Status> message:" + paramHdmiCecMessage, new Object[0]);
      return true;
    }
    setSystemAudioMode(HdmiUtils.parseCommandParamSystemAudioStatus(paramHdmiCecMessage), true);
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleTerminateArc(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if (this.mService.isPowerStandbyOrTransient())
    {
      setArcStatus(false);
      return true;
    }
    removeAction(RequestArcTerminationAction.class);
    addAndStartAction(new SetArcTransmissionStateAction(this, paramHdmiCecMessage.getSource(), false));
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleTextViewOn(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if ((this.mService.isPowerStandbyOrTransient()) && (this.mAutoWakeup)) {
      this.mService.wakeUp();
    }
    return true;
  }
  
  protected boolean handleTimerClearedStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    int i = paramHdmiCecMessage.getParams()[0];
    announceTimerRecordingResult(paramHdmiCecMessage.getSource(), i & 0xFF);
    return true;
  }
  
  protected boolean handleTimerStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    return true;
  }
  
  boolean hasSystemAudioDevice()
  {
    return getSafeAvrDeviceInfo() != null;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean isArcEstablished()
  {
    assertRunOnServiceThread();
    if (this.mArcEstablished)
    {
      int i = 0;
      while (i < this.mArcFeatureEnabled.size())
      {
        if (this.mArcFeatureEnabled.valueAt(i)) {
          return true;
        }
        i += 1;
      }
    }
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean isArcFeatureEnabled(int paramInt)
  {
    assertRunOnServiceThread();
    return this.mArcFeatureEnabled.get(paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean isConnected(int paramInt)
  {
    assertRunOnServiceThread();
    return this.mService.isConnected(paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean isInDeviceList(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    assertRunOnServiceThread();
    HdmiDeviceInfo localHdmiDeviceInfo = getCecDeviceInfo(paramInt1);
    if (localHdmiDeviceInfo == null) {
      return false;
    }
    if (localHdmiDeviceInfo.getPhysicalAddress() == paramInt2) {
      bool = true;
    }
    return bool;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean isInputReady(int paramInt)
  {
    assertRunOnServiceThread();
    return this.mTvInputs.containsValue(Integer.valueOf(paramInt));
  }
  
  boolean isPowerStandbyOrTransient()
  {
    return this.mService.isPowerStandbyOrTransient();
  }
  
  boolean isProhibitMode()
  {
    return this.mService.isProhibitMode();
  }
  
  boolean isSystemAudioActivated()
  {
    if (!hasSystemAudioDevice()) {
      return false;
    }
    synchronized (this.mLock)
    {
      boolean bool = this.mSystemAudioActivated;
      return bool;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void launchRoutingControl(boolean paramBoolean)
  {
    assertRunOnServiceThread();
    if (getActivePortId() != -1) {
      if ((!paramBoolean) && (!isProhibitMode())) {}
    }
    int i;
    do
    {
      return;
      i = this.mService.portIdToPath(getActivePortId());
      setActivePath(i);
      startRoutingControl(getActivePath(), i, paramBoolean, null);
      return;
      i = this.mService.getPhysicalAddress();
      setActivePath(i);
    } while ((paramBoolean) || (this.mDelayedMessageBuffer.isBuffered(130)));
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildActiveSource(this.mAddress, i));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected void onAddressAllocated(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    assertRunOnServiceThread();
    Iterator localIterator = this.mService.getPortInfo().iterator();
    while (localIterator.hasNext())
    {
      HdmiPortInfo localHdmiPortInfo = (HdmiPortInfo)localIterator.next();
      this.mArcFeatureEnabled.put(localHdmiPortInfo.getId(), localHdmiPortInfo.isArcSupported());
    }
    this.mService.registerTvInputCallback(this.mTvInputCallback);
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportPhysicalAddressCommand(this.mAddress, this.mService.getPhysicalAddress(), this.mDeviceType));
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildDeviceVendorIdCommand(this.mAddress, this.mService.getVendorId()));
    this.mCecSwitches.add(Integer.valueOf(this.mService.getPhysicalAddress()));
    this.mTvInputs.clear();
    if (paramInt2 == 3) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      this.mSkipRoutingControl = bool1;
      bool1 = bool2;
      if (paramInt2 != 0)
      {
        bool1 = bool2;
        if (paramInt2 != 1) {
          bool1 = true;
        }
      }
      launchRoutingControl(bool1);
      this.mLocalDeviceAddresses = initLocalDeviceAddresses();
      resetSelectRequestBuffer();
      launchDeviceDiscovery();
      return;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void onHotplug(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    if (!paramBoolean) {
      removeCecSwitches(paramInt);
    }
    List localList = getActions(HotplugDetectionAction.class);
    if (!localList.isEmpty()) {
      ((HotplugDetectionAction)localList.get(0)).pollAllDevicesNow();
    }
    updateArcFeatureStatus(paramInt, paramBoolean);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void onNewAvrAdded(HdmiDeviceInfo paramHdmiDeviceInfo)
  {
    assertRunOnServiceThread();
    addAndStartAction(new SystemAudioAutoInitiationAction(this, paramHdmiDeviceInfo.getLogicalAddress()));
    if ((!isArcFeatureEnabled(paramHdmiDeviceInfo.getPortId())) || (hasAction(SetArcTransmissionStateAction.class))) {
      return;
    }
    startArcAction(true);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected void onStandby(boolean paramBoolean, int paramInt)
  {
    assertRunOnServiceThread();
    if (!this.mService.isControlEnabled()) {
      return;
    }
    if ((!paramBoolean) && (this.mAutoDeviceOff)) {
      this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(this.mAddress, 15));
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void processAllDelayedMessages()
  {
    assertRunOnServiceThread();
    this.mDelayedMessageBuffer.processAllMessages();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void processDelayedActiveSource(int paramInt)
  {
    assertRunOnServiceThread();
    this.mDelayedMessageBuffer.processActiveSource(paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void processDelayedMessages(int paramInt)
  {
    assertRunOnServiceThread();
    this.mDelayedMessageBuffer.processMessagesForDevice(paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  final void removeCecDevice(int paramInt)
  {
    assertRunOnServiceThread();
    HdmiDeviceInfo localHdmiDeviceInfo = removeDeviceInfo(HdmiDeviceInfo.idForCecDevice(paramInt));
    this.mCecMessageCache.flushMessagesFrom(paramInt);
    invokeDeviceEventListener(localHdmiDeviceInfo, 2);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected void sendKeyEvent(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    if (!HdmiCecKeycode.isSupportedKeycode(paramInt))
    {
      Slog.w("HdmiCecLocalDeviceTv", "Unsupported key: " + paramInt);
      return;
    }
    List localList = getActions(SendKeyAction.class);
    int i = findKeyReceiverAddress();
    if (i == this.mAddress)
    {
      Slog.w("HdmiCecLocalDeviceTv", "Discard key event to itself :" + paramInt + " pressed:" + paramBoolean);
      return;
    }
    if (!localList.isEmpty())
    {
      ((SendKeyAction)localList.get(0)).processKeyEvent(paramInt, paramBoolean);
      return;
    }
    if ((paramBoolean) && (i != -1))
    {
      addAndStartAction(new SendKeyAction(this, i, paramInt));
      return;
    }
    Slog.w("HdmiCecLocalDeviceTv", "Discard key event: " + paramInt + " pressed:" + paramBoolean);
  }
  
  protected void sendStandby(int paramInt)
  {
    HdmiDeviceInfo localHdmiDeviceInfo = (HdmiDeviceInfo)this.mDeviceInfos.get(paramInt);
    if (localHdmiDeviceInfo == null) {
      return;
    }
    paramInt = localHdmiDeviceInfo.getLogicalAddress();
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(this.mAddress, paramInt));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean setArcStatus(boolean paramBoolean)
  {
    assertRunOnServiceThread();
    HdmiLogger.debug("Set Arc Status[old:%b new:%b]", new Object[] { Boolean.valueOf(this.mArcEstablished), Boolean.valueOf(paramBoolean) });
    boolean bool = this.mArcEstablished;
    setAudioReturnChannel(paramBoolean);
    notifyArcStatusToAudioService(paramBoolean);
    this.mArcEstablished = paramBoolean;
    return bool;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void setAudioReturnChannel(boolean paramBoolean)
  {
    assertRunOnServiceThread();
    HdmiDeviceInfo localHdmiDeviceInfo = getAvrDeviceInfo();
    if (localHdmiDeviceInfo != null) {
      this.mService.setAudioReturnChannel(localHdmiDeviceInfo.getPortId(), paramBoolean);
    }
  }
  
  void setAudioStatus(boolean paramBoolean, int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mSystemAudioMute = paramBoolean;
      this.mSystemAudioVolume = paramInt;
      int i = this.mService.getAudioManager().getStreamMaxVolume(3);
      this.mService.setAudioStatus(paramBoolean, VolumeControlAction.scaleToCustomVolume(paramInt, i));
      if (paramBoolean) {
        paramInt = 101;
      }
      displayOsd(2, paramInt);
      return;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void setAutoDeviceOff(boolean paramBoolean)
  {
    assertRunOnServiceThread();
    this.mAutoDeviceOff = paramBoolean;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void setAutoWakeup(boolean paramBoolean)
  {
    assertRunOnServiceThread();
    this.mAutoWakeup = paramBoolean;
  }
  
  protected void setPreferredAddress(int paramInt)
  {
    Slog.w("HdmiCecLocalDeviceTv", "Preferred addres will not be stored for TV");
  }
  
  void setPrevPortId(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mPrevPortId = paramInt;
      return;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  public void setSelectRequestBuffer(SelectRequestBuffer paramSelectRequestBuffer)
  {
    assertRunOnServiceThread();
    this.mSelectRequestBuffer = paramSelectRequestBuffer;
  }
  
  void setSystemAudioMode(boolean paramBoolean1, boolean paramBoolean2)
  {
    HdmiLogger.debug("System Audio Mode change[old:%b new:%b]", new Object[] { Boolean.valueOf(this.mSystemAudioActivated), Boolean.valueOf(paramBoolean1) });
    if (paramBoolean2) {
      this.mService.writeBooleanSetting("hdmi_system_audio_enabled", paramBoolean1);
    }
    updateAudioManagerForSystemAudio(paramBoolean1);
    synchronized (this.mLock)
    {
      if (this.mSystemAudioActivated != paramBoolean1)
      {
        this.mSystemAudioActivated = paramBoolean1;
        this.mService.announceSystemAudioModeChange(paramBoolean1);
      }
      return;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void startArcAction(boolean paramBoolean)
  {
    assertRunOnServiceThread();
    HdmiDeviceInfo localHdmiDeviceInfo = getAvrDeviceInfo();
    if (localHdmiDeviceInfo == null)
    {
      Slog.w("HdmiCecLocalDeviceTv", "Failed to start arc action; No AVR device.");
      return;
    }
    if (!canStartArcUpdateAction(localHdmiDeviceInfo.getLogicalAddress(), paramBoolean))
    {
      Slog.w("HdmiCecLocalDeviceTv", "Failed to start arc action; ARC configuration check failed.");
      if ((!paramBoolean) || (isConnectedToArcPort(localHdmiDeviceInfo.getPhysicalAddress()))) {
        return;
      }
      displayOsd(1);
      return;
    }
    if (paramBoolean)
    {
      removeAction(RequestArcTerminationAction.class);
      if (!hasAction(RequestArcInitiationAction.class)) {
        addAndStartAction(new RequestArcInitiationAction(this, localHdmiDeviceInfo.getLogicalAddress()));
      }
    }
    do
    {
      return;
      removeAction(RequestArcInitiationAction.class);
    } while (hasAction(RequestArcTerminationAction.class));
    addAndStartAction(new RequestArcTerminationAction(this, localHdmiDeviceInfo.getLogicalAddress()));
  }
  
  void startNewDeviceAction(HdmiCecLocalDevice.ActiveSource paramActiveSource, int paramInt)
  {
    Iterator localIterator = getActions(NewDeviceAction.class).iterator();
    while (localIterator.hasNext()) {
      if (((NewDeviceAction)localIterator.next()).isActionOf(paramActiveSource)) {
        return;
      }
    }
    addAndStartAction(new NewDeviceAction(this, paramActiveSource.logicalAddress, paramActiveSource.physicalAddress, paramInt));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  int startOneTouchRecord(int paramInt, byte[] paramArrayOfByte)
  {
    assertRunOnServiceThread();
    if (!this.mService.isControlEnabled())
    {
      Slog.w("HdmiCecLocalDeviceTv", "Can not start one touch record. CEC control is disabled.");
      announceOneTouchRecordResult(paramInt, 51);
      return 1;
    }
    if (!checkRecorder(paramInt))
    {
      Slog.w("HdmiCecLocalDeviceTv", "Invalid recorder address:" + paramInt);
      announceOneTouchRecordResult(paramInt, 49);
      return 1;
    }
    if (!checkRecordSource(paramArrayOfByte))
    {
      Slog.w("HdmiCecLocalDeviceTv", "Invalid record source." + Arrays.toString(paramArrayOfByte));
      announceOneTouchRecordResult(paramInt, 50);
      return 2;
    }
    addAndStartAction(new OneTouchRecordAction(this, paramInt, paramArrayOfByte));
    Slog.i("HdmiCecLocalDeviceTv", "Start new [One Touch Record]-Target:" + paramInt + ", recordSource:" + Arrays.toString(paramArrayOfByte));
    return -1;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void startRoutingControl(int paramInt1, int paramInt2, boolean paramBoolean, IHdmiControlCallback paramIHdmiControlCallback)
  {
    assertRunOnServiceThread();
    if (paramInt1 == paramInt2) {
      return;
    }
    HdmiCecMessage localHdmiCecMessage = HdmiCecMessageBuilder.buildRoutingChange(this.mAddress, paramInt1, paramInt2);
    this.mService.sendCecCommand(localHdmiCecMessage);
    removeAction(RoutingControlAction.class);
    addAndStartAction(new RoutingControlAction(this, paramInt2, paramBoolean, paramIHdmiControlCallback));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void startTimerRecording(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    assertRunOnServiceThread();
    if (!this.mService.isControlEnabled())
    {
      Slog.w("HdmiCecLocalDeviceTv", "Can not start one touch record. CEC control is disabled.");
      announceTimerRecordingResult(paramInt1, 3);
      return;
    }
    if (!checkRecorder(paramInt1))
    {
      Slog.w("HdmiCecLocalDeviceTv", "Invalid recorder address:" + paramInt1);
      announceTimerRecordingResult(paramInt1, 1);
      return;
    }
    if (!checkTimerRecordingSource(paramInt2, paramArrayOfByte))
    {
      Slog.w("HdmiCecLocalDeviceTv", "Invalid record source." + Arrays.toString(paramArrayOfByte));
      announceTimerRecordingResult(paramInt1, 2);
      return;
    }
    addAndStartAction(new TimerRecordingAction(this, paramInt1, paramInt2, paramArrayOfByte));
    Slog.i("HdmiCecLocalDeviceTv", "Start [Timer Recording]-Target:" + paramInt1 + ", SourceType:" + paramInt2 + ", RecordSource:" + Arrays.toString(paramArrayOfByte));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void stopOneTouchRecord(int paramInt)
  {
    assertRunOnServiceThread();
    if (!this.mService.isControlEnabled())
    {
      Slog.w("HdmiCecLocalDeviceTv", "Can not stop one touch record. CEC control is disabled.");
      announceOneTouchRecordResult(paramInt, 51);
      return;
    }
    if (!checkRecorder(paramInt))
    {
      Slog.w("HdmiCecLocalDeviceTv", "Invalid recorder address:" + paramInt);
      announceOneTouchRecordResult(paramInt, 49);
      return;
    }
    removeAction(OneTouchRecordAction.class);
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildRecordOff(this.mAddress, paramInt));
    Slog.i("HdmiCecLocalDeviceTv", "Stop [One Touch Record]-Target:" + paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void updateActiveInput(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    setActivePath(paramInt);
    if (paramBoolean)
    {
      HdmiDeviceInfo localHdmiDeviceInfo2 = getCecDeviceInfo(getActiveSource().logicalAddress);
      HdmiDeviceInfo localHdmiDeviceInfo1 = localHdmiDeviceInfo2;
      if (localHdmiDeviceInfo2 == null)
      {
        localHdmiDeviceInfo2 = this.mService.getDeviceInfoByPort(getActivePortId());
        localHdmiDeviceInfo1 = localHdmiDeviceInfo2;
        if (localHdmiDeviceInfo2 == null) {
          localHdmiDeviceInfo1 = new HdmiDeviceInfo(paramInt, getActivePortId());
        }
      }
      this.mService.invokeInputChangeListener(localHdmiDeviceInfo1);
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void updateActiveSource(int paramInt1, int paramInt2)
  {
    assertRunOnServiceThread();
    updateActiveSource(HdmiCecLocalDevice.ActiveSource.of(paramInt1, paramInt2));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void updateActiveSource(HdmiCecLocalDevice.ActiveSource paramActiveSource)
  {
    assertRunOnServiceThread();
    if (this.mActiveSource.equals(paramActiveSource)) {
      return;
    }
    setActiveSource(paramActiveSource);
    int i = paramActiveSource.logicalAddress;
    if ((getCecDeviceInfo(i) != null) && (i != this.mAddress) && (this.mService.pathToPortId(paramActiveSource.physicalAddress) == getActivePortId())) {
      setPrevPortId(getActivePortId());
    }
  }
  
  boolean updateCecSwitchInfo(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 == 15) && (paramInt2 == 6))
    {
      this.mCecSwitches.add(Integer.valueOf(paramInt3));
      updateSafeDeviceInfoList();
      return true;
    }
    if (paramInt2 == 5) {
      this.mCecSwitches.add(Integer.valueOf(paramInt3));
    }
    return false;
  }
  
  void updateDevicePowerStatus(int paramInt1, int paramInt2)
  {
    HdmiDeviceInfo localHdmiDeviceInfo = getCecDeviceInfo(paramInt1);
    if (localHdmiDeviceInfo == null)
    {
      Slog.w("HdmiCecLocalDeviceTv", "Can not update power status of non-existing device:" + paramInt1);
      return;
    }
    if (localHdmiDeviceInfo.getDevicePowerStatus() == paramInt2) {
      return;
    }
    localHdmiDeviceInfo = HdmiUtils.cloneHdmiDeviceInfo(localHdmiDeviceInfo, paramInt2);
    addDeviceInfo(localHdmiDeviceInfo);
    invokeDeviceEventListener(localHdmiDeviceInfo, 3);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiCecLocalDeviceTv.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */