package com.nvn.mobilent.screens.settings;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nvn.mobilent.R;
import com.nvn.mobilent.data.datalocal.DataLocalManager;
import com.nvn.mobilent.data.model.user.Info;
import com.nvn.mobilent.data.model.user.User;
import com.nvn.mobilent.data.api.UserAPI;
import com.nvn.mobilent.utils.AppUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ChangeInfoActivity extends AppCompatActivity {
    EditText firstName, lastName, birthday, address, phone, email;
    RadioButton rb1, rb2;
    Button btnChangeInfo;
    Toolbar toolbar;

    private TextInputLayout textInputLayoutFirstName;
    private TextInputLayout textInputLayoutLastName;
    private TextInputLayout textInputLayoutBirthDay;
    private TextInputLayout textInputLayoutAddress;
    private TextInputLayout textInputLayoutPhone;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutGender;

    private String regexName = "[aAàÀảẢãÃáÁạẠăĂằẰẳẲẵẴắẮặẶâÂầẦẩẨẫẪấẤậẬbBcCdDđĐeEèÈẻẺẽẼéÉẹẸêÊềỀểỂễỄếẾệỆ fFgGhHiIìÌỉỈĩĨíÍịỊjJkKlLmMnNoOòÒỏỎõÕóÓọỌôÔồỒổỔỗỖốỐộỘơƠờỜởỞỡỠớỚợỢpPqQrRsStTu UùÙủỦũŨúÚụỤưƯừỪửỬữỮứỨựỰvVwWxXyYỳỲỷỶỹỸýÝỵỴzZ]+";
    private String regexEmail = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";
    private String regexDate = "^(3[01]|[12][0-9]|0[1-9]|[1-9])/(1[0-2]|0[1-9]|[1-9])/[0-9]{4}$";
    private String regexPhone = "[0]{1}\\d{9}";

    private User user;

    public void onBackPressed() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);
        user = DataLocalManager.getUser();
        setControl();
        actionToolBar();
        loadDefault();
        catchData();
        setEvent();
    }

    String convertDate(String d) {
        String pattern = "(0?[1-9]|[1-2]\\d|3[0-1])/(0?[1-9]|1[0-2])/(19|20)\\d{2}";
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat f1 = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date();
        if (!d.matches(pattern)) {
            return "1";
        } else {
            f1.setLenient(false);
            try {
                date = f1.parse(d);
                return f.format(date);
            } catch (ParseException e) {
                System.out.println("Error fDate dd/MM/yyyy!");
            }
            return "1";
        }
    }

    String convertDateDB(String d) {
        String pattern = "(0?[1-9]|[1-2]\\d|3[0-1])/(0?[1-9]|1[0-2])/(19|20)\\d{2}";
        SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat f1 = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date();
        if (!d.matches(pattern)) {
            return "1";
        } else {
            f.setLenient(false);
            try {
                date = f.parse(d);
                return f1.format(date);
            } catch (ParseException e) {
                System.out.println("Error fDate!");
            }
            return "1";
        }
    }

    private void updateUser(User info){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collectionPath = "users";
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        Map<String, Object> updatedFields = new HashMap<>();
        updatedFields.put("email", info.getEmail());
        updatedFields.put("firstname", info.getFirstname());
        updatedFields.put("lastname", info.getLastname());
        updatedFields.put("address", info.getAddress());
        updatedFields.put("phone", info.getPhone());
        updatedFields.put("gender", info.getGender());
        updatedFields.put("birthday", info.getBirthday());

        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection(collectionPath)
                    .document(userId)
                    .update(updatedFields)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Update successful
                            // Perform any desired actions after updating the document
                            AppUtils.showToast_Short(getApplicationContext(), "Cập nhật thông tin thành công!");
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Update failed
                            // Handle the failure case
                            AppUtils.showToast_Short(getApplicationContext(), "Lỗi cập nhật thông tin rồi");
                        }
                    });


        }

    }

    private void setEvent() {

        btnChangeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkData()) {
                    int gender = rb1.isChecked() ? 0 : 1;
                    Info info = new Info(
                            user.getId(),
                            email.getText().toString().trim(),
                            firstName.getText().toString().trim(),
                            lastName.getText().toString().trim(),
                            address.getText().toString().trim(),
                            phone.getText().toString().trim(),
                            gender,
                            birthday.getText().toString().trim()
                    );


                    if (!AppUtils.haveNetworkConnection(getApplicationContext())) {
                        AppUtils.showToast_Short(getApplicationContext(), "Kiểm tra lại kết nối Internet");
                    } else {
                        user.setEmail(info.getEmail());
                        user.setFirstname(info.getFirstname());
                        user.setLastname(info.getLastname());
                        user.setAddress(info.getAddress());
                        user.setPhone(info.getPhone());
                        user.setGender(info.getGender());
                        user.setBirthday(info.getBirthday());
                        DataLocalManager.setUser(user);

                        updateUser(user);

                        loadDefault();
                    }
                }
            }
        });
    }

    private void loadDefault() {
        firstName.setText(user.getFirstname());
        lastName.setText(user.getLastname());
        birthday.setText(user.getBirthday());
        address.setText(user.getAddress());
        phone.setText(user.getPhone());
        email.setText(user.getEmail());
        if (user.getGender() == 0) {
            rb1.setChecked(true);
            rb2.setChecked(false);
        } else {
            rb2.setChecked(true);
            ;
            rb1.setChecked(false);
        }
    }

    private void setControl() {
        toolbar = findViewById(R.id.toolbar_changeinfo);
        firstName = findViewById(R.id.changefname);
        lastName = findViewById(R.id.changelname);
        birthday = findViewById(R.id.changebirthday);
        address = findViewById(R.id.changeaddress);
        phone = findViewById(R.id.changephone);
        email = findViewById(R.id.changeemail);

        rb1 = findViewById(R.id.c_rbnam);
        rb2 = findViewById(R.id.c_rbnu);
        btnChangeInfo = findViewById(R.id.btnthaydoithongtin);

        textInputLayoutFirstName = findViewById(R.id.til_changefname);
        textInputLayoutLastName = findViewById(R.id.til_changelname);
        textInputLayoutBirthDay = findViewById(R.id.til_changebirthday);
        textInputLayoutAddress = findViewById(R.id.til_changeaddress);
        textInputLayoutPhone = findViewById(R.id.til_changephone);
        textInputLayoutEmail = findViewById(R.id.til_changeemail);
        textInputLayoutGender = findViewById(R.id.til_changegender);
    }

    private void actionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //tạo nút home
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean checkData() {
        boolean check = true;
        if (firstName.getText().toString().trim().equals("")) {
            textInputLayoutFirstName.setError("Không được để trống!");
            return false;
        }
        if (lastName.getText().toString().trim().equals("")) {
            textInputLayoutLastName.setError("Không được để trống!");
            return false;
        }
        if (!rb1.isChecked() && !rb2.isChecked()) {
            textInputLayoutGender.setError("Không được để trống!");
            return false;
        } else {
            textInputLayoutGender.setError(null);
        }
        if (birthday.getText().toString().trim().equals("")) {
            textInputLayoutBirthDay.setError("Không được để trống!");
            return false;
        }
        if (address.getText().toString().trim().equals("")) {
            textInputLayoutAddress.setError("Không được để trống!");
            return false;
        }
        if (phone.getText().toString().trim().equals("")) {
            textInputLayoutPhone.setError("Không được để trống!");
            return false;
        }
        if (email.getText().toString().trim().equals("")) {
            textInputLayoutEmail.setError("Không được để trống!");
            return false;
        }

        // Check TextInput null
        if (!(textInputLayoutFirstName.getError() == null)) {
            return false;
        }
        if (!(textInputLayoutLastName.getError() == null)) {
            return false;
        }
        if (!(textInputLayoutGender.getError() == null)) {
            return false;
        }
        if (!(textInputLayoutBirthDay.getError() == null)) {
            return false;
        }
        if (!(textInputLayoutAddress.getError() == null)) {
            return false;
        }
        if (!(textInputLayoutPhone.getError() == null)) {
            return false;
        }
        if (!(textInputLayoutEmail.getError() == null)) {
            return false;
        }

        return check;
    }

    private void catchBirthDay() {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(ChangeInfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1; // 0-11
                        String date = day + "/" + month + "/" + year;
                        birthday.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        birthday.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && !charSequence.toString().matches(regexDate)) {
                    textInputLayoutBirthDay.setError("Định dạng dd/MM/yyyy");
                } else {
                    textInputLayoutBirthDay.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void catchData() {
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && !charSequence.toString().matches(regexName)) {
                    textInputLayoutFirstName.setError("Chỉ dùng các chữ cái");
                } else {
                    textInputLayoutFirstName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        lastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && !charSequence.toString().matches(regexName)) {
                    textInputLayoutLastName.setError("Chỉ dùng các chữ cái");
                } else {
                    textInputLayoutLastName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        catchBirthDay();
        // Address chữ và số
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && !charSequence.toString().matches(regexPhone)) {
                    textInputLayoutPhone.setError("Số điện thoại gồm 10 số và bắt đầu bằng 0");
                } else {
                    textInputLayoutPhone.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty() && !charSequence.toString().matches(regexEmail)) {
                    textInputLayoutEmail.setError("Email có định dạng username@abc.com");
                } else {
                    textInputLayoutEmail.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

}