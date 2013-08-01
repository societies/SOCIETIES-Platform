package org.societies.android.platform.useragent.feedback.guis;

import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.platform.useragent.feedback.R;

public class RadioPopup extends UserFeedbackPopup {

    public RadioPopup() {
        super(R.layout.activity_radio_popup,
                R.id.radioAckProposalText,
                R.id.radioAckOkButton,
                R.menu.activity_radio_popup,
                IAndroidSocietiesEvents.UF_EXPLICIT_RESPONSE_INTENT);
    }

    @Override
    protected void populateOptions() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioAckRadioGroup);

        // clear design time sample components
        radioGroup.removeAllViews();

        RadioButton radio = null;
        for (String option : getUserFeedbackBean().getOptions()) {
            radio = new RadioButton(this);
            radio.setText(option);
            radio.setTag(option);
            radio.setTextColor(R.color.Black);
            radioGroup.addView(radio);

            // remember the option as it's clicked
            radio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getResultPayload().clear();
                    getResultPayload().add((String) view.getTag());
                }
            });
        }
        // check the last item implicitly
        if (radio != null)
            radio.setChecked(true);
    }

}
