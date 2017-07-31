package com.android.server;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.service.persistentdata.IPersistentDataBlockService.Stub;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Arrays;

public class PersistentDataBlockService
  extends SystemService
{
  public static final int DIGEST_SIZE_BYTES = 32;
  private static final String FLASH_LOCK_LOCKED = "1";
  private static final String FLASH_LOCK_PROP = "ro.boot.flash.locked";
  private static final String FLASH_LOCK_UNLOCKED = "0";
  private static final int HEADER_SIZE = 8;
  private static final int MAX_DATA_BLOCK_SIZE = 102400;
  private static final String OEM_UNLOCK_PROP = "sys.oem_unlock_allowed";
  private static final int PARTITION_TYPE_MARKER = 428873843;
  private static final String PERSISTENT_DATA_BLOCK_PROP = "ro.frp.pst";
  private static final String TAG = PersistentDataBlockService.class.getSimpleName();
  private int mAllowedUid = -1;
  private long mBlockDeviceSize;
  private final Context mContext;
  private final String mDataBlockFile;
  @GuardedBy("mLock")
  private boolean mIsWritable = true;
  private final Object mLock = new Object();
  private final IBinder mService = new IPersistentDataBlockService.Stub()
  {
    private void enforcePersistentDataBlockAccess()
    {
      if (PersistentDataBlockService.-get1(PersistentDataBlockService.this).checkCallingPermission("android.permission.ACCESS_PDB_STATE") != 0) {
        PersistentDataBlockService.-wrap10(PersistentDataBlockService.this, Binder.getCallingUid());
      }
    }
    
    /* Error */
    public int getDataBlockSize()
    {
      // Byte code:
      //   0: aload_0
      //   1: invokespecial 46	com/android/server/PersistentDataBlockService$1:enforcePersistentDataBlockAccess	()V
      //   4: new 48	java/io/DataInputStream
      //   7: dup
      //   8: new 50	java/io/FileInputStream
      //   11: dup
      //   12: new 52	java/io/File
      //   15: dup
      //   16: aload_0
      //   17: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   20: invokestatic 56	com/android/server/PersistentDataBlockService:-get2	(Lcom/android/server/PersistentDataBlockService;)Ljava/lang/String;
      //   23: invokespecial 59	java/io/File:<init>	(Ljava/lang/String;)V
      //   26: invokespecial 62	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   29: invokespecial 65	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
      //   32: astore_2
      //   33: aload_0
      //   34: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   37: invokestatic 69	com/android/server/PersistentDataBlockService:-get4	(Lcom/android/server/PersistentDataBlockService;)Ljava/lang/Object;
      //   40: astore_3
      //   41: aload_3
      //   42: monitorenter
      //   43: aload_0
      //   44: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   47: aload_2
      //   48: invokestatic 73	com/android/server/PersistentDataBlockService:-wrap3	(Lcom/android/server/PersistentDataBlockService;Ljava/io/DataInputStream;)I
      //   51: istore_1
      //   52: aload_3
      //   53: monitorexit
      //   54: aload_2
      //   55: invokestatic 79	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   58: iload_1
      //   59: ireturn
      //   60: astore_2
      //   61: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   64: ldc 85
      //   66: invokestatic 91	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   69: pop
      //   70: iconst_0
      //   71: ireturn
      //   72: astore 4
      //   74: aload_3
      //   75: monitorexit
      //   76: aload 4
      //   78: athrow
      //   79: astore_3
      //   80: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   83: ldc 93
      //   85: invokestatic 91	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   88: pop
      //   89: aload_2
      //   90: invokestatic 79	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   93: iconst_0
      //   94: ireturn
      //   95: astore_3
      //   96: aload_2
      //   97: invokestatic 79	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   100: aload_3
      //   101: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	102	0	this	1
      //   51	8	1	i	int
      //   32	23	2	localDataInputStream	DataInputStream
      //   60	37	2	localFileNotFoundException	java.io.FileNotFoundException
      //   79	1	3	localIOException	IOException
      //   95	6	3	localObject2	Object
      //   72	5	4	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   4	33	60	java/io/FileNotFoundException
      //   43	52	72	finally
      //   33	43	79	java/io/IOException
      //   52	54	79	java/io/IOException
      //   74	79	79	java/io/IOException
      //   33	43	95	finally
      //   52	54	95	finally
      //   74	79	95	finally
      //   80	89	95	finally
    }
    
    public int getFlashLockState()
    {
      PersistentDataBlockService.-wrap8(PersistentDataBlockService.this);
      String str = SystemProperties.get("ro.boot.flash.locked");
      if (str.equals("1")) {
        return 1;
      }
      if (str.equals("0")) {
        return 0;
      }
      return -1;
    }
    
    public long getMaximumDataBlockSize()
    {
      long l = PersistentDataBlockService.-wrap5(PersistentDataBlockService.this) - 8L - 1L;
      if (l <= 102400L) {
        return l;
      }
      return 102400L;
    }
    
    public boolean getOemUnlockEnabled()
    {
      PersistentDataBlockService.-wrap8(PersistentDataBlockService.this);
      return PersistentDataBlockService.-wrap1(PersistentDataBlockService.this);
    }
    
    /* Error */
    public byte[] read()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   4: invokestatic 35	android/os/Binder:getCallingUid	()I
      //   7: invokestatic 39	com/android/server/PersistentDataBlockService:-wrap10	(Lcom/android/server/PersistentDataBlockService;I)V
      //   10: aload_0
      //   11: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   14: invokestatic 136	com/android/server/PersistentDataBlockService:-wrap2	(Lcom/android/server/PersistentDataBlockService;)Z
      //   17: ifne +7 -> 24
      //   20: iconst_0
      //   21: newarray <illegal type>
      //   23: areturn
      //   24: new 48	java/io/DataInputStream
      //   27: dup
      //   28: new 50	java/io/FileInputStream
      //   31: dup
      //   32: new 52	java/io/File
      //   35: dup
      //   36: aload_0
      //   37: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   40: invokestatic 56	com/android/server/PersistentDataBlockService:-get2	(Lcom/android/server/PersistentDataBlockService;)Ljava/lang/String;
      //   43: invokespecial 59	java/io/File:<init>	(Ljava/lang/String;)V
      //   46: invokespecial 62	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   49: invokespecial 65	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
      //   52: astore_3
      //   53: aload_0
      //   54: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   57: invokestatic 69	com/android/server/PersistentDataBlockService:-get4	(Lcom/android/server/PersistentDataBlockService;)Ljava/lang/Object;
      //   60: astore 4
      //   62: aload 4
      //   64: monitorenter
      //   65: aload_0
      //   66: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   69: aload_3
      //   70: invokestatic 73	com/android/server/PersistentDataBlockService:-wrap3	(Lcom/android/server/PersistentDataBlockService;Ljava/io/DataInputStream;)I
      //   73: istore_1
      //   74: iload_1
      //   75: ifne +44 -> 119
      //   78: iconst_0
      //   79: newarray <illegal type>
      //   81: astore 5
      //   83: aload 4
      //   85: monitorexit
      //   86: aload_3
      //   87: invokevirtual 139	java/io/DataInputStream:close	()V
      //   90: aload 5
      //   92: areturn
      //   93: astore_3
      //   94: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   97: ldc -115
      //   99: aload_3
      //   100: invokestatic 144	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   103: pop
      //   104: aconst_null
      //   105: areturn
      //   106: astore_3
      //   107: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   110: ldc -110
      //   112: invokestatic 91	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   115: pop
      //   116: aload 5
      //   118: areturn
      //   119: iload_1
      //   120: newarray <illegal type>
      //   122: astore 5
      //   124: aload_3
      //   125: aload 5
      //   127: iconst_0
      //   128: iload_1
      //   129: invokevirtual 149	java/io/DataInputStream:read	([BII)I
      //   132: istore_2
      //   133: iload_2
      //   134: iload_1
      //   135: if_icmpge +59 -> 194
      //   138: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   141: new 151	java/lang/StringBuilder
      //   144: dup
      //   145: invokespecial 152	java/lang/StringBuilder:<init>	()V
      //   148: ldc -102
      //   150: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   153: iload_2
      //   154: invokevirtual 161	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   157: ldc -93
      //   159: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   162: iload_1
      //   163: invokevirtual 161	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
      //   166: invokevirtual 166	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   169: invokestatic 91	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   172: pop
      //   173: aload 4
      //   175: monitorexit
      //   176: aload_3
      //   177: invokevirtual 139	java/io/DataInputStream:close	()V
      //   180: aconst_null
      //   181: areturn
      //   182: astore_3
      //   183: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   186: ldc -110
      //   188: invokestatic 91	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   191: pop
      //   192: aconst_null
      //   193: areturn
      //   194: aload 4
      //   196: monitorexit
      //   197: aload_3
      //   198: invokevirtual 139	java/io/DataInputStream:close	()V
      //   201: aload 5
      //   203: areturn
      //   204: astore_3
      //   205: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   208: ldc -110
      //   210: invokestatic 91	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   213: pop
      //   214: aload 5
      //   216: areturn
      //   217: astore 5
      //   219: aload 4
      //   221: monitorexit
      //   222: aload 5
      //   224: athrow
      //   225: astore 4
      //   227: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   230: ldc -88
      //   232: aload 4
      //   234: invokestatic 144	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   237: pop
      //   238: aload_3
      //   239: invokevirtual 139	java/io/DataInputStream:close	()V
      //   242: aconst_null
      //   243: areturn
      //   244: astore_3
      //   245: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   248: ldc -110
      //   250: invokestatic 91	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   253: pop
      //   254: aconst_null
      //   255: areturn
      //   256: astore 4
      //   258: aload_3
      //   259: invokevirtual 139	java/io/DataInputStream:close	()V
      //   262: aload 4
      //   264: athrow
      //   265: astore_3
      //   266: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   269: ldc -110
      //   271: invokestatic 91	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
      //   274: pop
      //   275: goto -13 -> 262
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	278	0	this	1
      //   73	90	1	i	int
      //   132	22	2	j	int
      //   52	35	3	localDataInputStream	DataInputStream
      //   93	7	3	localFileNotFoundException	java.io.FileNotFoundException
      //   106	71	3	localIOException1	IOException
      //   182	16	3	localIOException2	IOException
      //   204	35	3	localIOException3	IOException
      //   244	15	3	localIOException4	IOException
      //   265	1	3	localIOException5	IOException
      //   225	8	4	localIOException6	IOException
      //   256	7	4	localObject2	Object
      //   81	134	5	arrayOfByte	byte[]
      //   217	6	5	localObject3	Object
      // Exception table:
      //   from	to	target	type
      //   24	53	93	java/io/FileNotFoundException
      //   86	90	106	java/io/IOException
      //   176	180	182	java/io/IOException
      //   197	201	204	java/io/IOException
      //   65	74	217	finally
      //   78	83	217	finally
      //   119	133	217	finally
      //   138	173	217	finally
      //   53	65	225	java/io/IOException
      //   83	86	225	java/io/IOException
      //   173	176	225	java/io/IOException
      //   194	197	225	java/io/IOException
      //   219	225	225	java/io/IOException
      //   238	242	244	java/io/IOException
      //   53	65	256	finally
      //   83	86	256	finally
      //   173	176	256	finally
      //   194	197	256	finally
      //   219	225	256	finally
      //   227	238	256	finally
      //   258	262	265	java/io/IOException
    }
    
    public void setOemUnlockEnabled(boolean paramAnonymousBoolean)
      throws SecurityException
    {
      if (ActivityManager.isUserAMonkey()) {
        return;
      }
      PersistentDataBlockService.-wrap9(PersistentDataBlockService.this);
      PersistentDataBlockService.-wrap7(PersistentDataBlockService.this);
      if (paramAnonymousBoolean)
      {
        PersistentDataBlockService.-wrap11(PersistentDataBlockService.this, "no_oem_unlock");
        PersistentDataBlockService.-wrap11(PersistentDataBlockService.this, "no_factory_reset");
      }
      synchronized (PersistentDataBlockService.-get4(PersistentDataBlockService.this))
      {
        PersistentDataBlockService.-wrap6(PersistentDataBlockService.this, paramAnonymousBoolean);
        PersistentDataBlockService.-wrap0(PersistentDataBlockService.this);
        return;
      }
    }
    
    public void wipe()
    {
      PersistentDataBlockService.-wrap9(PersistentDataBlockService.this);
      synchronized (PersistentDataBlockService.-get4(PersistentDataBlockService.this))
      {
        if (PersistentDataBlockService.-wrap4(PersistentDataBlockService.this, PersistentDataBlockService.-get2(PersistentDataBlockService.this)) < 0)
        {
          Slog.e(PersistentDataBlockService.-get0(), "failed to wipe persistent partition");
          return;
        }
        PersistentDataBlockService.-set0(PersistentDataBlockService.this, false);
        Slog.i(PersistentDataBlockService.-get0(), "persistent partition now wiped and unwritable");
      }
    }
    
    /* Error */
    public int write(byte[] paramAnonymousArrayOfByte)
      throws android.os.RemoteException
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   4: invokestatic 35	android/os/Binder:getCallingUid	()I
      //   7: invokestatic 39	com/android/server/PersistentDataBlockService:-wrap10	(Lcom/android/server/PersistentDataBlockService;I)V
      //   10: aload_0
      //   11: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   14: invokestatic 121	com/android/server/PersistentDataBlockService:-wrap5	(Lcom/android/server/PersistentDataBlockService;)J
      //   17: ldc2_w 122
      //   20: lsub
      //   21: lconst_1
      //   22: lsub
      //   23: lstore_3
      //   24: aload_1
      //   25: arraylength
      //   26: i2l
      //   27: lload_3
      //   28: lcmp
      //   29: ifle +7 -> 36
      //   32: lload_3
      //   33: lneg
      //   34: l2i
      //   35: ireturn
      //   36: new 221	java/io/DataOutputStream
      //   39: dup
      //   40: new 223	java/io/FileOutputStream
      //   43: dup
      //   44: new 52	java/io/File
      //   47: dup
      //   48: aload_0
      //   49: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   52: invokestatic 56	com/android/server/PersistentDataBlockService:-get2	(Lcom/android/server/PersistentDataBlockService;)Ljava/lang/String;
      //   55: invokespecial 59	java/io/File:<init>	(Ljava/lang/String;)V
      //   58: invokespecial 224	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
      //   61: invokespecial 227	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
      //   64: astore 6
      //   66: aload_1
      //   67: arraylength
      //   68: bipush 8
      //   70: iadd
      //   71: invokestatic 233	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
      //   74: astore 7
      //   76: aload 7
      //   78: ldc -22
      //   80: invokevirtual 237	java/nio/ByteBuffer:putInt	(I)Ljava/nio/ByteBuffer;
      //   83: pop
      //   84: aload 7
      //   86: aload_1
      //   87: arraylength
      //   88: invokevirtual 237	java/nio/ByteBuffer:putInt	(I)Ljava/nio/ByteBuffer;
      //   91: pop
      //   92: aload 7
      //   94: aload_1
      //   95: invokevirtual 241	java/nio/ByteBuffer:put	([B)Ljava/nio/ByteBuffer;
      //   98: pop
      //   99: aload_0
      //   100: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   103: invokestatic 69	com/android/server/PersistentDataBlockService:-get4	(Lcom/android/server/PersistentDataBlockService;)Ljava/lang/Object;
      //   106: astore 5
      //   108: aload 5
      //   110: monitorenter
      //   111: aload_0
      //   112: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   115: invokestatic 244	com/android/server/PersistentDataBlockService:-get3	(Lcom/android/server/PersistentDataBlockService;)Z
      //   118: ifne +26 -> 144
      //   121: aload 6
      //   123: invokestatic 79	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   126: aload 5
      //   128: monitorexit
      //   129: iconst_m1
      //   130: ireturn
      //   131: astore_1
      //   132: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   135: ldc -115
      //   137: aload_1
      //   138: invokestatic 144	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   141: pop
      //   142: iconst_m1
      //   143: ireturn
      //   144: aload 6
      //   146: bipush 32
      //   148: newarray <illegal type>
      //   150: iconst_0
      //   151: bipush 32
      //   153: invokevirtual 247	java/io/DataOutputStream:write	([BII)V
      //   156: aload 6
      //   158: aload 7
      //   160: invokevirtual 250	java/nio/ByteBuffer:array	()[B
      //   163: invokevirtual 253	java/io/DataOutputStream:write	([B)V
      //   166: aload 6
      //   168: invokevirtual 256	java/io/DataOutputStream:flush	()V
      //   171: aload 6
      //   173: invokestatic 79	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   176: aload_0
      //   177: getfield 12	com/android/server/PersistentDataBlockService$1:this$0	Lcom/android/server/PersistentDataBlockService;
      //   180: invokestatic 198	com/android/server/PersistentDataBlockService:-wrap0	(Lcom/android/server/PersistentDataBlockService;)Z
      //   183: ifeq +47 -> 230
      //   186: aload_1
      //   187: arraylength
      //   188: istore_2
      //   189: aload 5
      //   191: monitorexit
      //   192: iload_2
      //   193: ireturn
      //   194: astore_1
      //   195: invokestatic 83	com/android/server/PersistentDataBlockService:-get0	()Ljava/lang/String;
      //   198: ldc_w 258
      //   201: aload_1
      //   202: invokestatic 144	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   205: pop
      //   206: aload 6
      //   208: invokestatic 79	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   211: aload 5
      //   213: monitorexit
      //   214: iconst_m1
      //   215: ireturn
      //   216: astore_1
      //   217: aload 6
      //   219: invokestatic 79	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
      //   222: aload_1
      //   223: athrow
      //   224: astore_1
      //   225: aload 5
      //   227: monitorexit
      //   228: aload_1
      //   229: athrow
      //   230: aload 5
      //   232: monitorexit
      //   233: iconst_m1
      //   234: ireturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	235	0	this	1
      //   0	235	1	paramAnonymousArrayOfByte	byte[]
      //   188	5	2	i	int
      //   23	10	3	l	long
      //   64	154	6	localDataOutputStream	java.io.DataOutputStream
      //   74	85	7	localByteBuffer	java.nio.ByteBuffer
      // Exception table:
      //   from	to	target	type
      //   36	66	131	java/io/FileNotFoundException
      //   144	171	194	java/io/IOException
      //   144	171	216	finally
      //   195	206	216	finally
      //   111	126	224	finally
      //   171	189	224	finally
      //   206	211	224	finally
      //   217	224	224	finally
    }
  };
  
  public PersistentDataBlockService(Context paramContext)
  {
    super(paramContext);
    this.mContext = paramContext;
    this.mDataBlockFile = SystemProperties.get("ro.frp.pst");
    this.mBlockDeviceSize = -1L;
    this.mAllowedUid = getAllowedUid(0);
  }
  
  /* Error */
  private boolean computeAndWriteDigestLocked()
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: invokespecial 174	com/android/server/PersistentDataBlockService:computeDigestLocked	([B)[B
    //   5: astore_2
    //   6: aload_2
    //   7: ifnull +84 -> 91
    //   10: new 176	java/io/DataOutputStream
    //   13: dup
    //   14: new 178	java/io/FileOutputStream
    //   17: dup
    //   18: new 180	java/io/File
    //   21: dup
    //   22: aload_0
    //   23: getfield 59	com/android/server/PersistentDataBlockService:mDataBlockFile	Ljava/lang/String;
    //   26: invokespecial 182	java/io/File:<init>	(Ljava/lang/String;)V
    //   29: invokespecial 185	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   32: invokespecial 188	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   35: astore_1
    //   36: aload_1
    //   37: aload_2
    //   38: iconst_0
    //   39: bipush 32
    //   41: invokevirtual 192	java/io/DataOutputStream:write	([BII)V
    //   44: aload_1
    //   45: invokevirtual 195	java/io/DataOutputStream:flush	()V
    //   48: aload_1
    //   49: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   52: iconst_1
    //   53: ireturn
    //   54: astore_1
    //   55: getstatic 50	com/android/server/PersistentDataBlockService:TAG	Ljava/lang/String;
    //   58: ldc -53
    //   60: aload_1
    //   61: invokestatic 209	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   64: pop
    //   65: iconst_0
    //   66: ireturn
    //   67: astore_2
    //   68: getstatic 50	com/android/server/PersistentDataBlockService:TAG	Ljava/lang/String;
    //   71: ldc -45
    //   73: aload_2
    //   74: invokestatic 209	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   77: pop
    //   78: aload_1
    //   79: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   82: iconst_0
    //   83: ireturn
    //   84: astore_2
    //   85: aload_1
    //   86: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   89: aload_2
    //   90: athrow
    //   91: iconst_0
    //   92: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	93	0	this	PersistentDataBlockService
    //   35	14	1	localDataOutputStream	java.io.DataOutputStream
    //   54	32	1	localFileNotFoundException	java.io.FileNotFoundException
    //   5	33	2	arrayOfByte	byte[]
    //   67	7	2	localIOException	IOException
    //   84	6	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   10	36	54	java/io/FileNotFoundException
    //   36	48	67	java/io/IOException
    //   36	48	84	finally
    //   68	78	84	finally
  }
  
  /* Error */
  private byte[] computeDigestLocked(byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: new 215	java/io/DataInputStream
    //   3: dup
    //   4: new 217	java/io/FileInputStream
    //   7: dup
    //   8: new 180	java/io/File
    //   11: dup
    //   12: aload_0
    //   13: getfield 59	com/android/server/PersistentDataBlockService:mDataBlockFile	Ljava/lang/String;
    //   16: invokespecial 182	java/io/File:<init>	(Ljava/lang/String;)V
    //   19: invokespecial 218	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   22: invokespecial 221	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   25: astore_3
    //   26: ldc -33
    //   28: invokestatic 229	java/security/MessageDigest:getInstance	(Ljava/lang/String;)Ljava/security/MessageDigest;
    //   31: astore 4
    //   33: aload_1
    //   34: ifnull +100 -> 134
    //   37: aload_1
    //   38: arraylength
    //   39: bipush 32
    //   41: if_icmpne +93 -> 134
    //   44: aload_3
    //   45: aload_1
    //   46: invokevirtual 233	java/io/DataInputStream:read	([B)I
    //   49: pop
    //   50: sipush 1024
    //   53: newarray <illegal type>
    //   55: astore_1
    //   56: aload 4
    //   58: aload_1
    //   59: iconst_0
    //   60: bipush 32
    //   62: invokevirtual 236	java/security/MessageDigest:update	([BII)V
    //   65: aload_3
    //   66: aload_1
    //   67: invokevirtual 233	java/io/DataInputStream:read	([B)I
    //   70: istore_2
    //   71: iload_2
    //   72: iconst_m1
    //   73: if_icmpeq +78 -> 151
    //   76: aload 4
    //   78: aload_1
    //   79: iconst_0
    //   80: iload_2
    //   81: invokevirtual 236	java/security/MessageDigest:update	([BII)V
    //   84: goto -19 -> 65
    //   87: astore_1
    //   88: getstatic 50	com/android/server/PersistentDataBlockService:TAG	Ljava/lang/String;
    //   91: ldc -18
    //   93: aload_1
    //   94: invokestatic 209	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   97: pop
    //   98: aload_3
    //   99: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   102: aconst_null
    //   103: areturn
    //   104: astore_1
    //   105: getstatic 50	com/android/server/PersistentDataBlockService:TAG	Ljava/lang/String;
    //   108: ldc -53
    //   110: aload_1
    //   111: invokestatic 209	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   114: pop
    //   115: aconst_null
    //   116: areturn
    //   117: astore_1
    //   118: getstatic 50	com/android/server/PersistentDataBlockService:TAG	Ljava/lang/String;
    //   121: ldc -16
    //   123: aload_1
    //   124: invokestatic 209	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   127: pop
    //   128: aload_3
    //   129: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   132: aconst_null
    //   133: areturn
    //   134: aload_3
    //   135: bipush 32
    //   137: invokevirtual 243	java/io/DataInputStream:skipBytes	(I)I
    //   140: pop
    //   141: goto -91 -> 50
    //   144: astore_1
    //   145: aload_3
    //   146: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   149: aload_1
    //   150: athrow
    //   151: aload_3
    //   152: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   155: aload 4
    //   157: invokevirtual 247	java/security/MessageDigest:digest	()[B
    //   160: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	161	0	this	PersistentDataBlockService
    //   0	161	1	paramArrayOfByte	byte[]
    //   70	11	2	i	int
    //   25	127	3	localDataInputStream	DataInputStream
    //   31	125	4	localMessageDigest	java.security.MessageDigest
    // Exception table:
    //   from	to	target	type
    //   37	50	87	java/io/IOException
    //   50	65	87	java/io/IOException
    //   65	71	87	java/io/IOException
    //   76	84	87	java/io/IOException
    //   134	141	87	java/io/IOException
    //   0	26	104	java/io/FileNotFoundException
    //   26	33	117	java/security/NoSuchAlgorithmException
    //   37	50	144	finally
    //   50	65	144	finally
    //   65	71	144	finally
    //   76	84	144	finally
    //   88	98	144	finally
    //   134	141	144	finally
  }
  
  /* Error */
  private boolean doGetOemUnlockEnabled()
  {
    // Byte code:
    //   0: new 215	java/io/DataInputStream
    //   3: dup
    //   4: new 217	java/io/FileInputStream
    //   7: dup
    //   8: new 180	java/io/File
    //   11: dup
    //   12: aload_0
    //   13: getfield 59	com/android/server/PersistentDataBlockService:mDataBlockFile	Ljava/lang/String;
    //   16: invokespecial 182	java/io/File:<init>	(Ljava/lang/String;)V
    //   19: invokespecial 218	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   22: invokespecial 221	java/io/DataInputStream:<init>	(Ljava/io/InputStream;)V
    //   25: astore_3
    //   26: aload_0
    //   27: getfield 67	com/android/server/PersistentDataBlockService:mLock	Ljava/lang/Object;
    //   30: astore 4
    //   32: aload 4
    //   34: monitorenter
    //   35: aload_3
    //   36: aload_0
    //   37: invokespecial 112	com/android/server/PersistentDataBlockService:getBlockDeviceSize	()J
    //   40: lconst_1
    //   41: lsub
    //   42: invokevirtual 251	java/io/DataInputStream:skip	(J)J
    //   45: pop2
    //   46: aload_3
    //   47: invokevirtual 255	java/io/DataInputStream:readByte	()B
    //   50: istore_1
    //   51: iload_1
    //   52: ifeq +27 -> 79
    //   55: iconst_1
    //   56: istore_2
    //   57: aload 4
    //   59: monitorexit
    //   60: aload_3
    //   61: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   64: iload_2
    //   65: ireturn
    //   66: astore_3
    //   67: getstatic 50	com/android/server/PersistentDataBlockService:TAG	Ljava/lang/String;
    //   70: ldc_w 257
    //   73: invokestatic 260	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   76: pop
    //   77: iconst_0
    //   78: ireturn
    //   79: iconst_0
    //   80: istore_2
    //   81: goto -24 -> 57
    //   84: astore 5
    //   86: aload 4
    //   88: monitorexit
    //   89: aload 5
    //   91: athrow
    //   92: astore 4
    //   94: getstatic 50	com/android/server/PersistentDataBlockService:TAG	Ljava/lang/String;
    //   97: ldc_w 262
    //   100: aload 4
    //   102: invokestatic 209	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   105: pop
    //   106: aload_3
    //   107: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   110: iconst_0
    //   111: ireturn
    //   112: astore 4
    //   114: aload_3
    //   115: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   118: aload 4
    //   120: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	121	0	this	PersistentDataBlockService
    //   50	2	1	i	int
    //   56	25	2	bool	boolean
    //   25	36	3	localDataInputStream	DataInputStream
    //   66	49	3	localFileNotFoundException	java.io.FileNotFoundException
    //   92	9	4	localIOException	IOException
    //   112	7	4	localObject2	Object
    //   84	6	5	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   0	26	66	java/io/FileNotFoundException
    //   35	51	84	finally
    //   26	35	92	java/io/IOException
    //   57	60	92	java/io/IOException
    //   86	92	92	java/io/IOException
    //   26	35	112	finally
    //   57	60	112	finally
    //   86	92	112	finally
    //   94	106	112	finally
  }
  
  /* Error */
  private void doSetOemUnlockEnabledLocked(boolean paramBoolean)
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_2
    //   2: new 178	java/io/FileOutputStream
    //   5: dup
    //   6: new 180	java/io/File
    //   9: dup
    //   10: aload_0
    //   11: getfield 59	com/android/server/PersistentDataBlockService:mDataBlockFile	Ljava/lang/String;
    //   14: invokespecial 182	java/io/File:<init>	(Ljava/lang/String;)V
    //   17: invokespecial 185	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   20: astore 4
    //   22: aload 4
    //   24: invokevirtual 266	java/io/FileOutputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   27: astore_3
    //   28: aload_3
    //   29: aload_0
    //   30: invokespecial 112	com/android/server/PersistentDataBlockService:getBlockDeviceSize	()J
    //   33: lconst_1
    //   34: lsub
    //   35: invokevirtual 272	java/nio/channels/FileChannel:position	(J)Ljava/nio/channels/FileChannel;
    //   38: pop
    //   39: iconst_1
    //   40: invokestatic 278	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   43: astore 5
    //   45: iload_1
    //   46: ifeq +60 -> 106
    //   49: aload 5
    //   51: iload_2
    //   52: invokevirtual 282	java/nio/ByteBuffer:put	(B)Ljava/nio/ByteBuffer;
    //   55: pop
    //   56: aload 5
    //   58: invokevirtual 286	java/nio/ByteBuffer:flip	()Ljava/nio/Buffer;
    //   61: pop
    //   62: aload_3
    //   63: aload 5
    //   65: invokevirtual 289	java/nio/channels/FileChannel:write	(Ljava/nio/ByteBuffer;)I
    //   68: pop
    //   69: aload 4
    //   71: invokevirtual 290	java/io/FileOutputStream:flush	()V
    //   74: iload_1
    //   75: ifeq +36 -> 111
    //   78: ldc 13
    //   80: astore_3
    //   81: ldc 26
    //   83: aload_3
    //   84: invokestatic 294	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   87: aload 4
    //   89: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   92: return
    //   93: astore_3
    //   94: getstatic 50	com/android/server/PersistentDataBlockService:TAG	Ljava/lang/String;
    //   97: ldc_w 257
    //   100: aload_3
    //   101: invokestatic 209	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   104: pop
    //   105: return
    //   106: iconst_0
    //   107: istore_2
    //   108: goto -59 -> 49
    //   111: ldc 19
    //   113: astore_3
    //   114: goto -33 -> 81
    //   117: astore_3
    //   118: getstatic 50	com/android/server/PersistentDataBlockService:TAG	Ljava/lang/String;
    //   121: ldc_w 262
    //   124: aload_3
    //   125: invokestatic 209	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   128: pop
    //   129: iload_1
    //   130: ifeq +18 -> 148
    //   133: ldc 13
    //   135: astore_3
    //   136: ldc 26
    //   138: aload_3
    //   139: invokestatic 294	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   142: aload 4
    //   144: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   147: return
    //   148: ldc 19
    //   150: astore_3
    //   151: goto -15 -> 136
    //   154: astore 5
    //   156: iload_1
    //   157: ifeq +20 -> 177
    //   160: ldc 13
    //   162: astore_3
    //   163: ldc 26
    //   165: aload_3
    //   166: invokestatic 294	android/os/SystemProperties:set	(Ljava/lang/String;Ljava/lang/String;)V
    //   169: aload 4
    //   171: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   174: aload 5
    //   176: athrow
    //   177: ldc 19
    //   179: astore_3
    //   180: goto -17 -> 163
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	183	0	this	PersistentDataBlockService
    //   0	183	1	paramBoolean	boolean
    //   1	107	2	b	byte
    //   27	57	3	localObject1	Object
    //   93	8	3	localFileNotFoundException	java.io.FileNotFoundException
    //   113	1	3	str1	String
    //   117	8	3	localIOException	IOException
    //   135	45	3	str2	String
    //   20	150	4	localFileOutputStream	java.io.FileOutputStream
    //   43	21	5	localByteBuffer	java.nio.ByteBuffer
    //   154	21	5	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   2	22	93	java/io/FileNotFoundException
    //   22	45	117	java/io/IOException
    //   49	74	117	java/io/IOException
    //   22	45	154	finally
    //   49	74	154	finally
    //   118	129	154	finally
  }
  
  private boolean enforceChecksumValidity()
  {
    byte[] arrayOfByte1 = new byte[32];
    synchronized (this.mLock)
    {
      byte[] arrayOfByte2 = computeDigestLocked(arrayOfByte1);
      if (arrayOfByte2 != null)
      {
        boolean bool = Arrays.equals(arrayOfByte1, arrayOfByte2);
        if (bool) {
          return true;
        }
      }
      Slog.i(TAG, "Formatting FRP partition...");
      formatPartitionLocked(false);
      return false;
    }
  }
  
  private void enforceIsAdmin()
  {
    int i = UserHandle.getCallingUserId();
    if (!UserManager.get(this.mContext).isUserAdmin(i)) {
      throw new SecurityException("Only the Admin user is allowed to change OEM unlock state");
    }
  }
  
  private void enforceOemUnlockReadPermission()
  {
    if ((this.mContext.checkCallingOrSelfPermission("android.permission.READ_OEM_UNLOCK_STATE") == -1) && (this.mContext.checkCallingOrSelfPermission("android.permission.OEM_UNLOCK_STATE") == -1)) {
      throw new SecurityException("Can't access OEM unlock state. Requires READ_OEM_UNLOCK_STATE or OEM_UNLOCK_STATE permission.");
    }
  }
  
  private void enforceOemUnlockWritePermission()
  {
    this.mContext.enforceCallingOrSelfPermission("android.permission.OEM_UNLOCK_STATE", "Can't modify OEM unlock state");
  }
  
  private void enforceUid(int paramInt)
  {
    if (paramInt != this.mAllowedUid) {
      throw new SecurityException("uid " + paramInt + " not allowed to access PST");
    }
  }
  
  private void enforceUserRestriction(String paramString)
  {
    if (UserManager.get(this.mContext).hasUserRestriction(paramString)) {
      throw new SecurityException("OEM unlock is disallowed by user restriction: " + paramString);
    }
  }
  
  private void formatIfOemUnlockEnabled()
  {
    boolean bool = doGetOemUnlockEnabled();
    if (bool) {}
    for (;;)
    {
      synchronized (this.mLock)
      {
        formatPartitionLocked(true);
        if (bool)
        {
          ??? = "1";
          SystemProperties.set("sys.oem_unlock_allowed", (String)???);
          return;
        }
      }
      ??? = "0";
    }
  }
  
  /* Error */
  private void formatPartitionLocked(boolean paramBoolean)
  {
    // Byte code:
    //   0: new 176	java/io/DataOutputStream
    //   3: dup
    //   4: new 178	java/io/FileOutputStream
    //   7: dup
    //   8: new 180	java/io/File
    //   11: dup
    //   12: aload_0
    //   13: getfield 59	com/android/server/PersistentDataBlockService:mDataBlockFile	Ljava/lang/String;
    //   16: invokespecial 182	java/io/File:<init>	(Ljava/lang/String;)V
    //   19: invokespecial 185	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   22: invokespecial 188	java/io/DataOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   25: astore_2
    //   26: bipush 32
    //   28: newarray <illegal type>
    //   30: astore_3
    //   31: aload_2
    //   32: aload_3
    //   33: iconst_0
    //   34: bipush 32
    //   36: invokevirtual 192	java/io/DataOutputStream:write	([BII)V
    //   39: aload_2
    //   40: ldc 28
    //   42: invokevirtual 371	java/io/DataOutputStream:writeInt	(I)V
    //   45: aload_2
    //   46: iconst_0
    //   47: invokevirtual 371	java/io/DataOutputStream:writeInt	(I)V
    //   50: aload_2
    //   51: invokevirtual 195	java/io/DataOutputStream:flush	()V
    //   54: aload_2
    //   55: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   58: aload_0
    //   59: iload_1
    //   60: invokespecial 118	com/android/server/PersistentDataBlockService:doSetOemUnlockEnabledLocked	(Z)V
    //   63: aload_0
    //   64: invokespecial 74	com/android/server/PersistentDataBlockService:computeAndWriteDigestLocked	()Z
    //   67: pop
    //   68: return
    //   69: astore_2
    //   70: getstatic 50	com/android/server/PersistentDataBlockService:TAG	Ljava/lang/String;
    //   73: ldc -53
    //   75: aload_2
    //   76: invokestatic 209	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   79: pop
    //   80: return
    //   81: astore_3
    //   82: getstatic 50	com/android/server/PersistentDataBlockService:TAG	Ljava/lang/String;
    //   85: ldc_w 373
    //   88: aload_3
    //   89: invokestatic 209	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   92: pop
    //   93: aload_2
    //   94: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   97: return
    //   98: astore_3
    //   99: aload_2
    //   100: invokestatic 201	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   103: aload_3
    //   104: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	105	0	this	PersistentDataBlockService
    //   0	105	1	paramBoolean	boolean
    //   25	30	2	localDataOutputStream	java.io.DataOutputStream
    //   69	31	2	localFileNotFoundException	java.io.FileNotFoundException
    //   30	3	3	arrayOfByte	byte[]
    //   81	8	3	localIOException	IOException
    //   98	6	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   0	26	69	java/io/FileNotFoundException
    //   31	54	81	java/io/IOException
    //   31	54	98	finally
    //   82	93	98	finally
  }
  
  private int getAllowedUid(int paramInt)
  {
    String str = this.mContext.getResources().getString(17039463);
    PackageManager localPackageManager = this.mContext.getPackageManager();
    try
    {
      paramInt = localPackageManager.getPackageUidAsUser(str, 1048576, paramInt);
      return paramInt;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException)
    {
      Slog.e(TAG, "not able to find package " + str, localNameNotFoundException);
    }
    return -1;
  }
  
  private long getBlockDeviceSize()
  {
    synchronized (this.mLock)
    {
      if (this.mBlockDeviceSize == -1L) {
        this.mBlockDeviceSize = nativeGetBlockDeviceSize(this.mDataBlockFile);
      }
      return this.mBlockDeviceSize;
    }
  }
  
  private int getTotalDataSizeLocked(DataInputStream paramDataInputStream)
    throws IOException
  {
    paramDataInputStream.skipBytes(32);
    if (paramDataInputStream.readInt() == 428873843) {
      return paramDataInputStream.readInt();
    }
    return 0;
  }
  
  private native long nativeGetBlockDeviceSize(String paramString);
  
  private native int nativeWipe(String paramString);
  
  public void onStart()
  {
    enforceChecksumValidity();
    formatIfOemUnlockEnabled();
    publishBinderService("persistent_data_block", this.mService);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/PersistentDataBlockService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */