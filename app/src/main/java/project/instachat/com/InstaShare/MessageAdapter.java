package project.instachat.com.InstaShare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseObject;

import java.util.List;

/**
 * Created by redskull on 4/9/2016.
 */
public class MessageAdapter extends ArrayAdapter<ParseObject>
{
    protected Context   mContext;
    protected List<ParseObject> mMessages;

    public MessageAdapter(Context context,List<ParseObject> messages)
    {
        super(context,R.layout.message_item,messages);
        mContext = context;
        mMessages = messages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if(convertView==null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item,null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView)convertView.findViewById(R.id.MessageIcon);
            holder.namelabel = (TextView)convertView.findViewById(R.id.Sender);

        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
        }
        ParseObject message = mMessages.get(position);
        if(message.getString(ParseConstants.KEY_FILE_TYPE).equals(ParseConstants.TYPE_IMAGE))
        {
            holder.iconImageView.setImageResource(R.drawable.image);
        }
        else
        {
            holder.iconImageView.setImageResource(R.drawable.video);
        }

        holder.namelabel.setText(message.getString(ParseConstants.KEY_SENDER_NAME));

        return convertView;
    }

    private static class ViewHolder
    {
        ImageView iconImageView;
        TextView namelabel;

    }
}
