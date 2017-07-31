package com.oneplus.gallery;

import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.base.component.ComponentCreationPriority;

public abstract class GalleryAppComponentBuilder
  implements ComponentBuilder
{
  private final Class<?> m_ComponentType;
  private final ComponentCreationPriority m_Priority;
  
  protected GalleryAppComponentBuilder(ComponentCreationPriority paramComponentCreationPriority, Class<?> paramClass)
  {
    if (paramComponentCreationPriority != null)
    {
      if (paramClass != null)
      {
        this.m_Priority = paramComponentCreationPriority;
        this.m_ComponentType = paramClass;
      }
    }
    else {
      throw new IllegalArgumentException("No creation priority.");
    }
    throw new IllegalArgumentException("No component type.");
  }
  
  protected GalleryAppComponentBuilder(Class<?> paramClass)
  {
    this(ComponentCreationPriority.NORMAL, paramClass);
  }
  
  protected abstract Component create(GalleryApplication paramGalleryApplication);
  
  public Component create(Object... paramVarArgs)
  {
    if (paramVarArgs.length != 1) {}
    while (!(paramVarArgs[0] instanceof GalleryApplication)) {
      return null;
    }
    return create((GalleryApplication)paramVarArgs[0]);
  }
  
  public ComponentCreationPriority getPriority()
  {
    return this.m_Priority;
  }
  
  public boolean isComponentTypeSupported(Class<?> paramClass)
  {
    if (paramClass == null) {}
    while (!paramClass.isAssignableFrom(this.m_ComponentType)) {
      return false;
    }
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/GalleryAppComponentBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */