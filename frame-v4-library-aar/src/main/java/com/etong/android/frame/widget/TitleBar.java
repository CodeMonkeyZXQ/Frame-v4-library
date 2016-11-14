package com.etong.android.frame.widget;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.etong.android.frame.R;
import com.etong.android.frame.subscriber.BaseSubscriberActivity;

    public class TitleBar {
        View mTitleBar;
        View mTitleName;
        View mBackButton;
        View mNextButton;
        View mNextButtonImage;
        View mDivider;
        EditText mSearch;
        Activity mContainer;

        public TitleBar(View container) {
            if (null == container) {
                return;
            }

            View titleBar = container.findViewById(R.id.titlebar_default);
            if (titleBar == null) {
                titleBar = container.findViewById(R.id.titlebar_search);
            }
            initView(titleBar);

        }

        public TitleBar(Activity container) {
            mContainer = container;
            if (null == container) {
                return;
            }

            View titleBar = container.findViewById(R.id.titlebar_default);
            if (titleBar == null) {
                titleBar = container.findViewById(R.id.titlebar_search);
            }
            initView(titleBar);
        }

        public TitleBar(Activity container, int layout) {
            mContainer = container;
            if (null == container) {
                return;
            }

            View titleBar = container.findViewById(layout);
            initView(titleBar);
        }

        void initView(View titleBar) {
            if (null == titleBar) {
                return;
            }
            mTitleBar = titleBar;

            mTitleName = titleBar.findViewById(R.id.titlebar_title_name);
            mBackButton = titleBar.findViewById(R.id.titlebar_back_button);
            mNextButton = titleBar.findViewById(R.id.titlebar_next_text);
            mNextButtonImage = titleBar.findViewById(R.id.titlebar_next_button);
            mDivider = titleBar.findViewById(R.id.titlebar_divider_line);
            mSearch = (EditText)titleBar.findViewById(R.id.title_search_input);

            showBackButton(true);
            showNextButton(false);
        }

        public void setTitle(String title) {
            if (null == mTitleName)
                return;
            ((TextView) mTitleName).setText(title);
        }

        public void showBackButton(boolean enable) {

            if (null == mBackButton)
                return;

            if (enable) {
                mBackButton.setVisibility(View.VISIBLE);
                mBackButton.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        if (null != mContainer)
                            mContainer.finish();
                    }

                });
            } else {
                mBackButton.setVisibility(View.GONE);
            }
        }

        public void showNextButton(boolean enable) {

            if (null == mNextButton)
                return;

            if (enable) {
                mNextButton.setVisibility(View.VISIBLE);
            } else {
                mNextButton.setVisibility(View.GONE);
            }
        }

        public void showBottomLin(boolean enable) {
            if (null == mDivider)
                return;

            if (enable) {
                mDivider.setVisibility(View.VISIBLE);
            } else {
                mDivider.setVisibility(View.GONE);
            }
        }

        public void setOnClickListener(OnClickListener listener) {
            if (null != mNextButton)
                mNextButton.setOnClickListener(listener);

            if (null != mBackButton) {
                mBackButton.setOnClickListener(listener);
            }
        }

        public View getBackButton() {
            return mBackButton;
        }

        public View getNextButton() {
            return mNextButton;
        }
        public View getNextButtonImage() {
            return mNextButtonImage;
        }

        public EditText getSearchView() {
            return mSearch;
        }

        public View getDivider() {
            return mDivider;
        }

        public void setNextButton(String name) {
            showNextButton(true);
            if (null == mNextButton)
                return;
            ((TextView) mNextButton).setText(name);
            mNextButtonImage.setVisibility(View.GONE);
        }

        public void setNextButton(int resId) {
            showNextButton(false);
            if (null == mNextButtonImage)
                return;
            ((ImageButton) mNextButtonImage).setBackgroundResource(resId);
            mNextButtonImage.setVisibility(View.VISIBLE);
        }

        public void setBackButton(int resId) {
            showBackButton(true);
            if (null == mBackButton)
                return;
            ((ImageButton) mBackButton).setBackgroundResource(resId);
        }

        public int getHeight() {
            if (null != mTitleBar)
                return mTitleBar.getHeight();

            return 0;
        }
    }
