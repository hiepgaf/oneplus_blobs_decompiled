package android.filterfw.core;

import android.filterpacks.base.FrameBranch;
import android.filterpacks.base.NullFilter;
import android.util.Log;
import java.util.AbstractSequentialList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

public class FilterGraph
{
  public static final int AUTOBRANCH_OFF = 0;
  public static final int AUTOBRANCH_SYNCED = 1;
  public static final int AUTOBRANCH_UNSYNCED = 2;
  public static final int TYPECHECK_DYNAMIC = 1;
  public static final int TYPECHECK_OFF = 0;
  public static final int TYPECHECK_STRICT = 2;
  private String TAG = "FilterGraph";
  private int mAutoBranchMode = 0;
  private boolean mDiscardUnconnectedOutputs = false;
  private HashSet<Filter> mFilters = new HashSet();
  private boolean mIsReady = false;
  private boolean mLogVerbose = Log.isLoggable(this.TAG, 2);
  private HashMap<String, Filter> mNameMap = new HashMap();
  private HashMap<OutputPort, LinkedList<InputPort>> mPreconnections = new HashMap();
  private int mTypeCheckMode = 2;
  
  private void checkConnections() {}
  
  private void connectPorts()
  {
    int i = 1;
    Iterator localIterator = this.mPreconnections.entrySet().iterator();
    for (;;)
    {
      if (localIterator.hasNext())
      {
        Object localObject2 = (Map.Entry)localIterator.next();
        Object localObject1 = (OutputPort)((Map.Entry)localObject2).getKey();
        LinkedList localLinkedList = (LinkedList)((Map.Entry)localObject2).getValue();
        if (localLinkedList.size() == 1)
        {
          ((OutputPort)localObject1).connectTo((InputPort)localLinkedList.get(0));
        }
        else
        {
          if (this.mAutoBranchMode == 0) {
            throw new RuntimeException("Attempting to connect " + localObject1 + " to multiple " + "filter ports! Enable auto-branching to allow this.");
          }
          if (this.mLogVerbose) {
            Log.v(this.TAG, "Creating branch for " + localObject1 + "!");
          }
          if (this.mAutoBranchMode == 1)
          {
            localObject2 = new FrameBranch("branch" + i);
            new KeyValueMap();
            ((Filter)localObject2).initWithAssignmentList(new Object[] { "outputs", Integer.valueOf(localLinkedList.size()) });
            addFilter((Filter)localObject2);
            ((OutputPort)localObject1).connectTo(((Filter)localObject2).getInputPort("in"));
            localObject1 = localLinkedList.iterator();
            localObject2 = ((Filter)localObject2).getOutputPorts().iterator();
            while (((Iterator)localObject2).hasNext()) {
              ((OutputPort)((Iterator)localObject2).next()).connectTo((InputPort)((Iterator)localObject1).next());
            }
          }
          throw new RuntimeException("TODO: Unsynced branches not implemented yet!");
        }
      }
      else
      {
        this.mPreconnections.clear();
        return;
        i += 1;
      }
    }
  }
  
  private void discardUnconnectedOutputs()
  {
    Object localObject = new LinkedList();
    Iterator localIterator1 = this.mFilters.iterator();
    while (localIterator1.hasNext())
    {
      Filter localFilter = (Filter)localIterator1.next();
      int i = 0;
      Iterator localIterator2 = localFilter.getOutputPorts().iterator();
      while (localIterator2.hasNext())
      {
        OutputPort localOutputPort = (OutputPort)localIterator2.next();
        if (!localOutputPort.isConnected())
        {
          if (this.mLogVerbose) {
            Log.v(this.TAG, "Autoconnecting unconnected " + localOutputPort + " to Null filter.");
          }
          NullFilter localNullFilter = new NullFilter(localFilter.getName() + "ToNull" + i);
          localNullFilter.init();
          ((LinkedList)localObject).add(localNullFilter);
          localOutputPort.connectTo(localNullFilter.getInputPort("frame"));
          i += 1;
        }
      }
    }
    localObject = ((Iterable)localObject).iterator();
    while (((Iterator)localObject).hasNext()) {
      addFilter((Filter)((Iterator)localObject).next());
    }
  }
  
  private HashSet<Filter> getSourceFilters()
  {
    HashSet localHashSet = new HashSet();
    Iterator localIterator = getFilters().iterator();
    while (localIterator.hasNext())
    {
      Filter localFilter = (Filter)localIterator.next();
      if (localFilter.getNumberOfConnectedInputs() == 0)
      {
        if (this.mLogVerbose) {
          Log.v(this.TAG, "Found source filter: " + localFilter);
        }
        localHashSet.add(localFilter);
      }
    }
    return localHashSet;
  }
  
  private void preconnect(OutputPort paramOutputPort, InputPort paramInputPort)
  {
    LinkedList localLinkedList2 = (LinkedList)this.mPreconnections.get(paramOutputPort);
    LinkedList localLinkedList1 = localLinkedList2;
    if (localLinkedList2 == null)
    {
      localLinkedList1 = new LinkedList();
      this.mPreconnections.put(paramOutputPort, localLinkedList1);
    }
    localLinkedList1.add(paramInputPort);
  }
  
  private boolean readyForProcessing(Filter paramFilter, Set<Filter> paramSet)
  {
    if (paramSet.contains(paramFilter)) {
      return false;
    }
    paramFilter = paramFilter.getInputPorts().iterator();
    while (paramFilter.hasNext())
    {
      Filter localFilter = ((InputPort)paramFilter.next()).getSourceFilter();
      if ((localFilter != null) && (!paramSet.contains(localFilter))) {
        return false;
      }
    }
    return true;
  }
  
  private void removeFilter(Filter paramFilter)
  {
    this.mFilters.remove(paramFilter);
    this.mNameMap.remove(paramFilter.getName());
  }
  
  private void runTypeCheck()
  {
    Stack localStack = new Stack();
    HashSet localHashSet = new HashSet();
    localStack.addAll(getSourceFilters());
    while (!localStack.empty())
    {
      Object localObject = (Filter)localStack.pop();
      localHashSet.add(localObject);
      updateOutputs((Filter)localObject);
      if (this.mLogVerbose) {
        Log.v(this.TAG, "Running type check on " + localObject + "...");
      }
      runTypeCheckOn((Filter)localObject);
      localObject = ((Filter)localObject).getOutputPorts().iterator();
      while (((Iterator)localObject).hasNext())
      {
        Filter localFilter = ((OutputPort)((Iterator)localObject).next()).getTargetFilter();
        if ((localFilter != null) && (readyForProcessing(localFilter, localHashSet))) {
          localStack.push(localFilter);
        }
      }
    }
    if (localHashSet.size() != getFilters().size()) {
      throw new RuntimeException("Could not schedule all filters! Is your graph malformed?");
    }
  }
  
  private void runTypeCheckOn(Filter paramFilter)
  {
    Iterator localIterator = paramFilter.getInputPorts().iterator();
    while (localIterator.hasNext())
    {
      InputPort localInputPort = (InputPort)localIterator.next();
      if (this.mLogVerbose) {
        Log.v(this.TAG, "Type checking port " + localInputPort);
      }
      FrameFormat localFrameFormat1 = localInputPort.getSourceFormat();
      FrameFormat localFrameFormat2 = localInputPort.getPortFormat();
      if ((localFrameFormat1 != null) && (localFrameFormat2 != null))
      {
        if (this.mLogVerbose) {
          Log.v(this.TAG, "Checking " + localFrameFormat1 + " against " + localFrameFormat2 + ".");
        }
        boolean bool = true;
        switch (this.mTypeCheckMode)
        {
        }
        while (!bool)
        {
          throw new RuntimeException("Type mismatch: Filter " + paramFilter + " expects a " + "format of type " + localFrameFormat2 + " but got a format of type " + localFrameFormat1 + "!");
          localInputPort.setChecksType(false);
          continue;
          bool = localFrameFormat1.mayBeCompatibleWith(localFrameFormat2);
          localInputPort.setChecksType(true);
          continue;
          bool = localFrameFormat1.isCompatibleWith(localFrameFormat2);
          localInputPort.setChecksType(false);
        }
      }
    }
  }
  
  private void updateOutputs(Filter paramFilter)
  {
    Iterator localIterator = paramFilter.getOutputPorts().iterator();
    while (localIterator.hasNext())
    {
      OutputPort localOutputPort = (OutputPort)localIterator.next();
      Object localObject = localOutputPort.getBasePort();
      if (localObject != null)
      {
        localObject = ((InputPort)localObject).getSourceFormat();
        localObject = paramFilter.getOutputFormat(localOutputPort.getName(), (FrameFormat)localObject);
        if (localObject == null) {
          throw new RuntimeException("Filter did not return an output format for " + localOutputPort + "!");
        }
        localOutputPort.setPortFormat((FrameFormat)localObject);
      }
    }
  }
  
  public boolean addFilter(Filter paramFilter)
  {
    if (!containsFilter(paramFilter))
    {
      this.mFilters.add(paramFilter);
      this.mNameMap.put(paramFilter.getName(), paramFilter);
      return true;
    }
    return false;
  }
  
  public void beginProcessing()
  {
    if (this.mLogVerbose) {
      Log.v(this.TAG, "Opening all filter connections...");
    }
    Iterator localIterator = this.mFilters.iterator();
    while (localIterator.hasNext()) {
      ((Filter)localIterator.next()).openOutputs();
    }
    this.mIsReady = true;
  }
  
  public void closeFilters(FilterContext paramFilterContext)
  {
    if (this.mLogVerbose) {
      Log.v(this.TAG, "Closing all filters...");
    }
    Iterator localIterator = this.mFilters.iterator();
    while (localIterator.hasNext()) {
      ((Filter)localIterator.next()).performClose(paramFilterContext);
    }
    this.mIsReady = false;
  }
  
  public void connect(Filter paramFilter1, String paramString1, Filter paramFilter2, String paramString2)
  {
    if ((paramFilter1 == null) || (paramFilter2 == null)) {
      throw new IllegalArgumentException("Passing null Filter in connect()!");
    }
    OutputPort localOutputPort;
    InputPort localInputPort;
    if ((containsFilter(paramFilter1)) && (containsFilter(paramFilter2)))
    {
      localOutputPort = paramFilter1.getOutputPort(paramString1);
      localInputPort = paramFilter2.getInputPort(paramString2);
      if (localOutputPort == null) {
        throw new RuntimeException("Unknown output port '" + paramString1 + "' on Filter " + paramFilter1 + "!");
      }
    }
    else
    {
      throw new RuntimeException("Attempting to connect filter not in graph!");
    }
    if (localInputPort == null) {
      throw new RuntimeException("Unknown input port '" + paramString2 + "' on Filter " + paramFilter2 + "!");
    }
    preconnect(localOutputPort, localInputPort);
  }
  
  public void connect(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    Filter localFilter1 = getFilter(paramString1);
    Filter localFilter2 = getFilter(paramString3);
    if (localFilter1 == null) {
      throw new RuntimeException("Attempting to connect unknown source filter '" + paramString1 + "'!");
    }
    if (localFilter2 == null) {
      throw new RuntimeException("Attempting to connect unknown target filter '" + paramString3 + "'!");
    }
    connect(localFilter1, paramString2, localFilter2, paramString4);
  }
  
  public boolean containsFilter(Filter paramFilter)
  {
    return this.mFilters.contains(paramFilter);
  }
  
  public void flushFrames()
  {
    Iterator localIterator = this.mFilters.iterator();
    while (localIterator.hasNext()) {
      ((Filter)localIterator.next()).clearOutputs();
    }
  }
  
  public Filter getFilter(String paramString)
  {
    return (Filter)this.mNameMap.get(paramString);
  }
  
  public Set<Filter> getFilters()
  {
    return this.mFilters;
  }
  
  public boolean isReady()
  {
    return this.mIsReady;
  }
  
  public void setAutoBranchMode(int paramInt)
  {
    this.mAutoBranchMode = paramInt;
  }
  
  public void setDiscardUnconnectedOutputs(boolean paramBoolean)
  {
    this.mDiscardUnconnectedOutputs = paramBoolean;
  }
  
  public void setTypeCheckMode(int paramInt)
  {
    this.mTypeCheckMode = paramInt;
  }
  
  void setupFilters()
  {
    if (this.mDiscardUnconnectedOutputs) {
      discardUnconnectedOutputs();
    }
    connectPorts();
    checkConnections();
    runTypeCheck();
  }
  
  public void tearDown(FilterContext paramFilterContext)
  {
    if (!this.mFilters.isEmpty())
    {
      flushFrames();
      Iterator localIterator = this.mFilters.iterator();
      while (localIterator.hasNext()) {
        ((Filter)localIterator.next()).performTearDown(paramFilterContext);
      }
      this.mFilters.clear();
      this.mNameMap.clear();
      this.mIsReady = false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/FilterGraph.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */