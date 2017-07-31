package android.content;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.StrictMode;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;
import java.util.ArrayList;
import java.util.List;

public class ClipData
  implements Parcelable
{
  public static final Parcelable.Creator<ClipData> CREATOR = new Parcelable.Creator()
  {
    public ClipData createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ClipData(paramAnonymousParcel);
    }
    
    public ClipData[] newArray(int paramAnonymousInt)
    {
      return new ClipData[paramAnonymousInt];
    }
  };
  static final String[] MIMETYPES_TEXT_HTML;
  static final String[] MIMETYPES_TEXT_INTENT;
  static final String[] MIMETYPES_TEXT_PLAIN = { "text/plain" };
  static final String[] MIMETYPES_TEXT_URILIST;
  final ClipDescription mClipDescription;
  final Bitmap mIcon;
  final ArrayList<Item> mItems;
  
  static
  {
    MIMETYPES_TEXT_HTML = new String[] { "text/html" };
    MIMETYPES_TEXT_URILIST = new String[] { "text/uri-list" };
    MIMETYPES_TEXT_INTENT = new String[] { "text/vnd.android.intent" };
  }
  
  public ClipData(ClipData paramClipData)
  {
    this.mClipDescription = paramClipData.mClipDescription;
    this.mIcon = paramClipData.mIcon;
    this.mItems = new ArrayList(paramClipData.mItems);
  }
  
  public ClipData(ClipDescription paramClipDescription, Item paramItem)
  {
    this.mClipDescription = paramClipDescription;
    if (paramItem == null) {
      throw new NullPointerException("item is null");
    }
    this.mIcon = null;
    this.mItems = new ArrayList();
    this.mItems.add(paramItem);
  }
  
  ClipData(Parcel paramParcel)
  {
    this.mClipDescription = new ClipDescription(paramParcel);
    int i;
    label57:
    CharSequence localCharSequence;
    String str;
    Intent localIntent;
    if (paramParcel.readInt() != 0)
    {
      this.mIcon = ((Bitmap)Bitmap.CREATOR.createFromParcel(paramParcel));
      this.mItems = new ArrayList();
      int j = paramParcel.readInt();
      i = 0;
      if (i >= j) {
        return;
      }
      localCharSequence = (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel);
      str = paramParcel.readString();
      if (paramParcel.readInt() == 0) {
        break label162;
      }
      localIntent = (Intent)Intent.CREATOR.createFromParcel(paramParcel);
      label103:
      if (paramParcel.readInt() == 0) {
        break label168;
      }
    }
    label162:
    label168:
    for (Uri localUri = (Uri)Uri.CREATOR.createFromParcel(paramParcel);; localUri = null)
    {
      this.mItems.add(new Item(localCharSequence, str, localIntent, localUri));
      i += 1;
      break label57;
      this.mIcon = null;
      break;
      localIntent = null;
      break label103;
    }
  }
  
  public ClipData(CharSequence paramCharSequence, String[] paramArrayOfString, Item paramItem)
  {
    this.mClipDescription = new ClipDescription(paramCharSequence, paramArrayOfString);
    if (paramItem == null) {
      throw new NullPointerException("item is null");
    }
    this.mIcon = null;
    this.mItems = new ArrayList();
    this.mItems.add(paramItem);
  }
  
  public static ClipData newHtmlText(CharSequence paramCharSequence1, CharSequence paramCharSequence2, String paramString)
  {
    paramCharSequence2 = new Item(paramCharSequence2, paramString);
    return new ClipData(paramCharSequence1, MIMETYPES_TEXT_HTML, paramCharSequence2);
  }
  
  public static ClipData newIntent(CharSequence paramCharSequence, Intent paramIntent)
  {
    paramIntent = new Item(paramIntent);
    return new ClipData(paramCharSequence, MIMETYPES_TEXT_INTENT, paramIntent);
  }
  
  public static ClipData newPlainText(CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    paramCharSequence2 = new Item(paramCharSequence2);
    return new ClipData(paramCharSequence1, MIMETYPES_TEXT_PLAIN, paramCharSequence2);
  }
  
  public static ClipData newRawUri(CharSequence paramCharSequence, Uri paramUri)
  {
    paramUri = new Item(paramUri);
    return new ClipData(paramCharSequence, MIMETYPES_TEXT_URILIST, paramUri);
  }
  
  public static ClipData newUri(ContentResolver paramContentResolver, CharSequence paramCharSequence, Uri paramUri)
  {
    Item localItem = new Item(paramUri);
    Object localObject = null;
    String str;
    if ("content".equals(paramUri.getScheme()))
    {
      str = paramContentResolver.getType(paramUri);
      paramContentResolver = paramContentResolver.getStreamTypes(paramUri, "*/*");
      localObject = paramContentResolver;
      if (str != null)
      {
        if (paramContentResolver != null) {
          break label82;
        }
        localObject = new String[1];
        localObject[0] = str;
      }
    }
    for (;;)
    {
      paramContentResolver = (ContentResolver)localObject;
      if (localObject == null) {
        paramContentResolver = MIMETYPES_TEXT_URILIST;
      }
      return new ClipData(paramCharSequence, paramContentResolver, localItem);
      label82:
      localObject = new String[paramContentResolver.length + 1];
      localObject[0] = str;
      System.arraycopy(paramContentResolver, 0, localObject, 1, paramContentResolver.length);
    }
  }
  
  public void addItem(Item paramItem)
  {
    if (paramItem == null) {
      throw new NullPointerException("item is null");
    }
    this.mItems.add(paramItem);
  }
  
  public void collectUris(List<Uri> paramList)
  {
    int i = 0;
    while (i < this.mItems.size())
    {
      Object localObject = getItemAt(i);
      if (((Item)localObject).getUri() != null) {
        paramList.add(((Item)localObject).getUri());
      }
      localObject = ((Item)localObject).getIntent();
      if (localObject != null)
      {
        if (((Intent)localObject).getData() != null) {
          paramList.add(((Intent)localObject).getData());
        }
        if (((Intent)localObject).getClipData() != null) {
          ((Intent)localObject).getClipData().collectUris(paramList);
        }
      }
      i += 1;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public void fixUris(int paramInt)
  {
    int j = this.mItems.size();
    int i = 0;
    while (i < j)
    {
      Item localItem = (Item)this.mItems.get(i);
      if (localItem.mIntent != null) {
        localItem.mIntent.fixUris(paramInt);
      }
      if (localItem.mUri != null) {
        localItem.mUri = ContentProvider.maybeAddUserId(localItem.mUri, paramInt);
      }
      i += 1;
    }
  }
  
  public void fixUrisLight(int paramInt)
  {
    int j = this.mItems.size();
    int i = 0;
    while (i < j)
    {
      Item localItem = (Item)this.mItems.get(i);
      if (localItem.mIntent != null)
      {
        Uri localUri = localItem.mIntent.getData();
        if (localUri != null) {
          localItem.mIntent.setData(ContentProvider.maybeAddUserId(localUri, paramInt));
        }
      }
      if (localItem.mUri != null) {
        localItem.mUri = ContentProvider.maybeAddUserId(localItem.mUri, paramInt);
      }
      i += 1;
    }
  }
  
  public ClipDescription getDescription()
  {
    return this.mClipDescription;
  }
  
  public Bitmap getIcon()
  {
    return this.mIcon;
  }
  
  public Item getItemAt(int paramInt)
  {
    return (Item)this.mItems.get(paramInt);
  }
  
  public int getItemCount()
  {
    return this.mItems.size();
  }
  
  public void prepareToEnterProcess()
  {
    int j = this.mItems.size();
    int i = 0;
    while (i < j)
    {
      Item localItem = (Item)this.mItems.get(i);
      if (localItem.mIntent != null) {
        localItem.mIntent.prepareToEnterProcess();
      }
      i += 1;
    }
  }
  
  public void prepareToLeaveProcess(boolean paramBoolean)
  {
    int j = this.mItems.size();
    int i = 0;
    while (i < j)
    {
      Item localItem = (Item)this.mItems.get(i);
      if (localItem.mIntent != null) {
        localItem.mIntent.prepareToLeaveProcess(paramBoolean);
      }
      if ((localItem.mUri != null) && (StrictMode.vmFileUriExposureEnabled()) && (paramBoolean)) {
        localItem.mUri.checkFileUriExposed("ClipData.Item.getUri()");
      }
      i += 1;
    }
  }
  
  public void setItemAt(int paramInt, Item paramItem)
  {
    this.mItems.set(paramInt, paramItem);
  }
  
  public void toShortString(StringBuilder paramStringBuilder)
  {
    int i;
    if (this.mClipDescription != null) {
      if (this.mClipDescription.toShortString(paramStringBuilder)) {
        i = 0;
      }
    }
    for (;;)
    {
      int j = i;
      if (this.mIcon != null)
      {
        if (i == 0) {
          paramStringBuilder.append(' ');
        }
        j = 0;
        paramStringBuilder.append("I:");
        paramStringBuilder.append(this.mIcon.getWidth());
        paramStringBuilder.append('x');
        paramStringBuilder.append(this.mIcon.getHeight());
      }
      i = 0;
      while (i < this.mItems.size())
      {
        if (j == 0) {
          paramStringBuilder.append(' ');
        }
        j = 0;
        paramStringBuilder.append('{');
        ((Item)this.mItems.get(i)).toShortString(paramStringBuilder);
        paramStringBuilder.append('}');
        i += 1;
      }
      i = 1;
      continue;
      i = 1;
    }
  }
  
  public void toShortStringShortItems(StringBuilder paramStringBuilder, boolean paramBoolean)
  {
    if (this.mItems.size() > 0)
    {
      if (!paramBoolean) {
        paramStringBuilder.append(' ');
      }
      ((Item)this.mItems.get(0)).toShortString(paramStringBuilder);
      if (this.mItems.size() > 1) {
        paramStringBuilder.append(" ...");
      }
    }
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder(128);
    localStringBuilder.append("ClipData { ");
    toShortString(localStringBuilder);
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    this.mClipDescription.writeToParcel(paramParcel, paramInt);
    int i;
    if (this.mIcon != null)
    {
      paramParcel.writeInt(1);
      this.mIcon.writeToParcel(paramParcel, paramInt);
      int j = this.mItems.size();
      paramParcel.writeInt(j);
      i = 0;
      label47:
      if (i >= j) {
        return;
      }
      Item localItem = (Item)this.mItems.get(i);
      TextUtils.writeToParcel(localItem.mText, paramParcel, paramInt);
      paramParcel.writeString(localItem.mHtmlText);
      if (localItem.mIntent == null) {
        break label146;
      }
      paramParcel.writeInt(1);
      localItem.mIntent.writeToParcel(paramParcel, paramInt);
      label108:
      if (localItem.mUri == null) {
        break label154;
      }
      paramParcel.writeInt(1);
      localItem.mUri.writeToParcel(paramParcel, paramInt);
    }
    for (;;)
    {
      i += 1;
      break label47;
      paramParcel.writeInt(0);
      break;
      label146:
      paramParcel.writeInt(0);
      break label108;
      label154:
      paramParcel.writeInt(0);
    }
  }
  
  public static class Item
  {
    final String mHtmlText;
    final Intent mIntent;
    final CharSequence mText;
    Uri mUri;
    
    public Item(Item paramItem)
    {
      this.mText = paramItem.mText;
      this.mHtmlText = paramItem.mHtmlText;
      this.mIntent = paramItem.mIntent;
      this.mUri = paramItem.mUri;
    }
    
    public Item(Intent paramIntent)
    {
      this.mText = null;
      this.mHtmlText = null;
      this.mIntent = paramIntent;
      this.mUri = null;
    }
    
    public Item(Uri paramUri)
    {
      this.mText = null;
      this.mHtmlText = null;
      this.mIntent = null;
      this.mUri = paramUri;
    }
    
    public Item(CharSequence paramCharSequence)
    {
      this.mText = paramCharSequence;
      this.mHtmlText = null;
      this.mIntent = null;
      this.mUri = null;
    }
    
    public Item(CharSequence paramCharSequence, Intent paramIntent, Uri paramUri)
    {
      this.mText = paramCharSequence;
      this.mHtmlText = null;
      this.mIntent = paramIntent;
      this.mUri = paramUri;
    }
    
    public Item(CharSequence paramCharSequence, String paramString)
    {
      this.mText = paramCharSequence;
      this.mHtmlText = paramString;
      this.mIntent = null;
      this.mUri = null;
    }
    
    public Item(CharSequence paramCharSequence, String paramString, Intent paramIntent, Uri paramUri)
    {
      if ((paramString != null) && (paramCharSequence == null)) {
        throw new IllegalArgumentException("Plain text must be supplied if HTML text is supplied");
      }
      this.mText = paramCharSequence;
      this.mHtmlText = paramString;
      this.mIntent = paramIntent;
      this.mUri = paramUri;
    }
    
    /* Error */
    private CharSequence coerceToHtmlOrStyledText(Context paramContext, boolean paramBoolean)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 28	android/content/ClipData$Item:mUri	Landroid/net/Uri;
      //   4: ifnull +585 -> 589
      //   7: aconst_null
      //   8: astore 9
      //   10: aload_1
      //   11: invokevirtual 58	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   14: aload_0
      //   15: getfield 28	android/content/ClipData$Item:mUri	Landroid/net/Uri;
      //   18: ldc 60
      //   20: invokevirtual 66	android/content/ContentResolver:getStreamTypes	(Landroid/net/Uri;Ljava/lang/String;)[Ljava/lang/String;
      //   23: astore 10
      //   25: aload 10
      //   27: astore 9
      //   29: iconst_0
      //   30: istore 6
      //   32: iconst_0
      //   33: istore_3
      //   34: iconst_0
      //   35: istore 7
      //   37: iconst_0
      //   38: istore 4
      //   40: aload 9
      //   42: ifnull +79 -> 121
      //   45: iconst_0
      //   46: istore 5
      //   48: aload 9
      //   50: arraylength
      //   51: istore 8
      //   53: iload_3
      //   54: istore 6
      //   56: iload 4
      //   58: istore 7
      //   60: iload 5
      //   62: iload 8
      //   64: if_icmpge +57 -> 121
      //   67: aload 9
      //   69: iload 5
      //   71: aaload
      //   72: astore 10
      //   74: ldc 68
      //   76: aload 10
      //   78: invokevirtual 74	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   81: ifeq +18 -> 99
      //   84: iconst_1
      //   85: istore 6
      //   87: iload 5
      //   89: iconst_1
      //   90: iadd
      //   91: istore 5
      //   93: iload 6
      //   95: istore_3
      //   96: goto -43 -> 53
      //   99: iload_3
      //   100: istore 6
      //   102: aload 10
      //   104: ldc 76
      //   106: invokevirtual 80	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   109: ifeq -22 -> 87
      //   112: iconst_1
      //   113: istore 4
      //   115: iload_3
      //   116: istore 6
      //   118: goto -31 -> 87
      //   121: iload 6
      //   123: ifne +8 -> 131
      //   126: iload 7
      //   128: ifeq +193 -> 321
      //   131: aconst_null
      //   132: astore 13
      //   134: aconst_null
      //   135: astore 14
      //   137: aconst_null
      //   138: astore 12
      //   140: aload 12
      //   142: astore 11
      //   144: aload 13
      //   146: astore 10
      //   148: aload 14
      //   150: astore 9
      //   152: aload_1
      //   153: invokevirtual 58	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   156: astore 15
      //   158: aload 12
      //   160: astore 11
      //   162: aload 13
      //   164: astore 10
      //   166: aload 14
      //   168: astore 9
      //   170: aload_0
      //   171: getfield 28	android/content/ClipData$Item:mUri	Landroid/net/Uri;
      //   174: astore 16
      //   176: iload 6
      //   178: ifeq +159 -> 337
      //   181: ldc 68
      //   183: astore_1
      //   184: aload 12
      //   186: astore 11
      //   188: aload 13
      //   190: astore 10
      //   192: aload 14
      //   194: astore 9
      //   196: aload 15
      //   198: aload 16
      //   200: aload_1
      //   201: aconst_null
      //   202: invokevirtual 84	android/content/ContentResolver:openTypedAssetFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;Landroid/os/Bundle;)Landroid/content/res/AssetFileDescriptor;
      //   205: invokevirtual 90	android/content/res/AssetFileDescriptor:createInputStream	()Ljava/io/FileInputStream;
      //   208: astore_1
      //   209: aload_1
      //   210: astore 11
      //   212: aload_1
      //   213: astore 10
      //   215: aload_1
      //   216: astore 9
      //   218: new 92	java/io/InputStreamReader
      //   221: dup
      //   222: aload_1
      //   223: ldc 94
      //   225: invokespecial 97	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
      //   228: astore 12
      //   230: aload_1
      //   231: astore 11
      //   233: aload_1
      //   234: astore 10
      //   236: aload_1
      //   237: astore 9
      //   239: new 99	java/lang/StringBuilder
      //   242: dup
      //   243: sipush 128
      //   246: invokespecial 102	java/lang/StringBuilder:<init>	(I)V
      //   249: astore 13
      //   251: aload_1
      //   252: astore 11
      //   254: aload_1
      //   255: astore 10
      //   257: aload_1
      //   258: astore 9
      //   260: sipush 8192
      //   263: newarray <illegal type>
      //   265: astore 14
      //   267: aload_1
      //   268: astore 11
      //   270: aload_1
      //   271: astore 10
      //   273: aload_1
      //   274: astore 9
      //   276: aload 12
      //   278: aload 14
      //   280: invokevirtual 106	java/io/InputStreamReader:read	([C)I
      //   283: istore_3
      //   284: iload_3
      //   285: ifle +58 -> 343
      //   288: aload_1
      //   289: astore 11
      //   291: aload_1
      //   292: astore 10
      //   294: aload_1
      //   295: astore 9
      //   297: aload 13
      //   299: aload 14
      //   301: iconst_0
      //   302: iload_3
      //   303: invokevirtual 110	java/lang/StringBuilder:append	([CII)Ljava/lang/StringBuilder;
      //   306: pop
      //   307: goto -40 -> 267
      //   310: astore_1
      //   311: aload 11
      //   313: ifnull +8 -> 321
      //   316: aload 11
      //   318: invokevirtual 115	java/io/FileInputStream:close	()V
      //   321: iload_2
      //   322: ifeq +255 -> 577
      //   325: aload_0
      //   326: aload_0
      //   327: getfield 28	android/content/ClipData$Item:mUri	Landroid/net/Uri;
      //   330: invokevirtual 121	android/net/Uri:toString	()Ljava/lang/String;
      //   333: invokespecial 125	android/content/ClipData$Item:uriToStyledText	(Ljava/lang/String;)Ljava/lang/CharSequence;
      //   336: areturn
      //   337: ldc 127
      //   339: astore_1
      //   340: goto -156 -> 184
      //   343: aload_1
      //   344: astore 11
      //   346: aload_1
      //   347: astore 10
      //   349: aload_1
      //   350: astore 9
      //   352: aload 13
      //   354: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   357: astore 12
      //   359: iload 6
      //   361: ifeq +102 -> 463
      //   364: iload_2
      //   365: ifeq +67 -> 432
      //   368: aload_1
      //   369: astore 11
      //   371: aload_1
      //   372: astore 10
      //   374: aload_1
      //   375: astore 9
      //   377: aload 12
      //   379: invokestatic 134	android/text/Html:fromHtml	(Ljava/lang/String;)Landroid/text/Spanned;
      //   382: astore 13
      //   384: aload 13
      //   386: astore 9
      //   388: aload 9
      //   390: ifnull +14 -> 404
      //   393: aload_1
      //   394: ifnull +7 -> 401
      //   397: aload_1
      //   398: invokevirtual 115	java/io/FileInputStream:close	()V
      //   401: aload 9
      //   403: areturn
      //   404: aload 12
      //   406: astore 9
      //   408: goto -15 -> 393
      //   411: astore_1
      //   412: aload 9
      //   414: areturn
      //   415: astore 9
      //   417: aload_1
      //   418: ifnull +7 -> 425
      //   421: aload_1
      //   422: invokevirtual 115	java/io/FileInputStream:close	()V
      //   425: aload 12
      //   427: areturn
      //   428: astore_1
      //   429: aload 12
      //   431: areturn
      //   432: aload_1
      //   433: astore 11
      //   435: aload_1
      //   436: astore 10
      //   438: aload_1
      //   439: astore 9
      //   441: aload 12
      //   443: invokevirtual 135	java/lang/String:toString	()Ljava/lang/String;
      //   446: astore 12
      //   448: aload_1
      //   449: ifnull +7 -> 456
      //   452: aload_1
      //   453: invokevirtual 115	java/io/FileInputStream:close	()V
      //   456: aload 12
      //   458: areturn
      //   459: astore_1
      //   460: aload 12
      //   462: areturn
      //   463: iload_2
      //   464: ifeq +18 -> 482
      //   467: aload_1
      //   468: ifnull +7 -> 475
      //   471: aload_1
      //   472: invokevirtual 115	java/io/FileInputStream:close	()V
      //   475: aload 12
      //   477: areturn
      //   478: astore_1
      //   479: aload 12
      //   481: areturn
      //   482: aload_1
      //   483: astore 11
      //   485: aload_1
      //   486: astore 10
      //   488: aload_1
      //   489: astore 9
      //   491: aload 12
      //   493: invokestatic 139	android/text/Html:escapeHtml	(Ljava/lang/CharSequence;)Ljava/lang/String;
      //   496: astore 12
      //   498: aload_1
      //   499: ifnull +7 -> 506
      //   502: aload_1
      //   503: invokevirtual 115	java/io/FileInputStream:close	()V
      //   506: aload 12
      //   508: areturn
      //   509: astore_1
      //   510: aload 12
      //   512: areturn
      //   513: astore_1
      //   514: aload 10
      //   516: astore 9
      //   518: ldc -115
      //   520: ldc -113
      //   522: aload_1
      //   523: invokestatic 149	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   526: pop
      //   527: aload 10
      //   529: astore 9
      //   531: aload_1
      //   532: invokevirtual 150	java/io/IOException:toString	()Ljava/lang/String;
      //   535: invokestatic 139	android/text/Html:escapeHtml	(Ljava/lang/CharSequence;)Ljava/lang/String;
      //   538: astore_1
      //   539: aload 10
      //   541: ifnull +8 -> 549
      //   544: aload 10
      //   546: invokevirtual 115	java/io/FileInputStream:close	()V
      //   549: aload_1
      //   550: areturn
      //   551: astore 9
      //   553: aload_1
      //   554: areturn
      //   555: astore_1
      //   556: goto -235 -> 321
      //   559: astore_1
      //   560: aload 9
      //   562: ifnull +8 -> 570
      //   565: aload 9
      //   567: invokevirtual 115	java/io/FileInputStream:close	()V
      //   570: aload_1
      //   571: athrow
      //   572: astore 9
      //   574: goto -4 -> 570
      //   577: aload_0
      //   578: aload_0
      //   579: getfield 28	android/content/ClipData$Item:mUri	Landroid/net/Uri;
      //   582: invokevirtual 121	android/net/Uri:toString	()Ljava/lang/String;
      //   585: invokespecial 154	android/content/ClipData$Item:uriToHtml	(Ljava/lang/String;)Ljava/lang/String;
      //   588: areturn
      //   589: aload_0
      //   590: getfield 26	android/content/ClipData$Item:mIntent	Landroid/content/Intent;
      //   593: ifnull +33 -> 626
      //   596: iload_2
      //   597: ifeq +16 -> 613
      //   600: aload_0
      //   601: aload_0
      //   602: getfield 26	android/content/ClipData$Item:mIntent	Landroid/content/Intent;
      //   605: iconst_1
      //   606: invokevirtual 160	android/content/Intent:toUri	(I)Ljava/lang/String;
      //   609: invokespecial 125	android/content/ClipData$Item:uriToStyledText	(Ljava/lang/String;)Ljava/lang/CharSequence;
      //   612: areturn
      //   613: aload_0
      //   614: aload_0
      //   615: getfield 26	android/content/ClipData$Item:mIntent	Landroid/content/Intent;
      //   618: iconst_1
      //   619: invokevirtual 160	android/content/Intent:toUri	(I)Ljava/lang/String;
      //   622: invokespecial 154	android/content/ClipData$Item:uriToHtml	(Ljava/lang/String;)Ljava/lang/String;
      //   625: areturn
      //   626: ldc -94
      //   628: areturn
      //   629: astore 10
      //   631: goto -602 -> 29
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	634	0	this	Item
      //   0	634	1	paramContext	Context
      //   0	634	2	paramBoolean	boolean
      //   33	270	3	i	int
      //   38	76	4	j	int
      //   46	46	5	k	int
      //   30	330	6	m	int
      //   35	92	7	n	int
      //   51	14	8	i1	int
      //   8	405	9	localObject1	Object
      //   415	1	9	localRuntimeException	RuntimeException
      //   439	91	9	localObject2	Object
      //   551	15	9	localIOException1	java.io.IOException
      //   572	1	9	localIOException2	java.io.IOException
      //   23	522	10	localObject3	Object
      //   629	1	10	localSecurityException	SecurityException
      //   142	342	11	localObject4	Object
      //   138	373	12	localObject5	Object
      //   132	253	13	localObject6	Object
      //   135	165	14	arrayOfChar	char[]
      //   156	41	15	localContentResolver	ContentResolver
      //   174	25	16	localUri	Uri
      // Exception table:
      //   from	to	target	type
      //   152	158	310	java/io/FileNotFoundException
      //   170	176	310	java/io/FileNotFoundException
      //   196	209	310	java/io/FileNotFoundException
      //   218	230	310	java/io/FileNotFoundException
      //   239	251	310	java/io/FileNotFoundException
      //   260	267	310	java/io/FileNotFoundException
      //   276	284	310	java/io/FileNotFoundException
      //   297	307	310	java/io/FileNotFoundException
      //   352	359	310	java/io/FileNotFoundException
      //   377	384	310	java/io/FileNotFoundException
      //   441	448	310	java/io/FileNotFoundException
      //   491	498	310	java/io/FileNotFoundException
      //   397	401	411	java/io/IOException
      //   377	384	415	java/lang/RuntimeException
      //   421	425	428	java/io/IOException
      //   452	456	459	java/io/IOException
      //   471	475	478	java/io/IOException
      //   502	506	509	java/io/IOException
      //   152	158	513	java/io/IOException
      //   170	176	513	java/io/IOException
      //   196	209	513	java/io/IOException
      //   218	230	513	java/io/IOException
      //   239	251	513	java/io/IOException
      //   260	267	513	java/io/IOException
      //   276	284	513	java/io/IOException
      //   297	307	513	java/io/IOException
      //   352	359	513	java/io/IOException
      //   377	384	513	java/io/IOException
      //   441	448	513	java/io/IOException
      //   491	498	513	java/io/IOException
      //   544	549	551	java/io/IOException
      //   316	321	555	java/io/IOException
      //   152	158	559	finally
      //   170	176	559	finally
      //   196	209	559	finally
      //   218	230	559	finally
      //   239	251	559	finally
      //   260	267	559	finally
      //   276	284	559	finally
      //   297	307	559	finally
      //   352	359	559	finally
      //   377	384	559	finally
      //   441	448	559	finally
      //   491	498	559	finally
      //   518	527	559	finally
      //   531	539	559	finally
      //   565	570	572	java/io/IOException
      //   10	25	629	java/lang/SecurityException
    }
    
    private String uriToHtml(String paramString)
    {
      StringBuilder localStringBuilder = new StringBuilder(256);
      localStringBuilder.append("<a href=\"");
      localStringBuilder.append(Html.escapeHtml(paramString));
      localStringBuilder.append("\">");
      localStringBuilder.append(Html.escapeHtml(paramString));
      localStringBuilder.append("</a>");
      return localStringBuilder.toString();
    }
    
    private CharSequence uriToStyledText(String paramString)
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
      localSpannableStringBuilder.append(paramString);
      localSpannableStringBuilder.setSpan(new URLSpan(paramString), 0, localSpannableStringBuilder.length(), 33);
      return localSpannableStringBuilder;
    }
    
    public String coerceToHtmlText(Context paramContext)
    {
      Object localObject1 = null;
      Object localObject2 = getHtmlText();
      if (localObject2 != null) {
        return (String)localObject2;
      }
      localObject2 = getText();
      if (localObject2 != null)
      {
        if ((localObject2 instanceof Spanned)) {
          return Html.toHtml((Spanned)localObject2);
        }
        return Html.escapeHtml((CharSequence)localObject2);
      }
      localObject2 = coerceToHtmlOrStyledText(paramContext, false);
      paramContext = (Context)localObject1;
      if (localObject2 != null) {
        paramContext = ((CharSequence)localObject2).toString();
      }
      return paramContext;
    }
    
    public CharSequence coerceToStyledText(Context paramContext)
    {
      CharSequence localCharSequence = getText();
      if ((localCharSequence instanceof Spanned)) {
        return localCharSequence;
      }
      Object localObject = getHtmlText();
      if (localObject != null) {
        try
        {
          localObject = Html.fromHtml((String)localObject);
          if (localObject != null) {
            return (CharSequence)localObject;
          }
        }
        catch (RuntimeException localRuntimeException) {}
      }
      if (localCharSequence != null) {
        return localCharSequence;
      }
      return coerceToHtmlOrStyledText(paramContext, true);
    }
    
    /* Error */
    public CharSequence coerceToText(Context paramContext)
    {
      // Byte code:
      //   0: aload_0
      //   1: invokevirtual 197	android/content/ClipData$Item:getText	()Ljava/lang/CharSequence;
      //   4: astore_3
      //   5: aload_3
      //   6: ifnull +5 -> 11
      //   9: aload_3
      //   10: areturn
      //   11: aload_0
      //   12: invokevirtual 215	android/content/ClipData$Item:getUri	()Landroid/net/Uri;
      //   15: astore 6
      //   17: aload 6
      //   19: ifnull +225 -> 244
      //   22: aconst_null
      //   23: astore_3
      //   24: aconst_null
      //   25: astore 4
      //   27: aconst_null
      //   28: astore 5
      //   30: aload_1
      //   31: invokevirtual 58	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   34: aload 6
      //   36: ldc 60
      //   38: aconst_null
      //   39: invokevirtual 84	android/content/ContentResolver:openTypedAssetFileDescriptor	(Landroid/net/Uri;Ljava/lang/String;Landroid/os/Bundle;)Landroid/content/res/AssetFileDescriptor;
      //   42: invokevirtual 90	android/content/res/AssetFileDescriptor:createInputStream	()Ljava/io/FileInputStream;
      //   45: astore_1
      //   46: aload_1
      //   47: astore 5
      //   49: aload_1
      //   50: astore_3
      //   51: aload_1
      //   52: astore 4
      //   54: new 92	java/io/InputStreamReader
      //   57: dup
      //   58: aload_1
      //   59: ldc 94
      //   61: invokespecial 97	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;Ljava/lang/String;)V
      //   64: astore 7
      //   66: aload_1
      //   67: astore 5
      //   69: aload_1
      //   70: astore_3
      //   71: aload_1
      //   72: astore 4
      //   74: new 99	java/lang/StringBuilder
      //   77: dup
      //   78: sipush 128
      //   81: invokespecial 102	java/lang/StringBuilder:<init>	(I)V
      //   84: astore 8
      //   86: aload_1
      //   87: astore 5
      //   89: aload_1
      //   90: astore_3
      //   91: aload_1
      //   92: astore 4
      //   94: sipush 8192
      //   97: newarray <illegal type>
      //   99: astore 9
      //   101: aload_1
      //   102: astore 5
      //   104: aload_1
      //   105: astore_3
      //   106: aload_1
      //   107: astore 4
      //   109: aload 7
      //   111: aload 9
      //   113: invokevirtual 106	java/io/InputStreamReader:read	([C)I
      //   116: istore_2
      //   117: iload_2
      //   118: ifle +41 -> 159
      //   121: aload_1
      //   122: astore 5
      //   124: aload_1
      //   125: astore_3
      //   126: aload_1
      //   127: astore 4
      //   129: aload 8
      //   131: aload 9
      //   133: iconst_0
      //   134: iload_2
      //   135: invokevirtual 110	java/lang/StringBuilder:append	([CII)Ljava/lang/StringBuilder;
      //   138: pop
      //   139: goto -38 -> 101
      //   142: astore_1
      //   143: aload 5
      //   145: ifnull +8 -> 153
      //   148: aload 5
      //   150: invokevirtual 115	java/io/FileInputStream:close	()V
      //   153: aload 6
      //   155: invokevirtual 121	android/net/Uri:toString	()Ljava/lang/String;
      //   158: areturn
      //   159: aload_1
      //   160: astore 5
      //   162: aload_1
      //   163: astore_3
      //   164: aload_1
      //   165: astore 4
      //   167: aload 8
      //   169: invokevirtual 128	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   172: astore 7
      //   174: aload_1
      //   175: ifnull +7 -> 182
      //   178: aload_1
      //   179: invokevirtual 115	java/io/FileInputStream:close	()V
      //   182: aload 7
      //   184: areturn
      //   185: astore_1
      //   186: aload 7
      //   188: areturn
      //   189: astore_1
      //   190: aload_3
      //   191: astore 4
      //   193: ldc -115
      //   195: ldc -113
      //   197: aload_1
      //   198: invokestatic 149	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
      //   201: pop
      //   202: aload_3
      //   203: astore 4
      //   205: aload_1
      //   206: invokevirtual 150	java/io/IOException:toString	()Ljava/lang/String;
      //   209: astore_1
      //   210: aload_3
      //   211: ifnull +7 -> 218
      //   214: aload_3
      //   215: invokevirtual 115	java/io/FileInputStream:close	()V
      //   218: aload_1
      //   219: areturn
      //   220: astore_3
      //   221: aload_1
      //   222: areturn
      //   223: astore_1
      //   224: goto -71 -> 153
      //   227: astore_1
      //   228: aload 4
      //   230: ifnull +8 -> 238
      //   233: aload 4
      //   235: invokevirtual 115	java/io/FileInputStream:close	()V
      //   238: aload_1
      //   239: athrow
      //   240: astore_3
      //   241: goto -3 -> 238
      //   244: aload_0
      //   245: invokevirtual 219	android/content/ClipData$Item:getIntent	()Landroid/content/Intent;
      //   248: astore_1
      //   249: aload_1
      //   250: ifnull +9 -> 259
      //   253: aload_1
      //   254: iconst_1
      //   255: invokevirtual 160	android/content/Intent:toUri	(I)Ljava/lang/String;
      //   258: areturn
      //   259: ldc -94
      //   261: areturn
      // Local variable table:
      //   start	length	slot	name	signature
      //   0	262	0	this	Item
      //   0	262	1	paramContext	Context
      //   116	19	2	i	int
      //   4	211	3	localObject1	Object
      //   220	1	3	localIOException1	java.io.IOException
      //   240	1	3	localIOException2	java.io.IOException
      //   25	209	4	localObject2	Object
      //   28	133	5	localContext	Context
      //   15	139	6	localUri	Uri
      //   64	123	7	localObject3	Object
      //   84	84	8	localStringBuilder	StringBuilder
      //   99	33	9	arrayOfChar	char[]
      // Exception table:
      //   from	to	target	type
      //   30	46	142	java/io/FileNotFoundException
      //   54	66	142	java/io/FileNotFoundException
      //   74	86	142	java/io/FileNotFoundException
      //   94	101	142	java/io/FileNotFoundException
      //   109	117	142	java/io/FileNotFoundException
      //   129	139	142	java/io/FileNotFoundException
      //   167	174	142	java/io/FileNotFoundException
      //   178	182	185	java/io/IOException
      //   30	46	189	java/io/IOException
      //   54	66	189	java/io/IOException
      //   74	86	189	java/io/IOException
      //   94	101	189	java/io/IOException
      //   109	117	189	java/io/IOException
      //   129	139	189	java/io/IOException
      //   167	174	189	java/io/IOException
      //   214	218	220	java/io/IOException
      //   148	153	223	java/io/IOException
      //   30	46	227	finally
      //   54	66	227	finally
      //   74	86	227	finally
      //   94	101	227	finally
      //   109	117	227	finally
      //   129	139	227	finally
      //   167	174	227	finally
      //   193	202	227	finally
      //   205	210	227	finally
      //   233	238	240	java/io/IOException
    }
    
    public String getHtmlText()
    {
      return this.mHtmlText;
    }
    
    public Intent getIntent()
    {
      return this.mIntent;
    }
    
    public CharSequence getText()
    {
      return this.mText;
    }
    
    public Uri getUri()
    {
      return this.mUri;
    }
    
    public void toShortString(StringBuilder paramStringBuilder)
    {
      if (this.mHtmlText != null)
      {
        paramStringBuilder.append("H:");
        paramStringBuilder.append(this.mHtmlText);
        return;
      }
      if (this.mText != null)
      {
        paramStringBuilder.append("T:");
        paramStringBuilder.append(this.mText);
        return;
      }
      if (this.mUri != null)
      {
        paramStringBuilder.append("U:");
        paramStringBuilder.append(this.mUri);
        return;
      }
      if (this.mIntent != null)
      {
        paramStringBuilder.append("I:");
        this.mIntent.toShortString(paramStringBuilder, true, true, true, true);
        return;
      }
      paramStringBuilder.append("NULL");
    }
    
    public void toShortSummaryString(StringBuilder paramStringBuilder)
    {
      if (this.mHtmlText != null)
      {
        paramStringBuilder.append("HTML");
        return;
      }
      if (this.mText != null)
      {
        paramStringBuilder.append("TEXT");
        return;
      }
      if (this.mUri != null)
      {
        paramStringBuilder.append("U:");
        paramStringBuilder.append(this.mUri);
        return;
      }
      if (this.mIntent != null)
      {
        paramStringBuilder.append("I:");
        this.mIntent.toShortString(paramStringBuilder, true, true, true, true);
        return;
      }
      paramStringBuilder.append("NULL");
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(128);
      localStringBuilder.append("ClipData.Item { ");
      toShortString(localStringBuilder);
      localStringBuilder.append(" }");
      return localStringBuilder.toString();
    }
  }
}


/* Location:              /Users/joshua/Desktop/system_framework/classes-dex2jar.jar!/android/content/ClipData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */