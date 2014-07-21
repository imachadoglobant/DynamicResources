package com.ismaelmachado.dynamicresources;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.io.File;
import java.io.InputStream;

/**
 * Main activity.
 *
 * @author ismael.machado
 */
public class Splash extends Activity {

    private Button downloadButton;
    private Button applyButton;
    private TextView downloadingText;
    private ImageView imageView;

    private Bitmap bitmap;
    private SpiceManager spiceManager;

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

        downloadButton = (Button) findViewById(R.id.downloadButton);
        applyButton = (Button) findViewById(R.id.applyButton);
        downloadingText = (TextView) findViewById(R.id.downloadingText);
        imageView = (ImageView) findViewById(R.id.imageView);

        spiceManager = new SpiceManager(UncachedSpiceService.class);

        //Fetch image from branding services
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadingText.setVisibility(View.VISIBLE);
                //Determine temp file location
                File folder = getFilesDir();
                File file = new File(folder.getPath() + "image.png");
                //Download image with Robospice
                BinaryRequest request = new BinaryRequest("https://api.amplifire.com/v2/branding/LYRUB48LE/image", file);
                spiceManager.execute(request, new RequestListener<InputStream>() {

                    @Override
                    public void onRequestSuccess(InputStream inputStream) {
                        downloadingText.setVisibility(View.GONE);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        downloadingText.setVisibility(View.GONE);
                        Log.d("ERROR", spiceException.getMessage());
                    }
                });
            }
        });

        //Place image as actionbar icon
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable d = new BitmapDrawable(getResources(), bitmap);
                getActionBar().setIcon(d);
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

}
