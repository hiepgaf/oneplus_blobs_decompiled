package com.oneplus.camera;

import android.os.Message;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerUtils;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyChangeEventArgs;
import com.oneplus.base.PropertyChangedCallback;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.PropertySource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

final class FaceTrackerImpl
  extends CameraComponent
  implements FaceTracker
{
  private static final int MSG_FACES_CHANGED = 10000;
  private List<Camera.Face> m_CurrentFaces = Collections.EMPTY_LIST;
  private final LinkedList<FaceDetectionDisableHandle> m_FaceDetectionDisableHandle = new LinkedList();
  private final List<List<Camera.Face>> m_FaceListQueue = new ArrayList();
  private final PropertyChangedCallback<List<Camera.Face>> m_FacesChangedCallback = new PropertyChangedCallback()
  {
    public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<List<Camera.Face>> paramAnonymousPropertyKey, PropertyChangeEventArgs<List<Camera.Face>> paramAnonymousPropertyChangeEventArgs)
    {
      FaceTrackerImpl.-wrap3(FaceTrackerImpl.this, (Camera)paramAnonymousPropertySource, (List)paramAnonymousPropertyChangeEventArgs.getNewValue());
    }
  };
  
  FaceTrackerImpl(CameraActivity paramCameraActivity)
  {
    super("Face tracker", paramCameraActivity, true);
  }
  
  private void enableFaceDetection(Handle paramHandle)
  {
    verifyAccess();
    if (!this.m_FaceDetectionDisableHandle.remove(paramHandle)) {
      return;
    }
    if (!this.m_FaceDetectionDisableHandle.isEmpty()) {
      return;
    }
    updateFaceDetectionState();
  }
  
  private void onCameraChanged(final Camera paramCamera1, final Camera paramCamera2)
  {
    if (paramCamera1 != null) {
      HandlerUtils.post(paramCamera1, new Runnable()
      {
        public void run()
        {
          paramCamera1.removeCallback(Camera.PROP_FACES, FaceTrackerImpl.-get0(FaceTrackerImpl.this));
        }
      });
    }
    if (paramCamera2 != null) {
      HandlerUtils.post(paramCamera2, new Runnable()
      {
        public void run()
        {
          paramCamera2.set(Camera.PROP_IS_FACE_DETECTION_ENABLED, Boolean.valueOf(this.val$isEmpty));
          paramCamera2.addCallback(Camera.PROP_FACES, FaceTrackerImpl.-get0(FaceTrackerImpl.this));
          FaceTrackerImpl.-wrap3(FaceTrackerImpl.this, paramCamera2, (List)paramCamera2.get(Camera.PROP_FACES));
        }
      });
    }
    onFacesChanged(null);
  }
  
  /* Error */
  private void onFacesChanged(List<Camera.Face> paramList)
  {
    // Byte code:
    //   0: aload_1
    //   1: astore_3
    //   2: aload_1
    //   3: ifnonnull +7 -> 10
    //   6: getstatic 76	java/util/Collections:EMPTY_LIST	Ljava/util/List;
    //   9: astore_3
    //   10: aload_0
    //   11: getfield 78	com/oneplus/camera/FaceTrackerImpl:m_CurrentFaces	Ljava/util/List;
    //   14: astore_1
    //   15: aload_3
    //   16: invokeinterface 122 1 0
    //   21: ifne +58 -> 79
    //   24: aload_0
    //   25: invokevirtual 126	com/oneplus/camera/FaceTrackerImpl:getCameraActivity	()Lcom/oneplus/camera/CameraActivity;
    //   28: getstatic 132	com/oneplus/camera/CameraActivity:PROP_CAMERA_PREVIEW_STATE	Lcom/oneplus/base/PropertyKey;
    //   31: invokevirtual 136	com/oneplus/camera/CameraActivity:get	(Lcom/oneplus/base/PropertyKey;)Ljava/lang/Object;
    //   34: getstatic 142	com/oneplus/camera/OperationState:STARTED	Lcom/oneplus/camera/OperationState;
    //   37: if_acmpeq +42 -> 79
    //   40: aload_3
    //   41: invokeinterface 146 1 0
    //   46: iconst_1
    //   47: isub
    //   48: istore_2
    //   49: iload_2
    //   50: iflt +23 -> 73
    //   53: aload_3
    //   54: iload_2
    //   55: invokeinterface 149 2 0
    //   60: checkcast 151	com/oneplus/camera/Camera$Face
    //   63: invokevirtual 154	com/oneplus/camera/Camera$Face:recycle	()V
    //   66: iload_2
    //   67: iconst_1
    //   68: isub
    //   69: istore_2
    //   70: goto -21 -> 49
    //   73: aload_3
    //   74: invokeinterface 157 1 0
    //   79: aload_0
    //   80: aload_3
    //   81: putfield 78	com/oneplus/camera/FaceTrackerImpl:m_CurrentFaces	Ljava/util/List;
    //   84: aload_0
    //   85: getstatic 160	com/oneplus/camera/FaceTrackerImpl:PROP_FACES	Lcom/oneplus/base/PropertyKey;
    //   88: aload_1
    //   89: aload_3
    //   90: invokevirtual 164	com/oneplus/camera/FaceTrackerImpl:notifyPropertyChanged	(Lcom/oneplus/base/PropertyKey;Ljava/lang/Object;Ljava/lang/Object;)Z
    //   93: pop
    //   94: aload_1
    //   95: ifnull +42 -> 137
    //   98: aload_1
    //   99: invokeinterface 146 1 0
    //   104: iconst_1
    //   105: isub
    //   106: istore_2
    //   107: iload_2
    //   108: iflt +23 -> 131
    //   111: aload_1
    //   112: iload_2
    //   113: invokeinterface 149 2 0
    //   118: checkcast 151	com/oneplus/camera/Camera$Face
    //   121: invokevirtual 154	com/oneplus/camera/Camera$Face:recycle	()V
    //   124: iload_2
    //   125: iconst_1
    //   126: isub
    //   127: istore_2
    //   128: goto -21 -> 107
    //   131: aload_1
    //   132: invokeinterface 157 1 0
    //   137: aload_1
    //   138: ifnull +30 -> 168
    //   141: aload_1
    //   142: getstatic 76	java/util/Collections:EMPTY_LIST	Ljava/util/List;
    //   145: if_acmpeq +23 -> 168
    //   148: aload_0
    //   149: getfield 90	com/oneplus/camera/FaceTrackerImpl:m_FaceListQueue	Ljava/util/List;
    //   152: astore_3
    //   153: aload_3
    //   154: monitorenter
    //   155: aload_0
    //   156: getfield 90	com/oneplus/camera/FaceTrackerImpl:m_FaceListQueue	Ljava/util/List;
    //   159: aload_1
    //   160: invokeinterface 167 2 0
    //   165: pop
    //   166: aload_3
    //   167: monitorexit
    //   168: return
    //   169: astore_1
    //   170: aload_3
    //   171: monitorexit
    //   172: aload_1
    //   173: athrow
    //   174: astore 4
    //   176: aload_1
    //   177: ifnull +42 -> 219
    //   180: aload_1
    //   181: invokeinterface 146 1 0
    //   186: iconst_1
    //   187: isub
    //   188: istore_2
    //   189: iload_2
    //   190: iflt +23 -> 213
    //   193: aload_1
    //   194: iload_2
    //   195: invokeinterface 149 2 0
    //   200: checkcast 151	com/oneplus/camera/Camera$Face
    //   203: invokevirtual 154	com/oneplus/camera/Camera$Face:recycle	()V
    //   206: iload_2
    //   207: iconst_1
    //   208: isub
    //   209: istore_2
    //   210: goto -21 -> 189
    //   213: aload_1
    //   214: invokeinterface 157 1 0
    //   219: aload_1
    //   220: ifnull +30 -> 250
    //   223: aload_1
    //   224: getstatic 76	java/util/Collections:EMPTY_LIST	Ljava/util/List;
    //   227: if_acmpeq +23 -> 250
    //   230: aload_0
    //   231: getfield 90	com/oneplus/camera/FaceTrackerImpl:m_FaceListQueue	Ljava/util/List;
    //   234: astore_3
    //   235: aload_3
    //   236: monitorenter
    //   237: aload_0
    //   238: getfield 90	com/oneplus/camera/FaceTrackerImpl:m_FaceListQueue	Ljava/util/List;
    //   241: aload_1
    //   242: invokeinterface 167 2 0
    //   247: pop
    //   248: aload_3
    //   249: monitorexit
    //   250: aload 4
    //   252: athrow
    //   253: astore_1
    //   254: aload_3
    //   255: monitorexit
    //   256: aload_1
    //   257: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	258	0	this	FaceTrackerImpl
    //   0	258	1	paramList	List<Camera.Face>
    //   48	162	2	i	int
    //   174	77	4	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   155	166	169	finally
    //   15	49	174	finally
    //   53	66	174	finally
    //   73	79	174	finally
    //   79	94	174	finally
    //   237	248	253	finally
  }
  
  private void onFacesPropChanged(Camera paramCamera, List<Camera.Face> arg2)
  {
    int i;
    synchronized (this.m_FaceListQueue)
    {
      if (!this.m_FaceListQueue.isEmpty())
      {
        paramCamera = (List)this.m_FaceListQueue.remove(this.m_FaceListQueue.size() - 1);
        i = ???.size() - 1;
        while (i >= 0)
        {
          paramCamera.add(Camera.Face.obtain((Camera.Face)???.get(i)));
          i -= 1;
        }
      }
      Log.w(this.TAG, "onFacesPropChanged() - No free face list");
      paramCamera = new ArrayList();
    }
    if (!HandlerUtils.sendMessage(this, 10000, 0, 0, paramCamera))
    {
      Log.e(this.TAG, "onFacesPropChanged() - Fail to send face list back");
      i = paramCamera.size() - 1;
      while (i >= 0)
      {
        ((Camera.Face)paramCamera.get(i)).recycle();
        i -= 1;
      }
      paramCamera.clear();
    }
    synchronized (this.m_FaceListQueue)
    {
      this.m_FaceListQueue.add(paramCamera);
      return;
    }
  }
  
  private void updateFaceDetectionState()
  {
    Camera localCamera = getCamera();
    if (localCamera != null)
    {
      updateFaceDetectionState(localCamera);
      return;
    }
    Log.d(this.TAG, "updateFaceDetectionState() - getCamera null");
  }
  
  private void updateFaceDetectionState(final Camera paramCamera)
  {
    HandlerUtils.post(paramCamera, new Runnable()
    {
      public void run()
      {
        paramCamera.set(Camera.PROP_IS_FACE_DETECTION_ENABLED, Boolean.valueOf(this.val$isEmpty));
      }
    });
  }
  
  public Handle disableFaceDetection(FaceTracker.FaceDetectionDisabledReason paramFaceDetectionDisabledReason, int paramInt)
  {
    verifyAccess();
    if (!isRunningOrInitializing())
    {
      Log.e(this.TAG, "disableFaceDetection() - Component is not running");
      return null;
    }
    FaceTracker.FaceDetectionDisabledReason localFaceDetectionDisabledReason = paramFaceDetectionDisabledReason;
    if (paramFaceDetectionDisabledReason == null) {
      localFaceDetectionDisabledReason = FaceTracker.FaceDetectionDisabledReason.UNKNOWN;
    }
    paramFaceDetectionDisabledReason = new FaceDetectionDisableHandle(localFaceDetectionDisabledReason);
    this.m_FaceDetectionDisableHandle.add(paramFaceDetectionDisabledReason);
    Log.d(this.TAG, "disableFaceDetection() - ", paramFaceDetectionDisabledReason.toString());
    updateFaceDetectionState();
    return paramFaceDetectionDisabledReason;
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey == PROP_FACES) {
      return this.m_CurrentFaces;
    }
    return (TValue)super.get(paramPropertyKey);
  }
  
  protected void handleMessage(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default: 
      super.handleMessage(paramMessage);
      return;
    }
    onFacesChanged((List)paramMessage.obj);
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    Object localObject = getCameraActivity();
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_CAMERA, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<Camera> paramAnonymousPropertyKey, PropertyChangeEventArgs<Camera> paramAnonymousPropertyChangeEventArgs)
      {
        FaceTrackerImpl.-wrap1(FaceTrackerImpl.this, (Camera)paramAnonymousPropertyChangeEventArgs.getOldValue(), (Camera)paramAnonymousPropertyChangeEventArgs.getNewValue());
      }
    });
    ((CameraActivity)localObject).addCallback(CameraActivity.PROP_CAMERA_PREVIEW_STATE, new PropertyChangedCallback()
    {
      public void onPropertyChanged(PropertySource paramAnonymousPropertySource, PropertyKey<OperationState> paramAnonymousPropertyKey, PropertyChangeEventArgs<OperationState> paramAnonymousPropertyChangeEventArgs)
      {
        if (paramAnonymousPropertyChangeEventArgs.getOldValue() == OperationState.STARTED) {
          FaceTrackerImpl.-wrap2(FaceTrackerImpl.this, null);
        }
      }
    });
    localObject = getCamera();
    if (localObject != null) {
      onCameraChanged(null, (Camera)localObject);
    }
  }
  
  private final class FaceDetectionDisableHandle
    extends Handle
  {
    public final FaceTracker.FaceDetectionDisabledReason reason;
    
    public FaceDetectionDisableHandle(FaceTracker.FaceDetectionDisabledReason paramFaceDetectionDisabledReason)
    {
      super();
      this.reason = paramFaceDetectionDisabledReason;
    }
    
    protected void onClose(int paramInt)
    {
      FaceTrackerImpl.-wrap0(FaceTrackerImpl.this, this);
    }
    
    public String toString()
    {
      return super.toString() + "{ Reason = " + this.reason + " }";
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/FaceTrackerImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */