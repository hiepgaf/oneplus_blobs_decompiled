package com.oneplus.gl;

import android.graphics.PointF;

public class Model
  extends ModelBase
{
  private static final float COORDINATE_THRESHOLD = 1.0E-5F;
  private final PointF[] m_TextureCoords;
  private final Point3D[] m_Vertices;
  
  public Model(int paramInt)
  {
    if (paramInt < 3) {
      throw new IllegalArgumentException("Invalid vertex count : " + paramInt);
    }
    this.m_Vertices = new Point3D[paramInt];
    this.m_TextureCoords = new PointF[paramInt];
    paramInt -= 1;
    while (paramInt >= 0)
    {
      this.m_Vertices[paramInt] = new Point3D();
      this.m_TextureCoords[paramInt] = new PointF();
      paramInt -= 1;
    }
  }
  
  public Model(Point3D[] paramArrayOfPoint3D)
  {
    if (paramArrayOfPoint3D.length < 3) {
      throw new IllegalArgumentException("Invalid vertex count : " + paramArrayOfPoint3D.length);
    }
    this.m_Vertices = new Point3D[paramArrayOfPoint3D.length];
    this.m_TextureCoords = new PointF[paramArrayOfPoint3D.length];
    int i = paramArrayOfPoint3D.length - 1;
    if (i >= 0)
    {
      Point3D localPoint3D = paramArrayOfPoint3D[i];
      if (localPoint3D != null) {
        this.m_Vertices[i] = new Point3D(localPoint3D);
      }
      for (;;)
      {
        this.m_TextureCoords[i] = new PointF();
        i -= 1;
        break;
        this.m_Vertices[i] = new Point3D();
      }
    }
  }
  
  public boolean getVertices(Point3D[] paramArrayOfPoint3D)
  {
    throwIfNotAccessible();
    if ((paramArrayOfPoint3D == null) || (paramArrayOfPoint3D.length != this.m_Vertices.length)) {
      return false;
    }
    int i = this.m_Vertices.length - 1;
    while (i >= 0)
    {
      Point3D localPoint3D = this.m_Vertices[i];
      if (paramArrayOfPoint3D[i] == null) {
        paramArrayOfPoint3D[i] = new Point3D();
      }
      paramArrayOfPoint3D[i].set(localPoint3D);
      i -= 1;
    }
    return true;
  }
  
  protected PointF[] onPrepareTexCoords(DrawingContext paramDrawingContext)
  {
    return this.m_TextureCoords;
  }
  
  protected Point3D[] onPrepareVertices(DrawingContext paramDrawingContext)
  {
    return this.m_Vertices;
  }
  
  public Model setTextureCoordinate(int paramInt, float paramFloat1, float paramFloat2)
  {
    throwIfNotAccessible();
    PointF localPointF = this.m_TextureCoords[paramInt];
    if ((Math.abs(localPointF.x - paramFloat1) > 1.0E-5F) || (Math.abs(localPointF.y - paramFloat2) > 1.0E-5F))
    {
      localPointF.set(paramFloat1, paramFloat2);
      invalidateTexCoord();
    }
    return this;
  }
  
  public Model setTextureCoordinate(int paramInt, PointF paramPointF)
  {
    return setTextureCoordinate(paramInt, paramPointF.x, paramPointF.y);
  }
  
  public Model setTextureCoordinates(PointF[] paramArrayOfPointF)
  {
    return setTextureCoordinates(paramArrayOfPointF, 0);
  }
  
  public Model setTextureCoordinates(PointF[] paramArrayOfPointF, int paramInt)
  {
    throwIfNotAccessible();
    int j = 0;
    int i = this.m_TextureCoords.length + paramInt - 1;
    while (i >= paramInt)
    {
      PointF localPointF = this.m_TextureCoords[(i - paramInt)];
      if (!localPointF.equals(paramArrayOfPointF[i]))
      {
        localPointF.set(paramArrayOfPointF[i]);
        j = 1;
      }
      i -= 1;
    }
    if (j != 0) {
      invalidateTexCoord();
    }
    return this;
  }
  
  public Model setVertex(int paramInt, float paramFloat1, float paramFloat2, float paramFloat3)
  {
    throwIfNotAccessible();
    Point3D localPoint3D = this.m_Vertices[paramInt];
    if ((Math.abs(localPoint3D.x - paramFloat1) > 1.0E-5F) || (Math.abs(localPoint3D.y - paramFloat2) > 1.0E-5F)) {}
    for (;;)
    {
      localPoint3D.set(paramFloat1, paramFloat2, paramFloat3);
      invalidateVertices();
      do
      {
        return this;
      } while (Math.abs(localPoint3D.z - paramFloat3) <= 1.0E-5F);
    }
  }
  
  public Model setVertex(int paramInt, Point3D paramPoint3D)
  {
    return setVertex(paramInt, paramPoint3D.x, paramPoint3D.y, paramPoint3D.z);
  }
  
  public Model setVertices(Point3D[] paramArrayOfPoint3D)
  {
    return setVertices(paramArrayOfPoint3D, 0);
  }
  
  public Model setVertices(Point3D[] paramArrayOfPoint3D, int paramInt)
  {
    throwIfNotAccessible();
    int j = 0;
    int i = this.m_Vertices.length + paramInt - 1;
    while (i >= paramInt)
    {
      Point3D localPoint3D = this.m_Vertices[(i - paramInt)];
      if (!localPoint3D.equals(paramArrayOfPoint3D[i]))
      {
        localPoint3D.set(paramArrayOfPoint3D[i]);
        j = 1;
      }
      i -= 1;
    }
    if (j != 0) {
      invalidateVertices();
    }
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/Model.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */