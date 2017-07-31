package com.oneplus.gl;

import com.oneplus.base.BasicThreadDependentObject;

public abstract class EglObject
  extends BasicThreadDependentObject
{
  EglObjectHolder holder;
  private boolean m_IsReleased;
  
  protected EglObject()
  {
    EglContextManager.registerEglObject(this);
  }
  
  public static <T extends EglObject> T release(T paramT)
  {
    if (paramT == null) {
      return null;
    }
    if (paramT.m_IsReleased) {
      return null;
    }
    paramT.verifyAccess();
    EglContextManager.unregisterEglObject(paramT);
    paramT.onRelease();
    paramT.m_IsReleased = true;
    return null;
  }
  
  public int getObjectId()
  {
    return 0;
  }
  
  public final boolean isEglContextReady()
  {
    return EglContextManager.isEglContextReady();
  }
  
  protected final boolean isReleased()
  {
    return this.m_IsReleased;
  }
  
  protected void onEglContextDestroying() {}
  
  protected void onEglContextReady() {}
  
  protected void onRelease() {}
  
  protected final void throwIfNotAccessible()
  {
    verifyAccess();
    if (this.m_IsReleased) {
      throw new RuntimeException("Object has been released");
    }
  }
  
  protected final void verifyReleaseState()
  {
    if (this.m_IsReleased) {
      throw new RuntimeException("Object has been released.");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/EglObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */