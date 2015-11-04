package me.zsr.feeder.source;

import android.content.Context;
import android.os.Handler;
import android.text.InputType;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;

import me.zsr.feeder.dao.FeedSource;
import me.zsr.feeder.data.FeedNetwork;
import me.zsr.feeder.util.AnalysisEvent;
import me.zsr.feeder.util.UrlUtil;

/**
 * @description:
 * @author: Zhangshaoru
 * @date: 11/4/15
 */
class SourceHelper {
    public static void showAddSourceDialog(final Context context, final Handler handler) {
        new MaterialDialog.Builder(context)
                .title("添加订阅")
                .content("RSS地址、网址或名称")
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText("添加")
                .negativeText("取消")
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
                                                Toast.makeText(context, "无效的源", Toast.LENGTH_SHORT).show();
                                                //TODO Add suffix and try again
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onNotFound() {
                                Toast.makeText(context, "没有找到相关的源", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                })
                .input("例如输入：酷", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
                    }
                }).show();
    }
}
