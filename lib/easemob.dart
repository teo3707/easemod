import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';
import 'package:flutter/material.dart';


class Easemob {
  static const MethodChannel _channel =
      const MethodChannel('com.newt.easemob/method_channel');
  static const EventChannel _eventChannel =
      const EventChannel('com.newt.easemob/event_channel');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Easemob _instance;

  List<EasemobListener> _listeners = [];

  Easemob._intern() {
    /// handle events:
    /// [ onMessageReceived, onCmdMessageReceived, onMessageRead,
    ///   onMessageDelivered, onMessageRecalled, onMessageChanged,
    ///   onContactAdded, onContactDeleted,onContactInvited,
    ///   onFriendRequestAccepted, onFriendRequestDeclined ]
    _eventChannel.receiveBroadcastStream().listen((data) {
      print('ease mob event: $data');
      for (EasemobListener listener in _listeners) {
        switch (data['event']) {
          case 'onMessageReceived':
            List messages = data['data'];
            listener.onMessageReceived(
                messages.map((item) => json.encode(item)).toList());
            break;

          case 'onCmdMessageReceived':
            List messages = data['data'];
            listener.onCmdMessageReceived(
                messages.map((item) => json.encode(item)).toList());
            break;

          case 'onMessageRead':
            List messages = data['data'];
            listener.onMessageRead(
                messages.map((item) => json.encode(item)).toList());
            break;

          case 'onMessageDelivered':
            List messages = data['data'];
            listener.onMessageDelivered(
                messages.map((item) => json.encode(item)).toList());
            break;

          case 'onMessageRecalled':
            List messages = data['data'];
            listener.onMessageRecalled(
                messages.map((item) => json.encode(item)).toList());
            break;

          case 'onMessageChanged':
            listener.onMessageChanged(
                json.decode(data['data']['message']), data['data']['change']);
            break;

          case 'onContactAdded':
            listener.onContactAdded(data['data']);
            break;

          case 'onContactDeleted':
            listener.onContactDeleted(data['data']);
            break;

          case 'onContactInvited':
            listener.onContactInvited(
                data['data']['username'], data['data']['reason']);
            break;

          case 'onFriendRequestAccepted':
            listener.onFriendRequestAccepted(data['data']);
            break;

          case 'onFriendRequestDeclined':
            listener.onFriendRequestDeclined(data['data']);
            break;

          case 'onConnected':
            listener.onConnected();
            break;

          case 'onDisconnected':
            listener.onDisconnected(data['data']);
            break;

          default:
            print('not implement event on $data');
        }
      }
    });
  }

  factory Easemob() {
    if (_instance == null) {
      _instance = Easemob._intern();
    }
    return _instance;
  }

  void addListener(EasemobListener listener) {
    if (!_listeners.contains(listener)) {
      _listeners.add(listener);
    }
  }

  void removeListener(EasemobListener listener) {
    _listeners.remove(listener);
  }

  Future<bool> init(
    String appKey, {
    bool acceptInvitationAlways = false,
    bool autoTransferMessageAttachments = true,
    bool autoDownloadThumbnail = true,
    bool autoAcceptGroupInvitation = true,
    bool autoLogin = true,
    bool deleteMessageAsExitGroup = true,
    String dnsUrl,
    int imPort = 443,
    String imServer,
    bool usingHttpsOnly = false,
    bool useFCM = false,
    bool requireAck = false,
    bool requireDeliverAck = false,
    String fcmSender,
    bool enableHWPush = false,
    bool enableVivoPush = false,
    String meizuAppId,
    String meizuAppKey,
    String miAppId,
    String miAppKey,
    String oppoAppKey,
    String oppoAppSecret,
    bool debugMode = false,
  }) async {
    final bool result = await _channel.invokeMethod("init", {
      'acceptInvitationAlways': acceptInvitationAlways,
      'autoTransferMessageAttachments': autoTransferMessageAttachments,
      'autoDownloadThumbnail': autoDownloadThumbnail,
      'appKey': appKey,
      'autoAcceptGroupInvitation': autoAcceptGroupInvitation,
      'autoLogin': autoLogin,
      'deleteMessageAsExitGroup': deleteMessageAsExitGroup,
      'dnsUrl': dnsUrl,
      'imPort': imPort,
      'imServer': imServer,
      'usingHttpsOnly': usingHttpsOnly,
      'useFCM': useFCM,
      'requireAck': requireAck,
      'requireDeliverAck': requireDeliverAck,
      'fcmSender': fcmSender,
      'enableHWPush': enableHWPush,
      'enableVivoPush': enableVivoPush,
      'meizuAppId': meizuAppId,
      'meizuAppKey': meizuAppKey,
      'miAppId': miAppId,
      'miAppKey': miAppKey,
      'oppoAppKey': oppoAppKey,
      'oppoAppSecret': oppoAppSecret,
      'debugMode': debugMode,
    });
    return result;
  }

  Future<bool> login(String id, String password) async {
    final bool res = await _channel.invokeMethod('login', {
      'id': id,
      'password': password,
    });
    return res;
  }

  Future<bool> logout({
    bool unbindToken = true,
  }) async {
    final bool res = await _channel.invokeMethod('logout', {
      'unbindToken': unbindToken,
    });
    return res;
  }

  Future<List<String>> getAllContactsFromServer() async {
    final List contacts =
        await _channel.invokeMethod('getAllContactsFromServer');
    return contacts.cast();
  }

  Future<Map<String, Map>> getAllConversations() async {
    final Map conversations =
        await _channel.invokeMethod('getAllConversations');
    return conversations
        .map((k, v) => MapEntry<String, Map>(k, json.decode(v)));
  }

  /// return the message
  Future<Map> sendMessage({
    @required String to,
    String msgType = MessageType.txt,
    String chatType = ChatType.chat,
    String content = '', // MessageType.txt required
    String filePath, // MessageType.[image, file, voice, video] required
    bool sendOriginalImage, // MessageType.image required, default is true
    double latitude, // MessageType.location required, default is 0.0
    double longitude, // MessageType.location required, default is 0.0
    String locationAddress = '', // MessageType.location required
    int timeLength =
        0, // MessageType.[voice, video] required, the length of the video time, unit s
    String imageThumbPath = '', // use for MessageType.video
    String action = 'action', // use for MessageType.cmd
    Map<String, dynamic> attributes, // use to extend message
  }) async {
    final res = await _channel.invokeMethod('sendMessage', {
      'to': to,
      'msgType': msgType,
      'chatType': chatType,
      'content': content,
      'filePath': filePath,
      'sendOriginalImage': sendOriginalImage,
      'latitude': latitude,
      'longitude': longitude,
      'locationAddress': locationAddress,
      'timeLength': timeLength,
      'imageThumbPath': imageThumbPath,
      'action': action,
      'attributes': attributes,
    });
    return json.decode(res);
  }

  /// throw PlatformException if error
  Future<bool> addContact({
    @required String username,
    String reason = '',
  }) async {
    final bool res = await _channel.invokeMethod('addContact', {
      'username': username,
      'reason': reason,
    });
    return res;
  }

  Future<bool> deleteContact({
    @required String username,
    bool keepConversation = false,
  }) async {
    final bool res = await _channel.invokeMethod('deleteContact', {
      'username': username,
      'keepConversation': keepConversation,
    });
    return res;
  }

  /// @see http://docs-im.easemob.com/im/android/basics/message
  /// to get all messages, set startMsgId = null
  Future<List<Map>> loadMoreMsgFromDB({
    @required String conversationId,
    String startMsgId = '',
    int pageSize,
  }) async {
    List messages = await _channel.invokeMethod('loadMoreMsgFromDB', {
      'conversationId': conversationId,
      'startMsgId': startMsgId,
      'pageSize': pageSize,
    });
    return messages.map<Map>((m) => json.decode(m)).toList();
  }

  Future<bool> acceptInvitation(String username) async {
    final bool res = await _channel.invokeMethod('acceptInvitation', {
      'username': username,
    });
    return res;
  }

  Future<bool> declineInvitation(String username) async {
    final bool res = await _channel.invokeMethod('declineInvitation', {
      'username': username,
    });
    return res;
  }

  Future<bool> deleteConversation({
    @required String conversationId,
    bool deleteMessages = true,
  }) async {
    final bool res = await _channel.invokeMethod('deleteConversation', {
      'conversationId': conversationId,
      'deleteMessages': deleteMessages,
    });
    return res;
  }

  Future<bool> deleteConversationMessage(
      {@required conversationId, @required msgId}) async {
    final bool res = await _channel.invokeMethod('deleteConversationMessage', {
      'conversationId': conversationId,
      'msgId': msgId,
    });
    return res;
  }

  Future<bool> importMessages(List messages) async {
    final bool res = await _channel.invokeMethod('importMessages', {
      'messages': messages,
    });
    return res;
  }

  /// @see http://docs-im.easemob.com/im/android/basics/message#%E8%8E%B7%E5%8F%96%E6%B6%88%E6%81%AF%E6%80%BB%E6%95%B0
  /// conversationId user id, group id or chat room id
  Future<Map<String, dynamic>> getConversionInfo(String conversationId) async {
    final Map res = await _channel.invokeMethod('getConversionInfo', {
      'conversationId': conversationId,
    });
    return res.cast();
  }

  Future<bool> markAllMessagesAsRead(String conversationId) async {
    final bool res = await _channel.invokeMethod('markAllMessagesAsRead', {
      'conversationId': conversationId,
    });
    return res;
  }

  Future<bool> markMessageAsRead({
    @required String conversationId,
    @required String msgId,
  }) async {
    final bool res = await _channel.invokeMethod('markMessageAsRead', {
      'conversationId': conversationId,
      'msgId': msgId,
    });
    return res;
  }

  Future<bool> markAllConversationsAsRead() async {
    final bool res = await _channel.invokeMethod('markAllConversationsAsRead');
    return res;
  }

  /// @see http://docs-im.easemob.com/im/android/basics/message#%E6%B6%88%E6%81%AF%E6%BC%AB%E6%B8%B8
  Future<List<Map>> loadMoreRoamingMessages({
    @required conversationId,
    String conversationType = ConversationType.chat,
    int pageSize = 10,
    String startMsgId =
        '', // the start search roam message, if empty start from the server last message
  }) async {
    final List messages =
        await _channel.invokeMethod('loadMoreRoamingMessages', {
      'conversationId': conversationId,
      'pageSize': pageSize,
      'startMsgId': startMsgId,
    });
    return messages.map<Map>((m) => json.decode(m)).toList();
  }

  /// @see http://docs-im.easemob.com/im/android/basics/message#%E6%92%A4%E5%9B%9E%E6%B6%88%E6%81%AF%E5%8A%9F%E8%83%BD
  Future<String> recallMessage(String msgId) async {
    final String msg = await _channel.invokeMethod("recallMessage", {
      'msgId': msgId,
    });
    return msg;
  }

  Future<List<String>> getBlackListFromServer() async {
    final List users = await _channel.invokeMethod('getBlackListFromServer');
    return users.cast();
  }

  /// 从本地db获取黑名单列表
  Future<List<String>> getBlackListUserNames() async {
    final List users = await _channel.invokeMethod('getBlackListUserNames');
    return users.cast();
  }

  /// `both`如果为true，则把用户加入到黑名单后双方发消息时对方都收不到；false，
  /// 则我能给黑名单的中用户发消息，但是对方发给我时我是收不到的
  Future<bool> addUserToBlackList(
      {@required String username, bool both = true}) async {
    final bool res = await _channel.invokeMethod('addUserToBlackList', {
      'username': username,
      'both': both,
    });
    return res;
  }

  Future<bool> removeUserFromBlackList(String username) async {
    final bool res = await _channel.invokeMethod('removeUserFromBlackList');
    return res;
  }

  /// 获取同一账号在其他端登录的id
  Future<List<String>> getSelfIdsOnOtherPlatform() async {
    final List res = await _channel.invokeMethod('getSelfIdsOnOtherPlatform');
    return res.cast();
  }

  /// @see http://docs-im.easemob.com/im/android/sdk/basic#%E6%B3%A8%E5%86%8C
  /// 注册用户名会自动转为小写字母，所以建议用户名均以小写注册。
  /// （强烈建议开发者通过后台调用 REST 接口去注册环信 ID，
  /// 客户端注册方法不提倡使用。）
  Future<bool> createAccount({
    @required String username,
    @required String password,
  }) async {
    final bool res = await _channel.invokeMethod('createAccount', {
      'username': username,
      'password': password,
    });
    return res;
  }

  /// @param extField 群详情扩展，可以采用json格式，包含跟多群信息
  /// @param inviteNeedConfirm 邀请进群是否需要对方同意。如果设置为false，直接加被邀请人进群。
  /// 在此情况下，被邀请人设置非自动同意进群不起作用。如果设置为true，被邀请人设置非自动
  /// 同意其作用，用户可以选择接受邀请进群，也可选择拒绝邀请
  /// @param reason 邀请群成员加入时的邀请信息
  /// @param members 群成员数组,不需要群主id
  Future<String> createGroup({
    int maxUsers = 200,
    String style = GroupStyle.privateOnlyOwnerInvite,
    String extField = '',
    bool inviteNeedConfirm = false,
    @required String groupName,
    String desc = '',
    String reason = '',
    List<String> members = const [],
  }) async {
    final String id = await _channel.invokeMethod('createGroup', {
      'maxUsers': maxUsers,
      'style': style,
      'extField': extField,
      'groupName': groupName,
      'inviteNeedConfirm': inviteNeedConfirm,
      'desc': desc,
      'reason': reason,
      'members': members,
    });
    return id;
  }

  /// 增加群组管理员，需要owner权限，admin無权限
  /// @param groupId the group id
  /// @param admin the user id to be add
  Future<bool> addGroupAdmin({
    @required String groupId,
    @required String admin,
  }) async {
    final bool res = await _channel.invokeMethod('addGroupAdmin', {
      'groupId': groupId,
      'admin': admin,
    });
    return res;
  }

  /// @see addGroupAdmin
  Future<bool> removeGroupAdmin({
    @required String groupId,
    @required String admin,
  }) async {
    final bool res = await _channel.invokeMethod('removeGroupAdmin', {
      'groupId': groupId,
      'admin': admin,
    });
    return res;
  }
}

/// @see enum EMMessage.ChatType
class ChatType {
  static const chat = 'Chat';
  static const groupChat = 'GroupChat';
  static const chatRoom = 'ChatRoom';

  // keep private
  ChatType._internal();
}

/// @see enum EMConversationType
class ConversationType {
  static const chat = 'Chat';
  static const groupChat = 'GroupChat';
  static const chatRoom = 'ChatRoom';
  static const discussionGroup = 'DiscussionGroup';
  static const helpDesk = 'HelpDesk';

  // keep private
  ConversationType._internal();
}

/// @see enum EMMessage.Type
class MessageType {
  static const txt = 'TXT';
  static const image = 'IMAGE';
  static const video = 'VIDEO';
  static const location = 'LOCATION';
  static const voice = 'VOICE';
  static const file = 'FILE';
  static const cmd = 'CMD';

  MessageType._internal();
}

/// @see enum EMGroupManager.EMGroupStyle
class GroupStyle {
  static const privateOnlyOwnerInvite = 'EMGroupStylePrivateOnlyOwnerInvite';
  static const privateMemberCanInvite = 'EMGroupStylePrivateMemberCanInvite';
  static const publicJoinNeedApproval = 'EMGroupStylePublicJoinNeedApproval';
  static const publicOpenJoin = 'EMGroupStylePublicOpenJoin';
}

abstract class EasemobListener {
  void onMessageReceived(List messages) {}

  void onCmdMessageReceived(List messages) {}

  void onMessageRead(List messages) {}

  void onMessageDelivered(List messages) {}

  void onMessageRecalled(List messages) {}

  void onMessageChanged(Map message, String change) {}

  void onContactAdded(String username) {}

  void onContactDeleted(String username) {}

  void onContactInvited(String username, String reason) {}

  void onFriendRequestAccepted(String username) {}

  void onFriendRequestDeclined(String username) {}

  /// 当掉线时，Android SDK 会自动重连，无需进行任何操作，通过注册连接监听来知道连接状态。
  void onConnected() {}
  void onDisconnected(int error) {}
}

mixin EasemobListenerMixin<T extends StatefulWidget> on State<T> implements EasemobListener {
  void onMessageReceived(List messages) {}

  void onCmdMessageReceived(List messages) {}

  void onMessageRead(List messages) {}

  void onMessageDelivered(List messages) {}

  void onMessageRecalled(List messages) {}

  void onMessageChanged(Map message, String change) {}

  void onContactAdded(String username) {}

  void onContactDeleted(String username) {}

  void onContactInvited(String username, String reason) {}

  void onFriendRequestAccepted(String username) {}

  void onFriendRequestDeclined(String username) {}

  /// 当掉线时，Android SDK 会自动重连，无需进行任何操作，通过注册连接监听来知道连接状态。
  void onConnected() {}
  void onDisconnected(int error) {}
}