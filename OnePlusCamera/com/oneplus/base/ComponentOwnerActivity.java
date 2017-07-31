package com.oneplus.base;

import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.base.component.ComponentEventArgs;
import com.oneplus.base.component.ComponentManager;
import com.oneplus.base.component.ComponentOwner;

public class ComponentOwnerActivity
  extends BaseActivity
  implements ComponentOwner
{
  private final ComponentManager m_ComponentManager = new ComponentManager();
  
  protected ComponentOwnerActivity()
  {
    this.m_ComponentManager.addHandler(ComponentManager.EVENT_COMPONENT_ADDED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
      {
        ComponentOwnerActivity.this.raise(ComponentOwnerActivity.EVENT_COMPONENT_ADDED, paramAnonymousComponentEventArgs);
      }
    });
    this.m_ComponentManager.addHandler(ComponentManager.EVENT_COMPONENT_REMOVED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
      {
        ComponentOwnerActivity.this.raise(ComponentOwnerActivity.EVENT_COMPONENT_REMOVED, paramAnonymousComponentEventArgs);
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
  
  protected void onDestroy()
  {
    this.m_ComponentManager.release();
    super.onDestroy();
  }
  
  public void removeComponent(Component paramComponent)
  {
    this.m_ComponentManager.removeComponent(paramComponent);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/ComponentOwnerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */