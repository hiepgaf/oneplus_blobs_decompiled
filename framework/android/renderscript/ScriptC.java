package android.renderscript;

import android.content.res.Resources;

public class ScriptC
  extends Script
{
  private static final String TAG = "ScriptC";
  
  protected ScriptC(int paramInt, RenderScript paramRenderScript)
  {
    super(paramInt, paramRenderScript);
  }
  
  protected ScriptC(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
  }
  
  protected ScriptC(RenderScript paramRenderScript, Resources paramResources, int paramInt)
  {
    super(0L, paramRenderScript);
    long l = internalCreate(paramRenderScript, paramResources, paramInt);
    if (l == 0L) {
      throw new RSRuntimeException("Loading of ScriptC script failed.");
    }
    setID(l);
  }
  
  protected ScriptC(RenderScript paramRenderScript, String paramString, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    super(0L, paramRenderScript);
    if (RenderScript.sPointerSize == 4) {}
    for (long l = internalStringCreate(paramRenderScript, paramString, paramArrayOfByte1); l == 0L; l = internalStringCreate(paramRenderScript, paramString, paramArrayOfByte2)) {
      throw new RSRuntimeException("Loading of ScriptC script failed.");
    }
    setID(l);
  }
  
  /* Error */
  private static long internalCreate(RenderScript paramRenderScript, Resources paramResources, int paramInt)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aload_1
    //   4: iload_2
    //   5: invokevirtual 51	android/content/res/Resources:openRawResource	(I)Ljava/io/InputStream;
    //   8: astore 10
    //   10: sipush 1024
    //   13: newarray <illegal type>
    //   15: astore 8
    //   17: iconst_0
    //   18: istore_3
    //   19: aload 8
    //   21: arraylength
    //   22: iload_3
    //   23: isub
    //   24: istore 5
    //   26: iload 5
    //   28: istore 4
    //   30: aload 8
    //   32: astore 9
    //   34: iload 5
    //   36: ifne +39 -> 75
    //   39: aload 8
    //   41: arraylength
    //   42: iconst_2
    //   43: imul
    //   44: newarray <illegal type>
    //   46: astore 9
    //   48: aload 8
    //   50: iconst_0
    //   51: aload 9
    //   53: iconst_0
    //   54: aload 8
    //   56: arraylength
    //   57: invokestatic 57	java/lang/System:arraycopy	([BI[BII)V
    //   60: aload 9
    //   62: astore 8
    //   64: aload 9
    //   66: arraylength
    //   67: iload_3
    //   68: isub
    //   69: istore 4
    //   71: aload 8
    //   73: astore 9
    //   75: aload 10
    //   77: aload 9
    //   79: iload_3
    //   80: iload 4
    //   82: invokevirtual 63	java/io/InputStream:read	([BII)I
    //   85: istore 4
    //   87: iload 4
    //   89: ifgt +31 -> 120
    //   92: aload 10
    //   94: invokevirtual 67	java/io/InputStream:close	()V
    //   97: aload_0
    //   98: aload_1
    //   99: iload_2
    //   100: invokevirtual 71	android/content/res/Resources:getResourceEntryName	(I)Ljava/lang/String;
    //   103: invokestatic 75	android/renderscript/RenderScript:getCachePath	()Ljava/lang/String;
    //   106: aload 9
    //   108: iload_3
    //   109: invokevirtual 79	android/renderscript/RenderScript:nScriptCCreate	(Ljava/lang/String;Ljava/lang/String;[BI)J
    //   112: lstore 6
    //   114: ldc 2
    //   116: monitorexit
    //   117: lload 6
    //   119: lreturn
    //   120: iload_3
    //   121: iload 4
    //   123: iadd
    //   124: istore_3
    //   125: aload 9
    //   127: astore 8
    //   129: goto -110 -> 19
    //   132: astore_0
    //   133: aload 10
    //   135: invokevirtual 67	java/io/InputStream:close	()V
    //   138: aload_0
    //   139: athrow
    //   140: astore_0
    //   141: new 81	android/content/res/Resources$NotFoundException
    //   144: dup
    //   145: invokespecial 83	android/content/res/Resources$NotFoundException:<init>	()V
    //   148: athrow
    //   149: astore_0
    //   150: ldc 2
    //   152: monitorexit
    //   153: aload_0
    //   154: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	155	0	paramRenderScript	RenderScript
    //   0	155	1	paramResources	Resources
    //   0	155	2	paramInt	int
    //   18	107	3	i	int
    //   28	96	4	j	int
    //   24	11	5	k	int
    //   112	6	6	l	long
    //   15	113	8	localObject1	Object
    //   32	94	9	localObject2	Object
    //   8	126	10	localInputStream	java.io.InputStream
    // Exception table:
    //   from	to	target	type
    //   10	17	132	finally
    //   19	26	132	finally
    //   39	60	132	finally
    //   64	71	132	finally
    //   75	87	132	finally
    //   92	97	140	java/io/IOException
    //   133	140	140	java/io/IOException
    //   3	10	149	finally
    //   92	97	149	finally
    //   97	114	149	finally
    //   133	140	149	finally
    //   141	149	149	finally
  }
  
  private static long internalStringCreate(RenderScript paramRenderScript, String paramString, byte[] paramArrayOfByte)
  {
    try
    {
      long l = paramRenderScript.nScriptCCreate(paramString, RenderScript.getCachePath(), paramArrayOfByte, paramArrayOfByte.length);
      return l;
    }
    finally
    {
      paramRenderScript = finally;
      throw paramRenderScript;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ScriptC.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */