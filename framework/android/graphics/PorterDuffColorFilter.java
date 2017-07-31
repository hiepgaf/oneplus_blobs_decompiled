package android.graphics;

public class PorterDuffColorFilter
  extends ColorFilter
{
  private int mColor;
  private PorterDuff.Mode mMode;
  
  public PorterDuffColorFilter(int paramInt, PorterDuff.Mode paramMode)
  {
    this.mColor = paramInt;
    this.mMode = paramMode;
    update();
  }
  
  private static native long native_CreatePorterDuffFilter(int paramInt1, int paramInt2);
  
  private void update()
  {
    destroyFilter(this.native_instance);
    this.native_instance = native_CreatePorterDuffFilter(this.mColor, this.mMode.nativeInt);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (PorterDuffColorFilter)paramObject;
    return (this.mColor == ((PorterDuffColorFilter)paramObject).mColor) && (this.mMode == ((PorterDuffColorFilter)paramObject).mMode);
  }
  
  public int getColor()
  {
    return this.mColor;
  }
  
  public PorterDuff.Mode getMode()
  {
    return this.mMode;
  }
  
  public int hashCode()
  {
    return this.mMode.hashCode() * 31 + this.mColor;
  }
  
  public void setColor(int paramInt)
  {
    this.mColor = paramInt;
    update();
  }
  
  public void setMode(PorterDuff.Mode paramMode)
  {
    this.mMode = paramMode;
    update();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/graphics/PorterDuffColorFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */