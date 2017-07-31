package android.hardware.camera2.marshal.impl;

import android.hardware.camera2.marshal.MarshalQueryable;
import android.hardware.camera2.marshal.Marshaler;
import android.hardware.camera2.utils.TypeReference;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class MarshalQueryableString
  implements MarshalQueryable<String>
{
  private static final boolean DEBUG = false;
  private static final byte NUL = 0;
  private static final String TAG = MarshalQueryableString.class.getSimpleName();
  private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
  
  public Marshaler<String> createMarshaler(TypeReference<String> paramTypeReference, int paramInt)
  {
    return new MarshalerString(paramTypeReference, paramInt);
  }
  
  public boolean isTypeMappingSupported(TypeReference<String> paramTypeReference, int paramInt)
  {
    boolean bool = false;
    if (paramInt == 0) {
      bool = String.class.equals(paramTypeReference.getType());
    }
    return bool;
  }
  
  private class MarshalerString
    extends Marshaler<String>
  {
    protected MarshalerString(int paramInt)
    {
      super(paramInt, i);
    }
    
    public int calculateMarshalSize(String paramString)
    {
      return paramString.getBytes(MarshalQueryableString.-get0()).length + 1;
    }
    
    public int getNativeSize()
    {
      return NATIVE_SIZE_DYNAMIC;
    }
    
    public void marshal(String paramString, ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.put(paramString.getBytes(MarshalQueryableString.-get0()));
      paramByteBuffer.put((byte)0);
    }
    
    public String unmarshal(ByteBuffer paramByteBuffer)
    {
      paramByteBuffer.mark();
      int k = 0;
      int i = 0;
      for (;;)
      {
        int j = k;
        if (paramByteBuffer.hasRemaining())
        {
          if (paramByteBuffer.get() == 0) {
            j = 1;
          }
        }
        else
        {
          if (j != 0) {
            break;
          }
          throw new UnsupportedOperationException("Strings must be null-terminated");
        }
        i += 1;
      }
      paramByteBuffer.reset();
      byte[] arrayOfByte = new byte[i + 1];
      paramByteBuffer.get(arrayOfByte, 0, i + 1);
      return new String(arrayOfByte, 0, i, MarshalQueryableString.-get0());
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/marshal/impl/MarshalQueryableString.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */