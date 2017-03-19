package br.edu.ufcg.empsoft;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import br.edu.ufcg.empsoft.activities.FazendaActivity;
import br.edu.ufcg.empsoft.adapters.FazendaAdapter;
import br.edu.ufcg.empsoft.models.Database;
import br.edu.ufcg.empsoft.models.Fazenda;

public class MainActivity extends AppCompatActivity {

    private FazendaAdapter adapter;
    private Database database = Database.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setBackground(new ColorDrawable(Color.parseColor("#2ecc71")));
        setSupportActionBar(toolbar);

        initAdapter(0); //Default mode card view

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FazendaActivity.class);
                startActivityForResult(intent, 25);
            }
        });
    }

    private void initAdapter(int typeOfView) {
        RecyclerView recList = (RecyclerView) findViewById(R.id.recyclerView);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        adapter = new FazendaAdapter(new ArrayList<Fazenda>(), recList, this, typeOfView);
        database.addListener(Database.Table.FAZENDAS, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, Fazenda>> t =
                        new GenericTypeIndicator<HashMap<String, Fazenda>>(){};
                HashMap<String, Fazenda> mapeamentoFazendas = dataSnapshot.getValue(t);

                List<Fazenda> fazendas = new ArrayList<Fazenda>();
                if (mapeamentoFazendas != null) {
                    fazendas = new ArrayList<>(mapeamentoFazendas.values());
                }

                adapter.setFazendasList(fazendas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25) {
            if(resultCode == Activity.RESULT_OK){
                Fazenda result= (Fazenda) data.getSerializableExtra("novaFazenda");
                database.append(Database.Table.FAZENDAS, result);
                adapter.addFazenda(result);
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cardset) {
            initAdapter(0);
            return true;
        } else if (id == R.id.action_listset) {
            initAdapter(1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
