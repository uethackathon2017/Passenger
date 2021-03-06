package com.fit.uet.passengerapp.Activity.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fit.uet.passengerapp.Activity.activities.TicketBoxActivity;
import com.fit.uet.passengerapp.R;
import com.fit.uet.passengerapp.database.DB;
import com.fit.uet.passengerapp.models.CoachSchedule;
import com.fit.uet.passengerapp.models.Ticket;
import com.fit.uet.passengerapp.models.User;
import com.fit.uet.passengerapp.utils.BarcodeUtils;
import com.fit.uet.passengerapp.utils.DateTimeUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class TicketInfoFragment extends Fragment implements View.OnClickListener{
    private static final String KEY_CODE = "CODE";

    private TicketBoxActivity activity;

    @BindView(R.id.tv_arrive_from)
    TextView tv_arrive_from;

    @BindView(R.id.tv_arrive_to)
    TextView tv_arrive_to;

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.tv_price)
    TextView tv_price;

    @BindView(R.id.tv_date)
    TextView tv_date;

    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.tv_phone)
    TextView tv_phone;

    @BindView(R.id.tv_pick_from)
    TextView tv_pick_from;

    @BindView(R.id.tv_pick_to)
    TextView tv_pick_to;

    @BindView(R.id.tv_seats)
    TextView tv_seats;

    @BindView(R.id.layout_info)
    View layout_info;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.tv_accept)
    TextView tv_accept;

    private DatabaseReference databaseReference;
    private DatabaseReference ticketDatabaseReference;

    public static TicketInfoFragment newInstance(String code) {
        TicketInfoFragment fragment = new TicketInfoFragment();
        Bundle arg = new Bundle();
        arg.putString(KEY_CODE, code);
        fragment.setArguments(arg);
        return fragment;
    }

    public TicketInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ticket_info, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (TicketBoxActivity) getActivity();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        tv_name.setText(currentUser.getDisplayName());

        databaseReference.child(DB.USER).child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tv_phone.setText(user.getPhoneNum());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ticketDatabaseReference = databaseReference.child(Ticket.CHILD_TICKET);

        ticketDatabaseReference.child(getArguments().getString(KEY_CODE)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Ticket ticket = dataSnapshot.getValue(Ticket.class);
                tv_seats.setText(ticket.seats.toString());

                DatabaseReference schedule = databaseReference.child(DB.SCHEDULE).child(ticket.coach_schedule_id);
                schedule.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CoachSchedule schedule = dataSnapshot.getValue(CoachSchedule.class);
                        tv_price.setText(schedule.costPerTicket * ticket.seats.size() + "$");
                        tv_pick_from.setText(schedule.pickFrom);
                        tv_pick_to.setText(schedule.pickTo);
                        tv_arrive_from.setText(schedule.arriveFrom);
                        tv_arrive_to.setText(schedule.arriveTo);

                        long ms = DateTimeUtils.getMillisFromString(schedule.departureTime);
                        tv_date.setText(DateTimeUtils.dateStringFormat(ms));
                        tv_time.setText(DateTimeUtils.getTimeFromMs(ms));

                        show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        tv_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ticketDatabaseReference.child(getArguments().getString(KEY_CODE)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Ticket ticket = dataSnapshot.getValue(Ticket.class);
                        ticket.checkout = true;
                        ticketDatabaseReference.child(getArguments().getString(KEY_CODE)).setValue(ticket);
                        activity.update();
                        activity.getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void show() {
        progressBar.setVisibility(View.INVISIBLE);
        layout_info.setVisibility(View.VISIBLE);
    }

    private void hide() {
        progressBar.setVisibility(View.VISIBLE);
        layout_info.setVisibility(View.INVISIBLE);
    }
}
