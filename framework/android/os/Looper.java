package android.os;

import android.util.Printer;

public final class Looper
{
  private static final String TAG = "Looper";
  private static Looper sMainLooper;
  static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal();
  private Printer mLogging;
  final MessageQueue mQueue;
  final Thread mThread;
  private long mTraceTag;
  
  private Looper(boolean paramBoolean)
  {
    this.mQueue = new MessageQueue(paramBoolean);
    this.mThread = Thread.currentThread();
  }
  
  public static Looper getMainLooper()
  {
    try
    {
      Looper localLooper = sMainLooper;
      return localLooper;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  public static void loop()
  {
    // Byte code:
    //   0: invokestatic 55	android/os/Looper:myLooper	()Landroid/os/Looper;
    //   3: astore 4
    //   5: aload 4
    //   7: ifnonnull +13 -> 20
    //   10: new 57	java/lang/RuntimeException
    //   13: dup
    //   14: ldc 59
    //   16: invokespecial 62	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   19: athrow
    //   20: aload 4
    //   22: getfield 39	android/os/Looper:mQueue	Landroid/os/MessageQueue;
    //   25: astore 5
    //   27: invokestatic 68	android/os/Binder:clearCallingIdentity	()J
    //   30: pop2
    //   31: invokestatic 68	android/os/Binder:clearCallingIdentity	()J
    //   34: lstore_0
    //   35: aload 5
    //   37: invokevirtual 72	android/os/MessageQueue:next	()Landroid/os/Message;
    //   40: astore 6
    //   42: aload 6
    //   44: ifnonnull +4 -> 48
    //   47: return
    //   48: aload 4
    //   50: getfield 74	android/os/Looper:mLogging	Landroid/util/Printer;
    //   53: astore 7
    //   55: aload 7
    //   57: ifnull +59 -> 116
    //   60: aload 7
    //   62: new 76	java/lang/StringBuilder
    //   65: dup
    //   66: invokespecial 77	java/lang/StringBuilder:<init>	()V
    //   69: ldc 79
    //   71: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: aload 6
    //   76: getfield 89	android/os/Message:target	Landroid/os/Handler;
    //   79: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   82: ldc 94
    //   84: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   87: aload 6
    //   89: getfield 98	android/os/Message:callback	Ljava/lang/Runnable;
    //   92: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   95: ldc 100
    //   97: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   100: aload 6
    //   102: getfield 104	android/os/Message:what	I
    //   105: invokevirtual 107	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   108: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   111: invokeinterface 116 2 0
    //   116: aload 4
    //   118: getfield 118	android/os/Looper:mTraceTag	J
    //   121: lstore_2
    //   122: lload_2
    //   123: lconst_0
    //   124: lcmp
    //   125: ifeq +24 -> 149
    //   128: lload_2
    //   129: invokestatic 124	android/os/Trace:isTagEnabled	(J)Z
    //   132: ifeq +17 -> 149
    //   135: lload_2
    //   136: aload 6
    //   138: getfield 89	android/os/Message:target	Landroid/os/Handler;
    //   141: aload 6
    //   143: invokevirtual 130	android/os/Handler:getTraceName	(Landroid/os/Message;)Ljava/lang/String;
    //   146: invokestatic 134	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   149: aload 6
    //   151: getfield 89	android/os/Message:target	Landroid/os/Handler;
    //   154: aload 6
    //   156: invokevirtual 138	android/os/Handler:dispatchMessage	(Landroid/os/Message;)V
    //   159: lload_2
    //   160: lconst_0
    //   161: lcmp
    //   162: ifeq +7 -> 169
    //   165: lload_2
    //   166: invokestatic 142	android/os/Trace:traceEnd	(J)V
    //   169: aload 7
    //   171: ifnull +46 -> 217
    //   174: aload 7
    //   176: new 76	java/lang/StringBuilder
    //   179: dup
    //   180: invokespecial 77	java/lang/StringBuilder:<init>	()V
    //   183: ldc -112
    //   185: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   188: aload 6
    //   190: getfield 89	android/os/Message:target	Landroid/os/Handler;
    //   193: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   196: ldc 94
    //   198: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   201: aload 6
    //   203: getfield 98	android/os/Message:callback	Ljava/lang/Runnable;
    //   206: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   209: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   212: invokeinterface 116 2 0
    //   217: invokestatic 68	android/os/Binder:clearCallingIdentity	()J
    //   220: lstore_2
    //   221: lload_0
    //   222: lload_2
    //   223: lcmp
    //   224: ifeq +88 -> 312
    //   227: ldc 8
    //   229: new 76	java/lang/StringBuilder
    //   232: dup
    //   233: invokespecial 77	java/lang/StringBuilder:<init>	()V
    //   236: ldc -110
    //   238: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   241: lload_0
    //   242: invokestatic 152	java/lang/Long:toHexString	(J)Ljava/lang/String;
    //   245: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   248: ldc -102
    //   250: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   253: lload_2
    //   254: invokestatic 152	java/lang/Long:toHexString	(J)Ljava/lang/String;
    //   257: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   260: ldc -100
    //   262: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   265: aload 6
    //   267: getfield 89	android/os/Message:target	Landroid/os/Handler;
    //   270: invokevirtual 160	android/os/Handler:getClass	()Ljava/lang/Class;
    //   273: invokevirtual 165	java/lang/Class:getName	()Ljava/lang/String;
    //   276: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   279: ldc 94
    //   281: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   284: aload 6
    //   286: getfield 98	android/os/Message:callback	Ljava/lang/Runnable;
    //   289: invokevirtual 92	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   292: ldc -89
    //   294: invokevirtual 83	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   297: aload 6
    //   299: getfield 104	android/os/Message:what	I
    //   302: invokevirtual 107	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   305: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   308: invokestatic 173	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;)I
    //   311: pop
    //   312: aload 6
    //   314: invokevirtual 176	android/os/Message:recycleUnchecked	()V
    //   317: goto -282 -> 35
    //   320: astore 4
    //   322: lload_2
    //   323: lconst_0
    //   324: lcmp
    //   325: ifeq +7 -> 332
    //   328: lload_2
    //   329: invokestatic 142	android/os/Trace:traceEnd	(J)V
    //   332: aload 4
    //   334: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   34	208	0	l1	long
    //   121	208	2	l2	long
    //   3	114	4	localLooper	Looper
    //   320	13	4	localObject	Object
    //   25	11	5	localMessageQueue	MessageQueue
    //   40	273	6	localMessage	Message
    //   53	122	7	localPrinter	Printer
    // Exception table:
    //   from	to	target	type
    //   149	159	320	finally
  }
  
  public static Looper myLooper()
  {
    return (Looper)sThreadLocal.get();
  }
  
  public static MessageQueue myQueue()
  {
    return myLooper().mQueue;
  }
  
  public static void prepare()
  {
    prepare(true);
  }
  
  private static void prepare(boolean paramBoolean)
  {
    if (sThreadLocal.get() != null) {
      throw new RuntimeException("Only one Looper may be created per thread");
    }
    sThreadLocal.set(new Looper(paramBoolean));
  }
  
  public static void prepareMainLooper()
  {
    prepare(false);
    try
    {
      if (sMainLooper != null) {
        throw new IllegalStateException("The main Looper has already been prepared.");
      }
    }
    finally {}
    sMainLooper = myLooper();
  }
  
  public void dump(Printer paramPrinter, String paramString)
  {
    paramPrinter.println(paramString + toString());
    this.mQueue.dump(paramPrinter, paramString + "  ");
  }
  
  public MessageQueue getQueue()
  {
    return this.mQueue;
  }
  
  public Thread getThread()
  {
    return this.mThread;
  }
  
  public boolean isCurrentThread()
  {
    return Thread.currentThread() == this.mThread;
  }
  
  public void quit()
  {
    this.mQueue.quit(false);
  }
  
  public void quitSafely()
  {
    this.mQueue.quit(true);
  }
  
  public void setMessageLogging(Printer paramPrinter)
  {
    this.mLogging = paramPrinter;
  }
  
  public void setTraceTag(long paramLong)
  {
    this.mTraceTag = paramLong;
  }
  
  public String toString()
  {
    return "Looper (" + this.mThread.getName() + ", tid " + this.mThread.getId() + ") {" + Integer.toHexString(System.identityHashCode(this)) + "}";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Looper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */