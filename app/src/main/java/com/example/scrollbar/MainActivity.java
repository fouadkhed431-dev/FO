package com.example.scrollbar;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.provider.Settings;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.*;

public class MainActivity extends Activity {
    SharedPreferences prefs;
    LinearLayout root;
    int green=0xFF397A58;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        prefs=getSharedPreferences("scrollbar",MODE_PRIVATE);
        root=new LinearLayout(this); root.setOrientation(1); root.setPadding(28,32,28,24); root.setBackgroundColor(0xFF101815);
        TextView title=new TextView(this); title.setText("ScrollBar"); title.setTextSize(30); title.setTextColor(0xFFF5F1E8); root.addView(title);
        TextView sub=new TextView(this); sub.setText("Floating scroll assistant"); sub.setTextSize(15); sub.setTextColor(0xFFB9C9BE); sub.setPadding(0,6,0,22); root.addView(sub);
        Button access=button("1 · Enable Accessibility Service"); access.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))); root.addView(access);
        Button overlay=button("2 · Start / refresh floating bar"); overlay.setOnClickListener(v->{ if(!Settings.canDrawOverlays(this)) startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)); else { startService(new Intent(this,ScrollAccessibilityService.class)); Toast.makeText(this,"If not visible, enable the ScrollBar accessibility service too.",Toast.LENGTH_LONG).show(); }}); root.addView(overlay);
        label("Default speed"); Spinner speeds=new Spinner(this); String[] vals={"Fast","Medium","Slow"}; speeds.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,vals)); speeds.setSelection(prefs.getInt("speedChoice",0)); speeds.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){} public void onItemSelected(android.widget.AdapterView<?> p,android.view.View v,int pos,long id){prefs.edit().putInt("speedChoice",pos).apply();}}); root.addView(speeds);
        label("Handle size"); SeekBar size=new SeekBar(this); size.setMax(3); size.setProgress(prefs.getInt("size",1)); size.setOnSeekBarChangeListener(seek(v->prefs.edit().putInt("size",v+1).apply())); root.addView(size);
        label("Transparency"); SeekBar alpha=new SeekBar(this); alpha.setMax(220); alpha.setProgress(prefs.getInt("opacity",170)); alpha.setOnSeekBarChangeListener(seek(v->prefs.edit().putInt("opacity",v+35).apply())); root.addView(alpha);
        label("Position"); Spinner pos=new Spinner(this); pos.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Right edge","Left edge"})); pos.setSelection(prefs.getInt("edge",0)); pos.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){} public void onItemSelected(android.widget.AdapterView<?> p,android.view.View v,int n,long id){prefs.edit().putInt("edge",n).apply();}}); root.addView(pos);
        label("Appearance"); Spinner theme=new Spinner(this); theme.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,new String[]{"Dark","Light"})); theme.setSelection(prefs.getInt("theme",0)); theme.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener(){public void onNothingSelected(android.widget.AdapterView<?> p){} public void onItemSelected(android.widget.AdapterView<?> p,android.view.View v,int n,long id){prefs.edit().putInt("theme",n).apply();}}); root.addView(theme);
        TextView info=new TextView(this); info.setText("\nHow to use\n• Tap the upper section of the bar to scroll up.\n• Tap the lower section to scroll down.\n• Tap the middle section to start/stop auto-scroll.\n• Drag the bar to move it vertically.\n\nAndroid limitation: scroll position seeking depends on whether the app exposes scroll range information. This version uses accessibility scroll actions and cannot guarantee pixel-perfect jumps in every app."); info.setTextColor(0xFFE4EAE5); info.setTextSize(14); root.addView(info);
        ScrollView sc=new ScrollView(this); sc.addView(root); setContentView(sc);
    }
    private Button button(String t){Button b=new Button(this);b.setText(t);return b;}
    private void label(String t){TextView v=new TextView(this);v.setText(t);v.setTextColor(0xFFE8EDE9);v.setPadding(0,18,0,4);root.addView(v);}
    private SeekBar.OnSeekBarChangeListener seek(java.util.function.IntConsumer f){return new SeekBar.OnSeekBarChangeListener(){public void onProgressChanged(SeekBar s,int p,boolean u){if(u)f.accept(p);}public void onStartTrackingTouch(SeekBar s){}public void onStopTrackingTouch(SeekBar s){}};}
}