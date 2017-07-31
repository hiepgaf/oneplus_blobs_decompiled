package com.oneplus.base.component;

import com.oneplus.base.BaseObject;
import com.oneplus.base.EventKey;
import com.oneplus.base.HandlerObject;

public abstract interface ComponentOwner
  extends BaseObject, HandlerObject
{
  public static final EventKey<ComponentEventArgs<Component>> EVENT_COMPONENT_ADDED = new EventKey("ComponentAdded", ComponentEventArgs.class, ComponentOwner.class);
  public static final EventKey<ComponentEventArgs<Component>> EVENT_COMPONENT_REMOVED = new EventKey("ComponentRemoved", ComponentEventArgs.class, ComponentOwner.class);
  
  public abstract <TComponent extends Component> TComponent findComponent(Class<TComponent> paramClass);
  
  public abstract <TComponent extends Component> TComponent[] findComponents(Class<TComponent> paramClass);
  
  public abstract void removeComponent(Component paramComponent);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/component/ComponentOwner.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */