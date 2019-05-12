package com.newt.easemob;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.push.EMPushConfig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** EasemobPlugin */
public class EasemobPlugin implements MethodCallHandler {

  private final Registrar registrar;
  private final Context context;
  private EventChannel.EventSink eventSink;
  private Executor executor = Executors.newFixedThreadPool(4);
  /// some const values
  private static final String ChatType_chat = "Chat";
  private static final String ChatType_groupChat = "GroupChat";
  private static final String ChatType_chatRoom = "ChatRoom";

  private static final String MessageType_txt = "TXT";
  private static final String MessageType_image = "IMAGE";
  private static final String MessageType_video = "VIDEO";
  private static final String MessageType_location = "LOCATION";
  private static final String MessageType_voice = "VOICE";
  private static final String MessageType_file = "FILE";
  private static final String MessageType_cmd = "CMD";

  public static final String ConversationType_chat = "Chat";
  public static final String ConversationType_groupChat = "GroupChat";
  public static final String ConversationType_chatRoom = "ChatRoom";
  public static final String ConversationType_helpDesk = "HelpDesk";

  private final EMMessageListener emMessageListener = new EMMessageListener() {
    @Override
    public void onMessageReceived(List<EMMessage> messages) {
      if (eventSink != null) {
        final List<String> wrapMessages = new ArrayList<>(messages.size());
        // use fast json handle Enum type
        for (EMMessage message : messages) {
          wrapMessages.add(JSON.toJSONString(message));
        }
        registrar.activity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            eventSink.success(event("onMessageReceived", wrapMessages));
          }
        });
      }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
      if (eventSink != null) {
        final List<String> wrapMessages = new ArrayList<>(messages.size());
        // use fast json handle Enum type
        for (EMMessage message : messages) {
          wrapMessages.add(JSON.toJSONString(message));
        }
        registrar.activity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            eventSink.success(event("onCmdMessageReceived", wrapMessages));
          }
        });
      }
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
      if (eventSink != null) {
        final List<String> wrapMessages = new ArrayList<>(messages.size());
        // use fast json handle Enum type
        for (EMMessage message : messages) {
          wrapMessages.add(JSON.toJSONString(message));
        }
        registrar.activity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            eventSink.success(event("onMessageRead", wrapMessages));
          }
        });
      }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
      if (eventSink != null) {
        final List<String> wrapMessages = new ArrayList<>(messages.size());
        // use fast json handle Enum type
        for (EMMessage message : messages) {
          wrapMessages.add(JSON.toJSONString(message));
        }
        registrar.activity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            eventSink.success(event("onMessageDelivered", wrapMessages));
          }
        });
      }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
      if (eventSink != null) {
        final List<String> wrapMessages = new ArrayList<>(messages.size());
        // use fast json handle Enum type
        for (EMMessage message : messages) {
          wrapMessages.add(JSON.toJSONString(message));
        }
        registrar.activity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            eventSink.success(event("onMessageRecalled", wrapMessages));
          }
        });
      }
    }

    @Override
    public void onMessageChanged(final EMMessage message, final Object change) {
      if (eventSink != null) {
        registrar.activity().runOnUiThread(new Runnable() {
          @Override
          public void run() {
            eventSink.success(
                    event("onMessageChanged",
                            "message", JSON.toJSONString(message),
                            "change", "" + change));
          }
        });
      }
    }
  };

  private final EMContactListener emContactListener = new EMContactListener() {
    @Override
    public void onContactAdded(final String username) {
      registrar.activity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (eventSink != null) {
            eventSink.success(event("onContactAdded", username));
          }
        }
      });
    }

    @Override
    public void onContactDeleted(final String username) {
      registrar.activity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (eventSink != null) {
            eventSink.success(event("onContactDeleted", username));
          }
        }
      });
    }

    @Override
    public void onContactInvited(final String username, final String reason) {
      registrar.activity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (eventSink != null) {
            eventSink.success(
                    event("onContactInvited", "username", username, "reason", reason));
          }
        }
      });
    }

    @Override
    public void onFriendRequestAccepted(final String username) {
      registrar.activity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (eventSink != null) {
            eventSink.success(event("onFriendRequestAccepted", username));
          }
        }
      });
    }

    @Override
    public void onFriendRequestDeclined(final String username) {
      registrar.activity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (eventSink != null) {
            eventSink.success(event("onFriendRequestDeclined", username));
          }
        }
      });
    }
  };

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "com.newt.easemob/method_channel");
    channel.setMethodCallHandler(new EasemobPlugin(registrar));
  }

  private EasemobPlugin(final Registrar registrar) {
    this.registrar = registrar;
    context = registrar.context();
    new EventChannel(registrar.messenger(), "com.newt.easemob/event_channel").setStreamHandler(new EventChannel.StreamHandler() {
      @Override
      public void onListen(Object o, EventChannel.EventSink eventSink) {
        EasemobPlugin.this.eventSink = eventSink;
      }

      @Override
      public void onCancel(Object o) {
        EasemobPlugin.this.eventSink = null;
      }
    });

  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    switch (call.method) {
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;

      case "init":
        init(call);
        result.success(true);
        break;

      case "login":
        login(call, result);
        break;

      case "logout":
        logout(call, result);
        break;

      case "getAllContactsFromServer":
        getAllContactsFromServer(result);
        break;

      case "addContact":
        addContact(call, result);
        break;

      case "deleteContact":
        deleteContact(call, result);
        break;

      default:
        try {
          Method method = getClass().getDeclaredMethod(call.method, MethodCall.class, Result.class);
          method.setAccessible(true);
          method.invoke(this, call, result);
        } catch (NoSuchMethodException e) {
          result.notImplemented();
        } catch (Throwable t) {
          result.error("method_" + call.method, t.getMessage(), null);
        }
    }
  }

  @SuppressWarnings("unused")
  private void acceptInvitation(MethodCall call, Result result) {
    try {
      EMClient.getInstance().contactManager().acceptInvitation(
              argument(call, "username", "")
      );
      result.success(true);
    } catch (Throwable t) {
      result.error("[method_acceptInvitation]", t.getMessage(), false);
    }
  }

  @SuppressWarnings("unused")
  private void declineInvitation(MethodCall call, Result result) {
    try {
      EMClient.getInstance().contactManager().declineInvitation(
              argument(call, "username", "")
      );
      result.success(true);
    } catch (Throwable t) {
      result.error("method_declineInvitation", t.getMessage(), false);
    }
  }

  /**
   * 获取所有会话
   * @param call MethodCall
   * @param result Result
   */
  @SuppressWarnings("unused")
  private void getAllConversations(MethodCall call, Result result) {
    EMClient.getInstance().chatManager().loadAllConversations();
    Map<String, EMConversation> conversations
            = EMClient.getInstance().chatManager().getAllConversations();
    Map<String, String> res = new HashMap<>(conversations.size());
    // simple way to handle enum type
    for (Map.Entry item : conversations.entrySet()) {
      res.put(item.getKey().toString(), JSON.toJSONString(item.getValue()));
    }
    result.success(res);
  }

  /**
   * MethodCall arguments: username, [msgId], [deleteMessages = true]
   *
   * 如果传入`msgId`，删除此用户此条消息，否则删除和`username`的会话
   */
  @SuppressWarnings("unused")
  private void deleteConversation(MethodCall call, Result result) {
    EMClient.getInstance().chatManager().deleteConversation(
            argument(call, "conversationId", ""),
            argument(call, "deleteMessages", true));
    result.success(true);
  }

  @SuppressWarnings("unused")
  private void deleteConversationMessage(MethodCall call, Result result) {
    EMConversation conversation =
            EMClient.getInstance().chatManager().getConversation(
                    argument(call, "conversationId", "$$required"));
    conversation.removeMessage(argument(call, "msgId", "$$required"));
    result.success(true);
  }

  @SuppressWarnings("unused")
  private void importMessages(MethodCall call, Result result) {
    List<String> stringMessages = argument(call, "messages", new ArrayList<String>(0));
    List<EMMessage> messages = new ArrayList<>(stringMessages.size());
    for (String message : stringMessages) {
      messages.add((EMMessage) JSON.parse(message));
    }
    EMClient.getInstance().chatManager().importMessages(messages);
  }

  private void deleteContact(MethodCall call, Result result) {
    try {
      EMClient.getInstance().contactManager().deleteContact(
              argument(call, "username", ""),
              argument(call, "keepConversation", false)
      );
      result.success(true);
    } catch (Throwable t) {
      result.error("[method_deleteContact]", t.getMessage(), false);
    }
  }

  /**
   * send message
   * MethodCall arguments: to,
   */
  @SuppressWarnings("unused")
  private void sendMessage(MethodCall call, Result result) {
    EMMessage message;
    String chatType = argument(call, "chatType", ChatType_chat);
    String msgType = argument(call, "msgType", MessageType_txt);
    String to = argument(call, "to", "$$required");
    switch (msgType) {
      case MessageType_txt:
        message = EMMessage.createTxtSendMessage(
                argument(call, "content", "$$required"), to);
        break;

      case MessageType_image:
        message = EMMessage.createImageSendMessage(
                argument(call, "filePath", "$$required"),
                argument(call, "sendOriginalImage", true),
                to);
        break;

      case MessageType_location:
        message = EMMessage.createLocationSendMessage(
                argument(call, "latitude", 0.0D),
                argument(call, "longitude", 0.0D),
                argument(call, "locationAddress", ""),
                to);
        break;

      case MessageType_file:
        message = EMMessage.createFileSendMessage(
                argument(call, "filePath", "$$required"),
                to);
        break;

      case MessageType_voice:
        message = EMMessage.createVoiceSendMessage(
                argument(call, "filePath", "$$required"),
                argument(call, "timeLength", 0),
                to);
        break;

      case MessageType_video:
        message = EMMessage.createVideoSendMessage(
                argument(call, "filePath", "$$required"),
                argument(call, "imageThumbPath", ""),
                argument(call, "timeLength", 0),
                to);
        break;

      case MessageType_cmd:
        message = EMMessage.createSendMessage(EMMessage.Type.CMD);
        message.setTo(to);
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(argument(call, "action", "action"));
        message.addBody(cmdBody);
        break;

      default:
        result.error("[method_sendMessage]", "not support message type " + msgType, null);
        return;
    }

    switch (chatType) {
      case ChatType_chat:
        message.setChatType(EMMessage.ChatType.Chat);
        break;

      case ChatType_groupChat:
        message.setChatType(EMMessage.ChatType.GroupChat);
        break;

      case ChatType_chatRoom:
        message.setChatType(EMMessage.ChatType.ChatRoom);
        break;

      default:
        result.error("[method_sendMessage]", "not support chat type " + chatType, null);
        return;
    }

    // 发送扩展消息
    try {
      Map<String, Object> attributes = argument(call, "attributes", new HashMap(0));
      for (Map.Entry<String, Object> item : attributes.entrySet()) {
        Object value = item.getValue();
        if (value instanceof Integer) {
          message.setAttribute(item.getKey(), (Integer) item.getValue());
        } else if (value instanceof Long) {
          message.setAttribute(item.getKey(), (Long) item.getValue());
        } else if (value instanceof Boolean) {
          message.setAttribute(item.getKey(), (Boolean) item.getValue());
        } else if (value instanceof Map) {
          message.setAttribute(item.getKey(), new org.json.JSONObject((Map)item.getValue()));
        } else if (value instanceof List) {
          message.setAttribute(item.getKey(), new org.json.JSONArray((List)item.getValue()));
        } else {
          message.setAttribute(item.getKey(), item.getValue().toString());
        }
      }
    } catch (Throwable t) {
      // nothing need to do
      t.printStackTrace();
    }

    EMClient.getInstance().chatManager().sendMessage(message);
    result.success(message.getMsgId());
  }

  /**
   * get messages.
   * if `startMsgId` not supply, return all messages. otherwise
   * return `pageSize` messages.
   * MethodCall arguments: username,
   */
  @SuppressWarnings("unused")
  private void loadMoreMsgFromDB(MethodCall call, Result result) {
    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(
            argument(call, "conversationId", "$$required"));
    String startMsgId = argument(call, "startMsgId", null);
    int pageSize = argument(call, "pageSize", 10);
    List<EMMessage> messages;
    if (notEmptyStrings(startMsgId)) {
      messages = conversation.loadMoreMsgFromDB(startMsgId, 10);
    } else {
      messages = conversation.getAllMessages();
    }
    // wrap enum type
    List<String> res = new ArrayList<>(messages.size());
    for (EMMessage message : messages) {
      res.add(JSON.toJSONString(message));
    }
    result.success(res);
  }

  private void addContact(MethodCall call, Result result) {
    try {
      EMClient.getInstance().contactManager().addContact(
              argument(call, "username", ""),
              argument(call, "reason", ""));
      result.success(true);
    } catch (Throwable t) {
      result.error("[method_addContact]", t.getMessage(), false);
    }
  }

  private void getAllContactsFromServer(final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          List<String> contacts = EMClient.getInstance().contactManager().getAllContactsFromServer();
          resultRunOnUiThread(result, contacts, true);
        } catch (Throwable t) {
          result.error("[method_getAllContactsFromServer]", t.getMessage(), false);
        }
      }
    });
  }

  // result.success/error marked as @UiThread
  // errors length must >= 2 if supply
  private void resultRunOnUiThread(final Result result, final Object res, final boolean success, final Object ...errors) {
    registrar.activity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (success) {
          result.success(res);
        } else {
          result.error(errors[0].toString(), errors[1].toString(), res);
        }
      }
    });
  }

  private void login(MethodCall call, final Result result) {
    String id = argument(call, "id", null);
    String password = argument(call, "password", null);
    if (notEmptyStrings(id, password)) {
      EMClient.getInstance().login(id, password, new EMCallBack() {
        @Override
        public void onSuccess() {
          resultRunOnUiThread(result, true, true);
        }

        @Override
        public void onError(int code, String error) {
          resultRunOnUiThread(result, code, false, "[method_login]", error);
        }

        @Override
        public void onProgress(int progress, String status) {
          if (eventSink != null) {
            eventSink.success(event("onLoginProgress", "progress", progress, "status", status));
          }
        }
      });
    } else {
      result.error("[method_login]", "id or password must not be null", null);
    }

  }

  private void logout(MethodCall call, final Result result) {
    EMClient.getInstance().logout(argument(call, "unbindToken", true), new EMCallBack() {
      @Override
      public void onSuccess() {
        resultRunOnUiThread(result, true, true);
      }

      @Override
      public void onError(int code, String error) {
        resultRunOnUiThread(result, code, false, "[method_logout]", error);
      }

      @Override
      public void onProgress(int progress, String status) {
        if (eventSink != null) {
          eventSink.success(event("onLogoutProgress", "progress", progress, "status", status));
        }
      }
    });
  }

  /**
   * make an event, send to flutter
   * @param name event name
   * @param pairs data, if pairs length == 1, the result is
   *              { event: name, data: pairs }, otherwise the
   *              result is { event: name, data: {pairs[i]: pairs[i+1] ...} }
   * @return map to send to flutter
   */
  private Map<String, Object> event(String name, Object ...pairs) {
    Map<String, Object> result = new HashMap<>(2);
    result.put("event", name);
    if (pairs.length == 1) {
      result.put("data", pairs[0]);
    } else if (pairs.length > 1){
      Map<String, Object> data = new HashMap<>(pairs.length / 2);
      for (int i = 0; i < pairs.length - 1; i += 2) {
        data.put(pairs[i].toString(), pairs[i + 1]);
      }
      result.put("data", data);
    }
    return  result;
  }

  private <T> T argument(MethodCall call, String key, T defaultValue) {
    try {
      T value = call.argument(key);
      return value == null ? defaultValue : value;
    } catch (Throwable t) {
      return defaultValue;
    }
  }

  private void init(MethodCall call) {
    EMOptions options = new EMOptions();
    options.setAcceptInvitationAlways(argument(call, "acceptInvitationAlways", false));
    options.setAutoTransferMessageAttachments(argument(call, "autoTransferMessageAttachments", true));
    options.setAutoDownloadThumbnail(argument(call, "autoDownloadThumbnail", true));
    options.setAppKey(argument(call, "appKey", ""));
    options.setAutoAcceptGroupInvitation(argument(call, "autoAcceptGroupInvitation", true));
    options.setAutoLogin(argument(call, "autoLogin", true));
    options.setDeleteMessagesAsExitGroup(argument(call, "deleteMessageAsExitGroup", true));
    if (argument(call,"dnsUrl", null) != null) {
      options.setDnsUrl(argument(call,"dnsUrl", ""));
    }
    options.setImPort(argument(call,"imPort", 443));
    if (argument(call, "imServer", null) != null) {
      options.setIMServer(argument(call,"imServer", ""));
    }
    options.setUsingHttpsOnly(argument(call, "usingHttpsOnly", false));
    options.setUseFCM(argument(call, "useFCM", false));
    options.setRequireAck(argument(call, "requireAck", false));
    options.setRequireDeliveryAck(argument(call, "requireDeliverAck", false));
    EMPushConfig.Builder pushConfigBuilder = new EMPushConfig.Builder(context);
    if (argument(call, "fcmSender", null) != null) {
      pushConfigBuilder.enableFCM(argument(call, "fcmSender", ""));
    }
    if (argument(call, "enableHWPush", false)) {
      pushConfigBuilder.enableHWPush();
    }
    if (argument(call, "enableVivoPush", false)) {
      pushConfigBuilder.enableVivoPush();
    }
    String appId = argument(call, "meizuAppId", null);
    String appKey = argument(call, "meizuAppKey", null);
    if (notEmptyStrings(appId, appKey)) {
      pushConfigBuilder.enableMeiZuPush(appId, appKey);
    }
    appId = argument(call, "miAppId", null);
    appKey = argument(call, "miAppKey", null);
    if (notEmptyStrings(appId, appKey)) {
      pushConfigBuilder.enableMiPush(appId, appKey);
    }
    appKey = argument(call, "oppoAppKey", null);
    appId = argument(call, "oppoAppSecret", null);
    if (notEmptyStrings(appId, appKey)) {
      pushConfigBuilder.enableOppoPush(appKey, appId);
    }
    options.setPushConfig(pushConfigBuilder.build());
    EMClient.getInstance().init(context, options);
    EMClient.getInstance().setDebugMode(argument(call, "debugMode", false));
    initListener();
  }

  private boolean notEmptyStrings(String ...args) {
    for (String arg : args) {
      if (arg == null || arg.trim().equals("")) {
        return false;
      }
    }
    return true;
  }

  // 注册消息监听,通过EventChannel通知Flutter
  private void initListener() {
    EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    EMClient.getInstance().contactManager().setContactListener(emContactListener);
  }

  @SuppressWarnings("unused")
  private void getConversionInfo(MethodCall call, Result result) {
    final EMConversation conversation = EMClient.getInstance().chatManager().getConversation(
            argument(call, "conversationId", "$$required"));
    result.success(new HashMap<String, Object>(){{
      put("unreadMsgCount", conversation.getUnreadMsgCount());
      put("allMsgCount", conversation.getAllMsgCount());
      put("allMessages", conversation.getAllMessages().size());
    }});
  }

  @SuppressWarnings("unused")
  private void markAllMessagesAsRead(MethodCall call, final Result result) {
    final EMConversation conversation = getConversation(
            argument(call, "conversationId", "$$required"));
    executor.execute(new Runnable() {
      @Override
      public void run() {
        conversation.markAllMessagesAsRead();
        result.success(true);
      }
    });
  }

  @SuppressWarnings("unused")
  private void markMessageAsRead(final MethodCall call, final Result result) {
    final EMConversation conversation = getConversation(
            argument(call, "conversationId", "$$required"));
    executor.execute(new Runnable() {
      @Override
      public void run() {
        conversation.markMessageAsRead(argument(call, "msgId", "$$required"));
        result.success(true);
      }
    });
  }

  @SuppressWarnings("unused")
  private void markAllConversationsAsRead(MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        EMClient.getInstance().chatManager().markAllConversationsAsRead();
        result.success(true);
      }
    });
  }

  @SuppressWarnings("unused")
  private void loadMoreRoamingMessages(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          EMCursorResult<EMMessage> cursor = EMClient.getInstance().chatManager().fetchHistoryMessages(
                  argument(call, "conversationId", "$$required"),
                  conversationType(argument(call, "conversationType", ConversationType_chat)),
                  argument(call, "pageSize", 10),
                  argument(call, "startMsgId", ""));
          List<EMMessage> messages = cursor.getData();
          List<String> res = new ArrayList<>(messages.size());
          for (EMMessage message : messages) {
            res.add(JSON.toJSONString(message));
          }
          result.success(res);
        } catch (Throwable t) {
          result.error("[method_loadMoreRoamingMessages]", t.getMessage(), null);
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void recallMessage(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          EMMessage message = EMClient.getInstance().chatManager().getMessage(
                  argument(call, "msgId", "$$required"));
          EMClient.getInstance().chatManager().recallMessage(message);
          result.success(message.getMsgId());
        } catch (Throwable t) {
          result.error("[method_recallMessage]", t.getMessage(), null);
        }
      }
    });
  }

  private EMConversation getConversation(String id) {
    return EMClient.getInstance().chatManager().getConversation(id);
  }

  private EMConversation.EMConversationType conversationType(String type) {
    switch (type) {
      case ConversationType_groupChat:
        return EMConversation.EMConversationType.GroupChat;

      case ConversationType_chatRoom:
        return EMConversation.EMConversationType.ChatRoom;

      case ConversationType_helpDesk:
        return EMConversation.EMConversationType.HelpDesk;
      default:
        return EMConversation.EMConversationType.Chat;
    }
  }
}
