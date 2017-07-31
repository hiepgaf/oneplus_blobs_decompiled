package com.oneplus.base;

public final class EventKey<TArgs extends EventArgs>
{
  public static final int FLAG_INTERRUPTIBLE = 1;
  private static volatile int m_NextId = 1;
  public final Class<TArgs> argumentType;
  public final int flags;
  public final int id;
  public final String name;
  public final Class<? extends EventSource> ownerType;
  
  public EventKey(String paramString, Class<TArgs> paramClass, Class<? extends EventSource> paramClass1)
  {
    this(paramString, paramClass, paramClass1, 0);
  }
  
  public EventKey(String paramString, Class<TArgs> paramClass, Class<? extends EventSource> paramClass1, int paramInt)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("No property name.");
    }
    if (paramClass == null) {
      throw new IllegalArgumentException("No argument type.");
    }
    if (paramClass1 == null) {
      throw new IllegalArgumentException("No owner type.");
    }
    this.argumentType = paramClass;
    this.flags = paramInt;
    this.id = generateId();
    this.name = paramString;
    this.ownerType = paramClass1;
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
  
  public boolean isInterruptible()
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/EventKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */