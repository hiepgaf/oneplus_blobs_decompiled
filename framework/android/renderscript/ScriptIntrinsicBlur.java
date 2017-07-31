package android.renderscript;

public final class ScriptIntrinsicBlur
  extends ScriptIntrinsic
{
  private Allocation mInput;
  private final float[] mValues = new float[9];
  
  private ScriptIntrinsicBlur(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
  }
  
  public static ScriptIntrinsicBlur create(RenderScript paramRenderScript, Element paramElement)
  {
    if ((paramElement.isCompatible(Element.U8_4(paramRenderScript))) || (paramElement.isCompatible(Element.U8(paramRenderScript))))
    {
      paramRenderScript = new ScriptIntrinsicBlur(paramRenderScript.nScriptIntrinsicCreate(5, paramElement.getID(paramRenderScript)), paramRenderScript);
      paramRenderScript.setRadius(5.0F);
      return paramRenderScript;
    }
    throw new RSIllegalArgumentException("Unsuported element type.");
  }
  
  public void forEach(Allocation paramAllocation)
  {
    forEach(0, (Allocation)null, paramAllocation, null);
  }
  
  public void forEach(Allocation paramAllocation, Script.LaunchOptions paramLaunchOptions)
  {
    forEach(0, (Allocation)null, paramAllocation, null, paramLaunchOptions);
  }
  
  public Script.FieldID getFieldID_Input()
  {
    return createFieldID(1, null);
  }
  
  public Script.KernelID getKernelID()
  {
    return createKernelID(0, 2, null, null);
  }
  
  public void setInput(Allocation paramAllocation)
  {
    this.mInput = paramAllocation;
    setVar(1, paramAllocation);
  }
  
  public void setRadius(float paramFloat)
  {
    if ((paramFloat <= 0.0F) || (paramFloat > 25.0F)) {
      throw new RSIllegalArgumentException("Radius out of range (0 < r <= 25).");
    }
    setVar(0, paramFloat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ScriptIntrinsicBlur.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */