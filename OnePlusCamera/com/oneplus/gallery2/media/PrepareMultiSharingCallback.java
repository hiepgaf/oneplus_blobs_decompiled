package com.oneplus.gallery2.media;

import android.net.Uri;
import java.util.List;

public abstract interface PrepareMultiSharingCallback
{
  public abstract void onPrepared(List<Media> paramList, List<Uri> paramList1, List<String> paramList2, PrepareSharingResult paramPrepareSharingResult);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/PrepareMultiSharingCallback.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */