package xyz.kosgei.diary;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import xyz.kosgei.diary.Model.Entries;

public class ContentActivity extends AppCompatActivity {
    String id="";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    EditText edContent;

    Entries entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        edContent= findViewById(R.id.editText);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Diary").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(getIntent() != null)
        {
            id = getIntent().getStringExtra("Id");

            if (!id.isEmpty() && id!=null)
            {
                loadContent(id);
            }
        }
    }

    private void loadContent(String id) {

        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                  entries = dataSnapshot.getValue(Entries.class);
                edContent.setText(entries.getBody());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void update(View view) {

        if (entries.getBody().contentEquals(edContent.getText().toString().trim()) )
        {
            Toast.makeText(this, "No change", Toast.LENGTH_SHORT).show();
        }
        else
        {
           databaseReference.child(id).child("body").setValue(edContent.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
               @Override
               public void onComplete(@NonNull Task<Void> task) {
                   if (task.isSuccessful())
                   {
                       Toast.makeText(ContentActivity.this, "Saved!!", Toast.LENGTH_SHORT).show();
                   }
               }
           });
        }
    }

}
