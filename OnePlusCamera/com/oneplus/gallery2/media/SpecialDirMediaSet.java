package com.oneplus.gallery2.media;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.preference.PreferenceManager;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.PropertyKey;
import com.oneplus.util.CollectionUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class SpecialDirMediaSet
  extends BaseMediaSet
{
  private static final int FLAG_HIDDEN = 1;
  private final String[] m_DirectoryPaths;
  private boolean m_IsVisible = true;
  private SharedPreferences m_Preferences;
  private SharedPreferences.OnSharedPreferenceChangeListener m_PreferencesChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener()
  {
    public void onSharedPreferenceChanged(SharedPreferences paramAnonymousSharedPreferences, String paramAnonymousString)
    {
      if (paramAnonymousSharedPreferences != null) {
        if ((paramAnonymousSharedPreferences.getLong(SpecialDirMediaSet.this.TAG, 0L) & 1L) != 0L) {
          break label40;
        }
      }
      label40:
      for (boolean bool1 = true; bool1 == SpecialDirMediaSet.this.m_IsVisible; bool1 = false)
      {
        return;
        return;
      }
      boolean bool2 = SpecialDirMediaSet.this.m_IsVisible;
      SpecialDirMediaSet.this.m_IsVisible = bool1;
      SpecialDirMediaSet.this.notifyPropertyChanged(SpecialDirMediaSet.PROP_IS_VISIBLE, Boolean.valueOf(bool2), Boolean.valueOf(bool1));
    }
  };
  
  protected SpecialDirMediaSet(MediaSource paramMediaSource, MediaType paramMediaType, Collection<String> paramCollection)
  {
    super(paramMediaSource, paramMediaType);
    this.m_DirectoryPaths = new String[paramCollection.size()];
    paramMediaSource = paramCollection.iterator();
    int i = 0;
    while (paramMediaSource.hasNext())
    {
      paramMediaType = (String)paramMediaSource.next();
      if (paramMediaType.length() == 0) {}
      while (paramMediaType.charAt(paramMediaType.length() - 1) != '/')
      {
        this.m_DirectoryPaths[i] = (paramMediaType + "/");
        i += 1;
        break;
      }
      this.m_DirectoryPaths[i] = paramMediaType;
      i += 1;
    }
    this.m_Preferences = PreferenceManager.getDefaultSharedPreferences(BaseApplication.current());
    this.m_Preferences.registerOnSharedPreferenceChangeListener(this.m_PreferencesChangeListener);
    if ((this.m_Preferences.getLong(this.TAG, 0L) & 1L) != 0L)
    {
      setReadOnly(PROP_IS_VISIBLE, Boolean.valueOf(false));
      Log.d(this.TAG, "SpecialDirMediaSet() - media set is hidden");
    }
  }
  
  private boolean setIsVisibleProp(boolean paramBoolean)
  {
    boolean bool = this.m_IsVisible;
    long l2;
    if (bool != paramBoolean)
    {
      verifyAccess();
      if (!isVisibilityChangeSupported()) {
        break label138;
      }
      this.m_IsVisible = paramBoolean;
      l2 = this.m_Preferences.getLong(this.TAG, 0L);
      if (paramBoolean) {
        break label140;
      }
    }
    label138:
    label140:
    for (long l1 = 1L | l2;; l1 = 0xFFFFFFFFFFFFFFFE & l2)
    {
      SharedPreferences.Editor localEditor = this.m_Preferences.edit();
      localEditor.putLong(this.TAG, l1);
      localEditor.apply();
      Log.d(this.TAG, "setIsVisibleProp() - pre flag: " + l2 + ",cur flag: " + l1);
      notifyAllMediaParentVisibilityChanged(paramBoolean);
      return notifyPropertyChanged(PROP_IS_VISIBLE, Boolean.valueOf(bool), Boolean.valueOf(paramBoolean));
      return false;
      return false;
    }
  }
  
  protected boolean addMedia(Media paramMedia, boolean paramBoolean)
  {
    onMediaNotifyParentVisibilityChanged(paramMedia, this.m_IsVisible);
    return super.addMedia(paramMedia, paramBoolean);
  }
  
  public Handle deleteMedia(Media paramMedia, Media.DeletionCallback paramDeletionCallback, int paramInt)
  {
    int i = 0;
    if (paramMedia != null)
    {
      if (shouldDeleteRawFiles()) {
        break label43;
      }
      if ((FLAG_MOVE_TO_RECYCE_BIN & paramInt) != 0) {
        break label53;
      }
    }
    for (;;)
    {
      return paramMedia.delete(paramDeletionCallback, i);
      Log.e(this.TAG, "delete() - No media to delete");
      return null;
      label43:
      i = Media.FLAG_INCLUDE_RAW_PHOTO | 0x0;
      break;
      label53:
      i |= Media.FLAG_MOVE_TO_RECYCE_BIN;
    }
  }
  
  public <TValue> TValue get(PropertyKey<TValue> paramPropertyKey)
  {
    if (paramPropertyKey != PROP_IS_VISIBLE) {
      return (TValue)super.get(paramPropertyKey);
    }
    return Boolean.valueOf(this.m_IsVisible);
  }
  
  public MediaSet.Type getType()
  {
    return MediaSet.Type.SYSTEM;
  }
  
  public boolean isVirtual()
  {
    return false;
  }
  
  public boolean isVisibilityChangeSupported()
  {
    return true;
  }
  
  protected void notifyAllMediaParentVisibilityChanged(boolean paramBoolean)
  {
    Iterator localIterator = getMedia().iterator();
    while (localIterator.hasNext())
    {
      Media localMedia = (Media)localIterator.next();
      if ((localMedia instanceof MediaStoreItem)) {
        ((MediaStoreItem)localMedia).notifyParentVisibilityChanged(paramBoolean);
      }
    }
  }
  
  protected void onDeletionCompleted(boolean paramBoolean, int paramInt)
  {
    MediaSource localMediaSource = getSource();
    if (!(localMediaSource instanceof MediaStoreMediaSource)) {}
    for (;;)
    {
      super.onDeletionCompleted(paramBoolean, paramInt);
      return;
      ((MediaStoreMediaSource)localMediaSource).notifyMediaSetDeleted(this, (Media[])CollectionUtils.toArray(getMedia(), Media.class));
    }
  }
  
  protected void onMediaNotifyParentVisibilityChanged(Media paramMedia, boolean paramBoolean)
  {
    if (paramBoolean) {}
    while (!(paramMedia instanceof MediaStoreItem)) {
      return;
    }
    ((MediaStoreItem)paramMedia).notifyParentVisibilityChanged(false);
  }
  
  protected void onRelease()
  {
    if (this.m_Preferences == null) {}
    for (;;)
    {
      super.onRelease();
      return;
      this.m_Preferences.unregisterOnSharedPreferenceChangeListener(this.m_PreferencesChangeListener);
    }
  }
  
  public <TValue> boolean set(PropertyKey<TValue> paramPropertyKey, TValue paramTValue)
  {
    if (paramPropertyKey != PROP_IS_VISIBLE) {
      return super.set(paramPropertyKey, paramTValue);
    }
    return setIsVisibleProp(((Boolean)paramTValue).booleanValue());
  }
  
  public boolean shouldContainsMedia(Media paramMedia, int paramInt)
  {
    if ((Media.FLAG_SUB_MEDIA & paramInt) == 0)
    {
      paramMedia = paramMedia.getFilePath();
      if (paramMedia == null) {
        return false;
      }
    }
    else
    {
      return false;
    }
    paramInt = this.m_DirectoryPaths.length;
    int i;
    do
    {
      i = paramInt - 1;
      if (i < 0) {
        break;
      }
      paramInt = i;
    } while (!paramMedia.startsWith(this.m_DirectoryPaths[i]));
    return true;
  }
  
  protected boolean shouldDeleteRawFiles()
  {
    return false;
  }
  
  protected void startDeletion(final Handle paramHandle, int paramInt)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    ArrayList localArrayList = new ArrayList();
    localStringBuilder.append("((media_type=1 OR media_type=3) AND (");
    int i = 0;
    if (i < this.m_DirectoryPaths.length)
    {
      if (i <= 0) {}
      for (;;)
      {
        localStringBuilder.append("_data LIKE ?");
        localArrayList.add(this.m_DirectoryPaths[i] + "%");
        i += 1;
        break;
        localStringBuilder.append(" OR ");
      }
    }
    localStringBuilder.append("))");
    if (!shouldDeleteRawFiles()) {}
    for (;;)
    {
      MediaStoreMediaSource localMediaStoreMediaSource = (MediaStoreMediaSource)BaseApplication.current().findComponent(MediaStoreMediaSource.class);
      if (localMediaStoreMediaSource == null) {
        break;
      }
      if (!Handle.isValid(localMediaStoreMediaSource.deleteFromMediaStore(localStringBuilder, (String[])localArrayList.toArray(new String[localArrayList.size()]), new MediaStoreMediaSource.MediaStoreAccessCallback()
      {
        public void onCompleted(Handle paramAnonymousHandle, Uri paramAnonymousUri, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          SpecialDirMediaSet.this.completeDeletion(paramHandle, true, paramAnonymousInt2);
        }
      }))) {
        break label294;
      }
      return;
      localStringBuilder.append(" OR (");
      i = 0;
      if (i < this.m_DirectoryPaths.length)
      {
        if (i <= 0) {}
        for (;;)
        {
          localStringBuilder.append("_data LIKE ?");
          localArrayList.add(this.m_DirectoryPaths[i] + "%.dng");
          i += 1;
          break;
          localStringBuilder.append(" OR ");
        }
      }
      localStringBuilder.append(')');
    }
    Log.e(this.TAG, "startDeletion() - Cannot find MediaStoreMediaSource");
    completeDeletion(paramHandle, false, paramInt);
    return;
    label294:
    Log.e(this.TAG, "startDeletion() - Fail to delete data from media store");
    completeDeletion(paramHandle, false, paramInt);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/SpecialDirMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */