package android.filterfw.core;

public class OutputPort
  extends FilterPort
{
  protected InputPort mBasePort;
  protected InputPort mTargetPort;
  
  public OutputPort(Filter paramFilter, String paramString)
  {
    super(paramFilter, paramString);
  }
  
  public void clear()
  {
    if (this.mTargetPort != null) {
      this.mTargetPort.clear();
    }
  }
  
  public void close()
  {
    super.close();
    if ((this.mTargetPort != null) && (this.mTargetPort.isOpen())) {
      this.mTargetPort.close();
    }
  }
  
  public void connectTo(InputPort paramInputPort)
  {
    if (this.mTargetPort != null) {
      throw new RuntimeException(this + " already connected to " + this.mTargetPort + "!");
    }
    this.mTargetPort = paramInputPort;
    this.mTargetPort.setSourcePort(this);
  }
  
  public boolean filterMustClose()
  {
    if (!isOpen()) {
      return isBlocking();
    }
    return false;
  }
  
  public InputPort getBasePort()
  {
    return this.mBasePort;
  }
  
  public Filter getTargetFilter()
  {
    if (this.mTargetPort == null) {
      return null;
    }
    return this.mTargetPort.getFilter();
  }
  
  public InputPort getTargetPort()
  {
    return this.mTargetPort;
  }
  
  public boolean hasFrame()
  {
    if (this.mTargetPort == null) {
      return false;
    }
    return this.mTargetPort.hasFrame();
  }
  
  public boolean isConnected()
  {
    return this.mTargetPort != null;
  }
  
  public boolean isReady()
  {
    boolean bool2 = true;
    boolean bool1;
    if (isOpen())
    {
      bool1 = bool2;
      if (this.mTargetPort.acceptsFrame()) {}
    }
    else
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
    if ((this.mTargetPort == null) || (this.mTargetPort.isOpen())) {
      return;
    }
    this.mTargetPort.open();
  }
  
  public Frame pullFrame()
  {
    throw new RuntimeException("Cannot pull frame on " + this + "!");
  }
  
  public void pushFrame(Frame paramFrame)
  {
    if (this.mTargetPort == null) {
      throw new RuntimeException("Attempting to push frame on unconnected port: " + this + "!");
    }
    this.mTargetPort.pushFrame(paramFrame);
  }
  
  public void setBasePort(InputPort paramInputPort)
  {
    this.mBasePort = paramInputPort;
  }
  
  public void setFrame(Frame paramFrame)
  {
    assertPortIsOpen();
    if (this.mTargetPort == null) {
      throw new RuntimeException("Attempting to set frame on unconnected port: " + this + "!");
    }
    this.mTargetPort.setFrame(paramFrame);
  }
  
  public String toString()
  {
    return "output " + super.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/OutputPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */