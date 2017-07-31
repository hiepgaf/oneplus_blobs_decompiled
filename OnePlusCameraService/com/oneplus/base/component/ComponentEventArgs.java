package com.oneplus.base.component;

import com.oneplus.base.EventArgs;

public class ComponentEventArgs<TComponent extends Component>
  extends EventArgs
{
  private final TComponent m_Component;
  
  public ComponentEventArgs(TComponent paramTComponent)
  {
    this.m_Component = paramTComponent;
  }
  
  public TComponent getComponent()
  {
    return this.m_Component;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/component/ComponentEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */