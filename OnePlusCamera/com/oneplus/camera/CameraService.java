package com.oneplus.camera;

import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface CameraService
  extends Component
{
  public static final PropertyKey<Boolean> PROP_IS_CONNECTED = new PropertyKey("IsConnected", Boolean.class, CameraService.class, Boolean.valueOf(false));
  public static final PropertyKey<SupportedState> PROP_TORCH_FLASH_SUPPORTED_STATE = new PropertyKey("TorchFlashSupportedState", SupportedState.class, CameraService.class, SupportedState.UNKNOWN);
  
  public abstract Handle setBacklightMaxBrightness();
  
  public abstract Handle torchFlash();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */