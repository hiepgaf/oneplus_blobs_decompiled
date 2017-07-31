package com.oneplus.base;

public final class PropertyKey<TValue>
{
  public static final int FLAG_ATTACHABLE = 4;
  public static final int FLAG_NOT_NULL = 2;
  public static final int FLAG_READONLY = 1;
  private static volatile int m_NextId = 1;
  public final TValue defaultValue;
  public final int flags;
  public final int id;
  public final String name;
  public final Class<? extends PropertySource> ownerType;
  public final Class<TValue> valueType;
  
  public PropertyKey(String paramString, Class<TValue> paramClass, Class<? extends PropertySource> paramClass1, int paramInt, TValue paramTValue)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("No property name.");
    }
    if (paramClass == null) {
      throw new IllegalArgumentException("No value type.");
    }
    if (paramClass1 == null) {
      throw new IllegalArgumentException("No owner type.");
    }
    if (((paramInt & 0x2) != 0) && (paramTValue == null)) {
      throw new IllegalArgumentException("Default value cannot be null.");
    }
    if (((paramInt & 0x4) != 0) && ((paramInt & 0x1) != 0)) {
      throw new IllegalArgumentException("Cannot set FLAG_ATTACHABLE and FLAG_READONLY at the same time");
    }
    this.defaultValue = paramTValue;
    this.flags = paramInt;
    this.id = generateId();
    this.name = paramString;
    this.ownerType = paramClass1;
    this.valueType = paramClass;
  }
  
  public PropertyKey(String paramString, Class<TValue> paramClass, Class<? extends PropertySource> paramClass1, TValue paramTValue)
  {
    this(paramString, paramClass, paramClass1, 3, paramTValue);
  }
  
  private static int generateId()
  {
    try
    {
      int i = m_NextId;
      m_NextId = i + 1;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public boolean isAttachable()
  {
    boolean bool = false;
    if ((this.flags & 0x4) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public boolean isReadOnly()
  {
    boolean bool = false;
    if ((this.flags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public String toString()
  {
    return this.name + "(id=" + this.id + ")";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/PropertyKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */