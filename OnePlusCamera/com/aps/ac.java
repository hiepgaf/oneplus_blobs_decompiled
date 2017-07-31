package com.aps;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

final class ac
  implements Serializable
{
  protected byte a = 0;
  protected ArrayList b = new ArrayList();
  private byte c = 3;
  
  protected final Boolean a(DataOutputStream paramDataOutputStream)
  {
    try
    {
      paramDataOutputStream.writeByte(this.c);
      paramDataOutputStream.writeByte(this.a);
      int i = 0;
      if (i >= this.b.size()) {
        return Boolean.valueOf(true);
      }
      ad localad = (ad)this.b.get(i);
      paramDataOutputStream.writeByte(localad.a);
      byte[] arrayOfByte1 = new byte[localad.a];
      byte[] arrayOfByte2 = localad.b;
      if (localad.a >= localad.b.length)
      {
        j = localad.b.length;
        label93:
        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, j);
        paramDataOutputStream.write(arrayOfByte1);
        paramDataOutputStream.writeDouble(localad.c);
        paramDataOutputStream.writeInt(localad.d);
        paramDataOutputStream.writeInt(localad.e);
        paramDataOutputStream.writeDouble(localad.f);
        paramDataOutputStream.writeByte(localad.g);
        paramDataOutputStream.writeByte(localad.h);
        arrayOfByte1 = new byte[localad.h];
        arrayOfByte2 = localad.i;
        if (localad.h < localad.i.length) {
          break label241;
        }
      }
      label241:
      for (int j = localad.i.length;; j = localad.h)
      {
        System.arraycopy(arrayOfByte2, 0, arrayOfByte1, 0, j);
        paramDataOutputStream.write(arrayOfByte1);
        paramDataOutputStream.writeByte(localad.j);
        i += 1;
        break;
        j = localad.a;
        break label93;
      }
      return Boolean.valueOf(false);
    }
    catch (IOException paramDataOutputStream) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ac.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */