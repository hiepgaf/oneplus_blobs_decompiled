package android.graphics;

import android.content.res.AssetManager.AssetInputStream;
import android.content.res.Resources;
import android.os.Trace;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;

public class BitmapFactory
{
  private static final int DECODE_BUFFER_SIZE = 16384;
  
  public static Bitmap decodeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return decodeByteArray(paramArrayOfByte, paramInt1, paramInt2, null);
  }
  
  public static Bitmap decodeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2, Options paramOptions)
  {
    if (((paramInt1 | paramInt2) < 0) || (paramArrayOfByte.length < paramInt1 + paramInt2)) {
      throw new ArrayIndexOutOfBoundsException();
    }
    Trace.traceBegin(2L, "decodeBitmap");
    try
    {
      paramArrayOfByte = nativeDecodeByteArray(paramArrayOfByte, paramInt1, paramInt2, paramOptions);
      if ((paramArrayOfByte == null) && (paramOptions != null) && (paramOptions.inBitmap != null)) {
        throw new IllegalArgumentException("Problem decoding into existing bitmap");
      }
    }
    finally
    {
      Trace.traceEnd(2L);
    }
    setDensityFromOptions(paramArrayOfByte, paramOptions);
    Trace.traceEnd(2L);
    return paramArrayOfByte;
  }
  
  public static Bitmap decodeFile(String paramString)
  {
    return decodeFile(paramString, null);
  }
  
  /* Error */
  public static Bitmap decodeFile(String paramString, Options paramOptions)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore_2
    //   4: aconst_null
    //   5: astore 4
    //   7: new 66	java/io/FileInputStream
    //   10: dup
    //   11: aload_0
    //   12: invokespecial 67	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   15: astore_0
    //   16: aload_0
    //   17: aconst_null
    //   18: aload_1
    //   19: invokestatic 71	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   22: astore_1
    //   23: aload_0
    //   24: ifnull +7 -> 31
    //   27: aload_0
    //   28: invokevirtual 76	java/io/InputStream:close	()V
    //   31: aload_1
    //   32: areturn
    //   33: astore_0
    //   34: goto -3 -> 31
    //   37: astore_1
    //   38: aload 4
    //   40: astore_0
    //   41: aload_0
    //   42: astore_2
    //   43: ldc 78
    //   45: new 80	java/lang/StringBuilder
    //   48: dup
    //   49: invokespecial 81	java/lang/StringBuilder:<init>	()V
    //   52: ldc 83
    //   54: invokevirtual 87	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: aload_1
    //   58: invokevirtual 90	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   61: invokevirtual 94	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   64: invokestatic 100	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   67: pop
    //   68: aload_3
    //   69: astore_1
    //   70: aload_0
    //   71: ifnull -40 -> 31
    //   74: aload_0
    //   75: invokevirtual 76	java/io/InputStream:close	()V
    //   78: aconst_null
    //   79: areturn
    //   80: astore_0
    //   81: aconst_null
    //   82: areturn
    //   83: astore_0
    //   84: aload_2
    //   85: ifnull +7 -> 92
    //   88: aload_2
    //   89: invokevirtual 76	java/io/InputStream:close	()V
    //   92: aload_0
    //   93: athrow
    //   94: astore_1
    //   95: goto -3 -> 92
    //   98: astore_1
    //   99: aload_0
    //   100: astore_2
    //   101: aload_1
    //   102: astore_0
    //   103: goto -19 -> 84
    //   106: astore_1
    //   107: goto -66 -> 41
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	110	0	paramString	String
    //   0	110	1	paramOptions	Options
    //   3	98	2	str	String
    //   1	68	3	localObject1	Object
    //   5	34	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   27	31	33	java/io/IOException
    //   7	16	37	java/lang/Exception
    //   74	78	80	java/io/IOException
    //   7	16	83	finally
    //   43	68	83	finally
    //   88	92	94	java/io/IOException
    //   16	23	98	finally
    //   16	23	106	java/lang/Exception
  }
  
  public static Bitmap decodeFileDescriptor(FileDescriptor paramFileDescriptor)
  {
    return decodeFileDescriptor(paramFileDescriptor, null, null);
  }
  
  /* Error */
  public static Bitmap decodeFileDescriptor(FileDescriptor paramFileDescriptor, Rect paramRect, Options paramOptions)
  {
    // Byte code:
    //   0: ldc2_w 24
    //   3: ldc 108
    //   5: invokestatic 33	android/os/Trace:traceBegin	(JLjava/lang/String;)V
    //   8: aload_0
    //   9: invokestatic 112	android/graphics/BitmapFactory:nativeIsSeekable	(Ljava/io/FileDescriptor;)Z
    //   12: ifeq +44 -> 56
    //   15: aload_0
    //   16: aload_1
    //   17: aload_2
    //   18: invokestatic 115	android/graphics/BitmapFactory:nativeDecodeFileDescriptor	(Ljava/io/FileDescriptor;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   21: astore_0
    //   22: aload_0
    //   23: ifnonnull +67 -> 90
    //   26: aload_2
    //   27: ifnull +63 -> 90
    //   30: aload_2
    //   31: getfield 40	android/graphics/BitmapFactory$Options:inBitmap	Landroid/graphics/Bitmap;
    //   34: ifnull +56 -> 90
    //   37: new 42	java/lang/IllegalArgumentException
    //   40: dup
    //   41: ldc 44
    //   43: invokespecial 47	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   46: athrow
    //   47: astore_0
    //   48: ldc2_w 24
    //   51: invokestatic 51	android/os/Trace:traceEnd	(J)V
    //   54: aload_0
    //   55: athrow
    //   56: new 66	java/io/FileInputStream
    //   59: dup
    //   60: aload_0
    //   61: invokespecial 118	java/io/FileInputStream:<init>	(Ljava/io/FileDescriptor;)V
    //   64: astore_3
    //   65: aload_3
    //   66: aload_1
    //   67: aload_2
    //   68: invokestatic 121	android/graphics/BitmapFactory:decodeStreamInternal	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   71: astore_0
    //   72: aload_3
    //   73: invokevirtual 122	java/io/FileInputStream:close	()V
    //   76: goto -54 -> 22
    //   79: astore_1
    //   80: goto -58 -> 22
    //   83: astore_0
    //   84: aload_3
    //   85: invokevirtual 122	java/io/FileInputStream:close	()V
    //   88: aload_0
    //   89: athrow
    //   90: aload_0
    //   91: aload_2
    //   92: invokestatic 55	android/graphics/BitmapFactory:setDensityFromOptions	(Landroid/graphics/Bitmap;Landroid/graphics/BitmapFactory$Options;)V
    //   95: ldc2_w 24
    //   98: invokestatic 51	android/os/Trace:traceEnd	(J)V
    //   101: aload_0
    //   102: areturn
    //   103: astore_1
    //   104: goto -16 -> 88
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	107	0	paramFileDescriptor	FileDescriptor
    //   0	107	1	paramRect	Rect
    //   0	107	2	paramOptions	Options
    //   64	21	3	localFileInputStream	java.io.FileInputStream
    // Exception table:
    //   from	to	target	type
    //   8	22	47	finally
    //   30	47	47	finally
    //   56	65	47	finally
    //   72	76	47	finally
    //   84	88	47	finally
    //   88	90	47	finally
    //   90	95	47	finally
    //   72	76	79	java/lang/Throwable
    //   65	72	83	finally
    //   84	88	103	java/lang/Throwable
  }
  
  public static Bitmap decodeResource(Resources paramResources, int paramInt)
  {
    return decodeResource(paramResources, paramInt, null);
  }
  
  public static Bitmap decodeResource(Resources paramResources, int paramInt, Options paramOptions)
  {
    Object localObject3 = null;
    Object localObject4 = null;
    InputStream localInputStream = null;
    Object localObject1 = localInputStream;
    Object localObject2 = localObject4;
    for (;;)
    {
      try
      {
        TypedValue localTypedValue = new TypedValue();
        localObject1 = localInputStream;
        localObject2 = localObject4;
        localInputStream = paramResources.openRawResource(paramInt, localTypedValue);
        localObject1 = localInputStream;
        localObject2 = localInputStream;
        paramResources = decodeResourceStream(paramResources, localTypedValue, localInputStream, null, paramOptions);
        localObject1 = paramResources;
        paramResources = (Resources)localObject1;
        if (localInputStream == null) {}
      }
      catch (Exception paramResources)
      {
        paramResources = (Resources)localObject3;
        if (localObject1 == null) {
          continue;
        }
        try
        {
          ((InputStream)localObject1).close();
          paramResources = (Resources)localObject3;
        }
        catch (IOException paramResources)
        {
          paramResources = (Resources)localObject3;
        }
        continue;
      }
      finally
      {
        if (localObject2 == null) {
          continue;
        }
        try
        {
          ((InputStream)localObject2).close();
          throw paramResources;
        }
        catch (IOException paramOptions)
        {
          continue;
        }
      }
      try
      {
        localInputStream.close();
        paramResources = (Resources)localObject1;
      }
      catch (IOException paramResources)
      {
        paramResources = (Resources)localObject1;
      }
    }
    if ((paramResources == null) && (paramOptions != null) && (paramOptions.inBitmap != null)) {
      throw new IllegalArgumentException("Problem decoding into existing bitmap");
    }
    return paramResources;
  }
  
  public static Bitmap decodeResourceStream(Resources paramResources, TypedValue paramTypedValue, InputStream paramInputStream, Rect paramRect, Options paramOptions)
  {
    Options localOptions = paramOptions;
    if (paramOptions == null) {
      localOptions = new Options();
    }
    int i;
    if ((localOptions.inDensity == 0) && (paramTypedValue != null))
    {
      i = paramTypedValue.density;
      if (i != 0) {
        break label81;
      }
      localOptions.inDensity = 160;
    }
    for (;;)
    {
      if ((localOptions.inTargetDensity == 0) && (paramResources != null)) {
        localOptions.inTargetDensity = paramResources.getDisplayMetrics().densityDpi;
      }
      return decodeStream(paramInputStream, paramRect, localOptions);
      label81:
      if (i != 65535) {
        localOptions.inDensity = i;
      }
    }
  }
  
  public static Bitmap decodeStream(InputStream paramInputStream)
  {
    return decodeStream(paramInputStream, null, null);
  }
  
  public static Bitmap decodeStream(InputStream paramInputStream, Rect paramRect, Options paramOptions)
  {
    if (paramInputStream == null) {
      return null;
    }
    Trace.traceBegin(2L, "decodeBitmap");
    for (;;)
    {
      try
      {
        if ((paramInputStream instanceof AssetManager.AssetInputStream))
        {
          paramInputStream = nativeDecodeAsset(((AssetManager.AssetInputStream)paramInputStream).getNativeAsset(), paramRect, paramOptions);
          if ((paramInputStream != null) || (paramOptions == null) || (paramOptions.inBitmap == null)) {
            break;
          }
          throw new IllegalArgumentException("Problem decoding into existing bitmap");
        }
      }
      finally
      {
        Trace.traceEnd(2L);
      }
      paramInputStream = decodeStreamInternal(paramInputStream, paramRect, paramOptions);
    }
    setDensityFromOptions(paramInputStream, paramOptions);
    Trace.traceEnd(2L);
    return paramInputStream;
  }
  
  private static Bitmap decodeStreamInternal(InputStream paramInputStream, Rect paramRect, Options paramOptions)
  {
    byte[] arrayOfByte1 = null;
    if (paramOptions != null) {
      arrayOfByte1 = paramOptions.inTempStorage;
    }
    byte[] arrayOfByte2 = arrayOfByte1;
    if (arrayOfByte1 == null) {
      arrayOfByte2 = new byte['䀀'];
    }
    return nativeDecodeStream(paramInputStream, arrayOfByte2, paramRect, paramOptions);
  }
  
  private static native Bitmap nativeDecodeAsset(long paramLong, Rect paramRect, Options paramOptions);
  
  private static native Bitmap nativeDecodeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2, Options paramOptions);
  
  private static native Bitmap nativeDecodeFileDescriptor(FileDescriptor paramFileDescriptor, Rect paramRect, Options paramOptions);
  
  private static native Bitmap nativeDecodeStream(InputStream paramInputStream, byte[] paramArrayOfByte, Rect paramRect, Options paramOptions);
  
  private static native boolean nativeIsSeekable(FileDescriptor paramFileDescriptor);
  
  private static void setDensityFromOptions(Bitmap paramBitmap, Options paramOptions)
  {
    if ((paramBitmap == null) || (paramOptions == null)) {
      return;
    }
    int i = paramOptions.inDensity;
    if (i != 0)
    {
      paramBitmap.setDensity(i);
      j = paramOptions.inTargetDensity;
      if ((j == 0) || (i == j)) {}
      while (i == paramOptions.inScreenDensity) {
        return;
      }
      arrayOfByte = paramBitmap.getNinePatchChunk();
      if (arrayOfByte != null)
      {
        bool = NinePatch.isNinePatchChunk(arrayOfByte);
        if ((paramOptions.inScaled) || (bool)) {
          paramBitmap.setDensity(j);
        }
      }
    }
    while (paramOptions.inBitmap == null) {
      for (;;)
      {
        int j;
        byte[] arrayOfByte;
        return;
        boolean bool = false;
      }
    }
    paramBitmap.setDensity(Bitmap.getDefaultDensity());
  }
  
  public static class Options
  {
    public Bitmap inBitmap;
    public int inDensity;
    public boolean inDither = false;
    @Deprecated
    public boolean inInputShareable;
    public boolean inJustDecodeBounds;
    public boolean inMutable;
    public boolean inPreferQualityOverSpeed;
    public Bitmap.Config inPreferredConfig = Bitmap.Config.ARGB_8888;
    public boolean inPremultiplied = true;
    @Deprecated
    public boolean inPurgeable;
    public int inSampleSize;
    public boolean inScaled = true;
    public int inScreenDensity;
    public int inTargetDensity;
    public byte[] inTempStorage;
    public boolean mCancel;
    public int outHeight;
    public String outMimeType;
    public int outWidth;
    
    public void requestCancelDecode()
    {
      this.mCancel = true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/BitmapFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */