package android.media;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Environment.UserEnvironment;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MediaStore.Audio.Media;
import android.provider.Settings.System;
import android.util.Log;
import android.util.OpFeatures;
import java.io.File;
import java.io.IOException;
import libcore.io.IoUtils;

public class RingtoneManagerUtils
{
  private static boolean DBG = Build.DEBUG_ONEPLUS;
  private static String[] MANAGED_RING_PATH = { "" };
  private static String[] RINGPATH_FROM_TYPE = { "", Environment.DIRECTORY_RINGTONES, Environment.DIRECTORY_NOTIFICATIONS, "", Environment.DIRECTORY_ALARMS };
  private static final String TAG = "RingtoneManagerUtils";
  private static final int TYPE_SMS = 100;
  
  private static boolean confirmExternalPermission(Context paramContext, Uri paramUri, String paramString)
  {
    String str = paramContext.getPackageName();
    if (DBG) {
      Log.d("RingtoneManagerUtils", "confirmExternalPermission: check " + str + " on [" + paramString + "].");
    }
    boolean bool2 = false;
    boolean bool1 = false;
    if (ringtoneIsExternal(paramUri))
    {
      if (paramContext.checkSelfPermission(paramString) == 0) {
        bool1 = true;
      }
      bool2 = bool1;
      if (!bool1)
      {
        Log.w("RingtoneManagerUtils", "No permission of [" + paramString + "] for " + str + ".");
        bool2 = bool1;
      }
    }
    while (!DBG) {
      return bool2;
    }
    Log.d("RingtoneManagerUtils", "confirmExternalPermission: [" + paramUri + "] is not external uri.");
    return false;
  }
  
  private static String getExternalPath()
  {
    return new Environment.UserEnvironment(UserHandle.myUserId()).getExternalStorageDirectory().getAbsolutePath();
  }
  
  public static String getRingtoneAlias(Context paramContext, int paramInt, String paramString)
  {
    String str1 = paramString;
    Object localObject;
    if (paramInt >= 1)
    {
      str1 = paramString;
      if (paramInt <= 4)
      {
        str1 = paramString;
        if (OpFeatures.isSupport(new int[] { 15 }))
        {
          localObject = new String[5];
          localObject[0] = "";
          localObject[1] = "oos_ring_ringtones_";
          localObject[2] = "oos_ring_notifications_";
          localObject[3] = "";
          localObject[4] = "oos_ring_alarms_";
          str1 = paramString;
          if (localObject[paramInt] != "")
          {
            str1 = paramString;
            if (paramString != null)
            {
              String str2 = null;
              if (!paramString.startsWith(paramContext.getString(17040358))) {
                break label195;
              }
              str2 = Ringtone.getTitle(paramContext, RingtoneManager.getActualDefaultRingtoneUri(paramContext, paramInt), false, false);
              str1 = str2.toLowerCase();
              localObject = localObject[paramInt] + str1.replace(" ", "_");
              paramInt = Resources.getSystem().getIdentifier((String)localObject, "string", "android");
              if (paramInt <= 0) {
                break label203;
              }
              paramString = paramContext.getString(paramInt);
              str1 = paramString;
              if (str2 != null) {
                str1 = paramContext.getString(17040359, new Object[] { paramString });
              }
            }
          }
        }
      }
    }
    label195:
    label203:
    do
    {
      return str1;
      str1 = paramString.toLowerCase();
      break;
      str1 = paramString;
    } while (!DBG);
    Log.v("RingtoneManagerUtils", "getRingtoneAlias: resource not found - " + (String)localObject);
    return paramString;
  }
  
  private static void printExceptionLogs(String paramString, Exception paramException)
  {
    if (DBG)
    {
      Log.w("RingtoneManagerUtils", paramString, paramException);
      return;
    }
    Log.w("RingtoneManagerUtils", paramString + ": " + paramException);
  }
  
  private static boolean ringtoneAudioIsSupported(String paramString)
  {
    boolean bool = false;
    int i;
    StringBuilder localStringBuilder;
    if (paramString != null)
    {
      i = MediaFile.getFileTypeForMimeType(paramString);
      if (OpFeatures.isSupport(new int[] { 19 })) {
        bool = MediaFile.isAudioFileType(i);
      }
    }
    else
    {
      localStringBuilder = new StringBuilder().append("ringtoneAudioIsSupported: [").append(paramString).append("] is ");
      if (!bool) {
        break label88;
      }
    }
    label88:
    for (paramString = "";; paramString = "not ")
    {
      Log.d("RingtoneManagerUtils", paramString + "supported");
      return bool;
      bool = MediaFile.isLegacyAudioFileType(i);
      break;
    }
  }
  
  private static boolean ringtoneCheckExtraExternalFileExisted(ContentProviderClient paramContentProviderClient, Uri paramUri)
    throws RemoteException
  {
    boolean bool3 = false;
    boolean bool2 = false;
    paramUri = paramUri.getLastPathSegment();
    paramContentProviderClient = paramContentProviderClient.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "_data" }, "_id=?", new String[] { paramUri }, null);
    boolean bool1 = bool3;
    if (paramContentProviderClient != null) {}
    for (;;)
    {
      bool1 = bool2;
      try
      {
        if (paramContentProviderClient.moveToNext())
        {
          paramUri = paramContentProviderClient.getString(paramContentProviderClient.getColumnIndex("_data"));
          String str = getExternalPath();
          if ((paramUri == null) || (paramUri.startsWith(str))) {
            continue;
          }
          if (DBG) {
            Log.d("RingtoneManagerUtils", "ringtoneCheckExtraExternalFileExisted: strRingPath = " + paramUri);
          }
          bool1 = new File(paramUri).exists();
          if (!bool1) {
            continue;
          }
          bool1 = true;
        }
      }
      catch (Exception paramUri)
      {
        for (;;)
        {
          printExceptionLogs("ringtoneCheckExtraExternalFileExisted", paramUri);
          IoUtils.closeQuietly(paramContentProviderClient);
          bool1 = bool3;
        }
      }
      finally
      {
        IoUtils.closeQuietly(paramContentProviderClient);
      }
    }
    if (DBG)
    {
      paramUri = new StringBuilder().append("ringtoneCheckExtraExternalFileExisted: ");
      if (!bool1) {
        break label217;
      }
    }
    label217:
    for (paramContentProviderClient = "existed";; paramContentProviderClient = "gone")
    {
      Log.d("RingtoneManagerUtils", paramContentProviderClient);
      return bool1;
    }
  }
  
  /* Error */
  private static String ringtoneCheckMimeType(ContentProviderClient paramContentProviderClient, Uri paramUri)
    throws RemoteException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 8
    //   6: aconst_null
    //   7: astore 5
    //   9: aconst_null
    //   10: astore 10
    //   12: aconst_null
    //   13: astore 4
    //   15: aconst_null
    //   16: astore 12
    //   18: aconst_null
    //   19: astore 11
    //   21: aconst_null
    //   22: astore 9
    //   24: new 300	android/media/MediaExtractor
    //   27: dup
    //   28: invokespecial 301	android/media/MediaExtractor:<init>	()V
    //   31: astore 6
    //   33: aload 12
    //   35: astore 4
    //   37: aload 11
    //   39: astore 5
    //   41: aload_0
    //   42: aload_1
    //   43: ldc_w 303
    //   46: invokevirtual 307	android/content/ContentProviderClient:openAssetFile	(Landroid/net/Uri;Ljava/lang/String;)Landroid/content/res/AssetFileDescriptor;
    //   49: astore_1
    //   50: aload 8
    //   52: astore_0
    //   53: aload_1
    //   54: ifnull +121 -> 175
    //   57: aload_1
    //   58: astore 4
    //   60: aload_1
    //   61: astore 5
    //   63: aload 6
    //   65: aload_1
    //   66: invokevirtual 313	android/content/res/AssetFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   69: invokevirtual 317	android/media/MediaExtractor:setDataSource	(Ljava/io/FileDescriptor;)V
    //   72: iconst_0
    //   73: istore_2
    //   74: aload 8
    //   76: astore_0
    //   77: aload_1
    //   78: astore 4
    //   80: aload_1
    //   81: astore 5
    //   83: iload_2
    //   84: aload 6
    //   86: invokevirtual 320	android/media/MediaExtractor:getTrackCount	()I
    //   89: if_icmpge +86 -> 175
    //   92: aload_1
    //   93: astore 4
    //   95: aload_1
    //   96: astore 5
    //   98: aload 6
    //   100: iload_2
    //   101: invokevirtual 324	android/media/MediaExtractor:getTrackFormat	(I)Landroid/media/MediaFormat;
    //   104: ldc_w 326
    //   107: invokevirtual 331	android/media/MediaFormat:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   110: astore_0
    //   111: aload_1
    //   112: astore 4
    //   114: aload_1
    //   115: astore 5
    //   117: ldc 18
    //   119: new 70	java/lang/StringBuilder
    //   122: dup
    //   123: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   126: ldc_w 333
    //   129: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   132: iload_2
    //   133: invokevirtual 336	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   136: ldc_w 338
    //   139: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   142: aload_0
    //   143: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   149: invokestatic 107	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   152: pop
    //   153: aload_0
    //   154: ifnull +43 -> 197
    //   157: aload_1
    //   158: astore 4
    //   160: aload_1
    //   161: astore 5
    //   163: aload_0
    //   164: ldc_w 340
    //   167: invokevirtual 158	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   170: istore_3
    //   171: iload_3
    //   172: ifeq +25 -> 197
    //   175: aload 6
    //   177: ifnull +8 -> 185
    //   180: aload 6
    //   182: invokevirtual 343	android/media/MediaExtractor:release	()V
    //   185: aload_1
    //   186: ifnull +7 -> 193
    //   189: aload_1
    //   190: invokevirtual 346	android/content/res/AssetFileDescriptor:close	()V
    //   193: aload_0
    //   194: astore_1
    //   195: aload_1
    //   196: areturn
    //   197: iload_2
    //   198: iconst_1
    //   199: iadd
    //   200: istore_2
    //   201: goto -127 -> 74
    //   204: astore_1
    //   205: ldc_w 348
    //   208: aload_1
    //   209: invokestatic 31	android/media/RingtoneManagerUtils:printExceptionLogs	(Ljava/lang/String;Ljava/lang/Exception;)V
    //   212: goto -19 -> 193
    //   215: astore 6
    //   217: aload 10
    //   219: astore_1
    //   220: aload 9
    //   222: astore_0
    //   223: aload_0
    //   224: astore 4
    //   226: aload_1
    //   227: astore 5
    //   229: ldc_w 349
    //   232: aload 6
    //   234: invokestatic 31	android/media/RingtoneManagerUtils:printExceptionLogs	(Ljava/lang/String;Ljava/lang/Exception;)V
    //   237: aload_1
    //   238: ifnull +7 -> 245
    //   241: aload_1
    //   242: invokevirtual 343	android/media/MediaExtractor:release	()V
    //   245: aload 7
    //   247: astore_1
    //   248: aload_0
    //   249: ifnull -54 -> 195
    //   252: aload_0
    //   253: invokevirtual 346	android/content/res/AssetFileDescriptor:close	()V
    //   256: aconst_null
    //   257: areturn
    //   258: astore_0
    //   259: ldc_w 348
    //   262: aload_0
    //   263: invokestatic 31	android/media/RingtoneManagerUtils:printExceptionLogs	(Ljava/lang/String;Ljava/lang/Exception;)V
    //   266: aconst_null
    //   267: areturn
    //   268: astore_0
    //   269: aload 5
    //   271: ifnull +8 -> 279
    //   274: aload 5
    //   276: invokevirtual 343	android/media/MediaExtractor:release	()V
    //   279: aload 4
    //   281: ifnull +8 -> 289
    //   284: aload 4
    //   286: invokevirtual 346	android/content/res/AssetFileDescriptor:close	()V
    //   289: aload_0
    //   290: athrow
    //   291: astore_1
    //   292: ldc_w 348
    //   295: aload_1
    //   296: invokestatic 31	android/media/RingtoneManagerUtils:printExceptionLogs	(Ljava/lang/String;Ljava/lang/Exception;)V
    //   299: goto -10 -> 289
    //   302: astore_0
    //   303: aload 6
    //   305: astore 5
    //   307: goto -38 -> 269
    //   310: astore 4
    //   312: aload 6
    //   314: astore_1
    //   315: aload 5
    //   317: astore_0
    //   318: aload 4
    //   320: astore 6
    //   322: goto -99 -> 223
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	325	0	paramContentProviderClient	ContentProviderClient
    //   0	325	1	paramUri	Uri
    //   73	128	2	i	int
    //   170	2	3	bool	boolean
    //   13	272	4	localObject1	Object
    //   310	9	4	localSecurityException1	SecurityException
    //   7	309	5	localObject2	Object
    //   31	150	6	localMediaExtractor	MediaExtractor
    //   215	98	6	localSecurityException2	SecurityException
    //   320	1	6	localSecurityException3	SecurityException
    //   1	245	7	localObject3	Object
    //   4	71	8	localObject4	Object
    //   22	199	9	localObject5	Object
    //   10	208	10	localObject6	Object
    //   19	19	11	localObject7	Object
    //   16	18	12	localObject8	Object
    // Exception table:
    //   from	to	target	type
    //   189	193	204	java/io/IOException
    //   24	33	215	java/lang/SecurityException
    //   24	33	215	java/io/IOException
    //   252	256	258	java/io/IOException
    //   24	33	268	finally
    //   229	237	268	finally
    //   284	289	291	java/io/IOException
    //   41	50	302	finally
    //   63	72	302	finally
    //   83	92	302	finally
    //   98	111	302	finally
    //   117	153	302	finally
    //   163	171	302	finally
    //   41	50	310	java/lang/SecurityException
    //   41	50	310	java/io/IOException
    //   63	72	310	java/lang/SecurityException
    //   63	72	310	java/io/IOException
    //   83	92	310	java/lang/SecurityException
    //   83	92	310	java/io/IOException
    //   98	111	310	java/lang/SecurityException
    //   98	111	310	java/io/IOException
    //   117	153	310	java/lang/SecurityException
    //   117	153	310	java/io/IOException
    //   163	171	310	java/lang/SecurityException
    //   163	171	310	java/io/IOException
  }
  
  private static boolean ringtoneCheckValid(ContentProviderClient paramContentProviderClient, Uri paramUri)
    throws RemoteException
  {
    boolean bool1 = true;
    boolean bool2 = true;
    if (paramContentProviderClient != null) {}
    for (;;)
    {
      try
      {
        localAssetFileDescriptor = paramContentProviderClient.openAssetFile(paramUri, "r");
        bool1 = bool2;
        if (localAssetFileDescriptor == null) {}
      }
      catch (SecurityException|IOException localSecurityException)
      {
        AssetFileDescriptor localAssetFileDescriptor;
        localSecurityException = localSecurityException;
        printExceptionLogs("ringtoneCheckValid", localSecurityException);
        bool1 = false;
        continue;
      }
      finally {}
      try
      {
        localAssetFileDescriptor.close();
        bool1 = bool2;
      }
      catch (IOException localIOException)
      {
        printExceptionLogs("ringtoneCheckValid Exception on fd closing", localIOException);
        bool1 = bool2;
      }
    }
    bool2 = bool1;
    if (!bool1) {
      bool2 = ringtoneCheckExtraExternalFileExisted(paramContentProviderClient, paramUri);
    }
    bool1 = bool2;
    if (DBG)
    {
      paramUri = new StringBuilder().append("ringtoneCheckValid: file_uri[").append(paramUri.toString()).append("] : ");
      if (!bool2) {
        break label138;
      }
    }
    label138:
    for (paramContentProviderClient = "alive";; paramContentProviderClient = "gone")
    {
      Log.v("RingtoneManagerUtils", paramContentProviderClient);
      bool1 = bool2;
      return bool1;
    }
  }
  
  public static boolean ringtoneCheckValid(Context paramContext, Uri paramUri)
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (confirmExternalPermission(paramContext, paramUri, "android.permission.READ_EXTERNAL_STORAGE")) {
      if (!paramContext.getApplicationInfo().isSystemApp())
      {
        bool1 = bool2;
        if (!paramContext.getApplicationInfo().isPrivilegedApp()) {}
      }
      else
      {
        paramContext = paramContext.getContentResolver().acquireUnstableContentProviderClient(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        bool1 = bool2;
        if (paramContext == null) {}
      }
    }
    try
    {
      bool1 = ringtoneCheckValid(paramContext, paramUri);
      return bool1;
    }
    catch (RemoteException paramUri)
    {
      printExceptionLogs("ringtoneCheckValid", paramUri);
      return true;
    }
    finally
    {
      paramContext.release();
    }
  }
  
  /* Error */
  public static Uri ringtoneCopyFrom3rdParty(Context paramContext, int paramInt, Uri paramUri)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 68	android/content/Context:getPackageName	()Ljava/lang/String;
    //   4: astore 11
    //   6: aload_2
    //   7: astore 5
    //   9: invokestatic 270	android/media/RingtoneManagerUtils:getExternalPath	()Ljava/lang/String;
    //   12: astore 12
    //   14: aload_0
    //   15: invokevirtual 381	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   18: getstatic 247	android/provider/MediaStore$Audio$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   21: invokevirtual 387	android/content/ContentResolver:acquireUnstableContentProviderClient	(Landroid/net/Uri;)Landroid/content/ContentProviderClient;
    //   24: astore 10
    //   26: aload 10
    //   28: ifnonnull +14 -> 42
    //   31: ldc 18
    //   33: ldc_w 395
    //   36: invokestatic 107	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   39: pop
    //   40: aload_2
    //   41: areturn
    //   42: getstatic 25	android/media/RingtoneManagerUtils:DBG	Z
    //   45: ifeq +55 -> 100
    //   48: ldc 18
    //   50: new 70	java/lang/StringBuilder
    //   53: dup
    //   54: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   57: ldc_w 397
    //   60: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   63: aload 11
    //   65: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   68: ldc_w 399
    //   71: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   74: iload_1
    //   75: invokevirtual 336	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   78: ldc_w 401
    //   81: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   84: aload_2
    //   85: invokevirtual 112	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   88: ldc 81
    //   90: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   93: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   96: invokestatic 90	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   99: pop
    //   100: iload_1
    //   101: istore_3
    //   102: iconst_1
    //   103: newarray <illegal type>
    //   105: dup
    //   106: iconst_0
    //   107: bipush 6
    //   109: iastore
    //   110: invokestatic 143	android/util/OpFeatures:isSupport	([I)Z
    //   113: ifeq +13 -> 126
    //   116: iload_1
    //   117: istore_3
    //   118: iload_1
    //   119: bipush 100
    //   121: if_icmpne +5 -> 126
    //   124: iconst_2
    //   125: istore_3
    //   126: aload_2
    //   127: invokevirtual 241	android/net/Uri:getLastPathSegment	()Ljava/lang/String;
    //   130: astore 13
    //   132: aconst_null
    //   133: astore 4
    //   135: aconst_null
    //   136: astore 6
    //   138: aconst_null
    //   139: astore 7
    //   141: aconst_null
    //   142: astore 8
    //   144: aload 10
    //   146: getstatic 247	android/provider/MediaStore$Audio$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   149: iconst_4
    //   150: anewarray 40	java/lang/String
    //   153: dup
    //   154: iconst_0
    //   155: ldc -5
    //   157: aastore
    //   158: dup
    //   159: iconst_1
    //   160: ldc_w 403
    //   163: aastore
    //   164: dup
    //   165: iconst_2
    //   166: ldc_w 405
    //   169: aastore
    //   170: dup
    //   171: iconst_3
    //   172: ldc_w 407
    //   175: aastore
    //   176: ldc -3
    //   178: iconst_1
    //   179: anewarray 40	java/lang/String
    //   182: dup
    //   183: iconst_0
    //   184: aload 13
    //   186: aastore
    //   187: aconst_null
    //   188: invokevirtual 259	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   191: astore_0
    //   192: aload_0
    //   193: ifnull +1023 -> 1216
    //   196: aload 8
    //   198: astore 5
    //   200: aload_2
    //   201: astore 4
    //   203: aload_0
    //   204: invokeinterface 264 1 0
    //   209: ifeq +1290 -> 1499
    //   212: aload_0
    //   213: aload_0
    //   214: ldc -5
    //   216: invokeinterface 267 2 0
    //   221: invokeinterface 268 2 0
    //   226: astore 14
    //   228: aload_0
    //   229: aload_0
    //   230: ldc_w 403
    //   233: invokeinterface 267 2 0
    //   238: invokeinterface 268 2 0
    //   243: astore 15
    //   245: aload_0
    //   246: aload_0
    //   247: ldc_w 405
    //   250: invokeinterface 267 2 0
    //   255: invokeinterface 268 2 0
    //   260: astore 16
    //   262: aload_0
    //   263: aload_0
    //   264: ldc_w 407
    //   267: invokeinterface 267 2 0
    //   272: invokeinterface 268 2 0
    //   277: astore 6
    //   279: getstatic 25	android/media/RingtoneManagerUtils:DBG	Z
    //   282: ifeq +46 -> 328
    //   285: ldc 18
    //   287: new 70	java/lang/StringBuilder
    //   290: dup
    //   291: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   294: ldc_w 409
    //   297: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   300: aload 15
    //   302: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   305: ldc_w 411
    //   308: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   311: aload 14
    //   313: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   316: ldc 81
    //   318: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   321: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   324: invokestatic 90	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   327: pop
    //   328: aload 6
    //   330: astore 8
    //   332: ldc_w 413
    //   335: aload 6
    //   337: invokevirtual 417	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   340: ifeq +11 -> 351
    //   343: aload 10
    //   345: aload_2
    //   346: invokestatic 419	android/media/RingtoneManagerUtils:ringtoneCheckMimeType	(Landroid/content/ContentProviderClient;Landroid/net/Uri;)Ljava/lang/String;
    //   349: astore 8
    //   351: aload 8
    //   353: invokestatic 421	android/media/RingtoneManagerUtils:ringtoneAudioIsSupported	(Ljava/lang/String;)Z
    //   356: ifne +53 -> 409
    //   359: ldc 18
    //   361: new 70	java/lang/StringBuilder
    //   364: dup
    //   365: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   368: ldc_w 423
    //   371: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   374: aload 8
    //   376: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   379: ldc_w 425
    //   382: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   385: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   388: invokestatic 428	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   391: pop
    //   392: aload_0
    //   393: invokestatic 284	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   396: aload 10
    //   398: ifnull +9 -> 407
    //   401: aload 10
    //   403: invokevirtual 391	android/content/ContentProviderClient:release	()Z
    //   406: pop
    //   407: aconst_null
    //   408: areturn
    //   409: aload 14
    //   411: invokestatic 431	android/media/RingtoneManagerUtils:ringtoneIsFromDefaultPath	(Ljava/lang/String;)Z
    //   414: ifne +21 -> 435
    //   417: aload 14
    //   419: invokestatic 434	android/media/RingtoneManagerUtils:ringtoneIsManagedBySystem	(Ljava/lang/String;)Z
    //   422: ifeq +13 -> 435
    //   425: aload 14
    //   427: ldc 104
    //   429: invokevirtual 437	java/lang/String:lastIndexOf	(Ljava/lang/String;)I
    //   432: ifgt +65 -> 497
    //   435: ldc 18
    //   437: new 70	java/lang/StringBuilder
    //   440: dup
    //   441: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   444: ldc_w 439
    //   447: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   450: aload 14
    //   452: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   455: ldc_w 441
    //   458: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   461: aload 11
    //   463: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   466: ldc_w 443
    //   469: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   472: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   475: invokestatic 90	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   478: pop
    //   479: aload_0
    //   480: invokestatic 284	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   483: aload 10
    //   485: ifnull +9 -> 494
    //   488: aload 10
    //   490: invokevirtual 391	android/content/ContentProviderClient:release	()Z
    //   493: pop
    //   494: aload 4
    //   496: areturn
    //   497: ldc 18
    //   499: new 70	java/lang/StringBuilder
    //   502: dup
    //   503: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   506: ldc_w 445
    //   509: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   512: aload 14
    //   514: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   517: ldc_w 447
    //   520: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   523: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   526: invokestatic 90	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   529: pop
    //   530: iconst_1
    //   531: newarray <illegal type>
    //   533: dup
    //   534: iconst_0
    //   535: bipush 16
    //   537: iastore
    //   538: invokestatic 143	android/util/OpFeatures:isSupport	([I)Z
    //   541: ifeq +955 -> 1496
    //   544: new 70	java/lang/StringBuilder
    //   547: dup
    //   548: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   551: ldc_w 449
    //   554: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   557: aload 13
    //   559: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   562: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   565: astore 17
    //   567: aload 14
    //   569: aload 14
    //   571: ldc 104
    //   573: invokevirtual 437	java/lang/String:lastIndexOf	(Ljava/lang/String;)I
    //   576: invokevirtual 452	java/lang/String:substring	(I)Ljava/lang/String;
    //   579: astore 4
    //   581: new 70	java/lang/StringBuilder
    //   584: dup
    //   585: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   588: aload 12
    //   590: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   593: ldc_w 454
    //   596: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   599: getstatic 55	android/media/RingtoneManagerUtils:RINGPATH_FROM_TYPE	[Ljava/lang/String;
    //   602: iload_3
    //   603: aaload
    //   604: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   607: ldc_w 454
    //   610: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   613: aload 11
    //   615: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   618: aload 4
    //   620: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   623: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   626: astore 18
    //   628: new 132	java/io/File
    //   631: dup
    //   632: aload 14
    //   634: invokespecial 275	java/io/File:<init>	(Ljava/lang/String;)V
    //   637: astore 19
    //   639: new 132	java/io/File
    //   642: dup
    //   643: aload 18
    //   645: invokespecial 275	java/io/File:<init>	(Ljava/lang/String;)V
    //   648: astore 9
    //   650: aload_0
    //   651: astore 6
    //   653: aload 9
    //   655: astore 7
    //   657: aload_0
    //   658: astore 4
    //   660: aload 9
    //   662: invokevirtual 278	java/io/File:exists	()Z
    //   665: ifeq +122 -> 787
    //   668: aload_0
    //   669: astore 6
    //   671: aload 9
    //   673: astore 7
    //   675: aload_0
    //   676: astore 4
    //   678: getstatic 25	android/media/RingtoneManagerUtils:DBG	Z
    //   681: ifeq +40 -> 721
    //   684: aload_0
    //   685: astore 6
    //   687: aload 9
    //   689: astore 7
    //   691: aload_0
    //   692: astore 4
    //   694: ldc 18
    //   696: new 70	java/lang/StringBuilder
    //   699: dup
    //   700: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   703: ldc_w 456
    //   706: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   709: aload 18
    //   711: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   714: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   717: invokestatic 90	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   720: pop
    //   721: aload_0
    //   722: astore 6
    //   724: aload 9
    //   726: astore 7
    //   728: aload_0
    //   729: astore 4
    //   731: aload 9
    //   733: invokevirtual 459	java/io/File:delete	()Z
    //   736: pop
    //   737: aload_0
    //   738: astore 6
    //   740: aload 9
    //   742: astore 7
    //   744: aload_0
    //   745: astore 4
    //   747: aload 10
    //   749: ldc_w 461
    //   752: invokestatic 467	android/provider/MediaStore$Files:getContentUri	(Ljava/lang/String;)Landroid/net/Uri;
    //   755: new 70	java/lang/StringBuilder
    //   758: dup
    //   759: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   762: ldc_w 469
    //   765: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   768: aload 18
    //   770: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   773: ldc_w 471
    //   776: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   779: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   782: aconst_null
    //   783: invokevirtual 474	android/content/ContentProviderClient:delete	(Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)I
    //   786: pop
    //   787: aload_0
    //   788: astore 6
    //   790: aload 9
    //   792: astore 7
    //   794: aload_0
    //   795: astore 4
    //   797: new 476	android/content/ContentValues
    //   800: dup
    //   801: invokespecial 477	android/content/ContentValues:<init>	()V
    //   804: astore 20
    //   806: aload_0
    //   807: astore 6
    //   809: aload 9
    //   811: astore 7
    //   813: aload_0
    //   814: astore 4
    //   816: aload 20
    //   818: ldc -5
    //   820: aload 18
    //   822: invokevirtual 481	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   825: aload_0
    //   826: astore 6
    //   828: aload 9
    //   830: astore 7
    //   832: aload_0
    //   833: astore 4
    //   835: aload 20
    //   837: ldc_w 403
    //   840: aload 15
    //   842: invokevirtual 481	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   845: aload_0
    //   846: astore 6
    //   848: aload 9
    //   850: astore 7
    //   852: aload_0
    //   853: astore 4
    //   855: aload 20
    //   857: ldc_w 405
    //   860: aload 16
    //   862: invokevirtual 481	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   865: aload_0
    //   866: astore 6
    //   868: aload 9
    //   870: astore 7
    //   872: aload_0
    //   873: astore 4
    //   875: aload 20
    //   877: ldc_w 407
    //   880: aload 8
    //   882: invokevirtual 481	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   885: aload_0
    //   886: astore 6
    //   888: aload 9
    //   890: astore 7
    //   892: aload_0
    //   893: astore 4
    //   895: aload 20
    //   897: ldc_w 483
    //   900: iconst_m1
    //   901: invokestatic 489	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   904: invokevirtual 492	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   907: aload_0
    //   908: astore 6
    //   910: aload 9
    //   912: astore 7
    //   914: aload_0
    //   915: astore 4
    //   917: aload 20
    //   919: ldc_w 494
    //   922: aload 17
    //   924: invokevirtual 481	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   927: iload_3
    //   928: iconst_1
    //   929: if_icmpne +310 -> 1239
    //   932: aload_0
    //   933: astore 6
    //   935: aload 9
    //   937: astore 7
    //   939: aload_0
    //   940: astore 4
    //   942: aload 20
    //   944: ldc_w 496
    //   947: iconst_1
    //   948: invokestatic 501	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   951: invokevirtual 504	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Boolean;)V
    //   954: aload 9
    //   956: astore 5
    //   958: aload_0
    //   959: astore 6
    //   961: aload 9
    //   963: astore 7
    //   965: aload_0
    //   966: astore 4
    //   968: aload 19
    //   970: invokevirtual 278	java/io/File:exists	()Z
    //   973: ifeq -773 -> 200
    //   976: aload_0
    //   977: astore 6
    //   979: aload 9
    //   981: astore 7
    //   983: aload_0
    //   984: astore 4
    //   986: getstatic 25	android/media/RingtoneManagerUtils:DBG	Z
    //   989: ifeq +51 -> 1040
    //   992: aload_0
    //   993: astore 6
    //   995: aload 9
    //   997: astore 7
    //   999: aload_0
    //   1000: astore 4
    //   1002: ldc 18
    //   1004: new 70	java/lang/StringBuilder
    //   1007: dup
    //   1008: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   1011: ldc_w 506
    //   1014: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1017: aload 14
    //   1019: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1022: ldc_w 508
    //   1025: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1028: aload 18
    //   1030: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1033: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1036: invokestatic 90	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1039: pop
    //   1040: aload_0
    //   1041: astore 6
    //   1043: aload 9
    //   1045: astore 7
    //   1047: aload_0
    //   1048: astore 4
    //   1050: aload 19
    //   1052: aload 9
    //   1054: invokestatic 514	android/os/FileUtils:copyFile	(Ljava/io/File;Ljava/io/File;)Z
    //   1057: ifeq +357 -> 1414
    //   1060: aload_0
    //   1061: astore 6
    //   1063: aload 9
    //   1065: astore 7
    //   1067: aload_0
    //   1068: astore 4
    //   1070: ldc 18
    //   1072: new 70	java/lang/StringBuilder
    //   1075: dup
    //   1076: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   1079: ldc_w 516
    //   1082: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1085: aload 11
    //   1087: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1090: ldc_w 518
    //   1093: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1096: aload 18
    //   1098: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1101: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1104: invokestatic 90	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1107: pop
    //   1108: aload 9
    //   1110: astore 5
    //   1112: aload_0
    //   1113: astore 6
    //   1115: aload 9
    //   1117: astore 7
    //   1119: aload_0
    //   1120: astore 4
    //   1122: aload 9
    //   1124: invokevirtual 278	java/io/File:exists	()Z
    //   1127: ifeq -927 -> 200
    //   1130: aload_0
    //   1131: astore 6
    //   1133: aload 9
    //   1135: astore 7
    //   1137: aload_0
    //   1138: astore 4
    //   1140: aload 10
    //   1142: ldc_w 461
    //   1145: invokestatic 467	android/provider/MediaStore$Files:getContentUri	(Ljava/lang/String;)Landroid/net/Uri;
    //   1148: aload 20
    //   1150: invokevirtual 522	android/content/ContentProviderClient:insert	(Landroid/net/Uri;Landroid/content/ContentValues;)Landroid/net/Uri;
    //   1153: astore 8
    //   1155: aload 8
    //   1157: astore 5
    //   1159: aload_0
    //   1160: astore 6
    //   1162: aload 9
    //   1164: astore 7
    //   1166: aload_0
    //   1167: astore 4
    //   1169: getstatic 25	android/media/RingtoneManagerUtils:DBG	Z
    //   1172: ifeq +44 -> 1216
    //   1175: aload_0
    //   1176: astore 6
    //   1178: aload 9
    //   1180: astore 7
    //   1182: aload_0
    //   1183: astore 4
    //   1185: ldc 18
    //   1187: new 70	java/lang/StringBuilder
    //   1190: dup
    //   1191: invokespecial 71	java/lang/StringBuilder:<init>	()V
    //   1194: ldc_w 524
    //   1197: invokevirtual 77	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1200: aload 8
    //   1202: invokevirtual 112	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1205: invokevirtual 84	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1208: invokestatic 90	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   1211: pop
    //   1212: aload 8
    //   1214: astore 5
    //   1216: aload_0
    //   1217: invokestatic 284	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   1220: aload 5
    //   1222: astore_0
    //   1223: aload 10
    //   1225: ifnull +12 -> 1237
    //   1228: aload 10
    //   1230: invokevirtual 391	android/content/ContentProviderClient:release	()Z
    //   1233: pop
    //   1234: aload 5
    //   1236: astore_0
    //   1237: aload_0
    //   1238: areturn
    //   1239: iload_3
    //   1240: iconst_2
    //   1241: if_icmpne +127 -> 1368
    //   1244: aload_0
    //   1245: astore 6
    //   1247: aload 9
    //   1249: astore 7
    //   1251: aload_0
    //   1252: astore 4
    //   1254: aload 20
    //   1256: ldc_w 526
    //   1259: iconst_1
    //   1260: invokestatic 501	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   1263: invokevirtual 504	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Boolean;)V
    //   1266: goto -312 -> 954
    //   1269: astore 5
    //   1271: aload 6
    //   1273: astore_0
    //   1274: aload_0
    //   1275: astore 4
    //   1277: ldc_w 527
    //   1280: aload 5
    //   1282: invokestatic 31	android/media/RingtoneManagerUtils:printExceptionLogs	(Ljava/lang/String;Ljava/lang/Exception;)V
    //   1285: aload 7
    //   1287: ifnull +62 -> 1349
    //   1290: aload_0
    //   1291: astore 4
    //   1293: aload 7
    //   1295: invokevirtual 278	java/io/File:exists	()Z
    //   1298: ifeq +51 -> 1349
    //   1301: aload_0
    //   1302: astore 4
    //   1304: aload 7
    //   1306: invokevirtual 135	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   1309: astore 5
    //   1311: aload_0
    //   1312: astore 4
    //   1314: aload 7
    //   1316: invokevirtual 459	java/io/File:delete	()Z
    //   1319: pop
    //   1320: aload 5
    //   1322: ifnull +27 -> 1349
    //   1325: aload_0
    //   1326: astore 4
    //   1328: new 529	java/lang/Thread
    //   1331: dup
    //   1332: new 8	android/media/RingtoneManagerUtils$2
    //   1335: dup
    //   1336: aload 10
    //   1338: aload 5
    //   1340: invokespecial 532	android/media/RingtoneManagerUtils$2:<init>	(Landroid/content/ContentProviderClient;Ljava/lang/String;)V
    //   1343: invokespecial 535	java/lang/Thread:<init>	(Ljava/lang/Runnable;)V
    //   1346: invokevirtual 538	java/lang/Thread:start	()V
    //   1349: aload_0
    //   1350: invokestatic 284	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   1353: aload_2
    //   1354: astore_0
    //   1355: aload 10
    //   1357: ifnull -120 -> 1237
    //   1360: aload 10
    //   1362: invokevirtual 391	android/content/ContentProviderClient:release	()Z
    //   1365: pop
    //   1366: aload_2
    //   1367: areturn
    //   1368: aload_0
    //   1369: astore 6
    //   1371: aload 9
    //   1373: astore 7
    //   1375: aload_0
    //   1376: astore 4
    //   1378: aload 20
    //   1380: ldc_w 540
    //   1383: iconst_1
    //   1384: invokestatic 501	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   1387: invokevirtual 504	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Boolean;)V
    //   1390: goto -436 -> 954
    //   1393: astore_2
    //   1394: aload 4
    //   1396: astore_0
    //   1397: aload_0
    //   1398: invokestatic 284	libcore/io/IoUtils:closeQuietly	(Ljava/lang/AutoCloseable;)V
    //   1401: aload 10
    //   1403: ifnull +9 -> 1412
    //   1406: aload 10
    //   1408: invokevirtual 391	android/content/ContentProviderClient:release	()Z
    //   1411: pop
    //   1412: aload_2
    //   1413: athrow
    //   1414: aload_0
    //   1415: astore 6
    //   1417: aload 9
    //   1419: astore 7
    //   1421: aload_0
    //   1422: astore 4
    //   1424: ldc 18
    //   1426: ldc_w 542
    //   1429: invokestatic 107	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   1432: pop
    //   1433: aload 9
    //   1435: ifnull +37 -> 1472
    //   1438: aload_0
    //   1439: astore 6
    //   1441: aload 9
    //   1443: astore 7
    //   1445: aload_0
    //   1446: astore 4
    //   1448: aload 9
    //   1450: invokevirtual 278	java/io/File:exists	()Z
    //   1453: ifeq +19 -> 1472
    //   1456: aload_0
    //   1457: astore 6
    //   1459: aload 9
    //   1461: astore 7
    //   1463: aload_0
    //   1464: astore 4
    //   1466: aload 9
    //   1468: invokevirtual 459	java/io/File:delete	()Z
    //   1471: pop
    //   1472: aload 9
    //   1474: astore 5
    //   1476: goto -1276 -> 200
    //   1479: astore_2
    //   1480: goto -83 -> 1397
    //   1483: astore 4
    //   1485: aload 5
    //   1487: astore 7
    //   1489: aload 4
    //   1491: astore 5
    //   1493: goto -219 -> 1274
    //   1496: goto -1296 -> 200
    //   1499: aload 4
    //   1501: astore 5
    //   1503: goto -287 -> 1216
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1506	0	paramContext	Context
    //   0	1506	1	paramInt	int
    //   0	1506	2	paramUri	Uri
    //   101	1141	3	i	int
    //   133	1332	4	localObject1	Object
    //   1483	17	4	localException1	Exception
    //   7	1228	5	localObject2	Object
    //   1269	12	5	localException2	Exception
    //   1309	193	5	localObject3	Object
    //   136	1322	6	localObject4	Object
    //   139	1349	7	localObject5	Object
    //   142	1071	8	localObject6	Object
    //   648	825	9	localFile1	File
    //   24	1383	10	localContentProviderClient	ContentProviderClient
    //   4	1082	11	str1	String
    //   12	577	12	str2	String
    //   130	428	13	str3	String
    //   226	792	14	str4	String
    //   243	598	15	str5	String
    //   260	601	16	str6	String
    //   565	358	17	str7	String
    //   626	471	18	str8	String
    //   637	414	19	localFile2	File
    //   804	575	20	localContentValues	android.content.ContentValues
    // Exception table:
    //   from	to	target	type
    //   144	192	1269	java/lang/Exception
    //   660	668	1269	java/lang/Exception
    //   678	684	1269	java/lang/Exception
    //   694	721	1269	java/lang/Exception
    //   731	737	1269	java/lang/Exception
    //   747	787	1269	java/lang/Exception
    //   797	806	1269	java/lang/Exception
    //   816	825	1269	java/lang/Exception
    //   835	845	1269	java/lang/Exception
    //   855	865	1269	java/lang/Exception
    //   875	885	1269	java/lang/Exception
    //   895	907	1269	java/lang/Exception
    //   917	927	1269	java/lang/Exception
    //   942	954	1269	java/lang/Exception
    //   968	976	1269	java/lang/Exception
    //   986	992	1269	java/lang/Exception
    //   1002	1040	1269	java/lang/Exception
    //   1050	1060	1269	java/lang/Exception
    //   1070	1108	1269	java/lang/Exception
    //   1122	1130	1269	java/lang/Exception
    //   1140	1155	1269	java/lang/Exception
    //   1169	1175	1269	java/lang/Exception
    //   1185	1212	1269	java/lang/Exception
    //   1254	1266	1269	java/lang/Exception
    //   1378	1390	1269	java/lang/Exception
    //   1424	1433	1269	java/lang/Exception
    //   1448	1456	1269	java/lang/Exception
    //   1466	1472	1269	java/lang/Exception
    //   144	192	1393	finally
    //   660	668	1393	finally
    //   678	684	1393	finally
    //   694	721	1393	finally
    //   731	737	1393	finally
    //   747	787	1393	finally
    //   797	806	1393	finally
    //   816	825	1393	finally
    //   835	845	1393	finally
    //   855	865	1393	finally
    //   875	885	1393	finally
    //   895	907	1393	finally
    //   917	927	1393	finally
    //   942	954	1393	finally
    //   968	976	1393	finally
    //   986	992	1393	finally
    //   1002	1040	1393	finally
    //   1050	1060	1393	finally
    //   1070	1108	1393	finally
    //   1122	1130	1393	finally
    //   1140	1155	1393	finally
    //   1169	1175	1393	finally
    //   1185	1212	1393	finally
    //   1254	1266	1393	finally
    //   1277	1285	1393	finally
    //   1293	1301	1393	finally
    //   1304	1311	1393	finally
    //   1314	1320	1393	finally
    //   1328	1349	1393	finally
    //   1378	1390	1393	finally
    //   1424	1433	1393	finally
    //   1448	1456	1393	finally
    //   1466	1472	1393	finally
    //   203	328	1479	finally
    //   332	351	1479	finally
    //   351	392	1479	finally
    //   409	435	1479	finally
    //   435	479	1479	finally
    //   497	650	1479	finally
    //   203	328	1483	java/lang/Exception
    //   332	351	1483	java/lang/Exception
    //   351	392	1483	java/lang/Exception
    //   409	435	1483	java/lang/Exception
    //   435	479	1483	java/lang/Exception
    //   497	650	1483	java/lang/Exception
  }
  
  private static Uri ringtoneGetOriginalUri(ContentProviderClient paramContentProviderClient, Uri paramUri)
    throws RemoteException
  {
    Object localObject2 = paramUri;
    String str = null;
    Object localObject1 = localObject2;
    if (ringtoneIsExternal(paramUri))
    {
      str = paramUri.getLastPathSegment();
      paramUri = paramContentProviderClient.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "_data", "composer" }, "_id=?", new String[] { str }, null);
      if (paramUri == null) {
        break label285;
      }
      localObject1 = localObject2;
    }
    label285:
    for (;;)
    {
      try
      {
        if (paramUri.moveToNext())
        {
          paramContentProviderClient = paramUri.getString(paramUri.getColumnIndex("_data"));
          localObject2 = paramUri.getString(paramUri.getColumnIndex("composer"));
          if ((ringtoneIsFromDefaultPath(paramContentProviderClient)) && (localObject2 != null) && (((String)localObject2).startsWith("from3rdParty_")))
          {
            paramContentProviderClient = ((String)localObject2).substring(((String)localObject2).lastIndexOf("_") + 1);
            paramContentProviderClient = Uri.parse("content://media/external/audio/media/" + paramContentProviderClient);
            localObject1 = paramContentProviderClient;
            if (DBG)
            {
              Log.d("RingtoneManagerUtils", "ringtoneGetOriginalUri: orig_Uri = " + paramContentProviderClient.toString());
              localObject1 = paramContentProviderClient;
            }
          }
        }
        else
        {
          localObject1 = null;
        }
      }
      catch (Exception paramContentProviderClient)
      {
        printExceptionLogs("ringtoneGetOriginalUri ", paramContentProviderClient);
        localObject1 = null;
        IoUtils.closeQuietly(paramUri);
        if (DBG) {
          Log.d("RingtoneManagerUtils", "ringtoneGetOriginalUri: file_uri[" + str + "] is from [" + localObject1 + "]");
        }
        return (Uri)localObject1;
        IoUtils.closeQuietly(paramUri);
      }
      finally
      {
        IoUtils.closeQuietly(paramUri);
      }
    }
  }
  
  private static String ringtoneGetPath(ContentProviderClient paramContentProviderClient, Uri paramUri)
    throws RemoteException
  {
    localObject2 = null;
    Object localObject3 = null;
    String str = paramUri.getLastPathSegment();
    localObject1 = localObject2;
    if (ringtoneIsExternal(paramUri))
    {
      paramUri = paramContentProviderClient.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "_data" }, "_id=?", new String[] { str }, null);
      localObject1 = localObject2;
      if (paramUri == null) {}
    }
    for (;;)
    {
      paramContentProviderClient = (ContentProviderClient)localObject3;
      try
      {
        if (paramUri.moveToNext())
        {
          paramContentProviderClient = paramUri.getString(paramUri.getColumnIndex("_data"));
          if (paramContentProviderClient == null) {}
        }
        else
        {
          IoUtils.closeQuietly(paramUri);
          localObject1 = paramContentProviderClient;
        }
      }
      catch (Exception paramContentProviderClient)
      {
        for (;;)
        {
          printExceptionLogs("ringtoneGetPath", paramContentProviderClient);
          IoUtils.closeQuietly(paramUri);
          localObject1 = localObject2;
        }
      }
      finally
      {
        IoUtils.closeQuietly(paramUri);
      }
    }
    if (DBG) {
      Log.d("RingtoneManagerUtils", "ringtoneGetPath: [" + str + "] is located under " + (String)localObject1);
    }
    return (String)localObject1;
  }
  
  private static boolean ringtoneIsExternal(Uri paramUri)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramUri != null)
    {
      bool1 = bool2;
      if (paramUri.toString().startsWith(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString())) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  private static boolean ringtoneIsFromDefaultPath(String paramString)
  {
    String str = getExternalPath();
    int i = 0;
    while (i < RINGPATH_FROM_TYPE.length)
    {
      if ((RINGPATH_FROM_TYPE[i].length() > 0) && (paramString.startsWith(str + "/" + RINGPATH_FROM_TYPE[i]))) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private static boolean ringtoneIsManagedBySystem(String paramString)
  {
    boolean bool2 = false;
    if (paramString != null)
    {
      boolean bool1;
      if (paramString.lastIndexOf(".") <= 0)
      {
        bool1 = false;
        if (bool1) {
          Log.v("RingtoneManagerUtils", "ringtoneIsManagedBySystem: [" + paramString + "] will be managed by system.");
        }
        return bool1;
      }
      int i = 0;
      for (;;)
      {
        bool1 = bool2;
        if (i >= MANAGED_RING_PATH.length) {
          break;
        }
        if ((!MANAGED_RING_PATH[i].isEmpty()) && (paramString.toLowerCase().contains(MANAGED_RING_PATH[i].toLowerCase())))
        {
          bool1 = true;
          break;
        }
        i += 1;
      }
    }
    Log.w("RingtoneManagerUtils", "ringtoneIsManagedBySystem: ringPath is empty");
    return true;
  }
  
  private static Uri ringtoneRestoreFromDefault(Context paramContext, ContentProviderClient paramContentProviderClient, String paramString, Uri paramUri)
    throws RemoteException
  {
    if (DBG) {
      Log.d("RingtoneManagerUtils", "ringtoneRestoreFromDefault: " + paramString);
    }
    int i = RingtoneManager.getDefaultType(Settings.System.getUriFor(paramString));
    localObject1 = null;
    if ((i & 0x1) != 0) {
      localObject1 = "is_ringtone";
    }
    if (((i & 0x2) != 0) || ((i & 0x8) != 0)) {
      localObject1 = "is_notification";
    }
    if ((i & 0x4) != 0) {
      localObject1 = "is_alarm";
    }
    Object localObject2 = paramUri;
    if (localObject1 != null)
    {
      if (!paramString.startsWith("ringtone")) {
        break label401;
      }
      paramString = "ringtone";
    }
    for (;;)
    {
      localObject2 = SystemProperties.get("ro.config." + "ringtone");
      String str = SystemProperties.get("ro.config." + paramString, (String)localObject2);
      if (DBG) {
        Log.d("RingtoneManagerUtils", "ringtoneRestoreFromDefault: fileName = " + str);
      }
      paramString = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
      localObject1 = (String)localObject1 + "=1 and Lower(" + "_display_name" + ")=?";
      localObject2 = str.toLowerCase();
      localObject1 = paramContentProviderClient.query(paramString, new String[] { "_id" }, (String)localObject1, new String[] { localObject2 }, null, null);
      localObject2 = paramUri;
      if (localObject1 != null)
      {
        paramContentProviderClient = paramUri;
        paramString = paramUri;
      }
      try
      {
        if (((Cursor)localObject1).getCount() > 0)
        {
          paramContentProviderClient = paramUri;
          paramString = paramUri;
          if (((Cursor)localObject1).moveToFirst())
          {
            paramString = paramUri;
            long l = ((Cursor)localObject1).getLong(0);
            paramString = paramUri;
            paramContentProviderClient = ContentUris.withAppendedId(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, l);
            paramString = paramContentProviderClient;
            if (DBG)
            {
              paramString = paramContentProviderClient;
              Log.d("RingtoneManagerUtils", "ringtoneRestoreFromDefault: [" + str + "] = " + paramContentProviderClient.toString());
            }
            paramString = paramContentProviderClient;
            RingtoneManager.setActualDefaultRingtoneUri(paramContext, i, paramContentProviderClient);
          }
        }
        IoUtils.closeQuietly((AutoCloseable)localObject1);
        localObject2 = paramContentProviderClient;
        return (Uri)localObject2;
      }
      catch (Exception paramContext)
      {
        label401:
        printExceptionLogs("ringtoneRestoreFromDefault", paramContext);
        return paramString;
      }
      finally
      {
        IoUtils.closeQuietly((AutoCloseable)localObject1);
      }
    }
  }
  
  private static String ringtoneTypeCheck(Context paramContext, Uri paramUri)
  {
    String[] arrayOfString = new String[4];
    arrayOfString[0] = "ringtone";
    arrayOfString[1] = "notification_sound";
    arrayOfString[2] = "mms_notification";
    arrayOfString[3] = "alarm_alert";
    paramUri = paramUri.getLastPathSegment();
    paramContext = paramContext.getContentResolver();
    if ((paramUri != null) && (paramContext != null))
    {
      int i = 0;
      while (i < arrayOfString.length)
      {
        String str = Settings.System.getString(paramContext, arrayOfString[i]);
        if ((str != null) && (str.endsWith("/" + paramUri))) {
          return arrayOfString[i];
        }
        i += 1;
      }
    }
    return "ringtone";
  }
  
  private static boolean ringtoneValidation(ContentProviderClient paramContentProviderClient, Uri paramUri)
    throws RemoteException
  {
    boolean bool2 = ringtoneCheckValid(paramContentProviderClient, paramUri);
    boolean bool3 = bool2;
    boolean bool1;
    if (ringtoneIsExternal(paramUri))
    {
      bool1 = bool2;
      if (bool2)
      {
        Uri localUri = ringtoneGetOriginalUri(paramContentProviderClient, paramUri);
        bool1 = bool2;
        if (localUri != null)
        {
          bool1 = bool2;
          if (localUri != paramUri)
          {
            String str = ringtoneGetPath(paramContentProviderClient, paramUri);
            bool1 = bool2;
            if (!ringtoneCheckValid(paramContentProviderClient, localUri))
            {
              Log.e("RingtoneManagerUtils", "ringtoneValidation: Removing " + str);
              paramContentProviderClient = new File(str);
              if (paramContentProviderClient.exists())
              {
                Log.d("RingtoneManagerUtils", "ringtoneValidation: Removing the backup ringtone in " + str);
                paramContentProviderClient.delete();
              }
              bool1 = false;
            }
          }
        }
      }
      bool3 = bool1;
      if (DBG)
      {
        paramUri = new StringBuilder().append("ringtoneValidation: file_uri[").append(paramUri.toString()).append("] : ");
        if (!bool1) {
          break label204;
        }
      }
    }
    label204:
    for (paramContentProviderClient = "alive";; paramContentProviderClient = "gone")
    {
      Log.d("RingtoneManagerUtils", paramContentProviderClient);
      bool3 = bool1;
      return bool3;
    }
  }
  
  public static Uri validForSound(Context paramContext, final Uri paramUri, String paramString)
  {
    Object localObject3 = paramUri;
    if (paramUri != null) {}
    for (localObject1 = paramUri.getScheme();; localObject1 = null)
    {
      localObject2 = localObject3;
      if (paramUri == null) {
        break label713;
      }
      localObject2 = localObject3;
      if (paramContext == null) {
        break label713;
      }
      localObject2 = localObject3;
      if (!"content".equals(localObject1)) {
        break label713;
      }
      localContentProviderClient = paramContext.getContentResolver().acquireUnstableContentProviderClient(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
      if (localContentProviderClient != null) {
        break;
      }
      Log.w("RingtoneManagerUtils", "Fail to acquire provider client. Skip validForSound.");
      return paramUri;
    }
    localObject1 = localObject3;
    for (;;)
    {
      try
      {
        if (DBG)
        {
          localObject1 = localObject3;
          Log.d("RingtoneManagerUtils", "validForSound from [" + paramContext.getPackageName() + "]: sound_uri = " + paramUri.toString() + ", sound_type(" + paramString + ")");
        }
        localObject2 = localObject3;
        str = paramString;
        localObject1 = localObject3;
        if ("settings".equals(paramUri.getAuthority()))
        {
          localObject1 = localObject3;
          str = paramUri.getLastPathSegment();
          localObject1 = localObject3;
          localObject2 = RingtoneManager.getActualDefaultRingtoneUri(paramContext, RingtoneManager.getDefaultType(paramUri));
          localObject1 = localObject3;
          if (confirmExternalPermission(paramContext, (Uri)localObject2, "android.permission.READ_EXTERNAL_STORAGE"))
          {
            localObject1 = localObject3;
            if (ringtoneCheckValid(localContentProviderClient, (Uri)localObject2))
            {
              localObject1 = localObject3;
              if (ringtoneValidation(localContentProviderClient, (Uri)localObject2))
              {
                localObject1 = localObject3;
                if (DBG)
                {
                  localObject1 = localObject3;
                  Log.d("RingtoneManagerUtils", "Valid ringtone(" + str + ") = " + paramUri);
                }
                localObject1 = localObject3;
                if ("mms_notification".equals(str))
                {
                  localObject1 = localObject3;
                  Log.d("RingtoneManagerUtils", "Return actualSoundUri for type[" + str + "] = " + localObject2);
                  localObject1 = localObject3;
                  if (DBG)
                  {
                    localObject1 = localObject3;
                    Log.v("RingtoneManagerUtils", " === Stack Dump Start === ", new Throwable());
                    localObject1 = localObject3;
                    Log.v("RingtoneManagerUtils", " === Stack Dump End === ");
                  }
                  return (Uri)localObject2;
                }
                return paramUri;
              }
            }
          }
        }
        localObject3 = localObject2;
        localObject1 = localObject2;
        if (confirmExternalPermission(paramContext, (Uri)localObject2, "android.permission.READ_EXTERNAL_STORAGE"))
        {
          localObject1 = localObject2;
          localObject3 = ringtoneGetPath(localContentProviderClient, (Uri)localObject2);
          paramString = (String)localObject2;
          if (localObject3 != null)
          {
            paramString = (String)localObject2;
            localObject1 = localObject2;
            if (ringtoneIsFromDefaultPath((String)localObject3))
            {
              paramString = (String)localObject2;
              localObject1 = localObject2;
              if (!new File((String)localObject3).exists())
              {
                localObject1 = localObject2;
                localObject3 = ringtoneGetOriginalUri(localContentProviderClient, (Uri)localObject2);
                paramString = (String)localObject2;
                if (localObject3 != null)
                {
                  paramString = (String)localObject2;
                  if (localObject3 != localObject2)
                  {
                    localObject1 = localObject2;
                    Log.d("RingtoneManagerUtils", "validForSound: The backup ringtone was gone. Will use the original one instead.");
                    localObject2 = localObject3;
                    paramString = (String)localObject2;
                    localObject1 = localObject2;
                    if (DBG)
                    {
                      localObject1 = localObject2;
                      Log.d("RingtoneManagerUtils", "validForSound: validUri was changed to " + localObject3);
                      paramString = (String)localObject2;
                    }
                  }
                }
              }
            }
          }
          localObject3 = paramString;
          localObject1 = paramString;
          if (!ringtoneValidation(localContentProviderClient, paramString))
          {
            localObject1 = paramString;
            if (DBG)
            {
              localObject1 = paramString;
              Log.d("RingtoneManagerUtils", "validForSound: Sound was gone.");
            }
            if (str != null) {
              continue;
            }
            localObject1 = paramString;
            localObject2 = ringtoneTypeCheck(paramContext, paramString);
            localObject1 = paramString;
            paramString = ringtoneRestoreFromDefault(paramContext, localContentProviderClient, (String)localObject2, paramString);
            localObject3 = paramString;
            localObject1 = paramString;
            if (OpFeatures.isSupport(new int[] { 16 }))
            {
              localObject3 = paramString;
              if (paramUri != paramString)
              {
                localObject3 = paramString;
                localObject1 = paramString;
                if (confirmExternalPermission(paramContext, paramUri, "android.permission.WRITE_EXTERNAL_STORAGE"))
                {
                  localObject1 = paramString;
                  new Thread(new Runnable()
                  {
                    public void run()
                    {
                      try
                      {
                        if (RingtoneManagerUtils.-get0()) {
                          Log.d("RingtoneManagerUtils", "validForSound: refreshing database.");
                        }
                        this.val$client.delete(paramUri, null, null);
                        return;
                      }
                      catch (SecurityException|RemoteException localSecurityException)
                      {
                        RingtoneManagerUtils.-wrap0("Thread fail", localSecurityException);
                      }
                    }
                  }).start();
                  localObject3 = paramString;
                }
              }
            }
          }
        }
        localObject2 = localObject3;
        if (localContentProviderClient != null)
        {
          localContentProviderClient.release();
          localObject2 = localObject3;
        }
      }
      catch (Exception paramContext)
      {
        String str;
        label713:
        printExceptionLogs("validForSound", paramContext);
        localObject2 = localObject1;
        return (Uri)localObject1;
      }
      finally
      {
        if (localContentProviderClient == null) {
          continue;
        }
        localContentProviderClient.release();
      }
      return (Uri)localObject2;
      localObject2 = str;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/RingtoneManagerUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */