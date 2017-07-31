package com.android.server;

import android.util.Slog;
import java.io.Closeable;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

class RandomBlock
{
  private static final int BLOCK_SIZE = 512;
  private static final boolean DEBUG = false;
  private static final String TAG = "RandomBlock";
  private byte[] block = new byte['Ȁ'];
  
  private static void close(Closeable paramCloseable)
  {
    if (paramCloseable == null) {
      return;
    }
    try
    {
      paramCloseable.close();
      return;
    }
    catch (IOException paramCloseable)
    {
      Slog.w("RandomBlock", "IOException thrown while closing Closeable", paramCloseable);
    }
  }
  
  static RandomBlock fromFile(String paramString)
    throws IOException
  {
    Object localObject3 = null;
    try
    {
      paramString = new FileInputStream(paramString);
      RandomBlock localRandomBlock;
      close(paramString);
    }
    finally
    {
      try
      {
        localRandomBlock = fromStream(paramString);
        close(paramString);
        return localRandomBlock;
      }
      finally {}
      localObject1 = finally;
      paramString = (String)localObject3;
    }
    throw ((Throwable)localObject1);
  }
  
  private static RandomBlock fromStream(InputStream paramInputStream)
    throws IOException
  {
    RandomBlock localRandomBlock = new RandomBlock();
    int i = 0;
    while (i < 512)
    {
      int j = paramInputStream.read(localRandomBlock.block, i, 512 - i);
      if (j == -1) {
        throw new EOFException();
      }
      i += j;
    }
    return localRandomBlock;
  }
  
  private void toDataOut(DataOutput paramDataOutput)
    throws IOException
  {
    paramDataOutput.write(this.block);
  }
  
  private static void truncateIfPossible(RandomAccessFile paramRandomAccessFile)
  {
    try
    {
      paramRandomAccessFile.setLength(512L);
      return;
    }
    catch (IOException paramRandomAccessFile) {}
  }
  
  /* Error */
  void toFile(String paramString, boolean paramBoolean)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: iload_2
    //   4: ifeq +30 -> 34
    //   7: ldc 85
    //   9: astore_3
    //   10: new 77	java/io/RandomAccessFile
    //   13: dup
    //   14: aload_1
    //   15: aload_3
    //   16: invokespecial 88	java/io/RandomAccessFile:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   19: astore_1
    //   20: aload_0
    //   21: aload_1
    //   22: invokespecial 90	com/android/server/RandomBlock:toDataOut	(Ljava/io/DataOutput;)V
    //   25: aload_1
    //   26: invokestatic 92	com/android/server/RandomBlock:truncateIfPossible	(Ljava/io/RandomAccessFile;)V
    //   29: aload_1
    //   30: invokestatic 52	com/android/server/RandomBlock:close	(Ljava/io/Closeable;)V
    //   33: return
    //   34: ldc 94
    //   36: astore_3
    //   37: goto -27 -> 10
    //   40: astore_3
    //   41: aload 4
    //   43: astore_1
    //   44: aload_1
    //   45: invokestatic 52	com/android/server/RandomBlock:close	(Ljava/io/Closeable;)V
    //   48: aload_3
    //   49: athrow
    //   50: astore_3
    //   51: goto -7 -> 44
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	54	0	this	RandomBlock
    //   0	54	1	paramString	String
    //   0	54	2	paramBoolean	boolean
    //   9	28	3	str	String
    //   40	9	3	localObject1	Object
    //   50	1	3	localObject2	Object
    //   1	41	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   10	20	40	finally
    //   20	29	50	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/RandomBlock.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */