package android.content;

import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

public class UriMatcher
{
  private static final int EXACT = 0;
  public static final int NO_MATCH = -1;
  private static final int NUMBER = 1;
  private static final int TEXT = 2;
  private ArrayList<UriMatcher> mChildren;
  private int mCode;
  private String mText;
  private int mWhich;
  
  private UriMatcher()
  {
    this.mCode = -1;
    this.mWhich = -1;
    this.mChildren = new ArrayList();
    this.mText = null;
  }
  
  public UriMatcher(int paramInt)
  {
    this.mCode = paramInt;
    this.mWhich = -1;
    this.mChildren = new ArrayList();
    this.mText = null;
  }
  
  public void addURI(String paramString1, String paramString2, int paramInt)
  {
    if (paramInt < 0) {
      throw new IllegalArgumentException("code " + paramInt + " is invalid: it must be positive");
    }
    Object localObject2 = null;
    Object localObject1;
    if (paramString2 != null)
    {
      localObject1 = paramString2;
      localObject2 = localObject1;
      if (paramString2.length() > 0)
      {
        localObject2 = localObject1;
        if (paramString2.charAt(0) == '/') {
          localObject2 = paramString2.substring(1);
        }
      }
      localObject2 = ((String)localObject2).split("/");
    }
    int i;
    int j;
    label102:
    String str;
    label117:
    int k;
    if (localObject2 != null)
    {
      i = localObject2.length;
      paramString2 = this;
      j = -1;
      if (j >= i) {
        break label277;
      }
      if (j >= 0) {
        break label232;
      }
      str = paramString1;
      ArrayList localArrayList = paramString2.mChildren;
      int m = localArrayList.size();
      k = 0;
      label133:
      localObject1 = paramString2;
      if (k < m)
      {
        localObject1 = (UriMatcher)localArrayList.get(k);
        if (!str.equals(((UriMatcher)localObject1).mText)) {
          break label242;
        }
      }
      paramString2 = (String)localObject1;
      if (k == m)
      {
        paramString2 = new UriMatcher();
        if (!str.equals("#")) {
          break label251;
        }
        paramString2.mWhich = 1;
      }
    }
    for (;;)
    {
      paramString2.mText = str;
      ((UriMatcher)localObject1).mChildren.add(paramString2);
      j += 1;
      break label102;
      i = 0;
      break;
      label232:
      str = localObject2[j];
      break label117;
      label242:
      k += 1;
      break label133;
      label251:
      if (str.equals("*")) {
        paramString2.mWhich = 2;
      } else {
        paramString2.mWhich = 0;
      }
    }
    label277:
    paramString2.mCode = paramInt;
  }
  
  public int match(Uri paramUri)
  {
    List localList = paramUri.getPathSegments();
    int m = localList.size();
    Object localObject1 = this;
    if ((m == 0) && (paramUri.getAuthority() == null)) {
      return this.mCode;
    }
    int i = -1;
    for (;;)
    {
      if (i < m) {
        if (i >= 0) {
          break label71;
        }
      }
      ArrayList localArrayList;
      label71:
      for (String str = paramUri.getAuthority();; str = (String)localList.get(i))
      {
        localArrayList = ((UriMatcher)localObject1).mChildren;
        if (localArrayList != null) {
          break;
        }
        return ((UriMatcher)localObject1).mCode;
      }
      Object localObject2 = null;
      int n = localArrayList.size();
      int j = 0;
      for (;;)
      {
        localObject1 = localObject2;
        UriMatcher localUriMatcher;
        if (j < n)
        {
          localUriMatcher = (UriMatcher)localArrayList.get(j);
          switch (localUriMatcher.mWhich)
          {
          default: 
            localObject1 = localObject2;
          }
        }
        while (localObject1 != null)
        {
          if (localObject1 != null) {
            break label274;
          }
          return -1;
          localObject1 = localObject2;
          if (localUriMatcher.mText.equals(str))
          {
            localObject1 = localUriMatcher;
            continue;
            int i1 = str.length();
            int k = 0;
            for (;;)
            {
              if (k >= i1) {
                break label249;
              }
              int i2 = str.charAt(k);
              localObject1 = localObject2;
              if (i2 < 48) {
                break;
              }
              localObject1 = localObject2;
              if (i2 > 57) {
                break;
              }
              k += 1;
            }
            label249:
            localObject1 = localUriMatcher;
            continue;
            localObject1 = localUriMatcher;
          }
        }
        j += 1;
        localObject2 = localObject1;
      }
      label274:
      i += 1;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/UriMatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */