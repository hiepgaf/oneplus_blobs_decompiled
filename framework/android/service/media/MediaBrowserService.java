package android.service.media;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.media.browse.MediaBrowser.MediaItem;
import android.media.browse.MediaBrowserUtils;
import android.media.session.MediaSession.Token;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public abstract class MediaBrowserService
  extends Service
{
  private static final boolean DBG = false;
  public static final String KEY_MEDIA_ITEM = "media_item";
  private static final int RESULT_FLAG_OPTION_NOT_HANDLED = 1;
  public static final String SERVICE_INTERFACE = "android.media.browse.MediaBrowserService";
  private static final String TAG = "MediaBrowserService";
  private ServiceBinder mBinder;
  private final ArrayMap<IBinder, ConnectionRecord> mConnections = new ArrayMap();
  private ConnectionRecord mCurConnection;
  private final Handler mHandler = new Handler();
  MediaSession.Token mSession;
  
  private void addSubscription(String paramString, ConnectionRecord paramConnectionRecord, IBinder paramIBinder, Bundle paramBundle)
  {
    Object localObject2 = (List)paramConnectionRecord.subscriptions.get(paramString);
    Object localObject1 = localObject2;
    if (localObject2 == null) {
      localObject1 = new ArrayList();
    }
    localObject2 = ((Iterable)localObject1).iterator();
    while (((Iterator)localObject2).hasNext())
    {
      Pair localPair = (Pair)((Iterator)localObject2).next();
      if ((paramIBinder == localPair.first) && (MediaBrowserUtils.areSameOptions(paramBundle, (Bundle)localPair.second))) {
        return;
      }
    }
    ((List)localObject1).add(new Pair(paramIBinder, paramBundle));
    paramConnectionRecord.subscriptions.put(paramString, localObject1);
    performLoadChildren(paramString, paramConnectionRecord, paramBundle);
  }
  
  private List<MediaBrowser.MediaItem> applyOptions(List<MediaBrowser.MediaItem> paramList, Bundle paramBundle)
  {
    if (paramList == null) {
      return null;
    }
    int i = paramBundle.getInt("android.media.browse.extra.PAGE", -1);
    int m = paramBundle.getInt("android.media.browse.extra.PAGE_SIZE", -1);
    if ((i == -1) && (m == -1)) {
      return paramList;
    }
    int k = m * i;
    int j = k + m;
    if ((i < 0) || (m < 1)) {}
    while (k >= paramList.size()) {
      return Collections.EMPTY_LIST;
    }
    i = j;
    if (j > paramList.size()) {
      i = paramList.size();
    }
    return paramList.subList(k, i);
  }
  
  private boolean isValidPackage(String paramString, int paramInt)
  {
    if (paramString == null) {
      return false;
    }
    String[] arrayOfString = getPackageManager().getPackagesForUid(paramInt);
    int i = arrayOfString.length;
    paramInt = 0;
    while (paramInt < i)
    {
      if (arrayOfString[paramInt].equals(paramString)) {
        return true;
      }
      paramInt += 1;
    }
    return false;
  }
  
  private void notifyChildrenChangedInternal(final String paramString, final Bundle paramBundle)
  {
    if (paramString == null) {
      throw new IllegalArgumentException("parentId cannot be null in notifyChildrenChanged");
    }
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        Iterator localIterator = MediaBrowserService.-get0(MediaBrowserService.this).keySet().iterator();
        while (localIterator.hasNext())
        {
          Object localObject1 = (IBinder)localIterator.next();
          localObject1 = (MediaBrowserService.ConnectionRecord)MediaBrowserService.-get0(MediaBrowserService.this).get(localObject1);
          Object localObject2 = (List)((MediaBrowserService.ConnectionRecord)localObject1).subscriptions.get(paramString);
          if (localObject2 != null)
          {
            localObject2 = ((Iterable)localObject2).iterator();
            while (((Iterator)localObject2).hasNext())
            {
              Pair localPair = (Pair)((Iterator)localObject2).next();
              if (MediaBrowserUtils.hasDuplicatedItems(paramBundle, (Bundle)localPair.second)) {
                MediaBrowserService.-wrap4(MediaBrowserService.this, paramString, (MediaBrowserService.ConnectionRecord)localObject1, (Bundle)localPair.second);
              }
            }
          }
        }
      }
    });
  }
  
  private void performLoadChildren(final String paramString, final ConnectionRecord paramConnectionRecord, final Bundle paramBundle)
  {
    Result local3 = new Result(this, paramString)
    {
      void onResultSent(List<MediaBrowser.MediaItem> paramAnonymousList, int paramAnonymousInt)
      {
        if (MediaBrowserService.-get0(jdField_this).get(paramConnectionRecord.callbacks.asBinder()) != paramConnectionRecord) {
          return;
        }
        if ((paramAnonymousInt & 0x1) != 0)
        {
          paramAnonymousList = MediaBrowserService.-wrap2(jdField_this, paramAnonymousList, paramBundle);
          if (paramAnonymousList != null) {
            break label80;
          }
        }
        for (paramAnonymousList = null;; paramAnonymousList = new ParceledListSlice(paramAnonymousList))
        {
          try
          {
            paramConnectionRecord.callbacks.onLoadChildrenWithOptions(paramString, paramAnonymousList, paramBundle);
            return;
          }
          catch (RemoteException paramAnonymousList)
          {
            label80:
            Log.w("MediaBrowserService", "Calling onLoadChildren() failed for id=" + paramString + " package=" + paramConnectionRecord.pkg);
          }
          break;
        }
      }
    };
    this.mCurConnection = paramConnectionRecord;
    if (paramBundle == null) {
      onLoadChildren(paramString, local3);
    }
    for (;;)
    {
      this.mCurConnection = null;
      if (local3.isDone()) {
        break;
      }
      throw new IllegalStateException("onLoadChildren must call detach() or sendResult() before returning for package=" + paramConnectionRecord.pkg + " id=" + paramString);
      onLoadChildren(paramString, local3, paramBundle);
    }
  }
  
  private void performLoadItem(String paramString, ConnectionRecord paramConnectionRecord, final ResultReceiver paramResultReceiver)
  {
    paramResultReceiver = new Result(this, paramString)
    {
      void onResultSent(MediaBrowser.MediaItem paramAnonymousMediaItem, int paramAnonymousInt)
      {
        Bundle localBundle = new Bundle();
        localBundle.putParcelable("media_item", paramAnonymousMediaItem);
        paramResultReceiver.send(0, localBundle);
      }
    };
    this.mCurConnection = paramConnectionRecord;
    onLoadItem(paramString, paramResultReceiver);
    this.mCurConnection = null;
    if (!paramResultReceiver.isDone()) {
      throw new IllegalStateException("onLoadItem must call detach() or sendResult() before returning for id=" + paramString);
    }
  }
  
  private boolean removeSubscription(String paramString, ConnectionRecord paramConnectionRecord, IBinder paramIBinder)
  {
    boolean bool1 = false;
    if (paramIBinder == null)
    {
      if (paramConnectionRecord.subscriptions.remove(paramString) != null) {
        bool1 = true;
      }
      return bool1;
    }
    boolean bool2 = false;
    bool1 = false;
    List localList = (List)paramConnectionRecord.subscriptions.get(paramString);
    if (localList != null)
    {
      Iterator localIterator = localList.iterator();
      while (localIterator.hasNext())
      {
        Pair localPair = (Pair)localIterator.next();
        if (paramIBinder == localPair.first)
        {
          bool1 = true;
          localList.remove(localPair);
        }
      }
      bool2 = bool1;
      if (localList.size() == 0)
      {
        paramConnectionRecord.subscriptions.remove(paramString);
        bool2 = bool1;
      }
    }
    return bool2;
  }
  
  public void dump(FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  public final Bundle getBrowserRootHints()
  {
    if (this.mCurConnection == null) {
      throw new IllegalStateException("This should be called inside of onLoadChildren or onLoadItem methods");
    }
    if (this.mCurConnection.rootHints == null) {
      return null;
    }
    return new Bundle(this.mCurConnection.rootHints);
  }
  
  public MediaSession.Token getSessionToken()
  {
    return this.mSession;
  }
  
  public void notifyChildrenChanged(String paramString)
  {
    notifyChildrenChangedInternal(paramString, null);
  }
  
  public void notifyChildrenChanged(String paramString, Bundle paramBundle)
  {
    if (paramBundle == null) {
      throw new IllegalArgumentException("options cannot be null in notifyChildrenChanged");
    }
    notifyChildrenChangedInternal(paramString, paramBundle);
  }
  
  public IBinder onBind(Intent paramIntent)
  {
    if ("android.media.browse.MediaBrowserService".equals(paramIntent.getAction())) {
      return this.mBinder;
    }
    return null;
  }
  
  public void onCreate()
  {
    super.onCreate();
    this.mBinder = new ServiceBinder(null);
  }
  
  public abstract BrowserRoot onGetRoot(String paramString, int paramInt, Bundle paramBundle);
  
  public abstract void onLoadChildren(String paramString, Result<List<MediaBrowser.MediaItem>> paramResult);
  
  public void onLoadChildren(String paramString, Result<List<MediaBrowser.MediaItem>> paramResult, Bundle paramBundle)
  {
    paramResult.setFlags(1);
    onLoadChildren(paramString, paramResult);
  }
  
  public void onLoadItem(String paramString, Result<MediaBrowser.MediaItem> paramResult)
  {
    paramResult.sendResult(null);
  }
  
  public void setSessionToken(final MediaSession.Token paramToken)
  {
    if (paramToken == null) {
      throw new IllegalArgumentException("Session token may not be null.");
    }
    if (this.mSession != null) {
      throw new IllegalStateException("The session token has already been set.");
    }
    this.mSession = paramToken;
    this.mHandler.post(new Runnable()
    {
      public void run()
      {
        Iterator localIterator = MediaBrowserService.-get0(MediaBrowserService.this).keySet().iterator();
        while (localIterator.hasNext())
        {
          IBinder localIBinder = (IBinder)localIterator.next();
          MediaBrowserService.ConnectionRecord localConnectionRecord = (MediaBrowserService.ConnectionRecord)MediaBrowserService.-get0(MediaBrowserService.this).get(localIBinder);
          try
          {
            localConnectionRecord.callbacks.onConnect(localConnectionRecord.root.getRootId(), paramToken, localConnectionRecord.root.getExtras());
          }
          catch (RemoteException localRemoteException)
          {
            Log.w("MediaBrowserService", "Connection for " + localConnectionRecord.pkg + " is no longer valid.");
            MediaBrowserService.-get0(MediaBrowserService.this).remove(localIBinder);
          }
        }
      }
    });
  }
  
  public static final class BrowserRoot
  {
    public static final String EXTRA_OFFLINE = "android.service.media.extra.OFFLINE";
    public static final String EXTRA_RECENT = "android.service.media.extra.RECENT";
    public static final String EXTRA_SUGGESTED = "android.service.media.extra.SUGGESTED";
    private final Bundle mExtras;
    private final String mRootId;
    
    public BrowserRoot(String paramString, Bundle paramBundle)
    {
      if (paramString == null) {
        throw new IllegalArgumentException("The root id in BrowserRoot cannot be null. Use null for BrowserRoot instead.");
      }
      this.mRootId = paramString;
      this.mExtras = paramBundle;
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public String getRootId()
    {
      return this.mRootId;
    }
  }
  
  private class ConnectionRecord
  {
    IMediaBrowserServiceCallbacks callbacks;
    String pkg;
    MediaBrowserService.BrowserRoot root;
    Bundle rootHints;
    HashMap<String, List<Pair<IBinder, Bundle>>> subscriptions = new HashMap();
    
    private ConnectionRecord() {}
  }
  
  public class Result<T>
  {
    private Object mDebug;
    private boolean mDetachCalled;
    private int mFlags;
    private boolean mSendResultCalled;
    
    Result(Object paramObject)
    {
      this.mDebug = paramObject;
    }
    
    public void detach()
    {
      if (this.mDetachCalled) {
        throw new IllegalStateException("detach() called when detach() had already been called for: " + this.mDebug);
      }
      if (this.mSendResultCalled) {
        throw new IllegalStateException("detach() called when sendResult() had already been called for: " + this.mDebug);
      }
      this.mDetachCalled = true;
    }
    
    boolean isDone()
    {
      if (!this.mDetachCalled) {
        return this.mSendResultCalled;
      }
      return true;
    }
    
    void onResultSent(T paramT, int paramInt) {}
    
    public void sendResult(T paramT)
    {
      if (this.mSendResultCalled) {
        throw new IllegalStateException("sendResult() called twice for: " + this.mDebug);
      }
      this.mSendResultCalled = true;
      onResultSent(paramT, this.mFlags);
    }
    
    void setFlags(int paramInt)
    {
      this.mFlags = paramInt;
    }
  }
  
  private class ServiceBinder
    extends IMediaBrowserService.Stub
  {
    private ServiceBinder() {}
    
    public void addSubscription(final String paramString, final IBinder paramIBinder, final Bundle paramBundle, final IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    {
      MediaBrowserService.-get1(MediaBrowserService.this).post(new Runnable()
      {
        public void run()
        {
          Object localObject = paramIMediaBrowserServiceCallbacks.asBinder();
          localObject = (MediaBrowserService.ConnectionRecord)MediaBrowserService.-get0(MediaBrowserService.this).get(localObject);
          if (localObject == null)
          {
            Log.w("MediaBrowserService", "addSubscription for callback that isn't registered id=" + paramString);
            return;
          }
          MediaBrowserService.-wrap3(MediaBrowserService.this, paramString, (MediaBrowserService.ConnectionRecord)localObject, paramIBinder, paramBundle);
        }
      });
    }
    
    public void addSubscriptionDeprecated(String paramString, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks) {}
    
    public void connect(final String paramString, final Bundle paramBundle, final IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    {
      final int i = Binder.getCallingUid();
      if (!MediaBrowserService.-wrap0(MediaBrowserService.this, paramString, i)) {
        throw new IllegalArgumentException("Package/uid mismatch: uid=" + i + " package=" + paramString);
      }
      MediaBrowserService.-get1(MediaBrowserService.this).post(new Runnable()
      {
        public void run()
        {
          IBinder localIBinder = paramIMediaBrowserServiceCallbacks.asBinder();
          MediaBrowserService.-get0(MediaBrowserService.this).remove(localIBinder);
          MediaBrowserService.ConnectionRecord localConnectionRecord = new MediaBrowserService.ConnectionRecord(MediaBrowserService.this, null);
          localConnectionRecord.pkg = paramString;
          localConnectionRecord.rootHints = paramBundle;
          localConnectionRecord.callbacks = paramIMediaBrowserServiceCallbacks;
          localConnectionRecord.root = MediaBrowserService.this.onGetRoot(paramString, i, paramBundle);
          if (localConnectionRecord.root == null) {
            Log.i("MediaBrowserService", "No root for client " + paramString + " from service " + getClass().getName());
          }
          for (;;)
          {
            try
            {
              paramIMediaBrowserServiceCallbacks.onConnectFailed();
              return;
            }
            catch (RemoteException localRemoteException1)
            {
              Log.w("MediaBrowserService", "Calling onConnectFailed() failed. Ignoring. pkg=" + paramString);
              return;
            }
            try
            {
              MediaBrowserService.-get0(MediaBrowserService.this).put(localRemoteException1, localConnectionRecord);
              if (MediaBrowserService.this.mSession != null)
              {
                paramIMediaBrowserServiceCallbacks.onConnect(localConnectionRecord.root.getRootId(), MediaBrowserService.this.mSession, localConnectionRecord.root.getExtras());
                return;
              }
            }
            catch (RemoteException localRemoteException2)
            {
              Log.w("MediaBrowserService", "Calling onConnect() failed. Dropping client. pkg=" + paramString);
              MediaBrowserService.-get0(MediaBrowserService.this).remove(localRemoteException1);
            }
          }
        }
      });
    }
    
    public void disconnect(final IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    {
      MediaBrowserService.-get1(MediaBrowserService.this).post(new Runnable()
      {
        public void run()
        {
          IBinder localIBinder = paramIMediaBrowserServiceCallbacks.asBinder();
          if ((MediaBrowserService.ConnectionRecord)MediaBrowserService.-get0(MediaBrowserService.this).remove(localIBinder) != null) {}
        }
      });
    }
    
    public void getMediaItem(final String paramString, final ResultReceiver paramResultReceiver, final IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    {
      if ((TextUtils.isEmpty(paramString)) || (paramResultReceiver == null)) {
        return;
      }
      MediaBrowserService.-get1(MediaBrowserService.this).post(new Runnable()
      {
        public void run()
        {
          Object localObject = paramIMediaBrowserServiceCallbacks.asBinder();
          localObject = (MediaBrowserService.ConnectionRecord)MediaBrowserService.-get0(MediaBrowserService.this).get(localObject);
          if (localObject == null)
          {
            Log.w("MediaBrowserService", "getMediaItem for callback that isn't registered id=" + paramString);
            return;
          }
          MediaBrowserService.-wrap5(MediaBrowserService.this, paramString, (MediaBrowserService.ConnectionRecord)localObject, paramResultReceiver);
        }
      });
    }
    
    public void removeSubscription(final String paramString, final IBinder paramIBinder, final IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks)
    {
      MediaBrowserService.-get1(MediaBrowserService.this).post(new Runnable()
      {
        public void run()
        {
          Object localObject = paramIMediaBrowserServiceCallbacks.asBinder();
          localObject = (MediaBrowserService.ConnectionRecord)MediaBrowserService.-get0(MediaBrowserService.this).get(localObject);
          if (localObject == null)
          {
            Log.w("MediaBrowserService", "removeSubscription for callback that isn't registered id=" + paramString);
            return;
          }
          if (!MediaBrowserService.-wrap1(MediaBrowserService.this, paramString, (MediaBrowserService.ConnectionRecord)localObject, paramIBinder)) {
            Log.w("MediaBrowserService", "removeSubscription called for " + paramString + " which is not subscribed");
          }
        }
      });
    }
    
    public void removeSubscriptionDeprecated(String paramString, IMediaBrowserServiceCallbacks paramIMediaBrowserServiceCallbacks) {}
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/media/MediaBrowserService.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */