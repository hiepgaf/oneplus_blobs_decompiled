package com.oneplus.gallery2.media;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.component.BasicComponent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ContentObserverImpl
  extends BasicComponent
  implements ContentObserver
{
  private static final long INTERVAL_CHECK_CONTENT_CHANGES = 2000L;
  private static final int MSG_CHECK_CONTENT_CHANGES = 10040;
  private static final int MSG_NOTIFY_CONTENT_CHANGED = 10041;
  private static final int MSG_REGISTER_CONTENT_CHANGED_CB = 10010;
  private static final int MSG_UNREGISTER_CONTENT_CHANGED_CB = 10011;
  private static final String PATTERN_SPECIFIC_CONTENT_URI = ".+/[\\d]+$";
  private volatile HandlerThread m_ContentObserverThread;
  private volatile Handler m_ContentObserverThreadHandler;
  private HashMap<Uri, ContentObserver> m_ContentObservers;
  private ContentResolver m_ContentResolver;
  private final Object m_Lock = new Object();
  
  ContentObserverImpl(BaseApplication paramBaseApplication)
  {
    super("Content Observer", paramBaseApplication, true);
  }
  
  private void checkContentChanges(Uri paramUri)
  {
    if (this.m_ContentObservers == null) {}
    while (this.m_ContentObservers.isEmpty()) {
      return;
    }
    Iterator localIterator = this.m_ContentObservers.values().iterator();
    while (localIterator.hasNext())
    {
      ContentObserver localContentObserver = (ContentObserver)localIterator.next();
      if (localContentObserver.lastChangedTime <= 0L) {}
      for (int i = 1;; i = 0)
      {
        if (i != 0) {
          break label83;
        }
        if (paramUri != null) {
          break label85;
        }
        localContentObserver.notifyChange(true);
        break;
      }
      label83:
      continue;
      label85:
      if (paramUri.toString().startsWith(localContentObserver.contentUri.toString())) {
        localContentObserver.notifyChange(paramUri, true);
      }
    }
  }
  
  private void handleContentObserverThreadMessage(Message paramMessage)
  {
    Uri localUri = null;
    switch (paramMessage.what)
    {
    default: 
      return;
    case 10040: 
      if (paramMessage.obj == null) {}
      for (;;)
      {
        checkContentChanges(localUri);
        return;
        if ((paramMessage.obj instanceof Uri)) {
          localUri = (Uri)paramMessage.obj;
        }
      }
    case 10041: 
      notifyContentChangedInternal((Uri)paramMessage.obj);
      return;
    case 10010: 
      registerContentChangedCallback((ContentChangeCallbackHandle)paramMessage.obj);
      return;
    }
    unregisterContentChangedCallback((ContentChangeCallbackHandle)paramMessage.obj);
  }
  
  private boolean isContentObserverThread()
  {
    return Thread.currentThread() == this.m_ContentObserverThread;
  }
  
  private void notifyContentChangedInternal(Uri paramUri)
  {
    if (this.m_ContentObservers != null)
    {
      String str = paramUri.toString();
      Iterator localIterator = this.m_ContentObservers.values().iterator();
      while (localIterator.hasNext())
      {
        ContentObserver localContentObserver = (ContentObserver)localIterator.next();
        if (str.startsWith(localContentObserver.contentUri.toString())) {
          localContentObserver.notifyChange(paramUri, false);
        }
      }
      return;
    }
  }
  
  private void registerContentChangedCallback(ContentChangeCallbackHandle paramContentChangeCallbackHandle)
  {
    if (this.m_ContentObservers != null) {}
    ContentObserver localContentObserver;
    for (;;)
    {
      localContentObserver = (ContentObserver)this.m_ContentObservers.get(paramContentChangeCallbackHandle.contentUri);
      if (localContentObserver == null) {
        break;
      }
      localContentObserver.callbackHandles.add(paramContentChangeCallbackHandle);
      return;
      this.m_ContentObservers = new HashMap();
    }
    Log.v(this.TAG, "registerContentChangedCallback() - Register to ", paramContentChangeCallbackHandle.contentUri);
    if (this.m_ContentResolver != null) {}
    for (;;)
    {
      localContentObserver = new ContentObserver(paramContentChangeCallbackHandle.contentUri, this.m_ContentObserverThreadHandler);
      this.m_ContentObservers.put(paramContentChangeCallbackHandle.contentUri, localContentObserver);
      this.m_ContentResolver.registerContentObserver(paramContentChangeCallbackHandle.contentUri, true, localContentObserver);
      break;
      this.m_ContentResolver = BaseApplication.current().getContentResolver();
    }
  }
  
  private void startContentObserverThread()
  {
    if (this.m_ContentObserverThreadHandler == null) {
      synchronized (this.m_Lock)
      {
        if (this.m_ContentObserverThreadHandler != null) {
          return;
        }
        this.m_ContentObserverThread = new HandlerThread("Gallery media content observer thread");
        Log.v(this.TAG, "startContentObserverThread() - Start content observer thread [start]");
        this.m_ContentObserverThread.start();
        this.m_ContentObserverThreadHandler = new Handler(this.m_ContentObserverThread.getLooper())
        {
          public void handleMessage(Message paramAnonymousMessage)
          {
            ContentObserverImpl.this.handleContentObserverThreadMessage(paramAnonymousMessage);
          }
        };
        Log.v(this.TAG, "startContentObserverThread() - Start content observer thread [end]");
      }
    }
  }
  
  private void unregisterContentChangedCallback(ContentChangeCallbackHandle paramContentChangeCallbackHandle)
  {
    ContentObserver localContentObserver;
    if (this.m_ContentObservers != null)
    {
      localContentObserver = (ContentObserver)this.m_ContentObservers.get(paramContentChangeCallbackHandle.contentUri);
      if (localContentObserver != null) {
        break label28;
      }
    }
    label28:
    while ((!localContentObserver.callbackHandles.remove(paramContentChangeCallbackHandle)) || (!localContentObserver.callbackHandles.isEmpty()))
    {
      return;
      return;
    }
    Log.v(this.TAG, "unregisterContentChangedCallback() - Unregister from ", paramContentChangeCallbackHandle.contentUri);
    this.m_ContentObservers.remove(paramContentChangeCallbackHandle.contentUri);
    this.m_ContentResolver.unregisterContentObserver(localContentObserver);
  }
  
  public void notifyContentChanged(Uri paramUri)
  {
    if (paramUri != null)
    {
      Log.v(this.TAG, "notifyContentChanged() - Content URI : ", paramUri);
      if (!isContentObserverThread())
      {
        startContentObserverThread();
        Message.obtain(this.m_ContentObserverThreadHandler, 10041, paramUri).sendToTarget();
      }
    }
    else
    {
      Log.e(this.TAG, "notifyContentChanged() - No content URI");
      return;
    }
    notifyContentChangedInternal(paramUri);
  }
  
  protected void onDeinitialize()
  {
    if (this.m_ContentObserverThread == null) {}
    for (;;)
    {
      this.m_ContentResolver = null;
      super.onDeinitialize();
      return;
      this.m_ContentObserverThreadHandler.removeMessages(10040);
      this.m_ContentObserverThreadHandler.removeMessages(10010);
      this.m_ContentObserverThreadHandler.removeMessages(10011);
      this.m_ContentObserverThread.quitSafely();
      this.m_ContentObserverThread = null;
    }
  }
  
  protected void onInitialize()
  {
    super.onInitialize();
    startContentObserverThread();
  }
  
  public Handle registerContentChangedCallback(Uri paramUri, ContentObserver.ContentChangeCallback paramContentChangeCallback, Handler paramHandler)
  {
    if (paramUri != null)
    {
      if (paramContentChangeCallback != null)
      {
        paramUri = new ContentChangeCallbackHandle(paramUri, paramContentChangeCallback, paramHandler);
        if (isContentObserverThread()) {
          break label71;
        }
        startContentObserverThread();
        Message.obtain(this.m_ContentObserverThreadHandler, 10010, paramUri).sendToTarget();
        return paramUri;
      }
    }
    else
    {
      Log.e(this.TAG, "registerContentChangedCallback() - No content URI");
      return null;
    }
    Log.e(this.TAG, "registerContentChangedCallback() - No call-back");
    return null;
    label71:
    registerContentChangedCallback(paramUri);
    return paramUri;
  }
  
  private final class ContentChangeCallbackHandle
    extends Handle
  {
    private static final int MSG_CONTENT_CHANGED = 10000;
    public final Uri contentUri;
    private final ContentObserver.ContentChangeCallback m_Callback;
    private final Handler m_CallbackHandler;
    
    public ContentChangeCallbackHandle(Uri paramUri, ContentObserver.ContentChangeCallback paramContentChangeCallback, Handler paramHandler)
    {
      super();
      this.contentUri = paramUri;
      this.m_Callback = paramContentChangeCallback;
      if (paramHandler == null)
      {
        this.m_CallbackHandler = null;
        return;
      }
      this.m_CallbackHandler = new Handler(paramHandler.getLooper())
      {
        public void handleMessage(Message paramAnonymousMessage)
        {
          switch (paramAnonymousMessage.what)
          {
          default: 
            return;
          }
          ContentObserverImpl.ContentChangeCallbackHandle.this.m_Callback.onContentChanged((Uri)paramAnonymousMessage.obj);
        }
      };
    }
    
    public void notifyContentChanged(Uri paramUri)
    {
      if (Handle.isValid(this))
      {
        if (paramUri == null) {
          break label30;
        }
        if (this.m_CallbackHandler != null) {
          break label38;
        }
      }
      label30:
      label38:
      while (Thread.currentThread() == this.m_CallbackHandler.getLooper().getThread())
      {
        this.m_Callback.onContentChanged(paramUri);
        return;
        return;
        paramUri = this.contentUri;
        break;
      }
      Message.obtain(this.m_CallbackHandler, 10000, paramUri).sendToTarget();
    }
    
    protected void onClose(int paramInt)
    {
      if (!ContentObserverImpl.this.isContentObserverThread())
      {
        Message.obtain(ContentObserverImpl.this.m_ContentObserverThreadHandler, 10011, this).sendToTarget();
        return;
      }
      ContentObserverImpl.this.unregisterContentChangedCallback(this);
    }
  }
  
  private final class ContentObserver
    extends android.database.ContentObserver
  {
    public final List<ContentObserverImpl.ContentChangeCallbackHandle> callbackHandles = new ArrayList();
    public final Uri contentUri;
    public long lastChangedTime;
    
    public ContentObserver(Uri paramUri, Handler paramHandler)
    {
      super();
      this.contentUri = paramUri;
    }
    
    public void notifyChange(Uri paramUri, boolean paramBoolean)
    {
      if (!paramBoolean) {}
      for (;;)
      {
        int i = this.callbackHandles.size() - 1;
        while (i >= 0)
        {
          ((ContentObserverImpl.ContentChangeCallbackHandle)this.callbackHandles.get(i)).notifyContentChanged(paramUri);
          i -= 1;
        }
        this.lastChangedTime = 0L;
      }
    }
    
    public void notifyChange(boolean paramBoolean)
    {
      notifyChange(this.contentUri, paramBoolean);
    }
    
    public void onChange(boolean paramBoolean)
    {
      onChange(paramBoolean, null);
    }
    
    public void onChange(boolean paramBoolean, Uri paramUri)
    {
      paramBoolean = paramUri.toString().matches(".+/[\\d]+$");
      this.lastChangedTime = SystemClock.elapsedRealtime();
      if (!paramBoolean)
      {
        if (!ContentObserverImpl.this.m_ContentObserverThreadHandler.hasMessages(10040)) {}
      }
      else
      {
        Message.obtain(ContentObserverImpl.this.m_ContentObserverThreadHandler, 10040, paramUri).sendToTarget();
        return;
      }
      ContentObserverImpl.this.m_ContentObserverThreadHandler.sendEmptyMessageDelayed(10040, 2000L);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/ContentObserverImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */