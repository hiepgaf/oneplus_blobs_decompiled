package android.filterfw.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class FilterFunction
{
  private Filter mFilter;
  private FilterContext mFilterContext;
  private boolean mFilterIsSetup = false;
  private FrameHolderPort[] mResultHolders;
  
  public FilterFunction(FilterContext paramFilterContext, Filter paramFilter)
  {
    this.mFilterContext = paramFilterContext;
    this.mFilter = paramFilter;
  }
  
  private void connectFilterOutputs()
  {
    int i = 0;
    this.mResultHolders = new FrameHolderPort[this.mFilter.getNumberOfOutputs()];
    Iterator localIterator = this.mFilter.getOutputPorts().iterator();
    while (localIterator.hasNext())
    {
      OutputPort localOutputPort = (OutputPort)localIterator.next();
      this.mResultHolders[i] = new FrameHolderPort();
      localOutputPort.connectTo(this.mResultHolders[i]);
      i += 1;
    }
  }
  
  public void close()
  {
    this.mFilter.performClose(this.mFilterContext);
  }
  
  public Frame execute(KeyValueMap paramKeyValueMap)
  {
    int k = this.mFilter.getNumberOfOutputs();
    if (k > 1) {
      throw new RuntimeException("Calling execute on filter " + this.mFilter + " with multiple " + "outputs! Use executeMulti() instead!");
    }
    if (!this.mFilterIsSetup)
    {
      connectFilterOutputs();
      this.mFilterIsSetup = true;
    }
    int j = 0;
    GLEnvironment localGLEnvironment = this.mFilterContext.getGLEnvironment();
    int i = j;
    if (localGLEnvironment != null)
    {
      if (localGLEnvironment.isActive()) {
        i = j;
      }
    }
    else {
      paramKeyValueMap = paramKeyValueMap.entrySet().iterator();
    }
    for (;;)
    {
      if (!paramKeyValueMap.hasNext()) {
        break label209;
      }
      localEntry = (Map.Entry)paramKeyValueMap.next();
      if ((localEntry.getValue() instanceof Frame))
      {
        this.mFilter.pushInputFrame((String)localEntry.getKey(), (Frame)localEntry.getValue());
        continue;
        localGLEnvironment.activate();
        i = 1;
        break;
      }
      this.mFilter.pushInputValue((String)localEntry.getKey(), localEntry.getValue());
    }
    label209:
    if (this.mFilter.getStatus() != 3) {
      this.mFilter.openOutputs();
    }
    this.mFilter.performProcess(this.mFilterContext);
    Map.Entry localEntry = null;
    paramKeyValueMap = localEntry;
    if (k == 1)
    {
      paramKeyValueMap = localEntry;
      if (this.mResultHolders[0].hasFrame()) {
        paramKeyValueMap = this.mResultHolders[0].pullFrame();
      }
    }
    if (i != 0) {
      localGLEnvironment.deactivate();
    }
    return paramKeyValueMap;
  }
  
  public Frame executeWithArgList(Object... paramVarArgs)
  {
    return execute(KeyValueMap.fromKeyValues(paramVarArgs));
  }
  
  public FilterContext getContext()
  {
    return this.mFilterContext;
  }
  
  public Filter getFilter()
  {
    return this.mFilter;
  }
  
  public void setInputFrame(String paramString, Frame paramFrame)
  {
    this.mFilter.setInputFrame(paramString, paramFrame);
  }
  
  public void setInputValue(String paramString, Object paramObject)
  {
    this.mFilter.setInputValue(paramString, paramObject);
  }
  
  public void tearDown()
  {
    this.mFilter.performTearDown(this.mFilterContext);
    this.mFilter = null;
  }
  
  public String toString()
  {
    return this.mFilter.getName();
  }
  
  private class FrameHolderPort
    extends StreamPort
  {
    public FrameHolderPort()
    {
      super("holder");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/FilterFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */