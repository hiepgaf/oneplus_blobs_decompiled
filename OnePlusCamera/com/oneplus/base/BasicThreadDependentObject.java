package com.oneplus.base;

public abstract class BasicThreadDependentObject
  implements ThreadDependentObject
{
  protected String TAG;
  private final Thread m_DependencyThread;
  
  protected BasicThreadDependentObject()
  {
    this.TAG = getClass().getSimpleName();
    this.m_DependencyThread = Thread.currentThread();
  }
  
  protected BasicThreadDependentObject(String paramString)
  {
    this.TAG = paramString;
    this.m_DependencyThread = Thread.currentThread();
  }
  
  public final boolean isDependencyThread()
  {
    return this.m_DependencyThread == Thread.currentThread();
  }
  
  protected final void verifyAccess()
  {
    if (this.m_DependencyThread != Thread.currentThread()) {
      throw new RuntimeException("Cross-thread access.");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/base/BasicThreadDependentObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */