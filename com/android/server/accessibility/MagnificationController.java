package com.android.server.accessibility;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.MathUtils;
import android.util.Property;
import android.view.MagnificationSpec;
import android.view.WindowManagerInternal;
import android.view.WindowManagerInternal.MagnificationCallbacks;
import android.view.animation.DecelerateInterpolator;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.SomeArgs;
import com.android.server.LocalServices;
import java.util.Locale;

class MagnificationController
{
  private static final boolean DEBUG_SET_MAGNIFICATION_SPEC = false;
  private static final float DEFAULT_MAGNIFICATION_SCALE = 2.0F;
  private static final int DEFAULT_SCREEN_MAGNIFICATION_AUTO_UPDATE = 1;
  private static final int INVALID_ID = -1;
  private static final String LOG_TAG = "MagnificationController";
  private static final float MAX_SCALE = 5.0F;
  private static final float MIN_PERSISTED_SCALE = 2.0F;
  private static final float MIN_SCALE = 1.0F;
  private final AccessibilityManagerService mAms;
  private final ContentResolver mContentResolver;
  private final MagnificationSpec mCurrentMagnificationSpec = MagnificationSpec.obtain();
  private int mIdOfLastServiceToMagnify = -1;
  private final Object mLock;
  private final Rect mMagnificationBounds = new Rect();
  private final Region mMagnificationRegion = Region.obtain();
  private boolean mRegistered;
  private final ScreenStateObserver mScreenStateObserver;
  private final SpecAnimationBridge mSpecAnimationBridge;
  private final Rect mTempRect = new Rect();
  private final Rect mTempRect1 = new Rect();
  private boolean mUnregisterPending;
  private int mUserId;
  private final WindowStateObserver mWindowStateObserver;
  
  public MagnificationController(Context paramContext, AccessibilityManagerService paramAccessibilityManagerService, Object paramObject)
  {
    this.mAms = paramAccessibilityManagerService;
    this.mContentResolver = paramContext.getContentResolver();
    this.mScreenStateObserver = new ScreenStateObserver(paramContext, this);
    this.mWindowStateObserver = new WindowStateObserver(paramContext, this);
    this.mLock = paramObject;
    this.mSpecAnimationBridge = new SpecAnimationBridge(paramContext, this.mLock, null);
  }
  
  private void getMagnifiedFrameInContentCoordsLocked(Rect paramRect)
  {
    float f1 = getSentScale();
    float f2 = getSentOffsetX();
    float f3 = getSentOffsetY();
    getMagnificationBounds(paramRect);
    paramRect.offset((int)-f2, (int)-f3);
    paramRect.scale(1.0F / f1);
  }
  
  private float getMinOffsetXLocked()
  {
    float f = this.mMagnificationBounds.width();
    return f - this.mCurrentMagnificationSpec.scale * f;
  }
  
  private float getMinOffsetYLocked()
  {
    float f = this.mMagnificationBounds.height();
    return f - this.mCurrentMagnificationSpec.scale * f;
  }
  
  private boolean isScreenMagnificationAutoUpdateEnabled()
  {
    return Settings.Secure.getInt(this.mContentResolver, "accessibility_display_magnification_auto_update", 1) == 1;
  }
  
  private void onMagnificationChangedLocked()
  {
    this.mAms.onMagnificationStateChanged();
    this.mAms.notifyMagnificationChanged(this.mMagnificationRegion, getScale(), getCenterX(), getCenterY());
    if ((!this.mUnregisterPending) || (isMagnifying())) {
      return;
    }
    unregisterInternalLocked();
  }
  
  private void onMagnificationRegionChanged(Region paramRegion, boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mRegistered;
      if (!bool) {
        return;
      }
      bool = false;
      int i = 0;
      if (!this.mMagnificationRegion.equals(paramRegion))
      {
        this.mMagnificationRegion.set(paramRegion);
        this.mMagnificationRegion.getBounds(this.mMagnificationBounds);
        i = 1;
      }
      if (paramBoolean)
      {
        paramRegion = SpecAnimationBridge.-get1(this.mSpecAnimationBridge);
        float f1 = paramRegion.scale;
        float f2 = paramRegion.offsetX;
        float f3 = paramRegion.offsetY;
        bool = setScaleAndCenterLocked(f1, (this.mMagnificationBounds.width() / 2.0F + this.mMagnificationBounds.left - f2) / f1, (this.mMagnificationBounds.height() / 2.0F + this.mMagnificationBounds.top - f3) / f1, false, -1);
      }
      if ((i == 0) || (!paramBoolean) || (bool)) {
        return;
      }
      onMagnificationChangedLocked();
    }
  }
  
  private void requestRectangleOnScreen(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    for (;;)
    {
      synchronized (this.mLock)
      {
        Rect localRect = this.mTempRect;
        getMagnificationBounds(localRect);
        boolean bool = localRect.intersects(paramInt1, paramInt2, paramInt3, paramInt4);
        if (!bool) {
          return;
        }
        localRect = this.mTempRect1;
        getMagnifiedFrameInContentCoordsLocked(localRect);
        if (paramInt3 - paramInt1 > localRect.width())
        {
          if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 0)
          {
            f1 = paramInt1 - localRect.left;
            if (paramInt4 - paramInt2 > localRect.height())
            {
              f2 = paramInt2 - localRect.top;
              float f3 = getScale();
              offsetMagnifiedRegionCenter(f1 * f3, f2 * f3, -1);
            }
          }
          else
          {
            f1 = paramInt3 - localRect.right;
            continue;
          }
        }
        else
        {
          if (paramInt1 < localRect.left)
          {
            f1 = paramInt1 - localRect.left;
            continue;
          }
          if (paramInt3 <= localRect.right) {
            break label250;
          }
          f1 = paramInt3 - localRect.right;
          continue;
        }
        if (paramInt2 < localRect.top)
        {
          f2 = paramInt2 - localRect.top;
          continue;
        }
        if (paramInt4 > localRect.bottom)
        {
          paramInt1 = localRect.bottom;
          f2 = paramInt4 - paramInt1;
          continue;
        }
        float f2 = 0.0F;
      }
      label250:
      float f1 = 0.0F;
    }
  }
  
  private boolean resetLocked(boolean paramBoolean)
  {
    boolean bool = false;
    if (!this.mRegistered) {
      return false;
    }
    MagnificationSpec localMagnificationSpec = this.mCurrentMagnificationSpec;
    if (localMagnificationSpec.isNop()) {}
    for (;;)
    {
      if (bool)
      {
        localMagnificationSpec.clear();
        onMagnificationChangedLocked();
      }
      this.mIdOfLastServiceToMagnify = -1;
      this.mSpecAnimationBridge.updateSentSpec(localMagnificationSpec, paramBoolean);
      return bool;
      bool = true;
    }
  }
  
  private boolean setScaleAndCenterLocked(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean, int paramInt)
  {
    boolean bool = updateMagnificationSpecLocked(paramFloat1, paramFloat2, paramFloat3);
    this.mSpecAnimationBridge.updateSentSpec(this.mCurrentMagnificationSpec, paramBoolean);
    if ((isMagnifying()) && (paramInt != -1)) {
      this.mIdOfLastServiceToMagnify = paramInt;
    }
    return bool;
  }
  
  private void unregisterInternalLocked()
  {
    if (this.mRegistered)
    {
      this.mSpecAnimationBridge.setEnabled(false);
      this.mScreenStateObserver.unregister();
      this.mWindowStateObserver.unregister();
      this.mMagnificationRegion.setEmpty();
      this.mRegistered = false;
    }
    this.mUnregisterPending = false;
  }
  
  private boolean updateMagnificationSpecLocked(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    float f = paramFloat2;
    if (Float.isNaN(paramFloat2)) {
      f = getCenterX();
    }
    paramFloat2 = paramFloat3;
    if (Float.isNaN(paramFloat3)) {
      paramFloat2 = getCenterY();
    }
    paramFloat3 = paramFloat1;
    if (Float.isNaN(paramFloat1)) {
      paramFloat3 = getScale();
    }
    if (!magnificationRegionContains(f, paramFloat2)) {
      return false;
    }
    MagnificationSpec localMagnificationSpec = this.mCurrentMagnificationSpec;
    boolean bool = false;
    paramFloat1 = MathUtils.constrain(paramFloat3, 1.0F, 5.0F);
    if (Float.compare(localMagnificationSpec.scale, paramFloat1) != 0)
    {
      localMagnificationSpec.scale = paramFloat1;
      bool = true;
    }
    paramFloat1 = MathUtils.constrain(this.mMagnificationBounds.width() / 2.0F + this.mMagnificationBounds.left - f * paramFloat3, getMinOffsetXLocked(), 0.0F);
    if (Float.compare(localMagnificationSpec.offsetX, paramFloat1) != 0)
    {
      localMagnificationSpec.offsetX = paramFloat1;
      bool = true;
    }
    paramFloat1 = MathUtils.constrain(this.mMagnificationBounds.height() / 2.0F + this.mMagnificationBounds.top - paramFloat2 * paramFloat3, getMinOffsetYLocked(), 0.0F);
    if (Float.compare(localMagnificationSpec.offsetY, paramFloat1) != 0)
    {
      localMagnificationSpec.offsetY = paramFloat1;
      bool = true;
    }
    if (bool) {
      onMagnificationChangedLocked();
    }
    return bool;
  }
  
  public float getCenterX()
  {
    synchronized (this.mLock)
    {
      float f1 = this.mMagnificationBounds.width() / 2.0F;
      float f2 = this.mMagnificationBounds.left;
      float f3 = getOffsetX();
      float f4 = getScale();
      f1 = (f1 + f2 - f3) / f4;
      return f1;
    }
  }
  
  public float getCenterY()
  {
    synchronized (this.mLock)
    {
      float f1 = this.mMagnificationBounds.height() / 2.0F;
      float f2 = this.mMagnificationBounds.top;
      float f3 = getOffsetY();
      float f4 = getScale();
      f1 = (f1 + f2 - f3) / f4;
      return f1;
    }
  }
  
  public int getIdOfLastServiceToMagnify()
  {
    return this.mIdOfLastServiceToMagnify;
  }
  
  public void getMagnificationBounds(Rect paramRect)
  {
    synchronized (this.mLock)
    {
      paramRect.set(this.mMagnificationBounds);
      return;
    }
  }
  
  public void getMagnificationRegion(Region paramRegion)
  {
    synchronized (this.mLock)
    {
      paramRegion.set(this.mMagnificationRegion);
      return;
    }
  }
  
  public float getOffsetX()
  {
    return this.mCurrentMagnificationSpec.offsetX;
  }
  
  public float getOffsetY()
  {
    return this.mCurrentMagnificationSpec.offsetY;
  }
  
  public float getPersistedScale()
  {
    return Settings.Secure.getFloatForUser(this.mContentResolver, "accessibility_display_magnification_scale", 2.0F, this.mUserId);
  }
  
  public float getScale()
  {
    return this.mCurrentMagnificationSpec.scale;
  }
  
  public float getSentOffsetX()
  {
    return SpecAnimationBridge.-get1(this.mSpecAnimationBridge).offsetX;
  }
  
  public float getSentOffsetY()
  {
    return SpecAnimationBridge.-get1(this.mSpecAnimationBridge).offsetY;
  }
  
  public float getSentScale()
  {
    return SpecAnimationBridge.-get1(this.mSpecAnimationBridge).scale;
  }
  
  public boolean isMagnifying()
  {
    return this.mCurrentMagnificationSpec.scale > 1.0F;
  }
  
  public boolean isRegisteredLocked()
  {
    return this.mRegistered;
  }
  
  public boolean magnificationRegionContains(float paramFloat1, float paramFloat2)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mMagnificationRegion.contains((int)paramFloat1, (int)paramFloat2);
      return bool;
    }
  }
  
  public void offsetMagnifiedRegionCenter(float paramFloat1, float paramFloat2, int paramInt)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mRegistered;
      if (!bool) {
        return;
      }
      MagnificationSpec localMagnificationSpec = this.mCurrentMagnificationSpec;
      localMagnificationSpec.offsetX = MathUtils.constrain(localMagnificationSpec.offsetX - paramFloat1, getMinOffsetXLocked(), 0.0F);
      localMagnificationSpec.offsetY = MathUtils.constrain(localMagnificationSpec.offsetY - paramFloat2, getMinOffsetYLocked(), 0.0F);
      if (paramInt != -1) {
        this.mIdOfLastServiceToMagnify = paramInt;
      }
      this.mSpecAnimationBridge.updateSentSpec(localMagnificationSpec, false);
      return;
    }
  }
  
  public void persistScale()
  {
    new AsyncTask()
    {
      protected Void doInBackground(Void... paramAnonymousVarArgs)
      {
        Settings.Secure.putFloatForUser(MagnificationController.-get0(MagnificationController.this), "accessibility_display_magnification_scale", this.val$scale, this.val$userId);
        return null;
      }
    }.execute(new Void[0]);
  }
  
  public void register()
  {
    synchronized (this.mLock)
    {
      if (!this.mRegistered)
      {
        this.mScreenStateObserver.register();
        this.mWindowStateObserver.register();
        this.mSpecAnimationBridge.setEnabled(true);
        this.mWindowStateObserver.getMagnificationRegion(this.mMagnificationRegion);
        this.mMagnificationRegion.getBounds(this.mMagnificationBounds);
        this.mRegistered = true;
      }
      return;
    }
  }
  
  public boolean reset(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      paramBoolean = resetLocked(paramBoolean);
      return paramBoolean;
    }
  }
  
  boolean resetIfNeeded(boolean paramBoolean)
  {
    synchronized (this.mLock)
    {
      if ((isMagnifying()) && (isScreenMagnificationAutoUpdateEnabled()))
      {
        reset(paramBoolean);
        return true;
      }
      return false;
    }
  }
  
  public boolean setCenter(float paramFloat1, float paramFloat2, boolean paramBoolean, int paramInt)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mRegistered;
      if (!bool) {
        return false;
      }
      paramBoolean = setScaleAndCenterLocked(NaN.0F, paramFloat1, paramFloat2, paramBoolean, paramInt);
      return paramBoolean;
    }
  }
  
  public boolean setScale(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean, int paramInt)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mRegistered;
      if (!bool) {
        return false;
      }
      paramFloat1 = MathUtils.constrain(paramFloat1, 1.0F, 5.0F);
      Rect localRect = this.mTempRect;
      this.mMagnificationRegion.getBounds(localRect);
      MagnificationSpec localMagnificationSpec = this.mCurrentMagnificationSpec;
      float f3 = localMagnificationSpec.scale;
      float f1 = (localRect.width() / 2.0F - localMagnificationSpec.offsetX) / f3;
      float f2 = (localRect.height() / 2.0F - localMagnificationSpec.offsetY) / f3;
      paramFloat2 = (paramFloat2 - localMagnificationSpec.offsetX) / f3;
      paramFloat3 = (paramFloat3 - localMagnificationSpec.offsetY) / f3;
      float f4 = f3 / paramFloat1;
      f3 /= paramFloat1;
      this.mIdOfLastServiceToMagnify = paramInt;
      paramBoolean = setScaleAndCenterLocked(paramFloat1, paramFloat2 + (f1 - paramFloat2) * f4, paramFloat3 + (f2 - paramFloat3) * f3, paramBoolean, paramInt);
      return paramBoolean;
    }
  }
  
  public boolean setScaleAndCenter(float paramFloat1, float paramFloat2, float paramFloat3, boolean paramBoolean, int paramInt)
  {
    synchronized (this.mLock)
    {
      boolean bool = this.mRegistered;
      if (!bool) {
        return false;
      }
      paramBoolean = setScaleAndCenterLocked(paramFloat1, paramFloat2, paramFloat3, paramBoolean, paramInt);
      return paramBoolean;
    }
  }
  
  public void setUserId(int paramInt)
  {
    if (this.mUserId != paramInt) {
      this.mUserId = paramInt;
    }
    synchronized (this.mLock)
    {
      if (isMagnifying()) {
        reset(false);
      }
      return;
    }
  }
  
  public void unregister()
  {
    synchronized (this.mLock)
    {
      if (!isMagnifying())
      {
        unregisterInternalLocked();
        return;
      }
      this.mUnregisterPending = true;
      resetLocked(true);
    }
  }
  
  private static class ScreenStateObserver
    extends BroadcastReceiver
  {
    private static final int MESSAGE_ON_SCREEN_STATE_CHANGE = 1;
    private final Context mContext;
    private final MagnificationController mController;
    private final Handler mHandler;
    
    public ScreenStateObserver(Context paramContext, MagnificationController paramMagnificationController)
    {
      this.mContext = paramContext;
      this.mController = paramMagnificationController;
      this.mHandler = new StateChangeHandler(paramContext);
    }
    
    private void handleOnScreenStateChange()
    {
      this.mController.resetIfNeeded(false);
    }
    
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      this.mHandler.obtainMessage(1, paramIntent.getAction()).sendToTarget();
    }
    
    public void register()
    {
      this.mContext.registerReceiver(this, new IntentFilter("android.intent.action.SCREEN_OFF"));
    }
    
    public void unregister()
    {
      this.mContext.unregisterReceiver(this);
    }
    
    private class StateChangeHandler
      extends Handler
    {
      public StateChangeHandler(Context paramContext)
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default: 
          return;
        }
        MagnificationController.ScreenStateObserver.-wrap0(MagnificationController.ScreenStateObserver.this);
      }
    }
  }
  
  private static class SpecAnimationBridge
  {
    private static final int ACTION_UPDATE_SPEC = 1;
    @GuardedBy("mLock")
    private boolean mEnabled = false;
    private final Handler mHandler;
    private final Object mLock;
    private final long mMainThreadId;
    private final MagnificationSpec mSentMagnificationSpec = MagnificationSpec.obtain();
    private final ValueAnimator mTransformationAnimator;
    private final WindowManagerInternal mWindowManager;
    
    private SpecAnimationBridge(Context paramContext, Object paramObject)
    {
      this.mLock = paramObject;
      this.mMainThreadId = paramContext.getMainLooper().getThread().getId();
      this.mHandler = new UpdateHandler(paramContext);
      this.mWindowManager = ((WindowManagerInternal)LocalServices.getService(WindowManagerInternal.class));
      paramObject = new MagnificationSpecProperty();
      MagnificationSpecEvaluator localMagnificationSpecEvaluator = new MagnificationSpecEvaluator(null);
      long l = paramContext.getResources().getInteger(17694722);
      this.mTransformationAnimator = ObjectAnimator.ofObject(this, (Property)paramObject, localMagnificationSpecEvaluator, new MagnificationSpec[] { this.mSentMagnificationSpec });
      this.mTransformationAnimator.setDuration(l);
      this.mTransformationAnimator.setInterpolator(new DecelerateInterpolator(2.5F));
    }
    
    private void animateMagnificationSpecLocked(MagnificationSpec paramMagnificationSpec)
    {
      this.mTransformationAnimator.setObjectValues(new Object[] { this.mSentMagnificationSpec, paramMagnificationSpec });
      this.mTransformationAnimator.start();
    }
    
    private void setMagnificationSpecLocked(MagnificationSpec paramMagnificationSpec)
    {
      if (this.mEnabled)
      {
        this.mSentMagnificationSpec.setTo(paramMagnificationSpec);
        this.mWindowManager.setMagnificationSpec(paramMagnificationSpec);
      }
    }
    
    private void updateSentSpecInternal(MagnificationSpec paramMagnificationSpec, boolean paramBoolean)
    {
      if (this.mTransformationAnimator.isRunning()) {
        this.mTransformationAnimator.cancel();
      }
      synchronized (this.mLock)
      {
        if (this.mSentMagnificationSpec.equals(paramMagnificationSpec)) {}
        for (int i = 0;; i = 1)
        {
          if (i != 0)
          {
            if (!paramBoolean) {
              break;
            }
            animateMagnificationSpecLocked(paramMagnificationSpec);
          }
          return;
        }
        setMagnificationSpecLocked(paramMagnificationSpec);
      }
    }
    
    public void setEnabled(boolean paramBoolean)
    {
      synchronized (this.mLock)
      {
        if (paramBoolean != this.mEnabled)
        {
          this.mEnabled = paramBoolean;
          if (!this.mEnabled)
          {
            this.mSentMagnificationSpec.clear();
            this.mWindowManager.setMagnificationSpec(this.mSentMagnificationSpec);
          }
        }
        return;
      }
    }
    
    public void updateSentSpec(MagnificationSpec paramMagnificationSpec, boolean paramBoolean)
    {
      if (Thread.currentThread().getId() == this.mMainThreadId)
      {
        updateSentSpecInternal(paramMagnificationSpec, paramBoolean);
        return;
      }
      Handler localHandler = this.mHandler;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(1, i, 0, paramMagnificationSpec).sendToTarget();
        return;
      }
    }
    
    private static class MagnificationSpecEvaluator
      implements TypeEvaluator<MagnificationSpec>
    {
      private final MagnificationSpec mTempSpec = MagnificationSpec.obtain();
      
      public MagnificationSpec evaluate(float paramFloat, MagnificationSpec paramMagnificationSpec1, MagnificationSpec paramMagnificationSpec2)
      {
        MagnificationSpec localMagnificationSpec = this.mTempSpec;
        paramMagnificationSpec1.scale += (paramMagnificationSpec2.scale - paramMagnificationSpec1.scale) * paramFloat;
        paramMagnificationSpec1.offsetX += (paramMagnificationSpec2.offsetX - paramMagnificationSpec1.offsetX) * paramFloat;
        paramMagnificationSpec1.offsetY += (paramMagnificationSpec2.offsetY - paramMagnificationSpec1.offsetY) * paramFloat;
        return localMagnificationSpec;
      }
    }
    
    private static class MagnificationSpecProperty
      extends Property<MagnificationController.SpecAnimationBridge, MagnificationSpec>
    {
      public MagnificationSpecProperty()
      {
        super("spec");
      }
      
      public MagnificationSpec get(MagnificationController.SpecAnimationBridge paramSpecAnimationBridge)
      {
        synchronized (MagnificationController.SpecAnimationBridge.-get0(paramSpecAnimationBridge))
        {
          paramSpecAnimationBridge = MagnificationController.SpecAnimationBridge.-get1(paramSpecAnimationBridge);
          return paramSpecAnimationBridge;
        }
      }
      
      public void set(MagnificationController.SpecAnimationBridge paramSpecAnimationBridge, MagnificationSpec paramMagnificationSpec)
      {
        synchronized (MagnificationController.SpecAnimationBridge.-get0(paramSpecAnimationBridge))
        {
          MagnificationController.SpecAnimationBridge.-wrap0(paramSpecAnimationBridge, paramMagnificationSpec);
          return;
        }
      }
    }
    
    private class UpdateHandler
      extends Handler
    {
      public UpdateHandler(Context paramContext)
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default: 
          return;
        }
        if (paramMessage.arg1 == 1) {}
        for (boolean bool = true;; bool = false)
        {
          paramMessage = (MagnificationSpec)paramMessage.obj;
          MagnificationController.SpecAnimationBridge.-wrap1(MagnificationController.SpecAnimationBridge.this, paramMessage, bool);
          return;
        }
      }
    }
  }
  
  private static class WindowStateObserver
    implements WindowManagerInternal.MagnificationCallbacks
  {
    private static final int MESSAGE_ON_MAGNIFIED_BOUNDS_CHANGED = 1;
    private static final int MESSAGE_ON_RECTANGLE_ON_SCREEN_REQUESTED = 2;
    private static final int MESSAGE_ON_ROTATION_CHANGED = 4;
    private static final int MESSAGE_ON_USER_CONTEXT_CHANGED = 3;
    private final MagnificationController mController;
    private final Handler mHandler;
    private boolean mSpecIsDirty;
    private final WindowManagerInternal mWindowManager;
    
    public WindowStateObserver(Context paramContext, MagnificationController paramMagnificationController)
    {
      this.mController = paramMagnificationController;
      this.mWindowManager = ((WindowManagerInternal)LocalServices.getService(WindowManagerInternal.class));
      this.mHandler = new CallbackHandler(paramContext);
    }
    
    private void handleOnMagnifiedBoundsChanged(Region paramRegion)
    {
      MagnificationController.-wrap0(this.mController, paramRegion, this.mSpecIsDirty);
      this.mSpecIsDirty = false;
    }
    
    private void handleOnRectangleOnScreenRequested(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      MagnificationController.-wrap1(this.mController, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    private void handleOnRotationChanged()
    {
      boolean bool = true;
      if (this.mController.resetIfNeeded(true)) {
        bool = false;
      }
      this.mSpecIsDirty = bool;
    }
    
    private void handleOnUserContextChanged()
    {
      this.mController.resetIfNeeded(true);
    }
    
    public void getMagnificationRegion(Region paramRegion)
    {
      this.mWindowManager.getMagnificationRegion(paramRegion);
    }
    
    public void onMagnificationRegionChanged(Region paramRegion)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = Region.obtain(paramRegion);
      this.mHandler.obtainMessage(1, localSomeArgs).sendToTarget();
    }
    
    public void onRectangleOnScreenRequested(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.argi1 = paramInt1;
      localSomeArgs.argi2 = paramInt2;
      localSomeArgs.argi3 = paramInt3;
      localSomeArgs.argi4 = paramInt4;
      this.mHandler.obtainMessage(2, localSomeArgs).sendToTarget();
    }
    
    public void onRotationChanged(int paramInt)
    {
      this.mHandler.obtainMessage(4, paramInt, 0).sendToTarget();
    }
    
    public void onUserContextChanged()
    {
      this.mHandler.sendEmptyMessage(3);
    }
    
    public void register()
    {
      this.mWindowManager.setMagnificationCallbacks(this);
    }
    
    public void unregister()
    {
      this.mWindowManager.setMagnificationCallbacks(null);
    }
    
    private class CallbackHandler
      extends Handler
    {
      public CallbackHandler(Context paramContext)
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        switch (paramMessage.what)
        {
        default: 
          return;
        case 1: 
          paramMessage = (Region)((SomeArgs)paramMessage.obj).arg1;
          MagnificationController.WindowStateObserver.-wrap0(MagnificationController.WindowStateObserver.this, paramMessage);
          paramMessage.recycle();
          return;
        case 2: 
          paramMessage = (SomeArgs)paramMessage.obj;
          int i = paramMessage.argi1;
          int j = paramMessage.argi2;
          int k = paramMessage.argi3;
          int m = paramMessage.argi4;
          MagnificationController.WindowStateObserver.-wrap1(MagnificationController.WindowStateObserver.this, i, j, k, m);
          paramMessage.recycle();
          return;
        case 3: 
          MagnificationController.WindowStateObserver.-wrap3(MagnificationController.WindowStateObserver.this);
          return;
        }
        MagnificationController.WindowStateObserver.-wrap2(MagnificationController.WindowStateObserver.this);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/accessibility/MagnificationController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */