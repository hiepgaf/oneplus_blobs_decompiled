package android.provider;

import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.Process;
import android.os.UserHandle;
import android.text.TextUtils;
import android.widget.Toast;
import java.util.List;

public class ContactsInternal
{
  private static final int CONTACTS_URI_LOOKUP = 1001;
  private static final int CONTACTS_URI_LOOKUP_ID = 1000;
  private static final UriMatcher sContactsUriMatcher = new UriMatcher(-1);
  
  static
  {
    UriMatcher localUriMatcher = sContactsUriMatcher;
    localUriMatcher.addURI("com.android.contacts", "contacts/lookup/*", 1001);
    localUriMatcher.addURI("com.android.contacts", "contacts/lookup/*/#", 1000);
  }
  
  private static boolean maybeStartManagedQuickContact(Context paramContext, Intent paramIntent)
  {
    Object localObject1 = paramIntent.getData();
    Object localObject2 = ((Uri)localObject1).getPathSegments();
    boolean bool;
    long l1;
    if (((List)localObject2).size() < 4)
    {
      bool = true;
      if (!bool) {
        break label124;
      }
      l1 = ContactsContract.Contacts.ENTERPRISE_CONTACT_ID_BASE;
      label34:
      localObject2 = (String)((List)localObject2).get(2);
      localObject1 = ((Uri)localObject1).getQueryParameter("directory");
      if (localObject1 != null) {
        break label133;
      }
    }
    label124:
    label133:
    for (long l2 = 1000000000L;; l2 = Long.parseLong((String)localObject1))
    {
      if ((TextUtils.isEmpty((CharSequence)localObject2)) || (!((String)localObject2).startsWith(ContactsContract.Contacts.ENTERPRISE_CONTACT_LOOKUP_PREFIX))) {
        break label143;
      }
      if (ContactsContract.Contacts.isEnterpriseContactId(l1)) {
        break label145;
      }
      throw new IllegalArgumentException("Invalid enterprise contact id: " + l1);
      bool = false;
      break;
      l1 = ContentUris.parseId((Uri)localObject1);
      break label34;
    }
    label143:
    return false;
    label145:
    if (!ContactsContract.Directory.isEnterpriseDirectoryId(l2)) {
      throw new IllegalArgumentException("Invalid enterprise directory id: " + l2);
    }
    ((DevicePolicyManager)paramContext.getSystemService(DevicePolicyManager.class)).startManagedQuickContact(((String)localObject2).substring(ContactsContract.Contacts.ENTERPRISE_CONTACT_LOOKUP_PREFIX.length()), l1 - ContactsContract.Contacts.ENTERPRISE_CONTACT_ID_BASE, bool, l2 - 1000000000L, paramIntent);
    return true;
  }
  
  public static void startQuickContactWithErrorToast(Context paramContext, Intent paramIntent)
  {
    Uri localUri = paramIntent.getData();
    switch (sContactsUriMatcher.match(localUri))
    {
    }
    do
    {
      startQuickContactWithErrorToastForUser(paramContext, paramIntent, Process.myUserHandle());
      return;
    } while (!maybeStartManagedQuickContact(paramContext, paramIntent));
  }
  
  public static void startQuickContactWithErrorToastForUser(Context paramContext, Intent paramIntent, UserHandle paramUserHandle)
  {
    try
    {
      paramContext.startActivityAsUser(paramIntent, paramUserHandle);
      return;
    }
    catch (ActivityNotFoundException paramIntent)
    {
      Toast.makeText(paramContext, 17040022, 0).show();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/provider/ContactsInternal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */