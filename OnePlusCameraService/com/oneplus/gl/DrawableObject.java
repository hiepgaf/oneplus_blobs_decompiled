package com.oneplus.gl;

public abstract class DrawableObject
  extends EglObject
{
  DrawingContext drawingContext;
  DrawableObject nextDrawableObj;
  DrawableObject prevDrawableObj;
  
  public abstract boolean hasAlphaBlending();
  
  protected abstract void onDraw(DrawingContext paramDrawingContext);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/DrawableObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */