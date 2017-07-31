package android.hardware.camera2.legacy;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.AutoFocusMoveCallback;
import android.hardware.Camera.Parameters;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.utils.ParamsUtils;
import android.util.Log;
import com.android.internal.util.Preconditions;
import java.util.Objects;

public class LegacyFocusStateMapper
{
  private static final boolean DEBUG = false;
  private static String TAG = "LegacyFocusStateMapper";
  private String mAfModePrevious = null;
  private int mAfRun = 0;
  private int mAfState = 0;
  private int mAfStatePrevious = 0;
  private final Camera mCamera;
  private final Object mLock = new Object();
  
  public LegacyFocusStateMapper(Camera paramCamera)
  {
    this.mCamera = ((Camera)Preconditions.checkNotNull(paramCamera, "camera must not be null"));
  }
  
  private static String afStateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN(" + paramInt + ")";
    case 3: 
      return "ACTIVE_SCAN";
    case 4: 
      return "FOCUSED_LOCKED";
    case 0: 
      return "INACTIVE";
    case 5: 
      return "NOT_FOCUSED_LOCKED";
    case 2: 
      return "PASSIVE_FOCUSED";
    case 1: 
      return "PASSIVE_SCAN";
    }
    return "PASSIVE_UNFOCUSED";
  }
  
  public void mapResultTriggers(CameraMetadataNative paramCameraMetadataNative)
  {
    Preconditions.checkNotNull(paramCameraMetadataNative, "result must not be null");
    synchronized (this.mLock)
    {
      int i = this.mAfState;
      paramCameraMetadataNative.set(CaptureResult.CONTROL_AF_STATE, Integer.valueOf(i));
      this.mAfStatePrevious = i;
      return;
    }
  }
  
  public void processRequestTriggers(CaptureRequest arg1, Camera.Parameters arg2)
  {
    Preconditions.checkNotNull(???, "captureRequest must not be null");
    int i = ((Integer)ParamsUtils.getOrDefault(???, CaptureRequest.CONTROL_AF_TRIGGER, Integer.valueOf(0))).intValue();
    ??? = ???.getFocusMode();
    if (!Objects.equals(this.mAfModePrevious, ???)) {}
    synchronized (this.mLock)
    {
      this.mAfRun += 1;
      this.mAfState = 0;
      this.mCamera.cancelAutoFocus();
      this.mAfModePrevious = ???;
    }
    for (;;)
    {
      final int j;
      synchronized (this.mLock)
      {
        j = this.mAfRun;
        ??? = new Camera.AutoFocusMoveCallback()
        {
          public void onAutoFocusMoving(boolean paramAnonymousBoolean, Camera arg2)
          {
            synchronized (LegacyFocusStateMapper.-get2(LegacyFocusStateMapper.this))
            {
              int i = LegacyFocusStateMapper.-get1(LegacyFocusStateMapper.this);
              if (j != i)
              {
                Log.d(LegacyFocusStateMapper.-get0(), "onAutoFocusMoving - ignoring move callbacks from old af run" + j);
                return;
              }
              String str;
              if (paramAnonymousBoolean)
              {
                i = 1;
                str = paramCaptureRequest;
                if (!str.equals("continuous-picture")) {
                  break label97;
                }
              }
              label97:
              while (str.equals("continuous-video"))
              {
                LegacyFocusStateMapper.-set0(LegacyFocusStateMapper.this, i);
                return;
                i = 2;
                break;
              }
              Log.w(LegacyFocusStateMapper.-get0(), "onAutoFocus - got unexpected onAutoFocus in mode " + paramCaptureRequest);
            }
          }
        };
        if (???.equals("auto"))
        {
          this.mCamera.setAutoFocusMoveCallback(???);
          switch (i)
          {
          default: 
            Log.w(TAG, "processRequestTriggers - ignoring unknown control.afTrigger = " + i);
          case 0: 
            return;
            ??? = finally;
            throw ???;
          }
        }
      }
      if ((!???.equals("macro")) && (!???.equals("continuous-picture"))) {
        if (???.equals("continuous-video"))
        {
          continue;
          if (???.equals("auto")) {
            label224:
            i = 3;
          }
          synchronized (this.mLock)
          {
            j = this.mAfRun + 1;
            this.mAfRun = j;
            this.mAfState = i;
            if (i != 0)
            {
              this.mCamera.autoFocus(new Camera.AutoFocusCallback()
              {
                public void onAutoFocus(boolean paramAnonymousBoolean, Camera arg2)
                {
                  synchronized (LegacyFocusStateMapper.-get2(LegacyFocusStateMapper.this))
                  {
                    int i = LegacyFocusStateMapper.-get1(LegacyFocusStateMapper.this);
                    if (i != j)
                    {
                      Log.d(LegacyFocusStateMapper.-get0(), String.format("onAutoFocus - ignoring AF callback (old run %d, new run %d)", new Object[] { Integer.valueOf(j), Integer.valueOf(i) }));
                      return;
                    }
                    String str;
                    if (paramAnonymousBoolean)
                    {
                      i = 4;
                      str = paramCaptureRequest;
                      if (!str.equals("auto")) {
                        break label101;
                      }
                    }
                    label101:
                    while ((str.equals("continuous-picture")) || (str.equals("continuous-video")) || (str.equals("macro")))
                    {
                      LegacyFocusStateMapper.-set0(LegacyFocusStateMapper.this, i);
                      return;
                      i = 5;
                      break;
                    }
                    Log.w(LegacyFocusStateMapper.-get0(), "onAutoFocus - got unexpected onAutoFocus in mode " + paramCaptureRequest);
                  }
                }
              });
              return;
              if (???.equals("macro")) {
                break label224;
              }
              if (???.equals("continuous-picture")) {}
              while (???.equals("continuous-video"))
              {
                i = 1;
                break;
              }
              i = 0;
            }
          }
        }
      }
    }
    synchronized (this.mLock)
    {
      synchronized (this.mLock)
      {
        this.mAfRun += 1;
        this.mAfState = 0;
        this.mCamera.cancelAutoFocus();
        return;
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/camera2/legacy/LegacyFocusStateMapper.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */