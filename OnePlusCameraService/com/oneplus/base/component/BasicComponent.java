package com.oneplus.base.component;

import com.oneplus.base.HandlerBaseObject;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;

public abstract class BasicComponent
  extends HandlerBaseObject
  implements Component
{
  private final String m_Name;
  private final ComponentOwner m_Owner;
  private volatile ComponentState m_State = ComponentState.NEW;
  
  protected BasicComponent(String paramString, ComponentOwner paramComponentOwner, boolean paramBoolean)
  {
    super(paramBoolean);
    if (paramString == null) {
      throw new IllegalArgumentException("No component name.");
    }
    if (paramComponentOwner == null) {
      throw new IllegalArgumentException("No component owner.");
    }
    this.m_Name = paramString;
    this.m_Owner = paramComponentOwner;
  }
  
  private ComponentState changeState(ComponentState paramComponentState)
  {
    ComponentState localComponentState = this.m_State;
    if (localComponentState != paramComponentState)
    {
      this.m_State = paramComponentState;
      notifyPropertyChanged(PROP_STATE, localComponentState, paramComponentState);
    }
    return this.m_State;
  }
  
  protected final <T extends Component> T findComponent(Class<T> paramClass)
  {
    return this.m_Owner.findComponent(paramClass);
  }
  
  protected final <T extends Component> boolean findComponent(Class<T> paramClass, ComponentSearchCallback<T> paramComponentSearchCallback)
  {
    return ComponentUtils.findComponent(this.m_Owner, paramClass, this.m_Owner, paramComponentSearchCallback);
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_OWNER) {
      return this.m_Owner;
    }
    if (paramPropertyKey == PROP_STATE) {
      return this.m_State;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  public boolean initialize()
  {
    verifyAccess();
    switch (-getcom-oneplus-base-component-ComponentStateSwitchesValues()[this.m_State.ordinal()])
    {
    default: 
      Log.e(this.TAG, "initialize() - Current state is " + this.m_State);
      return false;
    case 1: 
    case 3: 
      return true;
    }
    if (changeState(ComponentState.INITIALIZING) != ComponentState.INITIALIZING) {
      return false;
    }
    onInitialize();
    if (this.m_State != ComponentState.INITIALIZING)
    {
      Log.e(this.TAG, "initialize() - State has been changed to " + this.m_State + " while initializing");
      return false;
    }
    return changeState(ComponentState.RUNNING) == ComponentState.RUNNING;
  }
  
  public final boolean isRunningOrInitializing()
  {
    return (this.m_State == ComponentState.RUNNING) || (this.m_State == ComponentState.INITIALIZING);
  }
  
  protected final boolean isRunningOrInitializing(boolean paramBoolean)
  {
    if ((this.m_State == ComponentState.RUNNING) || (this.m_State == ComponentState.INITIALIZING)) {
      return true;
    }
    if (paramBoolean) {
      Log.w(this.TAG, "Component is not running or initializing");
    }
    return false;
  }
  
  protected void onDeinitialize() {}
  
  protected void onInitialize() {}
  
  protected void onRelease()
  {
    switch (-getcom-oneplus-base-component-ComponentStateSwitchesValues()[this.m_State.ordinal()])
    {
    }
    for (;;)
    {
      changeState(ComponentState.RELEASED);
      super.onRelease();
      return;
      changeState(ComponentState.RELEASING);
      onDeinitialize();
    }
  }
  
  public String toString()
  {
    return this.m_Name;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/component/BasicComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */