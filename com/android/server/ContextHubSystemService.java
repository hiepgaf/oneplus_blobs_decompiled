package com.android.server;

import android.content.Context;
import android.hardware.location.ContextHubService;
import android.util.Log;

class ContextHubSystemService
  extends SystemService
{
  private static final String TAG = "ContextHubSystemService";
  private final ContextHubService mContextHubService;
  
  public ContextHubSystemService(Context paramContext)
  {
    super(paramContext);
    this.mContextHubService = new ContextHubService(paramContext);
  }
  
  public void onBootPhase(int paramInt)
  {
    if (paramInt == 500)
    {
      Log.d("ContextHubSystemService", "onBootPhase: PHASE_SYSTEM_SERVICES_READY");
      publishBinderService("contexthub_service", this.mContextHubService);
    }
  }
  
  public void onStart() {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/ContextHubSystemService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */