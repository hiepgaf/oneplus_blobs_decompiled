package com.oneplus.camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Size;
import android.util.SparseArray;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnSystemUiVisibilityChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Toast;
import com.oneplus.base.BaseActivity.State;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.BaseThread;
import com.oneplus.base.BaseThread.ThreadStartCallback;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.OrientationManager;
import com.oneplus.base.OrientationManager.Callback;
import com.oneplus.base.PermissionEventArgs;
import com.oneplus.base.PermissionManager;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.RecyclableObject;
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.Settings;
import com.oneplus.base.ThreadMonitor;
import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.base.component.ComponentEventArgs;
import com.oneplus.base.component.ComponentManager;
import com.oneplus.base.component.ComponentOwner;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.capturemode.CaptureModeManager;
import com.oneplus.camera.media.MediaEventArgs;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.media.Resolution;
import com.oneplus.camera.media.ResolutionManager;
import com.oneplus.camera.media.ResolutionManagerBuilder;
import com.oneplus.camera.ui.KeyEventArgs;
import com.oneplus.camera.ui.MotionEventArgs;
import com.oneplus.camera.ui.PreviewCover;
import com.oneplus.camera.ui.PreviewCover.OnStateChangedListener;
import com.oneplus.camera.ui.PreviewCover.Style;
import com.oneplus.camera.ui.PreviewCover.UIState;
import com.oneplus.camera.ui.ReviewScreen;
import com.oneplus.camera.ui.ToastManager;
import com.oneplus.camera.ui.Viewfinder;
import com.oneplus.camera.ui.ViewfinderBuilder;
import com.oneplus.io.Storage;
import com.oneplus.io.Storage.Type;
import com.oneplus.io.StorageManager;
import com.oneplus.io.StorageUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class CameraActivity
  extends BaseCameraActivity
  implements ComponentOwner
{
  private static final String CUD_USAGE_CAMERA_PREVIEW = "CameraPreviewStartStop";
  private static final String CUD_USAGE_CAMERA_PREVIEW_FRAME = "CameraPreviewFrameWaiting";
  private static final String CUD_USAGE_CAMERA_SWITCH = "CameraSwitchStartStop";
  private static final String CUD_USAGE_VIDEO_CAPTURE = "VideoCaptureStartStop";
  private static final ComponentBuilder[] DEFAULT_COMPONENT_BUILDERS = { new ExposureControllerBuilder(), new FocusControllerBuilder(), new ResolutionManagerBuilder(), new ViewfinderBuilder() };
  private static final float DEVICE_STABILITY_RANGE_ACCELE_MAX = 0.43F;
  private static final float DEVICE_STABILITY_RANGE_ACCELE_MIN = 0.27F;
  private static final float DEVICE_STABILITY_RANGE_GYRO_MAX = 0.5F;
  private static final float DEVICE_STABILITY_RANGE_GYRO_MIN = 0.02F;
  private static final int DEVICE_STABILITY_SAMPLE_COUNT = 2;
  private static final int DEVICE_STABILITY_TO_CAPTURE = 100;
  private static final long DURATION_CHECK_WINDOW_ROTATION = 100L;
  private static final long DURATION_CLOSE_ALL_CAMERAS_DELAY = 0L;
  private static final long DURATION_DISABLE_TOUCH_FOR_NAV_BAR = 300L;
  private static final long DURATION_ENABLE_UI_FOR_PREVIEW_FRAME = 100L;
  public static final long DURATION_IDLE = 120000L;
  private static final long DURATION_MAX_PENDING_CAPTURE = 800L;
  private static final long DURATION_NAV_BAR_VISIBLE = 2000L;
  private static final long DURATION_QUICK_CAPTURE_DELAY = 300L;
  private static final long DURATION_STABLE_CAPTURE_TIMEOUT = 300L;
  private static final long DURATION_UPDATE_SCREENSHOT_ROTATION = 300L;
  private static final boolean ENABLE_STABLE_FRONT_CAM_CAPTURE = true;
  private static final boolean ENABLE_SWITCH_ANIMATION = true;
  public static final EventKey<CaptureEventArgs> EVENT_BURST_PHOTO_RECEIVED;
  public static final EventKey<CaptureEventArgs> EVENT_CAPTURE_CANCELLED;
  public static final EventKey<CaptureEventArgs> EVENT_CAPTURE_COMPLETED;
  public static final EventKey<CaptureEventArgs> EVENT_CAPTURE_FAILED;
  public static final EventKey<CaptureEventArgs> EVENT_CAPTURE_STARTED;
  public static final EventKey<CaptureEventArgs> EVENT_CAPTURE_STARTING;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_FILE_SAVED;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_SAVED;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_SAVE_CANCELLED;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_SAVE_FAILED;
  public static final EventKey<CameraCaptureEventArgs> EVENT_POSTVIEW_RECEIVED;
  public static final EventKey<CaptureEventArgs> EVENT_SHUTTER;
  public static final EventKey<MotionEventArgs> EVENT_TOUCH;
  public static final EventKey<UnprocessedPictureEventArgs> EVENT_UNPROCESSED_PHOTO_RECEIVED;
  public static final String EXTRA_KEY_CAMERA_ACTIVITY_ID = "CameraActivity.InstanceId";
  public static final String EXTRA_KEY_IS_DEBUG_MODE = "CameraActivity.IsDebugMode";
  public static final int FLAG_ENABLE_WHEN_PAUSING = 1;
  private static final int FLAG_IGNORE_CAMERA_SWITCHING_CHECK = 8;
  public static final int FLAG_IGNORE_SWITCH_ANIMATION = 4;
  public static final int FLAG_NOT_TO_START_CAMERA_PREVIEW = 32;
  public static final int FLAG_NO_SHUTTER_SOUND = 2;
  public static final int FLAG_TRIGGERED_BY_HW_BUTTON = 16;
  private static final int INTENT_FILTER_PRIORITY_MAX = Integer.MAX_VALUE;
  private static final DateFormat LOG_TIME_FORMAT;
  private static final int MSG_CAMERA_PREVIEW_STARTED = -11;
  private static final int MSG_CAMERA_PREVIEW_START_FAILED = -10;
  private static final int MSG_CAMERA_THREAD_EVENT_RAISED = -1;
  private static final int MSG_CAMERA_THREAD_PROP_CHANGED = -2;
  private static final int MSG_CAPTURE_PHOTO_DELAYED = -150;
  private static final int MSG_CEHCK_WINDOW_ROTATION = -100;
  private static final int MSG_CLOSE_ALL_CAMERAS = -110;
  private static final int MSG_ENABLE_UI_FOR_PREVIEW_FRAME = -70;
  private static final int MSG_FINISH_BY_SELF = -51;
  private static final int MSG_IDLE = -50;
  private static final int MSG_PHOTO_CAPTURE_FAILED = -20;
  private static final int MSG_PHOTO_CAPTURE_STARTED = -21;
  private static final int MSG_QUICK_CAPTURE_PHOTO = -140;
  private static final int MSG_ROTATION_READY = -60;
  private static final int MSG_STABLE_CAPTURE_TIMEOUT = -130;
  private static final int MSG_UPDATE_DELAYED_ROTATION = -95;
  private static final int MSG_UPDATE_ELAPSED_RECORDING_TIME = -40;
  private static final int MSG_UPDATE_SCREENSHOT_ROTATION = -90;
  private static final int MSG_UPDATE_SYS_UI_VISIBILITY = -80;
  private static final int MSG_VIDEO_CAPTURE_FAILED = -30;
  private static final int MSG_VIDEO_CAPTURE_PAUSED = -32;
  private static final int MSG_VIDEO_CAPTURE_RESUMED = -33;
  private static final int MSG_VIDEO_CAPTURE_STARTED = -31;
  private static final PreviewCover.Style PREVIEW_COVER_STYLE;
  private static final boolean PRINT_DEVICE_STABILITY_LOGS = false;
  public static final PropertyKey<float[]> PROP_ACCELEROMETER_VALUES = new PropertyKey("AccelerometerValues", float[].class, CameraActivity.class, new float[3]);
  public static final PropertyKey<Rotation> PROP_ACTIVITY_ROTATION = new PropertyKey("ActivityRotation", Rotation.class, CameraActivity.class, Rotation.LANDSCAPE);
  public static final PropertyKey<Boolean> PROP_ALL_REQUIRED_PERMISSIONS_GRANTED = new PropertyKey("AllRequiredPermissionsGranted", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<List<Camera>> PROP_AVAILABLE_CAMERAS = new PropertyKey("AvailableCameras", List.class, CameraActivity.class, Collections.EMPTY_LIST);
  public static final PropertyKey<Integer> PROP_BATTERY_LEVEL = new PropertyKey("BatteryLevel", Integer.class, CameraActivity.class, 1, Integer.valueOf(0));
  public static final PropertyKey<Camera> PROP_CAMERA = new PropertyKey("Camera", Camera.class, CameraActivity.class, 1, null);
  public static final PropertyKey<Size> PROP_CAMERA_PREVIEW_SIZE = new PropertyKey("CameraPreviewSize", Size.class, CameraActivity.class, new Size(0, 0));
  public static final PropertyKey<OperationState> PROP_CAMERA_PREVIEW_STATE = new PropertyKey("CameraPreviewState", OperationState.class, CameraActivity.class, OperationState.STOPPED);
  public static final PropertyKey<Integer> PROP_DEVICE_ORIENTATION = new PropertyKey("DeviceOrientation", Integer.class, CameraActivity.class, Integer.valueOf(0));
  public static final PropertyKey<Integer> PROP_DEVICE_STABILITY_LEVEL = new PropertyKey("DeviceStabilityLevel", Integer.class, CameraActivity.class, Integer.valueOf(0));
  public static final PropertyKey<Long> PROP_ELAPSED_RECORDING_SECONDS = new PropertyKey("ElapsedRecordingSeconds", Long.class, CameraActivity.class, Long.valueOf(0L));
  public static final PropertyKey<Boolean> PROP_IS_BURST_PHOTO_CAPTURE_ENABLED = new PropertyKey("IsBurstPhotoCaptureEnabled", Boolean.class, CameraActivity.class, Boolean.valueOf(true));
  public static final PropertyKey<Boolean> PROP_IS_BURST_PHOTO_ON_CAPTURE = new PropertyKey("IsBurstPhotoOnCapture", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_CAMERA_LOCKED = new PropertyKey("IsCameraLocked", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_CAMERA_PREVIEW_RECEIVED = new PropertyKey("IsCameraPreviewReceived", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_CAMERA_SWITCHING = new PropertyKey("IsCameraSwitching", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_CAMERA_THREAD_STARTED = new PropertyKey("IsCameraThreadStarted", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_CAPTURE_UI_ENABLED = new PropertyKey("IsCaptureUIEnabled", Boolean.class, CameraActivity.class, Boolean.valueOf(true));
  public static final PropertyKey<Boolean> PROP_IS_CAPTURING_RAW_PHOTO = new PropertyKey("IsCapturingRawPhoto", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_DEBUG_MODE = new PropertyKey("IsDebugMode", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_FAST_SHOT_TO_SHOT_ENABLED = new PropertyKey("IsFastShotToShotEnabled", Boolean.class, CameraActivity.class, Boolean.valueOf(true));
  public static final PropertyKey<Boolean> PROP_IS_IDLE = new PropertyKey("IsIdle", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_LAUNCHING;
  public static final PropertyKey<Boolean> PROP_IS_NAVIGATION_BAR_VISIBLE = new PropertyKey("IsNavigationBarVisible", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_QUICK_CAPTURE_SCHEDULED = new PropertyKey("IsQuickCaptureScheduled", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_READY_TO_CAPTURE = new PropertyKey("IsReadyToCapture", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_ROTATION_READY = new PropertyKey("IsRotationReady", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_SCREEN_ON = new PropertyKey("IsScreenOn", Boolean.class, CameraActivity.class, Boolean.valueOf(true));
  public static final PropertyKey<Boolean> PROP_IS_SECURE_MODE = new PropertyKey("IsSecureMode", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_SELF_TIMER_STARTED = new PropertyKey("IsSelfTimerStarted", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_SELF_TIMER_USABLE = new PropertyKey("IsSelfTimerUsable", Boolean.class, CameraActivity.class, Boolean.valueOf(true));
  public static final PropertyKey<Boolean> PROP_IS_SIMPLE_UI_MODE_ENTERED = new PropertyKey("IsSimpleUIModeEntered", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_TOUCHING_ON_SCREEN = new PropertyKey("IsTouchingOnScreen", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_VIDEO_SNAPSHOT_ENABLED = new PropertyKey("IsVideoSnapshotEnabled", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_WAITING_FOR_DEVICE_STABLE = new PropertyKey("IsWaitingForDeviceStable", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_WAITING_FOR_STABLE_TO_CAPTURE = new PropertyKey("IsWaitingForStableToCapture", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_KEEP_LAST_CAPTURE_SETTINGS;
  public static final PropertyKey<Long> PROP_MAX_VIDEO_DURATION_SECONDS;
  public static final PropertyKey<MediaType> PROP_MEDIA_TYPE;
  public static final PropertyKey<PhotoCaptureState> PROP_PHOTO_CAPTURE_STATE;
  public static final PropertyKey<Long> PROP_REMAINING_PHOTO_COUNT;
  public static final PropertyKey<Long> PROP_REMAINING_VIDEO_DURATION_SECONDS;
  public static final PropertyKey<Rotation> PROP_ROTATION;
  public static final PropertyKey<ScreenSize> PROP_SCREEN_SIZE;
  public static final PropertyKey<Long> PROP_SELF_TIMER_INTERVAL;
  public static final PropertyKey<Settings> PROP_SETTINGS;
  public static final PropertyKey<CaptureCompleteReason> PROP_VIDEO_CAPTURE_COMPLETE_REASON;
  public static final PropertyKey<VideoCaptureState> PROP_VIDEO_CAPTURE_STATE;
  protected static final List<String> REQUIRED_PERMISSION_LIST;
  public static final String SETTINGS_KEY_CAMERA_LENS_FACING = "CameraLensFacing";
  private static final String SETTINGS_KEY_IS_DEBUG_MODE = "IsDebugMode";
  public static final String SETTINGS_KEY_IS_QUICK_CAPTURE_ENABLED = "IsQuickCaptureEnabled";
  public static final String SETTINGS_KEY_SELF_TIMER_INTERVAL_BACK = "SelfTimer.Back";
  private static final String SETTINGS_KEY_SELF_TIMER_INTERVAL_FRONT = "SelfTimer.Front";
  private static final String STATE_KEY_IS_QUICK_CAPTURE_TRIGGERED = "CameraActivity.IsQuickCaptureTriggered";
  private static final long TIMEOUT_CHECK_ROTATION = 3000L;
  private static final long TIMEOUT_KEEP_CAPTURE_SETTINGS = 300000L;
  private static final long TIMEOUT_ROTATION_READY = 200L;
  private static final boolean USE_GYROSCOPE_FOR_DEVICE_STABILITY_CHECK = false;
  private static long m_LatestLeaveTimeMillis;
  private AudioManager mAudioManager = null;
  private int m_AccelValueSampleIndex;
  private float[][] m_AccelValueSamples = new float[2][];
  private final SensorEventListener m_AcceleromaterListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      CameraActivity.-wrap9(CameraActivity.this, paramAnonymousSensorEvent.values);
    }
  };
  private int m_AccelerometerValuesIndex;
  private final float[][] m_AccelerometerValuesTable = { new float[3], new float[3] };
  private final SparseArray<ActivityResultHandle> m_ActivityResultHandles = new SparseArray();
  private Rotation m_ActivityRotation = Rotation.LANDSCAPE;
  private BroadcastReceiver m_BatteryReceiver;
  private LinkedList<Handle> m_BurstDisableHandles = new LinkedList();
  private LinkedList<CameraContext> m_CameraContextStack = new LinkedList();
  private final LinkedList<CameraLockHandle> m_CameraLockHandles = new LinkedList();
  private Handle m_CameraPreviewFrameCUDHandle;
  private int m_CameraPreviewSessionID;
  private Handle m_CameraPreviewStartCUDHandle;
  private OperationState m_CameraPreviewState = OperationState.STOPPED;
  private Handle m_CameraSoundHandle;
  private Handle m_CameraSwitchCUDHandle;
  private CameraThread m_CameraThread;
  private boolean m_CancelTouchEvents;
  private final LinkedList<CaptureDelayTimeHandle> m_CaptureDelayTimeHandles = new LinkedList();
  private CaptureModeManager m_CaptureModeManager;
  private final LinkedList<UIDisableHandle> m_CaptureUIDisableHandles = new LinkedList();
  private long m_CheckRotationStartTime;
  private ComponentManager m_ComponentManager;
  private CountDownTimer m_CountDownTimer;
  private boolean m_DisableDebugModeWhenExiting;
  private long m_ElapsedPartialRecordingTimeMillis;
  private ExposureController m_ExposureController;
  private final LinkedList<Handle> m_FastShotToShotDisableHandles = new LinkedList();
  private final SensorEventListener m_GyroscopeListener = new SensorEventListener()
  {
    public void onAccuracyChanged(Sensor paramAnonymousSensor, int paramAnonymousInt) {}
    
    public void onSensorChanged(SensorEvent paramAnonymousSensorEvent)
    {
      CameraActivity.-wrap12(CameraActivity.this, paramAnonymousSensorEvent.values);
    }
  };
  private boolean m_IgnoreNavigationBar;
  private final List<ComponentBuilder> m_InitialComponentBuilders = new ArrayList();
  private String m_InstanceId;
  private boolean m_IsAccelerometerStarted;
  private boolean m_IsCameraPreviewReceiverReady;
  private boolean m_IsGyroscopeStarted;
  private boolean m_IsHighComponentsCreated;
  private boolean m_IsIdle;
  private boolean m_IsQuickCaptureScheduled;
  private boolean m_IsQuickCaptureTriggered;
  private boolean m_IsRotationReady;
  private boolean m_IsSelfTimerResetNeeded;
  private Set<Integer> m_KeyDownEvents = new HashSet();
  private List<KeyEventHandle> m_KeyEventHandles = new ArrayList();
  private long m_LastElapsedRecordingTimeCheckTime;
  private long m_LastNavBarVisibleTime;
  private LaunchSource m_LaunchSource = LaunchSource.NORMAL;
  private volatile MediaResultInfo m_MediaResultInfo;
  private List<String> m_OptionalPermissions = new ArrayList();
  private OrientationManager.Callback m_OrientationCallback;
  private Handle m_OrientationCallbackHandle;
  private Handle m_OrientationSensorHandle;
  private CaptureHandleImpl m_PendingPhotoCaptureHandle;
  private Camera m_PendingSwitchCamera;
  private final EventHandler<PermissionEventArgs> m_PermissionDeniedEventHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<PermissionEventArgs> paramAnonymousEventKey, PermissionEventArgs paramAnonymousPermissionEventArgs)
    {
      CameraActivity.-wrap13(CameraActivity.this, paramAnonymousPermissionEventArgs.getPermission(), -1);
    }
  };
  private final EventHandler<PermissionEventArgs> m_PermissionGrantedEventHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<PermissionEventArgs> paramAnonymousEventKey, PermissionEventArgs paramAnonymousPermissionEventArgs)
    {
      CameraActivity.-wrap13(CameraActivity.this, paramAnonymousPermissionEventArgs.getPermission(), 0);
    }
  };
  private PermissionManager m_PermissionManager;
  private CaptureHandleImpl m_PhotoCaptureHandle;
  private Handle m_PhotoRotationLockHandle;
  private final LinkedList<Handle> m_PreCaptureFocusLockReqHandles = new LinkedList();
  private PreviewCover m_PreviewCover;
  private Handle m_PreviewCoverHandle;
  private final PreviewCover.OnStateChangedListener m_PreviewCoverStateChangedListener = new PreviewCover.OnStateChangedListener()
  {
    public void onStateChanged(PreviewCover.UIState paramAnonymousUIState1, PreviewCover.UIState paramAnonymousUIState2)
    {
      CameraActivity.-wrap14(CameraActivity.this, paramAnonymousUIState1, paramAnonymousUIState2);
    }
  };
  private Rotation m_PreviousRotation;
  private final LinkedList<RecordingTimeRatioHandle> m_RecordingTimeRatioHandles = new LinkedList();
  private ResolutionManager m_ResolutionManager;
  private Handle m_ReviewScreenHandle;
  private ReviewScreen m_ReviewScreenUI;
  private Rotation m_Rotation = Rotation.PORTRAIT;
  private long m_RotationDelay = 0L;
  private final LinkedList<RotationLockHandle> m_RotationLockHandles = new LinkedList();
  private Bitmap m_ScreenShotBitmap;
  private Matrix m_ScreenShotImageMatrix;
  private ImageView m_ScreenShotImageView;
  private BroadcastReceiver m_ScreenStateReceiver;
  private LinkedList<Handle> m_SelfTimerDisableHandles = new LinkedList();
  private Handle m_SelfTimerHandle;
  private SensorManager m_SensorManager;
  private final List<SettingsHandle> m_SettingsHandles = new ArrayList();
  private final List<Handle> m_SimpleUIModeHandles = new ArrayList();
  private StartMode m_StartMode;
  private StorageManager m_StorageManager;
  private PropertyChangedCallback<List<Storage>> m_StorageManagerCallBack;
  protected int m_StorageStopRecordToast;
  protected int m_StorageToast;
  private List<Handle> m_TakeScreenShotHandles = new ArrayList();
  private Handle m_ThreadMonitorHandle;
  private ToastManager m_ToastManager;
  private long m_TotalPausedVideoRecordingTime;
  private int m_TouchDigits;
  private Handle m_VideoCaptureCUDHandle;
  private CaptureHandleImpl m_VideoCaptureHandle;
  private long m_VideoRecordStartTime;
  private long m_VideoRecordingPausedTime;
  private Handle m_VideoRotationLockHandle;
  private Viewfinder m_Viewfinder;
  
  static
  {
    PROP_IS_LAUNCHING = new PropertyKey("IsLaunching", Boolean.class, CameraActivity.class, Boolean.valueOf(true));
    PROP_KEEP_LAST_CAPTURE_SETTINGS = new PropertyKey("KeepLastCaptureSettings", Boolean.class, CameraActivity.class, Boolean.valueOf(false));
    PROP_MAX_VIDEO_DURATION_SECONDS = new PropertyKey("MaxVideoDurationSeconds", Long.class, CameraActivity.class, Long.valueOf(-1L));
    PROP_MEDIA_TYPE = new PropertyKey("MediaType", MediaType.class, CameraActivity.class, MediaType.PHOTO);
    PROP_PHOTO_CAPTURE_STATE = new PropertyKey("PhotoCaptureState", PhotoCaptureState.class, CameraActivity.class, PhotoCaptureState.PREPARING);
    PROP_REMAINING_PHOTO_COUNT = new PropertyKey("RemainingPhotoCount", Long.class, CameraActivity.class, Long.valueOf(0L));
    PROP_REMAINING_VIDEO_DURATION_SECONDS = new PropertyKey("RemainingVideoDurationSeconds", Long.class, CameraActivity.class, Long.valueOf(0L));
    PROP_ROTATION = new PropertyKey("Rotation", Rotation.class, CameraActivity.class, Rotation.PORTRAIT);
    PROP_SCREEN_SIZE = new PropertyKey("ScreenSize", ScreenSize.class, CameraActivity.class, ScreenSize.EMPTY);
    PROP_SELF_TIMER_INTERVAL = new PropertyKey("SelfTimerInterval", Long.class, CameraActivity.class, 2, Long.valueOf(0L));
    PROP_SETTINGS = new PropertyKey("Settings", Settings.class, CameraActivity.class, 1, null);
    PROP_VIDEO_CAPTURE_COMPLETE_REASON = new PropertyKey("VideoCaptureCompleteReason", CaptureCompleteReason.class, CameraActivity.class, CaptureCompleteReason.NORMAL);
    PROP_VIDEO_CAPTURE_STATE = new PropertyKey("VideoCaptureState", VideoCaptureState.class, CameraActivity.class, VideoCaptureState.PREPARING);
    EVENT_BURST_PHOTO_RECEIVED = new EventKey("BurstPhotoReceived", CaptureEventArgs.class, CameraActivity.class);
    EVENT_CAPTURE_CANCELLED = new EventKey("CaptureCancelled", CaptureEventArgs.class, CameraActivity.class);
    EVENT_CAPTURE_COMPLETED = new EventKey("CaptureCompleted", CaptureEventArgs.class, CameraActivity.class);
    EVENT_CAPTURE_FAILED = new EventKey("CaptureFailed", CaptureEventArgs.class, CameraActivity.class);
    EVENT_CAPTURE_STARTED = new EventKey("CaptureStarted", CaptureEventArgs.class, CameraActivity.class);
    EVENT_CAPTURE_STARTING = new EventKey("CaptureStarting", CaptureEventArgs.class, CameraActivity.class);
    EVENT_MEDIA_FILE_SAVED = new EventKey("MediaFileSaved", MediaEventArgs.class, CameraActivity.class);
    EVENT_MEDIA_SAVE_CANCELLED = new EventKey("MediaSaveCancelled", MediaEventArgs.class, CameraActivity.class);
    EVENT_MEDIA_SAVE_FAILED = new EventKey("MediaSaveFailed", MediaEventArgs.class, CameraActivity.class);
    EVENT_MEDIA_SAVED = new EventKey("MediaSaved", MediaEventArgs.class, CameraActivity.class);
    EVENT_POSTVIEW_RECEIVED = new EventKey("PostviewReceived", CameraCaptureEventArgs.class, CameraActivity.class);
    EVENT_SHUTTER = new EventKey("Shutter", CaptureEventArgs.class, CameraActivity.class);
    EVENT_TOUCH = new EventKey("Touch", MotionEventArgs.class, CameraActivity.class);
    EVENT_UNPROCESSED_PHOTO_RECEIVED = new EventKey("UnprocessedPhotoReceived", UnprocessedPictureEventArgs.class, CameraActivity.class);
    REQUIRED_PERMISSION_LIST = new ArrayList();
    LOG_TIME_FORMAT = new SimpleDateFormat("MM/dd HH:mm:ss.SSS");
    PREVIEW_COVER_STYLE = PreviewCover.Style.COLOR_BLACK;
    m_LatestLeaveTimeMillis = -300000L;
    Settings.setGlobalDefaultValue("CameraLensFacing", Camera.LensFacing.BACK);
    Settings.setGlobalDefaultValue("SelfTimer.Back", Long.valueOf(0L));
    Settings.setGlobalDefaultValue("SelfTimer.Front", Long.valueOf(0L));
    REQUIRED_PERMISSION_LIST.add("android.permission.CAMERA");
    REQUIRED_PERMISSION_LIST.add("android.permission.RECORD_AUDIO");
    REQUIRED_PERMISSION_LIST.add("android.permission.READ_EXTERNAL_STORAGE");
    REQUIRED_PERMISSION_LIST.add("android.permission.WRITE_EXTERNAL_STORAGE");
  }
  
  protected CameraActivity()
  {
    this.m_CameraContextStack.add(new CameraContext(false));
  }
  
  private void bindToCameraThread(MediaType paramMediaType, List<EventKey<?>> paramList, List<PropertyKey<?>> paramList1)
  {
    Log.v(this.TAG, "bindToCameraThread()");
    if (isServiceMode()) {
      this.m_CameraThread.disableVideoSnapshot();
    }
    Object localObject1;
    int i;
    if (!paramList.isEmpty())
    {
      localObject1 = new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
        {
          HandlerUtils.sendMessage(CameraActivity.this, -1, 0, 0, new Object[] { paramAnonymousEventKey, paramAnonymousEventArgs.clone() });
        }
      };
      i = paramList.size() - 1;
      while (i >= 0)
      {
        this.m_CameraThread.addHandler((EventKey)paramList.get(i), (EventHandler)localObject1);
        i -= 1;
      }
    }
    if (!paramList1.isEmpty())
    {
      paramList = new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
        {
          paramAnonymousPropertySource = new Object[2];
          paramAnonymousPropertySource[0] = paramAnonymousPropertyKey;
          paramAnonymousPropertySource[1] = paramAnonymousPropertyChangeEventArgs.clone();
          if (paramAnonymousPropertyKey != CameraThread.PROP_AVAILABLE_CAMERAS) {
            HandlerUtils.sendMessage(CameraActivity.this, -2, 0, 0, paramAnonymousPropertySource);
          }
          do
          {
            return;
            paramAnonymousPropertyKey = CameraActivity.this.getHandler();
          } while (paramAnonymousPropertyKey == null);
          paramAnonymousPropertyKey.sendMessageAtFrontOfQueue(Message.obtain(paramAnonymousPropertyKey, -2, paramAnonymousPropertySource));
        }
      };
      i = paramList1.size() - 1;
      if (i >= 0)
      {
        Object localObject2 = (PropertyKey)paramList1.get(i);
        localObject1 = new Object[2];
        localObject1[0] = localObject2;
        localObject1[1] = PropertyChangeEventArgs.obtain(((PropertyKey)localObject2).defaultValue, this.m_CameraThread.get((PropertyKey)localObject2));
        this.m_CameraThread.addCallback((PropertyKey)localObject2, paramList);
        if (localObject2 != CameraThread.PROP_AVAILABLE_CAMERAS) {
          HandlerUtils.sendMessage(this, -2, 0, 0, localObject1);
        }
        for (;;)
        {
          i -= 1;
          break;
          localObject2 = (List)this.m_CameraThread.get(CameraThread.PROP_AVAILABLE_CAMERAS);
          if ((localObject2 == null) || (((List)localObject2).isEmpty()))
          {
            Log.w(this.TAG, "bindToCameraThread() - Empty camera list");
          }
          else
          {
            localObject2 = getHandler();
            if (localObject2 != null) {
              ((Handler)localObject2).sendMessageAtFrontOfQueue(Message.obtain((Handler)localObject2, -2, localObject1));
            }
          }
        }
      }
    }
    if (!this.m_CameraThread.setMediaType(paramMediaType)) {
      Log.e(this.TAG, "bindToCameraThread() - Fail to set initial media type to " + paramMediaType);
    }
    this.m_CameraThread.set(CameraThread.PROP_MEDIA_RESULT_INFO, this.m_MediaResultInfo);
    this.m_CameraThread.set(CameraThread.PROP_CAPTURE_ROTATION, (Rotation)get(PROP_ROTATION));
    onBindToCameraThread();
  }
  
  private boolean bindToInitialComponents()
  {
    if (getResolutionManager() == null)
    {
      Log.e(this.TAG, "bindToInitialComponents() - No ResolutionManager");
      return false;
    }
    if (get(PROP_CAMERA) != null) {
      selectCameraPreviewSize();
    }
    if (getViewfinder() == null)
    {
      Log.e(this.TAG, "bindToInitialComponents() - No Viewfinder");
      return false;
    }
    this.m_CaptureModeManager = ((CaptureModeManager)findComponent(CaptureModeManager.class));
    if (this.m_CaptureModeManager == null)
    {
      Log.e(this.TAG, "bindToInitialComponents() - No CaptureModeManager");
      return false;
    }
    return true;
  }
  
  private boolean bindToNormalComponents()
  {
    this.m_StorageManager = ((StorageManager)CameraApplication.current().findComponent(StorageManager.class));
    if (this.m_StorageManager != null)
    {
      this.m_StorageManagerCallBack = new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Storage>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Storage>> paramAnonymousPropertyChangeEventArgs)
        {
          paramAnonymousPropertySource = (Storage.Type)((Settings)CameraActivity.this.get(CameraActivity.PROP_SETTINGS)).getEnum("StorageType", Storage.Type.class, Storage.Type.INTERNAL);
          int i;
          if ((StorageUtils.findStorage(CameraActivity.-get7(CameraActivity.this), Storage.Type.SD_CARD) == null) && (paramAnonymousPropertySource == Storage.Type.SD_CARD))
          {
            i = 0;
            switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)CameraActivity.this.get(CameraActivity.PROP_VIDEO_CAPTURE_STATE)).ordinal()])
            {
            }
          }
          for (;;)
          {
            if (i == 0) {
              CameraActivity.this.showToast(CameraActivity.this.m_StorageToast);
            }
            CameraActivity.-get3(CameraActivity.this).checkRemainingMediaCount();
            ((Settings)CameraActivity.this.get(CameraActivity.PROP_SETTINGS)).set("StorageType", Storage.Type.INTERNAL);
            return;
            CameraActivity.-wrap23(CameraActivity.this, CameraActivity.-get8(CameraActivity.this), false, false);
            CameraActivity.this.showToast(CameraActivity.this.m_StorageStopRecordToast);
            i = 1;
          }
        }
      };
      this.m_StorageManager.addCallback(StorageManager.PROP_STORAGE_LIST, this.m_StorageManagerCallBack);
    }
    if (this.m_CountDownTimer == null)
    {
      this.m_CountDownTimer = ((CountDownTimer)this.m_ComponentManager.findComponent(CountDownTimer.class, new Object[] { this }));
      if (this.m_CountDownTimer != null)
      {
        this.m_CountDownTimer.addCallback(CountDownTimer.PROP_REMAINING_SECONDS, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Long> paramAnonymousPropertyKey, PropertyChangeEventArgs<Long> paramAnonymousPropertyChangeEventArgs)
          {
            CameraActivity.-wrap11(CameraActivity.this, ((Long)paramAnonymousPropertyChangeEventArgs.getNewValue()).longValue());
          }
        });
        this.m_CountDownTimer.addHandler(CountDownTimer.EVENT_CANCELLED, new EventHandler()
        {
          public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<EventArgs> paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
          {
            CameraActivity.-wrap10(CameraActivity.this);
          }
        });
        updateSelfTimerInterval();
      }
    }
    this.m_PreviewCover = ((PreviewCover)findComponent(PreviewCover.class));
    if (this.m_PreviewCover != null) {
      this.m_PreviewCover.addOnStateChangedListener(PREVIEW_COVER_STYLE, this.m_PreviewCoverStateChangedListener);
    }
    this.m_ExposureController = ((ExposureController)findComponent(ExposureController.class));
    if (this.m_ExposureController != null) {
      this.m_ExposureController.addCallback(ExposureController.PROP_AE_STATE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<AutoExposureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<AutoExposureState> paramAnonymousPropertyChangeEventArgs)
        {
          if ((paramAnonymousPropertyChangeEventArgs.getNewValue() != AutoExposureState.SEARCHING) || (((Boolean)CameraActivity.this.get(CameraActivity.PROP_IS_CAMERA_PREVIEW_RECEIVED)).booleanValue())) {}
          while (CameraActivity.this.get(CameraActivity.PROP_CAMERA_PREVIEW_STATE) != OperationState.STARTED) {
            return;
          }
          CameraActivity.-set3(CameraActivity.this, Handle.close(CameraActivity.-get5(CameraActivity.this)));
        }
      });
    }
    return true;
  }
  
  private void cancelCaptureDelayTime(CaptureDelayTimeHandle paramCaptureDelayTimeHandle)
  {
    this.m_CaptureDelayTimeHandles.remove(paramCaptureDelayTimeHandle);
  }
  
  private void cancelQuickCaptures()
  {
    if (this.m_IsQuickCaptureScheduled)
    {
      this.m_IsQuickCaptureScheduled = false;
      getHandler().removeMessages(65396);
      Log.w(this.TAG, "cancelQuickCaptures()");
    }
  }
  
  private boolean capturePhoto(final CaptureHandleImpl paramCaptureHandleImpl, final boolean paramBoolean1, boolean paramBoolean2)
  {
    Log.v(this.TAG, "capturePhoto() - Handle : ", paramCaptureHandleImpl, ", from self timer : ", Boolean.valueOf(paramBoolean1), ", from stable waiting : ", Boolean.valueOf(paramBoolean2));
    if (((Boolean)get(PROP_IS_CAMERA_SWITCHING)).booleanValue())
    {
      Log.w(this.TAG, "capturePhoto() - Cannot take snapshot on camera switching state.");
      return false;
    }
    if ((get(PROP_MEDIA_TYPE) != MediaType.VIDEO) || (canVideoSnapshot()))
    {
      if ((!paramBoolean1) && (!paramBoolean2)) {
        break label201;
      }
      if ((!paramBoolean1) && (paramCaptureHandleImpl.frameCount == 1) && (((Boolean)get(PROP_IS_SELF_TIMER_USABLE)).booleanValue())) {
        switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[((MediaType)get(PROP_MEDIA_TYPE)).ordinal()])
        {
        }
      }
    }
    for (;;)
    {
      localObject = (Camera)get(PROP_CAMERA);
      if (localObject != null) {
        break label356;
      }
      Log.e(this.TAG, "capturePhoto() - No camera");
      return false;
      Log.e(this.TAG, "capturePhoto() - Cannot take video snapshot");
      if ((paramBoolean1) || (paramBoolean2)) {
        resetPhotoCaptureState();
      }
      return false;
      label201:
      setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.STARTING);
      break;
      final long l = ((Long)get(PROP_SELF_TIMER_INTERVAL)).longValue();
      if (l > 0L) {
        if (this.m_CountDownTimer != null)
        {
          Log.w(this.TAG, "capturePhoto() - Start self timer");
          this.m_SelfTimerHandle = this.m_CountDownTimer.start(l, 1);
          if (Handle.isValid(this.m_SelfTimerHandle))
          {
            this.m_PhotoCaptureHandle = paramCaptureHandleImpl;
            setReadOnly(PROP_IS_SELF_TIMER_STARTED, Boolean.valueOf(true));
            HandlerUtils.post(this.m_CameraThread, new Runnable()
            {
              public void run()
              {
                CameraActivity.-set0(CameraActivity.this, CameraActivity.-get3(CameraActivity.this).playCameraTimerSound(l));
              }
            });
            return true;
          }
          Log.e(this.TAG, "capturePhoto() - Fail to start self timer");
        }
        else
        {
          Log.w(this.TAG, "capturePhoto() - No CountDownTimer interface");
          continue;
          Log.w(this.TAG, "capturePhoto() - Video snapshot");
        }
      }
    }
    label356:
    int i;
    if ((paramBoolean2) || (paramBoolean1))
    {
      HandlerUtils.removeMessages(this, 65406);
      if (paramCaptureHandleImpl.delayTimeMillis <= 0L) {
        break label586;
      }
      Log.w(this.TAG, "capturePhoto() - Delayed capture after " + paramCaptureHandleImpl.delayTimeMillis + " ms");
      this.m_PhotoCaptureHandle = paramCaptureHandleImpl;
      if (!paramBoolean1) {
        break label574;
      }
      i = 1;
      label428:
      if (!paramBoolean2) {
        break label580;
      }
    }
    label574:
    label580:
    for (int j = 1;; j = 0)
    {
      HandlerUtils.sendMessage(this, 65386, i, j, paramCaptureHandleImpl, true, paramCaptureHandleImpl.delayTimeMillis);
      paramCaptureHandleImpl.delayTimeMillis = 0L;
      return true;
      if ((((Camera)localObject).get(Camera.PROP_LENS_FACING) != Camera.LensFacing.FRONT) || ((paramCaptureHandleImpl.flags & 0x10) == 0) || (paramCaptureHandleImpl.frameCount != 1)) {
        break;
      }
      i = ((Integer)get(PROP_DEVICE_STABILITY_LEVEL)).intValue();
      if (i >= 100) {
        break;
      }
      this.m_PhotoCaptureHandle = paramCaptureHandleImpl;
      setReadOnly(PROP_IS_WAITING_FOR_STABLE_TO_CAPTURE, Boolean.valueOf(true));
      Log.w(this.TAG, "capturePhoto() - Waiting for device stable to capture, stability level : " + i);
      HandlerUtils.sendMessage(this, 65406, 300L);
      return true;
      i = 0;
      break label428;
    }
    label586:
    HandlerUtils.removeMessages(this, 65386);
    this.m_PhotoRotationLockHandle = lockRotation(null);
    Object localObject = new CaptureEventArgs(paramCaptureHandleImpl, paramCaptureHandleImpl.getCaptureTrigger());
    raise(EVENT_CAPTURE_STARTING, (EventArgs)localObject);
    Log.w(this.TAG, "capturePhoto() - Capture");
    if ((paramCaptureHandleImpl.frameCount == 1) && (this.m_PreCaptureFocusLockReqHandles.isEmpty())) {}
    for (paramBoolean1 = false; !HandlerUtils.post(this.m_CameraThread, new Runnable()
        {
          public void run()
          {
            Log.w(CameraActivity.-get1(CameraActivity.this), "capturePhoto() - Capture in camera thread");
            int i = 0;
            if (CameraActivity.this.isServiceMode()) {
              i = 4;
            }
            int j = i;
            if (paramBoolean1) {
              j = i | 0x10;
            }
            CaptureHandle localCaptureHandle = CameraActivity.-get3(CameraActivity.this).capturePhoto(new CameraThread.PhotoParams(paramCaptureHandleImpl.frameCount, paramCaptureHandleImpl.getCaptureMode()), j);
            if (Handle.isValid(localCaptureHandle))
            {
              HandlerUtils.sendMessage(CameraActivity.this, -21, 0, 0, new Object[] { paramCaptureHandleImpl, localCaptureHandle });
              return;
            }
            HandlerUtils.sendMessage(CameraActivity.this, -20, 0, 0, new Object[] { paramCaptureHandleImpl, CameraActivity.-get3(CameraActivity.this).get(CameraThread.PROP_PHOTO_CAPTURE_COMPLETE_REASON) });
          }
        }); paramBoolean1 = true)
    {
      Log.e(this.TAG, "capturePhoto() - Fail to perform cross-thread operation");
      resetPhotoCaptureState();
      return false;
    }
    this.m_PhotoCaptureHandle = paramCaptureHandleImpl;
    setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.CAPTURING);
    resetIdleState();
    return true;
  }
  
  private boolean captureVideo(final CaptureHandleImpl paramCaptureHandleImpl)
  {
    Log.v(this.TAG, "captureVideo() - Handle : ", paramCaptureHandleImpl);
    if (((Boolean)get(PROP_IS_CAMERA_SWITCHING)).booleanValue())
    {
      Log.w(this.TAG, "captureVideo() - Cannot take video on camera switching state.");
      return false;
    }
    if (this.m_ResolutionManager == null)
    {
      Log.e(this.TAG, "captureVideo() - No ResolutionManager");
      return false;
    }
    final CameraThread.VideoParams localVideoParams = new CameraThread.VideoParams((Resolution)this.m_ResolutionManager.get(ResolutionManager.PROP_VIDEO_RESOLUTION));
    localVideoParams.maxFileSize = ((Long)this.m_ResolutionManager.get(ResolutionManager.PROP_MAX_VIDEO_FILE_SIZE)).longValue();
    localVideoParams.maxDurationSeconds = ((Long)this.m_ResolutionManager.get(ResolutionManager.PROP_MAX_VIDEO_DURATION_SECONDS)).longValue();
    this.m_VideoRotationLockHandle = lockRotation(null);
    this.m_VideoCaptureCUDHandle = disableCaptureUI("VideoCaptureStartStop");
    setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.STARTING);
    pauseAudioPlaybackForVideoRecording();
    Log.w(this.TAG, "captureVideo() - Capture");
    if (!HandlerUtils.post(this.m_CameraThread, new Runnable()
    {
      public void run()
      {
        Log.w(CameraActivity.-get1(CameraActivity.this), "captureVideo() - Capture in camera thread");
        int i = 0;
        if (CameraActivity.this.isServiceMode()) {
          i = 4;
        }
        CaptureHandle localCaptureHandle = CameraActivity.-get3(CameraActivity.this).captureVideo(localVideoParams, i);
        if (Handle.isValid(localCaptureHandle))
        {
          HandlerUtils.sendMessage(CameraActivity.this, -31, 0, 0, new Object[] { paramCaptureHandleImpl, localCaptureHandle });
          return;
        }
        HandlerUtils.sendMessage(CameraActivity.this, -30, 0, 0, new Object[] { paramCaptureHandleImpl, CameraActivity.-get3(CameraActivity.this).get(CameraThread.PROP_VIDEO_CAPTURE_COMPLETE_REASON) });
      }
    }))
    {
      Log.e(this.TAG, "captureVideo() - Fail to perform cross-thread operation");
      this.m_VideoRotationLockHandle = Handle.close(this.m_VideoRotationLockHandle);
      this.m_VideoCaptureCUDHandle = Handle.close(this.m_VideoCaptureCUDHandle);
      resetVideoCaptureState();
      return false;
    }
    this.m_VideoCaptureHandle = paramCaptureHandleImpl;
    resetIdleState();
    return true;
  }
  
  private OperationState changeCameraPreviewState(OperationState paramOperationState)
  {
    OperationState localOperationState = this.m_CameraPreviewState;
    if (localOperationState != paramOperationState)
    {
      this.m_CameraPreviewState = paramOperationState;
      notifyPropertyChanged(PROP_CAMERA_PREVIEW_STATE, localOperationState, paramOperationState);
      return this.m_CameraPreviewState;
    }
    return localOperationState;
  }
  
  private void checkLatestLeavingTime()
  {
    int i = 0;
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      i = 1;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      i = 1;
    }
    long l1 = SystemClock.elapsedRealtime();
    long l2 = m_LatestLeaveTimeMillis;
    if ((i == 0) && (l1 - l2 > 300000L))
    {
      Log.v(this.TAG, "checkLatestLeavingTime() - Clear last capture settings");
      setReadOnly(PROP_KEEP_LAST_CAPTURE_SETTINGS, Boolean.valueOf(false));
      return;
    }
    Log.v(this.TAG, "checkLatestLeavingTime() - Keep last capture settings");
    setReadOnly(PROP_KEEP_LAST_CAPTURE_SETTINGS, Boolean.valueOf(true));
  }
  
  private void checkReadyToCapture()
  {
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    }
    while (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.READY)
    {
      setReadOnly(PROP_IS_READY_TO_CAPTURE, Boolean.valueOf(true));
      return;
      setReadOnly(PROP_IS_READY_TO_CAPTURE, Boolean.valueOf(true));
      return;
      if ((!Handle.isValid(this.m_SelfTimerHandle)) && (get(PROP_MEDIA_TYPE) == MediaType.PHOTO) && (!isServiceMode()))
      {
        setReadOnly(PROP_IS_READY_TO_CAPTURE, Boolean.valueOf(true));
        return;
      }
    }
    setReadOnly(PROP_IS_READY_TO_CAPTURE, Boolean.valueOf(false));
  }
  
  private void checkRequiredPermissions()
  {
    boolean bool = true;
    int i = REQUIRED_PERMISSION_LIST.size() - 1;
    while (i >= 0)
    {
      if (!isPermissionGranted((String)REQUIRED_PERMISSION_LIST.get(i)))
      {
        bool = false;
        Log.w(this.TAG, "checkRequiredPermissions() - Permission " + (String)REQUIRED_PERMISSION_LIST.get(i) + " is not granted");
      }
      i -= 1;
    }
    if (bool) {
      Log.v(this.TAG, "checkRequiredPermissions() - All required permissions are granted");
    }
    setReadOnly(PROP_ALL_REQUIRED_PERMISSIONS_GRANTED, Boolean.valueOf(bool));
    if (this.m_CameraThread != null) {
      this.m_CameraThread.notifyRequiredPermissionsState(bool);
    }
  }
  
  private void checkScreenShotRotation(Rotation paramRotation)
  {
    if ((this.m_ScreenShotImageView == null) || (this.m_ScreenShotBitmap == null)) {
      return;
    }
    if (isSameAsWindowManagerRotation(paramRotation))
    {
      updateScreenShotRotation();
      Log.v(this.TAG, "checkScreenShotRotation() - the same with window rotation:");
      return;
    }
    if (SystemClock.elapsedRealtime() - this.m_CheckRotationStartTime <= 3000L)
    {
      HandlerUtils.sendMessage(this, -100, 0, 0, paramRotation, true, 100L);
      Log.v(this.TAG, "checkScreenShotRotation() - wait for next check");
      return;
    }
    Log.v(this.TAG, "checkScreenShotRotation() - check rotation timeout.");
  }
  
  private void checkStartMode()
  {
    Bundle localBundle = null;
    Object localObject = getIntent();
    this.m_StartMode = checkStartMode((Intent)localObject);
    if (localObject != null) {
      localBundle = ((Intent)localObject).getExtras();
    }
    if ((localBundle != null) && (localBundle.containsKey("com.android.systemui.camera_launch_source_gesture"))) {
      this.m_LaunchSource = checkLaunchSource(localBundle.getInt("com.android.systemui.camera_launch_source_gesture"));
    }
    switch (-getcom-oneplus-camera-StartModeSwitchesValues()[this.m_StartMode.ordinal()])
    {
    default: 
      switch (-getcom-oneplus-camera-StartModeSwitchesValues()[this.m_StartMode.ordinal()])
      {
      default: 
        setReadOnly(PROP_IS_SECURE_MODE, Boolean.valueOf(false));
      }
      break;
    }
    for (;;)
    {
      HandlerUtils.removeMessages(this, -51);
      if ((localBundle != null) && (localBundle.containsKey("CameraActivity.IsDebugMode")))
      {
        this.m_DisableDebugModeWhenExiting = true;
        setReadOnly(PROP_IS_DEBUG_MODE, Boolean.valueOf(localBundle.getBoolean("CameraActivity.IsDebugMode")));
      }
      Log.v(this.TAG, "checkStartMode() - Start mode: ", this.m_StartMode, ", Media extras: ", this.m_MediaResultInfo, ", Launch source: ", this.m_LaunchSource);
      return;
      if (localBundle == null) {
        break;
      }
      this.m_MediaResultInfo = new MediaResultInfo();
      localObject = localBundle.get("output");
      if ((localObject instanceof Uri)) {
        this.m_MediaResultInfo.extraOutput = ((Uri)localObject);
      }
      this.m_MediaResultInfo.extraSizeLimit = localBundle.getLong("android.intent.extra.sizeLimit");
      break;
      this.m_MediaResultInfo = new MediaResultInfo();
      if (localBundle == null) {
        break;
      }
      localObject = localBundle.get("output");
      if ((localObject instanceof Uri)) {
        this.m_MediaResultInfo.extraOutput = ((Uri)localObject);
      }
      this.m_MediaResultInfo.extraSizeLimit = localBundle.getLong("android.intent.extra.sizeLimit");
      break;
      if (localBundle == null) {
        break;
      }
      this.m_MediaResultInfo = new MediaResultInfo();
      localObject = localBundle.get("output");
      if ((localObject instanceof Uri)) {
        this.m_MediaResultInfo.extraOutput = ((Uri)localObject);
      }
      this.m_MediaResultInfo.extraSizeLimit = localBundle.getLong("android.intent.extra.sizeLimit");
      this.m_MediaResultInfo.extraDurationLimit = localBundle.getLong("android.intent.extra.durationLimit");
      this.m_MediaResultInfo.extraVideoQuality = localBundle.getInt("android.intent.extra.videoQuality");
      break;
      setReadOnly(PROP_IS_SECURE_MODE, Boolean.valueOf(true));
    }
  }
  
  private void checkTouchDigits(float paramFloat1, float paramFloat2)
  {
    if ((get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.STARTING) && (((Long)get(PROP_SELF_TIMER_INTERVAL)).longValue() == 5L))
    {
      RectF localRectF = (RectF)this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_BOUNDS);
      float f = localRectF.width();
      if (f - localRectF.height() > 1.0F)
      {
        this.m_TouchDigits = 0;
        return;
      }
      f /= 3.0F;
      int i = (int)((paramFloat1 - localRectF.left) / f);
      i = (int)((paramFloat2 - localRectF.top) / f) * 3 + i + 1;
      Log.v(this.TAG, "checkTouchDigits() - Touch digit: ", Integer.valueOf(i), ", current: ", Integer.valueOf(this.m_TouchDigits));
      if ((this.m_TouchDigits == 0) && (i == 3))
      {
        this.m_TouchDigits = 3;
        return;
      }
      if ((this.m_TouchDigits == 3) && (i == 7))
      {
        this.m_TouchDigits = 73;
        return;
      }
      if ((this.m_TouchDigits == 73) && (i == 2))
      {
        this.m_TouchDigits = 273;
        return;
      }
      if ((this.m_TouchDigits == 273) && (i == 1))
      {
        if (((Boolean)get(PROP_IS_DEBUG_MODE)).booleanValue()) {
          setDebugMode(false);
        }
        for (;;)
        {
          stopPhotoCapture(this.m_PhotoCaptureHandle);
          this.m_TouchDigits = 0;
          return;
          setDebugMode(true);
        }
      }
      this.m_TouchDigits = 0;
      return;
    }
    this.m_TouchDigits = 0;
  }
  
  private void completePhotoCapture(CaptureHandleImpl paramCaptureHandleImpl)
  {
    if (this.m_PhotoCaptureHandle != paramCaptureHandleImpl)
    {
      Log.e(this.TAG, "completePhotoCapture() - Invalid handle");
      return;
    }
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "completePhotoCapture() - Current capture state : " + get(PROP_PHOTO_CAPTURE_STATE));
      return;
    }
    if (get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.CAPTURING) {}
    for (int i = 1;; i = 0)
    {
      setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.STOPPING);
      if ((!isServiceMode()) || (i == 0)) {
        break;
      }
      setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.REVIEWING);
      if (!showReviewScreen()) {
        onCaptureCompleted(paramCaptureHandleImpl);
      }
      return;
    }
    onCaptureCompleted(paramCaptureHandleImpl);
  }
  
  private void completeVideoCapture(CaptureHandleImpl paramCaptureHandleImpl)
  {
    if (this.m_VideoCaptureHandle != paramCaptureHandleImpl)
    {
      Log.e(this.TAG, "completeVideoCapture() - Invalid handle");
      return;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    case 4: 
    case 5: 
    case 7: 
    case 8: 
    default: 
      Log.e(this.TAG, "completeVideoCapture() - Current capture state : " + get(PROP_VIDEO_CAPTURE_STATE));
      return;
    }
    if (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.STOPPING) {}
    for (int i = 1;; i = 0)
    {
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.STOPPING);
      if ((!isServiceMode()) || (i == 0)) {
        break;
      }
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.REVIEWING);
      if (!showReviewScreen()) {
        onCaptureCompleted(paramCaptureHandleImpl);
      }
      return;
    }
    onCaptureCompleted(paramCaptureHandleImpl);
  }
  
  private void enableBurstPhotoCapture(Handle paramHandle)
  {
    verifyAccess();
    if ((this.m_BurstDisableHandles.remove(paramHandle)) && (this.m_BurstDisableHandles.isEmpty()))
    {
      updateBurstEnablingState();
      return;
    }
  }
  
  private void enableCaptureUI(UIDisableHandle paramUIDisableHandle)
  {
    verifyAccess();
    if (!this.m_CaptureUIDisableHandles.remove(paramUIDisableHandle)) {
      return;
    }
    Log.w(this.TAG, "enableCaptureUI() - Handle : " + paramUIDisableHandle + ", handle count : " + this.m_CaptureUIDisableHandles.size());
    if (this.m_CaptureUIDisableHandles.isEmpty()) {
      setReadOnly(PROP_IS_CAPTURE_UI_ENABLED, Boolean.valueOf(true));
    }
  }
  
  private void enableFastShotToShot(Handle paramHandle)
  {
    verifyAccess();
    if ((this.m_FastShotToShotDisableHandles.remove(paramHandle)) && (this.m_FastShotToShotDisableHandles.isEmpty()))
    {
      if (get(PROP_MEDIA_TYPE) == MediaType.PHOTO) {
        setReadOnly(PROP_IS_FAST_SHOT_TO_SHOT_ENABLED, Boolean.valueOf(true));
      }
      return;
    }
  }
  
  private void enableSelfTimer(Handle paramHandle)
  {
    verifyAccess();
    if ((this.m_SelfTimerDisableHandles.remove(paramHandle)) && (this.m_SelfTimerDisableHandles.isEmpty()))
    {
      updateSelfTimerUsability();
      return;
    }
  }
  
  private void exitSimpleUIMode(Handle paramHandle)
  {
    verifyAccess();
    if (!this.m_SimpleUIModeHandles.remove(paramHandle)) {
      return;
    }
    Log.v(this.TAG, "exitSimpleUIMode() - Handle count : ", Integer.valueOf(this.m_SimpleUIModeHandles.size()));
    if (this.m_SimpleUIModeHandles.isEmpty()) {
      setReadOnly(PROP_IS_SIMPLE_UI_MODE_ENTERED, Boolean.valueOf(false));
    }
  }
  
  private void idle()
  {
    if ((!this.m_IsIdle) && (((Boolean)get(PROP_IS_RUNNING)).booleanValue()))
    {
      Log.w(this.TAG, "idle()");
      this.m_IsIdle = true;
      HandlerUtils.removeMessages(this, -50);
      getWindow().clearFlags(128);
      notifyPropertyChanged(PROP_IS_IDLE, Boolean.valueOf(false), Boolean.valueOf(true));
    }
  }
  
  private boolean isInteractive()
  {
    return ((PowerManager)getSystemService("power")).isInteractive();
  }
  
  private boolean isSameAsWindowManagerRotation(Rotation paramRotation)
  {
    Object localObject = getWindowManager();
    if (localObject == null)
    {
      Log.w(this.TAG, "isSameAsWindowRotation() - WindowManager is null");
      return false;
    }
    localObject = ((WindowManager)localObject).getDefaultDisplay();
    if (localObject == null)
    {
      Log.w(this.TAG, "isSameAsWindowRotation() - display is null");
      return false;
    }
    int i = ((Display)localObject).getRotation();
    int j = paramRotation.getDeviceOrientation();
    if ((i == 3) && (j == 90)) {
      return true;
    }
    if ((i == 1) && (j == 270)) {
      return true;
    }
    if ((i == 2) && (j == 180)) {
      return true;
    }
    return (i == 0) && (j == 0);
  }
  
  private boolean isStartedInSelfieMode()
  {
    return (this.m_StartMode == StartMode.SECURE_PHOTO_SELFIE) || (this.m_StartMode == StartMode.NORMAL_PHOTO_SELFIE);
  }
  
  private void notifyCameraThreadRotationChanged(Rotation paramRotation1, final Rotation paramRotation2)
  {
    HandlerUtils.post(this.m_CameraThread, new Runnable()
    {
      public void run()
      {
        CameraActivity.-get3(CameraActivity.this).set(CameraThread.PROP_CAPTURE_ROTATION, paramRotation2);
      }
    });
  }
  
  private void notifyUIRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    if (this.m_ScreenShotBitmap != null)
    {
      if ((paramRotation1 == null) || (paramRotation1.isLandscape() == paramRotation2.isLandscape())) {
        HandlerUtils.sendMessage(this, -90, 0, 0, paramRotation2, true, 300L);
      }
      if ((this.m_ScreenShotImageView != null) && (paramRotation2 != this.m_ActivityRotation)) {
        this.m_ScreenShotImageView.setVisibility(0);
      }
    }
    paramRotation1 = this.m_Rotation;
    if (paramRotation1 == paramRotation2) {
      return;
    }
    this.m_Rotation = paramRotation2;
    notifyPropertyChanged(PROP_ROTATION, paramRotation1, paramRotation2);
  }
  
  private void onAccelerometerValuesChanged(float[] paramArrayOfFloat)
  {
    if (!((Boolean)get(PROP_IS_CAMERA_PREVIEW_RECEIVED)).booleanValue()) {
      return;
    }
    float[] arrayOfFloat1 = this.m_AccelerometerValuesTable[this.m_AccelerometerValuesIndex];
    this.m_AccelerometerValuesIndex = ((this.m_AccelerometerValuesIndex + 1) % 2);
    float[] arrayOfFloat2 = this.m_AccelerometerValuesTable[this.m_AccelerometerValuesIndex];
    System.arraycopy(paramArrayOfFloat, 0, arrayOfFloat2, 0, 3);
    notifyPropertyChanged(PROP_ACCELEROMETER_VALUES, arrayOfFloat1, arrayOfFloat2);
    int i;
    double d;
    if (!this.m_IsGyroscopeStarted)
    {
      float f1 = Math.abs(arrayOfFloat2[0] - arrayOfFloat1[0]);
      float f2 = Math.abs(arrayOfFloat2[1] - arrayOfFloat1[1]);
      float f3 = Math.abs(arrayOfFloat2[2] - arrayOfFloat1[2]);
      if (this.m_AccelValueSamples[this.m_AccelValueSampleIndex] == null) {
        this.m_AccelValueSamples[this.m_AccelValueSampleIndex] = new float[3];
      }
      this.m_AccelValueSamples[this.m_AccelValueSampleIndex][0] = f1;
      this.m_AccelValueSamples[this.m_AccelValueSampleIndex][1] = f2;
      this.m_AccelValueSamples[this.m_AccelValueSampleIndex][2] = f3;
      this.m_AccelValueSampleIndex = ((this.m_AccelValueSampleIndex + 1) % this.m_AccelValueSamples.length);
      float f5 = 0.0F;
      f2 = 0.0F;
      f1 = 0.0F;
      int j = 0;
      i = this.m_AccelValueSamples.length - 1;
      while (i >= 0)
      {
        paramArrayOfFloat = this.m_AccelValueSamples[i];
        float f6 = f5;
        float f4 = f2;
        f3 = f1;
        int k = j;
        if (paramArrayOfFloat != null)
        {
          k = j + 1;
          f6 = f5 + paramArrayOfFloat[0];
          f4 = f2 + paramArrayOfFloat[1];
          f3 = f1 + paramArrayOfFloat[2];
        }
        i -= 1;
        f5 = f6;
        f2 = f4;
        f1 = f3;
        j = k;
      }
      f3 = f5 / j;
      f2 /= j;
      f1 /= j;
      d = Math.sqrt(f3 * f3 + f2 * f2 + f1 * f1);
      if (d > 0.27000001072883606D) {
        break label374;
      }
      i = 100;
    }
    for (;;)
    {
      updateStabilityLevel(i);
      return;
      label374:
      if (d >= 0.4300000071525574D) {
        i = 0;
      } else {
        i = (int)Math.round(100.0D - (d - 0.27000001072883606D) / 0.1599999964237213D * 100.0D);
      }
    }
  }
  
  private void onBurstPhotoReceived(CaptureEventArgs paramCaptureEventArgs)
  {
    Object localObject = null;
    if ((this.m_PhotoCaptureHandle == null) || (this.m_PhotoCaptureHandle.internalCaptureHandle != paramCaptureEventArgs.getCaptureHandle()))
    {
      Log.w(this.TAG, "onBurstPhotoReceived() - Unknown capture handle : " + paramCaptureEventArgs.getCaptureHandle());
      String str = this.TAG;
      StringBuilder localStringBuilder = new StringBuilder().append("onBurstPhotoReceived() - Expected capture handle : ");
      paramCaptureEventArgs = (CaptureEventArgs)localObject;
      if (this.m_PhotoCaptureHandle != null) {
        paramCaptureEventArgs = this.m_PhotoCaptureHandle.internalCaptureHandle;
      }
      Log.w(str, paramCaptureEventArgs);
      return;
    }
    raise(EVENT_BURST_PHOTO_RECEIVED, new CaptureEventArgs(this.m_PhotoCaptureHandle, paramCaptureEventArgs.getFrameIndex(), this.m_PhotoCaptureHandle.getCaptureTrigger()));
  }
  
  private void onCameraChanged(Camera paramCamera1, Camera paramCamera2)
  {
    updateBurstEnablingState();
  }
  
  private void onCameraPreviewReceived()
  {
    OperationState localOperationState = (OperationState)get(PROP_CAMERA_PREVIEW_STATE);
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[localOperationState.ordinal()])
    {
    default: 
      Log.e(this.TAG, "onCameraPreviewReceived() - Preview state is " + localOperationState);
      return;
    }
    if (!setReadOnly(PROP_IS_CAMERA_PREVIEW_RECEIVED, Boolean.valueOf(true))) {
      return;
    }
    this.m_PreviewCoverHandle = Handle.close(this.m_PreviewCoverHandle);
    getHandler().sendEmptyMessageDelayed(-70, 100L);
    if (this.m_CameraPreviewState == OperationState.STARTED) {
      this.m_CameraPreviewStartCUDHandle = Handle.close(this.m_CameraPreviewStartCUDHandle);
    }
    if (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.CAPTURING) {
      this.m_VideoCaptureCUDHandle = Handle.close(this.m_VideoCaptureCUDHandle);
    }
    if (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.PREPARING) {
      resetVideoCaptureState();
    }
    if (get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.PREPARING) {
      resetPhotoCaptureState();
    }
  }
  
  private void onCameraPreviewStartFailed(Camera paramCamera, int paramInt)
  {
    if (get(PROP_CAMERA) != paramCamera) {
      return;
    }
    if (this.m_CameraPreviewSessionID != paramInt)
    {
      Log.w(this.TAG, "onCameraPreviewStartFailed() - Preview session incorrect: " + paramInt + ", current preview session: " + this.m_CameraPreviewSessionID);
      return;
    }
    if (this.m_CameraPreviewState != OperationState.STARTING)
    {
      Log.w(this.TAG, "onCameraPreviewStartFailed() - Preview state is " + this.m_CameraPreviewState);
      return;
    }
    onCameraPreviewStartFailed();
  }
  
  private void onCameraPreviewStarted(Camera paramCamera, int paramInt)
  {
    if (get(PROP_CAMERA) != paramCamera) {
      return;
    }
    if (this.m_CameraPreviewSessionID != paramInt)
    {
      Log.w(this.TAG, "onCameraPreviewStarted() - Preview session incorrect: " + paramInt + ", current preview session: " + this.m_CameraPreviewSessionID);
      return;
    }
    if (this.m_CameraPreviewState != OperationState.STARTING)
    {
      Log.w(this.TAG, "onCameraPreviewStarted() - Preview state is " + this.m_CameraPreviewState);
      return;
    }
    onCameraPreviewStarted();
  }
  
  private void onCameraThreadCaptureStateChanged(PhotoCaptureState paramPhotoCaptureState1, PhotoCaptureState paramPhotoCaptureState2)
  {
    if ((paramPhotoCaptureState1 == PhotoCaptureState.STARTING) && (paramPhotoCaptureState2 != PhotoCaptureState.CAPTURING))
    {
      paramPhotoCaptureState1 = (CaptureCompleteReason)this.m_CameraThread.get(CameraThread.PROP_PHOTO_CAPTURE_COMPLETE_REASON);
      switch (-getcom-oneplus-camera-CaptureCompleteReasonSwitchesValues()[paramPhotoCaptureState1.ordinal()])
      {
      default: 
        Log.e(this.TAG, "onCameraThreadCaptureStateChanged() - Photo capture stopped unexpectly, reason : " + paramPhotoCaptureState1);
        onPhotoCaptureFailed(this.m_PhotoCaptureHandle, paramPhotoCaptureState1);
      }
    }
  }
  
  private void onCameraThreadCaptureStateChanged(VideoCaptureState paramVideoCaptureState1, VideoCaptureState paramVideoCaptureState2)
  {
    if (paramVideoCaptureState2 == VideoCaptureState.STOPPING)
    {
      HandlerUtils.removeMessages(this, -40);
      if (Handle.isValid(this.m_VideoCaptureHandle)) {
        stopVideoCapture(this.m_VideoCaptureHandle, false, true);
      }
    }
    do
    {
      return;
      if ((paramVideoCaptureState1 == VideoCaptureState.STARTING) && (paramVideoCaptureState2 != VideoCaptureState.CAPTURING))
      {
        onVideoCaptureFailed(this.m_VideoCaptureHandle, (CaptureCompleteReason)this.m_CameraThread.get(CameraThread.PROP_VIDEO_CAPTURE_COMPLETE_REASON));
        return;
      }
    } while ((paramVideoCaptureState1 != VideoCaptureState.STARTING) || (paramVideoCaptureState2 != VideoCaptureState.CAPTURING));
    this.m_VideoRecordStartTime = SystemClock.elapsedRealtime();
    this.m_TotalPausedVideoRecordingTime = 0L;
    updateElapsedRecordingTime(-1L, -1L);
  }
  
  private void onCaptureCompleted(CaptureHandleImpl paramCaptureHandleImpl)
  {
    Log.w(this.TAG, "onCaptureCompleted() - Handle : " + paramCaptureHandleImpl);
    Object localObject = this.m_PendingPhotoCaptureHandle;
    this.m_PendingPhotoCaptureHandle = null;
    this.m_TouchDigits = 0;
    paramCaptureHandleImpl.close();
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[paramCaptureHandleImpl.getMediaType().ordinal()])
    {
    default: 
      if (!((Boolean)get(PROP_IS_RUNNING)).booleanValue())
      {
        getHandler().postAtFrontOfQueue(new Runnable()
        {
          public void run()
          {
            if (!((Boolean)CameraActivity.this.get(CameraActivity.PROP_IS_RUNNING)).booleanValue()) {
              CameraActivity.this.stopCameraPreview(true);
            }
          }
        });
        label112:
        switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[paramCaptureHandleImpl.getMediaType().ordinal()])
        {
        }
      }
      break;
    }
    for (;;)
    {
      resetIdleState();
      return;
      this.m_PhotoRotationLockHandle = Handle.close(this.m_PhotoRotationLockHandle);
      break;
      this.m_VideoRotationLockHandle = Handle.close(this.m_VideoRotationLockHandle);
      break;
      if (this.m_CameraPreviewState != OperationState.STARTED) {
        break label112;
      }
      changeCameraPreviewState(OperationState.STOPPED);
      break label112;
      onCaptureCompleted(paramCaptureHandleImpl, (CaptureCompleteReason)this.m_CameraThread.get(CameraThread.PROP_PHOTO_CAPTURE_COMPLETE_REASON));
      if ((get(PROP_STATE) == BaseActivity.State.RUNNING) && (((Boolean)get(PROP_IS_RUNNING)).booleanValue())) {
        if (startCameraPreview())
        {
          paramCaptureHandleImpl = (CaptureHandleImpl)localObject;
          if (get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.REVIEWING) {
            paramCaptureHandleImpl = null;
          }
          localObject = paramCaptureHandleImpl;
          if (((Boolean)get(PROP_IS_SELF_TIMER_USABLE)).booleanValue())
          {
            localObject = paramCaptureHandleImpl;
            if (((Long)get(PROP_SELF_TIMER_INTERVAL)).longValue() > 0L)
            {
              Log.v(this.TAG, "onCaptureCompleted() - Clear pending handle");
              localObject = null;
            }
          }
          resetPhotoCaptureState();
          if ((localObject != null) && (SystemClock.elapsedRealtime() - ((CaptureHandleImpl)localObject).creationTime <= 800L))
          {
            Log.w(this.TAG, "onCaptureCompleted() - Capture next photo immediately");
            if (!capturePhoto((CaptureHandleImpl)localObject, false, false)) {}
          }
        }
        else
        {
          Log.e(this.TAG, "onCaptureCompleted() - Fail to start camera preview");
          setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.PREPARING);
        }
      }
      for (;;)
      {
        if ((get(PROP_MEDIA_TYPE) == MediaType.VIDEO) && (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.STOPPING) && (this.m_VideoCaptureHandle != null))
        {
          Log.w(this.TAG, "onCaptureCompleted() - Continue stopping video recording");
          stopVideoCapture(this.m_VideoCaptureHandle, true, false);
        }
        setReadOnly(PROP_IS_BURST_PHOTO_ON_CAPTURE, Boolean.valueOf(false));
        break;
        Log.w(this.TAG, "onCaptureCompleted() - Activity state is " + get(PROP_STATE));
        setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.PREPARING);
      }
      setReadOnly(PROP_ELAPSED_RECORDING_SECONDS, Long.valueOf(0L));
      this.m_VideoCaptureCUDHandle = Handle.close(this.m_VideoCaptureCUDHandle);
      resumeAudioPlayback();
      CaptureCompleteReason localCaptureCompleteReason = (CaptureCompleteReason)get(PROP_VIDEO_CAPTURE_COMPLETE_REASON);
      localObject = localCaptureCompleteReason;
      if (localCaptureCompleteReason == CaptureCompleteReason.NORMAL) {
        localObject = (CaptureCompleteReason)this.m_CameraThread.get(CameraThread.PROP_VIDEO_CAPTURE_COMPLETE_REASON);
      }
      onCaptureCompleted(paramCaptureHandleImpl, (CaptureCompleteReason)localObject);
      if ((get(PROP_STATE) == BaseActivity.State.RUNNING) && (((Boolean)get(PROP_IS_RUNNING)).booleanValue()))
      {
        if (startCameraPreview())
        {
          resetVideoCaptureState();
        }
        else
        {
          Log.e(this.TAG, "onCaptureCompleted() - Fail to start camera preview");
          setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.PREPARING);
        }
      }
      else
      {
        Log.w(this.TAG, "onCaptureCompleted() - Activity state is " + get(PROP_STATE));
        setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.PREPARING);
      }
    }
  }
  
  private void onCountDownTimerCancelled()
  {
    if (!Handle.isValid(this.m_SelfTimerHandle)) {
      return;
    }
    this.m_SelfTimerHandle = null;
    setReadOnly(PROP_IS_SELF_TIMER_STARTED, Boolean.valueOf(false));
    HandlerUtils.post(this.m_CameraThread, new Runnable()
    {
      public void run()
      {
        if (Handle.isValid(CameraActivity.-get2(CameraActivity.this))) {
          Handle.close(CameraActivity.-get2(CameraActivity.this));
        }
      }
    });
    if (get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.STARTING)
    {
      resetPhotoCaptureState();
      return;
    }
    Log.w(this.TAG, "onCountDownTimerCancelled() - Photo capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
  }
  
  private void onCountDownTimerChanged(final long paramLong)
  {
    if (!Handle.isValid(this.m_SelfTimerHandle)) {
      return;
    }
    Log.v(this.TAG, "onCountDownTimerChanged() - Remaining seconds : ", Long.valueOf(paramLong));
    if (get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.STARTING)
    {
      if (paramLong == 0L)
      {
        this.m_SelfTimerHandle = null;
        setReadOnly(PROP_IS_SELF_TIMER_STARTED, Boolean.valueOf(false));
        if (Handle.isValid(this.m_PhotoCaptureHandle))
        {
          Log.v(this.TAG, "onCountDownTimerChanged() - Capture photo");
          capturePhoto(this.m_PhotoCaptureHandle, true, false);
        }
      }
      while (paramLong < 2L)
      {
        return;
        Log.e(this.TAG, "onCountDownTimerChanged() - No capture handle");
        resetPhotoCaptureState();
        return;
      }
      HandlerUtils.post(this.m_CameraThread, new Runnable()
      {
        public void run()
        {
          CameraActivity.-set0(CameraActivity.this, CameraActivity.-get3(CameraActivity.this).playCameraTimerSound(paramLong));
        }
      });
      return;
    }
    Log.e(this.TAG, "onCountDownTimerChanged() - Photo capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
    this.m_SelfTimerHandle = Handle.close(this.m_SelfTimerHandle);
    setReadOnly(PROP_IS_SELF_TIMER_STARTED, Boolean.valueOf(false));
  }
  
  private void onDefaultPhotoCaptureCompleted(CaptureEventArgs paramCaptureEventArgs)
  {
    Object localObject = null;
    if ((this.m_PhotoCaptureHandle == null) || (this.m_PhotoCaptureHandle.internalCaptureHandle != paramCaptureEventArgs.getCaptureHandle()))
    {
      Log.w(this.TAG, "onDefaultPhotoCaptureCompleted() - Unknown capture handle : " + paramCaptureEventArgs.getCaptureHandle());
      String str = this.TAG;
      StringBuilder localStringBuilder = new StringBuilder().append("onDefaultPhotoCaptureCompleted() - Expected capture handle : ");
      paramCaptureEventArgs = (CaptureEventArgs)localObject;
      if (this.m_PhotoCaptureHandle != null) {
        paramCaptureEventArgs = this.m_PhotoCaptureHandle.internalCaptureHandle;
      }
      Log.w(str, paramCaptureEventArgs);
      return;
    }
    completeCapture(this.m_PhotoCaptureHandle);
  }
  
  private void onDefaultVideoCaptureCompleted(CaptureEventArgs paramCaptureEventArgs)
  {
    Object localObject = null;
    if ((this.m_VideoCaptureHandle == null) || (this.m_VideoCaptureHandle.internalCaptureHandle != paramCaptureEventArgs.getCaptureHandle()))
    {
      Log.w(this.TAG, "onDefaultVideoCaptureCompleted() - Unknown capture handle : " + paramCaptureEventArgs.getCaptureHandle());
      String str = this.TAG;
      StringBuilder localStringBuilder = new StringBuilder().append("onDefaultVideoCaptureCompleted() - Expected capture handle : ");
      paramCaptureEventArgs = (CaptureEventArgs)localObject;
      if (this.m_VideoCaptureHandle != null) {
        paramCaptureEventArgs = this.m_VideoCaptureHandle.internalCaptureHandle;
      }
      Log.w(str, paramCaptureEventArgs);
      return;
    }
    completeCapture(this.m_VideoCaptureHandle);
  }
  
  private void onGyroscopeValuesChanged(float[] paramArrayOfFloat)
  {
    float f1 = Math.abs(paramArrayOfFloat[0]);
    float f2 = Math.abs(paramArrayOfFloat[1]);
    float f3 = Math.abs(paramArrayOfFloat[2]);
    if (this.m_AccelValueSamples[this.m_AccelValueSampleIndex] == null) {
      this.m_AccelValueSamples[this.m_AccelValueSampleIndex] = new float[3];
    }
    this.m_AccelValueSamples[this.m_AccelValueSampleIndex][0] = f1;
    this.m_AccelValueSamples[this.m_AccelValueSampleIndex][1] = f2;
    this.m_AccelValueSamples[this.m_AccelValueSampleIndex][2] = f3;
    this.m_AccelValueSampleIndex = ((this.m_AccelValueSampleIndex + 1) % this.m_AccelValueSamples.length);
    float f5 = 0.0F;
    f2 = 0.0F;
    f1 = 0.0F;
    int j = 0;
    int i = this.m_AccelValueSamples.length - 1;
    while (i >= 0)
    {
      paramArrayOfFloat = this.m_AccelValueSamples[i];
      float f6 = f5;
      float f4 = f2;
      f3 = f1;
      int k = j;
      if (paramArrayOfFloat != null)
      {
        k = j + 1;
        f6 = f5 + paramArrayOfFloat[0];
        f4 = f2 + paramArrayOfFloat[1];
        f3 = f1 + paramArrayOfFloat[2];
      }
      i -= 1;
      f5 = f6;
      f2 = f4;
      f1 = f3;
      j = k;
    }
    f3 = f5 / j;
    f2 /= j;
    f1 /= j;
    f1 = Math.max(Math.max(f3, f2), f1);
    if (f1 <= 0.02F) {
      i = 100;
    }
    for (;;)
    {
      updateStabilityLevel(i);
      return;
      if (f1 >= 0.5F) {
        i = 0;
      } else {
        i = Math.round(100.0F - (f1 - 0.02F) / 0.48F * 100.0F);
      }
    }
  }
  
  private void onMediaFileSaved(MediaEventArgs paramMediaEventArgs)
  {
    raise(EVENT_MEDIA_FILE_SAVED, paramMediaEventArgs);
  }
  
  private void onMediaSaveCancelled(MediaEventArgs paramMediaEventArgs)
  {
    raise(EVENT_MEDIA_SAVE_CANCELLED, paramMediaEventArgs);
  }
  
  private void onMediaSaveFailed(MediaEventArgs paramMediaEventArgs)
  {
    raise(EVENT_MEDIA_SAVE_FAILED, paramMediaEventArgs);
  }
  
  private void onMediaSaved(MediaEventArgs paramMediaEventArgs)
  {
    raise(EVENT_MEDIA_SAVED, paramMediaEventArgs);
  }
  
  private void onPermissionResult(String paramString, int paramInt)
  {
    if (paramString.equals("android.permission.ACCESS_FINE_LOCATION"))
    {
      paramString = (Settings)get(PROP_SETTINGS);
      if (paramInt != -1) {
        break label51;
      }
      paramString.set("Location.Save", Boolean.valueOf(false));
    }
    for (;;)
    {
      this.m_OptionalPermissions.remove("android.permission.ACCESS_FINE_LOCATION");
      return;
      label51:
      if (paramInt == 0) {
        paramString.set("Location.Save", Boolean.valueOf(true));
      }
    }
  }
  
  private void onPhotoCaptureStarted(CaptureHandleImpl paramCaptureHandleImpl, CaptureHandle paramCaptureHandle)
  {
    if (this.m_PhotoCaptureHandle != paramCaptureHandleImpl)
    {
      Log.e(this.TAG, "onPhotoCaptureStarted() - Unknown handle : " + paramCaptureHandleImpl + ", expected handle : " + this.m_PhotoCaptureHandle);
      return;
    }
    Log.v(this.TAG, "onPhotoCaptureStarted() - Handle : ", paramCaptureHandleImpl);
    if (paramCaptureHandleImpl.frameCount != 1) {
      setReadOnly(PROP_IS_BURST_PHOTO_ON_CAPTURE, Boolean.valueOf(true));
    }
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "onPhotoCaptureStarted() - Photo capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
      return;
    case 1: 
      paramCaptureHandleImpl.internalCaptureHandle = paramCaptureHandle;
      raise(EVENT_CAPTURE_STARTED, new CaptureEventArgs(paramCaptureHandleImpl, paramCaptureHandleImpl.getCaptureTrigger()));
      return;
    }
    Log.w(this.TAG, "onPhotoCaptureStarted() - Stop capture immediately");
    paramCaptureHandleImpl.internalCaptureHandle = paramCaptureHandle;
    Handle.close(paramCaptureHandle);
  }
  
  private void onPostviewReceived(CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    if (!Handle.isValid(this.m_PhotoCaptureHandle))
    {
      Log.w(this.TAG, "onPostviewReceived() - Invalid photo capture handle");
      return;
    }
    if (this.m_PhotoCaptureHandle.getInternalCaptureHandle() != paramCameraCaptureEventArgs.getHandle())
    {
      Log.w(this.TAG, "onPostviewReceived() - Invalid internal capture handle");
      return;
    }
    paramCameraCaptureEventArgs = CameraCaptureEventArgs.obtain(this.m_PhotoCaptureHandle, null, paramCameraCaptureEventArgs.getFrameIndex(), paramCameraCaptureEventArgs.getPictureFormat(), paramCameraCaptureEventArgs.getPictureSize(), paramCameraCaptureEventArgs.getPicturePlanes(), paramCameraCaptureEventArgs.getCaptureResult(), paramCameraCaptureEventArgs.getTakenTime());
    raise(EVENT_POSTVIEW_RECEIVED, paramCameraCaptureEventArgs);
  }
  
  private void onPreviewCoverStateChanged(PreviewCover.UIState paramUIState1, PreviewCover.UIState paramUIState2)
  {
    if (this.m_PendingSwitchCamera == null) {
      return;
    }
    switch (-getcom-oneplus-camera-ui-PreviewCover$UIStateSwitchesValues()[paramUIState2.ordinal()])
    {
    case 2: 
    default: 
      return;
    case 4: 
      stopCameraPreview(false);
      return;
    }
    switchCamera((Camera)get(PROP_CAMERA), this.m_PendingSwitchCamera, 12);
    this.m_PendingSwitchCamera = null;
  }
  
  private void onRequestedOrientationChanged(int paramInt)
  {
    Rotation localRotation2 = this.m_ActivityRotation;
    Rotation localRotation1;
    switch (getRequestedOrientation())
    {
    default: 
      Log.e(this.TAG, "onRequestedOrientationChanged() - Unsupported orientation : " + paramInt);
      localRotation1 = Rotation.LANDSCAPE;
    }
    for (;;)
    {
      if (localRotation2 != localRotation1)
      {
        this.m_ActivityRotation = localRotation1;
        notifyPropertyChanged(PROP_ACTIVITY_ROTATION, localRotation2, localRotation1);
      }
      return;
      localRotation1 = Rotation.LANDSCAPE;
      continue;
      localRotation1 = Rotation.PORTRAIT;
      continue;
      localRotation1 = Rotation.INVERSE_LANDSCAPE;
      continue;
      localRotation1 = Rotation.INVERSE_PORTRAIT;
    }
  }
  
  private void onShutter(CaptureEventArgs paramCaptureEventArgs)
  {
    Object localObject = null;
    if ((this.m_PhotoCaptureHandle == null) || (this.m_PhotoCaptureHandle.internalCaptureHandle != paramCaptureEventArgs.getCaptureHandle()))
    {
      Log.w(this.TAG, "onShutter() - Unknown capture handle : " + paramCaptureEventArgs.getCaptureHandle());
      String str = this.TAG;
      StringBuilder localStringBuilder = new StringBuilder().append("onShutter() - Expected capture handle : ");
      paramCaptureEventArgs = (CaptureEventArgs)localObject;
      if (this.m_PhotoCaptureHandle != null) {
        paramCaptureEventArgs = this.m_PhotoCaptureHandle.internalCaptureHandle;
      }
      Log.w(str, paramCaptureEventArgs);
      return;
    }
    raise(EVENT_SHUTTER, new CaptureEventArgs(this.m_PhotoCaptureHandle, paramCaptureEventArgs.getFrameIndex(), this.m_PhotoCaptureHandle.getCaptureTrigger()));
  }
  
  private void onSystemOrientationSettingsChanged(boolean paramBoolean)
  {
    Rotation localRotation = OrientationManager.getRotation();
    if (localRotation != null) {
      onRotationChanged(this.m_Rotation, localRotation);
    }
  }
  
  private void onUnprocessedPictureReceived(UnprocessedPictureEventArgs paramUnprocessedPictureEventArgs)
  {
    raise(EVENT_UNPROCESSED_PHOTO_RECEIVED, paramUnprocessedPictureEventArgs);
  }
  
  private void onVideoCapturePaused(CaptureHandleImpl paramCaptureHandleImpl, boolean paramBoolean)
  {
    if (this.m_VideoCaptureHandle != paramCaptureHandleImpl)
    {
      Log.w(this.TAG, "onVideoCapturePaused() - Unknown handle : " + paramCaptureHandleImpl + ", expected handle : " + this.m_VideoCaptureHandle);
      return;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "onVideoCapturePaused() - Current capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return;
    }
    if (paramBoolean)
    {
      Log.v(this.TAG, "onVideoCapturePaused()");
      getHandler().removeMessages(-40);
      this.m_VideoRecordingPausedTime = SystemClock.elapsedRealtime();
      this.m_ElapsedPartialRecordingTimeMillis = (this.m_VideoRecordingPausedTime - this.m_LastElapsedRecordingTimeCheckTime);
      if (this.m_ElapsedPartialRecordingTimeMillis < 0L)
      {
        Log.w(this.TAG, "onVideoCapturePaused() - Unexpected partial elapsed time : " + this.m_ElapsedPartialRecordingTimeMillis + " ms");
        this.m_ElapsedPartialRecordingTimeMillis = 0L;
        setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.PAUSED);
      }
    }
    for (;;)
    {
      this.m_VideoCaptureCUDHandle = Handle.close(this.m_VideoCaptureCUDHandle);
      return;
      if (this.m_ElapsedPartialRecordingTimeMillis > 1000L)
      {
        Log.w(this.TAG, "onVideoCapturePaused() - Unexpected partial elapsed time : " + this.m_ElapsedPartialRecordingTimeMillis + " ms");
        this.m_ElapsedPartialRecordingTimeMillis = 1000L;
        break;
      }
      Log.d(this.TAG, "onVideoCapturePaused() - Partial elapsed time : " + this.m_ElapsedPartialRecordingTimeMillis + " ms");
      break;
      Log.e(this.TAG, "onVideoCapturePaused() - Fail to pause");
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.CAPTURING);
    }
  }
  
  private void onVideoCaptureResumed(CaptureHandleImpl paramCaptureHandleImpl, boolean paramBoolean)
  {
    if (this.m_VideoCaptureHandle != paramCaptureHandleImpl)
    {
      Log.w(this.TAG, "onVideoCaptureResumed() - Unknown handle : " + paramCaptureHandleImpl + ", expected handle : " + this.m_VideoCaptureHandle);
      return;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "onVideoCaptureResumed() - Current capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return;
    }
    long l1;
    if (paramBoolean)
    {
      Log.v(this.TAG, "onVideoCaptureResumed()");
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.CAPTURING);
      if (this.m_RecordingTimeRatioHandles.isEmpty())
      {
        l1 = 1000L;
        long l2 = SystemClock.elapsedRealtime();
        l1 = Math.max(0L, l1 - this.m_ElapsedPartialRecordingTimeMillis);
        this.m_TotalPausedVideoRecordingTime += l2 - this.m_VideoRecordingPausedTime;
        Log.d(this.TAG, "onVideoCaptureResumed() - Update elapsed recording time " + l1 + " ms later");
        HandlerUtils.sendMessage(this, -40, 0, 0, new Object[] { Long.valueOf(l2), get(PROP_ELAPSED_RECORDING_SECONDS) }, l1);
      }
    }
    for (;;)
    {
      this.m_VideoCaptureCUDHandle = Handle.close(this.m_VideoCaptureCUDHandle);
      return;
      l1 = (1000.0F / ((RecordingTimeRatioHandle)this.m_RecordingTimeRatioHandles.getLast()).ratio);
      break;
      Log.e(this.TAG, "onVideoCaptureResumed() - Fail to resume");
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.PAUSED);
    }
  }
  
  private void onVideoCaptureStarted(CaptureHandleImpl paramCaptureHandleImpl, CaptureHandle paramCaptureHandle)
  {
    if (this.m_VideoCaptureHandle != paramCaptureHandleImpl)
    {
      Log.e(this.TAG, "onVideoCaptureStarted() - Unknown handle : " + paramCaptureHandleImpl + ", expected handle : " + this.m_VideoCaptureHandle);
      return;
    }
    Log.v(this.TAG, "onVideoCaptureStarted() - Handle : ", paramCaptureHandleImpl);
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "onVideoCaptureStarted() - Video capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return;
    case 8: 
      paramCaptureHandleImpl.internalCaptureHandle = paramCaptureHandle;
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.CAPTURING);
      raise(EVENT_CAPTURE_STARTED, new CaptureEventArgs(paramCaptureHandleImpl, paramCaptureHandleImpl.getCaptureTrigger()));
      resetPhotoCaptureState();
      if (((Boolean)get(PROP_IS_CAMERA_PREVIEW_RECEIVED)).booleanValue()) {
        this.m_VideoCaptureCUDHandle = Handle.close(this.m_VideoCaptureCUDHandle);
      }
      return;
    }
    Log.w(this.TAG, "onVideoCaptureStarted() - Stop capture immediately");
    paramCaptureHandleImpl.internalCaptureHandle = paramCaptureHandle;
    Handle.close(paramCaptureHandle, 2);
    this.m_VideoCaptureCUDHandle = Handle.close(this.m_VideoCaptureCUDHandle);
  }
  
  private void quickCapturePhoto()
  {
    if (!this.m_IsQuickCaptureTriggered)
    {
      this.m_IsQuickCaptureTriggered = true;
      switch (-getcom-oneplus-camera-StartModeSwitchesValues()[this.m_StartMode.ordinal()])
      {
      }
    }
    do
    {
      return;
    } while (!((Settings)get(PROP_SETTINGS)).getBoolean("IsQuickCaptureEnabled", false));
    if (get(PROP_MEDIA_TYPE) == MediaType.PHOTO)
    {
      if (!((Boolean)get(PROP_IS_CAMERA_PREVIEW_RECEIVED)).booleanValue())
      {
        Log.w(this.TAG, "quickCapturePhoto() - Ignore waiting for first camera preview frame for quick capture");
        onCameraPreviewReceived();
      }
      Log.w(this.TAG, "quickCapturePhoto() - Perform quick capture");
      Handle localHandle = disableSelfTimer();
      capturePhoto();
      enableSelfTimer(localHandle);
      return;
    }
    Log.e(this.TAG, "quickCapturePhoto() - Fail to perform quick capture because current media type is " + get(PROP_MEDIA_TYPE));
  }
  
  private void registerReceivers()
  {
    if (this.m_ScreenStateReceiver == null) {
      this.m_ScreenStateReceiver = new BroadcastReceiver()
      {
        public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
        {
          paramAnonymousContext = paramAnonymousIntent.getAction();
          if (paramAnonymousContext.equals("android.intent.action.SCREEN_OFF"))
          {
            CameraActivity.this.setReadOnly(CameraActivity.PROP_IS_SCREEN_ON, Boolean.valueOf(false));
            if (CameraActivity.this.isSecureMode())
            {
              if (!CameraActivity.-wrap0(CameraActivity.this)) {
                break label132;
              }
              Log.v(CameraActivity.-get1(CameraActivity.this), "Intent: ", paramAnonymousIntent, ", the device is in an interactive state, ignore it");
            }
          }
          else
          {
            if (!paramAnonymousContext.equals("android.intent.action.SCREEN_ON")) {
              break label90;
            }
            CameraActivity.this.setReadOnly(CameraActivity.PROP_IS_SCREEN_ON, Boolean.valueOf(true));
          }
          label90:
          while ((!paramAnonymousContext.equals("android.intent.action.USER_PRESENT")) || (!CameraActivity.this.isSecureMode())) {
            return;
          }
          Log.v(CameraActivity.-get1(CameraActivity.this), "Intent: ", paramAnonymousIntent, ", finish itself");
          CameraActivity.this.finishAndRemoveTask();
          return;
          label132:
          paramAnonymousContext = CameraActivity.this.getWindow();
          WindowManager.LayoutParams localLayoutParams = paramAnonymousContext.getAttributes();
          localLayoutParams.flags &= 0xFFF7FFFF;
          paramAnonymousContext.setAttributes(localLayoutParams);
          switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)CameraActivity.this.get(CameraActivity.PROP_STATE)).ordinal()])
          {
          default: 
            Log.v(CameraActivity.-get1(CameraActivity.this), "Intent: ", paramAnonymousIntent, ", finish itself");
            CameraActivity.this.getHandler().post(new Runnable()
            {
              public void run()
              {
                CameraActivity.this.finishAndRemoveTask();
              }
            });
            return;
          }
        }
      };
    }
    IntentFilter localIntentFilter = new IntentFilter();
    localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
    localIntentFilter.addAction("android.intent.action.SCREEN_ON");
    localIntentFilter.addAction("android.intent.action.USER_PRESENT");
    localIntentFilter.setPriority(Integer.MAX_VALUE);
    registerReceiver(this.m_ScreenStateReceiver, localIntentFilter);
  }
  
  private void releasePreCaptureFocusLockRequest(Handle paramHandle)
  {
    verifyAccess();
    this.m_PreCaptureFocusLockReqHandles.remove(paramHandle);
  }
  
  private void removeScreenShot()
  {
    removeScreenShot(null);
  }
  
  private void removeScreenShot(Handle paramHandle)
  {
    if ((this.m_TakeScreenShotHandles == null) || (this.m_TakeScreenShotHandles.isEmpty())) {}
    do
    {
      return;
      if ((paramHandle != null) && ((!this.m_TakeScreenShotHandles.remove(paramHandle)) || (!this.m_TakeScreenShotHandles.isEmpty()))) {
        break;
      }
    } while ((this.m_ScreenShotImageView == null) || (this.m_ScreenShotImageView.getParent() == null));
    Log.v(this.TAG, "removeScreenShot()");
    this.m_ScreenShotBitmap = null;
    this.m_ScreenShotImageView.setImageBitmap(null);
    ((ViewGroup)this.m_ScreenShotImageView.getParent()).removeView(this.m_ScreenShotImageView);
    return;
  }
  
  private void resetIdleState()
  {
    HandlerUtils.removeMessages(this, -50);
    boolean bool = ((Boolean)get(PROP_IS_RUNNING)).booleanValue();
    if (this.m_IsIdle)
    {
      Log.w(this.TAG, "resetIdleState()");
      this.m_IsIdle = false;
      if (bool) {
        getWindow().addFlags(128);
      }
      notifyPropertyChanged(PROP_IS_IDLE, Boolean.valueOf(true), Boolean.valueOf(false));
    }
    if (!bool) {
      return;
    }
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    case 2: 
    case 3: 
    default: 
      switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
      {
      case 4: 
      case 5: 
      case 7: 
      default: 
        HandlerUtils.sendMessage(this, -50, true, 120000L);
        return;
      }
      break;
    }
    return;
  }
  
  private void resetPhotoCaptureState()
  {
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[((MediaType)get(PROP_MEDIA_TYPE)).ordinal()])
    {
    default: 
    case 1: 
      CaptureHandleImpl localCaptureHandleImpl;
      do
      {
        do
        {
          return;
          if ((this.m_CameraPreviewState != OperationState.STARTED) || (!((Boolean)get(PROP_IS_CAMERA_PREVIEW_RECEIVED)).booleanValue())) {
            break;
          }
          setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.READY);
          localCaptureHandleImpl = this.m_PendingPhotoCaptureHandle;
          this.m_PendingPhotoCaptureHandle = null;
        } while ((localCaptureHandleImpl == null) || (SystemClock.elapsedRealtime() - localCaptureHandleImpl.creationTime > 800L));
        Log.w(this.TAG, "resetPhotoCaptureState() - Capture next photo immediately");
      } while (!capturePhoto(localCaptureHandleImpl, false, false));
      return;
      setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.PREPARING);
      return;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.PREPARING);
      return;
    }
    setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.READY);
  }
  
  private void resetVideoCaptureState()
  {
    if ((this.m_CameraPreviewState == OperationState.STARTED) && (get(PROP_MEDIA_TYPE) == MediaType.VIDEO) && (((Boolean)get(PROP_IS_CAMERA_PREVIEW_RECEIVED)).booleanValue()))
    {
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.READY);
      return;
    }
    setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.PREPARING);
  }
  
  private void restoreRecordingTimeRatio(RecordingTimeRatioHandle paramRecordingTimeRatioHandle)
  {
    verifyAccess();
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    case 6: 
    case 8: 
    default: 
      throw new RuntimeException("Cannot restore recording time ratio when capture state is " + get(PROP_VIDEO_CAPTURE_STATE) + ".");
    }
    if (!this.m_RecordingTimeRatioHandles.remove(paramRecordingTimeRatioHandle)) {
      return;
    }
    Log.v(this.TAG, "restoreRecordingTimeRatio() - Ratio : " + paramRecordingTimeRatioHandle.ratio + ", handle : " + paramRecordingTimeRatioHandle);
  }
  
  private void restoreSettings(SettingsHandle paramSettingsHandle)
  {
    verifyAccess();
    int i = this.m_SettingsHandles.indexOf(paramSettingsHandle);
    if (i < 0)
    {
      Log.w(this.TAG, "restoreSettings() - Invalid handle");
      return;
    }
    Log.w(this.TAG, "restoreSettings() - Handle : " + paramSettingsHandle);
    this.m_SettingsHandles.remove(i);
    if (i == this.m_SettingsHandles.size())
    {
      if (!this.m_SettingsHandles.isEmpty())
      {
        paramSettingsHandle = (SettingsHandle)this.m_SettingsHandles.get(i - 1);
        setReadOnly(PROP_SETTINGS, paramSettingsHandle.settings);
      }
    }
    else {
      return;
    }
    Log.e(this.TAG, "restoreSettings() - All settings are removed");
    setReadOnly(PROP_SETTINGS, null);
  }
  
  private void restoreToDefaultCamera()
  {
    Log.v(this.TAG, "restoreToDefaultCamera()");
    ((Settings)get(PROP_SETTINGS)).set("CameraLensFacing", Camera.LensFacing.BACK);
    switchCamera(Camera.LensFacing.BACK, 36);
  }
  
  private void restoreToDefaultSelfTimerInterval()
  {
    this.m_IsSelfTimerResetNeeded = true;
    if ((get(PROP_MEDIA_TYPE) == MediaType.PHOTO) && (this.m_CountDownTimer != null)) {
      updateSelfTimerInterval();
    }
  }
  
  private void scheduleQuickCapturePhoto(boolean paramBoolean)
  {
    if ((this.m_IsQuickCaptureScheduled) || (this.m_IsQuickCaptureTriggered)) {
      return;
    }
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)get(PROP_STATE)).ordinal()])
    {
    case 2: 
    case 3: 
    case 4: 
    case 6: 
    case 7: 
    default: 
      Log.e(this.TAG, "scheduleQuickCapturePhoto() - Cannot schedule quick-capture, current state : " + get(PROP_STATE));
      return;
    }
    Log.v(this.TAG, "scheduleQuickCapturePhoto()");
    this.m_IsQuickCaptureScheduled = true;
    Handler localHandler = getHandler();
    if (paramBoolean) {}
    for (long l = 300L;; l = 0L)
    {
      localHandler.sendEmptyMessageDelayed(65396, l);
      return;
    }
  }
  
  private boolean setSelfTimerIntervalProp(long paramLong)
  {
    if (paramLong < 0L) {
      throw new IllegalArgumentException("Self timer interval cannot be negative.");
    }
    Object localObject = (Camera)get(PROP_CAMERA);
    if (localObject == null)
    {
      Log.e(this.TAG, "setSelfTimerIntervalProp() - No primary camera");
      super.set(PROP_SELF_TIMER_INTERVAL, Long.valueOf(0L));
      return false;
    }
    if (((Camera)localObject).get(Camera.PROP_LENS_FACING) == Camera.LensFacing.BACK) {}
    for (localObject = "SelfTimer.Back";; localObject = "SelfTimer.Front")
    {
      ((Settings)get(PROP_SETTINGS)).set((String)localObject, Long.valueOf(paramLong));
      return super.set(PROP_SELF_TIMER_INTERVAL, Long.valueOf(paramLong));
    }
  }
  
  private void setupWindowFlags()
  {
    Window localWindow = getWindow();
    WindowManager.LayoutParams localLayoutParams = localWindow.getAttributes();
    if (isSecureMode()) {}
    for (localLayoutParams.flags |= 0x80000;; localLayoutParams.flags &= 0xFFF7FFFF)
    {
      localWindow.setAttributes(localLayoutParams);
      return;
    }
  }
  
  private void startAccelerometer()
  {
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)get(PROP_STATE)).ordinal()])
    {
    default: 
      return;
    }
    if (this.m_SensorManager == null) {
      this.m_SensorManager = ((SensorManager)getSystemService("sensor"));
    }
    Camera localCamera = (Camera)get(PROP_CAMERA);
    if ((localCamera != null) && (localCamera.get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT))
    {
      i = 1;
      if (!this.m_IsAccelerometerStarted) {
        if (i == 0) {
          break label149;
        }
      }
    }
    label149:
    for (int i = 1;; i = 2)
    {
      this.m_SensorManager.registerListener(this.m_AcceleromaterListener, this.m_SensorManager.getDefaultSensor(1), i);
      this.m_IsAccelerometerStarted = true;
      Log.v(this.TAG, "startAccelerometer() - Accelerometer started");
      return;
      i = 0;
      break;
    }
  }
  
  private boolean startCameraPreview(boolean paramBoolean)
  {
    verifyAccess();
    if (paramBoolean) {
      switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[this.m_CameraPreviewState.ordinal()])
      {
      }
    }
    while (!canStartCameraPreview())
    {
      Log.w(this.TAG, "startCameraPreview() - Cannot start preview in current state");
      return false;
      Log.w(this.TAG, "startCameraPreview() - Start while stopping");
      continue;
      return true;
    }
    final Camera localCamera = (Camera)get(PROP_CAMERA);
    if (localCamera == null)
    {
      Log.w(this.TAG, "startCameraPreview() - No camera to start preview");
      return false;
    }
    if (changeCameraPreviewState(OperationState.STARTING) != OperationState.STARTING)
    {
      Log.e(this.TAG, "startCameraPreview() - Process interrupted");
      return false;
    }
    if (get(PROP_MEDIA_TYPE) == MediaType.VIDEO) {
      sendBroadcast(new Intent("com.oneplus.camera.ACTION_CAMERA_START"));
    }
    final int i = this.m_CameraPreviewSessionID + 1;
    this.m_CameraPreviewSessionID = i;
    if (!this.m_IsCameraPreviewReceiverReady)
    {
      Log.w(this.TAG, "startCameraPreview() - Preview receiver is not ready yet, start preview later");
      return true;
    }
    Log.w(this.TAG, "startCameraPreview() - Camera : " + localCamera);
    final Resolution localResolution1;
    final Resolution localResolution2;
    if (this.m_ResolutionManager != null)
    {
      localResolution1 = (Resolution)this.m_ResolutionManager.get(ResolutionManager.PROP_PHOTO_RESOLUTION);
      if (this.m_ResolutionManager == null) {
        break label377;
      }
      localResolution2 = (Resolution)this.m_ResolutionManager.get(ResolutionManager.PROP_VIDEO_RESOLUTION);
      label269:
      if (get(PROP_MEDIA_TYPE) != MediaType.VIDEO) {
        break label383;
      }
    }
    label377:
    label383:
    for (final Resolution localResolution3 = localResolution2;; localResolution3 = localResolution1)
    {
      final Size localSize = (Size)get(PROP_CAMERA_PREVIEW_SIZE);
      final Object localObject = this.m_Viewfinder.get(Viewfinder.PROP_PREVIEW_RECEIVER);
      if (HandlerUtils.post(this.m_CameraThread, new Runnable()
      {
        public void run()
        {
          int i = 0;
          switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)localCamera.get(Camera.PROP_PREVIEW_STATE)).ordinal()])
          {
          default: 
            localCamera.set(Camera.PROP_IS_SERVICE_MODE, Boolean.valueOf(CameraActivity.this.isServiceMode()));
            if (localResolution1 != null)
            {
              localCamera.set(Camera.PROP_PICTURE_SIZE, localResolution1.toSize());
              if (localResolution2 == null) {
                break label322;
              }
              localCamera.set(Camera.PROP_VIDEO_SIZE, localResolution2.toSize());
              label125:
              if (!CameraActivity.-get3(CameraActivity.this).startCameraPreview(localCamera, localSize, localResolution3, localObject, 8)) {
                break label364;
              }
              switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)localCamera.get(Camera.PROP_PREVIEW_STATE)).ordinal()])
              {
              default: 
                Log.e(CameraActivity.-get1(CameraActivity.this), "startCameraPreview() - Fail to start camera preview");
                HandlerUtils.sendMessage(CameraActivity.this, -10, i, 0, localCamera);
              }
            }
            break;
          }
          for (;;)
          {
            if (i != 0)
            {
              Log.v(CameraActivity.-get1(CameraActivity.this), "startCameraPreview() - Wait for camera preview start");
              localCamera.addCallback(Camera.PROP_PREVIEW_STATE, new PropertyChangedCallback()
              {
                public void onPropertyChanged(PropertySource paramAnonymous2PropertySource, PropertyKey<OperationState> paramAnonymous2PropertyKey, PropertyChangeEventArgs<OperationState> paramAnonymous2PropertyChangeEventArgs)
                {
                  if (paramAnonymous2PropertyChangeEventArgs.getNewValue() == OperationState.STARTED)
                  {
                    HandlerUtils.sendMessage(CameraActivity.this, -11, this.val$cameraPreviewSessionID, 0, this.val$camera);
                    this.val$camera.removeCallback(Camera.PROP_PREVIEW_STATE, this);
                    return;
                  }
                  Log.v(CameraActivity.-get1(CameraActivity.this), "startCameraPreview() - Preview state changed from ", paramAnonymous2PropertyChangeEventArgs.getOldValue(), " to ", paramAnonymous2PropertyChangeEventArgs.getNewValue(), ", session ID : ", Integer.valueOf(this.val$cameraPreviewSessionID));
                }
              });
            }
            return;
            i = 1;
            continue;
            HandlerUtils.sendMessage(CameraActivity.this, -11, i, 0, localCamera);
            continue;
            Log.e(CameraActivity.-get1(CameraActivity.this), "startCameraPreview() - No photo resolution to set");
            break;
            label322:
            Log.w(CameraActivity.-get1(CameraActivity.this), "startCameraPreview() - No photo resolution to set");
            break label125;
            HandlerUtils.sendMessage(CameraActivity.this, -11, i, 0, localCamera);
            continue;
            i = 1;
            continue;
            label364:
            Log.e(CameraActivity.-get1(CameraActivity.this), "startCameraPreview() - Fail to start camera preview");
            HandlerUtils.sendMessage(CameraActivity.this, -10, i, 0, localCamera);
          }
        }
      })) {
        break label389;
      }
      Log.e(this.TAG, "startCameraPreview() - Fail to perform cross-thread operation");
      if (this.m_CameraPreviewState == OperationState.STARTING) {
        changeCameraPreviewState(OperationState.STOPPED);
      }
      return false;
      localResolution1 = null;
      break;
      localResolution2 = null;
      break label269;
    }
    label389:
    if (!Handle.isValid(this.m_CameraPreviewStartCUDHandle)) {
      this.m_CameraPreviewStartCUDHandle = disableCaptureUI("CameraPreviewStartStop");
    }
    if (setReadOnly(PROP_IS_LAUNCHING, Boolean.valueOf(false)))
    {
      this.m_ComponentManager.createComponents(ComponentCreationPriority.NORMAL, new Object[] { this });
      bindToNormalComponents();
    }
    startOrientationListener();
    return true;
  }
  
  private void startOrientationListener()
  {
    if (Handle.isValid(this.m_OrientationSensorHandle)) {
      return;
    }
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)get(PROP_STATE)).ordinal()])
    {
    default: 
      return;
    }
    if (!Handle.isValid(this.m_OrientationCallbackHandle))
    {
      if (this.m_OrientationCallback == null) {
        this.m_OrientationCallback = new OrientationManager.Callback()
        {
          public void onOrientationChanged(int paramAnonymousInt)
          {
            CameraActivity.this.onDeviceOrientationChanged(paramAnonymousInt);
            super.onOrientationChanged(paramAnonymousInt);
          }
          
          public void onRotationChanged(Rotation paramAnonymousRotation1, Rotation paramAnonymousRotation2)
          {
            if (CameraActivity.-get6(CameraActivity.this) > 0L)
            {
              CameraActivity localCameraActivity = CameraActivity.this;
              long l = CameraActivity.-get6(CameraActivity.this);
              HandlerUtils.sendMessage(localCameraActivity, -95, 0, 0, new Object[] { paramAnonymousRotation1, paramAnonymousRotation2 }, true, l);
              return;
            }
            CameraActivity.this.onRotationChanged(paramAnonymousRotation1, paramAnonymousRotation2);
          }
          
          public void onSystemOrientationSettingsChanged(boolean paramAnonymousBoolean)
          {
            CameraActivity.-wrap15(CameraActivity.this, paramAnonymousBoolean);
          }
        };
      }
      this.m_OrientationCallbackHandle = OrientationManager.setCallback(this.m_OrientationCallback, getHandler());
    }
    Log.v(this.TAG, "startOrientationListener()");
    this.m_OrientationSensorHandle = OrientationManager.startOrientationSensor(this);
    getHandler().sendEmptyMessageDelayed(-60, 200L);
  }
  
  private void stopAccelerometer()
  {
    if (this.m_SensorManager == null) {
      return;
    }
    if (this.m_IsAccelerometerStarted)
    {
      this.m_SensorManager.unregisterListener(this.m_AcceleromaterListener);
      this.m_IsAccelerometerStarted = false;
      Log.v(this.TAG, "stopAccelerometer() - Accelerometer stopped");
    }
    if (this.m_IsGyroscopeStarted)
    {
      this.m_SensorManager.unregisterListener(this.m_GyroscopeListener);
      this.m_IsGyroscopeStarted = false;
      Log.v(this.TAG, "stopAccelerometer() - Gyroscope stopped");
    }
    onAccelerometerValuesChanged((float[])PROP_ACCELEROMETER_VALUES.defaultValue);
  }
  
  private void stopOrientationListener()
  {
    if (!Handle.isValid(this.m_OrientationSensorHandle)) {
      return;
    }
    Log.v(this.TAG, "stopOrientationListener()");
    this.m_OrientationSensorHandle = Handle.close(this.m_OrientationSensorHandle);
    getHandler().removeMessages(-60);
  }
  
  private void stopPhotoCapture(CaptureHandleImpl paramCaptureHandleImpl)
  {
    verifyAccess();
    if (this.m_PhotoCaptureHandle != paramCaptureHandleImpl)
    {
      Log.w(this.TAG, "stopPhotoCapture() - Invalid handle");
      return;
    }
    Log.w(this.TAG, "stopPhotoCapture() - Handle : " + paramCaptureHandleImpl);
    paramCaptureHandleImpl.close();
    if (this.m_PendingPhotoCaptureHandle == paramCaptureHandleImpl)
    {
      Log.w(this.TAG, "stopPhotoCapture() - Cancel pending capture");
      this.m_PendingPhotoCaptureHandle = null;
      return;
    }
    if (Handle.isValid(this.m_SelfTimerHandle))
    {
      Log.w(this.TAG, "stopPhotoCapture() - Stop self timer");
      setReadOnly(PROP_IS_SELF_TIMER_STARTED, Boolean.valueOf(false));
      this.m_SelfTimerHandle = Handle.close(this.m_SelfTimerHandle);
      HandlerUtils.post(this.m_CameraThread, new Runnable()
      {
        public void run()
        {
          if (Handle.isValid(CameraActivity.-get2(CameraActivity.this))) {
            Handle.close(CameraActivity.-get2(CameraActivity.this));
          }
        }
      });
      onCaptureCompleted(paramCaptureHandleImpl);
      return;
    }
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "stopPhotoCapture() - Photo capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
      return;
    }
    setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.STOPPING);
    HandlerUtils.removeMessages(this, 65406);
    HandlerUtils.removeMessages(this, 65386);
    if (Handle.isValid(paramCaptureHandleImpl.internalCaptureHandle))
    {
      Handle.close(paramCaptureHandleImpl.internalCaptureHandle);
      return;
    }
    Log.w(this.TAG, "stopPhotoCapture() - Stop when starting");
    getCameraThread().stopCurrentPhotoCapture();
  }
  
  private void stopVideoCapture(CaptureHandleImpl paramCaptureHandleImpl, boolean paramBoolean1, boolean paramBoolean2)
  {
    stopVideoCapture(paramCaptureHandleImpl, paramBoolean1, paramBoolean2, CaptureCompleteReason.NORMAL);
  }
  
  private void stopVideoCapture(CaptureHandleImpl paramCaptureHandleImpl, boolean paramBoolean1, boolean paramBoolean2, CaptureCompleteReason paramCaptureCompleteReason)
  {
    verifyAccess();
    if (this.m_VideoCaptureHandle != paramCaptureHandleImpl)
    {
      Log.w(this.TAG, "stopVideoCapture() - Invalid handle");
      return;
    }
    Log.w(this.TAG, "stopVideoCapture() - Handle : " + paramCaptureHandleImpl + ", from camera thread : " + paramBoolean2);
    if (paramBoolean1) {
      Log.w(this.TAG, "stopVideoCapture() - Stop from video snapshot");
    }
    setReadOnly(PROP_VIDEO_CAPTURE_COMPLETE_REASON, paramCaptureCompleteReason);
    paramCaptureHandleImpl.close();
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    case 4: 
    case 5: 
    case 7: 
    default: 
      Log.e(this.TAG, "stopVideoCapture() - Video capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return;
    case 3: 
      Log.w(this.TAG, "stopVideoCapture() - Stop while pausing");
    case 1: 
    case 2: 
    case 9: 
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.STOPPING);
      if (!Handle.isValid(this.m_VideoCaptureCUDHandle)) {
        this.m_VideoCaptureCUDHandle = disableCaptureUI("VideoCaptureStartStop");
      }
      HandlerUtils.removeMessages(this, -40);
      switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
      {
      default: 
        if ((paramBoolean1) || (paramBoolean2)) {
          completePhotoCapture(this.m_PhotoCaptureHandle);
        }
        break;
      }
      break;
    }
    for (;;)
    {
      if (Handle.isValid(paramCaptureHandleImpl.internalCaptureHandle))
      {
        int i = 0;
        if ((paramCaptureHandleImpl.closeFlags & 0x2) != 0) {
          i = 2;
        }
        Handle.close(paramCaptureHandleImpl.internalCaptureHandle, i);
        return;
        Log.w(this.TAG, "stopVideoCapture() - Stop while resuming");
        break;
        Log.w(this.TAG, "stopVideoCapture() - Stop while starting");
        break;
        resetPhotoCaptureState();
        continue;
        Log.w(this.TAG, "stopVideoCapture() - Waiting for video snapshot");
        return;
      }
    }
    Log.w(this.TAG, "stopVideoCapture() - Stop when starting");
  }
  
  private boolean switchCamera(Camera paramCamera)
  {
    return switchCamera((Camera)get(PROP_CAMERA), paramCamera, 0);
  }
  
  private boolean switchCamera(Camera paramCamera, Camera.LensFacing paramLensFacing, int paramInt)
  {
    verifyAccess();
    Camera localCamera = (Camera)get(PROP_CAMERA);
    if (localCamera == null) {
      Log.w(this.TAG, "switchCamera() - No primary camera");
    }
    if ((!this.m_CameraLockHandles.isEmpty()) && (((CameraLockHandle)this.m_CameraLockHandles.getLast()).lensFacing != paramLensFacing))
    {
      Log.e(this.TAG, "switchCamera() - Camera is locked to " + ((CameraLockHandle)this.m_CameraLockHandles.getLast()).lensFacing);
      return false;
    }
    Object localObject = (List)get(PROP_AVAILABLE_CAMERAS);
    if (((List)localObject).isEmpty())
    {
      Log.w(this.TAG, "switchCamera() - Camera list is not ready yet, switch camera later");
      if (this.m_CameraLockHandles.isEmpty()) {
        ((Settings)get(PROP_SETTINGS)).set("CameraLensFacing", paramLensFacing);
      }
      return true;
    }
    localObject = CameraUtils.findCamera((List)localObject, paramLensFacing, false);
    if (localObject == null)
    {
      Log.e(this.TAG, "switchCamera() - No camera with lens facing " + paramLensFacing);
      return false;
    }
    if (localCamera == localObject) {
      return true;
    }
    return switchCamera(paramCamera, (Camera)localObject, paramInt);
  }
  
  private boolean switchCamera(Camera paramCamera1, Camera paramCamera2, int paramInt)
  {
    if (paramCamera1 == paramCamera2)
    {
      Log.v(this.TAG, "switchCamera() - Switch to same camera");
      return true;
    }
    if (!((Boolean)get(PROP_ALL_REQUIRED_PERMISSIONS_GRANTED)).booleanValue())
    {
      Log.e(this.TAG, "switchCamera() - Required permissions not granted");
      return false;
    }
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "switchCamera() - Photo capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
      return false;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "switchCamera() - Video capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return false;
    }
    if (((paramInt & 0x8) == 0) && (((Boolean)get(PROP_IS_CAMERA_SWITCHING)).booleanValue()))
    {
      Log.e(this.TAG, "switchCamera() - Camera is switching");
      return false;
    }
    setReadOnly(PROP_IS_CAMERA_SWITCHING, Boolean.valueOf(true));
    if (!Handle.isValid(this.m_CameraSwitchCUDHandle)) {
      this.m_CameraSwitchCUDHandle = disableCaptureUI("CameraSwitchStartStop");
    }
    if ((paramInt & 0x4) == 0)
    {
      if (this.m_PreviewCover == null) {
        break label561;
      }
      if ((this.m_PreviewCover != null) && (this.m_PreviewCover.getPreviewCoverState(PREVIEW_COVER_STYLE) == PreviewCover.UIState.CLOSING))
      {
        Log.v(this.TAG, "switchCamera() - Preview cover is closing, ignore to switch camera");
        setReadOnly(PROP_IS_CAMERA_SWITCHING, Boolean.valueOf(false));
        this.m_CameraSwitchCUDHandle = Handle.close(this.m_CameraSwitchCUDHandle);
        return false;
      }
      Log.v(this.TAG, "switchCamera() - Show preview cover");
      this.m_PreviewCoverHandle = this.m_PreviewCover.showPreviewCover(PREVIEW_COVER_STYLE, 0);
      if (!Handle.isValid(this.m_PreviewCoverHandle)) {}
    }
    else
    {
      switch (-getcom-oneplus-camera-ui-PreviewCover$UIStateSwitchesValues()[this.m_PreviewCover.getPreviewCoverState(PREVIEW_COVER_STYLE).ordinal()])
      {
      case 3: 
      default: 
        Log.v(this.TAG, "switchCamera() - No need to show preview cover");
        Log.v(this.TAG, "switchCamera()");
        switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[this.m_CameraPreviewState.ordinal()])
        {
        }
        break;
      }
    }
    for (;;)
    {
      stopCameraPreview();
      if (this.m_CameraPreviewState == OperationState.STOPPED) {
        break label577;
      }
      Log.e(this.TAG, "switchCamera() - Preview state is " + this.m_CameraPreviewState);
      setReadOnly(PROP_IS_CAMERA_SWITCHING, Boolean.valueOf(false));
      this.m_CameraSwitchCUDHandle = Handle.close(this.m_CameraSwitchCUDHandle);
      return false;
      this.m_PendingSwitchCamera = paramCamera2;
      return true;
      Log.w(this.TAG, "switchCamera() - Cannot show preview cover");
      break;
      label561:
      Log.w(this.TAG, "switchCamera() - Cannot start switch animation");
      break;
    }
    label577:
    if (paramCamera1 != null) {
      this.m_CameraThread.closeCamera(paramCamera1);
    }
    boolean bool = this.m_CameraThread.openCamera(paramCamera2);
    if (bool)
    {
      setReadOnly(PROP_CAMERA, paramCamera2);
      if (this.m_CameraLockHandles.isEmpty()) {
        ((Settings)get(PROP_SETTINGS)).set("CameraLensFacing", paramCamera2.get(Camera.PROP_LENS_FACING));
      }
      stopAccelerometer();
      if (((paramInt & 0x20) == 0) && (!startCameraPreview())) {
        break label709;
      }
    }
    for (;;)
    {
      setReadOnly(PROP_IS_CAMERA_SWITCHING, Boolean.valueOf(false));
      this.m_CameraSwitchCUDHandle = Handle.close(this.m_CameraSwitchCUDHandle);
      updateSelfTimerInterval();
      return bool;
      Log.e(this.TAG, "switchCamera() - Fail to open camera by camera thread");
      break;
      label709:
      Log.e(this.TAG, "switchCamera() - Fail to restart preview");
    }
  }
  
  private void unlockCamera(CameraLockHandle paramCameraLockHandle)
  {
    int j = 0;
    verifyAccess();
    if ((this.m_CameraLockHandles.remove(paramCameraLockHandle)) && (this.m_CameraLockHandles.isEmpty()))
    {
      Log.w(this.TAG, "unlockCamera()");
      setReadOnly(PROP_IS_CAMERA_LOCKED, Boolean.valueOf(false));
      switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[this.m_CameraPreviewState.ordinal()])
      {
      default: 
        i = 1;
        paramCameraLockHandle = (CameraContext)this.m_CameraContextStack.removeLast();
        if (i == 0) {
          break;
        }
      }
    }
    for (int i = j;; i = 32)
    {
      switchCamera(paramCameraLockHandle.camera, ((CameraContext)this.m_CameraContextStack.getLast()).camera, i | 0x4);
      return;
      i = 0;
      break;
    }
  }
  
  private void unlockRotation(RotationLockHandle paramRotationLockHandle)
  {
    verifyAccess();
    if (!this.m_RotationLockHandles.remove(paramRotationLockHandle)) {
      return;
    }
    if (this.m_RotationLockHandles.isEmpty())
    {
      Log.w(this.TAG, "unlockRotation()");
      paramRotationLockHandle = OrientationManager.getRotation();
      if (paramRotationLockHandle != null) {
        onRotationChanged(this.m_Rotation, paramRotationLockHandle);
      }
    }
  }
  
  private void unregisterReceivers()
  {
    if (this.m_ScreenStateReceiver != null)
    {
      Log.v(this.TAG, "unregisterReceivers()");
      unregisterReceiver(this.m_ScreenStateReceiver);
    }
  }
  
  private void updateBurstEnablingState()
  {
    Camera localCamera;
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)get(PROP_STATE)).ordinal()])
    {
    case 4: 
    case 5: 
    case 8: 
    case 9: 
    case 10: 
    default: 
      localCamera = (Camera)get(PROP_CAMERA);
      if (localCamera == null)
      {
        Log.w(this.TAG, "updateBurstEnablingState() - Camera is null, disable burst");
        setReadOnly(PROP_IS_BURST_PHOTO_CAPTURE_ENABLED, Boolean.valueOf(false));
        return;
      }
      break;
    case 2: 
    case 3: 
    case 6: 
    case 7: 
    case 11: 
      Log.w(this.TAG, "updateBurstEnablingState() - Activity exited, ignore it.");
      return;
    }
    if ((get(PROP_MEDIA_TYPE) == MediaType.PHOTO) && (!isServiceMode()) && (this.m_BurstDisableHandles.isEmpty()) && (((Boolean)localCamera.get(Camera.PROP_IS_BURST_CAPTURE_SUPPORTED)).booleanValue())) {}
    for (boolean bool = true;; bool = false)
    {
      setReadOnly(PROP_IS_BURST_PHOTO_CAPTURE_ENABLED, Boolean.valueOf(bool));
      Log.d(this.TAG, "updateBurstEnablingState() - Burst enabled : ", Boolean.valueOf(bool));
      return;
    }
  }
  
  private void updateElapsedRecordingTime(long paramLong1, long paramLong2)
  {
    long l1 = SystemClock.elapsedRealtime();
    this.m_LastElapsedRecordingTimeCheckTime = l1;
    long l2 = paramLong2 + 1L;
    setReadOnly(PROP_ELAPSED_RECORDING_SECONDS, Long.valueOf(l2));
    paramLong2 = ((Long)get(PROP_MAX_VIDEO_DURATION_SECONDS)).longValue();
    if ((paramLong2 >= 0L) && (l2 >= paramLong2))
    {
      Log.w(this.TAG, "updateElapsedRecordingTime() - Max duration (" + paramLong2 + " sec) reached.");
      return;
    }
    if (this.m_RecordingTimeRatioHandles.isEmpty()) {}
    for (paramLong2 = 1000L;; paramLong2 = (1000.0F / ((RecordingTimeRatioHandle)this.m_RecordingTimeRatioHandles.getLast()).ratio))
    {
      long l3 = this.m_VideoRecordStartTime;
      long l4 = this.m_TotalPausedVideoRecordingTime;
      if (paramLong1 <= 0L) {
        break;
      }
      HandlerUtils.sendMessage(this, -40, 0, 0, new Object[] { Long.valueOf(l1), Long.valueOf(l2) }, (1L + l2) * paramLong2 - (l1 - l3 - l4));
      return;
    }
    HandlerUtils.sendMessage(this, -40, 0, 0, new Object[] { Long.valueOf(l1), Long.valueOf(l2) }, paramLong2);
  }
  
  private void updateScreenShotRotation()
  {
    if ((this.m_ScreenShotImageView == null) || (this.m_ScreenShotBitmap == null)) {
      return;
    }
    Object localObject = getWindowManager();
    if (localObject == null)
    {
      Log.w(this.TAG, "updateScreenShotRotation() - WindowManager is null");
      return;
    }
    localObject = ((WindowManager)localObject).getDefaultDisplay();
    if (localObject == null)
    {
      Log.w(this.TAG, "updateScreenShotRotation() - display is null");
      return;
    }
    int j = ((Display)localObject).getRotation();
    boolean bool = true;
    if (this.m_ScreenShotImageMatrix != null)
    {
      this.m_ScreenShotImageMatrix.reset();
      switch (j)
      {
      default: 
        if (((Boolean)get(PROP_IS_RUNNING)).booleanValue())
        {
          bool = false;
          label130:
          this.m_ScreenShotImageView.setImageMatrix(this.m_ScreenShotImageMatrix);
          localObject = this.m_ScreenShotImageView;
          if (!bool) {
            break label300;
          }
        }
        break;
      }
    }
    label300:
    for (int i = 0;; i = 8)
    {
      ((ImageView)localObject).setVisibility(i);
      Log.v(this.TAG, "updateScreenShotRotation() - Window Manager rotation : ", Integer.valueOf(j), ", visible : ", Boolean.valueOf(bool));
      return;
      this.m_ScreenShotImageMatrix = new Matrix();
      break;
      this.m_ScreenShotImageMatrix.postRotate(90.0F);
      this.m_ScreenShotImageMatrix.postTranslate(this.m_ScreenShotBitmap.getHeight(), 0.0F);
      break label130;
      this.m_ScreenShotImageMatrix.postRotate(-90.0F);
      this.m_ScreenShotImageMatrix.postTranslate(0.0F, this.m_ScreenShotBitmap.getWidth());
      break label130;
      this.m_ScreenShotImageMatrix.postRotate(180.0F);
      this.m_ScreenShotImageMatrix.postTranslate(this.m_ScreenShotBitmap.getWidth(), this.m_ScreenShotBitmap.getHeight());
      break label130;
      bool = true;
      break label130;
    }
  }
  
  private void updateScreenShotRotation(Rotation paramRotation)
  {
    if ((this.m_ScreenShotImageView == null) || (this.m_ScreenShotBitmap == null)) {
      return;
    }
    if (isSameAsWindowManagerRotation(paramRotation))
    {
      updateScreenShotRotation();
      Log.v(this.TAG, "updateScreenShotRotation() - the same with window rotation:");
      return;
    }
    this.m_CheckRotationStartTime = SystemClock.elapsedRealtime();
    HandlerUtils.sendMessage(this, -100, 0, 0, paramRotation, true, 100L);
  }
  
  private void updateScreenSize()
  {
    if (this.m_IgnoreNavigationBar) {}
    for (boolean bool = false;; bool = true)
    {
      ScreenSize localScreenSize = new ScreenSize(this, bool);
      if (setReadOnly(PROP_SCREEN_SIZE, localScreenSize)) {
        Log.w(this.TAG, "updateScreenSize() - Screen size : " + localScreenSize);
      }
      if (this.m_CameraThread != null) {
        this.m_CameraThread.setScreenSize(localScreenSize);
      }
      return;
    }
  }
  
  private void updateSelfTimerInterval()
  {
    if (this.m_CountDownTimer == null) {
      return;
    }
    if (get(PROP_MEDIA_TYPE) != MediaType.PHOTO)
    {
      super.set(PROP_SELF_TIMER_INTERVAL, Long.valueOf(0L));
      return;
    }
    Object localObject = (Camera)get(PROP_CAMERA);
    if (localObject == null)
    {
      Log.e(this.TAG, "updateSelfTimerInterval() - No primary camera");
      super.set(PROP_SELF_TIMER_INTERVAL, Long.valueOf(0L));
      return;
    }
    Settings localSettings = (Settings)get(PROP_SETTINGS);
    if (this.m_IsSelfTimerResetNeeded)
    {
      localSettings.set("SelfTimer.Back", Long.valueOf(0L));
      localSettings.set("SelfTimer.Front", Long.valueOf(0L));
      this.m_IsSelfTimerResetNeeded = false;
    }
    if (((Camera)localObject).get(Camera.PROP_LENS_FACING) == Camera.LensFacing.BACK) {}
    for (localObject = "SelfTimer.Back";; localObject = "SelfTimer.Front")
    {
      long l = Math.max(0L, localSettings.getLong((String)localObject));
      super.set(PROP_SELF_TIMER_INTERVAL, Long.valueOf(l));
      return;
    }
  }
  
  private void updateSelfTimerUsability()
  {
    if ((get(PROP_MEDIA_TYPE) == MediaType.PHOTO) && (this.m_SelfTimerDisableHandles.isEmpty())) {}
    for (boolean bool = true;; bool = false)
    {
      setReadOnly(PROP_IS_SELF_TIMER_USABLE, Boolean.valueOf(bool));
      return;
    }
  }
  
  private void updateStabilityLevel(int paramInt)
  {
    int i = ((Integer)get(PROP_DEVICE_STABILITY_LEVEL)).intValue();
    setReadOnly(PROP_DEVICE_STABILITY_LEVEL, Integer.valueOf(paramInt));
    if ((((Boolean)get(PROP_IS_WAITING_FOR_STABLE_TO_CAPTURE)).booleanValue()) && (paramInt >= 100) && (i >= 100))
    {
      Log.w(this.TAG, "updateStabilityLevel() - Capture photo for stable capture, stability level : " + paramInt);
      setReadOnly(PROP_IS_WAITING_FOR_STABLE_TO_CAPTURE, Boolean.valueOf(false));
      capturePhoto(this.m_PhotoCaptureHandle, false, true);
    }
  }
  
  private void updateSystemUiVisibility()
  {
    if (this.m_IgnoreNavigationBar) {
      return;
    }
    getHandler().removeMessages(-80);
    View localView = (View)get(PROP_CONTENT_VIEW);
    if (localView == null) {
      return;
    }
    localView.setSystemUiVisibility(localView.getSystemUiVisibility() | 0x800 | 0x200 | 0x2 | 0x400);
  }
  
  public final void addComponentBuilders(ComponentBuilder[] paramArrayOfComponentBuilder)
  {
    verifyAccess();
    if (this.m_ComponentManager != null)
    {
      this.m_ComponentManager.addComponentBuilders(paramArrayOfComponentBuilder, new Object[] { this });
      return;
    }
    this.m_InitialComponentBuilders.addAll(Arrays.asList(paramArrayOfComponentBuilder));
  }
  
  protected boolean canStartCameraPreview()
  {
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)get(PROP_STATE)).ordinal()])
    {
    default: 
      return false;
    }
    if (this.m_ActivityRotation.isLandscape())
    {
      if (((Integer)get(PROP_CONFIG_ORIENTATION)).intValue() != 2) {
        return false;
      }
    }
    else if (((Integer)get(PROP_CONFIG_ORIENTATION)).intValue() != 1) {
      return false;
    }
    if (!((Boolean)get(PROP_ALL_REQUIRED_PERMISSIONS_GRANTED)).booleanValue())
    {
      Log.w(this.TAG, "canStartCameraPreview() - Required permissions not granted");
      return false;
    }
    return true;
  }
  
  public boolean canVideoSnapshot()
  {
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[((MediaType)get(PROP_MEDIA_TYPE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "canVideoSnapshot() - Unknown media type : " + get(PROP_MEDIA_TYPE));
      return false;
    case 1: 
      return false;
    }
    if (!((Boolean)get(PROP_IS_VIDEO_SNAPSHOT_ENABLED)).booleanValue()) {
      return false;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      return false;
    }
    return true;
  }
  
  public final CaptureHandle capturePhoto()
  {
    return capturePhoto(1, 0);
  }
  
  public final CaptureHandle capturePhoto(int paramInt)
  {
    return capturePhoto(paramInt, 0);
  }
  
  public CaptureHandle capturePhoto(int paramInt1, int paramInt2)
  {
    if (paramInt1 == 0)
    {
      Log.e(this.TAG, "capturePhoto() - Invalid frame count");
      return null;
    }
    verifyAccess();
    if (this.m_CameraThread == null)
    {
      Log.e(this.TAG, "capturePhoto() - No camera thread");
      return null;
    }
    if (this.m_CaptureModeManager == null)
    {
      Log.e(this.TAG, "capturePhoto() - No capture mode manager");
      return null;
    }
    Object localObject = (CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE);
    cancelQuickCaptures();
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    default: 
    case 4: 
    case 1: 
    case 5: 
    case 2: 
      do
      {
        Log.e(this.TAG, "capturePhoto() - Capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
        return null;
        if (Handle.isValid(this.m_SelfTimerHandle))
        {
          Log.w(this.TAG, "capturePhoto() - Counting-down self timer");
          return null;
        }
        if (((Boolean)get(PROP_IS_FAST_SHOT_TO_SHOT_ENABLED)).booleanValue())
        {
          if (this.m_PendingPhotoCaptureHandle == null) {
            this.m_PendingPhotoCaptureHandle = new CaptureHandleImpl((Camera)get(PROP_CAMERA), (CaptureMode)localObject, (Rotation)get(PROP_ROTATION), paramInt1, paramInt2);
          }
          for (;;)
          {
            if (get(PROP_MEDIA_TYPE) == MediaType.VIDEO) {
              this.m_PendingPhotoCaptureHandle.isVideoSnapshot = true;
            }
            Log.w(this.TAG, "capturePhoto() - Start capture after current capture completes, pending handle : " + this.m_PendingPhotoCaptureHandle);
            return this.m_PendingPhotoCaptureHandle;
            this.m_PendingPhotoCaptureHandle.updateCreationTime();
          }
        }
        Log.w(this.TAG, "capturePhoto() - Fast shot-to-shot is disabled");
        return null;
      } while (get(PROP_MEDIA_TYPE) != MediaType.PHOTO);
      if (this.m_PendingPhotoCaptureHandle == null) {
        this.m_PendingPhotoCaptureHandle = new CaptureHandleImpl((Camera)get(PROP_CAMERA), (CaptureMode)localObject, (Rotation)get(PROP_ROTATION), paramInt1, paramInt2);
      }
      for (;;)
      {
        Log.w(this.TAG, "capturePhoto() - Start capture after capture state ready, pending handle : " + this.m_PendingPhotoCaptureHandle);
        return this.m_PendingPhotoCaptureHandle;
        this.m_PendingPhotoCaptureHandle.updateCreationTime();
      }
    }
    if (get(PROP_STATE) != BaseActivity.State.RUNNING)
    {
      Log.e(this.TAG, "capturePhoto() - Activity state is " + get(PROP_STATE));
      return null;
    }
    int i = paramInt1;
    if (paramInt1 != 1)
    {
      if (((Boolean)get(PROP_IS_BURST_PHOTO_CAPTURE_ENABLED)).booleanValue()) {
        i = paramInt1;
      }
    }
    else
    {
      localObject = new CaptureHandleImpl((Camera)get(PROP_CAMERA), (CaptureMode)localObject, (Rotation)get(PROP_ROTATION), i, paramInt2);
      if (get(PROP_MEDIA_TYPE) == MediaType.VIDEO) {
        ((CaptureHandleImpl)localObject).isVideoSnapshot = true;
      }
      if ((i == 1) && (!this.m_CaptureDelayTimeHandles.isEmpty())) {
        break label628;
      }
    }
    for (;;)
    {
      if (capturePhoto((CaptureHandleImpl)localObject, false, false)) {
        break label649;
      }
      Log.e(this.TAG, "capturePhoto() - Fail to capture");
      this.m_PhotoRotationLockHandle = Handle.close(this.m_PhotoRotationLockHandle);
      setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.READY);
      return null;
      Log.w(this.TAG, "capturePhoto() - Burst capture is disabled");
      i = 1;
      break;
      label628:
      ((CaptureHandleImpl)localObject).delayTimeMillis = ((CaptureDelayTimeHandle)this.m_CaptureDelayTimeHandles.getLast()).delayTimeMillis;
    }
    label649:
    return this.m_PhotoCaptureHandle;
  }
  
  public final CaptureHandle captureVideo()
  {
    return captureVideo(0);
  }
  
  public CaptureHandle captureVideo(int paramInt)
  {
    verifyAccess();
    if (get(PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.READY)
    {
      Log.e(this.TAG, "captureVideo() - Video capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return null;
    }
    if (this.m_CaptureModeManager == null)
    {
      Log.e(this.TAG, "captureVideo() - No capture mode manager");
      return null;
    }
    Object localObject = (CaptureMode)this.m_CaptureModeManager.get(CaptureModeManager.PROP_CAPTURE_MODE);
    cancelQuickCaptures();
    localObject = new CaptureHandleImpl((Camera)get(PROP_CAMERA), (CaptureMode)localObject, (Rotation)get(PROP_ROTATION), paramInt);
    if (!captureVideo((CaptureHandleImpl)localObject)) {
      return null;
    }
    return (CaptureHandle)localObject;
  }
  
  public void completeCapture(CaptureHandle paramCaptureHandle)
  {
    if (paramCaptureHandle == null)
    {
      Log.e(this.TAG, "completeCapture() - No handle");
      return;
    }
    if (!(paramCaptureHandle instanceof CaptureHandleImpl))
    {
      Log.e(this.TAG, "completeCapture() - Invalid handle");
      return;
    }
    verifyAccess();
    paramCaptureHandle = (CaptureHandleImpl)paramCaptureHandle;
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[paramCaptureHandle.getMediaType().ordinal()])
    {
    default: 
      return;
    case 1: 
      completePhotoCapture(paramCaptureHandle);
      return;
    }
    completeVideoCapture(paramCaptureHandle);
  }
  
  public Handle disableBurstPhotoCapture()
  {
    verifyAccess();
    Handle local15 = new Handle("BurstDisable")
    {
      protected void onClose(int paramAnonymousInt)
      {
        CameraActivity.-wrap4(CameraActivity.this, this);
      }
    };
    this.m_BurstDisableHandles.add(local15);
    if (this.m_BurstDisableHandles.size() == 1) {
      setReadOnly(PROP_IS_BURST_PHOTO_CAPTURE_ENABLED, Boolean.valueOf(false));
    }
    return local15;
  }
  
  public Handle disableCaptureUI(String paramString)
  {
    return disableCaptureUI(paramString, 0);
  }
  
  public Handle disableCaptureUI(String paramString, int paramInt)
  {
    verifyAccess();
    if ((paramInt & 0x1) != 0) {
      switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)get(PROP_STATE)).ordinal()])
      {
      case 2: 
      case 3: 
      case 6: 
      case 7: 
      default: 
        Log.w(this.TAG, "disableCaptureUI() - Activity state is " + get(PROP_STATE));
        return null;
      }
    }
    paramString = new UIDisableHandle(paramString, paramInt);
    this.m_CaptureUIDisableHandles.add(paramString);
    Log.w(this.TAG, "disableCaptureUI() - Handle : " + paramString + ", handle count : " + this.m_CaptureUIDisableHandles.size());
    if (this.m_CaptureUIDisableHandles.size() == 1) {
      setReadOnly(PROP_IS_CAPTURE_UI_ENABLED, Boolean.valueOf(false));
    }
    return paramString;
  }
  
  public Handle disableFastShotToShot()
  {
    verifyAccess();
    Handle local16 = new Handle("FastShotToShotDisable")
    {
      protected void onClose(int paramAnonymousInt)
      {
        CameraActivity.-wrap6(CameraActivity.this, this);
      }
    };
    this.m_FastShotToShotDisableHandles.add(local16);
    if ((this.m_FastShotToShotDisableHandles.size() == 1) && (setReadOnly(PROP_IS_FAST_SHOT_TO_SHOT_ENABLED, Boolean.valueOf(false))) && (this.m_PendingPhotoCaptureHandle != null))
    {
      this.m_PendingPhotoCaptureHandle.close();
      this.m_PendingPhotoCaptureHandle = null;
    }
    return local16;
  }
  
  public Handle disableSelfTimer()
  {
    verifyAccess();
    Handle local17 = new Handle("SelfTimerDisable")
    {
      protected void onClose(int paramAnonymousInt)
      {
        CameraActivity.-wrap7(CameraActivity.this, this);
      }
    };
    this.m_SelfTimerDisableHandles.add(local17);
    if (this.m_SelfTimerDisableHandles.size() == 1)
    {
      setReadOnly(PROP_IS_SELF_TIMER_USABLE, Boolean.valueOf(false));
      if ((Handle.isValid(this.m_SelfTimerHandle)) && (Handle.isValid(this.m_PhotoCaptureHandle)))
      {
        Log.w(this.TAG, "disableSelfTimer() - Stop self-timer");
        stopPhotoCapture(this.m_PhotoCaptureHandle);
      }
    }
    return local17;
  }
  
  public boolean dispatchTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if (i == 0)
    {
      Log.v(this.TAG, "dispatchTouchEvent() - Action=", Integer.valueOf(i), ", x=", Float.valueOf(paramMotionEvent.getX()), ", y=", Float.valueOf(paramMotionEvent.getY()));
      setReadOnly(PROP_IS_TOUCHING_ON_SCREEN, Boolean.valueOf(true));
      if ((!this.m_CancelTouchEvents) && (SystemClock.elapsedRealtime() - this.m_LastNavBarVisibleTime < 300L))
      {
        Log.w(this.TAG, "dispatchTouchEvent() - Cancel touch event after showing navigation bar");
        this.m_CancelTouchEvents = true;
      }
    }
    resetIdleState();
    try
    {
      if (!this.m_CancelTouchEvents) {
        bool = super.dispatchTouchEvent(paramMotionEvent);
      }
      for (;;)
      {
        if ((i == 1) || (i == 3))
        {
          Log.v(this.TAG, "dispatchTouchEvent() - Action=", Integer.valueOf(i), ", x=", Float.valueOf(paramMotionEvent.getX()), ", y=", Float.valueOf(paramMotionEvent.getY()));
          this.m_CancelTouchEvents = false;
          setReadOnly(PROP_IS_TOUCHING_ON_SCREEN, Boolean.valueOf(false));
          checkTouchDigits(paramMotionEvent.getX(), paramMotionEvent.getY());
        }
        return bool;
        MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
        localMotionEvent.setAction(3);
        bool = super.dispatchTouchEvent(localMotionEvent);
        localMotionEvent.recycle();
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "dispatchTouchEvent() - Unhandled error", localThrowable);
        boolean bool = false;
      }
    }
  }
  
  public Handle enterSimpleUIMode()
  {
    verifyAccess();
    Handle local18 = new Handle("SimpleUIMode")
    {
      protected void onClose(int paramAnonymousInt)
      {
        CameraActivity.-wrap8(CameraActivity.this, this);
      }
    };
    this.m_SimpleUIModeHandles.add(local18);
    Log.v(this.TAG, "enterSimpleUIMode() - Handle count : ", Integer.valueOf(this.m_SimpleUIModeHandles.size()));
    if (this.m_SimpleUIModeHandles.size() == 1) {
      setReadOnly(PROP_IS_SIMPLE_UI_MODE_ENTERED, Boolean.valueOf(true));
    }
    return local18;
  }
  
  public <TComponent extends Component> TComponent findComponent(Class<TComponent> paramClass)
  {
    if (this.m_ComponentManager != null) {
      return this.m_ComponentManager.findComponent(paramClass, new Object[] { this });
    }
    return null;
  }
  
  public <TComponent extends Component> TComponent[] findComponents(Class<TComponent> paramClass)
  {
    if (this.m_ComponentManager != null) {
      return this.m_ComponentManager.findComponents(paramClass, new Object[] { this });
    }
    return new Component[0];
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_ACTIVITY_ROTATION) {
      return this.m_ActivityRotation;
    }
    if (paramPropertyKey == PROP_CAMERA)
    {
      if (!this.m_CameraContextStack.isEmpty()) {
        return ((CameraContext)this.m_CameraContextStack.getLast()).camera;
      }
      return null;
    }
    if (paramPropertyKey == PROP_CAMERA_PREVIEW_STATE) {
      return this.m_CameraPreviewState;
    }
    if (paramPropertyKey == PROP_IS_IDLE) {
      return Boolean.valueOf(this.m_IsIdle);
    }
    if (paramPropertyKey == PROP_IS_QUICK_CAPTURE_SCHEDULED) {
      return Boolean.valueOf(this.m_IsQuickCaptureScheduled);
    }
    if (paramPropertyKey == PROP_IS_ROTATION_READY) {
      return Boolean.valueOf(this.m_IsRotationReady);
    }
    if (paramPropertyKey == PROP_ROTATION) {
      return this.m_Rotation;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  public final CameraThread getCameraThread()
  {
    return this.m_CameraThread;
  }
  
  protected final ComponentManager getComponentManager()
  {
    return this.m_ComponentManager;
  }
  
  public final String getInstanceId()
  {
    return this.m_InstanceId;
  }
  
  public final MediaResultInfo getMediaResultInfo()
  {
    return this.m_MediaResultInfo;
  }
  
  protected void getRequestPermissions(List<String> paramList)
  {
    super.getRequestPermissions(paramList);
    paramList.addAll(REQUIRED_PERMISSION_LIST);
    paramList.addAll(this.m_OptionalPermissions);
  }
  
  public final ResolutionManager getResolutionManager()
  {
    if (this.m_ResolutionManager == null)
    {
      this.m_ResolutionManager = ((ResolutionManager)this.m_ComponentManager.findComponent(ResolutionManager.class, new Object[] { this }));
      if (this.m_ResolutionManager == null) {
        break label135;
      }
      PropertyChangedCallback local19 = new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey paramAnonymousPropertyKey, PropertyChangeEventArgs paramAnonymousPropertyChangeEventArgs)
        {
          paramAnonymousPropertySource = CameraActivity.this;
          if (paramAnonymousPropertyKey == ResolutionManager.PROP_VIDEO_RESOLUTION) {}
          for (boolean bool = true;; bool = false)
          {
            paramAnonymousPropertySource.selectCameraPreviewSize(bool);
            return;
          }
        }
      };
      this.m_ResolutionManager.addCallback(ResolutionManager.PROP_PHOTO_PREVIEW_SIZE, local19);
      this.m_ResolutionManager.addCallback(ResolutionManager.PROP_VIDEO_RESOLUTION, local19);
      this.m_ResolutionManager.addCallback(ResolutionManager.PROP_VIDEO_PREVIEW_SIZE, local19);
      this.m_ResolutionManager.addCallback(ResolutionManager.PROP_MAX_VIDEO_DURATION_SECONDS, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Long> paramAnonymousPropertyKey, PropertyChangeEventArgs<Long> paramAnonymousPropertyChangeEventArgs)
        {
          CameraActivity.this.setReadOnly(CameraActivity.PROP_MAX_VIDEO_DURATION_SECONDS, (Long)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
      setReadOnly(PROP_MAX_VIDEO_DURATION_SECONDS, (Long)this.m_ResolutionManager.get(ResolutionManager.PROP_MAX_VIDEO_DURATION_SECONDS));
    }
    for (;;)
    {
      return this.m_ResolutionManager;
      label135:
      Log.e(this.TAG, "getResolutionManager() - No ResolutionManager");
    }
  }
  
  public StartMode getStartMode()
  {
    return this.m_StartMode;
  }
  
  public final Viewfinder getViewfinder()
  {
    if (this.m_Viewfinder == null)
    {
      this.m_Viewfinder = ((Viewfinder)this.m_ComponentManager.findComponent(Viewfinder.class, new Object[] { this }));
      if (this.m_Viewfinder == null) {
        break label64;
      }
      this.m_Viewfinder.addCallback(Viewfinder.PROP_PREVIEW_RECEIVER, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Object> paramAnonymousPropertyKey, PropertyChangeEventArgs<Object> paramAnonymousPropertyChangeEventArgs)
        {
          paramAnonymousPropertySource = paramAnonymousPropertyChangeEventArgs.getNewValue();
          if (paramAnonymousPropertySource != null)
          {
            CameraActivity.this.onCameraPreviewReceiverReady(paramAnonymousPropertySource);
            return;
          }
          CameraActivity.this.onCameraPreviewReceiverDestroyed(true);
        }
      });
    }
    for (;;)
    {
      return this.m_Viewfinder;
      label64:
      Log.e(this.TAG, "bindToComponents() - No Viewfinder");
    }
  }
  
  protected void handleMessage(Message paramMessage)
  {
    boolean bool1 = true;
    boolean bool3 = true;
    boolean bool2 = true;
    CaptureHandleImpl localCaptureHandleImpl;
    switch (paramMessage.what)
    {
    default: 
    case -10: 
    case -11: 
    case -1: 
    case -2: 
    case -150: 
    case -110: 
    case -70: 
    case -50: 
    case -20: 
    case -21: 
    case -140: 
    case -60: 
    case -130: 
      do
      {
        do
        {
          do
          {
            return;
            onCameraPreviewStartFailed((Camera)paramMessage.obj, paramMessage.arg1);
            return;
            onCameraPreviewStarted((Camera)paramMessage.obj, paramMessage.arg1);
            return;
            paramMessage = (Object[])paramMessage.obj;
            onCameraThreadEventReceived((EventKey)paramMessage[0], (EventArgs)paramMessage[1]);
            return;
            paramMessage = (Object[])paramMessage.obj;
            onCameraThreadPropertyChanged((PropertyKey)paramMessage[0], (PropertyChangeEventArgs)paramMessage[1]);
            return;
            localCaptureHandleImpl = (CaptureHandleImpl)paramMessage.obj;
            if (paramMessage.arg1 != 0)
            {
              bool1 = true;
              if (paramMessage.arg2 == 0) {
                break label327;
              }
            }
            for (;;)
            {
              capturePhoto(localCaptureHandleImpl, bool1, bool2);
              return;
              bool1 = false;
              break;
              bool2 = false;
            }
            Log.v(this.TAG, "handleMessage() - Close all cameras");
          } while (this.m_CameraThread == null);
          this.m_CameraThread.closeCameras();
          return;
          this.m_CameraPreviewFrameCUDHandle = Handle.close(this.m_CameraPreviewFrameCUDHandle);
          return;
          idle();
          return;
          paramMessage = (Object[])paramMessage.obj;
          onPhotoCaptureFailed((CaptureHandleImpl)paramMessage[0], (CaptureCompleteReason)paramMessage[1]);
          return;
          paramMessage = (Object[])paramMessage.obj;
          onPhotoCaptureStarted((CaptureHandleImpl)paramMessage[0], (CaptureHandle)paramMessage[1]);
          return;
          this.m_IsQuickCaptureScheduled = false;
          quickCapturePhoto();
          return;
        } while (this.m_IsRotationReady);
        paramMessage = OrientationManager.getRotation();
        if (paramMessage != null) {
          onRotationChanged(this.m_Rotation, paramMessage);
        }
        this.m_IsRotationReady = true;
        notifyPropertyChanged(PROP_IS_ROTATION_READY, Boolean.valueOf(false), Boolean.valueOf(true));
        return;
      } while (!((Boolean)get(PROP_IS_WAITING_FOR_STABLE_TO_CAPTURE)).booleanValue());
      Log.w(this.TAG, "handleMessage() - Capture photo for stable capture");
      setReadOnly(PROP_IS_WAITING_FOR_STABLE_TO_CAPTURE, Boolean.valueOf(false));
      capturePhoto(this.m_PhotoCaptureHandle, false, true);
      return;
    case -95: 
      Log.d(this.TAG, "MSG_UPDATE_DELAYED_ROTATION");
      paramMessage = (Object[])paramMessage.obj;
      onRotationChanged((Rotation)paramMessage[0], (Rotation)paramMessage[1]);
      return;
    case -40: 
      paramMessage = (Object[])paramMessage.obj;
      updateElapsedRecordingTime(((Long)paramMessage[0]).longValue(), ((Long)paramMessage[1]).longValue());
      return;
    case -90: 
      updateScreenShotRotation((Rotation)paramMessage.obj);
      return;
    case -100: 
      checkScreenShotRotation((Rotation)paramMessage.obj);
      return;
    case -80: 
      updateSystemUiVisibility();
      return;
    case -30: 
      paramMessage = (Object[])paramMessage.obj;
      onVideoCaptureFailed((CaptureHandleImpl)paramMessage[0], (CaptureCompleteReason)paramMessage[1]);
      return;
    case -32: 
      localCaptureHandleImpl = (CaptureHandleImpl)paramMessage.obj;
      if (paramMessage.arg1 != 0) {}
      for (;;)
      {
        onVideoCapturePaused(localCaptureHandleImpl, bool1);
        return;
        bool1 = false;
      }
    case -33: 
      localCaptureHandleImpl = (CaptureHandleImpl)paramMessage.obj;
      if (paramMessage.arg1 != 0) {}
      for (bool1 = bool3;; bool1 = false)
      {
        onVideoCaptureResumed(localCaptureHandleImpl, bool1);
        return;
      }
    case -31: 
      label327:
      paramMessage = (Object[])paramMessage.obj;
      onVideoCaptureStarted((CaptureHandleImpl)paramMessage[0], (CaptureHandle)paramMessage[1]);
      return;
    }
    Log.v(this.TAG, "MSG_FINISH_BY_SELF");
    finish();
  }
  
  public void hideReviewScreen()
  {
    Handle localHandle = this.m_ReviewScreenHandle;
    int i;
    if (get(PROP_STATE) == BaseActivity.State.RUNNING)
    {
      i = 1;
      this.m_ReviewScreenHandle = Handle.close(localHandle, i);
      if (get(PROP_PHOTO_CAPTURE_STATE) != PhotoCaptureState.REVIEWING) {
        break label56;
      }
      onCaptureCompleted(this.m_PhotoCaptureHandle);
    }
    label56:
    while (get(PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.REVIEWING)
    {
      return;
      i = 0;
      break;
    }
    onCaptureCompleted(this.m_VideoCaptureHandle);
  }
  
  protected final void ignoreNavigationBar()
  {
    this.m_IgnoreNavigationBar = true;
  }
  
  public boolean isBusinessCardMode()
  {
    return this.m_StartMode == StartMode.BUSINESS_CARD;
  }
  
  public boolean isPhotoServiceMode()
  {
    return this.m_StartMode == StartMode.SERVICE_PHOTO;
  }
  
  public boolean isSecureMode()
  {
    switch (-getcom-oneplus-camera-StartModeSwitchesValues()[this.m_StartMode.ordinal()])
    {
    default: 
      return false;
    }
    return true;
  }
  
  public boolean isServiceMode()
  {
    switch (-getcom-oneplus-camera-StartModeSwitchesValues()[this.m_StartMode.ordinal()])
    {
    default: 
      return false;
    }
    return true;
  }
  
  public boolean isVideoServiceMode()
  {
    return this.m_StartMode == StartMode.SERVICE_VIDEO;
  }
  
  public Handle lockCamera(Camera.LensFacing paramLensFacing)
  {
    return lockCamera(paramLensFacing, 0);
  }
  
  public Handle lockCamera(Camera.LensFacing paramLensFacing, int paramInt)
  {
    verifyAccess();
    if (get(PROP_STATE) == BaseActivity.State.DESTROYED)
    {
      Log.e(this.TAG, "lockCamera() - Activity state is DESTROYED");
      return null;
    }
    if (paramLensFacing == null)
    {
      Log.e(this.TAG, "lockCamera() - No lens facing specified");
      return null;
    }
    if ((!this.m_CameraLockHandles.isEmpty()) && (((CameraLockHandle)this.m_CameraLockHandles.getLast()).lensFacing != paramLensFacing))
    {
      Log.e(this.TAG, "lockCamera() - Camera is locked to " + ((CameraLockHandle)this.m_CameraLockHandles.getLast()).lensFacing);
      return null;
    }
    CameraLockHandle localCameraLockHandle = new CameraLockHandle(paramLensFacing);
    this.m_CameraLockHandles.addLast(localCameraLockHandle);
    Log.w(this.TAG, "lockCamera() - Lens facing : " + paramLensFacing + ", handle : " + localCameraLockHandle);
    if (this.m_CameraLockHandles.size() == 1)
    {
      Camera localCamera = (Camera)get(PROP_CAMERA);
      CameraContext localCameraContext = new CameraContext(localCamera, true);
      this.m_CameraContextStack.addLast(localCameraContext);
      if (!switchCamera(localCamera, paramLensFacing, paramInt | 0x4))
      {
        Log.e(this.TAG, "lockCamera() - Fail to switch camera");
        this.m_CameraContextStack.removeLast();
        this.m_CameraLockHandles.clear();
        return null;
      }
      setReadOnly(PROP_IS_CAMERA_LOCKED, Boolean.valueOf(true));
    }
    return localCameraLockHandle;
  }
  
  public Handle lockRotation(Rotation paramRotation)
  {
    verifyAccess();
    Rotation localRotation1;
    if (paramRotation == null) {
      localRotation1 = this.m_Rotation;
    }
    do
    {
      do
      {
        paramRotation = new RotationLockHandle(localRotation1);
        this.m_RotationLockHandles.addLast(paramRotation);
        if (this.m_RotationLockHandles.size() == 1)
        {
          Log.w(this.TAG, "lockRotation() - Rotation : " + localRotation1);
          Rotation localRotation2 = this.m_Rotation;
          if (localRotation2 != localRotation1)
          {
            this.m_Rotation = localRotation1;
            notifyCameraThreadRotationChanged(localRotation2, localRotation1);
            notifyUIRotationChanged(localRotation2, localRotation1);
            notifyPropertyChanged(PROP_ROTATION, localRotation2, localRotation1);
            if ((!this.m_IsRotationReady) && (((Boolean)get(PROP_IS_RUNNING)).booleanValue()))
            {
              this.m_IsRotationReady = true;
              notifyPropertyChanged(PROP_IS_ROTATION_READY, Boolean.valueOf(false), Boolean.valueOf(true));
            }
          }
        }
        return paramRotation;
        localRotation1 = paramRotation;
      } while (this.m_RotationLockHandles.isEmpty());
      localRotation1 = paramRotation;
    } while (((RotationLockHandle)this.m_RotationLockHandles.getLast()).rotation == paramRotation);
    Log.e(this.TAG, "lockRotation() - Rotation is already locked in " + this.m_Rotation);
    return null;
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    ActivityResultHandle localActivityResultHandle = (ActivityResultHandle)this.m_ActivityResultHandles.get(paramInt1);
    if (localActivityResultHandle != null)
    {
      this.m_ActivityResultHandles.delete(paramInt1);
      if ((Handle.isValid(localActivityResultHandle)) && (localActivityResultHandle.callback != null)) {
        localActivityResultHandle.callback.onActivityResult(localActivityResultHandle, paramInt2, paramIntent);
      }
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
  }
  
  protected void onAvailableCamerasChanged(List<Camera> paramList)
  {
    switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)get(PROP_STATE)).ordinal()])
    {
    default: 
      localObject1 = (Camera)get(PROP_CAMERA);
      if (localObject1 != null) {
        break;
      }
    }
    int j;
    for (int i = 1;; i = 0)
    {
      j = i;
      if (localObject1 != null)
      {
        j = i;
        if (!paramList.contains(localObject1))
        {
          Log.w(this.TAG, "onAvailableCamerasChanged() - Camera " + localObject1 + " is not contained in new list");
          j = 1;
        }
      }
      setReadOnly(PROP_AVAILABLE_CAMERAS, paramList);
      if (((Boolean)get(PROP_ALL_REQUIRED_PERMISSIONS_GRANTED)).booleanValue()) {
        break;
      }
      Log.w(this.TAG, "onAvailableCamerasChanged() - Required permissions not granted yet");
      return;
      Log.w(this.TAG, "onAvailableCamerasChanged() - Already destroyed, skip");
      return;
    }
    if ((paramList == null) || (paramList.isEmpty()))
    {
      Log.e(this.TAG, "onAvailableCamerasChanged() - Empty camera list");
      onCameraOpenFailedError(new CameraOpenFailedEventArgs(null, 0));
      return;
    }
    if (j == 0) {
      return;
    }
    Object localObject1 = (Settings)get(PROP_SETTINGS);
    if (!((Boolean)get(PROP_KEEP_LAST_CAPTURE_SETTINGS)).booleanValue()) {
      ((Settings)localObject1).set("CameraLensFacing", Camera.LensFacing.BACK);
    }
    Object localObject2;
    if (isStartedInSelfieMode())
    {
      ((Settings)localObject1).set("CameraLensFacing", Camera.LensFacing.FRONT);
      Camera.LensFacing localLensFacing = (Camera.LensFacing)((Settings)localObject1).getEnum("CameraLensFacing", Camera.LensFacing.class);
      localObject2 = CameraUtils.findCamera(paramList, localLensFacing, false);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        Log.e(this.TAG, "onAvailableCamerasChanged() - No camera with lens facing " + localLensFacing + ", select another camera");
        if (localLensFacing != Camera.LensFacing.BACK) {
          break label661;
        }
        localObject1 = Camera.LensFacing.FRONT;
        label347:
        localObject1 = CameraUtils.findCamera(paramList, (Camera.LensFacing)localObject1, false);
      }
      Log.w(this.TAG, "onAvailableCamerasChanged() - Default camera : " + localObject1);
      localObject2 = localObject1;
      if (!this.m_CameraLockHandles.isEmpty())
      {
        paramList = CameraUtils.findCamera(paramList, ((CameraLockHandle)this.m_CameraLockHandles.getLast()).lensFacing, false);
        Log.w(this.TAG, "onAvailableCamerasChanged() - Locked camera : " + paramList);
        localObject2 = localObject1;
        if (paramList != null)
        {
          ((CameraContext)this.m_CameraContextStack.getFirst()).camera = ((Camera)localObject1);
          localObject2 = paramList;
        }
      }
      if (localObject2 == null) {
        Log.e(this.TAG, "onAvailableCamerasChanged() - No camera to use");
      }
      setReadOnly(PROP_CAMERA, localObject2);
      switch (-getcom-oneplus-base-BaseActivity$StateSwitchesValues()[((BaseActivity.State)get(PROP_STATE)).ordinal()])
      {
      default: 
        i = 0;
        label550:
        if ((i != 0) && (localObject2 != null) && (!getCameraThread().openCamera((Camera)localObject2))) {
          break;
        }
      }
    }
    for (;;)
    {
      if (!this.m_IsHighComponentsCreated)
      {
        this.m_IsHighComponentsCreated = true;
        this.m_ComponentManager.createComponents(ComponentCreationPriority.HIGH, new Object[] { this });
        if (!bindToInitialComponents()) {
          finish();
        }
      }
      if ((i != 0) && (this.m_CameraPreviewState == OperationState.STOPPED) && (localObject2 != null))
      {
        Log.w(this.TAG, "onAvailableCamerasChanged() - Start preview");
        startCameraPreview();
      }
      return;
      ((Settings)localObject1).set("CameraLensFacing", Camera.LensFacing.BACK);
      break;
      label661:
      localObject1 = Camera.LensFacing.BACK;
      break label347;
      i = 1;
      break label550;
      Log.e(this.TAG, "onAvailableCamerasChanged() - Fail to open camera " + localObject2);
    }
  }
  
  protected void onBatteryLevelChanged(int paramInt)
  {
    setReadOnly(PROP_BATTERY_LEVEL, Integer.valueOf(paramInt));
  }
  
  protected void onBindToCameraThread()
  {
    if (this.m_CameraThread != null) {
      this.m_CameraThread.notifyRequiredPermissionsState(((Boolean)get(PROP_ALL_REQUIRED_PERMISSIONS_GRANTED)).booleanValue());
    }
    final List localList = (List)this.m_CameraThread.get(CameraThread.PROP_AVAILABLE_CAMERAS);
    getHandler().postAtFrontOfQueue(new Runnable()
    {
      public void run()
      {
        CameraActivity.this.onAvailableCamerasChanged(localList);
      }
    });
  }
  
  protected void onBindingToCameraThreadEvents(List<EventKey<?>> paramList)
  {
    paramList.add(CameraThread.EVENT_BURST_PHOTO_RECEIVED);
    paramList.add(CameraThread.EVENT_CAMERA_ERROR);
    paramList.add(CameraThread.EVENT_CAMERA_OPEN_FAILED);
    paramList.add(CameraThread.EVENT_DEFAULT_PHOTO_CAPTURE_COMPLETED);
    paramList.add(CameraThread.EVENT_DEFAULT_VIDEO_CAPTURE_COMPLETED);
    paramList.add(CameraThread.EVENT_MEDIA_FILE_SAVED);
    paramList.add(CameraThread.EVENT_MEDIA_SAVE_CANCELLED);
    paramList.add(CameraThread.EVENT_MEDIA_SAVE_FAILED);
    paramList.add(CameraThread.EVENT_MEDIA_SAVED);
    paramList.add(CameraThread.EVENT_POSTVIEW_RECEIVED);
    paramList.add(CameraThread.EVENT_SHUTTER);
    paramList.add(CameraThread.EVENT_UNPROCESSED_PHOTO_RECEIVED);
  }
  
  protected void onBindingToCameraThreadProperties(List<PropertyKey<?>> paramList)
  {
    paramList.add(CameraThread.PROP_AVAILABLE_CAMERAS);
    paramList.add(CameraThread.PROP_IS_CAMERA_PREVIEW_RECEIVED);
    paramList.add(CameraThread.PROP_IS_CAPTURING_RAW_PHOTO);
    paramList.add(CameraThread.PROP_IS_VIDEO_SNAPSHOT_ENABLED);
    paramList.add(CameraThread.PROP_PHOTO_CAPTURE_STATE);
    paramList.add(CameraThread.PROP_REMAINING_PHOTO_COUNT);
    paramList.add(CameraThread.PROP_REMAINING_VIDEO_DURATION_SECONDS);
    paramList.add(CameraThread.PROP_VIDEO_CAPTURE_STATE);
  }
  
  protected void onCameraError(Camera paramCamera)
  {
    Log.e(this.TAG, "onCameraError() - Camera : " + paramCamera);
    if (get(PROP_CAMERA) == paramCamera) {
      finish();
    }
  }
  
  protected void onCameraOpenFailedError(CameraOpenFailedEventArgs paramCameraOpenFailedEventArgs)
  {
    int i = paramCameraOpenFailedEventArgs.getErrorCode();
    Log.e(this.TAG, "onCameraOpenFailed() - Error code : " + i);
    if ((get(PROP_CAMERA) != paramCameraOpenFailedEventArgs.getCamera()) || (((Boolean)get(PROP_IS_DEBUG_MODE)).booleanValue())) {
      return;
    }
    finish();
  }
  
  protected void onCameraPreviewReceiverDestroyed(boolean paramBoolean)
  {
    this.m_IsCameraPreviewReceiverReady = false;
    stopCameraPreview(paramBoolean);
  }
  
  protected void onCameraPreviewReceiverReady(Object paramObject)
  {
    paramObject = this.m_CameraPreviewState;
    if (this.m_IsCameraPreviewReceiverReady)
    {
      Log.w(this.TAG, "onCameraPreviewReceiverReady() - Stop preview first");
      onCameraPreviewReceiverDestroyed(true);
    }
    this.m_IsCameraPreviewReceiverReady = true;
    if (paramObject == OperationState.STARTING)
    {
      Log.w(this.TAG, "onCameraPreviewReceiverReady() - Continue starting preview");
      if (!startCameraPreview(false))
      {
        Log.e(this.TAG, "onCameraPreviewReceiverReady() - Fail to start preview");
        this.m_CameraPreviewState = OperationState.STOPPED;
        notifyPropertyChanged(PROP_CAMERA_PREVIEW_STATE, OperationState.STARTING, OperationState.STOPPED);
      }
    }
  }
  
  protected void onCameraPreviewStartFailed()
  {
    Log.e(this.TAG, "onCameraPreviewStartFailed()");
    changeCameraPreviewState(OperationState.STOPPED);
  }
  
  protected void onCameraPreviewStarted()
  {
    if (changeCameraPreviewState(OperationState.STARTED) != OperationState.STARTED)
    {
      Log.e(this.TAG, "onCameraPreviewStarted() - Process interrupted");
      return;
    }
    Log.w(this.TAG, "onCameraPreviewStarted()");
    startAccelerometer();
    this.m_CameraThread.checkRemainingMediaCount();
    if (((Boolean)get(PROP_IS_CAMERA_PREVIEW_RECEIVED)).booleanValue()) {
      this.m_CameraPreviewStartCUDHandle = Handle.close(this.m_CameraPreviewStartCUDHandle);
    }
    if (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.PREPARING) {
      resetVideoCaptureState();
    }
    if (get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.PREPARING) {
      resetPhotoCaptureState();
    }
    if (this.m_LaunchSource == LaunchSource.POWER_KEY) {
      scheduleQuickCapturePhoto(true);
    }
  }
  
  protected void onCameraThreadEventReceived(EventKey<?> paramEventKey, EventArgs paramEventArgs)
  {
    if (paramEventKey == CameraThread.EVENT_BURST_PHOTO_RECEIVED) {
      onBurstPhotoReceived((CaptureEventArgs)paramEventArgs);
    }
    for (;;)
    {
      if ((paramEventArgs instanceof RecyclableObject)) {
        ((RecyclableObject)paramEventArgs).recycle();
      }
      return;
      if (paramEventKey == CameraThread.EVENT_CAMERA_ERROR) {
        onCameraError(((CameraEventArgs)paramEventArgs).getCamera());
      } else if (paramEventKey == CameraThread.EVENT_CAMERA_OPEN_FAILED) {
        onCameraOpenFailedError((CameraOpenFailedEventArgs)paramEventArgs);
      } else if (paramEventKey == CameraThread.EVENT_DEFAULT_PHOTO_CAPTURE_COMPLETED) {
        onDefaultPhotoCaptureCompleted((CaptureEventArgs)paramEventArgs);
      } else if (paramEventKey == CameraThread.EVENT_DEFAULT_VIDEO_CAPTURE_COMPLETED) {
        onDefaultVideoCaptureCompleted((CaptureEventArgs)paramEventArgs);
      } else if (paramEventKey == CameraThread.EVENT_MEDIA_FILE_SAVED) {
        onMediaFileSaved((MediaEventArgs)paramEventArgs);
      } else if (paramEventKey == CameraThread.EVENT_MEDIA_SAVE_CANCELLED) {
        onMediaSaveCancelled((MediaEventArgs)paramEventArgs);
      } else if (paramEventKey == CameraThread.EVENT_MEDIA_SAVE_FAILED) {
        onMediaSaveFailed((MediaEventArgs)paramEventArgs);
      } else if (paramEventKey == CameraThread.EVENT_MEDIA_SAVED) {
        onMediaSaved((MediaEventArgs)paramEventArgs);
      } else if (paramEventKey == CameraThread.EVENT_POSTVIEW_RECEIVED) {
        onPostviewReceived((CameraCaptureEventArgs)paramEventArgs);
      } else if (paramEventKey == CameraThread.EVENT_SHUTTER) {
        onShutter((CaptureEventArgs)paramEventArgs);
      } else if (paramEventKey == CameraThread.EVENT_UNPROCESSED_PHOTO_RECEIVED) {
        onUnprocessedPictureReceived((UnprocessedPictureEventArgs)paramEventArgs);
      }
    }
  }
  
  protected void onCameraThreadPropertyChanged(PropertyKey<?> paramPropertyKey, PropertyChangeEventArgs<?> paramPropertyChangeEventArgs)
  {
    if (paramPropertyKey == CameraThread.PROP_AVAILABLE_CAMERAS) {
      onAvailableCamerasChanged((List)paramPropertyChangeEventArgs.getNewValue());
    }
    for (;;)
    {
      paramPropertyChangeEventArgs.recycle();
      return;
      if (paramPropertyKey == CameraThread.PROP_IS_CAMERA_PREVIEW_RECEIVED)
      {
        if (((Boolean)paramPropertyChangeEventArgs.getNewValue()).booleanValue())
        {
          onCameraPreviewReceived();
        }
        else
        {
          if (!Handle.isValid(this.m_CameraPreviewFrameCUDHandle)) {
            this.m_CameraPreviewFrameCUDHandle = disableCaptureUI("CameraPreviewFrameWaiting", 1);
          }
          getHandler().removeMessages(-70);
          setReadOnly(PROP_IS_CAMERA_PREVIEW_RECEIVED, Boolean.valueOf(false));
        }
      }
      else if (paramPropertyKey == CameraThread.PROP_IS_CAPTURING_RAW_PHOTO) {
        setReadOnly(PROP_IS_CAPTURING_RAW_PHOTO, (Boolean)paramPropertyChangeEventArgs.getNewValue());
      } else if (paramPropertyKey == CameraThread.PROP_IS_VIDEO_SNAPSHOT_ENABLED) {
        setReadOnly(PROP_IS_VIDEO_SNAPSHOT_ENABLED, (Boolean)paramPropertyChangeEventArgs.getNewValue());
      } else if (paramPropertyKey == CameraThread.PROP_PHOTO_CAPTURE_STATE) {
        onCameraThreadCaptureStateChanged((PhotoCaptureState)paramPropertyChangeEventArgs.getOldValue(), (PhotoCaptureState)paramPropertyChangeEventArgs.getNewValue());
      } else if (paramPropertyKey == CameraThread.PROP_REMAINING_PHOTO_COUNT) {
        setReadOnly(PROP_REMAINING_PHOTO_COUNT, (Long)paramPropertyChangeEventArgs.getNewValue());
      } else if (paramPropertyKey == CameraThread.PROP_REMAINING_VIDEO_DURATION_SECONDS) {
        setReadOnly(PROP_REMAINING_VIDEO_DURATION_SECONDS, (Long)paramPropertyChangeEventArgs.getNewValue());
      } else if (paramPropertyKey == CameraThread.PROP_VIDEO_CAPTURE_STATE) {
        onCameraThreadCaptureStateChanged((VideoCaptureState)paramPropertyChangeEventArgs.getOldValue(), (VideoCaptureState)paramPropertyChangeEventArgs.getNewValue());
      }
    }
  }
  
  protected void onCameraThreadStarted()
  {
    final ArrayList localArrayList1 = new ArrayList();
    final ArrayList localArrayList2 = new ArrayList();
    final MediaType localMediaType = (MediaType)get(PROP_MEDIA_TYPE);
    onBindingToCameraThreadEvents(localArrayList1);
    onBindingToCameraThreadProperties(localArrayList2);
    Handler localHandler = this.m_CameraThread.getHandler();
    if (localHandler == null)
    {
      Log.e(this.TAG, "onCameraThreadStarted() - No camera thread handler");
      finish();
      return;
    }
    if (!localHandler.postAtFrontOfQueue(new Runnable()
    {
      public void run()
      {
        CameraActivity.-wrap1(CameraActivity.this, localMediaType, localArrayList1, localArrayList2);
      }
    }))
    {
      Log.e(this.TAG, "onCameraThreadStarted() - Fail to start binding to camera thread");
      finish();
      return;
    }
  }
  
  protected void onCaptureCompleted(CaptureHandle paramCaptureHandle, CaptureCompleteReason paramCaptureCompleteReason) {}
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    int i = 1;
    Object localObject = (BaseActivity.State)get(PROP_STATE);
    if ((localObject == BaseActivity.State.RESUMING) || (localObject == BaseActivity.State.RUNNING)) {
      removeScreenShot();
    }
    super.onConfigurationChanged(paramConfiguration);
    updateScreenSize();
    getHandler().removeMessages(-90);
    localObject = Rotation.PORTRAIT;
    switch (getWindowManager().getDefaultDisplay().getRotation())
    {
    default: 
      updateScreenShotRotation((Rotation)localObject);
      Log.v(this.TAG, "onConfigurationChanged() - Orientation changed to ", Integer.valueOf(paramConfiguration.orientation));
      if (paramConfiguration.orientation != 1) {
        break;
      }
    }
    for (;;)
    {
      if (i == this.m_ActivityRotation.isPortrait())
      {
        Log.w(this.TAG, "onConfigurationChanged() - Try starting preview");
        startCameraPreview();
      }
      return;
      localObject = Rotation.PORTRAIT;
      break;
      localObject = Rotation.LANDSCAPE;
      break;
      localObject = Rotation.INVERSE_PORTRAIT;
      break;
      localObject = Rotation.INVERSE_LANDSCAPE;
      break;
      i = 0;
    }
  }
  
  protected void onContentViewSet(View paramView)
  {
    super.onContentViewSet(paramView);
    if (!this.m_IgnoreNavigationBar)
    {
      if (paramView == null) {
        break label33;
      }
      paramView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
      {
        public void onSystemUiVisibilityChange(int paramAnonymousInt)
        {
          if (!((Boolean)CameraActivity.this.get(CameraActivity.PROP_IS_RUNNING)).booleanValue()) {
            return;
          }
          if ((paramAnonymousInt & 0x2) == 0)
          {
            if (((Boolean)CameraActivity.this.get(CameraActivity.PROP_IS_TOUCHING_ON_SCREEN)).booleanValue()) {
              CameraActivity.-set1(CameraActivity.this, true);
            }
            CameraActivity.-set2(CameraActivity.this, SystemClock.elapsedRealtime());
            CameraActivity.this.setReadOnly(CameraActivity.PROP_IS_NAVIGATION_BAR_VISIBLE, Boolean.valueOf(true));
          }
          for (;;)
          {
            CameraActivity.this.getHandler().removeMessages(-80);
            CameraActivity.this.getHandler().sendEmptyMessageDelayed(-80, 2000L);
            return;
            CameraActivity.this.setReadOnly(CameraActivity.PROP_IS_NAVIGATION_BAR_VISIBLE, Boolean.valueOf(false));
          }
        }
      });
    }
    for (;;)
    {
      updateSystemUiVisibility();
      return;
      label33:
      setReadOnly(PROP_IS_NAVIGATION_BAR_VISIBLE, Boolean.valueOf(false));
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    this.m_ThreadMonitorHandle = ThreadMonitor.startMonitorCurrentThread();
    this.m_InstanceId = Integer.toString(hashCode());
    Log.v(this.TAG, "onCreate() - Instance ID : ", this.m_InstanceId);
    super.onCreate(paramBundle);
    if (paramBundle != null) {
      this.m_IsQuickCaptureTriggered = paramBundle.getBoolean("CameraActivity.IsQuickCaptureTriggered", false);
    }
    checkRequiredPermissions();
    checkStartMode();
    setupWindowFlags();
    enablePropertyLogs(PROP_ALL_REQUIRED_PERMISSIONS_GRANTED, 1);
    enablePropertyLogs(PROP_BATTERY_LEVEL, 1);
    enablePropertyLogs(PROP_CAMERA_PREVIEW_SIZE, 1);
    enablePropertyLogs(PROP_CAMERA_PREVIEW_STATE, 1);
    enablePropertyLogs(PROP_ELAPSED_RECORDING_SECONDS, 1);
    enablePropertyLogs(PROP_IS_CAMERA_PREVIEW_RECEIVED, 1);
    enablePropertyLogs(PROP_IS_CAPTURE_UI_ENABLED, 1);
    enablePropertyLogs(PROP_IS_DEBUG_MODE, 1);
    enablePropertyLogs(PROP_IS_NAVIGATION_BAR_VISIBLE, 1);
    enablePropertyLogs(PROP_IS_READY_TO_CAPTURE, 1);
    enablePropertyLogs(PROP_IS_ROTATION_READY, 1);
    enablePropertyLogs(PROP_IS_SCREEN_ON, 1);
    enablePropertyLogs(PROP_KEEP_LAST_CAPTURE_SETTINGS, 1);
    enablePropertyLogs(PROP_PHOTO_CAPTURE_STATE, 1);
    enablePropertyLogs(PROP_ROTATION, 1);
    enablePropertyLogs(PROP_SETTINGS, 1);
    enablePropertyLogs(PROP_VIDEO_CAPTURE_STATE, 1);
    if (!((Boolean)get(PROP_KEEP_LAST_CAPTURE_SETTINGS)).booleanValue()) {
      restoreToDefaultSelfTimerInterval();
    }
    addCallback(PROP_PHOTO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<PhotoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<PhotoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        CameraActivity.-wrap3(CameraActivity.this);
      }
    });
    addCallback(PROP_VIDEO_CAPTURE_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<VideoCaptureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<VideoCaptureState> paramAnonymousPropertyChangeEventArgs)
      {
        CameraActivity.-wrap3(CameraActivity.this);
      }
    });
    onRequestedOrientationChanged(getRequestedOrientation());
    paramBundle = new Settings(this, null, false);
    this.m_SettingsHandles.add(new SettingsHandle(paramBundle));
    setReadOnly(PROP_SETTINGS, paramBundle);
    this.m_CameraThread = new CameraThread(this, new BaseThread.ThreadStartCallback()
    {
      public void onThreadStarted(BaseThread paramAnonymousBaseThread)
      {
        CameraActivity.this.setReadOnly(CameraActivity.PROP_IS_CAMERA_THREAD_STARTED, Boolean.valueOf(true));
        CameraActivity.this.onCameraThreadStarted();
      }
    }, getHandler());
    updateScreenSize();
    this.m_CameraPreviewStartCUDHandle = disableCaptureUI("CameraPreviewStartStop");
    this.m_ComponentManager = new ComponentManager();
    this.m_ComponentManager.addComponentBuilders(DEFAULT_COMPONENT_BUILDERS, new Object[] { this });
    this.m_ComponentManager.addHandler(ComponentManager.EVENT_COMPONENT_ADDED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
      {
        CameraActivity.-wrap16(CameraActivity.this, CameraActivity.EVENT_COMPONENT_ADDED, paramAnonymousComponentEventArgs);
      }
    });
    this.m_ComponentManager.addHandler(ComponentManager.EVENT_COMPONENT_REMOVED, new EventHandler()
    {
      public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
      {
        CameraActivity.-wrap16(CameraActivity.this, CameraActivity.EVENT_COMPONENT_REMOVED, paramAnonymousComponentEventArgs);
      }
    });
    if (!this.m_InitialComponentBuilders.isEmpty())
    {
      ComponentBuilder[] arrayOfComponentBuilder = new ComponentBuilder[this.m_InitialComponentBuilders.size()];
      this.m_InitialComponentBuilders.toArray(arrayOfComponentBuilder);
      this.m_InitialComponentBuilders.clear();
      this.m_ComponentManager.addComponentBuilders(arrayOfComponentBuilder, new Object[] { this });
    }
    this.m_ComponentManager.createComponents(ComponentCreationPriority.LAUNCH, new Object[] { this });
    if ((!paramBundle.getBoolean("Location.Save", true)) || (isSecureMode())) {
      return;
    }
    paramBundle.set("Location.Save", Boolean.valueOf(true));
    this.m_OptionalPermissions.add("android.permission.ACCESS_FINE_LOCATION");
    this.m_PermissionManager = ((PermissionManager)BaseApplication.current().findComponent(PermissionManager.class));
    if (this.m_PermissionManager != null)
    {
      this.m_PermissionManager.addHandler(PermissionManager.EVENT_PERMISSION_GRANTED, this.m_PermissionGrantedEventHandler);
      this.m_PermissionManager.addHandler(PermissionManager.EVENT_PERMISSION_DENIED, this.m_PermissionDeniedEventHandler);
      return;
    }
    Log.w(this.TAG, "onCreate() - No permission manager");
  }
  
  protected void onDestroy()
  {
    if ((this.m_DisableDebugModeWhenExiting) && (((Boolean)get(PROP_IS_DEBUG_MODE)).booleanValue()))
    {
      Log.w(this.TAG, "onDestroy() - Disable debug mode");
      setDebugMode(false);
    }
    setReadOnly(PROP_STATE, BaseActivity.State.DESTROYING);
    unregisterReceivers();
    if (this.m_CameraThread != null) {
      this.m_CameraThread.release();
    }
    try
    {
      this.m_CameraThread.join();
      this.m_ComponentManager.release();
      this.m_OrientationCallbackHandle = Handle.close(this.m_OrientationCallbackHandle);
      if (this.m_StorageManager != null) {
        this.m_StorageManager.removeCallback(StorageManager.PROP_STORAGE_LIST, this.m_StorageManagerCallBack);
      }
      super.onDestroy();
      this.m_ThreadMonitorHandle = Handle.close(this.m_ThreadMonitorHandle);
      if (this.m_PermissionManager != null)
      {
        this.m_PermissionManager.removeHandler(PermissionManager.EVENT_PERMISSION_GRANTED, this.m_PermissionGrantedEventHandler);
        this.m_PermissionManager.removeHandler(PermissionManager.EVENT_PERMISSION_DENIED, this.m_PermissionDeniedEventHandler);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "onDestroy() - Fail to join camera thread", localThrowable);
      }
    }
  }
  
  protected void onDeviceOrientationChanged(int paramInt)
  {
    if (paramInt == -1)
    {
      Log.w(this.TAG, "onDeviceOrientationChanged() - Unknown orientation");
      return;
    }
    setReadOnly(PROP_DEVICE_ORIENTATION, Integer.valueOf(paramInt));
  }
  
  protected boolean onInitialPermissionsRequestCompleted(String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    super.onInitialPermissionsRequestCompleted(paramArrayOfString, paramArrayOfInt);
    boolean bool2 = true;
    int i = paramArrayOfString.length - 1;
    boolean bool1 = bool2;
    if (i >= 0)
    {
      if (paramArrayOfString[i].equals("android.permission.ACCESS_FINE_LOCATION")) {}
      while (paramArrayOfInt[i] != -1)
      {
        i -= 1;
        break;
      }
      bool1 = false;
    }
    setReadOnly(PROP_ALL_REQUIRED_PERMISSIONS_GRANTED, Boolean.valueOf(bool1));
    if (this.m_CameraThread != null) {
      this.m_CameraThread.notifyRequiredPermissionsState(bool1);
    }
    if (!bool1)
    {
      Log.w(this.TAG, "onInitialPermissionsRequestCompleted() - Some permissions are not granted");
      finish();
      return false;
    }
    return bool1;
  }
  
  public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent)
  {
    this.m_KeyDownEvents.add(Integer.valueOf(paramInt));
    resetIdleState();
    int i = this.m_KeyEventHandles.size() - 1;
    while (i >= 0)
    {
      KeyEventHandler localKeyEventHandler = ((KeyEventHandle)this.m_KeyEventHandles.get(i)).handler;
      KeyEventHandler.KeyResult localKeyResult = localKeyEventHandler.onKeyDown(paramInt, new KeyEventArgs(paramKeyEvent));
      switch (-getcom-oneplus-camera-KeyEventHandler$KeyResultSwitchesValues()[localKeyResult.ordinal()])
      {
      default: 
        i -= 1;
        break;
      case 1: 
        Log.v(this.TAG, "onKeyDown() - Key ", Integer.valueOf(paramKeyEvent.getKeyCode()), " is handled by ", localKeyEventHandler);
        return true;
      case 2: 
        Log.v(this.TAG, "onKeyDown() - Pass to system directly: ", localKeyEventHandler);
        return false;
      }
    }
    if (!((Boolean)get(PROP_IS_CAPTURE_UI_ENABLED)).booleanValue())
    {
      Log.w(this.TAG, "onKeyDown() - Capture UI is disabled");
      return false;
    }
    return super.onKeyDown(paramInt, paramKeyEvent);
  }
  
  public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent)
  {
    KeyEventArgs localKeyEventArgs = new KeyEventArgs(paramKeyEvent);
    int i;
    if ((this.m_KeyDownEvents.remove(Integer.valueOf(paramInt))) || (localKeyEventArgs.isExternal())) {
      i = this.m_KeyEventHandles.size() - 1;
    }
    while (i >= 0)
    {
      KeyEventHandler localKeyEventHandler = ((KeyEventHandle)this.m_KeyEventHandles.get(i)).handler;
      KeyEventHandler.KeyResult localKeyResult = localKeyEventHandler.onKeyUp(paramInt, localKeyEventArgs);
      switch (-getcom-oneplus-camera-KeyEventHandler$KeyResultSwitchesValues()[localKeyResult.ordinal()])
      {
      default: 
        i -= 1;
        continue;
        Log.w(this.TAG, "onKeyUp() - Not a pair of key down and key up event, keyCode is " + paramInt);
        return false;
      case 1: 
        Log.v(this.TAG, "onKeyUp() - Key ", Integer.valueOf(paramKeyEvent.getKeyCode()), " is handled by ", localKeyEventHandler);
        return true;
      case 2: 
        Log.v(this.TAG, "onKeyUp() - Pass to system directly: ", localKeyEventHandler);
        return false;
      }
    }
    if (!((Boolean)get(PROP_IS_CAPTURE_UI_ENABLED)).booleanValue())
    {
      Log.w(this.TAG, "onKeyUp() - Capture UI is disabled");
      return false;
    }
    if (paramKeyEvent.getKeyCode() == 4)
    {
      switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
      {
      case 2: 
      default: 
        Log.w(this.TAG, "onKeyUp() - Back pressed, leave camera");
        if ((!isServiceMode()) && ((!this.m_DisableDebugModeWhenExiting) || (!((Boolean)get(PROP_IS_DEBUG_MODE)).booleanValue())) && (moveTaskToBack(false))) {
          return true;
        }
        break;
      case 1: 
      case 3: 
        if (Handle.isValid(this.m_VideoCaptureHandle))
        {
          Log.w(this.TAG, "onKeyUp() - Back pressed, stop video capture");
          stopVideoCapture(this.m_VideoCaptureHandle, false, false);
        }
        return true;
      }
      finish();
      return true;
    }
    return super.onKeyUp(paramInt, paramKeyEvent);
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    checkRequiredPermissions();
    super.onNewIntent(paramIntent);
    setIntent(paramIntent);
    setReadOnly(PROP_IS_SECURE_MODE, Boolean.valueOf(false));
    setReadOnly(PROP_KEEP_LAST_CAPTURE_SETTINGS, Boolean.valueOf(true));
    this.m_LaunchSource = LaunchSource.NORMAL;
    boolean bool = isSecureMode();
    checkStartMode();
    if ((!bool) || (isSecureMode()))
    {
      setupWindowFlags();
      checkLatestLeavingTime();
      if (!((Boolean)get(PROP_KEEP_LAST_CAPTURE_SETTINGS)).booleanValue()) {
        restoreToDefaultSelfTimerInterval();
      }
      if (!isStartedInSelfieMode()) {
        break label251;
      }
      Log.w(this.TAG, "onNewIntent() - Switch to front camera");
      if (!switchCamera(Camera.LensFacing.FRONT, 36))
      {
        if (!((Boolean)get(PROP_IS_CAMERA_LOCKED)).booleanValue()) {
          break label229;
        }
        Log.w(this.TAG, "onNewIntent() - Camera is locked");
        if (!this.m_CameraContextStack.isEmpty())
        {
          paramIntent = CameraUtils.findCamera((List)get(PROP_AVAILABLE_CAMERAS), Camera.LensFacing.FRONT, false);
          if (paramIntent == null) {
            break label216;
          }
          ((CameraContext)this.m_CameraContextStack.get(0)).camera = paramIntent;
        }
      }
    }
    for (;;)
    {
      this.m_IsQuickCaptureTriggered = false;
      return;
      Log.w(this.TAG, "onNewIntent() - Instance becomes non-secure mode from secure mode, finish itself");
      finish();
      return;
      label216:
      Log.e(this.TAG, "onNewIntent() - No front camera");
      continue;
      label229:
      ((Settings)get(PROP_SETTINGS)).set("CameraLensFacing", Camera.LensFacing.FRONT);
      continue;
      label251:
      if ((this.m_LaunchSource != LaunchSource.NORMAL) || (!((Boolean)get(PROP_KEEP_LAST_CAPTURE_SETTINGS)).booleanValue())) {
        restoreToDefaultCamera();
      }
    }
  }
  
  protected void onPause()
  {
    cancelQuickCaptures();
    if (Handle.isValid(this.m_PhotoCaptureHandle))
    {
      Log.w(this.TAG, "onPause() - Stop photo capture");
      if (Handle.isValid(this.m_PendingPhotoCaptureHandle))
      {
        this.m_PendingPhotoCaptureHandle.close();
        this.m_PendingPhotoCaptureHandle = null;
      }
      stopPhotoCapture(this.m_PhotoCaptureHandle);
    }
    if (Handle.isValid(this.m_VideoCaptureHandle))
    {
      Log.w(this.TAG, "onPause() - Stop video capture");
      stopVideoCapture(this.m_VideoCaptureHandle, false, false);
    }
    getHandler().postAtFrontOfQueue(new Runnable()
    {
      public void run()
      {
        if (!((Boolean)CameraActivity.this.get(CameraActivity.PROP_IS_RUNNING)).booleanValue()) {
          CameraActivity.this.stopCameraPreview(true);
        }
      }
    });
    super.onPause();
    hideReviewScreen();
    getWindow().clearFlags(128);
    resetIdleState();
    stopAccelerometer();
    stopOrientationListener();
    Object localObject1;
    int i;
    Object localObject2;
    if (!this.m_CaptureUIDisableHandles.isEmpty())
    {
      localObject1 = new UIDisableHandle[this.m_CaptureUIDisableHandles.size()];
      this.m_CaptureUIDisableHandles.toArray((Object[])localObject1);
      i = localObject1.length - 1;
      while (i >= 0)
      {
        localObject2 = localObject1[i];
        if ((((UIDisableHandle)localObject2).flags & 0x1) != 0)
        {
          Log.w(this.TAG, "onPause() - Remove capture UI disable handle : " + localObject2);
          this.m_CaptureUIDisableHandles.remove(localObject2);
        }
        i -= 1;
      }
      Log.w(this.TAG, "onPause() - Capture UI disable handle count : " + this.m_CaptureUIDisableHandles.size());
      if (this.m_CaptureUIDisableHandles.isEmpty()) {
        setReadOnly(PROP_IS_CAPTURE_UI_ENABLED, Boolean.valueOf(true));
      }
    }
    else
    {
      if (this.m_IsRotationReady)
      {
        this.m_IsRotationReady = false;
        notifyPropertyChanged(PROP_IS_ROTATION_READY, Boolean.valueOf(true), Boolean.valueOf(false));
      }
      if (this.m_BatteryReceiver != null)
      {
        unregisterReceiver(this.m_BatteryReceiver);
        this.m_BatteryReceiver = null;
      }
      if (!this.m_TakeScreenShotHandles.isEmpty())
      {
        localObject1 = (View)get(PROP_CONTENT_VIEW);
        if (!(localObject1 instanceof RelativeLayout)) {
          break label678;
        }
        if (this.m_ScreenShotImageView == null)
        {
          this.m_ScreenShotImageView = new ImageView(this);
          this.m_ScreenShotImageView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
          this.m_ScreenShotImageView.setScaleType(ImageView.ScaleType.MATRIX);
        }
        Log.v(this.TAG, "onPause() - Take screen-shot [start]");
      }
    }
    try
    {
      boolean bool = ((View)localObject1).isDrawingCacheEnabled();
      ((View)localObject1).setDrawingCacheEnabled(true);
      localObject2 = ((View)localObject1).getDrawingCache();
      if (localObject2 == null) {
        break label618;
      }
      this.m_ScreenShotBitmap = ((Bitmap)localObject2).copy(Bitmap.Config.ARGB_8888, false);
      label451:
      if (!bool) {
        ((View)localObject1).setDrawingCacheEnabled(false);
      }
    }
    catch (Throwable localThrowable)
    {
      for (;;)
      {
        Log.e(this.TAG, "onPause() - Fail to take screen-shot", localThrowable);
        this.m_ScreenShotBitmap = null;
        continue;
        Log.w(this.TAG, "onPause() - m_ScreenShotImageView has been added before.");
        continue;
        removeScreenShot();
      }
    }
    Log.v(this.TAG, "onPause() - Take screen-shot [end]");
    if (this.m_ScreenShotBitmap != null)
    {
      this.m_ScreenShotImageView.setImageBitmap(this.m_ScreenShotBitmap);
      if (this.m_ScreenShotImageView.getParent() == null)
      {
        ((ViewGroup)localObject1).addView(this.m_ScreenShotImageView);
        updateScreenShotRotation(this.m_Rotation);
        if (HandlerUtils.hasMessages(this, -100)) {
          updateScreenShotRotation();
        }
      }
    }
    for (;;)
    {
      this.m_PreviousRotation = OrientationManager.getRotation();
      HandlerUtils.sendMessage(this, -110, true, 0L);
      m_LatestLeaveTimeMillis = SystemClock.elapsedRealtime();
      return;
      i = this.m_CaptureUIDisableHandles.size() - 1;
      while (i >= 0)
      {
        Log.w(this.TAG, "onPause() - Active capture UI disable handle : [" + i + "] " + this.m_CaptureUIDisableHandles.get(i));
        i -= 1;
      }
      break;
      label618:
      this.m_ScreenShotBitmap = null;
      Log.w(this.TAG, "onPause() - No drawing cache");
      break label451;
      label678:
      Log.v(this.TAG, "onPause() - Cannot show screen-shot because content view is not RelativeLayout");
    }
  }
  
  protected void onPhotoCaptureFailed(CaptureHandle paramCaptureHandle, CaptureCompleteReason paramCaptureCompleteReason)
  {
    if (this.m_PhotoCaptureHandle != paramCaptureHandle)
    {
      Log.w(this.TAG, "onPhotoCaptureFailed() - Unknown handle : " + paramCaptureHandle + ", expected handle : " + this.m_PhotoCaptureHandle);
      return;
    }
    Log.e(this.TAG, "onPhotoCaptureFailed() - Handle : " + paramCaptureHandle);
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.w(this.TAG, "onPhotoCaptureFailed() - Photo capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
      return;
    case 1: 
      raise(EVENT_CAPTURE_FAILED, new CaptureEventArgs(paramCaptureHandle, this.m_PhotoCaptureHandle.getCaptureTrigger()));
      stopPhotoCapture((CaptureHandleImpl)paramCaptureHandle);
      completeCapture(paramCaptureHandle);
      return;
    }
    raise(EVENT_CAPTURE_FAILED, new CaptureEventArgs(paramCaptureHandle, this.m_PhotoCaptureHandle.getCaptureTrigger()));
    completeCapture(paramCaptureHandle);
  }
  
  protected void onResume()
  {
    boolean bool = true;
    if (getHandler().hasMessages(-110))
    {
      Log.w(this.TAG, "onResume() - Cancel closing cameras");
      getHandler().removeMessages(-110);
    }
    updateScreenSize();
    Object localObject = (PowerManager)getSystemService("power");
    setReadOnly(PROP_IS_SCREEN_ON, Boolean.valueOf(((PowerManager)localObject).isInteractive()));
    super.onResume();
    int j = ((Integer)get(PROP_CONFIG_ORIENTATION)).intValue();
    int i;
    if (this.m_ActivityRotation.isPortrait())
    {
      i = 1;
      if (j == i) {
        removeScreenShot();
      }
      getWindow().addFlags(128);
      resetIdleState();
      updateSystemUiVisibility();
      getHandler().sendEmptyMessageDelayed(-80, 500L);
      registerReceivers();
      if (this.m_BatteryReceiver == null)
      {
        this.m_BatteryReceiver = new BroadcastReceiver()
        {
          public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
          {
            int i = paramAnonymousIntent.getIntExtra("level", 50);
            int j = paramAnonymousIntent.getIntExtra("scale", 100);
            i = Math.min(100, Math.max(0, (int)(i / j * 100.0F)));
            CameraActivity.this.onBatteryLevelChanged(i);
          }
        };
        localObject = new IntentFilter("android.intent.action.BATTERY_CHANGED");
        registerReceiver(this.m_BatteryReceiver, (IntentFilter)localObject);
      }
      if ((!canStartCameraPreview()) || (get(PROP_PHOTO_CAPTURE_STATE) != PhotoCaptureState.PREPARING) || (get(PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.PREPARING)) {
        break label290;
      }
      startCameraPreview();
      label219:
      updateBurstEnablingState();
      if ((!this.m_IsRotationReady) && (!this.m_RotationLockHandles.isEmpty())) {
        break label371;
      }
    }
    for (;;)
    {
      updateSelfTimerInterval();
      if (!((Settings)get(PROP_SETTINGS)).getBoolean("IsDebugMode", false)) {
        bool = ((Boolean)get(PROP_IS_DEBUG_MODE)).booleanValue();
      }
      setDebugMode(bool);
      return;
      i = 2;
      break;
      label290:
      if ((this.m_CameraPreviewState != OperationState.STARTING) && (this.m_CameraPreviewState != OperationState.STARTED)) {
        break label219;
      }
      Log.v(this.TAG, "onResume() - preview is already started");
      startOrientationListener();
      getHandler().postDelayed(new Runnable()
      {
        public void run()
        {
          CameraActivity.-wrap21(CameraActivity.this);
        }
      }, 100L);
      if ((this.m_CameraPreviewState != OperationState.STARTED) || (this.m_LaunchSource != LaunchSource.POWER_KEY)) {
        break label219;
      }
      scheduleQuickCapturePhoto(false);
      break label219;
      label371:
      this.m_IsRotationReady = true;
      notifyPropertyChanged(PROP_IS_ROTATION_READY, Boolean.valueOf(false), Boolean.valueOf(true));
    }
  }
  
  protected void onRotationChanged(Rotation paramRotation1, Rotation paramRotation2)
  {
    try
    {
      boolean bool = this.m_RotationLockHandles.isEmpty();
      if (!bool) {
        return;
      }
      notifyCameraThreadRotationChanged(paramRotation1, paramRotation2);
      if (!OrientationManager.isSystemOrientationEnabled()) {
        paramRotation2 = Rotation.PORTRAIT;
      }
      notifyUIRotationChanged(paramRotation1, paramRotation2);
      return;
    }
    finally
    {
      if ((!this.m_IsRotationReady) && (((Boolean)get(PROP_IS_RUNNING)).booleanValue()))
      {
        getHandler().removeMessages(-60);
        this.m_IsRotationReady = true;
        notifyPropertyChanged(PROP_IS_ROTATION_READY, Boolean.valueOf(false), Boolean.valueOf(true));
      }
    }
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    paramBundle.putBoolean("CameraActivity.IsQuickCaptureTriggered", this.m_IsQuickCaptureTriggered);
    super.onSaveInstanceState(paramBundle);
  }
  
  protected void onStart()
  {
    checkRequiredPermissions();
    super.onStart();
    if (((Integer)get(PROP_CONFIG_ORIENTATION)).intValue() == 1) {
      removeScreenShot();
    }
    if ((!Handle.isValid(this.m_OrientationCallbackHandle)) && (this.m_OrientationCallback != null)) {
      this.m_OrientationCallbackHandle = OrientationManager.setCallback(this.m_OrientationCallback, getHandler());
    }
  }
  
  protected void onStop()
  {
    this.m_OrientationCallbackHandle = Handle.close(this.m_OrientationCallbackHandle);
    super.onStop();
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    MotionEventArgs localMotionEventArgs = MotionEventArgs.obtain(paramMotionEvent);
    raise(EVENT_TOUCH, localMotionEventArgs);
    if (localMotionEventArgs.isHandled())
    {
      localMotionEventArgs.recycle();
      return true;
    }
    localMotionEventArgs.recycle();
    return super.onTouchEvent(paramMotionEvent);
  }
  
  protected void onVideoCaptureFailed(CaptureHandle paramCaptureHandle, CaptureCompleteReason paramCaptureCompleteReason)
  {
    if (this.m_VideoCaptureHandle != paramCaptureHandle)
    {
      Log.w(this.TAG, "onVideoCaptureFailed() - Unknown handle : " + paramCaptureHandle + ", expected handle : " + this.m_PhotoCaptureHandle);
      return;
    }
    Log.e(this.TAG, "onVideoCaptureFailed() - Handle : " + paramCaptureHandle);
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    case 2: 
    case 4: 
    case 5: 
    case 6: 
    case 7: 
    default: 
      Log.w(this.TAG, "onVideoCaptureFailed() - Video capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return;
    case 1: 
    case 3: 
    case 8: 
      raise(EVENT_CAPTURE_FAILED, new CaptureEventArgs(paramCaptureHandle, this.m_VideoCaptureHandle.getCaptureTrigger()));
      stopVideoCapture((CaptureHandleImpl)paramCaptureHandle, false, true);
      completeCapture(paramCaptureHandle);
      this.m_VideoCaptureCUDHandle = Handle.close(this.m_VideoCaptureCUDHandle);
      return;
    }
    raise(EVENT_CAPTURE_FAILED, new CaptureEventArgs(paramCaptureHandle, this.m_VideoCaptureHandle.getCaptureTrigger()));
    completeCapture(paramCaptureHandle);
    this.m_VideoCaptureCUDHandle = Handle.close(this.m_VideoCaptureCUDHandle);
  }
  
  public void pauseAudioPlaybackForVideoRecording()
  {
    this.mAudioManager = ((AudioManager)getSystemService("audio"));
    this.mAudioManager.requestAudioFocus(null, 3, 4);
  }
  
  public boolean pauseVideoCapture()
  {
    verifyAccess();
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "pauseVideoCapture() - Current state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return false;
    case 2: 
    case 3: 
      return true;
    }
    if (this.m_VideoCaptureHandle == null)
    {
      Log.e(this.TAG, "pauseVideoCapture() - No capture handle");
      return false;
    }
    final CaptureHandleImpl localCaptureHandleImpl = this.m_VideoCaptureHandle;
    if (!HandlerUtils.post(this.m_CameraThread, new Runnable()
    {
      public void run()
      {
        Log.v(CameraActivity.-get1(CameraActivity.this), "pauseVideoCapture() - Pause in camera thread");
        boolean bool = CameraActivity.-get3(CameraActivity.this).pauseVideoCapture(localCaptureHandleImpl.internalCaptureHandle);
        CameraActivity localCameraActivity = CameraActivity.this;
        if (bool) {}
        for (int i = 1;; i = 0)
        {
          HandlerUtils.sendMessage(localCameraActivity, -32, i, 0, localCaptureHandleImpl);
          return;
        }
      }
    }))
    {
      Log.e(this.TAG, "pauseVideoCapture() - Fail to pause in camera thread");
      return false;
    }
    setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.PAUSING);
    if (get(PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.PAUSING)
    {
      Log.e(this.TAG, "pauseVideoCapture() - Interrupted by other operations");
      return false;
    }
    if (!Handle.isValid(this.m_VideoCaptureCUDHandle)) {
      this.m_VideoCaptureCUDHandle = disableCaptureUI("PauseVideoCapture");
    }
    return true;
  }
  
  public void removeComponent(Component paramComponent)
  {
    this.m_ComponentManager.removeComponent(paramComponent);
  }
  
  public Handle requestPreCaptureFocusLock()
  {
    verifyAccess();
    Handle local39 = new Handle("PreCaptureFocusLockRequest")
    {
      protected void onClose(int paramAnonymousInt)
      {
        CameraActivity.-wrap17(CameraActivity.this, this);
      }
    };
    this.m_PreCaptureFocusLockReqHandles.add(local39);
    return local39;
  }
  
  public void resumeAudioPlayback()
  {
    this.mAudioManager = ((AudioManager)getSystemService("audio"));
    this.mAudioManager.abandonAudioFocus(null);
  }
  
  public boolean resumeVideoCapture()
  {
    verifyAccess();
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    case 3: 
    case 4: 
    case 5: 
    default: 
      Log.e(this.TAG, "resumeVideoCapture() - Current state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return false;
    case 1: 
    case 6: 
      return true;
    }
    if (this.m_VideoCaptureHandle == null)
    {
      Log.e(this.TAG, "resumeVideoCapture() - No capture handle");
      return false;
    }
    final CaptureHandleImpl localCaptureHandleImpl = this.m_VideoCaptureHandle;
    if (!HandlerUtils.post(this.m_CameraThread, new Runnable()
    {
      public void run()
      {
        Log.v(CameraActivity.-get1(CameraActivity.this), "resumeVideoCapture() - Resume in camera thread");
        boolean bool = CameraActivity.-get3(CameraActivity.this).resumeVideoCapture(localCaptureHandleImpl.internalCaptureHandle);
        CameraActivity localCameraActivity = CameraActivity.this;
        if (bool) {}
        for (int i = 1;; i = 0)
        {
          HandlerUtils.sendMessage(localCameraActivity, -33, i, 0, localCaptureHandleImpl);
          return;
        }
      }
    }))
    {
      Log.e(this.TAG, "resumeVideoCapture() - Fail to resume in camera thread");
      return false;
    }
    setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.RESUMING);
    if (get(PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.RESUMING)
    {
      Log.e(this.TAG, "resumeVideoCapture() - Interrupted by other operations");
      return false;
    }
    if (!Handle.isValid(this.m_VideoCaptureCUDHandle)) {
      this.m_VideoCaptureCUDHandle = disableCaptureUI("ResumeVideoCapture");
    }
    return true;
  }
  
  protected void selectCameraPreviewSize()
  {
    selectCameraPreviewSize(false);
  }
  
  protected void selectCameraPreviewSize(boolean paramBoolean)
  {
    Object localObject = getResolutionManager();
    if (localObject == null)
    {
      Log.e(this.TAG, "selectCameraPreviewSize() - No ResolutionManager.");
      return;
    }
    Size localSize = (Size)get(PROP_CAMERA_PREVIEW_SIZE);
    int i;
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[((MediaType)get(PROP_MEDIA_TYPE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "selectCameraPreviewSize() - Unknown media type : " + get(PROP_MEDIA_TYPE));
      return;
    case 1: 
      localObject = (Size)((ResolutionManager)localObject).get(ResolutionManager.PROP_PHOTO_PREVIEW_SIZE);
      switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[this.m_CameraPreviewState.ordinal()])
      {
      default: 
        i = 0;
      }
      break;
    }
    for (;;)
    {
      setReadOnly(PROP_CAMERA_PREVIEW_SIZE, localObject);
      if (i != 0)
      {
        Log.w(this.TAG, "selectCameraPreviewSize() - Restart preview");
        startCameraPreview();
      }
      return;
      localObject = (Size)((ResolutionManager)localObject).get(ResolutionManager.PROP_VIDEO_PREVIEW_SIZE);
      break;
      if ((!paramBoolean) && (((Size)localObject).equals(localSize)))
      {
        i = 0;
      }
      else
      {
        Log.w(this.TAG, "selectCameraPreviewSize() - Stop preview to change preview size");
        stopCameraPreview(true);
        i = 1;
      }
    }
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey == PROP_SELF_TIMER_INTERVAL) {
      return setSelfTimerIntervalProp(((Long)paramTValue).longValue());
    }
    return super.set(paramPropertyKey, paramTValue);
  }
  
  public Handle setCaptureDelayTime(long paramLong)
  {
    verifyAccess();
    CaptureDelayTimeHandle localCaptureDelayTimeHandle = new CaptureDelayTimeHandle(paramLong);
    this.m_CaptureDelayTimeHandles.addLast(localCaptureDelayTimeHandle);
    return localCaptureDelayTimeHandle;
  }
  
  public void setContentView(int paramInt)
  {
    Log.v(this.TAG, "setContentView() - Load content view [start]");
    View localView = getLayoutInflater().inflate(paramInt, null);
    Log.v(this.TAG, "setContentView() - Load content view [end]");
    setContentView(localView);
  }
  
  public void setContentView(View paramView)
  {
    Log.v(this.TAG, "setContentView() - Set content view [start]");
    super.setContentView(paramView);
    Log.v(this.TAG, "setContentView() - Set content view [end]");
  }
  
  public void setContentView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    Log.v(this.TAG, "setContentView() - Set content view [start]");
    super.setContentView(paramView, paramLayoutParams);
    Log.v(this.TAG, "setContentView() - Set content view [end]");
  }
  
  public boolean setDebugMode(boolean paramBoolean)
  {
    if (setReadOnly(PROP_IS_DEBUG_MODE, Boolean.valueOf(paramBoolean)))
    {
      if (!paramBoolean)
      {
        Camera localCamera = (Camera)get(PROP_CAMERA);
        if (localCamera != null) {
          switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[((Camera.LensFacing)localCamera.get(Camera.PROP_LENS_FACING)).ordinal()])
          {
          }
        }
      }
      for (;;)
      {
        ((Settings)get(PROP_SETTINGS)).set("IsDebugMode", Boolean.valueOf(paramBoolean));
        return true;
        switchCamera();
      }
    }
    return false;
  }
  
  public Handle setKeyEventHandler(KeyEventHandler paramKeyEventHandler)
  {
    verifyAccess();
    Object localObject2 = null;
    Iterator localIterator = this.m_KeyEventHandles.iterator();
    Object localObject1;
    do
    {
      localObject1 = localObject2;
      if (!localIterator.hasNext()) {
        break;
      }
      localObject1 = (KeyEventHandle)localIterator.next();
    } while (((KeyEventHandle)localObject1).handler != paramKeyEventHandler);
    if (localObject1 != null)
    {
      this.m_KeyEventHandles.remove(localObject1);
      this.m_KeyEventHandles.add(localObject1);
      return (Handle)localObject1;
    }
    paramKeyEventHandler = new KeyEventHandle(paramKeyEventHandler);
    this.m_KeyEventHandles.add(paramKeyEventHandler);
    return paramKeyEventHandler;
  }
  
  public void setMediaResult(int paramInt, Bitmap paramBitmap)
  {
    if (!isServiceMode())
    {
      Log.w(this.TAG, "setMediaResult() - Not service mode");
      return;
    }
    switch (paramInt)
    {
    default: 
      Log.w(this.TAG, "setMediaResult() - Unknow review screen result: " + paramInt);
      return;
    case 0: 
      Log.v(this.TAG, "setMediaResult() - Cancel");
      setResult(0, new Intent());
      finish();
      return;
    case 1: 
      Log.v(this.TAG, "setMediaResult() - OK, Inline bitmap: ", paramBitmap);
      setResult(-1, new Intent().putExtra("data", paramBitmap));
      finish();
      return;
    }
    Log.v(this.TAG, "setMediaResult() - Retake");
    hideReviewScreen();
  }
  
  public void setMediaResult(int paramInt, MediaInfo paramMediaInfo)
  {
    if (!isServiceMode())
    {
      Log.w(this.TAG, "setMediaResult() - Not service mode");
      return;
    }
    switch (paramInt)
    {
    default: 
      Log.w(this.TAG, "setMediaResult() - Unknow review screen result: " + paramInt);
      return;
    case 0: 
      Log.v(this.TAG, "setMediaResult() - Cancel");
      setResult(0, new Intent());
      finish();
      return;
    case 1: 
      Log.v(this.TAG, "setMediaResult() - OK, Uri: ", paramMediaInfo.contentURI);
      if (get(PROP_MEDIA_TYPE) == MediaType.VIDEO) {
        setResult(-1, new Intent().setData(paramMediaInfo.contentURI).addFlags(1));
      }
      for (;;)
      {
        finish();
        return;
        setResult(-1);
      }
    }
    Log.v(this.TAG, "setMediaResult() - Retake");
    hideReviewScreen();
  }
  
  public boolean setMediaType(MediaType paramMediaType)
  {
    verifyAccess();
    if (get(PROP_MEDIA_TYPE) == paramMediaType) {
      return true;
    }
    Log.w(this.TAG, "setMediaType() - Media type : " + paramMediaType);
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[paramMediaType.ordinal()])
    {
    default: 
      Log.e(this.TAG, "setMediaType() - Unknown media type : " + paramMediaType);
      return false;
    case 1: 
      switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
      {
      default: 
        Log.e(this.TAG, "setMediaType() - Current video capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
        return false;
      }
      break;
    }
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "setMediaType() - Current photo capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
      return false;
    }
    if (((Boolean)get(PROP_IS_CAMERA_THREAD_STARTED)).booleanValue())
    {
      if (!this.m_CameraThread.setMediaType(paramMediaType))
      {
        Log.e(this.TAG, "setMediaType() - Fail to change media type");
        return false;
      }
    }
    else {
      Log.w(this.TAG, "setMediaType() - Change media type before camera thread start");
    }
    setReadOnly(PROP_MEDIA_TYPE, paramMediaType);
    if ((paramMediaType == MediaType.PHOTO) && (this.m_FastShotToShotDisableHandles.isEmpty())) {
      setReadOnly(PROP_IS_FAST_SHOT_TO_SHOT_ENABLED, Boolean.valueOf(true));
    }
    for (;;)
    {
      selectCameraPreviewSize();
      updateSelfTimerUsability();
      updateSelfTimerInterval();
      updateBurstEnablingState();
      return true;
      setReadOnly(PROP_IS_FAST_SHOT_TO_SHOT_ENABLED, Boolean.valueOf(false));
    }
  }
  
  protected <TValue> boolean setReadOnly(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    Camera localCamera = null;
    if (paramPropertyKey == PROP_ACTIVITY_ROTATION) {
      throw new IllegalAccessError("Cannot change activity rotation.");
    }
    if (paramPropertyKey == PROP_CAMERA)
    {
      paramPropertyKey = localCamera;
      if (!this.m_CameraContextStack.isEmpty()) {
        paramPropertyKey = (CameraContext)this.m_CameraContextStack.getLast();
      }
      if (paramPropertyKey == null)
      {
        paramPropertyKey = new CameraContext(false);
        this.m_CameraContextStack.addLast(paramPropertyKey);
      }
      for (localCamera = null;; localCamera = paramPropertyKey.camera)
      {
        paramPropertyKey.camera = ((Camera)paramTValue);
        boolean bool = super.setReadOnly(PROP_CAMERA, paramPropertyKey.camera);
        if (bool) {
          onCameraChanged(localCamera, paramPropertyKey.camera);
        }
        return bool;
      }
    }
    if (paramPropertyKey == PROP_CAMERA_PREVIEW_STATE) {
      throw new IllegalAccessError("Cannot change camera preview state.");
    }
    if (paramPropertyKey == PROP_ROTATION) {
      throw new IllegalAccessError("Cannot change UI rotation.");
    }
    return super.setReadOnly(paramPropertyKey, paramTValue);
  }
  
  public Handle setRecordingTimeRatio(float paramFloat)
  {
    verifyAccess();
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    case 6: 
    case 8: 
    default: 
      Log.e(this.TAG, "setRecordingTimeRatio() - Cannot restore recording time ratio when capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return null;
    }
    if (paramFloat <= 0.0F)
    {
      Log.e(this.TAG, "setRecordingTimeRatio() - Invalid ratio : " + paramFloat);
      return null;
    }
    RecordingTimeRatioHandle localRecordingTimeRatioHandle = new RecordingTimeRatioHandle(paramFloat);
    this.m_RecordingTimeRatioHandles.add(localRecordingTimeRatioHandle);
    Log.v(this.TAG, "setRecordingTimeRatio() - Ratio : " + paramFloat + ", handle : " + localRecordingTimeRatioHandle);
    return localRecordingTimeRatioHandle;
  }
  
  public void setRequestedOrientation(int paramInt)
  {
    super.setRequestedOrientation(paramInt);
    onRequestedOrientationChanged(paramInt);
  }
  
  public void setRotationDelayTime(long paramLong)
  {
    HandlerUtils.removeMessages(this, -95);
    Log.v(this.TAG, "setRotationDelayTime() - delay : " + paramLong);
    this.m_RotationDelay = paramLong;
    if (paramLong == 0L)
    {
      Rotation localRotation = OrientationManager.getRotation();
      if (localRotation != null) {
        onRotationChanged(this.m_Rotation, localRotation);
      }
    }
  }
  
  public final Handle setSettings(Settings paramSettings)
  {
    verifyAccess();
    if (paramSettings == null)
    {
      Log.e(this.TAG, "setSettings() - No settings.");
      return null;
    }
    SettingsHandle localSettingsHandle = new SettingsHandle(paramSettings);
    this.m_SettingsHandles.add(localSettingsHandle);
    Log.w(this.TAG, "setSettings() - Create handle : " + localSettingsHandle);
    setReadOnly(PROP_SETTINGS, paramSettings);
    updateSelfTimerInterval();
    return localSettingsHandle;
  }
  
  public boolean showReviewScreen()
  {
    if (this.m_ReviewScreenUI == null)
    {
      this.m_ReviewScreenUI = ((ReviewScreen)findComponent(ReviewScreen.class));
      if (this.m_ReviewScreenUI == null)
      {
        Log.w(this.TAG, "showReviewScreen() - Cannot find ReviewScreen component");
        return false;
      }
    }
    this.m_ReviewScreenHandle = this.m_ReviewScreenUI.showReviewScreen();
    return true;
  }
  
  public void showToast(int paramInt)
  {
    showToast(getString(paramInt));
  }
  
  public void showToast(CharSequence paramCharSequence)
  {
    if (this.m_ToastManager == null) {
      this.m_ToastManager = ((ToastManager)findComponent(ToastManager.class));
    }
    if (this.m_ToastManager != null)
    {
      this.m_ToastManager.showToast(paramCharSequence, 0);
      return;
    }
    Toast.makeText(this, paramCharSequence, 1).show();
  }
  
  public Handle startActivityForResult(Intent paramIntent, ActivityResultCallback paramActivityResultCallback)
  {
    if (paramIntent == null)
    {
      Log.e(this.TAG, "startActivityForResult() - No intent");
      return null;
    }
    verifyAccess();
    int i = 64;
    for (;;)
    {
      if ((i <= 0) || (this.m_ActivityResultHandles.get(i) == null))
      {
        if (i > 0) {
          break;
        }
        Log.e(this.TAG, "startActivityForResult() - No available request code");
        return null;
      }
      i -= 1;
    }
    paramActivityResultCallback = new ActivityResultHandle(paramActivityResultCallback);
    this.m_ActivityResultHandles.put(i, paramActivityResultCallback);
    try
    {
      startActivityForResult(paramIntent, i);
      return paramActivityResultCallback;
    }
    catch (Throwable paramIntent)
    {
      Log.e(this.TAG, "startActivityForResult() - Fail to start activity", paramIntent);
      this.m_ActivityResultHandles.delete(i);
    }
    return null;
  }
  
  public final boolean startCameraPreview()
  {
    return startCameraPreview(true);
  }
  
  public final void stopCameraPreview()
  {
    Camera localCamera = (Camera)get(PROP_CAMERA);
    if (localCamera == null) {
      return;
    }
    if ((Camera.HardwareLevel)localCamera.get(Camera.PROP_HARDWARE_LEVEL) == Camera.HardwareLevel.LEGACY)
    {
      stopCameraPreview(true);
      return;
    }
    stopCameraPreview(false);
  }
  
  public void stopCameraPreview(boolean paramBoolean)
  {
    verifyAccess();
    Camera localCamera;
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[this.m_CameraPreviewState.ordinal()])
    {
    case 1: 
    default: 
    case 2: 
    case 4: 
      do
      {
        for (;;)
        {
          localCamera = (Camera)get(PROP_CAMERA);
          if (localCamera != null) {
            break;
          }
          changeCameraPreviewState(OperationState.STOPPED);
          return;
          Log.w(this.TAG, "stopCameraPreview() - Stop while starting");
        }
      } while (paramBoolean);
      return;
    }
    return;
    if (!Handle.isValid(this.m_CameraPreviewStartCUDHandle)) {
      this.m_CameraPreviewStartCUDHandle = disableCaptureUI("CameraPreviewStartStop");
    }
    if (get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.READY) {
      setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.PREPARING);
    }
    if (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.READY) {
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.PREPARING);
    }
    if (changeCameraPreviewState(OperationState.STOPPING) != OperationState.STOPPING)
    {
      Log.w(this.TAG, "stopCameraPreview() - Process interrupted");
      return;
    }
    int i;
    long l1;
    if (paramBoolean)
    {
      i = 1;
      if (this.m_CameraThread != null)
      {
        if (!paramBoolean) {
          break label298;
        }
        l1 = SystemClock.elapsedRealtime();
        label202:
        if (!this.m_CameraThread.stopCameraPreview(localCamera, i))
        {
          if (!paramBoolean) {
            break label303;
          }
          Log.e(this.TAG, "stopCameraPreview() - Fail to stop camera preview synchronously");
        }
      }
    }
    for (;;)
    {
      if (paramBoolean)
      {
        long l2 = SystemClock.elapsedRealtime();
        Log.w(this.TAG, "stopCameraPreview() - Take " + (l2 - l1) + " ms to stop preview");
      }
      if (this.m_CameraPreviewState == OperationState.STOPPING) {
        changeCameraPreviewState(OperationState.STOPPED);
      }
      return;
      i = 0;
      break;
      label298:
      l1 = 0L;
      break label202;
      label303:
      Log.e(this.TAG, "stopCameraPreview() - Fail to stop camera preview");
    }
  }
  
  public boolean switchCamera()
  {
    verifyAccess();
    Object localObject = (Camera)get(PROP_CAMERA);
    if (localObject == null)
    {
      Log.e(this.TAG, "switchCamera() - No primary camera");
      return false;
    }
    if ((!this.m_CameraLockHandles.isEmpty()) && (((CameraLockHandle)this.m_CameraLockHandles.getLast()).lensFacing == ((Camera)localObject).get(Camera.PROP_LENS_FACING)))
    {
      Log.e(this.TAG, "switchCamera() - Camera is locked to " + ((CameraLockHandle)this.m_CameraLockHandles.getLast()).lensFacing);
      return false;
    }
    List localList = (List)get(PROP_AVAILABLE_CAMERAS);
    switch (-getcom-oneplus-camera-Camera$LensFacingSwitchesValues()[((Camera.LensFacing)localObject.get(Camera.PROP_LENS_FACING)).ordinal()])
    {
    default: 
      localObject = CameraUtils.findCamera(localList, Camera.LensFacing.BACK, false);
    }
    while (localObject == null)
    {
      Log.e(this.TAG, "switchCamera() - No camera to switch");
      return false;
      Camera localCamera;
      if (((Boolean)get(PROP_IS_DEBUG_MODE)).booleanValue())
      {
        localCamera = CameraUtils.findCamera(localList, Camera.LensFacing.BACK_WIDE, false);
        localObject = localCamera;
        if (localCamera == null)
        {
          localCamera = CameraUtils.findCamera(localList, Camera.LensFacing.BACK_TELE, false);
          localObject = localCamera;
          if (localCamera != null) {}
        }
      }
      else
      {
        localObject = CameraUtils.findCamera(localList, Camera.LensFacing.FRONT, false);
        continue;
        if (((Boolean)get(PROP_IS_DEBUG_MODE)).booleanValue())
        {
          localCamera = CameraUtils.findCamera(localList, Camera.LensFacing.BACK_TELE, false);
          localObject = localCamera;
          if (localCamera != null) {}
        }
        else
        {
          localObject = CameraUtils.findCamera(localList, Camera.LensFacing.FRONT, false);
          continue;
          localObject = CameraUtils.findCamera(localList, Camera.LensFacing.FRONT, false);
        }
      }
    }
    Log.w(this.TAG, "switchCamera() - Select " + localObject);
    return switchCamera((Camera)localObject);
  }
  
  public boolean switchCamera(Camera.LensFacing paramLensFacing)
  {
    return switchCamera(paramLensFacing, 0);
  }
  
  public boolean switchCamera(Camera.LensFacing paramLensFacing, int paramInt)
  {
    return switchCamera((Camera)get(PROP_CAMERA), paramLensFacing, paramInt);
  }
  
  public Handle takeScreenShot()
  {
    Handle local44 = new Handle("TakeScreenShot")
    {
      protected void onClose(int paramAnonymousInt)
      {
        CameraActivity.-wrap18(CameraActivity.this, this);
      }
    };
    this.m_TakeScreenShotHandles.add(local44);
    return local44;
  }
  
  public static abstract interface ActivityResultCallback
  {
    public abstract void onActivityResult(Handle paramHandle, int paramInt, Intent paramIntent);
  }
  
  private final class ActivityResultHandle
    extends Handle
  {
    public final CameraActivity.ActivityResultCallback callback;
    
    public ActivityResultHandle(CameraActivity.ActivityResultCallback paramActivityResultCallback)
    {
      super();
      this.callback = paramActivityResultCallback;
    }
    
    protected void onClose(int paramInt) {}
  }
  
  private static final class CameraContext
  {
    public volatile Camera camera;
    public volatile boolean isLocked;
    
    public CameraContext(Camera paramCamera, boolean paramBoolean)
    {
      this.camera = paramCamera;
      this.isLocked = paramBoolean;
    }
    
    public CameraContext(boolean paramBoolean)
    {
      this.isLocked = paramBoolean;
    }
  }
  
  private final class CameraLockHandle
    extends Handle
  {
    public final Camera.LensFacing lensFacing;
    
    public CameraLockHandle(Camera.LensFacing paramLensFacing)
    {
      super();
      this.lensFacing = paramLensFacing;
    }
    
    protected void onClose(int paramInt)
    {
      CameraActivity.-wrap24(CameraActivity.this, this);
    }
  }
  
  private final class CaptureDelayTimeHandle
    extends Handle
  {
    public final long delayTimeMillis;
    
    public CaptureDelayTimeHandle(long paramLong)
    {
      super();
      this.delayTimeMillis = paramLong;
    }
    
    protected void onClose(int paramInt)
    {
      CameraActivity.-wrap2(CameraActivity.this, this);
    }
  }
  
  private final class CaptureHandleImpl
    extends CaptureHandle
  {
    public volatile int closeFlags;
    public long creationTime;
    public long delayTimeMillis;
    public final int flags;
    public final int frameCount;
    public CaptureHandle internalCaptureHandle;
    public boolean isVideoSnapshot;
    private final long m_CaptureRealTime;
    
    public CaptureHandleImpl(Camera paramCamera, CaptureMode paramCaptureMode, Rotation paramRotation, int paramInt)
    {
      super(paramCaptureMode, paramRotation, MediaType.VIDEO);
      this.frameCount = 0;
      this.creationTime = SystemClock.elapsedRealtime();
      this.flags = paramInt;
      this.m_CaptureRealTime = SystemClock.elapsedRealtime();
    }
    
    public CaptureHandleImpl(Camera paramCamera, CaptureMode paramCaptureMode, Rotation paramRotation, int paramInt1, int paramInt2)
    {
      super(paramCaptureMode, paramRotation, MediaType.PHOTO);
      this.frameCount = paramInt1;
      this.creationTime = SystemClock.elapsedRealtime();
      this.flags = paramInt2;
      this.m_CaptureRealTime = SystemClock.elapsedRealtime();
    }
    
    public final void close()
    {
      super.closeDirectly();
    }
    
    public long getCaptureRealTime()
    {
      return this.m_CaptureRealTime;
    }
    
    public CaptureTrigger getCaptureTrigger()
    {
      if ((this.flags & 0x10) != 0) {
        return CaptureTrigger.HW_BUTTON;
      }
      return CaptureTrigger.SW_BUTTON;
    }
    
    public CaptureHandle getInternalCaptureHandle()
    {
      return this.internalCaptureHandle;
    }
    
    public boolean isBurstPhotoCapture()
    {
      return (getMediaType() == MediaType.PHOTO) && (this.frameCount != 1);
    }
    
    public boolean isVideoSnapshot()
    {
      return this.isVideoSnapshot;
    }
    
    protected void onClose(int paramInt)
    {
      this.closeFlags = paramInt;
      switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[getMediaType().ordinal()])
      {
      default: 
        return;
      case 1: 
        CameraActivity.-wrap22(CameraActivity.this, this);
        return;
      }
      CameraActivity.-wrap23(CameraActivity.this, this, false, false);
    }
    
    public void updateCreationTime()
    {
      this.creationTime = SystemClock.elapsedRealtime();
    }
  }
  
  private class KeyEventHandle
    extends Handle
  {
    public KeyEventHandler handler;
    
    public KeyEventHandle(KeyEventHandler paramKeyEventHandler)
    {
      super();
      this.handler = paramKeyEventHandler;
    }
    
    protected void onClose(int paramInt)
    {
      CameraActivity.this.verifyAccess();
      CameraActivity.-get4(CameraActivity.this).remove(this);
    }
  }
  
  private final class RecordingTimeRatioHandle
    extends Handle
  {
    public final float ratio;
    
    public RecordingTimeRatioHandle(float paramFloat)
    {
      super();
      this.ratio = paramFloat;
    }
    
    protected void onClose(int paramInt)
    {
      CameraActivity.-wrap19(CameraActivity.this, this);
    }
  }
  
  private final class RotationLockHandle
    extends Handle
  {
    public final Rotation rotation;
    
    public RotationLockHandle(Rotation paramRotation)
    {
      super();
      this.rotation = paramRotation;
    }
    
    protected void onClose(int paramInt)
    {
      CameraActivity.-wrap25(CameraActivity.this, this);
    }
  }
  
  private final class SettingsHandle
    extends Handle
  {
    public final Settings settings;
    
    public SettingsHandle(Settings paramSettings)
    {
      super();
      this.settings = paramSettings;
    }
    
    protected void onClose(int paramInt)
    {
      CameraActivity.-wrap20(CameraActivity.this, this);
    }
  }
  
  private final class UIDisableHandle
    extends Handle
  {
    public final int flags;
    public final Date time = new Date(System.currentTimeMillis());
    public final String usage;
    
    public UIDisableHandle(String paramString, int paramInt)
    {
      super();
      this.usage = paramString;
      this.flags = paramInt;
    }
    
    protected void onClose(int paramInt)
    {
      CameraActivity.-wrap5(CameraActivity.this, this);
    }
    
    public String toString()
    {
      return super.toString() + "{ Usage = " + this.usage + ", Time = " + CameraActivity.-get0().format(this.time) + " }";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */