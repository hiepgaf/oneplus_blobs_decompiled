package com.oneplus.camera.ui;

import android.graphics.Canvas;
import android.graphics.RectF;
import com.oneplus.base.Handle;
import com.oneplus.base.RecyclableObject;
import com.oneplus.base.component.Component;
import java.util.ArrayDeque;

public abstract interface CameraPreviewOverlay
  extends Component
{
  public abstract Handle addRenderer(Renderer paramRenderer, int paramInt);
  
  public abstract void invalidateCameraPreviewOverlay();
  
  public static abstract interface Renderer
  {
    public abstract void onRender(Canvas paramCanvas, CameraPreviewOverlay.RenderingParams paramRenderingParams);
  }
  
  public static class RenderingParams
    implements RecyclableObject
  {
    private static final ArrayDeque<RenderingParams> POOL = new ArrayDeque(2);
    private static final int POOL_SIZE = 2;
    private volatile boolean m_IsFreeInstance;
    private volatile RectF m_PreviewBounds;
    
    /* Error */
    public static RenderingParams obtain(RectF paramRectF)
    {
      // Byte code:
      //   0: ldc 2
      //   2: monitorenter
      //   3: getstatic 29	com/oneplus/camera/ui/CameraPreviewOverlay$RenderingParams:POOL	Ljava/util/ArrayDeque;
      //   6: invokevirtual 38	java/util/ArrayDeque:pollLast	()Ljava/lang/Object;
      //   9: checkcast 2	com/oneplus/camera/ui/CameraPreviewOverlay$RenderingParams
      //   12: astore_1
      //   13: aload_1
      //   14: ifnull +18 -> 32
      //   17: aload_1
      //   18: iconst_0
      //   19: putfield 40	com/oneplus/camera/ui/CameraPreviewOverlay$RenderingParams:m_IsFreeInstance	Z
      //   22: aload_1
      //   23: aload_0
      //   24: putfield 42	com/oneplus/camera/ui/CameraPreviewOverlay$RenderingParams:m_PreviewBounds	Landroid/graphics/RectF;
      //   27: ldc 2
      //   29: monitorexit
      //   30: aload_1
      //   31: areturn
      //   32: new 2	com/oneplus/camera/ui/CameraPreviewOverlay$RenderingParams
      //   35: dup
      //   36: invokespecial 43	com/oneplus/camera/ui/CameraPreviewOverlay$RenderingParams:<init>	()V
      //   39: astore_1
      //   40: goto -18 -> 22
      //   43: astore_0
      //   44: ldc 2
      //   46: monitorexit
      //   47: aload_0
      //   48: athrow
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	49	0	paramRectF	RectF
      //   12	28	1	localRenderingParams	RenderingParams
      // Exception table:
      //   from	to	target	type
      //   3	13	43	finally
      //   17	22	43	finally
      //   22	27	43	finally
      //   32	40	43	finally
    }
    
    public RectF getPreviewBounds()
    {
      return this.m_PreviewBounds;
    }
    
    public void recycle()
    {
      try
      {
        boolean bool = this.m_IsFreeInstance;
        if (bool) {
          return;
        }
        this.m_PreviewBounds = null;
        this.m_IsFreeInstance = true;
        if (POOL.size() < 2) {
          POOL.addLast(this);
        }
        return;
      }
      finally {}
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/CameraPreviewOverlay.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */