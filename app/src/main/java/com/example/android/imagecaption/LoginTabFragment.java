package com.example.android.imagecaption;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginTabFragment extends Fragment {

    private EditText editEmail;
    private EditText editPassword;
    private Button loginButton;
    private TextView resetPassword;

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

        editEmail = view.findViewById(R.id.email);
        editPassword=view.findViewById(R.id.password);
        loginButton=view.findViewById(R.id.loginButton);
        resetPassword=view.findViewById(R.id.resetPassword);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail = editEmail.getText().toString();
                String textPwd = editPassword.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(getActivity() , "please enter your email", Toast.LENGTH_LONG).show();
                    editEmail.setError("email is required");
                    editEmail.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(getActivity() , "please enter your Password", Toast.LENGTH_LONG).show();
                    editPassword.setError("Password is required");
                    editPassword.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(getActivity() , "please enter a valid email address",Toast.LENGTH_LONG).show();
                    editEmail.setError("email format is incorrect");
                    editEmail.requestFocus();

                    //noe we clear what is inside the email and give a fresh one
                    editEmail.clearComposingText();
                }else if(textPwd.length()<6){
                    Toast.makeText(getActivity() , "password must be atleast 6 character long",Toast.LENGTH_LONG).show();
                    editPassword.setError("email format is incorrect");
                    editPassword.requestFocus();

                    editPassword.clearComposingText();
                }else{
                    //viewModel.logIn(textEmail,textPwd);
                    loginUser(textEmail,textPwd);

                }

            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText resetMail = new EditText(view.getContext());
                resetMail.setHint("Enter Email");
                resetMail.setSingleLine();
                FrameLayout container = new FrameLayout(getActivity());
                FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = 20;
                resetMail.setLayoutParams(params);
                container.addView(resetMail);
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("A password reset email will be sent to you");
                passwordResetDialog.setView(container);


                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String mail = resetMail.getText().toString();
                        auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(), "Reset Link sent to your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error! Reset Link is not sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                passwordResetDialog.create().show();

            }
        });
    }

    private void loginUser(String textEmail, String textPwd) {
        auth.signInWithEmailAndPassword(textEmail ,textPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getActivity() , "User successfully logged in 🥳" ,Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity() , MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }else{
                    Toast.makeText(getActivity(), "Something Went Wrong",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab_fragment, container, false);
        return root;
    }
}
