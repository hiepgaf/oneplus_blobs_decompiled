package android.telecom;

import android.os.Bundle;
import android.os.RemoteException;
import com.android.internal.telecom.IInCallAdapter;
import java.util.List;

public final class InCallAdapter
{
  private final IInCallAdapter mAdapter;
  
  public InCallAdapter(IInCallAdapter paramIInCallAdapter)
  {
    this.mAdapter = paramIInCallAdapter;
  }
  
  public void answerCall(String paramString, int paramInt)
  {
    try
    {
      this.mAdapter.answerCall(paramString, paramInt);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void conference(String paramString1, String paramString2)
  {
    try
    {
      this.mAdapter.conference(paramString1, paramString2);
      return;
    }
    catch (RemoteException paramString1) {}
  }
  
  public void disconnectCall(String paramString)
  {
    try
    {
      this.mAdapter.disconnectCall(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void holdCall(String paramString)
  {
    try
    {
      this.mAdapter.holdCall(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void mergeConference(String paramString)
  {
    try
    {
      this.mAdapter.mergeConference(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void mute(boolean paramBoolean)
  {
    try
    {
      this.mAdapter.mute(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void phoneAccountSelected(String paramString, PhoneAccountHandle paramPhoneAccountHandle, boolean paramBoolean)
  {
    try
    {
      this.mAdapter.phoneAccountSelected(paramString, paramPhoneAccountHandle, paramBoolean);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void playDtmfTone(String paramString, char paramChar)
  {
    try
    {
      this.mAdapter.playDtmfTone(paramString, paramChar);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void postDialContinue(String paramString, boolean paramBoolean)
  {
    try
    {
      this.mAdapter.postDialContinue(paramString, paramBoolean);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void pullExternalCall(String paramString)
  {
    try
    {
      this.mAdapter.pullExternalCall(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void putExtra(String paramString1, String paramString2, int paramInt)
  {
    try
    {
      Bundle localBundle = new Bundle();
      localBundle.putInt(paramString2, paramInt);
      this.mAdapter.putExtras(paramString1, localBundle);
      return;
    }
    catch (RemoteException paramString1) {}
  }
  
  public void putExtra(String paramString1, String paramString2, String paramString3)
  {
    try
    {
      Bundle localBundle = new Bundle();
      localBundle.putString(paramString2, paramString3);
      this.mAdapter.putExtras(paramString1, localBundle);
      return;
    }
    catch (RemoteException paramString1) {}
  }
  
  public void putExtra(String paramString1, String paramString2, boolean paramBoolean)
  {
    try
    {
      Bundle localBundle = new Bundle();
      localBundle.putBoolean(paramString2, paramBoolean);
      this.mAdapter.putExtras(paramString1, localBundle);
      return;
    }
    catch (RemoteException paramString1) {}
  }
  
  public void putExtras(String paramString, Bundle paramBundle)
  {
    try
    {
      this.mAdapter.putExtras(paramString, paramBundle);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void rejectCall(String paramString1, boolean paramBoolean, String paramString2)
  {
    try
    {
      this.mAdapter.rejectCall(paramString1, paramBoolean, paramString2);
      return;
    }
    catch (RemoteException paramString1) {}
  }
  
  public void removeExtras(String paramString, List<String> paramList)
  {
    try
    {
      this.mAdapter.removeExtras(paramString, paramList);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void sendCallEvent(String paramString1, String paramString2, Bundle paramBundle)
  {
    try
    {
      this.mAdapter.sendCallEvent(paramString1, paramString2, paramBundle);
      return;
    }
    catch (RemoteException paramString1) {}
  }
  
  public void setAudioRoute(int paramInt)
  {
    try
    {
      this.mAdapter.setAudioRoute(paramInt);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void splitFromConference(String paramString)
  {
    try
    {
      this.mAdapter.splitFromConference(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void stopDtmfTone(String paramString)
  {
    try
    {
      this.mAdapter.stopDtmfTone(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void swapConference(String paramString)
  {
    try
    {
      this.mAdapter.swapConference(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
  
  public void turnProximitySensorOff(boolean paramBoolean)
  {
    try
    {
      this.mAdapter.turnOffProximitySensor(paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void turnProximitySensorOn()
  {
    try
    {
      this.mAdapter.turnOnProximitySensor();
      return;
    }
    catch (RemoteException localRemoteException) {}
  }
  
  public void unholdCall(String paramString)
  {
    try
    {
      this.mAdapter.unholdCall(paramString);
      return;
    }
    catch (RemoteException paramString) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/InCallAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */