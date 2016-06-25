package org.kslr.wechatautoreply;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by kslr on 16/6/26.
 */
public class AutoReplyService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        int eventType = accessibilityEvent.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                List<CharSequence> texts = accessibilityEvent.getText();
                if(! texts.isEmpty()) {
                    Log.d("main", String.valueOf(texts));
                    if(accessibilityEvent.getParcelableData() != null && accessibilityEvent.getParcelableData() instanceof Notification) {
                        Notification notification = (Notification) accessibilityEvent.getParcelableData();
                        PendingIntent pendingIntent = notification.contentIntent;
                        try {
                            pendingIntent.send();
                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = accessibilityEvent.getClassName().toString();
                Log.d("main", className);
                if(className.equals("com.tencent.mm.ui.LauncherUI")) {
                    autoReply();
                }
        }
    }

    @SuppressLint("NewApi")
    private void autoReply() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> inputList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/yq");
        AccessibilityNodeInfo inputNode = inputList.get(0);
        inputNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("message", "这是一条测试信息");
        clipboardManager.setPrimaryClip(clipData);

        inputNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
        inputNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);

        List<AccessibilityNodeInfo> sendList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/yw");
        AccessibilityNodeInfo sendNode = sendList.get(0);
        sendNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);

        this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    @Override
    public void onInterrupt() {

    }
}
