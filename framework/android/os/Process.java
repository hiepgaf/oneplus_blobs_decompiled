package android.os;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.LocalSocketAddress.Namespace;
import android.system.Os;
import android.system.OsConstants;
import android.util.Log;
import dalvik.system.VMRuntime;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Process
{
  public static final int AUDIOSERVER_UID = 1041;
  public static final int BLUETOOTH_UID = 1002;
  public static final int CAMERASERVER_UID = 1047;
  public static final int DRM_UID = 1019;
  public static final int FIRST_APPLICATION_UID = 10000;
  public static final int FIRST_ISOLATED_UID = 99000;
  public static final int FIRST_SHARED_APPLICATION_GID = 50000;
  public static final int LAST_APPLICATION_UID = 19999;
  public static final int LAST_ISOLATED_UID = 99999;
  public static final int LAST_SHARED_APPLICATION_GID = 59999;
  private static final String LOG_TAG = "Process";
  public static final int LOG_UID = 1007;
  public static final int MEDIA_RW_GID = 1023;
  public static final int MEDIA_UID = 1013;
  public static final int NFC_UID = 1027;
  public static final int PACKAGE_INFO_GID = 1032;
  public static final int PHONE_UID = 1001;
  public static final int PROC_CHAR = 2048;
  public static final int PROC_COMBINE = 256;
  public static final int PROC_OUT_FLOAT = 16384;
  public static final int PROC_OUT_LONG = 8192;
  public static final int PROC_OUT_STRING = 4096;
  public static final int PROC_PARENS = 512;
  public static final int PROC_QUOTES = 1024;
  public static final int PROC_SPACE_TERM = 32;
  public static final int PROC_TAB_TERM = 9;
  public static final int PROC_TERM_MASK = 255;
  public static final int PROC_ZERO_TERM = 0;
  public static final int ROOT_UID = 0;
  public static final int SCHED_BATCH = 3;
  public static final int SCHED_FIFO = 1;
  public static final int SCHED_IDLE = 5;
  public static final int SCHED_OTHER = 0;
  public static final int SCHED_RESET_ON_FORK = 1073741824;
  public static final int SCHED_RR = 2;
  public static final String SECONDARY_ZYGOTE_SOCKET = "zygote_secondary";
  public static final int SHARED_RELRO_UID = 1037;
  public static final int SHARED_USER_GID = 9997;
  public static final int SHELL_UID = 2000;
  public static final int SIGNAL_KILL = 9;
  public static final int SIGNAL_QUIT = 3;
  public static final int SIGNAL_USR1 = 10;
  public static final int SYSTEM_UID = 1000;
  public static final int THREAD_GROUP_AUDIO_APP = 3;
  public static final int THREAD_GROUP_AUDIO_SYS = 4;
  public static final int THREAD_GROUP_BG_NONINTERACTIVE = 0;
  public static final int THREAD_GROUP_DEFAULT = -1;
  private static final int THREAD_GROUP_FOREGROUND = 1;
  public static final int THREAD_GROUP_SYSTEM = 2;
  public static final int THREAD_GROUP_TOP_APP = 5;
  public static final int THREAD_PRIORITY_AUDIO = -16;
  public static final int THREAD_PRIORITY_BACKGROUND = 10;
  public static final int THREAD_PRIORITY_DEFAULT = 0;
  public static final int THREAD_PRIORITY_DISPLAY = -4;
  public static final int THREAD_PRIORITY_FOREGROUND = -2;
  public static final int THREAD_PRIORITY_LESS_FAVORABLE = 1;
  public static final int THREAD_PRIORITY_LOWEST = 19;
  public static final int THREAD_PRIORITY_MORE_FAVORABLE = -1;
  public static final int THREAD_PRIORITY_URGENT_AUDIO = -19;
  public static final int THREAD_PRIORITY_URGENT_DISPLAY = -8;
  public static final int VPN_UID = 1016;
  public static final int WIFI_UID = 1010;
  static final int ZYGOTE_RETRY_MILLIS = 500;
  public static final String ZYGOTE_SOCKET = "zygote";
  static ZygoteState primaryZygoteState;
  private static long sStartElapsedRealtime;
  private static long sStartUptimeMillis;
  static ZygoteState secondaryZygoteState;
  
  public static void establishZygoteConnectionForAbi(String paramString)
  {
    try
    {
      openZygoteSocketIfNeeded(paramString);
      return;
    }
    catch (ZygoteStartFailedEx localZygoteStartFailedEx)
    {
      throw new RuntimeException("Unable to connect to zygote for abi: " + paramString, localZygoteStartFailedEx);
    }
  }
  
  private static String getAbiList(BufferedWriter paramBufferedWriter, DataInputStream paramDataInputStream)
    throws IOException
  {
    paramBufferedWriter.write("1");
    paramBufferedWriter.newLine();
    paramBufferedWriter.write("--query-abi-list");
    paramBufferedWriter.newLine();
    paramBufferedWriter.flush();
    paramBufferedWriter = new byte[paramDataInputStream.readInt()];
    paramDataInputStream.readFully(paramBufferedWriter);
    return new String(paramBufferedWriter, StandardCharsets.US_ASCII);
  }
  
  public static final native long getElapsedCpuTime();
  
  public static final native int[] getExclusiveCores();
  
  public static final native long getFreeMemory();
  
  public static final native int getGidForName(String paramString);
  
  public static final int getParentPid(int paramInt)
  {
    long[] arrayOfLong = new long[1];
    arrayOfLong[0] = -1L;
    readProcLines("/proc/" + paramInt + "/status", new String[] { "PPid:" }, arrayOfLong);
    return (int)arrayOfLong[0];
  }
  
  public static final native int[] getPids(String paramString, int[] paramArrayOfInt);
  
  public static final native int[] getPidsForCommands(String[] paramArrayOfString);
  
  public static final native int getProcessGroup(int paramInt)
    throws IllegalArgumentException, SecurityException;
  
  public static final native long getPss(int paramInt);
  
  public static final long getStartElapsedRealtime()
  {
    return sStartElapsedRealtime;
  }
  
  public static final long getStartUptimeMillis()
  {
    return sStartUptimeMillis;
  }
  
  public static final int getThreadGroupLeader(int paramInt)
  {
    long[] arrayOfLong = new long[1];
    arrayOfLong[0] = -1L;
    readProcLines("/proc/" + paramInt + "/status", new String[] { "Tgid:" }, arrayOfLong);
    return (int)arrayOfLong[0];
  }
  
  public static final native int getThreadPriority(int paramInt)
    throws IllegalArgumentException;
  
  public static final native int getThreadScheduler(int paramInt)
    throws IllegalArgumentException;
  
  public static final native long getTotalMemory();
  
  public static final native int getUidForName(String paramString);
  
  public static final int getUidForPid(int paramInt)
  {
    long[] arrayOfLong = new long[1];
    arrayOfLong[0] = -1L;
    readProcLines("/proc/" + paramInt + "/status", new String[] { "Uid:" }, arrayOfLong);
    return (int)arrayOfLong[0];
  }
  
  public static final boolean is64Bit()
  {
    return VMRuntime.getRuntime().is64Bit();
  }
  
  public static boolean isApplicationUid(int paramInt)
  {
    return UserHandle.isApp(paramInt);
  }
  
  public static final boolean isIsolated()
  {
    return isIsolated(myUid());
  }
  
  public static final boolean isIsolated(int paramInt)
  {
    boolean bool2 = false;
    paramInt = UserHandle.getAppId(paramInt);
    boolean bool1 = bool2;
    if (paramInt >= 99000)
    {
      bool1 = bool2;
      if (paramInt <= 99999) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static final boolean isThreadInProcess(int paramInt1, int paramInt2)
  {
    StrictMode.ThreadPolicy localThreadPolicy = StrictMode.allowThreadDiskReads();
    try
    {
      boolean bool = Os.access("/proc/" + paramInt1 + "/task/" + paramInt2, OsConstants.F_OK);
      if (bool)
      {
        StrictMode.setThreadPolicy(localThreadPolicy);
        return true;
      }
      StrictMode.setThreadPolicy(localThreadPolicy);
      return false;
    }
    catch (Exception localException)
    {
      localException = localException;
      StrictMode.setThreadPolicy(localThreadPolicy);
      return false;
    }
    finally
    {
      localObject = finally;
      StrictMode.setThreadPolicy(localThreadPolicy);
      throw ((Throwable)localObject);
    }
  }
  
  public static final void killProcess(int paramInt)
  {
    sendSignal(paramInt, 9);
  }
  
  public static final native int killProcessGroup(int paramInt1, int paramInt2);
  
  public static final void killProcessQuiet(int paramInt)
  {
    sendSignalQuiet(paramInt, 9);
  }
  
  public static final int myPid()
  {
    return Os.getpid();
  }
  
  public static final int myPpid()
  {
    return Os.getppid();
  }
  
  public static final int myTid()
  {
    return Os.gettid();
  }
  
  public static final int myUid()
  {
    return Os.getuid();
  }
  
  public static UserHandle myUserHandle()
  {
    return UserHandle.of(UserHandle.getUserId(myUid()));
  }
  
  private static ZygoteState openZygoteSocketIfNeeded(String paramString)
    throws ZygoteStartFailedEx
  {
    if ((primaryZygoteState == null) || (primaryZygoteState.isClosed())) {}
    try
    {
      primaryZygoteState = ZygoteState.connect("zygote");
      if (primaryZygoteState.matches(paramString)) {
        return primaryZygoteState;
      }
    }
    catch (IOException paramString)
    {
      throw new ZygoteStartFailedEx("Error connecting to primary zygote", paramString);
    }
    if ((secondaryZygoteState == null) || (secondaryZygoteState.isClosed())) {}
    try
    {
      secondaryZygoteState = ZygoteState.connect("zygote_secondary");
      if (secondaryZygoteState.matches(paramString)) {
        return secondaryZygoteState;
      }
    }
    catch (IOException paramString)
    {
      throw new ZygoteStartFailedEx("Error connecting to secondary zygote", paramString);
    }
    throw new ZygoteStartFailedEx("Unsupported zygote ABI: " + paramString);
  }
  
  public static final native boolean parseProcLine(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int[] paramArrayOfInt, String[] paramArrayOfString, long[] paramArrayOfLong, float[] paramArrayOfFloat);
  
  public static final native boolean readProcFile(String paramString, int[] paramArrayOfInt, String[] paramArrayOfString, long[] paramArrayOfLong, float[] paramArrayOfFloat);
  
  public static final native void readProcLines(String paramString, String[] paramArrayOfString, long[] paramArrayOfLong);
  
  public static final native void removeAllProcessGroups();
  
  public static final native void sendSignal(int paramInt1, int paramInt2);
  
  public static final native void sendSignalQuiet(int paramInt1, int paramInt2);
  
  public static final native void setArgV0(String paramString);
  
  public static final native void setCanSelfBackground(boolean paramBoolean);
  
  public static final native int setGid(int paramInt);
  
  public static final native void setProcessGroup(int paramInt1, int paramInt2)
    throws IllegalArgumentException, SecurityException;
  
  public static final void setStartTimes(long paramLong1, long paramLong2)
  {
    sStartElapsedRealtime = paramLong1;
    sStartUptimeMillis = paramLong2;
  }
  
  public static final native boolean setSwappiness(int paramInt, boolean paramBoolean);
  
  public static final native void setThreadGroup(int paramInt1, int paramInt2)
    throws IllegalArgumentException, SecurityException;
  
  public static final native void setThreadPriority(int paramInt)
    throws IllegalArgumentException, SecurityException;
  
  public static final native void setThreadPriority(int paramInt1, int paramInt2)
    throws IllegalArgumentException, SecurityException;
  
  public static final native void setThreadScheduler(int paramInt1, int paramInt2, int paramInt3)
    throws IllegalArgumentException;
  
  public static final native int setUid(int paramInt);
  
  public static final ProcessStartResult start(String paramString1, String paramString2, int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, int paramInt5, String paramString3, String paramString4, String paramString5, String paramString6, String[] paramArrayOfString)
  {
    try
    {
      paramString1 = startViaZygote(paramString1, paramString2, paramInt1, paramInt2, paramArrayOfInt, paramInt3, paramInt4, paramInt5, paramString3, paramString4, paramString5, paramString6, paramArrayOfString);
      return paramString1;
    }
    catch (ZygoteStartFailedEx paramString1)
    {
      Log.e("Process", "Starting VM process through Zygote failed");
      throw new RuntimeException("Starting VM process through Zygote failed", paramString1);
    }
  }
  
  private static ProcessStartResult startViaZygote(String paramString1, String paramString2, int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3, int paramInt4, int paramInt5, String paramString3, String paramString4, String paramString5, String paramString6, String[] paramArrayOfString)
    throws ZygoteStartFailedEx
  {
    ArrayList localArrayList;
    StringBuilder localStringBuilder;
    for (;;)
    {
      try
      {
        localArrayList = new ArrayList();
        localArrayList.add("--runtime-args");
        localArrayList.add("--setuid=" + paramInt1);
        localArrayList.add("--setgid=" + paramInt2);
        if ((paramInt3 & 0x10) != 0) {
          localArrayList.add("--enable-jni-logging");
        }
        if ((paramInt3 & 0x8) != 0) {
          localArrayList.add("--enable-safemode");
        }
        if ((paramInt3 & 0x1) != 0) {
          localArrayList.add("--enable-debugger");
        }
        if ((paramInt3 & 0x2) != 0) {
          localArrayList.add("--enable-checkjni");
        }
        if ((paramInt3 & 0x20) != 0) {
          localArrayList.add("--generate-debug-info");
        }
        if ((paramInt3 & 0x40) != 0) {
          localArrayList.add("--always-jit");
        }
        if ((paramInt3 & 0x80) != 0) {
          localArrayList.add("--native-debuggable");
        }
        if ((paramInt3 & 0x4) != 0) {
          localArrayList.add("--enable-assert");
        }
        if (paramInt4 == 1)
        {
          localArrayList.add("--mount-external-default");
          localArrayList.add("--target-sdk-version=" + paramInt5);
          if ((paramArrayOfInt == null) || (paramArrayOfInt.length <= 0)) {
            break label371;
          }
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("--setgroups=");
          paramInt2 = paramArrayOfInt.length;
          paramInt1 = 0;
          if (paramInt1 >= paramInt2) {
            break;
          }
          if (paramInt1 != 0) {
            localStringBuilder.append(',');
          }
          localStringBuilder.append(paramArrayOfInt[paramInt1]);
          paramInt1 += 1;
          continue;
        }
        if (paramInt4 == 2)
        {
          localArrayList.add("--mount-external-read");
          continue;
        }
        if (paramInt4 != 3) {
          continue;
        }
      }
      finally {}
      localArrayList.add("--mount-external-write");
    }
    localArrayList.add(localStringBuilder.toString());
    label371:
    if (paramString2 != null) {
      localArrayList.add("--nice-name=" + paramString2);
    }
    if (paramString3 != null) {
      localArrayList.add("--seinfo=" + paramString3);
    }
    if (paramString5 != null) {
      localArrayList.add("--instruction-set=" + paramString5);
    }
    if (paramString6 != null) {
      localArrayList.add("--app-data-dir=" + paramString6);
    }
    localArrayList.add(paramString1);
    if (paramArrayOfString != null)
    {
      paramInt1 = 0;
      paramInt2 = paramArrayOfString.length;
      while (paramInt1 < paramInt2)
      {
        localArrayList.add(paramArrayOfString[paramInt1]);
        paramInt1 += 1;
      }
    }
    paramString1 = zygoteSendArgsAndGetResult(openZygoteSocketIfNeeded(paramString4), localArrayList);
    return paramString1;
  }
  
  @Deprecated
  public static final boolean supportsProcesses()
  {
    return true;
  }
  
  private static ProcessStartResult zygoteSendArgsAndGetResult(ZygoteState paramZygoteState, ArrayList<String> paramArrayList)
    throws ZygoteStartFailedEx
  {
    int j;
    for (;;)
    {
      try
      {
        j = paramArrayList.size();
        i = 0;
        if (i >= j) {
          break;
        }
        if (((String)paramArrayList.get(i)).indexOf('\n') >= 0) {
          throw new ZygoteStartFailedEx("embedded newlines not allowed");
        }
      }
      catch (IOException paramArrayList)
      {
        paramZygoteState.close();
        throw new ZygoteStartFailedEx(paramArrayList);
      }
      i += 1;
    }
    BufferedWriter localBufferedWriter = paramZygoteState.writer;
    DataInputStream localDataInputStream = paramZygoteState.inputStream;
    localBufferedWriter.write(Integer.toString(paramArrayList.size()));
    localBufferedWriter.newLine();
    int i = 0;
    while (i < j)
    {
      localBufferedWriter.write((String)paramArrayList.get(i));
      localBufferedWriter.newLine();
      i += 1;
    }
    localBufferedWriter.flush();
    paramArrayList = new ProcessStartResult();
    paramArrayList.pid = localDataInputStream.readInt();
    paramArrayList.usingWrapper = localDataInputStream.readBoolean();
    if (paramArrayList.pid < 0) {
      throw new ZygoteStartFailedEx("fork() failed");
    }
    return paramArrayList;
  }
  
  public static final class ProcessStartResult
  {
    public int pid;
    public boolean usingWrapper;
  }
  
  public static class ZygoteState
  {
    final List<String> abiList;
    final DataInputStream inputStream;
    boolean mClosed;
    final LocalSocket socket;
    final BufferedWriter writer;
    
    private ZygoteState(LocalSocket paramLocalSocket, DataInputStream paramDataInputStream, BufferedWriter paramBufferedWriter, List<String> paramList)
    {
      this.socket = paramLocalSocket;
      this.inputStream = paramDataInputStream;
      this.writer = paramBufferedWriter;
      this.abiList = paramList;
    }
    
    public static ZygoteState connect(String paramString)
      throws IOException
    {
      localLocalSocket = new LocalSocket();
      try
      {
        localLocalSocket.connect(new LocalSocketAddress(paramString, LocalSocketAddress.Namespace.RESERVED));
        paramString = new DataInputStream(localLocalSocket.getInputStream());
        try
        {
          BufferedWriter localBufferedWriter;
          String str;
          localLocalSocket.close();
          throw paramString;
        }
        catch (IOException localIOException)
        {
          for (;;) {}
        }
      }
      catch (IOException paramString)
      {
        try
        {
          localBufferedWriter = new BufferedWriter(new OutputStreamWriter(localLocalSocket.getOutputStream()), 256);
          str = Process.-wrap0(localBufferedWriter, paramString);
          Log.i("Zygote", "Process: zygote socket opened, supported ABIS: " + str);
          return new ZygoteState(localLocalSocket, paramString, localBufferedWriter, Arrays.asList(str.split(",")));
        }
        catch (IOException paramString)
        {
          for (;;) {}
        }
        paramString = paramString;
      }
    }
    
    public void close()
    {
      try
      {
        this.socket.close();
        this.mClosed = true;
        return;
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          Log.e("Process", "I/O exception on routine close", localIOException);
        }
      }
    }
    
    boolean isClosed()
    {
      return this.mClosed;
    }
    
    boolean matches(String paramString)
    {
      return this.abiList.contains(paramString);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/os/Process.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */