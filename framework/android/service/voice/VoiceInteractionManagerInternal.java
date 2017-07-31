package android.service.voice;

import android.os.Bundle;
import android.os.IBinder;

public abstract class VoiceInteractionManagerInternal
{
  public abstract void startLocalVoiceInteraction(IBinder paramIBinder, Bundle paramBundle);
  
  public abstract void stopLocalVoiceInteraction(IBinder paramIBinder);
  
  public abstract boolean supportsLocalVoiceInteraction();
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/voice/VoiceInteractionManagerInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */