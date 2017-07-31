package android.renderscript;

import android.util.Log;
import android.util.Pair;
import dalvik.system.CloseGuard;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class ScriptGroup
  extends BaseObj
{
  private static final String TAG = "ScriptGroup";
  private List<Closure> mClosures;
  IO[] mInputs;
  private List<Input> mInputs2;
  private String mName;
  IO[] mOutputs;
  private Future[] mOutputs2;
  
  ScriptGroup(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
    this.guard.open("destroy");
  }
  
  ScriptGroup(RenderScript paramRenderScript, String paramString, List<Closure> paramList, List<Input> paramList1, Future[] paramArrayOfFuture)
  {
    super(0L, paramRenderScript);
    this.mName = paramString;
    this.mClosures = paramList;
    this.mInputs2 = paramList1;
    this.mOutputs2 = paramArrayOfFuture;
    paramList1 = new long[paramList.size()];
    int i = 0;
    while (i < paramList1.length)
    {
      paramList1[i] = ((Closure)paramList.get(i)).getID(paramRenderScript);
      i += 1;
    }
    setID(paramRenderScript.nScriptGroup2Create(paramString, RenderScript.getCachePath(), paramList1));
    this.guard.open("destroy");
  }
  
  public void destroy()
  {
    super.destroy();
    if (this.mClosures != null)
    {
      Iterator localIterator = this.mClosures.iterator();
      while (localIterator.hasNext()) {
        ((Closure)localIterator.next()).destroy();
      }
    }
  }
  
  public void execute()
  {
    this.mRS.nScriptGroupExecute(getID(this.mRS));
  }
  
  public Object[] execute(Object... paramVarArgs)
  {
    if (paramVarArgs.length < this.mInputs2.size())
    {
      Log.e("ScriptGroup", toString() + " receives " + paramVarArgs.length + " inputs, " + "less than expected " + this.mInputs2.size());
      return null;
    }
    if (paramVarArgs.length > this.mInputs2.size()) {
      Log.i("ScriptGroup", toString() + " receives " + paramVarArgs.length + " inputs, " + "more than expected " + this.mInputs2.size());
    }
    int i = 0;
    Object localObject;
    while (i < this.mInputs2.size())
    {
      localObject = paramVarArgs[i];
      if (((localObject instanceof Future)) || ((localObject instanceof Input)))
      {
        Log.e("ScriptGroup", toString() + ": input " + i + " is a future or unbound value");
        return null;
      }
      ((Input)this.mInputs2.get(i)).set(localObject);
      i += 1;
    }
    this.mRS.nScriptGroup2Execute(getID(this.mRS));
    Object[] arrayOfObject = new Object[this.mOutputs2.length];
    Future[] arrayOfFuture = this.mOutputs2;
    int j = 0;
    int k = arrayOfFuture.length;
    i = 0;
    while (j < k)
    {
      localObject = arrayOfFuture[j].getValue();
      paramVarArgs = (Object[])localObject;
      if ((localObject instanceof Input)) {
        paramVarArgs = ((Input)localObject).get();
      }
      arrayOfObject[i] = paramVarArgs;
      j += 1;
      i += 1;
    }
    return arrayOfObject;
  }
  
  public void setInput(Script.KernelID paramKernelID, Allocation paramAllocation)
  {
    int i = 0;
    while (i < this.mInputs.length)
    {
      if (this.mInputs[i].mKID == paramKernelID)
      {
        this.mInputs[i].mAllocation = paramAllocation;
        this.mRS.nScriptGroupSetInput(getID(this.mRS), paramKernelID.getID(this.mRS), this.mRS.safeID(paramAllocation));
        return;
      }
      i += 1;
    }
    throw new RSIllegalArgumentException("Script not found");
  }
  
  public void setOutput(Script.KernelID paramKernelID, Allocation paramAllocation)
  {
    int i = 0;
    while (i < this.mOutputs.length)
    {
      if (this.mOutputs[i].mKID == paramKernelID)
      {
        this.mOutputs[i].mAllocation = paramAllocation;
        this.mRS.nScriptGroupSetOutput(getID(this.mRS), paramKernelID.getID(this.mRS), this.mRS.safeID(paramAllocation));
        return;
      }
      i += 1;
    }
    throw new RSIllegalArgumentException("Script not found");
  }
  
  public static final class Binding
  {
    private final Script.FieldID mField;
    private final Object mValue;
    
    public Binding(Script.FieldID paramFieldID, Object paramObject)
    {
      this.mField = paramFieldID;
      this.mValue = paramObject;
    }
    
    Script.FieldID getField()
    {
      return this.mField;
    }
    
    Object getValue()
    {
      return this.mValue;
    }
  }
  
  public static final class Builder
  {
    private int mKernelCount;
    private ArrayList<ScriptGroup.ConnectLine> mLines = new ArrayList();
    private ArrayList<ScriptGroup.Node> mNodes = new ArrayList();
    private RenderScript mRS;
    
    public Builder(RenderScript paramRenderScript)
    {
      this.mRS = paramRenderScript;
    }
    
    private ScriptGroup.Node findNode(Script.KernelID paramKernelID)
    {
      int i = 0;
      while (i < this.mNodes.size())
      {
        ScriptGroup.Node localNode = (ScriptGroup.Node)this.mNodes.get(i);
        int j = 0;
        while (j < localNode.mKernels.size())
        {
          if (paramKernelID == localNode.mKernels.get(j)) {
            return localNode;
          }
          j += 1;
        }
        i += 1;
      }
      return null;
    }
    
    private ScriptGroup.Node findNode(Script paramScript)
    {
      int i = 0;
      while (i < this.mNodes.size())
      {
        if (paramScript == ((ScriptGroup.Node)this.mNodes.get(i)).mScript) {
          return (ScriptGroup.Node)this.mNodes.get(i);
        }
        i += 1;
      }
      return null;
    }
    
    private void mergeDAGs(int paramInt1, int paramInt2)
    {
      int i = 0;
      while (i < this.mNodes.size())
      {
        if (((ScriptGroup.Node)this.mNodes.get(i)).dagNumber == paramInt2) {
          ((ScriptGroup.Node)this.mNodes.get(i)).dagNumber = paramInt1;
        }
        i += 1;
      }
    }
    
    private void validateCycle(ScriptGroup.Node paramNode1, ScriptGroup.Node paramNode2)
    {
      int i = 0;
      while (i < paramNode1.mOutputs.size())
      {
        Object localObject = (ScriptGroup.ConnectLine)paramNode1.mOutputs.get(i);
        if (((ScriptGroup.ConnectLine)localObject).mToK != null)
        {
          ScriptGroup.Node localNode = findNode(((ScriptGroup.ConnectLine)localObject).mToK.mScript);
          if (localNode.equals(paramNode2)) {
            throw new RSInvalidStateException("Loops in group not allowed.");
          }
          validateCycle(localNode, paramNode2);
        }
        if (((ScriptGroup.ConnectLine)localObject).mToF != null)
        {
          localObject = findNode(((ScriptGroup.ConnectLine)localObject).mToF.mScript);
          if (localObject.equals(paramNode2)) {
            throw new RSInvalidStateException("Loops in group not allowed.");
          }
          validateCycle((ScriptGroup.Node)localObject, paramNode2);
        }
        i += 1;
      }
    }
    
    private void validateDAG()
    {
      int i = 0;
      while (i < this.mNodes.size())
      {
        ScriptGroup.Node localNode = (ScriptGroup.Node)this.mNodes.get(i);
        if (localNode.mInputs.size() == 0)
        {
          if ((localNode.mOutputs.size() == 0) && (this.mNodes.size() > 1)) {
            throw new RSInvalidStateException("Groups cannot contain unconnected scripts");
          }
          validateDAGRecurse(localNode, i + 1);
        }
        i += 1;
      }
      int j = ((ScriptGroup.Node)this.mNodes.get(0)).dagNumber;
      i = 0;
      while (i < this.mNodes.size())
      {
        if (((ScriptGroup.Node)this.mNodes.get(i)).dagNumber != j) {
          throw new RSInvalidStateException("Multiple DAGs in group not allowed.");
        }
        i += 1;
      }
    }
    
    private void validateDAGRecurse(ScriptGroup.Node paramNode, int paramInt)
    {
      if ((paramNode.dagNumber != 0) && (paramNode.dagNumber != paramInt))
      {
        mergeDAGs(paramNode.dagNumber, paramInt);
        return;
      }
      paramNode.dagNumber = paramInt;
      int i = 0;
      while (i < paramNode.mOutputs.size())
      {
        ScriptGroup.ConnectLine localConnectLine = (ScriptGroup.ConnectLine)paramNode.mOutputs.get(i);
        if (localConnectLine.mToK != null) {
          validateDAGRecurse(findNode(localConnectLine.mToK.mScript), paramInt);
        }
        if (localConnectLine.mToF != null) {
          validateDAGRecurse(findNode(localConnectLine.mToF.mScript), paramInt);
        }
        i += 1;
      }
    }
    
    public Builder addConnection(Type paramType, Script.KernelID paramKernelID, Script.FieldID paramFieldID)
    {
      ScriptGroup.Node localNode1 = findNode(paramKernelID);
      if (localNode1 == null) {
        throw new RSInvalidStateException("From script not found.");
      }
      ScriptGroup.Node localNode2 = findNode(paramFieldID.mScript);
      if (localNode2 == null) {
        throw new RSInvalidStateException("To script not found.");
      }
      ScriptGroup.ConnectLine localConnectLine = new ScriptGroup.ConnectLine(paramType, paramKernelID, paramFieldID);
      this.mLines.add(new ScriptGroup.ConnectLine(paramType, paramKernelID, paramFieldID));
      localNode1.mOutputs.add(localConnectLine);
      localNode2.mInputs.add(localConnectLine);
      validateCycle(localNode1, localNode1);
      return this;
    }
    
    public Builder addConnection(Type paramType, Script.KernelID paramKernelID1, Script.KernelID paramKernelID2)
    {
      ScriptGroup.Node localNode1 = findNode(paramKernelID1);
      if (localNode1 == null) {
        throw new RSInvalidStateException("From script not found.");
      }
      ScriptGroup.Node localNode2 = findNode(paramKernelID2);
      if (localNode2 == null) {
        throw new RSInvalidStateException("To script not found.");
      }
      ScriptGroup.ConnectLine localConnectLine = new ScriptGroup.ConnectLine(paramType, paramKernelID1, paramKernelID2);
      this.mLines.add(new ScriptGroup.ConnectLine(paramType, paramKernelID1, paramKernelID2));
      localNode1.mOutputs.add(localConnectLine);
      localNode2.mInputs.add(localConnectLine);
      validateCycle(localNode1, localNode1);
      return this;
    }
    
    public Builder addKernel(Script.KernelID paramKernelID)
    {
      if (this.mLines.size() != 0) {
        throw new RSInvalidStateException("Kernels may not be added once connections exist.");
      }
      if (findNode(paramKernelID) != null) {
        return this;
      }
      this.mKernelCount += 1;
      ScriptGroup.Node localNode2 = findNode(paramKernelID.mScript);
      ScriptGroup.Node localNode1 = localNode2;
      if (localNode2 == null)
      {
        localNode1 = new ScriptGroup.Node(paramKernelID.mScript);
        this.mNodes.add(localNode1);
      }
      localNode1.mKernels.add(paramKernelID);
      return this;
    }
    
    public ScriptGroup create()
    {
      if (this.mNodes.size() == 0) {
        throw new RSInvalidStateException("Empty script groups are not allowed");
      }
      int i = 0;
      while (i < this.mNodes.size())
      {
        ((ScriptGroup.Node)this.mNodes.get(i)).dagNumber = 0;
        i += 1;
      }
      validateDAG();
      ArrayList localArrayList1 = new ArrayList();
      ArrayList localArrayList2 = new ArrayList();
      Object localObject1 = new long[this.mKernelCount];
      int j = 0;
      i = 0;
      while (i < this.mNodes.size())
      {
        localObject2 = (ScriptGroup.Node)this.mNodes.get(i);
        int k = 0;
        while (k < ((ScriptGroup.Node)localObject2).mKernels.size())
        {
          localObject3 = (Script.KernelID)((ScriptGroup.Node)localObject2).mKernels.get(k);
          localObject1[j] = ((BaseObj)localObject3).getID(this.mRS);
          int m = 0;
          int i1 = 0;
          int n = 0;
          while (n < ((ScriptGroup.Node)localObject2).mInputs.size())
          {
            if (((ScriptGroup.ConnectLine)((ScriptGroup.Node)localObject2).mInputs.get(n)).mToK == localObject3) {
              m = 1;
            }
            n += 1;
          }
          n = 0;
          while (n < ((ScriptGroup.Node)localObject2).mOutputs.size())
          {
            if (((ScriptGroup.ConnectLine)((ScriptGroup.Node)localObject2).mOutputs.get(n)).mFrom == localObject3) {
              i1 = 1;
            }
            n += 1;
          }
          if (m == 0) {
            localArrayList1.add(new ScriptGroup.IO((Script.KernelID)localObject3));
          }
          if (i1 == 0) {
            localArrayList2.add(new ScriptGroup.IO((Script.KernelID)localObject3));
          }
          k += 1;
          j += 1;
        }
        i += 1;
      }
      if (j != this.mKernelCount) {
        throw new RSRuntimeException("Count mismatch, should not happen.");
      }
      Object localObject2 = new long[this.mLines.size()];
      Object localObject3 = new long[this.mLines.size()];
      long[] arrayOfLong1 = new long[this.mLines.size()];
      long[] arrayOfLong2 = new long[this.mLines.size()];
      i = 0;
      while (i < this.mLines.size())
      {
        ScriptGroup.ConnectLine localConnectLine = (ScriptGroup.ConnectLine)this.mLines.get(i);
        localObject2[i] = localConnectLine.mFrom.getID(this.mRS);
        if (localConnectLine.mToK != null) {
          localObject3[i] = localConnectLine.mToK.getID(this.mRS);
        }
        if (localConnectLine.mToF != null) {
          arrayOfLong1[i] = localConnectLine.mToF.getID(this.mRS);
        }
        arrayOfLong2[i] = localConnectLine.mAllocationType.getID(this.mRS);
        i += 1;
      }
      long l = this.mRS.nScriptGroupCreate((long[])localObject1, (long[])localObject2, (long[])localObject3, arrayOfLong1, arrayOfLong2);
      if (l == 0L) {
        throw new RSRuntimeException("Object creation error, should not happen.");
      }
      localObject1 = new ScriptGroup(l, this.mRS);
      ((ScriptGroup)localObject1).mOutputs = new ScriptGroup.IO[localArrayList2.size()];
      i = 0;
      while (i < localArrayList2.size())
      {
        ((ScriptGroup)localObject1).mOutputs[i] = ((ScriptGroup.IO)localArrayList2.get(i));
        i += 1;
      }
      ((ScriptGroup)localObject1).mInputs = new ScriptGroup.IO[localArrayList1.size()];
      i = 0;
      while (i < localArrayList1.size())
      {
        ((ScriptGroup)localObject1).mInputs[i] = ((ScriptGroup.IO)localArrayList1.get(i));
        i += 1;
      }
      return (ScriptGroup)localObject1;
    }
  }
  
  public static final class Builder2
  {
    private static final String TAG = "ScriptGroup.Builder2";
    List<ScriptGroup.Closure> mClosures;
    List<ScriptGroup.Input> mInputs;
    RenderScript mRS;
    
    public Builder2(RenderScript paramRenderScript)
    {
      this.mRS = paramRenderScript;
      this.mClosures = new ArrayList();
      this.mInputs = new ArrayList();
    }
    
    private ScriptGroup.Closure addInvokeInternal(Script.InvokeID paramInvokeID, Object[] paramArrayOfObject, Map<Script.FieldID, Object> paramMap)
    {
      paramInvokeID = new ScriptGroup.Closure(this.mRS, paramInvokeID, paramArrayOfObject, paramMap);
      this.mClosures.add(paramInvokeID);
      return paramInvokeID;
    }
    
    private ScriptGroup.Closure addKernelInternal(Script.KernelID paramKernelID, Type paramType, Object[] paramArrayOfObject, Map<Script.FieldID, Object> paramMap)
    {
      paramKernelID = new ScriptGroup.Closure(this.mRS, paramKernelID, paramType, paramArrayOfObject, paramMap);
      this.mClosures.add(paramKernelID);
      return paramKernelID;
    }
    
    private boolean seperateArgsAndBindings(Object[] paramArrayOfObject, ArrayList<Object> paramArrayList, Map<Script.FieldID, Object> paramMap)
    {
      int i = 0;
      int j = i;
      if (i < paramArrayOfObject.length)
      {
        if (!(paramArrayOfObject[i] instanceof ScriptGroup.Binding)) {
          break label47;
        }
        j = i;
      }
      for (;;)
      {
        if (j >= paramArrayOfObject.length) {
          break label97;
        }
        if (!(paramArrayOfObject[j] instanceof ScriptGroup.Binding))
        {
          return false;
          label47:
          paramArrayList.add(paramArrayOfObject[i]);
          i += 1;
          break;
        }
        paramArrayList = (ScriptGroup.Binding)paramArrayOfObject[j];
        paramMap.put(paramArrayList.getField(), paramArrayList.getValue());
        j += 1;
      }
      label97:
      return true;
    }
    
    public ScriptGroup.Input addInput()
    {
      ScriptGroup.Input localInput = new ScriptGroup.Input();
      this.mInputs.add(localInput);
      return localInput;
    }
    
    public ScriptGroup.Closure addInvoke(Script.InvokeID paramInvokeID, Object... paramVarArgs)
    {
      ArrayList localArrayList = new ArrayList();
      HashMap localHashMap = new HashMap();
      if (!seperateArgsAndBindings(paramVarArgs, localArrayList, localHashMap)) {
        return null;
      }
      return addInvokeInternal(paramInvokeID, localArrayList.toArray(), localHashMap);
    }
    
    public ScriptGroup.Closure addKernel(Script.KernelID paramKernelID, Type paramType, Object... paramVarArgs)
    {
      ArrayList localArrayList = new ArrayList();
      HashMap localHashMap = new HashMap();
      if (!seperateArgsAndBindings(paramVarArgs, localArrayList, localHashMap)) {
        return null;
      }
      return addKernelInternal(paramKernelID, paramType, localArrayList.toArray(), localHashMap);
    }
    
    public ScriptGroup create(String paramString, ScriptGroup.Future... paramVarArgs)
    {
      if ((paramString == null) || (paramString.isEmpty()) || (paramString.length() > 100)) {}
      while (!paramString.equals(paramString.replaceAll("[^a-zA-Z0-9-]", "_"))) {
        throw new RSIllegalArgumentException("invalid script group name");
      }
      paramString = new ScriptGroup(this.mRS, paramString, this.mClosures, this.mInputs, paramVarArgs);
      this.mClosures = new ArrayList();
      this.mInputs = new ArrayList();
      return paramString;
    }
  }
  
  public static final class Closure
    extends BaseObj
  {
    private static final String TAG = "Closure";
    private Object[] mArgs;
    private Map<Script.FieldID, Object> mBindings;
    private FieldPacker mFP;
    private Map<Script.FieldID, ScriptGroup.Future> mGlobalFuture;
    private ScriptGroup.Future mReturnFuture;
    private Allocation mReturnValue;
    
    Closure(long paramLong, RenderScript paramRenderScript)
    {
      super(paramRenderScript);
    }
    
    Closure(RenderScript paramRenderScript, Script.InvokeID paramInvokeID, Object[] paramArrayOfObject, Map<Script.FieldID, Object> paramMap)
    {
      super(paramRenderScript);
      this.mFP = FieldPacker.createFromArray(paramArrayOfObject);
      this.mArgs = paramArrayOfObject;
      this.mBindings = paramMap;
      this.mGlobalFuture = new HashMap();
      int i = paramMap.size();
      paramArrayOfObject = new long[i];
      long[] arrayOfLong1 = new long[i];
      int[] arrayOfInt = new int[i];
      long[] arrayOfLong2 = new long[i];
      long[] arrayOfLong3 = new long[i];
      i = 0;
      paramMap = paramMap.entrySet().iterator();
      while (paramMap.hasNext())
      {
        Object localObject2 = (Map.Entry)paramMap.next();
        Object localObject1 = ((Map.Entry)localObject2).getValue();
        localObject2 = (Script.FieldID)((Map.Entry)localObject2).getKey();
        paramArrayOfObject[i] = ((BaseObj)localObject2).getID(paramRenderScript);
        retrieveValueAndDependenceInfo(paramRenderScript, i, (Script.FieldID)localObject2, localObject1, arrayOfLong1, arrayOfInt, arrayOfLong2, arrayOfLong3);
        i += 1;
      }
      setID(paramRenderScript.nInvokeClosureCreate(paramInvokeID.getID(paramRenderScript), this.mFP.getData(), paramArrayOfObject, arrayOfLong1, arrayOfInt));
      this.guard.open("destroy");
    }
    
    Closure(RenderScript paramRenderScript, Script.KernelID paramKernelID, Type paramType, Object[] paramArrayOfObject, Map<Script.FieldID, Object> paramMap)
    {
      super(paramRenderScript);
      this.mArgs = paramArrayOfObject;
      this.mReturnValue = Allocation.createTyped(paramRenderScript, paramType);
      this.mBindings = paramMap;
      this.mGlobalFuture = new HashMap();
      int i = paramArrayOfObject.length + paramMap.size();
      paramType = new long[i];
      long[] arrayOfLong1 = new long[i];
      int[] arrayOfInt = new int[i];
      long[] arrayOfLong2 = new long[i];
      long[] arrayOfLong3 = new long[i];
      i = 0;
      while (i < paramArrayOfObject.length)
      {
        paramType[i] = 0L;
        retrieveValueAndDependenceInfo(paramRenderScript, i, null, paramArrayOfObject[i], arrayOfLong1, arrayOfInt, arrayOfLong2, arrayOfLong3);
        i += 1;
      }
      paramArrayOfObject = paramMap.entrySet().iterator();
      while (paramArrayOfObject.hasNext())
      {
        Object localObject = (Map.Entry)paramArrayOfObject.next();
        paramMap = ((Map.Entry)localObject).getValue();
        localObject = (Script.FieldID)((Map.Entry)localObject).getKey();
        paramType[i] = ((BaseObj)localObject).getID(paramRenderScript);
        retrieveValueAndDependenceInfo(paramRenderScript, i, (Script.FieldID)localObject, paramMap, arrayOfLong1, arrayOfInt, arrayOfLong2, arrayOfLong3);
        i += 1;
      }
      setID(paramRenderScript.nClosureCreate(paramKernelID.getID(paramRenderScript), this.mReturnValue.getID(paramRenderScript), paramType, arrayOfLong1, arrayOfInt, arrayOfLong2, arrayOfLong3));
      this.guard.open("destroy");
    }
    
    private void retrieveValueAndDependenceInfo(RenderScript paramRenderScript, int paramInt, Script.FieldID paramFieldID, Object paramObject, long[] paramArrayOfLong1, int[] paramArrayOfInt, long[] paramArrayOfLong2, long[] paramArrayOfLong3)
    {
      long l;
      if ((paramObject instanceof ScriptGroup.Future))
      {
        Object localObject = ((ScriptGroup.Future)paramObject).getValue();
        paramArrayOfLong2[paramInt] = ((ScriptGroup.Future)paramObject).getClosure().getID(paramRenderScript);
        paramObject = ((ScriptGroup.Future)paramObject).getFieldID();
        if (paramObject != null)
        {
          l = ((BaseObj)paramObject).getID(paramRenderScript);
          paramArrayOfLong3[paramInt] = l;
          paramObject = localObject;
          label58:
          if (!(paramObject instanceof ScriptGroup.Input)) {
            break label122;
          }
          if (paramInt >= this.mArgs.length) {
            break label112;
          }
          ((ScriptGroup.Input)paramObject).addReference(this, paramInt);
        }
      }
      for (;;)
      {
        paramArrayOfLong1[paramInt] = 0L;
        paramArrayOfInt[paramInt] = 0;
        return;
        l = 0L;
        break;
        paramArrayOfLong2[paramInt] = 0L;
        paramArrayOfLong3[paramInt] = 0L;
        break label58;
        label112:
        ((ScriptGroup.Input)paramObject).addReference(this, paramFieldID);
      }
      label122:
      paramRenderScript = new ValueAndSize(paramRenderScript, paramObject);
      paramArrayOfLong1[paramInt] = paramRenderScript.value;
      paramArrayOfInt[paramInt] = paramRenderScript.size;
    }
    
    public void destroy()
    {
      super.destroy();
      if (this.mReturnValue != null) {
        this.mReturnValue.destroy();
      }
    }
    
    protected void finalize()
      throws Throwable
    {
      this.mReturnValue = null;
      super.finalize();
    }
    
    public ScriptGroup.Future getGlobal(Script.FieldID paramFieldID)
    {
      Object localObject2 = (ScriptGroup.Future)this.mGlobalFuture.get(paramFieldID);
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject2 = this.mBindings.get(paramFieldID);
        localObject1 = localObject2;
        if ((localObject2 instanceof ScriptGroup.Future)) {
          localObject1 = ((ScriptGroup.Future)localObject2).getValue();
        }
        localObject1 = new ScriptGroup.Future(this, paramFieldID, localObject1);
        this.mGlobalFuture.put(paramFieldID, localObject1);
      }
      return (ScriptGroup.Future)localObject1;
    }
    
    public ScriptGroup.Future getReturn()
    {
      if (this.mReturnFuture == null) {
        this.mReturnFuture = new ScriptGroup.Future(this, null, this.mReturnValue);
      }
      return this.mReturnFuture;
    }
    
    void setArg(int paramInt, Object paramObject)
    {
      Object localObject = paramObject;
      if ((paramObject instanceof ScriptGroup.Future)) {
        localObject = ((ScriptGroup.Future)paramObject).getValue();
      }
      this.mArgs[paramInt] = localObject;
      paramObject = new ValueAndSize(this.mRS, localObject);
      this.mRS.nClosureSetArg(getID(this.mRS), paramInt, ((ValueAndSize)paramObject).value, ((ValueAndSize)paramObject).size);
    }
    
    void setGlobal(Script.FieldID paramFieldID, Object paramObject)
    {
      Object localObject = paramObject;
      if ((paramObject instanceof ScriptGroup.Future)) {
        localObject = ((ScriptGroup.Future)paramObject).getValue();
      }
      this.mBindings.put(paramFieldID, localObject);
      paramObject = new ValueAndSize(this.mRS, localObject);
      this.mRS.nClosureSetGlobal(getID(this.mRS), paramFieldID.getID(this.mRS), ((ValueAndSize)paramObject).value, ((ValueAndSize)paramObject).size);
    }
    
    private static final class ValueAndSize
    {
      public int size;
      public long value;
      
      public ValueAndSize(RenderScript paramRenderScript, Object paramObject)
      {
        if ((paramObject instanceof Allocation))
        {
          this.value = ((BaseObj)paramObject).getID(paramRenderScript);
          this.size = -1;
        }
        do
        {
          return;
          if ((paramObject instanceof Boolean))
          {
            if (((Boolean)paramObject).booleanValue()) {}
            for (int i = 1;; i = 0)
            {
              this.value = i;
              this.size = 4;
              return;
            }
          }
          if ((paramObject instanceof Integer))
          {
            this.value = ((Integer)paramObject).longValue();
            this.size = 4;
            return;
          }
          if ((paramObject instanceof Long))
          {
            this.value = ((Long)paramObject).longValue();
            this.size = 8;
            return;
          }
          if ((paramObject instanceof Float))
          {
            this.value = Float.floatToRawIntBits(((Float)paramObject).floatValue());
            this.size = 4;
            return;
          }
        } while (!(paramObject instanceof Double));
        this.value = Double.doubleToRawLongBits(((Double)paramObject).doubleValue());
        this.size = 8;
      }
    }
  }
  
  static class ConnectLine
  {
    Type mAllocationType;
    Script.KernelID mFrom;
    Script.FieldID mToF;
    Script.KernelID mToK;
    
    ConnectLine(Type paramType, Script.KernelID paramKernelID, Script.FieldID paramFieldID)
    {
      this.mFrom = paramKernelID;
      this.mToF = paramFieldID;
      this.mAllocationType = paramType;
    }
    
    ConnectLine(Type paramType, Script.KernelID paramKernelID1, Script.KernelID paramKernelID2)
    {
      this.mFrom = paramKernelID1;
      this.mToK = paramKernelID2;
      this.mAllocationType = paramType;
    }
  }
  
  public static final class Future
  {
    ScriptGroup.Closure mClosure;
    Script.FieldID mFieldID;
    Object mValue;
    
    Future(ScriptGroup.Closure paramClosure, Script.FieldID paramFieldID, Object paramObject)
    {
      this.mClosure = paramClosure;
      this.mFieldID = paramFieldID;
      this.mValue = paramObject;
    }
    
    ScriptGroup.Closure getClosure()
    {
      return this.mClosure;
    }
    
    Script.FieldID getFieldID()
    {
      return this.mFieldID;
    }
    
    Object getValue()
    {
      return this.mValue;
    }
  }
  
  static class IO
  {
    Allocation mAllocation;
    Script.KernelID mKID;
    
    IO(Script.KernelID paramKernelID)
    {
      this.mKID = paramKernelID;
    }
  }
  
  public static final class Input
  {
    List<Pair<ScriptGroup.Closure, Integer>> mArgIndex = new ArrayList();
    List<Pair<ScriptGroup.Closure, Script.FieldID>> mFieldID = new ArrayList();
    Object mValue;
    
    void addReference(ScriptGroup.Closure paramClosure, int paramInt)
    {
      this.mArgIndex.add(Pair.create(paramClosure, Integer.valueOf(paramInt)));
    }
    
    void addReference(ScriptGroup.Closure paramClosure, Script.FieldID paramFieldID)
    {
      this.mFieldID.add(Pair.create(paramClosure, paramFieldID));
    }
    
    Object get()
    {
      return this.mValue;
    }
    
    void set(Object paramObject)
    {
      this.mValue = paramObject;
      Iterator localIterator = this.mArgIndex.iterator();
      Pair localPair;
      while (localIterator.hasNext())
      {
        localPair = (Pair)localIterator.next();
        ((ScriptGroup.Closure)localPair.first).setArg(((Integer)localPair.second).intValue(), paramObject);
      }
      localIterator = this.mFieldID.iterator();
      while (localIterator.hasNext())
      {
        localPair = (Pair)localIterator.next();
        ((ScriptGroup.Closure)localPair.first).setGlobal((Script.FieldID)localPair.second, paramObject);
      }
    }
  }
  
  static class Node
  {
    int dagNumber;
    ArrayList<ScriptGroup.ConnectLine> mInputs = new ArrayList();
    ArrayList<Script.KernelID> mKernels = new ArrayList();
    Node mNext;
    ArrayList<ScriptGroup.ConnectLine> mOutputs = new ArrayList();
    Script mScript;
    
    Node(Script paramScript)
    {
      this.mScript = paramScript;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ScriptGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */