package com.example.gameprojectdemov1

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder

class StartActivityMusic:Service(){

    private lateinit var player:MediaPlayer
    override fun onBind(intent: Intent?): IBinder? {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return null
    }


    override fun onCreate() {
        super.onCreate()
        player = MediaPlayer.create(this,R.raw.start_activity)
        player.isLooping = true
        player.setVolume(100f,100f)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        player.start()
        return super.onStartCommand(intent, flags, startId)
//        return 1
    }

    override fun onDestroy() {
//        super.onDestroy()
        player.stop()
        player.release()
    }



}