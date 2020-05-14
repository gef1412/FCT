package com.example.proyecto.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.proyecto.Fragments.AlumnosFragment;
import com.example.proyecto.Fragments.AsignaturasFragment;
import com.example.proyecto.Fragments.GruposFragment;

public class PagerAdapter extends FragmentPagerAdapter {
    private int tabsNumber;

    public PagerAdapter(@NonNull FragmentManager fm, int behavior,int tabs) {
        super(fm, behavior);
        this.tabsNumber = tabs;
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new AlumnosFragment();
            case 1:
                return new GruposFragment();
            case 2 :
                return new AsignaturasFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return tabsNumber;
    }
}
