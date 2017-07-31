package android.telecom;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import com.android.internal.os.SomeArgs;
import com.android.internal.telecom.IConnectionServiceAdapter;
import com.android.internal.telecom.IConnectionServiceAdapter.Stub;
import com.android.internal.telecom.IVideoProvider;
import com.android.internal.telecom.RemoteServiceCallback;
import java.util.List;

final class ConnectionServiceAdapterServant
{
  private static final int MSG_ADD_CONFERENCE_CALL = 10;
  private static final int MSG_ADD_EXISTING_CONNECTION = 21;
  private static final int MSG_HANDLE_CREATE_CONNECTION_COMPLETE = 1;
  private static final int MSG_ON_CONNECTION_EVENT = 26;
  private static final int MSG_ON_POST_DIAL_CHAR = 22;
  private static final int MSG_ON_POST_DIAL_WAIT = 12;
  private static final int MSG_PUT_EXTRAS = 24;
  private static final int MSG_QUERY_REMOTE_CALL_SERVICES = 13;
  private static final int MSG_REMOVE_CALL = 11;
  private static final int MSG_REMOVE_EXTRAS = 25;
  private static final int MSG_SET_ACTIVE = 2;
  private static final int MSG_SET_ADDRESS = 18;
  private static final int MSG_SET_CALLER_DISPLAY_NAME = 19;
  private static final int MSG_SET_CONFERENCEABLE_CONNECTIONS = 20;
  private static final int MSG_SET_CONFERENCE_MERGE_FAILED = 23;
  private static final int MSG_SET_CONNECTION_CAPABILITIES = 8;
  private static final int MSG_SET_CONNECTION_PROPERTIES = 27;
  private static final int MSG_SET_DIALING = 4;
  private static final int MSG_SET_DISCONNECTED = 5;
  private static final int MSG_SET_IS_CONFERENCED = 9;
  private static final int MSG_SET_IS_VOIP_AUDIO_MODE = 16;
  private static final int MSG_SET_ON_HOLD = 6;
  private static final int MSG_SET_PULLING = 28;
  private static final int MSG_SET_RINGBACK_REQUESTED = 7;
  private static final int MSG_SET_RINGING = 3;
  private static final int MSG_SET_STATUS_HINTS = 17;
  private static final int MSG_SET_VIDEO_CALL_PROVIDER = 15;
  private static final int MSG_SET_VIDEO_STATE = 14;
  private final IConnectionServiceAdapter mDelegate;
  private final Handler mHandler = new Handler()
  {
    private void internalHandleMessage(Message paramAnonymousMessage)
      throws RemoteException
    {
      boolean bool2 = true;
      boolean bool1 = true;
      String str;
      switch (paramAnonymousMessage.what)
      {
      default: 
        return;
      case 1: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).handleCreateConnectionComplete((String)paramAnonymousMessage.arg1, (ConnectionRequest)paramAnonymousMessage.arg2, (ParcelableConnection)paramAnonymousMessage.arg3);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 2: 
        ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setActive((String)paramAnonymousMessage.obj);
        return;
      case 3: 
        ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setRinging((String)paramAnonymousMessage.obj);
        return;
      case 4: 
        ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setDialing((String)paramAnonymousMessage.obj);
        return;
      case 28: 
        ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setPulling((String)paramAnonymousMessage.obj);
        return;
      case 5: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setDisconnected((String)paramAnonymousMessage.arg1, (DisconnectCause)paramAnonymousMessage.arg2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 6: 
        ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setOnHold((String)paramAnonymousMessage.obj);
        return;
      case 7: 
        IConnectionServiceAdapter localIConnectionServiceAdapter1 = ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this);
        str = (String)paramAnonymousMessage.obj;
        if (paramAnonymousMessage.arg1 == 1) {}
        for (;;)
        {
          localIConnectionServiceAdapter1.setRingbackRequested(str, bool1);
          return;
          bool1 = false;
        }
      case 8: 
        ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setConnectionCapabilities((String)paramAnonymousMessage.obj, paramAnonymousMessage.arg1);
        return;
      case 27: 
        ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setConnectionProperties((String)paramAnonymousMessage.obj, paramAnonymousMessage.arg1);
        return;
      case 9: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setIsConferenced((String)paramAnonymousMessage.arg1, (String)paramAnonymousMessage.arg2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 10: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).addConferenceCall((String)paramAnonymousMessage.arg1, (ParcelableConference)paramAnonymousMessage.arg2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 11: 
        ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).removeCall((String)paramAnonymousMessage.obj);
        return;
      case 12: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).onPostDialWait((String)paramAnonymousMessage.arg1, (String)paramAnonymousMessage.arg2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 22: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).onPostDialChar((String)paramAnonymousMessage.arg1, (char)paramAnonymousMessage.argi1);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 13: 
        ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).queryRemoteConnectionServices((RemoteServiceCallback)paramAnonymousMessage.obj);
        return;
      case 14: 
        ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setVideoState((String)paramAnonymousMessage.obj, paramAnonymousMessage.arg1);
        return;
      case 15: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setVideoProvider((String)paramAnonymousMessage.arg1, (IVideoProvider)paramAnonymousMessage.arg2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 16: 
        IConnectionServiceAdapter localIConnectionServiceAdapter2 = ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this);
        str = (String)paramAnonymousMessage.obj;
        if (paramAnonymousMessage.arg1 == 1) {}
        for (bool1 = bool2;; bool1 = false)
        {
          localIConnectionServiceAdapter2.setIsVoipAudioMode(str, bool1);
          return;
        }
      case 17: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setStatusHints((String)paramAnonymousMessage.arg1, (StatusHints)paramAnonymousMessage.arg2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 18: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setAddress((String)paramAnonymousMessage.arg1, (Uri)paramAnonymousMessage.arg2, paramAnonymousMessage.argi1);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 19: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setCallerDisplayName((String)paramAnonymousMessage.arg1, (String)paramAnonymousMessage.arg2, paramAnonymousMessage.argi1);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 20: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setConferenceableConnections((String)paramAnonymousMessage.arg1, (List)paramAnonymousMessage.arg2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 21: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).addExistingConnection((String)paramAnonymousMessage.arg1, (ParcelableConnection)paramAnonymousMessage.arg2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 23: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).setConferenceMergeFailed((String)paramAnonymousMessage.arg1);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 24: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).putExtras((String)paramAnonymousMessage.arg1, (Bundle)paramAnonymousMessage.arg2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 25: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).removeExtras((String)paramAnonymousMessage.arg1, (List)paramAnonymousMessage.arg2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      }
      paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
      try
      {
        ConnectionServiceAdapterServant.-get0(ConnectionServiceAdapterServant.this).onConnectionEvent((String)paramAnonymousMessage.arg1, (String)paramAnonymousMessage.arg2, (Bundle)paramAnonymousMessage.arg3);
        return;
      }
      finally
      {
        paramAnonymousMessage.recycle();
      }
    }
    
    public void handleMessage(Message paramAnonymousMessage)
    {
      try
      {
        internalHandleMessage(paramAnonymousMessage);
        return;
      }
      catch (RemoteException paramAnonymousMessage) {}
    }
  };
  private final IConnectionServiceAdapter mStub = new IConnectionServiceAdapter.Stub()
  {
    public void addConferenceCall(String paramAnonymousString, ParcelableConference paramAnonymousParcelableConference)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousParcelableConference;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(10, localSomeArgs).sendToTarget();
    }
    
    public final void addExistingConnection(String paramAnonymousString, ParcelableConnection paramAnonymousParcelableConnection)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousParcelableConnection;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(21, localSomeArgs).sendToTarget();
    }
    
    public void handleCreateConnectionComplete(String paramAnonymousString, ConnectionRequest paramAnonymousConnectionRequest, ParcelableConnection paramAnonymousParcelableConnection)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousConnectionRequest;
      localSomeArgs.arg3 = paramAnonymousParcelableConnection;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(1, localSomeArgs).sendToTarget();
    }
    
    public final void onConnectionEvent(String paramAnonymousString1, String paramAnonymousString2, Bundle paramAnonymousBundle)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString1;
      localSomeArgs.arg2 = paramAnonymousString2;
      localSomeArgs.arg3 = paramAnonymousBundle;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(26, localSomeArgs).sendToTarget();
    }
    
    public void onPostDialChar(String paramAnonymousString, char paramAnonymousChar)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.argi1 = paramAnonymousChar;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(22, localSomeArgs).sendToTarget();
    }
    
    public void onPostDialWait(String paramAnonymousString1, String paramAnonymousString2)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString1;
      localSomeArgs.arg2 = paramAnonymousString2;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(12, localSomeArgs).sendToTarget();
    }
    
    public final void putExtras(String paramAnonymousString, Bundle paramAnonymousBundle)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousBundle;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(24, localSomeArgs).sendToTarget();
    }
    
    public void queryRemoteConnectionServices(RemoteServiceCallback paramAnonymousRemoteServiceCallback)
    {
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(13, paramAnonymousRemoteServiceCallback).sendToTarget();
    }
    
    public void removeCall(String paramAnonymousString)
    {
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(11, paramAnonymousString).sendToTarget();
    }
    
    public final void removeExtras(String paramAnonymousString, List<String> paramAnonymousList)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousList;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(25, localSomeArgs).sendToTarget();
    }
    
    public void setActive(String paramAnonymousString)
    {
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(2, paramAnonymousString).sendToTarget();
    }
    
    public final void setAddress(String paramAnonymousString, Uri paramAnonymousUri, int paramAnonymousInt)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousUri;
      localSomeArgs.argi1 = paramAnonymousInt;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(18, localSomeArgs).sendToTarget();
    }
    
    public final void setCallerDisplayName(String paramAnonymousString1, String paramAnonymousString2, int paramAnonymousInt)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString1;
      localSomeArgs.arg2 = paramAnonymousString2;
      localSomeArgs.argi1 = paramAnonymousInt;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(19, localSomeArgs).sendToTarget();
    }
    
    public void setConferenceMergeFailed(String paramAnonymousString)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(23, localSomeArgs).sendToTarget();
    }
    
    public final void setConferenceableConnections(String paramAnonymousString, List<String> paramAnonymousList)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousList;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(20, localSomeArgs).sendToTarget();
    }
    
    public void setConnectionCapabilities(String paramAnonymousString, int paramAnonymousInt)
    {
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(8, paramAnonymousInt, 0, paramAnonymousString).sendToTarget();
    }
    
    public void setConnectionProperties(String paramAnonymousString, int paramAnonymousInt)
    {
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(27, paramAnonymousInt, 0, paramAnonymousString).sendToTarget();
    }
    
    public void setDialing(String paramAnonymousString)
    {
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(4, paramAnonymousString).sendToTarget();
    }
    
    public void setDisconnected(String paramAnonymousString, DisconnectCause paramAnonymousDisconnectCause)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousDisconnectCause;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(5, localSomeArgs).sendToTarget();
    }
    
    public void setIsConferenced(String paramAnonymousString1, String paramAnonymousString2)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString1;
      localSomeArgs.arg2 = paramAnonymousString2;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(9, localSomeArgs).sendToTarget();
    }
    
    public final void setIsVoipAudioMode(String paramAnonymousString, boolean paramAnonymousBoolean)
    {
      Handler localHandler = ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this);
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(16, i, 0, paramAnonymousString).sendToTarget();
        return;
      }
    }
    
    public void setOnHold(String paramAnonymousString)
    {
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(6, paramAnonymousString).sendToTarget();
    }
    
    public void setPulling(String paramAnonymousString)
    {
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(28, paramAnonymousString).sendToTarget();
    }
    
    public void setRingbackRequested(String paramAnonymousString, boolean paramAnonymousBoolean)
    {
      Handler localHandler = ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this);
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(7, i, 0, paramAnonymousString).sendToTarget();
        return;
      }
    }
    
    public void setRinging(String paramAnonymousString)
    {
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(3, paramAnonymousString).sendToTarget();
    }
    
    public final void setStatusHints(String paramAnonymousString, StatusHints paramAnonymousStatusHints)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousStatusHints;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(17, localSomeArgs).sendToTarget();
    }
    
    public void setVideoProvider(String paramAnonymousString, IVideoProvider paramAnonymousIVideoProvider)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousIVideoProvider;
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(15, localSomeArgs).sendToTarget();
    }
    
    public void setVideoState(String paramAnonymousString, int paramAnonymousInt)
    {
      ConnectionServiceAdapterServant.-get1(ConnectionServiceAdapterServant.this).obtainMessage(14, paramAnonymousInt, 0, paramAnonymousString).sendToTarget();
    }
  };
  
  public ConnectionServiceAdapterServant(IConnectionServiceAdapter paramIConnectionServiceAdapter)
  {
    this.mDelegate = paramIConnectionServiceAdapter;
  }
  
  public IConnectionServiceAdapter getStub()
  {
    return this.mStub;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/ConnectionServiceAdapterServant.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */