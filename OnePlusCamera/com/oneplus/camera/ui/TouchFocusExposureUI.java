package com.oneplus.camera.ui;

import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.Camera.MeteringRect;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.ExposureController;
import com.oneplus.camera.FocusController;
import com.oneplus.camera.FocusMode;
import com.oneplus.camera.FocusState;
import com.oneplus.camera.PhotoCaptureState;
import com.oneplus.camera.VideoCaptureState;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class TouchFocusExposureUI
  extends CameraComponent
  implements TouchAutoFocusUI, TouchAutoExposureUI
{
  private static final float AF_REGION_HEIGHT = 0.25F;
  private static final float AF_REGION_WIDTH = 0.25F;
  private static final long DURATION_AF_LOCK_THREAHOLD = 1000L;
  private static final long DURATION_MIN_TOUCH_AF_INTERVAL = 300L;
  private static final long DURATION_START_AF_THREAHOLD = 500L;
  private static final int MSG_LOCK_AE_AF = 10001;
  private static final int MSG_START_AF = 10000;
  private static final int MSG_TOUCH_UP = 10002;
  private static final float TOUCH_AF_DISTANCE_THRESHOLD = 0.05F;
  private List<Handle> m_DisableTouchExposureHandles = new ArrayList();
  private List<Handle> m_DisableTouchFocusHandles = new ArrayList();
  private ExposureController m_ExposureController;
  private Handle m_ExposureLockHandle;
  private FocusController m_FocusController;
  private Handle m_FocusLockHandle;
  private boolean m_IsPointerUppedWhenFocusScanning;
  private long m_LastAFTriggeredTime;
  private long m_LastTouchUpTimeStamp = 0L;
  private float m_TouchAfDistanceThreshold;
  private Handle m_TouchAfHandle;
  private final PointF m_TouchDownPosition = new PointF(-1.0F, -1.0F);
  
  TouchFocusExposureUI(CameraActivity paramCameraActivity)
  {
    super("Touch AE/AF UI", paramCameraActivity, true);
  }
  
  private boolean bindToExposureController()
  {
    if (this.m_ExposureController != null) {
      return true;
    }
    this.m_ExposureController = ((ExposureController)findComponent(ExposureController.class));
    return this.m_ExposureController != null;
  }
  
  private boolean bindToFocusController()
  {
    if (this.m_FocusController != null) {
      return true;
    }
    this.m_FocusController = ((FocusController)findComponent(FocusController.class));
    if (this.m_FocusController == null) {
      return false;
    }
    this.m_FocusController.addCallback(FocusController.PROP_FOCUS_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<FocusState> paramAnonymousPropertyKey, PropertyChangeEventArgs<FocusState> paramAnonymousPropertyChangeEventArgs)
      {
        TouchFocusExposureUI.-wrap0(TouchFocusExposureUI.this, (FocusState)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    return true;
  }
  
  private boolean canTouchFocus()
  {
    CameraActivity localCameraActivity = getCameraActivity();
    if (localCameraActivity.get(CameraActivity.PROP_STATE) != BaseActivity.State.RUNNING) {
      return false;
    }
    if ((!((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_CAMERA_PREVIEW_RECEIVED)).booleanValue()) && (localCameraActivity.get(CameraActivity.PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.CAPTURING)) {
      return false;
    }
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[((com.oneplus.camera.media.MediaType)localCameraActivity.get(CameraActivity.PROP_MEDIA_TYPE)).ordinal()])
    {
    default: 
    case 1: 
      do
      {
        return true;
      } while (localCameraActivity.get(CameraActivity.PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.READY);
      return false;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)localCameraActivity.get(CameraActivity.PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    }
    return false;
  }
  
  private void lockFocusAndExposure()
  {
    if (!((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue())
    {
      Log.w(this.TAG, "lockFocusAndExposure() - Capture is not ready");
      return;
    }
    if ((this.m_DisableTouchExposureHandles.isEmpty()) && (this.m_DisableTouchFocusHandles.isEmpty()))
    {
      if (((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_SELF_TIMER_STARTED)).booleanValue()) {
        Log.w(this.TAG, "lockFocusAndExposure() - Self timer started, returned");
      }
    }
    else
    {
      Log.w(this.TAG, "lockFocusAndExposure() - Disable exposure handle counts: " + this.m_DisableTouchExposureHandles.size() + ", disable focus handle counts: " + this.m_DisableTouchFocusHandles.size());
      return;
    }
    Log.w(this.TAG, "lockFocusAndExposure()");
    if ((!Handle.isValid(this.m_FocusLockHandle)) && (this.m_FocusController != null)) {
      this.m_FocusLockHandle = this.m_FocusController.lockFocus(0);
    }
    if ((!Handle.isValid(this.m_ExposureLockHandle)) && (this.m_ExposureController != null)) {
      this.m_ExposureLockHandle = this.m_ExposureController.lockAutoExposure(0);
    }
  }
  
  private void onFocusStateChanged(FocusState paramFocusState)
  {
    switch (-getcom-oneplus-camera-FocusStateSwitchesValues()[paramFocusState.ordinal()])
    {
    }
    do
    {
      return;
      if (((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_TOUCHING_ON_SCREEN)).booleanValue()) {}
      for (boolean bool = false;; bool = true)
      {
        this.m_IsPointerUppedWhenFocusScanning = bool;
        return;
      }
    } while ((this.m_FocusController.get(FocusController.PROP_FOCUS_MODE) != FocusMode.NORMAL_AF) || (!((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_TOUCHING_ON_SCREEN)).booleanValue()) || (this.m_LastAFTriggeredTime <= 0L) || (this.m_IsPointerUppedWhenFocusScanning));
    long l = 1000L - (SystemClock.elapsedRealtime() - this.m_LastAFTriggeredTime);
    if (l > 0L)
    {
      Log.v(this.TAG, "onFocusStateChanged() - Start AE/AF lock timer : ", Long.valueOf(l), "ms");
      getHandler().sendEmptyMessageDelayed(10001, l);
      return;
    }
    Log.v(this.TAG, "onFocusStateChanged() - Lock AE/AF immediately");
    lockFocusAndExposure();
  }
  
  private void onTouch(MotionEventArgs paramMotionEventArgs)
  {
    if ((paramMotionEventArgs.isHandled()) || (paramMotionEventArgs.getPointerCount() > 1))
    {
      this.m_IsPointerUppedWhenFocusScanning = true;
      getHandler().removeMessages(10000);
      return;
    }
    switch (paramMotionEventArgs.getAction())
    {
    default: 
    case 0: 
    case 2: 
      do
      {
        do
        {
          return;
          Log.v(this.TAG, "onTouch() - Down");
          PointF localPointF = new PointF();
          if (!getCameraActivity().getViewfinder().pointToPreview(paramMotionEventArgs.getX(), paramMotionEventArgs.getY(), localPointF, 0))
          {
            Log.w(this.TAG, "onTouch() - Cannot point to preview");
            return;
          }
          this.m_TouchDownPosition.x = paramMotionEventArgs.getX();
          this.m_TouchDownPosition.y = paramMotionEventArgs.getY();
          getHandler().removeMessages(10002);
          getHandler().sendEmptyMessageDelayed(10000, 500L);
          return;
        } while ((this.m_TouchDownPosition.x < 0.0F) || (this.m_TouchDownPosition.y < 0.0F));
        float f1 = Math.abs(paramMotionEventArgs.getX() - this.m_TouchDownPosition.x);
        float f2 = Math.abs(paramMotionEventArgs.getY() - this.m_TouchDownPosition.y);
        if (f1 * f1 + f2 * f2 > this.m_TouchAfDistanceThreshold * this.m_TouchAfDistanceThreshold) {
          this.m_TouchDownPosition.set(-1.0F, -1.0F);
        }
      } while (!getHandler().hasMessages(10001));
      paramMotionEventArgs.setHandled();
      return;
    case 3: 
      Log.v(this.TAG, "onTouch() - Cancel");
      getHandler().removeMessages(10000);
      getHandler().removeMessages(10001);
      this.m_TouchDownPosition.set(-1.0F, -1.0F);
      return;
    }
    if (Math.abs(System.currentTimeMillis() - this.m_LastTouchUpTimeStamp) > 0L) {
      getHandler().sendEmptyMessageDelayed(10002, 0L);
    }
    for (;;)
    {
      this.m_LastTouchUpTimeStamp = System.currentTimeMillis();
      return;
      if (canTouchFocus()) {
        getHandler().sendEmptyMessageDelayed(10002, 0L);
      } else {
        Log.v(this.TAG, "onTouch() - Ignore action up.");
      }
    }
  }
  
  private void onTouchUp()
  {
    Log.v(this.TAG, "onTouchUp()");
    getHandler().removeMessages(10001);
    if (getHandler().hasMessages(10000))
    {
      getHandler().removeMessages(10000);
      triggerFocusExposure(TriggerType.FOCUS_EXPOSURE_COMBINE);
    }
    this.m_TouchDownPosition.set(-1.0F, -1.0F);
  }
  
  private void triggerFocusExposure(TriggerType paramTriggerType)
  {
    triggerFocusExposure(paramTriggerType, this.m_TouchDownPosition.x, this.m_TouchDownPosition.y);
  }
  
  private void triggerFocusExposure(TriggerType paramTriggerType, float paramFloat1, float paramFloat2)
  {
    if (!bindToFocusController()) {
      return;
    }
    if (!canTouchFocus()) {
      return;
    }
    if ((paramFloat1 < 0.0F) || (paramFloat2 < 0.0F)) {
      return;
    }
    if (SystemClock.elapsedRealtime() - this.m_LastAFTriggeredTime < 300L) {
      return;
    }
    Object localObject = new PointF();
    CameraActivity localCameraActivity = getCameraActivity();
    if (!localCameraActivity.getViewfinder().pointToPreview(paramFloat1, paramFloat2, (PointF)localObject, 0)) {
      return;
    }
    float f1 = ((PointF)localObject).x - 0.125F;
    paramFloat2 = ((PointF)localObject).y - 0.125F;
    float f2 = f1 + 0.25F;
    float f3 = paramFloat2 + 0.25F;
    paramFloat1 = f1;
    if (f1 < 0.0F) {
      paramFloat1 = 0.0F;
    }
    f1 = paramFloat2;
    if (paramFloat2 < 0.0F) {
      f1 = 0.0F;
    }
    paramFloat2 = f2;
    if (f2 > 1.0F) {
      paramFloat2 = 1.0F;
    }
    f2 = f3;
    if (f3 > 1.0F) {
      f2 = 1.0F;
    }
    localObject = (Camera)localCameraActivity.get(CameraActivity.PROP_CAMERA);
    float f4 = paramFloat1;
    f3 = paramFloat2;
    if (localObject != null)
    {
      f4 = paramFloat1;
      f3 = paramFloat2;
      if (((Camera)localObject).get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT)
      {
        f4 = 1.0F - paramFloat2;
        f3 = 1.0F - paramFloat1;
      }
    }
    localObject = Arrays.asList(new Camera.MeteringRect[] { new Camera.MeteringRect(f4, f1, f3, f2, 1.0F) });
    unlockFocusAndExposure();
    if ((paramTriggerType == TriggerType.EXPOSURE) || (paramTriggerType == TriggerType.FOCUS_EXPOSURE_COMBINE))
    {
      if (bindToExposureController())
      {
        this.m_ExposureController.set(ExposureController.PROP_EXPOSURE_COMPENSATION, Float.valueOf(0.0F));
        this.m_ExposureController.set(ExposureController.PROP_AE_REGIONS, localObject);
      }
      raise(EVENT_TOUCH_AE, EventArgs.EMPTY);
    }
    if ((paramTriggerType == TriggerType.FOCUS) || (paramTriggerType == TriggerType.FOCUS_EXPOSURE_COMBINE))
    {
      this.m_TouchAfHandle = Handle.close(this.m_TouchAfHandle);
      this.m_TouchAfHandle = this.m_FocusController.startAutoFocus((List)localObject, 1);
      if (!Handle.isValid(this.m_TouchAfHandle))
      {
        Log.e(this.TAG, "startAutoFocus() - Fail to start touch AF");
        return;
      }
      this.m_LastAFTriggeredTime = SystemClock.elapsedRealtime();
      raise(EVENT_TOUCH_AF, EventArgs.EMPTY);
    }
  }
  
  private void unlockFocusAndExposure()
  {
    this.m_ExposureLockHandle = Handle.close(this.m_ExposureLockHandle);
    this.m_FocusLockHandle = Handle.close(this.m_FocusLockHandle);
  }
  
  private void updateDistanceThresholds(ScreenSize paramScreenSize)
  {
    this.m_TouchAfDistanceThreshold = (Math.min(paramScreenSize.getWidth(), paramScreenSize.getHeight()) * 0.05F);
  }
  
  public Handle disableTouchLockExposure()
  {
    Handle local2 = new Handle("Disable Touch Lock Exposure")
    {
      protected void onClose(int paramAnonymousInt)
      {
        TouchFocusExposureUI.-get0(TouchFocusExposureUI.this).remove(this);
      }
    };
    this.m_DisableTouchExposureHandles.add(local2);
    return local2;
  }
  
  public Handle disableTouchLockFocus()
  {
    Handle local3 = new Handle("Disable Touch Lock Focus")
    {
      protected void onClose(int paramAnonymousInt)
      {
        TouchFocusExposureUI.-get1(TouchFocusExposureUI.this).remove(this);
      }
    };
    this.m_DisableTouchFocusHandles.add(local3);
    return local3;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 10001: 
      lockFocusAndExposure();
      return;
    case 10000: 
      triggerFocusExposure(TriggerType.FOCUS_EXPOSURE_COMBINE);
      return;
    }
    onTouchUp();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addHandler(CameraActivity.EVENT_TOUCH, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MotionEventArgs> paramAnonymousEventKey, MotionEventArgs paramAnonymousMotionEventArgs)
      {
        TouchFocusExposureUI.-wrap1(TouchFocusExposureUI.this, paramAnonymousMotionEventArgs);
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_IS_TOUCHING_ON_SCREEN, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          TouchFocusExposureUI.-set0(TouchFocusExposureUI.this, true);
        }
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_SCREEN_SIZE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<ScreenSize> paramAnonymousPropertyKey, PropertyChangeEventArgs<ScreenSize> paramAnonymousPropertyChangeEventArgs)
      {
        TouchFocusExposureUI.-wrap3(TouchFocusExposureUI.this, (ScreenSize)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_PHOTO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PhotoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<PhotoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == PhotoCaptureState.REVIEWING) {
          TouchFocusExposureUI.-wrap2(TouchFocusExposureUI.this);
        }
      }
    });
    findComponent(CameraGallery.class, new ComponentSearchCallback()
    {
      public void onComponentFound(CameraGallery paramAnonymousCameraGallery)
      {
        paramAnonymousCameraGallery.addCallback(CameraGallery.PROP_UI_STATE, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey<CameraGallery.UIState> paramAnonymous2PropertyKey, PropertyChangeEventArgs<CameraGallery.UIState> paramAnonymous2PropertyChangeEventArgs)
          {
            if (paramAnonymous2PropertyChangeEventArgs.getNewValue() == CameraGallery.UIState.OPENED) {
              TouchFocusExposureUI.-wrap2(TouchFocusExposureUI.this);
            }
          }
        });
      }
    });
    updateDistanceThresholds(getScreenSize());
  }
  
  public Handle touchAutoExposure(float paramFloat1, float paramFloat2)
  {
    triggerFocusExposure(TriggerType.EXPOSURE, paramFloat1, paramFloat2);
    new Handle("Touch exposure")
    {
      protected void onClose(int paramAnonymousInt) {}
    };
  }
  
  public Handle touchAutoExposure(PointF paramPointF)
  {
    return touchAutoExposure(paramPointF.x, paramPointF.y);
  }
  
  public Handle touchAutoFocus(float paramFloat1, float paramFloat2)
  {
    triggerFocusExposure(TriggerType.FOCUS, paramFloat1, paramFloat2);
    return this.m_TouchAfHandle;
  }
  
  public Handle touchAutoFocus(PointF paramPointF)
  {
    return touchAutoExposure(paramPointF.x, paramPointF.y);
  }
  
  private static enum TriggerType
  {
    EXPOSURE,  FOCUS,  FOCUS_EXPOSURE_COMBINE;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/TouchFocusExposureUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */