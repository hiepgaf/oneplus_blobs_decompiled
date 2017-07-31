package android.support.v4.media;

import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

public final class RatingCompat
  implements Parcelable
{
  public static final Parcelable.Creator<RatingCompat> CREATOR = new Parcelable.Creator()
  {
    public RatingCompat createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RatingCompat(paramAnonymousParcel.readInt(), paramAnonymousParcel.readFloat(), null);
    }
    
    public RatingCompat[] newArray(int paramAnonymousInt)
    {
      return new RatingCompat[paramAnonymousInt];
    }
  };
  public static final int RATING_3_STARS = 3;
  public static final int RATING_4_STARS = 4;
  public static final int RATING_5_STARS = 5;
  public static final int RATING_HEART = 1;
  public static final int RATING_NONE = 0;
  private static final float RATING_NOT_RATED = -1.0F;
  public static final int RATING_PERCENTAGE = 6;
  public static final int RATING_THUMB_UP_DOWN = 2;
  private static final String TAG = "Rating";
  private Object mRatingObj;
  private final int mRatingStyle;
  private final float mRatingValue;
  
  private RatingCompat(int paramInt, float paramFloat)
  {
    this.mRatingStyle = paramInt;
    this.mRatingValue = paramFloat;
  }
  
  public static RatingCompat fromRating(Object paramObject)
  {
    if (paramObject == null) {}
    while (Build.VERSION.SDK_INT < 21) {
      return null;
    }
    int i = RatingCompatApi21.getRatingStyle(paramObject);
    RatingCompat localRatingCompat;
    if (!RatingCompatApi21.isRated(paramObject)) {
      localRatingCompat = newUnratedRating(i);
    }
    for (;;)
    {
      localRatingCompat.mRatingObj = paramObject;
      return localRatingCompat;
      switch (i)
      {
      default: 
        return null;
      case 1: 
        localRatingCompat = newHeartRating(RatingCompatApi21.hasHeart(paramObject));
        break;
      case 2: 
        localRatingCompat = newThumbRating(RatingCompatApi21.isThumbUp(paramObject));
        break;
      case 3: 
      case 4: 
      case 5: 
        localRatingCompat = newStarRating(i, RatingCompatApi21.getStarRating(paramObject));
        break;
      case 6: 
        localRatingCompat = newPercentageRating(RatingCompatApi21.getPercentRating(paramObject));
      }
    }
  }
  
  public static RatingCompat newHeartRating(boolean paramBoolean)
  {
    if (!paramBoolean) {}
    for (float f = 0.0F;; f = 1.0F) {
      return new RatingCompat(1, f);
    }
  }
  
  public static RatingCompat newPercentageRating(float paramFloat)
  {
    if (paramFloat < 0.0F) {}
    for (int i = 1; (i != 0) || (paramFloat > 100.0F); i = 0)
    {
      Log.e("Rating", "Invalid percentage-based rating value");
      return null;
    }
    return new RatingCompat(6, paramFloat);
  }
  
  public static RatingCompat newStarRating(int paramInt, float paramFloat)
  {
    float f;
    switch (paramInt)
    {
    default: 
      Log.e("Rating", "Invalid rating style (" + paramInt + ") for a star rating");
      return null;
    case 3: 
      f = 3.0F;
      if (paramFloat >= 0.0F) {
        break;
      }
    }
    for (int i = 1;; i = 0)
    {
      if ((i == 0) && (paramFloat <= f)) {
        break label108;
      }
      Log.e("Rating", "Trying to set out of range star-based rating");
      return null;
      f = 4.0F;
      break;
      f = 5.0F;
      break;
    }
    label108:
    return new RatingCompat(paramInt, paramFloat);
  }
  
  public static RatingCompat newThumbRating(boolean paramBoolean)
  {
    if (!paramBoolean) {}
    for (float f = 0.0F;; f = 1.0F) {
      return new RatingCompat(2, f);
    }
  }
  
  public static RatingCompat newUnratedRating(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    }
    return new RatingCompat(paramInt, -1.0F);
  }
  
  public int describeContents()
  {
    return this.mRatingStyle;
  }
  
  public float getPercentRating()
  {
    if (this.mRatingStyle != 6) {}
    while (!isRated()) {
      return -1.0F;
    }
    return this.mRatingValue;
  }
  
  public Object getRating()
  {
    if (this.mRatingObj != null) {}
    while (Build.VERSION.SDK_INT < 21) {
      return this.mRatingObj;
    }
    if (!isRated()) {
      this.mRatingObj = RatingCompatApi21.newUnratedRating(this.mRatingStyle);
    }
    for (;;)
    {
      return this.mRatingObj;
      switch (this.mRatingStyle)
      {
      default: 
        return null;
      case 1: 
        this.mRatingObj = RatingCompatApi21.newHeartRating(hasHeart());
        break;
      case 2: 
        this.mRatingObj = RatingCompatApi21.newThumbRating(isThumbUp());
        break;
      case 3: 
      case 4: 
      case 5: 
        this.mRatingObj = RatingCompatApi21.newStarRating(this.mRatingStyle, getStarRating());
      }
    }
    this.mRatingObj = RatingCompatApi21.newPercentageRating(getPercentRating());
    return null;
  }
  
  public int getRatingStyle()
  {
    return this.mRatingStyle;
  }
  
  public float getStarRating()
  {
    switch (this.mRatingStyle)
    {
    }
    do
    {
      return -1.0F;
    } while (!isRated());
    return this.mRatingValue;
  }
  
  public boolean hasHeart()
  {
    if (this.mRatingStyle == 1)
    {
      if (this.mRatingValue == 1.0F) {
        return true;
      }
    }
    else {
      return false;
    }
    return false;
  }
  
  public boolean isRated()
  {
    return this.mRatingValue >= 0.0F;
  }
  
  public boolean isThumbUp()
  {
    boolean bool = false;
    if (this.mRatingStyle == 2)
    {
      if (this.mRatingValue == 1.0F) {
        bool = true;
      }
      return bool;
    }
    return false;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("Rating:style=").append(this.mRatingStyle).append(" rating=");
    if (this.mRatingValue < 0.0F) {}
    for (String str = "unrated";; str = String.valueOf(this.mRatingValue)) {
      return str;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mRatingStyle);
    paramParcel.writeFloat(this.mRatingValue);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/media/RatingCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */