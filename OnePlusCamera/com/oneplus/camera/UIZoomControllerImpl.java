package com.oneplus.camera;

import android.os.SystemClock;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import java.util.List;

final class UIZoomControllerImpl
  extends ProxyComponent<ZoomController>
  implements ZoomController
{
  private CaptureModeManager m_CaptureModeManager;
  private Runnable m_DigitalZoomRunnable;
  private long m_LastZoomChangedTime;
  
  UIZoomControllerImpl(CameraActivity paramCameraActivity)
  {
    super("UI Zoom Controller", paramCameraActivity, paramCameraActivity.getCameraThread(), ZoomController.class);
  }
  
  private boolean applyDigitalZoom(final float paramFloat)
  {
    if (isTargetBound())
    {
      if (this.m_DigitalZoomRunnable != null) {
        HandlerUtils.removeCallbacks(getTargetOwner(), this.m_DigitalZoomRunnable);
      }
      this.m_DigitalZoomRunnable = new Runnable()
      {
        public void run()
        {
          ((ZoomController)UIZoomControllerImpl.this.getTarget()).set(UIZoomControllerImpl.PROP_DIGITAL_ZOOM, Float.valueOf(paramFloat));
        }
      };
      if (!HandlerUtils.post(getTargetOwner(), this.m_DigitalZoomRunnable))
      {
        Log.e(this.TAG, "applyDigitalZoom() - Fail to set zoom asynchronously");
        return false;
      }
      return true;
    }
    Log.w(this.TAG, "applyDigitalZoom() - Target is not ready, set zoom later");
    return true;
  }
  
  private boolean setDigitalZoomProp(float paramFloat)
  {
    verifyAccess();
    if (!((Boolean)get(PROP_IS_DIGITAL_ZOOM_SUPPORTED)).booleanValue())
    {
      Log.w(this.TAG, "setDigitalZoomProp() - Digital zoom is unsupported");
      return false;
    }
    if ((paramFloat < 1.0F) || (Math.abs(paramFloat - 1.0F) < 0.01F)) {}
    for (paramFloat = 1.0F;; paramFloat = Math.min(paramFloat, ((Float)get(PROP_MAX_DIGITAL_ZOOM)).floatValue()))
    {
      this.m_LastZoomChangedTime = SystemClock.elapsedRealtimeNanos();
      if (super.set(PROP_DIGITAL_ZOOM, Float.valueOf(paramFloat))) {
        break;
      }
      return false;
    }
    applyDigitalZoom(((Float)get(PROP_DIGITAL_ZOOM)).floatValue());
    return true;
  }
  
  public Handle lockZoom(int paramInt)
  {
    verifyAccess();
    Handle localHandle = callTargetMethod("lockZoom", new Class[] { Integer.TYPE }, new Object[] { Integer.valueOf(paramInt) });
    if (Handle.isValid(localHandle)) {
      setReadOnly(PROP_IS_ZOOM_LOCKED, Boolean.valueOf(true));
    }
    return localHandle;
  }
  
  protected void onBindingToTargetProperties(List<PropertyKey<?>> paramList)
  {
    super.onBindingToTargetProperties(paramList);
    paramList.add(PROP_DIGITAL_ZOOM);
    paramList.add(PROP_IS_DIGITAL_ZOOM_SUPPORTED);
    paramList.add(PROP_IS_ZOOM_LOCKED);
    paramList.add(PROP_MAX_DIGITAL_ZOOM);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    getCameraActivity().addCallback(CameraActivity.PROP_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<BaseActivity.State> paramAnonymousPropertyKey, PropertyChangeEventArgs<BaseActivity.State> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getNewValue() == BaseActivity.State.NEW_INTENT) {
          UIZoomControllerImpl.-wrap0(UIZoomControllerImpl.this, 1.0F);
        }
      }
    });
    this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    this.m_CaptureModeManager.addCallback(CaptureModeManager.PROP_CAPTURE_MODE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<CaptureMode> paramAnonymousPropertyKey, PropertyChangeEventArgs<CaptureMode> paramAnonymousPropertyChangeEventArgs)
      {
        UIZoomControllerImpl.-wrap0(UIZoomControllerImpl.this, 1.0F);
      }
    });
  }
  
  protected void onTargetBound(ZoomController paramZoomController)
  {
    super.onTargetBound(paramZoomController);
    if (this.m_LastZoomChangedTime > 0L) {
      applyDigitalZoom(((Float)get(PROP_DIGITAL_ZOOM)).floatValue());
    }
  }
  
  protected void onTargetPropertyChanged(long paramLong, PropertyKey<?> paramPropertyKey, PropertyChangeEventArgs<?> paramPropertyChangeEventArgs)
  {
    if (paramPropertyKey == PROP_DIGITAL_ZOOM)
    {
      if (paramLong >= this.m_LastZoomChangedTime) {
        super.set(PROP_DIGITAL_ZOOM, (Float)paramPropertyChangeEventArgs.getNewValue());
      }
      return;
    }
    super.onTargetPropertyChanged(paramLong, paramPropertyKey, paramPropertyChangeEventArgs);
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_DIGITAL_ZOOM) {
      return setDigitalZoomProp(((Float)paramTValue).floatValue());
    }
    return super.set(paramPropertyKey, paramTValue);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/UIZoomControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */