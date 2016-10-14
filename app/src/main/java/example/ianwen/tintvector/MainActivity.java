package example.ianwen.tintvector;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final int NUM_ITEM = 30;

    private static class RowItem {
        private Drawable mDrawable;
        private String mText;
        private String mKeyword;

        public RowItem(Drawable drawable, String text, String keyword) {
            mDrawable = drawable;
            mText = text;
            mKeyword = keyword;
        }
    }

    // Item holder for performance boost. See https://developer.android.com/training/improving-layouts/smooth-scrolling.html
    private static class ViewHolder {
        private ImageView mImageView;
        private TextView mTextView;

        public ViewHolder(View view) {
            mImageView = (ImageView) view.findViewById(R.id.icon);
            mTextView = (TextView) view.findViewById(R.id.description);
        }
    }

    private static class MyAdapter extends BaseAdapter {

        private List<RowItem> mRows = new ArrayList<>();

        @Override
        public int getCount() {
            return mRows.size();
        }

        @Override
        public RowItem getItem(int position) {
            return mRows.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder row;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row, parent, false);
                row = new ViewHolder(convertView);
                convertView.setTag(row);
            } else {
                row = (ViewHolder) convertView.getTag();
            }

            RowItem item = getItem(position);
            // First set it to null to prevent imageView setting level to previous drawable.
            row.mImageView.setImageDrawable(null);
            row.mImageView.setImageLevel(item.mDrawable.getLevel());
            row.mImageView.setImageDrawable(item.mDrawable);
            row.mTextView.setText(item.mText);

            return convertView;
        }

        public RowItem getByKeyword(String keyword) {
            for (RowItem item : mRows) {
                if (item.mKeyword.equals(keyword)) return item;
            }
            return null;
        }

        public void addOrUpdate(String keyword, String text, Drawable drawable) {
            RowItem item = getByKeyword(keyword);
            if (item == null) {
                mRows.add(new RowItem(drawable, text, keyword));
            } else {
                item.mText = text;
                item.mDrawable = drawable;
            }
            notifyDataSetChanged();
        }
    }

    private MyAdapter mAdapter;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAdapter = new MyAdapter();

        mListView = (ListView) findViewById(R.id.list);
        mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(this);
        mListView.setAdapter(mAdapter);
        mListView.setDivider(null);

        VectorDrawableCompat vdc1 = VectorDrawableCompat.create(getResources(), R.drawable.bar_1, getTheme());
        VectorDrawableCompat vdc3 = VectorDrawableCompat.create(getResources(), R.drawable.bar_3, getTheme());
        ColorStateList colorList = ResourcesCompat.getColorStateList(getResources(), R.color.item_text_color, getTheme());
        DrawableCompat.setTintList(vdc1, colorList);
        DrawableCompat.setTintList(vdc3, colorList);

        for (int i = 0; i < NUM_ITEM; i++) {
            Drawable v1 = vdc1.getConstantState().newDrawable();
            Drawable v3 = vdc3.getConstantState().newDrawable();

            LevelListDrawable lld = new LevelListDrawable();
            lld.addLevel(0, 0, v1);
            lld.addLevel(1, 1, v3);

            mAdapter.addOrUpdate("" + i, "Descripttion" + i, lld);
        }

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();
                String keyword = "" + time % NUM_ITEM;
                RowItem item = mAdapter.getByKeyword(keyword);
                Drawable drawable = item.mDrawable;
                drawable.setLevel((int) (time % 2));
                mAdapter.addOrUpdate(keyword, "" + time, item.mDrawable);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // setSelection() will make the corresponding view to be activated.
        mListView.setSelection(position);
        mAdapter.notifyDataSetChanged();
    }
}
