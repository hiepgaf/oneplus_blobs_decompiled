package com.oneplus.camera;

import com.oneplus.base.BasicBaseObject;

public abstract class InvalidMode<T extends Mode<?>>
  extends BasicBaseObject
  implements Mode<T>
{
  public boolean enter(T paramT, int paramInt)
  {
    return false;
  }
  
  public void exit(T paramT, int paramInt) {}
  
  public String toString()
  {
    return "(Invalid)";
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/InvalidMode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */