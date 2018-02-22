package com.wisdom;

import android.os.Bundle;
import android.widget.Toast;

import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);
        super.onCreate(savedInstanceState);
        setTitle("intros");
        Toast.makeText(this, "prejith", Toast.LENGTH_SHORT).show();

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_teal_500)
                .backgroundDark(R.color.md_teal_800)
                .fragment(R.layout.fragment_intro_welcome, R.style.Theme_Intro)
                .canGoBackward(false)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_green_500)
                .backgroundDark(R.color.md_green_800)
                .fragment(R.layout.fragment_intro_syllabus, R.style.Theme_Intro)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_orange_500)
                .backgroundDark(R.color.md_orange_800)
                .fragment(R.layout.fragment_intro_announcement, R.style.Theme_Intro)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_light_green_500)
                .backgroundDark(R.color.md_light_green_800)
                .fragment(R.layout.fragment_intro_marks, R.style.Theme_Intro)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_pink_500)
                .backgroundDark(R.color.md_pink_800)
                .fragment(R.layout.fragment_intro_attendance, R.style.Theme_Intro)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.md_red_600)
                .backgroundDark(R.color.md_red_900)

                .canGoForward(false)
                .build());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
