package zomifi.op27no2.socialbranding;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.Platform;
import com.clover.sdk.v1.BindingException;
import com.clover.sdk.v1.ClientException;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v1.ServiceException;
import com.clover.sdk.v1.merchant.Merchant;
import com.clover.sdk.v1.merchant.MerchantAddress;
import com.clover.sdk.v1.merchant.MerchantConnector;
import com.clover.sdk.v1.printer.ReceiptRegistrationConnector;
import com.clover.sdk.v3.employees.Employee;
import com.clover.sdk.v3.employees.EmployeeConnector;
import com.firebase.client.Firebase;
import com.google.gson.GsonBuilder;
import com.parse.ParseAnalytics;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MainActivity extends Activity {
        private SharedPreferences prefs;
        private SharedPreferences.Editor edt;
        private Account account;
        private ReceiptRegistrationConnector connector;
        private MerchantConnector merchantConnector;
        private EmployeeConnector mEmployeeConnector;

        private static Context mContext;
        private ImageView im1;
        private static String  mercID;
        private static String  mEmail = "no data";
        private static String  mCloverID;
        private static String  mAdd= "no data";
        private static String  mCity = "no data";
        private static String  mState= "no data";
        private static String  mCountry= "no data";
        private static String  mNumber= "no data";
        private static String  mZip= "no data";
        private static String  mTimezone= "no data";
        private static String  mName= "no data";
        public ArrayList<String> textlist = new ArrayList();
        public ArrayList<Boolean> checklist = new ArrayList();

        public ArrayAdapter<String> adapter;
        private ListView lv2;
        private TextView mtext;
        private TextView stext;
        private TextView merchanttext;
        private String myurl;
        private Switch mSwitch;
        private SeekBar seek1;
        private Boolean active;
        private Boolean tMerchant = false;
        private Boolean tEmployee = false;
        HttpURLConnection httpcon;
        String url = null;
        String data = null;
        String result = null;

    private Bitmap rows;
    private static Boolean isBig;
    private static int ourWidth;
    private static int ourWidthLong;

    private ArrayList<Bitmap> bitlist = new ArrayList();
    private ArrayList<String> printStrings = new ArrayList();

    private String myString;
    private float twidth = 250;
    private static int pixelwidth = 384;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Firebase.setAndroidContext(this);


            mContext = this;
            prefs = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
            edt = this.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
            lv2 = (ListView) findViewById(R.id.listView2);
            checklist.clear();
            textlist.clear();
            TypedArray imgs = getResources().obtainTypedArray(R.array.images);
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

            for(int i=0; i<10; i++){
                checklist.add(i, prefs.getBoolean("check" + i, false));
            }
            for(int i=0; i<10; i++){
                textlist.add(i,prefs.getString("text"+i, ""));
            }
            adapter = new EasyAdapter(this, textlist, checklist, imgs);
            lv2.setAdapter(adapter);


          //  stext = (TextView) findViewById(R.id.scaletext);
            mSwitch = (Switch) findViewById(R.id.mySwitch);

            active = prefs.getBoolean("active", false);
            mSwitch.setChecked(active);
        //    seek1 = (SeekBar) findViewById(R.id.scale);
        //    seek1.setProgress(prefs.getInt("scalepercent", 100));
        //    stext.setText("Logo Size: " + prefs.getInt("scalepercent", 100) + "%");
            mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView,
                                             boolean isChecked) {
                    if (isChecked) {
                        active = true;
                        connect();
                        edt.putBoolean("active", true);
                        edt.commit();
                    } else {
                        active = false;
                        if (connector != null) {
                            unregisterReceiptRegistration();
                        }
                        edt.putBoolean("active", false);
                        edt.commit();
                    }
                }
            });

        Bitmap myBitmap1 = null;
        Bitmap myBitmap2 = null;
        Bitmap myBitmap3 = null;
        Bitmap myBitmap4 = null;
        Bitmap myBitmap5 = null;
        Bitmap myBitmap6 = null;
        Bitmap myBitmap7 = null;
        Bitmap myBitmap8 = null;
        Bitmap myBitmap9 = null;
        Bitmap myBitmap10 = null;

        myBitmap1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.socialiconsapp_facebooks);
        myBitmap2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.socialiconsapp_googlepluss);
        myBitmap3 = BitmapFactory.decodeResource(this.getResources(), R.drawable.socialiconsapp_twitters);
        myBitmap4 = BitmapFactory.decodeResource(this.getResources(), R.drawable.socialiconsapp_pinterests);
        myBitmap5 = BitmapFactory.decodeResource(this.getResources(), R.drawable.socialiconsapp_instagrams);
        myBitmap6 = BitmapFactory.decodeResource(this.getResources(), R.drawable.socialiconsapp_yelps);
        myBitmap7 = BitmapFactory.decodeResource(this.getResources(), R.drawable.socialiconsapp_foursquares);
        myBitmap8 = BitmapFactory.decodeResource(this.getResources(), R.drawable.socialiconsapp_grubhubs);
        myBitmap9 = BitmapFactory.decodeResource(this.getResources(), R.drawable.socialiconsapp_reversed_zomatos);
        myBitmap10 = BitmapFactory.decodeResource(this.getResources(), R.drawable.socialiconsapp_opentables);

        bitlist.clear();
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

        Bitmap myBitmap = constructBitmap();
        saveImage(this, myBitmap, "logo","one");


    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Retrieve the Clover account
        if (account == null) {
            account = CloverAccount.getAccount(this);

            if (account == null) {
                Toast.makeText(this, getString(R.string.no_account), Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        active = prefs.getBoolean("active", false);

        // Create and Connect
        connect();

        getEmployee();

        // Get the merchant object
        getMerchant();

    }

    private void getEmployee() {
        System.out.println("GETEMPLOYEE CALLED");

        new AsyncTask<Void, Void, List<Employee>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<Employee> doInBackground(Void... params) {
                List<Employee> employees = null;

                try {
                    employees = mEmployeeConnector.getEmployees();
                    for (Employee employee : employees) {
                        if (employee.getIsOwner()) {
                            mEmail = employee.getEmail();
                        }
                    }
                    tEmployee = true;
                    if(tMerchant==true && tEmployee==true ){
                        System.out.println("preexecute");
                        infoexecute();

                    }

                } catch (RemoteException e) {

                } catch (ClientException e) {

                } catch (ServiceException e) {

                } catch (BindingException e) {

                }
                return employees;
            }

            @Override
            protected void onPostExecute(List<Employee> employees) {
                super.onPostExecute(employees);

            }
        }.execute();
    }

    private void getMerchant() {
        System.out.println("getmerchant called: ");

        new AsyncTask<Void, Void, Merchant>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();


            }

            @Override
            protected Merchant doInBackground(Void... params) {
                Merchant merchant = null;
                System.out.println("doinbackground ");
                try {
                    merchant = merchantConnector.getMerchant();
                    MerchantAddress mAddress = merchant.getAddress();
                    mAdd = mAddress.getAddress1()+" "+mAddress.getAddress2()+" "+mAddress.getAddress3();
                    mCity = mAddress.getCity();
                    mState = mAddress.getState();
                    mZip = mAddress.getZip();
                    mCountry = mAddress.getCountry();
                    TimeZone tz = merchant.getTimeZone();
                    mTimezone = tz.getDisplayName();
                    mName = merchant.getName();
                    mNumber = merchant.getPhoneNumber();
                    mercID = merchant.getId();
                    // either merchant or employee will finish first, check here and send info
                    tMerchant = true;
                    if(tMerchant==true && tEmployee==true ){
                        System.out.println("preexecute");
                        infoexecute();

                    }

                } catch (RemoteException e) {
                    System.out.println("error1 called: ");
                    Map<String, String> dimensions = new HashMap<String, String>();
                    dimensions.put("code", e.getMessage());
                    ParseAnalytics.trackEventInBackground("error", dimensions);
                    e.printStackTrace();
                } catch (ClientException e) {
                    System.out.println("error2 called: "+ e.getMessage());
                    Map<String, String> dimensions = new HashMap<String, String>();
                    dimensions.put("code", e.getMessage());
                    ParseAnalytics.trackEventInBackground("error", dimensions);
                    e.printStackTrace();
                } catch (ServiceException e) {
                    System.out.println("error3 called: ");
                    Map<String, String> dimensions = new HashMap<String, String>();
                    dimensions.put("code", e.getMessage());
                    ParseAnalytics.trackEventInBackground("error", dimensions);
                    e.printStackTrace();
                } catch (BindingException e) {
                    System.out.println("error4 called: ");
                    Map<String, String> dimensions = new HashMap<String, String>();
                    dimensions.put("code", e.getMessage());
                    ParseAnalytics.trackEventInBackground("error", dimensions);
                    e.printStackTrace();
                }
                return merchant;
            }

            @Override
            protected void onPostExecute(Merchant merchant) {
                super.onPostExecute(merchant);
                if (!isFinishing()) {
                    // Populate the merchant information
                    if (merchant != null) {
                        ReceiptRegistrationProvider2.passMercID(mercID, mContext);
                        // merchanttext.setText("This is your Merchant ID: "+mercID);
                      //  setImage();

                    }
                }

            }
        }.execute();
    }

    public static void infoexecute() {
        Firebase ref = new Firebase("https://zoomifi-app-installs.firebaseio.com/SocialBranding/" + mercID);
        ref.child("email").setValue(mEmail);
        ref.child("merchantid").setValue(mercID);
        ref.child("zipcode").setValue(mZip);
        ref.child("timezone").setValue(mTimezone);
        ref.child("phone").setValue(mNumber);
        ref.child("merchantName").setValue(mName);
        ref.child("address").setValue(mAdd);
        ref.child("city").setValue(mCity);
        ref.child("state").setValue(mState);
        ref.child("county").setValue(mCountry);


        Map<String, String> comment = new HashMap<String, String>();
        comment.put("email", mEmail);
        comment.put("merchantID", mercID);
        comment.put("zipcode", mZip);
        comment.put("timezone", mTimezone);
        comment.put("phone", mNumber);
        comment.put("merchantName", mName);
        comment.put("address", mAdd);
        comment.put("city", mCity);
        comment.put("state", mState);
        comment.put("country", mCountry);
        comment.put("cloverID", "97BK7R1NQPVKM");
        System.out.println("INFO: " + comment);
        String json = new GsonBuilder().create().toJson(comment, Map.class);
        makeRequest("https://ops.zoomifi.com/appinstall.php", json);
    }

    public static HttpResponse makeRequest(String uri, String json) {
        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            return new DefaultHttpClient().execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void registerReceiptRegistration() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // Create and Connect
                connector.register(Uri.parse(ReceiptRegistrationProvider2.CONTENT_URI_IMAGE.toString()), new ReceiptRegistrationConnector.ReceiptRegistrationCallback<Void>());

                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                System.out.println("REGISTERED!!");
            }
        }.execute();
    }

    private void unregisterReceiptRegistration() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                connector.unregister(Uri.parse(ReceiptRegistrationProvider2.CONTENT_URI_IMAGE.toString()), new ReceiptRegistrationConnector.ReceiptRegistrationCallback<Void>());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
               // disconnect();
            }
        }.execute();
    }

    @Override
    protected void onPause() {
       // disconnect();
        super.onPause();
    }



    private void connect() {
        disconnect();
        if (account != null) {
            connector = new ReceiptRegistrationConnector(this, account, new ServiceConnector.OnServiceConnectedListener() {
                @Override
                public void onServiceConnected(ServiceConnector<? extends IInterface> serviceConnector) {

                    if (connector != null) {

                        if (prefs.getBoolean("firstrun", true)) {
                            unregisterReceiptRegistration();
                            edt.putBoolean("firstrun", false);
                            edt.commit();
                        }
                        if (active) {
                            registerReceiptRegistration();

                        }
                    }
                }
                @Override
                public void onServiceDisconnected(ServiceConnector<? extends IInterface> serviceConnector) {
                }
            });
            connector.connect();

        }
        if (account != null) {

            merchantConnector = new MerchantConnector(this, account, null);
            merchantConnector.connect();

        }
        if (account != null) {
            mEmployeeConnector = new EmployeeConnector(this, account, null);
            mEmployeeConnector.connect();
        }
    }

    private void disconnect() {
        if (connector != null) {
            connector.disconnect();
            connector = null;
        }
        if (mEmployeeConnector != null) {
            mEmployeeConnector.disconnect();
            mEmployeeConnector = null;
        }
        if (merchantConnector != null) {
            merchantConnector.disconnect();
            merchantConnector = null;
        }

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
            FileInputStream fis = context.openFileInput(name);
            Bitmap b = BitmapFactory.decodeStream(fis);
            fis.close();
            return b;
        }
        catch(Exception e){
        }
        return null;
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




    public void listUpdate(){
        adapter.notifyDataSetChanged();
        Bitmap myBitmap = constructBitmap();
        saveImage(this, myBitmap, "logo", "one");

    }



}