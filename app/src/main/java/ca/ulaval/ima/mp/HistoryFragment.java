package ca.ulaval.ima.mp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class HistoryFragment extends Fragment {

    NavigationRequest navigationRequest = new NavigationRequest();

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = (ListView) view.findViewById(R.id.listViewHistory);

        try {
            AdapterHistory adapterHistory = new AdapterHistory(this, R.layout.adapter_history_view_layout, navigationRequest.getHistory());
            listView.setAdapter(adapterHistory);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    class AdapterHistory extends BaseAdapter {
        private JSONArray mHistoryArray;

        AdapterHistory(HistoryFragment historyFragment, int adapter_offers_view_layout, JSONArray historyArray) {
            mHistoryArray = historyArray;
        }

        @Override
        public int getCount() {
            return mHistoryArray.length();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.adapter_history_view_layout, null);
            try {
                JSONObject objTmp = new JSONObject(mHistoryArray.get(position).toString());

                TextView textViewKilometersAdapterHistory = (TextView) convertView.findViewById(R.id.textViewKilometersAdapterHistory);
                TextView textViewAddressToAdapterHistory = (TextView) convertView.findViewById(R.id.textViewAddressToAdapterHistory);
                TextView textViewCreatedAdapterHistory = (TextView) convertView.findViewById(R.id.textViewCreatedAdapterHistory);

                textViewKilometersAdapterHistory.setText(Double.toString(objTmp.getDouble("distance") / 1000) + " km");
                textViewAddressToAdapterHistory.setText(objTmp.get("address_to").toString());

                String simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date((long) objTmp.getDouble("created_at")));

                //textViewCreatedAdapterHistory.setText(Double.toString(objTmp.getDouble("created_at")));
                textViewCreatedAdapterHistory.setText(simpleDateFormat);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }

}
