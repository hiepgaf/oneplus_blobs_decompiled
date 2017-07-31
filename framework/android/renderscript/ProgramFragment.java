package android.renderscript;

public class ProgramFragment
  extends Program
{
  ProgramFragment(long paramLong, RenderScript paramRenderScript)
  {
    super(paramLong, paramRenderScript);
  }
  
  public static class Builder
    extends Program.BaseProgramBuilder
  {
    public Builder(RenderScript paramRenderScript)
    {
      super();
    }
    
    public ProgramFragment create()
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
      localObject = new ProgramFragment(this.mRS.nProgramFragmentCreate(this.mShader, arrayOfString, (long[])localObject), this.mRS);
      initProgram((Program)localObject);
      return (ProgramFragment)localObject;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/renderscript/ProgramFragment.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */