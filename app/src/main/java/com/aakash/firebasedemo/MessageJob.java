package com.aakash.firebasedemo;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.widget.Toast;

public class MessageJob extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    public class FirebaseTask extends AsyncTask<String, Integer, String>{

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
}
