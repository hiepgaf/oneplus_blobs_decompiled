package android.filterfw.core;

import java.lang.reflect.Field;

public class FinalPort
  extends FieldPort
{
  public FinalPort(Filter paramFilter, String paramString, Field paramField, boolean paramBoolean)
  {
    super(paramFilter, paramString, paramField, paramBoolean);
  }
  
  protected void setFieldFrame(Frame paramFrame, boolean paramBoolean)
  {
    try
    {
      assertPortIsOpen();
      checkFrameType(paramFrame, paramBoolean);
      if (this.mFilter.getStatus() != 0) {
        throw new RuntimeException("Attempting to modify " + this + "!");
      }
    }
    finally {}
    super.setFieldFrame(paramFrame, paramBoolean);
    super.transfer(null);
  }
  
  public String toString()
  {
    return "final " + super.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterfw/core/FinalPort.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */