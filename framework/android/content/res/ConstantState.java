package android.content.res;

public abstract class ConstantState<T>
{
  public abstract int getChangingConfigurations();
  
  public abstract T newInstance();
  
  public T newInstance(Resources paramResources)
  {
    return (T)newInstance();
  }
  
  public T newInstance(Resources paramResources, Resources.Theme paramTheme)
  {
    return (T)newInstance(paramResources);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/ConstantState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */