package com.example.clothesday.data;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.example.clothesday.R;

public class Indicator {
    // 인디케이터 생성
    public void setupIndicators(int count, LinearLayout layoutIndicator, Context context) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        if (layoutIndicator.getChildCount() != 0) // 이미 리니어레이아웃에 뷰가 존재하는 경우 초기화
             layoutIndicator.removeAllViews();

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(context);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(context,
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0,  layoutIndicator, context);
    }

    public LinearLayout deleteIndicator(LinearLayout layoutIndicator) { // 인디케이터지우기
        if (layoutIndicator.getChildCount() != 0) 
            layoutIndicator.removeAllViews();
        return layoutIndicator;
    }

    public LinearLayout deleteOneIndicator(LinearLayout layoutIndicator, int pos, Context context) { // 인디케이터지우기
        if (layoutIndicator.getChildCount() != 0) {
            layoutIndicator.removeViewAt(pos);
        }
        return layoutIndicator;
    }




    public void setCurrentIndicator(int position, LinearLayout layoutIndicator, Context context) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }


}
