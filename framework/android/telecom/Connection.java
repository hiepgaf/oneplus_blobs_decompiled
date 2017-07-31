package android.telecom;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.ArraySet;
import android.view.Surface;
import com.android.internal.os.SomeArgs;
import com.android.internal.telecom.IVideoCallback;
import com.android.internal.telecom.IVideoCallback.Stub;
import com.android.internal.telecom.IVideoProvider;
import com.android.internal.telecom.IVideoProvider.Stub;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Connection
  extends Conferenceable
{
  public static final int CAPABILITY_ADD_PARTICIPANT = 67108864;
  public static final int CAPABILITY_CANNOT_DOWNGRADE_VIDEO_TO_AUDIO = 8388608;
  public static final int CAPABILITY_CAN_PAUSE_VIDEO = 1048576;
  public static final int CAPABILITY_CAN_PULL_CALL = 16777216;
  public static final int CAPABILITY_CAN_SEND_RESPONSE_VIA_CONNECTION = 4194304;
  public static final int CAPABILITY_CAN_UPGRADE_TO_VIDEO = 524288;
  public static final int CAPABILITY_CONFERENCE_HAS_NO_CHILDREN = 2097152;
  public static final int CAPABILITY_DISCONNECT_FROM_CONFERENCE = 8192;
  public static final int CAPABILITY_HOLD = 1;
  public static final int CAPABILITY_MANAGE_CONFERENCE = 128;
  public static final int CAPABILITY_MERGE_CONFERENCE = 4;
  public static final int CAPABILITY_MUTE = 64;
  public static final int CAPABILITY_RESPOND_VIA_TEXT = 32;
  public static final int CAPABILITY_SEPARATE_FROM_CONFERENCE = 4096;
  public static final int CAPABILITY_SPEED_UP_MT_AUDIO = 262144;
  public static final int CAPABILITY_SUPPORTS_VT_LOCAL_BIDIRECTIONAL = 768;
  public static final int CAPABILITY_SUPPORTS_VT_LOCAL_RX = 256;
  public static final int CAPABILITY_SUPPORTS_VT_LOCAL_TX = 512;
  public static final int CAPABILITY_SUPPORTS_VT_REMOTE_BIDIRECTIONAL = 3072;
  public static final int CAPABILITY_SUPPORTS_VT_REMOTE_RX = 1024;
  public static final int CAPABILITY_SUPPORTS_VT_REMOTE_TX = 2048;
  public static final int CAPABILITY_SUPPORT_HOLD = 2;
  public static final int CAPABILITY_SWAP_CONFERENCE = 8;
  public static final int CAPABILITY_UNUSED = 16;
  public static final int CAPABILITY_UNUSED_2 = 16384;
  public static final int CAPABILITY_UNUSED_3 = 32768;
  public static final int CAPABILITY_UNUSED_4 = 65536;
  public static final int CAPABILITY_UNUSED_5 = 131072;
  public static final int CAPABILITY_VOICE_PRIVACY = 33554432;
  public static final String EVENT_CALL_MERGE_FAILED = "android.telecom.event.CALL_MERGE_FAILED";
  public static final String EVENT_CALL_PULL_FAILED = "android.telecom.event.CALL_PULL_FAILED";
  public static final String EVENT_CALL_REMOTELY_HELD = "android.telecom.event.CALL_REMOTELY_HELD";
  public static final String EVENT_CALL_REMOTELY_UNHELD = "android.telecom.event.CALL_REMOTELY_UNHELD";
  public static final String EVENT_ON_HOLD_TONE_END = "android.telecom.event.ON_HOLD_TONE_END";
  public static final String EVENT_ON_HOLD_TONE_START = "android.telecom.event.ON_HOLD_TONE_START";
  public static final String EXTRA_ANSWERING_DROPS_FG_CALL = "android.telecom.extra.ANSWERING_DROPS_FG_CALL";
  public static final String EXTRA_CALL_SUBJECT = "android.telecom.extra.CALL_SUBJECT";
  public static final String EXTRA_CHILD_ADDRESS = "android.telecom.extra.CHILD_ADDRESS";
  public static final String EXTRA_DISABLE_ADD_CALL = "android.telecom.extra.DISABLE_ADD_CALL";
  public static final String EXTRA_LAST_FORWARDED_NUMBER = "android.telecom.extra.LAST_FORWARDED_NUMBER";
  public static final String EXTRA_ORIGINAL_CONNECTION_ID = "android.telecom.extra.ORIGINAL_CONNECTION_ID";
  private static final boolean PII_DEBUG = Log.isLoggable(3);
  public static final int PROPERTY_EMERGENCY_CALLBACK_MODE = 1;
  public static final int PROPERTY_GENERIC_CONFERENCE = 2;
  public static final int PROPERTY_HAS_CDMA_VOICE_PRIVACY = 32;
  public static final int PROPERTY_HIGH_DEF_AUDIO = 4;
  public static final int PROPERTY_IS_DOWNGRADED_CONFERENCE = 64;
  public static final int PROPERTY_IS_EXTERNAL_CALL = 16;
  public static final int PROPERTY_WIFI = 8;
  public static final int STATE_ACTIVE = 4;
  public static final int STATE_DIALING = 3;
  public static final int STATE_DISCONNECTED = 6;
  public static final int STATE_HOLDING = 5;
  public static final int STATE_INITIALIZING = 0;
  public static final int STATE_NEW = 1;
  public static final int STATE_PULLING_CALL = 7;
  public static final int STATE_RINGING = 2;
  private Uri mAddress;
  private int mAddressPresentation;
  private boolean mAudioModeIsVoip;
  private CallAudioState mCallAudioState;
  private String mCallerDisplayName;
  private int mCallerDisplayNamePresentation;
  private Conference mConference;
  private final Conference.Listener mConferenceDeathListener = new Conference.Listener()
  {
    public void onDestroyed(Conference paramAnonymousConference)
    {
      if (Connection.-get0(Connection.this).remove(paramAnonymousConference)) {
        Connection.-wrap0(Connection.this);
      }
    }
  };
  private final List<Conferenceable> mConferenceables = new ArrayList();
  private long mConnectTimeMillis = 0L;
  private int mConnectionCapabilities;
  private final Listener mConnectionDeathListener = new Listener()
  {
    public void onDestroyed(Connection paramAnonymousConnection)
    {
      if (Connection.-get0(Connection.this).remove(paramAnonymousConnection)) {
        Connection.-wrap0(Connection.this);
      }
    }
  };
  private int mConnectionProperties;
  private ConnectionService mConnectionService;
  private DisconnectCause mDisconnectCause;
  private Bundle mExtras;
  private final Object mExtrasLock = new Object();
  private final Set<Listener> mListeners = Collections.newSetFromMap(new ConcurrentHashMap(8, 0.9F, 1));
  private Set<String> mPreviousExtraKeys;
  private boolean mRingbackRequested = false;
  private int mState = 1;
  private StatusHints mStatusHints;
  private String mTelecomCallId;
  private final List<Conferenceable> mUnmodifiableConferenceables = Collections.unmodifiableList(this.mConferenceables);
  private VideoProvider mVideoProvider;
  private int mVideoState;
  
  public static boolean can(int paramInt1, int paramInt2)
  {
    return (paramInt1 & paramInt2) == paramInt2;
  }
  
  public static String capabilitiesToString(int paramInt)
  {
    return capabilitiesToStringInternal(paramInt, true);
  }
  
  private static String capabilitiesToStringInternal(int paramInt, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[");
    if (paramBoolean) {
      localStringBuilder.append("Capabilities:");
    }
    if (can(paramInt, 1))
    {
      if (paramBoolean)
      {
        str = " CAPABILITY_HOLD";
        localStringBuilder.append(str);
      }
    }
    else
    {
      if (can(paramInt, 2))
      {
        if (!paramBoolean) {
          break label512;
        }
        str = " CAPABILITY_SUPPORT_HOLD";
        label66:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 4))
      {
        if (!paramBoolean) {
          break label519;
        }
        str = " CAPABILITY_MERGE_CONFERENCE";
        label88:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 8))
      {
        if (!paramBoolean) {
          break label526;
        }
        str = " CAPABILITY_SWAP_CONFERENCE";
        label111:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 32))
      {
        if (!paramBoolean) {
          break label533;
        }
        str = " CAPABILITY_RESPOND_VIA_TEXT";
        label134:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 64))
      {
        if (!paramBoolean) {
          break label540;
        }
        str = " CAPABILITY_MUTE";
        label157:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 128))
      {
        if (!paramBoolean) {
          break label547;
        }
        str = " CAPABILITY_MANAGE_CONFERENCE";
        label181:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 256))
      {
        if (!paramBoolean) {
          break label554;
        }
        str = " CAPABILITY_SUPPORTS_VT_LOCAL_RX";
        label205:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 512))
      {
        if (!paramBoolean) {
          break label561;
        }
        str = " CAPABILITY_SUPPORTS_VT_LOCAL_TX";
        label229:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 768))
      {
        if (!paramBoolean) {
          break label568;
        }
        str = " CAPABILITY_SUPPORTS_VT_LOCAL_BIDIRECTIONAL";
        label253:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 1024))
      {
        if (!paramBoolean) {
          break label575;
        }
        str = " CAPABILITY_SUPPORTS_VT_REMOTE_RX";
        label277:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 2048))
      {
        if (!paramBoolean) {
          break label582;
        }
        str = " CAPABILITY_SUPPORTS_VT_REMOTE_TX";
        label301:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 3072))
      {
        if (!paramBoolean) {
          break label589;
        }
        str = " CAPABILITY_SUPPORTS_VT_REMOTE_BIDIRECTIONAL";
        label325:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 8388608))
      {
        if (!paramBoolean) {
          break label596;
        }
        str = " CAPABILITY_CANNOT_DOWNGRADE_VIDEO_TO_AUDIO";
        label348:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 262144))
      {
        if (!paramBoolean) {
          break label603;
        }
        str = " CAPABILITY_SPEED_UP_MT_AUDIO";
        label371:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 524288))
      {
        if (!paramBoolean) {
          break label610;
        }
        str = " CAPABILITY_CAN_UPGRADE_TO_VIDEO";
        label394:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 1048576))
      {
        if (!paramBoolean) {
          break label617;
        }
        str = " CAPABILITY_CAN_PAUSE_VIDEO";
        label417:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 2097152))
      {
        if (!paramBoolean) {
          break label624;
        }
        str = " CAPABILITY_SINGLE_PARTY_CONFERENCE";
        label440:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 4194304))
      {
        if (!paramBoolean) {
          break label631;
        }
        str = " CAPABILITY_CAN_SEND_RESPONSE_VIA_CONNECTION";
        label463:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 16777216)) {
        if (!paramBoolean) {
          break label638;
        }
      }
    }
    label512:
    label519:
    label526:
    label533:
    label540:
    label547:
    label554:
    label561:
    label568:
    label575:
    label582:
    label589:
    label596:
    label603:
    label610:
    label617:
    label624:
    label631:
    label638:
    for (String str = " CAPABILITY_CAN_PULL_CALL";; str = " pull")
    {
      localStringBuilder.append(str);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
      str = " hld";
      break;
      str = " sup_hld";
      break label66;
      str = " mrg_cnf";
      break label88;
      str = " swp_cnf";
      break label111;
      str = " txt";
      break label134;
      str = " mut";
      break label157;
      str = " mng_cnf";
      break label181;
      str = " VTlrx";
      break label205;
      str = " VTltx";
      break label229;
      str = " VTlbi";
      break label253;
      str = " VTrrx";
      break label277;
      str = " VTrtx";
      break label301;
      str = " VTrbi";
      break label325;
      str = " !v2a";
      break label348;
      str = " spd_aud";
      break label371;
      str = " a2v";
      break label394;
      str = " paus_VT";
      break label417;
      str = " 1p_cnf";
      break label440;
      str = " rsp_by_con";
      break label463;
    }
  }
  
  public static String capabilitiesToStringShort(int paramInt)
  {
    return capabilitiesToStringInternal(paramInt, false);
  }
  
  private final void clearConferenceableList()
  {
    Iterator localIterator = this.mConferenceables.iterator();
    while (localIterator.hasNext())
    {
      Conferenceable localConferenceable = (Conferenceable)localIterator.next();
      if ((localConferenceable instanceof Connection)) {
        ((Connection)localConferenceable).removeConnectionListener(this.mConnectionDeathListener);
      } else if ((localConferenceable instanceof Conference)) {
        ((Conference)localConferenceable).removeListener(this.mConferenceDeathListener);
      }
    }
    this.mConferenceables.clear();
  }
  
  public static Connection createCanceledConnection()
  {
    return new FailureSignalingConnection(new DisconnectCause(4));
  }
  
  public static Connection createFailedConnection(DisconnectCause paramDisconnectCause)
  {
    return new FailureSignalingConnection(paramDisconnectCause);
  }
  
  private final void fireConferenceChanged()
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onConferenceChanged(this, this.mConference);
    }
  }
  
  private final void fireOnConferenceableConnectionsChanged()
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onConferenceablesChanged(this, getConferenceables());
    }
  }
  
  public static String propertiesToString(int paramInt)
  {
    return propertiesToStringInternal(paramInt, true);
  }
  
  private static String propertiesToStringInternal(int paramInt, boolean paramBoolean)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("[");
    if (paramBoolean) {
      localStringBuilder.append("Properties:");
    }
    if (can(paramInt, 1))
    {
      if (paramBoolean)
      {
        str = " PROPERTY_EMERGENCY_CALLBACK_MODE";
        localStringBuilder.append(str);
      }
    }
    else
    {
      if (can(paramInt, 4))
      {
        if (!paramBoolean) {
          break label183;
        }
        str = " PROPERTY_HIGH_DEF_AUDIO";
        label66:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 8))
      {
        if (!paramBoolean) {
          break label190;
        }
        str = " PROPERTY_WIFI";
        label89:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 2))
      {
        if (!paramBoolean) {
          break label197;
        }
        str = " PROPERTY_GENERIC_CONFERENCE";
        label111:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 16))
      {
        if (!paramBoolean) {
          break label204;
        }
        str = " PROPERTY_IS_EXTERNAL_CALL";
        label134:
        localStringBuilder.append(str);
      }
      if (can(paramInt, 32)) {
        if (!paramBoolean) {
          break label211;
        }
      }
    }
    label183:
    label190:
    label197:
    label204:
    label211:
    for (String str = " PROPERTY_HAS_CDMA_VOICE_PRIVACY";; str = " priv")
    {
      localStringBuilder.append(str);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
      str = " ecbm";
      break;
      str = " HD";
      break label66;
      str = " wifi";
      break label89;
      str = " gen_conf";
      break label111;
      str = " xtrnl";
      break label134;
    }
  }
  
  public static String propertiesToStringShort(int paramInt)
  {
    return propertiesToStringInternal(paramInt, false);
  }
  
  private void setState(int paramInt)
  {
    checkImmutable();
    if ((this.mState == 6) && (this.mState != paramInt))
    {
      Log.d(this, "Connection already DISCONNECTED; cannot transition out of this state.", new Object[0]);
      return;
    }
    if (this.mState != paramInt)
    {
      Log.d(this, "setState: %s", new Object[] { stateToString(paramInt) });
      this.mState = paramInt;
      onStateChanged(paramInt);
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext()) {
        ((Listener)localIterator.next()).onStateChanged(this, paramInt);
      }
    }
  }
  
  public static String stateToString(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      Log.wtf(Connection.class, "Unknown state %d", new Object[] { Integer.valueOf(paramInt) });
      return "UNKNOWN";
    case 0: 
      return "INITIALIZING";
    case 1: 
      return "NEW";
    case 2: 
      return "RINGING";
    case 3: 
      return "DIALING";
    case 7: 
      return "PULLING_CALL";
    case 4: 
      return "ACTIVE";
    case 5: 
      return "HOLDING";
    }
    return "DISCONNECTED";
  }
  
  static String toLogSafePhoneNumber(String paramString)
  {
    if (paramString == null) {
      return "";
    }
    if (PII_DEBUG) {
      return paramString;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if (i < paramString.length())
    {
      char c = paramString.charAt(i);
      if ((c == '-') || (c == '@')) {
        label52:
        localStringBuilder.append(c);
      }
      for (;;)
      {
        i += 1;
        break;
        if (c == '.') {
          break label52;
        }
        localStringBuilder.append('x');
      }
    }
    return localStringBuilder.toString();
  }
  
  public void addCapability(int paramInt)
  {
    this.mConnectionCapabilities |= paramInt;
  }
  
  public final Connection addConnectionListener(Listener paramListener)
  {
    this.mListeners.add(paramListener);
    return this;
  }
  
  public boolean can(int paramInt)
  {
    return can(this.mConnectionCapabilities, paramInt);
  }
  
  public void checkImmutable() {}
  
  public final void destroy()
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onDestroyed(this);
    }
  }
  
  public final Uri getAddress()
  {
    return this.mAddress;
  }
  
  public final int getAddressPresentation()
  {
    return this.mAddressPresentation;
  }
  
  public final boolean getAudioModeIsVoip()
  {
    return this.mAudioModeIsVoip;
  }
  
  @Deprecated
  public final AudioState getAudioState()
  {
    if (this.mCallAudioState == null) {
      return null;
    }
    return new AudioState(this.mCallAudioState);
  }
  
  public final CallAudioState getCallAudioState()
  {
    return this.mCallAudioState;
  }
  
  public final String getCallerDisplayName()
  {
    return this.mCallerDisplayName;
  }
  
  public final int getCallerDisplayNamePresentation()
  {
    return this.mCallerDisplayNamePresentation;
  }
  
  public final Conference getConference()
  {
    return this.mConference;
  }
  
  public final List<Conferenceable> getConferenceables()
  {
    return this.mUnmodifiableConferenceables;
  }
  
  public final long getConnectTimeMillis()
  {
    return this.mConnectTimeMillis;
  }
  
  public final int getConnectionCapabilities()
  {
    return this.mConnectionCapabilities;
  }
  
  public final int getConnectionProperties()
  {
    return this.mConnectionProperties;
  }
  
  public final ConnectionService getConnectionService()
  {
    return this.mConnectionService;
  }
  
  public final DisconnectCause getDisconnectCause()
  {
    return this.mDisconnectCause;
  }
  
  public final Bundle getExtras()
  {
    Bundle localBundle = null;
    synchronized (this.mExtrasLock)
    {
      if (this.mExtras != null) {
        localBundle = new Bundle(this.mExtras);
      }
      return localBundle;
    }
  }
  
  public final int getState()
  {
    return this.mState;
  }
  
  public final StatusHints getStatusHints()
  {
    return this.mStatusHints;
  }
  
  public final String getTelecomCallId()
  {
    return this.mTelecomCallId;
  }
  
  public final VideoProvider getVideoProvider()
  {
    return this.mVideoProvider;
  }
  
  public final int getVideoState()
  {
    return this.mVideoState;
  }
  
  final void handleExtrasChanged(Bundle paramBundle)
  {
    Object localObject1 = null;
    synchronized (this.mExtrasLock)
    {
      this.mExtras = paramBundle;
      paramBundle = (Bundle)localObject1;
      if (this.mExtras != null) {
        paramBundle = new Bundle(this.mExtras);
      }
      onExtrasChanged(paramBundle);
      return;
    }
  }
  
  public final boolean isRingbackRequested()
  {
    return this.mRingbackRequested;
  }
  
  protected final void notifyConferenceMergeFailed()
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onConferenceMergeFailed(this);
    }
  }
  
  protected void notifyConferenceStarted()
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onConferenceStarted();
    }
  }
  
  protected void notifyConferenceSupportedChanged(boolean paramBoolean)
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onConferenceSupportedChanged(this, paramBoolean);
    }
  }
  
  public void onAbort() {}
  
  public void onAnswer()
  {
    onAnswer(0);
  }
  
  public void onAnswer(int paramInt) {}
  
  @Deprecated
  public void onAudioStateChanged(AudioState paramAudioState) {}
  
  public void onCallAudioStateChanged(CallAudioState paramCallAudioState) {}
  
  public void onCallEvent(String paramString, Bundle paramBundle) {}
  
  public void onDisconnect() {}
  
  public void onDisconnectConferenceParticipant(Uri paramUri) {}
  
  public void onExtrasChanged(Bundle paramBundle) {}
  
  public void onHold() {}
  
  public void onPlayDtmfTone(char paramChar) {}
  
  public void onPostDialContinue(boolean paramBoolean) {}
  
  public void onPullExternalCall() {}
  
  public void onReject() {}
  
  public void onReject(String paramString) {}
  
  public void onSeparate() {}
  
  public void onSilence() {}
  
  public void onStateChanged(int paramInt) {}
  
  public void onStopDtmfTone() {}
  
  public void onUnhold() {}
  
  public final void putExtra(String paramString, int paramInt)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt(paramString, paramInt);
    putExtras(localBundle);
  }
  
  public final void putExtra(String paramString1, String paramString2)
  {
    Bundle localBundle = new Bundle();
    localBundle.putString(paramString1, paramString2);
    putExtras(localBundle);
  }
  
  public final void putExtra(String paramString, boolean paramBoolean)
  {
    Bundle localBundle = new Bundle();
    localBundle.putBoolean(paramString, paramBoolean);
    putExtras(localBundle);
  }
  
  public final void putExtras(Bundle paramBundle)
  {
    checkImmutable();
    if (paramBundle == null) {
      return;
    }
    synchronized (this.mExtrasLock)
    {
      if (this.mExtras == null) {
        this.mExtras = new Bundle();
      }
      this.mExtras.putAll(paramBundle);
      paramBundle = new Bundle(this.mExtras);
      ??? = this.mListeners.iterator();
      if (((Iterator)???).hasNext()) {
        ((Listener)((Iterator)???).next()).onExtrasChanged(this, new Bundle(paramBundle));
      }
    }
  }
  
  public void removeCapability(int paramInt)
  {
    this.mConnectionCapabilities &= paramInt;
  }
  
  public final Connection removeConnectionListener(Listener paramListener)
  {
    if (paramListener != null) {
      this.mListeners.remove(paramListener);
    }
    return this;
  }
  
  public final void removeExtras(List<String> paramList)
  {
    synchronized (this.mExtrasLock)
    {
      if (this.mExtras != null)
      {
        Iterator localIterator = paramList.iterator();
        if (localIterator.hasNext())
        {
          String str = (String)localIterator.next();
          this.mExtras.remove(str);
        }
      }
    }
    paramList = Collections.unmodifiableList(paramList);
    ??? = this.mListeners.iterator();
    while (((Iterator)???).hasNext()) {
      ((Listener)((Iterator)???).next()).onExtrasRemoved(this, paramList);
    }
  }
  
  public final void removeExtras(String... paramVarArgs)
  {
    removeExtras(Arrays.asList(paramVarArgs));
  }
  
  public final void resetConference()
  {
    if (this.mConference != null)
    {
      Log.d(this, "Conference reset", new Object[0]);
      this.mConference = null;
      fireConferenceChanged();
    }
  }
  
  public void sendConnectionEvent(String paramString, Bundle paramBundle)
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onConnectionEvent(this, paramString, paramBundle);
    }
  }
  
  public final void setActive()
  {
    checkImmutable();
    setRingbackRequested(false);
    setState(4);
  }
  
  public final void setAddress(Uri paramUri, int paramInt)
  {
    checkImmutable();
    Log.d(this, "setAddress %s", new Object[] { paramUri });
    this.mAddress = paramUri;
    this.mAddressPresentation = paramInt;
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onAddressChanged(this, paramUri, paramInt);
    }
  }
  
  public final void setAudioModeIsVoip(boolean paramBoolean)
  {
    checkImmutable();
    this.mAudioModeIsVoip = paramBoolean;
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onAudioModeIsVoipChanged(this, paramBoolean);
    }
  }
  
  final void setCallAudioState(CallAudioState paramCallAudioState)
  {
    checkImmutable();
    Log.d(this, "setAudioState %s", new Object[] { paramCallAudioState });
    this.mCallAudioState = paramCallAudioState;
    onAudioStateChanged(getAudioState());
    onCallAudioStateChanged(paramCallAudioState);
  }
  
  public final void setCallerDisplayName(String paramString, int paramInt)
  {
    checkImmutable();
    Log.d(this, "setCallerDisplayName %s", new Object[] { paramString });
    this.mCallerDisplayName = paramString;
    this.mCallerDisplayNamePresentation = paramInt;
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onCallerDisplayNameChanged(this, paramString, paramInt);
    }
  }
  
  public final boolean setConference(Conference paramConference)
  {
    checkImmutable();
    if (this.mConference == null)
    {
      this.mConference = paramConference;
      if ((this.mConnectionService != null) && (this.mConnectionService.containsConference(paramConference))) {
        fireConferenceChanged();
      }
      return true;
    }
    return false;
  }
  
  public final void setConferenceableConnections(List<Connection> paramList)
  {
    checkImmutable();
    clearConferenceableList();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Connection localConnection = (Connection)paramList.next();
      if (!this.mConferenceables.contains(localConnection))
      {
        localConnection.addConnectionListener(this.mConnectionDeathListener);
        this.mConferenceables.add(localConnection);
      }
    }
    fireOnConferenceableConnectionsChanged();
  }
  
  public final void setConferenceables(List<Conferenceable> paramList)
  {
    clearConferenceableList();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Conferenceable localConferenceable = (Conferenceable)paramList.next();
      if (!this.mConferenceables.contains(localConferenceable))
      {
        if ((localConferenceable instanceof Connection)) {
          ((Connection)localConferenceable).addConnectionListener(this.mConnectionDeathListener);
        }
        for (;;)
        {
          this.mConferenceables.add(localConferenceable);
          break;
          if ((localConferenceable instanceof Conference)) {
            ((Conference)localConferenceable).addListener(this.mConferenceDeathListener);
          }
        }
      }
    }
    fireOnConferenceableConnectionsChanged();
  }
  
  public final void setConnectTimeMillis(long paramLong)
  {
    this.mConnectTimeMillis = paramLong;
  }
  
  public final void setConnectionCapabilities(int paramInt)
  {
    checkImmutable();
    if (this.mConnectionCapabilities != paramInt)
    {
      this.mConnectionCapabilities = paramInt;
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext()) {
        ((Listener)localIterator.next()).onConnectionCapabilitiesChanged(this, this.mConnectionCapabilities);
      }
    }
  }
  
  public final void setConnectionProperties(int paramInt)
  {
    checkImmutable();
    if (this.mConnectionProperties != paramInt)
    {
      this.mConnectionProperties = paramInt;
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext()) {
        ((Listener)localIterator.next()).onConnectionPropertiesChanged(this, this.mConnectionProperties);
      }
    }
  }
  
  public final void setConnectionService(ConnectionService paramConnectionService)
  {
    checkImmutable();
    if (this.mConnectionService != null)
    {
      Log.e(this, new Exception(), "Trying to set ConnectionService on a connection which is already associated with another ConnectionService.", new Object[0]);
      return;
    }
    this.mConnectionService = paramConnectionService;
  }
  
  public final void setDialing()
  {
    checkImmutable();
    setState(3);
  }
  
  public final void setDisconnected(DisconnectCause paramDisconnectCause)
  {
    checkImmutable();
    this.mDisconnectCause = paramDisconnectCause;
    setState(6);
    Log.d(this, "Disconnected with cause %s", new Object[] { paramDisconnectCause });
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onDisconnected(this, paramDisconnectCause);
    }
  }
  
  public final void setExtras(Bundle paramBundle)
  {
    checkImmutable();
    putExtras(paramBundle);
    if (this.mPreviousExtraKeys != null)
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = this.mPreviousExtraKeys.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if ((paramBundle == null) || (!paramBundle.containsKey(str))) {
          localArrayList.add(str);
        }
      }
      if (!localArrayList.isEmpty()) {
        removeExtras(localArrayList);
      }
    }
    if (this.mPreviousExtraKeys == null) {
      this.mPreviousExtraKeys = new ArraySet();
    }
    this.mPreviousExtraKeys.clear();
    if (paramBundle != null) {
      this.mPreviousExtraKeys.addAll(paramBundle.keySet());
    }
  }
  
  public final void setInitialized()
  {
    checkImmutable();
    setState(1);
  }
  
  public final void setInitializing()
  {
    checkImmutable();
    setState(0);
  }
  
  public final void setNextPostDialChar(char paramChar)
  {
    checkImmutable();
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onPostDialChar(this, paramChar);
    }
  }
  
  public final void setOnHold()
  {
    checkImmutable();
    setState(5);
  }
  
  public final void setPostDialWait(String paramString)
  {
    checkImmutable();
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onPostDialWait(this, paramString);
    }
  }
  
  public final void setPulling()
  {
    checkImmutable();
    setState(7);
  }
  
  public final void setRingbackRequested(boolean paramBoolean)
  {
    checkImmutable();
    if (this.mRingbackRequested != paramBoolean)
    {
      this.mRingbackRequested = paramBoolean;
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext()) {
        ((Listener)localIterator.next()).onRingbackRequested(this, paramBoolean);
      }
    }
  }
  
  public final void setRinging()
  {
    checkImmutable();
    setState(2);
  }
  
  public final void setStatusHints(StatusHints paramStatusHints)
  {
    checkImmutable();
    this.mStatusHints = paramStatusHints;
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onStatusHintsChanged(this, paramStatusHints);
    }
  }
  
  public void setTelecomCallId(String paramString)
  {
    this.mTelecomCallId = paramString;
  }
  
  public final void setVideoProvider(VideoProvider paramVideoProvider)
  {
    checkImmutable();
    this.mVideoProvider = paramVideoProvider;
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onVideoProviderChanged(this, paramVideoProvider);
    }
  }
  
  public final void setVideoState(int paramInt)
  {
    checkImmutable();
    Log.d(this, "setVideoState %d", new Object[] { Integer.valueOf(paramInt) });
    this.mVideoState = paramInt;
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onVideoStateChanged(this, this.mVideoState);
    }
  }
  
  public final void unsetConnectionService(ConnectionService paramConnectionService)
  {
    if (this.mConnectionService != paramConnectionService)
    {
      Log.e(this, new Exception(), "Trying to remove ConnectionService from a Connection that does not belong to the ConnectionService.", new Object[0]);
      return;
    }
    this.mConnectionService = null;
  }
  
  protected final void updateConferenceParticipants(List<ConferenceParticipant> paramList)
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onConferenceParticipantsChanged(this, paramList);
    }
  }
  
  private static class FailureSignalingConnection
    extends Connection
  {
    private boolean mImmutable = false;
    
    public FailureSignalingConnection(DisconnectCause paramDisconnectCause)
    {
      setDisconnected(paramDisconnectCause);
      this.mImmutable = true;
    }
    
    public void checkImmutable()
    {
      if (this.mImmutable) {
        throw new UnsupportedOperationException("Connection is immutable");
      }
    }
  }
  
  public static abstract class Listener
  {
    public void onAddressChanged(Connection paramConnection, Uri paramUri, int paramInt) {}
    
    public void onAudioModeIsVoipChanged(Connection paramConnection, boolean paramBoolean) {}
    
    public void onCallerDisplayNameChanged(Connection paramConnection, String paramString, int paramInt) {}
    
    public void onConferenceChanged(Connection paramConnection, Conference paramConference) {}
    
    public void onConferenceMergeFailed(Connection paramConnection) {}
    
    public void onConferenceParticipantsChanged(Connection paramConnection, List<ConferenceParticipant> paramList) {}
    
    public void onConferenceStarted() {}
    
    public void onConferenceSupportedChanged(Connection paramConnection, boolean paramBoolean) {}
    
    public void onConferenceablesChanged(Connection paramConnection, List<Conferenceable> paramList) {}
    
    public void onConnectionCapabilitiesChanged(Connection paramConnection, int paramInt) {}
    
    public void onConnectionEvent(Connection paramConnection, String paramString, Bundle paramBundle) {}
    
    public void onConnectionPropertiesChanged(Connection paramConnection, int paramInt) {}
    
    public void onDestroyed(Connection paramConnection) {}
    
    public void onDisconnected(Connection paramConnection, DisconnectCause paramDisconnectCause) {}
    
    public void onExtrasChanged(Connection paramConnection, Bundle paramBundle) {}
    
    public void onExtrasRemoved(Connection paramConnection, List<String> paramList) {}
    
    public void onPostDialChar(Connection paramConnection, char paramChar) {}
    
    public void onPostDialWait(Connection paramConnection, String paramString) {}
    
    public void onRingbackRequested(Connection paramConnection, boolean paramBoolean) {}
    
    public void onStateChanged(Connection paramConnection, int paramInt) {}
    
    public void onStatusHintsChanged(Connection paramConnection, StatusHints paramStatusHints) {}
    
    public void onVideoProviderChanged(Connection paramConnection, Connection.VideoProvider paramVideoProvider) {}
    
    public void onVideoStateChanged(Connection paramConnection, int paramInt) {}
  }
  
  public static abstract class VideoProvider
  {
    private static final int MSG_ADD_VIDEO_CALLBACK = 1;
    private static final int MSG_REMOVE_VIDEO_CALLBACK = 12;
    private static final int MSG_REQUEST_CAMERA_CAPABILITIES = 9;
    private static final int MSG_REQUEST_CONNECTION_DATA_USAGE = 10;
    private static final int MSG_SEND_SESSION_MODIFY_REQUEST = 7;
    private static final int MSG_SEND_SESSION_MODIFY_RESPONSE = 8;
    private static final int MSG_SET_CAMERA = 2;
    private static final int MSG_SET_DEVICE_ORIENTATION = 5;
    private static final int MSG_SET_DISPLAY_SURFACE = 4;
    private static final int MSG_SET_PAUSE_IMAGE = 11;
    private static final int MSG_SET_PREVIEW_SURFACE = 3;
    private static final int MSG_SET_ZOOM = 6;
    public static final int SESSION_EVENT_CAMERA_FAILURE = 5;
    private static final String SESSION_EVENT_CAMERA_FAILURE_STR = "CAMERA_FAIL";
    public static final int SESSION_EVENT_CAMERA_READY = 6;
    private static final String SESSION_EVENT_CAMERA_READY_STR = "CAMERA_READY";
    public static final int SESSION_EVENT_RX_PAUSE = 1;
    private static final String SESSION_EVENT_RX_PAUSE_STR = "RX_PAUSE";
    public static final int SESSION_EVENT_RX_RESUME = 2;
    private static final String SESSION_EVENT_RX_RESUME_STR = "RX_RESUME";
    public static final int SESSION_EVENT_TX_START = 3;
    private static final String SESSION_EVENT_TX_START_STR = "TX_START";
    public static final int SESSION_EVENT_TX_STOP = 4;
    private static final String SESSION_EVENT_TX_STOP_STR = "TX_STOP";
    private static final String SESSION_EVENT_UNKNOWN_STR = "UNKNOWN";
    public static final int SESSION_MODIFY_REQUEST_FAIL = 2;
    public static final int SESSION_MODIFY_REQUEST_INVALID = 3;
    public static final int SESSION_MODIFY_REQUEST_REJECTED_BY_REMOTE = 5;
    public static final int SESSION_MODIFY_REQUEST_SUCCESS = 1;
    public static final int SESSION_MODIFY_REQUEST_TIMED_OUT = 4;
    private final VideoProviderBinder mBinder = new VideoProviderBinder(null);
    private VideoProviderHandler mMessageHandler;
    private ConcurrentHashMap<IBinder, IVideoCallback> mVideoCallbacks = new ConcurrentHashMap(8, 0.9F, 1);
    
    public VideoProvider()
    {
      this.mMessageHandler = new VideoProviderHandler(Looper.getMainLooper());
    }
    
    public VideoProvider(Looper paramLooper)
    {
      this.mMessageHandler = new VideoProviderHandler(paramLooper);
    }
    
    public static String sessionEventToString(int paramInt)
    {
      switch (paramInt)
      {
      default: 
        return "UNKNOWN " + paramInt;
      case 5: 
        return "CAMERA_FAIL";
      case 6: 
        return "CAMERA_READY";
      case 1: 
        return "RX_PAUSE";
      case 2: 
        return "RX_RESUME";
      case 3: 
        return "TX_START";
      }
      return "TX_STOP";
    }
    
    public void changeCallDataUsage(long paramLong)
    {
      setCallDataUsage(paramLong);
    }
    
    public void changeCameraCapabilities(VideoProfile.CameraCapabilities paramCameraCapabilities)
    {
      if (this.mVideoCallbacks != null)
      {
        Iterator localIterator = this.mVideoCallbacks.values().iterator();
        while (localIterator.hasNext())
        {
          IVideoCallback localIVideoCallback = (IVideoCallback)localIterator.next();
          try
          {
            localIVideoCallback.changeCameraCapabilities(paramCameraCapabilities);
          }
          catch (RemoteException localRemoteException)
          {
            Log.w(this, "changeCameraCapabilities callback failed", new Object[] { localRemoteException });
          }
        }
      }
    }
    
    public void changePeerDimensions(int paramInt1, int paramInt2)
    {
      if (this.mVideoCallbacks != null)
      {
        Iterator localIterator = this.mVideoCallbacks.values().iterator();
        while (localIterator.hasNext())
        {
          IVideoCallback localIVideoCallback = (IVideoCallback)localIterator.next();
          try
          {
            localIVideoCallback.changePeerDimensions(paramInt1, paramInt2);
          }
          catch (RemoteException localRemoteException)
          {
            Log.w(this, "changePeerDimensions callback failed", new Object[] { localRemoteException });
          }
        }
      }
    }
    
    public void changeVideoQuality(int paramInt)
    {
      if (this.mVideoCallbacks != null)
      {
        Iterator localIterator = this.mVideoCallbacks.values().iterator();
        while (localIterator.hasNext())
        {
          IVideoCallback localIVideoCallback = (IVideoCallback)localIterator.next();
          try
          {
            localIVideoCallback.changeVideoQuality(paramInt);
          }
          catch (RemoteException localRemoteException)
          {
            Log.w(this, "changeVideoQuality callback failed", new Object[] { localRemoteException });
          }
        }
      }
    }
    
    public final IVideoProvider getInterface()
    {
      return this.mBinder;
    }
    
    public void handleCallSessionEvent(int paramInt)
    {
      if (this.mVideoCallbacks != null)
      {
        Iterator localIterator = this.mVideoCallbacks.values().iterator();
        while (localIterator.hasNext())
        {
          IVideoCallback localIVideoCallback = (IVideoCallback)localIterator.next();
          try
          {
            localIVideoCallback.handleCallSessionEvent(paramInt);
          }
          catch (RemoteException localRemoteException)
          {
            Log.w(this, "handleCallSessionEvent callback failed", new Object[] { localRemoteException });
          }
        }
      }
    }
    
    public abstract void onRequestCameraCapabilities();
    
    public abstract void onRequestConnectionDataUsage();
    
    public abstract void onSendSessionModifyRequest(VideoProfile paramVideoProfile1, VideoProfile paramVideoProfile2);
    
    public abstract void onSendSessionModifyResponse(VideoProfile paramVideoProfile);
    
    public abstract void onSetCamera(String paramString);
    
    public abstract void onSetDeviceOrientation(int paramInt);
    
    public abstract void onSetDisplaySurface(Surface paramSurface);
    
    public abstract void onSetPauseImage(Uri paramUri);
    
    public abstract void onSetPreviewSurface(Surface paramSurface);
    
    public abstract void onSetZoom(float paramFloat);
    
    public void receiveSessionModifyRequest(VideoProfile paramVideoProfile)
    {
      if (this.mVideoCallbacks != null)
      {
        Iterator localIterator = this.mVideoCallbacks.values().iterator();
        while (localIterator.hasNext())
        {
          IVideoCallback localIVideoCallback = (IVideoCallback)localIterator.next();
          try
          {
            localIVideoCallback.receiveSessionModifyRequest(paramVideoProfile);
          }
          catch (RemoteException localRemoteException)
          {
            Log.w(this, "receiveSessionModifyRequest callback failed", new Object[] { localRemoteException });
          }
        }
      }
    }
    
    public void receiveSessionModifyResponse(int paramInt, VideoProfile paramVideoProfile1, VideoProfile paramVideoProfile2)
    {
      if (this.mVideoCallbacks != null)
      {
        Iterator localIterator = this.mVideoCallbacks.values().iterator();
        while (localIterator.hasNext())
        {
          IVideoCallback localIVideoCallback = (IVideoCallback)localIterator.next();
          try
          {
            localIVideoCallback.receiveSessionModifyResponse(paramInt, paramVideoProfile1, paramVideoProfile2);
          }
          catch (RemoteException localRemoteException)
          {
            Log.w(this, "receiveSessionModifyResponse callback failed", new Object[] { localRemoteException });
          }
        }
      }
    }
    
    public void setCallDataUsage(long paramLong)
    {
      if (this.mVideoCallbacks != null)
      {
        Iterator localIterator = this.mVideoCallbacks.values().iterator();
        while (localIterator.hasNext())
        {
          IVideoCallback localIVideoCallback = (IVideoCallback)localIterator.next();
          try
          {
            localIVideoCallback.changeCallDataUsage(paramLong);
          }
          catch (RemoteException localRemoteException)
          {
            Log.w(this, "setCallDataUsage callback failed", new Object[] { localRemoteException });
          }
        }
      }
    }
    
    private final class VideoProviderBinder
      extends IVideoProvider.Stub
    {
      private VideoProviderBinder() {}
      
      public void addVideoCallback(IBinder paramIBinder)
      {
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(1, paramIBinder).sendToTarget();
      }
      
      public void removeVideoCallback(IBinder paramIBinder)
      {
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(12, paramIBinder).sendToTarget();
      }
      
      public void requestCallDataUsage()
      {
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(10).sendToTarget();
      }
      
      public void requestCameraCapabilities()
      {
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(9).sendToTarget();
      }
      
      public void sendSessionModifyRequest(VideoProfile paramVideoProfile1, VideoProfile paramVideoProfile2)
      {
        SomeArgs localSomeArgs = SomeArgs.obtain();
        localSomeArgs.arg1 = paramVideoProfile1;
        localSomeArgs.arg2 = paramVideoProfile2;
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(7, localSomeArgs).sendToTarget();
      }
      
      public void sendSessionModifyResponse(VideoProfile paramVideoProfile)
      {
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(8, paramVideoProfile).sendToTarget();
      }
      
      public void setCamera(String paramString)
      {
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(2, paramString).sendToTarget();
      }
      
      public void setDeviceOrientation(int paramInt)
      {
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(5, paramInt, 0).sendToTarget();
      }
      
      public void setDisplaySurface(Surface paramSurface)
      {
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(4, paramSurface).sendToTarget();
      }
      
      public void setPauseImage(Uri paramUri)
      {
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(11, paramUri).sendToTarget();
      }
      
      public void setPreviewSurface(Surface paramSurface)
      {
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(3, paramSurface).sendToTarget();
      }
      
      public void setZoom(float paramFloat)
      {
        Connection.VideoProvider.-get0(Connection.VideoProvider.this).obtainMessage(6, Float.valueOf(paramFloat)).sendToTarget();
      }
    }
    
    private final class VideoProviderHandler
      extends Handler
    {
      public VideoProviderHandler() {}
      
      public VideoProviderHandler(Looper paramLooper)
      {
        super();
      }
      
      public void handleMessage(Message paramMessage)
      {
        IBinder localIBinder;
        switch (paramMessage.what)
        {
        default: 
          return;
        case 1: 
          localIBinder = (IBinder)paramMessage.obj;
          paramMessage = IVideoCallback.Stub.asInterface((IBinder)paramMessage.obj);
          if (paramMessage == null)
          {
            Log.w(this, "addVideoProvider - skipped; callback is null.", new Object[0]);
            return;
          }
          if (Connection.VideoProvider.-get1(Connection.VideoProvider.this).containsKey(localIBinder))
          {
            Log.i(this, "addVideoProvider - skipped; already present.", new Object[0]);
            return;
          }
          Connection.VideoProvider.-get1(Connection.VideoProvider.this).put(localIBinder, paramMessage);
          return;
        case 12: 
          localIBinder = (IBinder)paramMessage.obj;
          IVideoCallback.Stub.asInterface((IBinder)paramMessage.obj);
          if (!Connection.VideoProvider.-get1(Connection.VideoProvider.this).containsKey(localIBinder))
          {
            Log.i(this, "removeVideoProvider - skipped; not present.", new Object[0]);
            return;
          }
          Connection.VideoProvider.-get1(Connection.VideoProvider.this).remove(localIBinder);
          return;
        case 2: 
          Connection.VideoProvider.this.onSetCamera((String)paramMessage.obj);
          return;
        case 3: 
          Connection.VideoProvider.this.onSetPreviewSurface((Surface)paramMessage.obj);
          return;
        case 4: 
          Connection.VideoProvider.this.onSetDisplaySurface((Surface)paramMessage.obj);
          return;
        case 5: 
          Connection.VideoProvider.this.onSetDeviceOrientation(paramMessage.arg1);
          return;
        case 6: 
          Connection.VideoProvider.this.onSetZoom(((Float)paramMessage.obj).floatValue());
          return;
        case 7: 
          paramMessage = (SomeArgs)paramMessage.obj;
          try
          {
            Connection.VideoProvider.this.onSendSessionModifyRequest((VideoProfile)paramMessage.arg1, (VideoProfile)paramMessage.arg2);
            return;
          }
          finally
          {
            paramMessage.recycle();
          }
        case 8: 
          Connection.VideoProvider.this.onSendSessionModifyResponse((VideoProfile)paramMessage.obj);
          return;
        case 9: 
          Connection.VideoProvider.this.onRequestCameraCapabilities();
          return;
        case 10: 
          Connection.VideoProvider.this.onRequestConnectionDataUsage();
          return;
        }
        Connection.VideoProvider.this.onSetPauseImage((Uri)paramMessage.obj);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/Connection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */