package com.smail.de.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_left{

public static void LS_320x480_1(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
views.get("lv").vw.setLeft((int)(0d));
views.get("lv").vw.setWidth((int)((100d / 100 * width) - (0d)));
//BA.debugLineNum = 3;BA.debugLine="lv.SetTopAndBottom(0,100%y)"[left/320x480,scale=1]
views.get("lv").vw.setTop((int)(0d));
views.get("lv").vw.setHeight((int)((100d / 100 * height) - (0d)));

}
public static void LS_general(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
anywheresoftware.b4a.keywords.LayoutBuilder.scaleAll(views);

}
}