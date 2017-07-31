package com.oneplus.camera.ui;

import com.oneplus.base.HandlerBaseObject;

public class EmptyPreviewCoverProducer
  extends HandlerBaseObject
  implements PreviewCoverProducer
{
  public EmptyPreviewCoverProducer()
  {
    super(false);
  }
  
  public void hidePreviewCover(int paramInt)
  {
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.STOPPED);
  }
  
  public boolean isAlphaBlending()
  {
    return false;
  }
  
  public boolean showPreviewCover(int paramInt)
  {
    setReadOnly(PROP_STATE, PreviewCoverProducer.State.READY_TO_OUT_ANIMATION);
    return true;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/EmptyPreviewCoverProducer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */