package com.oneplus.camera.manual;

public abstract interface KnobViewChangedListener
{
  public abstract void onRotationStateChanged(KnobView paramKnobView, KnobView.RotationState paramRotationState);
  
  public abstract void onSelectedKnobItemChanged(KnobView paramKnobView, KnobItemInfo paramKnobItemInfo1, KnobItemInfo paramKnobItemInfo2);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/manual/KnobViewChangedListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */