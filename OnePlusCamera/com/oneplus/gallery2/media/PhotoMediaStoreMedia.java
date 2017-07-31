package com.oneplus.gallery2.media;

import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore.Images.Media;
import android.util.Size;
import com.oneplus.base.CallbackHandle;
import com.oneplus.base.EmptyHandle;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class PhotoMediaStoreMedia
  extends MediaStoreMedia
  implements PhotoMedia
{
  private static final Uri CONTENT_URI_IMAGE = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
  private static final String CONTENT_URI_STRING_IMAGE = CONTENT_URI_IMAGE.toString();
  private static final ExecutorService FILE_INFO_EXECUTOR = Executors.newFixedThreadPool(2);
  private static final int INTERNAL_FLAG_PANORAMA = 1;
  private static final String TAG = PhotoMediaStoreMedia.class.getSimpleName();
  private Set<Handle> m_CheckAnimatableHandles = new HashSet();
  private int m_InternalFlags;
  private volatile Boolean m_IsAnimatable;
  private Size m_Size;
  private final List<CallbackHandle<Media.SizeCallback>> m_SizeCallbackHandles = new ArrayList();
  private SizeObtainingTask m_SizeObtainingTask;
  
  public PhotoMediaStoreMedia(MediaStoreMediaSource paramMediaStoreMediaSource, MediaStoreMedia.DbValues paramDbValues, MediaStoreMedia.FileInfo paramFileInfo)
  {
    super(paramMediaStoreMediaSource, paramDbValues, paramFileInfo);
  }
  
  /* Error */
  private void checkAnimatable(final CheckAnimatableHandle paramCheckAnimatableHandle)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore_2
    //   2: aconst_null
    //   3: astore 7
    //   5: aload_0
    //   6: iconst_0
    //   7: invokestatic 135	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   10: putfield 115	com/oneplus/gallery2/media/PhotoMediaStoreMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   13: aload_0
    //   14: aconst_null
    //   15: iconst_0
    //   16: invokevirtual 139	com/oneplus/gallery2/media/PhotoMediaStoreMedia:openInputStream	(Lcom/oneplus/base/Ref;I)Ljava/io/InputStream;
    //   19: astore 8
    //   21: aload_0
    //   22: aload 8
    //   24: invokestatic 145	com/oneplus/media/ImageUtils:isGifHeader	(Ljava/io/InputStream;)Z
    //   27: invokestatic 135	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   30: putfield 115	com/oneplus/gallery2/media/PhotoMediaStoreMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   33: aload_0
    //   34: getfield 115	com/oneplus/gallery2/media/PhotoMediaStoreMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   37: invokevirtual 149	java/lang/Boolean:booleanValue	()Z
    //   40: istore_3
    //   41: iload_3
    //   42: ifne +23 -> 65
    //   45: aload 8
    //   47: ifnonnull +220 -> 267
    //   50: aload_0
    //   51: new 10	com/oneplus/gallery2/media/PhotoMediaStoreMedia$2
    //   54: dup
    //   55: aload_0
    //   56: aload_1
    //   57: invokespecial 151	com/oneplus/gallery2/media/PhotoMediaStoreMedia$2:<init>	(Lcom/oneplus/gallery2/media/PhotoMediaStoreMedia;Lcom/oneplus/gallery2/media/PhotoMediaStoreMedia$CheckAnimatableHandle;)V
    //   60: invokestatic 157	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
    //   63: pop
    //   64: return
    //   65: new 159	com/oneplus/util/GifDecoder
    //   68: dup
    //   69: invokespecial 160	com/oneplus/util/GifDecoder:<init>	()V
    //   72: astore 5
    //   74: aload 5
    //   76: astore 4
    //   78: aload 5
    //   80: aload 8
    //   82: invokevirtual 164	com/oneplus/util/GifDecoder:read	(Ljava/io/InputStream;)V
    //   85: aload 5
    //   87: astore 4
    //   89: aload 5
    //   91: invokevirtual 168	com/oneplus/util/GifDecoder:frameCount	()I
    //   94: iconst_1
    //   95: if_icmpgt +100 -> 195
    //   98: aload 5
    //   100: astore 4
    //   102: aload_0
    //   103: iload_2
    //   104: invokestatic 135	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   107: putfield 115	com/oneplus/gallery2/media/PhotoMediaStoreMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   110: aload 5
    //   112: astore 4
    //   114: aload_0
    //   115: getfield 115	com/oneplus/gallery2/media/PhotoMediaStoreMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   118: invokevirtual 149	java/lang/Boolean:booleanValue	()Z
    //   121: istore_2
    //   122: iload_2
    //   123: ifeq +77 -> 200
    //   126: aload 5
    //   128: ifnull -83 -> 45
    //   131: aload 5
    //   133: invokevirtual 171	com/oneplus/util/GifDecoder:release	()V
    //   136: goto -91 -> 45
    //   139: astore 5
    //   141: aload 8
    //   143: ifnonnull +132 -> 275
    //   146: aload 5
    //   148: athrow
    //   149: astore 4
    //   151: aload 5
    //   153: ifnull +130 -> 283
    //   156: aload 5
    //   158: aload 4
    //   160: if_acmpne +130 -> 290
    //   163: aload 5
    //   165: athrow
    //   166: astore 4
    //   168: getstatic 59	com/oneplus/gallery2/media/PhotoMediaStoreMedia:TAG	Ljava/lang/String;
    //   171: new 173	java/lang/StringBuilder
    //   174: dup
    //   175: ldc -81
    //   177: invokespecial 178	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   180: aload_0
    //   181: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   184: invokevirtual 183	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   187: aload 4
    //   189: invokestatic 189	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   192: goto -142 -> 50
    //   195: iconst_1
    //   196: istore_2
    //   197: goto -99 -> 98
    //   200: aload 5
    //   202: astore 4
    //   204: getstatic 59	com/oneplus/gallery2/media/PhotoMediaStoreMedia:TAG	Ljava/lang/String;
    //   207: ldc -65
    //   209: invokestatic 195	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   212: goto -86 -> 126
    //   215: astore 6
    //   217: aload 5
    //   219: astore 4
    //   221: getstatic 59	com/oneplus/gallery2/media/PhotoMediaStoreMedia:TAG	Ljava/lang/String;
    //   224: ldc -59
    //   226: aload 6
    //   228: invokestatic 189	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   231: aload 5
    //   233: astore 4
    //   235: aload_0
    //   236: iconst_0
    //   237: invokestatic 135	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
    //   240: putfield 115	com/oneplus/gallery2/media/PhotoMediaStoreMedia:m_IsAnimatable	Ljava/lang/Boolean;
    //   243: aload 5
    //   245: ifnull -200 -> 45
    //   248: aload 5
    //   250: invokevirtual 171	com/oneplus/util/GifDecoder:release	()V
    //   253: goto -208 -> 45
    //   256: aload 5
    //   258: athrow
    //   259: aload 4
    //   261: invokevirtual 171	com/oneplus/util/GifDecoder:release	()V
    //   264: goto -8 -> 256
    //   267: aload 8
    //   269: invokevirtual 202	java/io/InputStream:close	()V
    //   272: goto -222 -> 50
    //   275: aload 8
    //   277: invokevirtual 202	java/io/InputStream:close	()V
    //   280: goto -134 -> 146
    //   283: aload 4
    //   285: astore 5
    //   287: goto -124 -> 163
    //   290: aload 5
    //   292: aload 4
    //   294: invokevirtual 206	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   297: goto -134 -> 163
    //   300: astore 5
    //   302: goto +25 -> 327
    //   305: astore 6
    //   307: aconst_null
    //   308: astore 5
    //   310: goto -93 -> 217
    //   313: astore 4
    //   315: aload 7
    //   317: astore 5
    //   319: goto -168 -> 151
    //   322: astore 5
    //   324: aconst_null
    //   325: astore 4
    //   327: aload 4
    //   329: ifnonnull -70 -> 259
    //   332: goto -76 -> 256
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	335	0	this	PhotoMediaStoreMedia
    //   0	335	1	paramCheckAnimatableHandle	CheckAnimatableHandle
    //   1	196	2	bool1	boolean
    //   40	2	3	bool2	boolean
    //   76	37	4	localGifDecoder1	com.oneplus.util.GifDecoder
    //   149	10	4	localObject1	Object
    //   166	22	4	localThrowable1	Throwable
    //   202	91	4	localObject2	Object
    //   313	1	4	localObject3	Object
    //   325	3	4	localObject4	Object
    //   72	60	5	localGifDecoder2	com.oneplus.util.GifDecoder
    //   139	118	5	localObject5	Object
    //   285	6	5	localObject6	Object
    //   300	1	5	localObject7	Object
    //   308	10	5	localObject8	Object
    //   322	1	5	localObject9	Object
    //   215	12	6	localThrowable2	Throwable
    //   305	1	6	localThrowable3	Throwable
    //   3	313	7	localObject10	Object
    //   19	257	8	localInputStream	java.io.InputStream
    // Exception table:
    //   from	to	target	type
    //   21	41	139	finally
    //   131	136	139	finally
    //   248	253	139	finally
    //   256	259	139	finally
    //   259	264	139	finally
    //   146	149	149	finally
    //   275	280	149	finally
    //   163	166	166	java/lang/Throwable
    //   290	297	166	java/lang/Throwable
    //   78	85	215	java/lang/Throwable
    //   89	98	215	java/lang/Throwable
    //   102	110	215	java/lang/Throwable
    //   114	122	215	java/lang/Throwable
    //   204	212	215	java/lang/Throwable
    //   78	85	300	finally
    //   89	98	300	finally
    //   102	110	300	finally
    //   114	122	300	finally
    //   204	212	300	finally
    //   221	231	300	finally
    //   235	243	300	finally
    //   65	74	305	java/lang/Throwable
    //   13	21	313	finally
    //   267	272	313	finally
    //   65	74	322	finally
  }
  
  public static Uri createContentUri(long paramLong)
  {
    return Uri.parse(CONTENT_URI_STRING_IMAGE + "/" + paramLong);
  }
  
  /* Error */
  public static long getTakenTimeFromFile(Uri paramUri)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload 4
    //   5: astore_3
    //   6: invokestatic 233	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
    //   9: invokevirtual 237	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
    //   12: aload_0
    //   13: invokevirtual 242	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   16: astore 5
    //   18: aload 5
    //   20: invokestatic 246	com/oneplus/media/ImageUtils:decodeTakenTime	(Ljava/io/InputStream;)J
    //   23: lstore_1
    //   24: aload 5
    //   26: ifnonnull +5 -> 31
    //   29: lload_1
    //   30: lreturn
    //   31: aload 4
    //   33: astore_3
    //   34: aload 5
    //   36: invokevirtual 202	java/io/InputStream:close	()V
    //   39: lload_1
    //   40: lreturn
    //   41: astore 4
    //   43: aload_3
    //   44: ifnull +61 -> 105
    //   47: aload_3
    //   48: aload 4
    //   50: if_acmpne +61 -> 111
    //   53: aload_3
    //   54: athrow
    //   55: astore_3
    //   56: getstatic 59	com/oneplus/gallery2/media/PhotoMediaStoreMedia:TAG	Ljava/lang/String;
    //   59: new 173	java/lang/StringBuilder
    //   62: dup
    //   63: ldc -8
    //   65: invokespecial 178	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   68: aload_0
    //   69: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   72: invokevirtual 183	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   75: aload_3
    //   76: invokestatic 189	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   79: lconst_0
    //   80: lreturn
    //   81: astore 4
    //   83: aload 5
    //   85: ifnonnull +9 -> 94
    //   88: aload 4
    //   90: astore_3
    //   91: aload 4
    //   93: athrow
    //   94: aload 4
    //   96: astore_3
    //   97: aload 5
    //   99: invokevirtual 202	java/io/InputStream:close	()V
    //   102: goto -14 -> 88
    //   105: aload 4
    //   107: astore_3
    //   108: goto -55 -> 53
    //   111: aload_3
    //   112: aload 4
    //   114: invokevirtual 206	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   117: goto -64 -> 53
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	120	0	paramUri	Uri
    //   23	17	1	l	long
    //   5	49	3	localObject1	Object
    //   55	21	3	localThrowable1	Throwable
    //   90	22	3	localThrowable2	Throwable
    //   1	31	4	localObject2	Object
    //   41	8	4	localObject3	Object
    //   81	32	4	localThrowable3	Throwable
    //   16	82	5	localInputStream	java.io.InputStream
    // Exception table:
    //   from	to	target	type
    //   6	18	41	finally
    //   34	39	41	finally
    //   91	94	41	finally
    //   97	102	41	finally
    //   53	55	55	java/lang/Throwable
    //   111	117	55	java/lang/Throwable
    //   18	24	81	finally
  }
  
  /* Error */
  private void obtainDetails(final CallbackHandle<Media.DetailsCallback> paramCallbackHandle)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore 5
    //   6: aload_1
    //   7: invokestatic 254	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   10: ifeq +109 -> 119
    //   13: aload 4
    //   15: astore_3
    //   16: aload_0
    //   17: invokevirtual 257	com/oneplus/gallery2/media/PhotoMediaStoreMedia:getMimeType	()Ljava/lang/String;
    //   20: astore 6
    //   22: aload 4
    //   24: astore_3
    //   25: aload_0
    //   26: invokevirtual 260	com/oneplus/gallery2/media/PhotoMediaStoreMedia:getFilePath	()Ljava/lang/String;
    //   29: astore 7
    //   31: aload 6
    //   33: ifnonnull +87 -> 120
    //   36: aload 4
    //   38: astore_3
    //   39: aload 7
    //   41: invokestatic 266	com/oneplus/io/Path:getExtension	(Ljava/lang/String;)Ljava/lang/String;
    //   44: invokevirtual 269	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   47: astore 6
    //   49: aload 4
    //   51: astore_3
    //   52: aload 6
    //   54: invokevirtual 272	java/lang/String:hashCode	()I
    //   57: lookupswitch	default:+327->384, 1475827:+164->221, 45750678:+181->238
    //   84: aload 4
    //   86: astore_3
    //   87: getstatic 59	com/oneplus/gallery2/media/PhotoMediaStoreMedia:TAG	Ljava/lang/String;
    //   90: ldc_w 274
    //   93: invokestatic 277	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   96: aload_1
    //   97: invokestatic 254	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   100: ifeq +157 -> 257
    //   103: aload_0
    //   104: new 18	com/oneplus/gallery2/media/PhotoMediaStoreMedia$6
    //   107: dup
    //   108: aload_0
    //   109: aload_1
    //   110: aconst_null
    //   111: invokespecial 280	com/oneplus/gallery2/media/PhotoMediaStoreMedia$6:<init>	(Lcom/oneplus/gallery2/media/PhotoMediaStoreMedia;Lcom/oneplus/base/CallbackHandle;Lcom/oneplus/gallery2/media/PhotoMediaDetails;)V
    //   114: invokestatic 157	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
    //   117: pop
    //   118: return
    //   119: return
    //   120: aload 4
    //   122: astore_3
    //   123: aload 6
    //   125: ldc_w 282
    //   128: invokevirtual 286	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   131: istore_2
    //   132: iload_2
    //   133: ifeq +52 -> 185
    //   136: invokestatic 233	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
    //   139: invokevirtual 237	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
    //   142: aload_0
    //   143: invokevirtual 290	com/oneplus/gallery2/media/PhotoMediaStoreMedia:getContentUri	()Landroid/net/Uri;
    //   146: invokevirtual 242	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   149: astore 4
    //   151: aload 4
    //   153: invokestatic 296	com/oneplus/gallery2/media/MediaUtils:getPhotoMediaDetails	(Ljava/io/InputStream;)Lcom/oneplus/gallery2/media/PhotoMediaDetails;
    //   156: astore_3
    //   157: aload 4
    //   159: ifnonnull +99 -> 258
    //   162: aload_1
    //   163: invokestatic 254	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   166: ifeq +205 -> 371
    //   169: aload_0
    //   170: new 18	com/oneplus/gallery2/media/PhotoMediaStoreMedia$6
    //   173: dup
    //   174: aload_0
    //   175: aload_1
    //   176: aload_3
    //   177: invokespecial 280	com/oneplus/gallery2/media/PhotoMediaStoreMedia$6:<init>	(Lcom/oneplus/gallery2/media/PhotoMediaStoreMedia;Lcom/oneplus/base/CallbackHandle;Lcom/oneplus/gallery2/media/PhotoMediaDetails;)V
    //   180: invokestatic 157	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
    //   183: pop
    //   184: return
    //   185: aload 4
    //   187: astore_3
    //   188: getstatic 59	com/oneplus/gallery2/media/PhotoMediaStoreMedia:TAG	Ljava/lang/String;
    //   191: ldc_w 274
    //   194: invokestatic 277	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   197: aload_1
    //   198: invokestatic 254	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   201: ifeq +19 -> 220
    //   204: aload_0
    //   205: new 18	com/oneplus/gallery2/media/PhotoMediaStoreMedia$6
    //   208: dup
    //   209: aload_0
    //   210: aload_1
    //   211: aconst_null
    //   212: invokespecial 280	com/oneplus/gallery2/media/PhotoMediaStoreMedia$6:<init>	(Lcom/oneplus/gallery2/media/PhotoMediaStoreMedia;Lcom/oneplus/base/CallbackHandle;Lcom/oneplus/gallery2/media/PhotoMediaDetails;)V
    //   215: invokestatic 157	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
    //   218: pop
    //   219: return
    //   220: return
    //   221: aload 4
    //   223: astore_3
    //   224: aload 6
    //   226: ldc_w 298
    //   229: invokevirtual 286	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   232: ifeq -148 -> 84
    //   235: goto -99 -> 136
    //   238: aload 4
    //   240: astore_3
    //   241: aload 6
    //   243: ldc_w 300
    //   246: invokevirtual 286	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   249: istore_2
    //   250: iload_2
    //   251: ifeq -167 -> 84
    //   254: goto -118 -> 136
    //   257: return
    //   258: aload 4
    //   260: invokevirtual 202	java/io/InputStream:close	()V
    //   263: goto -101 -> 162
    //   266: astore 4
    //   268: aload 5
    //   270: ifnull +56 -> 326
    //   273: aload 5
    //   275: aload 4
    //   277: if_acmpne +56 -> 333
    //   280: aload 5
    //   282: athrow
    //   283: astore 5
    //   285: aload_3
    //   286: astore 4
    //   288: aload 4
    //   290: astore_3
    //   291: getstatic 59	com/oneplus/gallery2/media/PhotoMediaStoreMedia:TAG	Ljava/lang/String;
    //   294: ldc_w 302
    //   297: aload 5
    //   299: invokestatic 189	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   302: aload 4
    //   304: astore_3
    //   305: goto -143 -> 162
    //   308: astore 5
    //   310: aload 4
    //   312: ifnonnull +6 -> 318
    //   315: aload 5
    //   317: athrow
    //   318: aload 4
    //   320: invokevirtual 202	java/io/InputStream:close	()V
    //   323: goto -8 -> 315
    //   326: aload 4
    //   328: astore 5
    //   330: goto -50 -> 280
    //   333: aload 5
    //   335: aload 4
    //   337: invokevirtual 206	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   340: goto -60 -> 280
    //   343: astore 4
    //   345: aload_1
    //   346: invokestatic 254	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   349: ifeq +21 -> 370
    //   352: aload_0
    //   353: new 18	com/oneplus/gallery2/media/PhotoMediaStoreMedia$6
    //   356: dup
    //   357: aload_0
    //   358: aload_1
    //   359: aload_3
    //   360: invokespecial 280	com/oneplus/gallery2/media/PhotoMediaStoreMedia$6:<init>	(Lcom/oneplus/gallery2/media/PhotoMediaStoreMedia;Lcom/oneplus/base/CallbackHandle;Lcom/oneplus/gallery2/media/PhotoMediaDetails;)V
    //   363: invokestatic 157	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
    //   366: pop
    //   367: aload 4
    //   369: athrow
    //   370: return
    //   371: return
    //   372: astore 4
    //   374: goto -29 -> 345
    //   377: astore 4
    //   379: aconst_null
    //   380: astore_3
    //   381: goto -113 -> 268
    //   384: goto -300 -> 84
    //   387: astore 4
    //   389: aconst_null
    //   390: astore_3
    //   391: goto -123 -> 268
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	394	0	this	PhotoMediaStoreMedia
    //   0	394	1	paramCallbackHandle	CallbackHandle<Media.DetailsCallback>
    //   131	120	2	bool	boolean
    //   15	376	3	localObject1	Object
    //   1	258	4	localInputStream	java.io.InputStream
    //   266	10	4	localObject2	Object
    //   286	50	4	localObject3	Object
    //   343	25	4	localObject4	Object
    //   372	1	4	localObject5	Object
    //   377	1	4	localObject6	Object
    //   387	1	4	localObject7	Object
    //   4	277	5	localObject8	Object
    //   283	15	5	localThrowable	Throwable
    //   308	8	5	localObject9	Object
    //   328	6	5	localObject10	Object
    //   20	222	6	str1	String
    //   29	11	7	str2	String
    // Exception table:
    //   from	to	target	type
    //   258	263	266	finally
    //   280	283	283	java/lang/Throwable
    //   333	340	283	java/lang/Throwable
    //   151	157	308	finally
    //   280	283	343	finally
    //   333	340	343	finally
    //   16	22	372	finally
    //   25	31	372	finally
    //   39	49	372	finally
    //   52	84	372	finally
    //   87	96	372	finally
    //   123	132	372	finally
    //   188	197	372	finally
    //   224	235	372	finally
    //   241	250	372	finally
    //   291	302	372	finally
    //   136	151	377	finally
    //   315	318	387	finally
    //   318	323	387	finally
  }
  
  /* Error */
  private void obtainSize(final SizeObtainingTask paramSizeObtainingTask)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aload_1
    //   3: getfield 308	com/oneplus/gallery2/media/PhotoMediaStoreMedia$SizeObtainingTask:isCancelled	Z
    //   6: ifne +29 -> 35
    //   9: aload_0
    //   10: invokevirtual 290	com/oneplus/gallery2/media/PhotoMediaStoreMedia:getContentUri	()Landroid/net/Uri;
    //   13: astore 4
    //   15: aload 4
    //   17: ifnonnull +19 -> 36
    //   20: aload_1
    //   21: getfield 311	com/oneplus/gallery2/media/PhotoMediaStoreMedia$SizeObtainingTask:size	Landroid/util/Size;
    //   24: ifnull +111 -> 135
    //   27: aload_1
    //   28: getfield 308	com/oneplus/gallery2/media/PhotoMediaStoreMedia$SizeObtainingTask:isCancelled	Z
    //   31: ifeq +124 -> 155
    //   34: return
    //   35: return
    //   36: invokestatic 233	com/oneplus/base/BaseApplication:current	()Lcom/oneplus/base/BaseApplication;
    //   39: invokevirtual 237	com/oneplus/base/BaseApplication:getContentResolver	()Landroid/content/ContentResolver;
    //   42: aload 4
    //   44: invokevirtual 242	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   47: astore_2
    //   48: aload_1
    //   49: aload_2
    //   50: invokestatic 315	com/oneplus/media/ImageUtils:decodeSize	(Ljava/io/InputStream;)Landroid/util/Size;
    //   53: putfield 311	com/oneplus/gallery2/media/PhotoMediaStoreMedia$SizeObtainingTask:size	Landroid/util/Size;
    //   56: aload_2
    //   57: ifnull -37 -> 20
    //   60: aload_2
    //   61: invokevirtual 202	java/io/InputStream:close	()V
    //   64: goto -44 -> 20
    //   67: astore_2
    //   68: aload_3
    //   69: ifnull +53 -> 122
    //   72: aload_3
    //   73: aload_2
    //   74: if_acmpne +53 -> 127
    //   77: aload_3
    //   78: athrow
    //   79: astore_2
    //   80: getstatic 59	com/oneplus/gallery2/media/PhotoMediaStoreMedia:TAG	Ljava/lang/String;
    //   83: new 173	java/lang/StringBuilder
    //   86: dup
    //   87: ldc_w 317
    //   90: invokespecial 178	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   93: aload 4
    //   95: invokevirtual 182	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   98: invokevirtual 183	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   101: aload_2
    //   102: invokestatic 189	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   105: goto -85 -> 20
    //   108: astore_3
    //   109: aload_2
    //   110: ifnonnull +5 -> 115
    //   113: aload_3
    //   114: athrow
    //   115: aload_2
    //   116: invokevirtual 202	java/io/InputStream:close	()V
    //   119: goto -6 -> 113
    //   122: aload_2
    //   123: astore_3
    //   124: goto -47 -> 77
    //   127: aload_3
    //   128: aload_2
    //   129: invokevirtual 206	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   132: goto -55 -> 77
    //   135: aload_0
    //   136: invokevirtual 260	com/oneplus/gallery2/media/PhotoMediaStoreMedia:getFilePath	()Ljava/lang/String;
    //   139: astore_2
    //   140: aload_2
    //   141: ifnull -114 -> 27
    //   144: aload_1
    //   145: aload_2
    //   146: invokestatic 320	com/oneplus/media/ImageUtils:decodeSize	(Ljava/lang/String;)Landroid/util/Size;
    //   149: putfield 311	com/oneplus/gallery2/media/PhotoMediaStoreMedia$SizeObtainingTask:size	Landroid/util/Size;
    //   152: goto -125 -> 27
    //   155: aload_0
    //   156: new 20	com/oneplus/gallery2/media/PhotoMediaStoreMedia$7
    //   159: dup
    //   160: aload_0
    //   161: aload_1
    //   162: invokespecial 322	com/oneplus/gallery2/media/PhotoMediaStoreMedia$7:<init>	(Lcom/oneplus/gallery2/media/PhotoMediaStoreMedia;Lcom/oneplus/gallery2/media/PhotoMediaStoreMedia$SizeObtainingTask;)V
    //   165: invokestatic 157	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
    //   168: pop
    //   169: return
    //   170: astore_2
    //   171: goto -103 -> 68
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	174	0	this	PhotoMediaStoreMedia
    //   0	174	1	paramSizeObtainingTask	SizeObtainingTask
    //   47	14	2	localInputStream	java.io.InputStream
    //   67	7	2	localObject1	Object
    //   79	50	2	localThrowable	Throwable
    //   139	7	2	str	String
    //   170	1	2	localObject2	Object
    //   1	77	3	localObject3	Object
    //   108	6	3	localObject4	Object
    //   123	5	3	localObject5	Object
    //   13	81	4	localUri	Uri
    // Exception table:
    //   from	to	target	type
    //   36	48	67	finally
    //   60	64	67	finally
    //   77	79	79	java/lang/Throwable
    //   127	132	79	java/lang/Throwable
    //   48	56	108	finally
    //   113	115	170	finally
    //   115	119	170	finally
  }
  
  private void onSizeObtained(SizeObtainingTask paramSizeObtainingTask)
  {
    int j = 0;
    Size localSize;
    if (this.m_SizeObtainingTask == paramSizeObtainingTask)
    {
      this.m_SizeObtainingTask = null;
      localSize = this.m_Size;
      this.m_Size = paramSizeObtainingTask.size;
      if (this.m_Size == null) {
        break label55;
      }
      if (localSize != null) {
        break label69;
      }
    }
    for (;;)
    {
      if (!this.m_SizeCallbackHandles.isEmpty()) {
        break label98;
      }
      return;
      return;
      label55:
      if (localSize == null) {
        break;
      }
      this.m_Size = localSize;
      continue;
      label69:
      if (!localSize.equals(this.m_Size)) {
        ((MediaStoreMediaSource)getSource()).notifyMediaUpdatedByItself(this, FLAG_SIZE_CHANGED);
      }
    }
    label98:
    paramSizeObtainingTask = (CallbackHandle[])this.m_SizeCallbackHandles.toArray(new CallbackHandle[this.m_SizeCallbackHandles.size()]);
    this.m_SizeCallbackHandles.clear();
    int i;
    if (this.m_Size == null)
    {
      i = 0;
      label141:
      if (this.m_Size != null) {
        break label197;
      }
    }
    for (;;)
    {
      int k = paramSizeObtainingTask.length - 1;
      while (k >= 0)
      {
        ((Media.SizeCallback)paramSizeObtainingTask[k].getCallback()).onSizeObtained(this, i, j);
        k -= 1;
      }
      break;
      i = this.m_Size.getWidth();
      break label141;
      label197:
      j = this.m_Size.getHeight();
    }
  }
  
  static boolean parsePhotoContentUri(Uri paramUri, Ref<Long> paramRef)
  {
    return parseContentUri(CONTENT_URI_IMAGE, paramUri, paramRef);
  }
  
  public Handle checkAnimatable(final PhotoMedia.CheckAnimatableCallback paramCheckAnimatableCallback)
  {
    if (this.m_IsAnimatable == null)
    {
      paramCheckAnimatableCallback = new CheckAnimatableHandle(paramCheckAnimatableCallback);
      this.m_CheckAnimatableHandles.add(paramCheckAnimatableCallback);
      FILE_INFO_EXECUTOR.execute(new Runnable()
      {
        public void run()
        {
          PhotoMediaStoreMedia.this.checkAnimatable(paramCheckAnimatableCallback);
        }
      });
      return paramCheckAnimatableCallback;
    }
    if (paramCheckAnimatableCallback == null) {}
    for (;;)
    {
      return new EmptyHandle("CheckAnimatable");
      paramCheckAnimatableCallback.onChecked(this, this.m_IsAnimatable.booleanValue());
    }
  }
  
  protected Uri createContentUri(MediaStoreMedia.DbValues paramDbValues)
  {
    return createContentUri(paramDbValues.id);
  }
  
  public Handle getDetails(final Media.DetailsCallback paramDetailsCallback)
  {
    verifyAccess();
    if (paramDetailsCallback != null)
    {
      paramDetailsCallback = new CallbackHandle("GetPhotoDetails", paramDetailsCallback, null)
      {
        protected void onClose(int paramAnonymousInt) {}
      };
      FILE_INFO_EXECUTOR.execute(new Runnable()
      {
        public void run()
        {
          PhotoMediaStoreMedia.this.obtainDetails(paramDetailsCallback);
        }
      });
      return paramDetailsCallback;
    }
    return null;
  }
  
  public PhotoMedia getEncodedMedia()
  {
    return null;
  }
  
  public PhotoMedia getRawMedia()
  {
    return null;
  }
  
  public Handle getSize(Media.SizeCallback paramSizeCallback)
  {
    verifyAccess();
    if (paramSizeCallback != null)
    {
      if (this.m_Size == null)
      {
        paramSizeCallback = new CallbackHandle("GetPhotoSize", paramSizeCallback, null)
        {
          protected void onClose(int paramAnonymousInt)
          {
            PhotoMediaStoreMedia.this.verifyAccess();
            PhotoMediaStoreMedia.this.m_SizeCallbackHandles.remove(this);
          }
        };
        this.m_SizeCallbackHandles.add(paramSizeCallback);
        if (this.m_SizeObtainingTask == null) {
          break label83;
        }
        return paramSizeCallback;
      }
    }
    else {
      return null;
    }
    paramSizeCallback.onSizeObtained(this, this.m_Size.getWidth(), this.m_Size.getHeight());
    return new EmptyHandle("GetPhotoSize");
    label83:
    this.m_SizeObtainingTask = new SizeObtainingTask(null);
    FILE_INFO_EXECUTOR.submit(this.m_SizeObtainingTask);
    return paramSizeCallback;
  }
  
  public boolean isBokeh()
  {
    return false;
  }
  
  public boolean isBurstGroup()
  {
    return false;
  }
  
  public boolean isPanorama()
  {
    return (this.m_InternalFlags & 0x1) != 0;
  }
  
  public boolean isRaw()
  {
    return false;
  }
  
  protected int onUpdate(MediaStoreMedia.DbValues paramDbValues, MediaStoreMedia.FileInfo paramFileInfo, boolean paramBoolean)
  {
    int k = super.onUpdate(paramDbValues, paramFileInfo, paramBoolean);
    paramFileInfo = paramDbValues.extraInfo;
    label29:
    label36:
    int i;
    if (paramFileInfo == null)
    {
      this.m_InternalFlags &= 0xFFFFFFFE;
      if (paramDbValues.width > 0) {
        break label76;
      }
      if (this.m_Size != null) {
        break label234;
      }
      i = k;
    }
    label76:
    int j;
    label161:
    label188:
    label234:
    do
    {
      return i;
      if ((paramFileInfo.oneplusFlags & 0x2) == 0L) {
        break;
      }
      this.m_InternalFlags |= 0x1;
      break label29;
      if (paramDbValues.height <= 0) {
        break label36;
      }
      if (paramDbValues.orientation % 180 != 0)
      {
        j = paramDbValues.height;
        i = paramDbValues.width;
        if (this.m_Size != null) {
          break label161;
        }
      }
      while ((this.m_Size.getWidth() != j) || (this.m_Size.getHeight() != i))
      {
        k |= FLAG_SIZE_CHANGED;
        this.m_Size = new Size(j, i);
        if (this.m_SizeObtainingTask != null) {
          break label188;
        }
        return k;
        j = paramDbValues.width;
        i = paramDbValues.height;
        break;
      }
      return k;
      Log.w(TAG, "onUpdate() - Size changed, reschedule size obtaining");
      this.m_SizeObtainingTask.isCancelled = true;
      this.m_SizeObtainingTask = new SizeObtainingTask(null);
      FILE_INFO_EXECUTOR.submit(this.m_SizeObtainingTask);
      return k;
      j = FLAG_SIZE_CHANGED | k;
      this.m_Size = null;
      i = j;
    } while (this.m_SizeObtainingTask == null);
    Log.w(TAG, "onUpdate() - Size changed, reschedule size obtaining");
    this.m_SizeObtainingTask.isCancelled = true;
    this.m_SizeObtainingTask = new SizeObtainingTask(null);
    FILE_INFO_EXECUTOR.submit(this.m_SizeObtainingTask);
    return j;
  }
  
  public Boolean peekIsAnimatable()
  {
    return this.m_IsAnimatable;
  }
  
  public Size peekSize()
  {
    return this.m_Size;
  }
  
  private class CheckAnimatableHandle
    extends Handle
  {
    public volatile PhotoMedia.CheckAnimatableCallback callback;
    
    public CheckAnimatableHandle(PhotoMedia.CheckAnimatableCallback paramCheckAnimatableCallback)
    {
      super();
      this.callback = paramCheckAnimatableCallback;
    }
    
    protected void onClose(int paramInt)
    {
      PhotoMediaStoreMedia.this.m_CheckAnimatableHandles.remove(this);
    }
  }
  
  private final class SizeObtainingTask
    implements Runnable
  {
    public volatile boolean isCancelled;
    public volatile Size size;
    
    private SizeObtainingTask() {}
    
    public void run()
    {
      PhotoMediaStoreMedia.this.obtainSize(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/PhotoMediaStoreMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */