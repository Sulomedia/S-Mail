package com.smail.de;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends android.support.v7.app.AppCompatActivity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.smail.de", "com.smail.de.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.smail.de", "com.smail.de.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.smail.de.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public anywheresoftware.b4a.phone.PackageManagerWrapper _pak = null;
public static String _pack = "";
public static String _dir = "";
public anywheresoftware.b4a.objects.WebViewWrapper _web = null;
public static String _url1 = "";
public anywheresoftware.b4a.objects.SlidingMenuWrapper _dpan = null;
public anywheresoftware.b4a.objects.ListViewWrapper _lv = null;
public anywheresoftware.b4a.specci48.spdialogs.SPDialogs.CustomDialog3 _devi = null;
public com.smail.de.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _lftmenu = null;
int _offset = 0;
anywheresoftware.b4a.objects.LabelWrapper _l1 = null;
 //BA.debugLineNum = 33;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 35;BA.debugLine="Activity.LoadLayout(\"2\")";
mostCurrent._activity.LoadLayout("2",mostCurrent.activityBA);
 //BA.debugLineNum = 36;BA.debugLine="Activity.Title=\"SMail Client\"";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence("SMail Client"));
 //BA.debugLineNum = 37;BA.debugLine="dpan.Initialize(\"dpan\")";
mostCurrent._dpan.Initialize(mostCurrent.activityBA,"dpan");
 //BA.debugLineNum = 38;BA.debugLine="Dim lftMenu As Panel";
_lftmenu = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 39;BA.debugLine="lftMenu.Initialize(\"\")";
_lftmenu.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 40;BA.debugLine="lftMenu.LoadLayout(\"left\")";
_lftmenu.LoadLayout("left",mostCurrent.activityBA);
 //BA.debugLineNum = 41;BA.debugLine="Dim offset As Int = 70%x";
_offset = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (70),mostCurrent.activityBA);
 //BA.debugLineNum = 42;BA.debugLine="dpan.BehindOffset=offset";
mostCurrent._dpan.setBehindOffset(_offset);
 //BA.debugLineNum = 43;BA.debugLine="dpan.Menu.AddView(lftMenu, 0,0,100%x-offset, 100%";
mostCurrent._dpan.getMenu().AddView((android.view.View)(_lftmenu.getObject()),(int) (0),(int) (0),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA)-_offset),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 44;BA.debugLine="dpan.Mode=dpan.RIGHT";
mostCurrent._dpan.setMode(mostCurrent._dpan.RIGHT);
 //BA.debugLineNum = 45;BA.debugLine="Web.JavaScriptEnabled=True";
mostCurrent._web.setJavaScriptEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 46;BA.debugLine="Web.ZoomEnabled=True";
mostCurrent._web.setZoomEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 47;BA.debugLine="Web.LoadUrl(url1)";
mostCurrent._web.LoadUrl(mostCurrent._url1);
 //BA.debugLineNum = 48;BA.debugLine="Dim l1 As Label";
_l1 = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 49;BA.debugLine="l1=lv.SingleLineLayout.Label";
_l1 = mostCurrent._lv.getSingleLineLayout().Label;
 //BA.debugLineNum = 50;BA.debugLine="l1.TextSize=15";
_l1.setTextSize((float) (15));
 //BA.debugLineNum = 51;BA.debugLine="l1.TextColor=Colors.ARGB(200,255,255,255)";
_l1.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.ARGB((int) (200),(int) (255),(int) (255),(int) (255)));
 //BA.debugLineNum = 53;BA.debugLine="lv.AddSingleLine2(\"logout\",1)";
mostCurrent._lv.AddSingleLine2(BA.ObjectToCharSequence("logout"),(Object)(1));
 //BA.debugLineNum = 54;BA.debugLine="lv.AddSingleLine2(\"zurück\",2)";
mostCurrent._lv.AddSingleLine2(BA.ObjectToCharSequence("zurück"),(Object)(2));
 //BA.debugLineNum = 55;BA.debugLine="lv.AddSingleLine2(\"Neu laden\",3)";
mostCurrent._lv.AddSingleLine2(BA.ObjectToCharSequence("Neu laden"),(Object)(3));
 //BA.debugLineNum = 56;BA.debugLine="lv.AddSingleLine2(\"Info\",4)";
mostCurrent._lv.AddSingleLine2(BA.ObjectToCharSequence("Info"),(Object)(4));
 //BA.debugLineNum = 58;BA.debugLine="Activity.Title=pak.GetApplicationLabel(pack)";
mostCurrent._activity.setTitle(BA.ObjectToCharSequence(mostCurrent._pak.GetApplicationLabel(mostCurrent._pack)));
 //BA.debugLineNum = 59;BA.debugLine="If Not(File.IsDirectory(dir,\"data\")) Then";
if (anywheresoftware.b4a.keywords.Common.Not(anywheresoftware.b4a.keywords.Common.File.IsDirectory(mostCurrent._dir,"data"))) { 
 //BA.debugLineNum = 60;BA.debugLine="File.MakeDir(File.DirRootExternal,\"smail/data\")";
anywheresoftware.b4a.keywords.Common.File.MakeDir(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"smail/data");
 };
 //BA.debugLineNum = 62;BA.debugLine="ProgressDialogShow(\"loading Smail please wait\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("loading Smail please wait"));
 //BA.debugLineNum = 63;BA.debugLine="Web.LoadUrl(url1)";
mostCurrent._web.LoadUrl(mostCurrent._url1);
 //BA.debugLineNum = 65;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
int _res = 0;
 //BA.debugLineNum = 76;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 77;BA.debugLine="Dim res As Int";
_res = 0;
 //BA.debugLineNum = 78;BA.debugLine="If KeyCode=KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 79;BA.debugLine="Web.StopLoading";
mostCurrent._web.StopLoading();
 //BA.debugLineNum = 80;BA.debugLine="res=Msgbox2(\"Close Smail?\",\"Action:\",\"yes close.";
_res = anywheresoftware.b4a.keywords.Common.Msgbox2(BA.ObjectToCharSequence("Close Smail?"),BA.ObjectToCharSequence("Action:"),"yes close..","","cancel",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Comment-delete48.png").getObject()),mostCurrent.activityBA);
 //BA.debugLineNum = 81;BA.debugLine="If res=DialogResponse.POSITIVE Then";
if (_res==anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE) { 
 //BA.debugLineNum = 82;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 }else {
 //BA.debugLineNum = 84;BA.debugLine="ToastMessageShow(\"canceled..\",False)";
anywheresoftware.b4a.keywords.Common.ToastMessageShow(BA.ObjectToCharSequence("canceled.."),anywheresoftware.b4a.keywords.Common.False);
 };
 };
 //BA.debugLineNum = 87;BA.debugLine="If KeyCode=KeyCodes.KEYCODE_MENU Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_MENU) { 
 //BA.debugLineNum = 88;BA.debugLine="If Not(dpan.Visible=True) Then";
if (anywheresoftware.b4a.keywords.Common.Not(mostCurrent._dpan.getVisible()==anywheresoftware.b4a.keywords.Common.True)) { 
 //BA.debugLineNum = 89;BA.debugLine="dpan.ShowMenu";
mostCurrent._dpan.ShowMenu();
 }else {
 //BA.debugLineNum = 91;BA.debugLine="dpan.HideMenus";
mostCurrent._dpan.HideMenus();
 };
 };
 //BA.debugLineNum = 94;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 95;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 72;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 74;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 67;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 68;BA.debugLine="ProgressDialogShow(\"loading Smail please wait\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("loading Smail please wait"));
 //BA.debugLineNum = 69;BA.debugLine="Web.LoadUrl(url1)";
mostCurrent._web.LoadUrl(mostCurrent._url1);
 //BA.debugLineNum = 70;BA.debugLine="End Sub";
return "";
}
public static String  _activity_touch(int _action,float _x,float _y) throws Exception{
 //BA.debugLineNum = 97;BA.debugLine="Sub Activity_Touch (Action As Int, X As Float, Y A";
 //BA.debugLineNum = 99;BA.debugLine="End Sub";
return "";
}
public static String  _back_click() throws Exception{
 //BA.debugLineNum = 132;BA.debugLine="Sub back_Click";
 //BA.debugLineNum = 133;BA.debugLine="Web.StopLoading";
mostCurrent._web.StopLoading();
 //BA.debugLineNum = 134;BA.debugLine="Web.Back";
mostCurrent._web.Back();
 //BA.debugLineNum = 135;BA.debugLine="End Sub";
return "";
}
public static String  _dev_click() throws Exception{
anywheresoftware.b4a.objects.PanelWrapper _ppan = null;
anywheresoftware.b4a.objects.LabelWrapper _cpan = null;
 //BA.debugLineNum = 106;BA.debugLine="Sub dev_click";
 //BA.debugLineNum = 107;BA.debugLine="Dim ppan As Panel";
_ppan = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 108;BA.debugLine="ppan.Initialize(\"\")";
_ppan.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 109;BA.debugLine="Dim cpan As Label";
_cpan = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 110;BA.debugLine="cpan.Initialize(\"\")";
_cpan.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 111;BA.debugLine="ppan.AddView(cpan,3%x,5%x,97%x,100%y)";
_ppan.AddView((android.view.View)(_cpan.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (3),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (97),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 112;BA.debugLine="devi.AddView(ppan,95%x,50%y)";
mostCurrent._devi.AddView((android.view.View)(_ppan.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (95),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (50),mostCurrent.activityBA));
 //BA.debugLineNum = 113;BA.debugLine="cpan.Text=\"Developers:\"&CRLF&\"SuloMedia,D.Trojan\"";
_cpan.setText(BA.ObjectToCharSequence("Developers:"+anywheresoftware.b4a.keywords.Common.CRLF+"SuloMedia,D.Trojan"+anywheresoftware.b4a.keywords.Common.CRLF+"license info:"+anywheresoftware.b4a.keywords.Common.CRLF+"Written in Basic & Java™"+anywheresoftware.b4a.keywords.Common.CRLF+"Version "+mostCurrent._pak.GetVersionName(mostCurrent._pack)+anywheresoftware.b4a.keywords.Common.CRLF+"Sliding Menu Wrtaper Info: Copyright 2012-2014 Jeremy Feinstein"+anywheresoftware.b4a.keywords.Common.CRLF+"Licensed under the Apache License, Version 2.0 (the License) you may Not use this File except in compliance with the License."+anywheresoftware.b4a.keywords.Common.CRLF+"You may obtain a copy of the License athttp://www.apache.org/licenses/LICENSE-2.0. More Infos at www.sulomedia.de"));
 //BA.debugLineNum = 114;BA.debugLine="devi.Show(pak.GetApplicationLabel(pack),\"\",\"Ok\",\"";
mostCurrent._devi.Show(BA.ObjectToCharSequence(mostCurrent._pak.GetApplicationLabel(mostCurrent._pack)),BA.ObjectToCharSequence(""),BA.ObjectToCharSequence("Ok"),BA.ObjectToCharSequence(""),mostCurrent.activityBA,(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Comment48.png").getObject()));
 //BA.debugLineNum = 115;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 22;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 23;BA.debugLine="Private pak As PackageManager";
mostCurrent._pak = new anywheresoftware.b4a.phone.PackageManagerWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim pack As String =\"com.smail.de\"";
mostCurrent._pack = "com.smail.de";
 //BA.debugLineNum = 25;BA.debugLine="Private dir As String= File.DirRootExternal&\"/sma";
mostCurrent._dir = anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/smail/";
 //BA.debugLineNum = 26;BA.debugLine="Private Web As WebView";
mostCurrent._web = new anywheresoftware.b4a.objects.WebViewWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Dim url1 As String=\"http://webmail.sulomusic.de\"";
mostCurrent._url1 = "http://webmail.sulomusic.de";
 //BA.debugLineNum = 28;BA.debugLine="Private dpan As SlidingMenu";
mostCurrent._dpan = new anywheresoftware.b4a.objects.SlidingMenuWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private lv As ListView";
mostCurrent._lv = new anywheresoftware.b4a.objects.ListViewWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private devi As CustomDialog3";
mostCurrent._devi = new anywheresoftware.b4a.specci48.spdialogs.SPDialogs.CustomDialog3();
 //BA.debugLineNum = 31;BA.debugLine="End Sub";
return "";
}
public static String  _lo_click() throws Exception{
String _ur = "";
 //BA.debugLineNum = 119;BA.debugLine="Sub lo_click";
 //BA.debugLineNum = 120;BA.debugLine="ProgressDialogShow(\"loging out..\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("loging out.."));
 //BA.debugLineNum = 121;BA.debugLine="Dim ur As String =\"http://webmail.sulomedia.de/?_";
_ur = "http://webmail.sulomedia.de/?_task=logout";
 //BA.debugLineNum = 122;BA.debugLine="Web.LoadUrl(ur)";
mostCurrent._web.LoadUrl(_ur);
 //BA.debugLineNum = 123;BA.debugLine="End Sub";
return "";
}
public static String  _lv_itemclick(int _position,Object _value) throws Exception{
 //BA.debugLineNum = 137;BA.debugLine="Sub lv_ItemClick (Position As Int, Value As Object";
 //BA.debugLineNum = 138;BA.debugLine="Select Value";
switch (BA.switchObjectToInt(_value,(Object)(1),(Object)(2),(Object)(3),(Object)(4))) {
case 0: {
 //BA.debugLineNum = 140;BA.debugLine="lo_click";
_lo_click();
 break; }
case 1: {
 //BA.debugLineNum = 142;BA.debugLine="back_Click";
_back_click();
 break; }
case 2: {
 //BA.debugLineNum = 144;BA.debugLine="reload_Click";
_reload_click();
 break; }
case 3: {
 //BA.debugLineNum = 146;BA.debugLine="dev_click";
_dev_click();
 break; }
}
;
 //BA.debugLineNum = 148;BA.debugLine="End Sub";
return "";
}
public static String  _mm_click() throws Exception{
 //BA.debugLineNum = 116;BA.debugLine="Sub mm_click";
 //BA.debugLineNum = 117;BA.debugLine="dpan.ShowMenu";
mostCurrent._dpan.ShowMenu();
 //BA.debugLineNum = 118;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 19;BA.debugLine="End Sub";
return "";
}
public static String  _reload_click() throws Exception{
String _uri = "";
 //BA.debugLineNum = 126;BA.debugLine="Sub reload_Click";
 //BA.debugLineNum = 127;BA.debugLine="Dim uri As String =\"http://webmail.sulomedia.de\"";
_uri = "http://webmail.sulomedia.de";
 //BA.debugLineNum = 128;BA.debugLine="Web.StopLoading";
mostCurrent._web.StopLoading();
 //BA.debugLineNum = 129;BA.debugLine="Web.LoadUrl(uri)";
mostCurrent._web.LoadUrl(_uri);
 //BA.debugLineNum = 130;BA.debugLine="End Sub";
return "";
}
public static String  _web_pagefinished(String _url) throws Exception{
 //BA.debugLineNum = 101;BA.debugLine="Sub Web_PageFinished (url As String)";
 //BA.debugLineNum = 102;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
}
