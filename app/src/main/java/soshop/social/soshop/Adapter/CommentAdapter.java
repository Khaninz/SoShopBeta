package soshop.social.soshop.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import soshop.social.soshop.R;

/**
 * Created by Ninniez on 2/3/2015.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private ArrayList<String> mCommentList;

    public CommentAdapter(ArrayList<String> commentList){
        mCommentList = commentList;

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

        holder.getCommentTextView().setText(mCommentList.get(position));

    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);



    }
}
