package android.media;

import android.os.Handler;

public abstract interface AudioRouting
{
  public abstract void addOnRoutingChangedListener(OnRoutingChangedListener paramOnRoutingChangedListener, Handler paramHandler);
  
  public abstract AudioDeviceInfo getPreferredDevice();
  
  public abstract AudioDeviceInfo getRoutedDevice();
  
  public abstract void removeOnRoutingChangedListener(OnRoutingChangedListener paramOnRoutingChangedListener);
  
  public abstract boolean setPreferredDevice(AudioDeviceInfo paramAudioDeviceInfo);
  
  public static abstract interface OnRoutingChangedListener
  {
    public abstract void onRoutingChanged(AudioRouting paramAudioRouting);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/AudioRouting.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */