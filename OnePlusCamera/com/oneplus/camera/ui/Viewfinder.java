package com.oneplus.camera.ui;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Size;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface Viewfinder
  extends Component
{
  public static final int FLAG_NO_BOUNDS_CHECKING = 1;
  public static final PropertyKey<Boolean> PROP_IS_DISPLAY_PREVIEW_FRAME_COPY_SUPPORTED = new PropertyKey("IsPreviewFrameCopySupported", Boolean.class, Viewfinder.class, Boolean.valueOf(false));
  public static final PropertyKey<RectF> PROP_PREVIEW_BOUNDS = new PropertyKey("PreviewBounds", RectF.class, Viewfinder.class, new RectF());
  public static final PropertyKey<Size> PROP_PREVIEW_CONTAINER_SIZE = new PropertyKey("PreviewContainerSize", Size.class, Viewfinder.class, new Size(0, 0));
  public static final PropertyKey<Object> PROP_PREVIEW_RECEIVER = new PropertyKey("PreviewReceiver", Object.class, Viewfinder.class, 1, null);
  public static final PropertyKey<PreviewRenderingMode> PROP_PREVIEW_RENDERING_MODE = new PropertyKey("PreviewRenderingMode", PreviewRenderingMode.class, Viewfinder.class, PreviewRenderingMode.DIRECT);
  
  public abstract boolean copyDisplayPreviewFrame(Bitmap paramBitmap, int paramInt);
  
  public abstract boolean pointFromPreview(float paramFloat1, float paramFloat2, PointF paramPointF, int paramInt);
  
  public abstract boolean pointToPreview(float paramFloat1, float paramFloat2, PointF paramPointF, int paramInt);
  
  public abstract void setPreferredPreviewBounds(RectF paramRectF, int paramInt);
  
  public static enum PreviewRenderingMode
  {
    DIRECT,  OPENGL;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/Viewfinder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */