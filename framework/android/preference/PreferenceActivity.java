package android.preference;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentBreadCrumbs;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.android.internal.R.styleable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PreferenceActivity
  extends ListActivity
  implements PreferenceManager.OnPreferenceTreeClickListener, PreferenceFragment.OnPreferenceStartFragmentCallback
{
  private static final String BACK_STACK_PREFS = ":android:prefs";
  private static final String CUR_HEADER_TAG = ":android:cur_header";
  public static final String EXTRA_NO_HEADERS = ":android:no_headers";
  private static final String EXTRA_PREFS_SET_BACK_TEXT = "extra_prefs_set_back_text";
  private static final String EXTRA_PREFS_SET_NEXT_TEXT = "extra_prefs_set_next_text";
  private static final String EXTRA_PREFS_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";
  private static final String EXTRA_PREFS_SHOW_SKIP = "extra_prefs_show_skip";
  public static final String EXTRA_SHOW_FRAGMENT = ":android:show_fragment";
  public static final String EXTRA_SHOW_FRAGMENT_ARGUMENTS = ":android:show_fragment_args";
  public static final String EXTRA_SHOW_FRAGMENT_SHORT_TITLE = ":android:show_fragment_short_title";
  public static final String EXTRA_SHOW_FRAGMENT_TITLE = ":android:show_fragment_title";
  private static final int FIRST_REQUEST_CODE = 100;
  private static final String HEADERS_TAG = ":android:headers";
  public static final long HEADER_ID_UNDEFINED = -1L;
  private static final int MSG_BIND_PREFERENCES = 1;
  private static final int MSG_BUILD_HEADERS = 2;
  private static final String PREFERENCES_TAG = ":android:preferences";
  private static final String TAG = "PreferenceActivity";
  private Header mCurHeader;
  private FragmentBreadCrumbs mFragmentBreadCrumbs;
  private Handler mHandler = new Handler()
  {
    public void handleMessage(Message paramAnonymousMessage)
    {
      switch (paramAnonymousMessage.what)
      {
      }
      do
      {
        do
        {
          Object localObject;
          do
          {
            return;
            PreferenceActivity.-wrap0(PreferenceActivity.this);
            return;
            localObject = new ArrayList(PreferenceActivity.-get2(PreferenceActivity.this));
            PreferenceActivity.-get2(PreferenceActivity.this).clear();
            PreferenceActivity.this.onBuildHeaders(PreferenceActivity.-get2(PreferenceActivity.this));
            if ((PreferenceActivity.-get0(PreferenceActivity.this) instanceof BaseAdapter)) {
              ((BaseAdapter)PreferenceActivity.-get0(PreferenceActivity.this)).notifyDataSetChanged();
            }
            paramAnonymousMessage = PreferenceActivity.this.onGetNewHeader();
            if ((paramAnonymousMessage == null) || (paramAnonymousMessage.fragment == null)) {
              break;
            }
            localObject = PreferenceActivity.this.findBestMatchingHeader(paramAnonymousMessage, (ArrayList)localObject);
          } while ((localObject != null) && (PreferenceActivity.-get1(PreferenceActivity.this) == localObject));
          PreferenceActivity.this.switchToHeader(paramAnonymousMessage);
          return;
        } while (PreferenceActivity.-get1(PreferenceActivity.this) == null);
        paramAnonymousMessage = PreferenceActivity.this.findBestMatchingHeader(PreferenceActivity.-get1(PreferenceActivity.this), PreferenceActivity.-get2(PreferenceActivity.this));
      } while (paramAnonymousMessage == null);
      PreferenceActivity.this.setSelectedHeader(paramAnonymousMessage);
    }
  };
  private final ArrayList<Header> mHeaders = new ArrayList();
  private FrameLayout mListFooter;
  private Button mNextButton;
  private int mPreferenceHeaderItemResId = 0;
  private boolean mPreferenceHeaderRemoveEmptyIcon = false;
  private PreferenceManager mPreferenceManager;
  private ViewGroup mPrefsContainer;
  private Bundle mSavedInstanceState;
  private boolean mSinglePane;
  
  private void bindPreferences()
  {
    PreferenceScreen localPreferenceScreen = getPreferenceScreen();
    if (localPreferenceScreen != null)
    {
      localPreferenceScreen.bind(getListView());
      if (this.mSavedInstanceState != null)
      {
        super.onRestoreInstanceState(this.mSavedInstanceState);
        this.mSavedInstanceState = null;
      }
    }
  }
  
  private void postBindPreferences()
  {
    if (this.mHandler.hasMessages(1)) {
      return;
    }
    this.mHandler.obtainMessage(1).sendToTarget();
  }
  
  private void requirePreferenceManager()
  {
    if (this.mPreferenceManager == null)
    {
      if (this.mAdapter == null) {
        throw new RuntimeException("This should be called after super.onCreate.");
      }
      throw new RuntimeException("Modern two-pane PreferenceActivity requires use of a PreferenceFragment");
    }
  }
  
  private void switchToHeaderInner(String paramString, Bundle paramBundle)
  {
    getFragmentManager().popBackStack(":android:prefs", 1);
    if (!isValidFragment(paramString)) {
      throw new IllegalArgumentException("Invalid fragment for this activity: " + paramString);
    }
    paramString = Fragment.instantiate(this, paramString, paramBundle);
    paramBundle = getFragmentManager().beginTransaction();
    paramBundle.setTransition(4099);
    paramBundle.replace(16909277, paramString);
    paramBundle.commitAllowingStateLoss();
  }
  
  @Deprecated
  public void addPreferencesFromIntent(Intent paramIntent)
  {
    requirePreferenceManager();
    setPreferenceScreen(this.mPreferenceManager.inflateFromIntent(paramIntent, getPreferenceScreen()));
  }
  
  @Deprecated
  public void addPreferencesFromResource(int paramInt)
  {
    requirePreferenceManager();
    setPreferenceScreen(this.mPreferenceManager.inflateFromResource(this, paramInt, getPreferenceScreen()));
  }
  
  Header findBestMatchingHeader(Header paramHeader, ArrayList<Header> paramArrayList)
  {
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    Header localHeader;
    int j;
    if (i < paramArrayList.size())
    {
      localHeader = (Header)paramArrayList.get(i);
      if ((paramHeader == localHeader) || ((paramHeader.id != -1L) && (paramHeader.id == localHeader.id)))
      {
        localArrayList.clear();
        localArrayList.add(localHeader);
      }
    }
    else
    {
      j = localArrayList.size();
      if (j != 1) {
        break label198;
      }
      return (Header)localArrayList.get(0);
    }
    if (paramHeader.fragment != null) {
      if (paramHeader.fragment.equals(localHeader.fragment)) {
        localArrayList.add(localHeader);
      }
    }
    for (;;)
    {
      i += 1;
      break;
      if (paramHeader.intent != null)
      {
        if (paramHeader.intent.equals(localHeader.intent)) {
          localArrayList.add(localHeader);
        }
      }
      else if ((paramHeader.title != null) && (paramHeader.title.equals(localHeader.title))) {
        localArrayList.add(localHeader);
      }
    }
    label198:
    if (j > 1)
    {
      i = 0;
      while (i < j)
      {
        paramArrayList = (Header)localArrayList.get(i);
        if ((paramHeader.fragmentArguments != null) && (paramHeader.fragmentArguments.equals(paramArrayList.fragmentArguments))) {
          return paramArrayList;
        }
        if ((paramHeader.extras != null) && (paramHeader.extras.equals(paramArrayList.extras))) {
          return paramArrayList;
        }
        if ((paramHeader.title != null) && (paramHeader.title.equals(paramArrayList.title))) {
          return paramArrayList;
        }
        i += 1;
      }
    }
    return null;
  }
  
  @Deprecated
  public Preference findPreference(CharSequence paramCharSequence)
  {
    if (this.mPreferenceManager == null) {
      return null;
    }
    return this.mPreferenceManager.findPreference(paramCharSequence);
  }
  
  public void finishPreferencePanel(Fragment paramFragment, int paramInt, Intent paramIntent)
  {
    if (this.mSinglePane)
    {
      setResult(paramInt, paramIntent);
      finish();
    }
    do
    {
      return;
      onBackPressed();
    } while ((paramFragment == null) || (paramFragment.getTargetFragment() == null));
    paramFragment.getTargetFragment().onActivityResult(paramFragment.getTargetRequestCode(), paramInt, paramIntent);
  }
  
  public List<Header> getHeaders()
  {
    return this.mHeaders;
  }
  
  protected Button getNextButton()
  {
    return this.mNextButton;
  }
  
  @Deprecated
  public PreferenceManager getPreferenceManager()
  {
    return this.mPreferenceManager;
  }
  
  @Deprecated
  public PreferenceScreen getPreferenceScreen()
  {
    if (this.mPreferenceManager != null) {
      return this.mPreferenceManager.getPreferenceScreen();
    }
    return null;
  }
  
  public boolean hasHeaders()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (getListView().getVisibility() == 0)
    {
      bool1 = bool2;
      if (this.mPreferenceManager == null) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  protected boolean hasNextButton()
  {
    return this.mNextButton != null;
  }
  
  public void invalidateHeaders()
  {
    if (!this.mHandler.hasMessages(2)) {
      this.mHandler.sendEmptyMessage(2);
    }
  }
  
  public boolean isMultiPane()
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (hasHeaders())
    {
      bool1 = bool2;
      if (this.mPrefsContainer.getVisibility() == 0) {
        bool1 = true;
      }
    }
    return bool1;
  }
  
  protected boolean isValidFragment(String paramString)
  {
    if (getApplicationInfo().targetSdkVersion >= 19) {
      throw new RuntimeException("Subclasses of PreferenceActivity must override isValidFragment(String) to verify that the Fragment class is valid! " + getClass().getName() + " has not checked if fragment " + paramString + " is valid.");
    }
    return true;
  }
  
  /* Error */
  public void loadHeadersFromResource(int paramInt, List<Header> paramList)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 8
    //   6: aconst_null
    //   7: astore 6
    //   9: aload_0
    //   10: invokevirtual 411	android/preference/PreferenceActivity:getResources	()Landroid/content/res/Resources;
    //   13: iload_1
    //   14: invokevirtual 417	android/content/res/Resources:getXml	(I)Landroid/content/res/XmlResourceParser;
    //   17: astore 7
    //   19: aload 7
    //   21: astore 6
    //   23: aload 7
    //   25: astore 5
    //   27: aload 7
    //   29: astore 8
    //   31: aload 7
    //   33: invokestatic 423	android/util/Xml:asAttributeSet	(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;
    //   36: astore 11
    //   38: aload 7
    //   40: astore 6
    //   42: aload 7
    //   44: astore 5
    //   46: aload 7
    //   48: astore 8
    //   50: aload 7
    //   52: invokeinterface 428 1 0
    //   57: istore_1
    //   58: iload_1
    //   59: iconst_1
    //   60: if_icmpeq +8 -> 68
    //   63: iload_1
    //   64: iconst_2
    //   65: if_icmpne -27 -> 38
    //   68: aload 7
    //   70: astore 6
    //   72: aload 7
    //   74: astore 5
    //   76: aload 7
    //   78: astore 8
    //   80: aload 7
    //   82: invokeinterface 429 1 0
    //   87: astore 9
    //   89: aload 7
    //   91: astore 6
    //   93: aload 7
    //   95: astore 5
    //   97: aload 7
    //   99: astore 8
    //   101: ldc_w 431
    //   104: aload 9
    //   106: invokevirtual 293	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   109: ifne +92 -> 201
    //   112: aload 7
    //   114: astore 6
    //   116: aload 7
    //   118: astore 5
    //   120: aload 7
    //   122: astore 8
    //   124: new 180	java/lang/RuntimeException
    //   127: dup
    //   128: new 207	java/lang/StringBuilder
    //   131: dup
    //   132: invokespecial 208	java/lang/StringBuilder:<init>	()V
    //   135: ldc_w 433
    //   138: invokevirtual 214	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   141: aload 9
    //   143: invokevirtual 214	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   146: ldc_w 435
    //   149: invokevirtual 214	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   152: aload 7
    //   154: invokeinterface 438 1 0
    //   159: invokevirtual 214	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   162: invokevirtual 218	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   165: invokespecial 185	java/lang/RuntimeException:<init>	(Ljava/lang/String;)V
    //   168: athrow
    //   169: astore_2
    //   170: aload 6
    //   172: astore 5
    //   174: new 180	java/lang/RuntimeException
    //   177: dup
    //   178: ldc_w 440
    //   181: aload_2
    //   182: invokespecial 443	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   185: athrow
    //   186: astore_2
    //   187: aload 5
    //   189: ifnull +10 -> 199
    //   192: aload 5
    //   194: invokeinterface 446 1 0
    //   199: aload_2
    //   200: athrow
    //   201: aconst_null
    //   202: astore 9
    //   204: aload 7
    //   206: astore 6
    //   208: aload 7
    //   210: astore 5
    //   212: aload 7
    //   214: astore 8
    //   216: aload 7
    //   218: invokeinterface 449 1 0
    //   223: istore_1
    //   224: aload 7
    //   226: astore 6
    //   228: aload 7
    //   230: astore 5
    //   232: aload 7
    //   234: astore 8
    //   236: aload 7
    //   238: invokeinterface 428 1 0
    //   243: istore_3
    //   244: iload_3
    //   245: iconst_1
    //   246: if_icmpeq +1044 -> 1290
    //   249: iload_3
    //   250: iconst_3
    //   251: if_icmpne +26 -> 277
    //   254: aload 7
    //   256: astore 6
    //   258: aload 7
    //   260: astore 5
    //   262: aload 7
    //   264: astore 8
    //   266: aload 7
    //   268: invokeinterface 449 1 0
    //   273: iload_1
    //   274: if_icmple +1016 -> 1290
    //   277: iload_3
    //   278: iconst_3
    //   279: if_icmpeq -55 -> 224
    //   282: iload_3
    //   283: iconst_4
    //   284: if_icmpeq -60 -> 224
    //   287: aload 7
    //   289: astore 6
    //   291: aload 7
    //   293: astore 5
    //   295: aload 7
    //   297: astore 8
    //   299: ldc_w 451
    //   302: aload 7
    //   304: invokeinterface 429 1 0
    //   309: invokevirtual 293	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   312: ifeq +958 -> 1270
    //   315: aload 7
    //   317: astore 6
    //   319: aload 7
    //   321: astore 5
    //   323: aload 7
    //   325: astore 8
    //   327: new 18	android/preference/PreferenceActivity$Header
    //   330: dup
    //   331: invokespecial 452	android/preference/PreferenceActivity$Header:<init>	()V
    //   334: astore 12
    //   336: aload 7
    //   338: astore 6
    //   340: aload 7
    //   342: astore 5
    //   344: aload 7
    //   346: astore 8
    //   348: aload_0
    //   349: aload 11
    //   351: getstatic 458	com/android/internal/R$styleable:PreferenceHeader	[I
    //   354: invokevirtual 462	android/preference/PreferenceActivity:obtainStyledAttributes	(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;
    //   357: astore 10
    //   359: aload 7
    //   361: astore 6
    //   363: aload 7
    //   365: astore 5
    //   367: aload 7
    //   369: astore 8
    //   371: aload 12
    //   373: aload 10
    //   375: iconst_1
    //   376: iconst_m1
    //   377: invokevirtual 468	android/content/res/TypedArray:getResourceId	(II)I
    //   380: i2l
    //   381: putfield 278	android/preference/PreferenceActivity$Header:id	J
    //   384: aload 7
    //   386: astore 6
    //   388: aload 7
    //   390: astore 5
    //   392: aload 7
    //   394: astore 8
    //   396: aload 10
    //   398: iconst_2
    //   399: invokevirtual 472	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   402: astore 13
    //   404: aload 13
    //   406: ifnull +66 -> 472
    //   409: aload 7
    //   411: astore 6
    //   413: aload 7
    //   415: astore 5
    //   417: aload 7
    //   419: astore 8
    //   421: aload 13
    //   423: getfield 477	android/util/TypedValue:type	I
    //   426: iconst_3
    //   427: if_icmpne +45 -> 472
    //   430: aload 7
    //   432: astore 6
    //   434: aload 7
    //   436: astore 5
    //   438: aload 7
    //   440: astore 8
    //   442: aload 13
    //   444: getfield 480	android/util/TypedValue:resourceId	I
    //   447: ifeq +579 -> 1026
    //   450: aload 7
    //   452: astore 6
    //   454: aload 7
    //   456: astore 5
    //   458: aload 7
    //   460: astore 8
    //   462: aload 12
    //   464: aload 13
    //   466: getfield 480	android/util/TypedValue:resourceId	I
    //   469: putfield 483	android/preference/PreferenceActivity$Header:titleRes	I
    //   472: aload 7
    //   474: astore 6
    //   476: aload 7
    //   478: astore 5
    //   480: aload 7
    //   482: astore 8
    //   484: aload 10
    //   486: iconst_3
    //   487: invokevirtual 472	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   490: astore 13
    //   492: aload 13
    //   494: ifnull +66 -> 560
    //   497: aload 7
    //   499: astore 6
    //   501: aload 7
    //   503: astore 5
    //   505: aload 7
    //   507: astore 8
    //   509: aload 13
    //   511: getfield 477	android/util/TypedValue:type	I
    //   514: iconst_3
    //   515: if_icmpne +45 -> 560
    //   518: aload 7
    //   520: astore 6
    //   522: aload 7
    //   524: astore 5
    //   526: aload 7
    //   528: astore 8
    //   530: aload 13
    //   532: getfield 480	android/util/TypedValue:resourceId	I
    //   535: ifeq +516 -> 1051
    //   538: aload 7
    //   540: astore 6
    //   542: aload 7
    //   544: astore 5
    //   546: aload 7
    //   548: astore 8
    //   550: aload 12
    //   552: aload 13
    //   554: getfield 480	android/util/TypedValue:resourceId	I
    //   557: putfield 486	android/preference/PreferenceActivity$Header:summaryRes	I
    //   560: aload 7
    //   562: astore 6
    //   564: aload 7
    //   566: astore 5
    //   568: aload 7
    //   570: astore 8
    //   572: aload 10
    //   574: iconst_5
    //   575: invokevirtual 472	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   578: astore 13
    //   580: aload 13
    //   582: ifnull +66 -> 648
    //   585: aload 7
    //   587: astore 6
    //   589: aload 7
    //   591: astore 5
    //   593: aload 7
    //   595: astore 8
    //   597: aload 13
    //   599: getfield 477	android/util/TypedValue:type	I
    //   602: iconst_3
    //   603: if_icmpne +45 -> 648
    //   606: aload 7
    //   608: astore 6
    //   610: aload 7
    //   612: astore 5
    //   614: aload 7
    //   616: astore 8
    //   618: aload 13
    //   620: getfield 480	android/util/TypedValue:resourceId	I
    //   623: ifeq +453 -> 1076
    //   626: aload 7
    //   628: astore 6
    //   630: aload 7
    //   632: astore 5
    //   634: aload 7
    //   636: astore 8
    //   638: aload 12
    //   640: aload 13
    //   642: getfield 480	android/util/TypedValue:resourceId	I
    //   645: putfield 489	android/preference/PreferenceActivity$Header:breadCrumbTitleRes	I
    //   648: aload 7
    //   650: astore 6
    //   652: aload 7
    //   654: astore 5
    //   656: aload 7
    //   658: astore 8
    //   660: aload 10
    //   662: bipush 6
    //   664: invokevirtual 472	android/content/res/TypedArray:peekValue	(I)Landroid/util/TypedValue;
    //   667: astore 13
    //   669: aload 13
    //   671: ifnull +66 -> 737
    //   674: aload 7
    //   676: astore 6
    //   678: aload 7
    //   680: astore 5
    //   682: aload 7
    //   684: astore 8
    //   686: aload 13
    //   688: getfield 477	android/util/TypedValue:type	I
    //   691: iconst_3
    //   692: if_icmpne +45 -> 737
    //   695: aload 7
    //   697: astore 6
    //   699: aload 7
    //   701: astore 5
    //   703: aload 7
    //   705: astore 8
    //   707: aload 13
    //   709: getfield 480	android/util/TypedValue:resourceId	I
    //   712: ifeq +389 -> 1101
    //   715: aload 7
    //   717: astore 6
    //   719: aload 7
    //   721: astore 5
    //   723: aload 7
    //   725: astore 8
    //   727: aload 12
    //   729: aload 13
    //   731: getfield 480	android/util/TypedValue:resourceId	I
    //   734: putfield 492	android/preference/PreferenceActivity$Header:breadCrumbShortTitleRes	I
    //   737: aload 7
    //   739: astore 6
    //   741: aload 7
    //   743: astore 5
    //   745: aload 7
    //   747: astore 8
    //   749: aload 12
    //   751: aload 10
    //   753: iconst_0
    //   754: iconst_0
    //   755: invokevirtual 468	android/content/res/TypedArray:getResourceId	(II)I
    //   758: putfield 495	android/preference/PreferenceActivity$Header:iconRes	I
    //   761: aload 7
    //   763: astore 6
    //   765: aload 7
    //   767: astore 5
    //   769: aload 7
    //   771: astore 8
    //   773: aload 12
    //   775: aload 10
    //   777: iconst_4
    //   778: invokevirtual 499	android/content/res/TypedArray:getString	(I)Ljava/lang/String;
    //   781: putfield 288	android/preference/PreferenceActivity$Header:fragment	Ljava/lang/String;
    //   784: aload 7
    //   786: astore 6
    //   788: aload 7
    //   790: astore 5
    //   792: aload 7
    //   794: astore 8
    //   796: aload 10
    //   798: invokevirtual 502	android/content/res/TypedArray:recycle	()V
    //   801: aload 9
    //   803: astore 10
    //   805: aload 9
    //   807: ifnonnull +24 -> 831
    //   810: aload 7
    //   812: astore 6
    //   814: aload 7
    //   816: astore 5
    //   818: aload 7
    //   820: astore 8
    //   822: new 312	android/os/Bundle
    //   825: dup
    //   826: invokespecial 503	android/os/Bundle:<init>	()V
    //   829: astore 10
    //   831: aload 7
    //   833: astore 6
    //   835: aload 7
    //   837: astore 5
    //   839: aload 7
    //   841: astore 8
    //   843: aload 7
    //   845: invokeinterface 449 1 0
    //   850: istore_3
    //   851: aload 7
    //   853: astore 6
    //   855: aload 7
    //   857: astore 5
    //   859: aload 7
    //   861: astore 8
    //   863: aload 7
    //   865: invokeinterface 428 1 0
    //   870: istore 4
    //   872: iload 4
    //   874: iconst_1
    //   875: if_icmpeq +325 -> 1200
    //   878: iload 4
    //   880: iconst_3
    //   881: if_icmpne +26 -> 907
    //   884: aload 7
    //   886: astore 6
    //   888: aload 7
    //   890: astore 5
    //   892: aload 7
    //   894: astore 8
    //   896: aload 7
    //   898: invokeinterface 449 1 0
    //   903: iload_3
    //   904: if_icmple +296 -> 1200
    //   907: iload 4
    //   909: iconst_3
    //   910: if_icmpeq -59 -> 851
    //   913: iload 4
    //   915: iconst_4
    //   916: if_icmpeq -65 -> 851
    //   919: aload 7
    //   921: astore 6
    //   923: aload 7
    //   925: astore 5
    //   927: aload 7
    //   929: astore 8
    //   931: aload 7
    //   933: invokeinterface 429 1 0
    //   938: astore 9
    //   940: aload 7
    //   942: astore 6
    //   944: aload 7
    //   946: astore 5
    //   948: aload 7
    //   950: astore 8
    //   952: aload 9
    //   954: ldc_w 505
    //   957: invokevirtual 293	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   960: ifeq +166 -> 1126
    //   963: aload 7
    //   965: astore 6
    //   967: aload 7
    //   969: astore 5
    //   971: aload 7
    //   973: astore 8
    //   975: aload_0
    //   976: invokevirtual 411	android/preference/PreferenceActivity:getResources	()Landroid/content/res/Resources;
    //   979: ldc_w 505
    //   982: aload 11
    //   984: aload 10
    //   986: invokevirtual 509	android/content/res/Resources:parseBundleExtra	(Ljava/lang/String;Landroid/util/AttributeSet;Landroid/os/Bundle;)V
    //   989: aload 7
    //   991: astore 6
    //   993: aload 7
    //   995: astore 5
    //   997: aload 7
    //   999: astore 8
    //   1001: aload 7
    //   1003: invokestatic 515	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   1006: goto -155 -> 851
    //   1009: astore_2
    //   1010: aload 8
    //   1012: astore 5
    //   1014: new 180	java/lang/RuntimeException
    //   1017: dup
    //   1018: ldc_w 440
    //   1021: aload_2
    //   1022: invokespecial 443	java/lang/RuntimeException:<init>	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   1025: athrow
    //   1026: aload 7
    //   1028: astore 6
    //   1030: aload 7
    //   1032: astore 5
    //   1034: aload 7
    //   1036: astore 8
    //   1038: aload 12
    //   1040: aload 13
    //   1042: getfield 518	android/util/TypedValue:string	Ljava/lang/CharSequence;
    //   1045: putfield 304	android/preference/PreferenceActivity$Header:title	Ljava/lang/CharSequence;
    //   1048: goto -576 -> 472
    //   1051: aload 7
    //   1053: astore 6
    //   1055: aload 7
    //   1057: astore 5
    //   1059: aload 7
    //   1061: astore 8
    //   1063: aload 12
    //   1065: aload 13
    //   1067: getfield 518	android/util/TypedValue:string	Ljava/lang/CharSequence;
    //   1070: putfield 521	android/preference/PreferenceActivity$Header:summary	Ljava/lang/CharSequence;
    //   1073: goto -513 -> 560
    //   1076: aload 7
    //   1078: astore 6
    //   1080: aload 7
    //   1082: astore 5
    //   1084: aload 7
    //   1086: astore 8
    //   1088: aload 12
    //   1090: aload 13
    //   1092: getfield 518	android/util/TypedValue:string	Ljava/lang/CharSequence;
    //   1095: putfield 524	android/preference/PreferenceActivity$Header:breadCrumbTitle	Ljava/lang/CharSequence;
    //   1098: goto -450 -> 648
    //   1101: aload 7
    //   1103: astore 6
    //   1105: aload 7
    //   1107: astore 5
    //   1109: aload 7
    //   1111: astore 8
    //   1113: aload 12
    //   1115: aload 13
    //   1117: getfield 518	android/util/TypedValue:string	Ljava/lang/CharSequence;
    //   1120: putfield 527	android/preference/PreferenceActivity$Header:breadCrumbShortTitle	Ljava/lang/CharSequence;
    //   1123: goto -386 -> 737
    //   1126: aload 7
    //   1128: astore 6
    //   1130: aload 7
    //   1132: astore 5
    //   1134: aload 7
    //   1136: astore 8
    //   1138: aload 9
    //   1140: ldc_w 528
    //   1143: invokevirtual 293	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1146: ifeq +34 -> 1180
    //   1149: aload 7
    //   1151: astore 6
    //   1153: aload 7
    //   1155: astore 5
    //   1157: aload 7
    //   1159: astore 8
    //   1161: aload 12
    //   1163: aload_0
    //   1164: invokevirtual 411	android/preference/PreferenceActivity:getResources	()Landroid/content/res/Resources;
    //   1167: aload 7
    //   1169: aload 11
    //   1171: invokestatic 532	android/content/Intent:parseIntent	(Landroid/content/res/Resources;Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;)Landroid/content/Intent;
    //   1174: putfield 297	android/preference/PreferenceActivity$Header:intent	Landroid/content/Intent;
    //   1177: goto -326 -> 851
    //   1180: aload 7
    //   1182: astore 6
    //   1184: aload 7
    //   1186: astore 5
    //   1188: aload 7
    //   1190: astore 8
    //   1192: aload 7
    //   1194: invokestatic 515	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   1197: goto -346 -> 851
    //   1200: aload 7
    //   1202: astore 6
    //   1204: aload 7
    //   1206: astore 5
    //   1208: aload 7
    //   1210: astore 8
    //   1212: aload 10
    //   1214: astore 9
    //   1216: aload 10
    //   1218: invokevirtual 533	android/os/Bundle:size	()I
    //   1221: ifle +25 -> 1246
    //   1224: aload 7
    //   1226: astore 6
    //   1228: aload 7
    //   1230: astore 5
    //   1232: aload 7
    //   1234: astore 8
    //   1236: aload 12
    //   1238: aload 10
    //   1240: putfield 310	android/preference/PreferenceActivity$Header:fragmentArguments	Landroid/os/Bundle;
    //   1243: aconst_null
    //   1244: astore 9
    //   1246: aload 7
    //   1248: astore 6
    //   1250: aload 7
    //   1252: astore 5
    //   1254: aload 7
    //   1256: astore 8
    //   1258: aload_2
    //   1259: aload 12
    //   1261: invokeinterface 536 2 0
    //   1266: pop
    //   1267: goto -1043 -> 224
    //   1270: aload 7
    //   1272: astore 6
    //   1274: aload 7
    //   1276: astore 5
    //   1278: aload 7
    //   1280: astore 8
    //   1282: aload 7
    //   1284: invokestatic 515	com/android/internal/util/XmlUtils:skipCurrentTag	(Lorg/xmlpull/v1/XmlPullParser;)V
    //   1287: goto -1063 -> 224
    //   1290: aload 7
    //   1292: ifnull +10 -> 1302
    //   1295: aload 7
    //   1297: invokeinterface 446 1 0
    //   1302: return
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	1303	0	this	PreferenceActivity
    //   0	1303	1	paramInt	int
    //   0	1303	2	paramList	List<Header>
    //   243	662	3	i	int
    //   870	47	4	j	int
    //   1	1276	5	localObject1	Object
    //   7	1266	6	localObject2	Object
    //   17	1279	7	localXmlResourceParser	android.content.res.XmlResourceParser
    //   4	1277	8	localObject3	Object
    //   87	1158	9	localObject4	Object
    //   357	882	10	localObject5	Object
    //   36	1134	11	localAttributeSet	android.util.AttributeSet
    //   334	926	12	localHeader	Header
    //   402	714	13	localTypedValue	android.util.TypedValue
    // Exception table:
    //   from	to	target	type
    //   9	19	169	org/xmlpull/v1/XmlPullParserException
    //   31	38	169	org/xmlpull/v1/XmlPullParserException
    //   50	58	169	org/xmlpull/v1/XmlPullParserException
    //   80	89	169	org/xmlpull/v1/XmlPullParserException
    //   101	112	169	org/xmlpull/v1/XmlPullParserException
    //   124	169	169	org/xmlpull/v1/XmlPullParserException
    //   216	224	169	org/xmlpull/v1/XmlPullParserException
    //   236	244	169	org/xmlpull/v1/XmlPullParserException
    //   266	277	169	org/xmlpull/v1/XmlPullParserException
    //   299	315	169	org/xmlpull/v1/XmlPullParserException
    //   327	336	169	org/xmlpull/v1/XmlPullParserException
    //   348	359	169	org/xmlpull/v1/XmlPullParserException
    //   371	384	169	org/xmlpull/v1/XmlPullParserException
    //   396	404	169	org/xmlpull/v1/XmlPullParserException
    //   421	430	169	org/xmlpull/v1/XmlPullParserException
    //   442	450	169	org/xmlpull/v1/XmlPullParserException
    //   462	472	169	org/xmlpull/v1/XmlPullParserException
    //   484	492	169	org/xmlpull/v1/XmlPullParserException
    //   509	518	169	org/xmlpull/v1/XmlPullParserException
    //   530	538	169	org/xmlpull/v1/XmlPullParserException
    //   550	560	169	org/xmlpull/v1/XmlPullParserException
    //   572	580	169	org/xmlpull/v1/XmlPullParserException
    //   597	606	169	org/xmlpull/v1/XmlPullParserException
    //   618	626	169	org/xmlpull/v1/XmlPullParserException
    //   638	648	169	org/xmlpull/v1/XmlPullParserException
    //   660	669	169	org/xmlpull/v1/XmlPullParserException
    //   686	695	169	org/xmlpull/v1/XmlPullParserException
    //   707	715	169	org/xmlpull/v1/XmlPullParserException
    //   727	737	169	org/xmlpull/v1/XmlPullParserException
    //   749	761	169	org/xmlpull/v1/XmlPullParserException
    //   773	784	169	org/xmlpull/v1/XmlPullParserException
    //   796	801	169	org/xmlpull/v1/XmlPullParserException
    //   822	831	169	org/xmlpull/v1/XmlPullParserException
    //   843	851	169	org/xmlpull/v1/XmlPullParserException
    //   863	872	169	org/xmlpull/v1/XmlPullParserException
    //   896	907	169	org/xmlpull/v1/XmlPullParserException
    //   931	940	169	org/xmlpull/v1/XmlPullParserException
    //   952	963	169	org/xmlpull/v1/XmlPullParserException
    //   975	989	169	org/xmlpull/v1/XmlPullParserException
    //   1001	1006	169	org/xmlpull/v1/XmlPullParserException
    //   1038	1048	169	org/xmlpull/v1/XmlPullParserException
    //   1063	1073	169	org/xmlpull/v1/XmlPullParserException
    //   1088	1098	169	org/xmlpull/v1/XmlPullParserException
    //   1113	1123	169	org/xmlpull/v1/XmlPullParserException
    //   1138	1149	169	org/xmlpull/v1/XmlPullParserException
    //   1161	1177	169	org/xmlpull/v1/XmlPullParserException
    //   1192	1197	169	org/xmlpull/v1/XmlPullParserException
    //   1216	1224	169	org/xmlpull/v1/XmlPullParserException
    //   1236	1243	169	org/xmlpull/v1/XmlPullParserException
    //   1258	1267	169	org/xmlpull/v1/XmlPullParserException
    //   1282	1287	169	org/xmlpull/v1/XmlPullParserException
    //   9	19	186	finally
    //   31	38	186	finally
    //   50	58	186	finally
    //   80	89	186	finally
    //   101	112	186	finally
    //   124	169	186	finally
    //   174	186	186	finally
    //   216	224	186	finally
    //   236	244	186	finally
    //   266	277	186	finally
    //   299	315	186	finally
    //   327	336	186	finally
    //   348	359	186	finally
    //   371	384	186	finally
    //   396	404	186	finally
    //   421	430	186	finally
    //   442	450	186	finally
    //   462	472	186	finally
    //   484	492	186	finally
    //   509	518	186	finally
    //   530	538	186	finally
    //   550	560	186	finally
    //   572	580	186	finally
    //   597	606	186	finally
    //   618	626	186	finally
    //   638	648	186	finally
    //   660	669	186	finally
    //   686	695	186	finally
    //   707	715	186	finally
    //   727	737	186	finally
    //   749	761	186	finally
    //   773	784	186	finally
    //   796	801	186	finally
    //   822	831	186	finally
    //   843	851	186	finally
    //   863	872	186	finally
    //   896	907	186	finally
    //   931	940	186	finally
    //   952	963	186	finally
    //   975	989	186	finally
    //   1001	1006	186	finally
    //   1014	1026	186	finally
    //   1038	1048	186	finally
    //   1063	1073	186	finally
    //   1088	1098	186	finally
    //   1113	1123	186	finally
    //   1138	1149	186	finally
    //   1161	1177	186	finally
    //   1192	1197	186	finally
    //   1216	1224	186	finally
    //   1236	1243	186	finally
    //   1258	1267	186	finally
    //   1282	1287	186	finally
    //   9	19	1009	java/io/IOException
    //   31	38	1009	java/io/IOException
    //   50	58	1009	java/io/IOException
    //   80	89	1009	java/io/IOException
    //   101	112	1009	java/io/IOException
    //   124	169	1009	java/io/IOException
    //   216	224	1009	java/io/IOException
    //   236	244	1009	java/io/IOException
    //   266	277	1009	java/io/IOException
    //   299	315	1009	java/io/IOException
    //   327	336	1009	java/io/IOException
    //   348	359	1009	java/io/IOException
    //   371	384	1009	java/io/IOException
    //   396	404	1009	java/io/IOException
    //   421	430	1009	java/io/IOException
    //   442	450	1009	java/io/IOException
    //   462	472	1009	java/io/IOException
    //   484	492	1009	java/io/IOException
    //   509	518	1009	java/io/IOException
    //   530	538	1009	java/io/IOException
    //   550	560	1009	java/io/IOException
    //   572	580	1009	java/io/IOException
    //   597	606	1009	java/io/IOException
    //   618	626	1009	java/io/IOException
    //   638	648	1009	java/io/IOException
    //   660	669	1009	java/io/IOException
    //   686	695	1009	java/io/IOException
    //   707	715	1009	java/io/IOException
    //   727	737	1009	java/io/IOException
    //   749	761	1009	java/io/IOException
    //   773	784	1009	java/io/IOException
    //   796	801	1009	java/io/IOException
    //   822	831	1009	java/io/IOException
    //   843	851	1009	java/io/IOException
    //   863	872	1009	java/io/IOException
    //   896	907	1009	java/io/IOException
    //   931	940	1009	java/io/IOException
    //   952	963	1009	java/io/IOException
    //   975	989	1009	java/io/IOException
    //   1001	1006	1009	java/io/IOException
    //   1038	1048	1009	java/io/IOException
    //   1063	1073	1009	java/io/IOException
    //   1088	1098	1009	java/io/IOException
    //   1113	1123	1009	java/io/IOException
    //   1138	1149	1009	java/io/IOException
    //   1161	1177	1009	java/io/IOException
    //   1192	1197	1009	java/io/IOException
    //   1216	1224	1009	java/io/IOException
    //   1236	1243	1009	java/io/IOException
    //   1258	1267	1009	java/io/IOException
    //   1282	1287	1009	java/io/IOException
  }
  
  protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    super.onActivityResult(paramInt1, paramInt2, paramIntent);
    if (this.mPreferenceManager != null) {
      this.mPreferenceManager.dispatchActivityResult(paramInt1, paramInt2, paramIntent);
    }
  }
  
  public void onBuildHeaders(List<Header> paramList) {}
  
  public Intent onBuildStartFragmentIntent(String paramString, Bundle paramBundle, int paramInt1, int paramInt2)
  {
    Intent localIntent = new Intent("android.intent.action.MAIN");
    localIntent.setClass(this, getClass());
    localIntent.putExtra(":android:show_fragment", paramString);
    localIntent.putExtra(":android:show_fragment_args", paramBundle);
    localIntent.putExtra(":android:show_fragment_title", paramInt1);
    localIntent.putExtra(":android:show_fragment_short_title", paramInt2);
    localIntent.putExtra(":android:no_headers", true);
    return localIntent;
  }
  
  public void onContentChanged()
  {
    super.onContentChanged();
    if (this.mPreferenceManager != null) {
      postBindPreferences();
    }
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Object localObject1 = obtainStyledAttributes(null, R.styleable.PreferenceActivity, 18219039, 0);
    int i = ((TypedArray)localObject1).getResourceId(0, 17367221);
    this.mPreferenceHeaderItemResId = ((TypedArray)localObject1).getResourceId(1, 17367215);
    this.mPreferenceHeaderRemoveEmptyIcon = ((TypedArray)localObject1).getBoolean(2, false);
    ((TypedArray)localObject1).recycle();
    setContentView(i);
    this.mListFooter = ((FrameLayout)findViewById(16909275));
    this.mPrefsContainer = ((ViewGroup)findViewById(16909276));
    boolean bool;
    Object localObject2;
    int j;
    label227:
    label280:
    label287:
    String str;
    if ((!onIsHidingHeaders()) && (onIsMultiPane()))
    {
      bool = false;
      this.mSinglePane = bool;
      localObject1 = getIntent().getStringExtra(":android:show_fragment");
      localObject2 = getIntent().getBundleExtra(":android:show_fragment_args");
      i = getIntent().getIntExtra(":android:show_fragment_title", 0);
      j = getIntent().getIntExtra(":android:show_fragment_short_title", 0);
      if (paramBundle == null) {
        break label481;
      }
      localObject2 = paramBundle.getParcelableArrayList(":android:headers");
      if (localObject2 != null)
      {
        this.mHeaders.addAll((Collection)localObject2);
        int k = paramBundle.getInt(":android:cur_header", -1);
        if ((k >= 0) && (k < this.mHeaders.size())) {
          setSelectedHeader((Header)this.mHeaders.get(k));
        }
      }
      if ((localObject1 == null) || (!this.mSinglePane)) {
        break label594;
      }
      findViewById(16909274).setVisibility(8);
      this.mPrefsContainer.setVisibility(0);
      if (i != 0)
      {
        localObject1 = getText(i);
        if (j == 0) {
          break label589;
        }
        paramBundle = getText(j);
        showBreadCrumbs((CharSequence)localObject1, paramBundle);
      }
      paramBundle = getIntent();
      if (paramBundle.getBooleanExtra("extra_prefs_show_button_bar", false))
      {
        findViewById(16909278).setVisibility(0);
        localObject1 = (Button)findViewById(16909279);
        ((Button)localObject1).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PreferenceActivity.this.setResult(0);
            PreferenceActivity.this.finish();
          }
        });
        localObject2 = (Button)findViewById(16909280);
        ((Button)localObject2).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PreferenceActivity.this.setResult(-1);
            PreferenceActivity.this.finish();
          }
        });
        this.mNextButton = ((Button)findViewById(16909281));
        this.mNextButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramAnonymousView)
          {
            PreferenceActivity.this.setResult(-1);
            PreferenceActivity.this.finish();
          }
        });
        if (paramBundle.hasExtra("extra_prefs_set_next_text"))
        {
          str = paramBundle.getStringExtra("extra_prefs_set_next_text");
          if (!TextUtils.isEmpty(str)) {
            break label728;
          }
          this.mNextButton.setVisibility(8);
        }
        label426:
        if (paramBundle.hasExtra("extra_prefs_set_back_text"))
        {
          str = paramBundle.getStringExtra("extra_prefs_set_back_text");
          if (!TextUtils.isEmpty(str)) {
            break label740;
          }
          ((Button)localObject1).setVisibility(8);
        }
      }
    }
    for (;;)
    {
      if (paramBundle.getBooleanExtra("extra_prefs_show_skip", false)) {
        ((Button)localObject2).setVisibility(0);
      }
      return;
      bool = true;
      break;
      label481:
      if ((localObject1 != null) && (this.mSinglePane))
      {
        switchToHeader((String)localObject1, (Bundle)localObject2);
        if (i == 0) {
          break label227;
        }
        localObject2 = getText(i);
        if (j != 0) {}
        for (paramBundle = getText(j);; paramBundle = null)
        {
          showBreadCrumbs((CharSequence)localObject2, paramBundle);
          break;
        }
      }
      onBuildHeaders(this.mHeaders);
      if ((this.mHeaders.size() <= 0) || (this.mSinglePane)) {
        break label227;
      }
      if (localObject1 == null)
      {
        switchToHeader(onGetInitialHeader());
        break label227;
      }
      switchToHeader((String)localObject1, (Bundle)localObject2);
      break label227;
      label589:
      paramBundle = null;
      break label280;
      label594:
      if (this.mHeaders.size() > 0)
      {
        setListAdapter(new HeaderAdapter(this, this.mHeaders, this.mPreferenceHeaderItemResId, this.mPreferenceHeaderRemoveEmptyIcon));
        if (this.mSinglePane) {
          break label287;
        }
        getListView().setChoiceMode(1);
        if (this.mCurHeader != null) {
          setSelectedHeader(this.mCurHeader);
        }
        this.mPrefsContainer.setVisibility(0);
        break label287;
      }
      setContentView(17367223);
      this.mListFooter = ((FrameLayout)findViewById(16909275));
      this.mPrefsContainer = ((ViewGroup)findViewById(16909277));
      this.mPreferenceManager = new PreferenceManager(this, 100);
      this.mPreferenceManager.setOnPreferenceTreeClickListener(this);
      break label287;
      label728:
      this.mNextButton.setText(str);
      break label426;
      label740:
      ((Button)localObject1).setText(str);
    }
  }
  
  protected void onDestroy()
  {
    this.mHandler.removeMessages(1);
    this.mHandler.removeMessages(2);
    super.onDestroy();
    if (this.mPreferenceManager != null) {
      this.mPreferenceManager.dispatchActivityDestroy();
    }
  }
  
  public Header onGetInitialHeader()
  {
    int i = 0;
    while (i < this.mHeaders.size())
    {
      Header localHeader = (Header)this.mHeaders.get(i);
      if (localHeader.fragment != null) {
        return localHeader;
      }
      i += 1;
    }
    throw new IllegalStateException("Must have at least one header with a fragment");
  }
  
  public Header onGetNewHeader()
  {
    return null;
  }
  
  public void onHeaderClick(Header paramHeader, int paramInt)
  {
    if (paramHeader.fragment != null) {
      if (this.mSinglePane)
      {
        j = paramHeader.breadCrumbTitleRes;
        i = paramHeader.breadCrumbShortTitleRes;
        paramInt = j;
        if (j == 0)
        {
          paramInt = paramHeader.titleRes;
          i = 0;
        }
        startWithFragment(paramHeader.fragment, paramHeader.fragmentArguments, null, 0, paramInt, i);
      }
    }
    while (paramHeader.intent == null)
    {
      int j;
      int i;
      return;
      switchToHeader(paramHeader);
      return;
    }
    startActivity(paramHeader.intent);
  }
  
  public boolean onIsHidingHeaders()
  {
    return getIntent().getBooleanExtra(":android:no_headers", false);
  }
  
  public boolean onIsMultiPane()
  {
    return getResources().getBoolean(17956869);
  }
  
  protected void onListItemClick(ListView paramListView, View paramView, int paramInt, long paramLong)
  {
    if (!isResumed()) {
      return;
    }
    super.onListItemClick(paramListView, paramView, paramInt, paramLong);
    if (this.mAdapter != null)
    {
      paramListView = this.mAdapter.getItem(paramInt);
      if ((paramListView instanceof Header)) {
        onHeaderClick((Header)paramListView, paramInt);
      }
    }
  }
  
  protected void onNewIntent(Intent paramIntent)
  {
    if (this.mPreferenceManager != null) {
      this.mPreferenceManager.dispatchNewIntent(paramIntent);
    }
  }
  
  public boolean onPreferenceStartFragment(PreferenceFragment paramPreferenceFragment, Preference paramPreference)
  {
    startPreferencePanel(paramPreference.getFragment(), paramPreference.getExtras(), paramPreference.getTitleRes(), paramPreference.getTitle(), null, 0);
    return true;
  }
  
  @Deprecated
  public boolean onPreferenceTreeClick(PreferenceScreen paramPreferenceScreen, Preference paramPreference)
  {
    return false;
  }
  
  protected void onRestoreInstanceState(Bundle paramBundle)
  {
    if (this.mPreferenceManager != null)
    {
      Bundle localBundle = paramBundle.getBundle(":android:preferences");
      if (localBundle != null)
      {
        PreferenceScreen localPreferenceScreen = getPreferenceScreen();
        if (localPreferenceScreen != null)
        {
          localPreferenceScreen.restoreHierarchyState(localBundle);
          this.mSavedInstanceState = paramBundle;
          return;
        }
      }
    }
    super.onRestoreInstanceState(paramBundle);
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    super.onSaveInstanceState(paramBundle);
    if (this.mHeaders.size() > 0)
    {
      paramBundle.putParcelableArrayList(":android:headers", this.mHeaders);
      if (this.mCurHeader != null)
      {
        int i = this.mHeaders.indexOf(this.mCurHeader);
        if (i >= 0) {
          paramBundle.putInt(":android:cur_header", i);
        }
      }
    }
    if (this.mPreferenceManager != null)
    {
      PreferenceScreen localPreferenceScreen = getPreferenceScreen();
      if (localPreferenceScreen != null)
      {
        Bundle localBundle = new Bundle();
        localPreferenceScreen.saveHierarchyState(localBundle);
        paramBundle.putBundle(":android:preferences", localBundle);
      }
    }
  }
  
  protected void onStop()
  {
    super.onStop();
    if (this.mPreferenceManager != null) {
      this.mPreferenceManager.dispatchActivityStop();
    }
  }
  
  public void setListFooter(View paramView)
  {
    this.mListFooter.removeAllViews();
    this.mListFooter.addView(paramView, new FrameLayout.LayoutParams(-1, -2));
  }
  
  public void setParentTitle(CharSequence paramCharSequence1, CharSequence paramCharSequence2, View.OnClickListener paramOnClickListener)
  {
    if (this.mFragmentBreadCrumbs != null) {
      this.mFragmentBreadCrumbs.setParentTitle(paramCharSequence1, paramCharSequence2, paramOnClickListener);
    }
  }
  
  @Deprecated
  public void setPreferenceScreen(PreferenceScreen paramPreferenceScreen)
  {
    requirePreferenceManager();
    if ((this.mPreferenceManager.setPreferences(paramPreferenceScreen)) && (paramPreferenceScreen != null))
    {
      postBindPreferences();
      paramPreferenceScreen = getPreferenceScreen().getTitle();
      if (paramPreferenceScreen != null) {
        setTitle(paramPreferenceScreen);
      }
    }
  }
  
  void setSelectedHeader(Header paramHeader)
  {
    this.mCurHeader = paramHeader;
    int i = this.mHeaders.indexOf(paramHeader);
    if (i >= 0) {
      getListView().setItemChecked(i, true);
    }
    for (;;)
    {
      showBreadCrumbs(paramHeader);
      return;
      getListView().clearChoices();
    }
  }
  
  void showBreadCrumbs(Header paramHeader)
  {
    if (paramHeader != null)
    {
      Object localObject2 = paramHeader.getBreadCrumbTitle(getResources());
      Object localObject1 = localObject2;
      if (localObject2 == null) {
        localObject1 = paramHeader.getTitle(getResources());
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = getTitle();
      }
      showBreadCrumbs((CharSequence)localObject2, paramHeader.getBreadCrumbShortTitle(getResources()));
      return;
    }
    showBreadCrumbs(getTitle(), null);
  }
  
  public void showBreadCrumbs(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    if (this.mFragmentBreadCrumbs == null)
    {
      View localView = findViewById(16908310);
      try
      {
        this.mFragmentBreadCrumbs = ((FragmentBreadCrumbs)localView);
        if (this.mFragmentBreadCrumbs == null)
        {
          if (paramCharSequence1 != null) {
            setTitle(paramCharSequence1);
          }
          return;
        }
      }
      catch (ClassCastException paramCharSequence2)
      {
        setTitle(paramCharSequence1);
        return;
      }
      if (this.mSinglePane)
      {
        this.mFragmentBreadCrumbs.setVisibility(8);
        localView = findViewById(16909128);
        if (localView != null) {
          localView.setVisibility(8);
        }
        setTitle(paramCharSequence1);
      }
      this.mFragmentBreadCrumbs.setMaxVisible(2);
      this.mFragmentBreadCrumbs.setActivity(this);
    }
    if (this.mFragmentBreadCrumbs.getVisibility() != 0)
    {
      setTitle(paramCharSequence1);
      return;
    }
    this.mFragmentBreadCrumbs.setTitle(paramCharSequence1, paramCharSequence2);
    this.mFragmentBreadCrumbs.setParentTitle(null, null, null);
  }
  
  public void startPreferenceFragment(Fragment paramFragment, boolean paramBoolean)
  {
    FragmentTransaction localFragmentTransaction = getFragmentManager().beginTransaction();
    localFragmentTransaction.replace(16909277, paramFragment);
    if (paramBoolean)
    {
      localFragmentTransaction.setTransition(4097);
      localFragmentTransaction.addToBackStack(":android:prefs");
    }
    for (;;)
    {
      localFragmentTransaction.commitAllowingStateLoss();
      return;
      localFragmentTransaction.setTransition(4099);
    }
  }
  
  public void startPreferencePanel(String paramString, Bundle paramBundle, int paramInt1, CharSequence paramCharSequence, Fragment paramFragment, int paramInt2)
  {
    if (this.mSinglePane)
    {
      startWithFragment(paramString, paramBundle, paramFragment, paramInt2, paramInt1, 0);
      return;
    }
    paramString = Fragment.instantiate(this, paramString, paramBundle);
    if (paramFragment != null) {
      paramString.setTargetFragment(paramFragment, paramInt2);
    }
    paramBundle = getFragmentManager().beginTransaction();
    paramBundle.replace(16909277, paramString);
    if (paramInt1 != 0) {
      paramBundle.setBreadCrumbTitle(paramInt1);
    }
    for (;;)
    {
      paramBundle.setTransition(4097);
      paramBundle.addToBackStack(":android:prefs");
      paramBundle.commitAllowingStateLoss();
      return;
      if (paramCharSequence != null) {
        paramBundle.setBreadCrumbTitle(paramCharSequence);
      }
    }
  }
  
  public void startWithFragment(String paramString, Bundle paramBundle, Fragment paramFragment, int paramInt)
  {
    startWithFragment(paramString, paramBundle, paramFragment, paramInt, 0, 0);
  }
  
  public void startWithFragment(String paramString, Bundle paramBundle, Fragment paramFragment, int paramInt1, int paramInt2, int paramInt3)
  {
    paramString = onBuildStartFragmentIntent(paramString, paramBundle, paramInt2, paramInt3);
    if (paramFragment == null)
    {
      startActivity(paramString);
      return;
    }
    paramFragment.startActivityForResult(paramString, paramInt1);
  }
  
  public void switchToHeader(Header paramHeader)
  {
    if (this.mCurHeader == paramHeader)
    {
      getFragmentManager().popBackStack(":android:prefs", 1);
      return;
    }
    if (paramHeader.fragment == null) {
      throw new IllegalStateException("can't switch to header that has no fragment");
    }
    switchToHeaderInner(paramHeader.fragment, paramHeader.fragmentArguments);
    setSelectedHeader(paramHeader);
  }
  
  public void switchToHeader(String paramString, Bundle paramBundle)
  {
    Object localObject2 = null;
    int i = 0;
    for (;;)
    {
      Object localObject1 = localObject2;
      if (i < this.mHeaders.size())
      {
        if (paramString.equals(((Header)this.mHeaders.get(i)).fragment)) {
          localObject1 = (Header)this.mHeaders.get(i);
        }
      }
      else
      {
        setSelectedHeader((Header)localObject1);
        switchToHeaderInner(paramString, paramBundle);
        return;
      }
      i += 1;
    }
  }
  
  public static final class Header
    implements Parcelable
  {
    public static final Parcelable.Creator<Header> CREATOR = new Parcelable.Creator()
    {
      public PreferenceActivity.Header createFromParcel(Parcel paramAnonymousParcel)
      {
        return new PreferenceActivity.Header(paramAnonymousParcel);
      }
      
      public PreferenceActivity.Header[] newArray(int paramAnonymousInt)
      {
        return new PreferenceActivity.Header[paramAnonymousInt];
      }
    };
    public CharSequence breadCrumbShortTitle;
    public int breadCrumbShortTitleRes;
    public CharSequence breadCrumbTitle;
    public int breadCrumbTitleRes;
    public Bundle extras;
    public String fragment;
    public Bundle fragmentArguments;
    public int iconRes;
    public long id = -1L;
    public Intent intent;
    public CharSequence summary;
    public int summaryRes;
    public CharSequence title;
    public int titleRes;
    
    public Header() {}
    
    Header(Parcel paramParcel)
    {
      readFromParcel(paramParcel);
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    public CharSequence getBreadCrumbShortTitle(Resources paramResources)
    {
      if (this.breadCrumbShortTitleRes != 0) {
        return paramResources.getText(this.breadCrumbShortTitleRes);
      }
      return this.breadCrumbShortTitle;
    }
    
    public CharSequence getBreadCrumbTitle(Resources paramResources)
    {
      if (this.breadCrumbTitleRes != 0) {
        return paramResources.getText(this.breadCrumbTitleRes);
      }
      return this.breadCrumbTitle;
    }
    
    public CharSequence getSummary(Resources paramResources)
    {
      if (this.summaryRes != 0) {
        return paramResources.getText(this.summaryRes);
      }
      return this.summary;
    }
    
    public CharSequence getTitle(Resources paramResources)
    {
      if (this.titleRes != 0) {
        return paramResources.getText(this.titleRes);
      }
      return this.title;
    }
    
    public void readFromParcel(Parcel paramParcel)
    {
      this.id = paramParcel.readLong();
      this.titleRes = paramParcel.readInt();
      this.title = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.summaryRes = paramParcel.readInt();
      this.summary = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.breadCrumbTitleRes = paramParcel.readInt();
      this.breadCrumbTitle = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.breadCrumbShortTitleRes = paramParcel.readInt();
      this.breadCrumbShortTitle = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
      this.iconRes = paramParcel.readInt();
      this.fragment = paramParcel.readString();
      this.fragmentArguments = paramParcel.readBundle();
      if (paramParcel.readInt() != 0) {
        this.intent = ((Intent)Intent.CREATOR.createFromParcel(paramParcel));
      }
      this.extras = paramParcel.readBundle();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.id);
      paramParcel.writeInt(this.titleRes);
      TextUtils.writeToParcel(this.title, paramParcel, paramInt);
      paramParcel.writeInt(this.summaryRes);
      TextUtils.writeToParcel(this.summary, paramParcel, paramInt);
      paramParcel.writeInt(this.breadCrumbTitleRes);
      TextUtils.writeToParcel(this.breadCrumbTitle, paramParcel, paramInt);
      paramParcel.writeInt(this.breadCrumbShortTitleRes);
      TextUtils.writeToParcel(this.breadCrumbShortTitle, paramParcel, paramInt);
      paramParcel.writeInt(this.iconRes);
      paramParcel.writeString(this.fragment);
      paramParcel.writeBundle(this.fragmentArguments);
      if (this.intent != null)
      {
        paramParcel.writeInt(1);
        this.intent.writeToParcel(paramParcel, paramInt);
      }
      for (;;)
      {
        paramParcel.writeBundle(this.extras);
        return;
        paramParcel.writeInt(0);
      }
    }
  }
  
  private static class HeaderAdapter
    extends ArrayAdapter<PreferenceActivity.Header>
  {
    private LayoutInflater mInflater;
    private int mLayoutResId;
    private boolean mRemoveIconIfEmpty;
    
    public HeaderAdapter(Context paramContext, List<PreferenceActivity.Header> paramList, int paramInt, boolean paramBoolean)
    {
      super(0, paramList);
      this.mInflater = ((LayoutInflater)paramContext.getSystemService("layout_inflater"));
      this.mLayoutResId = paramInt;
      this.mRemoveIconIfEmpty = paramBoolean;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      Object localObject;
      if (paramView == null)
      {
        paramViewGroup = this.mInflater.inflate(this.mLayoutResId, paramViewGroup, false);
        paramView = new HeaderViewHolder(null);
        paramView.icon = ((ImageView)paramViewGroup.findViewById(16908294));
        paramView.title = ((TextView)paramViewGroup.findViewById(16908310));
        paramView.summary = ((TextView)paramViewGroup.findViewById(16908304));
        paramViewGroup.setTag(paramView);
        localObject = (PreferenceActivity.Header)getItem(paramInt);
        if (!this.mRemoveIconIfEmpty) {
          break label201;
        }
        if (((PreferenceActivity.Header)localObject).iconRes != 0) {
          break label178;
        }
        paramView.icon.setVisibility(8);
      }
      for (;;)
      {
        paramView.title.setText(((PreferenceActivity.Header)localObject).getTitle(getContext().getResources()));
        localObject = ((PreferenceActivity.Header)localObject).getSummary(getContext().getResources());
        if (TextUtils.isEmpty((CharSequence)localObject)) {
          break label216;
        }
        paramView.summary.setVisibility(0);
        paramView.summary.setText((CharSequence)localObject);
        return paramViewGroup;
        paramViewGroup = paramView;
        paramView = (HeaderViewHolder)paramView.getTag();
        break;
        label178:
        paramView.icon.setVisibility(0);
        paramView.icon.setImageResource(((PreferenceActivity.Header)localObject).iconRes);
        continue;
        label201:
        paramView.icon.setImageResource(((PreferenceActivity.Header)localObject).iconRes);
      }
      label216:
      paramView.summary.setVisibility(8);
      return paramViewGroup;
    }
    
    private static class HeaderViewHolder
    {
      ImageView icon;
      TextView summary;
      TextView title;
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/preference/PreferenceActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */