package android.filterfw.core;

public class ProgramVariable
{
  private Program mProgram;
  private String mVarName;
  
  public ProgramVariable(Program paramProgram, String paramString)
  {
    this.mProgram = paramProgram;
    this.mVarName = paramString;
  }
  
  public Program getProgram()
  {
    return this.mProgram;
  }
  
  public Object getValue()
  {
    if (this.mProgram == null) {
      throw new RuntimeException("Attempting to get program variable '" + this.mVarName + "' but the program is null!");
    }
    return this.mProgram.getHostValue(this.mVarName);
  }
  
  public String getVariableName()
  {
    return this.mVarName;
  }
  
  public void setValue(Object paramObject)
  {
    if (this.mProgram == null) {
      throw new RuntimeException("Attempting to set program variable '" + this.mVarName + "' but the program is null!");
    }
    this.mProgram.setHostValue(this.mVarName, paramObject);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/ProgramVariable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */