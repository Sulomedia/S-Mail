package com.smail.de.designerscripts;
import anywheresoftware.b4a.objects.TextViewWrapper;
import anywheresoftware.b4a.objects.ImageViewWrapper;
import anywheresoftware.b4a.BA;


public class LS_2{

public static void LS_320x480_1(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
//BA.debugLineNum = 2;BA.debugLine="Web.SetTopAndBottom(0,100%y)"[2/320x480,scale=1]
views.get("web").vw.setTop((int)(0d));
views.get("web").vw.setHeight((int)((100d / 100 * height) - (0d)));
//BA.debugLineNum = 3;BA.debugLine="Web.SetLeftAndRight(0,100%x)"[2/320x480,scale=1]
views.get("web").vw.setLeft((int)(0d));
views.get("web").vw.setWidth((int)((100d / 100 * width) - (0d)));

}
public static void LS_general(java.util.LinkedHashMap<String, anywheresoftware.b4a.keywords.LayoutBuilder.ViewWrapperAndAnchor> views, int width, int height, float scale) {
anywheresoftware.b4a.keywords.LayoutBuilder.setScaleRate(0.3);
anywheresoftware.b4a.keywords.LayoutBuilder.scaleAll(views);

}
}