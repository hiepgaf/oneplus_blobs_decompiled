package android.animation;

public abstract class TypeConverter<T, V>
{
  private Class<T> mFromClass;
  private Class<V> mToClass;
  
  public TypeConverter(Class<T> paramClass, Class<V> paramClass1)
  {
    this.mFromClass = paramClass;
    this.mToClass = paramClass1;
  }
  
  public abstract V convert(T paramT);
  
  Class<T> getSourceType()
  {
    return this.mFromClass;
  }
  
  Class<V> getTargetType()
  {
    return this.mToClass;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/animation/TypeConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */