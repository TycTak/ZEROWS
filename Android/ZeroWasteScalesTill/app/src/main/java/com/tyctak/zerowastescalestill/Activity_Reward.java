package com.tyctak.zerowastescalestill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Activity_Reward extends AppCompatActivity {

    Activity activity = this;
    _Reward reward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        XP_Library.FullScreen(activity);

        setContentView(R.layout.activity_reward);

        reward = getIntent().getParcelableExtra("reward");

        TextView txtMember = (TextView) findViewById(R.id.txtMember);
        TextView txtMemberBenefits = (TextView) findViewById(R.id.txtMemberBenefits);

        txtMember.setText(String.format(getString(R.string.AreYouReward), reward.RewardMember));
        txtMemberBenefits.setText(reward.RewardDescription);
        txtMemberBenefits.setVisibility((TextUtils.isEmpty(reward.RewardDescription) ? View.GONE : View.VISIBLE));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("reward", reward);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        reward = savedInstanceState.getParcelable("reward");
    }

    public void btnSubmit(View view) {
        Intent intent = new Intent(activity, Activity_Customer.class);
        intent.putExtra("reward", reward);
        startActivity(intent);
    }

    public void btnCancel(View view) {
        Intent intent = new Intent(activity, Activity_Customer.class);
        startActivity(intent);
    }
}
