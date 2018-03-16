package com.myitschool.androiddatabasedemo;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.List;

/**
 * Created by teacher on 16.03.18.
 */

class MyPersonsAdapter extends BaseAdapter implements ListAdapter {
    private final List<Person> persons;

    public MyPersonsAdapter(List<Person> persons){
        this.persons = persons;
    }

    @Override
    public int getCount() {
        return persons.size();
    }

    @Override
    public Object getItem(int position) {
        return persons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return persons.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //todo

        return null;
    }
}
