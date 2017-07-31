package com.oneplus.camera;

import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.camera.bokeh.BokehControllerBuilder;
import com.oneplus.camera.bokeh.BokehUIBuilder;
import com.oneplus.camera.capturemode.CaptureModeManagerBuilder;
import com.oneplus.camera.location.LocationManagerBuilder;
import com.oneplus.camera.manual.ManualModeControllerBuilder;
import com.oneplus.camera.manual.ManualModeUIBuilder;
import com.oneplus.camera.media.AudioManagerBuilder;
import com.oneplus.camera.panorama.PanoramaControllerBuilder;
import com.oneplus.camera.panorama.PanoramaUIBuilder;
import com.oneplus.camera.scene.SceneManagerBuilder;
import com.oneplus.camera.slowmotion.SlowMotionControllerBuilder;
import com.oneplus.camera.slowmotion.SlowMotionUIBuilder;
import com.oneplus.camera.timelapse.TimelapseControllerBuilder;
import com.oneplus.camera.timelapse.TimelapseUIBuilder;
import com.oneplus.camera.ui.BusinessCardUIBuilder;
import com.oneplus.camera.ui.CameraPreviewGridBuilder;
import com.oneplus.camera.ui.CameraSwitchAnimationBuilder;
import com.oneplus.camera.ui.CameraSwtichAnimationIconBuilder;
import com.oneplus.camera.ui.CameraWizardBuilder;
import com.oneplus.camera.ui.CaptureBarBuilder;
import com.oneplus.camera.ui.CaptureModeSwitcherBuilder;
import com.oneplus.camera.ui.CountDownTimerIndicatorBuilder;
import com.oneplus.camera.ui.DynamicShortcutsManagerBuilder;
import com.oneplus.camera.ui.FaceRendererBuilder;
import com.oneplus.camera.ui.FocusExposureIndicatorBuilder;
import com.oneplus.camera.ui.GestureDetectorImplBuilder;
import com.oneplus.camera.ui.LevelGaugeUIBuilder;
import com.oneplus.camera.ui.LongMediaProcessingUIBuilder;
import com.oneplus.camera.ui.MotionVectorPreviewRendererBuilder;
import com.oneplus.camera.ui.OnScreenHintBuilder;
import com.oneplus.camera.ui.PinchZoomingUIBuilder;
import com.oneplus.camera.ui.PreviewCoverBuilder;
import com.oneplus.camera.ui.ProcessingDialogBuilder;
import com.oneplus.camera.ui.RecordingTimerUIBuilder;
import com.oneplus.camera.ui.ReviewScreenBuilder;
import com.oneplus.camera.ui.SceneToastBuilder;
import com.oneplus.camera.ui.SecondLayerBarBuilder;
import com.oneplus.camera.ui.ShutterEffectBuilder;
import com.oneplus.camera.ui.SwitchAnimationBuilder;
import com.oneplus.camera.ui.ThumbnailBarBuilder;
import com.oneplus.camera.ui.ToastManagerBuilder;
import com.oneplus.camera.ui.TouchFocusExposureUIBuilder;
import com.oneplus.camera.ui.TutorialUIBuilder;
import com.oneplus.camera.ui.ZoomBarBuilder;
import com.oneplus.camera.watermark.OnlineWatermarkControllerBuilder;
import com.oneplus.camera.watermark.WatermarkUIBuilder;
import com.oneplus.gallery.BurstViewerBuilder;
import com.oneplus.gallery.CameraGalleryBuilder;

final class ComponentBuilders
{
  static final ComponentBuilder[] BUILDERS_CAMERA_THREAD = { new AudioManagerBuilder(), new BokehControllerBuilder(), new LocationManagerBuilder(), new ManualModeControllerBuilder(), new PanoramaControllerBuilder(), new SlowMotionControllerBuilder(), new TimelapseControllerBuilder(), new OnlineWatermarkControllerBuilder(), new ZoomControllerBuilder() };
  static final ComponentBuilder[] BUILDERS_MAIN_ACTIVITY = { new AppTrackerBuilder(), new BacklightBrightnessControllerBuilder(), new BokehUIBuilder(), new BusinessCardUIBuilder(), new BurstViewerBuilder(), new CameraGalleryBuilder(), new CameraPreviewGridBuilder(), new CameraServiceProxyBuilder(), new CameraSwitchAnimationBuilder(), new CameraSwtichAnimationIconBuilder(), new CameraWizardBuilder(), new CaptureBarBuilder(), new CaptureModeManagerBuilder(), new CaptureModeSwitcherBuilder(), new CountDownTimerBuilder(), new CountDownTimerIndicatorBuilder(), new DialogManagerBuilder(), new DynamicShortcutsManagerBuilder(), new FaceBeautyControllerBuilder(), new FaceRendererBuilder(), new FaceTrackerBuilder(), new FlashControllerBuilder(), new FocusExposureIndicatorBuilder(), new GestureDetectorImplBuilder(), new LevelGaugeUIBuilder(), new LocationManagerBuilder(), new LongMediaProcessingUIBuilder(), new ManualModeUIBuilder(), new MotionVectorPreviewRendererBuilder(), new OnScreenHintBuilder(), new PanoramaUIBuilder(), new PictureProcessServiceProxyBuilder(), new PinchZoomingUIBuilder(), new PreviewCoverBuilder(), new ProcessingDialogBuilder(), new RecordingTimerUIBuilder(), new ReviewScreenBuilder(), new SceneManagerBuilder(), new SceneToastBuilder(), new SecondLayerBarBuilder(), new SensorFocusControllerBuilder(), new ShutterEffectBuilder(), new SlowMotionUIBuilder(), new SmileCaptureControllerBuilder(), new SwitchAnimationBuilder(), new ThumbnailBarBuilder(), new TimelapseUIBuilder(), new ToastManagerBuilder(), new TouchFocusExposureUIBuilder(), new TutorialUIBuilder(), new UnprocessedPictureControllerBuilder(), new WatermarkUIBuilder(), new ZoomBarBuilder(), new ZoomControllerBuilder() };
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ComponentBuilders.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */