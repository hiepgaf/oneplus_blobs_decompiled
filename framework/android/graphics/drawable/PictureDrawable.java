package android.graphics.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Picture;
import android.graphics.Rect;

public class PictureDrawable
  extends Drawable
{
  private Picture mPicture;
  
  public PictureDrawable(Picture paramPicture)
  {
    this.mPicture = paramPicture;
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.mPicture != null)
    {
      Rect localRect = getBounds();
      paramCanvas.save();
      paramCanvas.clipRect(localRect);
      paramCanvas.translate(localRect.left, localRect.top);
      paramCanvas.drawPicture(this.mPicture);
      paramCanvas.restore();
    }
  }
  
  public int getIntrinsicHeight()
  {
    if (this.mPicture != null) {
      return this.mPicture.getHeight();
    }
    return -1;
  }
  
  public int getIntrinsicWidth()
  {
    if (this.mPicture != null) {
      return this.mPicture.getWidth();
    }
    return -1;
  }
  
  public int getOpacity()
  {
    return -3;
  }
  
  public Picture getPicture()
  {
    return this.mPicture;
  }
  
  public void setAlpha(int paramInt) {}
  
  public void setColorFilter(ColorFilter paramColorFilter) {}
  
  public void setPicture(Picture paramPicture)
  {
    this.mPicture = paramPicture;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/drawable/PictureDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */