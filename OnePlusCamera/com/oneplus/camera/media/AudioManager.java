package com.oneplus.camera.media;

import com.oneplus.base.Handle;
import com.oneplus.base.component.Component;

public abstract interface AudioManager
  extends Component
{
  public static final int FLAG_1_5X_FASTER = 2;
  public static final int FLAG_LOOP = 1;
  public static final int STREAM_RING = 2;
  public static final int STREAM_SYSTEM = 1;
  
  public abstract Handle loadSound(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract Handle playSound(Handle paramHandle, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/AudioManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */