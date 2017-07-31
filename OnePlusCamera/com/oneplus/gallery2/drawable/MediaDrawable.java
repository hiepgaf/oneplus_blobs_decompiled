package com.oneplus.gallery2.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.os.Handler;
import android.util.Size;
import com.oneplus.base.Handle;
import com.oneplus.gallery2.media.Media;
import com.oneplus.gallery2.media.Media.SizeCallback;
import com.oneplus.gallery2.media.MediaChangeCallback;
import com.oneplus.gallery2.media.MediaSource;

public class MediaDrawable
  extends Drawable
  implements Drawable.Callback
{
  private static final Size EMPTY_SIZE = new Size(-1, -1);
  private Drawable m_Drawable;
  private Handler m_Handler = new Handler();
  private Media m_Media;
  private final MediaChangeCallback m_MediaChangeCallback = new MediaChangeCallback()
  {
    public void onMediaUpdated(MediaSource paramAnonymousMediaSource, Media paramAnonymousMedia, int paramAnonymousInt)
    {
      MediaDrawable.this.onMediaUpdated(paramAnonymousMedia, paramAnonymousInt);
    }
  };
  private Handle m_MediaChangeCallbackHandle;
  private Size m_MediaSize = EMPTY_SIZE;
  
  public MediaDrawable(Media paramMedia)
  {
    this(paramMedia, null);
  }
  
  public MediaDrawable(Media paramMedia, int paramInt)
  {
    setMedia(paramMedia);
    this.m_Drawable = new ColorDrawable(paramInt);
    this.m_Drawable.setCallback(this);
  }
  
  public MediaDrawable(Media paramMedia, Resources paramResources, Bitmap paramBitmap)
  {
    setMedia(paramMedia);
    this.m_Drawable = new BitmapDrawable(paramResources, paramBitmap);
    this.m_Drawable.setCallback(this);
  }
  
  public MediaDrawable(Media paramMedia, Drawable paramDrawable)
  {
    setMedia(paramMedia);
    this.m_Drawable = paramDrawable;
    this.m_Drawable.setCallback(this);
  }
  
  public MediaDrawable(Media paramMedia, Size paramSize)
  {
    setMedia(paramMedia, paramSize);
  }
  
  private void onMediaUpdated(Media paramMedia, int paramInt)
  {
    if ((Media.FLAG_SIZE_CHANGED & paramInt) != 0)
    {
      updateMediaSize(null);
      return;
    }
  }
  
  private void resetDrawable()
  {
    if (this.m_Drawable != null)
    {
      this.m_Drawable.setCallback(null);
      this.m_Drawable = null;
      return;
    }
  }
  
  private void updateMediaSize(Size paramSize)
  {
    if (this.m_Media != null)
    {
      Size localSize = this.m_Media.peekSize();
      if (localSize == null) {
        break label35;
      }
      this.m_MediaSize = localSize;
    }
    label35:
    do
    {
      return;
      this.m_MediaSize = EMPTY_SIZE;
      return;
      this.m_Media.getSize(new Media.SizeCallback()
      {
        public void onSizeObtained(Media paramAnonymousMedia, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          if (paramAnonymousMedia.equals(MediaDrawable.this.m_Media))
          {
            MediaDrawable.this.m_MediaSize = new Size(paramAnonymousInt1, paramAnonymousInt2);
            return;
          }
        }
      });
    } while (paramSize == null);
    this.m_MediaSize = new Size(paramSize.getWidth(), paramSize.getHeight());
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.m_Drawable != null)
    {
      this.m_Drawable.draw(paramCanvas);
      return;
    }
  }
  
  public int getIntrinsicHeight()
  {
    return this.m_MediaSize.getHeight();
  }
  
  public int getIntrinsicWidth()
  {
    return this.m_MediaSize.getWidth();
  }
  
  public int getOpacity()
  {
    if (this.m_Drawable != null) {
      return this.m_Drawable.getOpacity();
    }
    return 0;
  }
  
  public void invalidateDrawable(Drawable paramDrawable)
  {
    if (this.m_Drawable != null)
    {
      invalidateSelf();
      return;
    }
  }
  
  public void release()
  {
    reset();
    this.m_Handler = null;
  }
  
  public void reset()
  {
    if (this.m_Media == null) {}
    for (;;)
    {
      resetDrawable();
      return;
      this.m_MediaChangeCallbackHandle = Handle.close(this.m_MediaChangeCallbackHandle);
      this.m_Media = null;
      updateMediaSize(null);
    }
  }
  
  public void scheduleDrawable(Drawable paramDrawable, Runnable paramRunnable, long paramLong)
  {
    if (this.m_Drawable != null)
    {
      if (paramDrawable == this.m_Drawable) {}
    }
    else {
      return;
    }
    this.m_Handler.postAtTime(paramRunnable, paramLong);
  }
  
  public void setAlpha(int paramInt)
  {
    if (this.m_Drawable != null)
    {
      this.m_Drawable.setAlpha(paramInt);
      return;
    }
  }
  
  public void setBitmap(Resources paramResources, Bitmap paramBitmap)
  {
    resetDrawable();
    if (paramBitmap == null) {
      this.m_Drawable = null;
    }
    for (;;)
    {
      invalidateSelf();
      return;
      this.m_Drawable = new BitmapDrawable(paramResources, paramBitmap);
      this.m_Drawable.setCallback(this);
    }
  }
  
  public void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.m_Drawable != null)
    {
      this.m_Drawable.setBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
  }
  
  public void setBounds(Rect paramRect)
  {
    if (paramRect != null)
    {
      setBounds(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
      return;
    }
  }
  
  public void setColor(int paramInt)
  {
    resetDrawable();
    this.m_Drawable = new ColorDrawable(paramInt);
    this.m_Drawable.setCallback(this);
    invalidateSelf();
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    if (this.m_Drawable != null)
    {
      this.m_Drawable.setColorFilter(paramColorFilter);
      return;
    }
  }
  
  public void setDrawable(Drawable paramDrawable)
  {
    resetDrawable();
    this.m_Drawable = paramDrawable;
    if (this.m_Drawable == null) {}
    for (;;)
    {
      invalidateSelf();
      return;
      this.m_Drawable.setCallback(this);
    }
  }
  
  public void setMedia(Media paramMedia)
  {
    setMedia(paramMedia, null);
  }
  
  public void setMedia(Media paramMedia, Size paramSize)
  {
    reset();
    if (paramMedia == null) {
      return;
    }
    this.m_Media = paramMedia;
    this.m_MediaChangeCallbackHandle = this.m_Media.getSource().addMediaChangedCallback(this.m_MediaChangeCallback);
    updateMediaSize(paramSize);
  }
  
  public void unscheduleDrawable(Drawable paramDrawable, Runnable paramRunnable)
  {
    if (this.m_Drawable != null)
    {
      if (paramDrawable == this.m_Drawable) {}
    }
    else {
      return;
    }
    this.m_Handler.removeCallbacks(paramRunnable);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/drawable/MediaDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */