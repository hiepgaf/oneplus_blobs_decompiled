package com.aps;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;

final class z
  implements Serializable
{
  protected int a = 0;
  protected int b = 0;
  protected int c = 0;
  protected int d = 0;
  protected int e = 0;
  protected short f = 0;
  protected byte g = 0;
  protected byte h = 0;
  protected long i = 0L;
  protected long j = 0L;
  private byte k = 1;
  
  protected final Boolean a(DataOutputStream paramDataOutputStream)
  {
    Boolean localBoolean = Boolean.valueOf(false);
    if (paramDataOutputStream != null) {}
    try
    {
      paramDataOutputStream.writeByte(this.k);
      paramDataOutputStream.writeInt(this.a);
      paramDataOutputStream.writeInt(this.b);
      paramDataOutputStream.writeInt(this.c);
      paramDataOutputStream.writeInt(this.d);
      paramDataOutputStream.writeInt(this.e);
      paramDataOutputStream.writeShort(this.f);
      paramDataOutputStream.writeByte(this.g);
      paramDataOutputStream.writeByte(this.h);
      paramDataOutputStream.writeLong(this.i);
      paramDataOutputStream.writeLong(this.j);
      return Boolean.valueOf(true);
    }
    catch (IOException paramDataOutputStream) {}
    return localBoolean;
    return localBoolean;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/z.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */