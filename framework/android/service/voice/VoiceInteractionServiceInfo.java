package android.service.voice;

import android.app.AppGlobals;
import android.content.ComponentName;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import com.android.internal.R.styleable;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

public class VoiceInteractionServiceInfo
{
  static final String TAG = "VoiceInteractionServiceInfo";
  private String mParseError;
  private String mRecognitionService;
  private ServiceInfo mServiceInfo;
  private String mSessionService;
  private String mSettingsActivity;
  private boolean mSupportsAssist;
  private boolean mSupportsLaunchFromKeyguard;
  private boolean mSupportsLocalInteraction;
  
  public VoiceInteractionServiceInfo(PackageManager paramPackageManager, ComponentName paramComponentName)
    throws PackageManager.NameNotFoundException
  {
    this(paramPackageManager, paramPackageManager.getServiceInfo(paramComponentName, 128));
  }
  
  public VoiceInteractionServiceInfo(PackageManager paramPackageManager, ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    this(paramPackageManager, getServiceInfoOrThrow(paramComponentName, paramInt));
  }
  
  public VoiceInteractionServiceInfo(PackageManager paramPackageManager, ServiceInfo paramServiceInfo)
  {
    if (paramServiceInfo == null)
    {
      this.mParseError = "Service not available";
      return;
    }
    if (!"android.permission.BIND_VOICE_INTERACTION".equals(paramServiceInfo.permission))
    {
      this.mParseError = "Service does not require permission android.permission.BIND_VOICE_INTERACTION";
      return;
    }
    Object localObject3 = null;
    Object localObject4 = null;
    Object localObject1 = null;
    Object localObject2 = null;
    try
    {
      XmlResourceParser localXmlResourceParser = paramServiceInfo.loadXmlMetaData(paramPackageManager, "android.voice_interaction");
      if (localXmlResourceParser == null)
      {
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        this.mParseError = ("No android.voice_interaction meta-data for " + paramServiceInfo.packageName);
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return;
      }
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      paramPackageManager = paramPackageManager.getResourcesForApplication(paramServiceInfo.applicationInfo);
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      AttributeSet localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
      int i;
      do
      {
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        i = localXmlResourceParser.next();
      } while ((i != 1) && (i != 2));
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      if (!"voice-interaction-service".equals(localXmlResourceParser.getName()))
      {
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        this.mParseError = "Meta-data does not start with voice-interaction-service tag";
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return;
      }
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      paramPackageManager = paramPackageManager.obtainAttributes(localAttributeSet, R.styleable.VoiceInteractionService);
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      this.mSessionService = paramPackageManager.getString(1);
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      this.mRecognitionService = paramPackageManager.getString(2);
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      this.mSettingsActivity = paramPackageManager.getString(0);
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      this.mSupportsAssist = paramPackageManager.getBoolean(3, false);
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      this.mSupportsLaunchFromKeyguard = paramPackageManager.getBoolean(4, false);
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      this.mSupportsLocalInteraction = paramPackageManager.getBoolean(5, false);
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      paramPackageManager.recycle();
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      if (this.mSessionService == null)
      {
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        this.mParseError = "No sessionService specified";
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return;
      }
      localObject2 = localXmlResourceParser;
      localObject3 = localXmlResourceParser;
      localObject4 = localXmlResourceParser;
      localObject1 = localXmlResourceParser;
      if (this.mRecognitionService == null)
      {
        localObject2 = localXmlResourceParser;
        localObject3 = localXmlResourceParser;
        localObject4 = localXmlResourceParser;
        localObject1 = localXmlResourceParser;
        this.mParseError = "No recognitionService specified";
        if (localXmlResourceParser != null) {
          localXmlResourceParser.close();
        }
        return;
      }
      if (localXmlResourceParser != null) {
        localXmlResourceParser.close();
      }
      this.mServiceInfo = paramServiceInfo;
      return;
    }
    catch (PackageManager.NameNotFoundException paramPackageManager)
    {
      localObject1 = localObject2;
      this.mParseError = ("Error parsing voice interation service meta-data: " + paramPackageManager);
      localObject1 = localObject2;
      Log.w("VoiceInteractionServiceInfo", "error parsing voice interaction service meta-data", paramPackageManager);
      return;
    }
    catch (IOException paramPackageManager)
    {
      localObject1 = localObject3;
      this.mParseError = ("Error parsing voice interation service meta-data: " + paramPackageManager);
      localObject1 = localObject3;
      Log.w("VoiceInteractionServiceInfo", "error parsing voice interaction service meta-data", paramPackageManager);
      return;
    }
    catch (XmlPullParserException paramPackageManager)
    {
      localObject1 = localObject4;
      this.mParseError = ("Error parsing voice interation service meta-data: " + paramPackageManager);
      localObject1 = localObject4;
      Log.w("VoiceInteractionServiceInfo", "error parsing voice interaction service meta-data", paramPackageManager);
      return;
    }
    finally
    {
      if (localObject1 != null) {
        ((XmlResourceParser)localObject1).close();
      }
    }
  }
  
  static ServiceInfo getServiceInfoOrThrow(ComponentName paramComponentName, int paramInt)
    throws PackageManager.NameNotFoundException
  {
    try
    {
      ServiceInfo localServiceInfo = AppGlobals.getPackageManager().getServiceInfo(paramComponentName, 269222016, paramInt);
      if (localServiceInfo != null) {
        return localServiceInfo;
      }
    }
    catch (RemoteException localRemoteException)
    {
      throw new PackageManager.NameNotFoundException(paramComponentName.toString());
    }
  }
  
  public String getParseError()
  {
    return this.mParseError;
  }
  
  public String getRecognitionService()
  {
    return this.mRecognitionService;
  }
  
  public ServiceInfo getServiceInfo()
  {
    return this.mServiceInfo;
  }
  
  public String getSessionService()
  {
    return this.mSessionService;
  }
  
  public String getSettingsActivity()
  {
    return this.mSettingsActivity;
  }
  
  public boolean getSupportsAssist()
  {
    return this.mSupportsAssist;
  }
  
  public boolean getSupportsLaunchFromKeyguard()
  {
    return this.mSupportsLaunchFromKeyguard;
  }
  
  public boolean getSupportsLocalInteraction()
  {
    return this.mSupportsLocalInteraction;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/service/voice/VoiceInteractionServiceInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */