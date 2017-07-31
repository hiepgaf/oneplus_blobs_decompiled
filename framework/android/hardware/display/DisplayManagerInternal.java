package android.hardware.display;

import android.hardware.SensorManager;
import android.os.Handler;
import android.view.Display;
import android.view.DisplayInfo;

public abstract class DisplayManagerInternal
{
  public abstract DisplayInfo getDisplayInfo(int paramInt);
  
  public abstract void initPowerManagement(DisplayPowerCallbacks paramDisplayPowerCallbacks, Handler paramHandler, SensorManager paramSensorManager);
  
  public abstract boolean isProximitySensorAvailable();
  
  public abstract void performTraversalInTransactionFromWindowManager();
  
  public abstract void registerDisplayTransactionListener(DisplayTransactionListener paramDisplayTransactionListener);
  
  public abstract boolean requestPowerState(DisplayPowerRequest paramDisplayPowerRequest, boolean paramBoolean);
  
  public abstract void setDisplayInfoOverrideFromWindowManager(int paramInt, DisplayInfo paramDisplayInfo);
  
  public abstract void setDisplayOffsets(int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void setDisplayProperties(int paramInt1, boolean paramBoolean1, float paramFloat, int paramInt2, boolean paramBoolean2);
  
  public abstract void setUseProximityForceSuspend(boolean paramBoolean);
  
  public abstract void setWakingupReason(String paramString);
  
  public abstract void unregisterDisplayTransactionListener(DisplayTransactionListener paramDisplayTransactionListener);
  
  public static abstract interface DisplayPowerCallbacks
  {
    public abstract void acquireSuspendBlocker();
    
    public abstract void onDisplayStateChange(int paramInt);
    
    public abstract void onProximityNegative();
    
    public abstract void onProximityNegativeForceSuspend();
    
    public abstract void onProximityPositive();
    
    public abstract void onProximityPositiveForceSuspend();
    
    public abstract void onStateChanged();
    
    public abstract void releaseSuspendBlocker();
    
    public abstract void unblockScreenOn();
  }
  
  public static final class DisplayPowerRequest
  {
    public static final int POLICY_BRIGHT = 3;
    public static final int POLICY_DIM = 2;
    public static final int POLICY_DOZE = 1;
    public static final int POLICY_OFF = 0;
    public boolean blockScreenOn;
    public boolean boostScreenBrightness;
    public boolean brightnessSetByUser;
    public int dozeScreenBrightness;
    public int dozeScreenState;
    public boolean lowPowerMode;
    public int policy;
    public float screenAutoBrightnessAdjustment;
    public int screenBrightness;
    public boolean useAutoBrightness;
    public boolean useProximitySensor;
    public boolean useTwilight;
    
    public DisplayPowerRequest()
    {
      this.policy = 3;
      this.useProximitySensor = false;
      this.screenBrightness = 255;
      this.screenAutoBrightnessAdjustment = 0.0F;
      this.useAutoBrightness = false;
      this.blockScreenOn = false;
      this.dozeScreenBrightness = -1;
      this.dozeScreenState = 0;
    }
    
    public DisplayPowerRequest(DisplayPowerRequest paramDisplayPowerRequest)
    {
      copyFrom(paramDisplayPowerRequest);
    }
    
    public static String policyToString(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return Integer.toString(paramInt);
      case 0: 
        return "OFF";
      case 1: 
        return "DOZE";
      case 2: 
        return "DIM";
      }
      return "BRIGHT";
    }
    
    public void copyFrom(DisplayPowerRequest paramDisplayPowerRequest)
    {
      this.policy = paramDisplayPowerRequest.policy;
      this.useProximitySensor = paramDisplayPowerRequest.useProximitySensor;
      this.screenBrightness = paramDisplayPowerRequest.screenBrightness;
      this.screenAutoBrightnessAdjustment = paramDisplayPowerRequest.screenAutoBrightnessAdjustment;
      this.brightnessSetByUser = paramDisplayPowerRequest.brightnessSetByUser;
      this.useAutoBrightness = paramDisplayPowerRequest.useAutoBrightness;
      this.blockScreenOn = paramDisplayPowerRequest.blockScreenOn;
      this.lowPowerMode = paramDisplayPowerRequest.lowPowerMode;
      this.boostScreenBrightness = paramDisplayPowerRequest.boostScreenBrightness;
      this.dozeScreenBrightness = paramDisplayPowerRequest.dozeScreenBrightness;
      this.dozeScreenState = paramDisplayPowerRequest.dozeScreenState;
      this.useTwilight = paramDisplayPowerRequest.useTwilight;
    }
    
    public boolean equals(DisplayPowerRequest paramDisplayPowerRequest)
    {
      boolean bool2 = false;
      boolean bool1 = bool2;
      if (paramDisplayPowerRequest != null)
      {
        bool1 = bool2;
        if (this.policy == paramDisplayPowerRequest.policy)
        {
          bool1 = bool2;
          if (this.useProximitySensor == paramDisplayPowerRequest.useProximitySensor)
          {
            bool1 = bool2;
            if (this.screenBrightness == paramDisplayPowerRequest.screenBrightness)
            {
              bool1 = bool2;
              if (this.screenAutoBrightnessAdjustment == paramDisplayPowerRequest.screenAutoBrightnessAdjustment)
              {
                bool1 = bool2;
                if (this.brightnessSetByUser == paramDisplayPowerRequest.brightnessSetByUser)
                {
                  bool1 = bool2;
                  if (this.useAutoBrightness == paramDisplayPowerRequest.useAutoBrightness)
                  {
                    bool1 = bool2;
                    if (this.blockScreenOn == paramDisplayPowerRequest.blockScreenOn)
                    {
                      bool1 = bool2;
                      if (this.lowPowerMode == paramDisplayPowerRequest.lowPowerMode)
                      {
                        bool1 = bool2;
                        if (this.boostScreenBrightness == paramDisplayPowerRequest.boostScreenBrightness)
                        {
                          bool1 = bool2;
                          if (this.dozeScreenBrightness == paramDisplayPowerRequest.dozeScreenBrightness)
                          {
                            bool1 = bool2;
                            if (this.dozeScreenState == paramDisplayPowerRequest.dozeScreenState)
                            {
                              bool1 = bool2;
                              if (this.useTwilight == paramDisplayPowerRequest.useTwilight) {
                                bool1 = true;
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
      return bool1;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof DisplayPowerRequest)) {
        return equals((DisplayPowerRequest)paramObject);
      }
      return false;
    }
    
    public int hashCode()
    {
      return 0;
    }
    
    public boolean isBrightOrDim()
    {
      return (this.policy == 3) || (this.policy == 2);
    }
    
    public String toString()
    {
      return "policy=" + policyToString(this.policy) + ", useProximitySensor=" + this.useProximitySensor + ", screenBrightness=" + this.screenBrightness + ", screenAutoBrightnessAdjustment=" + this.screenAutoBrightnessAdjustment + ", brightnessSetByUser=" + this.brightnessSetByUser + ", useAutoBrightness=" + this.useAutoBrightness + ", blockScreenOn=" + this.blockScreenOn + ", lowPowerMode=" + this.lowPowerMode + ", boostScreenBrightness=" + this.boostScreenBrightness + ", dozeScreenBrightness=" + this.dozeScreenBrightness + ", dozeScreenState=" + Display.stateToString(this.dozeScreenState) + ", useTwilight=" + this.useTwilight;
    }
  }
  
  public static abstract interface DisplayTransactionListener
  {
    public abstract void onDisplayTransaction();
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/display/DisplayManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */