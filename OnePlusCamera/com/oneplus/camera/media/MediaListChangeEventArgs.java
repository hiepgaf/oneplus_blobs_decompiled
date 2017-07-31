package com.oneplus.camera.media;

import com.oneplus.base.EventArgs;
import com.oneplus.base.RecyclableObject;
import java.util.ArrayDeque;
import java.util.Queue;

public class MediaListChangeEventArgs
  extends EventArgs
  implements RecyclableObject
{
  private static final Queue<MediaListChangeEventArgs> POOL = new ArrayDeque(16);
  private static final int POOL_SIZE = 16;
  private volatile int m_EndIndex;
  private volatile boolean m_IsFreeInstance;
  private volatile int m_StartIndex;
  
  private MediaListChangeEventArgs(int paramInt1, int paramInt2)
  {
    this.m_StartIndex = paramInt1;
    this.m_EndIndex = paramInt2;
  }
  
  public static MediaListChangeEventArgs obtain(int paramInt)
  {
    return obtain(paramInt, paramInt);
  }
  
  /* Error */
  public static MediaListChangeEventArgs obtain(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 26	com/oneplus/camera/media/MediaListChangeEventArgs:POOL	Ljava/util/Queue;
    //   6: invokeinterface 45 1 0
    //   11: checkcast 2	com/oneplus/camera/media/MediaListChangeEventArgs
    //   14: astore_2
    //   15: aload_2
    //   16: ifnull +23 -> 39
    //   19: aload_2
    //   20: iload_0
    //   21: putfield 32	com/oneplus/camera/media/MediaListChangeEventArgs:m_StartIndex	I
    //   24: aload_2
    //   25: iload_1
    //   26: putfield 34	com/oneplus/camera/media/MediaListChangeEventArgs:m_EndIndex	I
    //   29: aload_2
    //   30: iconst_0
    //   31: putfield 47	com/oneplus/camera/media/MediaListChangeEventArgs:m_IsFreeInstance	Z
    //   34: ldc 2
    //   36: monitorexit
    //   37: aload_2
    //   38: areturn
    //   39: new 2	com/oneplus/camera/media/MediaListChangeEventArgs
    //   42: dup
    //   43: iload_0
    //   44: iload_1
    //   45: invokespecial 49	com/oneplus/camera/media/MediaListChangeEventArgs:<init>	(II)V
    //   48: astore_2
    //   49: goto -15 -> 34
    //   52: astore_2
    //   53: ldc 2
    //   55: monitorexit
    //   56: aload_2
    //   57: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	58	0	paramInt1	int
    //   0	58	1	paramInt2	int
    //   14	35	2	localMediaListChangeEventArgs	MediaListChangeEventArgs
    //   52	5	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   3	15	52	finally
    //   19	34	52	finally
    //   39	49	52	finally
  }
  
  public int getEndIndex()
  {
    return this.m_EndIndex;
  }
  
  public int getItemCount()
  {
    return Math.max(0, this.m_EndIndex - this.m_StartIndex + 1);
  }
  
  public int getStartIndex()
  {
    return this.m_StartIndex;
  }
  
  public void recycle()
  {
    try
    {
      boolean bool = this.m_IsFreeInstance;
      if (bool) {
        return;
      }
      this.m_IsFreeInstance = false;
      if (POOL.size() < 16) {
        POOL.add(this);
      }
      return;
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/MediaListChangeEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */