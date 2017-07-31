package android.content.pm;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.FileBridge.FileBridgeOutputStream;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.util.ExceptionUtils;
import com.android.internal.util.IndentingPrintWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PackageInstaller
{
  public static final String ACTION_CONFIRM_PERMISSIONS = "android.content.pm.action.CONFIRM_PERMISSIONS";
  public static final String ACTION_SESSION_DETAILS = "android.content.pm.action.SESSION_DETAILS";
  public static final String EXTRA_CALLBACK = "android.content.pm.extra.CALLBACK";
  public static final String EXTRA_LEGACY_BUNDLE = "android.content.pm.extra.LEGACY_BUNDLE";
  public static final String EXTRA_LEGACY_STATUS = "android.content.pm.extra.LEGACY_STATUS";
  public static final String EXTRA_OTHER_PACKAGE_NAME = "android.content.pm.extra.OTHER_PACKAGE_NAME";
  public static final String EXTRA_PACKAGE_NAME = "android.content.pm.extra.PACKAGE_NAME";
  @Deprecated
  public static final String EXTRA_PACKAGE_NAMES = "android.content.pm.extra.PACKAGE_NAMES";
  public static final String EXTRA_SESSION_ID = "android.content.pm.extra.SESSION_ID";
  public static final String EXTRA_STATUS = "android.content.pm.extra.STATUS";
  public static final String EXTRA_STATUS_MESSAGE = "android.content.pm.extra.STATUS_MESSAGE";
  public static final String EXTRA_STORAGE_PATH = "android.content.pm.extra.STORAGE_PATH";
  public static final int STATUS_FAILURE = 1;
  public static final int STATUS_FAILURE_ABORTED = 3;
  public static final int STATUS_FAILURE_BLOCKED = 2;
  public static final int STATUS_FAILURE_CONFLICT = 5;
  public static final int STATUS_FAILURE_INCOMPATIBLE = 7;
  public static final int STATUS_FAILURE_INVALID = 4;
  public static final int STATUS_FAILURE_STORAGE = 6;
  public static final int STATUS_PENDING_USER_ACTION = -1;
  public static final int STATUS_SUCCESS = 0;
  private static final String TAG = "PackageInstaller";
  private final Context mContext;
  private final ArrayList<SessionCallbackDelegate> mDelegates = new ArrayList();
  private final IPackageInstaller mInstaller;
  private final String mInstallerPackageName;
  private final PackageManager mPm;
  private final int mUserId;
  
  public PackageInstaller(Context paramContext, PackageManager paramPackageManager, IPackageInstaller paramIPackageInstaller, String paramString, int paramInt)
  {
    this.mContext = paramContext;
    this.mPm = paramPackageManager;
    this.mInstaller = paramIPackageInstaller;
    this.mInstallerPackageName = paramString;
    this.mUserId = paramInt;
  }
  
  public void abandonSession(int paramInt)
  {
    try
    {
      this.mInstaller.abandonSession(paramInt);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  @Deprecated
  public void addSessionCallback(SessionCallback paramSessionCallback)
  {
    registerSessionCallback(paramSessionCallback);
  }
  
  @Deprecated
  public void addSessionCallback(SessionCallback paramSessionCallback, Handler paramHandler)
  {
    registerSessionCallback(paramSessionCallback, paramHandler);
  }
  
  public int createSession(SessionParams paramSessionParams)
    throws IOException
  {
    try
    {
      int i = this.mInstaller.createSession(paramSessionParams, this.mInstallerPackageName, this.mUserId);
      return i;
    }
    catch (RemoteException paramSessionParams)
    {
      throw paramSessionParams.rethrowFromSystemServer();
    }
    catch (RuntimeException paramSessionParams)
    {
      ExceptionUtils.maybeUnwrapIOException(paramSessionParams);
      throw paramSessionParams;
    }
  }
  
  public List<SessionInfo> getAllSessions()
  {
    try
    {
      List localList = this.mInstaller.getAllSessions(this.mUserId).getList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public List<SessionInfo> getMySessions()
  {
    try
    {
      List localList = this.mInstaller.getMySessions(this.mInstallerPackageName, this.mUserId).getList();
      return localList;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public SessionInfo getSessionInfo(int paramInt)
  {
    try
    {
      SessionInfo localSessionInfo = this.mInstaller.getSessionInfo(paramInt);
      return localSessionInfo;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public Session openSession(int paramInt)
    throws IOException
  {
    try
    {
      Session localSession = new Session(this.mInstaller.openSession(paramInt));
      return localSession;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
    catch (RuntimeException localRuntimeException)
    {
      ExceptionUtils.maybeUnwrapIOException(localRuntimeException);
      throw localRuntimeException;
    }
  }
  
  public void registerSessionCallback(SessionCallback paramSessionCallback)
  {
    registerSessionCallback(paramSessionCallback, new Handler());
  }
  
  public void registerSessionCallback(SessionCallback paramSessionCallback, Handler paramHandler)
  {
    synchronized (this.mDelegates)
    {
      paramSessionCallback = new SessionCallbackDelegate(paramSessionCallback, paramHandler.getLooper());
      try
      {
        this.mInstaller.registerCallback(paramSessionCallback, this.mUserId);
        this.mDelegates.add(paramSessionCallback);
        return;
      }
      catch (RemoteException paramSessionCallback)
      {
        throw paramSessionCallback.rethrowFromSystemServer();
      }
    }
  }
  
  @Deprecated
  public void removeSessionCallback(SessionCallback paramSessionCallback)
  {
    unregisterSessionCallback(paramSessionCallback);
  }
  
  public void setPermissionsResult(int paramInt, boolean paramBoolean)
  {
    try
    {
      this.mInstaller.setPermissionsResult(paramInt, paramBoolean);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      throw localRemoteException.rethrowFromSystemServer();
    }
  }
  
  public void uninstall(String paramString, IntentSender paramIntentSender)
  {
    try
    {
      this.mInstaller.uninstall(paramString, this.mInstallerPackageName, 0, paramIntentSender, this.mUserId);
      return;
    }
    catch (RemoteException paramString)
    {
      throw paramString.rethrowFromSystemServer();
    }
  }
  
  public void unregisterSessionCallback(SessionCallback paramSessionCallback)
  {
    Iterator localIterator;
    SessionCallbackDelegate localSessionCallbackDelegate;
    synchronized (this.mDelegates)
    {
      localIterator = this.mDelegates.iterator();
      SessionCallback localSessionCallback;
      do
      {
        if (!localIterator.hasNext()) {
          break;
        }
        localSessionCallbackDelegate = (SessionCallbackDelegate)localIterator.next();
        localSessionCallback = localSessionCallbackDelegate.mCallback;
      } while (localSessionCallback != paramSessionCallback);
    }
  }
  
  public void updateSessionAppIcon(int paramInt, Bitmap paramBitmap)
  {
    try
    {
      this.mInstaller.updateSessionAppIcon(paramInt, paramBitmap);
      return;
    }
    catch (RemoteException paramBitmap)
    {
      throw paramBitmap.rethrowFromSystemServer();
    }
  }
  
  public void updateSessionAppLabel(int paramInt, CharSequence paramCharSequence)
  {
    if (paramCharSequence != null) {}
    for (;;)
    {
      try
      {
        paramCharSequence = paramCharSequence.toString();
        this.mInstaller.updateSessionAppLabel(paramInt, paramCharSequence);
        return;
      }
      catch (RemoteException paramCharSequence)
      {
        throw paramCharSequence.rethrowFromSystemServer();
      }
      paramCharSequence = null;
    }
  }
  
  public static class Session
    implements Closeable
  {
    private IPackageInstallerSession mSession;
    
    public Session(IPackageInstallerSession paramIPackageInstallerSession)
    {
      this.mSession = paramIPackageInstallerSession;
    }
    
    public void abandon()
    {
      try
      {
        this.mSession.abandon();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    public void addProgress(float paramFloat)
    {
      try
      {
        this.mSession.addClientProgress(paramFloat);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    public void close()
    {
      try
      {
        this.mSession.close();
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
    
    public void commit(IntentSender paramIntentSender)
    {
      try
      {
        this.mSession.commit(paramIntentSender);
        return;
      }
      catch (RemoteException paramIntentSender)
      {
        throw paramIntentSender.rethrowFromSystemServer();
      }
    }
    
    public void fsync(OutputStream paramOutputStream)
      throws IOException
    {
      if ((paramOutputStream instanceof FileBridge.FileBridgeOutputStream))
      {
        ((FileBridge.FileBridgeOutputStream)paramOutputStream).fsync();
        return;
      }
      throw new IllegalArgumentException("Unrecognized stream");
    }
    
    public String[] getNames()
      throws IOException
    {
      try
      {
        String[] arrayOfString = this.mSession.getNames();
        return arrayOfString;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
      catch (RuntimeException localRuntimeException)
      {
        ExceptionUtils.maybeUnwrapIOException(localRuntimeException);
        throw localRuntimeException;
      }
    }
    
    public InputStream openRead(String paramString)
      throws IOException
    {
      try
      {
        paramString = new ParcelFileDescriptor.AutoCloseInputStream(this.mSession.openRead(paramString));
        return paramString;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
      catch (RuntimeException paramString)
      {
        ExceptionUtils.maybeUnwrapIOException(paramString);
        throw paramString;
      }
    }
    
    public OutputStream openWrite(String paramString, long paramLong1, long paramLong2)
      throws IOException
    {
      try
      {
        paramString = new FileBridge.FileBridgeOutputStream(this.mSession.openWrite(paramString, paramLong1, paramLong2));
        return paramString;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
      catch (RuntimeException paramString)
      {
        ExceptionUtils.maybeUnwrapIOException(paramString);
        throw paramString;
      }
    }
    
    public void removeSplit(String paramString)
      throws IOException
    {
      try
      {
        this.mSession.removeSplit(paramString);
        return;
      }
      catch (RemoteException paramString)
      {
        throw paramString.rethrowFromSystemServer();
      }
      catch (RuntimeException paramString)
      {
        ExceptionUtils.maybeUnwrapIOException(paramString);
        throw paramString;
      }
    }
    
    @Deprecated
    public void setProgress(float paramFloat)
    {
      setStagingProgress(paramFloat);
    }
    
    public void setStagingProgress(float paramFloat)
    {
      try
      {
        this.mSession.setClientProgress(paramFloat);
        return;
      }
      catch (RemoteException localRemoteException)
      {
        throw localRemoteException.rethrowFromSystemServer();
      }
    }
  }
  
  public static abstract class SessionCallback
  {
    public abstract void onActiveChanged(int paramInt, boolean paramBoolean);
    
    public abstract void onBadgingChanged(int paramInt);
    
    public abstract void onCreated(int paramInt);
    
    public abstract void onFinished(int paramInt, boolean paramBoolean);
    
    public abstract void onProgressChanged(int paramInt, float paramFloat);
  }
  
  private static class SessionCallbackDelegate
    extends IPackageInstallerCallback.Stub
    implements Handler.Callback
  {
    private static final int MSG_SESSION_ACTIVE_CHANGED = 3;
    private static final int MSG_SESSION_BADGING_CHANGED = 2;
    private static final int MSG_SESSION_CREATED = 1;
    private static final int MSG_SESSION_FINISHED = 5;
    private static final int MSG_SESSION_PROGRESS_CHANGED = 4;
    final PackageInstaller.SessionCallback mCallback;
    final Handler mHandler;
    
    public SessionCallbackDelegate(PackageInstaller.SessionCallback paramSessionCallback, Looper paramLooper)
    {
      this.mCallback = paramSessionCallback;
      this.mHandler = new Handler(paramLooper, this);
    }
    
    public boolean handleMessage(Message paramMessage)
    {
      boolean bool = false;
      int i = paramMessage.arg1;
      switch (paramMessage.what)
      {
      default: 
        return false;
      case 1: 
        this.mCallback.onCreated(i);
        return true;
      case 2: 
        this.mCallback.onBadgingChanged(i);
        return true;
      case 3: 
        if (paramMessage.arg2 != 0) {}
        for (bool = true;; bool = false)
        {
          this.mCallback.onActiveChanged(i, bool);
          return true;
        }
      case 4: 
        this.mCallback.onProgressChanged(i, ((Float)paramMessage.obj).floatValue());
        return true;
      }
      PackageInstaller.SessionCallback localSessionCallback = this.mCallback;
      if (paramMessage.arg2 != 0) {
        bool = true;
      }
      localSessionCallback.onFinished(i, bool);
      return true;
    }
    
    public void onSessionActiveChanged(int paramInt, boolean paramBoolean)
    {
      Handler localHandler = this.mHandler;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(3, paramInt, i).sendToTarget();
        return;
      }
    }
    
    public void onSessionBadgingChanged(int paramInt)
    {
      this.mHandler.obtainMessage(2, paramInt, 0).sendToTarget();
    }
    
    public void onSessionCreated(int paramInt)
    {
      this.mHandler.obtainMessage(1, paramInt, 0).sendToTarget();
    }
    
    public void onSessionFinished(int paramInt, boolean paramBoolean)
    {
      Handler localHandler = this.mHandler;
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        localHandler.obtainMessage(5, paramInt, i).sendToTarget();
        return;
      }
    }
    
    public void onSessionProgressChanged(int paramInt, float paramFloat)
    {
      this.mHandler.obtainMessage(4, paramInt, 0, Float.valueOf(paramFloat)).sendToTarget();
    }
  }
  
  public static class SessionInfo
    implements Parcelable
  {
    public static final Parcelable.Creator<SessionInfo> CREATOR = new Parcelable.Creator()
    {
      public PackageInstaller.SessionInfo createFromParcel(Parcel paramAnonymousParcel)
      {
        return new PackageInstaller.SessionInfo(paramAnonymousParcel);
      }
      
      public PackageInstaller.SessionInfo[] newArray(int paramAnonymousInt)
      {
        return new PackageInstaller.SessionInfo[paramAnonymousInt];
      }
    };
    public boolean active;
    public Bitmap appIcon;
    public CharSequence appLabel;
    public String appPackageName;
    public String installerPackageName;
    public int mode;
    public float progress;
    public String resolvedBaseCodePath;
    public boolean sealed;
    public int sessionId;
    public long sizeBytes;
    
    public SessionInfo() {}
    
    public SessionInfo(Parcel paramParcel)
    {
      this.sessionId = paramParcel.readInt();
      this.installerPackageName = paramParcel.readString();
      this.resolvedBaseCodePath = paramParcel.readString();
      this.progress = paramParcel.readFloat();
      if (paramParcel.readInt() != 0)
      {
        bool1 = true;
        this.sealed = bool1;
        if (paramParcel.readInt() == 0) {
          break label116;
        }
      }
      label116:
      for (boolean bool1 = bool2;; bool1 = false)
      {
        this.active = bool1;
        this.mode = paramParcel.readInt();
        this.sizeBytes = paramParcel.readLong();
        this.appPackageName = paramParcel.readString();
        this.appIcon = ((Bitmap)paramParcel.readParcelable(null));
        this.appLabel = paramParcel.readString();
        return;
        bool1 = false;
        break;
      }
    }
    
    public Intent createDetailsIntent()
    {
      Intent localIntent = new Intent("android.content.pm.action.SESSION_DETAILS");
      localIntent.putExtra("android.content.pm.extra.SESSION_ID", this.sessionId);
      localIntent.setPackage(this.installerPackageName);
      localIntent.setFlags(268435456);
      return localIntent;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public Bitmap getAppIcon()
    {
      return this.appIcon;
    }
    
    public CharSequence getAppLabel()
    {
      return this.appLabel;
    }
    
    public String getAppPackageName()
    {
      return this.appPackageName;
    }
    
    @Deprecated
    public Intent getDetailsIntent()
    {
      return createDetailsIntent();
    }
    
    public String getInstallerPackageName()
    {
      return this.installerPackageName;
    }
    
    public float getProgress()
    {
      return this.progress;
    }
    
    public int getSessionId()
    {
      return this.sessionId;
    }
    
    public boolean isActive()
    {
      return this.active;
    }
    
    @Deprecated
    public boolean isOpen()
    {
      return isActive();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int j = 1;
      paramParcel.writeInt(this.sessionId);
      paramParcel.writeString(this.installerPackageName);
      paramParcel.writeString(this.resolvedBaseCodePath);
      paramParcel.writeFloat(this.progress);
      int i;
      if (this.sealed)
      {
        i = 1;
        paramParcel.writeInt(i);
        if (!this.active) {
          break label127;
        }
        i = j;
        label59:
        paramParcel.writeInt(i);
        paramParcel.writeInt(this.mode);
        paramParcel.writeLong(this.sizeBytes);
        paramParcel.writeString(this.appPackageName);
        paramParcel.writeParcelable(this.appIcon, paramInt);
        if (this.appLabel == null) {
          break label132;
        }
      }
      label127:
      label132:
      for (String str = this.appLabel.toString();; str = null)
      {
        paramParcel.writeString(str);
        return;
        i = 0;
        break;
        i = 0;
        break label59;
      }
    }
  }
  
  public static class SessionParams
    implements Parcelable
  {
    public static final Parcelable.Creator<SessionParams> CREATOR = new Parcelable.Creator()
    {
      public PackageInstaller.SessionParams createFromParcel(Parcel paramAnonymousParcel)
      {
        return new PackageInstaller.SessionParams(paramAnonymousParcel);
      }
      
      public PackageInstaller.SessionParams[] newArray(int paramAnonymousInt)
      {
        return new PackageInstaller.SessionParams[paramAnonymousInt];
      }
    };
    public static final int MODE_FULL_INSTALL = 1;
    public static final int MODE_INHERIT_EXISTING = 2;
    public static final int MODE_INVALID = -1;
    public static final int UID_UNKNOWN = -1;
    public String abiOverride;
    public Bitmap appIcon;
    public long appIconLastModified = -1L;
    public String appLabel;
    public String appPackageName;
    public String[] grantedRuntimePermissions;
    public int installFlags;
    public int installLocation = 1;
    public int mode = -1;
    public int originatingUid = -1;
    public Uri originatingUri;
    public Uri referrerUri;
    public long sizeBytes = -1L;
    public String volumeUuid;
    
    public SessionParams(int paramInt)
    {
      this.mode = paramInt;
    }
    
    public SessionParams(Parcel paramParcel)
    {
      this.mode = paramParcel.readInt();
      this.installFlags = paramParcel.readInt();
      this.installLocation = paramParcel.readInt();
      this.sizeBytes = paramParcel.readLong();
      this.appPackageName = paramParcel.readString();
      this.appIcon = ((Bitmap)paramParcel.readParcelable(null));
      this.appLabel = paramParcel.readString();
      this.originatingUri = ((Uri)paramParcel.readParcelable(null));
      this.originatingUid = paramParcel.readInt();
      this.referrerUri = ((Uri)paramParcel.readParcelable(null));
      this.abiOverride = paramParcel.readString();
      this.volumeUuid = paramParcel.readString();
      this.grantedRuntimePermissions = paramParcel.readStringArray();
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public void dump(IndentingPrintWriter paramIndentingPrintWriter)
    {
      paramIndentingPrintWriter.printPair("mode", Integer.valueOf(this.mode));
      paramIndentingPrintWriter.printHexPair("installFlags", this.installFlags);
      paramIndentingPrintWriter.printPair("installLocation", Integer.valueOf(this.installLocation));
      paramIndentingPrintWriter.printPair("sizeBytes", Long.valueOf(this.sizeBytes));
      paramIndentingPrintWriter.printPair("appPackageName", this.appPackageName);
      if (this.appIcon != null) {}
      for (boolean bool = true;; bool = false)
      {
        paramIndentingPrintWriter.printPair("appIcon", Boolean.valueOf(bool));
        paramIndentingPrintWriter.printPair("appLabel", this.appLabel);
        paramIndentingPrintWriter.printPair("originatingUri", this.originatingUri);
        paramIndentingPrintWriter.printPair("originatingUid", Integer.valueOf(this.originatingUid));
        paramIndentingPrintWriter.printPair("referrerUri", this.referrerUri);
        paramIndentingPrintWriter.printPair("abiOverride", this.abiOverride);
        paramIndentingPrintWriter.printPair("volumeUuid", this.volumeUuid);
        paramIndentingPrintWriter.printPair("grantedRuntimePermissions", this.grantedRuntimePermissions);
        paramIndentingPrintWriter.println();
        return;
      }
    }
    
    public void setAllowDowngrade(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.installFlags |= 0x80;
        return;
      }
      this.installFlags &= 0xFF7F;
    }
    
    public void setAppIcon(Bitmap paramBitmap)
    {
      this.appIcon = paramBitmap;
    }
    
    public void setAppLabel(CharSequence paramCharSequence)
    {
      String str = null;
      if (paramCharSequence != null) {
        str = paramCharSequence.toString();
      }
      this.appLabel = str;
    }
    
    public void setAppPackageName(String paramString)
    {
      this.appPackageName = paramString;
    }
    
    public void setDontKillApp(boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.installFlags |= 0x1000;
        return;
      }
      this.installFlags &= 0xEFFF;
    }
    
    public void setGrantedRuntimePermissions(String[] paramArrayOfString)
    {
      this.installFlags |= 0x100;
      this.grantedRuntimePermissions = paramArrayOfString;
    }
    
    public void setInstallFlagsExternal()
    {
      this.installFlags |= 0x8;
      this.installFlags &= 0xFFFFFFEF;
    }
    
    public void setInstallFlagsForcePermissionPrompt()
    {
      this.installFlags |= 0x400;
    }
    
    public void setInstallFlagsInternal()
    {
      this.installFlags |= 0x10;
      this.installFlags &= 0xFFFFFFF7;
    }
    
    public void setInstallLocation(int paramInt)
    {
      this.installLocation = paramInt;
    }
    
    public void setOriginatingUid(int paramInt)
    {
      this.originatingUid = paramInt;
    }
    
    public void setOriginatingUri(Uri paramUri)
    {
      this.originatingUri = paramUri;
    }
    
    public void setReferrerUri(Uri paramUri)
    {
      this.referrerUri = paramUri;
    }
    
    public void setSize(long paramLong)
    {
      this.sizeBytes = paramLong;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mode);
      paramParcel.writeInt(this.installFlags);
      paramParcel.writeInt(this.installLocation);
      paramParcel.writeLong(this.sizeBytes);
      paramParcel.writeString(this.appPackageName);
      paramParcel.writeParcelable(this.appIcon, paramInt);
      paramParcel.writeString(this.appLabel);
      paramParcel.writeParcelable(this.originatingUri, paramInt);
      paramParcel.writeInt(this.originatingUid);
      paramParcel.writeParcelable(this.referrerUri, paramInt);
      paramParcel.writeString(this.abiOverride);
      paramParcel.writeString(this.volumeUuid);
      paramParcel.writeStringArray(this.grantedRuntimePermissions);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/pm/PackageInstaller.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */