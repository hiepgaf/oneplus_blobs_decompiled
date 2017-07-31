package android.support.v4.text;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Locale;

public class TextUtilsCompat
{
  private static String ARAB_SCRIPT_SUBTAG = "Arab";
  private static String HEBR_SCRIPT_SUBTAG = "Hebr";
  public static final Locale ROOT = new Locale("", "");
  
  private static int getLayoutDirectionFromFirstChar(Locale paramLocale)
  {
    switch (Character.getDirectionality(paramLocale.getDisplayName(paramLocale).charAt(0)))
    {
    case 0: 
    default: 
      return 0;
    }
    return 1;
  }
  
  public static int getLayoutDirectionFromLocale(@Nullable Locale paramLocale)
  {
    if (paramLocale == null) {}
    while (paramLocale.equals(ROOT)) {
      return 0;
    }
    String str = ICUCompat.getScript(ICUCompat.addLikelySubtags(paramLocale.toString()));
    if (str != null) {
      if (!str.equalsIgnoreCase(ARAB_SCRIPT_SUBTAG)) {
        break label48;
      }
    }
    label48:
    while (str.equalsIgnoreCase(HEBR_SCRIPT_SUBTAG))
    {
      return 1;
      return getLayoutDirectionFromFirstChar(paramLocale);
    }
    return 0;
  }
  
  @NonNull
  public static String htmlEncode(@NonNull String paramString)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    if (i >= paramString.length()) {
      return localStringBuilder.toString();
    }
    char c = paramString.charAt(i);
    switch (c)
    {
    default: 
      localStringBuilder.append(c);
    }
    for (;;)
    {
      i += 1;
      break;
      localStringBuilder.append("&lt;");
      continue;
      localStringBuilder.append("&gt;");
      continue;
      localStringBuilder.append("&amp;");
      continue;
      localStringBuilder.append("&#39;");
      continue;
      localStringBuilder.append("&quot;");
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/text/TextUtilsCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */