package com.mobiment.fix

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Format
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.MimeTypes

private const val VIDEO_URL = "https://drive.google.com/uc?export=download&id=1zH7Dse8nllMQwHBGC_aA7okJN3iRPeH4"
private const val SUB1_URL = "https://cdn.jwplayer.com/tracks/fhg4PfUp.srt"

class MainActivity : AppCompatActivity() {

    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val view1 = findViewById<PlayerView>(R.id.player_1)
        val view2 = findViewById<PlayerView>(R.id.player_2)


        val trackSelector = DefaultTrackSelector().apply { parameters = parameters.buildUpon().setPreferredTextLanguage("de").build() }
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        view1.player = null
        view1.player = player

        val sourceFactory = DefaultDataSourceFactory(this, "TesteExoPlayer")
        val video = ExtractorMediaSource.Factory(sourceFactory).createMediaSource(Uri.parse(VIDEO_URL))
        val sub1 = createCaptionsMediaSource(sourceFactory, SUB1_URL, "German", "de")
        player.prepare(MergingMediaSource(video, sub1))

        findViewById<Button>(R.id.btn_toggle_player).setOnClickListener {
            if (view1.player == null) {
                view2.player = null
                view1.player = null
                view1.player = player
            } else if (view2.player == null) {
                view1.player = null
                view2.player = null
                view2.player = player
            }
        }
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    private fun createCaptionsMediaSource(dataSourceFactory: DefaultDataSourceFactory, url: String, label: String, language: String): SingleSampleMediaSource {
        val format = Format.createTextSampleFormat(label, MimeTypes.APPLICATION_SUBRIP, C.SELECTION_FLAG_FORCED, language)
        return SingleSampleMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(url), format, C.TIME_UNSET)
    }

    private fun enableTextRenderer(trackSelector: DefaultTrackSelector, textRendererIndex: Int) {
        trackSelector.run {
            parameters = parameters.buildUpon()
                    .setRendererDisabled(textRendererIndex, false)
                    .build()
        }
    }

    private fun disableTextRenderer(trackSelector: DefaultTrackSelector, textRendererIndex: Int) {
        trackSelector.run {
            parameters = parameters.buildUpon()
                    .setRendererDisabled(textRendererIndex, true)
                    .build()
        }
    }
}
