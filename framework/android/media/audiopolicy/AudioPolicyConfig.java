package android.media.audiopolicy;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioFormat.Builder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class AudioPolicyConfig
  implements Parcelable
{
  public static final Parcelable.Creator<AudioPolicyConfig> CREATOR = new Parcelable.Creator()
  {
    public AudioPolicyConfig createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AudioPolicyConfig(paramAnonymousParcel, null);
    }
    
    public AudioPolicyConfig[] newArray(int paramAnonymousInt)
    {
      return new AudioPolicyConfig[paramAnonymousInt];
    }
  };
  private static final String TAG = "AudioPolicyConfig";
  protected int mDuckingPolicy = 0;
  protected ArrayList<AudioMix> mMixes;
  private String mRegistrationId = null;
  
  protected AudioPolicyConfig(AudioPolicyConfig paramAudioPolicyConfig)
  {
    this.mMixes = paramAudioPolicyConfig.mMixes;
  }
  
  private AudioPolicyConfig(Parcel paramParcel)
  {
    this.mMixes = new ArrayList();
    int k = paramParcel.readInt();
    int i = 0;
    while (i < k)
    {
      AudioMix.Builder localBuilder = new AudioMix.Builder();
      localBuilder.setRouteFlags(paramParcel.readInt());
      localBuilder.setCallbackFlags(paramParcel.readInt());
      localBuilder.setDevice(paramParcel.readInt(), paramParcel.readString());
      int j = paramParcel.readInt();
      int m = paramParcel.readInt();
      int n = paramParcel.readInt();
      localBuilder.setFormat(new AudioFormat.Builder().setSampleRate(j).setChannelMask(n).setEncoding(m).build());
      m = paramParcel.readInt();
      AudioMixingRule.Builder localBuilder1 = new AudioMixingRule.Builder();
      j = 0;
      while (j < m)
      {
        localBuilder1.addRuleFromParcel(paramParcel);
        j += 1;
      }
      localBuilder.setMixingRule(localBuilder1.build());
      this.mMixes.add(localBuilder.build());
      i += 1;
    }
  }
  
  AudioPolicyConfig(ArrayList<AudioMix> paramArrayList)
  {
    this.mMixes = paramArrayList;
  }
  
  private static String mixTypeId(int paramInt)
  {
    if (paramInt == 0) {
      return "p";
    }
    if (paramInt == 1) {
      return "r";
    }
    return "i";
  }
  
  public void addMix(AudioMix paramAudioMix)
    throws IllegalArgumentException
  {
    if (paramAudioMix == null) {
      throw new IllegalArgumentException("Illegal null AudioMix argument");
    }
    this.mMixes.add(paramAudioMix);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public ArrayList<AudioMix> getMixes()
  {
    return this.mMixes;
  }
  
  protected String getRegistration()
  {
    return this.mRegistrationId;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { this.mMixes });
  }
  
  protected void setRegistration(String paramString)
  {
    boolean bool1;
    boolean bool2;
    label25:
    label34:
    Object localObject;
    int i;
    if (this.mRegistrationId != null)
    {
      bool1 = this.mRegistrationId.isEmpty();
      if (paramString == null) {
        break label163;
      }
      bool2 = paramString.isEmpty();
      if ((!bool1) && (!bool2)) {
        break label169;
      }
      localObject = paramString;
      if (paramString == null) {
        localObject = "";
      }
      this.mRegistrationId = ((String)localObject);
      i = 0;
      paramString = this.mMixes.iterator();
    }
    for (;;)
    {
      if (!paramString.hasNext()) {
        return;
      }
      localObject = (AudioMix)paramString.next();
      if (!this.mRegistrationId.isEmpty())
      {
        if ((((AudioMix)localObject).getRouteFlags() & 0x2) == 2)
        {
          ((AudioMix)localObject).setRegistration(this.mRegistrationId + "mix" + mixTypeId(((AudioMix)localObject).getMixType()) + ":" + i);
          i += 1;
          continue;
          bool1 = true;
          break;
          label163:
          bool2 = true;
          break label25;
          label169:
          if (this.mRegistrationId.equals(paramString)) {
            break label34;
          }
          Log.e("AudioPolicyConfig", "Invalid registration transition from " + this.mRegistrationId + " to " + paramString);
          return;
        }
        if ((((AudioMix)localObject).getRouteFlags() & 0x1) != 1) {
          continue;
        }
        ((AudioMix)localObject).setRegistration(((AudioMix)localObject).mDeviceAddress);
        continue;
      }
      ((AudioMix)localObject).setRegistration("");
    }
  }
  
  public String toLogFriendlyString()
  {
    String str = new String("android.media.audiopolicy.AudioPolicyConfig:\n");
    Object localObject1 = str + this.mMixes.size() + " AudioMix: " + this.mRegistrationId + "\n";
    Iterator localIterator = this.mMixes.iterator();
    while (localIterator.hasNext())
    {
      Object localObject2 = (AudioMix)localIterator.next();
      str = (String)localObject1 + "* route flags=0x" + Integer.toHexString(((AudioMix)localObject2).getRouteFlags()) + "\n";
      str = str + "  rate=" + ((AudioMix)localObject2).getFormat().getSampleRate() + "Hz\n";
      str = str + "  encoding=" + ((AudioMix)localObject2).getFormat().getEncoding() + "\n";
      str = str + "  channels=0x";
      str = str + Integer.toHexString(((AudioMix)localObject2).getFormat().getChannelMask()).toUpperCase() + "\n";
      localObject2 = ((AudioMix)localObject2).getRule().getCriteria().iterator();
      localObject1 = str;
      if (((Iterator)localObject2).hasNext())
      {
        localObject1 = (AudioMixingRule.AudioMixMatchCriterion)((Iterator)localObject2).next();
        switch (((AudioMixingRule.AudioMixMatchCriterion)localObject1).mRule)
        {
        default: 
          str = str + "invalid rule!";
        }
        for (;;)
        {
          str = str + "\n";
          break;
          str = str + "  exclude usage ";
          str = str + ((AudioMixingRule.AudioMixMatchCriterion)localObject1).mAttr.usageToString();
          continue;
          str = str + "  match usage ";
          str = str + ((AudioMixingRule.AudioMixMatchCriterion)localObject1).mAttr.usageToString();
          continue;
          str = str + "  exclude capture preset ";
          str = str + ((AudioMixingRule.AudioMixMatchCriterion)localObject1).mAttr.getCapturePreset();
          continue;
          str = str + "  match capture preset ";
          str = str + ((AudioMixingRule.AudioMixMatchCriterion)localObject1).mAttr.getCapturePreset();
          continue;
          str = str + "  match UID ";
          str = str + ((AudioMixingRule.AudioMixMatchCriterion)localObject1).mIntProp;
          continue;
          str = str + "  exclude UID ";
          str = str + ((AudioMixingRule.AudioMixMatchCriterion)localObject1).mIntProp;
        }
      }
    }
    return (String)localObject1;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.mMixes.size());
    Iterator localIterator = this.mMixes.iterator();
    while (localIterator.hasNext())
    {
      Object localObject = (AudioMix)localIterator.next();
      paramParcel.writeInt(((AudioMix)localObject).getRouteFlags());
      paramParcel.writeInt(((AudioMix)localObject).mCallbackFlags);
      paramParcel.writeInt(((AudioMix)localObject).mDeviceSystemType);
      paramParcel.writeString(((AudioMix)localObject).mDeviceAddress);
      paramParcel.writeInt(((AudioMix)localObject).getFormat().getSampleRate());
      paramParcel.writeInt(((AudioMix)localObject).getFormat().getEncoding());
      paramParcel.writeInt(((AudioMix)localObject).getFormat().getChannelMask());
      localObject = ((AudioMix)localObject).getRule().getCriteria();
      paramParcel.writeInt(((ArrayList)localObject).size());
      localObject = ((Iterable)localObject).iterator();
      while (((Iterator)localObject).hasNext()) {
        ((AudioMixingRule.AudioMixMatchCriterion)((Iterator)localObject).next()).writeToParcel(paramParcel);
      }
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiopolicy/AudioPolicyConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */