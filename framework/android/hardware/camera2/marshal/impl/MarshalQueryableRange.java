package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.Range;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public class MarshalQueryableRange<T extends Comparable<? super T>>
  implements MarshalQueryable<Range<T>>
{
  private static final int RANGE_COUNT = 2;
  
  public Marshaler<Range<T>> createMarshaler(TypeReference<Range<T>> paramTypeReference, int paramInt)
  {
    return new MarshalerRange(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<Range<T>> paramTypeReference, int paramInt)
  {
    return Range.class.equals(paramTypeReference.getRawType());
  }
  
  private class MarshalerRange
    extends Marshaler<Range<T>>
  {
    private final Class<? super Range<T>> mClass;
    private final Constructor<Range<T>> mConstructor;
    private final Marshaler<T> mNestedTypeMarshaler;
    
    /* Error */
    protected MarshalerRange(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: putfield 27	android/hardware/camera2/marshal/impl/MarshalQueryableRange$MarshalerRange:this$0	Landroid/hardware/camera2/marshal/impl/MarshalQueryableRange;
      //   5: aload_0
      //   6: aload_1
      //   7: aload_2
      //   8: iload_3
      //   9: invokespecial 30	android/hardware/camera2/marshal/Marshaler:<init>	(Landroid/hardware/camera2/marshal/MarshalQueryable;Landroid/hardware/camera2/utils/TypeReference;I)V
      //   12: aload_0
      //   13: aload_2
      //   14: invokevirtual 36	android/hardware/camera2/utils/TypeReference:getRawType	()Ljava/lang/Class;
      //   17: putfield 38	android/hardware/camera2/marshal/impl/MarshalQueryableRange$MarshalerRange:mClass	Ljava/lang/Class;
      //   20: aload_2
      //   21: invokevirtual 42	android/hardware/camera2/utils/TypeReference:getType	()Ljava/lang/reflect/Type;
      //   24: checkcast 44	java/lang/reflect/ParameterizedType
      //   27: astore_1
      //   28: aload_0
      //   29: aload_1
      //   30: invokeinterface 48 1 0
      //   35: iconst_0
      //   36: aaload
      //   37: invokestatic 52	android/hardware/camera2/utils/TypeReference:createSpecializedTypeReference	(Ljava/lang/reflect/Type;)Landroid/hardware/camera2/utils/TypeReference;
      //   40: aload_0
      //   41: getfield 56	android/hardware/camera2/marshal/impl/MarshalQueryableRange$MarshalerRange:mNativeType	I
      //   44: invokestatic 62	android/hardware/camera2/marshal/MarshalRegistry:getMarshaler	(Landroid/hardware/camera2/utils/TypeReference;I)Landroid/hardware/camera2/marshal/Marshaler;
      //   47: putfield 64	android/hardware/camera2/marshal/impl/MarshalQueryableRange$MarshalerRange:mNestedTypeMarshaler	Landroid/hardware/camera2/marshal/Marshaler;
      //   50: aload_0
      //   51: aload_0
      //   52: getfield 38	android/hardware/camera2/marshal/impl/MarshalQueryableRange$MarshalerRange:mClass	Ljava/lang/Class;
      //   55: iconst_2
      //   56: anewarray 66	java/lang/Class
      //   59: dup
      //   60: iconst_0
      //   61: ldc 68
      //   63: aastore
      //   64: dup
      //   65: iconst_1
      //   66: ldc 68
      //   68: aastore
      //   69: invokevirtual 72	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
      //   72: putfield 74	android/hardware/camera2/marshal/impl/MarshalQueryableRange$MarshalerRange:mConstructor	Ljava/lang/reflect/Constructor;
      //   75: return
      //   76: astore_1
      //   77: new 76	java/lang/AssertionError
      //   80: dup
      //   81: ldc 78
      //   83: aload_1
      //   84: invokespecial 81	java/lang/AssertionError:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   87: athrow
      //   88: astore_1
      //   89: new 76	java/lang/AssertionError
      //   92: dup
      //   93: aload_1
      //   94: invokespecial 84	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
      //   97: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	98	0	this	MarshalerRange
      //   0	98	1	this$1	MarshalQueryableRange
      //   0	98	2	paramInt	int
      //   8	1	3	i	int
      // Exception table:
      //   from	to	target	type
      //   20	28	76	java/lang/ClassCastException
      //   50	75	88	java/lang/NoSuchMethodException
    }
    
    public int calculateMarshalSize(Range<T> paramRange)
    {
      int i = getNativeSize();
      if (i != NATIVE_SIZE_DYNAMIC) {
        return i;
      }
      return this.mNestedTypeMarshaler.calculateMarshalSize(paramRange.getLower()) + this.mNestedTypeMarshaler.calculateMarshalSize(paramRange.getUpper());
    }
    
    public int getNativeSize()
    {
      int i = this.mNestedTypeMarshaler.getNativeSize();
      if (i != NATIVE_SIZE_DYNAMIC) {
        return i * 2;
      }
      return NATIVE_SIZE_DYNAMIC;
    }
    
    public void marshal(Range<T> paramRange, ByteBuffer paramByteBuffer)
    {
      this.mNestedTypeMarshaler.marshal(paramRange.getLower(), paramByteBuffer);
      this.mNestedTypeMarshaler.marshal(paramRange.getUpper(), paramByteBuffer);
    }
    
    public Range<T> unmarshal(ByteBuffer paramByteBuffer)
    {
      Comparable localComparable = (Comparable)this.mNestedTypeMarshaler.unmarshal(paramByteBuffer);
      paramByteBuffer = (Comparable)this.mNestedTypeMarshaler.unmarshal(paramByteBuffer);
      try
      {
        paramByteBuffer = (Range)this.mConstructor.newInstance(new Object[] { localComparable, paramByteBuffer });
        return paramByteBuffer;
      }
      catch (InvocationTargetException paramByteBuffer)
      {
        throw new AssertionError(paramByteBuffer);
      }
      catch (IllegalArgumentException paramByteBuffer)
      {
        throw new AssertionError(paramByteBuffer);
      }
      catch (IllegalAccessException paramByteBuffer)
      {
        throw new AssertionError(paramByteBuffer);
      }
      catch (InstantiationException paramByteBuffer)
      {
        throw new AssertionError(paramByteBuffer);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableRange.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */