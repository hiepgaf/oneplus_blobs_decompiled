package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalHelpers;
import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.util.Rational;
import java.nio.ByteBuffer;

public final class MarshalQueryablePrimitive<T>
  implements MarshalQueryable<T>
{
  public Marshaler<T> createMarshaler(TypeReference<T> paramTypeReference, int paramInt)
  {
    return new MarshalerPrimitive(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<T> paramTypeReference, int paramInt)
  {
    if ((paramTypeReference.getType() instanceof Class))
    {
      paramTypeReference = (Class)paramTypeReference.getType();
      if ((paramTypeReference == Byte.TYPE) || (paramTypeReference == Byte.class)) {
        return paramInt == 0;
      }
      if ((paramTypeReference == Integer.TYPE) || (paramTypeReference == Integer.class)) {
        return paramInt == 1;
      }
      if ((paramTypeReference == Float.TYPE) || (paramTypeReference == Float.class)) {
        return paramInt == 2;
      }
      if ((paramTypeReference == Long.TYPE) || (paramTypeReference == Long.class)) {
        return paramInt == 3;
      }
      if ((paramTypeReference == Double.TYPE) || (paramTypeReference == Double.class)) {
        return paramInt == 4;
      }
      if (paramTypeReference == Rational.class) {
        return paramInt == 5;
      }
    }
    return false;
  }
  
  private class MarshalerPrimitive
    extends Marshaler<T>
  {
    private final Class<T> mClass;
    
    protected MarshalerPrimitive(int paramInt)
    {
      super(paramInt, i);
      this.mClass = MarshalHelpers.wrapClassIfPrimitive(paramInt.getRawType());
    }
    
    private void marshalPrimitive(byte paramByte, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.put(paramByte);
    }
    
    private void marshalPrimitive(double paramDouble, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.putDouble(paramDouble);
    }
    
    private void marshalPrimitive(float paramFloat, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.putFloat(paramFloat);
    }
    
    private void marshalPrimitive(int paramInt, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.putInt(paramInt);
    }
    
    private void marshalPrimitive(long paramLong, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.putLong(paramLong);
    }
    
    private void marshalPrimitive(Rational paramRational, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.putInt(paramRational.getNumerator());
      paramByteBuffer.putInt(paramRational.getDenominator());
    }
    
    private Object unmarshalObject(ByteBuffer paramByteBuffer)
    {
      switch (this.mNativeType)
      {
      default: 
        throw new UnsupportedOperationException("Can't unmarshal native type " + this.mNativeType);
      case 1: 
        return Integer.valueOf(paramByteBuffer.getInt());
      case 2: 
        return Float.valueOf(paramByteBuffer.getFloat());
      case 3: 
        return Long.valueOf(paramByteBuffer.getLong());
      case 5: 
        return new Rational(paramByteBuffer.getInt(), paramByteBuffer.getInt());
      case 4: 
        return Double.valueOf(paramByteBuffer.getDouble());
      }
      return Byte.valueOf(paramByteBuffer.get());
    }
    
    public int calculateMarshalSize(T paramT)
    {
      return MarshalHelpers.getPrimitiveTypeSize(this.mNativeType);
    }
    
    public int getNativeSize()
    {
      return MarshalHelpers.getPrimitiveTypeSize(this.mNativeType);
    }
    
    public void marshal(T paramT, ByteBuffer paramByteBuffer)
    {
      if ((paramT instanceof Integer))
      {
        MarshalHelpers.checkNativeTypeEquals(1, this.mNativeType);
        marshalPrimitive(((Integer)paramT).intValue(), paramByteBuffer);
        return;
      }
      if ((paramT instanceof Float))
      {
        MarshalHelpers.checkNativeTypeEquals(2, this.mNativeType);
        marshalPrimitive(((Float)paramT).floatValue(), paramByteBuffer);
        return;
      }
      if ((paramT instanceof Long))
      {
        MarshalHelpers.checkNativeTypeEquals(3, this.mNativeType);
        marshalPrimitive(((Long)paramT).longValue(), paramByteBuffer);
        return;
      }
      if ((paramT instanceof Rational))
      {
        MarshalHelpers.checkNativeTypeEquals(5, this.mNativeType);
        marshalPrimitive((Rational)paramT, paramByteBuffer);
        return;
      }
      if ((paramT instanceof Double))
      {
        MarshalHelpers.checkNativeTypeEquals(4, this.mNativeType);
        marshalPrimitive(((Double)paramT).doubleValue(), paramByteBuffer);
        return;
      }
      if ((paramT instanceof Byte))
      {
        MarshalHelpers.checkNativeTypeEquals(0, this.mNativeType);
        marshalPrimitive(((Byte)paramT).byteValue(), paramByteBuffer);
        return;
      }
      throw new UnsupportedOperationException("Can't marshal managed type " + this.mTypeReference);
    }
    
    public T unmarshal(ByteBuffer paramByteBuffer)
    {
      return (T)this.mClass.cast(unmarshalObject(paramByteBuffer));
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryablePrimitive.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */