package cck.com.chello;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class LauncherActivity extends Activity {
    ListView mPageList;
    Object[] mPages = new Object[] {
            new Object[] {"provider",MainActivity.class},
            new Object[] {"native进程",ProcessActivity.class},
            new Object[] {"属性动画",AnimationActivity.class},
            new Object[] {"物理动画",PhysicsAnimationActivity.class},
            new Object[] {"下拉刷新",PullRefreshActivity.class},
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        mPageList = findViewById(R.id.page_list);
        mPageList.setAdapter(new SimpleAdapter());
        mPageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(LauncherActivity.this,(Class<?>) ((Object[])mPages[position])[1]);
                startActivity(intent);
            }
        });
    }

    private class SimpleAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mPages.length;
        }

        @Override
        public Object getItem(int position) {
            return mPages[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = new TextView(parent.getContext());
                convertView.setPadding(50,50,50,50);
            }
            TextView textView = (TextView)convertView;
            textView.setText((String)((Object[])mPages[position])[0]);
            return convertView;
        }
    }

}
