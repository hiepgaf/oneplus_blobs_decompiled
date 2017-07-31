package com.amap.api.mapcore2d;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class dw
  implements Closeable
{
  private final InputStream a;
  private final Charset b;
  private byte[] c;
  private int d;
  private int e;
  
  public dw(InputStream paramInputStream, int paramInt, Charset paramCharset)
  {
    if (paramInputStream == null) {}
    while (paramCharset == null) {
      throw new NullPointerException();
    }
    if (paramInt >= 0)
    {
      if (paramCharset.equals(dx.a))
      {
        this.a = paramInputStream;
        this.b = paramCharset;
        this.c = new byte[paramInt];
      }
    }
    else {
      throw new IllegalArgumentException("capacity <= 0");
    }
    throw new IllegalArgumentException("Unsupported encoding");
  }
  
  public dw(InputStream paramInputStream, Charset paramCharset)
  {
    this(paramInputStream, 8192, paramCharset);
  }
  
  private void b()
    throws IOException
  {
    int i = this.a.read(this.c, 0, this.c.length);
    if (i != -1)
    {
      this.d = 0;
      this.e = i;
      return;
    }
    throw new EOFException();
  }
  
  public String a()
    throws IOException
  {
    int i;
    for (;;)
    {
      synchronized (this.a)
      {
        if (this.c != null)
        {
          if (this.d < this.e)
          {
            i = this.d;
            if (i != this.e) {
              break label146;
            }
            ByteArrayOutputStream local1 = new ByteArrayOutputStream(this.e - this.d + 80)
            {
              public String toString()
              {
                if (this.count <= 0) {}
                for (int i = this.count;; i = this.count - 1)
                {
                  try
                  {
                    String str = new String(this.buf, 0, i, dw.a(dw.this).name());
                    return str;
                  }
                  catch (UnsupportedEncodingException localUnsupportedEncodingException)
                  {
                    throw new AssertionError(localUnsupportedEncodingException);
                  }
                  if (this.buf[(this.count - 1)] != 13) {
                    break;
                  }
                }
              }
            };
            local1.write(this.c, this.d, this.e - this.d);
            this.e = -1;
            b();
            i = this.d;
            if (i != this.e)
            {
              if (this.c[i] == 10) {
                break label237;
              }
              i += 1;
              continue;
            }
            continue;
          }
        }
        else {
          throw new IOException("LineReader is closed");
        }
      }
      b();
      continue;
      label146:
      if (this.c[i] == 10) {
        break;
      }
      i += 1;
    }
    if (i == this.d) {
      break label286;
    }
    for (;;)
    {
      int j;
      String str = new String(this.c, this.d, j - this.d, this.b.name());
      this.d = (i + 1);
      return str;
      if (this.c[(i - 1)] == 13)
      {
        j = i - 1;
        continue;
        label237:
        if (i == this.d) {}
        for (;;)
        {
          this.d = (i + 1);
          str = str.toString();
          return str;
          str.write(this.c, this.d, i - this.d);
        }
      }
      else
      {
        label286:
        j = i;
      }
    }
  }
  
  public void close()
    throws IOException
  {
    synchronized (this.a)
    {
      if (this.c == null) {
        return;
      }
      this.c = null;
      this.a.close();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/dw.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */