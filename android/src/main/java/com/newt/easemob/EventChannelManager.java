package com.newt.easemob;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry;

public class EventChannelManager {
    private EventChannel eventChannel;
    private EventChannel.EventSink eventSink;
    private PluginRegistry.Registrar registrar;

    EventChannelManager(PluginRegistry.Registrar registrar, String eventName) {
        this.eventChannel = new EventChannel(registrar.messenger(), eventName);
        this.registrar = registrar;
        eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                EventChannelManager.this.eventSink = eventSink;
            }

            @Override
            public void onCancel(Object o) {
                EventChannelManager.this.eventSink = null;
            }
        });
    }

    void dispose() {
        eventChannel.setStreamHandler(null);
    }

    void success(final Object o) {
        registrar.activity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (eventSink != null) {
                    eventSink.success(o);
                }
            }
        });
    }

    void error(final String s, final String s1, final Object o) {
        registrar.activity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (eventSink != null) {
                    eventSink.error(s, s1, o);
                }
            }
        });

    }
}
