package com.oneplus.camera.ui;

import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import com.oneplus.camera.OperationState;

public abstract interface SwitchAnimation
  extends Component
{
  public static final PropertyKey<OperationState> PROP_ANIMATION_STATE = new PropertyKey("AnimationState", OperationState.class, SwitchAnimation.class, OperationState.STOPPED);
  
  public abstract void start(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/SwitchAnimation.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */