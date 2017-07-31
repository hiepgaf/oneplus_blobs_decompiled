package android.renderscript;

public abstract class ScriptIntrinsic
  extends Script
{
  ScriptIntrinsic(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
    if (paramLong == 0L) {
      throw new RSRuntimeException("Loading of ScriptIntrinsic failed.");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ScriptIntrinsic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */