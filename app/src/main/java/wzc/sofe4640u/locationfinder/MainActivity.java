package wzc.sofe4640u.locationfinder;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public DatabaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar searchToolbar = findViewById(R.id.searchToolbar);
        setSupportActionBar(searchToolbar);

        RecyclerView searchRecycler = findViewById(R.id.searchRecycler);
        searchRecycler.setLayoutManager(new LinearLayoutManager(this));
        db = new DatabaseHelper(getApplicationContext());
        searchRecycler.setAdapter(new SearchRecyclerAdapter(db.getAllNames()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        RecyclerView searchRecycler = findViewById(R.id.searchRecycler);
        MenuItem searchLocations = menu.findItem(R.id.searchLocations);
        SearchView searchView = (SearchView) searchLocations.getActionView();

        assert searchView != null;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (db.exists(query)) {
                    double[] coords = db.getCoords(query);
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    intent.putExtra("lat", coords[0]);
                    intent.putExtra("lon", coords[1]);
                    startActivity(intent);
                } else if (!query.trim().isBlank()){
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addr = geocoder.getFromLocationName(query, 1);
                        double lat = addr.get(0).getLatitude();
                        double lon = addr.get(0).getLongitude();
                        db.addLoc(query, lat, lon);
                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lon", lon);
                        startActivity(intent);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                ((SearchRecyclerAdapter) searchRecycler.getAdapter()).updateItems(db.filter(Arrays.asList(query.split("\\s+"))));
                return true;
            }
        });
        return true;
    }
}