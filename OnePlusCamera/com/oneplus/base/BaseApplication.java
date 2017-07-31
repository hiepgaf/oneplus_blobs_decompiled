package com.oneplus.base;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Handler;
import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.base.component.ComponentEventArgs;
import com.oneplus.base.component.ComponentManager;
import com.oneplus.base.component.ComponentOwner;
import java.util.Locale;

public abstract class BaseApplication
  extends Application
  implements ComponentOwner
{
  private static String[] INIT_CHECKING_PERMISSIONS = { "android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE" };
  public static final int LOG_EVENT_HANDLER = 1024;
  public static final int LOG_EVENT_HANDLER_CHANGE = 512;
  public static final int LOG_EVENT_RAISE = 256;
  public static final int LOG_PROPERTY_CALLBACK = 4;
  public static final int LOG_PROPERTY_CALLBACK_CHANGE = 2;
  public static final int LOG_PROPERTY_CHANGE = 1;
  public static final PropertyKey<Boolean> PROP_IS_READ_STORAGE_PERM_GRANTED = new PropertyKey("IsReadStoragePermissionGranted", Boolean.class, BaseApplication.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_RTL_LAYOUT = new PropertyKey("IsRtlLayout", Boolean.class, BaseApplication.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_WRITE_STORAGE_PERM_GRANTED = new PropertyKey("IsWriteStoragePermissionGranted", Boolean.class, BaseApplication.class, Boolean.valueOf(false));
  public static final PropertyKey<Locale> PROP_LOCALE = new PropertyKey("Locale", Locale.class, BaseApplication.class, Locale.US);
  private static final String STATIC_TAG = "BaseApplication";
  private static volatile BaseApplication m_Current;
  protected final String TAG = getClass().getSimpleName();
  private final BaseObjectAdapter m_BaseObjectAdapter = new BaseObjectAdapter(this, this.TAG);
  private final ComponentManager m_ComponentManager = new ComponentManager();
  private volatile Handler m_Handler;
  private volatile Locale m_Locale = Locale.US;
  private final Thread m_MainThread = Thread.currentThread();
  
  protected BaseApplication()
  {
    this.m_ComponentManager.addHandler(ComponentManager.EVENT_COMPONENT_ADDED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
      {
        BaseApplication.-get0(BaseApplication.this).raise(BaseApplication.EVENT_COMPONENT_ADDED, paramAnonymousComponentEventArgs);
      }
    });
    this.m_ComponentManager.addHandler(ComponentManager.EVENT_COMPONENT_REMOVED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
      {
        BaseApplication.-get0(BaseApplication.this).raise(BaseApplication.EVENT_COMPONENT_REMOVED, paramAnonymousComponentEventArgs);
      }
    });
    enablePropertyLogs(PROP_IS_READ_STORAGE_PERM_GRANTED, 1);
    enablePropertyLogs(PROP_IS_RTL_LAYOUT, 1);
    enablePropertyLogs(PROP_IS_WRITE_STORAGE_PERM_GRANTED, 1);
  }
  
  /* Error */
  public static BaseApplication current()
  {
    // Byte code:
    //   0: getstatic 156	com/oneplus/base/BaseApplication:m_Current	Lcom/oneplus/base/BaseApplication;
    //   3: ifnull +7 -> 10
    //   6: getstatic 156	com/oneplus/base/BaseApplication:m_Current	Lcom/oneplus/base/BaseApplication;
    //   9: areturn
    //   10: ldc 2
    //   12: monitorenter
    //   13: getstatic 156	com/oneplus/base/BaseApplication:m_Current	Lcom/oneplus/base/BaseApplication;
    //   16: ifnonnull +22 -> 38
    //   19: ldc 36
    //   21: ldc -98
    //   23: invokestatic 164	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   26: ldc 2
    //   28: invokevirtual 167	java/lang/Class:wait	()V
    //   31: ldc 36
    //   33: ldc -87
    //   35: invokestatic 164	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   38: ldc 2
    //   40: monitorexit
    //   41: getstatic 156	com/oneplus/base/BaseApplication:m_Current	Lcom/oneplus/base/BaseApplication;
    //   44: areturn
    //   45: astore_0
    //   46: ldc 36
    //   48: ldc -85
    //   50: aload_0
    //   51: invokestatic 175	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   54: goto -23 -> 31
    //   57: astore_0
    //   58: ldc 2
    //   60: monitorexit
    //   61: aload_0
    //   62: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   45	6	0	localInterruptedException	InterruptedException
    //   57	5	0	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   26	31	45	java/lang/InterruptedException
    //   13	26	57	finally
    //   26	31	57	finally
    //   31	38	57	finally
    //   46	54	57	finally
  }
  
  public <TValue> void addCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback)
  {
    this.m_BaseObjectAdapter.addCallback(paramPropertyKey, paramPropertyChangedCallback);
  }
  
  public final void addComponentBuilders(ComponentBuilder[] paramArrayOfComponentBuilder)
  {
    verifyAccess();
    this.m_ComponentManager.addComponentBuilders(paramArrayOfComponentBuilder, new Object[] { this });
  }
  
  public <TArgs extends EventArgs> void addHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler)
  {
    this.m_BaseObjectAdapter.addHandler(paramEventKey, paramEventHandler);
  }
  
  protected final void disableEventLogs(EventKey<?> paramEventKey, int paramInt)
  {
    this.m_BaseObjectAdapter.disableEventLogs(paramEventKey, paramInt);
  }
  
  protected final void disablePropertyLogs(PropertyKey<?> paramPropertyKey, int paramInt)
  {
    this.m_BaseObjectAdapter.disablePropertyLogs(paramPropertyKey, paramInt);
  }
  
  protected final void enableEventLogs(EventKey<?> paramEventKey, int paramInt)
  {
    this.m_BaseObjectAdapter.enableEventLogs(paramEventKey, paramInt);
  }
  
  protected final void enablePropertyLogs(PropertyKey<?> paramPropertyKey, int paramInt)
  {
    this.m_BaseObjectAdapter.enablePropertyLogs(paramPropertyKey, paramInt);
  }
  
  public <TComponent extends Component> TComponent findComponent(Class<TComponent> paramClass)
  {
    return this.m_ComponentManager.findComponent(paramClass, new Object[] { this });
  }
  
  public <TComponent extends Component> TComponent[] findComponents(Class<TComponent> paramClass)
  {
    return this.m_ComponentManager.findComponents(paramClass, new Object[] { this });
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_LOCALE) {
      return this.m_Locale;
    }
    return (TValue)this.m_BaseObjectAdapter.get(paramPropertyKey);
  }
  
  public Handler getHandler()
  {
    return this.m_Handler;
  }
  
  public boolean isDependencyThread()
  {
    return Thread.currentThread() == this.m_MainThread;
  }
  
  public void notifyPermissionDenied(String paramString)
  {
    verifyAccess();
    if (paramString != null)
    {
      if (!paramString.equals("android.permission.READ_EXTERNAL_STORAGE")) {
        break label30;
      }
      setReadOnly(PROP_IS_READ_STORAGE_PERM_GRANTED, Boolean.valueOf(false));
    }
    label30:
    while (!paramString.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
      return;
    }
    setReadOnly(PROP_IS_WRITE_STORAGE_PERM_GRANTED, Boolean.valueOf(false));
  }
  
  public void notifyPermissionGranted(String paramString)
  {
    verifyAccess();
    if (paramString != null)
    {
      if (!paramString.equals("android.permission.READ_EXTERNAL_STORAGE")) {
        break label30;
      }
      setReadOnly(PROP_IS_READ_STORAGE_PERM_GRANTED, Boolean.valueOf(true));
    }
    label30:
    while (!paramString.equals("android.permission.WRITE_EXTERNAL_STORAGE")) {
      return;
    }
    setReadOnly(PROP_IS_WRITE_STORAGE_PERM_GRANTED, Boolean.valueOf(true));
  }
  
  protected <TValue> boolean notifyPropertyChanged(PropertyKey<TValue> paramPropertyKey, TValue paramTValue1, TValue paramTValue2)
  {
    return this.m_BaseObjectAdapter.notifyPropertyChanged(paramPropertyKey, paramTValue1, paramTValue2);
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    boolean bool = true;
    super.onConfigurationChanged(paramConfiguration);
    Object localObject = paramConfiguration.locale;
    if ((localObject == null) || (((Locale)localObject).equals(this.m_Locale)))
    {
      localObject = PROP_IS_RTL_LAYOUT;
      if (paramConfiguration.getLayoutDirection() != 1) {
        break label78;
      }
    }
    for (;;)
    {
      setReadOnly((PropertyKey)localObject, Boolean.valueOf(bool));
      return;
      Locale localLocale = this.m_Locale;
      this.m_Locale = ((Locale)localObject);
      this.m_BaseObjectAdapter.notifyPropertyChanged(PROP_LOCALE, localLocale, localObject);
      break;
      label78:
      bool = false;
    }
  }
  
  /* Error */
  public void onCreate()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 262	android/app/Application:onCreate	()V
    //   4: aload_0
    //   5: getfield 113	com/oneplus/base/BaseApplication:TAG	Ljava/lang/String;
    //   8: ldc_w 264
    //   11: invokestatic 164	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   14: invokestatic 269	com/oneplus/base/ThreadMonitor:prepare	()V
    //   17: aload_0
    //   18: aload_0
    //   19: invokevirtual 273	com/oneplus/base/BaseApplication:getResources	()Landroid/content/res/Resources;
    //   22: invokevirtual 279	android/content/res/Resources:getConfiguration	()Landroid/content/res/Configuration;
    //   25: getfield 254	android/content/res/Configuration:locale	Ljava/util/Locale;
    //   28: putfield 101	com/oneplus/base/BaseApplication:m_Locale	Ljava/util/Locale;
    //   31: getstatic 76	com/oneplus/base/BaseApplication:PROP_IS_RTL_LAYOUT	Lcom/oneplus/base/PropertyKey;
    //   34: astore_3
    //   35: aload_0
    //   36: invokevirtual 273	com/oneplus/base/BaseApplication:getResources	()Landroid/content/res/Resources;
    //   39: invokevirtual 279	android/content/res/Resources:getConfiguration	()Landroid/content/res/Configuration;
    //   42: invokevirtual 259	android/content/res/Configuration:getLayoutDirection	()I
    //   45: iconst_1
    //   46: if_icmpne +52 -> 98
    //   49: iconst_1
    //   50: istore_2
    //   51: aload_0
    //   52: aload_3
    //   53: iload_2
    //   54: invokestatic 66	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   57: invokevirtual 239	com/oneplus/base/BaseApplication:setReadOnly	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;)Z
    //   60: pop
    //   61: getstatic 97	com/oneplus/base/BaseApplication:INIT_CHECKING_PERMISSIONS	[Ljava/lang/String;
    //   64: arraylength
    //   65: iconst_1
    //   66: isub
    //   67: istore_1
    //   68: iload_1
    //   69: iflt +34 -> 103
    //   72: getstatic 97	com/oneplus/base/BaseApplication:INIT_CHECKING_PERMISSIONS	[Ljava/lang/String;
    //   75: iload_1
    //   76: aaload
    //   77: astore_3
    //   78: aload_0
    //   79: aload_3
    //   80: invokevirtual 283	com/oneplus/base/BaseApplication:checkSelfPermission	(Ljava/lang/String;)I
    //   83: ifne +8 -> 91
    //   86: aload_0
    //   87: aload_3
    //   88: invokevirtual 285	com/oneplus/base/BaseApplication:notifyPermissionGranted	(Ljava/lang/String;)V
    //   91: iload_1
    //   92: iconst_1
    //   93: isub
    //   94: istore_1
    //   95: goto -27 -> 68
    //   98: iconst_0
    //   99: istore_2
    //   100: goto -49 -> 51
    //   103: ldc 2
    //   105: monitorenter
    //   106: getstatic 156	com/oneplus/base/BaseApplication:m_Current	Lcom/oneplus/base/BaseApplication;
    //   109: ifnonnull +45 -> 154
    //   112: aload_0
    //   113: new 287	android/os/Handler
    //   116: dup
    //   117: invokespecial 288	android/os/Handler:<init>	()V
    //   120: putfield 227	com/oneplus/base/BaseApplication:m_Handler	Landroid/os/Handler;
    //   123: aload_0
    //   124: putstatic 156	com/oneplus/base/BaseApplication:m_Current	Lcom/oneplus/base/BaseApplication;
    //   127: ldc 2
    //   129: invokevirtual 291	java/lang/Class:notifyAll	()V
    //   132: ldc 2
    //   134: monitorexit
    //   135: aload_0
    //   136: getfield 131	com/oneplus/base/BaseApplication:m_ComponentManager	Lcom/oneplus/base/component/ComponentManager;
    //   139: getstatic 297	com/oneplus/base/component/ComponentCreationPriority:NORMAL	Lcom/oneplus/base/component/ComponentCreationPriority;
    //   142: iconst_1
    //   143: anewarray 188	java/lang/Object
    //   146: dup
    //   147: iconst_0
    //   148: aload_0
    //   149: aastore
    //   150: invokevirtual 301	com/oneplus/base/component/ComponentManager:createComponents	(Lcom/oneplus/base/component/ComponentCreationPriority;[Ljava/lang/Object;)V
    //   153: return
    //   154: aload_0
    //   155: getstatic 156	com/oneplus/base/BaseApplication:m_Current	Lcom/oneplus/base/BaseApplication;
    //   158: getfield 227	com/oneplus/base/BaseApplication:m_Handler	Landroid/os/Handler;
    //   161: putfield 227	com/oneplus/base/BaseApplication:m_Handler	Landroid/os/Handler;
    //   164: goto -41 -> 123
    //   167: astore_3
    //   168: ldc 2
    //   170: monitorexit
    //   171: aload_3
    //   172: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	173	0	this	BaseApplication
    //   67	28	1	i	int
    //   50	50	2	bool	boolean
    //   34	54	3	localObject1	Object
    //   167	5	3	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   106	123	167	finally
    //   123	132	167	finally
    //   154	164	167	finally
  }
  
  public void onTerminate()
  {
    Log.v(this.TAG, "onTerminate()");
    this.m_ComponentManager.release();
    this.m_BaseObjectAdapter.release();
    super.onTerminate();
  }
  
  protected <TArgs extends EventArgs> void raise(EventKey<TArgs> paramEventKey, TArgs paramTArgs)
  {
    this.m_BaseObjectAdapter.raise(paramEventKey, paramTArgs);
  }
  
  public void release() {}
  
  public <TValue> void removeCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback)
  {
    this.m_BaseObjectAdapter.removeCallback(paramPropertyKey, paramPropertyChangedCallback);
  }
  
  public void removeComponent(Component paramComponent)
  {
    this.m_ComponentManager.removeComponent(paramComponent);
  }
  
  public <TArgs extends EventArgs> void removeHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler)
  {
    this.m_BaseObjectAdapter.removeHandler(paramEventKey, paramEventHandler);
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    return this.m_BaseObjectAdapter.set(paramPropertyKey, paramTValue);
  }
  
  protected <TValue> boolean setReadOnly(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    return this.m_BaseObjectAdapter.setReadOnly(paramPropertyKey, paramTValue);
  }
  
  protected final void verifyAccess()
  {
    if (Thread.currentThread() != this.m_MainThread) {
      throw new IllegalAccessError("Cross-thread access");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/BaseApplication.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */