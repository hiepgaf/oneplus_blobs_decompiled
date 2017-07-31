package android.graphics;

import android.content.res.AssetManager;
import java.nio.ByteBuffer;
import java.util.List;

public class FontFamily
{
  private static String TAG = "FontFamily";
  public long mNativePtr;
  
  public FontFamily()
  {
    this.mNativePtr = nCreateFamily(null, 0);
    if (this.mNativePtr == 0L) {
      throw new IllegalStateException("error creating native FontFamily");
    }
  }
  
  public FontFamily(String paramString1, String paramString2)
  {
    int i = 0;
    if ("compact".equals(paramString2)) {
      i = 1;
    }
    for (;;)
    {
      this.mNativePtr = nCreateFamily(paramString1, i);
      if (this.mNativePtr != 0L) {
        break;
      }
      throw new IllegalStateException("error creating native FontFamily");
      if ("elegant".equals(paramString2)) {
        i = 2;
      }
    }
  }
  
  private static native boolean nAddFont(long paramLong, ByteBuffer paramByteBuffer, int paramInt);
  
  private static native boolean nAddFontFromAsset(long paramLong, AssetManager paramAssetManager, String paramString);
  
  private static native boolean nAddFontWeightStyle(long paramLong, ByteBuffer paramByteBuffer, int paramInt1, List<FontListParser.Axis> paramList, int paramInt2, boolean paramBoolean);
  
  private static native long nCreateFamily(String paramString, int paramInt);
  
  private static native void nUnrefFamily(long paramLong);
  
  /* Error */
  public boolean addFont(String paramString, int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 9
    //   3: aconst_null
    //   4: astore 8
    //   6: aconst_null
    //   7: astore 7
    //   9: new 58	java/io/FileInputStream
    //   12: dup
    //   13: aload_1
    //   14: invokespecial 59	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   17: astore 6
    //   19: aload 6
    //   21: invokevirtual 63	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   24: astore 7
    //   26: aload 7
    //   28: invokevirtual 69	java/nio/channels/FileChannel:size	()J
    //   31: lstore_3
    //   32: aload 7
    //   34: getstatic 75	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
    //   37: lconst_0
    //   38: lload_3
    //   39: invokevirtual 79	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
    //   42: astore 7
    //   44: aload_0
    //   45: getfield 24	android/graphics/FontFamily:mNativePtr	J
    //   48: aload 7
    //   50: iload_2
    //   51: invokestatic 81	android/graphics/FontFamily:nAddFont	(JLjava/nio/ByteBuffer;I)Z
    //   54: istore 5
    //   56: aload 9
    //   58: astore 7
    //   60: aload 6
    //   62: ifnull +12 -> 74
    //   65: aload 6
    //   67: invokevirtual 84	java/io/FileInputStream:close	()V
    //   70: aload 9
    //   72: astore 7
    //   74: aload 7
    //   76: ifnull +41 -> 117
    //   79: aload 7
    //   81: athrow
    //   82: astore 6
    //   84: getstatic 14	android/graphics/FontFamily:TAG	Ljava/lang/String;
    //   87: new 86	java/lang/StringBuilder
    //   90: dup
    //   91: invokespecial 87	java/lang/StringBuilder:<init>	()V
    //   94: ldc 89
    //   96: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   99: aload_1
    //   100: invokevirtual 93	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   103: invokevirtual 97	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   106: invokestatic 103	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   109: pop
    //   110: iconst_0
    //   111: ireturn
    //   112: astore 7
    //   114: goto -40 -> 74
    //   117: iload 5
    //   119: ireturn
    //   120: astore 8
    //   122: aload 7
    //   124: astore 6
    //   126: aload 8
    //   128: astore 7
    //   130: aload 7
    //   132: athrow
    //   133: astore 9
    //   135: aload 6
    //   137: astore 8
    //   139: aload 7
    //   141: astore 6
    //   143: aload 9
    //   145: astore 7
    //   147: aload 6
    //   149: astore 9
    //   151: aload 8
    //   153: ifnull +12 -> 165
    //   156: aload 8
    //   158: invokevirtual 84	java/io/FileInputStream:close	()V
    //   161: aload 6
    //   163: astore 9
    //   165: aload 9
    //   167: ifnull +31 -> 198
    //   170: aload 9
    //   172: athrow
    //   173: aload 6
    //   175: astore 9
    //   177: aload 6
    //   179: aload 8
    //   181: if_acmpeq -16 -> 165
    //   184: aload 6
    //   186: aload 8
    //   188: invokevirtual 107	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   191: aload 6
    //   193: astore 9
    //   195: goto -30 -> 165
    //   198: aload 7
    //   200: athrow
    //   201: astore 7
    //   203: aconst_null
    //   204: astore 6
    //   206: goto -59 -> 147
    //   209: astore 7
    //   211: aconst_null
    //   212: astore 9
    //   214: aload 6
    //   216: astore 8
    //   218: aload 9
    //   220: astore 6
    //   222: goto -75 -> 147
    //   225: astore 7
    //   227: goto -97 -> 130
    //   230: astore 6
    //   232: goto -148 -> 84
    //   235: astore 8
    //   237: aload 6
    //   239: ifnonnull -66 -> 173
    //   242: aload 8
    //   244: astore 9
    //   246: goto -81 -> 165
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	249	0	this	FontFamily
    //   0	249	1	paramString	String
    //   0	249	2	paramInt	int
    //   31	8	3	l	long
    //   54	64	5	bool	boolean
    //   17	49	6	localFileInputStream	java.io.FileInputStream
    //   82	1	6	localIOException1	java.io.IOException
    //   124	97	6	localObject1	Object
    //   230	8	6	localIOException2	java.io.IOException
    //   7	73	7	localObject2	Object
    //   112	11	7	localThrowable1	Throwable
    //   128	71	7	localObject3	Object
    //   201	1	7	localObject4	Object
    //   209	1	7	localObject5	Object
    //   225	1	7	localThrowable2	Throwable
    //   4	1	8	localObject6	Object
    //   120	7	8	localThrowable3	Throwable
    //   137	80	8	localObject7	Object
    //   235	8	8	localThrowable4	Throwable
    //   1	70	9	localObject8	Object
    //   133	11	9	localObject9	Object
    //   149	96	9	localObject10	Object
    // Exception table:
    //   from	to	target	type
    //   65	70	82	java/io/IOException
    //   79	82	82	java/io/IOException
    //   65	70	112	java/lang/Throwable
    //   9	19	120	java/lang/Throwable
    //   130	133	133	finally
    //   9	19	201	finally
    //   19	56	209	finally
    //   19	56	225	java/lang/Throwable
    //   156	161	230	java/io/IOException
    //   170	173	230	java/io/IOException
    //   184	191	230	java/io/IOException
    //   198	201	230	java/io/IOException
    //   156	161	235	java/lang/Throwable
  }
  
  public boolean addFontFromAsset(AssetManager paramAssetManager, String paramString)
  {
    return nAddFontFromAsset(this.mNativePtr, paramAssetManager, paramString);
  }
  
  public boolean addFontWeightStyle(ByteBuffer paramByteBuffer, int paramInt1, List<FontListParser.Axis> paramList, int paramInt2, boolean paramBoolean)
  {
    return nAddFontWeightStyle(this.mNativePtr, paramByteBuffer, paramInt1, paramList, paramInt2, paramBoolean);
  }
  
  protected void finalize()
    throws Throwable
  {
    try
    {
      nUnrefFamily(this.mNativePtr);
      return;
    }
    finally
    {
      super.finalize();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/FontFamily.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */