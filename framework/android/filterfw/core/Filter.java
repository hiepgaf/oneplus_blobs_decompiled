package android.filterfw.core;

import android.filterfw.format.ObjectFormat;
import android.filterfw.io.GraphIOException;
import android.filterfw.io.TextGraphReader;
import android.util.Log;
import java.io.Serializable;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public abstract class Filter
{
  static final int STATUS_ERROR = 6;
  static final int STATUS_FINISHED = 5;
  static final int STATUS_PREINIT = 0;
  static final int STATUS_PREPARED = 2;
  static final int STATUS_PROCESSING = 3;
  static final int STATUS_RELEASED = 7;
  static final int STATUS_SLEEPING = 4;
  static final int STATUS_UNPREPARED = 1;
  private static final String TAG = "Filter";
  private long mCurrentTimestamp;
  private HashSet<Frame> mFramesToRelease;
  private HashMap<String, Frame> mFramesToSet;
  private int mInputCount = -1;
  private HashMap<String, InputPort> mInputPorts;
  private boolean mIsOpen = false;
  private boolean mLogVerbose;
  private String mName;
  private int mOutputCount = -1;
  private HashMap<String, OutputPort> mOutputPorts;
  private int mSleepDelay;
  private int mStatus = 0;
  
  public Filter(String paramString)
  {
    this.mName = paramString;
    this.mFramesToRelease = new HashSet();
    this.mFramesToSet = new HashMap();
    this.mStatus = 0;
    this.mLogVerbose = Log.isLoggable("Filter", 2);
  }
  
  private final void addAndSetFinalPorts(KeyValueMap paramKeyValueMap)
  {
    Field[] arrayOfField = getClass().getDeclaredFields();
    int i = 0;
    int j = arrayOfField.length;
    if (i < j)
    {
      Field localField = arrayOfField[i];
      Object localObject = localField.getAnnotation(GenerateFinalPort.class);
      GenerateFinalPort localGenerateFinalPort;
      if (localObject != null)
      {
        localGenerateFinalPort = (GenerateFinalPort)localObject;
        if (!localGenerateFinalPort.name().isEmpty()) {
          break label118;
        }
        localObject = localField.getName();
        label67:
        addFieldPort((String)localObject, localField, localGenerateFinalPort.hasDefault(), true);
        if (!paramKeyValueMap.containsKey(localObject)) {
          break label130;
        }
        setImmediateInputValue((String)localObject, paramKeyValueMap.get(localObject));
        paramKeyValueMap.remove(localObject);
      }
      label118:
      label130:
      while (localGenerateFinalPort.hasDefault())
      {
        i += 1;
        break;
        localObject = localGenerateFinalPort.name();
        break label67;
      }
      throw new RuntimeException("No value specified for final input port '" + (String)localObject + "' of filter " + this + "!");
    }
  }
  
  private final void addAnnotatedPorts()
  {
    Field[] arrayOfField = getClass().getDeclaredFields();
    int k = arrayOfField.length;
    int i = 0;
    if (i < k)
    {
      Field localField = arrayOfField[i];
      Object localObject = localField.getAnnotation(GenerateFieldPort.class);
      if (localObject != null) {
        addFieldGenerator((GenerateFieldPort)localObject, localField);
      }
      for (;;)
      {
        i += 1;
        break;
        localObject = localField.getAnnotation(GenerateProgramPort.class);
        if (localObject != null)
        {
          addProgramGenerator((GenerateProgramPort)localObject, localField);
        }
        else
        {
          localObject = localField.getAnnotation(GenerateProgramPorts.class);
          if (localObject != null)
          {
            localObject = ((GenerateProgramPorts)localObject).value();
            int m = localObject.length;
            int j = 0;
            while (j < m)
            {
              addProgramGenerator(localObject[j], localField);
              j += 1;
            }
          }
        }
      }
    }
  }
  
  private final void addFieldGenerator(GenerateFieldPort paramGenerateFieldPort, Field paramField)
  {
    if (paramGenerateFieldPort.name().isEmpty()) {}
    for (String str = paramField.getName();; str = paramGenerateFieldPort.name())
    {
      addFieldPort(str, paramField, paramGenerateFieldPort.hasDefault(), false);
      return;
    }
  }
  
  private final void addProgramGenerator(GenerateProgramPort paramGenerateProgramPort, Field paramField)
  {
    String str2 = paramGenerateProgramPort.name();
    if (paramGenerateProgramPort.variableName().isEmpty()) {}
    for (String str1 = str2;; str1 = paramGenerateProgramPort.variableName())
    {
      addProgramPort(str2, str1, paramField, paramGenerateProgramPort.type(), paramGenerateProgramPort.hasDefault());
      return;
    }
  }
  
  private final void closePorts()
  {
    if (this.mLogVerbose) {
      Log.v("Filter", "Closing all ports on " + this + "!");
    }
    Iterator localIterator = this.mInputPorts.values().iterator();
    while (localIterator.hasNext()) {
      ((InputPort)localIterator.next()).close();
    }
    localIterator = this.mOutputPorts.values().iterator();
    while (localIterator.hasNext()) {
      ((OutputPort)localIterator.next()).close();
    }
  }
  
  private final boolean filterMustClose()
  {
    Iterator localIterator = this.mInputPorts.values().iterator();
    Object localObject;
    while (localIterator.hasNext())
    {
      localObject = (InputPort)localIterator.next();
      if (((InputPort)localObject).filterMustClose())
      {
        if (this.mLogVerbose) {
          Log.v("Filter", "Filter " + this + " must close due to port " + localObject);
        }
        return true;
      }
    }
    localIterator = this.mOutputPorts.values().iterator();
    while (localIterator.hasNext())
    {
      localObject = (OutputPort)localIterator.next();
      if (((OutputPort)localObject).filterMustClose())
      {
        if (this.mLogVerbose) {
          Log.v("Filter", "Filter " + this + " must close due to port " + localObject);
        }
        return true;
      }
    }
    return false;
  }
  
  private final void initFinalPorts(KeyValueMap paramKeyValueMap)
  {
    this.mInputPorts = new HashMap();
    this.mOutputPorts = new HashMap();
    addAndSetFinalPorts(paramKeyValueMap);
  }
  
  private final void initRemainingPorts(KeyValueMap paramKeyValueMap)
  {
    addAnnotatedPorts();
    setupPorts();
    setInitialInputValues(paramKeyValueMap);
  }
  
  private final boolean inputConditionsMet()
  {
    Iterator localIterator = this.mInputPorts.values().iterator();
    while (localIterator.hasNext())
    {
      InputPort localInputPort = (InputPort)localIterator.next();
      if (!localInputPort.isReady())
      {
        if (this.mLogVerbose) {
          Log.v("Filter", "Input condition not met: " + localInputPort + "!");
        }
        return false;
      }
    }
    return true;
  }
  
  /* Error */
  public static final boolean isAvailable(String paramString)
  {
    // Byte code:
    //   0: invokestatic 266	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   3: invokevirtual 270	java/lang/Thread:getContextClassLoader	()Ljava/lang/ClassLoader;
    //   6: astore_1
    //   7: aload_1
    //   8: aload_0
    //   9: invokevirtual 276	java/lang/ClassLoader:loadClass	(Ljava/lang/String;)Ljava/lang/Class;
    //   12: astore_0
    //   13: aload_0
    //   14: ldc 2
    //   16: invokevirtual 280	java/lang/Class:asSubclass	(Ljava/lang/Class;)Ljava/lang/Class;
    //   19: pop
    //   20: iconst_1
    //   21: ireturn
    //   22: astore_0
    //   23: iconst_0
    //   24: ireturn
    //   25: astore_0
    //   26: iconst_0
    //   27: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	28	0	paramString	String
    //   6	2	1	localClassLoader	ClassLoader
    // Exception table:
    //   from	to	target	type
    //   7	13	22	java/lang/ClassNotFoundException
    //   13	20	25	java/lang/ClassCastException
  }
  
  private final boolean outputConditionsMet()
  {
    Iterator localIterator = this.mOutputPorts.values().iterator();
    while (localIterator.hasNext())
    {
      OutputPort localOutputPort = (OutputPort)localIterator.next();
      if (!localOutputPort.isReady())
      {
        if (this.mLogVerbose) {
          Log.v("Filter", "Output condition not met: " + localOutputPort + "!");
        }
        return false;
      }
    }
    return true;
  }
  
  private final void releasePulledFrames(FilterContext paramFilterContext)
  {
    Iterator localIterator = this.mFramesToRelease.iterator();
    while (localIterator.hasNext())
    {
      Frame localFrame = (Frame)localIterator.next();
      paramFilterContext.getFrameManager().releaseFrame(localFrame);
    }
    this.mFramesToRelease.clear();
  }
  
  private final void setImmediateInputValue(String paramString, Object paramObject)
  {
    if (this.mLogVerbose) {
      Log.v("Filter", "Setting immediate value " + paramObject + " for port " + paramString + "!");
    }
    paramString = getInputPort(paramString);
    paramString.open();
    paramString.setFrame(SimpleFrame.wrapObject(paramObject, null));
  }
  
  private final void setInitialInputValues(KeyValueMap paramKeyValueMap)
  {
    paramKeyValueMap = paramKeyValueMap.entrySet().iterator();
    while (paramKeyValueMap.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)paramKeyValueMap.next();
      setInputValue((String)localEntry.getKey(), localEntry.getValue());
    }
  }
  
  private final void transferInputFrames(FilterContext paramFilterContext)
  {
    Iterator localIterator = this.mInputPorts.values().iterator();
    while (localIterator.hasNext()) {
      ((InputPort)localIterator.next()).transfer(paramFilterContext);
    }
  }
  
  private final Frame wrapInputValue(String paramString, Object paramObject)
  {
    MutableFrameFormat localMutableFrameFormat = ObjectFormat.fromObject(paramObject, 1);
    label46:
    boolean bool;
    if (paramObject == null)
    {
      paramString = getInputPort(paramString).getPortFormat();
      if (paramString == null)
      {
        paramString = null;
        localMutableFrameFormat.setObjectClass(paramString);
      }
    }
    else
    {
      if ((!(paramObject instanceof Number)) && (!(paramObject instanceof Boolean))) {
        break label78;
      }
      bool = false;
      label48:
      if (!bool) {
        break label93;
      }
    }
    label78:
    label93:
    for (paramString = new SerializedFrame(localMutableFrameFormat, null);; paramString = new SimpleFrame(localMutableFrameFormat, null))
    {
      paramString.setObjectValue(paramObject);
      return paramString;
      paramString = paramString.getObjectClass();
      break;
      if ((paramObject instanceof String)) {
        break label46;
      }
      bool = paramObject instanceof Serializable;
      break label48;
    }
  }
  
  protected void addFieldPort(String paramString, Field paramField, boolean paramBoolean1, boolean paramBoolean2)
  {
    paramField.setAccessible(true);
    if (paramBoolean2) {}
    for (Object localObject = new FinalPort(this, paramString, paramField, paramBoolean1);; localObject = new FieldPort(this, paramString, paramField, paramBoolean1))
    {
      if (this.mLogVerbose) {
        Log.v("Filter", "Filter " + this + " adding " + localObject);
      }
      ((FilterPort)localObject).setPortFormat(ObjectFormat.fromClass(paramField.getType(), 1));
      this.mInputPorts.put(paramString, localObject);
      return;
    }
  }
  
  protected void addInputPort(String paramString)
  {
    addMaskedInputPort(paramString, null);
  }
  
  protected void addMaskedInputPort(String paramString, FrameFormat paramFrameFormat)
  {
    StreamPort localStreamPort = new StreamPort(this, paramString);
    if (this.mLogVerbose) {
      Log.v("Filter", "Filter " + this + " adding " + localStreamPort);
    }
    this.mInputPorts.put(paramString, localStreamPort);
    localStreamPort.setPortFormat(paramFrameFormat);
  }
  
  protected void addOutputBasedOnInput(String paramString1, String paramString2)
  {
    OutputPort localOutputPort = new OutputPort(this, paramString1);
    if (this.mLogVerbose) {
      Log.v("Filter", "Filter " + this + " adding " + localOutputPort);
    }
    localOutputPort.setBasePort(getInputPort(paramString2));
    this.mOutputPorts.put(paramString1, localOutputPort);
  }
  
  protected void addOutputPort(String paramString, FrameFormat paramFrameFormat)
  {
    OutputPort localOutputPort = new OutputPort(this, paramString);
    if (this.mLogVerbose) {
      Log.v("Filter", "Filter " + this + " adding " + localOutputPort);
    }
    localOutputPort.setPortFormat(paramFrameFormat);
    this.mOutputPorts.put(paramString, localOutputPort);
  }
  
  protected void addProgramPort(String paramString1, String paramString2, Field paramField, Class paramClass, boolean paramBoolean)
  {
    paramField.setAccessible(true);
    paramString2 = new ProgramPort(this, paramString1, paramString2, paramField, paramBoolean);
    if (this.mLogVerbose) {
      Log.v("Filter", "Filter " + this + " adding " + paramString2);
    }
    paramString2.setPortFormat(ObjectFormat.fromClass(paramClass, 1));
    this.mInputPorts.put(paramString1, paramString2);
  }
  
  final boolean canProcess()
  {
    boolean bool = false;
    try
    {
      if (this.mLogVerbose) {
        Log.v("Filter", "Checking if can process: " + this + " (" + this.mStatus + ").");
      }
      if (this.mStatus <= 3)
      {
        if (inputConditionsMet()) {
          bool = outputConditionsMet();
        }
        return bool;
      }
      return false;
    }
    finally {}
  }
  
  final void clearInputs()
  {
    Iterator localIterator = this.mInputPorts.values().iterator();
    while (localIterator.hasNext()) {
      ((InputPort)localIterator.next()).clear();
    }
  }
  
  final void clearOutputs()
  {
    Iterator localIterator = this.mOutputPorts.values().iterator();
    while (localIterator.hasNext()) {
      ((OutputPort)localIterator.next()).clear();
    }
  }
  
  public void close(FilterContext paramFilterContext) {}
  
  protected void closeOutputPort(String paramString)
  {
    getOutputPort(paramString).close();
  }
  
  protected void delayNextProcess(int paramInt)
  {
    this.mSleepDelay = paramInt;
    this.mStatus = 4;
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext) {}
  
  public String getFilterClassName()
  {
    return getClass().getSimpleName();
  }
  
  public final FrameFormat getInputFormat(String paramString)
  {
    return getInputPort(paramString).getSourceFormat();
  }
  
  public final InputPort getInputPort(String paramString)
  {
    if (this.mInputPorts == null) {
      throw new NullPointerException("Attempting to access input port '" + paramString + "' of " + this + " before Filter has been initialized!");
    }
    InputPort localInputPort = (InputPort)this.mInputPorts.get(paramString);
    if (localInputPort == null) {
      throw new IllegalArgumentException("Unknown input port '" + paramString + "' on filter " + this + "!");
    }
    return localInputPort;
  }
  
  final Collection<InputPort> getInputPorts()
  {
    return this.mInputPorts.values();
  }
  
  public final String getName()
  {
    return this.mName;
  }
  
  public final int getNumberOfConnectedInputs()
  {
    int i = 0;
    Iterator localIterator = this.mInputPorts.values().iterator();
    while (localIterator.hasNext()) {
      if (((InputPort)localIterator.next()).isConnected()) {
        i += 1;
      }
    }
    return i;
  }
  
  public final int getNumberOfConnectedOutputs()
  {
    int i = 0;
    Iterator localIterator = this.mOutputPorts.values().iterator();
    while (localIterator.hasNext()) {
      if (((OutputPort)localIterator.next()).isConnected()) {
        i += 1;
      }
    }
    return i;
  }
  
  public final int getNumberOfInputs()
  {
    if (this.mOutputPorts == null) {
      return 0;
    }
    return this.mInputPorts.size();
  }
  
  public final int getNumberOfOutputs()
  {
    if (this.mInputPorts == null) {
      return 0;
    }
    return this.mOutputPorts.size();
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return null;
  }
  
  public final OutputPort getOutputPort(String paramString)
  {
    if (this.mInputPorts == null) {
      throw new NullPointerException("Attempting to access output port '" + paramString + "' of " + this + " before Filter has been initialized!");
    }
    OutputPort localOutputPort = (OutputPort)this.mOutputPorts.get(paramString);
    if (localOutputPort == null) {
      throw new IllegalArgumentException("Unknown output port '" + paramString + "' on filter " + this + "!");
    }
    return localOutputPort;
  }
  
  final Collection<OutputPort> getOutputPorts()
  {
    return this.mOutputPorts.values();
  }
  
  public final int getSleepDelay()
  {
    return 250;
  }
  
  final int getStatus()
  {
    try
    {
      int i = this.mStatus;
      return i;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  public final void init()
    throws ProtocolException
  {
    initWithValueMap(new KeyValueMap());
  }
  
  protected void initProgramInputs(Program paramProgram, FilterContext paramFilterContext)
  {
    if (paramProgram != null)
    {
      Iterator localIterator = this.mInputPorts.values().iterator();
      while (localIterator.hasNext())
      {
        InputPort localInputPort = (InputPort)localIterator.next();
        if (localInputPort.getTarget() == paramProgram) {
          localInputPort.transfer(paramFilterContext);
        }
      }
    }
  }
  
  public final void initWithAssignmentList(Object... paramVarArgs)
  {
    KeyValueMap localKeyValueMap = new KeyValueMap();
    localKeyValueMap.setKeyValues(paramVarArgs);
    initWithValueMap(localKeyValueMap);
  }
  
  public final void initWithAssignmentString(String paramString)
  {
    try
    {
      initWithValueMap(new TextGraphReader().readKeyValueAssignments(paramString));
      return;
    }
    catch (GraphIOException paramString)
    {
      throw new IllegalArgumentException(paramString.getMessage());
    }
  }
  
  public final void initWithValueMap(KeyValueMap paramKeyValueMap)
  {
    initFinalPorts(paramKeyValueMap);
    initRemainingPorts(paramKeyValueMap);
    this.mStatus = 1;
  }
  
  public boolean isOpen()
  {
    return this.mIsOpen;
  }
  
  final void notifyFieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if ((this.mStatus == 3) || (this.mStatus == 2)) {
      fieldPortValueUpdated(paramString, paramFilterContext);
    }
  }
  
  public void open(FilterContext paramFilterContext) {}
  
  final void openOutputs()
  {
    if (this.mLogVerbose) {
      Log.v("Filter", "Opening all output ports on " + this + "!");
    }
    Iterator localIterator = this.mOutputPorts.values().iterator();
    while (localIterator.hasNext())
    {
      OutputPort localOutputPort = (OutputPort)localIterator.next();
      if (!localOutputPort.isOpen()) {
        localOutputPort.open();
      }
    }
  }
  
  protected void parametersUpdated(Set<String> paramSet) {}
  
  final void performClose(FilterContext paramFilterContext)
  {
    try
    {
      if (this.mIsOpen)
      {
        if (this.mLogVerbose) {
          Log.v("Filter", "Closing " + this);
        }
        this.mIsOpen = false;
        this.mStatus = 2;
        close(paramFilterContext);
        closePorts();
      }
      return;
    }
    finally {}
  }
  
  final void performOpen(FilterContext paramFilterContext)
  {
    try
    {
      if (this.mIsOpen) {
        break label181;
      }
      if (this.mStatus == 1)
      {
        if (this.mLogVerbose) {
          Log.v("Filter", "Preparing " + this);
        }
        prepare(paramFilterContext);
        this.mStatus = 2;
      }
      if (this.mStatus == 2)
      {
        if (this.mLogVerbose) {
          Log.v("Filter", "Opening " + this);
        }
        open(paramFilterContext);
        this.mStatus = 3;
      }
      if (this.mStatus != 3) {
        throw new RuntimeException("Filter " + this + " was brought into invalid state during " + "opening (state: " + this.mStatus + ")!");
      }
    }
    finally {}
    this.mIsOpen = true;
    label181:
  }
  
  final void performProcess(FilterContext paramFilterContext)
  {
    try
    {
      if (this.mStatus == 7) {
        throw new RuntimeException("Filter " + this + " is already torn down!");
      }
    }
    finally {}
    transferInputFrames(paramFilterContext);
    if (this.mStatus < 3) {
      performOpen(paramFilterContext);
    }
    if (this.mLogVerbose) {
      Log.v("Filter", "Processing " + this);
    }
    this.mCurrentTimestamp = -1L;
    process(paramFilterContext);
    releasePulledFrames(paramFilterContext);
    if (filterMustClose()) {
      performClose(paramFilterContext);
    }
  }
  
  final void performTearDown(FilterContext paramFilterContext)
  {
    try
    {
      performClose(paramFilterContext);
      if (this.mStatus != 7)
      {
        tearDown(paramFilterContext);
        this.mStatus = 7;
      }
      return;
    }
    finally
    {
      paramFilterContext = finally;
      throw paramFilterContext;
    }
  }
  
  protected void prepare(FilterContext paramFilterContext) {}
  
  public abstract void process(FilterContext paramFilterContext);
  
  protected final Frame pullInput(String paramString)
  {
    Frame localFrame = getInputPort(paramString).pullFrame();
    if (this.mCurrentTimestamp == -1L)
    {
      this.mCurrentTimestamp = localFrame.getTimestamp();
      if (this.mLogVerbose) {
        Log.v("Filter", "Default-setting current timestamp from input port " + paramString + " to " + this.mCurrentTimestamp);
      }
    }
    this.mFramesToRelease.add(localFrame);
    return localFrame;
  }
  
  final void pushInputFrame(String paramString, Frame paramFrame)
  {
    try
    {
      paramString = getInputPort(paramString);
      if (!paramString.isOpen()) {
        paramString.open();
      }
      paramString.pushFrame(paramFrame);
      return;
    }
    finally {}
  }
  
  final void pushInputValue(String paramString, Object paramObject)
  {
    try
    {
      pushInputFrame(paramString, wrapInputValue(paramString, paramObject));
      return;
    }
    finally
    {
      paramString = finally;
      throw paramString;
    }
  }
  
  protected final void pushOutput(String paramString, Frame paramFrame)
  {
    if (paramFrame.getTimestamp() == -2L)
    {
      if (this.mLogVerbose) {
        Log.v("Filter", "Default-setting output Frame timestamp on port " + paramString + " to " + this.mCurrentTimestamp);
      }
      paramFrame.setTimestamp(this.mCurrentTimestamp);
    }
    getOutputPort(paramString).pushFrame(paramFrame);
  }
  
  public void setInputFrame(String paramString, Frame paramFrame)
  {
    paramString = getInputPort(paramString);
    if (!paramString.isOpen()) {
      paramString.open();
    }
    paramString.setFrame(paramFrame);
  }
  
  public final void setInputValue(String paramString, Object paramObject)
  {
    setInputFrame(paramString, wrapInputValue(paramString, paramObject));
  }
  
  protected void setWaitsOnInputPort(String paramString, boolean paramBoolean)
  {
    getInputPort(paramString).setBlocking(paramBoolean);
  }
  
  protected void setWaitsOnOutputPort(String paramString, boolean paramBoolean)
  {
    getOutputPort(paramString).setBlocking(paramBoolean);
  }
  
  public abstract void setupPorts();
  
  public void tearDown(FilterContext paramFilterContext) {}
  
  public String toString()
  {
    return "'" + getName() + "' (" + getFilterClassName() + ")";
  }
  
  protected void transferInputPortFrame(String paramString, FilterContext paramFilterContext)
  {
    getInputPort(paramString).transfer(paramFilterContext);
  }
  
  final void unsetStatus(int paramInt)
  {
    try
    {
      this.mStatus &= paramInt;
      return;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/Filter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */