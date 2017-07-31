package android.renderscript;

public final class ScriptIntrinsicLUT
  extends ScriptIntrinsic
{
  private final byte[] mCache = new byte['Ѐ'];
  private boolean mDirty = true;
  private final Matrix4f mMatrix = new Matrix4f();
  private Allocation mTables;
  
  private ScriptIntrinsicLUT(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
    this.mTables = Allocation.createSized(paramRenderScript, Element.U8(paramRenderScript), 1024);
    int i = 0;
    while (i < 256)
    {
      this.mCache[i] = ((byte)i);
      this.mCache[(i + 256)] = ((byte)i);
      this.mCache[(i + 512)] = ((byte)i);
      this.mCache[(i + 768)] = ((byte)i);
      i += 1;
    }
    setVar(0, this.mTables);
  }
  
  public static ScriptIntrinsicLUT create(RenderScript paramRenderScript, Element paramElement)
  {
    return new ScriptIntrinsicLUT(paramRenderScript.nScriptIntrinsicCreate(3, paramElement.getID(paramRenderScript)), paramRenderScript);
  }
  
  private void validate(int paramInt1, int paramInt2)
  {
    if ((paramInt1 < 0) || (paramInt1 > 255)) {
      throw new RSIllegalArgumentException("Index out of range (0-255).");
    }
    if ((paramInt2 < 0) || (paramInt2 > 255)) {
      throw new RSIllegalArgumentException("Value out of range (0-255).");
    }
  }
  
  public void forEach(Allocation paramAllocation1, Allocation paramAllocation2)
  {
    forEach(paramAllocation1, paramAllocation2, null);
  }
  
  public void forEach(Allocation paramAllocation1, Allocation paramAllocation2, Script.LaunchOptions paramLaunchOptions)
  {
    if (this.mDirty)
    {
      this.mDirty = false;
      this.mTables.copyFromUnchecked(this.mCache);
    }
    forEach(0, paramAllocation1, paramAllocation2, null, paramLaunchOptions);
  }
  
  public Script.KernelID getKernelID()
  {
    return createKernelID(0, 3, null, null);
  }
  
  public void setAlpha(int paramInt1, int paramInt2)
  {
    validate(paramInt1, paramInt2);
    this.mCache[(paramInt1 + 768)] = ((byte)paramInt2);
    this.mDirty = true;
  }
  
  public void setBlue(int paramInt1, int paramInt2)
  {
    validate(paramInt1, paramInt2);
    this.mCache[(paramInt1 + 512)] = ((byte)paramInt2);
    this.mDirty = true;
  }
  
  public void setGreen(int paramInt1, int paramInt2)
  {
    validate(paramInt1, paramInt2);
    this.mCache[(paramInt1 + 256)] = ((byte)paramInt2);
    this.mDirty = true;
  }
  
  public void setRed(int paramInt1, int paramInt2)
  {
    validate(paramInt1, paramInt2);
    this.mCache[paramInt1] = ((byte)paramInt2);
    this.mDirty = true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ScriptIntrinsicLUT.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */