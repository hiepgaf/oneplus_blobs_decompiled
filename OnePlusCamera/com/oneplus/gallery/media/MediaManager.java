package com.oneplus.gallery.media;

import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Handler;
import android.os.RemoteException;
import com.oneplus.base.BitFlagsGroup;
import com.oneplus.base.EventKey;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.Ref;
import com.oneplus.base.component.Component;

public abstract interface MediaManager
  extends Component
{
  public static final EventKey<MediaSetEventArgs> EVENT_ALBUM_CREATED = new EventKey("AlbumCreated", MediaSetEventArgs.class, MediaManager.class);
  public static final BitFlagsGroup FLAGS_GROUP = new BitFlagsGroup(MediaManager.class);
  public static final int FLAG_ADD_ALL_MEDIA_SET;
  public static final int FLAG_INCLUDE_RAW_PHOTO = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_SINGLE_MEDIA_CHANGE;
  public static final Object INITIALIZER = new Object() {};
  public static final PropertyKey<Boolean> PROP_IS_ACTIVE;
  
  static
  {
    FLAG_ADD_ALL_MEDIA_SET = FLAGS_GROUP.nextIntFlag();
    FLAG_SINGLE_MEDIA_CHANGE = FLAGS_GROUP.nextIntFlag();
    PROP_IS_ACTIVE = new PropertyKey("IsActive", Boolean.class, MediaManager.class, Boolean.valueOf(false));
  }
  
  public abstract Handle accessContentProvider(Uri paramUri, ContentProviderAccessCallback paramContentProviderAccessCallback);
  
  public abstract Handle activate();
  
  public abstract MediaSetList createMediaSetList(MediaType paramMediaType, int paramInt);
  
  public abstract Handle deleteMedia(Media paramMedia, MediaDeletionCallback paramMediaDeletionCallback, Handler paramHandler, int paramInt);
  
  public abstract Uri insertToMediaStore(String paramString, ContentValues paramContentValues, Ref<Media> paramRef, int paramInt);
  
  public abstract boolean isContentThread();
  
  public abstract boolean isMediaRecycled(MediaId paramMediaId);
  
  public abstract void notifyMediaSetDeleted(MediaSet paramMediaSet);
  
  public abstract boolean postToContentThread(Runnable paramRunnable, long paramLong);
  
  public abstract boolean postToContentThreadAndWait(Runnable paramRunnable, long paramLong);
  
  public abstract Handle recycleMedia(Media paramMedia, int paramInt);
  
  public abstract Handle registerMediaChangeCallback(MediaChangeCallback paramMediaChangeCallback, Handler paramHandler);
  
  public static abstract interface ContentProviderAccessCallback
  {
    public abstract void onAccessContentProvider(ContentResolver paramContentResolver, Uri paramUri, ContentProviderClient paramContentProviderClient)
      throws RemoteException;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/MediaManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */