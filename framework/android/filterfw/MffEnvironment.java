package android.filterfw;

import android.filterfw.core.CachedFrameManager;
import android.filterfw.core.FilterContext;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GLEnvironment;

public class MffEnvironment
{
  private FilterContext mContext;
  
  protected MffEnvironment(FrameManager paramFrameManager)
  {
    Object localObject = paramFrameManager;
    if (paramFrameManager == null) {
      localObject = new CachedFrameManager();
    }
    this.mContext = new FilterContext();
    this.mContext.setFrameManager((FrameManager)localObject);
  }
  
  public void activateGLEnvironment()
  {
    if (this.mContext.getGLEnvironment() != null)
    {
      this.mContext.getGLEnvironment().activate();
      return;
    }
    throw new NullPointerException("No GLEnvironment in place to activate!");
  }
  
  public void createGLEnvironment()
  {
    GLEnvironment localGLEnvironment = new GLEnvironment();
    localGLEnvironment.initWithNewContext();
    setGLEnvironment(localGLEnvironment);
  }
  
  public void deactivateGLEnvironment()
  {
    if (this.mContext.getGLEnvironment() != null)
    {
      this.mContext.getGLEnvironment().deactivate();
      return;
    }
    throw new NullPointerException("No GLEnvironment in place to deactivate!");
  }
  
  public FilterContext getContext()
  {
    return this.mContext;
  }
  
  public void setGLEnvironment(GLEnvironment paramGLEnvironment)
  {
    this.mContext.initGLEnvironment(paramGLEnvironment);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/MffEnvironment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */