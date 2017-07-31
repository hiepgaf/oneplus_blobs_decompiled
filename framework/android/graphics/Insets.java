package android.graphics;

public class Insets
{
  public static final Insets NONE = new Insets(0, 0, 0, 0);
  public final int bottom;
  public final int left;
  public final int right;
  public final int top;
  
  private Insets(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.left = paramInt1;
    this.top = paramInt2;
    this.right = paramInt3;
    this.bottom = paramInt4;
  }
  
  public static Insets of(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((paramInt1 == 0) && (paramInt2 == 0) && (paramInt3 == 0) && (paramInt4 == 0)) {
      return NONE;
    }
    return new Insets(paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public static Insets of(Rect paramRect)
  {
    if (paramRect == null) {
      return NONE;
    }
    return of(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (Insets)paramObject;
    if (this.bottom != ((Insets)paramObject).bottom) {
      return false;
    }
    if (this.left != ((Insets)paramObject).left) {
      return false;
    }
    if (this.right != ((Insets)paramObject).right) {
      return false;
    }
    return this.top == ((Insets)paramObject).top;
  }
  
  public int hashCode()
  {
    return ((this.left * 31 + this.top) * 31 + this.right) * 31 + this.bottom;
  }
  
  public String toString()
  {
    return "Insets{left=" + this.left + ", top=" + this.top + ", right=" + this.right + ", bottom=" + this.bottom + '}';
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/Insets.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */