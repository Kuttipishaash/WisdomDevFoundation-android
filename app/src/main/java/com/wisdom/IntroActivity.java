package com.wisdom;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_teal_500)
                .backgroundDark(R.color.md_teal_800)
                .fragment(R.layout.fragment_intro_welcome, R.style.Theme_Intro)
                .canGoBackward(false)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_green_500)
                .backgroundDark(R.color.md_green_600)
                .fragment(R.layout.fragment_intro_aim, R.style.Theme_Intro)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_orange_500)
                .backgroundDark(R.color.md_orange_800)
                .fragment(R.layout.fragment_intro_action, R.style.Theme_Intro)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_red_600)
                .backgroundDark(R.color.md_red_900)
                .fragment(R.layout.fragment_intro_initiativese, R.style.Theme_Intro)
                .canGoForward(true)
                .build());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
