package com.oneplus.gallery2.media.content;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;
import com.oneplus.gallery2.media.GalleryDatabase.SceneDetectionResult;
import com.oneplus.mediacontentrecognition.photo.SceneDetectionResult;
import java.util.Collection;
import java.util.Set;

public class MediaContentRecognitionScene
  implements Scene
{
  private static final String ID_PREFIX = "OPMCR/";
  private static final SparseArray<MediaContentRecognitionScene> INSTANCES = new SparseArray();
  private static final SparseArray<int[]> KEYWORD_LIST_TABLE = new SparseArray();
  private static final int SCENE_COUNT = 1000;
  private final String m_Id;
  private final int m_RawId;
  
  private MediaContentRecognitionScene(int paramInt)
  {
    this.m_RawId = paramInt;
    this.m_Id = ("OPMCR/" + this.m_RawId);
  }
  
  public MediaContentRecognitionScene(GalleryDatabase.SceneDetectionResult paramSceneDetectionResult)
  {
    this(paramSceneDetectionResult.sceneId);
  }
  
  public MediaContentRecognitionScene(SceneDetectionResult paramSceneDetectionResult)
  {
    this(paramSceneDetectionResult.sceneId);
  }
  
  public static MediaContentRecognitionScene create(int paramInt)
  {
    synchronized (INSTANCES)
    {
      MediaContentRecognitionScene localMediaContentRecognitionScene = (MediaContentRecognitionScene)INSTANCES.get(paramInt);
      if (localMediaContentRecognitionScene != null) {
        return localMediaContentRecognitionScene;
      }
      localMediaContentRecognitionScene = new MediaContentRecognitionScene(paramInt);
      INSTANCES.put(paramInt, localMediaContentRecognitionScene);
    }
  }
  
  public static void getAllKeywords(Context paramContext, Set<String> paramSet)
  {
    paramSet.clear();
    int i = 999;
    while (i >= 0)
    {
      getKeywords(paramContext, i, paramSet, false);
      i -= 1;
    }
  }
  
  public static void getAllScenes(Collection<? super MediaContentRecognitionScene> paramCollection)
  {
    paramCollection.clear();
    int i = 999;
    while (i >= 0)
    {
      paramCollection.add(create(i));
      i -= 1;
    }
  }
  
  public static MediaContentRecognitionScene[] getAllScenes()
  {
    MediaContentRecognitionScene[] arrayOfMediaContentRecognitionScene = new MediaContentRecognitionScene['Ϩ'];
    int i = 999;
    while (i >= 0)
    {
      arrayOfMediaContentRecognitionScene[i] = create(i);
      i -= 1;
    }
    return arrayOfMediaContentRecognitionScene;
  }
  
  private static void getKeywords(Context paramContext, int paramInt, Set<String> paramSet, boolean paramBoolean)
  {
    Resources localResources = paramContext.getResources();
    if (!paramBoolean) {}
    for (;;)
    {
      synchronized (KEYWORD_LIST_TABLE)
      {
        paramContext = (int[])KEYWORD_LIST_TABLE.get(paramInt);
        if (paramContext != null)
        {
          paramInt = paramContext.length;
          i = paramInt - 1;
          if (i < 0) {
            break;
          }
          ??? = localResources.getString(paramContext[i]).split("\\|");
          paramInt = ???.length;
          int j = paramInt - 1;
          paramInt = i;
          if (j < 0) {
            continue;
          }
          paramSet.add(???[j]);
          paramInt = j;
          continue;
          paramSet.clear();
          continue;
        }
        int i = localResources.getIdentifier("opmcr_scene_keywords_" + paramInt, "array", "com.oneplus.gallery");
        if (i != 0)
        {
          String[] arrayOfString = localResources.getStringArray(i);
          paramContext = new int[arrayOfString.length];
          i = arrayOfString.length;
          i -= 1;
          if (i < 0) {
            break label203;
          }
          paramContext[i] = localResources.getIdentifier(arrayOfString[i], "string", "com.oneplus.gallery");
        }
      }
      return;
      label203:
      KEYWORD_LIST_TABLE.put(paramInt, paramContext);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof MediaContentRecognitionScene)) {
      return false;
    }
    return ((MediaContentRecognitionScene)paramObject).m_RawId == this.m_RawId;
  }
  
  public String getId()
  {
    return this.m_Id;
  }
  
  public void getKeywords(Context paramContext, Set<String> paramSet)
  {
    getKeywords(paramContext, this.m_RawId, paramSet, true);
  }
  
  public int getRawId()
  {
    return this.m_RawId;
  }
  
  public int hashCode()
  {
    return this.m_RawId;
  }
  
  public String toString()
  {
    return this.m_Id;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/media/content/MediaContentRecognitionScene.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */