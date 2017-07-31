package com.aps;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.BitSet;

public final class bf
{
  private RandomAccessFile a;
  private ah b;
  private String c = "";
  private File d = null;
  
  protected bf(ah paramah)
  {
    this.b = paramah;
  }
  
  protected final void a(long paramLong, byte[] paramArrayOfByte)
  {
    for (;;)
    {
      try
      {
        this.d = this.b.a(paramLong);
        Object localObject = this.d;
        if (localObject != null) {}
        try
        {
          this.a = new RandomAccessFile(this.d, "rw");
          localObject = new byte[this.b.a()];
          int j;
          if (this.a.read((byte[])localObject) != -1)
          {
            i = this.a.readInt();
            localObject = ah.b((byte[])localObject);
            j = this.b.a();
            if (i < 0)
            {
              this.a.close();
              this.d.delete();
              paramArrayOfByte = this.a;
              if (paramArrayOfByte != null) {
                continue;
              }
              return;
            }
          }
          else
          {
            i = 0;
            continue;
          }
          if (i > this.b.a() << 3) {
            continue;
          }
          this.a.seek(j + 4 + i * 1500);
          paramArrayOfByte = ah.a(paramArrayOfByte);
          this.a.writeInt(paramArrayOfByte.length);
          this.a.writeLong(paramLong);
          this.a.write(paramArrayOfByte);
          ((BitSet)localObject).set(i, true);
          this.a.seek(0L);
          this.a.write(ah.a((BitSet)localObject));
          i += 1;
          if (i == this.b.a() << 3) {
            continue;
          }
          this.a.writeInt(i);
          if (!this.c.equalsIgnoreCase(this.d.getName())) {
            continue;
          }
          this.d.length();
        }
        catch (FileNotFoundException paramArrayOfByte)
        {
          int i;
          paramArrayOfByte = this.a;
          if (paramArrayOfByte == null) {
            continue;
          }
          try
          {
            this.a.close();
          }
          catch (IOException paramArrayOfByte) {}
          continue;
          try
          {
            this.a.close();
          }
          catch (IOException paramArrayOfByte) {}
          continue;
        }
        catch (IOException paramArrayOfByte)
        {
          paramArrayOfByte = this.a;
          if (paramArrayOfByte == null) {
            continue;
          }
          try
          {
            this.a.close();
          }
          catch (IOException paramArrayOfByte) {}
          continue;
        }
        finally
        {
          if (this.a != null) {
            break label399;
          }
        }
        this.d = null;
        return;
      }
      finally {}
      try
      {
        this.a.close();
        return;
      }
      catch (IOException paramArrayOfByte) {}
      i = 0;
      continue;
      this.c = this.d.getName();
      continue;
      throw paramArrayOfByte;
      try
      {
        label399:
        this.a.close();
      }
      catch (IOException localIOException) {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/bf.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */