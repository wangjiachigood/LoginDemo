package com.pb.joindata.logindemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mUserName;
    private EditText mPassword;
    private Button mLoginButton;
    private ImageButton mDropDown;
    private DbHelper dbHelper;
    private CheckBox mCheckBox;
    private PopupWindow popView;
    private MyAdapter dropDownAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
    }

    private void initWidget() {
        dbHelper = new DbHelper(this);
        mUserName = (EditText) findViewById(R.id.username);
        mPassword = (EditText) findViewById(R.id.password);
        mLoginButton = (Button) findViewById(R.id.login);
        mDropDown = (ImageButton) findViewById(R.id.dropdown_button);
        mCheckBox = (CheckBox) findViewById(R.id.remember);
        mLoginButton.setOnClickListener(this);
        mDropDown.setOnClickListener(this);
        initLoginUserName();
    }

    private void initLoginUserName() {
        String[] usernames = dbHelper.queryAllUserName();
        if (usernames.length > 0) {
            String tempName = usernames[usernames.length - 1];
            mUserName.setText(tempName);
            mUserName.setSelection(tempName.length());
            String tempPwd = dbHelper.queryPasswordByName(tempName);
            int checkFlag = dbHelper.queryIsSavedByName(tempName);
            if (checkFlag == 0) {
                mCheckBox.setChecked(false);
            } else if (checkFlag == 1) {
                mCheckBox.setChecked(true);
            }
            mPassword.setText(tempPwd);
        }
        mUserName.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                mPassword.setText("");
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                String userName = mUserName.getText().toString();
                String password = mPassword.getText().toString();
                if (mCheckBox.isChecked()) {
                    dbHelper.insertOrUpdate(userName, password, 1);
                } else {
                    dbHelper.insertOrUpdate(userName, "", 0);
                }
                Toast.makeText(this, "记录已经保存", Toast.LENGTH_LONG).show();
                break;
            case R.id.dropdown_button:
                if (popView != null) {
                    if (!popView.isShowing()) {
                        popView.showAsDropDown(mUserName);
                    } else {
                        popView.dismiss();
                    }
                } else {
                    // 如果有已经登录过账号
                    if (dbHelper.queryAllUserName().length > 0) {
                        initPopView(dbHelper.queryAllUserName());
                        if (!popView.isShowing()) {
                            popView.showAsDropDown(mUserName);
                        } else {
                            popView.dismiss();
                        }
                    } else {
                        Toast.makeText(this, "无记录", Toast.LENGTH_LONG).show();
                    }

                }
                break;
        }
    }

    private void initPopView(String[] usernames) {
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < usernames.length; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("name", usernames[i]);
            map.put("drawable", R.drawable.edit_delete_pressed_icon_for_login);
            list.add(map);
        }
        dropDownAdapter = new MyAdapter(this, list, R.layout.dropdown_item,
                new String[] { "name", "drawable" }, new int[] { R.id.textview,
                R.id.delete });
        ListView listView = new ListView(this);
        listView.setAdapter(dropDownAdapter);

        popView = new PopupWindow(listView, mUserName.getWidth(),
                ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popView.setFocusable(true);
        popView.setOutsideTouchable(true);
        popView.setBackgroundDrawable(getResources().getDrawable(R.color.colorAccent));
        // popView.showAsDropDown(mUserName);
    }

    class MyAdapter extends SimpleAdapter {

        private List<HashMap<String, Object>> data;

        public MyAdapter(Context context, List<HashMap<String, Object>> data,
                         int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
            this.data = data;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            System.out.println(position);
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(
                        R.layout.dropdown_item, null);
                holder.btn = (ImageButton) convertView
                        .findViewById(R.id.delete);
                holder.tv = (TextView) convertView.findViewById(R.id.textview);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tv.setText(data.get(position).get("name").toString());
            holder.tv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String[] usernames = dbHelper.queryAllUserName();
                    mUserName.setText(usernames[position]);
                    mPassword.setText(dbHelper
                            .queryPasswordByName(usernames[position]));
                    popView.dismiss();
                }
            });
            holder.btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    String[] usernames = dbHelper.queryAllUserName();
                    if (usernames.length > 0) {
                        dbHelper.delete(usernames[position]);
                    }
                    String[] newusernames = dbHelper.queryAllUserName();
                    if (newusernames.length > 0) {
                        initPopView(newusernames);
                        popView.showAsDropDown(mUserName);
                    } else {
                        popView.dismiss();
                        popView = null;
                    }
                }
            });
            return convertView;
        }
    }

    class ViewHolder {
        private TextView tv;
        private ImageButton btn;
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbHelper.cleanup();
    }


}
