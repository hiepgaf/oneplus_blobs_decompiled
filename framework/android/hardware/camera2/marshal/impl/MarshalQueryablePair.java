package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.Pair;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;

public class MarshalQueryablePair<T1, T2>
  implements MarshalQueryable<Pair<T1, T2>>
{
  public Marshaler<Pair<T1, T2>> createMarshaler(TypeReference<Pair<T1, T2>> paramTypeReference, int paramInt)
  {
    return new MarshalerPair(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<Pair<T1, T2>> paramTypeReference, int paramInt)
  {
    return Pair.class.equals(paramTypeReference.getRawType());
  }
  
  private class MarshalerPair
    extends Marshaler<Pair<T1, T2>>
  {
    private final Class<? super Pair<T1, T2>> mClass;
    private final Constructor<Pair<T1, T2>> mConstructor;
    private final Marshaler<T1> mNestedTypeMarshalerFirst;
    private final Marshaler<T2> mNestedTypeMarshalerSecond;
    
    /* Error */
    protected MarshalerPair(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: putfield 29	android/hardware/camera2/marshal/impl/MarshalQueryablePair$MarshalerPair:this$0	Landroid/hardware/camera2/marshal/impl/MarshalQueryablePair;
      //   5: aload_0
      //   6: aload_1
      //   7: aload_2
      //   8: iload_3
      //   9: invokespecial 32	android/hardware/camera2/marshal/Marshaler:<init>	(Landroid/hardware/camera2/marshal/MarshalQueryable;Landroid/hardware/camera2/utils/TypeReference;I)V
      //   12: aload_0
      //   13: aload_2
      //   14: invokevirtual 38	android/hardware/camera2/utils/TypeReference:getRawType	()Ljava/lang/Class;
      //   17: putfield 40	android/hardware/camera2/marshal/impl/MarshalQueryablePair$MarshalerPair:mClass	Ljava/lang/Class;
      //   20: aload_2
      //   21: invokevirtual 44	android/hardware/camera2/utils/TypeReference:getType	()Ljava/lang/reflect/Type;
      //   24: checkcast 46	java/lang/reflect/ParameterizedType
      //   27: astore_1
      //   28: aload_0
      //   29: aload_1
      //   30: invokeinterface 50 1 0
      //   35: iconst_0
      //   36: aaload
      //   37: invokestatic 54	android/hardware/camera2/utils/TypeReference:createSpecializedTypeReference	(Ljava/lang/reflect/Type;)Landroid/hardware/camera2/utils/TypeReference;
      //   40: aload_0
      //   41: getfield 58	android/hardware/camera2/marshal/impl/MarshalQueryablePair$MarshalerPair:mNativeType	I
      //   44: invokestatic 64	android/hardware/camera2/marshal/MarshalRegistry:getMarshaler	(Landroid/hardware/camera2/utils/TypeReference;I)Landroid/hardware/camera2/marshal/Marshaler;
      //   47: putfield 66	android/hardware/camera2/marshal/impl/MarshalQueryablePair$MarshalerPair:mNestedTypeMarshalerFirst	Landroid/hardware/camera2/marshal/Marshaler;
      //   50: aload_0
      //   51: aload_1
      //   52: invokeinterface 50 1 0
      //   57: iconst_1
      //   58: aaload
      //   59: invokestatic 54	android/hardware/camera2/utils/TypeReference:createSpecializedTypeReference	(Ljava/lang/reflect/Type;)Landroid/hardware/camera2/utils/TypeReference;
      //   62: aload_0
      //   63: getfield 58	android/hardware/camera2/marshal/impl/MarshalQueryablePair$MarshalerPair:mNativeType	I
      //   66: invokestatic 64	android/hardware/camera2/marshal/MarshalRegistry:getMarshaler	(Landroid/hardware/camera2/utils/TypeReference;I)Landroid/hardware/camera2/marshal/Marshaler;
      //   69: putfield 68	android/hardware/camera2/marshal/impl/MarshalQueryablePair$MarshalerPair:mNestedTypeMarshalerSecond	Landroid/hardware/camera2/marshal/Marshaler;
      //   72: aload_0
      //   73: aload_0
      //   74: getfield 40	android/hardware/camera2/marshal/impl/MarshalQueryablePair$MarshalerPair:mClass	Ljava/lang/Class;
      //   77: iconst_2
      //   78: anewarray 70	java/lang/Class
      //   81: dup
      //   82: iconst_0
      //   83: ldc 72
      //   85: aastore
      //   86: dup
      //   87: iconst_1
      //   88: ldc 72
      //   90: aastore
      //   91: invokevirtual 76	java/lang/Class:getConstructor	([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;
      //   94: putfield 78	android/hardware/camera2/marshal/impl/MarshalQueryablePair$MarshalerPair:mConstructor	Ljava/lang/reflect/Constructor;
      //   97: return
      //   98: astore_1
      //   99: new 80	java/lang/AssertionError
      //   102: dup
      //   103: ldc 82
      //   105: aload_1
      //   106: invokespecial 85	java/lang/AssertionError:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
      //   109: athrow
      //   110: astore_1
      //   111: new 80	java/lang/AssertionError
      //   114: dup
      //   115: aload_1
      //   116: invokespecial 88	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
      //   119: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	120	0	this	MarshalerPair
      //   0	120	1	this$1	MarshalQueryablePair
      //   0	120	2	paramInt	int
      //   8	1	3	i	int
      // Exception table:
      //   from	to	target	type
      //   20	28	98	java/lang/ClassCastException
      //   72	97	110	java/lang/NoSuchMethodException
    }
    
    public int calculateMarshalSize(Pair<T1, T2> paramPair)
    {
      int i = getNativeSize();
      if (i != NATIVE_SIZE_DYNAMIC) {
        return i;
      }
      return this.mNestedTypeMarshalerFirst.calculateMarshalSize(paramPair.first) + this.mNestedTypeMarshalerSecond.calculateMarshalSize(paramPair.second);
    }
    
    public int getNativeSize()
    {
      int i = this.mNestedTypeMarshalerFirst.getNativeSize();
      int j = this.mNestedTypeMarshalerSecond.getNativeSize();
      if ((i != NATIVE_SIZE_DYNAMIC) && (j != NATIVE_SIZE_DYNAMIC)) {
        return i + j;
      }
      return NATIVE_SIZE_DYNAMIC;
    }
    
    public void marshal(Pair<T1, T2> paramPair, ByteBuffer paramByteBuffer)
    {
      if (paramPair.first == null) {
        throw new UnsupportedOperationException("Pair#first must not be null");
      }
      if (paramPair.second == null) {
        throw new UnsupportedOperationException("Pair#second must not be null");
      }
      this.mNestedTypeMarshalerFirst.marshal(paramPair.first, paramByteBuffer);
      this.mNestedTypeMarshalerSecond.marshal(paramPair.second, paramByteBuffer);
    }
    
    public Pair<T1, T2> unmarshal(ByteBuffer paramByteBuffer)
    {
      Object localObject = this.mNestedTypeMarshalerFirst.unmarshal(paramByteBuffer);
      paramByteBuffer = this.mNestedTypeMarshalerSecond.unmarshal(paramByteBuffer);
      try
      {
        paramByteBuffer = (Pair)this.mConstructor.newInstance(new Object[] { localObject, paramByteBuffer });
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryablePair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */