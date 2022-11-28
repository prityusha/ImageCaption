package com.example.android.imagecaption;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupTabFragment extends Fragment {

    private EditText editName;
    private EditText editEmail;
    //private EditText editOrganizationName;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private Button signUpButton;

    private FirebaseAuth auth;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        /*viewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication())).get(AuthViewModel.class);

        viewModel.getUserData().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if(firebaseUser!=null){
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                }
            }
        });*/

        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(getActivity() , MainActivity.class));
            getActivity().finish();
            //start the main activity
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editName = view.findViewById(R.id.name);
        editEmail = view.findViewById(R.id.email);
        //editOrganizationName = view.findViewById(R.id.college);
        editPassword = view.findViewById(R.id.password);
        editConfirmPassword = view.findViewById(R.id.confirmpassword);
        signUpButton = view.findViewById(R.id.signUp);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=editName.getText().toString();
                String email = editEmail.getText().toString();
                //String organizationName=editOrganizationName.getText().toString();
                String password=editPassword.getText().toString();
                String confirmPassword=editConfirmPassword.getText().toString();

                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getActivity() , "please enter the full name", Toast.LENGTH_LONG).show();
                    editName.setError("full name is required");
                    editName.requestFocus();
                }else if(TextUtils.isEmpty(email)){
                    Toast.makeText(getActivity() , "please enter the Email", Toast.LENGTH_LONG).show();
                    editEmail.setError("Email is required");
                    editEmail.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(getActivity() , "please re-enter the Email", Toast.LENGTH_LONG).show();
                    editEmail.setError("Valid Email is required");
                    editEmail.requestFocus();
                }else if(TextUtils.isEmpty(password)) {
                    Toast.makeText(getActivity(), "please enter the Password", Toast.LENGTH_LONG).show();
                    editPassword.setError("password is required");
                    editPassword.requestFocus();
                }else if(password.length()<6) {
                    Toast.makeText(getActivity(), "password length should be atleast 6", Toast.LENGTH_LONG).show();
                    editPassword.setError("password too weak");
                    editPassword.requestFocus();
                }else if(TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(getActivity(), "please enter the Password", Toast.LENGTH_LONG).show();
                    editConfirmPassword.setError("password is required");
                    editConfirmPassword.requestFocus();
                }else if(!password.equals(confirmPassword)) {
                    Toast.makeText(getActivity(), "please enter the same Password", Toast.LENGTH_LONG).show();
                    editConfirmPassword.setError("password must be same");
                    editConfirmPassword.requestFocus();

                    //clearing the previous password

                    editPassword.clearComposingText();
                    editConfirmPassword.clearComposingText();
                }
                /*else if(TextUtils.isEmpty(organizationName)){
                    Toast.makeText(getActivity() , "please enter the name of your College/Organization", Toast.LENGTH_LONG).show();
                    editName.setError("College/Organization name is required");
                    editName.requestFocus();
                }*/
                else {
                    //viewModel.registerUser(email,password);
                    registerUser(email,password);
                }

            }
        });
    }

    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(getActivity() , MainActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    getActivity().finish();
                }
                else{
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);
        return root;
    }
}
