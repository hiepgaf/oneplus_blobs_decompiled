package com.android.server.wm;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.view.Display;
import android.view.Surface;
import android.view.Surface.OutOfResourcesException;
import android.view.SurfaceControl;

class Watermark
{
  private final int mDeltaX;
  private final int mDeltaY;
  private final Display mDisplay;
  private boolean mDrawNeeded;
  private int mLastDH;
  private int mLastDW;
  private final Surface mSurface;
  private final SurfaceControl mSurfaceControl;
  private final String mText;
  private final int mTextHeight;
  private final Paint mTextPaint;
  private final int mTextWidth;
  private final String[] mTokens;
  
  /* Error */
  Watermark(Display paramDisplay, android.util.DisplayMetrics paramDisplayMetrics, android.view.SurfaceSession paramSurfaceSession, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 32	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: new 34	android/view/Surface
    //   8: dup
    //   9: invokespecial 35	android/view/Surface:<init>	()V
    //   12: putfield 37	com/android/server/wm/Watermark:mSurface	Landroid/view/Surface;
    //   15: aload_0
    //   16: aload_1
    //   17: putfield 39	com/android/server/wm/Watermark:mDisplay	Landroid/view/Display;
    //   20: aload_0
    //   21: aload 4
    //   23: putfield 41	com/android/server/wm/Watermark:mTokens	[Ljava/lang/String;
    //   26: new 43	java/lang/StringBuilder
    //   29: dup
    //   30: bipush 32
    //   32: invokespecial 46	java/lang/StringBuilder:<init>	(I)V
    //   35: astore_1
    //   36: aload_0
    //   37: getfield 41	com/android/server/wm/Watermark:mTokens	[Ljava/lang/String;
    //   40: iconst_0
    //   41: aaload
    //   42: invokevirtual 52	java/lang/String:length	()I
    //   45: istore 8
    //   47: iconst_0
    //   48: istore 7
    //   50: iload 7
    //   52: iload 8
    //   54: bipush -2
    //   56: iand
    //   57: if_icmpge +180 -> 237
    //   60: aload_0
    //   61: getfield 41	com/android/server/wm/Watermark:mTokens	[Ljava/lang/String;
    //   64: iconst_0
    //   65: aaload
    //   66: iload 7
    //   68: invokevirtual 56	java/lang/String:charAt	(I)C
    //   71: istore 5
    //   73: aload_0
    //   74: getfield 41	com/android/server/wm/Watermark:mTokens	[Ljava/lang/String;
    //   77: iconst_0
    //   78: aaload
    //   79: iload 7
    //   81: iconst_1
    //   82: iadd
    //   83: invokevirtual 56	java/lang/String:charAt	(I)C
    //   86: istore 6
    //   88: iload 5
    //   90: bipush 97
    //   92: if_icmplt +71 -> 163
    //   95: iload 5
    //   97: bipush 102
    //   99: if_icmpgt +64 -> 163
    //   102: iload 5
    //   104: bipush 97
    //   106: isub
    //   107: bipush 10
    //   109: iadd
    //   110: istore 5
    //   112: iload 6
    //   114: bipush 97
    //   116: if_icmplt +84 -> 200
    //   119: iload 6
    //   121: bipush 102
    //   123: if_icmpgt +77 -> 200
    //   126: iload 6
    //   128: bipush 97
    //   130: isub
    //   131: bipush 10
    //   133: iadd
    //   134: istore 6
    //   136: aload_1
    //   137: sipush 255
    //   140: iload 5
    //   142: bipush 16
    //   144: imul
    //   145: iload 6
    //   147: iadd
    //   148: isub
    //   149: i2c
    //   150: invokevirtual 60	java/lang/StringBuilder:append	(C)Ljava/lang/StringBuilder;
    //   153: pop
    //   154: iload 7
    //   156: iconst_2
    //   157: iadd
    //   158: istore 7
    //   160: goto -110 -> 50
    //   163: iload 5
    //   165: bipush 65
    //   167: if_icmplt +23 -> 190
    //   170: iload 5
    //   172: bipush 70
    //   174: if_icmpgt +16 -> 190
    //   177: iload 5
    //   179: bipush 65
    //   181: isub
    //   182: bipush 10
    //   184: iadd
    //   185: istore 5
    //   187: goto -75 -> 112
    //   190: iload 5
    //   192: bipush 48
    //   194: isub
    //   195: istore 5
    //   197: goto -85 -> 112
    //   200: iload 6
    //   202: bipush 65
    //   204: if_icmplt +23 -> 227
    //   207: iload 6
    //   209: bipush 70
    //   211: if_icmpgt +16 -> 227
    //   214: iload 6
    //   216: bipush 65
    //   218: isub
    //   219: bipush 10
    //   221: iadd
    //   222: istore 6
    //   224: goto -88 -> 136
    //   227: iload 6
    //   229: bipush 48
    //   231: isub
    //   232: istore 6
    //   234: goto -98 -> 136
    //   237: aload_0
    //   238: aload_1
    //   239: invokevirtual 64	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   242: putfield 66	com/android/server/wm/Watermark:mText	Ljava/lang/String;
    //   245: aload 4
    //   247: iconst_1
    //   248: iconst_1
    //   249: bipush 20
    //   251: aload_2
    //   252: invokestatic 72	com/android/server/wm/WindowManagerService:getPropertyInt	([Ljava/lang/String;IIILandroid/util/DisplayMetrics;)I
    //   255: istore 5
    //   257: aload_0
    //   258: new 74	android/graphics/Paint
    //   261: dup
    //   262: iconst_1
    //   263: invokespecial 75	android/graphics/Paint:<init>	(I)V
    //   266: putfield 77	com/android/server/wm/Watermark:mTextPaint	Landroid/graphics/Paint;
    //   269: aload_0
    //   270: getfield 77	com/android/server/wm/Watermark:mTextPaint	Landroid/graphics/Paint;
    //   273: iload 5
    //   275: i2f
    //   276: invokevirtual 81	android/graphics/Paint:setTextSize	(F)V
    //   279: aload_0
    //   280: getfield 77	com/android/server/wm/Watermark:mTextPaint	Landroid/graphics/Paint;
    //   283: getstatic 87	android/graphics/Typeface:SANS_SERIF	Landroid/graphics/Typeface;
    //   286: iconst_1
    //   287: invokestatic 91	android/graphics/Typeface:create	(Landroid/graphics/Typeface;I)Landroid/graphics/Typeface;
    //   290: invokevirtual 95	android/graphics/Paint:setTypeface	(Landroid/graphics/Typeface;)Landroid/graphics/Typeface;
    //   293: pop
    //   294: aload_0
    //   295: getfield 77	com/android/server/wm/Watermark:mTextPaint	Landroid/graphics/Paint;
    //   298: invokevirtual 99	android/graphics/Paint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   301: astore_1
    //   302: aload_0
    //   303: aload_0
    //   304: getfield 77	com/android/server/wm/Watermark:mTextPaint	Landroid/graphics/Paint;
    //   307: aload_0
    //   308: getfield 66	com/android/server/wm/Watermark:mText	Ljava/lang/String;
    //   311: invokevirtual 103	android/graphics/Paint:measureText	(Ljava/lang/String;)F
    //   314: f2i
    //   315: putfield 105	com/android/server/wm/Watermark:mTextWidth	I
    //   318: aload_0
    //   319: aload_1
    //   320: getfield 110	android/graphics/Paint$FontMetricsInt:descent	I
    //   323: aload_1
    //   324: getfield 113	android/graphics/Paint$FontMetricsInt:ascent	I
    //   327: isub
    //   328: putfield 115	com/android/server/wm/Watermark:mTextHeight	I
    //   331: aload_0
    //   332: aload 4
    //   334: iconst_2
    //   335: iconst_0
    //   336: aload_0
    //   337: getfield 105	com/android/server/wm/Watermark:mTextWidth	I
    //   340: iconst_2
    //   341: imul
    //   342: aload_2
    //   343: invokestatic 72	com/android/server/wm/WindowManagerService:getPropertyInt	([Ljava/lang/String;IIILandroid/util/DisplayMetrics;)I
    //   346: putfield 117	com/android/server/wm/Watermark:mDeltaX	I
    //   349: aload_0
    //   350: aload 4
    //   352: iconst_3
    //   353: iconst_0
    //   354: aload_0
    //   355: getfield 115	com/android/server/wm/Watermark:mTextHeight	I
    //   358: iconst_3
    //   359: imul
    //   360: aload_2
    //   361: invokestatic 72	com/android/server/wm/WindowManagerService:getPropertyInt	([Ljava/lang/String;IIILandroid/util/DisplayMetrics;)I
    //   364: putfield 119	com/android/server/wm/Watermark:mDeltaY	I
    //   367: aload 4
    //   369: iconst_4
    //   370: iconst_0
    //   371: ldc 120
    //   373: aload_2
    //   374: invokestatic 72	com/android/server/wm/WindowManagerService:getPropertyInt	([Ljava/lang/String;IIILandroid/util/DisplayMetrics;)I
    //   377: istore 5
    //   379: aload 4
    //   381: iconst_5
    //   382: iconst_0
    //   383: ldc 121
    //   385: aload_2
    //   386: invokestatic 72	com/android/server/wm/WindowManagerService:getPropertyInt	([Ljava/lang/String;IIILandroid/util/DisplayMetrics;)I
    //   389: istore 6
    //   391: aload 4
    //   393: bipush 6
    //   395: iconst_0
    //   396: bipush 7
    //   398: aload_2
    //   399: invokestatic 72	com/android/server/wm/WindowManagerService:getPropertyInt	([Ljava/lang/String;IIILandroid/util/DisplayMetrics;)I
    //   402: istore 7
    //   404: aload 4
    //   406: bipush 8
    //   408: iconst_0
    //   409: iconst_0
    //   410: aload_2
    //   411: invokestatic 72	com/android/server/wm/WindowManagerService:getPropertyInt	([Ljava/lang/String;IIILandroid/util/DisplayMetrics;)I
    //   414: istore 8
    //   416: aload 4
    //   418: bipush 9
    //   420: iconst_0
    //   421: iconst_0
    //   422: aload_2
    //   423: invokestatic 72	com/android/server/wm/WindowManagerService:getPropertyInt	([Ljava/lang/String;IIILandroid/util/DisplayMetrics;)I
    //   426: istore 9
    //   428: aload_0
    //   429: getfield 77	com/android/server/wm/Watermark:mTextPaint	Landroid/graphics/Paint;
    //   432: iload 6
    //   434: invokevirtual 124	android/graphics/Paint:setColor	(I)V
    //   437: aload_0
    //   438: getfield 77	com/android/server/wm/Watermark:mTextPaint	Landroid/graphics/Paint;
    //   441: iload 7
    //   443: i2f
    //   444: iload 8
    //   446: i2f
    //   447: iload 9
    //   449: i2f
    //   450: iload 5
    //   452: invokevirtual 128	android/graphics/Paint:setShadowLayer	(FFFI)V
    //   455: new 130	android/view/SurfaceControl
    //   458: dup
    //   459: aload_3
    //   460: ldc -124
    //   462: iconst_1
    //   463: iconst_1
    //   464: bipush -3
    //   466: iconst_4
    //   467: invokespecial 135	android/view/SurfaceControl:<init>	(Landroid/view/SurfaceSession;Ljava/lang/String;IIII)V
    //   470: astore_1
    //   471: aload_1
    //   472: aload_0
    //   473: getfield 39	com/android/server/wm/Watermark:mDisplay	Landroid/view/Display;
    //   476: invokevirtual 140	android/view/Display:getLayerStack	()I
    //   479: invokevirtual 143	android/view/SurfaceControl:setLayerStack	(I)V
    //   482: aload_1
    //   483: ldc -112
    //   485: invokevirtual 147	android/view/SurfaceControl:setLayer	(I)V
    //   488: aload_1
    //   489: fconst_0
    //   490: fconst_0
    //   491: invokevirtual 151	android/view/SurfaceControl:setPosition	(FF)V
    //   494: aload_1
    //   495: invokevirtual 154	android/view/SurfaceControl:show	()V
    //   498: aload_0
    //   499: getfield 37	com/android/server/wm/Watermark:mSurface	Landroid/view/Surface;
    //   502: aload_1
    //   503: invokevirtual 158	android/view/Surface:copyFrom	(Landroid/view/SurfaceControl;)V
    //   506: aload_0
    //   507: aload_1
    //   508: putfield 160	com/android/server/wm/Watermark:mSurfaceControl	Landroid/view/SurfaceControl;
    //   511: return
    //   512: astore_1
    //   513: aconst_null
    //   514: astore_1
    //   515: goto -9 -> 506
    //   518: astore_2
    //   519: goto -13 -> 506
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	522	0	this	Watermark
    //   0	522	1	paramDisplay	Display
    //   0	522	2	paramDisplayMetrics	android.util.DisplayMetrics
    //   0	522	3	paramSurfaceSession	android.view.SurfaceSession
    //   0	522	4	paramArrayOfString	String[]
    //   71	380	5	i	int
    //   86	347	6	j	int
    //   48	394	7	k	int
    //   45	400	8	m	int
    //   426	22	9	n	int
    // Exception table:
    //   from	to	target	type
    //   455	471	512	android/view/Surface$OutOfResourcesException
    //   471	506	518	android/view/Surface$OutOfResourcesException
  }
  
  void drawIfNeeded()
  {
    int n;
    int i1;
    Object localObject2;
    Object localObject1;
    if (this.mDrawNeeded)
    {
      n = this.mLastDW;
      i1 = this.mLastDH;
      this.mDrawNeeded = false;
      localObject2 = new Rect(0, 0, n, i1);
      localObject1 = null;
    }
    try
    {
      localObject2 = this.mSurface.lockCanvas((Rect)localObject2);
      localObject1 = localObject2;
    }
    catch (IllegalArgumentException localIllegalArgumentException)
    {
      int i;
      int i2;
      int j;
      int k;
      int m;
      for (;;) {}
    }
    catch (Surface.OutOfResourcesException localOutOfResourcesException)
    {
      for (;;) {}
    }
    if (localObject1 != null)
    {
      ((Canvas)localObject1).drawColor(0, PorterDuff.Mode.CLEAR);
      i = this.mDeltaX;
      i2 = this.mDeltaY;
      j = (this.mTextWidth + n) / i;
      k = this.mTextWidth + n - j * i;
      m = i / 4;
      if (k >= m)
      {
        j = i;
        if (k <= i - m) {}
      }
      else
      {
        j = i + i / 3;
      }
      k = -this.mTextHeight;
      i = -this.mTextWidth;
      while (k < this.mTextHeight + i1)
      {
        ((Canvas)localObject1).drawText(this.mText, i, k, this.mTextPaint);
        m = i + j;
        i = m;
        if (m >= n)
        {
          i = m - (this.mTextWidth + n);
          k += i2;
        }
      }
      this.mSurface.unlockCanvasAndPost((Canvas)localObject1);
    }
  }
  
  void positionSurface(int paramInt1, int paramInt2)
  {
    if ((this.mLastDW != paramInt1) || (this.mLastDH != paramInt2))
    {
      this.mLastDW = paramInt1;
      this.mLastDH = paramInt2;
      this.mSurfaceControl.setSize(paramInt1, paramInt2);
      this.mDrawNeeded = true;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/wm/Watermark.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */