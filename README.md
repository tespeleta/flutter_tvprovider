# flutter_tvprovider

This plugin exposes the Android TV basic recommendation API.

With this plugin, you can show `PreviewChannel`s and `PreviewProgram`s in the Android TV Home screeen.

See https://developer.android.com/codelabs/tv-recommendations-kotlin for more details.

## API
The API uses external identifiers (your string, custom ids). Note that `TvChannel` and `TvProgram` expose the `id` field,
used intenrally by the Android TV API. This is only for debugging purposes, they are not meant to be used as channelId and programId.

```dart
  Future<bool> upsertTvChannel(TvChannel channel, List<TvProgram> programs, bool clearPrograms)
  Future<List<TvChannel>> getTvChannels()
  Future<List<TvProgram>> getTvPrograms(String channelId)
  Future<bool> removeTvProgram(String programId)
  Future<bool> removeTvChannel(String channelId)
```

## Example
```dart
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
      "test_channel",
      "assets/ic_app_channel.png",
      displayName: "Test Channel",
      appLinkIntentUri: "https://$packageName/channel/$channelId"
  );
  try {
    final ok = await FlutterTvprovider.upsertTvChannel(channel, programs, false);
    print(">> upsertTvChannel OK: $ok");
  } catch (e) {
    print(">> upsertTvChannel KO: $e");
  }
  try {
    final channels = await FlutterTvprovider.getTvChannels();
    print(">> getTvChannels OK: $channels");
  } catch (e) {
    print(">> getTvChannels KO: $e");
  }
  var receivedPrograms = <TvProgram>[];
  try {
    receivedPrograms = await FlutterTvprovider.getTvPrograms(channelId);
    print(">> getTvPrograms OK: $receivedPrograms");
  } catch (e) {
    print(">> getTvPrograms KO: $e");
  }
  if (receivedPrograms.length > 0) {
    try {
      // remove second program
      final ok = await FlutterTvprovider.removeTvProgram(receivedPrograms[1].contentId!);
      print(">> removeTvProgram OK: $ok");
    } catch (e) {
      print(">> removeTvProgram KO: $e");
    }
    try {
      final receivedPrograms = await FlutterTvprovider.getTvPrograms(channelId);
      print(">> After Remove OK: ${receivedPrograms}");
    } catch (e) {
      print(">> After Remove KO: $e");
    }
  }
```
