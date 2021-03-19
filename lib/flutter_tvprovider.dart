
import 'dart:async';
import 'dart:typed_data';
import 'package:cbor/cbor.dart' as cbor;
import 'package:flutter/services.dart';
import 'package:flutter_tvprovider/model/tv_channel.dart';

import 'model/tv_program.dart';

class FlutterTvprovider {
  static const MethodChannel _channel = const MethodChannel('flutter_tvprovider');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> upsertTvChannel(TvChannel channel, List<TvProgram> programs, bool clearPrograms) async {
    final params = _map2Cbor(<String, Object>{
      'channel': channel.toMap(),
      'programs': programs.map((program) => program.toMap()).toList(),
      'clearPrograms': clearPrograms
    });
    final res = await _invokeMethod('upsertTvChannel', params);
    final inst = cbor.Cbor();
    inst.decodeFromList(res);
    final data = inst.getDecodedData();
    if (data != null && data.length > 0) {
      print("upsertTvChannel RECEIVED: ${data[0]}");
      return data[0];
    }
    return false;
  }

  static Future<List<TvChannel>> getTvChannels() async {
    final res = await _channel.invokeMethod('getTvChannels');
    final inst = cbor.Cbor();
    inst.decodeFromList(res);
    final data = inst.getDecodedData();
    if (data != null && data.length > 0) {
      print("getTvChannels RECEIVED: ${data[0]}");
      return data[0].map<TvChannel>((channelData) => TvChannel.fromMap(channelData)).toList();
    }
    return [];
  }

  static Future<List<TvProgram>> getTvPrograms(String channelId) async {
    final res = await _invokeMethod('getTvPrograms', _string2Cbor(channelId));
    final inst = cbor.Cbor();
    inst.decodeFromList(res);
    final data = inst.getDecodedData();
    if (data != null && data.length > 0) {
      print("getTvPrograms RECEIVED: ${data[0]}");
      return data[0].map<TvProgram>((programData) => TvProgram.fromMap(programData)).toList();
    }
    return [];
  }

  static Future<bool> removeTvProgram(String programId) async {
    final res = await _invokeMethod('removeTvProgram', _string2Cbor(programId));
    final inst = cbor.Cbor();
    inst.decodeFromList(res);
    final data = inst.getDecodedData();
    if (data != null && data.length > 0) {
      print("removeTvProgram RECEIVED: ${data[0]}");
      return data[0];
    }
    return false;
  }

  static Future<bool> removeTvChannel(String channelId) async {
    final res = await _invokeMethod('removeTvChannel', _string2Cbor(channelId));
    final inst = cbor.Cbor();
    inst.decodeFromList(res);
    final data = inst.getDecodedData();
    if (data != null && data.length > 0) {
      print("removeTvChannel RECEIVED: ${data[0]}");
      return data[0];
    }
    return false;
  }

  // private helpers
  static Future<T?> _invokeMethod<T>(String method, Uint8List args) {
    return _channel.invokeMethod(method, args);
  }

  static Uint8List _map2Cbor(Map<String, Object> map) {
    final inst = cbor.Cbor();
    final encoder = inst.encoder;
    encoder.writeMap(map);
    final buff = inst.output.getData();
    return Uint8List.view(buff.buffer, 0, buff.length);
  }

  static Uint8List _string2Cbor(String s) {
    final inst = cbor.Cbor();
    final encoder = inst.encoder;
    encoder.writeString(s);
    final buff = inst.output.getData();
    return Uint8List.view(buff.buffer, 0, buff.length);
  }

}
