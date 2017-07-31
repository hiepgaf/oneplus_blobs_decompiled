package com.oneplus.gallery2.media;

import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.util.Size;
import com.oneplus.base.BitFlagsGroup;
import com.oneplus.base.Handle;
import com.oneplus.base.HandlerObject;
import com.oneplus.base.Ref;
import com.oneplus.base.ThreadDependentObject;
import com.oneplus.gallery2.ExtraKey;
import com.oneplus.gallery2.ExtraKeyGenerator;
import com.oneplus.gallery2.SimpleExtraKeyGenerator;
import java.io.IOException;
import java.io.InputStream;

public abstract interface Media
  extends HandlerObject, ThreadDependentObject
{
  public static final ExtraKeyGenerator EXTRA_KEY_GENERATOR;
  public static final BitFlagsGroup FLAGS_GROUP = new BitFlagsGroup(Media.class);
  public static final int FLAG_ADDRESS_CHANGED;
  public static final int FLAG_DISPLAY_NAME_CHANGED;
  public static final int FLAG_FAVORITE_CHANGED;
  public static final int FLAG_FILE_PATH_CHANGED;
  public static final int FLAG_FILE_SIZE_CHANGED;
  public static final int FLAG_INCLUDE_RAW_PHOTO;
  public static final int FLAG_IS_SUB_MEDIA_CHANGED;
  public static final int FLAG_LAST_MODIFIED_TIME_CHANGED;
  public static final int FLAG_LOCATION_CHANGED;
  public static final int FLAG_MOVE_TO_RECYCE_BIN;
  public static final int FLAG_RESTORE_FROM_RECYCLE_BIN;
  public static final int FLAG_SHARE = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_SIZE_CHANGED;
  public static final int FLAG_SUB_MEDIA;
  public static final int FLAG_TAKEN_TIME_CHANGED;
  public static final int FLAG_TITLE_CHANGED;
  public static final int FLAG_VISIBILITY_CHANGED;
  
  static
  {
    EXTRA_KEY_GENERATOR = new SimpleExtraKeyGenerator();
    FLAG_SUB_MEDIA = FLAGS_GROUP.nextIntFlag();
    FLAG_MOVE_TO_RECYCE_BIN = FLAGS_GROUP.nextIntFlag();
    FLAG_RESTORE_FROM_RECYCLE_BIN = FLAGS_GROUP.nextIntFlag();
    FLAG_INCLUDE_RAW_PHOTO = FLAGS_GROUP.nextIntFlag();
    FLAG_ADDRESS_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_DISPLAY_NAME_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_FAVORITE_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_FILE_PATH_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_FILE_SIZE_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_IS_SUB_MEDIA_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_LAST_MODIFIED_TIME_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_LOCATION_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_SIZE_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_TAKEN_TIME_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_TITLE_CHANGED = FLAGS_GROUP.nextIntFlag();
    FLAG_VISIBILITY_CHANGED = FLAGS_GROUP.nextIntFlag();
  }
  
  public abstract boolean addToAlbum(long paramLong, int paramInt);
  
  public abstract boolean canAddToAlbum();
  
  public abstract Handle delete(DeletionCallback paramDeletionCallback, int paramInt);
  
  public abstract Address getAddress();
  
  public abstract MediaCacheKey getCacheKey();
  
  public abstract Uri getContentUri();
  
  public abstract Handle getDetails(DetailsCallback paramDetailsCallback);
  
  public abstract String getDisplayName();
  
  public abstract Media getEffectedMedia();
  
  public abstract boolean getEmbeddedThumbnailImageSize(int paramInt1, int paramInt2, int[] paramArrayOfInt, int paramInt3);
  
  public abstract <T> T getExtra(ExtraKey<T> paramExtraKey, T paramT);
  
  public abstract String getFileNameExtension();
  
  public abstract String getFilePath();
  
  public abstract long getFileSize();
  
  public abstract String getId();
  
  public abstract long getLastModifiedTime();
  
  public abstract Location getLocation();
  
  public abstract String getMimeType();
  
  public abstract Media getOriginalMedia();
  
  public abstract Address getPreviousAddress();
  
  public abstract String getPreviousFilePath();
  
  public abstract Location getPreviousLocation();
  
  public abstract long getPreviousTakenTime();
  
  public abstract Handle getSize(SizeCallback paramSizeCallback);
  
  public abstract <T extends MediaSource> T getSource();
  
  public abstract long getTakenTime();
  
  public abstract String getTitle();
  
  public abstract MediaType getType();
  
  public abstract boolean isAvailable();
  
  public abstract boolean isCapturedByFrontCamera();
  
  public abstract boolean isExternal();
  
  public abstract boolean isFavorite();
  
  public abstract boolean isFavoriteSupported();
  
  public abstract boolean isParentVisible();
  
  public abstract boolean isReadOnly();
  
  public abstract boolean isShareable();
  
  public abstract boolean isTemporary();
  
  public abstract boolean isVisibilityChangeSupported();
  
  public abstract boolean isVisible();
  
  public abstract InputStream openInputStream(Ref<Boolean> paramRef, int paramInt)
    throws IOException;
  
  public abstract InputStream openInputStreamForEmbeddedThumbnailImage(int paramInt1, int paramInt2, Ref<Boolean> paramRef, int paramInt3)
    throws IOException;
  
  public abstract Size peekSize();
  
  public abstract Handle prepareSharing(PrepareSharingCallback paramPrepareSharingCallback, int paramInt);
  
  public abstract <T> void putExtra(ExtraKey<T> paramExtraKey, T paramT);
  
  public abstract boolean removeFromAlbum(long paramLong, int paramInt);
  
  public abstract boolean setFavorite(boolean paramBoolean);
  
  public abstract boolean setVisible(boolean paramBoolean);
  
  public abstract Handle view(int paramInt);
  
  public static abstract class DeletionCallback
  {
    public void onDeletionCancelled(Media paramMedia, int paramInt) {}
    
    public void onDeletionCompleted(Media paramMedia, boolean paramBoolean, int paramInt) {}
    
    public void onDeletionStarted(Media paramMedia, int paramInt) {}
  }
  
  public static abstract interface DetailsCallback
  {
    public abstract void onDetailsObtained(Media paramMedia, MediaDetails paramMediaDetails);
  }
  
  public static abstract interface SizeCallback
  {
    public abstract void onSizeObtained(Media paramMedia, int paramInt1, int paramInt2);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/Media.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */