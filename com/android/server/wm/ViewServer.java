package com.android.server.wm;

import android.util.Slog;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ViewServer
  implements Runnable
{
  private static final String COMMAND_PROTOCOL_VERSION = "PROTOCOL";
  private static final String COMMAND_SERVER_VERSION = "SERVER";
  private static final String COMMAND_WINDOW_MANAGER_AUTOLIST = "AUTOLIST";
  private static final String COMMAND_WINDOW_MANAGER_GET_FOCUS = "GET_FOCUS";
  private static final String COMMAND_WINDOW_MANAGER_LIST = "LIST";
  private static final String LOG_TAG = "WindowManager";
  private static final String VALUE_PROTOCOL_VERSION = "4";
  private static final String VALUE_SERVER_VERSION = "4";
  public static final int VIEW_SERVER_DEFAULT_PORT = 4939;
  private static final int VIEW_SERVER_MAX_CONNECTIONS = 10;
  private final int mPort;
  private ServerSocket mServer;
  private Thread mThread;
  private ExecutorService mThreadPool;
  private final WindowManagerService mWindowManager;
  
  ViewServer(WindowManagerService paramWindowManagerService, int paramInt)
  {
    this.mWindowManager = paramWindowManagerService;
    this.mPort = paramInt;
  }
  
  /* Error */
  private static boolean writeValue(Socket paramSocket, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: new 74	java/io/BufferedWriter
    //   9: dup
    //   10: new 76	java/io/OutputStreamWriter
    //   13: dup
    //   14: aload_0
    //   15: invokevirtual 82	java/net/Socket:getOutputStream	()Ljava/io/OutputStream;
    //   18: invokespecial 85	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;)V
    //   21: sipush 8192
    //   24: invokespecial 88	java/io/BufferedWriter:<init>	(Ljava/io/Writer;I)V
    //   27: astore_0
    //   28: aload_0
    //   29: aload_1
    //   30: invokevirtual 92	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   33: aload_0
    //   34: ldc 94
    //   36: invokevirtual 92	java/io/BufferedWriter:write	(Ljava/lang/String;)V
    //   39: aload_0
    //   40: invokevirtual 97	java/io/BufferedWriter:flush	()V
    //   43: iconst_1
    //   44: istore_3
    //   45: iload_3
    //   46: istore_2
    //   47: aload_0
    //   48: ifnull +9 -> 57
    //   51: aload_0
    //   52: invokevirtual 100	java/io/BufferedWriter:close	()V
    //   55: iload_3
    //   56: istore_2
    //   57: iload_2
    //   58: ireturn
    //   59: astore_0
    //   60: iconst_0
    //   61: istore_2
    //   62: goto -5 -> 57
    //   65: astore_0
    //   66: aload 5
    //   68: astore_0
    //   69: iconst_0
    //   70: istore_2
    //   71: aload_0
    //   72: ifnull -15 -> 57
    //   75: aload_0
    //   76: invokevirtual 100	java/io/BufferedWriter:close	()V
    //   79: iconst_0
    //   80: ireturn
    //   81: astore_0
    //   82: iconst_0
    //   83: ireturn
    //   84: astore_0
    //   85: aload 4
    //   87: astore_1
    //   88: aload_1
    //   89: ifnull +7 -> 96
    //   92: aload_1
    //   93: invokevirtual 100	java/io/BufferedWriter:close	()V
    //   96: aload_0
    //   97: athrow
    //   98: astore_1
    //   99: goto -3 -> 96
    //   102: astore 4
    //   104: aload_0
    //   105: astore_1
    //   106: aload 4
    //   108: astore_0
    //   109: goto -21 -> 88
    //   112: astore_1
    //   113: goto -44 -> 69
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	116	0	paramSocket	Socket
    //   0	116	1	paramString	String
    //   46	25	2	bool1	boolean
    //   44	12	3	bool2	boolean
    //   1	85	4	localObject1	Object
    //   102	5	4	localObject2	Object
    //   4	63	5	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   51	55	59	java/io/IOException
    //   6	28	65	java/lang/Exception
    //   75	79	81	java/io/IOException
    //   6	28	84	finally
    //   92	96	98	java/io/IOException
    //   28	43	102	finally
    //   28	43	112	java/lang/Exception
  }
  
  boolean isRunning()
  {
    if (this.mThread != null) {
      return this.mThread.isAlive();
    }
    return false;
  }
  
  public void run()
  {
    while (Thread.currentThread() == this.mThread)
    {
      try
      {
        Socket localSocket = this.mServer.accept();
        if (this.mThreadPool == null) {
          break label61;
        }
        this.mThreadPool.submit(new ViewServerWorker(localSocket));
      }
      catch (Exception localException)
      {
        Slog.w(LOG_TAG, "Connection error: ", localException);
      }
      continue;
      try
      {
        label61:
        localException.close();
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
    }
  }
  
  boolean start()
    throws IOException
  {
    if (this.mThread != null) {
      return false;
    }
    this.mServer = new ServerSocket(this.mPort, 10, InetAddress.getLocalHost());
    this.mThread = new Thread(this, "Remote View Server [port=" + this.mPort + "]");
    this.mThreadPool = Executors.newFixedThreadPool(10);
    this.mThread.start();
    return true;
  }
  
  boolean stop()
  {
    if (this.mThread != null)
    {
      this.mThread.interrupt();
      if (this.mThreadPool != null) {}
      try
      {
        this.mThreadPool.shutdownNow();
        this.mThreadPool = null;
        this.mThread = null;
      }
      catch (SecurityException localSecurityException)
      {
        for (;;)
        {
          try
          {
            this.mServer.close();
            this.mServer = null;
            return true;
          }
          catch (IOException localIOException)
          {
            Slog.w(LOG_TAG, "Could not close the view server");
          }
          localSecurityException = localSecurityException;
          Slog.w(LOG_TAG, "Could not stop all view server threads");
        }
      }
    }
    return false;
  }
  
  class ViewServerWorker
    implements Runnable, WindowManagerService.WindowChangeListener
  {
    private Socket mClient;
    private boolean mNeedFocusedWindowUpdate;
    private boolean mNeedWindowListUpdate;
    
    public ViewServerWorker(Socket paramSocket)
    {
      this.mClient = paramSocket;
      this.mNeedWindowListUpdate = false;
      this.mNeedFocusedWindowUpdate = false;
    }
    
    /* Error */
    private boolean windowManagerAutolistLoop()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 22	com/android/server/wm/ViewServer$ViewServerWorker:this$0	Lcom/android/server/wm/ViewServer;
      //   4: invokestatic 42	com/android/server/wm/ViewServer:-get1	(Lcom/android/server/wm/ViewServer;)Lcom/android/server/wm/WindowManagerService;
      //   7: aload_0
      //   8: invokevirtual 48	com/android/server/wm/WindowManagerService:addWindowChangeListener	(Lcom/android/server/wm/WindowManagerService$WindowChangeListener;)V
      //   11: aconst_null
      //   12: astore 5
      //   14: aconst_null
      //   15: astore 4
      //   17: new 50	java/io/BufferedWriter
      //   20: dup
      //   21: new 52	java/io/OutputStreamWriter
      //   24: dup
      //   25: aload_0
      //   26: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   29: invokevirtual 58	java/net/Socket:getOutputStream	()Ljava/io/OutputStream;
      //   32: invokespecial 61	java/io/OutputStreamWriter:<init>	(Ljava/io/OutputStream;)V
      //   35: invokespecial 64	java/io/BufferedWriter:<init>	(Ljava/io/Writer;)V
      //   38: astore_3
      //   39: invokestatic 69	java/lang/Thread:interrupted	()Z
      //   42: ifne +152 -> 194
      //   45: iconst_0
      //   46: istore_1
      //   47: iconst_0
      //   48: istore_2
      //   49: aload_0
      //   50: monitorenter
      //   51: aload_0
      //   52: getfield 29	com/android/server/wm/ViewServer$ViewServerWorker:mNeedWindowListUpdate	Z
      //   55: ifne +10 -> 65
      //   58: aload_0
      //   59: getfield 31	com/android/server/wm/ViewServer$ViewServerWorker:mNeedFocusedWindowUpdate	Z
      //   62: ifeq +87 -> 149
      //   65: aload_0
      //   66: getfield 29	com/android/server/wm/ViewServer$ViewServerWorker:mNeedWindowListUpdate	Z
      //   69: ifeq +10 -> 79
      //   72: aload_0
      //   73: iconst_0
      //   74: putfield 29	com/android/server/wm/ViewServer$ViewServerWorker:mNeedWindowListUpdate	Z
      //   77: iconst_1
      //   78: istore_1
      //   79: aload_0
      //   80: getfield 31	com/android/server/wm/ViewServer$ViewServerWorker:mNeedFocusedWindowUpdate	Z
      //   83: ifeq +10 -> 93
      //   86: aload_0
      //   87: iconst_0
      //   88: putfield 31	com/android/server/wm/ViewServer$ViewServerWorker:mNeedFocusedWindowUpdate	Z
      //   91: iconst_1
      //   92: istore_2
      //   93: aload_0
      //   94: monitorexit
      //   95: iload_1
      //   96: ifeq +13 -> 109
      //   99: aload_3
      //   100: ldc 71
      //   102: invokevirtual 75	java/io/BufferedWriter:write	(Ljava/lang/String;)V
      //   105: aload_3
      //   106: invokevirtual 78	java/io/BufferedWriter:flush	()V
      //   109: iload_2
      //   110: ifeq -71 -> 39
      //   113: aload_3
      //   114: ldc 80
      //   116: invokevirtual 75	java/io/BufferedWriter:write	(Ljava/lang/String;)V
      //   119: aload_3
      //   120: invokevirtual 78	java/io/BufferedWriter:flush	()V
      //   123: goto -84 -> 39
      //   126: astore 4
      //   128: aload_3
      //   129: ifnull +7 -> 136
      //   132: aload_3
      //   133: invokevirtual 83	java/io/BufferedWriter:close	()V
      //   136: aload_0
      //   137: getfield 22	com/android/server/wm/ViewServer$ViewServerWorker:this$0	Lcom/android/server/wm/ViewServer;
      //   140: invokestatic 42	com/android/server/wm/ViewServer:-get1	(Lcom/android/server/wm/ViewServer;)Lcom/android/server/wm/WindowManagerService;
      //   143: aload_0
      //   144: invokevirtual 86	com/android/server/wm/WindowManagerService:removeWindowChangeListener	(Lcom/android/server/wm/WindowManagerService$WindowChangeListener;)V
      //   147: iconst_1
      //   148: ireturn
      //   149: aload_0
      //   150: invokevirtual 89	com/android/server/wm/ViewServer$ViewServerWorker:wait	()V
      //   153: goto -102 -> 51
      //   156: astore 4
      //   158: aload_0
      //   159: monitorexit
      //   160: aload 4
      //   162: athrow
      //   163: astore 5
      //   165: aload_3
      //   166: astore 4
      //   168: aload 5
      //   170: astore_3
      //   171: aload 4
      //   173: ifnull +8 -> 181
      //   176: aload 4
      //   178: invokevirtual 83	java/io/BufferedWriter:close	()V
      //   181: aload_0
      //   182: getfield 22	com/android/server/wm/ViewServer$ViewServerWorker:this$0	Lcom/android/server/wm/ViewServer;
      //   185: invokestatic 42	com/android/server/wm/ViewServer:-get1	(Lcom/android/server/wm/ViewServer;)Lcom/android/server/wm/WindowManagerService;
      //   188: aload_0
      //   189: invokevirtual 86	com/android/server/wm/WindowManagerService:removeWindowChangeListener	(Lcom/android/server/wm/WindowManagerService$WindowChangeListener;)V
      //   192: aload_3
      //   193: athrow
      //   194: aload_3
      //   195: ifnull +7 -> 202
      //   198: aload_3
      //   199: invokevirtual 83	java/io/BufferedWriter:close	()V
      //   202: aload_0
      //   203: getfield 22	com/android/server/wm/ViewServer$ViewServerWorker:this$0	Lcom/android/server/wm/ViewServer;
      //   206: invokestatic 42	com/android/server/wm/ViewServer:-get1	(Lcom/android/server/wm/ViewServer;)Lcom/android/server/wm/WindowManagerService;
      //   209: aload_0
      //   210: invokevirtual 86	com/android/server/wm/WindowManagerService:removeWindowChangeListener	(Lcom/android/server/wm/WindowManagerService$WindowChangeListener;)V
      //   213: goto -66 -> 147
      //   216: astore_3
      //   217: goto -15 -> 202
      //   220: astore_3
      //   221: goto -85 -> 136
      //   224: astore 4
      //   226: goto -45 -> 181
      //   229: astore_3
      //   230: aload 5
      //   232: astore 4
      //   234: goto -63 -> 171
      //   237: astore_3
      //   238: aload 4
      //   240: astore_3
      //   241: goto -113 -> 128
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	244	0	this	ViewServerWorker
      //   46	50	1	i	int
      //   48	62	2	j	int
      //   38	161	3	localObject1	Object
      //   216	1	3	localIOException1	IOException
      //   220	1	3	localIOException2	IOException
      //   229	1	3	localObject2	Object
      //   237	1	3	localException1	Exception
      //   240	1	3	localObject3	Object
      //   15	1	4	localObject4	Object
      //   126	1	4	localException2	Exception
      //   156	5	4	localObject5	Object
      //   166	11	4	localObject6	Object
      //   224	1	4	localIOException3	IOException
      //   232	7	4	localObject7	Object
      //   12	1	5	localObject8	Object
      //   163	68	5	localObject9	Object
      // Exception table:
      //   from	to	target	type
      //   39	45	126	java/lang/Exception
      //   49	51	126	java/lang/Exception
      //   93	95	126	java/lang/Exception
      //   99	109	126	java/lang/Exception
      //   113	123	126	java/lang/Exception
      //   158	163	126	java/lang/Exception
      //   51	65	156	finally
      //   65	77	156	finally
      //   79	91	156	finally
      //   149	153	156	finally
      //   39	45	163	finally
      //   49	51	163	finally
      //   93	95	163	finally
      //   99	109	163	finally
      //   113	123	163	finally
      //   158	163	163	finally
      //   198	202	216	java/io/IOException
      //   132	136	220	java/io/IOException
      //   176	181	224	java/io/IOException
      //   17	39	229	finally
      //   17	39	237	java/lang/Exception
    }
    
    public void focusChanged()
    {
      try
      {
        this.mNeedFocusedWindowUpdate = true;
        notifyAll();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_3
      //   2: aconst_null
      //   3: astore 5
      //   5: new 96	java/io/BufferedReader
      //   8: dup
      //   9: new 98	java/io/InputStreamReader
      //   12: dup
      //   13: aload_0
      //   14: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   17: invokevirtual 102	java/net/Socket:getInputStream	()Ljava/io/InputStream;
      //   20: invokespecial 105	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
      //   23: sipush 1024
      //   26: invokespecial 108	java/io/BufferedReader:<init>	(Ljava/io/Reader;I)V
      //   29: astore 4
      //   31: aload 4
      //   33: invokevirtual 112	java/io/BufferedReader:readLine	()Ljava/lang/String;
      //   36: astore 5
      //   38: aload 5
      //   40: bipush 32
      //   42: invokevirtual 118	java/lang/String:indexOf	(I)I
      //   45: istore_1
      //   46: iload_1
      //   47: iconst_m1
      //   48: if_icmpne +84 -> 132
      //   51: aload 5
      //   53: astore_3
      //   54: ldc 120
      //   56: astore 5
      //   58: ldc 122
      //   60: aload_3
      //   61: invokevirtual 126	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
      //   64: ifeq +89 -> 153
      //   67: aload_0
      //   68: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   71: ldc -128
      //   73: invokestatic 132	com/android/server/wm/ViewServer:-wrap0	(Ljava/net/Socket;Ljava/lang/String;)Z
      //   76: istore_2
      //   77: iload_2
      //   78: ifne +29 -> 107
      //   81: invokestatic 135	com/android/server/wm/ViewServer:-get0	()Ljava/lang/String;
      //   84: new 137	java/lang/StringBuilder
      //   87: dup
      //   88: invokespecial 138	java/lang/StringBuilder:<init>	()V
      //   91: ldc -116
      //   93: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   96: aload_3
      //   97: invokevirtual 144	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   100: invokevirtual 147	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   103: invokestatic 153	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;)I
      //   106: pop
      //   107: aload 4
      //   109: ifnull +8 -> 117
      //   112: aload 4
      //   114: invokevirtual 154	java/io/BufferedReader:close	()V
      //   117: aload_0
      //   118: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   121: ifnull +10 -> 131
      //   124: aload_0
      //   125: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   128: invokevirtual 155	java/net/Socket:close	()V
      //   131: return
      //   132: aload 5
      //   134: iconst_0
      //   135: iload_1
      //   136: invokevirtual 159	java/lang/String:substring	(II)Ljava/lang/String;
      //   139: astore_3
      //   140: aload 5
      //   142: iload_1
      //   143: iconst_1
      //   144: iadd
      //   145: invokevirtual 162	java/lang/String:substring	(I)Ljava/lang/String;
      //   148: astore 5
      //   150: goto -92 -> 58
      //   153: ldc -92
      //   155: aload_3
      //   156: invokevirtual 126	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
      //   159: ifeq +16 -> 175
      //   162: aload_0
      //   163: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   166: ldc -128
      //   168: invokestatic 132	com/android/server/wm/ViewServer:-wrap0	(Ljava/net/Socket;Ljava/lang/String;)Z
      //   171: istore_2
      //   172: goto -95 -> 77
      //   175: ldc -90
      //   177: aload_3
      //   178: invokevirtual 126	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
      //   181: ifeq +21 -> 202
      //   184: aload_0
      //   185: getfield 22	com/android/server/wm/ViewServer$ViewServerWorker:this$0	Lcom/android/server/wm/ViewServer;
      //   188: invokestatic 42	com/android/server/wm/ViewServer:-get1	(Lcom/android/server/wm/ViewServer;)Lcom/android/server/wm/WindowManagerService;
      //   191: aload_0
      //   192: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   195: invokevirtual 170	com/android/server/wm/WindowManagerService:viewServerListWindows	(Ljava/net/Socket;)Z
      //   198: istore_2
      //   199: goto -122 -> 77
      //   202: ldc -84
      //   204: aload_3
      //   205: invokevirtual 126	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
      //   208: ifeq +21 -> 229
      //   211: aload_0
      //   212: getfield 22	com/android/server/wm/ViewServer$ViewServerWorker:this$0	Lcom/android/server/wm/ViewServer;
      //   215: invokestatic 42	com/android/server/wm/ViewServer:-get1	(Lcom/android/server/wm/ViewServer;)Lcom/android/server/wm/WindowManagerService;
      //   218: aload_0
      //   219: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   222: invokevirtual 175	com/android/server/wm/WindowManagerService:viewServerGetFocusedWindow	(Ljava/net/Socket;)Z
      //   225: istore_2
      //   226: goto -149 -> 77
      //   229: ldc -79
      //   231: aload_3
      //   232: invokevirtual 126	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
      //   235: ifeq +11 -> 246
      //   238: aload_0
      //   239: invokespecial 179	com/android/server/wm/ViewServer$ViewServerWorker:windowManagerAutolistLoop	()Z
      //   242: istore_2
      //   243: goto -166 -> 77
      //   246: aload_0
      //   247: getfield 22	com/android/server/wm/ViewServer$ViewServerWorker:this$0	Lcom/android/server/wm/ViewServer;
      //   250: invokestatic 42	com/android/server/wm/ViewServer:-get1	(Lcom/android/server/wm/ViewServer;)Lcom/android/server/wm/WindowManagerService;
      //   253: aload_0
      //   254: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   257: aload_3
      //   258: aload 5
      //   260: invokevirtual 183	com/android/server/wm/WindowManagerService:viewServerWindowCommand	(Ljava/net/Socket;Ljava/lang/String;Ljava/lang/String;)Z
      //   263: istore_2
      //   264: goto -187 -> 77
      //   267: astore_3
      //   268: aload_3
      //   269: invokevirtual 186	java/io/IOException:printStackTrace	()V
      //   272: goto -155 -> 117
      //   275: astore_3
      //   276: aload_3
      //   277: invokevirtual 186	java/io/IOException:printStackTrace	()V
      //   280: goto -149 -> 131
      //   283: astore_3
      //   284: aload 5
      //   286: astore 4
      //   288: aload_3
      //   289: astore 5
      //   291: aload 4
      //   293: astore_3
      //   294: invokestatic 135	com/android/server/wm/ViewServer:-get0	()Ljava/lang/String;
      //   297: ldc -68
      //   299: aload 5
      //   301: invokestatic 191	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   304: pop
      //   305: aload 4
      //   307: ifnull +8 -> 315
      //   310: aload 4
      //   312: invokevirtual 154	java/io/BufferedReader:close	()V
      //   315: aload_0
      //   316: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   319: ifnull -188 -> 131
      //   322: aload_0
      //   323: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   326: invokevirtual 155	java/net/Socket:close	()V
      //   329: return
      //   330: astore_3
      //   331: aload_3
      //   332: invokevirtual 186	java/io/IOException:printStackTrace	()V
      //   335: return
      //   336: astore_3
      //   337: aload_3
      //   338: invokevirtual 186	java/io/IOException:printStackTrace	()V
      //   341: goto -26 -> 315
      //   344: astore 4
      //   346: aload_3
      //   347: ifnull +7 -> 354
      //   350: aload_3
      //   351: invokevirtual 154	java/io/BufferedReader:close	()V
      //   354: aload_0
      //   355: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   358: ifnull +10 -> 368
      //   361: aload_0
      //   362: getfield 27	com/android/server/wm/ViewServer$ViewServerWorker:mClient	Ljava/net/Socket;
      //   365: invokevirtual 155	java/net/Socket:close	()V
      //   368: aload 4
      //   370: athrow
      //   371: astore_3
      //   372: aload_3
      //   373: invokevirtual 186	java/io/IOException:printStackTrace	()V
      //   376: goto -22 -> 354
      //   379: astore_3
      //   380: aload_3
      //   381: invokevirtual 186	java/io/IOException:printStackTrace	()V
      //   384: goto -16 -> 368
      //   387: astore 5
      //   389: aload 4
      //   391: astore_3
      //   392: aload 5
      //   394: astore 4
      //   396: goto -50 -> 346
      //   399: astore 5
      //   401: goto -110 -> 291
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	404	0	this	ViewServerWorker
      //   45	100	1	i	int
      //   76	188	2	bool	boolean
      //   1	257	3	localObject1	Object
      //   267	2	3	localIOException1	IOException
      //   275	2	3	localIOException2	IOException
      //   283	6	3	localIOException3	IOException
      //   293	1	3	localObject2	Object
      //   330	2	3	localIOException4	IOException
      //   336	15	3	localIOException5	IOException
      //   371	2	3	localIOException6	IOException
      //   379	2	3	localIOException7	IOException
      //   391	1	3	localObject3	Object
      //   29	282	4	localObject4	Object
      //   344	46	4	localObject5	Object
      //   394	1	4	localObject6	Object
      //   3	297	5	localObject7	Object
      //   387	6	5	localObject8	Object
      //   399	1	5	localIOException8	IOException
      // Exception table:
      //   from	to	target	type
      //   112	117	267	java/io/IOException
      //   124	131	275	java/io/IOException
      //   5	31	283	java/io/IOException
      //   322	329	330	java/io/IOException
      //   310	315	336	java/io/IOException
      //   5	31	344	finally
      //   294	305	344	finally
      //   350	354	371	java/io/IOException
      //   361	368	379	java/io/IOException
      //   31	46	387	finally
      //   58	77	387	finally
      //   81	107	387	finally
      //   132	150	387	finally
      //   153	172	387	finally
      //   175	199	387	finally
      //   202	226	387	finally
      //   229	243	387	finally
      //   246	264	387	finally
      //   31	46	399	java/io/IOException
      //   58	77	399	java/io/IOException
      //   81	107	399	java/io/IOException
      //   132	150	399	java/io/IOException
      //   153	172	399	java/io/IOException
      //   175	199	399	java/io/IOException
      //   202	226	399	java/io/IOException
      //   229	243	399	java/io/IOException
      //   246	264	399	java/io/IOException
    }
    
    public void windowsChanged()
    {
      try
      {
        this.mNeedWindowListUpdate = true;
        notifyAll();
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/ViewServer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */