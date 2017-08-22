package com.tororobot.ui.adapte;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tororobot.R;
import com.tororobot.domain.model.Command;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by roger on 31/10/16.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String TAG = ChatAdapter.class.getSimpleName();

    private int SELF = 100;
    private static String today;

    private Context mContext;
    private ArrayList<Command> commandsList;

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView message, timestamp;

        ViewHolder(View view) {
            super(view);
            message = (TextView) itemView.findViewById(R.id.message);
            timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        }
    }

    public ChatAdapter(Context mContext, ArrayList<Command> commandsList) {
        this.mContext = mContext;
        this.commandsList = commandsList;
        Calendar calendar = Calendar.getInstance();
        today = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_self, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Command command = commandsList.get(position);
        ((ViewHolder) holder).message.setText(String.format("Comando: %s", command.getMessage()));
        ((ViewHolder) holder).timestamp.setText(command.getDates());
    }

    @Override
    public int getItemCount() {
        return commandsList.size();
    }

}