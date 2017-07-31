package com.amap.api.mapcore2d;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.NinePatch;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

class bc
{
  private static int a(byte[] paramArrayOfByte, int paramInt)
  {
    return paramArrayOfByte[(paramInt + 0)] & 0xFF | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 8 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 3)] & 0xFF) << 24;
  }
  
  private static Bitmap a(InputStream paramInputStream)
    throws Exception
  {
    Object localObject = BitmapFactory.decodeStream(paramInputStream);
    paramInputStream = a((Bitmap)localObject);
    if (!NinePatch.isNinePatchChunk(paramInputStream)) {
      return (Bitmap)localObject;
    }
    Bitmap localBitmap = Bitmap.createBitmap((Bitmap)localObject, 1, 1, ((Bitmap)localObject).getWidth() - 2, ((Bitmap)localObject).getHeight() - 2);
    ((Bitmap)localObject).recycle();
    localObject = localBitmap.getClass().getDeclaredField("mNinePatchChunk");
    ((Field)localObject).setAccessible(true);
    ((Field)localObject).set(localBitmap, paramInputStream);
    return localBitmap;
  }
  
  public static Drawable a(Context paramContext, String paramString)
    throws Exception
  {
    paramString = b(paramContext, paramString);
    if (paramString.getNinePatchChunk() != null)
    {
      Rect localRect = new Rect();
      a(paramString.getNinePatchChunk(), localRect);
      return new NinePatchDrawable(paramContext.getResources(), paramString, paramString.getNinePatchChunk(), localRect, null);
    }
    return new BitmapDrawable(paramString);
  }
  
  private static void a(Bitmap paramBitmap, byte[] paramArrayOfByte)
  {
    int j = 0;
    int[] arrayOfInt = new int[paramBitmap.getWidth() - 2];
    paramBitmap.getPixels(arrayOfInt, 0, arrayOfInt.length, 1, paramBitmap.getHeight() - 1, arrayOfInt.length, 1);
    int i = 0;
    label42:
    label46:
    int k;
    if (i >= arrayOfInt.length)
    {
      i = arrayOfInt.length;
      k = i - 1;
      if (k >= 0) {
        break label133;
      }
      label56:
      arrayOfInt = new int[paramBitmap.getHeight() - 2];
      paramBitmap.getPixels(arrayOfInt, 0, 1, paramBitmap.getWidth() - 1, 0, 1, arrayOfInt.length);
      i = j;
      label87:
      if (i < arrayOfInt.length) {
        break label163;
      }
      label94:
      i = arrayOfInt.length;
    }
    label133:
    label163:
    do
    {
      j = i - 1;
      if (j < 0)
      {
        return;
        if (-16777216 != arrayOfInt[i])
        {
          i += 1;
          break;
        }
        a(paramArrayOfByte, 12, i);
        break label42;
        i = k;
        if (-16777216 != arrayOfInt[k]) {
          break label46;
        }
        a(paramArrayOfByte, 16, arrayOfInt.length - k - 2);
        break label56;
        if (-16777216 != arrayOfInt[i])
        {
          i += 1;
          break label87;
        }
        a(paramArrayOfByte, 20, i);
        break label94;
      }
      i = j;
    } while (-16777216 != arrayOfInt[j]);
    a(paramArrayOfByte, 24, arrayOfInt.length - j - 2);
  }
  
  private static void a(OutputStream paramOutputStream, int paramInt)
    throws IOException
  {
    paramOutputStream.write(paramInt >> 0 & 0xFF);
    paramOutputStream.write(paramInt >> 8 & 0xFF);
    paramOutputStream.write(paramInt >> 16 & 0xFF);
    paramOutputStream.write(paramInt >> 24 & 0xFF);
  }
  
  private static void a(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    paramArrayOfByte[(paramInt1 + 0)] = ((byte)(byte)(paramInt2 >> 0));
    paramArrayOfByte[(paramInt1 + 1)] = ((byte)(byte)(paramInt2 >> 8));
    paramArrayOfByte[(paramInt1 + 2)] = ((byte)(byte)(paramInt2 >> 16));
    paramArrayOfByte[(paramInt1 + 3)] = ((byte)(byte)(paramInt2 >> 24));
  }
  
  private static void a(byte[] paramArrayOfByte, Rect paramRect)
  {
    paramRect.left = a(paramArrayOfByte, 12);
    paramRect.right = a(paramArrayOfByte, 16);
    paramRect.top = a(paramArrayOfByte, 20);
    paramRect.bottom = a(paramArrayOfByte, 24);
  }
  
  private static byte[] a(Bitmap paramBitmap)
    throws IOException
  {
    int j = paramBitmap.getWidth();
    int i1 = paramBitmap.getHeight();
    Object localObject = new ByteArrayOutputStream();
    int i = 0;
    int[] arrayOfInt;
    int m;
    label61:
    int k;
    label76:
    int i2;
    int n;
    if (i >= 32)
    {
      arrayOfInt = new int[j - 2];
      paramBitmap.getPixels(arrayOfInt, 0, j, 1, 0, j - 2, 1);
      if (arrayOfInt[0] == -16777216) {
        break label278;
      }
      m = 0;
      if (arrayOfInt[(arrayOfInt.length - 1)] == -16777216) {
        break label284;
      }
      k = 0;
      i2 = arrayOfInt.length;
      j = 0;
      n = 0;
      i = 0;
      if (j < i2) {
        break label289;
      }
      if (k != 0) {
        break label324;
      }
      j = i;
      label100:
      i = j + 1;
      if (m != 0) {
        break label339;
      }
      label109:
      if (k != 0) {
        break label346;
      }
      k = i;
      label115:
      arrayOfInt = new int[i1 - 2];
      paramBitmap.getPixels(arrayOfInt, 0, 1, 0, 1, 1, i1 - 2);
      if (arrayOfInt[0] == -16777216) {
        break label353;
      }
      n = 0;
      label150:
      if (arrayOfInt[(arrayOfInt.length - 1)] == -16777216) {
        break label359;
      }
      m = 0;
      label166:
      int i3 = arrayOfInt.length;
      i1 = 0;
      i2 = 0;
      i = 0;
      if (i1 < i3) {
        break label365;
      }
      if (m != 0) {
        break label405;
      }
      i1 = i;
      label194:
      i = i1 + 1;
      if (n != 0) {
        break label421;
      }
      label204:
      if (m != 0) {
        break label428;
      }
      label209:
      m = 0;
    }
    for (;;)
    {
      if (m >= k * i)
      {
        localObject = ((ByteArrayOutputStream)localObject).toByteArray();
        localObject[0] = 1;
        localObject[1] = ((byte)(byte)j);
        localObject[2] = ((byte)(byte)i1);
        localObject[3] = ((byte)(byte)(k * i));
        a(paramBitmap, (byte[])localObject);
        return (byte[])localObject;
        ((ByteArrayOutputStream)localObject).write(0);
        i += 1;
        break;
        label278:
        m = 1;
        break label61;
        label284:
        k = 1;
        break label76;
        label289:
        if (n == arrayOfInt[j]) {}
        for (;;)
        {
          j += 1;
          break;
          i += 1;
          a((OutputStream)localObject, j);
          n = arrayOfInt[j];
        }
        label324:
        a((OutputStream)localObject, arrayOfInt.length);
        j = i + 1;
        break label100;
        label339:
        i -= 1;
        break label109;
        label346:
        k = i - 1;
        break label115;
        label353:
        n = 1;
        break label150;
        label359:
        m = 1;
        break label166;
        label365:
        if (i2 == arrayOfInt[i1]) {}
        for (;;)
        {
          i1 += 1;
          break;
          i += 1;
          a((OutputStream)localObject, i1);
          i2 = arrayOfInt[i1];
        }
        label405:
        i1 = i + 1;
        a((OutputStream)localObject, arrayOfInt.length);
        break label194;
        label421:
        i -= 1;
        break label204;
        label428:
        i -= 1;
        break label209;
      }
      a((OutputStream)localObject, 1);
      m += 1;
    }
  }
  
  private static Bitmap b(Context paramContext, String paramString)
    throws Exception
  {
    paramContext = paramContext.getAssets().open(paramString);
    paramString = a(paramContext);
    paramContext.close();
    return paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/mapcore2d/bc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */