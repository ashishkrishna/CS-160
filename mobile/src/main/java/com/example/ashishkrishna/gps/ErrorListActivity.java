package com.example.ashishkrishna.gps;

import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PorterDuff;
import android.os.IBinder;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.ashishkrishna.gps.R;


public class ErrorListActivity extends Activity implements ActionBar.TabListener {

  /*  /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    public static DatabaseService FirstServicer;
    ServiceConnection mConnection;
    public static ErrorListActivity error1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_list);

        // Set up the action bar.
        error1 = this;
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }




        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        mConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName className,
                                           IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                DatabaseService.LocalBinder binder = (DatabaseService.LocalBinder) service;
                FirstServicer = binder.getService();
                //binded = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {

            }
        };
    }
    @Override
    public void onStart() {
        super.onStart();
        Intent alpha = new Intent(ErrorListActivity.this, DatabaseService.class);
        bindService(alpha, mConnection, Context.BIND_AUTO_CREATE);
        startService(alpha);
        //Intent i =  new Intent(this, Listener.class);
        //startService(i);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_error_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return ErrorReportFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ErrorListFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ErrorListFragment newInstance(int sectionNumber) {
            ErrorListFragment fragment = new ErrorListFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ErrorListFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_error_list, container, false);
            return rootView;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class ErrorReportFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ErrorReportFragment newInstance(int sectionNumber) {
            ErrorReportFragment fragment = new ErrorReportFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public ErrorReportFragment() {
        }


        //button animations
        public static void buttonEffect(View button) {
            button.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            v.getBackground().setColorFilter(0xffBDBDBD, PorterDuff.Mode.SRC_ATOP);
                            v.invalidate();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            v.getBackground().clearColorFilter();
                            v.invalidate();
                            break;
                        }
                    }
                    return false;
                }
            });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View errorReport = inflater.inflate(R.layout.fragment_error_report, container, false);

            //start modifications
            //set restroom name in top line
            TextView restroomName = (TextView) errorReport.findViewById(R.id.nameOfRestroom);
            String passed_in_name = "Sutardja Dai 3rd Floor";
            restroomName.setText("Reporting " + passed_in_name);

            //onclick to make textfield visible for commenting
            View.OnClickListener enterText = new View.OnClickListener(){
                public void onClick(View v) {
                    EditText textEntry = (EditText) errorReport.findViewById(R.id.editText);
                    textEntry.setVisibility(View.VISIBLE);
                    textEntry.setTextColor(getResources().getColor(R.color.textFieldColor));
                }
            };

            //onclick to go back
            View.OnClickListener goBack = new View.OnClickListener(){
                public void onClick(View v) {
                    //load the previous fragment
                }
            };

            //onclick to log the report
            View.OnClickListener submitOC;


            //set the text field & button onclicks
            RadioButton rb = (RadioButton) errorReport.findViewById(R.id.otherText);
            rb.setOnClickListener(enterText);
            Button back = (Button) errorReport.findViewById(R.id.back);
            back.setOnClickListener(goBack);
            Button submit = (Button) errorReport.findViewById(R.id.submit);
            submit.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v) {
                    //get the text entered

                    //if it exists, log this
                    //if it's empty, send 'DNE' message
                    EditText textEntry = (EditText) errorReport.findViewById(R.id.editText);
                    if (textEntry.getText().toString().isEmpty()) {
                        FirstServicer.createnewComment();
                        FirstServicer.commerror = "DNE";
                        FirstServicer.attempt8.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), FirstServicer.commerror);
                        error1.finish();

                    }

                    else {
                        FirstServicer.createnewComment();
                        FirstServicer.commerror = textEntry.getText().toString();
                        FirstServicer.attempt8.execute(Double.toString(FirstServicer.latitudedoub), Double.toString(FirstServicer.longitudedoub), FirstServicer.commerror);
                        error1.finish();
                    }


                }

            });
            buttonEffect(back);
            buttonEffect(submit);

            return errorReport;
        }
    }

}

