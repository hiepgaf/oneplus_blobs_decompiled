package com.aps;

import java.lang.reflect.Method;

public class n
{
  public static Object a(Object paramObject, String paramString, Object... paramVarArgs)
    throws Exception
  {
    Class localClass = paramObject.getClass();
    Class[] arrayOfClass = new Class[paramVarArgs.length];
    int i = 0;
    int j = paramVarArgs.length;
    for (;;)
    {
      if (i >= j) {
        return localClass.getMethod(paramString, arrayOfClass).invoke(paramObject, paramVarArgs);
      }
      arrayOfClass[i] = paramVarArgs[i].getClass();
      i += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/aps/n.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */