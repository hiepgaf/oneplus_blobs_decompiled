package android.support.v4.graphics.drawable;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.Rect;
import android.view.Gravity;

class RoundedBitmapDrawable21
  extends RoundedBitmapDrawable
{
  protected RoundedBitmapDrawable21(Resources paramResources, Bitmap paramBitmap)
  {
    super(paramResources, paramBitmap);
  }
  
  public void getOutline(Outline paramOutline)
  {
    updateDstRect();
    paramOutline.setRoundRect(this.mDstRect, getCornerRadius());
  }
  
  void gravityCompatApply(int paramInt1, int paramInt2, int paramInt3, Rect paramRect1, Rect paramRect2)
  {
    Gravity.apply(paramInt1, paramInt2, paramInt3, paramRect1, paramRect2, 0);
  }
  
  public boolean hasMipMap()
  {
    if (this.mBitmap == null) {}
    while (!this.mBitmap.hasMipMap()) {
      return false;
    }
    return true;
  }
  
  public void setMipMap(boolean paramBoolean)
  {
    if (this.mBitmap == null) {
      return;
    }
    this.mBitmap.setHasMipMap(paramBoolean);
    invalidateSelf();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/graphics/drawable/RoundedBitmapDrawable21.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */