package com.android.server.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplaySessionInfo;
import android.hardware.display.WifiDisplayStatus;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.view.Display.Mode;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import libcore.util.Objects;

final class WifiDisplayAdapter
  extends DisplayAdapter
{
  private static final String ACTION_DISCONNECT = "android.server.display.wfd.DISCONNECT";
  private static final boolean DEBUG = false;
  private static final String DISPLAY_NAME_PREFIX = "wifi:";
  private static final int MSG_SEND_STATUS_CHANGE_BROADCAST = 1;
  private static final String TAG = "WifiDisplayAdapter";
  private WifiDisplay mActiveDisplay;
  private int mActiveDisplayState;
  private WifiDisplay[] mAvailableDisplays = WifiDisplay.EMPTY_ARRAY;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context arg1, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.server.display.wfd.DISCONNECT")) {}
      synchronized (WifiDisplayAdapter.this.getSyncRoot())
      {
        WifiDisplayAdapter.this.requestDisconnectLocked();
        return;
      }
    }
  };
  private WifiDisplayStatus mCurrentStatus;
  private WifiDisplayController mDisplayController;
  private WifiDisplayDevice mDisplayDevice;
  private WifiDisplay[] mDisplays = WifiDisplay.EMPTY_ARRAY;
  private int mFeatureState;
  private final WifiDisplayHandler mHandler;
  private boolean mPendingStatusChangeBroadcast;
  private final PersistentDataStore mPersistentDataStore;
  private WifiDisplay[] mRememberedDisplays = WifiDisplay.EMPTY_ARRAY;
  private int mScanState;
  private WifiDisplaySessionInfo mSessionInfo;
  private final boolean mSupportsProtectedBuffers;
  private final WifiDisplayController.Listener mWifiDisplayListener = new WifiDisplayController.Listener()
  {
    public void onDisplayChanged(WifiDisplay paramAnonymousWifiDisplay)
    {
      synchronized (WifiDisplayAdapter.this.getSyncRoot())
      {
        paramAnonymousWifiDisplay = WifiDisplayAdapter.-get7(WifiDisplayAdapter.this).applyWifiDisplayAlias(paramAnonymousWifiDisplay);
        if ((WifiDisplayAdapter.-get0(WifiDisplayAdapter.this) != null) && (WifiDisplayAdapter.-get0(WifiDisplayAdapter.this).hasSameAddress(paramAnonymousWifiDisplay)))
        {
          boolean bool = WifiDisplayAdapter.-get0(WifiDisplayAdapter.this).equals(paramAnonymousWifiDisplay);
          if (!bool) {}
        }
        else
        {
          return;
        }
        WifiDisplayAdapter.-set0(WifiDisplayAdapter.this, paramAnonymousWifiDisplay);
        WifiDisplayAdapter.-wrap4(WifiDisplayAdapter.this, paramAnonymousWifiDisplay.getFriendlyDisplayName());
        WifiDisplayAdapter.-wrap5(WifiDisplayAdapter.this);
      }
    }
    
    public void onDisplayConnected(WifiDisplay paramAnonymousWifiDisplay, Surface paramAnonymousSurface, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
    {
      synchronized (WifiDisplayAdapter.this.getSyncRoot())
      {
        paramAnonymousWifiDisplay = WifiDisplayAdapter.-get7(WifiDisplayAdapter.this).applyWifiDisplayAlias(paramAnonymousWifiDisplay);
        WifiDisplayAdapter.-wrap0(WifiDisplayAdapter.this, paramAnonymousWifiDisplay, paramAnonymousSurface, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3);
        if ((WifiDisplayAdapter.-get1(WifiDisplayAdapter.this) != 2) || (WifiDisplayAdapter.-get0(WifiDisplayAdapter.this) == null)) {}
        boolean bool;
        do
        {
          WifiDisplayAdapter.-set1(WifiDisplayAdapter.this, 2);
          WifiDisplayAdapter.-set0(WifiDisplayAdapter.this, paramAnonymousWifiDisplay);
          WifiDisplayAdapter.-wrap5(WifiDisplayAdapter.this);
          return;
          bool = WifiDisplayAdapter.-get0(WifiDisplayAdapter.this).equals(paramAnonymousWifiDisplay);
        } while (!bool);
      }
    }
    
    public void onDisplayConnecting(WifiDisplay paramAnonymousWifiDisplay)
    {
      synchronized (WifiDisplayAdapter.this.getSyncRoot())
      {
        paramAnonymousWifiDisplay = WifiDisplayAdapter.-get7(WifiDisplayAdapter.this).applyWifiDisplayAlias(paramAnonymousWifiDisplay);
        if ((WifiDisplayAdapter.-get1(WifiDisplayAdapter.this) != 1) || (WifiDisplayAdapter.-get0(WifiDisplayAdapter.this) == null)) {}
        boolean bool;
        do
        {
          WifiDisplayAdapter.-set1(WifiDisplayAdapter.this, 1);
          WifiDisplayAdapter.-set0(WifiDisplayAdapter.this, paramAnonymousWifiDisplay);
          WifiDisplayAdapter.-wrap5(WifiDisplayAdapter.this);
          return;
          bool = WifiDisplayAdapter.-get0(WifiDisplayAdapter.this).equals(paramAnonymousWifiDisplay);
        } while (!bool);
      }
    }
    
    public void onDisplayConnectionFailed()
    {
      synchronized (WifiDisplayAdapter.this.getSyncRoot())
      {
        if ((WifiDisplayAdapter.-get1(WifiDisplayAdapter.this) != 0) || (WifiDisplayAdapter.-get0(WifiDisplayAdapter.this) != null))
        {
          WifiDisplayAdapter.-set1(WifiDisplayAdapter.this, 0);
          WifiDisplayAdapter.-set0(WifiDisplayAdapter.this, null);
          WifiDisplayAdapter.-wrap5(WifiDisplayAdapter.this);
        }
        return;
      }
    }
    
    public void onDisplayDisconnected()
    {
      synchronized (WifiDisplayAdapter.this.getSyncRoot())
      {
        WifiDisplayAdapter.-wrap3(WifiDisplayAdapter.this);
        if ((WifiDisplayAdapter.-get1(WifiDisplayAdapter.this) != 0) || (WifiDisplayAdapter.-get0(WifiDisplayAdapter.this) != null))
        {
          WifiDisplayAdapter.-set1(WifiDisplayAdapter.this, 0);
          WifiDisplayAdapter.-set0(WifiDisplayAdapter.this, null);
          WifiDisplayAdapter.-wrap5(WifiDisplayAdapter.this);
        }
        return;
      }
    }
    
    public void onDisplaySessionInfo(WifiDisplaySessionInfo paramAnonymousWifiDisplaySessionInfo)
    {
      synchronized (WifiDisplayAdapter.this.getSyncRoot())
      {
        WifiDisplayAdapter.-set6(WifiDisplayAdapter.this, paramAnonymousWifiDisplaySessionInfo);
        WifiDisplayAdapter.-wrap5(WifiDisplayAdapter.this);
        return;
      }
    }
    
    public void onFeatureStateChanged(int paramAnonymousInt)
    {
      synchronized (WifiDisplayAdapter.this.getSyncRoot())
      {
        if (WifiDisplayAdapter.-get5(WifiDisplayAdapter.this) != paramAnonymousInt)
        {
          WifiDisplayAdapter.-set4(WifiDisplayAdapter.this, paramAnonymousInt);
          WifiDisplayAdapter.-wrap5(WifiDisplayAdapter.this);
        }
        return;
      }
    }
    
    public void onScanFinished()
    {
      synchronized (WifiDisplayAdapter.this.getSyncRoot())
      {
        if (WifiDisplayAdapter.-get8(WifiDisplayAdapter.this) != 0)
        {
          WifiDisplayAdapter.-set5(WifiDisplayAdapter.this, 0);
          WifiDisplayAdapter.-wrap5(WifiDisplayAdapter.this);
        }
        return;
      }
    }
    
    public void onScanResults(WifiDisplay[] paramAnonymousArrayOfWifiDisplay)
    {
      int i;
      int j;
      for (;;)
      {
        synchronized (WifiDisplayAdapter.this.getSyncRoot())
        {
          paramAnonymousArrayOfWifiDisplay = WifiDisplayAdapter.-get7(WifiDisplayAdapter.this).applyWifiDisplayAliases(paramAnonymousArrayOfWifiDisplay);
          if (!Arrays.equals(WifiDisplayAdapter.-get2(WifiDisplayAdapter.this), paramAnonymousArrayOfWifiDisplay)) {
            break label135;
          }
          i = 0;
          break label123;
          if ((i == 0) && (j < paramAnonymousArrayOfWifiDisplay.length))
          {
            if (paramAnonymousArrayOfWifiDisplay[j].canConnect() == WifiDisplayAdapter.-get2(WifiDisplayAdapter.this)[j].canConnect()) {
              break label140;
            }
            i = 1;
            break;
          }
          if (i != 0)
          {
            WifiDisplayAdapter.-set2(WifiDisplayAdapter.this, paramAnonymousArrayOfWifiDisplay);
            WifiDisplayAdapter.-wrap1(WifiDisplayAdapter.this);
            WifiDisplayAdapter.-wrap6(WifiDisplayAdapter.this);
            WifiDisplayAdapter.-wrap5(WifiDisplayAdapter.this);
          }
          return;
        }
        label123:
        j = 0;
      }
      for (;;)
      {
        j += 1;
        break;
        label135:
        i = 1;
        break label123;
        label140:
        i = 0;
      }
    }
    
    public void onScanStarted()
    {
      synchronized (WifiDisplayAdapter.this.getSyncRoot())
      {
        if (WifiDisplayAdapter.-get8(WifiDisplayAdapter.this) != 1)
        {
          WifiDisplayAdapter.-set5(WifiDisplayAdapter.this, 1);
          WifiDisplayAdapter.-wrap5(WifiDisplayAdapter.this);
        }
        return;
      }
    }
  };
  
  public WifiDisplayAdapter(DisplayManagerService.SyncRoot paramSyncRoot, Context paramContext, Handler paramHandler, DisplayAdapter.Listener paramListener, PersistentDataStore paramPersistentDataStore)
  {
    super(paramSyncRoot, paramContext, paramHandler, paramListener, "WifiDisplayAdapter");
    this.mHandler = new WifiDisplayHandler(paramHandler.getLooper());
    this.mPersistentDataStore = paramPersistentDataStore;
    this.mSupportsProtectedBuffers = paramContext.getResources().getBoolean(17956992);
  }
  
  private void addDisplayDeviceLocked(WifiDisplay paramWifiDisplay, Surface paramSurface, int paramInt1, int paramInt2, int paramInt3)
  {
    removeDisplayDeviceLocked();
    if (this.mPersistentDataStore.rememberWifiDisplay(paramWifiDisplay))
    {
      this.mPersistentDataStore.saveIfNeeded();
      updateRememberedDisplaysLocked();
      scheduleStatusChangedBroadcastLocked();
    }
    if ((paramInt3 & 0x1) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      paramInt3 = 64;
      if (bool)
      {
        paramInt3 = 68;
        if (this.mSupportsProtectedBuffers) {
          paramInt3 = 0x44 | 0x8;
        }
      }
      String str = paramWifiDisplay.getFriendlyDisplayName();
      paramWifiDisplay = paramWifiDisplay.getDeviceAddress();
      this.mDisplayDevice = new WifiDisplayDevice(SurfaceControl.createDisplay(str, bool), str, paramInt1, paramInt2, 60.0F, paramInt3, paramWifiDisplay, paramSurface);
      sendDisplayDeviceEventLocked(this.mDisplayDevice, 1);
      return;
    }
  }
  
  private WifiDisplay findAvailableDisplayLocked(String paramString)
  {
    WifiDisplay[] arrayOfWifiDisplay = this.mAvailableDisplays;
    int i = 0;
    int j = arrayOfWifiDisplay.length;
    while (i < j)
    {
      WifiDisplay localWifiDisplay = arrayOfWifiDisplay[i];
      if (localWifiDisplay.getDeviceAddress().equals(paramString)) {
        return localWifiDisplay;
      }
      i += 1;
    }
    return null;
  }
  
  private void fixRememberedDisplayNamesFromAvailableDisplaysLocked()
  {
    boolean bool1 = false;
    int i = 0;
    if (i < this.mRememberedDisplays.length)
    {
      WifiDisplay localWifiDisplay1 = this.mRememberedDisplays[i];
      WifiDisplay localWifiDisplay2 = findAvailableDisplayLocked(localWifiDisplay1.getDeviceAddress());
      boolean bool2 = bool1;
      if (localWifiDisplay2 != null) {
        if (!localWifiDisplay1.equals(localWifiDisplay2)) {
          break label60;
        }
      }
      for (bool2 = bool1;; bool2 = bool1 | this.mPersistentDataStore.rememberWifiDisplay(localWifiDisplay2))
      {
        i += 1;
        bool1 = bool2;
        break;
        label60:
        this.mRememberedDisplays[i] = localWifiDisplay2;
      }
    }
    if (bool1) {
      this.mPersistentDataStore.saveIfNeeded();
    }
  }
  
  private void handleSendStatusChangeBroadcast()
  {
    synchronized (getSyncRoot())
    {
      boolean bool = this.mPendingStatusChangeBroadcast;
      if (!bool) {
        return;
      }
      this.mPendingStatusChangeBroadcast = false;
      Intent localIntent = new Intent("android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED");
      localIntent.addFlags(1073741824);
      localIntent.putExtra("android.hardware.display.extra.WIFI_DISPLAY_STATUS", getWifiDisplayStatusLocked());
      getContext().sendBroadcastAsUser(localIntent, UserHandle.ALL);
      return;
    }
  }
  
  private void removeDisplayDeviceLocked()
  {
    if (this.mDisplayDevice != null)
    {
      this.mDisplayDevice.destroyLocked();
      sendDisplayDeviceEventLocked(this.mDisplayDevice, 3);
      this.mDisplayDevice = null;
    }
  }
  
  private void renameDisplayDeviceLocked(String paramString)
  {
    if ((this.mDisplayDevice == null) || (this.mDisplayDevice.getNameLocked().equals(paramString))) {
      return;
    }
    this.mDisplayDevice.setNameLocked(paramString);
    sendDisplayDeviceEventLocked(this.mDisplayDevice, 2);
  }
  
  private void scheduleStatusChangedBroadcastLocked()
  {
    this.mCurrentStatus = null;
    if (!this.mPendingStatusChangeBroadcast)
    {
      this.mPendingStatusChangeBroadcast = true;
      this.mHandler.sendEmptyMessage(1);
    }
  }
  
  private void updateDisplaysLocked()
  {
    ArrayList localArrayList = new ArrayList(this.mAvailableDisplays.length + this.mRememberedDisplays.length);
    boolean[] arrayOfBoolean = new boolean[this.mAvailableDisplays.length];
    Object localObject1 = this.mRememberedDisplays;
    int n = localObject1.length;
    int i = 0;
    if (i < n)
    {
      Object localObject2 = localObject1[i];
      int m = 0;
      int j = 0;
      for (;;)
      {
        int k = m;
        if (j < this.mAvailableDisplays.length)
        {
          if (((WifiDisplay)localObject2).equals(this.mAvailableDisplays[j]))
          {
            k = 1;
            arrayOfBoolean[j] = true;
          }
        }
        else
        {
          if (k == 0) {
            localArrayList.add(new WifiDisplay(((WifiDisplay)localObject2).getDeviceAddress(), ((WifiDisplay)localObject2).getDeviceName(), ((WifiDisplay)localObject2).getDeviceAlias(), false, false, true));
          }
          i += 1;
          break;
        }
        j += 1;
      }
    }
    i = 0;
    while (i < this.mAvailableDisplays.length)
    {
      localObject1 = this.mAvailableDisplays[i];
      localArrayList.add(new WifiDisplay(((WifiDisplay)localObject1).getDeviceAddress(), ((WifiDisplay)localObject1).getDeviceName(), ((WifiDisplay)localObject1).getDeviceAlias(), true, ((WifiDisplay)localObject1).canConnect(), arrayOfBoolean[i]));
      i += 1;
    }
    this.mDisplays = ((WifiDisplay[])localArrayList.toArray(WifiDisplay.EMPTY_ARRAY));
  }
  
  private void updateRememberedDisplaysLocked()
  {
    this.mRememberedDisplays = this.mPersistentDataStore.getRememberedWifiDisplays();
    this.mActiveDisplay = this.mPersistentDataStore.applyWifiDisplayAlias(this.mActiveDisplay);
    this.mAvailableDisplays = this.mPersistentDataStore.applyWifiDisplayAliases(this.mAvailableDisplays);
    updateDisplaysLocked();
  }
  
  public void dumpLocked(PrintWriter paramPrintWriter)
  {
    super.dumpLocked(paramPrintWriter);
    paramPrintWriter.println("mCurrentStatus=" + getWifiDisplayStatusLocked());
    paramPrintWriter.println("mFeatureState=" + this.mFeatureState);
    paramPrintWriter.println("mScanState=" + this.mScanState);
    paramPrintWriter.println("mActiveDisplayState=" + this.mActiveDisplayState);
    paramPrintWriter.println("mActiveDisplay=" + this.mActiveDisplay);
    paramPrintWriter.println("mDisplays=" + Arrays.toString(this.mDisplays));
    paramPrintWriter.println("mAvailableDisplays=" + Arrays.toString(this.mAvailableDisplays));
    paramPrintWriter.println("mRememberedDisplays=" + Arrays.toString(this.mRememberedDisplays));
    paramPrintWriter.println("mPendingStatusChangeBroadcast=" + this.mPendingStatusChangeBroadcast);
    paramPrintWriter.println("mSupportsProtectedBuffers=" + this.mSupportsProtectedBuffers);
    if (this.mDisplayController == null)
    {
      paramPrintWriter.println("mDisplayController=null");
      return;
    }
    paramPrintWriter.println("mDisplayController:");
    paramPrintWriter = new IndentingPrintWriter(paramPrintWriter, "  ");
    paramPrintWriter.increaseIndent();
    DumpUtils.dumpAsync(getHandler(), this.mDisplayController, paramPrintWriter, "", 200L);
  }
  
  public WifiDisplayStatus getWifiDisplayStatusLocked()
  {
    if (this.mCurrentStatus == null) {
      this.mCurrentStatus = new WifiDisplayStatus(this.mFeatureState, this.mScanState, this.mActiveDisplayState, this.mActiveDisplay, this.mDisplays, this.mSessionInfo);
    }
    return this.mCurrentStatus;
  }
  
  public void registerLocked()
  {
    super.registerLocked();
    updateRememberedDisplaysLocked();
    getHandler().post(new Runnable()
    {
      public void run()
      {
        WifiDisplayAdapter.-set3(WifiDisplayAdapter.this, new WifiDisplayController(WifiDisplayAdapter.this.getContext(), WifiDisplayAdapter.this.getHandler(), WifiDisplayAdapter.-get9(WifiDisplayAdapter.this)));
        WifiDisplayAdapter.this.getContext().registerReceiverAsUser(WifiDisplayAdapter.-get3(WifiDisplayAdapter.this), UserHandle.ALL, new IntentFilter("android.server.display.wfd.DISCONNECT"), null, WifiDisplayAdapter.-get6(WifiDisplayAdapter.this));
      }
    });
  }
  
  public void requestConnectLocked(final String paramString)
  {
    getHandler().post(new Runnable()
    {
      public void run()
      {
        if (WifiDisplayAdapter.-get4(WifiDisplayAdapter.this) != null) {
          WifiDisplayAdapter.-get4(WifiDisplayAdapter.this).requestConnect(paramString);
        }
      }
    });
  }
  
  public void requestDisconnectLocked()
  {
    getHandler().post(new Runnable()
    {
      public void run()
      {
        if (WifiDisplayAdapter.-get4(WifiDisplayAdapter.this) != null) {
          WifiDisplayAdapter.-get4(WifiDisplayAdapter.this).requestDisconnect();
        }
      }
    });
  }
  
  public void requestForgetLocked(String paramString)
  {
    if (this.mPersistentDataStore.forgetWifiDisplay(paramString))
    {
      this.mPersistentDataStore.saveIfNeeded();
      updateRememberedDisplaysLocked();
      scheduleStatusChangedBroadcastLocked();
    }
    if ((this.mActiveDisplay != null) && (this.mActiveDisplay.getDeviceAddress().equals(paramString))) {
      requestDisconnectLocked();
    }
  }
  
  public void requestPauseLocked()
  {
    getHandler().post(new Runnable()
    {
      public void run()
      {
        if (WifiDisplayAdapter.-get4(WifiDisplayAdapter.this) != null) {
          WifiDisplayAdapter.-get4(WifiDisplayAdapter.this).requestPause();
        }
      }
    });
  }
  
  public void requestRenameLocked(String paramString1, String paramString2)
  {
    String str = paramString2;
    if (paramString2 != null)
    {
      paramString2 = paramString2.trim();
      if (!paramString2.isEmpty())
      {
        str = paramString2;
        if (!paramString2.equals(paramString1)) {}
      }
      else
      {
        str = null;
      }
    }
    paramString2 = this.mPersistentDataStore.getRememberedWifiDisplay(paramString1);
    if ((paramString2 == null) || (Objects.equal(paramString2.getDeviceAlias(), str))) {}
    for (;;)
    {
      if ((this.mActiveDisplay != null) && (this.mActiveDisplay.getDeviceAddress().equals(paramString1))) {
        renameDisplayDeviceLocked(this.mActiveDisplay.getFriendlyDisplayName());
      }
      return;
      paramString2 = new WifiDisplay(paramString1, paramString2.getDeviceName(), str, false, false, false);
      if (this.mPersistentDataStore.rememberWifiDisplay(paramString2))
      {
        this.mPersistentDataStore.saveIfNeeded();
        updateRememberedDisplaysLocked();
        scheduleStatusChangedBroadcastLocked();
      }
    }
  }
  
  public void requestResumeLocked()
  {
    getHandler().post(new Runnable()
    {
      public void run()
      {
        if (WifiDisplayAdapter.-get4(WifiDisplayAdapter.this) != null) {
          WifiDisplayAdapter.-get4(WifiDisplayAdapter.this).requestResume();
        }
      }
    });
  }
  
  public void requestStartScanLocked()
  {
    getHandler().post(new Runnable()
    {
      public void run()
      {
        if (WifiDisplayAdapter.-get4(WifiDisplayAdapter.this) != null) {
          WifiDisplayAdapter.-get4(WifiDisplayAdapter.this).requestStartScan();
        }
      }
    });
  }
  
  public void requestStopScanLocked()
  {
    getHandler().post(new Runnable()
    {
      public void run()
      {
        if (WifiDisplayAdapter.-get4(WifiDisplayAdapter.this) != null) {
          WifiDisplayAdapter.-get4(WifiDisplayAdapter.this).requestStopScan();
        }
      }
    });
  }
  
  private final class WifiDisplayDevice
    extends DisplayDevice
  {
    private final String mAddress;
    private final int mFlags;
    private final int mHeight;
    private DisplayDeviceInfo mInfo;
    private final Display.Mode mMode;
    private String mName;
    private final float mRefreshRate;
    private Surface mSurface;
    private final int mWidth;
    
    public WifiDisplayDevice(IBinder paramIBinder, String paramString1, int paramInt1, int paramInt2, float paramFloat, int paramInt3, String paramString2, Surface paramSurface)
    {
      super(paramIBinder, "wifi:" + paramString2);
      this.mName = paramString1;
      this.mWidth = paramInt1;
      this.mHeight = paramInt2;
      this.mRefreshRate = paramFloat;
      this.mFlags = paramInt3;
      this.mAddress = paramString2;
      this.mSurface = paramSurface;
      this.mMode = WifiDisplayAdapter.createMode(paramInt1, paramInt2, paramFloat);
    }
    
    public void destroyLocked()
    {
      if (this.mSurface != null)
      {
        this.mSurface.release();
        this.mSurface = null;
      }
      SurfaceControl.destroyDisplay(getDisplayTokenLocked());
    }
    
    public DisplayDeviceInfo getDisplayDeviceInfoLocked()
    {
      if (this.mInfo == null)
      {
        this.mInfo = new DisplayDeviceInfo();
        this.mInfo.name = this.mName;
        this.mInfo.uniqueId = getUniqueId();
        this.mInfo.width = this.mWidth;
        this.mInfo.height = this.mHeight;
        this.mInfo.modeId = this.mMode.getModeId();
        this.mInfo.defaultModeId = this.mMode.getModeId();
        this.mInfo.supportedModes = new Display.Mode[] { this.mMode };
        this.mInfo.presentationDeadlineNanos = (1000000000L / (int)this.mRefreshRate);
        this.mInfo.flags = this.mFlags;
        this.mInfo.type = 3;
        this.mInfo.address = this.mAddress;
        this.mInfo.touch = 2;
        this.mInfo.setAssumedDensityForExternalDisplay(this.mWidth, this.mHeight);
      }
      return this.mInfo;
    }
    
    public boolean hasStableUniqueId()
    {
      return true;
    }
    
    public void performTraversalInTransactionLocked()
    {
      if (this.mSurface != null) {
        setSurfaceInTransactionLocked(this.mSurface);
      }
    }
    
    public void setNameLocked(String paramString)
    {
      this.mName = paramString;
      this.mInfo = null;
    }
  }
  
  private final class WifiDisplayHandler
    extends Handler
  {
    public WifiDisplayHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
        return;
      }
      WifiDisplayAdapter.-wrap2(WifiDisplayAdapter.this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/WifiDisplayAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */