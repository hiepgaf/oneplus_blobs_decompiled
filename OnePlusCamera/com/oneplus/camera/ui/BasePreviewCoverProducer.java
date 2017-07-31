package com.oneplus.camera.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.oneplus.base.HandlerBaseObject;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.base.component.ComponentUtils;
import com.oneplus.camera.CameraActivity;

public abstract class BasePreviewCoverProducer
  extends HandlerBaseObject
  implements PreviewCoverProducer
{
  private ViewGroup m_BaseLayout;
  private CameraActivity m_CameraActivity;
  private CameraGallery m_CameraGallery;
  private final PropertyChangedCallback<CameraGallery.UIState> m_CameraGalleryStateChangedCB = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CameraGallery.UIState> paramAnonymousPropertyKey, PropertyChangeEventArgs<CameraGallery.UIState> paramAnonymousPropertyChangeEventArgs)
    {
      BasePreviewCoverProducer.this.onCameraGalleryUIStateChanged((CameraGallery.UIState)paramAnonymousPropertyChangeEventArgs.getOldValue(), (CameraGallery.UIState)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private int m_HideFlags;
  private boolean m_IsVisible;
  private View m_PreviewCoverBackgroundView;
  private View m_PreviewCoverContainer;
  private ImageView[] m_PreviewCoverImageViews;
  
  public BasePreviewCoverProducer(CameraActivity paramCameraActivity)
  {
    super(true);
    this.m_CameraActivity = paramCameraActivity;
    this.m_BaseLayout = ((ViewGroup)paramCameraActivity.findViewById(2131361958));
    View.inflate(paramCameraActivity, 2130903087, this.m_BaseLayout);
    this.m_PreviewCoverContainer = this.m_BaseLayout.getChildAt(this.m_BaseLayout.getChildCount() - 1);
    this.m_PreviewCoverBackgroundView = this.m_PreviewCoverContainer.findViewById(2131362033);
    this.m_PreviewCoverImageViews = new ImageView[] { (ImageView)this.m_PreviewCoverContainer.findViewById(2131362034), (ImageView)this.m_PreviewCoverContainer.findViewById(2131362035) };
    addCallback(PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PreviewCoverProducer.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<PreviewCoverProducer.State> paramAnonymousPropertyChangeEventArgs)
      {
        switch (-getcom-oneplus-camera-ui-PreviewCoverProducer$StateSwitchesValues()[((PreviewCoverProducer.State)paramAnonymousPropertyChangeEventArgs.getNewValue()).ordinal()])
        {
        }
        do
        {
          do
          {
            return;
          } while (paramAnonymousPropertyChangeEventArgs.getOldValue() != PreviewCoverProducer.State.PREPARING);
          BasePreviewCoverProducer.this.startInAnimation(true);
          return;
        } while (BasePreviewCoverProducer.-get3(BasePreviewCoverProducer.this));
        paramAnonymousPropertySource = BasePreviewCoverProducer.this;
        if ((BasePreviewCoverProducer.-get2(BasePreviewCoverProducer.this) & 0x1) == 0) {}
        for (boolean bool = true;; bool = false)
        {
          paramAnonymousPropertySource.startOutAnimation(bool);
          return;
        }
      }
    });
    ComponentUtils.findComponent(paramCameraActivity, CameraGallery.class, paramCameraActivity, new ComponentSearchCallback()
    {
      public void onComponentFound(CameraGallery paramAnonymousCameraGallery)
      {
        if (((Boolean)BasePreviewCoverProducer.this.get(BasePreviewCoverProducer.PROP_IS_RELEASED)).booleanValue()) {
          return;
        }
        BasePreviewCoverProducer.-set0(BasePreviewCoverProducer.this, paramAnonymousCameraGallery);
        BasePreviewCoverProducer.-get0(BasePreviewCoverProducer.this).addCallback(CameraGallery.PROP_UI_STATE, BasePreviewCoverProducer.-get1(BasePreviewCoverProducer.this));
      }
    });
    enablePropertyLogs(PROP_STATE, 1);
  }
  
  protected CameraActivity getCameraActivity()
  {
    return this.m_CameraActivity;
  }
  
  protected View getPreviewCoverBackgroundView()
  {
    return this.m_PreviewCoverBackgroundView;
  }
  
  protected ImageView getPreviewCoverBaseImageView()
  {
    return this.m_PreviewCoverImageViews[0];
  }
  
  protected View getPreviewCoverContainer()
  {
    return this.m_PreviewCoverContainer;
  }
  
  protected ImageView getPreviewCoverTopImageView()
  {
    return this.m_PreviewCoverImageViews[1];
  }
  
  public final void hidePreviewCover(int paramInt)
  {
    Log.v(this.TAG, "hidePreviewCover()");
    this.m_IsVisible = false;
    this.m_HideFlags = paramInt;
    switch (-getcom-oneplus-camera-ui-PreviewCoverProducer$StateSwitchesValues()[((PreviewCoverProducer.State)get(PROP_STATE)).ordinal()])
    {
    default: 
      return;
    case 2: 
      setReadOnly(PROP_STATE, PreviewCoverProducer.State.STOPPED);
      return;
    }
    if ((paramInt & 0x1) == 0) {}
    for (boolean bool = true;; bool = false)
    {
      startOutAnimation(bool);
      return;
    }
  }
  
  protected void onCameraGalleryUIStateChanged(CameraGallery.UIState paramUIState1, CameraGallery.UIState paramUIState2)
  {
    if (paramUIState1 == CameraGallery.UIState.OPENED) {
      if (this.m_BaseLayout != null) {
        this.m_BaseLayout.setVisibility(0);
      }
    }
    while ((paramUIState2 != CameraGallery.UIState.OPENED) || (this.m_BaseLayout == null) || (this.m_BaseLayout.getVisibility() != 0)) {
      return;
    }
    this.m_BaseLayout.setVisibility(4);
  }
  
  protected void onRelease()
  {
    if (this.m_CameraGallery != null)
    {
      this.m_CameraGallery.removeCallback(CameraGallery.PROP_UI_STATE, this.m_CameraGalleryStateChangedCB);
      this.m_CameraGallery = null;
    }
    super.onRelease();
  }
  
  protected abstract boolean preparePreviewCover();
  
  protected abstract void reverseInAnimation();
  
  protected abstract void reverseOutAnimation();
  
  public final boolean showPreviewCover(int paramInt)
  {
    Log.v(this.TAG, "showPreviewCover()");
    this.m_PreviewCoverContainer.bringToFront();
    this.m_IsVisible = true;
    switch (-getcom-oneplus-camera-ui-PreviewCoverProducer$StateSwitchesValues()[((PreviewCoverProducer.State)get(PROP_STATE)).ordinal()])
    {
    case 2: 
    case 3: 
    default: 
    case 4: 
      do
      {
        return true;
        setReadOnly(PROP_STATE, PreviewCoverProducer.State.PREPARING);
      } while (preparePreviewCover());
      setReadOnly(PROP_STATE, PreviewCoverProducer.State.STOPPED);
      return false;
    }
    reverseOutAnimation();
    return true;
  }
  
  protected abstract void startInAnimation(boolean paramBoolean);
  
  protected abstract void startOutAnimation(boolean paramBoolean);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/BasePreviewCoverProducer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */