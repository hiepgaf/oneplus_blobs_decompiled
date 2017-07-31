package android.media.browse;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ParceledListSlice;
import android.media.MediaDescription;
import android.media.session.MediaSession.Token;
import android.os.BaseBundle;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.service.media.IMediaBrowserService;
import android.service.media.IMediaBrowserService.Stub;
import android.service.media.IMediaBrowserServiceCallbacks;
import android.service.media.IMediaBrowserServiceCallbacks.Stub;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public final class MediaBrowser
{
  private static final int CONNECT_STATE_CONNECTED = 2;
  private static final int CONNECT_STATE_CONNECTING = 1;
  private static final int CONNECT_STATE_DISCONNECTED = 0;
  private static final int CONNECT_STATE_SUSPENDED = 3;
  private static final boolean DBG = false;
  public static final String EXTRA_PAGE = "android.media.browse.extra.PAGE";
  public static final String EXTRA_PAGE_SIZE = "android.media.browse.extra.PAGE_SIZE";
  private static final String TAG = "MediaBrowser";
  private final ConnectionCallback mCallback;
  private final Context mContext;
  private Bundle mExtras;
  private final Handler mHandler = new Handler();
  private MediaSession.Token mMediaSessionToken;
  private final Bundle mRootHints;
  private String mRootId;
  private IMediaBrowserService mServiceBinder;
  private IMediaBrowserServiceCallbacks mServiceCallbacks;
  private final ComponentName mServiceComponent;
  private MediaServiceConnection mServiceConnection;
  private int mState = 0;
  private final ArrayMap<String, Subscription> mSubscriptions = new ArrayMap();
  
  public MediaBrowser(Context paramContext, ComponentName paramComponentName, ConnectionCallback paramConnectionCallback, Bundle paramBundle)
  {
    if (paramContext == null) {
      throw new IllegalArgumentException("context must not be null");
    }
    if (paramComponentName == null) {
      throw new IllegalArgumentException("service component must not be null");
    }
    if (paramConnectionCallback == null) {
      throw new IllegalArgumentException("connection callback must not be null");
    }
    this.mContext = paramContext;
    this.mServiceComponent = paramComponentName;
    this.mCallback = paramConnectionCallback;
    if (paramBundle == null) {}
    for (paramContext = (Context)localObject;; paramContext = new Bundle(paramBundle))
    {
      this.mRootHints = paramContext;
      return;
    }
  }
  
  private void forceCloseConnection()
  {
    if (this.mServiceConnection != null) {
      this.mContext.unbindService(this.mServiceConnection);
    }
    this.mState = 0;
    this.mServiceConnection = null;
    this.mServiceBinder = null;
    this.mServiceCallbacks = null;
    this.mRootId = null;
    this.mMediaSessionToken = null;
  }
  
  private ServiceCallbacks getNewServiceCallbacks()
  {
    return new ServiceCallbacks(this);
  }
  
  private static String getStateLabel(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return "UNKNOWN/" + paramInt;
    case 0: 
      return "CONNECT_STATE_DISCONNECTED";
    case 1: 
      return "CONNECT_STATE_CONNECTING";
    case 2: 
      return "CONNECT_STATE_CONNECTED";
    }
    return "CONNECT_STATE_SUSPENDED";
  }
  
  private boolean isCurrent(IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks, String paramString)
  {
    if (this.mServiceCallbacks != paramIMediaBrowserServiceCallbacks)
    {
      if (this.mState != 0) {
        Log.i("MediaBrowser", paramString + " for " + this.mServiceComponent + " with mServiceConnection=" + this.mServiceCallbacks + " this=" + this);
      }
      return false;
    }
    return true;
  }
  
  private final void onConnectionFailed(final IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        Log.e("MediaBrowser", "onConnectFailed for " + MediaBrowser.-get6(MediaBrowser.this));
        if (!MediaBrowser.-wrap1(MediaBrowser.this, paramIMediaBrowserServiceCallbacks, "onConnectFailed")) {
          return;
        }
        if (MediaBrowser.-get8(MediaBrowser.this) != 1)
        {
          Log.w("MediaBrowser", "onConnect from service while mState=" + MediaBrowser.-wrap2(MediaBrowser.-get8(MediaBrowser.this)) + "... ignoring");
          return;
        }
        MediaBrowser.-wrap3(MediaBrowser.this);
        MediaBrowser.-get0(MediaBrowser.this).onConnectionFailed();
      }
    });
  }
  
  private final void onLoadChildren(final IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks, final String paramString, final ParceledListSlice paramParceledListSlice, final Bundle paramBundle)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        if (!MediaBrowser.-wrap1(MediaBrowser.this, paramIMediaBrowserServiceCallbacks, "onLoadChildren")) {
          return;
        }
        Object localObject = (MediaBrowser.Subscription)MediaBrowser.-get9(MediaBrowser.this).get(paramString);
        if (localObject != null)
        {
          MediaBrowser.SubscriptionCallback localSubscriptionCallback = ((MediaBrowser.Subscription)localObject).getCallback(paramBundle);
          if (localSubscriptionCallback != null)
          {
            if (paramParceledListSlice == null) {
              localObject = null;
            }
            while (paramBundle == null) {
              if (localObject == null)
              {
                localSubscriptionCallback.onError(paramString);
                return;
                localObject = paramParceledListSlice.getList();
              }
              else
              {
                localSubscriptionCallback.onChildrenLoaded(paramString, (List)localObject);
                return;
              }
            }
            if (localObject == null)
            {
              localSubscriptionCallback.onError(paramString, paramBundle);
              return;
            }
            localSubscriptionCallback.onChildrenLoaded(paramString, (List)localObject, paramBundle);
            return;
          }
        }
      }
    });
  }
  
  private final void onServiceConnected(final IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks, final String paramString, final MediaSession.Token paramToken, final Bundle paramBundle)
  {
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        if (!MediaBrowser.-wrap1(MediaBrowser.this, paramIMediaBrowserServiceCallbacks, "onConnect")) {
          return;
        }
        if (MediaBrowser.-get8(MediaBrowser.this) != 1)
        {
          Log.w("MediaBrowser", "onConnect from service while mState=" + MediaBrowser.-wrap2(MediaBrowser.-get8(MediaBrowser.this)) + "... ignoring");
          return;
        }
        MediaBrowser.-set2(MediaBrowser.this, paramString);
        MediaBrowser.-set1(MediaBrowser.this, paramToken);
        MediaBrowser.-set0(MediaBrowser.this, paramBundle);
        MediaBrowser.-set5(MediaBrowser.this, 2);
        MediaBrowser.-get0(MediaBrowser.this).onConnected();
        Iterator localIterator = MediaBrowser.-get9(MediaBrowser.this).entrySet().iterator();
        if (localIterator.hasNext())
        {
          Object localObject1 = (Map.Entry)localIterator.next();
          String str = (String)((Map.Entry)localObject1).getKey();
          Object localObject2 = (MediaBrowser.Subscription)((Map.Entry)localObject1).getValue();
          localObject1 = ((MediaBrowser.Subscription)localObject2).getCallbacks();
          localObject2 = ((MediaBrowser.Subscription)localObject2).getOptionsList();
          int i = 0;
          while (i < ((List)localObject1).size()) {
            try
            {
              MediaBrowser.-get4(MediaBrowser.this).addSubscription(str, ((MediaBrowser.SubscriptionCallback)((List)localObject1).get(i)).mToken, (Bundle)((List)localObject2).get(i), MediaBrowser.-get5(MediaBrowser.this));
              i += 1;
            }
            catch (RemoteException localRemoteException)
            {
              for (;;)
              {
                Log.d("MediaBrowser", "addSubscription failed with RemoteException parentId=" + str);
              }
            }
          }
        }
      }
    });
  }
  
  private void subscribeInternal(String paramString, Bundle paramBundle, SubscriptionCallback paramSubscriptionCallback)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("parentId is empty.");
    }
    if (paramSubscriptionCallback == null) {
      throw new IllegalArgumentException("callback is null");
    }
    Subscription localSubscription2 = (Subscription)this.mSubscriptions.get(paramString);
    Subscription localSubscription1 = localSubscription2;
    if (localSubscription2 == null)
    {
      localSubscription1 = new Subscription();
      this.mSubscriptions.put(paramString, localSubscription1);
    }
    localSubscription1.putCallback(paramBundle, paramSubscriptionCallback);
    if ((this.mState != 2) || (paramBundle == null)) {}
    try
    {
      this.mServiceBinder.addSubscriptionDeprecated(paramString, this.mServiceCallbacks);
      this.mServiceBinder.addSubscription(paramString, paramSubscriptionCallback.mToken, paramBundle, this.mServiceCallbacks);
      return;
    }
    catch (RemoteException paramBundle)
    {
      Log.d("MediaBrowser", "addSubscription failed with RemoteException parentId=" + paramString);
    }
  }
  
  private void unsubscribeInternal(String paramString, SubscriptionCallback paramSubscriptionCallback)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("parentId is empty.");
    }
    Subscription localSubscription = (Subscription)this.mSubscriptions.get(paramString);
    if (localSubscription == null) {
      return;
    }
    if (paramSubscriptionCallback == null) {}
    try
    {
      if (this.mState == 2)
      {
        this.mServiceBinder.removeSubscriptionDeprecated(paramString, this.mServiceCallbacks);
        this.mServiceBinder.removeSubscription(paramString, null, this.mServiceCallbacks);
      }
      for (;;)
      {
        if ((localSubscription.isEmpty()) || (paramSubscriptionCallback == null)) {
          this.mSubscriptions.remove(paramString);
        }
        return;
        List localList1 = localSubscription.getCallbacks();
        List localList2 = localSubscription.getOptionsList();
        int i = localList1.size() - 1;
        while (i >= 0)
        {
          if (localList1.get(i) == paramSubscriptionCallback)
          {
            if (this.mState == 2) {
              this.mServiceBinder.removeSubscription(paramString, paramSubscriptionCallback.mToken, this.mServiceCallbacks);
            }
            localList1.remove(i);
            localList2.remove(i);
          }
          i -= 1;
        }
      }
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.d("MediaBrowser", "removeSubscription failed with RemoteException parentId=" + paramString);
      }
    }
  }
  
  public void connect()
  {
    if (this.mState != 0) {
      throw new IllegalStateException("connect() called while not disconnected (state=" + getStateLabel(this.mState) + ")");
    }
    if (this.mServiceBinder != null) {
      throw new RuntimeException("mServiceBinder should be null. Instead it is " + this.mServiceBinder);
    }
    if (this.mServiceCallbacks != null) {
      throw new RuntimeException("mServiceCallbacks should be null. Instead it is " + this.mServiceCallbacks);
    }
    this.mState = 1;
    Intent localIntent = new Intent("android.media.browse.MediaBrowserService");
    localIntent.setComponent(this.mServiceComponent);
    final MediaServiceConnection localMediaServiceConnection = new MediaServiceConnection(null);
    this.mServiceConnection = localMediaServiceConnection;
    int i = 0;
    try
    {
      boolean bool = this.mContext.bindService(localIntent, this.mServiceConnection, 1);
      i = bool;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.e("MediaBrowser", "Failed binding to service " + this.mServiceComponent);
      }
    }
    if (i == 0) {
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          if (localMediaServiceConnection == MediaBrowser.-get7(MediaBrowser.this))
          {
            MediaBrowser.-wrap3(MediaBrowser.this);
            MediaBrowser.-get0(MediaBrowser.this).onConnectionFailed();
          }
        }
      });
    }
  }
  
  public void disconnect()
  {
    if (this.mServiceCallbacks != null) {}
    try
    {
      this.mServiceBinder.disconnect(this.mServiceCallbacks);
      forceCloseConnection();
      return;
    }
    catch (RemoteException localRemoteException)
    {
      for (;;)
      {
        Log.w("MediaBrowser", "RemoteException during connect for " + this.mServiceComponent);
      }
    }
  }
  
  void dump()
  {
    Log.d("MediaBrowser", "MediaBrowser...");
    Log.d("MediaBrowser", "  mServiceComponent=" + this.mServiceComponent);
    Log.d("MediaBrowser", "  mCallback=" + this.mCallback);
    Log.d("MediaBrowser", "  mRootHints=" + this.mRootHints);
    Log.d("MediaBrowser", "  mState=" + getStateLabel(this.mState));
    Log.d("MediaBrowser", "  mServiceConnection=" + this.mServiceConnection);
    Log.d("MediaBrowser", "  mServiceBinder=" + this.mServiceBinder);
    Log.d("MediaBrowser", "  mServiceCallbacks=" + this.mServiceCallbacks);
    Log.d("MediaBrowser", "  mRootId=" + this.mRootId);
    Log.d("MediaBrowser", "  mMediaSessionToken=" + this.mMediaSessionToken);
  }
  
  public Bundle getExtras()
  {
    if (!isConnected()) {
      throw new IllegalStateException("getExtras() called while not connected (state=" + getStateLabel(this.mState) + ")");
    }
    return this.mExtras;
  }
  
  public void getItem(final String paramString, final ItemCallback paramItemCallback)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("mediaId is empty.");
    }
    if (paramItemCallback == null) {
      throw new IllegalArgumentException("cb is null.");
    }
    if (this.mState != 2)
    {
      Log.i("MediaBrowser", "Not connected, unable to retrieve the MediaItem.");
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          paramItemCallback.onError(paramString);
        }
      });
      return;
    }
    ResultReceiver local3 = new ResultReceiver(this.mHandler)
    {
      protected void onReceiveResult(int paramAnonymousInt, Bundle paramAnonymousBundle)
      {
        if ((paramAnonymousInt != 0) || (paramAnonymousBundle == null)) {}
        while (!paramAnonymousBundle.containsKey("media_item"))
        {
          paramItemCallback.onError(paramString);
          return;
        }
        paramAnonymousBundle = paramAnonymousBundle.getParcelable("media_item");
        if (!(paramAnonymousBundle instanceof MediaBrowser.MediaItem))
        {
          paramItemCallback.onError(paramString);
          return;
        }
        paramItemCallback.onItemLoaded(paramAnonymousBundle);
      }
    };
    try
    {
      this.mServiceBinder.getMediaItem(paramString, local3, this.mServiceCallbacks);
      return;
    }
    catch (RemoteException localRemoteException)
    {
      Log.i("MediaBrowser", "Remote error getting media item.");
      this.mHandler.post(new Runnable()
      {
        public void run()
        {
          paramItemCallback.onError(paramString);
        }
      });
    }
  }
  
  public String getRoot()
  {
    if (!isConnected()) {
      throw new IllegalStateException("getRoot() called while not connected (state=" + getStateLabel(this.mState) + ")");
    }
    return this.mRootId;
  }
  
  public ComponentName getServiceComponent()
  {
    if (!isConnected()) {
      throw new IllegalStateException("getServiceComponent() called while not connected (state=" + this.mState + ")");
    }
    return this.mServiceComponent;
  }
  
  public MediaSession.Token getSessionToken()
  {
    if (!isConnected()) {
      throw new IllegalStateException("getSessionToken() called while not connected (state=" + this.mState + ")");
    }
    return this.mMediaSessionToken;
  }
  
  public boolean isConnected()
  {
    return this.mState == 2;
  }
  
  public void subscribe(String paramString, SubscriptionCallback paramSubscriptionCallback)
  {
    subscribeInternal(paramString, null, paramSubscriptionCallback);
  }
  
  public void subscribe(String paramString, Bundle paramBundle, SubscriptionCallback paramSubscriptionCallback)
  {
    if (paramBundle == null) {
      throw new IllegalArgumentException("options are null");
    }
    subscribeInternal(paramString, new Bundle(paramBundle), paramSubscriptionCallback);
  }
  
  public void unsubscribe(String paramString)
  {
    unsubscribeInternal(paramString, null);
  }
  
  public void unsubscribe(String paramString, SubscriptionCallback paramSubscriptionCallback)
  {
    if (paramSubscriptionCallback == null) {
      throw new IllegalArgumentException("callback is null");
    }
    unsubscribeInternal(paramString, paramSubscriptionCallback);
  }
  
  public static class ConnectionCallback
  {
    public void onConnected() {}
    
    public void onConnectionFailed() {}
    
    public void onConnectionSuspended() {}
  }
  
  public static abstract class ItemCallback
  {
    public void onError(String paramString) {}
    
    public void onItemLoaded(MediaBrowser.MediaItem paramMediaItem) {}
  }
  
  public static class MediaItem
    implements Parcelable
  {
    public static final Parcelable.Creator<MediaItem> CREATOR = new Parcelable.Creator()
    {
      public MediaBrowser.MediaItem createFromParcel(Parcel paramAnonymousParcel)
      {
        return new MediaBrowser.MediaItem(paramAnonymousParcel, null);
      }
      
      public MediaBrowser.MediaItem[] newArray(int paramAnonymousInt)
      {
        return new MediaBrowser.MediaItem[paramAnonymousInt];
      }
    };
    public static final int FLAG_BROWSABLE = 1;
    public static final int FLAG_PLAYABLE = 2;
    private final MediaDescription mDescription;
    private final int mFlags;
    
    public MediaItem(MediaDescription paramMediaDescription, int paramInt)
    {
      if (paramMediaDescription == null) {
        throw new IllegalArgumentException("description cannot be null");
      }
      if (TextUtils.isEmpty(paramMediaDescription.getMediaId())) {
        throw new IllegalArgumentException("description must have a non-empty media id");
      }
      this.mFlags = paramInt;
      this.mDescription = paramMediaDescription;
    }
    
    private MediaItem(Parcel paramParcel)
    {
      this.mFlags = paramParcel.readInt();
      this.mDescription = ((MediaDescription)MediaDescription.CREATOR.createFromParcel(paramParcel));
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public MediaDescription getDescription()
    {
      return this.mDescription;
    }
    
    public int getFlags()
    {
      return this.mFlags;
    }
    
    public String getMediaId()
    {
      return this.mDescription.getMediaId();
    }
    
    public boolean isBrowsable()
    {
      boolean bool = false;
      if ((this.mFlags & 0x1) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public boolean isPlayable()
    {
      boolean bool = false;
      if ((this.mFlags & 0x2) != 0) {
        bool = true;
      }
      return bool;
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder("MediaItem{");
      localStringBuilder.append("mFlags=").append(this.mFlags);
      localStringBuilder.append(", mDescription=").append(this.mDescription);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mFlags);
      this.mDescription.writeToParcel(paramParcel, paramInt);
    }
  }
  
  private class MediaServiceConnection
    implements ServiceConnection
  {
    private MediaServiceConnection() {}
    
    private boolean isCurrent(String paramString)
    {
      if (MediaBrowser.-get7(MediaBrowser.this) != this)
      {
        if (MediaBrowser.-get8(MediaBrowser.this) != 0) {
          Log.i("MediaBrowser", paramString + " for " + MediaBrowser.-get6(MediaBrowser.this) + " with mServiceConnection=" + MediaBrowser.-get7(MediaBrowser.this) + " this=" + this);
        }
        return false;
      }
      return true;
    }
    
    private void postOrRun(Runnable paramRunnable)
    {
      if (Thread.currentThread() == MediaBrowser.-get2(MediaBrowser.this).getLooper().getThread())
      {
        paramRunnable.run();
        return;
      }
      MediaBrowser.-get2(MediaBrowser.this).post(paramRunnable);
    }
    
    public void onServiceConnected(final ComponentName paramComponentName, final IBinder paramIBinder)
    {
      postOrRun(new Runnable()
      {
        public void run()
        {
          if (!MediaBrowser.MediaServiceConnection.-wrap0(MediaBrowser.MediaServiceConnection.this, "onServiceConnected")) {
            return;
          }
          MediaBrowser.-set3(MediaBrowser.this, IMediaBrowserService.Stub.asInterface(paramIBinder));
          MediaBrowser.-set4(MediaBrowser.this, MediaBrowser.-wrap0(MediaBrowser.this));
          MediaBrowser.-set5(MediaBrowser.this, 1);
          try
          {
            MediaBrowser.-get4(MediaBrowser.this).connect(MediaBrowser.-get1(MediaBrowser.this).getPackageName(), MediaBrowser.-get3(MediaBrowser.this), MediaBrowser.-get5(MediaBrowser.this));
            return;
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("MediaBrowser", "RemoteException during connect for " + MediaBrowser.-get6(MediaBrowser.this));
          }
        }
      });
    }
    
    public void onServiceDisconnected(final ComponentName paramComponentName)
    {
      postOrRun(new Runnable()
      {
        public void run()
        {
          if (!MediaBrowser.MediaServiceConnection.-wrap0(MediaBrowser.MediaServiceConnection.this, "onServiceDisconnected")) {
            return;
          }
          MediaBrowser.-set3(MediaBrowser.this, null);
          MediaBrowser.-set4(MediaBrowser.this, null);
          MediaBrowser.-set5(MediaBrowser.this, 3);
          MediaBrowser.-get0(MediaBrowser.this).onConnectionSuspended();
        }
      });
    }
  }
  
  private static class ServiceCallbacks
    extends IMediaBrowserServiceCallbacks.Stub
  {
    private WeakReference<MediaBrowser> mMediaBrowser;
    
    public ServiceCallbacks(MediaBrowser paramMediaBrowser)
    {
      this.mMediaBrowser = new WeakReference(paramMediaBrowser);
    }
    
    public void onConnect(String paramString, MediaSession.Token paramToken, Bundle paramBundle)
    {
      MediaBrowser localMediaBrowser = (MediaBrowser)this.mMediaBrowser.get();
      if (localMediaBrowser != null) {
        MediaBrowser.-wrap6(localMediaBrowser, this, paramString, paramToken, paramBundle);
      }
    }
    
    public void onConnectFailed()
    {
      MediaBrowser localMediaBrowser = (MediaBrowser)this.mMediaBrowser.get();
      if (localMediaBrowser != null) {
        MediaBrowser.-wrap4(localMediaBrowser, this);
      }
    }
    
    public void onLoadChildren(String paramString, ParceledListSlice paramParceledListSlice)
    {
      onLoadChildrenWithOptions(paramString, paramParceledListSlice, null);
    }
    
    public void onLoadChildrenWithOptions(String paramString, ParceledListSlice paramParceledListSlice, Bundle paramBundle)
    {
      MediaBrowser localMediaBrowser = (MediaBrowser)this.mMediaBrowser.get();
      if (localMediaBrowser != null) {
        MediaBrowser.-wrap5(localMediaBrowser, this, paramString, paramParceledListSlice, paramBundle);
      }
    }
  }
  
  private static class Subscription
  {
    private final List<MediaBrowser.SubscriptionCallback> mCallbacks = new ArrayList();
    private final List<Bundle> mOptionsList = new ArrayList();
    
    public MediaBrowser.SubscriptionCallback getCallback(Bundle paramBundle)
    {
      int i = 0;
      while (i < this.mOptionsList.size())
      {
        if (MediaBrowserUtils.areSameOptions((Bundle)this.mOptionsList.get(i), paramBundle)) {
          return (MediaBrowser.SubscriptionCallback)this.mCallbacks.get(i);
        }
        i += 1;
      }
      return null;
    }
    
    public List<MediaBrowser.SubscriptionCallback> getCallbacks()
    {
      return this.mCallbacks;
    }
    
    public List<Bundle> getOptionsList()
    {
      return this.mOptionsList;
    }
    
    public boolean isEmpty()
    {
      return this.mCallbacks.isEmpty();
    }
    
    public void putCallback(Bundle paramBundle, MediaBrowser.SubscriptionCallback paramSubscriptionCallback)
    {
      int i = 0;
      while (i < this.mOptionsList.size())
      {
        if (MediaBrowserUtils.areSameOptions((Bundle)this.mOptionsList.get(i), paramBundle))
        {
          this.mCallbacks.set(i, paramSubscriptionCallback);
          return;
        }
        i += 1;
      }
      this.mCallbacks.add(paramSubscriptionCallback);
      this.mOptionsList.add(paramBundle);
    }
  }
  
  public static abstract class SubscriptionCallback
  {
    Binder mToken = new Binder();
    
    public void onChildrenLoaded(String paramString, List<MediaBrowser.MediaItem> paramList) {}
    
    public void onChildrenLoaded(String paramString, List<MediaBrowser.MediaItem> paramList, Bundle paramBundle) {}
    
    public void onError(String paramString) {}
    
    public void onError(String paramString, Bundle paramBundle) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/browse/MediaBrowser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */