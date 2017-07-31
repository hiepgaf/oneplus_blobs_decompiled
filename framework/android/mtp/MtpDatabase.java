package android.mtp;

import android.content.BroadcastReceiver;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.MediaScanner;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.MediaStore.Audio.Playlists;
import android.provider.MediaStore.Files;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import dalvik.system.CloseGuard;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class MtpDatabase
  implements AutoCloseable
{
  static final int[] AUDIO_PROPERTIES;
  private static final int DEVICE_PROPERTIES_DATABASE_VERSION = 1;
  static final int[] FILE_PROPERTIES;
  private static final String FORMAT_PARENT_WHERE = "format=? AND parent=?";
  private static final String[] FORMAT_PROJECTION;
  private static final String FORMAT_WHERE = "format=?";
  private static final String[] ID_PROJECTION = { "_id" };
  private static final String ID_WHERE = "_id=?";
  static final int[] IMAGE_PROPERTIES = { 56321, 56322, 56323, 56324, 56327, 56329, 56331, 56385, 56388, 56544, 56398, 56392 };
  private static final String[] OBJECT_INFO_PROJECTION;
  private static final String PARENT_WHERE = "parent=?";
  private static final String[] PATH_FORMAT_PROJECTION;
  private static final String[] PATH_PROJECTION = { "_id", "_data" };
  private static final String PATH_WHERE = "_data=?";
  private static final String STORAGE_FORMAT_PARENT_WHERE = "storage_id=? AND format=? AND parent=?";
  private static final String STORAGE_FORMAT_WHERE = "storage_id=? AND format=?";
  private static final String STORAGE_PARENT_WHERE = "storage_id=? AND parent=?";
  private static final String STORAGE_WHERE = "storage_id=?";
  private static final String TAG = "MtpDatabase";
  static final int[] VIDEO_PROPERTIES;
  private int mBatteryLevel;
  private BroadcastReceiver mBatteryReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent.getAction().equals("android.intent.action.BATTERY_CHANGED"))
      {
        MtpDatabase.-set1(MtpDatabase.this, paramAnonymousIntent.getIntExtra("scale", 0));
        int i = paramAnonymousIntent.getIntExtra("level", 0);
        if (i != MtpDatabase.-get0(MtpDatabase.this))
        {
          MtpDatabase.-set0(MtpDatabase.this, i);
          if (MtpDatabase.-get1(MtpDatabase.this) != null) {
            MtpDatabase.-get1(MtpDatabase.this).sendDevicePropertyChanged(20481);
          }
        }
      }
    }
  };
  private int mBatteryScale;
  private final CloseGuard mCloseGuard = CloseGuard.get();
  private final AtomicBoolean mClosed = new AtomicBoolean();
  private final Context mContext;
  private boolean mDatabaseModified;
  private SharedPreferences mDeviceProperties;
  private final ContentProviderClient mMediaProvider;
  private final MediaScanner mMediaScanner;
  private final String mMediaStoragePath;
  private long mNativeContext;
  private final Uri mObjectsUri;
  private final String mPackageName;
  private final HashMap<Integer, MtpPropertyGroup> mPropertyGroupsByFormat = new HashMap();
  private final HashMap<Integer, MtpPropertyGroup> mPropertyGroupsByProperty = new HashMap();
  private MtpServer mServer;
  private final HashMap<String, MtpStorage> mStorageMap = new HashMap();
  private final String[] mSubDirectories;
  private String mSubDirectoriesWhere;
  private String[] mSubDirectoriesWhereArgs;
  private final String mVolumeName;
  
  static
  {
    FORMAT_PROJECTION = new String[] { "_id", "format" };
    PATH_FORMAT_PROJECTION = new String[] { "_id", "_data", "format" };
    OBJECT_INFO_PROJECTION = new String[] { "_id", "storage_id", "format", "parent", "_data", "date_added", "date_modified" };
    System.loadLibrary("media_jni");
    FILE_PROPERTIES = new int[] { 56321, 56322, 56323, 56324, 56327, 56329, 56331, 56385, 56388, 56544, 56398 };
    AUDIO_PROPERTIES = new int[] { 56321, 56322, 56323, 56324, 56327, 56329, 56331, 56385, 56388, 56544, 56398, 56390, 56474, 56475, 56459, 56473, 56457, 56460, 56470, 56985, 56978, 56986, 56980, 56979 };
    VIDEO_PROPERTIES = new int[] { 56321, 56322, 56323, 56324, 56327, 56329, 56331, 56385, 56388, 56544, 56398, 56390, 56474, 56457, 56392 };
  }
  
  public MtpDatabase(Context paramContext, String paramString1, String paramString2, String[] paramArrayOfString)
  {
    native_setup();
    this.mContext = paramContext;
    this.mPackageName = paramContext.getPackageName();
    this.mMediaProvider = paramContext.getContentResolver().acquireContentProviderClient("media");
    this.mVolumeName = paramString1;
    this.mMediaStoragePath = paramString2;
    this.mObjectsUri = MediaStore.Files.getMtpObjectsUri(paramString1);
    this.mMediaScanner = new MediaScanner(paramContext, this.mVolumeName);
    this.mSubDirectories = paramArrayOfString;
    if (paramArrayOfString != null)
    {
      paramString1 = new StringBuilder();
      paramString1.append("(");
      int k = paramArrayOfString.length;
      int i = 0;
      while (i < k)
      {
        paramString1.append("_data=? OR _data LIKE ?");
        if (i != k - 1) {
          paramString1.append(" OR ");
        }
        i += 1;
      }
      paramString1.append(")");
      this.mSubDirectoriesWhere = paramString1.toString();
      this.mSubDirectoriesWhereArgs = new String[k * 2];
      i = 0;
      int j = 0;
      while (i < k)
      {
        paramString1 = paramArrayOfString[i];
        paramString2 = this.mSubDirectoriesWhereArgs;
        int m = j + 1;
        paramString2[j] = paramString1;
        paramString2 = this.mSubDirectoriesWhereArgs;
        j = m + 1;
        paramString2[m] = (paramString1 + "/%");
        i += 1;
      }
    }
    initDeviceProperties(paramContext);
    this.mCloseGuard.open("close");
  }
  
  /* Error */
  private int beginSendObject(String paramString, int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial 292	android/mtp/MtpDatabase:inStorageRoot	(Ljava/lang/String;)Z
    //   5: ifne +31 -> 36
    //   8: ldc 52
    //   10: new 253	java/lang/StringBuilder
    //   13: dup
    //   14: invokespecial 254	java/lang/StringBuilder:<init>	()V
    //   17: ldc_w 294
    //   20: invokevirtual 260	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   23: aload_1
    //   24: invokevirtual 260	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   27: invokevirtual 269	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   30: invokestatic 300	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   33: pop
    //   34: iconst_m1
    //   35: ireturn
    //   36: aload_0
    //   37: aload_1
    //   38: invokespecial 303	android/mtp/MtpDatabase:inStorageSubDirectory	(Ljava/lang/String;)Z
    //   41: ifne +5 -> 46
    //   44: iconst_m1
    //   45: ireturn
    //   46: aload_1
    //   47: ifnull +120 -> 167
    //   50: aconst_null
    //   51: astore 10
    //   53: aconst_null
    //   54: astore 9
    //   56: aload_0
    //   57: getfield 230	android/mtp/MtpDatabase:mMediaProvider	Landroid/content/ContentProviderClient;
    //   60: aload_0
    //   61: getfield 242	android/mtp/MtpDatabase:mObjectsUri	Landroid/net/Uri;
    //   64: getstatic 111	android/mtp/MtpDatabase:ID_PROJECTION	[Ljava/lang/String;
    //   67: ldc 37
    //   69: iconst_1
    //   70: anewarray 107	java/lang/String
    //   73: dup
    //   74: iconst_0
    //   75: aload_1
    //   76: aastore
    //   77: aconst_null
    //   78: aconst_null
    //   79: invokevirtual 309	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Landroid/os/CancellationSignal;)Landroid/database/Cursor;
    //   82: astore 11
    //   84: aload 11
    //   86: ifnull +69 -> 155
    //   89: aload 11
    //   91: astore 9
    //   93: aload 11
    //   95: astore 10
    //   97: aload 11
    //   99: invokeinterface 315 1 0
    //   104: ifle +51 -> 155
    //   107: aload 11
    //   109: astore 9
    //   111: aload 11
    //   113: astore 10
    //   115: ldc 52
    //   117: new 253	java/lang/StringBuilder
    //   120: dup
    //   121: invokespecial 254	java/lang/StringBuilder:<init>	()V
    //   124: ldc_w 317
    //   127: invokevirtual 260	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   130: aload_1
    //   131: invokevirtual 260	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   134: invokevirtual 269	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   137: invokestatic 320	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   140: pop
    //   141: aload 11
    //   143: ifnull +10 -> 153
    //   146: aload 11
    //   148: invokeinterface 322 1 0
    //   153: iconst_m1
    //   154: ireturn
    //   155: aload 11
    //   157: ifnull +10 -> 167
    //   160: aload 11
    //   162: invokeinterface 322 1 0
    //   167: aload_0
    //   168: iconst_1
    //   169: putfield 324	android/mtp/MtpDatabase:mDatabaseModified	Z
    //   172: new 326	android/content/ContentValues
    //   175: dup
    //   176: invokespecial 327	android/content/ContentValues:<init>	()V
    //   179: astore 9
    //   181: aload 9
    //   183: ldc 113
    //   185: aload_1
    //   186: invokevirtual 331	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   189: aload 9
    //   191: ldc 117
    //   193: iload_2
    //   194: invokestatic 337	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   197: invokevirtual 340	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   200: aload 9
    //   202: ldc 125
    //   204: iload_3
    //   205: invokestatic 337	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   208: invokevirtual 340	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   211: aload 9
    //   213: ldc 123
    //   215: iload 4
    //   217: invokestatic 337	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   220: invokevirtual 340	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   223: aload 9
    //   225: ldc_w 342
    //   228: lload 5
    //   230: invokestatic 347	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   233: invokevirtual 350	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   236: aload 9
    //   238: ldc -127
    //   240: lload 7
    //   242: invokestatic 347	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   245: invokevirtual 350	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   248: aload_0
    //   249: getfield 230	android/mtp/MtpDatabase:mMediaProvider	Landroid/content/ContentProviderClient;
    //   252: aload_0
    //   253: getfield 242	android/mtp/MtpDatabase:mObjectsUri	Landroid/net/Uri;
    //   256: aload 9
    //   258: invokevirtual 354	android/content/ContentProviderClient:insert	(Landroid/net/Uri;Landroid/content/ContentValues;)Landroid/net/Uri;
    //   261: astore_1
    //   262: aload_1
    //   263: ifnull +69 -> 332
    //   266: aload_1
    //   267: invokevirtual 360	android/net/Uri:getPathSegments	()Ljava/util/List;
    //   270: iconst_2
    //   271: invokeinterface 365 2 0
    //   276: checkcast 107	java/lang/String
    //   279: invokestatic 369	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   282: istore_2
    //   283: iload_2
    //   284: ireturn
    //   285: astore 11
    //   287: aload 9
    //   289: astore 10
    //   291: ldc 52
    //   293: ldc_w 371
    //   296: aload 11
    //   298: invokestatic 374	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   301: pop
    //   302: aload 9
    //   304: ifnull -137 -> 167
    //   307: aload 9
    //   309: invokeinterface 322 1 0
    //   314: goto -147 -> 167
    //   317: astore_1
    //   318: aload 10
    //   320: ifnull +10 -> 330
    //   323: aload 10
    //   325: invokeinterface 322 1 0
    //   330: aload_1
    //   331: athrow
    //   332: iconst_m1
    //   333: ireturn
    //   334: astore_1
    //   335: ldc 52
    //   337: ldc_w 371
    //   340: aload_1
    //   341: invokestatic 374	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   344: pop
    //   345: iconst_m1
    //   346: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	347	0	this	MtpDatabase
    //   0	347	1	paramString	String
    //   0	347	2	paramInt1	int
    //   0	347	3	paramInt2	int
    //   0	347	4	paramInt3	int
    //   0	347	5	paramLong1	long
    //   0	347	7	paramLong2	long
    //   54	254	9	localObject1	Object
    //   51	273	10	localObject2	Object
    //   82	79	11	localCursor	Cursor
    //   285	12	11	localRemoteException	RemoteException
    // Exception table:
    //   from	to	target	type
    //   56	84	285	android/os/RemoteException
    //   97	107	285	android/os/RemoteException
    //   115	141	285	android/os/RemoteException
    //   56	84	317	finally
    //   97	107	317	finally
    //   115	141	317	finally
    //   291	302	317	finally
    //   248	262	334	android/os/RemoteException
    //   266	283	334	android/os/RemoteException
  }
  
  private Cursor createObjectQuery(int paramInt1, int paramInt2, int paramInt3)
    throws RemoteException
  {
    String str1;
    String[] arrayOfString1;
    String str2;
    String[] arrayOfString2;
    if (paramInt1 == -1) {
      if (paramInt2 == 0) {
        if (paramInt3 == 0)
        {
          str1 = null;
          arrayOfString1 = null;
          str2 = str1;
          arrayOfString2 = arrayOfString1;
          if (this.mSubDirectoriesWhere != null)
          {
            if (str1 != null) {
              break label314;
            }
            str2 = this.mSubDirectoriesWhere;
            arrayOfString2 = this.mSubDirectoriesWhereArgs;
          }
        }
      }
    }
    for (;;)
    {
      return this.mMediaProvider.query(this.mObjectsUri, ID_PROJECTION, str2, arrayOfString2, null, null);
      paramInt1 = paramInt3;
      if (paramInt3 == -1) {
        paramInt1 = 0;
      }
      str1 = "parent=?";
      arrayOfString1 = new String[1];
      arrayOfString1[0] = Integer.toString(paramInt1);
      break;
      if (paramInt3 == 0)
      {
        str1 = "format=?";
        arrayOfString1 = new String[1];
        arrayOfString1[0] = Integer.toString(paramInt2);
        break;
      }
      paramInt1 = paramInt3;
      if (paramInt3 == -1) {
        paramInt1 = 0;
      }
      str1 = "format=? AND parent=?";
      arrayOfString1 = new String[2];
      arrayOfString1[0] = Integer.toString(paramInt2);
      arrayOfString1[1] = Integer.toString(paramInt1);
      break;
      if (paramInt2 == 0)
      {
        if (paramInt3 == 0)
        {
          str1 = "storage_id=?";
          arrayOfString1 = new String[1];
          arrayOfString1[0] = Integer.toString(paramInt1);
          break;
        }
        paramInt2 = paramInt3;
        if (paramInt3 == -1) {
          paramInt2 = 0;
        }
        str1 = "storage_id=? AND parent=?";
        arrayOfString1 = new String[2];
        arrayOfString1[0] = Integer.toString(paramInt1);
        arrayOfString1[1] = Integer.toString(paramInt2);
        break;
      }
      if (paramInt3 == 0)
      {
        str1 = "storage_id=? AND format=?";
        arrayOfString1 = new String[2];
        arrayOfString1[0] = Integer.toString(paramInt1);
        arrayOfString1[1] = Integer.toString(paramInt2);
        break;
      }
      int i = paramInt3;
      if (paramInt3 == -1) {
        i = 0;
      }
      str1 = "storage_id=? AND format=? AND parent=?";
      arrayOfString1 = new String[3];
      arrayOfString1[0] = Integer.toString(paramInt1);
      arrayOfString1[1] = Integer.toString(paramInt2);
      arrayOfString1[2] = Integer.toString(i);
      break;
      label314:
      str2 = str1 + " AND " + this.mSubDirectoriesWhere;
      arrayOfString2 = new String[arrayOfString1.length + this.mSubDirectoriesWhereArgs.length];
      paramInt1 = 0;
      while (paramInt1 < arrayOfString1.length)
      {
        arrayOfString2[paramInt1] = arrayOfString1[paramInt1];
        paramInt1 += 1;
      }
      paramInt2 = 0;
      while (paramInt2 < this.mSubDirectoriesWhereArgs.length)
      {
        arrayOfString2[paramInt1] = this.mSubDirectoriesWhereArgs[paramInt2];
        paramInt1 += 1;
        paramInt2 += 1;
      }
    }
  }
  
  private int deleteFile(int paramInt)
  {
    this.mDatabaseModified = true;
    Object localObject1 = null;
    Object localObject3 = null;
    try
    {
      localCursor = this.mMediaProvider.query(this.mObjectsUri, PATH_FORMAT_PROJECTION, "_id=?", new String[] { Integer.toString(paramInt) }, null, null);
      int i;
      if (localCursor != null)
      {
        localObject3 = localCursor;
        localObject1 = localCursor;
        if (localCursor.moveToNext())
        {
          localObject3 = localCursor;
          localObject1 = localCursor;
          str = localCursor.getString(1);
          localObject3 = localCursor;
          localObject1 = localCursor;
          i = localCursor.getInt(2);
          if ((str != null) && (i != 0)) {
            break label141;
          }
          if (localCursor != null) {
            localCursor.close();
          }
          return 8194;
        }
      }
      if (localCursor != null) {
        localCursor.close();
      }
      return 8201;
      label141:
      localObject3 = localCursor;
      localObject1 = localCursor;
      boolean bool = isStorageSubDirectory(str);
      if (bool)
      {
        if (localCursor != null) {
          localCursor.close();
        }
        return 8205;
      }
      if (i == 12289)
      {
        localObject3 = localCursor;
        localObject1 = localCursor;
        localUri = MediaStore.Files.getMtpObjectsUri(this.mVolumeName);
        localObject3 = localCursor;
        localObject1 = localCursor;
        this.mMediaProvider.delete(localUri, "_data LIKE ?1 AND lower(substr(_data,1,?2))=lower(?3)", new String[] { str + "/%", Integer.toString(str.length() + 1), str + "/" });
      }
      localObject3 = localCursor;
      localObject1 = localCursor;
      Uri localUri = MediaStore.Files.getMtpObjectsUri(this.mVolumeName, paramInt);
      localObject3 = localCursor;
      localObject1 = localCursor;
      if (this.mMediaProvider.delete(localUri, null, null) > 0)
      {
        if (i != 12289)
        {
          localObject3 = localCursor;
          localObject1 = localCursor;
          bool = str.toLowerCase(Locale.US).endsWith("/.nomedia");
          if (bool) {
            localObject1 = localCursor;
          }
        }
        try
        {
          localObject3 = str.substring(0, str.lastIndexOf("/"));
          localObject1 = localCursor;
          this.mMediaProvider.call("unhide", (String)localObject3, null);
        }
        catch (RemoteException localRemoteException1)
        {
          for (;;)
          {
            localObject3 = localCursor;
            localObject2 = localCursor;
            Log.e("MtpDatabase", "failed to unhide/rescan for " + str);
          }
        }
        if (localCursor != null) {
          localCursor.close();
        }
        return 8193;
      }
    }
    catch (RemoteException localRemoteException2)
    {
      Cursor localCursor;
      String str;
      localObject2 = localObject3;
      Log.e("MtpDatabase", "RemoteException in deleteFile", localRemoteException2);
      return 8194;
      if (localRemoteException2 != null) {
        localRemoteException2.close();
      }
      return 8201;
    }
    finally
    {
      Object localObject2;
      if (localObject2 != null) {
        ((Cursor)localObject2).close();
      }
    }
  }
  
  private void endSendObject(String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (paramInt2 == 47621)
      {
        Object localObject1 = paramString;
        int i = paramString.lastIndexOf('/');
        if (i >= 0) {
          localObject1 = paramString.substring(i + 1);
        }
        Object localObject2 = localObject1;
        if (((String)localObject1).endsWith(".pla")) {
          localObject2 = ((String)localObject1).substring(0, ((String)localObject1).length() - 4);
        }
        localObject1 = new ContentValues(1);
        ((ContentValues)localObject1).put("_data", paramString);
        ((ContentValues)localObject1).put("name", (String)localObject2);
        ((ContentValues)localObject1).put("format", Integer.valueOf(paramInt2));
        ((ContentValues)localObject1).put("date_modified", Long.valueOf(System.currentTimeMillis() / 1000L));
        ((ContentValues)localObject1).put("media_scanner_new_object_id", Integer.valueOf(paramInt1));
        try
        {
          this.mMediaProvider.insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, (ContentValues)localObject1);
          return;
        }
        catch (RemoteException paramString)
        {
          Log.e("MtpDatabase", "RemoteException in endSendObject", paramString);
          return;
        }
      }
      this.mMediaScanner.scanMtpFile(paramString, paramInt1, paramInt2);
      return;
    }
    deleteFile(paramInt1);
  }
  
  private int getDeviceProperty(int paramInt, long[] paramArrayOfLong, char[] paramArrayOfChar)
  {
    switch (paramInt)
    {
    default: 
      return 8202;
    case 54273: 
    case 54274: 
      paramArrayOfLong = this.mDeviceProperties.getString(Integer.toString(paramInt), "");
      i = paramArrayOfLong.length();
      paramInt = i;
      if (i > 255) {
        paramInt = 255;
      }
      paramArrayOfLong.getChars(0, paramInt, paramArrayOfChar, 0);
      paramArrayOfChar[paramInt] = '\000';
      return 8193;
    }
    paramArrayOfLong = ((WindowManager)this.mContext.getSystemService("window")).getDefaultDisplay();
    paramInt = paramArrayOfLong.getMaximumSizeDimension();
    int i = paramArrayOfLong.getMaximumSizeDimension();
    paramArrayOfLong = Integer.toString(paramInt) + "x" + Integer.toString(i);
    paramArrayOfLong.getChars(0, paramArrayOfLong.length(), paramArrayOfChar, 0);
    paramArrayOfChar[paramArrayOfLong.length()] = '\000';
    return 8193;
  }
  
  private int getNumObjects(int paramInt1, int paramInt2, int paramInt3)
  {
    localObject3 = null;
    localObject1 = null;
    try
    {
      Cursor localCursor = createObjectQuery(paramInt1, paramInt2, paramInt3);
      if (localCursor != null)
      {
        localObject1 = localCursor;
        localObject3 = localCursor;
        paramInt1 = localCursor.getCount();
        if (localCursor != null) {
          localCursor.close();
        }
        return paramInt1;
      }
      if (localCursor != null) {
        localCursor.close();
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        localObject3 = localObject1;
        Log.e("MtpDatabase", "RemoteException in getNumObjects", localRemoteException);
        if (localObject1 != null) {
          ((Cursor)localObject1).close();
        }
      }
    }
    finally
    {
      if (localObject3 == null) {
        break label110;
      }
      ((Cursor)localObject3).close();
    }
    return -1;
  }
  
  private int getObjectFilePath(int paramInt, char[] paramArrayOfChar, long[] paramArrayOfLong)
  {
    if (paramInt == 0)
    {
      this.mMediaStoragePath.getChars(0, this.mMediaStoragePath.length(), paramArrayOfChar, 0);
      paramArrayOfChar[this.mMediaStoragePath.length()] = '\000';
      paramArrayOfLong[0] = 0L;
      paramArrayOfLong[1] = 12289L;
      return 8193;
    }
    Object localObject2 = null;
    Object localObject1 = null;
    try
    {
      Cursor localCursor = this.mMediaProvider.query(this.mObjectsUri, PATH_FORMAT_PROJECTION, "_id=?", new String[] { Integer.toString(paramInt) }, null, null);
      if (localCursor != null)
      {
        localObject1 = localCursor;
        localObject2 = localCursor;
        if (localCursor.moveToNext())
        {
          localObject1 = localCursor;
          localObject2 = localCursor;
          String str = localCursor.getString(1);
          localObject1 = localCursor;
          localObject2 = localCursor;
          str.getChars(0, str.length(), paramArrayOfChar, 0);
          localObject1 = localCursor;
          localObject2 = localCursor;
          paramArrayOfChar[str.length()] = '\000';
          localObject1 = localCursor;
          localObject2 = localCursor;
          paramArrayOfLong[0] = new File(str).length();
          localObject1 = localCursor;
          localObject2 = localCursor;
          paramArrayOfLong[1] = localCursor.getLong(2);
          if (localCursor != null) {
            localCursor.close();
          }
          return 8193;
        }
      }
      if (localCursor != null) {
        localCursor.close();
      }
      return 8201;
    }
    catch (RemoteException paramArrayOfChar)
    {
      localObject2 = localObject1;
      Log.e("MtpDatabase", "RemoteException in getObjectFilePath", paramArrayOfChar);
      return 8194;
    }
    finally
    {
      if (localObject2 != null) {
        ((Cursor)localObject2).close();
      }
    }
  }
  
  private int getObjectFormat(int paramInt)
  {
    Object localObject3 = null;
    Object localObject1 = null;
    try
    {
      Cursor localCursor = this.mMediaProvider.query(this.mObjectsUri, FORMAT_PROJECTION, "_id=?", new String[] { Integer.toString(paramInt) }, null, null);
      if (localCursor != null)
      {
        localObject1 = localCursor;
        localObject3 = localCursor;
        if (localCursor.moveToNext())
        {
          localObject1 = localCursor;
          localObject3 = localCursor;
          paramInt = localCursor.getInt(1);
          if (localCursor != null) {
            localCursor.close();
          }
          return paramInt;
        }
      }
      if (localCursor != null) {
        localCursor.close();
      }
      return -1;
    }
    catch (RemoteException localRemoteException)
    {
      localObject3 = localObject1;
      Log.e("MtpDatabase", "RemoteException in getObjectFilePath", localRemoteException);
      return -1;
    }
    finally
    {
      if (localObject3 != null) {
        ((Cursor)localObject3).close();
      }
    }
  }
  
  private boolean getObjectInfo(int paramInt, int[] paramArrayOfInt, char[] paramArrayOfChar, long[] paramArrayOfLong)
  {
    localObject2 = null;
    localObject1 = null;
    try
    {
      Cursor localCursor = this.mMediaProvider.query(this.mObjectsUri, OBJECT_INFO_PROJECTION, "_id=?", new String[] { Integer.toString(paramInt) }, null, null);
      if (localCursor != null)
      {
        localObject1 = localCursor;
        localObject2 = localCursor;
        if (localCursor.moveToNext())
        {
          localObject1 = localCursor;
          localObject2 = localCursor;
          paramArrayOfInt[0] = localCursor.getInt(1);
          localObject1 = localCursor;
          localObject2 = localCursor;
          paramArrayOfInt[1] = localCursor.getInt(2);
          localObject1 = localCursor;
          localObject2 = localCursor;
          paramArrayOfInt[2] = localCursor.getInt(3);
          localObject1 = localCursor;
          localObject2 = localCursor;
          paramArrayOfInt = localCursor.getString(4);
          localObject1 = localCursor;
          localObject2 = localCursor;
          paramInt = paramArrayOfInt.lastIndexOf('/');
          if (paramInt >= 0) {
            paramInt += 1;
          }
          for (;;)
          {
            localObject1 = localCursor;
            localObject2 = localCursor;
            int j = paramArrayOfInt.length();
            int i = j;
            if (j - paramInt > 255) {
              i = paramInt + 255;
            }
            localObject1 = localCursor;
            localObject2 = localCursor;
            paramArrayOfInt.getChars(paramInt, i, paramArrayOfChar, 0);
            paramArrayOfChar[(i - paramInt)] = '\000';
            localObject1 = localCursor;
            localObject2 = localCursor;
            paramArrayOfLong[0] = localCursor.getLong(5);
            localObject1 = localCursor;
            localObject2 = localCursor;
            paramArrayOfLong[1] = localCursor.getLong(6);
            if (paramArrayOfLong[0] == 0L) {
              paramArrayOfLong[0] = paramArrayOfLong[1];
            }
            if (localCursor != null) {
              localCursor.close();
            }
            return true;
            paramInt = 0;
          }
        }
      }
      if (localCursor != null) {
        localCursor.close();
      }
    }
    catch (RemoteException paramArrayOfInt)
    {
      for (;;)
      {
        localObject2 = localObject1;
        Log.e("MtpDatabase", "RemoteException in getObjectInfo", paramArrayOfInt);
        if (localObject1 != null) {
          ((Cursor)localObject1).close();
        }
      }
    }
    finally
    {
      if (localObject2 == null) {
        break label350;
      }
      ((Cursor)localObject2).close();
    }
    return false;
  }
  
  private int[] getObjectList(int paramInt1, int paramInt2, int paramInt3)
  {
    localObject3 = null;
    localObject1 = null;
    try
    {
      Cursor localCursor = createObjectQuery(paramInt1, paramInt2, paramInt3);
      if (localCursor == null)
      {
        if (localCursor != null) {
          localCursor.close();
        }
        return null;
      }
      localObject1 = localCursor;
      localObject3 = localCursor;
      paramInt2 = localCursor.getCount();
      if (paramInt2 > 0)
      {
        localObject1 = localCursor;
        localObject3 = localCursor;
        int[] arrayOfInt = new int[paramInt2];
        paramInt1 = 0;
        while (paramInt1 < paramInt2)
        {
          localObject1 = localCursor;
          localObject3 = localCursor;
          localCursor.moveToNext();
          localObject1 = localCursor;
          localObject3 = localCursor;
          arrayOfInt[paramInt1] = localCursor.getInt(0);
          paramInt1 += 1;
        }
        if (localCursor != null) {
          localCursor.close();
        }
        return arrayOfInt;
      }
      if (localCursor != null) {
        localCursor.close();
      }
    }
    catch (RemoteException localRemoteException)
    {
      localObject3 = localObject1;
      Log.e("MtpDatabase", "RemoteException in getObjectList", localRemoteException);
      return null;
    }
    finally
    {
      if (localObject3 == null) {
        break label191;
      }
      ((Cursor)localObject3).close();
    }
    return null;
  }
  
  private MtpPropertyList getObjectPropertyList(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    if (paramInt4 != 0) {
      return new MtpPropertyList(0, 43015);
    }
    MtpPropertyGroup localMtpPropertyGroup;
    Object localObject;
    if (paramInt3 == -1)
    {
      paramInt3 = paramInt2;
      if (paramInt2 == 0)
      {
        paramInt3 = paramInt2;
        if (paramInt1 != 0)
        {
          paramInt3 = paramInt2;
          if (paramInt1 != -1) {
            paramInt3 = getObjectFormat(paramInt1);
          }
        }
      }
      localMtpPropertyGroup = (MtpPropertyGroup)this.mPropertyGroupsByFormat.get(Integer.valueOf(paramInt3));
      localObject = localMtpPropertyGroup;
      paramInt4 = paramInt3;
      if (localMtpPropertyGroup == null)
      {
        localObject = getSupportedObjectProperties(paramInt3);
        localObject = new MtpPropertyGroup(this, this.mMediaProvider, this.mVolumeName, (int[])localObject);
        this.mPropertyGroupsByFormat.put(Integer.valueOf(paramInt3), localObject);
        paramInt4 = paramInt3;
      }
    }
    for (;;)
    {
      return ((MtpPropertyGroup)localObject).getPropertyList(paramInt1, paramInt4, paramInt5);
      localMtpPropertyGroup = (MtpPropertyGroup)this.mPropertyGroupsByProperty.get(Integer.valueOf(paramInt3));
      localObject = localMtpPropertyGroup;
      paramInt4 = paramInt2;
      if (localMtpPropertyGroup == null)
      {
        localObject = new MtpPropertyGroup(this, this.mMediaProvider, this.mVolumeName, new int[] { paramInt3 });
        this.mPropertyGroupsByProperty.put(Integer.valueOf(paramInt3), localObject);
        paramInt4 = paramInt2;
      }
    }
  }
  
  private int[] getObjectReferences(int paramInt)
  {
    Object localObject4 = MediaStore.Files.getMtpReferencesUri(this.mVolumeName, paramInt);
    localObject3 = null;
    localObject1 = null;
    try
    {
      localObject4 = this.mMediaProvider.query((Uri)localObject4, ID_PROJECTION, null, null, null, null);
      if (localObject4 == null) {
        return null;
      }
      localObject1 = localObject4;
      localObject3 = localObject4;
      int i = ((Cursor)localObject4).getCount();
      if (i > 0)
      {
        localObject1 = localObject4;
        localObject3 = localObject4;
        int[] arrayOfInt = new int[i];
        paramInt = 0;
        while (paramInt < i)
        {
          localObject1 = localObject4;
          localObject3 = localObject4;
          ((Cursor)localObject4).moveToNext();
          localObject1 = localObject4;
          localObject3 = localObject4;
          arrayOfInt[paramInt] = ((Cursor)localObject4).getInt(0);
          paramInt += 1;
        }
        return arrayOfInt;
      }
    }
    catch (RemoteException localRemoteException)
    {
      localObject3 = localObject1;
      Log.e("MtpDatabase", "RemoteException in getObjectList", localRemoteException);
      return null;
    }
    finally
    {
      if (localObject3 == null) {
        break label202;
      }
      ((Cursor)localObject3).close();
    }
    return null;
  }
  
  private int[] getSupportedCaptureFormats()
  {
    return null;
  }
  
  private int[] getSupportedDeviceProperties()
  {
    return new int[] { 54273, 54274, 20483, 20481 };
  }
  
  private int[] getSupportedObjectProperties(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return FILE_PROPERTIES;
    case 12296: 
    case 12297: 
    case 47361: 
    case 47362: 
    case 47363: 
      return AUDIO_PROPERTIES;
    case 12299: 
    case 47489: 
    case 47492: 
      return VIDEO_PROPERTIES;
    }
    return IMAGE_PROPERTIES;
  }
  
  private int[] getSupportedPlaybackFormats()
  {
    return new int[] { 12288, 12289, 12292, 12293, 12296, 12297, 12299, 14337, 14338, 14340, 14343, 14344, 14347, 14349, 47361, 47362, 47363, 47490, 47491, 47492, 47621, 47632, 47633, 47636, 47746, 47366, 14353 };
  }
  
  private boolean inStorageRoot(String paramString)
  {
    try
    {
      paramString = new File(paramString).getCanonicalPath();
      Iterator localIterator = this.mStorageMap.keySet().iterator();
      while (localIterator.hasNext())
      {
        boolean bool = paramString.startsWith((String)localIterator.next());
        if (bool) {
          return true;
        }
      }
    }
    catch (IOException paramString) {}
    return false;
  }
  
  private boolean inStorageSubDirectory(String paramString)
  {
    if (this.mSubDirectories == null) {
      return true;
    }
    if (paramString == null) {
      return false;
    }
    boolean bool1 = false;
    int j = paramString.length();
    int i = 0;
    for (;;)
    {
      if ((i >= this.mSubDirectories.length) || (bool1)) {
        return bool1;
      }
      String str = this.mSubDirectories[i];
      int k = str.length();
      boolean bool2 = bool1;
      if (k < j)
      {
        bool2 = bool1;
        if (paramString.charAt(k) == '/')
        {
          bool2 = bool1;
          if (paramString.startsWith(str)) {
            bool2 = true;
          }
        }
      }
      i += 1;
      bool1 = bool2;
    }
  }
  
  /* Error */
  private void initDeviceProperties(Context paramContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: ldc_w 628
    //   5: iconst_0
    //   6: invokevirtual 632	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   9: putfield 482	android/mtp/MtpDatabase:mDeviceProperties	Landroid/content/SharedPreferences;
    //   12: aload_1
    //   13: ldc_w 628
    //   16: invokevirtual 636	android/content/Context:getDatabasePath	(Ljava/lang/String;)Ljava/io/File;
    //   19: invokevirtual 639	java/io/File:exists	()Z
    //   22: ifeq +236 -> 258
    //   25: aconst_null
    //   26: astore 4
    //   28: aconst_null
    //   29: astore_2
    //   30: aconst_null
    //   31: astore 9
    //   33: aconst_null
    //   34: astore 8
    //   36: aconst_null
    //   37: astore 6
    //   39: aload 6
    //   41: astore_3
    //   42: aload 8
    //   44: astore 5
    //   46: aload_1
    //   47: ldc_w 628
    //   50: iconst_0
    //   51: aconst_null
    //   52: invokevirtual 643	android/content/Context:openOrCreateDatabase	(Ljava/lang/String;ILandroid/database/sqlite/SQLiteDatabase$CursorFactory;)Landroid/database/sqlite/SQLiteDatabase;
    //   55: astore 7
    //   57: aload 9
    //   59: astore_2
    //   60: aload 7
    //   62: ifnull +222 -> 284
    //   65: aload 7
    //   67: astore_2
    //   68: aload 6
    //   70: astore_3
    //   71: aload 7
    //   73: astore 4
    //   75: aload 8
    //   77: astore 5
    //   79: aload 7
    //   81: ldc_w 645
    //   84: iconst_3
    //   85: anewarray 107	java/lang/String
    //   88: dup
    //   89: iconst_0
    //   90: ldc 109
    //   92: aastore
    //   93: dup
    //   94: iconst_1
    //   95: ldc_w 647
    //   98: aastore
    //   99: dup
    //   100: iconst_2
    //   101: ldc_w 649
    //   104: aastore
    //   105: aconst_null
    //   106: aconst_null
    //   107: aconst_null
    //   108: aconst_null
    //   109: aconst_null
    //   110: invokevirtual 654	android/database/sqlite/SQLiteDatabase:query	(Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   113: astore 6
    //   115: aload 6
    //   117: astore_2
    //   118: aload 6
    //   120: ifnull +164 -> 284
    //   123: aload 7
    //   125: astore_2
    //   126: aload 6
    //   128: astore_3
    //   129: aload 7
    //   131: astore 4
    //   133: aload 6
    //   135: astore 5
    //   137: aload_0
    //   138: getfield 482	android/mtp/MtpDatabase:mDeviceProperties	Landroid/content/SharedPreferences;
    //   141: invokeinterface 658 1 0
    //   146: astore 8
    //   148: aload 7
    //   150: astore_2
    //   151: aload 6
    //   153: astore_3
    //   154: aload 7
    //   156: astore 4
    //   158: aload 6
    //   160: astore 5
    //   162: aload 6
    //   164: invokeinterface 388 1 0
    //   169: ifeq +90 -> 259
    //   172: aload 7
    //   174: astore_2
    //   175: aload 6
    //   177: astore_3
    //   178: aload 7
    //   180: astore 4
    //   182: aload 6
    //   184: astore 5
    //   186: aload 8
    //   188: aload 6
    //   190: iconst_1
    //   191: invokeinterface 391 2 0
    //   196: aload 6
    //   198: iconst_2
    //   199: invokeinterface 391 2 0
    //   204: invokeinterface 664 3 0
    //   209: pop
    //   210: goto -62 -> 148
    //   213: astore 6
    //   215: aload_2
    //   216: astore 4
    //   218: aload_3
    //   219: astore 5
    //   221: ldc 52
    //   223: ldc_w 666
    //   226: aload 6
    //   228: invokestatic 374	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   231: pop
    //   232: aload_3
    //   233: ifnull +9 -> 242
    //   236: aload_3
    //   237: invokeinterface 322 1 0
    //   242: aload_2
    //   243: ifnull +7 -> 250
    //   246: aload_2
    //   247: invokevirtual 667	android/database/sqlite/SQLiteDatabase:close	()V
    //   250: aload_1
    //   251: ldc_w 628
    //   254: invokevirtual 670	android/content/Context:deleteDatabase	(Ljava/lang/String;)Z
    //   257: pop
    //   258: return
    //   259: aload 7
    //   261: astore_2
    //   262: aload 6
    //   264: astore_3
    //   265: aload 7
    //   267: astore 4
    //   269: aload 6
    //   271: astore 5
    //   273: aload 8
    //   275: invokeinterface 673 1 0
    //   280: pop
    //   281: aload 6
    //   283: astore_2
    //   284: aload_2
    //   285: ifnull +9 -> 294
    //   288: aload_2
    //   289: invokeinterface 322 1 0
    //   294: aload 7
    //   296: ifnull -46 -> 250
    //   299: aload 7
    //   301: invokevirtual 667	android/database/sqlite/SQLiteDatabase:close	()V
    //   304: goto -54 -> 250
    //   307: astore_1
    //   308: aload 5
    //   310: ifnull +10 -> 320
    //   313: aload 5
    //   315: invokeinterface 322 1 0
    //   320: aload 4
    //   322: ifnull +8 -> 330
    //   325: aload 4
    //   327: invokevirtual 667	android/database/sqlite/SQLiteDatabase:close	()V
    //   330: aload_1
    //   331: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	332	0	this	MtpDatabase
    //   0	332	1	paramContext	Context
    //   29	260	2	localObject1	Object
    //   41	224	3	localObject2	Object
    //   26	300	4	localObject3	Object
    //   44	270	5	localObject4	Object
    //   37	160	6	localCursor	Cursor
    //   213	69	6	localException	Exception
    //   55	245	7	localSQLiteDatabase	android.database.sqlite.SQLiteDatabase
    //   34	240	8	localEditor	SharedPreferences.Editor
    //   31	27	9	localObject5	Object
    // Exception table:
    //   from	to	target	type
    //   46	57	213	java/lang/Exception
    //   79	115	213	java/lang/Exception
    //   137	148	213	java/lang/Exception
    //   162	172	213	java/lang/Exception
    //   186	210	213	java/lang/Exception
    //   273	281	213	java/lang/Exception
    //   46	57	307	finally
    //   79	115	307	finally
    //   137	148	307	finally
    //   162	172	307	finally
    //   186	210	307	finally
    //   221	232	307	finally
    //   273	281	307	finally
  }
  
  private boolean isStorageSubDirectory(String paramString)
  {
    if (this.mSubDirectories == null) {
      return false;
    }
    int i = 0;
    while (i < this.mSubDirectories.length)
    {
      if (paramString.equals(this.mSubDirectories[i])) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private final native void native_finalize();
  
  private final native void native_setup();
  
  private int renameFile(int paramInt, String paramString)
  {
    Object localObject2 = null;
    Object localObject1 = null;
    Object localObject5 = null;
    String[] arrayOfString = new String[1];
    arrayOfString[0] = Integer.toString(paramInt);
    Object localObject3;
    try
    {
      localObject4 = this.mMediaProvider.query(this.mObjectsUri, PATH_PROJECTION, "_id=?", arrayOfString, null, null);
      localObject3 = localObject5;
      if (localObject4 != null)
      {
        localObject3 = localObject5;
        localObject1 = localObject4;
        localObject2 = localObject4;
        if (((Cursor)localObject4).moveToNext())
        {
          localObject1 = localObject4;
          localObject2 = localObject4;
          localObject3 = ((Cursor)localObject4).getString(1);
        }
      }
      if (localObject4 != null) {
        ((Cursor)localObject4).close();
      }
      if (localObject3 == null) {
        return 8201;
      }
    }
    catch (RemoteException paramString)
    {
      localObject2 = localObject1;
      Log.e("MtpDatabase", "RemoteException in getObjectFilePath", paramString);
      return 8194;
    }
    finally
    {
      if (localObject2 != null) {
        ((Cursor)localObject2).close();
      }
    }
    if (isStorageSubDirectory((String)localObject3)) {
      return 8205;
    }
    localObject1 = new File((String)localObject3);
    paramInt = ((String)localObject3).lastIndexOf('/');
    if (paramInt <= 1) {
      return 8194;
    }
    paramString = ((String)localObject3).substring(0, paramInt + 1) + paramString;
    localObject2 = new File(paramString);
    if (!((File)localObject1).renameTo((File)localObject2))
    {
      Log.w("MtpDatabase", "renaming " + (String)localObject3 + " to " + paramString + " failed");
      return 8194;
    }
    Object localObject4 = new ContentValues();
    ((ContentValues)localObject4).put("_data", paramString);
    paramInt = 0;
    try
    {
      int i = this.mMediaProvider.update(this.mObjectsUri, (ContentValues)localObject4, "_id=?", arrayOfString);
      paramInt = i;
    }
    catch (RemoteException localRemoteException3)
    {
      for (;;)
      {
        Log.e("MtpDatabase", "RemoteException in mMediaProvider.update", localRemoteException3);
      }
    }
    if (paramInt == 0)
    {
      Log.e("MtpDatabase", "Unable to update path for " + (String)localObject3 + " to " + paramString);
      ((File)localObject2).renameTo((File)localObject1);
      return 8194;
    }
    if (((File)localObject2).isDirectory()) {
      if ((((File)localObject1).getName().startsWith(".")) && (!paramString.startsWith("."))) {}
    }
    for (;;)
    {
      return 8193;
      try
      {
        this.mMediaProvider.call("unhide", paramString, null);
      }
      catch (RemoteException localRemoteException1)
      {
        Log.e("MtpDatabase", "failed to unhide/rescan for " + paramString);
      }
      continue;
      if ((localRemoteException1.getName().toLowerCase(Locale.US).equals(".nomedia")) && (!paramString.toLowerCase(Locale.US).equals(".nomedia"))) {
        try
        {
          this.mMediaProvider.call("unhide", localRemoteException1.getParent(), null);
        }
        catch (RemoteException localRemoteException2)
        {
          Log.e("MtpDatabase", "failed to unhide/rescan for " + paramString);
        }
      }
    }
  }
  
  private void sessionEnded()
  {
    if (this.mDatabaseModified)
    {
      this.mContext.sendBroadcast(new Intent("android.provider.action.MTP_SESSION_END"));
      this.mDatabaseModified = false;
    }
  }
  
  private void sessionStarted()
  {
    this.mDatabaseModified = false;
  }
  
  private int setDeviceProperty(int paramInt, long paramLong, String paramString)
  {
    switch (paramInt)
    {
    default: 
      return 8202;
    }
    SharedPreferences.Editor localEditor = this.mDeviceProperties.edit();
    localEditor.putString(Integer.toString(paramInt), paramString);
    if (localEditor.commit()) {
      return 8193;
    }
    return 8194;
  }
  
  private int setObjectProperty(int paramInt1, int paramInt2, long paramLong, String paramString)
  {
    switch (paramInt2)
    {
    default: 
      return 43018;
    }
    return renameFile(paramInt1, paramString);
  }
  
  private int setObjectReferences(int paramInt, int[] paramArrayOfInt)
  {
    this.mDatabaseModified = true;
    Uri localUri = MediaStore.Files.getMtpReferencesUri(this.mVolumeName, paramInt);
    int i = paramArrayOfInt.length;
    ContentValues[] arrayOfContentValues = new ContentValues[i];
    paramInt = 0;
    while (paramInt < i)
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("_id", Integer.valueOf(paramArrayOfInt[paramInt]));
      arrayOfContentValues[paramInt] = localContentValues;
      paramInt += 1;
    }
    try
    {
      paramInt = this.mMediaProvider.bulkInsert(localUri, arrayOfContentValues);
      if (paramInt > 0) {
        return 8193;
      }
    }
    catch (RemoteException paramArrayOfInt)
    {
      Log.e("MtpDatabase", "RemoteException in setObjectReferences", paramArrayOfInt);
    }
    return 8194;
  }
  
  public void addStorage(MtpStorage paramMtpStorage)
  {
    this.mStorageMap.put(paramMtpStorage.getPath(), paramMtpStorage);
  }
  
  public void close()
  {
    this.mCloseGuard.close();
    if (this.mClosed.compareAndSet(false, true))
    {
      this.mMediaScanner.close();
      this.mMediaProvider.close();
      native_finalize();
    }
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      this.mCloseGuard.warnIfOpen();
      close();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public void removeStorage(MtpStorage paramMtpStorage)
  {
    this.mStorageMap.remove(paramMtpStorage.getPath());
  }
  
  public void setServer(MtpServer paramMtpServer)
  {
    this.mServer = paramMtpServer;
    try
    {
      this.mContext.unregisterReceiver(this.mBatteryReceiver);
      if (paramMtpServer != null) {
        this.mContext.registerReceiver(this.mBatteryReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
      }
      return;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      for (;;) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/mtp/MtpDatabase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */