package com.oneplus.camera;

import android.util.Size;
import com.oneplus.base.EventArgs;
import com.oneplus.base.Handle;
import com.oneplus.base.RecyclableObject;
import com.oneplus.camera.media.ImagePlane;
import java.util.ArrayDeque;
import java.util.Queue;

public class CameraCaptureEventArgs
  extends EventArgs
  implements RecyclableObject
{
  private static final Queue<CameraCaptureEventArgs> POOL = new ArrayDeque(8);
  private static final int POOL_SIZE = 8;
  private volatile Object m_CaptureResult;
  private volatile int m_Flags;
  private volatile int m_FrameIndex;
  private volatile Handle m_Handle;
  private volatile boolean m_IsFreeInstance;
  private volatile int m_PictureFormat;
  private volatile String m_PictureId;
  private volatile ImagePlane[] m_PicturePlanes;
  private volatile Size m_PictureSize;
  private volatile long m_TakenTime;
  
  public static CameraCaptureEventArgs obtain(Handle paramHandle, String paramString, int paramInt)
  {
    try
    {
      paramHandle = obtain(paramHandle, paramString, paramInt, 0, null, null, null, System.currentTimeMillis());
      return paramHandle;
    }
    finally
    {
      paramHandle = finally;
      throw paramHandle;
    }
  }
  
  public static CameraCaptureEventArgs obtain(Handle paramHandle, String paramString, int paramInt1, int paramInt2)
  {
    try
    {
      paramHandle = obtain(paramHandle, paramString, paramInt1, 0, null, null, null, System.currentTimeMillis(), paramInt2);
      return paramHandle;
    }
    finally
    {
      paramHandle = finally;
      throw paramHandle;
    }
  }
  
  public static CameraCaptureEventArgs obtain(Handle paramHandle, String paramString, int paramInt1, int paramInt2, Size paramSize, ImagePlane[] paramArrayOfImagePlane, Object paramObject, long paramLong)
  {
    try
    {
      paramHandle = obtain(paramHandle, paramString, paramInt1, paramInt2, paramSize, paramArrayOfImagePlane, paramObject, paramLong, 0);
      return paramHandle;
    }
    finally
    {
      paramHandle = finally;
      throw paramHandle;
    }
  }
  
  /* Error */
  public static CameraCaptureEventArgs obtain(Handle paramHandle, String paramString, int paramInt1, int paramInt2, Size paramSize, ImagePlane[] paramArrayOfImagePlane, Object paramObject, long paramLong, int paramInt3)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 39	com/oneplus/camera/CameraCaptureEventArgs:POOL	Ljava/util/Queue;
    //   6: invokeinterface 63 1 0
    //   11: checkcast 2	com/oneplus/camera/CameraCaptureEventArgs
    //   14: astore 10
    //   16: aload 10
    //   18: ifnull +74 -> 92
    //   21: aload 10
    //   23: iconst_0
    //   24: putfield 65	com/oneplus/camera/CameraCaptureEventArgs:m_IsFreeInstance	Z
    //   27: aload 10
    //   29: aload 6
    //   31: putfield 67	com/oneplus/camera/CameraCaptureEventArgs:m_CaptureResult	Ljava/lang/Object;
    //   34: aload 10
    //   36: aload_0
    //   37: putfield 69	com/oneplus/camera/CameraCaptureEventArgs:m_Handle	Lcom/oneplus/base/Handle;
    //   40: aload 10
    //   42: iload 9
    //   44: putfield 71	com/oneplus/camera/CameraCaptureEventArgs:m_Flags	I
    //   47: aload 10
    //   49: iload_2
    //   50: putfield 73	com/oneplus/camera/CameraCaptureEventArgs:m_FrameIndex	I
    //   53: aload 10
    //   55: aload_1
    //   56: putfield 75	com/oneplus/camera/CameraCaptureEventArgs:m_PictureId	Ljava/lang/String;
    //   59: aload 10
    //   61: aload 5
    //   63: putfield 77	com/oneplus/camera/CameraCaptureEventArgs:m_PicturePlanes	[Lcom/oneplus/camera/media/ImagePlane;
    //   66: aload 10
    //   68: iload_3
    //   69: putfield 79	com/oneplus/camera/CameraCaptureEventArgs:m_PictureFormat	I
    //   72: aload 10
    //   74: aload 4
    //   76: putfield 81	com/oneplus/camera/CameraCaptureEventArgs:m_PictureSize	Landroid/util/Size;
    //   79: aload 10
    //   81: lload 7
    //   83: putfield 83	com/oneplus/camera/CameraCaptureEventArgs:m_TakenTime	J
    //   86: ldc 2
    //   88: monitorexit
    //   89: aload 10
    //   91: areturn
    //   92: new 2	com/oneplus/camera/CameraCaptureEventArgs
    //   95: dup
    //   96: invokespecial 84	com/oneplus/camera/CameraCaptureEventArgs:<init>	()V
    //   99: astore 10
    //   101: goto -74 -> 27
    //   104: astore_0
    //   105: ldc 2
    //   107: monitorexit
    //   108: aload_0
    //   109: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	110	0	paramHandle	Handle
    //   0	110	1	paramString	String
    //   0	110	2	paramInt1	int
    //   0	110	3	paramInt2	int
    //   0	110	4	paramSize	Size
    //   0	110	5	paramArrayOfImagePlane	ImagePlane[]
    //   0	110	6	paramObject	Object
    //   0	110	7	paramLong	long
    //   0	110	9	paramInt3	int
    //   14	86	10	localCameraCaptureEventArgs	CameraCaptureEventArgs
    // Exception table:
    //   from	to	target	type
    //   3	16	104	finally
    //   21	27	104	finally
    //   27	86	104	finally
    //   92	101	104	finally
  }
  
  public final void clearImagePlane()
  {
    this.m_PicturePlanes = null;
  }
  
  public CameraCaptureEventArgs clone()
  {
    Size localSize = null;
    Object localObject1 = null;
    if (this.m_PicturePlanes != null)
    {
      localObject2 = new ImagePlane[this.m_PicturePlanes.length];
      i = 0;
      for (;;)
      {
        localObject1 = localObject2;
        if (i >= localObject2.length) {
          break;
        }
        localObject2[i] = this.m_PicturePlanes[i].clone();
        i += 1;
      }
    }
    Object localObject2 = this.m_Handle;
    String str = this.m_PictureId;
    int i = this.m_FrameIndex;
    int j = this.m_PictureFormat;
    if (this.m_PictureSize != null) {
      localSize = new Size(this.m_PictureSize.getWidth(), this.m_PictureSize.getHeight());
    }
    return obtain((Handle)localObject2, str, i, j, localSize, (ImagePlane[])localObject1, this.m_CaptureResult, this.m_TakenTime);
  }
  
  public final Object getCaptureResult()
  {
    return this.m_CaptureResult;
  }
  
  public final int getFlags()
  {
    return this.m_Flags;
  }
  
  public final int getFrameIndex()
  {
    return this.m_FrameIndex;
  }
  
  public final Handle getHandle()
  {
    return this.m_Handle;
  }
  
  public final int getPictureFormat()
  {
    return this.m_PictureFormat;
  }
  
  public final String getPictureId()
  {
    return this.m_PictureId;
  }
  
  public final ImagePlane[] getPicturePlanes()
  {
    return this.m_PicturePlanes;
  }
  
  public final Size getPictureSize()
  {
    return this.m_PictureSize;
  }
  
  public final long getTakenTime()
  {
    return this.m_TakenTime;
  }
  
  public void recycle()
  {
    try
    {
      if ((!this.m_IsFreeInstance) && (POOL.size() < 8))
      {
        this.m_CaptureResult = null;
        this.m_Handle = null;
        this.m_Flags = 0;
        this.m_FrameIndex = -1;
        this.m_PictureId = null;
        this.m_PicturePlanes = null;
        this.m_PictureFormat = 0;
        this.m_PictureSize = null;
        this.m_IsFreeInstance = true;
        POOL.add(this);
      }
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraCaptureEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */