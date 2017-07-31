package android.app.assist;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class AssistContent
  implements Parcelable
{
  public static final Parcelable.Creator<AssistContent> CREATOR = new Parcelable.Creator()
  {
    public AssistContent createFromParcel(Parcel paramAnonymousParcel)
    {
      return new AssistContent(paramAnonymousParcel);
    }
    
    public AssistContent[] newArray(int paramAnonymousInt)
    {
      return new AssistContent[paramAnonymousInt];
    }
  };
  private ClipData mClipData;
  private final Bundle mExtras;
  private Intent mIntent;
  private boolean mIsAppProvidedIntent = false;
  private boolean mIsAppProvidedWebUri = false;
  private String mStructuredData;
  private Uri mUri;
  
  public AssistContent()
  {
    this.mExtras = new Bundle();
  }
  
  AssistContent(Parcel paramParcel)
  {
    if (paramParcel.readInt() != 0) {
      this.mIntent = ((Intent)Intent.CREATOR.createFromParcel(paramParcel));
    }
    if (paramParcel.readInt() != 0) {
      this.mClipData = ((ClipData)ClipData.CREATOR.createFromParcel(paramParcel));
    }
    if (paramParcel.readInt() != 0) {
      this.mUri = ((Uri)Uri.CREATOR.createFromParcel(paramParcel));
    }
    if (paramParcel.readInt() != 0) {
      this.mStructuredData = paramParcel.readString();
    }
    if (paramParcel.readInt() == 1)
    {
      bool1 = true;
      this.mIsAppProvidedIntent = bool1;
      this.mExtras = paramParcel.readBundle();
      if (paramParcel.readInt() != 1) {
        break label144;
      }
    }
    label144:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      this.mIsAppProvidedWebUri = bool1;
      return;
      bool1 = false;
      break;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public ClipData getClipData()
  {
    return this.mClipData;
  }
  
  public Bundle getExtras()
  {
    return this.mExtras;
  }
  
  public Intent getIntent()
  {
    return this.mIntent;
  }
  
  public String getStructuredData()
  {
    return this.mStructuredData;
  }
  
  public Uri getWebUri()
  {
    return this.mUri;
  }
  
  public boolean isAppProvidedIntent()
  {
    return this.mIsAppProvidedIntent;
  }
  
  public boolean isAppProvidedWebUri()
  {
    return this.mIsAppProvidedWebUri;
  }
  
  public void setClipData(ClipData paramClipData)
  {
    this.mClipData = paramClipData;
  }
  
  public void setDefaultIntent(Intent paramIntent)
  {
    this.mIntent = paramIntent;
    this.mIsAppProvidedIntent = false;
    this.mIsAppProvidedWebUri = false;
    this.mUri = null;
    if ((paramIntent != null) && ("android.intent.action.VIEW".equals(paramIntent.getAction())))
    {
      paramIntent = paramIntent.getData();
      if ((paramIntent != null) && (("http".equals(paramIntent.getScheme())) || ("https".equals(paramIntent.getScheme())))) {
        this.mUri = paramIntent;
      }
    }
  }
  
  public void setIntent(Intent paramIntent)
  {
    this.mIsAppProvidedIntent = true;
    this.mIntent = paramIntent;
  }
  
  public void setStructuredData(String paramString)
  {
    this.mStructuredData = paramString;
  }
  
  public void setWebUri(Uri paramUri)
  {
    this.mIsAppProvidedWebUri = true;
    this.mUri = paramUri;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    writeToParcelInternal(paramParcel, paramInt);
  }
  
  void writeToParcelInternal(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    if (this.mIntent != null)
    {
      paramParcel.writeInt(1);
      this.mIntent.writeToParcel(paramParcel, paramInt);
      if (this.mClipData == null) {
        break label130;
      }
      paramParcel.writeInt(1);
      this.mClipData.writeToParcel(paramParcel, paramInt);
      label44:
      if (this.mUri == null) {
        break label138;
      }
      paramParcel.writeInt(1);
      this.mUri.writeToParcel(paramParcel, paramInt);
      label65:
      if (this.mStructuredData == null) {
        break label146;
      }
      paramParcel.writeInt(1);
      paramParcel.writeString(this.mStructuredData);
      label85:
      if (!this.mIsAppProvidedIntent) {
        break label154;
      }
      paramInt = 1;
      label94:
      paramParcel.writeInt(paramInt);
      paramParcel.writeBundle(this.mExtras);
      if (!this.mIsAppProvidedWebUri) {
        break label159;
      }
    }
    label130:
    label138:
    label146:
    label154:
    label159:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      return;
      paramParcel.writeInt(0);
      break;
      paramParcel.writeInt(0);
      break label44;
      paramParcel.writeInt(0);
      break label65;
      paramParcel.writeInt(0);
      break label85;
      paramInt = 0;
      break label94;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/app/assist/AssistContent.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */