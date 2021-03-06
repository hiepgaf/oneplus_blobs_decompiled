package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class MarshalQueryableParcelable<T extends Parcelable>
  implements MarshalQueryable<T>
{
  private static final boolean DEBUG = false;
  private static final String FIELD_CREATOR = "CREATOR";
  private static final String TAG = "MarshalParcelable";
  
  public Marshaler<T> createMarshaler(TypeReference<T> paramTypeReference, int paramInt)
  {
    return new MarshalerParcelable(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<T> paramTypeReference, int paramInt)
  {
    return Parcelable.class.isAssignableFrom(paramTypeReference.getRawType());
  }
  
  private class MarshalerParcelable
    extends Marshaler<T>
  {
    private final Class<T> mClass;
    private final Parcelable.Creator<T> mCreator;
    
    /* Error */
    protected MarshalerParcelable(int paramInt)
    {
      // Byte code:
      //   0: aload_0
      //   1: aload_1
      //   2: putfield 26	android/hardware/camera2/marshal/impl/MarshalQueryableParcelable$MarshalerParcelable:this$0	Landroid/hardware/camera2/marshal/impl/MarshalQueryableParcelable;
      //   5: aload_0
      //   6: aload_1
      //   7: aload_2
      //   8: iload_3
      //   9: invokespecial 29	android/hardware/camera2/marshal/Marshaler:<init>	(Landroid/hardware/camera2/marshal/MarshalQueryable;Landroid/hardware/camera2/utils/TypeReference;I)V
      //   12: aload_0
      //   13: aload_2
      //   14: invokevirtual 35	android/hardware/camera2/utils/TypeReference:getRawType	()Ljava/lang/Class;
      //   17: putfield 37	android/hardware/camera2/marshal/impl/MarshalQueryableParcelable$MarshalerParcelable:mClass	Ljava/lang/Class;
      //   20: aload_0
      //   21: getfield 37	android/hardware/camera2/marshal/impl/MarshalQueryableParcelable$MarshalerParcelable:mClass	Ljava/lang/Class;
      //   24: ldc 39
      //   26: invokevirtual 45	java/lang/Class:getDeclaredField	(Ljava/lang/String;)Ljava/lang/reflect/Field;
      //   29: astore_1
      //   30: aload_0
      //   31: aload_1
      //   32: aconst_null
      //   33: invokevirtual 51	java/lang/reflect/Field:get	(Ljava/lang/Object;)Ljava/lang/Object;
      //   36: checkcast 53	android/os/Parcelable$Creator
      //   39: putfield 55	android/hardware/camera2/marshal/impl/MarshalQueryableParcelable$MarshalerParcelable:mCreator	Landroid/os/Parcelable$Creator;
      //   42: return
      //   43: astore_1
      //   44: new 57	java/lang/AssertionError
      //   47: dup
      //   48: aload_1
      //   49: invokespecial 60	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
      //   52: athrow
      //   53: astore_1
      //   54: new 57	java/lang/AssertionError
      //   57: dup
      //   58: aload_1
      //   59: invokespecial 60	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
      //   62: athrow
      //   63: astore_1
      //   64: new 57	java/lang/AssertionError
      //   67: dup
      //   68: aload_1
      //   69: invokespecial 60	java/lang/AssertionError:<init>	(Ljava/lang/Object;)V
      //   72: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	73	0	this	MarshalerParcelable
      //   0	73	1	this$1	MarshalQueryableParcelable
      //   0	73	2	paramInt	int
      //   8	1	3	i	int
      // Exception table:
      //   from	to	target	type
      //   20	30	43	java/lang/NoSuchFieldException
      //   30	42	53	java/lang/IllegalArgumentException
      //   30	42	63	java/lang/IllegalAccessException
    }
    
    public int calculateMarshalSize(T paramT)
    {
      Parcel localParcel = Parcel.obtain();
      try
      {
        paramT.writeToParcel(localParcel, 0);
        int i = localParcel.marshall().length;
        return i;
      }
      finally
      {
        localParcel.recycle();
      }
    }
    
    public int getNativeSize()
    {
      return NATIVE_SIZE_DYNAMIC;
    }
    
    public void marshal(T paramT, ByteBuffer paramByteBuffer)
    {
      Parcel localParcel = Parcel.obtain();
      try
      {
        paramT.writeToParcel(localParcel, 0);
        if (localParcel.hasFileDescriptors()) {
          throw new UnsupportedOperationException("Parcelable " + paramT + " must not have file descriptors");
        }
      }
      finally
      {
        localParcel.recycle();
      }
      byte[] arrayOfByte = localParcel.marshall();
      localParcel.recycle();
      if (arrayOfByte.length == 0) {
        throw new AssertionError("No data marshaled for " + paramT);
      }
      paramByteBuffer.put(arrayOfByte);
    }
    
    public T unmarshal(ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.mark();
      Parcel localParcel = Parcel.obtain();
      int i;
      Object localObject;
      try
      {
        i = paramByteBuffer.remaining();
        localObject = new byte[i];
        paramByteBuffer.get((byte[])localObject);
        localParcel.unmarshall((byte[])localObject, 0, i);
        localParcel.setDataPosition(0);
        localObject = (Parcelable)this.mCreator.createFromParcel(localParcel);
        i = localParcel.dataPosition();
        if (i == 0) {
          throw new AssertionError("No data marshaled for " + localObject);
        }
      }
      finally
      {
        localParcel.recycle();
      }
      paramByteBuffer.reset();
      paramByteBuffer.position(paramByteBuffer.position() + i);
      paramByteBuffer = (Parcelable)this.mClass.cast(localObject);
      localParcel.recycle();
      return paramByteBuffer;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableParcelable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */