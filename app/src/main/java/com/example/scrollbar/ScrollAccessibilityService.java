package com.example.scrollbar;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;
import android.graphics.*;
import android.view.*;
import android.os.*;
import android.content.*;
import android.widget.Toast;

public class ScrollAccessibilityService extends AccessibilityService {
    private WindowManager wm; private Bar bar; private WindowManager.LayoutParams lp;
    private Handler handler=new Handler(Looper.getMainLooper()); private boolean auto=false, forward=true;
    private int tick=180; private SharedPreferences prefs;
    @Override public void onServiceConnected(){super.onServiceConnected();prefs=getSharedPreferences("scrollbar",MODE_PRIVATE);wm=(WindowManager)getSystemService(WINDOW_SERVICE);show();}
    private int dp(float x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
    private void show(){
        if(bar!=null)return;
        bar=new Bar(this);
        int edge=prefs.getInt("edge",0)==0?Gravity.RIGHT:Gravity.LEFT;
        lp=new WindowManager.LayoutParams(dp(42),dp(230),WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,PixelFormat.TRANSLUCENT);
        lp.gravity=edge|Gravity.CENTER_VERTICAL; lp.y=0; wm.addView(bar,lp);
    }
    @Override public void onAccessibilityEvent(android.view.accessibility.AccessibilityEvent e){}
    @Override public void onInterrupt(){stopAuto();}
    private AccessibilityNodeInfo findScrollable(AccessibilityNodeInfo n){
        if(n==null)return null;
        if(n.isScrollable())return n;
        for(int i=0;i<n.getChildCount();i++){AccessibilityNodeInfo x=findScrollable(n.getChild(i));if(x!=null)return x;}
        return null;
    }
    private boolean scroll(boolean f){
        AccessibilityNodeInfo root=getRootInActiveWindow(); if(root==null)return false;
        AccessibilityNodeInfo n=findScrollable(root);
        boolean ok=n!=null&&n.performAction(f?AccessibilityNodeInfo.ACTION_SCROLL_FORWARD:AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD);
        root.recycle(); return ok;
    }
    private void startAuto(boolean f){stopAuto();auto=true;forward=f;int choice=prefs.getInt("speedChoice",0);tick=choice==0?90:choice==1?180:320;runTick();}
    private void runTick(){if(!auto)return;scroll(forward);handler.postDelayed(this::runTick,tick);}
    private void stopAuto(){auto=false;handler.removeCallbacksAndMessages(null);}
    private class Bar extends View{
        Paint p=new Paint(3); float downX,downY,lastY; boolean moved=false;
        Bar(Context c){super(c);}
        protected void onDraw(Canvas c){float w=getWidth(),h=getHeight();int opacity=prefs.getInt("opacity",170);int size=prefs.getInt("size",2);float barW=dp(size+3);p.setColor(prefs.getInt("theme",0)==0?0xFF16221B:0xFFE8EEE9);p.setAlpha(opacity);c.drawRoundRect((w-barW)/2,0,(w+barW)/2,h,barW,barW,p);p.setColor(0xFF55A477);p.setAlpha(255);float top=h*.37f,bottom=h*.63f;c.drawRoundRect(w*.27f,top,w*.73f,bottom,w*.23f,w*.23f,p);p.setColor(Color.WHITE);p.setTextSize(dp(13));c.drawText("⌃",w*.30f,h*.20f,p);c.drawText("⌄",w*.30f,h*.88f,p);if(auto){p.setTextSize(dp(9));c.drawText("●",w*.40f,h*.54f,p);}}
        public boolean onTouchEvent(android.view.MotionEvent e){float y=e.getRawY();switch(e.getAction()){
            case MotionEvent.ACTION_DOWN:downX=e.getRawX();downY=y;lastY=y;moved=false;return true;
            case MotionEvent.ACTION_MOVE:
                if(Math.abs(y-downY)>dp(7)||moved){moved=true;lp.y+=(int)(y-lastY);int max=getResources().getDisplayMetrics().heightPixels/2;lp.y=Math.max(-max,Math.min(max,lp.y));wm.updateViewLayout(this,lp);lastY=y;}return true;
            case MotionEvent.ACTION_UP:
                if(!moved){float local=e.getY();if(local<getHeight()*.30f){stopAuto();scroll(false);}else if(local>getHeight()*.70f){stopAuto();scroll(true);}else{if(auto)stopAuto();else startAuto(true);}invalidate();}
                return true;
        }return true;}
    }
}