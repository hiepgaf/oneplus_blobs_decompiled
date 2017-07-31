package android.telecom;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import com.android.internal.os.SomeArgs;
import com.android.internal.telecom.IConnectionService.Stub;
import com.android.internal.telecom.IConnectionServiceAdapter;
import com.android.internal.telecom.IVideoProvider;
import com.android.internal.telecom.RemoteServiceCallback.Stub;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ConnectionService
  extends Service
{
  private static final int MSG_ABORT = 3;
  private static final int MSG_ADD_CONNECTION_SERVICE_ADAPTER = 1;
  private static final int MSG_ADD_PARTICIPANT_WITH_CONFERENCE = 30;
  private static final int MSG_ANSWER = 4;
  private static final int MSG_ANSWER_VIDEO = 17;
  private static final int MSG_CONFERENCE = 12;
  private static final int MSG_CREATE_CONNECTION = 2;
  private static final int MSG_DISCONNECT = 6;
  private static final int MSG_HOLD = 7;
  private static final int MSG_MERGE_CONFERENCE = 18;
  private static final int MSG_ON_CALL_AUDIO_STATE_CHANGED = 9;
  private static final int MSG_ON_EXTRAS_CHANGED = 24;
  private static final int MSG_ON_POST_DIAL_CONTINUE = 14;
  private static final int MSG_PLAY_DTMF_TONE = 10;
  private static final int MSG_PULL_EXTERNAL_CALL = 22;
  private static final int MSG_REJECT = 5;
  private static final int MSG_REJECT_WITH_MESSAGE = 20;
  private static final int MSG_REMOVE_CONNECTION_SERVICE_ADAPTER = 16;
  private static final int MSG_SEND_CALL_EVENT = 23;
  private static final int MSG_SILENCE = 21;
  private static final int MSG_SPLIT_FROM_CONFERENCE = 13;
  private static final int MSG_STOP_DTMF_TONE = 11;
  private static final int MSG_SWAP_CONFERENCE = 19;
  private static final int MSG_UNHOLD = 8;
  private static final boolean PII_DEBUG = Log.isLoggable(3);
  public static final String SERVICE_INTERFACE = "android.telecom.ConnectionService";
  private static Connection sNullConnection;
  private final ConnectionServiceAdapter mAdapter = new ConnectionServiceAdapter();
  private boolean mAreAccountsInitialized = false;
  private final IBinder mBinder = new IConnectionService.Stub()
  {
    public void abort(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(3, paramAnonymousString).sendToTarget();
    }
    
    public void addConnectionServiceAdapter(IConnectionServiceAdapter paramAnonymousIConnectionServiceAdapter)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(1, paramAnonymousIConnectionServiceAdapter).sendToTarget();
    }
    
    public void addParticipantWithConference(String paramAnonymousString1, String paramAnonymousString2)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString1;
      localSomeArgs.arg2 = paramAnonymousString2;
      ConnectionService.-get2(ConnectionService.this).obtainMessage(30, localSomeArgs).sendToTarget();
    }
    
    public void answer(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(4, paramAnonymousString).sendToTarget();
    }
    
    public void answerVideo(String paramAnonymousString, int paramAnonymousInt)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.argi1 = paramAnonymousInt;
      ConnectionService.-get2(ConnectionService.this).obtainMessage(17, localSomeArgs).sendToTarget();
    }
    
    public void conference(String paramAnonymousString1, String paramAnonymousString2)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString1;
      localSomeArgs.arg2 = paramAnonymousString2;
      ConnectionService.-get2(ConnectionService.this).obtainMessage(12, localSomeArgs).sendToTarget();
    }
    
    public void createConnection(PhoneAccountHandle paramAnonymousPhoneAccountHandle, String paramAnonymousString, ConnectionRequest paramAnonymousConnectionRequest, boolean paramAnonymousBoolean1, boolean paramAnonymousBoolean2)
    {
      int j = 1;
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousPhoneAccountHandle;
      localSomeArgs.arg2 = paramAnonymousString;
      localSomeArgs.arg3 = paramAnonymousConnectionRequest;
      if (paramAnonymousBoolean1)
      {
        i = 1;
        localSomeArgs.argi1 = i;
        if (!paramAnonymousBoolean2) {
          break label80;
        }
      }
      label80:
      for (int i = j;; i = 0)
      {
        localSomeArgs.argi2 = i;
        ConnectionService.-get2(ConnectionService.this).obtainMessage(2, localSomeArgs).sendToTarget();
        return;
        i = 0;
        break;
      }
    }
    
    public void disconnect(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(6, paramAnonymousString).sendToTarget();
    }
    
    public void hold(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(7, paramAnonymousString).sendToTarget();
    }
    
    public void mergeConference(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(18, paramAnonymousString).sendToTarget();
    }
    
    public void onCallAudioStateChanged(String paramAnonymousString, CallAudioState paramAnonymousCallAudioState)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousCallAudioState;
      ConnectionService.-get2(ConnectionService.this).obtainMessage(9, localSomeArgs).sendToTarget();
    }
    
    public void onExtrasChanged(String paramAnonymousString, Bundle paramAnonymousBundle)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      localSomeArgs.arg2 = paramAnonymousBundle;
      ConnectionService.-get2(ConnectionService.this).obtainMessage(24, localSomeArgs).sendToTarget();
    }
    
    public void onPostDialContinue(String paramAnonymousString, boolean paramAnonymousBoolean)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString;
      if (paramAnonymousBoolean) {}
      for (int i = 1;; i = 0)
      {
        localSomeArgs.argi1 = i;
        ConnectionService.-get2(ConnectionService.this).obtainMessage(14, localSomeArgs).sendToTarget();
        return;
      }
    }
    
    public void playDtmfTone(String paramAnonymousString, char paramAnonymousChar)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(10, paramAnonymousChar, 0, paramAnonymousString).sendToTarget();
    }
    
    public void pullExternalCall(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(22, paramAnonymousString).sendToTarget();
    }
    
    public void reject(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(5, paramAnonymousString).sendToTarget();
    }
    
    public void rejectWithMessage(String paramAnonymousString1, String paramAnonymousString2)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString1;
      localSomeArgs.arg2 = paramAnonymousString2;
      ConnectionService.-get2(ConnectionService.this).obtainMessage(20, localSomeArgs).sendToTarget();
    }
    
    public void removeConnectionServiceAdapter(IConnectionServiceAdapter paramAnonymousIConnectionServiceAdapter)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(16, paramAnonymousIConnectionServiceAdapter).sendToTarget();
    }
    
    public void sendCallEvent(String paramAnonymousString1, String paramAnonymousString2, Bundle paramAnonymousBundle)
    {
      SomeArgs localSomeArgs = SomeArgs.obtain();
      localSomeArgs.arg1 = paramAnonymousString1;
      localSomeArgs.arg2 = paramAnonymousString2;
      localSomeArgs.arg3 = paramAnonymousBundle;
      ConnectionService.-get2(ConnectionService.this).obtainMessage(23, localSomeArgs).sendToTarget();
    }
    
    public void silence(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(21, paramAnonymousString).sendToTarget();
    }
    
    public void splitFromConference(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(13, paramAnonymousString).sendToTarget();
    }
    
    public void stopDtmfTone(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(11, paramAnonymousString).sendToTarget();
    }
    
    public void swapConference(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(19, paramAnonymousString).sendToTarget();
    }
    
    public void unhold(String paramAnonymousString)
    {
      ConnectionService.-get2(ConnectionService.this).obtainMessage(8, paramAnonymousString).sendToTarget();
    }
  };
  private final Map<String, Conference> mConferenceById = new ConcurrentHashMap();
  private final Conference.Listener mConferenceListener = new Conference.Listener()
  {
    public void onConferenceMergeFailed(Conference paramAnonymousConference)
    {
      paramAnonymousConference = (String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference);
      if (paramAnonymousConference != null) {
        ConnectionService.-get0(ConnectionService.this).onConferenceMergeFailed(paramAnonymousConference);
      }
    }
    
    public void onConferenceableConnectionsChanged(Conference paramAnonymousConference, List<Connection> paramAnonymousList)
    {
      ConnectionService.-get0(ConnectionService.this).setConferenceableConnections((String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference), ConnectionService.-wrap0(ConnectionService.this, paramAnonymousList));
    }
    
    public void onConnectionAdded(Conference paramAnonymousConference, Connection paramAnonymousConnection) {}
    
    public void onConnectionCapabilitiesChanged(Conference paramAnonymousConference, int paramAnonymousInt)
    {
      paramAnonymousConference = (String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference);
      Log.d(this, "call capabilities: conference: %s", new Object[] { Connection.capabilitiesToString(paramAnonymousInt) });
      ConnectionService.-get0(ConnectionService.this).setConnectionCapabilities(paramAnonymousConference, paramAnonymousInt);
    }
    
    public void onConnectionPropertiesChanged(Conference paramAnonymousConference, int paramAnonymousInt)
    {
      paramAnonymousConference = (String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference);
      Log.d(this, "call capabilities: conference: %s", new Object[] { Connection.propertiesToString(paramAnonymousInt) });
      ConnectionService.-get0(ConnectionService.this).setConnectionProperties(paramAnonymousConference, paramAnonymousInt);
    }
    
    public void onConnectionRemoved(Conference paramAnonymousConference, Connection paramAnonymousConnection) {}
    
    public void onDestroyed(Conference paramAnonymousConference)
    {
      ConnectionService.-wrap20(ConnectionService.this, paramAnonymousConference);
    }
    
    public void onDisconnected(Conference paramAnonymousConference, DisconnectCause paramAnonymousDisconnectCause)
    {
      paramAnonymousConference = (String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference);
      ConnectionService.-get0(ConnectionService.this).setDisconnected(paramAnonymousConference, paramAnonymousDisconnectCause);
    }
    
    public void onExtrasChanged(Conference paramAnonymousConference, Bundle paramAnonymousBundle)
    {
      paramAnonymousConference = (String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference);
      if (paramAnonymousConference != null) {
        ConnectionService.-get0(ConnectionService.this).putExtras(paramAnonymousConference, paramAnonymousBundle);
      }
    }
    
    public void onExtrasRemoved(Conference paramAnonymousConference, List<String> paramAnonymousList)
    {
      paramAnonymousConference = (String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference);
      if (paramAnonymousConference != null) {
        ConnectionService.-get0(ConnectionService.this).removeExtras(paramAnonymousConference, paramAnonymousList);
      }
    }
    
    public void onStateChanged(Conference paramAnonymousConference, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      paramAnonymousConference = (String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference);
      switch (paramAnonymousInt2)
      {
      case 6: 
      default: 
        return;
      case 4: 
        ConnectionService.-get0(ConnectionService.this).setActive(paramAnonymousConference);
        return;
      }
      ConnectionService.-get0(ConnectionService.this).setOnHold(paramAnonymousConference);
    }
    
    public void onStatusHintsChanged(Conference paramAnonymousConference, StatusHints paramAnonymousStatusHints)
    {
      paramAnonymousConference = (String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference);
      if (paramAnonymousConference != null) {
        ConnectionService.-get0(ConnectionService.this).setStatusHints(paramAnonymousConference, paramAnonymousStatusHints);
      }
    }
    
    public void onVideoProviderChanged(Conference paramAnonymousConference, Connection.VideoProvider paramAnonymousVideoProvider)
    {
      String str = (String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference);
      Log.d(this, "onVideoProviderChanged: Connection: %s, VideoProvider: %s", new Object[] { paramAnonymousConference, paramAnonymousVideoProvider });
      ConnectionService.-get0(ConnectionService.this).setVideoProvider(str, paramAnonymousVideoProvider);
    }
    
    public void onVideoStateChanged(Conference paramAnonymousConference, int paramAnonymousInt)
    {
      paramAnonymousConference = (String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference);
      Log.d(this, "onVideoStateChanged set video state %d", new Object[] { Integer.valueOf(paramAnonymousInt) });
      ConnectionService.-get0(ConnectionService.this).setVideoState(paramAnonymousConference, paramAnonymousInt);
    }
  };
  private final Map<String, Connection> mConnectionById = new ConcurrentHashMap();
  private final Connection.Listener mConnectionListener = new Connection.Listener()
  {
    public void onAddressChanged(Connection paramAnonymousConnection, Uri paramAnonymousUri, int paramAnonymousInt)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      ConnectionService.-get0(ConnectionService.this).setAddress(paramAnonymousConnection, paramAnonymousUri, paramAnonymousInt);
    }
    
    public void onAudioModeIsVoipChanged(Connection paramAnonymousConnection, boolean paramAnonymousBoolean)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      ConnectionService.-get0(ConnectionService.this).setIsVoipAudioMode(paramAnonymousConnection, paramAnonymousBoolean);
    }
    
    public void onCallerDisplayNameChanged(Connection paramAnonymousConnection, String paramAnonymousString, int paramAnonymousInt)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      ConnectionService.-get0(ConnectionService.this).setCallerDisplayName(paramAnonymousConnection, paramAnonymousString, paramAnonymousInt);
    }
    
    public void onConferenceChanged(Connection paramAnonymousConnection, Conference paramAnonymousConference)
    {
      String str = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      if (str != null)
      {
        paramAnonymousConnection = null;
        if (paramAnonymousConference != null) {
          paramAnonymousConnection = (String)ConnectionService.-get3(ConnectionService.this).get(paramAnonymousConference);
        }
        ConnectionService.-get0(ConnectionService.this).setIsConferenced(str, paramAnonymousConnection);
      }
    }
    
    public void onConferenceMergeFailed(Connection paramAnonymousConnection)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      if (paramAnonymousConnection != null) {
        ConnectionService.-get0(ConnectionService.this).onConferenceMergeFailed(paramAnonymousConnection);
      }
    }
    
    public void onConferenceablesChanged(Connection paramAnonymousConnection, List<Conferenceable> paramAnonymousList)
    {
      ConnectionService.-get0(ConnectionService.this).setConferenceableConnections((String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection), ConnectionService.-wrap1(ConnectionService.this, paramAnonymousList));
    }
    
    public void onConnectionCapabilitiesChanged(Connection paramAnonymousConnection, int paramAnonymousInt)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      Log.d(this, "capabilities: parcelableconnection: %s", new Object[] { Connection.capabilitiesToString(paramAnonymousInt) });
      ConnectionService.-get0(ConnectionService.this).setConnectionCapabilities(paramAnonymousConnection, paramAnonymousInt);
    }
    
    public void onConnectionEvent(Connection paramAnonymousConnection, String paramAnonymousString, Bundle paramAnonymousBundle)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      if (paramAnonymousConnection != null) {
        ConnectionService.-get0(ConnectionService.this).onConnectionEvent(paramAnonymousConnection, paramAnonymousString, paramAnonymousBundle);
      }
    }
    
    public void onConnectionPropertiesChanged(Connection paramAnonymousConnection, int paramAnonymousInt)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      Log.d(this, "properties: parcelableconnection: %s", new Object[] { Connection.propertiesToString(paramAnonymousInt) });
      ConnectionService.-get0(ConnectionService.this).setConnectionProperties(paramAnonymousConnection, paramAnonymousInt);
    }
    
    public void onDestroyed(Connection paramAnonymousConnection)
    {
      ConnectionService.this.removeConnection(paramAnonymousConnection);
    }
    
    public void onDisconnected(Connection paramAnonymousConnection, DisconnectCause paramAnonymousDisconnectCause)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      Log.d(this, "Adapter set disconnected %s", new Object[] { paramAnonymousDisconnectCause });
      ConnectionService.-get0(ConnectionService.this).setDisconnected(paramAnonymousConnection, paramAnonymousDisconnectCause);
    }
    
    public void onExtrasChanged(Connection paramAnonymousConnection, Bundle paramAnonymousBundle)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      if (paramAnonymousConnection != null) {
        ConnectionService.-get0(ConnectionService.this).putExtras(paramAnonymousConnection, paramAnonymousBundle);
      }
    }
    
    public void onExtrasRemoved(Connection paramAnonymousConnection, List<String> paramAnonymousList)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      if (paramAnonymousConnection != null) {
        ConnectionService.-get0(ConnectionService.this).removeExtras(paramAnonymousConnection, paramAnonymousList);
      }
    }
    
    public void onPostDialChar(Connection paramAnonymousConnection, char paramAnonymousChar)
    {
      String str = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      Log.d(this, "Adapter onPostDialChar %s, %s", new Object[] { paramAnonymousConnection, Character.valueOf(paramAnonymousChar) });
      ConnectionService.-get0(ConnectionService.this).onPostDialChar(str, paramAnonymousChar);
    }
    
    public void onPostDialWait(Connection paramAnonymousConnection, String paramAnonymousString)
    {
      String str = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      Log.d(this, "Adapter onPostDialWait %s, %s", new Object[] { paramAnonymousConnection, paramAnonymousString });
      ConnectionService.-get0(ConnectionService.this).onPostDialWait(str, paramAnonymousString);
    }
    
    public void onRingbackRequested(Connection paramAnonymousConnection, boolean paramAnonymousBoolean)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      Log.d(this, "Adapter onRingback %b", new Object[] { Boolean.valueOf(paramAnonymousBoolean) });
      ConnectionService.-get0(ConnectionService.this).setRingbackRequested(paramAnonymousConnection, paramAnonymousBoolean);
    }
    
    public void onStateChanged(Connection paramAnonymousConnection, int paramAnonymousInt)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      Log.d(this, "Adapter set state %s %s", new Object[] { paramAnonymousConnection, Connection.stateToString(paramAnonymousInt) });
      switch (paramAnonymousInt)
      {
      case 1: 
      case 6: 
      default: 
        return;
      case 4: 
        ConnectionService.-get0(ConnectionService.this).setActive(paramAnonymousConnection);
        return;
      case 3: 
        ConnectionService.-get0(ConnectionService.this).setDialing(paramAnonymousConnection);
        return;
      case 7: 
        ConnectionService.-get0(ConnectionService.this).setPulling(paramAnonymousConnection);
        return;
      case 5: 
        ConnectionService.-get0(ConnectionService.this).setOnHold(paramAnonymousConnection);
        return;
      }
      ConnectionService.-get0(ConnectionService.this).setRinging(paramAnonymousConnection);
    }
    
    public void onStatusHintsChanged(Connection paramAnonymousConnection, StatusHints paramAnonymousStatusHints)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      ConnectionService.-get0(ConnectionService.this).setStatusHints(paramAnonymousConnection, paramAnonymousStatusHints);
    }
    
    public void onVideoProviderChanged(Connection paramAnonymousConnection, Connection.VideoProvider paramAnonymousVideoProvider)
    {
      String str = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      Log.d(this, "onVideoProviderChanged: Connection: %s, VideoProvider: %s", new Object[] { paramAnonymousConnection, paramAnonymousVideoProvider });
      ConnectionService.-get0(ConnectionService.this).setVideoProvider(str, paramAnonymousVideoProvider);
    }
    
    public void onVideoStateChanged(Connection paramAnonymousConnection, int paramAnonymousInt)
    {
      paramAnonymousConnection = (String)ConnectionService.-get4(ConnectionService.this).get(paramAnonymousConnection);
      Log.d(this, "Adapter set video state %d", new Object[] { Integer.valueOf(paramAnonymousInt) });
      ConnectionService.-get0(ConnectionService.this).setVideoState(paramAnonymousConnection, paramAnonymousInt);
    }
  };
  private final Handler mHandler = new Handler(Looper.getMainLooper())
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      final Object localObject10;
      final Object localObject11;
      final boolean bool1;
      switch (paramAnonymousMessage.what)
      {
      case 15: 
      case 25: 
      case 26: 
      case 27: 
      case 28: 
      case 29: 
      default: 
        return;
      case 1: 
        ConnectionService.-get0(ConnectionService.this).addAdapter((IConnectionServiceAdapter)paramAnonymousMessage.obj);
        ConnectionService.-wrap13(ConnectionService.this);
        return;
      case 16: 
        ConnectionService.-get0(ConnectionService.this).removeAdapter((IConnectionServiceAdapter)paramAnonymousMessage.obj);
        return;
      case 2: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          final PhoneAccountHandle localPhoneAccountHandle = (PhoneAccountHandle)paramAnonymousMessage.arg1;
          localObject10 = (String)paramAnonymousMessage.arg2;
          localObject11 = (ConnectionRequest)paramAnonymousMessage.arg3;
          final boolean bool2;
          if (paramAnonymousMessage.argi1 == 1)
          {
            bool1 = true;
            if (paramAnonymousMessage.argi2 != 1) {
              break label305;
            }
            bool2 = true;
            if (ConnectionService.-get1(ConnectionService.this)) {
              break label311;
            }
            Log.d(this, "Enqueueing pre-init request %s", new Object[] { localObject10 });
            ConnectionService.-get5(ConnectionService.this).add(new Runnable()
            {
              public void run()
              {
                ConnectionService.-wrap7(ConnectionService.this, localPhoneAccountHandle, localObject10, localObject11, bool1, bool2);
              }
            });
          }
          for (;;)
          {
            return;
            bool1 = false;
            break;
            bool2 = false;
            break label240;
            ConnectionService.-wrap7(ConnectionService.this, localPhoneAccountHandle, (String)localObject10, (ConnectionRequest)localObject11, bool1, bool2);
          }
          ConnectionService.-wrap2(ConnectionService.this, (String)paramAnonymousMessage.obj);
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 3: 
        return;
      case 4: 
        ConnectionService.-wrap5(ConnectionService.this, (String)paramAnonymousMessage.obj);
        return;
      case 17: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          String str1 = (String)paramAnonymousMessage.arg1;
          int i = paramAnonymousMessage.argi1;
          ConnectionService.-wrap4(ConnectionService.this, str1, i);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 5: 
        ConnectionService.-wrap18(ConnectionService.this, (String)paramAnonymousMessage.obj);
        return;
      case 20: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          ConnectionService.-wrap19(ConnectionService.this, (String)paramAnonymousMessage.arg1, (String)paramAnonymousMessage.arg2);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 6: 
        ConnectionService.-wrap8(ConnectionService.this, (String)paramAnonymousMessage.obj);
        return;
      case 21: 
        ConnectionService.-wrap22(ConnectionService.this, (String)paramAnonymousMessage.obj);
        return;
      case 7: 
        ConnectionService.-wrap10(ConnectionService.this, (String)paramAnonymousMessage.obj);
        return;
      case 8: 
        ConnectionService.-wrap26(ConnectionService.this, (String)paramAnonymousMessage.obj);
        return;
      case 9: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          String str2 = (String)paramAnonymousMessage.arg1;
          localObject10 = (CallAudioState)paramAnonymousMessage.arg2;
          ConnectionService.-wrap14(ConnectionService.this, str2, new CallAudioState((CallAudioState)localObject10));
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 10: 
        ConnectionService.-wrap16(ConnectionService.this, (String)paramAnonymousMessage.obj, (char)paramAnonymousMessage.arg1);
        return;
      case 11: 
        ConnectionService.-wrap24(ConnectionService.this, (String)paramAnonymousMessage.obj);
        return;
      case 12: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          String str3 = (String)paramAnonymousMessage.arg1;
          localObject10 = (String)paramAnonymousMessage.arg2;
          ConnectionService.-wrap6(ConnectionService.this, str3, (String)localObject10);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 13: 
        ConnectionService.-wrap23(ConnectionService.this, (String)paramAnonymousMessage.obj);
        return;
      case 30: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          String str4 = (String)paramAnonymousMessage.arg1;
          localObject10 = (String)paramAnonymousMessage.arg2;
          ConnectionService.-wrap3(ConnectionService.this, str4, (String)localObject10);
          return;
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 18: 
        ConnectionService.-wrap11(ConnectionService.this, (String)paramAnonymousMessage.obj);
        return;
      case 19: 
        ConnectionService.-wrap25(ConnectionService.this, (String)paramAnonymousMessage.obj);
        return;
      case 14: 
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          String str5 = (String)paramAnonymousMessage.arg1;
          if (paramAnonymousMessage.argi1 == 1) {}
          for (bool1 = true;; bool1 = false)
          {
            ConnectionService.-wrap15(ConnectionService.this, str5, bool1);
            return;
          }
          ConnectionService.-wrap17(ConnectionService.this, (String)paramAnonymousMessage.obj);
        }
        finally
        {
          paramAnonymousMessage.recycle();
        }
      case 22: 
        return;
      case 23: 
        label240:
        label305:
        label311:
        paramAnonymousMessage = (SomeArgs)paramAnonymousMessage.obj;
        try
        {
          String str6 = (String)paramAnonymousMessage.arg1;
          localObject10 = (String)paramAnonymousMessage.arg2;
          localObject11 = (Bundle)paramAnonymousMessage.arg3;
          ConnectionService.-wrap21(ConnectionService.this, str6, (String)localObject10, (Bundle)localObject11);
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
        String str7 = (String)paramAnonymousMessage.arg1;
        localObject10 = (Bundle)paramAnonymousMessage.arg2;
        ConnectionService.-wrap9(ConnectionService.this, str7, (Bundle)localObject10);
        return;
      }
      finally
      {
        paramAnonymousMessage.recycle();
      }
    }
  };
  private int mId = 0;
  private final Map<Conference, String> mIdByConference = new ConcurrentHashMap();
  private final Map<Connection, String> mIdByConnection = new ConcurrentHashMap();
  private Object mIdSyncRoot = new Object();
  private final List<Runnable> mPreInitializationConnectionRequests = new ArrayList();
  private final RemoteConnectionManager mRemoteConnectionManager = new RemoteConnectionManager(this);
  private Conference sNullConference;
  
  private void abort(String paramString)
  {
    Log.d(this, "abort %s", new Object[] { paramString });
    findConnectionForAction(paramString, "abort").onAbort();
  }
  
  private String addConferenceInternal(Conference paramConference)
  {
    Object localObject2 = null;
    Object localObject1 = localObject2;
    if (paramConference.getExtras() != null)
    {
      localObject1 = localObject2;
      if (paramConference.getExtras().containsKey("android.telecom.extra.ORIGINAL_CONNECTION_ID"))
      {
        localObject1 = paramConference.getExtras().getString("android.telecom.extra.ORIGINAL_CONNECTION_ID");
        Log.d(this, "addConferenceInternal: conf %s reusing original id %s", new Object[] { paramConference.getTelecomCallId(), localObject1 });
      }
    }
    if (this.mIdByConference.containsKey(paramConference)) {
      Log.w(this, "Re-adding an existing conference: %s.", new Object[] { paramConference });
    }
    while (paramConference == null) {
      return null;
    }
    if (localObject1 == null) {
      localObject1 = UUID.randomUUID().toString();
    }
    for (;;)
    {
      this.mConferenceById.put(localObject1, paramConference);
      this.mIdByConference.put(paramConference, localObject1);
      paramConference.addListener(this.mConferenceListener);
      return (String)localObject1;
    }
  }
  
  private void addConnection(String paramString, Connection paramConnection)
  {
    paramConnection.setTelecomCallId(paramString);
    this.mConnectionById.put(paramString, paramConnection);
    this.mIdByConnection.put(paramConnection, paramString);
    paramConnection.addConnectionListener(this.mConnectionListener);
    paramConnection.setConnectionService(this);
  }
  
  private String addExistingConnectionInternal(PhoneAccountHandle paramPhoneAccountHandle, Connection paramConnection)
  {
    if ((paramConnection.getExtras() != null) && (paramConnection.getExtras().containsKey("android.telecom.extra.ORIGINAL_CONNECTION_ID")))
    {
      paramPhoneAccountHandle = paramConnection.getExtras().getString("android.telecom.extra.ORIGINAL_CONNECTION_ID");
      Log.d(this, "addExistingConnectionInternal - conn %s reusing original id %s", new Object[] { paramConnection.getTelecomCallId(), paramPhoneAccountHandle });
    }
    for (;;)
    {
      addConnection(paramPhoneAccountHandle, paramConnection);
      return paramPhoneAccountHandle;
      if (paramPhoneAccountHandle == null) {
        paramPhoneAccountHandle = UUID.randomUUID().toString();
      } else {
        paramPhoneAccountHandle = paramPhoneAccountHandle.getComponentName().getClassName() + "@" + getNextCallId();
      }
    }
  }
  
  private void addParticipantWithConference(String paramString1, String paramString2)
  {
    Log.d(this, "ConnectionService addParticipantWithConference(%s, %s)", new Object[] { paramString2, paramString1 });
    Conference localConference = findConferenceForAction(paramString1, "addParticipantWithConference");
    paramString1 = findConnectionForAction(paramString1, "addParticipantWithConnection");
    if (paramString1 != getNullConnection()) {
      onAddParticipant(paramString1, paramString2);
    }
    while (localConference == getNullConference()) {
      return;
    }
    localConference.onAddParticipant(paramString2);
  }
  
  private void answer(String paramString)
  {
    Log.d(this, "answer %s", new Object[] { paramString });
    findConnectionForAction(paramString, "answer").onAnswer();
  }
  
  private void answerVideo(String paramString, int paramInt)
  {
    Log.d(this, "answerVideo %s", new Object[] { paramString });
    findConnectionForAction(paramString, "answer").onAnswer(paramInt);
  }
  
  private void conference(String paramString1, String paramString2)
  {
    Log.d(this, "conference %s, %s", new Object[] { paramString1, paramString2 });
    Connection localConnection = findConnectionForAction(paramString2, "conference");
    Object localObject = getNullConference();
    if (localConnection == getNullConnection())
    {
      Conference localConference = findConferenceForAction(paramString2, "conference");
      localObject = localConference;
      if (localConference == getNullConference())
      {
        Log.w(this, "Connection2 or Conference2 missing in conference request %s.", new Object[] { paramString2 });
        return;
      }
    }
    paramString2 = findConnectionForAction(paramString1, "conference");
    if (paramString2 == getNullConnection())
    {
      paramString2 = findConferenceForAction(paramString1, "addConnection");
      if (paramString2 == getNullConference())
      {
        Log.w(this, "Connection1 or Conference1 missing in conference request %s.", new Object[] { paramString1 });
        return;
      }
      if (localConnection != getNullConnection())
      {
        paramString2.onMerge(localConnection);
        return;
      }
      Log.wtf(this, "There can only be one conference and an attempt was made to merge two conferences.", new Object[0]);
      return;
    }
    if (localObject != getNullConference())
    {
      ((Conference)localObject).onMerge(paramString2);
      return;
    }
    onConference(paramString2, localConnection);
  }
  
  private void createConnection(PhoneAccountHandle paramPhoneAccountHandle, String paramString, ConnectionRequest paramConnectionRequest, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramString == null)
    {
      Log.d(this, "createConnection,callId is null, return.", new Object[0]);
      return;
    }
    Log.d(this, "createConnection, callManagerAccount: %s, callId: %s, request: %s, isIncoming: %b, isUnknown: %b", new Object[] { paramPhoneAccountHandle, paramString, paramConnectionRequest, Boolean.valueOf(paramBoolean1), Boolean.valueOf(paramBoolean2) });
    Object localObject;
    label139:
    ConnectionServiceAdapter localConnectionServiceAdapter;
    PhoneAccountHandle localPhoneAccountHandle;
    int i;
    int j;
    int k;
    Uri localUri;
    int m;
    String str;
    int n;
    if (paramBoolean2)
    {
      paramPhoneAccountHandle = onCreateUnknownConnection(paramPhoneAccountHandle, paramConnectionRequest);
      Log.d(this, "createConnection, connection: %s", new Object[] { paramPhoneAccountHandle });
      localObject = paramPhoneAccountHandle;
      if (paramPhoneAccountHandle == null) {
        localObject = Connection.createFailedConnection(new DisconnectCause(1));
      }
      ((Connection)localObject).setTelecomCallId(paramString);
      if (((Connection)localObject).getState() != 6) {
        addConnection(paramString, (Connection)localObject);
      }
      paramPhoneAccountHandle = ((Connection)localObject).getAddress();
      if (paramPhoneAccountHandle != null) {
        break label386;
      }
      paramPhoneAccountHandle = "null";
      Log.v(this, "createConnection, number: %s, state: %s, capabilities: %s, properties: %s", new Object[] { Connection.toLogSafePhoneNumber(paramPhoneAccountHandle), Connection.stateToString(((Connection)localObject).getState()), Connection.capabilitiesToString(((Connection)localObject).getConnectionCapabilities()), Connection.propertiesToString(((Connection)localObject).getConnectionProperties()) });
      Log.d(this, "createConnection, calling handleCreateConnectionSuccessful %s", new Object[] { paramString });
      localConnectionServiceAdapter = this.mAdapter;
      localPhoneAccountHandle = paramConnectionRequest.getAccountHandle();
      i = ((Connection)localObject).getState();
      j = ((Connection)localObject).getConnectionCapabilities();
      k = ((Connection)localObject).getConnectionProperties();
      localUri = ((Connection)localObject).getAddress();
      m = ((Connection)localObject).getAddressPresentation();
      str = ((Connection)localObject).getCallerDisplayName();
      n = ((Connection)localObject).getCallerDisplayNamePresentation();
      if (((Connection)localObject).getVideoProvider() != null) {
        break label394;
      }
    }
    label386:
    label394:
    for (paramPhoneAccountHandle = null;; paramPhoneAccountHandle = ((Connection)localObject).getVideoProvider().getInterface())
    {
      localConnectionServiceAdapter.handleCreateConnectionComplete(paramString, paramConnectionRequest, new ParcelableConnection(localPhoneAccountHandle, i, j, k, localUri, m, str, n, paramPhoneAccountHandle, ((Connection)localObject).getVideoState(), ((Connection)localObject).isRingbackRequested(), ((Connection)localObject).getAudioModeIsVoip(), ((Connection)localObject).getConnectTimeMillis(), ((Connection)localObject).getStatusHints(), ((Connection)localObject).getDisconnectCause(), createIdList(((Connection)localObject).getConferenceables()), ((Connection)localObject).getExtras()));
      if (paramBoolean2) {
        triggerConferenceRecalculate();
      }
      return;
      if (paramBoolean1)
      {
        paramPhoneAccountHandle = onCreateIncomingConnection(paramPhoneAccountHandle, paramConnectionRequest);
        break;
      }
      paramPhoneAccountHandle = onCreateOutgoingConnection(paramPhoneAccountHandle, paramConnectionRequest);
      break;
      paramPhoneAccountHandle = paramPhoneAccountHandle.getSchemeSpecificPart();
      break label139;
    }
  }
  
  private List<String> createConnectionIdList(List<Connection> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Connection localConnection = (Connection)paramList.next();
      if (this.mIdByConnection.containsKey(localConnection)) {
        localArrayList.add((String)this.mIdByConnection.get(localConnection));
      }
    }
    Collections.sort(localArrayList);
    return localArrayList;
  }
  
  private List<String> createIdList(List<Conferenceable> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Object localObject = (Conferenceable)paramList.next();
      if ((localObject instanceof Connection))
      {
        localObject = (Connection)localObject;
        if (this.mIdByConnection.containsKey(localObject)) {
          localArrayList.add((String)this.mIdByConnection.get(localObject));
        }
      }
      else if ((localObject instanceof Conference))
      {
        localObject = (Conference)localObject;
        if (this.mIdByConference.containsKey(localObject)) {
          localArrayList.add((String)this.mIdByConference.get(localObject));
        }
      }
    }
    Collections.sort(localArrayList);
    return localArrayList;
  }
  
  private void disconnect(String paramString)
  {
    Log.d(this, "disconnect %s", new Object[] { paramString });
    if (this.mConnectionById.containsKey(paramString))
    {
      findConnectionForAction(paramString, "disconnect").onDisconnect();
      return;
    }
    findConferenceForAction(paramString, "disconnect").onDisconnect();
  }
  
  private void endAllConnections()
  {
    Iterator localIterator = this.mIdByConnection.keySet().iterator();
    while (localIterator.hasNext())
    {
      Connection localConnection = (Connection)localIterator.next();
      if (localConnection.getConference() == null) {
        localConnection.onDisconnect();
      }
    }
    localIterator = this.mIdByConference.keySet().iterator();
    while (localIterator.hasNext()) {
      ((Conference)localIterator.next()).onDisconnect();
    }
  }
  
  private Conference findConferenceForAction(String paramString1, String paramString2)
  {
    if (this.mConferenceById.containsKey(paramString1)) {
      return (Conference)this.mConferenceById.get(paramString1);
    }
    Log.w(this, "%s - Cannot find conference %s", new Object[] { paramString2, paramString1 });
    return getNullConference();
  }
  
  private Connection findConnectionForAction(String paramString1, String paramString2)
  {
    if (this.mConnectionById.containsKey(paramString1)) {
      return (Connection)this.mConnectionById.get(paramString1);
    }
    Log.w(this, "%s - Cannot find Connection %s", new Object[] { paramString2, paramString1 });
    return getNullConnection();
  }
  
  private int getNextCallId()
  {
    synchronized (this.mIdSyncRoot)
    {
      int i = this.mId + 1;
      this.mId = i;
      return i;
    }
  }
  
  private Conference getNullConference()
  {
    if (this.sNullConference == null) {
      this.sNullConference = new Conference(null) {};
    }
    return this.sNullConference;
  }
  
  static Connection getNullConnection()
  {
    try
    {
      if (sNullConnection == null) {
        sNullConnection = new Connection() {};
      }
      Connection localConnection = sNullConnection;
      return localConnection;
    }
    finally {}
  }
  
  private void handleExtrasChanged(String paramString, Bundle paramBundle)
  {
    Log.d(this, "handleExtrasChanged(%s, %s)", new Object[] { paramString, paramBundle });
    if (this.mConnectionById.containsKey(paramString)) {
      findConnectionForAction(paramString, "handleExtrasChanged").handleExtrasChanged(paramBundle);
    }
    while (!this.mConferenceById.containsKey(paramString)) {
      return;
    }
    findConferenceForAction(paramString, "handleExtrasChanged").handleExtrasChanged(paramBundle);
  }
  
  private void hold(String paramString)
  {
    Log.d(this, "hold %s", new Object[] { paramString });
    if (this.mConnectionById.containsKey(paramString))
    {
      findConnectionForAction(paramString, "hold").onHold();
      return;
    }
    findConferenceForAction(paramString, "hold").onHold();
  }
  
  private void mergeConference(String paramString)
  {
    Log.d(this, "mergeConference(%s)", new Object[] { paramString });
    paramString = findConferenceForAction(paramString, "mergeConference");
    if (paramString != null) {
      paramString.onMerge();
    }
  }
  
  private void onAccountsInitialized()
  {
    this.mAreAccountsInitialized = true;
    Iterator localIterator = this.mPreInitializationConnectionRequests.iterator();
    while (localIterator.hasNext()) {
      ((Runnable)localIterator.next()).run();
    }
    this.mPreInitializationConnectionRequests.clear();
  }
  
  private void onAdapterAttached()
  {
    if (this.mAreAccountsInitialized) {
      return;
    }
    this.mAdapter.queryRemoteConnectionServices(new RemoteServiceCallback.Stub()
    {
      public void onError()
      {
        ConnectionService.-get2(ConnectionService.this).post(new Runnable()
        {
          public void run()
          {
            ConnectionService.-set0(ConnectionService.this, true);
          }
        });
      }
      
      public void onResult(final List<ComponentName> paramAnonymousList, final List<IBinder> paramAnonymousList1)
      {
        ConnectionService.-get2(ConnectionService.this).post(new Runnable()
        {
          public void run()
          {
            int i = 0;
            while ((i < paramAnonymousList.size()) && (i < paramAnonymousList1.size()))
            {
              ConnectionService.-get6(ConnectionService.this).addConnectionService((ComponentName)paramAnonymousList.get(i), IConnectionService.Stub.asInterface((IBinder)paramAnonymousList1.get(i)));
              i += 1;
            }
            ConnectionService.-wrap12(ConnectionService.this);
            Log.d(this, "remote connection services found: " + paramAnonymousList1, new Object[0]);
          }
        });
      }
    });
  }
  
  private void onCallAudioStateChanged(String paramString, CallAudioState paramCallAudioState)
  {
    Log.d(this, "onAudioStateChanged %s %s", new Object[] { paramString, paramCallAudioState });
    if (this.mConnectionById.containsKey(paramString))
    {
      findConnectionForAction(paramString, "onCallAudioStateChanged").setCallAudioState(paramCallAudioState);
      return;
    }
    findConferenceForAction(paramString, "onCallAudioStateChanged").setCallAudioState(paramCallAudioState);
  }
  
  private void onPostDialContinue(String paramString, boolean paramBoolean)
  {
    Log.d(this, "onPostDialContinue(%s)", new Object[] { paramString });
    findConnectionForAction(paramString, "stopDtmfTone").onPostDialContinue(paramBoolean);
  }
  
  private void playDtmfTone(String paramString, char paramChar)
  {
    Log.d(this, "playDtmfTone %s %c", new Object[] { paramString, Character.valueOf(paramChar) });
    if (this.mConnectionById.containsKey(paramString))
    {
      findConnectionForAction(paramString, "playDtmfTone").onPlayDtmfTone(paramChar);
      return;
    }
    findConferenceForAction(paramString, "playDtmfTone").onPlayDtmfTone(paramChar);
  }
  
  private void pullExternalCall(String paramString)
  {
    Log.d(this, "pullExternalCall(%s)", new Object[] { paramString });
    paramString = findConnectionForAction(paramString, "pullExternalCall");
    if (paramString != null) {
      paramString.onPullExternalCall();
    }
  }
  
  private void reject(String paramString)
  {
    Log.d(this, "reject %s", new Object[] { paramString });
    findConnectionForAction(paramString, "reject").onReject();
  }
  
  private void reject(String paramString1, String paramString2)
  {
    Log.d(this, "reject %s with message", new Object[] { paramString1 });
    findConnectionForAction(paramString1, "reject").onReject(paramString2);
  }
  
  private void removeConference(Conference paramConference)
  {
    if (this.mIdByConference.containsKey(paramConference))
    {
      paramConference.removeListener(this.mConferenceListener);
      String str = (String)this.mIdByConference.get(paramConference);
      this.mConferenceById.remove(str);
      this.mIdByConference.remove(paramConference);
      this.mAdapter.removeCall(str);
    }
  }
  
  private void sendCallEvent(String paramString1, String paramString2, Bundle paramBundle)
  {
    Log.d(this, "sendCallEvent(%s, %s)", new Object[] { paramString1, paramString2 });
    paramString1 = findConnectionForAction(paramString1, "sendCallEvent");
    if (paramString1 != null) {
      paramString1.onCallEvent(paramString2, paramBundle);
    }
  }
  
  private void silence(String paramString)
  {
    Log.d(this, "silence %s", new Object[] { paramString });
    findConnectionForAction(paramString, "silence").onSilence();
  }
  
  private void splitFromConference(String paramString)
  {
    Log.d(this, "splitFromConference(%s)", new Object[] { paramString });
    Connection localConnection = findConnectionForAction(paramString, "splitFromConference");
    if (localConnection == getNullConnection())
    {
      Log.w(this, "Connection missing in conference request %s.", new Object[] { paramString });
      return;
    }
    paramString = localConnection.getConference();
    if (paramString != null) {
      paramString.onSeparate(localConnection);
    }
  }
  
  private void stopDtmfTone(String paramString)
  {
    Log.d(this, "stopDtmfTone %s", new Object[] { paramString });
    if (this.mConnectionById.containsKey(paramString))
    {
      findConnectionForAction(paramString, "stopDtmfTone").onStopDtmfTone();
      return;
    }
    findConferenceForAction(paramString, "stopDtmfTone").onStopDtmfTone();
  }
  
  private void swapConference(String paramString)
  {
    Log.d(this, "swapConference(%s)", new Object[] { paramString });
    paramString = findConferenceForAction(paramString, "swapConference");
    if (paramString != null) {
      paramString.onSwap();
    }
  }
  
  private void unhold(String paramString)
  {
    Log.d(this, "unhold %s", new Object[] { paramString });
    if (this.mConnectionById.containsKey(paramString))
    {
      findConnectionForAction(paramString, "unhold").onUnhold();
      return;
    }
    findConferenceForAction(paramString, "unhold").onUnhold();
  }
  
  public final void addConference(Conference paramConference)
  {
    Log.d(this, "addConference: conference=%s", new Object[] { paramConference });
    String str = addConferenceInternal(paramConference);
    if (str != null)
    {
      ArrayList localArrayList = new ArrayList(2);
      Object localObject1 = paramConference.getConnections().iterator();
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (Connection)((Iterator)localObject1).next();
        if (this.mIdByConnection.containsKey(localObject2)) {
          localArrayList.add((String)this.mIdByConnection.get(localObject2));
        }
      }
      paramConference.setTelecomCallId(str);
      Object localObject2 = paramConference.getPhoneAccountHandle();
      int i = paramConference.getState();
      int j = paramConference.getConnectionCapabilities();
      int k = paramConference.getConnectionProperties();
      if (paramConference.getVideoProvider() == null) {}
      for (localObject1 = null;; localObject1 = paramConference.getVideoProvider().getInterface())
      {
        localObject1 = new ParcelableConference((PhoneAccountHandle)localObject2, i, j, k, localArrayList, (IVideoProvider)localObject1, paramConference.getVideoState(), paramConference.getConnectTimeMillis(), paramConference.getStatusHints(), paramConference.getExtras());
        this.mAdapter.addConferenceCall(str, (ParcelableConference)localObject1);
        this.mAdapter.setVideoProvider(str, paramConference.getVideoProvider());
        this.mAdapter.setVideoState(str, paramConference.getVideoState());
        paramConference = paramConference.getConnections().iterator();
        while (paramConference.hasNext())
        {
          localObject1 = (Connection)paramConference.next();
          localObject1 = (String)this.mIdByConnection.get(localObject1);
          if (localObject1 != null) {
            this.mAdapter.setIsConferenced((String)localObject1, str);
          }
        }
      }
    }
  }
  
  public final void addExistingConnection(PhoneAccountHandle paramPhoneAccountHandle, Connection paramConnection)
  {
    String str1 = addExistingConnectionInternal(paramPhoneAccountHandle, paramConnection);
    ArrayList localArrayList;
    int i;
    int j;
    int k;
    Uri localUri;
    int m;
    String str2;
    int n;
    if (str1 != null)
    {
      localArrayList = new ArrayList(0);
      i = paramConnection.getState();
      j = paramConnection.getConnectionCapabilities();
      k = paramConnection.getConnectionProperties();
      localUri = paramConnection.getAddress();
      m = paramConnection.getAddressPresentation();
      str2 = paramConnection.getCallerDisplayName();
      n = paramConnection.getCallerDisplayNamePresentation();
      if (paramConnection.getVideoProvider() != null) {
        break label139;
      }
    }
    label139:
    for (IVideoProvider localIVideoProvider = null;; localIVideoProvider = paramConnection.getVideoProvider().getInterface())
    {
      paramPhoneAccountHandle = new ParcelableConnection(paramPhoneAccountHandle, i, j, k, localUri, m, str2, n, localIVideoProvider, paramConnection.getVideoState(), paramConnection.isRingbackRequested(), paramConnection.getAudioModeIsVoip(), paramConnection.getConnectTimeMillis(), paramConnection.getStatusHints(), paramConnection.getDisconnectCause(), localArrayList, paramConnection.getExtras());
      this.mAdapter.addExistingConnection(str1, paramPhoneAccountHandle);
      return;
    }
  }
  
  void addRemoteConference(RemoteConference paramRemoteConference)
  {
    onRemoteConferenceAdded(paramRemoteConference);
  }
  
  void addRemoteExistingConnection(RemoteConnection paramRemoteConnection)
  {
    onRemoteExistingConnectionAdded(paramRemoteConnection);
  }
  
  public final void conferenceRemoteConnections(RemoteConnection paramRemoteConnection1, RemoteConnection paramRemoteConnection2)
  {
    this.mRemoteConnectionManager.conferenceRemoteConnections(paramRemoteConnection1, paramRemoteConnection2);
  }
  
  public boolean containsConference(Conference paramConference)
  {
    return this.mIdByConference.containsKey(paramConference);
  }
  
  public final RemoteConnection createRemoteIncomingConnection(PhoneAccountHandle paramPhoneAccountHandle, ConnectionRequest paramConnectionRequest)
  {
    return this.mRemoteConnectionManager.createRemoteConnection(paramPhoneAccountHandle, paramConnectionRequest, true);
  }
  
  public final RemoteConnection createRemoteOutgoingConnection(PhoneAccountHandle paramPhoneAccountHandle, ConnectionRequest paramConnectionRequest)
  {
    return this.mRemoteConnectionManager.createRemoteConnection(paramPhoneAccountHandle, paramConnectionRequest, false);
  }
  
  public final Collection<Conference> getAllConferences()
  {
    return this.mConferenceById.values();
  }
  
  public final Collection<Connection> getAllConnections()
  {
    return this.mConnectionById.values();
  }
  
  public void onAddParticipant(Connection paramConnection, String paramString) {}
  
  public final IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }
  
  public void onConference(Connection paramConnection1, Connection paramConnection2) {}
  
  public Connection onCreateIncomingConnection(PhoneAccountHandle paramPhoneAccountHandle, ConnectionRequest paramConnectionRequest)
  {
    return null;
  }
  
  public Connection onCreateOutgoingConnection(PhoneAccountHandle paramPhoneAccountHandle, ConnectionRequest paramConnectionRequest)
  {
    return null;
  }
  
  public Connection onCreateUnknownConnection(PhoneAccountHandle paramPhoneAccountHandle, ConnectionRequest paramConnectionRequest)
  {
    return null;
  }
  
  public void onRemoteConferenceAdded(RemoteConference paramRemoteConference) {}
  
  public void onRemoteExistingConnectionAdded(RemoteConnection paramRemoteConnection) {}
  
  public boolean onUnbind(Intent paramIntent)
  {
    endAllConnections();
    return super.onUnbind(paramIntent);
  }
  
  protected void removeConnection(Connection paramConnection)
  {
    paramConnection.unsetConnectionService(this);
    paramConnection.removeConnectionListener(this.mConnectionListener);
    String str = (String)this.mIdByConnection.get(paramConnection);
    if (str != null)
    {
      this.mConnectionById.remove(str);
      this.mIdByConnection.remove(paramConnection);
      this.mAdapter.removeCall(str);
    }
  }
  
  public void triggerConferenceRecalculate() {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/ConnectionService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */