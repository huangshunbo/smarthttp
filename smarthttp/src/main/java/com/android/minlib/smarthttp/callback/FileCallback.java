package com.android.minlib.smarthttp.callback;

import com.android.minlib.smarthttp.okhttp.SmartHttp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

public abstract class FileCallback extends AbstractCallback<File> {
    private String mSaveFileDir;
    private String mSaveFileName;

    public FileCallback(String saveFileDir, String saveFileName) {
        this.mSaveFileDir = saveFileDir;
        this.mSaveFileName = saveFileName;
    }

    private File saveFile(Response response) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;

        File var13;
        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0L;
            File dir = new File(this.mSaveFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, this.mSaveFileName);
            file.deleteOnExit();
            file.createNewFile();
            fos = new FileOutputStream(file);
            float lastProgress = 0.0F;

            int len;
            while((len = is.read(buf)) != -1) {
                sum += (long)len;
                fos.write(buf, 0, len);
                final float newProgress = (float)sum * 1.0F / (float)total;
                if ((int)(100.0F * newProgress) - (int)(100.0F * lastProgress) > 1) {
                    SmartHttp.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            FileCallback.this.onProgress(newProgress, total);
                        }
                    });
                    lastProgress = newProgress;
                }
            }

            fos.flush();
            var13 = file;
        } finally {
            try {
                response.body().close();
                if (is != null) {
                    is.close();
                }
            } catch (IOException var24) {
                var24.printStackTrace();
            }

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException var23) {
                var23.printStackTrace();
            }

        }

        return var13;
    }

    @Override
    public File parseResponse(Response response) throws IOException {
        return this.saveFile(response);
    }

    public abstract void onProgress(float progress,float total);
}
