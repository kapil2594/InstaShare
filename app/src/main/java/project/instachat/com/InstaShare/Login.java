package project.instachat.com.InstaShare;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class Login extends AppCompatActivity
{
//Declaring the variables
    protected TextView mSignupTextView;
    protected TextView mUsername;
    protected TextView mPassword;
    protected ImageButton mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS); //Spinner in the actionbar while data is loading


        setContentView(R.layout.activity_login);

//calling the signup and loginbox method

      //  SignUpMethod();
        LoginBox();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
//this method is called when user want to create new account


 /*   public void SignUpMethod() {
        mSignupTextView = (TextView) findViewById(R.id.SignUpText);
        mSignupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);

            }
        });
    }
*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    //Handling the click on the menu bar overflow items

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
      else if(id == R.id.refresh)
        {
            mUsername.setText("");
            mPassword.setText("");
        }
        else if(id == R.id.SignUp)
        {
            Intent intent = new Intent(Login.this, SignUp.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
//This method is for the login activity
    public void LoginBox() {
        mUsername = (TextView) findViewById(R.id.usernameField);
        mPassword = (TextView) findViewById(R.id.passwordField);

        mLoginButton = (ImageButton) findViewById(R.id.LoginButton);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = mUsername.getText().toString();
                String password = mPassword.getText().toString();


                username = username.trim();
                password = password.trim();

                if (username.isEmpty() || password.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                    builder.setMessage(R.string.login_error_message);
                    builder.setTitle(R.string.login_error_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                else
                {
                    setProgressBarIndeterminate(true);
                    //Login
                     ParseUser.logInInBackground(username, password, new LogInCallback() {
                         @Override
                         public void done(ParseUser user, ParseException e) {

                             setProgressBarIndeterminate(false);
                             if (e == null) {
                                 //Success

                                 Intent intent = new Intent(Login.this,userActivity.class);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                 startActivity(intent);

                             }
                             else
                             {
                                 AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                 builder.setMessage(e.getMessage());
                                 builder.setTitle(R.string.signup_error_title);
                                 builder.setPositiveButton(android.R.string.ok, null);
                                 AlertDialog dialog = builder.create();
                                 dialog.show();
                             }
                         }
                     });


                }
            }
        });
    }
}
