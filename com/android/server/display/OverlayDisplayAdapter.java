package com.android.server.display;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings.Global;
import android.util.Slog;
import android.view.Display.Mode;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class OverlayDisplayAdapter
  extends DisplayAdapter
{
  static final boolean DEBUG = false;
  private static final Pattern DISPLAY_PATTERN = Pattern.compile("([^,]+)(,[a-z]+)*");
  private static final int MAX_HEIGHT = 4096;
  private static final int MAX_WIDTH = 4096;
  private static final int MIN_HEIGHT = 100;
  private static final int MIN_WIDTH = 100;
  private static final Pattern MODE_PATTERN = Pattern.compile("(\\d+)x(\\d+)/(\\d+)");
  static final String TAG = "OverlayDisplayAdapter";
  private static final String UNIQUE_ID_PREFIX = "overlay:";
  private String mCurrentOverlaySetting = "";
  private final ArrayList<OverlayDisplayHandle> mOverlays = new ArrayList();
  private final Handler mUiHandler;
  
  public OverlayDisplayAdapter(DisplayManagerService.SyncRoot paramSyncRoot, Context paramContext, Handler paramHandler1, DisplayAdapter.Listener paramListener, Handler paramHandler2)
  {
    super(paramSyncRoot, paramContext, paramHandler1, paramListener, "OverlayDisplayAdapter");
    this.mUiHandler = paramHandler2;
  }
  
  private static int chooseOverlayGravity(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return 83;
    case 1: 
      return 51;
    case 2: 
      return 85;
    }
    return 53;
  }
  
  private void updateOverlayDisplayDevices()
  {
    synchronized (getSyncRoot())
    {
      updateOverlayDisplayDevicesLocked();
      return;
    }
  }
  
  private void updateOverlayDisplayDevicesLocked()
  {
    Object localObject2 = Settings.Global.getString(getContext().getContentResolver(), "overlay_display_devices");
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = "";
    }
    if (((String)localObject1).equals(this.mCurrentOverlaySetting)) {
      return;
    }
    this.mCurrentOverlaySetting = ((String)localObject1);
    if (!this.mOverlays.isEmpty())
    {
      Slog.i("OverlayDisplayAdapter", "Dismissing all overlay display devices.");
      localObject2 = this.mOverlays.iterator();
      while (((Iterator)localObject2).hasNext()) {
        ((OverlayDisplayHandle)((Iterator)localObject2).next()).dismissLocked();
      }
      this.mOverlays.clear();
    }
    int j = 0;
    localObject2 = ((String)localObject1).split(";");
    int m = localObject2.length;
    int i = 0;
    if (i < m)
    {
      localObject3 = localObject2[i];
      localObject3 = DISPLAY_PATTERN.matcher((CharSequence)localObject3);
      if (!((Matcher)localObject3).matches()) {
        break label554;
      }
      if (j >= 4) {
        Slog.w("OverlayDisplayAdapter", "Too many overlay display devices specified: " + (String)localObject1);
      }
    }
    else
    {
      return;
    }
    Object localObject4 = ((Matcher)localObject3).group(1);
    Object localObject3 = ((Matcher)localObject3).group(2);
    ArrayList localArrayList = new ArrayList();
    localObject4 = ((String)localObject4).split("\\|");
    int k = 0;
    int n = localObject4.length;
    for (;;)
    {
      if (k < n)
      {
        CharSequence localCharSequence = localObject4[k];
        Matcher localMatcher = MODE_PATTERN.matcher(localCharSequence);
        if (localMatcher.matches()) {
          try
          {
            int i1 = Integer.parseInt(localMatcher.group(1), 10);
            int i2 = Integer.parseInt(localMatcher.group(2), 10);
            int i3 = Integer.parseInt(localMatcher.group(3), 10);
            if ((i1 >= 100) && (i1 <= 4096) && (i2 >= 100) && (i2 <= 4096) && (i3 >= 120) && (i3 <= 640)) {
              localArrayList.add(new OverlayMode(i1, i2, i3));
            } else {
              Slog.w("OverlayDisplayAdapter", "Ignoring out-of-range overlay display mode: " + localCharSequence);
            }
          }
          catch (NumberFormatException localNumberFormatException) {}
        }
        if (!localNumberFormatException.isEmpty()) {}
      }
      else
      {
        boolean bool;
        if (!localArrayList.isEmpty())
        {
          j += 1;
          localObject4 = getContext().getResources().getString(17040669, new Object[] { Integer.valueOf(j) });
          k = chooseOverlayGravity(j);
          if (localObject3 != null)
          {
            bool = ((String)localObject3).contains(",secure");
            label466:
            Slog.i("OverlayDisplayAdapter", "Showing overlay display device #" + j + ": name=" + (String)localObject4 + ", modes=" + Arrays.toString(localArrayList.toArray()));
            this.mOverlays.add(new OverlayDisplayHandle((String)localObject4, localArrayList, k, bool, j));
          }
        }
        for (;;)
        {
          i += 1;
          break;
          bool = false;
          break label466;
          label554:
          Slog.w("OverlayDisplayAdapter", "Malformed overlay display devices setting: " + (String)localObject1);
        }
      }
      k += 1;
    }
  }
  
  public void dumpLocked(PrintWriter paramPrintWriter)
  {
    super.dumpLocked(paramPrintWriter);
    paramPrintWriter.println("mCurrentOverlaySetting=" + this.mCurrentOverlaySetting);
    paramPrintWriter.println("mOverlays: size=" + this.mOverlays.size());
    Iterator localIterator = this.mOverlays.iterator();
    while (localIterator.hasNext()) {
      ((OverlayDisplayHandle)localIterator.next()).dumpLocked(paramPrintWriter);
    }
  }
  
  public void registerLocked()
  {
    super.registerLocked();
    getHandler().post(new Runnable()
    {
      public void run()
      {
        OverlayDisplayAdapter.this.getContext().getContentResolver().registerContentObserver(Settings.Global.getUriFor("overlay_display_devices"), true, new ContentObserver(OverlayDisplayAdapter.this.getHandler())
        {
          public void onChange(boolean paramAnonymous2Boolean)
          {
            OverlayDisplayAdapter.-wrap0(OverlayDisplayAdapter.this);
          }
        });
        OverlayDisplayAdapter.-wrap0(OverlayDisplayAdapter.this);
      }
    });
  }
  
  private abstract class OverlayDisplayDevice
    extends DisplayDevice
  {
    private int mActiveMode;
    private final int mDefaultMode;
    private final long mDisplayPresentationDeadlineNanos;
    private DisplayDeviceInfo mInfo;
    private final Display.Mode[] mModes;
    private final String mName;
    private final List<OverlayDisplayAdapter.OverlayMode> mRawModes;
    private final float mRefreshRate;
    private final boolean mSecure;
    private int mState;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    
    public OverlayDisplayDevice(String paramString, List<OverlayDisplayAdapter.OverlayMode> paramList, int paramInt1, int paramInt2, float paramFloat, long paramLong, boolean paramBoolean, int paramInt3, SurfaceTexture paramSurfaceTexture, int paramInt4)
    {
      super(paramString, "overlay:" + i);
      this.mName = paramList;
      this.mRefreshRate = paramLong;
      this.mDisplayPresentationDeadlineNanos = ???;
      this.mSecure = paramInt3;
      this.mState = paramSurfaceTexture;
      this.mSurfaceTexture = paramInt4;
      this.mRawModes = paramInt1;
      this.mModes = new Display.Mode[paramInt1.size()];
      paramSurfaceTexture = 0;
      while (paramSurfaceTexture < paramInt1.size())
      {
        this$1 = (OverlayDisplayAdapter.OverlayMode)paramInt1.get(paramSurfaceTexture);
        this.mModes[paramSurfaceTexture] = OverlayDisplayAdapter.createMode(OverlayDisplayAdapter.this.mWidth, OverlayDisplayAdapter.this.mHeight, paramLong);
        paramSurfaceTexture += 1;
      }
      this.mActiveMode = paramInt2;
      this.mDefaultMode = paramFloat;
    }
    
    public void destroyLocked()
    {
      this.mSurfaceTexture = null;
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
        Object localObject = this.mModes[this.mActiveMode];
        OverlayDisplayAdapter.OverlayMode localOverlayMode = (OverlayDisplayAdapter.OverlayMode)this.mRawModes.get(this.mActiveMode);
        this.mInfo = new DisplayDeviceInfo();
        this.mInfo.name = this.mName;
        this.mInfo.uniqueId = getUniqueId();
        this.mInfo.width = ((Display.Mode)localObject).getPhysicalWidth();
        this.mInfo.height = ((Display.Mode)localObject).getPhysicalHeight();
        this.mInfo.modeId = ((Display.Mode)localObject).getModeId();
        this.mInfo.defaultModeId = this.mModes[0].getModeId();
        this.mInfo.supportedModes = this.mModes;
        this.mInfo.densityDpi = localOverlayMode.mDensityDpi;
        this.mInfo.xDpi = localOverlayMode.mDensityDpi;
        this.mInfo.yDpi = localOverlayMode.mDensityDpi;
        this.mInfo.presentationDeadlineNanos = (this.mDisplayPresentationDeadlineNanos + 1000000000L / (int)this.mRefreshRate);
        this.mInfo.flags = 64;
        if (this.mSecure)
        {
          localObject = this.mInfo;
          ((DisplayDeviceInfo)localObject).flags |= 0x4;
        }
        this.mInfo.type = 4;
        this.mInfo.touch = 0;
        this.mInfo.state = this.mState;
      }
      return this.mInfo;
    }
    
    public boolean hasStableUniqueId()
    {
      return false;
    }
    
    public abstract void onModeChangedLocked(int paramInt);
    
    public void performTraversalInTransactionLocked()
    {
      if (this.mSurfaceTexture != null)
      {
        if (this.mSurface == null) {
          this.mSurface = new Surface(this.mSurfaceTexture);
        }
        setSurfaceInTransactionLocked(this.mSurface);
      }
    }
    
    public void requestDisplayModesInTransactionLocked(int paramInt1, int paramInt2)
    {
      int j = -1;
      int i;
      if (paramInt2 == 0)
      {
        paramInt1 = 0;
        i = paramInt1;
        if (paramInt1 == -1)
        {
          Slog.w("OverlayDisplayAdapter", "Unable to locate mode " + paramInt2 + ", reverting to default.");
          i = this.mDefaultMode;
        }
        if (this.mActiveMode != i) {}
      }
      else
      {
        i = 0;
        for (;;)
        {
          paramInt1 = j;
          if (i >= this.mModes.length) {
            break;
          }
          if (this.mModes[i].getModeId() == paramInt2)
          {
            paramInt1 = i;
            break;
          }
          i += 1;
        }
      }
      this.mActiveMode = i;
      this.mInfo = null;
      OverlayDisplayAdapter.this.sendDisplayDeviceEventLocked(this, 2);
      onModeChangedLocked(i);
    }
    
    public void setStateLocked(int paramInt)
    {
      this.mState = paramInt;
      this.mInfo = null;
    }
  }
  
  private final class OverlayDisplayHandle
    implements OverlayDisplayWindow.Listener
  {
    private static final int DEFAULT_MODE_INDEX = 0;
    private int mActiveMode;
    private OverlayDisplayAdapter.OverlayDisplayDevice mDevice;
    private final Runnable mDismissRunnable = new Runnable()
    {
      public void run()
      {
        synchronized (OverlayDisplayAdapter.this.getSyncRoot())
        {
          OverlayDisplayWindow localOverlayDisplayWindow = OverlayDisplayAdapter.OverlayDisplayHandle.-get5(OverlayDisplayAdapter.OverlayDisplayHandle.this);
          OverlayDisplayAdapter.OverlayDisplayHandle.-set0(OverlayDisplayAdapter.OverlayDisplayHandle.this, null);
          if (localOverlayDisplayWindow != null) {
            localOverlayDisplayWindow.dismiss();
          }
          return;
        }
      }
    };
    private final int mGravity;
    private final List<OverlayDisplayAdapter.OverlayMode> mModes;
    private final String mName;
    private final int mNumber;
    private final Runnable mResizeRunnable = new Runnable()
    {
      public void run()
      {
        synchronized (OverlayDisplayAdapter.this.getSyncRoot())
        {
          Object localObject1 = OverlayDisplayAdapter.OverlayDisplayHandle.-get5(OverlayDisplayAdapter.OverlayDisplayHandle.this);
          if (localObject1 == null) {
            return;
          }
          localObject1 = (OverlayDisplayAdapter.OverlayMode)OverlayDisplayAdapter.OverlayDisplayHandle.-get2(OverlayDisplayAdapter.OverlayDisplayHandle.this).get(OverlayDisplayAdapter.OverlayDisplayHandle.-get0(OverlayDisplayAdapter.OverlayDisplayHandle.this));
          OverlayDisplayWindow localOverlayDisplayWindow = OverlayDisplayAdapter.OverlayDisplayHandle.-get5(OverlayDisplayAdapter.OverlayDisplayHandle.this);
          localOverlayDisplayWindow.resize(((OverlayDisplayAdapter.OverlayMode)localObject1).mWidth, ((OverlayDisplayAdapter.OverlayMode)localObject1).mHeight, ((OverlayDisplayAdapter.OverlayMode)localObject1).mDensityDpi);
          return;
        }
      }
    };
    private final boolean mSecure;
    private final Runnable mShowRunnable = new Runnable()
    {
      public void run()
      {
        ??? = (OverlayDisplayAdapter.OverlayMode)OverlayDisplayAdapter.OverlayDisplayHandle.-get2(OverlayDisplayAdapter.OverlayDisplayHandle.this).get(OverlayDisplayAdapter.OverlayDisplayHandle.-get0(OverlayDisplayAdapter.OverlayDisplayHandle.this));
        OverlayDisplayWindow localOverlayDisplayWindow = new OverlayDisplayWindow(OverlayDisplayAdapter.this.getContext(), OverlayDisplayAdapter.OverlayDisplayHandle.-get3(OverlayDisplayAdapter.OverlayDisplayHandle.this), ((OverlayDisplayAdapter.OverlayMode)???).mWidth, ((OverlayDisplayAdapter.OverlayMode)???).mHeight, ((OverlayDisplayAdapter.OverlayMode)???).mDensityDpi, OverlayDisplayAdapter.OverlayDisplayHandle.-get1(OverlayDisplayAdapter.OverlayDisplayHandle.this), OverlayDisplayAdapter.OverlayDisplayHandle.-get4(OverlayDisplayAdapter.OverlayDisplayHandle.this), OverlayDisplayAdapter.OverlayDisplayHandle.this);
        localOverlayDisplayWindow.show();
        synchronized (OverlayDisplayAdapter.this.getSyncRoot())
        {
          OverlayDisplayAdapter.OverlayDisplayHandle.-set0(OverlayDisplayAdapter.OverlayDisplayHandle.this, localOverlayDisplayWindow);
          return;
        }
      }
    };
    private OverlayDisplayWindow mWindow;
    
    public OverlayDisplayHandle(List<OverlayDisplayAdapter.OverlayMode> paramList, int paramInt1, boolean paramBoolean, int paramInt2)
    {
      this.mName = paramList;
      this.mModes = paramInt1;
      this.mGravity = paramBoolean;
      this.mSecure = paramInt2;
      int i;
      this.mNumber = i;
      this.mActiveMode = 0;
      showLocked();
    }
    
    private void onActiveModeChangedLocked(int paramInt)
    {
      OverlayDisplayAdapter.-get0(OverlayDisplayAdapter.this).removeCallbacks(this.mResizeRunnable);
      this.mActiveMode = paramInt;
      if (this.mWindow != null) {
        OverlayDisplayAdapter.-get0(OverlayDisplayAdapter.this).post(this.mResizeRunnable);
      }
    }
    
    private void showLocked()
    {
      OverlayDisplayAdapter.-get0(OverlayDisplayAdapter.this).post(this.mShowRunnable);
    }
    
    public void dismissLocked()
    {
      OverlayDisplayAdapter.-get0(OverlayDisplayAdapter.this).removeCallbacks(this.mShowRunnable);
      OverlayDisplayAdapter.-get0(OverlayDisplayAdapter.this).post(this.mDismissRunnable);
    }
    
    public void dumpLocked(PrintWriter paramPrintWriter)
    {
      paramPrintWriter.println("  " + this.mName + ":");
      paramPrintWriter.println("    mModes=" + Arrays.toString(this.mModes.toArray()));
      paramPrintWriter.println("    mActiveMode=" + this.mActiveMode);
      paramPrintWriter.println("    mGravity=" + this.mGravity);
      paramPrintWriter.println("    mSecure=" + this.mSecure);
      paramPrintWriter.println("    mNumber=" + this.mNumber);
      if (this.mWindow != null)
      {
        paramPrintWriter = new IndentingPrintWriter(paramPrintWriter, "    ");
        paramPrintWriter.increaseIndent();
        DumpUtils.dumpAsync(OverlayDisplayAdapter.-get0(OverlayDisplayAdapter.this), this.mWindow, paramPrintWriter, "", 200L);
      }
    }
    
    public void onStateChanged(int paramInt)
    {
      synchronized (OverlayDisplayAdapter.this.getSyncRoot())
      {
        if (this.mDevice != null)
        {
          this.mDevice.setStateLocked(paramInt);
          OverlayDisplayAdapter.this.sendDisplayDeviceEventLocked(this.mDevice, 2);
        }
        return;
      }
    }
    
    public void onWindowCreated(SurfaceTexture paramSurfaceTexture, float paramFloat, long paramLong, int paramInt)
    {
      synchronized (OverlayDisplayAdapter.this.getSyncRoot())
      {
        IBinder localIBinder = SurfaceControl.createDisplay(this.mName, this.mSecure);
        this.mDevice = new OverlayDisplayAdapter.OverlayDisplayDevice(OverlayDisplayAdapter.this, localIBinder, this.mName, this.mModes, this.mActiveMode, 0, paramFloat, paramLong, this.mSecure, paramInt, paramSurfaceTexture, this.mNumber)
        {
          public void onModeChangedLocked(int paramAnonymousInt)
          {
            OverlayDisplayAdapter.OverlayDisplayHandle.-wrap0(OverlayDisplayAdapter.OverlayDisplayHandle.this, paramAnonymousInt);
          }
        };
        OverlayDisplayAdapter.this.sendDisplayDeviceEventLocked(this.mDevice, 1);
        return;
      }
    }
    
    public void onWindowDestroyed()
    {
      synchronized (OverlayDisplayAdapter.this.getSyncRoot())
      {
        if (this.mDevice != null)
        {
          this.mDevice.destroyLocked();
          OverlayDisplayAdapter.this.sendDisplayDeviceEventLocked(this.mDevice, 3);
        }
        return;
      }
    }
  }
  
  private static final class OverlayMode
  {
    final int mDensityDpi;
    final int mHeight;
    final int mWidth;
    
    OverlayMode(int paramInt1, int paramInt2, int paramInt3)
    {
      this.mWidth = paramInt1;
      this.mHeight = paramInt2;
      this.mDensityDpi = paramInt3;
    }
    
    public String toString()
    {
      return "{" + "width=" + this.mWidth + ", height=" + this.mHeight + ", densityDpi=" + this.mDensityDpi + "}";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/OverlayDisplayAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */