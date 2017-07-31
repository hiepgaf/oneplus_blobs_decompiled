package com.oneplus.base.component;

import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.HandlerBaseObject;

public abstract class ComponentOwnerObject
  extends HandlerBaseObject
  implements ComponentOwner
{
  private final ComponentManager m_ComponentManager = new ComponentManager();
  
  protected ComponentOwnerObject(boolean paramBoolean)
  {
    super(paramBoolean);
    this.m_ComponentManager.addHandler(ComponentManager.EVENT_COMPONENT_ADDED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
      {
        ComponentOwnerObject.-wrap0(ComponentOwnerObject.this, ComponentOwnerObject.EVENT_COMPONENT_ADDED, paramAnonymousComponentEventArgs);
      }
    });
    this.m_ComponentManager.addHandler(ComponentManager.EVENT_COMPONENT_REMOVED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
      {
        ComponentOwnerObject.-wrap0(ComponentOwnerObject.this, ComponentOwnerObject.EVENT_COMPONENT_REMOVED, paramAnonymousComponentEventArgs);
      }
    });
  }
  
  public final void addComponentBuilders(ComponentBuilder[] paramArrayOfComponentBuilder)
  {
    verifyAccess();
    this.m_ComponentManager.addComponentBuilders(paramArrayOfComponentBuilder, new Object[] { this });
  }
  
  public <TComponent extends Component> TComponent findComponent(Class<TComponent> paramClass)
  {
    return this.m_ComponentManager.findComponent(paramClass, new Object[] { this });
  }
  
  public <TComponent extends Component> TComponent[] findComponents(Class<TComponent> paramClass)
  {
    return this.m_ComponentManager.findComponents(paramClass, new Object[] { this });
  }
  
  protected final ComponentManager getComponentManager()
  {
    return this.m_ComponentManager;
  }
  
  protected void onRelease()
  {
    this.m_ComponentManager.release();
    super.onRelease();
  }
  
  public void removeComponent(Component paramComponent)
  {
    this.m_ComponentManager.removeComponent(paramComponent);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/component/ComponentOwnerObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */