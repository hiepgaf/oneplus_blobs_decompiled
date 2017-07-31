package com.oneplus.base;

import android.util.Log;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.List;

public abstract class BasicBaseObject
  extends BasicThreadDependentObject
  implements BaseObject
{
  public static final int LOG_EVENT_HANDLER = 1024;
  public static final int LOG_EVENT_HANDLER_CHANGE = 512;
  public static final int LOG_EVENT_RAISE = 256;
  public static final int LOG_PROPERTY_CALLBACK = 4;
  public static final int LOG_PROPERTY_CALLBACK_CHANGE = 2;
  public static final int LOG_PROPERTY_CHANGE = 1;
  private final SparseArray<Event> m_Events = new SparseArray();
  private volatile boolean m_IsReleased;
  private final SparseArray<Property> m_Properties = new SparseArray();
  
  protected BasicBaseObject() {}
  
  protected BasicBaseObject(String paramString)
  {
    super(paramString);
  }
  
  private boolean checkValueChanges(Object paramObject1, Object paramObject2)
  {
    if (paramObject1 != null) {
      return !paramObject1.equals(paramObject2);
    }
    return paramObject2 != null;
  }
  
  private boolean notifyPropertyChanged(Property paramProperty, Object paramObject1, Object paramObject2)
  {
    if (!checkValueChanges(paramObject1, paramObject2)) {
      return false;
    }
    paramProperty.version += 1;
    boolean bool2 = true;
    boolean bool3 = true;
    int m = paramProperty.logFlags;
    if ((m & 0x2) != 0)
    {
      i = 1;
      paramProperty.updatingCounter += 1;
      if ((m & 0x1) == 0) {}
    }
    for (;;)
    {
      try
      {
        printPropertyLog(3, paramProperty, paramObject1 + " -> " + paramObject2);
        localList = paramProperty.callbacks;
        bool1 = bool3;
        if (localList != null)
        {
          bool1 = localList.isEmpty();
          if (bool1) {
            bool1 = bool3;
          }
        }
        else
        {
          paramProperty.updatingCounter -= 1;
          if (paramProperty.updatingCounter > 0) {
            continue;
          }
          if (paramProperty.removingCallbacks == null) {
            continue;
          }
          if (paramProperty.callbacks == null) {
            continue;
          }
          j = paramProperty.removingCallbacks.size() - 1;
          if (j < 0) {
            continue;
          }
          paramObject1 = (PropertyChangedCallback)paramProperty.removingCallbacks.get(j);
          k = paramProperty.callbacks.indexOf(paramObject1);
          if (k >= 0)
          {
            if (i != 0) {
              printPropertyLog(3, paramProperty, "Remove deferred removing call-back [" + k + "] " + paramObject1);
            }
            paramProperty.callbacks.remove(k);
          }
          j -= 1;
          continue;
          i = 0;
          break;
        }
        n = paramProperty.version;
        paramObject2 = PropertyChangeEventArgs.obtain(paramObject1, paramObject2);
        localPropertyKey = paramProperty.key;
        if ((m & 0x4) == 0) {
          continue;
        }
        j = 1;
      }
      finally
      {
        List localList;
        boolean bool1;
        int n;
        PropertyKey localPropertyKey;
        int i1;
        PropertyChangedCallback localPropertyChangedCallback;
        paramProperty.updatingCounter -= 1;
        if (paramProperty.updatingCounter > 0) {
          continue;
        }
        if (paramProperty.removingCallbacks == null) {
          continue;
        }
        if (paramProperty.callbacks == null) {
          continue;
        }
        int j = paramProperty.removingCallbacks.size() - 1;
        if (j < 0) {
          continue;
        }
        paramObject2 = (PropertyChangedCallback)paramProperty.removingCallbacks.get(j);
        int k = paramProperty.callbacks.indexOf(paramObject2);
        if (k < 0) {
          continue;
        }
        if (i == 0) {
          continue;
        }
        printPropertyLog(3, paramProperty, "Remove deferred removing call-back [" + k + "] " + paramObject2);
        paramProperty.callbacks.remove(k);
        j -= 1;
        continue;
        j = 0;
        continue;
        k += 1;
        continue;
        paramProperty.removingCallbacks = null;
        if (paramProperty.addingCallbacks == null) {
          continue;
        }
        if (paramProperty.addingCallbacks.isEmpty()) {
          continue;
        }
        if (paramProperty.callbacks != null) {
          continue;
        }
        paramProperty.callbacks = new ArrayList();
        j = 0;
        k = paramProperty.addingCallbacks.size();
        if (j >= k) {
          continue;
        }
        paramObject1 = (PropertyChangedCallback)paramProperty.addingCallbacks.get(j);
        if (i == 0) {
          continue;
        }
        printPropertyLog(3, paramProperty, "Add deferred adding call-back [" + paramProperty.callbacks.size() + "] " + paramObject1);
        paramProperty.callbacks.add(paramObject1);
        j += 1;
        continue;
        paramProperty.addingCallbacks = null;
        return bool1;
        paramProperty.removingCallbacks = null;
        if (paramProperty.addingCallbacks == null) {
          continue;
        }
        if (paramProperty.addingCallbacks.isEmpty()) {
          continue;
        }
        if (paramProperty.callbacks != null) {
          continue;
        }
        paramProperty.callbacks = new ArrayList();
        j = 0;
        k = paramProperty.addingCallbacks.size();
        if (j >= k) {
          continue;
        }
        paramObject2 = (PropertyChangedCallback)paramProperty.addingCallbacks.get(j);
        if (i == 0) {
          continue;
        }
        printPropertyLog(3, paramProperty, "Add deferred adding call-back [" + paramProperty.callbacks.size() + "] " + paramObject2);
        paramProperty.callbacks.add(paramObject2);
        j += 1;
        continue;
        paramProperty.addingCallbacks = null;
      }
      k = 0;
      i1 = localList.size();
      bool1 = bool2;
      if (k < i1)
      {
        localPropertyChangedCallback = (PropertyChangedCallback)localList.get(k);
        if (j != 0) {
          printPropertyLog(3, localPropertyKey, "Call [" + k + "] " + localPropertyChangedCallback);
        }
        callPropertyChangedCallback(this, localPropertyChangedCallback, localPropertyKey, (PropertyChangeEventArgs)paramObject2);
        if (n == paramProperty.version) {
          continue;
        }
        if ((m & 0x1) != 0) {
          printPropertyLog(5, localPropertyKey, "Value changed after calling call-back [" + k + "] " + localPropertyChangedCallback);
        }
        bool1 = checkValueChanges(paramObject1, get(localPropertyKey));
      }
      ((PropertyChangeEventArgs)paramObject2).recycle();
    }
  }
  
  private void printEventLog(int paramInt, EventKey<?> paramEventKey, String paramString)
  {
    Log.println(paramInt, this.TAG, "[Event] " + paramEventKey + " : " + paramString);
  }
  
  private void printPropertyLog(int paramInt, Property paramProperty, String paramString)
  {
    printPropertyLog(paramInt, paramProperty.key, paramString);
  }
  
  private void printPropertyLog(int paramInt, PropertyKey<?> paramPropertyKey, String paramString)
  {
    Log.println(paramInt, this.TAG, "[Property] " + paramPropertyKey + " : " + paramString);
  }
  
  private <TValue> boolean setInternal(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    verifyProperty(paramPropertyKey);
    verifyAccess();
    Property localProperty = (Property)this.m_Properties.get(paramPropertyKey.id);
    if (localProperty != null) {
      if (localProperty.hasValue) {
        paramPropertyKey = localProperty.value;
      }
    }
    for (;;)
    {
      localProperty.hasValue = true;
      localProperty.value = paramTValue;
      return notifyPropertyChanged(localProperty, paramPropertyKey, paramTValue);
      paramPropertyKey = localProperty.key.defaultValue;
      continue;
      localProperty = new Property(paramPropertyKey);
      this.m_Properties.put(paramPropertyKey.id, localProperty);
      paramPropertyKey = paramPropertyKey.defaultValue;
    }
  }
  
  public <TValue> void addCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback)
  {
    if (paramPropertyChangedCallback == null) {
      throw new IllegalArgumentException("No call-back.");
    }
    verifyAccess();
    if (this.m_IsReleased) {
      return;
    }
    Property localProperty2 = (Property)this.m_Properties.get(paramPropertyKey.id);
    Property localProperty1 = localProperty2;
    if (localProperty2 == null)
    {
      localProperty1 = new Property(paramPropertyKey);
      this.m_Properties.put(paramPropertyKey.id, localProperty1);
    }
    if (localProperty1.updatingCounter <= 0)
    {
      if (localProperty1.callbacks == null) {
        localProperty1.callbacks = new ArrayList();
      }
      if ((localProperty1.logFlags & 0x2) != 0) {
        printPropertyLog(3, localProperty1, "Add call-back [" + localProperty1.callbacks.size() + "] " + paramPropertyChangedCallback);
      }
      localProperty1.callbacks.add(paramPropertyChangedCallback);
      return;
    }
    if ((localProperty1.removingCallbacks != null) && (localProperty1.removingCallbacks.remove(paramPropertyChangedCallback)))
    {
      if ((localProperty1.logFlags & 0x2) != 0) {
        printPropertyLog(3, localProperty1, "Cancel deferred removing call-back " + paramPropertyChangedCallback);
      }
      return;
    }
    if (localProperty1.addingCallbacks == null) {
      localProperty1.addingCallbacks = new ArrayList();
    }
    if ((localProperty1.logFlags & 0x2) != 0) {
      printPropertyLog(3, localProperty1, "Create deferred adding call-back " + paramPropertyChangedCallback);
    }
    localProperty1.addingCallbacks.add(paramPropertyChangedCallback);
  }
  
  public <TArgs extends EventArgs> void addHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler)
  {
    verifyAccess();
    if (paramEventHandler == null) {
      throw new IllegalArgumentException("No handler.");
    }
    if (this.m_IsReleased) {
      return;
    }
    Event localEvent2 = (Event)this.m_Events.get(paramEventKey.id);
    Event localEvent1 = localEvent2;
    if (localEvent2 == null)
    {
      localEvent1 = new Event(paramEventKey);
      this.m_Events.put(paramEventKey.id, localEvent1);
    }
    if (localEvent1.raisingCounter <= 0)
    {
      if (localEvent1.handlers == null) {
        localEvent1.handlers = new ArrayList();
      }
      if ((localEvent1.logFlags & 0x200) != 0) {
        printEventLog(3, paramEventKey, "Add handler [" + localEvent1.handlers.size() + "] " + paramEventHandler);
      }
      localEvent1.handlers.add(paramEventHandler);
      return;
    }
    if ((localEvent1.removingHandlers != null) && (localEvent1.removingHandlers.remove(paramEventHandler)))
    {
      if ((localEvent1.logFlags & 0x200) != 0) {
        printEventLog(3, paramEventKey, "Cancel deferred removing handler " + paramEventHandler);
      }
      return;
    }
    if (localEvent1.addingHandlers == null) {
      localEvent1.addingHandlers = new ArrayList();
    }
    if ((localEvent1.logFlags & 0x200) != 0) {
      printEventLog(3, paramEventKey, "Create deferred adding handler " + paramEventHandler);
    }
    localEvent1.addingHandlers.add(paramEventHandler);
  }
  
  protected <TArgs extends EventArgs> void callEventHandler(EventSource paramEventSource, EventHandler<TArgs> paramEventHandler, EventKey<TArgs> paramEventKey, TArgs paramTArgs)
  {
    paramEventHandler.onEventReceived(paramEventSource, paramEventKey, paramTArgs);
  }
  
  protected <TValue> void callPropertyChangedCallback(PropertySource paramPropertySource, PropertyChangedCallback<TValue> paramPropertyChangedCallback, PropertyKey<TValue> paramPropertyKey, PropertyChangeEventArgs<TValue> paramPropertyChangeEventArgs)
  {
    paramPropertyChangedCallback.onPropertyChanged(paramPropertySource, paramPropertyKey, paramPropertyChangeEventArgs);
  }
  
  public final void disableEventLogs(EventKey<?> paramEventKey, int paramInt)
  {
    verifyAccess();
    paramEventKey = (Event)this.m_Events.get(paramEventKey.id);
    if (paramEventKey != null) {
      paramEventKey.logFlags &= paramInt;
    }
  }
  
  public final void disablePropertyLogs(PropertyKey<?> paramPropertyKey, int paramInt)
  {
    verifyAccess();
    paramPropertyKey = (Property)this.m_Properties.get(paramPropertyKey.id);
    if (paramPropertyKey != null) {
      paramPropertyKey.logFlags &= paramInt;
    }
  }
  
  public final void enableEventLogs(EventKey<?> paramEventKey, int paramInt)
  {
    verifyAccess();
    Event localEvent2 = (Event)this.m_Events.get(paramEventKey.id);
    Event localEvent1 = localEvent2;
    if (localEvent2 == null)
    {
      localEvent1 = new Event(paramEventKey);
      this.m_Events.put(paramEventKey.id, localEvent1);
    }
    localEvent1.logFlags |= paramInt;
  }
  
  public final void enablePropertyLogs(PropertyKey<?> paramPropertyKey, int paramInt)
  {
    verifyAccess();
    Property localProperty2 = (Property)this.m_Properties.get(paramPropertyKey.id);
    Property localProperty1 = localProperty2;
    if (localProperty2 == null)
    {
      localProperty1 = new Property(paramPropertyKey);
      this.m_Properties.put(paramPropertyKey.id, localProperty1);
    }
    localProperty1.logFlags |= paramInt;
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_IS_RELEASED) {
      return Boolean.valueOf(this.m_IsReleased);
    }
    for (Property localProperty = (Property)this.m_Properties.get(paramPropertyKey.id); (localProperty != null) && (localProperty.key != paramPropertyKey); localProperty = (Property)this.m_Properties.get(paramPropertyKey.id)) {}
    if ((localProperty != null) && (localProperty.hasValue)) {
      return (TValue)localProperty.value;
    }
    return (TValue)paramPropertyKey.defaultValue;
  }
  
  protected boolean hasCallbacks(PropertyKey<?> paramPropertyKey)
  {
    for (Property localProperty = (Property)this.m_Properties.get(paramPropertyKey.id); (localProperty != null) && (localProperty.key != paramPropertyKey); localProperty = (Property)this.m_Properties.get(paramPropertyKey.id)) {}
    if (localProperty == null) {
      return false;
    }
    paramPropertyKey = localProperty.callbacks;
    return (paramPropertyKey != null) && (!paramPropertyKey.isEmpty());
  }
  
  protected boolean hasHandlers(EventKey<?> paramEventKey)
  {
    for (Event localEvent = (Event)this.m_Events.get(paramEventKey.id); (localEvent != null) && (localEvent.key != paramEventKey); localEvent = (Event)this.m_Events.get(paramEventKey.id)) {}
    if (localEvent == null) {
      return false;
    }
    paramEventKey = localEvent.handlers;
    return (paramEventKey != null) && (!paramEventKey.isEmpty());
  }
  
  protected <TValue> boolean notifyPropertyChanged(PropertyKey<TValue> paramPropertyKey, TValue paramTValue1, TValue paramTValue2)
  {
    verifyAccess();
    paramPropertyKey = (Property)this.m_Properties.get(paramPropertyKey.id);
    if (paramPropertyKey != null) {
      return notifyPropertyChanged(paramPropertyKey, paramTValue1, paramTValue2);
    }
    return checkValueChanges(paramTValue1, paramTValue2);
  }
  
  protected void onRelease() {}
  
  protected <TArgs extends EventArgs> void raise(EventKey<TArgs> paramEventKey, TArgs paramTArgs)
  {
    verifyEvent(paramEventKey);
    verifyAccess();
    if (this.m_IsReleased) {
      return;
    }
    Event localEvent = (Event)this.m_Events.get(paramEventKey.id);
    int m;
    int i;
    if (localEvent != null)
    {
      localEvent.raisingCounter += 1;
      m = localEvent.logFlags;
      if ((m & 0x100) != 0) {
        i = 1;
      }
    }
    try
    {
      localObject = localEvent.handlers;
      if (i != 0) {
        printEventLog(3, paramEventKey, "Raise [start]");
      }
      if ((localObject == null) || (((List)localObject).isEmpty()))
      {
        if (i != 0) {
          printEventLog(3, paramEventKey, "Raise [end]");
        }
        localEvent.raisingCounter -= 1;
        if (localEvent.raisingCounter > 0) {
          break label558;
        }
        if ((m & 0x200) == 0) {
          break label393;
        }
        i = 1;
      }
      for (;;)
      {
        label147:
        if (localEvent.removingHandlers != null)
        {
          if (localEvent.handlers != null)
          {
            j = localEvent.removingHandlers.size() - 1;
            for (;;)
            {
              if (j >= 0)
              {
                paramTArgs = (EventHandler)localEvent.removingHandlers.get(j);
                k = localEvent.handlers.indexOf(paramTArgs);
                if (k >= 0)
                {
                  if (i != 0) {
                    printEventLog(3, paramEventKey, "Remove deferred removing handler [" + k + "] " + paramTArgs);
                  }
                  localEvent.handlers.remove(k);
                }
                j -= 1;
                continue;
                i = 0;
                break;
                if ((m & 0x400) != 0) {}
                for (j = 1;; j = 0)
                {
                  k = 0;
                  int n = ((List)localObject).size();
                  while (k < n)
                  {
                    EventHandler localEventHandler = (EventHandler)((List)localObject).get(k);
                    if (j != 0) {
                      printEventLog(3, paramEventKey, "Call [" + k + "] " + localEventHandler);
                    }
                    callEventHandler(this, localEventHandler, paramEventKey, paramTArgs);
                    k += 1;
                  }
                  break;
                }
                label393:
                i = 0;
                break label147;
              }
            }
          }
          localEvent.removingHandlers = null;
        }
      }
      if (localEvent.addingHandlers != null)
      {
        if (!localEvent.addingHandlers.isEmpty())
        {
          if (localEvent.handlers == null) {
            localEvent.handlers = new ArrayList();
          }
          j = 0;
          k = localEvent.addingHandlers.size();
          while (j < k)
          {
            paramTArgs = (EventHandler)localEvent.addingHandlers.get(j);
            if (i != 0) {
              printEventLog(3, paramEventKey, "Add deferred adding handler [" + localEvent.handlers.size() + "] " + paramTArgs);
            }
            localEvent.handlers.add(paramTArgs);
            j += 1;
          }
        }
        localEvent.addingHandlers = null;
      }
      label558:
      return;
    }
    finally
    {
      Object localObject;
      int j;
      int k;
      localEvent.raisingCounter -= 1;
      if (localEvent.raisingCounter <= 0)
      {
        if ((m & 0x200) != 0) {
          i = 1;
        }
        while (localEvent.removingHandlers != null)
        {
          if (localEvent.handlers != null)
          {
            j = localEvent.removingHandlers.size() - 1;
            for (;;)
            {
              if (j >= 0)
              {
                localObject = (EventHandler)localEvent.removingHandlers.get(j);
                k = localEvent.handlers.indexOf(localObject);
                if (k >= 0)
                {
                  if (i != 0) {
                    printEventLog(3, paramEventKey, "Remove deferred removing handler [" + k + "] " + localObject);
                  }
                  localEvent.handlers.remove(k);
                }
                j -= 1;
                continue;
                i = 0;
                break;
              }
            }
          }
          localEvent.removingHandlers = null;
        }
        if (localEvent.addingHandlers != null)
        {
          if (!localEvent.addingHandlers.isEmpty())
          {
            if (localEvent.handlers == null) {
              localEvent.handlers = new ArrayList();
            }
            j = 0;
            k = localEvent.addingHandlers.size();
            while (j < k)
            {
              localObject = (EventHandler)localEvent.addingHandlers.get(j);
              if (i != 0) {
                printEventLog(3, paramEventKey, "Add deferred adding handler [" + localEvent.handlers.size() + "] " + localObject);
              }
              localEvent.handlers.add(localObject);
              j += 1;
            }
          }
          localEvent.addingHandlers = null;
        }
      }
    }
  }
  
  public final void release()
  {
    verifyAccess();
    if (this.m_IsReleased) {
      return;
    }
    onRelease();
    int i = this.m_Properties.size() - 1;
    Object localObject;
    while (i >= 0)
    {
      localObject = (Property)this.m_Properties.valueAt(i);
      ((Property)localObject).addingCallbacks = null;
      ((Property)localObject).removingCallbacks = null;
      ((Property)localObject).callbacks = null;
      i -= 1;
    }
    i = this.m_Events.size() - 1;
    while (i >= 0)
    {
      localObject = (Event)this.m_Events.valueAt(i);
      ((Event)localObject).addingHandlers = null;
      ((Event)localObject).removingHandlers = null;
      ((Event)localObject).handlers = null;
      i -= 1;
    }
    this.m_IsReleased = true;
  }
  
  public <TValue> void removeCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback)
  {
    if (paramPropertyChangedCallback == null) {
      return;
    }
    verifyAccess();
    if (this.m_IsReleased) {
      return;
    }
    paramPropertyKey = (Property)this.m_Properties.get(paramPropertyKey.id);
    if (paramPropertyKey == null) {
      return;
    }
    if (paramPropertyKey.updatingCounter <= 0)
    {
      if (paramPropertyKey.callbacks != null)
      {
        int i = paramPropertyKey.callbacks.indexOf(paramPropertyChangedCallback);
        if (i >= 0)
        {
          if ((paramPropertyKey.logFlags & 0x2) != 0) {
            printPropertyLog(3, paramPropertyKey, "Remove call-back [" + i + "] " + paramPropertyChangedCallback);
          }
          paramPropertyKey.callbacks.remove(i);
        }
      }
      return;
    }
    if ((paramPropertyKey.addingCallbacks != null) && (paramPropertyKey.addingCallbacks.remove(paramPropertyChangedCallback)))
    {
      if ((paramPropertyKey.logFlags & 0x2) != 0) {
        printPropertyLog(3, paramPropertyKey, "Cancel deferred adding call-back " + paramPropertyChangedCallback);
      }
      return;
    }
    if (paramPropertyKey.removingCallbacks == null) {
      paramPropertyKey.removingCallbacks = new ArrayList();
    }
    if ((paramPropertyKey.logFlags & 0x2) != 0) {
      printPropertyLog(3, paramPropertyKey, "Create deferred removing call-back " + paramPropertyChangedCallback);
    }
    paramPropertyKey.removingCallbacks.add(paramPropertyChangedCallback);
  }
  
  public <TArgs extends EventArgs> void removeHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler)
  {
    verifyAccess();
    if ((paramEventHandler == null) || (this.m_IsReleased)) {
      return;
    }
    Event localEvent = (Event)this.m_Events.get(paramEventKey.id);
    if (localEvent == null) {
      return;
    }
    if (localEvent.raisingCounter <= 0)
    {
      if (localEvent.handlers != null)
      {
        int i = localEvent.handlers.indexOf(paramEventHandler);
        if (i >= 0)
        {
          if ((localEvent.logFlags & 0x200) != 0) {
            printEventLog(3, paramEventKey, "Remove handler [" + i + "] " + paramEventHandler);
          }
          localEvent.handlers.remove(i);
        }
      }
      return;
    }
    if ((localEvent.addingHandlers != null) && (localEvent.addingHandlers.remove(paramEventHandler)))
    {
      if ((localEvent.logFlags & 0x200) != 0) {
        printEventLog(3, paramEventKey, "Cancel deferred adding handler " + paramEventHandler);
      }
      return;
    }
    if (localEvent.removingHandlers == null) {
      localEvent.removingHandlers = new ArrayList();
    }
    if ((localEvent.logFlags & 0x200) != 0) {
      printEventLog(3, paramEventKey, "Create deferred removing handler " + paramEventHandler);
    }
    localEvent.removingHandlers.add(paramEventHandler);
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey.isReadOnly()) {
      throw new RuntimeException("Property " + paramPropertyKey + " is read-only.");
    }
    return setInternal(paramPropertyKey, paramTValue);
  }
  
  protected <TValue> boolean setReadOnly(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_IS_RELEASED) {
      throw new IllegalArgumentException("Cannot set property " + paramPropertyKey + ".");
    }
    if (!paramPropertyKey.isReadOnly()) {
      return set(paramPropertyKey, paramTValue);
    }
    return setInternal(paramPropertyKey, paramTValue);
  }
  
  protected void verifyEvent(EventKey<?> paramEventKey)
  {
    if (!paramEventKey.ownerType.isAssignableFrom(getClass())) {
      throw new IllegalArgumentException("Event " + paramEventKey + " is not owned by type " + getClass() + ".");
    }
  }
  
  protected void verifyProperty(PropertyKey<?> paramPropertyKey)
  {
    if ((paramPropertyKey.ownerType.isAssignableFrom(getClass())) || (paramPropertyKey.isAttachable())) {
      return;
    }
    throw new IllegalArgumentException("Property " + paramPropertyKey + " is not owned by type " + getClass() + ".");
  }
  
  protected final void verifyReleaseState()
  {
    if (this.m_IsReleased) {
      throw new RuntimeException("Object has been released.");
    }
  }
  
  private static final class Event
  {
    public List<EventHandler<?>> addingHandlers;
    public List<EventHandler<?>> handlers;
    public final EventKey<?> key;
    public int logFlags;
    public int raisingCounter;
    public List<EventHandler<?>> removingHandlers;
    
    public Event(EventKey<?> paramEventKey)
    {
      this.key = paramEventKey;
    }
  }
  
  private static final class Property
  {
    public List<PropertyChangedCallback<?>> addingCallbacks;
    public List<PropertyChangedCallback<?>> callbacks;
    public volatile boolean hasValue;
    public final PropertyKey<?> key;
    public int logFlags;
    public List<PropertyChangedCallback<?>> removingCallbacks;
    public int updatingCounter;
    public volatile Object value;
    public int version;
    
    public Property(PropertyKey<?> paramPropertyKey)
    {
      this.key = paramPropertyKey;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/BasicBaseObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */