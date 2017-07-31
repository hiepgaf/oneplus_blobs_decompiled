package android.media;

import android.graphics.Rect;
import java.nio.ByteBuffer;

public abstract class Image
  implements AutoCloseable
{
  private Rect mCropRect;
  protected boolean mIsImageValid = false;
  
  public abstract void close();
  
  public Rect getCropRect()
  {
    throwISEIfImageIsInvalid();
    if (this.mCropRect == null) {
      return new Rect(0, 0, getWidth(), getHeight());
    }
    return new Rect(this.mCropRect);
  }
  
  public abstract int getFormat();
  
  public abstract int getHeight();
  
  long getNativeContext()
  {
    throwISEIfImageIsInvalid();
    return 0L;
  }
  
  Object getOwner()
  {
    throwISEIfImageIsInvalid();
    return null;
  }
  
  public abstract Plane[] getPlanes();
  
  public abstract long getTimestamp();
  
  public abstract int getWidth();
  
  boolean isAttachable()
  {
    throwISEIfImageIsInvalid();
    return false;
  }
  
  public void setCropRect(Rect paramRect)
  {
    throwISEIfImageIsInvalid();
    Rect localRect = paramRect;
    if (paramRect != null)
    {
      paramRect = new Rect(paramRect);
      if (paramRect.intersect(0, 0, getWidth(), getHeight())) {
        break label48;
      }
      paramRect.setEmpty();
    }
    label48:
    for (localRect = paramRect;; localRect = paramRect)
    {
      this.mCropRect = localRect;
      return;
    }
  }
  
  public void setTimestamp(long paramLong)
  {
    throwISEIfImageIsInvalid();
  }
  
  protected void throwISEIfImageIsInvalid()
  {
    if (!this.mIsImageValid) {
      throw new IllegalStateException("Image is already closed");
    }
  }
  
  public static abstract class Plane
  {
    public abstract ByteBuffer getBuffer();
    
    public abstract int getPixelStride();
    
    public abstract int getRowStride();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Image.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */