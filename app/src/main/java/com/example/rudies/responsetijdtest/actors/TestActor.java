package com.example.rudies.responsetijdtest.actors;

import com.example.rudies.responsetijdtest.testoperations.TestOperations;
import com.example.rudies.responsetijdtest.timemeasurement.TimeMeasurement;

import akka.actor.Props;
import akka.actor.UntypedActor;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Timestamped;

/**
 * Created by rudies on 13-4-2016.
 */
public class TestActor extends UntypedActor {

    private final Action1<Long> respond = new Action1<Long>() {
        @Override
        public void call(Long aLong) {
            getSender().tell(String.valueOf(aLong), self());
        }
    };

    public static Props props() {
        return Props.create(TestActor.class);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof String) {
            Observable.just(null)
                    .timestamp()
                    .flatMap(new TestOperations<Timestamped<Object>>().doLongOperation)
                    .map(TimeMeasurement.getDuration)
                    .toBlocking()
                    .subscribe(respond);
        } else {
            unhandled(message);
        }
    }
}
