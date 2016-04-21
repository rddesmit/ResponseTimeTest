package com.example.rudies.responsetijdtest.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.rudies.responsetijdtest.R;
import com.example.rudies.responsetijdtest.loging.Logger;
import com.example.rudies.responsetijdtest.testoperations.TestOperations;
import com.example.rudies.responsetijdtest.timemeasurement.TimeMeasurement;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.Timestamped;

public class ResponseTimeSingleThreadActivity extends AppCompatActivity {

    private static final String TAG = "ResponseTimeSingleThreadActivity";
    private static final String FILE_NAME = "log_single_thread.txt";

    private static final Scheduler scheduler = Schedulers.from(Executors.newCachedThreadPool());

    private Subscription logSubscription;

    private static final Action1<Long> persistenceLog = new Action1<Long>() {
        @Override
        public void call(Long aLong) {
            Logger.log(FILE_NAME, String.valueOf(aLong));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_time_single_thread_test);

        Observable<Long> actionDurations = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                .take(1000)
                .timestamp()
                .flatMap(new TestOperations<Timestamped<Long>>().doLongOperation)
                .map(TimeMeasurement.getDuration);

        logSubscription = actionDurations.subscribe(persistenceLog);
    }

    @Override
    protected void onPause() {
        super.onPause();
        logSubscription.unsubscribe();
    }
}
