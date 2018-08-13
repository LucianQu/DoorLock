package com.blg.rtu.frmFunction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.blg.rtu3.R;

import java.util.List;

public class PopuwindowAdapter extends BaseAdapter {
	private List<String> datas=null;
public PopuwindowAdapter(Context context, List<String> datas){
	this.datas=datas;
}
	public void setData(List<String> datas) {
		this.datas = datas;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		if (datas == null || datas.size() == 0) {
			return 0;
		} else {
			return datas.size();
		}
	}

	@Override
	public Object getItem(int position) {
		if (datas != null) {
			return datas.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Context context = parent.getContext();
		ViewHolder holder;

		final String info = datas.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.list_popuwindow_item, null);
			holder.option = (TextView) convertView.findViewById(R.id.tvDesc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.option.setText(info);
		return convertView;
	}

	static class ViewHolder {
		TextView option;
	}
}
