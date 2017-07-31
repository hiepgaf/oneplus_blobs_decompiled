package com.aps;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

final class ae
  implements Serializable
{
  protected byte a = 0;
  protected ArrayList b = new ArrayList();
  private byte c = 8;
  
  protected final Boolean a(DataOutputStream paramDataOutputStream)
  {
    try
    {
      paramDataOutputStream.writeByte(this.c);
      paramDataOutputStream.writeByte(this.a);
      int i = 0;
      for (;;)
      {
        if (i >= this.a) {
          return Boolean.valueOf(true);
        }
        af localaf = (af)this.b.get(i);
        paramDataOutputStream.write(localaf.a);
        paramDataOutputStream.writeShort(localaf.b);
        paramDataOutputStream.write(ah.a(localaf.c, localaf.c.length));
        i += 1;
      }
      return Boolean.valueOf(false);
    }
    catch (IOException paramDataOutputStream) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ae.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */