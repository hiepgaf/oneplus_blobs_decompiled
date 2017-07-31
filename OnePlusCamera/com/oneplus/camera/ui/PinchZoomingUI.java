package com.oneplus.camera.ui;

import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.ScreenSize;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CameraComponent;
import com.oneplus.camera.PhotoCaptureState;
import com.oneplus.camera.VideoCaptureState;
import com.oneplus.camera.ZoomBar;
import com.oneplus.camera.ZoomController;

final class PinchZoomingUI
  extends CameraComponent
{
  private static final float MAX_PINCH_DISTANCE_RATIO = 0.6F;
  private CameraGallery m_CameraGallery;
  private ScaleGestureDetector m_GestureDetector;
  private float m_InitialDigitalZoom = 1.0F;
  private float m_InitialSpan;
  private boolean m_IsScaling;
  private float m_MaxPinchDistance;
  private final ScaleGestureDetector.OnScaleGestureListener m_ScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener()
  {
    public boolean onScale(ScaleGestureDetector paramAnonymousScaleGestureDetector)
    {
      PinchZoomingUI.-wrap0(PinchZoomingUI.this, paramAnonymousScaleGestureDetector.getCurrentSpan());
      return true;
    }
    
    public boolean onScaleBegin(ScaleGestureDetector paramAnonymousScaleGestureDetector)
    {
      PinchZoomingUI.-set1(PinchZoomingUI.this, paramAnonymousScaleGestureDetector.getCurrentSpan());
      PinchZoomingUI.-set0(PinchZoomingUI.this, ((Float)PinchZoomingUI.-get0(PinchZoomingUI.this).get(ZoomController.PROP_DIGITAL_ZOOM)).floatValue());
      PinchZoomingUI.-set2(PinchZoomingUI.this, true);
      return true;
    }
    
    public void onScaleEnd(ScaleGestureDetector paramAnonymousScaleGestureDetector)
    {
      PinchZoomingUI.-set2(PinchZoomingUI.this, false);
    }
  };
  private ZoomBar m_ZoomBar;
  private ZoomController m_ZoomController;
  
  PinchZoomingUI(CameraActivity paramCameraActivity)
  {
    super("Pinch Zooming UI", paramCameraActivity, true);
  }
  
  private void onScaleByGesture(float paramFloat)
  {
    float f1 = (paramFloat - this.m_InitialSpan) / this.m_MaxPinchDistance;
    float f2;
    if (f1 < -1.0F)
    {
      paramFloat = -1.0F;
      f1 = ((Float)this.m_ZoomController.get(ZoomController.PROP_MAX_DIGITAL_ZOOM)).floatValue();
      f2 = this.m_InitialDigitalZoom + (f1 - 1.0F) * paramFloat;
      if (f2 >= 1.0F) {
        break label128;
      }
      paramFloat = 1.0F;
    }
    for (;;)
    {
      this.m_ZoomController.set(ZoomController.PROP_DIGITAL_ZOOM, Float.valueOf(paramFloat));
      if (((paramFloat == 1.0F) || (Math.abs(paramFloat - f1) < 0.001F)) && (this.m_ZoomBar != null)) {
        this.m_ZoomBar.setZoomBarVisibility(true, 0);
      }
      return;
      paramFloat = f1;
      if (f1 <= 1.0F) {
        break;
      }
      paramFloat = 1.0F;
      break;
      label128:
      paramFloat = f2;
      if (f2 > f1) {
        paramFloat = f1;
      }
    }
  }
  
  private void onTouch(MotionEventArgs paramMotionEventArgs)
  {
    if (paramMotionEventArgs.isHandled()) {
      return;
    }
    Object localObject2 = getCameraActivity();
    Object localObject1 = (PhotoCaptureState)((CameraActivity)localObject2).get(CameraActivity.PROP_PHOTO_CAPTURE_STATE);
    localObject2 = (VideoCaptureState)((CameraActivity)localObject2).get(CameraActivity.PROP_VIDEO_CAPTURE_STATE);
    if ((localObject1 != PhotoCaptureState.READY) && (localObject2 != VideoCaptureState.READY) && (localObject2 != VideoCaptureState.CAPTURING)) {
      return;
    }
    if (this.m_CameraGallery != null) {
      switch (-getcom-oneplus-camera-ui-CameraGallery$UIStateSwitchesValues()[((CameraGallery.UIState)this.m_CameraGallery.get(CameraGallery.PROP_UI_STATE)).ordinal()])
      {
      default: 
        return;
      }
    }
    if ((this.m_ZoomController == null) || (!((Boolean)this.m_ZoomController.get(ZoomController.PROP_IS_DIGITAL_ZOOM_SUPPORTED)).booleanValue()) || (((Boolean)this.m_ZoomController.get(ZoomController.PROP_IS_ZOOM_LOCKED)).booleanValue())) {
      return;
    }
    localObject1 = paramMotionEventArgs.getMotionEvent();
    if ((localObject1 == null) || ((((MotionEvent)localObject1).getAction() != 1) && (((MotionEvent)localObject1).getPointerCount() == 1))) {
      return;
    }
    this.m_GestureDetector.onTouchEvent((MotionEvent)localObject1);
    if (this.m_IsScaling) {
      paramMotionEventArgs.setHandled();
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_ZoomBar = ((ZoomBar)findComponent(ZoomBar.class));
    this.m_ZoomController = ((ZoomController)findComponent(ZoomController.class));
    this.m_CameraGallery = ((CameraGallery)findComponent(CameraGallery.class));
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_GestureDetector = new ScaleGestureDetector(localCameraActivity, this.m_ScaleGestureListener, getHandler());
    this.m_GestureDetector.setQuickScaleEnabled(false);
    ScreenSize localScreenSize = getScreenSize();
    this.m_MaxPinchDistance = (Math.min(localScreenSize.getWidth(), localScreenSize.getHeight()) * 0.6F);
    localCameraActivity.addHandler(CameraActivity.EVENT_TOUCH, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MotionEventArgs> paramAnonymousEventKey, MotionEventArgs paramAnonymousMotionEventArgs)
      {
        PinchZoomingUI.-wrap1(PinchZoomingUI.this, paramAnonymousMotionEventArgs);
      }
    });
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/PinchZoomingUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */