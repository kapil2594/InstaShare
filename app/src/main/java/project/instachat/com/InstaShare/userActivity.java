package project.instachat.com.InstaShare;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;

import com.parse.ParseAnalytics;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class userActivity extends AppCompatActivity
{

        //Declaring the Tag variable that we can use in Log.i method
        public static final String TAG = userActivity.class.getSimpleName();

       //Declaring Constants for taking photos and videos and reading photos and videos from external storage
        public static int take_photo_request = 0;
        public static int take_video_request = 1;
        public static int pick_photo_request = 2;
        public static int pick_video_request = 3;


        //Declaring the media type video or image
        public static int Media_type_image = 4;
        public static int Media_type_video =5;

        //Declaring Uri(Uniform Resource Information)
        protected Uri mMediaUri ;

        public static int file_size_limit = 1024*1024*10; //10MB





        //Declaring the AlertDialog Interface that pop up when we click on the camera button on the action bar
        protected DialogInterface.OnClickListener mDialogListener = new DialogInterface.OnClickListener(){
            @Override
                    public void onClick(DialogInterface dialog,int which)
            {
                switch (which)  //Switch case to know which item is click in the Dialog interface
                {
                    case 0:
                        //take picture from the camera and store it in the external device
                        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //Creating an intent to capture photo from the camera
                        mMediaUri = getOutputMediaFileUri(Media_type_image);  // create a file to save the image

                        if(mMediaUri==null)
                        {
                            //display an error
                            Toast.makeText(userActivity.this,"There was a problem accessing your device's External Storage",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);  // set the image file name


                            startActivityForResult(takePhotoIntent, take_photo_request);// start the image capture Intent

                        }
                        break;
                    case 1:
                        //take video from the camera and store it in the external storage
                        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        mMediaUri = getOutputMediaFileUri(Media_type_video);
                        if(mMediaUri==null)
                        {
                            //display an error
                            Toast.makeText(userActivity.this,"There was a problem accessing your device's External Storage",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT,mMediaUri);
                            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10); //Declaring the Duration of the video which is 10 seconds
                            videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,0); //0=LOW RESOLUTION VIDEO
                            startActivityForResult(videoIntent,take_video_request);
                        }

                        break;
                    case 2:
                        //choose picture from the external storage
                        Intent choosePhotoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        choosePhotoIntent.setType("Pictures/*");
                        startActivityForResult(choosePhotoIntent,pick_photo_request);

                        break;
                    case 3:
                        //choose video
                        Intent chooseVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        chooseVideoIntent.setType("video/*");
                        Toast.makeText(userActivity.this,R.string.video_size_warning,Toast.LENGTH_LONG).show();
                        startActivityForResult(chooseVideoIntent, pick_photo_request);
                        break;
                }
            }
        };

        private Uri getOutputMediaFileUri(int mediaType)
        {

            // To be safe, you should check that the SDCard is mounted
            // using Environment.getExternalStorageState() before doing this.

            if(isExternalStorageAvailable())
            {
                //get the URI
                //1.get the external storage directory


                String appname = "InstaShare";
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),appname);

                //2.create our subDirectory


                if(!mediaStorageDir.exists())
                {
                  if(! mediaStorageDir.mkdirs())
                  {
                      Log.e(TAG,"Failed to create a Directory");
                      return null;
                  }
                }

                //3.Create filename

                //4.Create File

                File mediaFile;
                Date now = new Date();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                String path = mediaStorageDir.getPath()+File.pathSeparator;
                if(mediaType==Media_type_image)
                {
                    mediaFile = new File(path+"IMG_"+timeStamp+".jpg");
                }
                else if(mediaType == Media_type_video)
                {
                    mediaFile = new File(path+"VID_"+timeStamp+".mp4");
                }
                else
                {
                    return null;
                }
                Log.d(TAG,"File :"+Uri.fromFile(mediaFile));
                //5. Return the file URI

                return  Uri.fromFile(mediaFile);
            }
            else
            {
                return  null;
            }


        }

        private boolean isExternalStorageAvailable()
        {
            String state = Environment.getExternalStorageState();
            return state.equals(Environment.MEDIA_MOUNTED);
        }

        /**
         * The {@link android.support.v4.view.PagerAdapter} that will provide
         * fragments for each of the sections. We use a
         * {@link FragmentPagerAdapter} derivative, which will keep every
         * loaded fragment in memory. If this becomes too memory intensive, it
         * may be best to switch to a
         * {@link android.support.v4.app.FragmentStatePagerAdapter}.
         */
        private SectionsPagerAdapter mSectionsPagerAdapter;

        /**
         * The {@link ViewPager} that will host the section contents.
         */
        private ViewPager mViewPager;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_user);

            ParseAnalytics.trackAppOpenedInBackground(getIntent());//Declaring the parse analytics to do the analyse of the data

    //Checking whether the user is login or not if not then navigating to the login window

            ParseUser currentUser = ParseUser.getCurrentUser();
            if(currentUser == null)
            {
                navigateToLogin(); //Declaring the navigatetologin method
            }
            else
            {
                Log.i(TAG, currentUser.getUsername());
            }



            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

        }




    //navigate to the login window method
        private void navigateToLogin() {
            Intent intent = new Intent(this,Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }


        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_user, menu);
            return true;
        }



        //This method check on which item of the menu bar or action bar the uuser has clicked and takes action accordingly
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
            else   if(id==R.id.Logout)
            {
                ParseUser.logOut(); //logout the user
                navigateToLogin();

            }
            else if (id == R.id.Edit_friends)
            {
                Intent mIntent = new Intent(this,Edit_Friends.class);
                startActivity(mIntent); //Starts Edit Friends Activity

            }
            else if(id == R.id.action_camera)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this); //Creates a dialog box with option like choose image ,choose video etc
                builder.setItems(R.array.camera_choices,mDialogListener);
                AlertDialog dialog = builder.create();
                dialog.show();
            }



            return super.onOptionsItemSelected(item);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            try
            {
                super.onActivityResult(requestCode, resultCode, data);
               // Log.d(TAG, requestCode.toString());
                if (resultCode == RESULT_OK)
                {

                    //add image to the gallery
                    if(requestCode ==pick_photo_request||requestCode==pick_video_request)
                    {
                        if(data==null)
                        {
                            Toast.makeText(userActivity.this,R.string.general_error,Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            mMediaUri = data.getData();
                        }
                        Log.i(TAG ,"Media URI :"+mMediaUri);
                        if(requestCode ==pick_video_request)
                        {
                            int filesize =0;
                            InputStream inputStream = null;
                            try {
                                  inputStream = getContentResolver().openInputStream(mMediaUri); //contains the selected video in Bytes
                                filesize = inputStream.available(); //calculating the size of the video
                            }
                            catch(FileNotFoundException e)
                            {
                                Toast.makeText(userActivity.this,R.string.error_opening_file,Toast.LENGTH_LONG).show();
                                return;
                            }
                            catch(IOException e)
                            {
                                Toast.makeText(userActivity.this,R.string.error_opening_file,Toast.LENGTH_LONG).show();
                                return;
                            }
                            finally
                            {
                               try
                               {
                                   inputStream.close();
                               }
                               catch(IOException e)
                               {
                                   e.printStackTrace();
                               }
                            }
                            if( filesize >=file_size_limit)   //checking whether the file size is greater than 10 mb or not
                            {
                                Toast.makeText(userActivity.this,"File Size is greater than 10 MB",Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                    }
                    else
                    {
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.setData(mMediaUri);
                        sendBroadcast(mediaScanIntent);

                    }
                  //  Toast.makeText(userActivity.this,"successful",Toast.LENGTH_LONG).show();

                    Intent Recipientintent = new Intent(userActivity.this, RecipientActivity.class) ;
                    Recipientintent.setData(mMediaUri);

                    String fileType;
                    if(requestCode ==pick_photo_request||requestCode==take_photo_request)
                    {
                        fileType = ParseConstants.TYPE_IMAGE;
                    }
                    else
                    {
                        fileType = ParseConstants.TYPE_VIDEO;
                    }
                    Recipientintent.putExtra(ParseConstants.KEY_FILE_TYPE,fileType);
                    startActivity(Recipientintent);

                }
                else  if(resultCode!=RESULT_CANCELED)
                {
                    Toast.makeText(this, "unable to locate",Toast.LENGTH_LONG).show();
                   // Intent RecipientIntent = new Intent(this, RecipientActivity.class) ;
                   // RecipientIntent.setData(mMediaUri);
                    //startActivity(RecipientIntent);
                }
            } catch (Exception ex) {
                Toast.makeText(userActivity.this, ex.toString(),
                        Toast.LENGTH_SHORT).show();
            }

        }

   }



