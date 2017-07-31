package android.os;

import android.util.Log;
import android.util.Printer;

public class Handler
{
  private static final boolean FIND_POTENTIAL_LEAKS = false;
  private static final String TAG = "Handler";
  final boolean mAsynchronous;
  final Callback mCallback;
  final Looper mLooper;
  IMessenger mMessenger;
  final MessageQueue mQueue;
  
  public Handler()
  {
    this(null, false);
  }
  
  public Handler(Callback paramCallback)
  {
    this(paramCallback, false);
  }
  
  public Handler(Callback paramCallback, boolean paramBoolean)
  {
    this.mLooper = Looper.myLooper();
    if (this.mLooper == null) {
      throw new RuntimeException("Can't create handler inside thread that has not called Looper.prepare()");
    }
    this.mQueue = this.mLooper.mQueue;
    this.mCallback = paramCallback;
    this.mAsynchronous = paramBoolean;
  }
  
  public Handler(Looper paramLooper)
  {
    this(paramLooper, null, false);
  }
  
  public Handler(Looper paramLooper, Callback paramCallback)
  {
    this(paramLooper, paramCallback, false);
  }
  
  public Handler(Looper paramLooper, Callback paramCallback, boolean paramBoolean)
  {
    this.mLooper = paramLooper;
    this.mQueue = paramLooper.mQueue;
    this.mCallback = paramCallback;
    this.mAsynchronous = paramBoolean;
  }
  
  public Handler(boolean paramBoolean)
  {
    this(null, paramBoolean);
  }
  
  private boolean enqueueMessage(MessageQueue paramMessageQueue, Message paramMessage, long paramLong)
  {
    paramMessage.target = this;
    if (this.mAsynchronous) {
      paramMessage.setAsynchronous(true);
    }
    return paramMessageQueue.enqueueMessage(paramMessage, paramLong);
  }
  
  private static Message getPostMessage(Runnable paramRunnable)
  {
    Message localMessage = Message.obtain();
    localMessage.callback = paramRunnable;
    return localMessage;
  }
  
  private static Message getPostMessage(Runnable paramRunnable, Object paramObject)
  {
    Message localMessage = Message.obtain();
    localMessage.obj = paramObject;
    localMessage.callback = paramRunnable;
    return localMessage;
  }
  
  private static void handleCallback(Message paramMessage)
  {
    paramMessage.callback.run();
  }
  
  public void dispatchMessage(Message paramMessage)
  {
    if (paramMessage.callback != null)
    {
      handleCallback(paramMessage);
      return;
    }
    if ((this.mCallback != null) && (this.mCallback.handleMessage(paramMessage))) {
      return;
    }
    handleMessage(paramMessage);
  }
  
  public final void dump(Printer paramPrinter, String paramString)
  {
    paramPrinter.println(paramString + this + " @ " + SystemClock.uptimeMillis());
    if (this.mLooper == null)
    {
      paramPrinter.println(paramString + "looper uninitialized");
      return;
    }
    this.mLooper.dump(paramPrinter, paramString + "  ");
  }
  
  final IMessenger getIMessenger()
  {
    synchronized (this.mQueue)
    {
      if (this.mMessenger != null)
      {
        localIMessenger = this.mMessenger;
        return localIMessenger;
      }
      this.mMessenger = new MessengerImpl(null);
      IMessenger localIMessenger = this.mMessenger;
      return localIMessenger;
    }
  }
  
  public final Looper getLooper()
  {
    return this.mLooper;
  }
  
  public String getMessageName(Message paramMessage)
  {
    if (paramMessage.callback != null) {
      return paramMessage.callback.getClass().getName();
    }
    return "0x" + Integer.toHexString(paramMessage.what);
  }
  
  public String getTraceName(Message paramMessage)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(getClass().getName()).append(": ");
    if (paramMessage.callback != null) {
      localStringBuilder.append(paramMessage.callback.getClass().getName());
    }
    for (;;)
    {
      return localStringBuilder.toString();
      localStringBuilder.append("#").append(paramMessage.what);
    }
  }
  
  public void handleMessage(Message paramMessage) {}
  
  public final boolean hasCallbacks(Runnable paramRunnable)
  {
    return this.mQueue.hasMessages(this, paramRunnable, null);
  }
  
  public final boolean hasMessages(int paramInt)
  {
    return this.mQueue.hasMessages(this, paramInt, null);
  }
  
  public final boolean hasMessages(int paramInt, Object paramObject)
  {
    return this.mQueue.hasMessages(this, paramInt, paramObject);
  }
  
  public final Message obtainMessage()
  {
    return Message.obtain(this);
  }
  
  public final Message obtainMessage(int paramInt)
  {
    return Message.obtain(this, paramInt);
  }
  
  public final Message obtainMessage(int paramInt1, int paramInt2, int paramInt3)
  {
    return Message.obtain(this, paramInt1, paramInt2, paramInt3);
  }
  
  public final Message obtainMessage(int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    return Message.obtain(this, paramInt1, paramInt2, paramInt3, paramObject);
  }
  
  public final Message obtainMessage(int paramInt, Object paramObject)
  {
    return Message.obtain(this, paramInt, paramObject);
  }
  
  public final boolean post(Runnable paramRunnable)
  {
    return sendMessageDelayed(getPostMessage(paramRunnable), 0L);
  }
  
  public final boolean postAtFrontOfQueue(Runnable paramRunnable)
  {
    return sendMessageAtFrontOfQueue(getPostMessage(paramRunnable));
  }
  
  public final boolean postAtTime(Runnable paramRunnable, long paramLong)
  {
    return sendMessageAtTime(getPostMessage(paramRunnable), paramLong);
  }
  
  public final boolean postAtTime(Runnable paramRunnable, Object paramObject, long paramLong)
  {
    return sendMessageAtTime(getPostMessage(paramRunnable, paramObject), paramLong);
  }
  
  public final boolean postDelayed(Runnable paramRunnable, long paramLong)
  {
    return sendMessageDelayed(getPostMessage(paramRunnable), paramLong);
  }
  
  public final void removeCallbacks(Runnable paramRunnable)
  {
    this.mQueue.removeMessages(this, paramRunnable, null);
  }
  
  public final void removeCallbacks(Runnable paramRunnable, Object paramObject)
  {
    this.mQueue.removeMessages(this, paramRunnable, paramObject);
  }
  
  public final void removeCallbacksAndMessages(Object paramObject)
  {
    this.mQueue.removeCallbacksAndMessages(this, paramObject);
  }
  
  public final void removeMessages(int paramInt)
  {
    this.mQueue.removeMessages(this, paramInt, null);
  }
  
  public final void removeMessages(int paramInt, Object paramObject)
  {
    this.mQueue.removeMessages(this, paramInt, paramObject);
  }
  
  public final boolean runWithScissors(Runnable paramRunnable, long paramLong)
  {
    if (paramRunnable == null) {
      throw new IllegalArgumentException("runnable must not be null");
    }
    if (paramLong < 0L) {
      throw new IllegalArgumentException("timeout must be non-negative");
    }
    if (Looper.myLooper() == this.mLooper)
    {
      paramRunnable.run();
      return true;
    }
    return new BlockingRunnable(paramRunnable).postAndWait(this, paramLong);
  }
  
  public final boolean sendEmptyMessage(int paramInt)
  {
    return sendEmptyMessageDelayed(paramInt, 0L);
  }
  
  public final boolean sendEmptyMessageAtTime(int paramInt, long paramLong)
  {
    Message localMessage = Message.obtain();
    localMessage.what = paramInt;
    return sendMessageAtTime(localMessage, paramLong);
  }
  
  public final boolean sendEmptyMessageDelayed(int paramInt, long paramLong)
  {
    Message localMessage = Message.obtain();
    localMessage.what = paramInt;
    return sendMessageDelayed(localMessage, paramLong);
  }
  
  public final boolean sendMessage(Message paramMessage)
  {
    return sendMessageDelayed(paramMessage, 0L);
  }
  
  public final boolean sendMessageAtFrontOfQueue(Message paramMessage)
  {
    MessageQueue localMessageQueue = this.mQueue;
    if (localMessageQueue == null)
    {
      paramMessage = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
      Log.w("Looper", paramMessage.getMessage(), paramMessage);
      return false;
    }
    return enqueueMessage(localMessageQueue, paramMessage, 0L);
  }
  
  public boolean sendMessageAtTime(Message paramMessage, long paramLong)
  {
    MessageQueue localMessageQueue = this.mQueue;
    if (localMessageQueue == null)
    {
      paramMessage = new RuntimeException(this + " sendMessageAtTime() called with no mQueue");
      Log.w("Looper", paramMessage.getMessage(), paramMessage);
      return false;
    }
    return enqueueMessage(localMessageQueue, paramMessage, paramLong);
  }
  
  public final boolean sendMessageDelayed(Message paramMessage, long paramLong)
  {
    long l = paramLong;
    if (paramLong < 0L) {
      l = 0L;
    }
    return sendMessageAtTime(paramMessage, SystemClock.uptimeMillis() + l);
  }
  
  public String toString()
  {
    return "Handler (" + getClass().getName() + ") {" + Integer.toHexString(System.identityHashCode(this)) + "}";
  }
  
  private static final class BlockingRunnable
    implements Runnable
  {
    private boolean mDone;
    private final Runnable mTask;
    
    public BlockingRunnable(Runnable paramRunnable)
    {
      this.mTask = paramRunnable;
    }
    
    public boolean postAndWait(Handler paramHandler, long paramLong)
    {
      if (!paramHandler.post(this)) {
        return false;
      }
      if (paramLong > 0L) {}
      try
      {
        long l1 = SystemClock.uptimeMillis();
        while (!this.mDone)
        {
          long l2 = SystemClock.uptimeMillis();
          l2 = l1 + paramLong - l2;
          if (l2 <= 0L) {
            return false;
          }
          try
          {
            wait(l2);
          }
          catch (InterruptedException paramHandler) {}
          continue;
          for (;;)
          {
            boolean bool = this.mDone;
            if (bool) {
              break;
            }
            try
            {
              wait();
            }
            catch (InterruptedException paramHandler) {}
          }
        }
        return true;
      }
      finally {}
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 20	android/os/Handler$BlockingRunnable:mTask	Ljava/lang/Runnable;
      //   4: invokeinterface 46 1 0
      //   9: aload_0
      //   10: monitorenter
      //   11: aload_0
      //   12: iconst_1
      //   13: putfield 37	android/os/Handler$BlockingRunnable:mDone	Z
      //   16: aload_0
      //   17: invokevirtual 49	android/os/Handler$BlockingRunnable:notifyAll	()V
      //   20: aload_0
      //   21: monitorexit
      //   22: return
      //   23: astore_1
      //   24: aload_0
      //   25: monitorexit
      //   26: aload_1
      //   27: athrow
      //   28: astore_1
      //   29: aload_0
      //   30: monitorenter
      //   31: aload_0
      //   32: iconst_1
      //   33: putfield 37	android/os/Handler$BlockingRunnable:mDone	Z
      //   36: aload_0
      //   37: invokevirtual 49	android/os/Handler$BlockingRunnable:notifyAll	()V
      //   40: aload_0
      //   41: monitorexit
      //   42: aload_1
      //   43: athrow
      //   44: astore_1
      //   45: aload_0
      //   46: monitorexit
      //   47: aload_1
      //   48: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	49	0	this	BlockingRunnable
      //   23	4	1	localObject1	Object
      //   28	15	1	localObject2	Object
      //   44	4	1	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   11	20	23	finally
      //   0	9	28	finally
      //   31	40	44	finally
    }
  }
  
  public static abstract interface Callback
  {
    public abstract boolean handleMessage(Message paramMessage);
  }
  
  private final class MessengerImpl
    extends IMessenger.Stub
  {
    private MessengerImpl() {}
    
    public void send(Message paramMessage)
    {
      paramMessage.sendingUid = Binder.getCallingUid();
      Handler.this.sendMessage(paramMessage);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Handler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */