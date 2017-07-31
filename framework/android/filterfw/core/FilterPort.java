package android.filterfw.core;

import android.util.Log;

public abstract class FilterPort
{
  private static final String TAG = "FilterPort";
  protected boolean mChecksType = false;
  protected Filter mFilter;
  protected boolean mIsBlocking = true;
  protected boolean mIsOpen = false;
  private boolean mLogVerbose;
  protected String mName;
  protected FrameFormat mPortFormat;
  
  public FilterPort(Filter paramFilter, String paramString)
  {
    this.mName = paramString;
    this.mFilter = paramFilter;
    this.mLogVerbose = Log.isLoggable("FilterPort", 2);
  }
  
  protected void assertPortIsOpen()
  {
    if (!isOpen()) {
      throw new RuntimeException("Illegal operation on closed " + this + "!");
    }
  }
  
  protected void checkFrameManager(Frame paramFrame, FilterContext paramFilterContext)
  {
    if ((paramFrame.getFrameManager() != null) && (paramFrame.getFrameManager() != paramFilterContext.getFrameManager())) {
      throw new RuntimeException("Frame " + paramFrame + " is managed by foreign FrameManager! ");
    }
  }
  
  protected void checkFrameType(Frame paramFrame, boolean paramBoolean)
  {
    if (((!this.mChecksType) && (!paramBoolean)) || (this.mPortFormat == null) || (paramFrame.getFormat().isCompatibleWith(this.mPortFormat))) {
      return;
    }
    throw new RuntimeException("Frame passed to " + this + " is of incorrect type! " + "Expected " + this.mPortFormat + " but got " + paramFrame.getFormat());
  }
  
  public abstract void clear();
  
  public void close()
  {
    if ((this.mIsOpen) && (this.mLogVerbose)) {
      Log.v("FilterPort", "Closing " + this);
    }
    this.mIsOpen = false;
  }
  
  public abstract boolean filterMustClose();
  
  public Filter getFilter()
  {
    return this.mFilter;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public FrameFormat getPortFormat()
  {
    return this.mPortFormat;
  }
  
  public abstract boolean hasFrame();
  
  public boolean isAttached()
  {
    return this.mFilter != null;
  }
  
  public boolean isBlocking()
  {
    return this.mIsBlocking;
  }
  
  public boolean isOpen()
  {
    return this.mIsOpen;
  }
  
  public abstract boolean isReady();
  
  public void open()
  {
    if ((!this.mIsOpen) && (this.mLogVerbose)) {
      Log.v("FilterPort", "Opening " + this);
    }
    this.mIsOpen = true;
  }
  
  public abstract Frame pullFrame();
  
  public abstract void pushFrame(Frame paramFrame);
  
  public void setBlocking(boolean paramBoolean)
  {
    this.mIsBlocking = paramBoolean;
  }
  
  public void setChecksType(boolean paramBoolean)
  {
    this.mChecksType = paramBoolean;
  }
  
  public abstract void setFrame(Frame paramFrame);
  
  public void setPortFormat(FrameFormat paramFrameFormat)
  {
    this.mPortFormat = paramFrameFormat;
  }
  
  public String toString()
  {
    return "port '" + this.mName + "' of " + this.mFilter;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/FilterPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */