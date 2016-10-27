package zomifi.op27no2.socialbranding;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.clover.sdk.util.Platform;
import com.clover.sdk.v1.printer.ReceiptContract;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ReceiptRegistrationProvider2 extends ContentProvider {
    public static final String AUTHORITY = "com.clover.example.receipteditexample2";
    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    public static final String CONTENT_DIRECTORY_TEXT = "text";
    public static final Uri CONTENT_URI_TEXT = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_DIRECTORY_TEXT);

    public static final String CONTENT_DIRECTORY_IMAGE = "image";
    public static final Uri CONTENT_URI_IMAGE = Uri.withAppendedPath(AUTHORITY_URI, CONTENT_DIRECTORY_IMAGE);

    private static final int TEXT = 0;
    private static final int IMAGE = 1;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static String addOnText = "THIS IS MY  TEXT";

    private static String merchantID = "default";
    private Bitmap b;
    private static int pixelwidth = 384;
    private static Context mmContext;
    private static SharedPreferences prefs;
    private static SharedPreferences.Editor edt;
    private Bitmap rows;
    private static Boolean isBig;
    private static int ourWidth;
    private static int ourWidthLong;

    private ArrayList<Bitmap> bitlist = new ArrayList();
    private ArrayList<String> printStrings = new ArrayList();

    private String myString;
    private float twidth = 250;

    final static Target mtarget = new Target(){
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            System.out.println("bitmap presave ");
            saveImage(mmContext, bitmap, "logo", "one");
            System.out.println("bitmap postsave ");
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            System.out.println("bitmap fail ");

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            System.out.println("bitmap prepare ");

        }
    };
    final static Target mtarget2 = new Target(){
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            System.out.println("bitmap presave ");
            saveImage(mmContext, bitmap, "logo2", "one");
            System.out.println("bitmap postsave ");
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            System.out.println("bitmap fail ");

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            System.out.println("bitmap prepare ");

        }
    };
    static {
        uriMatcher.addURI(AUTHORITY, CONTENT_DIRECTORY_TEXT, TEXT);
        uriMatcher.addURI(AUTHORITY, CONTENT_DIRECTORY_IMAGE, IMAGE);
    }

    @Override
    public boolean onCreate() {



        return true;
    }

    public static void saveImage(Context context, Bitmap b,String name,String extension){
        name=name+"."+extension;
        FileOutputStream out;
        try {
            System.out.println("save ");
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("save error");
        }
    }

    public Bitmap getImageBitmap(Context context,String name,String extension){
        name=name+"."+extension;
        try{
            System.out.println("get ");
            FileInputStream fis = context.openFileInput(name);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        }
        catch(Exception e){
            System.out.println("get error");
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings2, String s2) {
        switch (uriMatcher.match(uri)) {
            case TEXT:
                MatrixCursor cursor = new MatrixCursor(new String[]{ReceiptContract.Text._ID, ReceiptContract.Text.TEXT});

                cursor.addRow(new Object[]{Integer.valueOf(0), addOnText});
                return cursor;

            default:
                throw new IllegalArgumentException("unknown uri: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TEXT:
                return ReceiptContract.Text.CONTENT_TYPE;
            case IMAGE:
                return ReceiptContract.Image.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new UnsupportedOperationException();
    }

    public static void passMercID(String mercID, Context context){
      //  merchantID = mercID;

        mmContext = context;
        prefs = mmContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        edt = mmContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();

      //  int coeff = prefs.getInt("scalepercent",100);
        if(Platform.isCloverMobile() || Platform.isCloverMini()){
            pixelwidth = 384;
            ourWidth = 230;
            ourWidthLong = 250;
            isBig = false;
        }
        else if(Platform.isCloverStation()){
            pixelwidth = 576;
            ourWidth = 350;
            ourWidthLong = 375;
            isBig = true;
        }

      //  pixelwidth = (int) Math.floor(pixelwidth*.01*coeff);
        System.out.println("ADJUSTED PIXELWIDTH");
        // setURL(context);



    }


    public static void setURL(Context context){
        final Context mContext = context;

        Picasso.with(mContext).load(R.drawable.testlogo)
                        .resize((pixelwidth/2), 0)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(mtarget);
        Picasso.with(mContext).load(R.drawable.zoomifi)
                .resize((pixelwidth/2), 0)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(mtarget2);
                /*.into(new Target() {
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                    *//* Save the bitmap or do something with it here *//*
                        System.out.println("bitmap presave ");
                        saveImage(mContext, bitmap, "logo", "one");
                        System.out.println("bitmap postsave ");
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        System.out.println("bitmap fail ");

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });*/

    }

    public Bitmap mergeBitmapH(Bitmap fr, Bitmap sc)
    {

        Bitmap comboBitmap;

        int width, height;

        width = pixelwidth;
        height = fr.getHeight();

        comboBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(comboBitmap);


        comboImage.drawBitmap(fr, 0f, 0f, null);
        comboImage.drawBitmap(sc, fr.getWidth(), 0f, null);
        return comboBitmap;

    }
    public Bitmap mergeBitmapV(Bitmap fr, Bitmap sc)
    {

        Bitmap comboBitmap;

        int width, height;

        width = fr.getWidth();
        height = fr.getHeight() + sc.getHeight();;

        comboBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(comboBitmap);


        comboImage.drawBitmap(fr, 0f, 0f, null);
        comboImage.drawBitmap(sc, 0f, fr.getHeight(), null);
        return comboBitmap;

    }

    public Bitmap textAsBitmap(String text, float textSize, int textColor) {
        printStrings.clear();
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        int j= 0;
        int k=0;
        Boolean set1 = false;
        Boolean set2 = false;
        Boolean set3 = false;

        for(int i=0;i<text.length();i++){
            if(set1==false) {
                twidth = paint.measureText(text.substring(0, i));
            }
            else if(set2== false && set1 == true){
                twidth = paint.measureText(text.substring(j, i));
            }
            else if(set3 == false && set2== true && set1 == true) {
                twidth = paint.measureText(text.substring(k, i));
            }

            if(twidth > ourWidth && set1 == false){
                //check previous 10 for space and break there instead
                for(int i2=0;i2<10;i2++){
                    if(set1==false) {
                        if (text.substring(i - i2, (i - i2 + 1)).equals(" ")) {
                            j = (i-i2);
                            printStrings.add(0, text.substring(0, (i-i2)));
                            set1 = true;
                            twidth = paint.measureText(text.substring(j, i));
                        }
                    }
                }
                //if we didn't set space after finding break, proceed to set line here
                if(set1==false) {
                    printStrings.add(0, text.substring(0, i));
                    System.out.println("LENGTH EXCEEDED1: " + i);
                    j = i;
                    set1 = true;
                    twidth = paint.measureText(text.substring(j, i));
                }
            }


            if(twidth > (ourWidth) && set2 == false){
                //check previous 10 for space and break there instead
                for(int i2=0;i2<10;i2++){
                    if(set2==false) {
                        if (text.substring(i - i2, (i - i2 + 1)).equals(" ")) {
                            k = (i-i2);
                            printStrings.add(1, text.substring(j, (i-i2)));
                            set2 = true;
                            twidth = paint.measureText(text.substring(k, i));
                        }
                    }
                }
                //if we didn't set space after finding break, proceed to set line here
                if(set2==false) {
                    printStrings.add(1, text.substring(j, i));
                    System.out.println("LENGTH EXCEEDED2: " + i);
                    k = i;
                    set2 = true;
                    twidth = paint.measureText(text.substring(k, i));
                }

            }

            if(twidth > (ourWidth) && set3 == false){

                printStrings.add(2, text.substring(k,i));
                System.out.println("LENGTH EXCEEDED3: "+i);
                set3 = true;
                twidth = paint.measureText(text.substring(k, i));
            }
        }

        // if the length wasn't exceeded, we need to add the partial string
        if(set1==false){
            printStrings.add(0,text);
        }
        if(set2== false && set1 == true){
            printStrings.add(1,text.substring(j,text.length()));
        }
        if(set3==false && set1==true && set2==true){
            printStrings.add(2,text.substring(k,text.length()));
        }


        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
       // int width = (int) (paint.measureText(printStrings.get(0)) + 0.5f); // round
        int width = ourWidthLong;
        int height = (int) (baseline + 60 + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(printStrings.get(0), 0, baseline, paint);
        if(printStrings.size() > 1) {
            String holdstring = printStrings.get(1);
            if(holdstring.substring(0,1).equals(" ")){
                printStrings.set(1, holdstring.substring(1,holdstring.length()));
            }
            canvas.drawText(printStrings.get(1), 0, baseline + 30, paint);
        }
        if(printStrings.size() > 2) {
            String holdstring2 = printStrings.get(2);
            if(holdstring2.substring(0,1).equals(" ")){
                printStrings.set(2, holdstring2.substring(1,holdstring2.length()));
            }
            canvas.drawText(printStrings.get(2), 0, baseline + 60, paint);
        }
        return image;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
       /* Bitmap myBitmap1 = null;
        Bitmap myBitmap2 = null;
        Bitmap myBitmap3 = null;
        Bitmap myBitmap4 = null;
        Bitmap myBitmap5 = null;
        Bitmap myBitmap6 = null;
        Bitmap myBitmap7 = null;
        Bitmap myBitmap8 = null;
        Bitmap myBitmap9 = null;
        Bitmap myBitmap10 = null;
        bitlist.clear();

            myBitmap1 = BitmapFactory.decodeResource(mmContext.getResources(), R.drawable.socialiconsapp_facebooks);
            myBitmap2 = BitmapFactory.decodeResource(mmContext.getResources(), R.drawable.socialiconsapp_googlepluss);
            myBitmap3 = BitmapFactory.decodeResource(mmContext.getResources(), R.drawable.socialiconsapp_twitters);
            myBitmap4 = BitmapFactory.decodeResource(mmContext.getResources(), R.drawable.socialiconsapp_pinterests);
            myBitmap5 = BitmapFactory.decodeResource(mmContext.getResources(), R.drawable.socialiconsapp_instagrams);
            myBitmap6 = BitmapFactory.decodeResource(mmContext.getResources(), R.drawable.socialiconsapp_yelps);
            myBitmap7 = BitmapFactory.decodeResource(mmContext.getResources(), R.drawable.socialiconsapp_foursquares);
            myBitmap8 = BitmapFactory.decodeResource(mmContext.getResources(), R.drawable.socialiconsapp_grubhubs);
            myBitmap9 = BitmapFactory.decodeResource(mmContext.getResources(), R.drawable.socialiconsapp_reversed_zomatos);
            myBitmap10 = BitmapFactory.decodeResource(mmContext.getResources(), R.drawable.socialiconsapp_opentables);

        bitlist.add(myBitmap1);
        bitlist.add(myBitmap2);
        bitlist.add(myBitmap3);
        bitlist.add(myBitmap4);
        bitlist.add(myBitmap5);
        bitlist.add(myBitmap6);
        bitlist.add(myBitmap7);
        bitlist.add(myBitmap8);
        bitlist.add(myBitmap9);
        bitlist.add(myBitmap10);

        Bitmap myBitmap = constructBitmap();*/
       // saveImage(mmContext, myBitmap, "logo","one");

        Bitmap b = getImageBitmap(getContext(), "logo","one");

        OutputStream os = null;
        try {
            File f = File.createTempFile("jeff", ".png", new File("/sdcard"));
            os = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.PNG, 100, os);
            return ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_ONLY);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

    public Bitmap constructBitmap(){
        int count = 0;
        rows = null;
        for(int i=0;i<10;i++){
            if(prefs.getBoolean("check"+i,false)){
                myString  = prefs.getString("text"+i, "");
                //Bitmap myBitmap1 = BitmapFactory.decodeResource(mmContext.getResources(), R.drawable.socialiconsapp_facebook);
                Bitmap thisBitmap = bitlist.get(i);
                Bitmap t1 = textAsBitmap(myString, 23, Color.BLACK);
                Bitmap b1 = bitlist.get(i);
                Bitmap row = mergeBitmapH(thisBitmap, t1);
                if(count == 0){
                    rows = row;
                }
                else{
                    rows = mergeBitmapV(rows, row);
                }
                count = count+1;
            }

        }

        return rows;
    }

}