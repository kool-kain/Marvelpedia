package layout;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Dani on 22/12/16.
 */

public class MyPageAdapter extends FragmentStatePagerAdapter{
    public MyPageAdapter(FragmentManager fm){super(fm);}

    @Override
    public Fragment getItem(int position){
        Fragment frag=null;
        switch (position){
            case 0:
                frag=new FgCharacters();
                break;

            case 1:
                frag=new FgComics();
                break;

            default: break;
        }

        return frag;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position){
        String title=" ";

        switch(position){
            case 0:
                title="BUSCAR PERSONAJES";
                break;
            case 1:
                title="BUSCAR COMICS";
                break;
            default: break;
        }

        return title;
    }
}
