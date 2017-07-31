package com.oneplus.gallery2.web;

import com.oneplus.base.component.Component;
import com.oneplus.gallery2.media.MediaSetList;
import com.oneplus.gallery2.media.MediaType;

public abstract interface WebMediaSetManager
  extends Component
{
  public abstract MediaSetList openMediaSetList(Account paramAccount, MediaType paramMediaType, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/web/WebMediaSetManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */