package android.support.v4.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;

public class ShareCompat
{
  public static final String EXTRA_CALLING_ACTIVITY = "android.support.v4.app.EXTRA_CALLING_ACTIVITY";
  public static final String EXTRA_CALLING_PACKAGE = "android.support.v4.app.EXTRA_CALLING_PACKAGE";
  private static ShareCompatImpl IMPL = new ShareCompatImplICS();
  
  static
  {
    if (Build.VERSION.SDK_INT < 16)
    {
      if (Build.VERSION.SDK_INT < 14) {
        IMPL = new ShareCompatImplBase();
      }
    }
    else
    {
      IMPL = new ShareCompatImplJB();
      return;
    }
  }
  
  public static void configureMenuItem(Menu paramMenu, int paramInt, IntentBuilder paramIntentBuilder)
  {
    paramMenu = paramMenu.findItem(paramInt);
    if (paramMenu != null)
    {
      configureMenuItem(paramMenu, paramIntentBuilder);
      return;
    }
    throw new IllegalArgumentException("Could not find menu item with id " + paramInt + " in the supplied menu");
  }
  
  public static void configureMenuItem(MenuItem paramMenuItem, IntentBuilder paramIntentBuilder)
  {
    IMPL.configureMenuItem(paramMenuItem, paramIntentBuilder);
  }
  
  public static ComponentName getCallingActivity(Activity paramActivity)
  {
    ComponentName localComponentName = paramActivity.getCallingActivity();
    if (localComponentName != null) {
      return localComponentName;
    }
    return (ComponentName)paramActivity.getIntent().getParcelableExtra("android.support.v4.app.EXTRA_CALLING_ACTIVITY");
  }
  
  public static String getCallingPackage(Activity paramActivity)
  {
    String str = paramActivity.getCallingPackage();
    if (str != null) {
      return str;
    }
    return paramActivity.getIntent().getStringExtra("android.support.v4.app.EXTRA_CALLING_PACKAGE");
  }
  
  public static class IntentBuilder
  {
    private Activity mActivity;
    private ArrayList<String> mBccAddresses;
    private ArrayList<String> mCcAddresses;
    private CharSequence mChooserTitle;
    private Intent mIntent;
    private ArrayList<Uri> mStreams;
    private ArrayList<String> mToAddresses;
    
    private IntentBuilder(Activity paramActivity)
    {
      this.mActivity = paramActivity;
      this.mIntent = new Intent().setAction("android.intent.action.SEND");
      this.mIntent.putExtra("android.support.v4.app.EXTRA_CALLING_PACKAGE", paramActivity.getPackageName());
      this.mIntent.putExtra("android.support.v4.app.EXTRA_CALLING_ACTIVITY", paramActivity.getComponentName());
      this.mIntent.addFlags(524288);
    }
    
    private void combineArrayExtra(String paramString, ArrayList<String> paramArrayList)
    {
      String[] arrayOfString1 = this.mIntent.getStringArrayExtra(paramString);
      int i;
      String[] arrayOfString2;
      if (arrayOfString1 == null)
      {
        i = 0;
        arrayOfString2 = new String[paramArrayList.size() + i];
        paramArrayList.toArray(arrayOfString2);
        if (arrayOfString1 != null) {
          break label59;
        }
      }
      for (;;)
      {
        this.mIntent.putExtra(paramString, arrayOfString2);
        return;
        i = arrayOfString1.length;
        break;
        label59:
        System.arraycopy(arrayOfString1, 0, arrayOfString2, paramArrayList.size(), i);
      }
    }
    
    private void combineArrayExtra(String paramString, String[] paramArrayOfString)
    {
      Intent localIntent = getIntent();
      String[] arrayOfString1 = localIntent.getStringArrayExtra(paramString);
      int i;
      String[] arrayOfString2;
      if (arrayOfString1 == null)
      {
        i = 0;
        arrayOfString2 = new String[paramArrayOfString.length + i];
        if (arrayOfString1 != null) {
          break label62;
        }
      }
      for (;;)
      {
        System.arraycopy(paramArrayOfString, 0, arrayOfString2, i, paramArrayOfString.length);
        localIntent.putExtra(paramString, arrayOfString2);
        return;
        i = arrayOfString1.length;
        break;
        label62:
        System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, i);
      }
    }
    
    public static IntentBuilder from(Activity paramActivity)
    {
      return new IntentBuilder(paramActivity);
    }
    
    public IntentBuilder addEmailBcc(String paramString)
    {
      if (this.mBccAddresses != null) {}
      for (;;)
      {
        this.mBccAddresses.add(paramString);
        return this;
        this.mBccAddresses = new ArrayList();
      }
    }
    
    public IntentBuilder addEmailBcc(String[] paramArrayOfString)
    {
      combineArrayExtra("android.intent.extra.BCC", paramArrayOfString);
      return this;
    }
    
    public IntentBuilder addEmailCc(String paramString)
    {
      if (this.mCcAddresses != null) {}
      for (;;)
      {
        this.mCcAddresses.add(paramString);
        return this;
        this.mCcAddresses = new ArrayList();
      }
    }
    
    public IntentBuilder addEmailCc(String[] paramArrayOfString)
    {
      combineArrayExtra("android.intent.extra.CC", paramArrayOfString);
      return this;
    }
    
    public IntentBuilder addEmailTo(String paramString)
    {
      if (this.mToAddresses != null) {}
      for (;;)
      {
        this.mToAddresses.add(paramString);
        return this;
        this.mToAddresses = new ArrayList();
      }
    }
    
    public IntentBuilder addEmailTo(String[] paramArrayOfString)
    {
      combineArrayExtra("android.intent.extra.EMAIL", paramArrayOfString);
      return this;
    }
    
    public IntentBuilder addStream(Uri paramUri)
    {
      Uri localUri = (Uri)this.mIntent.getParcelableExtra("android.intent.extra.STREAM");
      if (localUri != null)
      {
        if (this.mStreams == null) {
          break label45;
        }
        if (localUri != null) {
          break label59;
        }
      }
      for (;;)
      {
        this.mStreams.add(paramUri);
        return this;
        return setStream(paramUri);
        label45:
        this.mStreams = new ArrayList();
        break;
        label59:
        this.mIntent.removeExtra("android.intent.extra.STREAM");
        this.mStreams.add(localUri);
      }
    }
    
    public Intent createChooserIntent()
    {
      return Intent.createChooser(getIntent(), this.mChooserTitle);
    }
    
    Activity getActivity()
    {
      return this.mActivity;
    }
    
    public Intent getIntent()
    {
      label14:
      label21:
      label28:
      int i;
      label30:
      boolean bool;
      if (this.mToAddresses == null)
      {
        if (this.mCcAddresses != null) {
          break label74;
        }
        if (this.mBccAddresses != null) {
          break label92;
        }
        if (this.mStreams != null) {
          break label110;
        }
        i = 0;
        bool = this.mIntent.getAction().equals("android.intent.action.SEND_MULTIPLE");
        if (i == 0) {
          break label126;
        }
        label47:
        if (i != 0) {
          break label198;
        }
      }
      for (;;)
      {
        return this.mIntent;
        combineArrayExtra("android.intent.extra.EMAIL", this.mToAddresses);
        this.mToAddresses = null;
        break;
        label74:
        combineArrayExtra("android.intent.extra.CC", this.mCcAddresses);
        this.mCcAddresses = null;
        break label14;
        label92:
        combineArrayExtra("android.intent.extra.BCC", this.mBccAddresses);
        this.mBccAddresses = null;
        break label21;
        label110:
        if (this.mStreams.size() <= 1) {
          break label28;
        }
        i = 1;
        break label30;
        label126:
        if (!bool) {
          break label47;
        }
        this.mIntent.setAction("android.intent.action.SEND");
        if (this.mStreams == null) {
          label147:
          this.mIntent.removeExtra("android.intent.extra.STREAM");
        }
        for (;;)
        {
          this.mStreams = null;
          break;
          if (this.mStreams.isEmpty()) {
            break label147;
          }
          this.mIntent.putExtra("android.intent.extra.STREAM", (Parcelable)this.mStreams.get(0));
        }
        label198:
        if (!bool)
        {
          this.mIntent.setAction("android.intent.action.SEND_MULTIPLE");
          if (this.mStreams == null) {}
          while (this.mStreams.isEmpty())
          {
            this.mIntent.removeExtra("android.intent.extra.STREAM");
            break;
          }
          this.mIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", this.mStreams);
        }
      }
    }
    
    public IntentBuilder setChooserTitle(int paramInt)
    {
      return setChooserTitle(this.mActivity.getText(paramInt));
    }
    
    public IntentBuilder setChooserTitle(CharSequence paramCharSequence)
    {
      this.mChooserTitle = paramCharSequence;
      return this;
    }
    
    public IntentBuilder setEmailBcc(String[] paramArrayOfString)
    {
      this.mIntent.putExtra("android.intent.extra.BCC", paramArrayOfString);
      return this;
    }
    
    public IntentBuilder setEmailCc(String[] paramArrayOfString)
    {
      this.mIntent.putExtra("android.intent.extra.CC", paramArrayOfString);
      return this;
    }
    
    public IntentBuilder setEmailTo(String[] paramArrayOfString)
    {
      if (this.mToAddresses == null) {}
      for (;;)
      {
        this.mIntent.putExtra("android.intent.extra.EMAIL", paramArrayOfString);
        return this;
        this.mToAddresses = null;
      }
    }
    
    public IntentBuilder setHtmlText(String paramString)
    {
      this.mIntent.putExtra("android.intent.extra.HTML_TEXT", paramString);
      if (this.mIntent.hasExtra("android.intent.extra.TEXT")) {
        return this;
      }
      setText(Html.fromHtml(paramString));
      return this;
    }
    
    public IntentBuilder setStream(Uri paramUri)
    {
      if (this.mIntent.getAction().equals("android.intent.action.SEND")) {}
      for (;;)
      {
        this.mStreams = null;
        this.mIntent.putExtra("android.intent.extra.STREAM", paramUri);
        return this;
        this.mIntent.setAction("android.intent.action.SEND");
      }
    }
    
    public IntentBuilder setSubject(String paramString)
    {
      this.mIntent.putExtra("android.intent.extra.SUBJECT", paramString);
      return this;
    }
    
    public IntentBuilder setText(CharSequence paramCharSequence)
    {
      this.mIntent.putExtra("android.intent.extra.TEXT", paramCharSequence);
      return this;
    }
    
    public IntentBuilder setType(String paramString)
    {
      this.mIntent.setType(paramString);
      return this;
    }
    
    public void startChooser()
    {
      this.mActivity.startActivity(createChooserIntent());
    }
  }
  
  public static class IntentReader
  {
    private static final String TAG = "IntentReader";
    private Activity mActivity;
    private ComponentName mCallingActivity;
    private String mCallingPackage;
    private Intent mIntent;
    private ArrayList<Uri> mStreams;
    
    private IntentReader(Activity paramActivity)
    {
      this.mActivity = paramActivity;
      this.mIntent = paramActivity.getIntent();
      this.mCallingPackage = ShareCompat.getCallingPackage(paramActivity);
      this.mCallingActivity = ShareCompat.getCallingActivity(paramActivity);
    }
    
    public static IntentReader from(Activity paramActivity)
    {
      return new IntentReader(paramActivity);
    }
    
    public ComponentName getCallingActivity()
    {
      return this.mCallingActivity;
    }
    
    public Drawable getCallingActivityIcon()
    {
      Object localObject;
      if (this.mCallingActivity != null) {
        localObject = this.mActivity.getPackageManager();
      }
      try
      {
        localObject = ((PackageManager)localObject).getActivityIcon(this.mCallingActivity);
        return (Drawable)localObject;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e("IntentReader", "Could not retrieve icon for calling activity", localNameNotFoundException);
      }
      return null;
      return null;
    }
    
    public Drawable getCallingApplicationIcon()
    {
      Object localObject;
      if (this.mCallingPackage != null) {
        localObject = this.mActivity.getPackageManager();
      }
      try
      {
        localObject = ((PackageManager)localObject).getApplicationIcon(this.mCallingPackage);
        return (Drawable)localObject;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e("IntentReader", "Could not retrieve icon for calling application", localNameNotFoundException);
      }
      return null;
      return null;
    }
    
    public CharSequence getCallingApplicationLabel()
    {
      Object localObject;
      if (this.mCallingPackage != null) {
        localObject = this.mActivity.getPackageManager();
      }
      try
      {
        localObject = ((PackageManager)localObject).getApplicationLabel(((PackageManager)localObject).getApplicationInfo(this.mCallingPackage, 0));
        return (CharSequence)localObject;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException)
      {
        Log.e("IntentReader", "Could not retrieve label for calling application", localNameNotFoundException);
      }
      return null;
      return null;
    }
    
    public String getCallingPackage()
    {
      return this.mCallingPackage;
    }
    
    public String[] getEmailBcc()
    {
      return this.mIntent.getStringArrayExtra("android.intent.extra.BCC");
    }
    
    public String[] getEmailCc()
    {
      return this.mIntent.getStringArrayExtra("android.intent.extra.CC");
    }
    
    public String[] getEmailTo()
    {
      return this.mIntent.getStringArrayExtra("android.intent.extra.EMAIL");
    }
    
    public String getHtmlText()
    {
      String str = this.mIntent.getStringExtra("android.intent.extra.HTML_TEXT");
      if (str != null) {
        return str;
      }
      CharSequence localCharSequence = getText();
      if (!(localCharSequence instanceof Spanned))
      {
        if (localCharSequence == null) {
          return str;
        }
      }
      else {
        return Html.toHtml((Spanned)localCharSequence);
      }
      return ShareCompat.IMPL.escapeHtml(localCharSequence);
    }
    
    public Uri getStream()
    {
      return (Uri)this.mIntent.getParcelableExtra("android.intent.extra.STREAM");
    }
    
    public Uri getStream(int paramInt)
    {
      if (this.mStreams != null) {}
      while (this.mStreams == null)
      {
        if (paramInt == 0) {
          break label92;
        }
        throw new IndexOutOfBoundsException("Stream items available: " + getStreamCount() + " index requested: " + paramInt);
        if (isMultipleShare()) {
          this.mStreams = this.mIntent.getParcelableArrayListExtra("android.intent.extra.STREAM");
        }
      }
      return (Uri)this.mStreams.get(paramInt);
      label92:
      return (Uri)this.mIntent.getParcelableExtra("android.intent.extra.STREAM");
    }
    
    public int getStreamCount()
    {
      if (this.mStreams != null) {}
      while (this.mStreams == null)
      {
        if (this.mIntent.hasExtra("android.intent.extra.STREAM")) {
          break label59;
        }
        return 0;
        if (isMultipleShare()) {
          this.mStreams = this.mIntent.getParcelableArrayListExtra("android.intent.extra.STREAM");
        }
      }
      return this.mStreams.size();
      label59:
      return 1;
    }
    
    public String getSubject()
    {
      return this.mIntent.getStringExtra("android.intent.extra.SUBJECT");
    }
    
    public CharSequence getText()
    {
      return this.mIntent.getCharSequenceExtra("android.intent.extra.TEXT");
    }
    
    public String getType()
    {
      return this.mIntent.getType();
    }
    
    public boolean isMultipleShare()
    {
      return "android.intent.action.SEND_MULTIPLE".equals(this.mIntent.getAction());
    }
    
    public boolean isShareIntent()
    {
      String str = this.mIntent.getAction();
      if ("android.intent.action.SEND".equals(str)) {}
      while ("android.intent.action.SEND_MULTIPLE".equals(str)) {
        return true;
      }
      return false;
    }
    
    public boolean isSingleShare()
    {
      return "android.intent.action.SEND".equals(this.mIntent.getAction());
    }
  }
  
  static abstract interface ShareCompatImpl
  {
    public abstract void configureMenuItem(MenuItem paramMenuItem, ShareCompat.IntentBuilder paramIntentBuilder);
    
    public abstract String escapeHtml(CharSequence paramCharSequence);
  }
  
  static class ShareCompatImplBase
    implements ShareCompat.ShareCompatImpl
  {
    private static void withinStyle(StringBuilder paramStringBuilder, CharSequence paramCharSequence, int paramInt1, int paramInt2)
    {
      if (paramInt1 >= paramInt2) {
        return;
      }
      char c = paramCharSequence.charAt(paramInt1);
      if (c != '<')
      {
        if (c == '>') {
          break label90;
        }
        if (c == '&') {
          break label100;
        }
        if (c <= '~') {
          break label110;
        }
        label43:
        paramStringBuilder.append("&#" + c + ";");
      }
      label90:
      label100:
      label110:
      int i;
      for (;;)
      {
        paramInt1 += 1;
        break;
        paramStringBuilder.append("&lt;");
        continue;
        paramStringBuilder.append("&gt;");
        continue;
        paramStringBuilder.append("&amp;");
        continue;
        if (c < ' ') {
          break label43;
        }
        i = paramInt1;
        if (c == ' ') {
          break label150;
        }
        paramStringBuilder.append(c);
      }
      label137:
      paramStringBuilder.append("&nbsp;");
      i += 1;
      label150:
      if (i + 1 >= paramInt2) {}
      for (;;)
      {
        paramStringBuilder.append(' ');
        paramInt1 = i;
        break;
        if (paramCharSequence.charAt(i + 1) == ' ') {
          break label137;
        }
      }
    }
    
    public void configureMenuItem(MenuItem paramMenuItem, ShareCompat.IntentBuilder paramIntentBuilder)
    {
      paramMenuItem.setIntent(paramIntentBuilder.createChooserIntent());
    }
    
    public String escapeHtml(CharSequence paramCharSequence)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      withinStyle(localStringBuilder, paramCharSequence, 0, paramCharSequence.length());
      return localStringBuilder.toString();
    }
  }
  
  static class ShareCompatImplICS
    extends ShareCompat.ShareCompatImplBase
  {
    public void configureMenuItem(MenuItem paramMenuItem, ShareCompat.IntentBuilder paramIntentBuilder)
    {
      ShareCompatICS.configureMenuItem(paramMenuItem, paramIntentBuilder.getActivity(), paramIntentBuilder.getIntent());
      if (!shouldAddChooserIntent(paramMenuItem)) {
        return;
      }
      paramMenuItem.setIntent(paramIntentBuilder.createChooserIntent());
    }
    
    boolean shouldAddChooserIntent(MenuItem paramMenuItem)
    {
      return !paramMenuItem.hasSubMenu();
    }
  }
  
  static class ShareCompatImplJB
    extends ShareCompat.ShareCompatImplICS
  {
    public String escapeHtml(CharSequence paramCharSequence)
    {
      return ShareCompatJB.escapeHtml(paramCharSequence);
    }
    
    boolean shouldAddChooserIntent(MenuItem paramMenuItem)
    {
      return false;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/support/v4/app/ShareCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */