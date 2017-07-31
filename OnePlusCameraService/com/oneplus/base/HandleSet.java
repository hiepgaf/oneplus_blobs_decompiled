package com.oneplus.base;

import java.util.ArrayList;
import java.util.List;

public class HandleSet
  extends Handle
{
  private final List<Handle> m_Handles = new ArrayList();
  
  public HandleSet(Handle... paramVarArgs)
  {
    super("HandleSet");
    int i = paramVarArgs.length - 1;
    while (i >= 0)
    {
      this.m_Handles.add(paramVarArgs[i]);
      i -= 1;
    }
  }
  
  public HandleSet addHandle(Handle paramHandle)
  {
    try
    {
      if (!Handle.isValid(this)) {
        throw new IllegalAccessError("Handle set has been closed.");
      }
    }
    finally {}
    boolean bool = Handle.isValid(paramHandle);
    if (!bool) {
      return this;
    }
    this.m_Handles.add(paramHandle);
    return this;
  }
  
  protected void onClose(int paramInt)
  {
    paramInt = this.m_Handles.size() - 1;
    while (paramInt >= 0)
    {
      Handle.close((Handle)this.m_Handles.get(paramInt));
      paramInt -= 1;
    }
    this.m_Handles.clear();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/HandleSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */