package com.android.server.display;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.Display.HdrCapabilities;
import android.view.Display.Mode;
import android.view.DisplayEventReceiver;
import android.view.SurfaceControl;
import android.view.SurfaceControl.PhysicalDisplayInfo;
import com.android.server.LocalServices;
import com.android.server.lights.Light;
import com.android.server.lights.LightsManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class LocalDisplayAdapter
  extends DisplayAdapter
{
  private static final int[] BUILT_IN_DISPLAY_IDS_TO_SCAN = { 0, 1, 2 };
  private static final boolean DEBUG = false;
  private static final String PROPERTY_EMULATOR_CIRCULAR = "ro.emulator.circular";
  private static final String TAG = "LocalDisplayAdapter";
  private static final String UNIQUE_ID_PREFIX = "local:";
  private final SparseArray<LocalDisplayDevice> mDevices = new SparseArray();
  private HotplugDisplayEventReceiver mHotplugReceiver;
  
  public LocalDisplayAdapter(DisplayManagerService.SyncRoot paramSyncRoot, Context paramContext, Handler paramHandler, DisplayAdapter.Listener paramListener)
  {
    super(paramSyncRoot, paramContext, paramHandler, paramListener, "LocalDisplayAdapter");
  }
  
  static int getPowerModeForState(int paramInt)
  {
    switch (paramInt)
    {
    case 2: 
    default: 
      return 2;
    case 1: 
      return 0;
    case 3: 
      return 1;
    }
    return 3;
  }
  
  private void tryConnectDisplayLocked(int paramInt)
  {
    Object localObject = SurfaceControl.getBuiltInDisplay(paramInt);
    SurfaceControl.PhysicalDisplayInfo[] arrayOfPhysicalDisplayInfo;
    int k;
    int i;
    int[] arrayOfInt;
    LocalDisplayDevice localLocalDisplayDevice;
    if (localObject != null)
    {
      arrayOfPhysicalDisplayInfo = SurfaceControl.getDisplayConfigs((IBinder)localObject);
      if (arrayOfPhysicalDisplayInfo == null)
      {
        Slog.w("LocalDisplayAdapter", "No valid configs found for display device " + paramInt);
        return;
      }
      k = SurfaceControl.getActiveConfig((IBinder)localObject);
      if (k < 0)
      {
        Slog.w("LocalDisplayAdapter", "No active config found for display device " + paramInt);
        return;
      }
      int j = SurfaceControl.getActiveColorMode((IBinder)localObject);
      i = j;
      if (j < 0)
      {
        Slog.w("LocalDisplayAdapter", "Unable to get active color mode for display device " + paramInt);
        i = -1;
      }
      arrayOfInt = SurfaceControl.getDisplayColorModes((IBinder)localObject);
      localLocalDisplayDevice = (LocalDisplayDevice)this.mDevices.get(paramInt);
      if (localLocalDisplayDevice != null) {
        break label189;
      }
      localObject = new LocalDisplayDevice((IBinder)localObject, paramInt, arrayOfPhysicalDisplayInfo, k, arrayOfInt, i);
      this.mDevices.put(paramInt, localObject);
      sendDisplayDeviceEventLocked((DisplayDevice)localObject, 1);
    }
    label189:
    while (!localLocalDisplayDevice.updatePhysicalDisplayInfoLocked(arrayOfPhysicalDisplayInfo, k, arrayOfInt, i)) {
      return;
    }
    sendDisplayDeviceEventLocked(localLocalDisplayDevice, 2);
  }
  
  private void tryDisconnectDisplayLocked(int paramInt)
  {
    LocalDisplayDevice localLocalDisplayDevice = (LocalDisplayDevice)this.mDevices.get(paramInt);
    if (localLocalDisplayDevice != null)
    {
      this.mDevices.remove(paramInt);
      sendDisplayDeviceEventLocked(localLocalDisplayDevice, 3);
    }
  }
  
  public void registerLocked()
  {
    super.registerLocked();
    this.mHotplugReceiver = new HotplugDisplayEventReceiver(getHandler().getLooper());
    int[] arrayOfInt = BUILT_IN_DISPLAY_IDS_TO_SCAN;
    int i = 0;
    int j = arrayOfInt.length;
    while (i < j)
    {
      tryConnectDisplayLocked(arrayOfInt[i]);
      i += 1;
    }
  }
  
  private static final class DisplayModeRecord
  {
    public final Display.Mode mMode;
    
    public DisplayModeRecord(SurfaceControl.PhysicalDisplayInfo paramPhysicalDisplayInfo)
    {
      this.mMode = LocalDisplayAdapter.createMode(paramPhysicalDisplayInfo.width, paramPhysicalDisplayInfo.height, paramPhysicalDisplayInfo.refreshRate);
    }
    
    public boolean hasMatchingMode(SurfaceControl.PhysicalDisplayInfo paramPhysicalDisplayInfo)
    {
      boolean bool2 = false;
      int i = Float.floatToIntBits(this.mMode.getRefreshRate());
      int j = Float.floatToIntBits(paramPhysicalDisplayInfo.refreshRate);
      boolean bool1 = bool2;
      if (this.mMode.getPhysicalWidth() == paramPhysicalDisplayInfo.width)
      {
        bool1 = bool2;
        if (this.mMode.getPhysicalHeight() == paramPhysicalDisplayInfo.height)
        {
          bool1 = bool2;
          if (i == j) {
            bool1 = true;
          }
        }
      }
      return bool1;
    }
    
    public String toString()
    {
      return "DisplayModeRecord{mMode=" + this.mMode + "}";
    }
  }
  
  private final class HotplugDisplayEventReceiver
    extends DisplayEventReceiver
  {
    public HotplugDisplayEventReceiver(Looper paramLooper)
    {
      super();
    }
    
    public void onHotplug(long paramLong, int paramInt, boolean paramBoolean)
    {
      localSyncRoot = LocalDisplayAdapter.this.getSyncRoot();
      if (paramBoolean) {}
      for (;;)
      {
        try
        {
          LocalDisplayAdapter.-wrap0(LocalDisplayAdapter.this, paramInt);
          return;
        }
        finally {}
        LocalDisplayAdapter.-wrap1(LocalDisplayAdapter.this, paramInt);
      }
    }
  }
  
  private final class LocalDisplayDevice
    extends DisplayDevice
  {
    private int mActiveColorMode;
    private boolean mActiveColorModeInvalid;
    private int mActiveModeId;
    private boolean mActiveModeInvalid;
    private int mActivePhysIndex;
    private final Light mBacklight;
    private int mBrightness = -1;
    private final int mBuiltInDisplayId;
    private int mDefaultModeId;
    private SurfaceControl.PhysicalDisplayInfo[] mDisplayInfos;
    private boolean mHavePendingChanges;
    private Display.HdrCapabilities mHdrCapabilities;
    private DisplayDeviceInfo mInfo;
    private int mState = 0;
    private final ArrayList<Integer> mSupportedColorModes = new ArrayList();
    private final SparseArray<LocalDisplayAdapter.DisplayModeRecord> mSupportedModes = new SparseArray();
    
    static
    {
      if (LocalDisplayDevice.class.desiredAssertionStatus()) {}
      for (boolean bool = false;; bool = true)
      {
        -assertionsDisabled = bool;
        return;
      }
    }
    
    public LocalDisplayDevice(IBinder paramIBinder, int paramInt1, SurfaceControl.PhysicalDisplayInfo[] paramArrayOfPhysicalDisplayInfo, int paramInt2, int[] paramArrayOfInt, int paramInt3)
    {
      super(paramIBinder, "local:" + paramInt1);
      this.mBuiltInDisplayId = paramInt1;
      updatePhysicalDisplayInfoLocked(paramArrayOfPhysicalDisplayInfo, paramInt2, paramArrayOfInt, paramInt3);
      updateColorModesLocked(paramArrayOfInt, paramInt3);
      if (this.mBuiltInDisplayId == 0) {}
      for (this.mBacklight = ((LightsManager)LocalServices.getService(LightsManager.class)).getLight(0);; this.mBacklight = null)
      {
        this.mHdrCapabilities = SurfaceControl.getHdrCapabilities(paramIBinder);
        return;
      }
    }
    
    private int findDisplayInfoIndexLocked(int paramInt)
    {
      LocalDisplayAdapter.DisplayModeRecord localDisplayModeRecord = (LocalDisplayAdapter.DisplayModeRecord)this.mSupportedModes.get(paramInt);
      if (localDisplayModeRecord != null)
      {
        paramInt = 0;
        while (paramInt < this.mDisplayInfos.length)
        {
          if (localDisplayModeRecord.hasMatchingMode(this.mDisplayInfos[paramInt])) {
            return paramInt;
          }
          paramInt += 1;
        }
      }
      return -1;
    }
    
    private LocalDisplayAdapter.DisplayModeRecord findDisplayModeRecord(SurfaceControl.PhysicalDisplayInfo paramPhysicalDisplayInfo)
    {
      int i = 0;
      while (i < this.mSupportedModes.size())
      {
        LocalDisplayAdapter.DisplayModeRecord localDisplayModeRecord = (LocalDisplayAdapter.DisplayModeRecord)this.mSupportedModes.valueAt(i);
        if (localDisplayModeRecord.hasMatchingMode(paramPhysicalDisplayInfo)) {
          return localDisplayModeRecord;
        }
        i += 1;
      }
      return null;
    }
    
    private boolean updateColorModesLocked(int[] paramArrayOfInt, int paramInt)
    {
      ArrayList localArrayList = new ArrayList();
      paramInt = 0;
      if (paramArrayOfInt == null) {
        return false;
      }
      int j = paramArrayOfInt.length;
      int i = 0;
      while (i < j)
      {
        int k = paramArrayOfInt[i];
        if (!this.mSupportedColorModes.contains(Integer.valueOf(k))) {
          paramInt = 1;
        }
        localArrayList.add(Integer.valueOf(k));
        i += 1;
      }
      if (localArrayList.size() == this.mSupportedColorModes.size()) {}
      while (paramInt == 0)
      {
        return false;
        paramInt = 1;
      }
      this.mHavePendingChanges = true;
      this.mSupportedColorModes.clear();
      this.mSupportedColorModes.addAll(localArrayList);
      Collections.sort(this.mSupportedColorModes);
      if (!this.mSupportedColorModes.contains(Integer.valueOf(this.mActiveColorMode)))
      {
        if (this.mActiveColorMode != 0)
        {
          Slog.w("LocalDisplayAdapter", "Active color mode no longer available, reverting to default mode.");
          this.mActiveColorMode = 0;
          this.mActiveColorModeInvalid = true;
        }
      }
      else {
        return true;
      }
      if (!this.mSupportedColorModes.isEmpty())
      {
        Slog.e("LocalDisplayAdapter", "Default and active color mode is no longer available! Reverting to first available mode.");
        this.mActiveColorMode = ((Integer)this.mSupportedColorModes.get(0)).intValue();
        this.mActiveColorModeInvalid = true;
        return true;
      }
      Slog.e("LocalDisplayAdapter", "No color modes available!");
      return true;
    }
    
    private void updateDeviceInfoLocked()
    {
      this.mInfo = null;
      LocalDisplayAdapter.this.sendDisplayDeviceEventLocked(this, 2);
    }
    
    public void applyPendingDisplayDeviceInfoChangesLocked()
    {
      if (this.mHavePendingChanges)
      {
        this.mInfo = null;
        this.mHavePendingChanges = false;
      }
    }
    
    public void dumpLocked(PrintWriter paramPrintWriter)
    {
      super.dumpLocked(paramPrintWriter);
      paramPrintWriter.println("mBuiltInDisplayId=" + this.mBuiltInDisplayId);
      paramPrintWriter.println("mActivePhysIndex=" + this.mActivePhysIndex);
      paramPrintWriter.println("mActiveModeId=" + this.mActiveModeId);
      paramPrintWriter.println("mActiveColorMode=" + this.mActiveColorMode);
      paramPrintWriter.println("mState=" + Display.stateToString(this.mState));
      paramPrintWriter.println("mBrightness=" + this.mBrightness);
      paramPrintWriter.println("mBacklight=" + this.mBacklight);
      paramPrintWriter.println("mDisplayInfos=");
      int i = 0;
      while (i < this.mDisplayInfos.length)
      {
        paramPrintWriter.println("  " + this.mDisplayInfos[i]);
        i += 1;
      }
      paramPrintWriter.println("mSupportedModes=");
      i = 0;
      while (i < this.mSupportedModes.size())
      {
        paramPrintWriter.println("  " + this.mSupportedModes.valueAt(i));
        i += 1;
      }
      paramPrintWriter.print("mSupportedColorModes=[");
      i = 0;
      while (i < this.mSupportedColorModes.size())
      {
        if (i != 0) {
          paramPrintWriter.print(", ");
        }
        paramPrintWriter.print(this.mSupportedColorModes.get(i));
        i += 1;
      }
      paramPrintWriter.println("]");
    }
    
    public DisplayDeviceInfo getDisplayDeviceInfoLocked()
    {
      Object localObject1;
      Object localObject2;
      DisplayDeviceInfo localDisplayDeviceInfo;
      if (this.mInfo == null)
      {
        localObject1 = this.mDisplayInfos[this.mActivePhysIndex];
        this.mInfo = new DisplayDeviceInfo();
        this.mInfo.width = ((SurfaceControl.PhysicalDisplayInfo)localObject1).width;
        this.mInfo.height = ((SurfaceControl.PhysicalDisplayInfo)localObject1).height;
        this.mInfo.modeId = this.mActiveModeId;
        this.mInfo.defaultModeId = this.mDefaultModeId;
        this.mInfo.supportedModes = new Display.Mode[this.mSupportedModes.size()];
        int i = 0;
        while (i < this.mSupportedModes.size())
        {
          localObject2 = (LocalDisplayAdapter.DisplayModeRecord)this.mSupportedModes.valueAt(i);
          this.mInfo.supportedModes[i] = ((LocalDisplayAdapter.DisplayModeRecord)localObject2).mMode;
          i += 1;
        }
        this.mInfo.colorMode = this.mActiveColorMode;
        this.mInfo.supportedColorModes = new int[this.mSupportedColorModes.size()];
        i = 0;
        while (i < this.mSupportedColorModes.size())
        {
          this.mInfo.supportedColorModes[i] = ((Integer)this.mSupportedColorModes.get(i)).intValue();
          i += 1;
        }
        this.mInfo.hdrCapabilities = this.mHdrCapabilities;
        this.mInfo.appVsyncOffsetNanos = ((SurfaceControl.PhysicalDisplayInfo)localObject1).appVsyncOffsetNanos;
        this.mInfo.presentationDeadlineNanos = ((SurfaceControl.PhysicalDisplayInfo)localObject1).presentationDeadlineNanos;
        this.mInfo.state = this.mState;
        this.mInfo.uniqueId = getUniqueId();
        if (((SurfaceControl.PhysicalDisplayInfo)localObject1).secure) {
          this.mInfo.flags = 12;
        }
        localObject2 = LocalDisplayAdapter.this.getContext().getResources();
        if (this.mBuiltInDisplayId != 0) {
          break label431;
        }
        this.mInfo.name = ((Resources)localObject2).getString(17040667);
        localDisplayDeviceInfo = this.mInfo;
        localDisplayDeviceInfo.flags |= 0x3;
        if ((((Resources)localObject2).getBoolean(17957033)) || ((Build.IS_EMULATOR) && (SystemProperties.getBoolean("ro.emulator.circular", false))))
        {
          localObject2 = this.mInfo;
          ((DisplayDeviceInfo)localObject2).flags |= 0x100;
        }
        this.mInfo.type = 1;
        this.mInfo.densityDpi = ((int)(((SurfaceControl.PhysicalDisplayInfo)localObject1).density * 160.0F + 0.5F));
        this.mInfo.xDpi = ((SurfaceControl.PhysicalDisplayInfo)localObject1).xDpi;
        this.mInfo.yDpi = ((SurfaceControl.PhysicalDisplayInfo)localObject1).yDpi;
        this.mInfo.touch = 1;
      }
      for (;;)
      {
        return this.mInfo;
        label431:
        this.mInfo.type = 2;
        localDisplayDeviceInfo = this.mInfo;
        localDisplayDeviceInfo.flags |= 0x40;
        this.mInfo.name = LocalDisplayAdapter.this.getContext().getResources().getString(17040668);
        this.mInfo.touch = 2;
        this.mInfo.setAssumedDensityForExternalDisplay(((SurfaceControl.PhysicalDisplayInfo)localObject1).width, ((SurfaceControl.PhysicalDisplayInfo)localObject1).height);
        if ("portrait".equals(SystemProperties.get("persist.demo.hdmirotation"))) {
          this.mInfo.rotation = 3;
        }
        if (SystemProperties.getBoolean("persist.demo.hdmirotates", false))
        {
          localObject1 = this.mInfo;
          ((DisplayDeviceInfo)localObject1).flags |= 0x2;
        }
        if (!((Resources)localObject2).getBoolean(17956989))
        {
          localObject1 = this.mInfo;
          ((DisplayDeviceInfo)localObject1).flags |= 0x80;
        }
      }
    }
    
    public boolean hasStableUniqueId()
    {
      return true;
    }
    
    public boolean requestColorModeInTransactionLocked(int paramInt)
    {
      if (this.mActiveColorMode == paramInt) {
        return false;
      }
      if (!this.mSupportedColorModes.contains(Integer.valueOf(paramInt)))
      {
        Slog.w("LocalDisplayAdapter", "Unable to find color mode " + paramInt + ", ignoring request.");
        return false;
      }
      SurfaceControl.setActiveColorMode(getDisplayTokenLocked(), paramInt);
      this.mActiveColorMode = paramInt;
      this.mActiveColorModeInvalid = false;
      return true;
    }
    
    public void requestDisplayModesInTransactionLocked(int paramInt1, int paramInt2)
    {
      if ((requestModeInTransactionLocked(paramInt2)) || (requestColorModeInTransactionLocked(paramInt1))) {
        updateDeviceInfoLocked();
      }
    }
    
    public Runnable requestDisplayStateLocked(final int paramInt1, final int paramInt2)
    {
      final int j = 1;
      int i;
      if (!-assertionsDisabled)
      {
        i = j;
        if (paramInt1 == 1) {
          if (paramInt2 != 0) {
            break label36;
          }
        }
        label36:
        for (i = j; i == 0; i = 0) {
          throw new AssertionError();
        }
      }
      if (this.mState != paramInt1)
      {
        i = 1;
        if ((this.mBrightness == paramInt2) || (this.mBacklight == null)) {
          break label143;
        }
      }
      label143:
      for (final boolean bool = true;; bool = false)
      {
        if ((i == 0) && (!bool)) {
          break label149;
        }
        j = this.mBuiltInDisplayId;
        final IBinder localIBinder = getDisplayTokenLocked();
        final int k = this.mState;
        if (i != 0)
        {
          this.mState = paramInt1;
          updateDeviceInfoLocked();
        }
        if (bool) {
          this.mBrightness = paramInt2;
        }
        new Runnable()
        {
          private void setDisplayBrightness(int paramAnonymousInt)
          {
            Trace.traceBegin(131072L, "setDisplayBrightness(id=" + j + ", brightness=" + paramAnonymousInt + ")");
            try
            {
              LocalDisplayAdapter.LocalDisplayDevice.-get0(LocalDisplayAdapter.LocalDisplayDevice.this).setBrightness(paramAnonymousInt);
              return;
            }
            finally
            {
              Trace.traceEnd(131072L);
            }
          }
          
          private void setDisplayState(int paramAnonymousInt)
          {
            Trace.traceBegin(131072L, "setDisplayState(id=" + j + ", state=" + Display.stateToString(paramAnonymousInt) + ")");
            try
            {
              paramAnonymousInt = LocalDisplayAdapter.getPowerModeForState(paramAnonymousInt);
              SurfaceControl.setDisplayPowerMode(localIBinder, paramAnonymousInt);
              return;
            }
            finally
            {
              Trace.traceEnd(131072L);
            }
          }
          
          public void run()
          {
            int i = k;
            if ((Display.isSuspendedState(k)) || (k == 0))
            {
              if (Display.isSuspendedState(paramInt1)) {
                break label77;
              }
              setDisplayState(paramInt1);
            }
            for (i = paramInt1;; i = 3)
            {
              if (bool) {
                setDisplayBrightness(paramInt2);
              }
              if (paramInt1 != i) {
                setDisplayState(paramInt1);
              }
              return;
              label77:
              if ((paramInt1 != 4) && (k != 4)) {
                break;
              }
              setDisplayState(3);
            }
          }
        };
        i = 0;
        break;
      }
      label149:
      return null;
    }
    
    public boolean requestModeInTransactionLocked(int paramInt)
    {
      int i;
      if (paramInt == 0) {
        i = this.mDefaultModeId;
      }
      int j;
      for (;;)
      {
        int k = findDisplayInfoIndexLocked(i);
        j = k;
        paramInt = i;
        if (k < 0)
        {
          Slog.w("LocalDisplayAdapter", "Requested mode ID " + i + " not available," + " trying with default mode ID");
          paramInt = this.mDefaultModeId;
          j = findDisplayInfoIndexLocked(paramInt);
        }
        if (this.mActivePhysIndex != j) {
          break;
        }
        return false;
        i = paramInt;
        if (this.mSupportedModes.indexOfKey(paramInt) < 0)
        {
          Slog.w("LocalDisplayAdapter", "Requested mode " + paramInt + " is not supported by this display," + " reverting to default display mode.");
          i = this.mDefaultModeId;
        }
      }
      SurfaceControl.setActiveConfig(getDisplayTokenLocked(), j);
      this.mActivePhysIndex = j;
      this.mActiveModeId = paramInt;
      this.mActiveModeInvalid = false;
      return true;
    }
    
    public boolean updatePhysicalDisplayInfoLocked(SurfaceControl.PhysicalDisplayInfo[] paramArrayOfPhysicalDisplayInfo, int paramInt1, int[] paramArrayOfInt, int paramInt2)
    {
      this.mDisplayInfos = ((SurfaceControl.PhysicalDisplayInfo[])Arrays.copyOf(paramArrayOfPhysicalDisplayInfo, paramArrayOfPhysicalDisplayInfo.length));
      this.mActivePhysIndex = paramInt1;
      ArrayList localArrayList = new ArrayList();
      paramInt2 = 0;
      int i = 0;
      if (i < paramArrayOfPhysicalDisplayInfo.length)
      {
        SurfaceControl.PhysicalDisplayInfo localPhysicalDisplayInfo = paramArrayOfPhysicalDisplayInfo[i];
        int m = 0;
        int j = 0;
        label52:
        int k = m;
        if (j < localArrayList.size())
        {
          if (((LocalDisplayAdapter.DisplayModeRecord)localArrayList.get(j)).hasMatchingMode(localPhysicalDisplayInfo)) {
            k = 1;
          }
        }
        else {
          if (k == 0) {
            break label110;
          }
        }
        for (;;)
        {
          i += 1;
          break;
          j += 1;
          break label52;
          label110:
          localDisplayModeRecord = findDisplayModeRecord(localPhysicalDisplayInfo);
          paramArrayOfInt = localDisplayModeRecord;
          if (localDisplayModeRecord == null)
          {
            paramArrayOfInt = new LocalDisplayAdapter.DisplayModeRecord(localPhysicalDisplayInfo);
            paramInt2 = 1;
          }
          localArrayList.add(paramArrayOfInt);
        }
      }
      LocalDisplayAdapter.DisplayModeRecord localDisplayModeRecord = null;
      i = 0;
      paramArrayOfInt = localDisplayModeRecord;
      if (i < localArrayList.size())
      {
        paramArrayOfInt = (LocalDisplayAdapter.DisplayModeRecord)localArrayList.get(i);
        if (!paramArrayOfInt.hasMatchingMode(paramArrayOfPhysicalDisplayInfo[paramInt1])) {}
      }
      else
      {
        if ((this.mActiveModeId != 0) && (this.mActiveModeId != paramArrayOfInt.mMode.getModeId()))
        {
          this.mActiveModeInvalid = true;
          LocalDisplayAdapter.this.sendTraversalRequestLocked();
        }
        if (localArrayList.size() != this.mSupportedModes.size()) {
          break label253;
        }
      }
      for (;;)
      {
        if (paramInt2 != 0) {
          break label259;
        }
        return false;
        i += 1;
        break;
        label253:
        paramInt2 = 1;
      }
      label259:
      this.mHavePendingChanges = true;
      this.mSupportedModes.clear();
      paramArrayOfPhysicalDisplayInfo = localArrayList.iterator();
      while (paramArrayOfPhysicalDisplayInfo.hasNext())
      {
        localDisplayModeRecord = (LocalDisplayAdapter.DisplayModeRecord)paramArrayOfPhysicalDisplayInfo.next();
        this.mSupportedModes.put(localDisplayModeRecord.mMode.getModeId(), localDisplayModeRecord);
      }
      if (findDisplayInfoIndexLocked(this.mDefaultModeId) < 0)
      {
        if (this.mDefaultModeId != 0) {
          Slog.w("LocalDisplayAdapter", "Default display mode no longer available, using currently active mode as default.");
        }
        this.mDefaultModeId = paramArrayOfInt.mMode.getModeId();
      }
      if (this.mSupportedModes.indexOfKey(this.mActiveModeId) < 0)
      {
        if (this.mActiveModeId != 0) {
          Slog.w("LocalDisplayAdapter", "Active display mode no longer available, reverting to default mode.");
        }
        this.mActiveModeId = this.mDefaultModeId;
        this.mActiveModeInvalid = true;
      }
      LocalDisplayAdapter.this.sendTraversalRequestLocked();
      return true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/LocalDisplayAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */