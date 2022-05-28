package com.example.utsavlive

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.example.utsavlive.databinding.ActivityLiveBinding
import io.agora.rtc.Constants
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.video.VideoCanvas
import java.lang.Exception

class LiveActivity : AppCompatActivity() {
    lateinit var binding: ActivityLiveBinding
    private var userRole=""
    private var mRtcEngine:RtcEngine?=null
    private var channelName:String?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding=DataBindingUtil. setContentView(this,R.layout.activity_live)
        userRole=intent.getStringExtra("userRole").toString()
        channelName=intent.getStringExtra("channelName")
        Toast.makeText(this, channelName, Toast.LENGTH_SHORT).show()
        initAgoraEgineAndChannel()


        with(binding){
            mute.setOnClickListener {
                if (mute.isSelected){
                    mute.isSelected=false
                    mute.clearColorFilter()
                }
                else{
                    mute.isSelected=true
                    mute.setColorFilter(resources.getColor(R.color.purple_200),PorterDuff.Mode.MULTIPLY)
                }
                mRtcEngine?.muteLocalAudioStream(mute.isSelected)

            }
            switchCamera.setOnClickListener {
                mRtcEngine?.switchCamera()
            }
            endCall.setOnClickListener {
                finish()
            }


        }






    }

    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
        mRtcEngine=null
    }

    private fun initAgoraEgineAndChannel() {
        Log.d("n/k","initAgoraEgineAndChannel")
        initializeAgoraEgngine()
        mRtcEngine?.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING)
        mRtcEngine?.enableVideo()

        if (userRole=="1"){
            setUpLocalVideo()
        }
        else{
            binding.localVideoViewContainer.isVisible=false
        }
        joinChannel()
    }

    private fun joinChannel() {
        Log.d("n/k","joinChannel")
        Log.d("n/k", token)
        mRtcEngine?.joinChannel(token,channelName,null,0)
    }

    private fun setUpLocalVideo() {
        Log.d("n/k","setUpLocalVideo")
        val surfaceView=RtcEngine.CreateRendererView(baseContext)
        surfaceView.setZOrderMediaOverlay(true)
        binding.localVideoViewContainer.addView(surfaceView)
        mRtcEngine?.setupLocalVideo(VideoCanvas(surfaceView,VideoCanvas.RENDER_MODE_FIT,0))
    }


    private fun initializeAgoraEgngine() {

        try {
            Log.d("n/k","initializeAgoraEgngine")
            Log.d("n/k", App_Id)
            mRtcEngine= RtcEngine.create(baseContext, App_Id,mRtcEventHandler)

        }
        catch (e: Exception){
            println("Exception: ${e.message}")
        }
    }
    private val mRtcEventHandler:IRtcEngineEventHandler=object : IRtcEngineEventHandler(){

        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            runOnUiThread{setRemoteVedio(uid)}
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
            runOnUiThread { onRemoteUserLeft() }
        }

        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            runOnUiThread { println("join Channel Succes $uid") }
        }

    }

    private fun setRemoteVedio(uid: Int) {
        Log.d("n/k","setRemoteVedio")
    if (binding.remoteVideoViewContainer.childCount>=1){
        return
    }
        val surfaceViews=RtcEngine.CreateRendererView(baseContext)
        binding.remoteVideoViewContainer.addView(surfaceViews)
        mRtcEngine?.setupRemoteVideo(VideoCanvas(surfaceViews,VideoCanvas.RENDER_MODE_FIT,uid))
        surfaceViews.tag=uid
    }
   fun onRemoteUserLeft(){
        binding.remoteVideoViewContainer.removeAllViews()
    }



}