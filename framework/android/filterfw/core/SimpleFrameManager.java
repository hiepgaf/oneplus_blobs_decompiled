package android.filterfw.core;

public class SimpleFrameManager
  extends FrameManager
{
  private Frame createNewFrame(FrameFormat paramFrameFormat)
  {
    switch (paramFrameFormat.getTarget())
    {
    default: 
      throw new RuntimeException("Unsupported frame target type: " + FrameFormat.targetToString(paramFrameFormat.getTarget()) + "!");
    case 1: 
      return new SimpleFrame(paramFrameFormat, this);
    case 2: 
      return new NativeFrame(paramFrameFormat, this);
    case 3: 
      paramFrameFormat = new GLFrame(paramFrameFormat, this);
      paramFrameFormat.init(getGLEnvironment());
      return paramFrameFormat;
    }
    return new VertexFrame(paramFrameFormat, this);
  }
  
  public Frame newBoundFrame(FrameFormat paramFrameFormat, int paramInt, long paramLong)
  {
    switch (paramFrameFormat.getTarget())
    {
    default: 
      throw new RuntimeException("Attached frames are not supported for target type: " + FrameFormat.targetToString(paramFrameFormat.getTarget()) + "!");
    }
    paramFrameFormat = new GLFrame(paramFrameFormat, this, paramInt, paramLong);
    paramFrameFormat.init(getGLEnvironment());
    return paramFrameFormat;
  }
  
  public Frame newFrame(FrameFormat paramFrameFormat)
  {
    return createNewFrame(paramFrameFormat);
  }
  
  public Frame releaseFrame(Frame paramFrame)
  {
    int i = paramFrame.decRefCount();
    if ((i == 0) && (paramFrame.hasNativeAllocation()))
    {
      paramFrame.releaseNativeAllocation();
      return null;
    }
    if (i < 0) {
      throw new RuntimeException("Frame reference count dropped below 0!");
    }
    return paramFrame;
  }
  
  public Frame retainFrame(Frame paramFrame)
  {
    paramFrame.incRefCount();
    return paramFrame;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/SimpleFrameManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */