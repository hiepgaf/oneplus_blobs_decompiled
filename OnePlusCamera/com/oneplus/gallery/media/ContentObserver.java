package com.oneplus.gallery.media;

import android.net.Uri;
import android.os.Handler;
import com.oneplus.base.Handle;
import com.oneplus.base.component.Component;

public abstract interface ContentObserver
  extends Component
{
  public abstract void notifyContentChanged(Uri paramUri);
  
  public abstract Handle registerContentChangedCallback(Uri paramUri, ContentChangeCallback paramContentChangeCallback, Handler paramHandler);
  
  public static abstract interface ContentChangeCallback
  {
    public abstract void onContentChanged(Uri paramUri);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery/media/ContentObserver.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */