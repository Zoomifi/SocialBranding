package zomifi.op27no2.socialbranding;

import com.parse.Parse;
import com.parse.ParseCrashReporting;

public class MyApplication extends android.app.Application {

    public MyApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("APPONCREATE CALLED");
        ParseCrashReporting.enable(this);
        Parse.initialize(this, "B30JEurokcxd8nuF7HtABnEASDQnz2e2K5MveIa8", "evbsz4pCKwJK5V4RxO6t104T3sM4gltRwsv9gWrY");
    }
}

