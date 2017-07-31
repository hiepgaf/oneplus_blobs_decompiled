package com.oneplus.camera;

import android.graphics.PointF;
import com.oneplus.base.EventArgs;
import com.oneplus.base.RecyclableObject;
import java.util.ArrayDeque;
import java.util.Queue;

public class MotionVectorEventArgs
  extends EventArgs
  implements RecyclableObject
{
  private static final Queue<MotionVectorEventArgs> POOL = new ArrayDeque(8);
  private static final int POOL_SIZE = 8;
  private volatile boolean m_IsRecycled;
  private volatile PointF[][] m_Vectors;
  
  MotionVectorEventArgs(PointF[][] paramArrayOfPointF)
  {
    this.m_Vectors = paramArrayOfPointF;
  }
  
  public static MotionVectorEventArgs obtain(PointF[][] paramArrayOfPointF)
  {
    MotionVectorEventArgs localMotionVectorEventArgs = (MotionVectorEventArgs)POOL.poll();
    if (localMotionVectorEventArgs == null) {
      return new MotionVectorEventArgs(paramArrayOfPointF);
    }
    localMotionVectorEventArgs.m_IsRecycled = false;
    return localMotionVectorEventArgs;
  }
  
  public PointF[][] getVectors()
  {
    if (this.m_IsRecycled) {
      throw new IllegalStateException("Current event args is already recycled");
    }
    return this.m_Vectors;
  }
  
  public void recycle()
  {
    if (POOL.size() < 8)
    {
      this.m_IsRecycled = true;
      this.m_Vectors = null;
      POOL.add(this);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/MotionVectorEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */