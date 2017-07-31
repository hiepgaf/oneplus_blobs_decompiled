package android.media.audiopolicy;

import android.media.AudioAttributes;
import android.media.AudioAttributes.Builder;
import android.os.Parcel;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class AudioMixingRule
{
  public static final int RULE_EXCLUDE_ATTRIBUTE_CAPTURE_PRESET = 32770;
  public static final int RULE_EXCLUDE_ATTRIBUTE_USAGE = 32769;
  public static final int RULE_EXCLUDE_UID = 32772;
  private static final int RULE_EXCLUSION_MASK = 32768;
  public static final int RULE_MATCH_ATTRIBUTE_CAPTURE_PRESET = 2;
  public static final int RULE_MATCH_ATTRIBUTE_USAGE = 1;
  public static final int RULE_MATCH_UID = 4;
  private final ArrayList<AudioMixMatchCriterion> mCriteria;
  private final int mTargetMixType;
  
  private AudioMixingRule(int paramInt, ArrayList<AudioMixMatchCriterion> paramArrayList)
  {
    this.mCriteria = paramArrayList;
    this.mTargetMixType = paramInt;
  }
  
  private static boolean isAudioAttributeRule(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  private static boolean isPlayerRule(int paramInt)
  {
    switch (paramInt & 0xFFFF7FFF)
    {
    case 2: 
    case 3: 
    default: 
      return false;
    }
    return true;
  }
  
  private static boolean isValidAttributesSystemApiRule(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  private static boolean isValidRule(int paramInt)
  {
    switch (paramInt & 0xFFFF7FFF)
    {
    case 3: 
    default: 
      return false;
    }
    return true;
  }
  
  private static boolean isValidSystemApiRule(int paramInt)
  {
    switch (paramInt)
    {
    case 3: 
    default: 
      return false;
    }
    return true;
  }
  
  ArrayList<AudioMixMatchCriterion> getCriteria()
  {
    return this.mCriteria;
  }
  
  int getTargetMixType()
  {
    return this.mTargetMixType;
  }
  
  public int hashCode()
  {
    return Objects.hash(new Object[] { Integer.valueOf(this.mTargetMixType), this.mCriteria });
  }
  
  static final class AudioMixMatchCriterion
  {
    final AudioAttributes mAttr;
    final int mIntProp;
    final int mRule;
    
    AudioMixMatchCriterion(AudioAttributes paramAudioAttributes, int paramInt)
    {
      this.mAttr = paramAudioAttributes;
      this.mIntProp = Integer.MIN_VALUE;
      this.mRule = paramInt;
    }
    
    AudioMixMatchCriterion(Integer paramInteger, int paramInt)
    {
      this.mAttr = null;
      this.mIntProp = paramInteger.intValue();
      this.mRule = paramInt;
    }
    
    public int hashCode()
    {
      return Objects.hash(new Object[] { this.mAttr, Integer.valueOf(this.mIntProp), Integer.valueOf(this.mRule) });
    }
    
    void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeInt(this.mRule);
      int i = this.mRule & 0xFFFF7FFF;
      switch (i)
      {
      case 3: 
      default: 
        Log.e("AudioMixMatchCriterion", "Unknown match rule" + i + " when writing to Parcel");
        paramParcel.writeInt(-1);
        return;
      case 1: 
        paramParcel.writeInt(this.mAttr.getUsage());
        return;
      case 2: 
        paramParcel.writeInt(this.mAttr.getCapturePreset());
        return;
      }
      paramParcel.writeInt(this.mIntProp);
    }
  }
  
  public static class Builder
  {
    private ArrayList<AudioMixingRule.AudioMixMatchCriterion> mCriteria = new ArrayList();
    private int mTargetMixType = -1;
    
    private Builder addRuleInternal(AudioAttributes paramAudioAttributes, Integer paramInteger, int paramInt)
      throws IllegalArgumentException
    {
      if (this.mTargetMixType == -1) {
        if (AudioMixingRule.-wrap1(paramInt)) {
          this.mTargetMixType = 0;
        }
      }
      AudioMixingRule.AudioMixMatchCriterion localAudioMixMatchCriterion;
      do
      {
        do
        {
          synchronized (this.mCriteria)
          {
            do
            {
              for (;;)
              {
                Iterator localIterator = this.mCriteria.iterator();
                i = paramInt & 0xFFFF7FFF;
                do
                {
                  if (!localIterator.hasNext()) {
                    break;
                  }
                  localAudioMixMatchCriterion = (AudioMixingRule.AudioMixMatchCriterion)localIterator.next();
                  switch (i)
                  {
                  }
                } while (localAudioMixMatchCriterion.mAttr.getUsage() != paramAudioAttributes.getUsage());
                i = localAudioMixMatchCriterion.mRule;
                if (i != paramInt) {
                  break;
                }
                return this;
                this.mTargetMixType = 1;
              }
            } while (((this.mTargetMixType != 0) || (AudioMixingRule.-wrap1(paramInt))) && ((this.mTargetMixType != 1) || (!AudioMixingRule.-wrap1(paramInt))));
            throw new IllegalArgumentException("Incompatible rule for mix");
            throw new IllegalArgumentException("Contradictory rule exists for " + paramAudioAttributes);
          }
        } while (localAudioMixMatchCriterion.mAttr.getCapturePreset() != paramAudioAttributes.getCapturePreset());
        i = localAudioMixMatchCriterion.mRule;
        if (i == paramInt) {
          return this;
        }
        throw new IllegalArgumentException("Contradictory rule exists for " + paramAudioAttributes);
      } while (localAudioMixMatchCriterion.mIntProp != paramInteger.intValue());
      int i = localAudioMixMatchCriterion.mRule;
      if (i == paramInt) {
        return this;
      }
      throw new IllegalArgumentException("Contradictory rule exists for UID " + paramInteger);
      for (;;)
      {
        throw new IllegalStateException("Unreachable code in addRuleInternal()");
        this.mCriteria.add(new AudioMixingRule.AudioMixMatchCriterion(paramAudioAttributes, paramInt));
        for (;;)
        {
          return this;
          this.mCriteria.add(new AudioMixingRule.AudioMixMatchCriterion(paramInteger, paramInt));
        }
        break;
        switch (i)
        {
        }
      }
    }
    
    private Builder checkAddRuleObjInternal(int paramInt, Object paramObject)
      throws IllegalArgumentException
    {
      if (paramObject == null) {
        throw new IllegalArgumentException("Illegal null argument for mixing rule");
      }
      if (!AudioMixingRule.-wrap3(paramInt)) {
        throw new IllegalArgumentException("Illegal rule value " + paramInt);
      }
      if (AudioMixingRule.-wrap0(paramInt & 0xFFFF7FFF))
      {
        if (!(paramObject instanceof AudioAttributes)) {
          throw new IllegalArgumentException("Invalid AudioAttributes argument");
        }
        return addRuleInternal((AudioAttributes)paramObject, null, paramInt);
      }
      if (!(paramObject instanceof Integer)) {
        throw new IllegalArgumentException("Invalid Integer argument");
      }
      return addRuleInternal(null, (Integer)paramObject, paramInt);
    }
    
    public Builder addMixRule(int paramInt, Object paramObject)
      throws IllegalArgumentException
    {
      if (!AudioMixingRule.-wrap4(paramInt)) {
        throw new IllegalArgumentException("Illegal rule value " + paramInt);
      }
      return checkAddRuleObjInternal(paramInt, paramObject);
    }
    
    public Builder addRule(AudioAttributes paramAudioAttributes, int paramInt)
      throws IllegalArgumentException
    {
      if (!AudioMixingRule.-wrap2(paramInt)) {
        throw new IllegalArgumentException("Illegal rule value " + paramInt);
      }
      return checkAddRuleObjInternal(paramInt, paramAudioAttributes);
    }
    
    Builder addRuleFromParcel(Parcel paramParcel)
      throws IllegalArgumentException
    {
      int i = paramParcel.readInt();
      Object localObject = null;
      Integer localInteger = null;
      int j;
      switch (i & 0xFFFF7FFF)
      {
      case 3: 
      default: 
        paramParcel.readInt();
        throw new IllegalArgumentException("Illegal rule value " + i + " in parcel");
      case 1: 
        j = paramParcel.readInt();
        paramParcel = new AudioAttributes.Builder().setUsage(j).build();
      }
      for (;;)
      {
        return addRuleInternal(paramParcel, localInteger, i);
        j = paramParcel.readInt();
        paramParcel = new AudioAttributes.Builder().setInternalCapturePreset(j).build();
        continue;
        localInteger = new Integer(paramParcel.readInt());
        paramParcel = (Parcel)localObject;
      }
    }
    
    public AudioMixingRule build()
    {
      return new AudioMixingRule(this.mTargetMixType, this.mCriteria, null);
    }
    
    public Builder excludeMixRule(int paramInt, Object paramObject)
      throws IllegalArgumentException
    {
      if (!AudioMixingRule.-wrap4(paramInt)) {
        throw new IllegalArgumentException("Illegal rule value " + paramInt);
      }
      return checkAddRuleObjInternal(0x8000 | paramInt, paramObject);
    }
    
    public Builder excludeRule(AudioAttributes paramAudioAttributes, int paramInt)
      throws IllegalArgumentException
    {
      if (!AudioMixingRule.-wrap2(paramInt)) {
        throw new IllegalArgumentException("Illegal rule value " + paramInt);
      }
      return checkAddRuleObjInternal(0x8000 | paramInt, paramAudioAttributes);
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/media/audiopolicy/AudioMixingRule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */