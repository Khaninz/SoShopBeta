package soshop.social.soshop.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import soshop.social.soshop.R;
import soshop.social.soshop.Utils.ParseConstants;

/**
 * Created by Ninniez on 2/3/2015.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private List<ParseObject> mCommentObjects;

    public CommentAdapter(List<ParseObject> commentObjects){
        mCommentObjects = commentObjects;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {


        View v =  LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.comment_item, viewGroup, false);

        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView mCommentTextView;
        private TextView mSenderName;

        public ViewHolder(View itemView) {
            super(itemView);

            mCommentTextView = (TextView) itemView.findViewById(R.id.commentTextView);
            mSenderName = (TextView) itemView.findViewById(R.id.userNameTextView);


        }

        public TextView getCommentTextView(){
            return mCommentTextView;
        }
        public TextView getSenderName(){
            return mSenderName;
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        ParseObject commentObject = mCommentObjects.get(position);

        holder.getCommentTextView().setText(commentObject.getString(ParseConstants.KEY_COMMENT_TEXT));

        String senderId = commentObject.getString(ParseConstants.KEY_COMMENT_SENDER_ID);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(senderId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                String firstName = user.getString(ParseConstants.KEY_FIRST_NAME);
                String lastName = user.getString(ParseConstants.KEY_LAST_NAME);

                holder.getSenderName().setText(firstName+" "+lastName);
            }
        });

//        ParseObject senderObject = (ParseObject) commentObject.get(ParseConstants.KEY_RELATION_COMMENT_SENDER);
//        String firstName = senderObject.getString(ParseConstants.KEY_FIRST_NAME);
//        String lastName = senderObject.getString(ParseConstants.KEY_LAST_NAME);
//
//        holder.getSenderName().setText(firstName+" "+lastName);


    }

    @Override
    public int getItemCount() {
        return mCommentObjects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);



    }
}
