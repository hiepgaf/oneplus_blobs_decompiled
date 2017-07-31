package com.oneplus.camera.media;

import android.util.Size;
import com.oneplus.base.Settings;
import com.oneplus.camera.Camera;
import java.util.List;

public abstract interface ResolutionSelector
{
  public abstract String getResolutionSettingsKey(Camera paramCamera, Settings paramSettings);
  
  public abstract void saveResolution(Camera paramCamera, Settings paramSettings, Resolution paramResolution);
  
  public abstract Size selectPreviewSize(Camera paramCamera, Settings paramSettings, Size paramSize, Resolution paramResolution);
  
  public abstract Resolution selectResolution(Camera paramCamera, Settings paramSettings, List<Resolution> paramList, Resolution paramResolution, Restriction paramRestriction);
  
  public abstract List<Resolution> selectResolutions(Camera paramCamera, Settings paramSettings, Restriction paramRestriction);
  
  public static final class Restriction
  {
    public final int maxFps;
    public final float maxMegaPixels;
    public final Size maxSize;
    
    public Restriction(float paramFloat)
    {
      this(null, paramFloat, -1);
    }
    
    public Restriction(Size paramSize)
    {
      this(paramSize, NaN.0F, -1);
    }
    
    public Restriction(Size paramSize, float paramFloat, int paramInt)
    {
      this.maxSize = paramSize;
      this.maxMegaPixels = paramFloat;
      this.maxFps = paramInt;
    }
    
    public static boolean hasRestriction(Restriction paramRestriction)
    {
      boolean bool2 = true;
      if (paramRestriction == null) {
        return false;
      }
      boolean bool1 = bool2;
      if (paramRestriction.maxSize == null)
      {
        bool1 = bool2;
        if (Float.isNaN(paramRestriction.maxMegaPixels)) {
          bool1 = false;
        }
      }
      return bool1;
    }
    
    public static boolean match(Restriction paramRestriction, Size paramSize)
    {
      if (paramSize == null) {
        return false;
      }
      if (paramRestriction == null) {
        return true;
      }
      if ((paramRestriction.maxSize != null) && ((paramSize.getWidth() > paramRestriction.maxSize.getWidth()) || (paramSize.getHeight() > paramRestriction.maxSize.getHeight()))) {
        return false;
      }
      return (Float.isNaN(paramRestriction.maxMegaPixels)) || (paramSize.getWidth() * paramSize.getHeight() / 1024.0F / 1024.0F <= paramRestriction.maxMegaPixels);
    }
    
    public static boolean match(Restriction paramRestriction, Resolution paramResolution)
    {
      if (paramResolution == null) {
        return false;
      }
      if (paramRestriction == null) {
        return true;
      }
      if ((paramRestriction.maxSize != null) && ((paramResolution.getWidth() > paramRestriction.maxSize.getWidth()) || (paramResolution.getHeight() > paramRestriction.maxSize.getHeight()))) {
        return false;
      }
      if ((!Float.isNaN(paramRestriction.maxMegaPixels)) && (paramResolution.getMegaPixels() > paramRestriction.maxMegaPixels)) {
        return false;
      }
      return (paramRestriction.maxFps < 0) || (paramResolution.getFps() <= paramRestriction.maxFps);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/media/ResolutionSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */