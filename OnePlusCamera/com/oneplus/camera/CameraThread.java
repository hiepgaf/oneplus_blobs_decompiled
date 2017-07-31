package com.oneplus.camera;

import android.content.ContentResolver;
import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.DngCreator;
import android.location.Location;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.os.SystemClock;
import android.util.Range;
import android.util.Size;
import com.oneplus.base.BaseThread;
import com.oneplus.base.BaseThread.ThreadStartCallback;
import com.oneplus.base.EventArgs;
import com.oneplus.base.EventHandler;
import com.oneplus.base.EventKey;
import com.oneplus.base.EventSource;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import com.oneplus.base.Rotation;
import com.oneplus.base.ScreenSize;
import com.oneplus.base.Settings;
import com.oneplus.base.component.Component;
import com.oneplus.base.component.ComponentBuilder;
import com.oneplus.base.component.ComponentCreationPriority;
import com.oneplus.base.component.ComponentEventArgs;
import com.oneplus.base.component.ComponentManager;
import com.oneplus.base.component.ComponentOwner;
import com.oneplus.camera.capturemode.CaptureMode;
import com.oneplus.camera.io.FileManager;
import com.oneplus.camera.io.FileManagerBuilder;
import com.oneplus.camera.io.MediaSaveTask;
import com.oneplus.camera.io.RawPhotoSaveTask;
import com.oneplus.camera.io.VideoSaveTask;
import com.oneplus.camera.location.LocationManager;
import com.oneplus.camera.media.FileSizeEstimator;
import com.oneplus.camera.media.MediaEventArgs;
import com.oneplus.camera.media.MediaType;
import com.oneplus.camera.media.Resolution;
import com.oneplus.io.FileUtils;
import com.oneplus.io.Path;
import com.oneplus.io.Storage;
import com.oneplus.io.Storage.Type;
import com.oneplus.io.StorageManager;
import com.oneplus.io.StorageUtils;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class CameraThread
  extends BaseThread
  implements ComponentOwner
{
  private static final ComponentBuilder[] DEFAULT_COMPONENT_BUILDERS = { new CameraDeviceManagerBuilder(), new ExposureControllerBuilder(), new WhiteBalanceControllerBuilder(), new FileManagerBuilder(), new FocusControllerBuilder() };
  private static final long DURATION_VIDEO_CAPTURE_DELAY = 300L;
  private static final long DURATION_VIDEO_REC_LIMIT_CHECK_FAST = 500L;
  private static final long DURATION_VIDEO_REC_LIMIT_CHECK_FASTEST = 100L;
  public static final EventKey<CaptureEventArgs> EVENT_BURST_PHOTO_RECEIVED;
  public static final EventKey<CameraEventArgs> EVENT_CAMERA_ERROR;
  public static final EventKey<CameraOpenFailedEventArgs> EVENT_CAMERA_OPEN_FAILED;
  public static final EventKey<CaptureEventArgs> EVENT_CAPTURE_STARTED;
  public static final EventKey<CaptureEventArgs> EVENT_DEFAULT_PHOTO_CAPTURE_COMPLETED;
  public static final EventKey<CaptureEventArgs> EVENT_DEFAULT_VIDEO_CAPTURE_COMPLETED;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_FILE_SAVED;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_SAVED;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_SAVE_CANCELLED;
  public static final EventKey<MediaEventArgs> EVENT_MEDIA_SAVE_FAILED;
  public static final EventKey<CameraCaptureEventArgs> EVENT_POSTVIEW_RECEIVED;
  public static final EventKey<CaptureEventArgs> EVENT_SHUTTER;
  public static final EventKey<UnprocessedPictureEventArgs> EVENT_UNPROCESSED_PHOTO_RECEIVED;
  public static final int FLAG_IGNORE_STORAGE_CHECK = 8;
  public static final int FLAG_LOCK_FOCUS_BEFORE_CAPTURE = 16;
  public static final int FLAG_NEED_REVIEW = 4;
  public static final int FLAG_NO_SHUTTER_SOUND = 2;
  public static final int FLAG_SYNCHRONOUS = 1;
  private static final long KERNEL_FILE_IO_BUFFER_SIZE = 20971520L;
  private static final int MSG_CAPTURE_VIDEO = 10010;
  private static final int MSG_CHECK_REMAINING_MEDIA_COUNT = 10020;
  private static final int MSG_CHECK_VIDEO_REC_LIMIT = 10030;
  private static final int MSG_FAKE_SHUTTER = 10040;
  private static final int MSG_GET_STORAGE_MANAGER = 10080;
  private static final int MSG_NOTIFY_REQUIRED_PERMS_STATE = 10050;
  private static final int MSG_SCREEN_SIZE_CHANGED = 10000;
  private static final int MSG_STOP_CAMERA_PREVIEW = 10070;
  private static final int MSG_STOP_DEFAULT_CAPTURE = 10060;
  private static final int OFFLINE_JPEG_EXIF_TAG_ID = 1;
  public static final PropertyKey<Boolean> PROP_ALL_REQUIRED_PERMISSIONS_GRANTED = new PropertyKey("AllRequiredPermissionsGranted", Boolean.class, CameraThread.class, Boolean.valueOf(false));
  public static final PropertyKey<List<Camera>> PROP_AVAILABLE_CAMERAS = new PropertyKey("AvailableCameras", List.class, CameraThread.class, Collections.EMPTY_LIST);
  public static final PropertyKey<Camera> PROP_CAMERA = new PropertyKey("Camera", Camera.class, CameraThread.class, 1, null);
  public static final PropertyKey<OperationState> PROP_CAMERA_PREVIEW_STATE = new PropertyKey("CameraPreviewState", OperationState.class, CameraThread.class, OperationState.STOPPED);
  public static final PropertyKey<Rotation> PROP_CAPTURE_ROTATION = new PropertyKey("CaptureRotation", Rotation.class, CameraThread.class, 2, Rotation.PORTRAIT);
  public static final PropertyKey<Boolean> PROP_IS_CAMERA_PREVIEW_RECEIVED = new PropertyKey("IsCameraPreviewReceived", Boolean.class, CameraThread.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_CAPTURING_RAW_PHOTO = new PropertyKey("IsCapturingRawPhoto", Boolean.class, CameraThread.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_VIDEO_SNAPSHOT_ENABLED = new PropertyKey("IsVideoSnapshotEnabled", Boolean.class, CameraThread.class, Boolean.valueOf(false));
  public static final PropertyKey<MediaResultInfo> PROP_MEDIA_RESULT_INFO = new PropertyKey("MediaResultInfo", MediaResultInfo.class, CameraThread.class, 0, null);
  public static final PropertyKey<MediaType> PROP_MEDIA_TYPE = new PropertyKey("MediaType", MediaType.class, CameraThread.class, MediaType.PHOTO);
  public static final PropertyKey<CaptureCompleteReason> PROP_PHOTO_CAPTURE_COMPLETE_REASON = new PropertyKey("PhotoCaptureCompleteReason", CaptureCompleteReason.class, CameraThread.class, CaptureCompleteReason.NORMAL);
  public static final PropertyKey<PhotoCaptureState> PROP_PHOTO_CAPTURE_STATE = new PropertyKey("PhotoCaptureState", PhotoCaptureState.class, CameraThread.class, PhotoCaptureState.PREPARING);
  public static final PropertyKey<Long> PROP_REMAINING_PHOTO_COUNT = new PropertyKey("RemainingPhotoCount", Long.class, CameraThread.class, Long.valueOf(0L));
  public static final PropertyKey<Long> PROP_REMAINING_VIDEO_DURATION_SECONDS = new PropertyKey("RemainingVideoDurationSeconds", Long.class, CameraThread.class, Long.valueOf(0L));
  public static final PropertyKey<ScreenSize> PROP_SCREEN_SIZE = new PropertyKey("ScreenSize", ScreenSize.class, CameraThread.class, ScreenSize.EMPTY);
  public static final PropertyKey<CaptureCompleteReason> PROP_VIDEO_CAPTURE_COMPLETE_REASON = new PropertyKey("VideoCaptureCompleteReason", CaptureCompleteReason.class, CameraThread.class, CaptureCompleteReason.NORMAL);
  public static final PropertyKey<VideoCaptureState> PROP_VIDEO_CAPTURE_STATE = new PropertyKey("VideoCaptureState", VideoCaptureState.class, CameraThread.class, VideoCaptureState.PREPARING);
  public static final String SETTINGS_KEY_IS_MIRRORED = "IsMirrored";
  public static final String SETTINGS_KEY_RAW_CAPTURE = "RawCapture";
  public static final String SETTINGS_KEY_SHUTTER_SOUND = "ShutterSound";
  public static final String SETTINGS_KEY_VIDEO_FRAME_RATE = "VideoFrameRate";
  private static final long STORAGE_RESERVED_SPACE_PHOTO = 104857600L;
  private static final long STORAGE_RESERVED_SPACE_VIDEO = 104857600L;
  private static final long THRESHOLD_INVALID_VIDEO_DURATION = 1000L;
  private static final long THRESHOLD_VIDEO_REC_LIMIT_CHECK_FAST = 15L;
  private static final long THRESHOLD_VIDEO_REC_LIMIT_CHECK_FASTEST = 10L;
  private static final int THRESHOLD_WAIT_CAPTURE_COMPLETE = 2000;
  private final PropertyChangedCallback<AutoExposureState> m_AECallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<AutoExposureState> paramAnonymousPropertyKey, PropertyChangeEventArgs<AutoExposureState> paramAnonymousPropertyChangeEventArgs)
    {
      CameraThread.this.m_PreAEState = ((AutoExposureState)paramAnonymousPropertyChangeEventArgs.getOldValue());
    }
  };
  private com.oneplus.camera.media.AudioManager m_AudioManager;
  private Handle m_BurstCaptureSoundStreamHandle;
  private Handle m_BurstShutterPlaySoundHandle;
  private Handle m_CameraCaptureHandle;
  private CameraDeviceManager m_CameraDeviceManager;
  private final EventHandler<EventArgs> m_CameraErrorHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<EventArgs> paramAnonymousEventKey, EventArgs paramAnonymousEventArgs)
    {
      CameraThread.-wrap11(CameraThread.this, (Camera)paramAnonymousEventSource);
    }
  };
  private final EventHandler<CameraOpenFailedEventArgs> m_CameraOpenFailedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CameraOpenFailedEventArgs> paramAnonymousEventKey, CameraOpenFailedEventArgs paramAnonymousCameraOpenFailedEventArgs)
    {
      CameraThread.-wrap12(CameraThread.this, paramAnonymousCameraOpenFailedEventArgs);
    }
  };
  private final PropertyChangedCallback<Boolean> m_CameraPreviewReceivedChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
    {
      CameraThread.-wrap13(CameraThread.this, (Camera)paramAnonymousPropertySource, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
    }
  };
  private int m_CameraPreviewStartFlags;
  private final PropertyChangedCallback<OperationState> m_CameraPreviewStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<OperationState> paramAnonymousPropertyKey, PropertyChangeEventArgs<OperationState> paramAnonymousPropertyChangeEventArgs)
    {
      CameraThread.-wrap14(CameraThread.this, (Camera)paramAnonymousPropertySource, (OperationState)paramAnonymousPropertyChangeEventArgs.getOldValue(), (OperationState)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  private final EventHandler<CameraCaptureEventArgs> m_CameraShutterHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CameraCaptureEventArgs> paramAnonymousEventKey, CameraCaptureEventArgs paramAnonymousCameraCaptureEventArgs)
    {
      CameraThread.-wrap25(CameraThread.this, paramAnonymousCameraCaptureEventArgs.getFrameIndex());
    }
  };
  private Handle m_CameraTimer2SecSoundHandle;
  private Handle m_CameraTimerSoundHandle;
  private final EventHandler<CameraCaptureEventArgs> m_CaptureFailedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CameraCaptureEventArgs> paramAnonymousEventKey, CameraCaptureEventArgs paramAnonymousCameraCaptureEventArgs)
    {
      CameraThread.-wrap16(CameraThread.this, paramAnonymousCameraCaptureEventArgs);
    }
  };
  private final PropertyChangedCallback<OperationState> m_CaptureStateChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<OperationState> paramAnonymousPropertyKey, PropertyChangeEventArgs<OperationState> paramAnonymousPropertyChangeEventArgs)
    {
      if (paramAnonymousPropertyChangeEventArgs.getNewValue() == OperationState.STOPPED) {
        CameraThread.-wrap15(CameraThread.this, (Camera)paramAnonymousPropertySource);
      }
    }
  };
  private final Runnable m_CloseCamerasRunnable = new Runnable()
  {
    public void run()
    {
      CameraThread.-wrap8(CameraThread.this);
    }
  };
  private volatile ComponentManager m_ComponentManager;
  private final Context m_Context;
  private Resolution m_CurrentResolution;
  private Handle m_DefaultBurstShutterSoundEndHandle;
  private Handle m_DefaultBurstShutterSoundHandle;
  private final PhotoCaptureHandlerHandle m_DefaultPhotoCaptureHandlerHandle = new PhotoCaptureHandlerHandle(null);
  private Handle m_DefaultShutterSoundHandle;
  private final VideoCaptureHandlerHandle m_DefaultVideoCaptureHandlerHandle = new VideoCaptureHandlerHandle(null);
  private FileManager m_FileManager;
  private FocusController m_FocusController;
  private final List<ComponentBuilder> m_InitialComponentBuilders = new ArrayList();
  private volatile MediaType m_InitialMediaType;
  private volatile ScreenSize m_InitialScreenSize;
  private boolean m_IsDefaultShutterReceived;
  private boolean m_IsHighComponentsCreated;
  private boolean m_IsMaxFileSizeDesignated;
  private boolean m_IsMediaStoreUpdatePaused;
  private boolean m_IsNormalComponentsCreated;
  private boolean m_IsPictureReceived;
  private long m_LastBurstPhotoTime;
  private byte[] m_LastCapturedJpeg;
  private CaptureCompleteReason m_LastVideoCaptureCompleteReason;
  private LocationManager m_LocationManager;
  private MediaRecorder m_MediaRecorder;
  private MediaRecorder.OnErrorListener m_MediaRecorderErrorListener;
  private MediaRecorder.OnInfoListener m_MediaRecorderInfoListener;
  private long m_MediaRecorderPauseTime;
  private long m_MediaRecorderPauseTimeTotal;
  private long m_MediaRecorderStartTime;
  private final List<CameraPreviewStartRequest> m_PendingCameraPreviewStartRequests = new ArrayList();
  private final List<CameraPreviewStopRequest> m_PendingCameraPreviewStopRequests = new ArrayList();
  private PhotoCaptureHandle m_PhotoCaptureHandle;
  private PhotoCaptureHandlerHandle m_PhotoCaptureHandlerHandle;
  private List<PhotoCaptureHandlerHandle> m_PhotoCaptureHandlerHandles;
  private final EventHandler<CameraCaptureEventArgs> m_PictureReceivedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CameraCaptureEventArgs> paramAnonymousEventKey, CameraCaptureEventArgs paramAnonymousCameraCaptureEventArgs)
    {
      CameraThread.-wrap20(CameraThread.this, paramAnonymousCameraCaptureEventArgs);
    }
  };
  private final EventHandler<CameraCaptureEventArgs> m_PostviewReceivedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CameraCaptureEventArgs> paramAnonymousEventKey, CameraCaptureEventArgs paramAnonymousCameraCaptureEventArgs)
    {
      CameraThread.-wrap21(CameraThread.this, paramAnonymousCameraCaptureEventArgs);
    }
  };
  protected AutoExposureState m_PreAEState = AutoExposureState.INACTIVE;
  private Handle m_PreCaptureFocusLockHandle;
  private CaptureCompleteReason m_PreparedPhotoCaptureCompleteReason;
  private List<CaptureHandle> m_RawPhotoCaptureHandleList = new ArrayList();
  private final EventHandler<CameraCaptureEventArgs> m_RawPictureReceivedHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CameraCaptureEventArgs> paramAnonymousEventKey, CameraCaptureEventArgs paramAnonymousCameraCaptureEventArgs)
    {
      CameraThread.-wrap22(CameraThread.this, paramAnonymousCameraCaptureEventArgs);
    }
  };
  private final List<RecordingTimeRatioHandle> m_RecordingTimeRatioHandles = new ArrayList();
  private StatFs m_RemainingMediaCountStateFs;
  private volatile ResourceIdTable m_ResourceIdTable;
  private Settings m_Settings;
  private final EventHandler<CaptureEventArgs> m_ShutterHandler = new EventHandler()
  {
    public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<CaptureEventArgs> paramAnonymousEventKey, CaptureEventArgs paramAnonymousCaptureEventArgs)
    {
      CameraThread.-wrap24(CameraThread.this, paramAnonymousCaptureEventArgs);
    }
  };
  private StorageManager m_StorageManager;
  private VideoCaptureHandle m_VideoCaptureHandle;
  private VideoCaptureHandlerHandle m_VideoCaptureHandlerHandle;
  private List<VideoCaptureHandlerHandle> m_VideoCaptureHandlerHandles;
  private ParcelFileDescriptor m_VideoFileDescriptor = null;
  private String m_VideoFilePath;
  private Location m_VideoLocation;
  private List<Handle> m_VideoSnapshotDisableHandles;
  private Handle m_VideoStartSoundHandle;
  private Handle m_VideoStopSoundHandle;
  
  static
  {
    EVENT_BURST_PHOTO_RECEIVED = new EventKey("BurstPhotoReceived", CaptureEventArgs.class, CameraThread.class);
    EVENT_CAMERA_ERROR = new EventKey("CameraError", CameraEventArgs.class, CameraThread.class);
    EVENT_CAPTURE_STARTED = new EventKey("CaptureStarted", CaptureEventArgs.class, CameraThread.class);
    EVENT_CAMERA_OPEN_FAILED = new EventKey("CameraOpenFailed", CameraOpenFailedEventArgs.class, CameraThread.class);
    EVENT_DEFAULT_PHOTO_CAPTURE_COMPLETED = new EventKey("DefaultPhotoCaptureCompleted", CaptureEventArgs.class, CameraThread.class);
    EVENT_DEFAULT_VIDEO_CAPTURE_COMPLETED = new EventKey("DefaultVideoCaptureCompleted", CaptureEventArgs.class, CameraThread.class);
    EVENT_MEDIA_FILE_SAVED = new EventKey("MediaFileSaved", MediaEventArgs.class, CameraThread.class);
    EVENT_MEDIA_SAVE_CANCELLED = new EventKey("MediaSaveCancelled", MediaEventArgs.class, CameraThread.class);
    EVENT_MEDIA_SAVE_FAILED = new EventKey("MediaSaveFailed", MediaEventArgs.class, CameraThread.class);
    EVENT_MEDIA_SAVED = new EventKey("MediaSaved", MediaEventArgs.class, CameraThread.class);
    EVENT_POSTVIEW_RECEIVED = new EventKey("PostviewReceived", CameraCaptureEventArgs.class, CameraThread.class);
    EVENT_SHUTTER = new EventKey("Shutter", CaptureEventArgs.class, CameraThread.class);
    EVENT_UNPROCESSED_PHOTO_RECEIVED = new EventKey("UnprocessedPhotoReceived", UnprocessedPictureEventArgs.class, CameraThread.class);
    Settings.setGlobalDefaultValue("ShutterSound", Boolean.valueOf(true));
    Settings.setGlobalDefaultValue("RawCapture", Boolean.valueOf(false));
    Settings.setGlobalDefaultValue("IsMirrored", Boolean.valueOf(true));
  }
  
  public CameraThread(Context paramContext, BaseThread.ThreadStartCallback paramThreadStartCallback, Handler paramHandler)
  {
    super("Camera Thread", paramThreadStartCallback, paramHandler);
    if (paramContext == null) {
      throw new IllegalArgumentException("No context.");
    }
    this.m_Context = paramContext;
  }
  
  private void bindToHighComponents()
  {
    this.m_FileManager = ((FileManager)this.m_ComponentManager.findComponent(FileManager.class, new Object[] { this }));
    if (this.m_FileManager != null)
    {
      this.m_FileManager.addCallback(FileManager.PROP_IS_SAVING_QUEUE_FULL, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Boolean> paramAnonymousPropertyKey, PropertyChangeEventArgs<Boolean> paramAnonymousPropertyChangeEventArgs)
        {
          CameraThread.-wrap23(CameraThread.this, ((Boolean)paramAnonymousPropertyChangeEventArgs.getNewValue()).booleanValue());
        }
      });
      this.m_FileManager.addHandler(FileManager.EVENT_MEDIA_FILE_SAVED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
        {
          CameraThread.-wrap26(CameraThread.this, CameraThread.EVENT_MEDIA_FILE_SAVED, paramAnonymousMediaEventArgs);
        }
      });
      this.m_FileManager.addHandler(FileManager.EVENT_MEDIA_SAVE_CANCELLED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
        {
          CameraThread.-wrap29(CameraThread.this, paramAnonymousMediaEventArgs);
          CameraThread.-wrap26(CameraThread.this, CameraThread.EVENT_MEDIA_SAVE_CANCELLED, paramAnonymousMediaEventArgs);
        }
      });
      this.m_FileManager.addHandler(FileManager.EVENT_MEDIA_SAVE_FAILED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
        {
          CameraThread.-wrap29(CameraThread.this, paramAnonymousMediaEventArgs);
          CameraThread.-wrap26(CameraThread.this, CameraThread.EVENT_MEDIA_SAVE_FAILED, paramAnonymousMediaEventArgs);
        }
      });
      this.m_FileManager.addHandler(FileManager.EVENT_MEDIA_SAVED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<MediaEventArgs> paramAnonymousEventKey, MediaEventArgs paramAnonymousMediaEventArgs)
        {
          CameraThread.-wrap29(CameraThread.this, paramAnonymousMediaEventArgs);
          CameraThread.-wrap26(CameraThread.this, CameraThread.EVENT_MEDIA_SAVED, paramAnonymousMediaEventArgs);
        }
      });
      this.m_AudioManager = ((com.oneplus.camera.media.AudioManager)this.m_ComponentManager.findComponent(com.oneplus.camera.media.AudioManager.class, new Object[] { this }));
      if (this.m_AudioManager == null) {
        break label420;
      }
      if (this.m_ResourceIdTable != null)
      {
        if (this.m_ResourceIdTable.photoShutterSound != 0) {
          this.m_DefaultShutterSoundHandle = this.m_AudioManager.loadSound(this.m_ResourceIdTable.photoShutterSound, 1, 0);
        }
        if (this.m_ResourceIdTable.cameraTimerSound != 0) {
          this.m_CameraTimerSoundHandle = this.m_AudioManager.loadSound(this.m_ResourceIdTable.cameraTimerSound, 1, 0);
        }
        if (this.m_ResourceIdTable.burstShutterSound == 0) {
          break label409;
        }
      }
    }
    label409:
    for (this.m_DefaultBurstShutterSoundHandle = this.m_AudioManager.loadSound(this.m_ResourceIdTable.burstShutterSound, 1, 0);; this.m_DefaultBurstShutterSoundHandle = this.m_DefaultShutterSoundHandle)
    {
      if (this.m_ResourceIdTable.cameraTimer2SecSound != 0) {
        this.m_CameraTimer2SecSoundHandle = this.m_AudioManager.loadSound(this.m_ResourceIdTable.cameraTimer2SecSound, 1, 0);
      }
      if (this.m_ResourceIdTable.burstShutterSoundEnd != 0) {
        this.m_DefaultBurstShutterSoundEndHandle = this.m_AudioManager.loadSound(this.m_ResourceIdTable.burstShutterSoundEnd, 1, 0);
      }
      if (this.m_ResourceIdTable.videoStartSound != 0) {
        this.m_VideoStartSoundHandle = this.m_AudioManager.loadSound(this.m_ResourceIdTable.videoStartSound, 1, 0);
      }
      if (this.m_ResourceIdTable.videoStopSound != 0) {
        this.m_VideoStopSoundHandle = this.m_AudioManager.loadSound(this.m_ResourceIdTable.videoStopSound, 1, 0);
      }
      return;
      Log.w(this.TAG, "bindToInitialComponents() - No FileManager");
      break;
    }
    label420:
    Log.w(this.TAG, "bindToInitialComponents() - No AudioManager");
  }
  
  private boolean bindToInitialComponents()
  {
    this.m_CameraDeviceManager = ((CameraDeviceManager)this.m_ComponentManager.findComponent(CameraDeviceManager.class, new Object[0]));
    if (this.m_CameraDeviceManager != null)
    {
      this.m_CameraDeviceManager.addCallback(CameraDeviceManager.PROP_AVAILABLE_CAMERAS, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera>> paramAnonymousPropertyChangeEventArgs)
        {
          CameraThread.-wrap10(CameraThread.this, (List)paramAnonymousPropertyChangeEventArgs.getOldValue(), (List)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
      onAvailableCamerasChanged(Collections.EMPTY_LIST, (List)this.m_CameraDeviceManager.get(CameraDeviceManager.PROP_AVAILABLE_CAMERAS));
      return true;
    }
    Log.e(this.TAG, "bindToInitialComponents() - No CameraDeviceManager");
    return false;
  }
  
  private void bindToNormalComponents()
  {
    this.m_StorageManager = ((StorageManager)CameraApplication.current().findComponent(StorageManager.class));
    if (this.m_StorageManager == null)
    {
      Log.w(this.TAG, "bindToNormalComponents - No StorageManager, try later.");
      getHandler().sendEmptyMessageDelayed(10080, 50L);
    }
    this.m_FocusController = ((FocusController)this.m_ComponentManager.findComponent(FocusController.class, new Object[] { this }));
    if (this.m_FocusController != null) {
      this.m_FocusController.addCallback(FocusController.PROP_FOCUS_STATE, new PropertyChangedCallback()
      {
        public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<FocusState> paramAnonymousPropertyKey, PropertyChangeEventArgs<FocusState> paramAnonymousPropertyChangeEventArgs)
        {
          CameraThread.-wrap17(CameraThread.this, (FocusState)paramAnonymousPropertyChangeEventArgs.getNewValue());
        }
      });
    }
    for (;;)
    {
      this.m_LocationManager = ((LocationManager)this.m_ComponentManager.findComponent(LocationManager.class, new Object[] { this }));
      if (this.m_LocationManager == null) {
        Log.w(this.TAG, "bindToNormalComponents() - No LocationManager");
      }
      return;
      Log.w(this.TAG, "bindToNormalComponents() - No FocusController");
    }
  }
  
  private boolean capturePhotoInternal(PhotoCaptureHandle paramPhotoCaptureHandle)
  {
    Camera localCamera = (Camera)get(PROP_CAMERA);
    localCamera.addHandler(Camera.EVENT_CAPTURE_FAILED, this.m_CaptureFailedHandler);
    localCamera.addHandler(Camera.EVENT_PICTURE_RECEIVED, this.m_PictureReceivedHandler);
    localCamera.addHandler(Camera.EVENT_RAW_PICTURE_RECEIVED, this.m_RawPictureReceivedHandler);
    localCamera.addHandler(Camera.EVENT_SHUTTER, this.m_CameraShutterHandler);
    localCamera.addCallback(Camera.PROP_CAPTURE_STATE, this.m_CaptureStateChangedCallback);
    this.m_PreparedPhotoCaptureCompleteReason = CaptureCompleteReason.NORMAL;
    this.m_CameraCaptureHandle = localCamera.capture(paramPhotoCaptureHandle.frameCount, 0);
    if (!Handle.isValid(this.m_CameraCaptureHandle))
    {
      Log.e(this.TAG, "capturePhotoInternal() - Fail to capture");
      localCamera.removeHandler(Camera.EVENT_CAPTURE_FAILED, this.m_CaptureFailedHandler);
      localCamera.removeHandler(Camera.EVENT_PICTURE_RECEIVED, this.m_PictureReceivedHandler);
      localCamera.removeHandler(Camera.EVENT_RAW_PICTURE_RECEIVED, this.m_RawPictureReceivedHandler);
      localCamera.removeHandler(Camera.EVENT_SHUTTER, this.m_CameraShutterHandler);
      localCamera.removeCallback(Camera.PROP_CAPTURE_STATE, this.m_CaptureStateChangedCallback);
      return false;
    }
    if (((paramPhotoCaptureHandle.frameCount != 1) || (localCamera.get(Camera.PROP_AE_STATE) == AutoExposureState.FLASH_REQUIRED) || (localCamera.get(Camera.PROP_FLASH_MODE) == FlashMode.ON) || (localCamera.get(Camera.PROP_AE_STATE) != AutoExposureState.SEARCHING) || (this.m_PreAEState == AutoExposureState.FLASH_REQUIRED)) || (((Boolean)localCamera.get(Camera.PROP_IS_CAPTURING_RAW_PHOTO)).booleanValue())) {
      this.m_RawPhotoCaptureHandleList.add(paramPhotoCaptureHandle);
    }
    return true;
  }
  
  private boolean capturePhotoInternal(PhotoCaptureHandle paramPhotoCaptureHandle, boolean paramBoolean)
  {
    this.m_PhotoCaptureHandle = null;
    this.m_LastCapturedJpeg = null;
    this.m_IsPictureReceived = false;
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    case 3: 
    default: 
    case 4: 
      do
      {
        Log.e(this.TAG, "capturePhotoInternal() - Capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
        setReadOnly(PROP_PHOTO_CAPTURE_COMPLETE_REASON, CaptureCompleteReason.INVALID_STATE);
        return false;
      } while (!paramBoolean);
    }
    if ((this.m_FileManager != null) && (((Boolean)this.m_FileManager.get(FileManager.PROP_IS_SAVING_QUEUE_FULL)).booleanValue()))
    {
      Log.e(this.TAG, "capturePhotoInternal() - Media saving queue is full");
      setReadOnly(PROP_PHOTO_CAPTURE_COMPLETE_REASON, CaptureCompleteReason.SAVING_QUEUE_FULL);
      return false;
    }
    Log.w(this.TAG, "capturePhotoInternal() - Handle : " + paramPhotoCaptureHandle + ", focus finished : " + paramBoolean);
    Camera localCamera = (Camera)get(PROP_CAMERA);
    if (localCamera == null)
    {
      Log.e(this.TAG, "capturePhotoInternal() - No primary camera");
      setReadOnly(PROP_PHOTO_CAPTURE_COMPLETE_REASON, CaptureCompleteReason.INVALID_STATE);
      return false;
    }
    if (get(PROP_MEDIA_TYPE) == MediaType.VIDEO)
    {
      switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
      {
      case 4: 
      case 5: 
      default: 
        Log.e(this.TAG, "capturePhotoInternal() - Video recording not ready , cancel snaphot.");
        setReadOnly(PROP_PHOTO_CAPTURE_COMPLETE_REASON, CaptureCompleteReason.INVALID_STATE);
        return false;
      }
      paramPhotoCaptureHandle.isVideoSnapshot = true;
    }
    setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.STARTING);
    if (!paramBoolean)
    {
      checkRemainingMediaCountInternal();
      if (((Long)get(PROP_REMAINING_PHOTO_COUNT)).longValue() <= 0L)
      {
        Log.e(this.TAG, "capturePhotoInternal() - No enough storage space");
        setReadOnly(PROP_PHOTO_CAPTURE_COMPLETE_REASON, CaptureCompleteReason.STORAGE_FULL);
        if (get(PROP_CAMERA_PREVIEW_STATE) == OperationState.STARTED) {
          setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.READY);
        }
        for (;;)
        {
          return false;
          setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.PREPARING);
        }
      }
    }
    if ((!paramBoolean) && (this.m_FocusController != null) && ((this.m_FocusController.get(FocusController.PROP_FOCUS_STATE) == FocusState.SCANNING) || (this.m_FocusController.get(FocusController.PROP_FOCUS_STATE) == FocusState.STARTING)))
    {
      Log.w(this.TAG, "capturePhotoInternal() - Waiting for focus complete");
      this.m_PhotoCaptureHandle = paramPhotoCaptureHandle;
      return true;
    }
    if ((this.m_FocusController == null) || (this.m_FocusController.get(FocusController.PROP_FOCUS_MODE) != FocusMode.CONTINUOUS_AF) || ((localCamera.get(Camera.PROP_AE_STATE) != AutoExposureState.FLASH_REQUIRED) && ((paramPhotoCaptureHandle.flags & 0x10) == 0)) || (Handle.isValid(this.m_PreCaptureFocusLockHandle))) {}
    for (;;)
    {
      try
      {
        paramPhotoCaptureHandle.captureRealTime = SystemClock.elapsedRealtime();
        localCamera.set(Camera.PROP_PICTURE_ROTATION, (Rotation)get(PROP_CAPTURE_ROTATION));
        if (this.m_LocationManager != null)
        {
          localObject2 = (Location)this.m_LocationManager.get(LocationManager.PROP_LOCATION);
          localObject1 = localObject2;
          if (localObject2 != null)
          {
            localObject1 = new Location((Location)localObject2);
            long l = ((Location)localObject1).getTime() + (paramPhotoCaptureHandle.captureRealTime - ((Location)localObject1).getElapsedRealtimeNanos() / 1000000L);
            ((Location)localObject1).setTime(l);
            ((Location)localObject1).setElapsedRealtimeNanos(paramPhotoCaptureHandle.captureRealTime * 1000000L);
            Log.v(this.TAG, "capturePhotoInternal() - Fixed location time: ", Long.valueOf(l));
          }
          localCamera.set(Camera.PROP_LOCATION, localObject1);
        }
        if (localCamera.get(Camera.PROP_LENS_FACING) == Camera.LensFacing.FRONT)
        {
          paramBoolean = this.m_Settings.getBoolean("IsMirrored");
          localCamera.set(Camera.PROP_IS_MIRRORED, Boolean.valueOf(paramBoolean));
          paramPhotoCaptureHandle.setIsMirrored(paramBoolean);
        }
        i = this.m_PhotoCaptureHandlerHandles.size() - 1;
        Object localObject1 = null;
        if (i >= 0)
        {
          localObject1 = (PhotoCaptureHandlerHandle)this.m_PhotoCaptureHandlerHandles.get(i);
          if (!((PhotoCaptureHandlerHandle)localObject1).captureHandler.capture(localCamera, paramPhotoCaptureHandle, paramPhotoCaptureHandle.frameCount)) {
            break label1018;
          }
          Log.w(this.TAG, "capturePhotoInternal() - Capture process is handled by " + ((PhotoCaptureHandlerHandle)localObject1).captureHandler);
          paramPhotoCaptureHandle.captureHandler = ((PhotoCaptureHandlerHandle)localObject1).captureHandler;
          ((PhotoCaptureHandlerHandle)localObject1).captureHandler.addHandler(PhotoCaptureHandler.EVENT_SHUTTER, this.m_ShutterHandler);
        }
        localObject2 = localObject1;
        if (localObject1 != null) {
          break label1031;
        }
        Log.v(this.TAG, "capturePhotoInternal() - Use default capture process");
        if (capturePhotoInternal(paramPhotoCaptureHandle)) {
          break;
        }
        throw new RuntimeException("Fail to use default photo capture process.");
      }
      catch (Throwable paramPhotoCaptureHandle)
      {
        Log.e(this.TAG, "capturePhotoInternal() - Fail to capture", paramPhotoCaptureHandle);
        setReadOnly(PROP_PHOTO_CAPTURE_COMPLETE_REASON, CaptureCompleteReason.UNKNOWN_ERROR);
        if (get(PROP_CAMERA_PREVIEW_STATE) != OperationState.STARTED) {
          break label1124;
        }
      }
      setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.READY);
      return false;
      Log.w(this.TAG, "capturePhotoInternal() - Lock focus");
      if (((Boolean)this.m_FocusController.get(FocusController.PROP_IS_FOCUS_LOCKED)).booleanValue()) {}
      for (int i = 0;; i = 1)
      {
        this.m_PreCaptureFocusLockHandle = this.m_FocusController.lockFocus(0);
        if (i == 0) {
          break;
        }
        Log.w(this.TAG, "capturePhotoInternal() - Waiting for focus lock");
        this.m_PhotoCaptureHandle = paramPhotoCaptureHandle;
        return true;
      }
      label1018:
      i -= 1;
    }
    Object localObject2 = this.m_DefaultPhotoCaptureHandlerHandle;
    label1031:
    this.m_PhotoCaptureHandlerHandle = ((PhotoCaptureHandlerHandle)localObject2);
    this.m_PhotoCaptureHandle = paramPhotoCaptureHandle;
    this.m_IsDefaultShutterReceived = false;
    setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.CAPTURING);
    raise(EVENT_CAPTURE_STARTED, new CaptureEventArgs(this.m_PhotoCaptureHandle));
    if (((Boolean)localCamera.get(Camera.PROP_IS_CAPTURING_RAW_PHOTO)).booleanValue()) {
      setReadOnly(PROP_IS_CAPTURING_RAW_PHOTO, Boolean.valueOf(true));
    }
    if ((paramPhotoCaptureHandle.frameCount == 1) || (this.m_IsMediaStoreUpdatePaused)) {}
    for (;;)
    {
      return true;
      label1124:
      setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.PREPARING);
      break;
      if (this.m_FileManager != null)
      {
        this.m_IsMediaStoreUpdatePaused = true;
        this.m_FileManager.pauseInsert();
      }
    }
  }
  
  private boolean captureVideoInternal(VideoCaptureHandle paramVideoCaptureHandle, boolean paramBoolean)
  {
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    case 6: 
    case 7: 
    default: 
    case 8: 
      do
      {
        Log.e(this.TAG, "captureVideoInternal() - Video capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
        return false;
      } while (!paramBoolean);
    }
    Camera localCamera = (Camera)get(PROP_CAMERA);
    if (localCamera == null)
    {
      Log.e(this.TAG, "captureVideoInternal() - No primary camera");
      return false;
    }
    VideoParams localVideoParams = paramVideoCaptureHandle.params;
    Log.v(this.TAG, "captureVideoInternal() - Handle : ", paramVideoCaptureHandle, ", resolution : ", localVideoParams.resolution, ", shutter sound played : ", Boolean.valueOf(paramBoolean));
    if (!paramBoolean)
    {
      checkRemainingMediaCountInternal();
      if (((Long)get(PROP_REMAINING_VIDEO_DURATION_SECONDS)).longValue() <= 0L)
      {
        Log.e(this.TAG, "captureVideoInternal() - No enough storage space");
        setReadOnly(PROP_VIDEO_CAPTURE_COMPLETE_REASON, CaptureCompleteReason.STORAGE_FULL);
        return false;
      }
      if (!prepareVideoFilePath()) {
        return false;
      }
    }
    setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.STARTING);
    long l2 = 0L;
    long l1 = l2;
    if (!paramBoolean)
    {
      l1 = l2;
      if (Handle.isValid(this.m_VideoStartSoundHandle))
      {
        l1 = l2;
        if (isShutterSoundNeeded())
        {
          this.m_AudioManager.playSound(this.m_VideoStartSoundHandle, 0);
          l1 = SystemClock.elapsedRealtime();
        }
      }
    }
    if (!paramBoolean)
    {
      localCamera.set(Camera.PROP_VIDEO_SIZE, localVideoParams.resolution.toSize());
      MediaRecorder localMediaRecorder = new MediaRecorder();
      if (!localCamera.bindMediaRecorder(localMediaRecorder, 0))
      {
        setReadOnly(PROP_VIDEO_CAPTURE_COMPLETE_REASON, CaptureCompleteReason.UNKNOWN_ERROR);
        setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.READY);
        return false;
      }
      if (!prepareMediaRecorder(localCamera, localMediaRecorder, localVideoParams))
      {
        Log.e(this.TAG, "captureVideoInternal() - Fail to prepare media recorder");
        try
        {
          localMediaRecorder.release();
        }
        catch (Throwable paramVideoCaptureHandle)
        {
          for (;;)
          {
            Log.e(this.TAG, "captureVideoInternal() - Error when release", paramVideoCaptureHandle);
            localCamera.unbindMediaRecorder(0);
          }
        }
        finally
        {
          localCamera.unbindMediaRecorder(0);
        }
        setReadOnly(PROP_VIDEO_CAPTURE_COMPLETE_REASON, CaptureCompleteReason.UNKNOWN_ERROR);
        setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.READY);
        return false;
      }
      this.m_MediaRecorder = localMediaRecorder;
      l1 = 300L - (SystemClock.elapsedRealtime() - l1);
      if (l1 > 0L)
      {
        Log.w(this.TAG, "captureVideoInternal() - Start video recording " + l1 + " ms later");
        HandlerUtils.sendMessage(this, 10010, 0, 0, null, l1);
        this.m_VideoCaptureHandle = paramVideoCaptureHandle;
        return true;
      }
    }
    paramVideoCaptureHandle.captureRealTime = SystemClock.elapsedRealtime();
    if (!localCamera.startVideoRecording(0))
    {
      paramVideoCaptureHandle = CaptureCompleteReason.UNKNOWN_ERROR;
      int i = ((android.media.AudioManager)getContext().getSystemService("audio")).getMode();
      switch (i)
      {
      }
      for (;;)
      {
        Log.e(this.TAG, "captureVideoInternal() - Fail to start, reason : " + paramVideoCaptureHandle + ", audio mode : " + i);
        this.m_MediaRecorder.release();
        localCamera.unbindMediaRecorder(0);
        this.m_VideoCaptureHandle = null;
        closeVideoFileDescriptor();
        setReadOnly(PROP_VIDEO_CAPTURE_COMPLETE_REASON, paramVideoCaptureHandle);
        setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.READY);
        return false;
        paramVideoCaptureHandle = CaptureCompleteReason.ERROR_IN_CALL;
        continue;
        paramVideoCaptureHandle = CaptureCompleteReason.ERROR_IN_COMMUNICATION;
      }
    }
    this.m_MediaRecorderStartTime = SystemClock.elapsedRealtime();
    this.m_MediaRecorderPauseTimeTotal = 0L;
    if (this.m_VideoCaptureHandlerHandle == null) {
      this.m_VideoCaptureHandlerHandle = this.m_DefaultVideoCaptureHandlerHandle;
    }
    this.m_VideoCaptureHandle = paramVideoCaptureHandle;
    this.m_VideoCaptureHandle.captureHandler = this.m_VideoCaptureHandlerHandle.captureHandler;
    setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.CAPTURING);
    raise(EVENT_CAPTURE_STARTED, new CaptureEventArgs(this.m_VideoCaptureHandle));
    checkVideoRecordingLimitation();
    return true;
  }
  
  private void checkRemainingMediaCountInternal()
  {
    getHandler().removeMessages(10020);
    long l = getFreeSpace();
    checkRemainingPhotoCount(l);
    checkRemainingVideoDuration(l);
  }
  
  private void checkRemainingPhotoCount(long paramLong)
  {
    paramLong = getFreeSpace(paramLong, MediaType.PHOTO);
    if (paramLong <= 0L)
    {
      Log.w(this.TAG, "checkRemainingPhotoCount() - Storage is full");
      setReadOnly(PROP_REMAINING_PHOTO_COUNT, Long.valueOf(0L));
      return;
    }
    Camera localCamera = (Camera)get(PROP_CAMERA);
    if (localCamera == null)
    {
      Log.w(this.TAG, "checkRemainingPhotoCount() - No primary camera");
      setReadOnly(PROP_REMAINING_PHOTO_COUNT, Long.valueOf(0L));
      return;
    }
    long l = FileSizeEstimator.estimateJpegFileSize((Size)localCamera.get(Camera.PROP_PICTURE_SIZE), ((Integer)localCamera.get(Camera.PROP_JPEG_QUALITY)).intValue());
    setReadOnly(PROP_REMAINING_PHOTO_COUNT, Long.valueOf(paramLong / l));
  }
  
  private void checkRemainingVideoDuration(long paramLong)
  {
    paramLong = getFreeSpace(paramLong, MediaType.VIDEO);
    if (paramLong <= 0L)
    {
      Log.w(this.TAG, "checkRemainingVideoDuration() - Storage is full");
      setReadOnly(PROP_REMAINING_VIDEO_DURATION_SECONDS, Long.valueOf(0L));
      return;
    }
    Object localObject = (Camera)get(PROP_CAMERA);
    if (localObject == null)
    {
      Log.w(this.TAG, "checkRemainingVideoDuration() - No primary camera");
      setReadOnly(PROP_REMAINING_VIDEO_DURATION_SECONDS, Long.valueOf(0L));
      return;
    }
    localObject = (Size)((Camera)localObject).get(Camera.PROP_VIDEO_SIZE);
    localObject = createCamcorderProfile(((Size)localObject).getWidth(), ((Size)localObject).getHeight());
    if (localObject == null)
    {
      Log.w(this.TAG, "checkRemainingVideoDuration() - Cannot create camcorder profile");
      setReadOnly(PROP_REMAINING_VIDEO_DURATION_SECONDS, Long.valueOf(0L));
      return;
    }
    long l = FileSizeEstimator.estimateVideoFileSize((CamcorderProfile)localObject, 1L);
    setReadOnly(PROP_REMAINING_VIDEO_DURATION_SECONDS, Long.valueOf(paramLong / l));
  }
  
  private void checkVideoRecordingLimitation()
  {
    if (get(PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.CAPTURING) {
      return;
    }
    checkRemainingMediaCountInternal();
    long l = ((Long)get(PROP_REMAINING_VIDEO_DURATION_SECONDS)).longValue();
    if (l > 15L)
    {
      getHandler().sendEmptyMessageDelayed(10030, l / 5L * 1000L);
      return;
    }
    if (l > 10L)
    {
      getHandler().sendEmptyMessageDelayed(10030, 500L);
      return;
    }
    if (l > 0L)
    {
      getHandler().sendEmptyMessageDelayed(10030, 100L);
      return;
    }
    Log.w(this.TAG, "checkVideoRecordingLimitation() - Storage limitation reached");
    stopVideoCaptureInternal(this.m_VideoCaptureHandle, CaptureCompleteReason.STORAGE_FULL, 0);
  }
  
  private void closeCameraInternal(Camera paramCamera)
  {
    Log.w(this.TAG, "closeCameraInternal() - Start");
    Log.v(this.TAG, "closeCameraInternal() - Camera : ", paramCamera);
    paramCamera.close(0);
    Log.w(this.TAG, "closeCameraInternal() - End");
  }
  
  private void closeCamerasInternal()
  {
    Log.w(this.TAG, "closeCamerasInternal() - Start");
    List localList = (List)get(PROP_AVAILABLE_CAMERAS);
    int i = localList.size() - 1;
    while (i >= 0)
    {
      ((Camera)localList.get(i)).close(0);
      i -= 1;
    }
    Log.w(this.TAG, "closeCamerasInternal() - End");
  }
  
  private void closeVideoFileDescriptor()
  {
    if (this.m_VideoFileDescriptor != null) {}
    try
    {
      this.m_VideoFileDescriptor.close();
      this.m_VideoFileDescriptor = null;
      return;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Log.e(this.TAG, "closeVideoFileDescriptor() - Error, ", localIOException);
      }
    }
  }
  
  private boolean completeCaptureInternal(Handle paramHandle, CaptureHandle paramCaptureHandle, CaptureCompleteReason paramCaptureCompleteReason, boolean paramBoolean)
  {
    if (paramCaptureHandle == null)
    {
      Log.w(this.TAG, "completeCaptureInternal() - Capture handle is null, skip");
      return false;
    }
    Log.w(this.TAG, "completeCaptureInternal() - Handle : " + paramCaptureHandle);
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[paramCaptureHandle.getMediaType().ordinal()])
    {
    default: 
    case 1: 
      for (;;)
      {
        if (this.m_IsMediaStoreUpdatePaused)
        {
          this.m_IsMediaStoreUpdatePaused = false;
          this.m_FileManager.resumeInsert();
        }
        return true;
        if (paramBoolean)
        {
          if (this.m_PhotoCaptureHandlerHandle != paramHandle)
          {
            Log.e(this.TAG, "completeCaptureInternal() - Invalid capture handler handle : " + paramHandle);
            return false;
          }
          if (paramCaptureHandle != this.m_PhotoCaptureHandle)
          {
            Log.e(this.TAG, "completeCaptureInternal() - Invalid capture handle : " + paramCaptureHandle);
            return false;
          }
        }
        if ((this.m_PhotoCaptureHandle.isBurstPhotoCapture()) && (Handle.isValid(this.m_BurstShutterPlaySoundHandle)) && (isShutterSoundNeeded()) && (Handle.isValid(this.m_DefaultBurstShutterSoundEndHandle)) && (this.m_AudioManager != null))
        {
          Handle.close(this.m_BurstShutterPlaySoundHandle);
          this.m_AudioManager.playSound(this.m_DefaultBurstShutterSoundEndHandle, 0);
        }
        setReadOnly(PROP_PHOTO_CAPTURE_COMPLETE_REASON, paramCaptureCompleteReason);
        if (this.m_PhotoCaptureHandle.captureHandler == null) {
          raise(EVENT_DEFAULT_PHOTO_CAPTURE_COMPLETED, new CaptureEventArgs(this.m_PhotoCaptureHandle));
        }
        if ((this.m_PhotoCaptureHandlerHandle != null) && (this.m_PhotoCaptureHandlerHandle.captureHandler != null)) {
          this.m_PhotoCaptureHandlerHandle.captureHandler.removeHandler(PhotoCaptureHandler.EVENT_SHUTTER, this.m_ShutterHandler);
        }
        if (get(PROP_MEDIA_TYPE) == MediaType.VIDEO)
        {
          Log.w(this.TAG, "completeCaptureInternal() - Complete video snapshot");
          if (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.STOPPING) {
            stopVideoCaptureInternal(this.m_VideoCaptureHandle, this.m_LastVideoCaptureCompleteReason, 0);
          }
        }
        if (get(PROP_CAMERA_PREVIEW_STATE) == OperationState.STARTED) {
          setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.READY);
        }
        for (;;)
        {
          this.m_PreCaptureFocusLockHandle = Handle.close(this.m_PreCaptureFocusLockHandle);
          if (this.m_PendingCameraPreviewStopRequests.isEmpty()) {
            break;
          }
          int i = this.m_PendingCameraPreviewStopRequests.size() - 1;
          while (i >= 0)
          {
            paramHandle = (CameraPreviewStopRequest)this.m_PendingCameraPreviewStopRequests.get(i);
            if (paramHandle.camera == get(PROP_CAMERA))
            {
              Log.w(this.TAG, "completeCaptureInternal() - m_PendingCameraPreviewStopRequests is not empty, Continue stopping preview for " + get(PROP_CAMERA));
              this.m_PendingCameraPreviewStopRequests.remove(i);
              stopCameraPreviewInternal((Camera)get(PROP_CAMERA), paramHandle.result, paramHandle.flags);
            }
            i -= 1;
          }
          setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.PREPARING);
        }
        if ((this.m_PhotoCaptureHandle.flags & 0x4) != 0)
        {
          Log.v(this.TAG, "completeCaptureInternal() - Need review, stop preview");
          paramHandle = (Camera)get(PROP_CAMERA);
          if (paramHandle != null) {
            paramHandle.stopPreview(0);
          }
        }
        this.m_PhotoCaptureHandle = null;
        this.m_PhotoCaptureHandlerHandle = null;
      }
    }
    if (paramBoolean)
    {
      if (this.m_VideoCaptureHandlerHandle != paramHandle)
      {
        Log.e(this.TAG, "completeCaptureInternal() - Invalid capture handler handle : " + paramHandle);
        return false;
      }
      if (paramCaptureHandle != this.m_VideoCaptureHandle)
      {
        Log.e(this.TAG, "completeCaptureInternal() - Invalid capture handle : " + paramCaptureHandle);
        return false;
      }
    }
    setReadOnly(PROP_VIDEO_CAPTURE_COMPLETE_REASON, paramCaptureCompleteReason);
    raise(EVENT_DEFAULT_VIDEO_CAPTURE_COMPLETED, new CaptureEventArgs(paramCaptureHandle));
    if (this.m_VideoFileDescriptor != null)
    {
      paramHandle = (MediaResultInfo)get(PROP_MEDIA_RESULT_INFO);
      raise(EVENT_MEDIA_SAVED, new MediaEventArgs(paramCaptureHandle, null, 0, null, paramHandle.extraOutput, null));
      closeVideoFileDescriptor();
    }
    if ((get(PROP_CAMERA_PREVIEW_STATE) == OperationState.STARTED) && (get(PROP_MEDIA_TYPE) == MediaType.VIDEO)) {
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.READY);
    }
    for (;;)
    {
      if ((this.m_VideoCaptureHandle.flags & 0x4) != 0)
      {
        Log.v(this.TAG, "completeCaptureInternal() - Need review, stop preview");
        paramHandle = (Camera)get(PROP_CAMERA);
        if (paramHandle.get(Camera.PROP_FLASH_MODE) != FlashMode.OFF) {
          paramHandle.set(Camera.PROP_FLASH_MODE, FlashMode.OFF);
        }
        if (paramHandle != null) {
          paramHandle.stopPreview(0);
        }
      }
      this.m_VideoCaptureHandle = null;
      this.m_VideoCaptureHandlerHandle = null;
      break;
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.PREPARING);
    }
  }
  
  private CamcorderProfile createCamcorderProfile(int paramInt1, int paramInt2)
  {
    switch (paramInt2)
    {
    }
    do
    {
      do
      {
        do
        {
          do
          {
            Log.w(this.TAG, "createCamcorderProfile() - Unknown video size : " + paramInt1 + "x" + paramInt2);
            return null;
          } while (paramInt1 != 3840);
          return CamcorderProfile.get(8);
        } while (paramInt1 != 1920);
        return CamcorderProfile.get(6);
      } while (paramInt1 != 1280);
      return CamcorderProfile.get(5);
    } while (paramInt1 != 176);
    return CamcorderProfile.get(1002);
  }
  
  private void enableVideoSnapshot(Handle paramHandle)
  {
    verifyAccess();
    if ((this.m_VideoSnapshotDisableHandles.remove(paramHandle)) && (this.m_VideoSnapshotDisableHandles.isEmpty()) && (get(PROP_MEDIA_TYPE) == MediaType.VIDEO)) {
      setReadOnly(PROP_IS_VIDEO_SNAPSHOT_ENABLED, Boolean.valueOf(true));
    }
  }
  
  private long getFreeSpace()
  {
    label120:
    for (;;)
    {
      try
      {
        Object localObject = StorageUtils.findStorageFromSettings(this.m_StorageManager, this.m_Settings, Storage.Type.INTERNAL);
        if (localObject != null)
        {
          localObject = ((Storage)localObject).getDirectoryPath();
          if (this.m_RemainingMediaCountStateFs != null)
          {
            this.m_RemainingMediaCountStateFs.restat((String)localObject);
            if (this.m_FileManager == null) {
              break label120;
            }
            l = ((Long)this.m_FileManager.get(FileManager.PROP_SAVING_QUEUE_SIZE)).longValue();
            return Math.max(0L, this.m_RemainingMediaCountStateFs.getFreeBytes() - l);
          }
        }
        else
        {
          localObject = Environment.getExternalStorageDirectory().getAbsolutePath();
          continue;
        }
        this.m_RemainingMediaCountStateFs = new StatFs((String)localObject);
        continue;
        long l = 0L;
      }
      catch (Throwable localThrowable)
      {
        Log.e(this.TAG, "getFreeSpace() - Fail to get free storage space", localThrowable);
        return 0L;
      }
    }
  }
  
  private long getFreeSpace(long paramLong, MediaType paramMediaType)
  {
    if (paramMediaType == null) {
      return paramLong;
    }
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[paramMediaType.ordinal()])
    {
    default: 
      return paramLong;
    case 1: 
      return paramLong - 104857600L;
    }
    return paramLong - 125829120L;
  }
  
  private long getFreeSpace(MediaType paramMediaType)
  {
    return getFreeSpace(getFreeSpace(), paramMediaType);
  }
  
  private void onAvailableCamerasChanged(List<Camera> paramList1, List<Camera> paramList2)
  {
    int i = paramList2.size() - 1;
    Camera localCamera;
    while (i >= 0)
    {
      localCamera = (Camera)paramList2.get(i);
      if (!paramList1.contains(localCamera))
      {
        localCamera.addCallback(Camera.PROP_IS_PREVIEW_RECEIVED, this.m_CameraPreviewReceivedChangedCallback);
        localCamera.addCallback(Camera.PROP_PREVIEW_STATE, this.m_CameraPreviewStateChangedCallback);
        localCamera.addCallback(Camera.PROP_AE_STATE, this.m_AECallback);
        localCamera.addHandler(Camera.EVENT_ERROR, this.m_CameraErrorHandler);
        localCamera.addHandler(Camera.EVENT_OPEN_FAILED, this.m_CameraOpenFailedHandler);
        localCamera.addHandler(Camera.EVENT_POSTVIEW_RECEIVED, this.m_PostviewReceivedHandler);
      }
      i -= 1;
    }
    i = paramList1.size() - 1;
    while (i >= 0)
    {
      localCamera = (Camera)paramList1.get(i);
      if (!paramList2.contains(localCamera))
      {
        localCamera.removeCallback(Camera.PROP_IS_PREVIEW_RECEIVED, this.m_CameraPreviewReceivedChangedCallback);
        localCamera.removeCallback(Camera.PROP_PREVIEW_STATE, this.m_CameraPreviewStateChangedCallback);
        localCamera.removeCallback(Camera.PROP_AE_STATE, this.m_AECallback);
        localCamera.removeHandler(Camera.EVENT_ERROR, this.m_CameraErrorHandler);
        localCamera.removeHandler(Camera.EVENT_OPEN_FAILED, this.m_CameraOpenFailedHandler);
        localCamera.removeHandler(Camera.EVENT_POSTVIEW_RECEIVED, this.m_PostviewReceivedHandler);
      }
      i -= 1;
    }
    setReadOnly(PROP_AVAILABLE_CAMERAS, paramList2);
  }
  
  private void onCameraError(Camera paramCamera)
  {
    if (get(PROP_CAMERA) == paramCamera)
    {
      Log.e(this.TAG, "onCameraError() - Camera : " + paramCamera);
      raise(EVENT_CAMERA_ERROR, new CameraEventArgs(paramCamera));
    }
  }
  
  private void onCameraOpenFailed(CameraOpenFailedEventArgs paramCameraOpenFailedEventArgs)
  {
    Log.e(this.TAG, "onCameraOpenFailed() - Error code: " + paramCameraOpenFailedEventArgs.getErrorCode());
    raise(EVENT_CAMERA_OPEN_FAILED, paramCameraOpenFailedEventArgs);
  }
  
  private void onCameraPreviewReceivedStateChanged(Camera paramCamera, boolean paramBoolean)
  {
    if (get(PROP_CAMERA) != paramCamera) {
      return;
    }
    setReadOnly(PROP_IS_CAMERA_PREVIEW_RECEIVED, Boolean.valueOf(paramBoolean));
  }
  
  private void onCameraPreviewStateChanged(Camera paramCamera, OperationState paramOperationState1, OperationState paramOperationState2)
  {
    int i;
    if (paramOperationState2 == OperationState.STARTED)
    {
      i = this.m_PendingCameraPreviewStopRequests.size() - 1;
      while (i >= 0)
      {
        paramOperationState1 = (CameraPreviewStopRequest)this.m_PendingCameraPreviewStopRequests.get(i);
        if (paramOperationState1.camera == paramCamera)
        {
          Log.w(this.TAG, "onCameraPreviewStateChanged() - Continue stopping preview for " + paramCamera);
          this.m_PendingCameraPreviewStopRequests.remove(i);
          stopCameraPreviewInternal(paramCamera, paramOperationState1.result, paramOperationState1.flags);
        }
        i -= 1;
      }
      if (paramCamera.get(Camera.PROP_PREVIEW_STATE) != paramOperationState2) {
        return;
      }
    }
    if (paramOperationState2 == OperationState.STOPPED)
    {
      i = this.m_PendingCameraPreviewStartRequests.size() - 1;
      while (i >= 0)
      {
        paramOperationState1 = (CameraPreviewStartRequest)this.m_PendingCameraPreviewStartRequests.get(i);
        if (paramOperationState1.camera == paramCamera)
        {
          Log.w(this.TAG, "onCameraPreviewStateChanged() - Continue starting preview for " + paramCamera);
          this.m_PendingCameraPreviewStartRequests.remove(i);
          startCameraPreviewInternal(paramCamera, paramOperationState1.previewSize, paramOperationState1.resolution, paramOperationState1.previewReceiver, paramOperationState1.flags);
        }
        i -= 1;
      }
      if (paramCamera.get(Camera.PROP_PREVIEW_STATE) != paramOperationState2) {
        return;
      }
    }
    if (get(PROP_CAMERA) != paramCamera) {
      return;
    }
    setReadOnly(PROP_CAMERA_PREVIEW_STATE, paramOperationState2);
    if ((this.m_VideoCaptureHandle == null) && (this.m_MediaRecorder != null) && ((paramOperationState2 == OperationState.STARTED) || (paramOperationState2 == OperationState.STOPPED)))
    {
      Log.v(this.TAG, "onCameraPreviewStateChanged() - Release media recorder");
      this.m_MediaRecorder.release();
      this.m_MediaRecorder = null;
    }
    if ((this.m_CameraPreviewStartFlags & 0x8) == 0) {
      checkRemainingMediaCountInternal();
    }
    if (paramOperationState2 == OperationState.STARTED)
    {
      if (get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.PREPARING) {
        setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.READY);
      }
      if ((get(PROP_MEDIA_TYPE) == MediaType.VIDEO) && (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.PREPARING)) {
        setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.READY);
      }
    }
    do
    {
      return;
      if (get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.READY) {
        setReadOnly(PROP_PHOTO_CAPTURE_STATE, PhotoCaptureState.PREPARING);
      }
    } while (get(PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.READY);
    setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.PREPARING);
  }
  
  private void onCaptureCompleted(Camera paramCamera)
  {
    paramCamera.removeHandler(Camera.EVENT_CAPTURE_FAILED, this.m_CaptureFailedHandler);
    paramCamera.removeHandler(Camera.EVENT_PICTURE_RECEIVED, this.m_PictureReceivedHandler);
    paramCamera.removeHandler(Camera.EVENT_RAW_PICTURE_RECEIVED, this.m_RawPictureReceivedHandler);
    paramCamera.removeHandler(Camera.EVENT_SHUTTER, this.m_CameraShutterHandler);
    paramCamera.removeCallback(Camera.PROP_CAPTURE_STATE, this.m_CaptureStateChangedCallback);
    Handle.close(this.m_BurstCaptureSoundStreamHandle);
    getHandler().removeMessages(10060);
    this.m_CameraCaptureHandle = null;
    PhotoCaptureHandlerHandle localPhotoCaptureHandlerHandle;
    PhotoCaptureHandle localPhotoCaptureHandle;
    if ((this.m_IsPictureReceived) && (((Boolean)paramCamera.get(Camera.PROP_IS_CAPTURING_RAW_PHOTO)).booleanValue()))
    {
      if (this.m_RawPhotoCaptureHandleList.isEmpty()) {
        setReadOnly(PROP_IS_CAPTURING_RAW_PHOTO, Boolean.valueOf(false));
      }
      localPhotoCaptureHandlerHandle = this.m_DefaultPhotoCaptureHandlerHandle;
      localPhotoCaptureHandle = this.m_PhotoCaptureHandle;
      if (this.m_PreparedPhotoCaptureCompleteReason == null) {
        break label185;
      }
    }
    label185:
    for (paramCamera = this.m_PreparedPhotoCaptureCompleteReason;; paramCamera = CaptureCompleteReason.NORMAL)
    {
      completeCapture(localPhotoCaptureHandlerHandle, localPhotoCaptureHandle, paramCamera);
      return;
      this.m_RawPhotoCaptureHandleList.remove(this.m_PhotoCaptureHandle);
      break;
    }
  }
  
  private void onCaptureFailed(CameraCaptureEventArgs paramCameraCaptureEventArgs) {}
  
  private void onFocusStateChanged(FocusState paramFocusState)
  {
    if ((paramFocusState != FocusState.STARTING) && (paramFocusState != FocusState.SCANNING) && (get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.STARTING) && (Handle.isValid(this.m_PhotoCaptureHandle)))
    {
      Log.w(this.TAG, "onFocusStateChanged() - Continue capturing photo");
      capturePhotoInternal(this.m_PhotoCaptureHandle, true);
    }
  }
  
  private void onMediaRecorderErrorReceived(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2)
  {
    if (this.m_MediaRecorder != paramMediaRecorder)
    {
      Log.w(this.TAG, "onMediaRecorderErrorReceived() - Unknown media recorder");
      return;
    }
    switch (paramInt1)
    {
    default: 
      Log.e(this.TAG, "onMediaRecorderErrorReceived() - What : " + paramInt1 + ", extra : " + paramInt2);
    }
    for (;;)
    {
      stopVideoCaptureInternal(this.m_VideoCaptureHandle, CaptureCompleteReason.UNKNOWN_ERROR, 0);
      return;
      Log.e(this.TAG, "onMediaRecorderErrorReceived() - What : MEDIA_ERROR_SERVER_DIED, extra : " + paramInt2);
      continue;
      Log.e(this.TAG, "onMediaRecorderErrorReceived() - What : MEDIA_RECORDER_ERROR_UNKNOWN, extra : " + paramInt2);
    }
  }
  
  private void onMediaRecorderInfoReceived(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2)
  {
    if (this.m_MediaRecorder != paramMediaRecorder)
    {
      Log.w(this.TAG, "onMediaRecorderInfoReceived() - Unknown media recorder");
      return;
    }
    switch (paramInt1)
    {
    default: 
      Log.w(this.TAG, "onMediaRecorderInfoReceived() - What : " + paramInt1 + ", extra : " + paramInt2);
      return;
    case 800: 
      Log.w(this.TAG, "onMediaRecorderInfoReceived() - Max duration reached");
      stopVideoCaptureInternal(this.m_VideoCaptureHandle, CaptureCompleteReason.MAX_DURATION_REACHED, 0);
      return;
    }
    Log.w(this.TAG, "onMediaRecorderInfoReceived() - Max file size reached");
    if (this.m_IsMaxFileSizeDesignated)
    {
      stopVideoCaptureInternal(this.m_VideoCaptureHandle, CaptureCompleteReason.MAX_FILE_SIZE_REACHED, 0);
      return;
    }
    stopVideoCaptureInternal(this.m_VideoCaptureHandle, CaptureCompleteReason.STORAGE_FULL, 0);
  }
  
  /* Error */
  private void onPictureReceived(CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   4: ldc_w 1900
    //   7: aload_1
    //   8: invokevirtual 1903	com/oneplus/camera/CameraCaptureEventArgs:getFrameIndex	()I
    //   11: invokestatic 1906	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   14: ldc_w 1908
    //   17: aload_1
    //   18: invokevirtual 1911	com/oneplus/camera/CameraCaptureEventArgs:getPictureId	()Ljava/lang/String;
    //   21: invokestatic 1914	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)V
    //   24: aload_0
    //   25: iconst_1
    //   26: putfield 1253	com/oneplus/camera/CameraThread:m_IsPictureReceived	Z
    //   29: aload_1
    //   30: invokevirtual 1917	com/oneplus/camera/CameraCaptureEventArgs:getPictureFormat	()I
    //   33: sipush 256
    //   36: if_icmpne +297 -> 333
    //   39: aload_1
    //   40: invokevirtual 1921	com/oneplus/camera/CameraCaptureEventArgs:getPicturePlanes	()[Lcom/oneplus/camera/media/ImagePlane;
    //   43: astore_3
    //   44: aload_3
    //   45: ifnull +530 -> 575
    //   48: aload_3
    //   49: arraylength
    //   50: iconst_1
    //   51: if_icmpne +524 -> 575
    //   54: aload_0
    //   55: aload_3
    //   56: iconst_0
    //   57: aaload
    //   58: invokevirtual 1927	com/oneplus/camera/media/ImagePlane:getData	()[B
    //   61: putfield 1251	com/oneplus/camera/CameraThread:m_LastCapturedJpeg	[B
    //   64: aload_0
    //   65: getfield 1251	com/oneplus/camera/CameraThread:m_LastCapturedJpeg	[B
    //   68: ifnull +265 -> 333
    //   71: invokestatic 1932	com/oneplus/base/Device:isOnePlus	()Z
    //   74: ifeq +259 -> 333
    //   77: new 1934	java/io/ByteArrayInputStream
    //   80: dup
    //   81: aload_0
    //   82: getfield 1251	com/oneplus/camera/CameraThread:m_LastCapturedJpeg	[B
    //   85: invokespecial 1937	java/io/ByteArrayInputStream:<init>	([B)V
    //   88: invokestatic 1943	com/oneplus/media/ImageUtils:readPhotoMetadata	(Ljava/io/InputStream;)Lcom/oneplus/media/PhotoMetadata;
    //   91: astore 4
    //   93: ldc_w 1945
    //   96: astore_3
    //   97: aload 4
    //   99: getstatic 1950	com/oneplus/media/PhotoMetadata:PROP_MAKER_NOTE	Lcom/oneplus/base/PropertyKey;
    //   102: invokeinterface 1951 2 0
    //   107: checkcast 1952	[B
    //   110: astore 9
    //   112: aload_3
    //   113: astore 4
    //   115: aload 9
    //   117: ifnull +172 -> 289
    //   120: aconst_null
    //   121: astore 5
    //   123: aconst_null
    //   124: astore 6
    //   126: aconst_null
    //   127: astore 7
    //   129: aconst_null
    //   130: astore 8
    //   132: new 1954	com/oneplus/media/IfdEntryEnumerator
    //   135: dup
    //   136: new 1934	java/io/ByteArrayInputStream
    //   139: dup
    //   140: aload 9
    //   142: invokespecial 1937	java/io/ByteArrayInputStream:<init>	([B)V
    //   145: lconst_0
    //   146: invokespecial 1957	com/oneplus/media/IfdEntryEnumerator:<init>	(Ljava/io/InputStream;J)V
    //   149: astore 4
    //   151: aload 4
    //   153: invokevirtual 1960	com/oneplus/media/IfdEntryEnumerator:read	()Z
    //   156: ifeq +87 -> 243
    //   159: invokestatic 1962	com/oneplus/camera/CameraThread:-getcom-oneplus-media-IfdSwitchesValues	()[I
    //   162: aload 4
    //   164: invokevirtual 1966	com/oneplus/media/IfdEntryEnumerator:currentIfd	()Lcom/oneplus/media/Ifd;
    //   167: invokevirtual 534	com/oneplus/media/Ifd:ordinal	()I
    //   170: iaload
    //   171: tableswitch	default:+554->725, 1:+17->188
    //   188: aload 4
    //   190: invokevirtual 1969	com/oneplus/media/IfdEntryEnumerator:currentEntryId	()I
    //   193: tableswitch	default:+535->728, 1:+19->212
    //   212: aload 4
    //   214: invokevirtual 1972	com/oneplus/media/IfdEntryEnumerator:getEntryDataInteger	()[I
    //   217: astore 7
    //   219: aload 7
    //   221: ifnull -70 -> 151
    //   224: aload 7
    //   226: arraylength
    //   227: ifle -76 -> 151
    //   230: aload 7
    //   232: iconst_0
    //   233: iaload
    //   234: istore_2
    //   235: iload_2
    //   236: invokestatic 1977	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   239: astore_3
    //   240: goto -89 -> 151
    //   243: aload 6
    //   245: astore 5
    //   247: aload 4
    //   249: ifnull +12 -> 261
    //   252: aload 4
    //   254: invokevirtual 1978	com/oneplus/media/IfdEntryEnumerator:close	()V
    //   257: aload 6
    //   259: astore 5
    //   261: aload_3
    //   262: astore 4
    //   264: aload 5
    //   266: ifnull +23 -> 289
    //   269: aload 5
    //   271: athrow
    //   272: astore 4
    //   274: aload_0
    //   275: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   278: ldc_w 1980
    //   281: aload 4
    //   283: invokestatic 1439	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   286: aload_3
    //   287: astore 4
    //   289: aload_0
    //   290: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   293: ldc_w 1982
    //   296: aload 4
    //   298: invokestatic 1381	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   301: aload 4
    //   303: invokevirtual 1983	java/lang/String:isEmpty	()Z
    //   306: ifne +27 -> 333
    //   309: aload_0
    //   310: getstatic 932	com/oneplus/camera/CameraThread:EVENT_UNPROCESSED_PHOTO_RECEIVED	Lcom/oneplus/base/EventKey;
    //   313: new 930	com/oneplus/camera/UnprocessedPictureEventArgs
    //   316: dup
    //   317: aload_0
    //   318: getfield 1249	com/oneplus/camera/CameraThread:m_PhotoCaptureHandle	Lcom/oneplus/camera/CameraThread$PhotoCaptureHandle;
    //   321: aload_1
    //   322: invokevirtual 1911	com/oneplus/camera/CameraCaptureEventArgs:getPictureId	()Ljava/lang/String;
    //   325: aload 4
    //   327: invokespecial 1986	com/oneplus/camera/UnprocessedPictureEventArgs:<init>	(Lcom/oneplus/camera/CaptureHandle;Ljava/lang/String;Ljava/lang/String;)V
    //   330: invokevirtual 649	com/oneplus/camera/CameraThread:raise	(Lcom/oneplus/base/EventKey;Lcom/oneplus/base/EventArgs;)V
    //   333: aload_0
    //   334: getfield 1249	com/oneplus/camera/CameraThread:m_PhotoCaptureHandle	Lcom/oneplus/camera/CameraThread$PhotoCaptureHandle;
    //   337: invokestatic 1210	com/oneplus/base/Handle:isValid	(Lcom/oneplus/base/Handle;)Z
    //   340: ifeq +68 -> 408
    //   343: aload_0
    //   344: getfield 1249	com/oneplus/camera/CameraThread:m_PhotoCaptureHandle	Lcom/oneplus/camera/CameraThread$PhotoCaptureHandle;
    //   347: getfield 1198	com/oneplus/camera/CameraThread$PhotoCaptureHandle:frameCount	I
    //   350: iconst_1
    //   351: if_icmpeq +57 -> 408
    //   354: aload_0
    //   355: invokevirtual 1493	com/oneplus/camera/CameraThread:isShutterSoundNeeded	()Z
    //   358: ifeq +28 -> 386
    //   361: aload_0
    //   362: getfield 1062	com/oneplus/camera/CameraThread:m_AudioManager	Lcom/oneplus/camera/media/AudioManager;
    //   365: ifnull +21 -> 386
    //   368: aload_0
    //   369: aload_0
    //   370: getfield 1062	com/oneplus/camera/CameraThread:m_AudioManager	Lcom/oneplus/camera/media/AudioManager;
    //   373: aload_0
    //   374: getfield 1083	com/oneplus/camera/CameraThread:m_DefaultBurstShutterSoundHandle	Lcom/oneplus/base/Handle;
    //   377: iconst_0
    //   378: invokeinterface 1497 3 0
    //   383: putfield 1704	com/oneplus/camera/CameraThread:m_BurstShutterPlaySoundHandle	Lcom/oneplus/base/Handle;
    //   386: aload_0
    //   387: getstatic 874	com/oneplus/camera/CameraThread:EVENT_BURST_PHOTO_RECEIVED	Lcom/oneplus/base/EventKey;
    //   390: new 869	com/oneplus/camera/CaptureEventArgs
    //   393: dup
    //   394: aload_0
    //   395: getfield 1249	com/oneplus/camera/CameraThread:m_PhotoCaptureHandle	Lcom/oneplus/camera/CameraThread$PhotoCaptureHandle;
    //   398: aload_1
    //   399: invokevirtual 1903	com/oneplus/camera/CameraCaptureEventArgs:getFrameIndex	()I
    //   402: invokespecial 1989	com/oneplus/camera/CaptureEventArgs:<init>	(Lcom/oneplus/camera/CaptureHandle;I)V
    //   405: invokevirtual 649	com/oneplus/camera/CameraThread:raise	(Lcom/oneplus/base/EventKey;Lcom/oneplus/base/EventArgs;)V
    //   408: aload_0
    //   409: getstatic 816	com/oneplus/camera/CameraThread:PROP_MEDIA_RESULT_INFO	Lcom/oneplus/base/PropertyKey;
    //   412: invokevirtual 1178	com/oneplus/camera/CameraThread:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   415: checkcast 814	com/oneplus/camera/MediaResultInfo
    //   418: astore_3
    //   419: aload_3
    //   420: ifnonnull +234 -> 654
    //   423: aload_0
    //   424: getfield 1249	com/oneplus/camera/CameraThread:m_PhotoCaptureHandle	Lcom/oneplus/camera/CameraThread$PhotoCaptureHandle;
    //   427: invokevirtual 1702	com/oneplus/camera/CameraThread$PhotoCaptureHandle:isBurstPhotoCapture	()Z
    //   430: ifne +180 -> 610
    //   433: aload_0
    //   434: getstatic 790	com/oneplus/camera/CameraThread:PROP_CAMERA	Lcom/oneplus/base/PropertyKey;
    //   437: invokevirtual 1178	com/oneplus/camera/CameraThread:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   440: checkcast 785	com/oneplus/camera/Camera
    //   443: getstatic 1992	com/oneplus/camera/Camera:PROP_IS_BOKEH_ENABLED	Lcom/oneplus/base/PropertyKey;
    //   446: invokeinterface 1222 2 0
    //   451: checkcast 761	java/lang/Boolean
    //   454: invokevirtual 1241	java/lang/Boolean:booleanValue	()Z
    //   457: ifeq +131 -> 588
    //   460: aload_1
    //   461: invokevirtual 1995	com/oneplus/camera/CameraCaptureEventArgs:getFlags	()I
    //   464: getstatic 1998	com/oneplus/camera/Camera:FLAG_BOKEH_PICTURE	I
    //   467: getstatic 2001	com/oneplus/camera/Camera:FLAG_ORIGINAL_PICTURE	I
    //   470: ior
    //   471: iand
    //   472: ifeq +116 -> 588
    //   475: aload_0
    //   476: new 2003	com/oneplus/camera/io/BokehPhotoSaveTask
    //   479: dup
    //   480: aload_0
    //   481: invokevirtual 1553	com/oneplus/camera/CameraThread:getContext	()Landroid/content/Context;
    //   484: aload_0
    //   485: getfield 1249	com/oneplus/camera/CameraThread:m_PhotoCaptureHandle	Lcom/oneplus/camera/CameraThread$PhotoCaptureHandle;
    //   488: aload_1
    //   489: invokespecial 2006	com/oneplus/camera/io/BokehPhotoSaveTask:<init>	(Landroid/content/Context;Lcom/oneplus/camera/CaptureHandle;Lcom/oneplus/camera/CameraCaptureEventArgs;)V
    //   492: invokevirtual 2010	com/oneplus/camera/CameraThread:saveMedia	(Lcom/oneplus/camera/io/MediaSaveTask;)Lcom/oneplus/base/Handle;
    //   495: pop
    //   496: return
    //   497: astore 5
    //   499: goto -238 -> 261
    //   502: astore 5
    //   504: aload 8
    //   506: astore 4
    //   508: aload 5
    //   510: athrow
    //   511: astore 7
    //   513: aload 4
    //   515: astore 6
    //   517: aload 7
    //   519: astore 4
    //   521: aload 5
    //   523: astore 7
    //   525: aload 6
    //   527: ifnull +12 -> 539
    //   530: aload 6
    //   532: invokevirtual 1978	com/oneplus/media/IfdEntryEnumerator:close	()V
    //   535: aload 5
    //   537: astore 7
    //   539: aload 7
    //   541: ifnull +31 -> 572
    //   544: aload 7
    //   546: athrow
    //   547: aload 5
    //   549: astore 7
    //   551: aload 5
    //   553: aload 6
    //   555: if_acmpeq -16 -> 539
    //   558: aload 5
    //   560: aload 6
    //   562: invokevirtual 2014	java/lang/Throwable:addSuppressed	(Ljava/lang/Throwable;)V
    //   565: aload 5
    //   567: astore 7
    //   569: goto -30 -> 539
    //   572: aload 4
    //   574: athrow
    //   575: aload_0
    //   576: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   579: ldc_w 2016
    //   582: invokestatic 1131	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   585: goto -252 -> 333
    //   588: aload_0
    //   589: new 2018	com/oneplus/camera/io/PhotoSaveTask
    //   592: dup
    //   593: aload_0
    //   594: invokevirtual 1553	com/oneplus/camera/CameraThread:getContext	()Landroid/content/Context;
    //   597: aload_0
    //   598: getfield 1249	com/oneplus/camera/CameraThread:m_PhotoCaptureHandle	Lcom/oneplus/camera/CameraThread$PhotoCaptureHandle;
    //   601: aload_1
    //   602: invokespecial 2019	com/oneplus/camera/io/PhotoSaveTask:<init>	(Landroid/content/Context;Lcom/oneplus/camera/CaptureHandle;Lcom/oneplus/camera/CameraCaptureEventArgs;)V
    //   605: invokevirtual 2010	com/oneplus/camera/CameraThread:saveMedia	(Lcom/oneplus/camera/io/MediaSaveTask;)Lcom/oneplus/base/Handle;
    //   608: pop
    //   609: return
    //   610: aload_1
    //   611: invokevirtual 1903	com/oneplus/camera/CameraCaptureEventArgs:getFrameIndex	()I
    //   614: ifne +10 -> 624
    //   617: aload_0
    //   618: invokestatic 2024	java/lang/System:currentTimeMillis	()J
    //   621: putfield 2026	com/oneplus/camera/CameraThread:m_LastBurstPhotoTime	J
    //   624: aload_0
    //   625: new 2028	com/oneplus/camera/io/BurstPhotoSaveTask
    //   628: dup
    //   629: aload_0
    //   630: invokevirtual 1553	com/oneplus/camera/CameraThread:getContext	()Landroid/content/Context;
    //   633: aload_0
    //   634: getfield 1249	com/oneplus/camera/CameraThread:m_PhotoCaptureHandle	Lcom/oneplus/camera/CameraThread$PhotoCaptureHandle;
    //   637: aload_1
    //   638: aload_0
    //   639: getfield 2026	com/oneplus/camera/CameraThread:m_LastBurstPhotoTime	J
    //   642: aload_1
    //   643: invokevirtual 1903	com/oneplus/camera/CameraCaptureEventArgs:getFrameIndex	()I
    //   646: invokespecial 2031	com/oneplus/camera/io/BurstPhotoSaveTask:<init>	(Landroid/content/Context;Lcom/oneplus/camera/CaptureHandle;Lcom/oneplus/camera/CameraCaptureEventArgs;JI)V
    //   649: invokevirtual 2010	com/oneplus/camera/CameraThread:saveMedia	(Lcom/oneplus/camera/io/MediaSaveTask;)Lcom/oneplus/base/Handle;
    //   652: pop
    //   653: return
    //   654: aload_3
    //   655: getfield 1742	com/oneplus/camera/MediaResultInfo:extraOutput	Landroid/net/Uri;
    //   658: ifnull +29 -> 687
    //   661: aload_0
    //   662: new 2033	com/oneplus/camera/io/RequestedPhotoSaveTask
    //   665: dup
    //   666: aload_0
    //   667: invokevirtual 1553	com/oneplus/camera/CameraThread:getContext	()Landroid/content/Context;
    //   670: aload_0
    //   671: getfield 1249	com/oneplus/camera/CameraThread:m_PhotoCaptureHandle	Lcom/oneplus/camera/CameraThread$PhotoCaptureHandle;
    //   674: aload_3
    //   675: getfield 1742	com/oneplus/camera/MediaResultInfo:extraOutput	Landroid/net/Uri;
    //   678: aload_1
    //   679: invokespecial 2036	com/oneplus/camera/io/RequestedPhotoSaveTask:<init>	(Landroid/content/Context;Lcom/oneplus/camera/CaptureHandle;Landroid/net/Uri;Lcom/oneplus/camera/CameraCaptureEventArgs;)V
    //   682: invokevirtual 2010	com/oneplus/camera/CameraThread:saveMedia	(Lcom/oneplus/camera/io/MediaSaveTask;)Lcom/oneplus/base/Handle;
    //   685: pop
    //   686: return
    //   687: aload_0
    //   688: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   691: ldc_w 2038
    //   694: invokestatic 1111	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   697: return
    //   698: astore 4
    //   700: aload 7
    //   702: astore 6
    //   704: goto -183 -> 521
    //   707: astore 7
    //   709: aload 4
    //   711: astore 6
    //   713: aload 7
    //   715: astore 4
    //   717: goto -196 -> 521
    //   720: astore 5
    //   722: goto -214 -> 508
    //   725: goto -574 -> 151
    //   728: goto -577 -> 151
    //   731: astore 4
    //   733: goto -459 -> 274
    //   736: astore 6
    //   738: aload 5
    //   740: ifnonnull -193 -> 547
    //   743: aload 6
    //   745: astore 7
    //   747: goto -208 -> 539
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	750	0	this	CameraThread
    //   0	750	1	paramCameraCaptureEventArgs	CameraCaptureEventArgs
    //   234	2	2	i	int
    //   43	632	3	localObject1	Object
    //   91	172	4	localObject2	Object
    //   272	10	4	localThrowable1	Throwable
    //   287	286	4	localObject3	Object
    //   698	12	4	localObject4	Object
    //   715	1	4	localObject5	Object
    //   731	1	4	localThrowable2	Throwable
    //   121	149	5	localObject6	Object
    //   497	1	5	localThrowable3	Throwable
    //   502	64	5	localThrowable4	Throwable
    //   720	19	5	localThrowable5	Throwable
    //   124	588	6	localObject7	Object
    //   736	8	6	localThrowable6	Throwable
    //   127	104	7	arrayOfInt	int[]
    //   511	7	7	localObject8	Object
    //   523	178	7	localThrowable7	Throwable
    //   707	7	7	localObject9	Object
    //   745	1	7	localThrowable8	Throwable
    //   130	375	8	localObject10	Object
    //   110	31	9	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   269	272	272	java/lang/Throwable
    //   252	257	497	java/lang/Throwable
    //   132	151	502	java/lang/Throwable
    //   508	511	511	finally
    //   132	151	698	finally
    //   151	188	707	finally
    //   188	212	707	finally
    //   212	219	707	finally
    //   224	230	707	finally
    //   151	188	720	java/lang/Throwable
    //   188	212	720	java/lang/Throwable
    //   212	219	720	java/lang/Throwable
    //   224	230	720	java/lang/Throwable
    //   544	547	731	java/lang/Throwable
    //   558	565	731	java/lang/Throwable
    //   572	575	731	java/lang/Throwable
    //   530	535	736	java/lang/Throwable
  }
  
  private void onPostviewReceived(CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    if (this.m_CameraCaptureHandle != paramCameraCaptureEventArgs.getHandle())
    {
      Log.w(this.TAG, "onPostviewReceived() - Invalid camera capture handle");
      return;
    }
    if (!Handle.isValid(this.m_PhotoCaptureHandle))
    {
      Log.w(this.TAG, "onPostviewReceived() - Invalid photo capture handle");
      return;
    }
    paramCameraCaptureEventArgs = CameraCaptureEventArgs.obtain(this.m_PhotoCaptureHandle, null, paramCameraCaptureEventArgs.getFrameIndex(), paramCameraCaptureEventArgs.getPictureFormat(), paramCameraCaptureEventArgs.getPictureSize(), paramCameraCaptureEventArgs.getPicturePlanes(), paramCameraCaptureEventArgs.getCaptureResult(), paramCameraCaptureEventArgs.getTakenTime());
    raise(EVENT_POSTVIEW_RECEIVED, paramCameraCaptureEventArgs);
  }
  
  private void onRawPictureReceived(CameraCaptureEventArgs paramCameraCaptureEventArgs)
  {
    Log.v(this.TAG, "onRawPictureReceived() - Index : ", Integer.valueOf(paramCameraCaptureEventArgs.getFrameIndex()));
    Object localObject = null;
    CaptureResult localCaptureResult = null;
    if ((((Camera)get(PROP_CAMERA)).get(Camera.PROP_CAMERA_CHARACTERISTICS) instanceof CameraCharacteristics)) {
      localObject = (CameraCharacteristics)((Camera)get(PROP_CAMERA)).get(Camera.PROP_CAMERA_CHARACTERISTICS);
    }
    if ((paramCameraCaptureEventArgs.getCaptureResult() instanceof CaptureResult)) {
      localCaptureResult = (CaptureResult)paramCameraCaptureEventArgs.getCaptureResult();
    }
    if ((localObject == null) || (localCaptureResult == null))
    {
      Log.w(this.TAG, "onRawPictureReceived() - parameters type is incorrect");
      return;
    }
    localObject = new DngCreator((CameraCharacteristics)localObject, localCaptureResult);
    if (((Camera)get(PROP_CAMERA)).get(Camera.PROP_LOCATION) != null) {
      ((DngCreator)localObject).setLocation((Location)((Camera)get(PROP_CAMERA)).get(Camera.PROP_LOCATION));
    }
    int i = 1;
    switch (-getcom-oneplus-base-RotationSwitchesValues()[((Rotation)((Camera)get(PROP_CAMERA)).get(Camera.PROP_PICTURE_ROTATION)).ordinal()])
    {
    }
    for (;;)
    {
      ((DngCreator)localObject).setOrientation(i);
      saveMedia(new RawPhotoSaveTask(getContext(), this.m_PhotoCaptureHandle, paramCameraCaptureEventArgs, (DngCreator)localObject));
      return;
      i = 1;
      continue;
      i = 6;
      continue;
      i = 3;
      continue;
      i = 8;
    }
  }
  
  private void onSavingQueueStateChanged(boolean paramBoolean)
  {
    if (!paramBoolean) {
      return;
    }
    Log.w(this.TAG, "onSavingQueueStateChanged() - Media saving queue is full");
    if (Handle.isValid(this.m_PhotoCaptureHandle)) {
      stopPhotoCaptureInternal(this.m_PhotoCaptureHandle, CaptureCompleteReason.SAVING_QUEUE_FULL);
    }
  }
  
  private void onShutter(int paramInt)
  {
    Log.v(this.TAG, "onShutter() - Index : ", Integer.valueOf(paramInt));
    if (!Handle.isValid(this.m_PhotoCaptureHandle))
    {
      Log.e(this.TAG, "onShutter() - Not capturing");
      return;
    }
    if (paramInt == 0)
    {
      if (this.m_PhotoCaptureHandle.isFastCaptureEnabled) {
        return;
      }
      getHandler().removeMessages(10040);
    }
    this.m_IsDefaultShutterReceived = true;
    if ((isShutterSoundNeeded()) && (get(PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.CAPTURING) && (get(PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.PAUSED) && (this.m_PhotoCaptureHandle.frameCount == 1) && (paramInt == 0)) {
      playDefaultShutterSound();
    }
    raise(EVENT_SHUTTER, new CaptureEventArgs(this.m_PhotoCaptureHandle, paramInt));
  }
  
  private void onShutter(CaptureEventArgs paramCaptureEventArgs)
  {
    if (paramCaptureEventArgs == null)
    {
      Log.e(this.TAG, "onShutter() - No event data");
      return;
    }
    if (this.m_PhotoCaptureHandle != paramCaptureEventArgs.getCaptureHandle())
    {
      Log.e(this.TAG, "onShutter() - Unknown capture handle : " + paramCaptureEventArgs.getCaptureHandle() + ", current handle : " + this.m_PhotoCaptureHandle);
      return;
    }
    raise(EVENT_SHUTTER, paramCaptureEventArgs);
  }
  
  private boolean openCameraInternal(Camera paramCamera, int paramInt)
  {
    if (!((List)get(PROP_AVAILABLE_CAMERAS)).contains(paramCamera))
    {
      Log.e(this.TAG, "openCameraInternal() - Camera " + paramCamera + " is not contained in available camera list");
      return false;
    }
    switch (-getcom-oneplus-camera-Camera$StateSwitchesValues()[((Camera.State)paramCamera.get(Camera.PROP_STATE)).ordinal()])
    {
    default: 
      Log.v(this.TAG, "openCameraInternal() - Open ", paramCamera);
    }
    try
    {
      if (paramCamera.open(0)) {
        break label151;
      }
      Log.e(this.TAG, "openCameraInternal() - Fail to open " + paramCamera);
      return false;
    }
    catch (Throwable paramCamera)
    {
      return false;
    }
    return true;
    label151:
    PropertyKey localPropertyKey = Camera.PROP_IS_RECORDING_MODE;
    if (get(PROP_MEDIA_TYPE) == MediaType.VIDEO) {}
    for (boolean bool = true;; bool = false)
    {
      paramCamera.set(localPropertyKey, Boolean.valueOf(bool));
      setReadOnly(PROP_IS_CAMERA_PREVIEW_RECEIVED, (Boolean)paramCamera.get(Camera.PROP_IS_PREVIEW_RECEIVED));
      setReadOnly(PROP_CAMERA, paramCamera);
      if (!this.m_IsHighComponentsCreated)
      {
        this.m_IsHighComponentsCreated = true;
        this.m_ComponentManager.createComponents(ComponentCreationPriority.HIGH, new Object[] { this });
      }
      return true;
    }
  }
  
  private boolean pauseVideoCaptureInternal(VideoCaptureHandle paramVideoCaptureHandle, int paramInt)
  {
    if (this.m_VideoCaptureHandle != paramVideoCaptureHandle)
    {
      Log.w(this.TAG, "pauseVideoCaptureInternal() - Invalid handle");
      return false;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "pauseVideoCaptureInternal() - Current capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return false;
    case 2: 
    case 3: 
      return true;
    }
    paramVideoCaptureHandle = (Camera)get(PROP_CAMERA);
    if (paramVideoCaptureHandle == null)
    {
      Log.e(this.TAG, "pauseVideoCaptureInternal() - No camera");
      return false;
    }
    setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.PAUSING);
    if (get(PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.PAUSING)
    {
      Log.e(this.TAG, "pauseVideoCaptureInternal() - Interrupted by other operations");
      return false;
    }
    long l = SystemClock.elapsedRealtime() - this.m_MediaRecorderStartTime;
    if (l < 1000L)
    {
      l = 1000L - l;
      Log.w(this.TAG, "pauseVideoCaptureInternal() - Delay " + l + " ms");
    }
    try
    {
      Thread.sleep(l);
    }
    catch (InterruptedException localInterruptedException)
    {
      try
      {
        for (;;)
        {
          l = SystemClock.elapsedRealtime();
          if (paramVideoCaptureHandle.pauseVideoRecording(0)) {
            break;
          }
          Log.e(this.TAG, "pauseVideoCaptureInternal() - Fail to pause");
          return false;
          localInterruptedException = localInterruptedException;
          Log.e(this.TAG, "pauseVideoCaptureInternal() - Interrupted");
        }
        this.m_MediaRecorderPauseTime = l;
        setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.PAUSED);
        if (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.PAUSED) {
          return true;
        }
      }
      catch (Throwable paramVideoCaptureHandle)
      {
        Log.e(this.TAG, "pauseVideoCaptureInternal() - Fail to pause", paramVideoCaptureHandle);
        return false;
      }
    }
    return false;
  }
  
  /* Error */
  private boolean prepareMediaRecorder(Camera paramCamera, MediaRecorder paramMediaRecorder, VideoParams paramVideoParams)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 7
    //   3: iload 7
    //   5: istore 6
    //   7: aload_0
    //   8: getfield 2174	com/oneplus/camera/CameraThread:m_VideoCaptureHandlerHandles	Ljava/util/List;
    //   11: invokeinterface 1715 1 0
    //   16: ifne +106 -> 122
    //   19: aload_0
    //   20: getfield 2174	com/oneplus/camera/CameraThread:m_VideoCaptureHandlerHandles	Ljava/util/List;
    //   23: invokeinterface 1408 1 0
    //   28: iconst_1
    //   29: isub
    //   30: istore 5
    //   32: iload 7
    //   34: istore 6
    //   36: iload 5
    //   38: iflt +84 -> 122
    //   41: aload_0
    //   42: getfield 2174	com/oneplus/camera/CameraThread:m_VideoCaptureHandlerHandles	Ljava/util/List;
    //   45: iload 5
    //   47: invokeinterface 1411 2 0
    //   52: checkcast 108	com/oneplus/camera/CameraThread$VideoCaptureHandlerHandle
    //   55: getfield 1591	com/oneplus/camera/CameraThread$VideoCaptureHandlerHandle:captureHandler	Lcom/oneplus/camera/VideoCaptureHandler;
    //   58: astore 8
    //   60: aload 8
    //   62: aload_1
    //   63: aload_2
    //   64: aload_3
    //   65: invokeinterface 2179 4 0
    //   70: ifeq +162 -> 232
    //   73: aload_0
    //   74: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   77: new 1257	java/lang/StringBuilder
    //   80: dup
    //   81: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   84: ldc_w 2181
    //   87: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   90: aload 8
    //   92: invokevirtual 1267	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   95: invokevirtual 1271	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   98: invokestatic 1111	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   101: aload_0
    //   102: aload_0
    //   103: getfield 2174	com/oneplus/camera/CameraThread:m_VideoCaptureHandlerHandles	Ljava/util/List;
    //   106: iload 5
    //   108: invokeinterface 1411 2 0
    //   113: checkcast 108	com/oneplus/camera/CameraThread$VideoCaptureHandlerHandle
    //   116: putfield 1588	com/oneplus/camera/CameraThread:m_VideoCaptureHandlerHandle	Lcom/oneplus/camera/CameraThread$VideoCaptureHandlerHandle;
    //   119: iconst_1
    //   120: istore 6
    //   122: iload 6
    //   124: ifne +340 -> 464
    //   127: aload_3
    //   128: getfield 1480	com/oneplus/camera/CameraThread$VideoParams:resolution	Lcom/oneplus/camera/media/Resolution;
    //   131: astore 8
    //   133: aload_0
    //   134: aload 8
    //   136: invokevirtual 2182	com/oneplus/camera/media/Resolution:getWidth	()I
    //   139: aload 8
    //   141: invokevirtual 2183	com/oneplus/camera/media/Resolution:getHeight	()I
    //   144: invokespecial 1647	com/oneplus/camera/CameraThread:createCamcorderProfile	(II)Landroid/media/CamcorderProfile;
    //   147: astore 9
    //   149: aload 9
    //   151: ifnonnull +90 -> 241
    //   154: aload_0
    //   155: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   158: new 1257	java/lang/StringBuilder
    //   161: dup
    //   162: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   165: ldc_w 2185
    //   168: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   171: aload 8
    //   173: invokevirtual 1267	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   176: invokevirtual 1271	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   179: invokestatic 1131	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   182: aload_1
    //   183: iconst_0
    //   184: invokeinterface 1526 2 0
    //   189: pop
    //   190: iconst_0
    //   191: ireturn
    //   192: astore_2
    //   193: aload_0
    //   194: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   197: new 1257	java/lang/StringBuilder
    //   200: dup
    //   201: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   204: ldc_w 2187
    //   207: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   210: aload 8
    //   212: invokevirtual 1267	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   215: invokevirtual 1271	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   218: aload_2
    //   219: invokestatic 1439	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   222: aload_1
    //   223: iconst_0
    //   224: invokeinterface 1526 2 0
    //   229: pop
    //   230: iconst_0
    //   231: ireturn
    //   232: iload 5
    //   234: iconst_1
    //   235: isub
    //   236: istore 5
    //   238: goto -206 -> 32
    //   241: aload_2
    //   242: iconst_5
    //   243: invokevirtual 2190	android/media/MediaRecorder:setAudioSource	(I)V
    //   246: aload_2
    //   247: aload 9
    //   249: invokevirtual 2194	android/media/MediaRecorder:setProfile	(Landroid/media/CamcorderProfile;)V
    //   252: aload 8
    //   254: invokevirtual 2197	com/oneplus/camera/media/Resolution:getFps	()I
    //   257: ifle +137 -> 394
    //   260: aload_0
    //   261: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   264: new 1257	java/lang/StringBuilder
    //   267: dup
    //   268: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   271: ldc_w 2199
    //   274: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   277: aload 8
    //   279: invokevirtual 2197	com/oneplus/camera/media/Resolution:getFps	()I
    //   282: invokevirtual 1573	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   285: invokevirtual 1271	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   288: invokestatic 1429	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   291: aload_2
    //   292: aload 8
    //   294: invokevirtual 2197	com/oneplus/camera/media/Resolution:getFps	()I
    //   297: invokevirtual 2202	android/media/MediaRecorder:setVideoFrameRate	(I)V
    //   300: aload 8
    //   302: invokevirtual 2197	com/oneplus/camera/media/Resolution:getFps	()I
    //   305: bipush 30
    //   307: if_icmple +87 -> 394
    //   310: aload_0
    //   311: sipush 1920
    //   314: sipush 1080
    //   317: invokespecial 1647	com/oneplus/camera/CameraThread:createCamcorderProfile	(II)Landroid/media/CamcorderProfile;
    //   320: astore 9
    //   322: aload 8
    //   324: invokevirtual 2182	com/oneplus/camera/media/Resolution:getWidth	()I
    //   327: aload 8
    //   329: invokevirtual 2183	com/oneplus/camera/media/Resolution:getHeight	()I
    //   332: imul
    //   333: aload 8
    //   335: invokevirtual 2197	com/oneplus/camera/media/Resolution:getFps	()I
    //   338: imul
    //   339: i2f
    //   340: ldc_w 2203
    //   343: fdiv
    //   344: fstore 4
    //   346: aload 9
    //   348: getfield 2206	android/media/CamcorderProfile:videoBitRate	I
    //   351: i2f
    //   352: fload 4
    //   354: fmul
    //   355: invokestatic 2210	java/lang/Math:round	(F)I
    //   358: istore 5
    //   360: aload_0
    //   361: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   364: new 1257	java/lang/StringBuilder
    //   367: dup
    //   368: invokespecial 1258	java/lang/StringBuilder:<init>	()V
    //   371: ldc_w 2212
    //   374: invokevirtual 1264	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   377: iload 5
    //   379: invokevirtual 1573	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   382: invokevirtual 1271	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   385: invokestatic 1429	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;)V
    //   388: aload_2
    //   389: iload 5
    //   391: invokevirtual 2215	android/media/MediaRecorder:setVideoEncodingBitRate	(I)V
    //   394: aload_0
    //   395: getstatic 798	com/oneplus/camera/CameraThread:PROP_CAPTURE_ROTATION	Lcom/oneplus/base/PropertyKey;
    //   398: invokevirtual 1178	com/oneplus/camera/CameraThread:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   401: checkcast 381	com/oneplus/base/Rotation
    //   404: invokevirtual 2218	com/oneplus/base/Rotation:getDeviceOrientation	()I
    //   407: getstatic 399	com/oneplus/base/Rotation:LANDSCAPE	Lcom/oneplus/base/Rotation;
    //   410: invokevirtual 2218	com/oneplus/base/Rotation:getDeviceOrientation	()I
    //   413: isub
    //   414: istore 6
    //   416: iload 6
    //   418: istore 5
    //   420: aload_1
    //   421: getstatic 1385	com/oneplus/camera/Camera:PROP_LENS_FACING	Lcom/oneplus/base/PropertyKey;
    //   424: invokeinterface 1222 2 0
    //   429: getstatic 1391	com/oneplus/camera/Camera$LensFacing:FRONT	Lcom/oneplus/camera/Camera$LensFacing;
    //   432: if_acmpne +298 -> 730
    //   435: iload 6
    //   437: ineg
    //   438: istore 5
    //   440: goto +290 -> 730
    //   443: aload_0
    //   444: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   447: ldc_w 2220
    //   450: iload 6
    //   452: invokestatic 1906	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   455: invokestatic 1381	com/oneplus/base/Log:v	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V
    //   458: aload_2
    //   459: iload 6
    //   461: invokevirtual 2223	android/media/MediaRecorder:setOrientationHint	(I)V
    //   464: aload_0
    //   465: getfield 1172	com/oneplus/camera/CameraThread:m_LocationManager	Lcom/oneplus/camera/location/LocationManager;
    //   468: ifnull +22 -> 490
    //   471: aload_0
    //   472: aload_0
    //   473: getfield 1172	com/oneplus/camera/CameraThread:m_LocationManager	Lcom/oneplus/camera/location/LocationManager;
    //   476: getstatic 1354	com/oneplus/camera/location/LocationManager:PROP_LOCATION	Lcom/oneplus/base/PropertyKey;
    //   479: invokeinterface 1355 2 0
    //   484: checkcast 1357	android/location/Location
    //   487: putfield 2225	com/oneplus/camera/CameraThread:m_VideoLocation	Landroid/location/Location;
    //   490: aload_0
    //   491: getfield 2225	com/oneplus/camera/CameraThread:m_VideoLocation	Landroid/location/Location;
    //   494: ifnull +23 -> 517
    //   497: aload_2
    //   498: aload_0
    //   499: getfield 2225	com/oneplus/camera/CameraThread:m_VideoLocation	Landroid/location/Location;
    //   502: invokevirtual 2229	android/location/Location:getLatitude	()D
    //   505: d2f
    //   506: aload_0
    //   507: getfield 2225	com/oneplus/camera/CameraThread:m_VideoLocation	Landroid/location/Location;
    //   510: invokevirtual 2232	android/location/Location:getLongitude	()D
    //   513: d2f
    //   514: invokevirtual 2235	android/media/MediaRecorder:setLocation	(FF)V
    //   517: aload_0
    //   518: getfield 969	com/oneplus/camera/CameraThread:m_VideoFileDescriptor	Landroid/os/ParcelFileDescriptor;
    //   521: ifnull +156 -> 677
    //   524: aload_2
    //   525: aload_0
    //   526: getfield 969	com/oneplus/camera/CameraThread:m_VideoFileDescriptor	Landroid/os/ParcelFileDescriptor;
    //   529: invokevirtual 2239	android/os/ParcelFileDescriptor:getFileDescriptor	()Ljava/io/FileDescriptor;
    //   532: invokevirtual 2243	android/media/MediaRecorder:setOutputFile	(Ljava/io/FileDescriptor;)V
    //   535: aload_3
    //   536: getfield 2246	com/oneplus/camera/CameraThread$VideoParams:maxFileSize	J
    //   539: lconst_0
    //   540: lcmp
    //   541: ifle +18 -> 559
    //   544: aload_3
    //   545: getfield 2246	com/oneplus/camera/CameraThread$VideoParams:maxFileSize	J
    //   548: aload_0
    //   549: getstatic 521	com/oneplus/camera/media/MediaType:VIDEO	Lcom/oneplus/camera/media/MediaType;
    //   552: invokespecial 2248	com/oneplus/camera/CameraThread:getFreeSpace	(Lcom/oneplus/camera/media/MediaType;)J
    //   555: lcmp
    //   556: ifle +158 -> 714
    //   559: aload_2
    //   560: aload_0
    //   561: getstatic 521	com/oneplus/camera/media/MediaType:VIDEO	Lcom/oneplus/camera/media/MediaType;
    //   564: invokespecial 2248	com/oneplus/camera/CameraThread:getFreeSpace	(Lcom/oneplus/camera/media/MediaType;)J
    //   567: invokevirtual 2251	android/media/MediaRecorder:setMaxFileSize	(J)V
    //   570: aload_0
    //   571: iconst_0
    //   572: putfield 1895	com/oneplus/camera/CameraThread:m_IsMaxFileSizeDesignated	Z
    //   575: aload_3
    //   576: getfield 2254	com/oneplus/camera/CameraThread$VideoParams:maxDurationSeconds	J
    //   579: lconst_0
    //   580: lcmp
    //   581: iflt +16 -> 597
    //   584: aload_2
    //   585: aload_3
    //   586: getfield 2254	com/oneplus/camera/CameraThread$VideoParams:maxDurationSeconds	J
    //   589: ldc2_w 234
    //   592: lmul
    //   593: l2i
    //   594: invokevirtual 2257	android/media/MediaRecorder:setMaxDuration	(I)V
    //   597: aload_0
    //   598: getfield 2259	com/oneplus/camera/CameraThread:m_MediaRecorderErrorListener	Landroid/media/MediaRecorder$OnErrorListener;
    //   601: ifnonnull +15 -> 616
    //   604: aload_0
    //   605: new 54	com/oneplus/camera/CameraThread$30
    //   608: dup
    //   609: aload_0
    //   610: invokespecial 2260	com/oneplus/camera/CameraThread$30:<init>	(Lcom/oneplus/camera/CameraThread;)V
    //   613: putfield 2259	com/oneplus/camera/CameraThread:m_MediaRecorderErrorListener	Landroid/media/MediaRecorder$OnErrorListener;
    //   616: aload_0
    //   617: getfield 2262	com/oneplus/camera/CameraThread:m_MediaRecorderInfoListener	Landroid/media/MediaRecorder$OnInfoListener;
    //   620: ifnonnull +15 -> 635
    //   623: aload_0
    //   624: new 56	com/oneplus/camera/CameraThread$31
    //   627: dup
    //   628: aload_0
    //   629: invokespecial 2263	com/oneplus/camera/CameraThread$31:<init>	(Lcom/oneplus/camera/CameraThread;)V
    //   632: putfield 2262	com/oneplus/camera/CameraThread:m_MediaRecorderInfoListener	Landroid/media/MediaRecorder$OnInfoListener;
    //   635: aload_2
    //   636: aload_0
    //   637: getfield 2259	com/oneplus/camera/CameraThread:m_MediaRecorderErrorListener	Landroid/media/MediaRecorder$OnErrorListener;
    //   640: invokevirtual 2267	android/media/MediaRecorder:setOnErrorListener	(Landroid/media/MediaRecorder$OnErrorListener;)V
    //   643: aload_2
    //   644: aload_0
    //   645: getfield 2262	com/oneplus/camera/CameraThread:m_MediaRecorderInfoListener	Landroid/media/MediaRecorder$OnInfoListener;
    //   648: invokevirtual 2271	android/media/MediaRecorder:setOnInfoListener	(Landroid/media/MediaRecorder$OnInfoListener;)V
    //   651: aload_0
    //   652: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   655: ldc_w 2273
    //   658: invokestatic 1111	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   661: aload_2
    //   662: invokevirtual 2276	android/media/MediaRecorder:prepare	()V
    //   665: aload_0
    //   666: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   669: ldc_w 2278
    //   672: invokestatic 1111	com/oneplus/base/Log:w	(Ljava/lang/String;Ljava/lang/String;)V
    //   675: iconst_1
    //   676: ireturn
    //   677: aload_2
    //   678: aload_0
    //   679: getfield 2280	com/oneplus/camera/CameraThread:m_VideoFilePath	Ljava/lang/String;
    //   682: invokevirtual 2282	android/media/MediaRecorder:setOutputFile	(Ljava/lang/String;)V
    //   685: goto -150 -> 535
    //   688: astore_2
    //   689: aload_0
    //   690: getfield 368	com/oneplus/camera/CameraThread:TAG	Ljava/lang/String;
    //   693: ldc_w 2284
    //   696: aload_2
    //   697: invokestatic 1439	com/oneplus/base/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   700: aload_0
    //   701: invokespecial 1576	com/oneplus/camera/CameraThread:closeVideoFileDescriptor	()V
    //   704: aload_1
    //   705: iconst_0
    //   706: invokeinterface 1526 2 0
    //   711: pop
    //   712: iconst_0
    //   713: ireturn
    //   714: aload_2
    //   715: aload_3
    //   716: getfield 2246	com/oneplus/camera/CameraThread$VideoParams:maxFileSize	J
    //   719: invokevirtual 2251	android/media/MediaRecorder:setMaxFileSize	(J)V
    //   722: aload_0
    //   723: iconst_1
    //   724: putfield 1895	com/oneplus/camera/CameraThread:m_IsMaxFileSizeDesignated	Z
    //   727: goto -152 -> 575
    //   730: iload 5
    //   732: istore 6
    //   734: iload 5
    //   736: ifge -293 -> 443
    //   739: iload 5
    //   741: sipush 360
    //   744: iadd
    //   745: istore 6
    //   747: goto -304 -> 443
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	750	0	this	CameraThread
    //   0	750	1	paramCamera	Camera
    //   0	750	2	paramMediaRecorder	MediaRecorder
    //   0	750	3	paramVideoParams	VideoParams
    //   344	9	4	f	float
    //   30	715	5	i	int
    //   5	741	6	j	int
    //   1	32	7	k	int
    //   58	276	8	localObject	Object
    //   147	200	9	localCamcorderProfile	CamcorderProfile
    // Exception table:
    //   from	to	target	type
    //   60	119	192	java/lang/Throwable
    //   127	149	688	java/lang/Throwable
    //   154	190	688	java/lang/Throwable
    //   241	394	688	java/lang/Throwable
    //   394	416	688	java/lang/Throwable
    //   420	435	688	java/lang/Throwable
    //   443	464	688	java/lang/Throwable
    //   464	490	688	java/lang/Throwable
    //   490	517	688	java/lang/Throwable
    //   517	535	688	java/lang/Throwable
    //   535	559	688	java/lang/Throwable
    //   559	575	688	java/lang/Throwable
    //   575	597	688	java/lang/Throwable
    //   597	616	688	java/lang/Throwable
    //   616	635	688	java/lang/Throwable
    //   635	675	688	java/lang/Throwable
    //   677	685	688	java/lang/Throwable
    //   714	727	688	java/lang/Throwable
  }
  
  private boolean prepareVideoFilePath()
  {
    closeVideoFileDescriptor();
    if (((this.m_Context instanceof CameraActivity)) && (((CameraActivity)this.m_Context).isVideoServiceMode()))
    {
      MediaResultInfo localMediaResultInfo = (MediaResultInfo)get(PROP_MEDIA_RESULT_INFO);
      if ((localMediaResultInfo != null) && (localMediaResultInfo.extraOutput != null)) {
        try
        {
          this.m_VideoFileDescriptor = this.m_Context.getContentResolver().openFileDescriptor(localMediaResultInfo.extraOutput, "rw");
          Log.v(this.TAG, "prepareVideoFilePath() - Video file path : ", this.m_VideoFileDescriptor.toString());
          return true;
        }
        catch (Throwable localThrowable)
        {
          Log.e(this.TAG, "prepareVideoFilePath() - Error when open extra output", localThrowable);
          return false;
        }
      }
    }
    Object localObject2 = StorageUtils.getDcimPath(StorageUtils.findStorageFromSettings(this.m_StorageManager, this.m_Settings, Storage.Type.INTERNAL));
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }
    localObject1 = new File(Path.combine(new String[] { localObject1, "Camera" }));
    if ((((File)localObject1).exists()) || (((File)localObject1).mkdirs()))
    {
      localObject2 = new SimpleDateFormat("yyyyMMdd_HHmmss");
      this.m_VideoFilePath = new File((File)localObject1, "VID_" + ((SimpleDateFormat)localObject2).format(new Date()) + ".mp4").getAbsolutePath();
      Log.w(this.TAG, "prepareVideoFilePath() - Video file path : " + this.m_VideoFilePath);
      return true;
    }
    Log.e(this.TAG, "prepareVideoFilePath() - Fail to create " + ((File)localObject1).getAbsolutePath());
    return false;
  }
  
  private void removePhotoCaptureHandler(PhotoCaptureHandlerHandle paramPhotoCaptureHandlerHandle)
  {
    verifyAccess();
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      throw new RuntimeException("Cannot remove capture handler when photo capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
    }
    if (!this.m_PhotoCaptureHandlerHandles.remove(paramPhotoCaptureHandlerHandle)) {
      return;
    }
    Log.w(this.TAG, "removePhotoCaptureHandler() - Handle : " + paramPhotoCaptureHandlerHandle);
  }
  
  private void removeVideoCaptureHandler(VideoCaptureHandlerHandle paramVideoCaptureHandlerHandle)
  {
    verifyAccess();
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    case 6: 
    default: 
      throw new RuntimeException("Cannot remove capture handler when video capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
    }
    if (!this.m_VideoCaptureHandlerHandles.remove(paramVideoCaptureHandlerHandle)) {
      return;
    }
    Log.w(this.TAG, "removeVideoCaptureHandler() - Handle : " + paramVideoCaptureHandlerHandle);
  }
  
  private void resetCapturingRawPhotoProperty(MediaEventArgs paramMediaEventArgs)
  {
    Camera localCamera = (Camera)get(PROP_CAMERA);
    if (localCamera != null)
    {
      if ((!((Boolean)localCamera.get(Camera.PROP_IS_RAW_CAPTURE_ENABLED)).booleanValue()) || (!FileUtils.isRawFilePath(paramMediaEventArgs.getFilePath())) || (this.m_RawPhotoCaptureHandleList.isEmpty())) {}
      do
      {
        return;
        this.m_RawPhotoCaptureHandleList.remove(paramMediaEventArgs.getCaptureHandle());
      } while (!this.m_RawPhotoCaptureHandleList.isEmpty());
      setReadOnly(PROP_IS_CAPTURING_RAW_PHOTO, Boolean.valueOf(false));
      return;
    }
    Log.w(this.TAG, "resetCapturingRawPhotoProperty() - camera is null");
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
  
  private boolean resumeVideoCaptureInternal(VideoCaptureHandle paramVideoCaptureHandle, int paramInt)
  {
    if (this.m_VideoCaptureHandle != paramVideoCaptureHandle)
    {
      Log.w(this.TAG, "resumeVideoCaptureInternal() - Invalid handle");
      return false;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    case 3: 
    case 4: 
    case 5: 
    default: 
      Log.e(this.TAG, "resumeVideoCaptureInternal() - Current capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return false;
    case 1: 
    case 6: 
      return true;
    }
    paramVideoCaptureHandle = (Camera)get(PROP_CAMERA);
    if (paramVideoCaptureHandle == null)
    {
      Log.e(this.TAG, "resumeVideoCaptureInternal() - No camera");
      return false;
    }
    setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.RESUMING);
    if (get(PROP_VIDEO_CAPTURE_STATE) != VideoCaptureState.RESUMING)
    {
      Log.e(this.TAG, "resumeVideoCaptureInternal() - Interrupted by other operations");
      return false;
    }
    try
    {
      long l = SystemClock.elapsedRealtime();
      if (!paramVideoCaptureHandle.resumeVideoRecording(0))
      {
        Log.e(this.TAG, "resumeVideoCaptureInternal() - Fail to resume");
        return false;
      }
      this.m_MediaRecorderPauseTimeTotal += l - this.m_MediaRecorderPauseTime;
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.CAPTURING);
      if (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.CAPTURING) {
        return true;
      }
    }
    catch (Throwable paramVideoCaptureHandle)
    {
      Log.e(this.TAG, "resumeVideoCaptureInternal() - Fail to resume", paramVideoCaptureHandle);
      return false;
    }
    return false;
  }
  
  private boolean setMediaTypeInternal(MediaType paramMediaType)
  {
    if (get(PROP_MEDIA_TYPE) == paramMediaType) {
      return true;
    }
    Log.v(this.TAG, "setMediaTypeInternal() - Media type : ", paramMediaType);
    Camera localCamera;
    int i;
    label226:
    PropertyKey localPropertyKey;
    switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[paramMediaType.ordinal()])
    {
    default: 
      Log.e(this.TAG, "setMediaTypeInternal() - Unknown media type : " + paramMediaType);
      return false;
    case 1: 
      switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
      {
      default: 
        Log.e(this.TAG, "setMediaTypeInternal() - Current video capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
        return false;
      }
      setReadOnly(PROP_IS_VIDEO_SNAPSHOT_ENABLED, Boolean.valueOf(false));
      localCamera = (Camera)get(PROP_CAMERA);
      switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)get(PROP_CAMERA_PREVIEW_STATE)).ordinal()])
      {
      default: 
        i = 0;
        setReadOnly(PROP_MEDIA_TYPE, paramMediaType);
        if (localCamera != null)
        {
          localPropertyKey = Camera.PROP_IS_RECORDING_MODE;
          if (paramMediaType != MediaType.VIDEO) {
            break label439;
          }
        }
        break;
      }
      break;
    }
    label439:
    for (boolean bool = true;; bool = false)
    {
      localCamera.set(localPropertyKey, Boolean.valueOf(bool));
      if (i != 0)
      {
        Log.w(this.TAG, "setMediaTypeInternal() - Restart preview");
        if (!startCameraPreview(localCamera, null, null, null)) {
          Log.e(this.TAG, "setMediaTypeInternal() - Fail to restart preview");
        }
      }
      return true;
      switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
      {
      default: 
        Log.e(this.TAG, "setMediaTypeInternal() - Current photo capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
        return false;
      }
      if (!this.m_VideoSnapshotDisableHandles.isEmpty()) {
        break;
      }
      setReadOnly(PROP_IS_VIDEO_SNAPSHOT_ENABLED, Boolean.valueOf(true));
      break;
      Log.w(this.TAG, "setMediaTypeInternal() - Stop preview to change media type");
      i = 1;
      if (stopCameraPreview(localCamera)) {
        break label226;
      }
      Log.e(this.TAG, "setMediaTypeInternal() - Fail to stop preview");
      return false;
    }
  }
  
  private boolean startCameraPreviewInternal(Camera paramCamera, Size paramSize, Resolution paramResolution, Object paramObject, int paramInt)
  {
    int i = this.m_PendingCameraPreviewStopRequests.size() - 1;
    for (;;)
    {
      if (i >= 0)
      {
        if (((CameraPreviewStopRequest)this.m_PendingCameraPreviewStopRequests.get(i)).camera == paramCamera)
        {
          Log.w(this.TAG, "startCameraPreviewInternal() - Cancel pending preview stop rquest");
          this.m_PendingCameraPreviewStopRequests.remove(i);
        }
      }
      else
      {
        if (!HandlerUtils.hasMessages(this, 10070)) {
          break;
        }
        Log.w(this.TAG, "startCameraPreviewInternal() - Pending preview stop detected");
        return false;
      }
      i -= 1;
    }
    if (!openCameraInternal(paramCamera, 0))
    {
      Log.e(this.TAG, "startCameraPreviewInternal() - Fail to open camera");
      return false;
    }
    if (HandlerUtils.hasMessages(this, 10070))
    {
      Log.w(this.TAG, "startCameraPreviewInternal() - Pending preview stop detected after opening camera");
      return false;
    }
    switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)paramCamera.get(Camera.PROP_PREVIEW_STATE)).ordinal()])
    {
    case 3: 
    default: 
      if (paramObject != null)
      {
        Log.w(this.TAG, "startCameraPreviewInternal() - Change preview receiver to " + paramObject);
        paramCamera.set(Camera.PROP_PREVIEW_RECEIVER, paramObject);
        label229:
        if (paramSize == null) {
          break label569;
        }
        Log.w(this.TAG, "startCameraPreviewInternal() - Preview size : " + paramSize);
        paramCamera.set(Camera.PROP_PREVIEW_SIZE, paramSize);
        label271:
        this.m_CurrentResolution = paramResolution;
        if (paramResolution != null)
        {
          if (paramResolution.getFps() <= 0) {
            break label582;
          }
          Log.v(this.TAG, "startCameraPreviewInternal() - Set preview fps : ", Integer.valueOf(paramResolution.getFps()));
          paramCamera.set(Camera.PROP_PREVIEW_FPS_RANGE, new Range(Integer.valueOf(paramResolution.getFps()), Integer.valueOf(paramResolution.getFps())));
        }
      }
      break;
    }
    for (;;)
    {
      Log.w(this.TAG, "startCameraPreviewInternal() - Start preview for camera " + paramCamera);
      if (paramCamera.startPreview(0)) {
        break label606;
      }
      Log.e(this.TAG, "startCameraPreviewInternal() - Fail to start preview for camera " + paramCamera);
      return false;
      if ((paramObject == null) || (paramCamera.get(Camera.PROP_PREVIEW_RECEIVER) == paramObject)) {
        break;
      }
      Log.w(this.TAG, "startCameraPreviewInternal() - Preview receiver changed, stop preview first");
      paramCamera.stopPreview(0);
      break;
      Log.w(this.TAG, "startCameraPreviewInternal() - Start while stopping");
      i = this.m_PendingCameraPreviewStartRequests.size() - 1;
      while (i >= 0)
      {
        CameraPreviewStartRequest localCameraPreviewStartRequest = (CameraPreviewStartRequest)this.m_PendingCameraPreviewStartRequests.get(i);
        if (localCameraPreviewStartRequest.camera == paramCamera)
        {
          localCameraPreviewStartRequest.previewSize = paramSize;
          localCameraPreviewStartRequest.previewReceiver = paramObject;
          localCameraPreviewStartRequest.flags = paramInt;
          localCameraPreviewStartRequest.resolution = paramResolution;
          return true;
        }
        i -= 1;
      }
      this.m_PendingCameraPreviewStartRequests.add(new CameraPreviewStartRequest(paramCamera, paramSize, paramResolution, paramObject, paramInt));
      return true;
      Log.v(this.TAG, "startCameraPreviewInternal() - Use current preview receiver");
      break label229;
      label569:
      Log.v(this.TAG, "startCameraPreviewInternal() - Use current preview size");
      break label271;
      label582:
      Log.v(this.TAG, "startCameraPreviewInternal() - Reset preview fps");
      paramCamera.set(Camera.PROP_PREVIEW_FPS_RANGE, null);
    }
    label606:
    this.m_CameraPreviewStartFlags = paramInt;
    if (!this.m_IsNormalComponentsCreated)
    {
      this.m_IsNormalComponentsCreated = true;
      bindToHighComponents();
      this.m_ComponentManager.createComponents(ComponentCreationPriority.NORMAL, new Object[] { this });
      bindToNormalComponents();
    }
    return true;
  }
  
  private boolean stopCameraPreviewInternal(final Camera paramCamera, final boolean[] paramArrayOfBoolean, int paramInt)
  {
    try
    {
      i = this.m_PendingCameraPreviewStartRequests.size() - 1;
      if (i >= 0)
      {
        if (((CameraPreviewStartRequest)this.m_PendingCameraPreviewStartRequests.get(i)).camera == paramCamera)
        {
          Log.w(this.TAG, "stopCameraPreviewInternal() - Remove pending start request");
          this.m_PendingCameraPreviewStartRequests.remove(i);
        }
      }
      else
      {
        if ((paramCamera.get(Camera.PROP_PREVIEW_STATE) == OperationState.STARTING) || (Handle.isValid(this.m_CameraCaptureHandle)))
        {
          Log.w(this.TAG, "stopCameraPreviewInternal() - Wait for preview start or capture complete");
          this.m_PendingCameraPreviewStopRequests.add(new CameraPreviewStopRequest(paramCamera, paramArrayOfBoolean, paramInt));
          return true;
        }
        Log.v(this.TAG, "stopCameraPreviewInternal() - Stop preview [start]");
        paramCamera.stopPreview(0);
        Log.v(this.TAG, "stopCameraPreviewInternal() - Stop preview [end]");
        if (Handle.isValid(this.m_VideoCaptureHandle))
        {
          Log.w(this.TAG, "stopCameraPreviewInternal() - Stop video recording");
          stopVideoCaptureInternal(this.m_VideoCaptureHandle, CaptureCompleteReason.NORMAL, 0);
        }
        if (paramArrayOfBoolean == null) {
          break label310;
        }
        if (paramCamera.get(Camera.PROP_PREVIEW_STATE) != OperationState.STOPPING) {
          try
          {
            Log.w(this.TAG, "stopCameraPreviewInternal() - Notify waiting thread");
            paramArrayOfBoolean[0] = true;
            paramArrayOfBoolean.notifyAll();
            return true;
          }
          finally {}
        }
      }
    }
    catch (Throwable paramCamera)
    {
      for (;;)
      {
        int i;
        Log.e(this.TAG, "stopCameraPreviewInternal() - Error stopping camera preview", paramCamera);
        if (paramArrayOfBoolean != null) {}
        try
        {
          Log.w(this.TAG, "stopCameraPreviewInternal() - Notify waiting thread");
          paramArrayOfBoolean[0] = true;
          paramArrayOfBoolean.notifyAll();
          return false;
        }
        finally {}
        Log.w(this.TAG, "stopCameraPreviewInternal() - Wait for camera preview stop");
        paramCamera.addCallback(Camera.PROP_PREVIEW_STATE, new PropertyChangedCallback()
        {
          public void onPropertyChanged(PropertySource arg1, PropertyKey<OperationState> paramAnonymousPropertyKey, PropertyChangeEventArgs<OperationState> paramAnonymousPropertyChangeEventArgs)
          {
            if (paramAnonymousPropertyChangeEventArgs.getOldValue() == OperationState.STOPPING) {}
            synchronized (paramArrayOfBoolean)
            {
              Log.w(CameraThread.-get0(CameraThread.this), "stopCameraPreviewInternal() - Notify waiting thread");
              paramArrayOfBoolean[0] = true;
              paramArrayOfBoolean.notifyAll();
              paramCamera.removeCallback(Camera.PROP_PREVIEW_STATE, this);
              return;
            }
          }
        });
        return true;
        i -= 1;
      }
    }
    label310:
    return true;
  }
  
  private void stopPhotoCapture(final PhotoCaptureHandle paramPhotoCaptureHandle)
  {
    if (isDependencyThread()) {
      stopPhotoCaptureInternal(paramPhotoCaptureHandle, CaptureCompleteReason.NORMAL);
    }
    while (HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        CameraThread.-wrap31(CameraThread.this, paramPhotoCaptureHandle, CaptureCompleteReason.NORMAL);
      }
    })) {
      return;
    }
    Log.e(this.TAG, "stopPhotoCapture() - Fail to perform cross-thread operation");
  }
  
  private void stopPhotoCaptureInternal(PhotoCaptureHandle paramPhotoCaptureHandle, CaptureCompleteReason paramCaptureCompleteReason)
  {
    if (this.m_PhotoCaptureHandle != paramPhotoCaptureHandle)
    {
      Log.e(this.TAG, "stopPhotoCaptureInternal() - Invalid handle");
      return;
    }
    Log.v(this.TAG, "stopPhotoCaptureInternal() - Handle : ", paramPhotoCaptureHandle);
    if (get(PROP_PHOTO_CAPTURE_STATE) == PhotoCaptureState.STARTING)
    {
      Log.w(this.TAG, "stopPhotoCaptureInternal() - Stop when locking focus");
      completeCaptureInternal(null, this.m_PhotoCaptureHandle, CaptureCompleteReason.CANCELLED, false);
      return;
    }
    Camera localCamera = (Camera)get(PROP_CAMERA);
    if (localCamera == null)
    {
      Log.e(this.TAG, "stopPhotoCaptureInternal() - No camera");
      return;
    }
    for (;;)
    {
      try
      {
        if (paramPhotoCaptureHandle.captureHandler != null) {
          break;
        }
        if (paramPhotoCaptureHandle.isBurstPhotoCapture())
        {
          getHandler().sendEmptyMessage(10060);
          this.m_PreparedPhotoCaptureCompleteReason = paramCaptureCompleteReason;
          return;
        }
        if (!this.m_IsDefaultShutterReceived)
        {
          Log.w(this.TAG, "stopPhotoCaptureInternal() - Shutter not received yet, stop immediately");
          getHandler().sendEmptyMessage(10060);
        }
        else
        {
          getHandler().sendEmptyMessageDelayed(10060, 2000L);
        }
      }
      catch (Throwable paramPhotoCaptureHandle)
      {
        Log.e(this.TAG, "stopPhotoCaptureInternal() - Fail to stop capture", paramPhotoCaptureHandle);
        return;
      }
    }
    Log.w(this.TAG, "stopPhotoCaptureInternal() - Use " + paramPhotoCaptureHandle.captureHandler + " to stop capture");
    if (!paramPhotoCaptureHandle.captureHandler.stopCapture(localCamera, paramPhotoCaptureHandle, paramCaptureCompleteReason)) {
      Log.e(this.TAG, "stopPhotoCaptureInternal() - Fail to stop capture");
    }
  }
  
  private void stopVideoCapture(final VideoCaptureHandle paramVideoCaptureHandle, final CaptureCompleteReason paramCaptureCompleteReason, final int paramInt)
  {
    if (isDependencyThread()) {
      stopVideoCaptureInternal(paramVideoCaptureHandle, paramCaptureCompleteReason, paramInt);
    }
    while (HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        CameraThread.-wrap33(CameraThread.this, paramVideoCaptureHandle, paramCaptureCompleteReason, paramInt);
      }
    })) {
      return;
    }
    Log.e(this.TAG, "stopVideoCapture() - Fail to perform cross-thread operation");
  }
  
  private void stopVideoCaptureInternal(VideoCaptureHandle paramVideoCaptureHandle, CaptureCompleteReason paramCaptureCompleteReason, int paramInt)
  {
    if ((this.m_VideoCaptureHandle != paramVideoCaptureHandle) || (paramVideoCaptureHandle == null))
    {
      Log.w(this.TAG, "stopVideoCaptureInternal() - Invalid handle");
      return;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    case 4: 
    case 5: 
    default: 
      Log.w(this.TAG, "stopVideoCaptureInternal() - Video capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
    }
    boolean bool;
    int k;
    if (get(PROP_VIDEO_CAPTURE_STATE) == VideoCaptureState.STARTING)
    {
      bool = getHandler().hasMessages(10010);
      i = 0;
      m = 0;
      int n = 0;
      j = m;
      k = n;
      switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
      {
      default: 
        k = n;
      }
    }
    for (int j = m;; j = i)
    {
      setReadOnly(PROP_VIDEO_CAPTURE_STATE, VideoCaptureState.STOPPING);
      getHandler().removeMessages(10030);
      if (!Handle.isValid(this.m_PhotoCaptureHandle)) {
        break label293;
      }
      Log.w(this.TAG, "stopVideoCaptureInternal() - Stop video snapshot and wait");
      this.m_LastVideoCaptureCompleteReason = paramCaptureCompleteReason;
      stopPhotoCaptureInternal(this.m_PhotoCaptureHandle, CaptureCompleteReason.NORMAL);
      return;
      bool = false;
      break;
      i = 1;
      k = 1;
    }
    label293:
    int m = 0;
    int i = m;
    if (this.m_MediaRecorder != null)
    {
      if (!bool) {
        break label576;
      }
      i = m;
    }
    for (;;)
    {
      if (((paramInt & 0x2) == 0) && (Handle.isValid(this.m_VideoStopSoundHandle)) && (isShutterSoundNeeded())) {
        this.m_AudioManager.playSound(this.m_VideoStopSoundHandle, 0);
      }
      paramVideoCaptureHandle.complete();
      Object localObject = paramCaptureCompleteReason;
      if (bool)
      {
        Log.w(this.TAG, "stopVideoCaptureInternal() - Stop while starting");
        getHandler().removeMessages(10010);
        localObject = paramCaptureCompleteReason;
        if (paramCaptureCompleteReason == CaptureCompleteReason.NORMAL) {
          localObject = CaptureCompleteReason.CANCELLED;
        }
      }
      paramCaptureCompleteReason = (Camera)get(PROP_CAMERA);
      if ((bool) || (i != 0)) {
        Log.w(this.TAG, "stopVideoCaptureInternal() - Delete " + this.m_VideoFilePath);
      }
      try
      {
        if (this.m_VideoFileDescriptor == null) {
          new File(this.m_VideoFilePath).delete();
        }
        for (;;)
        {
          if (this.m_MediaRecorder != null) {}
          switch (-getcom-oneplus-camera-OperationStateSwitchesValues()[((OperationState)paramCaptureCompleteReason.get(Camera.PROP_PREVIEW_STATE)).ordinal()])
          {
          case 2: 
          default: 
            Log.w(this.TAG, "stopVideoCaptureInternal() - Release media recorder after preview start or stop");
            paramCaptureCompleteReason.unbindMediaRecorder(0);
            paramVideoCaptureHandle = this.m_VideoCaptureHandlerHandle;
            paramCaptureCompleteReason = this.m_VideoCaptureHandle;
            if (!bool) {
              break label1053;
            }
            bool = false;
            completeCaptureInternal(paramVideoCaptureHandle, paramCaptureCompleteReason, (CaptureCompleteReason)localObject, bool);
            return;
            label576:
            localObject = (Camera)get(PROP_CAMERA);
            long l1 = SystemClock.elapsedRealtime();
            long l2 = l1 - this.m_MediaRecorderStartTime;
            if ((j != 0) || (k != 0)) {
              this.m_MediaRecorderPauseTimeTotal += l1 - this.m_MediaRecorderPauseTime;
            }
            l1 = l2;
            if (this.m_MediaRecorderPauseTimeTotal > 0L)
            {
              l1 = Math.max(0L, l2 - this.m_MediaRecorderPauseTimeTotal);
              Log.d(this.TAG, "stopVideoCaptureInternal() - Total paused time : ", Long.valueOf(this.m_MediaRecorderPauseTimeTotal), " ms");
            }
            float f;
            if (this.m_RecordingTimeRatioHandles.isEmpty())
            {
              f = 1.0F;
              label689:
              if (Math.abs(f - 1.0F) > 0.001F) {
                break label888;
              }
            }
            label888:
            for (l2 = 1000L;; l2 = (1000.0F / f))
            {
              Log.d(this.TAG, "stopVideoCaptureInternal() - Minimum duration : ", Long.valueOf(l2));
              if ((l1 >= l2) || (j != 0)) {}
              try
              {
                Log.w(this.TAG, "stopVideoCaptureInternal() - Duration is too short, resume video recording");
                if (!((Camera)localObject).resumeVideoRecording(0)) {
                  Log.e(this.TAG, "stopVideoCaptureInternal() - Fail to resume video recording");
                }
                long l3 = l2 - l1;
                Log.w(this.TAG, "stopVideoCaptureInternal() - Duration is too short : " + l1 + " ms, delay " + l3 + " ms");
                Thread.sleep(l3);
              }
              catch (Throwable localThrowable)
              {
                VideoCaptureHandler localVideoCaptureHandler;
                for (;;) {}
              }
              i = m;
              if (((Camera)localObject).stopVideoRecording(0)) {
                break;
              }
              i = m;
              if (l1 >= l2) {
                break;
              }
              i = 1;
              break;
              f = ((RecordingTimeRatioHandle)this.m_RecordingTimeRatioHandles.get(this.m_RecordingTimeRatioHandles.size() - 1)).ratio;
              break label689;
            }
            if (this.m_VideoFileDescriptor == null)
            {
              localVideoCaptureHandler = paramVideoCaptureHandle.captureHandler;
              if ((localVideoCaptureHandler == null) || (!localVideoCaptureHandler.saveVideo(paramVideoCaptureHandle, this.m_VideoFilePath, (Size)paramCaptureCompleteReason.get(Camera.PROP_VIDEO_SIZE), this.m_VideoFileDescriptor, 0)))
              {
                saveMedia(new VideoSaveTask(getContext(), paramVideoCaptureHandle, this.m_VideoFilePath, (Size)paramCaptureCompleteReason.get(Camera.PROP_VIDEO_SIZE)));
                continue;
                closeVideoFileDescriptor();
              }
            }
            break;
          }
        }
      }
      catch (Throwable paramVideoCaptureHandle)
      {
        for (;;)
        {
          Log.w(this.TAG, "stopVideoCaptureInternal() - Fail to delete " + this.m_VideoFilePath, paramVideoCaptureHandle);
          continue;
          Log.v(this.TAG, "stopVideoCaptureInternal() - Release media recorder");
          this.m_MediaRecorder.release();
          this.m_MediaRecorder = null;
          continue;
          label1053:
          bool = true;
        }
      }
    }
  }
  
  /* Error */
  public final void addComponentBuilders(final ComponentBuilder[] paramArrayOfComponentBuilder)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 2517	com/oneplus/camera/CameraThread:isDependencyThread	()Z
    //   4: ifeq +20 -> 24
    //   7: aload_0
    //   8: getfield 373	com/oneplus/camera/CameraThread:m_ComponentManager	Lcom/oneplus/base/component/ComponentManager;
    //   11: aload_1
    //   12: iconst_1
    //   13: anewarray 1030	java/lang/Object
    //   16: dup
    //   17: iconst_0
    //   18: aload_0
    //   19: aastore
    //   20: invokevirtual 2626	com/oneplus/base/component/ComponentManager:addComponentBuilders	([Lcom/oneplus/base/component/ComponentBuilder;[Ljava/lang/Object;)V
    //   23: return
    //   24: aload_0
    //   25: monitorenter
    //   26: aload_0
    //   27: getfield 373	com/oneplus/camera/CameraThread:m_ComponentManager	Lcom/oneplus/base/component/ComponentManager;
    //   30: ifnull +20 -> 50
    //   33: aload_0
    //   34: new 18	com/oneplus/camera/CameraThread$14
    //   37: dup
    //   38: aload_0
    //   39: aload_1
    //   40: invokespecial 2629	com/oneplus/camera/CameraThread$14:<init>	(Lcom/oneplus/camera/CameraThread;[Lcom/oneplus/base/component/ComponentBuilder;)V
    //   43: invokestatic 2523	com/oneplus/base/HandlerUtils:post	(Lcom/oneplus/base/HandlerObject;Ljava/lang/Runnable;)Z
    //   46: pop
    //   47: aload_0
    //   48: monitorexit
    //   49: return
    //   50: aload_0
    //   51: getfield 959	com/oneplus/camera/CameraThread:m_InitialComponentBuilders	Ljava/util/List;
    //   54: aload_1
    //   55: invokestatic 2635	java/util/Arrays:asList	([Ljava/lang/Object;)Ljava/util/List;
    //   58: invokeinterface 2639 2 0
    //   63: pop
    //   64: goto -17 -> 47
    //   67: astore_1
    //   68: aload_0
    //   69: monitorexit
    //   70: aload_1
    //   71: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	72	0	this	CameraThread
    //   0	72	1	paramArrayOfComponentBuilder	ComponentBuilder[]
    // Exception table:
    //   from	to	target	type
    //   26	47	67	finally
    //   50	64	67	finally
  }
  
  public final CaptureHandle capturePhoto()
  {
    return capturePhoto(new PhotoParams(1), 0);
  }
  
  public final CaptureHandle capturePhoto(PhotoParams paramPhotoParams)
  {
    return capturePhoto(paramPhotoParams, 0);
  }
  
  public final CaptureHandle capturePhoto(final PhotoParams paramPhotoParams, int paramInt)
  {
    PhotoParams localPhotoParams = paramPhotoParams;
    if (paramPhotoParams == null) {
      localPhotoParams = new PhotoParams(1);
    }
    if (localPhotoParams.frameCount == 0)
    {
      Log.e(this.TAG, "capturePhoto() - Invalid frame count");
      return null;
    }
    paramPhotoParams = new PhotoCaptureHandle((Camera)get(PROP_CAMERA), localPhotoParams.captureMode, (Rotation)get(PROP_CAPTURE_ROTATION), localPhotoParams.frameCount, paramInt);
    if (isDependencyThread())
    {
      if (capturePhotoInternal(paramPhotoParams, false)) {
        return paramPhotoParams;
      }
      return null;
    }
    if (HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        CameraThread.-wrap0(CameraThread.this, paramPhotoParams, false);
      }
    }))
    {
      Log.v(this.TAG, "capturePhoto() - Create handle ", paramPhotoParams);
      return paramPhotoParams;
    }
    Log.e(this.TAG, "capturePhoto() - Fail to perform cross-thread operation");
    return null;
  }
  
  public final CaptureHandle captureVideo(final VideoParams paramVideoParams, int paramInt)
  {
    if (paramVideoParams == null)
    {
      Log.e(this.TAG, "captureVideo() - No video parameters");
      return null;
    }
    Resolution localResolution = paramVideoParams.resolution;
    if (localResolution == null)
    {
      Log.e(this.TAG, "captureVideo() - No video resolution");
      return null;
    }
    if (localResolution.getTargetType() != MediaType.VIDEO)
    {
      Log.e(this.TAG, "captureVideo() - Invalid resolution : " + localResolution);
      return null;
    }
    paramVideoParams = new VideoCaptureHandle((Camera)get(PROP_CAMERA), (Rotation)get(PROP_CAPTURE_ROTATION), paramVideoParams.clone(), paramInt);
    if (isDependencyThread())
    {
      if (captureVideoInternal(paramVideoParams, false)) {
        return paramVideoParams;
      }
      return null;
    }
    if (HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        CameraThread.-wrap1(CameraThread.this, paramVideoParams, false);
      }
    })) {
      return paramVideoParams;
    }
    Log.e(this.TAG, "captureVideo() - Fail to perform cross-thread operation");
    return null;
  }
  
  public void checkRemainingMediaCount()
  {
    if (isDependencyThread()) {
      checkRemainingMediaCountInternal();
    }
    while ((HandlerUtils.hasMessages(this, 10020)) || (HandlerUtils.sendMessage(this, 10020))) {
      return;
    }
    Log.e(this.TAG, "checkRemainingMediaCount() - Fail to perform cross-thread operation");
  }
  
  public final void closeCamera(final Camera paramCamera)
  {
    if (paramCamera == null)
    {
      Log.e(this.TAG, "closeCamera() - No camera to close");
      return;
    }
    if (isDependencyThread()) {
      closeCameraInternal(paramCamera);
    }
    while (HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        CameraThread.-wrap7(CameraThread.this, paramCamera);
      }
    })) {
      return;
    }
    Log.e(this.TAG, "closeCamera() - Fail to perform cross-thread operation");
  }
  
  public final void closeCameras()
  {
    if (isDependencyThread()) {
      closeCamerasInternal();
    }
    while (HandlerUtils.post(this, this.m_CloseCamerasRunnable)) {
      return;
    }
    Log.e(this.TAG, "closeCameras() - Fail to perform cross-thread operation");
  }
  
  public final boolean completeCapture(Handle paramHandle, CaptureHandle paramCaptureHandle)
  {
    return completeCapture(paramHandle, paramCaptureHandle, CaptureCompleteReason.NORMAL);
  }
  
  public final boolean completeCapture(Handle paramHandle, CaptureHandle paramCaptureHandle, CaptureCompleteReason paramCaptureCompleteReason)
  {
    verifyAccess();
    if (paramHandle == null)
    {
      Log.e(this.TAG, "completeCapture() - No capture handler handle");
      return false;
    }
    if (paramCaptureHandle == null)
    {
      Log.e(this.TAG, "completeCapture() - No capture handle");
      return false;
    }
    if (paramCaptureCompleteReason == null)
    {
      Log.e(this.TAG, "completeCapture() - No complete reason");
      return false;
    }
    return completeCaptureInternal(paramHandle, paramCaptureHandle, paramCaptureCompleteReason, true);
  }
  
  public Handle disableVideoSnapshot()
  {
    verifyAccess();
    Handle local25 = new Handle("DisableVideoSnapshot")
    {
      protected void onClose(int paramAnonymousInt)
      {
        CameraThread.-wrap9(CameraThread.this, this);
      }
    };
    this.m_VideoSnapshotDisableHandles.add(local25);
    if (this.m_VideoSnapshotDisableHandles.size() == 1) {
      setReadOnly(PROP_IS_VIDEO_SNAPSHOT_ENABLED, Boolean.valueOf(false));
    }
    return local25;
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
  
  protected final ComponentManager getComponentManager()
  {
    return this.m_ComponentManager;
  }
  
  public final Context getContext()
  {
    return this.m_Context;
  }
  
  public byte[] getLastCapturedJpeg()
  {
    return this.m_LastCapturedJpeg;
  }
  
  protected void handleMessage(Message paramMessage)
  {
    boolean bool = false;
    switch (paramMessage.what)
    {
    }
    for (;;)
    {
      super.handleMessage(paramMessage);
      return;
      captureVideoInternal(this.m_VideoCaptureHandle, true);
      return;
      checkRemainingMediaCountInternal();
      return;
      checkVideoRecordingLimitation();
      return;
      onShutter(paramMessage.arg1);
      ((PhotoCaptureHandle)paramMessage.obj).isFastCaptureEnabled = true;
      return;
      if (paramMessage.arg1 != 0) {
        bool = true;
      }
      notifyRequiredPermissionsState(bool);
      return;
      setReadOnly(PROP_SCREEN_SIZE, (ScreenSize)paramMessage.obj);
      return;
      Object[] arrayOfObject = (Object[])paramMessage.obj;
      stopCameraPreviewInternal((Camera)arrayOfObject[0], (boolean[])arrayOfObject[1], paramMessage.arg1);
      return;
      Log.w(this.TAG, "stopPhotoCaptureInternal() - Use default photo capture stop process");
      this.m_CameraCaptureHandle = Handle.close(this.m_CameraCaptureHandle);
      this.m_BurstCaptureSoundStreamHandle = Handle.close(this.m_BurstCaptureSoundStreamHandle);
      getHandler().removeMessages(10040);
      return;
      if (this.m_StorageManager == null)
      {
        this.m_StorageManager = ((StorageManager)CameraApplication.current().findComponent(StorageManager.class));
        if (this.m_StorageManager == null) {
          getHandler().sendEmptyMessageDelayed(10080, 50L);
        }
      }
    }
  }
  
  public boolean isShutterSoundNeeded()
  {
    if (this.m_Settings != null) {
      return this.m_Settings.getBoolean("ShutterSound");
    }
    return false;
  }
  
  public void notifyRequiredPermissionsState(boolean paramBoolean)
  {
    if (!isDependencyThread())
    {
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        HandlerUtils.sendMessage(this, 10050, i, 0, null);
        return;
      }
    }
    setReadOnly(PROP_ALL_REQUIRED_PERMISSIONS_GRANTED, Boolean.valueOf(paramBoolean));
  }
  
  protected void onStarted()
  {
    super.onStarted();
    if (!bindToInitialComponents()) {
      throw new RuntimeException("Fail to bind components.");
    }
  }
  
  protected void onStarting()
  {
    super.onStarting();
    enableEventLogs(EVENT_SHUTTER, 256);
    enablePropertyLogs(PROP_ALL_REQUIRED_PERMISSIONS_GRANTED, 1);
    enablePropertyLogs(PROP_CAMERA_PREVIEW_STATE, 1);
    enablePropertyLogs(PROP_CAPTURE_ROTATION, 1);
    enablePropertyLogs(PROP_PHOTO_CAPTURE_STATE, 1);
    enablePropertyLogs(PROP_REMAINING_PHOTO_COUNT, 1);
    enablePropertyLogs(PROP_REMAINING_VIDEO_DURATION_SECONDS, 1);
    enablePropertyLogs(PROP_VIDEO_CAPTURE_STATE, 1);
    if (Build.VERSION.SDK_INT < 23)
    {
      Log.v(this.TAG, "onStarting() - No need to request permissions");
      setReadOnly(PROP_ALL_REQUIRED_PERMISSIONS_GRANTED, Boolean.valueOf(true));
    }
    this.m_PhotoCaptureHandlerHandles = new ArrayList();
    this.m_VideoCaptureHandlerHandles = new ArrayList();
    this.m_VideoSnapshotDisableHandles = new ArrayList();
    this.m_Settings = new Settings(this.m_Context, null, true);
    try
    {
      if (this.m_InitialScreenSize != null)
      {
        Log.v(this.TAG, "onStarting() - Initial screen size : ", this.m_InitialScreenSize);
        setReadOnly(PROP_SCREEN_SIZE, this.m_InitialScreenSize);
        this.m_InitialScreenSize = null;
      }
      if (this.m_InitialMediaType != null)
      {
        Log.v(this.TAG, "onStarting() - Initial media type : ", this.m_InitialMediaType);
        setReadOnly(PROP_MEDIA_TYPE, this.m_InitialMediaType);
        if (this.m_InitialMediaType == MediaType.VIDEO) {
          setReadOnly(PROP_IS_VIDEO_SNAPSHOT_ENABLED, Boolean.valueOf(true));
        }
      }
      this.m_ComponentManager = new ComponentManager();
      this.m_ComponentManager.addComponentBuilders(DEFAULT_COMPONENT_BUILDERS, new Object[] { this });
      this.m_ComponentManager.addHandler(ComponentManager.EVENT_COMPONENT_ADDED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
        {
          CameraThread.-wrap26(CameraThread.this, CameraThread.EVENT_COMPONENT_ADDED, paramAnonymousComponentEventArgs);
        }
      });
      this.m_ComponentManager.addHandler(ComponentManager.EVENT_COMPONENT_REMOVED, new EventHandler()
      {
        public void onEventReceived(EventSource paramAnonymousEventSource, EventKey<ComponentEventArgs<Component>> paramAnonymousEventKey, ComponentEventArgs<Component> paramAnonymousComponentEventArgs)
        {
          CameraThread.-wrap26(CameraThread.this, CameraThread.EVENT_COMPONENT_REMOVED, paramAnonymousComponentEventArgs);
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
      return;
    }
    finally {}
  }
  
  protected void onStopping()
  {
    closeCamerasInternal();
    this.m_ComponentManager.release();
    if (this.m_Settings != null) {
      this.m_Settings.release();
    }
    if (this.m_RawPhotoCaptureHandleList != null) {
      this.m_RawPhotoCaptureHandleList.clear();
    }
    super.onStopping();
  }
  
  public final boolean openCamera(Camera paramCamera)
  {
    return openCamera(paramCamera, 0);
  }
  
  public final boolean openCamera(final Camera paramCamera, final int paramInt)
  {
    if (paramCamera == null)
    {
      Log.e(this.TAG, "openCamera() - No camera");
      return false;
    }
    if (isDependencyThread()) {
      return openCameraInternal(paramCamera, paramInt);
    }
    if (HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        CameraThread.-wrap2(CameraThread.this, paramCamera, paramInt);
      }
    })) {
      return true;
    }
    Log.e(this.TAG, "openCamera() - Fail to perform cross-thread operation");
    return false;
  }
  
  public boolean pauseVideoCapture(final CaptureHandle paramCaptureHandle)
  {
    if (paramCaptureHandle == null)
    {
      Log.e(this.TAG, "pauseVideoCapture() - No handle");
      return false;
    }
    if (!(paramCaptureHandle instanceof VideoCaptureHandle))
    {
      Log.e(this.TAG, "pauseVideoCapture() - Invalid handle : " + paramCaptureHandle);
      return false;
    }
    if (isDependencyThread()) {
      return pauseVideoCaptureInternal((VideoCaptureHandle)paramCaptureHandle, 0);
    }
    getHandler().post(new Runnable()
    {
      public void run()
      {
        CameraThread.-wrap3(CameraThread.this, (CameraThread.VideoCaptureHandle)paramCaptureHandle, 0);
      }
    });
  }
  
  public final Handle playCameraTimerSound(long paramLong)
  {
    verifyAccess();
    if (paramLong < 2L) {
      return null;
    }
    if ((!isShutterSoundNeeded()) || (this.m_AudioManager == null)) {
      return null;
    }
    if (paramLong == 2L) {
      return this.m_AudioManager.playSound(this.m_CameraTimer2SecSoundHandle, 0);
    }
    return this.m_AudioManager.playSound(this.m_CameraTimerSoundHandle, 0);
  }
  
  public void playDefaultShutterSound()
  {
    verifyAccess();
    if (!Handle.isValid(this.m_DefaultShutterSoundHandle))
    {
      Log.w(this.TAG, "playDefaultShutterSound() - No shutter sound to play");
      return;
    }
    this.m_AudioManager.playSound(this.m_DefaultShutterSoundHandle, 0);
  }
  
  public void removeComponent(Component paramComponent)
  {
    verifyAccess();
    this.m_ComponentManager.removeComponent(paramComponent);
  }
  
  public boolean resumeVideoCapture(final CaptureHandle paramCaptureHandle)
  {
    if (paramCaptureHandle == null)
    {
      Log.e(this.TAG, "resumeVideoCapture() - No handle");
      return false;
    }
    if (!(paramCaptureHandle instanceof VideoCaptureHandle))
    {
      Log.e(this.TAG, "resumeVideoCapture() - Invalid handle : " + paramCaptureHandle);
      return false;
    }
    if (isDependencyThread()) {
      return resumeVideoCaptureInternal((VideoCaptureHandle)paramCaptureHandle, 0);
    }
    getHandler().post(new Runnable()
    {
      public void run()
      {
        CameraThread.-wrap4(CameraThread.this, (CameraThread.VideoCaptureHandle)paramCaptureHandle, 0);
      }
    });
  }
  
  public Handle saveMedia(MediaSaveTask paramMediaSaveTask)
  {
    verifyAccess();
    if (this.m_FileManager == null)
    {
      Log.e(this.TAG, "saveMedia() - No FileManager interface");
      return null;
    }
    if (paramMediaSaveTask == null)
    {
      Log.e(this.TAG, "saveMedia() - No save task");
      return null;
    }
    Camera localCamera = (Camera)get(PROP_CAMERA);
    int i = ((Integer)localCamera.get(Camera.PROP_SCENE_MODE)).intValue();
    if (paramMediaSaveTask.getSceneMode() == null) {
      paramMediaSaveTask.setSceneMode(Integer.valueOf(i));
    }
    boolean bool;
    if (paramMediaSaveTask.isHdrActive() == null)
    {
      if (i != 18)
      {
        if (i != 10001) {
          break label251;
        }
        bool = ((Boolean)localCamera.get(Camera.PROP_AUTO_HDR_STATUS)).booleanValue();
        paramMediaSaveTask.setIsHdrActive(Boolean.valueOf(bool));
      }
    }
    else if (paramMediaSaveTask.getLocation() == null)
    {
      CaptureHandle localCaptureHandle = paramMediaSaveTask.getCaptureHandle();
      if (localCaptureHandle != null) {
        switch (-getcom-oneplus-camera-media-MediaTypeSwitchesValues()[localCaptureHandle.getMediaType().ordinal()])
        {
        }
      }
    }
    for (;;)
    {
      if (paramMediaSaveTask.getLensFacing() == null) {
        paramMediaSaveTask.setLensFacing((Camera.LensFacing)localCamera.get(Camera.PROP_LENS_FACING));
      }
      if (paramMediaSaveTask.getStorageType() == null) {
        paramMediaSaveTask.setStorageType((Storage.Type)this.m_Settings.getEnum("StorageType", Storage.Type.class, Storage.Type.INTERNAL));
      }
      return this.m_FileManager.saveMedia(paramMediaSaveTask, 0);
      bool = true;
      break;
      label251:
      bool = false;
      break;
      paramMediaSaveTask.setLocation((Location)localCamera.get(Camera.PROP_LOCATION));
      continue;
      paramMediaSaveTask.setLocation(this.m_VideoLocation);
    }
  }
  
  public boolean setMediaType(final MediaType paramMediaType)
  {
    if (isDependencyThread()) {
      return setMediaTypeInternal(paramMediaType);
    }
    if (HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        CameraThread.-wrap5(CameraThread.this, paramMediaType);
      }
    })) {
      return true;
    }
    Log.e(this.TAG, "setMediaType() - Fail to perform cross-thread operation");
    return false;
  }
  
  public final Handle setPhotoCaptureHandler(PhotoCaptureHandler paramPhotoCaptureHandler, int paramInt)
  {
    verifyAccess();
    if (paramPhotoCaptureHandler == null)
    {
      Log.e(this.TAG, "setPhotoCaptureHandler() - No capture handler");
      return null;
    }
    switch (-getcom-oneplus-camera-PhotoCaptureStateSwitchesValues()[((PhotoCaptureState)get(PROP_PHOTO_CAPTURE_STATE)).ordinal()])
    {
    default: 
      Log.e(this.TAG, "setPhotoCaptureHandler() - Cannot change capture handler when photo capture state is " + get(PROP_PHOTO_CAPTURE_STATE));
      return null;
    }
    PhotoCaptureHandlerHandle localPhotoCaptureHandlerHandle = new PhotoCaptureHandlerHandle(paramPhotoCaptureHandler);
    this.m_PhotoCaptureHandlerHandles.add(localPhotoCaptureHandlerHandle);
    Log.w(this.TAG, "setPhotoCaptureHandler() - Capture handler : " + paramPhotoCaptureHandler + ", handle : " + localPhotoCaptureHandlerHandle);
    return localPhotoCaptureHandlerHandle;
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
  
  public final void setResourceIdTable(ResourceIdTable paramResourceIdTable)
  {
    try
    {
      if (((Boolean)get(PROP_IS_STARTED)).booleanValue()) {
        throw new RuntimeException("Cannot change resource ID table after starting");
      }
    }
    finally {}
    if (paramResourceIdTable != null) {}
    for (paramResourceIdTable = paramResourceIdTable.clone();; paramResourceIdTable = null)
    {
      this.m_ResourceIdTable = paramResourceIdTable;
      return;
    }
  }
  
  final void setScreenSize(ScreenSize paramScreenSize)
  {
    if (paramScreenSize == null) {
      throw new IllegalArgumentException("No screen size.");
    }
    if (isDependencyThread())
    {
      setReadOnly(PROP_SCREEN_SIZE, paramScreenSize);
      return;
    }
    try
    {
      if (!HandlerUtils.sendMessage(this, 10000, 0, 0, paramScreenSize)) {
        this.m_InitialScreenSize = paramScreenSize;
      }
      return;
    }
    finally
    {
      paramScreenSize = finally;
      throw paramScreenSize;
    }
  }
  
  public final Handle setVideoCaptureHandler(VideoCaptureHandler paramVideoCaptureHandler, int paramInt)
  {
    verifyAccess();
    if (paramVideoCaptureHandler == null)
    {
      Log.e(this.TAG, "setVideoCaptureHandler() - No capture handler");
      return null;
    }
    switch (-getcom-oneplus-camera-VideoCaptureStateSwitchesValues()[((VideoCaptureState)get(PROP_VIDEO_CAPTURE_STATE)).ordinal()])
    {
    case 6: 
    default: 
      Log.e(this.TAG, "setVideoCaptureHandler() - Cannot change capture handler when video capture state is " + get(PROP_VIDEO_CAPTURE_STATE));
      return null;
    }
    VideoCaptureHandlerHandle localVideoCaptureHandlerHandle = new VideoCaptureHandlerHandle(paramVideoCaptureHandler);
    this.m_VideoCaptureHandlerHandles.add(localVideoCaptureHandlerHandle);
    Log.w(this.TAG, "setVideoCaptureHandler() - Capture handler : " + paramVideoCaptureHandler + ", handle : " + localVideoCaptureHandlerHandle);
    return localVideoCaptureHandlerHandle;
  }
  
  public void start(MediaType paramMediaType)
  {
    try
    {
      start();
      this.m_InitialMediaType = paramMediaType;
      return;
    }
    finally
    {
      paramMediaType = finally;
      throw paramMediaType;
    }
  }
  
  public final boolean startCameraPreview(Camera paramCamera, Size paramSize, Resolution paramResolution, Object paramObject)
  {
    return startCameraPreview(paramCamera, paramSize, paramResolution, paramObject, 0);
  }
  
  public final boolean startCameraPreview(final Camera paramCamera, final Size paramSize, final Resolution paramResolution, final Object paramObject, final int paramInt)
  {
    if (paramCamera == null)
    {
      Log.e(this.TAG, "startCameraPreview() - No camera");
      return false;
    }
    if (isDependencyThread()) {
      return startCameraPreviewInternal(paramCamera, paramSize, paramResolution, paramObject, paramInt);
    }
    if (HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        CameraThread.-wrap6(CameraThread.this, paramCamera, paramSize, paramResolution, paramObject, paramInt);
      }
    })) {
      return true;
    }
    Log.e(this.TAG, "startCameraPreview() - Fail to perform cross-thread operation");
    return false;
  }
  
  public final boolean stopCameraPreview(Camera paramCamera)
  {
    return stopCameraPreview(paramCamera, 0);
  }
  
  public final boolean stopCameraPreview(Camera paramCamera, int paramInt)
  {
    Object localObject = null;
    if (paramCamera == null)
    {
      Log.e(this.TAG, "stopCameraPreview() - No camera");
      return false;
    }
    if (isDependencyThread()) {
      return stopCameraPreviewInternal(paramCamera, null, paramInt);
    }
    int i;
    if ((paramInt & 0x1) != 0) {
      i = 1;
    }
    for (;;)
    {
      boolean[] arrayOfBoolean = new boolean[1];
      arrayOfBoolean[0] = false;
      if (i != 0) {
        localObject = arrayOfBoolean;
      }
      try
      {
        if (!HandlerUtils.sendMessage(this, 10070, paramInt, 0, new Object[] { paramCamera, localObject }))
        {
          Log.e(this.TAG, "stopCameraPreview() - Fail to perform cross-thread operation");
          return false;
          i = 0;
          continue;
        }
        if (i != 0) {
          try
          {
            Log.w(this.TAG, "stopCameraPreview() - Wait for camera thread [start]");
            arrayOfBoolean.wait(10000L);
            Log.w(this.TAG, "stopCameraPreview() - Wait for camera thread [end]");
            int j = arrayOfBoolean[0];
            if (j != 0) {
              return true;
            }
            Log.e(this.TAG, "stopCameraPreview() - Timeout");
            return false;
          }
          catch (InterruptedException paramCamera)
          {
            Log.e(this.TAG, "stopCameraPreview() - Interrupted", paramCamera);
            return false;
          }
        }
        return true;
      }
      finally {}
    }
  }
  
  public void stopCurrentPhotoCapture()
  {
    if (isDependencyThread())
    {
      Log.v(this.TAG, "stopCurrentPhotoCapture() - Handle : ", this.m_PhotoCaptureHandle);
      stopPhotoCaptureInternal(this.m_PhotoCaptureHandle, CaptureCompleteReason.NORMAL);
      return;
    }
    HandlerUtils.post(this, new Runnable()
    {
      public void run()
      {
        CameraThread.this.stopCurrentPhotoCapture();
      }
    });
  }
  
  private static final class CameraPreviewStartRequest
  {
    public final Camera camera;
    public int flags;
    public Object previewReceiver;
    public Size previewSize;
    public Resolution resolution;
    
    public CameraPreviewStartRequest(Camera paramCamera, Size paramSize, Resolution paramResolution, Object paramObject, int paramInt)
    {
      this.camera = paramCamera;
      this.previewSize = paramSize;
      this.previewReceiver = paramObject;
      this.flags = paramInt;
      this.resolution = paramResolution;
    }
  }
  
  private static final class CameraPreviewStopRequest
  {
    public final Camera camera;
    public final int flags;
    public final boolean[] result;
    
    public CameraPreviewStopRequest(Camera paramCamera, boolean[] paramArrayOfBoolean, int paramInt)
    {
      this.camera = paramCamera;
      this.flags = paramInt;
      this.result = paramArrayOfBoolean;
    }
  }
  
  private final class PhotoCaptureHandle
    extends CaptureHandle
  {
    public PhotoCaptureHandler captureHandler;
    public long captureRealTime;
    public final int flags;
    public final int frameCount;
    public boolean isFastCaptureEnabled;
    public boolean isVideoSnapshot;
    
    public PhotoCaptureHandle(Camera paramCamera, CaptureMode paramCaptureMode, Rotation paramRotation, int paramInt1, int paramInt2)
    {
      super(paramCaptureMode, paramRotation, MediaType.PHOTO);
      this.flags = paramInt2;
      this.frameCount = paramInt1;
    }
    
    public void complete()
    {
      closeDirectly();
    }
    
    public long getCaptureRealTime()
    {
      return this.captureRealTime;
    }
    
    public boolean isBurstPhotoCapture()
    {
      return this.frameCount != 1;
    }
    
    public boolean isVideoSnapshot()
    {
      return this.isVideoSnapshot;
    }
    
    protected void onClose(int paramInt)
    {
      CameraThread.-wrap32(CameraThread.this, this);
    }
  }
  
  private final class PhotoCaptureHandlerHandle
    extends Handle
  {
    public final PhotoCaptureHandler captureHandler;
    
    public PhotoCaptureHandlerHandle(PhotoCaptureHandler paramPhotoCaptureHandler)
    {
      super();
      this.captureHandler = paramPhotoCaptureHandler;
    }
    
    protected void onClose(int paramInt)
    {
      CameraThread.-wrap27(CameraThread.this, this);
    }
  }
  
  public static class PhotoParams
    implements Cloneable
  {
    public CaptureMode captureMode;
    public final int frameCount;
    
    public PhotoParams(int paramInt)
    {
      this.frameCount = paramInt;
    }
    
    public PhotoParams(int paramInt, CaptureMode paramCaptureMode)
    {
      this.captureMode = paramCaptureMode;
      this.frameCount = paramInt;
    }
    
    public PhotoParams clone()
    {
      try
      {
        PhotoParams localPhotoParams = (PhotoParams)super.clone();
        return localPhotoParams;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new RuntimeException(localCloneNotSupportedException);
      }
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
      CameraThread.-wrap30(CameraThread.this, this);
    }
  }
  
  public static class ResourceIdTable
    implements Cloneable
  {
    public int burstShutterSound;
    public int burstShutterSoundEnd;
    public int cameraTimer2SecSound;
    public int cameraTimerSound;
    public int photoShutterSound;
    public int videoStartSound;
    public int videoStopSound;
    
    public ResourceIdTable clone()
    {
      try
      {
        ResourceIdTable localResourceIdTable = (ResourceIdTable)super.clone();
        return localResourceIdTable;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new RuntimeException(localCloneNotSupportedException);
      }
    }
  }
  
  private final class VideoCaptureHandle
    extends CaptureHandle
  {
    public CamcorderProfile camcorderProfile;
    public VideoCaptureHandler captureHandler;
    public long captureRealTime;
    public final int flags;
    public final CameraThread.VideoParams params;
    
    public VideoCaptureHandle(Camera paramCamera, Rotation paramRotation, CameraThread.VideoParams paramVideoParams, int paramInt)
    {
      super(paramVideoParams.captureMode, paramRotation, MediaType.VIDEO);
      this.params = paramVideoParams;
      this.flags = paramInt;
    }
    
    public void complete()
    {
      closeDirectly();
    }
    
    public long getCaptureRealTime()
    {
      return this.captureRealTime;
    }
    
    protected void onClose(int paramInt)
    {
      CameraThread.-wrap34(CameraThread.this, this, CaptureCompleteReason.NORMAL, paramInt);
    }
  }
  
  private final class VideoCaptureHandlerHandle
    extends Handle
  {
    public final VideoCaptureHandler captureHandler;
    
    public VideoCaptureHandlerHandle(VideoCaptureHandler paramVideoCaptureHandler)
    {
      super();
      this.captureHandler = paramVideoCaptureHandler;
    }
    
    protected void onClose(int paramInt)
    {
      CameraThread.-wrap28(CameraThread.this, this);
    }
  }
  
  public static class VideoParams
    implements Cloneable
  {
    public CaptureMode captureMode;
    public long maxDurationSeconds = -1L;
    public long maxFileSize = -1L;
    public Resolution resolution;
    
    public VideoParams(Resolution paramResolution)
    {
      this.resolution = paramResolution;
    }
    
    public VideoParams clone()
    {
      try
      {
        VideoParams localVideoParams = (VideoParams)super.clone();
        return localVideoParams;
      }
      catch (CloneNotSupportedException localCloneNotSupportedException)
      {
        throw new RuntimeException(localCloneNotSupportedException);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/CameraThread.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */