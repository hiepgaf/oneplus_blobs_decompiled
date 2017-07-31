package com.android.server.usb;

import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.content.res.Resources;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.net.LocalSocketAddress.Namespace;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Slog;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.FgThread;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UsbDebuggingManager
{
  private static final boolean DEBUG = false;
  private static final String TAG = "UsbDebuggingManager";
  private final String ADBD_SOCKET = "adbd";
  private final String ADB_DIRECTORY = "misc/adb";
  private final String ADB_KEYS_FILE = "adb_keys";
  private final int BUFFER_SIZE = 4096;
  private boolean mAdbEnabled = false;
  private final Context mContext;
  private String mFingerprints;
  private final Handler mHandler = new UsbDebuggingHandler(FgThread.get().getLooper());
  private UsbDebuggingThread mThread;
  
  public UsbDebuggingManager(Context paramContext)
  {
    this.mContext = paramContext;
  }
  
  private Intent createConfirmationIntent(ComponentName paramComponentName, String paramString1, String paramString2)
  {
    Intent localIntent = new Intent();
    localIntent.setClassName(paramComponentName.getPackageName(), paramComponentName.getClassName());
    localIntent.putExtra("key", paramString1);
    localIntent.putExtra("fingerprints", paramString2);
    return localIntent;
  }
  
  private void deleteKeyFile()
  {
    File localFile = getUserKeyFile();
    if (localFile != null) {
      localFile.delete();
    }
  }
  
  /* Error */
  private String getFingerprints(String paramString)
  {
    // Byte code:
    //   0: new 150	java/lang/StringBuilder
    //   3: dup
    //   4: invokespecial 151	java/lang/StringBuilder:<init>	()V
    //   7: astore_3
    //   8: aload_1
    //   9: ifnonnull +6 -> 15
    //   12: ldc -103
    //   14: areturn
    //   15: ldc -101
    //   17: invokestatic 161	java/security/MessageDigest:getInstance	(Ljava/lang/String;)Ljava/security/MessageDigest;
    //   20: astore 4
    //   22: aload_1
    //   23: ldc -93
    //   25: invokevirtual 169	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   28: iconst_0
    //   29: aaload
    //   30: invokevirtual 173	java/lang/String:getBytes	()[B
    //   33: astore_1
    //   34: aload 4
    //   36: aload_1
    //   37: iconst_0
    //   38: invokestatic 179	android/util/Base64:decode	([BI)[B
    //   41: invokevirtual 183	java/security/MessageDigest:digest	([B)[B
    //   44: astore_1
    //   45: iconst_0
    //   46: istore_2
    //   47: iload_2
    //   48: aload_1
    //   49: arraylength
    //   50: if_icmpge +85 -> 135
    //   53: aload_3
    //   54: ldc -71
    //   56: aload_1
    //   57: iload_2
    //   58: baload
    //   59: iconst_4
    //   60: ishr
    //   61: bipush 15
    //   63: iand
    //   64: invokevirtual 189	java/lang/String:charAt	(I)C
    //   67: invokevirtual 193	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   70: pop
    //   71: aload_3
    //   72: ldc -71
    //   74: aload_1
    //   75: iload_2
    //   76: baload
    //   77: bipush 15
    //   79: iand
    //   80: invokevirtual 189	java/lang/String:charAt	(I)C
    //   83: invokevirtual 193	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   86: pop
    //   87: iload_2
    //   88: aload_1
    //   89: arraylength
    //   90: iconst_1
    //   91: isub
    //   92: if_icmpge +10 -> 102
    //   95: aload_3
    //   96: ldc -61
    //   98: invokevirtual 198	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   101: pop
    //   102: iload_2
    //   103: iconst_1
    //   104: iadd
    //   105: istore_2
    //   106: goto -59 -> 47
    //   109: astore_1
    //   110: ldc 17
    //   112: ldc -56
    //   114: aload_1
    //   115: invokestatic 206	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   118: pop
    //   119: ldc -103
    //   121: areturn
    //   122: astore_1
    //   123: ldc 17
    //   125: ldc -48
    //   127: aload_1
    //   128: invokestatic 206	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   131: pop
    //   132: ldc -103
    //   134: areturn
    //   135: aload_3
    //   136: invokevirtual 211	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   139: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	140	0	this	UsbDebuggingManager
    //   0	140	1	paramString	String
    //   46	60	2	i	int
    //   7	129	3	localStringBuilder	StringBuilder
    //   20	15	4	localMessageDigest	java.security.MessageDigest
    // Exception table:
    //   from	to	target	type
    //   15	22	109	java/lang/Exception
    //   34	45	122	java/lang/IllegalArgumentException
  }
  
  private File getUserKeyFile()
  {
    File localFile = new File(Environment.getDataDirectory(), "misc/adb");
    if (!localFile.exists())
    {
      Slog.e("UsbDebuggingManager", "ADB data directory does not exist");
      return null;
    }
    return new File(localFile, "adb_keys");
  }
  
  private void startConfirmation(String paramString1, String paramString2)
  {
    int i = ActivityManager.getCurrentUser();
    UserInfo localUserInfo = UserManager.get(this.mContext).getUserInfo(i);
    if (localUserInfo.isAdmin()) {}
    for (String str = Resources.getSystem().getString(17039458);; str = Resources.getSystem().getString(17039459))
    {
      ComponentName localComponentName = ComponentName.unflattenFromString(str);
      if ((!startConfirmationActivity(localComponentName, localUserInfo.getUserHandle(), paramString1, paramString2)) && (!startConfirmationService(localComponentName, localUserInfo.getUserHandle(), paramString1, paramString2))) {
        break;
      }
      return;
    }
    Slog.e("UsbDebuggingManager", "unable to start customAdbPublicKeyConfirmation[SecondaryUser]Component " + str + " as an Activity or a Service");
  }
  
  private boolean startConfirmationActivity(ComponentName paramComponentName, UserHandle paramUserHandle, String paramString1, String paramString2)
  {
    PackageManager localPackageManager = this.mContext.getPackageManager();
    paramString1 = createConfirmationIntent(paramComponentName, paramString1, paramString2);
    paramString1.addFlags(268435456);
    if (localPackageManager.resolveActivity(paramString1, 65536) != null) {
      try
      {
        this.mContext.startActivityAsUser(paramString1, paramUserHandle);
        return true;
      }
      catch (ActivityNotFoundException paramUserHandle)
      {
        Slog.e("UsbDebuggingManager", "unable to start adb whitelist activity: " + paramComponentName, paramUserHandle);
      }
    }
    return false;
  }
  
  private boolean startConfirmationService(ComponentName paramComponentName, UserHandle paramUserHandle, String paramString1, String paramString2)
  {
    paramString1 = createConfirmationIntent(paramComponentName, paramString1, paramString2);
    try
    {
      paramUserHandle = this.mContext.startServiceAsUser(paramString1, paramUserHandle);
      if (paramUserHandle != null) {
        return true;
      }
    }
    catch (SecurityException paramUserHandle)
    {
      Slog.e("UsbDebuggingManager", "unable to start adb whitelist service: " + paramComponentName, paramUserHandle);
    }
    return false;
  }
  
  private void writeKey(String paramString)
  {
    try
    {
      Object localObject = getUserKeyFile();
      if (localObject == null) {
        return;
      }
      if (!((File)localObject).exists())
      {
        ((File)localObject).createNewFile();
        FileUtils.setPermissions(((File)localObject).toString(), 416, -1, -1);
      }
      localObject = new FileOutputStream((File)localObject, true);
      ((FileOutputStream)localObject).write(paramString.getBytes());
      ((FileOutputStream)localObject).write(10);
      ((FileOutputStream)localObject).close();
      return;
    }
    catch (IOException paramString)
    {
      Slog.e("UsbDebuggingManager", "Error writing key:" + paramString);
    }
  }
  
  public void allowUsbDebugging(boolean paramBoolean, String paramString)
  {
    Message localMessage = this.mHandler.obtainMessage(3);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localMessage.arg1 = i;
      localMessage.obj = paramString;
      this.mHandler.sendMessage(localMessage);
      return;
    }
  }
  
  public void clearUsbDebuggingKeys()
  {
    this.mHandler.sendEmptyMessage(6);
  }
  
  public void denyUsbDebugging()
  {
    this.mHandler.sendEmptyMessage(4);
  }
  
  public void dump(IndentingPrintWriter paramIndentingPrintWriter)
  {
    boolean bool = false;
    paramIndentingPrintWriter.println("USB Debugging State:");
    StringBuilder localStringBuilder = new StringBuilder().append("  Connected to adbd: ");
    if (this.mThread != null) {
      bool = true;
    }
    paramIndentingPrintWriter.println(bool);
    paramIndentingPrintWriter.println("  Last key received: " + this.mFingerprints);
    paramIndentingPrintWriter.println("  User keys:");
    try
    {
      paramIndentingPrintWriter.println(FileUtils.readTextFile(new File("/data/misc/adb/adb_keys"), 0, null));
      paramIndentingPrintWriter.println("  System keys:");
    }
    catch (IOException localIOException1)
    {
      for (;;)
      {
        try
        {
          paramIndentingPrintWriter.println(FileUtils.readTextFile(new File("/adb_keys"), 0, null));
          return;
        }
        catch (IOException localIOException2)
        {
          paramIndentingPrintWriter.println("IOException: " + localIOException2);
        }
        localIOException1 = localIOException1;
        paramIndentingPrintWriter.println("IOException: " + localIOException1);
      }
    }
  }
  
  public void setAdbEnabled(boolean paramBoolean)
  {
    Handler localHandler = this.mHandler;
    if (paramBoolean) {}
    for (int i = 1;; i = 2)
    {
      localHandler.sendEmptyMessage(i);
      return;
    }
  }
  
  class UsbDebuggingHandler
    extends Handler
  {
    private static final int MESSAGE_ADB_ALLOW = 3;
    private static final int MESSAGE_ADB_CLEAR = 6;
    private static final int MESSAGE_ADB_CONFIRM = 5;
    private static final int MESSAGE_ADB_DENY = 4;
    private static final int MESSAGE_ADB_DISABLED = 2;
    private static final int MESSAGE_ADB_ENABLED = 1;
    
    public UsbDebuggingHandler(Looper paramLooper)
    {
      super();
    }
    
    public void handleMessage(Message paramMessage)
    {
      switch (paramMessage.what)
      {
      default: 
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      case 5: 
        String str1;
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        return;
                      } while (UsbDebuggingManager.-get0(UsbDebuggingManager.this));
                      UsbDebuggingManager.-set0(UsbDebuggingManager.this, true);
                      UsbDebuggingManager.-set2(UsbDebuggingManager.this, new UsbDebuggingManager.UsbDebuggingThread(UsbDebuggingManager.this));
                      UsbDebuggingManager.-get3(UsbDebuggingManager.this).start();
                      return;
                    } while (!UsbDebuggingManager.-get0(UsbDebuggingManager.this));
                    UsbDebuggingManager.-set0(UsbDebuggingManager.this, false);
                  } while (UsbDebuggingManager.-get3(UsbDebuggingManager.this) == null);
                  UsbDebuggingManager.-get3(UsbDebuggingManager.this).stopListening();
                  UsbDebuggingManager.-set2(UsbDebuggingManager.this, null);
                  return;
                  str1 = (String)paramMessage.obj;
                  String str2 = UsbDebuggingManager.-wrap0(UsbDebuggingManager.this, str1);
                  if (!str2.equals(UsbDebuggingManager.-get1(UsbDebuggingManager.this)))
                  {
                    Slog.e("UsbDebuggingManager", "Fingerprints do not match. Got " + str2 + ", expected " + UsbDebuggingManager.-get1(UsbDebuggingManager.this));
                    return;
                  }
                  if (paramMessage.arg1 == 1) {
                    UsbDebuggingManager.-wrap3(UsbDebuggingManager.this, str1);
                  }
                } while (UsbDebuggingManager.-get3(UsbDebuggingManager.this) == null);
                UsbDebuggingManager.-get3(UsbDebuggingManager.this).sendResponse("OK");
                return;
              } while (UsbDebuggingManager.-get3(UsbDebuggingManager.this) == null);
              UsbDebuggingManager.-get3(UsbDebuggingManager.this).sendResponse("NO");
              return;
              if (!"trigger_restart_min_framework".equals(SystemProperties.get("vold.decrypt"))) {
                break;
              }
              Slog.d("UsbDebuggingManager", "Deferring adb confirmation until after vold decrypt");
            } while (UsbDebuggingManager.-get3(UsbDebuggingManager.this) == null);
            UsbDebuggingManager.-get3(UsbDebuggingManager.this).sendResponse("NO");
            return;
            paramMessage = (String)paramMessage.obj;
            str1 = UsbDebuggingManager.-wrap0(UsbDebuggingManager.this, paramMessage);
            if (!SystemProperties.get("persist.sys.allcommode", "false").equals("true")) {
              break;
            }
          } while (UsbDebuggingManager.-get3(UsbDebuggingManager.this) == null);
          UsbDebuggingManager.-get3(UsbDebuggingManager.this).sendResponse("OK");
          return;
          if (!"".equals(str1)) {
            break;
          }
        } while (UsbDebuggingManager.-get3(UsbDebuggingManager.this) == null);
        UsbDebuggingManager.-get3(UsbDebuggingManager.this).sendResponse("NO");
        return;
        UsbDebuggingManager.-set1(UsbDebuggingManager.this, str1);
        UsbDebuggingManager.-wrap2(UsbDebuggingManager.this, paramMessage, UsbDebuggingManager.-get1(UsbDebuggingManager.this));
        return;
      }
      UsbDebuggingManager.-wrap1(UsbDebuggingManager.this);
    }
  }
  
  class UsbDebuggingThread
    extends Thread
  {
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private LocalSocket mSocket;
    private boolean mStopped;
    
    UsbDebuggingThread()
    {
      super();
    }
    
    private void closeSocketLocked()
    {
      try
      {
        if (this.mOutputStream != null)
        {
          this.mOutputStream.close();
          this.mOutputStream = null;
        }
      }
      catch (IOException localIOException1)
      {
        for (;;)
        {
          try
          {
            if (this.mSocket != null)
            {
              this.mSocket.close();
              this.mSocket = null;
            }
            return;
          }
          catch (IOException localIOException2)
          {
            Slog.e("UsbDebuggingManager", "Failed closing socket: " + localIOException2);
          }
          localIOException1 = localIOException1;
          Slog.e("UsbDebuggingManager", "Failed closing output stream: " + localIOException1);
        }
      }
    }
    
    /* Error */
    private void listenToSocket()
      throws IOException
    {
      // Byte code:
      //   0: sipush 4096
      //   3: newarray <illegal type>
      //   5: astore_2
      //   6: aload_0
      //   7: getfield 71	com/android/server/usb/UsbDebuggingManager$UsbDebuggingThread:mInputStream	Ljava/io/InputStream;
      //   10: aload_2
      //   11: invokevirtual 77	java/io/InputStream:read	([B)I
      //   14: istore_1
      //   15: iload_1
      //   16: ifge +12 -> 28
      //   19: aload_0
      //   20: monitorenter
      //   21: aload_0
      //   22: invokespecial 79	com/android/server/usb/UsbDebuggingManager$UsbDebuggingThread:closeSocketLocked	()V
      //   25: aload_0
      //   26: monitorexit
      //   27: return
      //   28: aload_2
      //   29: iconst_0
      //   30: baload
      //   31: bipush 80
      //   33: if_icmpne +96 -> 129
      //   36: aload_2
      //   37: iconst_1
      //   38: baload
      //   39: bipush 75
      //   41: if_icmpne +88 -> 129
      //   44: new 81	java/lang/String
      //   47: dup
      //   48: aload_2
      //   49: iconst_2
      //   50: iload_1
      //   51: invokestatic 87	java/util/Arrays:copyOfRange	([BII)[B
      //   54: invokespecial 90	java/lang/String:<init>	([B)V
      //   57: astore_3
      //   58: ldc 23
      //   60: new 45	java/lang/StringBuilder
      //   63: dup
      //   64: invokespecial 47	java/lang/StringBuilder:<init>	()V
      //   67: ldc 92
      //   69: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   72: aload_3
      //   73: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   76: invokevirtual 60	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   79: invokestatic 95	android/util/Slog:d	(Ljava/lang/String;Ljava/lang/String;)I
      //   82: pop
      //   83: aload_0
      //   84: getfield 21	com/android/server/usb/UsbDebuggingManager$UsbDebuggingThread:this$0	Lcom/android/server/usb/UsbDebuggingManager;
      //   87: invokestatic 99	com/android/server/usb/UsbDebuggingManager:-get2	(Lcom/android/server/usb/UsbDebuggingManager;)Landroid/os/Handler;
      //   90: iconst_5
      //   91: invokevirtual 105	android/os/Handler:obtainMessage	(I)Landroid/os/Message;
      //   94: astore 4
      //   96: aload 4
      //   98: aload_3
      //   99: putfield 111	android/os/Message:obj	Ljava/lang/Object;
      //   102: aload_0
      //   103: getfield 21	com/android/server/usb/UsbDebuggingManager$UsbDebuggingThread:this$0	Lcom/android/server/usb/UsbDebuggingManager;
      //   106: invokestatic 99	com/android/server/usb/UsbDebuggingManager:-get2	(Lcom/android/server/usb/UsbDebuggingManager;)Landroid/os/Handler;
      //   109: aload 4
      //   111: invokevirtual 115	android/os/Handler:sendMessage	(Landroid/os/Message;)Z
      //   114: pop
      //   115: goto -109 -> 6
      //   118: astore_2
      //   119: aload_0
      //   120: monitorenter
      //   121: aload_0
      //   122: invokespecial 79	com/android/server/usb/UsbDebuggingManager$UsbDebuggingThread:closeSocketLocked	()V
      //   125: aload_0
      //   126: monitorexit
      //   127: aload_2
      //   128: athrow
      //   129: ldc 23
      //   131: new 45	java/lang/StringBuilder
      //   134: dup
      //   135: invokespecial 47	java/lang/StringBuilder:<init>	()V
      //   138: ldc 117
      //   140: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   143: new 81	java/lang/String
      //   146: dup
      //   147: aload_2
      //   148: iconst_0
      //   149: iconst_2
      //   150: invokestatic 87	java/util/Arrays:copyOfRange	([BII)[B
      //   153: invokespecial 90	java/lang/String:<init>	([B)V
      //   156: invokevirtual 53	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   159: invokevirtual 60	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   162: invokestatic 66	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   165: pop
      //   166: goto -147 -> 19
      //   169: astore_2
      //   170: aload_0
      //   171: monitorexit
      //   172: aload_2
      //   173: athrow
      //   174: astore_2
      //   175: aload_0
      //   176: monitorexit
      //   177: aload_2
      //   178: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	179	0	this	UsbDebuggingThread
      //   14	37	1	i	int
      //   5	44	2	arrayOfByte1	byte[]
      //   118	30	2	arrayOfByte2	byte[]
      //   169	4	2	localObject1	Object
      //   174	4	2	localObject2	Object
      //   57	42	3	str	String
      //   94	16	4	localMessage	Message
      // Exception table:
      //   from	to	target	type
      //   0	6	118	finally
      //   6	15	118	finally
      //   44	115	118	finally
      //   129	166	118	finally
      //   21	25	169	finally
      //   121	125	174	finally
    }
    
    private void openSocketLocked()
      throws IOException
    {
      try
      {
        LocalSocketAddress localLocalSocketAddress = new LocalSocketAddress("adbd", LocalSocketAddress.Namespace.RESERVED);
        this.mInputStream = null;
        this.mSocket = new LocalSocket();
        this.mSocket.connect(localLocalSocketAddress);
        this.mOutputStream = this.mSocket.getOutputStream();
        this.mInputStream = this.mSocket.getInputStream();
        return;
      }
      catch (IOException localIOException)
      {
        closeSocketLocked();
        throw localIOException;
      }
    }
    
    /* Error */
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 150	com/android/server/usb/UsbDebuggingManager$UsbDebuggingThread:mStopped	Z
      //   6: istore_1
      //   7: iload_1
      //   8: ifeq +6 -> 14
      //   11: aload_0
      //   12: monitorexit
      //   13: return
      //   14: aload_0
      //   15: invokespecial 152	com/android/server/usb/UsbDebuggingManager$UsbDebuggingThread:openSocketLocked	()V
      //   18: aload_0
      //   19: monitorexit
      //   20: aload_0
      //   21: invokespecial 154	com/android/server/usb/UsbDebuggingManager$UsbDebuggingThread:listenToSocket	()V
      //   24: goto -24 -> 0
      //   27: astore_2
      //   28: ldc2_w 155
      //   31: invokestatic 162	android/os/SystemClock:sleep	(J)V
      //   34: goto -34 -> 0
      //   37: astore_2
      //   38: ldc2_w 155
      //   41: invokestatic 162	android/os/SystemClock:sleep	(J)V
      //   44: goto -26 -> 18
      //   47: astore_2
      //   48: aload_0
      //   49: monitorexit
      //   50: aload_2
      //   51: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	52	0	this	UsbDebuggingThread
      //   6	2	1	bool	boolean
      //   27	1	2	localException1	Exception
      //   37	1	2	localException2	Exception
      //   47	4	2	localObject	Object
      // Exception table:
      //   from	to	target	type
      //   20	24	27	java/lang/Exception
      //   14	18	37	java/lang/Exception
      //   2	7	47	finally
      //   14	18	47	finally
      //   38	44	47	finally
    }
    
    /* Error */
    void sendResponse(String paramString)
    {
      // Byte code:
      //   0: aload_0
      //   1: monitorenter
      //   2: aload_0
      //   3: getfield 150	com/android/server/usb/UsbDebuggingManager$UsbDebuggingThread:mStopped	Z
      //   6: ifne +23 -> 29
      //   9: aload_0
      //   10: getfield 33	com/android/server/usb/UsbDebuggingManager$UsbDebuggingThread:mOutputStream	Ljava/io/OutputStream;
      //   13: astore_2
      //   14: aload_2
      //   15: ifnull +14 -> 29
      //   18: aload_0
      //   19: getfield 33	com/android/server/usb/UsbDebuggingManager$UsbDebuggingThread:mOutputStream	Ljava/io/OutputStream;
      //   22: aload_1
      //   23: invokevirtual 167	java/lang/String:getBytes	()[B
      //   26: invokevirtual 170	java/io/OutputStream:write	([B)V
      //   29: aload_0
      //   30: monitorexit
      //   31: return
      //   32: astore_1
      //   33: ldc 23
      //   35: ldc -84
      //   37: aload_1
      //   38: invokestatic 175	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   41: pop
      //   42: goto -13 -> 29
      //   45: astore_1
      //   46: aload_0
      //   47: monitorexit
      //   48: aload_1
      //   49: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	50	0	this	UsbDebuggingThread
      //   0	50	1	paramString	String
      //   13	2	2	localOutputStream	OutputStream
      // Exception table:
      //   from	to	target	type
      //   18	29	32	java/io/IOException
      //   2	14	45	finally
      //   18	29	45	finally
      //   33	42	45	finally
    }
    
    void stopListening()
    {
      try
      {
        this.mStopped = true;
        closeSocketLocked();
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/usb/UsbDebuggingManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */