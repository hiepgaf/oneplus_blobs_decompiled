package android.media;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.os.Process;
import android.provider.MediaStore.Audio.Media;
import android.provider.Settings.System;
import android.util.Log;
import com.android.internal.database.SortCursor;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import libcore.io.IoUtils;

public class RingtoneManager
{
  public static final String ACTION_RINGTONE_PICKER = "android.intent.action.RINGTONE_PICKER";
  private static boolean DBG = Build.DEBUG_ONEPLUS;
  public static final String EXTRA_RINGTONE_AUDIO_ATTRIBUTES_FLAGS = "android.intent.extra.ringtone.AUDIO_ATTRIBUTES_FLAGS";
  public static final String EXTRA_RINGTONE_DEFAULT_URI = "android.intent.extra.ringtone.DEFAULT_URI";
  public static final String EXTRA_RINGTONE_EXISTING_URI = "android.intent.extra.ringtone.EXISTING_URI";
  @Deprecated
  public static final String EXTRA_RINGTONE_INCLUDE_DRM = "android.intent.extra.ringtone.INCLUDE_DRM";
  public static final String EXTRA_RINGTONE_PICKED_URI = "android.intent.extra.ringtone.PICKED_URI";
  public static final String EXTRA_RINGTONE_SHOW_DEFAULT = "android.intent.extra.ringtone.SHOW_DEFAULT";
  public static final String EXTRA_RINGTONE_SHOW_SILENT = "android.intent.extra.ringtone.SHOW_SILENT";
  public static final String EXTRA_RINGTONE_TITLE = "android.intent.extra.ringtone.TITLE";
  public static final String EXTRA_RINGTONE_TYPE = "android.intent.extra.ringtone.TYPE";
  public static final int ID_COLUMN_INDEX = 0;
  private static final String[] INTERNAL_COLUMNS = { "_id", "title", "\"" + MediaStore.Audio.Media.INTERNAL_CONTENT_URI + "\"", "title_key", "_display_name" };
  private static final String[] MEDIA_COLUMNS = { "_id", "title", "\"" + MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "\"", "title_key", "_display_name" };
  private static final String TAG = "RingtoneManager";
  public static final int TITLE_COLUMN_INDEX = 1;
  public static final int TYPE_ALARM = 4;
  public static final int TYPE_ALL = 7;
  public static final int TYPE_MMS_NOTIFICATION = 8;
  public static final int TYPE_NOTIFICATION = 2;
  public static final int TYPE_RINGTONE = 1;
  public static final int URI_COLUMN_INDEX = 2;
  private final Activity mActivity;
  private final Context mContext;
  private Cursor mCursor;
  private final List<String> mFilterColumns = new ArrayList();
  private Ringtone mPreviousRingtone;
  private boolean mStopPreviousRingtone = true;
  private int mType = 1;
  
  public RingtoneManager(Activity paramActivity)
  {
    this.mActivity = paramActivity;
    this.mContext = paramActivity;
    setType(this.mType);
  }
  
  public RingtoneManager(Context paramContext)
  {
    this.mActivity = null;
    this.mContext = paramContext;
    setType(this.mType);
  }
  
  private static String constructBooleanTrueWhereClause(List<String> paramList)
  {
    if (paramList == null) {
      return null;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("(");
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      localStringBuilder.append((String)paramList.get(i)).append("=1 or ");
      i -= 1;
    }
    if (paramList.size() > 0) {
      localStringBuilder.setLength(localStringBuilder.length() - 4);
    }
    localStringBuilder.append(")");
    return localStringBuilder.toString();
  }
  
  public static Uri getActualDefaultRingtoneUri(Context paramContext, int paramInt)
  {
    Object localObject = null;
    String str = getSettingForType(paramInt);
    if (str == null) {
      return null;
    }
    str = Settings.System.getStringForUser(paramContext.getContentResolver(), str, paramContext.getUserId());
    paramContext = (Context)localObject;
    if (str != null) {
      paramContext = Uri.parse(str);
    }
    return paramContext;
  }
  
  public static Uri getActualRingtoneUriBySubId(Context paramContext, int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 2)) {
      return null;
    }
    if (paramInt == 0) {}
    String str;
    for (localObject1 = "ringtone";; localObject1 = "ringtone_" + (paramInt + 1))
    {
      str = Settings.System.getStringForUser(paramContext.getContentResolver(), (String)localObject1, paramContext.getUserId());
      if (str != null) {
        break;
      }
      if (DBG) {
        Log.d("RingtoneManager", "getActualRingtoneUriBySubId(" + paramInt + ") = " + str);
      }
      return null;
    }
    localUri3 = getStaticDefaultRingtoneUri(paramContext);
    localObject2 = null;
    localObject1 = null;
    try
    {
      Cursor localCursor = paramContext.getContentResolver().query(Uri.parse(str), null, null, null, null);
      localUri1 = localUri3;
      if (localCursor != null)
      {
        localUri1 = localUri3;
        localObject1 = localCursor;
        localObject2 = localCursor;
        if (localCursor.getCount() > 0)
        {
          localObject1 = localCursor;
          localObject2 = localCursor;
          localUri1 = Uri.parse(str);
        }
      }
      IoUtils.closeQuietly(localCursor);
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        Uri localUri1;
        localObject2 = localObject1;
        Log.e("RingtoneManager", "ex " + localSQLiteException);
        IoUtils.closeQuietly((AutoCloseable)localObject1);
        Uri localUri2 = localUri3;
      }
    }
    finally
    {
      IoUtils.closeQuietly((AutoCloseable)localObject2);
    }
    localObject1 = localUri1;
    if (localUri1 == null)
    {
      Log.w("RingtoneManager", "getActualRingtoneUriBySubId(" + paramInt + ") failed. Use actual default instead.");
      localObject1 = getActualDefaultRingtoneUri(paramContext, 1);
    }
    if (DBG) {
      Log.d("RingtoneManager", "getActualRingtoneUriBySubId(" + paramInt + ") of user[" + paramContext.getUserId() + "] = " + localObject1);
    }
    return (Uri)localObject1;
  }
  
  public static Uri getCacheForType(int paramInt)
  {
    if ((paramInt & 0x1) != 0) {
      return Settings.System.RINGTONE_CACHE_URI;
    }
    if ((paramInt & 0x2) != 0) {
      return Settings.System.NOTIFICATION_SOUND_CACHE_URI;
    }
    if ((paramInt & 0x8) != 0) {
      return Settings.System.MMS_NOTIFICATION_CACHE_URI;
    }
    if ((paramInt & 0x4) != 0) {
      return Settings.System.ALARM_ALERT_CACHE_URI;
    }
    return null;
  }
  
  public static int getDefaultRingtoneSubIdByUri(Uri paramUri)
  {
    if (paramUri == null) {
      return -1;
    }
    if (paramUri.equals(Settings.System.DEFAULT_RINGTONE_URI)) {
      return 0;
    }
    paramUri = paramUri.toString();
    if (paramUri.startsWith(Settings.System.DEFAULT_RINGTONE_URI.toString()))
    {
      int i = Integer.parseInt(paramUri.substring(paramUri.lastIndexOf("_") + 1));
      if ((i > 0) && (i <= 2)) {
        return i - 1;
      }
    }
    return -1;
  }
  
  public static Uri getDefaultRingtoneUriBySubId(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= 2)) {
      return null;
    }
    if (paramInt == 0) {
      return Settings.System.DEFAULT_RINGTONE_URI;
    }
    return Uri.parse(Settings.System.DEFAULT_RINGTONE_URI.toString() + "_" + (paramInt + 1));
  }
  
  public static int getDefaultType(Uri paramUri)
  {
    if (paramUri == null) {
      return -1;
    }
    if ((paramUri.equals(Settings.System.DEFAULT_RINGTONE_URI)) || (paramUri.equals(Settings.System.DEFAULT_RINGTONE_URI_2))) {
      return 1;
    }
    if (paramUri.equals(Settings.System.DEFAULT_NOTIFICATION_URI)) {
      return 2;
    }
    if (paramUri.equals(Settings.System.DEFAULT_ALARM_ALERT_URI)) {
      return 4;
    }
    if (paramUri.equals(Settings.System.DEFAULT_MMS_NOTIFICATION_URI)) {
      return 8;
    }
    return -1;
  }
  
  public static Uri getDefaultUri(int paramInt)
  {
    if ((paramInt & 0x1) != 0) {
      return Settings.System.DEFAULT_RINGTONE_URI;
    }
    if ((paramInt & 0x2) != 0) {
      return Settings.System.DEFAULT_NOTIFICATION_URI;
    }
    if ((paramInt & 0x8) != 0) {
      return Settings.System.DEFAULT_MMS_NOTIFICATION_URI;
    }
    if ((paramInt & 0x4) != 0) {
      return Settings.System.DEFAULT_ALARM_ALERT_URI;
    }
    return null;
  }
  
  private Cursor getInternalRingtones()
  {
    return query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, INTERNAL_COLUMNS, constructBooleanTrueWhereClause(this.mFilterColumns), null, "title_key");
  }
  
  private Cursor getMediaRingtones()
  {
    Cursor localCursor = null;
    if (this.mContext.checkPermission("android.permission.READ_EXTERNAL_STORAGE", Process.myPid(), Process.myUid()) != 0)
    {
      Log.w("RingtoneManager", "No READ_EXTERNAL_STORAGE permission, ignoring ringtones on ext storage");
      return null;
    }
    String str = Environment.getExternalStorageState();
    if ((str.equals("mounted")) || (str.equals("mounted_ro"))) {
      localCursor = query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, MEDIA_COLUMNS, constructBooleanTrueWhereClause(this.mFilterColumns), null, "title_key");
    }
    return localCursor;
  }
  
  public static Ringtone getRingtone(Context paramContext, Uri paramUri)
  {
    return getRingtone(paramContext, paramUri, -1);
  }
  
  private static Ringtone getRingtone(Context paramContext, Uri paramUri, int paramInt)
  {
    try
    {
      paramContext = new Ringtone(paramContext, true);
      if (paramInt >= 0) {
        paramContext.setStreamType(paramInt);
      }
      paramContext.setUri(paramUri);
      return paramContext;
    }
    catch (Exception paramContext)
    {
      Log.e("RingtoneManager", "Failed to open ringtone " + paramUri + ": ", paramContext);
    }
    return null;
  }
  
  public static String getRingtoneAlias(Context paramContext, int paramInt, String paramString)
  {
    return RingtoneManagerUtils.getRingtoneAlias(paramContext, paramInt, paramString);
  }
  
  private static String getSettingForType(int paramInt)
  {
    if ((paramInt & 0x1) != 0) {
      return "ringtone";
    }
    if ((paramInt & 0x2) != 0) {
      return "notification_sound";
    }
    if ((paramInt & 0x4) != 0) {
      return "alarm_alert";
    }
    if ((paramInt & 0x8) != 0) {
      return "mms_notification";
    }
    return null;
  }
  
  public static Uri getStaticDefaultRingtoneUri(Context paramContext)
  {
    Object localObject = null;
    String str = Settings.System.getStringForUser(paramContext.getContentResolver(), "ringtone_default".toString(), paramContext.getUserId());
    paramContext = (Context)localObject;
    if (str != null) {
      paramContext = Uri.parse(str);
    }
    return paramContext;
  }
  
  private static Uri getUriFromCursor(Cursor paramCursor)
  {
    return ContentUris.withAppendedId(Uri.parse(paramCursor.getString(2)), paramCursor.getLong(0));
  }
  
  public static Uri getValidRingtoneUri(Context paramContext)
  {
    RingtoneManager localRingtoneManager = new RingtoneManager(paramContext);
    Uri localUri2 = getValidRingtoneUriFromCursorAndClose(paramContext, localRingtoneManager.getInternalRingtones());
    Uri localUri1 = localUri2;
    if (localUri2 == null) {
      localUri1 = getValidRingtoneUriFromCursorAndClose(paramContext, localRingtoneManager.getMediaRingtones());
    }
    return localUri1;
  }
  
  private static Uri getValidRingtoneUriFromCursorAndClose(Context paramContext, Cursor paramCursor)
  {
    if (paramCursor != null)
    {
      paramContext = null;
      if (paramCursor.moveToFirst()) {
        paramContext = getUriFromCursor(paramCursor);
      }
      paramCursor.close();
      return paramContext;
    }
    return null;
  }
  
  public static boolean isDefault(Uri paramUri)
  {
    return getDefaultType(paramUri) != -1;
  }
  
  private static InputStream openRingtone(Context paramContext, Uri paramUri)
    throws IOException
  {
    Object localObject = paramContext.getContentResolver();
    try
    {
      localObject = ((ContentResolver)localObject).openInputStream(paramUri);
      return (InputStream)localObject;
    }
    catch (SecurityException|IOException localSecurityException)
    {
      Log.w("RingtoneManager", "Failed to open directly; attempting failover: " + localSecurityException);
      paramContext = ((AudioManager)paramContext.getSystemService(AudioManager.class)).getRingtonePlayer();
      try
      {
        paramContext = new ParcelFileDescriptor.AutoCloseInputStream(paramContext.openRingtone(paramUri));
        return paramContext;
      }
      catch (Exception paramContext)
      {
        throw new IOException(paramContext);
      }
    }
  }
  
  private Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
  {
    if (this.mActivity != null) {
      return this.mActivity.managedQuery(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
    }
    return this.mContext.getContentResolver().query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
  }
  
  /* Error */
  public static void setActualDefaultRingtoneUri(Context paramContext, int paramInt, Uri paramUri)
  {
    // Byte code:
    //   0: aload_2
    //   1: astore 8
    //   3: aload_2
    //   4: ifnull +47 -> 51
    //   7: aload_2
    //   8: astore 8
    //   10: aload_2
    //   11: invokevirtual 277	android/net/Uri:toString	()Ljava/lang/String;
    //   14: getstatic 120	android/provider/MediaStore$Audio$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   17: invokevirtual 277	android/net/Uri:toString	()Ljava/lang/String;
    //   20: invokevirtual 281	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   23: ifeq +28 -> 51
    //   26: aload_0
    //   27: iload_1
    //   28: aload_2
    //   29: invokestatic 471	android/media/RingtoneManagerUtils:ringtoneCopyFrom3rdParty	(Landroid/content/Context;ILandroid/net/Uri;)Landroid/net/Uri;
    //   32: astore_2
    //   33: aload_2
    //   34: astore 8
    //   36: aload_2
    //   37: ifnonnull +14 -> 51
    //   40: new 473	java/lang/UnsupportedOperationException
    //   43: dup
    //   44: ldc_w 475
    //   47: invokespecial 478	java/lang/UnsupportedOperationException:<init>	(Ljava/lang/String;)V
    //   50: athrow
    //   51: aload_0
    //   52: invokevirtual 181	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   55: astore 13
    //   57: iload_1
    //   58: invokestatic 175	android/media/RingtoneManager:getSettingForType	(I)Ljava/lang/String;
    //   61: astore 5
    //   63: aload 5
    //   65: ifnonnull +4 -> 69
    //   68: return
    //   69: iconst_0
    //   70: istore 4
    //   72: iconst_m1
    //   73: istore_3
    //   74: aload_0
    //   75: invokevirtual 482	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   78: ifnull +30 -> 108
    //   81: aload_0
    //   82: invokevirtual 482	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   85: invokevirtual 487	android/content/pm/ApplicationInfo:isPrivilegedApp	()Z
    //   88: ifne +324 -> 412
    //   91: aload_0
    //   92: invokevirtual 482	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   95: invokevirtual 490	android/content/pm/ApplicationInfo:isSystemApp	()Z
    //   98: istore 4
    //   100: aload_0
    //   101: invokevirtual 482	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   104: getfield 493	android/content/pm/ApplicationInfo:uid	I
    //   107: istore_3
    //   108: getstatic 81	android/media/RingtoneManager:DBG	Z
    //   111: ifeq +73 -> 184
    //   114: ldc 47
    //   116: new 89	java/lang/StringBuilder
    //   119: dup
    //   120: invokespecial 92	java/lang/StringBuilder:<init>	()V
    //   123: ldc_w 495
    //   126: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   129: aload 5
    //   131: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   134: ldc -109
    //   136: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: iload_1
    //   140: invokevirtual 206	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   143: ldc_w 497
    //   146: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   149: aload 8
    //   151: invokevirtual 107	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   154: ldc_w 499
    //   157: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   160: aload_0
    //   161: invokevirtual 502	android/content/Context:getPackageName	()Ljava/lang/String;
    //   164: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   167: ldc_w 504
    //   170: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   173: iload_3
    //   174: invokevirtual 206	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   177: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   180: invokestatic 214	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   183: pop
    //   184: aload 8
    //   186: ifnull +232 -> 418
    //   189: aload 8
    //   191: invokevirtual 277	android/net/Uri:toString	()Ljava/lang/String;
    //   194: astore_2
    //   195: aload 13
    //   197: aload 5
    //   199: aload_2
    //   200: aload_0
    //   201: invokevirtual 184	android/content/Context:getUserId	()I
    //   204: invokestatic 508	android/provider/Settings$System:putStringForUser	(Landroid/content/ContentResolver;Ljava/lang/String;Ljava/lang/String;I)Z
    //   207: pop
    //   208: aload 5
    //   210: ldc_w 376
    //   213: if_acmpne +8 -> 221
    //   216: iload 4
    //   218: ifeq +205 -> 423
    //   221: aload 5
    //   223: ldc -55
    //   225: if_acmpne +34 -> 259
    //   228: aload_0
    //   229: invokevirtual 181	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   232: astore 5
    //   234: aload 8
    //   236: ifnull +280 -> 516
    //   239: aload 8
    //   241: invokevirtual 277	android/net/Uri:toString	()Ljava/lang/String;
    //   244: astore_2
    //   245: aload 5
    //   247: ldc_w 510
    //   250: aload_2
    //   251: aload_0
    //   252: invokevirtual 184	android/content/Context:getUserId	()I
    //   255: invokestatic 508	android/provider/Settings$System:putStringForUser	(Landroid/content/ContentResolver;Ljava/lang/String;Ljava/lang/String;I)Z
    //   258: pop
    //   259: aload 8
    //   261: ifnull +150 -> 411
    //   264: iload_1
    //   265: invokestatic 512	android/media/RingtoneManager:getCacheForType	(I)Landroid/net/Uri;
    //   268: astore 14
    //   270: aconst_null
    //   271: astore 10
    //   273: aconst_null
    //   274: astore 11
    //   276: aconst_null
    //   277: astore 7
    //   279: aconst_null
    //   280: astore 5
    //   282: aconst_null
    //   283: astore 12
    //   285: aconst_null
    //   286: astore 9
    //   288: aload 9
    //   290: astore 6
    //   292: aload 12
    //   294: astore_2
    //   295: aload_0
    //   296: aload 8
    //   298: invokestatic 514	android/media/RingtoneManager:openRingtone	(Landroid/content/Context;Landroid/net/Uri;)Ljava/io/InputStream;
    //   301: astore 8
    //   303: aload 8
    //   305: astore 5
    //   307: aload 9
    //   309: astore 6
    //   311: aload 8
    //   313: astore 7
    //   315: aload 12
    //   317: astore_2
    //   318: aload 13
    //   320: aload 14
    //   322: invokevirtual 518	android/content/ContentResolver:openOutputStream	(Landroid/net/Uri;)Ljava/io/OutputStream;
    //   325: astore 9
    //   327: aload 8
    //   329: astore 5
    //   331: aload 9
    //   333: astore 6
    //   335: aload 8
    //   337: astore 7
    //   339: aload 9
    //   341: astore_2
    //   342: aload 8
    //   344: aload 9
    //   346: invokestatic 524	libcore/io/Streams:copy	(Ljava/io/InputStream;Ljava/io/OutputStream;)I
    //   349: pop
    //   350: aload 11
    //   352: astore_0
    //   353: aload 9
    //   355: ifnull +11 -> 366
    //   358: aload 9
    //   360: invokevirtual 527	java/io/OutputStream:close	()V
    //   363: aload 11
    //   365: astore_0
    //   366: aload 8
    //   368: ifnull +8 -> 376
    //   371: aload 8
    //   373: invokevirtual 530	java/io/InputStream:close	()V
    //   376: aload_0
    //   377: astore_2
    //   378: aload_2
    //   379: ifnull +32 -> 411
    //   382: aload_2
    //   383: athrow
    //   384: astore_0
    //   385: ldc 47
    //   387: new 89	java/lang/StringBuilder
    //   390: dup
    //   391: invokespecial 92	java/lang/StringBuilder:<init>	()V
    //   394: ldc_w 532
    //   397: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   400: aload_0
    //   401: invokevirtual 107	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   404: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   407: invokestatic 242	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   410: pop
    //   411: return
    //   412: iconst_1
    //   413: istore 4
    //   415: goto -315 -> 100
    //   418: aconst_null
    //   419: astore_2
    //   420: goto -225 -> 195
    //   423: ldc 47
    //   425: new 89	java/lang/StringBuilder
    //   428: dup
    //   429: invokespecial 92	java/lang/StringBuilder:<init>	()V
    //   432: ldc_w 534
    //   435: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   438: aload 8
    //   440: invokevirtual 107	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   443: ldc_w 499
    //   446: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   449: aload_0
    //   450: invokevirtual 502	android/content/Context:getPackageName	()Ljava/lang/String;
    //   453: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   456: ldc -109
    //   458: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   461: iload_3
    //   462: invokevirtual 206	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   465: ldc -89
    //   467: invokevirtual 98	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   470: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   473: invokestatic 214	android/util/Log:d	(Ljava/lang/String;Ljava/lang/String;)I
    //   476: pop
    //   477: aload_0
    //   478: invokevirtual 181	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   481: astore 6
    //   483: aload 8
    //   485: ifnull +26 -> 511
    //   488: aload 8
    //   490: invokevirtual 277	android/net/Uri:toString	()Ljava/lang/String;
    //   493: astore_2
    //   494: aload 6
    //   496: ldc_w 380
    //   499: aload_2
    //   500: aload_0
    //   501: invokevirtual 184	android/content/Context:getUserId	()I
    //   504: invokestatic 508	android/provider/Settings$System:putStringForUser	(Landroid/content/ContentResolver;Ljava/lang/String;Ljava/lang/String;I)Z
    //   507: pop
    //   508: goto -287 -> 221
    //   511: aconst_null
    //   512: astore_2
    //   513: goto -19 -> 494
    //   516: aconst_null
    //   517: astore_2
    //   518: goto -273 -> 245
    //   521: astore_0
    //   522: goto -156 -> 366
    //   525: astore 5
    //   527: aload 5
    //   529: astore_2
    //   530: aload_0
    //   531: ifnull -153 -> 378
    //   534: aload_0
    //   535: aload 5
    //   537: if_acmpeq -161 -> 376
    //   540: aload_0
    //   541: aload 5
    //   543: invokevirtual 537	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   546: aload_0
    //   547: astore_2
    //   548: goto -170 -> 378
    //   551: astore_2
    //   552: aload_2
    //   553: athrow
    //   554: astore 7
    //   556: aload 6
    //   558: ifnull +8 -> 566
    //   561: aload 6
    //   563: invokevirtual 527	java/io/OutputStream:close	()V
    //   566: aload_2
    //   567: astore_0
    //   568: aload 5
    //   570: ifnull +8 -> 578
    //   573: aload 5
    //   575: invokevirtual 530	java/io/InputStream:close	()V
    //   578: aload_0
    //   579: astore_2
    //   580: aload_2
    //   581: ifnull +57 -> 638
    //   584: aload_2
    //   585: athrow
    //   586: astore 6
    //   588: aload 6
    //   590: astore_0
    //   591: aload_2
    //   592: ifnull -24 -> 568
    //   595: aload_2
    //   596: aload 6
    //   598: if_acmpeq -32 -> 566
    //   601: aload_2
    //   602: aload 6
    //   604: invokevirtual 537	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   607: aload_2
    //   608: astore_0
    //   609: goto -41 -> 568
    //   612: astore 5
    //   614: aload 5
    //   616: astore_2
    //   617: aload_0
    //   618: ifnull -38 -> 580
    //   621: aload_0
    //   622: aload 5
    //   624: if_acmpeq -46 -> 578
    //   627: aload_0
    //   628: aload 5
    //   630: invokevirtual 537	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   633: aload_0
    //   634: astore_2
    //   635: goto -55 -> 580
    //   638: aload 7
    //   640: athrow
    //   641: astore_0
    //   642: aload 7
    //   644: astore 5
    //   646: aload_2
    //   647: astore 6
    //   649: aload_0
    //   650: astore 7
    //   652: aload 10
    //   654: astore_2
    //   655: goto -99 -> 556
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	658	0	paramContext	Context
    //   0	658	1	paramInt	int
    //   0	658	2	paramUri	Uri
    //   73	389	3	i	int
    //   70	344	4	bool	boolean
    //   61	269	5	localObject1	Object
    //   525	49	5	localThrowable1	Throwable
    //   612	17	5	localThrowable2	Throwable
    //   644	1	5	localObject2	Object
    //   290	272	6	localObject3	Object
    //   586	17	6	localThrowable3	Throwable
    //   647	1	6	localUri1	Uri
    //   277	61	7	localObject4	Object
    //   554	89	7	localObject5	Object
    //   650	1	7	localContext	Context
    //   1	488	8	localObject6	Object
    //   286	73	9	localOutputStream	java.io.OutputStream
    //   271	382	10	localObject7	Object
    //   274	90	11	localObject8	Object
    //   283	33	12	localObject9	Object
    //   55	264	13	localContentResolver	ContentResolver
    //   268	53	14	localUri2	Uri
    // Exception table:
    //   from	to	target	type
    //   358	363	384	java/io/IOException
    //   371	376	384	java/io/IOException
    //   382	384	384	java/io/IOException
    //   540	546	384	java/io/IOException
    //   561	566	384	java/io/IOException
    //   573	578	384	java/io/IOException
    //   584	586	384	java/io/IOException
    //   601	607	384	java/io/IOException
    //   627	633	384	java/io/IOException
    //   638	641	384	java/io/IOException
    //   358	363	521	java/lang/Throwable
    //   371	376	525	java/lang/Throwable
    //   295	303	551	java/lang/Throwable
    //   318	327	551	java/lang/Throwable
    //   342	350	551	java/lang/Throwable
    //   552	554	554	finally
    //   561	566	586	java/lang/Throwable
    //   573	578	612	java/lang/Throwable
    //   295	303	641	finally
    //   318	327	641	finally
    //   342	350	641	finally
  }
  
  public static void setActualRingtoneUriBySubId(Context paramContext, int paramInt, Uri paramUri)
  {
    Object localObject = null;
    if ((paramInt < 0) || (paramInt >= 2)) {
      return;
    }
    if (paramInt == 0) {}
    for (String str = "ringtone";; str = "ringtone_" + (paramInt + 1))
    {
      ContentResolver localContentResolver = paramContext.getContentResolver();
      paramContext = (Context)localObject;
      if (paramUri != null) {
        paramContext = paramUri.toString();
      }
      Settings.System.putString(localContentResolver, str, paramContext);
      return;
    }
  }
  
  private void setFilterColumnsList(int paramInt)
  {
    List localList = this.mFilterColumns;
    localList.clear();
    if ((paramInt & 0x1) != 0) {
      localList.add("is_ringtone");
    }
    if ((paramInt & 0x2) != 0) {
      localList.add("is_notification");
    }
    if ((paramInt & 0x4) != 0) {
      localList.add("is_alarm");
    }
  }
  
  public static Uri validForSound(Context paramContext, Uri paramUri, String paramString)
  {
    return RingtoneManagerUtils.validForSound(paramContext, paramUri, paramString);
  }
  
  public Cursor getCursor()
  {
    if ((this.mCursor != null) && (this.mCursor.requery())) {
      return this.mCursor;
    }
    Cursor localCursor1 = getInternalRingtones();
    Cursor localCursor2 = getMediaRingtones();
    try
    {
      this.mCursor = new SortCursor(new Cursor[] { localCursor1, localCursor2 }, "_display_name");
      return this.mCursor;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        if (DBG) {
          Log.d("RingtoneManager", "Invalid column DISPLAY_NAME, use DEFAULT_SORT_ORDER instead: " + localException);
        }
        if (this.mCursor != null) {
          this.mCursor.close();
        }
        this.mCursor = new SortCursor(new Cursor[] { localCursor1, localCursor2 }, "title_key");
      }
    }
  }
  
  @Deprecated
  public boolean getIncludeDrm()
  {
    return false;
  }
  
  public Ringtone getRingtone(int paramInt)
  {
    if ((this.mStopPreviousRingtone) && (this.mPreviousRingtone != null)) {
      this.mPreviousRingtone.stop();
    }
    this.mPreviousRingtone = getRingtone(this.mContext, getRingtoneUri(paramInt), inferStreamType());
    return this.mPreviousRingtone;
  }
  
  public int getRingtonePosition(Uri paramUri)
  {
    if (paramUri == null) {
      return -1;
    }
    Cursor localCursor = getCursor();
    int j = localCursor.getCount();
    if (!localCursor.moveToFirst()) {
      return -1;
    }
    Uri localUri = null;
    Object localObject = null;
    int i = 0;
    while (i < j)
    {
      String str = localCursor.getString(2);
      if ((localUri != null) && (str.equals(localObject))) {}
      while (paramUri.equals(ContentUris.withAppendedId(localUri, localCursor.getLong(0))))
      {
        return i;
        localUri = Uri.parse(str);
      }
      localCursor.move(1);
      localObject = str;
      i += 1;
    }
    return -1;
  }
  
  public Uri getRingtoneUri(int paramInt)
  {
    if ((this.mCursor != null) && (this.mCursor.moveToPosition(paramInt))) {
      return getUriFromCursor(this.mCursor);
    }
    return null;
  }
  
  public boolean getStopPreviousRingtone()
  {
    return this.mStopPreviousRingtone;
  }
  
  public int inferStreamType()
  {
    switch (this.mType)
    {
    case 3: 
    default: 
      return 2;
    case 4: 
      return 4;
    }
    return 5;
  }
  
  @Deprecated
  public void setIncludeDrm(boolean paramBoolean)
  {
    if (paramBoolean) {
      Log.w("RingtoneManager", "setIncludeDrm no longer supported");
    }
  }
  
  public void setStopPreviousRingtone(boolean paramBoolean)
  {
    this.mStopPreviousRingtone = paramBoolean;
  }
  
  public void setType(int paramInt)
  {
    if (this.mCursor != null) {
      throw new IllegalStateException("Setting filter columns should be done before querying for ringtones.");
    }
    this.mType = paramInt;
    setFilterColumnsList(paramInt);
  }
  
  public void stopPreviousRingtone()
  {
    if (this.mPreviousRingtone != null) {
      this.mPreviousRingtone.stop();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/RingtoneManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */