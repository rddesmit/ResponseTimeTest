package com.example.rudies.responsetijdtest.actors;

import android.os.Environment;

import com.example.rudies.responsetijdtest.loging.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import akka.actor.Actor;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

/**
 * Created by rudies on 13-4-2016.
 */
public class LogActor extends UntypedActor {

    private final String fileName;
    private PrintWriter writer = null;

    private LogActor(final String fileName){
        this.fileName = fileName;
    }

    public static Props props(final String fileName) {
        return Props.create(new Creator<Actor>(){

            @Override
            public Actor create() throws Exception {
                return new LogActor(fileName);
            }
        });
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();

        final File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        final File file = new File(path, fileName);

        path.mkdirs();

        writer = new PrintWriter(new BufferedWriter(new FileWriter(file, true)));
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if(message instanceof String){
            writer.println((String) message);
            writer.flush();
            System.out.println((String) message);
        } else {
            unhandled(message);
        }
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();

        if (writer == null) return;
        writer.flush();
        writer.close();
    }

}
