package com.example.gameprojectdemov1

import android.annotation.SuppressLint
import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.Bitmap

import org.tensorflow.lite.Interpreter

import java.io.BufferedReader
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.util.ArrayList
import java.util.Comparator
import java.util.PriorityQueue
import kotlin.experimental.and


class TensorFlowImageClassifier private constructor() : Classifier {

    private var interpreter: Interpreter? = null
    private var inputSize: Int = 0
    private var labelList: List<String>? = null
    private var quant: Boolean = false

    override fun recognizeImage(bitmap: Bitmap): List<Classifier.Recognition> {
        val byteBuffer = convertBitmapToByteBuffer(bitmap)
        if (quant) {
            val result = Array(1) { ByteArray(labelList!!.size) }
            interpreter!!.run(byteBuffer, result)
            return getSortedResultByte(result)
        } else {
            val result = Array(1) { FloatArray(labelList!!.size) }
            interpreter!!.run(byteBuffer, result)
            return getSortedResultFloat(result)
        }

    }

    override fun close() {
        interpreter!!.close()
        interpreter = null
    }

    @Throws(IOException::class)
    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    @Throws(IOException::class)
    private fun loadLabelList(assetManager: AssetManager, labelPath: String): List<String> {
        val labelList = ArrayList<String>()
        val reader = BufferedReader(InputStreamReader(assetManager.open(labelPath)))
        //var line: String
        for(line in reader.lines()) {
            labelList.add(line)
        }
        reader.close()
        return labelList
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer: ByteBuffer

        if (quant) {
            byteBuffer = ByteBuffer.allocateDirect(BATCH_SIZE * inputSize * inputSize * PIXEL_SIZE)
        } else {
            byteBuffer =
                ByteBuffer.allocateDirect(4 * BATCH_SIZE * inputSize * inputSize * PIXEL_SIZE / 3)
        }

        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputSize * inputSize)
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until inputSize) {
            for (j in 0 until inputSize) {
                val `val` = intValues[pixel++]
                if (quant) {
                    byteBuffer.put((`val` and 0xFF).toByte())
                } else {
                    byteBuffer.putFloat(((`val` and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                }

            }
        }
        return byteBuffer
    }

    @SuppressLint("DefaultLocale")
    private fun getSortedResultByte(labelProbArray: Array<ByteArray>): List<Classifier.Recognition> {

        val pq = PriorityQueue(
            MAX_RESULTS,
            Comparator<Classifier.Recognition> { lhs, rhs ->
                java.lang.Float.compare(
                    rhs.confidence!!,
                    lhs.confidence!!
                )
            })

        for (i in labelList!!.indices) {
            val confidence = (labelProbArray[0][i] and 0xff.toByte()) / 255.0f
            if (confidence > THRESHOLD) {
                pq.add(
                    Classifier.Recognition(
                        "" + i,
                        if (labelList!!.size > i) labelList!![i] else "unknown",
                        confidence, quant
                    )
                )
            }
        }

        val recognitions = ArrayList<Classifier.Recognition>()
        val recognitionsSize = Math.min(pq.size, MAX_RESULTS)
        for (i in 0 until recognitionsSize) {
            recognitions.add(pq.poll())
        }

        return recognitions
    }

    @SuppressLint("DefaultLocale")
    private fun getSortedResultFloat(labelProbArray: Array<FloatArray>): List<Classifier.Recognition> {

        val pq = PriorityQueue(
            MAX_RESULTS,
            Comparator<Classifier.Recognition> { lhs, rhs ->
                java.lang.Float.compare(
                    rhs.confidence!!,
                    lhs.confidence!!
                )
            })

        for (i in labelList!!.indices) {
            val confidence = labelProbArray[0][i]
            if (confidence > THRESHOLD) {
                pq.add(
                    Classifier.Recognition(
                        "" + i,
                        if (labelList!!.size > i) labelList!![i] else "unknown",
                        confidence, quant
                    )
                )
            }
        }

        val recognitions = ArrayList<Classifier.Recognition>()
        val recognitionsSize = Math.min(pq.size, MAX_RESULTS)
        for (i in 0 until recognitionsSize) {
            recognitions.add(pq.poll())
        }

        return recognitions
    }

    companion object {

        private val MAX_RESULTS = 3
        private val BATCH_SIZE = 1
        private val PIXEL_SIZE = 3
        private val THRESHOLD = 0.1f

        private val IMAGE_MEAN = 128
        private val IMAGE_STD = 128.0f

        @Throws(IOException::class)
        internal fun create(
            assetManager: AssetManager,
            modelPath: String,
            labelPath: String,
            inputSize: Int,
            quant: Boolean
        ): Classifier {

            val classifier = TensorFlowImageClassifier()
            classifier.interpreter = Interpreter(
                classifier.loadModelFile(assetManager, modelPath),
                Interpreter.Options()
            )
            classifier.labelList = classifier.loadLabelList(assetManager, labelPath)
            classifier.inputSize = inputSize
            classifier.quant = quant

            return classifier
        }
    }

}
