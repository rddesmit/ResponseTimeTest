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
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.Timestamped;

public class ResponseTimeMultiThreadActivity extends AppCompatActivity {

    private static final String TAG = "ResponseTimeMultiThreadActivity";
    private static final String FILE_NAME = "log_multi_thread.txt";

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
        setContentView(R.layout.activity_response_time_mulity_thread);

        Observable<Long> actionDurations = Observable.interval(100, TimeUnit.MILLISECONDS, scheduler)
                .take(1000)
                .timestamp()
                .map(new Func1<Timestamped<Long>, Timestamped<Long>>() {
                    @Override
                    public Timestamped<Long> call(final Timestamped<Long> aLong) {
                        Observable<Long> first = Observable.just(null)
                                .timestamp()
                                .map(new TestOperations<Timestamped<Object>>().doLongOperation)
                                .map(TimeMeasurement.getDuration);

                        Observable<Long> second = Observable.just(null)
                                .timestamp()
                                .map(new TestOperations<Timestamped<Object>>().doLongOperation)
                                .map(TimeMeasurement.getDuration);

                        first.mergeWith(second).toBlocking().first();

                        return aLong;
                    }
                })
                .map(TimeMeasurement.getDuration);

        logSubscription = actionDurations.subscribe(persistenceLog);
    }

    @Override
    protected void onPause() {
        super.onPause();
        logSubscription.unsubscribe();
    }
}
