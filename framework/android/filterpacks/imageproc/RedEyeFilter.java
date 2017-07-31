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
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;

public class RedEyeFilter
  extends Filter
{
  private static final float DEFAULT_RED_INTENSITY = 1.3F;
  private static final float MIN_RADIUS = 10.0F;
  private static final float RADIUS_RATIO = 0.06F;
  private final Canvas mCanvas = new Canvas();
  @GenerateFieldPort(name="centers")
  private float[] mCenters;
  private int mHeight = 0;
  private final Paint mPaint = new Paint();
  private Program mProgram;
  private float mRadius;
  private Bitmap mRedEyeBitmap;
  private Frame mRedEyeFrame;
  private final String mRedEyeShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float intensity;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  if (mask.a > 0.0) {\n    float green_blue = color.g + color.b;\n    float red_intensity = color.r / green_blue;\n    if (red_intensity > intensity) {\n      color.r = 0.5 * green_blue;\n    }\n  }\n  gl_FragColor = color;\n}\n";
  private int mTarget = 0;
  @GenerateFieldPort(hasDefault=true, name="tile_size")
  private int mTileSize = 640;
  private int mWidth = 0;
  
  public RedEyeFilter(String paramString)
  {
    super(paramString);
  }
  
  private void createRedEyeFrame(FilterContext paramFilterContext)
  {
    int j = this.mWidth / 2;
    int k = this.mHeight / 2;
    Bitmap localBitmap = Bitmap.createBitmap(j, k, Bitmap.Config.ARGB_8888);
    this.mCanvas.setBitmap(localBitmap);
    this.mPaint.setColor(-1);
    this.mRadius = Math.max(10.0F, Math.min(j, k) * 0.06F);
    int i = 0;
    while (i < this.mCenters.length)
    {
      this.mCanvas.drawCircle(this.mCenters[i] * j, this.mCenters[(i + 1)] * k, this.mRadius, this.mPaint);
      i += 2;
    }
    MutableFrameFormat localMutableFrameFormat = ImageFormat.create(j, k, 3, 3);
    this.mRedEyeFrame = paramFilterContext.getFrameManager().newFrame(localMutableFrameFormat);
    this.mRedEyeFrame.setBitmap(localBitmap);
    localBitmap.recycle();
  }
  
  private void updateProgramParams()
  {
    if (this.mCenters.length % 2 == 1) {
      throw new RuntimeException("The size of center array must be even.");
    }
  }
  
  public void fieldPortValueUpdated(String paramString, FilterContext paramFilterContext)
  {
    if (this.mProgram != null) {
      updateProgramParams();
    }
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
      throw new RuntimeException("Filter RedEye does not support frames of target " + paramInt + "!");
    }
    paramFilterContext = new ShaderProgram(paramFilterContext, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float intensity;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  if (mask.a > 0.0) {\n    float green_blue = color.g + color.b;\n    float red_intensity = color.r / green_blue;\n    if (red_intensity > intensity) {\n      color.r = 0.5 * green_blue;\n    }\n  }\n  gl_FragColor = color;\n}\n");
    paramFilterContext.setMaximumTileSize(this.mTileSize);
    this.mProgram = paramFilterContext;
    this.mProgram.setHostValue("intensity", Float.valueOf(1.3F));
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
    if ((localFrameFormat.getWidth() != this.mWidth) || (localFrameFormat.getHeight() != this.mHeight))
    {
      this.mWidth = localFrameFormat.getWidth();
      this.mHeight = localFrameFormat.getHeight();
    }
    createRedEyeFrame(paramFilterContext);
    paramFilterContext = this.mRedEyeFrame;
    this.mProgram.process(new Frame[] { localFrame1, paramFilterContext }, localFrame2);
    pushOutput("image", localFrame2);
    localFrame2.release();
    this.mRedEyeFrame.release();
    this.mRedEyeFrame = null;
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3));
    addOutputBasedOnInput("image", "image");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/RedEyeFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */