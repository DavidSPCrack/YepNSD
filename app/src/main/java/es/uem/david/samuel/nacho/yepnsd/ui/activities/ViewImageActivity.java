package es.uem.david.samuel.nacho.yepnsd.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.Timer;
import java.util.TimerTask;

import es.uem.david.samuel.nacho.yepnsd.R;
import es.uem.david.samuel.nacho.yepnsd.constants.Constantes;


public class ViewImageActivity extends AbstractActionBarActivity {

    private static final int TIME_TO_CLOSE = 10 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        ActionBar actionBar = getSupportActionBar();

        Intent intent = getIntent();
        ImageView photo = (ImageView) findViewById(R.id.imageView);

        String sender = intent.getStringExtra(Constantes.ParseClasses.Messages.KEY_SENDER_NAME);
        String createdAt = intent.getStringExtra(Constantes.ParseClasses.Messages.KEY_CREATED_AT);
        actionBar.setTitle(sender);
        actionBar.setSubtitle(createdAt);

        Uri uriImg = intent.getData();

        Picasso pic = Picasso.with(this);
        RequestCreator req = pic.load(uriImg);
        req.into(photo);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, TIME_TO_CLOSE);

    }
}
