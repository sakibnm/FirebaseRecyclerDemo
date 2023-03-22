package space.sakibnm.recyclerviewdemo.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import space.sakibnm.recyclerviewdemo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRegister#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegister extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private EditText editTextName, editTextEmail, editTextPassword, editTextRepPassword;
    private Button buttonRegister;
    private String name, email, password, rep_password;
    private IregisterFragmentAction mListener;


    public FragmentRegister() {
        // Required empty public constructor
    }

    public static FragmentRegister newInstance() {
        FragmentRegister fragment = new FragmentRegister();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IregisterFragmentAction){
            this.mListener = (IregisterFragmentAction) context;
        }else{
            throw new RuntimeException(context.toString()
                    + "must implement RegisterRquest");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        editTextName = rootView.findViewById(R.id.editTextRegister_Name);
        editTextEmail = rootView.findViewById(R.id.editTextRegister_Email);
        editTextPassword = rootView.findViewById(R.id.editTextRegister_Password);
        editTextRepPassword = rootView.findViewById(R.id.editTextRegister_Rep_Password);
        buttonRegister = rootView.findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        this.name = String.valueOf(editTextName.getText()).trim();
        this.email = String.valueOf(editTextEmail.getText()).trim();
        this.password = String.valueOf(editTextPassword.getText()).trim();
        this.rep_password = String.valueOf(editTextRepPassword.getText()).trim();

        if(view.getId()== R.id.buttonRegister){
//            Validations........
            if(name.equals("")){
                editTextName.setError("Must input email!");
            }
            if(email.equals("")){
                editTextEmail.setError("Must input email!");
            }
            if(password.equals("")){
                editTextPassword.setError("Password must not be empty!");
            }
            if(!rep_password.equals(password)){
                editTextRepPassword.setError("Passwords must match!");
            }

//            Validation complete.....
            if(!name.equals("") && !email.equals("")
                    && !password.equals("")
                    && rep_password.equals(password)){

                //              Firebase authentication: Create user.......
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    mUser = task.getResult().getUser();

//                                    Adding name to the FirebaseUser...
                                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                                    mUser.updateProfile(profileChangeRequest)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        mListener.registerDone(mUser);
                                                    }
                                                }
                                            });

                                }
                            }
                        });
            }
        }
    }

    public interface IregisterFragmentAction {
        void registerDone(FirebaseUser mUser);
    }
}