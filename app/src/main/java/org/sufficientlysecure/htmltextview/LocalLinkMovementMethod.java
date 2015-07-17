/*
 * Copyright (C) 2015 Heliangwei
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

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Copied from http://stackoverflow.com/questions/8558732
 * Fix : TextView with LinkMovementMethod makes list item unclickable.
 */
public class LocalLinkMovementMethod extends LinkMovementMethod {
    static LocalLinkMovementMethod sInstance;

    public static LocalLinkMovementMethod getInstance() {
        if (sInstance == null)
            sInstance = new LocalLinkMovementMethod();

        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView textview, Spannable spannable, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= textview.getTotalPaddingLeft();
            y -= textview.getTotalPaddingTop();

            x += textview.getScrollX();
            y += textview.getScrollY();

            Layout layout = textview.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = spannable.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(textview);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(spannable,
                            spannable.getSpanStart(link[0]),
                            spannable.getSpanEnd(link[0]));
                }

                if (textview instanceof HtmlTextView) {
                    ((HtmlTextView) textview).mLinkHit = true;
                }
                return true;
            } else {
                Selection.removeSelection(spannable);
                Touch.onTouchEvent(textview, spannable, event);
                return false;
            }
        }
        return Touch.onTouchEvent(textview, spannable, event);
    }
}
