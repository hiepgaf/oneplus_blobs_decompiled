package com.oneplus.media;

public class JfifSegment
{
  public final byte[] data;
  public final int marker;
  
  public JfifSegment(int paramInt, byte[] paramArrayOfByte)
  {
    this.marker = paramInt;
    this.data = paramArrayOfByte;
  }
  
  public String toString()
  {
    int i = 0;
    StringBuilder localStringBuilder = new StringBuilder().append("[").append(super.toString()).append("] ").append(String.format("%x", new Object[] { Integer.valueOf(this.marker) })).append("(");
    if (this.data != null) {
      i = this.data.length;
    }
    return i + ")";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/media/JfifSegment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */