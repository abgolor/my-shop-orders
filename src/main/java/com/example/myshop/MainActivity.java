package com.example.myshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myshop.InternetUtility.NetworkChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Filterable {

    //Assigning variables
    private EditText searchText;
    private ListView ordersList;

    ProgressDialog loadingDialog;

    private String previousOrderNumber;
    private String previousOrderName;
    private String previousAction;

    private SwipeRefreshLayout refreshOrder;

    private ImageView settings;


    private ArrayList<String> orderList = new ArrayList<>();
    private Map<String, ArrayList<String>> productMap = new HashMap<>();

    //Copied ArrayList
    private ArrayList<String> copiedOrderList = new ArrayList<>();

    NetworkChangeListener networkChangeListener = new NetworkChangeListener();
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assigning view hooks
        ordersList = findViewById(R.id.orders_list);
        searchText = findViewById(R.id.search_edText);
        refreshOrder = findViewById(R.id.refreshOrder);

        customAdapter = new CustomAdapter();

        settings = findViewById(R.id.settings);

        //When the user clicks on the setting button at the top right corner
        settings.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
            startActivity(intent);
        });

        //Fetch the My Shop Orders.
        getMyShopOrders();

        //What happens when the user search for items in the search box
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //What happens when the user pull to refresh
        refreshOrder.setOnRefreshListener(() -> {
            getMyShopOrders();
            refreshOrder.setRefreshing(false);
        });

        //What happens when the user click on the enter button in their keyboard
        searchText.setImeOptions(EditorInfo.IME_ACTION_GO);
    }


    //Method to get My Shop Orders.
    private void getMyShopOrders(){
        new FetchShopOrders().execute();
    }

    //Get the filtered orders depending on the user search query
    @Override
    public Filter getFilter() {
        return orderFilter;
    }

    //Filter the My Shop orders to enable searching
    private Filter orderFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<String> filteredOrderList = new ArrayList<>();

            String filteredText = constraint.toString().toLowerCase().trim();

            for (String order : copiedOrderList) {
                String[] key = order.split(":");

                String purchaseNumber = key[0];
                String orderName = key[1];

                if (purchaseNumber.toLowerCase().startsWith(filteredText) || orderName.toLowerCase().startsWith(filteredText)) {
                    filteredOrderList.add(order);
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredOrderList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            orderList.clear();
            orderList.addAll((ArrayList) results.values);
            customAdapter.notifyDataSetChanged();
        }
    };

    //Custom Adapter for customizing the list view
    private class CustomAdapter extends BaseAdapter {

        public CustomAdapter() {
            super();
        }

        @Override
        public int getCount() {
            return orderList.size();
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

            View view = getLayoutInflater().inflate(R.layout.list_items, null);

            TextView orderName = view.findViewById(R.id.orderName);
            TextView orderNumber = view.findViewById(R.id.orderNumber);
            TextView productsCount = view.findViewById(R.id.productCounts);
            ImageView packedStatus = view.findViewById(R.id.packedStatus);

            String key = orderList.get(position);

            String[] arrayOfString = key.split(":");

            orderNumber.setText(arrayOfString[0]);
            orderName.setText(arrayOfString[1]);

            if(!arrayOfString[2].equals("Unpacked")){
                packedStatus.setImageResource(R.drawable.packed);
            } else {
                packedStatus.setImageResource(R.drawable.my_shop_package_unpacked);
            }

            productsCount.setText(String.valueOf(productMap.get(key).size()));

            return view;
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

    //Fetch the shop order from the spreadsheet link using: OkHttp
    public class FetchShopOrders extends AsyncTask<Void, Void, Void> {

        JSONObject data = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingDialog = ProgressDialog.show(MainActivity.this, "Loading", "Fetching orders...", false, false);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            data = Controller.readAllData();
            return null;
        }

        private void parseItem(JSONObject data) {

            try {
                JSONObject jsonObject = data;
                JSONArray jsonArray = jsonObject.getJSONArray("items");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    String purchaseNumber = jsonObject1.getString("PO#");
                    String action = jsonObject1.getString("ACTIONS");
                    String orderName = jsonObject1.getString("SPLY#");
                    String product = jsonObject1.getString("PRODUCT");
                    String caseNumber = jsonObject1.getString("CASE");
                    String lans = jsonObject1.getString("Lans");
                    String quantityOrder = jsonObject1.getString("ORDER_QTY");
                    String quantityPacked = jsonObject1.getString("QTY_Packed");

                    if (lans.equals("")) {
                        lans = " ";
                    }

                    if (quantityPacked.equals("")) {
                        quantityPacked = " ";
                    }

                    if(action.equals("")){
                        action = "Unpacked";
                    }

                    if (!purchaseNumber.equals("PO#") && !orderName.equals("SPLY#") && !product.equals("")) {
                        if (!purchaseNumber.equals("") && !orderName.equals("") && !product.equals("")) {

                            productMap.put(purchaseNumber + ":" + orderName + ":" + action, new ArrayList<>());
                            previousOrderName = orderName;
                            previousOrderNumber = purchaseNumber;
                            previousAction = action;
                            orderList.add(purchaseNumber + ":" + orderName + ":" + action);

                            productMap.get(purchaseNumber + ":" + orderName + ":" + action).add(product + ":" + caseNumber + ":" + quantityOrder + ":" + quantityPacked + ":" + lans);

                        } else if (purchaseNumber.equals("") && orderName.equals("")
                                && !product.equals("")) {
                            productMap.get(previousOrderNumber + ":" + previousOrderName + ":" + previousAction).add(product + ":" + caseNumber + ":" + quantityOrder + ":" + quantityPacked + ":" + lans);
                        }
                    }
                }

                copiedOrderList.addAll(orderList);

                ordersList.setAdapter(customAdapter);
                loadingDialog.dismiss();

                ordersList.setOnItemClickListener((parent, view, position, id) -> {

                    Intent intent = new Intent(getApplicationContext(), SingleProductView.class);

                    String key = orderList.get(position);
                    String[] getOrderInfo = key.split(":");

                    String purchaseNumber = getOrderInfo[0];
                    String splyNumber = getOrderInfo[1];
                    int numberOfProducts = productMap.get(key).size();

                    ArrayList<String> productInfo = productMap.get(orderList.get(position));

                    intent.putExtra("purchase-number", purchaseNumber);
                    intent.putExtra("sply-number", splyNumber);
                    intent.putExtra("product-info", productInfo);
                    intent.putExtra("product-count", numberOfProducts);

                    startActivity(intent);
                });
            } catch (JSONException e) {
                loadingDialog.setMessage("An error occurred while getting your My Shop Orders");
                loadingDialog.dismiss();
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(data != null){
                parseItem(data);
            } else {
                loadingDialog.setMessage("An error occurred while getting your My Shop Orders");
                loadingDialog.dismiss();
            }
        }
    }

    //WHen the user clicks on the back button
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Important!")
                .setMessage("Are you sure you want to exit the application?")
                .setPositiveButton("LEAVE NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do Nothing: The user rejected just close the dialog
                    }
                });
        alertBuilder.show();
    }
}