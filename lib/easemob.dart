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
    ///   onFriendRequestAccepted, onFriendRequestDeclined,
    ///   onConnected, onDisconnected, onContactEvent,
    ///   onGroupEvent]
    _eventChannel.receiveBroadcastStream().listen((data) {
      print('ease mob event: $data');
      for (EasemobListener listener in _listeners) {
        switch (data['event']) {
          case 'onMessageReceived':
            List messages = data['data'];
            listener.onEMMessageReceived(
                messages.map((item) => json.encode(item)).toList());
            break;

          case 'onCmdMessageReceived':
            List messages = data['data'];
            listener.onEMCmdMessageReceived(
                messages.map((item) => json.encode(item)).toList());
            break;

          case 'onMessageRead':
            List messages = data['data'];
            listener.onEMMessageRead(
                messages.map((item) => json.encode(item)).toList());
            break;

          case 'onMessageDelivered':
            List messages = data['data'];
            listener.onEMMessageDelivered(
                messages.map((item) => json.encode(item)).toList());
            break;

          case 'onMessageRecalled':
            List messages = data['data'];
            listener.onEMMessageRecalled(
                messages.map((item) => json.encode(item)).toList());
            break;

          case 'onMessageChanged':
            listener.onEMMessageChanged(
                json.decode(data['data']['message']), data['data']['change']);
            break;

          case 'onContactAdded':
            listener.onEMContactAdded(data['data']);
            break;

          case 'onContactDeleted':
            listener.onEMContactDeleted(data['data']);
            break;

          case 'onContactInvited':
            listener.onEMContactInvited(
                data['data']['username'], data['data']['reason']);
            break;

          case 'onFriendRequestAccepted':
            listener.onEMFriendRequestAccepted(data['data']);
            break;

          case 'onFriendRequestDeclined':
            listener.onEMFriendRequestDeclined(data['data']);
            break;

          case 'onConnected':
            listener.onEMConnected();
            break;

          case 'onDisconnected':
            listener.onEMDisconnected(data['data']);
            break;

          case 'onContactEvent':
            listener.onEMMultiDeviceContactEvent(data['data']['event'],
                data['data']['target'], data['data']['ext']);
            break;

          case 'onGroupEvent':
            listener.onEMMultiDeviceGroupEvent(data['data']['event'],
                data['data']['target'], data['data']['userNames']);
            break;

          case 'onGroupInvitationReceived':
            listener.onEMGroupInvitationReceived(
              data['data']['groupId'],
              data['data']['groupName'],
              data['data']['inviter'],
              data['data']['reason'],
            );
            break;

          case 'onGroupRequestToJoinReceived':
            listener.onEMGroupRequestToJoinReceived(
              data['data']['groupId'],
              data['data']['groupName'],
              data['data']['applicant'],
              data['data']['reason'],
            );
            break;

          case 'onGroupRequestToJoinAccepted':
            listener.onEMGroupRequestToJoinAccepted(
              data['data']['groupId'],
              data['data']['groupName'],
              data['data']['acceptor'],
            );
            break;

          case 'onGroupRequestToJoinDeclined':
            listener.onEMGroupRequestToJoinDeclined(
              data['data']['groupId'],
              data['data']['groupName'],
              data['data']['decliner'],
              data['data']['reason'],
            );
            break;

          case 'onGroupInvitationAccepted':
            listener.onEMGroupInvitationAccepted(
              data['data']['groupId'],
              data['data']['invitee'],
              data['data']['reason'],
            );
            break;

          case 'onGroupInvitationDeclined':
            listener.onEMGroupInvitationDeclined(
              data['data']['groupId'],
              data['data']['invitee'],
              data['data']['reason'],
            );
            break;

          // current user has been removed from the group
          case 'onGroupUserRemoved':
            listener.onEMGroupUserRemoved(
                data['data']['groupId'], data['data']['groupName']);
            break;

          case 'onGroupDestroyed':
            listener.onEMGroupDestroyed(
              data['data']['groupId'],
              data['data']['groupName'],
            );
            break;

          case 'onAutoAcceptInvitationFromGroup':
            listener.onEMAutoAcceptInvitationFromGroup(
              data['data']['groupId'],
              data['data']['inviter'],
              data['data']['inviteMessage'],
            );
            break;

          case 'onGroupMuteListRemoved':
            listener.onEMGroupMuteListRemoved(
              data['data']['groupId'],
              data['data']['mutes'].cast(),
            );
            break;

          case 'onGroupAdminAdded':
            listener.onEMGroupAdminAdded(
              data['data']['groupId'],
              data['data']['admin'],
            );
            break;

          case 'onGroupAdminRemoved':
            listener.onEMGroupAdminRemoved(
              data['data']['groupId'],
              data['data']['admin'],
            );
            break;

          case 'onGroupOwnerChanged':
            listener.onEMGroupOwnerChanged(
              data['data']['groupId'],
              data['data']['newOwner'],
              data['data']['oldOwner'],
            );
            break;

          case 'onGroupMemberJoined':
            listener.onEMGroupMemberJoined(
              data['data']['groupId'],
              data['data']['member'],
            );
            break;

          case 'onGroupMemberExited':
            listener.onEMGroupMemberExited(
              data['data']['groupId'],
              data['data']['member'],
            );
            break;

          case 'onGroupAnnouncementChanged':
            listener.onEMGroupAnnouncementChanged(
              data['data']['groupId'],
              data['data']['announcement'],
            );
            break;

          case 'onGroupSharedFileAdded':
            listener.onEMGroupSharedFileAdded(
              data['data']['groupId'],
              json.decode(data['data']['sharedFile']),
            );
            break;

          case 'onGroupSharedFileDeleted':
            listener.onEMGroupSharedFileDeleted(
              data['data']['groupId'],
              data['data']['fileId'],
            );
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
  Future<Map> createGroup({
    int maxUsers = 200,
    String style = GroupStyle.privateOnlyOwnerInvite,
    String extField = '',
    bool inviteNeedConfirm = false,
    @required String groupName,
    String desc = '',
    String reason = '',
    List<String> members = const [],
  }) async {
    final String res = await _channel.invokeMethod('createGroup', {
      'maxUsers': maxUsers,
      'style': style,
      'extField': extField,
      'groupName': groupName,
      'inviteNeedConfirm': inviteNeedConfirm,
      'desc': desc,
      'reason': reason,
      'members': members,
    });
    return json.decode(res);
  }

  /// 增加群组管理员，需要owner权限，admin無权限
  /// @param groupId the group id
  /// @param admin the user id to be add
  Future<Map> addGroupAdmin({
    @required String groupId,
    @required String admin,
  }) async {
    final String res = await _channel.invokeMethod('addGroupAdmin', {
      'groupId': groupId,
      'admin': admin,
    });
    return json.decode(res);
  }

  /// @see addGroupAdmin
  Future<Map> removeGroupAdmin({
    @required String groupId,
    @required String admin,
  }) async {
    final String res = await _channel.invokeMethod('removeGroupAdmin', {
      'groupId': groupId,
      'admin': admin,
    });
    return json.decode(res);
  }

  Future<Map> changeGroupOwner({
    @required String groupId,
    @required String newOwner,
  }) async {
    final String res = await _channel.invokeMethod('changeGroupOwner', {
      'groupId': groupId,
      'newOwner': newOwner,
    });
    return json.decode(res);
  }

  /// 群主加人，或开放了群成员邀请，群成员邀请入群，methodChannel通过判断
  /// 是否是群拥有者，调用不同的方法
  Future<bool> addOrInviteUsersToGroup({
    @required String groupId,
    @required List<String> members,
    String reason,
  }) async {
    final bool res = await _channel.invokeMethod('addOrInviteUsersToGroup', {
      'groupId': groupId,
      'reason': reason,
      'members': members,
    });
    return res;
  }

  /// 群组踢人
  Future<bool> removeUserFromGroup({
    @required String groupId,
    @required String username,
  }) async {
    final bool res = await _channel.invokeMethod('removeUserFromGroup', {
      'groupId': groupId,
      'username': username,
    });
    return res;
  }

  /// 加入某个群组, MethodChannel根据群的属性调用不同的方法
  /// @see http://docs-im.easemob.com/im/android/basics/group#%E5%8A%A0%E5%85%A5%E6%9F%90%E4%B8%AA%E7%BE%A4%E7%BB%84
  Future<bool> joinOrApplyJoinGroup({
    @required String groupId,
    String reason,
  }) async {
    final bool res = await _channel.invokeMethod('joinOrApplyJoinGroup', {
      'groupId': groupId,
      'reason': reason,
    });
    return res;
  }

  /// 退出群组
  Future<bool> leaveGroup(String groupId) async {
    final bool res = await _channel.invokeMethod('leaveGroup', {
      'groupId': groupId,
    });
    return res;
  }

  /// 解散群组
  Future<bool> destroyGroup(String groupId) async {
    final bool res = await _channel.invokeMethod('destroyGroup', {
      'groupId': groupId,
    });
    return res;
  }

  /// @see http://docs-im.easemob.com/im/android/basics/group#%E8%8E%B7%E5%8F%96%E5%AE%8C%E6%95%B4%E7%9A%84%E7%BE%A4%E6%88%90%E5%91%98%E5%88%97%E8%A1%A8
  /// 从服务器获取完整的群成员列表
  /// @param pageSize 每次获取条数
  /// @param cursor 获取下一页的游标，获取第一页传`''`，下一次传返回的`cursor`
  /// @return { hasMore: bool, data: List<String>, cursor: String }
  Future<Map<String, dynamic>> fetchGroupMembers({
    @required String groupId,
    String cursor = '',
    int pageSize = 20,
  }) async {
    final Map res = await _channel.invokeMethod('fetchGroupMembers', {
      'groupId': groupId,
      'pageSize': pageSize,
      'cursor': cursor,
    });
    return res.cast();
  }

  /// @see http://docs-im.easemob.com/im/android/basics/group#%E8%8E%B7%E5%8F%96%E7%BE%A4%E7%BB%84%E5%88%97%E8%A1%A8
  /// 从服务器端获取当前用户的所有群组 （此操作只返回群组列表，并不获取群组的所有成员信息),
  /// 如果要更新某个群组包括成员的全部信息，需要再调用。从第一页开始取。
  /// 此api获取的群组sdk会自动保存到内存和db。
  /// @param fetchAll 是否获取所有记录，为true时忽略其他参数
  /// @param pageIndex 从1开始
  Future<List<Map<String, dynamic>>> getJoinedGroupsFromServer({
    bool fetchAll = false,
    int pageIndex = 1,
    int pageSize = 20,
  }) async {
    final List groups =
        await _channel.invokeMethod('getJoinedGroupsFromServer', {
      'pageIndex': pageIndex,
      'pageSize': pageSize,
      'fetchAll': fetchAll,
    });
    return List<Map<String, dynamic>>.generate(
      groups.length,
      (idx) => json.decode(groups[idx]),
    );
  }

  /// 从本地加载群组列表
  Future<List<Map>> getAllGroups() async {
    final List groups = await _channel.invokeMethod('getAllGroups');
    return groups.cast();
  }

  /// @param cursor 获取下一页的游标，获取第一页传`''`，下一次传返回的`cursor`
  /// @return {hasMore: bool, data: List<Map>, cursor: cursor }
  /// the group is {groupId, groupName},
  Future<List<Map>> getPublicGroupsFromServer(
      {int pageSize, String cursor}) async {
    final List groups =
        await _channel.invokeMethod('getPublicGroupsFromServer', {
      'pageSize': pageSize,
      'cursor': cursor,
    });
    return groups.cast();
  }

  /// 修改群组名称｜描述｜群公告｜群扩展字段
  Future<bool> changeGroup({
    @required groupId,
    String changedGroupName,
    String description,
    String announcement,
    String extension,
  }) async {
    final bool res = await _channel.invokeMethod('changeGroup', {
      'groupId': groupId,
      'changedGroupName': changedGroupName,
      'description': description,
      'announcement': announcement,
      'extension': extension,
    });
    return res;
  }

  /// 群组信息，先尝试在本地获取，若没有找到，尝试在服务器获取
  /// @param fetchMembers 在服务器获取时是否获取群成员
  /// @param tryServerFirst 是否优先在服务器获取
  Future<Map> getGroup({
    @required String groupId,
    bool fetchMembers = true,
    bool tryServerFirst = false,
  }) async {
    final String res = await _channel.invokeMethod('getGroup', {
      'groupId': groupId,
      'fetchMembers': fetchMembers,
      'tryServerFirst': tryServerFirst,
    });
    return json.decode(res);
  }

  /// 屏蔽群消息
  /// 不允许 Owner 权限的调用。
  /// 屏蔽群消息后，就不能接收到此群的消息（还是群里面的成员，但不再接收消息）
  /// @see http://docs-im.easemob.com/im/android/basics/group#%E5%B1%8F%E8%94%BD%E7%BE%A4%E6%B6%88%E6%81%AF
  Future<bool> blockGroupMessage(String groupId) async {
    final bool res = await _channel.invokeMethod('blockGroupMessage', {
      'groupId': groupId,
    });
    return res;
  }

  /// 解除屏蔽群
  Future<bool> unblockGroupMessage(String groupId) async {
    final bool res = await _channel.invokeMethod('unblockGroupMessage', {
      'groupId': groupId,
    });
    return res;
  }

  /// 将用户加到群组的黑名单，被加入黑名单的用户无法加入群，无法收发此群的消息
  /// 只有群主才能设置群的黑名单
  Future<bool> blockGroupUser({
    @required String groupId,
    @required String username,
  }) async {
    final bool res = await _channel.invokeMethod('blockGroupUser', {
      'groupId': groupId,
      'username': username,
    });
    return res;
  }

  /// 将用户移除出群黑名单
  Future<bool> unblockGroupUser({
    @required String groupId,
    @required String username,
  }) async {
    final bool res = await _channel.invokeMethod('unblockGroupUser', {
      'groupId': groupId,
      'username': username,
    });
    return res;
  }

  /// 获取群组的黑名单用户列表
  /// 默认最多取200个成员（只有群主才能调用此函数）
  /// @param pageIndex 从1开始
  Future<List<String>> getGroupBlockedUsers({
    @required groupId,
    int pageIndex = 1,
    int pageSize = 200,
  }) async {
    final List res = await _channel.invokeMethod('getGroupBlockedUsers', {
      'groupId': groupId,
      'pageIndex': pageIndex,
      'pageSize': pageSize,
    });
    return res.cast();
  }

  /// @see http://docs-im.easemob.com/im/android/basics/group#%E7%BE%A4%E7%BB%84%E7%A6%81%E8%A8%80%E6%93%8D%E4%BD%9C
  /// 禁止某些群组成员发言, 需要群组拥有者或者管理员权限
  /// @param duration 禁言的时间，单位是毫秒
  Future<bool> muteGroupMembers({
    @required String groupId,
    @required List<String> members,
    int duration = 12 * 30 * 24 * 60 * 60 * 1000,
  }) async {
    final bool res = await _channel.invokeMethod('muteGroupMembers', {
      'groupId': groupId,
      'members': members,
      'duration': duration,
    });
    return res;
  }

  Future<bool> unMuteGroupMembers({
    @required String groupId,
    @required List<String> members,
  }) async {
    final bool res = await _channel.invokeMethod('unMuteGroupMembers', {
      'groupId': groupId,
      'members': members,
    });
    return res;
  }

  /// 获取群成员禁言列表
  /// @param pageIndex 不确定是否是从1开始??
  /// @return Map<String, int>, key is username, int is mute duration.
  Future<Map<String, int>> fetchGroupMuteList({
    @required String groupId,
    int pageIndex = 1,
    int pageSize = 200,
  }) async {
    final Map res = await _channel.invokeMethod('fetchGroupMuteList', {
      'groupId': groupId,
      'pageSize': pageSize,
      'pageIndex': pageIndex,
    });
    return res.cast();
  }

  /// 获取群公告
  Future<String> fetchGroupAnnouncement(String groupId) async {
    final String res = await _channel.invokeMethod('fetchGroupAnnouncement', {
      'groupId': groupId,
    });
    return res;
  }

  Future<void> uploadGroupSharedFile({
    @required String groupId,
    @required String filePath,
    OnEMUploadSuccess onSuccess,
    OnEMError onError,
    OnEMProgress onProgress,
  }) async {
    String channel = '$_id';
    await _channel.invokeMethod('uploadGroupSharedFile', {
      'groupId': groupId,
      'filePath': filePath,
      'eventChannel': channel,
    }).then((res) {
      EventChannel eventChannel =
          EventChannel('com.newt.easemob/upload_event_channel_$channel');
      eventChannel.receiveBroadcastStream().listen((event) {
        switch (event['event']) {
          case 'onSuccess':
            if (onSuccess != null) {
              onSuccess(json.decode(event['data']));
            }
            break;

          case 'onError':
            if (onError != null) {
              onError(event['data']['code'], event['data']['error']);
            }
            break;

          case 'onProgress':
            if (onProgress != null) {
              onProgress(event['data']['progress'], event['data']['status']);
            }
            break;

          default:
            print('not supported $event');
        }
      });
    }).catchError(print);
  }

  static int _index = 0;
  get _id => _index++;

  /// 删除群共享文件
  Future<bool> deleteGroupSharedFile({
    @required String groupId,
    @required String fileId,
  }) async {
    final bool res = await _channel.invokeMethod('deleteGroupSharedFile', {
      'groupId': groupId,
      'fileId': fileId,
    });
    return res;
  }

  /// 获取群共享文件列表
  /// @param pageIndex ???? start 0 or 1 ????
  Future<List<Map>> fetchGroupSharedFileList(
      {@required String groupId, int pageIndex = 1, pageSize = 200}) async {
    final List res = await _channel.invokeMethod('fetchGroupSharedFileList', {
      'groupId': groupId,
      'pageIndex': pageIndex,
      'pageSize': pageSize,
    });
    return List<Map>.generate(res.length, (idx) => json.decode(res[idx]));
  }

  /// 下载群共享文件
  /// 下载群里的某个共享文件，注意callback只做进度回调用
  Future<void> downloadGroupSharedFile({
    @required String groupId,
    @required String fileId,
    @required String savePath,
    OnEMSuccess onSuccess,
    OnEMError onError,
    OnEMProgress onProgress,
  }) async {
    String channel = '$_id';
    await _channel.invokeMethod('downloadGroupSharedFile', {
      'groupId': groupId,
      'fileId': fileId,
      'savePath': savePath,
      'eventChannel': channel,
    }).then((res) {
      EventChannel eventChannel =
          EventChannel('com.newt.easemob/download_event_channel_$channel');
      eventChannel.receiveBroadcastStream().listen((event) {
        switch (event['event']) {
          case 'onSuccess':
            if (onSuccess != null) {
              onSuccess();
            }
            break;

          case 'onError':
            if (onError != null) {
              onError(event['data']['code'], event['data']['error']);
            }
            break;

          case 'onProgress':
            if (onProgress != null) {
              onProgress(event['data']['progress'], event['data']['status']);
            }
            break;

          default:
            print('not supported $event');
        }
      });
    }).catchError(print);


    //// --- chat room ---


  }
}

typedef void OnEMUploadSuccess(Map file);
typedef void OnEMSuccess();
typedef void OnEMError(int code, String error);
typedef void OnEMProgress(int progress, String status);

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

/// @see java com.hyphenate.EMMultiDeviceListener
abstract class MultiDeviceEvent {
  /// 在其他设备上发起好友请求(没有使用)
  static const int CONTACT_ADD = 1;

  /// 好友已经在其他机子上被移除
  static const int CONTACT_REMOVE = 2;

  ///好友请求已经在其他机子上被同意
  static const int CONTACT_ACCEPT = 3;

  /// 好友请求已经在其他机子上被拒绝
  static const int CONTACT_DECLINE = 4;

  /// 当前用户在其他设备加某人进入黑名单
  static const int CONTACT_BAN = 5;

  /// 好友在其他设备被移出黑名单
  static const int CONTACT_ALLOW = 6;

  ///创建了群组
  static const int GROUP_CREATE = 10;

  /// 销毁了群组
  static const int GROUP_DESTROY = 11;

  /// 已经加入群组
  static const int GROUP_JOIN = 12;

  /// 已经离开群组
  static const int GROUP_LEAVE = 13;

  /// 发起群组申请
  static const int GROUP_APPLY = 14;

  /// 同意群组申请
  static const int GROUP_APPLY_ACCEPT = 15;

  /// 拒绝群组申请
  static const int GROUP_APPLY_DECLINE = 16;

  /// 邀请群成员
  static const int GROUP_INVITE = 17;

  /// 同意群组邀请
  static const int GROUP_INVITE_ACCEPT = 18;

  /// 拒绝群组邀请
  static const int GROUP_INVITE_DECLINE = 19;

  /// 将某人踢出群
  static const int GROUP_KICK = 20;

  /// 加入群组黑名单
  static const int GROUP_BAN = 21; //加入群组黑名单

  /// 移除群组黑名单
  static const int GROUP_ALLOW = 22;

  /// 屏蔽群组
  static const int GROUP_BLOCK = 23;

  /// 取消群组屏蔽
  static const int GROUP_UNBLOCK = 24;

  /// 转移群主
  static const int GROUP_ASSIGN_OWNER = 25;

  /// 添加管理员
  static const int GROUP_ADD_ADMIN = 26;

  /// 移除管理员
  static const int GROUP_REMOVE_ADMIN = 27;

  /// 禁言用户
  static const int GROUP_ADD_MUTE = 28;

  /// 移除禁言
  static const int GROUP_REMOVE_MUTE = 29;
}

abstract class EasemobListener {
  void onEMMessageReceived(List messages) {}

  void onEMCmdMessageReceived(List messages) {}

  void onEMMessageRead(List messages) {}

  void onEMMessageDelivered(List messages) {}

  void onEMMessageRecalled(List messages) {}

  void onEMMessageChanged(Map message, String change) {}

  void onEMContactAdded(String username) {}

  void onEMContactDeleted(String username) {}

  void onEMContactInvited(String username, String reason) {}

  void onEMFriendRequestAccepted(String username) {}

  void onEMFriendRequestDeclined(String username) {}

  /// 当掉线时，Android SDK 会自动重连，无需进行任何操作，通过注册连接监听来知道连接状态。
  void onEMConnected() {}
  void onEMDisconnected(int error) {}

  /// EMMultiDeviceListener
  void onEMMultiDeviceContactEvent(int event, String target, String ext) {}
  void onEMMultiDeviceGroupEvent(int event, String target, List userNames) {}

  /// 群组事件
  void onEMGroupInvitationReceived(
      String groupId, String groupName, String inviter, String reason) {}
  void onEMGroupRequestToJoinReceived(
      String groupId, String groupName, String applicant, String reason) {}
  void onEMGroupRequestToJoinAccepted(
      String groupId, String groupName, String acceptor) {}
  void onEMGroupRequestToJoinDeclined(
      String groupId, String groupName, String decliner, String reason) {}
  void onEMGroupInvitationAccepted(
      String groupId, String invitee, String reason) {}
  void onEMGroupInvitationDeclined(
      String groupId, String invitee, String reason) {}

  /// current user has been removed from the group
  void onEMGroupUserRemoved(String groupId, String groupName) {}
  void onEMGroupDestroyed(String groupId, String groupName) {}
  void onEMAutoAcceptInvitationFromGroup(
      String groupId, String inviter, String inviteMessage) {}
  void onEMGroupMuteListAdded(
      String groupId, List<String> mutes, int muteExpire) {}
  void onEMGroupMuteListRemoved(String groupId, List<String> mutes) {}
  void onEMGroupAdminAdded(String groupId, String administrator) {}
  void onEMGroupAdminRemoved(String groupId, String administrator) {}
  void onEMGroupOwnerChanged(
      String groupId, String newOwner, String oldOwner) {}
  void onEMGroupMemberJoined(String groupId, String member) {}
  void onEMGroupMemberExited(String groupId, String member) {}
  void onEMGroupAnnouncementChanged(String groupId, String announcement);
  void onEMGroupSharedFileAdded(String groupId, Map sharedFile) {}
  void onEMGroupSharedFileDeleted(String groupId, String fileId) {}
}

mixin EasemobListenerMixin<T extends StatefulWidget> on State<T>
    implements EasemobListener {
  void onEMMessageReceived(List messages) {}

  void onEMCmdMessageReceived(List messages) {}

  void onEMMessageRead(List messages) {}

  void onEMMessageDelivered(List messages) {}

  void onEMMessageRecalled(List messages) {}

  void onEMMessageChanged(Map message, String change) {}

  void onEMContactAdded(String username) {}

  void onEMContactDeleted(String username) {}

  void onEMContactInvited(String username, String reason) {}

  void onEMFriendRequestAccepted(String username) {}

  void onEMFriendRequestDeclined(String username) {}

  /// 当掉线时，Android SDK 会自动重连，无需进行任何操作，通过注册连接监听来知道连接状态。
  void onEMConnected() {}
  void onEMDisconnected(int error) {}

  /// EMMultiDeviceListener
  void onEMMultiDeviceContactEvent(int event, String target, String ext) {}
  void onEMMultiDeviceGroupEvent(int event, String target, List userNames) {}

  /// 群组事件
  void onEMGroupInvitationReceived(
      String groupId, String groupName, String inviter, String reason) {}
  void onEMGroupRequestToJoinReceived(
      String groupId, String groupName, String applicant, String reason) {}
  void onEMGroupRequestToJoinAccepted(
      String groupId, String groupName, String acceptor) {}
  void onEMGroupRequestToJoinDeclined(
      String groupId, String groupName, String decliner, String reason) {}
  void onEMGroupInvitationAccepted(
      String groupId, String invitee, String reason) {}
  void onEMGroupInvitationDeclined(
      String groupId, String invitee, String reason) {}

  /// current user has been removed from the group
  void onEMGroupUserRemoved(String groupId, String groupName) {}
  void onEMGroupDestroyed(String groupId, String groupName) {}
  void onEMAutoAcceptInvitationFromGroup(
      String groupId, String inviter, String inviteMessage) {}
  void onEMGroupMuteListAdded(
      String groupId, List<String> mutes, int muteExpire) {}
  void onEMGroupMuteListRemoved(String groupId, List<String> mutes) {}
  void onEMGroupAdminAdded(String groupId, String administrator) {}
  void onEMGroupAdminRemoved(String groupId, String administrator) {}
  void onEMGroupOwnerChanged(
      String groupId, String newOwner, String oldOwner) {}
  void onEMGroupMemberJoined(String groupId, String member) {}
  void onEMGroupMemberExited(String groupId, String member) {}
  void onEMGroupAnnouncementChanged(String groupId, String announcement);
  void onEMGroupSharedFileAdded(String groupId, Map sharedFile) {}
  void onEMGroupSharedFileDeleted(String groupId, String fileId) {}
}
