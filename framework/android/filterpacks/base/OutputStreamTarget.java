package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class OutputStreamTarget
  extends Filter
{
  @GenerateFieldPort(name="stream")
  private OutputStream mOutputStream;
  
  public OutputStreamTarget(String paramString)
  {
    super(paramString);
  }
  
  public void process(FilterContext paramFilterContext)
  {
    paramFilterContext = pullInput("data");
    if (paramFilterContext.getFormat().getObjectClass() == String.class) {}
    for (paramFilterContext = ByteBuffer.wrap(((String)paramFilterContext.getObjectValue()).getBytes());; paramFilterContext = paramFilterContext.getData()) {
      try
      {
        this.mOutputStream.write(paramFilterContext.array(), 0, paramFilterContext.limit());
        this.mOutputStream.flush();
        return;
      }
      catch (IOException paramFilterContext)
      {
        throw new RuntimeException("OutputStreamTarget: Could not write to stream: " + paramFilterContext.getMessage() + "!");
      }
    }
  }
  
  public void setupPorts()
  {
    addInputPort("data");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/base/OutputStreamTarget.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */