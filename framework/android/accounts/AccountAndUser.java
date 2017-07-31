package android.accounts;

public class AccountAndUser
{
  public Account account;
  public int userId;
  
  public AccountAndUser(Account paramAccount, int paramInt)
  {
    this.account = paramAccount;
    this.userId = paramInt;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (!(paramObject instanceof AccountAndUser)) {
      return false;
    }
    paramObject = (AccountAndUser)paramObject;
    if (this.account.equals(((AccountAndUser)paramObject).account)) {
      return this.userId == ((AccountAndUser)paramObject).userId;
    }
    return false;
  }
  
  public int hashCode()
  {
    return this.account.hashCode() + this.userId;
  }
  
  public String toString()
  {
    return this.account.toString() + " u" + this.userId;
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/accounts/AccountAndUser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */