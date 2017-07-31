package android.service.wallpaper;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Display;
import android.view.IWindowSession;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceHolder.Callback2;
import android.view.WindowInsets;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManagerGlobal;
import com.android.internal.R.styleable;
import com.android.internal.os.HandlerCaller;
import com.android.internal.os.HandlerCaller.Callback;
import com.android.internal.view.BaseIWindow;
import com.android.internal.view.BaseSurfaceHolder;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public abstract class WallpaperService
  extends Service
{
  static final boolean DEBUG = false;
  private static final int DO_ATTACH = 10;
  private static final int DO_DETACH = 20;
  private static final int DO_SET_DESIRED_SIZE = 30;
  private static final int DO_SET_DISPLAY_PADDING = 40;
  private static final int MSG_TOUCH_EVENT = 10040;
  private static final int MSG_UPDATE_SURFACE = 10000;
  private static final int MSG_VISIBILITY_CHANGED = 10010;
  private static final int MSG_WALLPAPER_COMMAND = 10025;
  private static final int MSG_WALLPAPER_OFFSETS = 10020;
  private static final int MSG_WINDOW_MOVED = 10035;
  private static final int MSG_WINDOW_RESIZED = 10030;
  public static final String SERVICE_INTERFACE = "android.service.wallpaper.WallpaperService";
  public static final String SERVICE_META_DATA = "android.service.wallpaper";
  static final String TAG = "WallpaperService";
  private final ArrayList<Engine> mActiveEngines = new ArrayList();
  
  protected void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print("State of wallpaper ");
    paramPrintWriter.print(this);
    paramPrintWriter.println(":");
    int i = 0;
    while (i < this.mActiveEngines.size())
    {
      Engine localEngine = (Engine)this.mActiveEngines.get(i);
      paramPrintWriter.print("  Engine ");
      paramPrintWriter.print(localEngine);
      paramPrintWriter.println(":");
      localEngine.dump("    ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
      i += 1;
    }
  }
  
  public final IBinder onBind(Intent paramIntent)
  {
    return new IWallpaperServiceWrapper(this);
  }
  
  public void onCreate()
  {
    super.onCreate();
  }
  
  public abstract Engine onCreateEngine();
  
  public void onDestroy()
  {
    super.onDestroy();
    int i = 0;
    while (i < this.mActiveEngines.size())
    {
      ((Engine)this.mActiveEngines.get(i)).detach();
      i += 1;
    }
    this.mActiveEngines.clear();
  }
  
  public class Engine
  {
    final Rect mBackdropFrame = new Rect();
    HandlerCaller mCaller;
    final Configuration mConfiguration = new Configuration();
    IWallpaperConnection mConnection;
    final Rect mContentInsets = new Rect();
    boolean mCreated;
    int mCurHeight;
    int mCurWidth;
    int mCurWindowFlags = this.mWindowFlags;
    int mCurWindowPrivateFlags = this.mWindowPrivateFlags;
    boolean mDestroyed;
    final Rect mDispatchedContentInsets = new Rect();
    final Rect mDispatchedOutsets = new Rect();
    final Rect mDispatchedOverscanInsets = new Rect();
    final Rect mDispatchedStableInsets = new Rect();
    Display mDisplay;
    private final DisplayManager.DisplayListener mDisplayListener = new DisplayManager.DisplayListener()
    {
      public void onDisplayAdded(int paramAnonymousInt) {}
      
      public void onDisplayChanged(int paramAnonymousInt)
      {
        if (WallpaperService.Engine.this.mDisplay.getDisplayId() == paramAnonymousInt) {
          WallpaperService.Engine.this.reportVisibility();
        }
      }
      
      public void onDisplayRemoved(int paramAnonymousInt) {}
    };
    DisplayManager mDisplayManager;
    private int mDisplayState;
    boolean mDrawingAllowed;
    final Rect mFinalStableInsets = new Rect();
    final Rect mFinalSystemInsets = new Rect();
    boolean mFixedSizeAllowed;
    int mFormat;
    int mHeight;
    WallpaperService.IWallpaperEngineWrapper mIWallpaperEngine;
    boolean mInitializing = true;
    InputChannel mInputChannel;
    WallpaperInputEventReceiver mInputEventReceiver;
    boolean mIsCreating;
    final WindowManager.LayoutParams mLayout = new WindowManager.LayoutParams();
    final Object mLock = new Object();
    boolean mOffsetMessageEnqueued;
    boolean mOffsetsChanged;
    final Rect mOutsets = new Rect();
    final Rect mOverscanInsets = new Rect();
    MotionEvent mPendingMove;
    boolean mPendingSync;
    float mPendingXOffset;
    float mPendingXOffsetStep;
    float mPendingYOffset;
    float mPendingYOffsetStep;
    boolean mReportedVisible;
    IWindowSession mSession;
    final Rect mStableInsets = new Rect();
    boolean mSurfaceCreated;
    final BaseSurfaceHolder mSurfaceHolder = new BaseSurfaceHolder()
    {
      public boolean isCreating()
      {
        return WallpaperService.Engine.this.mIsCreating;
      }
      
      public Canvas lockCanvas()
      {
        if ((WallpaperService.Engine.-get0(WallpaperService.Engine.this) == 3) || (WallpaperService.Engine.-get0(WallpaperService.Engine.this) == 4)) {}
        try
        {
          WallpaperService.Engine.this.mSession.pokeDrawLock(WallpaperService.Engine.this.mWindow);
          return super.lockCanvas();
        }
        catch (RemoteException localRemoteException)
        {
          for (;;) {}
        }
      }
      
      public boolean onAllowLockCanvas()
      {
        return WallpaperService.Engine.this.mDrawingAllowed;
      }
      
      public void onRelayoutContainer()
      {
        Message localMessage = WallpaperService.Engine.this.mCaller.obtainMessage(10000);
        WallpaperService.Engine.this.mCaller.sendMessage(localMessage);
      }
      
      public void onUpdateSurface()
      {
        Message localMessage = WallpaperService.Engine.this.mCaller.obtainMessage(10000);
        WallpaperService.Engine.this.mCaller.sendMessage(localMessage);
      }
      
      public void setFixedSize(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        if (!WallpaperService.Engine.this.mFixedSizeAllowed) {
          throw new UnsupportedOperationException("Wallpapers currently only support sizing from layout");
        }
        super.setFixedSize(paramAnonymousInt1, paramAnonymousInt2);
      }
      
      public void setKeepScreenOn(boolean paramAnonymousBoolean)
      {
        throw new UnsupportedOperationException("Wallpapers do not support keep screen on");
      }
    };
    int mType;
    boolean mVisible;
    final Rect mVisibleInsets = new Rect();
    int mWidth;
    final Rect mWinFrame = new Rect();
    final BaseIWindow mWindow = new BaseIWindow()
    {
      public void dispatchAppVisibility(boolean paramAnonymousBoolean)
      {
        Object localObject;
        if (!WallpaperService.Engine.this.mIWallpaperEngine.mIsPreview)
        {
          localObject = WallpaperService.Engine.this.mCaller;
          if (!paramAnonymousBoolean) {
            break label48;
          }
        }
        label48:
        for (int i = 1;; i = 0)
        {
          localObject = ((HandlerCaller)localObject).obtainMessageI(10010, i);
          WallpaperService.Engine.this.mCaller.sendMessage((Message)localObject);
          return;
        }
      }
      
      public void dispatchWallpaperCommand(String paramAnonymousString, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, Bundle paramAnonymousBundle, boolean paramAnonymousBoolean)
      {
        synchronized (WallpaperService.Engine.this.mLock)
        {
          WallpaperService.WallpaperCommand localWallpaperCommand = new WallpaperService.WallpaperCommand();
          localWallpaperCommand.action = paramAnonymousString;
          localWallpaperCommand.x = paramAnonymousInt1;
          localWallpaperCommand.y = paramAnonymousInt2;
          localWallpaperCommand.z = paramAnonymousInt3;
          localWallpaperCommand.extras = paramAnonymousBundle;
          localWallpaperCommand.sync = paramAnonymousBoolean;
          paramAnonymousString = WallpaperService.Engine.this.mCaller.obtainMessage(10025);
          paramAnonymousString.obj = localWallpaperCommand;
          WallpaperService.Engine.this.mCaller.sendMessage(paramAnonymousString);
          return;
        }
      }
      
      public void dispatchWallpaperOffsets(float paramAnonymousFloat1, float paramAnonymousFloat2, float paramAnonymousFloat3, float paramAnonymousFloat4, boolean paramAnonymousBoolean)
      {
        synchronized (WallpaperService.Engine.this.mLock)
        {
          WallpaperService.Engine.this.mPendingXOffset = paramAnonymousFloat1;
          WallpaperService.Engine.this.mPendingYOffset = paramAnonymousFloat2;
          WallpaperService.Engine.this.mPendingXOffsetStep = paramAnonymousFloat3;
          WallpaperService.Engine.this.mPendingYOffsetStep = paramAnonymousFloat4;
          if (paramAnonymousBoolean) {
            WallpaperService.Engine.this.mPendingSync = true;
          }
          if (!WallpaperService.Engine.this.mOffsetMessageEnqueued)
          {
            WallpaperService.Engine.this.mOffsetMessageEnqueued = true;
            Message localMessage = WallpaperService.Engine.this.mCaller.obtainMessage(10020);
            WallpaperService.Engine.this.mCaller.sendMessage(localMessage);
          }
          return;
        }
      }
      
      public void moved(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        Message localMessage = WallpaperService.Engine.this.mCaller.obtainMessageII(10035, paramAnonymousInt1, paramAnonymousInt2);
        WallpaperService.Engine.this.mCaller.sendMessage(localMessage);
      }
      
      public void resized(Rect paramAnonymousRect1, Rect paramAnonymousRect2, Rect paramAnonymousRect3, Rect paramAnonymousRect4, Rect paramAnonymousRect5, Rect paramAnonymousRect6, boolean paramAnonymousBoolean1, Configuration paramAnonymousConfiguration, Rect paramAnonymousRect7, boolean paramAnonymousBoolean2, boolean paramAnonymousBoolean3)
      {
        paramAnonymousRect1 = WallpaperService.Engine.this.mCaller;
        if (paramAnonymousBoolean1) {}
        for (int i = 1;; i = 0)
        {
          paramAnonymousRect1 = paramAnonymousRect1.obtainMessageIO(10030, i, paramAnonymousRect6);
          WallpaperService.Engine.this.mCaller.sendMessage(paramAnonymousRect1);
          return;
        }
      }
    };
    int mWindowFlags = 16;
    int mWindowPrivateFlags = 4;
    IBinder mWindowToken;
    
    public Engine() {}
    
    private void dispatchPointer(MotionEvent paramMotionEvent)
    {
      if (paramMotionEvent.isTouchEvent()) {
        synchronized (this.mLock)
        {
          if (paramMotionEvent.getAction() == 2)
          {
            this.mPendingMove = paramMotionEvent;
            paramMotionEvent = this.mCaller.obtainMessageO(10040, paramMotionEvent);
            this.mCaller.sendMessage(paramMotionEvent);
            return;
          }
          this.mPendingMove = null;
        }
      }
      paramMotionEvent.recycle();
    }
    
    void attach(WallpaperService.IWallpaperEngineWrapper paramIWallpaperEngineWrapper)
    {
      if (this.mDestroyed) {
        return;
      }
      this.mIWallpaperEngine = paramIWallpaperEngineWrapper;
      this.mCaller = WallpaperService.IWallpaperEngineWrapper.-get0(paramIWallpaperEngineWrapper);
      this.mConnection = paramIWallpaperEngineWrapper.mConnection;
      this.mWindowToken = paramIWallpaperEngineWrapper.mWindowToken;
      this.mSurfaceHolder.setSizeFromLayout();
      this.mInitializing = true;
      this.mSession = WindowManagerGlobal.getWindowSession();
      this.mWindow.setSession(this.mSession);
      this.mLayout.packageName = WallpaperService.this.getPackageName();
      this.mDisplayManager = ((DisplayManager)WallpaperService.this.getSystemService("display"));
      this.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mCaller.getHandler());
      this.mDisplay = this.mDisplayManager.getDisplay(0);
      this.mDisplayState = this.mDisplay.getState();
      onCreate(this.mSurfaceHolder);
      this.mInitializing = false;
      this.mReportedVisible = false;
      updateSurface(false, false, false);
    }
    
    void detach()
    {
      if (this.mDestroyed) {
        return;
      }
      this.mDestroyed = true;
      if (this.mDisplayManager != null) {
        this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
      }
      if (this.mVisible)
      {
        this.mVisible = false;
        onVisibilityChanged(false);
      }
      reportSurfaceDestroyed();
      onDestroy();
      if (this.mCreated) {}
      try
      {
        if (this.mInputEventReceiver != null)
        {
          this.mInputEventReceiver.dispose();
          this.mInputEventReceiver = null;
        }
        this.mSession.remove(this.mWindow);
      }
      catch (RemoteException localRemoteException)
      {
        for (;;) {}
      }
      this.mSurfaceHolder.mSurface.release();
      this.mCreated = false;
      if (this.mInputChannel != null)
      {
        this.mInputChannel.dispose();
        this.mInputChannel = null;
      }
    }
    
    void doCommand(WallpaperService.WallpaperCommand paramWallpaperCommand)
    {
      if (!this.mDestroyed) {}
      for (Bundle localBundle = onCommand(paramWallpaperCommand.action, paramWallpaperCommand.x, paramWallpaperCommand.y, paramWallpaperCommand.z, paramWallpaperCommand.extras, paramWallpaperCommand.sync);; localBundle = null)
      {
        if (paramWallpaperCommand.sync) {}
        try
        {
          this.mSession.wallpaperCommandComplete(this.mWindow.asBinder(), localBundle);
          return;
        }
        catch (RemoteException paramWallpaperCommand) {}
      }
    }
    
    void doDesiredSizeChanged(int paramInt1, int paramInt2)
    {
      if (!this.mDestroyed)
      {
        this.mIWallpaperEngine.mReqWidth = paramInt1;
        this.mIWallpaperEngine.mReqHeight = paramInt2;
        onDesiredSizeChanged(paramInt1, paramInt2);
        doOffsetsChanged(true);
      }
    }
    
    void doDisplayPaddingChanged(Rect paramRect)
    {
      if ((!this.mDestroyed) && (!this.mIWallpaperEngine.mDisplayPadding.equals(paramRect)))
      {
        this.mIWallpaperEngine.mDisplayPadding.set(paramRect);
        updateSurface(true, false, false);
      }
    }
    
    void doOffsetsChanged(boolean paramBoolean)
    {
      int j = 0;
      if (this.mDestroyed) {
        return;
      }
      if ((paramBoolean) || (this.mOffsetsChanged)) {}
      for (;;)
      {
        synchronized (this.mLock)
        {
          float f1 = this.mPendingXOffset;
          float f2 = this.mPendingYOffset;
          float f3 = this.mPendingXOffsetStep;
          float f4 = this.mPendingYOffsetStep;
          paramBoolean = this.mPendingSync;
          this.mPendingSync = false;
          this.mOffsetMessageEnqueued = false;
          if (this.mSurfaceCreated)
          {
            if (!this.mReportedVisible) {
              break label199;
            }
            i = this.mIWallpaperEngine.mReqWidth - this.mCurWidth;
            if (i > 0)
            {
              i = -(int)(i * f1 + 0.5F);
              int k = this.mIWallpaperEngine.mReqHeight - this.mCurHeight;
              if (k > 0) {
                j = -(int)(k * f2 + 0.5F);
              }
              onOffsetsChanged(f1, f2, f3, f4, i, j);
            }
          }
          else if (!paramBoolean) {}
          try
          {
            this.mSession.wallpaperOffsetsComplete(this.mWindow.asBinder());
            return;
          }
          catch (RemoteException localRemoteException) {}
          return;
        }
        int i = 0;
        continue;
        label199:
        this.mOffsetsChanged = true;
      }
    }
    
    void doVisibilityChanged(boolean paramBoolean)
    {
      if (!this.mDestroyed)
      {
        this.mVisible = paramBoolean;
        reportVisibility();
      }
    }
    
    protected void dump(String paramString, FileDescriptor arg2, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mInitializing=");
      paramPrintWriter.print(this.mInitializing);
      paramPrintWriter.print(" mDestroyed=");
      paramPrintWriter.println(this.mDestroyed);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mVisible=");
      paramPrintWriter.print(this.mVisible);
      paramPrintWriter.print(" mReportedVisible=");
      paramPrintWriter.println(this.mReportedVisible);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mDisplay=");
      paramPrintWriter.println(this.mDisplay);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mCreated=");
      paramPrintWriter.print(this.mCreated);
      paramPrintWriter.print(" mSurfaceCreated=");
      paramPrintWriter.print(this.mSurfaceCreated);
      paramPrintWriter.print(" mIsCreating=");
      paramPrintWriter.print(this.mIsCreating);
      paramPrintWriter.print(" mDrawingAllowed=");
      paramPrintWriter.println(this.mDrawingAllowed);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mWidth=");
      paramPrintWriter.print(this.mWidth);
      paramPrintWriter.print(" mCurWidth=");
      paramPrintWriter.print(this.mCurWidth);
      paramPrintWriter.print(" mHeight=");
      paramPrintWriter.print(this.mHeight);
      paramPrintWriter.print(" mCurHeight=");
      paramPrintWriter.println(this.mCurHeight);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mType=");
      paramPrintWriter.print(this.mType);
      paramPrintWriter.print(" mWindowFlags=");
      paramPrintWriter.print(this.mWindowFlags);
      paramPrintWriter.print(" mCurWindowFlags=");
      paramPrintWriter.println(this.mCurWindowFlags);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mWindowPrivateFlags=");
      paramPrintWriter.print(this.mWindowPrivateFlags);
      paramPrintWriter.print(" mCurWindowPrivateFlags=");
      paramPrintWriter.println(this.mCurWindowPrivateFlags);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mVisibleInsets=");
      paramPrintWriter.print(this.mVisibleInsets.toShortString());
      paramPrintWriter.print(" mWinFrame=");
      paramPrintWriter.print(this.mWinFrame.toShortString());
      paramPrintWriter.print(" mContentInsets=");
      paramPrintWriter.println(this.mContentInsets.toShortString());
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mConfiguration=");
      paramPrintWriter.println(this.mConfiguration);
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("mLayout=");
      paramPrintWriter.println(this.mLayout);
      synchronized (this.mLock)
      {
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mPendingXOffset=");
        paramPrintWriter.print(this.mPendingXOffset);
        paramPrintWriter.print(" mPendingXOffset=");
        paramPrintWriter.println(this.mPendingXOffset);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mPendingXOffsetStep=");
        paramPrintWriter.print(this.mPendingXOffsetStep);
        paramPrintWriter.print(" mPendingXOffsetStep=");
        paramPrintWriter.println(this.mPendingXOffsetStep);
        paramPrintWriter.print(paramString);
        paramPrintWriter.print("mOffsetMessageEnqueued=");
        paramPrintWriter.print(this.mOffsetMessageEnqueued);
        paramPrintWriter.print(" mPendingSync=");
        paramPrintWriter.println(this.mPendingSync);
        if (this.mPendingMove != null)
        {
          paramPrintWriter.print(paramString);
          paramPrintWriter.print("mPendingMove=");
          paramPrintWriter.println(this.mPendingMove);
        }
        return;
      }
    }
    
    public int getDesiredMinimumHeight()
    {
      return this.mIWallpaperEngine.mReqHeight;
    }
    
    public int getDesiredMinimumWidth()
    {
      return this.mIWallpaperEngine.mReqWidth;
    }
    
    public SurfaceHolder getSurfaceHolder()
    {
      return this.mSurfaceHolder;
    }
    
    public boolean isPreview()
    {
      return this.mIWallpaperEngine.mIsPreview;
    }
    
    public boolean isVisible()
    {
      return this.mReportedVisible;
    }
    
    public void onApplyWindowInsets(WindowInsets paramWindowInsets) {}
    
    public Bundle onCommand(String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle, boolean paramBoolean)
    {
      return null;
    }
    
    public void onCreate(SurfaceHolder paramSurfaceHolder) {}
    
    public void onDesiredSizeChanged(int paramInt1, int paramInt2) {}
    
    public void onDestroy() {}
    
    public void onOffsetsChanged(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, int paramInt1, int paramInt2) {}
    
    public void onSurfaceChanged(SurfaceHolder paramSurfaceHolder, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onSurfaceCreated(SurfaceHolder paramSurfaceHolder) {}
    
    public void onSurfaceDestroyed(SurfaceHolder paramSurfaceHolder) {}
    
    public void onSurfaceRedrawNeeded(SurfaceHolder paramSurfaceHolder) {}
    
    public void onTouchEvent(MotionEvent paramMotionEvent) {}
    
    public void onVisibilityChanged(boolean paramBoolean) {}
    
    void reportSurfaceDestroyed()
    {
      int i = 0;
      if (this.mSurfaceCreated)
      {
        this.mSurfaceCreated = false;
        this.mSurfaceHolder.ungetCallbacks();
        SurfaceHolder.Callback[] arrayOfCallback = this.mSurfaceHolder.getCallbacks();
        if (arrayOfCallback != null)
        {
          int j = arrayOfCallback.length;
          while (i < j)
          {
            arrayOfCallback[i].surfaceDestroyed(this.mSurfaceHolder);
            i += 1;
          }
        }
        onSurfaceDestroyed(this.mSurfaceHolder);
      }
    }
    
    void reportVisibility()
    {
      int i;
      if (!this.mDestroyed)
      {
        if (this.mDisplay != null) {
          break label73;
        }
        i = 0;
        this.mDisplayState = i;
        if ((!this.mVisible) || (this.mDisplayState == 1)) {
          break label84;
        }
      }
      label73:
      label84:
      for (boolean bool = true;; bool = false)
      {
        if (this.mReportedVisible != bool)
        {
          this.mReportedVisible = bool;
          if (bool)
          {
            doOffsetsChanged(false);
            updateSurface(false, false, false);
          }
          onVisibilityChanged(bool);
        }
        return;
        i = this.mDisplay.getState();
        break;
      }
    }
    
    public void setFixedSizeAllowed(boolean paramBoolean)
    {
      this.mFixedSizeAllowed = paramBoolean;
    }
    
    public void setOffsetNotificationsEnabled(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = this.mWindowPrivateFlags | 0x4;; i = this.mWindowPrivateFlags & 0xFFFFFFFB)
      {
        this.mWindowPrivateFlags = i;
        if (this.mCreated) {
          updateSurface(false, false, false);
        }
        return;
      }
    }
    
    public void setTouchEventsEnabled(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = this.mWindowFlags & 0xFFFFFFEF;; i = this.mWindowFlags | 0x10)
      {
        this.mWindowFlags = i;
        if (this.mCreated) {
          updateSurface(false, false, false);
        }
        return;
      }
    }
    
    void updateSurface(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    {
      if (this.mDestroyed) {
        Log.w("WallpaperService", "Ignoring updateSurface: destroyed");
      }
      boolean bool1 = false;
      boolean bool4 = this.mSurfaceHolder.getRequestedWidth();
      int n;
      int m;
      label58:
      int i;
      label68:
      boolean bool3;
      label78:
      int k;
      label95:
      label116:
      int j;
      label126:
      boolean bool5;
      label143:
      boolean bool2;
      if (!bool4)
      {
        bool4 = true;
        n = this.mSurfaceHolder.getRequestedHeight();
        if (n > 0) {
          break label223;
        }
        n = -1;
        m = bool1;
        if (!this.mCreated) {
          break label229;
        }
        i = 0;
        if (!this.mSurfaceCreated) {
          break label235;
        }
        bool3 = false;
        if (this.mFormat == this.mSurfaceHolder.getRequestedFormat()) {
          break label241;
        }
        k = 1;
        if ((this.mWidth == bool4) && (this.mHeight == n)) {
          break label247;
        }
        bool1 = true;
        if (!this.mCreated) {
          break label253;
        }
        j = 0;
        if (this.mType == this.mSurfaceHolder.getRequestedType()) {
          break label259;
        }
        bool5 = true;
        if (this.mCurWindowFlags != this.mWindowFlags) {
          break label265;
        }
        if (this.mCurWindowPrivateFlags == this.mWindowPrivateFlags) {
          break label271;
        }
        bool2 = true;
      }
      for (;;)
      {
        if ((paramBoolean1) || (i != 0) || (bool3) || (k != 0) || (bool1) || (bool5) || (bool2) || (paramBoolean3) || (!this.mIWallpaperEngine.mShownReported)) {
          break label277;
        }
        return;
        bool1 = true;
        break;
        label223:
        m = 1;
        break label58;
        label229:
        i = 1;
        break label68;
        label235:
        bool3 = true;
        break label78;
        label241:
        k = 0;
        break label95;
        label247:
        bool1 = false;
        break label116;
        label253:
        j = 1;
        break label126;
        label259:
        bool5 = false;
        break label143;
        label265:
        bool2 = true;
        continue;
        label271:
        bool2 = false;
      }
      label277:
      int i2;
      boolean bool6;
      Rect localRect2;
      int i1;
      for (;;)
      {
        try
        {
          this.mWidth = bool4;
          this.mHeight = n;
          this.mFormat = this.mSurfaceHolder.getRequestedFormat();
          this.mType = this.mSurfaceHolder.getRequestedType();
          this.mLayout.x = 0;
          this.mLayout.y = 0;
          this.mLayout.width = bool4;
          this.mLayout.height = n;
          this.mLayout.format = this.mFormat;
          this.mCurWindowFlags = this.mWindowFlags;
          this.mLayout.flags = (this.mWindowFlags | 0x200 | 0x10000 | 0x100 | 0x8);
          this.mCurWindowPrivateFlags = this.mWindowPrivateFlags;
          this.mLayout.privateFlags = this.mWindowPrivateFlags;
          this.mLayout.memoryType = this.mType;
          this.mLayout.token = this.mWindowToken;
          if (!this.mCreated)
          {
            WallpaperService.this.obtainStyledAttributes(R.styleable.Window).recycle();
            this.mLayout.type = this.mIWallpaperEngine.mWindowType;
            this.mLayout.gravity = 8388659;
            this.mLayout.setTitle(WallpaperService.this.getClass().getName());
            this.mLayout.windowAnimations = 16974578;
            this.mInputChannel = new InputChannel();
            if (this.mSession.addToDisplay(this.mWindow, this.mWindow.mSeq, this.mLayout, 0, 0, this.mContentInsets, this.mStableInsets, this.mOutsets, this.mInputChannel) < 0)
            {
              Log.w("WallpaperService", "Failed to add window while updating wallpaper surface.");
              return;
            }
            this.mCreated = true;
            this.mInputEventReceiver = new WallpaperInputEventReceiver(this.mInputChannel, Looper.myLooper());
          }
          this.mSurfaceHolder.mSurfaceLock.lock();
          this.mDrawingAllowed = true;
          if (m == 0)
          {
            this.mLayout.surfaceInsets.set(this.mIWallpaperEngine.mDisplayPadding);
            Rect localRect1 = this.mLayout.surfaceInsets;
            localRect1.left += this.mOutsets.left;
            localRect1 = this.mLayout.surfaceInsets;
            localRect1.top += this.mOutsets.top;
            localRect1 = this.mLayout.surfaceInsets;
            localRect1.right += this.mOutsets.right;
            localRect1 = this.mLayout.surfaceInsets;
            localRect1.bottom += this.mOutsets.bottom;
            i2 = this.mSession.relayout(this.mWindow, this.mWindow.mSeq, this.mLayout, this.mWidth, this.mHeight, 0, 0, this.mWinFrame, this.mOverscanInsets, this.mContentInsets, this.mVisibleInsets, this.mStableInsets, this.mOutsets, this.mBackdropFrame, this.mConfiguration, this.mSurfaceHolder.mSurface);
            bool4 = this.mWinFrame.width();
            bool2 = this.mWinFrame.height();
            bool6 = bool2;
            bool5 = bool4;
            if (m == 0)
            {
              localRect1 = this.mIWallpaperEngine.mDisplayPadding;
              bool5 = bool4 + (localRect1.left + localRect1.right + this.mOutsets.left + this.mOutsets.right);
              bool6 = bool2 + (localRect1.top + localRect1.bottom + this.mOutsets.top + this.mOutsets.bottom);
              localRect2 = this.mOverscanInsets;
              localRect2.left += localRect1.left;
              localRect2 = this.mOverscanInsets;
              localRect2.top += localRect1.top;
              localRect2 = this.mOverscanInsets;
              localRect2.right += localRect1.right;
              localRect2 = this.mOverscanInsets;
              localRect2.bottom += localRect1.bottom;
              localRect2 = this.mContentInsets;
              localRect2.left += localRect1.left;
              localRect2 = this.mContentInsets;
              localRect2.top += localRect1.top;
              localRect2 = this.mContentInsets;
              localRect2.right += localRect1.right;
              localRect2 = this.mContentInsets;
              localRect2.bottom += localRect1.bottom;
              localRect2 = this.mStableInsets;
              localRect2.left += localRect1.left;
              localRect2 = this.mStableInsets;
              localRect2.top += localRect1.top;
              localRect2 = this.mStableInsets;
              localRect2.right += localRect1.right;
              localRect2 = this.mStableInsets;
              localRect2.bottom += localRect1.bottom;
            }
            if (this.mCurWidth != bool5)
            {
              bool1 = true;
              this.mCurWidth = bool5;
            }
            bool4 = bool1;
            if (this.mCurHeight != bool6)
            {
              bool4 = true;
              this.mCurHeight = bool6;
            }
            if (this.mDispatchedOverscanInsets.equals(this.mOverscanInsets))
            {
              bool2 = false;
              if (!this.mDispatchedContentInsets.equals(this.mContentInsets)) {
                break label1354;
              }
              m = 0;
              if (!this.mDispatchedStableInsets.equals(this.mStableInsets)) {
                break label1360;
              }
              n = 0;
              if (!this.mDispatchedOutsets.equals(this.mOutsets)) {
                break label1366;
              }
              i1 = 0;
              this.mSurfaceHolder.setSurfaceFrameSize(bool5, bool6);
              this.mSurfaceHolder.mSurfaceLock.unlock();
              if (this.mSurfaceHolder.mSurface.isValid()) {
                break;
              }
              reportSurfaceDestroyed();
            }
          }
          else
          {
            this.mLayout.surfaceInsets.set(0, 0, 0, 0);
            continue;
          }
          bool2 = true;
        }
        catch (RemoteException localRemoteException)
        {
          return;
        }
        continue;
        label1354:
        m = 1;
        continue;
        label1360:
        n = 1;
        continue;
        label1366:
        i1 = 1;
      }
      bool1 = false;
      paramBoolean1 = paramBoolean3;
      for (;;)
      {
        try
        {
          this.mSurfaceHolder.ungetCallbacks();
          if (bool3)
          {
            paramBoolean1 = paramBoolean3;
            this.mIsCreating = true;
            bool6 = true;
            paramBoolean1 = paramBoolean3;
            onSurfaceCreated(this.mSurfaceHolder);
            paramBoolean1 = paramBoolean3;
            Object localObject1 = this.mSurfaceHolder.getCallbacks();
            bool1 = bool6;
            if (localObject1 != null)
            {
              bool5 = false;
              paramBoolean1 = paramBoolean3;
              boolean bool7 = localObject1.length;
              bool1 = bool6;
              if (bool5 < bool7)
              {
                paramBoolean1 = paramBoolean3;
                localObject1[bool5].surfaceCreated(this.mSurfaceHolder);
                bool5 += true;
                continue;
                bool3 = true;
                paramBoolean1 = paramBoolean3;
                onSurfaceChanged(this.mSurfaceHolder, this.mFormat, this.mCurWidth, this.mCurHeight);
                paramBoolean1 = paramBoolean3;
                localObject1 = this.mSurfaceHolder.getCallbacks();
                bool1 = bool3;
                if (localObject1 != null)
                {
                  i = 0;
                  paramBoolean1 = paramBoolean3;
                  k = localObject1.length;
                  bool1 = bool3;
                  if (i < k)
                  {
                    paramBoolean1 = paramBoolean3;
                    localObject1[i].surfaceChanged(this.mSurfaceHolder, this.mFormat, this.mCurWidth, this.mCurHeight);
                    i += 1;
                    continue;
                  }
                }
                label1578:
                if ((j | bool2 | m | n | i1) != 0)
                {
                  paramBoolean1 = paramBoolean3;
                  this.mDispatchedOverscanInsets.set(this.mOverscanInsets);
                  paramBoolean1 = paramBoolean3;
                  localObject1 = this.mDispatchedOverscanInsets;
                  paramBoolean1 = paramBoolean3;
                  ((Rect)localObject1).left += this.mOutsets.left;
                  paramBoolean1 = paramBoolean3;
                  localObject1 = this.mDispatchedOverscanInsets;
                  paramBoolean1 = paramBoolean3;
                  ((Rect)localObject1).top += this.mOutsets.top;
                  paramBoolean1 = paramBoolean3;
                  localObject1 = this.mDispatchedOverscanInsets;
                  paramBoolean1 = paramBoolean3;
                  ((Rect)localObject1).right += this.mOutsets.right;
                  paramBoolean1 = paramBoolean3;
                  localObject1 = this.mDispatchedOverscanInsets;
                  paramBoolean1 = paramBoolean3;
                  ((Rect)localObject1).bottom += this.mOutsets.bottom;
                  paramBoolean1 = paramBoolean3;
                  this.mDispatchedContentInsets.set(this.mContentInsets);
                  paramBoolean1 = paramBoolean3;
                  this.mDispatchedStableInsets.set(this.mStableInsets);
                  paramBoolean1 = paramBoolean3;
                  this.mDispatchedOutsets.set(this.mOutsets);
                  paramBoolean1 = paramBoolean3;
                  this.mFinalSystemInsets.set(this.mDispatchedOverscanInsets);
                  paramBoolean1 = paramBoolean3;
                  this.mFinalStableInsets.set(this.mDispatchedStableInsets);
                  paramBoolean1 = paramBoolean3;
                  onApplyWindowInsets(new WindowInsets(this.mFinalSystemInsets, null, this.mFinalStableInsets, WallpaperService.this.getResources().getConfiguration().isScreenRound(), false));
                }
                if (paramBoolean3)
                {
                  paramBoolean1 = paramBoolean3;
                  onSurfaceRedrawNeeded(this.mSurfaceHolder);
                  paramBoolean1 = paramBoolean3;
                  localObject1 = this.mSurfaceHolder.getCallbacks();
                  if (localObject1 != null)
                  {
                    bool2 = false;
                    paramBoolean1 = paramBoolean3;
                    i = localObject1.length;
                    if (bool2 < i)
                    {
                      localRect2 = localObject1[bool2];
                      paramBoolean1 = paramBoolean3;
                      if (!(localRect2 instanceof SurfaceHolder.Callback2)) {
                        break label2073;
                      }
                      paramBoolean1 = paramBoolean3;
                      ((SurfaceHolder.Callback2)localRect2).surfaceRedrawNeeded(this.mSurfaceHolder);
                      break label2073;
                    }
                  }
                }
                if (bool1)
                {
                  paramBoolean1 = paramBoolean3;
                  paramBoolean2 = this.mReportedVisible;
                  if (!paramBoolean2) {}
                }
                else
                {
                  return;
                }
                paramBoolean1 = paramBoolean3;
                if (this.mIsCreating)
                {
                  paramBoolean1 = paramBoolean3;
                  onVisibilityChanged(true);
                }
                paramBoolean1 = paramBoolean3;
                onVisibilityChanged(false);
                continue;
              }
            }
          }
          if (i != 0) {
            break label2032;
          }
        }
        finally
        {
          this.mIsCreating = false;
          this.mSurfaceCreated = true;
          if (paramBoolean1) {
            this.mSession.finishDrawing(this.mWindow);
          }
          this.mIWallpaperEngine.reportShown();
        }
        if ((i2 & 0x2) != 0) {}
        label2032:
        for (bool5 = true;; bool5 = false)
        {
          paramBoolean3 |= bool5;
          if ((paramBoolean2) || (i != 0) || (bool3) || (k != 0)) {
            break;
          }
          if (!bool4) {
            break label1578;
          }
          break;
        }
        label2073:
        bool2 += true;
      }
    }
    
    final class WallpaperInputEventReceiver
      extends InputEventReceiver
    {
      public WallpaperInputEventReceiver(InputChannel paramInputChannel, Looper paramLooper)
      {
        super(paramLooper);
      }
      
      public void onInputEvent(InputEvent paramInputEvent)
      {
        boolean bool2 = false;
        boolean bool1 = bool2;
        try
        {
          if ((paramInputEvent instanceof MotionEvent))
          {
            bool1 = bool2;
            if ((paramInputEvent.getSource() & 0x2) != 0)
            {
              MotionEvent localMotionEvent = MotionEvent.obtainNoHistory((MotionEvent)paramInputEvent);
              WallpaperService.Engine.-wrap0(WallpaperService.Engine.this, localMotionEvent);
              bool1 = true;
            }
          }
          finishInputEvent(paramInputEvent, bool1);
          return;
        }
        finally
        {
          finishInputEvent(paramInputEvent, false);
        }
      }
    }
  }
  
  class IWallpaperEngineWrapper
    extends IWallpaperEngine.Stub
    implements HandlerCaller.Callback
  {
    private final HandlerCaller mCaller = new HandlerCaller(paramWallpaperService, paramWallpaperService.getMainLooper(), this, true);
    final IWallpaperConnection mConnection;
    final Rect mDisplayPadding = new Rect();
    WallpaperService.Engine mEngine;
    final boolean mIsPreview;
    int mReqHeight;
    int mReqWidth;
    boolean mShownReported;
    final IBinder mWindowToken;
    final int mWindowType;
    
    IWallpaperEngineWrapper(WallpaperService paramWallpaperService, IWallpaperConnection paramIWallpaperConnection, IBinder paramIBinder, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, Rect paramRect)
    {
      this.mConnection = paramIWallpaperConnection;
      this.mWindowToken = paramIBinder;
      this.mWindowType = paramInt1;
      this.mIsPreview = paramBoolean;
      this.mReqWidth = paramInt2;
      this.mReqHeight = paramInt3;
      this.mDisplayPadding.set(paramRect);
      this$1 = this.mCaller.obtainMessage(10);
      this.mCaller.sendMessage(WallpaperService.this);
    }
    
    public void destroy()
    {
      Message localMessage = this.mCaller.obtainMessage(20);
      this.mCaller.sendMessage(localMessage);
    }
    
    public void dispatchPointer(MotionEvent paramMotionEvent)
    {
      if (this.mEngine != null)
      {
        WallpaperService.Engine.-wrap0(this.mEngine, paramMotionEvent);
        return;
      }
      paramMotionEvent.recycle();
    }
    
    public void dispatchWallpaperCommand(String paramString, int paramInt1, int paramInt2, int paramInt3, Bundle paramBundle)
    {
      if (this.mEngine != null) {
        this.mEngine.mWindow.dispatchWallpaperCommand(paramString, paramInt1, paramInt2, paramInt3, paramBundle, false);
      }
    }
    
    public void executeMessage(Message arg1)
    {
      boolean bool;
      switch (???.what)
      {
      default: 
        Log.w("WallpaperService", "Unknown message type " + ???.what);
      case 10035: 
        return;
      case 10: 
        try
        {
          this.mConnection.attachEngine(this);
          ??? = WallpaperService.this.onCreateEngine();
          this.mEngine = ???;
          WallpaperService.-get0(WallpaperService.this).add(???);
          ???.attach(this);
          return;
        }
        catch (RemoteException ???)
        {
          Log.w("WallpaperService", "Wallpaper host disappeared", ???);
          return;
        }
      case 20: 
        WallpaperService.-get0(WallpaperService.this).remove(this.mEngine);
        this.mEngine.detach();
        return;
      case 30: 
        this.mEngine.doDesiredSizeChanged(???.arg1, ???.arg2);
        return;
      case 40: 
        this.mEngine.doDisplayPaddingChanged((Rect)???.obj);
      case 10000: 
        this.mEngine.updateSurface(true, false, false);
        return;
      case 10010: 
        localObject1 = this.mEngine;
        if (???.arg1 != 0) {}
        for (bool = true;; bool = false)
        {
          ((WallpaperService.Engine)localObject1).doVisibilityChanged(bool);
          return;
        }
      case 10020: 
        this.mEngine.doOffsetsChanged(true);
        return;
      case 10025: 
        ??? = (WallpaperService.WallpaperCommand)???.obj;
        this.mEngine.doCommand(???);
        return;
      case 10030: 
        if (???.arg1 != 0) {}
        for (bool = true;; bool = false)
        {
          this.mEngine.mOutsets.set((Rect)???.obj);
          this.mEngine.updateSurface(true, false, bool);
          this.mEngine.doOffsetsChanged(true);
          return;
        }
      }
      int i = 0;
      int j = 0;
      Object localObject1 = (MotionEvent)???.obj;
      if (((MotionEvent)localObject1).getAction() == 2) {}
      synchronized (this.mEngine.mLock)
      {
        if (this.mEngine.mPendingMove == localObject1)
        {
          this.mEngine.mPendingMove = null;
          i = j;
          if (i == 0) {
            this.mEngine.onTouchEvent((MotionEvent)localObject1);
          }
          ((MotionEvent)localObject1).recycle();
          return;
        }
        i = 1;
      }
    }
    
    public void reportShown()
    {
      if (!this.mShownReported) {
        this.mShownReported = true;
      }
      try
      {
        this.mConnection.engineShown(this);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        Log.w("WallpaperService", "Wallpaper host disappeared", localRemoteException);
      }
    }
    
    public void setDesiredSize(int paramInt1, int paramInt2)
    {
      Message localMessage = this.mCaller.obtainMessageII(30, paramInt1, paramInt2);
      this.mCaller.sendMessage(localMessage);
    }
    
    public void setDisplayPadding(Rect paramRect)
    {
      paramRect = this.mCaller.obtainMessageO(40, paramRect);
      this.mCaller.sendMessage(paramRect);
    }
    
    public void setVisibility(boolean paramBoolean)
    {
      Object localObject = this.mCaller;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localObject = ((HandlerCaller)localObject).obtainMessageI(10010, i);
        this.mCaller.sendMessage((Message)localObject);
        return;
      }
    }
  }
  
  class IWallpaperServiceWrapper
    extends IWallpaperService.Stub
  {
    private final WallpaperService mTarget;
    
    public IWallpaperServiceWrapper(WallpaperService paramWallpaperService)
    {
      this.mTarget = paramWallpaperService;
    }
    
    public void attach(IWallpaperConnection paramIWallpaperConnection, IBinder paramIBinder, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3, Rect paramRect)
    {
      new WallpaperService.IWallpaperEngineWrapper(WallpaperService.this, this.mTarget, paramIWallpaperConnection, paramIBinder, paramInt1, paramBoolean, paramInt2, paramInt3, paramRect);
    }
  }
  
  static final class WallpaperCommand
  {
    String action;
    Bundle extras;
    boolean sync;
    int x;
    int y;
    int z;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/wallpaper/WallpaperService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */