package com.aps;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

final class v
  implements Serializable
{
  protected short a = 0;
  protected int b = 0;
  protected byte c = 0;
  protected byte d = 0;
  protected ArrayList e = new ArrayList();
  private byte f = 2;
  
  protected final Boolean a(DataOutputStream paramDataOutputStream)
  {
    try
    {
      paramDataOutputStream.writeByte(this.f);
      paramDataOutputStream.writeShort(this.a);
      paramDataOutputStream.writeInt(this.b);
      paramDataOutputStream.writeByte(this.c);
      paramDataOutputStream.writeByte(this.d);
      int i = 0;
      for (;;)
      {
        if (i >= this.d) {
          return Boolean.valueOf(true);
        }
        paramDataOutputStream.writeShort(((aj)this.e.get(i)).a);
        paramDataOutputStream.writeInt(((aj)this.e.get(i)).b);
        paramDataOutputStream.writeByte(((aj)this.e.get(i)).c);
        i += 1;
      }
      return Boolean.valueOf(false);
    }
    catch (IOException paramDataOutputStream) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/v.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */