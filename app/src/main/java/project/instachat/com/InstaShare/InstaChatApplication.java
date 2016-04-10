package project.instachat.com.InstaShare;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by redskull on 3/28/2016.
 */
public class InstaChatApplication extends Application {

    public void onCreate()
    {
        super.onCreate();
        Parse.initialize(this, "nUZdqvhFYRVQNchj4PUPD8fv1YJ6IhXBaF4ZvKWi", "PLmr9Sy9ljmZlRseKQPavcYaHAR6MLHkU3Wg6kLa");

    }

}
