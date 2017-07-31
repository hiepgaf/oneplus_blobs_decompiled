package android.speech.tts;

import android.os.Bundle;

public final class SynthesisRequest
{
  private int mCallerUid;
  private String mCountry;
  private String mLanguage;
  private final Bundle mParams;
  private int mPitch;
  private int mSpeechRate;
  private final CharSequence mText;
  private String mVariant;
  private String mVoiceName;
  
  public SynthesisRequest(CharSequence paramCharSequence, Bundle paramBundle)
  {
    this.mText = paramCharSequence;
    this.mParams = new Bundle(paramBundle);
  }
  
  public SynthesisRequest(String paramString, Bundle paramBundle)
  {
    this.mText = paramString;
    this.mParams = new Bundle(paramBundle);
  }
  
  public int getCallerUid()
  {
    return this.mCallerUid;
  }
  
  public CharSequence getCharSequenceText()
  {
    return this.mText;
  }
  
  public String getCountry()
  {
    return this.mCountry;
  }
  
  public String getLanguage()
  {
    return this.mLanguage;
  }
  
  public Bundle getParams()
  {
    return this.mParams;
  }
  
  public int getPitch()
  {
    return this.mPitch;
  }
  
  public int getSpeechRate()
  {
    return this.mSpeechRate;
  }
  
  @Deprecated
  public String getText()
  {
    return this.mText.toString();
  }
  
  public String getVariant()
  {
    return this.mVariant;
  }
  
  public String getVoiceName()
  {
    return this.mVoiceName;
  }
  
  void setCallerUid(int paramInt)
  {
    this.mCallerUid = paramInt;
  }
  
  void setLanguage(String paramString1, String paramString2, String paramString3)
  {
    this.mLanguage = paramString1;
    this.mCountry = paramString2;
    this.mVariant = paramString3;
  }
  
  void setPitch(int paramInt)
  {
    this.mPitch = paramInt;
  }
  
  void setSpeechRate(int paramInt)
  {
    this.mSpeechRate = paramInt;
  }
  
  void setVoiceName(String paramString)
  {
    this.mVoiceName = paramString;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/SynthesisRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */