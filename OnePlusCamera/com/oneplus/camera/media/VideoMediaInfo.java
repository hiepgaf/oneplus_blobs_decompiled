package com.oneplus.camera.media;

import android.content.Context;
import android.location.Location;
import java.util.Hashtable;
import java.util.Map;

public class VideoMediaInfo
  extends MediaInfo
{
  private static final String TAG = VideoMediaInfo.class.getSimpleName();
  private final int m_ActualHeight;
  private final int m_ActualWidth;
  private final long m_Duration;
  private final Location m_Location;
  private final long m_TakenTime;
  
  /* Error */
  public VideoMediaInfo(android.net.Uri paramUri, android.database.Cursor paramCursor)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: aload_2
    //   3: invokespecial 31	com/oneplus/camera/media/MediaInfo:<init>	(Landroid/net/Uri;Landroid/database/Cursor;)V
    //   6: aload_2
    //   7: ifnull +257 -> 264
    //   10: aload_0
    //   11: aload_2
    //   12: ldc 33
    //   14: lconst_0
    //   15: invokestatic 39	com/oneplus/database/CursorUtils:getLong	(Landroid/database/Cursor;Ljava/lang/String;J)J
    //   18: putfield 41	com/oneplus/camera/media/VideoMediaInfo:m_Duration	J
    //   21: aload_0
    //   22: aload_2
    //   23: ldc 43
    //   25: lconst_0
    //   26: invokestatic 39	com/oneplus/database/CursorUtils:getLong	(Landroid/database/Cursor;Ljava/lang/String;J)J
    //   29: putfield 45	com/oneplus/camera/media/VideoMediaInfo:m_TakenTime	J
    //   32: aload_0
    //   33: invokespecial 49	com/oneplus/camera/media/MediaInfo:getWidth	()I
    //   36: istore 10
    //   38: aload_0
    //   39: invokespecial 52	com/oneplus/camera/media/MediaInfo:getHeight	()I
    //   42: istore 7
    //   44: iload 7
    //   46: istore 8
    //   48: iload 10
    //   50: istore 9
    //   52: aload_0
    //   53: invokevirtual 56	com/oneplus/camera/media/VideoMediaInfo:hasFilePath	()Z
    //   56: ifeq +154 -> 210
    //   59: aconst_null
    //   60: astore 13
    //   62: aconst_null
    //   63: astore 14
    //   65: new 58	android/media/MediaMetadataRetriever
    //   68: dup
    //   69: invokespecial 60	android/media/MediaMetadataRetriever:<init>	()V
    //   72: astore_1
    //   73: iload 7
    //   75: istore 11
    //   77: iload 10
    //   79: istore 9
    //   81: aload_1
    //   82: aload_0
    //   83: invokevirtual 63	com/oneplus/camera/media/VideoMediaInfo:getFilePath	()Ljava/lang/String;
    //   86: invokevirtual 67	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   89: iload 7
    //   91: istore 11
    //   93: iload 10
    //   95: istore 9
    //   97: aload_1
    //   98: bipush 24
    //   100: invokevirtual 71	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   103: astore 13
    //   105: iload 7
    //   107: istore 11
    //   109: iload 10
    //   111: istore 9
    //   113: aload_1
    //   114: bipush 18
    //   116: invokevirtual 71	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   119: invokestatic 77	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   122: istore 8
    //   124: iload 7
    //   126: istore 11
    //   128: iload 8
    //   130: istore 9
    //   132: aload_1
    //   133: bipush 19
    //   135: invokevirtual 71	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   138: invokestatic 77	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   141: istore 7
    //   143: iload 7
    //   145: istore 11
    //   147: iload 8
    //   149: istore 10
    //   151: aload 13
    //   153: ifnull +33 -> 186
    //   156: iload 7
    //   158: istore 11
    //   160: iload 8
    //   162: istore 9
    //   164: aload 13
    //   166: ldc 79
    //   168: invokevirtual 85	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   171: istore 12
    //   173: iload 12
    //   175: ifeq +102 -> 277
    //   178: iload 7
    //   180: istore 10
    //   182: iload 8
    //   184: istore 11
    //   186: iload 11
    //   188: istore 8
    //   190: iload 10
    //   192: istore 9
    //   194: aload_1
    //   195: ifnull +15 -> 210
    //   198: aload_1
    //   199: invokevirtual 88	android/media/MediaMetadataRetriever:release	()V
    //   202: iload 10
    //   204: istore 9
    //   206: iload 11
    //   208: istore 8
    //   210: aload_2
    //   211: ldc 90
    //   213: ldc2_w 91
    //   216: invokestatic 96	com/oneplus/database/CursorUtils:getDouble	(Landroid/database/Cursor;Ljava/lang/String;D)D
    //   219: dstore_3
    //   220: aload_2
    //   221: ldc 98
    //   223: ldc2_w 91
    //   226: invokestatic 96	com/oneplus/database/CursorUtils:getDouble	(Landroid/database/Cursor;Ljava/lang/String;D)D
    //   229: dstore 5
    //   231: dload_3
    //   232: invokestatic 104	java/lang/Double:isNaN	(D)Z
    //   235: ifne +11 -> 246
    //   238: dload 5
    //   240: invokestatic 104	java/lang/Double:isNaN	(D)Z
    //   243: ifeq +112 -> 355
    //   246: aload_0
    //   247: aconst_null
    //   248: putfield 106	com/oneplus/camera/media/VideoMediaInfo:m_Location	Landroid/location/Location;
    //   251: aload_0
    //   252: iload 9
    //   254: putfield 108	com/oneplus/camera/media/VideoMediaInfo:m_ActualWidth	I
    //   257: aload_0
    //   258: iload 8
    //   260: putfield 110	com/oneplus/camera/media/VideoMediaInfo:m_ActualHeight	I
    //   263: return
    //   264: aload_0
    //   265: lconst_0
    //   266: putfield 41	com/oneplus/camera/media/VideoMediaInfo:m_Duration	J
    //   269: aload_0
    //   270: lconst_0
    //   271: putfield 45	com/oneplus/camera/media/VideoMediaInfo:m_TakenTime	J
    //   274: goto -242 -> 32
    //   277: iload 7
    //   279: istore 11
    //   281: iload 8
    //   283: istore 9
    //   285: aload 13
    //   287: ldc 112
    //   289: invokevirtual 85	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   292: istore 12
    //   294: iload 7
    //   296: istore 11
    //   298: iload 8
    //   300: istore 10
    //   302: iload 12
    //   304: ifeq -118 -> 186
    //   307: goto -129 -> 178
    //   310: astore_1
    //   311: aload 14
    //   313: astore_1
    //   314: iload 7
    //   316: istore 8
    //   318: iload 10
    //   320: istore 9
    //   322: aload_1
    //   323: ifnull -113 -> 210
    //   326: aload_1
    //   327: invokevirtual 88	android/media/MediaMetadataRetriever:release	()V
    //   330: iload 7
    //   332: istore 8
    //   334: iload 10
    //   336: istore 9
    //   338: goto -128 -> 210
    //   341: astore_2
    //   342: aload 13
    //   344: astore_1
    //   345: aload_1
    //   346: ifnull +7 -> 353
    //   349: aload_1
    //   350: invokevirtual 88	android/media/MediaMetadataRetriever:release	()V
    //   353: aload_2
    //   354: athrow
    //   355: dload_3
    //   356: dconst_0
    //   357: dcmpl
    //   358: ifeq -112 -> 246
    //   361: dload 5
    //   363: dconst_0
    //   364: dcmpl
    //   365: ifeq -119 -> 246
    //   368: aload_0
    //   369: new 114	android/location/Location
    //   372: dup
    //   373: getstatic 24	com/oneplus/camera/media/VideoMediaInfo:TAG	Ljava/lang/String;
    //   376: invokespecial 116	android/location/Location:<init>	(Ljava/lang/String;)V
    //   379: putfield 106	com/oneplus/camera/media/VideoMediaInfo:m_Location	Landroid/location/Location;
    //   382: aload_0
    //   383: getfield 106	com/oneplus/camera/media/VideoMediaInfo:m_Location	Landroid/location/Location;
    //   386: dload_3
    //   387: invokevirtual 120	android/location/Location:setLatitude	(D)V
    //   390: aload_0
    //   391: getfield 106	com/oneplus/camera/media/VideoMediaInfo:m_Location	Landroid/location/Location;
    //   394: dload 5
    //   396: invokevirtual 123	android/location/Location:setLongitude	(D)V
    //   399: goto -148 -> 251
    //   402: astore_2
    //   403: goto -58 -> 345
    //   406: astore 13
    //   408: iload 11
    //   410: istore 7
    //   412: iload 9
    //   414: istore 10
    //   416: goto -102 -> 314
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	419	0	this	VideoMediaInfo
    //   0	419	1	paramUri	android.net.Uri
    //   0	419	2	paramCursor	android.database.Cursor
    //   219	168	3	d1	double
    //   229	166	5	d2	double
    //   42	369	7	i	int
    //   46	287	8	j	int
    //   50	363	9	k	int
    //   36	379	10	m	int
    //   75	334	11	n	int
    //   171	132	12	bool	boolean
    //   60	283	13	str	String
    //   406	1	13	localThrowable	Throwable
    //   63	249	14	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   65	73	310	java/lang/Throwable
    //   65	73	341	finally
    //   81	89	402	finally
    //   97	105	402	finally
    //   113	124	402	finally
    //   132	143	402	finally
    //   164	173	402	finally
    //   285	294	402	finally
    //   81	89	406	java/lang/Throwable
    //   97	105	406	java/lang/Throwable
    //   113	124	406	java/lang/Throwable
    //   132	143	406	java/lang/Throwable
    //   164	173	406	java/lang/Throwable
    //   285	294	406	java/lang/Throwable
  }
  
  public Map<String, Object> getDetails(Context paramContext)
  {
    Map localMap = super.getDetails(paramContext);
    paramContext = localMap;
    if (localMap == null) {
      paramContext = new Hashtable();
    }
    if (this.m_Location != null) {
      paramContext.put("Location", this.m_Location);
    }
    return paramContext;
  }
  
  public long getDuration()
  {
    return this.m_Duration;
  }
  
  public int getHeight()
  {
    return this.m_ActualHeight;
  }
  
  public long getTakenTime()
  {
    return this.m_TakenTime;
  }
  
  public int getWidth()
  {
    return this.m_ActualWidth;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/VideoMediaInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */