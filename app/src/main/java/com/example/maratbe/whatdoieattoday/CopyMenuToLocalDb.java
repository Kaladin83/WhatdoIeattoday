package com.example.maratbe.whatdoieattoday;

import android.app.job.JobParameters;
import android.app.job.JobService;

public class CopyMenuToLocalDb extends JobService {
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
//        FirebaseDbToRoomDataUpdateTask dbUpdateTask = new FirebaseDbToRoomDataUpdateTask();
//        dbUpdateTask.getCouponsFromFirebaseUpdateLocalDb(this);

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
