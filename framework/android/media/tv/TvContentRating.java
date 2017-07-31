package android.media.tv;

import android.text.TextUtils;
import com.android.internal.util.Preconditions;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class TvContentRating
{
  private static final String DELIMITER = "/";
  public static final TvContentRating UNRATED = new TvContentRating("null", "null", "null", null);
  private final String mDomain;
  private final int mHashCode;
  private final String mRating;
  private final String mRatingSystem;
  private final String[] mSubRatings;
  
  private TvContentRating(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString)
  {
    this.mDomain = paramString1;
    this.mRatingSystem = paramString2;
    this.mRating = paramString3;
    if ((paramArrayOfString == null) || (paramArrayOfString.length == 0)) {}
    for (this.mSubRatings = null;; this.mSubRatings = paramArrayOfString)
    {
      this.mHashCode = (Objects.hash(new Object[] { this.mDomain, this.mRating }) * 31 + Arrays.hashCode(this.mSubRatings));
      return;
      Arrays.sort(paramArrayOfString);
    }
  }
  
  public static TvContentRating createRating(String paramString1, String paramString2, String paramString3, String... paramVarArgs)
  {
    if (TextUtils.isEmpty(paramString1)) {
      throw new IllegalArgumentException("domain cannot be empty");
    }
    if (TextUtils.isEmpty(paramString2)) {
      throw new IllegalArgumentException("ratingSystem cannot be empty");
    }
    if (TextUtils.isEmpty(paramString3)) {
      throw new IllegalArgumentException("rating cannot be empty");
    }
    return new TvContentRating(paramString1, paramString2, paramString3, paramVarArgs);
  }
  
  public static TvContentRating unflattenFromString(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      throw new IllegalArgumentException("ratingString cannot be empty");
    }
    String[] arrayOfString = paramString.split("/");
    if (arrayOfString.length < 3) {
      throw new IllegalArgumentException("Invalid rating string: " + paramString);
    }
    if (arrayOfString.length > 3)
    {
      paramString = new String[arrayOfString.length - 3];
      System.arraycopy(arrayOfString, 3, paramString, 0, paramString.length);
      return new TvContentRating(arrayOfString[0], arrayOfString[1], arrayOfString[2], paramString);
    }
    return new TvContentRating(arrayOfString[0], arrayOfString[1], arrayOfString[2], null);
  }
  
  public final boolean contains(TvContentRating paramTvContentRating)
  {
    Preconditions.checkNotNull(paramTvContentRating);
    if (!paramTvContentRating.getMainRating().equals(this.mRating)) {
      return false;
    }
    List localList;
    if ((paramTvContentRating.getDomain().equals(this.mDomain)) && (paramTvContentRating.getRatingSystem().equals(this.mRatingSystem)) && (paramTvContentRating.getMainRating().equals(this.mRating)))
    {
      localList = getSubRatings();
      paramTvContentRating = paramTvContentRating.getSubRatings();
      if ((localList == null) && (paramTvContentRating == null)) {
        return true;
      }
    }
    else
    {
      return false;
    }
    if ((localList == null) && (paramTvContentRating != null)) {
      return false;
    }
    if ((localList != null) && (paramTvContentRating == null)) {
      return true;
    }
    return localList.containsAll(paramTvContentRating);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof TvContentRating)) {
      return false;
    }
    if (this.mHashCode != ((TvContentRating)paramObject).mHashCode) {
      return false;
    }
    if (!TextUtils.equals(this.mDomain, ((TvContentRating)paramObject).mDomain)) {
      return false;
    }
    if (!TextUtils.equals(this.mRatingSystem, ((TvContentRating)paramObject).mRatingSystem)) {
      return false;
    }
    if (!TextUtils.equals(this.mRating, ((TvContentRating)paramObject).mRating)) {
      return false;
    }
    return Arrays.equals(this.mSubRatings, ((TvContentRating)paramObject).mSubRatings);
  }
  
  public String flattenToString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(this.mDomain);
    localStringBuilder.append("/");
    localStringBuilder.append(this.mRatingSystem);
    localStringBuilder.append("/");
    localStringBuilder.append(this.mRating);
    if (this.mSubRatings != null)
    {
      String[] arrayOfString = this.mSubRatings;
      int i = 0;
      int j = arrayOfString.length;
      while (i < j)
      {
        String str = arrayOfString[i];
        localStringBuilder.append("/");
        localStringBuilder.append(str);
        i += 1;
      }
    }
    return localStringBuilder.toString();
  }
  
  public String getDomain()
  {
    return this.mDomain;
  }
  
  public String getMainRating()
  {
    return this.mRating;
  }
  
  public String getRatingSystem()
  {
    return this.mRatingSystem;
  }
  
  public List<String> getSubRatings()
  {
    if (this.mSubRatings == null) {
      return null;
    }
    return Collections.unmodifiableList(Arrays.asList(this.mSubRatings));
  }
  
  public int hashCode()
  {
    return this.mHashCode;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/tv/TvContentRating.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */