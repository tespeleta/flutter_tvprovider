import 'dart:typed_data';
import 'package:cbor/cbor.dart' as cbor;

/**
 * Data class representing a channel of playable programs
 */
class TvChannel {
    int? id;
    String internalProviderId;
    String defaultLogoUri;
    String? logoUri;
    String? type;
    String? displayName;
    String? description;
    String? appLinkIntentUri;

    TvChannel(
        this.internalProviderId,
        this.defaultLogoUri,
        {
            this.id,
            this.logoUri,
            this.type,
            this.displayName,
            this.description,
            this.appLinkIntentUri
        }
    );

    factory TvChannel.fromMap(Map data) {
      return TvChannel(
        data['internalProviderId'] as String,
        data['defaultLogoUri'] as String,
        id: data['id'] as int?,
        logoUri: data['logoUri'] as String?,
        type: data['type'] as String?,
        displayName: data['displayName'] as String?,
        description: data['description'] as String?,
        appLinkIntentUri: data['appLinkIntentUri'] as String?,
      );
    }

    Map<String, dynamic> toMap() {
      return <String, dynamic>{
        'id': this.id,
        'internalProviderId': this.internalProviderId,
        'defaultLogoUri': this.defaultLogoUri,
        'logoUri': this.logoUri,
        'type': this.type,
        'displayName': this.displayName,
        'description': this.description,
        'appLinkIntentUri': this.appLinkIntentUri
      };
    }

    String toString() {
      return "${this.id}: ${this.internalProviderId} - [${this.displayName}]";
    }

}