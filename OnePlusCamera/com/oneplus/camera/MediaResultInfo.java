package com.oneplus.camera;

import android.net.Uri;

public class MediaResultInfo
{
  public long extraDurationLimit;
  public Uri extraOutput;
  public long extraSizeLimit;
  public int extraVideoQuality;
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(super.toString()).append(" - [ ");
    localStringBuilder.append("extraDurationLimit: ").append(this.extraDurationLimit).append(", ");
    localStringBuilder.append("extraOutput: ").append(this.extraOutput).append(", ");
    localStringBuilder.append("extraSizeLimit: ").append(this.extraSizeLimit).append(", ");
    localStringBuilder.append("extraVideoQuality: ").append(this.extraVideoQuality);
    localStringBuilder.append(" ]");
    return localStringBuilder.toString();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/MediaResultInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */