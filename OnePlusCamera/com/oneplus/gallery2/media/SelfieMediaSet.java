package com.oneplus.gallery2.media;

import android.content.res.Resources;
import com.oneplus.base.BaseApplication;
import com.oneplus.base.Handle;
import com.oneplus.base.Log;
import com.oneplus.base.Ref;
import com.oneplus.base.SimpleRef;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SelfieMediaSet
  extends CameraRollMediaSet
{
  public SelfieMediaSet(MediaStoreMediaSource paramMediaStoreMediaSource, MediaType paramMediaType)
  {
    super(paramMediaStoreMediaSource, paramMediaType);
  }
  
  protected int getNameResourceId()
  {
    return BaseApplication.current().getResources().getIdentifier("media_set_name_selfie", "string", "com.oneplus.gallery");
  }
  
  public boolean isVisibilityChangeSupported()
  {
    return true;
  }
  
  protected void notifyAllMediaParentVisibilityChanged(boolean paramBoolean) {}
  
  protected void onMediaNotifyParentVisibilityChanged(Media paramMedia, boolean paramBoolean) {}
  
  public boolean shouldContainsMedia(Media paramMedia, int paramInt)
  {
    if (!super.shouldContainsMedia(paramMedia, paramInt)) {}
    while (!paramMedia.isCapturedByFrontCamera()) {
      return false;
    }
    return true;
  }
  
  protected void startDeletion(final Handle paramHandle, final int paramInt)
  {
    Object localObject = (Integer)get(PROP_MEDIA_COUNT);
    if (localObject == null) {}
    while (((Integer)localObject).intValue() == 0)
    {
      completeDeletion(paramHandle, true, paramInt);
      return;
    }
    final SimpleRef localSimpleRef = new SimpleRef(Integer.valueOf(0));
    localObject = new ArrayList(((Integer)localObject).intValue());
    Iterator localIterator = getMedia().iterator();
    while (localIterator.hasNext()) {
      ((List)localObject).add((Media)localIterator.next());
    }
    paramInt = ((List)localObject).size();
    Log.v(this.TAG, "startDeletion() - Delete " + paramInt + " media");
    paramHandle = new Media.DeletionCallback()
    {
      public void onDeletionCompleted(Media paramAnonymousMedia, boolean paramAnonymousBoolean, int paramAnonymousInt)
      {
        localSimpleRef.set(Integer.valueOf(((Integer)localSimpleRef.get()).intValue() + 1));
        if (((Integer)localSimpleRef.get()).intValue() < paramInt) {
          return;
        }
        SelfieMediaSet.this.completeDeletion(paramHandle, true, paramAnonymousInt);
      }
    };
    paramInt -= 1;
    if (paramInt >= 0)
    {
      if (Handle.isValid(((Media)((List)localObject).get(paramInt)).delete(paramHandle, Media.FLAG_INCLUDE_RAW_PHOTO))) {}
      for (;;)
      {
        paramInt -= 1;
        break;
        paramHandle.onDeletionCompleted((Media)((List)localObject).get(paramInt), false, 0);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/SelfieMediaSet.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */