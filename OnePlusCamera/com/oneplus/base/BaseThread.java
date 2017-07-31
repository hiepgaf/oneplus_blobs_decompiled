package com.oneplus.base;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public abstract class BaseThread
  extends Thread
  implements BaseObject, HandlerObject
{
  public static final EventKey<EventArgs> EVENT_STARTED = new EventKey("Started", EventArgs.class, BaseThread.class);
  public static final EventKey<EventArgs> EVENT_STOPPING = new EventKey("Stopping", EventArgs.class, BaseThread.class);
  public static final int LOG_EVENT_HANDLER = 1024;
  public static final int LOG_EVENT_HANDLER_CHANGE = 512;
  public static final int LOG_EVENT_RAISE = 256;
  public static final int LOG_PROPERTY_CALLBACK = 4;
  public static final int LOG_PROPERTY_CALLBACK_CHANGE = 2;
  public static final int LOG_PROPERTY_CHANGE = 1;
  private static final int MSG_QUIT = -1;
  public static final PropertyKey<Boolean> PROP_IS_STARTED = new PropertyKey("IsStarted", Boolean.class, BaseThread.class, Boolean.valueOf(false));
  protected final String TAG = getClass().getSimpleName();
  private volatile BaseObjectAdapter m_BaseObjectAdapter;
  private boolean m_EnableThreadMonitor;
  private volatile InternalHandler m_Handler;
  private volatile boolean m_IsReleased;
  private volatile boolean m_IsStartCalled;
  private Handle m_ThreadMonitorHandle;
  private final ThreadStartCallback m_ThreadStartCallback;
  private final Handler m_ThreadStartCallbackHandler;
  
  protected BaseThread(String paramString, ThreadStartCallback paramThreadStartCallback, Handler paramHandler)
  {
    this(paramString, paramThreadStartCallback, paramHandler, true);
  }
  
  protected BaseThread(String paramString, ThreadStartCallback paramThreadStartCallback, Handler paramHandler, boolean paramBoolean)
  {
    super(paramString);
    this.m_ThreadStartCallback = paramThreadStartCallback;
    this.m_ThreadStartCallbackHandler = paramHandler;
    this.m_EnableThreadMonitor = paramBoolean;
  }
  
  public <TValue> void addCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback)
  {
    verifyAccess();
    this.m_BaseObjectAdapter.addCallback(paramPropertyKey, paramPropertyChangedCallback);
  }
  
  public <TArgs extends EventArgs> void addHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler)
  {
    verifyAccess();
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
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_IS_RELEASED) {
      return Boolean.valueOf(this.m_IsReleased);
    }
    if (this.m_BaseObjectAdapter != null) {
      return (TValue)this.m_BaseObjectAdapter.get(paramPropertyKey);
    }
    return (TValue)paramPropertyKey.defaultValue;
  }
  
  public final Handler getHandler()
  {
    return this.m_Handler;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      return;
    }
    Log.w(this.TAG, "handleMessage() - Quit looper");
    onStopping();
    Looper.myLooper().quit();
  }
  
  public boolean isDependencyThread()
  {
    return Thread.currentThread() == this;
  }
  
  protected <TValue> boolean notifyPropertyChanged(PropertyKey<TValue> paramPropertyKey, TValue paramTValue1, TValue paramTValue2)
  {
    return this.m_BaseObjectAdapter.notifyPropertyChanged(paramPropertyKey, paramTValue1, paramTValue2);
  }
  
  protected void onStarted()
  {
    if ((this.m_ThreadStartCallback != null) && (this.m_ThreadStartCallbackHandler != null && (!this.m_ThreadStartCallbackHandler.postAtFrontOfQueue(new Runnable()
    {
      public void run()
      {
        BaseThread.-get0(BaseThread.this).onThreadStarted(BaseThread.this);
      }
    }))) {
      Log.e(this.TAG, "onStarted() - Fail to call-back");
    }
  }
  
  protected void onStarting() {}
  
  protected void onStopping()
  {
    raise(EVENT_STOPPING, EventArgs.EMPTY);
  }
  
  protected <TArgs extends EventArgs> void raise(EventKey<TArgs> paramEventKey, TArgs paramTArgs)
  {
    this.m_BaseObjectAdapter.raise(paramEventKey, paramTArgs);
  }
  
  public void release()
  {
    try
    {
      boolean bool = this.m_IsReleased;
      if (bool) {
        return;
      }
      Log.w(this.TAG, "release()");
      this.m_IsReleased = true;
      if (!this.m_IsStartCalled)
      {
        Log.w(this.TAG, "release() - Start thread to prevent thread leak");
        super.start();
        this.m_IsStartCalled = true;
      }
      HandlerUtils.sendMessage(this, -1);
      return;
    }
    finally {}
  }
  
  public <TValue> void removeCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback)
  {
    verifyAccess();
    this.m_BaseObjectAdapter.removeCallback(paramPropertyKey, paramPropertyChangedCallback);
  }
  
  public <TArgs extends EventArgs> void removeHandler(EventKey<TArgs> paramEventKey, EventHandler<TArgs> paramEventHandler)
  {
    verifyAccess();
    this.m_BaseObjectAdapter.removeHandler(paramEventKey, paramEventHandler);
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 111	com/oneplus/base/BaseThread:TAG	Ljava/lang/String;
    //   4: ldc_w 256
    //   7: invokestatic 182	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   10: aload_0
    //   11: monitorenter
    //   12: aload_0
    //   13: getfield 156	com/oneplus/base/BaseThread:m_IsReleased	Z
    //   16: ifeq +77 -> 93
    //   19: aload_0
    //   20: getfield 111	com/oneplus/base/BaseThread:TAG	Ljava/lang/String;
    //   23: ldc_w 258
    //   26: invokestatic 182	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   29: aload_0
    //   30: monitorexit
    //   31: aload_0
    //   32: getfield 167	com/oneplus/base/BaseThread:m_Handler	Lcom/oneplus/base/BaseThread$InternalHandler;
    //   35: ifnull +15 -> 50
    //   38: aload_0
    //   39: getfield 167	com/oneplus/base/BaseThread:m_Handler	Lcom/oneplus/base/BaseThread$InternalHandler;
    //   42: invokevirtual 260	com/oneplus/base/BaseThread$InternalHandler:release	()V
    //   45: aload_0
    //   46: aconst_null
    //   47: putfield 167	com/oneplus/base/BaseThread:m_Handler	Lcom/oneplus/base/BaseThread$InternalHandler;
    //   50: aload_0
    //   51: getfield 122	com/oneplus/base/BaseThread:m_BaseObjectAdapter	Lcom/oneplus/base/BaseObjectAdapter;
    //   54: ifnull +10 -> 64
    //   57: aload_0
    //   58: getfield 122	com/oneplus/base/BaseThread:m_BaseObjectAdapter	Lcom/oneplus/base/BaseObjectAdapter;
    //   61: invokevirtual 261	com/oneplus/base/BaseObjectAdapter:release	()V
    //   64: aload_0
    //   65: getfield 115	com/oneplus/base/BaseThread:m_EnableThreadMonitor	Z
    //   68: ifeq +14 -> 82
    //   71: aload_0
    //   72: aload_0
    //   73: getfield 263	com/oneplus/base/BaseThread:m_ThreadMonitorHandle	Lcom/oneplus/base/Handle;
    //   76: invokestatic 269	com/oneplus/base/Handle:close	(Lcom/oneplus/base/Handle;)Lcom/oneplus/base/Handle;
    //   79: putfield 263	com/oneplus/base/BaseThread:m_ThreadMonitorHandle	Lcom/oneplus/base/Handle;
    //   82: aload_0
    //   83: getfield 111	com/oneplus/base/BaseThread:TAG	Ljava/lang/String;
    //   86: ldc_w 271
    //   89: invokestatic 182	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   92: return
    //   93: aload_0
    //   94: monitorexit
    //   95: aload_0
    //   96: new 124	com/oneplus/base/BaseObjectAdapter
    //   99: dup
    //   100: aload_0
    //   101: aload_0
    //   102: getfield 111	com/oneplus/base/BaseThread:TAG	Ljava/lang/String;
    //   105: invokespecial 274	com/oneplus/base/BaseObjectAdapter:<init>	(Ljava/lang/Object;Ljava/lang/String;)V
    //   108: putfield 122	com/oneplus/base/BaseThread:m_BaseObjectAdapter	Lcom/oneplus/base/BaseObjectAdapter;
    //   111: invokestatic 277	android/os/Looper:prepare	()V
    //   114: aload_0
    //   115: new 12	com/oneplus/base/BaseThread$InternalHandler
    //   118: dup
    //   119: aload_0
    //   120: invokespecial 278	com/oneplus/base/BaseThread$InternalHandler:<init>	(Lcom/oneplus/base/BaseThread;)V
    //   123: putfield 167	com/oneplus/base/BaseThread:m_Handler	Lcom/oneplus/base/BaseThread$InternalHandler;
    //   126: aload_0
    //   127: getfield 115	com/oneplus/base/BaseThread:m_EnableThreadMonitor	Z
    //   130: ifeq +10 -> 140
    //   133: aload_0
    //   134: invokestatic 284	com/oneplus/base/ThreadMonitor:startMonitorCurrentThread	()Lcom/oneplus/base/Handle;
    //   137: putfield 263	com/oneplus/base/BaseThread:m_ThreadMonitorHandle	Lcom/oneplus/base/Handle;
    //   140: aload_0
    //   141: invokevirtual 286	com/oneplus/base/BaseThread:onStarting	()V
    //   144: aload_0
    //   145: invokevirtual 288	com/oneplus/base/BaseThread:onStarted	()V
    //   148: aload_0
    //   149: getfield 111	com/oneplus/base/BaseThread:TAG	Ljava/lang/String;
    //   152: ldc_w 290
    //   155: invokestatic 293	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   158: invokestatic 296	android/os/Looper:loop	()V
    //   161: aload_0
    //   162: getfield 167	com/oneplus/base/BaseThread:m_Handler	Lcom/oneplus/base/BaseThread$InternalHandler;
    //   165: ifnull +15 -> 180
    //   168: aload_0
    //   169: getfield 167	com/oneplus/base/BaseThread:m_Handler	Lcom/oneplus/base/BaseThread$InternalHandler;
    //   172: invokevirtual 260	com/oneplus/base/BaseThread$InternalHandler:release	()V
    //   175: aload_0
    //   176: aconst_null
    //   177: putfield 167	com/oneplus/base/BaseThread:m_Handler	Lcom/oneplus/base/BaseThread$InternalHandler;
    //   180: aload_0
    //   181: getfield 122	com/oneplus/base/BaseThread:m_BaseObjectAdapter	Lcom/oneplus/base/BaseObjectAdapter;
    //   184: ifnull +10 -> 194
    //   187: aload_0
    //   188: getfield 122	com/oneplus/base/BaseThread:m_BaseObjectAdapter	Lcom/oneplus/base/BaseObjectAdapter;
    //   191: invokevirtual 261	com/oneplus/base/BaseObjectAdapter:release	()V
    //   194: aload_0
    //   195: getfield 115	com/oneplus/base/BaseThread:m_EnableThreadMonitor	Z
    //   198: ifeq +14 -> 212
    //   201: aload_0
    //   202: aload_0
    //   203: getfield 263	com/oneplus/base/BaseThread:m_ThreadMonitorHandle	Lcom/oneplus/base/Handle;
    //   206: invokestatic 269	com/oneplus/base/Handle:close	(Lcom/oneplus/base/Handle;)Lcom/oneplus/base/Handle;
    //   209: putfield 263	com/oneplus/base/BaseThread:m_ThreadMonitorHandle	Lcom/oneplus/base/Handle;
    //   212: aload_0
    //   213: getfield 111	com/oneplus/base/BaseThread:TAG	Ljava/lang/String;
    //   216: ldc_w 271
    //   219: invokestatic 182	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   222: return
    //   223: astore_1
    //   224: aload_0
    //   225: monitorexit
    //   226: aload_1
    //   227: athrow
    //   228: astore_1
    //   229: aload_0
    //   230: getfield 167	com/oneplus/base/BaseThread:m_Handler	Lcom/oneplus/base/BaseThread$InternalHandler;
    //   233: ifnull +15 -> 248
    //   236: aload_0
    //   237: getfield 167	com/oneplus/base/BaseThread:m_Handler	Lcom/oneplus/base/BaseThread$InternalHandler;
    //   240: invokevirtual 260	com/oneplus/base/BaseThread$InternalHandler:release	()V
    //   243: aload_0
    //   244: aconst_null
    //   245: putfield 167	com/oneplus/base/BaseThread:m_Handler	Lcom/oneplus/base/BaseThread$InternalHandler;
    //   248: aload_0
    //   249: getfield 122	com/oneplus/base/BaseThread:m_BaseObjectAdapter	Lcom/oneplus/base/BaseObjectAdapter;
    //   252: ifnull +10 -> 262
    //   255: aload_0
    //   256: getfield 122	com/oneplus/base/BaseThread:m_BaseObjectAdapter	Lcom/oneplus/base/BaseObjectAdapter;
    //   259: invokevirtual 261	com/oneplus/base/BaseObjectAdapter:release	()V
    //   262: aload_0
    //   263: getfield 115	com/oneplus/base/BaseThread:m_EnableThreadMonitor	Z
    //   266: ifeq +14 -> 280
    //   269: aload_0
    //   270: aload_0
    //   271: getfield 263	com/oneplus/base/BaseThread:m_ThreadMonitorHandle	Lcom/oneplus/base/Handle;
    //   274: invokestatic 269	com/oneplus/base/Handle:close	(Lcom/oneplus/base/Handle;)Lcom/oneplus/base/Handle;
    //   277: putfield 263	com/oneplus/base/BaseThread:m_ThreadMonitorHandle	Lcom/oneplus/base/Handle;
    //   280: aload_0
    //   281: getfield 111	com/oneplus/base/BaseThread:TAG	Ljava/lang/String;
    //   284: ldc_w 271
    //   287: invokestatic 182	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   290: aload_1
    //   291: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	292	0	this	BaseThread
    //   223	4	1	localObject1	Object
    //   228	63	1	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   12	29	223	finally
    //   10	12	228	finally
    //   29	31	228	finally
    //   93	140	228	finally
    //   140	161	228	finally
    //   224	228	228	finally
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    verifyAccess();
    return this.m_BaseObjectAdapter.set(paramPropertyKey, paramTValue);
  }
  
  protected <TValue> boolean setReadOnly(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    return this.m_BaseObjectAdapter.setReadOnly(paramPropertyKey, paramTValue);
  }
  
  public void start()
  {
    try
    {
      if (this.m_IsReleased) {
        throw new RuntimeException("Thread has been released.");
      }
    }
    finally {}
    if (this.m_IsStartCalled) {
      throw new RuntimeException("Thread is already started.");
    }
    Log.w(this.TAG, "start()");
    this.m_IsStartCalled = true;
    super.start();
  }
  
  public final void verifyAccess()
  {
    if (Thread.currentThread() != this) {
      throw new RuntimeException("Cross-thread access.");
    }
  }
  
  private static final class InternalHandler
    extends Handler
  {
    private volatile BaseThread m_Owner;
    
    public InternalHandler(BaseThread paramBaseThread)
    {
      this.m_Owner = paramBaseThread;
    }
    
    public void handleMessage(Message paramMessage)
    {
      BaseThread localBaseThread = this.m_Owner;
      if (localBaseThread != null)
      {
        localBaseThread.handleMessage(paramMessage);
        return;
      }
      Log.e("BaseThread", "Owner released, drop message " + paramMessage.what);
    }
    
    public void release()
    {
      this.m_Owner = null;
    }
  }
  
  public static abstract interface ThreadStartCallback
  {
    public abstract void onThreadStarted(BaseThread paramBaseThread);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/BaseThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */