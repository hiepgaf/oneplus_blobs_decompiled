package com.android.server;

import android.net.LocalSocketAddress;
import android.net.LocalSocketAddress.Namespace;
import android.os.Build;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.util.LocalLog;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import com.google.android.collect.Lists;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

final class NativeDaemonConnector
  implements Runnable, Handler.Callback, Watchdog.Monitor
{
  private static final long DEFAULT_TIMEOUT = 60000L;
  private static final boolean VDBG = false;
  private static final long WARN_EXECUTE_DELAY_MS = 500L;
  private final int BUFFER_SIZE = 4096;
  private final String TAG;
  private Handler mCallbackHandler;
  private INativeDaemonConnectorCallbacks mCallbacks;
  private final Object mDaemonLock = new Object();
  private volatile boolean mDebug = false;
  private LocalLog mLocalLog;
  private final Looper mLooper;
  private OutputStream mOutputStream;
  private final ResponseQueue mResponseQueue;
  private AtomicInteger mSequenceNumber;
  private String mSocket;
  private final PowerManager.WakeLock mWakeLock;
  private volatile Object mWarnIfHeld;
  
  NativeDaemonConnector(INativeDaemonConnectorCallbacks paramINativeDaemonConnectorCallbacks, String paramString1, int paramInt1, String paramString2, int paramInt2, PowerManager.WakeLock paramWakeLock)
  {
    this(paramINativeDaemonConnectorCallbacks, paramString1, paramInt1, paramString2, paramInt2, paramWakeLock, FgThread.get().getLooper());
  }
  
  NativeDaemonConnector(INativeDaemonConnectorCallbacks paramINativeDaemonConnectorCallbacks, String paramString1, int paramInt1, String paramString2, int paramInt2, PowerManager.WakeLock paramWakeLock, Looper paramLooper)
  {
    this.mCallbacks = paramINativeDaemonConnectorCallbacks;
    this.mSocket = paramString1;
    this.mResponseQueue = new ResponseQueue(paramInt1);
    this.mWakeLock = paramWakeLock;
    if (this.mWakeLock != null) {
      this.mWakeLock.setReferenceCounted(true);
    }
    this.mLooper = paramLooper;
    this.mSequenceNumber = new AtomicInteger(0);
    if (paramString2 != null) {}
    for (;;)
    {
      this.TAG = paramString2;
      this.mLocalLog = new LocalLog(paramInt2);
      return;
      paramString2 = "NativeDaemonConnector";
    }
  }
  
  static void appendEscaped(StringBuilder paramStringBuilder, String paramString)
  {
    int i = 0;
    if (paramString.indexOf(' ') >= 0) {
      i = 1;
    }
    if (i != 0) {
      paramStringBuilder.append('"');
    }
    int k = paramString.length();
    int j = 0;
    if (j < k)
    {
      char c = paramString.charAt(j);
      if (c == '"') {
        paramStringBuilder.append("\\\"");
      }
      for (;;)
      {
        j += 1;
        break;
        if (c == '\\') {
          paramStringBuilder.append("\\\\");
        } else {
          paramStringBuilder.append(c);
        }
      }
    }
    if (i != 0) {
      paramStringBuilder.append('"');
    }
  }
  
  private LocalSocketAddress determineSocketAddress()
  {
    if ((this.mSocket.startsWith("__test__")) && (Build.IS_DEBUGGABLE)) {
      return new LocalSocketAddress(this.mSocket);
    }
    return new LocalSocketAddress(this.mSocket, LocalSocketAddress.Namespace.RESERVED);
  }
  
  /* Error */
  private void listenToSocket()
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 10
    //   3: aconst_null
    //   4: astore 12
    //   6: new 186	android/net/LocalSocket
    //   9: dup
    //   10: invokespecial 187	android/net/LocalSocket:<init>	()V
    //   13: astore 11
    //   15: aload 11
    //   17: aload_0
    //   18: invokespecial 189	com/android/server/NativeDaemonConnector:determineSocketAddress	()Landroid/net/LocalSocketAddress;
    //   21: invokevirtual 193	android/net/LocalSocket:connect	(Landroid/net/LocalSocketAddress;)V
    //   24: aload 11
    //   26: invokevirtual 197	android/net/LocalSocket:getInputStream	()Ljava/io/InputStream;
    //   29: astore 10
    //   31: aload_0
    //   32: getfield 88	com/android/server/NativeDaemonConnector:mDaemonLock	Ljava/lang/Object;
    //   35: astore 12
    //   37: aload 12
    //   39: monitorenter
    //   40: aload_0
    //   41: aload 11
    //   43: invokevirtual 201	android/net/LocalSocket:getOutputStream	()Ljava/io/OutputStream;
    //   46: putfield 203	com/android/server/NativeDaemonConnector:mOutputStream	Ljava/io/OutputStream;
    //   49: aload 12
    //   51: monitorexit
    //   52: aload_0
    //   53: getfield 92	com/android/server/NativeDaemonConnector:mCallbacks	Lcom/android/server/INativeDaemonConnectorCallbacks;
    //   56: invokeinterface 208 1 0
    //   61: sipush 4096
    //   64: newarray <illegal type>
    //   66: astore 12
    //   68: iconst_0
    //   69: istore_1
    //   70: aload 10
    //   72: aload 12
    //   74: iload_1
    //   75: sipush 4096
    //   78: iload_1
    //   79: isub
    //   80: invokevirtual 214	java/io/InputStream:read	([BII)I
    //   83: istore_2
    //   84: iload_2
    //   85: ifge +204 -> 289
    //   88: aload_0
    //   89: new 133	java/lang/StringBuilder
    //   92: dup
    //   93: invokespecial 215	java/lang/StringBuilder:<init>	()V
    //   96: ldc -39
    //   98: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   101: iload_2
    //   102: invokevirtual 220	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   105: ldc -34
    //   107: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: iload_1
    //   111: invokevirtual 220	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   114: invokevirtual 226	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   117: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   120: aload_0
    //   121: getfield 88	com/android/server/NativeDaemonConnector:mDaemonLock	Ljava/lang/Object;
    //   124: astore 10
    //   126: aload 10
    //   128: monitorenter
    //   129: aload_0
    //   130: getfield 203	com/android/server/NativeDaemonConnector:mOutputStream	Ljava/io/OutputStream;
    //   133: astore 12
    //   135: aload 12
    //   137: ifnull +41 -> 178
    //   140: aload_0
    //   141: new 133	java/lang/StringBuilder
    //   144: dup
    //   145: invokespecial 215	java/lang/StringBuilder:<init>	()V
    //   148: ldc -25
    //   150: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   153: aload_0
    //   154: getfield 94	com/android/server/NativeDaemonConnector:mSocket	Ljava/lang/String;
    //   157: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: invokevirtual 226	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   163: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   166: aload_0
    //   167: getfield 203	com/android/server/NativeDaemonConnector:mOutputStream	Ljava/io/OutputStream;
    //   170: invokevirtual 236	java/io/OutputStream:close	()V
    //   173: aload_0
    //   174: aconst_null
    //   175: putfield 203	com/android/server/NativeDaemonConnector:mOutputStream	Ljava/io/OutputStream;
    //   178: aload 10
    //   180: monitorexit
    //   181: aload 11
    //   183: ifnull +8 -> 191
    //   186: aload 11
    //   188: invokevirtual 237	android/net/LocalSocket:close	()V
    //   191: return
    //   192: astore 10
    //   194: aload 12
    //   196: monitorexit
    //   197: aload 10
    //   199: athrow
    //   200: astore 12
    //   202: aload 11
    //   204: astore 10
    //   206: aload 12
    //   208: astore 11
    //   210: aload 11
    //   212: athrow
    //   213: astore 11
    //   215: aload_0
    //   216: getfield 88	com/android/server/NativeDaemonConnector:mDaemonLock	Ljava/lang/Object;
    //   219: astore 12
    //   221: aload 12
    //   223: monitorenter
    //   224: aload_0
    //   225: getfield 203	com/android/server/NativeDaemonConnector:mOutputStream	Ljava/io/OutputStream;
    //   228: astore 13
    //   230: aload 13
    //   232: ifnull +41 -> 273
    //   235: aload_0
    //   236: new 133	java/lang/StringBuilder
    //   239: dup
    //   240: invokespecial 215	java/lang/StringBuilder:<init>	()V
    //   243: ldc -25
    //   245: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   248: aload_0
    //   249: getfield 94	com/android/server/NativeDaemonConnector:mSocket	Ljava/lang/String;
    //   252: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   255: invokevirtual 226	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   258: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   261: aload_0
    //   262: getfield 203	com/android/server/NativeDaemonConnector:mOutputStream	Ljava/io/OutputStream;
    //   265: invokevirtual 236	java/io/OutputStream:close	()V
    //   268: aload_0
    //   269: aconst_null
    //   270: putfield 203	com/android/server/NativeDaemonConnector:mOutputStream	Ljava/io/OutputStream;
    //   273: aload 12
    //   275: monitorexit
    //   276: aload 10
    //   278: ifnull +8 -> 286
    //   281: aload 10
    //   283: invokevirtual 237	android/net/LocalSocket:close	()V
    //   286: aload 11
    //   288: athrow
    //   289: aload 11
    //   291: invokevirtual 241	android/net/LocalSocket:getAncillaryFileDescriptors	()[Ljava/io/FileDescriptor;
    //   294: astore 13
    //   296: iload_2
    //   297: iload_1
    //   298: iadd
    //   299: istore 8
    //   301: iconst_0
    //   302: istore_1
    //   303: iconst_0
    //   304: istore 4
    //   306: iload 4
    //   308: iload 8
    //   310: if_icmpge +325 -> 635
    //   313: iload_1
    //   314: istore_2
    //   315: aload 12
    //   317: iload 4
    //   319: baload
    //   320: ifne +222 -> 542
    //   323: new 127	java/lang/String
    //   326: dup
    //   327: aload 12
    //   329: iload_1
    //   330: iload 4
    //   332: iload_1
    //   333: isub
    //   334: getstatic 247	java/nio/charset/StandardCharsets:UTF_8	Ljava/nio/charset/Charset;
    //   337: invokespecial 250	java/lang/String:<init>	([BIILjava/nio/charset/Charset;)V
    //   340: astore 14
    //   342: iconst_0
    //   343: istore_1
    //   344: iconst_0
    //   345: istore 6
    //   347: iconst_0
    //   348: istore 7
    //   350: iconst_0
    //   351: istore 5
    //   353: iload 6
    //   355: istore_2
    //   356: iload 7
    //   358: istore_3
    //   359: aload 14
    //   361: aload 13
    //   363: invokestatic 256	com/android/server/NativeDaemonEvent:parseRawEvent	(Ljava/lang/String;[Ljava/io/FileDescriptor;)Lcom/android/server/NativeDaemonEvent;
    //   366: astore 14
    //   368: iload 6
    //   370: istore_2
    //   371: iload 7
    //   373: istore_3
    //   374: aload_0
    //   375: new 133	java/lang/StringBuilder
    //   378: dup
    //   379: invokespecial 215	java/lang/StringBuilder:<init>	()V
    //   382: ldc_w 258
    //   385: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   388: aload 14
    //   390: invokevirtual 261	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   393: ldc_w 263
    //   396: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   399: invokevirtual 226	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   402: invokespecial 266	com/android/server/NativeDaemonConnector:log	(Ljava/lang/String;)V
    //   405: iload 6
    //   407: istore_2
    //   408: iload 7
    //   410: istore_3
    //   411: aload 14
    //   413: invokevirtual 270	com/android/server/NativeDaemonEvent:isClassUnsolicited	()Z
    //   416: ifeq +137 -> 553
    //   419: iload 5
    //   421: istore_1
    //   422: iload 6
    //   424: istore_2
    //   425: iload 7
    //   427: istore_3
    //   428: aload_0
    //   429: getfield 92	com/android/server/NativeDaemonConnector:mCallbacks	Lcom/android/server/INativeDaemonConnectorCallbacks;
    //   432: aload 14
    //   434: invokevirtual 273	com/android/server/NativeDaemonEvent:getCode	()I
    //   437: invokeinterface 277 2 0
    //   442: ifeq +34 -> 476
    //   445: iload 5
    //   447: istore_1
    //   448: iload 6
    //   450: istore_2
    //   451: iload 7
    //   453: istore_3
    //   454: aload_0
    //   455: getfield 101	com/android/server/NativeDaemonConnector:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   458: ifnull +18 -> 476
    //   461: iload 6
    //   463: istore_2
    //   464: iload 7
    //   466: istore_3
    //   467: aload_0
    //   468: getfield 101	com/android/server/NativeDaemonConnector:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   471: invokevirtual 280	android/os/PowerManager$WakeLock:acquire	()V
    //   474: iconst_1
    //   475: istore_1
    //   476: iload_1
    //   477: istore_2
    //   478: iload_1
    //   479: istore_3
    //   480: aload_0
    //   481: getfield 282	com/android/server/NativeDaemonConnector:mCallbackHandler	Landroid/os/Handler;
    //   484: aload 14
    //   486: invokevirtual 273	com/android/server/NativeDaemonEvent:getCode	()I
    //   489: aload_0
    //   490: invokespecial 285	com/android/server/NativeDaemonConnector:uptimeMillisInt	()I
    //   493: iconst_0
    //   494: aload 14
    //   496: invokevirtual 288	com/android/server/NativeDaemonEvent:getRawEvent	()Ljava/lang/String;
    //   499: invokevirtual 294	android/os/Handler:obtainMessage	(IIILjava/lang/Object;)Landroid/os/Message;
    //   502: astore 14
    //   504: iload_1
    //   505: istore_2
    //   506: iload_1
    //   507: istore_3
    //   508: aload_0
    //   509: getfield 282	com/android/server/NativeDaemonConnector:mCallbackHandler	Landroid/os/Handler;
    //   512: aload 14
    //   514: invokevirtual 298	android/os/Handler:sendMessage	(Landroid/os/Message;)Z
    //   517: istore 9
    //   519: iload 9
    //   521: ifeq +5 -> 526
    //   524: iconst_0
    //   525: istore_1
    //   526: iload_1
    //   527: ifeq +10 -> 537
    //   530: aload_0
    //   531: getfield 101	com/android/server/NativeDaemonConnector:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   534: invokevirtual 301	android/os/PowerManager$WakeLock:release	()V
    //   537: iload 4
    //   539: iconst_1
    //   540: iadd
    //   541: istore_2
    //   542: iload 4
    //   544: iconst_1
    //   545: iadd
    //   546: istore 4
    //   548: iload_2
    //   549: istore_1
    //   550: goto -244 -> 306
    //   553: iload 6
    //   555: istore_2
    //   556: iload 7
    //   558: istore_3
    //   559: aload_0
    //   560: getfield 99	com/android/server/NativeDaemonConnector:mResponseQueue	Lcom/android/server/NativeDaemonConnector$ResponseQueue;
    //   563: aload 14
    //   565: invokevirtual 304	com/android/server/NativeDaemonEvent:getCmdNumber	()I
    //   568: aload 14
    //   570: invokevirtual 308	com/android/server/NativeDaemonConnector$ResponseQueue:add	(ILcom/android/server/NativeDaemonEvent;)V
    //   573: goto -47 -> 526
    //   576: astore 14
    //   578: iload_2
    //   579: istore_3
    //   580: aload_0
    //   581: new 133	java/lang/StringBuilder
    //   584: dup
    //   585: invokespecial 215	java/lang/StringBuilder:<init>	()V
    //   588: ldc_w 310
    //   591: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   594: aload 14
    //   596: invokevirtual 261	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   599: invokevirtual 226	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   602: invokespecial 266	com/android/server/NativeDaemonConnector:log	(Ljava/lang/String;)V
    //   605: iload_2
    //   606: ifeq -69 -> 537
    //   609: aload_0
    //   610: getfield 101	com/android/server/NativeDaemonConnector:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   613: invokevirtual 301	android/os/PowerManager$WakeLock:release	()V
    //   616: goto -79 -> 537
    //   619: astore 10
    //   621: iload_3
    //   622: ifeq +10 -> 632
    //   625: aload_0
    //   626: getfield 101	com/android/server/NativeDaemonConnector:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   629: invokevirtual 301	android/os/PowerManager$WakeLock:release	()V
    //   632: aload 10
    //   634: athrow
    //   635: iload_1
    //   636: ifne +10 -> 646
    //   639: aload_0
    //   640: ldc_w 312
    //   643: invokespecial 266	com/android/server/NativeDaemonConnector:log	(Ljava/lang/String;)V
    //   646: iload_1
    //   647: iload 8
    //   649: if_icmpeq +24 -> 673
    //   652: sipush 4096
    //   655: iload_1
    //   656: isub
    //   657: istore_2
    //   658: aload 12
    //   660: iload_1
    //   661: aload 12
    //   663: iconst_0
    //   664: iload_2
    //   665: invokestatic 318	java/lang/System:arraycopy	([BI[BII)V
    //   668: iload_2
    //   669: istore_1
    //   670: goto -600 -> 70
    //   673: iconst_0
    //   674: istore_1
    //   675: goto -605 -> 70
    //   678: astore 12
    //   680: aload_0
    //   681: new 133	java/lang/StringBuilder
    //   684: dup
    //   685: invokespecial 215	java/lang/StringBuilder:<init>	()V
    //   688: ldc_w 320
    //   691: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   694: aload 12
    //   696: invokevirtual 261	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   699: invokevirtual 226	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   702: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   705: goto -532 -> 173
    //   708: astore 11
    //   710: aload 10
    //   712: monitorexit
    //   713: aload 11
    //   715: athrow
    //   716: astore 10
    //   718: aload_0
    //   719: new 133	java/lang/StringBuilder
    //   722: dup
    //   723: invokespecial 215	java/lang/StringBuilder:<init>	()V
    //   726: ldc_w 322
    //   729: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   732: aload 10
    //   734: invokevirtual 261	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   737: invokevirtual 226	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   740: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   743: return
    //   744: astore 13
    //   746: aload_0
    //   747: new 133	java/lang/StringBuilder
    //   750: dup
    //   751: invokespecial 215	java/lang/StringBuilder:<init>	()V
    //   754: ldc_w 320
    //   757: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   760: aload 13
    //   762: invokevirtual 261	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   765: invokevirtual 226	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   768: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   771: goto -503 -> 268
    //   774: astore 10
    //   776: aload 12
    //   778: monitorexit
    //   779: aload 10
    //   781: athrow
    //   782: astore 10
    //   784: aload_0
    //   785: new 133	java/lang/StringBuilder
    //   788: dup
    //   789: invokespecial 215	java/lang/StringBuilder:<init>	()V
    //   792: ldc_w 322
    //   795: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   798: aload 10
    //   800: invokevirtual 261	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   803: invokevirtual 226	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   806: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   809: goto -523 -> 286
    //   812: astore 11
    //   814: aload 12
    //   816: astore 10
    //   818: goto -608 -> 210
    //   821: astore 12
    //   823: aload 11
    //   825: astore 10
    //   827: aload 12
    //   829: astore 11
    //   831: goto -616 -> 215
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	834	0	this	NativeDaemonConnector
    //   69	606	1	i	int
    //   83	586	2	j	int
    //   358	264	3	k	int
    //   304	243	4	m	int
    //   351	95	5	n	int
    //   345	209	6	i1	int
    //   348	209	7	i2	int
    //   299	351	8	i3	int
    //   517	3	9	bool	boolean
    //   1	178	10	localObject1	Object
    //   192	6	10	localObject2	Object
    //   204	78	10	localObject3	Object
    //   619	92	10	localObject4	Object
    //   716	17	10	localIOException1	IOException
    //   774	6	10	localObject5	Object
    //   782	17	10	localIOException2	IOException
    //   816	10	10	localObject6	Object
    //   13	198	11	localObject7	Object
    //   213	77	11	localObject8	Object
    //   708	6	11	localObject9	Object
    //   812	12	11	localIOException3	IOException
    //   829	1	11	localObject10	Object
    //   4	191	12	localObject11	Object
    //   200	7	12	localIOException4	IOException
    //   219	443	12	localObject12	Object
    //   678	137	12	localIOException5	IOException
    //   821	7	12	localObject13	Object
    //   228	134	13	localObject14	Object
    //   744	17	13	localIOException6	IOException
    //   340	229	14	localObject15	Object
    //   576	19	14	localIllegalArgumentException	IllegalArgumentException
    // Exception table:
    //   from	to	target	type
    //   40	49	192	finally
    //   15	40	200	java/io/IOException
    //   49	68	200	java/io/IOException
    //   70	84	200	java/io/IOException
    //   88	120	200	java/io/IOException
    //   194	200	200	java/io/IOException
    //   289	296	200	java/io/IOException
    //   323	342	200	java/io/IOException
    //   530	537	200	java/io/IOException
    //   609	616	200	java/io/IOException
    //   625	632	200	java/io/IOException
    //   632	635	200	java/io/IOException
    //   639	646	200	java/io/IOException
    //   658	668	200	java/io/IOException
    //   6	15	213	finally
    //   210	213	213	finally
    //   359	368	576	java/lang/IllegalArgumentException
    //   374	405	576	java/lang/IllegalArgumentException
    //   411	419	576	java/lang/IllegalArgumentException
    //   428	445	576	java/lang/IllegalArgumentException
    //   454	461	576	java/lang/IllegalArgumentException
    //   467	474	576	java/lang/IllegalArgumentException
    //   480	504	576	java/lang/IllegalArgumentException
    //   508	519	576	java/lang/IllegalArgumentException
    //   559	573	576	java/lang/IllegalArgumentException
    //   359	368	619	finally
    //   374	405	619	finally
    //   411	419	619	finally
    //   428	445	619	finally
    //   454	461	619	finally
    //   467	474	619	finally
    //   480	504	619	finally
    //   508	519	619	finally
    //   559	573	619	finally
    //   580	605	619	finally
    //   140	173	678	java/io/IOException
    //   129	135	708	finally
    //   140	173	708	finally
    //   173	178	708	finally
    //   680	705	708	finally
    //   186	191	716	java/io/IOException
    //   235	268	744	java/io/IOException
    //   224	230	774	finally
    //   235	268	774	finally
    //   268	273	774	finally
    //   746	771	774	finally
    //   281	286	782	java/io/IOException
    //   6	15	812	java/io/IOException
    //   15	40	821	finally
    //   49	68	821	finally
    //   70	84	821	finally
    //   88	120	821	finally
    //   194	200	821	finally
    //   289	296	821	finally
    //   323	342	821	finally
    //   530	537	821	finally
    //   609	616	821	finally
    //   625	632	821	finally
    //   632	635	821	finally
    //   639	646	821	finally
    //   658	668	821	finally
  }
  
  private void log(String paramString)
  {
    if (this.mDebug) {
      Slog.d(this.TAG, paramString);
    }
    this.mLocalLog.log(paramString);
  }
  
  private void loge(String paramString)
  {
    Slog.e(this.TAG, paramString);
    this.mLocalLog.log(paramString);
  }
  
  static void makeCommand(StringBuilder paramStringBuilder1, StringBuilder paramStringBuilder2, int paramInt, String paramString, Object... paramVarArgs)
  {
    if (paramString.indexOf(0) >= 0) {
      throw new IllegalArgumentException("Unexpected command: " + paramString);
    }
    if (paramString.indexOf(' ') >= 0) {
      throw new IllegalArgumentException("Arguments must be separate from command");
    }
    paramStringBuilder1.append(paramInt).append(' ').append(paramString);
    paramStringBuilder2.append(paramInt).append(' ').append(paramString);
    int i = paramVarArgs.length;
    paramInt = 0;
    if (paramInt < i)
    {
      paramString = paramVarArgs[paramInt];
      String str = String.valueOf(paramString);
      if (str.indexOf(0) >= 0) {
        throw new IllegalArgumentException("Unexpected argument: " + paramString);
      }
      paramStringBuilder1.append(' ');
      paramStringBuilder2.append(' ');
      appendEscaped(paramStringBuilder1, str);
      if ((paramString instanceof SensitiveArg)) {
        paramStringBuilder2.append("[scrubbed]");
      }
      for (;;)
      {
        paramInt += 1;
        break;
        appendEscaped(paramStringBuilder2, str);
      }
    }
    paramStringBuilder1.append('\000');
  }
  
  private int uptimeMillisInt()
  {
    return (int)SystemClock.uptimeMillis() & 0x7FFFFFFF;
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    this.mLocalLog.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    paramPrintWriter.println();
    this.mResponseQueue.dump(paramFileDescriptor, paramPrintWriter, paramArrayOfString);
  }
  
  public NativeDaemonEvent execute(long paramLong, String paramString, Object... paramVarArgs)
    throws NativeDaemonConnectorException
  {
    paramString = executeForList(paramLong, paramString, paramVarArgs);
    if (paramString.length != 1) {
      throw new NativeDaemonConnectorException("Expected exactly one response, but received " + paramString.length);
    }
    return paramString[0];
  }
  
  public NativeDaemonEvent execute(Command paramCommand)
    throws NativeDaemonConnectorException
  {
    return execute(Command.-get1(paramCommand), Command.-get0(paramCommand).toArray());
  }
  
  public NativeDaemonEvent execute(String paramString, Object... paramVarArgs)
    throws NativeDaemonConnectorException
  {
    return execute(60000L, paramString, paramVarArgs);
  }
  
  public NativeDaemonEvent[] executeForList(long paramLong, String arg3, Object... paramVarArgs)
    throws NativeDaemonConnectorException
  {
    if ((this.mWarnIfHeld != null) && (Thread.holdsLock(this.mWarnIfHeld))) {
      Slog.wtf(this.TAG, "Calling thread " + Thread.currentThread().getName() + " is holding 0x" + Integer.toHexString(System.identityHashCode(this.mWarnIfHeld)), new Throwable());
    }
    long l = SystemClock.elapsedRealtime();
    ArrayList localArrayList = Lists.newArrayList();
    StringBuilder localStringBuilder = new StringBuilder();
    Object localObject = new StringBuilder();
    int i = this.mSequenceNumber.incrementAndGet();
    makeCommand(localStringBuilder, (StringBuilder)localObject, i, ???, paramVarArgs);
    paramVarArgs = localStringBuilder.toString();
    localObject = ((StringBuilder)localObject).toString();
    log("SND -> {" + (String)localObject + "}");
    synchronized (this.mDaemonLock)
    {
      if (this.mOutputStream == null) {
        throw new NativeDaemonConnectorException("missing output stream");
      }
    }
    do
    {
      try
      {
        this.mOutputStream.write(paramVarArgs.getBytes(StandardCharsets.UTF_8));
        ??? = this.mResponseQueue.remove(i, paramLong, (String)localObject);
        if (??? == null)
        {
          loge("timed-out waiting for response to " + (String)localObject);
          throw new NativeDaemonTimeoutException((String)localObject, ???);
        }
      }
      catch (IOException paramVarArgs)
      {
        throw new NativeDaemonConnectorException("problem sending command", paramVarArgs);
      }
      localArrayList.add(???);
    } while (???.isClassContinue());
    paramLong = SystemClock.elapsedRealtime();
    if (paramLong - l > 500L) {
      loge("NDC Command {" + (String)localObject + "} took too long (" + (paramLong - l) + "ms)");
    }
    if (???.isClassClientError()) {
      throw new NativeDaemonArgumentException((String)localObject, ???);
    }
    if (???.isClassServerError()) {
      throw new NativeDaemonFailureException((String)localObject, ???);
    }
    return (NativeDaemonEvent[])localArrayList.toArray(new NativeDaemonEvent[localArrayList.size()]);
  }
  
  public NativeDaemonEvent[] executeForList(Command paramCommand)
    throws NativeDaemonConnectorException
  {
    return executeForList(Command.-get1(paramCommand), Command.-get0(paramCommand).toArray());
  }
  
  public NativeDaemonEvent[] executeForList(String paramString, Object... paramVarArgs)
    throws NativeDaemonConnectorException
  {
    return executeForList(60000L, paramString, paramVarArgs);
  }
  
  /* Error */
  public boolean handleMessage(android.os.Message paramMessage)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 518	android/os/Message:obj	Ljava/lang/Object;
    //   4: checkcast 127	java/lang/String
    //   7: astore 5
    //   9: aload_0
    //   10: invokespecial 285	com/android/server/NativeDaemonConnector:uptimeMillisInt	()I
    //   13: istore_2
    //   14: aload_1
    //   15: getfield 521	android/os/Message:arg1	I
    //   18: istore_3
    //   19: aload_0
    //   20: getfield 92	com/android/server/NativeDaemonConnector:mCallbacks	Lcom/android/server/INativeDaemonConnectorCallbacks;
    //   23: aload_1
    //   24: getfield 524	android/os/Message:what	I
    //   27: aload 5
    //   29: aload 5
    //   31: invokestatic 528	com/android/server/NativeDaemonEvent:unescapeArgs	(Ljava/lang/String;)[Ljava/lang/String;
    //   34: invokeinterface 532 4 0
    //   39: ifne +22 -> 61
    //   42: aload_0
    //   43: ldc_w 534
    //   46: iconst_1
    //   47: anewarray 4	java/lang/Object
    //   50: dup
    //   51: iconst_0
    //   52: aload 5
    //   54: aastore
    //   55: invokestatic 538	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   58: invokespecial 266	com/android/server/NativeDaemonConnector:log	(Ljava/lang/String;)V
    //   61: aload_0
    //   62: getfield 92	com/android/server/NativeDaemonConnector:mCallbacks	Lcom/android/server/INativeDaemonConnectorCallbacks;
    //   65: aload_1
    //   66: getfield 524	android/os/Message:what	I
    //   69: invokeinterface 277 2 0
    //   74: ifeq +17 -> 91
    //   77: aload_0
    //   78: getfield 101	com/android/server/NativeDaemonConnector:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   81: ifnull +10 -> 91
    //   84: aload_0
    //   85: getfield 101	com/android/server/NativeDaemonConnector:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   88: invokevirtual 301	android/os/PowerManager$WakeLock:release	()V
    //   91: aload_0
    //   92: invokespecial 285	com/android/server/NativeDaemonConnector:uptimeMillisInt	()I
    //   95: istore 4
    //   97: iload_2
    //   98: iload_3
    //   99: if_icmple +42 -> 141
    //   102: iload_2
    //   103: iload_3
    //   104: isub
    //   105: i2l
    //   106: ldc2_w 39
    //   109: lcmp
    //   110: ifle +31 -> 141
    //   113: aload_0
    //   114: ldc_w 540
    //   117: iconst_2
    //   118: anewarray 4	java/lang/Object
    //   121: dup
    //   122: iconst_0
    //   123: aload 5
    //   125: aastore
    //   126: dup
    //   127: iconst_1
    //   128: iload_2
    //   129: iload_3
    //   130: isub
    //   131: invokestatic 543	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   134: aastore
    //   135: invokestatic 538	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   138: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   141: iload 4
    //   143: iload_2
    //   144: if_icmple +44 -> 188
    //   147: iload 4
    //   149: iload_2
    //   150: isub
    //   151: i2l
    //   152: ldc2_w 39
    //   155: lcmp
    //   156: ifle +32 -> 188
    //   159: aload_0
    //   160: ldc_w 545
    //   163: iconst_2
    //   164: anewarray 4	java/lang/Object
    //   167: dup
    //   168: iconst_0
    //   169: aload 5
    //   171: aastore
    //   172: dup
    //   173: iconst_1
    //   174: iload 4
    //   176: iload_2
    //   177: isub
    //   178: invokestatic 543	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   181: aastore
    //   182: invokestatic 538	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   185: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   188: iconst_1
    //   189: ireturn
    //   190: astore 6
    //   192: aload_0
    //   193: new 133	java/lang/StringBuilder
    //   196: dup
    //   197: invokespecial 215	java/lang/StringBuilder:<init>	()V
    //   200: ldc_w 547
    //   203: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: aload 5
    //   208: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   211: ldc_w 549
    //   214: invokevirtual 150	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   217: aload 6
    //   219: invokevirtual 261	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   222: invokevirtual 226	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   225: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   228: aload_0
    //   229: getfield 92	com/android/server/NativeDaemonConnector:mCallbacks	Lcom/android/server/INativeDaemonConnectorCallbacks;
    //   232: aload_1
    //   233: getfield 524	android/os/Message:what	I
    //   236: invokeinterface 277 2 0
    //   241: ifeq +17 -> 258
    //   244: aload_0
    //   245: getfield 101	com/android/server/NativeDaemonConnector:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   248: ifnull +10 -> 258
    //   251: aload_0
    //   252: getfield 101	com/android/server/NativeDaemonConnector:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   255: invokevirtual 301	android/os/PowerManager$WakeLock:release	()V
    //   258: aload_0
    //   259: invokespecial 285	com/android/server/NativeDaemonConnector:uptimeMillisInt	()I
    //   262: istore 4
    //   264: iload_2
    //   265: iload_3
    //   266: if_icmple +42 -> 308
    //   269: iload_2
    //   270: iload_3
    //   271: isub
    //   272: i2l
    //   273: ldc2_w 39
    //   276: lcmp
    //   277: ifle +31 -> 308
    //   280: aload_0
    //   281: ldc_w 540
    //   284: iconst_2
    //   285: anewarray 4	java/lang/Object
    //   288: dup
    //   289: iconst_0
    //   290: aload 5
    //   292: aastore
    //   293: dup
    //   294: iconst_1
    //   295: iload_2
    //   296: iload_3
    //   297: isub
    //   298: invokestatic 543	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   301: aastore
    //   302: invokestatic 538	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   305: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   308: iload 4
    //   310: iload_2
    //   311: if_icmple -123 -> 188
    //   314: iload 4
    //   316: iload_2
    //   317: isub
    //   318: i2l
    //   319: ldc2_w 39
    //   322: lcmp
    //   323: ifle -135 -> 188
    //   326: aload_0
    //   327: ldc_w 545
    //   330: iconst_2
    //   331: anewarray 4	java/lang/Object
    //   334: dup
    //   335: iconst_0
    //   336: aload 5
    //   338: aastore
    //   339: dup
    //   340: iconst_1
    //   341: iload 4
    //   343: iload_2
    //   344: isub
    //   345: invokestatic 543	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   348: aastore
    //   349: invokestatic 538	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   352: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   355: iconst_1
    //   356: ireturn
    //   357: astore 6
    //   359: aload_0
    //   360: getfield 92	com/android/server/NativeDaemonConnector:mCallbacks	Lcom/android/server/INativeDaemonConnectorCallbacks;
    //   363: aload_1
    //   364: getfield 524	android/os/Message:what	I
    //   367: invokeinterface 277 2 0
    //   372: ifeq +17 -> 389
    //   375: aload_0
    //   376: getfield 101	com/android/server/NativeDaemonConnector:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   379: ifnull +10 -> 389
    //   382: aload_0
    //   383: getfield 101	com/android/server/NativeDaemonConnector:mWakeLock	Landroid/os/PowerManager$WakeLock;
    //   386: invokevirtual 301	android/os/PowerManager$WakeLock:release	()V
    //   389: aload_0
    //   390: invokespecial 285	com/android/server/NativeDaemonConnector:uptimeMillisInt	()I
    //   393: istore 4
    //   395: iload_2
    //   396: iload_3
    //   397: if_icmple +42 -> 439
    //   400: iload_2
    //   401: iload_3
    //   402: isub
    //   403: i2l
    //   404: ldc2_w 39
    //   407: lcmp
    //   408: ifle +31 -> 439
    //   411: aload_0
    //   412: ldc_w 540
    //   415: iconst_2
    //   416: anewarray 4	java/lang/Object
    //   419: dup
    //   420: iconst_0
    //   421: aload 5
    //   423: aastore
    //   424: dup
    //   425: iconst_1
    //   426: iload_2
    //   427: iload_3
    //   428: isub
    //   429: invokestatic 543	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   432: aastore
    //   433: invokestatic 538	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   436: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   439: iload 4
    //   441: iload_2
    //   442: if_icmple +44 -> 486
    //   445: iload 4
    //   447: iload_2
    //   448: isub
    //   449: i2l
    //   450: ldc2_w 39
    //   453: lcmp
    //   454: ifle +32 -> 486
    //   457: aload_0
    //   458: ldc_w 545
    //   461: iconst_2
    //   462: anewarray 4	java/lang/Object
    //   465: dup
    //   466: iconst_0
    //   467: aload 5
    //   469: aastore
    //   470: dup
    //   471: iconst_1
    //   472: iload 4
    //   474: iload_2
    //   475: isub
    //   476: invokestatic 543	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   479: aastore
    //   480: invokestatic 538	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   483: invokespecial 229	com/android/server/NativeDaemonConnector:loge	(Ljava/lang/String;)V
    //   486: aload 6
    //   488: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	489	0	this	NativeDaemonConnector
    //   0	489	1	paramMessage	android.os.Message
    //   13	463	2	i	int
    //   18	411	3	j	int
    //   95	381	4	k	int
    //   7	461	5	str	String
    //   190	28	6	localException	Exception
    //   357	130	6	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   19	61	190	java/lang/Exception
    //   19	61	357	finally
    //   192	228	357	finally
  }
  
  public void monitor()
  {
    Object localObject = this.mDaemonLock;
  }
  
  public void run()
  {
    this.mCallbackHandler = new Handler(this.mLooper, this);
    long l2 = 0L;
    try
    {
      long l3;
      long l1;
      for (;;)
      {
        listenToSocket();
      }
    }
    catch (Exception localException)
    {
      l3 = SystemClock.uptimeMillis();
      l1 = l2;
      if (l3 - l2 >= 5000L)
      {
        l1 = l2;
        if (l2 > 0L)
        {
          loge("Error in NativeDaemonConnector: " + localException);
          l1 = 0L;
        }
      }
      l2 = l1;
      if (l1 == 0L) {
        l2 = l3;
      }
      SystemClock.sleep(100L);
    }
  }
  
  public void setDebug(boolean paramBoolean)
  {
    this.mDebug = paramBoolean;
  }
  
  public void setWarnIfHeld(Object paramObject)
  {
    if (this.mWarnIfHeld == null) {}
    for (boolean bool = true;; bool = false)
    {
      Preconditions.checkState(bool);
      this.mWarnIfHeld = Preconditions.checkNotNull(paramObject);
      return;
    }
  }
  
  public void waitForCallbacks()
  {
    if (Thread.currentThread() == this.mLooper.getThread()) {
      throw new IllegalStateException("Must not call this method on callback thread");
    }
    final CountDownLatch localCountDownLatch = new CountDownLatch(1);
    this.mCallbackHandler.post(new Runnable()
    {
      public void run()
      {
        localCountDownLatch.countDown();
      }
    });
    try
    {
      localCountDownLatch.await();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      Slog.wtf(this.TAG, "Interrupted while waiting for unsolicited response handling", localInterruptedException);
    }
  }
  
  public static class Command
  {
    private ArrayList<Object> mArguments = Lists.newArrayList();
    private String mCmd;
    
    public Command(String paramString, Object... paramVarArgs)
    {
      this.mCmd = paramString;
      int i = 0;
      int j = paramVarArgs.length;
      while (i < j)
      {
        appendArg(paramVarArgs[i]);
        i += 1;
      }
    }
    
    public Command appendArg(Object paramObject)
    {
      this.mArguments.add(paramObject);
      return this;
    }
  }
  
  private static class NativeDaemonArgumentException
    extends NativeDaemonConnectorException
  {
    public NativeDaemonArgumentException(String paramString, NativeDaemonEvent paramNativeDaemonEvent)
    {
      super(paramNativeDaemonEvent);
    }
    
    public IllegalArgumentException rethrowAsParcelableException()
    {
      throw new IllegalArgumentException(getMessage(), this);
    }
  }
  
  private static class NativeDaemonFailureException
    extends NativeDaemonConnectorException
  {
    public NativeDaemonFailureException(String paramString, NativeDaemonEvent paramNativeDaemonEvent)
    {
      super(paramNativeDaemonEvent);
    }
  }
  
  private static class ResponseQueue
  {
    private int mMaxCount;
    private final LinkedList<PendingCmd> mPendingCmds = new LinkedList();
    
    ResponseQueue(int paramInt)
    {
      this.mMaxCount = paramInt;
    }
    
    public void add(int paramInt, NativeDaemonEvent paramNativeDaemonEvent)
    {
      PendingCmd localPendingCmd;
      synchronized (this.mPendingCmds)
      {
        Iterator localIterator = this.mPendingCmds.iterator();
        int i;
        do
        {
          if (!localIterator.hasNext()) {
            break;
          }
          localPendingCmd = (PendingCmd)localIterator.next();
          i = localPendingCmd.cmdNum;
        } while (i != paramInt);
        if (localPendingCmd == null)
        {
          try
          {
            while (this.mPendingCmds.size() >= this.mMaxCount)
            {
              Slog.e("NativeDaemonConnector.ResponseQueue", "more buffered than allowed: " + this.mPendingCmds.size() + " >= " + this.mMaxCount);
              localPendingCmd = (PendingCmd)this.mPendingCmds.remove();
              Slog.e("NativeDaemonConnector.ResponseQueue", "Removing request: " + localPendingCmd.logCmd + " (" + localPendingCmd.cmdNum + ")");
            }
          }
          finally {}
          throw paramNativeDaemonEvent;
          localPendingCmd = new PendingCmd(paramInt, null);
          this.mPendingCmds.add(localPendingCmd);
          localPendingCmd.availableResponseCount += 1;
          if (localPendingCmd.availableResponseCount == 0) {
            this.mPendingCmds.remove(localPendingCmd);
          }
          try
          {
            localPendingCmd.responses.put(paramNativeDaemonEvent);
            return;
          }
          catch (InterruptedException paramNativeDaemonEvent) {}
        }
      }
    }
    
    public void dump(FileDescriptor arg1, PrintWriter paramPrintWriter, String[] paramArrayOfString)
    {
      paramPrintWriter.println("Pending requests:");
      synchronized (this.mPendingCmds)
      {
        paramArrayOfString = this.mPendingCmds.iterator();
        if (paramArrayOfString.hasNext())
        {
          PendingCmd localPendingCmd = (PendingCmd)paramArrayOfString.next();
          paramPrintWriter.println("  Cmd " + localPendingCmd.cmdNum + " - " + localPendingCmd.logCmd);
        }
      }
    }
    
    /* Error */
    public NativeDaemonEvent remove(int paramInt, long paramLong, String paramString)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 25	com/android/server/NativeDaemonConnector$ResponseQueue:mPendingCmds	Ljava/util/LinkedList;
      //   4: astore 7
      //   6: aload 7
      //   8: monitorenter
      //   9: aload_0
      //   10: getfield 25	com/android/server/NativeDaemonConnector$ResponseQueue:mPendingCmds	Ljava/util/LinkedList;
      //   13: invokeinterface 38 1 0
      //   18: astore 8
      //   20: aload 8
      //   22: invokeinterface 44 1 0
      //   27: ifeq +155 -> 182
      //   30: aload 8
      //   32: invokeinterface 48 1 0
      //   37: checkcast 9	com/android/server/NativeDaemonConnector$ResponseQueue$PendingCmd
      //   40: astore 6
      //   42: aload 6
      //   44: getfield 51	com/android/server/NativeDaemonConnector$ResponseQueue$PendingCmd:cmdNum	I
      //   47: istore 5
      //   49: iload 5
      //   51: iload_1
      //   52: if_icmpne -32 -> 20
      //   55: aload 6
      //   57: ifnonnull +118 -> 175
      //   60: new 9	com/android/server/NativeDaemonConnector$ResponseQueue$PendingCmd
      //   63: dup
      //   64: iload_1
      //   65: aload 4
      //   67: invokespecial 97	com/android/server/NativeDaemonConnector$ResponseQueue$PendingCmd:<init>	(ILjava/lang/String;)V
      //   70: astore 4
      //   72: aload_0
      //   73: getfield 25	com/android/server/NativeDaemonConnector$ResponseQueue:mPendingCmds	Ljava/util/LinkedList;
      //   76: aload 4
      //   78: invokevirtual 100	java/util/LinkedList:add	(Ljava/lang/Object;)Z
      //   81: pop
      //   82: aload 4
      //   84: aload 4
      //   86: getfield 103	com/android/server/NativeDaemonConnector$ResponseQueue$PendingCmd:availableResponseCount	I
      //   89: iconst_1
      //   90: isub
      //   91: putfield 103	com/android/server/NativeDaemonConnector$ResponseQueue$PendingCmd:availableResponseCount	I
      //   94: aload 4
      //   96: getfield 103	com/android/server/NativeDaemonConnector$ResponseQueue$PendingCmd:availableResponseCount	I
      //   99: ifne +13 -> 112
      //   102: aload_0
      //   103: getfield 25	com/android/server/NativeDaemonConnector$ResponseQueue:mPendingCmds	Ljava/util/LinkedList;
      //   106: aload 4
      //   108: invokevirtual 105	java/util/LinkedList:remove	(Ljava/lang/Object;)Z
      //   111: pop
      //   112: aload 7
      //   114: monitorexit
      //   115: aconst_null
      //   116: astore 6
      //   118: aload 4
      //   120: getfield 109	com/android/server/NativeDaemonConnector$ResponseQueue$PendingCmd:responses	Ljava/util/concurrent/BlockingQueue;
      //   123: lload_2
      //   124: getstatic 136	java/util/concurrent/TimeUnit:MILLISECONDS	Ljava/util/concurrent/TimeUnit;
      //   127: invokeinterface 140 4 0
      //   132: checkcast 142	com/android/server/NativeDaemonEvent
      //   135: astore 4
      //   137: aload 4
      //   139: ifnonnull +11 -> 150
      //   142: ldc 57
      //   144: ldc -112
      //   146: invokestatic 81	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   149: pop
      //   150: aload 4
      //   152: areturn
      //   153: astore 4
      //   155: aload 7
      //   157: monitorexit
      //   158: aload 4
      //   160: athrow
      //   161: astore 4
      //   163: aload 6
      //   165: astore 4
      //   167: goto -30 -> 137
      //   170: astore 4
      //   172: goto -17 -> 155
      //   175: aload 6
      //   177: astore 4
      //   179: goto -97 -> 82
      //   182: aconst_null
      //   183: astore 6
      //   185: goto -130 -> 55
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	188	0	this	ResponseQueue
      //   0	188	1	paramInt	int
      //   0	188	2	paramLong	long
      //   0	188	4	paramString	String
      //   47	6	5	i	int
      //   40	144	6	localPendingCmd	PendingCmd
      //   4	152	7	localLinkedList	LinkedList
      //   18	13	8	localIterator	Iterator
      // Exception table:
      //   from	to	target	type
      //   9	20	153	finally
      //   20	49	153	finally
      //   72	82	153	finally
      //   82	112	153	finally
      //   118	137	161	java/lang/InterruptedException
      //   60	72	170	finally
    }
    
    private static class PendingCmd
    {
      public int availableResponseCount;
      public final int cmdNum;
      public final String logCmd;
      public BlockingQueue<NativeDaemonEvent> responses = new ArrayBlockingQueue(10);
      
      public PendingCmd(int paramInt, String paramString)
      {
        this.cmdNum = paramInt;
        this.logCmd = paramString;
      }
    }
  }
  
  public static class SensitiveArg
  {
    private final Object mArg;
    
    public SensitiveArg(Object paramObject)
    {
      this.mArg = paramObject;
    }
    
    public String toString()
    {
      return String.valueOf(this.mArg);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/NativeDaemonConnector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */