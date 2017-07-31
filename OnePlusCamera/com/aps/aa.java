package com.aps;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

final class aa
  implements Serializable
{
  protected byte[] a = new byte[16];
  protected byte[] b = new byte[16];
  protected byte[] c = new byte[16];
  protected short d = 0;
  protected short e = 0;
  protected byte f = 0;
  protected byte[] g = new byte[16];
  protected byte[] h = new byte[32];
  protected short i = 0;
  protected ArrayList j = new ArrayList();
  private byte k = 41;
  private short l = 0;
  
  private Boolean a(DataOutputStream paramDataOutputStream)
  {
    for (;;)
    {
      DataOutputStream localDataOutputStream2;
      x localx;
      try
      {
        ByteArrayOutputStream localByteArrayOutputStream1 = new ByteArrayOutputStream();
        DataOutputStream localDataOutputStream1 = new DataOutputStream(localByteArrayOutputStream1);
        localDataOutputStream1.flush();
        localDataOutputStream1.write(this.a);
        localDataOutputStream1.write(this.b);
        localDataOutputStream1.write(this.c);
        localDataOutputStream1.writeShort(this.d);
        localDataOutputStream1.writeShort(this.e);
        localDataOutputStream1.writeByte(this.f);
        this.g[15] = 0;
        localDataOutputStream1.write(ah.a(this.g, this.g.length));
        this.h[31] = 0;
        localDataOutputStream1.write(ah.a(this.h, this.h.length));
        localDataOutputStream1.writeShort(this.i);
        int m = 0;
        if (m >= this.i)
        {
          this.l = Integer.valueOf(localByteArrayOutputStream1.size()).shortValue();
          paramDataOutputStream.writeByte(this.k);
          paramDataOutputStream.writeShort(this.l);
          paramDataOutputStream.write(localByteArrayOutputStream1.toByteArray());
          return Boolean.valueOf(true);
        }
        ByteArrayOutputStream localByteArrayOutputStream2 = new ByteArrayOutputStream();
        localDataOutputStream2 = new DataOutputStream(localByteArrayOutputStream2);
        localDataOutputStream2.flush();
        localx = (x)this.j.get(m);
        if (localx.c == null)
        {
          if (localx.d == null)
          {
            if (localx.e != null) {
              break label367;
            }
            if (localx.f != null) {
              break label386;
            }
            if (localx.g != null) {
              break label405;
            }
            localx.a = Integer.valueOf(localByteArrayOutputStream2.size() + 4).shortValue();
            localDataOutputStream1.writeShort(localx.a);
            localDataOutputStream1.writeInt(localx.b);
            localDataOutputStream1.write(localByteArrayOutputStream2.toByteArray());
            m = (short)(m + 1);
          }
        }
        else
        {
          if (localx.c.a(localDataOutputStream2).booleanValue()) {
            continue;
          }
          continue;
        }
        if (localx.d.a(localDataOutputStream2).booleanValue()) {
          continue;
        }
      }
      catch (IOException paramDataOutputStream)
      {
        return Boolean.valueOf(false);
      }
      continue;
      label367:
      if (!localx.e.a(localDataOutputStream2).booleanValue())
      {
        continue;
        label386:
        if (!localx.f.a(localDataOutputStream2).booleanValue())
        {
          continue;
          label405:
          if (localx.g.a(localDataOutputStream2).booleanValue()) {}
        }
      }
    }
  }
  
  protected final byte[] a()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    a(new DataOutputStream(localByteArrayOutputStream));
    return localByteArrayOutputStream.toByteArray();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/aa.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */