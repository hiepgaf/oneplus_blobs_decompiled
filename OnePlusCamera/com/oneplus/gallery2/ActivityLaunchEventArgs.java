package com.oneplus.gallery2;

import com.oneplus.base.EventArgs;

public class ActivityLaunchEventArgs
  extends EventArgs
{
  private final GalleryActivity m_Activity;
  private final ActivityLaunchType m_LaunchType;
  
  public ActivityLaunchEventArgs(GalleryActivity paramGalleryActivity, ActivityLaunchType paramActivityLaunchType)
  {
    this.m_Activity = paramGalleryActivity;
    this.m_LaunchType = paramActivityLaunchType;
  }
  
  public GalleryActivity getActivity()
  {
    return this.m_Activity;
  }
  
  public ActivityLaunchType getLaunchType()
  {
    return this.m_LaunchType;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/ActivityLaunchEventArgs.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */