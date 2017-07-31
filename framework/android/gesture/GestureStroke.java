package android.gesture;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class GestureStroke
{
  static final float TOUCH_TOLERANCE = 3.0F;
  public final RectF boundingBox;
  public final float length;
  private Path mCachedPath;
  public final float[] points;
  private final long[] timestamps;
  
  private GestureStroke(RectF paramRectF, float paramFloat, float[] paramArrayOfFloat, long[] paramArrayOfLong)
  {
    this.boundingBox = new RectF(paramRectF.left, paramRectF.top, paramRectF.right, paramRectF.bottom);
    this.length = paramFloat;
    this.points = ((float[])paramArrayOfFloat.clone());
    this.timestamps = ((long[])paramArrayOfLong.clone());
  }
  
  public GestureStroke(ArrayList<GesturePoint> paramArrayList)
  {
    int k = paramArrayList.size();
    float[] arrayOfFloat = new float[k * 2];
    long[] arrayOfLong = new long[k];
    RectF localRectF = null;
    float f = 0.0F;
    int j = 0;
    int i = 0;
    if (i < k)
    {
      GesturePoint localGesturePoint = (GesturePoint)paramArrayList.get(i);
      arrayOfFloat[(i * 2)] = localGesturePoint.x;
      arrayOfFloat[(i * 2 + 1)] = localGesturePoint.y;
      arrayOfLong[j] = localGesturePoint.timestamp;
      if (localRectF == null)
      {
        localRectF = new RectF();
        localRectF.top = localGesturePoint.y;
        localRectF.left = localGesturePoint.x;
        localRectF.right = localGesturePoint.x;
        localRectF.bottom = localGesturePoint.y;
        f = 0.0F;
      }
      for (;;)
      {
        j += 1;
        i += 1;
        break;
        f = (float)(f + Math.hypot(localGesturePoint.x - arrayOfFloat[((i - 1) * 2)], localGesturePoint.y - arrayOfFloat[((i - 1) * 2 + 1)]));
        localRectF.union(localGesturePoint.x, localGesturePoint.y);
      }
    }
    this.timestamps = arrayOfLong;
    this.points = arrayOfFloat;
    this.boundingBox = localRectF;
    this.length = f;
  }
  
  static GestureStroke deserialize(DataInputStream paramDataInputStream)
    throws IOException
  {
    int j = paramDataInputStream.readInt();
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      localArrayList.add(GesturePoint.deserialize(paramDataInputStream));
      i += 1;
    }
    return new GestureStroke(localArrayList);
  }
  
  private void makePath()
  {
    float[] arrayOfFloat = this.points;
    int j = arrayOfFloat.length;
    Object localObject1 = null;
    float f5 = 0.0F;
    float f4 = 0.0F;
    int i = 0;
    if (i < j)
    {
      float f6 = arrayOfFloat[i];
      float f3 = arrayOfFloat[(i + 1)];
      Object localObject2;
      float f1;
      float f2;
      if (localObject1 == null)
      {
        localObject2 = new Path();
        ((Path)localObject2).moveTo(f6, f3);
        f1 = f6;
        f2 = f3;
      }
      for (;;)
      {
        i += 2;
        f5 = f1;
        f4 = f2;
        localObject1 = localObject2;
        break;
        f1 = Math.abs(f6 - f5);
        float f7 = Math.abs(f3 - f4);
        if (f1 < 3.0F)
        {
          f1 = f5;
          f2 = f4;
          localObject2 = localObject1;
          if (f7 < 3.0F) {}
        }
        else
        {
          ((Path)localObject1).quadTo(f5, f4, (f6 + f5) / 2.0F, (f3 + f4) / 2.0F);
          f1 = f6;
          f2 = f3;
          localObject2 = localObject1;
        }
      }
    }
    this.mCachedPath = ((Path)localObject1);
  }
  
  public void clearPath()
  {
    if (this.mCachedPath != null) {
      this.mCachedPath.rewind();
    }
  }
  
  public Object clone()
  {
    return new GestureStroke(this.boundingBox, this.length, this.points, this.timestamps);
  }
  
  public OrientedBoundingBox computeOrientedBoundingBox()
  {
    return GestureUtils.computeOrientedBoundingBox(this.points);
  }
  
  void draw(Canvas paramCanvas, Paint paramPaint)
  {
    if (this.mCachedPath == null) {
      makePath();
    }
    paramCanvas.drawPath(this.mCachedPath, paramPaint);
  }
  
  public Path getPath()
  {
    if (this.mCachedPath == null) {
      makePath();
    }
    return this.mCachedPath;
  }
  
  void serialize(DataOutputStream paramDataOutputStream)
    throws IOException
  {
    float[] arrayOfFloat = this.points;
    long[] arrayOfLong = this.timestamps;
    int j = this.points.length;
    paramDataOutputStream.writeInt(j / 2);
    int i = 0;
    while (i < j)
    {
      paramDataOutputStream.writeFloat(arrayOfFloat[i]);
      paramDataOutputStream.writeFloat(arrayOfFloat[(i + 1)]);
      paramDataOutputStream.writeLong(arrayOfLong[(i / 2)]);
      i += 2;
    }
  }
  
  public Path toPath(float paramFloat1, float paramFloat2, int paramInt)
  {
    float[] arrayOfFloat = GestureUtils.temporalSampling(this, paramInt);
    Object localObject1 = this.boundingBox;
    GestureUtils.translate(arrayOfFloat, -((RectF)localObject1).left, -((RectF)localObject1).top);
    paramFloat1 /= ((RectF)localObject1).width();
    paramFloat2 /= ((RectF)localObject1).height();
    float f3;
    float f2;
    label79:
    float f4;
    float f1;
    Object localObject2;
    if (paramFloat1 > paramFloat2)
    {
      paramFloat1 = paramFloat2;
      GestureUtils.scale(arrayOfFloat, paramFloat1, paramFloat1);
      f3 = 0.0F;
      f2 = 0.0F;
      localObject1 = null;
      int i = arrayOfFloat.length;
      paramInt = 0;
      if (paramInt >= i) {
        break label228;
      }
      f4 = arrayOfFloat[paramInt];
      f1 = arrayOfFloat[(paramInt + 1)];
      if (localObject1 != null) {
        break label148;
      }
      localObject2 = new Path();
      ((Path)localObject2).moveTo(f4, f1);
      paramFloat1 = f4;
      paramFloat2 = f1;
    }
    for (;;)
    {
      paramInt += 2;
      f3 = paramFloat1;
      f2 = paramFloat2;
      localObject1 = localObject2;
      break label79;
      break;
      label148:
      paramFloat1 = Math.abs(f4 - f3);
      float f5 = Math.abs(f1 - f2);
      if (paramFloat1 < 3.0F)
      {
        paramFloat1 = f3;
        paramFloat2 = f2;
        localObject2 = localObject1;
        if (f5 < 3.0F) {}
      }
      else
      {
        ((Path)localObject1).quadTo(f3, f2, (f4 + f3) / 2.0F, (f1 + f2) / 2.0F);
        paramFloat1 = f4;
        paramFloat2 = f1;
        localObject2 = localObject1;
      }
    }
    label228:
    return (Path)localObject1;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/gesture/GestureStroke.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */