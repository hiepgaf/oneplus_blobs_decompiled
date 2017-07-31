package com.oneplus.gl;

import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.Matrix;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public abstract class ModelBase
  extends DrawableObject
{
  private static final ThreadLocal<VertexShader> m_DefaultVertexShader = new ThreadLocal();
  private float m_BorderThickness = 1.0F;
  private FragmentShader m_FragmentShader;
  private boolean m_IsOpacityMaskTexCoordPrepared;
  private boolean m_IsTexCoordPrepared;
  private boolean m_IsVerticesPrepared;
  private DrawingContext.CoordinateSystem m_LastCoordinateSystem;
  private float m_Opacity = 1.0F;
  private Texture2D m_OpacityMask;
  private FloatBuffer m_OpacityMaskTexCoordBuffer;
  private int m_OpacityMaskTexCoordCount;
  private boolean m_OwnsFragmentShader;
  private Program m_Program;
  private final Point3D m_RotationCenter = new Point3D();
  private final float[] m_Rotations = new float[3];
  private Style m_Style = Style.FACES;
  private final PointF m_TempPoint = new PointF();
  private final Point3D m_TempPoint3D = new Point3D();
  private FloatBuffer m_TexCoordBuffer;
  private int m_TexCoordCount;
  private final float[] m_TransformMatrix = new float[16];
  private final float[] m_Translations = new float[3];
  private FloatBuffer m_VertexBuffer;
  private int m_VertexCount;
  private VertexShader m_VertexShader;
  
  protected ModelBase()
  {
    Matrix.setIdentityM(this.m_TransformMatrix, 0);
  }
  
  protected static final VertexShader getDefaultVertexShader()
  {
    VertexShader localVertexShader = (VertexShader)m_DefaultVertexShader.get();
    Object localObject = localVertexShader;
    if (localVertexShader == null)
    {
      localObject = new SimpleVertexShader();
      m_DefaultVertexShader.set(localObject);
    }
    return (VertexShader)localObject;
  }
  
  private ModelBase setRotation(int paramInt, float paramFloat)
  {
    if (this.m_Rotations[paramInt] == paramFloat) {
      return this;
    }
    throwIfNotAccessible();
    float f;
    if (paramFloat > 360.0F) {
      f = paramFloat % 360.0F;
    }
    for (;;)
    {
      this.m_Rotations[paramInt] = f;
      invalidateVertices();
      return this;
      f = paramFloat;
      if (paramFloat < 0.0F) {
        f = paramFloat % 360.0F;
      }
    }
  }
  
  private ModelBase setRotationBy(int paramInt, float paramFloat)
  {
    if (paramFloat == 0.0F) {
      return this;
    }
    throwIfNotAccessible();
    float f = paramFloat + this.m_Rotations[paramInt];
    if (f > 360.0F) {
      paramFloat = f % 360.0F;
    }
    for (;;)
    {
      this.m_Rotations[paramInt] = paramFloat;
      invalidateVertices();
      return this;
      paramFloat = f;
      if (f < 0.0F) {
        paramFloat = f % 360.0F;
      }
    }
  }
  
  private ModelBase setTranslation(int paramInt, float paramFloat)
  {
    throwIfNotAccessible();
    switch (paramInt)
    {
    default: 
      return this;
    case 0: 
      Matrix.translateM(this.m_TransformMatrix, 0, paramFloat - this.m_Translations[0], 0.0F, 0.0F);
    }
    for (;;)
    {
      this.m_Translations[paramInt] = paramFloat;
      return this;
      Matrix.translateM(this.m_TransformMatrix, 0, 0.0F, paramFloat - this.m_Translations[1], 0.0F);
      continue;
      Matrix.translateM(this.m_TransformMatrix, 0, 0.0F, 0.0F, paramFloat - this.m_Translations[2]);
    }
  }
  
  public float getBorderThickness()
  {
    return this.m_BorderThickness;
  }
  
  protected int getDrawArraysMode()
  {
    switch (-getcom-oneplus-gl-ModelBase$StyleSwitchesValues()[this.m_Style.ordinal()])
    {
    default: 
      return 5;
    }
    return 2;
  }
  
  public FragmentShader getFragmentShader()
  {
    return this.m_FragmentShader;
  }
  
  public float getOpacity()
  {
    return this.m_Opacity;
  }
  
  public Texture2D getOpacityMask()
  {
    return this.m_OpacityMask;
  }
  
  public FloatBuffer getOpacityMaskTexCoordBuffer(DrawingContext paramDrawingContext)
  {
    throwIfNotAccessible();
    if ((!this.m_IsOpacityMaskTexCoordPrepared) || (paramDrawingContext.getCoordinateSystem() != this.m_LastCoordinateSystem))
    {
      paramDrawingContext = onPrepareOpacityMaskTexCoords(paramDrawingContext);
      if ((paramDrawingContext == null) || (paramDrawingContext.length <= 0)) {
        break label159;
      }
      int j = paramDrawingContext.length;
      int i = j * 2;
      if ((this.m_OpacityMaskTexCoordBuffer == null) || (this.m_OpacityMaskTexCoordBuffer.capacity() < i)) {
        this.m_OpacityMaskTexCoordBuffer = ByteBuffer.allocateDirect(i * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
      }
      PointF localPointF = this.m_TempPoint;
      i = 0;
      while (i < j)
      {
        localPointF.set(paramDrawingContext[i]);
        this.m_OpacityMaskTexCoordBuffer.put(localPointF.x);
        this.m_OpacityMaskTexCoordBuffer.put(localPointF.y);
        i += 1;
      }
      this.m_OpacityMaskTexCoordBuffer.position(0);
      this.m_OpacityMaskTexCoordCount = j;
    }
    for (;;)
    {
      this.m_IsOpacityMaskTexCoordPrepared = true;
      return this.m_OpacityMaskTexCoordBuffer;
      label159:
      this.m_OpacityMaskTexCoordCount = 0;
      this.m_OpacityMaskTexCoordBuffer = null;
    }
  }
  
  public int getOpacityMaskTexCoordCount(DrawingContext paramDrawingContext)
  {
    return this.m_OpacityMaskTexCoordCount;
  }
  
  public float getRotationX()
  {
    return this.m_Rotations[0];
  }
  
  public float getRotationY()
  {
    return this.m_Rotations[1];
  }
  
  public float getRotationZ()
  {
    return this.m_Rotations[2];
  }
  
  public void getRotations(float[] paramArrayOfFloat)
  {
    paramArrayOfFloat[0] = this.m_Rotations[0];
    paramArrayOfFloat[1] = this.m_Rotations[1];
    paramArrayOfFloat[2] = this.m_Rotations[2];
  }
  
  public Style getStyle()
  {
    return this.m_Style;
  }
  
  public FloatBuffer getTexCoordBuffer(DrawingContext paramDrawingContext)
  {
    throwIfNotAccessible();
    if ((!this.m_IsTexCoordPrepared) || (paramDrawingContext.getCoordinateSystem() != this.m_LastCoordinateSystem))
    {
      paramDrawingContext = onPrepareTexCoords(paramDrawingContext);
      if ((paramDrawingContext == null) || (paramDrawingContext.length <= 0)) {
        break label159;
      }
      int j = paramDrawingContext.length;
      int i = j * 2;
      if ((this.m_TexCoordBuffer == null) || (this.m_TexCoordBuffer.capacity() < i)) {
        this.m_TexCoordBuffer = ByteBuffer.allocateDirect(i * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
      }
      PointF localPointF = this.m_TempPoint;
      i = 0;
      while (i < j)
      {
        localPointF.set(paramDrawingContext[i]);
        this.m_TexCoordBuffer.put(localPointF.x);
        this.m_TexCoordBuffer.put(localPointF.y);
        i += 1;
      }
      this.m_TexCoordBuffer.position(0);
      this.m_TexCoordCount = j;
    }
    for (;;)
    {
      this.m_IsTexCoordPrepared = true;
      return this.m_TexCoordBuffer;
      label159:
      this.m_TexCoordCount = 0;
      this.m_TexCoordBuffer = null;
    }
  }
  
  public int getTexCoordCount(DrawingContext paramDrawingContext)
  {
    return this.m_TexCoordCount;
  }
  
  public float[] getTransformMatrix()
  {
    return this.m_TransformMatrix;
  }
  
  public float getTranslationX()
  {
    return this.m_Translations[0];
  }
  
  public float getTranslationY()
  {
    return this.m_Translations[1];
  }
  
  public float getTranslationZ()
  {
    return this.m_Translations[2];
  }
  
  public void getTranslations(float[] paramArrayOfFloat)
  {
    paramArrayOfFloat[0] = this.m_Translations[0];
    paramArrayOfFloat[1] = this.m_Translations[1];
    paramArrayOfFloat[2] = this.m_Translations[2];
  }
  
  public FloatBuffer getVertexBuffer(DrawingContext paramDrawingContext)
  {
    throwIfNotAccessible();
    if ((!this.m_IsVerticesPrepared) || (paramDrawingContext.getCoordinateSystem() != this.m_LastCoordinateSystem))
    {
      paramDrawingContext = onPrepareVertices(paramDrawingContext);
      if ((paramDrawingContext == null) || (paramDrawingContext.length <= 0)) {
        break label263;
      }
      int j = paramDrawingContext.length;
      int i = j * 3;
      if ((this.m_VertexBuffer == null) || (this.m_VertexBuffer.capacity() < i)) {
        this.m_VertexBuffer = ByteBuffer.allocateDirect(i * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
      }
      this.m_VertexBuffer.position(0);
      Point3D localPoint3D1 = this.m_RotationCenter;
      Point3D localPoint3D2 = this.m_TempPoint3D;
      i = 0;
      while (i < j)
      {
        localPoint3D2.set(paramDrawingContext[i]);
        if (this.m_Rotations[0] != 0.0F) {
          rotateX(localPoint3D2, localPoint3D1, this.m_Rotations[0]);
        }
        if (this.m_Rotations[1] != 0.0F) {
          rotateY(localPoint3D2, localPoint3D1, this.m_Rotations[1]);
        }
        if (this.m_Rotations[2] != 0.0F) {
          rotateZ(localPoint3D2, localPoint3D1, this.m_Rotations[2]);
        }
        this.m_VertexBuffer.put(localPoint3D2.x);
        this.m_VertexBuffer.put(localPoint3D2.y);
        this.m_VertexBuffer.put(localPoint3D2.z);
        i += 1;
      }
      this.m_VertexCount = j;
      this.m_VertexBuffer.position(0);
    }
    for (;;)
    {
      this.m_IsVerticesPrepared = true;
      return this.m_VertexBuffer;
      label263:
      this.m_VertexCount = 0;
      this.m_VertexBuffer = null;
    }
  }
  
  public int getVertexCount(DrawingContext paramDrawingContext)
  {
    return this.m_VertexCount;
  }
  
  public boolean hasAlphaBlending()
  {
    if ((this.m_FragmentShader != null) && (this.m_FragmentShader.hasAlphaBlending())) {
      return true;
    }
    return this.m_Opacity < 1.0F;
  }
  
  protected void invalidateTexCoord()
  {
    throwIfNotAccessible();
    this.m_IsTexCoordPrepared = false;
  }
  
  protected void invalidateVertices()
  {
    throwIfNotAccessible();
    this.m_VertexCount = 0;
    this.m_IsVerticesPrepared = false;
  }
  
  protected void onDraw(DrawingContext paramDrawingContext)
  {
    VertexShader localVertexShader2 = this.m_VertexShader;
    VertexShader localVertexShader1 = localVertexShader2;
    if (localVertexShader2 == null) {
      localVertexShader1 = getDefaultVertexShader();
    }
    if ((this.m_FragmentShader == null) || (localVertexShader1 == null)) {
      return;
    }
    GLES20.glEnable(2929);
    if (this.m_Program == null)
    {
      this.m_Program = new Program();
      this.m_Program.addShader(this.m_FragmentShader, false);
      this.m_Program.addShader(localVertexShader1, false);
    }
    GLES20.glUseProgram(this.m_Program.getObjectId());
    localVertexShader1.onPrepare(paramDrawingContext, this.m_Program, this);
    this.m_FragmentShader.onPrepare(paramDrawingContext, this.m_Program, this);
    if (this.m_Style == Style.LINES) {
      GLES20.glLineWidth(this.m_BorderThickness);
    }
    GLES20.glDisable(2884);
    GLES20.glDrawArrays(getDrawArraysMode(), 0, this.m_VertexCount);
    this.m_FragmentShader.onComplete(paramDrawingContext, this.m_Program, this);
    localVertexShader1.onComplete(paramDrawingContext, this.m_Program, this);
    this.m_LastCoordinateSystem = paramDrawingContext.getCoordinateSystem();
  }
  
  protected PointF[] onPrepareOpacityMaskTexCoords(DrawingContext paramDrawingContext)
  {
    return onPrepareTexCoords(paramDrawingContext);
  }
  
  protected abstract PointF[] onPrepareTexCoords(DrawingContext paramDrawingContext);
  
  protected abstract Point3D[] onPrepareVertices(DrawingContext paramDrawingContext);
  
  protected void onRelease()
  {
    if (this.m_FragmentShader != null)
    {
      if (this.m_OwnsFragmentShader) {
        EglObject.release(this.m_FragmentShader);
      }
      this.m_FragmentShader = null;
    }
    this.m_VertexShader = null;
    this.m_Program = ((Program)EglObject.release(this.m_Program));
    super.onRelease();
  }
  
  protected final void rotateX(Point3D paramPoint3D1, Point3D paramPoint3D2, float paramFloat)
  {
    if ((paramPoint3D1 == null) || (paramFloat == 0.0F)) {
      return;
    }
    float f1;
    if (paramPoint3D2 == null)
    {
      f1 = 0.0F;
      if (paramPoint3D2 != null) {
        break label114;
      }
    }
    label114:
    for (float f2 = 0.0F;; f2 = paramPoint3D2.z)
    {
      float f3 = paramPoint3D1.y - f1;
      float f4 = paramPoint3D1.z - f2;
      double d = paramFloat / 180.0F * 3.141592653589793D;
      paramFloat = (float)Math.sin(d);
      float f5 = (float)Math.cos(d);
      paramPoint3D1.y = (f3 * f5 - f4 * paramFloat + f1);
      paramPoint3D1.z = (f3 * paramFloat + f4 * f5 + f2);
      return;
      f1 = paramPoint3D2.y;
      break;
    }
  }
  
  protected void rotateY(Point3D paramPoint3D1, Point3D paramPoint3D2, float paramFloat)
  {
    if ((paramPoint3D1 == null) || (paramFloat == 0.0F)) {
      return;
    }
    float f1;
    if (paramPoint3D2 == null)
    {
      f1 = 0.0F;
      if (paramPoint3D2 != null) {
        break label115;
      }
    }
    label115:
    for (float f2 = 0.0F;; f2 = paramPoint3D2.z)
    {
      float f3 = paramPoint3D1.x - f1;
      float f4 = paramPoint3D1.z - f2;
      double d = paramFloat / 180.0F * 3.141592653589793D;
      paramFloat = (float)Math.sin(d);
      float f5 = (float)Math.cos(d);
      paramPoint3D1.x = (f3 * f5 + f4 * paramFloat + f1);
      paramPoint3D1.z = (-f3 * paramFloat + f4 * f5 + f2);
      return;
      f1 = paramPoint3D2.x;
      break;
    }
  }
  
  protected void rotateZ(Point3D paramPoint3D1, Point3D paramPoint3D2, float paramFloat)
  {
    if ((paramPoint3D1 == null) || (paramFloat == 0.0F)) {
      return;
    }
    float f1;
    if (paramPoint3D2 == null)
    {
      f1 = 0.0F;
      if (paramPoint3D2 != null) {
        break label114;
      }
    }
    label114:
    for (float f2 = 0.0F;; f2 = paramPoint3D2.y)
    {
      float f3 = paramPoint3D1.x - f1;
      float f4 = paramPoint3D1.y - f2;
      double d = paramFloat / 180.0F * 3.141592653589793D;
      paramFloat = (float)Math.sin(d);
      float f5 = (float)Math.cos(d);
      paramPoint3D1.x = (f3 * f5 - f4 * paramFloat + f1);
      paramPoint3D1.y = (f3 * paramFloat + f4 * f5 + f2);
      return;
      f1 = paramPoint3D2.x;
      break;
    }
  }
  
  public ModelBase setBorderThickness(float paramFloat)
  {
    throwIfNotAccessible();
    if (paramFloat >= 0.0F) {}
    for (;;)
    {
      this.m_BorderThickness = paramFloat;
      return this;
      paramFloat = 0.0F;
    }
  }
  
  public ModelBase setFragmentShader(FragmentShader paramFragmentShader)
  {
    return setFragmentShader(paramFragmentShader, false);
  }
  
  public ModelBase setFragmentShader(FragmentShader paramFragmentShader, boolean paramBoolean)
  {
    throwIfNotAccessible();
    if (this.m_FragmentShader == paramFragmentShader) {
      return this;
    }
    this.m_Program = ((Program)EglObject.release(this.m_Program));
    this.m_FragmentShader = paramFragmentShader;
    this.m_OwnsFragmentShader = paramBoolean;
    return this;
  }
  
  public ModelBase setOpacity(float paramFloat)
  {
    float f;
    if (paramFloat > 1.0F) {
      f = 1.0F;
    }
    for (;;)
    {
      throwIfNotAccessible();
      this.m_Opacity = f;
      return this;
      f = paramFloat;
      if (paramFloat < 0.0F) {
        f = 0.0F;
      }
    }
  }
  
  public ModelBase setOpacityMask(Texture2D paramTexture2D)
  {
    throwIfNotAccessible();
    this.m_OpacityMask = paramTexture2D;
    return this;
  }
  
  public ModelBase setRotation(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    throwIfNotAccessible();
    this.m_Rotations[0] = paramFloat1;
    this.m_Rotations[1] = paramFloat2;
    this.m_Rotations[2] = paramFloat3;
    invalidateVertices();
    return this;
  }
  
  public ModelBase setRotationCenter(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    throwIfNotAccessible();
    this.m_RotationCenter.set(paramFloat1, paramFloat2, paramFloat3);
    return this;
  }
  
  public ModelBase setRotationX(float paramFloat)
  {
    return setRotation(0, paramFloat);
  }
  
  public ModelBase setRotationXBy(float paramFloat)
  {
    return setRotationBy(0, paramFloat);
  }
  
  public ModelBase setRotationY(float paramFloat)
  {
    return setRotation(1, paramFloat);
  }
  
  public ModelBase setRotationYBy(float paramFloat)
  {
    return setRotationBy(1, paramFloat);
  }
  
  public ModelBase setRotationZ(float paramFloat)
  {
    return setRotation(2, paramFloat);
  }
  
  public ModelBase setRotationZBy(float paramFloat)
  {
    return setRotationBy(2, paramFloat);
  }
  
  protected ModelBase setStyle(Style paramStyle)
  {
    throwIfNotAccessible();
    if (paramStyle == null) {
      throw new IllegalArgumentException("No style specifid");
    }
    if (this.m_Style != paramStyle)
    {
      this.m_Program = ((Program)EglObject.release(this.m_Program));
      invalidateVertices();
      invalidateTexCoord();
      this.m_Style = paramStyle;
    }
    return this;
  }
  
  public ModelBase setTranslationX(float paramFloat)
  {
    throwIfNotAccessible();
    return setTranslation(0, paramFloat);
  }
  
  public ModelBase setTranslationY(float paramFloat)
  {
    throwIfNotAccessible();
    return setTranslation(1, paramFloat);
  }
  
  public ModelBase setTranslationZ(float paramFloat)
  {
    throwIfNotAccessible();
    return setTranslation(2, paramFloat);
  }
  
  public ModelBase setTranslations(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    throwIfNotAccessible();
    Matrix.translateM(this.m_TransformMatrix, 0, paramFloat1 - this.m_Translations[0], paramFloat2 - this.m_Translations[1], paramFloat3 - this.m_Translations[2]);
    this.m_Translations[0] = paramFloat1;
    this.m_Translations[1] = paramFloat2;
    this.m_Translations[2] = paramFloat3;
    return this;
  }
  
  public static enum Style
  {
    FACES,  LINES;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/ModelBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */