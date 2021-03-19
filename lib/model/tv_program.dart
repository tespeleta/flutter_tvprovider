import 'dart:core';

/**
 * Data class representing a piece of content metadata
 * (title, content URI, state-related fields [playback position], etc.)
 */
class TvProgram {
    int? id;
    int? channelId;
    List<String>? audioLanguages;
    String? author;
    int? availability;
    List<String>? canonicalGenres;
    String? contentId;
    String? description;
    int? durationMillis;
    int? endTimeUtcMillis;
    int? episodeNumber;
    String? episodeTitle;
    String? genre;
    String? intentUri;
    int? interactionCount;
    int? interactionType;
    String? internalProviderId;
    int? internalProviderFlag1;
    int? internalProviderFlag2;
    int? internalProviderFlag3;
    int? internalProviderFlag4;
    int? itemCount;
    int? lastEngagementTimeUtcMillis;
    int? lastPlaybackPositionMillis;
    bool? live;
    String? logoContentDescription;
    String? logoUri;
    String? longDescription;
    String? offerPrice;
    String? packageName;
    int? posterArtAspectRatio;
    String? posterArtUri;
    String? previewAudioUri;
    String? previewVideoUri;
    bool? projection;
    String? releaseDate;
    String? reviewRating;
    int? reviewRatingStyle;
    bool? searchable;
    String? seasonNumber;
    String? seasonTitle;
    String? startingPrice;
    int? startTimeUtcMillis;
    int? thumbnailAspectRatio;
    String? thumbnailUri;
    String? title;
    bool? transient;
    int? tvSeriesItemType;
    int type;
    int? videoHeight;
    int? videoWidth;
    int? watchNextType;
    int? weight;

    TvProgram(
        {
            this.id,
            this.channelId,
            this.audioLanguages,
            this.author,
            this.availability,
            this.canonicalGenres,
            this.contentId,
            this.description,
            this.durationMillis,
            this.endTimeUtcMillis,
            this.episodeNumber,
            this.episodeTitle,
            this.genre,
            this.intentUri,
            this.interactionCount,
            this.interactionType,
            this.internalProviderId,
            this.internalProviderFlag1,
            this.internalProviderFlag2,
            this.internalProviderFlag3,
            this.internalProviderFlag4,
            this.itemCount,
            this.lastEngagementTimeUtcMillis,
            this.lastPlaybackPositionMillis,
            this.live,
            this.logoContentDescription,
            this.logoUri,
            this.longDescription,
            this.offerPrice,
            this.packageName,
            this.posterArtAspectRatio = TvProgramAspectRatio.ASPECT_RATIO_MOVIE_POSTER,
            this.posterArtUri,
            this.previewAudioUri,
            this.previewVideoUri,
            this.projection,
            this.releaseDate,
            this.reviewRating,
            this.reviewRatingStyle,
            this.searchable,
            this.seasonNumber,
            this.seasonTitle,
            this.startingPrice,
            this.startTimeUtcMillis,
            this.thumbnailAspectRatio,
            this.thumbnailUri,
            this.title,
            this.transient,
            this.tvSeriesItemType,
            this.type = TvProgramType.TYPE_MOVIE,
            this.videoHeight,
            this.videoWidth,
            this.watchNextType,
            this.weight
        }
    );

    factory TvProgram.fromMap(Map data) {
      return TvProgram(
          id: data['id'] as int?,
          channelId: data['channelId'] as int?,
          audioLanguages: data['audioLanguages']?.cast<String>(),
          author: data['author'] as String?,
          availability: data['availability'] as int?,
          canonicalGenres: data['canonicalGenres']?.cast<String>(),
          contentId: data['contentId'] as String?,
          description: data['description'] as String?,
          durationMillis: data['durationMillis'] as int?,
          endTimeUtcMillis: data['endTimeUtcMillis'] as int?,
          episodeNumber: data['episodeNumber'] as int?,
          episodeTitle: data['episodeTitle'] as String?,
          genre: data['genre'] as String?,
          intentUri: data['intentUri'] as String?,
          interactionCount: data['interactionCount'] as int?,
          interactionType: data['interactionType'] as int?,
          internalProviderId: data['internalProviderId'] as String?,
          internalProviderFlag1: data['internalProviderFlag1'] as int?,
          internalProviderFlag2: data['internalProviderFlag2'] as int?,
          internalProviderFlag3: data['internalProviderFlag3'] as int?,
          internalProviderFlag4: data['internalProviderFlag4'] as int?,
          itemCount: data['itemCount'] as int?,
          lastEngagementTimeUtcMillis: data['lastEngagementTimeUtcMillis'] as int?,
          lastPlaybackPositionMillis: data['lastPlaybackPositionMillis'] as int?,
          live: data['live'] as bool?,
          logoContentDescription: data['logoContentDescription'] as String?,
          logoUri: data['logoUri'] as String?,
          longDescription: data['longDescription'] as String?,
          offerPrice: data['offerPrice'] as String?,
          packageName: data['packageName'] as String?,
          posterArtAspectRatio: data['posterArtAspectRatio'] as int?,
          posterArtUri: data['posterArtUri'] as String?,
          previewAudioUri: data['previewAudioUri'] as String?,
          previewVideoUri: data['previewVideoUri'] as String?,
          projection: data['projection'] as bool?,
          releaseDate: data['releaseDate'] as String?,
          reviewRating: data['reviewRating'] as String?,
          reviewRatingStyle: data['reviewRatingStyle'] as int?,
          searchable: data['searchable'] as bool?,
          seasonNumber: data['seasonNumber'] as String?,
          seasonTitle: data['seasonTitle'] as String?,
          startingPrice: data['startingPrice'] as String?,
          startTimeUtcMillis: data['startTimeUtcMillis'] as int?,
          thumbnailAspectRatio: data['thumbnailAspectRatio'] as int?,
          thumbnailUri: data['thumbnailUri'] as String?,
          title: data['title'] as String?,
          transient: data['transient'] as bool?,
          tvSeriesItemType: data['tvSeriesItemType'] as int?,
          type: data['type'] as int,
          videoHeight: data['videoHeight'] as int?,
          videoWidth: data['videoWidth'] as int?,
          watchNextType: data['watchNextType'] as int?,
          weight: data['weight'] as int?
      );
    }

    Map<String, dynamic> toMap() {
      return <String, dynamic>{
        'id': this.id,
        'audioLanguages': this.audioLanguages,
        'author': this.author,
        'availability': this.availability,
        'canonicalGenres': this.canonicalGenres,
        'contentId': this.contentId,
        'description': this.description,
        'durationMillis': this.durationMillis,
        'endTimeUtcMillis': this.endTimeUtcMillis,
        'episodeNumber': this.episodeNumber,
        'episodeTitle': this.episodeTitle,
        'genre': this.genre,
        'intentUri': this.intentUri,
        'interactionCount': this.interactionCount,
        'interactionType': this.interactionType,
        'internalProviderId': this.internalProviderId,
        'internalProviderFlag1': this.internalProviderFlag1,
        'internalProviderFlag2': this.internalProviderFlag2,
        'internalProviderFlag3': this.internalProviderFlag3,
        'internalProviderFlag4': this.internalProviderFlag4,
        'itemCount': this.itemCount,
        'lastEngagementTimeUtcMillis': this.lastEngagementTimeUtcMillis,
        'lastPlaybackPositionMillis': this.lastPlaybackPositionMillis,
        'live': this.live,
        'logoContentDescription': this.logoContentDescription,
        'logoUri': this.logoUri,
        'longDescription': this.longDescription,
        'offerPrice': this.offerPrice,
        'packageName': this.packageName,
        'posterArtAspectRatio': this.posterArtAspectRatio,
        'posterArtUri': this.posterArtUri,
        'previewAudioUri': this.previewAudioUri,
        'previewVideoUri': this.previewVideoUri,
        'projection': this.projection,
        'releaseDate': this.releaseDate,
        'reviewRating': this.reviewRating,
        'reviewRatingStyle': this.reviewRatingStyle,
        'searchable': this.searchable,
        'seasonNumber': this.seasonNumber,
        'seasonTitle': this.seasonTitle,
        'startingPrice': this.startingPrice,
        'startTimeUtcMillis': this.startTimeUtcMillis,
        'thumbnailAspectRatio': this.thumbnailAspectRatio,
        'thumbnailUri': this.thumbnailUri,
        'title': this.title,
        'transient': this.transient,
        'tvSeriesItemType': this.tvSeriesItemType,
        'type': this.type,
        'videoHeight': this.videoHeight,
        'videoWidth': this.videoWidth,
        'watchNextType': this.watchNextType,
        'weight': this.weight
      };
    }

    String toString() {
      return "${this.id}: ${this.contentId} - [${this.title}]";
    }

}

class TvProgramType {
  // The program type for movie.
  static const TYPE_MOVIE = 0;
  // The program type for TV series.
  static const TYPE_TV_SERIES = 1;
  // The program type for TV season.
  static const TYPE_TV_SEASON = 2;
  // The program type for TV episode.
  static const TYPE_TV_EPISODE = 3;
  // The program type for clip.
  static const TYPE_CLIP = 4;
  // The program type for event.
  static const TYPE_EVENT = 5;
  // The program type for channel.
  static const TYPE_CHANNEL = 6;
  // The program type for track.
  static const TYPE_TRACK = 7;
  // The program type for album.
  static const TYPE_ALBUM = 8;
  // The program type for artist.
  static const TYPE_ARTIST = 9;
  // The program type for playlist.
  static const TYPE_PLAYLIST = 10;
  // The program type for station.
  static const TYPE_STATION = 11;
  // The program type for game.
  static const TYPE_GAME = 12;
}

class TvProgramAspectRatio {
  // The aspect ratio for 16:9.
  static const ASPECT_RATIO_16_9 = 0;
  // The aspect ratio for 3:2.
  static const ASPECT_RATIO_3_2 = 1;
  // The aspect ratio for 4:3.
  static const ASPECT_RATIO_4_3 = 2;
  //The aspect ratio for 1:1.
  static const ASPECT_RATIO_1_1 = 3;
  // The aspect ratio for 2:3.
  static const ASPECT_RATIO_2_3 = 4;
  // The aspect ratio for movie poster which is 1:1.441.
  static const ASPECT_RATIO_MOVIE_POSTER = 5;
}