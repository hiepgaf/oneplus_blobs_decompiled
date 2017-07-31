package android.content;

public class MutableContextWrapper
  extends ContextWrapper
{
  public MutableContextWrapper(Context paramContext)
  {
    super(paramContext);
  }
  
  public void setBaseContext(Context paramContext)
  {
    this.mBase = paramContext;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/MutableContextWrapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */