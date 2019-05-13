import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:easemob/easemob.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  Easemob easemob = Easemob();

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await Easemob.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: ListView(
          children: <Widget>[
            FlatButton(
              child: Text('Login'),
              onPressed: () async {
                var res = await easemob.login('14787820611', '123456');
                print('login: $res');
              },
            ),
            SizedBox(height: 12),
            FlatButton(
              child: Text('Logout'),
              onPressed: () async {
                var res = await easemob.logout();
                print('logout: $res');
              },
            ),

            SizedBox(height: 12),
            FlatButton(
              child: Text('getAllContactsFromServer'),
              onPressed: () async {
                var res = await easemob.getAllContactsFromServer();
                print('getAllContactsFromServer: $res');
              },
            ),
            SizedBox(height: 12),
            FlatButton(
              child: Text('getAllConversations'),
              onPressed: () async {
                var res = await easemob.getAllConversations();
                print('getAllConversations: $res');
              },
            ),
            SizedBox(height: 12),
            FlatButton(
              child: Text('loadMoreMsgFromDB'),
              onPressed: () async {
                var res = await easemob.loadMoreMsgFromDB(conversationId: 'admin');
                print('loadMoreMsgFromDB: $res');
              },
            ),
            SizedBox(height: 12),
            FlatButton(
              child: Text('sendMessage'),
              onPressed: () async {
                var res = await easemob.sendMessage(
                  to: 'admin',
                  content: 'Good',
                  attributes: {
                    'attribute1': true,
                    'attribute2': 1,
                    'attribute3': 'string',
                    'attribute4': { 'a': 'a' },
                    'attribute5': [1,2,3],
                  }
                );
                print('sendMessage: $res');
              },
            ),
          ],
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: () async {
            print("init: ${await easemob.init('1126190510019557#imnewt')}");
          },
          child: Icon(Icons.access_alarm),
        ),
      ),
    );
  }
}
