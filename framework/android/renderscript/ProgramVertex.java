package android.renderscript;

public class ProgramVertex
  extends Program
{
  ProgramVertex(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
  }
  
  public Element getInput(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mInputs.length)) {
      throw new IllegalArgumentException("Slot ID out of range.");
    }
    return this.mInputs[paramInt];
  }
  
  public int getInputCount()
  {
    if (this.mInputs != null) {
      return this.mInputs.length;
    }
    return 0;
  }
  
  public static class Builder
    extends Program.BaseProgramBuilder
  {
    public Builder(RenderScript paramRenderScript)
    {
      super();
    }
    
    public Builder addInput(Element paramElement)
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
    
    public ProgramVertex create()
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
      localObject = new ProgramVertex(this.mRS.nProgramVertexCreate(this.mShader, arrayOfString, (long[])localObject), this.mRS);
      initProgram((Program)localObject);
      return (ProgramVertex)localObject;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ProgramVertex.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */