package android.content.res;

public class ConfigurationBoundResourceCache<T>
  extends ThemedResourceCache<ConstantState<T>>
{
  public T getInstance(long paramLong, Resources paramResources, Resources.Theme paramTheme)
  {
    ConstantState localConstantState = (ConstantState)get(paramLong, paramTheme);
    if (localConstantState != null) {
      return (T)localConstantState.newInstance(paramResources, paramTheme);
    }
    return null;
  }
  
  public boolean shouldInvalidateEntry(ConstantState<T> paramConstantState, int paramInt)
  {
    return Configuration.needNewResources(paramInt, paramConstantState.getChangingConfigurations());
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/res/ConfigurationBoundResourceCache.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */