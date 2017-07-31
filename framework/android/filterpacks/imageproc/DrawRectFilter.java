package android.filterpacks.imageproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.FrameManager;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.filterfw.format.ObjectFormat;
import android.filterfw.geometry.Point;
import android.filterfw.geometry.Quad;
import android.opengl.GLES20;

public class DrawRectFilter
  extends Filter
{
  @GenerateFieldPort(hasDefault=true, name="colorBlue")
  private float mColorBlue = 0.0F;
  @GenerateFieldPort(hasDefault=true, name="colorGreen")
  private float mColorGreen = 0.8F;
  @GenerateFieldPort(hasDefault=true, name="colorRed")
  private float mColorRed = 0.8F;
  private final String mFixedColorFragmentShader = "precision mediump float;\nuniform vec4 color;\nvoid main() {\n  gl_FragColor = color;\n}\n";
  private ShaderProgram mProgram;
  private final String mVertexShader = "attribute vec4 aPosition;\nvoid main() {\n  gl_Position = aPosition;\n}\n";
  
  public DrawRectFilter(String paramString)
  {
    super(paramString);
  }
  
  private void renderBox(Quad paramQuad)
  {
    float f1 = this.mColorRed;
    float f2 = this.mColorGreen;
    float f3 = this.mColorBlue;
    float f4 = paramQuad.p0.x;
    float f5 = paramQuad.p0.y;
    float f6 = paramQuad.p1.x;
    float f7 = paramQuad.p1.y;
    float f8 = paramQuad.p3.x;
    float f9 = paramQuad.p3.y;
    float f10 = paramQuad.p2.x;
    float f11 = paramQuad.p2.y;
    this.mProgram.setHostValue("color", new float[] { f1, f2, f3, 1.0F });
    this.mProgram.setAttributeValues("aPosition", new float[] { f4, f5, f6, f7, f8, f9, f10, f11 }, 2);
    this.mProgram.setVertexCount(4);
    this.mProgram.beginDrawing();
    GLES20.glLineWidth(1.0F);
    GLES20.glDrawArrays(2, 0, 4);
  }
  
  public FrameFormat getOutputFormat(String paramString, FrameFormat paramFrameFormat)
  {
    return paramFrameFormat;
  }
  
  public void prepare(FilterContext paramFilterContext)
  {
    this.mProgram = new ShaderProgram(paramFilterContext, "attribute vec4 aPosition;\nvoid main() {\n  gl_Position = aPosition;\n}\n", "precision mediump float;\nuniform vec4 color;\nvoid main() {\n  gl_FragColor = color;\n}\n");
  }
  
  public void process(FilterContext paramFilterContext)
  {
    Frame localFrame = pullInput("image");
    Quad localQuad = ((Quad)pullInput("box").getObjectValue()).scaled(2.0F).translated(-1.0F, -1.0F);
    paramFilterContext = (GLFrame)paramFilterContext.getFrameManager().duplicateFrame(localFrame);
    paramFilterContext.focus();
    renderBox(localQuad);
    pushOutput("image", paramFilterContext);
    paramFilterContext.release();
  }
  
  public void setupPorts()
  {
    addMaskedInputPort("image", ImageFormat.create(3, 3));
    addMaskedInputPort("box", ObjectFormat.fromClass(Quad.class, 1));
    addOutputBasedOnInput("image", "image");
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/filterpacks/imageproc/DrawRectFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */