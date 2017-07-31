package com.android.server.display;

import android.content.Context;
import android.hardware.display.IVirtualDisplayCallback;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionCallback.Stub;
import android.os.Handler;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.Display;
import android.view.Display.Mode;
import android.view.Surface;
import android.view.SurfaceControl;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

final class VirtualDisplayAdapter
  extends DisplayAdapter
{
  static final boolean DEBUG = false;
  static final String TAG = "VirtualDisplayAdapter";
  private static final String UNIQUE_ID_PREFIX = "virtual:";
  private Handler mHandler;
  private final ArrayMap<IBinder, VirtualDisplayDevice> mVirtualDisplayDevices = new ArrayMap();
  
  public VirtualDisplayAdapter(DisplayManagerService.SyncRoot paramSyncRoot, Context paramContext, Handler paramHandler, DisplayAdapter.Listener paramListener)
  {
    super(paramSyncRoot, paramContext, paramHandler, paramListener, "VirtualDisplayAdapter");
    this.mHandler = paramHandler;
  }
  
  private int getNextUniqueIndex(String paramString)
  {
    if (this.mVirtualDisplayDevices.isEmpty()) {
      return 0;
    }
    int i = 0;
    Iterator localIterator = this.mVirtualDisplayDevices.values().iterator();
    while (localIterator.hasNext())
    {
      VirtualDisplayDevice localVirtualDisplayDevice = (VirtualDisplayDevice)localIterator.next();
      if ((localVirtualDisplayDevice.getUniqueId().startsWith(paramString)) && (VirtualDisplayDevice.-get0(localVirtualDisplayDevice) >= i)) {
        i = VirtualDisplayDevice.-get0(localVirtualDisplayDevice) + 1;
      }
    }
    return i;
  }
  
  private void handleBinderDiedLocked(IBinder paramIBinder)
  {
    paramIBinder = (VirtualDisplayDevice)this.mVirtualDisplayDevices.remove(paramIBinder);
    if (paramIBinder != null)
    {
      Slog.i("VirtualDisplayAdapter", "Virtual display device released because application token died: " + paramIBinder.mOwnerPackageName);
      paramIBinder.destroyLocked(false);
      sendDisplayDeviceEventLocked(paramIBinder, 3);
    }
  }
  
  private void handleMediaProjectionStoppedLocked(IBinder paramIBinder)
  {
    paramIBinder = (VirtualDisplayDevice)this.mVirtualDisplayDevices.remove(paramIBinder);
    if (paramIBinder != null)
    {
      Slog.i("VirtualDisplayAdapter", "Virtual display device released because media projection stopped: " + paramIBinder.mName);
      paramIBinder.stopLocked();
    }
  }
  
  public DisplayDevice createVirtualDisplayLocked(IVirtualDisplayCallback paramIVirtualDisplayCallback, IMediaProjection paramIMediaProjection, int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3, int paramInt4, Surface paramSurface, int paramInt5)
  {
    if ((paramInt5 & 0x4) != 0) {}
    for (boolean bool = true;; bool = false)
    {
      IBinder localIBinder1 = paramIVirtualDisplayCallback.asBinder();
      IBinder localIBinder2 = SurfaceControl.createDisplay(paramString2, bool);
      String str = "virtual:" + paramString1 + "," + paramInt1 + "," + paramString2 + ",";
      int i = getNextUniqueIndex(str);
      paramIVirtualDisplayCallback = new VirtualDisplayDevice(localIBinder2, localIBinder1, paramInt1, paramString1, paramString2, paramInt2, paramInt3, paramInt4, paramSurface, paramInt5, new Callback(paramIVirtualDisplayCallback, this.mHandler), str + i, i);
      this.mVirtualDisplayDevices.put(localIBinder1, paramIVirtualDisplayCallback);
      if (paramIMediaProjection != null) {}
      try
      {
        paramIMediaProjection.registerCallback(new MediaProjectionCallback(localIBinder1));
        localIBinder1.linkToDeath(paramIVirtualDisplayCallback, 0);
        return paramIVirtualDisplayCallback;
      }
      catch (RemoteException paramIMediaProjection)
      {
        this.mVirtualDisplayDevices.remove(localIBinder1);
        paramIVirtualDisplayCallback.destroyLocked(false);
      }
    }
    return null;
  }
  
  public DisplayDevice releaseVirtualDisplayLocked(IBinder paramIBinder)
  {
    VirtualDisplayDevice localVirtualDisplayDevice = (VirtualDisplayDevice)this.mVirtualDisplayDevices.remove(paramIBinder);
    if (localVirtualDisplayDevice != null)
    {
      localVirtualDisplayDevice.destroyLocked(true);
      paramIBinder.unlinkToDeath(localVirtualDisplayDevice, 0);
    }
    return localVirtualDisplayDevice;
  }
  
  public void resizeVirtualDisplayLocked(IBinder paramIBinder, int paramInt1, int paramInt2, int paramInt3)
  {
    paramIBinder = (VirtualDisplayDevice)this.mVirtualDisplayDevices.get(paramIBinder);
    if (paramIBinder != null) {
      paramIBinder.resizeLocked(paramInt1, paramInt2, paramInt3);
    }
  }
  
  public void setVirtualDisplaySurfaceLocked(IBinder paramIBinder, Surface paramSurface)
  {
    paramIBinder = (VirtualDisplayDevice)this.mVirtualDisplayDevices.get(paramIBinder);
    if (paramIBinder != null) {
      paramIBinder.setSurfaceLocked(paramSurface);
    }
  }
  
  private static class Callback
    extends Handler
  {
    private static final int MSG_ON_DISPLAY_PAUSED = 0;
    private static final int MSG_ON_DISPLAY_RESUMED = 1;
    private static final int MSG_ON_DISPLAY_STOPPED = 2;
    private final IVirtualDisplayCallback mCallback;
    
    public Callback(IVirtualDisplayCallback paramIVirtualDisplayCallback, Handler paramHandler)
    {
      super();
      this.mCallback = paramIVirtualDisplayCallback;
    }
    
    public void dispatchDisplayPaused()
    {
      sendEmptyMessage(0);
    }
    
    public void dispatchDisplayResumed()
    {
      sendEmptyMessage(1);
    }
    
    public void dispatchDisplayStopped()
    {
      sendEmptyMessage(2);
    }
    
    public void handleMessage(Message paramMessage)
    {
      try
      {
        switch (paramMessage.what)
        {
        case 0: 
          this.mCallback.onPaused();
          return;
        }
      }
      catch (RemoteException paramMessage)
      {
        Slog.w("VirtualDisplayAdapter", "Failed to notify listener of virtual display event.", paramMessage);
        return;
      }
      this.mCallback.onResumed();
      return;
      this.mCallback.onStopped();
      return;
    }
  }
  
  private final class MediaProjectionCallback
    extends IMediaProjectionCallback.Stub
  {
    private IBinder mAppToken;
    
    public MediaProjectionCallback(IBinder paramIBinder)
    {
      this.mAppToken = paramIBinder;
    }
    
    public void onStop()
    {
      synchronized (VirtualDisplayAdapter.this.getSyncRoot())
      {
        VirtualDisplayAdapter.-wrap1(VirtualDisplayAdapter.this, this.mAppToken);
        return;
      }
    }
  }
  
  private final class VirtualDisplayDevice
    extends DisplayDevice
    implements IBinder.DeathRecipient
  {
    private static final int PENDING_RESIZE = 2;
    private static final int PENDING_SURFACE_CHANGE = 1;
    private static final float REFRESH_RATE = 60.0F;
    private final IBinder mAppToken;
    private final VirtualDisplayAdapter.Callback mCallback;
    private int mDensityDpi;
    private int mDisplayState;
    private final int mFlags;
    private int mHeight;
    private DisplayDeviceInfo mInfo;
    private Display.Mode mMode;
    final String mName;
    final String mOwnerPackageName;
    private final int mOwnerUid;
    private int mPendingChanges;
    private boolean mStopped;
    private Surface mSurface;
    private int mUniqueIndex;
    private int mWidth;
    
    public VirtualDisplayDevice(IBinder paramIBinder1, IBinder paramIBinder2, int paramInt1, String paramString1, String paramString2, int paramInt2, int paramInt3, int paramInt4, Surface paramSurface, int paramInt5, VirtualDisplayAdapter.Callback paramCallback, String paramString3, int paramInt6)
    {
      super(paramIBinder1, paramString3);
      this.mAppToken = paramIBinder2;
      this.mOwnerUid = paramInt1;
      this.mOwnerPackageName = paramString1;
      this.mName = paramString2;
      this.mWidth = paramInt2;
      this.mHeight = paramInt3;
      this.mMode = VirtualDisplayAdapter.createMode(paramInt2, paramInt3, 60.0F);
      this.mDensityDpi = paramInt4;
      this.mSurface = paramSurface;
      this.mFlags = paramInt5;
      this.mCallback = paramCallback;
      this.mDisplayState = 0;
      this.mPendingChanges |= 0x1;
      this.mUniqueIndex = paramInt6;
    }
    
    public void binderDied()
    {
      synchronized (VirtualDisplayAdapter.this.getSyncRoot())
      {
        VirtualDisplayAdapter.-wrap0(VirtualDisplayAdapter.this, this.mAppToken);
        return;
      }
    }
    
    public void destroyLocked(boolean paramBoolean)
    {
      if (this.mSurface != null)
      {
        this.mSurface.release();
        this.mSurface = null;
      }
      SurfaceControl.destroyDisplay(getDisplayTokenLocked());
      if (paramBoolean) {
        this.mCallback.dispatchDisplayStopped();
      }
    }
    
    public void dumpLocked(PrintWriter paramPrintWriter)
    {
      super.dumpLocked(paramPrintWriter);
      paramPrintWriter.println("mFlags=" + this.mFlags);
      paramPrintWriter.println("mDisplayState=" + Display.stateToString(this.mDisplayState));
      paramPrintWriter.println("mStopped=" + this.mStopped);
    }
    
    public DisplayDeviceInfo getDisplayDeviceInfoLocked()
    {
      int i = 1;
      DisplayDeviceInfo localDisplayDeviceInfo;
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
        this.mInfo.densityDpi = this.mDensityDpi;
        this.mInfo.xDpi = this.mDensityDpi;
        this.mInfo.yDpi = this.mDensityDpi;
        this.mInfo.presentationDeadlineNanos = 16666666L;
        this.mInfo.flags = 0;
        if ((this.mFlags & 0x1) == 0)
        {
          localDisplayDeviceInfo = this.mInfo;
          localDisplayDeviceInfo.flags |= 0x30;
        }
        if ((this.mFlags & 0x10) == 0) {
          break label355;
        }
        localDisplayDeviceInfo = this.mInfo;
      }
      for (localDisplayDeviceInfo.flags &= 0xFFFFFFDF;; localDisplayDeviceInfo.flags |= 0x80)
      {
        if ((this.mFlags & 0x4) != 0)
        {
          localDisplayDeviceInfo = this.mInfo;
          localDisplayDeviceInfo.flags |= 0x4;
        }
        if ((this.mFlags & 0x2) != 0)
        {
          localDisplayDeviceInfo = this.mInfo;
          localDisplayDeviceInfo.flags |= 0x40;
          if (((this.mFlags & 0x1) != 0) && ("portrait".equals(SystemProperties.get("persist.demo.remoterotation")))) {
            this.mInfo.rotation = 3;
          }
        }
        this.mInfo.type = 5;
        this.mInfo.touch = 0;
        localDisplayDeviceInfo = this.mInfo;
        if (this.mSurface != null) {
          i = 2;
        }
        localDisplayDeviceInfo.state = i;
        this.mInfo.ownerUid = this.mOwnerUid;
        this.mInfo.ownerPackageName = this.mOwnerPackageName;
        return this.mInfo;
        label355:
        localDisplayDeviceInfo = this.mInfo;
      }
    }
    
    public boolean hasStableUniqueId()
    {
      return false;
    }
    
    public void performTraversalInTransactionLocked()
    {
      if ((this.mPendingChanges & 0x2) != 0) {
        SurfaceControl.setDisplaySize(getDisplayTokenLocked(), this.mWidth, this.mHeight);
      }
      if ((this.mPendingChanges & 0x1) != 0) {
        setSurfaceInTransactionLocked(this.mSurface);
      }
      this.mPendingChanges = 0;
    }
    
    public Runnable requestDisplayStateLocked(int paramInt1, int paramInt2)
    {
      if (paramInt1 != this.mDisplayState)
      {
        this.mDisplayState = paramInt1;
        if (paramInt1 != 1) {
          break label27;
        }
        this.mCallback.dispatchDisplayPaused();
      }
      for (;;)
      {
        return null;
        label27:
        this.mCallback.dispatchDisplayResumed();
      }
    }
    
    public void resizeLocked(int paramInt1, int paramInt2, int paramInt3)
    {
      if ((this.mWidth != paramInt1) || (this.mHeight != paramInt2)) {}
      for (;;)
      {
        VirtualDisplayAdapter.this.sendDisplayDeviceEventLocked(this, 2);
        VirtualDisplayAdapter.this.sendTraversalRequestLocked();
        this.mWidth = paramInt1;
        this.mHeight = paramInt2;
        this.mMode = VirtualDisplayAdapter.createMode(paramInt1, paramInt2, 60.0F);
        this.mDensityDpi = paramInt3;
        this.mInfo = null;
        this.mPendingChanges |= 0x2;
        do
        {
          return;
        } while (this.mDensityDpi == paramInt3);
      }
    }
    
    public void setSurfaceLocked(Surface paramSurface)
    {
      int j = 1;
      int i;
      if ((!this.mStopped) && (this.mSurface != paramSurface))
      {
        if (this.mSurface == null) {
          break label72;
        }
        i = 1;
        if (paramSurface == null) {
          break label77;
        }
      }
      for (;;)
      {
        if (i != j) {
          VirtualDisplayAdapter.this.sendDisplayDeviceEventLocked(this, 2);
        }
        VirtualDisplayAdapter.this.sendTraversalRequestLocked();
        this.mSurface = paramSurface;
        this.mInfo = null;
        this.mPendingChanges |= 0x1;
        return;
        label72:
        i = 0;
        break;
        label77:
        j = 0;
      }
    }
    
    public void stopLocked()
    {
      setSurfaceLocked(null);
      this.mStopped = true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/VirtualDisplayAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */