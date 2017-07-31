package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

public final class Rating
  implements Parcelable
{
  public static final Parcelable.Creator<Rating> CREATOR = new Parcelable.Creator()
  {
    public Rating createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Rating(paramAnonymousParcel.readInt(), paramAnonymousParcel.readFloat(), null);
    }
    
    public Rating[] newArray(int paramAnonymousInt)
    {
      return new Rating[paramAnonymousInt];
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
  private final int mRatingStyle;
  private final float mRatingValue;
  
  private Rating(int paramInt, float paramFloat)
  {
    this.mRatingStyle = paramInt;
    this.mRatingValue = paramFloat;
  }
  
  public static Rating newHeartRating(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F) {
      return new Rating(1, f);
    }
  }
  
  public static Rating newPercentageRating(float paramFloat)
  {
    if ((paramFloat < 0.0F) || (paramFloat > 100.0F))
    {
      Log.e("Rating", "Invalid percentage-based rating value");
      return null;
    }
    return new Rating(6, paramFloat);
  }
  
  public static Rating newStarRating(int paramInt, float paramFloat)
  {
    float f;
    switch (paramInt)
    {
    default: 
      Log.e("Rating", "Invalid rating style (" + paramInt + ") for a star rating");
      return null;
    case 3: 
      f = 3.0F;
    }
    while ((paramFloat < 0.0F) || (paramFloat > f))
    {
      Log.e("Rating", "Trying to set out of range star-based rating");
      return null;
      f = 4.0F;
      continue;
      f = 5.0F;
    }
    return new Rating(paramInt, paramFloat);
  }
  
  public static Rating newThumbRating(boolean paramBoolean)
  {
    if (paramBoolean) {}
    for (float f = 1.0F;; f = 0.0F) {
      return new Rating(2, f);
    }
  }
  
  public static Rating newUnratedRating(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    }
    return new Rating(paramInt, -1.0F);
  }
  
  public int describeContents()
  {
    return this.mRatingStyle;
  }
  
  public float getPercentRating()
  {
    if ((this.mRatingStyle == 6) && (isRated())) {
      return this.mRatingValue;
    }
    return -1.0F;
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
    if (this.mRatingStyle != 1) {
      return false;
    }
    return this.mRatingValue == 1.0F;
  }
  
  public boolean isRated()
  {
    return this.mRatingValue >= 0.0F;
  }
  
  public boolean isThumbUp()
  {
    boolean bool = false;
    if (this.mRatingStyle != 2) {
      return false;
    }
    if (this.mRatingValue == 1.0F) {
      bool = true;
    }
    return bool;
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


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/Rating.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */