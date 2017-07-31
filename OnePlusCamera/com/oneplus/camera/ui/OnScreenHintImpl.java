package com.oneplus.camera.ui;

import android.content.res.Resources;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewPropertyAnimator;
import android.view.ViewStub;
import android.widget.TextView;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.PhotoCaptureState;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.UIComponent.ViewRotationCallback;
import com.oneplus.camera.ZoomBar;
import com.oneplus.camera.ZoomController;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.widget.RotateRelativeLayout;
import com.oneplus.util.AspectRatio;
import com.oneplus.util.ListUtils;
import com.oneplus.widget.ViewUtils;
import java.util.Iterator;
import java.util.LinkedList;

final class OnScreenHintImpl
  extends UIComponent
  implements OnScreenHint
{
  private static final long DURATION_SCREEN_HINT_INVISIBLE = 1000L;
  private static final long DURATION_SHOW_ZOOM_WHEEL = 200L;
  private static final int MSG_SHOW_SCREEN_HINT = 10000;
  private CaptureModeManager m_CaptureModeManager;
  private RotateRelativeLayout m_Container;
  private final UIComponent.ViewRotationCallback m_ContainerRotationCallback = new UIComponent.ViewRotationCallback()
  {
    public void onRotated(View paramAnonymousView, Rotation paramAnonymousRotation)
    {
      if (((Boolean)OnScreenHintImpl.this.get(OnScreenHintImpl.PROP_IS_VISIBLE)).booleanValue()) {
        OnScreenHintImpl.-wrap5(OnScreenHintImpl.this, OnScreenHintImpl.-get0(OnScreenHintImpl.this), true);
      }
      for (;;)
      {
        OnScreenHintImpl.-wrap7(OnScreenHintImpl.this, false);
        return;
        OnScreenHintImpl.-wrap5(OnScreenHintImpl.this, OnScreenHintImpl.-get0(OnScreenHintImpl.this), false);
      }
    }
  };
  private LinkedList<HintHandle> m_HintHandles = new LinkedList();
  private View m_HintTextContainer;
  private TextView m_HintTextView;
  private LinkedList<HintHandle> m_SecondaryHintHandles = new LinkedList();
  private TextView m_SecondaryHintTextView;
  private PropertyChangedCallback<Boolean> m_UpdateHintMarginCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      if ((paramAnonymousPropertyChangeEventArgs.getNewValue() == paramAnonymousPropertyChangeEventArgs.getOldValue()) || (OnScreenHintImpl.-get1(OnScreenHintImpl.this).isEmpty())) {
        return;
      }
      OnScreenHintImpl.-wrap7(OnScreenHintImpl.this, true);
    }
  };
  private Viewfinder m_Viewfinder;
  private ZoomBar m_ZoomBar;
  private ZoomController m_ZoomController;
  private int m_ZoomWheelHeight;
  
  OnScreenHintImpl(CameraActivity paramCameraActivity)
  {
    super("On-screen hint", paramCameraActivity, true);
  }
  
  private boolean canShowHint()
  {
    return getCameraActivity().get(CameraActivity.PROP_PHOTO_CAPTURE_STATE) != PhotoCaptureState.REVIEWING;
  }
  
  private void hideHint(HintHandle paramHintHandle)
  {
    verifyAccess();
    boolean bool = ListUtils.isLastObject(this.m_HintHandles, paramHintHandle);
    if ((this.m_HintHandles.remove(paramHintHandle)) && (bool)) {
      if ((this.m_HintHandles.isEmpty()) || (!showHint((HintHandle)this.m_HintHandles.getLast()))) {
        break label57;
      }
    }
    label57:
    while (!this.m_SecondaryHintHandles.isEmpty())
    {
      return;
      return;
    }
    setViewVisibility(this.m_Container, false);
    setReadOnly(PROP_IS_VISIBLE, Boolean.valueOf(false));
  }
  
  private void hideSecondaryHint(HintHandle paramHintHandle)
  {
    verifyAccess();
    boolean bool = ListUtils.isLastObject(this.m_SecondaryHintHandles, paramHintHandle);
    if ((this.m_SecondaryHintHandles.remove(paramHintHandle)) && (bool)) {
      if ((this.m_SecondaryHintHandles.isEmpty()) || (!showSecondaryHint((HintHandle)this.m_SecondaryHintHandles.getLast()))) {
        break label57;
      }
    }
    label57:
    do
    {
      return;
      return;
      this.m_SecondaryHintTextView.setVisibility(8);
    } while (!this.m_HintHandles.isEmpty());
    setViewVisibility(this.m_Container, false);
    setReadOnly(PROP_IS_VISIBLE, Boolean.valueOf(false));
  }
  
  private void setupUI()
  {
    if (this.m_Container != null) {
      return;
    }
    if (this.m_Container == null)
    {
      this.m_Container = ((RotateRelativeLayout)((ViewStub)getCameraActivity().findViewById(2131361957)).inflate());
      this.m_HintTextContainer = this.m_Container.findViewById(2131362009);
      this.m_HintTextView = ((TextView)this.m_Container.findViewById(2131362011));
      this.m_HintTextView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          if (OnScreenHintImpl.-get1(OnScreenHintImpl.this).isEmpty()) {
            return false;
          }
          OnScreenHintImpl.HintHandle localHintHandle = (OnScreenHintImpl.HintHandle)OnScreenHintImpl.-get1(OnScreenHintImpl.this).getLast();
          if (localHintHandle.clickListener == null) {
            return false;
          }
          float f1 = paramAnonymousMotionEvent.getX();
          float f2 = paramAnonymousMotionEvent.getY();
          if ((f1 < 0.0F) || (f1 > paramAnonymousView.getWidth())) {}
          while ((f2 < 0.0F) || (f2 > paramAnonymousView.getHeight())) {
            return false;
          }
          if (paramAnonymousMotionEvent.getAction() == 1)
          {
            paramAnonymousView.playSoundEffect(0);
            localHintHandle.clickListener.onClick(paramAnonymousView);
          }
          return true;
        }
      });
      this.m_SecondaryHintTextView = ((TextView)this.m_Container.findViewById(2131362010));
      addNavBarAlignedView(this.m_Container);
      if (this.m_Viewfinder != null) {
        updateBaseViewLayout((RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS));
      }
    }
  }
  
  private Handle showHint(Object paramObject, View.OnClickListener paramOnClickListener, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing(true)) {
      return null;
    }
    paramObject = new HintHandle(paramObject, paramOnClickListener, false, paramInt);
    if ((paramInt & 0x8) == 0)
    {
      paramInt = 0;
      paramOnClickListener = this.m_HintHandles.iterator();
      if ((!paramOnClickListener.hasNext()) || ((((HintHandle)paramOnClickListener.next()).flags & 0x8) != 0)) {
        this.m_HintHandles.add(paramInt, paramObject);
      }
    }
    for (;;)
    {
      if ((this.m_HintHandles.getLast() == paramObject) && (showHint((HintHandle)paramObject))) {
        setReadOnly(PROP_IS_VISIBLE, Boolean.valueOf(true));
      }
      return (Handle)paramObject;
      paramInt += 1;
      break;
      this.m_HintHandles.add(paramObject);
    }
  }
  
  private boolean showHint(HintHandle paramHintHandle)
  {
    long l = 0L;
    if (!canShowHint()) {
      return false;
    }
    Object localObject = getCameraActivity();
    if ((!((Boolean)((CameraActivity)localObject).get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue()) && ((paramHintHandle.flags & 0x1) == 0)) {
      return false;
    }
    if ((((Boolean)((CameraActivity)localObject).get(CameraActivity.PROP_IS_SELF_TIMER_STARTED)).booleanValue()) && ((paramHintHandle.flags & 0x10) != 0)) {
      return false;
    }
    setupUI();
    updateHintBackground(paramHintHandle);
    if (paramHintHandle.isDrawableContent())
    {
      this.m_HintTextView.setText(null);
      if ((paramHintHandle.flags & 0x2) != 0) {
        break label190;
      }
    }
    label190:
    for (int i = 1;; i = 0)
    {
      updateHintMarginBottom(false);
      updateHintTextAppearance();
      rotateLayout(this.m_Container, 0L);
      paramHintHandle = this.m_Container;
      if (i != 0) {
        l = 600L;
      }
      setViewVisibility(paramHintHandle, true, l, INTERPOLATOR_FADE_IN);
      return true;
      TextView localTextView = this.m_HintTextView;
      if (paramHintHandle.content != null) {}
      for (localObject = paramHintHandle.content.toString();; localObject = null)
      {
        localTextView.setText((CharSequence)localObject);
        break;
      }
    }
  }
  
  private Handle showSecondaryHint(Object paramObject, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing(true)) {
      return null;
    }
    paramObject = new HintHandle(paramObject, null, true, paramInt);
    if ((paramInt & 0x8) == 0)
    {
      paramInt = 0;
      Iterator localIterator = this.m_SecondaryHintHandles.iterator();
      if ((!localIterator.hasNext()) || ((((HintHandle)localIterator.next()).flags & 0x8) != 0)) {
        this.m_SecondaryHintHandles.add(paramInt, paramObject);
      }
    }
    for (;;)
    {
      if ((this.m_SecondaryHintHandles.getLast() == paramObject) && (showSecondaryHint((HintHandle)paramObject))) {
        setReadOnly(PROP_IS_VISIBLE, Boolean.valueOf(true));
      }
      return (Handle)paramObject;
      paramInt += 1;
      break;
      this.m_SecondaryHintHandles.add(paramObject);
    }
  }
  
  private boolean showSecondaryHint(HintHandle paramHintHandle)
  {
    long l = 0L;
    if (!canShowHint()) {
      return false;
    }
    Object localObject = getCameraActivity();
    if ((!((Boolean)((CameraActivity)localObject).get(CameraActivity.PROP_IS_READY_TO_CAPTURE)).booleanValue()) && ((paramHintHandle.flags & 0x1) == 0)) {
      return false;
    }
    if ((((Boolean)((CameraActivity)localObject).get(CameraActivity.PROP_IS_SELF_TIMER_STARTED)).booleanValue()) && ((paramHintHandle.flags & 0x10) != 0)) {
      return false;
    }
    setupUI();
    this.m_SecondaryHintTextView.setVisibility(0);
    updateHintBackground(paramHintHandle);
    TextView localTextView = this.m_SecondaryHintTextView;
    if (paramHintHandle.content != null)
    {
      localObject = paramHintHandle.content.toString();
      localTextView.setText((CharSequence)localObject);
      if ((paramHintHandle.flags & 0x2) != 0) {
        break label180;
      }
    }
    label180:
    for (int i = 1;; i = 0)
    {
      updateHintMarginBottom(false);
      updateHintTextAppearance();
      rotateLayout(this.m_Container, 0L);
      paramHintHandle = this.m_Container;
      if (i != 0) {
        l = 600L;
      }
      setViewVisibility(paramHintHandle, true, l, INTERPOLATOR_FADE_IN);
      return true;
      localObject = null;
      break;
    }
  }
  
  private void updateBaseViewLayout(RectF paramRectF)
  {
    if ((this.m_Container == null) || (paramRectF == null)) {
      return;
    }
    int i = getCameraActivity().getResources().getDimensionPixelSize(2131296337);
    int j = Math.max(getScreenSize().getHeight(), getScreenSize().getWidth());
    if (paramRectF.height() + i > j) {
      ViewUtils.setSize(this.m_Container, (int)paramRectF.width(), j - i);
    }
    for (;;)
    {
      ViewUtils.setMargins(this.m_Container, (int)paramRectF.left, (int)paramRectF.top, 0, 0);
      if ((!this.m_HintHandles.isEmpty()) || (!this.m_SecondaryHintHandles.isEmpty())) {
        break;
      }
      return;
      ViewUtils.setSize(this.m_Container, (int)paramRectF.width(), (int)paramRectF.height());
    }
    updateHintMarginBottom(false);
  }
  
  private void updateHintBackground(HintHandle paramHintHandle)
  {
    if (paramHintHandle.isSecondaryHint) {}
    for (TextView localTextView = this.m_SecondaryHintTextView; paramHintHandle.isDrawableContent(); localTextView = this.m_HintTextView)
    {
      localTextView.setPaddingRelative(0, 0, 0, 0);
      localTextView.setBackground((Drawable)paramHintHandle.content);
      return;
    }
    if ((paramHintHandle.flags & 0x4) == 0)
    {
      localTextView.setPaddingRelative(0, 0, 0, 0);
      localTextView.setBackground(null);
      return;
    }
    Resources localResources = getCameraActivity().getResources();
    localTextView.setPadding(localResources.getDimensionPixelSize(2131296466), localResources.getDimensionPixelSize(2131296468), localResources.getDimensionPixelSize(2131296467), localResources.getDimensionPixelSize(2131296465));
    if ((paramHintHandle.flags & 0x20) == 0)
    {
      localTextView.setBackgroundResource(2130837770);
      return;
    }
    localTextView.setBackgroundResource(2130837771);
  }
  
  private void updateHintMarginBottom(boolean paramBoolean)
  {
    if (this.m_Container == null) {
      return;
    }
    Object localObject;
    if (this.m_HintHandles.size() > 0) {
      localObject = (HintHandle)this.m_HintHandles.getLast();
    }
    while (localObject == null)
    {
      return;
      if (this.m_SecondaryHintHandles.size() > 0) {
        localObject = (HintHandle)this.m_SecondaryHintHandles.getLast();
      } else {
        localObject = null;
      }
    }
    Resources localResources = getCameraActivity().getResources();
    if ((localObject != null) && (((HintHandle)localObject).isDrawableContent())) {}
    for (int i = 0;; i = 1)
    {
      localObject = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
      localObject = AspectRatio.get(((RectF)localObject).width(), ((RectF)localObject).height());
      Rotation localRotation = getRotation();
      switch (-getcom-oneplus-base-RotationSwitchesValues()[localRotation.ordinal()])
      {
      default: 
        return;
      }
    }
    if (i != 0)
    {
      this.m_HintTextContainer.animate().translationY(-localResources.getDimensionPixelSize(2131296462)).setDuration(0L).start();
      return;
    }
    this.m_HintTextContainer.animate().translationY(-localResources.getDimensionPixelSize(2131296458)).setDuration(0L).start();
    return;
    int j;
    float f2;
    float f1;
    boolean bool;
    if (i != 0) {
      if (localObject == AspectRatio.RATIO_1x1)
      {
        j = localResources.getDimensionPixelSize(2131296462);
        f2 = j;
        f1 = f2;
        bool = paramBoolean;
        if (this.m_ZoomController != null)
        {
          if (!((Boolean)this.m_ZoomController.get(ZoomController.PROP_IS_ZOOM_LOCKED)).booleanValue()) {
            break label397;
          }
          f1 = f2;
          if (localObject != AspectRatio.RATIO_1x1) {
            f1 = f2 - localResources.getDimensionPixelSize(2131296472);
          }
          bool = false;
        }
      }
    }
    for (;;)
    {
      if (bool) {
        break label547;
      }
      this.m_HintTextContainer.animate().translationY(-f1).setDuration(0L).start();
      return;
      j = localResources.getDimensionPixelSize(2131296463);
      break;
      if (localObject == AspectRatio.RATIO_1x1) {}
      for (j = localResources.getDimensionPixelSize(2131296458);; j = localResources.getDimensionPixelSize(2131296459))
      {
        f2 = j;
        break;
      }
      label397:
      f1 = f2;
      bool = paramBoolean;
      if (this.m_ZoomBar != null) {
        if (((Boolean)this.m_ZoomBar.get(ZoomBar.PROP_IS_ZOOM_WHEEL_VISIBLE)).booleanValue())
        {
          if (localObject == AspectRatio.RATIO_1x1)
          {
            f1 = localResources.getDimensionPixelSize(2131296461);
            bool = paramBoolean;
          }
          else
          {
            f1 = this.m_ZoomWheelHeight + localResources.getDimensionPixelSize(2131296473) + localResources.getDimensionPixelSize(2131296539);
            bool = paramBoolean;
          }
        }
        else
        {
          f1 = f2;
          bool = paramBoolean;
          if (!((Boolean)this.m_ZoomBar.get(ZoomBar.PROP_IS_ZOOM_VALUE_VISIBLE)).booleanValue()) {
            if (i != 0)
            {
              f1 = localResources.getDimensionPixelSize(2131296462);
              bool = paramBoolean;
            }
            else
            {
              f1 = localResources.getDimensionPixelSize(2131296458);
              bool = paramBoolean;
            }
          }
        }
      }
    }
    label547:
    this.m_HintTextContainer.animate().translationY(-f1).setDuration(200L).start();
    return;
    if (localObject != AspectRatio.RATIO_1x1)
    {
      this.m_HintTextContainer.animate().translationY(-localResources.getDimensionPixelSize(2131296647)).setDuration(0L).start();
      return;
    }
    if (i != 0)
    {
      this.m_HintTextContainer.animate().translationY(-localResources.getDimensionPixelSize(2131296462)).setDuration(0L).start();
      return;
    }
    this.m_HintTextContainer.animate().translationY(-localResources.getDimensionPixelSize(2131296458)).setDuration(0L).start();
  }
  
  private void updateHintTextAppearance()
  {
    if (this.m_HintHandles.size() > 0)
    {
      localHintHandle = (HintHandle)this.m_HintHandles.getLast();
      if ((localHintHandle == null) || ((localHintHandle.flags & 0x40) != 0)) {
        break label96;
      }
      this.m_HintTextView.setTextAppearance(2131492912);
      label45:
      if (this.m_SecondaryHintHandles.size() <= 0) {
        break label109;
      }
    }
    label96:
    label109:
    for (HintHandle localHintHandle = (HintHandle)this.m_SecondaryHintHandles.getLast();; localHintHandle = null)
    {
      if ((localHintHandle == null) || ((localHintHandle.flags & 0x40) != 0)) {
        break label114;
      }
      this.m_SecondaryHintTextView.setTextAppearance(2131492912);
      return;
      localHintHandle = null;
      break;
      this.m_HintTextView.setTextAppearance(2131492913);
      break label45;
    }
    label114:
    this.m_SecondaryHintTextView.setTextAppearance(2131492913);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
    }
    do
    {
      return;
    } while (this.m_HintHandles.isEmpty());
    showHint((HintHandle)this.m_HintHandles.getLast());
  }
  
  public void hideScreenHint()
  {
    setViewVisibility(this.m_Container, false);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_Viewfinder = ((Viewfinder)findComponent(Viewfinder.class));
    this.m_ZoomController = ((ZoomController)findComponent(ZoomController.class));
    this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    findComponent(ZoomBar.class, new ComponentSearchCallback()
    {
      public void onComponentFound(ZoomBar paramAnonymousZoomBar)
      {
        OnScreenHintImpl.-set0(OnScreenHintImpl.this, paramAnonymousZoomBar);
        OnScreenHintImpl.-get4(OnScreenHintImpl.this).addCallback(ZoomBar.PROP_IS_ZOOM_VALUE_VISIBLE, OnScreenHintImpl.-get3(OnScreenHintImpl.this));
        OnScreenHintImpl.-get4(OnScreenHintImpl.this).addCallback(ZoomBar.PROP_IS_ZOOM_WHEEL_VISIBLE, OnScreenHintImpl.-get3(OnScreenHintImpl.this));
      }
    });
    CameraActivity localCameraActivity = getCameraActivity();
    this.m_ZoomWheelHeight = localCameraActivity.getResources().getDimensionPixelSize(2131296540);
    PropertyChangedCallback local4 = new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
      {
        if (!OnScreenHintImpl.-get1(OnScreenHintImpl.this).isEmpty())
        {
          if (OnScreenHintImpl.-wrap1(OnScreenHintImpl.this, (OnScreenHintImpl.HintHandle)OnScreenHintImpl.-get1(OnScreenHintImpl.this).getLast())) {
            OnScreenHintImpl.-wrap0(OnScreenHintImpl.this, OnScreenHintImpl.PROP_IS_VISIBLE, Boolean.valueOf(true));
          }
        }
        else {
          return;
        }
        OnScreenHintImpl.-wrap5(OnScreenHintImpl.this, OnScreenHintImpl.-get0(OnScreenHintImpl.this), false);
        OnScreenHintImpl.-wrap0(OnScreenHintImpl.this, OnScreenHintImpl.PROP_IS_VISIBLE, Boolean.valueOf(false));
      }
    };
    localCameraActivity.addCallback(CameraActivity.PROP_IS_SELF_TIMER_STARTED, local4);
    localCameraActivity.addCallback(CameraActivity.PROP_PHOTO_CAPTURE_STATE, local4);
    localCameraActivity.addCallback(CameraActivity.PROP_VIDEO_CAPTURE_STATE, local4);
    if (this.m_CaptureModeManager != null) {
      this.m_CaptureModeManager.addCallback(CaptureModeManager.PROP_CAPTURE_MODE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CaptureMode> paramAnonymousPropertyKey, PropertyChangeEventArgs<CaptureMode> paramAnonymousPropertyChangeEventArgs)
        {
          if ((OnScreenHintImpl.-get1(OnScreenHintImpl.this).isEmpty()) && (OnScreenHintImpl.-get2(OnScreenHintImpl.this).isEmpty())) {
            return;
          }
          OnScreenHintImpl.-wrap7(OnScreenHintImpl.this, false);
          OnScreenHintImpl.-wrap8(OnScreenHintImpl.this);
        }
      });
    }
    if (this.m_Viewfinder != null) {
      this.m_Viewfinder.addCallback(Viewfinder.PROP_PREVIEW_BOUNDS, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<RectF> paramAnonymousPropertyKey, PropertyChangeEventArgs<RectF> paramAnonymousPropertyChangeEventArgs)
        {
          OnScreenHintImpl.-wrap6(OnScreenHintImpl.this, (RectF)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
    }
    for (;;)
    {
      if (this.m_ZoomController != null) {
        this.m_ZoomController.addCallback(ZoomController.PROP_DIGITAL_ZOOM, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Float> paramAnonymousPropertyKey, PropertyChangeEventArgs<Float> paramAnonymousPropertyChangeEventArgs)
          {
            if ((!((Boolean)OnScreenHintImpl.-get5(OnScreenHintImpl.this).get(ZoomController.PROP_IS_ZOOM_LOCKED)).booleanValue()) && (OnScreenHintImpl.-wrap2(OnScreenHintImpl.this) == Rotation.INVERSE_LANDSCAPE))
            {
              HandlerUtils.sendMessage(OnScreenHintImpl.this, 10000, true, 1000L);
              OnScreenHintImpl.this.hideScreenHint();
            }
          }
        });
      }
      return;
      Log.w(this.TAG, "onInitialize() - No Viewfinder");
    }
  }
  
  protected void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    super.onRotationChanged(paramRotation1, paramRotation2);
    rotateLayout(this.m_Container, this.m_ContainerRotationCallback);
  }
  
  public Handle showHint(Drawable paramDrawable, View.OnClickListener paramOnClickListener, int paramInt)
  {
    if (paramDrawable == null)
    {
      Log.e(this.TAG, "showHint() - No Drawable to show");
      return null;
    }
    return showHint(paramDrawable, paramOnClickListener, paramInt);
  }
  
  public Handle showHint(CharSequence paramCharSequence, int paramInt)
  {
    return showHint(paramCharSequence, null, paramInt);
  }
  
  public Handle showHint(CharSequence paramCharSequence, View.OnClickListener paramOnClickListener, int paramInt)
  {
    return showHint(paramCharSequence, paramOnClickListener, paramInt);
  }
  
  public Handle showSecondaryHint(CharSequence paramCharSequence, int paramInt)
  {
    return showSecondaryHint(paramCharSequence, paramInt);
  }
  
  public boolean updateHint(Handle paramHandle, Drawable paramDrawable, int paramInt)
  {
    verifyAccess();
    if ((paramHandle != null) && (this.m_HintHandles.contains(paramHandle)))
    {
      if (paramDrawable == null)
      {
        Log.e(this.TAG, "updateHint() - No Drawable to update");
        return false;
      }
    }
    else {
      return false;
    }
    ((HintHandle)paramHandle).content = paramDrawable;
    if ((ListUtils.isLastObject(this.m_HintHandles, (HintHandle)paramHandle)) && (this.m_HintTextView != null))
    {
      updateHintBackground((HintHandle)paramHandle);
      this.m_HintTextView.setText(null);
    }
    return true;
  }
  
  public boolean updateHint(Handle paramHandle, CharSequence paramCharSequence, int paramInt)
  {
    verifyAccess();
    if (paramHandle == null) {
      return false;
    }
    paramHandle = (HintHandle)paramHandle;
    if (!paramHandle.isSecondaryHint)
    {
      if (!this.m_HintHandles.contains(paramHandle)) {
        return false;
      }
      paramHandle.content = paramCharSequence;
      paramHandle.flags = paramInt;
      if ((ListUtils.isLastObject(this.m_HintHandles, paramHandle)) && (this.m_HintTextView != null))
      {
        updateHintBackground(paramHandle);
        this.m_HintTextView.setText(paramCharSequence);
      }
    }
    for (;;)
    {
      updateHintMarginBottom(false);
      updateHintTextAppearance();
      return true;
      if (!this.m_SecondaryHintHandles.contains(paramHandle)) {
        return false;
      }
      paramHandle.content = paramCharSequence;
      paramHandle.flags = paramInt;
      if ((ListUtils.isLastObject(this.m_SecondaryHintHandles, paramHandle)) && (this.m_SecondaryHintTextView != null))
      {
        updateHintBackground(paramHandle);
        this.m_SecondaryHintTextView.setText(paramCharSequence);
      }
    }
  }
  
  private final class HintHandle
    extends Handle
  {
    public final View.OnClickListener clickListener;
    public Object content;
    public int flags;
    public final boolean isSecondaryHint;
    
    public HintHandle(Object paramObject, View.OnClickListener paramOnClickListener, boolean paramBoolean, int paramInt)
    {
      super();
      this.content = paramObject;
      this.clickListener = paramOnClickListener;
      this.flags = paramInt;
      this.isSecondaryHint = paramBoolean;
    }
    
    public boolean isDrawableContent()
    {
      return this.content instanceof Drawable;
    }
    
    protected void onClose(int paramInt)
    {
      if (!this.isSecondaryHint)
      {
        OnScreenHintImpl.-wrap3(OnScreenHintImpl.this, this);
        return;
      }
      OnScreenHintImpl.-wrap4(OnScreenHintImpl.this, this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/OnScreenHintImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */