package com.thoughtworks.simpleandroid;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import com.thoughtworks.extensionpoint.TextProvider;

import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.util.tracker.ServiceTracker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;


public class MainActivity extends ActionBarActivity {

    private Framework framework;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launchFramework();
        initExtensionPoint();
        startBundle("extension-a.jar");
        //        startBundle("extension-b.jar");
    }

    private void launchFramework() {
        Log.d("osgi", "finding framework factory");
        ServiceLoader<FrameworkFactory> frameworkFactories = ServiceLoader.load(FrameworkFactory.class);
        Iterator<FrameworkFactory> iterator = frameworkFactories.iterator();
        if (iterator.hasNext()) {
            Log.d("osgi", "creating framework factory");
            framework = iterator.next().newFramework(getFrameworkConfig());
            try {
                Log.d("osgi", "framework starting");
                framework.start();
                Log.d("osgi", "framework started");

            } catch (BundleException e) {
                Log.d("osgi", "framework start failed", e);
            }
        }
    }

    private Map<String, String> getFrameworkConfig() {
        HashMap<String, String> config = new HashMap<String, String>();
        config.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "android,com.thoughtworks.extensionpoint");
        try {
            config.put(Constants.FRAMEWORK_STORAGE, File.createTempFile("osgi", "launcher").getParent());
        } catch (IOException e) {
            Log.d("osgi", "creating storage", e);
        }
        return config;
    }

    private void initExtensionPoint() {
        ServiceTracker<TextProvider, TextProvider> extensions
                = new ServiceTracker<>(framework.getBundleContext(), TextProvider.class, new ExtensionPoint(this));
        extensions.open(true);
    }

    private void startBundle(String bundle) {
        Log.d("osgi", "installing bundle " + bundle);
        InputStream stream = null;
        try {
            stream = getAssets().open("bundles/" + bundle);
            org.osgi.framework.Bundle installed = framework.getBundleContext().installBundle(bundle, stream);
            Log.d("osgi", "bundle " + bundle + " installed");
            installed.start();
            Log.d("osgi", "bundle " + bundle + " started");
        } catch (IOException e) {
            Log.e("osgi", "install bundle failed", e);
        } catch (BundleException e) {
            Log.e("osgi", "install bundle failed", e);
        } finally {
            try {
                if (stream != null) stream.close();
            } catch (IOException e) {
                Log.e("osgi", e.toString());
            }
        }
    }

    public void addButton(int id, String text) {
        Button button = new Button(this);
        button.setId(id);
        button.setText(text);

        Log.d("extension point", "add button " + text);

        LinearLayout layout = (LinearLayout) findViewById(R.id.main);
        layout.addView(button, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
