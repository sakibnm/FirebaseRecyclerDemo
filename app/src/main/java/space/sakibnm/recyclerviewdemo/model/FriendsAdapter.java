package space.sakibnm.recyclerviewdemo.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import space.sakibnm.recyclerviewdemo.Friend;
import space.sakibnm.recyclerviewdemo.R;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private static final String TAG = "demo";

    private ArrayList<Friend> friends;

    private IfriendsListRecyclerAction mListener;

    public FriendsAdapter() {
    }

    public FriendsAdapter(ArrayList<Friend> friends, Context context) {
        this.friends = friends;
        if(context instanceof IfriendsListRecyclerAction){
            this.mListener = (IfriendsListRecyclerAction) context;
        }else{
            throw new RuntimeException(context.toString()+ "must implement IeditButtonAction");
        }

    }

    public ArrayList<Friend> getFriends() {
        return friends;
    }

    public void setUsers(ArrayList<Friend> friends) {
        this.friends = friends;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView textViewName;
        private final TextView textViewEmail;
        private final TextView textViewPhone;
        private final Button buttonEdit;
        private final Button buttonDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewName = itemView.findViewById(R.id.textViewName);
            this.textViewEmail = itemView.findViewById(R.id.textViewEmail);
            this.textViewPhone = itemView.findViewById(R.id.textViewPhone);
            this.buttonEdit = itemView.findViewById(R.id.buttonEdit);
            this.buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public TextView getTextViewName() {
            return textViewName;
        }

        public TextView getTextViewEmail() {
            return textViewEmail;
        }

        public TextView getTextViewPhone() {
            return textViewPhone;
        }

        public Button getButtonEdit() {
            return buttonEdit;
        }

        public Button getButtonDelete() {
            return buttonDelete;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRecyclerView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycler_row,parent, false);

        return new ViewHolder(itemRecyclerView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Friend curFriend = this.getFriends().get(position);

        holder.getTextViewName().setText(curFriend.getName());
        holder.getTextViewEmail().setText(curFriend.getEmail());
        holder.getTextViewPhone().setText(curFriend.getPhone()+"");

        holder.getButtonEdit().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(TAG, "Edit clicked on: "+ friends.get(holder.getAdapterPosition()).toString());
                mListener.editButtonClickedFromRecyclerView(friends.get(holder.getAdapterPosition()));
            }
        });

        holder.getButtonDelete().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(TAG, "Delete clicked on: "+ friends.get(holder.getAdapterPosition()).toString());
//                friends.remove(holder.getAdapterPosition());
//                notifyDataSetChanged();
                String email = friends.get(holder.getAdapterPosition()).getEmail();
                mListener.deleteButtonPressedFromRecyclerView(email);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.getFriends().size();
    }

    public interface IfriendsListRecyclerAction {
        void editButtonClickedFromRecyclerView(Friend friend);
        void deleteButtonPressedFromRecyclerView(String friendEmail);
    }
}
