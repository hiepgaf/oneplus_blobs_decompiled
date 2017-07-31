package com.oneplus.base.component;

import com.oneplus.base.BaseObject;
import com.oneplus.base.HandlerObject;
import com.oneplus.base.PropertyKey;

public abstract interface Component
  extends BaseObject, HandlerObject
{
  public static final PropertyKey<ComponentOwner> PROP_OWNER = new PropertyKey("Owner", ComponentOwner.class, Component.class, 1, null);
  public static final PropertyKey<ComponentState> PROP_STATE = new PropertyKey("State", ComponentState.class, Component.class, ComponentState.NEW);
  
  public abstract boolean initialize();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/component/Component.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */