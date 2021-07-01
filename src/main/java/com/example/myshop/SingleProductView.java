package com.example.myshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myshop.InternetUtility.NetworkChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleProductView extends AppCompatActivity {

    //Variables
    private TextView orderTitle, purchaseNumber, purchaseName, totalProduct;
    private ListView singleProductsList;
    private Button markPacked;

    private ArrayList<String> productNameList = new ArrayList<>();
    private ArrayList<String> orderQtyList = new ArrayList<>();
    private ArrayList<String> qtyPackedList = new ArrayList<>();
    private ArrayList<String> caseList = new ArrayList<>();
    private ArrayList<String> lansList = new ArrayList<>();

    private NetworkChangeListener networkChangeListener = new NetworkChangeListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_product_view);

        //Setting the Hooks
        orderTitle = findViewById(R.id.sp_ot);
        purchaseNumber = findViewById(R.id.sp_po);
        purchaseName = findViewById(R.id.sp_sply);
        totalProduct = findViewById(R.id.sp_tp);
        singleProductsList = findViewById(R.id.sp_pl);
        markPacked = findViewById(R.id.sp_btn_pkd);

        getInformation();

        CustomAdapter customAdapter = new CustomAdapter();
        singleProductsList.setAdapter(customAdapter);

        //Setting the onClickListener on the markPacked button that calls the markPacked() method
        markPacked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markPacked();
            }
        });
    }

    private void getInformation() {
        String orderNumber = getIntent().getStringExtra("purchase-number");
        String orderName = getIntent().getStringExtra("sply-number");
        int productCount = getIntent().getIntExtra("product-count", 0);
        ArrayList<String> productInfo = getIntent().getStringArrayListExtra("product-info");

        orderTitle.setText("Order #" + orderNumber);
        purchaseNumber.setText("#" + orderNumber);
        purchaseName.setText(orderName.toUpperCase());
        totalProduct.setText(String.valueOf(productCount));

        for (String products : productInfo) {

            String productKey = products;
            String[] getProductInfo = productKey.split(":");


            String productName = getProductInfo[0];
            String caseNumber = getProductInfo[1];
            String qtyOrdered = getProductInfo[2];
            String qtyPacked = getProductInfo[3];
            String productLans = getProductInfo[4];

            productNameList.add(productName);
            orderQtyList.add(qtyOrdered);
            qtyPackedList.add(qtyPacked);
            caseList.add(caseNumber);
            lansList.add(productLans);
        }
    }

    private void markPacked() {
        new UpdateProduct().execute();
    }

    private class CustomAdapter extends BaseAdapter {

        public CustomAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return productNameList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.single_product_list, null);

            TextView productName = view.findViewById(R.id.spl_pn);
            TextView orderQty = view.findViewById(R.id.spl_oq);
            EditText qtyPacked = view.findViewById(R.id.spl_qp);
            TextView caseNumber = view.findViewById(R.id.spl_case);
            EditText lans = view.findViewById(R.id.spl_pl);

            productName.setText(productNameList.get(position));
            orderQty.setText(orderQtyList.get(position));
            caseNumber.setText(caseList.get(position));
            if (qtyPackedList.size() > 0) {
                qtyPacked.setText(qtyPackedList.get(position));
                lans.setText(lansList.get(position));
            }

            //If product name contains 1000ml in keyword: set background color to aliceblue
            if(productNameList.get(position).contains("1000ml")){
                view.setBackgroundColor(Color.parseColor("#F0F8FF"));
            }

            //If product name contains 1125ml in the keyword: set background color to yellow yellow1
            if(productNameList.get(position).contains("1125ml")){
                view.setBackgroundColor(Color.parseColor("#FFFFCC"));
            }

            //If product contains Cans in the keyword: set background color to mistyrose
            if(productNameList.get(position).contains("Cans")){
                view.setBackgroundColor(Color.parseColor("#FFE4E1"));
            }


            qtyPacked.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (qtyPacked.getText().toString().length() > 0) { //Make sure the user entered some value..
                        qtyPackedList.set(position, qtyPacked.getText().toString());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            lans.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(lans.getText().toString().length() > 0){ //Make sure the user entered some value..
                        lansList.set(position, lans.getText().toString());
                    }
                }
            });
            return view;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public class UpdateProduct extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;
        String result = null;
        String orderNumber = getIntent().getStringExtra("purchase-number");

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(SingleProductView.this);
            dialog.setTitle("Hey! Wait Please..");
            dialog.setMessage("We are updating your shop order list.");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                String serverResponse = Controller.updateData(orderNumber, productNameList,
                        qtyPackedList, lansList);

                if (serverResponse != null) {
                    result = serverResponse;
                } else {
                    result = "An error occurred try again";
                }
            } catch (IOException e) {
                result = "An error occurred try again";
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.setMessage(result);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.setMessage("I am done! You will be redirected in the next 3s.");
                   new Handler().postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           dialog.dismiss();
                           startActivity(new Intent(getApplicationContext(), MainActivity.class));
                       }
                   }, 3000);
                }
            }, 10000);
        }
    }

    //Do this when the activity starts
    @Override
    protected void onStart() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeListener, filter);
        super.onStart();
    }

    //Do this when the activity stops
    @Override
    protected void onStop() {
        unregisterReceiver(networkChangeListener);
        super.onStop();
    }
}