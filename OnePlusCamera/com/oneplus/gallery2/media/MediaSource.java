package com.oneplus.gallery2.media;

import android.net.Uri;
import com.oneplus.base.BitFlagsGroup;
import com.oneplus.base.Handle;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;

public abstract interface MediaSource
  extends Component
{
  public static final BitFlagsGroup FLAGS_GROUP = new BitFlagsGroup(MediaSource.class);
  public static final int FLAG_ALWAYS_REFRESH = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_EXPAND_GROUPED_MEDIA = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_RECYCLED_MEDIA_ONLY = FLAGS_GROUP.nextIntFlag();
  public static final PropertyKey<Boolean> PROP_IS_ACTIVE = new PropertyKey("IsActive", Boolean.class, MediaSource.class, Boolean.valueOf(false));
  public static final PropertyKey<Boolean> PROP_IS_MEDIA_TABLE_READY = new PropertyKey("IsMediaTableReady", Boolean.class, MediaSource.class, Boolean.valueOf(false));
  
  public abstract Handle activate(int paramInt);
  
  public abstract Handle addMediaChangedCallback(MediaChangeCallback paramMediaChangeCallback);
  
  public abstract Handle addMediaIterationClient(MediaIterationClient paramMediaIterationClient, MediaType paramMediaType);
  
  public abstract GroupedMedia[] getGroupedMedia(Media paramMedia, int paramInt);
  
  public abstract Handle getMedia(String paramString, MediaObtainCallback paramMediaObtainCallback, int paramInt);
  
  public abstract <T extends Media> T getMedia(String paramString, int paramInt);
  
  public abstract Iterable<Media> getMedia(MediaType paramMediaType, int paramInt);
  
  public abstract String getMediaId(Uri paramUri, String paramString);
  
  public abstract boolean isMediaIdSupported(String paramString);
  
  public abstract boolean isRecycledMedia(Media paramMedia);
  
  public abstract boolean isSubMedia(Media paramMedia);
  
  public abstract boolean scheduleMediaIteration(int paramInt);
  
  public static abstract interface MediaObtainCallback
  {
    public static final MediaObtainCallback EMPTY = new MediaObtainCallback()
    {
      public void onMediaObtained(MediaSource paramAnonymousMediaSource, Uri paramAnonymousUri, String paramAnonymousString, Media paramAnonymousMedia, int paramAnonymousInt) {}
    };
    
    public abstract void onMediaObtained(MediaSource paramMediaSource, Uri paramUri, String paramString, Media paramMedia, int paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/MediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */