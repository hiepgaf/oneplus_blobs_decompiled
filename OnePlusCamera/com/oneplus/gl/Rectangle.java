package com.oneplus.gl;

import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

public class Rectangle
  extends ModelBase
{
  private static final PointF[] TEXTURE_COORDINATES_XRYD = { new PointF(0.0F, 1.0F), new PointF(0.0F, 0.0F), new PointF(1.0F, 1.0F), new PointF(1.0F, 0.0F) };
  private static final PointF[] TEXTURE_COORDINATES_XRYU = { new PointF(0.0F, 0.0F), new PointF(0.0F, 1.0F), new PointF(1.0F, 0.0F), new PointF(1.0F, 1.0F) };
  private boolean m_FlipTextureX;
  private boolean m_FlipTextureY;
  private final Point3D[] m_LineEndPoints = { new Point3D(), new Point3D(), new Point3D(), new Point3D() };
  private final Point3D[] m_Points = { new Point3D(), new Point3D(), new Point3D(), new Point3D() };
  private final PointF[] m_TexCoords = { new PointF(), new PointF(), new PointF(), new PointF() };
  private RectF m_TextureSourceRect;
  
  public Rectangle() {}
  
  public Rectangle(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    this.m_Points[0].set(paramFloat1, paramFloat4, paramFloat5);
    this.m_Points[1].set(paramFloat1, paramFloat2, paramFloat5);
    this.m_Points[2].set(paramFloat3, paramFloat4, paramFloat5);
    this.m_Points[3].set(paramFloat3, paramFloat2, paramFloat5);
  }
  
  public Rectangle(Rect paramRect)
  {
    this(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom, 0.0F);
  }
  
  public Rectangle(RectF paramRectF)
  {
    this(paramRectF.left, paramRectF.top, paramRectF.right, paramRectF.bottom, 0.0F);
  }
  
  public Rectangle(RectF paramRectF, float paramFloat)
  {
    this(paramRectF.left, paramRectF.top, paramRectF.right, paramRectF.bottom, paramFloat);
  }
  
  public void getBounds(RectF paramRectF)
  {
    paramRectF.left = this.m_Points[1].x;
    paramRectF.top = this.m_Points[1].y;
    paramRectF.right = this.m_Points[2].x;
    paramRectF.bottom = this.m_Points[2].y;
  }
  
  public float getHeight()
  {
    return Math.abs(this.m_Points[2].y - this.m_Points[1].y);
  }
  
  public float getWidth()
  {
    return Math.abs(this.m_Points[2].x - this.m_Points[1].x);
  }
  
  public float getZ()
  {
    return this.m_Points[0].z;
  }
  
  protected PointF[] onPrepareOpacityMaskTexCoords(DrawingContext paramDrawingContext)
  {
    switch (-getcom-oneplus-gl-DrawingContext$CoordinateSystemSwitchesValues()[paramDrawingContext.getCoordinateSystem().ordinal()])
    {
    default: 
      return null;
    case 2: 
      return TEXTURE_COORDINATES_XRYU;
    }
    return TEXTURE_COORDINATES_XRYD;
  }
  
  protected PointF[] onPrepareTexCoords(DrawingContext paramDrawingContext)
  {
    boolean bool3 = this.m_FlipTextureX;
    boolean bool2 = this.m_FlipTextureY;
    boolean bool1 = bool2;
    float f3;
    float f1;
    float f4;
    label51:
    float f6;
    float f5;
    label155:
    int i;
    if (paramDrawingContext.getCoordinateSystem() == DrawingContext.CoordinateSystem.X_RIGHT_Y_DOWN)
    {
      if (bool2) {
        bool1 = false;
      }
    }
    else
    {
      if (this.m_TextureSourceRect != null) {
        break label246;
      }
      f3 = 0.0F;
      f1 = 1.0F;
      f4 = 1.0F;
      f2 = 0.0F;
      f6 = f3;
      f5 = f4;
      if (bool3)
      {
        f5 = f3;
        f6 = f4;
      }
      f4 = f2;
      f3 = f1;
      if (bool1)
      {
        f3 = f2;
        f4 = f1;
      }
      if (bool3) {
        break label342;
      }
      if (bool1) {
        break label283;
      }
      this.m_TexCoords[0].set(TEXTURE_COORDINATES_XRYU[0]);
      this.m_TexCoords[1].set(TEXTURE_COORDINATES_XRYU[1]);
      this.m_TexCoords[2].set(TEXTURE_COORDINATES_XRYU[2]);
      this.m_TexCoords[3].set(TEXTURE_COORDINATES_XRYU[3]);
      i = 3;
      label158:
      if (i < 0) {
        break label495;
      }
      f1 = this.m_TexCoords[i].x;
      f2 = this.m_TexCoords[i].y;
      if (!bool3) {
        break label465;
      }
      f1 = Math.max(f5, Math.min(f6, f1));
      label202:
      if (!bool1) {
        break label480;
      }
    }
    label246:
    label283:
    label342:
    label465:
    label480:
    for (float f2 = Math.max(f3, Math.min(f4, f2));; f2 = Math.max(f4, Math.min(f3, f2)))
    {
      this.m_TexCoords[i].set(f1, f2);
      i -= 1;
      break label158;
      bool1 = true;
      break;
      f3 = this.m_TextureSourceRect.left;
      f1 = this.m_TextureSourceRect.top;
      f4 = this.m_TextureSourceRect.right;
      f2 = this.m_TextureSourceRect.bottom;
      break label51;
      this.m_TexCoords[0].set(TEXTURE_COORDINATES_XRYU[1]);
      this.m_TexCoords[1].set(TEXTURE_COORDINATES_XRYU[0]);
      this.m_TexCoords[2].set(TEXTURE_COORDINATES_XRYU[3]);
      this.m_TexCoords[3].set(TEXTURE_COORDINATES_XRYU[2]);
      break label155;
      if (!bool1)
      {
        this.m_TexCoords[0].set(TEXTURE_COORDINATES_XRYU[2]);
        this.m_TexCoords[1].set(TEXTURE_COORDINATES_XRYU[3]);
        this.m_TexCoords[2].set(TEXTURE_COORDINATES_XRYU[0]);
        this.m_TexCoords[3].set(TEXTURE_COORDINATES_XRYU[1]);
        break label155;
      }
      this.m_TexCoords[0].set(TEXTURE_COORDINATES_XRYU[3]);
      this.m_TexCoords[1].set(TEXTURE_COORDINATES_XRYU[2]);
      this.m_TexCoords[2].set(TEXTURE_COORDINATES_XRYU[1]);
      this.m_TexCoords[3].set(TEXTURE_COORDINATES_XRYU[0]);
      break label155;
      f1 = Math.max(f6, Math.min(f5, f1));
      break label202;
    }
    label495:
    return this.m_TexCoords;
  }
  
  protected Point3D[] onPrepareVertices(DrawingContext paramDrawingContext)
  {
    if (getStyle() == ModelBase.Style.LINES)
    {
      this.m_LineEndPoints[0].set(this.m_Points[1]);
      this.m_LineEndPoints[1].set(this.m_Points[3]);
      this.m_LineEndPoints[2].set(this.m_Points[2]);
      this.m_LineEndPoints[3].set(this.m_Points[0]);
      return this.m_LineEndPoints;
    }
    return this.m_Points;
  }
  
  public Rectangle resetTextureSourceRect()
  {
    throwIfNotAccessible();
    if (this.m_TextureSourceRect != null)
    {
      this.m_TextureSourceRect = null;
      invalidateTexCoord();
    }
    return this;
  }
  
  public Rectangle setBounds(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    return setBounds(paramFloat1, paramFloat2, paramFloat3, paramFloat4, this.m_Points[0].z);
  }
  
  public Rectangle setBounds(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4, float paramFloat5)
  {
    throwIfNotAccessible();
    this.m_Points[0].set(paramFloat1, paramFloat4, paramFloat5);
    this.m_Points[1].set(paramFloat1, paramFloat2, paramFloat5);
    this.m_Points[2].set(paramFloat3, paramFloat4, paramFloat5);
    this.m_Points[3].set(paramFloat3, paramFloat2, paramFloat5);
    invalidateVertices();
    return this;
  }
  
  public Rectangle setBounds(Rect paramRect)
  {
    return setBounds(paramRect.left, paramRect.top, paramRect.right, paramRect.bottom, this.m_Points[0].z);
  }
  
  public Rectangle setBounds(RectF paramRectF)
  {
    return setBounds(paramRectF.left, paramRectF.top, paramRectF.right, paramRectF.bottom, this.m_Points[0].z);
  }
  
  public Rectangle setBounds(RectF paramRectF, float paramFloat)
  {
    return setBounds(paramRectF.left, paramRectF.top, paramRectF.right, paramRectF.bottom, paramFloat);
  }
  
  public Rectangle setStyle(ModelBase.Style paramStyle)
  {
    return (Rectangle)super.setStyle(paramStyle);
  }
  
  public Rectangle setTextureFlipX(boolean paramBoolean)
  {
    throwIfNotAccessible();
    if (this.m_FlipTextureX != paramBoolean)
    {
      this.m_FlipTextureX = paramBoolean;
      invalidateTexCoord();
    }
    return this;
  }
  
  public Rectangle setTextureFlipY(boolean paramBoolean)
  {
    throwIfNotAccessible();
    if (this.m_FlipTextureY != paramBoolean)
    {
      this.m_FlipTextureY = paramBoolean;
      invalidateTexCoord();
    }
    return this;
  }
  
  public Rectangle setTextureSourceRect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    throwIfNotAccessible();
    if (this.m_TextureSourceRect == null) {
      this.m_TextureSourceRect = new RectF();
    }
    this.m_TextureSourceRect.set(paramFloat1, paramFloat2, paramFloat3, paramFloat4);
    invalidateTexCoord();
    return this;
  }
  
  public Rectangle setTextureSourceRect(RectF paramRectF)
  {
    throwIfNotAccessible();
    if (this.m_TextureSourceRect == null) {
      this.m_TextureSourceRect = new RectF();
    }
    this.m_TextureSourceRect.set(paramRectF);
    invalidateTexCoord();
    return this;
  }
  
  public Rectangle setZ(float paramFloat)
  {
    throwIfNotAccessible();
    this.m_Points[0].z = paramFloat;
    this.m_Points[1].z = paramFloat;
    this.m_Points[2].z = paramFloat;
    this.m_Points[3].z = paramFloat;
    invalidateVertices();
    return this;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/Rectangle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */