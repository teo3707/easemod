package com.newt.easemob;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMMultiDeviceListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupInfo;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMucSharedFile;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.chat.EMPageResult;
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

  /// some const values `DartClassName_Field = value`
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

  private static final String ConversationType_chat = "Chat";
  private static final String ConversationType_groupChat = "GroupChat";
  private static final String ConversationType_chatRoom = "ChatRoom";
  private static final String ConversationType_helpDesk = "HelpDesk";

  private static final String GroupStyle_privateOnlyOwnerInvite = "EMGroupStylePrivateOnlyOwnerInvite";
  private static final String GroupStyle_privateMemberCanInvite = "EMGroupStylePrivateMemberCanInvite";
  private static final String GroupStyle_publicJoinNeedApproval = "EMGroupStylePublicJoinNeedApproval";
  private static final String GroupStyle_publicOpenJoin = "EMGroupStylePublicOpenJoin";


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

  private EMConnectionListener connectionListener = new EMConnectionListener() {
    @Override
    public void onConnected() {
      registrar.activity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (eventSink != null) {
            eventSink.success(event("onConnected"));
          }
        }
      });
    }

    @Override
    public void onDisconnected(final int errorCode) {
      registrar.activity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (eventSink != null) {
            eventSink.success(event("onDisconnected", errorCode));
          }
        }
      });
    }
  };

  private EMMultiDeviceListener multiDeviceListener = new EMMultiDeviceListener() {
    @Override
    public void onContactEvent(final int event, final String target, final String ext) {
      registrar.activity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (eventSink != null) {
            eventSink.success(event("onContactEvent", "event", event, "target", target, "ext", ext));
          }
        }
      });
    }

    @Override
    public void onGroupEvent(final int event, final String target, final List<String> userNames) {
      registrar.activity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (eventSink != null) {
            eventSink.success(event("onGroupEvent", "event", event, "target", target, "userNames", userNames));
          }
        }
      });
    }
  };

  private void eventSinkSuccess(final Object o) {
    registrar.activity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (eventSink != null) {
          eventSink.success(o);
        }
      }
    });
  }

  private EMGroupChangeListener groupChangeListener = new EMGroupChangeListener() {
    @Override
    public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
      eventSinkSuccess(event("onGroupInvitationReceived", "groupId", groupId,
              "groupName", groupName,
              "inviter", inviter,
              "reason", reason));
    }

    @Override
    public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
      eventSinkSuccess(event("onGroupRequestToJoinReceived", "groupId", groupId,
              "groupName", groupName,
              "applicant", applicant,
              "reason", reason));
    }

    @Override
    public void onRequestToJoinAccepted(String groupId, String groupName, String acceptor) {
      eventSinkSuccess(event("onGroupRequestToJoinAccepted", "groupId", groupId,
              "groupName", groupName,
              "acceptor", acceptor));
    }

    @Override
    public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
      eventSinkSuccess(event("onGroupRequestToJoinDeclined", "groupId", groupId,
              "groupName", groupName,
              "decliner", decliner,
              "reason", reason));
    }

    @Override
    public void onInvitationAccepted(String groupId, String invitee, String reason) {
      eventSinkSuccess(event("onGroupInvitationAccepted", "groupId", groupId,
              "invitee", invitee,
              "reason", reason));
    }

    @Override
    public void onInvitationDeclined(String groupId, String invitee, String reason) {
      eventSinkSuccess(event("onGroupInvitationDeclined", "groupId", groupId,
              "invitee", invitee,
              "reason", reason));
    }

    @Override
    public void onUserRemoved(String groupId, String groupName) {
      eventSinkSuccess(event("onGroupUserRemoved", "groupId", groupId,
              "groupName", groupName));
    }

    @Override
    public void onGroupDestroyed(String groupId, String groupName) {
      eventSinkSuccess(event("onGroupDestroyed", "groupId", groupId,
              "groupName", groupName));
    }

    @Override
    public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
      eventSinkSuccess(event("onAutoAcceptInvitationFromGroup", "groupId", groupId,
              "inviter", inviter,
              "inviteMessage", inviteMessage));
    }

    @Override
    public void onMuteListAdded(String groupId, List<String> mutes, long muteExpire) {
      eventSinkSuccess(event("onGroupMuteListAdded", "groupId", groupId,
              "mutes", mutes,
              "muteExpire", muteExpire));
    }

    @Override
    public void onMuteListRemoved(String groupId, List<String> mutes) {
      eventSinkSuccess(event("onGroupMuteListRemoved", "groupId", groupId,
              "mutes", mutes));
    }

    @Override
    public void onAdminAdded(String groupId, String administrator) {
      eventSinkSuccess(event("onGroupAdminAdded", "groupId", groupId,
              "admin", administrator));
    }

    @Override
    public void onAdminRemoved(String groupId, String administrator) {
      eventSinkSuccess(event("onGroupAdminRemoved", "groupId", groupId,
              "admin", administrator));
    }

    @Override
    public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {
      eventSinkSuccess(event("onGroupOwnerChanged", "groupId", groupId,
              "newOwner", newOwner,
              "oldOwner", oldOwner));
    }

    @Override
    public void onMemberJoined(String groupId, String member) {
      eventSinkSuccess(event("onGroupMemberJoined", "groupId", groupId,
              "member", member));
    }

    @Override
    public void onMemberExited(String groupId, String member) {
      eventSinkSuccess(event("onGroupMemberExited", "groupId", groupId,
              "member", member));
    }

    @Override
    public void onAnnouncementChanged(String groupId, String announcement) {
      eventSinkSuccess(event("onGroupAnnouncementChanged", "groupId", groupId,
              "announcement", announcement));
    }

    @Override
    public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {
      eventSinkSuccess(event("onGroupSharedFileAdded", "groupId", groupId,
              "sharedFile", JSON.toJSONString(sharedFile)));
    }

    @Override
    public void onSharedFileDeleted(String groupId, String fileId) {
      eventSinkSuccess(event("onGroupSharedFileDeleted", "groupId", groupId,
              "fileId", fileId));
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
          t.printStackTrace();
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
  private void sendMessage(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
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
            resultRunOnUiThread(result, null, false,
                    "[method_sendMessage]", "not support message type " + msgType);
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
            resultRunOnUiThread(result, null, false,
                    "[method_sendMessage]", "not support chat type " + chatType);
            return;
        }

        // 发送扩展消息
        try {
          Map<String, Object> attributes = argument(call, "attributes", new HashMap<String, Object>(0));
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
        resultRunOnUiThread(result, JSON.toJSONString(message), true);
      }
    });

  }

  /**
   * get messages.
   * if `startMsgId` not supply, return all messages. otherwise
   * return `pageSize` messages.
   * MethodCall arguments: username,
   */
  @SuppressWarnings("unused")
  private void loadMoreMsgFromDB(MethodCall call, Result result) {
    String conversationId = argument(call, "conversationId", "$$required");
    EMConversation conversation = EMClient.getInstance().chatManager().getConversation(conversationId);
    if (conversation == null) {
      result.error("[method_loadMoreFromDB]", "conversation `" + conversationId + "` not exist", true);
      return;
    }
    String startMsgId = argument(call, "startMsgId", null);
    int pageSize = argument(call, "pageSize", 10);
    List<EMMessage> messages;
    if (startMsgId != null) {
      messages = conversation.loadMoreMsgFromDB(startMsgId, pageSize);
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

  @SuppressWarnings("unused")
  private void getBlackListFromServer(MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          List<String> users = EMClient.getInstance().contactManager().getBlackListFromServer();
          resultRunOnUiThread(result, users, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(
                  result,
                  null,
                  false,
                  "[method_getBlackListFromServer]",
                  t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void getBlackListUserNames(MethodCall call,Result result) {
    List<String> users = EMClient.getInstance().contactManager().getBlackListUsernames();
    if (users == null) {
      users = new ArrayList<>(0);
    }
    result.success(users);
  }

  @SuppressWarnings("unused")
  private void addUserToBlackList(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String username = argument(call, "username", "$$required");
        boolean both = argument(call, "both", true);
        try {
          EMClient.getInstance().contactManager().addUserToBlackList(username, both);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_addUserToBlackList]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void removeUserFromBlackList(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String username = argument(call, "username", "$$required");
        try {
          EMClient.getInstance().contactManager().removeUserFromBlackList(username);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_removeUserFromBlackList]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void createAccount(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          String username = argument(call, "username", "$$required");
          String password = argument(call, "password", "$$required");
          EMClient.getInstance().createAccount(username, password);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_createAccount]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void getSelfIdsOnOtherPlatform(MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          List<String> ids = EMClient.getInstance().contactManager().getSelfIdsOnOtherPlatform();
          resultRunOnUiThread(result, ids, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "method_getSelfIdsOnOtherPlatform", t.getMessage());
        }
      }
    });
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
    EMClient.getInstance().addConnectionListener(connectionListener);
    EMClient.getInstance().addMultiDeviceListener(multiDeviceListener);
    EMClient.getInstance().groupManager().addGroupChangeListener(groupChangeListener);
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
        resultRunOnUiThread(result, true, true);
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
        resultRunOnUiThread(result, true, true);
      }
    });
  }

  @SuppressWarnings("unused")
  private void markAllConversationsAsRead(MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        EMClient.getInstance().chatManager().markAllConversationsAsRead();
        resultRunOnUiThread(result, true, true);
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
          resultRunOnUiThread(result, res, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_loadMoreRoamingMessages]", t.getMessage());
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
          resultRunOnUiThread(result, message.getMsgId(), true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_recallMessage]", t.getMessage());
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


  /// --------------------群组管理-----------------------

  @SuppressWarnings("unused")
  private void createGroup(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        EMGroupOptions option = new EMGroupOptions();
        option.maxUsers = argument(call, "maxUsers", 200);
        switch (argument(call, "style", GroupStyle_privateOnlyOwnerInvite)) {
          case GroupStyle_privateMemberCanInvite:
            option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
            break;

          case GroupStyle_publicJoinNeedApproval:
            option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
            break;

          case GroupStyle_publicOpenJoin:
            option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
            break;

          default:
            option.style = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
        }
        option.extField = argument(call, "extField", "");
        option.inviteNeedConfirm = argument(call, "inviteNeedConfirm", false);
        String groupName = argument(call, "groupName", "$$required");
        String desc = argument(call, "desc", "");
        String reason = argument(call, "reason", "");
        List<String> allMembers = argument(call, "members", new ArrayList<String>(0));
        String[] members = allMembers.toArray(new String[0]);
        try {
          EMGroup group = EMClient.getInstance()
                  .groupManager()
                  .createGroup(groupName, desc, members, reason, option);
          resultRunOnUiThread(result, JSON.toJSONString(group), true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false, t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void addGroupAdmin(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String admin = argument(call, "admin", "$$required");
        try {
          EMGroup group = EMClient.getInstance().groupManager().addGroupAdmin(groupId, admin);
          resultRunOnUiThread(result, JSON.toJSONString(group), true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_addGroupAdmin]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void removeGroupAdmin(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String admin = argument(call, "admin", "$$required");
        try {
          EMGroup group = EMClient.getInstance().groupManager().removeGroupAdmin(groupId, admin);
          resultRunOnUiThread(result, JSON.toJSONString(group), true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_removeGroupAdmin]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void changeGroupOwner(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String newOwner = argument(call, "newOwner", "$$required");
        try {
          EMGroup group = EMClient.getInstance().groupManager().changeOwner(groupId, newOwner);
          resultRunOnUiThread(result, JSON.toJSONString(group), true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_changeGroupOwner]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void addOrInviteUsersToGroup(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String reason = argument(call, "reason", "");
        List<String> allMembers = argument(call, "members", new ArrayList<String>(0));
        String[] members = allMembers.toArray(new String[0]);
        EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
        if (group == null) {
          resultRunOnUiThread(result, true, false,
                  "[method_addUserToGroup]", "group `" + groupId +"` does not exists");
        } else {
          try {
            if (EMClient.getInstance().getCurrentUser().equals(group.getOwner())) {
              EMClient.getInstance().groupManager().addUsersToGroup(groupId, members);
            } else {
              EMClient.getInstance().groupManager().inviteUser(groupId, members, reason);
            }
            resultRunOnUiThread(result, true, true);
          } catch (Throwable t) {
            t.printStackTrace();
            resultRunOnUiThread(result, null, false,
                    "[method_addOrInviteUsersToGroup]", t.getMessage());
          }
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void removeUserFromGroup(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String username = argument(call, "username", "$$required");
        try {
          EMClient.getInstance().groupManager().removeUserFromGroup(groupId, username);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_removeUserFromGroup]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void joinOrApplyJoinGroup(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String reason = argument(call, "reason", "");
        EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
        if (group == null) {
          resultRunOnUiThread(result, true, false,
                  "[method_joinOrApplyJoinGroup]", "group `" + groupId +"` does not exists");
        } else {
          try {
            if (group.isMemberOnly()) {
              EMClient.getInstance().groupManager().applyJoinToGroup(groupId, reason);
            } else {
              EMClient.getInstance().groupManager().joinGroup(groupId);
            }
            resultRunOnUiThread(result, true, true);
          } catch (Throwable t) {
            t.printStackTrace();
            resultRunOnUiThread(result, null, false,
                    "[method_joinOrApplyJoinGroup]", t.getMessage());
          }
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void leaveGroup(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        try {
          EMClient.getInstance().groupManager().leaveGroup(groupId);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_leaveGroup]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void destroyGroup(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          String groupId = argument(call, "groupId", "$$required");
          EMClient.getInstance().groupManager().destroyGroup(groupId);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_destroyGroup]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void fetchGroupMembers(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        final int pageSize = argument(call, "pageSize", 20);
        String cursor = argument(call, "cursor", "");
        try {
          final EMCursorResult<String> emResult = EMClient
                  .getInstance()
                  .groupManager()
                  .fetchGroupMembers(groupId, cursor, pageSize);
          final List<String> members = new ArrayList<>(emResult.getData());
          resultRunOnUiThread(result, new HashMap<String, Object>(3) {{
            put("hasMore", !TextUtils.isEmpty(emResult.getCursor())
                    && emResult.getData().size() == pageSize);
            put("data", members);
            put("cursor", emResult.getCursor());
          }}, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_fetchGroupMembers]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void getJoinedGroupsFromServer(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        int pageIndex = argument(call, "pageIndex", 1);
        int pageSize = argument(call, "pageSize", 20);
        boolean fetchAll = argument(call, "fetchAll", false);
        List<EMGroup> groups;
        try {
          if (fetchAll) {
            groups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
          } else {
            groups = EMClient.getInstance().groupManager().getJoinedGroupsFromServer(pageIndex, pageSize);
          }
          List<String> res = new ArrayList<>(groups.size());
          for (EMGroup group : groups) {
            res.add(JSON.toJSONString(group));
          }
          resultRunOnUiThread(result, res, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_getJoinedGroupsFromServer]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void getAllGroups(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        List<EMGroup> groups = EMClient.getInstance().groupManager().getAllGroups();
        List<String> res = new ArrayList<>(groups.size());
        for (EMGroup group : groups) {
          res.add(JSON.toJSONString(group));
        }
        resultRunOnUiThread(result, res, true);
      }
    });
  }

  @SuppressWarnings("unused")
  private void getPublicGroupsFromServer(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        final int pageSize = argument(call, "pageSize", 20);
        String cursor = argument(call, "cursor", "");
        try {
          final EMCursorResult<EMGroupInfo> emResult = EMClient.getInstance()
                  .groupManager()
                  .getPublicGroupsFromServer(pageSize, cursor);
          resultRunOnUiThread(result, new HashMap<String, Object>(3) {{
            put("hasMore", !TextUtils.isEmpty(emResult.getCursor())
                    && emResult.getData().size() == pageSize);
            put("data", emResult.getData());
            put("cursor", emResult.getCursor());
          }}, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_getPublicGroupsFromServer]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void changeGroup(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String changedGroupName = argument(call, "changedGroupName", null);
        String description = argument(call, "description", null);
        String announcement = argument(call, "announcement", null);
        String extension = argument(call, "extension", null);
        try {
          if (changedGroupName != null) {
            EMClient.getInstance().groupManager().changeGroupName(groupId, changedGroupName);
          }
          if (description != null) {
            EMClient.getInstance().groupManager().changeGroupDescription(groupId, description);
          }
          if (announcement != null) {
            EMClient.getInstance().groupManager().updateGroupAnnouncement(groupId, announcement);
          }
          if (extension != null) {
            EMClient.getInstance().groupManager().updateGroupExtension(groupId, extension);
          }
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_changeGroup]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void getGroup(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        boolean fetchMembers = argument(call, "fetchMembers", true);
        boolean tryServerFirst = argument(call, "tryServerFirst", false);
        try {
          EMGroup group;
          if (tryServerFirst) {
            group = EMClient.getInstance().groupManager().getGroupFromServer(groupId, fetchMembers);
          } else {
            group = EMClient.getInstance().groupManager().getGroup(groupId);
          }
          if (group == null && !tryServerFirst) {
            group = EMClient.getInstance().groupManager().getGroupFromServer(groupId, fetchMembers);
          }
          if (group == null) {
            resultRunOnUiThread(result, true, false,
                    "[method_getGroup]", groupId + " does not exists");
          } else {
            resultRunOnUiThread(result, JSON.toJSONString(group), true);
          }
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_getGroup]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void blockGroupMessage(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        try {
          EMClient.getInstance().groupManager().blockGroupMessage(groupId);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_blockGroupMessage]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void unblockGroupMessage(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        try {
          EMClient.getInstance().groupManager().unblockGroupMessage(groupId);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_unblockGroupMessage]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void blockGroupUser(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String username = argument(call, "username", "$$required");
        try {
          EMClient.getInstance().groupManager().blockUser(groupId, username);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_blockGroupUser]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void unblockGroupUser(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String username = argument(call, "username", "$$required");
        try {
          EMClient.getInstance().groupManager().unblockUser(groupId, username);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_unblockGroupUser]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void getGroupBlockedUsers(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        int pageSize = argument(call, "pageSize", 200);
        // 统一从第一页开始
        int pageIndex = argument(call, "pageIndex", 1) - 1;
        try {
          List<String> users =
                  EMClient.getInstance().groupManager().getBlockedUsers(groupId, pageIndex, pageSize);
          resultRunOnUiThread(result, users, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_getGroupBlockedUsers]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void muteGroupMembers(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        List<String> members = argument(call, "members", new ArrayList<String>(0));
        long duration = argument(call, "duration", 12L * 30 * 24 * 60 * 60 * 1000);
        try {
          EMClient.getInstance().groupManager().muteGroupMembers(groupId, members, duration);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_muteGroupMembers]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void unMuteGroupMembers(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        List<String> members = argument(call, "members", new ArrayList<String>(0));
        try {
          EMClient.getInstance().groupManager().unMuteGroupMembers(groupId, members);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_unMuteGroupMembers]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void fetchGroupMuteList(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        int pageIndex = argument(call, "pageIndex", 1); // ######## 是否是从1开始??
        int pageSize = argument(call, "pageSize", 200);
        try {
          Map<String, Long> res =
                  EMClient.getInstance().groupManager().fetchGroupMuteList(groupId, pageIndex, pageSize);
          resultRunOnUiThread(result, res, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_fetchGroupMuteList]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void fetchGroupAnnouncement(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        try {
          String res = EMClient.getInstance().groupManager().fetchGroupAnnouncement(groupId);
          resultRunOnUiThread(result, res, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_fetchGroupAnnouncement]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void uploadGroupSharedFile(final MethodCall call, final Result result) {
    String eventChannel = argument(call, "eventChannel", "$$required");
    final EventChannelManager eventChannelManager = new EventChannelManager(
            registrar, "com.newt.easemob/upload_event_channel_" + eventChannel);
    // tell flutter can user the event channel
    resultRunOnUiThread(result, true, true);
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String filePath = argument(call, "filePath", "$$required");
        try {
          final EMMucSharedFile emFile = EMClient.getInstance().groupManager().uploadGroupSharedFile(groupId, filePath, new EMCallBack() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(final int code, final String error) {
              eventChannelManager.success(event("onError", "error", error, "code", code));
            }

            @Override
            public void onProgress(int progress, String status) {
              eventChannelManager.success(event("onProgress", "progress", progress, "status", status));
            }
          });
          eventChannelManager.success(event("onSuccess", JSON.toJSONString(emFile)));
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_uploadGroupSharedFile]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void deleteGroupSharedFile(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String fileId = argument(call, "fileId", "$$required");
        try {
          EMClient.getInstance().groupManager().deleteGroupSharedFile(groupId, fileId);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_deleteGroupSharedFile]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void fetchGroupSharedFileList(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        int pageIndex = argument(call, "pageIndex", 1); // ######## 是否是从1开始??
        int pageSize = argument(call, "pageSize", 200);
        try {
          List<EMMucSharedFile> files = EMClient.getInstance()
                  .groupManager().fetchGroupSharedFileList(groupId, pageIndex, pageSize);
          List<String> res = new ArrayList<>(files.size());
          for (EMMucSharedFile file : files) {
            res.add(JSON.toJSONString(file));
          }
          resultRunOnUiThread(result, res, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_fetchGroupSharedFileList]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void downloadGroupSharedFile(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String groupId = argument(call, "groupId", "$$required");
        String fileId = argument(call, "fileId", "$$required");
        String savePath = argument(call, "savePath", "$$required");
        final String eventChannel = argument(call, "eventChannel", "$$required");
        final EventChannelManager eventChannelManager = new EventChannelManager(
                registrar, "com.newt.easemob/download_event_channel_" + eventChannel);
        try {
          EMClient.getInstance().groupManager().downloadGroupSharedFile(groupId, fileId, savePath, new EMCallBack() {
            @Override
            public void onSuccess() {
              eventChannelManager.success(event("onSuccess"));
            }

            @Override
            public void onError(int code, String error) {
              eventChannelManager.success(event("onError", "code", code, "error", error));
            }

            @Override
            public void onProgress(int progress, String status) {
              eventChannelManager.success(event("onProgress", "progress", "status", status));
            }
          });
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_downloadGroupSharedFile]", t.getMessage());
        }
      }
    });
  }


  // ------ chat room -----

  @SuppressWarnings("unused")
  private void createChatRoom(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String subject = argument(call, "subject", "");
        String description = argument(call, "description", "");
        String welcomeMessage = argument(call, "welcomeMessage", "");
        List<String> members = argument(call, "members", new ArrayList<String>(0));
        int maxUserCount = argument(call, "maxUserCount", 5000);
        try {
          EMChatRoom chatRoom = EMClient.getInstance().chatroomManager().createChatRoom(
                  subject, description, welcomeMessage, maxUserCount, members);
          resultRunOnUiThread(result, JSON.toJSONString(chatRoom), true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_createChatRoom]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void joinChatRoom(final MethodCall call, final Result result) {
    String roomId = argument(call, "roomId", "$$required");
    EMClient.getInstance().chatroomManager().joinChatRoom(roomId, new EMValueCallBack<EMChatRoom>() {
      @Override
      public void onSuccess(EMChatRoom value) {
        resultRunOnUiThread(result, JSON.toJSONString(value), true);
      }

      @Override
      public void onError(int error, String errorMsg) {
        resultRunOnUiThread(result, error, false, "[method_joinChatRoom]", errorMsg);
      }
    });
  }

  @SuppressWarnings("unused")
  private void leaveChatRoom(final MethodCall call, final Result result) {
    String roomId = argument(call, "roomId", "$$required");
    EMClient.getInstance().chatroomManager().leaveChatRoom(roomId);
    result.success(true);
  }

  @SuppressWarnings("unused")
  private void destroyChatRoom(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        String roomId = argument(call, "roomId", "$$required");
        try {
          EMClient.getInstance().chatroomManager().destroyChatRoom(roomId);
          resultRunOnUiThread(result, true, true);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_destroyChatRoom]", t.getMessage());
        }
      }
    });
  }

  @SuppressWarnings("unused")
  private void fetchPublicChatRoomsFromServer(final MethodCall call, final Result result) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        int pageSize = argument(call, "pageSize", 20);
        int pageIndex = argument(call, "pageIndex", 1);
        try {
          EMPageResult<EMChatRoom> rooms = EMClient.getInstance()
                  .chatroomManager().fetchPublicChatRoomsFromServer(pageIndex, pageSize);
        } catch (Throwable t) {
          t.printStackTrace();
          resultRunOnUiThread(result, null, false,
                  "[method_fetchPublicChatRoomsFromServer]", t.getMessage());
        }
      }
    });
  }
}
