package com.aps;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Process;
import android.os.StatFs;
import android.telephony.NeighboringCellInfo;
import android.text.TextUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class ah
{
  private Context a;
  private int b = 0;
  private int c = 0;
  private int d = 0;
  
  protected ah(Context paramContext)
  {
    this.a = paramContext;
    a(768);
  }
  
  private static int a(int paramInt1, int paramInt2)
  {
    if (paramInt1 >= paramInt2) {
      return paramInt2;
    }
    return paramInt1;
  }
  
  protected static aa a(Location paramLocation, ak paramak, int paramInt, byte paramByte, long paramLong, boolean paramBoolean)
  {
    aa localaa = new aa();
    if (paramInt <= 0) {}
    while ((paramInt > 3) || (paramak == null)) {
      return null;
    }
    int i;
    label32:
    label37:
    int j;
    if (paramInt == 1)
    {
      i = 1;
      if (paramInt != 2) {
        break label274;
      }
      j = 1;
      label40:
      localObject1 = paramak.p().getBytes();
      System.arraycopy(localObject1, 0, localaa.c, 0, a(localObject1.length, localaa.c.length));
      localObject1 = paramak.f().getBytes();
      System.arraycopy(localObject1, 0, localaa.g, 0, a(localObject1.length, localaa.g.length));
      localObject1 = paramak.g().getBytes();
      System.arraycopy(localObject1, 0, localaa.a, 0, a(localObject1.length, localaa.a.length));
      localObject1 = paramak.h().getBytes();
      System.arraycopy(localObject1, 0, localaa.b, 0, a(localObject1.length, localaa.b.length));
      localaa.d = ((short)(short)paramak.q());
      localaa.e = ((short)(short)paramak.r());
      localaa.f = ((byte)(byte)paramak.s());
      localObject1 = paramak.t().getBytes();
      System.arraycopy(localObject1, 0, localaa.h, 0, a(localObject1.length, localaa.h.length));
      paramLong /= 1000L;
      if (paramLocation != null) {
        break label285;
      }
    }
    label250:
    for (paramInt = 0;; paramInt = 1)
    {
      if (paramInt != 0) {
        break label297;
      }
      if (paramBoolean) {
        break label527;
      }
      return null;
      if (paramInt == 3) {
        break;
      }
      i = 0;
      break label32;
      label274:
      if (paramInt == 3) {
        break label37;
      }
      j = 0;
      break label40;
      label285:
      if (!paramak.e()) {
        break label250;
      }
    }
    label297:
    Object localObject1 = new x();
    ((x)localObject1).b = ((int)paramLong);
    Object localObject2 = new z();
    ((z)localObject2).a = ((int)(paramLocation.getLongitude() * 1000000.0D));
    ((z)localObject2).b = ((int)(paramLocation.getLatitude() * 1000000.0D));
    ((z)localObject2).c = ((int)paramLocation.getAltitude());
    ((z)localObject2).d = ((int)paramLocation.getAccuracy());
    ((z)localObject2).e = ((int)paramLocation.getSpeed());
    ((z)localObject2).f = ((short)(short)(int)paramLocation.getBearing());
    if (Build.MODEL.equals("sdk"))
    {
      ((z)localObject2).g = 1;
      ((z)localObject2).h = ((byte)paramByte);
      ((z)localObject2).i = System.currentTimeMillis();
      ((z)localObject2).j = paramak.o();
      ((x)localObject1).c = ((z)localObject2);
      localaa.j.add(localObject1);
      paramByte = 1;
      if (paramak.c()) {
        break label757;
      }
      paramInt = paramByte;
      label463:
      paramByte = paramInt;
      if (paramak.c()) {
        break label1028;
      }
      paramInt = paramByte;
      label474:
      if (paramak.d()) {
        break label1232;
      }
      paramByte = paramInt;
      label483:
      localaa.i = ((short)(short)paramByte);
      if (paramByte < 2) {
        break label1499;
      }
    }
    label527:
    label757:
    label821:
    label1028:
    label1232:
    label1499:
    while (paramBoolean)
    {
      return localaa;
      if (!ak.b(paramak.y())) {}
      while (!y.b)
      {
        ((z)localObject2).g = 0;
        break;
      }
      break;
      localObject1 = new x();
      ((x)localObject1).b = ((int)paramLong);
      localObject2 = new ac();
      ((ac)localObject2).a = paramak.x();
      paramInt = 0;
      for (;;)
      {
        if (paramInt >= ((ac)localObject2).a)
        {
          ((x)localObject1).g = ((ac)localObject2);
          localaa.j.add(localObject1);
          break;
        }
        localObject3 = new ad();
        ((ad)localObject3).a = ((byte)(byte)paramak.a(paramInt).length());
        System.arraycopy(paramak.a(paramInt).getBytes(), 0, ((ad)localObject3).b, 0, ((ad)localObject3).a);
        ((ad)localObject3).c = paramak.b(paramInt);
        ((ad)localObject3).d = paramak.c(paramInt);
        ((ad)localObject3).e = paramak.d(paramInt);
        ((ad)localObject3).f = paramak.e(paramInt);
        ((ad)localObject3).g = paramak.f(paramInt);
        ((ad)localObject3).h = ((byte)(byte)paramak.g(paramInt).length());
        System.arraycopy(paramak.g(paramInt).getBytes(), 0, ((ad)localObject3).i, 0, ((ad)localObject3).h);
        ((ad)localObject3).j = paramak.h(paramInt);
        ((ac)localObject2).b.add(localObject3);
        paramInt += 1;
      }
      paramInt = paramByte;
      if (paramak.i()) {
        break label463;
      }
      paramInt = paramByte;
      if (i == 0) {
        break label463;
      }
      paramInt = paramByte;
      if (paramBoolean) {
        break label463;
      }
      localObject1 = new x();
      ((x)localObject1).b = ((int)paramLong);
      localObject2 = new v();
      Object localObject3 = paramak.a(paramLocation.getSpeed());
      if (localObject3 == null)
      {
        ((v)localObject2).c = paramak.l();
        localObject3 = paramak.m();
        ((v)localObject2).d = ((byte)(byte)((List)localObject3).size());
        paramInt = 0;
      }
      Object localObject4;
      for (;;)
      {
        if (paramInt >= ((List)localObject3).size())
        {
          ((x)localObject1).d = ((v)localObject2);
          paramInt = 2;
          localaa.j.add(localObject1);
          break;
          if (((List)localObject3).size() < 3) {
            break label821;
          }
          ((v)localObject2).a = ((short)(short)((Integer)((List)localObject3).get(0)).intValue());
          ((v)localObject2).b = ((Integer)((List)localObject3).get(1)).intValue();
          break label821;
        }
        localObject4 = new aj();
        ((aj)localObject4).a = ((short)(short)((NeighboringCellInfo)((List)localObject3).get(paramInt)).getLac());
        ((aj)localObject4).b = ((NeighboringCellInfo)((List)localObject3).get(paramInt)).getCid();
        ((aj)localObject4).c = ((byte)(byte)((NeighboringCellInfo)((List)localObject3).get(paramInt)).getRssi());
        ((v)localObject2).e.add(localObject4);
        paramInt += 1;
      }
      paramInt = paramByte;
      if (!paramak.i()) {
        break label474;
      }
      paramInt = paramByte;
      if (i == 0) {
        break label474;
      }
      paramInt = paramByte;
      if (paramBoolean) {
        break label474;
      }
      localObject1 = new x();
      ((x)localObject1).b = ((int)paramLong);
      localObject2 = new ai();
      paramLocation = paramak.b(paramLocation.getSpeed());
      if (paramLocation == null) {}
      for (;;)
      {
        ((x)localObject1).e = ((ai)localObject2);
        paramInt = paramByte + 1;
        localaa.j.add(localObject1);
        break;
        if (paramLocation.size() >= 6)
        {
          ((ai)localObject2).a = ((Integer)paramLocation.get(3)).intValue();
          ((ai)localObject2).b = ((Integer)paramLocation.get(4)).intValue();
          ((ai)localObject2).c = ((short)(short)((Integer)paramLocation.get(0)).intValue());
          ((ai)localObject2).d = ((short)(short)((Integer)paramLocation.get(1)).intValue());
          ((ai)localObject2).e = ((Integer)paramLocation.get(2)).intValue();
          ((ai)localObject2).f = paramak.l();
        }
      }
      paramByte = paramInt;
      if (j == 0) {
        break label483;
      }
      paramByte = paramInt;
      if (paramBoolean) {
        break label483;
      }
      paramLocation = new x();
      localObject1 = new ae();
      paramak = paramak.u();
      paramLocation.b = ((int)(((Long)paramak.get(0)).longValue() / 1000L));
      ((ae)localObject1).a = ((byte)(byte)(paramak.size() - 1));
      paramByte = 1;
      if (paramByte >= paramak.size())
      {
        paramLocation.f = ((ae)localObject1);
        paramByte = paramInt + 1;
        localaa.j.add(paramLocation);
        break label483;
      }
      localObject3 = (List)paramak.get(paramByte);
      if (localObject3 == null) {}
      for (;;)
      {
        paramByte += 1;
        break;
        if (((List)localObject3).size() >= 3)
        {
          localObject2 = new af();
          localObject4 = ((String)((List)localObject3).get(0)).getBytes();
          System.arraycopy(localObject4, 0, ((af)localObject2).a, 0, a(localObject4.length, ((af)localObject2).a.length));
          ((af)localObject2).b = ((short)(short)((Integer)((List)localObject3).get(1)).intValue());
          localObject3 = ((String)((List)localObject3).get(2)).getBytes();
          System.arraycopy(localObject3, 0, ((af)localObject2).c, 0, a(localObject3.length, ((af)localObject2).c.length));
          ((ae)localObject1).b.add(localObject2);
        }
      }
    }
    return null;
  }
  
  protected static File a(Context paramContext)
  {
    paramContext = "/Android/data/" + paramContext.getPackageName() + "/files/";
    return new File(Environment.getExternalStorageDirectory().getPath() + paramContext);
  }
  
  public static Object a(Object paramObject, String paramString, Object... paramVarArgs)
  {
    int i = 0;
    Class localClass = paramObject.getClass();
    Class[] arrayOfClass = new Class[paramVarArgs.length];
    int j = paramVarArgs.length;
    if (i >= j)
    {
      paramString = localClass.getDeclaredMethod(paramString, arrayOfClass);
      if (!paramString.isAccessible()) {
        break label85;
      }
    }
    for (;;)
    {
      return paramString.invoke(paramObject, paramVarArgs);
      arrayOfClass[i] = paramVarArgs[i].getClass();
      if (arrayOfClass[i] != Integer.class) {}
      for (;;)
      {
        i += 1;
        break;
        arrayOfClass[i] = Integer.TYPE;
      }
      label85:
      paramString.setAccessible(true);
    }
  }
  
  private static ArrayList a(File[] paramArrayOfFile)
  {
    int i = 0;
    ArrayList localArrayList = new ArrayList();
    if (i >= paramArrayOfFile.length) {
      return localArrayList;
    }
    if (!paramArrayOfFile[i].isFile()) {}
    for (;;)
    {
      i += 1;
      break;
      if ((paramArrayOfFile[i].getName().length() == 10) && (TextUtils.isDigitsOnly(paramArrayOfFile[i].getName()))) {
        localArrayList.add(paramArrayOfFile[i]);
      }
    }
  }
  
  protected static byte[] a(BitSet paramBitSet)
  {
    byte[] arrayOfByte = new byte[paramBitSet.size() / 8];
    int i = 0;
    if (i >= paramBitSet.size()) {
      return arrayOfByte;
    }
    int k = i / 8;
    int m = arrayOfByte[k];
    if (!paramBitSet.get(i)) {}
    for (int j = 0;; j = 1)
    {
      arrayOfByte[k] = ((byte)(byte)(j << 7 - i % 8 | m));
      i += 1;
      break;
    }
  }
  
  protected static byte[] a(byte[] paramArrayOfByte)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      localObject1 = localObject2;
      GZIPOutputStream localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
      localObject1 = localObject2;
      localGZIPOutputStream.write(paramArrayOfByte);
      localObject1 = localObject2;
      localGZIPOutputStream.finish();
      localObject1 = localObject2;
      localGZIPOutputStream.close();
      localObject1 = localObject2;
      paramArrayOfByte = localByteArrayOutputStream.toByteArray();
      localObject1 = paramArrayOfByte;
      localByteArrayOutputStream.close();
      return paramArrayOfByte;
    }
    catch (Exception paramArrayOfByte) {}
    return (byte[])localObject1;
  }
  
  protected static byte[] a(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramArrayOfByte == null) {}
    while (paramArrayOfByte.length == 0) {
      return null;
    }
    int j = new String(paramArrayOfByte).indexOf(0);
    int i;
    if (j <= 0) {
      i = 1;
    }
    for (;;)
    {
      byte[] arrayOfByte = new byte[i];
      System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, i);
      arrayOfByte[(i - 1)] = 0;
      return arrayOfByte;
      i = paramInt;
      if (j + 1 <= paramInt) {
        i = j + 1;
      }
    }
  }
  
  public static int b(Object paramObject, String paramString, Object... paramVarArgs)
  {
    int i = 0;
    Class localClass = paramObject.getClass();
    Class[] arrayOfClass = new Class[paramVarArgs.length];
    int j = paramVarArgs.length;
    if (i >= j)
    {
      paramString = localClass.getDeclaredMethod(paramString, arrayOfClass);
      if (!paramString.isAccessible()) {
        break label91;
      }
    }
    for (;;)
    {
      return ((Integer)paramString.invoke(paramObject, paramVarArgs)).intValue();
      arrayOfClass[i] = paramVarArgs[i].getClass();
      if (arrayOfClass[i] != Integer.class) {}
      for (;;)
      {
        i += 1;
        break;
        arrayOfClass[i] = Integer.TYPE;
      }
      label91:
      paramString.setAccessible(true);
    }
  }
  
  protected static BitSet b(byte[] paramArrayOfByte)
  {
    BitSet localBitSet = new BitSet(paramArrayOfByte.length << 3);
    int i = 0;
    int j = 0;
    int k;
    for (;;)
    {
      if (i >= paramArrayOfByte.length) {
        return localBitSet;
      }
      k = 7;
      if (k >= 0) {
        break;
      }
      i += 1;
    }
    if ((paramArrayOfByte[i] & 1 << k) >> k != 1) {}
    for (boolean bool = false;; bool = true)
    {
      localBitSet.set(j, bool);
      k -= 1;
      j += 1;
      break;
    }
  }
  
  private File c(long paramLong)
  {
    boolean bool2 = false;
    if (Process.myUid() != 1000) {}
    try
    {
      bool1 = "mounted".equals(Environment.getExternalStorageState());
      if (!c())
      {
        StatFs localStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        if (localStatFs.getAvailableBlocks() * localStatFs.getBlockSize() <= this.c / 2) {
          break label105;
        }
        i = 1;
        if (i != 0) {
          break label110;
        }
        return null;
        return null;
      }
    }
    catch (Exception localException)
    {
      int i;
      do
      {
        for (;;)
        {
          bool1 = false;
        }
      } while (bool1);
      Object localObject = null;
      boolean bool1 = bool2;
      for (;;)
      {
        if (bool1) {
          break label239;
        }
        return null;
        label105:
        i = 0;
        break;
        label110:
        localObject = a(this.a).getPath();
        localObject = new File((String)localObject + File.separator + "carrierdata");
        if (!((File)localObject).exists()) {
          label166:
          ((File)localObject).mkdirs();
        }
        for (;;)
        {
          localObject = new File(((File)localObject).getPath() + File.separator + paramLong);
          try
          {
            bool1 = ((File)localObject).createNewFile();
          }
          catch (IOException localIOException)
          {
            bool1 = bool2;
          }
          if (!((File)localObject).isDirectory()) {
            break label166;
          }
        }
      }
      label239:
      return (File)localObject;
    }
  }
  
  protected static boolean c()
  {
    if (Build.VERSION.SDK_INT < 9) {}
    for (;;)
    {
      return true;
      try
      {
        boolean bool = ((Boolean)Environment.class.getMethod("isExternalStorageRemovable", null).invoke(null, null)).booleanValue();
        return bool;
      }
      catch (Exception localException) {}
    }
  }
  
  private File d()
  {
    if (Process.myUid() != 1000) {}
    for (;;)
    {
      Object localObject2;
      try
      {
        bool = "mounted".equals(Environment.getExternalStorageState());
        if (!c())
        {
          Object localObject1 = a(this.a).getPath();
          localObject1 = new File((String)localObject1 + File.separator + "carrierdata");
          if (((File)localObject1).exists()) {
            continue;
          }
          localObject1 = null;
          return (File)localObject1;
          return null;
        }
      }
      catch (Exception localException)
      {
        boolean bool = false;
        continue;
        if (bool) {
          continue;
        }
        if ((goto 77) || (!localException.isDirectory())) {
          continue;
        }
        localObject2 = localException.listFiles();
        if ((localObject2 == null) || (localObject2.length <= 0)) {
          continue;
        }
        localObject2 = a((File[])localObject2);
        if (((ArrayList)localObject2).size() != 1)
        {
          if (((ArrayList)localObject2).size() < 2) {
            continue;
          }
          File localFile1 = (File)((ArrayList)localObject2).get(0);
          File localFile2 = (File)((ArrayList)localObject2).get(1);
          localObject2 = localFile1;
          if (localFile1.getName().compareTo(localFile2.getName()) > 0) {
            continue;
          }
          return localFile2;
        }
        if (((File)((ArrayList)localObject2).get(0)).length() < this.d) {}
      }
      for (int i = 1; i == 0; i = 0) {
        return (File)((ArrayList)localObject2).get(0);
      }
    }
  }
  
  private int e()
  {
    int i = 0;
    if (Process.myUid() != 1000) {}
    Object localObject2;
    do
    {
      do
      {
        do
        {
          try
          {
            bool = "mounted".equals(Environment.getExternalStorageState());
            if (!c())
            {
              Object localObject1 = a(this.a).getPath();
              localObject1 = new File((String)localObject1 + File.separator + "carrierdata");
              if (((File)localObject1).exists()) {
                continue;
              }
              return 0;
              return 0;
            }
          }
          catch (Exception localException)
          {
            boolean bool;
            do
            {
              for (;;)
              {
                bool = false;
              }
            } while (bool);
            return 0;
          }
        } while (!localException.isDirectory());
        localObject2 = localException.listFiles();
      } while ((localObject2 == null) || (localObject2.length <= 0));
      localObject2 = a((File[])localObject2);
      if (((ArrayList)localObject2).size() == 1) {
        break;
      }
    } while (((ArrayList)localObject2).size() < 2);
    return 2;
    if (((File)((ArrayList)localObject2).get(0)).length() > 0L) {
      i = 1;
    }
    if (i == 0) {
      return 10;
    }
    return 1;
  }
  
  private File f()
  {
    if (Process.myUid() != 1000) {}
    try
    {
      bool = "mounted".equals(Environment.getExternalStorageState());
      if (!c())
      {
        File localFile1 = a(this.a);
        if (localFile1 != null) {
          break label56;
        }
        localFile1 = null;
        return localFile1;
        return null;
      }
    }
    catch (Exception localException)
    {
      label56:
      File localFile2;
      File localFile3;
      do
      {
        do
        {
          do
          {
            do
            {
              for (;;)
              {
                boolean bool = false;
                continue;
                if (bool) {}
              }
              localObject = localException.getPath();
              localObject = new File((String)localObject + File.separator + "carrierdata");
            } while ((!((File)localObject).exists()) || (!((File)localObject).isDirectory()));
            localObject = ((File)localObject).listFiles();
          } while ((localObject == null) || (localObject.length <= 0));
          localObject = a((File[])localObject);
        } while (((ArrayList)localObject).size() < 2);
        localFile2 = (File)((ArrayList)localObject).get(0);
        localFile3 = (File)((ArrayList)localObject).get(1);
        Object localObject = localFile2;
      } while (localFile2.getName().compareTo(localFile3.getName()) <= 0);
      return localFile3;
    }
  }
  
  protected int a()
  {
    return this.b;
  }
  
  /* Error */
  protected File a(long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokespecial 548	com/aps/ah:d	()Ljava/io/File;
    //   6: astore_3
    //   7: aload_3
    //   8: ifnull +7 -> 15
    //   11: aload_0
    //   12: monitorexit
    //   13: aload_3
    //   14: areturn
    //   15: aload_0
    //   16: lload_1
    //   17: invokespecial 550	com/aps/ah:c	(J)Ljava/io/File;
    //   20: astore_3
    //   21: goto -10 -> 11
    //   24: astore_3
    //   25: aload_0
    //   26: monitorexit
    //   27: aload_3
    //   28: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	29	0	this	ah
    //   0	29	1	paramLong	long
    //   6	15	3	localFile	File
    //   24	4	3	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	24	finally
    //   15	21	24	finally
  }
  
  protected void a(int paramInt)
  {
    this.b = paramInt;
    this.c = ((this.b << 3) * 1500 + this.b + 4);
    if (this.b == 256) {}
    while (this.b == 768)
    {
      this.d = (this.c / 100);
      return;
    }
    if (this.b != 8736) {
      return;
    }
    this.d = (this.c - 5000);
  }
  
  protected File b()
  {
    return f();
  }
  
  protected boolean b(long paramLong)
  {
    try
    {
      int i = e();
      if (i != 0)
      {
        if (i != 1)
        {
          if (i == 2) {
            break label49;
          }
          return false;
        }
      }
      else {
        return false;
      }
      File localFile = c(paramLong);
      return localFile != null;
      label49:
      return true;
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/ah.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */