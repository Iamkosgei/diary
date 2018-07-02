package xyz.kosgei.diary;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import xyz.kosgei.diary.Interface.ItemOnClick;
import xyz.kosgei.diary.Model.Entries;
import xyz.kosgei.diary.ViewHolder.EntriesViewHolder;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private RecyclerView mEntriesRecyclerView;
    FirebaseRecyclerAdapter adapter;
    FirebaseUser currentUser;

    SwipeController swipeController = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        currentUser = auth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Diary");

        setupRecyclerView();


        if (currentUser != null)
        {
            getSupportActionBar().setTitle(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        }



        //offline persistence
       // FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        ///setting the layout manager
        mEntriesRecyclerView = findViewById(R.id.entries_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mEntriesRecyclerView.setLayoutManager(linearLayoutManager);
        mEntriesRecyclerView.hasFixedSize();



        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            loadEntries();
        }



    }

    private void loadEntries() {

        //Query
        //Query foodQuery = databaseReference.orderByKey().equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Query entryQuery = databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //configuring the adapter
        FirebaseRecyclerOptions<Entries> firebaseRecyclerOptions =new FirebaseRecyclerOptions.Builder<Entries>().setQuery(entryQuery,Entries.class).build();


        adapter= new FirebaseRecyclerAdapter<Entries,EntriesViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(final EntriesViewHolder holder, int position, final Entries model) {

                holder.Title.setText(model.getTitle());
                holder.Date.setText(model.getDate());




                holder.setItemOnClick(new ItemOnClick() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Get item clicked
                        Intent Id = new Intent(MainActivity.this, ContentActivity.class);
                        Id.putExtra("Id", adapter.getRef(position).getKey());

                        startActivity(Id);
                    }
                });
            }
            @Override
            public EntriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entries, parent, false);

                return new EntriesViewHolder(view);
            }
        };
        mEntriesRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemSelected = item.getItemId();
        if (itemSelected == R.id.logout) {
            FirebaseAuth.getInstance().signOut();

            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
        if (itemSelected == R.id.entry)
        {
            //finish();
            startActivity(new Intent(MainActivity.this,NewEntryActivity.class));

        }

        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.entries_recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                /*adapter.players.remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());*/


                DatabaseReference databaseReference1 = firebaseDatabase.getReference("Diary").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(adapter.getRef(position).getKey());


                databaseReference1.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });




            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }


}
