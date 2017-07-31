package android.graphics;

public final class Outline
{
  public static final int MODE_CONVEX_PATH = 2;
  public static final int MODE_EMPTY = 0;
  public static final int MODE_ROUND_RECT = 1;
  private static final float RADIUS_UNDEFINED = Float.NEGATIVE_INFINITY;
  public float mAlpha;
  public int mMode = 0;
  public final Path mPath = new Path();
  public float mRadius = Float.NEGATIVE_INFINITY;
  public final Rect mRect = new Rect();
  
  public Outline() {}
  
  public Outline(Outline paramOutline)
  {
    set(paramOutline);
  }
  
  public boolean canClip()
  {
    return this.mMode != 2;
  }
  
  public float getAlpha()
  {
    return this.mAlpha;
  }
  
  public float getRadius()
  {
    return this.mRadius;
  }
  
  public boolean getRect(Rect paramRect)
  {
    if (this.mMode != 1) {
      return false;
    }
    paramRect.set(this.mRect);
    return true;
  }
  
  public boolean isEmpty()
  {
    boolean bool = false;
    if (this.mMode == 0) {
      bool = true;
    }
    return bool;
  }
  
  public void offset(int paramInt1, int paramInt2)
  {
    if (this.mMode == 1) {
      this.mRect.offset(paramInt1, paramInt2);
    }
    while (this.mMode != 2) {
      return;
    }
    this.mPath.offset(paramInt1, paramInt2);
  }
  
  public void set(Outline paramOutline)
  {
    this.mMode = paramOutline.mMode;
    this.mPath.set(paramOutline.mPath);
    this.mRect.set(paramOutline.mRect);
    this.mRadius = paramOutline.mRadius;
    this.mAlpha = paramOutline.mAlpha;
  }
  
  public void setAlpha(float paramFloat)
  {
    this.mAlpha = paramFloat;
  }
  
  public void setConvexPath(Path paramPath)
  {
    if (paramPath.isEmpty())
    {
      setEmpty();
      return;
    }
    if (!paramPath.isConvex()) {
      throw new IllegalArgumentException("path must be convex");
    }
    this.mMode = 2;
    this.mPath.set(paramPath);
    this.mRect.setEmpty();
    this.mRadius = Float.NEGATIVE_INFINITY;
  }
  
  public void setEmpty()
  {
    this.mMode = 0;
    this.mPath.rewind();
    this.mRect.setEmpty();
    this.mRadius = Float.NEGATIVE_INFINITY;
  }
  
  public void setOval(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt1 >= paramInt3) || (paramInt2 >= paramInt4))
    {
      setEmpty();
      return;
    }
    if (paramInt4 - paramInt2 == paramInt3 - paramInt1)
    {
      setRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, (paramInt4 - paramInt2) / 2.0F);
      return;
    }
    this.mMode = 2;
    this.mPath.rewind();
    this.mPath.addOval(paramInt1, paramInt2, paramInt3, paramInt4, Path.Direction.CW);
    this.mRect.setEmpty();
    this.mRadius = Float.NEGATIVE_INFINITY;
  }
  
  public void setOval(Rect paramRect)
  {
    setOval(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public void setRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setRoundRect(paramInt1, paramInt2, paramInt3, paramInt4, 0.0F);
  }
  
  public void setRect(Rect paramRect)
  {
    setRect(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public void setRoundRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat)
  {
    if ((paramInt1 >= paramInt3) || (paramInt2 >= paramInt4))
    {
      setEmpty();
      return;
    }
    this.mMode = 1;
    this.mRect.set(paramInt1, paramInt2, paramInt3, paramInt4);
    this.mRadius = paramFloat;
    this.mPath.rewind();
  }
  
  public void setRoundRect(Rect paramRect, float paramFloat)
  {
    setRoundRect(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom, paramFloat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/Outline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */