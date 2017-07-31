package com.oneplus.camera;

import com.oneplus.base.EventArgs;
import com.oneplus.base.Handle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MeteringEventArgs
  extends EventArgs
{
  private static final ArrayDeque<MeteringEventArgs> POOL = new ArrayDeque(8);
  private static final int POOL_SIZE = 8;
  private volatile Handle m_Handle;
  private volatile boolean m_IsFreeInstance;
  private volatile boolean m_IsSuccessful;
  private final List<Camera.MeteringRect> m_ReadOnlyRegions = Collections.unmodifiableList(this.m_Regions);
  private final List<Camera.MeteringRect> m_Regions = new ArrayList();
  
  public static MeteringEventArgs obtain(Handle paramHandle, List<Camera.MeteringRect> paramList, boolean paramBoolean)
  {
    try
    {
      MeteringEventArgs localMeteringEventArgs = (MeteringEventArgs)POOL.pollLast();
      if (localMeteringEventArgs != null) {
        localMeteringEventArgs.m_IsFreeInstance = false;
      }
      while (paramList != null)
      {
        int i = paramList.size() - 1;
        while (i >= 0)
        {
          localMeteringEventArgs.m_Regions.add((Camera.MeteringRect)paramList.get(i));
          i -= 1;
        }
        localMeteringEventArgs = new MeteringEventArgs();
      }
      localMeteringEventArgs.m_Handle = paramHandle;
      localMeteringEventArgs.m_IsSuccessful = paramBoolean;
      return localMeteringEventArgs;
    }
    finally {}
  }
  
  public MeteringEventArgs clone()
  {
    return obtain(this.m_Handle, this.m_Regions, this.m_IsSuccessful);
  }
  
  public final Handle getHandle()
  {
    return this.m_Handle;
  }
  
  public final List<Camera.MeteringRect> getRegions()
  {
    return this.m_ReadOnlyRegions;
  }
  
  public final boolean isSuccessful()
  {
    return this.m_IsSuccessful;
  }
  
  public final void recycle()
  {
    try
    {
      if (!this.m_IsFreeInstance)
      {
        this.m_IsFreeInstance = true;
        this.m_Regions.clear();
        this.m_Handle = null;
      }
      if (POOL.size() < 8) {
        POOL.addLast(this);
      }
      return;
    }
    finally {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/MeteringEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */