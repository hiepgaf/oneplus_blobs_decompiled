package android.app;

import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.ClipDescription;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class RemoteInput
  implements Parcelable
{
  public static final Parcelable.Creator<RemoteInput> CREATOR = new Parcelable.Creator()
  {
    public RemoteInput createFromParcel(Parcel paramAnonymousParcel)
    {
      return new RemoteInput(paramAnonymousParcel, null);
    }
    
    public RemoteInput[] newArray(int paramAnonymousInt)
    {
      return new RemoteInput[paramAnonymousInt];
    }
  };
  private static final int DEFAULT_FLAGS = 1;
  public static final String EXTRA_RESULTS_DATA = "android.remoteinput.resultsData";
  private static final int FLAG_ALLOW_FREE_FORM_INPUT = 1;
  public static final String RESULTS_CLIP_LABEL = "android.remoteinput.results";
  private final CharSequence[] mChoices;
  private final Bundle mExtras;
  private final int mFlags;
  private final CharSequence mLabel;
  private final String mResultKey;
  
  private RemoteInput(Parcel paramParcel)
  {
    this.mResultKey = paramParcel.readString();
    this.mLabel = paramParcel.readCharSequence();
    this.mChoices = paramParcel.readCharSequenceArray();
    this.mFlags = paramParcel.readInt();
    this.mExtras = paramParcel.readBundle();
  }
  
  private RemoteInput(String paramString, CharSequence paramCharSequence, CharSequence[] paramArrayOfCharSequence, int paramInt, Bundle paramBundle)
  {
    this.mResultKey = paramString;
    this.mLabel = paramCharSequence;
    this.mChoices = paramArrayOfCharSequence;
    this.mFlags = paramInt;
    this.mExtras = paramBundle;
  }
  
  public static void addResultsToIntent(RemoteInput[] paramArrayOfRemoteInput, Intent paramIntent, Bundle paramBundle)
  {
    Bundle localBundle = new Bundle();
    int i = 0;
    int j = paramArrayOfRemoteInput.length;
    while (i < j)
    {
      RemoteInput localRemoteInput = paramArrayOfRemoteInput[i];
      Object localObject = paramBundle.get(localRemoteInput.getResultKey());
      if ((localObject instanceof CharSequence)) {
        localBundle.putCharSequence(localRemoteInput.getResultKey(), (CharSequence)localObject);
      }
      i += 1;
    }
    paramArrayOfRemoteInput = new Intent();
    paramArrayOfRemoteInput.putExtra("android.remoteinput.resultsData", localBundle);
    paramIntent.setClipData(ClipData.newIntent("android.remoteinput.results", paramArrayOfRemoteInput));
  }
  
  public static Bundle getResultsFromIntent(Intent paramIntent)
  {
    paramIntent = paramIntent.getClipData();
    if (paramIntent == null) {
      return null;
    }
    ClipDescription localClipDescription = paramIntent.getDescription();
    if (!localClipDescription.hasMimeType("text/vnd.android.intent")) {
      return null;
    }
    if (localClipDescription.getLabel().equals("android.remoteinput.results")) {
      return (Bundle)paramIntent.getItemAt(0).getIntent().getExtras().getParcelable("android.remoteinput.resultsData");
    }
    return null;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean getAllowFreeFormInput()
  {
    boolean bool = false;
    if ((this.mFlags & 0x1) != 0) {
      bool = true;
    }
    return bool;
  }
  
  public CharSequence[] getChoices()
  {
    return this.mChoices;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public CharSequence getLabel()
  {
    return this.mLabel;
  }
  
  public String getResultKey()
  {
    return this.mResultKey;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mResultKey);
    paramParcel.writeCharSequence(this.mLabel);
    paramParcel.writeCharSequenceArray(this.mChoices);
    paramParcel.writeInt(this.mFlags);
    paramParcel.writeBundle(this.mExtras);
  }
  
  public static final class Builder
  {
    private CharSequence[] mChoices;
    private Bundle mExtras = new Bundle();
    private int mFlags = 1;
    private CharSequence mLabel;
    private final String mResultKey;
    
    public Builder(String paramString)
    {
      if (paramString == null) {
        throw new IllegalArgumentException("Result key can't be null");
      }
      this.mResultKey = paramString;
    }
    
    private void setFlag(int paramInt, boolean paramBoolean)
    {
      if (paramBoolean)
      {
        this.mFlags |= paramInt;
        return;
      }
      this.mFlags &= paramInt;
    }
    
    public Builder addExtras(Bundle paramBundle)
    {
      if (paramBundle != null) {
        this.mExtras.putAll(paramBundle);
      }
      return this;
    }
    
    public RemoteInput build()
    {
      return new RemoteInput(this.mResultKey, this.mLabel, this.mChoices, this.mFlags, this.mExtras, null);
    }
    
    public Bundle getExtras()
    {
      return this.mExtras;
    }
    
    public Builder setAllowFreeFormInput(boolean paramBoolean)
    {
      setFlag(this.mFlags, paramBoolean);
      return this;
    }
    
    public Builder setChoices(CharSequence[] paramArrayOfCharSequence)
    {
      if (paramArrayOfCharSequence == null) {
        this.mChoices = null;
      }
      for (;;)
      {
        return this;
        this.mChoices = new CharSequence[paramArrayOfCharSequence.length];
        int i = 0;
        while (i < paramArrayOfCharSequence.length)
        {
          this.mChoices[i] = Notification.safeCharSequence(paramArrayOfCharSequence[i]);
          i += 1;
        }
      }
    }
    
    public Builder setLabel(CharSequence paramCharSequence)
    {
      this.mLabel = Notification.safeCharSequence(paramCharSequence);
      return this;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/RemoteInput.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */