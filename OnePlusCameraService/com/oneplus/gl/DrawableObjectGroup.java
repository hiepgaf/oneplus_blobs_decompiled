package com.oneplus.gl;

import android.annotation.SuppressLint;
import java.util.ArrayList;
import java.util.List;

@SuppressLint({"WrongCall"})
public class DrawableObjectGroup
  extends DrawableObject
{
  private final List<DrawableObject> m_Objects = new ArrayList();
  
  public DrawableObjectGroup() {}
  
  public DrawableObjectGroup(DrawableObject... paramVarArgs)
  {
    int i = 0;
    int j = paramVarArgs.length;
    while (i < j)
    {
      if (paramVarArgs[i] != null) {
        this.m_Objects.add(paramVarArgs[i]);
      }
      i += 1;
    }
  }
  
  public void addObject(int paramInt, DrawableObject paramDrawableObject)
  {
    verifyAccess();
    if (paramDrawableObject == null) {
      throw new IllegalArgumentException();
    }
    this.m_Objects.add(paramInt, paramDrawableObject);
  }
  
  public void addObject(DrawableObject paramDrawableObject)
  {
    addObject(this.m_Objects.size(), paramDrawableObject);
  }
  
  public DrawableObject getObject(int paramInt)
  {
    return (DrawableObject)this.m_Objects.get(paramInt);
  }
  
  public int getObjectCount()
  {
    return this.m_Objects.size();
  }
  
  public boolean hasAlphaBlending()
  {
    int i = this.m_Objects.size() - 1;
    while (i >= 0)
    {
      if (((DrawableObject)this.m_Objects.get(i)).hasAlphaBlending()) {
        return true;
      }
      i -= 1;
    }
    return false;
  }
  
  protected void onDraw(DrawingContext paramDrawingContext)
  {
    int i = 0;
    int j = this.m_Objects.size();
    while (i < j)
    {
      ((DrawableObject)this.m_Objects.get(i)).onDraw(this.drawingContext);
      i += 1;
    }
  }
  
  protected void onRelease()
  {
    this.m_Objects.clear();
    super.onRelease();
  }
  
  public void removeObject(int paramInt)
  {
    verifyAccess();
    this.m_Objects.remove(paramInt);
  }
  
  public boolean removeObject(DrawableObject paramDrawableObject)
  {
    verifyAccess();
    return this.m_Objects.remove(paramDrawableObject);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/DrawableObjectGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */