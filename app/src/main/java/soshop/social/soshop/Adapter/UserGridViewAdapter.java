package soshop.social.soshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseUser;

import java.util.List;

import soshop.social.soshop.ProfileActivity;
import soshop.social.soshop.R;
import soshop.social.soshop.Utils.IntentConstants;
import soshop.social.soshop.Utils.ParseConstants;

/**
 * Created by Ninniez on 3/20/2015.
 */
public class UserGridViewAdapter extends RecyclerView.Adapter<UserGridViewAdapter.ViewHolder>{

    private List<ParseUser> mUserToDisplay;
    private Context mContext;


    public UserGridViewAdapter(Context context, List<ParseUser> userToDisplay){
        mContext = context;
        mUserToDisplay = userToDisplay;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_view,parent,false);

        return new ViewHolder(v);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mGridImageView;
        private TextView mImageName;

        public ViewHolder(View itemView) {
            super(itemView);

            mGridImageView = (ImageView) itemView.findViewById(R.id.gridImageView);
            mImageName = (TextView) itemView.findViewById(R.id.imageName);

        }

        public ImageView getGridImageView(){
            return mGridImageView;
        }

        public TextView getImageName(){
            return mImageName;
        }


    }

    @Override
    public void onBindViewHolder(UserGridViewAdapter.ViewHolder holder, int position) {
        final ParseUser user = mUserToDisplay.get(position);

        //START: add image from Parse to imageView using tool from Picasso
//        ParseFile file = post.getParseFile(ParseConstants.KEY_IMAGE_I);// get the file in parse object
//        Uri fileUri = Uri.parse(file.getUrl());//get the uri of the file.
//        Picasso.with(mContext).load(fileUri.toString()).into(holder.getGridImageView());
        //END: add image from Parse to imageView using tool from Picasso

        String firstName = user.getString(ParseConstants.KEY_FIRST_NAME);
        String lastName = user.getString(ParseConstants.KEY_LAST_NAME);

        holder.getImageName().setText(firstName+" "+lastName);

        holder.getGridImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String senderId = user.getObjectId();

                Intent intent = new Intent(mContext, ProfileActivity.class);
                intent.putExtra(IntentConstants.KEY_USER_ID,senderId);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUserToDisplay.size();
    }


}
