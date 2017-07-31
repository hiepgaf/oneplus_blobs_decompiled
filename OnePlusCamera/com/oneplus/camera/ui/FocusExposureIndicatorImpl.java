package com.oneplus.camera.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Message;
import android.util.Range;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.camera.Camera.Face;
import com.oneplus.camera.Camera.MeteringRect;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.ExposureController;
import com.oneplus.camera.FaceTracker;
import com.oneplus.camera.FocusController;
import com.oneplus.camera.FocusMode;
import com.oneplus.camera.FocusState;
import com.oneplus.camera.OPCameraActivity;
import com.oneplus.camera.PhotoCaptureState;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.VideoCaptureState;
import com.oneplus.widget.VerticalSeekBar;
import com.oneplus.widget.ViewUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

final class FocusExposureIndicatorImpl
  extends UIComponent
  implements FocusExposureIndicator
{
  private static final long DELAY_SHOW_FOCUS_INDICATOR = 50L;
  private static final long DURATION_EXPOSURE_COMP_ICON_ROTATION = 100L;
  private static final long DURATION_FOCUS_INDICATOR_SHOW = 300L;
  private static final long DURATION_FOCUS_INDICATOR_VISIBLE_LONG = 3000L;
  private static final long DURATION_FOCUS_INDICATOR_VISIBLE_NORMAL = 1000L;
  private static final long DURATION_UI_ROTATION = 700L;
  private static final int EXPOSURE_BAR_HALF_STEPS = 1000;
  private static final int EXPOSURE_BAR_STEPS = 2000;
  private static final int MSG_HIDE_FOCUS_INDICATOR = 10001;
  private static final int MSG_SHOW_FOCUS_INDICATOR = 10000;
  private static final int MSG_UI_ROTATING = 10005;
  private static final int THRESHOLD_SEPARATE_INDICATORS = 50;
  private TextView m_AeAfLockText;
  private Camera.MeteringRect m_AeRegion;
  private Camera.MeteringRect m_AfRegion;
  private PointF m_CenterPoint;
  private ExposureController m_ExposureController;
  private ImageView m_ExposureIndicator;
  private View m_ExposureIndicatorContainer;
  private TextView m_ExposureIndicatorText;
  private VerticalSeekBar m_ExposureSeekBar;
  private RelativeLayout m_ExposureSeekBarContainer;
  private FaceTracker m_FaceTracker;
  private FocusController m_FocusController;
  private ImageView m_FocusIndicator;
  private ImageView m_FocusIndicatorCenter;
  private TextView m_FocusIndicatorText;
  private FocusExposureRegionDrawable m_FocusLockedDrawable;
  private FocusExposureRegionDrawable m_FocusingDrawable;
  private GestureDetector m_GestureDetector;
  private BaseGestureHandler m_GestureHandler = new BaseGestureHandler()
  {
    public boolean onScroll(MotionEvent paramAnonymousMotionEvent1, MotionEvent paramAnonymousMotionEvent2, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      if (FocusExposureIndicatorImpl.-get4(FocusExposureIndicatorImpl.this) != FocusExposureIndicatorImpl.IndicatorState.VISIBLE) {
        return super.onScroll(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, paramAnonymousFloat1, paramAnonymousFloat2);
      }
      if (paramAnonymousMotionEvent2.getPointerCount() > 1) {
        return super.onScroll(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, paramAnonymousFloat1, paramAnonymousFloat2);
      }
      switch (-getcom-oneplus-base-RotationSwitchesValues()[FocusExposureIndicatorImpl.-wrap2(FocusExposureIndicatorImpl.this).ordinal()])
      {
      }
      while (!((Boolean)FocusExposureIndicatorImpl.this.getCameraActivity().get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue())
      {
        return super.onScroll(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, paramAnonymousFloat1, paramAnonymousFloat2);
        if (paramAnonymousFloat1 != 0.0F) {}
        for (float f = paramAnonymousFloat2 / paramAnonymousFloat1; Math.abs(f) < 0.84F; f = Float.MAX_VALUE) {
          return super.onScroll(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, paramAnonymousFloat1, paramAnonymousFloat2);
        }
      }
      if (FocusExposureIndicatorImpl.-get1(FocusExposureIndicatorImpl.this) == null) {}
      for (int i = 1; i != 0; i = 0) {
        return super.onScroll(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, paramAnonymousFloat1, paramAnonymousFloat2);
      }
      HandlerUtils.removeMessages(FocusExposureIndicatorImpl.this, 10001);
      ScreenSize localScreenSize = FocusExposureIndicatorImpl.-wrap3(FocusExposureIndicatorImpl.this);
      switch (-getcom-oneplus-base-RotationSwitchesValues()[FocusExposureIndicatorImpl.-wrap2(FocusExposureIndicatorImpl.this).ordinal()])
      {
      default: 
        Log.w(FocusExposureIndicatorImpl.-get0(FocusExposureIndicatorImpl.this), "onScroll() - Unknown rotation.");
        return super.onScroll(paramAnonymousMotionEvent1, paramAnonymousMotionEvent2, paramAnonymousFloat1, paramAnonymousFloat2);
      }
      for (i = Math.max(localScreenSize.getHeight(), localScreenSize.getWidth());; i = Math.min(localScreenSize.getHeight(), localScreenSize.getWidth()))
      {
        paramAnonymousFloat1 = paramAnonymousFloat2 / i;
        FocusExposureIndicatorImpl.-wrap16(FocusExposureIndicatorImpl.this, paramAnonymousFloat1 * 2.0F);
        return true;
      }
    }
  };
  private View m_IndicatorBaseView;
  private final Point m_IndicatorCenterPointInWindow = new Point();
  private View m_IndicatorContainer;
  private int m_IndicatorContainerHeight;
  private int m_IndicatorContainerWidth;
  private IndicatorState m_IndicatorState = IndicatorState.INVISIBLE;
  private boolean m_IsTouchingIndicator;
  private boolean m_LockExposureIndicator;
  private float m_RelativeExposureComp;
  private PointF m_TempInitPoint = new PointF();
  private final int[] m_TempLocationBuffer = new int[2];
  private PointF m_TempTargetInitPoint = new PointF();
  private TouchAutoExposureUI m_TouchAutoExposureUI;
  private TouchAutoFocusUI m_TouchAutoFocusUI;
  private Viewfinder m_Viewfinder;
  
  FocusExposureIndicatorImpl(CameraActivity paramCameraActivity)
  {
    super("Focus/Exposure indicator", paramCameraActivity, true);
  }
  
  private void changeIndicatorState(IndicatorState paramIndicatorState)
  {
    if (this.m_IndicatorState == paramIndicatorState) {
      return;
    }
    int i = 0;
    switch (-getcom-oneplus-camera-ui-FocusExposureIndicatorImpl$IndicatorStateSwitchesValues()[this.m_IndicatorState.ordinal()])
    {
    default: 
      if (i != 0) {
        Log.e(this.TAG, "changeIndicatorState() - Abnormal state change : " + this.m_IndicatorState + " -> " + paramIndicatorState);
      }
      Log.v(this.TAG, "changeIndicatorState() - State: ", paramIndicatorState);
      if (paramIndicatorState == IndicatorState.VISIBLE_SEPARATED) {
        setReadOnly(PROP_IS_FOCUS_EXPOSURE_SEPARATED, Boolean.valueOf(true));
      }
      break;
    }
    for (;;)
    {
      this.m_IndicatorState = paramIndicatorState;
      return;
      if (paramIndicatorState == IndicatorState.VISIBLE) {
        break;
      }
      i = 1;
      break;
      if (paramIndicatorState == IndicatorState.VISIBLE_SEPARATED) {
        break;
      }
      i = 1;
      break;
      if (paramIndicatorState == IndicatorState.VISIBLE_IN_ANIMATING) {
        break;
      }
      i = 1;
      break;
      switch (-getcom-oneplus-camera-ui-FocusExposureIndicatorImpl$IndicatorStateSwitchesValues()[paramIndicatorState.ordinal()])
      {
      }
      i = 1;
      break;
      switch (-getcom-oneplus-camera-ui-FocusExposureIndicatorImpl$IndicatorStateSwitchesValues()[paramIndicatorState.ordinal()])
      {
      }
      i = 1;
      break;
      switch (-getcom-oneplus-camera-ui-FocusExposureIndicatorImpl$IndicatorStateSwitchesValues()[paramIndicatorState.ordinal()])
      {
      }
      i = 1;
      break;
      setReadOnly(PROP_IS_FOCUS_EXPOSURE_SEPARATED, Boolean.valueOf(false));
    }
  }
  
  private void hideFocusIndicator()
  {
    switch (-getcom-oneplus-camera-ui-FocusExposureIndicatorImpl$IndicatorStateSwitchesValues()[this.m_IndicatorState.ordinal()])
    {
    default: 
      if (this.m_IsTouchingIndicator)
      {
        Log.v(this.TAG, "hideFocusIndicator() - Touching indicator, skip");
        return;
      }
      break;
    case 3: 
      return;
    }
    Log.v(this.TAG, "hideFocusIndicator() - Hide");
    changeIndicatorState(IndicatorState.INVISIBLE);
    if (!isExposureLocked()) {
      setRelativeExposureComp(0.0F, true, false);
    }
    HandlerUtils.removeMessages(this, 10000);
    HandlerUtils.removeMessages(this, 10001);
    if (this.m_IndicatorContainer != null) {
      this.m_IndicatorContainer.clearAnimation();
    }
    setViewVisibility(this.m_IndicatorContainer, false);
    setViewVisibility(this.m_FocusIndicatorText, false);
    if (((Boolean)get(PROP_CAN_FOCUS_EXPOSURE_SEPARATED)).booleanValue())
    {
      if (this.m_ExposureIndicatorContainer != null) {
        this.m_ExposureIndicatorContainer.clearAnimation();
      }
      setViewVisibility(this.m_ExposureIndicatorContainer, false);
      setViewVisibility(this.m_ExposureIndicatorText, false);
    }
  }
  
  private void hideFocusIndicatorDelayed()
  {
    if ((isFocusExposureLocked()) && (((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue())) {
      return;
    }
    if (this.m_FocusController != null)
    {
      long l;
      if ((this.m_FocusController.get(FocusController.PROP_FOCUS_STATE) != FocusState.SCANNING) && (this.m_FocusController.get(FocusController.PROP_FOCUS_STATE) != FocusState.STARTING))
      {
        l = 1000L;
        switch (-getcom-oneplus-camera-ui-FocusExposureIndicatorImpl$IndicatorStateSwitchesValues()[this.m_IndicatorState.ordinal()])
        {
        }
      }
      for (;;)
      {
        HandlerUtils.sendMessage(this, 10001, true, l);
        return;
        l = 3000L;
      }
    }
    hideFocusIndicator();
  }
  
  private boolean isExposureLocked()
  {
    return (this.m_ExposureController != null) && (((Boolean)this.m_ExposureController.get(ExposureController.PROP_IS_AE_LOCKED)).booleanValue());
  }
  
  private boolean isFocusExposureAtSameRegion()
  {
    if ((this.m_AfRegion == null) && (this.m_AeRegion == null)) {
      return true;
    }
    return (this.m_AfRegion != null) && (this.m_AeRegion != null) && (Math.abs(this.m_AfRegion.getLeft() - this.m_AeRegion.getLeft()) < 0.01D) && (Math.abs(this.m_AfRegion.getTop() - this.m_AeRegion.getTop()) < 0.01D) && (Math.abs(this.m_AfRegion.getRight() - this.m_AeRegion.getRight()) < 0.01D) && (Math.abs(this.m_AfRegion.getBottom() - this.m_AeRegion.getBottom()) < 0.01D);
  }
  
  private boolean isFocusExposureLocked()
  {
    if (isFocusLocked()) {
      return isExposureLocked();
    }
    return false;
  }
  
  private boolean isFocusLocked()
  {
    return (this.m_FocusController != null) && (((Boolean)this.m_FocusController.get(FocusController.PROP_IS_FOCUS_LOCKED)).booleanValue());
  }
  
  private void lockExposureIndicator(boolean paramBoolean)
  {
    this.m_LockExposureIndicator = paramBoolean;
  }
  
  private void onAeRegionsChanged(List<Camera.MeteringRect> paramList)
  {
    if ((paramList == null) || (paramList.isEmpty()))
    {
      this.m_AeRegion = null;
      hideFocusIndicator();
    }
    for (;;)
    {
      Log.v(this.TAG, "onAeRegionsChanged() - AE region: ", this.m_AeRegion);
      return;
      this.m_AeRegion = ((Camera.MeteringRect)paramList.get(0));
    }
  }
  
  private void onAfRegionsChanged(List<Camera.MeteringRect> paramList)
  {
    if ((paramList == null) || (paramList.isEmpty()))
    {
      this.m_AfRegion = null;
      hideFocusIndicator();
    }
    for (;;)
    {
      Log.v(this.TAG, "onAfRegionsChanged() - AF region: ", this.m_AfRegion);
      return;
      this.m_AfRegion = ((Camera.MeteringRect)paramList.get(0));
    }
  }
  
  private void onExposureCompChanged(boolean paramBoolean)
  {
    if (this.m_IndicatorState == IndicatorState.DRAGING_EXPOSURE_COMP) {
      return;
    }
    float f = ((Float)this.m_ExposureController.get(ExposureController.PROP_EXPOSURE_COMPENSATION)).floatValue();
    Range localRange = (Range)this.m_ExposureController.get(ExposureController.PROP_EXPOSURE_COMPENSATION_RANGE);
    if (f >= 0.0F) {}
    for (f /= ((Float)localRange.getUpper()).floatValue();; f = -f / ((Float)localRange.getLower()).floatValue())
    {
      this.m_RelativeExposureComp = f;
      if (Math.abs(((Float)localRange.getUpper()).floatValue() - ((Float)localRange.getLower()).floatValue()) > 0.001D) {
        break;
      }
      if (this.m_ExposureSeekBarContainer != null) {
        this.m_ExposureSeekBarContainer.setVisibility(8);
      }
      return;
    }
    updateExposureSeekBarVisibility();
    if ((HandlerUtils.hasMessages(this, 10001)) && (f == 0.0F)) {}
    for (int i = 1;; i = 0)
    {
      if (i == 0) {
        setRelativeExposureComp(f, false, paramBoolean);
      }
      return;
    }
  }
  
  private void onExposureCompDragIconActionUp()
  {
    if (this.m_IndicatorState != IndicatorState.DRAGING_EXPOSURE_COMP) {
      return;
    }
    changeIndicatorState(IndicatorState.VISIBLE);
    onExposureCompChanged(true);
    hideFocusIndicatorDelayed();
  }
  
  private boolean onExposureCompDragIconTouch(MotionEvent paramMotionEvent)
  {
    if (this.m_LockExposureIndicator) {
      return false;
    }
    switch (paramMotionEvent.getAction())
    {
    default: 
      return true;
    case 0: 
      HandlerUtils.removeMessages(this, 10001);
      this.m_IndicatorContainer.getLocationInWindow(this.m_TempLocationBuffer);
      this.m_IndicatorCenterPointInWindow.x = (this.m_TempLocationBuffer[0] + this.m_IndicatorContainerWidth / 2);
      this.m_IndicatorCenterPointInWindow.y = (this.m_TempLocationBuffer[1] + this.m_IndicatorContainerHeight / 2);
      switch ((getRotation().getDeviceOrientation() - getCameraActivityRotation().getDeviceOrientation() + 360) % 360)
      {
      }
      for (;;)
      {
        changeIndicatorState(IndicatorState.DRAGING_EXPOSURE_COMP);
        break;
        paramMotionEvent = this.m_IndicatorCenterPointInWindow;
        paramMotionEvent.y -= this.m_IndicatorContainerWidth;
        continue;
        paramMotionEvent = this.m_IndicatorCenterPointInWindow;
        paramMotionEvent.x -= this.m_IndicatorContainerWidth;
        paramMotionEvent = this.m_IndicatorCenterPointInWindow;
        paramMotionEvent.y -= this.m_IndicatorContainerHeight;
        continue;
        paramMotionEvent = this.m_IndicatorCenterPointInWindow;
        paramMotionEvent.x -= this.m_IndicatorContainerHeight;
      }
    case 2: 
      if (this.m_IndicatorState == IndicatorState.DRAGING_EXPOSURE_COMP)
      {
        float f2 = paramMotionEvent.getRawX();
        float f1 = paramMotionEvent.getRawY();
        f2 -= this.m_IndicatorCenterPointInWindow.x;
        f1 -= this.m_IndicatorCenterPointInWindow.y;
        if (f2 >= 0.0F) {
          if (f1 <= 0.0F)
          {
            f1 = (float)(Math.atan(f2 / -f1) / 3.141592653589793D / 2.0D);
            label323:
            f1 += (getRotation().getDeviceOrientation() - getCameraActivityRotation().getDeviceOrientation()) / 360.0F;
            if (f1 <= 1.0F) {
              break label474;
            }
            f2 = f1 - 1.0F;
            label356:
            f1 = this.m_RelativeExposureComp;
            if (Math.abs(f1 - 1.0F) > 0.001D) {
              break label489;
            }
            if (f2 > 0.5F) {
              f1 = f2;
            }
          }
        }
        for (;;)
        {
          setRelativeExposureComp(f1, true, false);
          break;
          f1 = (float)((3.141592653589793D - Math.atan(f2 / f1)) / 3.141592653589793D / 2.0D);
          break label323;
          if (f1 <= 0.0F)
          {
            f1 = (float)((6.283185307179586D - Math.atan(f2 / f1)) / 3.141592653589793D / 2.0D);
            break label323;
          }
          f1 = (float)((Math.atan(-f2 / f1) + 3.141592653589793D) / 3.141592653589793D / 2.0D);
          break label323;
          label474:
          f2 = f1;
          if (f1 >= 0.0F) {
            break label356;
          }
          f2 = f1 + 1.0F;
          break label356;
          label489:
          if (Math.abs(1.0F + f1) <= 0.001D)
          {
            if (f2 < 0.5F) {
              f1 = -(1.0F - f2);
            }
          }
          else if (Math.abs(f1) <= 0.001D)
          {
            if (f2 <= 0.5F) {
              f1 = f2;
            } else {
              f1 = -(1.0F - f2);
            }
          }
          else if (f1 > 0.0F)
          {
            float f3 = f2;
            if ((f2 > 0.0F) && (this.m_RelativeExposureComp > 0.5F) && (f3 - this.m_RelativeExposureComp < -0.5F))
            {
              f1 = 1.0F;
            }
            else
            {
              f1 = f3;
              if (this.m_RelativeExposureComp > 0.0F)
              {
                f1 = f3;
                if (this.m_RelativeExposureComp <= 0.5F)
                {
                  f1 = f3;
                  if (f3 - this.m_RelativeExposureComp > 0.5F) {
                    f1 = f3 - 1.0F;
                  }
                }
              }
            }
          }
          else
          {
            f2 = -(1.0F - f2);
            if ((this.m_RelativeExposureComp < -0.5F) && (f2 < 0.0F) && (f2 - this.m_RelativeExposureComp > 0.5F))
            {
              f1 = -1.0F;
            }
            else
            {
              f1 = f2;
              if (this.m_RelativeExposureComp < 0.0F)
              {
                f1 = f2;
                if (this.m_RelativeExposureComp >= -0.5F)
                {
                  f1 = f2;
                  if (f2 - this.m_RelativeExposureComp < -0.5F) {
                    f1 = f2 + 1.0F;
                  }
                }
              }
            }
          }
        }
      }
      return false;
    }
    onExposureCompDragIconActionUp();
    return false;
  }
  
  private void onExposureSeekBarProgressChanged(int paramInt)
  {
    setRelativeExposureComp(paramInt / this.m_ExposureSeekBar.getMax() * 2.0F - 1.0F, true, false);
  }
  
  private void onFocusStateChanged(FocusState paramFocusState1, FocusState paramFocusState2)
  {
    switch (-getcom-oneplus-camera-FocusStateSwitchesValues()[paramFocusState2.ordinal()])
    {
    }
    while (paramFocusState1 != FocusState.SCANNING)
    {
      hideFocusIndicator();
      label51:
      label87:
      int j;
      do
      {
        return;
        i = 0;
        paramFocusState1 = (PhotoCaptureState)getCameraActivity().get(CameraActivity.PROP_PHOTO_CAPTURE_STATE);
        if (this.m_IndicatorState != IndicatorState.VISIBLE_SEPARATED) {
          break;
        }
        if (isFocusExposureAtSameRegion()) {
          i = 1;
        }
        Log.d(this.TAG, "onFocusStateChanged() - captureSate : " + paramFocusState1);
        if (paramFocusState1 != PhotoCaptureState.CAPTURING) {
          break label210;
        }
        j = 0;
        if (j == 0) {
          break label293;
        }
        Log.v(this.TAG, "onFocusStateChanged() - Hide and Show, m_AfRegion : " + this.m_AfRegion);
      } while (this.m_AfRegion == null);
      hideFocusIndicator();
      if (isFocusExposureLocked()) {}
      for (int i = 1;; i = 0)
      {
        HandlerUtils.sendMessage(this, 10000, i, 1, null, true, 50L);
        return;
        if (this.m_IndicatorState == IndicatorState.DRAGING_INDICATOR) {
          break label87;
        }
        i = 1;
        break label87;
        label210:
        switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)getCameraActivity().get(CameraActivity.PROP_VIDEO_CAPTURE_STATE)).ordinal()])
        {
        default: 
          j = i;
          break;
        case 1: 
        case 2: 
          j = i;
          if (this.m_FocusController == null) {
            break;
          }
          j = i;
          if (this.m_FocusController.get(FocusController.PROP_FOCUS_MODE) != FocusMode.CONTINUOUS_AF) {
            break;
          }
          j = 0;
          break;
          label293:
          break label51;
        }
      }
      return;
      if (paramFocusState1 == FocusState.SCANNING)
      {
        if (Math.abs(this.m_RelativeExposureComp) < 0.001D) {
          hideFocusIndicatorDelayed();
        }
        return;
      }
    }
    HandlerUtils.removeMessages(this, 10000);
    hideFocusIndicatorDelayed();
  }
  
  private boolean onIndicatorIconTouch(View paramView, MotionEvent paramMotionEvent)
  {
    if (!((Boolean)get(PROP_CAN_FOCUS_EXPOSURE_SEPARATED)).booleanValue()) {
      return false;
    }
    switch (-getcom-oneplus-camera-ui-FocusExposureIndicatorImpl$IndicatorStateSwitchesValues()[this.m_IndicatorState.ordinal()])
    {
    case 3: 
    case 5: 
    default: 
      return false;
    }
    float f1 = paramMotionEvent.getRawX();
    float f2 = paramMotionEvent.getRawY();
    switch (paramMotionEvent.getAction())
    {
    }
    for (;;)
    {
      return true;
      Log.v(this.TAG, "onIndicatorIconTouch() - Action down, indicator state : ", this.m_IndicatorState);
      this.m_IsTouchingIndicator = true;
      if (HandlerUtils.hasMessages(this, 10005))
      {
        Log.v(this.TAG, "onIndicatorIconTouch() - UI rotating, ignore");
        return true;
      }
      if (this.m_IndicatorState != IndicatorState.VISIBLE) {
        HandlerUtils.removeMessages(this, 10001);
      }
      this.m_TempInitPoint.set(f1, f2);
      paramView.getLocationInWindow(this.m_TempLocationBuffer);
      this.m_TempTargetInitPoint.set(this.m_TempLocationBuffer[0], this.m_TempLocationBuffer[1]);
      switch (-getcom-oneplus-base-RotationSwitchesValues()[getRotation().ordinal()])
      {
      default: 
        break;
      case 1: 
        this.m_TempTargetInitPoint.offset(0.0F, -paramView.getHeight());
        break;
      case 3: 
        this.m_TempTargetInitPoint.offset(-paramView.getWidth(), 0.0F);
        break;
      case 2: 
        this.m_TempTargetInitPoint.offset(-paramView.getWidth(), -paramView.getHeight());
        continue;
        f1 -= this.m_TempInitPoint.x;
        float f3 = f2 - this.m_TempInitPoint.y;
        if ((this.m_IndicatorState != IndicatorState.DRAGING_INDICATOR) && ((Math.abs(f1) > 50.0F) || (Math.abs(f3) > 50.0F)))
        {
          changeIndicatorState(IndicatorState.DRAGING_INDICATOR);
          HandlerUtils.removeMessages(this, 10001);
          updateExposureSeekBarVisibility();
          if (this.m_ExposureController != null) {
            this.m_ExposureController.set(ExposureController.PROP_EXPOSURE_COMPENSATION, Float.valueOf(0.0F));
          }
          if ((this.m_FocusIndicatorText != null) && (this.m_FocusIndicatorText.getVisibility() != 0)) {
            this.m_FocusIndicatorText.setVisibility(0);
          }
          if ((this.m_ExposureIndicatorText != null) && (this.m_ExposureIndicatorText.getVisibility() != 0)) {
            this.m_ExposureIndicatorText.setVisibility(0);
          }
        }
        if (this.m_IndicatorState == IndicatorState.DRAGING_INDICATOR)
        {
          f2 = this.m_TempTargetInitPoint.x;
          float f4 = this.m_TempTargetInitPoint.y;
          f2 = f2 + f1 + paramView.getWidth() / 2;
          float f5 = f4 + f3 + paramView.getHeight() / 2;
          f4 = f2;
          f3 = f5;
          if (this.m_Viewfinder != null)
          {
            paramMotionEvent = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
            f3 = paramMotionEvent.width() / 8.0F;
            if (f2 - f3 >= paramMotionEvent.left) {
              break label664;
            }
            f1 = paramMotionEvent.left + f3;
            label573:
            if (f5 - f3 >= paramMotionEvent.top) {
              break label691;
            }
            f2 = paramMotionEvent.top + f3;
          }
          for (;;)
          {
            f4 = f1;
            f3 = f2;
            if (paramView == this.m_IndicatorContainer)
            {
              f4 = f1 - paramMotionEvent.left;
              f3 = f2 - paramMotionEvent.top;
            }
            f1 = paramView.getWidth() / 2;
            f2 = paramView.getHeight() / 2;
            paramView.setX(f4 - f1);
            paramView.setY(f3 - f2);
            break;
            label664:
            f1 = f2;
            if (f2 + f3 <= paramMotionEvent.right) {
              break label573;
            }
            f1 = paramMotionEvent.right - f3;
            break label573;
            label691:
            f2 = f5;
            if (f5 + f3 > paramMotionEvent.bottom) {
              f2 = paramMotionEvent.bottom - f3;
            }
          }
          Log.v(this.TAG, "onIndicatorIconTouch() - Action up, indicator state : ", this.m_IndicatorState);
          this.m_IsTouchingIndicator = false;
          if (this.m_IndicatorState != IndicatorState.DRAGING_INDICATOR) {
            return true;
          }
          f1 = paramView.getX() + paramView.getWidth() / 2;
          f2 = paramView.getY() + paramView.getHeight() / 2;
          int i;
          if (paramView == this.m_IndicatorContainer)
          {
            i = 1;
            label789:
            if (i == 0) {
              break label834;
            }
            if (this.m_TouchAutoFocusUI != null) {
              this.m_TouchAutoFocusUI.touchAutoFocus(f1, f2);
            }
          }
          for (;;)
          {
            changeIndicatorState(IndicatorState.VISIBLE_SEPARATED);
            hideFocusIndicatorDelayed();
            break;
            i = 0;
            break label789;
            label834:
            if (this.m_TouchAutoExposureUI != null) {
              this.m_TouchAutoExposureUI.touchAutoExposure(f1, f2);
            }
          }
          Log.v(this.TAG, "onIndicatorIconTouch() - Action cancel, indicator state : ", this.m_IndicatorState);
          this.m_IsTouchingIndicator = false;
          if (this.m_IndicatorState != IndicatorState.DRAGING_INDICATOR) {
            return true;
          }
          changeIndicatorState(IndicatorState.VISIBLE_SEPARATED);
          hideFocusIndicatorDelayed();
        }
        break;
      }
    }
  }
  
  private void setAeAfLockTextVisibility(boolean paramBoolean)
  {
    if (paramBoolean) {
      updateAeAfLockTextPosition(this.m_CenterPoint, getRotation());
    }
    setViewVisibility(this.m_AeAfLockText, paramBoolean);
  }
  
  private void setCanFocusExposureSeparated(boolean paramBoolean)
  {
    String str2 = this.TAG;
    if (paramBoolean) {}
    for (String str1 = "Can separate";; str1 = " Cannot separate")
    {
      Log.v(str2, "setCanFocusExposureSeparated() - ", str1);
      hideFocusIndicator();
      return;
    }
  }
  
  private void setRelativeExposureComp(float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (Math.abs(this.m_RelativeExposureComp - paramFloat) <= 0.001D) {
      return;
    }
    Log.v(this.TAG, "setRelativeExposureComp() - Relative exposure compensation : ", Float.valueOf(paramFloat), ", apply : ", Boolean.valueOf(paramBoolean1));
    this.m_RelativeExposureComp = paramFloat;
    if (this.m_ExposureController != null)
    {
      Range localRange;
      if (paramBoolean1)
      {
        localRange = (Range)this.m_ExposureController.get(ExposureController.PROP_EXPOSURE_COMPENSATION_RANGE);
        if (paramFloat < 0.0F) {
          break label110;
        }
      }
      label110:
      for (paramFloat *= ((Float)localRange.getUpper()).floatValue();; paramFloat = -paramFloat * ((Float)localRange.getLower()).floatValue())
      {
        this.m_ExposureController.set(ExposureController.PROP_EXPOSURE_COMPENSATION, Float.valueOf(paramFloat));
        return;
      }
    }
    Log.e(this.TAG, "setRelativeExposureComp() - No ExposureController interface");
  }
  
  private void showFocusIndicator(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.m_Viewfinder == null) {
      return;
    }
    if (this.m_IndicatorContainer == null) {
      return;
    }
    HandlerUtils.removeMessages(this, 10000);
    HandlerUtils.removeMessages(this, 10001);
    Object localObject1 = getCameraActivity();
    if ((!((Boolean)((CameraActivity)localObject1).get(CameraActivity.PROP_IS_CAMERA_PREVIEW_RECEIVED)).booleanValue()) && (((CameraActivity)localObject1).get(CameraActivity.PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.CAPTURING)) {
      return;
    }
    if (((Boolean)((CameraActivity)localObject1).get(CameraActivity.PROP_IS_SELF_TIMER_STARTED)).booleanValue()) {
      return;
    }
    float f1;
    Object localObject2;
    if ((this.m_AfRegion != null) || (this.m_FaceTracker == null) || (((List)this.m_FaceTracker.get(FaceTracker.PROP_FACES)).isEmpty()))
    {
      f1 = (this.m_RelativeExposureComp + 1.0F) * this.m_ExposureSeekBar.getMax() / 2.0F;
      this.m_ExposureSeekBar.setProgress((int)f1);
      if (!paramBoolean1) {
        break label273;
      }
      this.m_FocusIndicator.setImageDrawable(this.m_FocusLockedDrawable);
      this.m_FocusLockedDrawable.stopAnimation();
      this.m_FocusLockedDrawable.startAnimation();
      Log.v(this.TAG, "showFocusIndicator() - Focus");
      if (this.m_GestureDetector != null) {
        this.m_GestureDetector.setGestureHandler(this.m_GestureHandler, 0);
      }
      localObject1 = new PointF();
      localObject2 = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
      if (this.m_AfRegion != null) {
        break label287;
      }
      f1 = 0.5F;
    }
    for (float f2 = 0.5F;; f2 = (this.m_AfRegion.getTop() + this.m_AfRegion.getBottom()) / 2.0F)
    {
      if (this.m_Viewfinder.pointFromPreview(f1, f2, (PointF)localObject1, 0)) {
        break label327;
      }
      return;
      return;
      label273:
      this.m_FocusIndicator.setImageDrawable(this.m_FocusingDrawable);
      break;
      label287:
      f1 = (this.m_AfRegion.getLeft() + this.m_AfRegion.getRight()) / 2.0F;
    }
    label327:
    this.m_CenterPoint = ((PointF)localObject1);
    this.m_IndicatorContainer.setTranslationX(((PointF)localObject1).x - this.m_IndicatorContainerWidth / 2 - ((RectF)localObject2).left);
    this.m_IndicatorContainer.setTranslationY(((PointF)localObject1).y - this.m_IndicatorContainerHeight / 2 - ((RectF)localObject2).top);
    this.m_IndicatorContainer.setVisibility(0);
    updateExposureSeekBarPosition((PointF)localObject1, getRotation());
    updateExposureSeekBarVisibility();
    int i;
    if ((((Boolean)get(PROP_CAN_FOCUS_EXPOSURE_SEPARATED)).booleanValue()) && (this.m_AfRegion != null)) {
      i = 1;
    }
    while (i != 0) {
      if (this.m_ExposureIndicatorContainer == null)
      {
        return;
        i = 0;
      }
      else
      {
        Log.v(this.TAG, "showFocusIndicator() - Exposure");
        localObject1 = new PointF();
        if (this.m_AeRegion == null) {
          f1 = 0.5F;
        }
        for (f2 = 0.5F; !this.m_Viewfinder.pointFromPreview(f1, f2, (PointF)localObject1, 0); f2 = (this.m_AeRegion.getTop() + this.m_AeRegion.getBottom()) / 2.0F)
        {
          return;
          f1 = (this.m_AeRegion.getLeft() + this.m_AeRegion.getRight()) / 2.0F;
        }
        this.m_ExposureIndicatorContainer.setTranslationX(((PointF)localObject1).x - this.m_ExposureIndicatorContainer.getWidth() / 2);
        this.m_ExposureIndicatorContainer.setTranslationY(((PointF)localObject1).y - this.m_ExposureIndicatorContainer.getHeight() / 2);
        this.m_ExposureIndicatorContainer.setVisibility(0);
      }
    }
    if (paramBoolean2)
    {
      localObject1 = new ScaleAnimation(1.4F, 1.0F, 1.4F, 1.0F, 1, 0.5F, 1, 0.5F);
      localObject2 = new ScaleAnimation(0.0F, 1.0F, 0.0F, 1.0F, 1, 0.5F, 1, 0.5F);
      AlphaAnimation localAlphaAnimation = new AlphaAnimation(0.0F, 1.0F);
      ((ScaleAnimation)localObject1).setDuration(300L);
      ((ScaleAnimation)localObject1).setFillBefore(true);
      ((ScaleAnimation)localObject2).setDuration(300L);
      ((ScaleAnimation)localObject2).setFillBefore(true);
      localAlphaAnimation.setDuration(300L);
      localAlphaAnimation.setFillBefore(true);
      ((ScaleAnimation)localObject1).setAnimationListener(new Animation.AnimationListener()
      {
        public void onAnimationEnd(Animation paramAnonymousAnimation)
        {
          if (FocusExposureIndicatorImpl.-get4(FocusExposureIndicatorImpl.this) == FocusExposureIndicatorImpl.IndicatorState.VISIBLE_IN_ANIMATING)
          {
            FocusExposureIndicatorImpl.-wrap4(FocusExposureIndicatorImpl.this, FocusExposureIndicatorImpl.IndicatorState.VISIBLE);
            FocusExposureIndicatorImpl.-wrap5(FocusExposureIndicatorImpl.this);
          }
        }
        
        public void onAnimationRepeat(Animation paramAnonymousAnimation) {}
        
        public void onAnimationStart(Animation paramAnonymousAnimation)
        {
          FocusExposureIndicatorImpl.-wrap4(FocusExposureIndicatorImpl.this, FocusExposureIndicatorImpl.IndicatorState.VISIBLE_IN_ANIMATING);
        }
      });
      this.m_FocusIndicator.startAnimation((Animation)localObject1);
      this.m_FocusIndicatorCenter.startAnimation((Animation)localObject2);
      this.m_ExposureSeekBarContainer.startAnimation(localAlphaAnimation);
      if (i != 0) {
        this.m_ExposureIndicator.startAnimation((Animation)localObject2);
      }
    }
  }
  
  private void updateAeAfLockTextPosition(PointF paramPointF, Rotation paramRotation)
  {
    if (this.m_AeAfLockText == null) {
      return;
    }
    if (this.m_Viewfinder == null) {
      return;
    }
    if (paramPointF == null) {
      return;
    }
    RectF localRectF = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
    int i = 0;
    Resources localResources = getCameraActivity().getResources();
    int k = localResources.getDimensionPixelOffset(2131296439) / 2;
    int j = localResources.getDimensionPixelOffset(2131296444);
    int m = localResources.getDimensionPixelOffset(2131296469);
    switch (-getcom-oneplus-base-RotationSwitchesValues()[paramRotation.ordinal()])
    {
    default: 
      paramPointF = (RelativeLayout.LayoutParams)this.m_AeAfLockText.getLayoutParams();
      if (i != 0)
      {
        paramPointF.removeRule(3);
        paramPointF.addRule(2, 2131361972);
        paramPointF.topMargin = 0;
      }
      break;
    }
    for (paramPointF.bottomMargin = j;; paramPointF.bottomMargin = 0)
    {
      this.m_AeAfLockText.requestLayout();
      return;
      if ((int)paramPointF.x - k - j - m > localRectF.left) {
        break;
      }
      i = 1;
      break;
      if ((int)paramPointF.y + k + j + m < localRectF.bottom) {
        break;
      }
      i = 1;
      break;
      if ((int)paramPointF.x + k + j + m < localRectF.right) {
        break;
      }
      i = 1;
      break;
      if ((int)paramPointF.y - k - j - m > localRectF.top) {
        break;
      }
      i = 1;
      break;
      paramPointF.removeRule(2);
      paramPointF.addRule(3, 2131361972);
      paramPointF.topMargin = j;
    }
  }
  
  private void updateBaseViewLayout(RectF paramRectF)
  {
    if ((this.m_IndicatorBaseView == null) || (paramRectF == null)) {
      return;
    }
    ViewUtils.setSize(this.m_IndicatorBaseView, (int)paramRectF.width(), (int)paramRectF.height());
    ViewUtils.setMargins(this.m_IndicatorBaseView, (int)paramRectF.left, (int)paramRectF.top, 0, 0);
  }
  
  private void updateExposureSeekBarPosition(PointF paramPointF, Rotation paramRotation)
  {
    if (this.m_ExposureSeekBarContainer == null) {
      return;
    }
    if (this.m_Viewfinder == null) {
      return;
    }
    if (paramPointF == null) {
      return;
    }
    RectF localRectF = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
    int i = 0;
    Resources localResources = getCameraActivity().getResources();
    int j = localResources.getDimensionPixelOffset(2131296439) / 2;
    int k = localResources.getDimensionPixelOffset(2131296442);
    int m = localResources.getDimensionPixelOffset(2131296441);
    switch (-getcom-oneplus-base-RotationSwitchesValues()[paramRotation.ordinal()])
    {
    default: 
      paramPointF = (RelativeLayout.LayoutParams)this.m_ExposureSeekBarContainer.getLayoutParams();
      if (i != 0)
      {
        paramPointF.removeRule(17);
        paramPointF.addRule(16, 2131361972);
        paramPointF.setMarginStart(0);
        paramPointF.setMarginEnd(localResources.getDimensionPixelOffset(2131296442));
      }
      break;
    }
    for (;;)
    {
      this.m_ExposureSeekBarContainer.requestLayout();
      return;
      if ((int)paramPointF.y + j + k + m < localRectF.bottom) {
        break;
      }
      i = 1;
      break;
      if ((int)paramPointF.x + j + k + m < localRectF.right) {
        break;
      }
      i = 1;
      break;
      if ((int)paramPointF.y - j - k - m > localRectF.top) {
        break;
      }
      i = 1;
      break;
      if ((int)paramPointF.x - j - k - m > localRectF.left) {
        break;
      }
      i = 1;
      break;
      paramPointF.removeRule(16);
      paramPointF.addRule(17, 2131361972);
      paramPointF.setMarginEnd(0);
      paramPointF.setMarginStart(localResources.getDimensionPixelOffset(2131296442));
    }
  }
  
  private void updateExposureSeekBarProgress(float paramFloat)
  {
    if (this.m_ExposureSeekBar == null) {
      return;
    }
    int j = (int)(1000.0F * paramFloat) + this.m_ExposureSeekBar.getProgress();
    int i;
    if (j < 0) {
      i = 0;
    }
    for (;;)
    {
      this.m_ExposureSeekBar.setProgress(i);
      return;
      i = j;
      if (j > 2000) {
        i = 2000;
      }
    }
  }
  
  private void updateExposureSeekBarVisibility()
  {
    int j;
    if (this.m_ExposureSeekBarContainer != null)
    {
      if (this.m_AfRegion != null) {
        break label69;
      }
      i = 1;
      if ((this.m_IndicatorState != IndicatorState.VISIBLE_SEPARATED) && (this.m_IndicatorState != IndicatorState.DRAGING_INDICATOR)) {
        break label74;
      }
      j = 1;
      label38:
      if ((i == 0) && (j == 0)) {
        break label79;
      }
    }
    label69:
    label74:
    label79:
    for (int i = 8;; i = 0)
    {
      if (this.m_ExposureSeekBarContainer.getVisibility() != i) {
        this.m_ExposureSeekBarContainer.setVisibility(i);
      }
      return;
      i = 0;
      break;
      j = 0;
      break label38;
    }
  }
  
  private void updateFocusExposureLockIndicator()
  {
    Log.v(this.TAG, "updateFocusExposureLockIndicator()");
    boolean bool2 = isFocusExposureLocked();
    CameraActivity localCameraActivity = getCameraActivity();
    boolean bool1 = bool2;
    if (bool2)
    {
      if (localCameraActivity.get(CameraActivity.PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.REVIEWING) {
        break label98;
      }
      if (localCameraActivity.get(CameraActivity.PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.REVIEWING) {
        break label93;
      }
      bool1 = true;
    }
    while (bool1)
    {
      if (((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue())
      {
        showFocusIndicator(true, false);
        HandlerUtils.removeMessages(this, 10001);
        setAeAfLockTextVisibility(true);
      }
      return;
      label93:
      bool1 = false;
      continue;
      label98:
      bool1 = false;
    }
    if ((isFocusLocked()) || (isExposureLocked()))
    {
      hideFocusIndicatorDelayed();
      return;
    }
    this.m_FocusIndicator.setImageDrawable(this.m_FocusingDrawable);
    setAeAfLockTextVisibility(false);
    hideFocusIndicator();
  }
  
  protected void handleMessage(Message paramMessage)
  {
    boolean bool2 = true;
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 10001: 
      hideFocusIndicator();
      return;
    }
    boolean bool1;
    if (paramMessage.arg1 != 0)
    {
      bool1 = true;
      if (paramMessage.arg2 == 0) {
        break label67;
      }
    }
    for (;;)
    {
      showFocusIndicator(bool1, bool2);
      return;
      bool1 = false;
      break;
      label67:
      bool2 = false;
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_ExposureController = ((ExposureController)findComponent(ExposureController.class));
    this.m_FaceTracker = ((FaceTracker)findComponent(FaceTracker.class));
    this.m_FocusController = ((FocusController)findComponent(FocusController.class));
    this.m_GestureDetector = ((GestureDetector)findComponent(GestureDetector.class));
    this.m_TouchAutoExposureUI = ((TouchAutoExposureUI)findComponent(TouchAutoExposureUI.class));
    this.m_TouchAutoFocusUI = ((TouchAutoFocusUI)findComponent(TouchAutoFocusUI.class));
    this.m_Viewfinder = ((Viewfinder)findComponent(Viewfinder.class));
    Object localObject = getCameraActivity();
    Resources localResources = ((CameraActivity)localObject).getResources();
    ViewGroup localViewGroup = ((OPCameraActivity)localObject).getCaptureUIContainer();
    this.m_IndicatorBaseView = localViewGroup.findViewById(2131361970);
    this.m_IndicatorContainer = this.m_IndicatorBaseView.findViewById(2131361971);
    this.m_IndicatorContainerWidth = localResources.getDimensionPixelSize(2131296436);
    this.m_IndicatorContainerHeight = localResources.getDimensionPixelSize(2131296435);
    this.m_FocusIndicator = ((ImageView)this.m_IndicatorContainer.findViewById(2131361972));
    this.m_FocusIndicatorCenter = ((ImageView)this.m_IndicatorContainer.findViewById(2131361973));
    this.m_FocusIndicatorText = ((TextView)this.m_IndicatorContainer.findViewById(2131361974));
    this.m_AeAfLockText = ((TextView)this.m_IndicatorContainer.findViewById(2131361975));
    this.m_ExposureSeekBarContainer = ((RelativeLayout)this.m_IndicatorBaseView.findViewById(2131361976));
    this.m_ExposureSeekBar = ((VerticalSeekBar)this.m_IndicatorContainer.findViewById(2131361977));
    this.m_ExposureSeekBar.setMax(2000);
    this.m_ExposureSeekBar.setProgress(1000);
    this.m_ExposureSeekBar.setEnabled(false);
    this.m_ExposureSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
    {
      public void onProgressChanged(SeekBar paramAnonymousSeekBar, int paramAnonymousInt, boolean paramAnonymousBoolean)
      {
        FocusExposureIndicatorImpl.-wrap12(FocusExposureIndicatorImpl.this, paramAnonymousInt);
      }
      
      public void onStartTrackingTouch(SeekBar paramAnonymousSeekBar) {}
      
      public void onStopTrackingTouch(SeekBar paramAnonymousSeekBar) {}
    });
    this.m_FocusIndicator.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return FocusExposureIndicatorImpl.-wrap1(FocusExposureIndicatorImpl.this, FocusExposureIndicatorImpl.-get3(FocusExposureIndicatorImpl.this), paramAnonymousMotionEvent);
      }
    });
    addAutoRotateView(this.m_IndicatorContainer);
    this.m_FocusingDrawable = new FocusExposureRegionDrawable((Context)localObject, 2130837616);
    this.m_FocusLockedDrawable = new FocusExposureRegionDrawable((Context)localObject, 2130837616);
    setRelativeExposureComp(0.0F, false, false);
    this.m_ExposureIndicatorContainer = localViewGroup.findViewById(2131361963);
    this.m_ExposureIndicatorText = ((TextView)this.m_ExposureIndicatorContainer.findViewById(2131361965));
    this.m_ExposureIndicator = ((ImageView)this.m_ExposureIndicatorContainer.findViewById(2131361964));
    this.m_ExposureIndicator.setImageDrawable(((CameraActivity)localObject).getDrawable(2130837605));
    this.m_ExposureIndicator.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return FocusExposureIndicatorImpl.-wrap1(FocusExposureIndicatorImpl.this, FocusExposureIndicatorImpl.-get2(FocusExposureIndicatorImpl.this), paramAnonymousMotionEvent);
      }
    });
    addAutoRotateView(this.m_ExposureIndicatorContainer);
    this.m_ExposureIndicatorContainer.setVisibility(4);
    if (this.m_GestureDetector != null) {
      this.m_GestureDetector.setGestureHandler(this.m_GestureHandler, 0);
    }
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_IS_CAMERA_PREVIEW_RECEIVED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if ((((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) || (FocusExposureIndicatorImpl.-wrap0(FocusExposureIndicatorImpl.this))) {
          return;
        }
        switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)FocusExposureIndicatorImpl.this.getCameraActivity().get(CameraActivity.PROP_VIDEO_CAPTURE_STATE)).ordinal()])
        {
        }
        FocusExposureIndicatorImpl.-wrap6(FocusExposureIndicatorImpl.this);
        FocusExposureIndicatorImpl.-wrap14(FocusExposureIndicatorImpl.this, false);
      }
    });
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_IS_CAMERA_SWITCHING, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue())
        {
          FocusExposureIndicatorImpl.-wrap6(FocusExposureIndicatorImpl.this);
          FocusExposureIndicatorImpl.-wrap14(FocusExposureIndicatorImpl.this, false);
        }
      }
    });
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_PHOTO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PhotoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<PhotoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        boolean bool2 = false;
        boolean bool1;
        switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
        {
        default: 
          bool1 = bool2;
        }
        for (;;)
        {
          FocusExposureIndicatorImpl.-wrap7(FocusExposureIndicatorImpl.this, bool1);
          return;
          FocusExposureIndicatorImpl.-wrap11(FocusExposureIndicatorImpl.this);
          bool1 = bool2;
          continue;
          bool1 = true;
          continue;
          bool1 = bool2;
          if (!FocusExposureIndicatorImpl.-wrap0(FocusExposureIndicatorImpl.this))
          {
            bool1 = bool2;
            if (!((Boolean)FocusExposureIndicatorImpl.this.getCameraActivity().get(CameraActivity.PROP_IS_SELF_TIMER_STARTED)).booleanValue())
            {
              FocusExposureIndicatorImpl.-wrap6(FocusExposureIndicatorImpl.this);
              bool1 = bool2;
              continue;
              FocusExposureIndicatorImpl.-wrap6(FocusExposureIndicatorImpl.this);
              FocusExposureIndicatorImpl.-wrap14(FocusExposureIndicatorImpl.this, false);
              bool1 = bool2;
            }
          }
        }
      }
    });
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_VIDEO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<VideoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<VideoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
        {
        case 1: 
        default: 
          return;
        case 2: 
          FocusExposureIndicatorImpl.-wrap17(FocusExposureIndicatorImpl.this);
          return;
        }
        FocusExposureIndicatorImpl.-wrap6(FocusExposureIndicatorImpl.this);
        FocusExposureIndicatorImpl.-wrap14(FocusExposureIndicatorImpl.this, false);
      }
    });
    if (this.m_ExposureController != null)
    {
      localObject = new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
        {
          FocusExposureIndicatorImpl.-wrap10(FocusExposureIndicatorImpl.this, true);
        }
      };
      this.m_ExposureController.addCallback(ExposureController.PROP_EXPOSURE_COMPENSATION, (PropertyChangedCallback)localObject);
      this.m_ExposureController.addCallback(ExposureController.PROP_EXPOSURE_COMPENSATION_RANGE, (PropertyChangedCallback)localObject);
      this.m_ExposureController.addCallback(ExposureController.PROP_EXPOSURE_COMPENSATION_STEP, (PropertyChangedCallback)localObject);
      this.m_ExposureController.addCallback(ExposureController.PROP_IS_AE_LOCKED, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          FocusExposureIndicatorImpl.-wrap17(FocusExposureIndicatorImpl.this);
        }
      });
      onExposureCompChanged(false);
      this.m_ExposureController.addCallback(ExposureController.PROP_AE_REGIONS, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera.MeteringRect>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera.MeteringRect>> paramAnonymousPropertyChangeEventArgs)
        {
          FocusExposureIndicatorImpl.-wrap8(FocusExposureIndicatorImpl.this, (List)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
    }
    if (this.m_FaceTracker != null) {
      this.m_FaceTracker.addCallback(FaceTracker.PROP_FACES, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera.Face>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera.Face>> paramAnonymousPropertyChangeEventArgs)
        {
          if ((!((List)paramAnonymousPropertyChangeEventArgs.getNewValue()).isEmpty()) && (FocusExposureIndicatorImpl.-get1(FocusExposureIndicatorImpl.this) == null)) {
            FocusExposureIndicatorImpl.-wrap6(FocusExposureIndicatorImpl.this);
          }
        }
      });
    }
    if (this.m_FocusController != null)
    {
      this.m_FocusController.addCallback(FocusController.PROP_AF_REGIONS, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera.MeteringRect>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera.MeteringRect>> paramAnonymousPropertyChangeEventArgs)
        {
          FocusExposureIndicatorImpl.-wrap9(FocusExposureIndicatorImpl.this, (List)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
      this.m_FocusController.addCallback(FocusController.PROP_FOCUS_STATE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<FocusState> paramAnonymousPropertyKey, PropertyChangeEventArgs<FocusState> paramAnonymousPropertyChangeEventArgs)
        {
          FocusExposureIndicatorImpl.-wrap13(FocusExposureIndicatorImpl.this, (FocusState)paramAnonymousPropertyChangeEventArgs.getOldValue(), (FocusState)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
      this.m_FocusController.addCallback(FocusController.PROP_IS_FOCUS_LOCKED, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          FocusExposureIndicatorImpl.-wrap17(FocusExposureIndicatorImpl.this);
        }
      });
      this.m_FocusController.addCallback(FocusController.PROP_FOCUS_MODE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<FocusMode> paramAnonymousPropertyKey, PropertyChangeEventArgs<FocusMode> paramAnonymousPropertyChangeEventArgs)
        {
          if (paramAnonymousPropertyChangeEventArgs.getNewValue() == FocusMode.MANUAL) {
            FocusExposureIndicatorImpl.-wrap6(FocusExposureIndicatorImpl.this);
          }
        }
      });
      if (this.m_Viewfinder == null) {
        break label875;
      }
      this.m_Viewfinder.addCallback(Viewfinder.PROP_PREVIEW_BOUNDS, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<RectF> paramAnonymousPropertyKey, PropertyChangeEventArgs<RectF> paramAnonymousPropertyChangeEventArgs)
        {
          FocusExposureIndicatorImpl.-wrap15(FocusExposureIndicatorImpl.this, (RectF)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
    }
    for (;;)
    {
      if (this.m_Viewfinder != null) {
        updateBaseViewLayout((RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS));
      }
      if (this.m_FocusController != null)
      {
        onAfRegionsChanged((List)this.m_FocusController.get(FocusController.PROP_AF_REGIONS));
        onFocusStateChanged(null, (FocusState)this.m_FocusController.get(FocusController.PROP_FOCUS_STATE));
      }
      updateFocusExposureLockIndicator();
      return;
      Log.w(this.TAG, "onInitialize() - No FocusController");
      break;
      label875:
      Log.w(this.TAG, "onInitialize() - No Viewfinder");
    }
  }
  
  protected void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    HandlerUtils.sendMessage(this, 10005, true, 700L);
    super.onRotationChanged(paramRotation1, paramRotation2);
    updateExposureSeekBarPosition(this.m_CenterPoint, paramRotation2);
    updateAeAfLockTextPosition(this.m_CenterPoint, paramRotation2);
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_CAN_FOCUS_EXPOSURE_SEPARATED) {
      setCanFocusExposureSeparated(((Boolean)paramTValue).booleanValue());
    }
    return super.set(paramPropertyKey, paramTValue);
  }
  
  private static class DrawableItem
  {
    public int duration;
    public int resId;
    
    public DrawableItem(int paramInt1, int paramInt2)
    {
      this.duration = paramInt2;
      this.resId = paramInt1;
    }
  }
  
  private static final class FocusExposureRegionDrawable
    extends Drawable
  {
    private final Drawable m_BaseDrawable;
    private final int m_ExposureCompBorderWidth;
    private final Paint m_ExposureCompPaint;
    private float m_RelativeExposureComp;
    
    public FocusExposureRegionDrawable(Context paramContext, int paramInt)
    {
      this(paramContext, new FocusExposureIndicatorImpl.DrawableItem[] { new FocusExposureIndicatorImpl.DrawableItem(paramInt, 0) });
    }
    
    public FocusExposureRegionDrawable(Context paramContext, FocusExposureIndicatorImpl.DrawableItem... paramVarArgs)
    {
      if (paramVarArgs.length == 0) {
        throw new IllegalArgumentException("Need at least one item");
      }
      if (paramVarArgs.length == 1)
      {
        this.m_BaseDrawable = paramContext.getDrawable(paramVarArgs[0].resId);
        this.m_BaseDrawable.setCallback(new Drawable.Callback()
        {
          public void invalidateDrawable(Drawable paramAnonymousDrawable)
          {
            FocusExposureIndicatorImpl.FocusExposureRegionDrawable.this.invalidateSelf();
          }
          
          public void scheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable, long paramAnonymousLong)
          {
            FocusExposureIndicatorImpl.FocusExposureRegionDrawable.this.scheduleSelf(paramAnonymousRunnable, paramAnonymousLong);
          }
          
          public void unscheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable)
          {
            FocusExposureIndicatorImpl.FocusExposureRegionDrawable.this.unscheduleSelf(paramAnonymousRunnable);
          }
        });
      }
      for (;;)
      {
        this.m_ExposureCompBorderWidth = paramContext.getResources().getDimensionPixelSize(2131296256);
        this.m_ExposureCompPaint = new Paint();
        this.m_ExposureCompPaint.setColor(paramContext.getResources().getColor(2131230720));
        this.m_ExposureCompPaint.setStyle(Paint.Style.STROKE);
        this.m_ExposureCompPaint.setStrokeWidth(this.m_ExposureCompBorderWidth);
        this.m_ExposureCompPaint.setAntiAlias(true);
        return;
        FocusExposureIndicatorImpl.MultiCallbackAnimationDrawable localMultiCallbackAnimationDrawable = new FocusExposureIndicatorImpl.MultiCallbackAnimationDrawable(null);
        int i = 0;
        while (i < paramVarArgs.length)
        {
          localMultiCallbackAnimationDrawable.addFrame(paramContext.getDrawable(paramVarArgs[i].resId), paramVarArgs[i].duration);
          i += 1;
        }
        localMultiCallbackAnimationDrawable.setCallback(localMultiCallbackAnimationDrawable);
        localMultiCallbackAnimationDrawable.addCallback(new Drawable.Callback()
        {
          public void invalidateDrawable(Drawable paramAnonymousDrawable)
          {
            FocusExposureIndicatorImpl.FocusExposureRegionDrawable.this.invalidateSelf();
          }
          
          public void scheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable, long paramAnonymousLong)
          {
            FocusExposureIndicatorImpl.FocusExposureRegionDrawable.this.scheduleSelf(paramAnonymousRunnable, paramAnonymousLong);
          }
          
          public void unscheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable)
          {
            FocusExposureIndicatorImpl.FocusExposureRegionDrawable.this.unscheduleSelf(paramAnonymousRunnable);
          }
        });
        this.m_BaseDrawable = localMultiCallbackAnimationDrawable;
      }
    }
    
    public void draw(Canvas paramCanvas)
    {
      Rect localRect = getBounds();
      this.m_BaseDrawable.setBounds(localRect);
      this.m_BaseDrawable.draw(paramCanvas);
      if (Math.abs(this.m_RelativeExposureComp) > 0.01D)
      {
        int i = this.m_ExposureCompBorderWidth / 2;
        localRect.inset(i, i);
        paramCanvas.drawArc(localRect.left, localRect.top, localRect.right, localRect.bottom, 270.0F, 360.0F * this.m_RelativeExposureComp, false, this.m_ExposureCompPaint);
        localRect.inset(-i, -i);
      }
    }
    
    public int getIntrinsicHeight()
    {
      return this.m_BaseDrawable.getIntrinsicHeight();
    }
    
    public int getIntrinsicWidth()
    {
      return this.m_BaseDrawable.getIntrinsicWidth();
    }
    
    public int getOpacity()
    {
      return this.m_BaseDrawable.getOpacity();
    }
    
    public void setAlpha(int paramInt)
    {
      this.m_BaseDrawable.setAlpha(paramInt);
    }
    
    public void setColorFilter(ColorFilter paramColorFilter)
    {
      this.m_BaseDrawable.setColorFilter(paramColorFilter);
    }
    
    public void setRelativeExposureComp(float paramFloat)
    {
      this.m_RelativeExposureComp = paramFloat;
      invalidateSelf();
    }
    
    public void startAnimation()
    {
      startAnimation(true);
    }
    
    public void startAnimation(boolean paramBoolean)
    {
      if ((this.m_BaseDrawable instanceof AnimationDrawable))
      {
        AnimationDrawable localAnimationDrawable = (AnimationDrawable)this.m_BaseDrawable;
        localAnimationDrawable.setOneShot(paramBoolean);
        localAnimationDrawable.start();
      }
    }
    
    public void stopAnimation()
    {
      if ((this.m_BaseDrawable instanceof AnimationDrawable)) {
        ((AnimationDrawable)this.m_BaseDrawable).stop();
      }
    }
  }
  
  private static enum IndicatorState
  {
    DRAGING_EXPOSURE_COMP,  DRAGING_INDICATOR,  INVISIBLE,  VISIBLE,  VISIBLE_IN_ANIMATING,  VISIBLE_SEPARATED;
  }
  
  private static class MultiCallbackAnimationDrawable
    extends AnimationDrawable
  {
    private Set<Drawable.Callback> m_Callbacks;
    
    public void addCallback(Drawable.Callback paramCallback)
    {
      if (this.m_Callbacks == null) {
        this.m_Callbacks = new HashSet();
      }
      this.m_Callbacks.add(paramCallback);
    }
    
    public void clearCallbacks()
    {
      if (this.m_Callbacks == null) {
        return;
      }
      this.m_Callbacks.clear();
      this.m_Callbacks = null;
    }
    
    public void invalidateDrawable(Drawable paramDrawable)
    {
      super.invalidateDrawable(paramDrawable);
      Iterator localIterator = this.m_Callbacks.iterator();
      while (localIterator.hasNext()) {
        ((Drawable.Callback)localIterator.next()).invalidateDrawable(paramDrawable);
      }
    }
    
    public void removeCallback(Drawable.Callback paramCallback)
    {
      if (this.m_Callbacks == null) {
        return;
      }
      this.m_Callbacks.remove(paramCallback);
    }
    
    public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
    {
      super.scheduleDrawable(paramDrawable, paramRunnable, paramLong);
      Iterator localIterator = this.m_Callbacks.iterator();
      while (localIterator.hasNext()) {
        ((Drawable.Callback)localIterator.next()).scheduleDrawable(paramDrawable, paramRunnable, paramLong);
      }
    }
    
    public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
    {
      super.unscheduleDrawable(paramDrawable, paramRunnable);
      Iterator localIterator = this.m_Callbacks.iterator();
      while (localIterator.hasNext()) {
        ((Drawable.Callback)localIterator.next()).unscheduleDrawable(paramDrawable, paramRunnable);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/FocusExposureIndicatorImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */