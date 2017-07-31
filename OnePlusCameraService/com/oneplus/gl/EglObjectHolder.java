package com.oneplus.gl;

import java.lang.ref.WeakReference;

final class EglObjectHolder
{
  public WeakReference<EglObject> eglObject;
  public EglObjectHolder nextHolder;
  public EglObjectHolder prevHolder;
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gl/EglObjectHolder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */