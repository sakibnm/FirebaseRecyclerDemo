package space.sakibnm.recyclerviewdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import space.sakibnm.recyclerviewdemo.fragments.FragmentLogin;
import space.sakibnm.recyclerviewdemo.fragments.FragmentMain;
import space.sakibnm.recyclerviewdemo.fragments.FragmentRegister;
import space.sakibnm.recyclerviewdemo.model.FriendsAdapter;

public class MainActivity extends AppCompatActivity
        implements FragmentLogin.IloginFragmentAction,
        FragmentMain.ImainFragmentButtonAction,
        FriendsAdapter.IfriendsListRecyclerAction,
        FragmentRegister.IregisterFragmentAction {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Firebase Demo");
//        Initializing Firebase Authentication...
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        populateScreen();
    }

    private void populateScreen() {
        //      Check for Authenticated users ....
        if(currentUser != null){
            //The user is authenticated, Populating The Main Fragment....
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerMain, FragmentMain.newInstance(),"mainFragment")
                    .commit();

        }else{
//            The user is not logged in, load the login Fragment....
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerMain, FragmentLogin.newInstance(),"loginFragment")
                    .commit();
        }
    }

    @Override
    public void populateMainFragment(FirebaseUser mUser) {
        this.currentUser = mUser;
        populateScreen();
    }
    @Override
    public void registerDone(FirebaseUser mUser) {
        this.currentUser = mUser;
        populateScreen();
    }
    @Override
    public void populateRegisterFragment() {
//            The user needs to create an account, load the register Fragment....
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerMain, FragmentRegister.newInstance(),"registerFragment")
                .commit();
    }

    @Override
    public void editButtonClickedFromRecyclerView(Friend friend) {
        FragmentMain fragmentMain = (FragmentMain) getSupportFragmentManager()
                .findFragmentByTag("mainFragment");
        fragmentMain.enableEdit(friend);
    }

    @Override
    public void deleteButtonPressedFromRecyclerView(String friendEmail) {
        FragmentMain fragmentMain = (FragmentMain) getSupportFragmentManager()
                .findFragmentByTag("mainFragment");
        fragmentMain.deleteUser(friendEmail);
    }

    @Override
    public void logoutPressed() {
        mAuth.signOut();
        currentUser = null;
        populateScreen();
    }

}

