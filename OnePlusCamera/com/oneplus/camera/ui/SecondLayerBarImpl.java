package com.oneplus.camera.ui;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewPropertyAnimator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CaptureEventArgs;
import com.oneplus.camera.OPCameraActivity;
import com.oneplus.camera.UIComponent;
import com.oneplus.camera.VideoCaptureState;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.drawable.ExpandableBackgroundDrawable;
import java.util.Iterator;
import java.util.List;

final class SecondLayerBarImpl
  extends UIComponent
  implements SecondLayerBar
{
  private static final long DURATION_FADE_IN = 50L;
  private static final long DURATION_FADE_IN_ITEMS = 300L;
  private static final long DURATION_FADE_OUT = 300L;
  private static final long DURATION_FADE_OUT_ITEMS = 100L;
  private ExpandableBackgroundDrawable m_BackgroundDrawable;
  private CaptureModeManager m_CaptureModeManager;
  private CaptureModeSwitcher m_CaptureModeSwitcher;
  private LayerBarHandle m_CurrentHandle;
  private LinearLayout m_ItemListContainer;
  private RelativeLayout m_ProgressBar;
  private RelativeLayout m_ProgressBarContainer;
  private LinearLayout m_ProgressIcon;
  private RelativeLayout m_SecondLayoutBarContainer;
  
  SecondLayerBarImpl(CameraActivity paramCameraActivity)
  {
    super("Second Layer Bar", paramCameraActivity, true);
  }
  
  private void updateVisibility(boolean paramBoolean1, float paramFloat1, float paramFloat2, boolean paramBoolean2)
  {
    Object localObject = this.m_SecondLayoutBarContainer;
    long l;
    if (paramBoolean1)
    {
      l = 50L;
      setViewVisibility((View)localObject, paramBoolean1, l, null);
      if (!paramBoolean1) {
        break label172;
      }
      if (this.m_ItemListContainer != null)
      {
        this.m_ItemListContainer.setAlpha(0.0F);
        this.m_ItemListContainer.animate().alpha(1.0F).setDuration(300L).start();
      }
      if (this.m_ProgressBarContainer != null)
      {
        this.m_ProgressBarContainer.setAlpha(0.0F);
        this.m_ProgressBarContainer.animate().alpha(1.0F).setDuration(300L).start();
      }
    }
    for (;;)
    {
      set(PROP_IS_VISIBLE, Boolean.valueOf(paramBoolean1));
      if (this.m_SecondLayoutBarContainer != null)
      {
        localObject = new int[2];
        this.m_SecondLayoutBarContainer.getLocationOnScreen((int[])localObject);
        paramFloat1 -= localObject[0];
        paramFloat2 -= localObject[1];
        if (!paramBoolean1) {
          break label229;
        }
        this.m_BackgroundDrawable.expand(paramFloat1, paramFloat2, paramBoolean2);
      }
      return;
      l = 300L;
      break;
      label172:
      if (this.m_ItemListContainer != null) {
        this.m_ItemListContainer.animate().alpha(0.0F).setDuration(100L).start();
      }
      if (this.m_ProgressBarContainer != null) {
        this.m_ProgressBarContainer.animate().alpha(0.0F).setDuration(100L).start();
      }
    }
    label229:
    this.m_BackgroundDrawable.collapse(paramFloat1, paramFloat2, paramBoolean2);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    int i = paramMessage.what;
    super.handleMessage(paramMessage);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    OPCameraActivity localOPCameraActivity = (OPCameraActivity)getCameraActivity();
    this.m_SecondLayoutBarContainer = ((RelativeLayout)localOPCameraActivity.findViewById(2131362060));
    this.m_ItemListContainer = ((LinearLayout)this.m_SecondLayoutBarContainer.findViewById(2131362061));
    this.m_ProgressBarContainer = ((RelativeLayout)this.m_SecondLayoutBarContainer.findViewById(2131362062));
    this.m_ProgressIcon = ((LinearLayout)this.m_ProgressBarContainer.findViewById(2131362063));
    this.m_ProgressBar = ((RelativeLayout)this.m_ProgressBarContainer.findViewById(2131362064));
    this.m_BackgroundDrawable = new ExpandableBackgroundDrawable(Color.argb(127, 0, 0, 0));
    this.m_SecondLayoutBarContainer.setBackground(this.m_BackgroundDrawable);
    localOPCameraActivity.addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.PAUSING) {
          Handle.close(SecondLayerBarImpl.-get0(SecondLayerBarImpl.this));
        }
      }
    });
    localOPCameraActivity.addCallback(CameraActivity.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        Handle.close(SecondLayerBarImpl.-get0(SecondLayerBarImpl.this));
      }
    });
    localOPCameraActivity.addCallback(CameraActivity.PROP_IS_SELF_TIMER_STARTED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          Handle.close(SecondLayerBarImpl.-get0(SecondLayerBarImpl.this));
        }
      }
    });
    localOPCameraActivity.addCallback(CameraActivity.PROP_VIDEO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<VideoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<VideoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        Handle.close(SecondLayerBarImpl.-get0(SecondLayerBarImpl.this));
      }
    });
    localOPCameraActivity.addHandler(CameraActivity.EVENT_CAPTURE_STARTED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
      {
        Handle.close(SecondLayerBarImpl.-get0(SecondLayerBarImpl.this));
      }
    });
    localOPCameraActivity.addCallback(CameraActivity.PROP_IS_SIMPLE_UI_MODE_ENTERED, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
          Handle.close(SecondLayerBarImpl.-get0(SecondLayerBarImpl.this));
        }
      }
    });
    this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    if (this.m_CaptureModeManager != null) {
      this.m_CaptureModeManager.addCallback(CaptureModeManager.PROP_CAPTURE_MODE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CaptureMode> paramAnonymousPropertyKey, PropertyChangeEventArgs<CaptureMode> paramAnonymousPropertyChangeEventArgs)
        {
          Handle.close(SecondLayerBarImpl.-get0(SecondLayerBarImpl.this));
        }
      });
    }
    this.m_CaptureModeSwitcher = ((CaptureModeSwitcher)findComponent(CaptureModeSwitcher.class));
    if (this.m_CaptureModeSwitcher != null) {
      this.m_CaptureModeSwitcher.addCallback(CaptureModeSwitcher.PROP_IS_CAPTURE_MODE_PANEL_OPEN, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          if (((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue()) {
            Handle.close(SecondLayerBarImpl.-get0(SecondLayerBarImpl.this));
          }
        }
      });
    }
  }
  
  public <TValue extends View> Handle show(float paramFloat1, float paramFloat2, List<TValue> paramList, int paramInt)
  {
    this.m_ProgressBarContainer.setVisibility(4);
    this.m_ItemListContainer.setVisibility(0);
    this.m_ItemListContainer.removeAllViews();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      final Object localObject = (View)paramList.next();
      if ((((View)localObject).getParent() instanceof ViewGroup)) {
        ((ViewGroup)((View)localObject).getParent()).removeView((View)localObject);
      }
      RelativeLayout localRelativeLayout = new RelativeLayout(getCameraActivity());
      localRelativeLayout.setBackground(getCameraActivity().getDrawable(2130838090));
      RelativeLayout.LayoutParams localLayoutParams = new RelativeLayout.LayoutParams(-2, -2);
      localLayoutParams.addRule(13);
      localRelativeLayout.addView((View)localObject, localLayoutParams);
      localRelativeLayout.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          localObject.callOnClick();
        }
      });
      localObject = new LinearLayout.LayoutParams(-1, -1);
      ((LinearLayout.LayoutParams)localObject).gravity = 17;
      ((LinearLayout.LayoutParams)localObject).weight = 1.0F;
      this.m_ItemListContainer.addView(localRelativeLayout, (ViewGroup.LayoutParams)localObject);
    }
    if ((paramInt & 0x1) == 0) {}
    for (boolean bool = true;; bool = false)
    {
      updateVisibility(true, paramFloat1, paramFloat2, bool);
      Handle.close(this.m_CurrentHandle, 1);
      this.m_CurrentHandle = new LayerBarHandle("", paramFloat1, paramFloat2);
      return this.m_CurrentHandle;
    }
  }
  
  public <TValue extends View> Handle show(float paramFloat1, float paramFloat2, List<TValue> paramList, ProgressBar paramProgressBar, int paramInt)
  {
    this.m_ProgressBarContainer.setVisibility(0);
    this.m_ItemListContainer.setVisibility(4);
    this.m_ProgressBar.removeAllViews();
    this.m_ProgressIcon.removeAllViews();
    if ((paramProgressBar.getParent() instanceof ViewGroup)) {
      ((ViewGroup)paramProgressBar.getParent()).removeView(paramProgressBar);
    }
    Object localObject = paramList.iterator();
    while (((Iterator)localObject).hasNext())
    {
      View localView = (View)((Iterator)localObject).next();
      if ((localView.getParent() instanceof ViewGroup)) {
        ((ViewGroup)localView.getParent()).removeView(localView);
      }
    }
    localObject = new LinearLayout.LayoutParams(-1, -1);
    this.m_ProgressBar.addView(paramProgressBar, (ViewGroup.LayoutParams)localObject);
    paramProgressBar = new LinearLayout.LayoutParams(-2, -2);
    paramProgressBar.setMarginStart(getCameraActivity().getResources().getDimensionPixelSize(2131296648));
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      localObject = (View)paramList.next();
      this.m_ProgressIcon.addView((View)localObject, paramProgressBar);
    }
    if ((paramInt & 0x1) == 0) {}
    for (boolean bool = true;; bool = false)
    {
      updateVisibility(true, paramFloat1, paramFloat2, bool);
      Handle.close(this.m_CurrentHandle, 1);
      this.m_CurrentHandle = new LayerBarHandle("", paramFloat1, paramFloat2);
      return this.m_CurrentHandle;
    }
  }
  
  public class LayerBarHandle
    extends Handle
  {
    public final float pivotX;
    public final float pivotY;
    
    protected LayerBarHandle(String paramString, float paramFloat1, float paramFloat2)
    {
      super();
      this.pivotX = paramFloat1;
      this.pivotY = paramFloat2;
    }
    
    protected void onClose(int paramInt)
    {
      SecondLayerBarImpl localSecondLayerBarImpl = SecondLayerBarImpl.this;
      float f1 = this.pivotX;
      float f2 = this.pivotY;
      if ((paramInt & 0x1) == 0) {}
      for (boolean bool = true;; bool = false)
      {
        SecondLayerBarImpl.-wrap0(localSecondLayerBarImpl, false, f1, f2, bool);
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/SecondLayerBarImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */