package com.aps;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

final class ai
  implements Serializable
{
  protected int a = 0;
  protected int b = 0;
  protected short c = 0;
  protected short d = 0;
  protected int e = 0;
  protected byte f = 0;
  private byte g = 4;
  
  protected final Boolean a(DataOutputStream paramDataOutputStream)
  {
    try
    {
      paramDataOutputStream.writeByte(this.g);
      paramDataOutputStream.writeInt(this.a);
      paramDataOutputStream.writeInt(this.b);
      paramDataOutputStream.writeShort(this.c);
      paramDataOutputStream.writeShort(this.d);
      paramDataOutputStream.writeInt(this.e);
      paramDataOutputStream.writeByte(this.f);
      return Boolean.valueOf(true);
    }
    catch (IOException paramDataOutputStream) {}
    return Boolean.valueOf(false);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ai.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */