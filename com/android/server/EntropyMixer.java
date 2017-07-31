package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Slog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EntropyMixer
  extends Binder
{
  private static final int ENTROPY_WHAT = 1;
  private static final int ENTROPY_WRITE_PERIOD = 10800000;
  private static final long START_NANOTIME = System.nanoTime();
  private static final long START_TIME = ;
  private static final String TAG = "EntropyMixer";
  private final String entropyFile;
  private final String hwRandomDevice;
  private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      EntropyMixer.-wrap2(EntropyMixer.this);
    }
  };
  private final Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      if (paramAnonymousMessage.what != 1)
      {
        Slog.e("EntropyMixer", "Will not process invalid message");
        return;
      }
      EntropyMixer.-wrap0(EntropyMixer.this);
      EntropyMixer.-wrap2(EntropyMixer.this);
      EntropyMixer.-wrap1(EntropyMixer.this);
    }
  };
  private final String randomDevice;
  
  public EntropyMixer(Context paramContext)
  {
    this(paramContext, getSystemDir() + "/entropy.dat", "/dev/urandom", "/dev/hw_random");
  }
  
  public EntropyMixer(Context paramContext, String paramString1, String paramString2, String paramString3)
  {
    if (paramString2 == null) {
      throw new NullPointerException("randomDevice");
    }
    if (paramString3 == null) {
      throw new NullPointerException("hwRandomDevice");
    }
    if (paramString1 == null) {
      throw new NullPointerException("entropyFile");
    }
    this.randomDevice = paramString2;
    this.hwRandomDevice = paramString3;
    this.entropyFile = paramString1;
    loadInitialEntropy();
    addDeviceSpecificEntropy();
    addHwRandomEntropy();
    writeEntropy();
    scheduleEntropyWriter();
    paramString1 = new IntentFilter("android.intent.action.ACTION_SHUTDOWN");
    paramString1.addAction("android.intent.action.ACTION_POWER_CONNECTED");
    paramString1.addAction("android.intent.action.REBOOT");
    paramContext.registerReceiver(this.mBroadcastReceiver, paramString1);
  }
  
  /* Error */
  private void addDeviceSpecificEntropy()
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aconst_null
    //   3: astore_3
    //   4: new 132	java/io/PrintWriter
    //   7: dup
    //   8: new 134	java/io/FileOutputStream
    //   11: dup
    //   12: aload_0
    //   13: getfield 100	com/android/server/EntropyMixer:randomDevice	Ljava/lang/String;
    //   16: invokespecial 135	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   19: invokespecial 138	java/io/PrintWriter:<init>	(Ljava/io/OutputStream;)V
    //   22: astore_2
    //   23: aload_2
    //   24: ldc -116
    //   26: invokevirtual 143	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   29: aload_2
    //   30: ldc -111
    //   32: invokevirtual 143	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   35: aload_2
    //   36: getstatic 51	com/android/server/EntropyMixer:START_TIME	J
    //   39: invokevirtual 148	java/io/PrintWriter:println	(J)V
    //   42: aload_2
    //   43: getstatic 56	com/android/server/EntropyMixer:START_NANOTIME	J
    //   46: invokevirtual 148	java/io/PrintWriter:println	(J)V
    //   49: aload_2
    //   50: ldc -106
    //   52: invokestatic 156	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   55: invokevirtual 143	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   58: aload_2
    //   59: ldc -98
    //   61: invokestatic 156	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   64: invokevirtual 143	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   67: aload_2
    //   68: ldc -96
    //   70: invokestatic 156	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   73: invokevirtual 143	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   76: aload_2
    //   77: ldc -94
    //   79: invokestatic 156	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   82: invokevirtual 143	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   85: aload_2
    //   86: ldc -92
    //   88: invokestatic 156	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   91: invokevirtual 143	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   94: aload_2
    //   95: ldc -90
    //   97: invokestatic 156	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   100: invokevirtual 143	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   103: aload_2
    //   104: ldc -88
    //   106: invokestatic 156	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   109: invokevirtual 143	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   112: aload_2
    //   113: ldc -86
    //   115: invokestatic 156	android/os/SystemProperties:get	(Ljava/lang/String;)Ljava/lang/String;
    //   118: invokevirtual 143	java/io/PrintWriter:println	(Ljava/lang/String;)V
    //   121: aload_2
    //   122: new 172	java/lang/Object
    //   125: dup
    //   126: invokespecial 173	java/lang/Object:<init>	()V
    //   129: invokevirtual 177	java/lang/Object:hashCode	()I
    //   132: invokevirtual 180	java/io/PrintWriter:println	(I)V
    //   135: aload_2
    //   136: invokestatic 49	java/lang/System:currentTimeMillis	()J
    //   139: invokevirtual 148	java/io/PrintWriter:println	(J)V
    //   142: aload_2
    //   143: invokestatic 54	java/lang/System:nanoTime	()J
    //   146: invokevirtual 148	java/io/PrintWriter:println	(J)V
    //   149: aload_2
    //   150: ifnull +7 -> 157
    //   153: aload_2
    //   154: invokevirtual 183	java/io/PrintWriter:close	()V
    //   157: return
    //   158: astore_1
    //   159: aload_3
    //   160: astore_2
    //   161: aload_1
    //   162: astore_3
    //   163: aload_2
    //   164: astore_1
    //   165: ldc 20
    //   167: ldc -71
    //   169: aload_3
    //   170: invokestatic 191	android/util/Slog:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   173: pop
    //   174: aload_2
    //   175: ifnull -18 -> 157
    //   178: aload_2
    //   179: invokevirtual 183	java/io/PrintWriter:close	()V
    //   182: return
    //   183: astore_2
    //   184: aload_1
    //   185: ifnull +7 -> 192
    //   188: aload_1
    //   189: invokevirtual 183	java/io/PrintWriter:close	()V
    //   192: aload_2
    //   193: athrow
    //   194: astore_3
    //   195: aload_2
    //   196: astore_1
    //   197: aload_3
    //   198: astore_2
    //   199: goto -15 -> 184
    //   202: astore_3
    //   203: goto -40 -> 163
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	206	0	this	EntropyMixer
    //   1	1	1	localObject1	Object
    //   158	4	1	localIOException1	IOException
    //   164	33	1	localObject2	Object
    //   22	157	2	localObject3	Object
    //   183	13	2	localObject4	Object
    //   198	1	2	localObject5	Object
    //   3	167	3	localIOException2	IOException
    //   194	4	3	localObject6	Object
    //   202	1	3	localIOException3	IOException
    // Exception table:
    //   from	to	target	type
    //   4	23	158	java/io/IOException
    //   4	23	183	finally
    //   165	174	183	finally
    //   23	149	194	finally
    //   23	149	202	java/io/IOException
  }
  
  private void addHwRandomEntropy()
  {
    try
    {
      RandomBlock.fromFile(this.hwRandomDevice).toFile(this.randomDevice, false);
      Slog.i("EntropyMixer", "Added HW RNG output to entropy pool");
      return;
    }
    catch (IOException localIOException)
    {
      Slog.w("EntropyMixer", "Failed to add HW RNG output to entropy pool", localIOException);
      return;
    }
    catch (FileNotFoundException localFileNotFoundException) {}
  }
  
  private static String getSystemDir()
  {
    File localFile = new File(Environment.getDataDirectory(), "system");
    localFile.mkdirs();
    return localFile.toString();
  }
  
  private void loadInitialEntropy()
  {
    try
    {
      RandomBlock.fromFile(this.entropyFile).toFile(this.randomDevice, false);
      return;
    }
    catch (IOException localIOException)
    {
      Slog.w("EntropyMixer", "Failure loading existing entropy file", localIOException);
      return;
    }
    catch (FileNotFoundException localFileNotFoundException)
    {
      Slog.w("EntropyMixer", "No existing entropy file -- first boot?");
    }
  }
  
  private void scheduleEntropyWriter()
  {
    this.mHandler.removeMessages(1);
    this.mHandler.sendEmptyMessageDelayed(1, 10800000L);
  }
  
  private void writeEntropy()
  {
    try
    {
      Slog.i("EntropyMixer", "Writing entropy...");
      RandomBlock.fromFile(this.randomDevice).toFile(this.entropyFile, true);
      return;
    }
    catch (IOException localIOException)
    {
      Slog.w("EntropyMixer", "Unable to write entropy", localIOException);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/EntropyMixer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */