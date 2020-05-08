package chatapp.beast.firebasechat;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class TabPagerAdapter extends FragmentPagerAdapter {
    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i)
        {
            case 0:
                ChatFragment chatFragment=new ChatFragment();
                return  chatFragment;
            case 1:
                ContactFragment contactFragment=new ContactFragment();
                return contactFragment;
                default:return  null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
       switch (position)
       {
           case 0:
               return "Chats";
           case 1: return "Contacts";
           default: return null;
       }
    }
}
