package com.oneplus.base.component;

import com.oneplus.base.BaseObject;
import com.oneplus.base.EventKey;
import com.oneplus.base.HandlerBaseObject;
import com.oneplus.base.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ComponentManager
  extends HandlerBaseObject
{
  public static final EventKey<ComponentEventArgs<Component>> EVENT_COMPONENT_ADDED = new EventKey("ComponentAdded", ComponentEventArgs.class, ComponentManager.class);
  public static final EventKey<ComponentEventArgs<Component>> EVENT_COMPONENT_REMOVED = new EventKey("ComponentRemoved", ComponentEventArgs.class, ComponentManager.class);
  private final List<ComponentBuilder> m_Builders = new ArrayList();
  private final List<Component> m_Components = new ArrayList();
  private final HashSet<ComponentCreationPriority> m_CreatePriorities = new HashSet();
  private final ReentrantLock m_Lock = new ReentrantLock();
  
  public ComponentManager()
  {
    super(true);
  }
  
  private Component createComponent(ComponentBuilder paramComponentBuilder, boolean paramBoolean, Object... paramVarArgs)
  {
    Object[] arrayOfObject = null;
    try
    {
      paramVarArgs = paramComponentBuilder.create(paramVarArgs);
      if (paramVarArgs == null)
      {
        arrayOfObject = paramVarArgs;
        Log.w(this.TAG, "createComponent() - Component is unsupported, builder : " + paramComponentBuilder);
        return null;
      }
      arrayOfObject = paramVarArgs;
      Log.d(this.TAG, "createComponent() - Component : " + paramVarArgs);
      if (paramBoolean)
      {
        arrayOfObject = paramVarArgs;
        if (!paramVarArgs.initialize()) {}
      }
      else
      {
        arrayOfObject = paramVarArgs;
        this.m_Components.add(paramVarArgs);
        return paramVarArgs;
      }
      arrayOfObject = paramVarArgs;
      Log.w(this.TAG, "createComponent() - Release " + paramVarArgs);
      arrayOfObject = paramVarArgs;
      paramVarArgs.release();
      return null;
    }
    catch (Throwable paramVarArgs)
    {
      Log.e(this.TAG, "createComponent() - Fail to create component by builder " + paramComponentBuilder, paramVarArgs);
      if (arrayOfObject != null)
      {
        Log.w(this.TAG, "createComponent() - Release " + arrayOfObject);
        arrayOfObject.release();
      }
    }
    return null;
  }
  
  private boolean initializeComponent(Component paramComponent)
  {
    switch (-getcom-oneplus-base-component-ComponentStateSwitchesValues()[((ComponentState)paramComponent.get(Component.PROP_STATE)).ordinal()])
    {
    default: 
      return false;
    case 1: 
    case 3: 
      return true;
    }
    Log.d(this.TAG, "initializeComponent() - Component : " + paramComponent);
    try
    {
      boolean bool = paramComponent.initialize();
      if (!bool) {
        Log.e(this.TAG, "initializeComponent() - Fail to initialize " + paramComponent);
      }
      raise(EVENT_COMPONENT_ADDED, new ComponentEventArgs(paramComponent));
      return bool;
    }
    catch (Throwable localThrowable)
    {
      Log.e(this.TAG, "initializeComponent() - Fail to initialize " + paramComponent, localThrowable);
    }
    return false;
  }
  
  private void removeComponentInternal(Component paramComponent)
  {
    if (((Boolean)paramComponent.get(BaseObject.PROP_IS_RELEASED)).booleanValue()) {
      return;
    }
    if (!this.m_Components.remove(paramComponent)) {
      return;
    }
    Log.w(this.TAG, "removeComponentInternal() - Component : " + paramComponent);
    raise(EVENT_COMPONENT_REMOVED, new ComponentEventArgs(paramComponent));
    paramComponent.release();
  }
  
  /* Error */
  public void addComponentBuilder(ComponentBuilder paramComponentBuilder, Object... paramVarArgs)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 118	com/oneplus/base/component/ComponentManager:m_Lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: invokevirtual 228	java/util/concurrent/locks/ReentrantLock:lock	()V
    //   7: aload_0
    //   8: invokevirtual 231	com/oneplus/base/component/ComponentManager:verifyAccess	()V
    //   11: aload_0
    //   12: invokevirtual 234	com/oneplus/base/component/ComponentManager:verifyReleaseState	()V
    //   15: aload_0
    //   16: getfield 113	com/oneplus/base/component/ComponentManager:m_CreatePriorities	Ljava/util/HashSet;
    //   19: aload_1
    //   20: invokeinterface 238 1 0
    //   25: invokevirtual 241	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   28: ifeq +19 -> 47
    //   31: aload_0
    //   32: aload_1
    //   33: iconst_1
    //   34: aload_2
    //   35: invokespecial 243	com/oneplus/base/component/ComponentManager:createComponent	(Lcom/oneplus/base/component/ComponentBuilder;Z[Ljava/lang/Object;)Lcom/oneplus/base/component/Component;
    //   38: pop
    //   39: aload_0
    //   40: getfield 118	com/oneplus/base/component/ComponentManager:m_Lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   43: invokevirtual 246	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   46: return
    //   47: aload_0
    //   48: getfield 106	com/oneplus/base/component/ComponentManager:m_Builders	Ljava/util/List;
    //   51: aload_1
    //   52: invokeinterface 171 2 0
    //   57: pop
    //   58: goto -19 -> 39
    //   61: astore_1
    //   62: aload_0
    //   63: getfield 118	com/oneplus/base/component/ComponentManager:m_Lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   66: invokevirtual 246	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   69: aload_1
    //   70: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	71	0	this	ComponentManager
    //   0	71	1	paramComponentBuilder	ComponentBuilder
    //   0	71	2	paramVarArgs	Object[]
    // Exception table:
    //   from	to	target	type
    //   7	39	61	finally
    //   47	58	61	finally
  }
  
  public void addComponentBuilders(ComponentBuilder[] paramArrayOfComponentBuilder, Object... paramVarArgs)
  {
    int i = paramArrayOfComponentBuilder.length - 1;
    while (i >= 0)
    {
      addComponentBuilder(paramArrayOfComponentBuilder[i], paramVarArgs);
      i -= 1;
    }
  }
  
  public final void createComponents(ComponentCreationPriority paramComponentCreationPriority, Object... paramVarArgs)
  {
    this.m_Lock.lock();
    ArrayList localArrayList;
    for (;;)
    {
      try
      {
        verifyAccess();
        verifyReleaseState();
        boolean bool = this.m_CreatePriorities.contains(paramComponentCreationPriority);
        if (bool) {
          return;
        }
        Log.w(this.TAG, "createComponents(" + paramComponentCreationPriority + ") - Start");
        switch (-getcom-oneplus-base-component-ComponentCreationPrioritySwitchesValues()[paramComponentCreationPriority.ordinal()])
        {
        case 2: 
          this.m_CreatePriorities.add(paramComponentCreationPriority);
          localArrayList = new ArrayList();
          i = this.m_Builders.size() - 1;
          label142:
          if (i < 0) {
            break label263;
          }
          Object localObject = (ComponentBuilder)this.m_Builders.get(i);
          if (((ComponentBuilder)localObject).getPriority() != paramComponentCreationPriority) {
            break label383;
          }
          localObject = createComponent((ComponentBuilder)localObject, false, paramVarArgs);
          if (localObject == null) {
            break label383;
          }
          localArrayList.add(localObject);
          this.m_Builders.remove(i);
        }
      }
      finally
      {
        this.m_Lock.unlock();
      }
      createComponents(ComponentCreationPriority.LAUNCH, paramVarArgs);
      continue;
      createComponents(ComponentCreationPriority.HIGH, paramVarArgs);
      continue;
      createComponents(ComponentCreationPriority.NORMAL, paramVarArgs);
    }
    throw new IllegalArgumentException("Cannot create on-demand components.");
    label263:
    int i = localArrayList.size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        paramVarArgs = (Component)localArrayList.get(i);
        if (!initializeComponent(paramVarArgs))
        {
          this.m_Components.remove(paramVarArgs);
          Log.w(this.TAG, "createComponents() - Release " + paramVarArgs);
          paramVarArgs.release();
        }
      }
      else
      {
        Log.w(this.TAG, "createComponents(" + paramComponentCreationPriority + ") - End");
        this.m_Lock.unlock();
        return;
        break;
        label383:
        i -= 1;
        break label142;
      }
      i -= 1;
    }
  }
  
  public final <TComponent extends Component> TComponent findComponent(Class<TComponent> paramClass, Object... paramVarArgs)
  {
    if (!this.m_Lock.tryLock())
    {
      Log.w(this.TAG, "findComponent() - Fail to lock component manager");
      return null;
    }
    try
    {
      int i = this.m_Components.size() - 1;
      Object localObject;
      while (i >= 0)
      {
        localObject = (Component)this.m_Components.get(i);
        if (paramClass.isAssignableFrom(localObject.getClass()))
        {
          bool = initializeComponent((Component)localObject);
          if (bool) {
            return (TComponent)localObject;
          }
        }
        i -= 1;
      }
      boolean bool = isDependencyThread();
      if (!bool) {
        return null;
      }
      i = this.m_Builders.size() - 1;
      while (i >= 0)
      {
        localObject = (ComponentBuilder)this.m_Builders.get(i);
        if ((((ComponentBuilder)localObject).getPriority() == ComponentCreationPriority.ON_DEMAND) && (((ComponentBuilder)localObject).isComponentTypeSupported(paramClass)))
        {
          Component localComponent = createComponent((ComponentBuilder)localObject, true, paramVarArgs);
          if (localComponent != null)
          {
            this.m_Builders.remove(localObject);
            return localComponent;
          }
        }
        i -= 1;
      }
      return null;
    }
    finally
    {
      this.m_Lock.unlock();
    }
  }
  
  /* Error */
  public final <TComponent extends Component> TComponent[] findComponents(Class<TComponent> paramClass, Object... paramVarArgs)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 118	com/oneplus/base/component/ComponentManager:m_Lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   4: invokevirtual 290	java/util/concurrent/locks/ReentrantLock:tryLock	()Z
    //   7: ifne +23 -> 30
    //   10: aload_0
    //   11: getfield 118	com/oneplus/base/component/ComponentManager:m_Lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   14: ldc2_w 317
    //   17: getstatic 324	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
    //   20: invokevirtual 327	java/util/concurrent/locks/ReentrantLock:tryLock	(JLjava/util/concurrent/TimeUnit;)Z
    //   23: istore 4
    //   25: iload 4
    //   27: ifeq +107 -> 134
    //   30: aload_0
    //   31: getfield 108	com/oneplus/base/component/ComponentManager:m_Components	Ljava/util/List;
    //   34: invokeinterface 262 1 0
    //   39: istore_3
    //   40: iload_3
    //   41: iconst_1
    //   42: isub
    //   43: istore_3
    //   44: aconst_null
    //   45: astore 5
    //   47: iload_3
    //   48: iflt +115 -> 163
    //   51: aload 5
    //   53: astore 6
    //   55: aload_0
    //   56: getfield 108	com/oneplus/base/component/ComponentManager:m_Components	Ljava/util/List;
    //   59: iload_3
    //   60: invokeinterface 265 2 0
    //   65: checkcast 161	com/oneplus/base/component/Component
    //   68: astore 7
    //   70: aload 5
    //   72: astore 6
    //   74: aload_1
    //   75: aload 7
    //   77: invokevirtual 298	java/lang/Object:getClass	()Ljava/lang/Class;
    //   80: invokevirtual 304	java/lang/Class:isAssignableFrom	(Ljava/lang/Class;)Z
    //   83: ifeq +77 -> 160
    //   86: aload 5
    //   88: astore 6
    //   90: aload_0
    //   91: aload 7
    //   93: invokespecial 281	com/oneplus/base/component/ComponentManager:initializeComponent	(Lcom/oneplus/base/component/Component;)Z
    //   96: ifeq +296 -> 392
    //   99: aload 5
    //   101: ifnonnull +288 -> 389
    //   104: aload 5
    //   106: astore 6
    //   108: new 102	java/util/ArrayList
    //   111: dup
    //   112: invokespecial 104	java/util/ArrayList:<init>	()V
    //   115: astore 5
    //   117: aload 5
    //   119: aload 7
    //   121: invokeinterface 171 2 0
    //   126: pop
    //   127: iload_3
    //   128: iconst_1
    //   129: isub
    //   130: istore_3
    //   131: goto -84 -> 47
    //   134: aload_0
    //   135: getfield 132	com/oneplus/base/component/ComponentManager:TAG	Ljava/lang/String;
    //   138: ldc_w 292
    //   141: invokestatic 154	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   144: aconst_null
    //   145: areturn
    //   146: astore_1
    //   147: aload_0
    //   148: getfield 132	com/oneplus/base/component/ComponentManager:TAG	Ljava/lang/String;
    //   151: ldc_w 292
    //   154: aload_1
    //   155: invokestatic 182	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   158: aconst_null
    //   159: areturn
    //   160: goto -33 -> 127
    //   163: aload 5
    //   165: astore 7
    //   167: aload 5
    //   169: astore 6
    //   171: aload_0
    //   172: invokevirtual 307	com/oneplus/base/component/ComponentManager:isDependencyThread	()Z
    //   175: ifeq +144 -> 319
    //   178: aload 5
    //   180: astore 6
    //   182: aload_0
    //   183: getfield 106	com/oneplus/base/component/ComponentManager:m_Builders	Ljava/util/List;
    //   186: invokeinterface 262 1 0
    //   191: iconst_1
    //   192: isub
    //   193: istore_3
    //   194: aload 5
    //   196: astore 7
    //   198: iload_3
    //   199: iflt +120 -> 319
    //   202: aload 5
    //   204: astore 6
    //   206: aload_0
    //   207: getfield 106	com/oneplus/base/component/ComponentManager:m_Builders	Ljava/util/List;
    //   210: iload_3
    //   211: invokeinterface 265 2 0
    //   216: checkcast 124	com/oneplus/base/component/ComponentBuilder
    //   219: astore 8
    //   221: aload 5
    //   223: astore 6
    //   225: aload 8
    //   227: invokeinterface 238 1 0
    //   232: getstatic 53	com/oneplus/base/component/ComponentCreationPriority:ON_DEMAND	Lcom/oneplus/base/component/ComponentCreationPriority;
    //   235: if_acmpne +167 -> 402
    //   238: aload 5
    //   240: astore 6
    //   242: aload 8
    //   244: aload_1
    //   245: invokeinterface 310 2 0
    //   250: ifeq +136 -> 386
    //   253: aload 5
    //   255: astore 6
    //   257: aload_0
    //   258: aload 8
    //   260: iconst_1
    //   261: aload_2
    //   262: invokespecial 243	com/oneplus/base/component/ComponentManager:createComponent	(Lcom/oneplus/base/component/ComponentBuilder;Z[Ljava/lang/Object;)Lcom/oneplus/base/component/Component;
    //   265: astore 7
    //   267: aload 7
    //   269: ifnull +117 -> 386
    //   272: aload 5
    //   274: astore 6
    //   276: aload_0
    //   277: getfield 106	com/oneplus/base/component/ComponentManager:m_Builders	Ljava/util/List;
    //   280: aload 8
    //   282: invokeinterface 221 2 0
    //   287: pop
    //   288: aload 5
    //   290: ifnonnull +93 -> 383
    //   293: aload 5
    //   295: astore 6
    //   297: new 102	java/util/ArrayList
    //   300: dup
    //   301: invokespecial 104	java/util/ArrayList:<init>	()V
    //   304: astore 5
    //   306: aload 5
    //   308: aload 7
    //   310: invokeinterface 171 2 0
    //   315: pop
    //   316: goto +79 -> 395
    //   319: aload 7
    //   321: ifnull +36 -> 357
    //   324: aload_1
    //   325: aload 7
    //   327: invokeinterface 262 1 0
    //   332: invokestatic 333	java/lang/reflect/Array:newInstance	(Ljava/lang/Class;I)Ljava/lang/Object;
    //   335: checkcast 335	[Lcom/oneplus/base/component/Component;
    //   338: astore_1
    //   339: aload 7
    //   341: aload_1
    //   342: invokeinterface 339 2 0
    //   347: pop
    //   348: aload_0
    //   349: getfield 118	com/oneplus/base/component/ComponentManager:m_Lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   352: invokevirtual 246	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   355: aload_1
    //   356: areturn
    //   357: aload_0
    //   358: getfield 118	com/oneplus/base/component/ComponentManager:m_Lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   361: invokevirtual 246	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   364: iconst_0
    //   365: anewarray 161	com/oneplus/base/component/Component
    //   368: areturn
    //   369: astore_1
    //   370: aload_0
    //   371: getfield 118	com/oneplus/base/component/ComponentManager:m_Lock	Ljava/util/concurrent/locks/ReentrantLock;
    //   374: invokevirtual 246	java/util/concurrent/locks/ReentrantLock:unlock	()V
    //   377: aload_1
    //   378: athrow
    //   379: astore_1
    //   380: goto -10 -> 370
    //   383: goto -77 -> 306
    //   386: goto +9 -> 395
    //   389: goto -272 -> 117
    //   392: goto -265 -> 127
    //   395: iload_3
    //   396: iconst_1
    //   397: isub
    //   398: istore_3
    //   399: goto -205 -> 194
    //   402: goto -7 -> 395
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	405	0	this	ComponentManager
    //   0	405	1	paramClass	Class<TComponent>
    //   0	405	2	paramVarArgs	Object[]
    //   39	360	3	i	int
    //   23	3	4	bool	boolean
    //   45	262	5	localArrayList1	ArrayList
    //   53	243	6	localArrayList2	ArrayList
    //   68	272	7	localObject	Object
    //   219	62	8	localComponentBuilder	ComponentBuilder
    // Exception table:
    //   from	to	target	type
    //   0	25	146	java/lang/InterruptedException
    //   134	144	146	java/lang/InterruptedException
    //   30	40	369	finally
    //   117	127	369	finally
    //   306	316	369	finally
    //   324	348	369	finally
    //   55	70	379	finally
    //   74	86	379	finally
    //   90	99	379	finally
    //   108	117	379	finally
    //   171	178	379	finally
    //   182	194	379	finally
    //   206	221	379	finally
    //   225	238	379	finally
    //   242	253	379	finally
    //   257	267	379	finally
    //   276	288	379	finally
    //   297	306	379	finally
  }
  
  protected void onRelease()
  {
    this.m_Lock.lock();
    try
    {
      this.m_Builders.clear();
      Component[] arrayOfComponent = new Component[this.m_Components.size()];
      this.m_Components.toArray(arrayOfComponent);
      int i = arrayOfComponent.length - 1;
      while (i >= 0)
      {
        removeComponentInternal(arrayOfComponent[i]);
        i -= 1;
      }
      this.m_Lock.unlock();
      super.onRelease();
      return;
    }
    finally
    {
      this.m_Lock.unlock();
    }
  }
  
  public final void removeComponent(Component paramComponent)
  {
    verifyAccess();
    this.m_Lock.lock();
    try
    {
      removeComponentInternal(paramComponent);
      return;
    }
    finally
    {
      this.m_Lock.unlock();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/component/ComponentManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */