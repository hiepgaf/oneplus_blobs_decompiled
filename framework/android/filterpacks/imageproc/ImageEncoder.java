package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.format.ImageFormat;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import java.io.OutputStream;

public class ImageEncoder
  extends Filter
{
  @GenerateFieldPort(name="stream")
  private OutputStream mOutputStream;
  @GenerateFieldPort(hasDefault=true, name="quality")
  private int mQuality = 80;
  
  public ImageEncoder(String paramString)
  {
    super(paramString);
  }
  
  public void process(FilterContext paramFilterContext)
  {
    pullInput("image").getBitmap().compress(Bitmap.CompressFormat.JPEG, this.mQuality, this.mOutputStream);
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3, 0));
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/ImageEncoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */