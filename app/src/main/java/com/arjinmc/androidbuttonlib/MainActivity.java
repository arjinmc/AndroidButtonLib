package com.arjinmc.androidbuttonlib;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arjinmc.androidbuttonlib.buttons.DownloadButtonActivity;
import com.arjinmc.androidbuttonlib.buttons.SlideButtonActivity;
import com.arjinmc.androidbuttonlib.buttons.SubmitButtonActivity;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyAdapter myAdapter;
    private String[] titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        titles = getResources().getStringArray(R.array.titles);

        recyclerView = (RecyclerView) findViewById(R.id.rv);
        myAdapter = new MyAdapter();
        recyclerView.setAdapter(myAdapter);
        recyclerView.addItemDecoration(new RecyclerViewItemDecoration(
                RecyclerViewItemDecoration.MODE_HORIZONTAL, Color.BLACK, 2, 0, 0));
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }


    private class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new MyViewHolder(
                    LayoutInflater.from(MainActivity.this)
                            .inflate(R.layout.item_list, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            holder.tv.setText(titles[position]);
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }

    }


    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tv;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.item_tv);
            tv.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {

            switch (getLayoutPosition()) {
                case 0:
                    jumpActivity(AboutActivity.class);
                    break;
                case 1:
                    jumpActivity(SlideButtonActivity.class);
                    break;
                case 2:
                    jumpActivity(DownloadButtonActivity.class);
                    break;
                case 3:
                    jumpActivity(SubmitButtonActivity.class);
                    break;
                default:
                    break;
            }

        }
    }

    private void jumpActivity(Class clazz) {
        startActivity(new Intent(this, clazz));
    }
}
