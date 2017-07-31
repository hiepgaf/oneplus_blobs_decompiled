package com.android.server.pm;

import android.content.pm.PackageInfo;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

abstract class ShortcutPackageItem
{
  private static final String KEY_NAME = "name";
  private static final String TAG = "ShortcutService";
  private final ShortcutPackageInfo mPackageInfo;
  private final String mPackageName;
  private final int mPackageUserId;
  protected ShortcutUser mShortcutUser;
  
  protected ShortcutPackageItem(ShortcutUser paramShortcutUser, int paramInt, String paramString, ShortcutPackageInfo paramShortcutPackageInfo)
  {
    this.mShortcutUser = paramShortcutUser;
    this.mPackageUserId = paramInt;
    this.mPackageName = ((String)Preconditions.checkStringNotEmpty(paramString));
    this.mPackageInfo = ((ShortcutPackageInfo)Preconditions.checkNotNull(paramShortcutPackageInfo));
  }
  
  public void attemptToRestoreIfNeededAndSave()
  {
    if (!this.mPackageInfo.isShadow()) {
      return;
    }
    ShortcutService localShortcutService = this.mShortcutUser.mService;
    if (!localShortcutService.isPackageInstalled(this.mPackageName, this.mPackageUserId))
    {
      if (ShortcutService.DEBUG) {
        Slog.d("ShortcutService", String.format("Package still not installed: %s user=%d", new Object[] { this.mPackageName, Integer.valueOf(this.mPackageUserId) }));
      }
      return;
    }
    if (!this.mPackageInfo.hasSignatures())
    {
      localShortcutService.wtf("Attempted to restore package " + this.mPackageName + ", user=" + this.mPackageUserId + " but signatures not found in the restore data.");
      onRestoreBlocked();
      return;
    }
    PackageInfo localPackageInfo = localShortcutService.getPackageInfoWithSignatures(this.mPackageName, this.mPackageUserId);
    if (!this.mPackageInfo.canRestoreTo(localShortcutService, localPackageInfo))
    {
      onRestoreBlocked();
      return;
    }
    if (ShortcutService.DEBUG) {
      Slog.d("ShortcutService", String.format("Restored package: %s/%d on user %d", new Object[] { this.mPackageName, Integer.valueOf(this.mPackageUserId), Integer.valueOf(getOwnerUserId()) }));
    }
    onRestored();
    this.mPackageInfo.setShadow(false);
    localShortcutService.scheduleSaveUser(this.mPackageUserId);
  }
  
  public JSONObject dumpCheckin(boolean paramBoolean)
    throws JSONException
  {
    JSONObject localJSONObject = new JSONObject();
    localJSONObject.put("name", this.mPackageName);
    return localJSONObject;
  }
  
  public abstract int getOwnerUserId();
  
  public ShortcutPackageInfo getPackageInfo()
  {
    return this.mPackageInfo;
  }
  
  public String getPackageName()
  {
    return this.mPackageName;
  }
  
  public int getPackageUserId()
  {
    return this.mPackageUserId;
  }
  
  public ShortcutUser getUser()
  {
    return this.mShortcutUser;
  }
  
  protected abstract void onRestoreBlocked();
  
  protected abstract void onRestored();
  
  public void refreshPackageSignatureAndSave()
  {
    if (this.mPackageInfo.isShadow()) {
      return;
    }
    ShortcutService localShortcutService = this.mShortcutUser.mService;
    this.mPackageInfo.refreshSignature(localShortcutService, this);
    localShortcutService.scheduleSaveUser(getOwnerUserId());
  }
  
  public void replaceUser(ShortcutUser paramShortcutUser)
  {
    this.mShortcutUser = paramShortcutUser;
  }
  
  public abstract void saveToXml(XmlSerializer paramXmlSerializer, boolean paramBoolean)
    throws IOException, XmlPullParserException;
  
  public void verifyStates() {}
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/android/server/pm/ShortcutPackageItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */