package com.example.ashishkrishna.gps;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.lang.reflect.Field;

/**
 * Created by qinbian on 8/6/15.
 */
public class PassiveUnitActivity extends Activity {

    NumberPicker unitPicker;
    ImageButton confirmButton;
    String pickerValue;
    String[] units;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passive_unit);
        unitPicker = (NumberPicker) findViewById(R.id.unitPicker);
        setDividerColor(unitPicker, Color.parseColor("#E81D58"));
        confirmButton = (ImageButton) findViewById(R.id.confirm_icon);
        createWordSpinner();
        confirm();

    }

    private void createWordSpinner(){
        units = new String[2];
        units[0] = "Feet";
        units[1] = "Miles";
        unitPicker.setMaxValue(units.length -1);
        unitPicker.setMinValue(0);
        unitPicker.setDisplayedValues(units);
    }

    private void confirm(){
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerValue = units[unitPicker.getValue()];
//                Toast.makeText(getApplicationContext(), pickerValue,
//                        Toast.LENGTH_SHORT).show();
                if (pickerValue.equals("Miles")) {
                     new Thread(new Runnable() {
                         @Override
                         public void run(){
                                 Intent openMilesIntent = new Intent(PassiveUnitActivity.this, PassiveMilesActivity.class);
                                 PassiveUnitActivity.this.startActivity(openMilesIntent);

                             }
                    }).start();
                    //finish();
                    try {
                        Thread.currentThread().sleep(500);
                    }
                    catch(Exception f) {
                        f.printStackTrace();
                    }


                } else {

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Intent openFeetIntent = new Intent(PassiveUnitActivity.this, PassiveFeetActivity.class);
                            PassiveUnitActivity.this.startActivity(openFeetIntent);
                        }
                    }).start();
                    try {
                        Thread.currentThread().sleep(500);
                    }
                    catch(Exception g) {
                        g.printStackTrace();
                    }
                    //finish();

                }
            }
        });

    }

    private void setDividerColor(NumberPicker picker, int color) {

        java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (java.lang.reflect.Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    ColorDrawable colorDrawable = new ColorDrawable(color);
                    pf.set(picker, colorDrawable);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


}
