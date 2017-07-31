package com.oneplus.camera;

import android.util.Range;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import java.util.Collections;
import java.util.List;

public abstract interface ExposureController
  extends Component
{
  public static final PropertyKey<List<Camera.MeteringRect>> PROP_AE_REGIONS = new PropertyKey("AERegions", List.class, ExposureController.class, 2, Collections.EMPTY_LIST);
  public static final PropertyKey<AutoExposureState> PROP_AE_STATE = new PropertyKey("AEState", AutoExposureState.class, ExposureController.class, AutoExposureState.INACTIVE);
  public static final PropertyKey<Float> PROP_EXPOSURE_COMPENSATION = new PropertyKey("ExposureCompensation", Float.class, ExposureController.class, 2, Float.valueOf(0.0F));
  public static final PropertyKey<Range<Float>> PROP_EXPOSURE_COMPENSATION_RANGE = new PropertyKey("ExposureCompensationRange", Range.class, ExposureController.class, new Range(Float.valueOf(0.0F), Float.valueOf(0.0F)));
  public static final PropertyKey<Float> PROP_EXPOSURE_COMPENSATION_STEP = new PropertyKey("ExposureCompensationStep", Float.class, ExposureController.class, Float.valueOf(0.0F));
  public static final PropertyKey<Boolean> PROP_IS_AE_LOCKED = new PropertyKey("IsAELocked", Boolean.class, ExposureController.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_AE_ON = new PropertyKey("IsAEOn", Boolean.class, ExposureController.class, Boolean.valueOf(false));
  
  public abstract Handle lockAutoExposure(int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ExposureController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */