package android.filterfw.core;

public abstract class InputPort
  extends FilterPort
{
  protected OutputPort mSourcePort;
  
  public InputPort(Filter paramFilter, String paramString)
  {
    super(paramFilter, paramString);
  }
  
  public boolean acceptsFrame()
  {
    return !hasFrame();
  }
  
  public void close()
  {
    if ((this.mSourcePort != null) && (this.mSourcePort.isOpen())) {
      this.mSourcePort.close();
    }
    super.close();
  }
  
  public boolean filterMustClose()
  {
    return (!isOpen()) && (isBlocking()) && (!hasFrame());
  }
  
  public Filter getSourceFilter()
  {
    if (this.mSourcePort == null) {
      return null;
    }
    return this.mSourcePort.getFilter();
  }
  
  public FrameFormat getSourceFormat()
  {
    if (this.mSourcePort != null) {
      return this.mSourcePort.getPortFormat();
    }
    return getPortFormat();
  }
  
  public OutputPort getSourcePort()
  {
    return this.mSourcePort;
  }
  
  public Object getTarget()
  {
    return null;
  }
  
  public boolean isConnected()
  {
    return this.mSourcePort != null;
  }
  
  public boolean isReady()
  {
    boolean bool2 = true;
    boolean bool1 = bool2;
    if (!hasFrame())
    {
      bool1 = bool2;
      if (isBlocking()) {
        bool1 = false;
      }
    }
    return bool1;
  }
  
  public void open()
  {
    super.open();
    if ((this.mSourcePort == null) || (this.mSourcePort.isOpen())) {
      return;
    }
    this.mSourcePort.open();
  }
  
  public void setSourcePort(OutputPort paramOutputPort)
  {
    if (this.mSourcePort != null) {
      throw new RuntimeException(this + " already connected to " + this.mSourcePort + "!");
    }
    this.mSourcePort = paramOutputPort;
  }
  
  public abstract void transfer(FilterContext paramFilterContext);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/InputPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */