package android.hardware.display;

import android.content.Context;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.util.SparseArray;
import android.view.Display;
import android.view.Surface;
import java.util.ArrayList;

public final class DisplayManager
{
  public static final String ACTION_WIFI_DISPLAY_STATUS_CHANGED = "android.hardware.display.action.WIFI_DISPLAY_STATUS_CHANGED";
  private static final boolean DEBUG = false;
  public static final String DISPLAY_CATEGORY_PRESENTATION = "android.hardware.display.category.PRESENTATION";
  public static final String EXTRA_WIFI_DISPLAY_STATUS = "android.hardware.display.extra.WIFI_DISPLAY_STATUS";
  private static final String TAG = "DisplayManager";
  public static final int VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR = 16;
  public static final int VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY = 8;
  public static final int VIRTUAL_DISPLAY_FLAG_PRESENTATION = 2;
  public static final int VIRTUAL_DISPLAY_FLAG_PUBLIC = 1;
  public static final int VIRTUAL_DISPLAY_FLAG_SECURE = 4;
  private final Context mContext;
  private final SparseArray<Display> mDisplays = new SparseArray();
  private final DisplayManagerGlobal mGlobal;
  private final Object mLock = new Object();
  private final ArrayList<Display> mTempDisplays = new ArrayList();
  
  public DisplayManager(Context paramContext)
  {
    this.mContext = paramContext;
    this.mGlobal = DisplayManagerGlobal.getInstance();
  }
  
  private void addAllDisplaysLocked(ArrayList<Display> paramArrayList, int[] paramArrayOfInt)
  {
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      Display localDisplay = getOrCreateDisplayLocked(paramArrayOfInt[i], true);
      if (localDisplay != null) {
        paramArrayList.add(localDisplay);
      }
      i += 1;
    }
  }
  
  private void addPresentationDisplaysLocked(ArrayList<Display> paramArrayList, int[] paramArrayOfInt, int paramInt)
  {
    int i = 0;
    while (i < paramArrayOfInt.length)
    {
      Display localDisplay = getOrCreateDisplayLocked(paramArrayOfInt[i], true);
      if ((localDisplay != null) && ((localDisplay.getFlags() & 0x8) != 0) && (localDisplay.getType() == paramInt)) {
        paramArrayList.add(localDisplay);
      }
      i += 1;
    }
  }
  
  private Display getOrCreateDisplayLocked(int paramInt, boolean paramBoolean)
  {
    Display localDisplay2 = (Display)this.mDisplays.get(paramInt);
    Display localDisplay1;
    if (localDisplay2 == null)
    {
      localDisplay2 = this.mGlobal.getCompatibleDisplay(paramInt, this.mContext.getDisplayAdjustments(paramInt));
      localDisplay1 = localDisplay2;
      if (localDisplay2 != null)
      {
        this.mDisplays.put(paramInt, localDisplay2);
        localDisplay1 = localDisplay2;
      }
    }
    do
    {
      do
      {
        return localDisplay1;
        localDisplay1 = localDisplay2;
      } while (paramBoolean);
      localDisplay1 = localDisplay2;
    } while (localDisplay2.isValid());
    return null;
  }
  
  public void connectWifiDisplay(String paramString)
  {
    this.mGlobal.connectWifiDisplay(paramString);
  }
  
  public VirtualDisplay createVirtualDisplay(MediaProjection paramMediaProjection, String paramString, int paramInt1, int paramInt2, int paramInt3, Surface paramSurface, int paramInt4, VirtualDisplay.Callback paramCallback, Handler paramHandler)
  {
    return this.mGlobal.createVirtualDisplay(this.mContext, paramMediaProjection, paramString, paramInt1, paramInt2, paramInt3, paramSurface, paramInt4, paramCallback, paramHandler);
  }
  
  public VirtualDisplay createVirtualDisplay(String paramString, int paramInt1, int paramInt2, int paramInt3, Surface paramSurface, int paramInt4)
  {
    return createVirtualDisplay(paramString, paramInt1, paramInt2, paramInt3, paramSurface, paramInt4, null, null);
  }
  
  public VirtualDisplay createVirtualDisplay(String paramString, int paramInt1, int paramInt2, int paramInt3, Surface paramSurface, int paramInt4, VirtualDisplay.Callback paramCallback, Handler paramHandler)
  {
    return createVirtualDisplay(null, paramString, paramInt1, paramInt2, paramInt3, paramSurface, paramInt4, paramCallback, paramHandler);
  }
  
  public void disconnectWifiDisplay()
  {
    this.mGlobal.disconnectWifiDisplay();
  }
  
  public void forgetWifiDisplay(String paramString)
  {
    this.mGlobal.forgetWifiDisplay(paramString);
  }
  
  public Display getDisplay(int paramInt)
  {
    synchronized (this.mLock)
    {
      Display localDisplay = getOrCreateDisplayLocked(paramInt, false);
      return localDisplay;
    }
  }
  
  public Display[] getDisplays()
  {
    return getDisplays(null);
  }
  
  public Display[] getDisplays(String paramString)
  {
    int[] arrayOfInt = this.mGlobal.getDisplayIds();
    localObject = this.mLock;
    if (paramString == null) {}
    for (;;)
    {
      try
      {
        addAllDisplaysLocked(this.mTempDisplays, arrayOfInt);
        paramString = (Display[])this.mTempDisplays.toArray(new Display[this.mTempDisplays.size()]);
      }
      finally
      {
        this.mTempDisplays.clear();
      }
      try
      {
        this.mTempDisplays.clear();
        return paramString;
      }
      finally {}
      if (paramString.equals("android.hardware.display.category.PRESENTATION"))
      {
        addPresentationDisplaysLocked(this.mTempDisplays, arrayOfInt, 3);
        addPresentationDisplaysLocked(this.mTempDisplays, arrayOfInt, 2);
        addPresentationDisplaysLocked(this.mTempDisplays, arrayOfInt, 4);
        addPresentationDisplaysLocked(this.mTempDisplays, arrayOfInt, 5);
      }
    }
  }
  
  public WifiDisplayStatus getWifiDisplayStatus()
  {
    return this.mGlobal.getWifiDisplayStatus();
  }
  
  public void pauseWifiDisplay()
  {
    this.mGlobal.pauseWifiDisplay();
  }
  
  public void registerDisplayListener(DisplayListener paramDisplayListener, Handler paramHandler)
  {
    this.mGlobal.registerDisplayListener(paramDisplayListener, paramHandler);
  }
  
  public void renameWifiDisplay(String paramString1, String paramString2)
  {
    this.mGlobal.renameWifiDisplay(paramString1, paramString2);
  }
  
  public void resumeWifiDisplay()
  {
    this.mGlobal.resumeWifiDisplay();
  }
  
  public void startWifiDisplayScan()
  {
    this.mGlobal.startWifiDisplayScan();
  }
  
  public void stopWifiDisplayScan()
  {
    this.mGlobal.stopWifiDisplayScan();
  }
  
  public void unregisterDisplayListener(DisplayListener paramDisplayListener)
  {
    this.mGlobal.unregisterDisplayListener(paramDisplayListener);
  }
  
  public static abstract interface DisplayListener
  {
    public abstract void onDisplayAdded(int paramInt);
    
    public abstract void onDisplayChanged(int paramInt);
    
    public abstract void onDisplayRemoved(int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/DisplayManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */