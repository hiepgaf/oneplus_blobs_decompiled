package com.oneplus.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import com.oneplus.base.Log;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class CompressedBitmapLruCache<TKey extends Serializable>
  extends AsyncLruCache<TKey, Bitmap>
{
  private final Bitmap.Config m_BitmapConfig;
  private final Bitmap.CompressFormat m_CompressFormat;
  private final ConcurrentHashMap<TKey, byte[]> m_CompressedBitmaps = new ConcurrentHashMap();
  
  public CompressedBitmapLruCache(Context paramContext, String paramString, long paramLong)
  {
    this(paramContext, paramString, Bitmap.Config.RGB_565, Bitmap.CompressFormat.JPEG, paramLong);
  }
  
  public CompressedBitmapLruCache(Context paramContext, String paramString, Bitmap.Config paramConfig, Bitmap.CompressFormat paramCompressFormat, long paramLong)
  {
    super(paramLong);
    if (paramConfig == null) {
      throw new IllegalArgumentException("No bitmap configuration specified.");
    }
    if (paramCompressFormat == null) {
      throw new IllegalArgumentException("No bitmap compression format specified.");
    }
    this.m_BitmapConfig = paramConfig;
    this.m_CompressFormat = paramCompressFormat;
  }
  
  protected Object addEntry(TKey paramTKey, Bitmap paramBitmap)
  {
    return this.m_CompressedBitmaps.get(paramTKey);
  }
  
  protected Bitmap get(TKey paramTKey, Object paramObject, Bitmap paramBitmap, long paramLong)
  {
    paramTKey = (byte[])paramObject;
    if (paramTKey == null) {
      return paramBitmap;
    }
    paramObject = new BitmapFactory.Options();
    ((BitmapFactory.Options)paramObject).inPreferredConfig = Bitmap.Config.ARGB_8888;
    paramObject = BitmapFactory.decodeByteArray(paramTKey, 0, paramTKey.length, (BitmapFactory.Options)paramObject);
    if (paramObject != null)
    {
      paramTKey = (TKey)paramObject;
      if (this.m_BitmapConfig != Bitmap.Config.ARGB_8888) {}
      try
      {
        paramTKey = ((Bitmap)paramObject).copy(this.m_BitmapConfig, false);
        return paramTKey;
      }
      catch (Throwable paramTKey)
      {
        Log.e(this.TAG, "get() - Fail to copy bitmap as " + this.m_BitmapConfig, paramTKey);
        return null;
      }
    }
    return paramBitmap;
  }
  
  /* Error */
  protected long getSizeInBytes(TKey paramTKey, Bitmap paramBitmap)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 7
    //   3: aconst_null
    //   4: astore 8
    //   6: aconst_null
    //   7: astore 10
    //   9: aconst_null
    //   10: astore 9
    //   12: new 124	java/io/ByteArrayOutputStream
    //   15: dup
    //   16: invokespecial 125	java/io/ByteArrayOutputStream:<init>	()V
    //   19: astore 6
    //   21: aload_2
    //   22: aload_0
    //   23: getfield 51	com/oneplus/cache/CompressedBitmapLruCache:m_CompressFormat	Landroid/graphics/Bitmap$CompressFormat;
    //   26: bipush 80
    //   28: aload 6
    //   30: invokevirtual 129	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   33: pop
    //   34: aload 6
    //   36: invokevirtual 133	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   39: astore_2
    //   40: aload_0
    //   41: getfield 38	com/oneplus/cache/CompressedBitmapLruCache:m_CompressedBitmaps	Ljava/util/concurrent/ConcurrentHashMap;
    //   44: aload_1
    //   45: aload_2
    //   46: invokevirtual 136	java/util/concurrent/ConcurrentHashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   49: pop
    //   50: aload_2
    //   51: arraylength
    //   52: istore_3
    //   53: iload_3
    //   54: i2l
    //   55: lstore 4
    //   57: aload 8
    //   59: astore_1
    //   60: aload 6
    //   62: ifnull +11 -> 73
    //   65: aload 6
    //   67: invokevirtual 139	java/io/ByteArrayOutputStream:close	()V
    //   70: aload 8
    //   72: astore_1
    //   73: aload_1
    //   74: ifnull +22 -> 96
    //   77: aload_1
    //   78: athrow
    //   79: astore_1
    //   80: aload_0
    //   81: getfield 94	com/oneplus/cache/CompressedBitmapLruCache:TAG	Ljava/lang/String;
    //   84: ldc -115
    //   86: aload_1
    //   87: invokestatic 116	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   90: lconst_0
    //   91: lreturn
    //   92: astore_1
    //   93: goto -20 -> 73
    //   96: lload 4
    //   98: lreturn
    //   99: astore_2
    //   100: aload 9
    //   102: astore_1
    //   103: aload_2
    //   104: athrow
    //   105: astore 7
    //   107: aload_2
    //   108: astore 6
    //   110: aload 7
    //   112: astore_2
    //   113: aload 6
    //   115: astore 7
    //   117: aload_1
    //   118: ifnull +11 -> 129
    //   121: aload_1
    //   122: invokevirtual 139	java/io/ByteArrayOutputStream:close	()V
    //   125: aload 6
    //   127: astore 7
    //   129: aload 7
    //   131: ifnull +29 -> 160
    //   134: aload 7
    //   136: athrow
    //   137: aload 6
    //   139: astore 7
    //   141: aload 6
    //   143: aload_1
    //   144: if_acmpeq -15 -> 129
    //   147: aload 6
    //   149: aload_1
    //   150: invokevirtual 145	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   153: aload 6
    //   155: astore 7
    //   157: goto -28 -> 129
    //   160: aload_2
    //   161: athrow
    //   162: astore_2
    //   163: aload 10
    //   165: astore_1
    //   166: aload 7
    //   168: astore 6
    //   170: goto -57 -> 113
    //   173: astore_2
    //   174: aload 6
    //   176: astore_1
    //   177: aload 7
    //   179: astore 6
    //   181: goto -68 -> 113
    //   184: astore_2
    //   185: aload 6
    //   187: astore_1
    //   188: goto -85 -> 103
    //   191: astore_1
    //   192: goto -112 -> 80
    //   195: astore_1
    //   196: aload 6
    //   198: ifnonnull -61 -> 137
    //   201: aload_1
    //   202: astore 7
    //   204: goto -75 -> 129
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	207	0	this	CompressedBitmapLruCache
    //   0	207	1	paramTKey	TKey
    //   0	207	2	paramBitmap	Bitmap
    //   52	2	3	i	int
    //   55	42	4	l	long
    //   19	178	6	localObject1	Object
    //   1	1	7	localObject2	Object
    //   105	6	7	localObject3	Object
    //   115	88	7	localObject4	Object
    //   4	67	8	localObject5	Object
    //   10	91	9	localObject6	Object
    //   7	157	10	localObject7	Object
    // Exception table:
    //   from	to	target	type
    //   77	79	79	java/lang/Throwable
    //   65	70	92	java/lang/Throwable
    //   12	21	99	java/lang/Throwable
    //   103	105	105	finally
    //   12	21	162	finally
    //   21	53	173	finally
    //   21	53	184	java/lang/Throwable
    //   134	137	191	java/lang/Throwable
    //   147	153	191	java/lang/Throwable
    //   160	162	191	java/lang/Throwable
    //   121	125	195	java/lang/Throwable
  }
  
  protected void removeEntry(TKey paramTKey, Object paramObject)
  {
    this.m_CompressedBitmaps.remove(paramTKey);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/cache/CompressedBitmapLruCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */