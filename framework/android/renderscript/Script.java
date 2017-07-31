package android.renderscript;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.SparseArray;
import dalvik.system.CloseGuard;
import java.io.UnsupportedEncodingException;

public class Script
  extends BaseObj
{
  private final SparseArray<FieldID> mFIDs = new SparseArray();
  private final SparseArray<InvokeID> mIIDs = new SparseArray();
  long[] mInIdsBuffer = new long[1];
  private final SparseArray<KernelID> mKIDs = new SparseArray();
  
  Script(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
    this.guard.open("destroy");
  }
  
  public void bindAllocation(Allocation paramAllocation, int paramInt)
  {
    this.mRS.validate();
    this.mRS.validateObject(paramAllocation);
    if (paramAllocation != null)
    {
      if (this.mRS.getApplicationContext().getApplicationInfo().targetSdkVersion >= 20)
      {
        Type localType = paramAllocation.mType;
        if ((localType.hasMipmaps()) || (localType.hasFaces()) || (localType.getY() != 0)) {}
        while (localType.getZ() != 0) {
          throw new RSIllegalArgumentException("API 20+ only allows simple 1D allocations to be used with bind.");
        }
      }
      this.mRS.nScriptBindAllocation(getID(this.mRS), paramAllocation.getID(this.mRS), paramInt);
      return;
    }
    this.mRS.nScriptBindAllocation(getID(this.mRS), 0L, paramInt);
  }
  
  protected FieldID createFieldID(int paramInt, Element paramElement)
  {
    paramElement = (FieldID)this.mFIDs.get(paramInt);
    if (paramElement != null) {
      return paramElement;
    }
    long l = this.mRS.nScriptFieldIDCreate(getID(this.mRS), paramInt);
    if (l == 0L) {
      throw new RSDriverException("Failed to create FieldID");
    }
    paramElement = new FieldID(l, this.mRS, this, paramInt);
    this.mFIDs.put(paramInt, paramElement);
    return paramElement;
  }
  
  protected InvokeID createInvokeID(int paramInt)
  {
    InvokeID localInvokeID = (InvokeID)this.mIIDs.get(paramInt);
    if (localInvokeID != null) {
      return localInvokeID;
    }
    long l = this.mRS.nScriptInvokeIDCreate(getID(this.mRS), paramInt);
    if (l == 0L) {
      throw new RSDriverException("Failed to create KernelID");
    }
    localInvokeID = new InvokeID(l, this.mRS, this, paramInt);
    this.mIIDs.put(paramInt, localInvokeID);
    return localInvokeID;
  }
  
  protected KernelID createKernelID(int paramInt1, int paramInt2, Element paramElement1, Element paramElement2)
  {
    paramElement1 = (KernelID)this.mKIDs.get(paramInt1);
    if (paramElement1 != null) {
      return paramElement1;
    }
    long l = this.mRS.nScriptKernelIDCreate(getID(this.mRS), paramInt1, paramInt2);
    if (l == 0L) {
      throw new RSDriverException("Failed to create KernelID");
    }
    paramElement1 = new KernelID(l, this.mRS, this, paramInt1, paramInt2);
    this.mKIDs.put(paramInt1, paramElement1);
    return paramElement1;
  }
  
  protected void forEach(int paramInt, Allocation paramAllocation1, Allocation paramAllocation2, FieldPacker paramFieldPacker)
  {
    forEach(paramInt, paramAllocation1, paramAllocation2, paramFieldPacker, null);
  }
  
  protected void forEach(int paramInt, Allocation paramAllocation1, Allocation paramAllocation2, FieldPacker paramFieldPacker, LaunchOptions paramLaunchOptions)
  {
    this.mRS.validate();
    this.mRS.validateObject(paramAllocation1);
    this.mRS.validateObject(paramAllocation2);
    if ((paramAllocation1 == null) && (paramAllocation2 == null) && (paramLaunchOptions == null)) {
      throw new RSIllegalArgumentException("At least one of input allocation, output allocation, or LaunchOptions is required to be non-null.");
    }
    long[] arrayOfLong = null;
    if (paramAllocation1 != null)
    {
      arrayOfLong = this.mInIdsBuffer;
      arrayOfLong[0] = paramAllocation1.getID(this.mRS);
    }
    long l = 0L;
    if (paramAllocation2 != null) {
      l = paramAllocation2.getID(this.mRS);
    }
    paramAllocation1 = null;
    if (paramFieldPacker != null) {
      paramAllocation1 = paramFieldPacker.getData();
    }
    paramAllocation2 = null;
    if (paramLaunchOptions != null)
    {
      paramAllocation2 = new int[6];
      paramAllocation2[0] = LaunchOptions.-get1(paramLaunchOptions);
      paramAllocation2[1] = LaunchOptions.-get0(paramLaunchOptions);
      paramAllocation2[2] = LaunchOptions.-get3(paramLaunchOptions);
      paramAllocation2[3] = LaunchOptions.-get2(paramLaunchOptions);
      paramAllocation2[4] = LaunchOptions.-get5(paramLaunchOptions);
      paramAllocation2[5] = LaunchOptions.-get4(paramLaunchOptions);
    }
    this.mRS.nScriptForEach(getID(this.mRS), paramInt, arrayOfLong, l, paramAllocation1, paramAllocation2);
  }
  
  protected void forEach(int paramInt, Allocation[] paramArrayOfAllocation, Allocation paramAllocation, FieldPacker paramFieldPacker)
  {
    forEach(paramInt, paramArrayOfAllocation, paramAllocation, paramFieldPacker, null);
  }
  
  protected void forEach(int paramInt, Allocation[] paramArrayOfAllocation, Allocation paramAllocation, FieldPacker paramFieldPacker, LaunchOptions paramLaunchOptions)
  {
    this.mRS.validate();
    int i;
    if (paramArrayOfAllocation != null)
    {
      i = 0;
      int j = paramArrayOfAllocation.length;
      while (i < j)
      {
        localObject = paramArrayOfAllocation[i];
        this.mRS.validateObject((BaseObj)localObject);
        i += 1;
      }
    }
    this.mRS.validateObject(paramAllocation);
    if ((paramArrayOfAllocation == null) && (paramAllocation == null)) {
      throw new RSIllegalArgumentException("At least one of ain or aout is required to be non-null.");
    }
    if (paramArrayOfAllocation != null)
    {
      long[] arrayOfLong = new long[paramArrayOfAllocation.length];
      i = 0;
      for (;;)
      {
        localObject = arrayOfLong;
        if (i >= paramArrayOfAllocation.length) {
          break;
        }
        arrayOfLong[i] = paramArrayOfAllocation[i].getID(this.mRS);
        i += 1;
      }
    }
    Object localObject = null;
    long l = 0L;
    if (paramAllocation != null) {
      l = paramAllocation.getID(this.mRS);
    }
    paramArrayOfAllocation = null;
    if (paramFieldPacker != null) {
      paramArrayOfAllocation = paramFieldPacker.getData();
    }
    paramAllocation = null;
    if (paramLaunchOptions != null)
    {
      paramAllocation = new int[6];
      paramAllocation[0] = LaunchOptions.-get1(paramLaunchOptions);
      paramAllocation[1] = LaunchOptions.-get0(paramLaunchOptions);
      paramAllocation[2] = LaunchOptions.-get3(paramLaunchOptions);
      paramAllocation[3] = LaunchOptions.-get2(paramLaunchOptions);
      paramAllocation[4] = LaunchOptions.-get5(paramLaunchOptions);
      paramAllocation[5] = LaunchOptions.-get4(paramLaunchOptions);
    }
    this.mRS.nScriptForEach(getID(this.mRS), paramInt, (long[])localObject, l, paramArrayOfAllocation, paramAllocation);
  }
  
  public boolean getVarB(int paramInt)
  {
    boolean bool = false;
    if (this.mRS.nScriptGetVarI(getID(this.mRS), paramInt) > 0) {
      bool = true;
    }
    return bool;
  }
  
  public double getVarD(int paramInt)
  {
    return this.mRS.nScriptGetVarD(getID(this.mRS), paramInt);
  }
  
  public float getVarF(int paramInt)
  {
    return this.mRS.nScriptGetVarF(getID(this.mRS), paramInt);
  }
  
  public int getVarI(int paramInt)
  {
    return this.mRS.nScriptGetVarI(getID(this.mRS), paramInt);
  }
  
  public long getVarJ(int paramInt)
  {
    return this.mRS.nScriptGetVarJ(getID(this.mRS), paramInt);
  }
  
  public void getVarV(int paramInt, FieldPacker paramFieldPacker)
  {
    this.mRS.nScriptGetVarV(getID(this.mRS), paramInt, paramFieldPacker.getData());
  }
  
  protected void invoke(int paramInt)
  {
    this.mRS.nScriptInvoke(getID(this.mRS), paramInt);
  }
  
  protected void invoke(int paramInt, FieldPacker paramFieldPacker)
  {
    if (paramFieldPacker != null)
    {
      this.mRS.nScriptInvokeV(getID(this.mRS), paramInt, paramFieldPacker.getData());
      return;
    }
    this.mRS.nScriptInvoke(getID(this.mRS), paramInt);
  }
  
  protected void reduce(int paramInt, Allocation[] paramArrayOfAllocation, Allocation paramAllocation, LaunchOptions paramLaunchOptions)
  {
    this.mRS.validate();
    if ((paramArrayOfAllocation == null) || (paramArrayOfAllocation.length < 1)) {
      throw new RSIllegalArgumentException("At least one input is required.");
    }
    if (paramAllocation == null) {
      throw new RSIllegalArgumentException("aout is required to be non-null.");
    }
    int i = 0;
    int j = paramArrayOfAllocation.length;
    while (i < j)
    {
      localObject = paramArrayOfAllocation[i];
      this.mRS.validateObject((BaseObj)localObject);
      i += 1;
    }
    Object localObject = new long[paramArrayOfAllocation.length];
    i = 0;
    while (i < paramArrayOfAllocation.length)
    {
      localObject[i] = paramArrayOfAllocation[i].getID(this.mRS);
      i += 1;
    }
    long l = paramAllocation.getID(this.mRS);
    paramArrayOfAllocation = null;
    if (paramLaunchOptions != null)
    {
      paramArrayOfAllocation = new int[6];
      paramArrayOfAllocation[0] = LaunchOptions.-get1(paramLaunchOptions);
      paramArrayOfAllocation[1] = LaunchOptions.-get0(paramLaunchOptions);
      paramArrayOfAllocation[2] = LaunchOptions.-get3(paramLaunchOptions);
      paramArrayOfAllocation[3] = LaunchOptions.-get2(paramLaunchOptions);
      paramArrayOfAllocation[4] = LaunchOptions.-get5(paramLaunchOptions);
      paramArrayOfAllocation[5] = LaunchOptions.-get4(paramLaunchOptions);
    }
    this.mRS.nScriptReduce(getID(this.mRS), paramInt, (long[])localObject, l, paramArrayOfAllocation);
  }
  
  public void setTimeZone(String paramString)
  {
    this.mRS.validate();
    try
    {
      this.mRS.nScriptSetTimeZone(getID(this.mRS), paramString.getBytes("UTF-8"));
      return;
    }
    catch (UnsupportedEncodingException paramString)
    {
      throw new RuntimeException(paramString);
    }
  }
  
  public void setVar(int paramInt, double paramDouble)
  {
    this.mRS.nScriptSetVarD(getID(this.mRS), paramInt, paramDouble);
  }
  
  public void setVar(int paramInt, float paramFloat)
  {
    this.mRS.nScriptSetVarF(getID(this.mRS), paramInt, paramFloat);
  }
  
  public void setVar(int paramInt1, int paramInt2)
  {
    this.mRS.nScriptSetVarI(getID(this.mRS), paramInt1, paramInt2);
  }
  
  public void setVar(int paramInt, long paramLong)
  {
    this.mRS.nScriptSetVarJ(getID(this.mRS), paramInt, paramLong);
  }
  
  public void setVar(int paramInt, BaseObj paramBaseObj)
  {
    this.mRS.validate();
    this.mRS.validateObject(paramBaseObj);
    RenderScript localRenderScript = this.mRS;
    long l2 = getID(this.mRS);
    if (paramBaseObj == null) {}
    for (long l1 = 0L;; l1 = paramBaseObj.getID(this.mRS))
    {
      localRenderScript.nScriptSetVarObj(l2, paramInt, l1);
      return;
    }
  }
  
  public void setVar(int paramInt, FieldPacker paramFieldPacker)
  {
    this.mRS.nScriptSetVarV(getID(this.mRS), paramInt, paramFieldPacker.getData());
  }
  
  public void setVar(int paramInt, FieldPacker paramFieldPacker, Element paramElement, int[] paramArrayOfInt)
  {
    this.mRS.nScriptSetVarVE(getID(this.mRS), paramInt, paramFieldPacker.getData(), paramElement.getID(this.mRS), paramArrayOfInt);
  }
  
  public void setVar(int paramInt, boolean paramBoolean)
  {
    RenderScript localRenderScript = this.mRS;
    long l = getID(this.mRS);
    if (paramBoolean) {}
    for (int i = 1;; i = 0)
    {
      localRenderScript.nScriptSetVarI(l, paramInt, i);
      return;
    }
  }
  
  public static class Builder
  {
    RenderScript mRS;
    
    Builder(RenderScript paramRenderScript)
    {
      this.mRS = paramRenderScript;
    }
  }
  
  public static class FieldBase
  {
    protected Allocation mAllocation;
    protected Element mElement;
    
    public Allocation getAllocation()
    {
      return this.mAllocation;
    }
    
    public Element getElement()
    {
      return this.mElement;
    }
    
    public Type getType()
    {
      return this.mAllocation.getType();
    }
    
    protected void init(RenderScript paramRenderScript, int paramInt)
    {
      this.mAllocation = Allocation.createSized(paramRenderScript, this.mElement, paramInt, 1);
    }
    
    protected void init(RenderScript paramRenderScript, int paramInt1, int paramInt2)
    {
      this.mAllocation = Allocation.createSized(paramRenderScript, this.mElement, paramInt1, paramInt2 | 0x1);
    }
    
    public void updateAllocation() {}
  }
  
  public static final class FieldID
    extends BaseObj
  {
    Script mScript;
    int mSlot;
    
    FieldID(long paramLong, RenderScript paramRenderScript, Script paramScript, int paramInt)
    {
      super(paramRenderScript);
      this.mScript = paramScript;
      this.mSlot = paramInt;
      this.guard.open("destroy");
    }
  }
  
  public static final class InvokeID
    extends BaseObj
  {
    Script mScript;
    int mSlot;
    
    InvokeID(long paramLong, RenderScript paramRenderScript, Script paramScript, int paramInt)
    {
      super(paramRenderScript);
      this.mScript = paramScript;
      this.mSlot = paramInt;
    }
  }
  
  public static final class KernelID
    extends BaseObj
  {
    Script mScript;
    int mSig;
    int mSlot;
    
    KernelID(long paramLong, RenderScript paramRenderScript, Script paramScript, int paramInt1, int paramInt2)
    {
      super(paramRenderScript);
      this.mScript = paramScript;
      this.mSlot = paramInt1;
      this.mSig = paramInt2;
      this.guard.open("destroy");
    }
  }
  
  public static final class LaunchOptions
  {
    private int strategy;
    private int xend = 0;
    private int xstart = 0;
    private int yend = 0;
    private int ystart = 0;
    private int zend = 0;
    private int zstart = 0;
    
    public int getXEnd()
    {
      return this.xend;
    }
    
    public int getXStart()
    {
      return this.xstart;
    }
    
    public int getYEnd()
    {
      return this.yend;
    }
    
    public int getYStart()
    {
      return this.ystart;
    }
    
    public int getZEnd()
    {
      return this.zend;
    }
    
    public int getZStart()
    {
      return this.zstart;
    }
    
    public LaunchOptions setX(int paramInt1, int paramInt2)
    {
      if ((paramInt1 < 0) || (paramInt2 <= paramInt1)) {
        throw new RSIllegalArgumentException("Invalid dimensions");
      }
      this.xstart = paramInt1;
      this.xend = paramInt2;
      return this;
    }
    
    public LaunchOptions setY(int paramInt1, int paramInt2)
    {
      if ((paramInt1 < 0) || (paramInt2 <= paramInt1)) {
        throw new RSIllegalArgumentException("Invalid dimensions");
      }
      this.ystart = paramInt1;
      this.yend = paramInt2;
      return this;
    }
    
    public LaunchOptions setZ(int paramInt1, int paramInt2)
    {
      if ((paramInt1 < 0) || (paramInt2 <= paramInt1)) {
        throw new RSIllegalArgumentException("Invalid dimensions");
      }
      this.zstart = paramInt1;
      this.zend = paramInt2;
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/Script.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */