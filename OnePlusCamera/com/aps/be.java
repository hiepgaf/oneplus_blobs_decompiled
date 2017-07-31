package com.aps;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.zip.GZIPInputStream;

public final class be
{
  private RandomAccessFile a;
  private ah b;
  private File c = null;
  
  protected be(ah paramah)
  {
    this.b = paramah;
  }
  
  private static byte a(byte[] paramArrayOfByte)
  {
    Object localObject2 = null;
    localObject1 = localObject2;
    for (;;)
    {
      try
      {
        ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
        localObject1 = localObject2;
        GZIPInputStream localGZIPInputStream = new GZIPInputStream(localByteArrayInputStream);
        localObject1 = localObject2;
        paramArrayOfByte = new byte['Ѐ'];
        localObject1 = localObject2;
        localByteArrayOutputStream = new ByteArrayOutputStream();
        localObject1 = localObject2;
        i = localGZIPInputStream.read(paramArrayOfByte, 0, paramArrayOfByte.length);
        if (i != -1) {
          continue;
        }
        localObject1 = localObject2;
        paramArrayOfByte = localByteArrayOutputStream.toByteArray();
        localObject1 = paramArrayOfByte;
        localByteArrayOutputStream.flush();
        localObject1 = paramArrayOfByte;
        localByteArrayOutputStream.close();
        localObject1 = paramArrayOfByte;
        localGZIPInputStream.close();
        localObject1 = paramArrayOfByte;
        localByteArrayInputStream.close();
      }
      catch (Exception paramArrayOfByte)
      {
        ByteArrayOutputStream localByteArrayOutputStream;
        int i;
        paramArrayOfByte = (byte[])localObject1;
        continue;
      }
      return paramArrayOfByte[0];
      localObject1 = localObject2;
      localByteArrayOutputStream.write(paramArrayOfByte, 0, i);
    }
  }
  
  private static int a(int paramInt1, int paramInt2, int paramInt3)
  {
    paramInt1 = (paramInt3 - 1) * 1500 + paramInt1;
    for (;;)
    {
      if (paramInt1 < paramInt2) {
        return paramInt1;
      }
      paramInt1 -= 1500;
    }
  }
  
  private int a(BitSet paramBitSet)
  {
    int i = 0;
    for (;;)
    {
      if (i >= paramBitSet.length()) {
        return 0;
      }
      if (paramBitSet.get(i)) {
        break;
      }
      i += 1;
    }
    return this.b.a() + (i * 1500 + 4);
  }
  
  private ArrayList a(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = new ArrayList();
    if (paramInt1 > paramInt2) {
      return localArrayList;
    }
    do
    {
      try
      {
        this.a.seek(paramInt1);
        i = this.a.readInt();
        this.a.readLong();
        if (i <= 0) {
          return null;
        }
        if (i > 1500) {
          break label116;
        }
        byte[] arrayOfByte = new byte[i];
        this.a.read(arrayOfByte);
        i = a(arrayOfByte);
        if (i != 3) {
          continue;
        }
        localArrayList.add(arrayOfByte);
      }
      catch (IOException localIOException)
      {
        int i;
        for (;;) {}
      }
      paramInt1 += 1500;
      break;
    } while ((i == 4) || (i == 41));
    return null;
    label116:
    return null;
  }
  
  private BitSet b()
  {
    Object localObject = new byte[this.b.a()];
    try
    {
      this.a.read((byte[])localObject);
      localObject = ah.b((byte[])localObject);
      return (BitSet)localObject;
    }
    catch (IOException localIOException) {}
    return null;
  }
  
  protected final int a()
  {
    int i = 0;
    int i1 = 0;
    int i2 = 0;
    int i3 = 0;
    int n = 0;
    int j;
    int k;
    int m;
    for (;;)
    {
      try
      {
        this.c = this.b.b();
        j = i1;
        k = i2;
        m = i3;
        try
        {
          localObject1 = this.c;
          if (localObject1 != null) {
            continue;
          }
          if (this.a != null) {
            continue;
          }
          i = n;
        }
        catch (FileNotFoundException localFileNotFoundException)
        {
          Object localObject1;
          boolean bool;
          localRandomAccessFile1 = this.a;
          i = j;
          if (localRandomAccessFile1 == null) {
            continue;
          }
          try
          {
            this.a.close();
            i = j;
          }
          catch (IOException localIOException2)
          {
            i = j;
          }
          continue;
        }
        catch (IOException localIOException3)
        {
          localRandomAccessFile2 = this.a;
          i = k;
          if (localRandomAccessFile2 == null) {
            continue;
          }
          try
          {
            this.a.close();
            i = k;
          }
          catch (IOException localIOException4)
          {
            i = k;
          }
          continue;
        }
        catch (NullPointerException localNullPointerException)
        {
          localRandomAccessFile3 = this.a;
          i = m;
          if (localRandomAccessFile3 == null) {
            continue;
          }
          try
          {
            this.a.close();
            i = m;
          }
          catch (IOException localIOException5)
          {
            i = m;
          }
          continue;
        }
        finally
        {
          if (this.a != null) {
            break;
          }
        }
        this.c = null;
        return i;
      }
      finally {}
      j = i1;
      k = i2;
      m = i3;
      this.a = new RandomAccessFile(this.b.b(), "rw");
      j = i1;
      k = i2;
      m = i3;
      localObject1 = new byte[this.b.a()];
      j = i1;
      k = i2;
      m = i3;
      this.a.read((byte[])localObject1);
      j = i1;
      k = i2;
      m = i3;
      localObject1 = ah.b((byte[])localObject1);
      i1 = 0;
      n = i;
      j = i;
      k = i;
      m = i;
      if (i1 < ((BitSet)localObject1).size())
      {
        j = i;
        k = i;
        m = i;
        bool = ((BitSet)localObject1).get(i1);
        if (!bool)
        {
          i1 += 1;
        }
        else
        {
          i += 1;
          continue;
          try
          {
            this.a.close();
            i = n;
          }
          catch (IOException localIOException1)
          {
            i = n;
          }
        }
      }
    }
    for (;;)
    {
      RandomAccessFile localRandomAccessFile1;
      RandomAccessFile localRandomAccessFile2;
      RandomAccessFile localRandomAccessFile3;
      throw ((Throwable)localObject2);
      try
      {
        this.a.close();
      }
      catch (IOException localIOException6) {}
    }
  }
  
  protected final ag a(int paramInt)
  {
    for (;;)
    {
      try
      {
        if (this.b != null) {}
        try
        {
          this.c = this.b.b();
          Object localObject1 = this.c;
          if (localObject1 != null) {}
          try
          {
            this.a = new RandomAccessFile(this.c, "rw");
            localObject1 = b();
            if (localObject1 != null)
            {
              int i = a((BitSet)localObject1);
              paramInt = a(i, (int)this.c.length(), paramInt);
              localObject1 = a(i, paramInt);
              if (localObject1 != null)
              {
                i = (i - this.b.a() - 4) / 1500;
                paramInt = (paramInt - this.b.a() - 4) / 1500;
                localObject1 = new ag(this.c, (ArrayList)localObject1, new int[] { i, paramInt });
                if (this.a != null) {
                  continue;
                }
                break label382;
                this.c.delete();
                this.c = null;
                return null;
                return null;
                return null;
              }
            }
            else
            {
              this.c.delete();
              if (this.a == null) {
                return null;
              }
              try
              {
                this.a.close();
              }
              catch (Exception localException1) {}
              continue;
            }
            this.c.delete();
            if (this.a == null) {
              return null;
            }
            try
            {
              this.a.close();
            }
            catch (Exception localException2) {}
            continue;
            try
            {
              this.a.close();
            }
            catch (Exception localException6) {}
            RandomAccessFile localRandomAccessFile1;
            RandomAccessFile localRandomAccessFile2;
            throw ((Throwable)localObject2);
          }
          catch (FileNotFoundException localFileNotFoundException)
          {
            localRandomAccessFile1 = this.a;
            if (localRandomAccessFile1 == null)
            {
              localRandomAccessFile1 = null;
              break label382;
            }
            try
            {
              this.a.close();
            }
            catch (Exception localException3) {}
            continue;
          }
          catch (Exception localException4)
          {
            localRandomAccessFile2 = this.a;
            if (localRandomAccessFile2 == null) {
              continue;
            }
            try
            {
              this.a.close();
            }
            catch (Exception localException5) {}
            continue;
          }
          finally
          {
            if (this.a != null) {
              break label341;
            }
          }
          localag = finally;
        }
        finally {}
        try
        {
          this.a.close();
        }
        catch (Exception localException7) {}
      }
      finally {}
      label341:
      continue;
      if (localag.c() > 100)
      {
        paramInt = localag.c();
        if (paramInt < 5242880)
        {
          return localag;
          label382:
          if (localag != null) {
            break;
          }
        }
      }
    }
  }
  
  protected final void a(ag paramag)
  {
    Object localObject4 = null;
    Object localObject3 = null;
    for (;;)
    {
      try
      {
        try
        {
          this.c = paramag.a;
          localObject1 = this.c;
          if (localObject1 != null)
          {
            localObject1 = localObject3;
            localObject2 = localObject4;
          }
          try
          {
            this.a = new RandomAccessFile(this.c, "rw");
            localObject1 = localObject3;
            localObject2 = localObject4;
            byte[] arrayOfByte = new byte[this.b.a()];
            localObject1 = localObject3;
            localObject2 = localObject4;
            this.a.read(arrayOfByte);
            localObject1 = localObject3;
            localObject2 = localObject4;
            localObject3 = ah.b(arrayOfByte);
            localObject1 = localObject3;
            localObject2 = localObject3;
            boolean bool = paramag.b();
            if (bool) {
              continue;
            }
          }
          catch (FileNotFoundException paramag)
          {
            int i;
            paramag = this.a;
            localObject3 = localObject1;
            if (paramag == null) {
              continue;
            }
            try
            {
              this.a.close();
              localObject3 = localObject1;
            }
            catch (IOException paramag)
            {
              localObject3 = localObject1;
            }
            continue;
            localObject1 = localObject3;
            localObject2 = localObject3;
            ((BitSet)localObject3).set(i, false);
            i += 1;
            continue;
            try
            {
              this.a.close();
            }
            catch (IOException paramag) {}
            continue;
          }
          catch (IOException paramag)
          {
            paramag = this.a;
            localObject3 = localObject2;
            if (paramag == null) {
              continue;
            }
            try
            {
              this.a.close();
              localObject3 = localObject2;
            }
            catch (IOException paramag)
            {
              localObject3 = localObject2;
            }
            continue;
          }
          finally
          {
            if (this.a != null) {
              break label353;
            }
          }
          if (((BitSet)localObject3).isEmpty()) {
            break label368;
          }
          this.c = null;
          return;
          return;
        }
        finally {}
        Object localObject1 = localObject3;
        Object localObject2 = localObject3;
        i = paramag.b[0];
        localObject1 = localObject3;
        localObject2 = localObject3;
        if (i > paramag.b[1])
        {
          localObject1 = localObject3;
          localObject2 = localObject3;
          this.a.seek(0L);
          localObject1 = localObject3;
          localObject2 = localObject3;
          this.a.write(ah.a((BitSet)localObject3));
          continue;
        }
        throw paramag;
      }
      finally {}
      try
      {
        label353:
        this.a.close();
      }
      catch (IOException localIOException) {}
      continue;
      label368:
      this.c.delete();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/be.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */