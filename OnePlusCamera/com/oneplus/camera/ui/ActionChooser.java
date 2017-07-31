package com.oneplus.camera.ui;

import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.util.LongSparseArray;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.camera.CameraActivity.ActivityResultCallback;
import com.oneplus.camera.OPCameraActivity;

public class ActionChooser
{
  static final String EXTRA_KEY_CHOOSER_ID = "ActionChooserId";
  private static final String TAG = ActionChooser.class.getSimpleName();
  private static final LongSparseArray<ActionChooser> m_ActiveChoosers = new LongSparseArray();
  private final OPCameraActivity m_Activity;
  private final CameraActivity.ActivityResultCallback m_ActivityResultCallback = new CameraActivity.ActivityResultCallback()
  {
    public void onActivityResult(Handle paramAnonymousHandle, int paramAnonymousInt, Intent paramAnonymousIntent)
    {
      ActionChooser.-wrap0(ActionChooser.this, paramAnonymousHandle, paramAnonymousInt, paramAnonymousIntent);
    }
  };
  private Handle m_ActivityResultHandle;
  private Callback m_Callback;
  private final long m_Id;
  private Intent m_Intent;
  private volatile long m_NextId = 1L;
  private CharSequence m_Title;
  
  public ActionChooser(OPCameraActivity paramOPCameraActivity, Intent paramIntent, int paramInt) {}
  
  public ActionChooser(OPCameraActivity paramOPCameraActivity, Intent paramIntent, CharSequence paramCharSequence)
  {
    if (paramOPCameraActivity == null) {
      throw new IllegalArgumentException("No activity");
    }
    this.m_Activity = paramOPCameraActivity;
    paramOPCameraActivity = (OPCameraActivity)localObject;
    if (paramIntent != null) {
      paramOPCameraActivity = (Intent)paramIntent.clone();
    }
    this.m_Intent = paramOPCameraActivity;
    this.m_Title = paramCharSequence;
    try
    {
      long l = this.m_NextId;
      this.m_NextId = (1L + l);
      this.m_Id = l;
      return;
    }
    finally
    {
      paramOPCameraActivity = finally;
      throw paramOPCameraActivity;
    }
  }
  
  public static ActionChooser getActiveChooser(long paramLong)
  {
    try
    {
      ActionChooser localActionChooser = (ActionChooser)m_ActiveChoosers.get(paramLong);
      return localActionChooser;
    }
    finally
    {
      localObject = finally;
      throw ((Throwable)localObject);
    }
  }
  
  private void onActivityResult(Handle paramHandle, int paramInt, Intent paramIntent)
  {
    if (this.m_ActivityResultHandle != paramHandle) {
      return;
    }
    this.m_ActivityResultHandle = null;
    try
    {
      m_ActiveChoosers.delete(this.m_Id);
      if (this.m_Callback != null) {
        this.m_Callback.onActivityResult(this, paramInt, paramIntent);
      }
      return;
    }
    finally {}
  }
  
  public final boolean isShowing()
  {
    return Handle.isValid(this.m_ActivityResultHandle);
  }
  
  final void notifyActivitySelected(ComponentName paramComponentName)
  {
    if (!Handle.isValid(this.m_ActivityResultHandle)) {
      return;
    }
    if (this.m_Callback != null) {
      this.m_Callback.onActivitySelected(this, paramComponentName);
    }
  }
  
  public void setCallback(Callback paramCallback)
  {
    this.m_Callback = paramCallback;
  }
  
  public boolean show(boolean paramBoolean)
  {
    if (Handle.isValid(this.m_ActivityResultHandle))
    {
      Log.e(TAG, "show() - Chooser is showing");
      return false;
    }
    if (this.m_Intent == null)
    {
      Log.e(TAG, "show() - No intent");
      return false;
    }
    Object localObject1 = new Intent(BaseApplication.current().getApplicationContext(), ActionChooserIntentSender.class);
    ((Intent)localObject1).putExtra("ActionChooserId", this.m_Id);
    localObject1 = PendingIntent.getBroadcast(BaseApplication.current().getBaseContext(), 0, (Intent)localObject1, 134217728);
    localObject1 = Intent.createChooser(this.m_Intent, this.m_Title, ((PendingIntent)localObject1).getIntentSender());
    if (paramBoolean)
    {
      this.m_ActivityResultHandle = this.m_Activity.startActivityForResultByAgent((Intent)localObject1, this.m_ActivityResultCallback);
      if (!Handle.isValid(this.m_ActivityResultHandle)) {}
    }
    else
    {
      for (;;)
      {
        try
        {
          m_ActiveChoosers.put(this.m_Id, this);
          return true;
        }
        finally {}
        try
        {
          this.m_Activity.startActivityByAgent((Intent)localObject1);
          return true;
        }
        catch (ActivityNotFoundException localActivityNotFoundException)
        {
          return false;
        }
      }
    }
    return false;
  }
  
  public static abstract class Callback
  {
    public void onActivityResult(ActionChooser paramActionChooser, int paramInt, Intent paramIntent) {}
    
    public void onActivitySelected(ActionChooser paramActionChooser, ComponentName paramComponentName) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/ui/ActionChooser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */