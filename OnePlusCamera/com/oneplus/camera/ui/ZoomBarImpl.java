package com.oneplus.camera.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.oneplus.base.EventArgs;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.OPCameraActivity;
import com.oneplus.camera.PhotoCaptureState;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.VideoCaptureState;
import com.oneplus.camera.ZoomBar;
import com.oneplus.camera.ZoomController;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.drawable.ExpandableBackgroundDrawable;
import com.oneplus.camera.manual.ManualModeUI;
import com.oneplus.camera.media.MediaType;
import com.oneplus.widget.Wheel;
import com.oneplus.widget.Wheel.Callback;
import com.oneplus.widget.Wheel.WheelDrawable;

final class ZoomBarImpl
  extends UIComponent
  implements ZoomBar
{
  private static final long DURATION_HIDE_ZOOM_WHEEL = 200L;
  private static final long DURATION_SHOW_ZOOM_WHEEL = 200L;
  private static final long DURATION_ZOOM_WHEEL_VISIBLE = 1000L;
  private static final int MSG_HIDE_ZOOM_WHEEL = 10001;
  private static final float THRESHOLD_DISTANCE_TO_OPEN_ZOOM_WHEEL = 100.0F;
  private static final int ZOOM_STEP = 100;
  private CaptureModeManager m_CaptureModeManager;
  private boolean m_DropZoomBarTouchEvent;
  private AnimationDrawable m_HideZoomWheelAnimationDrawable;
  private boolean m_IsWheelTouchDownSet = false;
  private boolean m_IsZoomValueVisible;
  private boolean m_IsZoomWheelVisible;
  private MotionEvent m_LastTouchEvent;
  private ManualModeUI m_ManualModeUi;
  private AnimationDrawable m_ShowZoomWheelAnimationDrawable;
  private final PointF m_TouchDownPosition = new PointF();
  private ExpandableBackgroundDrawable m_WheelContainerBackground;
  private ZoomController m_ZoomController;
  private RelativeLayout m_ZoomValueContainer;
  private ImageView m_ZoomValueContainerAnimationBackground;
  private TextView m_ZoomValueText;
  private Wheel m_ZoomWheel;
  private final Wheel.Callback m_ZoomWheelCallback = new Wheel.Callback()
  {
    public void onStartTrackingTouch(Wheel paramAnonymousWheel)
    {
      if (!ZoomBarImpl.-get3(ZoomBarImpl.this)) {
        ZoomBarImpl.-wrap5(ZoomBarImpl.this);
      }
      HandlerUtils.removeMessages(ZoomBarImpl.this, 10001);
    }
    
    public void onStopTrackingTouch(Wheel paramAnonymousWheel)
    {
      HandlerUtils.sendMessage(ZoomBarImpl.this, 10001, true, 1000L);
    }
    
    public void onValueChanged(Wheel paramAnonymousWheel, int paramAnonymousInt, boolean paramAnonymousBoolean)
    {
      ZoomBarImpl.-wrap3(ZoomBarImpl.this, paramAnonymousInt, paramAnonymousBoolean);
    }
  };
  private RelativeLayout m_ZoomWheelContainer;
  private ZoomWheelDrawable m_ZoomWheelDrawable;
  private TextView m_zoomValueXText;
  
  ZoomBarImpl(CameraActivity paramCameraActivity)
  {
    super("Zoom Bar", paramCameraActivity, true);
  }
  
  private int convertZoomValueFromDigitalToWheel(float paramFloat)
  {
    return (int)((paramFloat - 1.0F) * 100.0F);
  }
  
  private float convertZoomValueFromWheelToDigital(int paramInt)
  {
    return paramInt * 1.0F / 100.0F + 1.0F;
  }
  
  private void hideZoomWheel(boolean paramBoolean)
  {
    if (!setReadOnly(PROP_IS_ZOOM_WHEEL_VISIBLE, Boolean.valueOf(false))) {
      return;
    }
    Object localObject;
    long l;
    if (this.m_IsWheelTouchDownSet)
    {
      this.m_DropZoomBarTouchEvent = true;
      localObject = this.m_ZoomWheelContainer;
      if (!paramBoolean) {
        break label233;
      }
      l = 200L;
      label42:
      setViewVisibility((View)localObject, false, l, null);
      this.m_WheelContainerBackground.collapse(this.m_ZoomWheelContainer.getWidth() / 2.0F, this.m_ZoomWheelContainer.getHeight() / 2.0F, paramBoolean);
      localObject = getCameraActivity().getResources();
      if (!paramBoolean) {
        break label252;
      }
      if (this.m_ShowZoomWheelAnimationDrawable != null) {
        break label239;
      }
      this.m_ZoomValueContainerAnimationBackground.setBackgroundResource(2130838303);
      this.m_HideZoomWheelAnimationDrawable = startIconAnimation(this.m_ZoomValueContainerAnimationBackground, new Runnable()
      {
        public void run()
        {
          if (!ZoomBarImpl.-get3(ZoomBarImpl.this))
          {
            ZoomBarImpl.-get9(ZoomBarImpl.this).setBackgroundResource(2130838280);
            ZoomBarImpl.-set1(ZoomBarImpl.this, null);
          }
        }
      });
      label130:
      this.m_ZoomValueContainer.animate().translationY(((Resources)localObject).getDimensionPixelSize(2131296540)).setDuration(200L).start();
    }
    for (;;)
    {
      this.m_ZoomValueText.setTextAppearance(2131492935);
      this.m_zoomValueXText.setTextAppearance(2131492935);
      int i = getContext().getResources().getDimensionPixelSize(2131296602);
      int j = getContext().getResources().getDimensionPixelSize(2131296601);
      this.m_zoomValueXText.setPadding(j, i, j, 0);
      HandlerUtils.removeMessages(this, 10001);
      return;
      this.m_IsWheelTouchDownSet = false;
      break;
      label233:
      l = 0L;
      break label42;
      label239:
      this.m_ZoomValueContainerAnimationBackground.setBackgroundResource(2130838280);
      break label130;
      label252:
      this.m_ZoomValueContainer.animate().cancel();
      this.m_ZoomValueContainer.setTranslationY(((Resources)localObject).getDimensionPixelSize(2131296540));
      this.m_ZoomValueContainerAnimationBackground.setBackgroundResource(2130838280);
    }
  }
  
  private boolean isDualCamera()
  {
    boolean bool2 = true;
    Camera localCamera = getCamera();
    boolean bool1 = bool2;
    if (localCamera != null)
    {
      bool1 = bool2;
      if (localCamera.get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT) {
        bool1 = false;
      }
    }
    return bool1;
  }
  
  private boolean isZoomAdjustable()
  {
    CameraActivity localCameraActivity = getCameraActivity();
    if (!((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_CAPTURE_UI_ENABLED)).booleanValue())
    {
      Log.v(this.TAG, "isZoomAdjustable() - Capture UI is disabled.");
      return false;
    }
    if ((localCameraActivity.get(CameraActivity.PROP_MEDIA_TYPE) == MediaType.PHOTO) && (localCameraActivity.get(CameraActivity.PROP_PHOTO_CAPTURE_STATE) != PhotoCaptureState.READY))
    {
      Log.v(this.TAG, "isZoomAdjustable() - Capture state is not ready.");
      return false;
    }
    return true;
  }
  
  private void onZoomWheelValueChanged(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      this.m_ZoomController.set(ZoomController.PROP_DIGITAL_ZOOM, Float.valueOf(convertZoomValueFromWheelToDigital(paramInt)));
    }
    if ((!this.m_IsZoomWheelVisible) || (this.m_IsWheelTouchDownSet)) {
      return;
    }
    HandlerUtils.sendMessage(this, 10001, true, 1000L);
  }
  
  private void setZoomValueVisibility(boolean paramBoolean)
  {
    if (paramBoolean) {
      setViewVisibility(this.m_ZoomValueContainer, true);
    }
    for (;;)
    {
      setReadOnly(PROP_IS_ZOOM_VALUE_VISIBLE, Boolean.valueOf(paramBoolean));
      return;
      setViewVisibility(this.m_ZoomValueContainer, false);
    }
  }
  
  private void showZoomWheel()
  {
    if (!setReadOnly(PROP_IS_ZOOM_WHEEL_VISIBLE, Boolean.valueOf(true))) {
      return;
    }
    setViewVisibility(this.m_ZoomWheelContainer, true, 200L, null);
    if (!((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_SIMPLE_UI_MODE_ENTERED)).booleanValue()) {
      this.m_WheelContainerBackground.expand(this.m_ZoomWheelContainer.getWidth() / 2.0F, this.m_ZoomWheelContainer.getHeight() / 2.0F, true);
    }
    if (this.m_HideZoomWheelAnimationDrawable == null)
    {
      this.m_ZoomValueContainerAnimationBackground.setBackgroundResource(2130838304);
      this.m_ShowZoomWheelAnimationDrawable = startIconAnimation(this.m_ZoomValueContainerAnimationBackground, new Runnable()
      {
        public void run()
        {
          if (ZoomBarImpl.-get3(ZoomBarImpl.this))
          {
            ZoomBarImpl.-get9(ZoomBarImpl.this).setBackgroundResource(2130838302);
            ZoomBarImpl.-set4(ZoomBarImpl.this, null);
          }
        }
      });
    }
    for (;;)
    {
      this.m_ZoomValueContainer.animate().translationY(0.0F).setDuration(200L).start();
      this.m_ZoomValueText.setTextAppearance(2131492934);
      this.m_zoomValueXText.setTextAppearance(2131492934);
      int i = getContext().getResources().getDimensionPixelSize(2131296600);
      int j = getContext().getResources().getDimensionPixelSize(2131296599);
      this.m_zoomValueXText.setPadding(j, i, j, 0);
      HandlerUtils.sendMessage(this, 10001, true, 1000L);
      return;
      this.m_ZoomValueContainerAnimationBackground.setBackgroundResource(2130838302);
    }
  }
  
  private AnimationDrawable startIconAnimation(final ImageView paramImageView, final Runnable paramRunnable)
  {
    if (paramImageView == null)
    {
      Log.w(this.TAG, "startIconAnimation() - View is null.");
      return null;
    }
    if (!(paramImageView.getBackground() instanceof AnimationDrawable)) {
      return null;
    }
    final AnimationDrawable localAnimationDrawable = (AnimationDrawable)paramImageView.getBackground();
    if (localAnimationDrawable.isRunning()) {
      localAnimationDrawable.stop();
    }
    localAnimationDrawable.start();
    localAnimationDrawable.setOneShot(true);
    int i = localAnimationDrawable.getNumberOfFrames();
    int j = localAnimationDrawable.getDuration(0);
    getHandler().postDelayed(new Runnable()
    {
      public void run()
      {
        localAnimationDrawable.stop();
        paramImageView.invalidate();
        if (paramRunnable != null) {
          paramRunnable.run();
        }
      }
    }, i * j);
    return localAnimationDrawable;
  }
  
  private void updateValueText(float paramFloat)
  {
    if (Math.abs(paramFloat - Math.round(paramFloat)) < 0.05F)
    {
      this.m_ZoomValueText.setText(String.format("%d", new Object[] { Integer.valueOf(Math.round(paramFloat)) }));
      return;
    }
    this.m_ZoomValueText.setText(String.format("%.1f", new Object[] { Float.valueOf(paramFloat) }));
  }
  
  private void updateZoomContainerVisibility()
  {
    CameraActivity localCameraActivity = getCameraActivity();
    if ((this.m_ManualModeUi != null) && (((Boolean)this.m_ManualModeUi.get(ManualModeUI.PROP_IS_KNOB_VIEW_VISIBLE)).booleanValue()))
    {
      hideZoomWheel(false);
      setZoomValueVisibility(false);
      return;
    }
    if (((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_SELF_TIMER_STARTED)).booleanValue())
    {
      hideZoomWheel(false);
      setZoomValueVisibility(false);
      return;
    }
    if (localCameraActivity.get(CameraActivity.PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.REVIEWING)
    {
      hideZoomWheel(false);
      setZoomValueVisibility(false);
      return;
    }
    if ((this.m_ZoomController == null) || (!((Boolean)this.m_ZoomController.get(ZoomController.PROP_IS_DIGITAL_ZOOM_SUPPORTED)).booleanValue()) || (((Boolean)this.m_ZoomController.get(ZoomController.PROP_IS_ZOOM_LOCKED)).booleanValue()))
    {
      hideZoomWheel(false);
      setZoomValueVisibility(false);
      return;
    }
    if (!isDualCamera())
    {
      hideZoomWheel(false);
      setZoomValueVisibility(false);
      return;
    }
    hideZoomWheel(true);
    setZoomValueVisibility(true);
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_IS_ZOOM_VALUE_VISIBLE) {
      return Boolean.valueOf(this.m_IsZoomValueVisible);
    }
    if (paramPropertyKey == PROP_IS_ZOOM_WHEEL_VISIBLE) {
      return Boolean.valueOf(this.m_IsZoomWheelVisible);
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    hideZoomWheel(true);
    setZoomValueVisibility(true);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_ZoomController = ((ZoomController)findComponent(ZoomController.class));
    OPCameraActivity localOPCameraActivity = (OPCameraActivity)getCameraActivity();
    this.m_WheelContainerBackground = new ExpandableBackgroundDrawable(localOPCameraActivity.getColor(2131230803));
    this.m_ZoomWheelDrawable = new ZoomWheelDrawable(localOPCameraActivity.getResources(), 0);
    ViewGroup localViewGroup = localOPCameraActivity.getCaptureUIContainer();
    this.m_ZoomValueContainer = ((RelativeLayout)localViewGroup.findViewById(2131362099));
    this.m_ZoomValueContainerAnimationBackground = ((ImageView)this.m_ZoomValueContainer.findViewById(2131362101));
    this.m_ZoomValueContainerAnimationBackground.setBackgroundResource(2130838280);
    this.m_ZoomWheelContainer = ((RelativeLayout)localViewGroup.findViewById(2131362100));
    this.m_ZoomWheelContainer.setBackground(this.m_WheelContainerBackground);
    this.m_ZoomWheel = ((Wheel)this.m_ZoomWheelContainer.findViewById(2131362104));
    this.m_ZoomWheel.setCurrentValueIndicatorDrawable(new ColorDrawable(0));
    this.m_ZoomWheel.setWheelDrawable(this.m_ZoomWheelDrawable);
    this.m_ZoomWheel.setWheelLengthRatio(0.8F);
    this.m_ZoomValueText = ((TextView)this.m_ZoomValueContainer.findViewById(2131362102));
    this.m_zoomValueXText = ((TextView)this.m_ZoomValueContainer.findViewById(2131362103));
    this.m_ZoomValueContainer.setSoundEffectsEnabled(false);
    this.m_ZoomValueContainer.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if (!ZoomBarImpl.-wrap0(ZoomBarImpl.this)) {
          return;
        }
        if (((Float)ZoomBarImpl.-get7(ZoomBarImpl.this).get(ZoomController.PROP_DIGITAL_ZOOM)).floatValue() == 1.0F) {
          ZoomBarImpl.-get7(ZoomBarImpl.this).set(ZoomController.PROP_DIGITAL_ZOOM, Float.valueOf(2.0F));
        }
        for (;;)
        {
          ZoomBarImpl.-wrap4(ZoomBarImpl.this, ZoomBarImpl.EVENT_ZOOM_VALUE_CLICK, EventArgs.EMPTY);
          return;
          ZoomBarImpl.-get7(ZoomBarImpl.this).set(ZoomController.PROP_DIGITAL_ZOOM, Float.valueOf(1.0F));
        }
      }
    });
    this.m_ZoomValueContainer.setOnLongClickListener(new View.OnLongClickListener()
    {
      public boolean onLongClick(View paramAnonymousView)
      {
        if (!ZoomBarImpl.-wrap0(ZoomBarImpl.this)) {
          return false;
        }
        Log.v(ZoomBarImpl.-get0(ZoomBarImpl.this), "onLongClick() - m_ZoomValueContainer");
        ZoomBarImpl.-wrap5(ZoomBarImpl.this);
        ZoomBarImpl.-wrap4(ZoomBarImpl.this, ZoomBarImpl.EVENT_ZOOM_VALUE_LONG_CLICK, EventArgs.EMPTY);
        if ((ZoomBarImpl.-get4(ZoomBarImpl.this) != null) && (!ZoomBarImpl.-get2(ZoomBarImpl.this)))
        {
          ZoomBarImpl.-get10(ZoomBarImpl.this).onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, ZoomBarImpl.-get4(ZoomBarImpl.this).getX(), ZoomBarImpl.-get4(ZoomBarImpl.this).getY(), 0));
          ZoomBarImpl.-set2(ZoomBarImpl.this, true);
        }
        return true;
      }
    });
    this.m_ZoomValueContainer.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        if (!ZoomBarImpl.-wrap0(ZoomBarImpl.this)) {
          return false;
        }
        ZoomBarImpl.-set3(ZoomBarImpl.this, paramAnonymousMotionEvent);
        float f1 = paramAnonymousMotionEvent.getX();
        float f2 = paramAnonymousMotionEvent.getY();
        switch (paramAnonymousMotionEvent.getAction())
        {
        }
        while (ZoomBarImpl.-get3(ZoomBarImpl.this))
        {
          if (!ZoomBarImpl.-get2(ZoomBarImpl.this))
          {
            ZoomBarImpl.-get10(ZoomBarImpl.this).onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 0, paramAnonymousMotionEvent.getX(), paramAnonymousMotionEvent.getY(), 0));
            ZoomBarImpl.-set2(ZoomBarImpl.this, true);
          }
          ZoomBarImpl.-get10(ZoomBarImpl.this).onTouchEvent(paramAnonymousMotionEvent);
          return true;
          ZoomBarImpl.-get5(ZoomBarImpl.this).set(f1, f2);
          ZoomBarImpl.-set0(ZoomBarImpl.this, false);
          continue;
          if (ZoomBarImpl.-get1(ZoomBarImpl.this)) {
            return false;
          }
          if ((!ZoomBarImpl.-get3(ZoomBarImpl.this)) && (Math.abs(f1 - ZoomBarImpl.-get5(ZoomBarImpl.this).x) >= 100.0F))
          {
            ZoomBarImpl.-wrap5(ZoomBarImpl.this);
            ZoomBarImpl.-wrap4(ZoomBarImpl.this, ZoomBarImpl.EVENT_ZOOM_VALUE_DRAGED, EventArgs.EMPTY);
            continue;
            ZoomBarImpl.-set2(ZoomBarImpl.this, false);
            if (ZoomBarImpl.-get3(ZoomBarImpl.this)) {
              HandlerUtils.sendMessage(ZoomBarImpl.this, 10001, true, 1000L);
            }
            ZoomBarImpl.-set0(ZoomBarImpl.this, false);
            ZoomBarImpl.-set3(ZoomBarImpl.this, null);
          }
        }
        ZoomBarImpl.-set2(ZoomBarImpl.this, false);
        return false;
      }
    });
    this.m_ZoomWheel.addCallback(this.m_ZoomWheelCallback);
    addAutoRotateView((View)this.m_ZoomValueText.getParent());
    localOPCameraActivity.addCallback(CameraActivity.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        ZoomBarImpl.-wrap7(ZoomBarImpl.this);
      }
    });
    localOPCameraActivity.addCallback(CameraActivity.PROP_PHOTO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PhotoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<PhotoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        ZoomBarImpl.-wrap2(ZoomBarImpl.this, true);
      }
    });
    localOPCameraActivity.addCallback(CameraActivity.PROP_IS_SELF_TIMER_STARTED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        ZoomBarImpl.-wrap7(ZoomBarImpl.this);
      }
    });
    localOPCameraActivity.addCallback(CameraActivity.PROP_MEDIA_TYPE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<MediaType> paramAnonymousPropertyKey, PropertyChangeEventArgs<MediaType> paramAnonymousPropertyChangeEventArgs)
      {
        ZoomBarImpl.-wrap7(ZoomBarImpl.this);
      }
    });
    localOPCameraActivity.addCallback(CameraActivity.PROP_VIDEO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<VideoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<VideoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        if (ZoomBarImpl.-get8(ZoomBarImpl.this) != null)
        {
          paramAnonymousPropertySource = ZoomBarImpl.-get8(ZoomBarImpl.this);
          if (paramAnonymousPropertyChangeEventArgs.getNewValue() == VideoCaptureState.CAPTURING) {
            break label53;
          }
        }
        label53:
        for (boolean bool = true;; bool = false)
        {
          paramAnonymousPropertySource.setHapticFeedbackEnabled(bool);
          ZoomBarImpl.-wrap2(ZoomBarImpl.this, true);
          ZoomBarImpl.-wrap7(ZoomBarImpl.this);
          return;
        }
      }
    });
    localOPCameraActivity.addCallback(CameraActivity.PROP_IS_SIMPLE_UI_MODE_ENTERED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if ((ZoomBarImpl.-get11(ZoomBarImpl.this) == null) || (ZoomBarImpl.-get6(ZoomBarImpl.this) == null)) {
          return;
        }
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          ZoomBarImpl.-get6(ZoomBarImpl.this).collapse(ZoomBarImpl.-get11(ZoomBarImpl.this).getWidth() / 2.0F, ZoomBarImpl.-get11(ZoomBarImpl.this).getHeight() / 2.0F, true);
        }
        while (!ZoomBarImpl.-get3(ZoomBarImpl.this)) {
          return;
        }
        ZoomBarImpl.-get6(ZoomBarImpl.this).expand(ZoomBarImpl.-get11(ZoomBarImpl.this).getWidth() / 2.0F, ZoomBarImpl.-get11(ZoomBarImpl.this).getHeight() / 2.0F, false);
      }
    });
    if (this.m_ZoomController != null)
    {
      this.m_ZoomController.addCallback(ZoomController.PROP_DIGITAL_ZOOM, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Float> paramAnonymousPropertyKey, PropertyChangeEventArgs<Float> paramAnonymousPropertyChangeEventArgs)
        {
          ZoomBarImpl.-wrap6(ZoomBarImpl.this, ((Float)paramAnonymousPropertyChangeEventArgs.getNewValue()).floatValue());
          ZoomBarImpl.-get10(ZoomBarImpl.this).setValue(ZoomBarImpl.-wrap1(ZoomBarImpl.this, ((Float)paramAnonymousPropertyChangeEventArgs.getNewValue()).floatValue()));
        }
      });
      this.m_ZoomController.addCallback(ZoomController.PROP_IS_DIGITAL_ZOOM_SUPPORTED, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          ZoomBarImpl.-wrap7(ZoomBarImpl.this);
        }
      });
      this.m_ZoomController.addCallback(ZoomController.PROP_IS_ZOOM_LOCKED, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          ZoomBarImpl.-wrap7(ZoomBarImpl.this);
        }
      });
      this.m_ZoomController.addCallback(ZoomController.PROP_MAX_DIGITAL_ZOOM, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Float> paramAnonymousPropertyKey, PropertyChangeEventArgs<Float> paramAnonymousPropertyChangeEventArgs)
        {
          float f = ((Float)paramAnonymousPropertyChangeEventArgs.getNewValue()).floatValue();
          paramAnonymousPropertySource = ZoomBarImpl.-get12(ZoomBarImpl.this);
          if (f >= 1.0F) {}
          for (int i = Math.round(f - 1.0F);; i = 1)
          {
            paramAnonymousPropertySource.setSegmentCount(i);
            ZoomBarImpl.-get10(ZoomBarImpl.this).setMaxValue(ZoomBarImpl.-wrap1(ZoomBarImpl.this, f));
            ZoomBarImpl.-get10(ZoomBarImpl.this).setValue(ZoomBarImpl.-wrap1(ZoomBarImpl.this, ((Float)ZoomBarImpl.-get7(ZoomBarImpl.this).get(ZoomController.PROP_DIGITAL_ZOOM)).floatValue()));
            ZoomBarImpl.-get10(ZoomBarImpl.this).setCenterValue(0);
            return;
          }
        }
      });
      updateValueText(((Float)this.m_ZoomController.get(ZoomController.PROP_DIGITAL_ZOOM)).floatValue());
    }
    this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    if (this.m_CaptureModeManager != null) {
      this.m_CaptureModeManager.addCallback(CaptureModeManager.PROP_CAPTURE_MODE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CaptureMode> paramAnonymousPropertyKey, PropertyChangeEventArgs<CaptureMode> paramAnonymousPropertyChangeEventArgs)
        {
          ZoomBarImpl.-wrap7(ZoomBarImpl.this);
        }
      });
    }
    this.m_ManualModeUi = ((ManualModeUI)findComponent(ManualModeUI.class));
    if (this.m_ManualModeUi != null) {
      this.m_ManualModeUi.addCallback(ManualModeUI.PROP_IS_KNOB_VIEW_VISIBLE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          ZoomBarImpl.-wrap7(ZoomBarImpl.this);
        }
      });
    }
    updateZoomContainerVisibility();
  }
  
  protected <TValue> boolean setReadOnly(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_IS_ZOOM_VALUE_VISIBLE) {
      this.m_IsZoomValueVisible = ((Boolean)paramTValue).booleanValue();
    }
    for (;;)
    {
      return super.setReadOnly(paramPropertyKey, paramTValue);
      if (paramPropertyKey == PROP_IS_ZOOM_WHEEL_VISIBLE) {
        this.m_IsZoomWheelVisible = ((Boolean)paramTValue).booleanValue();
      }
    }
  }
  
  public Handle setZoomBarVisibility(boolean paramBoolean, int paramInt)
  {
    new Handle("Set Zoom Bar Visibility Handle")
    {
      protected void onClose(int paramAnonymousInt) {}
    };
  }
  
  private static final class BaseProgressDrawable
    extends Drawable
  {
    private final Paint m_Paint = new Paint();
    private final float m_Thickness;
    
    public BaseProgressDrawable(Context paramContext, int paramInt)
    {
      this.m_Paint.setStyle(Paint.Style.FILL);
      this.m_Paint.setColor(paramInt);
      this.m_Thickness = paramContext.getResources().getDimensionPixelSize(2131296538);
    }
    
    public void draw(Canvas paramCanvas)
    {
      Rect localRect = getBounds();
      float f = localRect.top + (localRect.height() - this.m_Thickness) / 2.0F;
      paramCanvas.drawRect(localRect.left, f, localRect.right, f + this.m_Thickness, this.m_Paint);
    }
    
    public int getOpacity()
    {
      return 255;
    }
    
    public void setAlpha(int paramInt) {}
    
    public void setColorFilter(ColorFilter paramColorFilter) {}
  }
  
  private static final class ProgressDrawable
    extends LayerDrawable
  {
    public ProgressDrawable(Context paramContext)
    {
      super();
      setId(0, 16908288);
      setId(1, 16908301);
    }
  }
  
  public static class ZoomWheelDrawable
    extends Wheel.WheelDrawable
  {
    private static final int TICK_COUNT = 90;
    private static final int WHEEL_SEGMENTS = 6;
    private int m_SegmentCount = 6;
    private int m_TickCount = 90;
    
    public ZoomWheelDrawable(Resources paramResources, int paramInt)
    {
      super(paramInt);
    }
    
    public void draw(Canvas paramCanvas)
    {
      Rect localRect = getBounds();
      switch (this.m_Orientation)
      {
      }
      do
      {
        return;
        i = localRect.height() - this.m_Padding - this.m_Padding;
      } while (i <= 0);
      int m = this.m_TickCount / this.m_SegmentCount;
      int i = localRect.top;
      i = this.m_Padding;
      this.m_TickPaint.setColor(-1);
      int k = 0;
      label86:
      int n;
      int j;
      if (k <= this.m_TickCount)
      {
        n = Math.round(localRect.left + localRect.width() * (k / this.m_TickCount));
        if ((k != 0) && (k != this.m_TickCount)) {
          break label194;
        }
        i = localRect.height() - this.m_Padding - this.m_Padding;
        j = (localRect.height() - i) / 2;
        i = j + i;
      }
      for (;;)
      {
        paramCanvas.drawLine(n, j, n, i, this.m_TickPaint);
        k += 1;
        break label86;
        break;
        label194:
        if (k % m == 0)
        {
          i = (int)((localRect.height() - this.m_Padding - this.m_Padding) * 2.5D / 4.0D);
          j = (localRect.height() - i) / 2;
          i = j + i;
        }
        else
        {
          i = (localRect.height() - this.m_Padding - this.m_Padding) / 4;
          j = (localRect.height() - i) / 2;
          i = j + i;
        }
      }
    }
    
    public void setSegmentCount(int paramInt)
    {
      if (this.m_SegmentCount != paramInt)
      {
        this.m_SegmentCount = paramInt;
        this.m_TickCount = ((int)Math.ceil(90.0D / paramInt) * paramInt);
        invalidateSelf();
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ZoomBarImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */