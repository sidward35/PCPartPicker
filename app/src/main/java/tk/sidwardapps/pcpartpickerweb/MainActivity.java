package tk.sidwardapps.pcpartpickerweb;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import tk.sidwardapps.pcpartpickerweb.logger.LogFragment;
import tk.sidwardapps.pcpartpickerweb.logger.LogWrapper;
import tk.sidwardapps.pcpartpickerweb.logger.MessageOnlyLogFilter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener/*, Listener*/ {

    public static final String TAG = "Network Connect";

    // Reference to the fragment showing events, so we can clear it with a button
    // as necessary.
    public LogFragment mLogFragment;

    private WebView builder;
    private boolean webOpened=false;
    private Button button1, button2, button4, button5, button6, button7, button8, button9;
    private NavigationView navigationView;
    private String query = "http://pcpartpicker.com/search/?cc=us&q=", html;
    private SpannableString info = new SpannableString("PC Part Picker:\nVersion: 0.5.3\n\nDeveloper: Sid\nE-mail: sid@stscamps.org\nPhone: (408) 638-2398\nWebsite: www.sidm35.cf (WIP)"),
        comingSoon = new SpannableString("This feature is coming soon.");

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeb("http://pcpartpicker.com/parts/partlist/");
            }
        });
        button1 = (Button) findViewById(R.id.button) ;
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeb("http://pcpartpicker.com/parts/partlist/");
            }
        });
        button2 = (Button) findViewById(R.id.button2) ;
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeb("http://pcpartpicker.com/guide/");
            }
        });
        button4 = (Button) findViewById(R.id.button4) ;
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeb("http://pcpartpicker.com/parts/cpu");
            }
        });
        button5 = (Button) findViewById(R.id.button5) ;
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeb("http://pcpartpicker.com/parts/motherboard");
            }
        });
        button6 = (Button) findViewById(R.id.button6) ;
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeb("http://pcpartpicker.com/parts/memory");
            }
        });
        button7 = (Button) findViewById(R.id.button7) ;
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeb("http://pcpartpicker.com/parts/internal-hard-drive");
            }
        });
        button8 = (Button) findViewById(R.id.button8) ;
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeb("http://pcpartpicker.com/parts/video-card");
            }
        });
        button9 = (Button) findViewById(R.id.button9) ;
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openWeb("http://pcpartpicker.com/parts/power-supply");
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onResume();
        if(!readFromFile().equals("read")) {
            new AlertDialog.Builder(this)
                .setTitle("Welcome!")
                .setMessage("PCPartPicker provides computer part selection, compatibility, and pricing "
                        + "guidance for do-it-yourself computer builders. Assemble your virtual part "
                        + "lists with PCPartPicker and we will provide compatibility guidance with "
                        + "up-to-date pricing from dozens of the most popular online retailers. "
                        + "We make it easy to share your part list with others, and our community "
                        + "forums provide a great place to discuss ideas and solicit feedback.\n\n"
                        + "Building your own PC and need ideas on where to get started? Explore our "
                        + "build guides, which cover systems for all use-cases and budgets, or create "
                        + "your own and share it with the community.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        writeToFile("read");
                    }
                })
                .setIcon(R.drawable.info_icon)
                .show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(webOpened && builder.canGoBack()) {
            builder.goBack();
        } else if(webOpened){
                webOpened=false;
                onResume();
        } else if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
        } else{
            setContentView(R.layout.content_main);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_build) {
            openWeb("http://pcpartpicker.com/parts/partlist/");
        } else if (id == R.id.nav_guides) {
            openWeb("http://pcpartpicker.com/guide/");
        } else if (id == R.id.nav_login){
            openWeb("http://pcpartpicker.com/accounts/login/?next=/");
        } else if (id == R.id.nav_share){
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Download the PC Part Picker app at amzn.com/B01IG6J6AO now!\n\nThis app really helped me with building a custom PC!");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.shareString)));
        } else if (id == R.id.nav_about){
            final TextView tx1 = new TextView(this);
            tx1.setText(info);
            tx1.setAutoLinkMask(RESULT_OK);
            tx1.setMovementMethod(LinkMovementMethod.getInstance());
            Linkify.addLinks(info, Linkify.ALL);
            tx1.setTextSize(19);
            tx1.setPadding(45, 40, 45, 40);
            tx1.setTextColor(Color.BLACK);
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(R.drawable.info_icon)
                    .setView(tx1)
                    .show();
        } else if (id == R.id.nav_portablepicker){
            openWeb("http://portablepicker.com/");
        }
        /*else if(id == R.id.blog){
            //setContentView(R.layout.blog_page);
            // Initialize the logging framework.
            //DownloadTask dt = new DownloadTask();
            //dt.initializeLogging();
            final TextView tx1 = new TextView(this);
            tx1.setText(comingSoon);
            tx1.setAutoLinkMask(RESULT_OK);
            tx1.setMovementMethod(LinkMovementMethod.getInstance());
            //Linkify.addLinks(info, Linkify.ALL);
            tx1.setTextSize(19);
            tx1.setPadding(75, 40, 75, 40);
            tx1.setTextColor(Color.BLACK);
            new AlertDialog.Builder(this)
                    .setTitle("Coming Soon")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(R.drawable.info_icon)
                    .setView(tx1)
                    .show();
        }*/
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        final SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search parts...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String x) {
                query += x;
                openWeb(query);
                query = "http://pcpartpicker.com/search/?cc=us&q=";
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Download the PC Part Picker app at amzn.com/B01IG6J6AO now!\n\nThis app really helped me with building a custom PC!");
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.shareString)));
            return true;
        } else if (id == R.id.action_about) {
            final TextView tx1 = new TextView(this);
            tx1.setText(info);
            tx1.setAutoLinkMask(RESULT_OK);
            tx1.setMovementMethod(LinkMovementMethod.getInstance());
            Linkify.addLinks(info, Linkify.ALL);
            tx1.setTextSize(19);
            tx1.setPadding(45, 40, 45, 40);
            tx1.setTextColor(Color.BLACK);
            new AlertDialog.Builder(this)
                    .setTitle("About")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(R.drawable.info_icon)
                    .setView(tx1)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openWeb(String url){
        setContentView(R.layout.build_layout);
        builder = (WebView) findViewById(R.id.build_webview);
        WebSettings webSettings = builder.getSettings();
        webSettings.setJavaScriptEnabled(true);
        builder.loadUrl(url);
        builder.setWebViewClient(new WebViewClient());
        webOpened = true;
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("config", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = openFileInput("config");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public View onCreateView (LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState)
    {
        View rootView = inflater.inflate (R.layout.activity_main,container,false);

        TextView tvOutput = (TextView) rootView.findViewById (R.id.textView);
        tvOutput.setText(html);

        return rootView;
    }

}

/**
 * Implementation of AsyncTask, to fetch the data in the background away from
 * the UI thread.
 */
class DownloadTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... urls) {
        try {
            return loadFromNetwork(urls[0]);
        } catch (IOException e) {
            return "Connection Error";
        }
    }

    /**
     * Uses the logging framework to display the output of the fetch
     * operation in the log fragment.
     */
    @Override
    protected void onPostExecute(String result) {
        Log.i("Network Connect", result);
    }


    /** Initiates the fetch operation. */
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str ="";

        try {
            stream = downloadUrl(urlString);
            str = readIt(stream, 500000);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return str;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws java.io.IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
        // BEGIN_INCLUDE(get_inputstream)
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
        // END_INCLUDE(get_inputstream)
    }

    /** Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from targeted site.
     * @param len Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    /** Create a chain of targets that will receive log data */
    public void initializeLogging() {

        // Using Log, front-end to the logging chain, emulates
        // android.util.log method signatures.

        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        //Log.setLogNode(logWrapper);

        // A filter that strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        MainActivity ma = new MainActivity();
        //ma.mLogFragment =
                //(LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);
        msgFilter.setNext(ma.mLogFragment.getLogView());
    }
}

class ParseURL extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        StringBuffer buffer = new StringBuffer();
        try {
            Log.d("JSwa", "Connecting to [" + strings[0] + "]");
            Document doc = Jsoup.connect(strings[0]).get();
            Log.d("JSwa", "Connected to [" + strings[0] + "]");
            // Get document (HTML page) title
            String title = doc.title();
            Log.d("JSwA", "Title [" + title + "]");
            buffer.append("Title: " + title + "\r\n");

            // Get meta info
            Elements metaElems = doc.select("meta");
            buffer.append("META DATA\r\n");
            for (Element metaElem : metaElems) {
                String name = metaElem.attr("name");
                String content = metaElem.attr("content");
                buffer.append("name [" + name + "] - content [" + content + "] \r\n");
            }

            Elements topicList = doc.select("h2.topic");
            buffer.append("Topic list\r\n");
            for (Element topic : topicList) {
                String data = topic.text();

                buffer.append("Data [" + data + "] \r\n");
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return buffer.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        MainActivity ma = new MainActivity();
        String code = s;
        System.out.println(code);
    }
}