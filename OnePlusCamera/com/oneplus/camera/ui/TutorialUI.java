package com.oneplus.camera.ui;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.Settings;
import com.oneplus.camera.Camera;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.Camera.MeteringRect;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.FocusController;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.ZoomBar;
import com.oneplus.camera.bokeh.BokehCaptureMode;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.capturemode.PhotoCaptureMode;
import com.oneplus.camera.drawable.ShadowDrawable;
import com.oneplus.camera.manual.ManualCaptureMode;
import com.oneplus.camera.manual.ManualModeUI;
import com.oneplus.camera.panorama.PanoramaCaptureMode;
import com.oneplus.drawable.HollowDrawable;
import com.oneplus.widget.ViewUtils.AnimationCompletedCallback;
import java.util.Iterator;
import java.util.List;

public class TutorialUI
  extends UIComponent
{
  private static final float ALPHA_ZOOM_FADE_OUT_DEFAULT_VALUE = 0.8F;
  private static final long DELAY_HIDE_TUTORIAL_CONTAINER = 5000L;
  private static final int DISTANCE_ANIMATION_DRAG_FOCAL_POINT = 300;
  private static final int DISTANCE_ANIMATION_ENTER_EXIT_IMMERSIVE_MODE = 400;
  private static final int DISTANCE_ANIMATION_ZOOM_X = -300;
  private static final long DURATION_ANIMATION_DRAG_FOCAL_POINT_SCALE = 400L;
  private static final long DURATION_ANIMATION_ENTER_EXIT_IMMERSIVE_MODE = 1667L;
  private static final long DURATION_ANIMATION_FADE_IN_OUT = 200L;
  private static final long DURATION_ANIMATION_TRANSLATION = 1000L;
  private static final long DURATION_ANIMATION_ZOOM_SCALE = 250L;
  private static final float FACTOR_DECELERATE_INTERPOLATOR = 2.0F;
  private static final int INSET_X_Y_HOLLOW_DRAWABLE = 9;
  private static final int MSG_HIDE_TUTORIAL_CONTAINER = 50001;
  private static final int MSG_SHOW_ZOOM_ANIMATION = 50002;
  private static final String PREF_TUTORIAL_BOKEH = "TutorialBokeh";
  private static final String PREF_TUTORIAL_DRAG_FOCAL_POINT = "TutorialDragFocalPoint";
  private static final String PREF_TUTORIAL_DRAG_ZOOM_VALUE = "TutorialDragZoomValue";
  private static final String PREF_TUTORIAL_ENTER_CUSTOM_MODE = "TutorialEnterCustomMode";
  private static final String PREF_TUTORIAL_ENTER_IMMERSIVE = "TutorialEnterImmersive";
  private static final String PREF_TUTORIAL_EXIT_IMMERSIVE = "TutorialExitImmersive";
  private static final String PREF_TUTORIAL_FRONT_CAMERA = "TutorialFrontCamera";
  private static final String PREF_TUTORIAL_PANORAMA = "TutorialPanorama";
  private static final String PREF_TUTORIAL_SAVE_CUSTOM_BUTTON = "TutorialSaveCustomButton";
  private static final String PREF_TUTORIAL_SWIPE_LEFT_RIGHT = "TutorialSwipeLeftRight";
  private static final int REPEAT_COUNT_FOR_ANIMATION = 3;
  private static final float SCALE_SIZE_FOR_DRAG_FOCAL_POINT_ANIMATION = 0.7F;
  private static final float SCALE_SIZE_FOR_ZOOM_ANIMATION = 0.85F;
  private final PropertyChangedCallback<BaseActivity.State> m_CameraActivityStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
    {
      if (paramAnonymousPropertyChangeEventArgs.getNewValue() != BaseActivity.State.RUNNING) {
        TutorialUI.-wrap0(TutorialUI.this, false);
      }
    }
  };
  private final PropertyChangedCallback<CameraGallery.UIState> m_CameraGalleryUIStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CameraGallery.UIState> paramAnonymousPropertyKey, PropertyChangeEventArgs<CameraGallery.UIState> paramAnonymousPropertyChangeEventArgs)
    {
      paramAnonymousPropertySource = (CameraGallery.UIState)paramAnonymousPropertyChangeEventArgs.getNewValue();
      if ((paramAnonymousPropertySource == CameraGallery.UIState.OPENING) || (paramAnonymousPropertySource == CameraGallery.UIState.OPENED)) {
        TutorialUI.-wrap0(TutorialUI.this, false);
      }
    }
  };
  private CaptureBar m_CaptureBar;
  private CaptureModeManager m_CaptureModeManager;
  private final PropertyChangedCallback<Boolean> m_CaptureModePanelChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
        TutorialUI.-wrap0(TutorialUI.this, false);
      }
    }
  };
  private CaptureModeSwitcher m_CaptureModeSwitcher;
  private final EventHandler<EventArgs> m_CustomModeClickHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<EventArgs> paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
    {
      TutorialUI.-wrap3(TutorialUI.this);
    }
  };
  private final PropertyChangedCallback<Integer> m_CustomModeSettingChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Integer> paramAnonymousPropertyKey, PropertyChangeEventArgs<Integer> paramAnonymousPropertyChangeEventArgs)
    {
      if (((Integer)paramAnonymousPropertyChangeEventArgs.getNewValue()).intValue() != 0) {
        TutorialUI.-wrap2(TutorialUI.this, ((Integer)paramAnonymousPropertyChangeEventArgs.getNewValue()).intValue());
      }
    }
  };
  private View m_FocalPointFocusView;
  private View m_FocalPointWhiteView;
  private final PropertyChangedCallback<List<Camera.MeteringRect>> m_FocusControllerAFRegionChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera.MeteringRect>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera.MeteringRect>> paramAnonymousPropertyChangeEventArgs)
    {
      paramAnonymousPropertySource = (List)paramAnonymousPropertyChangeEventArgs.getOldValue();
      paramAnonymousPropertyKey = (List)paramAnonymousPropertyChangeEventArgs.getNewValue();
      if (((paramAnonymousPropertySource != null) && (!paramAnonymousPropertySource.isEmpty())) || (paramAnonymousPropertyKey.isEmpty())) {
        return;
      }
      TutorialUI.-wrap1(TutorialUI.this, paramAnonymousPropertyKey);
    }
  };
  private ValueAnimator m_ImmersiveModeAnimator;
  private boolean m_IsZoomValueAnimation;
  private Handle m_LockRotationHandle;
  private final PropertyChangedCallback<Boolean> m_SelfTimerStartedChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
        TutorialUI.-wrap0(TutorialUI.this, true);
      }
    }
  };
  private final EventHandler<EventArgs> m_SwipeToSwitchSimpleCaptureModeHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<EventArgs> paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
    {
      TutorialUI.-wrap6(TutorialUI.this);
    }
  };
  private ViewGroup m_TutorialUIContainer;
  private Viewfinder m_ViewFinder;
  private int m_ZoomValueAnimationCounter;
  private final EventHandler<EventArgs> m_ZoomValueClickHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<EventArgs> paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
    {
      TutorialUI.-wrap8(TutorialUI.this);
    }
  };
  private final EventHandler<EventArgs> m_ZoomValueDragedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<EventArgs> paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
    {
      TutorialUI.-wrap9(TutorialUI.this);
    }
  };
  private View m_ZoomValueFadeOutImageView;
  private View m_ZoomValueImageView;
  private final EventHandler<EventArgs> m_ZoomValueLongClickHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<EventArgs> paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
    {
      TutorialUI.-wrap9(TutorialUI.this);
    }
  };
  
  public TutorialUI(CameraActivity paramCameraActivity)
  {
    super("Tutorial UI", paramCameraActivity, true);
  }
  
  private boolean canSwipeForVideoAndPortrait()
  {
    if ((this.m_CaptureModeManager == null) || (this.m_CaptureModeSwitcher == null)) {
      return false;
    }
    CameraActivity localCameraActivity = getCameraActivity();
    boolean bool1 = ((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue();
    if (getCamera().get(Camera.PROP_LENS_FACING) == Camera.LensFacing.BACK) {}
    boolean bool2;
    CaptureMode localCaptureMode;
    int j;
    for (int i = 1;; i = 0)
    {
      bool2 = ((Boolean)this.m_CaptureModeSwitcher.get(CaptureModeSwitcher.PROP_IS_CAPTURE_MODE_PANEL_OPEN)).booleanValue();
      localCaptureMode = (CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE);
      j = 0;
      localObject = (List)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODES);
      if (localObject != null) {
        break;
      }
      return false;
    }
    Object localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      if (((CaptureMode)((Iterator)localObject).next() instanceof BokehCaptureMode)) {
        j = 1;
      }
    }
    if ((!bool1) || (i == 0) || (j == 0) || (bool2)) {}
    while ((!(localCaptureMode instanceof PhotoCaptureMode)) || (PreferenceManager.getDefaultSharedPreferences(localCameraActivity).getBoolean("TutorialSwipeLeftRight", false))) {
      return false;
    }
    return true;
  }
  
  private void doDragFocalPointAnimation(final float paramFloat1, final float paramFloat2)
  {
    if ((this.m_FocalPointWhiteView == null) || (this.m_FocalPointFocusView == null)) {
      return;
    }
    this.m_FocalPointFocusView.setScaleX(1.0F);
    this.m_FocalPointFocusView.setScaleY(1.0F);
    this.m_FocalPointWhiteView.setScaleX(0.9F);
    this.m_FocalPointWhiteView.setScaleY(0.9F);
    this.m_FocalPointFocusView.animate().scaleX(0.7F).scaleY(0.7F).setDuration(400L).withEndAction(new Runnable()
    {
      public void run()
      {
        TutorialUI.-get1(TutorialUI.this).animate().translationX(paramFloat1).translationY(paramFloat2).setInterpolator(new DecelerateInterpolator(2.0F)).setDuration(1000L).withEndAction(new Runnable()
        {
          public void run()
          {
            TutorialUI.-get1(TutorialUI.this).animate().scaleX(1.0F).scaleY(1.0F).alpha(0.0F).setDuration(400L).withEndAction(new Runnable()
            {
              public void run()
              {
                TutorialUI.-wrap0(TutorialUI.this, true);
              }
            }).start();
          }
        }).start();
        TutorialUI.-get2(TutorialUI.this).animate().translationX(paramFloat1).translationY(paramFloat2).setInterpolator(new DecelerateInterpolator(2.0F)).setDuration(1000L).start();
      }
    }).start();
  }
  
  private void doEnterExitImmersiveModeAnimation(final View paramView, final int paramInt)
  {
    paramView = paramView.findViewById(2131362080);
    if (this.m_ImmersiveModeAnimator != null) {
      this.m_ImmersiveModeAnimator.cancel();
    }
    this.m_ImmersiveModeAnimator = ValueAnimator.ofFloat(new float[] { 0.0F, 1.0F }).setDuration(1667L);
    this.m_ImmersiveModeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
    {
      public void onAnimationUpdate(ValueAnimator paramAnonymousValueAnimator)
      {
        float f = ((Float)paramAnonymousValueAnimator.getAnimatedValue()).floatValue();
        paramView.setTranslationY(paramInt * f);
      }
    });
    this.m_ImmersiveModeAnimator.setInterpolator(new DecelerateInterpolator(2.0F));
    this.m_ImmersiveModeAnimator.setRepeatCount(3);
    this.m_ImmersiveModeAnimator.start();
  }
  
  private void doZoomAnimation()
  {
    if ((this.m_ZoomValueImageView == null) || (this.m_ZoomValueFadeOutImageView == null)) {
      return;
    }
    this.m_ZoomValueImageView.setTranslationX(0.0F);
    this.m_ZoomValueImageView.setScaleX(1.0F);
    this.m_ZoomValueImageView.setScaleY(1.0F);
    this.m_ZoomValueFadeOutImageView.setScaleX(0.0F);
    this.m_ZoomValueFadeOutImageView.setScaleY(0.0F);
    this.m_ZoomValueFadeOutImageView.setAlpha(0.8F);
    this.m_ZoomValueFadeOutImageView.setVisibility(0);
    this.m_ZoomValueImageView.animate().scaleX(0.85F).scaleY(0.85F).setDuration(250L).withEndAction(new Runnable()
    {
      public void run()
      {
        TutorialUI.-get6(TutorialUI.this).animate().setInterpolator(new DecelerateInterpolator(2.0F)).translationX(-300.0F).setDuration(1000L).withEndAction(new Runnable()
        {
          public void run()
          {
            TutorialUI.-get6(TutorialUI.this).animate().scaleX(1.0F).scaleY(1.0F).setDuration(250L).withEndAction(new Runnable()
            {
              public void run()
              {
                TutorialUI localTutorialUI = TutorialUI.this;
                TutorialUI.-set0(localTutorialUI, TutorialUI.-get4(localTutorialUI) + 1);
                if (TutorialUI.-get4(TutorialUI.this) < 3)
                {
                  HandlerUtils.sendMessage(TutorialUI.this, 50002, true, 300L);
                  return;
                }
                TutorialUI.-wrap7(TutorialUI.this);
              }
            }).start();
          }
        }).start();
      }
    }).start();
    this.m_ZoomValueFadeOutImageView.animate().scaleX(0.85F).scaleY(0.85F).setDuration(250L).alpha(0.0F).withEndAction(new Runnable()
    {
      public void run()
      {
        TutorialUI.-get5(TutorialUI.this).setVisibility(8);
      }
    }).start();
  }
  
  private void hideTutorialUIContainer(boolean paramBoolean)
  {
    this.m_LockRotationHandle = Handle.close(this.m_LockRotationHandle);
    if (this.m_TutorialUIContainer != null)
    {
      if (!paramBoolean) {
        break label153;
      }
      setViewVisibility(this.m_TutorialUIContainer, false, 200L, null, new ViewUtils.AnimationCompletedCallback()
      {
        public void onAnimationCompleted(View paramAnonymousView, boolean paramAnonymousBoolean)
        {
          TutorialUI.-get3(TutorialUI.this).setVisibility(8);
          TutorialUI.-get3(TutorialUI.this).removeAllViews();
        }
      });
    }
    for (;;)
    {
      HandlerUtils.removeMessages(this, 50001);
      Log.d(this.TAG, "hideTutorialUIContainer()");
      if (this.m_ImmersiveModeAnimator != null) {
        this.m_ImmersiveModeAnimator.cancel();
      }
      if (this.m_ZoomValueImageView != null) {
        this.m_ZoomValueImageView.animate().cancel();
      }
      if (this.m_ZoomValueFadeOutImageView != null) {
        this.m_ZoomValueFadeOutImageView.animate().cancel();
      }
      this.m_IsZoomValueAnimation = false;
      HandlerUtils.removeMessages(this, 50002);
      if (this.m_FocalPointFocusView != null) {
        this.m_FocalPointFocusView.animate().cancel();
      }
      if (this.m_FocalPointWhiteView != null) {
        this.m_FocalPointWhiteView.animate().cancel();
      }
      return;
      label153:
      this.m_TutorialUIContainer.setVisibility(8);
      this.m_TutorialUIContainer.removeAllViews();
    }
  }
  
  private View inflateLayoutAndSetTouchReceiver(int paramInt, final boolean paramBoolean)
  {
    this.m_TutorialUIContainer.removeAllViews();
    View localView = ((ViewGroup)getCameraActivity().getLayoutInflater().inflate(paramInt, this.m_TutorialUIContainer)).getChildAt(this.m_TutorialUIContainer.getChildCount() - 1);
    localView.findViewById(2131362068).setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        if (paramBoolean) {
          TutorialUI.-wrap0(TutorialUI.this, true);
        }
        return false;
      }
    });
    return localView;
  }
  
  private void lockPortraitAndShowContainer(CameraActivity paramCameraActivity)
  {
    this.m_LockRotationHandle = Handle.close(this.m_LockRotationHandle);
    this.m_LockRotationHandle = paramCameraActivity.lockRotation(Rotation.PORTRAIT);
    if (this.m_TutorialUIContainer != null) {
      setViewVisibility(this.m_TutorialUIContainer, true, 200L, null);
    }
  }
  
  private void onAFRegionChanged(List<Camera.MeteringRect> paramList)
  {
    if ((this.m_CaptureModeManager == null) || (this.m_ViewFinder == null)) {}
    while ((paramList == null) || (paramList.isEmpty())) {
      return;
    }
    if (!((CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE) instanceof ManualCaptureMode)) {
      return;
    }
    Object localObject = getCameraActivity();
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences((Context)localObject);
    float f1;
    float f3;
    float f4;
    if (!localSharedPreferences.getBoolean("TutorialDragFocalPoint", false))
    {
      View localView = inflateLayoutAndSetTouchReceiver(2130903098, true);
      lockPortraitAndShowContainer((CameraActivity)localObject);
      this.m_FocalPointFocusView = localView.findViewById(2131362073);
      this.m_FocalPointWhiteView = localView.findViewById(2131362072);
      localSharedPreferences.edit().putBoolean("TutorialDragFocalPoint", true).apply();
      f1 = ((CameraActivity)localObject).getResources().getDimensionPixelSize(2131296672) / 2.0F;
      localObject = ((Camera.MeteringRect)paramList.get(0)).getRect();
      paramList = new PointF();
      this.m_ViewFinder.pointFromPreview(((RectF)localObject).centerX(), ((RectF)localObject).centerY(), paramList, 0);
      localObject = (RectF)this.m_ViewFinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
      f3 = paramList.x - f1;
      f4 = paramList.y - f1;
      this.m_FocalPointWhiteView.setTranslationX(f3);
      this.m_FocalPointWhiteView.setTranslationY(f4);
      this.m_FocalPointFocusView.setTranslationX(f3);
      this.m_FocalPointFocusView.setTranslationY(f4);
      if (f3 <= ((RectF)localObject).centerX()) {
        break label421;
      }
      f1 = f3 - 300.0F;
      if (f4 <= ((RectF)localObject).centerY()) {
        break label431;
      }
    }
    label421:
    label431:
    for (float f2 = f4 - 300.0F;; f2 = f4 + 300.0F)
    {
      doDragFocalPointAnimation(f1, f2);
      Log.d(this.TAG, "onAFRegionChanged() - touch x:" + paramList.x + ",y:" + paramList.y + ", translatyion x:" + f3 + ",y:" + f4 + ",preview x:" + ((RectF)localObject).centerX() + ",y:" + ((RectF)localObject).centerY() + ",ax:" + f1 + ",ay:" + f2);
      return;
      f1 = f3 + 300.0F;
      break;
    }
  }
  
  private void onCustomModeSettingChanged(int paramInt)
  {
    Log.d(this.TAG, "onCustomModeSettingChanged() - newSetting:" + paramInt);
    CameraActivity localCameraActivity = getCameraActivity();
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(localCameraActivity);
    if (!localSharedPreferences.getBoolean("TutorialSaveCustomButton", false))
    {
      View localView = inflateLayoutAndSetTouchReceiver(2130903105, true);
      lockPortraitAndShowContainer(localCameraActivity);
      setConfirmTextListener(localView);
      setEmptyTouchListener(localView, 2131362077);
      localSharedPreferences.edit().putBoolean("TutorialSaveCustomButton", true).apply();
      Log.d(this.TAG, "onCustomModeSettingChanged() - save to shared preference.");
    }
  }
  
  private void onEnterCustomMode()
  {
    Log.d(this.TAG, "onEnterCustomMode() - ");
    CameraActivity localCameraActivity = getCameraActivity();
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(localCameraActivity);
    if (!localSharedPreferences.getBoolean("TutorialEnterCustomMode", false))
    {
      View localView = inflateLayoutAndSetTouchReceiver(2130903100, true);
      lockPortraitAndShowContainer(localCameraActivity);
      setConfirmTextListener(localView);
      setEmptyTouchListener(localView, 2131362077);
      localSharedPreferences.edit().putBoolean("TutorialEnterCustomMode", true).apply();
      Log.d(this.TAG, "onEnterCustomMode() - save to shared preference.");
    }
  }
  
  private void onEnterImmersiveMode()
  {
    if (this.m_CaptureModeManager == null) {
      return;
    }
    CameraActivity localCameraActivity = getCameraActivity();
    boolean bool = ((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue();
    Object localObject = (CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE);
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(localCameraActivity);
    if ((getSettings().getBoolean("ManualModeUI.IsSimpleUIModeEnabled", false)) && (bool) && ((localObject instanceof ManualCaptureMode)) && (!localSharedPreferences.getBoolean("TutorialEnterImmersive", false)))
    {
      localObject = inflateLayoutAndSetTouchReceiver(2130903101, true);
      lockPortraitAndShowContainer(localCameraActivity);
      doEnterExitImmersiveModeAnimation((View)localObject, 400);
      localSharedPreferences.edit().putBoolean("TutorialEnterImmersive", true).apply();
      HandlerUtils.sendMessage(this, 50001, true, 5000L);
      Log.d(this.TAG, "onEnterImmersiveMode() - save to shared preference.");
    }
  }
  
  private void onExitImmersiveMode()
  {
    CameraActivity localCameraActivity = getCameraActivity();
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(localCameraActivity);
    if (!localSharedPreferences.getBoolean("TutorialExitImmersive", false))
    {
      View localView = inflateLayoutAndSetTouchReceiver(2130903102, true);
      lockPortraitAndShowContainer(localCameraActivity);
      doEnterExitImmersiveModeAnimation(localView, 65136);
      localSharedPreferences.edit().putBoolean("TutorialExitImmersive", true).apply();
      HandlerUtils.sendMessage(this, 50001, true, 5000L);
      Log.d(this.TAG, "onExitImmersiveMode() - save to shared preference.");
    }
  }
  
  private void onSwipeToSwitchSimpleCaptureMode()
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getCameraActivity());
    if (!localSharedPreferences.getBoolean("TutorialSwipeLeftRight", false))
    {
      localSharedPreferences.edit().putBoolean("TutorialSwipeLeftRight", true).apply();
      Log.d(this.TAG, "onSwipeToSwitchSimpleCaptureMode() - save to shared preference.");
    }
  }
  
  private void onZoomValueAnimationEnd()
  {
    Log.d(this.TAG, "onZoomValueAnimationEnd() - already do animation " + this.m_ZoomValueAnimationCounter + " times.");
    hideTutorialUIContainer(true);
  }
  
  private void onZoomValueClick()
  {
    Log.d(this.TAG, "onZoomValueClick() - ");
    CameraActivity localCameraActivity = getCameraActivity();
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(localCameraActivity);
    if (!localSharedPreferences.getBoolean("TutorialDragZoomValue", false))
    {
      View localView = inflateLayoutAndSetTouchReceiver(2130903099, false);
      lockPortraitAndShowContainer(localCameraActivity);
      this.m_ZoomValueImageView = localView.findViewById(2131362074);
      this.m_ZoomValueFadeOutImageView = localView.findViewById(2131362075);
      localSharedPreferences.edit().putBoolean("TutorialDragZoomValue", true).apply();
      HandlerUtils.sendMessage(this, 50001, true, 5000L);
      this.m_IsZoomValueAnimation = true;
      doZoomAnimation();
      Log.d(this.TAG, "onZoomValueClick() - save to shared preference.");
    }
  }
  
  private void onZoomValueDragedorLongClick()
  {
    Log.d(this.TAG, "onZoomValueDraggedorLongClick()");
    if (this.m_IsZoomValueAnimation)
    {
      onZoomValueAnimationEnd();
      return;
    }
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getCameraActivity());
    if (localSharedPreferences.getBoolean("TutorialDragZoomValue", false)) {
      return;
    }
    localSharedPreferences.edit().putBoolean("TutorialDragZoomValue", true).apply();
    Log.d(this.TAG, "onZoomValueDraggedorLongClick() - save to shared preference.");
  }
  
  private void setConfirmTextListener(View paramView)
  {
    setEmptyTouchListener(paramView, 2131362078);
    ((TextView)paramView.findViewById(2131362071)).setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        Log.v(TutorialUI.-get0(TutorialUI.this), "setConfirmTextListener() - click ok.");
        TutorialUI.-wrap0(TutorialUI.this, true);
      }
    });
  }
  
  private void setEmptyTouchListener(View paramView, int paramInt)
  {
    paramView.findViewById(paramInt).setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
  }
  
  private void setIconShadowDrawable(ImageView paramImageView, int paramInt)
  {
    if (paramImageView == null) {
      return;
    }
    CameraActivity localCameraActivity = getCameraActivity();
    ShadowDrawable localShadowDrawable = new ShadowDrawable(getCameraActivity(), paramInt, 2131492939);
    paramInt = localCameraActivity.getResources().getInteger(2131427357);
    localShadowDrawable.setPaddings(paramInt, paramInt, paramInt, paramInt);
    paramImageView.setImageDrawable(localShadowDrawable);
  }
  
  private void showBokehTutorial()
  {
    if (this.m_CaptureModeManager == null) {
      return;
    }
    Object localObject1 = getCameraActivity();
    boolean bool = ((Boolean)((CameraActivity)localObject1).get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue();
    if (getCamera().get(Camera.PROP_LENS_FACING) == Camera.LensFacing.BACK) {}
    Object localObject2;
    int j;
    for (int i = 1;; i = 0)
    {
      localObject2 = (CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE);
      j = 0;
      localObject3 = (List)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODES);
      if (localObject3 != null) {
        break;
      }
      return;
    }
    Object localObject3 = ((Iterable)localObject3).iterator();
    while (((Iterator)localObject3).hasNext()) {
      if (((CaptureMode)((Iterator)localObject3).next() instanceof BokehCaptureMode)) {
        j = 1;
      }
    }
    if ((bool) && (i != 0) && (j != 0) && ((localObject2 instanceof BokehCaptureMode)))
    {
      localObject2 = PreferenceManager.getDefaultSharedPreferences((Context)localObject1);
      if (!((SharedPreferences)localObject2).getBoolean("TutorialBokeh", false))
      {
        localObject3 = inflateLayoutAndSetTouchReceiver(2130903097, true);
        lockPortraitAndShowContainer((CameraActivity)localObject1);
        setEmptyTouchListener((View)localObject3, 2131362069);
        ((TextView)((View)localObject3).findViewById(2131362071)).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            Log.v(TutorialUI.-get0(TutorialUI.this), "showBokehTutorial() - click ok.");
            TutorialUI.-wrap0(TutorialUI.this, true);
          }
        });
        localObject1 = (AnimationDrawable)((CameraActivity)localObject1).getDrawable(2130838239);
        ((ImageView)((View)localObject3).findViewById(2131362069)).setImageDrawable((Drawable)localObject1);
        ((AnimationDrawable)localObject1).setOneShot(true);
        ((AnimationDrawable)localObject1).start();
        ((SharedPreferences)localObject2).edit().putBoolean("TutorialBokeh", true).apply();
        Log.d(this.TAG, "showBokehTutorial() - save to shared preference.");
      }
    }
  }
  
  private void showPanoramaTutorial()
  {
    if (this.m_CaptureModeManager == null) {
      return;
    }
    CameraActivity localCameraActivity = getCameraActivity();
    boolean bool = ((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue();
    if (getCamera().get(Camera.PROP_LENS_FACING) == Camera.LensFacing.BACK) {}
    for (int i = 1;; i = 0)
    {
      Object localObject = (CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE);
      if ((bool) && (i != 0) && ((localObject instanceof PanoramaCaptureMode)))
      {
        localObject = PreferenceManager.getDefaultSharedPreferences(localCameraActivity);
        if (!((SharedPreferences)localObject).getBoolean("TutorialPanorama", false))
        {
          inflateLayoutAndSetTouchReceiver(2130903104, true);
          lockPortraitAndShowContainer(localCameraActivity);
          ((SharedPreferences)localObject).edit().putBoolean("TutorialPanorama", true).apply();
          HandlerUtils.sendMessage(this, 50001, true, 5000L);
          Log.d(this.TAG, "showPanoramaTutorial() - save to shared preference.");
        }
      }
      return;
    }
  }
  
  private void showSwipeForVideoPortraitTutorial()
  {
    if (!canSwipeForVideoAndPortrait()) {
      return;
    }
    CameraActivity localCameraActivity = getCameraActivity();
    View localView = inflateLayoutAndSetTouchReceiver(2130903106, true);
    lockPortraitAndShowContainer(localCameraActivity);
    setIconShadowDrawable((ImageView)localView.findViewById(2131362094), 2130838245);
    setIconShadowDrawable((ImageView)localView.findViewById(2131362095), 2130838246);
    PreferenceManager.getDefaultSharedPreferences(localCameraActivity).edit().putBoolean("TutorialSwipeLeftRight", true).apply();
    HandlerUtils.sendMessage(this, 50001, true, 5000L);
    Log.d(this.TAG, "showSwipeForVideoPortraitTutorial() - save to shared preference.");
  }
  
  private void updateFrontCameraTutorial()
  {
    if (this.m_CaptureModeManager == null) {
      return;
    }
    Object localObject2 = getCameraActivity();
    boolean bool = ((Boolean)((CameraActivity)localObject2).get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue();
    if (getCamera().get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT) {}
    for (int i = 1;; i = 0)
    {
      Object localObject1 = (CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE);
      if ((bool) && (i != 0) && ((localObject1 instanceof PhotoCaptureMode)))
      {
        localObject1 = PreferenceManager.getDefaultSharedPreferences((Context)localObject2);
        if (!((SharedPreferences)localObject1).getBoolean("TutorialFrontCamera", false))
        {
          View localView1 = inflateLayoutAndSetTouchReceiver(2130903103, true);
          lockPortraitAndShowContainer((CameraActivity)localObject2);
          setConfirmTextListener(localView1);
          View localView2 = localView1.findViewById(2131362081);
          localObject2 = new ColorDrawable(((CameraActivity)localObject2).getResources().getColor(2131230838, null));
          Rect localRect1 = new Rect();
          Rect localRect2 = new Rect();
          Rect localRect3 = new Rect();
          this.m_CaptureBar.getIconButtonRect(CaptureBar.IconButton.FACE_BEAUTY, localRect1);
          this.m_CaptureBar.getIconButtonRect(CaptureBar.IconButton.SMILE_CAPTURE, localRect2);
          this.m_CaptureBar.getIconButtonRect(CaptureBar.IconButton.FLASH, localRect3);
          localRect1.inset(9, 9);
          localRect2.inset(9, 9);
          localRect3.inset(9, 9);
          localView2.setBackground(new HollowDrawable((Drawable)localObject2, new Rect[] { localRect1, localRect2, localRect3 }));
          setIconShadowDrawable((ImageView)localView1.findViewById(2131362085), 2130838241);
          setIconShadowDrawable((ImageView)localView1.findViewById(2131362087), 2130838244);
          setIconShadowDrawable((ImageView)localView1.findViewById(2131362089), 2130838242);
          ((SharedPreferences)localObject1).edit().putBoolean("TutorialFrontCamera", true).apply();
          Log.d(this.TAG, "updateFrontCameraTutorial() - show front camera tutorial");
        }
      }
      return;
    }
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case 50001: 
      hideTutorialUIContainer(true);
      Log.d(this.TAG, "handleMessage() - MSG_HIDE_TUTORIAL_CONTAINER");
      return;
    }
    doZoomAnimation();
    Log.d(this.TAG, "handleMessage() - MSG_SHOW_ZOOM_ANIMATION");
  }
  
  protected void onDeinitialize()
  {
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    Object localObject = getCameraActivity();
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_IS_READY_TO_CAPTURE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (!((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          TutorialUI.-wrap0(TutorialUI.this, true);
        }
        TutorialUI.-wrap13(TutorialUI.this);
        TutorialUI.-wrap12(TutorialUI.this);
        TutorialUI.-wrap10(TutorialUI.this);
        TutorialUI.-wrap4(TutorialUI.this);
        TutorialUI.-wrap11(TutorialUI.this);
      }
    });
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_IS_SIMPLE_UI_MODE_ENTERED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        Log.d(TutorialUI.-get0(TutorialUI.this), "onPropertyChanged() - PROP_IS_SIMPLE_UI_MODE_ENTERED, old:" + paramAnonymousPropertyChangeEventArgs.getOldValue() + ",new:" + paramAnonymousPropertyChangeEventArgs.getNewValue());
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue())
        {
          TutorialUI.-wrap5(TutorialUI.this);
          return;
        }
        TutorialUI.-wrap4(TutorialUI.this);
      }
    });
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_IS_SELF_TIMER_STARTED, this.m_SelfTimerStartedChangedCallback);
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_STATE, this.m_CameraActivityStateChangedCallback);
    this.m_TutorialUIContainer = ((RelativeLayout)((CameraActivity)localObject).findViewById(2131361838));
    localObject = (ZoomBar)findComponent(ZoomBar.class);
    ((ZoomBar)localObject).addHandler(ZoomBar.EVENT_ZOOM_VALUE_CLICK, this.m_ZoomValueClickHandler);
    ((ZoomBar)localObject).addHandler(ZoomBar.EVENT_ZOOM_VALUE_DRAGED, this.m_ZoomValueDragedHandler);
    ((ZoomBar)localObject).addHandler(ZoomBar.EVENT_ZOOM_VALUE_LONG_CLICK, this.m_ZoomValueLongClickHandler);
    this.m_CaptureBar = ((CaptureBar)findComponent(CaptureBar.class));
    this.m_CaptureBar.addHandler(CaptureBar.EVENT_CUSTOM_MODE_CLICK, this.m_CustomModeClickHandler);
    ((ManualModeUI)findComponent(ManualModeUI.class)).addCallback(ManualModeUI.PROP_MANUAL_MODE_CUSTOM_SETTING, this.m_CustomModeSettingChangedCallback);
    this.m_CaptureModeSwitcher = ((CaptureModeSwitcher)findComponent(CaptureModeSwitcher.class));
    this.m_CaptureModeSwitcher.addHandler(CaptureModeSwitcher.EVENT_SWIPE_TO_SWITCH_SIMPLE_CAPTURE_MODE, this.m_SwipeToSwitchSimpleCaptureModeHandler);
    this.m_CaptureModeSwitcher.addCallback(CaptureModeSwitcher.PROP_IS_CAPTURE_MODE_PANEL_OPEN, this.m_CaptureModePanelChangedCallback);
    ((FocusController)findComponent(FocusController.class)).addCallback(FocusController.PROP_AF_REGIONS, this.m_FocusControllerAFRegionChangedCallback);
    this.m_ViewFinder = ((Viewfinder)findComponent(Viewfinder.class));
    ((CameraGallery)findComponent(CameraGallery.class)).addCallback(CameraGallery.PROP_UI_STATE, this.m_CameraGalleryUIStateChangedCallback);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/TutorialUI.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */