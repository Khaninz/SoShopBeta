package soshop.social.soshop.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.List;

import soshop.social.soshop.FullPostActivity;
import soshop.social.soshop.R;
import soshop.social.soshop.Utils.ParseConstants;

/**
 * Created by Ninniez on 3/20/2015.
 */
public class PostGridViewAdapter extends RecyclerView.Adapter<PostGridViewAdapter.ViewHolder>{

    private List<ParseObject> mPostToDisplay;
    private Context mContext;


    public PostGridViewAdapter(Context context, List<ParseObject> itemToDisplay){
        mContext = context;
        mPostToDisplay = itemToDisplay;

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
    public void onBindViewHolder(PostGridViewAdapter.ViewHolder holder, int position) {
        final ParseObject post = mPostToDisplay.get(position);

        //START: add image from Parse to imageView using tool from Picasso
        ParseFile file = post.getParseFile(ParseConstants.KEY_IMAGE_I);// get the file in parse object
        Uri fileUri = Uri.parse(file.getUrl());//get the uri of the file.
        Picasso.with(mContext).load(fileUri.toString()).into(holder.getGridImageView());
        //END: add image from Parse to imageView using tool from Picasso

        //holder.getImageName().setText(post.getString(ParseConstants.KEY_ITEM_NAME));
        holder.getGridImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String soShopPostObjectId = post.getObjectId();
                Intent fullPostIntent = new Intent(mContext, FullPostActivity.class);
                fullPostIntent.putExtra("soShopPostObjectId", soShopPostObjectId);
                mContext.startActivity(fullPostIntent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mPostToDisplay.size();
    }


}
