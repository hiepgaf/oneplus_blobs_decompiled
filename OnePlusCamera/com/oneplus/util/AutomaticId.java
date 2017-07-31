package com.oneplus.util;

public class AutomaticId
{
  private static final String PREFIX_SEPARATOR = "/";
  private static final String SUFFIX_SEPARATOR = "_";
  private static volatile long m_PreviousTimeNanos;
  private static volatile long m_SuffixCounter;
  
  public static String generate()
  {
    return generate("");
  }
  
  /* Error */
  public static String generate(String paramString)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: invokestatic 32	android/os/SystemClock:elapsedRealtimeNanos	()J
    //   6: lstore_1
    //   7: lload_1
    //   8: invokestatic 38	java/lang/Long:toHexString	(J)Ljava/lang/String;
    //   11: astore 4
    //   13: ldc 23
    //   15: astore_3
    //   16: lload_1
    //   17: getstatic 40	com/oneplus/util/AutomaticId:m_PreviousTimeNanos	J
    //   20: lcmp
    //   21: ifeq +45 -> 66
    //   24: lload_1
    //   25: putstatic 40	com/oneplus/util/AutomaticId:m_PreviousTimeNanos	J
    //   28: lconst_0
    //   29: putstatic 42	com/oneplus/util/AutomaticId:m_SuffixCounter	J
    //   32: new 44	java/lang/StringBuilder
    //   35: dup
    //   36: invokespecial 45	java/lang/StringBuilder:<init>	()V
    //   39: aload_0
    //   40: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   43: ldc 8
    //   45: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: aload 4
    //   50: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   53: aload_3
    //   54: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   57: invokevirtual 52	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   60: astore_0
    //   61: ldc 2
    //   63: monitorexit
    //   64: aload_0
    //   65: areturn
    //   66: getstatic 42	com/oneplus/util/AutomaticId:m_SuffixCounter	J
    //   69: lconst_1
    //   70: ladd
    //   71: putstatic 42	com/oneplus/util/AutomaticId:m_SuffixCounter	J
    //   74: new 44	java/lang/StringBuilder
    //   77: dup
    //   78: invokespecial 45	java/lang/StringBuilder:<init>	()V
    //   81: ldc 11
    //   83: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: getstatic 42	com/oneplus/util/AutomaticId:m_SuffixCounter	J
    //   89: invokestatic 38	java/lang/Long:toHexString	(J)Ljava/lang/String;
    //   92: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   95: invokevirtual 52	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   98: astore_3
    //   99: goto -67 -> 32
    //   102: astore_0
    //   103: ldc 2
    //   105: monitorexit
    //   106: aload_0
    //   107: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	108	0	paramString	String
    //   6	19	1	l	long
    //   15	84	3	str1	String
    //   11	38	4	str2	String
    // Exception table:
    //   from	to	target	type
    //   3	13	102	finally
    //   16	32	102	finally
    //   32	61	102	finally
    //   66	99	102	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/util/AutomaticId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */