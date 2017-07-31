package com.oneplus.gallery2.web;

import com.oneplus.base.EventKey;
import com.oneplus.base.PropertyKey;
import com.oneplus.base.component.Component;
import com.oneplus.gallery2.Gallery;
import java.util.ArrayList;
import java.util.List;

public abstract interface AccountManager
  extends Component
{
  public static final EventKey<AccountEventArgs> EVENT_ACCOUNT_CREATED = new EventKey("AccountCreated", AccountEventArgs.class, AccountManager.class);
  public static final EventKey<AccountEventArgs> EVENT_ACCOUNT_DELETED = new EventKey("AccountDeleted", AccountEventArgs.class, AccountManager.class);
  public static final EventKey<AccountEventArgs> EVENT_ACCOUNT_STATUS_UPDATED = new EventKey("AccountStatusUpdated", AccountEventArgs.class, AccountManager.class);
  public static final PropertyKey<List<Account>> PROP_ACCOUNTS = new PropertyKey("Accounts", List.class, AccountManager.class, new ArrayList());
  
  public abstract boolean authorize(Gallery paramGallery, Account paramAccount);
  
  public abstract boolean createAccount(Gallery paramGallery);
  
  public abstract boolean deleteAccount(Account paramAccount);
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/com/oneplus/gallery2/web/AccountManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */