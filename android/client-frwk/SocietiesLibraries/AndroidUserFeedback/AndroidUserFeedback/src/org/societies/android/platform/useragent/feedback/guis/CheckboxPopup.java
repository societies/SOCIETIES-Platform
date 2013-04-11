package org.societies.android.platform.useragent.feedback.guis;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import org.societies.android.api.events.IAndroidSocietiesEvents;
import org.societies.android.platform.useragent.feedback.R;

public class CheckboxPopup extends UserFeedbackPopup {

    public CheckboxPopup() {
        super(R.layout.activity_checkbox_popup,
                R.id.checkAckProposalText,
                R.id.checkAckOkButton,
                R.menu.activity_checkbox_popup,
                IAndroidSocietiesEvents.UF_EXPLICIT_RESPONSE_INTENT);

    }

    @Override
    protected void populateOptions() {
        LinearLayout checkboxGroup = (LinearLayout) findViewById(R.id.checkAckInnerLinearLayout);

        // clear design time sample components
        checkboxGroup.removeAllViews();

        for (String option : getUserFeedbackBean().getOptions()) {
            CheckBox checkbox = new CheckBox(this);
            checkbox.setText(option);
            checkbox.setTag(option);
            checkboxGroup.addView(checkbox);

            // remember the option as it's clicked
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox) view;

                    if (cb.isChecked())
                        getResultPayload().add((String) view.getTag());
                    else
                        getResultPayload().remove((String) view.getTag());
                }
            });
        }
    }

}
