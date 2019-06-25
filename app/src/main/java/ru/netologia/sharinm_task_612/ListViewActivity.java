package ru.netologia.sharinm_task_612;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ListView list;
    private List<Map<String, String>> content;
    private ArrayList<Integer> deletedItems = new ArrayList<Integer>();
    private static final String KEY = "LIST";

    private String[] arrayContent;

    private static final String APP_PREFERENCES = "mysettings";
    private static final String APP_PREFERENCES_TEXT = "Text";

    private SwipeRefreshLayout swipeRefreshLayout;

    public SharedPreferences.Editor editor;

    SharedPreferences mSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        swipeRefreshLayout =  (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(this);

        PreferencesSettings(savedInstanceState);
    }

    @Override
    public void onRefresh(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                PreferencesSettings(null);

                swipeRefreshLayout.setRefreshing(false);
            }
        }, 100);
    }


    private void PreferencesSettings(Bundle saved) {
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        editor = mSettings.edit();

        if(mSettings.contains(APP_PREFERENCES_TEXT)) {
            arrayContent = mSettings.getString(APP_PREFERENCES_TEXT, "нет текста").split("\n\n");
        } else {
            editor.putString(APP_PREFERENCES_TEXT, getResources().getString(R.string.large_text));
            editor.apply();
            arrayContent = mSettings.getString(APP_PREFERENCES_TEXT, "нет текста").split("\n\n");
        }

        list = findViewById(R.id.list);

        final List<Map<String, String>> values = prepareContent();

        final BaseAdapter listContentAdapter = createAdapter(values);

        list.setAdapter(listContentAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                deletedItems.add(position);
                values.remove(position);
                listContentAdapter.notifyDataSetChanged();
            }
        });

        if(saved != null){
            deletedItems.addAll(saved.getIntegerArrayList(KEY));

            for (int index: deletedItems) {
                values.remove(index);
            }
        }
    }

    @NonNull
    private BaseAdapter createAdapter(List<Map<String, String>> values) {
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, values, R.layout.activity_separate, new String[]{"bottom", "top"}, new int[]{R.id.textView1, R.id.textView2});
        return simpleAdapter;
    }

    @NonNull
    private List<Map<String, String>> prepareContent() {
        content = new ArrayList<>();

        //String[] arrayContent = getString(R.string.large_text).split("\n\n");
        for(int i=0 ; i < arrayContent.length; i++) {

            Map<String, String> map = new HashMap<>();

            map.put("bottom", arrayContent[i]);
            map.put("top", String.valueOf(arrayContent[i].length()));

            content.add(map);
        }

        return content;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putIntegerArrayList(KEY, deletedItems);
        super.onSaveInstanceState(outState);
    }
}
