package com.android.server.policy;

import android.util.Log;
import java.io.FileOutputStream;
import java.io.IOException;

public final class FileUtils
{
  private static final int BUFFER = 512;
  private static final String TAG = "FileUtils";
  
  /* Error */
  public static String readOneLine(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aconst_null
    //   4: astore_1
    //   5: aconst_null
    //   6: astore_3
    //   7: new 22	java/io/BufferedReader
    //   10: dup
    //   11: new 24	java/io/FileReader
    //   14: dup
    //   15: aload_0
    //   16: invokespecial 27	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   19: sipush 512
    //   22: invokespecial 30	java/io/BufferedReader:<init>	(Ljava/io/Reader;I)V
    //   25: astore_2
    //   26: aload_2
    //   27: invokevirtual 34	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   30: astore_1
    //   31: aload_1
    //   32: astore_0
    //   33: aload_2
    //   34: ifnull +7 -> 41
    //   37: aload_2
    //   38: invokevirtual 37	java/io/BufferedReader:close	()V
    //   41: aload_0
    //   42: areturn
    //   43: astore_1
    //   44: goto -3 -> 41
    //   47: astore_1
    //   48: aload_3
    //   49: astore_2
    //   50: aload_1
    //   51: astore_3
    //   52: aload_2
    //   53: astore_1
    //   54: ldc 11
    //   56: new 39	java/lang/StringBuilder
    //   59: dup
    //   60: invokespecial 40	java/lang/StringBuilder:<init>	()V
    //   63: ldc 42
    //   65: invokevirtual 46	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   68: aload_0
    //   69: invokevirtual 46	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   72: invokevirtual 49	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   75: aload_3
    //   76: invokestatic 55	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   79: pop
    //   80: aload 4
    //   82: astore_0
    //   83: aload_2
    //   84: ifnull -43 -> 41
    //   87: aload_2
    //   88: invokevirtual 37	java/io/BufferedReader:close	()V
    //   91: aconst_null
    //   92: areturn
    //   93: astore_0
    //   94: aconst_null
    //   95: areturn
    //   96: astore_0
    //   97: aload_1
    //   98: ifnull +7 -> 105
    //   101: aload_1
    //   102: invokevirtual 37	java/io/BufferedReader:close	()V
    //   105: aload_0
    //   106: athrow
    //   107: astore_1
    //   108: goto -3 -> 105
    //   111: astore_0
    //   112: aload_2
    //   113: astore_1
    //   114: goto -17 -> 97
    //   117: astore_3
    //   118: goto -66 -> 52
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	121	0	paramString	String
    //   4	28	1	str	String
    //   43	1	1	localIOException1	IOException
    //   47	4	1	localIOException2	IOException
    //   53	49	1	localObject1	Object
    //   107	1	1	localIOException3	IOException
    //   113	1	1	localObject2	Object
    //   25	88	2	localObject3	Object
    //   6	70	3	localIOException4	IOException
    //   117	1	3	localIOException5	IOException
    //   1	80	4	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   37	41	43	java/io/IOException
    //   7	26	47	java/io/IOException
    //   87	91	93	java/io/IOException
    //   7	26	96	finally
    //   54	80	96	finally
    //   101	105	107	java/io/IOException
    //   26	31	111	finally
    //   26	31	117	java/io/IOException
  }
  
  public static boolean writeByteArray(String paramString, byte[] paramArrayOfByte)
  {
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
      localFileOutputStream.write(paramArrayOfByte);
      localFileOutputStream.flush();
      localFileOutputStream.close();
      return true;
    }
    catch (IOException paramArrayOfByte)
    {
      Log.e("FileUtils", "Could not write to file " + paramString, paramArrayOfByte);
    }
    return false;
  }
  
  public static boolean writeIntLine(String paramString, int paramInt)
  {
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramString);
      localFileOutputStream.write(paramInt);
      localFileOutputStream.flush();
      localFileOutputStream.close();
      return true;
    }
    catch (IOException localIOException)
    {
      Log.e("FileUtils", "Could not write to file " + paramString, localIOException);
    }
    return false;
  }
  
  public static boolean writeLine(String paramString1, String paramString2)
  {
    try
    {
      FileOutputStream localFileOutputStream = new FileOutputStream(paramString1);
      localFileOutputStream.write(paramString2.getBytes());
      localFileOutputStream.flush();
      localFileOutputStream.close();
      return true;
    }
    catch (IOException paramString2)
    {
      Log.e("FileUtils", "Could not write to file " + paramString1, paramString2);
    }
    return false;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/policy/FileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */