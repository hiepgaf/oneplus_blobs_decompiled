package android.renderscript;

public final class ScriptIntrinsicYuvToRGB
  extends ScriptIntrinsic
{
  private Allocation mInput;
  
  ScriptIntrinsicYuvToRGB(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
  }
  
  public static ScriptIntrinsicYuvToRGB create(RenderScript paramRenderScript, Element paramElement)
  {
    return new ScriptIntrinsicYuvToRGB(paramRenderScript.nScriptIntrinsicCreate(6, paramElement.getID(paramRenderScript)), paramRenderScript);
  }
  
  public void forEach(Allocation paramAllocation)
  {
    forEach(0, null, paramAllocation, null);
  }
  
  public Script.FieldID getFieldID_Input()
  {
    return createFieldID(0, null);
  }
  
  public Script.KernelID getKernelID()
  {
    return createKernelID(0, 2, null, null);
  }
  
  public void setInput(Allocation paramAllocation)
  {
    this.mInput = paramAllocation;
    setVar(0, paramAllocation);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ScriptIntrinsicYuvToRGB.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */