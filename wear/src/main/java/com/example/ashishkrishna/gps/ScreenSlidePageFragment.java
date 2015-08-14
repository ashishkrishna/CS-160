package com.example.ashishkrishna.gps;

/**
 * Created by noon on 8/2/15.
 */
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.wearable.activity.ConfirmationActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

public class ScreenSlidePageFragment extends Fragment {
    private ImageButton StartNav;
    private Handler mHandler;
    private int mPageNumber;
    public static final String ARG_PAGE = "page";
    public ArrayList<String> listItems;
    public ArrayList<String> listcoords;

    public static ScreenSlidePageFragment create(int pageNumber) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
        listItems = new ArrayList<String>();
        listcoords = new ArrayList<String>();
        int i = 0;
        while (i < Listener.itemizer.length) {

            String pc1 = Listener.itemizer[i];
            String[] d = pc1.split("Lat:");

            String d0 = d[0];
            String d1 = d[1];
            Log.w("Part1", d0);
            Log.w("Part2", d1);
            listItems.add(d0);
            listcoords.add(d1);
            i++;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.active, container, false);
        String a1 = listItems.get(mPageNumber);
        String[] d = a1.split("Rating:");
        String[] e = d[1].split("Gender:");
        String[] f = e[1].split("ADA:");

        e[0] = e[0].replaceAll("\\s+", "");
        ImageView a3 = (ImageView) rootView.findViewById(R.id.active_rating);
        if (e[0].equals("1")) {
            a3.setImageResource(R.drawable.active_soda_rating);
        }
        if (e[0].equals("2")) {
            a3.setImageResource(R.drawable.two_stars);
        }
        if (e[0].equals("3")) {
            a3.setImageResource(R.drawable.three_stars);
        }
        if (e[0].equals("4")) {
            a3.setImageResource(R.drawable.four_stars);
        }
        if (e[0].equals("5")) {
            a3.setImageResource(R.drawable.active_hearst_rating);
        }
        ImageView a4 = (ImageView) rootView.findViewById(R.id.active_gender);
        ImageView a5 = (ImageView) rootView.findViewById(R.id.active_ADA);
        f[0] = f[0].replaceAll("\\s+", "");
        if(f[0].equals("male")) {
            a4.setImageResource(R.drawable.active_male);
        }
        if(f[0].equals("female")) {
            a4.setImageResource(R.drawable.active_female);
        }
        if(f[0].equals("genderneutral")) {
            a4.setImageResource(R.drawable.active_genderneutral);
        }
        f[1] = f[1].replaceAll("\\s+", "");
        if(f[1].equals("true")) {
            a5.setImageResource(R.drawable.active_ada);
        }
        else if (f[1].equals("false")) {
            a5.setImageResource(android.R.color.transparent);
        }
        TextView v1 = (TextView) rootView.findViewById(R.id.active_name);
        v1.setText(d[0]);
        mHandler = new Handler();

        StartNav = (ImageButton) rootView.findViewById(R.id.active_confirm);
        initSelectionButton(StartNav);

        return rootView;
    }


    //Set up listener for selection buttons
    private void initSelectionButton(ImageButton IButton) {

        IButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartNav.setImageResource(R.drawable.check_circle_clicked);
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        StartNav.setImageResource(R.drawable.check_circle);
                    }
                }, 2000);

//normally, we would start maps activity
//but for demo purposes, let's create an arrived notification.
//createNotification();

                startMap();
            }
        });
    }

    private void startMap() {
//active 2
        String a = listcoords.get(mPageNumber);
        String[] b = a.split("Lon:");
        String l1 = b[0];
        String l2 = b[1];
        l1.replaceAll("\\s+", "");
        l2.replaceAll("\\s+", "");
        MapsActivity.lat = l1;
        MapsActivity.lon = l2;
        Intent gamma = new Intent(getActivity(), MapsActivity.class);
        startActivity(gamma);
    }

    private void createNotification() {
        int notificationId = 001;
// Build intent for notification content
        Intent viewIntent = new Intent(getActivity(), InvisibleActivity.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(getActivity(), 0, viewIntent, 0);

        Intent helpMeIDKIntent = new Intent(getActivity(), ConfirmationActivity.class);
        helpMeIDKIntent.putExtra(ConfirmationActivity.EXTRA_ANIMATION_TYPE, ConfirmationActivity.OPEN_ON_PHONE_ANIMATION);
        helpMeIDKIntent.putExtra(ConfirmationActivity.EXTRA_MESSAGE,
                getString(R.string.phone_opened));

        PendingIntent helpPendingIntent =
                PendingIntent.getActivity(getActivity(), 0, helpMeIDKIntent, 0);

        Bitmap bitmapbg = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.active_bg);


        String restroomName = "Sutardja Dai";
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.drawable.watch_icon)
                        .setContentTitle("You've arrived")
                        .setContentText("at " + restroomName + "!")
                        .setContentIntent(viewPendingIntent)
                        .addAction(R.drawable.report_action_button, "Report Error", helpPendingIntent)
                        .extend(new NotificationCompat.WearableExtender().setBackground(bitmapbg));
// Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getActivity());

// Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());

    }
}