package com.example.rudies.responsetijdtest.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.rudies.responsetijdtest.R;
import com.example.rudies.responsetijdtest.actors.LogActor;
import com.example.rudies.responsetijdtest.actors.TestActor;
import com.example.rudies.responsetijdtest.loging.Logger;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.routing.ScatterGatherFirstCompletedPool;
import akka.routing.TailChoppingPool;
import akka.util.Timeout;
import scala.PartialFunction;
import scala.concurrent.duration.FiniteDuration;

import static akka.pattern.Patterns.ask;
import static akka.pattern.Patterns.pipe;

public class TestActivity extends AppCompatActivity {

    private static final String ACTION = "tailChopping";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_time_multi_actor);

        final FiniteDuration within = FiniteDuration.apply(1, TimeUnit.DAYS);
        final FiniteDuration interval = FiniteDuration.apply(200, TimeUnit.MILLISECONDS);
        final Timeout timeout = Timeout.apply(1, TimeUnit.DAYS);

        final Config config = ConfigFactory.parseString("" +
                "akka {\n" +
                "  loglevel = \"DEBUG\"\n" +
                "}");
        final ActorSystem actorSystem = ActorSystem.create("system", config);


        switch (ACTION){
            case "single": {
                final ActorRef logActor = actorSystem.actorOf(LogActor.props("log_single_actor_static.txt"));
                final ActorRef actorRef = actorSystem.actorOf(TestActor.props());

                for (int i = 0; i < 1000; i++) {
                    pipe(ask(actorRef, "test", timeout), actorSystem.dispatcher()).to(logActor);
                }
                break;
            }
            case "tailChopping": {
                final ActorRef logActor = actorSystem.actorOf(LogActor.props("log_tail_chopping_actor_static.txt"));
                final ActorRef actorRef = actorSystem.actorOf(new TailChoppingPool(2, within, interval).props(TestActor.props()));

                for(int i = 0; i < 1000; i++){
                    pipe(ask(actorRef, "test", timeout), actorSystem.dispatcher()).to(logActor);
                }
                break;
            }
            case "scatterGather": {
                final ActorRef logActor = actorSystem.actorOf(LogActor.props("log_scatter_gather_actor_static.txt"));
                final ActorRef actorRef = actorSystem.actorOf(new ScatterGatherFirstCompletedPool(2, within).props(TestActor.props()));

                for(int i = 0; i < 1000; i++){
                    pipe(ask(actorRef, "test", timeout), actorSystem.dispatcher()).to(logActor);
                }
                break;
            }
        }

    }
}
