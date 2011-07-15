package com.rickreation.webcomicviewer;

import android.os.Bundle;
import com.phonegap.*;

public class HomeActivity extends DroidGap {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/index.html");
    }
}
