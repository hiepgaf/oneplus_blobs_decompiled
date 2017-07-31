package com.oneplus.camera;

import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import java.util.Collections;
import java.util.List;

public abstract interface FaceBeautyController
  extends Component
{
  public static final int FLAG_FROM_USER = 1;
  public static final PropertyKey<Boolean> PROP_IS_ACTIVATED = new PropertyKey("IsActivated", Boolean.class, FaceBeautyController.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_STANDALONE_FACE_BEAUTY_SUPPORTED = new PropertyKey("IsStandaloneFaceBeautySupported", Boolean.class, FaceBeautyController.class, 1, null);
  public static final PropertyKey<Boolean> PROP_IS_SUPPORTED = new PropertyKey("IsSupported", Boolean.class, FaceBeautyController.class, 1, null);
  public static final PropertyKey<Integer> PROP_VALUE = new PropertyKey("Value", Integer.class, FaceBeautyController.class, 2, Integer.valueOf(0));
  public static final PropertyKey<List<Integer>> PROP_VALUE_LIST = new PropertyKey("ValueList", List.class, FaceBeautyController.class, Collections.EMPTY_LIST);
  
  public abstract boolean activate(int paramInt);
  
  public abstract void deactivate(int paramInt);
  
  public abstract Handle disable(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/FaceBeautyController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */