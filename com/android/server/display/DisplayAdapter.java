package com.android.server.display;

import android.content.Context;
import android.os.Handler;
import android.view.Display.Mode;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

abstract class DisplayAdapter
{
  public static final int DISPLAY_DEVICE_EVENT_ADDED = 1;
  public static final int DISPLAY_DEVICE_EVENT_CHANGED = 2;
  public static final int DISPLAY_DEVICE_EVENT_REMOVED = 3;
  private static final AtomicInteger NEXT_DISPLAY_MODE_ID = new AtomicInteger(1);
  private final Context mContext;
  private final Handler mHandler;
  private final Listener mListener;
  private final String mName;
  private final DisplayManagerService.SyncRoot mSyncRoot;
  
  public DisplayAdapter(DisplayManagerService.SyncRoot paramSyncRoot, Context paramContext, Handler paramHandler, Listener paramListener, String paramString)
  {
    this.mSyncRoot = paramSyncRoot;
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.mListener = paramListener;
    this.mName = paramString;
  }
  
  public static Display.Mode createMode(int paramInt1, int paramInt2, float paramFloat)
  {
    return new Display.Mode(NEXT_DISPLAY_MODE_ID.getAndIncrement(), paramInt1, paramInt2, paramFloat);
  }
  
  public void dumpLocked(PrintWriter paramPrintWriter) {}
  
  public final Context getContext()
  {
    return this.mContext;
  }
  
  public final Handler getHandler()
  {
    return this.mHandler;
  }
  
  public final String getName()
  {
    return this.mName;
  }
  
  public final DisplayManagerService.SyncRoot getSyncRoot()
  {
    return this.mSyncRoot;
  }
  
  public void registerLocked() {}
  
  protected final void sendDisplayDeviceEventLocked(final DisplayDevice paramDisplayDevice, final int paramInt)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        DisplayAdapter.-get0(DisplayAdapter.this).onDisplayDeviceEvent(paramDisplayDevice, paramInt);
      }
    });
  }
  
  protected final void sendTraversalRequestLocked()
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        DisplayAdapter.-get0(DisplayAdapter.this).onTraversalRequested();
      }
    });
  }
  
  public static abstract interface Listener
  {
    public abstract void onDisplayDeviceEvent(DisplayDevice paramDisplayDevice, int paramInt);
    
    public abstract void onTraversalRequested();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/display/DisplayAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */