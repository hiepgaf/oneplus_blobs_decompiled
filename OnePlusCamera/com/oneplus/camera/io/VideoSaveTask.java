package com.oneplus.camera.io;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build.VERSION;
import android.util.Size;
import com.oneplus.camera.Camera.LensFacing;
import com.oneplus.camera.CameraActivity;
import com.oneplus.camera.CaptureHandle;

public class VideoSaveTask
  extends MediaSaveTask
{
  private static final String INTENT_NEW_VIDEO = "com.oneplus.camera.intent.action.NEW_VIDEO";
  private final Context m_Context;
  private String m_FilePath;
  private final Size m_Size;
  
  public VideoSaveTask(Context paramContext, CaptureHandle paramCaptureHandle, String paramString, Size paramSize)
  {
    super(paramContext, paramCaptureHandle);
    this.m_Context = paramContext;
    this.m_FilePath = paramString;
    this.m_Size = paramSize;
  }
  
  public long getMediaSize()
  {
    return 0L;
  }
  
  public String getPictureId()
  {
    return null;
  }
  
  public Bitmap getThumbnail()
  {
    if (this.m_Thumbnail != null) {
      return this.m_Thumbnail;
    }
    this.m_Thumbnail = ThumbnailUtils.createVideoThumbnail(this.m_FilePath, 1);
    return this.m_Thumbnail;
  }
  
  public boolean insertToMediaStore()
  {
    if (!super.insertToMediaStore()) {
      return false;
    }
    Uri localUri = getContentUri();
    if (localUri != null) {
      if (Build.VERSION.SDK_INT >= 24) {
        break label81;
      }
    }
    label81:
    for (Object localObject = "android.hardware.action.NEW_VIDEO";; localObject = "com.oneplus.camera.intent.action.NEW_VIDEO")
    {
      localObject = new Intent((String)localObject);
      ((Intent)localObject).setData(localUri);
      if ((this.m_Context instanceof CameraActivity)) {
        ((Intent)localObject).putExtra("CameraActivity.InstanceId", ((CameraActivity)this.m_Context).getInstanceId());
      }
      this.m_Context.sendBroadcast((Intent)localObject);
      return true;
    }
  }
  
  protected String onGenerateFilePath(boolean paramBoolean)
  {
    return this.m_FilePath;
  }
  
  /* Error */
  protected Uri onInsertToMediaStore(String paramString, ContentValues paramContentValues)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 92	com/oneplus/camera/io/VideoSaveTask:TAG	Ljava/lang/String;
    //   4: ldc 94
    //   6: aload_1
    //   7: new 96	java/lang/StringBuilder
    //   10: dup
    //   11: invokespecial 99	java/lang/StringBuilder:<init>	()V
    //   14: ldc 101
    //   16: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   19: aload_2
    //   20: invokevirtual 108	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   23: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   26: invokestatic 117	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   29: aload_0
    //   30: getfield 20	com/oneplus/camera/io/VideoSaveTask:m_Context	Landroid/content/Context;
    //   33: invokevirtual 121	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   36: getstatic 127	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   39: invokevirtual 133	android/content/ContentResolver:acquireUnstableContentProviderClient	(Landroid/net/Uri;)Landroid/content/ContentProviderClient;
    //   42: astore 6
    //   44: aload 6
    //   46: ifnonnull +14 -> 60
    //   49: aload_0
    //   50: getfield 92	com/oneplus/camera/io/VideoSaveTask:TAG	Ljava/lang/String;
    //   53: ldc -121
    //   55: invokestatic 139	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   58: aconst_null
    //   59: areturn
    //   60: aload 6
    //   62: getstatic 127	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   65: aload_2
    //   66: invokevirtual 145	android/content/ContentProviderClient:insert	(Landroid/net/Uri;Landroid/content/ContentValues;)Landroid/net/Uri;
    //   69: astore 4
    //   71: aload 4
    //   73: astore 5
    //   75: aload 4
    //   77: ifnonnull +153 -> 230
    //   80: aload_0
    //   81: getfield 92	com/oneplus/camera/io/VideoSaveTask:TAG	Ljava/lang/String;
    //   84: ldc -109
    //   86: invokestatic 150	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   89: iconst_1
    //   90: anewarray 152	java/lang/String
    //   93: astore 4
    //   95: aload 4
    //   97: iconst_0
    //   98: aload_1
    //   99: aastore
    //   100: aload 6
    //   102: getstatic 127	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   105: aload_2
    //   106: ldc -102
    //   108: aload 4
    //   110: invokevirtual 158	android/content/ContentProviderClient:update	(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
    //   113: istore_3
    //   114: iload_3
    //   115: iconst_1
    //   116: if_icmpne +223 -> 339
    //   119: aconst_null
    //   120: astore_2
    //   121: aconst_null
    //   122: astore_1
    //   123: aload 6
    //   125: getstatic 127	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   128: iconst_1
    //   129: anewarray 152	java/lang/String
    //   132: dup
    //   133: iconst_0
    //   134: ldc -96
    //   136: aastore
    //   137: ldc -102
    //   139: aload 4
    //   141: aconst_null
    //   142: invokevirtual 164	android/content/ContentProviderClient:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   145: astore 4
    //   147: aload 4
    //   149: astore_1
    //   150: aload 4
    //   152: astore_2
    //   153: aload 4
    //   155: invokeinterface 169 1 0
    //   160: ifeq +79 -> 239
    //   163: aload 4
    //   165: astore_1
    //   166: aload 4
    //   168: astore_2
    //   169: new 96	java/lang/StringBuilder
    //   172: dup
    //   173: invokespecial 99	java/lang/StringBuilder:<init>	()V
    //   176: getstatic 127	android/provider/MediaStore$Video$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
    //   179: invokevirtual 172	android/net/Uri:toString	()Ljava/lang/String;
    //   182: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   185: ldc -82
    //   187: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   190: aload 4
    //   192: iconst_0
    //   193: invokeinterface 178 2 0
    //   198: invokevirtual 181	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   201: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   204: invokestatic 185	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   207: astore 5
    //   209: aload 5
    //   211: astore_1
    //   212: aload_1
    //   213: astore 5
    //   215: aload 4
    //   217: ifnull +13 -> 230
    //   220: aload 4
    //   222: invokeinterface 188 1 0
    //   227: aload_1
    //   228: astore 5
    //   230: aload 6
    //   232: invokevirtual 191	android/content/ContentProviderClient:release	()Z
    //   235: pop
    //   236: aload 5
    //   238: areturn
    //   239: aload 4
    //   241: astore_1
    //   242: aload 4
    //   244: astore_2
    //   245: aload_0
    //   246: getfield 92	com/oneplus/camera/io/VideoSaveTask:TAG	Ljava/lang/String;
    //   249: ldc -63
    //   251: invokestatic 139	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   254: aload 4
    //   256: ifnull +10 -> 266
    //   259: aload 4
    //   261: invokeinterface 188 1 0
    //   266: aload 6
    //   268: invokevirtual 191	android/content/ContentProviderClient:release	()Z
    //   271: pop
    //   272: aconst_null
    //   273: areturn
    //   274: astore 4
    //   276: aload_1
    //   277: astore_2
    //   278: aload_0
    //   279: getfield 92	com/oneplus/camera/io/VideoSaveTask:TAG	Ljava/lang/String;
    //   282: ldc -63
    //   284: aload 4
    //   286: invokestatic 196	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   289: aload_1
    //   290: ifnull +9 -> 299
    //   293: aload_1
    //   294: invokeinterface 188 1 0
    //   299: aload 6
    //   301: invokevirtual 191	android/content/ContentProviderClient:release	()Z
    //   304: pop
    //   305: aconst_null
    //   306: areturn
    //   307: astore_1
    //   308: aload_2
    //   309: ifnull +9 -> 318
    //   312: aload_2
    //   313: invokeinterface 188 1 0
    //   318: aload_1
    //   319: athrow
    //   320: astore_1
    //   321: aload_0
    //   322: getfield 92	com/oneplus/camera/io/VideoSaveTask:TAG	Ljava/lang/String;
    //   325: ldc -58
    //   327: aload_1
    //   328: invokestatic 196	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   331: aload 6
    //   333: invokevirtual 191	android/content/ContentProviderClient:release	()Z
    //   336: pop
    //   337: aconst_null
    //   338: areturn
    //   339: aload_0
    //   340: getfield 92	com/oneplus/camera/io/VideoSaveTask:TAG	Ljava/lang/String;
    //   343: ldc -58
    //   345: invokestatic 139	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   348: aload 6
    //   350: invokevirtual 191	android/content/ContentProviderClient:release	()Z
    //   353: pop
    //   354: aconst_null
    //   355: areturn
    //   356: astore_1
    //   357: aload 6
    //   359: invokevirtual 191	android/content/ContentProviderClient:release	()Z
    //   362: pop
    //   363: aload_1
    //   364: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	365	0	this	VideoSaveTask
    //   0	365	1	paramString	String
    //   0	365	2	paramContentValues	ContentValues
    //   113	4	3	i	int
    //   69	191	4	localObject1	Object
    //   274	11	4	localThrowable	Throwable
    //   73	164	5	localObject2	Object
    //   42	316	6	localContentProviderClient	android.content.ContentProviderClient
    // Exception table:
    //   from	to	target	type
    //   123	147	274	java/lang/Throwable
    //   153	163	274	java/lang/Throwable
    //   169	209	274	java/lang/Throwable
    //   245	254	274	java/lang/Throwable
    //   123	147	307	finally
    //   153	163	307	finally
    //   169	209	307	finally
    //   245	254	307	finally
    //   278	289	307	finally
    //   60	71	320	java/lang/Throwable
    //   80	95	320	java/lang/Throwable
    //   100	114	320	java/lang/Throwable
    //   220	227	320	java/lang/Throwable
    //   259	266	320	java/lang/Throwable
    //   293	299	320	java/lang/Throwable
    //   312	318	320	java/lang/Throwable
    //   318	320	320	java/lang/Throwable
    //   339	348	320	java/lang/Throwable
    //   60	71	356	finally
    //   80	95	356	finally
    //   100	114	356	finally
    //   220	227	356	finally
    //   259	266	356	finally
    //   293	299	356	finally
    //   312	318	356	finally
    //   318	320	356	finally
    //   321	331	356	finally
    //   339	348	356	finally
  }
  
  protected boolean onPrepareGalleryDatabaseValues(String paramString, Uri paramUri, ContentValues paramContentValues)
  {
    if (getLensFacing() == Camera.LensFacing.FRONT)
    {
      paramContentValues.put("oneplus_flags", Integer.valueOf(1));
      return true;
    }
    return false;
  }
  
  /* Error */
  protected boolean onPrepareMediaStoreValues(String paramString, ContentValues paramContentValues)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 20	com/oneplus/camera/io/VideoSaveTask:m_Context	Landroid/content/Context;
    //   4: aload_1
    //   5: invokestatic 185	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
    //   8: invokestatic 232	android/media/MediaPlayer:create	(Landroid/content/Context;Landroid/net/Uri;)Landroid/media/MediaPlayer;
    //   11: astore 5
    //   13: aload_2
    //   14: ldc -22
    //   16: aload_1
    //   17: invokestatic 240	com/oneplus/io/Path:getFileNameWithoutExtension	(Ljava/lang/String;)Ljava/lang/String;
    //   20: invokevirtual 242	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   23: aload_2
    //   24: ldc -12
    //   26: aload_1
    //   27: invokestatic 247	com/oneplus/io/Path:getFileName	(Ljava/lang/String;)Ljava/lang/String;
    //   30: invokevirtual 242	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   33: aload_2
    //   34: ldc -7
    //   36: ldc -5
    //   38: invokevirtual 242	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   41: invokestatic 256	java/lang/System:currentTimeMillis	()J
    //   44: lstore_3
    //   45: new 258	java/io/File
    //   48: dup
    //   49: aload_1
    //   50: invokespecial 259	java/io/File:<init>	(Ljava/lang/String;)V
    //   53: lload_3
    //   54: invokevirtual 263	java/io/File:setLastModified	(J)Z
    //   57: pop
    //   58: aload_2
    //   59: ldc_w 265
    //   62: lload_3
    //   63: ldc2_w 266
    //   66: ldiv
    //   67: invokestatic 272	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   70: invokevirtual 275	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   73: aload_2
    //   74: ldc_w 277
    //   77: lload_3
    //   78: invokestatic 272	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   81: invokevirtual 275	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Long;)V
    //   84: aload_0
    //   85: invokevirtual 281	com/oneplus/camera/io/VideoSaveTask:getLocation	()Landroid/location/Location;
    //   88: astore 6
    //   90: aload 6
    //   92: ifnull +33 -> 125
    //   95: aload_2
    //   96: ldc_w 283
    //   99: aload 6
    //   101: invokevirtual 289	android/location/Location:getLatitude	()D
    //   104: invokestatic 294	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   107: invokevirtual 297	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Double;)V
    //   110: aload_2
    //   111: ldc_w 299
    //   114: aload 6
    //   116: invokevirtual 302	android/location/Location:getLongitude	()D
    //   119: invokestatic 294	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   122: invokevirtual 297	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Double;)V
    //   125: aload_2
    //   126: ldc_w 304
    //   129: aload_0
    //   130: getfield 24	com/oneplus/camera/io/VideoSaveTask:m_Size	Landroid/util/Size;
    //   133: invokevirtual 310	android/util/Size:getWidth	()I
    //   136: invokestatic 218	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   139: invokevirtual 224	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   142: aload_2
    //   143: ldc_w 312
    //   146: aload_0
    //   147: getfield 24	com/oneplus/camera/io/VideoSaveTask:m_Size	Landroid/util/Size;
    //   150: invokevirtual 315	android/util/Size:getHeight	()I
    //   153: invokestatic 218	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   156: invokevirtual 224	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   159: aload_2
    //   160: ldc_w 317
    //   163: new 96	java/lang/StringBuilder
    //   166: dup
    //   167: invokespecial 99	java/lang/StringBuilder:<init>	()V
    //   170: aload_0
    //   171: getfield 24	com/oneplus/camera/io/VideoSaveTask:m_Size	Landroid/util/Size;
    //   174: invokevirtual 310	android/util/Size:getWidth	()I
    //   177: invokevirtual 320	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   180: ldc_w 322
    //   183: invokevirtual 105	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   186: aload_0
    //   187: getfield 24	com/oneplus/camera/io/VideoSaveTask:m_Size	Landroid/util/Size;
    //   190: invokevirtual 315	android/util/Size:getHeight	()I
    //   193: invokevirtual 320	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   196: invokevirtual 111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   199: invokevirtual 242	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   202: aload_2
    //   203: ldc_w 324
    //   206: aload 5
    //   208: invokevirtual 327	android/media/MediaPlayer:getDuration	()I
    //   211: invokestatic 218	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   214: invokevirtual 224	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/Integer;)V
    //   217: aload_2
    //   218: ldc_w 329
    //   221: aload_1
    //   222: invokevirtual 242	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   225: aload 5
    //   227: invokevirtual 331	android/media/MediaPlayer:release	()V
    //   230: iconst_1
    //   231: ireturn
    //   232: astore_1
    //   233: aload 5
    //   235: invokevirtual 331	android/media/MediaPlayer:release	()V
    //   238: aload_1
    //   239: athrow
    //   240: astore 6
    //   242: goto -158 -> 84
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	245	0	this	VideoSaveTask
    //   0	245	1	paramString	String
    //   0	245	2	paramContentValues	ContentValues
    //   44	34	3	l	long
    //   11	223	5	localMediaPlayer	android.media.MediaPlayer
    //   88	27	6	localLocation	android.location.Location
    //   240	1	6	localThrowable	Throwable
    // Exception table:
    //   from	to	target	type
    //   13	41	232	finally
    //   41	84	232	finally
    //   84	90	232	finally
    //   95	125	232	finally
    //   125	225	232	finally
    //   41	84	240	java/lang/Throwable
  }
  
  protected boolean onSaveToFile(String paramString)
  {
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/io/VideoSaveTask.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */