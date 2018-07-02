package xyz.kosgei.diary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import xyz.kosgei.diary.Model.Entries;

public class NewEntryActivity extends AppCompatActivity {

    EditText edTitle,edBody;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseAuth auth;
    FirebaseUser user;
    String currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_entry);



        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Diary");

        //offline persistence
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        currentDate = sdf.format(new Date());

        auth = FirebaseAuth.getInstance();

        edTitle = findViewById(R.id.editText2);
        edBody = findViewById(R.id.editText3);

    }

    public void onSave(View view)
    {
        valid();
        if (valid())
        {
            save();
        }

    }



    public boolean valid()
    {
        boolean valid = true;

        if (edTitle.getText().toString().trim().isEmpty())
        {
            valid= false;
            edTitle.setError("Enter a Title");
        }
        else if (edBody.getText().toString().trim().isEmpty())
        {
            valid = false;
            edBody.setError("This cant be empty");
        }

        return valid;
    }

    private void save() {
        databaseReference = firebaseDatabase.getReference("Diary").child(auth.getCurrentUser().getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                 Entries entries = new Entries(currentDate,edTitle.getText().toString(),edBody.getText().toString());

                databaseReference.push().setValue(entries).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(NewEntryActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                            edBody.setText("");
                            edTitle.setText("");
                            startActivity(new Intent(NewEntryActivity.this,MainActivity.class));
                            finish();
                        }
                    }
                });







            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();

            }
        });

    }
}
