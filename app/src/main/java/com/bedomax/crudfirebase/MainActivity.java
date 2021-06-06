package com.bedomax.crudfirebase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText mTitle, mDesc;
    private Button mSaveBtn,  mShowBtn;
    private FirebaseFirestore db;
    private String uTitle, uDesc, uId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTitle = findViewById(R.id.edit_title);
        mDesc = findViewById(R.id.edit_desc);
        mSaveBtn = findViewById(R.id.save_btn);
        mShowBtn = findViewById(R.id.showall_btn);

        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null ){
            mSaveBtn.setText("Actualizar");
            uTitle = bundle.getString("uTitle");
            uId = bundle.getString("uId");
            uDesc = bundle.getString("uDesc");
            mTitle.setText(uTitle);
            mDesc.setText(uDesc);
        }else{
            mSaveBtn.setText("Guardar");
        }

        mShowBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startActivity(new Intent(MainActivity.this, ShowActivity.class));
            }
        });

        mSaveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String title  = mTitle.getText().toString();
                String description  = mDesc.getText().toString();

                Bundle bundle1 = getIntent().getExtras();
                if(bundle1 != null){
                    String id = uId;
                    updateToFireStore(id, title, description);
                }else{
                    String id = UUID.randomUUID().toString();
                    saveToFireStore(id, title, description);
                }
            }
        });
    }

    private void saveToFireStore(String id, String  title, String description){
        if(!title.isEmpty() && !description.isEmpty()){
            HashMap<String, Object> map = new HashMap<>();
            map.put("id",  id);
            map.put("title",  title);
            map.put("description", description);
            db.collection("Tasks").document(id).set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity.this, "Se guardo correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Error en el servicio", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateToFireStore(String id, String  title, String description) {
        db.collection("Tasks").document(id).update("title", title, "description", description)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Se actualizo correctamente", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(MainActivity.this, "Error"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error en el servicio", Toast.LENGTH_SHORT).show();
            }
        });
    }
}