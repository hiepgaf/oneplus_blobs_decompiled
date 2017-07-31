package android.hardware.camera2.utils;

import android.hardware.camera2.legacy.LegacyCameraDevice;
import android.hardware.camera2.legacy.LegacyExceptionUtils.BufferQueueAbandonedException;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SurfaceUtils
{
  public static void checkConstrainedHighSpeedSurfaces(Collection<Surface> paramCollection, Range<Integer> paramRange, StreamConfigurationMap paramStreamConfigurationMap)
  {
    if ((paramCollection == null) || (paramCollection.size() == 0)) {}
    while (paramCollection.size() > 2) {
      throw new IllegalArgumentException("Output target surface list must not be null and the size must be 1 or 2");
    }
    if (paramRange == null)
    {
      paramRange = Arrays.asList(paramStreamConfigurationMap.getHighSpeedVideoSizes());
      paramStreamConfigurationMap = paramCollection.iterator();
    }
    for (;;)
    {
      if (paramStreamConfigurationMap.hasNext())
      {
        Object localObject = (Surface)paramStreamConfigurationMap.next();
        checkHighSpeedSurfaceFormat((Surface)localObject);
        Size localSize = getSurfaceSize((Surface)localObject);
        if (!paramRange.contains(localSize))
        {
          throw new IllegalArgumentException("Surface size " + localSize.toString() + " is" + " not part of the high speed supported size list " + Arrays.toString(paramRange.toArray()));
          localObject = paramStreamConfigurationMap.getHighSpeedVideoFpsRanges();
          if (!Arrays.asList((Object[])localObject).contains(paramRange)) {
            throw new IllegalArgumentException("Fps range " + paramRange.toString() + " in the" + " request is not a supported high speed fps range " + Arrays.toString((Object[])localObject));
          }
          paramRange = Arrays.asList(paramStreamConfigurationMap.getHighSpeedVideoSizesFor(paramRange));
          break;
        }
        if ((isSurfaceForPreview((Surface)localObject)) || (isSurfaceForHwVideoEncoder((Surface)localObject)))
        {
          if ((isSurfaceForPreview((Surface)localObject)) && (isSurfaceForHwVideoEncoder((Surface)localObject))) {
            throw new IllegalArgumentException("This output surface can not be both preview and hardware video encoding surface");
          }
        }
        else {
          throw new IllegalArgumentException("This output surface is neither preview nor hardware video encoding surface");
        }
      }
    }
    if (paramCollection.size() == 2)
    {
      paramCollection = paramCollection.iterator();
      if (isSurfaceForPreview((Surface)paramCollection.next()) == isSurfaceForPreview((Surface)paramCollection.next())) {
        throw new IllegalArgumentException("The 2 output surfaces must have different type");
      }
    }
  }
  
  private static void checkHighSpeedSurfaceFormat(Surface paramSurface)
  {
    int j = getSurfaceFormat(paramSurface);
    int i = j;
    if (j >= 1)
    {
      i = j;
      if (j <= 5) {
        i = 34;
      }
    }
    if (i != 34) {
      throw new IllegalArgumentException("Surface format(" + i + ") is not" + " for preview or hardware video encoding!");
    }
  }
  
  public static int getSurfaceDataspace(Surface paramSurface)
  {
    try
    {
      int i = LegacyCameraDevice.detectSurfaceDataspace(paramSurface);
      return i;
    }
    catch (LegacyExceptionUtils.BufferQueueAbandonedException paramSurface)
    {
      throw new IllegalArgumentException("Surface was abandoned", paramSurface);
    }
  }
  
  public static int getSurfaceFormat(Surface paramSurface)
  {
    try
    {
      int i = LegacyCameraDevice.detectSurfaceType(paramSurface);
      return i;
    }
    catch (LegacyExceptionUtils.BufferQueueAbandonedException paramSurface)
    {
      throw new IllegalArgumentException("Surface was abandoned", paramSurface);
    }
  }
  
  public static Size getSurfaceSize(Surface paramSurface)
  {
    try
    {
      paramSurface = LegacyCameraDevice.getSurfaceSize(paramSurface);
      return paramSurface;
    }
    catch (LegacyExceptionUtils.BufferQueueAbandonedException paramSurface)
    {
      throw new IllegalArgumentException("Surface was abandoned", paramSurface);
    }
  }
  
  public static boolean isFlexibleConsumer(Surface paramSurface)
  {
    return LegacyCameraDevice.isFlexibleConsumer(paramSurface);
  }
  
  public static boolean isSurfaceForHwVideoEncoder(Surface paramSurface)
  {
    return LegacyCameraDevice.isVideoEncoderConsumer(paramSurface);
  }
  
  public static boolean isSurfaceForPreview(Surface paramSurface)
  {
    return LegacyCameraDevice.isPreviewConsumer(paramSurface);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/utils/SurfaceUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */