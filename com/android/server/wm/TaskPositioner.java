package com.android.server.wm;

import android.annotation.IntDef;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Looper;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Slog;
import android.view.BatchedInputEventReceiver;
import android.view.Choreographer;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.InputChannel;
import android.view.SurfaceControl;
import com.android.server.input.InputApplicationHandle;
import com.android.server.input.InputManagerService;
import com.android.server.input.InputWindowHandle;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class TaskPositioner
  implements DimLayer.DimLayerUser
{
  private static final int CTRL_BOTTOM = 8;
  private static final int CTRL_LEFT = 1;
  private static final int CTRL_NONE = 0;
  private static final int CTRL_RIGHT = 2;
  private static final int CTRL_TOP = 4;
  public static final float RESIZING_HINT_ALPHA = 0.5F;
  public static final int RESIZING_HINT_DURATION_MS = 0;
  static final int SIDE_MARGIN_DIP = 100;
  private static final String TAG = "WindowManager";
  private static final String TAG_LOCAL = "TaskPositioner";
  InputChannel mClientChannel;
  private int mCtrlType = 0;
  private int mCurrentDimSide;
  private DimLayer mDimLayer;
  private Display mDisplay;
  private final DisplayMetrics mDisplayMetrics = new DisplayMetrics();
  InputApplicationHandle mDragApplicationHandle;
  private boolean mDragEnded = false;
  InputWindowHandle mDragWindowHandle;
  private WindowPositionerEventReceiver mInputEventReceiver;
  private int mMinVisibleHeight;
  private int mMinVisibleWidth;
  private boolean mResizing;
  InputChannel mServerChannel;
  private final WindowManagerService mService;
  private int mSideMargin;
  private float mStartDragX;
  private float mStartDragY;
  private Task mTask;
  private Rect mTmpRect = new Rect();
  private final Rect mWindowDragBounds = new Rect();
  private final Rect mWindowOriginalBounds = new Rect();
  
  TaskPositioner(WindowManagerService paramWindowManagerService)
  {
    this.mService = paramWindowManagerService;
  }
  
  private void endDragLocked()
  {
    this.mResizing = false;
    this.mTask.setDragResizing(false, 0);
  }
  
  private int getDimSide(int paramInt)
  {
    if ((this.mTask.mStack.mStackId != 2) || (!this.mTask.mStack.isFullscreen()) || (this.mService.mCurConfiguration.orientation != 2)) {
      return 0;
    }
    this.mTask.mStack.getDimBounds(this.mTmpRect);
    if (paramInt - this.mSideMargin <= this.mTmpRect.left) {
      return 1;
    }
    if (this.mSideMargin + paramInt >= this.mTmpRect.right) {
      return 2;
    }
    return 0;
  }
  
  private boolean notifyMoveLocked(float paramFloat1, float paramFloat2)
  {
    if (WindowManagerDebugConfig.DEBUG_TASK_POSITIONING) {
      Slog.d(TAG, "notifyMoveLocked: {" + paramFloat1 + "," + paramFloat2 + "}");
    }
    if (this.mCtrlType != 0)
    {
      int i2 = Math.round(paramFloat1 - this.mStartDragX);
      int i1 = Math.round(paramFloat2 - this.mStartDragY);
      j = this.mWindowOriginalBounds.left;
      int k = this.mWindowOriginalBounds.top;
      int m = this.mWindowOriginalBounds.right;
      int n = this.mWindowOriginalBounds.bottom;
      i = j;
      if ((this.mCtrlType & 0x1) != 0) {
        i = Math.min(j + i2, m - this.mMinVisibleWidth);
      }
      j = k;
      if ((this.mCtrlType & 0x4) != 0) {
        j = Math.min(k + i1, n - this.mMinVisibleHeight);
      }
      k = m;
      if ((this.mCtrlType & 0x2) != 0) {
        k = Math.max(this.mMinVisibleWidth + i, m + i2);
      }
      m = n;
      if ((this.mCtrlType & 0x8) != 0) {
        m = Math.max(this.mMinVisibleHeight + j, n + i1);
      }
      this.mWindowDragBounds.set(i, j, k, m);
      this.mTask.setDragResizing(true, 0);
      return false;
    }
    this.mTask.mStack.getDimBounds(this.mTmpRect);
    if (!this.mTask.isDockedInEffect()) {
      this.mTmpRect.inset(this.mMinVisibleWidth, this.mMinVisibleHeight);
    }
    boolean bool = false;
    int i = (int)paramFloat1;
    int j = (int)paramFloat2;
    if (!this.mTmpRect.contains(i, j))
    {
      Math.min(Math.max(paramFloat1, this.mTmpRect.left), this.mTmpRect.right);
      Math.min(Math.max(paramFloat2, this.mTmpRect.top), this.mTmpRect.bottom);
      bool = true;
    }
    updateWindowDragBounds(i, j);
    updateDimLayerVisibility(i);
    return bool;
  }
  
  private void showDimLayer()
  {
    this.mTask.mStack.getDimBounds(this.mTmpRect);
    if (this.mCurrentDimSide == 1) {
      this.mTmpRect.right = this.mTmpRect.centerX();
    }
    for (;;)
    {
      this.mDimLayer.setBounds(this.mTmpRect);
      this.mDimLayer.show(this.mService.getDragLayerLocked(), 0.5F, 0L);
      return;
      if (this.mCurrentDimSide == 2) {
        this.mTmpRect.left = this.mTmpRect.centerX();
      }
    }
  }
  
  private void updateDimLayerVisibility(int paramInt)
  {
    paramInt = getDimSide(paramInt);
    if (paramInt == this.mCurrentDimSide) {
      return;
    }
    this.mCurrentDimSide = paramInt;
    if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
      Slog.i(TAG, ">>> OPEN TRANSACTION updateDimLayerVisibility");
    }
    SurfaceControl.openTransaction();
    if (this.mCurrentDimSide == 0) {
      this.mDimLayer.hide();
    }
    for (;;)
    {
      SurfaceControl.closeTransaction();
      return;
      showDimLayer();
    }
  }
  
  private void updateWindowDragBounds(int paramInt1, int paramInt2)
  {
    this.mWindowDragBounds.set(this.mWindowOriginalBounds);
    if (this.mTask.isDockedInEffect()) {
      if (this.mService.mCurConfiguration.orientation == 2) {
        this.mWindowDragBounds.offset(Math.round(paramInt1 - this.mStartDragX), 0);
      }
    }
    for (;;)
    {
      if (WindowManagerDebugConfig.DEBUG_TASK_POSITIONING) {
        Slog.d(TAG, "updateWindowDragBounds: " + this.mWindowDragBounds);
      }
      return;
      this.mWindowDragBounds.offset(0, Math.round(paramInt2 - this.mStartDragY));
      continue;
      this.mWindowDragBounds.offset(Math.round(paramInt1 - this.mStartDragX), Math.round(paramInt2 - this.mStartDragY));
    }
  }
  
  public boolean dimFullscreen()
  {
    return isFullscreen();
  }
  
  public void getDimBounds(Rect paramRect) {}
  
  public DisplayInfo getDisplayInfo()
  {
    return this.mTask.mStack.getDisplayInfo();
  }
  
  boolean isFullscreen()
  {
    return false;
  }
  
  void register(Display paramDisplay)
  {
    if (WindowManagerDebugConfig.DEBUG_TASK_POSITIONING) {
      Slog.d(TAG, "Registering task positioner");
    }
    if (this.mClientChannel != null)
    {
      Slog.e(TAG, "Task positioner already registered");
      return;
    }
    this.mDisplay = paramDisplay;
    this.mDisplay.getMetrics(this.mDisplayMetrics);
    paramDisplay = InputChannel.openInputChannelPair(TAG);
    this.mServerChannel = paramDisplay[0];
    this.mClientChannel = paramDisplay[1];
    this.mService.mInputManager.registerInputChannel(this.mServerChannel, null);
    this.mInputEventReceiver = new WindowPositionerEventReceiver(this.mClientChannel, this.mService.mH.getLooper(), this.mService.mChoreographer);
    this.mDragApplicationHandle = new InputApplicationHandle(null);
    this.mDragApplicationHandle.name = TAG;
    this.mDragApplicationHandle.dispatchingTimeoutNanos = 5000000000L;
    this.mDragWindowHandle = new InputWindowHandle(this.mDragApplicationHandle, null, this.mDisplay.getDisplayId());
    this.mDragWindowHandle.name = TAG;
    this.mDragWindowHandle.inputChannel = this.mServerChannel;
    this.mDragWindowHandle.layer = this.mService.getDragLayerLocked();
    this.mDragWindowHandle.layoutParamsFlags = 0;
    this.mDragWindowHandle.layoutParamsType = 2016;
    this.mDragWindowHandle.dispatchingTimeoutNanos = 5000000000L;
    this.mDragWindowHandle.visible = true;
    this.mDragWindowHandle.canReceiveKeys = false;
    this.mDragWindowHandle.hasFocus = true;
    this.mDragWindowHandle.hasWallpaper = false;
    this.mDragWindowHandle.paused = false;
    this.mDragWindowHandle.ownerPid = Process.myPid();
    this.mDragWindowHandle.ownerUid = Process.myUid();
    this.mDragWindowHandle.inputFeatures = 0;
    this.mDragWindowHandle.scaleFactor = 1.0F;
    this.mDragWindowHandle.touchableRegion.setEmpty();
    this.mDragWindowHandle.frameLeft = 0;
    this.mDragWindowHandle.frameTop = 0;
    paramDisplay = new Point();
    this.mDisplay.getRealSize(paramDisplay);
    this.mDragWindowHandle.frameRight = paramDisplay.x;
    this.mDragWindowHandle.frameBottom = paramDisplay.y;
    if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
      Slog.d(TAG, "Pausing rotation during re-position");
    }
    this.mService.pauseRotationLocked();
    this.mDimLayer = new DimLayer(this.mService, this, this.mDisplay.getDisplayId(), "TaskPositioner");
    this.mSideMargin = WindowManagerService.dipToPixel(100, this.mDisplayMetrics);
    this.mMinVisibleWidth = WindowManagerService.dipToPixel(48, this.mDisplayMetrics);
    this.mMinVisibleHeight = WindowManagerService.dipToPixel(32, this.mDisplayMetrics);
    this.mDragEnded = false;
  }
  
  void startDragLocked(WindowState paramWindowState, boolean paramBoolean, float paramFloat1, float paramFloat2)
  {
    if (WindowManagerDebugConfig.DEBUG_TASK_POSITIONING) {
      Slog.d(TAG, "startDragLocked: win=" + paramWindowState + ", resize=" + paramBoolean + ", {" + paramFloat1 + ", " + paramFloat2 + "}");
    }
    this.mCtrlType = 0;
    this.mTask = paramWindowState.getTask();
    this.mStartDragX = paramFloat1;
    this.mStartDragY = paramFloat2;
    if (this.mTask.isDockedInEffect()) {
      this.mTask.getBounds(this.mTmpRect);
    }
    for (;;)
    {
      if (paramBoolean)
      {
        if (paramFloat1 < this.mTmpRect.left) {
          this.mCtrlType |= 0x1;
        }
        if (paramFloat1 > this.mTmpRect.right) {
          this.mCtrlType |= 0x2;
        }
        if (paramFloat2 < this.mTmpRect.top) {
          this.mCtrlType |= 0x4;
        }
        if (paramFloat2 > this.mTmpRect.bottom) {
          this.mCtrlType |= 0x8;
        }
        this.mResizing = true;
      }
      this.mWindowOriginalBounds.set(this.mTmpRect);
      return;
      this.mTask.getDimBounds(this.mTmpRect);
    }
  }
  
  public String toShortString()
  {
    return TAG;
  }
  
  void unregister()
  {
    if (WindowManagerDebugConfig.DEBUG_TASK_POSITIONING) {
      Slog.d(TAG, "Unregistering task positioner");
    }
    if (this.mClientChannel == null)
    {
      Slog.e(TAG, "Task positioner not registered");
      return;
    }
    this.mService.mInputManager.unregisterInputChannel(this.mServerChannel);
    this.mInputEventReceiver.dispose();
    this.mInputEventReceiver = null;
    this.mClientChannel.dispose();
    this.mServerChannel.dispose();
    this.mClientChannel = null;
    this.mServerChannel = null;
    this.mDragWindowHandle = null;
    this.mDragApplicationHandle = null;
    this.mDisplay = null;
    if (this.mDimLayer != null)
    {
      this.mDimLayer.destroySurface();
      this.mDimLayer = null;
    }
    this.mCurrentDimSide = 0;
    this.mDragEnded = true;
    if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
      Slog.d(TAG, "Resuming rotation after re-position");
    }
    this.mService.resumeRotationLocked();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @IntDef(flag=true, value={0L, 1L, 2L, 4L, 8L})
  static @interface CtrlType {}
  
  private final class WindowPositionerEventReceiver
    extends BatchedInputEventReceiver
  {
    public WindowPositionerEventReceiver(InputChannel paramInputChannel, Looper paramLooper, Choreographer paramChoreographer)
    {
      super(paramLooper, paramChoreographer);
    }
    
    /* Error */
    public void onInputEvent(android.view.InputEvent paramInputEvent)
    {
      // Byte code:
      //   0: aload_1
      //   1: instanceof 25
      //   4: ifeq +12 -> 16
      //   7: aload_1
      //   8: invokevirtual 31	android/view/InputEvent:getSource	()I
      //   11: iconst_2
      //   12: iand
      //   13: ifne +4 -> 17
      //   16: return
      //   17: aload_1
      //   18: checkcast 25	android/view/MotionEvent
      //   21: astore 6
      //   23: aload_0
      //   24: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   27: invokestatic 35	com/android/server/wm/TaskPositioner:-get2	(Lcom/android/server/wm/TaskPositioner;)Z
      //   30: istore 5
      //   32: iload 5
      //   34: ifeq +10 -> 44
      //   37: aload_0
      //   38: aload_1
      //   39: iconst_1
      //   40: invokevirtual 39	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:finishInputEvent	(Landroid/view/InputEvent;Z)V
      //   43: return
      //   44: aload 6
      //   46: invokevirtual 43	android/view/MotionEvent:getRawX	()F
      //   49: fstore_2
      //   50: aload 6
      //   52: invokevirtual 46	android/view/MotionEvent:getRawY	()F
      //   55: fstore_3
      //   56: aload 6
      //   58: invokevirtual 49	android/view/MotionEvent:getAction	()I
      //   61: tableswitch	default:+588->649, 0:+192->253, 1:+448->509, 2:+261->322, 3:+506->567
      //   92: aload_0
      //   93: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   96: invokestatic 35	com/android/server/wm/TaskPositioner:-get2	(Lcom/android/server/wm/TaskPositioner;)Z
      //   99: ifeq +147 -> 246
      //   102: aload_0
      //   103: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   106: invokestatic 52	com/android/server/wm/TaskPositioner:-get3	(Lcom/android/server/wm/TaskPositioner;)Z
      //   109: istore 5
      //   111: aload_0
      //   112: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   115: invokestatic 56	com/android/server/wm/TaskPositioner:-get4	(Lcom/android/server/wm/TaskPositioner;)Lcom/android/server/wm/WindowManagerService;
      //   118: getfield 62	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   121: astore 6
      //   123: aload 6
      //   125: monitorenter
      //   126: aload_0
      //   127: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   130: invokestatic 66	com/android/server/wm/TaskPositioner:-wrap1	(Lcom/android/server/wm/TaskPositioner;)V
      //   133: aload 6
      //   135: monitorexit
      //   136: iload 5
      //   138: ifeq +36 -> 174
      //   141: aload_0
      //   142: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   145: invokestatic 56	com/android/server/wm/TaskPositioner:-get4	(Lcom/android/server/wm/TaskPositioner;)Lcom/android/server/wm/WindowManagerService;
      //   148: getfield 70	com/android/server/wm/WindowManagerService:mActivityManager	Landroid/app/IActivityManager;
      //   151: aload_0
      //   152: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   155: invokestatic 74	com/android/server/wm/TaskPositioner:-get5	(Lcom/android/server/wm/TaskPositioner;)Lcom/android/server/wm/Task;
      //   158: getfield 80	com/android/server/wm/Task:mTaskId	I
      //   161: aload_0
      //   162: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   165: invokestatic 84	com/android/server/wm/TaskPositioner:-get7	(Lcom/android/server/wm/TaskPositioner;)Landroid/graphics/Rect;
      //   168: iconst_3
      //   169: invokeinterface 90 4 0
      //   174: aload_0
      //   175: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   178: invokestatic 94	com/android/server/wm/TaskPositioner:-get1	(Lcom/android/server/wm/TaskPositioner;)I
      //   181: ifeq +49 -> 230
      //   184: aload_0
      //   185: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   188: invokestatic 94	com/android/server/wm/TaskPositioner:-get1	(Lcom/android/server/wm/TaskPositioner;)I
      //   191: iconst_1
      //   192: if_icmpne +441 -> 633
      //   195: iconst_0
      //   196: istore 4
      //   198: aload_0
      //   199: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   202: invokestatic 56	com/android/server/wm/TaskPositioner:-get4	(Lcom/android/server/wm/TaskPositioner;)Lcom/android/server/wm/WindowManagerService;
      //   205: getfield 70	com/android/server/wm/WindowManagerService:mActivityManager	Landroid/app/IActivityManager;
      //   208: aload_0
      //   209: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   212: invokestatic 74	com/android/server/wm/TaskPositioner:-get5	(Lcom/android/server/wm/TaskPositioner;)Lcom/android/server/wm/Task;
      //   215: getfield 80	com/android/server/wm/Task:mTaskId	I
      //   218: iload 4
      //   220: iconst_1
      //   221: iconst_1
      //   222: aconst_null
      //   223: iconst_0
      //   224: invokeinterface 98 7 0
      //   229: pop
      //   230: aload_0
      //   231: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   234: invokestatic 56	com/android/server/wm/TaskPositioner:-get4	(Lcom/android/server/wm/TaskPositioner;)Lcom/android/server/wm/WindowManagerService;
      //   237: getfield 102	com/android/server/wm/WindowManagerService:mH	Lcom/android/server/wm/WindowManagerService$H;
      //   240: bipush 40
      //   242: invokevirtual 108	com/android/server/wm/WindowManagerService$H:sendEmptyMessage	(I)Z
      //   245: pop
      //   246: aload_0
      //   247: aload_1
      //   248: iconst_1
      //   249: invokevirtual 39	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:finishInputEvent	(Landroid/view/InputEvent;Z)V
      //   252: return
      //   253: getstatic 114	com/android/server/wm/WindowManagerDebugConfig:DEBUG_TASK_POSITIONING	Z
      //   256: ifeq -164 -> 92
      //   259: invokestatic 118	com/android/server/wm/TaskPositioner:-get0	()Ljava/lang/String;
      //   262: new 120	java/lang/StringBuilder
      //   265: dup
      //   266: invokespecial 123	java/lang/StringBuilder:<init>	()V
      //   269: ldc 125
      //   271: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   274: fload_2
      //   275: invokevirtual 132	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   278: ldc -122
      //   280: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   283: fload_3
      //   284: invokevirtual 132	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   287: ldc -120
      //   289: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   292: invokevirtual 139	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   295: invokestatic 145	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   298: pop
      //   299: goto -207 -> 92
      //   302: astore 6
      //   304: invokestatic 118	com/android/server/wm/TaskPositioner:-get0	()Ljava/lang/String;
      //   307: ldc -109
      //   309: aload 6
      //   311: invokestatic 151	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   314: pop
      //   315: aload_0
      //   316: aload_1
      //   317: iconst_0
      //   318: invokevirtual 39	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:finishInputEvent	(Landroid/view/InputEvent;Z)V
      //   321: return
      //   322: getstatic 114	com/android/server/wm/WindowManagerDebugConfig:DEBUG_TASK_POSITIONING	Z
      //   325: ifeq +43 -> 368
      //   328: invokestatic 118	com/android/server/wm/TaskPositioner:-get0	()Ljava/lang/String;
      //   331: new 120	java/lang/StringBuilder
      //   334: dup
      //   335: invokespecial 123	java/lang/StringBuilder:<init>	()V
      //   338: ldc -103
      //   340: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   343: fload_2
      //   344: invokevirtual 132	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   347: ldc -122
      //   349: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   352: fload_3
      //   353: invokevirtual 132	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   356: ldc -120
      //   358: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   361: invokevirtual 139	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   364: invokestatic 145	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   367: pop
      //   368: aload_0
      //   369: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   372: invokestatic 56	com/android/server/wm/TaskPositioner:-get4	(Lcom/android/server/wm/TaskPositioner;)Lcom/android/server/wm/WindowManagerService;
      //   375: getfield 62	com/android/server/wm/WindowManagerService:mWindowMap	Ljava/util/HashMap;
      //   378: astore 6
      //   380: aload 6
      //   382: monitorenter
      //   383: aload_0
      //   384: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   387: aload_0
      //   388: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   391: fload_2
      //   392: fload_3
      //   393: invokestatic 157	com/android/server/wm/TaskPositioner:-wrap0	(Lcom/android/server/wm/TaskPositioner;FF)Z
      //   396: invokestatic 161	com/android/server/wm/TaskPositioner:-set0	(Lcom/android/server/wm/TaskPositioner;Z)Z
      //   399: pop
      //   400: aload_0
      //   401: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   404: invokestatic 74	com/android/server/wm/TaskPositioner:-get5	(Lcom/android/server/wm/TaskPositioner;)Lcom/android/server/wm/Task;
      //   407: aload_0
      //   408: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   411: invokestatic 164	com/android/server/wm/TaskPositioner:-get6	(Lcom/android/server/wm/TaskPositioner;)Landroid/graphics/Rect;
      //   414: invokevirtual 168	com/android/server/wm/Task:getDimBounds	(Landroid/graphics/Rect;)V
      //   417: aload 6
      //   419: monitorexit
      //   420: aload_0
      //   421: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   424: invokestatic 164	com/android/server/wm/TaskPositioner:-get6	(Lcom/android/server/wm/TaskPositioner;)Landroid/graphics/Rect;
      //   427: aload_0
      //   428: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   431: invokestatic 84	com/android/server/wm/TaskPositioner:-get7	(Lcom/android/server/wm/TaskPositioner;)Landroid/graphics/Rect;
      //   434: invokevirtual 174	android/graphics/Rect:equals	(Ljava/lang/Object;)Z
      //   437: ifne -345 -> 92
      //   440: ldc2_w 175
      //   443: ldc -78
      //   445: invokestatic 184	android/os/Trace:traceBegin	(JLjava/lang/String;)V
      //   448: aload_0
      //   449: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   452: invokestatic 56	com/android/server/wm/TaskPositioner:-get4	(Lcom/android/server/wm/TaskPositioner;)Lcom/android/server/wm/WindowManagerService;
      //   455: getfield 70	com/android/server/wm/WindowManagerService:mActivityManager	Landroid/app/IActivityManager;
      //   458: aload_0
      //   459: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   462: invokestatic 74	com/android/server/wm/TaskPositioner:-get5	(Lcom/android/server/wm/TaskPositioner;)Lcom/android/server/wm/Task;
      //   465: getfield 80	com/android/server/wm/Task:mTaskId	I
      //   468: aload_0
      //   469: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   472: invokestatic 84	com/android/server/wm/TaskPositioner:-get7	(Lcom/android/server/wm/TaskPositioner;)Landroid/graphics/Rect;
      //   475: iconst_1
      //   476: invokeinterface 90 4 0
      //   481: ldc2_w 175
      //   484: invokestatic 188	android/os/Trace:traceEnd	(J)V
      //   487: goto -395 -> 92
      //   490: astore 6
      //   492: aload_0
      //   493: aload_1
      //   494: iconst_0
      //   495: invokevirtual 39	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:finishInputEvent	(Landroid/view/InputEvent;Z)V
      //   498: aload 6
      //   500: athrow
      //   501: astore 7
      //   503: aload 6
      //   505: monitorexit
      //   506: aload 7
      //   508: athrow
      //   509: getstatic 114	com/android/server/wm/WindowManagerDebugConfig:DEBUG_TASK_POSITIONING	Z
      //   512: ifeq +43 -> 555
      //   515: invokestatic 118	com/android/server/wm/TaskPositioner:-get0	()Ljava/lang/String;
      //   518: new 120	java/lang/StringBuilder
      //   521: dup
      //   522: invokespecial 123	java/lang/StringBuilder:<init>	()V
      //   525: ldc -66
      //   527: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   530: fload_2
      //   531: invokevirtual 132	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   534: ldc -122
      //   536: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   539: fload_3
      //   540: invokevirtual 132	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   543: ldc -120
      //   545: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   548: invokevirtual 139	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   551: invokestatic 145	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   554: pop
      //   555: aload_0
      //   556: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   559: iconst_1
      //   560: invokestatic 161	com/android/server/wm/TaskPositioner:-set0	(Lcom/android/server/wm/TaskPositioner;Z)Z
      //   563: pop
      //   564: goto -472 -> 92
      //   567: getstatic 114	com/android/server/wm/WindowManagerDebugConfig:DEBUG_TASK_POSITIONING	Z
      //   570: ifeq +43 -> 613
      //   573: invokestatic 118	com/android/server/wm/TaskPositioner:-get0	()Ljava/lang/String;
      //   576: new 120	java/lang/StringBuilder
      //   579: dup
      //   580: invokespecial 123	java/lang/StringBuilder:<init>	()V
      //   583: ldc -64
      //   585: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   588: fload_2
      //   589: invokevirtual 132	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   592: ldc -122
      //   594: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   597: fload_3
      //   598: invokevirtual 132	java/lang/StringBuilder:append	(F)Ljava/lang/StringBuilder;
      //   601: ldc -120
      //   603: invokevirtual 129	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   606: invokevirtual 139	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   609: invokestatic 145	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   612: pop
      //   613: aload_0
      //   614: getfield 13	com/android/server/wm/TaskPositioner$WindowPositionerEventReceiver:this$0	Lcom/android/server/wm/TaskPositioner;
      //   617: iconst_1
      //   618: invokestatic 161	com/android/server/wm/TaskPositioner:-set0	(Lcom/android/server/wm/TaskPositioner;Z)Z
      //   621: pop
      //   622: goto -530 -> 92
      //   625: astore 7
      //   627: aload 6
      //   629: monitorexit
      //   630: aload 7
      //   632: athrow
      //   633: iconst_1
      //   634: istore 4
      //   636: goto -438 -> 198
      //   639: astore 6
      //   641: goto -411 -> 230
      //   644: astore 6
      //   646: goto -165 -> 481
      //   649: goto -557 -> 92
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	652	0	this	WindowPositionerEventReceiver
      //   0	652	1	paramInputEvent	android.view.InputEvent
      //   49	540	2	f1	float
      //   55	543	3	f2	float
      //   196	439	4	i	int
      //   30	107	5	bool	boolean
      //   302	8	6	localException	Exception
      //   490	138	6	localObject2	Object
      //   639	1	6	localRemoteException1	android.os.RemoteException
      //   644	1	6	localRemoteException2	android.os.RemoteException
      //   501	6	7	localObject3	Object
      //   625	6	7	localObject4	Object
      // Exception table:
      //   from	to	target	type
      //   23	32	302	java/lang/Exception
      //   44	92	302	java/lang/Exception
      //   92	126	302	java/lang/Exception
      //   133	136	302	java/lang/Exception
      //   141	174	302	java/lang/Exception
      //   174	195	302	java/lang/Exception
      //   198	230	302	java/lang/Exception
      //   230	246	302	java/lang/Exception
      //   253	299	302	java/lang/Exception
      //   322	368	302	java/lang/Exception
      //   368	383	302	java/lang/Exception
      //   417	448	302	java/lang/Exception
      //   448	481	302	java/lang/Exception
      //   481	487	302	java/lang/Exception
      //   503	509	302	java/lang/Exception
      //   509	555	302	java/lang/Exception
      //   555	564	302	java/lang/Exception
      //   567	613	302	java/lang/Exception
      //   613	622	302	java/lang/Exception
      //   627	633	302	java/lang/Exception
      //   23	32	490	finally
      //   44	92	490	finally
      //   92	126	490	finally
      //   133	136	490	finally
      //   141	174	490	finally
      //   174	195	490	finally
      //   198	230	490	finally
      //   230	246	490	finally
      //   253	299	490	finally
      //   304	315	490	finally
      //   322	368	490	finally
      //   368	383	490	finally
      //   417	448	490	finally
      //   448	481	490	finally
      //   481	487	490	finally
      //   503	509	490	finally
      //   509	555	490	finally
      //   555	564	490	finally
      //   567	613	490	finally
      //   613	622	490	finally
      //   627	633	490	finally
      //   383	417	501	finally
      //   126	133	625	finally
      //   141	174	639	android/os/RemoteException
      //   174	195	639	android/os/RemoteException
      //   198	230	639	android/os/RemoteException
      //   448	481	644	android/os/RemoteException
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/TaskPositioner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */