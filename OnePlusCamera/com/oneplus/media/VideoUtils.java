package com.oneplus.media;

import android.content.Context;
import android.net.Uri;
import com.oneplus.base.Log;

public final class VideoUtils
{
  private static final String TAG = "VideoUtils";
  private static final boolean USE_GENERIC_METADATA_ONLY = true;
  
  /* Error */
  public static boolean isIsoBaseMediaHeader(java.io.InputStream paramInputStream)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore 4
    //   5: aconst_null
    //   6: astore 6
    //   8: aconst_null
    //   9: astore 5
    //   11: new 22	com/oneplus/io/StreamState
    //   14: dup
    //   15: aload_0
    //   16: bipush 8
    //   18: invokespecial 25	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;I)V
    //   21: astore_2
    //   22: bipush 8
    //   24: newarray <illegal type>
    //   26: astore 5
    //   28: aload_0
    //   29: aload 5
    //   31: invokevirtual 31	java/io/InputStream:read	([B)I
    //   34: bipush 8
    //   36: if_icmpne +40 -> 76
    //   39: aload 5
    //   41: invokestatic 34	com/oneplus/media/VideoUtils:isIsoBaseMediaHeader	([B)Z
    //   44: istore_1
    //   45: aload 4
    //   47: astore_0
    //   48: aload_2
    //   49: ifnull +10 -> 59
    //   52: aload_2
    //   53: invokevirtual 37	com/oneplus/io/StreamState:close	()V
    //   56: aload 4
    //   58: astore_0
    //   59: aload_0
    //   60: ifnull +25 -> 85
    //   63: aload_0
    //   64: athrow
    //   65: astore_0
    //   66: ldc 8
    //   68: ldc 39
    //   70: aload_0
    //   71: invokestatic 45	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   74: iconst_0
    //   75: ireturn
    //   76: iconst_0
    //   77: istore_1
    //   78: goto -33 -> 45
    //   81: astore_0
    //   82: goto -23 -> 59
    //   85: iload_1
    //   86: ireturn
    //   87: astore_2
    //   88: aload 5
    //   90: astore_0
    //   91: aload_2
    //   92: athrow
    //   93: astore 4
    //   95: aload_2
    //   96: astore_3
    //   97: aload 4
    //   99: astore_2
    //   100: aload_3
    //   101: astore 4
    //   103: aload_0
    //   104: ifnull +10 -> 114
    //   107: aload_0
    //   108: invokevirtual 37	com/oneplus/io/StreamState:close	()V
    //   111: aload_3
    //   112: astore 4
    //   114: aload 4
    //   116: ifnull +25 -> 141
    //   119: aload 4
    //   121: athrow
    //   122: aload_3
    //   123: astore 4
    //   125: aload_3
    //   126: aload_0
    //   127: if_acmpeq -13 -> 114
    //   130: aload_3
    //   131: aload_0
    //   132: invokevirtual 49	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   135: aload_3
    //   136: astore 4
    //   138: goto -24 -> 114
    //   141: aload_2
    //   142: athrow
    //   143: astore_2
    //   144: aload 6
    //   146: astore_0
    //   147: goto -47 -> 100
    //   150: astore 4
    //   152: aload_2
    //   153: astore_0
    //   154: aload 4
    //   156: astore_2
    //   157: goto -57 -> 100
    //   160: astore_3
    //   161: aload_2
    //   162: astore_0
    //   163: aload_3
    //   164: astore_2
    //   165: goto -74 -> 91
    //   168: astore_0
    //   169: goto -103 -> 66
    //   172: astore_0
    //   173: aload_3
    //   174: ifnonnull -52 -> 122
    //   177: aload_0
    //   178: astore 4
    //   180: goto -66 -> 114
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	183	0	paramInputStream	java.io.InputStream
    //   44	42	1	bool	boolean
    //   21	32	2	localStreamState	com.oneplus.io.StreamState
    //   87	9	2	localThrowable1	Throwable
    //   99	43	2	localObject1	Object
    //   143	10	2	localObject2	Object
    //   156	9	2	localObject3	Object
    //   1	135	3	localObject4	Object
    //   160	14	3	localThrowable2	Throwable
    //   3	54	4	localObject5	Object
    //   93	5	4	localObject6	Object
    //   101	36	4	localObject7	Object
    //   150	5	4	localObject8	Object
    //   178	1	4	localInputStream	java.io.InputStream
    //   9	80	5	arrayOfByte	byte[]
    //   6	139	6	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   63	65	65	java/lang/Throwable
    //   52	56	81	java/lang/Throwable
    //   11	22	87	java/lang/Throwable
    //   91	93	93	finally
    //   11	22	143	finally
    //   22	45	150	finally
    //   22	45	160	java/lang/Throwable
    //   119	122	168	java/lang/Throwable
    //   130	135	168	java/lang/Throwable
    //   141	143	168	java/lang/Throwable
    //   107	111	172	java/lang/Throwable
  }
  
  public static boolean isIsoBaseMediaHeader(byte[] paramArrayOfByte)
  {
    boolean bool2 = false;
    if ((paramArrayOfByte == null) || (paramArrayOfByte.length < 8)) {
      return false;
    }
    boolean bool1 = bool2;
    if (paramArrayOfByte[4] == 102)
    {
      bool1 = bool2;
      if (paramArrayOfByte[5] == 116)
      {
        bool1 = bool2;
        if (paramArrayOfByte[6] == 121)
        {
          bool1 = bool2;
          if (paramArrayOfByte[7] == 112) {
            bool1 = true;
          }
        }
      }
    }
    return bool1;
  }
  
  public static VideoMetadata readMetadata(Context paramContext, Uri paramUri)
  {
    if (paramContext == null)
    {
      Log.e("VideoUtils", "readMetadata() - No context");
      return null;
    }
    if (paramUri == null)
    {
      Log.e("VideoUtils", "readMetadata() - No content URI");
      return null;
    }
    try
    {
      paramContext = new GenericVideoMetadata(paramContext, paramUri);
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      Log.e("VideoUtils", "readMetadata() - Fail to read metadata", paramContext);
    }
    return null;
  }
  
  /* Error */
  public static VideoMetadata readMetadata(java.io.InputStream paramInputStream)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +12 -> 13
    //   4: ldc 8
    //   6: ldc 68
    //   8: invokestatic 56	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   11: aconst_null
    //   12: areturn
    //   13: new 22	com/oneplus/io/StreamState
    //   16: dup
    //   17: aload_0
    //   18: invokespecial 71	com/oneplus/io/StreamState:<init>	(Ljava/io/InputStream;)V
    //   21: astore_0
    //   22: aload_0
    //   23: ifnull +7 -> 30
    //   26: aload_0
    //   27: invokevirtual 37	com/oneplus/io/StreamState:close	()V
    //   30: aconst_null
    //   31: astore_0
    //   32: aload_0
    //   33: ifnull +20 -> 53
    //   36: aload_0
    //   37: athrow
    //   38: astore_0
    //   39: ldc 8
    //   41: ldc 65
    //   43: aload_0
    //   44: invokestatic 45	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   47: aconst_null
    //   48: areturn
    //   49: astore_0
    //   50: goto -18 -> 32
    //   53: aconst_null
    //   54: areturn
    //   55: astore_0
    //   56: aload_0
    //   57: athrow
    //   58: astore_1
    //   59: aload_0
    //   60: astore_2
    //   61: iconst_0
    //   62: ifeq +11 -> 73
    //   65: new 73	java/lang/NullPointerException
    //   68: dup
    //   69: invokespecial 74	java/lang/NullPointerException:<init>	()V
    //   72: athrow
    //   73: aload_2
    //   74: ifnull +22 -> 96
    //   77: aload_2
    //   78: athrow
    //   79: aload_0
    //   80: astore_2
    //   81: aload_0
    //   82: aload_3
    //   83: if_acmpeq -10 -> 73
    //   86: aload_0
    //   87: aload_3
    //   88: invokevirtual 49	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   91: aload_0
    //   92: astore_2
    //   93: goto -20 -> 73
    //   96: aload_1
    //   97: athrow
    //   98: astore_1
    //   99: aconst_null
    //   100: astore_0
    //   101: goto -42 -> 59
    //   104: astore_0
    //   105: goto -66 -> 39
    //   108: astore_3
    //   109: aload_0
    //   110: ifnonnull -31 -> 79
    //   113: aload_3
    //   114: astore_2
    //   115: goto -42 -> 73
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	118	0	paramInputStream	java.io.InputStream
    //   58	39	1	localObject1	Object
    //   98	1	1	localObject2	Object
    //   60	55	2	localObject3	Object
    //   82	6	3	localThrowable1	Throwable
    //   108	6	3	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   36	38	38	java/lang/Throwable
    //   26	30	49	java/lang/Throwable
    //   13	22	55	java/lang/Throwable
    //   56	58	58	finally
    //   13	22	98	finally
    //   77	79	104	java/lang/Throwable
    //   86	91	104	java/lang/Throwable
    //   96	98	104	java/lang/Throwable
    //   65	73	108	java/lang/Throwable
  }
  
  /* Error */
  public static VideoMetadata readMetadata(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +12 -> 13
    //   4: ldc 8
    //   6: ldc 77
    //   8: invokestatic 56	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   11: aconst_null
    //   12: areturn
    //   13: aconst_null
    //   14: astore_3
    //   15: aconst_null
    //   16: astore_2
    //   17: new 79	java/io/FileInputStream
    //   20: dup
    //   21: aload_0
    //   22: invokespecial 82	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   25: astore_1
    //   26: aload_1
    //   27: invokestatic 84	com/oneplus/media/VideoUtils:readMetadata	(Ljava/io/InputStream;)Lcom/oneplus/media/VideoMetadata;
    //   30: astore_2
    //   31: aload_2
    //   32: ifnull +36 -> 68
    //   35: aload_1
    //   36: ifnull +7 -> 43
    //   39: aload_1
    //   40: invokevirtual 85	java/io/FileInputStream:close	()V
    //   43: aconst_null
    //   44: astore_0
    //   45: aload_0
    //   46: ifnull +20 -> 66
    //   49: aload_0
    //   50: athrow
    //   51: astore_0
    //   52: ldc 8
    //   54: ldc 65
    //   56: aload_0
    //   57: invokestatic 45	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   60: aconst_null
    //   61: areturn
    //   62: astore_0
    //   63: goto -18 -> 45
    //   66: aload_2
    //   67: areturn
    //   68: aload_1
    //   69: ifnull +7 -> 76
    //   72: aload_1
    //   73: invokevirtual 85	java/io/FileInputStream:close	()V
    //   76: aconst_null
    //   77: astore_1
    //   78: aload_1
    //   79: ifnull +50 -> 129
    //   82: aload_1
    //   83: athrow
    //   84: astore_1
    //   85: goto -7 -> 78
    //   88: astore_0
    //   89: aload_0
    //   90: athrow
    //   91: astore_1
    //   92: aload_0
    //   93: astore_3
    //   94: aload_2
    //   95: ifnull +9 -> 104
    //   98: aload_2
    //   99: invokevirtual 85	java/io/FileInputStream:close	()V
    //   102: aload_0
    //   103: astore_3
    //   104: aload_3
    //   105: ifnull +22 -> 127
    //   108: aload_3
    //   109: athrow
    //   110: aload_0
    //   111: astore_3
    //   112: aload_0
    //   113: aload_2
    //   114: if_acmpeq -10 -> 104
    //   117: aload_0
    //   118: aload_2
    //   119: invokevirtual 49	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   122: aload_0
    //   123: astore_3
    //   124: goto -20 -> 104
    //   127: aload_1
    //   128: athrow
    //   129: new 60	com/oneplus/media/GenericVideoMetadata
    //   132: dup
    //   133: aload_0
    //   134: invokespecial 86	com/oneplus/media/GenericVideoMetadata:<init>	(Ljava/lang/String;)V
    //   137: astore_0
    //   138: aload_0
    //   139: areturn
    //   140: astore_1
    //   141: aconst_null
    //   142: astore_0
    //   143: aload_3
    //   144: astore_2
    //   145: goto -53 -> 92
    //   148: astore_3
    //   149: aconst_null
    //   150: astore_0
    //   151: aload_1
    //   152: astore_2
    //   153: aload_3
    //   154: astore_1
    //   155: goto -63 -> 92
    //   158: astore_0
    //   159: aload_1
    //   160: astore_2
    //   161: goto -72 -> 89
    //   164: astore_0
    //   165: goto -113 -> 52
    //   168: astore_2
    //   169: aload_0
    //   170: ifnonnull -60 -> 110
    //   173: aload_2
    //   174: astore_3
    //   175: goto -71 -> 104
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	178	0	paramString	String
    //   25	58	1	localFileInputStream	java.io.FileInputStream
    //   84	1	1	localThrowable1	Throwable
    //   91	37	1	localObject1	Object
    //   140	12	1	localObject2	Object
    //   154	6	1	localObject3	Object
    //   16	145	2	localObject4	Object
    //   168	6	2	localThrowable2	Throwable
    //   14	130	3	str	String
    //   148	6	3	localObject5	Object
    //   174	1	3	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   49	51	51	java/lang/Throwable
    //   82	84	51	java/lang/Throwable
    //   129	138	51	java/lang/Throwable
    //   39	43	62	java/lang/Throwable
    //   72	76	84	java/lang/Throwable
    //   17	26	88	java/lang/Throwable
    //   89	91	91	finally
    //   17	26	140	finally
    //   26	31	148	finally
    //   26	31	158	java/lang/Throwable
    //   108	110	164	java/lang/Throwable
    //   117	122	164	java/lang/Throwable
    //   127	129	164	java/lang/Throwable
    //   98	102	168	java/lang/Throwable
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/VideoUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */