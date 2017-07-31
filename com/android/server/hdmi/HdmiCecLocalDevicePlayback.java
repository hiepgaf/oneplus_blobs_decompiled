package com.android.server.hdmi;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.hdmi.IHdmiControlCallback;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Slog;
import com.android.internal.app.LocalePicker;
import com.android.internal.app.LocalePicker.LocaleInfo;
import com.android.internal.util.IndentingPrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

final class HdmiCecLocalDevicePlayback
  extends HdmiCecLocalDevice
{
  private static final boolean SET_MENU_LANGUAGE = SystemProperties.getBoolean("ro.hdmi.set_menu_language", false);
  private static final String TAG = "HdmiCecLocalDevicePlayback";
  private static final boolean WAKE_ON_HOTPLUG = SystemProperties.getBoolean("ro.hdmi.wake_on_hotplug", true);
  private boolean mAutoTvOff = this.mService.readBooleanSetting("hdmi_control_auto_device_off_enabled", false);
  private boolean mIsActiveSource = false;
  private ActiveWakeLock mWakeLock;
  
  HdmiCecLocalDevicePlayback(HdmiControlService paramHdmiControlService)
  {
    super(paramHdmiControlService, 4);
    this.mService.writeBooleanSetting("hdmi_control_auto_device_off_enabled", this.mAutoTvOff);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private ActiveWakeLock getWakeLock()
  {
    assertRunOnServiceThread();
    if (this.mWakeLock == null)
    {
      if (!SystemProperties.getBoolean("persist.sys.hdmi.keep_awake", true)) {
        break label37;
      }
      this.mWakeLock = new SystemWakeLock();
    }
    for (;;)
    {
      return this.mWakeLock;
      label37:
      this.mWakeLock = new ActiveWakeLock()
      {
        public void acquire() {}
        
        public boolean isHeld()
        {
          return false;
        }
        
        public void release() {}
      };
      HdmiLogger.debug("No wakelock is used to keep the display on.", new Object[0]);
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  private void invokeCallback(IHdmiControlCallback paramIHdmiControlCallback, int paramInt)
  {
    assertRunOnServiceThread();
    try
    {
      paramIHdmiControlCallback.onComplete(paramInt);
      return;
    }
    catch (RemoteException paramIHdmiControlCallback)
    {
      Slog.e("HdmiCecLocalDevicePlayback", "Invoking callback failed:" + paramIHdmiControlCallback);
    }
  }
  
  private void mayResetActiveSource(int paramInt)
  {
    if (paramInt != this.mService.getPhysicalAddress()) {
      setActiveSource(false);
    }
  }
  
  private void maySendActiveSource(int paramInt)
  {
    if (this.mIsActiveSource)
    {
      this.mService.sendCecCommand(HdmiCecMessageBuilder.buildActiveSource(this.mAddress, this.mService.getPhysicalAddress()));
      this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportMenuStatus(this.mAddress, paramInt, 0));
    }
  }
  
  private void maySetActiveSource(int paramInt)
  {
    if (paramInt == this.mService.getPhysicalAddress()) {}
    for (boolean bool = true;; bool = false)
    {
      setActiveSource(bool);
      return;
    }
  }
  
  private void wakeUpIfActiveSource()
  {
    if (!this.mIsActiveSource) {
      return;
    }
    if ((!this.mService.isPowerStandbyOrTransient()) && (this.mService.getPowerManager().isScreenOn())) {
      return;
    }
    this.mService.wakeUp();
  }
  
  protected boolean canGoToStandby()
  {
    return !getWakeLock().isHeld();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected void disableDevice(boolean paramBoolean, HdmiCecLocalDevice.PendingActionClearedCallback paramPendingActionClearedCallback)
  {
    super.disableDevice(paramBoolean, paramPendingActionClearedCallback);
    assertRunOnServiceThread();
    if ((!paramBoolean) && (this.mIsActiveSource)) {
      this.mService.sendCecCommand(HdmiCecMessageBuilder.buildInactiveSource(this.mAddress, this.mService.getPhysicalAddress()));
    }
    setActiveSource(false);
    checkIfPendingActionsCleared();
  }
  
  protected void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    super.dump(paramIndentingPrintWriter);
    paramIndentingPrintWriter.println("mIsActiveSource: " + this.mIsActiveSource);
    paramIndentingPrintWriter.println("mAutoTvOff:" + this.mAutoTvOff);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected int getPreferredAddress()
  {
    assertRunOnServiceThread();
    return SystemProperties.getInt("persist.sys.hdmi.addr.playback", 15);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleActiveSource(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    mayResetActiveSource(HdmiUtils.twoBytesToInt(paramHdmiCecMessage.getParams()));
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleRequestActiveSource(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    maySendActiveSource(paramHdmiCecMessage.getSource());
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleRoutingChange(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    maySetActiveSource(HdmiUtils.twoBytesToInt(paramHdmiCecMessage.getParams(), 2));
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleRoutingInformation(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    maySetActiveSource(HdmiUtils.twoBytesToInt(paramHdmiCecMessage.getParams()));
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleSetMenuLanguage(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    if (!SET_MENU_LANGUAGE) {
      return false;
    }
    try
    {
      paramHdmiCecMessage = new String(paramHdmiCecMessage.getParams(), 0, 3, "US-ASCII");
      if (this.mService.getContext().getResources().getConfiguration().locale.getISO3Language().equals(paramHdmiCecMessage)) {
        return true;
      }
      Iterator localIterator = LocalePicker.getAllAssetLocales(this.mService.getContext(), false).iterator();
      while (localIterator.hasNext())
      {
        LocalePicker.LocaleInfo localLocaleInfo = (LocalePicker.LocaleInfo)localIterator.next();
        if (localLocaleInfo.getLocale().getISO3Language().equals(paramHdmiCecMessage))
        {
          LocalePicker.updateLocale(localLocaleInfo.getLocale());
          return true;
        }
      }
      Slog.w("HdmiCecLocalDevicePlayback", "Can't handle <Set Menu Language> of " + paramHdmiCecMessage);
      return false;
    }
    catch (UnsupportedEncodingException paramHdmiCecMessage)
    {
      Slog.w("HdmiCecLocalDevicePlayback", "Can't handle <Set Menu Language>", paramHdmiCecMessage);
    }
    return false;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleSetStreamPath(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    maySetActiveSource(HdmiUtils.twoBytesToInt(paramHdmiCecMessage.getParams()));
    maySendActiveSource(paramHdmiCecMessage.getSource());
    wakeUpIfActiveSource();
    return true;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected boolean handleUserControlPressed(HdmiCecMessage paramHdmiCecMessage)
  {
    assertRunOnServiceThread();
    wakeUpIfActiveSource();
    return super.handleUserControlPressed(paramHdmiCecMessage);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected void onAddressAllocated(int paramInt1, int paramInt2)
  {
    assertRunOnServiceThread();
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildReportPhysicalAddressCommand(this.mAddress, this.mService.getPhysicalAddress(), this.mDeviceType));
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildDeviceVendorIdCommand(this.mAddress, this.mService.getVendorId()));
    startQueuedActions();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void onHotplug(int paramInt, boolean paramBoolean)
  {
    assertRunOnServiceThread();
    this.mCecMessageCache.flushAll();
    if ((WAKE_ON_HOTPLUG) && (paramBoolean) && (this.mService.isPowerStandbyOrTransient())) {
      this.mService.wakeUp();
    }
    if (!paramBoolean) {
      getWakeLock().release();
    }
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected void onStandby(boolean paramBoolean, int paramInt)
  {
    assertRunOnServiceThread();
    if ((this.mService.isControlEnabled()) && (!paramBoolean) && (this.mAutoTvOff)) {}
    switch (paramInt)
    {
    default: 
      return;
      return;
    case 0: 
      this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(this.mAddress, 0));
      return;
    }
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(this.mAddress, 15));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void oneTouchPlay(IHdmiControlCallback paramIHdmiControlCallback)
  {
    assertRunOnServiceThread();
    Object localObject = getActions(OneTouchPlayAction.class);
    if (!((List)localObject).isEmpty())
    {
      Slog.i("HdmiCecLocalDevicePlayback", "oneTouchPlay already in progress");
      ((OneTouchPlayAction)((List)localObject).get(0)).addCallback(paramIHdmiControlCallback);
      return;
    }
    localObject = OneTouchPlayAction.create(this, 0, paramIHdmiControlCallback);
    if (localObject == null)
    {
      Slog.w("HdmiCecLocalDevicePlayback", "Cannot initiate oneTouchPlay");
      invokeCallback(paramIHdmiControlCallback, 5);
      return;
    }
    addAndStartAction((HdmiCecFeatureAction)localObject);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void queryDisplayStatus(IHdmiControlCallback paramIHdmiControlCallback)
  {
    assertRunOnServiceThread();
    Object localObject = getActions(DevicePowerStatusAction.class);
    if (!((List)localObject).isEmpty())
    {
      Slog.i("HdmiCecLocalDevicePlayback", "queryDisplayStatus already in progress");
      ((DevicePowerStatusAction)((List)localObject).get(0)).addCallback(paramIHdmiControlCallback);
      return;
    }
    localObject = DevicePowerStatusAction.create(this, 0, paramIHdmiControlCallback);
    if (localObject == null)
    {
      Slog.w("HdmiCecLocalDevicePlayback", "Cannot initiate queryDisplayStatus");
      invokeCallback(paramIHdmiControlCallback, 5);
      return;
    }
    addAndStartAction((HdmiCecFeatureAction)localObject);
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected void sendStandby(int paramInt)
  {
    assertRunOnServiceThread();
    this.mService.sendCecCommand(HdmiCecMessageBuilder.buildStandby(this.mAddress, 0));
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void setActiveSource(boolean paramBoolean)
  {
    assertRunOnServiceThread();
    this.mIsActiveSource = paramBoolean;
    if (paramBoolean)
    {
      getWakeLock().acquire();
      return;
    }
    getWakeLock().release();
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  void setAutoDeviceOff(boolean paramBoolean)
  {
    assertRunOnServiceThread();
    this.mAutoTvOff = paramBoolean;
  }
  
  @HdmiAnnotations.ServiceThreadOnly
  protected void setPreferredAddress(int paramInt)
  {
    assertRunOnServiceThread();
    SystemProperties.set("persist.sys.hdmi.addr.playback", String.valueOf(paramInt));
  }
  
  private static abstract interface ActiveWakeLock
  {
    public abstract void acquire();
    
    public abstract boolean isHeld();
    
    public abstract void release();
  }
  
  private class SystemWakeLock
    implements HdmiCecLocalDevicePlayback.ActiveWakeLock
  {
    private final PowerManager.WakeLock mWakeLock = HdmiCecLocalDevicePlayback.this.mService.getPowerManager().newWakeLock(1, "HdmiCecLocalDevicePlayback");
    
    public SystemWakeLock()
    {
      this.mWakeLock.setReferenceCounted(false);
    }
    
    public void acquire()
    {
      this.mWakeLock.acquire();
      HdmiLogger.debug("active source: %b. Wake lock acquired", new Object[] { Boolean.valueOf(HdmiCecLocalDevicePlayback.-get0(HdmiCecLocalDevicePlayback.this)) });
    }
    
    public boolean isHeld()
    {
      return this.mWakeLock.isHeld();
    }
    
    public void release()
    {
      this.mWakeLock.release();
      HdmiLogger.debug("Wake lock released", new Object[0]);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/hdmi/HdmiCecLocalDevicePlayback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */