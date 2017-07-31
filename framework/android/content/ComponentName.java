package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import java.io.PrintWriter;

public final class ComponentName
  implements Parcelable, Cloneable, Comparable<ComponentName>
{
  public static final Parcelable.Creator<ComponentName> CREATOR = new Parcelable.Creator()
  {
    public ComponentName createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ComponentName(paramAnonymousParcel);
    }
    
    public ComponentName[] newArray(int paramAnonymousInt)
    {
      return new ComponentName[paramAnonymousInt];
    }
  };
  private final String mClass;
  private final String mPackage;
  
  public ComponentName(Context paramContext, Class<?> paramClass)
  {
    this.mPackage = paramContext.getPackageName();
    this.mClass = paramClass.getName();
  }
  
  public ComponentName(Context paramContext, String paramString)
  {
    if (paramString == null) {
      throw new NullPointerException("class name is null");
    }
    this.mPackage = paramContext.getPackageName();
    this.mClass = paramString;
  }
  
  public ComponentName(Parcel paramParcel)
  {
    this.mPackage = paramParcel.readString();
    if (this.mPackage == null) {
      throw new NullPointerException("package name is null");
    }
    this.mClass = paramParcel.readString();
    if (this.mClass == null) {
      throw new NullPointerException("class name is null");
    }
  }
  
  private ComponentName(String paramString, Parcel paramParcel)
  {
    this.mPackage = paramString;
    this.mClass = paramParcel.readString();
  }
  
  public ComponentName(String paramString1, String paramString2)
  {
    if (paramString1 == null) {
      throw new NullPointerException("package name is null");
    }
    if (paramString2 == null) {
      throw new NullPointerException("class name is null");
    }
    this.mPackage = paramString1;
    this.mClass = paramString2;
  }
  
  private static void appendShortClassName(StringBuilder paramStringBuilder, String paramString1, String paramString2)
  {
    if (paramString2.startsWith(paramString1))
    {
      int i = paramString1.length();
      int j = paramString2.length();
      if ((j > i) && (paramString2.charAt(i) == '.'))
      {
        paramStringBuilder.append(paramString2, i, j);
        return;
      }
    }
    paramStringBuilder.append(paramString2);
  }
  
  public static void appendShortString(StringBuilder paramStringBuilder, String paramString1, String paramString2)
  {
    paramStringBuilder.append(paramString1).append('/');
    appendShortClassName(paramStringBuilder, paramString1, paramString2);
  }
  
  public static ComponentName createRelative(Context paramContext, String paramString)
  {
    return createRelative(paramContext.getPackageName(), paramString);
  }
  
  public static ComponentName createRelative(String paramString1, String paramString2)
  {
    if (TextUtils.isEmpty(paramString2)) {
      throw new IllegalArgumentException("class name cannot be empty");
    }
    if (paramString2.charAt(0) == '.') {
      paramString2 = paramString1 + paramString2;
    }
    for (;;)
    {
      return new ComponentName(paramString1, paramString2);
    }
  }
  
  private static void printShortClassName(PrintWriter paramPrintWriter, String paramString1, String paramString2)
  {
    if (paramString2.startsWith(paramString1))
    {
      int i = paramString1.length();
      int j = paramString2.length();
      if ((j > i) && (paramString2.charAt(i) == '.'))
      {
        paramPrintWriter.write(paramString2, i, j - i);
        return;
      }
    }
    paramPrintWriter.print(paramString2);
  }
  
  public static void printShortString(PrintWriter paramPrintWriter, String paramString1, String paramString2)
  {
    paramPrintWriter.print(paramString1);
    paramPrintWriter.print('/');
    printShortClassName(paramPrintWriter, paramString1, paramString2);
  }
  
  public static ComponentName readFromParcel(Parcel paramParcel)
  {
    ComponentName localComponentName = null;
    String str = paramParcel.readString();
    if (str != null) {
      localComponentName = new ComponentName(str, paramParcel);
    }
    return localComponentName;
  }
  
  public static ComponentName unflattenFromString(String paramString)
  {
    int i = paramString.indexOf('/');
    if ((i < 0) || (i + 1 >= paramString.length())) {
      return null;
    }
    String str2 = paramString.substring(0, i);
    String str1 = paramString.substring(i + 1);
    paramString = str1;
    if (str1.length() > 0)
    {
      paramString = str1;
      if (str1.charAt(0) == '.') {
        paramString = str2 + str1;
      }
    }
    return new ComponentName(str2, paramString);
  }
  
  public static void writeToParcel(ComponentName paramComponentName, Parcel paramParcel)
  {
    if (paramComponentName != null)
    {
      paramComponentName.writeToParcel(paramParcel, 0);
      return;
    }
    paramParcel.writeString(null);
  }
  
  public void appendShortString(StringBuilder paramStringBuilder)
  {
    appendShortString(paramStringBuilder, this.mPackage, this.mClass);
  }
  
  public ComponentName clone()
  {
    return new ComponentName(this.mPackage, this.mClass);
  }
  
  public int compareTo(ComponentName paramComponentName)
  {
    int i = this.mPackage.compareTo(paramComponentName.mPackage);
    if (i != 0) {
      return i;
    }
    return this.mClass.compareTo(paramComponentName.mClass);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = false;
    if (paramObject != null) {
      try
      {
        paramObject = (ComponentName)paramObject;
        if (this.mPackage.equals(((ComponentName)paramObject).mPackage)) {
          bool = this.mClass.equals(((ComponentName)paramObject).mClass);
        }
        return bool;
      }
      catch (ClassCastException paramObject) {}
    }
    return false;
  }
  
  public String flattenToShortString()
  {
    StringBuilder localStringBuilder = new StringBuilder(this.mPackage.length() + this.mClass.length());
    appendShortString(localStringBuilder, this.mPackage, this.mClass);
    return localStringBuilder.toString();
  }
  
  public String flattenToString()
  {
    return this.mPackage + "/" + this.mClass;
  }
  
  public String getClassName()
  {
    return this.mClass;
  }
  
  public String getPackageName()
  {
    return this.mPackage;
  }
  
  public String getShortClassName()
  {
    if (this.mClass.startsWith(this.mPackage))
    {
      int i = this.mPackage.length();
      int j = this.mClass.length();
      if ((j > i) && (this.mClass.charAt(i) == '.')) {
        return this.mClass.substring(i, j);
      }
    }
    return this.mClass;
  }
  
  public int hashCode()
  {
    return this.mPackage.hashCode() + this.mClass.hashCode();
  }
  
  public String toShortString()
  {
    return "{" + this.mPackage + "/" + this.mClass + "}";
  }
  
  public String toString()
  {
    return "ComponentInfo{" + this.mPackage + "/" + this.mClass + "}";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mPackage);
    paramParcel.writeString(this.mClass);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ComponentName.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */