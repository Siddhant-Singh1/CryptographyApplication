package com.example.finalproject;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.CLIPBOARD_SERVICE;


public class Crypto extends AppCompatActivity {
        EditText keytext;
        EditText normaltext;
        EditText ciphertext;
        private Button copy_normal;
        private Button copy_cipher;
        private Button encrypt;
        private Button decrypt;
        private Button delete_normal;
        private Button delete_cipher;
        private Button Share;
        TextView char_count;
        TextView char_count2;
        Context c;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.linear_layout);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                Window window= this.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(Color.parseColor("#2a9db7"));
            }
            c=Crypto.this;

            normaltext=findViewById(R.id.normaltext);
            keytext=findViewById(R.id.key);
            ciphertext=findViewById(R.id.cipher_text);
            copy_cipher=findViewById(R.id.copy_cipher);
            copy_normal=findViewById(R.id.copy_normal);
            encrypt=findViewById(R.id.encrypt);
            decrypt=findViewById(R.id.decrypt);
            delete_normal=findViewById(R.id.delete_normal);
            delete_cipher=findViewById(R.id.delete_cipher);
            char_count=findViewById(R.id.char_count);
            char_count2=findViewById(R.id.char_count2);
            Share=findViewById(R.id.share);

            Share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = ciphertext.getText().toString();
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
                });

            encrypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (normaltext.getText().toString().matches("")||keytext.getText().toString().matches(""))
                        App.ToastMaker(c, "Enter Input Text And Key");
                    else if (keytext.getText().toString().length()!=8){
                        App.ToastMaker(c,"Enter Key Of 8 Characters");
                    }
                    else{
                        ciphertext.setText(encrypt(normaltext.getText().toString(),c));
                    }
                }
            });

            decrypt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ciphertext.getText().toString().matches("")||keytext.getText().toString().matches("")){
                        App.ToastMaker(c,"Enter the encrypted text and key");
                    }
                    else if (keytext.getText().toString().length()!=8){
                        App.ToastMaker(c,"Enter Key Of 8 Characters");
                    }
                    else{
                        normaltext.setText(decrypt(ciphertext.getText().toString(),c));
                    }
                }
            });


            copy_normal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("CIPHER TEXT",normaltext.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    App.ToastMaker(c,"Input Text Copied");
                }
            });

            copy_cipher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard= (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("CIPHER TEXT",ciphertext.getText().toString());
                    clipboard.setPrimaryClip(clip);
                    App.ToastMaker(c,"Encypted Text Copied");
                }
            });


            delete_normal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    normaltext.setText("");
                }
            });

            delete_cipher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ciphertext.setText("");
                }
            });


            normaltext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    char_count.setText(normaltext.getText().toString().length()+"");
                }
            });


            ciphertext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    char_count.setText(ciphertext.getText().toString().length()+"");
                }
            });

        }
        public String decrypt(String value,Context c) {
            String coded;
            String result = null;
            if (value.startsWith("CODE==")) {
                coded = value.substring(6, value.length()).trim();
            } else {
                coded = value.trim();
            }


            try {
                byte[] bytesDecoded = Base64.decode(coded.getBytes("UTF-8"), Base64.DEFAULT);
                SecretKeySpec key = new SecretKeySpec(keytext.getText().toString().getBytes(), "DES");
                Cipher cipher = Cipher.getInstance("DES/ECB/ZeroBytePadding");
                cipher.init(Cipher.DECRYPT_MODE, key);
                byte[] textDecrypted = cipher.doFinal(bytesDecoded);
                result = new String(textDecrypted);
            }
            catch (NoSuchAlgorithmException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (NoSuchPaddingException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (IllegalBlockSizeException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (BadPaddingException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (InvalidKeyException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (Exception e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }

            return  result;
        }
        public String encrypt(String value,Context c){
            String crypted="";
            try{
                byte[] cleartext=value.getBytes("UTF-8");//key is of 8 characters and passed by user

                SecretKey key=new SecretKeySpec(keytext.getText().toString().getBytes(),"DES");
                Cipher cipher = Cipher.getInstance("DES/ECB/ZeroBytePadding");
                cipher.init(Cipher.ENCRYPT_MODE,key);
                crypted= Base64.encodeToString(cipher.doFinal(cleartext),Base64.DEFAULT);
            }
            catch (NoSuchAlgorithmException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (NoSuchPaddingException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (IllegalBlockSizeException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (BadPaddingException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (InvalidKeyException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }
            catch (Exception e){
                e.printStackTrace();
                App.DialogMaker(c,"ENCRYPT ERROR","ERROR"+"\n"+e.getMessage());
                return "ENCYPT ERROR";
            }

            return  crypted;//DES ALGORITHM FOR THE ENCRYPTION PURPOSE IN THIS PROJECT

        }
    }

