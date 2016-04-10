package project.instachat.com.InstaShare;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class RecipientActivity extends ListActivity
{

        protected List<ParseUser> mFriends;
        protected ParseRelation<ParseUser> mFriendRelation;
        protected ParseUser mCurrentUser;
        public static final String TAG = RecipientActivity.class.getSimpleName();


        Button sendButton  ;


        protected Uri mMediaUri;
        protected String mFileType;

        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_recipient);

            sendButton = (Button)findViewById(R.id.imageButton);
            sendButton.setVisibility(View.INVISIBLE);


            mMediaUri = getIntent().getData();
            mFileType = getIntent().getExtras().getString(ParseConstants.KEY_FILE_TYPE);



            //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

            //android.app.ActionBar ac = getActionBar();
           // ac.setDisplayShowHomeEnabled(true);

            Selecting_Multiple_Friends();

            //setSupportActionBar(toolbar);
           // getActionBar().setDisplayShowHomeEnabled(true);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
           // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        private void Selecting_Multiple_Friends() {
            getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }

        @Override
        public void  onResume()
        {
            super.onResume();
            mCurrentUser = ParseUser.getCurrentUser();
            mFriendRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);
            mFriendRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> friends, ParseException e) {
                    if (e == null) {
                        mFriends = friends;
                        //success

                        String[] username = new String[mFriends.size()];
                        int i = 0;
                        for (ParseUser user : mFriends) {
                            username[i] = user.getUsername();
                            i++;
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getListView().getContext(), android.R.layout.simple_list_item_checked, username);
                        setListAdapter(adapter);
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(RecipientActivity.this);
                        builder.setMessage(e.getMessage());
                        builder.setTitle(R.string.error_title);
                        builder.setPositiveButton(android.R.string.ok, null);
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }


                }

            });
        }
        @Override
        public boolean onCreateOptionsMenu(Menu menu)
        {
            // Inflate the menu; this adds items to the action bar if it is present.
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_recipient, menu);

            return super.onCreateOptionsMenu(menu);
        }

        @Override
        protected void onListItemClick(ListView l, View v, int position, long id)
        {
            super.onListItemClick(l, v, position, id);
            if(l.getCheckedItemCount()>0)
            {
                sendButton.setVisibility(View.VISIBLE);
                sendButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        ParseObject message = createMessage();
                        if(message == null)
                        {
                            AlertDialog.Builder builder = new AlertDialog.Builder(RecipientActivity.this);
                            builder.setMessage("There was an error with selected file please select a new File");
                            builder.setTitle("We're Sorry !!!");
                            builder.setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();

                        }
                        else
                        {
                             send(message);
                            finish();
                        }

                        //Toast.makeText(RecipientActivity.this,"hello World",Toast.LENGTH_LONG).show();
                    }
                });
            }
            else
            {
                sendButton.setVisibility(View.INVISIBLE);
            }


        }

    protected void send(ParseObject message)
    {
        message.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e== null)
                {
                    //success
                    Toast.makeText(RecipientActivity.this,"Message Sent !",Toast.LENGTH_LONG).show();

                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RecipientActivity.this);
                    builder.setMessage("There was an error sending your message please try again");
                    builder.setTitle("We're Sorry !!!");
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            }
        });

    }

    protected ParseObject createMessage()
        {
            ParseObject message= new ParseObject(ParseConstants.CLASS_MESSAGES);
            message.put(ParseConstants.KEY_SENDER_ID,ParseUser.getCurrentUser().getObjectId());
            message.put(ParseConstants.KEY_SENDER_NAME,ParseUser.getCurrentUser().getUsername());
            message.put(ParseConstants.KEY_RECIPIENT_IDS, getRecipientIDs());
            message.put(ParseConstants.KEY_FILE_TYPE,mFileType);

            byte[] fileBytes = FileHelper.getByteArrayFromFile(this,mMediaUri);
            if(fileBytes == null )
            {
                return null;
            }
            else
            {
                if(mFileType.equals(ParseConstants.TYPE_IMAGE))
                {
                    fileBytes = FileHelper.reduceImageForUpload(fileBytes);
                }
                String FileName = FileHelper.getFileName(this,mMediaUri,mFileType);
                ParseFile file = new ParseFile(FileName,fileBytes);
                message.put(ParseConstants.KEY_FILE,file);
                return message;
            }




        }

        protected ArrayList<String> getRecipientIDs()
        {
            ArrayList<String> recipientIDs = new ArrayList<String>();
            for(int i=0;i<getListView().getCount();i++)
            {
                if(getListView().isItemChecked(i))
                {
                    recipientIDs.add(mFriends.get(i).getObjectId());
                }
            }
            return  recipientIDs;
        }


}
