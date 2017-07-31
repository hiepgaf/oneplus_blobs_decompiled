package com.oneplus.camera.scene;

import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.BasicMode;
import com.oneplus.camera.Camera;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.media.MediaType;

public abstract class BasicScene
  extends BasicMode<Scene>
  implements Scene
{
  private final PropertyChangedCallback<Camera> m_CameraChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
    {
      BasicScene.this.onCameraChanged((Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final PropertyChangedCallback<MediaType> m_MediaTypeChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<MediaType> paramAnonymousPropertyKey, PropertyChangeEventArgs<MediaType> paramAnonymousPropertyChangeEventArgs)
    {
      BasicScene.this.onMediaTypeChanged((MediaType)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  
  protected BasicScene(CameraActivity paramCameraActivity, String paramString)
  {
    super(paramCameraActivity, paramString);
    paramCameraActivity.addCallback(CameraActivity.PROP_CAMERA, this.m_CameraChangedCallback);
    paramCameraActivity.addCallback(CameraActivity.PROP_MEDIA_TYPE, this.m_MediaTypeChangedCallback);
    onCameraChanged((Camera)paramCameraActivity.get(CameraActivity.PROP_CAMERA));
    onMediaTypeChanged((MediaType)paramCameraActivity.get(CameraActivity.PROP_MEDIA_TYPE));
  }
  
  protected void onCameraChanged(Camera paramCamera) {}
  
  protected void onMediaTypeChanged(MediaType paramMediaType) {}
  
  protected void onRelease()
  {
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.removeCallback(CameraActivity.PROP_CAMERA, this.m_CameraChangedCallback);
    localCameraActivity.removeCallback(CameraActivity.PROP_MEDIA_TYPE, this.m_MediaTypeChangedCallback);
    super.onRelease();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/scene/BasicScene.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */