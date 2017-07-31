package android.net.wifi.nan;

import java.nio.BufferOverflowException;
import java.nio.ByteOrder;
import java.util.Iterator;
import libcore.io.Memory;

public class TlvBufferUtils
{
  public static class TlvConstructor
  {
    private byte[] mArray;
    private int mArrayLength;
    private int mLengthSize;
    private int mPosition;
    private int mTypeSize;
    
    public TlvConstructor(int paramInt1, int paramInt2)
    {
      if ((paramInt1 < 0) || (paramInt1 > 2)) {}
      while ((paramInt2 <= 0) || (paramInt2 > 2)) {
        throw new IllegalArgumentException("Invalid sizes - typeSize=" + paramInt1 + ", lengthSize=" + paramInt2);
      }
      this.mTypeSize = paramInt1;
      this.mLengthSize = paramInt2;
    }
    
    private void addHeader(int paramInt1, int paramInt2)
    {
      if (this.mTypeSize == 1)
      {
        this.mArray[this.mPosition] = ((byte)paramInt1);
        this.mPosition += this.mTypeSize;
        if (this.mLengthSize != 1) {
          break label92;
        }
        this.mArray[this.mPosition] = ((byte)paramInt2);
      }
      for (;;)
      {
        this.mPosition += this.mLengthSize;
        return;
        if (this.mTypeSize != 2) {
          break;
        }
        Memory.pokeShort(this.mArray, this.mPosition, (short)paramInt1, ByteOrder.BIG_ENDIAN);
        break;
        label92:
        if (this.mLengthSize == 2) {
          Memory.pokeShort(this.mArray, this.mPosition, (short)paramInt2, ByteOrder.BIG_ENDIAN);
        }
      }
    }
    
    private void checkLength(int paramInt)
    {
      if (this.mPosition + this.mTypeSize + this.mLengthSize + paramInt > this.mArrayLength) {
        throw new BufferOverflowException();
      }
    }
    
    public TlvConstructor allocate(int paramInt)
    {
      this.mArray = new byte[paramInt];
      this.mArrayLength = paramInt;
      return this;
    }
    
    public int getActualLength()
    {
      return this.mPosition;
    }
    
    public byte[] getArray()
    {
      return this.mArray;
    }
    
    public TlvConstructor putByte(int paramInt, byte paramByte)
    {
      checkLength(1);
      addHeader(paramInt, 1);
      byte[] arrayOfByte = this.mArray;
      paramInt = this.mPosition;
      this.mPosition = (paramInt + 1);
      arrayOfByte[paramInt] = paramByte;
      return this;
    }
    
    public TlvConstructor putByteArray(int paramInt, byte[] paramArrayOfByte)
    {
      return putByteArray(paramInt, paramArrayOfByte, 0, paramArrayOfByte.length);
    }
    
    public TlvConstructor putByteArray(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3)
    {
      checkLength(paramInt3);
      addHeader(paramInt1, paramInt3);
      System.arraycopy(paramArrayOfByte, paramInt2, this.mArray, this.mPosition, paramInt3);
      this.mPosition += paramInt3;
      return this;
    }
    
    public TlvConstructor putInt(int paramInt1, int paramInt2)
    {
      checkLength(4);
      addHeader(paramInt1, 4);
      Memory.pokeInt(this.mArray, this.mPosition, paramInt2, ByteOrder.BIG_ENDIAN);
      this.mPosition += 4;
      return this;
    }
    
    public TlvConstructor putShort(int paramInt, short paramShort)
    {
      checkLength(2);
      addHeader(paramInt, 2);
      Memory.pokeShort(this.mArray, this.mPosition, paramShort, ByteOrder.BIG_ENDIAN);
      this.mPosition += 2;
      return this;
    }
    
    public TlvConstructor putString(int paramInt, String paramString)
    {
      return putByteArray(paramInt, paramString.getBytes(), 0, paramString.length());
    }
    
    public TlvConstructor putZeroLengthElement(int paramInt)
    {
      checkLength(0);
      addHeader(paramInt, 0);
      return this;
    }
    
    public TlvConstructor wrap(byte[] paramArrayOfByte)
    {
      this.mArray = paramArrayOfByte;
      this.mArrayLength = paramArrayOfByte.length;
      return this;
    }
  }
  
  public static class TlvElement
  {
    public int mLength;
    public int mOffset;
    public byte[] mRefArray;
    public int mType;
    
    private TlvElement(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      this.mType = paramInt1;
      this.mLength = paramInt2;
      this.mRefArray = paramArrayOfByte;
      this.mOffset = paramInt3;
    }
    
    public byte getByte()
    {
      if (this.mLength != 1) {
        throw new IllegalArgumentException("Accesing a byte from a TLV element of length " + this.mLength);
      }
      return this.mRefArray[this.mOffset];
    }
    
    public int getInt()
    {
      if (this.mLength != 4) {
        throw new IllegalArgumentException("Accesing an int from a TLV element of length " + this.mLength);
      }
      return Memory.peekInt(this.mRefArray, this.mOffset, ByteOrder.BIG_ENDIAN);
    }
    
    public short getShort()
    {
      if (this.mLength != 2) {
        throw new IllegalArgumentException("Accesing a short from a TLV element of length " + this.mLength);
      }
      return Memory.peekShort(this.mRefArray, this.mOffset, ByteOrder.BIG_ENDIAN);
    }
    
    public String getString()
    {
      return new String(this.mRefArray, this.mOffset, this.mLength);
    }
  }
  
  public static class TlvIterable
    implements Iterable<TlvBufferUtils.TlvElement>
  {
    private byte[] mArray;
    private int mArrayLength;
    private int mLengthSize;
    private int mTypeSize;
    
    public TlvIterable(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
      if ((paramInt1 < 0) || (paramInt1 > 2)) {}
      while ((paramInt2 <= 0) || (paramInt2 > 2)) {
        throw new IllegalArgumentException("Invalid sizes - typeSize=" + paramInt1 + ", lengthSize=" + paramInt2);
      }
      this.mTypeSize = paramInt1;
      this.mLengthSize = paramInt2;
      this.mArray = paramArrayOfByte;
      this.mArrayLength = paramInt3;
    }
    
    public Iterator<TlvBufferUtils.TlvElement> iterator()
    {
      new Iterator()
      {
        private int mOffset = 0;
        
        public boolean hasNext()
        {
          return this.mOffset < TlvBufferUtils.TlvIterable.-get1(TlvBufferUtils.TlvIterable.this);
        }
        
        public TlvBufferUtils.TlvElement next()
        {
          int i = 0;
          int j;
          if (TlvBufferUtils.TlvIterable.-get3(TlvBufferUtils.TlvIterable.this) == 1)
          {
            i = TlvBufferUtils.TlvIterable.-get0(TlvBufferUtils.TlvIterable.this)[this.mOffset];
            this.mOffset += TlvBufferUtils.TlvIterable.-get3(TlvBufferUtils.TlvIterable.this);
            j = 0;
            if (TlvBufferUtils.TlvIterable.-get2(TlvBufferUtils.TlvIterable.this) != 1) {
              break label150;
            }
            j = TlvBufferUtils.TlvIterable.-get0(TlvBufferUtils.TlvIterable.this)[this.mOffset];
          }
          for (;;)
          {
            this.mOffset += TlvBufferUtils.TlvIterable.-get2(TlvBufferUtils.TlvIterable.this);
            TlvBufferUtils.TlvElement localTlvElement = new TlvBufferUtils.TlvElement(i, j, TlvBufferUtils.TlvIterable.-get0(TlvBufferUtils.TlvIterable.this), this.mOffset, null);
            this.mOffset += j;
            return localTlvElement;
            if (TlvBufferUtils.TlvIterable.-get3(TlvBufferUtils.TlvIterable.this) != 2) {
              break;
            }
            i = Memory.peekShort(TlvBufferUtils.TlvIterable.-get0(TlvBufferUtils.TlvIterable.this), this.mOffset, ByteOrder.BIG_ENDIAN);
            break;
            label150:
            if (TlvBufferUtils.TlvIterable.-get2(TlvBufferUtils.TlvIterable.this) == 2) {
              j = Memory.peekShort(TlvBufferUtils.TlvIterable.-get0(TlvBufferUtils.TlvIterable.this), this.mOffset, ByteOrder.BIG_ENDIAN);
            }
          }
        }
        
        public void remove()
        {
          throw new UnsupportedOperationException();
        }
      };
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[");
      int i = 1;
      Iterator localIterator = iterator();
      if (localIterator.hasNext())
      {
        TlvBufferUtils.TlvElement localTlvElement = (TlvBufferUtils.TlvElement)localIterator.next();
        if (i == 0) {
          localStringBuilder.append(",");
        }
        int j = 0;
        localStringBuilder.append(" (");
        if (this.mTypeSize != 0) {
          localStringBuilder.append("T=").append(localTlvElement.mType).append(",");
        }
        localStringBuilder.append("L=").append(localTlvElement.mLength).append(") ");
        if (localTlvElement.mLength == 0) {
          localStringBuilder.append("<null>");
        }
        for (;;)
        {
          i = j;
          if (localTlvElement.mLength == 0) {
            break;
          }
          localStringBuilder.append(" (S='").append(localTlvElement.getString()).append("')");
          i = j;
          break;
          if (localTlvElement.mLength == 1) {
            localStringBuilder.append(localTlvElement.getByte());
          } else if (localTlvElement.mLength == 2) {
            localStringBuilder.append(localTlvElement.getShort());
          } else if (localTlvElement.mLength == 4) {
            localStringBuilder.append(localTlvElement.getInt());
          } else {
            localStringBuilder.append("<bytes>");
          }
        }
      }
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/net/wifi/nan/TlvBufferUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */