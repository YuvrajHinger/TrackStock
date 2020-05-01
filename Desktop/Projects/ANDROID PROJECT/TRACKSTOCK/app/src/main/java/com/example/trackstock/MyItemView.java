package com.example.trackstock;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MyItemView extends AppCompatActivity {

    public static boolean FLAG = false;
    private MyItem myItem;
    Uri imageURI;
    byte[] pic;
    String name,stock,price;
    ImageView uploadImageView;
    EditText itemName,itemStock,itemPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);
        itemName = findViewById(R.id.update_item_name);
        itemStock = findViewById(R.id.update_item_stock);
        itemPrice = findViewById(R.id.update_item_price);
        uploadImageView = findViewById(R.id.update_item_image);
        Intent intent = getIntent();
        if(intent.getExtras()!=null){
            myItem = (MyItem) intent.getSerializableExtra("object");
            byte[] itemPic = myItem.getItemImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(itemPic,0,itemPic.length);
            uploadImageView.setImageBitmap(bitmap);
            itemName.setText(myItem.getItemName());
            itemStock.setText(String.valueOf(myItem.getCurrentStock()));
            itemPrice.setText(String.valueOf(myItem.getPrice()));
        }
    }

    public void uploadImage(View view){
        Intent gallery = new Intent();
        gallery.setType("image/");
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallery,"Select Picture"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode== RESULT_OK){
            imageURI = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageURI);
                uploadImageView = findViewById(R.id.update_item_image);
                uploadImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateItem(final View view){
        initializeItem();
        if(!validiateItem()){
            Toast.makeText(view.getContext(),"Updation Failed",Toast.LENGTH_SHORT).show();
        }
        else{
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Update Item");
            alert.setMessage("Are you sure you want to update?");
            alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if(price.isEmpty()) price="0";
                    DatabaseHelper db = new DatabaseHelper(view.getContext());
                    pic = imageViewToByte(uploadImageView);

                    myItem.setItemName(name);
                    myItem.setCurrentStock(Integer.parseInt(stock));
                    myItem.setPrice(Integer.parseInt(price));
                    myItem.setItemImage(pic);
                    db.updateItemView(myItem);
                    Toast.makeText(getApplicationContext(),"Updation Success",Toast.LENGTH_SHORT).show();
                    FLAG=true;
                    finish();
                }
            });
            alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // close dialog
                    dialog.cancel();
                }
            });
            alert.show();
        }
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
        name = itemName.getText().toString().trim();
        price = itemPrice.getText().toString().trim();
        stock = itemStock.getText().toString().trim();
    }

    public void cancelUpdate(View view){
        finish();
    }

    public void deleteItem(final View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Delete Item");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                db.deleteItem(myItem.getId());
                FLAG=true;
                finish();            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }
}
