package tk.sidwardapps.pcpartpickerweb;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class build extends Fragment {
    View rootview;
    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootview = inflater.inflate(R.layout.build_layout, container, false);
        return this.rootview;
    }
}