package android.service.notification;

import android.content.Context;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Objects;

public final class Condition
  implements Parcelable
{
  public static final Parcelable.Creator<Condition> CREATOR = new Parcelable.Creator()
  {
    public Condition createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Condition(paramAnonymousParcel);
    }
    
    public Condition[] newArray(int paramAnonymousInt)
    {
      return new Condition[paramAnonymousInt];
    }
  };
  public static final int FLAG_RELEVANT_ALWAYS = 2;
  public static final int FLAG_RELEVANT_NOW = 1;
  public static final String SCHEME = "condition";
  public static final int STATE_ERROR = 3;
  public static final int STATE_FALSE = 0;
  public static final int STATE_TRUE = 1;
  public static final int STATE_UNKNOWN = 2;
  public final int flags;
  public final int icon;
  public final Uri id;
  public final String line1;
  public final String line2;
  public final int state;
  public final String summary;
  
  public Condition(Uri paramUri, String paramString, int paramInt)
  {
    this(paramUri, paramString, "", "", -1, paramInt, 2);
  }
  
  public Condition(Uri paramUri, String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramUri == null) {
      throw new IllegalArgumentException("id is required");
    }
    if (paramString1 == null) {
      throw new IllegalArgumentException("summary is required");
    }
    if (!isValidState(paramInt2)) {
      throw new IllegalArgumentException("state is invalid: " + paramInt2);
    }
    this.id = paramUri;
    this.summary = paramString1;
    this.line1 = paramString2;
    this.line2 = paramString3;
    this.icon = paramInt1;
    this.state = paramInt2;
    this.flags = paramInt3;
  }
  
  public Condition(Parcel paramParcel)
  {
    this((Uri)paramParcel.readParcelable(Condition.class.getClassLoader()), paramParcel.readString(), paramParcel.readString(), paramParcel.readString(), paramParcel.readInt(), paramParcel.readInt(), paramParcel.readInt());
  }
  
  public static boolean isValidId(Uri paramUri, String paramString)
  {
    if ((paramUri != null) && ("condition".equals(paramUri.getScheme()))) {
      return paramString.equals(paramUri.getAuthority());
    }
    return false;
  }
  
  private static boolean isValidState(int paramInt)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (paramInt >= 0)
    {
      bool1 = bool2;
      if (paramInt <= 3) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  public static Uri.Builder newId(Context paramContext)
  {
    return new Uri.Builder().scheme("condition").authority(paramContext.getPackageName());
  }
  
  public static String relevanceToString(int paramInt)
  {
    int i;
    if ((paramInt & 0x1) != 0)
    {
      i = 1;
      if ((paramInt & 0x2) == 0) {
        break label40;
      }
    }
    label40:
    for (paramInt = 1;; paramInt = 0)
    {
      if ((i == 0) && (paramInt == 0)) {
        break label45;
      }
      if ((i == 0) || (paramInt == 0)) {
        break label48;
      }
      return "NOW, ALWAYS";
      i = 0;
      break;
    }
    label45:
    return "NONE";
    label48:
    if (i != 0) {
      return "NOW";
    }
    return "ALWAYS";
  }
  
  public static String stateToString(int paramInt)
  {
    if (paramInt == 0) {
      return "STATE_FALSE";
    }
    if (paramInt == 1) {
      return "STATE_TRUE";
    }
    if (paramInt == 2) {
      return "STATE_UNKNOWN";
    }
    if (paramInt == 3) {
      return "STATE_ERROR";
    }
    throw new IllegalArgumentException("state is invalid: " + paramInt);
  }
  
  public Condition copy()
  {
    Parcel localParcel = Parcel.obtain();
    try
    {
      writeToParcel(localParcel, 0);
      localParcel.setDataPosition(0);
      Condition localCondition = new Condition(localParcel);
      return localCondition;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof Condition)) {
      return false;
    }
    if (paramObject == this) {
      return true;
    }
    paramObject = (Condition)paramObject;
    if ((Objects.equals(((Condition)paramObject).id, this.id)) && (Objects.equals(((Condition)paramObject).summary, this.summary)) && (Objects.equals(((Condition)paramObject).line1, this.line1)) && (Objects.equals(((Condition)paramObject).line2, this.line2)) && (((Condition)paramObject).icon == this.icon) && (((Condition)paramObject).state == this.state)) {
      return ((Condition)paramObject).flags == this.flags;
    }
    return false;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.id, this.summary, this.line1, this.line2, Integer.valueOf(this.icon), Integer.valueOf(this.state), Integer.valueOf(this.flags) });
  }
  
  public String toString()
  {
    return Condition.class.getSimpleName() + '[' + "id=" + this.id + ",summary=" + this.summary + ",line1=" + this.line1 + ",line2=" + this.line2 + ",icon=" + this.icon + ",state=" + stateToString(this.state) + ",flags=" + this.flags + ']';
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeParcelable(this.id, 0);
    paramParcel.writeString(this.summary);
    paramParcel.writeString(this.line1);
    paramParcel.writeString(this.line2);
    paramParcel.writeInt(this.icon);
    paramParcel.writeInt(this.state);
    paramParcel.writeInt(this.flags);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/notification/Condition.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */