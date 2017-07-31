package android.speech.tts;

import android.text.TextUtils;

class EventLoggerV1
  extends AbstractEventLogger
{
  private final SynthesisRequest mRequest;
  
  EventLoggerV1(SynthesisRequest paramSynthesisRequest, int paramInt1, int paramInt2, String paramString)
  {
    super(paramInt1, paramInt2, paramString);
    this.mRequest = paramSynthesisRequest;
  }
  
  private String getLocaleString()
  {
    StringBuilder localStringBuilder = new StringBuilder(this.mRequest.getLanguage());
    if (!TextUtils.isEmpty(this.mRequest.getCountry()))
    {
      localStringBuilder.append('-');
      localStringBuilder.append(this.mRequest.getCountry());
      if (!TextUtils.isEmpty(this.mRequest.getVariant()))
      {
        localStringBuilder.append('-');
        localStringBuilder.append(this.mRequest.getVariant());
      }
    }
    return localStringBuilder.toString();
  }
  
  private int getUtteranceLength()
  {
    String str = this.mRequest.getText();
    if (str == null) {
      return 0;
    }
    return str.length();
  }
  
  protected void logFailure(int paramInt)
  {
    if (paramInt != -2) {
      EventLogTags.writeTtsSpeakFailure(this.mServiceApp, this.mCallerUid, this.mCallerPid, getUtteranceLength(), getLocaleString(), this.mRequest.getSpeechRate(), this.mRequest.getPitch());
    }
  }
  
  protected void logSuccess(long paramLong1, long paramLong2, long paramLong3)
  {
    EventLogTags.writeTtsSpeakSuccess(this.mServiceApp, this.mCallerUid, this.mCallerPid, getUtteranceLength(), getLocaleString(), this.mRequest.getSpeechRate(), this.mRequest.getPitch(), paramLong2, paramLong3, paramLong1);
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/speech/tts/EventLoggerV1.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */