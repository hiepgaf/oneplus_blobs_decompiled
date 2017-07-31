package com.android.server.display;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.display.DisplayManagerInternal.DisplayPowerCallbacks;
import android.hardware.display.DisplayManagerInternal.DisplayPowerRequest;
import android.hardware.display.DisplayManagerInternal.DisplayTransactionListener;
import android.hardware.display.DisplayViewport;
import android.hardware.display.IDisplayManager.Stub;
import android.hardware.display.IDisplayManagerCallback;
import android.hardware.display.IVirtualDisplayCallback;
import android.hardware.display.WifiDisplayStatus;
import android.hardware.input.InputManagerInternal;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionManager;
import android.media.projection.IMediaProjectionManager.Stub;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.Surface;
import android.view.WindowManagerInternal;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.DisplayThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.UiThread;
import com.android.server.am.OnePlusProcessManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DisplayManagerService
  extends SystemService
{
  private static boolean DEBUG = false;
  private static final String FORCE_WIFI_DISPLAY_ENABLE = "persist.debug.wfd.enable";
  private static final int MSG_DELAY_MESSAGE = 6;
  private static final int MSG_DELIVER_DISPLAY_EVENT = 3;
  private static final int MSG_REGISTER_ADDITIONAL_DISPLAY_ADAPTERS = 2;
  private static final int MSG_REGISTER_DEFAULT_DISPLAY_ADAPTER = 1;
  private static final int MSG_REQUEST_TRAVERSAL = 4;
  private static final int MSG_UPDATE_VIEWPORT = 5;
  private static final String TAG = "DisplayManagerService";
  private static final long WAIT_FOR_DEFAULT_DISPLAY_TIMEOUT = 10000L;
  public final SparseArray<CallbackRecord> mCallbacks = new SparseArray();
  private final Context mContext;
  private final DisplayViewport mDefaultViewport = new DisplayViewport();
  private final ArrayList<CallbackRecord> mDelayDeliverCallbacks = new ArrayList();
  int mDelayDisplayId;
  int mDelayEvent;
  private final DisplayAdapterListener mDisplayAdapterListener;
  private final ArrayList<DisplayAdapter> mDisplayAdapters = new ArrayList();
  private final ArrayList<DisplayDevice> mDisplayDevices = new ArrayList();
  private DisplayPowerController mDisplayPowerController;
  private final CopyOnWriteArrayList<DisplayManagerInternal.DisplayTransactionListener> mDisplayTransactionListeners = new CopyOnWriteArrayList();
  private final DisplayViewport mExternalTouchViewport = new DisplayViewport();
  private int mGlobalDisplayBrightness = -1;
  private int mGlobalDisplayState = 2;
  private final DisplayManagerHandler mHandler;
  private InputManagerInternal mInputManagerInternal;
  private final SparseArray<LogicalDisplay> mLogicalDisplays = new SparseArray();
  private int mNextNonDefaultDisplayId = 1;
  public boolean mOnlyCore;
  private boolean mPendingTraversal;
  private final PersistentDataStore mPersistentDataStore = new PersistentDataStore();
  private IMediaProjectionManager mProjectionService;
  public boolean mSafeMode;
  private final boolean mSingleDisplayDemoMode;
  private final SyncRoot mSyncRoot = new SyncRoot();
  private final ArrayList<CallbackRecord> mTempCallbacks = new ArrayList();
  private final DisplayViewport mTempDefaultViewport = new DisplayViewport();
  private final DisplayInfo mTempDisplayInfo = new DisplayInfo();
  private final ArrayList<Runnable> mTempDisplayStateWorkQueue = new ArrayList();
  private final DisplayViewport mTempExternalTouchViewport = new DisplayViewport();
  private final Handler mUiHandler;
  private VirtualDisplayAdapter mVirtualDisplayAdapter;
  private WifiDisplayAdapter mWifiDisplayAdapter;
  private int mWifiDisplayScanRequestCount;
  private WindowManagerInternal mWindowManagerInternal;
  
  public DisplayManagerService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mHandler = new DisplayManagerHandler(DisplayThread.get().getLooper());
    this.mUiHandler = UiThread.getHandler();
    this.mDisplayAdapterListener = new DisplayAdapterListener(null);
    this.mSingleDisplayDemoMode = SystemProperties.getBoolean("persist.demo.singledisplay", false);
    this.mGlobalDisplayBrightness = ((PowerManager)this.mContext.getSystemService("power")).getDefaultScreenBrightnessSetting();
  }
  
  private LogicalDisplay addLogicalDisplayLocked(DisplayDevice paramDisplayDevice)
  {
    DisplayDeviceInfo localDisplayDeviceInfo = paramDisplayDevice.getDisplayDeviceInfoLocked();
    if ((localDisplayDeviceInfo.flags & 0x1) != 0) {}
    boolean bool2;
    for (boolean bool1 = true;; bool1 = false)
    {
      bool2 = bool1;
      if (bool1)
      {
        bool2 = bool1;
        if (this.mLogicalDisplays.get(0) != null)
        {
          Slog.w("DisplayManagerService", "Ignoring attempt to add a second default display: " + localDisplayDeviceInfo);
          bool2 = false;
        }
      }
      if ((bool2) || (!this.mSingleDisplayDemoMode)) {
        break;
      }
      Slog.i("DisplayManagerService", "Not creating a logical display for a secondary display  because single display demo mode is enabled: " + localDisplayDeviceInfo);
      return null;
    }
    int i = assignDisplayIdLocked(bool2);
    paramDisplayDevice = new LogicalDisplay(i, assignLayerStackLocked(i), paramDisplayDevice);
    paramDisplayDevice.updateLocked(this.mDisplayDevices);
    if (!paramDisplayDevice.isValidLocked())
    {
      Slog.w("DisplayManagerService", "Ignoring display device because the logical display created from it was not considered valid: " + localDisplayDeviceInfo);
      return null;
    }
    this.mLogicalDisplays.put(i, paramDisplayDevice);
    if (bool2) {
      this.mSyncRoot.notifyAll();
    }
    sendDisplayEventLocked(i, 1);
    return paramDisplayDevice;
  }
  
  private void applyGlobalDisplayStateLocked(List<Runnable> paramList)
  {
    int j = this.mDisplayDevices.size();
    int i = 0;
    while (i < j)
    {
      Runnable localRunnable = updateDisplayStateLocked((DisplayDevice)this.mDisplayDevices.get(i));
      if (localRunnable != null) {
        paramList.add(localRunnable);
      }
      i += 1;
    }
  }
  
  private int assignDisplayIdLocked(boolean paramBoolean)
  {
    if (paramBoolean) {
      return 0;
    }
    int i = this.mNextNonDefaultDisplayId;
    this.mNextNonDefaultDisplayId = (i + 1);
    return i;
  }
  
  private int assignLayerStackLocked(int paramInt)
  {
    return paramInt;
  }
  
  private void clearViewportsLocked()
  {
    this.mDefaultViewport.valid = false;
    this.mExternalTouchViewport.valid = false;
  }
  
  private void configureDisplayInTransactionLocked(DisplayDevice paramDisplayDevice)
  {
    boolean bool = true;
    DisplayDeviceInfo localDisplayDeviceInfo = paramDisplayDevice.getDisplayDeviceInfoLocked();
    int i;
    LogicalDisplay localLogicalDisplay2;
    Object localObject;
    if ((localDisplayDeviceInfo.flags & 0x80) != 0)
    {
      i = 1;
      localLogicalDisplay2 = findLogicalDisplayForDeviceLocked(paramDisplayDevice);
      localObject = localLogicalDisplay2;
      if (i == 0)
      {
        localLogicalDisplay1 = localLogicalDisplay2;
        if (localLogicalDisplay2 != null) {
          if (!localLogicalDisplay2.hasContentLocked()) {
            break label120;
          }
        }
      }
    }
    label120:
    for (LogicalDisplay localLogicalDisplay1 = localLogicalDisplay2;; localLogicalDisplay1 = null)
    {
      localObject = localLogicalDisplay1;
      if (localLogicalDisplay1 == null) {
        localObject = (LogicalDisplay)this.mLogicalDisplays.get(0);
      }
      if (localObject != null) {
        break label126;
      }
      Slog.w("DisplayManagerService", "Missing logical display to use for physical display device: " + paramDisplayDevice.getDisplayDeviceInfoLocked());
      return;
      i = 0;
      break;
    }
    label126:
    if (localDisplayDeviceInfo.state == 1) {}
    for (;;)
    {
      ((LogicalDisplay)localObject).configureDisplayInTransactionLocked(paramDisplayDevice, bool);
      if ((!this.mDefaultViewport.valid) && ((localDisplayDeviceInfo.flags & 0x1) != 0)) {
        setViewportLocked(this.mDefaultViewport, (LogicalDisplay)localObject, paramDisplayDevice);
      }
      if ((!this.mExternalTouchViewport.valid) && (localDisplayDeviceInfo.touch == 2)) {
        setViewportLocked(this.mExternalTouchViewport, (LogicalDisplay)localObject, paramDisplayDevice);
      }
      return;
      bool = false;
    }
  }
  
  private void connectWifiDisplayInternal(String paramString)
  {
    synchronized (this.mSyncRoot)
    {
      if (this.mWifiDisplayAdapter != null) {
        this.mWifiDisplayAdapter.requestConnectLocked(paramString);
      }
      return;
    }
  }
  
  private int createVirtualDisplayInternal(IVirtualDisplayCallback paramIVirtualDisplayCallback, IMediaProjection paramIMediaProjection, int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3, int paramInt4, Surface paramSurface, int paramInt5)
  {
    synchronized (this.mSyncRoot)
    {
      if (this.mVirtualDisplayAdapter == null)
      {
        Slog.w("DisplayManagerService", "Rejecting request to create private virtual display because the virtual display adapter is not available.");
        return -1;
      }
      paramIMediaProjection = this.mVirtualDisplayAdapter.createVirtualDisplayLocked(paramIVirtualDisplayCallback, paramIMediaProjection, paramInt1, paramString1, paramString2, paramInt2, paramInt3, paramInt4, paramSurface, paramInt5);
      if (paramIMediaProjection == null) {
        return -1;
      }
      handleDisplayDeviceAddedLocked(paramIMediaProjection);
      paramString1 = findLogicalDisplayForDeviceLocked(paramIMediaProjection);
      if (paramString1 != null)
      {
        paramInt1 = paramString1.getDisplayIdLocked();
        return paramInt1;
      }
      Slog.w("DisplayManagerService", "Rejecting request to create virtual display because the logical display was not created.");
      this.mVirtualDisplayAdapter.releaseVirtualDisplayLocked(paramIVirtualDisplayCallback.asBinder());
      handleDisplayDeviceRemovedLocked(paramIMediaProjection);
      return -1;
    }
  }
  
  private void deliverDelayDisplayEvent()
  {
    int j = this.mDelayDeliverCallbacks.size();
    if (j > 0)
    {
      int i = 0;
      while (i < j)
      {
        ((CallbackRecord)this.mDelayDeliverCallbacks.get(i)).notifyDisplayEventAsync(this.mDelayDisplayId, this.mDelayEvent, true);
        i += 1;
      }
      this.mDelayDeliverCallbacks.clear();
      this.mDelayEvent = -1;
      this.mDelayDisplayId = -1;
    }
  }
  
  private void deliverDisplayEvent(int paramInt1, int paramInt2)
  {
    if (DEBUG) {
      Slog.d("DisplayManagerService", "Delivering display event: displayId=" + paramInt1 + ", event=" + paramInt2);
    }
    synchronized (this.mSyncRoot)
    {
      int j = this.mCallbacks.size();
      this.mTempCallbacks.clear();
      int i = 0;
      while (i < j)
      {
        this.mTempCallbacks.add((CallbackRecord)this.mCallbacks.valueAt(i));
        i += 1;
      }
      this.mHandler.removeMessages(6);
      this.mDelayDeliverCallbacks.clear();
      this.mDelayEvent = -1;
      this.mDelayDisplayId = -1;
      i = 0;
      if (i < j)
      {
        if (!((CallbackRecord)this.mTempCallbacks.get(i)).notifyDisplayEventAsync(paramInt1, paramInt2, false)) {
          this.mDelayDeliverCallbacks.add((CallbackRecord)this.mTempCallbacks.get(i));
        }
        i += 1;
      }
    }
    if (this.mDelayDeliverCallbacks.size() > 0)
    {
      if (DEBUG) {
        Slog.d("DisplayManagerService", "mDelayDeliverCallbacks size" + this.mDelayDeliverCallbacks.size());
      }
      this.mDelayDisplayId = paramInt1;
      this.mDelayEvent = paramInt2;
      ??? = this.mHandler.obtainMessage(6);
      this.mHandler.sendMessageDelayed((Message)???, 300L);
    }
  }
  
  private void disconnectWifiDisplayInternal()
  {
    synchronized (this.mSyncRoot)
    {
      if (this.mWifiDisplayAdapter != null) {
        this.mWifiDisplayAdapter.requestDisconnectLocked();
      }
      return;
    }
  }
  
  private void dumpInternal(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("DISPLAY MANAGER (dumpsys display)");
    Object localObject1;
    Object localObject3;
    synchronized (this.mSyncRoot)
    {
      paramPrintWriter.println("  mOnlyCode=" + this.mOnlyCore);
      paramPrintWriter.println("  mSafeMode=" + this.mSafeMode);
      paramPrintWriter.println("  mPendingTraversal=" + this.mPendingTraversal);
      paramPrintWriter.println("  mGlobalDisplayState=" + Display.stateToString(this.mGlobalDisplayState));
      paramPrintWriter.println("  mNextNonDefaultDisplayId=" + this.mNextNonDefaultDisplayId);
      paramPrintWriter.println("  mDefaultViewport=" + this.mDefaultViewport);
      paramPrintWriter.println("  mExternalTouchViewport=" + this.mExternalTouchViewport);
      paramPrintWriter.println("  mSingleDisplayDemoMode=" + this.mSingleDisplayDemoMode);
      paramPrintWriter.println("  mWifiDisplayScanRequestCount=" + this.mWifiDisplayScanRequestCount);
      localObject1 = new IndentingPrintWriter(paramPrintWriter, "    ");
      ((IndentingPrintWriter)localObject1).increaseIndent();
      paramPrintWriter.println();
      paramPrintWriter.println("Display Adapters: size=" + this.mDisplayAdapters.size());
      localObject2 = this.mDisplayAdapters.iterator();
      if (((Iterator)localObject2).hasNext())
      {
        localObject3 = (DisplayAdapter)((Iterator)localObject2).next();
        paramPrintWriter.println("  " + ((DisplayAdapter)localObject3).getName());
        ((DisplayAdapter)localObject3).dumpLocked((PrintWriter)localObject1);
      }
    }
    paramPrintWriter.println();
    paramPrintWriter.println("Display Devices: size=" + this.mDisplayDevices.size());
    Object localObject2 = this.mDisplayDevices.iterator();
    while (((Iterator)localObject2).hasNext())
    {
      localObject3 = (DisplayDevice)((Iterator)localObject2).next();
      paramPrintWriter.println("  " + ((DisplayDevice)localObject3).getDisplayDeviceInfoLocked());
      ((DisplayDevice)localObject3).dumpLocked((PrintWriter)localObject1);
    }
    int j = this.mLogicalDisplays.size();
    paramPrintWriter.println();
    paramPrintWriter.println("Logical Displays: size=" + j);
    int i = 0;
    while (i < j)
    {
      int k = this.mLogicalDisplays.keyAt(i);
      localObject2 = (LogicalDisplay)this.mLogicalDisplays.valueAt(i);
      paramPrintWriter.println("  Display " + k + ":");
      ((LogicalDisplay)localObject2).dumpLocked((PrintWriter)localObject1);
      i += 1;
    }
    j = this.mCallbacks.size();
    paramPrintWriter.println();
    paramPrintWriter.println("Callbacks: size=" + j);
    i = 0;
    while (i < j)
    {
      localObject1 = (CallbackRecord)this.mCallbacks.valueAt(i);
      paramPrintWriter.println("  " + i + ": mPid=" + ((CallbackRecord)localObject1).mPid + ", mWifiDisplayScanRequested=" + ((CallbackRecord)localObject1).mWifiDisplayScanRequested);
      i += 1;
    }
    if (this.mDisplayPowerController != null) {
      this.mDisplayPowerController.dump(paramPrintWriter);
    }
    paramPrintWriter.println();
    this.mPersistentDataStore.dump(paramPrintWriter);
  }
  
  private LogicalDisplay findLogicalDisplayForDeviceLocked(DisplayDevice paramDisplayDevice)
  {
    int j = this.mLogicalDisplays.size();
    int i = 0;
    while (i < j)
    {
      LogicalDisplay localLogicalDisplay = (LogicalDisplay)this.mLogicalDisplays.valueAt(i);
      if (localLogicalDisplay.getPrimaryDisplayDeviceLocked() == paramDisplayDevice) {
        return localLogicalDisplay;
      }
      i += 1;
    }
    return null;
  }
  
  private void forgetWifiDisplayInternal(String paramString)
  {
    synchronized (this.mSyncRoot)
    {
      if (this.mWifiDisplayAdapter != null) {
        this.mWifiDisplayAdapter.requestForgetLocked(paramString);
      }
      return;
    }
  }
  
  private int[] getDisplayIdsInternal(int paramInt)
  {
    for (;;)
    {
      int j;
      synchronized (this.mSyncRoot)
      {
        int m = this.mLogicalDisplays.size();
        int[] arrayOfInt2 = new int[m];
        j = 0;
        int i = 0;
        if (j < m)
        {
          if (((LogicalDisplay)this.mLogicalDisplays.valueAt(j)).getDisplayInfoLocked().hasAccess(paramInt))
          {
            int k = i + 1;
            arrayOfInt2[i] = this.mLogicalDisplays.keyAt(j);
            i = k;
          }
        }
        else
        {
          int[] arrayOfInt1 = arrayOfInt2;
          if (i != m) {
            arrayOfInt1 = Arrays.copyOfRange(arrayOfInt2, 0, i);
          }
          return arrayOfInt1;
        }
      }
      j += 1;
    }
  }
  
  private DisplayInfo getDisplayInfoInternal(int paramInt1, int paramInt2)
  {
    synchronized (this.mSyncRoot)
    {
      Object localObject1 = (LogicalDisplay)this.mLogicalDisplays.get(paramInt1);
      if (localObject1 != null)
      {
        localObject1 = ((LogicalDisplay)localObject1).getDisplayInfoLocked();
        boolean bool = ((DisplayInfo)localObject1).hasAccess(paramInt2);
        if (bool) {
          return (DisplayInfo)localObject1;
        }
      }
      return null;
    }
  }
  
  private IMediaProjectionManager getProjectionService()
  {
    if (this.mProjectionService == null) {
      this.mProjectionService = IMediaProjectionManager.Stub.asInterface(ServiceManager.getService("media_projection"));
    }
    return this.mProjectionService;
  }
  
  private WifiDisplayStatus getWifiDisplayStatusInternal()
  {
    synchronized (this.mSyncRoot)
    {
      if (this.mWifiDisplayAdapter != null)
      {
        localWifiDisplayStatus = this.mWifiDisplayAdapter.getWifiDisplayStatusLocked();
        return localWifiDisplayStatus;
      }
      WifiDisplayStatus localWifiDisplayStatus = new WifiDisplayStatus();
      return localWifiDisplayStatus;
    }
  }
  
  private void handleDisplayDeviceAdded(DisplayDevice paramDisplayDevice)
  {
    synchronized (this.mSyncRoot)
    {
      handleDisplayDeviceAddedLocked(paramDisplayDevice);
      return;
    }
  }
  
  private void handleDisplayDeviceAddedLocked(DisplayDevice paramDisplayDevice)
  {
    Object localObject = paramDisplayDevice.getDisplayDeviceInfoLocked();
    if (this.mDisplayDevices.contains(paramDisplayDevice))
    {
      Slog.w("DisplayManagerService", "Attempted to add already added display device: " + localObject);
      return;
    }
    Slog.i("DisplayManagerService", "Display device added: " + localObject);
    paramDisplayDevice.mDebugLastLoggedDeviceInfo = ((DisplayDeviceInfo)localObject);
    this.mDisplayDevices.add(paramDisplayDevice);
    localObject = addLogicalDisplayLocked(paramDisplayDevice);
    Runnable localRunnable = updateDisplayStateLocked(paramDisplayDevice);
    if (localRunnable != null) {
      localRunnable.run();
    }
    if ((localObject != null) && (((LogicalDisplay)localObject).getPrimaryDisplayDeviceLocked() == paramDisplayDevice)) {
      ((LogicalDisplay)localObject).setRequestedColorModeLocked(this.mPersistentDataStore.getColorMode(paramDisplayDevice));
    }
    scheduleTraversalLocked(false);
  }
  
  private void handleDisplayDeviceChanged(DisplayDevice paramDisplayDevice)
  {
    synchronized (this.mSyncRoot)
    {
      DisplayDeviceInfo localDisplayDeviceInfo = paramDisplayDevice.getDisplayDeviceInfoLocked();
      if (!this.mDisplayDevices.contains(paramDisplayDevice))
      {
        Slog.w("DisplayManagerService", "Attempted to change non-existent display device: " + localDisplayDeviceInfo);
        return;
      }
      int i = paramDisplayDevice.mDebugLastLoggedDeviceInfo.diff(localDisplayDeviceInfo);
      if (i == 1) {
        Slog.i("DisplayManagerService", "Display device changed state: \"" + localDisplayDeviceInfo.name + "\", " + Display.stateToString(localDisplayDeviceInfo.state));
      }
      do
      {
        if ((i & 0x4) != 0) {}
        try
        {
          this.mPersistentDataStore.setColorMode(paramDisplayDevice, localDisplayDeviceInfo.colorMode);
          this.mPersistentDataStore.saveIfNeeded();
          paramDisplayDevice.mDebugLastLoggedDeviceInfo = localDisplayDeviceInfo;
          paramDisplayDevice.applyPendingDisplayDeviceInfoChangesLocked();
          if (updateLogicalDisplaysLocked()) {
            scheduleTraversalLocked(false);
          }
          return;
        }
        finally
        {
          this.mPersistentDataStore.saveIfNeeded();
        }
      } while (i == 0);
      Slog.i("DisplayManagerService", "Display device changed: " + localDisplayDeviceInfo);
    }
  }
  
  private void handleDisplayDeviceRemoved(DisplayDevice paramDisplayDevice)
  {
    synchronized (this.mSyncRoot)
    {
      handleDisplayDeviceRemovedLocked(paramDisplayDevice);
      return;
    }
  }
  
  private void handleDisplayDeviceRemovedLocked(DisplayDevice paramDisplayDevice)
  {
    DisplayDeviceInfo localDisplayDeviceInfo = paramDisplayDevice.getDisplayDeviceInfoLocked();
    if (!this.mDisplayDevices.remove(paramDisplayDevice))
    {
      Slog.w("DisplayManagerService", "Attempted to remove non-existent display device: " + localDisplayDeviceInfo);
      return;
    }
    Slog.i("DisplayManagerService", "Display device removed: " + localDisplayDeviceInfo);
    paramDisplayDevice.mDebugLastLoggedDeviceInfo = localDisplayDeviceInfo;
    updateLogicalDisplaysLocked();
    scheduleTraversalLocked(false);
  }
  
  private void onCallbackDied(CallbackRecord paramCallbackRecord)
  {
    synchronized (this.mSyncRoot)
    {
      this.mCallbacks.remove(paramCallbackRecord.mPid);
      stopWifiDisplayScanLocked(paramCallbackRecord);
      return;
    }
  }
  
  private void pauseWifiDisplayInternal()
  {
    synchronized (this.mSyncRoot)
    {
      if (this.mWifiDisplayAdapter != null) {
        this.mWifiDisplayAdapter.requestPauseLocked();
      }
      return;
    }
  }
  
  private void performTraversalInTransactionFromWindowManagerInternal()
  {
    synchronized (this.mSyncRoot)
    {
      boolean bool = this.mPendingTraversal;
      if (!bool) {
        return;
      }
      this.mPendingTraversal = false;
      performTraversalInTransactionLocked();
      ??? = this.mDisplayTransactionListeners.iterator();
      if (((Iterator)???).hasNext()) {
        ((DisplayManagerInternal.DisplayTransactionListener)((Iterator)???).next()).onDisplayTransaction();
      }
    }
  }
  
  private void performTraversalInTransactionLocked()
  {
    clearViewportsLocked();
    int j = this.mDisplayDevices.size();
    int i = 0;
    while (i < j)
    {
      DisplayDevice localDisplayDevice = (DisplayDevice)this.mDisplayDevices.get(i);
      configureDisplayInTransactionLocked(localDisplayDevice);
      localDisplayDevice.performTraversalInTransactionLocked();
      i += 1;
    }
    if (this.mInputManagerInternal != null) {
      this.mHandler.sendEmptyMessage(5);
    }
  }
  
  private void registerAdditionalDisplayAdapters()
  {
    synchronized (this.mSyncRoot)
    {
      if (shouldRegisterNonEssentialDisplayAdaptersLocked())
      {
        registerOverlayDisplayAdapterLocked();
        registerWifiDisplayAdapterLocked();
        registerVirtualDisplayAdapterLocked();
      }
      return;
    }
  }
  
  private void registerCallbackInternal(IDisplayManagerCallback paramIDisplayManagerCallback, int paramInt1, int paramInt2)
  {
    synchronized (this.mSyncRoot)
    {
      if (this.mCallbacks.get(paramInt1) != null) {
        throw new SecurityException("The calling process has already registered an IDisplayManagerCallback.");
      }
    }
    CallbackRecord localCallbackRecord = new CallbackRecord(paramInt1, paramIDisplayManagerCallback, paramInt2);
    try
    {
      paramIDisplayManagerCallback.asBinder().linkToDeath(localCallbackRecord, 0);
      this.mCallbacks.put(paramInt1, localCallbackRecord);
      return;
    }
    catch (RemoteException paramIDisplayManagerCallback)
    {
      throw new RuntimeException(paramIDisplayManagerCallback);
    }
  }
  
  private void registerDefaultDisplayAdapter()
  {
    synchronized (this.mSyncRoot)
    {
      registerDisplayAdapterLocked(new LocalDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener));
      return;
    }
  }
  
  private void registerDisplayAdapterLocked(DisplayAdapter paramDisplayAdapter)
  {
    this.mDisplayAdapters.add(paramDisplayAdapter);
    paramDisplayAdapter.registerLocked();
  }
  
  private void registerDisplayTransactionListenerInternal(DisplayManagerInternal.DisplayTransactionListener paramDisplayTransactionListener)
  {
    this.mDisplayTransactionListeners.add(paramDisplayTransactionListener);
  }
  
  private void registerOverlayDisplayAdapterLocked()
  {
    registerDisplayAdapterLocked(new OverlayDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener, this.mUiHandler));
  }
  
  private void registerVirtualDisplayAdapterLocked()
  {
    this.mVirtualDisplayAdapter = new VirtualDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener);
    registerDisplayAdapterLocked(this.mVirtualDisplayAdapter);
  }
  
  private void registerWifiDisplayAdapterLocked()
  {
    if ((this.mContext.getResources().getBoolean(17956988)) || (SystemProperties.getInt("persist.debug.wfd.enable", -1) == 1))
    {
      this.mWifiDisplayAdapter = new WifiDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener, this.mPersistentDataStore);
      registerDisplayAdapterLocked(this.mWifiDisplayAdapter);
    }
  }
  
  private void releaseVirtualDisplayInternal(IBinder paramIBinder)
  {
    synchronized (this.mSyncRoot)
    {
      VirtualDisplayAdapter localVirtualDisplayAdapter = this.mVirtualDisplayAdapter;
      if (localVirtualDisplayAdapter == null) {
        return;
      }
      paramIBinder = this.mVirtualDisplayAdapter.releaseVirtualDisplayLocked(paramIBinder);
      if (paramIBinder != null) {
        handleDisplayDeviceRemovedLocked(paramIBinder);
      }
      return;
    }
  }
  
  private void renameWifiDisplayInternal(String paramString1, String paramString2)
  {
    synchronized (this.mSyncRoot)
    {
      if (this.mWifiDisplayAdapter != null) {
        this.mWifiDisplayAdapter.requestRenameLocked(paramString1, paramString2);
      }
      return;
    }
  }
  
  private void requestColorModeInternal(int paramInt1, int paramInt2)
  {
    synchronized (this.mSyncRoot)
    {
      LogicalDisplay localLogicalDisplay = (LogicalDisplay)this.mLogicalDisplays.get(paramInt1);
      if ((localLogicalDisplay != null) && (localLogicalDisplay.getRequestedColorModeLocked() != paramInt2))
      {
        localLogicalDisplay.setRequestedColorModeLocked(paramInt2);
        scheduleTraversalLocked(false);
      }
      return;
    }
  }
  
  private void requestGlobalDisplayStateInternal(int paramInt1, int paramInt2)
  {
    int i = paramInt1;
    if (paramInt1 == 0) {
      i = 2;
    }
    if (i == 1) {
      paramInt1 = 0;
    }
    ArrayList localArrayList;
    for (;;)
    {
      localArrayList = this.mTempDisplayStateWorkQueue;
      try
      {
        synchronized (this.mSyncRoot)
        {
          if (this.mGlobalDisplayState == i)
          {
            paramInt2 = this.mGlobalDisplayBrightness;
            if (paramInt2 != paramInt1) {}
          }
          try
          {
            return;
          }
          finally {}
          if (paramInt2 < 0)
          {
            paramInt1 = -1;
            continue;
          }
          paramInt1 = paramInt2;
          if (paramInt2 <= 255) {
            continue;
          }
          paramInt1 = 255;
          continue;
          Trace.traceBegin(131072L, "requestGlobalDisplayState(" + Display.stateToString(i) + ", brightness=" + paramInt1 + ")");
          this.mGlobalDisplayState = i;
          this.mGlobalDisplayBrightness = paramInt1;
          applyGlobalDisplayStateLocked(this.mTempDisplayStateWorkQueue);
          paramInt1 = 0;
          if (paramInt1 < this.mTempDisplayStateWorkQueue.size())
          {
            ((Runnable)this.mTempDisplayStateWorkQueue.get(paramInt1)).run();
            paramInt1 += 1;
          }
        }
        Trace.traceEnd(131072L);
      }
      finally
      {
        this.mTempDisplayStateWorkQueue.clear();
      }
    }
    this.mTempDisplayStateWorkQueue.clear();
  }
  
  private void resizeVirtualDisplayInternal(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3)
  {
    synchronized (this.mSyncRoot)
    {
      VirtualDisplayAdapter localVirtualDisplayAdapter = this.mVirtualDisplayAdapter;
      if (localVirtualDisplayAdapter == null) {
        return;
      }
      this.mVirtualDisplayAdapter.resizeVirtualDisplayLocked(paramIBinder, paramInt1, paramInt2, paramInt3);
      return;
    }
  }
  
  private void resumeWifiDisplayInternal()
  {
    synchronized (this.mSyncRoot)
    {
      if (this.mWifiDisplayAdapter != null) {
        this.mWifiDisplayAdapter.requestResumeLocked();
      }
      return;
    }
  }
  
  private void scheduleTraversalLocked(boolean paramBoolean)
  {
    if ((!this.mPendingTraversal) && (this.mWindowManagerInternal != null))
    {
      this.mPendingTraversal = true;
      if (!paramBoolean) {
        this.mHandler.sendEmptyMessage(4);
      }
    }
  }
  
  private void sendDisplayEventLocked(int paramInt1, int paramInt2)
  {
    Message localMessage = this.mHandler.obtainMessage(3, paramInt1, paramInt2);
    this.mHandler.sendMessage(localMessage);
  }
  
  private void setDisplayInfoOverrideFromWindowManagerInternal(int paramInt, DisplayInfo paramDisplayInfo)
  {
    synchronized (this.mSyncRoot)
    {
      LogicalDisplay localLogicalDisplay = (LogicalDisplay)this.mLogicalDisplays.get(paramInt);
      if ((localLogicalDisplay != null) && (localLogicalDisplay.setDisplayInfoOverrideFromWindowManagerLocked(paramDisplayInfo)))
      {
        sendDisplayEventLocked(paramInt, 2);
        scheduleTraversalLocked(false);
      }
      return;
    }
  }
  
  private void setDisplayOffsetsInternal(int paramInt1, int paramInt2, int paramInt3)
  {
    synchronized (this.mSyncRoot)
    {
      LogicalDisplay localLogicalDisplay = (LogicalDisplay)this.mLogicalDisplays.get(paramInt1);
      if (localLogicalDisplay == null) {
        return;
      }
      if ((localLogicalDisplay.getDisplayOffsetXLocked() != paramInt2) || (localLogicalDisplay.getDisplayOffsetYLocked() != paramInt3))
      {
        if (DEBUG) {
          Slog.d("DisplayManagerService", "Display " + paramInt1 + " burn-in offset set to (" + paramInt2 + ", " + paramInt3 + ")");
        }
        localLogicalDisplay.setDisplayOffsetsLocked(paramInt2, paramInt3);
        scheduleTraversalLocked(false);
      }
      return;
    }
  }
  
  private void setDisplayPropertiesInternal(int paramInt1, boolean paramBoolean1, float paramFloat, int paramInt2, boolean paramBoolean2)
  {
    synchronized (this.mSyncRoot)
    {
      LogicalDisplay localLogicalDisplay = (LogicalDisplay)this.mLogicalDisplays.get(paramInt1);
      if (localLogicalDisplay == null) {
        return;
      }
      if (localLogicalDisplay.hasContentLocked() != paramBoolean1)
      {
        if (DEBUG) {
          Slog.d("DisplayManagerService", "Display " + paramInt1 + " hasContent flag changed: " + "hasContent=" + paramBoolean1 + ", inTraversal=" + paramBoolean2);
        }
        localLogicalDisplay.setHasContentLocked(paramBoolean1);
        scheduleTraversalLocked(paramBoolean2);
      }
      int i = paramInt2;
      if (paramInt2 == 0)
      {
        i = paramInt2;
        if (paramFloat != 0.0F) {
          i = localLogicalDisplay.getDisplayInfoLocked().findDefaultModeByRefreshRate(paramFloat);
        }
      }
      if (localLogicalDisplay.getRequestedModeIdLocked() != i)
      {
        if (DEBUG) {
          Slog.d("DisplayManagerService", "Display " + paramInt1 + " switching to mode " + i);
        }
        localLogicalDisplay.setRequestedModeIdLocked(i);
        scheduleTraversalLocked(paramBoolean2);
      }
      return;
    }
  }
  
  private static void setViewportLocked(DisplayViewport paramDisplayViewport, LogicalDisplay paramLogicalDisplay, DisplayDevice paramDisplayDevice)
  {
    paramDisplayViewport.valid = true;
    paramDisplayViewport.displayId = paramLogicalDisplay.getDisplayIdLocked();
    paramDisplayDevice.populateViewportLocked(paramDisplayViewport);
  }
  
  private void setVirtualDisplaySurfaceInternal(IBinder paramIBinder, Surface paramSurface)
  {
    synchronized (this.mSyncRoot)
    {
      VirtualDisplayAdapter localVirtualDisplayAdapter = this.mVirtualDisplayAdapter;
      if (localVirtualDisplayAdapter == null) {
        return;
      }
      this.mVirtualDisplayAdapter.setVirtualDisplaySurfaceLocked(paramIBinder, paramSurface);
      return;
    }
  }
  
  private boolean shouldRegisterNonEssentialDisplayAdaptersLocked()
  {
    return (!this.mSafeMode) && (!this.mOnlyCore);
  }
  
  private void startWifiDisplayScanInternal(int paramInt)
  {
    synchronized (this.mSyncRoot)
    {
      CallbackRecord localCallbackRecord1 = (CallbackRecord)this.mCallbacks.get(paramInt);
      if (localCallbackRecord1 == null) {
        throw new IllegalStateException("The calling process has not registered an IDisplayManagerCallback.");
      }
    }
    startWifiDisplayScanLocked(localCallbackRecord2);
  }
  
  private void startWifiDisplayScanLocked(CallbackRecord paramCallbackRecord)
  {
    if (!paramCallbackRecord.mWifiDisplayScanRequested)
    {
      paramCallbackRecord.mWifiDisplayScanRequested = true;
      int i = this.mWifiDisplayScanRequestCount;
      this.mWifiDisplayScanRequestCount = (i + 1);
      if ((i == 0) && (this.mWifiDisplayAdapter != null)) {
        this.mWifiDisplayAdapter.requestStartScanLocked();
      }
    }
  }
  
  private void stopWifiDisplayScanInternal(int paramInt)
  {
    synchronized (this.mSyncRoot)
    {
      CallbackRecord localCallbackRecord1 = (CallbackRecord)this.mCallbacks.get(paramInt);
      if (localCallbackRecord1 == null) {
        throw new IllegalStateException("The calling process has not registered an IDisplayManagerCallback.");
      }
    }
    stopWifiDisplayScanLocked(localCallbackRecord2);
  }
  
  private void stopWifiDisplayScanLocked(CallbackRecord paramCallbackRecord)
  {
    if (paramCallbackRecord.mWifiDisplayScanRequested)
    {
      paramCallbackRecord.mWifiDisplayScanRequested = false;
      int i = this.mWifiDisplayScanRequestCount - 1;
      this.mWifiDisplayScanRequestCount = i;
      if (i != 0) {
        break label43;
      }
      if (this.mWifiDisplayAdapter != null) {
        this.mWifiDisplayAdapter.requestStopScanLocked();
      }
    }
    label43:
    while (this.mWifiDisplayScanRequestCount >= 0) {
      return;
    }
    Slog.wtf("DisplayManagerService", "mWifiDisplayScanRequestCount became negative: " + this.mWifiDisplayScanRequestCount);
    this.mWifiDisplayScanRequestCount = 0;
  }
  
  private void unregisterDisplayTransactionListenerInternal(DisplayManagerInternal.DisplayTransactionListener paramDisplayTransactionListener)
  {
    this.mDisplayTransactionListeners.remove(paramDisplayTransactionListener);
  }
  
  private Runnable updateDisplayStateLocked(DisplayDevice paramDisplayDevice)
  {
    if ((paramDisplayDevice.getDisplayDeviceInfoLocked().flags & 0x20) == 0) {
      return paramDisplayDevice.requestDisplayStateLocked(this.mGlobalDisplayState, this.mGlobalDisplayBrightness);
    }
    return null;
  }
  
  private boolean updateLogicalDisplaysLocked()
  {
    boolean bool = false;
    int i = this.mLogicalDisplays.size();
    int j = i - 1;
    if (i > 0)
    {
      i = this.mLogicalDisplays.keyAt(j);
      LogicalDisplay localLogicalDisplay = (LogicalDisplay)this.mLogicalDisplays.valueAt(j);
      this.mTempDisplayInfo.copyFrom(localLogicalDisplay.getDisplayInfoLocked());
      localLogicalDisplay.updateLocked(this.mDisplayDevices);
      if (!localLogicalDisplay.isValidLocked())
      {
        this.mLogicalDisplays.removeAt(j);
        sendDisplayEventLocked(i, 3);
        bool = true;
      }
      for (;;)
      {
        i = j;
        break;
        if (!this.mTempDisplayInfo.equals(localLogicalDisplay.getDisplayInfoLocked()))
        {
          sendDisplayEventLocked(i, 2);
          bool = true;
        }
      }
    }
    return bool;
  }
  
  protected boolean dynamicallyConfigDisplayLogTag(PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    if (paramArrayOfString.length >= 1)
    {
      if (!"log".equals(paramArrayOfString[0])) {
        return false;
      }
      if (paramArrayOfString.length != 3)
      {
        paramPrintWriter.println("Invalid argument! Get detail help as bellow:");
        logOutDisplayLogTagHelp(paramPrintWriter);
        return true;
      }
    }
    else
    {
      return false;
    }
    paramPrintWriter.println("dynamicallyConfigDisplayLogTag, args.length:" + paramArrayOfString.length);
    int i = 0;
    while (i < paramArrayOfString.length)
    {
      paramPrintWriter.println("dynamicallyConfigPowerLogTag, args[" + i + "]:" + paramArrayOfString[i]);
      i += 1;
    }
    String str = paramArrayOfString[1];
    if ("1".equals(paramArrayOfString[2])) {}
    for (boolean bool = true;; bool = false)
    {
      paramPrintWriter.println("dynamicallyConfigDisplayLogTag, logCategoryTag:" + str + ", on:" + bool);
      if (!"all".equals(str)) {
        break;
      }
      DEBUG = bool;
      DisplayPowerController.DEBUG = bool;
      AutomaticBrightnessController.DEBUG = bool;
      ColorFade.DEBUG = bool;
      DisplayPowerState.DEBUG = bool;
      return true;
    }
    paramPrintWriter.println("Invalid log tag argument! Get detail help as bellow:");
    logOutDisplayLogTagHelp(paramPrintWriter);
    return true;
  }
  
  protected void logOutDisplayLogTagHelp(PrintWriter paramPrintWriter)
  {
    paramPrintWriter.println("********************** Help begin:**********************");
    paramPrintWriter.println("1 All display log:DEBUG | DisplayPowerController");
    paramPrintWriter.println("cmd: dumpsys display log all 0/1");
    paramPrintWriter.println("----------------------------------");
    paramPrintWriter.println("********************** Help end.  **********************");
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 100) {
      for (;;)
      {
        long l2;
        synchronized (this.mSyncRoot)
        {
          long l1 = SystemClock.uptimeMillis();
          if (this.mLogicalDisplays.get(0) != null) {
            break;
          }
          l2 = l1 + 10000L - SystemClock.uptimeMillis();
          if (l2 <= 0L) {
            throw new RuntimeException("Timeout waiting for default display to be initialized.");
          }
        }
        if (DEBUG) {
          Slog.d("DisplayManagerService", "waitForDefaultDisplay: waiting, timeout=" + l2);
        }
        try
        {
          this.mSyncRoot.wait(l2);
        }
        catch (InterruptedException localInterruptedException) {}
      }
    }
  }
  
  public void onStart()
  {
    this.mPersistentDataStore.loadIfNeeded();
    this.mHandler.sendEmptyMessage(1);
    publishBinderService("display", new BinderService(null), true);
    publishLocalService(DisplayManagerInternal.class, new LocalService(null));
    publishLocalService(DisplayTransformManager.class, new DisplayTransformManager());
  }
  
  public void systemReady(boolean paramBoolean1, boolean paramBoolean2)
  {
    synchronized (this.mSyncRoot)
    {
      this.mSafeMode = paramBoolean1;
      this.mOnlyCore = paramBoolean2;
      this.mHandler.sendEmptyMessage(2);
      return;
    }
  }
  
  public void windowManagerAndInputReady()
  {
    synchronized (this.mSyncRoot)
    {
      this.mWindowManagerInternal = ((WindowManagerInternal)LocalServices.getService(WindowManagerInternal.class));
      this.mInputManagerInternal = ((InputManagerInternal)LocalServices.getService(InputManagerInternal.class));
      scheduleTraversalLocked(false);
      return;
    }
  }
  
  private final class BinderService
    extends IDisplayManager.Stub
  {
    private BinderService() {}
    
    private boolean canProjectSecureVideo(IMediaProjection paramIMediaProjection)
    {
      if (paramIMediaProjection != null) {
        try
        {
          boolean bool = paramIMediaProjection.canProjectSecureVideo();
          if (bool) {
            return true;
          }
        }
        catch (RemoteException paramIMediaProjection)
        {
          Slog.e("DisplayManagerService", "Unable to query projection service for permissions", paramIMediaProjection);
        }
      }
      return DisplayManagerService.-get1(DisplayManagerService.this).checkCallingPermission("android.permission.CAPTURE_SECURE_VIDEO_OUTPUT") == 0;
    }
    
    private boolean canProjectVideo(IMediaProjection paramIMediaProjection)
    {
      if (paramIMediaProjection != null) {
        try
        {
          boolean bool = paramIMediaProjection.canProjectVideo();
          if (bool) {
            return true;
          }
        }
        catch (RemoteException localRemoteException)
        {
          Slog.e("DisplayManagerService", "Unable to query projection service for permissions", localRemoteException);
        }
      }
      if (DisplayManagerService.-get1(DisplayManagerService.this).checkCallingPermission("android.permission.CAPTURE_VIDEO_OUTPUT") == 0) {
        return true;
      }
      return canProjectSecureVideo(paramIMediaProjection);
    }
    
    private boolean validatePackageName(int paramInt, String paramString)
    {
      if (paramString != null)
      {
        String[] arrayOfString = DisplayManagerService.-get1(DisplayManagerService.this).getPackageManager().getPackagesForUid(paramInt);
        if (arrayOfString != null)
        {
          int i = arrayOfString.length;
          paramInt = 0;
          while (paramInt < i)
          {
            if (arrayOfString[paramInt].equals(paramString)) {
              return true;
            }
            paramInt += 1;
          }
        }
      }
      return false;
    }
    
    public void connectWifiDisplay(String paramString)
    {
      if (paramString == null) {
        throw new IllegalArgumentException("address must not be null");
      }
      DisplayManagerService.-get1(DisplayManagerService.this).enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to connect to a wifi display");
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap5(DisplayManagerService.this, paramString);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public int createVirtualDisplay(IVirtualDisplayCallback paramIVirtualDisplayCallback, IMediaProjection paramIMediaProjection, String paramString1, String paramString2, int paramInt1, int paramInt2, int paramInt3, Surface paramSurface, int paramInt4)
    {
      int j = Binder.getCallingUid();
      if (!validatePackageName(j, paramString1)) {
        throw new SecurityException("packageName must match the calling uid");
      }
      if (paramIVirtualDisplayCallback == null) {
        throw new IllegalArgumentException("appToken must not be null");
      }
      if (TextUtils.isEmpty(paramString2)) {
        throw new IllegalArgumentException("name must be non-null and non-empty");
      }
      if ((paramInt1 <= 0) || (paramInt2 <= 0)) {}
      while (paramInt3 <= 0) {
        throw new IllegalArgumentException("width, height, and densityDpi must be greater than 0");
      }
      if ((paramSurface != null) && (paramSurface.isSingleBuffered())) {
        throw new IllegalArgumentException("Surface can't be single-buffered");
      }
      int i = paramInt4;
      if ((paramInt4 & 0x1) != 0) {
        i = paramInt4 | 0x10;
      }
      paramInt4 = i;
      if ((i & 0x8) != 0) {
        paramInt4 = i & 0xFFFFFFEF;
      }
      i = paramInt4;
      if (paramIMediaProjection != null)
      {
        try
        {
          if (!DisplayManagerService.-wrap1(DisplayManagerService.this).isValidMediaProjection(paramIMediaProjection)) {
            throw new SecurityException("Invalid media projection");
          }
        }
        catch (RemoteException paramIVirtualDisplayCallback)
        {
          throw new SecurityException("unable to validate media projection or flags");
        }
        i = paramIMediaProjection.applyVirtualDisplayFlags(paramInt4);
      }
      if ((j != 1000) && ((i & 0x10) != 0) && (!canProjectVideo(paramIMediaProjection))) {
        throw new SecurityException("Requires CAPTURE_VIDEO_OUTPUT or CAPTURE_SECURE_VIDEO_OUTPUT permission, or an appropriate MediaProjection token in order to create a screen sharing virtual display.");
      }
      if (((i & 0x4) != 0) && (!canProjectSecureVideo(paramIMediaProjection))) {
        throw new SecurityException("Requires CAPTURE_SECURE_VIDEO_OUTPUT or an appropriate MediaProjection token to create a secure virtual display.");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        paramInt1 = DisplayManagerService.-wrap4(DisplayManagerService.this, paramIVirtualDisplayCallback, paramIMediaProjection, j, paramString1, paramString2, paramInt1, paramInt2, paramInt3, paramSurface, i);
        return paramInt1;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void disconnectWifiDisplay()
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap8(DisplayManagerService.this);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      if ((DisplayManagerService.-get1(DisplayManagerService.this) == null) || (DisplayManagerService.-get1(DisplayManagerService.this).checkCallingOrSelfPermission("android.permission.DUMP") != 0))
      {
        paramPrintWriter.println("Permission Denial: can't dump DisplayManager from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return;
      }
      if (DisplayManagerService.this.dynamicallyConfigDisplayLogTag(paramPrintWriter, paramArrayOfString)) {
        return;
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap9(DisplayManagerService.this, paramPrintWriter);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void forgetWifiDisplay(String paramString)
    {
      if (paramString == null) {
        throw new IllegalArgumentException("address must not be null");
      }
      DisplayManagerService.-get1(DisplayManagerService.this).enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to forget to a wifi display");
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap10(DisplayManagerService.this, paramString);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public int[] getDisplayIds()
    {
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        int[] arrayOfInt = DisplayManagerService.-wrap3(DisplayManagerService.this, i);
        return arrayOfInt;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public DisplayInfo getDisplayInfo(int paramInt)
    {
      int i = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayInfo localDisplayInfo = DisplayManagerService.-wrap2(DisplayManagerService.this, paramInt, i);
        return localDisplayInfo;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public WifiDisplayStatus getWifiDisplayStatus()
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        WifiDisplayStatus localWifiDisplayStatus = DisplayManagerService.-wrap0(DisplayManagerService.this);
        return localWifiDisplayStatus;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void pauseWifiDisplay()
    {
      DisplayManagerService.-get1(DisplayManagerService.this).enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to pause a wifi display session");
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap15(DisplayManagerService.this);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void registerCallback(IDisplayManagerCallback paramIDisplayManagerCallback)
    {
      if (paramIDisplayManagerCallback == null) {
        throw new IllegalArgumentException("listener must not be null");
      }
      int i = Binder.getCallingPid();
      int j = Binder.getCallingUid();
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap18(DisplayManagerService.this, paramIDisplayManagerCallback, i, j);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void releaseVirtualDisplay(IVirtualDisplayCallback paramIVirtualDisplayCallback)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap21(DisplayManagerService.this, paramIVirtualDisplayCallback.asBinder());
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void renameWifiDisplay(String paramString1, String paramString2)
    {
      if (paramString1 == null) {
        throw new IllegalArgumentException("address must not be null");
      }
      DisplayManagerService.-get1(DisplayManagerService.this).enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to rename to a wifi display");
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap22(DisplayManagerService.this, paramString1, paramString2);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void requestColorMode(int paramInt1, int paramInt2)
    {
      DisplayManagerService.-get1(DisplayManagerService.this).enforceCallingOrSelfPermission("android.permission.CONFIGURE_DISPLAY_COLOR_MODE", "Permission required to change the display color mode");
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap23(DisplayManagerService.this, paramInt1, paramInt2);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void resizeVirtualDisplay(IVirtualDisplayCallback paramIVirtualDisplayCallback, int paramInt1, int paramInt2, int paramInt3)
    {
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap25(DisplayManagerService.this, paramIVirtualDisplayCallback.asBinder(), paramInt1, paramInt2, paramInt3);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void resumeWifiDisplay()
    {
      DisplayManagerService.-get1(DisplayManagerService.this).enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to resume a wifi display session");
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap26(DisplayManagerService.this);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void setVirtualDisplaySurface(IVirtualDisplayCallback paramIVirtualDisplayCallback, Surface paramSurface)
    {
      if ((paramSurface != null) && (paramSurface.isSingleBuffered())) {
        throw new IllegalArgumentException("Surface can't be single-buffered");
      }
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap31(DisplayManagerService.this, paramIVirtualDisplayCallback.asBinder(), paramSurface);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void startWifiDisplayScan()
    {
      DisplayManagerService.-get1(DisplayManagerService.this).enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to start wifi display scans");
      int i = Binder.getCallingPid();
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap32(DisplayManagerService.this, i);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
    
    public void stopWifiDisplayScan()
    {
      DisplayManagerService.-get1(DisplayManagerService.this).enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to stop wifi display scans");
      int i = Binder.getCallingPid();
      long l = Binder.clearCallingIdentity();
      try
      {
        DisplayManagerService.-wrap33(DisplayManagerService.this, i);
        return;
      }
      finally
      {
        Binder.restoreCallingIdentity(l);
      }
    }
  }
  
  private final class CallbackRecord
    implements IBinder.DeathRecipient
  {
    private final IDisplayManagerCallback mCallback;
    public final int mPid;
    public final int mUid;
    public boolean mWifiDisplayScanRequested;
    
    public CallbackRecord(int paramInt1, IDisplayManagerCallback paramIDisplayManagerCallback, int paramInt2)
    {
      this.mPid = paramInt1;
      this.mCallback = paramIDisplayManagerCallback;
      this.mUid = paramInt2;
    }
    
    public void binderDied()
    {
      if (DisplayManagerService.-get0()) {
        Slog.d("DisplayManagerService", "Display listener for pid " + this.mPid + " died.");
      }
      DisplayManagerService.-wrap14(DisplayManagerService.this, this);
    }
    
    public boolean notifyDisplayEventAsync(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      try
      {
        if (OnePlusProcessManager.isSupportFrozenApp())
        {
          if ((paramBoolean) || (OnePlusProcessManager.isDeliverDisplayChange(this.mUid))) {
            OnePlusProcessManager.resumeProcessByUID_out_Delay(this.mUid, "notifyDisplayEventAsync", 1);
          }
        }
        else
        {
          this.mCallback.onDisplayEvent(paramInt1, paramInt2);
          return true;
        }
        return false;
      }
      catch (RemoteException localRemoteException)
      {
        Slog.w("DisplayManagerService", "Failed to notify process " + this.mPid + " that displays changed, assuming it died.", localRemoteException);
        binderDied();
      }
      return true;
    }
  }
  
  private final class DisplayAdapterListener
    implements DisplayAdapter.Listener
  {
    private DisplayAdapterListener() {}
    
    public void onDisplayDeviceEvent(DisplayDevice paramDisplayDevice, int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return;
      case 1: 
        DisplayManagerService.-wrap11(DisplayManagerService.this, paramDisplayDevice);
        return;
      case 2: 
        DisplayManagerService.-wrap12(DisplayManagerService.this, paramDisplayDevice);
        return;
      }
      DisplayManagerService.-wrap13(DisplayManagerService.this, paramDisplayDevice);
    }
    
    public void onTraversalRequested()
    {
      synchronized (DisplayManagerService.-get6(DisplayManagerService.this))
      {
        DisplayManagerService.-wrap27(DisplayManagerService.this, false);
        return;
      }
    }
  }
  
  private final class DisplayManagerHandler
    extends Handler
  {
    public DisplayManagerHandler(Looper paramLooper)
    {
      super(null, true);
    }
    
    public void handleMessage(Message arg1)
    {
      Trace.traceBegin(131072L, "handleMessagemsg.what");
      switch (???.what)
      {
      }
      for (;;)
      {
        Trace.traceEnd(131072L);
        return;
        DisplayManagerService.-wrap19(DisplayManagerService.this);
        continue;
        DisplayManagerService.-wrap17(DisplayManagerService.this);
        continue;
        DisplayManagerService.-wrap7(DisplayManagerService.this, ???.arg1, ???.arg2);
        continue;
        DisplayManagerService.-get9(DisplayManagerService.this).requestTraversalFromDisplayManager();
        continue;
        synchronized (DisplayManagerService.-get6(DisplayManagerService.this))
        {
          DisplayManagerService.-get7(DisplayManagerService.this).copyFrom(DisplayManagerService.-get2(DisplayManagerService.this));
          DisplayManagerService.-get8(DisplayManagerService.this).copyFrom(DisplayManagerService.-get4(DisplayManagerService.this));
          DisplayManagerService.-get5(DisplayManagerService.this).setDisplayViewports(DisplayManagerService.-get7(DisplayManagerService.this), DisplayManagerService.-get8(DisplayManagerService.this));
        }
      }
    }
  }
  
  private final class LocalService
    extends DisplayManagerInternal
  {
    private LocalService() {}
    
    public DisplayInfo getDisplayInfo(int paramInt)
    {
      return DisplayManagerService.-wrap2(DisplayManagerService.this, paramInt, Process.myUid());
    }
    
    public void initPowerManagement(final DisplayManagerInternal.DisplayPowerCallbacks paramDisplayPowerCallbacks, Handler paramHandler, SensorManager paramSensorManager)
    {
      synchronized (DisplayManagerService.-get6(DisplayManagerService.this))
      {
        DisplayBlanker local1 = new DisplayBlanker()
        {
          public void requestDisplayState(int paramAnonymousInt1, int paramAnonymousInt2)
          {
            if (paramAnonymousInt1 == 1) {
              DisplayManagerService.-wrap24(DisplayManagerService.this, paramAnonymousInt1, paramAnonymousInt2);
            }
            paramDisplayPowerCallbacks.onDisplayStateChange(paramAnonymousInt1);
            if (paramAnonymousInt1 != 1) {
              DisplayManagerService.-wrap24(DisplayManagerService.this, paramAnonymousInt1, paramAnonymousInt2);
            }
          }
        };
        DisplayManagerService.-set0(DisplayManagerService.this, new DisplayPowerController(DisplayManagerService.-get1(DisplayManagerService.this), paramDisplayPowerCallbacks, paramHandler, paramSensorManager, local1));
        return;
      }
    }
    
    public boolean isProximitySensorAvailable()
    {
      return DisplayManagerService.-get3(DisplayManagerService.this).isProximitySensorAvailable();
    }
    
    public void performTraversalInTransactionFromWindowManager()
    {
      DisplayManagerService.-wrap16(DisplayManagerService.this);
    }
    
    public void registerDisplayTransactionListener(DisplayManagerInternal.DisplayTransactionListener paramDisplayTransactionListener)
    {
      if (paramDisplayTransactionListener == null) {
        throw new IllegalArgumentException("listener must not be null");
      }
      DisplayManagerService.-wrap20(DisplayManagerService.this, paramDisplayTransactionListener);
    }
    
    public boolean requestPowerState(DisplayManagerInternal.DisplayPowerRequest paramDisplayPowerRequest, boolean paramBoolean)
    {
      return DisplayManagerService.-get3(DisplayManagerService.this).requestPowerState(paramDisplayPowerRequest, paramBoolean);
    }
    
    public void setDisplayInfoOverrideFromWindowManager(int paramInt, DisplayInfo paramDisplayInfo)
    {
      DisplayManagerService.-wrap28(DisplayManagerService.this, paramInt, paramDisplayInfo);
    }
    
    public void setDisplayOffsets(int paramInt1, int paramInt2, int paramInt3)
    {
      DisplayManagerService.-wrap29(DisplayManagerService.this, paramInt1, paramInt2, paramInt3);
    }
    
    public void setDisplayProperties(int paramInt1, boolean paramBoolean1, float paramFloat, int paramInt2, boolean paramBoolean2)
    {
      DisplayManagerService.-wrap30(DisplayManagerService.this, paramInt1, paramBoolean1, paramFloat, paramInt2, paramBoolean2);
    }
    
    public void setUseProximityForceSuspend(boolean paramBoolean)
    {
      DisplayManagerService.-get3(DisplayManagerService.this).setUseProximityForceSuspend(paramBoolean);
    }
    
    public void setWakingupReason(String paramString)
    {
      Slog.d("DisplayManagerService", "setWakingupReason: " + paramString);
      DisplayManagerService.-get3(DisplayManagerService.this).setWakingupReason(paramString);
    }
    
    public void unregisterDisplayTransactionListener(DisplayManagerInternal.DisplayTransactionListener paramDisplayTransactionListener)
    {
      if (paramDisplayTransactionListener == null) {
        throw new IllegalArgumentException("listener must not be null");
      }
      DisplayManagerService.-wrap34(DisplayManagerService.this, paramDisplayTransactionListener);
    }
  }
  
  public static final class SyncRoot {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/DisplayManagerService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */