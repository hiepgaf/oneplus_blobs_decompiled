package android.filterfw;

import android.content.Context;
import android.filterfw.core.AsyncRunner;
import android.filterfw.core.FilterContext;
import android.filterfw.core.FilterGraph;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GraphRunner;
import android.filterfw.core.RoundRobinScheduler;
import android.filterfw.core.SyncRunner;
import android.filterfw.io.GraphIOException;
import android.filterfw.io.GraphReader;
import android.filterfw.io.TextGraphReader;
import java.util.ArrayList;

public class GraphEnvironment
  extends MffEnvironment
{
  public static final int MODE_ASYNCHRONOUS = 1;
  public static final int MODE_SYNCHRONOUS = 2;
  private GraphReader mGraphReader;
  private ArrayList<GraphHandle> mGraphs = new ArrayList();
  
  public GraphEnvironment()
  {
    super(null);
  }
  
  public GraphEnvironment(FrameManager paramFrameManager, GraphReader paramGraphReader)
  {
    super(paramFrameManager);
    this.mGraphReader = paramGraphReader;
  }
  
  public int addGraph(FilterGraph paramFilterGraph)
  {
    paramFilterGraph = new GraphHandle(paramFilterGraph);
    this.mGraphs.add(paramFilterGraph);
    return this.mGraphs.size() - 1;
  }
  
  public void addReferences(Object... paramVarArgs)
  {
    getGraphReader().addReferencesByKeysAndValues(paramVarArgs);
  }
  
  public FilterGraph getGraph(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mGraphs.size())) {
      throw new IllegalArgumentException("Invalid graph ID " + paramInt + " specified in runGraph()!");
    }
    return ((GraphHandle)this.mGraphs.get(paramInt)).getGraph();
  }
  
  public GraphReader getGraphReader()
  {
    if (this.mGraphReader == null) {
      this.mGraphReader = new TextGraphReader();
    }
    return this.mGraphReader;
  }
  
  public GraphRunner getRunner(int paramInt1, int paramInt2)
  {
    switch (paramInt2)
    {
    default: 
      throw new RuntimeException("Invalid execution mode " + paramInt2 + " specified in getRunner()!");
    case 1: 
      return ((GraphHandle)this.mGraphs.get(paramInt1)).getAsyncRunner(getContext());
    }
    return ((GraphHandle)this.mGraphs.get(paramInt1)).getSyncRunner(getContext());
  }
  
  public int loadGraph(Context paramContext, int paramInt)
  {
    try
    {
      paramContext = getGraphReader().readGraphResource(paramContext, paramInt);
      return addGraph(paramContext);
    }
    catch (GraphIOException paramContext)
    {
      throw new RuntimeException("Could not read graph: " + paramContext.getMessage());
    }
  }
  
  private class GraphHandle
  {
    private AsyncRunner mAsyncRunner;
    private FilterGraph mGraph;
    private SyncRunner mSyncRunner;
    
    public GraphHandle(FilterGraph paramFilterGraph)
    {
      this.mGraph = paramFilterGraph;
    }
    
    public AsyncRunner getAsyncRunner(FilterContext paramFilterContext)
    {
      if (this.mAsyncRunner == null)
      {
        this.mAsyncRunner = new AsyncRunner(paramFilterContext, RoundRobinScheduler.class);
        this.mAsyncRunner.setGraph(this.mGraph);
      }
      return this.mAsyncRunner;
    }
    
    public FilterGraph getGraph()
    {
      return this.mGraph;
    }
    
    public GraphRunner getSyncRunner(FilterContext paramFilterContext)
    {
      if (this.mSyncRunner == null) {
        this.mSyncRunner = new SyncRunner(paramFilterContext, this.mGraph, RoundRobinScheduler.class);
      }
      return this.mSyncRunner;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/GraphEnvironment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */