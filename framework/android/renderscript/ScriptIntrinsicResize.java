package android.renderscript;

public final class ScriptIntrinsicResize
  extends ScriptIntrinsic
{
  private Allocation mInput;
  
  private ScriptIntrinsicResize(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
  }
  
  public static ScriptIntrinsicResize create(RenderScript paramRenderScript)
  {
    return new ScriptIntrinsicResize(paramRenderScript.nScriptIntrinsicCreate(12, 0L), paramRenderScript);
  }
  
  public void forEach_bicubic(Allocation paramAllocation)
  {
    if (paramAllocation == this.mInput) {
      throw new RSIllegalArgumentException("Output cannot be same as Input.");
    }
    forEach_bicubic(paramAllocation, null);
  }
  
  public void forEach_bicubic(Allocation paramAllocation, Script.LaunchOptions paramLaunchOptions)
  {
    forEach(0, null, paramAllocation, null, paramLaunchOptions);
  }
  
  public Script.FieldID getFieldID_Input()
  {
    return createFieldID(0, null);
  }
  
  public Script.KernelID getKernelID_bicubic()
  {
    return createKernelID(0, 2, null, null);
  }
  
  public void setInput(Allocation paramAllocation)
  {
    Element localElement = paramAllocation.getElement();
    if ((localElement.isCompatible(Element.U8(this.mRS))) || (localElement.isCompatible(Element.U8_2(this.mRS)))) {}
    while ((localElement.isCompatible(Element.U8_3(this.mRS))) || (localElement.isCompatible(Element.U8_4(this.mRS))) || (localElement.isCompatible(Element.F32(this.mRS))) || (localElement.isCompatible(Element.F32_2(this.mRS))) || (localElement.isCompatible(Element.F32_3(this.mRS))) || (localElement.isCompatible(Element.F32_4(this.mRS))))
    {
      this.mInput = paramAllocation;
      setVar(0, paramAllocation);
      return;
    }
    throw new RSIllegalArgumentException("Unsuported element type.");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ScriptIntrinsicResize.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */