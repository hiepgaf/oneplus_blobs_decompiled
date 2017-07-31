package com.aps;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class ag
{
  protected File a;
  protected int[] b;
  private ArrayList c;
  private boolean d = false;
  
  protected ag(File paramFile, ArrayList paramArrayList, int[] paramArrayOfInt)
  {
    this.a = paramFile;
    this.c = paramArrayList;
    this.b = paramArrayOfInt;
  }
  
  protected final void a(boolean paramBoolean)
  {
    this.d = paramBoolean;
  }
  
  public byte[] a()
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream localDataOutputStream = new DataOutputStream(localByteArrayOutputStream);
    Iterator localIterator = this.c.iterator();
    for (;;)
    {
      if (!localIterator.hasNext()) {}
      try
      {
        localByteArrayOutputStream.close();
        localDataOutputStream.close();
        return localByteArrayOutputStream.toByteArray();
        byte[] arrayOfByte = (byte[])localIterator.next();
        try
        {
          localDataOutputStream.writeInt(arrayOfByte.length);
          localDataOutputStream.write(arrayOfByte);
        }
        catch (IOException localIOException2) {}
      }
      catch (IOException localIOException1)
      {
        for (;;) {}
      }
    }
  }
  
  protected final boolean b()
  {
    return this.d;
  }
  
  protected final int c()
  {
    int i;
    int j;
    if (this.c != null)
    {
      i = 0;
      j = 0;
      if (i >= this.c.size()) {
        return j;
      }
    }
    else
    {
      return 0;
    }
    if (this.c.get(i) == null) {}
    for (int k = 0;; k = ((byte[])this.c.get(i)).length)
    {
      j += k;
      i += 1;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ag.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */