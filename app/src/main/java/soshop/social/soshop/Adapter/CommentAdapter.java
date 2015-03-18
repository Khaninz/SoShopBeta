package soshop.social.soshop.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;

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

        public ViewHolder(View itemView) {
            super(itemView);

            mCommentTextView = (TextView) itemView.findViewById(R.id.commentTextView);
        }

        public TextView getCommentTextView(){
            return mCommentTextView;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ParseObject commentObject = mCommentObjects.get(position);

        holder.getCommentTextView().setText(commentObject.getString(ParseConstants.KEY_COMMENT_TEXT));

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
