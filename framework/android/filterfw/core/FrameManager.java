package android.filterfw.core;

public abstract class FrameManager
{
  private FilterContext mContext;
  
  public Frame duplicateFrame(Frame paramFrame)
  {
    Frame localFrame = newFrame(paramFrame.getFormat());
    localFrame.setDataFromFrame(paramFrame);
    return localFrame;
  }
  
  public Frame duplicateFrameToTarget(Frame paramFrame, int paramInt)
  {
    Object localObject = paramFrame.getFormat().mutableCopy();
    ((MutableFrameFormat)localObject).setTarget(paramInt);
    localObject = newFrame((FrameFormat)localObject);
    ((Frame)localObject).setDataFromFrame(paramFrame);
    return (Frame)localObject;
  }
  
  public FilterContext getContext()
  {
    return this.mContext;
  }
  
  public GLEnvironment getGLEnvironment()
  {
    GLEnvironment localGLEnvironment = null;
    if (this.mContext != null) {
      localGLEnvironment = this.mContext.getGLEnvironment();
    }
    return localGLEnvironment;
  }
  
  public abstract Frame newBoundFrame(FrameFormat paramFrameFormat, int paramInt, long paramLong);
  
  public abstract Frame newFrame(FrameFormat paramFrameFormat);
  
  public abstract Frame releaseFrame(Frame paramFrame);
  
  public abstract Frame retainFrame(Frame paramFrame);
  
  void setContext(FilterContext paramFilterContext)
  {
    this.mContext = paramFilterContext;
  }
  
  public void tearDown() {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/FrameManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */