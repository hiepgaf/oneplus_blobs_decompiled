package android.filterfw.core;

import java.lang.reflect.Field;

public class ProgramPort
  extends FieldPort
{
  protected String mVarName;
  
  public ProgramPort(Filter paramFilter, String paramString1, String paramString2, Field paramField, boolean paramBoolean)
  {
    super(paramFilter, paramString1, paramField, paramBoolean);
    this.mVarName = paramString2;
  }
  
  public String toString()
  {
    return "Program " + super.toString();
  }
  
  /* Error */
  public void transfer(FilterContext paramFilterContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 40	android/filterfw/core/FieldPort:mValueWaiting	Z
    //   6: istore_2
    //   7: iload_2
    //   8: ifeq +39 -> 47
    //   11: aload_0
    //   12: getfield 44	android/filterfw/core/FieldPort:mField	Ljava/lang/reflect/Field;
    //   15: aload_0
    //   16: getfield 50	android/filterfw/core/FilterPort:mFilter	Landroid/filterfw/core/Filter;
    //   19: invokevirtual 56	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   22: astore_1
    //   23: aload_1
    //   24: ifnull +23 -> 47
    //   27: aload_1
    //   28: checkcast 58	android/filterfw/core/Program
    //   31: aload_0
    //   32: getfield 13	android/filterfw/core/ProgramPort:mVarName	Ljava/lang/String;
    //   35: aload_0
    //   36: getfield 62	android/filterfw/core/FieldPort:mValue	Ljava/lang/Object;
    //   39: invokevirtual 66	android/filterfw/core/Program:setHostValue	(Ljava/lang/String;Ljava/lang/Object;)V
    //   42: aload_0
    //   43: iconst_0
    //   44: putfield 40	android/filterfw/core/FieldPort:mValueWaiting	Z
    //   47: aload_0
    //   48: monitorexit
    //   49: return
    //   50: astore_1
    //   51: new 68	java/lang/RuntimeException
    //   54: dup
    //   55: new 18	java/lang/StringBuilder
    //   58: dup
    //   59: invokespecial 21	java/lang/StringBuilder:<init>	()V
    //   62: ldc 70
    //   64: invokevirtual 27	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: aload_0
    //   68: getfield 44	android/filterfw/core/FieldPort:mField	Ljava/lang/reflect/Field;
    //   71: invokevirtual 73	java/lang/reflect/Field:getName	()Ljava/lang/String;
    //   74: invokevirtual 27	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: ldc 75
    //   79: invokevirtual 27	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   82: invokevirtual 30	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   85: invokespecial 78	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   88: athrow
    //   89: astore_1
    //   90: aload_0
    //   91: monitorexit
    //   92: aload_1
    //   93: athrow
    //   94: astore_1
    //   95: new 68	java/lang/RuntimeException
    //   98: dup
    //   99: new 18	java/lang/StringBuilder
    //   102: dup
    //   103: invokespecial 21	java/lang/StringBuilder:<init>	()V
    //   106: ldc 80
    //   108: invokevirtual 27	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   111: aload_0
    //   112: getfield 44	android/filterfw/core/FieldPort:mField	Ljava/lang/reflect/Field;
    //   115: invokevirtual 73	java/lang/reflect/Field:getName	()Ljava/lang/String;
    //   118: invokevirtual 27	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   121: ldc 82
    //   123: invokevirtual 27	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   126: invokevirtual 30	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   129: invokespecial 78	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   132: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	133	0	this	ProgramPort
    //   0	133	1	paramFilterContext	FilterContext
    //   6	2	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   11	23	50	java/lang/ClassCastException
    //   27	47	50	java/lang/ClassCastException
    //   2	7	89	finally
    //   11	23	89	finally
    //   27	47	89	finally
    //   51	89	89	finally
    //   95	133	89	finally
    //   11	23	94	java/lang/IllegalAccessException
    //   27	47	94	java/lang/IllegalAccessException
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/ProgramPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */