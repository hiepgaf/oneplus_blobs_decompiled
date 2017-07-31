package com.oneplus.camera;

import com.oneplus.camera.bokeh.BokehCaptureModeBuilder;
import com.oneplus.camera.capturemode.CaptureModeBuilder;
import com.oneplus.camera.capturemode.PhotoCaptureModeBuilder;
import com.oneplus.camera.capturemode.VideoCaptureModeBuilder;
import com.oneplus.camera.manual.ManualCaptureModeBuilder;
import com.oneplus.camera.panorama.PanoramaCaptureModeBuilder;
import com.oneplus.camera.slowmotion.SlowMotionCaptureModeBuilder;
import com.oneplus.camera.timelapse.TimelapseCaptureModeBuilder;

final class CaptureModeBuilders
{
  public static final CaptureModeBuilder[] BUILDERS = { new PhotoCaptureModeBuilder(), new VideoCaptureModeBuilder(), new BokehCaptureModeBuilder(), new ManualCaptureModeBuilder(), new TimelapseCaptureModeBuilder(), new SlowMotionCaptureModeBuilder(), new PanoramaCaptureModeBuilder() };
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CaptureModeBuilders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */