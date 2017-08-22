package com.tororobot.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rogerp91.prefsshared.ManagerPrefs;
import com.tororobot.R;
import com.tororobot.domain.model.Command;
import com.tororobot.ui.adapte.ChatAdapter;
import com.tororobot.ui.fragment.BaseFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatFragment extends BaseFragment {

    private String TAG = ChatFragment.class.getSimpleName();

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.message)
    EditText inputMessage;

    @BindView(R.id.btn_send)
    Button btnSend;

    private ChatAdapter chatAdapter;
    private ArrayList<Command> commandsArrayList;

    private int co = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected List<Object> getModules() {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        commandsArrayList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getActivity(), commandsArrayList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatAdapter);

        btnSend.setOnClickListener(v -> sendMessage());

    }

    private void sendMessage() {

        int cos = ManagerPrefs.getInt("CO", 0);
        if (cos == 0) {
            cos = cos + 1;
        } else {
            cos = cos + 1;
        }
        ManagerPrefs.putInt("CO", cos);

        final String message_ = this.inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message_)) {
            Toast.makeText(getActivity(), "Enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        this.inputMessage.setText("");
        Command command = new Command();
        command.setId(Integer.toString(cos));
        command.setMessage(message_);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String formattedDate = dateFormat.format(new Date());
        command.setDates(formattedDate);

        commandsArrayList.add(command);
        chatAdapter.notifyDataSetChanged();
        if (chatAdapter.getItemCount() > 1) {
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, chatAdapter.getItemCount() - 1);
        }

    }

}