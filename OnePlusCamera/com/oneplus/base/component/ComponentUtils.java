package com.oneplus.base.component;

import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.HandlerObject;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;

public final class ComponentUtils
{
  private static final String TAG = "ComponentUtils";
  
  private static <TComponent extends Component> void callComponentSearchCallback(HandlerObject paramHandlerObject, ComponentSearchCallback<TComponent> paramComponentSearchCallback, final TComponent paramTComponent)
  {
    if ((paramTComponent != null) && (paramComponentSearchCallback != null))
    {
      if ((paramHandlerObject != null) && (!paramHandlerObject.isDependencyThread())) {
        break label29;
      }
      paramComponentSearchCallback.onComponentFound(paramTComponent);
    }
    label29:
    while (HandlerUtils.post(paramHandlerObject, new Runnable()
    {
      public void run()
      {
        this.val$callback.onComponentFound(paramTComponent);
      }
    })) {
      return;
    }
    Log.e("ComponentUtils", "callComponentSearchCallback() - Fail to perform cross-thread call-back");
  }
  
  public static <TComponent extends Component> boolean findComponent(final ComponentOwner paramComponentOwner, final Class<TComponent> paramClass, final HandlerObject paramHandlerObject, final ComponentSearchCallback<TComponent> paramComponentSearchCallback)
  {
    Component localComponent = paramComponentOwner.findComponent(paramClass);
    if (localComponent != null)
    {
      callComponentSearchCallback(paramHandlerObject, paramComponentSearchCallback, localComponent);
      if (paramHandlerObject != null) {
        return paramHandlerObject.isDependencyThread();
      }
      return true;
    }
    if (paramComponentSearchCallback == null) {
      return false;
    }
    if (paramComponentOwner.isDependencyThread()) {
      paramComponentOwner.addHandler(ComponentOwner.EVENT_COMPONENT_ADDED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
        {
          paramAnonymousEventSource = paramAnonymousComponentEventArgs.getComponent();
          if (this.val$componentType.isAssignableFrom(paramAnonymousEventSource.getClass()))
          {
            paramComponentOwner.removeHandler(ComponentOwner.EVENT_COMPONENT_ADDED, this);
            ComponentUtils.-wrap0(paramHandlerObject, paramComponentSearchCallback, paramAnonymousEventSource);
          }
        }
      });
    }
    while (HandlerUtils.post(paramComponentOwner, new Runnable()
    {
      public void run()
      {
        ComponentUtils.findComponent(this.val$owner, paramClass, paramHandlerObject, paramComponentSearchCallback);
      }
    })) {
      return false;
    }
    Log.e("ComponentUtils", "findComponent() - Fail to find component in owner thread");
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/component/ComponentUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */