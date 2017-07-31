package com.android.server.am;

import android.app.ApplicationErrorReport.CrashInfo;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructTimeval;
import android.util.Slog;
import android.util.SparseArray;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.InterruptedIOException;

final class NativeCrashListener
  extends Thread
{
  static final boolean DEBUG = false;
  static final String DEBUGGERD_SOCKET_PATH = "/data/system/ndebugsocket";
  static final boolean MORE_DEBUG = false;
  static final long SOCKET_TIMEOUT_MILLIS = 10000L;
  static final String TAG = "NativeCrashListener";
  final ActivityManagerService mAm;
  
  NativeCrashListener(ActivityManagerService paramActivityManagerService)
  {
    this.mAm = paramActivityManagerService;
  }
  
  static int readExactly(FileDescriptor paramFileDescriptor, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws ErrnoException, InterruptedIOException
  {
    int j = 0;
    int i = paramInt2;
    paramInt2 = j;
    while (i > 0)
    {
      j = Os.read(paramFileDescriptor, paramArrayOfByte, paramInt1 + paramInt2, i);
      if (j <= 0) {
        return -1;
      }
      i -= j;
      paramInt2 += j;
    }
    return paramInt2;
  }
  
  static int unpackInt(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0xFF) << 24 | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  void consumeNativeCrashData(FileDescriptor arg1)
  {
    byte[] arrayOfByte = new byte['က'];
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream(4096);
    int j;
    int i;
    ProcessRecord localProcessRecord;
    try
    {
      ??? = StructTimeval.fromMillis(10000L);
      Os.setsockoptTimeval(???, OsConstants.SOL_SOCKET, OsConstants.SO_RCVTIMEO, (StructTimeval)???);
      Os.setsockoptTimeval(???, OsConstants.SOL_SOCKET, OsConstants.SO_SNDTIMEO, (StructTimeval)???);
      if (readExactly(???, arrayOfByte, 0, 8) != 8)
      {
        Slog.e("NativeCrashListener", "Unable to read from debuggerd");
        return;
      }
      j = unpackInt(arrayOfByte, 0);
      i = unpackInt(arrayOfByte, 4);
      if (j <= 0) {
        break label293;
      }
      synchronized (this.mAm.mPidsSelfLocked)
      {
        localProcessRecord = (ProcessRecord)this.mAm.mPidsSelfLocked.get(j);
        if (localProcessRecord == null) {
          break label267;
        }
        if (!localProcessRecord.persistent) {
          break label166;
        }
        return;
      }
      localByteArrayOutputStream.write(arrayOfByte, 0, j);
    }
    catch (Exception ???)
    {
      Slog.e("NativeCrashListener", "Exception dealing with report", ???);
      return;
    }
    for (;;)
    {
      label166:
      j = Os.read(???, arrayOfByte, 0, arrayOfByte.length);
      if (j > 0)
      {
        if (arrayOfByte[(j - 1)] != 0) {
          break;
        }
        localByteArrayOutputStream.write(arrayOfByte, 0, j - 1);
      }
      label267:
      label293:
      while (j <= 0)
      {
        synchronized (this.mAm)
        {
          ActivityManagerService.boostPriorityForLockedSection();
          localProcessRecord.crashing = true;
          localProcessRecord.forceCrashReport = true;
          ActivityManagerService.resetPriorityAfterLockedSection();
          new NativeCrashReporter(localProcessRecord, i, new String(localByteArrayOutputStream.toByteArray(), "UTF-8")).start();
          return;
        }
        Slog.w("NativeCrashListener", "Couldn't find ProcessRecord for pid " + j);
        return;
        Slog.e("NativeCrashListener", "Bogus pid!");
        return;
      }
    }
  }
  
  /* Error */
  public void run()
  {
    // Byte code:
    //   0: iconst_1
    //   1: newarray <illegal type>
    //   3: astore 4
    //   5: new 168	java/io/File
    //   8: dup
    //   9: ldc 14
    //   11: invokespecial 171	java/io/File:<init>	(Ljava/lang/String;)V
    //   14: astore_1
    //   15: aload_1
    //   16: invokevirtual 175	java/io/File:exists	()Z
    //   19: ifeq +8 -> 27
    //   22: aload_1
    //   23: invokevirtual 178	java/io/File:delete	()Z
    //   26: pop
    //   27: getstatic 181	android/system/OsConstants:AF_UNIX	I
    //   30: getstatic 184	android/system/OsConstants:SOCK_STREAM	I
    //   33: iconst_0
    //   34: invokestatic 188	android/system/Os:socket	(III)Ljava/io/FileDescriptor;
    //   37: astore 5
    //   39: aload 5
    //   41: ldc 14
    //   43: invokestatic 194	android/system/UnixSocketAddress:createFileSystem	(Ljava/lang/String;)Landroid/system/UnixSocketAddress;
    //   46: invokestatic 198	android/system/Os:bind	(Ljava/io/FileDescriptor;Ljava/net/SocketAddress;)V
    //   49: aload 5
    //   51: iconst_1
    //   52: invokestatic 202	android/system/Os:listen	(Ljava/io/FileDescriptor;I)V
    //   55: aconst_null
    //   56: astore_2
    //   57: aconst_null
    //   58: astore_1
    //   59: aload 5
    //   61: aconst_null
    //   62: invokestatic 206	android/system/Os:accept	(Ljava/io/FileDescriptor;Ljava/net/InetSocketAddress;)Ljava/io/FileDescriptor;
    //   65: astore_3
    //   66: aload_3
    //   67: ifnull +32 -> 99
    //   70: aload_3
    //   71: astore_1
    //   72: aload_3
    //   73: astore_2
    //   74: aload_3
    //   75: getstatic 67	android/system/OsConstants:SOL_SOCKET	I
    //   78: getstatic 209	android/system/OsConstants:SO_PEERCRED	I
    //   81: invokestatic 213	android/system/Os:getsockoptUcred	(Ljava/io/FileDescriptor;II)Landroid/system/StructUcred;
    //   84: getfield 218	android/system/StructUcred:uid	I
    //   87: ifne +12 -> 99
    //   90: aload_3
    //   91: astore_1
    //   92: aload_3
    //   93: astore_2
    //   94: aload_0
    //   95: aload_3
    //   96: invokevirtual 220	com/android/server/am/NativeCrashListener:consumeNativeCrashData	(Ljava/io/FileDescriptor;)V
    //   99: aload_3
    //   100: ifnull -45 -> 55
    //   103: aload_3
    //   104: aload 4
    //   106: iconst_0
    //   107: iconst_1
    //   108: invokestatic 222	android/system/Os:write	(Ljava/io/FileDescriptor;[BII)I
    //   111: pop
    //   112: aload_3
    //   113: invokestatic 225	android/system/Os:close	(Ljava/io/FileDescriptor;)V
    //   116: goto -61 -> 55
    //   119: astore_1
    //   120: goto -65 -> 55
    //   123: astore_1
    //   124: goto -12 -> 112
    //   127: astore_3
    //   128: aload_1
    //   129: astore_2
    //   130: ldc 22
    //   132: ldc -29
    //   134: aload_3
    //   135: invokestatic 229	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   138: pop
    //   139: aload_1
    //   140: ifnull -85 -> 55
    //   143: aload_1
    //   144: aload 4
    //   146: iconst_0
    //   147: iconst_1
    //   148: invokestatic 222	android/system/Os:write	(Ljava/io/FileDescriptor;[BII)I
    //   151: pop
    //   152: aload_1
    //   153: invokestatic 225	android/system/Os:close	(Ljava/io/FileDescriptor;)V
    //   156: goto -101 -> 55
    //   159: astore_1
    //   160: goto -105 -> 55
    //   163: astore_2
    //   164: goto -12 -> 152
    //   167: astore_1
    //   168: aload_2
    //   169: ifnull +16 -> 185
    //   172: aload_2
    //   173: aload 4
    //   175: iconst_0
    //   176: iconst_1
    //   177: invokestatic 222	android/system/Os:write	(Ljava/io/FileDescriptor;[BII)I
    //   180: pop
    //   181: aload_2
    //   182: invokestatic 225	android/system/Os:close	(Ljava/io/FileDescriptor;)V
    //   185: aload_1
    //   186: athrow
    //   187: astore_1
    //   188: ldc 22
    //   190: ldc -25
    //   192: aload_1
    //   193: invokestatic 111	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   196: pop
    //   197: return
    //   198: astore_3
    //   199: goto -18 -> 181
    //   202: astore_2
    //   203: goto -18 -> 185
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	206	0	this	NativeCrashListener
    //   14	78	1	localObject1	Object
    //   119	1	1	localErrnoException1	ErrnoException
    //   123	30	1	localException1	Exception
    //   159	1	1	localErrnoException2	ErrnoException
    //   167	19	1	localObject2	Object
    //   187	6	1	localException2	Exception
    //   56	74	2	localObject3	Object
    //   163	19	2	localException3	Exception
    //   202	1	2	localErrnoException3	ErrnoException
    //   65	48	3	localFileDescriptor1	FileDescriptor
    //   127	8	3	localException4	Exception
    //   198	1	3	localException5	Exception
    //   3	171	4	arrayOfByte	byte[]
    //   37	23	5	localFileDescriptor2	FileDescriptor
    // Exception table:
    //   from	to	target	type
    //   112	116	119	android/system/ErrnoException
    //   103	112	123	java/lang/Exception
    //   59	66	127	java/lang/Exception
    //   74	90	127	java/lang/Exception
    //   94	99	127	java/lang/Exception
    //   152	156	159	android/system/ErrnoException
    //   143	152	163	java/lang/Exception
    //   59	66	167	finally
    //   74	90	167	finally
    //   94	99	167	finally
    //   130	139	167	finally
    //   27	55	187	java/lang/Exception
    //   112	116	187	java/lang/Exception
    //   152	156	187	java/lang/Exception
    //   181	185	187	java/lang/Exception
    //   185	187	187	java/lang/Exception
    //   172	181	198	java/lang/Exception
    //   181	185	202	android/system/ErrnoException
  }
  
  class NativeCrashReporter
    extends Thread
  {
    ProcessRecord mApp;
    String mCrashReport;
    int mSignal;
    
    NativeCrashReporter(ProcessRecord paramProcessRecord, int paramInt, String paramString)
    {
      super();
      this.mApp = paramProcessRecord;
      this.mSignal = paramInt;
      this.mCrashReport = paramString;
    }
    
    public void run()
    {
      try
      {
        ApplicationErrorReport.CrashInfo localCrashInfo = new ApplicationErrorReport.CrashInfo();
        localCrashInfo.exceptionClassName = "Native crash";
        localCrashInfo.exceptionMessage = Os.strsignal(this.mSignal);
        localCrashInfo.throwFileName = "unknown";
        localCrashInfo.throwClassName = "unknown";
        localCrashInfo.throwMethodName = "unknown";
        localCrashInfo.stackTrace = this.mCrashReport;
        NativeCrashListener.this.mAm.handleApplicationCrashInner("native_crash", this.mApp, this.mApp.processName, localCrashInfo);
        return;
      }
      catch (Exception localException)
      {
        Slog.e("NativeCrashListener", "Unable to report native crash", localException);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/am/NativeCrashListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */