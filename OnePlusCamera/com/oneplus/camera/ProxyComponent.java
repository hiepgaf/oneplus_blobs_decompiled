package com.oneplus.camera;

import android.os.Message;
import android.os.SystemClock;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.RecyclableObject;
import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentOwner;
import com.oneplus.base.component.ComponentSearchCallback;
import com.oneplus.base.component.ComponentUtils;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class ProxyComponent<TTarget extends Component>
  extends CameraComponent
{
  private static final int MSG_TARGET_EVENT_RAISED = -10000;
  private static final int MSG_TARGET_PROPERTY_CHANGED = -10001;
  private boolean m_IsBindingToTarget;
  private PropertyChangedCallback<Boolean> m_IsCameraThreadStartedCallback;
  private final LinkedList<ProxyComponent<TTarget>.AsyncMethodCallHandle> m_PendingAsyncMethodCalls = new LinkedList();
  private TTarget m_Target;
  private final Class<? extends TTarget> m_TargetClass;
  private ComponentOwner m_TargetOwner;
  private final ComponentSearchCallback<TTarget> m_TargetSearchCallback = new ComponentSearchCallback()
  {
    public void onComponentFound(TTarget paramAnonymousTTarget)
    {
      ProxyComponent.-wrap3(ProxyComponent.this, paramAnonymousTTarget);
    }
  };
  
  protected ProxyComponent(String paramString, CameraActivity paramCameraActivity, ComponentOwner paramComponentOwner, Class<? extends TTarget> paramClass)
  {
    super(paramString, paramCameraActivity, true);
    if (paramComponentOwner == null) {
      throw new IllegalArgumentException("No target component owner.");
    }
    if (paramClass == null) {
      throw new IllegalArgumentException("No target type.");
    }
    this.m_TargetClass = paramClass;
    this.m_TargetOwner = paramComponentOwner;
  }
  
  protected ProxyComponent(String paramString, CameraThread paramCameraThread, ComponentOwner paramComponentOwner, Class<? extends TTarget> paramClass)
  {
    super(paramString, paramCameraThread, true);
    if (paramComponentOwner == null) {
      throw new IllegalArgumentException("No target component owner.");
    }
    if (paramClass == null) {
      throw new IllegalArgumentException("No target type.");
    }
    this.m_TargetClass = paramClass;
    this.m_TargetOwner = paramComponentOwner;
  }
  
  /* Error */
  private void callTargetMethod(ProxyComponent<TTarget>.AsyncMethodCallHandle paramProxyComponent)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 116	com/oneplus/camera/ProxyComponent$AsyncMethodCallHandle:method	Ljava/lang/reflect/Method;
    //   4: aload_0
    //   5: getfield 55	com/oneplus/camera/ProxyComponent:m_Target	Lcom/oneplus/base/component/Component;
    //   8: aload_1
    //   9: getfield 120	com/oneplus/camera/ProxyComponent$AsyncMethodCallHandle:args	[Ljava/lang/Object;
    //   12: invokevirtual 126	java/lang/reflect/Method:invoke	(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    //   15: astore_2
    //   16: aload_2
    //   17: instanceof 128
    //   20: ifeq +22 -> 42
    //   23: aload_1
    //   24: monitorenter
    //   25: aload_1
    //   26: invokestatic 132	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   29: ifeq +14 -> 43
    //   32: aload_1
    //   33: aload_2
    //   34: checkcast 128	com/oneplus/base/Handle
    //   37: putfield 136	com/oneplus/camera/ProxyComponent$AsyncMethodCallHandle:resultHandle	Lcom/oneplus/base/Handle;
    //   40: aload_1
    //   41: monitorexit
    //   42: return
    //   43: aload_2
    //   44: checkcast 128	com/oneplus/base/Handle
    //   47: invokestatic 140	com/oneplus/base/Handle:close	(Lcom/oneplus/base/Handle;)Lcom/oneplus/base/Handle;
    //   50: pop
    //   51: goto -11 -> 40
    //   54: astore_2
    //   55: aload_1
    //   56: monitorexit
    //   57: aload_2
    //   58: athrow
    //   59: astore_1
    //   60: aload_0
    //   61: getfield 144	com/oneplus/camera/ProxyComponent:TAG	Ljava/lang/String;
    //   64: ldc -110
    //   66: aload_1
    //   67: invokestatic 152	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   70: new 154	java/lang/RuntimeException
    //   73: dup
    //   74: ldc -100
    //   76: aload_1
    //   77: invokespecial 159	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   80: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	81	0	this	ProxyComponent
    //   0	81	1	paramProxyComponent	ProxyComponent<TTarget>.AsyncMethodCallHandle
    //   15	29	2	localObject1	Object
    //   54	4	2	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   25	40	54	finally
    //   43	51	54	finally
    //   0	25	59	java/lang/Throwable
    //   40	42	59	java/lang/Throwable
    //   55	59	59	java/lang/Throwable
  }
  
  private void cancelCallingTargetMethod(final ProxyComponent<TTarget>.AsyncMethodCallHandle paramProxyComponent)
  {
    synchronized (this.m_PendingAsyncMethodCalls)
    {
      boolean bool = this.m_PendingAsyncMethodCalls.remove(paramProxyComponent);
      if (bool) {
        return;
      }
    }
  }
  
  private void onCameraThreadStarted()
  {
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "onCameraThreadStarted() - Component is not running");
      return;
    }
    if (!this.m_IsBindingToTarget) {
      return;
    }
    Log.v(this.TAG, "onCameraThreadStarted() - Start binding");
    ComponentUtils.findComponent(this.m_TargetOwner, this.m_TargetClass, this, this.m_TargetSearchCallback);
  }
  
  private void onTargetFound(final TTarget paramTTarget)
  {
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "onTargetFound() - Component is not running");
      return;
    }
    this.m_Target = paramTTarget;
    this.m_IsBindingToTarget = false;
    final ArrayList localArrayList1 = new ArrayList();
    final ArrayList localArrayList2 = new ArrayList();
    onBindingToTargetEvents(localArrayList1);
    onBindingToTargetProperties(localArrayList2);
    if ((localArrayList1.isEmpty()) && (localArrayList2.isEmpty())) {}
    while (HandlerUtils.post(this.m_TargetOwner, new Runnable()
    {
      public void run()
      {
        int i;
        if (!localArrayList1.isEmpty())
        {
          localObject1 = new EventHandler()
          {
            public void onEventReceived(EventSource paramAnonymous2EventSource, EventKey paramAnonymous2EventKey, EventArgs paramAnonymous2EventArgs)
            {
              HandlerUtils.sendMessage(ProxyComponent.this, 55536, 0, 0, new Object[] { Long.valueOf(SystemClock.elapsedRealtimeNanos()), paramAnonymous2EventKey, paramAnonymous2EventArgs.clone() });
            }
          };
          i = localArrayList1.size() - 1;
          while (i >= 0)
          {
            paramTTarget.addHandler((EventKey)localArrayList1.get(i), (EventHandler)localObject1);
            i -= 1;
          }
        }
        if (!localArrayList2.isEmpty())
        {
          localObject1 = new PropertyChangedCallback()
          {
            public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey paramAnonymous2PropertyKey, PropertyChangeEventArgs paramAnonymous2PropertyChangeEventArgs)
            {
              HandlerUtils.sendMessage(ProxyComponent.this, 55535, 0, 0, new Object[] { Long.valueOf(SystemClock.elapsedRealtimeNanos()), paramAnonymous2PropertyKey, paramAnonymous2PropertyChangeEventArgs.clone() });
            }
          };
          i = localArrayList2.size() - 1;
          while (i >= 0)
          {
            ??? = (PropertyKey)localArrayList2.get(i);
            Object localObject4 = paramTTarget.get((PropertyKey)???);
            paramTTarget.addCallback((PropertyKey)???, (PropertyChangedCallback)localObject1);
            HandlerUtils.sendMessage(ProxyComponent.this, 55535, 0, 0, new Object[] { Long.valueOf(SystemClock.elapsedRealtimeNanos()), ???, PropertyChangeEventArgs.obtain(((PropertyKey)???).defaultValue, localObject4) });
            i -= 1;
          }
        }
        Object localObject1 = null;
        synchronized (ProxyComponent.-get0(ProxyComponent.this))
        {
          if (!ProxyComponent.-get0(ProxyComponent.this).isEmpty())
          {
            localObject1 = new ProxyComponent.AsyncMethodCallHandle[ProxyComponent.-get0(ProxyComponent.this).size()];
            ProxyComponent.-get0(ProxyComponent.this).toArray((Object[])localObject1);
            ProxyComponent.-get0(ProxyComponent.this).clear();
          }
          if (localObject1 != null)
          {
            i = 0;
            int j = localObject1.length;
            if (i < j)
            {
              ProxyComponent.-wrap0(ProxyComponent.this, localObject1[i]);
              i += 1;
            }
          }
        }
      }
    }))
    {
      onTargetBound(paramTTarget);
      return;
    }
    Log.e(this.TAG, "onTargetFound() - Fail to bind to target events and properties asynchronously");
    this.m_Target = null;
  }
  
  protected final boolean bindToTarget()
  {
    verifyAccess();
    if ((this.m_Target != null) || (this.m_IsBindingToTarget)) {
      return true;
    }
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "bindToTarget() - Component is not running");
      return false;
    }
    CameraActivity localCameraActivity;
    if ((this.m_TargetOwner instanceof CameraThread))
    {
      localCameraActivity = getCameraActivity();
      if ((localCameraActivity.isDependencyThread()) && (!((Boolean)localCameraActivity.get(CameraActivity.PROP_IS_CAMERA_THREAD_STARTED)).booleanValue())) {}
    }
    else
    {
      if (!ComponentUtils.findComponent(this.m_TargetOwner, this.m_TargetClass, this, this.m_TargetSearchCallback)) {
        this.m_IsBindingToTarget = true;
      }
      return true;
    }
    Log.v(this.TAG, "bindToTarget() - Start binding when camera thread starts");
    this.m_IsBindingToTarget = true;
    this.m_IsCameraThreadStartedCallback = new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
      {
        ProxyComponent.-wrap2(ProxyComponent.this);
      }
    };
    localCameraActivity.addCallback(CameraActivity.PROP_IS_CAMERA_THREAD_STARTED, this.m_IsCameraThreadStartedCallback);
    return true;
  }
  
  protected final Handle callTargetMethod(String arg1, final Class<?>[] paramArrayOfClass, Object... paramVarArgs)
  {
    if (??? == null) {
      throw new IllegalArgumentException("No target method name");
    }
    try
    {
      paramArrayOfClass = this.m_TargetClass.getMethod(???, paramArrayOfClass);
      paramArrayOfClass = new AsyncMethodCallHandle(paramArrayOfClass, paramVarArgs);
      if ((this.m_Target != null) && (this.m_Target.isDependencyThread()))
      {
        callTargetMethod(paramArrayOfClass);
        return paramArrayOfClass;
      }
    }
    catch (Throwable paramArrayOfClass)
    {
      Log.e(this.TAG, "callTargetMethod() - Cannot find method '" + ??? + "'", paramArrayOfClass);
      return null;
    }
    synchronized (this.m_PendingAsyncMethodCalls)
    {
      this.m_PendingAsyncMethodCalls.add(paramArrayOfClass);
      paramArrayOfClass.callingRunnable = new Runnable()
      {
        public void run()
        {
          if (ProxyComponent.-get1(ProxyComponent.this) == null) {
            return;
          }
          synchronized (ProxyComponent.-get0(ProxyComponent.this))
          {
            ProxyComponent.-get0(ProxyComponent.this).remove(paramArrayOfClass);
            ProxyComponent.-wrap0(ProxyComponent.this, paramArrayOfClass);
            return;
          }
        }
      };
      HandlerUtils.post(this.m_TargetOwner, paramArrayOfClass.callingRunnable);
      return paramArrayOfClass;
    }
  }
  
  protected final TTarget getTarget()
  {
    return this.m_Target;
  }
  
  protected final ComponentOwner getTargetOwner()
  {
    return this.m_TargetOwner;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    case -10000: 
      paramMessage = (Object[])paramMessage.obj;
      onTargetEventRaised(((Long)paramMessage[0]).longValue(), (EventKey)paramMessage[1], (EventArgs)paramMessage[2]);
      return;
    }
    paramMessage = (Object[])paramMessage.obj;
    onTargetPropertyChanged(((Long)paramMessage[0]).longValue(), (PropertyKey)paramMessage[1], (PropertyChangeEventArgs)paramMessage[2]);
  }
  
  protected final boolean isTargetBound()
  {
    return this.m_Target != null;
  }
  
  protected void onBindingToTargetEvents(List<EventKey<?>> paramList) {}
  
  protected void onBindingToTargetProperties(List<PropertyKey<?>> paramList) {}
  
  protected void onDeinitialize()
  {
    if (this.m_IsCameraThreadStartedCallback != null)
    {
      getCameraActivity().removeCallback(CameraActivity.PROP_IS_CAMERA_THREAD_STARTED, this.m_IsCameraThreadStartedCallback);
      this.m_IsCameraThreadStartedCallback = null;
    }
    super.onDeinitialize();
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    bindToTarget();
  }
  
  protected void onTargetBound(TTarget paramTTarget) {}
  
  protected void onTargetEventRaised(long paramLong, EventKey<?> paramEventKey, EventArgs paramEventArgs)
  {
    raise(paramEventKey, paramEventArgs);
    if ((paramEventArgs instanceof RecyclableObject)) {
      ((RecyclableObject)paramEventArgs).recycle();
    }
  }
  
  protected void onTargetPropertyChanged(long paramLong, PropertyKey<?> paramPropertyKey, PropertyChangeEventArgs<?> paramPropertyChangeEventArgs)
  {
    if (paramPropertyKey != PROP_IS_RELEASED)
    {
      if (!paramPropertyKey.isReadOnly()) {
        break label31;
      }
      super.setReadOnly(paramPropertyKey, paramPropertyChangeEventArgs.getNewValue());
    }
    for (;;)
    {
      paramPropertyChangeEventArgs.recycle();
      return;
      label31:
      super.set(paramPropertyKey, paramPropertyChangeEventArgs.getNewValue());
    }
  }
  
  private final class AsyncMethodCallHandle
    extends Handle
  {
    public final Object[] args;
    public Runnable callingRunnable;
    public final Method method;
    public volatile Handle resultHandle;
    
    public AsyncMethodCallHandle(Method paramMethod, Object... paramVarArgs)
    {
      super();
      this.method = paramMethod;
      this.args = paramVarArgs;
    }
    
    protected void onClose(int paramInt)
    {
      ProxyComponent.-wrap1(ProxyComponent.this, this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ProxyComponent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */