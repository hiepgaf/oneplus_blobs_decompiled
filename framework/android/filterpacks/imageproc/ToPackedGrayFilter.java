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

public class ToPackedGrayFilter
  extends Filter
{
  private final String mColorToPackedGrayShader = "precision mediump float;\nconst vec4 coeff_y = vec4(0.299, 0.587, 0.114, 0);\nuniform sampler2D tex_sampler_0;\nuniform float pix_stride;\nvarying vec2 v_texcoord;\nvoid main() {\n  for (int i = 0; i < 4; ++i) {\n    vec4 p = texture2D(tex_sampler_0,\n                       v_texcoord + vec2(pix_stride * float(i), 0.0));\n    gl_FragColor[i] = dot(p, coeff_y);\n  }\n}\n";
  @GenerateFieldPort(hasDefault=true, name="keepAspectRatio")
  private boolean mKeepAspectRatio = false;
  @GenerateFieldPort(hasDefault=true, name="oheight")
  private int mOHeight = 0;
  @GenerateFieldPort(hasDefault=true, name="owidth")
  private int mOWidth = 0;
  private Program mProgram;
  
  public ToPackedGrayFilter(String paramString)
  {
    super(paramString);
  }
  
  private void checkOutputDimensions(int paramInt1, int paramInt2)
  {
    if ((paramInt1 <= 0) || (paramInt2 <= 0)) {
      throw new RuntimeException("Invalid output dimensions: " + paramInt1 + " " + paramInt2);
    }
  }
  
  private FrameFormat convertInputFormat(FrameFormat paramFrameFormat)
  {
    int j = this.mOWidth;
    int m = this.mOHeight;
    int n = paramFrameFormat.getWidth();
    int i1 = paramFrameFormat.getHeight();
    if (this.mOWidth == 0) {
      j = n;
    }
    if (this.mOHeight == 0) {
      m = i1;
    }
    int i = m;
    int k = j;
    if (this.mKeepAspectRatio)
    {
      if (n > i1)
      {
        k = Math.max(j, m);
        i = k * i1 / n;
      }
    }
    else {
      if ((k <= 0) || (k >= 4)) {
        break label121;
      }
    }
    label121:
    for (j = 4;; j = k / 4 * 4)
    {
      return ImageFormat.create(j, i, 1, 2);
      i = Math.max(j, m);
      k = i * n / i1;
      break;
    }
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return convertInputFormat(paramFrameFormat);
  }
  
  public void prepare(FilterContext paramFilterContext)
  {
    this.mProgram = new ShaderProgram(paramFilterContext, "precision mediump float;\nconst vec4 coeff_y = vec4(0.299, 0.587, 0.114, 0);\nuniform sampler2D tex_sampler_0;\nuniform float pix_stride;\nvarying vec2 v_texcoord;\nvoid main() {\n  for (int i = 0; i < 4; ++i) {\n    vec4 p = texture2D(tex_sampler_0,\n                       v_texcoord + vec2(pix_stride * float(i), 0.0));\n    gl_FragColor[i] = dot(p, coeff_y);\n  }\n}\n");
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("image");
    Object localObject = localFrame.getFormat();
    FrameFormat localFrameFormat = convertInputFormat((FrameFormat)localObject);
    int i = localFrameFormat.getWidth();
    int j = localFrameFormat.getHeight();
    checkOutputDimensions(i, j);
    this.mProgram.setHostValue("pix_stride", Float.valueOf(1.0F / i));
    localObject = ((FrameFormat)localObject).mutableCopy();
    ((MutableFrameFormat)localObject).setDimensions(i / 4, j);
    localObject = paramFilterContext.getFrameManager().newFrame((FrameFormat)localObject);
    this.mProgram.process(localFrame, (Frame)localObject);
    paramFilterContext = paramFilterContext.getFrameManager().newFrame(localFrameFormat);
    paramFilterContext.setDataFromFrame((Frame)localObject);
    ((Frame)localObject).release();
    pushOutput("image", paramFilterContext);
    paramFilterContext.release();
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3, 3));
    addOutputBasedOnInput("image", "image");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/ToPackedGrayFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */