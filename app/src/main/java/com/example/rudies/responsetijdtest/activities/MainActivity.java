package com.example.rudies.responsetijdtest.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.rudies.responsetijdtest.R;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1;

    //function wrappers
    private static final Func1<Boolean, Boolean> isTrue = new Func1<Boolean, Boolean>() {
        @Override
        public Boolean call(Boolean aBoolean) {
            return aBoolean;
        }
    };

    private static final Func2<Boolean, Boolean, Boolean> areBothTrue = new Func2<Boolean, Boolean, Boolean>() {
        @Override
        public Boolean call(Boolean aBoolean, Boolean aBoolean2) {
            return aBoolean && aBoolean2;
        }
    };


    private final Action1<Object> navigateToMultiActorActivity = new Action1<Object>() {
        @Override
        public void call(Object o) {
            navigateToResponseTimeMultiActorActivity();
        }
    };

    //observables
    private static final Subject<Boolean, Boolean> hasPermission = BehaviorSubject.create();
    private static final Subject<Boolean, Boolean> buttonClicked = PublishSubject.create();

    private static final Observable<Boolean> navigate = Observable
            .combineLatest(hasPermission, buttonClicked, areBothTrue)
            .filter(isTrue);

    private Subscription buttonSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button4).setOnClickListener(this);

        if(hasPermissions()){
            hasPermission.onNext(true);
        }
        else{
            hasPermission.onNext(false);
            getPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        buttonSubscription = navigate.subscribe(navigateToMultiActorActivity);
    }

    private boolean hasPermissions(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void getPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
    }

    private void navigateToResponseTimeMultiActorActivity(){
        final Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
    }

    private static boolean isSinglePermissionGranted(int[] grantResults){
        return grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE:
                if(isSinglePermissionGranted(grantResults)){
                    hasPermission.onNext(true);
                }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button4:
                buttonClicked.onNext(true);
                break;

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        buttonSubscription.unsubscribe();
    }
}
