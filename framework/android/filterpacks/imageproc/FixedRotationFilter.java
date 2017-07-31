package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.filterfw.geometry.Point;
import android.filterfw.geometry.Quad;

public class FixedRotationFilter
  extends Filter
{
  private ShaderProgram mProgram = null;
  @GenerateFieldPort(hasDefault=true, name="rotation")
  private int mRotation = 0;
  
  public FixedRotationFilter(String paramString)
  {
    super(paramString);
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return paramFrameFormat;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("image");
    if (this.mRotation == 0)
    {
      pushOutput("image", localFrame);
      return;
    }
    Object localObject = localFrame.getFormat();
    if (this.mProgram == null) {
      this.mProgram = ShaderProgram.createIdentity(paramFilterContext);
    }
    MutableFrameFormat localMutableFrameFormat = ((FrameFormat)localObject).mutableCopy();
    int i = ((FrameFormat)localObject).getWidth();
    int j = ((FrameFormat)localObject).getHeight();
    localObject = new Point(0.0F, 0.0F);
    Point localPoint1 = new Point(1.0F, 0.0F);
    Point localPoint2 = new Point(0.0F, 1.0F);
    Point localPoint3 = new Point(1.0F, 1.0F);
    switch (Math.round(this.mRotation / 90.0F) % 4)
    {
    default: 
      localObject = new Quad((Point)localObject, localPoint1, localPoint2, localPoint3);
    }
    for (;;)
    {
      paramFilterContext = paramFilterContext.getFrameManager().newFrame(localMutableFrameFormat);
      this.mProgram.setSourceRegion((Quad)localObject);
      this.mProgram.process(localFrame, paramFilterContext);
      pushOutput("image", paramFilterContext);
      paramFilterContext.release();
      return;
      localObject = new Quad(localPoint2, (Point)localObject, localPoint3, localPoint1);
      localMutableFrameFormat.setDimensions(j, i);
      continue;
      localObject = new Quad(localPoint3, localPoint2, localPoint1, (Point)localObject);
      continue;
      localObject = new Quad(localPoint1, localPoint3, (Point)localObject, localPoint2);
      localMutableFrameFormat.setDimensions(j, i);
    }
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3, 3));
    addOutputBasedOnInput("image", "image");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/FixedRotationFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */