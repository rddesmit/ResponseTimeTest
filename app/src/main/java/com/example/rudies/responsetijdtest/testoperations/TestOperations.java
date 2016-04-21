package com.example.rudies.responsetijdtest.testoperations;

import org.apache.commons.math3.analysis.function.Gaussian;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.ParetoDistribution;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by rudies on 25-3-2016.
 */
public class TestOperations<T> {

    private static final double mean = 140;
    private static final double stDev = 63;
    private static final LogNormalDistribution random = new LogNormalDistribution();

    public final Func1<T, Observable<T>> doLongOperation = new Func1<T, Observable<T>>() {
        @Override
        public Observable<T> call(final T value) {
            double responseTime = stDev * random.sample() + mean;
            return Observable.timer((long) responseTime, TimeUnit.MILLISECONDS)
                    .map(new Func1<Long, T>() {
                        @Override
                        public T call(Long aLong) {
                            return value;
                        }
                    })
                    .first();
        }
    };
}
