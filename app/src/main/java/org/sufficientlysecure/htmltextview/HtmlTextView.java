/*
 * Copyright (C) 2013-2014 Dominik Schürmann <dominik@dominikschuermann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sufficientlysecure.htmltextview;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HtmlTextView extends JellyBeanSpanFixTextView {
    public static final String TAG = "HtmlTextView";
    public static final boolean DEBUG = false;
    boolean mDontConsumeNonUrlClicks = true;
    boolean mLinkHit;

    public HtmlTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HtmlTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HtmlTextView(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mLinkHit = false;
        boolean res = super.onTouchEvent(event);

        if (mDontConsumeNonUrlClicks) {
            return mLinkHit;
        }
        return res;
    }

    /**
     * Parses String containing HTML to Android's Spannable format and displays it in this TextView.
     *
     * @param html String containing HTML, for example: "<b>Hello world!</b>"
     */
    public void setHtmlText(String html) {
        Html.ImageGetter imgGetter = new UrlImageGetter(this, getContext());

        setText(Html.fromHtml(html, imgGetter, new HtmlTagHandler()));

        // Make links work
        setMovementMethod(LocalLinkMovementMethod.getInstance());
    }
}
