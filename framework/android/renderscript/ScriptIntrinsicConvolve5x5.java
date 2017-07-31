package android.renderscript;

public final class ScriptIntrinsicConvolve5x5
  extends ScriptIntrinsic
{
  private Allocation mInput;
  private final float[] mValues = new float[25];
  
  private ScriptIntrinsicConvolve5x5(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
  }
  
  public static ScriptIntrinsicConvolve5x5 create(RenderScript paramRenderScript, Element paramElement)
  {
    if ((paramElement.isCompatible(Element.U8(paramRenderScript))) || (paramElement.isCompatible(Element.U8_2(paramRenderScript)))) {}
    while ((paramElement.isCompatible(Element.U8_3(paramRenderScript))) || (paramElement.isCompatible(Element.U8_4(paramRenderScript))) || (paramElement.isCompatible(Element.F32(paramRenderScript))) || (paramElement.isCompatible(Element.F32_2(paramRenderScript))) || (paramElement.isCompatible(Element.F32_3(paramRenderScript))) || (paramElement.isCompatible(Element.F32_4(paramRenderScript)))) {
      return new ScriptIntrinsicConvolve5x5(paramRenderScript.nScriptIntrinsicCreate(4, paramElement.getID(paramRenderScript)), paramRenderScript);
    }
    throw new RSIllegalArgumentException("Unsuported element type.");
  }
  
  public void forEach(Allocation paramAllocation)
  {
    forEach(0, null, paramAllocation, null);
  }
  
  public void forEach(Allocation paramAllocation, Script.LaunchOptions paramLaunchOptions)
  {
    forEach(0, null, paramAllocation, null, paramLaunchOptions);
  }
  
  public Script.FieldID getFieldID_Input()
  {
    return createFieldID(1, null);
  }
  
  public Script.KernelID getKernelID()
  {
    return createKernelID(0, 2, null, null);
  }
  
  public void setCoefficients(float[] paramArrayOfFloat)
  {
    FieldPacker localFieldPacker = new FieldPacker(100);
    int i = 0;
    while (i < this.mValues.length)
    {
      this.mValues[i] = paramArrayOfFloat[i];
      localFieldPacker.addF32(this.mValues[i]);
      i += 1;
    }
    setVar(0, localFieldPacker);
  }
  
  public void setInput(Allocation paramAllocation)
  {
    this.mInput = paramAllocation;
    setVar(1, paramAllocation);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ScriptIntrinsicConvolve5x5.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */