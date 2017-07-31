package android.telecom;

import android.os.Bundle;
import android.util.ArraySet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Conference
  extends Conferenceable
{
  public static final long CONNECT_TIME_NOT_SPECIFIED = 0L;
  private CallAudioState mCallAudioState;
  private final List<Connection> mChildConnections = new CopyOnWriteArrayList();
  private final List<Connection> mConferenceableConnections = new ArrayList();
  private long mConnectTimeMillis = 0L;
  private int mConnectionCapabilities;
  private final Connection.Listener mConnectionDeathListener = new Connection.Listener()
  {
    public void onDestroyed(Connection paramAnonymousConnection)
    {
      if (Conference.-get0(Conference.this).remove(paramAnonymousConnection)) {
        Conference.-wrap0(Conference.this);
      }
    }
  };
  private int mConnectionProperties;
  private DisconnectCause mDisconnectCause;
  private String mDisconnectMessage;
  private Bundle mExtras;
  private final Object mExtrasLock = new Object();
  private final Set<Listener> mListeners = new CopyOnWriteArraySet();
  private PhoneAccountHandle mPhoneAccount;
  private Set<String> mPreviousExtraKeys;
  private int mState = 1;
  private StatusHints mStatusHints;
  private String mTelecomCallId;
  private final List<Connection> mUnmodifiableChildConnections = Collections.unmodifiableList(this.mChildConnections);
  private final List<Connection> mUnmodifiableConferenceableConnections = Collections.unmodifiableList(this.mConferenceableConnections);
  
  public Conference(PhoneAccountHandle paramPhoneAccountHandle)
  {
    this.mPhoneAccount = paramPhoneAccountHandle;
  }
  
  public static boolean can(int paramInt1, int paramInt2)
  {
    boolean bool = false;
    if ((paramInt1 & paramInt2) != 0) {
      bool = true;
    }
    return bool;
  }
  
  private final void clearConferenceableList()
  {
    Iterator localIterator = this.mConferenceableConnections.iterator();
    while (localIterator.hasNext()) {
      ((Connection)localIterator.next()).removeConnectionListener(this.mConnectionDeathListener);
    }
    this.mConferenceableConnections.clear();
  }
  
  private final void fireOnConferenceableConnectionsChanged()
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onConferenceableConnectionsChanged(this, getConferenceableConnections());
    }
  }
  
  private void setState(int paramInt)
  {
    if ((paramInt != 4) && (paramInt != 5) && (paramInt != 6))
    {
      Log.w(this, "Unsupported state transition for Conference call.", new Object[] { Connection.stateToString(paramInt) });
      return;
    }
    if (this.mState != paramInt)
    {
      int i = this.mState;
      this.mState = paramInt;
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext()) {
        ((Listener)localIterator.next()).onStateChanged(this, i, paramInt);
      }
    }
  }
  
  public void addCapability(int paramInt)
  {
    setConnectionCapabilities(this.mConnectionCapabilities | paramInt);
  }
  
  public final boolean addConnection(Connection paramConnection)
  {
    Log.d(this, "Connection=%s, connection=", new Object[] { paramConnection });
    if ((paramConnection == null) || (this.mChildConnections.contains(paramConnection))) {}
    while (!paramConnection.setConference(this)) {
      return false;
    }
    this.mChildConnections.add(paramConnection);
    onConnectionAdded(paramConnection);
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onConnectionAdded(this, paramConnection);
    }
    return true;
  }
  
  public final Conference addListener(Listener paramListener)
  {
    this.mListeners.add(paramListener);
    return this;
  }
  
  public boolean can(int paramInt)
  {
    return can(this.mConnectionCapabilities, paramInt);
  }
  
  public final void destroy()
  {
    Log.d(this, "destroying conference : %s", new Object[] { this });
    Iterator localIterator = this.mChildConnections.iterator();
    while (localIterator.hasNext())
    {
      Connection localConnection = (Connection)localIterator.next();
      Log.d(this, "removing connection %s", new Object[] { localConnection });
      removeConnection(localConnection);
    }
    if (this.mState != 6)
    {
      Log.d(this, "setting to disconnected", new Object[0]);
      setDisconnected(new DisconnectCause(2));
    }
    localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onDestroyed(this);
    }
  }
  
  @Deprecated
  public final AudioState getAudioState()
  {
    return new AudioState(this.mCallAudioState);
  }
  
  public final CallAudioState getCallAudioState()
  {
    return this.mCallAudioState;
  }
  
  public final List<Connection> getConferenceableConnections()
  {
    return this.mUnmodifiableConferenceableConnections;
  }
  
  @Deprecated
  public final long getConnectTimeMillis()
  {
    return getConnectionTime();
  }
  
  public final int getConnectionCapabilities()
  {
    return this.mConnectionCapabilities;
  }
  
  public final int getConnectionProperties()
  {
    return this.mConnectionProperties;
  }
  
  public final long getConnectionTime()
  {
    return this.mConnectTimeMillis;
  }
  
  public final List<Connection> getConnections()
  {
    return this.mUnmodifiableChildConnections;
  }
  
  public final DisconnectCause getDisconnectCause()
  {
    return this.mDisconnectCause;
  }
  
  public final Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public final PhoneAccountHandle getPhoneAccountHandle()
  {
    return this.mPhoneAccount;
  }
  
  public Connection getPrimaryConnection()
  {
    if ((this.mUnmodifiableChildConnections == null) || (this.mUnmodifiableChildConnections.isEmpty())) {
      return null;
    }
    return (Connection)this.mUnmodifiableChildConnections.get(0);
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
  
  public Connection.VideoProvider getVideoProvider()
  {
    return null;
  }
  
  public int getVideoState()
  {
    return 0;
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
  
  public void onAddParticipant(String paramString) {}
  
  @Deprecated
  public void onAudioStateChanged(AudioState paramAudioState) {}
  
  public void onCallAudioStateChanged(CallAudioState paramCallAudioState) {}
  
  public void onConnectionAdded(Connection paramConnection) {}
  
  public void onDisconnect() {}
  
  public void onExtrasChanged(Bundle paramBundle) {}
  
  public void onHold() {}
  
  public void onMerge() {}
  
  public void onMerge(Connection paramConnection) {}
  
  public void onPlayDtmfTone(char paramChar) {}
  
  public void onSeparate(Connection paramConnection) {}
  
  public void onStopDtmfTone() {}
  
  public void onSwap() {}
  
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
    setConnectionCapabilities(this.mConnectionCapabilities & paramInt);
  }
  
  public final void removeConnection(Connection paramConnection)
  {
    Log.d(this, "removing %s from %s", new Object[] { paramConnection, this.mChildConnections });
    if ((paramConnection != null) && (this.mChildConnections.remove(paramConnection)))
    {
      paramConnection.resetConference();
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext()) {
        ((Listener)localIterator.next()).onConnectionRemoved(this, paramConnection);
      }
    }
  }
  
  public final void removeExtras(List<String> paramList)
  {
    if ((paramList == null) || (paramList.isEmpty())) {
      return;
    }
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
  
  public final Conference removeListener(Listener paramListener)
  {
    this.mListeners.remove(paramListener);
    return this;
  }
  
  public final void setActive()
  {
    setState(4);
  }
  
  final void setCallAudioState(CallAudioState paramCallAudioState)
  {
    Log.d(this, "setCallAudioState %s", new Object[] { paramCallAudioState });
    this.mCallAudioState = paramCallAudioState;
    onAudioStateChanged(getAudioState());
    onCallAudioStateChanged(paramCallAudioState);
  }
  
  public final void setConferenceableConnections(List<Connection> paramList)
  {
    clearConferenceableList();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Connection localConnection = (Connection)paramList.next();
      if (!this.mConferenceableConnections.contains(localConnection))
      {
        localConnection.addConnectionListener(this.mConnectionDeathListener);
        this.mConferenceableConnections.add(localConnection);
      }
    }
    fireOnConferenceableConnectionsChanged();
  }
  
  @Deprecated
  public final void setConnectTimeMillis(long paramLong)
  {
    setConnectionTime(paramLong);
  }
  
  public final void setConnectionCapabilities(int paramInt)
  {
    if (paramInt != this.mConnectionCapabilities)
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
    if (paramInt != this.mConnectionProperties)
    {
      this.mConnectionProperties = paramInt;
      Iterator localIterator = this.mListeners.iterator();
      while (localIterator.hasNext()) {
        ((Listener)localIterator.next()).onConnectionPropertiesChanged(this, this.mConnectionProperties);
      }
    }
  }
  
  public final void setConnectionTime(long paramLong)
  {
    this.mConnectTimeMillis = paramLong;
  }
  
  public final void setDialing()
  {
    setState(3);
  }
  
  public final void setDisconnected(DisconnectCause paramDisconnectCause)
  {
    this.mDisconnectCause = paramDisconnectCause;
    setState(6);
    paramDisconnectCause = this.mListeners.iterator();
    while (paramDisconnectCause.hasNext()) {
      ((Listener)paramDisconnectCause.next()).onDisconnected(this, this.mDisconnectCause);
    }
  }
  
  public final void setExtras(Bundle paramBundle)
  {
    ArrayList localArrayList;
    synchronized (this.mExtrasLock)
    {
      putExtras(paramBundle);
      if (this.mPreviousExtraKeys == null) {
        break label104;
      }
      localArrayList = new ArrayList();
      Iterator localIterator = this.mPreviousExtraKeys.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        if ((paramBundle == null) || (!paramBundle.containsKey(str))) {
          localArrayList.add(str);
        }
      }
    }
    if (!localArrayList.isEmpty()) {
      removeExtras(localArrayList);
    }
    label104:
    if (this.mPreviousExtraKeys == null) {
      this.mPreviousExtraKeys = new ArraySet();
    }
    this.mPreviousExtraKeys.clear();
    if (paramBundle != null) {
      this.mPreviousExtraKeys.addAll(paramBundle.keySet());
    }
  }
  
  public final void setOnHold()
  {
    setState(5);
  }
  
  public final void setStatusHints(StatusHints paramStatusHints)
  {
    this.mStatusHints = paramStatusHints;
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onStatusHintsChanged(this, paramStatusHints);
    }
  }
  
  public final void setTelecomCallId(String paramString)
  {
    this.mTelecomCallId = paramString;
  }
  
  public final void setVideoProvider(Connection paramConnection, Connection.VideoProvider paramVideoProvider)
  {
    Log.d(this, "setVideoProvider Conference: %s Connection: %s VideoState: %s", new Object[] { this, paramConnection, paramVideoProvider });
    paramConnection = this.mListeners.iterator();
    while (paramConnection.hasNext()) {
      ((Listener)paramConnection.next()).onVideoProviderChanged(this, paramVideoProvider);
    }
  }
  
  public final void setVideoState(Connection paramConnection, int paramInt)
  {
    Log.d(this, "setVideoState Conference: %s Connection: %s VideoState: %s", new Object[] { this, paramConnection, Integer.valueOf(paramInt) });
    paramConnection = this.mListeners.iterator();
    while (paramConnection.hasNext()) {
      ((Listener)paramConnection.next()).onVideoStateChanged(this, paramInt);
    }
  }
  
  public String toString()
  {
    return String.format(Locale.US, "[State: %s,Capabilites: %s, VideoState: %s, VideoProvider: %s, ThisObject %s]", new Object[] { Connection.stateToString(this.mState), Call.Details.capabilitiesToString(this.mConnectionCapabilities), Integer.valueOf(getVideoState()), getVideoProvider(), super.toString() });
  }
  
  public final void updateMergeConferenceFailed()
  {
    Iterator localIterator = this.mListeners.iterator();
    while (localIterator.hasNext()) {
      ((Listener)localIterator.next()).onConferenceMergeFailed(this);
    }
  }
  
  public static abstract class Listener
  {
    public void onConferenceMergeFailed(Conference paramConference) {}
    
    public void onConferenceableConnectionsChanged(Conference paramConference, List<Connection> paramList) {}
    
    public void onConnectionAdded(Conference paramConference, Connection paramConnection) {}
    
    public void onConnectionCapabilitiesChanged(Conference paramConference, int paramInt) {}
    
    public void onConnectionPropertiesChanged(Conference paramConference, int paramInt) {}
    
    public void onConnectionRemoved(Conference paramConference, Connection paramConnection) {}
    
    public void onDestroyed(Conference paramConference) {}
    
    public void onDisconnected(Conference paramConference, DisconnectCause paramDisconnectCause) {}
    
    public void onExtrasChanged(Conference paramConference, Bundle paramBundle) {}
    
    public void onExtrasRemoved(Conference paramConference, List<String> paramList) {}
    
    public void onStateChanged(Conference paramConference, int paramInt1, int paramInt2) {}
    
    public void onStatusHintsChanged(Conference paramConference, StatusHints paramStatusHints) {}
    
    public void onVideoProviderChanged(Conference paramConference, Connection.VideoProvider paramVideoProvider) {}
    
    public void onVideoStateChanged(Conference paramConference, int paramInt) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/Conference.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */