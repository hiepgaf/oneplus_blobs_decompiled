package android.media;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class MediaDrm
{
  public static final int CERTIFICATE_TYPE_NONE = 0;
  public static final int CERTIFICATE_TYPE_X509 = 1;
  private static final int DRM_EVENT = 200;
  public static final int EVENT_KEY_EXPIRED = 3;
  public static final int EVENT_KEY_REQUIRED = 2;
  public static final int EVENT_PROVISION_REQUIRED = 1;
  public static final int EVENT_SESSION_RECLAIMED = 5;
  public static final int EVENT_VENDOR_DEFINED = 4;
  private static final int EXPIRATION_UPDATE = 201;
  private static final int KEY_STATUS_CHANGE = 202;
  public static final int KEY_TYPE_OFFLINE = 2;
  public static final int KEY_TYPE_RELEASE = 3;
  public static final int KEY_TYPE_STREAMING = 1;
  private static final String PERMISSION = "android.permission.ACCESS_DRM_CERTIFICATES";
  public static final String PROPERTY_ALGORITHMS = "algorithms";
  public static final String PROPERTY_DESCRIPTION = "description";
  public static final String PROPERTY_DEVICE_UNIQUE_ID = "deviceUniqueId";
  public static final String PROPERTY_VENDOR = "vendor";
  public static final String PROPERTY_VERSION = "version";
  private static final String TAG = "MediaDrm";
  private EventHandler mEventHandler;
  private long mNativeContext;
  private OnEventListener mOnEventListener;
  private EventHandler mOnExpirationUpdateEventHandler;
  private OnExpirationUpdateListener mOnExpirationUpdateListener;
  private EventHandler mOnKeyStatusChangeEventHandler;
  private OnKeyStatusChangeListener mOnKeyStatusChangeListener;
  
  static
  {
    System.loadLibrary("media_jni");
    native_init();
  }
  
  public MediaDrm(UUID paramUUID)
    throws UnsupportedSchemeException
  {
    Looper localLooper = Looper.myLooper();
    if (localLooper != null) {
      this.mEventHandler = new EventHandler(this, localLooper);
    }
    for (;;)
    {
      native_setup(new WeakReference(this), getByteArrayFromUUID(paramUUID));
      return;
      localLooper = Looper.getMainLooper();
      if (localLooper != null) {
        this.mEventHandler = new EventHandler(this, localLooper);
      } else {
        this.mEventHandler = null;
      }
    }
  }
  
  private static final native byte[] decryptNative(MediaDrm paramMediaDrm, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4);
  
  private static final native byte[] encryptNative(MediaDrm paramMediaDrm, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4);
  
  private static final byte[] getByteArrayFromUUID(UUID paramUUID)
  {
    long l1 = paramUUID.getMostSignificantBits();
    long l2 = paramUUID.getLeastSignificantBits();
    paramUUID = new byte[16];
    int i = 0;
    while (i < 8)
    {
      paramUUID[i] = ((byte)(int)(l1 >>> (7 - i) * 8));
      paramUUID[(i + 8)] = ((byte)(int)(l2 >>> (7 - i) * 8));
      i += 1;
    }
    return paramUUID;
  }
  
  private native ProvisionRequest getProvisionRequestNative(int paramInt, String paramString);
  
  public static final boolean isCryptoSchemeSupported(UUID paramUUID)
  {
    return isCryptoSchemeSupportedNative(getByteArrayFromUUID(paramUUID), null);
  }
  
  public static final boolean isCryptoSchemeSupported(UUID paramUUID, String paramString)
  {
    return isCryptoSchemeSupportedNative(getByteArrayFromUUID(paramUUID), paramString);
  }
  
  private static final native boolean isCryptoSchemeSupportedNative(byte[] paramArrayOfByte, String paramString);
  
  private List<KeyStatus> keyStatusListFromParcel(Parcel paramParcel)
  {
    int i = paramParcel.readInt();
    ArrayList localArrayList = new ArrayList(i);
    while (i > 0)
    {
      localArrayList.add(new KeyStatus(paramParcel.createByteArray(), paramParcel.readInt()));
      i -= 1;
    }
    return localArrayList;
  }
  
  private final native void native_finalize();
  
  private static final native void native_init();
  
  private final native void native_setup(Object paramObject, byte[] paramArrayOfByte);
  
  private static void postEventFromNative(Object paramObject1, int paramInt1, int paramInt2, int paramInt3, Object paramObject2)
  {
    paramObject1 = (MediaDrm)((WeakReference)paramObject1).get();
    if (paramObject1 == null) {
      return;
    }
    if (((MediaDrm)paramObject1).mEventHandler != null)
    {
      paramObject2 = ((MediaDrm)paramObject1).mEventHandler.obtainMessage(paramInt1, paramInt2, paramInt3, paramObject2);
      ((MediaDrm)paramObject1).mEventHandler.sendMessage((Message)paramObject2);
    }
  }
  
  private native Certificate provideProvisionResponseNative(byte[] paramArrayOfByte)
    throws DeniedByServerException;
  
  private static final native void setCipherAlgorithmNative(MediaDrm paramMediaDrm, byte[] paramArrayOfByte, String paramString);
  
  private static final native void setMacAlgorithmNative(MediaDrm paramMediaDrm, byte[] paramArrayOfByte, String paramString);
  
  private static final native byte[] signNative(MediaDrm paramMediaDrm, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3);
  
  private static final native byte[] signRSANative(MediaDrm paramMediaDrm, byte[] paramArrayOfByte1, String paramString, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3);
  
  private static final native boolean verifyNative(MediaDrm paramMediaDrm, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3, byte[] paramArrayOfByte4);
  
  public native void closeSession(byte[] paramArrayOfByte);
  
  protected void finalize()
  {
    native_finalize();
  }
  
  public CertificateRequest getCertificateRequest(int paramInt, String paramString)
  {
    paramString = getProvisionRequestNative(paramInt, paramString);
    return new CertificateRequest(paramString.getData(), paramString.getDefaultUrl());
  }
  
  public CryptoSession getCryptoSession(byte[] paramArrayOfByte, String paramString1, String paramString2)
  {
    return new CryptoSession(paramArrayOfByte, paramString1, paramString2);
  }
  
  public native KeyRequest getKeyRequest(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString, int paramInt, HashMap<String, String> paramHashMap)
    throws NotProvisionedException;
  
  public native byte[] getPropertyByteArray(String paramString);
  
  public native String getPropertyString(String paramString);
  
  public ProvisionRequest getProvisionRequest()
  {
    return getProvisionRequestNative(0, "");
  }
  
  public native byte[] getSecureStop(byte[] paramArrayOfByte);
  
  public native List<byte[]> getSecureStops();
  
  public native byte[] openSession()
    throws NotProvisionedException, ResourceBusyException;
  
  public Certificate provideCertificateResponse(byte[] paramArrayOfByte)
    throws DeniedByServerException
  {
    return provideProvisionResponseNative(paramArrayOfByte);
  }
  
  public native byte[] provideKeyResponse(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    throws NotProvisionedException, DeniedByServerException;
  
  public void provideProvisionResponse(byte[] paramArrayOfByte)
    throws DeniedByServerException
  {
    provideProvisionResponseNative(paramArrayOfByte);
  }
  
  public native HashMap<String, String> queryKeyStatus(byte[] paramArrayOfByte);
  
  public final native void release();
  
  public native void releaseAllSecureStops();
  
  public native void releaseSecureStops(byte[] paramArrayOfByte);
  
  public native void removeKeys(byte[] paramArrayOfByte);
  
  public native void restoreKeys(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);
  
  public void setOnEventListener(OnEventListener paramOnEventListener)
  {
    this.mOnEventListener = paramOnEventListener;
  }
  
  public void setOnExpirationUpdateListener(OnExpirationUpdateListener paramOnExpirationUpdateListener, Handler paramHandler)
  {
    if (paramOnExpirationUpdateListener != null) {
      if (paramHandler == null) {
        break label55;
      }
    }
    label55:
    for (paramHandler = paramHandler.getLooper();; paramHandler = Looper.myLooper())
    {
      if ((paramHandler != null) && ((this.mEventHandler == null) || (this.mEventHandler.getLooper() != paramHandler))) {
        this.mEventHandler = new EventHandler(this, paramHandler);
      }
      this.mOnExpirationUpdateListener = paramOnExpirationUpdateListener;
      return;
    }
  }
  
  public void setOnKeyStatusChangeListener(OnKeyStatusChangeListener paramOnKeyStatusChangeListener, Handler paramHandler)
  {
    if (paramOnKeyStatusChangeListener != null) {
      if (paramHandler == null) {
        break label55;
      }
    }
    label55:
    for (paramHandler = paramHandler.getLooper();; paramHandler = Looper.myLooper())
    {
      if ((paramHandler != null) && ((this.mEventHandler == null) || (this.mEventHandler.getLooper() != paramHandler))) {
        this.mEventHandler = new EventHandler(this, paramHandler);
      }
      this.mOnKeyStatusChangeListener = paramOnKeyStatusChangeListener;
      return;
    }
  }
  
  public native void setPropertyByteArray(String paramString, byte[] paramArrayOfByte);
  
  public native void setPropertyString(String paramString1, String paramString2);
  
  public byte[] signRSA(byte[] paramArrayOfByte1, String paramString, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
  {
    return signRSANative(this, paramArrayOfByte1, paramString, paramArrayOfByte2, paramArrayOfByte3);
  }
  
  public static final class Certificate
  {
    private byte[] mCertificateData;
    private byte[] mWrappedKey;
    
    public byte[] getContent()
    {
      if (this.mCertificateData == null) {
        throw new RuntimeException("Cerfificate is not initialized");
      }
      return this.mCertificateData;
    }
    
    public byte[] getWrappedPrivateKey()
    {
      if (this.mWrappedKey == null) {
        throw new RuntimeException("Cerfificate is not initialized");
      }
      return this.mWrappedKey;
    }
  }
  
  public static final class CertificateRequest
  {
    private byte[] mData;
    private String mDefaultUrl;
    
    CertificateRequest(byte[] paramArrayOfByte, String paramString)
    {
      this.mData = paramArrayOfByte;
      this.mDefaultUrl = paramString;
    }
    
    public byte[] getData()
    {
      return this.mData;
    }
    
    public String getDefaultUrl()
    {
      return this.mDefaultUrl;
    }
  }
  
  public final class CryptoSession
  {
    private byte[] mSessionId;
    
    CryptoSession(byte[] paramArrayOfByte, String paramString1, String paramString2)
    {
      this.mSessionId = paramArrayOfByte;
      MediaDrm.-wrap5(MediaDrm.this, paramArrayOfByte, paramString1);
      MediaDrm.-wrap6(MediaDrm.this, paramArrayOfByte, paramString2);
    }
    
    public byte[] decrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    {
      return MediaDrm.-wrap1(MediaDrm.this, this.mSessionId, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3);
    }
    
    public byte[] encrypt(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    {
      return MediaDrm.-wrap2(MediaDrm.this, this.mSessionId, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3);
    }
    
    public byte[] sign(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
    {
      return MediaDrm.-wrap3(MediaDrm.this, this.mSessionId, paramArrayOfByte1, paramArrayOfByte2);
    }
    
    public boolean verify(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, byte[] paramArrayOfByte3)
    {
      return MediaDrm.-wrap0(MediaDrm.this, this.mSessionId, paramArrayOfByte1, paramArrayOfByte2, paramArrayOfByte3);
    }
  }
  
  private class EventHandler
    extends Handler
  {
    private MediaDrm mMediaDrm;
    
    public EventHandler(MediaDrm paramMediaDrm, Looper paramLooper)
    {
      super();
      this.mMediaDrm = paramMediaDrm;
    }
    
    public void handleMessage(Message paramMessage)
    {
      if (MediaDrm.-get0(this.mMediaDrm) == 0L)
      {
        Log.w("MediaDrm", "MediaDrm went away with unhandled events");
        return;
      }
      Object localObject2;
      Object localObject1;
      switch (paramMessage.what)
      {
      default: 
        Log.e("MediaDrm", "Unknown message type " + paramMessage.what);
        return;
      case 200: 
        if ((MediaDrm.-get1(MediaDrm.this) != null) && (paramMessage.obj != null) && ((paramMessage.obj instanceof Parcel)))
        {
          Object localObject3 = (Parcel)paramMessage.obj;
          localObject2 = ((Parcel)localObject3).createByteArray();
          localObject1 = localObject2;
          if (localObject2.length == 0) {
            localObject1 = null;
          }
          localObject3 = ((Parcel)localObject3).createByteArray();
          localObject2 = localObject3;
          if (localObject3.length == 0) {
            localObject2 = null;
          }
          Log.i("MediaDrm", "Drm event (" + paramMessage.arg1 + "," + paramMessage.arg2 + ")");
          MediaDrm.-get1(MediaDrm.this).onEvent(this.mMediaDrm, (byte[])localObject1, paramMessage.arg1, paramMessage.arg2, (byte[])localObject2);
        }
        return;
      case 202: 
        if ((MediaDrm.-get3(MediaDrm.this) != null) && (paramMessage.obj != null) && ((paramMessage.obj instanceof Parcel)))
        {
          paramMessage = (Parcel)paramMessage.obj;
          localObject1 = paramMessage.createByteArray();
          if (localObject1.length > 0)
          {
            localObject2 = MediaDrm.-wrap4(MediaDrm.this, paramMessage);
            if (paramMessage.readInt() == 0) {
              break label327;
            }
          }
        }
        label327:
        for (boolean bool = true;; bool = false)
        {
          Log.i("MediaDrm", "Drm key status changed");
          MediaDrm.-get3(MediaDrm.this).onKeyStatusChange(this.mMediaDrm, (byte[])localObject1, (List)localObject2, bool);
          return;
        }
      }
      if ((MediaDrm.-get2(MediaDrm.this) != null) && (paramMessage.obj != null) && ((paramMessage.obj instanceof Parcel)))
      {
        paramMessage = (Parcel)paramMessage.obj;
        localObject1 = paramMessage.createByteArray();
        if (localObject1.length > 0)
        {
          long l = paramMessage.readLong();
          Log.i("MediaDrm", "Drm key expiration update: " + l);
          MediaDrm.-get2(MediaDrm.this).onExpirationUpdate(this.mMediaDrm, (byte[])localObject1, l);
        }
      }
    }
  }
  
  public static final class KeyRequest
  {
    public static final int REQUEST_TYPE_INITIAL = 0;
    public static final int REQUEST_TYPE_RELEASE = 2;
    public static final int REQUEST_TYPE_RENEWAL = 1;
    private byte[] mData;
    private String mDefaultUrl;
    private int mRequestType;
    
    public byte[] getData()
    {
      if (this.mData == null) {
        throw new RuntimeException("KeyRequest is not initialized");
      }
      return this.mData;
    }
    
    public String getDefaultUrl()
    {
      if (this.mDefaultUrl == null) {
        throw new RuntimeException("KeyRequest is not initialized");
      }
      return this.mDefaultUrl;
    }
    
    public int getRequestType()
    {
      return this.mRequestType;
    }
  }
  
  public static final class KeyStatus
  {
    public static final int STATUS_EXPIRED = 1;
    public static final int STATUS_INTERNAL_ERROR = 4;
    public static final int STATUS_OUTPUT_NOT_ALLOWED = 2;
    public static final int STATUS_PENDING = 3;
    public static final int STATUS_USABLE = 0;
    private final byte[] mKeyId;
    private final int mStatusCode;
    
    KeyStatus(byte[] paramArrayOfByte, int paramInt)
    {
      this.mKeyId = paramArrayOfByte;
      this.mStatusCode = paramInt;
    }
    
    public byte[] getKeyId()
    {
      return this.mKeyId;
    }
    
    public int getStatusCode()
    {
      return this.mStatusCode;
    }
  }
  
  public static final class MediaDrmStateException
    extends IllegalStateException
  {
    private final String mDiagnosticInfo;
    private final int mErrorCode;
    
    public MediaDrmStateException(int paramInt, String paramString)
    {
      super();
      this.mErrorCode = paramInt;
      if (paramInt < 0) {}
      for (paramString = "neg_";; paramString = "")
      {
        this.mDiagnosticInfo = ("android.media.MediaDrm.error_" + paramString + Math.abs(paramInt));
        return;
      }
    }
    
    public String getDiagnosticInfo()
    {
      return this.mDiagnosticInfo;
    }
    
    public int getErrorCode()
    {
      return this.mErrorCode;
    }
  }
  
  public static abstract interface OnEventListener
  {
    public abstract void onEvent(MediaDrm paramMediaDrm, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2);
  }
  
  public static abstract interface OnExpirationUpdateListener
  {
    public abstract void onExpirationUpdate(MediaDrm paramMediaDrm, byte[] paramArrayOfByte, long paramLong);
  }
  
  public static abstract interface OnKeyStatusChangeListener
  {
    public abstract void onKeyStatusChange(MediaDrm paramMediaDrm, byte[] paramArrayOfByte, List<MediaDrm.KeyStatus> paramList, boolean paramBoolean);
  }
  
  public static final class ProvisionRequest
  {
    private byte[] mData;
    private String mDefaultUrl;
    
    public byte[] getData()
    {
      if (this.mData == null) {
        throw new RuntimeException("ProvisionRequest is not initialized");
      }
      return this.mData;
    }
    
    public String getDefaultUrl()
    {
      if (this.mDefaultUrl == null) {
        throw new RuntimeException("ProvisionRequest is not initialized");
      }
      return this.mDefaultUrl;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/MediaDrm.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */