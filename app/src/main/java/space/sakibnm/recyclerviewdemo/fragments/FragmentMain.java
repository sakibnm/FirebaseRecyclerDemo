package space.sakibnm.recyclerviewdemo.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import space.sakibnm.recyclerviewdemo.model.Friend;
import space.sakibnm.recyclerviewdemo.R;
import space.sakibnm.recyclerviewdemo.model.FriendsAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMain extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FRIENDS = "friendsarray";

    private Friend mFriend;
    private int position;
    private String username;
    private ImageButton logout;
    private TextView greeting;
    private EditText editTextName;
    private EditText editTextEmail;
    private EditText editTextAge;
    private Button buttonAddEdit;

    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private ArrayList<Friend> mFriends;


    private Boolean isEdit = false;

    private ImainFragmentButtonAction mListener;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;



    public FragmentMain() {
        // Required empty public constructor
    }

    public static FragmentMain newInstance() {
        FragmentMain fragment = new FragmentMain();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FRIENDS, new ArrayList<Friend>());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_FRIENDS)) {
                mFriends = (ArrayList<Friend>) args.getSerializable(ARG_FRIENDS);
            }

            //            Initializing Firebase...
            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();

            this.username = mUser.getDisplayName();

            //            Loading initial data...
            loadData();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof ImainFragmentButtonAction){
            mListener = (ImainFragmentButtonAction) context;
        }else{
            throw new RuntimeException(context.toString()+ "must implement IaddButtonAction");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        greeting = rootView.findViewById(R.id.textViewGreet);
        editTextName = rootView.findViewById(R.id.editTextName);
        editTextEmail = rootView.findViewById(R.id.editTextEmail);
        editTextAge = rootView.findViewById(R.id.editTextPhone);
        buttonAddEdit = rootView.findViewById(R.id.buttonAddEdit);
        logout = rootView.findViewById(R.id.imageButtonLogout);
        greeting.setText("Hello "+username+"!!!");

        //      Setting up RecyclerView........
        recyclerView = rootView.findViewById(R.id.recyclerReview);
        recyclerViewLayoutManager = new LinearLayoutManager(getContext());
        friendsAdapter = new FriendsAdapter(mFriends, getContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerView.setAdapter(friendsAdapter);

        //      AddEdit button.......
        buttonAddEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Edit button.....
                if(isEdit){
                    String name = String.valueOf(editTextName.getText());
                    String email = String.valueOf(editTextEmail.getText());
                    String age = String.valueOf(editTextAge.getText());

                    db.collection("users")
                            .document(mUser.getEmail())
                            .collection("friends")
                            .document(mFriend.getEmail())
                            .delete();

                    mFriend = new Friend(name, email, age);

                    addToFirebase(mFriend);
                    clearFields();
                    disableEdit();
//                Add button....
                }else{
                    String name = String.valueOf(editTextName.getText());
                    String email = String.valueOf(editTextEmail.getText());
                    String age = String.valueOf(editTextAge.getText());
                    mFriend = new Friend(name, email, age);
                    addToFirebase(mFriend);
                    clearFields();
                }
            }
        });
        //  logout button...
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.logoutPressed();
            }
        });

//        Create a listener for Firebase data change...
        db.collection("users")
                .document(mUser.getEmail())
                .collection("friends")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error!=null){
                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }else{
//                            retrieving all the elements from Firebase....
                            ArrayList<Friend> newFriends = new ArrayList<>();
                            for(DocumentSnapshot document : value.getDocuments()){
                                newFriends.add(document.toObject(Friend.class));
                            }
//                            replace all the item in the current RecyclerView with the received elements...
                            friendsAdapter.setUsers(newFriends);
                            friendsAdapter.notifyDataSetChanged();
                        }
                    }
                });

        return rootView;
    }

    private void addToFirebase(Friend friend) {
        //        Add the new friend to Firebase Cloud Firestore...
//        Map<String, Object> friendMap = new HashMap<>();
//        friendMap.put("name", mFriend.getName());
//        friendMap.put("email", mFriend.getEmail());
//        friendMap.put("phone", mFriend.getPhone());

        db.collection("users")
                .document(mUser.getEmail())
                .collection("friends")
                .document(friend.getEmail())
                .set(friend)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getContext(),"Friend added!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to add a friend! Try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    Updating the RecyclerView when something gets changed...
    public void updateRecyclerView(ArrayList<Friend> friends){
        this.mFriends = friends;
        friendsAdapter.notifyDataSetChanged();
    }

    //    Enabling Edit.....
    public void enableEdit(Friend friend){
        this.isEdit = true;
        this.mFriend = friend;
        editTextName.setText(mFriend.getName());
        editTextEmail.setText(mFriend.getEmail());
        editTextAge.setText(mFriend.getPhone()+"");
        buttonAddEdit.setText("Edit");
    }

    //    Disabling Edit.....
    public void disableEdit(){
        this.isEdit = false;
        buttonAddEdit.setText("Add");
    }
    public void clearFields(){
        editTextName.setText("");
        editTextEmail.setText("");
        editTextAge.setText("");
    }

    private void loadData() {
        ArrayList<Friend> friends = new ArrayList<>();
        db.collection("users")
                .document(mUser.getEmail())
                .collection("friends")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
//                                Just like GSON..... Friend has to be Serializable,
//                                has to exactly match the variable names with the keys in the documents,
//                                and must have getters, setters, and toString() ....

                                Friend friend = documentSnapshot.toObject(Friend.class);
                                friends.add(friend);

                            }
                            updateRecyclerView(friends);
                        }
                    }
                });
    }

    public void deleteUser(String friendEmail) {
        db.collection("users")
                .document(mUser.getEmail())
                .collection("friends")
                .document(friendEmail)
                .delete();
    }

    public interface ImainFragmentButtonAction {
        void logoutPressed();
    }
}
