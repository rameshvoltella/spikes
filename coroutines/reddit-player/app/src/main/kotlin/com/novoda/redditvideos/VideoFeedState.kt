package com.novoda.redditvideos

import com.novoda.redditvideos.model.Video
import java.lang.Exception

sealed class VideoFeedState {

    object Loading : VideoFeedState()
    data class Idle(override val videos: List<Video>) : VideoFeedState(), HasVideos
    data class Failure(val message: String, val exception: Exception?) : VideoFeedState()

    interface HasVideos {

        val videos: List<Video>

    }

}
