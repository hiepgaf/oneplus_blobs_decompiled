package android.filterfw.core;

import java.lang.reflect.Field;

public class FieldPort
  extends InputPort
{
  protected Field mField;
  protected boolean mHasFrame;
  protected Object mValue;
  protected boolean mValueWaiting = false;
  
  public FieldPort(Filter paramFilter, String paramString, Field paramField, boolean paramBoolean)
  {
    super(paramFilter, paramString);
    this.mField = paramField;
    this.mHasFrame = paramBoolean;
  }
  
  /* Error */
  public boolean acceptsFrame()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 18	android/filterfw/core/FieldPort:mValueWaiting	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +9 -> 17
    //   11: iconst_0
    //   12: istore_1
    //   13: aload_0
    //   14: monitorexit
    //   15: iload_1
    //   16: ireturn
    //   17: iconst_1
    //   18: istore_1
    //   19: goto -6 -> 13
    //   22: astore_2
    //   23: aload_0
    //   24: monitorexit
    //   25: aload_2
    //   26: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	27	0	this	FieldPort
    //   6	13	1	bool	boolean
    //   22	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	22	finally
  }
  
  public void clear() {}
  
  public Object getTarget()
  {
    try
    {
      Object localObject = this.mField.get(this.mFilter);
      return localObject;
    }
    catch (IllegalAccessException localIllegalAccessException) {}
    return null;
  }
  
  public boolean hasFrame()
  {
    try
    {
      boolean bool = this.mHasFrame;
      return bool;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public Frame pullFrame()
  {
    try
    {
      throw new RuntimeException("Cannot pull frame on " + this + "!");
    }
    finally {}
  }
  
  public void pushFrame(Frame paramFrame)
  {
    setFieldFrame(paramFrame, false);
  }
  
  /* Error */
  protected void setFieldFrame(Frame paramFrame, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 79	android/filterfw/core/FilterPort:assertPortIsOpen	()V
    //   6: aload_0
    //   7: aload_1
    //   8: iload_2
    //   9: invokevirtual 82	android/filterfw/core/FilterPort:checkFrameType	(Landroid/filterfw/core/Frame;Z)V
    //   12: aload_1
    //   13: invokevirtual 87	android/filterfw/core/Frame:getObjectValue	()Ljava/lang/Object;
    //   16: astore_1
    //   17: aload_1
    //   18: ifnonnull +28 -> 46
    //   21: aload_0
    //   22: getfield 89	android/filterfw/core/FieldPort:mValue	Ljava/lang/Object;
    //   25: ifnull +21 -> 46
    //   28: aload_0
    //   29: aload_1
    //   30: putfield 89	android/filterfw/core/FieldPort:mValue	Ljava/lang/Object;
    //   33: aload_0
    //   34: iconst_1
    //   35: putfield 18	android/filterfw/core/FieldPort:mValueWaiting	Z
    //   38: aload_0
    //   39: iconst_1
    //   40: putfield 22	android/filterfw/core/FieldPort:mHasFrame	Z
    //   43: aload_0
    //   44: monitorexit
    //   45: return
    //   46: aload_1
    //   47: aload_0
    //   48: getfield 89	android/filterfw/core/FieldPort:mValue	Ljava/lang/Object;
    //   51: invokevirtual 95	java/lang/Object:equals	(Ljava/lang/Object;)Z
    //   54: istore_2
    //   55: iload_2
    //   56: ifeq -28 -> 28
    //   59: goto -21 -> 38
    //   62: astore_1
    //   63: aload_0
    //   64: monitorexit
    //   65: aload_1
    //   66: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	67	0	this	FieldPort
    //   0	67	1	paramFrame	Frame
    //   0	67	2	paramBoolean	boolean
    // Exception table:
    //   from	to	target	type
    //   2	17	62	finally
    //   21	28	62	finally
    //   28	38	62	finally
    //   38	43	62	finally
    //   46	55	62	finally
  }
  
  public void setFrame(Frame paramFrame)
  {
    setFieldFrame(paramFrame, true);
  }
  
  public String toString()
  {
    return "field " + super.toString();
  }
  
  /* Error */
  public void transfer(FilterContext paramFilterContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 18	android/filterfw/core/FieldPort:mValueWaiting	Z
    //   6: istore_2
    //   7: iload_2
    //   8: ifeq +39 -> 47
    //   11: aload_0
    //   12: getfield 20	android/filterfw/core/FieldPort:mField	Ljava/lang/reflect/Field;
    //   15: aload_0
    //   16: getfield 37	android/filterfw/core/FilterPort:mFilter	Landroid/filterfw/core/Filter;
    //   19: aload_0
    //   20: getfield 89	android/filterfw/core/FieldPort:mValue	Ljava/lang/Object;
    //   23: invokevirtual 105	java/lang/reflect/Field:set	(Ljava/lang/Object;Ljava/lang/Object;)V
    //   26: aload_0
    //   27: iconst_0
    //   28: putfield 18	android/filterfw/core/FieldPort:mValueWaiting	Z
    //   31: aload_1
    //   32: ifnull +15 -> 47
    //   35: aload_0
    //   36: getfield 37	android/filterfw/core/FilterPort:mFilter	Landroid/filterfw/core/Filter;
    //   39: aload_0
    //   40: getfield 109	android/filterfw/core/FilterPort:mName	Ljava/lang/String;
    //   43: aload_1
    //   44: invokevirtual 115	android/filterfw/core/Filter:notifyFieldPortValueUpdated	(Ljava/lang/String;Landroid/filterfw/core/FilterContext;)V
    //   47: aload_0
    //   48: monitorexit
    //   49: return
    //   50: astore_1
    //   51: new 48	java/lang/RuntimeException
    //   54: dup
    //   55: new 50	java/lang/StringBuilder
    //   58: dup
    //   59: invokespecial 52	java/lang/StringBuilder:<init>	()V
    //   62: ldc 117
    //   64: invokevirtual 58	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: aload_0
    //   68: getfield 20	android/filterfw/core/FieldPort:mField	Ljava/lang/reflect/Field;
    //   71: invokevirtual 120	java/lang/reflect/Field:getName	()Ljava/lang/String;
    //   74: invokevirtual 58	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: ldc 122
    //   79: invokevirtual 58	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   82: invokevirtual 67	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   85: invokespecial 70	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   88: athrow
    //   89: astore_1
    //   90: aload_0
    //   91: monitorexit
    //   92: aload_1
    //   93: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	94	0	this	FieldPort
    //   0	94	1	paramFilterContext	FilterContext
    //   6	2	2	bool	boolean
    // Exception table:
    //   from	to	target	type
    //   11	26	50	java/lang/IllegalAccessException
    //   2	7	89	finally
    //   11	26	89	finally
    //   26	31	89	finally
    //   35	47	89	finally
    //   51	89	89	finally
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/FieldPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */