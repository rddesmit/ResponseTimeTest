package com.example.rudies.responsetijdtest.timemeasurement;

import java.util.Calendar;

import rx.functions.Func1;
import rx.schedulers.Timestamped;

/**
 * Created by rudies on 25-3-2016.
 */
public class TimeMeasurement {

    public static final Func1<Timestamped<?>, Long> getDuration = new Func1<Timestamped<?>, Long>() {
        @Override
        public Long call(final Timestamped<?> timeStamp) {
            final Long endTime = Calendar.getInstance().getTimeInMillis();
            final Long startTime = timeStamp.getTimestampMillis();

            return endTime - startTime;
        }
    };

}
