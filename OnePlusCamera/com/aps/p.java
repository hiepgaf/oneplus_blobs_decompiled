package com.aps;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class p
{
  private static List<File> a = new ArrayList();
  private g b;
  private int c;
  
  private p(File paramFile, int paramInt, long paramLong)
    throws IOException
  {
    this.c = paramInt;
    this.b = g.a(paramFile, paramInt, 1, paramLong);
  }
  
  public static p a(File paramFile, int paramInt, long paramLong)
    throws IOException
  {
    try
    {
      if (!a.contains(paramFile))
      {
        a.add(paramFile);
        paramFile = new p(paramFile, paramInt, paramLong);
        return paramFile;
      }
      throw new IllegalStateException("Cache dir " + paramFile.getAbsolutePath() + " was used before.");
    }
    finally {}
  }
  
  /* Error */
  private Map<String, Serializable> a(g.c paramc)
    throws IOException
  {
    // Byte code:
    //   0: new 81	java/io/ObjectInputStream
    //   3: dup
    //   4: new 83	java/io/BufferedInputStream
    //   7: dup
    //   8: aload_1
    //   9: iconst_0
    //   10: invokevirtual 88	com/aps/g$c:a	(I)Ljava/io/InputStream;
    //   13: invokespecial 91	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   16: invokespecial 92	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
    //   19: astore_2
    //   20: aload_2
    //   21: astore_1
    //   22: aload_2
    //   23: invokevirtual 96	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   26: checkcast 98	java/util/Map
    //   29: astore_3
    //   30: aload_2
    //   31: ifnonnull +28 -> 59
    //   34: aload_3
    //   35: areturn
    //   36: astore_3
    //   37: aconst_null
    //   38: astore_1
    //   39: new 100	java/lang/RuntimeException
    //   42: dup
    //   43: aload_3
    //   44: invokespecial 103	java/lang/RuntimeException:<init>	(Ljava/lang/Throwable;)V
    //   47: athrow
    //   48: astore_3
    //   49: aload_1
    //   50: astore_2
    //   51: aload_3
    //   52: astore_1
    //   53: aload_2
    //   54: ifnonnull +11 -> 65
    //   57: aload_1
    //   58: athrow
    //   59: aload_2
    //   60: invokevirtual 106	java/io/ObjectInputStream:close	()V
    //   63: aload_3
    //   64: areturn
    //   65: aload_2
    //   66: invokevirtual 106	java/io/ObjectInputStream:close	()V
    //   69: goto -12 -> 57
    //   72: astore_1
    //   73: aconst_null
    //   74: astore_2
    //   75: goto -22 -> 53
    //   78: astore_3
    //   79: aload_2
    //   80: astore_1
    //   81: goto -42 -> 39
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	84	0	this	p
    //   0	84	1	paramc	g.c
    //   19	61	2	localObject	Object
    //   29	6	3	localMap	Map
    //   36	8	3	localClassNotFoundException1	ClassNotFoundException
    //   48	16	3	localMap1	Map<String, Serializable>
    //   78	1	3	localClassNotFoundException2	ClassNotFoundException
    // Exception table:
    //   from	to	target	type
    //   0	20	36	java/lang/ClassNotFoundException
    //   22	30	48	finally
    //   39	48	48	finally
    //   0	20	72	finally
    //   22	30	78	java/lang/ClassNotFoundException
  }
  
  private String b(String paramString)
  {
    return c(paramString);
  }
  
  private String c(String paramString)
  {
    try
    {
      MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
      localMessageDigest.update(paramString.getBytes("UTF-8"));
      paramString = new BigInteger(1, localMessageDigest.digest()).toString(16);
      return paramString;
    }
    catch (NoSuchAlgorithmException paramString)
    {
      throw new AssertionError();
    }
    catch (UnsupportedEncodingException paramString)
    {
      throw new AssertionError();
    }
  }
  
  public OutputStream a(String paramString, Map<String, ? extends Serializable> paramMap)
    throws IOException
  {
    paramString = this.b.b(b(paramString));
    if (paramString != null) {}
    try
    {
      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(paramString.a(0));
      localObjectOutputStream.writeObject(paramMap);
      paramMap = new a(localObjectOutputStream, paramString, null);
      return paramMap;
    }
    catch (IOException paramMap)
    {
      paramString.b();
      throw paramMap;
    }
    return null;
  }
  
  public Map<String, Serializable> a(String paramString)
    throws IOException
  {
    paramString = this.b.a(b(paramString));
    if (paramString != null) {}
    try
    {
      Map localMap = a(paramString);
      return localMap;
    }
    finally
    {
      paramString.close();
    }
    return null;
  }
  
  public void a()
  {
    try
    {
      if (a == null) {}
      while (this.b == null)
      {
        return;
        a.clear();
      }
      this.b.close();
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
      return;
    }
  }
  
  public void b(String paramString, Map<String, ? extends Serializable> paramMap)
    throws IOException
  {
    try
    {
      paramString = a(paramString, paramMap);
      if (paramString == null) {
        return;
      }
    }
    finally {}
    paramString.close();
  }
  
  private static class a
    extends FilterOutputStream
  {
    private final g.a a;
    private boolean b = false;
    
    private a(OutputStream paramOutputStream, g.a parama)
    {
      super();
      this.a = parama;
    }
    
    public void close()
      throws IOException
    {
      Object localObject = null;
      try
      {
        super.close();
        if (!this.b)
        {
          this.a.a();
          if (localObject != null) {
            break label39;
          }
        }
      }
      catch (IOException localIOException)
      {
        for (;;)
        {
          continue;
          this.a.b();
        }
        label39:
        throw localIOException;
      }
    }
    
    public void flush()
      throws IOException
    {
      try
      {
        super.flush();
        return;
      }
      catch (IOException localIOException)
      {
        this.b = true;
        throw localIOException;
      }
    }
    
    public void write(int paramInt)
      throws IOException
    {
      try
      {
        super.write(paramInt);
        return;
      }
      catch (IOException localIOException)
      {
        this.b = true;
        throw localIOException;
      }
    }
    
    public void write(byte[] paramArrayOfByte)
      throws IOException
    {
      try
      {
        super.write(paramArrayOfByte);
        return;
      }
      catch (IOException paramArrayOfByte)
      {
        this.b = true;
        throw paramArrayOfByte;
      }
    }
    
    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
      throws IOException
    {
      try
      {
        super.write(paramArrayOfByte, paramInt1, paramInt2);
        return;
      }
      catch (IOException paramArrayOfByte)
      {
        this.b = true;
        throw paramArrayOfByte;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/p.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */