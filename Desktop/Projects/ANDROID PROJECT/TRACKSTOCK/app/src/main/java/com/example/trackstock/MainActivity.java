package com.example.trackstock;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;

import android.view.Gravity;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static com.example.trackstock.DatabaseHelper.DATABASE_NAME;


public class MainActivity extends AppCompatActivity {

    SearchView searchView;
    RecyclerView homeRecyclerView;
    HomeAdapter homeAdapter;
    List<MyItem> myItems;

    TableLayout tableLayout;
    SpinnerDialog spinnerDialog;
    List<MyItem> myTransactionItem;

    Uri imageURI;
    byte[] pic;
    String name,stock,price;
    ImageView uploadImageView;
    EditText itemName,itemStock,itemPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1); }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        homeItemList();
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragement=new HomeFragment();
                    switch (menuItem.getItemId()){
                        case R.id.nav_add:
                            selectedFragement = new AddFragment();
                            break;
                        case R.id.nav_transaction:
                            transactionCall();
                            selectedFragement = new TransactionFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragement).commit();
                    return true;
                }
            };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_grid_view:
                viewGrid();
                Toast.makeText(getBaseContext(),"GRID VIEW",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_list_view:
                viewList();
                Toast.makeText(getBaseContext(),"LIST VIEW",Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_import:
                importDB();
                break;
            case R.id.action_export:
                exportDB();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(MyItemView.FLAG==true){
            MyItemView.FLAG=false;
            homeItemList();
        }
    }

    public void homeItemList(){
        findViewById(R.id.nav_home).performClick();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
    }

    public void uploadImage(View view){
        Intent gallery = new Intent();
        gallery.setType("image/");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery,"Select Picture"),1);
    }

    public void registerItem(final View view){
        initializeItem();
        if(!validiateItem()){
            Toast.makeText(view.getContext(),"Registration Failed",Toast.LENGTH_SHORT).show();
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Register Item");
            alert.setMessage("Confirm Register Item?");
            alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(price.isEmpty()) price="0";
                    DatabaseHelper db = new DatabaseHelper(view.getContext());
                    pic = imageViewToByte(uploadImageView);
                    MyItem myItem = new MyItem(0,name,Integer.parseInt(price),Integer.parseInt(stock),pic);
                    db.registerItem(myItem);
                    Toast.makeText(view.getContext(),"Registration Success",Toast.LENGTH_SHORT).show();
                    findViewById(R.id.nav_home).performClick();
                }
            });
            alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alert.show();
        }
    }

    public void cancelRegister(View view){
        homeItemList();
    }

    private byte[] imageViewToByte(ImageView imgView) {
        Bitmap bitmap = ((BitmapDrawable) imgView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,stream);
        byte[] bytes = stream.toByteArray();
        return  bytes;
    }

    private boolean validiateItem() {
        boolean valid = true;
        if(name.isEmpty()){
            itemName.setError("Please Enter Valid Item name");
            valid = false;
        }
        if(stock.isEmpty()){
            itemStock.setError("Please Enter Valid Stock");
            valid = false;
        }
        return valid;
    }

    private void initializeItem() {
        itemName = findViewById(R.id.register_item_name);
        itemStock = findViewById(R.id.register_item_stock);
        itemPrice = findViewById(R.id.register_item_price);
        uploadImageView = findViewById(R.id.register_item_image);
        name = itemName.getText().toString().trim();
        price = itemPrice.getText().toString().trim();
        stock = itemStock.getText().toString().trim();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode== RESULT_OK){
            imageURI = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageURI);
                uploadImageView = findViewById(R.id.register_item_image);
                uploadImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void transactionCall(){
        myItems = new ArrayList<>();
        myTransactionItem = new ArrayList<>();
        ArrayList<String> items = new ArrayList<>();
        DatabaseHelper db = new DatabaseHelper(MainActivity.this);
        Cursor cursor = db.getMyItem();
        cursor.moveToFirst();
        if (cursor.moveToFirst()) {
            do {
                myItems.add(new MyItem(cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3),cursor.getBlob(4)));
            } while (cursor.moveToNext());
        }
        for(int i=0;i<myItems.size();i++){
            MyItem myItem = myItems.get(i);
            items.add(myItem.getItemName());
        }

        spinnerDialog=new SpinnerDialog(MainActivity.this,items,"Select or Search Item",R.style.DialogAnimations,"Close");// With 	Animation

        spinnerDialog.setCancellable(true); // for cancellable
        spinnerDialog.setShowKeyboard(false);//

        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String s, int i) {
                MyItem myItem = myItems.get(i);
                myTransactionItem.add(myItem);
                updateTransactionTable(myItem);
            }
        });


    }

    public void updateTransactionTable(MyItem myItem){
        tableLayout = findViewById(R.id.transaction_table);
        tableLayout.setStretchAllColumns(true);

        for(int i=1;i<tableLayout.getChildCount();i++){
            TableRow tr = (TableRow) tableLayout.getChildAt(i);
            if(myItem.getId()==tr.getId()){
                Toast.makeText(getBaseContext(),"Already Exists",Toast.LENGTH_SHORT).show();
                return;
            }
        }

        final TableRow tableRow = new TableRow(this);

        TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        params.setMargins(5,5,0,0);

        TextView itemNametextView = new TextView(this);
        itemNametextView.setBackgroundColor(Color.WHITE);
        itemNametextView.setLayoutParams(params);
        itemNametextView.setGravity(Gravity.CENTER);
        itemNametextView.setPadding(5,15,0,15);
        itemNametextView.setText(myItem.getItemName());
        tableRow.addView(itemNametextView);

        TextView currentStocktextView = new TextView(this);
        currentStocktextView.setBackgroundColor(Color.WHITE);
        currentStocktextView.setLayoutParams(params);
        currentStocktextView.setGravity(Gravity.CENTER);
        currentStocktextView.setPadding(5,15,0,15);
        currentStocktextView.setText(String.valueOf(myItem.getCurrentStock()));
        tableRow.addView(currentStocktextView);


        EditText itemIneditText = new EditText(this);
        itemIneditText.setBackgroundColor(Color.WHITE);
        itemIneditText.setLayoutParams(params);
        itemIneditText.setPadding(5,15,0,15);
        itemIneditText.setHint("In");
        itemIneditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        tableRow.addView(itemIneditText);


        EditText itemOuteditText = new EditText(this);
        itemOuteditText.setBackgroundColor(Color.WHITE);
        itemOuteditText.setLayoutParams(params);
        itemOuteditText.setPadding(5,15,0,15);
        itemOuteditText.setHint("Out");
        itemOuteditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        tableRow.addView(itemOuteditText);

        TextView actiontextView = new TextView(this);
        actiontextView.setLayoutParams(params);
        actiontextView.setGravity(Gravity.CENTER);
        actiontextView.setText("Drop");
        actiontextView.setTextColor(Color.RED);
        tableRow.addView(actiontextView);

        tableRow.setId(myItem.getId());

        actiontextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.removeView(tableRow);
            }
        });

        tableLayout.addView(tableRow);
    }

    public void addTransactionItem(View view){
        spinnerDialog.showSpinerDialog();
    }

    public void cancelTransaction(View view){
        homeItemList();
    }

    public void registerTransaction(final View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Transaction");
        alert.setMessage("Confirm Transaction?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                tableLayout = findViewById(R.id.transaction_table);
                for(int i=1;i<tableLayout.getChildCount();i++){
                    TableRow tr = (TableRow) tableLayout.getChildAt(i);
                    int id = tr.getId();
                    TextView textView = (TextView) tr.getChildAt(1);
                    EditText itemIn = (EditText) tr.getChildAt(2);
                    EditText itemOut = (EditText) tr.getChildAt(3);
                    int in,out,currentStock;
                    if(itemIn.getText().toString().trim().isEmpty()) in=0;
                    else in=Integer.parseInt(itemIn.getText().toString().trim());
                    if(itemOut.getText().toString().trim().isEmpty()) out=0;
                    else out=Integer.parseInt(itemOut.getText().toString().trim());
                    currentStock = Integer.parseInt(textView.getText().toString().trim());
                    currentStock = currentStock+in-out;
                    DatabaseHelper db = new DatabaseHelper(view.getContext());
                    db.updateItem(id,currentStock);
                }
                Toast.makeText(view.getContext(),"Transaction Success..",Toast.LENGTH_SHORT).show();
                homeItemList();
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    public void viewList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        homeRecyclerView = findViewById(R.id.home_recycler_view);
        homeRecyclerView.setLayoutManager(layoutManager);
        Toast.makeText(getApplicationContext(),"APPLIED LIST VIEW",Toast.LENGTH_SHORT).show();
    }
    public void viewGrid(){
        GridLayoutManager layoutManager = new GridLayoutManager(this,2);
        homeRecyclerView = findViewById(R.id.home_recycler_view);
        homeRecyclerView.setLayoutManager(layoutManager);
        Toast.makeText(getApplicationContext(),"APPLIED GRID VIEW",Toast.LENGTH_SHORT).show();
    }

    private void exportDB(){
        FileChannel source = null;
        FileChannel destination = null;

        File currentDB = new File(Environment.getExternalStorageDirectory().getPath() + "/Backup/" + File.separator + DATABASE_NAME);
        File backupDB = new File(this.getDatabasePath(DATABASE_NAME).getAbsolutePath());

        try {
            source = new FileInputStream(backupDB).getChannel();
            destination = new FileOutputStream(currentDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "Your Database is Exported !!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Export to File Failed !!"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void importDB() {
        FileChannel source = null;
        FileChannel destination = null;

        File currentDB = new File(Environment.getExternalStorageDirectory().getPath() + "/Backup/" + File.separator + DATABASE_NAME);
        File backupDB = new File(this.getDatabasePath(DATABASE_NAME).getAbsolutePath());

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "Your Database is Imported !!", Toast.LENGTH_SHORT).show();
            homeItemList();
        } catch (IOException e) {
            Toast.makeText(this, "Import From File Failed !!"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { }
                return;
        }
    }
}
