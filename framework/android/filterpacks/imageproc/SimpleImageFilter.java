package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.Program;
import android.filterfw.format.ImageFormat;
import java.lang.reflect.Field;

public abstract class SimpleImageFilter
  extends Filter
{
  protected int mCurrentTarget = 0;
  protected String mParameterName;
  protected Program mProgram;
  
  public SimpleImageFilter(String paramString1, String paramString2)
  {
    super(paramString1);
    this.mParameterName = paramString2;
  }
  
  protected abstract Program getNativeProgram(FilterContext paramFilterContext);
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return paramFrameFormat;
  }
  
  protected abstract Program getShaderProgram(FilterContext paramFilterContext);
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame1 = pullInput("image");
    FrameFormat localFrameFormat = localFrame1.getFormat();
    Frame localFrame2 = paramFilterContext.getFrameManager().newFrame(localFrameFormat);
    updateProgramWithTarget(localFrameFormat.getTarget(), paramFilterContext);
    this.mProgram.process(localFrame1, localFrame2);
    pushOutput("image", localFrame2);
    localFrame2.release();
  }
  
  public void setupPorts()
  {
    if (this.mParameterName != null) {}
    try
    {
      Field localField = SimpleImageFilter.class.getDeclaredField("mProgram");
      addProgramPort(this.mParameterName, this.mParameterName, localField, Float.TYPE, false);
      addMaskedInputPort("image", ImageFormat.create(3));
      addOutputBasedOnInput("image", "image");
      return;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/SimpleImageFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */