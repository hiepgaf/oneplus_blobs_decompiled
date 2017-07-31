package com.android.server.hdmi;

import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Slog;
import android.view.KeyEvent;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

abstract class HdmiCecLocalDevice
{
  private static final int DEVICE_CLEANUP_TIMEOUT = 5000;
  private static final int FOLLOWER_SAFETY_TIMEOUT = 550;
  private static final int MSG_DISABLE_DEVICE_TIMEOUT = 1;
  private static final int MSG_USER_CONTROL_RELEASE_TIMEOUT = 2;
  private static final String TAG = "HdmiCecLocalDevice";
  private final ArrayList<HdmiCecFeatureAction> mActions = new ArrayList();
  @GuardedBy("mLock")
  private int mActiveRoutingPath;
  @GuardedBy("mLock")
  protected final ActiveSource mActiveSource = new ActiveSource();
  protected int mAddress;
  protected final HdmiCecMessageCache mCecMessageCache = new HdmiCecMessageCache();
  protected HdmiDeviceInfo mDeviceInfo;
  protected final int mDeviceType;
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        HdmiCecLocalDevice.-wrap0(HdmiCecLocalDevice.this);
        return;
      }
      HdmiCecLocalDevice.this.handleUserControlReleased();
    }
  };
  protected int mLastKeyRepeatCount = 0;
  protected int mLastKeycode = -1;
  protected final Object mLock;
  protected PendingActionClearedCallback mPendingActionClearedCallback;
  protected int mPreferredAddress;
  protected final HdmiControlService mService;
  
  protected HdmiCecLocalDevice(HdmiControlService paramHdmiControlService, int paramInt)
  {
    this.mService = paramHdmiControlService;
    this.mDeviceType = paramInt;
    this.mAddress = 15;
    this.mLock = paramHdmiControlService.getServiceLock();
  }
  
  static HdmiCecLocalDevice create(HdmiControlService paramHdmiControlService, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return new HdmiCecLocalDeviceTv(paramHdmiControlService);
    }
    return new HdmiCecLocalDevicePlayback(paramHdmiControlService);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private boolean dispatchMessageToAction(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    boolean bool1 = false;
    Iterator localIterator = new ArrayList(this.mActions).iterator();
    while (localIterator.hasNext())
    {
      boolean bool2 = ((HdmiCecFeatureAction)localIterator.next()).processCommand(paramHdmiCecMessage);
      if (!bool1) {
        bool1 = bool2;
      } else {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void handleDisableDeviceTimeout()
  {
    assertRunOnServiceThread();
    Iterator localIterator = this.mActions.iterator();
    while (localIterator.hasNext())
    {
      ((HdmiCecFeatureAction)localIterator.next()).finish(false);
      localIterator.remove();
    }
    if (this.mPendingActionClearedCallback != null) {
      this.mPendingActionClearedCallback.onCleared(this);
    }
  }
  
  static void injectKeyEvent(long paramLong, int paramInt1, int paramInt2, int paramInt3)
  {
    KeyEvent localKeyEvent = KeyEvent.obtain(paramLong, paramLong, paramInt1, paramInt2, paramInt3, 0, -1, 0, 8, 33554433, null);
    InputManager.getInstance().injectInputEvent(localKeyEvent, 0);
    localKeyEvent.recycle();
  }
  
  static boolean isPowerOffOrToggleCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    byte[] arrayOfByte = paramHdmiCecMessage.getParams();
    if (paramHdmiCecMessage.getOpcode() == 68)
    {
      if ((arrayOfByte[0] == 64) || (arrayOfByte[0] == 108)) {}
      while (arrayOfByte[0] == 107) {
        return true;
      }
      return false;
    }
    return false;
  }
  
  static boolean isPowerOnOrToggleCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    byte[] arrayOfByte = paramHdmiCecMessage.getParams();
    if (paramHdmiCecMessage.getOpcode() == 68)
    {
      if ((arrayOfByte[0] == 64) || (arrayOfByte[0] == 109)) {}
      while (arrayOfByte[0] == 107) {
        return true;
      }
      return false;
    }
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void addAndStartAction(HdmiCecFeatureAction paramHdmiCecFeatureAction)
  {
    assertRunOnServiceThread();
    this.mActions.add(paramHdmiCecFeatureAction);
    if ((!this.mService.isPowerStandby()) && (this.mService.isAddressAllocated()))
    {
      paramHdmiCecFeatureAction.start();
      return;
    }
    Slog.i("HdmiCecLocalDevice", "Not ready to start action. Queued for deferred start:" + paramHdmiCecFeatureAction);
  }
  
  protected void assertRunOnServiceThread()
  {
    if (Looper.myLooper() != this.mService.getServiceLooper()) {
      throw new IllegalStateException("Should run on service thread.");
    }
  }
  
  protected boolean canGoToStandby()
  {
    return true;
  }
  
  protected void checkIfPendingActionsCleared()
  {
    if ((this.mActions.isEmpty()) && (this.mPendingActionClearedCallback != null))
    {
      PendingActionClearedCallback localPendingActionClearedCallback = this.mPendingActionClearedCallback;
      this.mPendingActionClearedCallback = null;
      localPendingActionClearedCallback.onCleared(this);
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void clearAddress()
  {
    assertRunOnServiceThread();
    this.mAddress = 15;
  }
  
  protected void disableDevice(boolean paramBoolean, final PendingActionClearedCallback paramPendingActionClearedCallback)
  {
    this.mPendingActionClearedCallback = new PendingActionClearedCallback()
    {
      public void onCleared(HdmiCecLocalDevice paramAnonymousHdmiCecLocalDevice)
      {
        HdmiCecLocalDevice.-get0(HdmiCecLocalDevice.this).removeMessages(1);
        paramPendingActionClearedCallback.onCleared(paramAnonymousHdmiCecLocalDevice);
      }
    };
    this.mHandler.sendMessageDelayed(Message.obtain(this.mHandler, 1), 5000L);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean dispatchMessage(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    int i = paramHdmiCecMessage.getDestination();
    if ((i != this.mAddress) && (i != 15)) {
      return false;
    }
    this.mCecMessageCache.cacheMessage(paramHdmiCecMessage);
    return onMessage(paramHdmiCecMessage);
  }
  
  protected void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    paramIndentingPrintWriter.println("mDeviceType: " + this.mDeviceType);
    paramIndentingPrintWriter.println("mAddress: " + this.mAddress);
    paramIndentingPrintWriter.println("mPreferredAddress: " + this.mPreferredAddress);
    paramIndentingPrintWriter.println("mDeviceInfo: " + this.mDeviceInfo);
    paramIndentingPrintWriter.println("mActiveSource: " + this.mActiveSource);
    paramIndentingPrintWriter.println(String.format("mActiveRoutingPath: 0x%04x", new Object[] { Integer.valueOf(this.mActiveRoutingPath) }));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  <T extends HdmiCecFeatureAction> List<T> getActions(Class<T> paramClass)
  {
    assertRunOnServiceThread();
    Object localObject1 = Collections.emptyList();
    Iterator localIterator = this.mActions.iterator();
    while (localIterator.hasNext())
    {
      HdmiCecFeatureAction localHdmiCecFeatureAction = (HdmiCecFeatureAction)localIterator.next();
      if (localHdmiCecFeatureAction.getClass().equals(paramClass))
      {
        Object localObject2 = localObject1;
        if (((List)localObject1).isEmpty()) {
          localObject2 = new ArrayList();
        }
        ((List)localObject2).add(localHdmiCecFeatureAction);
        localObject1 = localObject2;
      }
    }
    return (List<T>)localObject1;
  }
  
  int getActivePath()
  {
    synchronized (this.mLock)
    {
      int i = this.mActiveRoutingPath;
      return i;
    }
  }
  
  int getActivePortId()
  {
    synchronized (this.mLock)
    {
      int i = this.mService.pathToPortId(this.mActiveRoutingPath);
      return i;
    }
  }
  
  ActiveSource getActiveSource()
  {
    synchronized (this.mLock)
    {
      ActiveSource localActiveSource = this.mActiveSource;
      return localActiveSource;
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  HdmiCecMessageCache getCecMessageCache()
  {
    assertRunOnServiceThread();
    return this.mCecMessageCache;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  HdmiDeviceInfo getDeviceInfo()
  {
    assertRunOnServiceThread();
    return this.mDeviceInfo;
  }
  
  protected abstract int getPreferredAddress();
  
  final HdmiControlService getService()
  {
    return this.mService;
  }
  
  int getType()
  {
    return this.mDeviceType;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleActiveSource(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  final void handleAddressAllocated(int paramInt1, int paramInt2)
  {
    assertRunOnServiceThread();
    this.mPreferredAddress = paramInt1;
    this.mAddress = paramInt1;
    onAddressAllocated(paramInt1, paramInt2);
    setPreferredAddress(paramInt1);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleGetCecVersion(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    int i = this.mService.getCecVersion();
    paramHdmiCecMessage = HdmiCecMessageBuilder.buildCecVersion(paramHdmiCecMessage.getDestination(), paramHdmiCecMessage.getSource(), i);
    this.mService.sendCecCommand(paramHdmiCecMessage);
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleGetMenuLanguage(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    Slog.w("HdmiCecLocalDevice", "Only TV can handle <Get Menu Language>:" + paramHdmiCecMessage.toString());
    return false;
  }
  
  protected boolean handleGiveDevicePowerStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportPowerStatus(this.mAddress, paramHdmiCecMessage.getSource(), this.mService.getPowerStatus()));
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleGiveDeviceVendorId()
  {
    assertRunOnServiceThread();
    int i = this.mService.getVendorId();
    HdmiCecMessage localHdmiCecMessage = HdmiCecMessageBuilder.buildDeviceVendorIdCommand(this.mAddress, i);
    this.mService.sendCecCommand(localHdmiCecMessage);
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleGiveOsdName(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    paramHdmiCecMessage = HdmiCecMessageBuilder.buildSetOsdNameCommand(this.mAddress, paramHdmiCecMessage.getSource(), this.mDeviceInfo.getDisplayName());
    if (paramHdmiCecMessage != null) {
      this.mService.sendCecCommand(paramHdmiCecMessage);
    }
    for (;;)
    {
      return true;
      Slog.w("HdmiCecLocalDevice", "Failed to build <Get Osd Name>:" + this.mDeviceInfo.getDisplayName());
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleGivePhysicalAddress()
  {
    assertRunOnServiceThread();
    int i = this.mService.getPhysicalAddress();
    HdmiCecMessage localHdmiCecMessage = HdmiCecMessageBuilder.buildReportPhysicalAddressCommand(this.mAddress, i, this.mDeviceType);
    this.mService.sendCecCommand(localHdmiCecMessage);
    return true;
  }
  
  protected boolean handleImageViewOn(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleInactiveSource(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleInitiateArc(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleMenuRequest(HdmiCecMessage paramHdmiCecMessage)
  {
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportMenuStatus(this.mAddress, paramHdmiCecMessage.getSource(), 0));
    return true;
  }
  
  protected boolean handleMenuStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleRecordStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleRecordTvScreen(HdmiCecMessage paramHdmiCecMessage)
  {
    this.mService.maySendFeatureAbortCommand(paramHdmiCecMessage, 2);
    return true;
  }
  
  protected boolean handleReportAudioStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleReportPhysicalAddress(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleReportPowerStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleRequestActiveSource(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleRoutingChange(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleRoutingInformation(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleSetMenuLanguage(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    Slog.w("HdmiCecLocalDevice", "Only Playback device can handle <Set Menu Language>:" + paramHdmiCecMessage.toString());
    return false;
  }
  
  protected boolean handleSetOsdName(HdmiCecMessage paramHdmiCecMessage)
  {
    return true;
  }
  
  protected boolean handleSetStreamPath(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleSetSystemAudioMode(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleStandby(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if ((!this.mService.isControlEnabled()) || (this.mService.isProhibitMode())) {}
    while (!this.mService.isPowerOnOrTransient()) {
      return false;
    }
    this.mService.standby();
    return true;
  }
  
  protected boolean handleSystemAudioModeStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleTerminateArc(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleTextViewOn(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleTimerClearedStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  protected boolean handleTimerStatus(HdmiCecMessage paramHdmiCecMessage)
  {
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleUserControlPressed(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    this.mHandler.removeMessages(2);
    if ((this.mService.isPowerOnOrTransient()) && (isPowerOffOrToggleCommand(paramHdmiCecMessage)))
    {
      this.mService.standby();
      return true;
    }
    if ((this.mService.isPowerStandbyOrTransient()) && (isPowerOnOrToggleCommand(paramHdmiCecMessage)))
    {
      this.mService.wakeUp();
      return true;
    }
    long l = SystemClock.uptimeMillis();
    int k = HdmiCecKeycode.cecKeycodeAndParamsToAndroidKey(paramHdmiCecMessage.getParams());
    int j = 0;
    int i = j;
    if (this.mLastKeycode != -1) {
      if (k != this.mLastKeycode) {
        break label153;
      }
    }
    for (i = this.mLastKeyRepeatCount + 1;; i = j)
    {
      this.mLastKeycode = k;
      this.mLastKeyRepeatCount = i;
      if (k == -1) {
        break;
      }
      injectKeyEvent(l, 0, k, i);
      this.mHandler.sendMessageDelayed(Message.obtain(this.mHandler, 2), 550L);
      return true;
      label153:
      injectKeyEvent(l, 1, this.mLastKeycode, 0);
    }
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleUserControlReleased()
  {
    assertRunOnServiceThread();
    this.mHandler.removeMessages(2);
    this.mLastKeyRepeatCount = 0;
    if (this.mLastKeycode != -1)
    {
      injectKeyEvent(SystemClock.uptimeMillis(), 1, this.mLastKeycode, 0);
      this.mLastKeycode = -1;
      return true;
    }
    return false;
  }
  
  protected boolean handleVendorCommand(HdmiCecMessage paramHdmiCecMessage)
  {
    if (!this.mService.invokeVendorCommandListenersOnReceived(this.mDeviceType, paramHdmiCecMessage.getSource(), paramHdmiCecMessage.getDestination(), paramHdmiCecMessage.getParams(), false)) {
      this.mService.maySendFeatureAbortCommand(paramHdmiCecMessage, 1);
    }
    return true;
  }
  
  protected boolean handleVendorCommandWithId(HdmiCecMessage paramHdmiCecMessage)
  {
    byte[] arrayOfByte = paramHdmiCecMessage.getParams();
    if (HdmiUtils.threeBytesToInt(arrayOfByte) == this.mService.getVendorId())
    {
      if (!this.mService.invokeVendorCommandListenersOnReceived(this.mDeviceType, paramHdmiCecMessage.getSource(), paramHdmiCecMessage.getDestination(), arrayOfByte, true)) {
        this.mService.maySendFeatureAbortCommand(paramHdmiCecMessage, 1);
      }
      return true;
    }
    if ((paramHdmiCecMessage.getDestination() != 15) && (paramHdmiCecMessage.getSource() != 15))
    {
      Slog.v("HdmiCecLocalDevice", "Wrong direct vendor command. Replying with <Feature Abort>");
      this.mService.maySendFeatureAbortCommand(paramHdmiCecMessage, 0);
      return true;
    }
    Slog.v("HdmiCecLocalDevice", "Wrong broadcast vendor command. Ignoring");
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  <T extends HdmiCecFeatureAction> boolean hasAction(Class<T> paramClass)
  {
    assertRunOnServiceThread();
    Iterator localIterator = this.mActions.iterator();
    while (localIterator.hasNext()) {
      if (((HdmiCecFeatureAction)localIterator.next()).getClass().equals(paramClass)) {
        return true;
      }
    }
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void init()
  {
    assertRunOnServiceThread();
    this.mPreferredAddress = getPreferredAddress();
    this.mPendingActionClearedCallback = null;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  boolean isAddressOf(int paramInt)
  {
    assertRunOnServiceThread();
    return paramInt == this.mAddress;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  final boolean isConnectedToArcPort(int paramInt)
  {
    assertRunOnServiceThread();
    return this.mService.isConnectedToArcPort(paramInt);
  }
  
  protected boolean isInputReady(int paramInt)
  {
    return true;
  }
  
  protected abstract void onAddressAllocated(int paramInt1, int paramInt2);
  
  void onHotplug(int paramInt, boolean paramBoolean) {}
  
  @HdmiAnnotations.ServiceThreadOnly
  protected final boolean onMessage(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if (dispatchMessageToAction(paramHdmiCecMessage)) {
      return true;
    }
    switch (paramHdmiCecMessage.getOpcode())
    {
    default: 
      return false;
    case 130: 
      return handleActiveSource(paramHdmiCecMessage);
    case 157: 
      return handleInactiveSource(paramHdmiCecMessage);
    case 133: 
      return handleRequestActiveSource(paramHdmiCecMessage);
    case 145: 
      return handleGetMenuLanguage(paramHdmiCecMessage);
    case 50: 
      return handleSetMenuLanguage(paramHdmiCecMessage);
    case 131: 
      return handleGivePhysicalAddress();
    case 70: 
      return handleGiveOsdName(paramHdmiCecMessage);
    case 140: 
      return handleGiveDeviceVendorId();
    case 159: 
      return handleGetCecVersion(paramHdmiCecMessage);
    case 132: 
      return handleReportPhysicalAddress(paramHdmiCecMessage);
    case 128: 
      return handleRoutingChange(paramHdmiCecMessage);
    case 129: 
      return handleRoutingInformation(paramHdmiCecMessage);
    case 192: 
      return handleInitiateArc(paramHdmiCecMessage);
    case 197: 
      return handleTerminateArc(paramHdmiCecMessage);
    case 114: 
      return handleSetSystemAudioMode(paramHdmiCecMessage);
    case 126: 
      return handleSystemAudioModeStatus(paramHdmiCecMessage);
    case 122: 
      return handleReportAudioStatus(paramHdmiCecMessage);
    case 54: 
      return handleStandby(paramHdmiCecMessage);
    case 13: 
      return handleTextViewOn(paramHdmiCecMessage);
    case 4: 
      return handleImageViewOn(paramHdmiCecMessage);
    case 68: 
      return handleUserControlPressed(paramHdmiCecMessage);
    case 69: 
      return handleUserControlReleased();
    case 134: 
      return handleSetStreamPath(paramHdmiCecMessage);
    case 143: 
      return handleGiveDevicePowerStatus(paramHdmiCecMessage);
    case 141: 
      return handleMenuRequest(paramHdmiCecMessage);
    case 142: 
      return handleMenuStatus(paramHdmiCecMessage);
    case 137: 
      return handleVendorCommand(paramHdmiCecMessage);
    case 160: 
      return handleVendorCommandWithId(paramHdmiCecMessage);
    case 71: 
      return handleSetOsdName(paramHdmiCecMessage);
    case 15: 
      return handleRecordTvScreen(paramHdmiCecMessage);
    case 67: 
      return handleTimerClearedStatus(paramHdmiCecMessage);
    case 144: 
      return handleReportPowerStatus(paramHdmiCecMessage);
    case 53: 
      return handleTimerStatus(paramHdmiCecMessage);
    }
    return handleRecordStatus(paramHdmiCecMessage);
  }
  
  protected void onStandby(boolean paramBoolean, int paramInt) {}
  
  @HdmiAnnotations.ServiceThreadOnly
  int pathToPortId(int paramInt)
  {
    assertRunOnServiceThread();
    return this.mService.pathToPortId(paramInt);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void removeAction(HdmiCecFeatureAction paramHdmiCecFeatureAction)
  {
    assertRunOnServiceThread();
    paramHdmiCecFeatureAction.finish(false);
    this.mActions.remove(paramHdmiCecFeatureAction);
    checkIfPendingActionsCleared();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  <T extends HdmiCecFeatureAction> void removeAction(Class<T> paramClass)
  {
    assertRunOnServiceThread();
    removeActionExcept(paramClass, null);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  <T extends HdmiCecFeatureAction> void removeActionExcept(Class<T> paramClass, HdmiCecFeatureAction paramHdmiCecFeatureAction)
  {
    assertRunOnServiceThread();
    Iterator localIterator = this.mActions.iterator();
    while (localIterator.hasNext())
    {
      HdmiCecFeatureAction localHdmiCecFeatureAction = (HdmiCecFeatureAction)localIterator.next();
      if ((localHdmiCecFeatureAction != paramHdmiCecFeatureAction) && (localHdmiCecFeatureAction.getClass().equals(paramClass)))
      {
        localHdmiCecFeatureAction.finish(false);
        localIterator.remove();
      }
    }
    checkIfPendingActionsCleared();
  }
  
  protected void sendKeyEvent(int paramInt, boolean paramBoolean)
  {
    Slog.w("HdmiCecLocalDevice", "sendKeyEvent not implemented");
  }
  
  protected void sendStandby(int paramInt) {}
  
  void sendUserControlPressedAndReleased(int paramInt1, int paramInt2)
  {
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildUserControlPressed(this.mAddress, paramInt1, paramInt2));
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildUserControlReleased(this.mAddress, paramInt1));
  }
  
  void setActivePath(int paramInt)
  {
    synchronized (this.mLock)
    {
      this.mActiveRoutingPath = paramInt;
      this.mService.setActivePortId(pathToPortId(paramInt));
      return;
    }
  }
  
  void setActivePortId(int paramInt)
  {
    setActivePath(this.mService.portIdToPath(paramInt));
  }
  
  void setActiveSource(int paramInt1, int paramInt2)
  {
    synchronized (this.mLock)
    {
      this.mActiveSource.logicalAddress = paramInt1;
      this.mActiveSource.physicalAddress = paramInt2;
      this.mService.setLastInputForMhl(-1);
      return;
    }
  }
  
  void setActiveSource(HdmiDeviceInfo paramHdmiDeviceInfo)
  {
    setActiveSource(paramHdmiDeviceInfo.getLogicalAddress(), paramHdmiDeviceInfo.getPhysicalAddress());
  }
  
  void setActiveSource(ActiveSource paramActiveSource)
  {
    setActiveSource(paramActiveSource.logicalAddress, paramActiveSource.physicalAddress);
  }
  
  void setAutoDeviceOff(boolean paramBoolean) {}
  
  @HdmiAnnotations.ServiceThreadOnly
  void setDeviceInfo(HdmiDeviceInfo paramHdmiDeviceInfo)
  {
    assertRunOnServiceThread();
    this.mDeviceInfo = paramHdmiDeviceInfo;
  }
  
  protected abstract void setPreferredAddress(int paramInt);
  
  @HdmiAnnotations.ServiceThreadOnly
  void startQueuedActions()
  {
    assertRunOnServiceThread();
    Iterator localIterator = this.mActions.iterator();
    while (localIterator.hasNext())
    {
      HdmiCecFeatureAction localHdmiCecFeatureAction = (HdmiCecFeatureAction)localIterator.next();
      if (!localHdmiCecFeatureAction.started())
      {
        Slog.i("HdmiCecLocalDevice", "Starting queued action:" + localHdmiCecFeatureAction);
        localHdmiCecFeatureAction.start();
      }
    }
  }
  
  static class ActiveSource
  {
    int logicalAddress;
    int physicalAddress;
    
    public ActiveSource()
    {
      invalidate();
    }
    
    public ActiveSource(int paramInt1, int paramInt2)
    {
      this.logicalAddress = paramInt1;
      this.physicalAddress = paramInt2;
    }
    
    public static ActiveSource of(int paramInt1, int paramInt2)
    {
      return new ActiveSource(paramInt1, paramInt2);
    }
    
    public static ActiveSource of(ActiveSource paramActiveSource)
    {
      return new ActiveSource(paramActiveSource.logicalAddress, paramActiveSource.physicalAddress);
    }
    
    public boolean equals(int paramInt1, int paramInt2)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (this.logicalAddress == paramInt1)
      {
        bool1 = bool2;
        if (this.physicalAddress == paramInt2) {
          bool1 = true;
        }
      }
      return bool1;
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if ((paramObject instanceof ActiveSource))
      {
        paramObject = (ActiveSource)paramObject;
        boolean bool1 = bool2;
        if (((ActiveSource)paramObject).logicalAddress == this.logicalAddress)
        {
          bool1 = bool2;
          if (((ActiveSource)paramObject).physicalAddress == this.physicalAddress) {
            bool1 = true;
          }
        }
        return bool1;
      }
      return false;
    }
    
    public int hashCode()
    {
      return this.logicalAddress * 29 + this.physicalAddress;
    }
    
    public void invalidate()
    {
      this.logicalAddress = -1;
      this.physicalAddress = 65535;
    }
    
    public boolean isValid()
    {
      return HdmiUtils.isValidAddress(this.logicalAddress);
    }
    
    public String toString()
    {
      StringBuffer localStringBuffer = new StringBuffer();
      if (this.logicalAddress == -1)
      {
        str = "invalid";
        localStringBuffer.append("(").append(str);
        if (this.physicalAddress != 65535) {
          break label86;
        }
      }
      label86:
      for (String str = "invalid";; str = String.format("0x%04x", new Object[] { Integer.valueOf(this.physicalAddress) }))
      {
        localStringBuffer.append(", ").append(str).append(")");
        return localStringBuffer.toString();
        str = String.format("0x%02x", new Object[] { Integer.valueOf(this.logicalAddress) });
        break;
      }
    }
  }
  
  static abstract interface PendingActionClearedCallback
  {
    public abstract void onCleared(HdmiCecLocalDevice paramHdmiCecLocalDevice);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiCecLocalDevice.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */