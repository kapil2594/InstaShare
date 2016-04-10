package project.instachat.com.InstaShare;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class Edit_Friends extends ListActivity
{

    private static final String TAG = Edit_Friends.class.getSimpleName();
    protected List<ParseUser> mUsers;
    protected ParseRelation<ParseUser> mFriendRelation;
    protected ParseUser mCurrentUser;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__friends);
       Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setActionBar(toolbar);

        selecting_Multiple_Friends_From_Edit_FriendList();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
      //  getActionBar().setDisplayHomeAsUpEnabled(true);
    }//end of onCreate() method

    public void selecting_Multiple_Friends_From_Edit_FriendList()
    {
        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }




    @Override
    protected void  onResume()
    {
        super.onResume();
        mCurrentUser = ParseUser.getCurrentUser();
        mFriendRelation = mCurrentUser.getRelation(ParseConstants.KEY_FRIENDS_RELATION);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.orderByAscending(ParseConstants.KEY_USERNAME);
        query.setLimit(10);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> users, ParseException e) {
                if (e == null) {
                    //success
                    mUsers = users;
                    String[] username = new String[mUsers.size()];
                    int i = 0;
                    for (ParseUser user : mUsers) {
                        username[i] = user.getUsername();
                        i++;
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(Edit_Friends.this, android.R.layout.simple_list_item_checked, username);
                    setListAdapter(adapter);

                    addFriendCheckmark();


                } else {
                    Log.e(TAG, e.getMessage());
                    AlertDialog.Builder builder = new AlertDialog.Builder(Edit_Friends.this);
                    builder.setMessage(e.getMessage());
                    builder.setTitle(R.string.error_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    } //end of onResume() method

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        if(getListView().isItemChecked(position))
        {
            //add friends
            super.onListItemClick(l, v, position, id);
            mFriendRelation.add(mUsers.get(position));
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null)
                    {
                        Log.e(TAG,e.getMessage());
                    }
                }
            });


        }
        else
        {
            //Remove  friends
            mFriendRelation.remove(mUsers.get(position));
            mCurrentUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            });



        }

    }

    private void addFriendCheckmark()
    {
        mFriendRelation.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> friends, ParseException e) {

                if(e==null)
                {
                    //list returned - look for a match
                    for(int i=0;i<mUsers.size();i++)
                    {
                        ParseUser user = mUsers.get(i);
                        for(ParseUser friend : friends)
                        {
                             if(friend.getObjectId().equals(user.getObjectId()));
                            getListView().setItemChecked(i,true );
                        }
                    }
                }
                else
                {
                    Log.e(TAG,e.getMessage());
                }
            }
        });
    }

} //end of class


