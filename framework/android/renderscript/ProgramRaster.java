package android.renderscript;

public class ProgramRaster
  extends BaseObj
{
  CullMode mCullMode = CullMode.BACK;
  boolean mPointSprite = false;
  
  ProgramRaster(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
  }
  
  public static ProgramRaster CULL_BACK(RenderScript paramRenderScript)
  {
    if (paramRenderScript.mProgramRaster_CULL_BACK == null)
    {
      Builder localBuilder = new Builder(paramRenderScript);
      localBuilder.setCullMode(CullMode.BACK);
      paramRenderScript.mProgramRaster_CULL_BACK = localBuilder.create();
    }
    return paramRenderScript.mProgramRaster_CULL_BACK;
  }
  
  public static ProgramRaster CULL_FRONT(RenderScript paramRenderScript)
  {
    if (paramRenderScript.mProgramRaster_CULL_FRONT == null)
    {
      Builder localBuilder = new Builder(paramRenderScript);
      localBuilder.setCullMode(CullMode.FRONT);
      paramRenderScript.mProgramRaster_CULL_FRONT = localBuilder.create();
    }
    return paramRenderScript.mProgramRaster_CULL_FRONT;
  }
  
  public static ProgramRaster CULL_NONE(RenderScript paramRenderScript)
  {
    if (paramRenderScript.mProgramRaster_CULL_NONE == null)
    {
      Builder localBuilder = new Builder(paramRenderScript);
      localBuilder.setCullMode(CullMode.NONE);
      paramRenderScript.mProgramRaster_CULL_NONE = localBuilder.create();
    }
    return paramRenderScript.mProgramRaster_CULL_NONE;
  }
  
  public CullMode getCullMode()
  {
    return this.mCullMode;
  }
  
  public boolean isPointSpriteEnabled()
  {
    return this.mPointSprite;
  }
  
  public static class Builder
  {
    ProgramRaster.CullMode mCullMode;
    boolean mPointSprite;
    RenderScript mRS;
    
    public Builder(RenderScript paramRenderScript)
    {
      this.mRS = paramRenderScript;
      this.mPointSprite = false;
      this.mCullMode = ProgramRaster.CullMode.BACK;
    }
    
    public ProgramRaster create()
    {
      this.mRS.validate();
      ProgramRaster localProgramRaster = new ProgramRaster(this.mRS.nProgramRasterCreate(this.mPointSprite, this.mCullMode.mID), this.mRS);
      localProgramRaster.mPointSprite = this.mPointSprite;
      localProgramRaster.mCullMode = this.mCullMode;
      return localProgramRaster;
    }
    
    public Builder setCullMode(ProgramRaster.CullMode paramCullMode)
    {
      this.mCullMode = paramCullMode;
      return this;
    }
    
    public Builder setPointSpriteEnabled(boolean paramBoolean)
    {
      this.mPointSprite = paramBoolean;
      return this;
    }
  }
  
  public static enum CullMode
  {
    BACK(0),  FRONT(1),  NONE(2);
    
    int mID;
    
    private CullMode(int paramInt1)
    {
      this.mID = paramInt1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ProgramRaster.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */