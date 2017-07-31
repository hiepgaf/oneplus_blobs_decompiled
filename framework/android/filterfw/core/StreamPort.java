package android.filterfw.core;

public class StreamPort
  extends InputPort
{
  private Frame mFrame;
  private boolean mPersistent;
  
  public StreamPort(Filter paramFilter, String paramString)
  {
    super(paramFilter, paramString);
  }
  
  protected void assignFrame(Frame paramFrame, boolean paramBoolean)
  {
    try
    {
      assertPortIsOpen();
      checkFrameType(paramFrame, paramBoolean);
      if (paramBoolean) {
        if (this.mFrame != null) {
          this.mFrame.release();
        }
      }
      while (this.mFrame == null)
      {
        this.mFrame = paramFrame.retain();
        this.mFrame.markReadOnly();
        this.mPersistent = paramBoolean;
        return;
      }
      throw new RuntimeException("Attempting to push more than one frame on port: " + this + "!");
    }
    finally {}
  }
  
  public void clear()
  {
    if (this.mFrame != null)
    {
      this.mFrame.release();
      this.mFrame = null;
    }
  }
  
  /* Error */
  public boolean hasFrame()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 26	android/filterfw/core/StreamPort:mFrame	Landroid/filterfw/core/Frame;
    //   6: astore_2
    //   7: aload_2
    //   8: ifnull +9 -> 17
    //   11: iconst_1
    //   12: istore_1
    //   13: aload_0
    //   14: monitorexit
    //   15: iload_1
    //   16: ireturn
    //   17: iconst_0
    //   18: istore_1
    //   19: goto -6 -> 13
    //   22: astore_2
    //   23: aload_0
    //   24: monitorexit
    //   25: aload_2
    //   26: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	27	0	this	StreamPort
    //   12	7	1	bool	boolean
    //   6	2	2	localFrame	Frame
    //   22	4	2	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	7	22	finally
  }
  
  public Frame pullFrame()
  {
    try
    {
      if (this.mFrame == null) {
        throw new RuntimeException("No frame available to pull on port: " + this + "!");
      }
    }
    finally {}
    Frame localFrame = this.mFrame;
    if (this.mPersistent) {
      this.mFrame.retain();
    }
    for (;;)
    {
      return localFrame;
      this.mFrame = null;
    }
  }
  
  public void pushFrame(Frame paramFrame)
  {
    assignFrame(paramFrame, false);
  }
  
  public void setFrame(Frame paramFrame)
  {
    assignFrame(paramFrame, true);
  }
  
  public String toString()
  {
    return "input " + super.toString();
  }
  
  public void transfer(FilterContext paramFilterContext)
  {
    try
    {
      if (this.mFrame != null) {
        checkFrameManager(this.mFrame, paramFilterContext);
      }
      return;
    }
    finally
    {
      paramFilterContext = finally;
      throw paramFilterContext;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/StreamPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */