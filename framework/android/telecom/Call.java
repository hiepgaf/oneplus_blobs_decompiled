package android.telecom;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class Call
{
  public static final String AVAILABLE_PHONE_ACCOUNTS = "selectPhoneAccountAccounts";
  public static final int STATE_ACTIVE = 4;
  public static final int STATE_CONNECTING = 9;
  public static final int STATE_DIALING = 1;
  public static final int STATE_DISCONNECTED = 7;
  public static final int STATE_DISCONNECTING = 10;
  public static final int STATE_HOLDING = 3;
  public static final int STATE_NEW = 0;
  @Deprecated
  public static final int STATE_PRE_DIAL_WAIT = 8;
  public static final int STATE_PULLING_CALL = 11;
  public static final int STATE_RINGING = 2;
  public static final int STATE_SELECT_PHONE_ACCOUNT = 8;
  private final List<CallbackRecord<Callback>> mCallbackRecords = new CopyOnWriteArrayList();
  private List<String> mCannedTextResponses = null;
  private final List<Call> mChildren = new ArrayList();
  private boolean mChildrenCached;
  private final List<String> mChildrenIds = new ArrayList();
  private final List<Call> mConferenceableCalls = new ArrayList();
  private Details mDetails;
  private Bundle mExtras;
  private final InCallAdapter mInCallAdapter;
  private String mParentId = null;
  private final Phone mPhone;
  private String mRemainingPostDialSequence;
  private int mState;
  private final String mTelecomCallId;
  private final List<Call> mUnmodifiableChildren = Collections.unmodifiableList(this.mChildren);
  private final List<Call> mUnmodifiableConferenceableCalls = Collections.unmodifiableList(this.mConferenceableCalls);
  private VideoCallImpl mVideoCallImpl;
  
  Call(Phone paramPhone, String paramString, InCallAdapter paramInCallAdapter)
  {
    this.mPhone = paramPhone;
    this.mTelecomCallId = paramString;
    this.mInCallAdapter = paramInCallAdapter;
    this.mState = 0;
  }
  
  Call(Phone paramPhone, String paramString, InCallAdapter paramInCallAdapter, int paramInt)
  {
    this.mPhone = paramPhone;
    this.mTelecomCallId = paramString;
    this.mInCallAdapter = paramInCallAdapter;
    this.mState = paramInt;
  }
  
  private static boolean areBundlesEqual(Bundle paramBundle1, Bundle paramBundle2)
  {
    if ((paramBundle1 == null) || (paramBundle2 == null)) {
      return paramBundle1 == paramBundle2;
    }
    if (paramBundle1.size() != paramBundle2.size()) {
      return false;
    }
    Iterator localIterator = paramBundle1.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if ((str != null) && (!Objects.equals(paramBundle1.get(str), paramBundle2.get(str)))) {
        return false;
      }
    }
    return true;
  }
  
  private void fireCallDestroyed()
  {
    if (this.mCallbackRecords.isEmpty()) {
      this.mPhone.internalRemoveCall(this);
    }
    Iterator localIterator = this.mCallbackRecords.iterator();
    while (localIterator.hasNext())
    {
      final CallbackRecord localCallbackRecord = (CallbackRecord)localIterator.next();
      final Callback localCallback = (Callback)localCallbackRecord.getCallback();
      localCallbackRecord.getHandler().post(new Runnable()
      {
        public void run()
        {
          int i = 0;
          Object localObject1 = null;
          try
          {
            localCallback.onCallDestroyed(jdField_this);
            boolean bool;
            return;
          }
          catch (RuntimeException localRuntimeException)
          {
            synchronized (Call.this)
            {
              Call.-get0(Call.this).remove(localCallbackRecord);
              bool = Call.-get0(Call.this).isEmpty();
              if (bool) {
                i = 1;
              }
              if (i != 0) {
                Call.-get1(Call.this).internalRemoveCall(jdField_this);
              }
              if (localObject1 != null)
              {
                throw ((Throwable)localObject1);
                localRuntimeException = localRuntimeException;
              }
            }
          }
        }
      });
    }
  }
  
  private void fireCannedTextResponsesLoaded(final List<String> paramList)
  {
    Iterator localIterator = this.mCallbackRecords.iterator();
    while (localIterator.hasNext())
    {
      CallbackRecord localCallbackRecord = (CallbackRecord)localIterator.next();
      final Callback localCallback = (Callback)localCallbackRecord.getCallback();
      localCallbackRecord.getHandler().post(new Runnable()
      {
        public void run()
        {
          localCallback.onCannedTextResponsesLoaded(jdField_this, paramList);
        }
      });
    }
  }
  
  private void fireChildrenChanged(final List<Call> paramList)
  {
    Iterator localIterator = this.mCallbackRecords.iterator();
    while (localIterator.hasNext())
    {
      CallbackRecord localCallbackRecord = (CallbackRecord)localIterator.next();
      final Callback localCallback = (Callback)localCallbackRecord.getCallback();
      localCallbackRecord.getHandler().post(new Runnable()
      {
        public void run()
        {
          localCallback.onChildrenChanged(jdField_this, paramList);
        }
      });
    }
  }
  
  private void fireConferenceableCallsChanged()
  {
    Iterator localIterator = this.mCallbackRecords.iterator();
    while (localIterator.hasNext())
    {
      CallbackRecord localCallbackRecord = (CallbackRecord)localIterator.next();
      final Callback localCallback = (Callback)localCallbackRecord.getCallback();
      localCallbackRecord.getHandler().post(new Runnable()
      {
        public void run()
        {
          localCallback.onConferenceableCallsChanged(jdField_this, Call.-get2(Call.this));
        }
      });
    }
  }
  
  private void fireDetailsChanged(final Details paramDetails)
  {
    Iterator localIterator = this.mCallbackRecords.iterator();
    while (localIterator.hasNext())
    {
      CallbackRecord localCallbackRecord = (CallbackRecord)localIterator.next();
      final Callback localCallback = (Callback)localCallbackRecord.getCallback();
      localCallbackRecord.getHandler().post(new Runnable()
      {
        public void run()
        {
          localCallback.onDetailsChanged(jdField_this, paramDetails);
        }
      });
    }
  }
  
  private void fireOnConnectionEvent(final String paramString, final Bundle paramBundle)
  {
    Iterator localIterator = this.mCallbackRecords.iterator();
    while (localIterator.hasNext())
    {
      CallbackRecord localCallbackRecord = (CallbackRecord)localIterator.next();
      final Callback localCallback = (Callback)localCallbackRecord.getCallback();
      localCallbackRecord.getHandler().post(new Runnable()
      {
        public void run()
        {
          localCallback.onConnectionEvent(jdField_this, paramString, paramBundle);
        }
      });
    }
  }
  
  private void fireParentChanged(final Call paramCall)
  {
    Iterator localIterator = this.mCallbackRecords.iterator();
    while (localIterator.hasNext())
    {
      CallbackRecord localCallbackRecord = (CallbackRecord)localIterator.next();
      final Callback localCallback = (Callback)localCallbackRecord.getCallback();
      localCallbackRecord.getHandler().post(new Runnable()
      {
        public void run()
        {
          localCallback.onParentChanged(jdField_this, paramCall);
        }
      });
    }
  }
  
  private void firePostDialWait(final String paramString)
  {
    Iterator localIterator = this.mCallbackRecords.iterator();
    while (localIterator.hasNext())
    {
      CallbackRecord localCallbackRecord = (CallbackRecord)localIterator.next();
      final Callback localCallback = (Callback)localCallbackRecord.getCallback();
      localCallbackRecord.getHandler().post(new Runnable()
      {
        public void run()
        {
          localCallback.onPostDialWait(jdField_this, paramString);
        }
      });
    }
  }
  
  private void fireStateChanged(final int paramInt)
  {
    Iterator localIterator = this.mCallbackRecords.iterator();
    while (localIterator.hasNext())
    {
      CallbackRecord localCallbackRecord = (CallbackRecord)localIterator.next();
      final Callback localCallback = (Callback)localCallbackRecord.getCallback();
      localCallbackRecord.getHandler().post(new Runnable()
      {
        public void run()
        {
          localCallback.onStateChanged(jdField_this, paramInt);
        }
      });
    }
  }
  
  private void fireVideoCallChanged(final InCallService.VideoCall paramVideoCall)
  {
    Iterator localIterator = this.mCallbackRecords.iterator();
    while (localIterator.hasNext())
    {
      CallbackRecord localCallbackRecord = (CallbackRecord)localIterator.next();
      final Callback localCallback = (Callback)localCallbackRecord.getCallback();
      localCallbackRecord.getHandler().post(new Runnable()
      {
        public void run()
        {
          localCallback.onVideoCallChanged(jdField_this, paramVideoCall);
        }
      });
    }
  }
  
  private static String stateToString(int paramInt)
  {
    switch (paramInt)
    {
    case 5: 
    case 6: 
    default: 
      Log.w(Call.class, "Unknown state %d", new Object[] { Integer.valueOf(paramInt) });
      return "UNKNOWN";
    case 0: 
      return "NEW";
    case 2: 
      return "RINGING";
    case 1: 
      return "DIALING";
    case 4: 
      return "ACTIVE";
    case 3: 
      return "HOLDING";
    case 7: 
      return "DISCONNECTED";
    case 9: 
      return "CONNECTING";
    case 10: 
      return "DISCONNECTING";
    }
    return "SELECT_PHONE_ACCOUNT";
  }
  
  @Deprecated
  public void addListener(Listener paramListener)
  {
    registerCallback(paramListener);
  }
  
  public void answer(int paramInt)
  {
    this.mInCallAdapter.answerCall(this.mTelecomCallId, paramInt);
  }
  
  public void conference(Call paramCall)
  {
    if (paramCall != null) {
      this.mInCallAdapter.conference(this.mTelecomCallId, paramCall.mTelecomCallId);
    }
  }
  
  public void disconnect()
  {
    this.mInCallAdapter.disconnectCall(this.mTelecomCallId);
  }
  
  public List<String> getCannedTextResponses()
  {
    return this.mCannedTextResponses;
  }
  
  public List<Call> getChildren()
  {
    if (!this.mChildrenCached)
    {
      this.mChildrenCached = true;
      this.mChildren.clear();
      Iterator localIterator = this.mChildrenIds.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = (String)localIterator.next();
        localObject = this.mPhone.internalGetCallByTelecomId((String)localObject);
        if (localObject == null) {
          this.mChildrenCached = false;
        } else {
          this.mChildren.add(localObject);
        }
      }
    }
    return this.mUnmodifiableChildren;
  }
  
  public List<Call> getConferenceableCalls()
  {
    return this.mUnmodifiableConferenceableCalls;
  }
  
  public Details getDetails()
  {
    return this.mDetails;
  }
  
  public Call getParent()
  {
    if (this.mParentId != null) {
      return this.mPhone.internalGetCallByTelecomId(this.mParentId);
    }
    return null;
  }
  
  public String getRemainingPostDialSequence()
  {
    return this.mRemainingPostDialSequence;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public InCallService.VideoCall getVideoCall()
  {
    return this.mVideoCallImpl;
  }
  
  public void hold()
  {
    this.mInCallAdapter.holdCall(this.mTelecomCallId);
  }
  
  final String internalGetCallId()
  {
    return this.mTelecomCallId;
  }
  
  final void internalOnConnectionEvent(String paramString, Bundle paramBundle)
  {
    fireOnConnectionEvent(paramString, paramBundle);
  }
  
  final void internalSetDisconnected()
  {
    if (this.mState != 7)
    {
      this.mState = 7;
      fireStateChanged(this.mState);
      fireCallDestroyed();
    }
  }
  
  final void internalSetPostDialWait(String paramString)
  {
    this.mRemainingPostDialSequence = paramString;
    firePostDialWait(this.mRemainingPostDialSequence);
  }
  
  final void internalUpdate(ParcelableCall paramParcelableCall, Map<String, Call> paramMap)
  {
    Object localObject = Details.createFromParcelableCall(paramParcelableCall);
    int j;
    int i;
    int k;
    label69:
    label96:
    int n;
    int m;
    if (Objects.equals(this.mDetails, localObject))
    {
      j = 0;
      if (j != 0) {
        this.mDetails = ((Details)localObject);
      }
      i = 0;
      k = i;
      if (this.mCannedTextResponses == null)
      {
        k = i;
        if (paramParcelableCall.getCannedSmsResponses() != null)
        {
          if (!paramParcelableCall.getCannedSmsResponses().isEmpty()) {
            break label328;
          }
          k = i;
        }
      }
      localObject = paramParcelableCall.getVideoCallImpl();
      if (!paramParcelableCall.isVideoCallProviderChanged()) {
        break label350;
      }
      if (!Objects.equals(this.mVideoCallImpl, localObject)) {
        break label345;
      }
      i = 0;
      if (i != 0) {
        this.mVideoCallImpl = ((VideoCallImpl)localObject);
      }
      if (this.mVideoCallImpl != null) {
        this.mVideoCallImpl.setVideoState(getDetails().getVideoState());
      }
      n = paramParcelableCall.getState();
      if (this.mState == n) {
        break label355;
      }
      m = 1;
      label145:
      if (m != 0) {
        this.mState = n;
      }
      localObject = paramParcelableCall.getParentCallId();
      if (!Objects.equals(this.mParentId, localObject)) {
        break label361;
      }
      n = 0;
      label177:
      if (n != 0) {
        this.mParentId = ((String)localObject);
      }
      if (!Objects.equals(paramParcelableCall.getChildCallIds(), this.mChildrenIds)) {
        break label367;
      }
    }
    label328:
    label345:
    label350:
    label355:
    label361:
    label367:
    for (int i1 = 0;; i1 = 1)
    {
      if (i1 != 0)
      {
        this.mChildrenIds.clear();
        this.mChildrenIds.addAll(paramParcelableCall.getChildCallIds());
        this.mChildrenCached = false;
      }
      localObject = paramParcelableCall.getConferenceableCallIds();
      paramParcelableCall = new ArrayList(((List)localObject).size());
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        String str = (String)((Iterator)localObject).next();
        if (paramMap.containsKey(str)) {
          paramParcelableCall.add((Call)paramMap.get(str));
        }
      }
      j = 1;
      break;
      this.mCannedTextResponses = Collections.unmodifiableList(paramParcelableCall.getCannedSmsResponses());
      k = 1;
      break label69;
      i = 1;
      break label96;
      i = 0;
      break label96;
      m = 0;
      break label145;
      n = 1;
      break label177;
    }
    if (!Objects.equals(this.mConferenceableCalls, paramParcelableCall))
    {
      this.mConferenceableCalls.clear();
      this.mConferenceableCalls.addAll(paramParcelableCall);
      fireConferenceableCallsChanged();
    }
    if (m != 0) {
      fireStateChanged(this.mState);
    }
    if (j != 0) {
      fireDetailsChanged(this.mDetails);
    }
    if (k != 0) {
      fireCannedTextResponsesLoaded(this.mCannedTextResponses);
    }
    if (i != 0) {
      fireVideoCallChanged(this.mVideoCallImpl);
    }
    if (n != 0) {
      fireParentChanged(getParent());
    }
    if (i1 != 0) {
      fireChildrenChanged(getChildren());
    }
    if (this.mState == 7) {
      fireCallDestroyed();
    }
  }
  
  public void mergeConference()
  {
    this.mInCallAdapter.mergeConference(this.mTelecomCallId);
  }
  
  public void phoneAccountSelected(PhoneAccountHandle paramPhoneAccountHandle, boolean paramBoolean)
  {
    this.mInCallAdapter.phoneAccountSelected(this.mTelecomCallId, paramPhoneAccountHandle, paramBoolean);
  }
  
  public void playDtmfTone(char paramChar)
  {
    this.mInCallAdapter.playDtmfTone(this.mTelecomCallId, paramChar);
  }
  
  public void postDialContinue(boolean paramBoolean)
  {
    this.mInCallAdapter.postDialContinue(this.mTelecomCallId, paramBoolean);
  }
  
  public void pullExternalCall()
  {
    if (!this.mDetails.hasProperty(64)) {
      return;
    }
    this.mInCallAdapter.pullExternalCall(this.mTelecomCallId);
  }
  
  public final void putExtra(String paramString, int paramInt)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putInt(paramString, paramInt);
    this.mInCallAdapter.putExtra(this.mTelecomCallId, paramString, paramInt);
  }
  
  public final void putExtra(String paramString1, String paramString2)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putString(paramString1, paramString2);
    this.mInCallAdapter.putExtra(this.mTelecomCallId, paramString1, paramString2);
  }
  
  public final void putExtra(String paramString, boolean paramBoolean)
  {
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putBoolean(paramString, paramBoolean);
    this.mInCallAdapter.putExtra(this.mTelecomCallId, paramString, paramBoolean);
  }
  
  public final void putExtras(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return;
    }
    if (this.mExtras == null) {
      this.mExtras = new Bundle();
    }
    this.mExtras.putAll(paramBundle);
    this.mInCallAdapter.putExtras(this.mTelecomCallId, paramBundle);
  }
  
  public void registerCallback(Callback paramCallback)
  {
    registerCallback(paramCallback, new Handler());
  }
  
  public void registerCallback(Callback paramCallback, Handler paramHandler)
  {
    unregisterCallback(paramCallback);
    if ((paramCallback != null) && (paramHandler != null) && (this.mState != 7)) {
      this.mCallbackRecords.add(new CallbackRecord(paramCallback, paramHandler));
    }
  }
  
  public void reject(boolean paramBoolean, String paramString)
  {
    this.mInCallAdapter.rejectCall(this.mTelecomCallId, paramBoolean, paramString);
  }
  
  public final void removeExtras(List<String> paramList)
  {
    if (this.mExtras != null)
    {
      Iterator localIterator = paramList.iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        this.mExtras.remove(str);
      }
      if (this.mExtras.size() == 0) {
        this.mExtras = null;
      }
    }
    this.mInCallAdapter.removeExtras(this.mTelecomCallId, paramList);
  }
  
  public final void removeExtras(String... paramVarArgs)
  {
    removeExtras(Arrays.asList(paramVarArgs));
  }
  
  @Deprecated
  public void removeListener(Listener paramListener)
  {
    unregisterCallback(paramListener);
  }
  
  public void sendCallEvent(String paramString, Bundle paramBundle)
  {
    this.mInCallAdapter.sendCallEvent(this.mTelecomCallId, paramString, paramBundle);
  }
  
  public void splitFromConference()
  {
    this.mInCallAdapter.splitFromConference(this.mTelecomCallId);
  }
  
  public void stopDtmfTone()
  {
    this.mInCallAdapter.stopDtmfTone(this.mTelecomCallId);
  }
  
  public void swapConference()
  {
    this.mInCallAdapter.swapConference(this.mTelecomCallId);
  }
  
  public String toString()
  {
    return "Call [id: " + this.mTelecomCallId + ", state: " + stateToString(this.mState) + ", details: " + this.mDetails + "]";
  }
  
  public void unhold()
  {
    this.mInCallAdapter.unholdCall(this.mTelecomCallId);
  }
  
  public void unregisterCallback(Callback paramCallback)
  {
    if ((paramCallback != null) && (this.mState != 7))
    {
      Iterator localIterator = this.mCallbackRecords.iterator();
      while (localIterator.hasNext())
      {
        CallbackRecord localCallbackRecord = (CallbackRecord)localIterator.next();
        if (localCallbackRecord.getCallback() == paramCallback) {
          this.mCallbackRecords.remove(localCallbackRecord);
        }
      }
    }
  }
  
  public static abstract class Callback
  {
    public void onCallDestroyed(Call paramCall) {}
    
    public void onCannedTextResponsesLoaded(Call paramCall, List<String> paramList) {}
    
    public void onChildrenChanged(Call paramCall, List<Call> paramList) {}
    
    public void onConferenceableCallsChanged(Call paramCall, List<Call> paramList) {}
    
    public void onConnectionEvent(Call paramCall, String paramString, Bundle paramBundle) {}
    
    public void onDetailsChanged(Call paramCall, Call.Details paramDetails) {}
    
    public void onParentChanged(Call paramCall1, Call paramCall2) {}
    
    public void onPostDialWait(Call paramCall, String paramString) {}
    
    public void onStateChanged(Call paramCall, int paramInt) {}
    
    public void onVideoCallChanged(Call paramCall, InCallService.VideoCall paramVideoCall) {}
  }
  
  public static class Details
  {
    public static final int CAPABILITY_ADD_PARTICIPANT = 33554432;
    public static final int CAPABILITY_CANNOT_DOWNGRADE_VIDEO_TO_AUDIO = 4194304;
    public static final int CAPABILITY_CAN_PAUSE_VIDEO = 1048576;
    public static final int CAPABILITY_CAN_PULL_CALL = 8388608;
    public static final int CAPABILITY_CAN_SEND_RESPONSE_VIA_CONNECTION = 2097152;
    public static final int CAPABILITY_CAN_UPGRADE_TO_VIDEO = 524288;
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
    public static final int CAPABILITY_UNUSED_1 = 16;
    public static final int CAPABILITY_VOICE_PRIVACY = 16777216;
    public static final int PROPERTY_CONFERENCE = 1;
    public static final int PROPERTY_EMERGENCY_CALLBACK_MODE = 4;
    public static final int PROPERTY_ENTERPRISE_CALL = 32;
    public static final int PROPERTY_GENERIC_CONFERENCE = 2;
    public static final int PROPERTY_HAS_CDMA_VOICE_PRIVACY = 128;
    public static final int PROPERTY_HIGH_DEF_AUDIO = 16;
    public static final int PROPERTY_IS_EXTERNAL_CALL = 64;
    public static final int PROPERTY_WIFI = 8;
    private final PhoneAccountHandle mAccountHandle;
    private final int mCallCapabilities;
    private final int mCallProperties;
    private final String mCallerDisplayName;
    private final int mCallerDisplayNamePresentation;
    private final long mConnectTimeMillis;
    private final DisconnectCause mDisconnectCause;
    private final Bundle mExtras;
    private final GatewayInfo mGatewayInfo;
    private final Uri mHandle;
    private final int mHandlePresentation;
    private final Bundle mIntentExtras;
    private final StatusHints mStatusHints;
    private final String mTelecomCallId;
    private final int mVideoState;
    
    public Details(String paramString1, Uri paramUri, int paramInt1, String paramString2, int paramInt2, PhoneAccountHandle paramPhoneAccountHandle, int paramInt3, int paramInt4, DisconnectCause paramDisconnectCause, long paramLong, GatewayInfo paramGatewayInfo, int paramInt5, StatusHints paramStatusHints, Bundle paramBundle1, Bundle paramBundle2)
    {
      this.mTelecomCallId = paramString1;
      this.mHandle = paramUri;
      this.mHandlePresentation = paramInt1;
      this.mCallerDisplayName = paramString2;
      this.mCallerDisplayNamePresentation = paramInt2;
      this.mAccountHandle = paramPhoneAccountHandle;
      this.mCallCapabilities = paramInt3;
      this.mCallProperties = paramInt4;
      this.mDisconnectCause = paramDisconnectCause;
      this.mConnectTimeMillis = paramLong;
      this.mGatewayInfo = paramGatewayInfo;
      this.mVideoState = paramInt5;
      this.mStatusHints = paramStatusHints;
      this.mExtras = paramBundle1;
      this.mIntentExtras = paramBundle2;
    }
    
    public static boolean can(int paramInt1, int paramInt2)
    {
      return (paramInt1 & paramInt2) == paramInt2;
    }
    
    public static String capabilitiesToString(int paramInt)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[Capabilities:");
      if (can(paramInt, 1)) {
        localStringBuilder.append(" CAPABILITY_HOLD");
      }
      if (can(paramInt, 2)) {
        localStringBuilder.append(" CAPABILITY_SUPPORT_HOLD");
      }
      if (can(paramInt, 4)) {
        localStringBuilder.append(" CAPABILITY_MERGE_CONFERENCE");
      }
      if (can(paramInt, 8)) {
        localStringBuilder.append(" CAPABILITY_SWAP_CONFERENCE");
      }
      if (can(paramInt, 32)) {
        localStringBuilder.append(" CAPABILITY_RESPOND_VIA_TEXT");
      }
      if (can(paramInt, 64)) {
        localStringBuilder.append(" CAPABILITY_MUTE");
      }
      if (can(paramInt, 128)) {
        localStringBuilder.append(" CAPABILITY_MANAGE_CONFERENCE");
      }
      if (can(paramInt, 256)) {
        localStringBuilder.append(" CAPABILITY_SUPPORTS_VT_LOCAL_RX");
      }
      if (can(paramInt, 512)) {
        localStringBuilder.append(" CAPABILITY_SUPPORTS_VT_LOCAL_TX");
      }
      if (can(paramInt, 768)) {
        localStringBuilder.append(" CAPABILITY_SUPPORTS_VT_LOCAL_BIDIRECTIONAL");
      }
      if (can(paramInt, 1024)) {
        localStringBuilder.append(" CAPABILITY_SUPPORTS_VT_REMOTE_RX");
      }
      if (can(paramInt, 2048)) {
        localStringBuilder.append(" CAPABILITY_SUPPORTS_VT_REMOTE_TX");
      }
      if (can(paramInt, 4194304)) {
        localStringBuilder.append(" CAPABILITY_CANNOT_DOWNGRADE_VIDEO_TO_AUDIO");
      }
      if (can(paramInt, 3072)) {
        localStringBuilder.append(" CAPABILITY_SUPPORTS_VT_REMOTE_BIDIRECTIONAL");
      }
      if (can(paramInt, 262144)) {
        localStringBuilder.append(" CAPABILITY_SPEED_UP_MT_AUDIO");
      }
      if (can(paramInt, 524288)) {
        localStringBuilder.append(" CAPABILITY_CAN_UPGRADE_TO_VIDEO");
      }
      if (can(paramInt, 1048576)) {
        localStringBuilder.append(" CAPABILITY_CAN_PAUSE_VIDEO");
      }
      if (can(paramInt, 8388608)) {
        localStringBuilder.append(" CAPABILITY_CAN_PULL_CALL");
      }
      if (can(paramInt, 16777216)) {
        localStringBuilder.append(" CAPABILITY_VOICE_PRIVACY");
      }
      if (can(paramInt, 33554432)) {
        localStringBuilder.append(" CAPABILITY_ADD_PARTICIPANT");
      }
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
    
    public static Details createFromParcelableCall(ParcelableCall paramParcelableCall)
    {
      return new Details(paramParcelableCall.getId(), paramParcelableCall.getHandle(), paramParcelableCall.getHandlePresentation(), paramParcelableCall.getCallerDisplayName(), paramParcelableCall.getCallerDisplayNamePresentation(), paramParcelableCall.getAccountHandle(), paramParcelableCall.getCapabilities(), paramParcelableCall.getProperties(), paramParcelableCall.getDisconnectCause(), paramParcelableCall.getConnectTimeMillis(), paramParcelableCall.getGatewayInfo(), paramParcelableCall.getVideoState(), paramParcelableCall.getStatusHints(), paramParcelableCall.getExtras(), paramParcelableCall.getIntentExtras());
    }
    
    public static boolean hasProperty(int paramInt1, int paramInt2)
    {
      return (paramInt1 & paramInt2) == paramInt2;
    }
    
    public static String propertiesToString(int paramInt)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[Properties:");
      if (hasProperty(paramInt, 1)) {
        localStringBuilder.append(" PROPERTY_CONFERENCE");
      }
      if (hasProperty(paramInt, 2)) {
        localStringBuilder.append(" PROPERTY_GENERIC_CONFERENCE");
      }
      if (hasProperty(paramInt, 8)) {
        localStringBuilder.append(" PROPERTY_WIFI");
      }
      if (hasProperty(paramInt, 16)) {
        localStringBuilder.append(" PROPERTY_HIGH_DEF_AUDIO");
      }
      if (hasProperty(paramInt, 4)) {
        localStringBuilder.append(" PROPERTY_EMERGENCY_CALLBACK_MODE");
      }
      if (hasProperty(paramInt, 64)) {
        localStringBuilder.append(" PROPERTY_IS_EXTERNAL_CALL");
      }
      if (hasProperty(paramInt, 128)) {
        localStringBuilder.append(" PROPERTY_HAS_CDMA_VOICE_PRIVACY");
      }
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
    
    public boolean can(int paramInt)
    {
      return can(this.mCallCapabilities, paramInt);
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool2 = false;
      if ((paramObject instanceof Details))
      {
        paramObject = (Details)paramObject;
        boolean bool1 = bool2;
        if (Objects.equals(this.mHandle, ((Details)paramObject).mHandle))
        {
          bool1 = bool2;
          if (Objects.equals(Integer.valueOf(this.mHandlePresentation), Integer.valueOf(((Details)paramObject).mHandlePresentation)))
          {
            bool1 = bool2;
            if (Objects.equals(this.mCallerDisplayName, ((Details)paramObject).mCallerDisplayName))
            {
              bool1 = bool2;
              if (Objects.equals(Integer.valueOf(this.mCallerDisplayNamePresentation), Integer.valueOf(((Details)paramObject).mCallerDisplayNamePresentation)))
              {
                bool1 = bool2;
                if (Objects.equals(this.mAccountHandle, ((Details)paramObject).mAccountHandle))
                {
                  bool1 = bool2;
                  if (Objects.equals(Integer.valueOf(this.mCallCapabilities), Integer.valueOf(((Details)paramObject).mCallCapabilities)))
                  {
                    bool1 = bool2;
                    if (Objects.equals(Integer.valueOf(this.mCallProperties), Integer.valueOf(((Details)paramObject).mCallProperties)))
                    {
                      bool1 = bool2;
                      if (Objects.equals(this.mDisconnectCause, ((Details)paramObject).mDisconnectCause))
                      {
                        bool1 = bool2;
                        if (Objects.equals(Long.valueOf(this.mConnectTimeMillis), Long.valueOf(((Details)paramObject).mConnectTimeMillis)))
                        {
                          bool1 = bool2;
                          if (Objects.equals(this.mGatewayInfo, ((Details)paramObject).mGatewayInfo))
                          {
                            bool1 = bool2;
                            if (Objects.equals(Integer.valueOf(this.mVideoState), Integer.valueOf(((Details)paramObject).mVideoState)))
                            {
                              bool1 = bool2;
                              if (Objects.equals(this.mStatusHints, ((Details)paramObject).mStatusHints))
                              {
                                bool1 = bool2;
                                if (Call.-wrap0(this.mExtras, ((Details)paramObject).mExtras)) {
                                  bool1 = Call.-wrap0(this.mIntentExtras, ((Details)paramObject).mIntentExtras);
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
      return false;
    }
    
    public PhoneAccountHandle getAccountHandle()
    {
      return this.mAccountHandle;
    }
    
    public int getCallCapabilities()
    {
      return this.mCallCapabilities;
    }
    
    public int getCallProperties()
    {
      return this.mCallProperties;
    }
    
    public String getCallerDisplayName()
    {
      return this.mCallerDisplayName;
    }
    
    public int getCallerDisplayNamePresentation()
    {
      return this.mCallerDisplayNamePresentation;
    }
    
    public final long getConnectTimeMillis()
    {
      return this.mConnectTimeMillis;
    }
    
    public DisconnectCause getDisconnectCause()
    {
      return this.mDisconnectCause;
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public GatewayInfo getGatewayInfo()
    {
      return this.mGatewayInfo;
    }
    
    public Uri getHandle()
    {
      return this.mHandle;
    }
    
    public int getHandlePresentation()
    {
      return this.mHandlePresentation;
    }
    
    public Bundle getIntentExtras()
    {
      return this.mIntentExtras;
    }
    
    public StatusHints getStatusHints()
    {
      return this.mStatusHints;
    }
    
    public String getTelecomCallId()
    {
      return this.mTelecomCallId;
    }
    
    public int getVideoState()
    {
      return this.mVideoState;
    }
    
    public boolean hasProperty(int paramInt)
    {
      return hasProperty(this.mCallProperties, paramInt);
    }
    
    public int hashCode()
    {
      return Objects.hashCode(this.mHandle) + Objects.hashCode(Integer.valueOf(this.mHandlePresentation)) + Objects.hashCode(this.mCallerDisplayName) + Objects.hashCode(Integer.valueOf(this.mCallerDisplayNamePresentation)) + Objects.hashCode(this.mAccountHandle) + Objects.hashCode(Integer.valueOf(this.mCallCapabilities)) + Objects.hashCode(Integer.valueOf(this.mCallProperties)) + Objects.hashCode(this.mDisconnectCause) + Objects.hashCode(Long.valueOf(this.mConnectTimeMillis)) + Objects.hashCode(this.mGatewayInfo) + Objects.hashCode(Integer.valueOf(this.mVideoState)) + Objects.hashCode(this.mStatusHints) + Objects.hashCode(this.mExtras) + Objects.hashCode(this.mIntentExtras);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("[pa: ");
      localStringBuilder.append(this.mAccountHandle);
      localStringBuilder.append(", hdl: ");
      localStringBuilder.append(Log.pii(this.mHandle));
      localStringBuilder.append(", caps: ");
      localStringBuilder.append(capabilitiesToString(this.mCallCapabilities));
      localStringBuilder.append(", props: ");
      localStringBuilder.append(propertiesToString(this.mCallProperties));
      localStringBuilder.append("]");
      return localStringBuilder.toString();
    }
  }
  
  @Deprecated
  public static abstract class Listener
    extends Call.Callback
  {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/Call.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */