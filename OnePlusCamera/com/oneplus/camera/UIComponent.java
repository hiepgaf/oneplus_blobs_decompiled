package com.oneplus.camera;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.camera.widget.RotateRelativeLayout;
import com.oneplus.widget.ViewUtils;
import com.oneplus.widget.ViewUtils.AnimationCompletedCallback;
import java.util.ArrayList;
import java.util.List;

public abstract class UIComponent
  extends CameraComponent
{
  public static final long DURATION_FADE_IN = 600L;
  public static final long DURATION_FADE_OUT = 300L;
  public static final long DURATION_HIDE_NAVIGATION_BAR = 350L;
  public static final long DURATION_ROTATION = 600L;
  public static final long DURATION_SHOW_NAVIGATION_BAR;
  public static final Interpolator INTERPOLATOR_FADE_IN;
  public static final Interpolator INTERPOLATOR_FADE_OUT;
  public static final Interpolator INTERPOLATOR_HIDE_NAVIGATION_BAR;
  public static final Interpolator INTERPOLATOR_ROTATION = new PathInterpolator(0.8F, 0.0F, 0.2F, 1.0F);
  public static final Interpolator INTERPOLATOR_SHOW_NAVIGATION_BAR = new DecelerateInterpolator(3.0F);
  private List<View> m_AutoRotateViews;
  private final PropertyChangedCallback<Boolean> m_CaptureUIEnabledChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      UIComponent.this.onCaptureUIEnabledStateChanged(((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
    }
  };
  private PropertyChangedCallback<Boolean> m_IsCameraThreadStartedCallback;
  private List<View> m_NavBarAlignedViews;
  private final PropertyChangedCallback<Boolean> m_NavBarVisibilityCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      UIComponent.-wrap0(UIComponent.this, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
    }
  };
  private Rotation m_Rotation = Rotation.LANDSCAPE;
  private final PropertyChangedCallback<Rotation> m_RotationChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Rotation> paramAnonymousPropertyKey, PropertyChangeEventArgs<Rotation> paramAnonymousPropertyChangeEventArgs)
    {
      UIComponent.this.onRotationChanged((Rotation)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Rotation)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  
  static
  {
    INTERPOLATOR_HIDE_NAVIGATION_BAR = new AccelerateInterpolator(2.0F);
    DURATION_SHOW_NAVIGATION_BAR = 350L;
  }
  
  protected UIComponent(String paramString, CameraActivity paramCameraActivity, boolean paramBoolean)
  {
    this(paramString, paramCameraActivity, paramBoolean, false);
  }
  
  protected UIComponent(String paramString, CameraActivity paramCameraActivity, boolean paramBoolean1, boolean paramBoolean2)
  {
    super(paramString, paramCameraActivity, paramBoolean1, paramBoolean2);
  }
  
  private void onNavBarVisibilityChanged(boolean paramBoolean)
  {
    if (this.m_NavBarAlignedViews != null)
    {
      boolean bool = getCameraActivityRotation().isPortrait();
      int j = getScreenSize().getNavigationBarSize();
      int i = this.m_NavBarAlignedViews.size() - 1;
      while (i >= 0)
      {
        onNavBarVisibilityChanged(paramBoolean, (View)this.m_NavBarAlignedViews.get(i), bool, j);
        i -= 1;
      }
    }
  }
  
  private void onNavBarVisibilityChanged(boolean paramBoolean1, View paramView, boolean paramBoolean2, int paramInt) {}
  
  protected void addAutoRotateView(View paramView)
  {
    verifyAccess();
    if (this.m_AutoRotateViews == null) {
      this.m_AutoRotateViews = new ArrayList();
    }
    this.m_AutoRotateViews.add(paramView);
    rotateView(paramView, this.m_Rotation, 0L);
  }
  
  protected void addNavBarAlignedView(View paramView)
  {
    verifyAccess();
    if (this.m_NavBarAlignedViews == null) {
      this.m_NavBarAlignedViews = new ArrayList();
    }
    this.m_NavBarAlignedViews.add(paramView);
    if (this.m_NavBarAlignedViews.size() == 1) {
      getCameraActivity().addCallback(CameraActivity.PROP_IS_NAVIGATION_BAR_VISIBLE, this.m_NavBarVisibilityCallback);
    }
    if (((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_NAVIGATION_BAR_VISIBLE)).booleanValue()) {
      onNavBarVisibilityChanged(true, paramView, getCameraActivityRotation().isPortrait(), getScreenSize().getNavigationBarSize());
    }
  }
  
  protected final Rotation getCameraActivityRotation()
  {
    return (Rotation)getCameraActivity().get(CameraActivity.PROP_ACTIVITY_ROTATION);
  }
  
  protected final Rotation getRotation()
  {
    return (Rotation)getCameraActivity().get(CameraActivity.PROP_ROTATION);
  }
  
  protected final boolean isCameraThreadStarted()
  {
    return ((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_CAMERA_THREAD_STARTED)).booleanValue();
  }
  
  protected final boolean isCaptureUIEnabled()
  {
    return ((Boolean)getCameraActivity().get(CameraActivity.PROP_IS_CAPTURE_UI_ENABLED)).booleanValue();
  }
  
  protected void onCameraThreadStarted() {}
  
  protected void onCaptureUIEnabledStateChanged(boolean paramBoolean) {}
  
  protected void onDeinitialize()
  {
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.removeCallback(CameraActivity.PROP_IS_CAPTURE_UI_ENABLED, this.m_CaptureUIEnabledChangedCallback);
    localCameraActivity.removeCallback(CameraActivity.PROP_ROTATION, this.m_RotationChangedCallback);
    if (this.m_IsCameraThreadStartedCallback != null)
    {
      localCameraActivity.removeCallback(CameraActivity.PROP_IS_CAMERA_THREAD_STARTED, this.m_IsCameraThreadStartedCallback);
      this.m_IsCameraThreadStartedCallback = null;
    }
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addCallback(CameraActivity.PROP_IS_CAPTURE_UI_ENABLED, this.m_CaptureUIEnabledChangedCallback);
    localCameraActivity.addCallback(CameraActivity.PROP_ROTATION, this.m_RotationChangedCallback);
    if (!((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_CAMERA_THREAD_STARTED)).booleanValue())
    {
      this.m_IsCameraThreadStartedCallback = new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          UIComponent.-set0(UIComponent.this, null);
          paramAnonymousPropertySource.removeCallback(paramAnonymousPropertyKey, this);
          UIComponent.this.onCameraThreadStarted();
        }
      };
      localCameraActivity.addCallback(CameraActivity.PROP_IS_CAMERA_THREAD_STARTED, this.m_IsCameraThreadStartedCallback);
    }
    this.m_Rotation = getRotation();
  }
  
  protected void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    this.m_Rotation = paramRotation2;
    if (this.m_AutoRotateViews != null)
    {
      int i = this.m_AutoRotateViews.size() - 1;
      while (i >= 0)
      {
        rotateView((View)this.m_AutoRotateViews.get(i), paramRotation2);
        i -= 1;
      }
    }
  }
  
  protected void removeAutoRotateView(View paramView)
  {
    verifyAccess();
    if (this.m_AutoRotateViews != null) {
      this.m_AutoRotateViews.remove(paramView);
    }
  }
  
  protected void removeNavBarAlignedView(View paramView)
  {
    verifyAccess();
    if ((this.m_NavBarAlignedViews != null) && (this.m_NavBarAlignedViews.remove(paramView)))
    {
      if (this.m_NavBarAlignedViews.isEmpty()) {
        getCameraActivity().removeCallback(CameraActivity.PROP_IS_NAVIGATION_BAR_VISIBLE, this.m_NavBarVisibilityCallback);
      }
      return;
    }
  }
  
  protected void rotateLayout(RotateRelativeLayout paramRotateRelativeLayout)
  {
    rotateLayout(paramRotateRelativeLayout, 600L, null);
  }
  
  protected void rotateLayout(RotateRelativeLayout paramRotateRelativeLayout, long paramLong)
  {
    rotateLayout(paramRotateRelativeLayout, paramLong, null);
  }
  
  protected void rotateLayout(final RotateRelativeLayout paramRotateRelativeLayout, final long paramLong, ViewRotationCallback paramViewRotationCallback)
  {
    if (paramRotateRelativeLayout == null) {
      return;
    }
    Rotation localRotation = getRotation();
    if (paramRotateRelativeLayout.getLayoutRotation() == localRotation) {
      return;
    }
    if ((paramLong > 0L) && (paramRotateRelativeLayout.getVisibility() == 0))
    {
      paramLong /= 2L;
      setViewVisibility(paramRotateRelativeLayout, false, paramLong, INTERPOLATOR_FADE_OUT, new ViewUtils.AnimationCompletedCallback()
      {
        public void onAnimationCompleted(View paramAnonymousView, boolean paramAnonymousBoolean)
        {
          if (!paramAnonymousBoolean)
          {
            paramAnonymousView = UIComponent.this.getRotation();
            paramRotateRelativeLayout.setRotation(paramAnonymousView);
            UIComponent.this.setViewVisibility(paramRotateRelativeLayout, true, paramLong, UIComponent.INTERPOLATOR_FADE_IN);
            if (this.val$callback != null) {
              this.val$callback.onRotated(paramRotateRelativeLayout, paramAnonymousView);
            }
          }
        }
      });
      return;
    }
    paramRotateRelativeLayout.setRotation(localRotation);
  }
  
  protected void rotateLayout(RotateRelativeLayout paramRotateRelativeLayout, ViewRotationCallback paramViewRotationCallback)
  {
    rotateLayout(paramRotateRelativeLayout, 600L, paramViewRotationCallback);
  }
  
  protected void rotateView(View paramView, Rotation paramRotation)
  {
    rotateView(paramView, paramRotation, 600L, INTERPOLATOR_ROTATION);
  }
  
  protected void rotateView(View paramView, Rotation paramRotation, long paramLong)
  {
    rotateView(paramView, paramRotation, paramLong, INTERPOLATOR_ROTATION);
  }
  
  protected void rotateView(View paramView, Rotation paramRotation, long paramLong, Interpolator paramInterpolator)
  {
    if (paramView == null) {
      return;
    }
    Rotation localRotation = (Rotation)getCameraActivity().get(CameraActivity.PROP_ACTIVITY_ROTATION);
    float f1 = paramView.getRotation();
    float f2 = localRotation.getDeviceOrientation() - paramRotation.getDeviceOrientation();
    if (Math.abs(f1 - f2) < 0.1F)
    {
      ViewUtils.rotate(paramView, f2, 0L, null);
      return;
    }
    if (Math.abs(f2 - f1) > 180.0F)
    {
      if (f1 <= f2) {
        break label106;
      }
      paramView.setRotation(f1 - 360.0F);
    }
    for (;;)
    {
      ViewUtils.rotate(paramView, f2, paramLong, paramInterpolator);
      return;
      label106:
      paramView.setRotation(f1 + 360.0F);
    }
  }
  
  protected void setViewVisibility(View paramView, boolean paramBoolean)
  {
    setViewVisibility(paramView, paramBoolean, null);
  }
  
  protected void setViewVisibility(View paramView, boolean paramBoolean, long paramLong, Interpolator paramInterpolator)
  {
    ViewUtils.setVisibility(paramView, paramBoolean, paramLong, paramInterpolator);
  }
  
  protected void setViewVisibility(View paramView, boolean paramBoolean, long paramLong, Interpolator paramInterpolator, ViewUtils.AnimationCompletedCallback paramAnimationCompletedCallback)
  {
    ViewUtils.setVisibility(paramView, paramBoolean, paramLong, paramInterpolator, paramAnimationCompletedCallback);
  }
  
  protected void setViewVisibility(View paramView, boolean paramBoolean, ViewUtils.AnimationCompletedCallback paramAnimationCompletedCallback)
  {
    long l;
    if (paramBoolean) {
      l = 600L;
    }
    for (Interpolator localInterpolator = INTERPOLATOR_FADE_IN;; localInterpolator = null)
    {
      ViewUtils.setVisibility(paramView, paramBoolean, l, localInterpolator, paramAnimationCompletedCallback);
      return;
      l = 0L;
    }
  }
  
  protected static abstract interface ViewRotationCallback
  {
    public abstract void onRotated(View paramView, Rotation paramRotation);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/UIComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */