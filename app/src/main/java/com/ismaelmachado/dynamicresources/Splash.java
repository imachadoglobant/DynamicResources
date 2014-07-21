package com.ismaelmachado.dynamicresources;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.ProgressByteProcessor;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;

import org.springframework.http.HttpEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class Splash extends Activity {

    private SpiceManager spiceManager = new SpiceManager(UncachedSpiceService.class);

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final ImageView imageView = (ImageView) findViewById(R.id.imageView);

        //ImageDownload task = new ImageDownload("https://preyproject.com/up/2010/12/android_platform.png", imageView);
        //task.execute();

        File folder = getFilesDir();
        File file = new File(folder.getPath() + "image.png");
        //BinaryRequest request = new BinaryRequest("https://api.amplifire.com/v2/branding/ELVSBG7SU/image", file);
        BinaryRequest request = new BinaryRequest("https://api.amplifire.com/v2/branding/LYRUB48LE/image", file);
        spiceManager.execute(request, new RequestListener<InputStream>() {

            @Override
            public void onRequestSuccess(InputStream inputStream) {
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmap);
                BitmapDrawable d = new BitmapDrawable(getResources(), bitmap);
                getActionBar().setIcon(d);
            }

            @Override
            public void onRequestFailure(SpiceException spiceException) {
                Log.d("ERROR", spiceException.getMessage());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ImageDownload extends AsyncTask {
        private String requestUrl;
        private ImageView view;
        private Bitmap pic;

        private ImageDownload(String requestUrl, ImageView view) {
            this.requestUrl = requestUrl;
            this.view = view;
        }

        @Override
        protected Object doInBackground(Object... objects) {
            try {
                URL url = new URL(requestUrl);
                URLConnection conn = url.openConnection();
                pic = BitmapFactory.decodeStream(conn.getInputStream());
            } catch (Exception ex) {
                Log.d("ERROR", ex.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            view.setImageBitmap(pic);
            BitmapDrawable d = new BitmapDrawable(getResources(), pic);
            getActionBar().setIcon(d);
        }
    }

}
