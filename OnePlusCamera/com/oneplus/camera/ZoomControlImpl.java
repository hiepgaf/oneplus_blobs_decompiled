package com.oneplus.camera;

import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import java.util.ArrayList;
import java.util.List;

final class ZoomControlImpl
  extends CameraComponent
  implements ZoomController
{
  private volatile float m_DigitalZoom = 1.0F;
  private volatile boolean m_IsDigitalZoomSupported;
  private volatile float m_MaxDigitalZoom = 1.0F;
  private final PropertyChangedCallback<Float> m_ScalerCropRegionChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Float> paramAnonymousPropertyKey, PropertyChangeEventArgs<Float> paramAnonymousPropertyChangeEventArgs)
    {
      ZoomControlImpl.-wrap2(ZoomControlImpl.this, (Camera)paramAnonymousPropertySource, (Float)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final List<Handle> m_ZoomLockHandles = new ArrayList();
  
  ZoomControlImpl(CameraThread paramCameraThread)
  {
    super("Zoom Controller", paramCameraThread, false);
  }
  
  private void attachToCamera(Camera paramCamera)
  {
    float f1;
    float f2;
    boolean bool2;
    if (paramCamera != null)
    {
      f1 = ((Float)paramCamera.get(Camera.PROP_MAX_DIGITAL_ZOOM)).floatValue();
      f2 = this.m_MaxDigitalZoom;
      this.m_MaxDigitalZoom = f1;
      bool2 = this.m_IsDigitalZoomSupported;
      if (Math.abs(this.m_MaxDigitalZoom - 1.0F) < 0.001F) {
        break label153;
      }
    }
    label153:
    for (boolean bool1 = true;; bool1 = false)
    {
      this.m_IsDigitalZoomSupported = bool1;
      notifyPropertyChanged(PROP_IS_DIGITAL_ZOOM_SUPPORTED, Boolean.valueOf(bool2), Boolean.valueOf(this.m_IsDigitalZoomSupported));
      notifyPropertyChanged(PROP_MAX_DIGITAL_ZOOM, Float.valueOf(f2), Float.valueOf(this.m_MaxDigitalZoom));
      paramCamera.set(Camera.PROP_DIGITAL_ZOOM, Float.valueOf(1.0F));
      f1 = this.m_DigitalZoom;
      this.m_DigitalZoom = 1.0F;
      notifyPropertyChanged(PROP_DIGITAL_ZOOM, Float.valueOf(f1), Float.valueOf(1.0F));
      paramCamera.addCallback(Camera.PROP_DIGITAL_ZOOM, this.m_ScalerCropRegionChangedCallback);
      return;
    }
  }
  
  private void detachFromCamera(Camera paramCamera)
  {
    if (paramCamera != null) {
      paramCamera.removeCallback(Camera.PROP_DIGITAL_ZOOM, this.m_ScalerCropRegionChangedCallback);
    }
  }
  
  private void onDigitalZoomChanged(Camera paramCamera, Float paramFloat)
  {
    float f = this.m_DigitalZoom;
    if (paramFloat == null) {}
    for (this.m_DigitalZoom = 1.0F;; this.m_DigitalZoom = paramFloat.floatValue())
    {
      notifyPropertyChanged(PROP_DIGITAL_ZOOM, Float.valueOf(f), Float.valueOf(this.m_DigitalZoom));
      return;
    }
  }
  
  private boolean setDigitalZoomProp(float paramFloat, boolean paramBoolean)
  {
    verifyAccess();
    if (!this.m_IsDigitalZoomSupported)
    {
      Log.w(this.TAG, "setDigitalZoomProp() - Digital zoom is unsupported");
      return false;
    }
    Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.e(this.TAG, "setDigitalZoomProp() - No primary camera");
      return false;
    }
    if ((paramFloat < 1.0F) || (Math.abs(paramFloat - 1.0F) < 0.01F)) {}
    for (paramFloat = 1.0F; Math.abs(this.m_DigitalZoom - paramFloat) < 0.01F; paramFloat = Math.min(paramFloat, ((Float)get(PROP_MAX_DIGITAL_ZOOM)).floatValue())) {
      return false;
    }
    if ((!((Boolean)get(PROP_IS_ZOOM_LOCKED)).booleanValue()) || (paramBoolean))
    {
      Log.v(this.TAG, "setDigitalZoomProp() - Zoom : ", Float.valueOf(paramFloat));
      localCamera.set(Camera.PROP_DIGITAL_ZOOM, Float.valueOf(paramFloat));
      return true;
    }
    Log.w(this.TAG, "setDigitalZoomProp() - Zoom is locked");
    return false;
  }
  
  private void unlockZoom(Handle paramHandle)
  {
    verifyAccess();
    if ((this.m_ZoomLockHandles.remove(paramHandle)) && (this.m_ZoomLockHandles.isEmpty())) {
      setReadOnly(PROP_IS_ZOOM_LOCKED, Boolean.valueOf(false));
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_DIGITAL_ZOOM) {
      return Float.valueOf(this.m_DigitalZoom);
    }
    if (paramPropertyKey == PROP_IS_DIGITAL_ZOOM_SUPPORTED) {
      return Boolean.valueOf(this.m_IsDigitalZoomSupported);
    }
    if (paramPropertyKey == PROP_MAX_DIGITAL_ZOOM) {
      return Float.valueOf(this.m_MaxDigitalZoom);
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  public Handle lockZoom(int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "lockZoom() - Component is not running");
      return null;
    }
    Handle local2 = new Handle("ZoomLock")
    {
      protected void onClose(int paramAnonymousInt)
      {
        ZoomControlImpl.-wrap3(ZoomControlImpl.this, this);
      }
    };
    this.m_ZoomLockHandles.add(local2);
    if (this.m_ZoomLockHandles.size() == 1)
    {
      setReadOnly(PROP_IS_ZOOM_LOCKED, Boolean.valueOf(true));
      if (getCamera() != null) {
        setDigitalZoomProp(1.0F, true);
      }
    }
    return local2;
  }
  
  protected void onDeinitialize()
  {
    detachFromCamera(getCamera());
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    getCameraThread().addCallback(CameraThread.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        ZoomControlImpl.-wrap1(ZoomControlImpl.this, (Camera)paramAnonymousPropertyChangeEventArgs.getOldValue());
        ZoomControlImpl.-wrap0(ZoomControlImpl.this, (Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    attachToCamera(getCamera());
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_DIGITAL_ZOOM) {
      return setDigitalZoomProp(((Float)paramTValue).floatValue(), false);
    }
    return super.set(paramPropertyKey, paramTValue);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ZoomControlImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */