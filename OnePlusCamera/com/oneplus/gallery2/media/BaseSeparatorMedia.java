package com.oneplus.gallery2.media;

import com.oneplus.base.BaseObjectAdapter;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;

public abstract class BaseSeparatorMedia
  extends BaseDecorationMedia
  implements SeparatorMedia
{
  private final BaseObjectAdapter m_BaseObjectAdapter = new BaseObjectAdapter(this, getClass().getSimpleName());
  
  protected BaseSeparatorMedia(MediaSource paramMediaSource)
  {
    super(paramMediaSource);
  }
  
  public <TValue> void addCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback)
  {
    this.m_BaseObjectAdapter.addCallback(paramPropertyKey, paramPropertyChangedCallback);
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    return (TValue)this.m_BaseObjectAdapter.get(paramPropertyKey);
  }
  
  public <TValue> void removeCallback(PropertyKey<TValue> paramPropertyKey, PropertyChangedCallback<TValue> paramPropertyChangedCallback)
  {
    this.m_BaseObjectAdapter.removeCallback(paramPropertyKey, paramPropertyChangedCallback);
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    return this.m_BaseObjectAdapter.set(paramPropertyKey, paramTValue);
  }
  
  protected <TValue> boolean setReadOnly(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (!paramPropertyKey.isReadOnly()) {
      return set(paramPropertyKey, paramTValue);
    }
    return this.m_BaseObjectAdapter.setReadOnly(paramPropertyKey, paramTValue);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/BaseSeparatorMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */