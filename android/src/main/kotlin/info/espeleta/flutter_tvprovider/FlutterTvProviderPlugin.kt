package info.espeleta.flutter_tvprovider

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.NonNull
import info.espeleta.flutter_tvprovider.model.TvChannel
import info.espeleta.flutter_tvprovider.model.TvProgram
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.*
import kotlinx.serialization.cbor.Cbor


@ExperimentalSerializationApi
class FlutterTvProviderPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {

  companion object {
    private val TAG = FlutterTvProviderPlugin::class.java.simpleName
  }

  private lateinit var context: Context
  private lateinit var activity: Activity

  /// The MethodChannel that will the communication between Flutter and native Android
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var methodChannel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_tvprovider")
    methodChannel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    methodChannel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity;
  }

  override fun onDetachedFromActivityForConfigChanges() {
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
  }

  override fun onDetachedFromActivity() {
  }

  @Serializable
  data class upsertTvChannelArgs(
    val channel: TvChannel,
    val programs: List<TvProgram>,
    val clearPrograms: Boolean = false
  )

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    try {
      when (call.method) {
        "getPlatformVersion" -> {
          result.success("Android ${android.os.Build.VERSION.RELEASE}")
        }
        "upsertTvChannel" -> {
          val (channel, programs, clearPrograms) = argumentAsUpsertTvChannelArgs(call.arguments())
          Log.i(TAG, "Adding channel $channel")
          result.success(encodeResult(FlutterTvProviderUtils.upsertChannel(context, channel, programs, clearPrograms)))
        }
        "getTvChannels" -> {
          Log.i(TAG, "Getting all channels")
          result.success(encodeChannels(FlutterTvProviderUtils.getTvChannels(context)))
        }
        "getTvPrograms" -> {
          val channelId = argumentAsString(call.arguments())
          Log.i(TAG, "Getting programs from channel $channelId")
          result.success(encodePrograms(FlutterTvProviderUtils.getTvPrograms(context, channelId)))
        }
        "removeTvProgram" -> {
          val programId = argumentAsString(call.arguments())
          Log.i(TAG, "Removing program $programId")
          result.success(encodeResult(FlutterTvProviderUtils.removeTvProgram(context, programId)))
        }
        "removeTvChannel" -> {
          val channelId = argumentAsString(call.arguments())
          Log.i(TAG, "Removing channel $channelId")
          result.success(encodeResult(FlutterTvProviderUtils.removeTvChannel(context, channelId)))
        }
        "getWatchNextPrograms" -> {
          Log.i(TAG, "Getting watch next programs")
          result.success(encodePrograms(FlutterTvProviderUtils.getWatchNextTvPrograms(context)))
        }
        "upsertWatchNext" -> {
          val program = argumentAsTvProgram(call.arguments())
          result.success(encodeResult(FlutterTvProviderUtils.upsertWatchNext(context, program)))
        }
        "removeFromWatchNext" -> {
          val channelId = argumentAsString(call.arguments())
          result.success(encodeResult(FlutterTvProviderUtils.removeFromWatchNext(context, channelId)))
        }
        else -> {
          result.notImplemented()
        }
      }
    } catch (e: Exception) {
      result.error("error", e.message, null)
    }
  }

  private fun argumentAsString(arguments: ByteArray): String? {
    return Cbor.decodeFromByteArray(String.serializer().nullable, arguments)
  }

  private fun argumentAsUpsertTvChannelArgs(arguments: ByteArray): upsertTvChannelArgs {
    return Cbor.decodeFromByteArray(upsertTvChannelArgs.serializer(), arguments)
  }

  private fun argumentAsTvProgram(arguments: ByteArray): TvProgram {
    return Cbor.decodeFromByteArray(TvProgram.serializer(), arguments)
  }

  private fun encodeResult(ok: Boolean): ByteArray {
    return Cbor.encodeToByteArray(Boolean.serializer().nullable, ok)
  }

  private fun encodePrograms(programs: List<TvProgram>): ByteArray {
    return Cbor.encodeToByteArray(ListSerializer(TvProgram.serializer()), programs)
  }

  private fun encodeChannels(channels: List<TvChannel>): ByteArray {
    return Cbor.encodeToByteArray(ListSerializer(TvChannel.serializer()), channels)
  }

}
