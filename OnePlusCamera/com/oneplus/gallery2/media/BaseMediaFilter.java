package com.oneplus.gallery2.media;

import com.oneplus.base.BasicThreadDependentObject;

public abstract class BaseMediaFilter
  extends BasicThreadDependentObject
  implements MediaFilter
{
  private boolean m_IsReleased;
  private final MediaFilter.FilterParams m_Params;
  
  public BaseMediaFilter(MediaFilter.FilterParams paramFilterParams)
  {
    this.m_Params = paramFilterParams;
  }
  
  public boolean filter(Media paramMedia, int paramInt)
  {
    return filter(paramMedia, this.m_Params, paramInt);
  }
  
  protected abstract boolean filter(Media paramMedia, MediaFilter.FilterParams paramFilterParams, int paramInt);
  
  public MediaFilter.FilterParams getParams()
  {
    return this.m_Params;
  }
  
  public final boolean isReleased()
  {
    return this.m_IsReleased;
  }
  
  protected void onRelease() {}
  
  public void release()
  {
    verifyAccess();
    if (!this.m_IsReleased)
    {
      onRelease();
      this.m_IsReleased = true;
      return;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/BaseMediaFilter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */