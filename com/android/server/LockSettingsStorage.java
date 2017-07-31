package com.android.server;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import java.io.File;

class LockSettingsStorage
{
  private static final String BASE_ZERO_LOCK_PATTERN_FILE = "gatekeeper.gesture.key";
  private static final String CHILD_PROFILE_LOCK_FILE = "gatekeeper.profile.key";
  private static final String[] COLUMNS_FOR_PREFETCH = { "name", "value" };
  private static final String[] COLUMNS_FOR_QUERY = { "value" };
  private static final String COLUMN_KEY = "name";
  private static final String COLUMN_USERID = "user";
  private static final String COLUMN_VALUE = "value";
  private static final boolean DEBUG = false;
  private static final Object DEFAULT = new Object();
  private static final String LEGACY_LOCK_PASSWORD_FILE = "password.key";
  private static final String LEGACY_LOCK_PATTERN_FILE = "gesture.key";
  private static final String LOCK_PASSWORD_FILE = "gatekeeper.password.key";
  private static final String LOCK_PATTERN_FILE = "gatekeeper.pattern.key";
  private static final String SYSTEM_DIRECTORY = "/system/";
  private static final String TABLE = "locksettings";
  private static final String TAG = "LockSettingsStorage";
  private final Cache mCache = new Cache(null);
  private final Context mContext;
  private final Object mFileWriteLock = new Object();
  private final DatabaseHelper mOpenHelper;
  private SparseArray<Integer> mStoredCredentialType;
  
  public LockSettingsStorage(Context paramContext, Callback paramCallback)
  {
    this.mContext = paramContext;
    this.mOpenHelper = new DatabaseHelper(paramContext, paramCallback);
    this.mStoredCredentialType = new SparseArray();
  }
  
  private void clearPasswordHash(int paramInt)
  {
    writeFile(getLockPasswordFilename(paramInt), null);
  }
  
  private void clearPatternHash(int paramInt)
  {
    writeFile(getLockPatternFilename(paramInt), null);
  }
  
  private void deleteFile(String paramString)
  {
    synchronized (this.mFileWriteLock)
    {
      File localFile = new File(paramString);
      if (localFile.exists())
      {
        localFile.delete();
        this.mCache.putFile(paramString, null);
      }
      return;
    }
  }
  
  private String getBaseZeroLockPatternFilename(int paramInt)
  {
    return getLockCredentialFilePathForUser(paramInt, "gatekeeper.gesture.key");
  }
  
  private String getLockCredentialFilePathForUser(int paramInt, String paramString)
  {
    String str = Environment.getDataDirectory().getAbsolutePath() + "/system/";
    if (paramInt == 0) {
      return str + paramString;
    }
    return new File(Environment.getUserSystemDirectory(paramInt), paramString).getAbsolutePath();
  }
  
  private boolean hasFile(String paramString)
  {
    boolean bool2 = false;
    paramString = readFile(paramString);
    boolean bool1 = bool2;
    if (paramString != null)
    {
      bool1 = bool2;
      if (paramString.length > 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  /* Error */
  private byte[] readFile(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 96	com/android/server/LockSettingsStorage:mCache	Lcom/android/server/LockSettingsStorage$Cache;
    //   4: astore_3
    //   5: aload_3
    //   6: monitorenter
    //   7: aload_0
    //   8: getfield 96	com/android/server/LockSettingsStorage:mCache	Lcom/android/server/LockSettingsStorage$Cache;
    //   11: aload_1
    //   12: invokevirtual 182	com/android/server/LockSettingsStorage$Cache:hasFile	(Ljava/lang/String;)Z
    //   15: ifeq +16 -> 31
    //   18: aload_0
    //   19: getfield 96	com/android/server/LockSettingsStorage:mCache	Lcom/android/server/LockSettingsStorage$Cache;
    //   22: aload_1
    //   23: invokevirtual 185	com/android/server/LockSettingsStorage$Cache:peekFile	(Ljava/lang/String;)[B
    //   26: astore_1
    //   27: aload_3
    //   28: monitorexit
    //   29: aload_1
    //   30: areturn
    //   31: aload_0
    //   32: getfield 96	com/android/server/LockSettingsStorage:mCache	Lcom/android/server/LockSettingsStorage$Cache;
    //   35: invokestatic 189	com/android/server/LockSettingsStorage$Cache:-wrap0	(Lcom/android/server/LockSettingsStorage$Cache;)I
    //   38: istore_2
    //   39: aload_3
    //   40: monitorexit
    //   41: aconst_null
    //   42: astore_3
    //   43: aconst_null
    //   44: astore 6
    //   46: aconst_null
    //   47: astore 7
    //   49: aconst_null
    //   50: astore 4
    //   52: new 191	java/io/RandomAccessFile
    //   55: dup
    //   56: aload_1
    //   57: ldc -63
    //   59: invokespecial 196	java/io/RandomAccessFile:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   62: astore 5
    //   64: aload 7
    //   66: astore 4
    //   68: aload 5
    //   70: invokevirtual 200	java/io/RandomAccessFile:length	()J
    //   73: l2i
    //   74: newarray <illegal type>
    //   76: astore_3
    //   77: aload_3
    //   78: astore 4
    //   80: aload 5
    //   82: aload_3
    //   83: iconst_0
    //   84: aload_3
    //   85: arraylength
    //   86: invokevirtual 204	java/io/RandomAccessFile:readFully	([BII)V
    //   89: aload_3
    //   90: astore 4
    //   92: aload 5
    //   94: invokevirtual 207	java/io/RandomAccessFile:close	()V
    //   97: aload 5
    //   99: ifnull +8 -> 107
    //   102: aload 5
    //   104: invokevirtual 207	java/io/RandomAccessFile:close	()V
    //   107: aload_0
    //   108: getfield 96	com/android/server/LockSettingsStorage:mCache	Lcom/android/server/LockSettingsStorage$Cache;
    //   111: aload_1
    //   112: aload_3
    //   113: iload_2
    //   114: invokevirtual 211	com/android/server/LockSettingsStorage$Cache:putFileIfUnchanged	(Ljava/lang/String;[BI)V
    //   117: aload_3
    //   118: areturn
    //   119: astore_1
    //   120: aload_3
    //   121: monitorexit
    //   122: aload_1
    //   123: athrow
    //   124: astore 4
    //   126: ldc 64
    //   128: new 147	java/lang/StringBuilder
    //   131: dup
    //   132: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   135: ldc -43
    //   137: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   140: aload 4
    //   142: invokevirtual 216	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   145: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   148: invokestatic 222	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   151: pop
    //   152: goto -45 -> 107
    //   155: astore_3
    //   156: aload 6
    //   158: astore 5
    //   160: aload_3
    //   161: astore 6
    //   163: aload 5
    //   165: astore_3
    //   166: ldc 64
    //   168: new 147	java/lang/StringBuilder
    //   171: dup
    //   172: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   175: ldc -32
    //   177: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   180: aload 6
    //   182: invokevirtual 216	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   185: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   188: invokestatic 222	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   191: pop
    //   192: aload 4
    //   194: astore_3
    //   195: aload 5
    //   197: ifnull -90 -> 107
    //   200: aload 5
    //   202: invokevirtual 207	java/io/RandomAccessFile:close	()V
    //   205: aload 4
    //   207: astore_3
    //   208: goto -101 -> 107
    //   211: astore_3
    //   212: ldc 64
    //   214: new 147	java/lang/StringBuilder
    //   217: dup
    //   218: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   221: ldc -43
    //   223: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   226: aload_3
    //   227: invokevirtual 216	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   230: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   233: invokestatic 222	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   236: pop
    //   237: aload 4
    //   239: astore_3
    //   240: goto -133 -> 107
    //   243: astore_1
    //   244: aload_3
    //   245: ifnull +7 -> 252
    //   248: aload_3
    //   249: invokevirtual 207	java/io/RandomAccessFile:close	()V
    //   252: aload_1
    //   253: athrow
    //   254: astore_3
    //   255: ldc 64
    //   257: new 147	java/lang/StringBuilder
    //   260: dup
    //   261: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   264: ldc -43
    //   266: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   269: aload_3
    //   270: invokevirtual 216	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   273: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   276: invokestatic 222	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   279: pop
    //   280: goto -28 -> 252
    //   283: astore_1
    //   284: aload 5
    //   286: astore_3
    //   287: goto -43 -> 244
    //   290: astore 6
    //   292: goto -129 -> 163
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	295	0	this	LockSettingsStorage
    //   0	295	1	paramString	String
    //   38	76	2	i	int
    //   4	117	3	localObject1	Object
    //   155	6	3	localIOException1	java.io.IOException
    //   165	43	3	localObject2	Object
    //   211	16	3	localIOException2	java.io.IOException
    //   239	10	3	localObject3	Object
    //   254	16	3	localIOException3	java.io.IOException
    //   286	1	3	localObject4	Object
    //   50	41	4	localObject5	Object
    //   124	114	4	localIOException4	java.io.IOException
    //   62	223	5	localObject6	Object
    //   44	137	6	localIOException5	java.io.IOException
    //   290	1	6	localIOException6	java.io.IOException
    //   47	18	7	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   7	27	119	finally
    //   31	39	119	finally
    //   102	107	124	java/io/IOException
    //   52	64	155	java/io/IOException
    //   200	205	211	java/io/IOException
    //   52	64	243	finally
    //   166	192	243	finally
    //   248	252	254	java/io/IOException
    //   68	77	283	finally
    //   80	89	283	finally
    //   92	97	283	finally
    //   68	77	290	java/io/IOException
    //   80	89	290	java/io/IOException
    //   92	97	290	java/io/IOException
  }
  
  /* Error */
  private void writeFile(String paramString, byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 98	com/android/server/LockSettingsStorage:mFileWriteLock	Ljava/lang/Object;
    //   4: astore 7
    //   6: aload 7
    //   8: monitorenter
    //   9: aconst_null
    //   10: astore_3
    //   11: aconst_null
    //   12: astore 6
    //   14: new 191	java/io/RandomAccessFile
    //   17: dup
    //   18: aload_1
    //   19: ldc -30
    //   21: invokespecial 196	java/io/RandomAccessFile:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   24: astore 4
    //   26: aload_2
    //   27: ifnull +8 -> 35
    //   30: aload_2
    //   31: arraylength
    //   32: ifne +37 -> 69
    //   35: aload 4
    //   37: lconst_0
    //   38: invokevirtual 230	java/io/RandomAccessFile:setLength	(J)V
    //   41: aload 4
    //   43: invokevirtual 207	java/io/RandomAccessFile:close	()V
    //   46: aload 4
    //   48: ifnull +8 -> 56
    //   51: aload 4
    //   53: invokevirtual 207	java/io/RandomAccessFile:close	()V
    //   56: aload_0
    //   57: getfield 96	com/android/server/LockSettingsStorage:mCache	Lcom/android/server/LockSettingsStorage$Cache;
    //   60: aload_1
    //   61: aload_2
    //   62: invokevirtual 140	com/android/server/LockSettingsStorage$Cache:putFile	(Ljava/lang/String;[B)V
    //   65: aload 7
    //   67: monitorexit
    //   68: return
    //   69: aload 4
    //   71: aload_2
    //   72: iconst_0
    //   73: aload_2
    //   74: arraylength
    //   75: invokevirtual 233	java/io/RandomAccessFile:write	([BII)V
    //   78: goto -37 -> 41
    //   81: astore 5
    //   83: aload 4
    //   85: astore_3
    //   86: ldc 64
    //   88: new 147	java/lang/StringBuilder
    //   91: dup
    //   92: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   95: ldc -21
    //   97: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   100: aload 5
    //   102: invokevirtual 216	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   105: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   108: invokestatic 222	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   111: pop
    //   112: aload 4
    //   114: ifnull -58 -> 56
    //   117: aload 4
    //   119: invokevirtual 207	java/io/RandomAccessFile:close	()V
    //   122: goto -66 -> 56
    //   125: astore_3
    //   126: ldc 64
    //   128: new 147	java/lang/StringBuilder
    //   131: dup
    //   132: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   135: ldc -43
    //   137: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   140: aload_3
    //   141: invokevirtual 216	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   144: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   147: invokestatic 222	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   150: pop
    //   151: goto -95 -> 56
    //   154: astore_1
    //   155: aload 7
    //   157: monitorexit
    //   158: aload_1
    //   159: athrow
    //   160: astore_3
    //   161: ldc 64
    //   163: new 147	java/lang/StringBuilder
    //   166: dup
    //   167: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   170: ldc -43
    //   172: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   175: aload_3
    //   176: invokevirtual 216	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   179: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   182: invokestatic 222	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   185: pop
    //   186: goto -130 -> 56
    //   189: astore_1
    //   190: goto -35 -> 155
    //   193: astore_1
    //   194: aload_3
    //   195: ifnull +7 -> 202
    //   198: aload_3
    //   199: invokevirtual 207	java/io/RandomAccessFile:close	()V
    //   202: aload_1
    //   203: athrow
    //   204: astore_2
    //   205: ldc 64
    //   207: new 147	java/lang/StringBuilder
    //   210: dup
    //   211: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   214: ldc -43
    //   216: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   219: aload_2
    //   220: invokevirtual 216	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   223: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   226: invokestatic 222	android/util/Slog:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   229: pop
    //   230: goto -28 -> 202
    //   233: astore_1
    //   234: aload 4
    //   236: astore_3
    //   237: goto -43 -> 194
    //   240: astore 5
    //   242: aload 6
    //   244: astore 4
    //   246: goto -163 -> 83
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	249	0	this	LockSettingsStorage
    //   0	249	1	paramString	String
    //   0	249	2	paramArrayOfByte	byte[]
    //   10	76	3	localObject1	Object
    //   125	16	3	localIOException1	java.io.IOException
    //   160	39	3	localIOException2	java.io.IOException
    //   236	1	3	localObject2	Object
    //   24	221	4	localObject3	Object
    //   81	20	5	localIOException3	java.io.IOException
    //   240	1	5	localIOException4	java.io.IOException
    //   12	231	6	localObject4	Object
    //   4	152	7	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   30	35	81	java/io/IOException
    //   35	41	81	java/io/IOException
    //   41	46	81	java/io/IOException
    //   69	78	81	java/io/IOException
    //   117	122	125	java/io/IOException
    //   56	65	154	finally
    //   117	122	154	finally
    //   126	151	154	finally
    //   198	202	154	finally
    //   202	204	154	finally
    //   205	230	154	finally
    //   51	56	160	java/io/IOException
    //   51	56	189	finally
    //   161	186	189	finally
    //   14	26	193	finally
    //   86	112	193	finally
    //   198	202	204	java/io/IOException
    //   30	35	233	finally
    //   35	41	233	finally
    //   41	46	233	finally
    //   69	78	233	finally
    //   14	26	240	java/io/IOException
  }
  
  void clearCache()
  {
    this.mCache.clear();
  }
  
  void closeDatabase()
  {
    this.mOpenHelper.close();
  }
  
  String getChildProfileLockFile(int paramInt)
  {
    return getLockCredentialFilePathForUser(paramInt, "gatekeeper.profile.key");
  }
  
  String getLegacyLockPasswordFilename(int paramInt)
  {
    return getLockCredentialFilePathForUser(paramInt, "password.key");
  }
  
  String getLegacyLockPatternFilename(int paramInt)
  {
    return getLockCredentialFilePathForUser(paramInt, "gesture.key");
  }
  
  String getLockPasswordFilename(int paramInt)
  {
    return getLockCredentialFilePathForUser(paramInt, "gatekeeper.password.key");
  }
  
  String getLockPatternFilename(int paramInt)
  {
    return getLockCredentialFilePathForUser(paramInt, "gatekeeper.pattern.key");
  }
  
  public int getStoredCredentialType(int paramInt)
  {
    Object localObject = (Integer)this.mStoredCredentialType.get(paramInt);
    if (localObject != null) {
      return ((Integer)localObject).intValue();
    }
    int i;
    if (readPatternHash(paramInt) == null) {
      if (readPasswordHash(paramInt) != null) {
        i = 2;
      }
    }
    for (;;)
    {
      this.mStoredCredentialType.put(paramInt, Integer.valueOf(i));
      return i;
      i = -1;
      continue;
      localObject = readPasswordHash(paramInt);
      if (localObject != null)
      {
        if (((CredentialHash)localObject).version == 1) {
          i = 2;
        } else {
          i = 1;
        }
      }
      else {
        i = 1;
      }
    }
  }
  
  public boolean hasChildProfileLock(int paramInt)
  {
    return hasFile(getChildProfileLockFile(paramInt));
  }
  
  public boolean hasPassword(int paramInt)
  {
    if (!hasFile(getLockPasswordFilename(paramInt))) {
      return hasFile(getLegacyLockPasswordFilename(paramInt));
    }
    return true;
  }
  
  public boolean hasPattern(int paramInt)
  {
    if ((!hasFile(getLockPatternFilename(paramInt))) && (!hasFile(getBaseZeroLockPatternFilename(paramInt)))) {
      return hasFile(getLegacyLockPatternFilename(paramInt));
    }
    return true;
  }
  
  public void prefetchUser(int paramInt)
  {
    synchronized (this.mCache)
    {
      boolean bool = this.mCache.isFetched(paramInt);
      if (bool) {
        return;
      }
      this.mCache.setFetched(paramInt);
      int i = Cache.-wrap0(this.mCache);
      ??? = this.mOpenHelper.getReadableDatabase().query("locksettings", COLUMNS_FOR_PREFETCH, "user=?", new String[] { Integer.toString(paramInt) }, null, null, null);
      if (??? == null) {
        break label145;
      }
      if (((Cursor)???).moveToNext())
      {
        String str1 = ((Cursor)???).getString(0);
        String str2 = ((Cursor)???).getString(1);
        this.mCache.putKeyValueIfUnchanged(str1, str2, paramInt, i);
      }
    }
    ((Cursor)???).close();
    label145:
    readPasswordHash(paramInt);
    readPatternHash(paramInt);
  }
  
  public byte[] readChildProfileLock(int paramInt)
  {
    return readFile(getChildProfileLockFile(paramInt));
  }
  
  public String readKeyValue(String paramString1, String paramString2, int paramInt)
  {
    Object localObject2;
    synchronized (this.mCache)
    {
      if (this.mCache.hasKeyValue(paramString1, paramInt))
      {
        paramString1 = this.mCache.peekKeyValue(paramString1, paramString2, paramInt);
        return paramString1;
      }
      int i = Cache.-wrap0(this.mCache);
      ??? = DEFAULT;
      Cursor localCursor = this.mOpenHelper.getReadableDatabase().query("locksettings", COLUMNS_FOR_QUERY, "user=? AND name=?", new String[] { Integer.toString(paramInt), paramString1 }, null, null, null);
      localObject2 = ???;
      if (localCursor != null)
      {
        if (localCursor.moveToFirst()) {
          ??? = localCursor.getString(0);
        }
        localCursor.close();
        localObject2 = ???;
      }
      this.mCache.putKeyValueIfUnchanged(paramString1, localObject2, paramInt, i);
      if (localObject2 == DEFAULT) {
        return paramString2;
      }
    }
    return (String)localObject2;
  }
  
  public CredentialHash readPasswordHash(int paramInt)
  {
    byte[] arrayOfByte = readFile(getLockPasswordFilename(paramInt));
    if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
      return new CredentialHash(arrayOfByte, 1);
    }
    arrayOfByte = readFile(getLegacyLockPasswordFilename(paramInt));
    if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
      return new CredentialHash(arrayOfByte, 0);
    }
    return null;
  }
  
  public CredentialHash readPatternHash(int paramInt)
  {
    byte[] arrayOfByte = readFile(getLockPatternFilename(paramInt));
    if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
      return new CredentialHash(arrayOfByte, 1);
    }
    arrayOfByte = readFile(getBaseZeroLockPatternFilename(paramInt));
    if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
      return new CredentialHash(arrayOfByte, true);
    }
    arrayOfByte = readFile(getLegacyLockPatternFilename(paramInt));
    if ((arrayOfByte != null) && (arrayOfByte.length > 0)) {
      return new CredentialHash(arrayOfByte, 0);
    }
    return null;
  }
  
  public void removeChildProfileLock(int paramInt)
  {
    try
    {
      deleteFile(getChildProfileLockFile(paramInt));
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }
  
  /* Error */
  public void removeUser(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 105	com/android/server/LockSettingsStorage:mOpenHelper	Lcom/android/server/LockSettingsStorage$DatabaseHelper;
    //   4: invokevirtual 356	com/android/server/LockSettingsStorage$DatabaseHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   7: astore_2
    //   8: aload_0
    //   9: getfield 100	com/android/server/LockSettingsStorage:mContext	Landroid/content/Context;
    //   12: ldc 35
    //   14: invokevirtual 362	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   17: checkcast 364	android/os/UserManager
    //   20: iload_1
    //   21: invokevirtual 368	android/os/UserManager:getProfileParent	(I)Landroid/content/pm/UserInfo;
    //   24: ifnonnull +156 -> 180
    //   27: aload_0
    //   28: getfield 98	com/android/server/LockSettingsStorage:mFileWriteLock	Ljava/lang/Object;
    //   31: astore_3
    //   32: aload_3
    //   33: monitorenter
    //   34: aload_0
    //   35: iload_1
    //   36: invokevirtual 116	com/android/server/LockSettingsStorage:getLockPasswordFilename	(I)Ljava/lang/String;
    //   39: astore 4
    //   41: new 128	java/io/File
    //   44: dup
    //   45: aload 4
    //   47: invokespecial 130	java/io/File:<init>	(Ljava/lang/String;)V
    //   50: astore 5
    //   52: aload 5
    //   54: invokevirtual 134	java/io/File:exists	()Z
    //   57: ifeq +19 -> 76
    //   60: aload 5
    //   62: invokevirtual 137	java/io/File:delete	()Z
    //   65: pop
    //   66: aload_0
    //   67: getfield 96	com/android/server/LockSettingsStorage:mCache	Lcom/android/server/LockSettingsStorage$Cache;
    //   70: aload 4
    //   72: aconst_null
    //   73: invokevirtual 140	com/android/server/LockSettingsStorage$Cache:putFile	(Ljava/lang/String;[B)V
    //   76: aload_0
    //   77: iload_1
    //   78: invokevirtual 124	com/android/server/LockSettingsStorage:getLockPatternFilename	(I)Ljava/lang/String;
    //   81: astore 4
    //   83: new 128	java/io/File
    //   86: dup
    //   87: aload 4
    //   89: invokespecial 130	java/io/File:<init>	(Ljava/lang/String;)V
    //   92: astore 5
    //   94: aload 5
    //   96: invokevirtual 134	java/io/File:exists	()Z
    //   99: ifeq +19 -> 118
    //   102: aload 5
    //   104: invokevirtual 137	java/io/File:delete	()Z
    //   107: pop
    //   108: aload_0
    //   109: getfield 96	com/android/server/LockSettingsStorage:mCache	Lcom/android/server/LockSettingsStorage$Cache;
    //   112: aload 4
    //   114: aconst_null
    //   115: invokevirtual 140	com/android/server/LockSettingsStorage$Cache:putFile	(Ljava/lang/String;[B)V
    //   118: aload_3
    //   119: monitorexit
    //   120: aload_2
    //   121: invokevirtual 371	android/database/sqlite/SQLiteDatabase:beginTransaction	()V
    //   124: aload_2
    //   125: ldc 61
    //   127: new 147	java/lang/StringBuilder
    //   130: dup
    //   131: invokespecial 148	java/lang/StringBuilder:<init>	()V
    //   134: ldc_w 373
    //   137: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   140: iload_1
    //   141: invokevirtual 376	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   144: ldc_w 378
    //   147: invokevirtual 162	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   150: invokevirtual 165	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   153: aconst_null
    //   154: invokevirtual 381	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   157: pop
    //   158: aload_2
    //   159: invokevirtual 384	android/database/sqlite/SQLiteDatabase:setTransactionSuccessful	()V
    //   162: aload_0
    //   163: getfield 96	com/android/server/LockSettingsStorage:mCache	Lcom/android/server/LockSettingsStorage$Cache;
    //   166: iload_1
    //   167: invokevirtual 386	com/android/server/LockSettingsStorage$Cache:removeUser	(I)V
    //   170: aload_2
    //   171: invokevirtual 389	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   174: return
    //   175: astore_2
    //   176: aload_3
    //   177: monitorexit
    //   178: aload_2
    //   179: athrow
    //   180: aload_0
    //   181: iload_1
    //   182: invokevirtual 391	com/android/server/LockSettingsStorage:removeChildProfileLock	(I)V
    //   185: goto -65 -> 120
    //   188: astore_3
    //   189: aload_2
    //   190: invokevirtual 389	android/database/sqlite/SQLiteDatabase:endTransaction	()V
    //   193: aload_3
    //   194: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	195	0	this	LockSettingsStorage
    //   0	195	1	paramInt	int
    //   7	164	2	localSQLiteDatabase	SQLiteDatabase
    //   175	15	2	localObject1	Object
    //   31	146	3	localObject2	Object
    //   188	6	3	localObject3	Object
    //   39	74	4	str	String
    //   50	53	5	localFile	File
    // Exception table:
    //   from	to	target	type
    //   34	76	175	finally
    //   76	118	175	finally
    //   120	170	188	finally
  }
  
  public void writeChildProfileLock(int paramInt, byte[] paramArrayOfByte)
  {
    writeFile(getChildProfileLockFile(paramInt), paramArrayOfByte);
  }
  
  public void writeKeyValue(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, int paramInt)
  {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put("name", paramString1);
    localContentValues.put("user", Integer.valueOf(paramInt));
    localContentValues.put("value", paramString2);
    paramSQLiteDatabase.beginTransaction();
    try
    {
      paramSQLiteDatabase.delete("locksettings", "name=? AND user=?", new String[] { paramString1, Integer.toString(paramInt) });
      paramSQLiteDatabase.insert("locksettings", null, localContentValues);
      paramSQLiteDatabase.setTransactionSuccessful();
      this.mCache.putKeyValue(paramString1, paramString2, paramInt);
      return;
    }
    finally
    {
      paramSQLiteDatabase.endTransaction();
    }
  }
  
  public void writeKeyValue(String paramString1, String paramString2, int paramInt)
  {
    writeKeyValue(this.mOpenHelper.getWritableDatabase(), paramString1, paramString2, paramInt);
  }
  
  public void writePasswordHash(byte[] paramArrayOfByte, int paramInt)
  {
    SparseArray localSparseArray = this.mStoredCredentialType;
    if (paramArrayOfByte == null) {}
    for (int i = -1;; i = 2)
    {
      localSparseArray.put(paramInt, Integer.valueOf(i));
      writeFile(getLockPasswordFilename(paramInt), paramArrayOfByte);
      clearPatternHash(paramInt);
      return;
    }
  }
  
  public void writePatternHash(byte[] paramArrayOfByte, int paramInt)
  {
    SparseArray localSparseArray = this.mStoredCredentialType;
    if (paramArrayOfByte == null) {}
    for (int i = -1;; i = 1)
    {
      localSparseArray.put(paramInt, Integer.valueOf(i));
      writeFile(getLockPatternFilename(paramInt), paramArrayOfByte);
      clearPasswordHash(paramInt);
      return;
    }
  }
  
  private static class Cache
  {
    private final ArrayMap<CacheKey, Object> mCache = new ArrayMap();
    private final CacheKey mCacheKey = new CacheKey(null);
    private int mVersion = 0;
    
    private boolean contains(int paramInt1, String paramString, int paramInt2)
    {
      try
      {
        boolean bool = this.mCache.containsKey(this.mCacheKey.set(paramInt1, paramString, paramInt2));
        return bool;
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    private int getVersion()
    {
      try
      {
        int i = this.mVersion;
        return i;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    private Object peek(int paramInt1, String paramString, int paramInt2)
    {
      try
      {
        paramString = this.mCache.get(this.mCacheKey.set(paramInt1, paramString, paramInt2));
        return paramString;
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    private void put(int paramInt1, String paramString, Object paramObject, int paramInt2)
    {
      try
      {
        this.mCache.put(new CacheKey(null).set(paramInt1, paramString, paramInt2), paramObject);
        this.mVersion += 1;
        return;
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    private void putIfUnchanged(int paramInt1, String paramString, Object paramObject, int paramInt2, int paramInt3)
    {
      try
      {
        if ((!contains(paramInt1, paramString, paramInt2)) && (this.mVersion == paramInt3)) {
          put(paramInt1, paramString, paramObject, paramInt2);
        }
        return;
      }
      finally
      {
        paramString = finally;
        throw paramString;
      }
    }
    
    void clear()
    {
      try
      {
        this.mCache.clear();
        this.mVersion += 1;
        return;
      }
      finally
      {
        localObject = finally;
        throw ((Throwable)localObject);
      }
    }
    
    boolean hasFile(String paramString)
    {
      return contains(1, paramString, -1);
    }
    
    boolean hasKeyValue(String paramString, int paramInt)
    {
      return contains(0, paramString, paramInt);
    }
    
    boolean isFetched(int paramInt)
    {
      return contains(2, "", paramInt);
    }
    
    byte[] peekFile(String paramString)
    {
      return (byte[])peek(1, paramString, -1);
    }
    
    String peekKeyValue(String paramString1, String paramString2, int paramInt)
    {
      paramString1 = peek(0, paramString1, paramInt);
      if (paramString1 == LockSettingsStorage.-get0()) {
        return paramString2;
      }
      return (String)paramString1;
    }
    
    void putFile(String paramString, byte[] paramArrayOfByte)
    {
      put(1, paramString, paramArrayOfByte, -1);
    }
    
    void putFileIfUnchanged(String paramString, byte[] paramArrayOfByte, int paramInt)
    {
      putIfUnchanged(1, paramString, paramArrayOfByte, -1, paramInt);
    }
    
    void putKeyValue(String paramString1, String paramString2, int paramInt)
    {
      put(0, paramString1, paramString2, paramInt);
    }
    
    void putKeyValueIfUnchanged(String paramString, Object paramObject, int paramInt1, int paramInt2)
    {
      putIfUnchanged(0, paramString, paramObject, paramInt1, paramInt2);
    }
    
    void removeUser(int paramInt)
    {
      for (;;)
      {
        int i;
        try
        {
          i = this.mCache.size() - 1;
          if (i >= 0)
          {
            if (((CacheKey)this.mCache.keyAt(i)).userId == paramInt) {
              this.mCache.removeAt(i);
            }
          }
          else
          {
            this.mVersion += 1;
            return;
          }
        }
        finally {}
        i -= 1;
      }
    }
    
    void setFetched(int paramInt)
    {
      put(2, "isFetched", "true", paramInt);
    }
    
    private static final class CacheKey
    {
      static final int TYPE_FETCHED = 2;
      static final int TYPE_FILE = 1;
      static final int TYPE_KEY_VALUE = 0;
      String key;
      int type;
      int userId;
      
      public boolean equals(Object paramObject)
      {
        boolean bool2 = false;
        if (!(paramObject instanceof CacheKey)) {
          return false;
        }
        paramObject = (CacheKey)paramObject;
        boolean bool1 = bool2;
        if (this.userId == ((CacheKey)paramObject).userId)
        {
          bool1 = bool2;
          if (this.type == ((CacheKey)paramObject).type) {
            bool1 = this.key.equals(((CacheKey)paramObject).key);
          }
        }
        return bool1;
      }
      
      public int hashCode()
      {
        return this.key.hashCode() ^ this.userId ^ this.type;
      }
      
      public CacheKey set(int paramInt1, String paramString, int paramInt2)
      {
        this.type = paramInt1;
        this.key = paramString;
        this.userId = paramInt2;
        return this;
      }
    }
  }
  
  public static abstract interface Callback
  {
    public abstract void initialize(SQLiteDatabase paramSQLiteDatabase);
  }
  
  static class CredentialHash
  {
    static final int TYPE_NONE = -1;
    static final int TYPE_PASSWORD = 2;
    static final int TYPE_PATTERN = 1;
    static final int VERSION_GATEKEEPER = 1;
    static final int VERSION_LEGACY = 0;
    byte[] hash;
    boolean isBaseZeroPattern;
    int version;
    
    CredentialHash(byte[] paramArrayOfByte, int paramInt)
    {
      this.hash = paramArrayOfByte;
      this.version = paramInt;
      this.isBaseZeroPattern = false;
    }
    
    CredentialHash(byte[] paramArrayOfByte, boolean paramBoolean)
    {
      this.hash = paramArrayOfByte;
      this.version = 1;
      this.isBaseZeroPattern = paramBoolean;
    }
  }
  
  class DatabaseHelper
    extends SQLiteOpenHelper
  {
    private static final String DATABASE_NAME = "locksettings.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "LockSettingsDB";
    private final LockSettingsStorage.Callback mCallback;
    
    public DatabaseHelper(Context paramContext, LockSettingsStorage.Callback paramCallback)
    {
      super("locksettings.db", null, 2);
      setWriteAheadLoggingEnabled(true);
      this.mCallback = paramCallback;
    }
    
    private void createTable(SQLiteDatabase paramSQLiteDatabase)
    {
      paramSQLiteDatabase.execSQL("CREATE TABLE locksettings (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT,user INTEGER,value TEXT);");
    }
    
    public void onCreate(SQLiteDatabase paramSQLiteDatabase)
    {
      createTable(paramSQLiteDatabase);
      this.mCallback.initialize(paramSQLiteDatabase);
    }
    
    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
    {
      paramInt2 = paramInt1;
      if (paramInt1 == 1) {
        paramInt2 = 2;
      }
      if (paramInt2 != 2) {
        Log.w("LockSettingsDB", "Failed to upgrade database!");
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/LockSettingsStorage.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */