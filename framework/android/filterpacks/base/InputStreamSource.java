package android.filterpacks.base;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.format.PrimitiveFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class InputStreamSource
  extends Filter
{
  @GenerateFieldPort(name="stream")
  private InputStream mInputStream;
  @GenerateFinalPort(hasDefault=true, name="format")
  private MutableFrameFormat mOutputFormat = null;
  @GenerateFinalPort(name="target")
  private String mTarget;
  
  public InputStreamSource(String paramString)
  {
    super(paramString);
  }
  
  public void process(FilterContext paramFilterContext)
  {
    int i = 0;
    try
    {
      Object localObject = new ByteArrayOutputStream();
      byte[] arrayOfByte = new byte['Ѐ'];
      for (;;)
      {
        int j = this.mInputStream.read(arrayOfByte);
        if (j <= 0) {
          break;
        }
        ((ByteArrayOutputStream)localObject).write(arrayOfByte, 0, j);
        i += j;
      }
      localObject = ByteBuffer.wrap(((ByteArrayOutputStream)localObject).toByteArray());
      this.mOutputFormat.setDimensions(i);
      paramFilterContext = paramFilterContext.getFrameManager().newFrame(this.mOutputFormat);
      paramFilterContext.setData((ByteBuffer)localObject);
      pushOutput("data", paramFilterContext);
      paramFilterContext.release();
      closeOutputPort("data");
      return;
    }
    catch (IOException paramFilterContext)
    {
      throw new RuntimeException("InputStreamSource: Could not read stream: " + paramFilterContext.getMessage() + "!");
    }
  }
  
  public void setupPorts()
  {
    int i = FrameFormat.readTargetString(this.mTarget);
    if (this.mOutputFormat == null) {
      this.mOutputFormat = PrimitiveFormat.createByteFormat(i);
    }
    addOutputPort("data", this.mOutputFormat);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/base/InputStreamSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */