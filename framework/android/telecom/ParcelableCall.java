package android.telecom;

import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.android.internal.telecom.IVideoProvider;
import com.android.internal.telecom.IVideoProvider.Stub;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ParcelableCall
  implements Parcelable
{
  public static final Parcelable.Creator<ParcelableCall> CREATOR = new Parcelable.Creator()
  {
    public ParcelableCall createFromParcel(Parcel paramAnonymousParcel)
    {
      ClassLoader localClassLoader = ParcelableCall.class.getClassLoader();
      String str1 = paramAnonymousParcel.readString();
      int i = paramAnonymousParcel.readInt();
      DisconnectCause localDisconnectCause = (DisconnectCause)paramAnonymousParcel.readParcelable(localClassLoader);
      ArrayList localArrayList1 = new ArrayList();
      paramAnonymousParcel.readList(localArrayList1, localClassLoader);
      int j = paramAnonymousParcel.readInt();
      int k = paramAnonymousParcel.readInt();
      long l = paramAnonymousParcel.readLong();
      Uri localUri = (Uri)paramAnonymousParcel.readParcelable(localClassLoader);
      int m = paramAnonymousParcel.readInt();
      String str2 = paramAnonymousParcel.readString();
      int n = paramAnonymousParcel.readInt();
      GatewayInfo localGatewayInfo = (GatewayInfo)paramAnonymousParcel.readParcelable(localClassLoader);
      PhoneAccountHandle localPhoneAccountHandle = (PhoneAccountHandle)paramAnonymousParcel.readParcelable(localClassLoader);
      if (paramAnonymousParcel.readByte() == 1) {}
      for (boolean bool = true;; bool = false)
      {
        IVideoProvider localIVideoProvider = IVideoProvider.Stub.asInterface(paramAnonymousParcel.readStrongBinder());
        String str3 = paramAnonymousParcel.readString();
        ArrayList localArrayList2 = new ArrayList();
        paramAnonymousParcel.readList(localArrayList2, localClassLoader);
        StatusHints localStatusHints = (StatusHints)paramAnonymousParcel.readParcelable(localClassLoader);
        int i1 = paramAnonymousParcel.readInt();
        ArrayList localArrayList3 = new ArrayList();
        paramAnonymousParcel.readList(localArrayList3, localClassLoader);
        return new ParcelableCall(str1, i, localDisconnectCause, localArrayList1, j, k, l, localUri, m, str2, n, localGatewayInfo, localPhoneAccountHandle, bool, localIVideoProvider, str3, localArrayList2, localStatusHints, i1, localArrayList3, paramAnonymousParcel.readBundle(localClassLoader), paramAnonymousParcel.readBundle(localClassLoader));
      }
    }
    
    public ParcelableCall[] newArray(int paramAnonymousInt)
    {
      return new ParcelableCall[paramAnonymousInt];
    }
  };
  private final PhoneAccountHandle mAccountHandle;
  private final String mCallerDisplayName;
  private final int mCallerDisplayNamePresentation;
  private final List<String> mCannedSmsResponses;
  private final int mCapabilities;
  private final List<String> mChildCallIds;
  private final List<String> mConferenceableCallIds;
  private final long mConnectTimeMillis;
  private final DisconnectCause mDisconnectCause;
  private final Bundle mExtras;
  private final GatewayInfo mGatewayInfo;
  private final Uri mHandle;
  private final int mHandlePresentation;
  private final String mId;
  private final Bundle mIntentExtras;
  private final boolean mIsVideoCallProviderChanged;
  private final String mParentCallId;
  private final int mProperties;
  private final int mState;
  private final StatusHints mStatusHints;
  private VideoCallImpl mVideoCall;
  private final IVideoProvider mVideoCallProvider;
  private final int mVideoState;
  
  public ParcelableCall(String paramString1, int paramInt1, DisconnectCause paramDisconnectCause, List<String> paramList1, int paramInt2, int paramInt3, long paramLong, Uri paramUri, int paramInt4, String paramString2, int paramInt5, GatewayInfo paramGatewayInfo, PhoneAccountHandle paramPhoneAccountHandle, boolean paramBoolean, IVideoProvider paramIVideoProvider, String paramString3, List<String> paramList2, StatusHints paramStatusHints, int paramInt6, List<String> paramList3, Bundle paramBundle1, Bundle paramBundle2)
  {
    this.mId = paramString1;
    this.mState = paramInt1;
    this.mDisconnectCause = paramDisconnectCause;
    this.mCannedSmsResponses = paramList1;
    this.mCapabilities = paramInt2;
    this.mProperties = paramInt3;
    this.mConnectTimeMillis = paramLong;
    this.mHandle = paramUri;
    this.mHandlePresentation = paramInt4;
    this.mCallerDisplayName = paramString2;
    this.mCallerDisplayNamePresentation = paramInt5;
    this.mGatewayInfo = paramGatewayInfo;
    this.mAccountHandle = paramPhoneAccountHandle;
    this.mIsVideoCallProviderChanged = paramBoolean;
    this.mVideoCallProvider = paramIVideoProvider;
    this.mParentCallId = paramString3;
    this.mChildCallIds = paramList2;
    this.mStatusHints = paramStatusHints;
    this.mVideoState = paramInt6;
    this.mConferenceableCallIds = Collections.unmodifiableList(paramList3);
    this.mIntentExtras = paramBundle1;
    this.mExtras = paramBundle2;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public PhoneAccountHandle getAccountHandle()
  {
    return this.mAccountHandle;
  }
  
  public String getCallerDisplayName()
  {
    return this.mCallerDisplayName;
  }
  
  public int getCallerDisplayNamePresentation()
  {
    return this.mCallerDisplayNamePresentation;
  }
  
  public List<String> getCannedSmsResponses()
  {
    return this.mCannedSmsResponses;
  }
  
  public int getCapabilities()
  {
    return this.mCapabilities;
  }
  
  public List<String> getChildCallIds()
  {
    return this.mChildCallIds;
  }
  
  public List<String> getConferenceableCallIds()
  {
    return this.mConferenceableCallIds;
  }
  
  public long getConnectTimeMillis()
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
  
  public String getId()
  {
    return this.mId;
  }
  
  public Bundle getIntentExtras()
  {
    return this.mIntentExtras;
  }
  
  public String getParentCallId()
  {
    return this.mParentCallId;
  }
  
  public int getProperties()
  {
    return this.mProperties;
  }
  
  public int getState()
  {
    return this.mState;
  }
  
  public StatusHints getStatusHints()
  {
    return this.mStatusHints;
  }
  
  public VideoCallImpl getVideoCallImpl()
  {
    if ((this.mVideoCall == null) && (this.mVideoCallProvider != null)) {}
    try
    {
      this.mVideoCall = new VideoCallImpl(this.mVideoCallProvider);
      return this.mVideoCall;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;) {}
    }
  }
  
  public int getVideoState()
  {
    return this.mVideoState;
  }
  
  public boolean isVideoCallProviderChanged()
  {
    return this.mIsVideoCallProviderChanged;
  }
  
  public String toString()
  {
    return String.format("[%s, parent:%s, children:%s]", new Object[] { this.mId, this.mParentCallId, this.mChildCallIds });
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mId);
    paramParcel.writeInt(this.mState);
    paramParcel.writeParcelable(this.mDisconnectCause, 0);
    paramParcel.writeList(this.mCannedSmsResponses);
    paramParcel.writeInt(this.mCapabilities);
    paramParcel.writeInt(this.mProperties);
    paramParcel.writeLong(this.mConnectTimeMillis);
    paramParcel.writeParcelable(this.mHandle, 0);
    paramParcel.writeInt(this.mHandlePresentation);
    paramParcel.writeString(this.mCallerDisplayName);
    paramParcel.writeInt(this.mCallerDisplayNamePresentation);
    paramParcel.writeParcelable(this.mGatewayInfo, 0);
    paramParcel.writeParcelable(this.mAccountHandle, 0);
    if (this.mIsVideoCallProviderChanged)
    {
      paramInt = 1;
      paramParcel.writeByte((byte)paramInt);
      if (this.mVideoCallProvider == null) {
        break label208;
      }
    }
    label208:
    for (IBinder localIBinder = this.mVideoCallProvider.asBinder();; localIBinder = null)
    {
      paramParcel.writeStrongBinder(localIBinder);
      paramParcel.writeString(this.mParentCallId);
      paramParcel.writeList(this.mChildCallIds);
      paramParcel.writeParcelable(this.mStatusHints, 0);
      paramParcel.writeInt(this.mVideoState);
      paramParcel.writeList(this.mConferenceableCallIds);
      paramParcel.writeBundle(this.mIntentExtras);
      paramParcel.writeBundle(this.mExtras);
      return;
      paramInt = 0;
      break;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/telecom/ParcelableCall.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */