

package com.example.ashishkrishna.gps;

        import android.app.Activity;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.graphics.Color;
        import android.media.Image;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.v4.app.NavUtils;
        import android.support.v7.app.ActionBar;
        import android.support.v7.app.ActionBarActivity;
        import android.util.Log;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.ImageButton;
        import android.content.Context;

        import java.io.BufferedReader;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.FileReader;
        import java.io.FileWriter;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.io.PrintWriter;


/**
 * Created by qinbian on 8/2/15.
 */
public class SettingActivity extends ActionBarActivity {

    private ImageButton maleSetting;
    private ImageButton femaleSetting;
    private ImageButton genderNeutralSetting;
    private ImageButton adaSetting;
    public String filename = "settingsgpees.txt";
    public FileOutputStream fr1;
    public FileInputStream fr2;
    public StringBuffer crosscheck;
    public FileOutputStream streamo;
    public FileInputStream streami;
    public File a;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Context now = this;
        maleSetting = (ImageButton) findViewById(R.id.male_icon);
        femaleSetting = (ImageButton) findViewById(R.id.female_icon);
        genderNeutralSetting = (ImageButton) findViewById(R.id.genderneutral_icon);
        adaSetting = (ImageButton) findViewById(R.id.ada_icon);
        //File a = new File(this.getFilesDir(), "settingsgps.txt");
        SharedPreferences settings = getSharedPreferences("prefs", 0);
        boolean firstRun = settings.getBoolean("firstRun", true);
        if(firstRun) {
            try {
                Log.w("FIRST RUN", "R/W");
                //fr1 = new FileOutputStream(a);
                a = new File(this.getFilesDir(), "settingsgps.txt");
                fr1 = this.openFileOutput("settingsgps.txt", MODE_WORLD_WRITEABLE);
                reinit();
                boolean l = fr2.markSupported();
                if (l == false) {
                    Log.w("Truth: ", "True");
                } else {
                    Log.w("Truth:", " ");
                }

                Log.w("Last changed: ", Long.toString(a.lastModified()));
                try {
                    FileWriter fstream = new FileWriter(a, true);
                    PrintWriter pstream = new PrintWriter(fstream);
                    pstream.print("");
                    pstream.print("NNNN");
                    pstream.close();


                    InvisibleActivity.setter = 1;
                } catch (IOException b) {

                }

            }
            catch (FileNotFoundException r) {
                r.printStackTrace();
            }
            MainActivity.x = 0;
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("firstRun", false);
            editor.commit();
        }
        else {
            Log.w("NOT THE FIRST", "R/W");
            a = new File(this.getFilesDir(), "settingsgps.txt");
            reading();
            reinit();
            String d1 = reading();
            Log.w("File now:", d1);
            if (d1.charAt(0) == 'Y') {
                maleSetting.setSelected(true);
                femaleSetting.setSelected(false);
                genderNeutralSetting.setSelected(false);
            }
            else {
                maleSetting.setSelected(false);
            }
            if (d1.charAt(1) == 'Y') {
                femaleSetting.setSelected(true);
                maleSetting.setSelected(false);
                genderNeutralSetting.setSelected(false);
            }
            else {
                femaleSetting.setSelected(false);
            }
            if (d1.charAt(2) == 'Y') {
                genderNeutralSetting.setSelected(true);
                maleSetting.setSelected(false);
                femaleSetting.setSelected(false);
            }
            else {
                genderNeutralSetting.setSelected(false);
            }
            if (d1.charAt(3) == 'Y') {
                adaSetting.setSelected(true);
            }
            else {
                adaSetting.setSelected(false);
            }


        }
        //





        // streamo = openFileOutput(filename, Context.MODE_PRIVATE);
            /*streami = openFileInput(filename);
            crosscheck = new StringBuffer();
            BufferedReader tau = new BufferedReader(new InputStreamReader(streami));
            if (streami != null) {
                String f = "not here";
                try {
                   f = tau.readLine();
                    if (f != null) {
                        Log.w("HERE", f);
                    }
                }
                catch (IOException b) {
                    Log.w("ERROR on read", "E");
                }
                crosscheck.append(f);

            }
            Log.w("MESSAGE:", "File found");

        }
        catch(FileNotFoundException d) {
            File c = new File(this.getFilesDir(), filename);
            try {
                openFileOutput(filename, Context.MODE_PRIVATE);
                Log.w("MESSAGE:", "file retrieved from new");
            }
            catch(FileNotFoundException n) {
                Log.w("ERROR-","File not found");
            }


        } */

        maleSettingChange();
        femaleSettingChange();
        genderNeutralSettingChange();
        adaSettingChange();
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        final ActionBar actionBar = getSupportActionBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(3, 155, 229));
        }
        actionBar.setDisplayShowHomeEnabled(false);

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }
    public void reinit() {
        try {
            fr2 = this.openFileInput("settingsgps.txt");
        }
        catch(FileNotFoundException w) {

        }
    }
    public String reading() {
        String toreturn = "NONE";
        FileInputStream y;
        try {
            y = this.openFileInput("settingsgps.txt");
        }
        catch(FileNotFoundException n) {
            n.printStackTrace();
            return "Done";
        }
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(y));
        String Read;
        try {
            if (y != null) {
                while ((Read = reader.readLine()) != null) {
                    buffer.append(Read + "\n");
                }
                toreturn = buffer.toString();

            }
        }
        catch (IOException h) {
            h.printStackTrace();
        }
        return toreturn;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                Intent beta = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(beta);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void maleSettingChange(){
        maleSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maleSetting.isSelected()) {
                    maleSetting.setSelected(false);
                    try {
                        // fr2.mark(0);
                        reinit();

                        StringBuffer buffer = new StringBuffer();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fr2));
                        String Read;
                        if (fr2!=null) {
                            while ((Read = reader.readLine()) != null) {
                                buffer.append(Read + "\n" );
                            }

                        }


                        Log.w("Buffer:", buffer.toString());
                        if(buffer.toString().charAt(0) == 'Y') {
                            String parsing = "N"+buffer.toString().substring(1,4);
                            FileOutputStream writer = openFileOutput("settingsgps.txt", MODE_WORLD_WRITEABLE);
                            writer.write((new String()).getBytes());
                            FileWriter fstream = new FileWriter(a, true);
                            PrintWriter pstream = new PrintWriter(fstream);
                            pstream.print("");
                            pstream.print(parsing);
                            pstream.close();
                            //fr1 = SettingActivity.this.openFileOutput("settingsgps.txt", MODE_WORLD_WRITEABLE);
                            //fr1.write(parsing.getBytes());
                            // fr1.flush();
                        }

                    }
                    catch (IOException j) {
                        j.printStackTrace();
                    }
                    Log.w("File:", "File found");
                    String b1 = reading();
                    Log.w("This is the state now:", b1);



                } else {
                    maleSetting.setSelected(true);
                    try {
                        // fr2.mark(0);
                        reinit();

                        StringBuffer buffer = new StringBuffer();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fr2));
                        String Read;
                        if (fr2!=null) {
                            while ((Read = reader.readLine()) != null) {
                                buffer.append(Read + "\n" );
                            }

                        }


                        Log.w("Buffer:", buffer.toString());
                        if(buffer.toString().charAt(0) == 'N') {
                            String parsing = "Y"+"NN"+buffer.toString().substring(3,4);
                            FileOutputStream writer = openFileOutput("settingsgps.txt", MODE_WORLD_WRITEABLE);
                            writer.write((new String()).getBytes());
                            writer.close();
                            FileWriter fstream = new FileWriter(a, true);
                            PrintWriter pstream = new PrintWriter(fstream);
                            pstream.print("");
                            pstream.print(parsing);
                            pstream.close();
                            femaleSetting.setSelected(false);
                            genderNeutralSetting.setSelected(false);
                        }

                    }
                    catch (IOException j) {
                        j.printStackTrace();
                    }
                    Log.w("File:", "File found");
                    String b1 = reading();
                    Log.w("This is the state now:", b1);
                }
            }
        });
    }

    private void femaleSettingChange(){
        femaleSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (femaleSetting.isSelected()){
                    femaleSetting.setSelected(false);
                    try {
                        reinit();
                        //fr2.mark(0);
                        StringBuffer buffer = new StringBuffer();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fr2));
                        String Read;
                        if (fr2!=null) {
                            while ((Read = reader.readLine()) != null) {
                                buffer.append(Read + "\n" );
                            }

                        }

                        String str1 = reading();
                        Log.w("Buffer:", buffer.toString());
                        if(buffer.toString().charAt(1) == 'Y') {
                            String parsing = str1.toString().substring(0,1) + "N" + str1.toString().substring(2,4);
                            FileOutputStream writer = openFileOutput("settingsgps.txt", MODE_WORLD_WRITEABLE);
                            writer.write((new String()).getBytes());
                            writer.close();
                            FileWriter fstream = new FileWriter(a, true);
                            PrintWriter pstream = new PrintWriter(fstream);
                            pstream.print("");
                            pstream.print(parsing);
                            pstream.close();

                        }

                    }
                    catch (IOException j) {
                        j.printStackTrace();
                    }
                    Log.w("File:", "File found");
                    String b1 = reading();
                    Log.w("This is the state now:", b1);
                } else {
                    femaleSetting.setSelected(true);
                    try {
                        reinit();
                        //fr2.mark(0);
                        String str1 = reading();


                        Log.w("Buffer:", str1);
                        if(str1.charAt(1) == 'N') {
                            String parsing = "NYN" + str1.toString().substring(3,4);
                            FileOutputStream writer = openFileOutput("settingsgps.txt", MODE_WORLD_WRITEABLE);
                            writer.write((new String()).getBytes());
                            writer.close();
                            FileWriter fstream = new FileWriter(a, true);
                            PrintWriter pstream = new PrintWriter(fstream);
                            pstream.print("");
                            pstream.print(parsing);
                            pstream.close();
                            maleSetting.setSelected(false);
                            genderNeutralSetting.setSelected(false);

                        }

                    }
                    catch (IOException j) {
                        j.printStackTrace();
                    }
                    /*IOUtil.close(fr1); */

                    Log.w("File:", "File found");
                    String b1 = reading();
                    Log.w("This is the state now:", b1);
                }

            }
        });
    }

    private void genderNeutralSettingChange(){
        genderNeutralSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (genderNeutralSetting.isSelected()){
                    genderNeutralSetting.setSelected(false);
                    try {
                        //fr2.mark(16);
                        reinit();
                        StringBuffer buffer = new StringBuffer();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fr2));
                        String Read;
                        if (fr2!=null) {
                            while ((Read = reader.readLine()) != null) {
                                buffer.append(Read + "\n" );
                            }

                        }



                        Log.w("Buffer:", buffer.toString());
                        if(buffer.toString().charAt(2) == 'Y') {
                            String parsing = buffer.toString().substring(0,2) + "N" + buffer.toString().substring(3,4);
                            FileOutputStream writer = openFileOutput("settingsgps.txt", MODE_WORLD_WRITEABLE);
                            writer.write((new String()).getBytes());
                            writer.close();
                            FileWriter fstream = new FileWriter(a, true);
                            PrintWriter pstream = new PrintWriter(fstream);
                            pstream.print("");
                            pstream.print(parsing);
                            pstream.close();
                        }

                    }
                    catch (IOException j) {
                        j.printStackTrace();
                    }
                    Log.w("File:", "File found");
                    String b1 = reading();
                    Log.w("This is the state now:", b1);
                } else {
                    genderNeutralSetting.setSelected(true);
                    try {
                        //fr2.mark(16);
                        reinit();
                        StringBuffer buffer = new StringBuffer();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fr2));
                        String Read;
                        if (fr2!=null) {
                            while ((Read = reader.readLine()) != null) {
                                buffer.append(Read + "\n" );
                            }

                        }



                        Log.w("Buffer:", buffer.toString());
                        if(buffer.toString().charAt(2) == 'N') {
                            String parsing = "NNY" + buffer.toString().substring(3,4);
                            FileOutputStream writer = openFileOutput("settingsgps.txt", MODE_WORLD_WRITEABLE);
                            writer.write((new String()).getBytes());
                            writer.close();
                            FileWriter fstream = new FileWriter(a, true);
                            PrintWriter pstream = new PrintWriter(fstream);
                            pstream.print("");
                            pstream.print(parsing);
                            pstream.close();
                            maleSetting.setSelected(false);
                            femaleSetting.setSelected(false);

                        }

                    }
                    catch (IOException j) {
                        j.printStackTrace();
                    }
                    Log.w("File:", "File found");
                    String b1 = reading();
                    Log.w("This is the state now:", b1);

                }
            }
        });
    }

    private void adaSettingChange(){
        adaSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adaSetting.isSelected()){
                    adaSetting.setSelected(false);
                    try {
                        //fr2.mark(16);
                        reinit();
                        StringBuffer buffer = new StringBuffer();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fr2));
                        String Read;
                        if (fr2!=null) {
                            while ((Read = reader.readLine()) != null) {
                                buffer.append(Read + "\n" );
                            }

                        }



                        Log.w("Buffer:", buffer.toString());
                        if(buffer.toString().charAt(3) == 'Y') {
                            String parsing = buffer.toString().substring(0,3) + "N";
                            FileOutputStream writer = openFileOutput("settingsgps.txt", MODE_WORLD_WRITEABLE);
                            writer.write((new String()).getBytes());
                            writer.close();
                            FileWriter fstream = new FileWriter(a, true);
                            PrintWriter pstream = new PrintWriter(fstream);
                            pstream.print("");
                            pstream.print(parsing);
                            pstream.close();
                        }

                    }
                    catch (IOException j) {
                        j.printStackTrace();
                    }
                    Log.w("File:", "File found");
                    String b1 = reading();
                    Log.w("This is the state now:", b1);
                } else {
                    adaSetting.setSelected(true);
                    try {
                        //fr2.mark(16);
                        reinit();
                        StringBuffer buffer = new StringBuffer();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(fr2));
                        String Read;
                        if (fr2!=null) {
                            while ((Read = reader.readLine()) != null) {
                                buffer.append(Read + "\n" );
                            }

                        }



                        Log.w("Buffer:", buffer.toString());
                        if(buffer.toString().charAt(3) == 'N') {
                            String parsing = buffer.toString().substring(0,3) + "Y";
                            FileOutputStream writer = openFileOutput("settingsgps.txt", MODE_WORLD_WRITEABLE);
                            writer.write((new String()).getBytes());
                            writer.close();
                            FileWriter fstream = new FileWriter(a, true);
                            PrintWriter pstream = new PrintWriter(fstream);
                            pstream.print("");
                            pstream.print(parsing);
                            pstream.close();
                        }

                    }
                    catch (IOException j) {
                        j.printStackTrace();
                    }
                    Log.w("File:", "File found");
                    String b1 = reading();
                    Log.w("This is the state now:", b1);
                }
            }
        });
    }


}

