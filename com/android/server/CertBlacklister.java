package com.android.server;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.os.Binder;
import android.provider.Settings.Secure;
import java.io.File;

public class CertBlacklister
  extends Binder
{
  private static final String BLACKLIST_ROOT = System.getenv("ANDROID_DATA") + "/misc/keychain/";
  public static final String PUBKEY_BLACKLIST_KEY = "pubkey_blacklist";
  public static final String PUBKEY_PATH = BLACKLIST_ROOT + "pubkey_blacklist.txt";
  public static final String SERIAL_BLACKLIST_KEY = "serial_blacklist";
  public static final String SERIAL_PATH = BLACKLIST_ROOT + "serial_blacklist.txt";
  private static final String TAG = "CertBlacklister";
  
  public CertBlacklister(Context paramContext)
  {
    registerObservers(paramContext.getContentResolver());
  }
  
  private BlacklistObserver buildPubkeyObserver(ContentResolver paramContentResolver)
  {
    return new BlacklistObserver("pubkey_blacklist", "pubkey", PUBKEY_PATH, paramContentResolver);
  }
  
  private BlacklistObserver buildSerialObserver(ContentResolver paramContentResolver)
  {
    return new BlacklistObserver("serial_blacklist", "serial", SERIAL_PATH, paramContentResolver);
  }
  
  private void registerObservers(ContentResolver paramContentResolver)
  {
    paramContentResolver.registerContentObserver(Settings.Secure.getUriFor("pubkey_blacklist"), true, buildPubkeyObserver(paramContentResolver));
    paramContentResolver.registerContentObserver(Settings.Secure.getUriFor("serial_blacklist"), true, buildSerialObserver(paramContentResolver));
  }
  
  private static class BlacklistObserver
    extends ContentObserver
  {
    private final ContentResolver mContentResolver;
    private final String mKey;
    private final String mName;
    private final String mPath;
    private final File mTmpDir;
    
    public BlacklistObserver(String paramString1, String paramString2, String paramString3, ContentResolver paramContentResolver)
    {
      super();
      this.mKey = paramString1;
      this.mName = paramString2;
      this.mPath = paramString3;
      this.mTmpDir = new File(this.mPath).getParentFile();
      this.mContentResolver = paramContentResolver;
    }
    
    private void writeBlacklist()
    {
      new Thread("BlacklistUpdater")
      {
        /* Error */
        public void run()
        {
          // Byte code:
          //   0: aload_0
          //   1: getfield 18	com/android/server/CertBlacklister$BlacklistObserver$1:this$1	Lcom/android/server/CertBlacklister$BlacklistObserver;
          //   4: invokestatic 29	com/android/server/CertBlacklister$BlacklistObserver:-get1	(Lcom/android/server/CertBlacklister$BlacklistObserver;)Ljava/io/File;
          //   7: astore 4
          //   9: aload 4
          //   11: monitorenter
          //   12: aload_0
          //   13: getfield 18	com/android/server/CertBlacklister$BlacklistObserver$1:this$1	Lcom/android/server/CertBlacklister$BlacklistObserver;
          //   16: invokevirtual 33	com/android/server/CertBlacklister$BlacklistObserver:getValue	()Ljava/lang/String;
          //   19: astore 5
          //   21: aload 5
          //   23: ifnull +101 -> 124
          //   26: ldc 35
          //   28: ldc 37
          //   30: invokestatic 43	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
          //   33: pop
          //   34: aconst_null
          //   35: astore_2
          //   36: aconst_null
          //   37: astore_3
          //   38: aload_2
          //   39: astore_1
          //   40: ldc 45
          //   42: ldc 47
          //   44: aload_0
          //   45: getfield 18	com/android/server/CertBlacklister$BlacklistObserver$1:this$1	Lcom/android/server/CertBlacklister$BlacklistObserver;
          //   48: invokestatic 29	com/android/server/CertBlacklister$BlacklistObserver:-get1	(Lcom/android/server/CertBlacklister$BlacklistObserver;)Ljava/io/File;
          //   51: invokestatic 53	java/io/File:createTempFile	(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;)Ljava/io/File;
          //   54: astore 6
          //   56: aload_2
          //   57: astore_1
          //   58: aload 6
          //   60: iconst_1
          //   61: iconst_0
          //   62: invokevirtual 57	java/io/File:setReadable	(ZZ)Z
          //   65: pop
          //   66: aload_2
          //   67: astore_1
          //   68: new 59	java/io/FileOutputStream
          //   71: dup
          //   72: aload 6
          //   74: invokespecial 62	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
          //   77: astore_2
          //   78: aload_2
          //   79: aload 5
          //   81: invokevirtual 68	java/lang/String:getBytes	()[B
          //   84: invokevirtual 72	java/io/FileOutputStream:write	([B)V
          //   87: aload_2
          //   88: invokestatic 78	android/os/FileUtils:sync	(Ljava/io/FileOutputStream;)Z
          //   91: pop
          //   92: aload 6
          //   94: new 49	java/io/File
          //   97: dup
          //   98: aload_0
          //   99: getfield 18	com/android/server/CertBlacklister$BlacklistObserver$1:this$1	Lcom/android/server/CertBlacklister$BlacklistObserver;
          //   102: invokestatic 82	com/android/server/CertBlacklister$BlacklistObserver:-get0	(Lcom/android/server/CertBlacklister$BlacklistObserver;)Ljava/lang/String;
          //   105: invokespecial 83	java/io/File:<init>	(Ljava/lang/String;)V
          //   108: invokevirtual 87	java/io/File:renameTo	(Ljava/io/File;)Z
          //   111: pop
          //   112: ldc 35
          //   114: ldc 89
          //   116: invokestatic 43	android/util/Slog:i	(Ljava/lang/String;Ljava/lang/String;)I
          //   119: pop
          //   120: aload_2
          //   121: invokestatic 95	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
          //   124: aload 4
          //   126: monitorexit
          //   127: return
          //   128: astore_1
          //   129: aload_3
          //   130: astore_2
          //   131: aload_1
          //   132: astore_3
          //   133: aload_2
          //   134: astore_1
          //   135: ldc 35
          //   137: ldc 97
          //   139: aload_3
          //   140: invokestatic 101	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
          //   143: pop
          //   144: aload_2
          //   145: invokestatic 95	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
          //   148: goto -24 -> 124
          //   151: astore_1
          //   152: aload 4
          //   154: monitorexit
          //   155: aload_1
          //   156: athrow
          //   157: astore_2
          //   158: aload_1
          //   159: invokestatic 95	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
          //   162: aload_2
          //   163: athrow
          //   164: astore_3
          //   165: aload_2
          //   166: astore_1
          //   167: aload_3
          //   168: astore_2
          //   169: goto -11 -> 158
          //   172: astore_3
          //   173: goto -40 -> 133
          // Local variable table:
          //   start	length	slot	name	signature
          //   0	176	0	this	1
          //   39	29	1	localObject1	Object
          //   128	4	1	localIOException1	java.io.IOException
          //   134	1	1	localObject2	Object
          //   151	8	1	localAutoCloseable	AutoCloseable
          //   166	1	1	localObject3	Object
          //   35	110	2	localObject4	Object
          //   157	9	2	localObject5	Object
          //   168	1	2	localObject6	Object
          //   37	103	3	localObject7	Object
          //   164	4	3	localObject8	Object
          //   172	1	3	localIOException2	java.io.IOException
          //   7	146	4	localFile1	File
          //   19	61	5	str	String
          //   54	39	6	localFile2	File
          // Exception table:
          //   from	to	target	type
          //   40	56	128	java/io/IOException
          //   58	66	128	java/io/IOException
          //   68	78	128	java/io/IOException
          //   12	21	151	finally
          //   26	34	151	finally
          //   120	124	151	finally
          //   144	148	151	finally
          //   158	164	151	finally
          //   40	56	157	finally
          //   58	66	157	finally
          //   68	78	157	finally
          //   135	144	157	finally
          //   78	120	164	finally
          //   78	120	172	java/io/IOException
        }
      }.start();
    }
    
    public String getValue()
    {
      return Settings.Secure.getString(this.mContentResolver, this.mKey);
    }
    
    public void onChange(boolean paramBoolean)
    {
      super.onChange(paramBoolean);
      writeBlacklist();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/CertBlacklister.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */