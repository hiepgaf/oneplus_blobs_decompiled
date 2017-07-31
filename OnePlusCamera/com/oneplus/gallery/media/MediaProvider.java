package com.oneplus.gallery.media;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import com.oneplus.base.BitFlagsGroup;
import com.oneplus.base.Handle;
import com.oneplus.base.ThreadDependentObject;

public abstract interface MediaProvider
  extends MediaIterable, ThreadDependentObject
{
  public static final BitFlagsGroup FLAGS_GROUP = new BitFlagsGroup(MediaProvider.class);
  public static final int FLAG_ALWAYS_REFRESH;
  public static final int FLAG_IGNORE_THUMBNAIL_UPDATE = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_INCLUDE_RAW_PHOTO;
  public static final int FLAG_INCLUDE_RECYCLED_MEDIA = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_USE_EXISTENCE_ONLY;
  
  static
  {
    FLAG_INCLUDE_RAW_PHOTO = FLAGS_GROUP.nextIntFlag();
    FLAG_ALWAYS_REFRESH = FLAGS_GROUP.nextIntFlag();
    FLAG_USE_EXISTENCE_ONLY = FLAGS_GROUP.nextIntFlag();
  }
  
  public abstract Handle addGroupMediaChangedCallback(GroupMedia.GroupMediaChangeCallback paramGroupMediaChangeCallback);
  
  public abstract Handle addMediaChangedCallback(MediaChangeCallback paramMediaChangeCallback);
  
  public abstract boolean deleteMedia(Media paramMedia, int paramInt);
  
  public abstract boolean initialize(int paramInt);
  
  public abstract boolean isMediaRecycled(Media paramMedia);
  
  public abstract boolean isOwnedMedia(Media paramMedia);
  
  public abstract void notifyMediaDeleted(Media paramMedia, int paramInt);
  
  public abstract void notifyMediaUpdated(Media paramMedia, int paramInt);
  
  public abstract GroupMedia obtainGroupMedia(Media paramMedia, int paramInt);
  
  public abstract Media obtainMedia(ContentResolver paramContentResolver, Uri paramUri, String paramString, int paramInt);
  
  public abstract Media obtainMedia(MediaId paramMediaId, int paramInt);
  
  public abstract boolean recycleMedia(Media paramMedia, int paramInt);
  
  public abstract void refreshMedia();
  
  public abstract void release();
  
  public abstract boolean restoreMedia(Media paramMedia, int paramInt);
  
  public static abstract interface ContentProviderQueryCallback
  {
    public abstract void onQuery(ContentResolver paramContentResolver, Uri paramUri, Cursor paramCursor)
      throws RemoteException;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/MediaProvider.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */