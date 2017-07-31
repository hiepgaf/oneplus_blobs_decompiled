package com.oneplus.gallery2.media;

import com.oneplus.base.BitFlagsGroup;
import com.oneplus.gallery2.contentdetection.ObjectType;
import com.oneplus.gallery2.media.content.Scene;
import java.util.List;

public abstract interface ContentAwareMedia
  extends Media
{
  public static final int FLAG_CONTENT_OBJECT_TYPES_CHANGED = FLAGS_GROUP.nextIntFlag();
  public static final int FLAG_CONTENT_SCENES_CHANGED = FLAGS_GROUP.nextIntFlag();
  
  public abstract boolean containsObject(ObjectType paramObjectType);
  
  public abstract boolean containsScene(Scene paramScene);
  
  public abstract int getContentObjectTypes(List<ObjectType> paramList, int paramInt);
  
  public abstract int getContentScenes(List<Scene> paramList, int paramInt);
  
  public abstract int getPreviousContentObjectTypes(List<ObjectType> paramList, int paramInt);
  
  public abstract int getPreviousContentScenes(List<Scene> paramList, int paramInt);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/ContentAwareMedia.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */