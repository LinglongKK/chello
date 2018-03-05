package cck.com.chello;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

/**
 * Created by chenlong on 18-1-10.
 */

public class PullRefreshActivity extends Activity{
    ListView mListView;
    PullRefreshView mPullRefreshView;
    Handler mHandle = new Handler();
    String[] mDataList = new String[] {
            "1","2","3","4","5",
            "1","2","3","4","5",
            "1","2","3","4","5",
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refresh_activity_layout);
        mPullRefreshView = findViewById(R.id.pull_refresh);
        mListView = mPullRefreshView.getListView();
        mListView.setAdapter(new StrAdapter());
        mPullRefreshView.setPullRefreshListener(new PullRefreshView.IPullRefreshListener() {
            @Override
            public void onRefresh() {
                mHandle.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPullRefreshView.refreshFinish();
                    }
                },TimeUnit.SECONDS.toMillis(2));
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(),"click list",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class StrAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mDataList.length;
        }

        @Override
        public Object getItem(int position) {
            return mDataList[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = new TextView(parent.getContext());
                convertView.setPadding(60,60,60,60);
            }
            TextView textView = (TextView)convertView;
            textView.setText(mDataList[position]);
            return convertView;
        }
    }
}
