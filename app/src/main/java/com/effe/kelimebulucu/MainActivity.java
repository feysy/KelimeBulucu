package com.effe.kelimebulucu;

import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import kelimebulucu.effe.com.kelimebulucu.R;

public class MainActivity extends AppCompatActivity {
    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;
    EditText m_edit;
    ListView m_listView;
    ArrayAdapter<String> m_adapter;
    LinearLayout m_layoutWait, m_layoutMain;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public static List<Character> schars = new ArrayList<>();
    public static List<String> found = new ArrayList<>();
    public static List<Leaf> rootList = new ArrayList<>();

    private static SharedPreferences settings;
    boolean darkButton = false;
//    public static native boolean abs(int i);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = getSharedPreferences("preferences", 0);

        setContentView(R.layout.activity_main);

        m_layoutMain = (LinearLayout) findViewById(R.id.LinearLayout1);
        m_layoutWait = (LinearLayout) findViewById(R.id.layoutWait);
        m_edit = (EditText) findViewById(R.id.editSearch);
        m_listView = (ListView) findViewById(R.id.listResult);


        m_adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, found);
        m_listView.setAdapter(m_adapter);
        GetTask k = new GetTask(this, "id");
        k.execute();
        // m_adapter.notifyDataSetChanged();


        m_listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String str = (String) m_listView.getItemAtPosition(arg2);
                String url = "https://eksisozluk.com/" + str;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        m_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    schars.clear();
                    found.clear();
                    String s = m_edit.getText().toString().toUpperCase();
                    for (int i = 0; i < s.length(); i++) {
                        schars.add(s.charAt(i));
                    }
                    searchScrambled();
                    Collections.sort(found, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            if (s1.length() == s2.length())
                                return s1.compareTo(s2);
                            else
                                return s1.length() - s2.length();
                        }
                    });
                    m_adapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        darkButton = settings.getBoolean("darkMode", darkButton);
        menu.findItem(R.id.darkwin_button).setChecked(darkButton);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        switch (item.getItemId()) {
            case R.id.float_button:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {


                    //If the draw over permission is not available open the settings screen
                    //to grant the permission.
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
                } else {
                    startService(new Intent(MainActivity.this, FloatingViewService.class));
                    finish();
                }
                Toast.makeText(getBaseContext(), "Floating oldu", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
            case R.id.darkwin_button:
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("darkMode", !item.isChecked());
                editor.commit();
                darkButton = !item.isChecked();
                item.setChecked(darkButton);
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.effe.kelimebulucu/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.effe.kelimebulucu/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    public void loadTree() {
        AssetManager assetManager = getAssets();
        try {
            InputStream filestr = assetManager.open("dictionaryTree.tree");
            rootList.clear();
            byte[] arr = new byte[4];
            filestr.read(arr);
            int rootLen = toInt(arr);//BitConverter.ToInt32(arr,0);
            for (int i = 0; i < rootLen; i++) {
                arr = new byte[2];
                filestr.read(arr);
                char key = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getChar();
                arr = new byte[1];
                filestr.read(arr);
                boolean isword = arr[0] == 0 ? false : true;
                Leaf temp = new Leaf(key, isword);
                rootList.add(temp);
                loadLeaf(temp, filestr);
            }
            filestr.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    int toInt(byte[] bytes) {
        int value = ((bytes[3] & 0xFF) << 24) | ((bytes[2] & 0xFF) << 16)
                | ((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF);
        return value;
    }

    char toChar(byte[] arr) {

        return 0;

    }

    public void loadLeaf(Leaf parent, InputStream stream) throws IOException {
        byte[] arr = new byte[4];
        stream.read(arr);
        int childCount = toInt(arr);
        for (int i = 0; i < childCount; i++) {
            arr = new byte[2];
            stream.read(arr);
            char key = ByteBuffer.wrap(arr).order(ByteOrder.LITTLE_ENDIAN).getChar();
            arr = new byte[1];
            stream.read(arr);
            boolean isword = arr[0] == 0 ? false : true;
            Leaf temp = new Leaf(key, isword);
            parent.Children.add(temp);
            loadLeaf(temp, stream);
        }
    }

    public static Leaf getRootbyKey(char key) {
        for (int i = 0; i < rootList.size(); i++) {
            if (rootList.get(i).getKey() == key)
                return rootList.get(i);
        }
        return null;
    }



    public static void searchScrambled() {
        String s = "";
        for (int i = 0; i < schars.size(); i++) {
            char ch = schars.get(i);
            schars.remove(i);
            Leaf temp = getRootbyKey(ch);
            if (temp != null)
                _searchScr(temp, s);
            schars.add(i, ch);
        }
        Log.v("Leaf", "-" + lc + "-");

    }

    private static void _searchScr(Leaf l, String s) {
        lc++;
        s = s + l.getKey();
        if (l.getIsWord()) {
            if (!found.contains(s))
                found.add(s);
        }
        for (int i = 0; i < schars.size(); i++) {
            char ch = schars.get(i);
            schars.remove(i);
            Leaf temp = l.getChildbyKey(ch);
            if (temp != null)
                _searchScr(temp, s);
            schars.add(i, ch);
        }
    }

    static int lc = 0;
    ProgressDialog mDialog;

    class GetTask extends AsyncTask<Object, Void, String> {
        Context context;

        GetTask(Context context, String userid) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            mDialog = new ProgressDialog(context);
            mDialog.setMessage("Lütfen bekleyin...");
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.setCancelable(false);
            mDialog.show();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            m_layoutWait.setVisibility(View.GONE);
            mDialog.dismiss();
        }

        @Override
        protected String doInBackground(Object... params) {
            loadTree();
            return null;
        }
    }

}