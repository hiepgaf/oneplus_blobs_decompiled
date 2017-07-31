package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.Program;
import android.filterfw.format.ImageFormat;
import java.lang.reflect.Field;

public abstract class ImageCombineFilter
  extends Filter
{
  protected int mCurrentTarget = 0;
  protected String[] mInputNames;
  protected String mOutputName;
  protected String mParameterName;
  protected Program mProgram;
  
  public ImageCombineFilter(String paramString1, String[] paramArrayOfString, String paramString2, String paramString3)
  {
    super(paramString1);
    this.mInputNames = paramArrayOfString;
    this.mOutputName = paramString2;
    this.mParameterName = paramString3;
  }
  
  private void assertAllInputTargetsMatch()
  {
    int i = 0;
    int j = getInputFormat(this.mInputNames[0]).getTarget();
    String[] arrayOfString = this.mInputNames;
    int k = arrayOfString.length;
    while (i < k)
    {
      if (j != getInputFormat(arrayOfString[i]).getTarget()) {
        throw new RuntimeException("Type mismatch of input formats in filter " + this + ". All input frames must have the same target!");
      }
      i += 1;
    }
  }
  
  protected abstract Program getNativeProgram(FilterContext paramFilterContext);
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return paramFrameFormat;
  }
  
  protected abstract Program getShaderProgram(FilterContext paramFilterContext);
  
  public void process(FilterContext paramFilterContext)
  {
    Frame[] arrayOfFrame = new Frame[this.mInputNames.length];
    Object localObject = this.mInputNames;
    int k = localObject.length;
    int j = 0;
    int i = 0;
    while (j < k)
    {
      arrayOfFrame[i] = pullInput(localObject[j]);
      j += 1;
      i += 1;
    }
    localObject = paramFilterContext.getFrameManager().newFrame(arrayOfFrame[0].getFormat());
    updateProgramWithTarget(arrayOfFrame[0].getFormat().getTarget(), paramFilterContext);
    this.mProgram.process(arrayOfFrame, (Frame)localObject);
    pushOutput(this.mOutputName, (Frame)localObject);
    ((Frame)localObject).release();
  }
  
  public void setupPorts()
  {
    if (this.mParameterName != null) {}
    try
    {
      Object localObject = ImageCombineFilter.class.getDeclaredField("mProgram");
      addProgramPort(this.mParameterName, this.mParameterName, (Field)localObject, Float.TYPE, false);
      localObject = this.mInputNames;
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        addMaskedInputPort(localObject[i], ImageFormat.create(3));
        i += 1;
      }
      addOutputBasedOnInput(this.mOutputName, this.mInputNames[0]);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      throw new RuntimeException("Internal Error: mProgram field not found!");
    }
  }
  
  protected void updateProgramWithTarget(int paramInt, FilterContext paramFilterContext)
  {
    if (paramInt != this.mCurrentTarget)
    {
      switch (paramInt)
      {
      default: 
        this.mProgram = null;
      }
      while (this.mProgram == null)
      {
        throw new RuntimeException("Could not create a program for image filter " + this + "!");
        this.mProgram = getNativeProgram(paramFilterContext);
        continue;
        this.mProgram = getShaderProgram(paramFilterContext);
      }
      initProgramInputs(this.mProgram, paramFilterContext);
      this.mCurrentTarget = paramInt;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/ImageCombineFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */