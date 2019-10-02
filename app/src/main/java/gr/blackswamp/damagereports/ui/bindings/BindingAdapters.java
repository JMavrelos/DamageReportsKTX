package gr.blackswamp.damagereports.ui.bindings;

import android.view.View;

import androidx.databinding.BindingAdapter;


public class BindingAdapters {
    @BindingAdapter("shown")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}