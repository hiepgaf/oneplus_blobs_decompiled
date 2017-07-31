package android.media;

import android.content.res.AssetFileDescriptor;
import android.os.IBinder;
import com.android.internal.util.Preconditions;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public final class MediaExtractor
{
  public static final int SAMPLE_FLAG_ENCRYPTED = 2;
  public static final int SAMPLE_FLAG_SYNC = 1;
  public static final int SEEK_TO_CLOSEST_SYNC = 2;
  public static final int SEEK_TO_NEXT_SYNC = 1;
  public static final int SEEK_TO_PREVIOUS_SYNC = 0;
  private long mNativeContext;
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  public MediaExtractor()
  {
    native_setup();
  }
  
  private native Map<String, Object> getFileFormatNative();
  
  private native Map<String, Object> getTrackFormatNative(int paramInt);
  
  private final native void nativeSetDataSource(IBinder paramIBinder, String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2)
    throws IOException;
  
  private final native void native_finalize();
  
  private static final native void native_init();
  
  private final native void native_setup();
  
  public native boolean advance();
  
  protected void finalize()
  {
    native_finalize();
  }
  
  public native long getCachedDuration();
  
  public DrmInitData getDrmInitData()
  {
    final Object localObject1 = getFileFormatNative();
    if (localObject1 == null) {
      return null;
    }
    final Object localObject2;
    if (((Map)localObject1).containsKey("pssh"))
    {
      localObject2 = getPsshInfo();
      localObject1 = new HashMap();
      localObject2 = ((Map)localObject2).entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject2).next();
        ((Map)localObject1).put((UUID)localEntry.getKey(), new DrmInitData.SchemeInitData("cenc", (byte[])localEntry.getValue()));
      }
      new DrmInitData()
      {
        public DrmInitData.SchemeInitData get(UUID paramAnonymousUUID)
        {
          return (DrmInitData.SchemeInitData)localObject1.get(paramAnonymousUUID);
        }
      };
    }
    int j = getTrackCount();
    int i = 0;
    while (i < j)
    {
      localObject1 = getTrackFormatNative(i);
      if (!((Map)localObject1).containsKey("crypto-key"))
      {
        i += 1;
      }
      else
      {
        localObject1 = (ByteBuffer)((Map)localObject1).get("crypto-key");
        ((ByteBuffer)localObject1).rewind();
        localObject2 = new byte[((ByteBuffer)localObject1).remaining()];
        ((ByteBuffer)localObject1).get((byte[])localObject2);
        new DrmInitData()
        {
          public DrmInitData.SchemeInitData get(UUID paramAnonymousUUID)
          {
            return new DrmInitData.SchemeInitData("webm", localObject2);
          }
        };
      }
    }
    return null;
  }
  
  public Map<UUID, byte[]> getPsshInfo()
  {
    HashMap localHashMap = null;
    Object localObject2 = getFileFormatNative();
    Object localObject1 = localHashMap;
    if (localObject2 != null)
    {
      localObject1 = localHashMap;
      if (((Map)localObject2).containsKey("pssh"))
      {
        ByteBuffer localByteBuffer = (ByteBuffer)((Map)localObject2).get("pssh");
        localByteBuffer.order(ByteOrder.nativeOrder());
        localByteBuffer.rewind();
        ((Map)localObject2).remove("pssh");
        localHashMap = new HashMap();
        for (;;)
        {
          localObject1 = localHashMap;
          if (localByteBuffer.remaining() <= 0) {
            break;
          }
          localByteBuffer.order(ByteOrder.BIG_ENDIAN);
          localObject1 = new UUID(localByteBuffer.getLong(), localByteBuffer.getLong());
          localByteBuffer.order(ByteOrder.nativeOrder());
          localObject2 = new byte[localByteBuffer.getInt()];
          localByteBuffer.get((byte[])localObject2);
          localHashMap.put(localObject1, localObject2);
        }
      }
    }
    return (Map<UUID, byte[]>)localObject1;
  }
  
  public native boolean getSampleCryptoInfo(MediaCodec.CryptoInfo paramCryptoInfo);
  
  public native int getSampleFlags();
  
  public native long getSampleTime();
  
  public native int getSampleTrackIndex();
  
  public final native int getTrackCount();
  
  public MediaFormat getTrackFormat(int paramInt)
  {
    return new MediaFormat(getTrackFormatNative(paramInt));
  }
  
  public native boolean hasCacheReachedEndOfStream();
  
  public native int readSampleData(ByteBuffer paramByteBuffer, int paramInt);
  
  public final native void release();
  
  public native void seekTo(long paramLong, int paramInt);
  
  public native void selectTrack(int paramInt);
  
  /* Error */
  public final void setDataSource(android.content.Context paramContext, android.net.Uri paramUri, Map<String, String> paramMap)
    throws IOException
  {
    // Byte code:
    //   0: aload_2
    //   1: invokevirtual 203	android/net/Uri:getScheme	()Ljava/lang/String;
    //   4: astore 4
    //   6: aload 4
    //   8: ifnull +13 -> 21
    //   11: aload 4
    //   13: ldc -51
    //   15: invokevirtual 210	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   18: ifeq +12 -> 30
    //   21: aload_0
    //   22: aload_2
    //   23: invokevirtual 213	android/net/Uri:getPath	()Ljava/lang/String;
    //   26: invokevirtual 215	android/media/MediaExtractor:setDataSource	(Ljava/lang/String;)V
    //   29: return
    //   30: aconst_null
    //   31: astore 5
    //   33: aconst_null
    //   34: astore 6
    //   36: aconst_null
    //   37: astore 4
    //   39: aload_1
    //   40: invokevirtual 221	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   43: aload_2
    //   44: ldc -33
    //   46: invokevirtual 229	android/content/ContentResolver:openAssetFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;)Landroid/content/res/AssetFileDescriptor;
    //   49: astore_1
    //   50: aload_1
    //   51: ifnonnull +12 -> 63
    //   54: aload_1
    //   55: ifnull +7 -> 62
    //   58: aload_1
    //   59: invokevirtual 234	android/content/res/AssetFileDescriptor:close	()V
    //   62: return
    //   63: aload_1
    //   64: astore 4
    //   66: aload_1
    //   67: astore 5
    //   69: aload_1
    //   70: astore 6
    //   72: aload_1
    //   73: invokevirtual 237	android/content/res/AssetFileDescriptor:getDeclaredLength	()J
    //   76: lconst_0
    //   77: lcmp
    //   78: ifge +29 -> 107
    //   81: aload_1
    //   82: astore 4
    //   84: aload_1
    //   85: astore 5
    //   87: aload_1
    //   88: astore 6
    //   90: aload_0
    //   91: aload_1
    //   92: invokevirtual 241	android/content/res/AssetFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   95: invokevirtual 244	android/media/MediaExtractor:setDataSource	(Ljava/io/FileDescriptor;)V
    //   98: aload_1
    //   99: ifnull +7 -> 106
    //   102: aload_1
    //   103: invokevirtual 234	android/content/res/AssetFileDescriptor:close	()V
    //   106: return
    //   107: aload_1
    //   108: astore 4
    //   110: aload_1
    //   111: astore 5
    //   113: aload_1
    //   114: astore 6
    //   116: aload_0
    //   117: aload_1
    //   118: invokevirtual 241	android/content/res/AssetFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   121: aload_1
    //   122: invokevirtual 247	android/content/res/AssetFileDescriptor:getStartOffset	()J
    //   125: aload_1
    //   126: invokevirtual 237	android/content/res/AssetFileDescriptor:getDeclaredLength	()J
    //   129: invokevirtual 250	android/media/MediaExtractor:setDataSource	(Ljava/io/FileDescriptor;JJ)V
    //   132: goto -34 -> 98
    //   135: astore_1
    //   136: aload 4
    //   138: ifnull +8 -> 146
    //   141: aload 4
    //   143: invokevirtual 234	android/content/res/AssetFileDescriptor:close	()V
    //   146: aload_0
    //   147: aload_2
    //   148: invokevirtual 253	android/net/Uri:toString	()Ljava/lang/String;
    //   151: aload_3
    //   152: invokevirtual 256	android/media/MediaExtractor:setDataSource	(Ljava/lang/String;Ljava/util/Map;)V
    //   155: return
    //   156: astore_1
    //   157: aload 5
    //   159: ifnull -13 -> 146
    //   162: aload 5
    //   164: invokevirtual 234	android/content/res/AssetFileDescriptor:close	()V
    //   167: goto -21 -> 146
    //   170: astore_1
    //   171: aload 6
    //   173: ifnull +8 -> 181
    //   176: aload 6
    //   178: invokevirtual 234	android/content/res/AssetFileDescriptor:close	()V
    //   181: aload_1
    //   182: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	183	0	this	MediaExtractor
    //   0	183	1	paramContext	android.content.Context
    //   0	183	2	paramUri	android.net.Uri
    //   0	183	3	paramMap	Map<String, String>
    //   4	138	4	localObject	Object
    //   31	132	5	localContext1	android.content.Context
    //   34	143	6	localContext2	android.content.Context
    // Exception table:
    //   from	to	target	type
    //   39	50	135	java/lang/SecurityException
    //   72	81	135	java/lang/SecurityException
    //   90	98	135	java/lang/SecurityException
    //   116	132	135	java/lang/SecurityException
    //   39	50	156	java/io/IOException
    //   72	81	156	java/io/IOException
    //   90	98	156	java/io/IOException
    //   116	132	156	java/io/IOException
    //   39	50	170	finally
    //   72	81	170	finally
    //   90	98	170	finally
    //   116	132	170	finally
  }
  
  public final void setDataSource(AssetFileDescriptor paramAssetFileDescriptor)
    throws IOException, IllegalArgumentException, IllegalStateException
  {
    Preconditions.checkNotNull(paramAssetFileDescriptor);
    if (paramAssetFileDescriptor.getDeclaredLength() < 0L)
    {
      setDataSource(paramAssetFileDescriptor.getFileDescriptor());
      return;
    }
    setDataSource(paramAssetFileDescriptor.getFileDescriptor(), paramAssetFileDescriptor.getStartOffset(), paramAssetFileDescriptor.getDeclaredLength());
  }
  
  public final native void setDataSource(MediaDataSource paramMediaDataSource)
    throws IOException;
  
  public final void setDataSource(FileDescriptor paramFileDescriptor)
    throws IOException
  {
    setDataSource(paramFileDescriptor, 0L, 576460752303423487L);
  }
  
  public final native void setDataSource(FileDescriptor paramFileDescriptor, long paramLong1, long paramLong2)
    throws IOException;
  
  public final void setDataSource(String paramString)
    throws IOException
  {
    nativeSetDataSource(MediaHTTPService.createHttpServiceBinderIfNecessary(paramString), paramString, null, null);
  }
  
  public final void setDataSource(String paramString, Map<String, String> paramMap)
    throws IOException
  {
    Object localObject1 = null;
    Object localObject2 = null;
    if (paramMap != null)
    {
      String[] arrayOfString1 = new String[paramMap.size()];
      String[] arrayOfString2 = new String[paramMap.size()];
      int i = 0;
      paramMap = paramMap.entrySet().iterator();
      for (;;)
      {
        localObject1 = arrayOfString1;
        localObject2 = arrayOfString2;
        if (!paramMap.hasNext()) {
          break;
        }
        localObject1 = (Map.Entry)paramMap.next();
        arrayOfString1[i] = ((String)((Map.Entry)localObject1).getKey());
        arrayOfString2[i] = ((String)((Map.Entry)localObject1).getValue());
        i += 1;
      }
    }
    nativeSetDataSource(MediaHTTPService.createHttpServiceBinderIfNecessary(paramString), paramString, (String[])localObject1, (String[])localObject2);
  }
  
  public native void unselectTrack(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */