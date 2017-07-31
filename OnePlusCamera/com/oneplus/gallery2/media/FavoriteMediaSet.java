package com.oneplus.gallery2.media;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import com.oneplus.gallery2.GalleryApplication;
import java.util.Iterator;

public final class FavoriteMediaSet
  extends MultiSourcesVirtualMediaSet
{
  private static final int FLAG_HIDDEN = 1;
  private boolean m_IsVisible = true;
  
  public FavoriteMediaSet(MediaType paramMediaType)
  {
    super(null, paramMediaType);
    if ((PreferenceManager.getDefaultSharedPreferences(GalleryApplication.current()).getLong(this.TAG, 0L) & 1L) != 0L)
    {
      setReadOnly(PROP_IS_VISIBLE, Boolean.valueOf(false));
      Log.d(this.TAG, "FavoriteMediaSet() - media set is hidden");
    }
  }
  
  private boolean setIsVisibleProp(boolean paramBoolean)
  {
    boolean bool = this.m_IsVisible;
    Object localObject;
    long l2;
    if (bool != paramBoolean)
    {
      verifyAccess();
      if (!isVisibilityChangeSupported()) {
        break label137;
      }
      this.m_IsVisible = paramBoolean;
      localObject = PreferenceManager.getDefaultSharedPreferences(GalleryApplication.current());
      l2 = ((SharedPreferences)localObject).getLong(this.TAG, 0L);
      if (paramBoolean) {
        break label139;
      }
    }
    label137:
    label139:
    for (long l1 = 1L | l2;; l1 = 0xFFFFFFFFFFFFFFFE & l2)
    {
      localObject = ((SharedPreferences)localObject).edit();
      ((SharedPreferences.Editor)localObject).putLong(this.TAG, l1);
      ((SharedPreferences.Editor)localObject).apply();
      Log.d(this.TAG, "setIsVisibleProp() - pre flag: " + l2 + ",cur flag: " + l1);
      return notifyPropertyChanged(PROP_IS_VISIBLE, Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
      return false;
      return false;
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey != PROP_IS_VISIBLE) {
      return (TValue)super.get(paramPropertyKey);
    }
    return Boolean.valueOf(this.m_IsVisible);
  }
  
  public String getId()
  {
    return "Favorite";
  }
  
  protected int getNameResourceId()
  {
    return BaseApplication.current().getResources().getIdentifier("media_set_name_favorite", "string", "com.oneplus.gallery");
  }
  
  public MediaSet.Type getType()
  {
    return MediaSet.Type.SYSTEM;
  }
  
  public boolean isVisibilityChangeSupported()
  {
    return true;
  }
  
  protected void onMediaMovedToRecycleBin(Media paramMedia, int paramInt)
  {
    super.onMediaMovedToRecycleBin(paramMedia, paramInt);
    if (!paramMedia.isFavoriteSupported()) {
      return;
    }
    paramMedia.setFavorite(false);
  }
  
  protected void onMediaRestoringFromRecycleBin(Media paramMedia, int paramInt)
  {
    if (!paramMedia.isFavoriteSupported()) {}
    for (;;)
    {
      super.onMediaRestoringFromRecycleBin(paramMedia, paramInt);
      return;
      paramMedia.setFavorite(true);
    }
  }
  
  protected boolean removeMediaFromSet(Media paramMedia)
  {
    return paramMedia.setFavorite(false);
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey != PROP_IS_VISIBLE) {
      return super.set(paramPropertyKey, paramTValue);
    }
    return setIsVisibleProp(((Boolean)paramTValue).booleanValue());
  }
  
  protected boolean shouldContainsMedia(Media paramMedia, int paramInt)
  {
    if ((Media.FLAG_SUB_MEDIA & paramInt) == 0) {
      return paramMedia.isFavorite();
    }
    return false;
  }
  
  protected void startDeletion(Handle paramHandle, int paramInt)
  {
    Iterator localIterator = getMedia().iterator();
    while (localIterator.hasNext()) {
      ((Media)localIterator.next()).setFavorite(false);
    }
    completeDeletion(paramHandle, true, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/FavoriteMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */