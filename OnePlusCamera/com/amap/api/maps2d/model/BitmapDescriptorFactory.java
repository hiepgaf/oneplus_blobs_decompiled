package com.amap.api.maps2d.model;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import com.amap.api.mapcore2d.ah.a;
import com.amap.api.mapcore2d.aq;
import com.amap.api.mapcore2d.cj;
import java.io.FileInputStream;

public final class BitmapDescriptorFactory
{
  public static final float HUE_AZURE = 210.0F;
  public static final float HUE_BLUE = 240.0F;
  public static final float HUE_CYAN = 180.0F;
  public static final float HUE_GREEN = 120.0F;
  public static final float HUE_MAGENTA = 300.0F;
  public static final float HUE_ORANGE = 30.0F;
  public static final float HUE_RED = 0.0F;
  public static final float HUE_ROSE = 330.0F;
  public static final float HUE_VIOLET = 270.0F;
  public static final float HUE_YELLOW = 60.0F;
  
  private static Bitmap a(View paramView)
  {
    paramView.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
    paramView.layout(0, 0, paramView.getMeasuredWidth(), paramView.getMeasuredHeight());
    paramView.buildDrawingCache();
    return paramView.getDrawingCache().copy(Bitmap.Config.ARGB_8888, false);
  }
  
  public static BitmapDescriptor defaultMarker()
  {
    try
    {
      BitmapDescriptor localBitmapDescriptor = fromAsset(ah.a.b.name() + ".png");
      return localBitmapDescriptor;
    }
    catch (Throwable localThrowable)
    {
      cj.a(localThrowable, "BitmapDescriptorFactory", "defaultMarker");
    }
    return null;
  }
  
  public static BitmapDescriptor defaultMarker(float paramFloat)
  {
    int i = (int)(15.0F + paramFloat);
    for (;;)
    {
      try
      {
        paramFloat = i / 30 * 30;
        if (paramFloat <= 330.0F) {
          break label90;
        }
        paramFloat = 330.0F;
      }
      catch (Throwable localThrowable)
      {
        String str1;
        cj.a(localThrowable, "BitmapDescriptorFactory", "defaultMarker");
        return null;
      }
      return fromAsset(str1 + "2d.png");
      if (paramFloat == 330.0F)
      {
        str1 = "ROSE";
        continue;
        String str2;
        for (;;)
        {
          str2 = "";
          if (paramFloat != 0.0F) {
            break label104;
          }
          str2 = "RED";
          break;
          label90:
          if (paramFloat < 0.0F) {
            paramFloat = 0.0F;
          }
        }
        label104:
        if (paramFloat == 30.0F)
        {
          str2 = "ORANGE";
        }
        else if (paramFloat == 60.0F)
        {
          str2 = "YELLOW";
        }
        else if (paramFloat == 120.0F)
        {
          str2 = "GREEN";
        }
        else if (paramFloat == 180.0F)
        {
          str2 = "CYAN";
        }
        else if (paramFloat == 210.0F)
        {
          str2 = "AZURE";
        }
        else if (paramFloat == 240.0F)
        {
          str2 = "BLUE";
        }
        else if (paramFloat == 270.0F)
        {
          str2 = "VIOLET";
        }
        else
        {
          if (paramFloat != 300.0F) {
            break;
          }
          str2 = "MAGENTAV";
        }
      }
    }
  }
  
  /* Error */
  public static BitmapDescriptor fromAsset(String paramString)
  {
    // Byte code:
    //   0: ldc 2
    //   2: new 80	java/lang/StringBuilder
    //   5: dup
    //   6: invokespecial 81	java/lang/StringBuilder:<init>	()V
    //   9: ldc -116
    //   11: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   14: aload_0
    //   15: invokevirtual 95	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   18: invokevirtual 100	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   21: invokevirtual 146	java/lang/Class:getResourceAsStream	(Ljava/lang/String;)Ljava/io/InputStream;
    //   24: astore_1
    //   25: aload_1
    //   26: astore_0
    //   27: aload_1
    //   28: invokestatic 152	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;)Landroid/graphics/Bitmap;
    //   31: invokestatic 156	com/amap/api/maps2d/model/BitmapDescriptorFactory:fromBitmap	(Landroid/graphics/Bitmap;)Lcom/amap/api/maps2d/model/BitmapDescriptor;
    //   34: astore_2
    //   35: aload_1
    //   36: ifnonnull +5 -> 41
    //   39: aload_2
    //   40: areturn
    //   41: aload_1
    //   42: invokevirtual 161	java/io/InputStream:close	()V
    //   45: aload_2
    //   46: areturn
    //   47: astore_0
    //   48: aload_0
    //   49: ldc 106
    //   51: ldc -94
    //   53: invokestatic 112	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   56: aconst_null
    //   57: areturn
    //   58: astore_2
    //   59: aconst_null
    //   60: astore_1
    //   61: aload_1
    //   62: astore_0
    //   63: aload_2
    //   64: ldc 106
    //   66: ldc -94
    //   68: invokestatic 112	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   71: aload_1
    //   72: ifnonnull +5 -> 77
    //   75: aconst_null
    //   76: areturn
    //   77: aload_1
    //   78: invokevirtual 161	java/io/InputStream:close	()V
    //   81: aconst_null
    //   82: areturn
    //   83: astore_0
    //   84: aload_0
    //   85: ldc 106
    //   87: ldc -94
    //   89: invokestatic 112	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   92: aconst_null
    //   93: areturn
    //   94: astore_1
    //   95: aconst_null
    //   96: astore_0
    //   97: aload_0
    //   98: ifnonnull +5 -> 103
    //   101: aload_1
    //   102: athrow
    //   103: aload_0
    //   104: invokevirtual 161	java/io/InputStream:close	()V
    //   107: goto -6 -> 101
    //   110: astore_0
    //   111: aload_0
    //   112: ldc 106
    //   114: ldc -94
    //   116: invokestatic 112	com/amap/api/mapcore2d/cj:a	(Ljava/lang/Throwable;Ljava/lang/String;Ljava/lang/String;)V
    //   119: aconst_null
    //   120: areturn
    //   121: astore_1
    //   122: goto -25 -> 97
    //   125: astore_2
    //   126: goto -65 -> 61
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	129	0	paramString	String
    //   24	54	1	localInputStream	java.io.InputStream
    //   94	8	1	localObject1	Object
    //   121	1	1	localObject2	Object
    //   34	12	2	localBitmapDescriptor	BitmapDescriptor
    //   58	6	2	localThrowable1	Throwable
    //   125	1	2	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   41	45	47	java/lang/Throwable
    //   0	25	58	java/lang/Throwable
    //   77	81	83	java/lang/Throwable
    //   0	25	94	finally
    //   103	107	110	java/lang/Throwable
    //   27	35	121	finally
    //   63	71	121	finally
    //   27	35	125	java/lang/Throwable
  }
  
  public static BitmapDescriptor fromBitmap(Bitmap paramBitmap)
  {
    if (paramBitmap != null) {
      return new BitmapDescriptor(paramBitmap);
    }
    return null;
  }
  
  public static BitmapDescriptor fromFile(String paramString)
  {
    try
    {
      Object localObject = aq.a;
      if (localObject == null) {
        return null;
      }
      paramString = ((Context)localObject).openFileInput(paramString);
      localObject = BitmapFactory.decodeStream(paramString);
      paramString.close();
      paramString = fromBitmap((Bitmap)localObject);
      return paramString;
    }
    catch (Throwable paramString)
    {
      cj.a(paramString, "BitmapDescriptorFactory", "fromFile");
    }
    return null;
  }
  
  public static BitmapDescriptor fromPath(String paramString)
  {
    try
    {
      paramString = fromBitmap(BitmapFactory.decodeFile(paramString));
      return paramString;
    }
    catch (Throwable paramString)
    {
      cj.a(paramString, "BitmapDescriptorFactory", "fromPath");
    }
    return null;
  }
  
  public static BitmapDescriptor fromResource(int paramInt)
  {
    try
    {
      Object localObject = aq.a;
      if (localObject == null) {
        return null;
      }
      localObject = fromBitmap(BitmapFactory.decodeStream(((Context)localObject).getResources().openRawResource(paramInt)));
      return (BitmapDescriptor)localObject;
    }
    catch (Throwable localThrowable)
    {
      cj.a(localThrowable, "BitmapDescriptorFactory", "fromResource");
    }
    return null;
  }
  
  public static BitmapDescriptor fromView(View paramView)
  {
    try
    {
      Object localObject = aq.a;
      if (localObject == null) {
        return null;
      }
      localObject = new FrameLayout((Context)localObject);
      ((FrameLayout)localObject).addView(paramView);
      ((FrameLayout)localObject).destroyDrawingCache();
      paramView = fromBitmap(a((View)localObject));
      return paramView;
    }
    catch (Throwable paramView)
    {
      cj.a(paramView, "BitmapDescriptorFactory", "fromView");
    }
    return null;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/amap/api/maps2d/model/BitmapDescriptorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */