package me.zsr.feeder.source;

import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;

import java.util.List;

import me.zsr.feeder.R;
import me.zsr.feeder.dao.FeedItem;
import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.FeedNetwork;
import me.zsr.feeder.util.AnalysisEvent;
import me.zsr.feeder.util.LogUtil;
import me.zsr.feeder.util.UrlUtil;

/**
 * @description:
 * @author: Match
 * @date: 11/4/15
 */
class SourceHelper {
    public static void showAddSourceDialog(final Context context, final Handler handler) {
        new MaterialDialog.Builder(context)
                .title(R.string.add_subscription)
                .content(R.string.add_subscription_content)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText(R.string.add)
                .negativeText(R.string.cancel)
                .autoDismiss(false)
                .alwaysCallInputCallback() // this forces the callback to be invoked with every input change
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(final MaterialDialog dialog) {
                        super.onPositive(dialog);
                        String input = dialog.getInputEditText().getText().toString();

                        AVAnalytics.onEvent(context, AnalysisEvent.ADD_SOURCE, input);

                        UrlUtil.searchForTarget(handler, input, new UrlUtil.OnSearchResultListener() {
                            @Override
                            public void onFound(final String result, boolean isUploaded, String reTitle) {
                                if (isUploaded) {
                                    FeedNetwork.getInstance().addSource(result, reTitle, new FeedNetwork.OnAddListener() {
                                        @Override
                                        public void onError(String msg) {
                                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    dialog.dismiss();
                                } else {
                                    FeedNetwork.getInstance().verifySource(result, new FeedNetwork.OnVerifyListener() {
                                        @Override
                                        public void onResult(boolean isValid, FeedSource feedSource) {
                                            if (isValid) {
                                                // Upload
                                                AVObject feedSourceObj = new AVObject("FeedSource");
                                                feedSourceObj.put("title", feedSource.getTitle());
                                                feedSourceObj.put("url", feedSource.getUrl());
                                                feedSourceObj.put("link", feedSource.getLink());
                                                feedSourceObj.saveInBackground();

                                                FeedNetwork.getInstance().addSource(feedSource, new FeedNetwork.OnAddListener() {
                                                    @Override
                                                    public void onError(String msg) {
                                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                dialog.dismiss();
                                            } else {
                                                Toast.makeText(context, R.string.invalid_input, Toast.LENGTH_SHORT).show();
                                                //TODO Add suffix and try again
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onNotFound() {
                                Toast.makeText(context, R.string.no_subscription_found, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                })
                .input(R.string.add_subscription_hint, R.string.none, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                }).show();
    }

    public static int countUnread(FeedSource source) {
        int count = 0;
        for (FeedItem item : source.getFeedItems()) {
            if (!item.getRead()) {
                count++;
            }
        }
        return count;
    }
}
