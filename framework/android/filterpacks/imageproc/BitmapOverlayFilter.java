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
import android.graphics.Bitmap;

public class BitmapOverlayFilter
  extends Filter
{
  @GenerateFieldPort(name="bitmap")
  private Bitmap mBitmap;
  private Frame mFrame;
  private final String mOverlayShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 original = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  gl_FragColor = vec4(original.rgb * (1.0 - mask.a) + mask.rgb, 1.0);\n}\n";
  private Program mProgram;
  private int mTarget = 0;
  @GenerateFieldPort(hasDefault=true, name="tile_size")
  private int mTileSize = 640;
  
  public BitmapOverlayFilter(String paramString)
  {
    super(paramString);
  }
  
  private Frame createBitmapFrame(FilterContext paramFilterContext)
  {
    MutableFrameFormat localMutableFrameFormat = ImageFormat.create(this.mBitmap.getWidth(), this.mBitmap.getHeight(), 3, 3);
    paramFilterContext = paramFilterContext.getFrameManager().newFrame(localMutableFrameFormat);
    paramFilterContext.setBitmap(this.mBitmap);
    this.mBitmap.recycle();
    this.mBitmap = null;
    return paramFilterContext;
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return paramFrameFormat;
  }
  
  public void initProgram(FilterContext paramFilterContext, int paramInt)
  {
    switch (paramInt)
    {
    default: 
      throw new RuntimeException("Filter FisheyeFilter does not support frames of target " + paramInt + "!");
    }
    paramFilterContext = new ShaderProgram(paramFilterContext, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 original = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  gl_FragColor = vec4(original.rgb * (1.0 - mask.a) + mask.rgb, 1.0);\n}\n");
    paramFilterContext.setMaximumTileSize(this.mTileSize);
    this.mProgram = paramFilterContext;
    this.mTarget = paramInt;
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame1 = pullInput("image");
    FrameFormat localFrameFormat = localFrame1.getFormat();
    Frame localFrame2 = paramFilterContext.getFrameManager().newFrame(localFrameFormat);
    if ((this.mProgram == null) || (localFrameFormat.getTarget() != this.mTarget)) {
      initProgram(paramFilterContext, localFrameFormat.getTarget());
    }
    if (this.mBitmap != null)
    {
      paramFilterContext = createBitmapFrame(paramFilterContext);
      this.mProgram.process(new Frame[] { localFrame1, paramFilterContext }, localFrame2);
      paramFilterContext.release();
    }
    for (;;)
    {
      pushOutput("image", localFrame2);
      localFrame2.release();
      return;
      localFrame2.setDataFromFrame(localFrame1);
    }
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3));
    addOutputBasedOnInput("image", "image");
  }
  
  public void tearDown(FilterContext paramFilterContext)
  {
    if (this.mFrame != null)
    {
      this.mFrame.release();
      this.mFrame = null;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/BitmapOverlayFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */