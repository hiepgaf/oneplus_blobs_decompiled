package android.renderscript;

public class ProgramVertexFixedFunction
  extends ProgramVertex
{
  ProgramVertexFixedFunction(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
  }
  
  public void bindConstants(Constants paramConstants)
  {
    this.mRS.validate();
    bindConstants(paramConstants.getAllocation(), 0);
  }
  
  public static class Builder
  {
    RenderScript mRS;
    String mShader;
    boolean mTextureMatrixEnable;
    
    public Builder(RenderScript paramRenderScript)
    {
      this.mRS = paramRenderScript;
    }
    
    private void buildShaderString()
    {
      this.mShader = "//rs_shader_internal\n";
      this.mShader += "varying vec4 varColor;\n";
      this.mShader += "varying vec2 varTex0;\n";
      this.mShader += "void main() {\n";
      this.mShader += "  gl_Position = UNI_MVP * ATTRIB_position;\n";
      this.mShader += "  gl_PointSize = 1.0;\n";
      this.mShader += "  varColor = ATTRIB_color;\n";
      if (this.mTextureMatrixEnable) {}
      for (this.mShader += "  varTex0 = (UNI_TexMatrix * vec4(ATTRIB_texture0, 0.0, 1.0)).xy;\n";; this.mShader += "  varTex0 = ATTRIB_texture0;\n")
      {
        this.mShader += "}\n";
        return;
      }
    }
    
    static Type getConstantInputType(RenderScript paramRenderScript)
    {
      Element.Builder localBuilder = new Element.Builder(paramRenderScript);
      localBuilder.add(Element.MATRIX4X4(paramRenderScript), "MV");
      localBuilder.add(Element.MATRIX4X4(paramRenderScript), "P");
      localBuilder.add(Element.MATRIX4X4(paramRenderScript), "TexMatrix");
      localBuilder.add(Element.MATRIX4X4(paramRenderScript), "MVP");
      paramRenderScript = new Type.Builder(paramRenderScript, localBuilder.create());
      paramRenderScript.setX(1);
      return paramRenderScript.create();
    }
    
    public ProgramVertexFixedFunction create()
    {
      buildShaderString();
      ProgramVertexFixedFunction.InternalBuilder localInternalBuilder = new ProgramVertexFixedFunction.InternalBuilder(this.mRS);
      localInternalBuilder.setShader(this.mShader);
      localInternalBuilder.addConstant(getConstantInputType(this.mRS));
      Element.Builder localBuilder = new Element.Builder(this.mRS);
      localBuilder.add(Element.F32_4(this.mRS), "position");
      localBuilder.add(Element.F32_4(this.mRS), "color");
      localBuilder.add(Element.F32_3(this.mRS), "normal");
      localBuilder.add(Element.F32_2(this.mRS), "texture0");
      localInternalBuilder.addInput(localBuilder.create());
      return localInternalBuilder.create();
    }
    
    public Builder setTextureMatrixEnable(boolean paramBoolean)
    {
      this.mTextureMatrixEnable = paramBoolean;
      return this;
    }
  }
  
  public static class Constants
  {
    static final int MODELVIEW_OFFSET = 0;
    static final int PROJECTION_OFFSET = 16;
    static final int TEXTURE_OFFSET = 32;
    Allocation mAlloc;
    private FieldPacker mIOBuffer;
    Matrix4f mModel;
    Matrix4f mProjection;
    Matrix4f mTexture;
    
    public Constants(RenderScript paramRenderScript)
    {
      Type localType = ProgramVertexFixedFunction.Builder.getConstantInputType(paramRenderScript);
      this.mAlloc = Allocation.createTyped(paramRenderScript, localType);
      this.mIOBuffer = new FieldPacker(localType.getElement().getBytesSize() * localType.getCount());
      this.mModel = new Matrix4f();
      this.mProjection = new Matrix4f();
      this.mTexture = new Matrix4f();
      setModelview(new Matrix4f());
      setProjection(new Matrix4f());
      setTexture(new Matrix4f());
    }
    
    private void addToBuffer(int paramInt, Matrix4f paramMatrix4f)
    {
      this.mIOBuffer.reset(paramInt);
      paramInt = 0;
      while (paramInt < 16)
      {
        this.mIOBuffer.addF32(paramMatrix4f.mMat[paramInt]);
        paramInt += 1;
      }
      this.mIOBuffer.reset(this.mIOBuffer.getData().length);
      this.mAlloc.setFromFieldPacker(0, this.mIOBuffer);
    }
    
    public void destroy()
    {
      this.mAlloc.destroy();
      this.mAlloc = null;
    }
    
    Allocation getAllocation()
    {
      return this.mAlloc;
    }
    
    public void setModelview(Matrix4f paramMatrix4f)
    {
      this.mModel.load(paramMatrix4f);
      addToBuffer(0, paramMatrix4f);
    }
    
    public void setProjection(Matrix4f paramMatrix4f)
    {
      this.mProjection.load(paramMatrix4f);
      addToBuffer(64, paramMatrix4f);
    }
    
    public void setTexture(Matrix4f paramMatrix4f)
    {
      this.mTexture.load(paramMatrix4f);
      addToBuffer(128, paramMatrix4f);
    }
  }
  
  static class InternalBuilder
    extends Program.BaseProgramBuilder
  {
    public InternalBuilder(RenderScript paramRenderScript)
    {
      super();
    }
    
    public InternalBuilder addInput(Element paramElement)
      throws IllegalStateException
    {
      if (this.mInputCount >= 8) {
        throw new RSIllegalArgumentException("Max input count exceeded.");
      }
      if (paramElement.isComplex()) {
        throw new RSIllegalArgumentException("Complex elements not allowed.");
      }
      Element[] arrayOfElement = this.mInputs;
      int i = this.mInputCount;
      this.mInputCount = (i + 1);
      arrayOfElement[i] = paramElement;
      return this;
    }
    
    public ProgramVertexFixedFunction create()
    {
      this.mRS.validate();
      Object localObject = new long[(this.mInputCount + this.mOutputCount + this.mConstantCount + this.mTextureCount) * 2];
      String[] arrayOfString = new String[this.mTextureCount];
      int i = 0;
      int j = 0;
      while (j < this.mInputCount)
      {
        k = i + 1;
        localObject[i] = Program.ProgramParam.INPUT.mID;
        i = k + 1;
        localObject[k] = this.mInputs[j].getID(this.mRS);
        j += 1;
      }
      j = 0;
      while (j < this.mOutputCount)
      {
        k = i + 1;
        localObject[i] = Program.ProgramParam.OUTPUT.mID;
        i = k + 1;
        localObject[k] = this.mOutputs[j].getID(this.mRS);
        j += 1;
      }
      j = 0;
      while (j < this.mConstantCount)
      {
        k = i + 1;
        localObject[i] = Program.ProgramParam.CONSTANT.mID;
        i = k + 1;
        localObject[k] = this.mConstants[j].getID(this.mRS);
        j += 1;
      }
      int k = 0;
      j = i;
      i = k;
      while (i < this.mTextureCount)
      {
        k = j + 1;
        localObject[j] = Program.ProgramParam.TEXTURE_TYPE.mID;
        j = k + 1;
        localObject[k] = this.mTextureTypes[i].mID;
        arrayOfString[i] = this.mTextureNames[i];
        i += 1;
      }
      localObject = new ProgramVertexFixedFunction(this.mRS.nProgramVertexCreate(this.mShader, arrayOfString, (long[])localObject), this.mRS);
      initProgram((Program)localObject);
      return (ProgramVertexFixedFunction)localObject;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ProgramVertexFixedFunction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */