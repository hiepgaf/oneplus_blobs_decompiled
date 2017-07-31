package android.hardware.soundtrigger;

import android.util.ArraySet;
import java.util.Locale;

public class KeyphraseMetadata
{
  public final int id;
  public final String keyphrase;
  public final int recognitionModeFlags;
  public final ArraySet<Locale> supportedLocales;
  
  public KeyphraseMetadata(int paramInt1, String paramString, ArraySet<Locale> paramArraySet, int paramInt2)
  {
    this.id = paramInt1;
    this.keyphrase = paramString;
    this.supportedLocales = paramArraySet;
    this.recognitionModeFlags = paramInt2;
  }
  
  public boolean supportsLocale(Locale paramLocale)
  {
    if (!this.supportedLocales.isEmpty()) {
      return this.supportedLocales.contains(paramLocale);
    }
    return true;
  }
  
  public boolean supportsPhrase(String paramString)
  {
    if (!this.keyphrase.isEmpty()) {
      return this.keyphrase.equalsIgnoreCase(paramString);
    }
    return true;
  }
  
  public String toString()
  {
    return "id=" + this.id + ", keyphrase=" + this.keyphrase + ", supported-locales=" + this.supportedLocales + ", recognition-modes=" + this.recognitionModeFlags;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/hardware/soundtrigger/KeyphraseMetadata.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */