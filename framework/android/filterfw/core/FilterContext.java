package android.filterfw.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FilterContext
{
  private FrameManager mFrameManager;
  private GLEnvironment mGLEnvironment;
  private Set<FilterGraph> mGraphs = new HashSet();
  private HashMap<String, Frame> mStoredFrames = new HashMap();
  
  final void addGraph(FilterGraph paramFilterGraph)
  {
    this.mGraphs.add(paramFilterGraph);
  }
  
  public Frame fetchFrame(String paramString)
  {
    try
    {
      paramString = (Frame)this.mStoredFrames.get(paramString);
      if (paramString != null) {
        paramString.onFrameFetch();
      }
      return paramString;
    }
    finally {}
  }
  
  public FrameManager getFrameManager()
  {
    return this.mFrameManager;
  }
  
  public GLEnvironment getGLEnvironment()
  {
    return this.mGLEnvironment;
  }
  
  public void initGLEnvironment(GLEnvironment paramGLEnvironment)
  {
    if (this.mGLEnvironment == null)
    {
      this.mGLEnvironment = paramGLEnvironment;
      return;
    }
    throw new RuntimeException("Attempting to re-initialize GL Environment for FilterContext!");
  }
  
  public void removeFrame(String paramString)
  {
    try
    {
      Frame localFrame = (Frame)this.mStoredFrames.get(paramString);
      if (localFrame != null)
      {
        this.mStoredFrames.remove(paramString);
        localFrame.release();
      }
      return;
    }
    finally {}
  }
  
  public void setFrameManager(FrameManager paramFrameManager)
  {
    if (paramFrameManager == null) {
      throw new NullPointerException("Attempting to set null FrameManager!");
    }
    if (paramFrameManager.getContext() != null) {
      throw new IllegalArgumentException("Attempting to set FrameManager which is already bound to another FilterContext!");
    }
    this.mFrameManager = paramFrameManager;
    this.mFrameManager.setContext(this);
  }
  
  public void storeFrame(String paramString, Frame paramFrame)
  {
    try
    {
      Frame localFrame = fetchFrame(paramString);
      if (localFrame != null) {
        localFrame.release();
      }
      paramFrame.onFrameStore();
      this.mStoredFrames.put(paramString, paramFrame.retain());
      return;
    }
    finally {}
  }
  
  public void tearDown()
  {
    try
    {
      Iterator localIterator1 = this.mStoredFrames.values().iterator();
      while (localIterator1.hasNext()) {
        ((Frame)localIterator1.next()).release();
      }
      this.mStoredFrames.clear();
    }
    finally {}
    Iterator localIterator2 = this.mGraphs.iterator();
    while (localIterator2.hasNext()) {
      ((FilterGraph)localIterator2.next()).tearDown(this);
    }
    this.mGraphs.clear();
    if (this.mFrameManager != null)
    {
      this.mFrameManager.tearDown();
      this.mFrameManager = null;
    }
    if (this.mGLEnvironment != null)
    {
      this.mGLEnvironment.tearDown();
      this.mGLEnvironment = null;
    }
  }
  
  public static abstract interface OnFrameReceivedListener
  {
    public abstract void onFrameReceived(Filter paramFilter, Frame paramFrame, Object paramObject);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/FilterContext.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */