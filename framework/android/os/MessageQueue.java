package android.os;

import android.util.Log;
import android.util.Printer;
import android.util.SparseArray;
import java.io.FileDescriptor;
import java.util.ArrayList;

public final class MessageQueue
{
  private static final boolean DEBUG = false;
  private static final String TAG = "MessageQueue";
  private boolean mBlocked;
  private SparseArray<FileDescriptorRecord> mFileDescriptorRecords;
  private final ArrayList<IdleHandler> mIdleHandlers = new ArrayList();
  Message mMessages;
  private int mNextBarrierToken;
  private IdleHandler[] mPendingIdleHandlers;
  private long mPtr;
  private final boolean mQuitAllowed;
  private boolean mQuitting;
  
  MessageQueue(boolean paramBoolean)
  {
    this.mQuitAllowed = paramBoolean;
    this.mPtr = nativeInit();
  }
  
  /* Error */
  private int dispatchEvents(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 60	android/os/MessageQueue:mFileDescriptorRecords	Landroid/util/SparseArray;
    //   6: iload_1
    //   7: invokevirtual 66	android/util/SparseArray:get	(I)Ljava/lang/Object;
    //   10: checkcast 6	android/os/MessageQueue$FileDescriptorRecord
    //   13: astore 6
    //   15: aload 6
    //   17: ifnonnull +7 -> 24
    //   20: aload_0
    //   21: monitorexit
    //   22: iconst_0
    //   23: ireturn
    //   24: aload 6
    //   26: getfield 69	android/os/MessageQueue$FileDescriptorRecord:mEvents	I
    //   29: istore 4
    //   31: iload_2
    //   32: iload 4
    //   34: iand
    //   35: istore_2
    //   36: iload_2
    //   37: ifne +8 -> 45
    //   40: aload_0
    //   41: monitorexit
    //   42: iload 4
    //   44: ireturn
    //   45: aload 6
    //   47: getfield 73	android/os/MessageQueue$FileDescriptorRecord:mListener	Landroid/os/MessageQueue$OnFileDescriptorEventListener;
    //   50: astore 7
    //   52: aload 6
    //   54: getfield 76	android/os/MessageQueue$FileDescriptorRecord:mSeq	I
    //   57: istore 5
    //   59: aload_0
    //   60: monitorexit
    //   61: aload 7
    //   63: aload 6
    //   65: getfield 80	android/os/MessageQueue$FileDescriptorRecord:mDescriptor	Ljava/io/FileDescriptor;
    //   68: iload_2
    //   69: invokeinterface 84 3 0
    //   74: istore_3
    //   75: iload_3
    //   76: istore_2
    //   77: iload_3
    //   78: ifeq +7 -> 85
    //   81: iload_3
    //   82: iconst_4
    //   83: ior
    //   84: istore_2
    //   85: iload_2
    //   86: iload 4
    //   88: if_icmpeq +61 -> 149
    //   91: aload_0
    //   92: monitorenter
    //   93: aload_0
    //   94: getfield 60	android/os/MessageQueue:mFileDescriptorRecords	Landroid/util/SparseArray;
    //   97: iload_1
    //   98: invokevirtual 88	android/util/SparseArray:indexOfKey	(I)I
    //   101: istore_1
    //   102: iload_1
    //   103: iflt +44 -> 147
    //   106: aload_0
    //   107: getfield 60	android/os/MessageQueue:mFileDescriptorRecords	Landroid/util/SparseArray;
    //   110: iload_1
    //   111: invokevirtual 91	android/util/SparseArray:valueAt	(I)Ljava/lang/Object;
    //   114: aload 6
    //   116: if_acmpne +31 -> 147
    //   119: aload 6
    //   121: getfield 76	android/os/MessageQueue$FileDescriptorRecord:mSeq	I
    //   124: iload 5
    //   126: if_icmpne +21 -> 147
    //   129: aload 6
    //   131: iload_2
    //   132: putfield 69	android/os/MessageQueue$FileDescriptorRecord:mEvents	I
    //   135: iload_2
    //   136: ifne +11 -> 147
    //   139: aload_0
    //   140: getfield 60	android/os/MessageQueue:mFileDescriptorRecords	Landroid/util/SparseArray;
    //   143: iload_1
    //   144: invokevirtual 95	android/util/SparseArray:removeAt	(I)V
    //   147: aload_0
    //   148: monitorexit
    //   149: iload_2
    //   150: ireturn
    //   151: astore 6
    //   153: aload_0
    //   154: monitorexit
    //   155: aload 6
    //   157: athrow
    //   158: astore 6
    //   160: aload_0
    //   161: monitorexit
    //   162: aload 6
    //   164: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	165	0	this	MessageQueue
    //   0	165	1	paramInt1	int
    //   0	165	2	paramInt2	int
    //   74	10	3	i	int
    //   29	60	4	j	int
    //   57	70	5	k	int
    //   13	117	6	localFileDescriptorRecord	FileDescriptorRecord
    //   151	5	6	localObject1	Object
    //   158	5	6	localObject2	Object
    //   50	12	7	localOnFileDescriptorEventListener	OnFileDescriptorEventListener
    // Exception table:
    //   from	to	target	type
    //   2	15	151	finally
    //   24	31	151	finally
    //   45	59	151	finally
    //   93	102	158	finally
    //   106	135	158	finally
    //   139	147	158	finally
  }
  
  private void dispose()
  {
    if (this.mPtr != 0L)
    {
      nativeDestroy(this.mPtr);
      this.mPtr = 0L;
    }
  }
  
  private boolean isPollingLocked()
  {
    if (!this.mQuitting) {
      return nativeIsPolling(this.mPtr);
    }
    return false;
  }
  
  private static native void nativeDestroy(long paramLong);
  
  private static native long nativeInit();
  
  private static native boolean nativeIsPolling(long paramLong);
  
  private native void nativePollOnce(long paramLong, int paramInt);
  
  private static native void nativeSetFileDescriptorEvents(long paramLong, int paramInt1, int paramInt2);
  
  private static native void nativeWake(long paramLong);
  
  /* Error */
  private int postSyncBarrier(long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 117	android/os/MessageQueue:mNextBarrierToken	I
    //   6: istore_3
    //   7: aload_0
    //   8: iload_3
    //   9: iconst_1
    //   10: iadd
    //   11: putfield 117	android/os/MessageQueue:mNextBarrierToken	I
    //   14: invokestatic 123	android/os/Message:obtain	()Landroid/os/Message;
    //   17: astore 8
    //   19: aload 8
    //   21: invokevirtual 126	android/os/Message:markInUse	()V
    //   24: aload 8
    //   26: lload_1
    //   27: putfield 129	android/os/Message:when	J
    //   30: aload 8
    //   32: iload_3
    //   33: putfield 132	android/os/Message:arg1	I
    //   36: aconst_null
    //   37: astore 7
    //   39: aconst_null
    //   40: astore 5
    //   42: aload_0
    //   43: getfield 134	android/os/MessageQueue:mMessages	Landroid/os/Message;
    //   46: astore 4
    //   48: aload 4
    //   50: astore 6
    //   52: lload_1
    //   53: lconst_0
    //   54: lcmp
    //   55: ifeq +48 -> 103
    //   58: aload 4
    //   60: astore 6
    //   62: aload 5
    //   64: astore 7
    //   66: aload 4
    //   68: ifnull +35 -> 103
    //   71: aload 4
    //   73: astore 6
    //   75: aload 5
    //   77: astore 7
    //   79: aload 4
    //   81: getfield 129	android/os/Message:when	J
    //   84: lload_1
    //   85: lcmp
    //   86: ifgt +17 -> 103
    //   89: aload 4
    //   91: astore 5
    //   93: aload 4
    //   95: getfield 137	android/os/Message:next	Landroid/os/Message;
    //   98: astore 4
    //   100: goto -42 -> 58
    //   103: aload 7
    //   105: ifnull +21 -> 126
    //   108: aload 8
    //   110: aload 6
    //   112: putfield 137	android/os/Message:next	Landroid/os/Message;
    //   115: aload 7
    //   117: aload 8
    //   119: putfield 137	android/os/Message:next	Landroid/os/Message;
    //   122: aload_0
    //   123: monitorexit
    //   124: iload_3
    //   125: ireturn
    //   126: aload 8
    //   128: aload 6
    //   130: putfield 137	android/os/Message:next	Landroid/os/Message;
    //   133: aload_0
    //   134: aload 8
    //   136: putfield 134	android/os/MessageQueue:mMessages	Landroid/os/Message;
    //   139: goto -17 -> 122
    //   142: astore 4
    //   144: aload_0
    //   145: monitorexit
    //   146: aload 4
    //   148: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	149	0	this	MessageQueue
    //   0	149	1	paramLong	long
    //   6	119	3	i	int
    //   46	53	4	localMessage1	Message
    //   142	5	4	localObject1	Object
    //   40	52	5	localObject2	Object
    //   50	79	6	localMessage2	Message
    //   37	79	7	localObject3	Object
    //   17	118	8	localMessage3	Message
    // Exception table:
    //   from	to	target	type
    //   2	36	142	finally
    //   42	48	142	finally
    //   79	89	142	finally
    //   93	100	142	finally
    //   108	122	142	finally
    //   126	139	142	finally
  }
  
  private void removeAllFutureMessagesLocked()
  {
    long l = SystemClock.uptimeMillis();
    Message localMessage2 = this.mMessages;
    if (localMessage2 != null)
    {
      localMessage1 = localMessage2;
      if (localMessage2.when > l) {
        removeAllMessagesLocked();
      }
    }
    else
    {
      return;
    }
    do
    {
      localMessage1 = localMessage2;
      localMessage2 = localMessage1.next;
      if (localMessage2 == null) {
        return;
      }
    } while (localMessage2.when <= l);
    localMessage1.next = null;
    for (Message localMessage1 = localMessage2;; localMessage1 = localMessage2)
    {
      localMessage2 = localMessage1.next;
      localMessage1.recycleUnchecked();
      if (localMessage2 == null) {
        break;
      }
    }
  }
  
  private void removeAllMessagesLocked()
  {
    Message localMessage;
    for (Object localObject = this.mMessages; localObject != null; localObject = localMessage)
    {
      localMessage = ((Message)localObject).next;
      ((Message)localObject).recycleUnchecked();
    }
    this.mMessages = null;
  }
  
  private void updateOnFileDescriptorEventListenerLocked(FileDescriptor paramFileDescriptor, int paramInt, OnFileDescriptorEventListener paramOnFileDescriptorEventListener)
  {
    int k = paramFileDescriptor.getInt$();
    int i = -1;
    FileDescriptorRecord localFileDescriptorRecord2 = null;
    FileDescriptorRecord localFileDescriptorRecord1 = localFileDescriptorRecord2;
    if (this.mFileDescriptorRecords != null)
    {
      int j = this.mFileDescriptorRecords.indexOfKey(k);
      i = j;
      localFileDescriptorRecord1 = localFileDescriptorRecord2;
      if (j >= 0)
      {
        localFileDescriptorRecord2 = (FileDescriptorRecord)this.mFileDescriptorRecords.valueAt(j);
        i = j;
        localFileDescriptorRecord1 = localFileDescriptorRecord2;
        if (localFileDescriptorRecord2 != null)
        {
          i = j;
          localFileDescriptorRecord1 = localFileDescriptorRecord2;
          if (localFileDescriptorRecord2.mEvents == paramInt) {
            return;
          }
        }
      }
    }
    if (paramInt != 0)
    {
      paramInt |= 0x4;
      if (localFileDescriptorRecord1 == null)
      {
        if (this.mFileDescriptorRecords == null) {
          this.mFileDescriptorRecords = new SparseArray();
        }
        paramFileDescriptor = new FileDescriptorRecord(paramFileDescriptor, paramInt, paramOnFileDescriptorEventListener);
        this.mFileDescriptorRecords.put(k, paramFileDescriptor);
        nativeSetFileDescriptorEvents(this.mPtr, k, paramInt);
      }
    }
    while (localFileDescriptorRecord1 == null) {
      for (;;)
      {
        return;
        localFileDescriptorRecord1.mListener = paramOnFileDescriptorEventListener;
        localFileDescriptorRecord1.mEvents = paramInt;
        localFileDescriptorRecord1.mSeq += 1;
      }
    }
    localFileDescriptorRecord1.mEvents = 0;
    this.mFileDescriptorRecords.removeAt(i);
  }
  
  public void addIdleHandler(IdleHandler paramIdleHandler)
  {
    if (paramIdleHandler == null) {
      throw new NullPointerException("Can't add a null IdleHandler");
    }
    try
    {
      this.mIdleHandlers.add(paramIdleHandler);
      return;
    }
    finally
    {
      paramIdleHandler = finally;
      throw paramIdleHandler;
    }
  }
  
  public void addOnFileDescriptorEventListener(FileDescriptor paramFileDescriptor, int paramInt, OnFileDescriptorEventListener paramOnFileDescriptorEventListener)
  {
    if (paramFileDescriptor == null) {
      throw new IllegalArgumentException("fd must not be null");
    }
    if (paramOnFileDescriptorEventListener == null) {
      throw new IllegalArgumentException("listener must not be null");
    }
    try
    {
      updateOnFileDescriptorEventListenerLocked(paramFileDescriptor, paramInt, paramOnFileDescriptorEventListener);
      return;
    }
    finally
    {
      paramFileDescriptor = finally;
      throw paramFileDescriptor;
    }
  }
  
  void dump(Printer paramPrinter, String paramString)
  {
    try
    {
      long l = SystemClock.uptimeMillis();
      int i = 0;
      for (Message localMessage = this.mMessages; localMessage != null; localMessage = localMessage.next)
      {
        paramPrinter.println(paramString + "Message " + i + ": " + localMessage.toString(l));
        i += 1;
      }
      paramPrinter.println(paramString + "(Total messages: " + i + ", polling=" + isPollingLocked() + ", quitting=" + this.mQuitting + ")");
      return;
    }
    finally {}
  }
  
  boolean enqueueMessage(Message paramMessage, long paramLong)
  {
    if (paramMessage.target == null) {
      throw new IllegalArgumentException("Message must have a target.");
    }
    if (paramMessage.isInUse()) {
      throw new IllegalStateException(paramMessage + " This message is already in use.");
    }
    for (;;)
    {
      boolean bool1;
      Message localMessage;
      try
      {
        if (this.mQuitting)
        {
          localObject = new IllegalStateException(paramMessage.target + " sending message to a Handler on a dead thread");
          Log.w("MessageQueue", ((IllegalStateException)localObject).getMessage(), (Throwable)localObject);
          paramMessage.recycle();
          return false;
        }
        paramMessage.markInUse();
        paramMessage.when = paramLong;
        localObject = this.mMessages;
        if ((localObject == null) || (paramLong == 0L))
        {
          paramMessage.next = ((Message)localObject);
          this.mMessages = paramMessage;
          bool1 = this.mBlocked;
          if (bool1) {
            nativeWake(this.mPtr);
          }
          return true;
        }
        if (paramLong < ((Message)localObject).when) {
          continue;
        }
        if ((this.mBlocked) && (((Message)localObject).target == null))
        {
          bool1 = paramMessage.isAsynchronous();
          localMessage = ((Message)localObject).next;
          if ((localMessage != null) && (paramLong >= localMessage.when)) {
            break label250;
          }
          paramMessage.next = localMessage;
          ((Message)localObject).next = paramMessage;
          continue;
        }
        bool1 = false;
      }
      finally {}
      continue;
      label250:
      Object localObject = localMessage;
      if (bool1)
      {
        boolean bool2 = localMessage.isAsynchronous();
        localObject = localMessage;
        if (bool2)
        {
          bool1 = false;
          localObject = localMessage;
        }
      }
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      dispose();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  boolean hasMessages(Handler paramHandler, int paramInt, Object paramObject)
  {
    if (paramHandler == null) {
      return false;
    }
    try
    {
      for (Message localMessage = this.mMessages; localMessage != null; localMessage = localMessage.next) {
        if ((localMessage.target == paramHandler) && (localMessage.what == paramInt)) {
          if (paramObject != null)
          {
            Object localObject = localMessage.obj;
            if (localObject != paramObject) {}
          }
          else
          {
            return true;
          }
        }
      }
      return false;
    }
    finally {}
  }
  
  boolean hasMessages(Handler paramHandler, Runnable paramRunnable, Object paramObject)
  {
    if (paramHandler == null) {
      return false;
    }
    try
    {
      for (Message localMessage = this.mMessages; localMessage != null; localMessage = localMessage.next) {
        if ((localMessage.target == paramHandler) && (localMessage.callback == paramRunnable)) {
          if (paramObject != null)
          {
            Object localObject = localMessage.obj;
            if (localObject != paramObject) {}
          }
          else
          {
            return true;
          }
        }
      }
      return false;
    }
    finally {}
  }
  
  /* Error */
  public boolean isIdle()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: aload_0
    //   3: monitorenter
    //   4: invokestatic 143	android/os/SystemClock:uptimeMillis	()J
    //   7: lstore_3
    //   8: iload_2
    //   9: istore_1
    //   10: aload_0
    //   11: getfield 134	android/os/MessageQueue:mMessages	Landroid/os/Message;
    //   14: ifnull +21 -> 35
    //   17: aload_0
    //   18: getfield 134	android/os/MessageQueue:mMessages	Landroid/os/Message;
    //   21: getfield 129	android/os/Message:when	J
    //   24: lstore 5
    //   26: lload_3
    //   27: lload 5
    //   29: lcmp
    //   30: ifge +9 -> 39
    //   33: iload_2
    //   34: istore_1
    //   35: aload_0
    //   36: monitorexit
    //   37: iload_1
    //   38: ireturn
    //   39: iconst_0
    //   40: istore_1
    //   41: goto -6 -> 35
    //   44: astore 7
    //   46: aload_0
    //   47: monitorexit
    //   48: aload 7
    //   50: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	51	0	this	MessageQueue
    //   9	32	1	bool1	boolean
    //   1	33	2	bool2	boolean
    //   7	20	3	l1	long
    //   24	4	5	l2	long
    //   44	5	7	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   4	8	44	finally
    //   10	26	44	finally
  }
  
  public boolean isPolling()
  {
    try
    {
      boolean bool = isPollingLocked();
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  Message next()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 55	android/os/MessageQueue:mPtr	J
    //   4: lstore 4
    //   6: lload 4
    //   8: lconst_0
    //   9: lcmp
    //   10: ifne +5 -> 15
    //   13: aconst_null
    //   14: areturn
    //   15: iconst_m1
    //   16: istore_1
    //   17: iconst_0
    //   18: istore_2
    //   19: iload_2
    //   20: ifeq +6 -> 26
    //   23: invokestatic 299	android/os/Binder:flushPendingCommands	()V
    //   26: aload_0
    //   27: lload 4
    //   29: iload_2
    //   30: invokespecial 301	android/os/MessageQueue:nativePollOnce	(JI)V
    //   33: aload_0
    //   34: monitorenter
    //   35: invokestatic 143	android/os/SystemClock:uptimeMillis	()J
    //   38: lstore 6
    //   40: aconst_null
    //   41: astore 13
    //   43: aload_0
    //   44: getfield 134	android/os/MessageQueue:mMessages	Landroid/os/Message;
    //   47: astore 12
    //   49: aload 12
    //   51: astore 10
    //   53: aload 13
    //   55: astore 11
    //   57: aload 12
    //   59: ifnull +67 -> 126
    //   62: aload 12
    //   64: astore 10
    //   66: aload 13
    //   68: astore 11
    //   70: aload 12
    //   72: getfield 236	android/os/Message:target	Landroid/os/Handler;
    //   75: ifnonnull +51 -> 126
    //   78: aload 12
    //   80: astore 10
    //   82: aload 10
    //   84: astore 12
    //   86: aload 10
    //   88: getfield 137	android/os/Message:next	Landroid/os/Message;
    //   91: astore 13
    //   93: aload 13
    //   95: astore 10
    //   97: aload 12
    //   99: astore 11
    //   101: aload 13
    //   103: ifnull +23 -> 126
    //   106: aload 13
    //   108: astore 10
    //   110: aload 13
    //   112: invokevirtual 270	android/os/Message:isAsynchronous	()Z
    //   115: ifeq -33 -> 82
    //   118: aload 12
    //   120: astore 11
    //   122: aload 13
    //   124: astore 10
    //   126: aload 10
    //   128: ifnull +100 -> 228
    //   131: lload 6
    //   133: aload 10
    //   135: getfield 129	android/os/Message:when	J
    //   138: lcmp
    //   139: ifge +34 -> 173
    //   142: aload 10
    //   144: getfield 129	android/os/Message:when	J
    //   147: lload 6
    //   149: lsub
    //   150: ldc2_w 302
    //   153: invokestatic 309	java/lang/Math:min	(JJ)J
    //   156: l2i
    //   157: istore_2
    //   158: aload_0
    //   159: getfield 104	android/os/MessageQueue:mQuitting	Z
    //   162: ifeq +71 -> 233
    //   165: aload_0
    //   166: invokespecial 275	android/os/MessageQueue:dispose	()V
    //   169: aload_0
    //   170: monitorexit
    //   171: aconst_null
    //   172: areturn
    //   173: aload_0
    //   174: iconst_0
    //   175: putfield 265	android/os/MessageQueue:mBlocked	Z
    //   178: aload 11
    //   180: ifnull +29 -> 209
    //   183: aload 11
    //   185: aload 10
    //   187: getfield 137	android/os/Message:next	Landroid/os/Message;
    //   190: putfield 137	android/os/Message:next	Landroid/os/Message;
    //   193: aload 10
    //   195: aconst_null
    //   196: putfield 137	android/os/Message:next	Landroid/os/Message;
    //   199: aload 10
    //   201: invokevirtual 126	android/os/Message:markInUse	()V
    //   204: aload_0
    //   205: monitorexit
    //   206: aload 10
    //   208: areturn
    //   209: aload_0
    //   210: aload 10
    //   212: getfield 137	android/os/Message:next	Landroid/os/Message;
    //   215: putfield 134	android/os/MessageQueue:mMessages	Landroid/os/Message;
    //   218: goto -25 -> 193
    //   221: astore 10
    //   223: aload_0
    //   224: monitorexit
    //   225: aload 10
    //   227: athrow
    //   228: iconst_m1
    //   229: istore_2
    //   230: goto -72 -> 158
    //   233: iload_1
    //   234: istore_3
    //   235: iload_1
    //   236: ifge +33 -> 269
    //   239: aload_0
    //   240: getfield 134	android/os/MessageQueue:mMessages	Landroid/os/Message;
    //   243: ifnull +18 -> 261
    //   246: iload_1
    //   247: istore_3
    //   248: lload 6
    //   250: aload_0
    //   251: getfield 134	android/os/MessageQueue:mMessages	Landroid/os/Message;
    //   254: getfield 129	android/os/Message:when	J
    //   257: lcmp
    //   258: ifge +11 -> 269
    //   261: aload_0
    //   262: getfield 47	android/os/MessageQueue:mIdleHandlers	Ljava/util/ArrayList;
    //   265: invokevirtual 312	java/util/ArrayList:size	()I
    //   268: istore_3
    //   269: iload_3
    //   270: ifgt +15 -> 285
    //   273: aload_0
    //   274: iconst_1
    //   275: putfield 265	android/os/MessageQueue:mBlocked	Z
    //   278: aload_0
    //   279: monitorexit
    //   280: iload_3
    //   281: istore_1
    //   282: goto -263 -> 19
    //   285: aload_0
    //   286: getfield 314	android/os/MessageQueue:mPendingIdleHandlers	[Landroid/os/MessageQueue$IdleHandler;
    //   289: ifnonnull +15 -> 304
    //   292: aload_0
    //   293: iload_3
    //   294: iconst_4
    //   295: invokestatic 317	java/lang/Math:max	(II)I
    //   298: anewarray 9	android/os/MessageQueue$IdleHandler
    //   301: putfield 314	android/os/MessageQueue:mPendingIdleHandlers	[Landroid/os/MessageQueue$IdleHandler;
    //   304: aload_0
    //   305: aload_0
    //   306: getfield 47	android/os/MessageQueue:mIdleHandlers	Ljava/util/ArrayList;
    //   309: aload_0
    //   310: getfield 314	android/os/MessageQueue:mPendingIdleHandlers	[Landroid/os/MessageQueue$IdleHandler;
    //   313: invokevirtual 321	java/util/ArrayList:toArray	([Ljava/lang/Object;)[Ljava/lang/Object;
    //   316: checkcast 322	[Landroid/os/MessageQueue$IdleHandler;
    //   319: putfield 314	android/os/MessageQueue:mPendingIdleHandlers	[Landroid/os/MessageQueue$IdleHandler;
    //   322: aload_0
    //   323: monitorexit
    //   324: iconst_0
    //   325: istore_1
    //   326: iload_1
    //   327: iload_3
    //   328: if_icmpge +83 -> 411
    //   331: aload_0
    //   332: getfield 314	android/os/MessageQueue:mPendingIdleHandlers	[Landroid/os/MessageQueue$IdleHandler;
    //   335: iload_1
    //   336: aaload
    //   337: astore 10
    //   339: aload_0
    //   340: getfield 314	android/os/MessageQueue:mPendingIdleHandlers	[Landroid/os/MessageQueue$IdleHandler;
    //   343: iload_1
    //   344: aconst_null
    //   345: aastore
    //   346: iconst_0
    //   347: istore 8
    //   349: aload 10
    //   351: invokeinterface 325 1 0
    //   356: istore 9
    //   358: iload 9
    //   360: istore 8
    //   362: iload 8
    //   364: ifne +17 -> 381
    //   367: aload_0
    //   368: monitorenter
    //   369: aload_0
    //   370: getfield 47	android/os/MessageQueue:mIdleHandlers	Ljava/util/ArrayList;
    //   373: aload 10
    //   375: invokevirtual 328	java/util/ArrayList:remove	(Ljava/lang/Object;)Z
    //   378: pop
    //   379: aload_0
    //   380: monitorexit
    //   381: iload_1
    //   382: iconst_1
    //   383: iadd
    //   384: istore_1
    //   385: goto -59 -> 326
    //   388: astore 11
    //   390: ldc 20
    //   392: ldc_w 330
    //   395: aload 11
    //   397: invokestatic 333	android/util/Log:wtf	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   400: pop
    //   401: goto -39 -> 362
    //   404: astore 10
    //   406: aload_0
    //   407: monitorexit
    //   408: aload 10
    //   410: athrow
    //   411: iconst_0
    //   412: istore_1
    //   413: iconst_0
    //   414: istore_2
    //   415: goto -396 -> 19
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	418	0	this	MessageQueue
    //   16	397	1	i	int
    //   18	397	2	j	int
    //   234	95	3	k	int
    //   4	24	4	l1	long
    //   38	211	6	l2	long
    //   347	16	8	m	int
    //   356	3	9	bool	boolean
    //   51	160	10	localObject1	Object
    //   221	5	10	localObject2	Object
    //   337	37	10	localIdleHandler	IdleHandler
    //   404	5	10	localObject3	Object
    //   55	129	11	localObject4	Object
    //   388	8	11	localThrowable	Throwable
    //   47	72	12	localObject5	Object
    //   41	82	13	localMessage	Message
    // Exception table:
    //   from	to	target	type
    //   35	40	221	finally
    //   43	49	221	finally
    //   70	78	221	finally
    //   86	93	221	finally
    //   110	118	221	finally
    //   131	158	221	finally
    //   158	169	221	finally
    //   173	178	221	finally
    //   183	193	221	finally
    //   193	204	221	finally
    //   209	218	221	finally
    //   239	246	221	finally
    //   248	261	221	finally
    //   261	269	221	finally
    //   273	278	221	finally
    //   285	304	221	finally
    //   304	322	221	finally
    //   349	358	388	java/lang/Throwable
    //   369	379	404	finally
  }
  
  public int postSyncBarrier()
  {
    return postSyncBarrier(SystemClock.uptimeMillis());
  }
  
  public int postSyncBarrierRightBehind()
  {
    try
    {
      int i = this.mNextBarrierToken;
      this.mNextBarrierToken = (i + 1);
      Message localMessage = Message.obtain();
      localMessage.markInUse();
      localMessage.when = SystemClock.uptimeMillis();
      localMessage.arg1 = i;
      localMessage.next = this.mMessages;
      this.mMessages = localMessage;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  /* Error */
  void quit(boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 49	android/os/MessageQueue:mQuitAllowed	Z
    //   4: ifne +14 -> 18
    //   7: new 243	java/lang/IllegalStateException
    //   10: dup
    //   11: ldc_w 339
    //   14: invokespecial 249	java/lang/IllegalStateException:<init>	(Ljava/lang/String;)V
    //   17: athrow
    //   18: aload_0
    //   19: monitorenter
    //   20: aload_0
    //   21: getfield 104	android/os/MessageQueue:mQuitting	Z
    //   24: istore_2
    //   25: iload_2
    //   26: ifeq +6 -> 32
    //   29: aload_0
    //   30: monitorexit
    //   31: return
    //   32: aload_0
    //   33: iconst_1
    //   34: putfield 104	android/os/MessageQueue:mQuitting	Z
    //   37: iload_1
    //   38: ifeq +17 -> 55
    //   41: aload_0
    //   42: invokespecial 341	android/os/MessageQueue:removeAllFutureMessagesLocked	()V
    //   45: aload_0
    //   46: getfield 55	android/os/MessageQueue:mPtr	J
    //   49: invokestatic 267	android/os/MessageQueue:nativeWake	(J)V
    //   52: aload_0
    //   53: monitorexit
    //   54: return
    //   55: aload_0
    //   56: invokespecial 146	android/os/MessageQueue:removeAllMessagesLocked	()V
    //   59: goto -14 -> 45
    //   62: astore_3
    //   63: aload_0
    //   64: monitorexit
    //   65: aload_3
    //   66: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	67	0	this	MessageQueue
    //   0	67	1	paramBoolean	boolean
    //   24	2	2	bool	boolean
    //   62	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   20	25	62	finally
    //   32	37	62	finally
    //   41	45	62	finally
    //   45	52	62	finally
    //   55	59	62	finally
  }
  
  void removeCallbacksAndMessages(Handler paramHandler, Object paramObject)
  {
    if (paramHandler == null) {
      return;
    }
    for (;;)
    {
      try
      {
        Object localObject1 = this.mMessages;
        Object localObject2 = localObject1;
        if (localObject1 != null)
        {
          localObject2 = localObject1;
          if (((Message)localObject1).target == paramHandler) {
            if (paramObject != null)
            {
              localObject2 = localObject1;
              if (((Message)localObject1).obj != paramObject) {}
            }
            else
            {
              localObject2 = ((Message)localObject1).next;
              this.mMessages = ((Message)localObject2);
              ((Message)localObject1).recycleUnchecked();
              localObject1 = localObject2;
              continue;
            }
          }
        }
        if (localObject2 == null) {
          break;
        }
        localObject1 = ((Message)localObject2).next;
        if ((localObject1 != null) && (((Message)localObject1).target == paramHandler) && ((paramObject == null) || (((Message)localObject1).obj == paramObject)))
        {
          Message localMessage = ((Message)localObject1).next;
          ((Message)localObject1).recycleUnchecked();
          ((Message)localObject2).next = localMessage;
        }
        else
        {
          localObject2 = localObject1;
        }
      }
      finally {}
    }
  }
  
  public void removeIdleHandler(IdleHandler paramIdleHandler)
  {
    try
    {
      this.mIdleHandlers.remove(paramIdleHandler);
      return;
    }
    finally
    {
      paramIdleHandler = finally;
      throw paramIdleHandler;
    }
  }
  
  void removeMessages(Handler paramHandler, int paramInt, Object paramObject)
  {
    if (paramHandler == null) {
      return;
    }
    for (;;)
    {
      try
      {
        Object localObject1 = this.mMessages;
        Object localObject2 = localObject1;
        if (localObject1 != null)
        {
          localObject2 = localObject1;
          if (((Message)localObject1).target == paramHandler)
          {
            localObject2 = localObject1;
            if (((Message)localObject1).what == paramInt) {
              if (paramObject != null)
              {
                localObject2 = localObject1;
                if (((Message)localObject1).obj != paramObject) {}
              }
              else
              {
                localObject2 = ((Message)localObject1).next;
                this.mMessages = ((Message)localObject2);
                ((Message)localObject1).recycleUnchecked();
                localObject1 = localObject2;
                continue;
              }
            }
          }
        }
        if (localObject2 == null) {
          break;
        }
        localObject1 = ((Message)localObject2).next;
        if ((localObject1 != null) && (((Message)localObject1).target == paramHandler) && (((Message)localObject1).what == paramInt) && ((paramObject == null) || (((Message)localObject1).obj == paramObject)))
        {
          Message localMessage = ((Message)localObject1).next;
          ((Message)localObject1).recycleUnchecked();
          ((Message)localObject2).next = localMessage;
        }
        else
        {
          localObject2 = localObject1;
        }
      }
      finally {}
    }
  }
  
  void removeMessages(Handler paramHandler, Runnable paramRunnable, Object paramObject)
  {
    if ((paramHandler == null) || (paramRunnable == null)) {
      return;
    }
    for (;;)
    {
      try
      {
        Object localObject1 = this.mMessages;
        Object localObject2 = localObject1;
        if (localObject1 != null)
        {
          localObject2 = localObject1;
          if (((Message)localObject1).target == paramHandler)
          {
            localObject2 = localObject1;
            if (((Message)localObject1).callback == paramRunnable) {
              if (paramObject != null)
              {
                localObject2 = localObject1;
                if (((Message)localObject1).obj != paramObject) {}
              }
              else
              {
                localObject2 = ((Message)localObject1).next;
                this.mMessages = ((Message)localObject2);
                ((Message)localObject1).recycleUnchecked();
                localObject1 = localObject2;
                continue;
              }
            }
          }
        }
        if (localObject2 == null) {
          break;
        }
        localObject1 = ((Message)localObject2).next;
        if ((localObject1 != null) && (((Message)localObject1).target == paramHandler) && (((Message)localObject1).callback == paramRunnable) && ((paramObject == null) || (((Message)localObject1).obj == paramObject)))
        {
          Message localMessage = ((Message)localObject1).next;
          ((Message)localObject1).recycleUnchecked();
          ((Message)localObject2).next = localMessage;
        }
        else
        {
          localObject2 = localObject1;
        }
      }
      finally {}
    }
  }
  
  public void removeOnFileDescriptorEventListener(FileDescriptor paramFileDescriptor)
  {
    if (paramFileDescriptor == null) {
      throw new IllegalArgumentException("fd must not be null");
    }
    try
    {
      updateOnFileDescriptorEventListenerLocked(paramFileDescriptor, 0, null);
      return;
    }
    finally
    {
      paramFileDescriptor = finally;
      throw paramFileDescriptor;
    }
  }
  
  public void removeSyncBarrier(int paramInt)
  {
    Object localObject2 = null;
    try
    {
      for (Message localMessage = this.mMessages; (localMessage != null) && ((localMessage.target != null) || (localMessage.arg1 != paramInt)); localMessage = localMessage.next) {
        localObject2 = localMessage;
      }
      if (localMessage == null) {
        throw new IllegalStateException("The specified message queue synchronization  barrier token has not been posted or has already been removed.");
      }
    }
    finally {}
    if (localObject2 != null)
    {
      ((Message)localObject2).next = ((Message)localObject1).next;
      paramInt = 0;
    }
    for (;;)
    {
      ((Message)localObject1).recycleUnchecked();
      if (paramInt != 0)
      {
        boolean bool = this.mQuitting;
        if (!bool) {
          break label124;
        }
      }
      for (;;)
      {
        return;
        this.mMessages = ((Message)localObject1).next;
        if (this.mMessages == null) {
          break;
        }
        if (this.mMessages.target == null) {
          break label139;
        }
        break;
        label124:
        nativeWake(this.mPtr);
      }
      paramInt = 1;
      continue;
      label139:
      paramInt = 0;
    }
  }
  
  private static final class FileDescriptorRecord
  {
    public final FileDescriptor mDescriptor;
    public int mEvents;
    public MessageQueue.OnFileDescriptorEventListener mListener;
    public int mSeq;
    
    public FileDescriptorRecord(FileDescriptor paramFileDescriptor, int paramInt, MessageQueue.OnFileDescriptorEventListener paramOnFileDescriptorEventListener)
    {
      this.mDescriptor = paramFileDescriptor;
      this.mEvents = paramInt;
      this.mListener = paramOnFileDescriptorEventListener;
    }
  }
  
  public static abstract interface IdleHandler
  {
    public abstract boolean queueIdle();
  }
  
  public static abstract interface OnFileDescriptorEventListener
  {
    public static final int EVENT_ERROR = 4;
    public static final int EVENT_INPUT = 1;
    public static final int EVENT_OUTPUT = 2;
    
    public abstract int onFileDescriptorEvents(FileDescriptor paramFileDescriptor, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/MessageQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */