package android.content.res;

public abstract class ComplexColor
{
  private int mChangingConfigurations;
  
  public abstract boolean canApplyTheme();
  
  public int getChangingConfigurations()
  {
    return this.mChangingConfigurations;
  }
  
  public abstract ConstantState<ComplexColor> getConstantState();
  
  public abstract int getDefaultColor();
  
  public boolean isStateful()
  {
    return false;
  }
  
  public abstract ComplexColor obtainForTheme(Resources.Theme paramTheme);
  
  final void setBaseChangingConfigurations(int paramInt)
  {
    this.mChangingConfigurations = paramInt;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/ComplexColor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */