package com.creativeshare.registerme.activities_fragments.activities.sign_in_sign_up_activity.fragments;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;


import com.creativeshare.registerme.R;
import com.creativeshare.registerme.activities_fragments.activities.sign_in_sign_up_activity.activity.Login_Activity;
import com.creativeshare.registerme.models.UserModel;
import com.creativeshare.registerme.preferences.Preferences;
import com.creativeshare.registerme.remote.Api;
import com.creativeshare.registerme.share.Common;
import com.creativeshare.registerme.tags.Tags;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.listeners.OnCountryPickerListener;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Fragment_Signup extends Fragment implements OnCountryPickerListener {


    private ImageView image_back, image_phone_code;
    private EditText edt_name, edt_phone, edt_email;
    private SegmentedButtonGroup segmentedButtonGroup;
    private TextView tv_code;
    private Button btn_sign_up;
    private CountryPicker picker;
    private Login_Activity activity;
    private String current_language;
    private String code = "";
    private int gender=0;
    private Preferences preferences;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String id;
    private String vercode;
    private FirebaseAuth mAuth;
    private Dialog dialog;

    public static Fragment_Signup newInstance() {
        return new Fragment_Signup();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        initView(view);
        authn();
        return view;
    }
    private void authn() {

        mAuth= FirebaseAuth.getInstance();
        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.e("id",s);
                id=s;
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//                Log.e("code",phoneAuthCredential.getSmsCode());
//phoneAuthCredential.getProvider();
                if(phoneAuthCredential.getSmsCode()!=null){
                    code=phoneAuthCredential.getSmsCode();
                  //  edt_confirm_code.setText(code);
                    verfiycode(code);}


            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("llll",e.getMessage());
            }
        };

    }
    private void verfiycode(String code) {
        Toast.makeText(activity,code,Toast.LENGTH_LONG).show();
        Log.e("ccc",code);
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(id,code);
        siginwithcredental(credential);
    }

    private void siginwithcredental(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.e("task",code);

            }
        });
    }

    private void sendverficationcode(String phone, String phone_code) {
        Log.e("kkk",phone_code+phone);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone_code+phone,59, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD,  mCallbacks);

    }
    private void initView(View view) {
        activity = (Login_Activity) getActivity();
        Paper.init(activity);
        current_language = Paper.book().read("lang", Locale.getDefault().getLanguage());

        image_back = view.findViewById(R.id.image_back);
        image_phone_code = view.findViewById(R.id.image_phone_code);

        if (current_language.equals("ar")) {
            image_back.setRotation(180.0f);
            image_phone_code.setRotation(180.0f);
        }


        edt_name = view.findViewById(R.id.edt_name);
        edt_phone = view.findViewById(R.id.edt_phone);
        tv_code = view.findViewById(R.id.tv_code);
        edt_email = view.findViewById(R.id.edt_email);
        segmentedButtonGroup=view.findViewById(R.id.segmentGroup);
        btn_sign_up = view.findViewById(R.id.btn_sign_up);

        CreateCountryDialog();

        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.Back();
            }
        });


        image_phone_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picker.showDialog(activity);
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });

segmentedButtonGroup.setOnClickedButtonListener(new SegmentedButtonGroup.OnClickedButtonListener() {
    @Override
    public void onClickedButton(int position) {
        if(position==0){
            gender=1;
        }
        else if(position==1){
            gender=2;
        }
    }
});
    }

    private void CreateCountryDialog() {
        CountryPicker.Builder builder = new CountryPicker.Builder()
                .canSearch(true)
                .with(activity)
                .listener(this);
        picker = builder.build();

        TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);


        if (picker.getCountryFromSIM() != null) {
            updateUi(picker.getCountryFromSIM());

        } else if (telephonyManager != null && picker.getCountryByISO(telephonyManager.getNetworkCountryIso()) != null) {
            updateUi(picker.getCountryByISO(telephonyManager.getNetworkCountryIso()));


        } else if (picker.getCountryByLocale(Locale.getDefault()) != null) {
            updateUi(picker.getCountryByLocale(Locale.getDefault()));

        } else {
            tv_code.setText("+966");
            code = "+966";
        }


    }

    @Override
    public void onSelectCountry(Country country) {
        updateUi(country);
    }

    private void updateUi(Country country) {

        tv_code.setText(country.getDialCode());
        code = country.getDialCode();


    }

    private void checkData() {

        String m_name = edt_name.getText().toString().trim();
        String m_phone = edt_phone.getText().toString().trim();
        String m_email = edt_email.getText().toString().trim();

        if (!TextUtils.isEmpty(m_name) &&
                !TextUtils.isEmpty(m_phone) &&
               // !TextUtils.isEmpty(m_email) &&
                //Patterns.EMAIL_ADDRESS.matcher(m_email).matches() &&
                ( TextUtils.isEmpty(m_email)||(!TextUtils.isEmpty(m_email) &&
                        Patterns.EMAIL_ADDRESS.matcher(m_email).matches()))&&
        !TextUtils.isEmpty(code)
                &&gender!=0

        ) {
            Common.CloseKeyBoard(activity, edt_name);
            edt_name.setError(null);
            edt_phone.setError(null);
            edt_email.setError(null);
            //edt_password.setError(null);

            sign_up(m_name, code, m_phone, m_email);
        } else {
            if(gender==0){
                Toast.makeText(activity,activity.getResources().getString(R.string.choose_gender),Toast.LENGTH_LONG).show();
            }
            if (TextUtils.isEmpty(m_name)) {
                edt_name.setError(getString(R.string.field_req));
            } else {
                edt_name.setError(null);

            }


            if (TextUtils.isEmpty(m_phone)) {
                edt_phone.setError(getString(R.string.field_req));
            } else {
                edt_phone.setError(null);

            }


           if (!TextUtils.isEmpty(m_email)&&!Patterns.EMAIL_ADDRESS.matcher(m_email).matches()) {
                edt_email.setError(getString(R.string.inv_email));

            } else {
                edt_email.setError(null);

            }




            if (TextUtils.isEmpty(code)) {
                tv_code.setError(getString(R.string.field_req));
            } else {
                tv_code.setError(null);

            }

        }

    }

    private void sign_up(String m_name, String code, String m_phone, String m_email) {
        final ProgressDialog dialog = Common.createProgressDialog(activity,getString(R.string.wait));
        dialog.setCancelable(false);
        dialog.show();
       Api.getService(Tags.base_url)
                .Signup(m_name,m_phone,code.replace("+","00"),m_email,gender)
                .enqueue(new Callback<UserModel>() {
                    @Override
                    public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                        dialog.dismiss();
                        if (response.isSuccessful()&&response.body()!=null) {
                           CreateSignAlertDialog();
                           // preferences = Preferences.getInstance();
                            //preferences.create_update_userdata(activity,response.body());
                           // activity.NavigateToHomeActivity();
                        } else if (response.code() == 422) {
                                Common.CreateSignAlertDialog(activity,getString(R.string.email_exists));
                        } else {

                            try {
                                Log.e("Error_code",response.code()+"_"+response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<UserModel> call, Throwable t) {
                        try {
                            dialog.dismiss();
                            Toast.makeText(activity,getString(R.string.something), Toast.LENGTH_SHORT).show();
                            Log.e("Error",t.getMessage());
                        } catch (Exception e) {
                        }
                    }
                });
    }
    public void CreateSignAlertDialog() {
       dialog = new Dialog(activity, R.style.CustomAlertDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_login);
        LinearLayout ll = dialog.findViewById(R.id.ll);

        ll.setBackgroundResource(R.drawable.custom_bg_login);
        dialog.show();
    }



}
