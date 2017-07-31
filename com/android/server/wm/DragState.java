package com.android.server.wm;

import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Region;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IUserManager;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Slog;
import android.view.Display;
import android.view.DragEvent;
import android.view.IWindow;
import android.view.InputChannel;
import android.view.SurfaceControl;
import android.view.WindowManagerPolicy;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import com.android.internal.view.IDragAndDropPermissions;
import com.android.server.input.InputApplicationHandle;
import com.android.server.input.InputManagerService;
import com.android.server.input.InputWindowHandle;
import java.util.ArrayList;
import java.util.Iterator;

class DragState
{
  private static final long ANIMATION_DURATION_MS = 500L;
  private static final int DRAG_FLAGS_URI_ACCESS = 3;
  private static final int DRAG_FLAGS_URI_PERMISSIONS = 195;
  private Animation mAnimation;
  InputChannel mClientChannel;
  boolean mCrossProfileCopyAllowed;
  private final Interpolator mCubicEaseOutInterpolator = new DecelerateInterpolator(1.5F);
  float mCurrentX;
  float mCurrentY;
  ClipData mData;
  ClipDescription mDataDescription;
  DisplayContent mDisplayContent;
  InputApplicationHandle mDragApplicationHandle;
  boolean mDragInProgress;
  boolean mDragResult;
  InputWindowHandle mDragWindowHandle;
  int mFlags;
  WindowManagerService.DragInputEventReceiver mInputEventReceiver;
  IBinder mLocalWin;
  ArrayList<WindowState> mNotifiedWindows;
  float mOriginalAlpha;
  float mOriginalX;
  float mOriginalY;
  int mPid;
  InputChannel mServerChannel;
  final WindowManagerService mService;
  int mSourceUserId;
  SurfaceControl mSurfaceControl;
  WindowState mTargetWindow;
  float mThumbOffsetX;
  float mThumbOffsetY;
  IBinder mToken;
  int mTouchSource;
  final Transformation mTransformation = new Transformation();
  int mUid;
  
  DragState(WindowManagerService paramWindowManagerService, IBinder paramIBinder1, SurfaceControl paramSurfaceControl, int paramInt, IBinder paramIBinder2)
  {
    this.mService = paramWindowManagerService;
    this.mToken = paramIBinder1;
    this.mSurfaceControl = paramSurfaceControl;
    this.mFlags = paramInt;
    this.mLocalWin = paramIBinder2;
    this.mNotifiedWindows = new ArrayList();
  }
  
  private void broadcastDragEndedLw()
  {
    int i = Process.myPid();
    if (WindowManagerDebugConfig.DEBUG_DRAG) {
      Slog.d("WindowManager", "broadcasting DRAG_ENDED");
    }
    Iterator localIterator = this.mNotifiedWindows.iterator();
    for (;;)
    {
      if (localIterator.hasNext())
      {
        WindowState localWindowState = (WindowState)localIterator.next();
        float f3 = 0.0F;
        float f4 = 0.0F;
        float f2 = f3;
        float f1 = f4;
        if (!this.mDragResult)
        {
          f2 = f3;
          f1 = f4;
          if (localWindowState.mSession.mPid == this.mPid)
          {
            f2 = this.mCurrentX;
            f1 = this.mCurrentY;
          }
        }
        DragEvent localDragEvent = DragEvent.obtain(4, f2, f1, null, null, null, null, this.mDragResult);
        try
        {
          localWindowState.mClient.dispatchDragEvent(localDragEvent);
          if (i != localWindowState.mSession.mPid) {
            localDragEvent.recycle();
          }
        }
        catch (RemoteException localRemoteException)
        {
          for (;;)
          {
            Slog.w("WindowManager", "Unable to drag-end window " + localWindowState);
          }
        }
      }
    }
    this.mNotifiedWindows.clear();
    this.mDragInProgress = false;
  }
  
  private void cleanUpDragLw()
  {
    broadcastDragEndedLw();
    if (isFromSource(8194)) {
      this.mService.restorePointerIconLocked(this.mDisplayContent, this.mCurrentX, this.mCurrentY);
    }
    unregister();
    reset();
    this.mService.mDragState = null;
    this.mService.mInputMonitor.updateInputWindowsLw(true);
  }
  
  private Animation createCancelAnimationLocked()
  {
    AnimationSet localAnimationSet = new AnimationSet(false);
    localAnimationSet.addAnimation(new ScaleAnimation(1.0F, 0.0F, 1.0F, 0.0F, this.mThumbOffsetX, this.mThumbOffsetY));
    localAnimationSet.addAnimation(new AlphaAnimation(this.mOriginalAlpha, 0.0F));
    localAnimationSet.setDuration(500L);
    localAnimationSet.setInterpolator(this.mCubicEaseOutInterpolator);
    localAnimationSet.initialize(0, 0, 0, 0);
    localAnimationSet.start();
    return localAnimationSet;
  }
  
  private Animation createReturnAnimationLocked()
  {
    AnimationSet localAnimationSet = new AnimationSet(false);
    localAnimationSet.addAnimation(new TranslateAnimation(0.0F, this.mOriginalX - this.mCurrentX, 0.0F, this.mOriginalY - this.mCurrentY));
    localAnimationSet.addAnimation(new AlphaAnimation(this.mOriginalAlpha, this.mOriginalAlpha / 2.0F));
    localAnimationSet.setDuration(500L);
    localAnimationSet.setInterpolator(this.mCubicEaseOutInterpolator);
    localAnimationSet.initialize(0, 0, 0, 0);
    localAnimationSet.start();
    return localAnimationSet;
  }
  
  private boolean isFromSource(int paramInt)
  {
    return (this.mTouchSource & paramInt) == paramInt;
  }
  
  private boolean isValidDropTarget(WindowState paramWindowState)
  {
    if (paramWindowState == null) {
      return false;
    }
    if (!paramWindowState.isPotentialDragTarget()) {
      return false;
    }
    if (((this.mFlags & 0x100) != 0) && (targetWindowSupportsGlobalDrag(paramWindowState))) {}
    while ((this.mCrossProfileCopyAllowed) || (this.mSourceUserId == UserHandle.getUserId(paramWindowState.getOwningUid())))
    {
      return true;
      if (this.mLocalWin != paramWindowState.mClient.asBinder()) {
        return false;
      }
    }
    return false;
  }
  
  private boolean isWindowNotified(WindowState paramWindowState)
  {
    Iterator localIterator = this.mNotifiedWindows.iterator();
    while (localIterator.hasNext()) {
      if ((WindowState)localIterator.next() == paramWindowState) {
        return true;
      }
    }
    return false;
  }
  
  private static DragEvent obtainDragEvent(WindowState paramWindowState, int paramInt, float paramFloat1, float paramFloat2, Object paramObject, ClipDescription paramClipDescription, ClipData paramClipData, IDragAndDropPermissions paramIDragAndDropPermissions, boolean paramBoolean)
  {
    return DragEvent.obtain(paramInt, paramWindowState.translateToWindowX(paramFloat1), paramWindowState.translateToWindowY(paramFloat2), paramObject, paramClipDescription, paramClipData, paramIDragAndDropPermissions, paramBoolean);
  }
  
  private void sendDragStartedLw(WindowState paramWindowState, float paramFloat1, float paramFloat2, ClipDescription paramClipDescription)
  {
    if ((this.mDragInProgress) && (isValidDropTarget(paramWindowState))) {
      paramClipDescription = obtainDragEvent(paramWindowState, 1, paramFloat1, paramFloat2, null, paramClipDescription, null, null, false);
    }
    try
    {
      paramWindowState.mClient.dispatchDragEvent(paramClipDescription);
      this.mNotifiedWindows.add(paramWindowState);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("WindowManager", "Unable to drag-start window " + paramWindowState);
      return;
    }
    finally
    {
      if (Process.myPid() != paramWindowState.mSession.mPid) {
        paramClipDescription.recycle();
      }
    }
  }
  
  private boolean targetWindowSupportsGlobalDrag(WindowState paramWindowState)
  {
    return (paramWindowState.mAppToken == null) || (paramWindowState.mAppToken.targetSdk >= 24);
  }
  
  void broadcastDragStartedLw(float paramFloat1, float paramFloat2)
  {
    Object localObject = null;
    this.mCurrentX = paramFloat1;
    this.mOriginalX = paramFloat1;
    this.mCurrentY = paramFloat2;
    this.mOriginalY = paramFloat2;
    if (this.mData != null) {
      localObject = this.mData.getDescription();
    }
    this.mDataDescription = ((ClipDescription)localObject);
    this.mNotifiedWindows.clear();
    this.mDragInProgress = true;
    this.mSourceUserId = UserHandle.getUserId(this.mUid);
    localObject = (IUserManager)ServiceManager.getService("user");
    for (;;)
    {
      try
      {
        if (!((IUserManager)localObject).getUserRestrictions(this.mSourceUserId).getBoolean("no_cross_profile_copy_paste")) {
          continue;
        }
        bool = false;
        this.mCrossProfileCopyAllowed = bool;
      }
      catch (RemoteException localRemoteException)
      {
        boolean bool;
        int j;
        int i;
        Slog.e("WindowManager", "Remote Exception calling UserManager: " + localRemoteException);
        this.mCrossProfileCopyAllowed = false;
        continue;
      }
      if (WindowManagerDebugConfig.DEBUG_DRAG) {
        Slog.d("WindowManager", "broadcasting DRAG_STARTED at (" + paramFloat1 + ", " + paramFloat2 + ")");
      }
      localObject = this.mDisplayContent.getWindowList();
      j = ((WindowList)localObject).size();
      i = 0;
      if (i >= j) {
        break;
      }
      sendDragStartedLw((WindowState)((WindowList)localObject).get(i), paramFloat1, paramFloat2, this.mDataDescription);
      i += 1;
      continue;
      bool = true;
    }
  }
  
  void cancelDragLw()
  {
    if (this.mAnimation != null) {
      return;
    }
    this.mAnimation = createCancelAnimationLocked();
    this.mService.scheduleAnimationLocked();
  }
  
  void endDragLw()
  {
    if (this.mAnimation != null) {
      return;
    }
    if (!this.mDragResult)
    {
      this.mAnimation = createReturnAnimationLocked();
      this.mService.scheduleAnimationLocked();
      return;
    }
    cleanUpDragLw();
  }
  
  int getDragLayerLw()
  {
    return this.mService.mPolicy.windowTypeToLayerLw(2016) * 10000 + 1000;
  }
  
  boolean notifyDropLw(float paramFloat1, float paramFloat2)
  {
    if (this.mAnimation != null) {
      return false;
    }
    this.mCurrentX = paramFloat1;
    this.mCurrentY = paramFloat2;
    WindowState localWindowState = this.mDisplayContent.getTouchableWinAtPointLocked(paramFloat1, paramFloat2);
    if (!isWindowNotified(localWindowState))
    {
      this.mDragResult = false;
      return true;
    }
    if (WindowManagerDebugConfig.DEBUG_DRAG) {
      Slog.d("WindowManager", "sending DROP to " + localWindowState);
    }
    int i = UserHandle.getUserId(localWindowState.getOwningUid());
    IBinder localIBinder = null;
    Object localObject1 = localIBinder;
    if ((this.mFlags & 0x100) != 0)
    {
      localObject1 = localIBinder;
      if ((this.mFlags & 0x3) != 0) {
        localObject1 = new DragAndDropPermissionsHandler(this.mData, this.mUid, localWindowState.getOwningPackage(), this.mFlags & 0xC3, this.mSourceUserId, i);
      }
    }
    if (this.mSourceUserId != i) {
      this.mData.fixUris(this.mSourceUserId);
    }
    i = Process.myPid();
    localIBinder = localWindowState.mClient.asBinder();
    localObject1 = obtainDragEvent(localWindowState, 3, paramFloat1, paramFloat2, null, null, this.mData, (IDragAndDropPermissions)localObject1, false);
    try
    {
      localWindowState.mClient.dispatchDragEvent((DragEvent)localObject1);
      this.mService.mH.removeMessages(21, localIBinder);
      Message localMessage = this.mService.mH.obtainMessage(21, localIBinder);
      this.mService.mH.sendMessageDelayed(localMessage, 5000L);
      if (i != localWindowState.mSession.mPid) {
        ((DragEvent)localObject1).recycle();
      }
      this.mToken = localIBinder;
      return false;
    }
    catch (RemoteException localRemoteException)
    {
      Slog.w("WindowManager", "can't send drop notification to win " + localWindowState);
      return true;
    }
    finally
    {
      if (i != localWindowState.mSession.mPid) {
        ((DragEvent)localObject1).recycle();
      }
    }
  }
  
  void notifyLocationLw(float paramFloat1, float paramFloat2)
  {
    Object localObject2 = this.mDisplayContent.getTouchableWinAtPointLocked(paramFloat1, paramFloat2);
    Object localObject1 = localObject2;
    if (localObject2 != null) {
      if (!isWindowNotified((WindowState)localObject2)) {
        break label195;
      }
    }
    for (localObject1 = localObject2;; localObject1 = null)
    {
      try
      {
        int i = Process.myPid();
        if ((localObject1 != this.mTargetWindow) && (this.mTargetWindow != null))
        {
          if (WindowManagerDebugConfig.DEBUG_DRAG) {
            Slog.d("WindowManager", "sending DRAG_EXITED to " + this.mTargetWindow);
          }
          localObject2 = obtainDragEvent(this.mTargetWindow, 6, 0.0F, 0.0F, null, null, null, null, false);
          this.mTargetWindow.mClient.dispatchDragEvent((DragEvent)localObject2);
          if (i != this.mTargetWindow.mSession.mPid) {
            ((DragEvent)localObject2).recycle();
          }
        }
        if (localObject1 != null)
        {
          localObject2 = obtainDragEvent((WindowState)localObject1, 2, paramFloat1, paramFloat2, null, null, null, null, false);
          ((WindowState)localObject1).mClient.dispatchDragEvent((DragEvent)localObject2);
          if (i != ((WindowState)localObject1).mSession.mPid) {
            ((DragEvent)localObject2).recycle();
          }
        }
      }
      catch (RemoteException localRemoteException)
      {
        for (;;)
        {
          label195:
          Slog.w("WindowManager", "can't send drag notification to windows");
        }
      }
      this.mTargetWindow = ((WindowState)localObject1);
      return;
    }
  }
  
  void notifyMoveLw(float paramFloat1, float paramFloat2)
  {
    if (this.mAnimation != null) {
      return;
    }
    this.mCurrentX = paramFloat1;
    this.mCurrentY = paramFloat2;
    if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
      Slog.i("WindowManager", ">>> OPEN TRANSACTION notifyMoveLw");
    }
    SurfaceControl.openTransaction();
    try
    {
      this.mSurfaceControl.setPosition(paramFloat1 - this.mThumbOffsetX, paramFloat2 - this.mThumbOffsetY);
      if (WindowManagerDebugConfig.SHOW_TRANSACTIONS) {
        Slog.i("WindowManager", "  DRAG " + this.mSurfaceControl + ": pos=(" + (int)(paramFloat1 - this.mThumbOffsetX) + "," + (int)(paramFloat2 - this.mThumbOffsetY) + ")");
      }
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION notifyMoveLw");
      }
      notifyLocationLw(paramFloat1, paramFloat2);
      return;
    }
    finally
    {
      SurfaceControl.closeTransaction();
      if (WindowManagerDebugConfig.SHOW_LIGHT_TRANSACTIONS) {
        Slog.i("WindowManager", "<<< CLOSE TRANSACTION notifyMoveLw");
      }
    }
  }
  
  void overridePointerIconLw(int paramInt)
  {
    this.mTouchSource = paramInt;
    if (isFromSource(8194)) {
      InputManager.getInstance().setPointerIconType(1021);
    }
  }
  
  void register(Display paramDisplay)
  {
    if (WindowManagerDebugConfig.DEBUG_DRAG) {
      Slog.d("WindowManager", "registering drag input channel");
    }
    if (this.mClientChannel != null)
    {
      Slog.e("WindowManager", "Duplicate register of drag input channel");
      return;
    }
    this.mDisplayContent = this.mService.getDisplayContentLocked(paramDisplay.getDisplayId());
    Object localObject = InputChannel.openInputChannelPair("drag");
    this.mServerChannel = localObject[0];
    this.mClientChannel = localObject[1];
    this.mService.mInputManager.registerInputChannel(this.mServerChannel, null);
    localObject = this.mService;
    localObject.getClass();
    this.mInputEventReceiver = new WindowManagerService.DragInputEventReceiver((WindowManagerService)localObject, this.mClientChannel, this.mService.mH.getLooper());
    this.mDragApplicationHandle = new InputApplicationHandle(null);
    this.mDragApplicationHandle.name = "drag";
    this.mDragApplicationHandle.dispatchingTimeoutNanos = 5000000000L;
    this.mDragWindowHandle = new InputWindowHandle(this.mDragApplicationHandle, null, paramDisplay.getDisplayId());
    this.mDragWindowHandle.name = "drag";
    this.mDragWindowHandle.inputChannel = this.mServerChannel;
    this.mDragWindowHandle.layer = getDragLayerLw();
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
    localObject = new Point();
    paramDisplay.getRealSize((Point)localObject);
    this.mDragWindowHandle.frameRight = ((Point)localObject).x;
    this.mDragWindowHandle.frameBottom = ((Point)localObject).y;
    if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
      Slog.d("WindowManager", "Pausing rotation during drag");
    }
    this.mService.pauseRotationLocked();
  }
  
  void reset()
  {
    if (this.mSurfaceControl != null) {
      this.mSurfaceControl.destroy();
    }
    this.mSurfaceControl = null;
    this.mFlags = 0;
    this.mLocalWin = null;
    this.mToken = null;
    this.mData = null;
    this.mThumbOffsetY = 0.0F;
    this.mThumbOffsetX = 0.0F;
    this.mNotifiedWindows = null;
  }
  
  void sendDragStartedIfNeededLw(WindowState paramWindowState)
  {
    if (this.mDragInProgress)
    {
      if (isWindowNotified(paramWindowState)) {
        return;
      }
      if (WindowManagerDebugConfig.DEBUG_DRAG) {
        Slog.d("WindowManager", "need to send DRAG_STARTED to new window " + paramWindowState);
      }
      sendDragStartedLw(paramWindowState, this.mCurrentX, this.mCurrentY, this.mDataDescription);
    }
  }
  
  boolean stepAnimationLocked(long paramLong)
  {
    if (this.mAnimation == null) {
      return false;
    }
    this.mTransformation.clear();
    if (!this.mAnimation.getTransformation(paramLong, this.mTransformation))
    {
      cleanUpDragLw();
      return false;
    }
    this.mTransformation.getMatrix().postTranslate(this.mCurrentX - this.mThumbOffsetX, this.mCurrentY - this.mThumbOffsetY);
    float[] arrayOfFloat = this.mService.mTmpFloats;
    this.mTransformation.getMatrix().getValues(arrayOfFloat);
    this.mSurfaceControl.setPosition(arrayOfFloat[2], arrayOfFloat[5]);
    this.mSurfaceControl.setAlpha(this.mTransformation.getAlpha());
    this.mSurfaceControl.setMatrix(arrayOfFloat[0], arrayOfFloat[3], arrayOfFloat[1], arrayOfFloat[4]);
    return true;
  }
  
  void unregister()
  {
    if (WindowManagerDebugConfig.DEBUG_DRAG) {
      Slog.d("WindowManager", "unregistering drag input channel");
    }
    if (this.mClientChannel == null)
    {
      Slog.e("WindowManager", "Unregister of nonexistent drag input channel");
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
    if (WindowManagerDebugConfig.DEBUG_ORIENTATION) {
      Slog.d("WindowManager", "Resuming rotation after drag");
    }
    this.mService.resumeRotationLocked();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/DragState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */