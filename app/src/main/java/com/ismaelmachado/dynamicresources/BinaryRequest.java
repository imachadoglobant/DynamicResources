package com.ismaelmachado.dynamicresources;

import android.util.Log;

import com.octo.android.robospice.request.ProgressByteProcessor;
import com.octo.android.robospice.request.SpiceRequest;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ismael.machado on 02/07/14.
 */
public class BinaryRequest extends SpiceRequest<InputStream> {

    private static final int BUF_SIZE = 4096;
    protected String url;
    private File cacheFile;

    public BinaryRequest(final String url, final File cacheFile) {
        super(InputStream.class);
        this.url = url;
        this.cacheFile = cacheFile;
    }

    @Override
    public final InputStream loadDataFromNetwork() throws Exception {
        try {
            final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
            httpURLConnection.setRequestProperty("Authorization", "Basic S01VQ0FVOFY2Om8xckl0dDU4eGxHREdCZU0=");
            return processStream(httpURLConnection.getContentLength(), httpURLConnection.getInputStream());
        } catch (final MalformedURLException e) {
            Log.d("ERROR", "Unable to create URL");
            throw e;
        } catch (final IOException e) {
            Log.e("ERROR", "Unable to download binary");
            throw e;
        }
    }

    protected final String getUrl() {
        return this.url;
    }

    public InputStream processStream(int contentLength, InputStream inputStream) throws IOException {
        OutputStream fileOutputStream = null;
        try {

            fileOutputStream = new FileOutputStream(cacheFile);

            BufferedInputStream is = new BufferedInputStream(inputStream, 8 * 1024);

            IOUtils.copy(is, fileOutputStream);
            IOUtils.closeQuietly(is);
            Log.d("Aptoide-Parser", "Writed to " + cacheFile.getAbsolutePath());

        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
        return new FileInputStream(cacheFile);
    }

    protected void readBytes(final InputStream in, final ProgressByteProcessor processor) throws IOException {
        final byte[] buf = new byte[BUF_SIZE];
        try {
            int amt;
            do {
                amt = in.read(buf);
                if (amt == -1) {
                    break;
                }
            } while (processor.processBytes(buf, 0, amt));
        } finally {
            in.close();
        }
    }
}