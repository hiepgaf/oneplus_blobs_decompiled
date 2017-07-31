package android.media;

import android.graphics.Bitmap;
import android.os.IBinder;
import java.io.FileDescriptor;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class MediaMetadataRetriever
{
  private static final int EMBEDDED_PICTURE_TYPE_ANY = 65535;
  public static final int METADATA_KEY_ALBUM = 1;
  public static final int METADATA_KEY_ALBUMARTIST = 13;
  public static final int METADATA_KEY_ARTIST = 2;
  public static final int METADATA_KEY_AUTHOR = 3;
  public static final int METADATA_KEY_BITRATE = 20;
  public static final int METADATA_KEY_CAPTURE_FRAMERATE = 25;
  public static final int METADATA_KEY_CD_TRACK_NUMBER = 0;
  public static final int METADATA_KEY_COMPILATION = 15;
  public static final int METADATA_KEY_COMPOSER = 4;
  public static final int METADATA_KEY_DATE = 5;
  public static final int METADATA_KEY_DISC_NUMBER = 14;
  public static final int METADATA_KEY_DURATION = 9;
  public static final int METADATA_KEY_GENRE = 6;
  public static final int METADATA_KEY_HAS_AUDIO = 16;
  public static final int METADATA_KEY_HAS_VIDEO = 17;
  public static final int METADATA_KEY_IS_DRM = 22;
  public static final int METADATA_KEY_LOCATION = 23;
  public static final int METADATA_KEY_MIMETYPE = 12;
  public static final int METADATA_KEY_NUM_TRACKS = 10;
  public static final int METADATA_KEY_TIMED_TEXT_LANGUAGES = 21;
  public static final int METADATA_KEY_TITLE = 7;
  public static final int METADATA_KEY_VIDEO_HEIGHT = 19;
  public static final int METADATA_KEY_VIDEO_ROTATION = 24;
  public static final int METADATA_KEY_VIDEO_WIDTH = 18;
  public static final int METADATA_KEY_WRITER = 11;
  public static final int METADATA_KEY_YEAR = 8;
  public static final int OPTION_CLOSEST = 3;
  public static final int OPTION_CLOSEST_SYNC = 2;
  public static final int OPTION_NEXT_SYNC = 1;
  public static final int OPTION_PREVIOUS_SYNC = 0;
  private long mNativeContext;
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  public MediaMetadataRetriever()
  {
    native_setup();
  }
  
  private native Bitmap _getFrameAtTime(long paramLong, int paramInt);
  
  private native void _setDataSource(MediaDataSource paramMediaDataSource)
    throws IllegalArgumentException;
  
  private native void _setDataSource(IBinder paramIBinder, String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2)
    throws IllegalArgumentException;
  
  private native byte[] getEmbeddedPicture(int paramInt);
  
  private final native void native_finalize();
  
  private static native void native_init();
  
  private native void native_setup();
  
  public native String extractMetadata(int paramInt);
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      native_finalize();
      return;
    }
    finally
    {
      super.finalize();
    }
  }
  
  public byte[] getEmbeddedPicture()
  {
    return getEmbeddedPicture(65535);
  }
  
  public Bitmap getFrameAtTime()
  {
    return getFrameAtTime(-1L, 2);
  }
  
  public Bitmap getFrameAtTime(long paramLong)
  {
    return getFrameAtTime(paramLong, 2);
  }
  
  public Bitmap getFrameAtTime(long paramLong, int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 3)) {
      throw new IllegalArgumentException("Unsupported option: " + paramInt);
    }
    return _getFrameAtTime(paramLong, paramInt);
  }
  
  public native void release();
  
  /* Error */
  public void setDataSource(android.content.Context paramContext, android.net.Uri paramUri)
    throws IllegalArgumentException, java.lang.SecurityException
  {
    // Byte code:
    //   0: aload_2
    //   1: ifnonnull +11 -> 12
    //   4: new 91	java/lang/IllegalArgumentException
    //   7: dup
    //   8: invokespecial 145	java/lang/IllegalArgumentException:<init>	()V
    //   11: athrow
    //   12: aload_2
    //   13: invokevirtual 150	android/net/Uri:getScheme	()Ljava/lang/String;
    //   16: astore_3
    //   17: aload_3
    //   18: ifnull +12 -> 30
    //   21: aload_3
    //   22: ldc -104
    //   24: invokevirtual 158	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   27: ifeq +12 -> 39
    //   30: aload_0
    //   31: aload_2
    //   32: invokevirtual 161	android/net/Uri:getPath	()Ljava/lang/String;
    //   35: invokevirtual 163	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   38: return
    //   39: aconst_null
    //   40: astore 6
    //   42: aconst_null
    //   43: astore 5
    //   45: aload 5
    //   47: astore_3
    //   48: aload 6
    //   50: astore 4
    //   52: aload_1
    //   53: invokevirtual 169	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   56: astore_1
    //   57: aload 5
    //   59: astore_3
    //   60: aload 6
    //   62: astore 4
    //   64: aload_1
    //   65: aload_2
    //   66: ldc -85
    //   68: invokevirtual 177	android/content/ContentResolver:openAssetFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;)Landroid/content/res/AssetFileDescriptor;
    //   71: astore_1
    //   72: aload_1
    //   73: ifnonnull +63 -> 136
    //   76: aload_1
    //   77: astore_3
    //   78: aload_1
    //   79: astore 4
    //   81: new 91	java/lang/IllegalArgumentException
    //   84: dup
    //   85: invokespecial 145	java/lang/IllegalArgumentException:<init>	()V
    //   88: athrow
    //   89: astore_1
    //   90: aload_3
    //   91: ifnull +7 -> 98
    //   94: aload_3
    //   95: invokevirtual 182	android/content/res/AssetFileDescriptor:close	()V
    //   98: aload_0
    //   99: aload_2
    //   100: invokevirtual 183	android/net/Uri:toString	()Ljava/lang/String;
    //   103: invokevirtual 163	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   106: return
    //   107: astore_1
    //   108: aload 5
    //   110: astore_3
    //   111: aload 6
    //   113: astore 4
    //   115: new 91	java/lang/IllegalArgumentException
    //   118: dup
    //   119: invokespecial 145	java/lang/IllegalArgumentException:<init>	()V
    //   122: athrow
    //   123: astore_1
    //   124: aload 4
    //   126: ifnull +8 -> 134
    //   129: aload 4
    //   131: invokevirtual 182	android/content/res/AssetFileDescriptor:close	()V
    //   134: aload_1
    //   135: athrow
    //   136: aload_1
    //   137: astore_3
    //   138: aload_1
    //   139: astore 4
    //   141: aload_1
    //   142: invokevirtual 187	android/content/res/AssetFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   145: astore 5
    //   147: aload_1
    //   148: astore_3
    //   149: aload_1
    //   150: astore 4
    //   152: aload 5
    //   154: invokevirtual 193	java/io/FileDescriptor:valid	()Z
    //   157: ifne +16 -> 173
    //   160: aload_1
    //   161: astore_3
    //   162: aload_1
    //   163: astore 4
    //   165: new 91	java/lang/IllegalArgumentException
    //   168: dup
    //   169: invokespecial 145	java/lang/IllegalArgumentException:<init>	()V
    //   172: athrow
    //   173: aload_1
    //   174: astore_3
    //   175: aload_1
    //   176: astore 4
    //   178: aload_1
    //   179: invokevirtual 197	android/content/res/AssetFileDescriptor:getDeclaredLength	()J
    //   182: lconst_0
    //   183: lcmp
    //   184: ifge +23 -> 207
    //   187: aload_1
    //   188: astore_3
    //   189: aload_1
    //   190: astore 4
    //   192: aload_0
    //   193: aload 5
    //   195: invokevirtual 200	android/media/MediaMetadataRetriever:setDataSource	(Ljava/io/FileDescriptor;)V
    //   198: aload_1
    //   199: ifnull +7 -> 206
    //   202: aload_1
    //   203: invokevirtual 182	android/content/res/AssetFileDescriptor:close	()V
    //   206: return
    //   207: aload_1
    //   208: astore_3
    //   209: aload_1
    //   210: astore 4
    //   212: aload_0
    //   213: aload 5
    //   215: aload_1
    //   216: invokevirtual 203	android/content/res/AssetFileDescriptor:getStartOffset	()J
    //   219: aload_1
    //   220: invokevirtual 197	android/content/res/AssetFileDescriptor:getDeclaredLength	()J
    //   223: invokevirtual 206	android/media/MediaMetadataRetriever:setDataSource	(Ljava/io/FileDescriptor;JJ)V
    //   226: goto -28 -> 198
    //   229: astore_1
    //   230: return
    //   231: astore_1
    //   232: goto -134 -> 98
    //   235: astore_2
    //   236: goto -102 -> 134
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	239	0	this	MediaMetadataRetriever
    //   0	239	1	paramContext	android.content.Context
    //   0	239	2	paramUri	android.net.Uri
    //   16	193	3	localObject1	Object
    //   50	161	4	localObject2	Object
    //   43	171	5	localFileDescriptor	FileDescriptor
    //   40	72	6	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   52	57	89	java/lang/SecurityException
    //   64	72	89	java/lang/SecurityException
    //   81	89	89	java/lang/SecurityException
    //   115	123	89	java/lang/SecurityException
    //   141	147	89	java/lang/SecurityException
    //   152	160	89	java/lang/SecurityException
    //   165	173	89	java/lang/SecurityException
    //   178	187	89	java/lang/SecurityException
    //   192	198	89	java/lang/SecurityException
    //   212	226	89	java/lang/SecurityException
    //   64	72	107	java/io/FileNotFoundException
    //   52	57	123	finally
    //   64	72	123	finally
    //   81	89	123	finally
    //   115	123	123	finally
    //   141	147	123	finally
    //   152	160	123	finally
    //   165	173	123	finally
    //   178	187	123	finally
    //   192	198	123	finally
    //   212	226	123	finally
    //   202	206	229	java/io/IOException
    //   94	98	231	java/io/IOException
    //   129	134	235	java/io/IOException
  }
  
  public void setDataSource(MediaDataSource paramMediaDataSource)
    throws IllegalArgumentException
  {
    _setDataSource(paramMediaDataSource);
  }
  
  public void setDataSource(FileDescriptor paramFileDescriptor)
    throws IllegalArgumentException
  {
    setDataSource(paramFileDescriptor, 0L, 576460752303423487L);
  }
  
  public native void setDataSource(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2)
    throws IllegalArgumentException;
  
  /* Error */
  public void setDataSource(String paramString)
    throws IllegalArgumentException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_1
    //   4: ifnonnull +11 -> 15
    //   7: new 91	java/lang/IllegalArgumentException
    //   10: dup
    //   11: invokespecial 145	java/lang/IllegalArgumentException:<init>	()V
    //   14: athrow
    //   15: aconst_null
    //   16: astore_3
    //   17: aconst_null
    //   18: astore_2
    //   19: new 212	java/io/FileInputStream
    //   22: dup
    //   23: aload_1
    //   24: invokespecial 213	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   27: astore_1
    //   28: aload_0
    //   29: aload_1
    //   30: invokevirtual 216	java/io/FileInputStream:getFD	()Ljava/io/FileDescriptor;
    //   33: lconst_0
    //   34: ldc2_w 209
    //   37: invokevirtual 206	android/media/MediaMetadataRetriever:setDataSource	(Ljava/io/FileDescriptor;JJ)V
    //   40: aload 4
    //   42: astore_2
    //   43: aload_1
    //   44: ifnull +10 -> 54
    //   47: aload_1
    //   48: invokevirtual 217	java/io/FileInputStream:close	()V
    //   51: aload 4
    //   53: astore_2
    //   54: aload_2
    //   55: ifnull +86 -> 141
    //   58: aload_2
    //   59: athrow
    //   60: astore_1
    //   61: new 91	java/lang/IllegalArgumentException
    //   64: dup
    //   65: invokespecial 145	java/lang/IllegalArgumentException:<init>	()V
    //   68: athrow
    //   69: astore_2
    //   70: goto -16 -> 54
    //   73: astore_3
    //   74: aload_2
    //   75: astore_1
    //   76: aload_3
    //   77: astore_2
    //   78: aload_2
    //   79: athrow
    //   80: astore 4
    //   82: aload_1
    //   83: astore_3
    //   84: aload_2
    //   85: astore_1
    //   86: aload 4
    //   88: astore_2
    //   89: aload_1
    //   90: astore 4
    //   92: aload_3
    //   93: ifnull +10 -> 103
    //   96: aload_3
    //   97: invokevirtual 217	java/io/FileInputStream:close	()V
    //   100: aload_1
    //   101: astore 4
    //   103: aload 4
    //   105: ifnull +34 -> 139
    //   108: aload 4
    //   110: athrow
    //   111: aload_1
    //   112: astore 4
    //   114: aload_1
    //   115: aload_3
    //   116: if_acmpeq -13 -> 103
    //   119: aload_1
    //   120: aload_3
    //   121: invokevirtual 221	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   124: aload_1
    //   125: astore 4
    //   127: goto -24 -> 103
    //   130: astore_1
    //   131: new 91	java/lang/IllegalArgumentException
    //   134: dup
    //   135: invokespecial 145	java/lang/IllegalArgumentException:<init>	()V
    //   138: athrow
    //   139: aload_2
    //   140: athrow
    //   141: return
    //   142: astore_1
    //   143: goto -12 -> 131
    //   146: astore_2
    //   147: aconst_null
    //   148: astore_1
    //   149: goto -60 -> 89
    //   152: astore_2
    //   153: aconst_null
    //   154: astore 4
    //   156: aload_1
    //   157: astore_3
    //   158: aload 4
    //   160: astore_1
    //   161: goto -72 -> 89
    //   164: astore_2
    //   165: goto -87 -> 78
    //   168: astore_1
    //   169: goto -108 -> 61
    //   172: astore_3
    //   173: aload_1
    //   174: ifnonnull -63 -> 111
    //   177: aload_3
    //   178: astore 4
    //   180: goto -77 -> 103
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	183	0	this	MediaMetadataRetriever
    //   0	183	1	paramString	String
    //   18	41	2	localObject1	Object
    //   69	6	2	localThrowable1	Throwable
    //   77	63	2	localObject2	Object
    //   146	1	2	localObject3	Object
    //   152	1	2	localObject4	Object
    //   164	1	2	localThrowable2	Throwable
    //   16	1	3	localObject5	Object
    //   73	4	3	localThrowable3	Throwable
    //   83	75	3	str	String
    //   172	6	3	localThrowable4	Throwable
    //   1	51	4	localObject6	Object
    //   80	7	4	localObject7	Object
    //   90	89	4	localObject8	Object
    // Exception table:
    //   from	to	target	type
    //   47	51	60	java/io/FileNotFoundException
    //   58	60	60	java/io/FileNotFoundException
    //   47	51	69	java/lang/Throwable
    //   19	28	73	java/lang/Throwable
    //   78	80	80	finally
    //   96	100	130	java/io/IOException
    //   108	111	130	java/io/IOException
    //   119	124	130	java/io/IOException
    //   139	141	130	java/io/IOException
    //   47	51	142	java/io/IOException
    //   58	60	142	java/io/IOException
    //   19	28	146	finally
    //   28	40	152	finally
    //   28	40	164	java/lang/Throwable
    //   96	100	168	java/io/FileNotFoundException
    //   108	111	168	java/io/FileNotFoundException
    //   119	124	168	java/io/FileNotFoundException
    //   139	141	168	java/io/FileNotFoundException
    //   96	100	172	java/lang/Throwable
  }
  
  public void setDataSource(String paramString, Map<String, String> paramMap)
    throws IllegalArgumentException
  {
    int i = 0;
    String[] arrayOfString1 = new String[paramMap.size()];
    String[] arrayOfString2 = new String[paramMap.size()];
    paramMap = paramMap.entrySet().iterator();
    while (paramMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramMap.next();
      arrayOfString1[i] = ((String)localEntry.getKey());
      arrayOfString2[i] = ((String)localEntry.getValue());
      i += 1;
    }
    _setDataSource(MediaHTTPService.createHttpServiceBinderIfNecessary(paramString), paramString, arrayOfString1, arrayOfString2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaMetadataRetriever.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */