package com.oneplus.camera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.OrientationManager;
import com.oneplus.base.OrientationManager.Callback;
import com.oneplus.base.Rotation;

public class AgentActivity
  extends Activity
{
  public static final int AGENT_TYPE_START_ACTIVITY = 0;
  public static final int AGENT_TYPE_START_ACTIVITY_FOR_RESULT = 1;
  public static final String EXTRA_AGENT_TYPE = "com.oneplus.camera.agent.intent.extra.AGENT_TYPE";
  public static final String EXTRA_COMPONENT = "com.oneplus.camera.agent.intent.extra.COMPONENT";
  private static final long FINISH_RUNNABLE_DELAYED = 500L;
  private static final int REQUEST_CODE_AGENT = 10000;
  private static final String STATE_AGENT = "stateAgent";
  private static final int STATE_AGENT_DONE = 1;
  private static final String TAG = "CameraAgentActivity";
  private int m_AgentType;
  private final Runnable m_FinishRunnable = new Runnable()
  {
    public void run()
    {
      AgentActivity.this.finish();
    }
  };
  private Handler m_Handler;
  private Rotation m_InitialRotation;
  private boolean m_IsAgentDone;
  private boolean m_IsRunning = true;
  private Handle m_OrientationCallbackHandle;
  private Handle m_OrientationSensorHandle;
  
  private void doRequestedStartActivity(final Intent paramIntent)
  {
    if (this.m_IsAgentDone) {
      return;
    }
    this.m_IsAgentDone = true;
    switch (this.m_AgentType)
    {
    default: 
      Log.e("CameraAgentActivity", "doRequestedStartActivity() - Unknown agent type : " + this.m_AgentType);
      finish();
      return;
    case 0: 
      this.m_Handler.postDelayed(new Runnable()
      {
        public void run()
        {
          Log.v("CameraAgentActivity", "doRequestedStartActivity() - StartActivity ", paramIntent.getAction());
          AgentActivity.this.getWindow().clearFlags(1024);
          AgentActivity.this.startActivity(paramIntent);
          AgentActivity.-set0(AgentActivity.this, false);
        }
      }, 150L);
      return;
    }
    this.m_Handler.postDelayed(new Runnable()
    {
      public void run()
      {
        Log.v("CameraAgentActivity", "doRequestedStartActivity() - StartActivityForResult ", paramIntent.getAction());
        AgentActivity.this.getWindow().clearFlags(1024);
        AgentActivity.this.startActivityForResult(paramIntent, 10000);
        AgentActivity.-set0(AgentActivity.this, false);
      }
    }, 150L);
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (this.m_AgentType == 1) {
      setResult(paramInt2, paramIntent);
    }
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    getWindow().addFlags(1024);
    if (this.m_InitialRotation != null)
    {
      Log.v("CameraAgentActivity", "onActivityResult() - Finish activity later");
      this.m_Handler.postDelayed(this.m_FinishRunnable, 500L);
      return;
    }
    finish();
  }
  
  public void onBackPressed()
  {
    if (this.m_IsRunning) {
      super.onBackPressed();
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    boolean bool = true;
    super.onCreate(paramBundle);
    Log.v("CameraAgentActivity", "onCreate() - Start");
    if (paramBundle != null)
    {
      if (paramBundle.getInt("stateAgent", 0) == 1) {
        this.m_IsAgentDone = bool;
      }
    }
    else
    {
      this.m_Handler = new Handler();
      paramBundle = getWindow();
      WindowManager.LayoutParams localLayoutParams = paramBundle.getAttributes();
      localLayoutParams.rotationAnimation = 2;
      paramBundle.setAttributes(localLayoutParams);
      paramBundle.addFlags(524288);
      paramBundle.getDecorView().setSystemUiVisibility(paramBundle.getDecorView().getSystemUiVisibility() | 0x800 | 0x2 | 0x200 | 0x400);
      switch (getRequestedOrientation())
      {
      }
    }
    for (;;)
    {
      if (OrientationManager.isSystemOrientationEnabled()) {
        setRequestedOrientation(10);
      }
      Log.v("CameraAgentActivity", "onCreate() - End");
      return;
      bool = false;
      break;
      this.m_InitialRotation = Rotation.PORTRAIT;
      continue;
      this.m_InitialRotation = Rotation.LANDSCAPE;
      continue;
      this.m_InitialRotation = Rotation.INVERSE_LANDSCAPE;
      continue;
      this.m_InitialRotation = Rotation.INVERSE_PORTRAIT;
    }
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    boolean bool = true;
    super.onRestoreInstanceState(paramBundle);
    Log.v("CameraAgentActivity", "onRestoreInstanceState");
    if (paramBundle != null) {
      if (paramBundle.getInt("stateAgent", 0) != 1) {
        break label35;
      }
    }
    for (;;)
    {
      this.m_IsAgentDone = bool;
      return;
      label35:
      bool = false;
    }
  }
  
  protected void onResume()
  {
    super.onResume();
    Log.v("CameraAgentActivity", "onResume()");
    if (!this.m_IsRunning)
    {
      getWindow().addFlags(1024);
      if (this.m_InitialRotation != null)
      {
        Log.v("CameraAgentActivity", "onResume() - Finish activity later");
        this.m_Handler.postDelayed(this.m_FinishRunnable, 500L);
        return;
      }
      finish();
      return;
    }
    Intent localIntent = new Intent(getIntent());
    localIntent.setComponent((ComponentName)localIntent.getParcelableExtra("com.oneplus.camera.agent.intent.extra.COMPONENT"));
    this.m_AgentType = localIntent.getIntExtra("com.oneplus.camera.agent.intent.extra.AGENT_TYPE", 0);
    doRequestedStartActivity(localIntent);
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    Log.v("CameraAgentActivity", "onSaveInstanceState()");
    if (this.m_IsAgentDone) {
      paramBundle.putInt("stateAgent", 1);
    }
    super.onSaveInstanceState(paramBundle);
  }
  
  protected void onStart()
  {
    super.onStart();
    Log.v("CameraAgentActivity", "onStart()");
    this.m_OrientationCallbackHandle = OrientationManager.setCallback(new OrientationManager.Callback()
    {
      public void onRotationChanged(Rotation paramAnonymousRotation1, Rotation paramAnonymousRotation2) {}
      
      public void onSystemOrientationSettingsChanged(boolean paramAnonymousBoolean)
      {
        if (paramAnonymousBoolean)
        {
          AgentActivity.this.setRequestedOrientation(4);
          return;
        }
        AgentActivity.this.setRequestedOrientation(1);
      }
    }, this.m_Handler);
    this.m_OrientationSensorHandle = OrientationManager.startOrientationSensor(this);
  }
  
  protected void onStop()
  {
    Log.v("CameraAgentActivity", "onStop()");
    this.m_OrientationCallbackHandle = Handle.close(this.m_OrientationCallbackHandle);
    this.m_OrientationSensorHandle = Handle.close(this.m_OrientationSensorHandle);
    this.m_IsRunning = false;
    super.onStop();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/camera/AgentActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */