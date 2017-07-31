package com.oneplus.camera;

import android.os.Message;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.scene.Scene;
import com.oneplus.camera.scene.SceneManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FaceBeautyControllerImpl
  extends CameraComponent
  implements FaceBeautyController
{
  private static final int MSG_ON_FACE_BEAUTY_DEFAULT_VALUE_CHANGED = 10002;
  private static final int MSG_ON_FACE_BEAUTY_VALUES_CHANGED = 10001;
  private Map<Camera.LensFacing, Boolean> m_ActivateStatus = new HashMap();
  private List<DisableHandle> m_DisableHandles = new ArrayList();
  PropertyChangedCallback<Integer> m_FaceBeautyDefaultValueChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Integer> paramAnonymousPropertyKey, PropertyChangeEventArgs<Integer> paramAnonymousPropertyChangeEventArgs)
    {
      HandlerUtils.sendMessage(FaceBeautyControllerImpl.this, 10001);
    }
  };
  PropertyChangedCallback<List<Integer>> m_FaceBeautyValuesChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Integer>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Integer>> paramAnonymousPropertyChangeEventArgs)
    {
      HandlerUtils.sendMessage(FaceBeautyControllerImpl.this, 10001);
    }
  };
  private boolean m_IsActivated;
  private boolean m_IsStandaloneFaceBeautySupported;
  private Handle m_SceneLockHandle;
  private SceneManager m_SceneManager;
  private Map<Camera.LensFacing, Integer> m_Values = new HashMap();
  
  FaceBeautyControllerImpl(CameraActivity paramCameraActivity)
  {
    super("Face Beauty Controller", paramCameraActivity, true);
    enablePropertyLogs(PROP_IS_ACTIVATED, 1);
    enablePropertyLogs(PROP_IS_SUPPORTED, 1);
    enablePropertyLogs(PROP_VALUE, 1);
    enablePropertyLogs(PROP_VALUE_LIST, 1);
  }
  
  private boolean activate(final Camera paramCamera, boolean paramBoolean, int paramInt)
  {
    if (!((Boolean)get(PROP_IS_SUPPORTED)).booleanValue())
    {
      Log.v(this.TAG, "activate() - Face beauty is not supported");
      return false;
    }
    if (this.m_IsActivated)
    {
      Log.v(this.TAG, "activate() - Face beauty is already enabled");
      return true;
    }
    if (paramCamera == null)
    {
      Log.v(this.TAG, "activate() - Camera is null");
      return false;
    }
    Camera.LensFacing localLensFacing = (Camera.LensFacing)paramCamera.get(Camera.PROP_LENS_FACING);
    Log.v(this.TAG, "activate() - Lens facing: ", localLensFacing, ", flags: ", Integer.valueOf(paramInt));
    if (!this.m_IsStandaloneFaceBeautySupported)
    {
      this.m_SceneLockHandle = this.m_SceneManager.setDefaultScene(Scene.NO_SCENE, 2);
      HandlerUtils.post(paramCamera, new Runnable()
      {
        public void run()
        {
          paramCamera.set(Camera.PROP_SCENE_MODE, Integer.valueOf(3));
        }
      });
    }
    for (;;)
    {
      if (paramBoolean) {
        this.m_ActivateStatus.put(localLensFacing, Boolean.valueOf(true));
      }
      this.m_IsActivated = true;
      updateValue();
      notifyPropertyChanged(PROP_IS_ACTIVATED, Boolean.valueOf(false), Boolean.valueOf(true));
      return true;
      HandlerUtils.post(paramCamera, new Runnable()
      {
        public void run()
        {
          paramCamera.set(Camera.PROP_IS_STANDALONE_FACE_BEAUTY_ENABLED, Boolean.valueOf(true));
        }
      });
    }
  }
  
  private boolean applyFaceBeautyValue(final int paramInt)
  {
    final Camera localCamera = getCamera();
    if (localCamera == null)
    {
      Log.e(this.TAG, "applyFaceBeautyValue() - No camera to apply");
      return false;
    }
    HandlerUtils.post(localCamera, new Runnable()
    {
      public void run()
      {
        localCamera.set(Camera.PROP_FACE_BEAUTY_VALUE, Integer.valueOf(paramInt));
      }
    });
    return true;
  }
  
  private void deactivate(final Camera paramCamera, boolean paramBoolean, int paramInt)
  {
    if (!this.m_IsActivated) {
      return;
    }
    if (paramCamera == null)
    {
      Log.v(this.TAG, "deactivate() - Camera is null");
      return;
    }
    Camera.LensFacing localLensFacing = (Camera.LensFacing)paramCamera.get(Camera.PROP_LENS_FACING);
    Log.v(this.TAG, "deactivate() - Lens facing: ", localLensFacing, ", flags: ", Integer.valueOf(paramInt));
    if (!this.m_IsStandaloneFaceBeautySupported)
    {
      this.m_SceneLockHandle = Handle.close(this.m_SceneLockHandle);
      HandlerUtils.post(paramCamera, new Runnable()
      {
        public void run()
        {
          paramCamera.set(Camera.PROP_SCENE_MODE, Integer.valueOf(0));
        }
      });
    }
    for (;;)
    {
      if (paramBoolean) {
        this.m_ActivateStatus.put(localLensFacing, Boolean.valueOf(false));
      }
      super.set(PROP_VALUE, Integer.valueOf(0));
      this.m_IsActivated = false;
      notifyPropertyChanged(PROP_IS_ACTIVATED, Boolean.valueOf(true), Boolean.valueOf(false));
      return;
      HandlerUtils.post(paramCamera, new Runnable()
      {
        public void run()
        {
          paramCamera.set(Camera.PROP_IS_STANDALONE_FACE_BEAUTY_ENABLED, Boolean.valueOf(false));
        }
      });
    }
  }
  
  private boolean isSupported(Camera paramCamera)
  {
    if (paramCamera == null) {
      return false;
    }
    Camera.LensFacing localLensFacing = (Camera.LensFacing)paramCamera.get(Camera.PROP_LENS_FACING);
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[localLensFacing.ordinal()])
    {
    default: 
      return false;
    }
    this.m_IsStandaloneFaceBeautySupported = ((Boolean)paramCamera.get(Camera.PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED)).booleanValue();
    if (!this.m_IsStandaloneFaceBeautySupported) {
      return ((List)paramCamera.get(Camera.PROP_SCENE_MODES)).contains(Integer.valueOf(3));
    }
    return true;
  }
  
  private boolean isSupported(MediaType paramMediaType)
  {
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[paramMediaType.ordinal()])
    {
    default: 
      return false;
    }
    return true;
  }
  
  private void onCameraChanged(final Camera paramCamera1, final Camera paramCamera2)
  {
    if (isSupported(paramCamera1))
    {
      HandlerUtils.post(paramCamera1, new Runnable()
      {
        public void run()
        {
          paramCamera1.removeCallback(Camera.PROP_FACE_BEAUTY_VALUE_LIST, FaceBeautyControllerImpl.this.m_FaceBeautyValuesChangedCallback);
          paramCamera1.removeCallback(Camera.PROP_FACE_BEAUTY_DEFAULT_VALUE, FaceBeautyControllerImpl.this.m_FaceBeautyDefaultValueChangedCallback);
        }
      });
      deactivate(paramCamera1, false, 0);
    }
    if (isSupported(paramCamera2)) {
      HandlerUtils.post(paramCamera2, new Runnable()
      {
        public void run()
        {
          paramCamera2.addCallback(Camera.PROP_FACE_BEAUTY_VALUE_LIST, FaceBeautyControllerImpl.this.m_FaceBeautyValuesChangedCallback);
          paramCamera2.addCallback(Camera.PROP_FACE_BEAUTY_DEFAULT_VALUE, FaceBeautyControllerImpl.this.m_FaceBeautyDefaultValueChangedCallback);
          HandlerUtils.sendMessage(FaceBeautyControllerImpl.this, 10001);
          HandlerUtils.sendMessage(FaceBeautyControllerImpl.this, 10002);
        }
      });
    }
    updateSupportedState();
    updateActivateState();
    updateValueList();
    updateValue();
  }
  
  private void onFaceBeautyDefaultValueChanged()
  {
    updateValue();
  }
  
  private void onFaceBeautyValuesChanged()
  {
    updateValueList();
  }
  
  private void onKeepLastCaptureSettingsChanged(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      Iterator localIterator = ((List)getCameraActivity().get(CameraActivity.PROP_AVAILABLE_CAMERAS)).iterator();
      while (localIterator.hasNext())
      {
        Camera localCamera = (Camera)localIterator.next();
        Camera.LensFacing localLensFacing = (Camera.LensFacing)localCamera.get(Camera.PROP_LENS_FACING);
        int i = ((Integer)localCamera.get(Camera.PROP_FACE_BEAUTY_DEFAULT_VALUE)).intValue();
        this.m_ActivateStatus.put(localLensFacing, Boolean.valueOf(false));
        this.m_Values.put(localLensFacing, Integer.valueOf(i));
        Log.v(this.TAG, "onKeepLastCaptureSettingsChanged() - Reset face beauty: ", localLensFacing, ", value: ", Integer.valueOf(i));
      }
    }
  }
  
  private void onMediaTypeChanged()
  {
    updateSupportedState();
    updateActivateState();
    updateValueList();
    updateValue();
  }
  
  private boolean setValueProp(int paramInt)
  {
    if (!this.m_IsActivated)
    {
      Log.w(this.TAG, "setValueProp() - Face beauty is not activated");
      return false;
    }
    Object localObject = getCamera();
    if (localObject == null) {
      return false;
    }
    applyFaceBeautyValue(paramInt);
    localObject = (Camera.LensFacing)((Camera)localObject).get(Camera.PROP_LENS_FACING);
    this.m_Values.put(localObject, Integer.valueOf(paramInt));
    return super.set(PROP_VALUE, Integer.valueOf(paramInt));
  }
  
  private void updateActivateState()
  {
    Camera localCamera = getCamera();
    if (localCamera == null) {
      return;
    }
    Camera.LensFacing localLensFacing = (Camera.LensFacing)localCamera.get(Camera.PROP_LENS_FACING);
    if ((((Boolean)get(PROP_IS_SUPPORTED)).booleanValue()) && (this.m_ActivateStatus.containsKey(localLensFacing))) {}
    for (boolean bool = ((Boolean)this.m_ActivateStatus.get(localLensFacing)).booleanValue();; bool = false)
    {
      Log.v(this.TAG, "updateActivateState() - Lens facing: ", localLensFacing, ", activate: ", Boolean.valueOf(bool));
      if (!bool) {
        break;
      }
      activate(localCamera, false, 0);
      return;
    }
    deactivate(localCamera, false, 0);
  }
  
  private void updateSupportedState()
  {
    boolean bool2 = true;
    if (!isSupported(getCamera())) {
      bool2 = false;
    }
    MediaType localMediaType = getMediaType();
    boolean bool1 = bool2;
    if (bool2) {
      if (!isSupported(localMediaType)) {
        break label85;
      }
    }
    label85:
    for (bool1 = bool2;; bool1 = false)
    {
      bool2 = bool1;
      if (bool1)
      {
        bool2 = bool1;
        if (this.m_DisableHandles.size() > 0) {
          bool2 = false;
        }
      }
      Log.v(this.TAG, "updateSupportedState() - Is supported: ", Boolean.valueOf(bool2));
      setReadOnly(PROP_IS_SUPPORTED, Boolean.valueOf(bool2));
      return;
    }
  }
  
  private void updateValue()
  {
    Camera localCamera = getCamera();
    if (localCamera == null) {
      return;
    }
    Camera.LensFacing localLensFacing = (Camera.LensFacing)localCamera.get(Camera.PROP_LENS_FACING);
    Integer localInteger = (Integer)this.m_Values.get(localLensFacing);
    int j = ((Integer)localCamera.get(Camera.PROP_FACE_BEAUTY_DEFAULT_VALUE)).intValue();
    Log.v(this.TAG, "updateValue() - Lens facing: ", localLensFacing, ", value: ", localInteger, ", camera: ", Integer.valueOf(j));
    int i;
    if (localInteger != null) {
      i = localInteger.intValue();
    }
    for (;;)
    {
      if (this.m_IsActivated)
      {
        applyFaceBeautyValue(i);
        super.set(PROP_VALUE, Integer.valueOf(i));
      }
      return;
      i = j;
      this.m_Values.put(localLensFacing, Integer.valueOf(j));
    }
  }
  
  private void updateValueList()
  {
    Object localObject = getCamera();
    if (localObject == null) {
      return;
    }
    localObject = (List)((Camera)localObject).get(Camera.PROP_FACE_BEAUTY_VALUE_LIST);
    Log.v(this.TAG, "updateValueList() - Face beauty values: ", localObject);
    setReadOnly(PROP_VALUE_LIST, new ArrayList((Collection)localObject));
  }
  
  public boolean activate(int paramInt)
  {
    return activate(getCamera(), true, paramInt);
  }
  
  public void deactivate(int paramInt)
  {
    deactivate(getCamera(), true, paramInt);
  }
  
  public Handle disable(int paramInt)
  {
    DisableHandle localDisableHandle = new DisableHandle();
    this.m_DisableHandles.add(localDisableHandle);
    updateSupportedState();
    return localDisableHandle;
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_IS_ACTIVATED) {
      return Boolean.valueOf(this.m_IsActivated);
    }
    if (paramPropertyKey == PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED) {
      return Boolean.valueOf(this.m_IsStandaloneFaceBeautySupported);
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
    case 10002: 
      onFaceBeautyDefaultValueChanged();
      return;
    }
    onFaceBeautyValuesChanged();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    this.m_SceneManager = ((SceneManager)findComponent(SceneManager.class));
    CameraActivity localCameraActivity = getCameraActivity();
    localCameraActivity.addCallback(CameraActivity.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        FaceBeautyControllerImpl.-wrap0(FaceBeautyControllerImpl.this, (Camera)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_KEEP_LAST_CAPTURE_SETTINGS, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        FaceBeautyControllerImpl.-wrap1(FaceBeautyControllerImpl.this, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
      }
    });
    localCameraActivity.addCallback(CameraActivity.PROP_MEDIA_TYPE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<MediaType> paramAnonymousPropertyKey, PropertyChangeEventArgs<MediaType> paramAnonymousPropertyChangeEventArgs)
      {
        FaceBeautyControllerImpl.-wrap2(FaceBeautyControllerImpl.this);
      }
    });
    onKeepLastCaptureSettingsChanged(((Boolean)localCameraActivity.get(CameraActivity.PROP_KEEP_LAST_CAPTURE_SETTINGS)).booleanValue());
    updateSupportedState();
    updateActivateState();
    updateValueList();
    updateValue();
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_VALUE) {
      return setValueProp(((Integer)paramTValue).intValue());
    }
    return super.set(paramPropertyKey, paramTValue);
  }
  
  private class DisableHandle
    extends Handle
  {
    DisableHandle()
    {
      super();
    }
    
    protected void onClose(int paramInt)
    {
      if (FaceBeautyControllerImpl.-get0(FaceBeautyControllerImpl.this).remove(this)) {
        FaceBeautyControllerImpl.-wrap3(FaceBeautyControllerImpl.this);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/FaceBeautyControllerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */