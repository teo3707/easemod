import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:meta/meta.dart';

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

  List<Listener> _listeners = [];

  Easemob._intern() {
    /// handle events:
    /// [ onMessageReceived, onCmdMessageReceived, onMessageRead,
    ///   onMessageDelivered, onMessageRecalled, onMessageChanged,
    ///   onContactAdded, onContactDeleted,onContactInvited,
    ///   onFriendRequestAccepted, onFriendRequestDeclined ]
    _eventChannel.receiveBroadcastStream().listen((data) {
      for (Listener listener in _listeners) {
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

  void addListener(Listener listener) {
    if (!_listeners.contains(listener)) {
      _listeners.add(listener);
    }
  }

  void removeListener(Listener listener) {
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

  /// return the message id
  Future<String> sendMessage({
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
    return res;
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
  /// if `startMsgId` not supply, return all messages. otherwise
  //  return `pageSize` messages.
  Future<List<Map>> loadMoreMsgFromDB({
    @required String conversationId,
    String startMsgId,
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
    String startMsgId = '', // the start search roam message, if empty start from the server last message
  }) async {
    final List messages = await _channel.invokeMethod('loadMoreRoamingMessages', {
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

class Listener {
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
}
