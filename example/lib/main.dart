import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:flutter_tvprovider/flutter_tvprovider.dart';
import 'package:flutter_tvprovider/model/tv_channel.dart';
import 'package:flutter_tvprovider/model/tv_program.dart';
import 'package:package_info/package_info.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _results = 'Working...';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    var results = <String>[];

    // Create or Update channel
    PackageInfo packageInfo = await PackageInfo.fromPlatform();
    final packageName = packageInfo.packageName;
    final channelId = "favourites";
    final programs = [
      TvProgram(
          contentId: "le_voyage_dans_la_lun",
          releaseDate: "1902",
          durationMillis: 780,
          title: "Le Voyage Dans la Lun",
          author: "Georges Méliès",
          description: "A group of astronomers go on an expedition to the Moon.",
          intentUri: "https://$packageName/program/le_voyage_dans_la_lun",
          posterArtUri: "https://android-tv-classics.firebaseapp.com/content/le_voyage_dans_la_lun/poster_art_le_voyage_dans_la_lun.jpg"
      ),
      TvProgram(
          contentId: "robinson_crusoe",
          releaseDate: "1903",
          durationMillis: 770,
          title: "Les aventures de Robinson Crusoé",
          author: "Georges Méliès",
          description: "Robinson Crusoe and Friday fight with hostile natives, and eventually retire to their jungle cottage to relax.",
          intentUri: "https://$packageName/program/robinson_crusoe",
          posterArtUri: "https://android-tv-classics.firebaseapp.com/content/robinson_crusoe/poster_art_robinson_crusoe.jpg"
      ),
      TvProgram(
          contentId: "alice_wonderland",
          releaseDate: "1903",
          durationMillis: 600,
          title: "Alice in Wonderland",
          author: "Cecil Hepworth",
          description: "Alice dozes in a garden, awakened by a dithering white rabbit in waistcoat with pocket watch. She follows him down a hole.",
          intentUri: "https://$packageName/program/alice_wonderland",
          posterArtUri: "https://android-tv-classics.firebaseapp.com/content/alice_wonderland/poster_art_alice_wonderland.jpg"
      ),
    ];

    final channel = TvChannel(
        channelId,
        "assets/ic_app_channel.png",
        displayName: "Test Channel",
        appLinkIntentUri: "https://$packageName/channel/$channelId"
    );
    try {
      final ok = await FlutterTvprovider.upsertTvChannel(channel, programs, false);
      results.add(">> upsertTvChannel OK: $ok");
    } catch (e) {
      results.add(">> upsertTvChannel KO: $e");
    }
    try {
      final channels = await FlutterTvprovider.getTvChannels();
      results.add(">> getTvChannels OK: $channels");
    } catch (e) {
      results.add(">> getTvChannels KO: $e");
    }
    var receivedPrograms = <TvProgram>[];
    try {
      receivedPrograms = await FlutterTvprovider.getTvPrograms(channelId);
      results.add(">> getTvPrograms OK: $receivedPrograms");
    } catch (e) {
      results.add(">> getTvPrograms KO: $e");
    }
    if (receivedPrograms.length > 0) {
      try {
        // remove second program
        final ok = await FlutterTvprovider.removeTvProgram(receivedPrograms[1].contentId!);
        results.add(">> removeTvProgram OK: $ok");
      } catch (e) {
        results.add(">> removeTvProgram KO: $e");
      }
      try {
        final receivedPrograms = await FlutterTvprovider.getTvPrograms(channelId);
        results.add(">> After Remove OK: ${receivedPrograms}");
      } catch (e) {
        results.add(">> After Remove KO: $e");
      }
    }
    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _results = results.join("\n");
      print("TEST RESULT: $_results");
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text('$_results\n'),
            ],
          ),
        ),
      ),
    );
  }
}
